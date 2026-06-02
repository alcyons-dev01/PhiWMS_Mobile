package fr.alcyons.phiwms_mobile.IdentificationParScan

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Produit_IdentificationOpenHelper
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Classes.Produit_Identification
import fr.alcyons.phiwms_mobile.Fragment.ScannerFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerInputFragment
import fr.alcyons.phiwms_mobile.Interfaces.ScannerControllable
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.ServiceActivity
import fr.alcyons.phiwms_mobile.Services.ServiceIdentificationParScanActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.graphics.drawable.toDrawable

class DetailIdentificationParScan : ServiceActivity() {

    // ── Tier 1 ──────────────────────────────────────────────────
    private lateinit var scannerContainer: FragmentContainerView
    private var scannerFragment: ScannerControllable? = null

    // ── Tier 2 — Fiche produit ───────────────────────────────────
    private lateinit var barreDeTitre: CardView
    private lateinit var nomProduit: TextView
    private lateinit var referenceFournisseur: TextView
    private lateinit var nomFournisseur: TextView
    private lateinit var zoneCategorie: LinearLayout
    private lateinit var categorie: TextView
    private lateinit var separateurCategorie: View
    private lateinit var zoneClassATC: LinearLayout
    private lateinit var classATC: TextView
    private lateinit var separateurATC: View
    private lateinit var zoneDCI: LinearLayout
    private lateinit var dci: TextView
    private lateinit var condAchat: TextView

    private lateinit var condDistrib: TextView


    // ── Zone action ──────────────────────────────────────────────
    private lateinit var zoneAction: LinearLayout
    private lateinit var codeScanne: TextView
    private lateinit var btnAnnulerCode: ImageButton
    private lateinit var btnCarton: LinearLayout
    private lateinit var btnUnitaire: LinearLayout
    private lateinit var labelCarton: TextView
    private lateinit var labelUnitaire: TextView
    private lateinit var btnValiderCode: LinearLayout

    // ── Tier 3 — Liste codes ─────────────────────────────────────
    private lateinit var listeCodesIdentification: RecyclerView
    private lateinit var identificationAdapter: IdentificationAdapter

    // ── État ─────────────────────────────────────────────────────
    private var scannerProcessing = false
    private var alerteVisible = false
    private var estCarton = true
    private lateinit var produitCourant: Produit

    private var itemEnAttenteSupression: Produit_Identification? = null
    private var positionEnAttenteSupression: Int = -1
    private var typecodeidentification: String? = ""

    // ────────────────────────────────────────────────────────────
    // Cycle de vie
    // ────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_identification_par_scan)

        bindViews()
        setupZoneAction()
        setupListeIdentifications()
        ouvrirScanner()

        produitCourant = ProduitOpenHelper.getProduitByID(
            db,
            intent.extras?.getInt("produitSelectionneID") ?: 0
        )

        afficherProduit(produitCourant)
        chargerIdentificationsExistantes()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                retourService(this@DetailIdentificationParScan.bundle)
            }
        })
    }

    // ────────────────────────────────────────────────────────────
    // Liaison des vues
    // ────────────────────────────────────────────────────────────

    private fun bindViews() {
        scannerContainer         = findViewById(R.id.scannerContainer)
        barreDeTitre             = findViewById(R.id.barreDeTitre)
        nomProduit               = findViewById(R.id.nomProduit)
        referenceFournisseur     = findViewById(R.id.referenceFournisseur)
        nomFournisseur           = findViewById(R.id.nomFournisseur)
        zoneCategorie            = findViewById(R.id.zoneCategorie)
        categorie                = findViewById(R.id.categorie)
        separateurCategorie      = findViewById(R.id.separateurCategorie)
        zoneClassATC             = findViewById(R.id.zoneClassATC)
        classATC                 = findViewById(R.id.classATC)
        separateurATC            = findViewById(R.id.separateurATC)
        zoneDCI                  = findViewById(R.id.zoneDCI)
        dci                      = findViewById(R.id.dci)
        zoneAction               = findViewById(R.id.zoneAction_LL)
        codeScanne               = findViewById(R.id.codeScanné_TV)
        btnAnnulerCode           = findViewById(R.id.btnAnnulerCode)
        btnCarton                = findViewById(R.id.btnCarton)
        btnUnitaire              = findViewById(R.id.btnUnitaire)
        labelCarton              = findViewById(R.id.labelCarton)
        labelUnitaire            = findViewById(R.id.labelUnitaire)
        btnValiderCode           = findViewById(R.id.btnValiderCode)
        listeCodesIdentification = findViewById(R.id.listeCodesIdentification_RV)
        condAchat                = findViewById(R.id.condAchat)
        condDistrib              = findViewById(R.id.condDistrib)
    }

    // ────────────────────────────────────────────────────────────
    // Tier 3 — RecyclerView
    // ────────────────────────────────────────────────────────────

    private fun setupListeIdentifications() {
        identificationAdapter = IdentificationAdapter(
            items = mutableListOf(),
            onSupprimerClick = { item, position -> supprimerIdentification(item, position) }
        )
        listeCodesIdentification.apply {
            layoutManager = LinearLayoutManager(this@DetailIdentificationParScan)
            adapter = identificationAdapter
        }
    }

    /**
     * Charge depuis la BDD les identifications déjà enregistrées
     * pour le produit courant et les injecte dans l'adapter.
     */
    private fun chargerIdentificationsExistantes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val liste: List<Produit_Identification> =
                Produit_IdentificationOpenHelper.getIdentificationsByCodeProduit(
                    db,
                    produitCourant.iD_produit
                )
            withContext(Dispatchers.Main) {
                liste.forEach { identificationAdapter.ajouterItem(it) }
            }
        }
    }

    /**
     * Supprime une identification en BDD puis retire la ligne du RecyclerView.
     */
    private fun supprimerIdentification(item: Produit_Identification, position: Int) {
        // On mémorise avant d'ouvrir l'alerte
        itemEnAttenteSupression = item
        positionEnAttenteSupression = position

        Alerte.afficherAlerteConfirmation(
            this,
            layoutInflater,
            bundle,
            "Souhaitez-vous supprimer le code sélectionné",
            false,
            true,
            this
        )
    }

    override fun confirmationService() {
        val item     = itemEnAttenteSupression ?: return
        val position = positionEnAttenteSupression.takeIf { it >= 0 } ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            Produit_IdentificationOpenHelper.supprimerUneIdentificationEnBDD(db, item)
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE,item.phiwms_mobileUUID,item.phiwms_mobileUUID, DBOpenHelper.ActionsEAS.SUPPR)
            ElementASynchroniserOpenHelper.toutSynchroniser(this@DetailIdentificationParScan, db, utilisateurConnecte, false)
            withContext(Dispatchers.Main) {
                identificationAdapter.retirerItem(position)
                // Nettoyage après usage
                itemEnAttenteSupression = null
                positionEnAttenteSupression = -1
            }
        }
    }

    // ────────────────────────────────────────────────────────────
    // Zone action : toggle + annuler + valider
    // ────────────────────────────────────────────────────────────

    private fun setupZoneAction() {
        setToggle(isCarton = true)

        btnCarton.setOnClickListener   { setToggle(isCarton = true)  }
        btnUnitaire.setOnClickListener { setToggle(isCarton = false) }

        btnAnnulerCode.setOnClickListener {
            codeScanne.text = ""
            zoneAction.visibility = View.GONE
            scannerProcessing = false
        }

        btnValiderCode.setOnClickListener {
            val code = codeScanne.text.toString()
            if (code.isNotEmpty()) {
                validerCode(code, estCarton)
            }
        }
    }

    private fun setToggle(isCarton: Boolean) {
        estCarton = isCarton
        btnCarton.setBackgroundResource(
            if (isCarton) R.drawable.toggle_item_selected else android.R.color.transparent
        )
        btnUnitaire.setBackgroundResource(
            if (isCarton) android.R.color.transparent else R.drawable.toggle_item_selected
        )
        labelCarton.alpha   = if (isCarton) 1f else 0.4f
        labelUnitaire.alpha = if (isCarton) 0.4f else 1f
    }

    /**
     * Insère la nouvelle identification en BDD,
     * puis ajoute la ligne dans le RecyclerView.
     */
    private fun validerCode(code: String, isCarton: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            val natureIdentification = if (isCarton) "Carton" else "Unitaire"

            val nouvelleIdentification = Produit_Identification(
                produitCourant.iD_produit,
                code,
                typecodeidentification,
                natureIdentification,
                utilisateurConnecte.etablissementId,
                -1
            )

            val rowId = Produit_IdentificationOpenHelper.insererUneIdentificationEnBDD(
                db,
                nouvelleIdentification
            )

            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                db,
                Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE,
                nouvelleIdentification.phiwms_mobileUUID,
                nouvelleIdentification.phiwms_mobileUUID,
                DBOpenHelper.ActionsEAS.AJOUT
            )
            ElementASynchroniserOpenHelper.toutSynchroniser(this@DetailIdentificationParScan, db, utilisateurConnecte, false)


            if (rowId != -1L) {
                withContext(Dispatchers.Main) {
                    identificationAdapter.ajouterItem(nouvelleIdentification)
                    // Scroll automatique vers la nouvelle ligne
                    listeCodesIdentification.smoothScrollToPosition(
                        identificationAdapter.itemCount - 1
                    )
                    // Réinitialise la zone action
                    codeScanne.text = ""
                    zoneAction.visibility = View.GONE
                    scannerProcessing = false
                }
            } else {
                withContext(Dispatchers.Main) {
                    alerteVisible = true
                    afficherAlerteAvecCallback(
                        "Erreur",
                        "Impossible d'enregistrer le code"
                    ) { alerteVisible = false }
                }
            }
        }
    }

    // ────────────────────────────────────────────────────────────
    // Scanner
    // ────────────────────────────────────────────────────────────

    private fun ouvrirScanner() {
        scannerContainer.apply {
            layoutParams = (layoutParams as LinearLayout.LayoutParams).also {
                it.height = (300 * resources.displayMetrics.density).toInt()
                it.weight = 0f
            }
            visibility = View.VISIBLE
            translationY = -resources.displayMetrics.heightPixels.toFloat()
            animate().translationY(0f).setDuration(100).start()
        }

        val frag = choisirFragmentScanner().also { scannerFragment = it as? ScannerControllable }
        scannerFragment?.btnCloseVisible = false

        when (frag) {
            is ScannerInputFragment -> { frag.onCodeScanned = { code -> traiterCodeScanne(code) } }
            is ScannerFragment      -> { frag.onCodeScanned = { code -> traiterCodeScanne(code) } }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.scannerContainer, frag)
            .commit()
    }

    private fun choisirFragmentScanner(): Fragment {
        if (estScannerProfessionnel()) return ScannerInputFragment()
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            ScannerFragment()
        } else {
            ScannerInputFragment()
        }
    }

    private fun estScannerProfessionnel(): Boolean {
        val fabricant = Build.MANUFACTURER.uppercase()
        val modele    = Build.MODEL.uppercase()
        return fabricant.contains("ZEBRA")     ||
                fabricant.contains("HONEYWELL") ||
                modele.contains("TC")           ||
                modele.contains("MC")           ||
                modele.contains("CK")           ||
                modele.contains("CT")           ||
                modele.contains("CN")
    }

    // ────────────────────────────────────────────────────────────
    // Traitement du code scanné
    // ────────────────────────────────────────────────────────────

    private fun traiterCodeScanne(code: String) {
        if (!scannerProcessing && !alerteVisible) {
            typecodeidentification = ""
            scannerProcessing = true
            lifecycleScope.launch(Dispatchers.IO) {
                if (code.uppercase().startsWith("PHITAGPLACE")) {
                    withContext(Dispatchers.Main) {
                        alerteVisible = true
                        afficherAlerteAvecCallback(
                            "Erreur",
                            "Le code scanné n'est pas un code produit"
                        ) {
                            alerteVisible = false
                            scannerProcessing = false
                        }
                    }
                } else {
                    val resultDecoupage: HashMap<String, String> =
                        GestionCodeScanne.decoupageCode(code)
                    val codeIdentification = resultDecoupage["code"]
                    typecodeidentification = resultDecoupage["type"]
                    if(typecodeidentification == "GS1")
                        typecodeidentification = "GTIN"

                    val produitIdentifier: List<Produit> =
                        ProduitOpenHelper.getProduitsByIdentification(db, codeIdentification)

                    if (produitIdentifier.isNotEmpty() && produitIdentifier.size == 1) {
                        withContext(Dispatchers.Main) {
                            alerteVisible = true
                            afficherAlerteAvecCallback(
                                "Erreur",
                                "Le code scanné est déjà identifié"
                            ) {
                                alerteVisible = false
                                scannerProcessing = false
                            }
                        }
                    } else {
                        //on recherche dans la table de produit identification
                        val listeIdentificationExistante =
                            Produit_IdentificationOpenHelper.getProduitIdentification(
                                db,
                                codeIdentification
                            )

                        if (listeIdentificationExistante.isNotEmpty()) {
                            withContext(Dispatchers.Main) {
                                alerteVisible = true
                                afficherAlerteAvecCallback(
                                    "Erreur",
                                    "Le code scanné est déjà identifié"
                                ) {
                                    alerteVisible = false
                                    scannerProcessing = false
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                codeScanne.text = codeIdentification
                                zoneAction.visibility = View.VISIBLE
                            }
                        }

                    }
                }
            }
        }
    }

    // ────────────────────────────────────────────────────────────
    // Fiche produit (tier 2)
    // ────────────────────────────────────────────────────────────

    private fun afficherProduit(produit: Produit) {
        nomProduit.text           = produit.designation_interne ?: ""
        referenceFournisseur.text = produit.ref_fourni ?: ""
        nomFournisseur.text       = produit.fournisseur ?: ""
        condAchat.text            = produit.cond_achat.toString()
        condDistrib.text          = produit.cond_distrib.toInt().toString()

        val cat = produit.categorie
        if (cat.isNullOrEmpty()) {
            zoneCategorie.visibility       = View.GONE
            separateurCategorie.visibility = View.GONE
        } else {
            zoneCategorie.visibility       = View.VISIBLE
            separateurCategorie.visibility = View.VISIBLE
            categorie.text                 = cat
        }

        val atc = produit.classeATC
        if (atc.isNullOrEmpty()) {
            zoneClassATC.visibility  = View.GONE
            separateurATC.visibility = View.GONE
        } else {
            zoneClassATC.visibility  = View.VISIBLE
            separateurATC.visibility = View.VISIBLE
            classATC.text            = atc
        }

        val dciVal = produit.dci
        if (dciVal.isNullOrEmpty()) {
            zoneDCI.visibility = View.GONE
        } else {
            zoneDCI.visibility = View.VISIBLE
            dci.text           = dciVal
        }
    }

    // ────────────────────────────────────────────────────────────
    // Alerte
    // ────────────────────────────────────────────────────────────

    private fun afficherAlerteAvecCallback(titre: String, message: String, onDismiss: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        val layout  = layoutInflater.inflate(R.layout.alerte_information, null)

        layout.findViewById<TextView>(R.id.titre).text      = titre
        layout.findViewById<TextView>(R.id.messageFin).text = message
        builder.setView(layout)

        val alertDialog = builder.create()
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.window?.setBackgroundDrawable(
            Color.TRANSPARENT.toDrawable()
        )
        alertDialog.show()

        layout.findViewById<LinearLayout>(R.id.buttonOk).setOnClickListener {
            alertDialog.dismiss()
            onDismiss()
        }
    }

    // ────────────────────────────────────────────────────────────
    // Retour activité
    // ────────────────────────────────────────────────────────────
     override fun retourService(bundle: Bundle?) {
        val detailPreparationIntent = Intent(
            this@DetailIdentificationParScan,
            ServiceIdentificationParScanActivity::class.java
        )

        val detailPreparationBundle = super.getBundle()
        detailPreparationIntent.putExtras(detailPreparationBundle)
        this@DetailIdentificationParScan.startActivity(detailPreparationIntent)
        this@DetailIdentificationParScan.finish()
    }
}