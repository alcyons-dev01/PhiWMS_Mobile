package fr.alcyons.phiwms_mobile.Reception.Fragment

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
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
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.Reception.DetailReception_V2
import java.util.Calendar
import java.util.Random

class DetailFragment : Fragment() {

    var onFermer: (() -> Unit)? = null
    var onValider: ((ligne: PH_Reliquat?, ajout:Boolean) -> Unit)? = null

    private lateinit var db: SQLiteDatabase // ton type de BDD

    lateinit var produit : Produit

    companion object {
        private const val ARG_LIGNE = "ligne"

        fun newInstance(reliquatBase: PH_Reliquat?, produit : Produit) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LIGNE, reliquatBase)
                }
                this.produit = produit
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_detail_ligne, container, false)

    fun mettreAJourLigne(ligne: PH_Reliquat) {
        val view = view ?: return
        view.findViewById<AutoCompleteTextView>(R.id.emplacementLot_TV).setText(ligne.emplacement)
        view.findViewById<TextView>(R.id.designationReference_TV).text = ligne.designationCourte
        view.findViewById<EditText>(R.id.quantiteComptee_ET)
            .setText(ligne.qteLivraison.toInt().toString())
        view.findViewById<EditText>(R.id.numeroLot_ET)
            .setText(ligne.lot ?: "")
        // Mettez à jour les autres champs si nécessaire
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = (requireActivity() as DetailReception_V2).db

        val reliquatBase = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_LIGNE, PH_Reliquat::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable(ARG_LIGNE) as? PH_Reliquat
        } ?: return

        val produit = ProduitOpenHelper.getProduitByID(db, reliquatBase.produitID)
        var conditionnement: Int = reliquatBase.conditionnementAchat
        val layoutCarton_CV = view.findViewById<CardView>(R.id.layoutCarton_CV)
        val layoutMoinsLL = view.findViewById<ImageView>(R.id.layoutMoins_LL)
        val layoutPlusLL = view.findViewById<ImageView>(R.id.layoutPlus_LL)
        val spinnerMoisDatePeremptionSP = view.findViewById<Spinner>(R.id.selecteurDateMois_SP)
        val adapterMoisPeremption: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_date_item,
            getListeMoisDatePicker()
        )
        val spinnerAnneeDatePeremptionSP = view.findViewById<Spinner>(R.id.selecteurDateAnnee_SP)
        val adapterAnneePeremption: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_date_item,
            getListeAnneeDatePicker()
        )
        val quantiteCompteeET = view.findViewById<EditText>(R.id.quantiteComptee_ET)

        var emplacementCourant = reliquatBase.emplacement
        if(emplacementCourant == "null")
            emplacementCourant = "RECEPTION"
        val emplacementAutoComplete = view.findViewById<AutoCompleteTextView>(R.id.emplacementLot_TV)
        val chevronEmplacement = view.findViewById<ImageView>(R.id.chevronEmplacement)

        val depotCourant = DepotOpenHelper.getDepotParReference(db, reliquatBase.depotReference)
        var listeEmplacements : MutableList<String> = mutableListOf()
        val zoneCourante = ZoneOpenHelper.getZoneByDepotEtNom(db, depotCourant, produit.zone_PUI_Defaut)
        if(zoneCourante != null)
        {
            listeEmplacements = EmplacementOpenHelper.getNomEmplacementsParZone(db, zoneCourante)
            if(listeEmplacements.isEmpty())
                listeEmplacements.add("RECEPTION")
        }
        else
            listeEmplacements.add("RECEPTION")

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_depot, listeEmplacements)
        emplacementAutoComplete.setAdapter(adapter)
        emplacementAutoComplete.setThreshold(100)

        // Ouvre au clic
        emplacementAutoComplete.setOnClickListener { emplacementAutoComplete.showDropDown() }
        chevronEmplacement.setOnClickListener { emplacementAutoComplete.showDropDown() }

        // Hauteur dropdown
        val hauteurEcran = resources.displayMetrics.heightPixels
        emplacementAutoComplete.setDropDownHeight(hauteurEcran / 3)
        emplacementAutoComplete.setDropDownBackgroundResource(android.R.color.white)

        emplacementAutoComplete.post {
            val dpToPx = (12 * resources.displayMetrics.density).toInt()
            emplacementAutoComplete.setDropDownWidth(
                view.findViewById<View>(R.id.emplacementLot_TV).width - dpToPx
            )
        }
        emplacementAutoComplete.setText(emplacementCourant, false)

        emplacementAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val emplacementSelectionne = listeEmplacements[position]
            emplacementAutoComplete.setText(emplacementSelectionne, false)
            emplacementAutoComplete.dismissDropDown()
            emplacementCourant = emplacementSelectionne
        }

        //gestion des données
        if(emplacementCourant != "")
        {
            view.findViewById<AutoCompleteTextView>(R.id.emplacementLot_TV).setText(emplacementCourant)
        }
        else if(produit.emplacement_PUI_Defaut != "")
        {
            emplacementCourant = produit.emplacement_PUI_Defaut
            view.findViewById<AutoCompleteTextView>(R.id.emplacementLot_TV).setText(produit.emplacement_PUI_Defaut)
        }
        else
        {
            emplacementCourant = listeEmplacements[0]
            view.findViewById<AutoCompleteTextView>(R.id.emplacementLot_TV).setText(listeEmplacements[0])
        }

        view.findViewById<TextView>(R.id.designationReference_TV).text = reliquatBase.designationCourte
        var reliquatBaseReception = reliquatBase
        var maxAReceptionner = reliquatBase.qteReliquat_X
        if(reliquatBase.reliquat_UID < 0)
        {
            //on remet en place la quantité qui sera modifié après coup
            reliquatBaseReception = PH_ReliquatOpenHelper.getPH_ReliquatBaseByUnIdProduitetNumero(db, reliquatBase.produitID, reliquatBase.commandeNumero)

            reliquatBaseReception.qteReliquat_X += reliquatBase.qteLivraison
            maxAReceptionner = reliquatBaseReception.qteReliquat_X
            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquatBaseReception)

            view.findViewById<EditText>(R.id.numeroLot_ET).setText(reliquatBase.lot.toString())
            quantiteCompteeET.setText(reliquatBase.qteLivraison.toString())

            if(reliquatBase.serie != "")
            {
                maxAReceptionner = produit.cond_achat
                view.findViewById<EditText>(R.id.numeroLot_ET).apply {
                    isFocusable = false
                    isFocusableInTouchMode = false
                    isClickable = false
                    spinnerMoisDatePeremptionSP.isEnabled = false
                    spinnerAnneeDatePeremptionSP.isEnabled = false
                    view.findViewById<AutoCompleteTextView>(R.id.emplacementLot_TV).setText(produit.emplacement_PUI_Defaut)
                }
            }
        }
        else
        {
            maxAReceptionner = reliquatBaseReception.qteReliquat_X
            view.findViewById<EditText>(R.id.numeroLot_ET).setText("")
            quantiteCompteeET.setText("0")
        }

        //gestion du conditionnment
        layoutCarton_CV.visibility = View.GONE
        layoutPlusLL.setOnClickListener { _: View? ->
            var qteActuelle = quantiteCompteeET.text.toString().toInt()
            if (qteActuelle == -1)
                qteActuelle = 0

            qteActuelle += conditionnement

            if(qteActuelle > maxAReceptionner)
                qteActuelle = maxAReceptionner
            quantiteCompteeET.setText(qteActuelle.toString())
        }
        layoutMoinsLL.setOnClickListener { _: View? ->
            var qteActuelle = quantiteCompteeET.text.toString().toInt()
            qteActuelle -= conditionnement
            if (qteActuelle < 0) qteActuelle = 0
            quantiteCompteeET.setText(qteActuelle.toString())
        }

        //gestion bandeau restant à réceptionner
        view.findViewById<LinearLayout>(R.id.bandeauQteRestante_LL).visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.restantAReceptionner_TV).text = maxAReceptionner.toString()

        //gestion de la date de péremption
        adapterMoisPeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMoisDatePeremptionSP.adapter = adapterMoisPeremption
        adapterAnneePeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAnneeDatePeremptionSP.adapter = adapterAnneePeremption
        spinnerAnneeDatePeremptionSP.setSelection(3)

        if (!reliquatBase.peremptionDate.isNullOrEmpty() && reliquatBase.peremptionDate != "0000-00-00") {
            val parts = reliquatBase.peremptionDate.split("-")
            val annee = parts[0] // "2026"
            val mois = parts[1].toInt() - 1 // "04" → index 3 (0-based)

            // Règle le spinner mois (index 0-based donc avril = 3)
            spinnerMoisDatePeremptionSP.setSelection(mois)

            // Règle le spinner année en cherchant la position de l'année dans l'adapter
            val positionAnnee = (0 until adapterAnneePeremption.count).indexOfFirst {
                adapterAnneePeremption.getItem(it) == annee
            }
            if (positionAnnee != -1) {
                spinnerAnneeDatePeremptionSP.setSelection(positionAnnee)
            }
        } else {
            view.findViewById<CardView>(R.id.layoutDatePeremption_CV).visibility = View.INVISIBLE
        }


        //gestion du suivi de lot et de péremption
        if (!produit.isSuivi_Lot && !produit.isPeremption) {
            view.findViewById<LinearLayout>(R.id.layoutLotPeremption_LL).visibility = View.GONE
        } else {
            if (!produit.isSuivi_Lot) {
                view.findViewById<EditText>(R.id.numeroLot_ET).isFocusable = false
                if (!produit.isPeremption)
                    view.findViewById<CardView>(R.id.layoutDatePeremption_CV).visibility =
                        View.INVISIBLE
            } else
                view.findViewById<CardView>(R.id.layoutDatePeremption_CV).visibility = View.VISIBLE
        }

        //gestion de la fermeture
        view.findViewById<LinearLayout>(R.id.layoutFermer_LL)
            .setOnClickListener {
                if(reliquatBase.reliquat_UID < 0)
                {
                    //on remet en place la quantité qui sera modifié après coup
                    var reliquatBaseReception = PH_ReliquatOpenHelper.getPH_ReliquatBaseByUnIdProduitetNumero(db, reliquatBase.produitID, reliquatBase.commandeNumero)

                    reliquatBaseReception.qteReliquat_X -= reliquatBase.qteLivraison
                    PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquatBaseReception)
                }

                onFermer?.invoke()
            }

        //gestion du bouton valider
        view.findViewById<LinearLayout>(R.id.layoutValider_LL)
            .setOnClickListener {
                val quantite = quantiteCompteeET.text.toString().toInt()

                if(quantite == 0 && reliquatBase.reliquat_UID > 0)
                {
                    Alerte.afficherAlerteInformation(
                        context,
                        LayoutInflater.from(context),
                        "Erreur",
                        "Veuillez saisir une quantité supérieur à 0",
                        false,
                        false
                    )
                }
                else if(quantite == 0 && reliquatBase.reliquat_UID < 0)
                {
                    demandeConfirmation(LayoutInflater.from(context)) {
                        if (it) {
                            PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquatBase)
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                                db,
                                PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT,
                                reliquatBase.getPhiMR4UUID(),
                                reliquatBase.getReliquat_UID(),
                                DBOpenHelper.ActionsEAS.SUPPR
                            )

                            onValider?.invoke(null, true)
                        }
                    }
                }
                else
                {
                    var lot = view.findViewById<EditText>(R.id.numeroLot_ET).text.toString().trim()

                    if (produit.isSuivi_Lot && lot.isEmpty()) {
                        Alerte.afficherAlerteInformation(
                            context,
                            LayoutInflater.from(context),
                            "Erreur",
                            "Veuillez saisir un numéro de lot",
                            false,
                            false
                        )
                    } else {
                        reliquatBaseReception.qteReliquat_X-=quantite
                        PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquatBaseReception)

                        if (!produit.isSuivi_Lot)
                            lot = "LOT NON TRACÉ"

                        val moisIndex =
                            spinnerMoisDatePeremptionSP.selectedItemPosition + 1 // 1-based
                        val annee = spinnerAnneeDatePeremptionSP.selectedItem.toString()
                        // Dernier jour du mois sélectionné
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.YEAR, annee.toInt())
                        calendar.set(Calendar.MONTH, moisIndex - 1) // Calendar est 0-based
                        calendar.set(
                            Calendar.DAY_OF_MONTH,
                            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        )
                        val moisFormate = String.format("%02d", moisIndex)
                        val jour = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        var datePeremption = "$annee-$moisFormate-$jour"

                        if (!produit.isPeremption)
                            datePeremption = "0000-00-00"

                        val reliquatliste =
                            PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(
                                db,
                                reliquatBase.commandeNumero,
                                reliquatBase.produitID
                            )
                        var phReliquatCourant: PH_Reliquat = reliquatBase

                        var existe = false

                        for (reliquatcourant in reliquatliste) {
                            if (reliquatcourant.lot.trim { it <= ' ' }.contentEquals(
                                    lot
                                        .trim { it <= ' ' }) && reliquatcourant.peremptionDate
                                    .trim { it <= ' ' }.contentEquals(datePeremption) && reliquatcourant.serie == ""
                            ) {
                                phReliquatCourant = reliquatcourant
                                existe = true
                            }
                        }

                        if (existe) {
                            if(reliquatBase.reliquat_UID < 0)
                                phReliquatCourant.qteLivraison = quantite
                            else
                                phReliquatCourant.qteLivraison += quantite
                            onValider?.invoke(phReliquatCourant, false)
                        } else {
                            val randomreliquat = Random()
                            var reliquatId = randomreliquat.nextInt()
                            if (reliquatId > 0) reliquatId *= -1

                            phReliquatCourant.reliquat_UID = reliquatId
                            val numeroLot = lot

                            phReliquatCourant.lot = numeroLot.trim { it <= ' ' }
                            phReliquatCourant.peremptionDate = datePeremption.trim { it <= ' ' }
                            phReliquatCourant.qteLivraison = quantite
                            phReliquatCourant.scanValue = ""
                            phReliquatCourant.bL_Numero = ""
                            phReliquatCourant.emplacement = emplacementCourant

                            onValider?.invoke(phReliquatCourant, true)
                        }
                    }
                }
            }

    }

    fun getListeMoisDatePicker(): Array<String?> {
        val tableauMois = arrayOfNulls<String>(12)

        for (i in 0..11) {
            tableauMois[i] = if (i < 9) ("0" + (i + 1)) else (i + 1).toString()
        }

        return tableauMois
    }

    fun getListeAnneeDatePicker(): Array<String?> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = currentYear - 2
        val endYear = currentYear + 10

        val size = (endYear - startYear) + 1
        val tableauAnnee = arrayOfNulls<String>(size)

        for (i in 0..<size) {
            tableauAnnee[i] = (startYear + i).toString()
        }

        return tableauAnnee
    }

    fun demandeConfirmation(inflater: LayoutInflater, onResultat: (Boolean) -> Unit) {
        val builder = context?.let { AlertDialog.Builder(it) }
        val layout = inflater.inflate(R.layout.alerte_confirmation, null)

        val zoneok = layout.findViewById<LinearLayout>(R.id.buttonOk)
        val buttonAnnuler = layout.findViewById<LinearLayout>(R.id.buttonAnnuler)
        val messageTextView = layout.findViewById<TextView>(R.id.messageFin)

        messageTextView.text = "Souhaitez-vous supprimer le lot ?"
        layout.findViewById<TextView>(R.id.TitreAnnulation).text = "Annuler"
        layout.findViewById<TextView>(R.id.TitreConfirmation).text = "Confirmer"

        builder?.setView(layout)

        val alertDialog = builder?.create()
        alertDialog?.window?.setGravity(Gravity.CENTER)
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog?.show()

        zoneok.setOnClickListener {
            alertDialog?.dismiss()
            onResultat(true)
        }

        buttonAnnuler.setOnClickListener {
            alertDialog?.dismiss()
            onResultat(false)
        }
    }
}