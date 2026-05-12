package fr.alcyons.phiwms_mobile.RetourPUI

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.google.android.datatransport.runtime.firebase.transport.LogSourceMetrics
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne
import fr.alcyons.phiwms_mobile.Classes.Produit
import fr.alcyons.phiwms_mobile.Classes.Retour
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.Fragment.RechercheFragment
import fr.alcyons.phiwms_mobile.Fragment.RechercheFragment.OnElementRechercheListener
import fr.alcyons.phiwms_mobile.Fragment.ScannerFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerInputFragment
import fr.alcyons.phiwms_mobile.Interfaces.RechercheAdjustable
import fr.alcyons.phiwms_mobile.Interfaces.ScanDebounce
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.RetourPUI.Fragment.ARetournerPUIFragment
import fr.alcyons.phiwms_mobile.RetourPUI.Fragment.DetailFragment
import fr.alcyons.phiwms_mobile.RetourPUI.Fragment.RetournerPUIFragment
import fr.alcyons.phiwms_mobile.ServiceActivity
import fr.alcyons.phiwms_mobile.Services.ServiceRetourPUIActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import java.util.Random

class DetailRetourPUIActivity : ServiceActivity(), ARetournerPUIFragment.OnElementSelectionneListener, RetournerPUIFragment.OnElementSelectionneListener, OnElementRechercheListener, RechercheAdjustable
{
    companion object
    {
        private const val ANIMATION_DURATION_MS = 300
        private const val SCANNER_HEIGHT_DP = 300
    }

    // Data
    private var commentaire: String? = null
    private var retourSelectionne: Retour? = null
    private var retourLigneList: MutableList<Retour_Ligne>? = null

    // UI
    private var scannerContainer: FragmentContainerView? = null
    private var rechercheContainer: FragmentContainerView? = null
    private var referenceARetournerPUIContainer: FragmentContainerView? = null
    private var referenceRetournerPUIContainer: FragmentContainerView? = null
    private var detailContainer: FragmentContainerView? = null
    private var lancerScan: LinearLayout? = null
    private var lancerRecherche: LinearLayout? = null
    private var aRetournerPUI_LL: LinearLayout? = null
    private var retournerPUI_LL: LinearLayout? = null
    private var actionButton: AppCompatButton? = null
    private var textChercher_TV: TextView? = null
    private var searchInput_ET: EditText? = null
    private var effacerRecherche_IV: ImageView? = null

    // Fragments
    private var scannerFragment: Fragment? = null
    private var rechercheFragment: RechercheFragment? = null
    private var aRetournerPUIFragment: ARetournerPUIFragment? = null
    private var retournerPUIFragment: RetournerPUIFragment? = null
    private var detailFragment: DetailFragment? = null

    // State
    private var isScannerOpen: Boolean = false
    private var isSearchOpen: Boolean = false
    private var isARetournerOpen: Boolean = false
    private var isRetournerOpen: Boolean = false
    private var isDetailOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_detail_retour_pui)

        this.initializeData()
        this.initializeUI()
        this.setupEventListeners()
        this.lancerScan?.performClick()
        this.setupOnBackPressedCallback()
    }

    private fun initializeData()
    {
        this.retourSelectionne = RetourOpenHelper.getRetourByID(this.db, Objects.requireNonNull<Bundle?>(this.intent.extras).getInt("retourSelectionneID"))
        this.retourLigneList = ArrayList<Retour_Ligne>()
    }

    private fun initializeUI()
    {
        this.bindViews()
        this.updateUI()
    }

    private fun updateUI() { this.findViewById<TextView>(R.id.numero).text = "Numéro de retour : " + this.retourSelectionne?.numero }

    private fun bindViews()
    {
        this.scannerContainer = this.findViewById<FragmentContainerView?>(R.id.scannerContainer)
        this.rechercheContainer = this.findViewById<FragmentContainerView?>(R.id.rechercheContainer)
        this.referenceARetournerPUIContainer = this.findViewById<FragmentContainerView?>(R.id.referenceARetournerPUIContainer)
        this.referenceRetournerPUIContainer = this.findViewById<FragmentContainerView?>(R.id.referenceRetournerPUIContainer)
        this.detailContainer = this.findViewById<FragmentContainerView?>(R.id.detailContainer)

        this.lancerScan = this.findViewById<LinearLayout?>(R.id.lancerScan)
        this.lancerRecherche = this.findViewById<LinearLayout?>(R.id.lancerRecherhe)
        this.aRetournerPUI_LL = this.findViewById<LinearLayout?>(R.id.aRetournerPUI_LL)
        this.retournerPUI_LL = this.findViewById<LinearLayout?>(R.id.retournerPUI_LL)

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
        this.setupARetournerPUIClickListener()
        this.setupRetournerPUIClickListener()
        this.setupActionButtonClickListener()
    }

    private fun setupScannerClickListener()
    {
        this.lancerScan?.setOnClickListener {
            if (this.isScannerOpen) this.closeScanner()
            else
            {
                this.closeOpenedFragments()
                this.openScanner()
            }
        }
    }

    private fun setupSearchClickListener()
    {
        this.lancerRecherche?.setOnClickListener {
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

    private fun setupARetournerPUIClickListener()
    {
        this.aRetournerPUI_LL?.setOnClickListener {
            if (this.isARetournerOpen) this.closeARetourner()
            else
            {
                this.closeOpenedFragments()
                this.openARetourner()
            }
        }
    }

    private fun setupRetournerPUIClickListener()
    {
        this.retournerPUI_LL?.setOnClickListener {
            if (this.isRetournerOpen) this.closeRetourner()
            else
            {
                this.closeOpenedFragments()
                this.openRetourner()
            }
        }
    }

    private fun setupActionButtonClickListener() { this.actionButton?.setOnClickListener { Alerte.afficherAlerteSaisieText(this, this.layoutInflater, "Validation retour PUI", "Souhaitez-vous valider le retour PUI ?", "Ajouter un commentaire...") } }

    public override fun onResume()
    {
        super.onResume()
        this.invalidateOptionsMenu()

        this.loadData()
        this.updateUI()
        this.updateListView()
    }

    private fun loadData()
    {
        this.retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(this.db, this.retourSelectionne)

        for (retourLigneTemp in this.retourLigneList ?: return)
        {
            val retourLigneRetournee = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(this.db, this.retourSelectionne, retourLigneTemp.code_produit)

            if (retourLigneRetournee.isEmpty())
            {
                val produitCourant = ProduitOpenHelper.getProduitByID(this.db, retourLigneTemp.code_produit)
                if (!produitCourant.emplacement_PUI_Defaut.contentEquals("") && produitCourant.emplacement_PUI_Defaut != null) { this.creationRetourLigne(retourLigneTemp, produitCourant) }
            }
        }
    }

    private fun updateListView()
    {
        if (this.aRetournerPUIFragment != null) { (this.aRetournerPUIFragment ?: return).updateList(this.retourLigneList as ArrayList<Retour_Ligne?>? as ArrayList<Retour_Ligne>, this.retourSelectionne ?: return) }
        val lignesRetournees = this.getRetourLignesRetournees()
        if (this.retournerPUIFragment != null) { (this.retournerPUIFragment ?: return).updateList(ArrayList(lignesRetournees), this.retourSelectionne ?: return) }

        val nbRefTV = this.findViewById<TextView?>(R.id.nbReferenceARetournerPUI_TV)
        if (nbRefTV != null) { nbRefTV.text = (this.retourLigneList ?: return).size.toString() }
        val nbRetourneTV = this.findViewById<TextView?>(R.id.nbReferenceRetournerPUI_TV)
        if (nbRetourneTV != null) { nbRetourneTV.text = lignesRetournees.size.toString() }
    }

    override fun retourSaisieText(text: String?)
    {
        this.commentaire = text
        this.validerRetourPUI()
    }

    override fun retourService(bundle: Bundle?) { this.navigateToServiceRetourPUI() }

    override fun confirmationService() { Alerte.afficherAlerteSaisieText(this, this.layoutInflater, "Commentaire", "Veuillez saisir un commentaire pour ce retour PUI :", "Saisir un commentaire...") }

    private fun validerRetourPUI()
    {
        this.deleteRetourLignesBase()
        val listRetourLigneNegatif = Retour_LigneOpenHelper.getAllRetourLignesNegByRetour(this.db, this.retourSelectionne)

        val actionUtilisateur = this.createActionUtilisateur()
        this.processRetourLignesNeg(listRetourLigneNegatif, actionUtilisateur)
        this.updateRetourAfterValidation()
        this.finalizeRetourPUI(actionUtilisateur)
    }

    private fun deleteRetourLignesBase()
    {
        for (retour_ligneTemp in this.retourLigneList ?: return)
        {
            Retour_LigneOpenHelper.supprimerUnRetourLigne(this.db, retour_ligneTemp)
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retour_ligneTemp.phiMR4UUID, retour_ligneTemp._UID, DBOpenHelper.ActionsEAS.SUPPR)
        }
    }

    private fun createActionUtilisateur(): ActionUtilisateur
    {
        val actionId = this.generateNegativeRandomId()
        val dateRetourPUI = Date()
        val dateString = this.formatDateTime(dateRetourPUI)

        val resultingActionUtilisateur: ActionUtilisateur = ActionUtilisateur(actionId, this.utilisateurConnecte.id, dateString, this.serviceActuel.id, this.utilisateurConnecte.etablissementId, "En attente", (this.retourSelectionne ?: return ActionUtilisateur(0, 0, "", 0, 0, "", 0, "", ""))._UID, "", "Retour PUI")
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

    private fun processRetourLignesNeg(listRetourLigneNegatif: List<Retour_Ligne>, actionUtilisateur: ActionUtilisateur)
    {
        for (retour_ligneTemp in listRetourLigneNegatif)
        {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retour_ligneTemp.phiMR4UUID, retour_ligneTemp._UID, DBOpenHelper.ActionsEAS.AJOUT)

            this.createActionUtilisateurLigne(actionUtilisateur, retour_ligneTemp)
        }
    }

    private fun createActionUtilisateurLigne(actionUtilisateur: ActionUtilisateur, retourLigne: Retour_Ligne)
    {
        val actionLigneId = generateNegativeRandomId()
        val actionLigne = ActionUtilisateur_Ligne(actionLigneId, actionUtilisateur.id, "Retour Ligne", retourLigne._UID, "", 0, retourLigne.qte_Retourner.toInt(), retourLigne.produit_Designation)
        ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(this.db, actionLigne)

        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionLigne.phiMR4UUID, actionLigne.id, DBOpenHelper.ActionsEAS.AJOUT)
    }

    private fun updateRetourAfterValidation()
    {
        val retour = this.retourSelectionne ?: return

        var intitule = retour.intitule
        intitule = intitule.replace(getString(R.string.RetourPUIDemande), getString(R.string.RetourPUIEffectue))
        retour.intitule = intitule.trim()
        retour.en_Attente_de = getString(R.string.RetourPUIEffectue)
        retour.commentaire = this.commentaire

        val date = Date()
        @SuppressLint("SimpleDateFormat")
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        retour.date_retour = dateFormat.format(date)

        val rowID = RetourOpenHelper.mettreAJourRetour(this.db, retour)
        if (rowID != -1L) { ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, RetourOpenHelper.Constantes.TABLE_RETOUR, retour.phiMR4UUID, retour._UID, DBOpenHelper.ActionsEAS.MAJ) }
    }

    private fun finalizeRetourPUI(actionUtilisateur: ActionUtilisateur)
    {
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, actionUtilisateur.phiMR4UUID, actionUtilisateur.id, DBOpenHelper.ActionsEAS.AJOUT)

        Toast.makeText(this, "Retour PUI effectué", Toast.LENGTH_SHORT).show()

        if (statutConnexion) { ElementASynchroniserOpenHelper.toutSynchroniser(this, this.db, this.utilisateurConnecte, true) }

        this.navigateToServiceRetourPUI()
    }

    private fun navigateToServiceRetourPUI()
    {
        val intent = Intent(this, ServiceRetourPUIActivity::class.java)
        intent.putExtras(super.getBundle())
        this.startActivity(intent)
        this.finish()
    }

    private fun creationRetourLigne(retourLigneBase: Retour_Ligne, produit: Produit)
    {
        val random = Random()
        var retourLigneId = random.nextInt()
        if (retourLigneId > 0) retourLigneId *= -1

        val retourLigneCourant = Retour_Ligne(retourLigneBase)
        retourLigneCourant._UID = retourLigneId
        retourLigneCourant.retourPUI_Zone = produit.zone_PUI_Defaut
        retourLigneCourant.retourPUI_Emplacement = produit.emplacement_PUI_Defaut
        retourLigneCourant.qte_Retourner = retourLigneBase.qte_Retourner

        Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(this.db, retourLigneCourant)
    }

    override fun onElementSelectionne(retourLigne: Retour_Ligne)
    {
        if (this.isScannerOpen) { this.closeScanner() }
        if (this.isSearchOpen) { this.closeSearch() }
        this.openDetailFragment(retourLigne)
    }

    private fun openARetourner()
    {
        Log.d("DetailRetourPUIActivity", "openARetourner() - lignesRetournees.size = ${this.retourLigneList?.size}")
        for (retourLigne in this.retourLigneList ?: return) { Log.d("DetailRetourPUIActivity", "retour uid : ${retourLigne.retour_UID}, uid : ${retourLigne._UID}, lot retourner : ${retourLigne.lot_Retourner}, qte retourner : ${retourLigne.qte_Retourner}, qte demander : ${retourLigne.qte_Demander}") }

        this.referenceARetournerPUIContainer?.let { container ->
            container.apply {
                this.layoutParams = (this.layoutParams as LinearLayout.LayoutParams).also {
                    it.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    it.weight = 0f
                }
                this.visibility = View.VISIBLE
                this.translationY = -resources.displayMetrics.heightPixels.toFloat()
                this.animate().translationY(0f).setDuration(ANIMATION_DURATION_MS.toLong()).start()
            }

            val frag = ARetournerPUIFragment.newInstance(this.retourLigneList as ArrayList<Retour_Ligne?>? as ArrayList<Retour_Ligne>, this.retourSelectionne ?: return).also { aRetournerPUIFragment = it }
            this.supportFragmentManager.beginTransaction().replace(R.id.referenceARetournerPUIContainer, frag).commitNow()

            // Scroll to the container
            val scrollView = findViewById<NestedScrollView>(R.id.scrollView)
            scrollView.post { scrollView.smoothScrollTo(0, container.top) }
        }

        this.isARetournerOpen = true
    }

    private fun closeARetourner()
    {
        (this.referenceARetournerPUIContainer ?: return).animate().translationY(-(this.referenceARetournerPUIContainer ?: return).height.toFloat()).setDuration(ANIMATION_DURATION_MS.toLong()).withEndAction {
            (this.referenceARetournerPUIContainer ?: return@withEndAction).visibility = View.GONE
            (this.referenceARetournerPUIContainer ?: return@withEndAction).layoutParams = ((this.referenceARetournerPUIContainer ?: return@withEndAction).layoutParams as LinearLayout.LayoutParams).also { it.height = 0 }
            this.aRetournerPUIFragment?.let { frag -> supportFragmentManager.beginTransaction().remove(frag).commit() }
            this.aRetournerPUIFragment = null
        }.start()

        this.isARetournerOpen = false
    }

    private fun openRetourner()
    {
        val lignesRetournees = ArrayList(this.getRetourLignesRetournees())
        if (lignesRetournees.isEmpty()) { return }

        Log.d("DetailRetourPUIActivity", "openRetourner() - lignesRetournees.size = ${lignesRetournees.size}")
        for (retourLigne in lignesRetournees) { Log.d("DetailRetourPUIActivity", "retour uid : ${retourLigne.retour_UID}, uid : ${retourLigne._UID}, lot retourner : ${retourLigne.lot_Retourner}, qte retourner : ${retourLigne.qte_Retourner}, qte demander : ${retourLigne.qte_Demander}") }

        this.referenceRetournerPUIContainer?.let { container ->
            container.apply {
                this.layoutParams = (this.layoutParams as LinearLayout.LayoutParams).also {
                    it.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    it.weight = 0f
                }
                this.visibility = View.VISIBLE
                this.translationY = -resources.displayMetrics.heightPixels.toFloat()
                this.animate().translationY(0f).setDuration(ANIMATION_DURATION_MS.toLong()).start()
            }

            val frag = RetournerPUIFragment.newInstance(lignesRetournees, this.retourSelectionne ?: return).also { this.retournerPUIFragment = it }
            this.supportFragmentManager.beginTransaction().replace(R.id.referenceRetournerPUIContainer, frag).commitNow()

            val scrollView = findViewById<NestedScrollView>(R.id.scrollView)
            scrollView.post { scrollView.smoothScrollTo(0, container.top) }
        }

        this.isRetournerOpen = true
    }

    private fun closeRetourner()
    {
        (this.referenceRetournerPUIContainer ?: return).animate().translationY(-(this.referenceRetournerPUIContainer ?: return).height.toFloat()).setDuration(ANIMATION_DURATION_MS.toLong()).withEndAction {
                (this.referenceRetournerPUIContainer ?: return@withEndAction).visibility = View.GONE
                (this.referenceRetournerPUIContainer ?: return@withEndAction).layoutParams = ((this.referenceRetournerPUIContainer ?: return@withEndAction).layoutParams as LinearLayout.LayoutParams).also { it.height = 0 }
                this.retournerPUIFragment?.let { frag -> supportFragmentManager.beginTransaction().remove(frag).commit() }
                this.retournerPUIFragment = null
            }
            .start()

        this.isRetournerOpen = false
    }

    override fun onElementRechercher(element: Int) { this.scrollToItemOrDisplayAlert(element) }

    override fun ajusterHauteurRecherche(hauteur: Int)
    {
        (this.rechercheContainer ?: return).layoutParams = ((this.rechercheContainer ?: return).layoutParams as LinearLayout.LayoutParams).also { it.height = if (hauteur == 0) 0 else LinearLayout.LayoutParams.WRAP_CONTENT }
        (this.rechercheContainer ?: return).requestLayout()
    }

    private fun scrollToItemOrDisplayAlert(idProduit: Int)
    {
        val position = (this.retourLigneList ?: emptyList()).indexOfFirst { retourLigne -> retourLigne.code_produit == idProduit }

        if (position >= 0)
        {
            if (!this.isARetournerOpen)
            {
                this.closeOpenedFragments()
                this.openARetourner()
            }
            this.aRetournerPUIFragment?.scrollToPosition(position)
        }
        else { Alerte.afficherAlerteInformation(this, this.layoutInflater, "Produit non trouvé", "Ce produit n'est pas dans la liste de retour PUI", false, false) }
    }

    private fun closeOpenedFragments()
    {
        if (this.isScannerOpen) this.closeScanner()
        if (this.isSearchOpen) this.closeSearch()
        if (this.isARetournerOpen) this.closeARetourner()
        if (this.isRetournerOpen) this.closeRetourner()
        if (this.isDetailOpen) this.closeDetailFragment()
    }

    private fun openDetailFragment(retourLigneBase: Retour_Ligne)
    {
        this.closeOpenedFragments()

        val produit = ProduitOpenHelper.getProduitByID(this.db, retourLigneBase.code_produit)
        val retourLigneEdition = this.getOrCreateEditableRetourLigne(retourLigneBase, produit)
        val maxQuantite = this.getQuantiteMaxEditable(retourLigneBase, retourLigneEdition)

        this.detailContainer?.let { container ->
            val fragment = DetailFragment.newInstance(retourLigneEdition, maxQuantite).also { this.detailFragment = it }

            fragment.onFermer = { this.closeDetailFragment() }
            fragment.onValider = { ligne ->
                Retour_LigneOpenHelper.mettreAJourUnRetourLigne(this.db, ligne)
                this.loadData()
                this.updateListView()
                this.closeDetailFragment()
                if (ligne.qte_Retourner > 0) { if (!this.isRetournerOpen) { this.openRetourner() } }
                else if (this.isRetournerOpen && this.getRetourLignesRetournees().isEmpty()) { this.closeRetourner() }
                if (!this.isARetournerOpen) { this.openARetourner() }
                this.aRetournerPUIFragment?.scrollToPosition((this.retourLigneList ?: emptyList()).indexOfFirst { it.code_produit == retourLigneBase.code_produit })
            }

            this.supportFragmentManager.beginTransaction().replace(R.id.detailContainer, fragment).commitNow()

            container.visibility = View.VISIBLE
            container.translationY = container.height.toFloat().takeIf { it > 0f } ?: 600f
            container.animate().translationY(0f).setDuration(ANIMATION_DURATION_MS.toLong()).start()
        }

        this.isDetailOpen = true
    }

    private fun closeDetailFragment()
    {
        val container = this.detailContainer ?: return
        container.animate().translationY(container.height.toFloat().takeIf { it > 0f } ?: 600f).setDuration(ANIMATION_DURATION_MS.toLong()).withEndAction {
                container.visibility = View.GONE
                this.detailFragment?.let { frag -> this.supportFragmentManager.beginTransaction().remove(frag).commit() }
                this.detailFragment = null
            }
            .start()

        this.isDetailOpen = false
    }

    private fun getOrCreateEditableRetourLigne(retourLigneBase: Retour_Ligne, produit: Produit): Retour_Ligne
    {
        val retourLignesNegatives = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(this.db, this.retourSelectionne ?: return retourLigneBase, retourLigneBase.code_produit)

        if (retourLignesNegatives.isNotEmpty())
        {
            val emplacementCourant = retourLigneBase.retourPUI_Emplacement
            return retourLignesNegatives.firstOrNull { !emplacementCourant.isNullOrEmpty() && it.retourPUI_Emplacement == emplacementCourant } ?: retourLignesNegatives.first()
        }

        this.creationRetourLigne(retourLigneBase, produit)
        return Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(this.db, this.retourSelectionne ?: return retourLigneBase, retourLigneBase.code_produit).first()
    }

    private fun getQuantiteMaxEditable(retourLigneBase: Retour_Ligne, retourLigneEdition: Retour_Ligne): Int
    {
        val retourLignesNegatives = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(this.db, this.retourSelectionne ?: return retourLigneBase.qte_avant_retour.toInt(), retourLigneBase.code_produit)

        val quantiteAutresLignes = retourLignesNegatives.filter { it._UID != retourLigneEdition._UID }.sumOf { it.qte_Retourner.toInt() }

        return (retourLigneBase.qte_avant_retour.toInt() - quantiteAutresLignes).coerceAtLeast(0)
    }

    private fun getRetourLignesRetournees(): List<Retour_Ligne> { return Retour_LigneOpenHelper.getAllRetourLignesNegByRetour(this.db, this.retourSelectionne ?: return emptyList()).filter { it.qte_Retourner > 0 } }

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
                model.contains("TC") ||
                model.contains("MC") ||
                model.contains("CK") ||
                model.contains("CT") ||
                model.contains("CN")
    }

    private fun hasCamera(): Boolean { return this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) }

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

    private fun handleScannedCode(scannedCode: String)
    {
        val resultDecoupage: HashMap<String, String> = GestionCodeScanne.decoupageCode(scannedCode)
        val codeIdentification = resultDecoupage["code"] ?: ""

        if (codeIdentification.isNotEmpty())
        {
            val produits = ProduitOpenHelper.getProduitsByIdentification(this.db, codeIdentification)

            if (produits.isNotEmpty())
            {
                val produitId = produits.first().iD_produit
                this.scrollToItemOrDisplayAlert(produitId)
            }
            else { Alerte.afficherAlerteInformation(this, this.layoutInflater, "Produit non trouvé", "Aucun produit trouvé pour le code scanné: $codeIdentification", false, false) }
        }
        else { Alerte.afficherAlerteInformation(this, this.layoutInflater, "Code non reconnu", "Le code scanné n'a pas pu être analysé: $scannedCode", false, false) }
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
                    this@DetailRetourPUIActivity.openSearch()
                    this@DetailRetourPUIActivity.rechercheFragment?.lancerRecherche(query, "retourPUI", (this@DetailRetourPUIActivity.retourSelectionne ?: return)._UID.toString())
                }
                else { this@DetailRetourPUIActivity.rechercheFragment?.viderListe() }
            }
        })
    }

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
            this.animate().translationY(0f).setDuration(ANIMATION_DURATION_MS.toLong()).start()
        }

        val frag = RechercheFragment().also { rechercheFragment = it }
        this.supportFragmentManager.beginTransaction().replace(R.id.rechercheContainer, frag).commitNow()

        this.isSearchOpen = true
    }

    private fun closeSearch()
    {
        this.hideSearchInput()

        this.findViewById<NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = true
        (this.rechercheContainer ?: return).animate().translationY(-(this.rechercheContainer ?: return).height.toFloat()).setDuration(ANIMATION_DURATION_MS.toLong()).withEndAction {
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

    private fun setupOnBackPressedCallback()
    {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (this@DetailRetourPUIActivity.isDetailOpen) {
                    this@DetailRetourPUIActivity.closeDetailFragment()
                } else {
                    Alerte.afficherAlerteConfirmation(this@DetailRetourPUIActivity, this@DetailRetourPUIActivity.layoutInflater, getBundle(), "Voulez-vous quitter le détail du retour PUI ?", true, false, this@DetailRetourPUIActivity)
                }
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }
}
