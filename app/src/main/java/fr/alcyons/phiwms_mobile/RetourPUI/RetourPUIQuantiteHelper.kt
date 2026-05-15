package fr.alcyons.phiwms_mobile.RetourPUI

import android.database.sqlite.SQLiteDatabase
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Retour
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import java.util.Random
import kotlin.math.min

object RetourPUIQuantiteHelper
{
    private const val BASE_UID_PREFIX = "BASE_UID:"

    @JvmStatic fun buildBaseOrigin(baseUid: Int): String = BASE_UID_PREFIX + baseUid

    @JvmStatic fun parseBaseUid(retourLigne: Retour_Ligne?): Int? = parseBaseUid(retourLigne?.emplacementOrigine)

    @JvmStatic fun parseBaseUid(emplacementOrigine: String?): Int?
    {
        val valeur = emplacementOrigine?.trim().orEmpty()
        if (valeur.isEmpty()) { return null }
        if (valeur.startsWith(BASE_UID_PREFIX)) { return valeur.removePrefix(BASE_UID_PREFIX).toIntOrNull() }
        return valeur.toIntOrNull()
    }

    @JvmStatic fun getDisplayedLot(retourLigne: Retour_Ligne): String
    {
        val lotRetourne = retourLigne.lot_Retourner?.trim().orEmpty()
        if (lotRetourne.isNotEmpty()) { return lotRetourne }
        return retourLigne.lot?.trim().orEmpty()
    }

    @JvmStatic fun getNegativeLinesForBase(allNegativeLines: List<Retour_Ligne>, baseLine: Retour_Ligne): List<Retour_Ligne> { return allNegativeLines.filter { parseBaseUid(it) == baseLine._UID } }

    @JvmStatic fun getNegativeLinesForBase(db: SQLiteDatabase, retour: Retour, baseLine: Retour_Ligne): List<Retour_Ligne> { return getNegativeLinesForBase(Retour_LigneOpenHelper.getAllRetourLignesNegByRetour(db, retour), baseLine) }

    @JvmStatic fun getAllocatedQuantityForBase(allNegativeLines: List<Retour_Ligne>, baseLine: Retour_Ligne): Int { return getNegativeLinesForBase(allNegativeLines, baseLine).sumOf { it.qte_Retourner.toInt() } }

    @JvmStatic fun normalizeNegativeLineOrigins(db: SQLiteDatabase, retour: Retour?, baseLines: List<Retour_Ligne>)
    {
        if (retour == null || baseLines.isEmpty()) { return }

        val negativeLines = Retour_LigneOpenHelper.getAllRetourLignesNegByRetour(db, retour).sortedBy { it._UID }
        val allocatedByBaseUid = mutableMapOf<Int, Int>()
        baseLines.forEach { baseLine -> allocatedByBaseUid[baseLine._UID] = 0 }

        negativeLines.forEach { negativeLine ->
            val baseUid = parseBaseUid(negativeLine)
            if (baseUid != null && allocatedByBaseUid.containsKey(baseUid)) { allocatedByBaseUid[baseUid] = (allocatedByBaseUid[baseUid] ?: 0) + negativeLine.qte_Retourner.toInt() }
        }

        negativeLines.filter { parseBaseUid(it) == null }.forEach { negativeLine -> assignLegacyNegativeLine(db, negativeLine, baseLines, allocatedByBaseUid) }
    }

    private fun assignLegacyNegativeLine(db: SQLiteDatabase, negativeLine: Retour_Ligne, baseLines: List<Retour_Ligne>, allocatedByBaseUid: MutableMap<Int, Int>)
    {
        val candidateBaseLines = getCandidateBaseLines(baseLines, negativeLine)
        if (candidateBaseLines.isEmpty()) { return }

        var quantiteRestante = negativeLine.qte_Retourner.toInt()
        val allocations = mutableListOf<Pair<Retour_Ligne, Int>>()

        for (baseLine in candidateBaseLines)
        {
            val quantiteDejaAllouee = allocatedByBaseUid[baseLine._UID] ?: 0
            val capaciteRestante = baseLine.qte_avant_retour.toInt() - quantiteDejaAllouee
            if (capaciteRestante <= 0) { continue }

            val quantiteAAllouer = min(quantiteRestante, capaciteRestante)
            if (quantiteAAllouer <= 0) { continue }

            allocations.add(baseLine to quantiteAAllouer)
            allocatedByBaseUid[baseLine._UID] = quantiteDejaAllouee + quantiteAAllouer
            quantiteRestante -= quantiteAAllouer

            if (quantiteRestante == 0) { break }
        }

        if (allocations.isEmpty())
        {
            val fallbackBaseLine = candidateBaseLines.first()
            allocations.add(fallbackBaseLine to negativeLine.qte_Retourner.toInt())
            allocatedByBaseUid[fallbackBaseLine._UID] = (allocatedByBaseUid[fallbackBaseLine._UID] ?: 0) + negativeLine.qte_Retourner.toInt()
            quantiteRestante = 0
        }

        if (quantiteRestante > 0)
        {
            val (baseLine, quantiteAllouee) = allocations.first()
            allocations[0] = baseLine to (quantiteAllouee + quantiteRestante)
            allocatedByBaseUid[baseLine._UID] = (allocatedByBaseUid[baseLine._UID] ?: 0) + quantiteRestante
        }

        val (premiereBaseLine, premiereQuantite) = allocations.first()
        negativeLine.emplacementOrigine = buildBaseOrigin(premiereBaseLine._UID)
        negativeLine.qte_Retourner = premiereQuantite.toDouble()
        negativeLine.qte_avant_retour = premiereBaseLine.qte_avant_retour
        Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, negativeLine)

        allocations.drop(1).forEach { (baseLine, quantite) ->
            val nouvelleLigne = Retour_Ligne(negativeLine)
            nouvelleLigne._UID = generateNegativeUid()
            nouvelleLigne.emplacementOrigine = buildBaseOrigin(baseLine._UID)
            nouvelleLigne.qte_Retourner = quantite.toDouble()
            nouvelleLigne.qte_avant_retour = baseLine.qte_avant_retour
            Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, nouvelleLigne)
        }
    }

    private fun getCandidateBaseLines(baseLines: List<Retour_Ligne>, negativeLine: Retour_Ligne): List<Retour_Ligne>
    {
        val lot = getDisplayedLot(negativeLine)
        val serie = negativeLine.serie_Retourner?.trim().orEmpty()
        val peremption = negativeLine.peremptionDate?.trim().orEmpty()

        val exactMatches = baseLines.filter {
            it.code_produit == negativeLine.code_produit &&
            getDisplayedLot(it) == lot &&
            it.serie_Retourner?.trim().orEmpty() == serie &&
            it.peremptionDate?.trim().orEmpty() == peremption
        }.sortedBy { it._UID }
        if (exactMatches.isNotEmpty()) { return exactMatches }

        return baseLines.filter { it.code_produit == negativeLine.code_produit }.sortedBy { it._UID }
    }

    @JvmStatic fun generateNegativeUid(): Int
    {
        var retourLigneId = Random().nextInt()
        if (retourLigneId > 0) { retourLigneId *= -1 }
        if (retourLigneId == 0) { retourLigneId = -1 }
        return retourLigneId
    }
}
