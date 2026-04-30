package fr.alcyons.phiwms_mobile.PreparationPUFetPAD;

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockUtilisesOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Depot
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Classes.StockUtilises
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light
import fr.alcyons.phiwms_mobile.Fragment.RechercheFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerInputFragment
import fr.alcyons.phiwms_mobile.Interfaces.RechercheAdjustable
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Adapter.DetailPreparationAdapter
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Fragment.APreparerFragment
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Fragment.DetailFragment
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.Fragment.PreparerFragment
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity
import fr.alcyons.phiwms_mobile.Services.ServicePreparationPadActivity
import fr.alcyons.phiwms_mobile.Services.ServicePreparationPufActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.lang.Double
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparator
import kotlin.Deprecated
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.also
import kotlin.apply
import kotlin.compareTo
import kotlin.let
import kotlin.plus
import kotlin.toString

class DetailPreparationV2 : ServiceAvecConnexionActivity(),
    RechercheFragment.OnElementRechercheListener, APreparerFragment.OnElementSelectionneListener, PreparerFragment.OnElementSelectionneListener, RechercheAdjustable {

    private lateinit var preparationCourante: PH_Preparation
    private lateinit var context: Context
    private lateinit var scannerContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var rechercheContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var referenceAPreparerContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var referencePreparerContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var detailContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var lancerScan: LinearLayout
    private lateinit var lancerRecherhe: LinearLayout
    private lateinit var aPreparer_LL: LinearLayout
    private lateinit var preparer_LL: LinearLayout
    private lateinit var btnValiderPreparation_LL: LinearLayout
    private lateinit var btnValiderPreparation_CV: CardView
    private var adapter: DetailPreparationAdapter? = null
    private var scannerFragment: Fragment? = null
    private var rechercheFragment: RechercheFragment? = null
    private var aPreparerFragment: APreparerFragment? = null
    private var preparerFragment: PreparerFragment? = null
    private var detailFragment: DetailFragment? = null
    private var scannerVisible = false
    private var rechercheVisible = false
    private var aPreparerVisible = false
    private var preparerVisible = false
    private var detailVisible = false
    private var scannerProcessing = false
    private var alerteVisible = false
    private var positionSelectionnee = -1
    private var hauteurDetailFragment = 0

    private lateinit var textChercher_TV: TextView
    private lateinit var searchInput_ET: EditText
    private lateinit var effacerRecherche_IV: ImageView
    private lateinit var depotOrigine : Depot
    private lateinit var phPreparationLignes : ArrayList<PH_Preparation_Ligne>
    var body = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_preparation_module)
        context = this

        // Récupération des données de l'intent
        preparationCourante = PH_PreparationOpenHelper.getPH_PreparationByID(
            db, intent.extras!!.getInt("ph_preparationUID_Selectionne")
        )

        depotOrigine = DepotOpenHelper.getDepotPUI(db)
        phPreparationLignes =
            PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(
                db,
                preparationCourante
            ) as ArrayList<PH_Preparation_Ligne>

        phPreparationLignes.sortWith(Comparator { o1: PH_Preparation_Ligne?, o2: PH_Preparation_Ligne? ->
            o2!!.produitPoids.compareTo(o1!!.produitPoids)
        })

        // Binding des vues
        findViewById<TextView>(R.id.preparationNumero).text = preparationCourante.uid.toString()

        scannerContainer = findViewById(R.id.scannerContainer)
        rechercheContainer = findViewById(R.id.rechercheContainer)
        referenceAPreparerContainer = findViewById(R.id.referenceAPreparerContainer)
        referencePreparerContainer = findViewById(R.id.referencePreparerContainer)
        detailContainer = findViewById(R.id.detailContainer)

        lancerScan = findViewById(R.id.lancerScan)
        lancerRecherhe = findViewById(R.id.lancerRecherhe)
        aPreparer_LL = findViewById(R.id.aPreparer_LL)
        preparer_LL = findViewById(R.id.preparer_LL)
        btnValiderPreparation_LL = findViewById(R.id.btnValiderPreparation_LL)
        btnValiderPreparation_CV = findViewById(R.id.btnValiderPreparation_CV)
        textChercher_TV = findViewById(R.id.textChercher_TV)
        searchInput_ET = findViewById(R.id.searchInput_ET)
        effacerRecherche_IV = findViewById(R.id.effacerRecherche_IV)

        // Dans onCreate(), après setContentView
        val frameContenu = findViewById<RelativeLayout>(R.id.frameLayout)
        frameContenu.post {
            hauteurDetailFragment = frameContenu.height / 2
        }

        lancerScan.setOnClickListener {
            if (scannerVisible) {
                fermerScanner()
            } else {
                fermerFragment()
                ouvrirScanner()
            }
        }

        lancerRecherhe.setOnClickListener {
            if (rechercheVisible) {
                fermerRecherche()
            } else {
                fermerFragment()
                afficherSearchInput()
            }
        }

        effacerRecherche_IV.setOnClickListener {
            searchInput_ET.text.clear()
            fermerRecherche()
        }

        aPreparer_LL.setOnClickListener {
            if (aPreparerVisible) {
                fermerAPreparer()
            } else {
                fermerFragment()
                ouvrirAPreparer()
            }
        }

        preparer_LL.setOnClickListener {
            if (preparerVisible) {
                fermerPreparer()
            } else {
                fermerFragment()
                ouvrirPreparer()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (statutConnexion && passageParOnCreate) {
            val requestQueue = Volley.newRequestQueue(this@DetailPreparationV2)
            val urlRequete =
                ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePreparationDetail + preparationCourante.uid

            val obreq: JsonObjectRequest = getJsonObjectRequest(urlRequete)
            requestQueue.add<JSONObject?>(obreq)
        } else {
          gestionVisuelle()
        }
    }
    private fun getJsonObjectRequest(urlRequete: String?): JsonObjectRequest {
        val obreq = object : JsonObjectRequest(
            Method.GET, urlRequete, null,
            { response ->
                try {
                    val nbResultat = response.getInt("resultCount")
                    if (nbResultat == 0) {
                        val erreur = response.getString("erreur")
                        when {
                            erreur == context.getString(R.string.tokenInvalide) ->
                                Alerte.afficherAlerte(context, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte")
                            erreur == context.getString(R.string.tokenExpire) ->
                                Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte")
                            !erreur.contentEquals("Aucun PH_Preparation trouvé") ->
                                Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Aucune ligne trouvée", "alerte")
                        }
                    } else {
                        Stock_Lot_EmplacementLightOpenHelper.viderTableStock_Lot_EmplacementsSansSerie(db)
                        StockUtilisesOpenHelper.viderTableStockUtiliser(db)

                        val phPreparationLigneJSONArray = response.getJSONArray("PH_Preparation_Ligne")

                        for (k in 0 until phPreparationLigneJSONArray.length()) {
                            val phPreparationLigneJSONObject = phPreparationLigneJSONArray.getJSONObject(k)
                            val phStockLotEmplacementJSONArray = phPreparationLigneJSONObject.getJSONArray("ph_stock_lot_emplacements")
                            val stockUtilisesJSONArray = phPreparationLigneJSONObject.getJSONArray("stock_utilises")

                            for (y in 0 until phStockLotEmplacementJSONArray.length()) {
                                val stockLotEmplacementLight = Stock_Lot_Emplacement_Light(phStockLotEmplacementJSONArray.getJSONObject(y))
                                val stockLotEmplacementBdd = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stockLotEmplacementLight.get_UID())

                                if (stockLotEmplacementBdd == null) {
                                    if (stockLotEmplacementLight.getQte() >= 0) {
                                        Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stockLotEmplacementLight)
                                    }
                                } else {
                                    if (stockLotEmplacementBdd.getQte() != stockLotEmplacementLight.getQte()) {
                                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacementLight)
                                    }
                                }
                            }

                            for (z in 0 until stockUtilisesJSONArray.length()) {
                                val stockUtilisesTemp = StockUtilises(stockUtilisesJSONArray.getJSONObject(z))
                                StockUtilisesOpenHelper.insererUnStockUtilisesEnBDD(db, stockUtilisesTemp)

                                if (stockUtilisesTemp.getUserId() != utilisateurConnecte.getId()) {
                                    val stockCourantTemp = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stockUtilisesTemp.getStockId())
                                    if (stockCourantTemp != null) {
                                        stockCourantTemp.setQte(stockCourantTemp.getQte() - stockUtilisesTemp.getQuantite())
                                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockCourantTemp)
                                    }
                                }
                            }
                        }

                        for (phPrepLigne in phPreparationLignes) {
                            if ((phPrepLigne.getQte_APreparer() > 0 || phPrepLigne.getQte_Demander() == phPrepLigne.getQte_preparer()) && phPrepLigne.getQte_APreparer() != 0) {
                                val produit = ProduitOpenHelper.getProduitByID(db, phPrepLigne.getProduitID())

                                if (produit != null) {
                                    val stockLotEmplacementLightList = Stock_Lot_EmplacementLightOpenHelper
                                        .getAllStockLotEmplacementByProduitEtDepot(db, produit, depotOrigine)

                                    val lignesPreparer = PH_Preparation_LigneOpenHelper
                                        .getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, preparationCourante, phPrepLigne.getProduitID())

                                    stockLotEmplacementLightList.sortWith(compareBy { it.getLot() })
                                    stockLotEmplacementLightList.sortWith(compareBy { it.getPeremptionDate() })

                                    for (stockLotEmplacement in stockLotEmplacementLightList) {
                                        if (stockLotEmplacement.getQte() >= 0) {
                                            stockLotEmplacement.setQte_Preparer(0)

                                            for (ligneCourante in lignesPreparer) {
                                                if (ligneCourante.getLotNumero().contentEquals(stockLotEmplacement.getLot()) &&
                                                    ligneCourante.getEmplacementParDefaut().contentEquals(stockLotEmplacement.getEmplacement())) {
                                                    stockLotEmplacement.setQte_Preparer(ligneCourante.getQte_preparer())
                                                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacement)
                                                    break
                                                }
                                            }
                                        }
                                    }

                                    for (ligneCourante in lignesPreparer) {
                                        val stockTemp = Stock_Lot_EmplacementLightOpenHelper
                                            .getStockLotEmplacementByLotPeremptionEtDepotEmplacement(
                                                db,
                                                ligneCourante.getLotNumero(),
                                                ligneCourante.getPeremptionDate(),
                                                depotOrigine,
                                                ligneCourante.getEmplacementParDefaut()
                                            )
                                        if (stockTemp == null) {
                                            PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ligneCourante)
                                        }
                                    }
                                }
                            }
                        }

                        invalidateOptionsMenu()
                        passageParOnCreate = false
                        arreterSpinner()
                        gestionVisuelle()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("Volley", "Error")
                Alerte.afficherAlerte(
                    this@DetailPreparationV2,
                    "Erreur",
                    "Veuillez contacter la société Alcyons (erreur Volley : Préparation PAD)",
                    "alerte"
                )
            }
        ) {
            override fun getHeaders(): MutableMap<String?, String?> {
                return HashMap<String?, String?>().apply {
                    put("Authorization", utilisateurConnecte.getToken())
                }
            }
        }

        obreq.setRetryPolicy(retryPolicy)
        return obreq
    }

    fun gestionVisuelle()
    {
        //on récupère les ph_reliquat de base
        val listePHPreparationLigneBase = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, preparationCourante)

        for(preparationLigneBase in listePHPreparationLigneBase)
        {
            //on récupère les reliquats négatif du reliquat courant
            val listePreparationLigneNegByProduit = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, preparationCourante, preparationLigneBase.produitID)
            for(ligneNeg in listePreparationLigneNegByProduit)
            {
                preparationLigneBase.qte_APreparer -= ligneNeg.qte_preparer
            }

            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, preparationLigneBase)
        }

        val nbLigneTotal = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, preparationCourante).size
        val nbLignePreparer = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationNeg(db, preparationCourante).size
        findViewById<TextView>(R.id.nbReferenceAPreparer_TV).text = nbLigneTotal.toString()
        findViewById<TextView>(R.id.nbReferencePreparer_TV).text = nbLignePreparer.toString()
        findViewById<ProgressBar>(R.id.progressBarPreparation_PB).max = nbLigneTotal
        findViewById<ProgressBar>(R.id.progressBarPreparation_PB).progress = nbLignePreparer

        if(nbLignePreparer > 0)
            findViewById<CardView>(R.id.btnValiderPreparation_CV).visibility = View.VISIBLE
        else
            findViewById<CardView>(R.id.btnValiderPreparation_CV).visibility = View.GONE

        findViewById<CardView>(R.id.btnValiderPreparation_CV).setOnClickListener { v: View? ->
            demandeConfirmationValidation(layoutInflater) { resultat ->
            }
        }

        ouvrirScanner()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH) {
            rafraichirListe()
        }
        invalidateOptionsMenu()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (detailVisible)
            fermerDetailFragment()
        else {
            retourService(bundle)
        }
    }

    /**
     * SCANNER
     */
    private fun ouvrirScanner() {
        scannerContainer.apply {
            layoutParams = (layoutParams as LinearLayout.LayoutParams).also {
                it.height = (300 * resources.displayMetrics.density).toInt()
                it.weight = 0f
            }
            visibility = View.VISIBLE
            translationY = -resources.displayMetrics.heightPixels.toFloat()
            animate().translationY(0f).setDuration(300).start()
        }

        val frag = choisirFragmentScanner().also { scannerFragment = it }

        when (frag) {
            is ScannerInputFragment -> {
                frag.onCodeScanned = { code -> traiterCodeScanne(code) }
                frag.onCloseRequested = { fermerScanner() }
            }

            is ScannerFragment -> {
                frag.onCodeScanned = { code -> traiterCodeScanne(code) }
                frag.onCloseRequested = { fermerScanner() }
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.scannerContainer, frag)
            .commit()

        scannerVisible = true
    }

    private fun choisirFragmentScanner(): Fragment {
        // Vérifie si c'est un Zebra ou Honeywell
        if (estScannerProfessionnel()) {
            return ScannerInputFragment()
        }

        // Vérifie si l'appareil a une caméra
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            ScannerFragment()
        } else {
            ScannerInputFragment()
        }
    }

    private fun estScannerProfessionnel(): Boolean {
        val fabricant = Build.MANUFACTURER.uppercase()
        val modele = Build.MODEL.uppercase()
        return fabricant.contains("ZEBRA") ||
                fabricant.contains("HONEYWELL") ||
                modele.contains("TC") ||    // Zebra TC series
                modele.contains("MC") ||    // Zebra MC series
                modele.contains("CK") ||    // Honeywell CK series
                modele.contains("CT") ||    // Honeywell CT series
                modele.contains("CN")       // Honeywell CN series
    }

    private fun fermerScanner() {
        scannerContainer.animate()
            .translationY(-scannerContainer.height.toFloat())
            .setDuration(300)
            .withEndAction {
                scannerContainer.visibility = View.GONE
                scannerContainer.layoutParams =
                    (scannerContainer.layoutParams as LinearLayout.LayoutParams).also {
                        it.height = 0
                    }
                scannerFragment?.let { frag: Fragment ->
                    supportFragmentManager.beginTransaction().remove(frag).commit()
                }
                scannerFragment = null
            }.start()

        scannerVisible = false
    }

    /**
     * RECHERCHE
     */
    private fun ouvrirRecherche() {
        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled =
            false
        rechercheContainer.apply {
            layoutParams = (layoutParams as LinearLayout.LayoutParams).also {
                it.height = LinearLayout.LayoutParams.WRAP_CONTENT
                it.weight = 0f
            }
            visibility = View.VISIBLE
            translationY = -resources.displayMetrics.heightPixels.toFloat()
            animate().translationY(0f).setDuration(300).start()
        }

        val frag = RechercheFragment().also { rechercheFragment = it }
        supportFragmentManager.beginTransaction()
            .replace(R.id.rechercheContainer, frag)
            .commitNow()

        rechercheVisible = true
    }

    private fun fermerRecherche() {
        cacherSearchInput()
        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled =
            true
        rechercheContainer.animate()
            .translationY(-rechercheContainer.height.toFloat())
            .setDuration(300)
            .withEndAction {
                rechercheContainer.visibility = View.GONE
                rechercheContainer.layoutParams =
                    (rechercheContainer.layoutParams as LinearLayout.LayoutParams).also {
                        it.height = 0
                    }
                rechercheFragment?.let { frag ->
                    supportFragmentManager.beginTransaction().remove(frag).commit()
                }
                rechercheFragment = null
            }.start()

        rechercheVisible = false
    }

    private fun afficherSearchInput() {
        rechercheVisible = true
        // Bascule TextView → EditText dans le header
        findViewById<ImageView>(R.id.chevronRecherche).visibility = View.GONE
        textChercher_TV.visibility = View.GONE
        searchInput_ET.visibility = View.VISIBLE
        effacerRecherche_IV.visibility = View.VISIBLE
        searchInput_ET.requestFocus()

        // Ouvre le clavier
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(searchInput_ET, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)

        // Écoute la saisie et lance la recherche dans le fragment
        searchInput_ET.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    ouvrirRecherche()
                    rechercheFragment?.lancerRecherche(query, "preparation", preparationCourante.uid.toString())
                } else {
                    rechercheFragment?.viderListe()
                }
            }
        })
    }

    private fun cacherSearchInput() {
        findViewById<ImageView>(R.id.chevronRecherche).visibility = View.VISIBLE
        textChercher_TV.visibility = View.VISIBLE
        searchInput_ET.visibility = View.GONE
        effacerRecherche_IV.visibility = View.GONE
        searchInput_ET.text.clear()

        // Ferme le clavier
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(searchInput_ET.windowToken, 0)
    }

    /**
     * A préparer
     */
    private fun ouvrirAPreparer(idProduit: Int = 0) {
        var liste: ArrayList<PH_Preparation_Ligne> = arrayListOf()

        if (idProduit == 0) {
            liste = ArrayList(
                PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(
                    db,
                    preparationCourante
                )
            )
        } else {
            liste.add(
                PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneBaseByPreparationAndIdProduit(
                    db,
                    preparationCourante,
                    idProduit
                )
            )
        }

        if (liste.isNotEmpty()) {
            val frag = APreparerFragment.newInstance(liste)
            supportFragmentManager.beginTransaction()
                .replace(R.id.referenceAPreparerContainer, frag)
                .commitNow()

            referenceAPreparerContainer.apply {
                layoutParams = (layoutParams as LinearLayout.LayoutParams).also {
                    it.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    it.weight = 0f
                }
                visibility = View.VISIBLE
                translationY = 0f // Plus d'animation de translation
                alpha = 0f        // Animation en fondu à la place
                animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }

            aPreparerVisible = true
        }
    }

    private fun fermerAPreparer() {
        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled =
            true
        referenceAPreparerContainer.animate()
            .translationY(-referenceAPreparerContainer.height.toFloat())
            .setDuration(300)
            .withEndAction {
                referenceAPreparerContainer.visibility = View.GONE
                referenceAPreparerContainer.layoutParams =
                    (referenceAPreparerContainer.layoutParams as LinearLayout.LayoutParams).also {
                        it.height = 0
                    }
                aPreparerFragment?.let { frag ->
                    supportFragmentManager.beginTransaction().remove(frag).commit()
                }
                aPreparerFragment = null
            }.start()

        aPreparerVisible = false
    }

    /**
     * Préparer
     */
    private fun ouvrirPreparer(idProduit: Int = 0) {
        var liste: ArrayList<PH_Preparation_Ligne>

        if (idProduit == 0) {
            /*liste = ArrayList(
                PH_ReliquatOpenHelper
                    .getPH_ReliquatNegByCommandeNumero(
                        db,
                        receptionCourant.numero
                    )
            )*/
        } else {
            /*liste = ArrayList(
                PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(
                    db,
                    receptionCourant.numero,
                    idProduit
                )
            )*/
        }

        /*if (liste.isNotEmpty()) {
            // Affiche le container
            referencePreparerContainer.apply {
                layoutParams = (layoutParams as LinearLayout.LayoutParams).also {
                    it.height = (400 * resources.displayMetrics.density).toInt()
                    it.weight = 0f
                }
                visibility = View.VISIBLE
                translationY = -resources.displayMetrics.heightPixels.toFloat()
                animate().translationY(0f).setDuration(300).start()
            }

            // Crée le fragment avec la liste
            val frag = ReceptionnerFragment.newInstance(liste)
            supportFragmentManager.beginTransaction()
                .replace(R.id.referenceReceptionnerContainer, frag)
                .commitNow()

            preparerVisible = true
        }*/
    }

    private fun fermerPreparer() {
        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled =
            true
        referencePreparerContainer.animate()
            .translationY(-referencePreparerContainer.height.toFloat())
            .setDuration(300)
            .withEndAction {
                referencePreparerContainer.visibility = View.GONE
                referencePreparerContainer.layoutParams =
                    (referencePreparerContainer.layoutParams as LinearLayout.LayoutParams).also {
                        it.height = 0
                    }
                preparerFragment?.let { frag ->
                    supportFragmentManager.beginTransaction().remove(frag).commit()
                }
                preparerFragment = null
            }.start()

        preparerVisible = false
    }

    private fun ouvrirDetailFragment(
        ligne: PH_Preparation_Ligne?,
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            val fragmentDejaOuvert =
                detailFragment != null && detailContainer.visibility == View.VISIBLE

            if (fragmentDejaOuvert) {
                // ─── Fragment déjà visible : on met juste à jour les données ───
                ligne?.let { detailFragment?.mettreAJourLigne(it) }
            } else {
                // ─── Fragment fermé : on l'ouvre normalement ───
                val produit = ProduitOpenHelper.getProduitByID(db, ligne?.produitID ?: 0)
                val frag = DetailFragment.newInstance(ligne, produit)
                    .also { detailFragment = it }
                frag.onFermer = { fermerDetailFragment() }

                frag.onValider = { ligne, ajout ->
                    if(ligne == null)
                    {
                        ElementASynchroniserOpenHelper.toutSynchroniser(this@DetailPreparationV2, db, utilisateurConnecte, false)
                        fermerDetailFragment()
                        rafraichirListe()
                        ouvrirScanner()
                    }
                    else
                    {
                        if (ajout) {
                            // L'utilisateur a choisi Ajouter
                            ajouterPHPreparationLigne(
                                ligne
                            )
                        } else {
                            // L'utilisateur a choisi Modifier
                            enregistrerPhPreparationLigne(ligne)
                        }
                    }

                }

                supportFragmentManager.beginTransaction()
                    .replace(R.id.detailContainer, frag)
                    .commit()

                detailContainer.translationY = hauteurDetailFragment.toFloat()
                detailContainer.visibility = View.VISIBLE
                detailContainer.animate()
                    .translationY(0f)
                    .setDuration(300)
                    .start()
            }
        }

        detailVisible = true
    }

    private fun fermerDetailFragment() {
        positionSelectionnee = -1

        detailContainer.animate()
            .translationY(hauteurDetailFragment.toFloat())
            .setDuration(300)
            .withEndAction {
                detailContainer.visibility = View.GONE
                detailFragment?.also {
                    supportFragmentManager.beginTransaction().remove(it).commit()
                }
                detailFragment = null
            }.start()

        detailVisible = false
    }

    private fun fermerFragment() {
        if (scannerVisible) fermerScanner()
        if (rechercheVisible) fermerRecherche()
        if (aPreparerVisible) fermerAPreparer()
        if (preparerVisible) fermerPreparer()
        if (detailVisible) fermerDetailFragment()
    }

    private fun traiterCodeScanne(code: String) {
        fermerScanner()

        if (code.isEmpty()) {
            ouvrirScanner()
            return
        }

        if (!scannerProcessing && !alerteVisible) { // ← vérifie aussi alerteVisible
            scannerProcessing = true
            lifecycleScope.launch(Dispatchers.IO) {
                val resultDecoupage: HashMap<String, String> = GestionCodeScanne.decoupageCode(code)
                val codeIdentification = resultDecoupage["code"]
                val numeroLotIdentification = resultDecoupage["lot"]
                val peremptionIdentification = resultDecoupage["peremption"]
                val numeroSerieIdentification = resultDecoupage["serie"]
                val tabDateSQL = peremptionIdentification?.split("/")
                var datePeremptionSQL = ""
                if (tabDateSQL?.size == 3)
                    datePeremptionSQL =
                        tabDateSQL?.get(tabDateSQL.size - 1) + "-" + tabDateSQL?.get(1) + "-" + tabDateSQL?.get(
                            0
                        )
                val produitIdentifier: List<Produit> =
                    ProduitOpenHelper.getProduitsByIdentification(db, codeIdentification)

                if (!produitIdentifier.isEmpty() && produitIdentifier.size == 1) {
                    val produit = produitIdentifier[0]

                    /*var reliquatcourant = PH_ReliquatOpenHelper.getPH_ReliquatByUnIdProduitetNumeroLotSerie(db, produit.iD_produit, receptionCourant.numero, numeroLotIdentification, numeroSerieIdentification)

                    if(reliquatcourant != null)
                    {
                        if(produit.isSuivi_Serialisation && produit.isSerialiser_Reception_Delivrance && numeroSerieIdentification != "")
                        {
                            withContext(Dispatchers.Main) {
                                alerteVisible = true // ← on lève le flag avant d'afficher
                                afficherAlerteAvecCallback(
                                    "Erreur",
                                    "Numero de série déjà scanné"
                                ) {
                                    alerteVisible = false // ← on baisse le flag à la fermeture
                                    ouvrirScanner()
                                }
                            }
                        }
                        else
                        {
                            ouvrirDetailFragment(reliquatcourant)
                        }
                    }
                    else
                    {
                        reliquatcourant = PH_ReliquatOpenHelper.getPH_ReliquatBaseByUnIdProduitetNumero(db, produit.iD_produit, receptionCourant.numero)

                        if(reliquatcourant != null)
                        {
                            val randomreliquat = Random()
                            var reliquatId = randomreliquat.nextInt()
                            if (reliquatId > 0) reliquatId = reliquatId * -1

                            reliquatcourant.setReliquat_UID(reliquatId)
                            reliquatcourant.lot = numeroLotIdentification
                            reliquatcourant.serie = numeroSerieIdentification
                            reliquatcourant.peremptionDate = datePeremptionSQL
                            reliquatcourant.scanValue = ""
                            reliquatcourant.bL_Numero = ""

                            //ajout de la serialisation si suivi par série
                            if(produit.isSuivi_Serialisation && produit.isSerialiser_Reception_Delivrance && numeroSerieIdentification != "")
                                Serialisation.Serialisation_Creer(utilisateurConnecte.id, "G110", codeIdentification, "GTIN", numeroLotIdentification, peremptionIdentification, numeroSerieIdentification, "CDE", receptionCourant.numero).toInt()


                            ouvrirDetailFragment(reliquatcourant)
                        }
                        else
                        {
                            withContext(Dispatchers.Main) {
                                alerteVisible = true // ← on lève le flag avant d'afficher
                                afficherAlerteAvecCallback(
                                    "Erreur",
                                    "Référence non présente dans la réception"
                                ) {
                                    alerteVisible = false // ← on baisse le flag à la fermeture
                                    ouvrirScanner()
                                }
                            }
                        }
                    }*/
                } else {
                    withContext(Dispatchers.Main) {
                        alerteVisible = true // ← on lève le flag avant d'afficher
                        afficherAlerteAvecCallback(
                            "Erreur",
                            "Produit inconnu en base de données"
                        ) {
                            alerteVisible = false // ← on baisse le flag à la fermeture
                            ouvrirScanner()
                        }
                    }
                }

                scannerProcessing = false
            }
        }
    }

    private fun rafraichirListe() {
        /*val nbReliquatTotal = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, receptionCourant.numero).size
        val nbReliquatPreparer = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumero(db, receptionCourant.numero).size
        findViewById<TextView>(R.id.nbReferenceAReceptionner_TV).text = nbReliquatTotal.toString()
        findViewById<TextView>(R.id.nbReferenceReceptionner_TV).text = nbReliquatPreparer.toString()
        findViewById<ProgressBar>(R.id.progressBarReception_PB).max = PH_ReliquatOpenHelper.getNbReliquatBaseByCommande(db, receptionCourant.numero)
        findViewById<ProgressBar>(R.id.progressBarReception_PB).progress = nbReliquatPreparer

        if(nbReliquatPreparer > 0)
            findViewById<CardView>(R.id.btnValiderReception_CV).visibility = View.VISIBLE
        else
            findViewById<CardView>(R.id.btnValiderReception_CV).visibility = View.GONE*/
    }

    private fun getDateDuJour(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onElementRechercher(idProduit: Int) {
        fermerRecherche()
        ouvrirAPreparer(idProduit)
        ouvrirPreparer(idProduit)
    }

    private fun ajouterPHPreparationLigne(
        nouveauPHPL : PH_Preparation_Ligne
    ) {


        val rowID = PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, nouveauPHPL)
        if (rowID != -1L) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                db,
                PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE,
                nouveauPHPL.phiMR4UUID,
                nouveauPHPL._UID,
                DBOpenHelper.ActionsEAS.AJOUT
            )
            ElementASynchroniserOpenHelper.toutSynchroniser(
                this@DetailPreparationV2,
                db,
                utilisateurConnecte,
                false
            )

            fermerDetailFragment()
            rafraichirListe()
            ouvrirScanner()
        }
    }

    private fun enregistrerPhPreparationLigne(phPL: PH_Preparation_Ligne) {
        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, phPL)
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
            db,
            PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE,
            phPL.phiMR4UUID,
            phPL._UID,
            DBOpenHelper.ActionsEAS.MAJ
        )
        ElementASynchroniserOpenHelper.toutSynchroniser(
            this@DetailPreparationV2,
            db,
            utilisateurConnecte,
            false
        )

        fermerDetailFragment()
        rafraichirListe()
        ouvrirScanner()
    }

    private fun afficherAlerteAvecCallback(titre: String, message: String, onDismiss: () -> Unit) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val layout = layoutInflater.inflate(R.layout.alerte_information, null)

        layout.findViewById<TextView>(R.id.titre).text = titre
        layout.findViewById<TextView>(R.id.messageFin).text = message
        builder.setView(layout)

        val alertDialog = builder.create()
        alertDialog.window?.setGravity(android.view.Gravity.CENTER)
        alertDialog.window?.setBackgroundDrawable(
            android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
        )
        alertDialog.show()

        layout.findViewById<LinearLayout>(R.id.buttonOk).setOnClickListener {
            alertDialog.dismiss()
            onDismiss() // ← remet alerteVisible à false
        }
    }

    fun demandeConfirmationValidation(inflater: LayoutInflater, onResultat: (Boolean) -> Unit) {
        val builder = context?.let { AlertDialog.Builder(it) }
        val layout = inflater.inflate(R.layout.alerte_confirmation, null)

        val zoneok = layout.findViewById<LinearLayout>(R.id.buttonOk)
        val buttonAnnuler = layout.findViewById<LinearLayout>(R.id.buttonAnnuler)
        val messageTextView = layout.findViewById<TextView>(R.id.messageFin)

        messageTextView.text = "Souhaitez-vous valider la préparation ?"
        layout.findViewById<TextView>(R.id.TitreAnnulation).text = "Non"
        layout.findViewById<TextView>(R.id.TitreConfirmation).text = "Oui"

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

    override fun ajusterHauteurRecherche(hauteur: Int) {
        rechercheContainer.layoutParams =
            (rechercheContainer.layoutParams as LinearLayout.LayoutParams).also {
                it.height = if (hauteur == 0) 0 else LinearLayout.LayoutParams.WRAP_CONTENT
            }
        rechercheContainer.requestLayout()
    }

    private fun checkInternetConnection(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        // test for connection
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo()!!.isAvailable()
                && cm.getActiveNetworkInfo()!!.isConnected()
    }

    override fun retourService(bundle: Bundle) {
        var detailPreparationIntent =
            Intent(this@DetailPreparationV2, ServicePreparationPufActivity::class.java)
        if (preparationCourante.depotDestinataireReference.contains("-PAD")) detailPreparationIntent =
            Intent(this@DetailPreparationV2, ServicePreparationPadActivity::class.java)
        detailPreparationIntent.putExtras(bundle)
        this@DetailPreparationV2.startActivity(detailPreparationIntent)
        this@DetailPreparationV2.finish()
    }

    override fun onElementSelectionne(element: PH_Preparation_Ligne) {
        fermerFragment()
        ouvrirDetailFragment(element)
    }
}