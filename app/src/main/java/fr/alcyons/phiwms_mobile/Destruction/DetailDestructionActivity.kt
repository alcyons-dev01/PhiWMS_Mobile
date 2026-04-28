package fr.alcyons.phiwms_mobile.Destruction

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_RetourMotifOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat
import fr.alcyons.phiwms_mobile.Classes.Retour
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne
import fr.alcyons.phiwms_mobile.Fragment.RechercheFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerInputFragment
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_DestructionAdapter
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.Reception.Fragment.AReceptionnerFragment
import fr.alcyons.phiwms_mobile.Reception.Fragment.DetailFragment
import fr.alcyons.phiwms_mobile.ServiceActivity
import fr.alcyons.phiwms_mobile.Services.ServiceDestructionActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import java.util.Random
import androidx.core.view.isVisible

class DetailDestructionActivity : ServiceActivity()
{
    // OTHERS
    private var commentaire: String? = null
    private var retourSelectionne: Retour? = null
    private var listRetourLignes: MutableList<Retour_Ligne?>? = null

    // UI
    private var listViewRetourLignes: ListView? = null
    private var adapter: Retour_Ligne_DestructionAdapter? = null

    // TMP
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

    private var scannerFragment: Fragment? = null
    private var rechercheFragment: RechercheFragment? = null
    private var aReceptionnerFragment: AReceptionnerFragment? = null
    private var detailFragment: DetailFragment? = null
    private var isScannerOpen: Boolean = false
    private var isSearchOpen: Boolean = false
    private var isACompterOpen: Boolean = false
    private var isDetailOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_detail_destruction)

        // Récupération des variables globales
        this.retourSelectionne = RetourOpenHelper.getRetourByID(this.db, Objects.requireNonNull<Bundle?>(this.intent.getExtras()).getInt("retourSelectionneID"))

        this.bindViews()
        this.setListeners()

        // Affichage des constantes
        (this.findViewById<View?>(R.id.numero) as TextView).setText(this.retourSelectionne!!.getNumero().trim { it <= ' ' })

        // Gestion de la ListView
        this.listViewRetourLignes = this.findViewById<ListView?>(R.id.listeView)
        //listViewRetourLignes.setDivider(footer);
        this.listViewRetourLignes!!.setItemsCanFocus(true)

        this.getOnBackPressedDispatcher().addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed()
            {
                val detailDestructionIntent = Intent(this@DetailDestructionActivity, ServiceDestructionActivity::class.java)
                val detailDestructionBundle = super@DetailDestructionActivity.getBundle()
                detailDestructionIntent.putExtras(detailDestructionBundle)
                this@DetailDestructionActivity.startActivity(detailDestructionIntent)
                this@DetailDestructionActivity.finish()
            }
        })
    }

    public override fun onResume()
    {
        super.onResume()
        this.invalidateOptionsMenu()

        // Récupération en BDD locale de la liste des retourLignes
        this.listRetourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(this.db, this.retourSelectionne)
        this.adapter = Retour_Ligne_DestructionAdapter(this@DetailDestructionActivity, this.listRetourLignes)
        this.listViewRetourLignes!!.setAdapter(this.adapter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        super.onCreateOptionsMenu(menu)

        val inflater = this.getMenuInflater()
        inflater.inflate(R.menu.menu_action, menu)
        menu.findItem(R.id.menuSaveCircle).setVisible(true)
        menu.findItem(R.id.menuCommentaire).setVisible(true)

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean
    {
        val item = menu.findItem(R.id.menuSaveCircle)
        item.setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener { item1: MenuItem? ->
            Alerte.afficherAlerteSaisieText(
                this@DetailDestructionActivity,
                this.getLayoutInflater(),
                "Validation destruction",
                "Souhaitez-vous valider la destruction ?",
                "Ajouter un commentaire...")

            true
        })

        val item_commentaire = menu.findItem(R.id.menuCommentaire)

        if (this.retourSelectionne!!.getCommentaire().contentEquals(""))
        {
            item_commentaire.getIcon()!!.mutate().setAlpha(50)
            item_commentaire.setOnMenuItemClickListener(null)
        }
        else
        {
            item_commentaire.getIcon()!!.mutate().setAlpha(255)
            item_commentaire.setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener { item1: MenuItem? ->
                Alerte.afficherAlerteInformation(
                    this@DetailDestructionActivity,
                    this.getLayoutInflater(),
                    "Commentaire",
                    this.retourSelectionne!!.getCommentaire(),
                    false,
                    false)

                true
            })
        }

        return true
    }

    override fun retourSaisieText(text: String?)
    {
        this.commentaire = text
        this.validerDestruction()
    }

    private fun validerDestruction()
    {
        var compteurReussite = 0

        //MAJ du User qui as mis à jour le retour
        this.retourSelectionne!!.setSYS_USER_MAJ(this.utilisateurConnecte.getIdentifiant())

        var motif = this.retourSelectionne!!.getMotif()
        if (motif.contentEquals(""))
        {
            val ph_retourMotifListe = PH_RetourMotifOpenHelper.getAllPH_RetourMotif(this.db)
            val retourMotifStringList: MutableList<String?> = ArrayList<String?>()
            for (ph_retourMotif in ph_retourMotifListe) { retourMotifStringList.add(ph_retourMotif.getMotifRetour()) }
            motif = Alerte.afficherAlerteListView(
                this@DetailDestructionActivity,
                "Sélectionner le motif",
                retourMotifStringList)
        }

        if (null == motif)
        {
            Alerte.afficherAlerteInformation(
                this@DetailDestructionActivity,
                this.getLayoutInflater(),
                "Alerte",
                "Motif invalide",
                false,
                false)
            return
        }

        this.retourSelectionne!!.setMotif(motif.trim { it <= ' ' })

        val random = Random()
        var actionId = random.nextInt()
        if (0 < actionId) actionId = actionId * -1

        @SuppressLint("SimpleDateFormat") val parseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateDestruction = Date()
        val date_string = parseFormat.format(dateDestruction)

        val new_action_utilisateur = ActionUtilisateur(
            actionId,
            this.utilisateurConnecte.getId(),
            date_string,
            this.serviceActuel.getId(),
            this.utilisateurConnecte.getEtablissementId(),
            "En attente",
            this.retourSelectionne!!.get_UID(),
            "",
            "Destruction")
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(this.db, new_action_utilisateur)
        for (retourLigne in this.adapter!!.mRetour_Lignes)
        {
            retourLigne.setQte_Retourner(if (0.0 == retourLigne.getQte_Retourner()) retourLigne.getQte_Demander() else retourLigne.getQte_Retourner())

            val rowID = Retour_LigneOpenHelper.mettreAJourUnRetourLigne(this.db, retourLigne)
            if (-1L != rowID)
            {
                compteurReussite++
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                    this.db,
                    Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE,
                    retourLigne.getPhiMR4UUID(),
                    retourLigne.get_UID(),
                    DBOpenHelper.ActionsEAS.MAJ)
            }

            val randomactionligne = Random()
            var actionligneId = randomactionligne.nextInt()
            if (0 < actionligneId) actionligneId = actionligneId * -1

            val actionUtilisateur_ligne = ActionUtilisateur_Ligne(
                actionligneId,
                new_action_utilisateur.getId(),
                "Retour Ligne",
                retourLigne.get_UID(),
                "",
                0,
                retourLigne.getQte_Retourner().toInt(),
                retourLigne.getProduit_Designation()
            )
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(
                this.db,
                actionUtilisateur_ligne
            )
        }

        // Si tous les retoursLignes ont bien été mis à jour, on met à jour le retour
        if (compteurReussite == this.adapter!!.mRetour_Lignes.size)
        {
            val intitule = this.retourSelectionne!!.getIntitule()
            this.retourSelectionne!!.setIntitule(intitule.replace(this.getString(R.string.DestructionDemandee), this.getString(R.string.DestructionEffectuee)))
            this.retourSelectionne!!.setEn_Attente_de(this.getString(R.string.DestructionEffectuee))
            this.retourSelectionne!!.setCommentaire(this.commentaire)

            val date = Date()
            @SuppressLint("SimpleDateFormat") val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            this.retourSelectionne!!.setDate_retour(dateFormat.format(date))


            val rowID = RetourOpenHelper.mettreAJourRetour(this.db, this.retourSelectionne)
            if (-1L != rowID)
            {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                    this.db,
                    RetourOpenHelper.Constantes.TABLE_RETOUR,
                    this.retourSelectionne!!.getPhiMR4UUID(),
                    this.retourSelectionne!!.get_UID(),
                    DBOpenHelper.ActionsEAS.MAJ)
            }
            else { compteurReussite = 0 }
        }

        // Si une erreur est survenue, on annule tout
        if (compteurReussite != this.adapter!!.mRetour_Lignes.size)
        {
            Alerte.afficherAlerteInformation(
                this@DetailDestructionActivity,
                this.getLayoutInflater(),
                "Alerte",
                "une erreur est survenue, aucun traitement ne sera effectué",
                false,
                false)
            ElementASynchroniserOpenHelper.viderTableElementASynchroniser(this.db)
            this@DetailDestructionActivity.finish()

            return
        }

        // Si possible, on tente de tout mettre à jour en BDD distante directement
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
            this.db,
            ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR,
            new_action_utilisateur.getPhiMR4UUID(),
            new_action_utilisateur.getId(),
            DBOpenHelper.ActionsEAS.AJOUT)
        Toast.makeText(this@DetailDestructionActivity, "Destruction effectuée", Toast.LENGTH_SHORT).show()
        if (statutConnexion)
        {
            ElementASynchroniserOpenHelper.toutSynchroniser(
                this@DetailDestructionActivity,
                this.db,
                this.utilisateurConnecte,
                true)
        }

        val detailDestructionIntent = Intent(this@DetailDestructionActivity, ServiceDestructionActivity::class.java)
        val detailDestructionBundle = super@DetailDestructionActivity.getBundle()
        detailDestructionIntent.putExtras(detailDestructionBundle)
        this@DetailDestructionActivity.startActivity(detailDestructionIntent)
        this@DetailDestructionActivity.finish()
    }

    private fun bindViews()
    {
        this.scannerContainer = this.findViewById<FragmentContainerView?>(R.id.scannerContainer)

        this.rechercheContainer = this.findViewById<FragmentContainerView?>(R.id.rechercheContainer)
        this.referenceADetruireContainer = this.findViewById<FragmentContainerView?>(R.id.referenceADetruireContainer)
        this.detailContainer = this.findViewById<FragmentContainerView?>(R.id.detailContainer)

        this.lancerScan = this.findViewById<LinearLayout?>(R.id.lancerScan)
        this.lancerRecherhe = this.findViewById<LinearLayout?>(R.id.lancerRecherhe)
        this.aDetruire_LL = this.findViewById<LinearLayout?>(R.id.aDetruire_LL)
        this.btnValiderDestruction_LL = this.findViewById<LinearLayout?>(R.id.btnValiderDestruction_LL)
        this.btnValiderDestruction_CV = this.findViewById<CardView?>(R.id.btnValiderDestruction_CV)
        this.textChercher_TV = this.findViewById<TextView?>(R.id.textChercher_TV)
        this.searchInput_ET = this.findViewById<EditText?>(R.id.searchInput_ET)
        this.effacerRecherche_IV = this.findViewById<ImageView?>(R.id.effacerRecherche_IV)
    }

    private fun setListeners()
    {
        (this.lancerScan ?: return).setOnClickListener {
            if (this.isScannerOpen) { this.closeScanner() }
            else
            {
                this.closeOpenedFragments()
                this.openScanner()
            }
        }

        (this.lancerRecherhe ?: return).setOnClickListener {
            if (this.isSearchOpen) { this.closeSearch() }
            else
            {
                this.closeOpenedFragments()
                this.showSearchInput()
            }
        }

        (this.effacerRecherche_IV ?: return).setOnClickListener {
            (this.searchInput_ET ?: return@setOnClickListener).setText("")
            this.closeSearch()
        }

        (this.aDetruire_LL ?: return).setOnClickListener {
            if (this.isACompterOpen) { this.closeACompter() }
            else
            {
                this.closeOpenedFragments()
                this.openACompter()
            }
        }
    }


    private fun choisirFragmentScanner(): Fragment
    {
        // Vérifie si c'est un Zebra ou Honeywell
        if (this.estScannerProfessionnel()) { return ScannerInputFragment() }

        // Vérifie si l'appareil a une caméra
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { ScannerFragment() }
        else { ScannerInputFragment() }
    }

    private fun estScannerProfessionnel(): Boolean
    {
        val fabricant = Build.MANUFACTURER.uppercase()
        val modele = Build.MODEL.uppercase()
        return fabricant.contains("ZEBRA") ||
                fabricant.contains("HONEYWELL") ||
                modele.contains("TC") || // Zebra TC series
                modele.contains("MC") || // Zebra MC series
                modele.contains("CK") || // Honeywell CK series
                modele.contains("CT") || // Honeywell CT series
                modele.contains("CN")  // Honeywell CN series
    }

    private fun openScanner()
    {
        this.scannerContainer.apply {
            (this ?: return@apply).layoutParams = (this.layoutParams as LinearLayout.LayoutParams).also {
                it.height = (300 * resources.displayMetrics.density).toInt()
                it.weight = 0f
            }
            this.visibility = View.VISIBLE
            this.translationY = -resources.displayMetrics.heightPixels.toFloat()
            this.animate().translationY(0f).setDuration(300).start()
        }

        val frag = choisirFragmentScanner().also { this.scannerFragment = it }

        when (frag) {
            is ScannerInputFragment -> {
                frag.onCodeScanned = { code -> this.handleScannedCode(code) }
                frag.onCloseRequested = { closeScanner() }
            }

            is ScannerFragment -> {
                frag.onCodeScanned = { code -> this.handleScannedCode(code) }
                frag.onCloseRequested = { closeScanner() }
            }
        }

        this.supportFragmentManager.beginTransaction().replace(R.id.scannerContainer, frag).commit()

        this.isScannerOpen = true
    }

    private fun closeScanner()
    {
        (scannerContainer ?: return).animate().translationY(-(this.scannerContainer ?: return).height.toFloat()).setDuration(300).withEndAction {
            (this.scannerContainer ?: return@withEndAction).visibility = View.GONE
            (this.scannerContainer ?: return@withEndAction).layoutParams = ((this.scannerContainer ?: return@withEndAction).layoutParams as LinearLayout.LayoutParams).also { it.height = 0 }
            this.scannerFragment?.let { frag: Fragment -> this.supportFragmentManager.beginTransaction().remove(frag).commit() }
            this.scannerFragment = null
        }.start()

        this.isScannerOpen = false
    }

    internal fun openSearch()
    {
        this.findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = false
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
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(searchInput_ET, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)

        // Écoute la saisie et lance la recherche dans le fragment
        (searchInput_ET ?: return).addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?)
            {
                val query = s.toString().trim()
                if (query.isNotEmpty())
                {
                    this@DetailDestructionActivity.openSearch()
                    this@DetailDestructionActivity.rechercheFragment?.lancerRecherche(query, "reception", receptionCourant.numero)
                }
                else { this@DetailDestructionActivity.rechercheFragment?.viderListe() }
            }
        })
    }

    private fun closeSearch()
    {
        this.hideSearchInput()

        this.findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = true
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
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow((this.searchInput_ET ?: return).windowToken, 0)
    }

    private fun openACompter(idProduit: Int = 0)
    {
        var liste: ArrayList<PH_Reliquat> = arrayListOf()

        if (idProduit == 0) { liste = ArrayList(PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(this.db, receptionCourant.numero)) }
        else { liste.add(PH_ReliquatOpenHelper.getPH_ReliquatByUnIdProduitetNumero(this.db, idProduit, receptionCourant.numero)) }

        if (liste.isNotEmpty())
        {
            val frag = AReceptionnerFragment.newInstance(liste)
            this.supportFragmentManager.beginTransaction().replace(R.id.referenceAReceptionnerContainer, frag).commitNow()

            this.referenceADetruireContainer.apply {
                (this ?: return@apply).layoutParams = (this.layoutParams as LinearLayout.LayoutParams).also {
                    it.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    it.weight = 0f
                }
                this.visibility = View.VISIBLE
                this.translationY = 0f // Plus d'animation de translation
                this.alpha = 0f // Animation en fondu à la place
                this.animate().alpha(1f).setDuration(300).start()
            }

            this.isACompterOpen = true
        }
    }

    private fun closeACompter()
    {
        this.findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = true

        (this.referenceADetruireContainer ?: return).animate().translationY(-(this.referenceADetruireContainer ?: return).height.toFloat()).setDuration(300).withEndAction {
            (this.referenceADetruireContainer ?: return@withEndAction).visibility = View.GONE
            (this.referenceADetruireContainer ?: return@withEndAction).layoutParams = ((referenceADetruireContainer ?: return@withEndAction).layoutParams as LinearLayout.LayoutParams).also { it.height = 0 }
            this.aReceptionnerFragment?.let { frag -> this.supportFragmentManager.beginTransaction().remove(frag).commit() }
            this.aReceptionnerFragment = null
        }.start()

        this.isACompterOpen = false
    }

    private fun openDetailFragment(ligne: PH_Reliquat?)
    {
        this.lifecycleScope.launch(Dispatchers.Main) {
            val fragmentDejaOuvert = this@DetailDestructionActivity.detailFragment != null && (this@DetailDestructionActivity.detailContainer ?: return@launch).isVisible

            // ─── Fragment déjà visible : on met juste à jour les données ───
            if (fragmentDejaOuvert) { ligne?.let { this@DetailDestructionActivity.detailFragment?.mettreAJourLigne(it) } }
            // ─── Fragment fermé : on l'ouvre normalement ───
            else
            {
                val produit = ProduitOpenHelper.getProduitByID(db, ligne?.produitID ?: 0)
                val frag = DetailFragment.newInstance(ligne, produit).also { detailFragment = it }
                frag.onFermer = { this@DetailDestructionActivity.fermerDetailFragment() }

                frag.onValider = { ligne, ajout ->
                    if(ligne == null)
                    {
                        ElementASynchroniserOpenHelper.toutSynchroniser(this@DetailDestructionActivity, this@DetailDestructionActivity.db, this@DetailDestructionActivity.utilisateurConnecte, false)
                        fermerDetailFragment()
                        this@DetailDestructionActivity.rafraichirListe()
                        this@DetailDestructionActivity.openScanner()
                    }
                    else
                    {
                        // L'utilisateur a choisi Ajouter
                        if (ajout) { this@DetailDestructionActivity.ajouterPHReliquat(ligne) }
                        // L'utilisateur a choisi Modifier
                        else { this@DetailDestructionActivity.enregistrerPhReliquat(ligne) }
                    }

                }

                supportFragmentManager.beginTransaction().replace(R.id.detailContainer, frag).commit()

                (this@DetailDestructionActivity.detailContainer ?: return@launch).translationY = this@DetailDestructionActivity.hauteurDetailFragment.toFloat()
                (this@DetailDestructionActivity.detailContainer ?: return@launch).visibility = View.VISIBLE
                (this@DetailDestructionActivity.detailContainer ?: return@launch).animate().translationY(0f).setDuration(300).start()
            }
        }

        this.isDetailOpen = true
    }

    private fun closeDetailFragment()
    {
        this.positionSelectionnee = -1

        (this.detailContainer ?: return).animate().translationY(this.hauteurDetailFragment.toFloat()).setDuration(300).withEndAction {
            (this.detailContainer ?: return@withEndAction).visibility = View.GONE
            this.detailFragment?.also { this.supportFragmentManager.beginTransaction().remove(it).commit() }
            this.detailFragment = null
        }.start()

        this.isDetailOpen = false
    }

    private fun closeOpenedFragments()
    {
        if (this.isScannerOpen) this.closeScanner()
        if (this.isSearchOpen) this.closeSearch()
        if (this.isACompterOpen) this.closeACompter()
        if (this.isDetailOpen) this.closeDetailFragment()
    }

    private fun handleScannedCode(scannedCode: String) {}
}
