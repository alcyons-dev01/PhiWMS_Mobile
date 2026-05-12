package fr.alcyons.phiwms_mobile.RetourPUI.Fragment

import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Classes.Retour
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.RetourPUI.DetailRetourPUIActivity
import fr.alcyons.phiwms_mobile.RetourPUI.RetourPUIQuantiteHelper
import java.util.Calendar
import java.util.Objects
import kotlin.math.max

class DetailFragment : Fragment()
{
    var onFermer: (() -> Unit)? = null
    var onValider: (() -> Unit)? = null

    private lateinit var db: SQLiteDatabase
    private lateinit var produit: Produit
    private lateinit var retourCourant: Retour
    private lateinit var retourLigneInitiale: Retour_Ligne
    private lateinit var retourLigneCourante: Retour_Ligne
    private lateinit var quantiteCompteeET: EditText
    private lateinit var restantTV: TextView
    private lateinit var emplacementView: AutoCompleteTextView
    private lateinit var chevronEmplacement: ImageView

    private var maxQuantite = 0
    private var ligneCouranteExisteEnBDD = true
    private var emplacementsDisponibles: List<Depot_Emplacement> = emptyList()

    companion object
    {
        private const val ARG_LIGNE = "ligne"
        private const val ARG_RETOUR_LIGNE_SELECTIONNEE_UID = "retour_ligne_selectionnee_uid"

        fun newInstance(ligne: Retour_Ligne, retourLigneSelectionneeUid: Int? = null): DetailFragment = DetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_LIGNE, ligne)
                if (retourLigneSelectionneeUid != null) { putInt(ARG_RETOUR_LIGNE_SELECTIONNEE_UID, retourLigneSelectionneeUid) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { return inflater.inflate(R.layout.fragment_detail_ligne_retour_pui, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        db = (requireActivity() as DetailRetourPUIActivity).db

        retourLigneInitiale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { arguments?.getSerializable(ARG_LIGNE, Retour_Ligne::class.java) }
        else { arguments?.getSerializable(ARG_LIGNE) as? Retour_Ligne } ?: return

        retourCourant = RetourOpenHelper.getRetourByID(db, retourLigneInitiale.retour_UID)
        produit = ProduitOpenHelper.getProduitByID(db, retourLigneInitiale.code_produit)
        retourLigneCourante = Retour_Ligne(retourLigneInitiale)
        val retourLigneSelectionneeUid = arguments?.getInt(ARG_RETOUR_LIGNE_SELECTIONNEE_UID)

        emplacementView = view.findViewById(R.id.emplacementLot_TV)
        chevronEmplacement = view.findViewById(R.id.chevronEmplacement)
        quantiteCompteeET = view.findViewById(R.id.quantiteComptee_ET)
        restantTV = view.findViewById(R.id.restantARetourner_TV)
        val numeroLotET = view.findViewById<EditText>(R.id.numeroLot_ET)
        val spinnerMois = view.findViewById<Spinner>(R.id.selecteurDateMois_SP)
        val spinnerAnnee = view.findViewById<Spinner>(R.id.selecteurDateAnnee_SP)
        val layoutDatePeremption = view.findViewById<CardView>(R.id.layoutDatePeremption_CV)

        view.findViewById<TextView>(R.id.designationReference_TV).text = retourLigneInitiale.produit_Designation
        view.findViewById<CardView>(R.id.layoutCarton_CV).visibility = View.GONE
        view.findViewById<LinearLayout>(R.id.bandeauQteRestante_LL).visibility = View.VISIBLE

        this.configureEmplacements()

        numeroLotET.setText(getDisplayedLot())
        numeroLotET.isFocusable = false
        numeroLotET.isFocusableInTouchMode = false
        numeroLotET.isClickable = false

        configureDatePeremption(spinnerMois, spinnerAnnee, layoutDatePeremption)
        initializeSelection(retourLigneSelectionneeUid)

        view.findViewById<ImageView>(R.id.layoutPlus_LL).setOnClickListener {
            val quantite = (this.quantiteCompteeET.text.toString().toIntOrNull() ?: 0) + getPasQuantite()
            this.quantiteCompteeET.setText(quantite.coerceAtMost(this.maxQuantite).toString())
        }
        view.findViewById<ImageView>(R.id.layoutMoins_LL).setOnClickListener {
            val quantite = (quantiteCompteeET.text.toString().toIntOrNull() ?: 0) - getPasQuantite()
            this.quantiteCompteeET.setText(quantite.coerceAtLeast(0).toString())
        }

        view.findViewById<LinearLayout>(R.id.layoutFermer_LL).setOnClickListener { this.onFermer?.invoke() }

        view.findViewById<LinearLayout>(R.id.layoutValider_LL).setOnClickListener { this.demanderConfirmationSiSuppressionNecessaire() }
    }

    private fun initializeSelection(retourLigneSelectionneeUid: Int?)
    {
        val retourLigneSelectionnee = retourLigneSelectionneeUid?.takeIf { it < 0 }?.let { Retour_LigneOpenHelper.getRetourLigneByID(this.db, it) }

        if (retourLigneSelectionnee != null)
        {
            this.retourLigneCourante = retourLigneSelectionnee
            this.ligneCouranteExisteEnBDD = true
            applySelection(retourLigneSelectionnee.retourPUI_Emplacement?.trim().orEmpty(), true)
            return
        }

        applySelection(this.retourLigneInitiale.retourPUI_Emplacement?.trim().orEmpty(), true)
    }

    private fun configureEmplacements()
    {
        emplacementsDisponibles = getEmplacementsZonePui()
        val valeurs = emplacementsDisponibles.map { it.adressage }.distinct().toMutableList()
        val emplacementCourant = retourLigneInitiale.retourPUI_Emplacement?.trim().orEmpty()

        if (emplacementCourant.isNotEmpty() && emplacementCourant !in valeurs) { valeurs.add(0, emplacementCourant) }

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_depot, valeurs)
        this.emplacementView.setAdapter(adapter)
        this.chevronEmplacement.setOnClickListener { this.emplacementView.showDropDown() }
        this.emplacementView.setOnClickListener { this.emplacementView.showDropDown() }
        this.emplacementView.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) { this.emplacementView.showDropDown() } }
        this.emplacementView.setOnItemClickListener { _, _, position, _ ->
            val emplacement = adapter.getItem(position)?.trim().orEmpty()
            applySelection(emplacement, false)
        }
    }

    private fun configureDatePeremption(spinnerMois: Spinner, spinnerAnnee: Spinner, layoutDatePeremption: CardView)
    {
        val adapterMois = ArrayAdapter(requireContext(), R.layout.spinner_date_item, getListeMoisDatePicker())
        adapterMois.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMois.adapter = adapterMois
        spinnerMois.isEnabled = false

        val adapterAnnee = ArrayAdapter(requireContext(), R.layout.spinner_date_item, getListeAnneeDatePicker())
        adapterAnnee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAnnee.adapter = adapterAnnee
        spinnerAnnee.isEnabled = false

        val peremptionDate = retourLigneInitiale.peremptionDate?.trim().orEmpty()
        if (peremptionDate.isNotEmpty() && peremptionDate != "0000-00-00")
        {
            val parts = peremptionDate.split("-")
            if (parts.size == 3)
            {
                val annee = parts[0]
                val mois = (parts[1].toIntOrNull() ?: 1) - 1
                spinnerMois.setSelection(mois.coerceAtLeast(0))
                val positionAnnee = (0 until adapterAnnee.count).indexOfFirst { adapterAnnee.getItem(it) == annee }
                if (positionAnnee >= 0) { spinnerAnnee.setSelection(positionAnnee) }
            }
        }
        else { layoutDatePeremption.visibility = View.INVISIBLE }
    }

    private fun applySelection(emplacement: String, initialize: Boolean)
    {
        val quantiteSaisieAvantChangement = this.quantiteCompteeET.text.toString().toIntOrNull() ?: 0
        val emplacementSelectionne = emplacement.ifEmpty { this.retourLigneInitiale.retourPUI_Emplacement?.trim().orEmpty().ifEmpty { produit.emplacement_PUI_Defaut } }

        val ligneExistante = getRetourLigneByEmplacement(emplacementSelectionne)
        if (ligneExistante != null)
        {
            this.retourLigneCourante = ligneExistante
            this.ligneCouranteExisteEnBDD = true
        }
        else
        {
            this.retourLigneCourante = createTemporaryRetourLigne(emplacementSelectionne)
            this.ligneCouranteExisteEnBDD = false
        }

        this.maxQuantite = getQuantiteRestantePourLigne(retourLigneCourante)
        val quantiteAffichee = if (this.retourLigneCourante.qte_Retourner > 0) { this.retourLigneCourante.qte_Retourner.toInt().coerceAtMost(this.maxQuantite) }
        else if (!initialize && quantiteSaisieAvantChangement > 0) { quantiteSaisieAvantChangement.coerceAtMost(this.maxQuantite) }
        else { this.maxQuantite }

        this.restantTV.text = maxQuantite.toString()
        this.quantiteCompteeET.setText(quantiteAffichee.toString())
        if (!initialize) { this.emplacementView.setText(emplacementSelectionne, false) }
        else { this.emplacementView.setText(emplacementSelectionne, false) }
    }

    private fun enregistrerLigne()
    {
        val quantite = this.quantiteCompteeET.text.toString().toIntOrNull()
        if (quantite == null)
        {
            Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "Veuillez saisir une quantité valide", false, false)
            return
        }

        if (quantite > this.maxQuantite)
        {
            Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "La quantité ne peut pas dépasser $maxQuantite", false, false)
            return
        }

        val emplacementSelectionne = this.emplacementView.text.toString().trim()
        if (emplacementSelectionne.isEmpty())
        {
            Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "Veuillez sélectionner un emplacement", false, false)
            return
        }

        this.retourLigneCourante.qte_Retourner = quantite.toDouble()
        if (quantite == 0)
        {
            this.retourLigneCourante.retourPUI_Zone = ""
            this.retourLigneCourante.retourPUI_Emplacement = ""
        }
        else
        {
            this.retourLigneCourante.retourPUI_Zone = getZoneNameForEmplacement(emplacementSelectionne)
            this.retourLigneCourante.retourPUI_Emplacement = emplacementSelectionne
        }

        if (this.ligneCouranteExisteEnBDD) { Retour_LigneOpenHelper.mettreAJourUnRetourLigne(this.db, this.retourLigneCourante) }
        else if (quantite > 0) { Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(this.db, this.retourLigneCourante) }

        this.onValider?.invoke()
    }

    private fun demanderConfirmationSiSuppressionNecessaire()
    {
        val quantite = this.quantiteCompteeET.text.toString().toIntOrNull() ?: 0
        val estSuppressionLigneRetournee = this.ligneCouranteExisteEnBDD && this.retourLigneCourante._UID < 0 && this.retourLigneCourante.qte_Retourner > 0 && quantite == 0

        if (!estSuppressionLigneRetournee)
        {
            this.enregistrerLigne()
            return
        }

        afficherConfirmationRemiseAZero()
    }

    private fun afficherConfirmationRemiseAZero()
    {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val layout = layoutInflater.inflate(R.layout.alerte_confirmation, null)
        val buttonOk = layout.findViewById<LinearLayout>(R.id.buttonOk)
        val buttonAnnuler = layout.findViewById<LinearLayout>(R.id.buttonAnnuler)
        val messageTextView = layout.findViewById<TextView>(R.id.messageFin)

        messageTextView.text = "La quantité retournée va être remise à 0. Voulez-vous continuer ?"
        builder.setView(layout)

        val alertDialog = builder.create()
        Objects.requireNonNull(alertDialog.window)?.setGravity(Gravity.CENTER)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        buttonOk.setOnClickListener {
            alertDialog.dismiss()
            this.enregistrerLigne()
        }

        buttonAnnuler.setOnClickListener { alertDialog.dismiss() }
    }

    private fun getRetourLigneByEmplacement(emplacement: String): Retour_Ligne? { return RetourPUIQuantiteHelper.getNegativeLinesForBase(this.db, this.retourCourant, this.retourLigneInitiale).firstOrNull { it.retourPUI_Emplacement == emplacement } }

    private fun getQuantiteRestantePourLigne(retourLigne: Retour_Ligne): Int
    {
        var quantiteRestante = this.retourLigneInitiale.qte_avant_retour.toInt()
        val retourLignesNegatives = RetourPUIQuantiteHelper.getNegativeLinesForBase(this.db, this.retourCourant, this.retourLigneInitiale)

        for (ligne in retourLignesNegatives) { if (ligne._UID != retourLigne._UID) { quantiteRestante -= ligne.qte_Retourner.toInt() } }

        return quantiteRestante.coerceAtLeast(0)
    }

    private fun createTemporaryRetourLigne(emplacement: String): Retour_Ligne
    {
        val ligne = Retour_Ligne(this.retourLigneInitiale)
        ligne._UID = RetourPUIQuantiteHelper.generateNegativeUid()
        ligne.retourPUI_Zone = getZoneNameForEmplacement(emplacement)
        ligne.retourPUI_Emplacement = emplacement
        ligne.emplacementOrigine = RetourPUIQuantiteHelper.buildBaseOrigin(this.retourLigneInitiale._UID)
        ligne.qte_Retourner = 0.0
        return ligne
    }

    private fun getZoneNameForEmplacement(emplacement: String): String
    {
        val depotEmplacement = this.emplacementsDisponibles.firstOrNull { it.adressage == emplacement }
        return if (depotEmplacement != null) { ZoneOpenHelper.getUneZoneByID(this.db, depotEmplacement.zoneID)?.zoneName ?: this.produit.zone_PUI_Defaut } else { this.produit.zone_PUI_Defaut }
    }

    private fun getEmplacementsZonePui(): List<Depot_Emplacement>
    {
        val depot = DepotOpenHelper.getDepotParReference(this.db, this.retourCourant.ref_Depot_Dest)
        val zoneName = this.produit.zone_PUI_Defaut?.trim().orEmpty()
        if (zoneName.isEmpty()) { return emptyList() }

        val zone = ZoneOpenHelper.getZoneByDepotEtNom(this.db, depot, zoneName) ?: return emptyList()
        return EmplacementOpenHelper.getEmplacementsParZoneID(this.db, zone.zoneID)
    }

    private fun getDisplayedLot(): String { return RetourPUIQuantiteHelper.getDisplayedLot(this.retourLigneInitiale) }

    private fun getPasQuantite(): Int
    {
        var pasNumberPicker = this.produit.cond_distrib.toInt()
        if (pasNumberPicker == 0 || pasNumberPicker >= this.maxQuantite) { pasNumberPicker = 1 }
        return max(1, pasNumberPicker)
    }

    private fun getListeMoisDatePicker(): Array<String> { return Array(12) { index -> String.format("%02d", index + 1) } }

    private fun getListeAnneeDatePicker(): Array<String>
    {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = currentYear - 2
        val endYear = currentYear + 10
        return Array((endYear - startYear) + 1) { index -> (startYear + index).toString() }
    }
}
