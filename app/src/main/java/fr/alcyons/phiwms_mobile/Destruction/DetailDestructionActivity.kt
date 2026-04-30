package fr.alcyons.phiwms_mobile.Destruction

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
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
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_DestructionAdapter
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.Destruction.Fragment.ADetruireFragment
import fr.alcyons.phiwms_mobile.ServiceActivity
import fr.alcyons.phiwms_mobile.Services.ServiceDestructionActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import java.util.Random

class DetailDestructionActivity : ServiceActivity(), RechercheFragment.OnElementRechercheListener, RechercheAdjustable
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
    private var adapter: Retour_Ligne_DestructionAdapter? = null
    private var scannerContainer: FragmentContainerView? = null
    private var rechercheContainer: FragmentContainerView? = null
    private var referenceADetruireContainer: FragmentContainerView? = null
    private var detailContainer: FragmentContainerView? = null
    private var lancerScan: LinearLayout? = null
    private var lancerRecherhe: LinearLayout? = null
    private var aDetruire_LL: LinearLayout? = null
    private var btnValiderDestruction_LL: LinearLayout? = null
    private var btnValiderDestruction_CV: CardView? = null
    private var textChercher_TV: TextView? = null
    private var searchInput_ET: EditText? = null
    private var effacerRecherche_IV: ImageView? = null
    private var actionButton: Button? = null

    // Fragments
    private var scannerFragment: Fragment? = null
    private var rechercheFragment: RechercheFragment? = null
    private var aDetruireFragment: ADetruireFragment? = null

    // State
    private var isScannerOpen: Boolean = false
    private var isSearchOpen: Boolean = false
    private var isACompterOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_detail_destruction)

        this.initializeData()
        this.initializeUI()
        this.setupEventListeners()
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
        this.adapter = Retour_Ligne_DestructionAdapter(this, this.listRetourLignes)
        
        val itemCount = this.listRetourLignes?.size ?: 0
        findViewById<TextView>(R.id.nbReferenceADetruire_TV).text = itemCount.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        super.onCreateOptionsMenu(menu)

        this.menuInflater.inflate(R.menu.menu_action, menu)
        this.configureMenuItems(menu)
        
        return true
    }
    
    private fun configureMenuItems(menu: Menu)
    {
        menu.findItem(R.id.menuSaveCircle).isVisible = true
        menu.findItem(R.id.menuCommentaire).isVisible = true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean
    {
        this.setupSaveMenuItem(menu)
        this.setupCommentMenuItem(menu)
        return true
    }
    
    private fun setupSaveMenuItem(menu: Menu)
    {
        val saveItem = menu.findItem(R.id.menuSaveCircle)
        saveItem.setOnMenuItemClickListener {
            Alerte.afficherAlerteSaisieText(this, this.layoutInflater, "Validation destruction", "Souhaitez-vous valider la destruction ?", "Ajouter un commentaire...")
            true
        }
    }
    
    private fun setupCommentMenuItem(menu: Menu)
    {
        val commentItem = menu.findItem(R.id.menuCommentaire)
        
        if (this.retourSelectionne?.commentaire?.isEmpty() == true)
        {
            commentItem.icon?.mutate()?.alpha = DetailDestructionActivity.ALPHA_DISABLED
            commentItem.setOnMenuItemClickListener(null)
        }
        else
        {
            commentItem.icon?.mutate()?.alpha = DetailDestructionActivity.ALPHA_ENABLED
            commentItem.setOnMenuItemClickListener {
                Alerte.afficherAlerteInformation(this, this.layoutInflater, "Commentaire", this.retourSelectionne?.commentaire ?: "", false, false)
                true
            }
        }
    }

    override fun retourSaisieText(text: String?)
    {
        this.commentaire = text
        this.validerDestruction()
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
            this.aDetruireFragment?.scrollToPosition(position)
        }
        else { Alerte.afficherAlerteInformation(this@DetailDestructionActivity, this.layoutInflater, "Produit non trouvé", "Ce produit n'est pas dans la liste de destruction", false, false) }
    }

    private fun validerDestruction()
    {
        val retour = this.retourSelectionne ?: return
        
        if (!validateAndSetMotif(retour)) return
        
        val actionUtilisateur = this.createActionUtilisateur()
        val successCount = this.processRetourLignes(actionUtilisateur)
        
        if (successCount == this.adapter?.mRetour_Lignes?.size)
        {
            this.updateRetourAfterValidation(retour)
            this.finalizeDestruction(actionUtilisateur)
        }
        else { this.handleDestructionError() }
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
        val dateDestruction = Date()
        val dateString = this.formatDateTime(dateDestruction)
        
        return ActionUtilisateur(actionId, this.utilisateurConnecte.id, dateString, this.serviceActuel.id, this.utilisateurConnecte.etablissementId, "En attente", this.retourSelectionne?._UID!!, "", "Destruction")
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
        retour.intitule = oldIntitule.replace(getString(R.string.DestructionDemandee), getString(R.string.DestructionEffectuee))
        retour.en_Attente_de = getString(R.string.DestructionEffectuee)
        retour.commentaire = commentaire
        
        val date = Date()
        @SuppressLint("SimpleDateFormat")
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        retour.date_retour = dateFormat.format(date)
        
        val rowId = RetourOpenHelper.mettreAJourRetour(this.db, retour)
        if (rowId != -1L) { ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, RetourOpenHelper.Constantes.TABLE_RETOUR, retour.phiMR4UUID, retour._UID, DBOpenHelper.ActionsEAS.MAJ) }
    }
    
    private fun finalizeDestruction(actionUtilisateur: ActionUtilisateur)
    {
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, actionUtilisateur.phiMR4UUID, actionUtilisateur.id, DBOpenHelper.ActionsEAS.AJOUT)
        
        Toast.makeText(this, "Destruction confirmée", Toast.LENGTH_SHORT).show()
        
        if (statutConnexion) { ElementASynchroniserOpenHelper.toutSynchroniser(this, this.db, this.utilisateurConnecte, true) }

        this.navigateToServiceDestruction()
    }
    
    private fun handleDestructionError()
    {
        Alerte.afficherAlerteInformation(this, this.layoutInflater, "Alerte", "une erreur est survenue, aucun traitement ne sera effectué", false, false)
        ElementASynchroniserOpenHelper.viderTableElementASynchroniser(this.db)
        finish()
    }
    
    private fun navigateToServiceDestruction()
    {
        val intent = Intent(this, ServiceDestructionActivity::class.java)
        intent.putExtras(super.getBundle())
        this.startActivity(intent)
        this.finish()
    }

    private fun bindViews()
    {
        this.scannerContainer = this.findViewById<FragmentContainerView?>(R.id.scannerContainer)
        this.rechercheContainer = this.findViewById<FragmentContainerView?>(R.id.rechercheContainer)
        this.referenceADetruireContainer = this.findViewById<FragmentContainerView?>(R.id.referenceADetruireContainer)
        this.detailContainer = this.findViewById<FragmentContainerView?>(R.id.detailContainer)

        this.listViewRetourLignes = this.findViewById<ListView?>(R.id.listeView)

        this.lancerScan = this.findViewById<LinearLayout?>(R.id.lancerScan)
        this.lancerRecherhe = this.findViewById<LinearLayout?>(R.id.lancerRecherhe)
        this.aDetruire_LL = this.findViewById<LinearLayout?>(R.id.aDetruire_LL)
        this.btnValiderDestruction_LL = this.findViewById<LinearLayout?>(R.id.btnValiderDestruction_LL)
        this.btnValiderDestruction_CV = this.findViewById<CardView?>(R.id.btnValiderDestruction_CV)
        this.textChercher_TV = this.findViewById<TextView?>(R.id.textChercher_TV)
        this.searchInput_ET = this.findViewById<EditText?>(R.id.searchInput_ET)
        this.effacerRecherche_IV = this.findViewById<ImageView?>(R.id.effacerRecherche_IV)

        this.actionButton = this.findViewById<Button>(R.id.boutonAction)
    }

    private fun setupEventListeners()
    {
        this.setupScannerClickListener()
        this.setupSearchClickListener()
        this.setupClearSearchClickListener()
        this.setupADetruireClickListener()
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
    
    private fun setupADetruireClickListener()
    {
        this.aDetruire_LL?.setOnClickListener {
            if (this.isACompterOpen) this.closeACompter()
            else
            {
                this.closeOpenedFragments()
                this.openACompter()
            }
        }
    }

    private fun setupActionButtonClickListener() { this.actionButton?.setOnClickListener { Alerte.afficherAlerteSaisieText(this, this.layoutInflater, "Validation destruction", "Souhaitez-vous valider la destruction ?", "Ajouter un commentaire...") } }

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
                    this@DetailDestructionActivity.openSearch()
                    this@DetailDestructionActivity.rechercheFragment?.lancerRecherche(query, "destruction", (this@DetailDestructionActivity.retourSelectionne ?: return)._UID.toString())
                }
                else { this@DetailDestructionActivity.rechercheFragment?.viderListe() }
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
        this.referenceADetruireContainer.apply {
            (this ?: return@apply).layoutParams = (this.layoutParams as LinearLayout.LayoutParams).also {
                it.height = LinearLayout.LayoutParams.WRAP_CONTENT
                it.weight = 0f
            }
            this.visibility = View.VISIBLE
            this.translationY = -resources.displayMetrics.heightPixels.toFloat()
            this.animate().translationY(0f).setDuration(300).start()
        }

        val frag = ADetruireFragment.newInstance(this.listRetourLignes as ArrayList<Retour_Ligne>).also { aDetruireFragment = it }
        this.supportFragmentManager.beginTransaction().replace(R.id.referenceADetruireContainer, frag).commitNow()

        this.isACompterOpen = true
    }

    private fun closeACompter()
    {
        (this.referenceADetruireContainer ?: return).animate().translationY(-(this.referenceADetruireContainer ?: return).height.toFloat()).setDuration(300).withEndAction {
            (this.referenceADetruireContainer ?: return@withEndAction).visibility = View.GONE
            (this.referenceADetruireContainer ?: return@withEndAction).layoutParams = ((this.referenceADetruireContainer ?: return@withEndAction).layoutParams as LinearLayout.LayoutParams).also { it.height = 0 }
            this.aDetruireFragment?.let { frag -> supportFragmentManager.beginTransaction().remove(frag).commit() }
            this.aDetruireFragment = null
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
                val detailDestructionIntent = Intent(this@DetailDestructionActivity, ServiceDestructionActivity::class.java)
                val detailDestructionBundle = super@DetailDestructionActivity.getBundle()
                detailDestructionIntent.putExtras(detailDestructionBundle)
                this@DetailDestructionActivity.startActivity(detailDestructionIntent)
                this@DetailDestructionActivity.finish()
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
