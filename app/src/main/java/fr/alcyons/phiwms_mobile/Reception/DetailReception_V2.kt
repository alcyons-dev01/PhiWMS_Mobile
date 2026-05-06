package fr.alcyons.phiwms_mobile.Reception

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne
import fr.alcyons.phiwms_mobile.Classes.Commande
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Fragment.RechercheFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerInputFragment
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.Outils.Mail
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.Reception.Adapter.DetailReceptionAdapter
import fr.alcyons.phiwms_mobile.Reception.Fragment.AReceptionnerFragment
import fr.alcyons.phiwms_mobile.Reception.Fragment.DetailFragment
import fr.alcyons.phiwms_mobile.Reception.Fragment.ReceptionnerFragment
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity
import fr.alcyons.phiwms_mobile.Services.ServiceReceptionPadActivity
import fr.alcyons.phiwms_mobile.Services.ServiceReceptionPuiActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.Random
import fr.alcyons.phiwms_mobile.Interfaces.RechercheAdjustable

class DetailReception_V2 : ServiceAvecConnexionActivity(),
    RechercheFragment.OnElementRechercheListener,
    AReceptionnerFragment.OnElementSelectionneListener,
    ReceptionnerFragment.OnElementSelectionneListener, RechercheAdjustable {

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
    var body = ""
    var bonLivraisonPhotoName = ""
    var subject = ""
    var bonLivraisonBitmap = null
    lateinit var serialisation : Serialisation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_reception_module)
        context = this
        serialisation = Serialisation(this, db, utilisateurConnecte)

        // Récupération des données de l'intent
        receptionCourant = CommandeOpenHelper.getCommandeByID(
            db, intent.extras!!.getInt("commandeID_Selectionne")
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
                fermerAReceptionner()
            } else {
                fermerFragment()
                ouvrirAReceptionner()
            }
        }

        receptionner_LL.setOnClickListener {
            if (CompterVisible) {
                fermerReceptionner()
            } else {
                fermerFragment()
                ouvrirReceptionner()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //on récupère les ph_reliquat de base
        val listeReliquatBase = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, receptionCourant.numero)

        for(reliquatBase in listeReliquatBase)
        {
            //on récupère les reliquats négatif du reliquat courant
            val listeReliquatNegByProduit = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, receptionCourant.numero, reliquatBase.produitID)
            for(reliquatNeg in listeReliquatNegByProduit)
            {
                reliquatBase.qteReliquat_X -= reliquatNeg.qteLivraison
            }

            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquatBase)
        }

        val nbReliquatTotal = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, receptionCourant.numero).size
        val nbReliquatPreparer = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumero(db, receptionCourant.numero).size
        findViewById<TextView>(R.id.nbReferenceAReceptionner_TV).text = nbReliquatTotal.toString()
        findViewById<TextView>(R.id.nbReferenceReceptionner_TV).text = nbReliquatPreparer.toString()
        findViewById<ProgressBar>(R.id.progressBarReception_PB).max = PH_ReliquatOpenHelper.getNbReliquatBaseByCommande(db, receptionCourant.numero)
        findViewById<ProgressBar>(R.id.progressBarReception_PB).progress = nbReliquatPreparer

        if(nbReliquatPreparer > 0)
            findViewById<CardView>(R.id.btnValiderReception_CV).visibility = View.VISIBLE
        else
            findViewById<CardView>(R.id.btnValiderReception_CV).visibility = View.GONE

        findViewById<CardView>(R.id.btnValiderReception_CV).setOnClickListener { v: View? ->
            demandeConfirmationValidation(layoutInflater) { resultat ->
                if (resultat)
                    if(receptionner(receptionCourant))
                        validerReception()
                    else
                        Alerte.afficherAlerteInformation(this@DetailReception_V2, layoutInflater, "Erreur", "Une erreur est survenue", false, false)
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
                    rechercheFragment?.lancerRecherche(query, "reception", receptionCourant.numero)
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
    private fun ouvrirAReceptionner(idProduit: Int = 0) {
        var liste: ArrayList<PH_Reliquat> = arrayListOf()

        if (idProduit == 0) {
            liste = ArrayList(
                PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(
                    db,
                    receptionCourant.numero
                )
            )
        } else {
            liste.add(
                PH_ReliquatOpenHelper.getPH_ReliquatBaseByUnIdProduitetNumero(
                    db,
                    idProduit,
                    receptionCourant.numero
                )
            )
        }

        if (liste.isNotEmpty()) {
            val frag = AReceptionnerFragment.newInstance(liste)
            supportFragmentManager.beginTransaction()
                .replace(R.id.referenceAReceptionnerContainer, frag)
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
        }
    }

    private fun fermerAReceptionner() {
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
    private fun ouvrirReceptionner(idProduit: Int = 0) {
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
                .replace(R.id.referenceReceptionnerContainer, frag)
                .commitNow()

            CompterVisible = true
        }
    }

    private fun fermerReceptionner() {
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
                        ElementASynchroniserOpenHelper.toutSynchroniser(this@DetailReception_V2, db, utilisateurConnecte, false)
                        fermerDetailFragment()
                        rafraichirListe()
                        ouvrirScanner()
                    }
                    else
                    {
                        if (ajout) {
                            // L'utilisateur a choisi Ajouter
                            ajouterPHReliquat(
                                ligne
                            )
                        } else {
                            // L'utilisateur a choisi Modifier
                            enregistrerPhReliquat(ligne)
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
        if (aCompterVisible) fermerAReceptionner()
        if (CompterVisible) fermerReceptionner()
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
                var datePeremptionSerialisation = ""
                if (tabDateSQL?.size == 3) {
                    datePeremptionSQL =
                        tabDateSQL[tabDateSQL.size - 1] + "-" + tabDateSQL[1] + "-" + tabDateSQL[0]

                    datePeremptionSerialisation =
                        tabDateSQL[tabDateSQL.size - 1].takeLast(2) + tabDateSQL[1] + tabDateSQL[0]
                }
                val produitIdentifier: List<Produit> =
                    ProduitOpenHelper.getProduitsByIdentification(db, codeIdentification)

                if (!produitIdentifier.isEmpty() && produitIdentifier.size == 1) {
                    val produit = produitIdentifier[0]

                    var reliquatcourant = PH_ReliquatOpenHelper.getPH_ReliquatByUnIdProduitetNumeroLotSerie(db, produit.iD_produit, receptionCourant.numero, numeroLotIdentification, numeroSerieIdentification)

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
                                Serialisation.Serialisation_Creer(utilisateurConnecte.id, "G110", codeIdentification, "GTIN", numeroLotIdentification, datePeremptionSerialisation, numeroSerieIdentification, "CDE", receptionCourant.numero).toInt()


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
                    }
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
        val nbReliquatTotal = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, receptionCourant.numero).size
        val nbReliquatPreparer = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumero(db, receptionCourant.numero).size
        findViewById<TextView>(R.id.nbReferenceAReceptionner_TV).text = nbReliquatTotal.toString()
        findViewById<TextView>(R.id.nbReferenceReceptionner_TV).text = nbReliquatPreparer.toString()
        findViewById<ProgressBar>(R.id.progressBarReception_PB).max = PH_ReliquatOpenHelper.getNbReliquatBaseByCommande(db, receptionCourant.numero)
        findViewById<ProgressBar>(R.id.progressBarReception_PB).progress = nbReliquatPreparer

        if(nbReliquatPreparer > 0)
            findViewById<CardView>(R.id.btnValiderReception_CV).visibility = View.VISIBLE
        else
            findViewById<CardView>(R.id.btnValiderReception_CV).visibility = View.GONE
    }

    @SuppressLint("SimpleDateFormat")
    private fun validerReception() {


        //on check la connexion à internet pour l'envoie du mail
        val internet: Boolean = checkInternetConnection()
        if (!internet) {
            Alerte.afficherAlerte(
                this@DetailReception_V2,
                "Erreur",
                "Aucune connexion internet détectée, aucun envoi de mail possible",
                "alerte"
            )
        } else {
            //Construction mail
            @SuppressLint("SimpleDateFormat") var dateFormat =
                SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            var dateDuJour = Date()
            var date = dateFormat.format(dateDuJour)
            val depotdest =
                DepotOpenHelper.getDepotParReference(db, receptionCourant.ref_Depot_Dest)
            var depot_destinataire = ""
            if (depotdest != null) {
                depot_destinataire = depotdest.getNom()
            }

             subject =
                "PhiWMS Mobile - " + depot_destinataire + " - " + receptionCourant.fournisseur + " - Réception PUI N°" + receptionCourant.numero + " - " + date

            if (receptionCourant.ref_Depot_Dest.contains("-PAD")) subject =
                "PhiWMS Mobile - " + depot_destinataire + " - " + receptionCourant.fournisseur + " - Réception PAD N°" + receptionCourant.numero + " - " + date

            if (bonLivraisonBitmap != null) {
                body = "Madame, Monsieur, \n \n" +
                        "La réception N°" + receptionCourant.numero+ " a été réalisée par " + utilisateurConnecte.nom + " " + utilisateurConnecte.prenom + ". \n" +
                        "Le numéro de bon de livraison saisi est le suivant : " + receptionCourant.blNumero + "\n\n" +
                        "Vous pourrez trouver ci-joint le bon de livraison. \n\n" +
                        "Cordialement, \n\n" +
                        "L'équipe Phi \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre"
            } else {
                body = "Madame, Monsieur, \n \n" +
                        "La réception N°" + receptionCourant.numero + " a été réalisée par " + utilisateurConnecte.nom + " " + utilisateurConnecte.prenom + ". \n" +
                        "Le numéro de bon de livraison saisi est le suivant : " + receptionCourant.blNumero + "\n\n" +
                        "Cordialement, \n\n" +
                        "L'équipe Phi \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre"
            }


            //Sauvegarde de la signature dans une image
            if (bonLivraisonBitmap != null) {
                dateFormat = SimpleDateFormat("yyyyMMdd")
                dateDuJour = Date()
                date = dateFormat.format(dateDuJour)

                bonLivraisonPhotoName =
                    receptionCourant.numero + "_" + date + "_ReceptionPuiBonLivraison"

                OutilsGestionPhotos.verifyStoragePermissions(this@DetailReception_V2)
            }

            // Récupération Mail Pharmacie
            var email = ParametresServeurOpenHelper.getMailPharmacie(db)
            if (utilisateurConnecte.getIdentifiant().uppercase(Locale.getDefault())
                    .contentEquals("ALCYONS")
            ) {
                email = "dev01@alcyons.fr"
            }
            afficherAlerteConfirmationMail(
                this@DetailReception_V2,
                LayoutInflater.from(this@DetailReception_V2),
                email
            )
        }
    }

    fun afficherAlerteConfirmationMail(context: Context, inflater: LayoutInflater, email: String?) {
        val builder = AlertDialog.Builder(context)
        val layout = inflater.inflate(R.layout.alerte_confirmation, null)

        val zoneok = layout.findViewById<View?>(R.id.buttonOk) as LinearLayout
        val buttonAnnuler = layout.findViewById<View?>(R.id.buttonAnnuler) as LinearLayout
        builder.setView(layout)

        val alertDialog = builder.create()
        Objects.requireNonNull<Window?>(alertDialog.getWindow()).setGravity(Gravity.CENTER)
        alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        zoneok.setOnClickListener(View.OnClickListener { v: View? ->
            alertDialog.dismiss()
            try {
                envoyerEmail(subject, body)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
        })

        buttonAnnuler.setOnClickListener(View.OnClickListener { v: View? ->
            alertDialog.dismiss()
            try {
                envoyerEmail(subject, body)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
        })
    }


    private fun getDateDuJour(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onElementRechercher(idProduit: Int) {
        fermerRecherche()
        ouvrirAReceptionner(idProduit)
        ouvrirReceptionner(idProduit)
    }

    private fun ajouterPHReliquat(
        nouveauReliquat : PH_Reliquat
    ) {
        //on regarde si un reliquat existe déjà avec ces informations
        if (receptionCourant.ref_Depot_Dest.contains("-PAD")) {
            nouveauReliquat.setZone("RECEPTION")
            nouveauReliquat.setEmplacement("RECEPTION-" + receptionCourant.numero + "-" + receptionCourant.patient_identite)
        }

        val rowID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, nouveauReliquat)
        if (rowID != -1L) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                db,
                PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT,
                nouveauReliquat.phiMR4UUID,
                nouveauReliquat.reliquat_UID,
                DBOpenHelper.ActionsEAS.AJOUT
            )
            ElementASynchroniserOpenHelper.toutSynchroniser(
                this@DetailReception_V2,
                db,
                utilisateurConnecte,
                false
            )

            fermerDetailFragment()
            rafraichirListe()
            ouvrirScanner()
        }
    }

    private fun enregistrerPhReliquat(phReliquat: PH_Reliquat) {
        PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat)
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
            db,
            PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT,
            phReliquat.phiMR4UUID,
            phReliquat.reliquat_UID,
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

        messageTextView.text = "Souhaitez-vous valider la réception de la commande ?"
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

    private fun receptionner(commande: Commande): Boolean {
        val random = Random()
        var actionId = random.nextInt()
        if (actionId > 0) actionId = actionId * -1
        @SuppressLint("SimpleDateFormat") val parseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date()
        val date_string = parseFormat.format(date)
        var new_action_utilisateur = ActionUtilisateur(
            actionId,
            utilisateurConnecte.getId(),
            date_string,
            serviceActuel.getId(),
            utilisateurConnecte.getEtablissementId(),
            "En attente",
            commande.getID_commande(),
            "",
            "Réception PUI"
        )
        if (receptionCourant.getRef_Depot_Dest().contains("-PAD")) new_action_utilisateur =
            ActionUtilisateur(
                actionId,
                utilisateurConnecte.getId(),
                date_string,
                serviceActuel.getId(),
                utilisateurConnecte.getEtablissementId(),
                "En attente",
                commande.getID_commande(),
                "",
                "Réception PAD"
            )
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur)

        val listeReliquatBase = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(
            db,
            receptionCourant.getNumero()
        )
        for (reliquat in listeReliquatBase) {
            PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquat)
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                db,
                PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT,
                reliquat!!.getPhiMR4UUID(),
                reliquat.getReliquat_UID(),
                DBOpenHelper.ActionsEAS.SUPPR
            )
        }

        val listeReliquat = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumero(
            db,
            receptionCourant.getNumero()
        )
        for (reliquatcourant in listeReliquat) {
            val randomactionligne = Random()
            var actionligneId = randomactionligne.nextInt()
            if (actionligneId > 0) actionligneId = actionligneId * -1
            val actionUtilisateur_ligne = ActionUtilisateur_Ligne(
                actionligneId,
                new_action_utilisateur.getId(),
                "PH_Reliquat",
                reliquatcourant.getReliquat_UID(),
                "",
                0,
                reliquatcourant.getQteLivraison(),
                reliquatcourant.getDesignationCourte()
            )
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(
                db,
                actionUtilisateur_ligne
            )

            PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquatcourant)
        }

        val list_serialisation = PH_SerialisationOpenHelper.getAllPH_SerialisationByMvtId(
            db,
            receptionCourant.getNumero().toString()
        )
        if (!list_serialisation.isEmpty()) {
            for (serialisation in list_serialisation) {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                    db,
                    PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION,
                    serialisation.getPhiMR4UUID(),
                    serialisation.get_UID(),
                    DBOpenHelper.ActionsEAS.AJOUT
                )
                val randomAUSeri = Random()
                var actionSerId = randomAUSeri.nextInt()
                if (actionSerId > 0) actionSerId = actionSerId * -1
                val new_action_utilisateur_serialisation = ActionUtilisateur(
                    actionSerId,
                    utilisateurConnecte.getId(),
                    date_string,
                    serviceActuel.getId(),
                    utilisateurConnecte.getEtablissementId(),
                    "En attente",
                    serialisation.get_UID(),
                    "",
                    "Serialisation"
                )
                ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(
                    db,
                    new_action_utilisateur_serialisation
                )
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                    db,
                    ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR,
                    new_action_utilisateur_serialisation.getPhiMR4UUID(),
                    new_action_utilisateur_serialisation.getId(),
                    DBOpenHelper.ActionsEAS.AJOUT
                )
            }
        }

        commande.setSituation("RM") //R = Réception, M = Mobile
        val rowID = CommandeOpenHelper.mettreAJourUneCommande(db, commande)
        if (rowID != -1L) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                db,
                CommandeOpenHelper.Constantes.TABLE_COMMANDE,
                commande.getPhiMR4UUID(),
                commande.getID_commande(),
                DBOpenHelper.ActionsEAS.MAJ
            )
            //on ajoute l'action utilisateur à synchroniser à la fin
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                db,
                ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR,
                new_action_utilisateur.getPhiMR4UUID(),
                new_action_utilisateur.getId(),
                DBOpenHelper.ActionsEAS.AJOUT
            )

            // Si possible, on essaie de mettre à jour les éléments
            ElementASynchroniserOpenHelper.toutSynchroniser(
                this@DetailReception_V2,
                db,
                utilisateurConnecte,
                true
            )
            return true
        } else {
            return false
        }
    }

    private fun envoyerEmail(subject: String, body: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val sender = Mail(
                    this@DetailReception_V2,
                    "dev01@alcyons.fr",
                    true,
                    db,
                    utilisateurConnecte
                )

                if (bonLivraisonPhotoName.contentEquals("")) {
                    sender.sendMailVerification(subject, body)
                } else {
                    sender.sendMail(
                        subject,
                        body,
                        "Documents/$bonLivraisonPhotoName.jpeg"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Toast.makeText(this@DetailReception_V2, "Réception effectuée", Toast.LENGTH_SHORT).show()
        retourService(bundle)
    }

    override fun retourService(bundle: Bundle) {
        var detailReceptionIntent =
            Intent(this@DetailReception_V2, ServiceReceptionPuiActivity::class.java)
        if (receptionCourant.ref_Depot_Dest.contains("-PAD")) detailReceptionIntent =
            Intent(this@DetailReception_V2, ServiceReceptionPadActivity::class.java)
        detailReceptionIntent.putExtras(bundle)
        this@DetailReception_V2.startActivity(detailReceptionIntent)
        this@DetailReception_V2.finish()
    }
}