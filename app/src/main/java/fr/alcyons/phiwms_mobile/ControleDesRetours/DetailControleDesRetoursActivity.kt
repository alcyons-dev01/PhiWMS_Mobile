package fr.alcyons.phiwms_mobile.ControleDesRetours

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerRetourActivity
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne
import fr.alcyons.phiwms_mobile.Classes.Depot
import fr.alcyons.phiwms_mobile.Classes.Retour
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light
import fr.alcyons.phiwms_mobile.ControleDesRetours.Fragment.ControleRetourLignesFragment
import fr.alcyons.phiwms_mobile.ControleDesRetours.Fragment.DetailControleDesRetoursFragment
import fr.alcyons.phiwms_mobile.Fragment.RechercheFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerInputFragment
import fr.alcyons.phiwms_mobile.Interfaces.RechercheAdjustable
import fr.alcyons.phiwms_mobile.Interfaces.ScanDebounce
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity
import fr.alcyons.phiwms_mobile.Services.ServiceControleRetoursActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

class DetailControleDesRetoursActivity : ServiceAvecConnexionActivity(),
    ControleRetourLignesFragment.OnElementSelectionneListener,
    RechercheFragment.OnElementRechercheListener,
    RechercheAdjustable
{
    companion object
    {
        private const val ANIMATION_DURATION_MS = 300L
        private const val SCANNER_HEIGHT_DP = 300
        private const val DETAIL_FALLBACK_TRANSLATION_Y = 600f
        private const val RETOUR_SELECTIONNE_ID_ARG = "retourSelectionneID"
        private const val SEARCH_DOMAIN_CONTROLE_RETOURS = "controleDesRetours"
        private const val SCAN_DEBOUNCE_MS = 750L
    }

    private var retourSelectionne: Retour? = null
    private var depot: Depot? = null
    private var listeRetourLigne: MutableList<Retour_Ligne> = ArrayList()
    private var listelot: MutableList<String?> = ArrayList()
    private var context: Context? = null
    private var pm: PackageManager? = null
    private var triChoisi: String? = null
    private var premierPassage = false
    private var hauteurListeFragment = 0

    private var optionTri: Spinner? = null
    private var lancerScan: LinearLayout? = null
    private var lancerRecherche: LinearLayout? = null
    private var aControlerLL: LinearLayout? = null
    private var controleLL: LinearLayout? = null
    private var actionButton: AppCompatButton? = null
    private var scannerContainer: FragmentContainerView? = null
    private var rechercheContainer: FragmentContainerView? = null
    private var aControlerContainer: FragmentContainerView? = null
    private var controleContainer: FragmentContainerView? = null
    private var detailContainer: FragmentContainerView? = null
    private var textChercherTV: TextView? = null
    private var searchInputET: EditText? = null
    private var effacerRechercheIV: ImageView? = null
    private var searchTextWatcher: TextWatcher? = null

    private var scannerFragment: Fragment? = null
    private var rechercheFragment: RechercheFragment? = null
    private var aControlerFragment: ControleRetourLignesFragment? = null
    private var controleFragment: ControleRetourLignesFragment? = null
    private var detailFragment: DetailControleDesRetoursFragment? = null

    private var isScannerOpen = false
    private var isScannerClosing = false
    private var isSearchOpen = false
    private var isAControlerOpen = false
    private var isControleOpen = false
    private var isDetailOpen = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_controle_retours)
        context = this
        pm = packageManager
        premierPassage = true

        initializeData()
        bindViews()
        setupUi()
        setupResponsiveListHeight()
        setupListeners()
        setupOnBackPressedCallback()
        openDefaultFragment()
    }

    private fun initializeData()
    {
        val retourId = requireNotNull(intent.extras).getInt(RETOUR_SELECTIONNE_ID_ARG)
        retourSelectionne = RetourOpenHelper.getRetourByID(db, retourId)
        depot = DepotOpenHelper.getDepotParReference(db, retourSelectionne?.ref_Depot_Origine)
        listeRetourLigne = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retourSelectionne).toMutableList()
    }

    private fun bindViews()
    {
        optionTri = findViewById(R.id.optionTri)
        lancerScan = findViewById(R.id.lancerScan)
        lancerRecherche = findViewById(R.id.lancerRecherhe)
        aControlerLL = findViewById(R.id.aControler_LL)
        controleLL = findViewById(R.id.controle_LL)
        actionButton = findViewById(R.id.boutonAction)
        scannerContainer = findViewById(R.id.scannerContainer)
        rechercheContainer = findViewById(R.id.rechercheContainer)
        aControlerContainer = findViewById(R.id.referenceAControlerContainer)
        controleContainer = findViewById(R.id.referenceControleContainer)
        detailContainer = findViewById(R.id.detailContainer)
        textChercherTV = findViewById(R.id.textChercher_TV)
        searchInputET = findViewById(R.id.searchInput_ET)
        effacerRechercheIV = findViewById(R.id.effacerRecherche_IV)
    }

    private fun setupUi()
    {
        findViewById<TextView>(R.id.numero).text = "Numéro retour : #" + retourSelectionne?.numero.orEmpty()
        findViewById<TextView>(R.id.motif).text = "Motif : " + retourSelectionne?.motif.orEmpty()

        triChoisi = ParametreUtilisateurOpenHelper.getChoixTriRetourLigne(db)
        if (triChoisi == null)
        {
            ParametreUtilisateurOpenHelper.mettreAJourTriRetourLigne(db, 0, "Designation")
            triChoisi = ParametreUtilisateurOpenHelper.getChoixTriRetourLigne(db)
        }
    }

    private fun setupResponsiveListHeight()
    {
        val frameContenu = findViewById<RelativeLayout>(R.id.frameLayout)
        frameContenu.post {
            hauteurListeFragment = calculateResponsiveListHeight(frameContenu.height)
            resizeOpenListContainers()
        }
    }

    private fun ensureListHeight(): Int
    {
        if (hauteurListeFragment > 0) { return hauteurListeFragment }
        val frameContenu = findViewById<RelativeLayout>(R.id.frameLayout)
        if (frameContenu.height > 0)
        {
            hauteurListeFragment = calculateResponsiveListHeight(frameContenu.height)
        }
        return hauteurListeFragment
    }

    private fun calculateResponsiveListHeight(frameHeight: Int): Int
    {
        val widthDp = resources.displayMetrics.run { widthPixels / density }
        var frameheightModifiable: Float = frameHeight.toFloat()
        val SCANNER_HEIGHT_PX = SCANNER_HEIGHT_DP * resources.displayMetrics.density
        frameheightModifiable += SCANNER_HEIGHT_PX
        return (frameheightModifiable * when {
            widthDp < 400 -> 0.35
            widthDp < 600 -> 0.65
            else -> 0.75
        }).toInt()
    }

    private fun setupListeners()
    {
        optionTri?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            private var isFirstSelection = true

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long)
            {
                if (isFirstSelection)
                {
                    isFirstSelection = false
                    return
                }

                if (parent.getChildAt(0) != null) { parent.getChildAt(0).visibility = View.INVISIBLE }
                triChoisi = optionTri?.getItemAtPosition(position).toString()
                ParametreUtilisateurOpenHelper.mettreAJourTriPreparation(db, 0, triChoisi)
                applySortAndRefresh()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        val openScannerClickListener = View.OnClickListener {
            if (isScannerOpen) closeScanner()
            else
            {
                closeOpenedFragments()
                openScanner()
            }
        }
        lancerScan?.setOnClickListener(openScannerClickListener)
        lancerRecherche?.setOnClickListener {
            if (isSearchOpen) closeSearch()
            else
            {
                closeOpenedFragments()
                showSearchInput()
            }
        }
        effacerRechercheIV?.setOnClickListener {
            searchInputET?.setText("")
            closeSearch()
        }
        aControlerLL?.setOnClickListener {
            if (isAControlerOpen) closeAControler()
            else
            {
                closeOpenedFragments()
                openAControler()
            }
        }
        controleLL?.setOnClickListener {
            if (isControleOpen) closeControle()
            else
            {
                closeOpenedFragments()
                openControle()
            }
        }
        actionButton?.setOnClickListener { demanderValidationControle() }
    }

    public override fun onResume()
    {
        super.onResume()
        if (statutConnexion && premierPassage) { chargerDetailRetourDepuisServeur() }
        else { refreshRetourData() }
    }

    private fun chargerDetailRetourDepuisServeur()
    {
        if (swipeRefreshLayout == null || !swipeRefreshLayout.isRefreshing) { afficherSpinner(this, LayoutInflater.from(this)) }

        val requestQueue = Volley.newRequestQueue(this)
        val urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteControleRetours + "/" + (retourSelectionne ?: return)._UID
        val obreq: JsonObjectRequest = object : JsonObjectRequest(Method.GET, urlRequete, null, Response.Listener { response: JSONObject? ->
            try
            {
                val nbResultat = response?.getInt("resultCount") ?: return@Listener
                if (nbResultat == 0)
                {
                    val erreur = response.getString("erreur")
                    if (erreur == getString(R.string.tokenInvalide)) { Alerte.afficherAlerteInformation(this, layoutInflater, "Alerte", "Votre session est invalide, veuillez vous reconnecter.", true, false) }
                    else if (erreur == getString(R.string.tokenExpire)) { Alerte.afficherAlerteInformation(this, layoutInflater, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", true, false) }
                    else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) { Alerte.afficherAlerteInformation(this, layoutInflater, "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Contrôle des retours", true, false) }
                }
                else
                {
                    val retourLignesJSONArray = response.getJSONArray("PH_Retour_Lignes")
                    for (k in 0 until retourLignesJSONArray.length())
                    {
                        val retourLigneJSONObject = retourLignesJSONArray.getJSONObject(k)
                        val stockLotEmplacementsJSONArray = retourLigneJSONObject.getJSONArray("ph_stock_lot_emplacements")
                        for (y in 0 until stockLotEmplacementsJSONArray.length())
                        {
                            val stockLotEmplacementLight = Stock_Lot_Emplacement_Light(stockLotEmplacementsJSONArray.getJSONObject(y))
                            val stockLotEmplacementBdd = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stockLotEmplacementLight._UID)
                            if (stockLotEmplacementBdd == null)
                            { if (stockLotEmplacementLight.qte >= 0.0) { Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stockLotEmplacementLight) } }
                            else if (stockLotEmplacementBdd.qte != stockLotEmplacementLight.qte)
                            { Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacementLight) }
                            listelot.add(stockLotEmplacementLight.lot)
                        }
                    }
                }

                premierPassage = false
                refreshRetourData()
                arreterSpinner()
            }
            catch (e: JSONException) { e.printStackTrace() }
        }, Response.ErrorListener { error: VolleyError? ->
            Log.e("Volley CdR", error.toString())
            Alerte.afficherAlerteInformation(this, layoutInflater, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Contrôle des retours)", true, false)
        })
        {
            override fun getHeaders(): MutableMap<String?, String?>
            {
                val headers: MutableMap<String?, String?> = HashMap()
                headers["Authorization"] = utilisateurConnecte.token
                return headers
            }
        }
        obreq.retryPolicy = retryPolicy
        requestQueue.add(obreq)
    }

    private fun refreshRetourData()
    {
        listeRetourLigne = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retourSelectionne).toMutableList()
        applySortAndRefresh()
    }

    private fun applySortAndRefresh()
    {
        when (triChoisi)
        {
            "Designation" -> listeRetourLigne.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.produit_Designation ?: "" })
            "Place" -> listeRetourLigne.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { getEmplacementTri(it) })
            "Catégorie" -> listeRetourLigne.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ProduitOpenHelper.getProduitByID(db, it.code_produit)?.categorie ?: "" })
            "Poids" -> listeRetourLigne.sortBy { ProduitOpenHelper.getProduitByID(db, it.code_produit)?.poids ?: 0.0 }
            else -> listeRetourLigne.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.produit_Designation ?: "" })
        }
        updateFragments()
    }

    private fun getEmplacementTri(retourLigne: Retour_Ligne): String
    {
        val emplacementOrigine = retourLigne.emplacementOrigine
        if (!emplacementOrigine.isNullOrEmpty()) { return emplacementOrigine }
        return ProduitOpenHelper.getProduitByID(db, retourLigne.code_produit)?.emplacement_PUI_Defaut ?: ""
    }

    private fun updateFragments()
    {
        val aControler = getRetourLignesAControler()
        val controlees = getRetourLignesControlees()
        findViewById<TextView>(R.id.nbReferenceAControler_TV).text = aControler.size.toString()
        findViewById<TextView>(R.id.nbReferenceControle_TV).text = controlees.size.toString()

        aControlerFragment?.updateList(ArrayList(aControler), retourSelectionne ?: return)
        controleFragment?.updateList(ArrayList(controlees), retourSelectionne ?: return)
        if (isAControlerOpen && aControler.isEmpty()) { closeAControler() }
        if (isControleOpen && controlees.isEmpty()) { closeControle() }
        if (!isScannerOpen && !isSearchOpen && !isAControlerOpen && !isControleOpen && !isDetailOpen)
        {
            if (aControler.isNotEmpty()) { openAControler(aControler) }
            else if (controlees.isNotEmpty()) { openControle(controlees) }
        }
    }

    override fun onElementRechercher(element: Int) { scrollToItemOrDisplayAlert(element) }

    override fun ajusterHauteurRecherche(hauteur: Int)
    {
        (rechercheContainer ?: return).layoutParams = ((rechercheContainer ?: return).layoutParams as LinearLayout.LayoutParams).also { it.height = if (hauteur == 0) 0 else LinearLayout.LayoutParams.WRAP_CONTENT }
        (rechercheContainer ?: return).requestLayout()
    }

    private fun scrollToItemOrDisplayAlert(idProduit: Int)
    {
        val lignesAControler = getRetourLignesAControler().filter { retourLigne -> retourLigne.code_produit == idProduit }
        val lignesControlees = getRetourLignesControlees().filter { retourLigne -> retourLigne.code_produit == idProduit }

        if (lignesAControler.isEmpty() && lignesControlees.isEmpty())
        {
            Alerte.afficherAlerteInformation(this, layoutInflater, "Produit non trouvé", "Ce produit n'est pas dans la liste du contrôle des retours", false, false)
            return
        }

        closeOpenedFragments()
        if (lignesAControler.isNotEmpty()) { openAControler(lignesAControler) }
        if (lignesControlees.isNotEmpty()) { openControle(lignesControlees) }
        if (isSearchOpen) { closeSearch() }
    }

    private fun openScanner()
    {
        hauteurListeFragment = 0
        scannerContainer?.let { container ->
            animateFixedHeightContainerOpen(container, SCANNER_HEIGHT_DP)
            val fragment = createScannerFragment().also { scannerFragment = it }
            setupScannerFragmentCallbacks(fragment)
            supportFragmentManager.beginTransaction().replace(R.id.scannerContainer, fragment).commit()
        }
        isScannerOpen = true
    }

    private fun openDefaultFragment()
    {
        scannerContainer?.post {
            if (!isScannerOpen && !isSearchOpen && !isAControlerOpen && !isControleOpen && !isDetailOpen)
            {
                openScanner()
            }
        }
    }

    private fun closeScanner()
    {
        isScannerClosing = true
        closeContainer(scannerContainer) {
            scannerFragment?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
            scannerFragment = null
            hauteurListeFragment = 0
            isScannerClosing = false
        }
        isScannerOpen = false
    }

    private fun createScannerFragment(): Fragment
    {
        return when
        {
            isProfessionalScanner() -> ScannerInputFragment()
            hasCamera() -> ScannerFragment()
            else -> ScannerInputFragment()
        }
    }

    private fun setupScannerFragmentCallbacks(fragment: Fragment)
    {
        when (fragment)
        {
            is ScannerInputFragment -> {
                fragment.onCodeScanned = { code -> handleScannedCode(code) }
                fragment.onCloseRequested = { closeScanner() }
            }
            is ScannerFragment -> {
                fragment.onCodeScanned = { code -> handleScannedCode(code) }
                fragment.onCloseRequested = { closeScanner() }
            }
        }
        (fragment as ScanDebounce).setScanDebounce(SCAN_DEBOUNCE_MS)
    }

    private fun handleScannedCode(scannedCode: String)
    {
        val resultDecoupage = GestionCodeScanne.decoupageCode(scannedCode)
        val codeIdentification = resultDecoupage["code"] ?: ""

        if (codeIdentification.isEmpty())
        {
            Alerte.afficherAlerteInformation(this, layoutInflater, "Code non reconnu", "Le code scanné n'a pas pu être analysé: $scannedCode", false, false)
            return
        }

        val produits = ProduitOpenHelper.getProduitsByIdentification(db, codeIdentification)
        if (produits.isEmpty())
        {
            Alerte.afficherAlerteInformation(this, layoutInflater, "Produit non trouvé", "Aucun produit trouvé pour le code scanné: $codeIdentification", false, false)
            return
        }

        scrollToItemOrDisplayAlert(produits.first().iD_produit)
    }

    private fun isProfessionalScanner(): Boolean
    {
        val manufacturer = Build.MANUFACTURER.uppercase()
        val model = Build.MODEL.uppercase()
        return manufacturer.contains("ZEBRA") ||
                manufacturer.contains("HONEYWELL") ||
                model.contains("TC") ||
                model.contains("MC") ||
                model.contains("CK") ||
                model.contains("CT") ||
                model.contains("CN")
    }

    private fun hasCamera(): Boolean { return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) }

    private fun showSearchInput()
    {
        isSearchOpen = true

        val searchInput = searchInputET ?: return
        findViewById<ImageView>(R.id.chevronRecherche).visibility = View.GONE
        textChercherTV?.visibility = View.GONE
        searchInput.visibility = View.VISIBLE
        effacerRechercheIV?.visibility = View.VISIBLE
        searchInput.requestFocus()

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchInputET, InputMethodManager.SHOW_IMPLICIT)
        attachSearchWatcher(searchInput)
    }

    private fun attachSearchWatcher(searchInput: EditText)
    {
        searchTextWatcher?.let(searchInput::removeTextChangedListener)
        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?)
            {
                val query = s.toString().trim()
                if (query.isEmpty())
                {
                    rechercheFragment?.viderListe()
                    return
                }

                openSearch()
                rechercheFragment?.lancerRecherche(query, SEARCH_DOMAIN_CONTROLE_RETOURS, (retourSelectionne ?: return)._UID.toString())
            }
        }
        searchInput.addTextChangedListener(searchTextWatcher)
    }

    private fun openSearch()
    {
        findViewById<NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = false
        rechercheContainer?.let { container ->
            container.layoutParams = (container.layoutParams as LinearLayout.LayoutParams).also {
                it.height = LinearLayout.LayoutParams.WRAP_CONTENT
                it.weight = 0f
            }
            container.visibility = View.VISIBLE
            container.translationY = -resources.displayMetrics.heightPixels.toFloat()
            container.animate().translationY(0f).setDuration(ANIMATION_DURATION_MS).start()
        }

        if (rechercheFragment == null)
        {
            val frag = RechercheFragment().also { rechercheFragment = it }
            supportFragmentManager.beginTransaction().replace(R.id.rechercheContainer, frag).commitNow()
        }

        isSearchOpen = true
    }

    private fun closeSearch()
    {
        hideSearchInput()
        findViewById<NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = true
        closeContainer(rechercheContainer) {
            rechercheFragment?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
            rechercheFragment = null
        }
        isSearchOpen = false
    }

    private fun hideSearchInput()
    {
        findViewById<ImageView>(R.id.chevronRecherche).visibility = View.VISIBLE
        textChercherTV?.visibility = View.VISIBLE
        searchInputET?.visibility = View.GONE
        effacerRechercheIV?.visibility = View.GONE
        searchInputET?.text?.clear()

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow((searchInputET ?: return).windowToken, 0)
    }

    private fun getRetourLignesAControler(): List<Retour_Ligne>
    {
        return listeRetourLigne.filter { ligne -> getQuantiteRetournee(ligne) < ligne.qte_Demander.toInt() }
    }

    private fun getRetourLignesControlees(): List<Retour_Ligne>
    {
        return Retour_LigneOpenHelper.getAllRetourLignesNegByRetour(db, retourSelectionne).filter { ligne -> ligne.qte_Retourner > 0 }
    }

    private fun getQuantiteRetournee(ligne: Retour_Ligne): Int
    {
        var qteRetourner = 0
        val retourLigneNegList = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourSelectionne, ligne.code_produit)
        for (negTemp in retourLigneNegList) { qteRetourner += negTemp.qte_Retourner.toInt() }
        return qteRetourner
    }

    override fun onElementSelectionne(element: Retour_Ligne) { openDetailFragment(element) }

    private fun openAControler(lignes: List<Retour_Ligne> = getRetourLignesAControler())
    {
        if (lignes.isEmpty())
        {
            if (isAControlerOpen) { closeAControler() }
            return
        }
        val retour = retourSelectionne ?: return
        aControlerContainer?.let { container ->
            openListContainer(container)
            val frag = ControleRetourLignesFragment.newInstance(ArrayList(lignes), retour).also { aControlerFragment = it }
            supportFragmentManager.beginTransaction().replace(R.id.referenceAControlerContainer, frag).commitNow()
            scrollTo(container)
        }
        isAControlerOpen = true
    }

    private fun closeAControler()
    {
        closeContainer(aControlerContainer) {
            aControlerFragment?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
            aControlerFragment = null
        }
        isAControlerOpen = false
    }

    private fun openControle(lignes: List<Retour_Ligne> = getRetourLignesControlees())
    {
        if (lignes.isEmpty()) { return }
        val retour = retourSelectionne ?: return
        controleContainer?.let { container ->
            openListContainer(container)
            val frag = ControleRetourLignesFragment.newInstance(ArrayList(lignes), retour).also { controleFragment = it }
            supportFragmentManager.beginTransaction().replace(R.id.referenceControleContainer, frag).commitNow()
            scrollTo(container)
        }
        isControleOpen = true
    }

    private fun closeControle()
    {
        closeContainer(controleContainer) {
            controleFragment?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
            controleFragment = null
        }
        isControleOpen = false
    }

    private fun openDetailFragment(retourLigne: Retour_Ligne)
    {
        closeOpenedFragments()
        detailContainer?.let { container ->
            val fragment = DetailControleDesRetoursFragment.newInstance(retourLigne).also { detailFragment = it }
            fragment.onFermer = { closeDetailFragment() }
            fragment.onValider = {
                closeDetailFragment()
                refreshRetourData()
                openAControler()
            }
            supportFragmentManager.beginTransaction().replace(R.id.detailContainer, fragment).commitNow()
            container.visibility = View.VISIBLE
            container.translationY = container.height.toFloat().takeIf { it > 0f } ?: DETAIL_FALLBACK_TRANSLATION_Y
            container.animate().translationY(0f).setDuration(ANIMATION_DURATION_MS).start()
        }
        isDetailOpen = true
    }

    private fun closeDetailFragment()
    {
        val container = detailContainer ?: return
        container.animate().translationY(container.height.toFloat().takeIf { it > 0f } ?: DETAIL_FALLBACK_TRANSLATION_Y).setDuration(ANIMATION_DURATION_MS).withEndAction {
            container.visibility = View.GONE
            detailFragment?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
            detailFragment = null
        }.start()
        isDetailOpen = false
    }

    private fun openContainer(container: FragmentContainerView)
    {
        container.layoutParams = (container.layoutParams as LinearLayout.LayoutParams).also {
            it.height = LinearLayout.LayoutParams.WRAP_CONTENT
            it.weight = 0f
        }
        container.visibility = View.VISIBLE
        container.translationY = -resources.displayMetrics.heightPixels.toFloat()
        container.animate().translationY(0f).setDuration(ANIMATION_DURATION_MS).start()
    }

    private fun openListContainer(container: FragmentContainerView)
    {
        if (isScannerClosing)
        {
            container.postDelayed({ openListContainer(container) }, ANIMATION_DURATION_MS)
            return
        }

        hauteurListeFragment = 0
        if (ensureListHeight() <= 0)
        {
            val frameContenu = findViewById<RelativeLayout>(R.id.frameLayout)
            frameContenu.post {
                hauteurListeFragment = calculateResponsiveListHeight(frameContenu.height)
                applyListContainerHeight(container)
            }
        }
        else
        {
            applyListContainerHeight(container)
        }
        container.visibility = View.VISIBLE
        container.translationY = 0f
        container.alpha = 0f
        container.animate().alpha(1f).setDuration(200L).start()
    }

    private fun applyListContainerHeight(container: FragmentContainerView)
    {
        container.layoutParams = (container.layoutParams as LinearLayout.LayoutParams).also {
            it.height = hauteurListeFragment
            it.weight = 0f
        }
    }

    private fun resizeOpenListContainers()
    {
        if (isAControlerOpen) { aControlerContainer?.let { applyListContainerHeight(it) } }
        if (isControleOpen) { controleContainer?.let { applyListContainerHeight(it) } }
    }

    private fun animateFixedHeightContainerOpen(container: FragmentContainerView, heightDp: Int)
    {
        container.layoutParams = (container.layoutParams as LinearLayout.LayoutParams).also {
            it.height = (heightDp * resources.displayMetrics.density).toInt()
            it.weight = 0f
        }
        container.visibility = View.VISIBLE
        container.translationY = -resources.displayMetrics.heightPixels.toFloat()
        container.animate().translationY(0f).setDuration(ANIMATION_DURATION_MS).start()
    }

    private fun closeContainer(container: FragmentContainerView?, onComplete: () -> Unit)
    {
        (container ?: return).animate().translationY(-container.height.toFloat()).setDuration(ANIMATION_DURATION_MS).withEndAction {
            container.visibility = View.GONE
            container.layoutParams = (container.layoutParams as LinearLayout.LayoutParams).also { it.height = 0 }
            onComplete()
        }.start()
    }

    private fun scrollTo(container: View)
    {
        val scrollView = findViewById<NestedScrollView?>(R.id.scrollView)
        scrollView?.post { scrollView.smoothScrollTo(0, container.top) }
    }

    private fun closeOpenedFragments()
    {
        if (isScannerOpen) closeScanner()
        if (isSearchOpen) closeSearch()
        if (isAControlerOpen) closeAControler()
        if (isControleOpen) closeControle()
        if (isDetailOpen) closeDetailFragment()
    }

    private fun lancerScanner()
    {
        val intent = Intent(this, ScannerRetourActivity::class.java)
        val bundle = super.getBundle()
        bundle.putString("contexte", R.string.scannerContextMultipleNewControleRetour.toString())
        bundle.putBoolean("isBoutonSuppressionExistant", true)
        bundle.putSerializable("RetourCourant", retourSelectionne)
        bundle.putSerializable("DepotOrigine", depot)
        bundle.putStringArrayList("liste_lot", listelot as ArrayList<String?>)
        bundle.putSerializable("ListeRetourLigne", listeRetourLigne as Serializable)
        bundle.putBoolean("EmplacementUF", true)
        intent.putExtras(bundle)
        startActivityForResult(intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH) { refreshRetourData() }
    }

    private fun demanderValidationControle()
    {
        val listeBaseTemp = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retourSelectionne)
        var retourComplet = true
        for (baseTemp in listeBaseTemp)
        {
            val qteARetourner = baseTemp.qte_Demander.toInt()
            if (qteARetourner != getQuantiteRetournee(baseTemp))
            {
                retourComplet = false
                break
            }
        }

        if (retourComplet) onMenuSaveClick()
        else Alerte.afficherAlerteConfirmation(this, layoutInflater, getBundle(), "Toutes les références n'ont pas été retournées, souhaitez vous continuer ?", false, true, this)
    }

    fun onMenuSaveClick()
    {
        val retourLigneBase = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retourSelectionne)
        for (retourLigneTemp in retourLigneBase)
        {
            Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigneTemp)
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigneTemp.phiMR4UUID, retourLigneTemp._UID, DBOpenHelper.ActionsEAS.SUPPR)
        }

        val actionId = generateNegativeRandomId()
        val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val actionUtilisateur = ActionUtilisateur(actionId, utilisateurConnecte.id, dateString, serviceActuel.id, utilisateurConnecte.etablissementId, "En attente", (retourSelectionne ?: return)._UID, "", "Controle des retours")
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, actionUtilisateur)
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, actionUtilisateur.phiMR4UUID, actionUtilisateur.id, DBOpenHelper.ActionsEAS.AJOUT)

        val retourLignesListe = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retourSelectionne)
        for (retourLigne in retourLignesListe)
        {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigne.phiMR4UUID, retourLigne._UID, DBOpenHelper.ActionsEAS.AJOUT)
            val actionLigne = ActionUtilisateur_Ligne(generateNegativeRandomId(), actionUtilisateur.id, "Retour Ligne", retourLigne._UID, "", 0, retourLigne.qte_Retourner.toInt(), retourLigne.produit_Designation)
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionLigne)
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionLigne.phiMR4UUID, actionLigne.id, DBOpenHelper.ActionsEAS.AJOUT)
        }

        val listSerialisation = PH_SerialisationOpenHelper.getAllPH_SerialisationByMvtId(db, (retourSelectionne ?: return)._UID.toString())
        for (serialisationCourante in listSerialisation)
        { ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, serialisationCourante.phiMR4UUID, serialisationCourante.get_UID(), DBOpenHelper.ActionsEAS.AJOUT) }

        val retour = retourSelectionne ?: return
        retour.en_Attente_de = getString(R.string.RepriseEffectuee)
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        retour.date_retour = dateFormat.format(Date())
        retour.date_Validation = dateFormat.format(Date())

        val rowID = RetourOpenHelper.mettreAJourRetour(db, retour)
        if (rowID != -1L) { ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retour.phiMR4UUID, retour._UID, DBOpenHelper.ActionsEAS.MAJ) }

        Toast.makeText(this, "Retour contrôlé", Toast.LENGTH_SHORT).show()
        ElementASynchroniserOpenHelper.toutSynchroniser(this, db, utilisateurConnecte, true)
        val intent = Intent(this, ServiceControleRetoursActivity::class.java)
        intent.putExtras(super.getBundle())
        startActivity(intent)
        finish()
    }

    private fun generateNegativeRandomId(): Int
    {
        var id = Random().nextInt()
        if (id > 0) { id *= -1 }
        if (id == 0) { id = -1 }
        return id
    }

    override fun confirmationService() { onMenuSaveClick() }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean
    {
        val item: MenuItem? = menu.findItem(R.id.menuSaveCircle)
        item?.isVisible = false
        return true
    }

    private fun setupOnBackPressedCallback()
    {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed()
            {
                if (isDetailOpen) closeDetailFragment()
                else
                {
                    val intent = Intent(this@DetailControleDesRetoursActivity, ServiceControleRetoursActivity::class.java)
                    val bundle = super@DetailControleDesRetoursActivity.getBundle()
                    bundle.putString("Etat", "Retour")
                    intent.putExtras(bundle)
                    startActivity(intent)
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}
