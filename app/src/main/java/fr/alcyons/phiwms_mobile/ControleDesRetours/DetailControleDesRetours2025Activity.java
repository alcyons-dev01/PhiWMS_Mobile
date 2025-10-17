package fr.alcyons.phiwms_mobile.ControleDesRetours;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerRetourActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_ControleRetour_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_ControleRetoursAdapter_2025;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceControleRetoursActivity;

public class DetailControleDesRetours2025Activity  extends ServiceAvecConnexionActivity {

    Retour retourSelectionne;

    ListView retourLigneControleRetourAdapteListView;
    Retour_Ligne_ControleRetoursAdapter_2025 retourLigneControleRetoursAdapter;
    Retour_Ligne_ControleRetoursAdapter_2025.Retour_LigneViewHolder viewHolderAModifier;
    PackageManager pm;
    Serialisation serialisation;
    List<Integer> liste_id_retour_ligne;
    boolean premierPassage;
    List<Retour_Ligne> liste_retour_ligne;
    Context context;
    String tri_choisi;
    LinearLayout lancerScan;
    Depot depot;
    List<String> listelot;

    Spinner optionTri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_controle_retours);

        context = DetailControleDesRetours2025Activity.this;

        // Récupération des variables globales
        retourSelectionne = RetourOpenHelper.getRetourByID(db, intent.getExtras().getInt("retourSelectionneID"));
        depot = DepotOpenHelper.getDepotParReference(db, retourSelectionne.getRef_Depot_Origine());

        listelot = new ArrayList<>();
        serialisation = new Serialisation(DetailControleDesRetours2025Activity.this, db, utilisateurConnecte);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.intitule)).setText(retourSelectionne.getIntitule());
        ((TextView) findViewById(R.id.numero)).setText(retourSelectionne.getNumero());

        // Récupération et initialisation de la listView
        retourLigneControleRetourAdapteListView = (ListView) findViewById(R.id.listeView);

        retourLigneControleRetourAdapteListView.setItemsCanFocus(true);
        retourLigneControleRetourAdapteListView.setOnItemClickListener((parent, view, position, id) -> {
            Retour_Ligne retourLigne = retourLigneControleRetoursAdapter.retour_Lignes.get(position);

            viewHolderAModifier = retourLigneControleRetoursAdapter.retourLigneViewHolderList.get(position);

            Bundle DetailControleRetours_Bundle = DetailControleDesRetours2025Activity.super.getBundle();
            DetailControleRetours_Bundle.putInt("produitID", retourLigne.getCode_produit());
            DetailControleRetours_Bundle.putInt("retourLigneId", retourLigne.get_UID());
            Intent DetailControleRetours_Intent = new Intent(DetailControleDesRetours2025Activity.this, ListeLotsControleDesRetours2025Activity.class);
            DetailControleRetours_Intent.putExtras(DetailControleRetours_Bundle);
            DetailControleDesRetours2025Activity.this.startActivityForResult(DetailControleRetours_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
        });

        //Initialisation des variables
        premierPassage = true;
        liste_id_retour_ligne = new ArrayList<>();
        //liste_resultat_scan = new ArrayList<>();
        liste_retour_ligne = new ArrayList<>();
        pm = DetailControleDesRetours2025Activity.this.getPackageManager();
        liste_retour_ligne =  Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retourSelectionne);

        optionTri = (Spinner) findViewById(R.id.optionTri);
        tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriRetourLigne(db);
        if(tri_choisi == null)
        {
            ParametreUtilisateurOpenHelper.mettreAJourTriRetourLigne(db, 0, "Designation");
            tri_choisi= ParametreUtilisateurOpenHelper.getChoixTriRetourLigne(db);
        }

        optionTri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstSelection = true; // drapeau pour ignorer le premier appel

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false; // on consomme le premier appel
                    return; // ne rien faire au lancement
                }

                if (((TextView) parent.getChildAt(0)) != null) {
                    ((TextView) parent.getChildAt(0)).setVisibility(View.INVISIBLE);
                }
                tri_choisi = optionTri.getItemAtPosition(position).toString();
                ParametreUtilisateurOpenHelper.mettreAJourTriPreparation(db, 0, tri_choisi);

                switch (tri_choisi)
                {
                    case "Designation":
                        onClickTriDesignation();
                        break;
                    case "Place":
                        onClickTriParPlace();
                        break;

                    case "Catégorie":
                        onClickTriCategorie();
                        break;

                    case "Poids":
                        onClickTriParPoids();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //gestion du bouton de scan
        lancerScan = (LinearLayout) findViewById(R.id.lancerScan);
        lancerScan.setOnClickListener(v -> lancerScanner());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent detailControleIntent = new Intent(DetailControleDesRetours2025Activity.this, ServiceControleRetoursActivity.class);
                Bundle detailControleBundle = DetailControleDesRetours2025Activity.super.getBundle();
                detailControleBundle.putString("Etat", "Retour");
                detailControleIntent.putExtras(detailControleBundle);
                DetailControleDesRetours2025Activity.this.startActivity(detailControleIntent);
                DetailControleDesRetours2025Activity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (statutConnexion && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(DetailControleDesRetours2025Activity.this, LayoutInflater.from(DetailControleDesRetours2025Activity.this));
            }

            RequestQueue requestQueue = Volley.newRequestQueue(DetailControleDesRetours2025Activity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteControleRetours+"/"+retourSelectionne.get_UID();

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,null,
                    new Response.Listener<JSONObject>() {

                        // Takes the response from the JSON request
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int nbResultat = response.getInt("resultCount");
                                if (nbResultat == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                    } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                        Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Contrôle des retours", "alerte");
                                    }
                                } else {
                                    JSONArray retourLignesJSONArray= response.getJSONArray("PH_Retour_Lignes");
                                    for (int k = 0; k < retourLignesJSONArray.length(); k++) {
                                        JSONObject retourLigneJSONObject = retourLignesJSONArray.getJSONObject(k);
                                        JSONArray stockLotEmplacementsJSONArray = retourLigneJSONObject.getJSONArray("ph_stock_lot_emplacements");

                                        for (int y = 0; y < stockLotEmplacementsJSONArray.length(); y++) {
                                            Stock_Lot_Emplacement_Light stock_lot_emplacement_light = new Stock_Lot_Emplacement_Light(stockLotEmplacementsJSONArray.getJSONObject(y));
                                            Stock_Lot_Emplacement_Light stock_lot_emplacement_bdd = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stock_lot_emplacement_light.get_UID());

                                            if (stock_lot_emplacement_bdd == null) {
                                                if (stock_lot_emplacement_light.getQte() >= 0) {
                                                    Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stock_lot_emplacement_light);
                                                }
                                            }
                                            else
                                            {
                                                if(stock_lot_emplacement_bdd.getQte() != stock_lot_emplacement_light.getQte())
                                                {
                                                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_lot_emplacement_light);
                                                }
                                            }

                                            listelot.add(stock_lot_emplacement_light.getLot());
                                        }
                                    }
                                    // Récupération des retours_lignes du Retour présélectionné
                                    for (Retour_Ligne retourLigne : liste_retour_ligne) {
                                        Retour_Ligne_ControleRetour_Adapte retourLigneAdapte = new Retour_Ligne_ControleRetour_Adapte(retourLigne.get_UID());
                                        Produit produit = ProduitOpenHelper.getProduitByID(db, retourLigne.getCode_produit());
                                        Depot depot = DepotOpenHelper.getDepotParReference(db, retourSelectionne.getRef_Depot_Origine());
                                        if(produit != null && depot !=null)
                                        {
                                            for (Stock_Lot_Emplacement_Light stockLotEmplacement : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)) {
                                                retourLigneAdapte.getLotAdaptes().add(retourLigneAdapte.new LotAdapte(stockLotEmplacement));
                                            }
                                        }
                                        liste_id_retour_ligne.add(retourLigne.get_UID());
                                    }
                                    switch (tri_choisi)
                                    {
                                        case "Designation":
                                            onClickTriDesignation();
                                            break;
                                        case "Place":
                                            onClickTriParPlace();
                                            break;

                                        case "Catégorie":
                                            onClickTriCategorie();
                                            break;

                                        case "Poids":
                                            onClickTriParPoids();
                                            break;
                                    }

                                    invalidateOptionsMenu();
                                }
                                passageParOnCreate = false;

                                arreterSpinner();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    error -> {
                        Log.e("Volley CdR", error.toString());
                        Alerte.afficherAlerte(DetailControleDesRetours2025Activity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Contrôle des retours)", "alerte");
                    }
            ) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    return headers;
                }
            };
            obreq.setRetryPolicy(retryPolicy);
            requestQueue.add(obreq);
        }
        else
        {
            // Récupération des retours_lignes du Retour présélectionné
            for (Retour_Ligne retourLigne : liste_retour_ligne) {
                Retour_Ligne_ControleRetour_Adapte retourLigneAdapte = new Retour_Ligne_ControleRetour_Adapte(retourLigne.get_UID());
                Produit produit = ProduitOpenHelper.getProduitByID(db, retourLigne.getCode_produit());
                Depot depot = DepotOpenHelper.getDepotParReference(db, retourSelectionne.getRef_Depot_Origine());
                if(produit != null && depot !=null)
                {
                    for (Stock_Lot_Emplacement_Light stockLotEmplacement : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)) {
                        retourLigneAdapte.getLotAdaptes().add(retourLigneAdapte.new LotAdapte(stockLotEmplacement));
                    }
                }
                liste_id_retour_ligne.add(retourLigne.get_UID());
            }
            switch (tri_choisi)
            {
                case "Designation":
                    onClickTriDesignation();
                    break;
                case "Place":
                    onClickTriParPlace();
                    break;

                case "Catégorie":
                    onClickTriCategorie();
                    break;

                case "Poids":
                    onClickTriParPoids();
                    break;
            }

            invalidateOptionsMenu();
        }

    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_LOTS:
//                    Retour_Ligne_ControleRetour_Adapte retourLigneAdapte = retourLigneControleRetoursAdapter.retour_Lignes.get(retourLigneControleRetoursAdapter.retourLigneViewHolderList.indexOf(viewHolderAModifier));
//                    retourLigneAdapte.setLotAdaptes((List<Retour_Ligne_ControleRetour_Adapte.LotAdapte>) data.getExtras().getSerializable("lotAdaptesList"));
                    break;
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    if(data != null)
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
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSaveCircle).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSaveCircle);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<Retour_Ligne> listeBaseTemp = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retourSelectionne);
                boolean retourComplet = true;
                for(Retour_Ligne baseTemp : listeBaseTemp)
                {
                    int qteARetourner = (int) baseTemp.getQte_Demander();
                    boolean retourLigneComplet = true;
                    List<Retour_Ligne> retourLigneNegList = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourSelectionne, baseTemp.getCode_produit());

                    int qteRetourner = 0;
                    for(Retour_Ligne negTemp : retourLigneNegList)
                    {
                        qteRetourner = (int) (qteRetourner + negTemp.getQte_Retourner());
                    }

                    if(qteARetourner != qteRetourner)
                    {
                        retourLigneComplet = false;
                    }

                    if(!retourLigneComplet)
                    {
                        retourComplet = false;
                        break;
                    }
                }

                if(retourComplet)
                    onMenuSaveClick();
                else
                    afficherAlerteConfirmationRetour(DetailControleDesRetours2025Activity.this, getLayoutInflater());

                return true;
            }
        });
        return true;
    }

    // Définition de l'action sur Click du bouton Save
    public void onMenuSaveClick() {
        List<Retour_Ligne> retourLigneBase = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retourSelectionne);

        for (Retour_Ligne retourLigneTemp : retourLigneBase) {
            Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigneTemp);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigneTemp.getPhiMR4UUID(), retourLigneTemp.get_UID(), DBOpenHelper.ActionsEAS.SUPPR);
        }

        //Création de l'action utilisateur
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", retourSelectionne.get_UID(), "", "Controle des retours");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

        List<Retour_Ligne> retourLignesListe = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retourSelectionne);
        for(Retour_Ligne retourLigne : retourLignesListe)
        {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigne.getPhiMR4UUID(), retourLigne.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);

            Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if(actionligneId > 0)
                actionligneId= actionligneId*-1;

            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retourLigne.get_UID(), "", 0, (int)retourLigne.getQte_Retourner(), retourLigne.getProduit_Designation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionUtilisateur_ligne.getPhiMR4UUID(), actionUtilisateur_ligne.getId(), DBOpenHelper.ActionsEAS.AJOUT);
        }

        List<PH_Serialisation> listSerialisation = PH_SerialisationOpenHelper.getAllPH_SerialisationByMvtId(db, String.valueOf(retourSelectionne.get_UID()));
        for(PH_Serialisation serialisationCourante : listSerialisation)
        {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, serialisationCourante.getPhiMR4UUID(), serialisationCourante.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
        }

        retourSelectionne.setEn_Attente_de(getString(R.string.RepriseEffectuee));

        Date dateJour = new Date();
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        retourSelectionne.setDate_retour(format.format(dateJour));
        retourSelectionne.setDate_Validation(format.format(dateJour));

        long rowID = RetourOpenHelper.mettreAJourRetour(db, retourSelectionne);
        if (rowID != -1) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retourSelectionne.getPhiMR4UUID(), retourSelectionne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
        }

        Toast.makeText(DetailControleDesRetours2025Activity.this, "Retour contrôlé", Toast.LENGTH_SHORT).show();

        // Si possible, on essaie de mettre à jour les éléments
        ElementASynchroniserOpenHelper.toutSynchroniser(DetailControleDesRetours2025Activity.this, db, utilisateurConnecte, true);

        Intent validationRetour_Intent = new Intent(DetailControleDesRetours2025Activity.this, ServiceControleRetoursActivity.class);
        Bundle validationRetours_Bundle = DetailControleDesRetours2025Activity.super.getBundle();
        validationRetour_Intent.putExtras(validationRetours_Bundle);
        DetailControleDesRetours2025Activity.this.startActivity(validationRetour_Intent);
        DetailControleDesRetours2025Activity.this.finish();
        return;
    }

    public void lancerScanner()
    {
        premierPassage = false;
        Intent controleDesRetour_Intent = null;
        Bundle controleDesRetour_Bundle = super.getBundle();
        controleDesRetour_Bundle.putString("contexte", String.valueOf(R.string.scannerContextMultipleNewControleRetour));

        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            controleDesRetour_Intent = new Intent(DetailControleDesRetours2025Activity.this, ScannerRetourActivity.class);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                //controleDesRetour_Intent = new Intent(DetailControleDesRetours2025Activity.this, BarcodePreparationActivity.class);

            }
            else
            {
                controleDesRetour_Intent = new Intent(DetailControleDesRetours2025Activity.this, ScannerRetourActivity.class);
            }
        }
        controleDesRetour_Bundle.putBoolean("isBoutonSuppressionExistant", true);
        controleDesRetour_Bundle.putSerializable("RetourCourant", (Serializable) retourSelectionne);
        controleDesRetour_Bundle.putSerializable("DepotOrigine", (Serializable) depot);
        controleDesRetour_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) listelot);
        controleDesRetour_Bundle.putSerializable("ListeRetourLigne", (Serializable) liste_retour_ligne);
        controleDesRetour_Bundle.putBoolean("EmplacementUF", true);


        controleDesRetour_Intent.putExtras(controleDesRetour_Bundle);
        DetailControleDesRetours2025Activity.this.startActivityForResult(controleDesRetour_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }

    private void onClickTriDesignation()
    {
        tri_choisi = "Designation";
        Collections.sort(liste_retour_ligne, (oo1, oo2) -> oo1.getProduit_Designation().toLowerCase().compareTo(oo2.getProduit_Designation().toLowerCase()));
        gestionAdapter();
    }

    private void onClickTriCategorie()
    {
        tri_choisi = "Catégorie";
        Collections.sort(liste_retour_ligne, (oo1, oo2) -> {
            Produit produit1 = ProduitOpenHelper.getProduitByID(db, oo1.getCode_produit());
            Produit produit2 = ProduitOpenHelper.getProduitByID(db, oo2.getCode_produit());

            return produit1.getCategorie().toLowerCase().compareTo(produit2.getCategorie().toLowerCase());
        });

        gestionAdapter();
    }

    private void onClickTriParPoids()
    {
        tri_choisi = "Poids";
        Collections.sort(liste_retour_ligne, (oo1, oo2) -> {
            Produit produit1 = ProduitOpenHelper.getProduitByID(db, oo1.getCode_produit());
            Produit produit2 = ProduitOpenHelper.getProduitByID(db, oo2.getCode_produit());

            return  Double.compare(produit1.getPoids(), produit2.getPoids());
        });

        gestionAdapter();
    }

    private void onClickTriParPlace()
    {
        tri_choisi = "Place";
        Collections.sort(liste_retour_ligne, (oo1, oo2) -> {
            String oo1EmplacementParDefaut = oo1.getEmplacementOrigine();
            String oo2EmplacementParDefaut = oo2.getEmplacementOrigine();

            if(oo1EmplacementParDefaut == null || oo1EmplacementParDefaut.contentEquals("")){
                Produit produit = ProduitOpenHelper.getProduitByID(db, oo1.getCode_produit());
                oo1EmplacementParDefaut = produit.getEmplacement_PUI_Defaut();

            }
            if(oo2EmplacementParDefaut == null || oo2EmplacementParDefaut.contentEquals("")){
                Produit produit = ProduitOpenHelper.getProduitByID(db, oo2.getCode_produit());
                oo2EmplacementParDefaut = produit.getEmplacement_PUI_Defaut();
            }

            return oo1EmplacementParDefaut.compareTo(oo2EmplacementParDefaut);
        });

        gestionAdapter();
    }

    public void gestionAdapter()
    {
        retourLigneControleRetoursAdapter = new Retour_Ligne_ControleRetoursAdapter_2025(DetailControleDesRetours2025Activity.this, liste_retour_ligne, db, retourSelectionne);
        retourLigneControleRetourAdapteListView.setAdapter(retourLigneControleRetoursAdapter);
    }

    @SuppressLint("SetTextI18n")
    public void afficherAlerteConfirmationRetour(Context context, LayoutInflater inflater) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_mail, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageTextView = (TextView) layout.findViewById(R.id.messageFin);
        messageTextView.setText("Toutes les références n'ont pas été retournées, souhaitez vous continuer ?");
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            onMenuSaveClick();
            alertDialog.dismiss();
        });

        buttonAnnuler.setOnClickListener(v -> alertDialog.dismiss());
    }
}
