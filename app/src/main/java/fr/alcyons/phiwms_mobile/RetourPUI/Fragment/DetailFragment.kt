package fr.alcyons.phiwms_mobile.RetourPUI.Fragment

import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.RetourPUI.DetailRetourPUIActivity
import java.util.Calendar
import kotlin.math.max

class DetailFragment : Fragment()
{

    var onFermer: (() -> Unit)? = null
    var onValider: ((ligne: Retour_Ligne) -> Unit)? = null

    private lateinit var db: SQLiteDatabase
    private lateinit var produit: Produit
    private lateinit var retourLigne: Retour_Ligne
    private var maxQuantite = 0
    private var emplacementsDisponibles: List<Depot_Emplacement> = emptyList()

    companion object
    {
        private const val ARG_LIGNE = "ligne"
        private const val ARG_MAX_QUANTITE = "max_quantite"

        fun newInstance(ligne: Retour_Ligne, maxQuantite: Int): DetailFragment = DetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_LIGNE, ligne)
                putInt(ARG_MAX_QUANTITE, maxQuantite)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_detail_ligne_retour_pui, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        this.db = (requireActivity() as DetailRetourPUIActivity).db

        this.retourLigne = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { arguments?.getSerializable(ARG_LIGNE, Retour_Ligne::class.java) } else { arguments?.getSerializable(ARG_LIGNE) as? Retour_Ligne } ?: return

        this.maxQuantite = arguments?.getInt(ARG_MAX_QUANTITE, this.retourLigne.qte_avant_retour.toInt()) ?: this.retourLigne.qte_avant_retour.toInt()
        this.produit = ProduitOpenHelper.getProduitByID(this.db, this.retourLigne.code_produit)

        val emplacementView = view.findViewById<AutoCompleteTextView>(R.id.emplacementLot_TV)
        val numeroLotET = view.findViewById<EditText>(R.id.numeroLot_ET)
        val quantiteCompteeET = view.findViewById<EditText>(R.id.quantiteComptee_ET)
        val spinnerMois = view.findViewById<Spinner>(R.id.selecteurDateMois_SP)
        val spinnerAnnee = view.findViewById<Spinner>(R.id.selecteurDateAnnee_SP)
        val layoutDatePeremption = view.findViewById<CardView>(R.id.layoutDatePeremption_CV)
        val restantTV = view.findViewById<TextView>(R.id.restantARetourner_TV)

        view.findViewById<TextView>(R.id.designationReference_TV).text = retourLigne.produit_Designation
        view.findViewById<CardView>(R.id.layoutCarton_CV).visibility = View.GONE
        view.findViewById<LinearLayout>(R.id.bandeauQteRestante_LL).visibility = View.VISIBLE
        restantTV.text = this.maxQuantite.toString()

        this.configureEmplacements(emplacementView)

        numeroLotET.setText(this.getDisplayedLot())
        numeroLotET.isFocusable = false
        numeroLotET.isFocusableInTouchMode = false
        numeroLotET.isClickable = false

        this.configureDatePeremption(spinnerMois, spinnerAnnee, layoutDatePeremption)

        val quantiteInitiale = this.retourLigne.qte_Retourner.toInt().coerceAtMost(this.maxQuantite)
        quantiteCompteeET.setText(quantiteInitiale.toString())

        val pasQuantite = max(1, produit.cond_distrib.toInt())
        view.findViewById<ImageView>(R.id.layoutPlus_LL).setOnClickListener {
            val quantite = (quantiteCompteeET.text.toString().toIntOrNull() ?: 0) + pasQuantite
            quantiteCompteeET.setText(quantite.coerceAtMost(this.maxQuantite).toString())
        }
        view.findViewById<ImageView>(R.id.layoutMoins_LL).setOnClickListener {
            val quantite = (quantiteCompteeET.text.toString().toIntOrNull() ?: 0) - pasQuantite
            quantiteCompteeET.setText(quantite.coerceAtLeast(0).toString())
        }

        view.findViewById<LinearLayout>(R.id.layoutFermer_LL).setOnClickListener { this.onFermer?.invoke() }

        view.findViewById<LinearLayout>(R.id.layoutValider_LL).setOnClickListener {
            val quantite = quantiteCompteeET.text.toString().toIntOrNull()
            if (quantite == null)
            {
                Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "Veuillez saisir une quantité valide", false, false)
                return@setOnClickListener
            }

            if (quantite > this.maxQuantite)
            {
                Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "La quantité ne peut pas dépasser $this.maxQuantite", false, false)
                return@setOnClickListener
            }

            val emplacementSelectionne = emplacementView.text.toString().trim()
            if (emplacementSelectionne.isEmpty())
            {
                Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "Veuillez sélectionner un emplacement", false, false)
                return@setOnClickListener
            }

            this.retourLigne.retourPUI_Zone = produit.zone_PUI_Defaut
            this.retourLigne.retourPUI_Emplacement = emplacementSelectionne
            this.retourLigne.qte_Retourner = quantite.toDouble()

            this.onValider?.invoke(this.retourLigne)
        }
    }

    private fun configureEmplacements(emplacementView: AutoCompleteTextView)
    {
        this.emplacementsDisponibles = this.getEmplacementsZonePui()
        val valeurs = this.emplacementsDisponibles.map { it.adressage }.distinct().toMutableList()

        val emplacementCourant = this.retourLigne.retourPUI_Emplacement?.trim().orEmpty()
        if (emplacementCourant.isNotEmpty() && emplacementCourant !in valeurs) { valeurs.add(0, emplacementCourant) }

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_depot, valeurs)
        emplacementView.setAdapter(adapter)
        emplacementView.setText(emplacementCourant.ifEmpty { this.produit.emplacement_PUI_Defaut }, false)
        emplacementView.isFocusable = false
        emplacementView.isFocusableInTouchMode = false
        emplacementView.setOnClickListener { emplacementView.showDropDown() }
        emplacementView.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) { emplacementView.showDropDown() } }
    }

    private fun configureDatePeremption(spinnerMois: Spinner, spinnerAnnee: Spinner, layoutDatePeremption: CardView)
    {
        val adapterMois = ArrayAdapter(requireContext(), R.layout.spinner_date_item, this.getListeMoisDatePicker())
        adapterMois.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMois.adapter = adapterMois
        spinnerMois.isEnabled = false

        val adapterAnnee = ArrayAdapter(requireContext(), R.layout.spinner_date_item, this.getListeAnneeDatePicker())
        adapterAnnee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAnnee.adapter = adapterAnnee
        spinnerAnnee.isEnabled = false

        val peremptionDate = retourLigne.peremptionDate?.trim().orEmpty()
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

    private fun getEmplacementsZonePui(): List<Depot_Emplacement>
    {
        val retour = RetourOpenHelper.getRetourByID(this.db, this.retourLigne.retour_UID)
        val depot = DepotOpenHelper.getDepotParReference(this.db, retour.ref_Depot_Dest)
        val zoneName = produit.zone_PUI_Defaut?.trim().orEmpty()
        if (zoneName.isEmpty()) { return emptyList() }

        val zone = ZoneOpenHelper.getZoneByDepotEtNom(this.db, depot, zoneName) ?: return emptyList()
        return EmplacementOpenHelper.getEmplacementsParZoneID(this.db, zone.zoneID)
    }

    private fun getDisplayedLot(): String
    {
        val lotRetourne = this.retourLigne.lot_Retourner?.trim().orEmpty()
        if (lotRetourne.isNotEmpty()) { return lotRetourne }

        return this.retourLigne.lot?.trim().orEmpty()
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
