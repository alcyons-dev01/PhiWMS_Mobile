package fr.alcyons.phiwms_mobile.RetourFournisseur

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_RetourMotifOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne
import fr.alcyons.phiwms_mobile.Classes.Retour
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.Fragment.RechercheFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerInputFragment
import fr.alcyons.phiwms_mobile.Interfaces.RechercheAdjustable
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_RetourFournisseurAdapter
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.RetourFournisseur.Fragment.ARetournerFournisseurFragment
import fr.alcyons.phiwms_mobile.Interfaces.ScanDebounce
import fr.alcyons.phiwms_mobile.ServiceActivity
import fr.alcyons.phiwms_mobile.Services.ServiceRetourFournisseurActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import java.util.Random

class DetailRetourFournisseurActivity : ServiceActivity(), RechercheFragment.OnElementRechercheListener, RechercheAdjustable
{
    companion object
    {
        private const val ANIMATION_DURATION_MS = 300
        private const val SCANNER_HEIGHT_DP = 300
        private const val ALPHA_DISABLED = 50
        private const val ALPHA_ENABLED = 255
    }

    // Data
    private var commentaire: String? = null
    private var retourSelectionne: Retour? = null
    private var listRetourLignes: MutableList<Retour_Ligne?>? = null

    // UI
    private var listViewRetourLignes: ListView? = null
    private var adapter: Retour_Ligne_RetourFournisseurAdapter? = null
    private var scannerContainer: FragmentContainerView? = null
    private var rechercheContainer: FragmentContainerView? = null
    private var referenceARetournerFournisseurContainer: FragmentContainerView? = null
    private var detailContainer: FragmentContainerView? = null
    private var lancerScan: LinearLayout? = null
    private var lancerRecherhe: LinearLayout? = null
    private var aRetournerFournisseur_LL: LinearLayout? = null
    private var btnValiderRetourFournisseur_LL: LinearLayout? = null
    private var btnValiderRetourFournisseur_CV: CardView? = null
    private var textChercher_TV: TextView? = null
    private var searchInput_ET: EditText? = null
    private var effacerRecherche_IV: ImageView? = null
    private var actionButton: AppCompatButton? = null

    // Fragments
    private var scannerFragment: Fragment? = null
    private var rechercheFragment: RechercheFragment? = null
    private var aRetournerFournisseurFragment: ARetournerFournisseurFragment? = null

    // State
    private var isScannerOpen: Boolean = false
    private var isSearchOpen: Boolean = false
    private var isACompterOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_detail_retour_fournisseur)

        this.initializeData()
        this.initializeUI()
        this.setupEventListeners()
        this.lancerScan?.performClick()
        this.setupOnBackPressedCallback()
    }

    private fun initializeData() { this.retourSelectionne = RetourOpenHelper.getRetourByID(this.db, Objects.requireNonNull<Bundle?>(this.intent.extras).getInt("retourSelectionneID")) }

    private fun initializeUI()
    {
        this.bindViews()
        this.updateUI()
    }

    private fun updateUI() { this.findViewById<TextView>(R.id.numero).text = "Numéro de retour : " + this.retourSelectionne?.numero?.trim() }

    public override fun onResume()
    {
        super.onResume()
        this.invalidateOptionsMenu()

        this.loadData()
        this.updateUI()
        this.updateListView()
    }

    private fun loadData() { this.listRetourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retourSelectionne) }

    private fun updateListView()
    {
        this.adapter = Retour_Ligne_RetourFournisseurAdapter(this, this.listRetourLignes)

        val itemCount = this.listRetourLignes?.size ?: 0
        findViewById<TextView>(R.id.nbReferenceARetournerFournisseur_TV).text = itemCount.toString()
    }

    override fun retourSaisieText(text: String?)
    {
        this.commentaire = text
        this.validerRetourFournisseur()
    }

    override fun onElementRechercher(element: Int) { this.scrollToItemOrDisplayAlert(element) }

    private fun scrollToItemOrDisplayAlert(idProduit: Int)
    {
        val position = (this.listRetourLignes ?: emptyList()).indexOfFirst { retourLigne -> retourLigne?.code_produit == idProduit }

        if (position >= 0)
        {
            if (!this.isACompterOpen)
            {
                this.closeOpenedFragments()
                this.openACompter()
            }
            this.aRetournerFournisseurFragment?.scrollToPosition(position)
        }
        else { Alerte.afficherAlerteInformation(this@DetailRetourFournisseurActivity, this.layoutInflater, "Produit non trouvé", "Ce produit n'est pas dans la liste de retour fournisseur", false, false) }
    }

    private fun validerRetourFournisseur()
    {
        val retour = this.retourSelectionne ?: return

        if (!validateAndSetMotif(retour)) return

        val actionUtilisateur = this.createActionUtilisateur()
        val successCount = this.processRetourLignes(actionUtilisateur)

        if (successCount == this.adapter?.mRetour_Lignes?.size)
        {
            this.updateRetourAfterValidation(retour)
            this.finalizeRetourFournisseur(actionUtilisateur)
        }
        else { this.handleRetourFournisseurError() }
    }

    private fun validateAndSetMotif(retour: Retour): Boolean
    {
        retour.syS_USER_MAJ = this.utilisateurConnecte.identifiant

        var motif = retour.motif
        if (motif.isEmpty()) { motif = this.promptUserForMotif() }

        if (motif == null)
        {
            Alerte.afficherAlerteInformation(this, this.layoutInflater, "Alerte", "Motif invalide", false, false)
            return false
        }

        retour.motif = motif.trim()
        return true
    }

    private fun promptUserForMotif(): String?
    {
        val motifList = PH_RetourMotifOpenHelper.getAllPH_RetourMotif(this.db)
        val motifStringList = motifList.map { it.motifRetour }
        return Alerte.afficherAlerteListView(this, "Sélectionner le motif", motifStringList)
    }

    private fun createActionUtilisateur(): ActionUtilisateur
    {
        val actionId = this.generateNegativeRandomId()
        val dateRetourFournisseur = Date()
        val dateString = this.formatDateTime(dateRetourFournisseur)

        val resultingActionUtilisateur: ActionUtilisateur = ActionUtilisateur(actionId, this.utilisateurConnecte.id, dateString, this.serviceActuel.id, this.utilisateurConnecte.etablissementId, "En attente", this.retourSelectionne?._UID!!, "", "Retour Frs")
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(this.db, resultingActionUtilisateur)

        return resultingActionUtilisateur
    }

    private fun generateNegativeRandomId(): Int
    {
        val random = Random()
        val id = random.nextInt()
        return if (id > 0) -id else id
    }

    private fun formatDateTime(date: Date): String
    {
        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }

    private fun processRetourLignes(actionUtilisateur: ActionUtilisateur): Int
    {
        var successCount = 0

        for (retourLigne in this.adapter?.mRetour_Lignes ?: emptyList()) { if (this.processRetourLigne(retourLigne, actionUtilisateur)) { successCount++ } }

        return successCount
    }

    private fun processRetourLigne(retourLigne: Retour_Ligne, actionUtilisateur: ActionUtilisateur): Boolean
    {
        retourLigne.qte_Retourner = if (retourLigne.qte_Retourner == 0.0) { retourLigne.qte_Demander } else { retourLigne.qte_Retourner }

        // Update retour ligne
        val rowId = Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne)
        if (rowId != -1L)
        {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigne.phiMR4UUID, retourLigne._UID, DBOpenHelper.ActionsEAS.MAJ)

            // Create action utilisateur ligne
            this.createActionUtilisateurLigne(actionUtilisateur, retourLigne)
            return true
        }

        return false
    }

    private fun createActionUtilisateurLigne(actionUtilisateur: ActionUtilisateur, retourLigne: Retour_Ligne)
    {
        val actionLigneId = generateNegativeRandomId()
        val actionLigne = ActionUtilisateur_Ligne(actionLigneId, actionUtilisateur.id, "Retour Ligne", retourLigne._UID, "", 0, retourLigne.qte_Retourner.toInt(), retourLigne.produit_Designation)
        ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(this.db, actionLigne)
    }

    private fun updateRetourAfterValidation(retour: Retour)
    {
        val oldIntitule = retour.intitule
        retour.intitule = oldIntitule.replace(getString(R.string.RetourFRSDemande), getString(R.string.RetourFRSEffectue))
        retour.en_Attente_de = getString(R.string.RetourFRSEffectue)
        retour.commentaire = commentaire

        val date = Date()
        @SuppressLint("SimpleDateFormat")
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        retour.date_retour = dateFormat.format(date)

        val rowId = RetourOpenHelper.mettreAJourRetour(this.db, retour)
        if (rowId != -1L) { ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, RetourOpenHelper.Constantes.TABLE_RETOUR, retour.phiMR4UUID, retour._UID, DBOpenHelper.ActionsEAS.MAJ) }
    }

    private fun finalizeRetourFournisseur(actionUtilisateur: ActionUtilisateur)
    {
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, actionUtilisateur.phiMR4UUID, actionUtilisateur.id, DBOpenHelper.ActionsEAS.AJOUT)

        Toast.makeText(this, "Retour fournisseur confirmé", Toast.LENGTH_SHORT).show()

        if (statutConnexion) { ElementASynchroniserOpenHelper.toutSynchroniser(this, this.db, this.utilisateurConnecte, true) }

        this.navigateToServiceRetourFournisseur()
    }

    private fun handleRetourFournisseurError()
    {
        Alerte.afficherAlerteInformation(this, this.layoutInflater, "Alerte", "une erreur est survenue, aucun traitement ne sera effectué", false, false)
        ElementASynchroniserOpenHelper.viderTableElementASynchroniser(this.db)
        finish()
    }

    private fun navigateToServiceRetourFournisseur()
    {
        val intent = Intent(this, ServiceRetourFournisseurActivity::class.java)
        intent.putExtras(super.getBundle())
        this.startActivity(intent)
        this.finish()
    }

    private fun bindViews()
    {
        this.scannerContainer = this.findViewById<FragmentContainerView?>(R.id.scannerContainer)
        this.rechercheContainer = this.findViewById<FragmentContainerView?>(R.id.rechercheContainer)
        this.referenceARetournerFournisseurContainer = this.findViewById<FragmentContainerView?>(R.id.referenceARetournerFournisseurContainer)
        this.detailContainer = this.findViewById<FragmentContainerView?>(R.id.detailContainer)

        this.listViewRetourLignes = this.findViewById<ListView?>(R.id.listeView)

        this.lancerScan = this.findViewById<LinearLayout?>(R.id.lancerScan)
        this.lancerRecherhe = this.findViewById<LinearLayout?>(R.id.lancerRecherhe)
        this.aRetournerFournisseur_LL = this.findViewById<LinearLayout?>(R.id.aRetournerFournisseur_LL)
        this.btnValiderRetourFournisseur_LL = this.findViewById<LinearLayout?>(R.id.btnValiderRetourFournisseur_LL)
        this.btnValiderRetourFournisseur_CV = this.findViewById<CardView?>(R.id.btnValiderRetourFournisseur_CV)
        this.textChercher_TV = this.findViewById<TextView?>(R.id.textChercher_TV)
        this.searchInput_ET = this.findViewById<EditText?>(R.id.searchInput_ET)
        this.effacerRecherche_IV = this.findViewById<ImageView?>(R.id.effacerRecherche_IV)

        this.actionButton = this.findViewById<AppCompatButton>(R.id.boutonAction)
    }

    private fun setupEventListeners()
    {
        this.setupScannerClickListener()
        this.setupSearchClickListener()
        this.setupClearSearchClickListener()
        this.setupARetournerFournisseurClickListener()
        this.setupActionButtonClickListener()
    }

    private fun setupScannerClickListener()
    {
        this.lancerScan?.setOnClickListener {
            if (isScannerOpen) closeScanner()
            else
            {
                this.closeOpenedFragments()
                this.openScanner()
            }
        }
    }

    private fun setupSearchClickListener()
    {
        this.lancerRecherhe?.setOnClickListener {
            if (this.isSearchOpen) this.closeSearch()
            else
            {
                closeOpenedFragments()
                showSearchInput()
            }
        }
    }

    private fun setupClearSearchClickListener()
    {
        this.effacerRecherche_IV?.setOnClickListener {
            this.searchInput_ET?.setText("")
            this.closeSearch()
        }
    }

    private fun setupARetournerFournisseurClickListener()
    {
        this.aRetournerFournisseur_LL?.setOnClickListener {
            if (this.isACompterOpen) this.closeACompter()
            else
            {
                this.closeOpenedFragments()
                this.openACompter()
            }
        }
    }

    private fun setupActionButtonClickListener() { this.actionButton?.setOnClickListener { Alerte.afficherAlerteSaisieText(this, this.layoutInflater, "Validation retour fournisseur", "Souhaitez-vous valider le retour fournisseur ?", "Ajouter un commentaire...") } }

    private fun createScannerFragment(): Fragment
    {
        return when
        {
            this.isProfessionalScanner() -> ScannerInputFragment()
            this.hasCamera() -> ScannerFragment()
            else -> ScannerInputFragment()
        }
    }

    private fun isProfessionalScanner(): Boolean
    {
        val manufacturer = Build.MANUFACTURER.uppercase()
        val model = Build.MODEL.uppercase()

        return manufacturer.contains("ZEBRA") ||
                manufacturer.contains("HONEYWELL") ||
                model.contains("TC") || // Zebra TC series
                model.contains("MC") || // Zebra MC series
                model.contains("CK") || // Honeywell CK series
                model.contains("CT") || // Honeywell CT series
                model.contains("CN")  // Honeywell CN series
    }

    private fun hasCamera(): Boolean { return this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) }


    private fun openScanner()
    {
        this.scannerContainer?.let { container ->
            this.animateContainerOpen(container, SCANNER_HEIGHT_DP)
            val fragment = this.createScannerFragment().also { this.scannerFragment = it }
            this.setupScannerFragmentCallbacks(fragment)
            this.replaceFragment(R.id.scannerContainer, fragment)
        }

        this.isScannerOpen = true
    }

    private fun animateContainerOpen(container: FragmentContainerView, heightDp: Int)
    {
        container.apply {
            this.layoutParams = (this.layoutParams as LinearLayout.LayoutParams).also {
                it.height = (heightDp * resources.displayMetrics.density).toInt()
                it.weight = 0f
            }
            this.visibility = View.VISIBLE
            this.translationY = -resources.displayMetrics.heightPixels.toFloat()
            this.animate().translationY(0f).setDuration(ANIMATION_DURATION_MS.toLong()).start()
        }
    }

    private fun setupScannerFragmentCallbacks(fragment: Fragment)
    {
        when (fragment)
        {
            is ScannerInputFragment -> {
                fragment.onCodeScanned = { code -> this.handleScannedCode(code) }
                fragment.onCloseRequested = { this.closeScanner() }
            }
            is ScannerFragment -> {
                fragment.onCodeScanned = { code -> this.handleScannedCode(code) }
                fragment.onCloseRequested = { this.closeScanner() }
            }
        }

        (fragment as ScanDebounce).setScanDebounce(750L)
    }

    private fun replaceFragment(containerId: Int, fragment: Fragment) { this.supportFragmentManager.beginTransaction().replace(containerId, fragment).commit() }

    private fun closeScanner()
    {
        this.scannerContainer?.let { container ->
            this.animateContainerClose(container) {
                this.removeFragment(this.scannerFragment)
                this.scannerFragment = null
            }
        }

        this.isScannerOpen = false
    }

    private fun animateContainerClose(container: FragmentContainerView, onComplete: () -> Unit)
    {
        container.animate().translationY(-container.height.toFloat()).setDuration(ANIMATION_DURATION_MS.toLong()).withEndAction {
            container.visibility = View.GONE
            container.layoutParams = (container.layoutParams as LinearLayout.LayoutParams).also { it.height = 0 }
            onComplete()
        }.start()
    }

    private fun removeFragment(fragment: Fragment?) { fragment?.let { frag -> this.supportFragmentManager.beginTransaction().remove(frag).commit() } }

    internal fun openSearch()
    {
        this.findViewById<NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = false
        this.rechercheContainer.apply {
            (this ?: return@apply).layoutParams = (this.layoutParams as LinearLayout.LayoutParams).also {
                it.height = LinearLayout.LayoutParams.WRAP_CONTENT
                it.weight = 0f
            }
            this.visibility = View.VISIBLE
            this.translationY = -resources.displayMetrics.heightPixels.toFloat()
            this.animate().translationY(0f).setDuration(300).start()
        }

        val frag = RechercheFragment().also { rechercheFragment = it }
        this.supportFragmentManager.beginTransaction().replace(R.id.rechercheContainer, frag).commitNow()

        this.isSearchOpen = true
    }

    private fun showSearchInput()
    {
        this.isSearchOpen = true

        // Bascule TextView → EditText dans le header
        this.findViewById<ImageView>(R.id.chevronRecherche).visibility = View.GONE
        (textChercher_TV ?: return).visibility = View.GONE
        (searchInput_ET ?: return).visibility = View.VISIBLE
        (effacerRecherche_IV ?: return).visibility = View.VISIBLE
        (searchInput_ET ?: return).requestFocus()

        // Ouvre le clavier
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchInput_ET, InputMethodManager.SHOW_IMPLICIT)

        // Écoute la saisie et lance la recherche dans le fragment
        (searchInput_ET ?: return).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?)
            {
                val query = s.toString().trim()
                if (query.isNotEmpty())
                {
                    this@DetailRetourFournisseurActivity.openSearch()
                    this@DetailRetourFournisseurActivity.rechercheFragment?.lancerRecherche(query, "retourFournisseur", (this@DetailRetourFournisseurActivity.retourSelectionne ?: return)._UID.toString())
                }
                else { this@DetailRetourFournisseurActivity.rechercheFragment?.viderListe() }
            }
        })
    }

    private fun closeSearch()
    {
        this.hideSearchInput()

        this.findViewById<NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = true
        (this.rechercheContainer ?: return).animate().translationY(-(this.rechercheContainer ?: return).height.toFloat()).setDuration(300).withEndAction {
            (this.rechercheContainer ?: return@withEndAction).visibility = View.GONE
            (this.rechercheContainer ?: return@withEndAction).layoutParams = ((this.rechercheContainer ?: return@withEndAction).layoutParams as LinearLayout.LayoutParams).also { it.height = 0 }
            this.rechercheFragment?.let { frag -> supportFragmentManager.beginTransaction().remove(frag).commit() }
            this.rechercheFragment = null
        }.start()

        this.isSearchOpen = false
    }

    private fun hideSearchInput()
    {
        this.findViewById<ImageView>(R.id.chevronRecherche).visibility = View.VISIBLE
        (textChercher_TV ?: return).visibility = View.VISIBLE
        (searchInput_ET ?: return).visibility = View.GONE
        (effacerRecherche_IV ?: return).visibility = View.GONE
        (searchInput_ET ?: return).text.clear()

        // Ferme le clavier
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow((this.searchInput_ET ?: return).windowToken, 0)
    }

    private fun openACompter()
    {
        this.referenceARetournerFournisseurContainer.apply {
            (this ?: return@apply).layoutParams = (this.layoutParams as LinearLayout.LayoutParams).also {
                it.height = LinearLayout.LayoutParams.WRAP_CONTENT
                it.weight = 0f
            }
            this.visibility = View.VISIBLE
            this.translationY = -resources.displayMetrics.heightPixels.toFloat()
            this.animate().translationY(0f).setDuration(300).start()
        }

        val frag = ARetournerFournisseurFragment.newInstance(this.listRetourLignes as ArrayList<Retour_Ligne>).also { aRetournerFournisseurFragment = it }
        this.supportFragmentManager.beginTransaction().replace(R.id.referenceARetournerFournisseurContainer, frag).commitNow()

        this.isACompterOpen = true
    }

    private fun closeACompter()
    {
        (this.referenceARetournerFournisseurContainer ?: return).animate().translationY(-(this.referenceARetournerFournisseurContainer ?: return).height.toFloat()).setDuration(300).withEndAction {
            (this.referenceARetournerFournisseurContainer ?: return@withEndAction).visibility = View.GONE
            (this.referenceARetournerFournisseurContainer ?: return@withEndAction).layoutParams = ((this.referenceARetournerFournisseurContainer ?: return@withEndAction).layoutParams as LinearLayout.LayoutParams).also { it.height = 0 }
            this.aRetournerFournisseurFragment?.let { frag -> supportFragmentManager.beginTransaction().remove(frag).commit() }
            this.aRetournerFournisseurFragment = null
        }.start()

        this.isACompterOpen = false
    }

    private fun closeOpenedFragments()
    {
        if (this.isScannerOpen) closeScanner()
        if (this.isSearchOpen) this.closeSearch()
        if (this.isACompterOpen) this.closeACompter()
    }

    private fun handleScannedCode(scannedCode: String)
    {
        val resultDecoupage: HashMap<String, String> = GestionCodeScanne.decoupageCode(scannedCode)
        val codeIdentification = resultDecoupage["code"] ?: ""

        if (codeIdentification.isNotEmpty())
        {
            // Rechercher le produit dans la base de données
            val produits = ProduitOpenHelper.getProduitsByIdentification(this.db, codeIdentification)

            // Prendre le premier produit trouvé
            if (produits.isNotEmpty())
            {
                val produitId = produits.first().iD_produit
                this.scrollToItemOrDisplayAlert(produitId)
            }
            // Aucun produit trouvé avec ce code
            else { Alerte.afficherAlerteInformation(this, this.layoutInflater, "Produit non trouvé", "Aucun produit trouvé pour le code scanné: $codeIdentification", false, false) }
        }
        // Code non reconnu
        else { Alerte.afficherAlerteInformation(this, this.layoutInflater, "Code non reconnu", "Le code scanné n'a pas pu être analysé: $scannedCode", false, false) }
    }

    private fun setupOnBackPressedCallback()
    {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed()
            {
                val detailRetourFournisseurIntent = Intent(this@DetailRetourFournisseurActivity, ServiceRetourFournisseurActivity::class.java)
                val detailRetourFournisseurBundle = super@DetailRetourFournisseurActivity.getBundle()
                detailRetourFournisseurIntent.putExtras(detailRetourFournisseurBundle)
                this@DetailRetourFournisseurActivity.startActivity(detailRetourFournisseurIntent)
                this@DetailRetourFournisseurActivity.finish()
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun ajusterHauteurRecherche(hauteur: Int)
    {
        (this.rechercheContainer ?: return).layoutParams = ((this.rechercheContainer ?: return).layoutParams as LinearLayout.LayoutParams).also { it.height = if (hauteur == 0) 0 else LinearLayout.LayoutParams.WRAP_CONTENT }
        (this.rechercheContainer ?: return).requestLayout()
    }
}
