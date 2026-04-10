package fr.alcyons.phiwms_mobile.Reception

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur
import fr.alcyons.phiwms_mobile.Classes.Commande
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Fragment.RechercheFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerInputFragment
import fr.alcyons.phiwms_mobile.Inventaire.InventaireZoneActivity
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.Reception.Adapter.DetailReceptionAdapter
import fr.alcyons.phiwms_mobile.Reception.Fragment.AReceptionnerFragment
import fr.alcyons.phiwms_mobile.Reception.Fragment.DetailFragment
import fr.alcyons.phiwms_mobile.Reception.Fragment.ReceptionnerFragment
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.Locale
import java.util.Objects
import java.util.Random

class DetailReception_V2 : ServiceAvecConnexionActivity(),
    RechercheFragment.OnElementRechercheListener,
    AReceptionnerFragment.OnElementSelectionneListener,
    ReceptionnerFragment.OnElementSelectionneListener {

    private lateinit var receptionCourant: Commande
    private lateinit var context: Context
    private lateinit var scannerContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var rechercheContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var referenceAReceptionnerContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var referenceReceptionnerContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var detailContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var lancerScan: LinearLayout
    private lateinit var lancerRecherhe: LinearLayout
    private lateinit var aReceptionner_LL: LinearLayout
    private lateinit var receptionner_LL: LinearLayout
    private lateinit var btnValiderReception_LL: LinearLayout
    private lateinit var btnValiderReception_CV: CardView
    private lateinit var btnSimulationReception_CV: CardView
    private var adapter: DetailReceptionAdapter? = null
    private var scannerFragment: Fragment? = null
    private var rechercheFragment: RechercheFragment? = null
    private var aReceptionnerFragment: AReceptionnerFragment? = null
    private var receptionnerFragment: ReceptionnerFragment? = null
    private var detailFragment: DetailFragment? = null
    private var scannerVisible = false
    private var rechercheVisible = false
    private var aCompterVisible = false
    private var CompterVisible = false
    private var detailVisible = false
    private var scannerProcessing = false
    private var alerteVisible = false
    private var positionSelectionnee = -1
    private var hauteurDetailFragment = 0

    private lateinit var textChercher_TV: TextView
    private lateinit var searchInput_ET: EditText
    private lateinit var effacerRecherche_IV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_reception_module)
        context = this

        // Récupération des données de l'intent
        receptionCourant = CommandeOpenHelper.getCommandeByID(
            db, intent.extras!!.getInt("commandeId")
        )

        // Binding des vues
        findViewById<TextView>(R.id.commandeNumero).text = receptionCourant.numero

        scannerContainer = findViewById(R.id.scannerContainer)
        rechercheContainer = findViewById(R.id.rechercheContainer)
        referenceAReceptionnerContainer = findViewById(R.id.referenceAReceptionnerContainer)
        referenceReceptionnerContainer = findViewById(R.id.referenceReceptionnerContainer)
        detailContainer = findViewById(R.id.detailContainer)

        lancerScan = findViewById(R.id.lancerScan)
        lancerRecherhe = findViewById(R.id.lancerRecherhe)
        aReceptionner_LL = findViewById(R.id.aReceptionner_LL)
        receptionner_LL = findViewById(R.id.receptionner_LL)
        btnValiderReception_LL = findViewById(R.id.btnValiderReception_LL)
        btnValiderReception_CV = findViewById(R.id.btnValiderReception_CV)
        btnSimulationReception_CV = findViewById(R.id.btnSimulationReception_CV)
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

        aReceptionner_LL.setOnClickListener {
            if (aCompterVisible) {
                fermerACompter()
            } else {
                fermerFragment()
                ouvrirACompter()
            }
        }

        receptionner_LL.setOnClickListener {
            if (CompterVisible) {
                fermerCompter()
            } else {
                fermerFragment()
                ouvrirCompter()
            }
        }

        if (utilisateurConnecte.identifiant.uppercase().contentEquals("ALCYONS")) {
            btnSimulationReception_CV.visibility = View.VISIBLE
            btnSimulationReception_CV.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.progressbar_modale, null)
                val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)
                val tvProgress = dialogView.findViewById<TextView>(R.id.tvProgress)

                val dialog = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create()

                dialog.show()

                val liste = ArrayList(
                    PH_ReliquatOpenHelper
                        .getPH_ReliquatBaseByCommandeNumero(
                            db,
                            receptionCourant.numero
                        )
                )

                progressBar.max = liste.size

                // Thread de traitement
                lifecycleScope.launch(Dispatchers.IO) {
                    liste.forEachIndexed { index, inventaireLigne ->
                        val progression = index + 1

                        /*inventaireLigne.stockPhysique = 10.toDouble()
                        inventaireLigne.inventaireDate = getDateDuJour()

                        Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(
                            db,
                            inventaireLigne
                        )

                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                            db,
                            Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP,
                            inventaireLigne.getPhiMR4UUID(),
                            inventaireLigne.get_UID(),
                            DBOpenHelper.ActionsEAS.MAJ
                        )

                        ElementASynchroniserOpenHelper.toutSynchroniser(
                            this@DetailReception_V2,
                            db,
                            utilisateurConnecte,
                            false
                        )*/

                        // Mise à jour de l'UI sur le thread principal
                        withContext(Dispatchers.Main) {
                            progressBar.progress = progression
                            tvProgress.text = "$progression / ${liste.size}"
                            rafraichirListe()
                        }
                    }

                    // Fermeture de la modale sur le thread principal
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                        btnSimulationReception_CV.visibility = View.GONE
                    }
                }
            }
        } else {
            btnSimulationReception_CV.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (statutConnexion && passageParOnCreate) {
            afficherSpinner(
                this@DetailReception_V2,
                LayoutInflater.from(this@DetailReception_V2)
            )
            val requestQueue = Volley.newRequestQueue(this@DetailReception_V2)
            val urlRequete = ""

            val obreq: JsonObjectRequest = getJsonObjectRequest(urlRequete)
            requestQueue.add<JSONObject?>(obreq)
        }
    }

    private fun getJsonObjectRequest(urlRequete: String?): JsonObjectRequest {
        val obreq: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, urlRequete, null,
            Response.Listener { response: JSONObject? ->
                try {
                    val resultCount = response!!.getInt("resultCount")
                    if (resultCount == 0) {
                        val erreur = response.getString("erreur")
                        if (erreur == context.getString(R.string.tokenInvalide)) {
                            Alerte.afficherAlerteInformation(
                                this@DetailReception_V2,
                                getLayoutInflater(),
                                "Erreur",
                                "Votre session de connexion est invalide, veuillez vous reconnecter.",
                                false,
                                true
                            )
                        } else if (erreur == context.getString(R.string.tokenExpire)) {
                            Alerte.afficherAlerteInformation(
                                this@DetailReception_V2,
                                getLayoutInflater(),
                                "Erreur",
                                "Votre session de connexion est expirée, veuillez vous reconnecter.",
                                false,
                                true
                            )
                        } else if (!erreur.contentEquals("Aucun Inventaire trouvé")) {
                            Alerte.afficherAlerteInformation(
                                this@DetailReception_V2,
                                getLayoutInflater(),
                                "Erreur",
                                "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Général",
                                false,
                                true
                            )
                        } else {
                            arreterSpinner()
                            Alerte.afficherAlerteInformation(
                                this@DetailReception_V2,
                                getLayoutInflater(),
                                "Information",
                                "Aucun inventaire général à traiter",
                                false,
                                true
                            )
                        }
                    } else {

                        arreterSpinner()
                        ouvrirScanner()
                        if (passageParOnCreate) {
                            invalidateOptionsMenu()
                        }

                        passageParOnCreate = false
                        Handler(Looper.getMainLooper()).postDelayed(
                            Runnable { this.arreterSpinner() },
                            500
                        )
                    }
                } catch (e: JSONException) {
                    Log.e("JSON Exception", Objects.requireNonNull<String?>(e.message))
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                Log.e("Volley", "Error")
                Alerte.afficherAlerteInformation(
                    this@DetailReception_V2,
                    getLayoutInflater(),
                    "Erreur",
                    "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Général",
                    false,
                    true
                )
            }
        ) {
            override fun getHeaders(): MutableMap<String?, String?> {
                val headers = HashMap<String?, String?>()
                headers.put("Authorization", utilisateurConnecte.getToken())
                return headers
            }
        }
        return obreq
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
            val intent = Intent(
                this,
                InventaireZoneActivity::class.java
            )
            intent.putExtras(Bundle().apply {
                putInt("utilisateurConnecteID", utilisateurConnecte.getId())
                putInt("commandeId", receptionCourant.iD_commande)
            })

            startActivity(intent)
            finish()
        }
    }

    override fun onElementSelectionne(element: PH_Reliquat) {
        fermerFragment()
        ouvrirDetailFragment(element)
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
                    rechercheFragment?.lancerRecherche(query)
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
     * A COMPTER
     */
    private fun ouvrirACompter(idProduit: Int = 0) {
        var liste: ArrayList<PH_Reliquat> = arrayListOf()

        if (idProduit == 0) {
            liste = ArrayList(
                PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(
                    db,
                    receptionCourant.numero
                )
            )
        } else {
            /*liste.add(
                PH_ReliquatOpenHelper.getPH_ReliquatByUnIdProduitetNumero(
                    db,
                    idProduit,
                    receptionCourant.numero
                )
            )*/
        }

        if (liste.isNotEmpty()) {
            val frag = AReceptionnerFragment.newInstance(liste)
            supportFragmentManager.beginTransaction()
                .replace(R.id.referenceACompterContainer, frag)
                .commitNow()

            referenceAReceptionnerContainer.apply {
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

            aCompterVisible = true
        } else {
            if (idProduit != 0)
                ajouterInventaireLigneTemp(idProduit)
        }
    }

    private fun fermerACompter() {
        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled =
            true
        referenceAReceptionnerContainer.animate()
            .translationY(-referenceAReceptionnerContainer.height.toFloat())
            .setDuration(300)
            .withEndAction {
                referenceAReceptionnerContainer.visibility = View.GONE
                referenceAReceptionnerContainer.layoutParams =
                    (referenceAReceptionnerContainer.layoutParams as LinearLayout.LayoutParams).also {
                        it.height = 0
                    }
                aReceptionnerFragment?.let { frag ->
                    supportFragmentManager.beginTransaction().remove(frag).commit()
                }
                aReceptionnerFragment = null
            }.start()

        aCompterVisible = false
    }

    /**
     * COMPTER
     */
    private fun ouvrirCompter(idProduit: Int = 0) {
        var liste: ArrayList<PH_Reliquat>

        if (idProduit == 0) {
            liste = ArrayList(
                PH_ReliquatOpenHelper
                    .getPH_ReliquatNegByCommandeNumero(
                        db,
                        receptionCourant.numero
                    )
            )
        } else {
            liste = ArrayList(
                PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(
                    db,
                    receptionCourant.numero,
                    idProduit
                )
            )
        }

        if (liste.isNotEmpty()) {
            // Affiche le container
            referenceReceptionnerContainer.apply {
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
                .replace(R.id.referenceCompterContainer, frag)
                .commitNow()

            CompterVisible = true
        }
    }

    private fun fermerCompter() {
        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled =
            true
        referenceReceptionnerContainer.animate()
            .translationY(-referenceReceptionnerContainer.height.toFloat())
            .setDuration(300)
            .withEndAction {
                referenceReceptionnerContainer.visibility = View.GONE
                referenceReceptionnerContainer.layoutParams =
                    (referenceReceptionnerContainer.layoutParams as LinearLayout.LayoutParams).also {
                        it.height = 0
                    }
                receptionnerFragment?.let { frag ->
                    supportFragmentManager.beginTransaction().remove(frag).commit()
                }
                receptionnerFragment = null
            }.start()

        CompterVisible = false
    }

    private fun ouvrirDetailFragment(
        ligne: PH_Reliquat?,
        nouvelleCreation: Boolean = false
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            val fragmentDejaOuvert =
                detailFragment != null && detailContainer.visibility == View.VISIBLE

            if (fragmentDejaOuvert) {
                // ─── Fragment déjà visible : on met juste à jour les données ───
                ligne?.let { detailFragment?.mettreAJourLigne(it) }
            } else {
                // ─── Fragment fermé : on l'ouvre normalement ───
                val frag = DetailFragment.newInstance(ligne, nouvelleCreation)
                    .also { detailFragment = it }
                frag.onFermer = { fermerDetailFragment() }

                frag.onValider = { ligne, ajout ->
                    if (ajout) {
                        // L'utilisateur a choisi Ajouter
                        ajouterInventaireLigneTemp(
                            ligne.produitID,
                            ligne.lot,
                            ligne.peremptionDate,
                            false,
                            ligne.qteLivraison.toInt()
                        )
                    } else {
                        // L'utilisateur a choisi Modifier
                        enregistrerPhReliquat(ligne)
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
        if (aCompterVisible) fermerACompter()
        if (CompterVisible) fermerCompter()
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

                    /*val inventaireLigneTemp = Inventaire_Ligne_TempOpenHelper
                        .getInventaireLigneByProduitLotPeremptionZoneDepot(
                            db,
                            inventaireCourant?.inventaire_ID ?: 0,
                            produit.iD_produit,
                            numeroLotIdentification,
                            datePeremptionSQL,
                            zoneCourante,
                            depotCourant!!.getDepot_Reference()
                        )

                    if (inventaireLigneTemp == null)
                        ajouterInventaireLigneTemp(
                            produit.iD_produit,
                            numeroLotIdentification ?: "",
                            datePeremptionSQL
                        )
                    else
                        ouvrirDetailFragment(inventaireLigneTemp)*/
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
        /*findViewById<TextView>(R.id.nbReferenceACompter_TV).text =
            Inventaire_Ligne_TempOpenHelper.getILTACompte(
                db,
                inventaireCourant!!.getInventaire_ID(),
                zoneCourante,
                depotCourant!!.getDepot_Reference()
            ).toString()
        findViewById<TextView>(R.id.nbReferenceCompter_TV).text =
            Inventaire_Ligne_TempOpenHelper.getILTCompte(
                db,
                inventaireCourant!!.getInventaire_ID(),
                zoneCourante,
                depotCourant!!.getDepot_Reference()
            ).toString()

        findViewById<ProgressBar>(R.id.progressBarInventaire_PB).max =
            Inventaire_Ligne_TempOpenHelper.getILTTotal(
                db,
                inventaireCourant!!.getInventaire_ID(),
                zoneCourante,
                depotCourant!!.getDepot_Reference()
            )
        findViewById<ProgressBar>(R.id.progressBarInventaire_PB).progress =
            Inventaire_Ligne_TempOpenHelper.getILTCompte(
                db,
                inventaireCourant!!.getInventaire_ID(),
                zoneCourante,
                depotCourant!!.getDepot_Reference()
            )*/

        invalidateOptionsMenu()
        verificationEtatInventaire()
    }

    @SuppressLint("SimpleDateFormat")
    private fun validerInventaire() {
        /*val randomAction = Random()
        var actionId = randomAction.nextInt().let { if (it > 0) it * -1 else it }
        val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

        val newAction = ActionUtilisateur(
            actionId,
            utilisateurConnecte.getId(),
            dateString,
            serviceActuel.getId(),
            utilisateurConnecte.getEtablissementId(),
            "En attente",
            inventaireCourant!!.getInventaire_ID(),
            "",
            "Inventaire General à traiter"
        )

        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, newAction)
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
            db,
            ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR,
            newAction.getPhiMR4UUID(),
            newAction.getId(),
            DBOpenHelper.ActionsEAS.AJOUT
        )
        ElementASynchroniserOpenHelper.toutSynchroniser(this, db, utilisateurConnecte, false)

        onBackPressed()*/
    }

    fun verificationEtatInventaire() {
        /*val nbTotalInventaire = Inventaire_Ligne_TempOpenHelper.getILTTotal(
            db,
            inventaireCourant!!.getInventaire_ID(),
            zoneCourante,
            depotCourant!!.getDepot_Reference()
        )
        val nbCompteInventaire = Inventaire_Ligne_TempOpenHelper.getILTCompte(
            db,
            inventaireCourant!!.getInventaire_ID(),
            zoneCourante,
            depotCourant!!.getDepot_Reference()
        )

        if (nbCompteInventaire == nbTotalInventaire) {
            btnSimulationInventaire_CV.visibility = View.GONE
            btnValiderInventaire_CV.visibility = View.VISIBLE
            btnValiderInventaire_LL.setOnClickListener {
                demandeConfirmationValidation(layoutInflater) { resultat ->
                    if (resultat)
                        validerInventaire()
                }
            }
        } else {
            if (utilisateurConnecte.identifiant.uppercase().contentEquals("ALCYONS"))
                btnSimulationInventaire_CV.visibility = View.VISIBLE
            btnValiderInventaire_CV.visibility = View.GONE
        }*/
    }

    private fun getDateDuJour(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onElementRechercher(idProduit: Int) {
        fermerRecherche()
        ouvrirACompter(idProduit)
        ouvrirCompter(idProduit)
    }

    private fun ajouterInventaireLigneTemp(
        idProduit: Int,
        lot: String = "",
        peremption: String = "",
        ouvrirDetail: Boolean = true,
        qte: Int = -1
    ) {
        /*val produit = ProduitOpenHelper.getProduitByID(db, idProduit)
        val nouvelInventaireLigneTemp = Inventaire_Ligne_Temp(
            produit,
            inventaireCourant?.inventaire_ID ?: 0,
            depotCourant,
            utilisateurConnecte,
            zoneCourante
        )

        if (depotCourant?.structure == "PUI")
            nouvelInventaireLigneTemp.emplacement = produit.emplacement_PUI_Defaut
        else
            nouvelInventaireLigneTemp.emplacement = produit.emplacement_UF_Defaut

        nouvelInventaireLigneTemp.lot = lot
        nouvelInventaireLigneTemp.peremptionDate = peremption
        nouvelInventaireLigneTemp.stockPhysique = qte.toDouble()

        if (qte != -1)
            nouvelInventaireLigneTemp.inventaireDate = getDateDuJour()

        Inventaire_Ligne_TempOpenHelper.insererUnInventaire_Ligne_TempEnBDD(
            db,
            nouvelInventaireLigneTemp
        )
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
            db,
            Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP,
            nouvelInventaireLigneTemp.getPhiMR4UUID(),
            nouvelInventaireLigneTemp.get_UID(),
            DBOpenHelper.ActionsEAS.AJOUT
        )
        ElementASynchroniserOpenHelper.toutSynchroniser(
            this@DetailReception_V2,
            db,
            utilisateurConnecte,
            false
        )

        if (ouvrirDetail)
            ouvrirDetailFragment(nouvelInventaireLigneTemp, true)
        else {
            fermerDetailFragment()
            rafraichirListe()
            ouvrirScanner()
        }*/
    }

    private fun enregistrerPhReliquat(inventaireLigne: PH_Reliquat) {
        /*inventaireLigne.inventaireDate = getDateDuJour()
        Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(
            db,
            inventaireLigne
        )

        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
            db,
            Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP,
            inventaireLigne.getPhiMR4UUID(),
            inventaireLigne.get_UID(),
            DBOpenHelper.ActionsEAS.MAJ
        )
        ElementASynchroniserOpenHelper.toutSynchroniser(
            this@DetailReception_V2,
            db,
            utilisateurConnecte,
            false
        )

        fermerDetailFragment()
        rafraichirListe()
        ouvrirScanner()*/
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

        messageTextView.text = "Souhaitez-vous valider l'inventaire de cette zone ?"
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

    fun ajusterHauteurRecherche(hauteur: Int) {
        rechercheContainer.layoutParams =
            (rechercheContainer.layoutParams as LinearLayout.LayoutParams).also {
                it.height = if (hauteur == 0) 0 else LinearLayout.LayoutParams.WRAP_CONTENT
            }
        rechercheContainer.requestLayout()
    }
}