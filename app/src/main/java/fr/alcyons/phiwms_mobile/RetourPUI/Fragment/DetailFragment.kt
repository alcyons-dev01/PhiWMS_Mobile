package fr.alcyons.phiwms_mobile.RetourPUI.Fragment

import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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

    private data class DatePeremptionViews(
        val spinnerMois: Spinner,
        val spinnerAnnee: Spinner,
        val layout: CardView
    )

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
    private var retourLigneSelectionneeUid: Int? = null

    companion object
    {
        private const val ARG_LIGNE = "ligne"
        private const val ARG_RETOUR_LIGNE_SELECTIONNEE_UID = "retour_ligne_selectionnee_uid"
        private const val EMPTY_DATE = "0000-00-00"
        private const val YEAR_RANGE_BEFORE = 2
        private const val YEAR_RANGE_AFTER = 10

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
        retourLigneSelectionneeUid = arguments?.getInt(ARG_RETOUR_LIGNE_SELECTIONNEE_UID)

        val numeroLotET = bindViews(view)
        val datePeremptionViews = bindDatePeremptionViews(view)

        setupStaticUi(view, numeroLotET)
        configureEmplacements()
        configureDatePeremption(datePeremptionViews)
        initializeSelection(retourLigneSelectionneeUid)
        configureQuantiteField()
        setupQuantiteButtons(view)
        setupActionButtons(view)
    }

    private fun bindViews(view: View): EditText
    {
        emplacementView = view.findViewById(R.id.emplacementLot_TV)
        chevronEmplacement = view.findViewById(R.id.chevronEmplacement)
        quantiteCompteeET = view.findViewById(R.id.quantiteComptee_ET)
        restantTV = view.findViewById(R.id.restantARetourner_TV)
        return view.findViewById(R.id.numeroLot_ET)
    }

    private fun bindDatePeremptionViews(view: View): DatePeremptionViews
    {
        return DatePeremptionViews(
            spinnerMois = view.findViewById(R.id.selecteurDateMois_SP),
            spinnerAnnee = view.findViewById(R.id.selecteurDateAnnee_SP),
            layout = view.findViewById(R.id.layoutDatePeremption_CV)
        )
    }

    private fun setupStaticUi(view: View, numeroLotET: EditText)
    {
        view.findViewById<TextView>(R.id.designationReference_TV).text = retourLigneInitiale.produit_Designation
        view.findViewById<CardView>(R.id.layoutCarton_CV).visibility = View.GONE
        view.findViewById<LinearLayout>(R.id.bandeauQteRestante_LL).visibility = View.VISIBLE

        numeroLotET.setText(getDisplayedLot())
        numeroLotET.isFocusable = false
        numeroLotET.isFocusableInTouchMode = false
        numeroLotET.isClickable = false
    }

    private fun setupQuantiteButtons(view: View)
    {
        view.findViewById<ImageView>(R.id.layoutPlus_LL).setOnClickListener {
            val quantite = (quantiteCompteeET.text.toString().toIntOrNull() ?: 0) + getPasQuantite()
            quantiteCompteeET.setText(quantite.coerceAtMost(maxQuantite).toString())
        }
        view.findViewById<ImageView>(R.id.layoutMoins_LL).setOnClickListener {
            val quantite = (quantiteCompteeET.text.toString().toIntOrNull() ?: 0) - getPasQuantite()
            quantiteCompteeET.setText(quantite.coerceAtLeast(0).toString())
        }
    }

    private fun setupActionButtons(view: View)
    {
        view.findViewById<LinearLayout>(R.id.layoutFermer_LL).setOnClickListener { onFermer?.invoke() }
        view.findViewById<LinearLayout>(R.id.layoutValider_LL).setOnClickListener { demanderConfirmationSiSuppressionNecessaire() }
    }

    private fun configureQuantiteField()
    {
        quantiteCompteeET.setSelectAllOnFocus(true)
        quantiteCompteeET.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) { selectAllQuantityText() } }
        quantiteCompteeET.setOnClickListener { selectAllQuantityText() }
        quantiteCompteeET.setOnEditorActionListener { _, actionId, event ->
            val isValidationAction = actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)

            if (!isValidationAction) { return@setOnEditorActionListener false }

            verifierQuantiteSaisie() != null
        }
    }

    private fun selectAllQuantityText() { quantiteCompteeET.post { quantiteCompteeET.selectAll() } }

    private fun initializeSelection(retourLigneSelectionneeUid: Int?)
    {
        val retourLigneSelectionnee = retourLigneSelectionneeUid?.takeIf { it < 0 }?.let { Retour_LigneOpenHelper.getRetourLigneByID(db, it) }

        if (retourLigneSelectionnee != null)
        {
            retourLigneCourante = retourLigneSelectionnee
            ligneCouranteExisteEnBDD = true
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
        emplacementView.setAdapter(adapter)
        chevronEmplacement.setOnClickListener { emplacementView.showDropDown() }
        emplacementView.setOnClickListener { emplacementView.showDropDown() }
        emplacementView.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) { emplacementView.showDropDown() } }
        emplacementView.setOnItemClickListener { _, _, position, _ ->
            val emplacement = adapter.getItem(position)?.trim().orEmpty()
            applySelection(emplacement, false)
        }
    }

    private fun configureDatePeremption(views: DatePeremptionViews)
    {
        val adapterMois = ArrayAdapter(requireContext(), R.layout.spinner_date_item, getListeMoisDatePicker())
        adapterMois.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.spinnerMois.adapter = adapterMois
        views.spinnerMois.isEnabled = false

        val adapterAnnee = ArrayAdapter(requireContext(), R.layout.spinner_date_item, getListeAnneeDatePicker())
        adapterAnnee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.spinnerAnnee.adapter = adapterAnnee
        views.spinnerAnnee.isEnabled = false

        val peremptionDate = retourLigneInitiale.peremptionDate?.trim().orEmpty()
        if (peremptionDate.isNotEmpty() && peremptionDate != EMPTY_DATE)
        {
            val parts = peremptionDate.split("-")
            if (parts.size == 3)
            {
                val annee = parts[0]
                val mois = (parts[1].toIntOrNull() ?: 1) - 1
                views.spinnerMois.setSelection(mois.coerceAtLeast(0))
                val positionAnnee = (0 until adapterAnnee.count).indexOfFirst { adapterAnnee.getItem(it) == annee }
                if (positionAnnee >= 0) { views.spinnerAnnee.setSelection(positionAnnee) }
            }
        }
        else { views.layout.visibility = View.INVISIBLE }
    }

    private fun applySelection(emplacement: String, initialize: Boolean)
    {
        val quantiteSaisieAvantChangement = quantiteCompteeET.text.toString().toIntOrNull() ?: 0
        val emplacementSelectionne = emplacement.ifEmpty { this.retourLigneInitiale.retourPUI_Emplacement?.trim().orEmpty().ifEmpty { produit.emplacement_PUI_Defaut } }

        if (isEditingReturnedLine()) { ligneCouranteExisteEnBDD = true }
        else
        {
            val ligneExistante = getRetourLigneByEmplacement(emplacementSelectionne)
            if (ligneExistante != null)
            {
                retourLigneCourante = ligneExistante
                ligneCouranteExisteEnBDD = true
            }
            else
            {
                retourLigneCourante = createTemporaryRetourLigne(emplacementSelectionne)
                ligneCouranteExisteEnBDD = false
            }
        }

        maxQuantite = getQuantiteRestantePourLigne(retourLigneCourante)
        val quantiteAffichee = if (retourLigneCourante.qte_Retourner > 0) { retourLigneCourante.qte_Retourner.toInt().coerceAtMost(maxQuantite) }
        else if (!initialize && quantiteSaisieAvantChangement > 0) { quantiteSaisieAvantChangement.coerceAtMost(this.maxQuantite) }
        else { maxQuantite }

        restantTV.text = maxQuantite.toString()
        quantiteCompteeET.setText(quantiteAffichee.toString())
        emplacementView.setText(emplacementSelectionne, false)
    }

    private fun enregistrerLigne()
    {
        val quantite = verifierQuantiteSaisie() ?: return

        val emplacementSelectionne = emplacementView.text.toString().trim()
        if (emplacementSelectionne.isEmpty())
        {
            Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "Veuillez sélectionner un emplacement", false, false)
            return
        }

        if (isEditingReturnedLine())
        {
            enregistrerLigneDepuisRetourner(quantite, emplacementSelectionne)
            return
        }

        retourLigneCourante.qte_Retourner = quantite.toDouble()
        if (quantite == 0)
        {
            retourLigneCourante.retourPUI_Zone = ""
            retourLigneCourante.retourPUI_Emplacement = ""
        }
        else
        {
            retourLigneCourante.retourPUI_Zone = getZoneNameForEmplacement(emplacementSelectionne)
            retourLigneCourante.retourPUI_Emplacement = emplacementSelectionne
        }

        if (ligneCouranteExisteEnBDD) { Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigneCourante) }
        else if (quantite > 0) { Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, retourLigneCourante) }

        onValider?.invoke()
    }

    private fun verifierQuantiteSaisie(): Int?
    {
        val quantite = quantiteCompteeET.text.toString().toIntOrNull()
        if (quantite == null)
        {
            Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "Veuillez saisir une quantité valide", false, false)
            return null
        }

        if (quantite > maxQuantite)
        {
            Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "La quantité ne peut pas dépasser $maxQuantite", false, false)
            return null
        }

        return quantite
    }

    private fun enregistrerLigneDepuisRetourner(quantite: Int, emplacementSelectionne: String)
    {
        val ligneSelectionnee = Retour_LigneOpenHelper.getRetourLigneByID(db, retourLigneCourante._UID) ?: retourLigneCourante
        val ligneDestinationExistante = getRetourLigneByEmplacement(emplacementSelectionne, ligneSelectionnee._UID)

        if (quantite == 0)
        {
            Retour_LigneOpenHelper.supprimerUnRetourLigne(db, ligneSelectionnee)
            onValider?.invoke()
            return
        }

        if (ligneDestinationExistante != null)
        {
            // A returned line keeps its identity while editing. If the target emplacement already
            // exists for the same base line, the merge happens only on save.
            ligneDestinationExistante.qte_Retourner += quantite.toDouble()
            ligneDestinationExistante.retourPUI_Zone = getZoneNameForEmplacement(emplacementSelectionne)
            ligneDestinationExistante.retourPUI_Emplacement = emplacementSelectionne
            Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, ligneDestinationExistante)
            Retour_LigneOpenHelper.supprimerUnRetourLigne(db, ligneSelectionnee)
        }
        else
        {
            ligneSelectionnee.qte_Retourner = quantite.toDouble()
            ligneSelectionnee.retourPUI_Zone = getZoneNameForEmplacement(emplacementSelectionne)
            ligneSelectionnee.retourPUI_Emplacement = emplacementSelectionne
            Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, ligneSelectionnee)
        }

        onValider?.invoke()
    }

    private fun demanderConfirmationSiSuppressionNecessaire()
    {
        val quantite = quantiteCompteeET.text.toString().toIntOrNull() ?: 0
        val estSuppressionLigneRetournee = ligneCouranteExisteEnBDD && retourLigneCourante._UID < 0 && retourLigneCourante.qte_Retourner > 0 && quantite == 0

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
            enregistrerLigne()
        }

        buttonAnnuler.setOnClickListener { alertDialog.dismiss() }
    }

    private fun getRetourLigneByEmplacement(emplacement: String, excludedUid: Int? = null): Retour_Ligne?
    {
        return RetourPUIQuantiteHelper.getNegativeLinesForBase(db, retourCourant, retourLigneInitiale)
            .firstOrNull { it.retourPUI_Emplacement == emplacement && (excludedUid == null || it._UID != excludedUid) }
    }

    private fun getQuantiteRestantePourLigne(retourLigne: Retour_Ligne): Int
    {
        var quantiteRestante = retourLigneInitiale.qte_avant_retour.toInt()
        val retourLignesNegatives = RetourPUIQuantiteHelper.getNegativeLinesForBase(db, retourCourant, retourLigneInitiale)

        for (ligne in retourLignesNegatives) { if (ligne._UID != retourLigne._UID) { quantiteRestante -= ligne.qte_Retourner.toInt() } }

        return quantiteRestante.coerceAtLeast(0)
    }

    private fun createTemporaryRetourLigne(emplacement: String): Retour_Ligne
    {
        val ligne = Retour_Ligne(retourLigneInitiale)
        ligne._UID = RetourPUIQuantiteHelper.generateNegativeUid()
        ligne.retourPUI_Zone = getZoneNameForEmplacement(emplacement)
        ligne.retourPUI_Emplacement = emplacement
        ligne.emplacementOrigine = RetourPUIQuantiteHelper.buildBaseOrigin(retourLigneInitiale._UID)
        ligne.qte_Retourner = 0.0
        return ligne
    }

    private fun getZoneNameForEmplacement(emplacement: String): String
    {
        val depotEmplacement = emplacementsDisponibles.firstOrNull { it.adressage == emplacement }
        return if (depotEmplacement != null) { ZoneOpenHelper.getUneZoneByID(db, depotEmplacement.zoneID)?.zoneName ?: produit.zone_PUI_Defaut } else { produit.zone_PUI_Defaut }
    }

    private fun getEmplacementsZonePui(): List<Depot_Emplacement>
    {
        val depot = DepotOpenHelper.getDepotParReference(db, retourCourant.ref_Depot_Dest)
        val zoneName = produit.zone_PUI_Defaut?.trim().orEmpty()
        if (zoneName.isEmpty()) { return emptyList() }

        val zone = ZoneOpenHelper.getZoneByDepotEtNom(db, depot, zoneName) ?: return emptyList()
        return EmplacementOpenHelper.getEmplacementsParZoneID(db, zone.zoneID)
    }

    private fun getDisplayedLot(): String = RetourPUIQuantiteHelper.getDisplayedLot(retourLigneInitiale)

    private fun getPasQuantite(): Int
    {
        var pasNumberPicker = produit.cond_distrib.toInt()
        if (pasNumberPicker == 0 || pasNumberPicker >= this.maxQuantite) { pasNumberPicker = 1 }
        return max(1, pasNumberPicker)
    }

    private fun isEditingReturnedLine(): Boolean
    {
        val selectedUid = this.retourLigneSelectionneeUid ?: return false
        return selectedUid < 0 && this.retourLigneCourante._UID == selectedUid
    }

    private fun getListeMoisDatePicker(): Array<String> { return Array(12) { index -> String.format("%02d", index + 1) } }

    private fun getListeAnneeDatePicker(): Array<String>
    {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = currentYear - YEAR_RANGE_BEFORE
        val endYear = currentYear + YEAR_RANGE_AFTER
        return Array((endYear - startYear) + 1) { index -> (startYear + index).toString() }
    }
}
