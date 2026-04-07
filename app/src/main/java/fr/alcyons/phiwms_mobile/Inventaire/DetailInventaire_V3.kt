package fr.alcyons.phiwms_mobile.Inventaire

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.alcyons.phiwms_mobile.BaseDeDonnees.*
import fr.alcyons.phiwms_mobile.Classes.*
import fr.alcyons.phiwms_mobile.Fragment.DetailLigneFragment
import fr.alcyons.phiwms_mobile.Fragment.RechercheFragment
import fr.alcyons.phiwms_mobile.Fragment.ScannerFragment
import fr.alcyons.phiwms_mobile.Inventaire.Fragment.ACompterFragment
import fr.alcyons.phiwms_mobile.Inventaire.Fragment.CompterFragment
import fr.alcyons.phiwms_mobile.ListViewAdapters.DetailInventaireAdapter
import fr.alcyons.phiwms_mobile.ListViewAdapters.InventaireZoneAdapter
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DetailInventaire_V3 : ServiceAvecConnexionActivity(), RechercheFragment.OnElementSelectionnéListener, ACompterFragment.OnElementSelectionnéListener, CompterFragment.OnElementSelectionnéListener {

    private lateinit var context: Context
    private var inventaireCourant: Inventaire? = null
    private var inventaireLigneTempList: MutableList<Inventaire_Ligne_Temp> = mutableListOf()
    private var zoneCourante: String? = null
    private var depotCourant: Depot? = null
    private lateinit var scannerContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var rechercheContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var referenceACompterContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var referenceCompterContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var detailContainer: androidx.fragment.app.FragmentContainerView
    private lateinit var lancerScan: LinearLayout
    private lateinit var lancerRecherhe: LinearLayout
    private lateinit var aCompter_LL: LinearLayout
    private lateinit var compter_LL: LinearLayout
    private var adapter: DetailInventaireAdapter? = null
    private var valider_item: MenuItem? = null
    private var scannerFragment: ScannerFragment? = null
    private var rechercheFragment: RechercheFragment? = null
    private var aCompterFragment: ACompterFragment? = null
    private var compterFragment: CompterFragment? = null
    private var detailFragment: DetailLigneFragment? = null
    private var scannerVisible = false
    private var rechercheVisible = false
    private var aCompterVisible = false
    private var CompterVisible = false
    private var positionSelectionnee = -1
    private var hauteurDetailFragment = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_inventaire_module)
        context = this

        // Récupération des données de l'intent
        inventaireCourant = InventaireOpenHelper.getInventaireById(
            db, intent.extras!!.getInt("inventaireId")
        )
        zoneCourante  = intent.extras!!.getString("zoneSelectionne")
        depotCourant  = DepotOpenHelper.getDepotParReference(
            db, intent.extras!!.getString("depotSelectionne")
        )

        // Binding des vues
        findViewById<TextView>(R.id.zone).text     = zoneCourante

        scannerContainer    = findViewById(R.id.scannerContainer)
        rechercheContainer = findViewById(R.id.rechercheContainer)
        referenceACompterContainer = findViewById(R.id.referenceACompterContainer)
        referenceCompterContainer = findViewById(R.id.referenceCompterContainer)
        detailContainer = findViewById(R.id.detailContainer)

        lancerScan          = findViewById(R.id.lancerScan)
        lancerRecherhe          = findViewById(R.id.lancerRecherhe)
        aCompter_LL          = findViewById(R.id.aCompter_LL)
        compter_LL          = findViewById(R.id.compter_LL)

        // Dans onCreate(), après setContentView
        val frameContenu = findViewById<RelativeLayout>(R.id.frameLayout)
        frameContenu.post {
            hauteurDetailFragment = frameContenu.height / 2
        }

        lancerScan.setOnClickListener {
            if(scannerVisible)
            {
                fermerScanner()
            }
            else
            {
                fermerFragment()
                ouvrirScanner()
            }
        }

        lancerRecherhe.setOnClickListener {
            if(rechercheVisible)
            {
                fermerRecherche()
            }
            else
            {
                fermerFragment()
                ouvrirRecherche()
            }
        }

        aCompter_LL.setOnClickListener {
            if(aCompterVisible)
            {
                fermerACompter()
            }
            else
            {
                fermerFragment()
                ouvrirACompter()
            }
        }

        compter_LL.setOnClickListener {
            if(CompterVisible)
            {
                fermerCompter()
            }
            else
            {
                fermerFragment()
                ouvrirCompter()
            }
        }

        /*inventaireListView.setOnItemClickListener { _, _, position, _ ->
            positionSelectionnee = position
            adapter?.setSelectedPosition(position) // ← l'adapter gère la couleur
            onClickLigne(position)
        }*/
    }
    override fun onResume() {
        super.onResume()
        if (statutConnexion && passageParOnCreate) {
            afficherSpinner(
                this@DetailInventaire_V3,
                LayoutInflater.from(this@DetailInventaire_V3)
            )
            val requestQueue = Volley.newRequestQueue(this@DetailInventaire_V3)
            val urlRequete =
                ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteInventaireGeneral + "/" + depotCourant?.depot_Reference +"/"+ inventaireCourant!!.getInventaire_ID() + "/"+zoneCourante

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
                                this@DetailInventaire_V3,
                                getLayoutInflater(),
                                "Erreur",
                                "Votre session de connexion est invalide, veuillez vous reconnecter.",
                                false,
                                true
                            )
                        } else if (erreur == context.getString(R.string.tokenExpire)) {
                            Alerte.afficherAlerteInformation(
                                this@DetailInventaire_V3,
                                getLayoutInflater(),
                                "Erreur",
                                "Votre session de connexion est expirée, veuillez vous reconnecter.",
                                false,
                                true
                            )
                        } else if (!erreur.contentEquals("Aucun Inventaire trouvé")) {
                            Alerte.afficherAlerteInformation(
                                this@DetailInventaire_V3,
                                getLayoutInflater(),
                                "Erreur",
                                "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Général",
                                false,
                                true
                            )
                        } else {
                            arreterSpinner()
                            Alerte.afficherAlerteInformation(
                                this@DetailInventaire_V3,
                                getLayoutInflater(),
                                "Information",
                                "Aucun inventaire général à traiter",
                                false,
                                true
                            )
                        }
                    } else {
                        Inventaire_Ligne_TempOpenHelper.supprimerTousLesInventaireLigneTempsParInventaireDepotZone(db, inventaireCourant?.inventaire_ID ?: 0, zoneCourante, depotCourant)
                        val arrayInventaireLigneTemp = response.getJSONArray("InventaireLigneTemp")

                        for (i in 0 until arrayInventaireLigneTemp.length())
                        {
                            val ligne = arrayInventaireLigneTemp.getJSONObject(i)
                            val inventaireLigne = Inventaire_Ligne_Temp(ligne)
                            Inventaire_Ligne_TempOpenHelper.insererUnInventaire_Ligne_TempEnBDD(db, inventaireLigne)
                        }

                        arreterSpinner()
                        rafraichirListe()
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
                    this@DetailInventaire_V3,
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
        super.onBackPressed()
        val intent = Intent(
            this,
            InventaireZoneActivity::class.java
        )
        intent.putExtras(Bundle().apply {
            putInt("utilisateurConnecteID", utilisateurConnecte.getId())
            putInt("inventaireId", inventaireCourant?.inventaire_ID ?: 0)
            putInt("depotId", depotCourant?.depot_UID?:0)
        })

        startActivity(intent)
        finish()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_action, menu)
        valider_item = menu.findItem(R.id.menuSaveCircle).apply { isVisible = true }
        verificationEtatInventaire()
        return true
    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.prepareOptionsMenu(
            menu, adapter, null,
            "Désignation référence, inventaire non complet,..."
        )

        valider_item?.setOnMenuItemClickListener {
            validerInventaire()
            true
        }
        return true
    }

    override fun onElementSelectionne(element: Inventaire_Ligne_Temp) {
        // C'est ici que tu ouvres le second fragment dans son conteneur
        fermerFragment()
        ouvrirDetailFragment(element)
    }

    /**
     * SCANNER
     */
    private fun ouvrirScanner() {
        //fermerDetailFragment()
        // Rend le container visible avec weight = 1 (1/3 de l'écran selon votre layout)
        scannerContainer.apply {
            layoutParams = (layoutParams as LinearLayout.LayoutParams).also {
                it.height = (300 * resources.displayMetrics.density).toInt() // 300dp en pixels
                it.weight = 0f // on n'utilise pas le weight
            }
            visibility = View.VISIBLE
            translationY = -resources.displayMetrics.heightPixels.toFloat()
            animate().translationY(0f).setDuration(300).start()
        }

        val frag = ScannerFragment().also { scannerFragment = it }

        frag.onCodeScanned = { code ->
            traiterCodeScanne(code)
        }

        frag.onCloseRequested = {
            fermerScanner()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.scannerContainer, frag)
            .commit()

        scannerVisible = true
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
                scannerFragment?.let { frag: ScannerFragment ->
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
        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = false
        rechercheContainer.apply {
            layoutParams = (layoutParams as LinearLayout.LayoutParams).also {
                it.height = (300 * resources.displayMetrics.density).toInt() // 300dp en pixels
                it.weight = 0f // on n'utilise pas le weight
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
        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = true
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

    /**
     * A COMPTER
     */
    private fun ouvrirACompter()
    {
        val liste = ArrayList(
            Inventaire_Ligne_TempOpenHelper
                .getInventaireLigneTempACompterByInventaireEtZoneEtDepotInventorie(
                    db,
                    inventaireCourant!!.getInventaire_ID(),
                    zoneCourante,
                    depotCourant!!.getDepot_Reference()
                )
        )

        val frag = ACompterFragment.newInstance(liste)
        supportFragmentManager.beginTransaction()
            .replace(R.id.referenceACompterContainer, frag)
            .commitNow()

        referenceACompterContainer.apply {
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

    private fun fermerACompter()
    {
        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = true
        referenceACompterContainer.animate()
            .translationY(-referenceACompterContainer.height.toFloat())
            .setDuration(300)
            .withEndAction {
                referenceACompterContainer.visibility = View.GONE
                referenceACompterContainer.layoutParams =
                    (referenceACompterContainer.layoutParams as LinearLayout.LayoutParams).also {
                        it.height = 0
                    }
                aCompterFragment?.let { frag ->
                    supportFragmentManager.beginTransaction().remove(frag).commit()
                }
                aCompterFragment = null
            }.start()

        aCompterVisible = false
    }

    /**
     * COMPTER
     */
    private fun ouvrirCompter()
    {
        val liste = ArrayList(
            Inventaire_Ligne_TempOpenHelper
                .getInventaireLigneTempByInventaireEtZoneEtDepotInventorie(
                    db,
                    inventaireCourant!!.getInventaire_ID(),
                    zoneCourante,
                    depotCourant!!.getDepot_Reference()
                )
        )

        // Affiche le container
        referenceCompterContainer.apply {
            layoutParams = (layoutParams as LinearLayout.LayoutParams).also {
                it.height = (400 * resources.displayMetrics.density).toInt()
                it.weight = 0f
            }
            visibility = View.VISIBLE
            translationY = -resources.displayMetrics.heightPixels.toFloat()
            animate().translationY(0f).setDuration(300).start()
        }

        // Crée le fragment avec la liste
        val frag = CompterFragment.newInstance(liste)
        supportFragmentManager.beginTransaction()
            .replace(R.id.referenceCompterContainer, frag)
            .commitNow()

        CompterVisible = true
    }

    private fun fermerCompter()
    {
        findViewById<androidx.core.widget.NestedScrollView>(R.id.scrollView)?.isNestedScrollingEnabled = true
        referenceCompterContainer.animate()
            .translationY(-referenceCompterContainer.height.toFloat())
            .setDuration(300)
            .withEndAction {
                referenceCompterContainer.visibility = View.GONE
                referenceCompterContainer.layoutParams =
                    (referenceCompterContainer.layoutParams as LinearLayout.LayoutParams).also {
                        it.height = 0
                    }
                compterFragment?.let { frag ->
                    supportFragmentManager.beginTransaction().remove(frag).commit()
                }
                compterFragment = null
            }.start()

        CompterVisible = false
    }

    private fun fermerFragment()
    {
        if(scannerVisible)fermerScanner()
        if(rechercheVisible)fermerRecherche()
        if(aCompterVisible)fermerACompter()
        if(CompterVisible)fermerCompter()
    }

    private fun traiterCodeScanne(code: String) {
        if (code.isEmpty()) return

        lifecycleScope.launch(Dispatchers.IO) {
            val resultDecoupage: HashMap<String, String> = GestionCodeScanne.decoupageCode(code)
            val codeIdentification = resultDecoupage["code"]
            val numeroLotIdentification = resultDecoupage["lot"]
            val peremptionIdentification = resultDecoupage["peremption"]
            val tabDateSQL = peremptionIdentification?.split("/")
            val datePeremptionSQL = tabDateSQL?.get(tabDateSQL.size-1)+"-"+tabDateSQL?.get(1)+"-"+tabDateSQL?.get(0)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateDuJour = sdf.format(Date())
            val produitIdentifier : List<Produit> = ProduitOpenHelper.getProduitsByIdentification(db, codeIdentification)

            if(!produitIdentifier.isEmpty() && produitIdentifier.size == 1)
            {
                //un seul produit identifie
                val produit = produitIdentifier[0]

                //on vérifie la présence du produit dans la préparation
                var produitpresent = false
                var ligneBase : Inventaire_Ligne_Temp? = null
                var ligneCorrespondante : Inventaire_Ligne_Temp? = null
                val position = inventaireLigneTempList.indexOfFirst {
                    it.produitID == produit.iD_produit
                }

                for(courante : Inventaire_Ligne_Temp in inventaireLigneTempList)
                {
                    if(courante.produitID == produit?.iD_produit)
                    {
                        produitpresent = true
                        ligneBase = courante
                        if(courante.lot.contentEquals(numeroLotIdentification) && courante.peremptionDate.contentEquals(datePeremptionSQL)) {
                            ligneCorrespondante = courante
                            break
                        }
                    }
                }

                if(produitpresent)
                {
                    val conditionnement = produit?.cond_distrib?.toInt().toString()
                    var quantiteinventaire = ligneBase?.stockPhysique?.toInt()

                    if(ligneCorrespondante?.inventaireDate.contentEquals("0000-00-00") || ligneCorrespondante?.inventaireDate.contentEquals("") || ligneCorrespondante?.inventaireDate.contentEquals("null"))
                        quantiteinventaire = 0

                    quantiteinventaire = quantiteinventaire?.plus(conditionnement.toInt())

                    if(ligneCorrespondante != null)
                    {
                        ligneCorrespondante?.stockPhysique = quantiteinventaire?.toDouble() ?: 0.0
                        ligneCorrespondante?.inventaireDate = dateDuJour

                        Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(db, ligneCorrespondante)

                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                            db,
                            Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP,
                            ligneCorrespondante?.phiMR4UUID ?: 0,
                            ligneCorrespondante?._UID ?: 0,
                            DBOpenHelper.ActionsEAS.MAJ
                        )
                        ElementASynchroniserOpenHelper.toutSynchroniser(
                            this@DetailInventaire_V3,
                            db,
                            utilisateurConnecte,
                            false
                        )

                        ouvrirDetailFragment(ligneCorrespondante)
                        rafraichirListe()
                    }
                    else
                    {
                        //création de l'inventaire ligne temp
                        val nouvelInventaireLigneTemp =
                            Inventaire_Ligne_Temp(ligneBase)

                        val randominventairelignetemp = Random()
                        var inventairelignetempid = randominventairelignetemp.nextInt()
                        if (inventairelignetempid > 0) inventairelignetempid *= -1

                        nouvelInventaireLigneTemp._UID = inventairelignetempid
                        nouvelInventaireLigneTemp.inventaireDate = dateDuJour
                        nouvelInventaireLigneTemp.stockPhysique = conditionnement.toDouble()
                        nouvelInventaireLigneTemp.lot = numeroLotIdentification
                        nouvelInventaireLigneTemp.isSynchroniser = false
                        nouvelInventaireLigneTemp.peremptionDate = datePeremptionSQL

                        nouvelInventaireLigneTemp.emplacement = ligneBase?.emplacement

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
                            this@DetailInventaire_V3,
                            db,
                            utilisateurConnecte,
                            false
                        )

                        ouvrirDetailFragment(nouvelInventaireLigneTemp)
                        rafraichirListe()
                    }
                }
            }
        }
    }
    private fun rafraichirListe() {
        findViewById<TextView>(R.id.nbReferenceACompter_TV).text = Inventaire_Ligne_TempOpenHelper.getILTACompte(db, inventaireCourant!!.getInventaire_ID(), zoneCourante, depotCourant!!.getDepot_Reference()).toString()
        findViewById<TextView>(R.id.nbReferenceCompter_TV).text = Inventaire_Ligne_TempOpenHelper.getILTCompte(db, inventaireCourant!!.getInventaire_ID(), zoneCourante, depotCourant!!.getDepot_Reference()).toString()

        findViewById<ProgressBar>(R.id.progressBarInventaire_PB).max = Inventaire_Ligne_TempOpenHelper.getILTTotal(db, inventaireCourant!!.getInventaire_ID(), zoneCourante, depotCourant!!.getDepot_Reference())
        findViewById<ProgressBar>(R.id.progressBarInventaire_PB).progress = Inventaire_Ligne_TempOpenHelper.getILTCompte(db, inventaireCourant!!.getInventaire_ID(), zoneCourante, depotCourant!!.getDepot_Reference())

        invalidateOptionsMenu()
        verificationEtatInventaire()
    }
    @SuppressLint("SimpleDateFormat")
    private fun validerInventaire() {
        val randomAction = Random()
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
            "Inventaire Partiel à traiter"
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
        finish()
    }
    fun verificationEtatInventaire() {
        val produitIdList = mutableListOf<Int>()
        var nbReferenceInventorie = 0

        for (ligne in inventaireLigneTempList) {
            if (!produitIdList.contains(ligne.getProduitID())) {
                produitIdList.add(ligne.getProduitID())
                val qte = Inventaire_Ligne_TempOpenHelper
                    .getQteInventorieByInventaireProduitZoneDepot(
                        db,
                        ligne.getInventaire_ID(),
                        ligne.getProduitID(),
                        ligne.getZone(),
                        ligne.getDepotReference()
                    )
                if (qte >= 0) nbReferenceInventorie++
            }
        }

        /*val inventaireComplet = nbReferenceInventorie == produitIdList.size
        valider_item?.isVisible = inventaireComplet

        findViewById<ProgressBar>(R.id.progressBarInventaire_PB).apply {
            max      = produitIdList.size
            progress = nbReferenceInventorie
            if (inventaireComplet) {
                progressTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this@DetailInventaire_V3, R.color.vert)
                )
            }
        }*/
    }
    private fun onClickLigne(position: Int) {
        val courant = inventaireLigneTempList[position]
        ouvrirDetailFragment(courant)
    }
    private fun ouvrirDetailFragment(ligne: Inventaire_Ligne_Temp?) {
        lifecycleScope.launch(Dispatchers.Main) {
            val fragmentDejaOuvert = detailFragment != null && detailContainer.visibility == View.VISIBLE

            if (fragmentDejaOuvert) {
                // ─── Fragment déjà visible : on met juste à jour les données ───
                ligne?.let { detailFragment?.mettreAJourLigne(it) }
            } else {
                // ─── Fragment fermé : on l'ouvre normalement ───
                val frag = DetailLigneFragment.newInstance(ligne).also { detailFragment = it }
                frag.onFermer  = { fermerDetailFragment() }
                frag.onValider = { _, _, _ -> fermerDetailFragment() }

                supportFragmentManager.beginTransaction()
                    .replace(R.id.detailContainer, frag)
                    .commit()

                detailContainer.translationY = hauteurDetailFragment.toFloat()
                detailContainer.visibility   = View.VISIBLE
                detailContainer.animate()
                    .translationY(0f)
                    .setDuration(300)
                    .start()
            }
        }
    }
    private fun fermerDetailFragment() {
        positionSelectionnee = -1
        adapter?.setSelectedPosition(-1)

        detailContainer.animate()
            .translationY(hauteurDetailFragment.toFloat())
            .setDuration(300)
            .withEndAction {
                detailContainer.visibility = View.GONE
                detailFragment?.also {
                    supportFragmentManager.beginTransaction().remove(it).commit()
                }
                detailFragment = null
                /*inventaireListView.setPadding(
                    inventaireListView.paddingLeft,
                    inventaireListView.paddingTop,
                    inventaireListView.paddingRight,
                    0
                )
                inventaireListView.clipToPadding = true*/
            }.start()
    }

    private fun getIdInventaireLigneTemp(): Int =
        Random().nextInt().let { if (it > 0) it * -1 else it }

    private fun getDateDuJour(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}