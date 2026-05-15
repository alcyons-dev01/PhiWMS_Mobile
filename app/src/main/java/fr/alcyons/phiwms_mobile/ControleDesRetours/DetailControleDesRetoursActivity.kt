package fr.alcyons.phiwms_mobile.ControleDesRetours

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_ControleRetour_Adapte
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_ControleRetoursAdapter_2025
import fr.alcyons.phiwms_mobile.Outils.Alerte
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation
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
import java.util.function.Function

class DetailControleDesRetoursActivity : ServiceAvecConnexionActivity()
{
    private var retourSelectionne: Retour? = null

    private var retourLigneControleRetourAdapteListView: ListView? = null
    private var retourLigneControleRetoursAdapter: Retour_Ligne_ControleRetoursAdapter_2025? = null
    private var viewHolderAModifier: Retour_Ligne_ControleRetoursAdapter_2025.Retour_LigneViewHolder? = null
    private var pm: PackageManager? = null
    private var serialisation: Serialisation? = null
    private var liste_id_retour_ligne: MutableList<Int?>? = null
    private var premierPassage = false
    private var liste_retour_ligne: MutableList<Retour_Ligne>? = null
    private var context: Context? = null
    private var tri_choisi: String? = null
    private var lancerScan: LinearLayout? = null
    private var depot: Depot? = null
    private var listelot: MutableList<String?>? = null

    private var optionTri: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_detail_controle_retours)

        this.context = this@DetailControleDesRetoursActivity

        // Récupération des variables globales
        this.retourSelectionne = RetourOpenHelper.getRetourByID(this.db, (this.intent.extras ?: return).getInt("retourSelectionneID"))
        this.depot = DepotOpenHelper.getDepotParReference(this.db, (this.retourSelectionne ?: return).ref_Depot_Origine
        )

        this.listelot = ArrayList<String?>()
        this.serialisation = Serialisation(this@DetailControleDesRetoursActivity, this.db, this.utilisateurConnecte)

        // Affichage des informations de base
        (this.findViewById<View?>(R.id.intitule) as TextView).text = (this.retourSelectionne ?: return).intitule
        (this.findViewById<View?>(R.id.numero) as TextView).text = (this.retourSelectionne ?: return).numero

        // Récupération et initialisation de la listView
        this.retourLigneControleRetourAdapteListView = this.findViewById<ListView?>(R.id.listeView)

        (this.retourLigneControleRetourAdapteListView ?: return).itemsCanFocus = true
        (this.retourLigneControleRetourAdapteListView ?: return).setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val retourLigne = (this.retourLigneControleRetoursAdapter ?: return@setOnItemClickListener).retour_Lignes[position]
            this.viewHolderAModifier = (this.retourLigneControleRetoursAdapter ?: return@setOnItemClickListener).retourLigneViewHolderList[position]

            val DetailControleRetours_Bundle = super@DetailControleDesRetoursActivity.getBundle()
            DetailControleRetours_Bundle.putInt("produitID", retourLigne.code_produit)
            DetailControleRetours_Bundle.putInt("retourLigneId", retourLigne._UID)
            val DetailControleRetours_Intent = Intent(this@DetailControleDesRetoursActivity, ListeLotsControleDesRetoursActivity::class.java)
            DetailControleRetours_Intent.putExtras(DetailControleRetours_Bundle)
            this@DetailControleDesRetoursActivity.startActivityForResult(DetailControleRetours_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS)
        }

        //Initialisation des variables
        this.premierPassage = true
        this.liste_id_retour_ligne = ArrayList<Int?>()
        //liste_resultat_scan = new ArrayList<>();
        this.liste_retour_ligne = ArrayList<Retour_Ligne>()
        this.pm = this@DetailControleDesRetoursActivity.packageManager
        this.liste_retour_ligne = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(this.db, this.retourSelectionne)

        this.optionTri = this.findViewById<Spinner?>(R.id.optionTri)
        this.tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriRetourLigne(this.db)
        if (null == this.tri_choisi)
        {
            ParametreUtilisateurOpenHelper.mettreAJourTriRetourLigne(this.db, 0, "Designation")
            this.tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriRetourLigne(this.db)
        }

        (this.optionTri ?: return).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var isFirstSelection: Boolean = true // drapeau pour ignorer le premier appel

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (this.isFirstSelection) {
                    this.isFirstSelection = false // on consomme le premier appel
                    return  // ne rien faire au lancement
                }

                if (null != parent.getChildAt(0)) {
                    parent.getChildAt(0).visibility = View.INVISIBLE
                }
                this@DetailControleDesRetoursActivity.tri_choisi = (this@DetailControleDesRetoursActivity.optionTri ?: return).getItemAtPosition(position).toString()
                ParametreUtilisateurOpenHelper.mettreAJourTriPreparation(this@DetailControleDesRetoursActivity.db, 0, this@DetailControleDesRetoursActivity.tri_choisi)

                when (this@DetailControleDesRetoursActivity.tri_choisi) {
                    "Designation" -> this@DetailControleDesRetoursActivity.onClickTriDesignation()
                    "Place" -> this@DetailControleDesRetoursActivity.onClickTriParPlace()
                    "Catégorie" -> this@DetailControleDesRetoursActivity.onClickTriCategorie()
                    "Poids" -> this@DetailControleDesRetoursActivity.onClickTriParPoids()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //gestion du bouton de scan
        this.lancerScan = this.findViewById<LinearLayout?>(R.id.lancerScan)
        (this.lancerScan ?: return).setOnClickListener { v: View? -> this.lancerScanner() }

        this.setupOnBackPressedCallback()
    }

    public override fun onResume()
    {
        super.onResume()
        if (statutConnexion && this.passageParOnCreate)
        {
            if (!this.swipeRefreshLayout.isRefreshing) { this.afficherSpinner(this@DetailControleDesRetoursActivity, LayoutInflater.from(this@DetailControleDesRetoursActivity)) }

            val requestQueue = Volley.newRequestQueue(this@DetailControleDesRetoursActivity)
            val urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(this.db) + DBOpenHelper.Urls.uriRequeteControleRetours + "/" + (this.retourSelectionne ?: return)._UID

            // Takes the response from the JSON request
            val obreq: JsonObjectRequest = object : JsonObjectRequest(Method.GET, urlRequete, null, Response.Listener { response: JSONObject? ->
                try
                {
                    val nbResultat = (response ?: return@Listener).getInt("resultCount")
                    if (0 == nbResultat)
                    {
                        val erreur = response.getString("erreur")
                        if (erreur == (this.context ?: return@Listener).getString(R.string.tokenInvalide)) { Alerte.afficherAlerteInformation(this@DetailControleDesRetoursActivity, this.layoutInflater, "Alerte", "Votre session est invalide, veuillez vous reconnecter.", true, false) }
                        else if (erreur == (this.context ?: return@Listener).getString(R.string.tokenExpire)) { Alerte.afficherAlerteInformation(this@DetailControleDesRetoursActivity, this.layoutInflater, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", true, false) }
                        else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) { Alerte.afficherAlerteInformation(this@DetailControleDesRetoursActivity, this.layoutInflater, "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Contrôle des retours", true, false) }
                    }
                    else
                    {
                        val retourLignesJSONArray = response.getJSONArray("PH_Retour_Lignes")
                        for (k in 0..<retourLignesJSONArray.length()) {
                            val retourLigneJSONObject = retourLignesJSONArray.getJSONObject(k)
                            val stockLotEmplacementsJSONArray = retourLigneJSONObject.getJSONArray("ph_stock_lot_emplacements")

                            for (y in 0..<stockLotEmplacementsJSONArray.length())
                            {
                                val stock_lot_emplacement_light = Stock_Lot_Emplacement_Light(stockLotEmplacementsJSONArray.getJSONObject(y))
                                val stock_lot_emplacement_bdd = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(this.db, stock_lot_emplacement_light._UID)

                                if (null == stock_lot_emplacement_bdd) { if (0.0 <= stock_lot_emplacement_light.qte) { Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(this.db, stock_lot_emplacement_light) } }
                                else { if (stock_lot_emplacement_bdd.qte != stock_lot_emplacement_light.qte) { Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(this.db, stock_lot_emplacement_light) } }

                                (this.listelot ?: return@Listener).add(stock_lot_emplacement_light.lot)
                            }
                        }
                        // Récupération des retours_lignes du Retour présélectionné
                        for (retourLigne in this.liste_retour_ligne ?: return@Listener)
                        {
                            val retourLigneAdapte = Retour_Ligne_ControleRetour_Adapte(retourLigne._UID)
                            val produit = ProduitOpenHelper.getProduitByID(this.db, retourLigne.code_produit)
                            val depot = DepotOpenHelper.getDepotParReference(this.db, (this.retourSelectionne ?: return@Listener).ref_Depot_Origine
                            )
                            if (null != produit && null != depot)
                            {
                                for (stockLotEmplacement in Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(this.db, produit, depot))
                                { retourLigneAdapte.lotAdaptes.add(retourLigneAdapte.LotAdapte(stockLotEmplacement)) }
                            }

                            (this.liste_id_retour_ligne ?: return@Listener).add(retourLigne._UID)
                        }

                        when (this.tri_choisi)
                        {
                            "Designation" -> this.onClickTriDesignation()
                            "Place" -> this.onClickTriParPlace()
                            "Catégorie" -> this.onClickTriCategorie()
                            "Poids" -> this.onClickTriParPoids()
                        }

                        this.invalidateOptionsMenu()
                    }

                    this.passageParOnCreate = false

                    this.arreterSpinner()
                }
                catch (e: JSONException) { e.printStackTrace() }
                },
                Response.ErrorListener { error: VolleyError? ->
                    Log.e("Volley CdR", error.toString())
                    Alerte.afficherAlerteInformation(this@DetailControleDesRetoursActivity, this.layoutInflater, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Contrôle des retours)", true, false)
                })
            {
                /**
                 * Passing some request headers
                 */
                override fun getHeaders(): MutableMap<String?, String?>
                {
                    val headers: MutableMap<String?, String?> = HashMap<String?, String?>()
                    headers["Authorization"] = this@DetailControleDesRetoursActivity.utilisateurConnecte.token
                    return headers
                }
            }

            obreq.setRetryPolicy(this.retryPolicy)
            requestQueue.add<JSONObject?>(obreq)
        }
        else
        {
            // Récupération des retours_lignes du Retour présélectionné
            for (retourLigne in this.liste_retour_ligne ?: return)
            {
                val retourLigneAdapte = Retour_Ligne_ControleRetour_Adapte(retourLigne._UID)
                val produit = ProduitOpenHelper.getProduitByID(this.db, retourLigne.code_produit)
                val depot = DepotOpenHelper.getDepotParReference(this.db, (this.retourSelectionne ?: return).ref_Depot_Origine
                )
                if (null != produit && null != depot)
                {
                    for (stockLotEmplacement in Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(this.db, produit, depot))
                    { retourLigneAdapte.lotAdaptes.add(retourLigneAdapte.LotAdapte(stockLotEmplacement)) }
                }
                (this.liste_id_retour_ligne ?: return).add(retourLigne._UID)
            }

            when (this.tri_choisi)
            {
                "Designation" -> this.onClickTriDesignation()
                "Place" -> this.onClickTriParPlace()
                "Catégorie" -> this.onClickTriCategorie()
                "Poids" -> this.onClickTriParPoids()
            }

            this.invalidateOptionsMenu()
        }
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (null != data)
        {
            when (requestCode)
            {
                CodesEchangesActivites.RETOUR_LISTE_LOTS -> {}
                CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH -> if (null != data)
                {
//                        int retourLigneId = data.getExtras().getInt("retourLigneId");
//                        String numLot = data.getExtras().getString("numLot");
//                        String numSerie = data.getExtras().getString("numSerie");
//                        String datePeremption = data.getExtras().getString("datePeremption");
//                        int qteActuelle = data.getExtras().getInt("qteActuelle");
//
//                        Retour_Ligne retour_ligne_courant = Retour_LigneOpenHelper.getRetourLigneByID(db, retourLigneId);
//
//                        if(retour_ligne_courant != null)
//                        {
//                            Produit produit_courant = ProduitOpenHelper.getProduitByID(db, retour_ligne_courant.getCode_produit());
//
//                            //MAJ du retour ligne
//                            retour_ligne_courant.setQte_Retourner(retour_ligne_courant.getQte_Retourner()+qteActuelle);
//                            Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retour_ligne_courant);
//
//                            ObjetPreparationScannee objetPreparationScannee = new ObjetPreparationScannee(qteActuelle, numLot, datePeremption, "", "", "", produit_courant.getID_produit(), qteActuelle, numSerie);
//                            Retour_Ligne_ControleRetour_Adapte retourLigneAdapteCourant = new Retour_Ligne_ControleRetour_Adapte(retourLigneId);
//                            int index_a_supprimer = -1;
//                            boolean aSupprimer = false;
//                            for(Retour_Ligne_ControleRetour_Adapte retour_adapte_temp : retourLigneControleRetourAdapteList)
//                            {
//                                index_a_supprimer ++;
//                                if(retour_adapte_temp.getRetourLigneID() == retourLigneAdapteCourant.getRetourLigneID())
//                                {
//                                    retourLigneAdapteCourant = retour_adapte_temp;
//                                    aSupprimer = true;
//                                    break;
//                                }
//                            }
//
//                            if(aSupprimer)
//                            {
//                                retourLigneControleRetourAdapteList.remove(index_a_supprimer);
//                            }
//
//                            retourLigneAdapteCourant.getLotAdaptes().add(retourLigneAdapteCourant.new LotAdapte(objetPreparationScannee));
//                            retourLigneControleRetourAdapteList.add(retourLigneAdapteCourant);
//                        }
                }
            }

            this.invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        super.onCreateOptionsMenu(menu)
        //Récupération du menu
        val inflater = this.menuInflater
        inflater.inflate(R.menu.menu_action, menu)
        menu.findItem(R.id.menuSaveCircle).isVisible = true
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean
    {
        val item = menu.findItem(R.id.menuSaveCircle)
        item.setOnMenuItemClickListener { item1: MenuItem? ->
            val listeBaseTemp = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(this.db, this.retourSelectionne)
            var retourComplet = true
            for (baseTemp in listeBaseTemp) {
                val qteARetourner = baseTemp.qte_Demander.toInt()
                var retourLigneComplet = true
                val retourLigneNegList = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(this.db, this.retourSelectionne, baseTemp.code_produit)

                var qteRetourner = 0
                for (negTemp in retourLigneNegList) { qteRetourner = (qteRetourner.toDouble() + negTemp.qte_Retourner).toInt() }

                if (qteARetourner != qteRetourner) { retourLigneComplet = false }

                if (!retourLigneComplet)
                {
                    retourComplet = false
                    break
                }
            }

            if (retourComplet) this.onMenuSaveClick()
            else Alerte.afficherAlerteConfirmation(this@DetailControleDesRetoursActivity, this.layoutInflater, this.getBundle(), "Toutes les références n'ont pas été retournées, souhaitez vous continuer ?", false, true, this@DetailControleDesRetoursActivity)
            true
        }

        return true
    }

    // Définition de l'action sur Click du bouton Save
    fun onMenuSaveClick()
    {
        val retourLigneBase = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(this.db, this.retourSelectionne)

        for (retourLigneTemp in retourLigneBase)
        {
            Retour_LigneOpenHelper.supprimerUnRetourLigne(this.db, retourLigneTemp)
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigneTemp.phiMR4UUID, retourLigneTemp._UID, DBOpenHelper.ActionsEAS.SUPPR)
        }

        //Création de l'action utilisateur
        val random = Random()
        var actionId = random.nextInt()
        if (0 < actionId) actionId *= -1
        val parseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date()
        val date_string = parseFormat.format(date)
        val new_action_utilisateur = ActionUtilisateur(actionId, this.utilisateurConnecte.id, date_string, this.serviceActuel.id, this.utilisateurConnecte.etablissementId, "En attente", (this.retourSelectionne ?: return)._UID, "", "Controle des retours")
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(this.db, new_action_utilisateur)
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.phiMR4UUID, new_action_utilisateur.id, DBOpenHelper.ActionsEAS.AJOUT)

        val retourLignesListe = Retour_LigneOpenHelper.getAllRetourLignesByRetour(this.db, this.retourSelectionne)
        for (retourLigne in retourLignesListe)
        {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigne.phiMR4UUID, retourLigne._UID, DBOpenHelper.ActionsEAS.AJOUT)

            val randomactionligne = Random()
            var actionligneId = randomactionligne.nextInt()
            if (0 < actionligneId) actionligneId *= -1

            val actionUtilisateur_ligne = ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.id, "Retour Ligne", retourLigne._UID, "", 0, retourLigne.qte_Retourner.toInt(), retourLigne.produit_Designation)
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(this.db, actionUtilisateur_ligne)
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionUtilisateur_ligne.phiMR4UUID, actionUtilisateur_ligne.id, DBOpenHelper.ActionsEAS.AJOUT)
        }

        val listSerialisation = PH_SerialisationOpenHelper.getAllPH_SerialisationByMvtId(this.db, (this.retourSelectionne ?: return)._UID.toString())
        for (serialisationCourante in listSerialisation) { ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, serialisationCourante.phiMR4UUID, serialisationCourante.get_UID(), DBOpenHelper.ActionsEAS.AJOUT) }

        (this.retourSelectionne ?: return).en_Attente_de = this.getString(R.string.RepriseEffectuee)

        val dateJour = Date()
        val format: DateFormat = SimpleDateFormat("dd-MM-yyyy")

        (this.retourSelectionne ?: return).date_retour = format.format(dateJour)
        (this.retourSelectionne ?: return).date_Validation = format.format(dateJour)

        val rowID = RetourOpenHelper.mettreAJourRetour(this.db, this.retourSelectionne)
        if (-1L != rowID) { ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, RetourOpenHelper.Constantes.TABLE_RETOUR, (this.retourSelectionne ?: return).phiMR4UUID, (this.retourSelectionne ?: return)._UID, DBOpenHelper.ActionsEAS.MAJ) }

        Toast.makeText(this@DetailControleDesRetoursActivity, "Retour contrôlé", Toast.LENGTH_SHORT).show()

        // Si possible, on essaie de mettre à jour les éléments
        ElementASynchroniserOpenHelper.toutSynchroniser(this@DetailControleDesRetoursActivity, this.db, this.utilisateurConnecte, true)
        val validationRetour_Intent = Intent(this@DetailControleDesRetoursActivity, ServiceControleRetoursActivity::class.java)
        val validationRetours_Bundle = super@DetailControleDesRetoursActivity.getBundle()
        validationRetour_Intent.putExtras(validationRetours_Bundle)
        this@DetailControleDesRetoursActivity.startActivity(validationRetour_Intent)
        this@DetailControleDesRetoursActivity.finish()
        return
    }

    private fun lancerScanner()
    {
        this.premierPassage = false
        var controleDesRetour_Intent: Intent? = null
        val controleDesRetour_Bundle = super.getBundle()
        controleDesRetour_Bundle.putString("contexte", R.string.scannerContextMultipleNewControleRetour.toString())

        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.lowercase(Locale.getDefault()).contains("honeywell")) { controleDesRetour_Intent = Intent(this@DetailControleDesRetoursActivity, ScannerRetourActivity::class.java) }
        else
        {
            if ((this.pm ?: return).hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                //controleDesRetour_Intent = new Intent(DetailControleDesRetours2025Activity.this, BarcodePreparationActivity.class);
            }
            else { controleDesRetour_Intent = Intent(this@DetailControleDesRetoursActivity, ScannerRetourActivity::class.java) }
        }

        controleDesRetour_Bundle.putBoolean("isBoutonSuppressionExistant", true)
        controleDesRetour_Bundle.putSerializable("RetourCourant", this.retourSelectionne)
        controleDesRetour_Bundle.putSerializable("DepotOrigine", this.depot)
        controleDesRetour_Bundle.putStringArrayList("liste_lot", this.listelot as ArrayList<String?>?)
        controleDesRetour_Bundle.putSerializable("ListeRetourLigne", this.liste_retour_ligne as Serializable?)
        controleDesRetour_Bundle.putBoolean("EmplacementUF", true)

        (controleDesRetour_Intent ?: return).putExtras(controleDesRetour_Bundle)
        this@DetailControleDesRetoursActivity.startActivityForResult(controleDesRetour_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH)
    }

    internal fun onClickTriDesignation()
    {
        this.tri_choisi = "Designation"
        (liste_retour_ligne ?: return).sortWith(Comparator.comparing<Retour_Ligne?, String?>(Function { oo: Retour_Ligne? -> oo!!.produit_Designation.lowercase(Locale.getDefault()) }))
        this.gestionAdapter()
    }

    internal fun onClickTriCategorie()
    {
        this.tri_choisi = "Catégorie"
        (liste_retour_ligne ?: return).sortWith(Comparator { oo1: Retour_Ligne?, oo2: Retour_Ligne? ->
            val produit1 = ProduitOpenHelper.getProduitByID(this.db, oo1!!.code_produit)
            val produit2 = ProduitOpenHelper.getProduitByID(this.db, oo2!!.code_produit)
            produit1.categorie.lowercase(Locale.getDefault()).compareTo(produit2.categorie.lowercase(Locale.getDefault()))
        })

        this.gestionAdapter()
    }

    internal fun onClickTriParPoids()
    {
        this.tri_choisi = "Poids"
        (liste_retour_ligne ?: return).sortWith(Comparator { oo1: Retour_Ligne?, oo2: Retour_Ligne? ->
            val produit1 = ProduitOpenHelper.getProduitByID(this.db, oo1!!.code_produit)
            val produit2 = ProduitOpenHelper.getProduitByID(this.db, oo2!!.code_produit)
            produit1.poids.compareTo(produit2.poids)
        })

        this.gestionAdapter()
    }

    internal fun onClickTriParPlace()
    {
        this.tri_choisi = "Place"
        (liste_retour_ligne ?: return).sortWith(Comparator { oo1: Retour_Ligne?, oo2: Retour_Ligne? ->
            var oo1EmplacementParDefaut = oo1!!.emplacementOrigine
            var oo2EmplacementParDefaut = oo2!!.emplacementOrigine

            if (null == oo1EmplacementParDefaut || oo1EmplacementParDefaut.contentEquals(""))
            {
                val produit = ProduitOpenHelper.getProduitByID(this.db, oo1.code_produit)
                oo1EmplacementParDefaut = produit.emplacement_PUI_Defaut
            }

            if (null == oo2EmplacementParDefaut || oo2EmplacementParDefaut.contentEquals(""))
            {
                val produit = ProduitOpenHelper.getProduitByID(this.db, oo2.code_produit)
                oo2EmplacementParDefaut = produit.emplacement_PUI_Defaut
            }

            oo1EmplacementParDefaut.compareTo(oo2EmplacementParDefaut)
        })

        this.gestionAdapter()
    }

    fun gestionAdapter()
    {
        this.retourLigneControleRetoursAdapter = Retour_Ligne_ControleRetoursAdapter_2025(this@DetailControleDesRetoursActivity, this.liste_retour_ligne, this.db, this.retourSelectionne)
        (this.retourLigneControleRetourAdapteListView ?: return).adapter = this.retourLigneControleRetoursAdapter
    }

    override fun confirmationService() { this.onMenuSaveClick() }

    private fun setupOnBackPressedCallback()
    {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed()
            {
                val detailControleIntent = Intent(this@DetailControleDesRetoursActivity, ServiceControleRetoursActivity::class.java)
                val detailControleBundle = super@DetailControleDesRetoursActivity.getBundle()
                detailControleBundle.putString("Etat", "Retour")
                detailControleIntent.putExtras(detailControleBundle)
                this@DetailControleDesRetoursActivity.startActivity(detailControleIntent)
                this@DetailControleDesRetoursActivity.finish()
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }
}
