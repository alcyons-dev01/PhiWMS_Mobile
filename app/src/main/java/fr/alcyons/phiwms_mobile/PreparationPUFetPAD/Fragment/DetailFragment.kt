package fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Adapter.LotAdapter
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.DetailPreparationV2
import fr.alcyons.phiwms_mobile.R
import java.util.Calendar
import java.util.Random
import androidx.core.graphics.drawable.toDrawable
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper

class DetailFragment : Fragment() {

    var onFermer: (() -> Unit)? = null
    var onValider: ((ligne: PH_Preparation_Ligne?, ajout:Boolean) -> Unit)? = null

    private lateinit var db: SQLiteDatabase // ton type de BDD

    lateinit var produit : Produit

    lateinit var preparationCourante : PH_Preparation

    var listeLotDisponible: MutableList<Stock_Lot_Emplacement_Light>? = null
    var autoCompleteAdapter: ArrayAdapter<String?>? = null
    var autoComplete: AutoCompleteTextView? = null

    var maxAPreparer = 0

    var nouveauLot = false

    companion object {
        private const val ARG_LIGNE = "ligne"

        fun newInstance(phPLBase: PH_Preparation_Ligne?, produit : Produit) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LIGNE, phPLBase)
                }
                this.produit = produit
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_detail_preparationligne, container, false)

    fun mettreAJourLigne(ligne: PH_Preparation_Ligne) {
        val view = view ?: return
        view.findViewById<TextView>(R.id.emplacementLot_TV).text = ligne.emplacementParDefaut
        view.findViewById<TextView>(R.id.designationReference_TV).text = ligne.produitDesignation
        view.findViewById<EditText>(R.id.quantiteComptee_ET)
            .setText(ligne.qte_preparer.toString())
        view.findViewById<EditText>(R.id.numeroLot_ET)
            .setText(ligne.lotNumero ?: "")
        // Mettez à jour les autres champs si nécessaire
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = (requireActivity() as DetailPreparationV2).db

        val preparationLigneBase = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_LIGNE, PH_Preparation_Ligne::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable(ARG_LIGNE) as? PH_Preparation_Ligne
        } ?: return
        this.preparationCourante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparationLigneBase.preparationID)

        // step modifiable selon le bouton actif (x10 / x100 / Carton ouvert)
        val conditionnementCartonOuvert: Int = preparationLigneBase.produitCondDistrib.toInt()
        val conditionnementX10: Int = conditionnementCartonOuvert * 10
        val conditionnementX100: Int = conditionnementCartonOuvert * 100
        var conditionnement: Int = conditionnementCartonOuvert

        val layoutListeLot_LL = view.findViewById<LinearLayout>(R.id.layoutListeLot_LL)
        val layoutLotPeremption_LL = view.findViewById<LinearLayout>(R.id.layoutLotPeremption_LL)
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
        val effacerLot = view.findViewById<ImageView>(R.id.effacerLot_IV)

        // ─── Bloc multiplicateur (x10 / x100 / Carton ouvert) ───
        val layoutCarton_CV = view.findViewById<CardView>(R.id.layoutCarton_CV)
        val layoutMultiple10LL = view.findViewById<LinearLayout>(R.id.layoutmultiple10)
        val layoutMultiple100LL = view.findViewById<LinearLayout>(R.id.layoutmultiple100)
        val layoutCartonOuvertLL = view.findViewById<LinearLayout>(R.id.layoutCartonOuvert_LL)

        val textMultiple10TV = view.findViewById<TextView>(R.id.textMultiple10_TV)
        val textMultiple100TV = view.findViewById<TextView>(R.id.textMultiple100_TV)
        val textCartonOuvertTV = view.findViewById<TextView>(R.id.textCartonOuvert_TV)

        val boutonsConditionnement = listOf(
            layoutMultiple10LL to textMultiple10TV,
            layoutMultiple100LL to textMultiple100TV,
            layoutCartonOuvertLL to textCartonOuvertTV
        )

        fun selectionnerConditionnement(actif: LinearLayout) {
            boutonsConditionnement.forEach { (layout, texte) ->
                if (layout == actif) {
                    layout.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.bleu_fonce_alcyons)
                    )
                    texte.setTextColor(ContextCompat.getColor(requireContext(), R.color.blanc))
                } else {
                    val outValue = android.util.TypedValue()
                    requireContext().theme.resolveAttribute(
                        android.R.attr.selectableItemBackground, outValue, true
                    )
                    layout.setBackgroundResource(outValue.resourceId)
                    texte.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.bleu_fonce_alcyons)
                    )
                }
            }
        }

        // caché tant qu'aucun lot n'est sélectionné ou saisi
        layoutCarton_CV.visibility = View.GONE

        // textes dynamiques selon le conditionnement de distribution du produit
        textMultiple10TV.text = "x" + conditionnementX10.toString()
        textMultiple100TV.text = "x"+ conditionnementX100.toString()
        textCartonOuvertTV.text = "x" + conditionnementCartonOuvert.toString()

        // état par défaut : Carton ouvert sélectionné
        selectionnerConditionnement(layoutCartonOuvertLL)

        layoutMultiple10LL.setOnClickListener {
            conditionnement = conditionnementX10
            selectionnerConditionnement(layoutMultiple10LL)
        }

        layoutMultiple100LL.setOnClickListener {
            conditionnement = conditionnementX100
            selectionnerConditionnement(layoutMultiple100LL)
        }

        layoutCartonOuvertLL.setOnClickListener {
            conditionnement = conditionnementCartonOuvert
            selectionnerConditionnement(layoutCartonOuvertLL)
        }
        // ─── Fin bloc multiplicateur ───

        //gestion de la date de péremption
        var datePeremption_String = ""

        //tant que l'on a pas sélectionné de lot on ne peux pas saisir de quantité
        quantiteCompteeET.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            isClickable = false
        }

        //gestion de l'autocomplete
        //récupération des lots disponible du produit
        val depot = DepotOpenHelper.getDepotPUI(db);

        //déclaration de la quantité max préparable
        maxAPreparer = preparationLigneBase.qte_APreparer

        listeLotDisponible = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepotString(db, produit, depot)
        listeLotDisponible?.sortWith(compareBy { it.peremptionDate })

        val ajouterLot = Stock_Lot_Emplacement_Light()
        ajouterLot.setLot("Ajouter un lot")
        listeLotDisponible?.add(ajouterLot)

        val selectionnerLot = Stock_Lot_Emplacement_Light()
        selectionnerLot.setLot("Sélectionner un lot")
        listeLotDisponible?.add(0, selectionnerLot)

        autoComplete = view.findViewById<AutoCompleteTextView?>(R.id.lot_Autocomplete)

        val adapter = LotAdapter(requireContext(), listeLotDisponible.orEmpty())
        autoComplete?.setAdapter(adapter)

        /*autoCompleteAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item_depot, listeLotDisponible.orEmpty())
        autoComplete!!.setAdapter<ArrayAdapter<String?>?>(autoCompleteAdapter)*/
        autoComplete!!.setThreshold(100) // Empêche le filtrage automatique

        // Affiche le premier élément par défaut
        if (!listeLotDisponible!!.isEmpty()) {
            autoComplete!!.setText(listeLotDisponible!![0].lot, false)
        }

        // Hauteur = 1/3 de l'écran
        val hauteurEcran = resources.displayMetrics.heightPixels
        autoComplete!!.dropDownHeight = hauteurEcran / 3
        val dpToPx = (12 * resources.displayMetrics.density).toInt()
        autoComplete!!.post(Runnable { autoComplete!!.dropDownWidth =
            view.findViewById<View?>(R.id.listeLotDisponible_LL).getWidth() - dpToPx })
        autoComplete!!.setDropDownBackgroundResource(android.R.color.white)

        // Ouvre la liste au clic
        autoComplete!!.setOnClickListener(View.OnClickListener { v: View? -> autoComplete!!.showDropDown() })

        // Chevron ouvre aussi la liste
        view.findViewById<View?>(R.id.chevronFiltre).setOnClickListener { v: View? -> autoComplete!!.showDropDown() }

        // Gère la sélection
        autoComplete!!.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->

            //on récupère les informations du lot sélectionné
            val stockSelectionne = listeLotDisponible!![position]

            if (stockSelectionne.lot != "Sélectionner un lot") {
                if (stockSelectionne.lot == "Ajouter un lot") {
                    nouveauLot = true
                    view.findViewById<LinearLayout>(R.id.bandeauNouveauLot_LL).visibility =
                        View.VISIBLE
                    layoutCarton_CV.visibility = View.VISIBLE
                    spinnerMoisDatePeremptionSP.setSelection(0)
                    spinnerAnneeDatePeremptionSP.setSelection(0)
                    layoutListeLot_LL.visibility = View.GONE
                    layoutLotPeremption_LL.visibility = View.VISIBLE
                    view.findViewById<EditText>(R.id.numeroLot_ET).setText("")
                    view.findViewById<TextView>(R.id.emplacementLot_TV).text =
                        produit.emplacement_PUI_Defaut
                    view.findViewById<EditText>(R.id.numeroLot_ET).apply {
                        isFocusable = true
                        isFocusableInTouchMode = true
                        isClickable = true
                        requestFocus()
                        val imm =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                    }

                    spinnerMoisDatePeremptionSP.apply {
                        isEnabled = true
                    }
                    spinnerAnneeDatePeremptionSP.apply {
                        isEnabled = true
                    }
                } else {
                    view.findViewById<LinearLayout>(R.id.bandeauNouveauLot_LL).visibility =
                        View.GONE
                    layoutCarton_CV.visibility = View.VISIBLE
                    //on affiche le lot et la date de péremption
                    view.findViewById<EditText>(R.id.numeroLot_ET).setText(stockSelectionne.lot)
                    view.findViewById<TextView>(R.id.emplacementLot_TV).text =
                        stockSelectionne.emplacement
                    val parts = stockSelectionne.peremptionDate.split("-")
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

                    datePeremption_String = stockSelectionne.peremptionDate

                    layoutListeLot_LL.visibility = View.GONE
                    layoutLotPeremption_LL.visibility = View.VISIBLE

                    //gestion du maximum a preparer
                    if (maxAPreparer > stockSelectionne.qte)
                        maxAPreparer = stockSelectionne.qte.toInt()

                    view.findViewById<EditText>(R.id.numeroLot_ET).apply {
                        isFocusable = false
                        isFocusableInTouchMode = false
                        isClickable = false
                    }

                    spinnerMoisDatePeremptionSP.apply {
                        isEnabled = false
                    }
                    spinnerAnneeDatePeremptionSP.apply {
                        isEnabled = false
                    }
                }

                //au clic sur le lot on réouvre la liste
                effacerLot.setOnClickListener {
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                    if (imm.isAcceptingText) {
                        // Le clavier est visible
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    view.postDelayed({
                        layoutListeLot_LL.visibility = View.VISIBLE
                        layoutLotPeremption_LL.visibility = View.GONE
                        layoutCarton_CV.visibility = View.GONE
                        view.findViewById<View?>(R.id.chevronFiltre).performClick()
                    }, 200)
                }

                //gestion du conditionnment
                layoutPlusLL.setOnClickListener { _: View? ->
                    var qteActuelle = quantiteCompteeET.text.toString().toInt()
                    if (qteActuelle == -1)
                        qteActuelle = 0

                    qteActuelle += conditionnement

                    if (qteActuelle > maxAPreparer)
                        qteActuelle = maxAPreparer
                    quantiteCompteeET.setText(qteActuelle.toString())
                }
                layoutMoinsLL.setOnClickListener { _: View? ->
                    var qteActuelle = quantiteCompteeET.text.toString().toInt()
                    qteActuelle -= conditionnement
                    if (qteActuelle < 0) qteActuelle = 0
                    quantiteCompteeET.setText(qteActuelle.toString())
                }
            } else {
                layoutPlusLL.setOnClickListener(null)
                layoutMoinsLL.setOnClickListener(null)
                layoutCarton_CV.visibility = View.GONE
                view.findViewById<EditText>(R.id.numeroLot_ET).setText("")
            }
        }

        //gestion des données
        if(preparationLigneBase.emplacementParDefaut == "")
        {
            view.findViewById<TextView>(R.id.emplacementLot_TV).text = preparationLigneBase.emplacementParDefaut
        }
        else
        {
            view.findViewById<TextView>(R.id.emplacementLot_TV).text = produit.emplacement_PUI_Defaut
        }

        view.findViewById<TextView>(R.id.designationReference_TV).text = preparationLigneBase.produitDesignation
        var preparationLigneBasePreparation = preparationLigneBase
        if(preparationLigneBase._UID < 0)
        {
            //on remet en place la quantité qui sera modifié après coup
            val ph_preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparationLigneBase.preparationID)
            preparationLigneBasePreparation = PH_Preparation_LigneOpenHelper.getUnPHPreparationLignesBaseParPHPreparationetProduit(db, ph_preparation,preparationLigneBase.produitID)

            preparationLigneBasePreparation.qte_APreparer += preparationLigneBase.qte_preparer
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, preparationLigneBasePreparation)

            view.findViewById<EditText>(R.id.numeroLot_ET).setText(preparationLigneBase.lotNumero.toString())
            quantiteCompteeET.setText(preparationLigneBase.qte_preparer.toString())

            layoutListeLot_LL.visibility = View.GONE
            layoutLotPeremption_LL.visibility = View.VISIBLE
            layoutCarton_CV.visibility = View.VISIBLE
            effacerLot.visibility = View.GONE

            val stockCourant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByLotPeremptionEtDepot(db, preparationLigneBase.lotNumero, preparationLigneBase.peremptionDate, depot)

            if(stockCourant != null)
                maxAPreparer = stockCourant.qte.toInt()
            else
                maxAPreparer = preparationLigneBasePreparation.qte_APreparer

            if(maxAPreparer > preparationLigneBasePreparation.qte_APreparer)
                maxAPreparer = preparationLigneBasePreparation.qte_APreparer

            layoutPlusLL.setOnClickListener { _: View? ->
                var qteActuelle = quantiteCompteeET.text.toString().toInt()
                if (qteActuelle == -1)
                    qteActuelle = 0

                qteActuelle += conditionnement

                if (qteActuelle > maxAPreparer)
                    qteActuelle = maxAPreparer
                quantiteCompteeET.setText(qteActuelle.toString())
            }
            layoutMoinsLL.setOnClickListener { _: View? ->
                var qteActuelle = quantiteCompteeET.text.toString().toInt()
                qteActuelle -= conditionnement
                if (qteActuelle < 0) qteActuelle = 0
                quantiteCompteeET.setText(qteActuelle.toString())
            }

            datePeremption_String = preparationLigneBase.peremptionDate
        }
        else
        {
            view.findViewById<EditText>(R.id.numeroLot_ET).setText("")
            quantiteCompteeET.setText("0")
        }

        //affichage du restant à préparer
        view.findViewById<TextView>(R.id.restantAPreparer_TV).setText(preparationLigneBasePreparation.qte_APreparer.toString())

        //gestion de la date de péremption
        adapterMoisPeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMoisDatePeremptionSP.adapter = adapterMoisPeremption
        adapterAnneePeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAnneeDatePeremptionSP.adapter = adapterAnneePeremption
        spinnerAnneeDatePeremptionSP.setSelection(3)

        if (!preparationLigneBase.peremptionDate.isNullOrEmpty() && preparationLigneBase.peremptionDate != "0000-00-00") {
            val parts = preparationLigneBase.peremptionDate.split("-")
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
        val produit = ProduitOpenHelper.getProduitByID(db, preparationLigneBase.produitID)
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
                if(preparationLigneBase._UID < 0)
                {
                    val preparationCourante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparationLigneBase.preparationID)
                    //on remet en place la quantité qui sera modifié après coup
                    val preparationLigneBasePreparation = PH_Preparation_LigneOpenHelper.getUnPHPreparationLignesAPreparerParPHPreparationetProduit(db, preparationCourante,preparationLigneBase.produitID)

                    preparationLigneBasePreparation.qte_APreparer -= preparationLigneBase.qte_preparer
                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, preparationLigneBasePreparation)
                }

                onFermer?.invoke()
            }

        //gestion du bouton valider
        view.findViewById<LinearLayout>(R.id.layoutValider_LL)
            .setOnClickListener {
                val quantite = quantiteCompteeET.text.toString().toInt()

                if(quantite == 0 && preparationLigneBase._UID > 0)
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
                else if(quantite == 0 && preparationLigneBase._UID < 0)
                {
                    demandeConfirmation(LayoutInflater.from(context)) {
                        if (it) {
                            PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, preparationLigneBase)
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                                db,
                                PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE,
                                preparationLigneBase.getPhiMR4UUID(),
                                preparationLigneBase._UID,
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
                        preparationLigneBasePreparation.qte_APreparer-=quantite
                        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, preparationLigneBasePreparation)

                        if (!produit.isSuivi_Lot && lot.isEmpty())
                            lot = "LOT NON TRACÉ"

                        val moisIndex = spinnerMoisDatePeremptionSP.selectedItemPosition + 1
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

                        if(datePeremption_String != "")
                            datePeremption = datePeremption_String

                        val phPreparationLigneliste =
                            PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(
                                db,
                                preparationCourante,
                                produit.iD_produit
                            )
                        var phPreparationLigneCourant: PH_Preparation_Ligne = preparationLigneBase

                        var existe = false

                        for (prepaLigneCourant in phPreparationLigneliste) {
                            if (prepaLigneCourant.lotNumero.trim { it <= ' ' }.contentEquals(
                                    lot
                                        .trim { it <= ' ' }) && prepaLigneCourant.peremptionDate
                                    .trim { it <= ' ' }.contentEquals(datePeremption) && prepaLigneCourant.serieNumero == "" && prepaLigneCourant.produitID == preparationLigneBase.produitID
                            ) {
                                phPreparationLigneCourant = prepaLigneCourant
                                existe = true
                            }
                        }

                        if (existe) {
                            if(preparationLigneBase._UID < 0)
                                phPreparationLigneCourant.qte_preparer = quantite
                            else
                                phPreparationLigneCourant.qte_preparer += quantite
                            onValider?.invoke(phPreparationLigneCourant, false)
                        } else
                        {
                            val randompreparationligne = Random()
                            var preparationLigneId = randompreparationligne.nextInt()
                            if (preparationLigneId > 0) preparationLigneId = preparationLigneId * -1

                            phPreparationLigneCourant._UID = preparationLigneId
                            val numeroLot = lot
                            val zoneName = preparationLigneBase.zoneDepot
                            val emplacementName = preparationLigneBase.emplacementParDefaut

                            phPreparationLigneCourant.lotNumero = numeroLot.trim { it <= ' ' }
                            phPreparationLigneCourant.peremptionDate = datePeremption.trim { it <= ' ' }
                            phPreparationLigneCourant.qte_preparer = quantite

                            onValider?.invoke(phPreparationLigneCourant, true)
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
        alertDialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
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