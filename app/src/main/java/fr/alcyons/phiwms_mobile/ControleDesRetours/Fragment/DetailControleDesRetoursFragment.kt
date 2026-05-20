package fr.alcyons.phiwms_mobile.ControleDesRetours.Fragment

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.EditorInfo
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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Depot
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Classes.Retour
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light
import fr.alcyons.phiwms_mobile.ControleDesRetours.DetailControleDesRetoursActivity
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Adapter.LotAdapter
import fr.alcyons.phiwms_mobile.R
import java.util.Calendar
import java.util.Objects
import java.util.Random
import kotlin.math.max
import kotlin.math.min

class DetailControleDesRetoursFragment : Fragment()
{
    var onFermer: (() -> Unit)? = null
    var onValider: (() -> Unit)? = null

    private data class DatePeremptionViews(
        val spinnerMois: Spinner,
        val spinnerAnnee: Spinner,
        val layout: CardView
    )

    private lateinit var db: SQLiteDatabase
    private lateinit var retourLigneBase: Retour_Ligne
    private lateinit var retourCourant: Retour
    private lateinit var depot: Depot
    private lateinit var produit: Produit
    private lateinit var lotView: AutoCompleteTextView
    private lateinit var numeroLotET: EditText
    private lateinit var layoutListeLot: LinearLayout
    private lateinit var layoutLotPeremption: LinearLayout
    private lateinit var bandeauNouveauLotLL: LinearLayout
    private lateinit var effacerLotIV: ImageView
    private lateinit var quantiteCompteeET: EditText
    private lateinit var restantTV: TextView
    private lateinit var datePeremptionViews: DatePeremptionViews

    private var lotsDisponibles: MutableList<Stock_Lot_Emplacement_Light> = mutableListOf()
    private var lotsDropdown: MutableList<Stock_Lot_Emplacement_Light> = mutableListOf()
    private var stockSelectionne: Stock_Lot_Emplacement_Light? = null
    private var retourLigneSelectionnee: Retour_Ligne? = null
    private var maxQuantite = 0
    private var lastQuantityClickAt = 0L
    private var nouveauLot = false
    private var insertedOrUpdatedNewLot = false

    companion object
    {
        private const val ARG_LIGNE = "ligne"
        private const val EMPTY_DATE = "0000-00-00"
        private const val ADD_LOT_LABEL = "Ajouter un lot"
        private const val SELECT_LOT_LABEL = "Sélectionner un lot"
        private const val YEAR_RANGE_BEFORE = 2
        private const val YEAR_RANGE_AFTER = 10
        private const val QUANTITY_CLICK_DEBOUNCE_MS = 180L

        fun newInstance(ligne: Retour_Ligne): DetailControleDesRetoursFragment = DetailControleDesRetoursFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_LIGNE, ligne) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return inflater.inflate(R.layout.fragment_detail_controle_retours, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        db = (requireActivity() as DetailControleDesRetoursActivity).db
        retourLigneBase = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_LIGNE, Retour_Ligne::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable(ARG_LIGNE) as? Retour_Ligne
        } ?: return

        retourCourant = RetourOpenHelper.getRetourByID(db, retourLigneBase.retour_UID)
        depot = DepotOpenHelper.getDepotParReference(db, retourCourant.ref_Depot_Origine)
        produit = ProduitOpenHelper.getProduitByID(db, retourLigneBase.code_produit)

        bindViews(view)
        setupStaticUi(view)
        configureDatePeremption()
        configureLots()
        setupQuantiteButtons(view)
        setupActionButtons(view)
    }

    private fun bindViews(view: View)
    {
        lotView = view.findViewById(R.id.lot_Autocomplete)
        numeroLotET = view.findViewById(R.id.numeroLot_ET)
        layoutListeLot = view.findViewById(R.id.layoutListeLot_LL)
        layoutLotPeremption = view.findViewById(R.id.layoutLotPeremption_LL)
        bandeauNouveauLotLL = view.findViewById(R.id.bandeauNouveauLot_LL)
        effacerLotIV = view.findViewById(R.id.effacerLot_IV)
        quantiteCompteeET = view.findViewById(R.id.quantiteComptee_ET)
        restantTV = view.findViewById(R.id.restantAControler_TV)
        datePeremptionViews = DatePeremptionViews(
            spinnerMois = view.findViewById(R.id.selecteurDateMois_SP),
            spinnerAnnee = view.findViewById(R.id.selecteurDateAnnee_SP),
            layout = view.findViewById(R.id.layoutDatePeremption_CV)
        )
    }

    private fun setupStaticUi(view: View)
    {
        view.findViewById<TextView>(R.id.designationReference_TV).text = retourLigneBase.produit_Designation
        numeroLotET.isFocusable = false
        numeroLotET.isFocusableInTouchMode = false
        numeroLotET.isClickable = false
        effacerLotIV.setOnClickListener { showLotPicker() }
        quantiteCompteeET.setSelectAllOnFocus(true)
        quantiteCompteeET.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) { selectAllQuantityText() } }
        quantiteCompteeET.setOnClickListener { selectAllQuantityText() }
        quantiteCompteeET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                enregistrerLigne()
                true
            } else false
        }
    }

    private fun configureLots()
    {
        lotsDisponibles = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot).toMutableList()
        lotsDisponibles.sortWith(compareBy<Stock_Lot_Emplacement_Light> { it.peremptionDate ?: "" }.thenBy { it.lot ?: "" }.thenBy { it.serie ?: "" })

        lotsDropdown = mutableListOf<Stock_Lot_Emplacement_Light>().apply {
            add(Stock_Lot_Emplacement_Light().also { it.lot = SELECT_LOT_LABEL })
            addAll(lotsDisponibles)
            add(Stock_Lot_Emplacement_Light().also { it.lot = ADD_LOT_LABEL })
        }

        val adapter = LotAdapter(requireContext(), lotsDropdown)
        lotView.setAdapter(adapter)
        lotView.threshold = 100
        lotView.dropDownHeight = resources.displayMetrics.heightPixels / 3
        lotView.setDropDownBackgroundResource(android.R.color.white)
        lotView.setText(SELECT_LOT_LABEL, false)
        lotView.setOnClickListener { lotView.showDropDown() }
        view?.findViewById<ImageView>(R.id.chevronFiltre)?.setOnClickListener { lotView.showDropDown() }
        lotView.setOnItemClickListener { _, _, position, _ -> onLotSelected(position) }

        initializeFirstSelection()
    }

    private fun initializeFirstSelection()
    {
        val premiereLigneRetournee = if (retourLigneBase._UID < 0) { retourLigneBase } else { getRetourLignesRetournees().firstOrNull() }
        if (premiereLigneRetournee != null)
        {
            val stock = lotsDisponibles.firstOrNull { sameLot(it, premiereLigneRetournee) }
            if (stock != null)
            {
                lotView.setText(stock.lot, false)
                applySelection(stock, premiereLigneRetournee)
                return
            }
        }

        quantiteCompteeET.setText("0")
        restantTV.text = getQuantiteRestanteSansSelection().toString()
    }

    private fun onLotSelected(position: Int)
    {
        when
        {
            position == 0 -> clearSelection()
            position == lotsDisponibles.size + 1 -> showManualLotEntry()
            else -> {
                val stock = lotsDropdown[position]
                lotView.setText(stock.lot, false)
                applySelection(stock, getRetourLigneForStock(stock))
            }
        }
    }

    private fun applySelection(stock: Stock_Lot_Emplacement_Light, ligneRetournee: Retour_Ligne?)
    {
        nouveauLot = false
        stockSelectionne = stock
        retourLigneSelectionnee = ligneRetournee
        maxQuantite = getMaxQuantitePourStock(stock, ligneRetournee)
        restantTV.text = maxQuantite.toString()
        numeroLotET.setText(stock.lot.orEmpty())
        val allowLotNameEdit = ligneRetournee?._UID ?: 0 < 0
        numeroLotET.isFocusable = allowLotNameEdit
        numeroLotET.isFocusableInTouchMode = allowLotNameEdit
        numeroLotET.isClickable = allowLotNameEdit
        datePeremptionViews.spinnerMois.isEnabled = false
        datePeremptionViews.spinnerAnnee.isEnabled = false
        bandeauNouveauLotLL.visibility = View.GONE
        layoutListeLot.visibility = View.GONE
        layoutLotPeremption.visibility = View.VISIBLE

        val quantiteAffichee = ligneRetournee?.qte_Retourner?.toInt()?.coerceAtMost(maxQuantite)
            ?: maxQuantite
        quantiteCompteeET.setText(quantiteAffichee.toString())
        applyPeremption(stock.peremptionDate)
    }

    private fun clearSelection()
    {
        nouveauLot = false
        stockSelectionne = null
        retourLigneSelectionnee = null
        maxQuantite = 0
        bandeauNouveauLotLL.visibility = View.GONE
        numeroLotET.isFocusable = false
        numeroLotET.isFocusableInTouchMode = false
        numeroLotET.isClickable = false
        datePeremptionViews.spinnerMois.isEnabled = false
        datePeremptionViews.spinnerAnnee.isEnabled = false
        quantiteCompteeET.setText("0")
        restantTV.text = getQuantiteRestanteSansSelection().toString()
        showLotPicker()
    }

    private fun showLotPicker()
    {
        nouveauLot = false
        stockSelectionne = null
        retourLigneSelectionnee = null
        numeroLotET.setText("")
        lotView.setText(SELECT_LOT_LABEL, false)
        bandeauNouveauLotLL.visibility = View.GONE
        numeroLotET.isFocusable = false
        numeroLotET.isFocusableInTouchMode = false
        numeroLotET.isClickable = false
        datePeremptionViews.spinnerMois.isEnabled = false
        datePeremptionViews.spinnerAnnee.isEnabled = false
        layoutLotPeremption.visibility = View.GONE
        layoutListeLot.visibility = View.VISIBLE
        lotView.post { lotView.showDropDown() }
    }

    private fun showManualLotEntry()
    {
        nouveauLot = true
        stockSelectionne = null
        retourLigneSelectionnee = null
        maxQuantite = getQuantiteRestanteSansSelection()
        restantTV.text = maxQuantite.toString()
        quantiteCompteeET.setText(maxQuantite.toString())
        numeroLotET.setText("")
        bandeauNouveauLotLL.visibility = View.VISIBLE
        layoutListeLot.visibility = View.GONE
        layoutLotPeremption.visibility = View.VISIBLE
        datePeremptionViews.layout.visibility = View.VISIBLE
        datePeremptionViews.spinnerMois.isEnabled = true
        datePeremptionViews.spinnerAnnee.isEnabled = true
        datePeremptionViews.spinnerMois.setSelection(0)
        datePeremptionViews.spinnerAnnee.setSelection(YEAR_RANGE_BEFORE.coerceAtMost(datePeremptionViews.spinnerAnnee.adapter.count - 1))
        numeroLotET.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true
            requestFocus()
        }
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(numeroLotET, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupQuantiteButtons(view: View)
    {
        view.findViewById<ImageView>(R.id.layoutPlus_LL).setOnClickListener { ajusterQuantite(getPasQuantite()) }
        view.findViewById<ImageView>(R.id.layoutMoins_LL).setOnClickListener { ajusterQuantite(-getPasQuantite()) }
    }

    private fun ajusterQuantite(delta: Int)
    {
        if (!canHandleQuantityClick()) { return }
        val quantiteActuelle = quantiteCompteeET.text.toString().toIntOrNull() ?: 0
        val quantite = (quantiteActuelle + delta).coerceIn(0, maxQuantite)
        quantiteCompteeET.setText(quantite.toString())
    }

    private fun canHandleQuantityClick(): Boolean
    {
        val now = SystemClock.elapsedRealtime()
        if (now - lastQuantityClickAt < QUANTITY_CLICK_DEBOUNCE_MS) { return false }
        lastQuantityClickAt = now
        return true
    }

    private fun setupActionButtons(view: View)
    {
        view.findViewById<LinearLayout>(R.id.layoutFermer_LL).setOnClickListener { onFermer?.invoke() }
        view.findViewById<LinearLayout>(R.id.layoutValider_LL).setOnClickListener { demanderConfirmationSiSuppressionNecessaire() }
    }

    private fun enregistrerLigne()
    {
        val quantite = verifierQuantiteSaisie() ?: return
        val isLotNameChanged = isSelectedLotNameChanged()
        if (isLotNameChanged && quantite > getQuantiteRestanteSansSelection())
        {
            Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "La quantité ne peut pas dépasser ${getQuantiteRestanteSansSelection()}", false, false)
            return
        }

        val stock = if (nouveauLot || isLotNameChanged) { getOrCreateManualStock(quantite) } else { stockSelectionne }
        if (stock == null)
        {
            Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "Veuillez sélectionner un lot", false, false)
            return
        }

        val ligneExistante = if (isLotNameChanged) { getRetourLigneForStock(stock, retourLigneSelectionnee?._UID) } else { retourLigneSelectionnee ?: getRetourLigneForStock(stock) }

        if (quantite == 0)
        {
            if (!isLotNameChanged && ligneExistante != null) { supprimerRetourLigne(ligneExistante, stock) }
            onValider?.invoke()
            return
        }

        if (ligneExistante != null)
        {
            ligneExistante.qte_Retourner = quantite.toDouble()
            Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, ligneExistante)
        }
        else
        {
            val nouvelleLigne = Retour_Ligne(retourLigneBase)
            var retourLigneId = Random().nextInt()
            if (retourLigneId > 0) { retourLigneId *= -1 }
            nouvelleLigne._UID = retourLigneId
            nouvelleLigne.qte_Retourner = quantite.toDouble()
            nouvelleLigne.lot_Retourner = stock.lot
            nouvelleLigne.serie_Retourner = stock.serie
            nouvelleLigne.peremptionDate = stock.peremptionDate
            Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, nouvelleLigne)
            if (nouveauLot || isLotNameChanged) { markRetourLigneForSync(nouvelleLigne, DBOpenHelper.ActionsEAS.AJOUT) }
        }

        stock.qte_Preparer = quantite
        if (stock._UID == 0)
        {
            var stockId = Random().nextInt()
            if (stockId > 0) { stockId *= -1 }
            stock._UID = stockId
            Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stock)
            markStockForSync(stock, DBOpenHelper.ActionsEAS.AJOUT)
        }
        else
        {
            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock)
            if (nouveauLot || isLotNameChanged) { markStockForSync(stock, DBOpenHelper.ActionsEAS.MAJ) }
        }
        syncNewLotIfNeeded()
        onValider?.invoke()
    }

    private fun isSelectedLotNameChanged(): Boolean
    {
        val selectedStock = stockSelectionne ?: return false
        val selectedLine = retourLigneSelectionnee ?: return false
        if (selectedLine._UID >= 0) { return false }
        return numeroLotET.text.toString().trim() != selectedStock.lot.orEmpty().trim()
    }

    private fun getOrCreateManualStock(quantite: Int): Stock_Lot_Emplacement_Light?
    {
        val numeroLot = numeroLotET.text.toString().trim()
        if (numeroLot.isEmpty())
        {
            Alerte.afficherAlerteInformation(requireContext(), LayoutInflater.from(requireContext()), "Erreur", "Veuillez saisir un numéro de lot", false, false)
            return null
        }

        val datePeremption = if (!nouveauLot && stockSelectionne?.peremptionDate?.isNotBlank() == true) { stockSelectionne?.peremptionDate.orEmpty() } else { getSelectedPeremptionDate() }
        val stockExistant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(db, produit, depot, numeroLot, "")
        if (stockExistant != null && stockExistant.peremptionDate.orEmpty().trim() == datePeremption)
        {
            return stockExistant
        }

        val emplacement = retourLigneBase.retourPUI_Emplacement?.takeIf { it.isNotBlank() } ?: produit.emplacement_PUI_Defaut.orEmpty()
        val zone = retourLigneBase.retourPUI_Zone?.takeIf { it.isNotBlank() } ?: produit.zone_PUI_Defaut.orEmpty()
        return Stock_Lot_Emplacement_Light(
            quantite.toDouble(),
            numeroLot,
            datePeremption,
            emplacement,
            depot.depot_Reference,
            zone,
            produit.getID_produit(),
            quantite,
            ""
        )
    }

    private fun markRetourLigneForSync(ligne: Retour_Ligne, action: String)
    {
        insertedOrUpdatedNewLot = true
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, ligne.phiMR4UUID, ligne._UID, action)
    }

    private fun markStockForSync(stock: Stock_Lot_Emplacement_Light, action: String)
    {
        insertedOrUpdatedNewLot = true
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, stock.phiMR4UUID, stock._UID, action)
    }

    private fun syncNewLotIfNeeded()
    {
        if (!insertedOrUpdatedNewLot) { return }
        val activity = requireActivity() as DetailControleDesRetoursActivity
        ElementASynchroniserOpenHelper.toutSynchroniser(activity, db, activity.utilisateurConnecte, true)
    }

    private fun demanderConfirmationSiSuppressionNecessaire()
    {
        val quantite = quantiteCompteeET.text.toString().toIntOrNull() ?: 0
        if (quantite != 0 || retourLigneSelectionnee == null)
        {
            enregistrerLigne()
            return
        }

        val builder = AlertDialog.Builder(requireContext())
        val layout = layoutInflater.inflate(R.layout.alerte_confirmation, null)
        val buttonOk = layout.findViewById<LinearLayout>(R.id.buttonOk)
        val buttonAnnuler = layout.findViewById<LinearLayout>(R.id.buttonAnnuler)
        layout.findViewById<TextView>(R.id.messageFin).text = "La quantité retournée va être remise à 0. Voulez-vous continuer ?"
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

    private fun supprimerRetourLigne(ligne: Retour_Ligne, stock: Stock_Lot_Emplacement_Light)
    {
        Retour_LigneOpenHelper.supprimerUnRetourLigne(db, ligne)
        stock.qte_Preparer = 0
        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock)
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

    private fun configureDatePeremption()
    {
        val adapterMois = android.widget.ArrayAdapter(requireContext(), R.layout.spinner_date_item, getListeMoisDatePicker())
        adapterMois.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        datePeremptionViews.spinnerMois.adapter = adapterMois
        datePeremptionViews.spinnerMois.isEnabled = false

        val adapterAnnee = android.widget.ArrayAdapter(requireContext(), R.layout.spinner_date_item, getListeAnneeDatePicker())
        adapterAnnee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        datePeremptionViews.spinnerAnnee.adapter = adapterAnnee
        datePeremptionViews.spinnerAnnee.isEnabled = false
        datePeremptionViews.spinnerAnnee.setSelection(2.coerceAtMost(adapterAnnee.count - 1))
    }

    private fun applyPeremption(peremptionDate: String?)
    {
        val date = peremptionDate?.trim().orEmpty()
        if (date.isEmpty() || date == EMPTY_DATE)
        {
            datePeremptionViews.layout.visibility = View.INVISIBLE
            return
        }

        datePeremptionViews.layout.visibility = View.VISIBLE
        val parts = date.split("-")
        if (parts.size != 3) { return }

        val mois = (parts[1].toIntOrNull() ?: 1) - 1
        datePeremptionViews.spinnerMois.setSelection(mois.coerceIn(0, 11))
        val annee = parts[0]
        val adapter = datePeremptionViews.spinnerAnnee.adapter
        val positionAnnee = (0 until adapter.count).indexOfFirst { adapter.getItem(it).toString() == annee }
        if (positionAnnee >= 0) { datePeremptionViews.spinnerAnnee.setSelection(positionAnnee) }
    }

    private fun getSelectedPeremptionDate(): String
    {
        val moisIndex = datePeremptionViews.spinnerMois.selectedItemPosition + 1
        val annee = datePeremptionViews.spinnerAnnee.selectedItem.toString()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, annee.toInt())
        calendar.set(Calendar.MONTH, moisIndex - 1)
        val jour = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        return "$annee-${String.format("%02d", moisIndex)}-$jour"
    }

    private fun getMaxQuantitePourStock(stock: Stock_Lot_Emplacement_Light, ligneRetournee: Retour_Ligne?): Int
    {
        val quantiteRestante = getQuantiteRestanteSansSelection() + (ligneRetournee?.qte_Retourner?.toInt() ?: 0)
        return min(stock.qte.toInt(), quantiteRestante).coerceAtLeast(0)
    }

    private fun getQuantiteRestanteSansSelection(): Int
    {
        var quantiteRestante = retourLigneBase.qte_Demander.toInt()
        getRetourLignesRetournees().forEach { quantiteRestante -= it.qte_Retourner.toInt() }
        return quantiteRestante.coerceAtLeast(0)
    }

    private fun getRetourLignesRetournees(): List<Retour_Ligne>
    {
        return Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourCourant, retourLigneBase.code_produit)
    }

    private fun getRetourLigneForStock(stock: Stock_Lot_Emplacement_Light, excludedUid: Int? = null): Retour_Ligne?
    {
        return getRetourLignesRetournees().firstOrNull { sameLot(stock, it) && (excludedUid == null || it._UID != excludedUid) }
    }

    private fun sameLot(stock: Stock_Lot_Emplacement_Light, ligne: Retour_Ligne): Boolean
    {
        return stock.lot.orEmpty().trim() == ligne.lot_Retourner.orEmpty().trim() &&
            stock.peremptionDate.orEmpty().trim() == ligne.peremptionDate.orEmpty().trim() &&
            stock.serie.orEmpty().trim() == ligne.serie_Retourner.orEmpty().trim()
    }

    private fun getLotLabel(stock: Stock_Lot_Emplacement_Light): String
    {
        val peremption = stock.peremptionDate?.takeIf { it.isNotBlank() && it != EMPTY_DATE }?.let { " - ${formatPeremption(it)}" }.orEmpty()
        val serie = stock.serie?.takeIf { it.isNotBlank() && it != "null" }?.let { " - $it" }.orEmpty()
        return "${stock.lot.orEmpty()}$peremption$serie"
    }

    private fun formatPeremption(date: String): String
    {
        val parts = date.split("-")
        return if (parts.size == 3) { "${parts[1]}/${parts[0].takeLast(2)}" } else date
    }

    private fun getPasQuantite(): Int
    {
        var pas = produit.cond_distrib.toInt()
        if (pas == 0 || pas >= maxQuantite) { pas = 1 }
        return max(1, pas)
    }

    private fun selectAllQuantityText() { quantiteCompteeET.post { quantiteCompteeET.selectAll() } }

    private fun getListeMoisDatePicker(): Array<String> = Array(12) { index -> String.format("%02d", index + 1) }

    private fun getListeAnneeDatePicker(): Array<String>
    {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = currentYear - YEAR_RANGE_BEFORE
        val endYear = currentYear + YEAR_RANGE_AFTER
        return Array((endYear - startYear) + 1) { index -> (startYear + index).toString() }
    }
}
