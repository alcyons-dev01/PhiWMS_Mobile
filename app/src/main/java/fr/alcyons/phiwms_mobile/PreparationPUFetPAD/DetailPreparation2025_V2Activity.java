package fr.alcyons.phiwms_mobile.PreparationPUFetPAD;

import static com.google.android.gms.vision.L.TAG;
import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_SINGLE_PACK.serialisationDispenserSingle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparation2025Activity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparation2025Activity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparation2025_V2Activity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ImprimanteEtiquetteOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.ImprimanteEtiquette;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.AlertePreparationAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Preparation_Ligne_PreparationLotAdapter2025_V2;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.Chronometer;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
import fr.alcyons.phiwms_mobile.Services.ServicePreparationPadActivity;
import fr.alcyons.phiwms_mobile.Services.ServicePreparationPufActivity;

public class DetailPreparation2025_V2Activity extends ServiceAvecConnexionActivity {
    public PH_Preparation ph_preparation_Selectionne;
    public ListView phPreparationLigne_ListView;
    PH_Preparation_Ligne_PreparationLotAdapter2025_V2 ph_preparation_ligne_preparationLotAdapter;
    boolean premierpassage;
    String genre_preparation;
    List<PH_Preparation_Ligne> phPreparationLignes;
    List<String> liste_lot;
    Context context;
    String tri_choisi;
    int position_selectionne;
    Depot depotOrigine;
    List<ImprimanteEtiquette> listeImprimanteEtiquette;
    Spinner optionTri;
    public void enregistrerPhPreparation() throws JSONException {
        int nbTotalLotsAvecValeurSaisie = 0;

        int nbLigneSansValeur = 0;
        List<String> produitNonRenseigne = new ArrayList<>();
        List<Integer> listeIdPreparationLigne = new ArrayList<>();
        // On vérifie que toutes les ph_preparations_lignes ont une quantité pour au moins un lot
        for (PH_Preparation_Ligne ph_preparation_ligne : phPreparationLignes) {
            List<PH_Preparation_Ligne> ligneCourante = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, ph_preparation_Selectionne, ph_preparation_ligne.getProduitID());

            if(ligneCourante.isEmpty())
            {
                nbLigneSansValeur ++;
                produitNonRenseigne.add(ph_preparation_ligne.getProduitDesignation());
            }
        }

        if(nbLigneSansValeur > 0)
        {
            AfficherAlertePreparation(produitNonRenseigne, nbTotalLotsAvecValeurSaisie);
        }
        else
        {
            EnregistrerPreparation();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_preparation_modifiable);
        //déclaration de la position selectionné
        position_selectionne = 0;

        //gestion date début
        Chronometer.LancementChrono();
        //Gestion spinner
        //initi du tri
        optionTri = (Spinner) findViewById(R.id.optionTri);
        tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriPreparation(db);

        listeImprimanteEtiquette = ImprimanteEtiquetteOpenHelper.getAllImprimante(db);

        liste_lot = new ArrayList<>();
        context = DetailPreparation2025_V2Activity.this;
        premierpassage = true;
        // Récupération des variables globales
        ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, Objects.requireNonNull(intent.getExtras()).getInt("ph_preparationUID_Selectionne"));
        genre_preparation = intent.getExtras().getString("genre");
        // Affichage des informations de base
        depotOrigine = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());
        Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotDestinataireReference());

        String intitule = "#" + String.valueOf(ph_preparation_Selectionne.getUID());
        ((TextView) findViewById(R.id.intitule)).setText(intitule);

        String textDepot = "";
        if(depot != null)
        {
            textDepot = depot.getNom();
            if(utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depot.getStructure().contentEquals("PAD"))
            {
                textDepot = "Patient - "+depot.getPAD_IPP();
            }
        }

        ((TextView) findViewById(R.id.depot)).setText(textDepot);

        ((LinearLayout) findViewById(R.id.lancerScan)).setOnClickListener(view -> onMenuDatamatrixClick());

        // Gestion de la listView
        phPreparationLigne_ListView = (ListView) findViewById(R.id.listeView);

        phPreparationLignes = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, ph_preparation_Selectionne);

        phPreparationLignes.sort((o1, o2) -> Double.compare(o2.getProduitPoids(), o1.getProduitPoids()));

        phPreparationLigne_ListView.setOnItemClickListener((parent, view, position, id) -> {
            position_selectionne = position;
            PH_Preparation_Ligne ph_preparationLigne = ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_liste.get(position);

            Intent detailPreparation_Intent = new Intent(DetailPreparation2025_V2Activity.this, ListeLotPreparation2025_V2Activity.class);
            Bundle detailPreparation_Bundle = DetailPreparation2025_V2Activity.super.getBundle();
            detailPreparation_Bundle.putInt("phPreparationLigneId", ph_preparationLigne.get_UID());
            detailPreparation_Intent.putExtras(detailPreparation_Bundle);

            DetailPreparation2025_V2Activity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
        });

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
                        onTriParPlace();
                        break;
                    case "Poids":
                        onTriParPoids();
                        break;
                    case "Categorie":
                        onClickTriCategorie();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if(!ph_preparation_Selectionne.getListe().contentEquals("ALCYONS_LISTE"))
        {
            if (statutConnexion && passageParOnCreate)
            {
                RequestQueue requestQueue = Volley.newRequestQueue(DetailPreparation2025_V2Activity.this);
                String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePreparationDetail+ph_preparation_Selectionne.getUID();

                JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
                requestQueue.add(obreq);
            }
            else
            {
                ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter2025_V2(DetailPreparation2025_V2Activity.this, db, utilisateurConnecte);

                List<String> listeZoneEmplacement = new ArrayList<>();
                for(PH_Preparation_Ligne ph_preparationLigne : phPreparationLignes)
                {
                    Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
                    String zone = produit.getZone_PUI_Defaut();
                    String emplacement = produit.getEmplacement_PUI_Defaut();
                    String zoneemplacement = zone + "-" + emplacement;

                    if(!listeZoneEmplacement.contains(zoneemplacement)) {
                        listeZoneEmplacement.add(zoneemplacement);
                        ph_preparation_ligne_preparationLotAdapter.addSectionHeaderItem(ph_preparationLigne);
                    }

                    ph_preparation_ligne_preparationLotAdapter.addItem(ph_preparationLigne);
                }

                phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
                phPreparationLigne_ListView.setDivider(null);
                ph_preparation_ligne_preparationLotAdapter.notifyDataSetChanged();
            }
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(ph_preparation_ligne_preparationLotAdapter != null)
                {
                    if (!ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_liste.isEmpty()) {
                        afficherAlerteConfirmationRetour(DetailPreparation2025_V2Activity.this, LayoutInflater.from(DetailPreparation2025_V2Activity.this), DetailPreparation2025_V2Activity.super.getBundle());
                    } else {
                        retourService(DetailPreparation2025_V2Activity.super.getBundle());
                    }
                }
                else
                {
                    retourService(DetailPreparation2025_V2Activity.super.getBundle());
                }
            }
        });

        if(!passageParOnCreate)
        {
            invalidateOptionsMenu();
            switch(tri_choisi)
            {
                case "Designation":
                    onClickTriDesignation();
                    break;
                case "Place":
                    onTriParPlace();
                    break;
                case "Poids":
                    onTriParPoids();
                    break;
                case "Categorie":
                    onClickTriCategorie();
                    break;
            }
        }
    }

    @NonNull
    private JsonObjectRequest getJsonObjectRequest(String urlRequete) {
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int nbResultat = response.getInt("resultCount");
                            if (nbResultat == 0) {
                                String erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");;
                                } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                    Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Aucune ligne trouvée", "alerte");
                                }
                            } else {
                                JSONArray ph_preparationLigne_JSONArray = response.getJSONArray("PH_Preparation_Ligne");
                                for (int k = 0; k < ph_preparationLigne_JSONArray.length(); k++) {
                                    JSONObject ph_preparationLigne_JSONObject = ph_preparationLigne_JSONArray.getJSONObject(k);
                                    JSONArray phStockLotEmplacement_JSONArray = ph_preparationLigne_JSONObject.getJSONArray("ph_stock_lot_emplacements");

                                    for (int y = 0; y < phStockLotEmplacement_JSONArray.length(); y++) {
                                        Stock_Lot_Emplacement_Light stock_lot_emplacement_light = new Stock_Lot_Emplacement_Light(phStockLotEmplacement_JSONArray.getJSONObject(y));
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
                                    }
                                }

                                for (PH_Preparation_Ligne phPrepLigne : phPreparationLignes) {
                                    if ((phPrepLigne.getQte_APreparer() > 0 || phPrepLigne.getQte_Demander() == phPrepLigne.getQte_preparer()) && phPrepLigne.getQte_APreparer() != 0) {

                                        Produit produit = ProduitOpenHelper.getProduitByID(db, phPrepLigne.getProduitID());
                                        if(produit != null)
                                        {
                                            List<Stock_Lot_Emplacement_Light> stock_lot_emplacement_lightList = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depotOrigine);

                                            //récupération des ph_preparation_ligne déjà préparer
                                            List<PH_Preparation_Ligne> lignes_preparer = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, ph_preparation_Selectionne, phPrepLigne.getProduitID());

                                            stock_lot_emplacement_lightList.sort(Comparator.comparing(Stock_Lot_Emplacement_Light::getLot));
                                            stock_lot_emplacement_lightList.sort(Comparator.comparing(Stock_Lot_Emplacement_Light::getPeremptionDate));

                                            for (Stock_Lot_Emplacement_Light stockLotEmplacement : stock_lot_emplacement_lightList) {
                                                if (stockLotEmplacement.getQte() >= 0) {
                                                    stockLotEmplacement.setQte_Preparer(0);
                                                    for (PH_Preparation_Ligne ligne_courante : lignes_preparer) {
                                                        if (ligne_courante.getLotNumero().contentEquals(stockLotEmplacement.getLot()) && ligne_courante.getEmplacementParDefaut().contentEquals(stockLotEmplacement.getEmplacement())) {
                                                            stockLotEmplacement.setQte_Preparer((int) ligne_courante.getQte_preparer());
                                                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacement);
                                                            break;
                                                        }
                                                    }
                                                    liste_lot.add(stockLotEmplacement.getLot());
                                                }
                                            }
                                        }
                                    }
                                }

                                invalidateOptionsMenu();
                                switch(tri_choisi)
                                {
                                    case "Designation":
                                        onClickTriDesignation();
                                        break;
                                    case "Place":
                                        onTriParPlace();
                                        break;
                                    case "Poids":
                                        onTriParPoids();
                                        break;
                                    case "Categorie":
                                        onClickTriCategorie();
                                        break;
                                }

                                passageParOnCreate = false;
                                arreterSpinner();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(DetailPreparation2025_V2Activity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Préparation PAD)", "alerte");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                return headers;
            }
        };
        obreq.setRetryPolicy(retryPolicy);
        return obreq;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_LOTS:
                    if(ph_preparation_ligne_preparationLotAdapter != null)
                    {
                        ph_preparation_ligne_preparationLotAdapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.menu_preparation_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, null, null, "Désignation référence");

        MenuItem valider = menu.findItem(R.id.boutonValider);
        valider.setOnMenuItemClickListener(menuItem -> {
            //vérification de la saisie d'au moins une ligne
            int nbLigneSansValeur = 0;
            // On vérifie que toutes les ph_preparations_lignes ont une quantité pour au moins un lot
            List<PH_Preparation_Ligne> listePreparer = new ArrayList<>();
            listePreparer = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationNeg(db, ph_preparation_Selectionne);

            if(listePreparer.isEmpty())
            {
                Alerte.afficherAlerte(DetailPreparation2025_V2Activity.this, "Attention", "Vous n'avez pas saisie de ligne", "alerte");
            }
            else
            {
                onClick_ActionContenant();
            }
            return true;
        });

        return true;
    }

    private void onClickTriDesignation()
    {
        tri_choisi = "Designation";
        phPreparationLignes.sort((o1, o2) -> {

            return o1.getProduitDesignation().compareTo(o2.getProduitDesignation());
        });
        premierpassage = false;
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter2025_V2(DetailPreparation2025_V2Activity.this, db, utilisateurConnecte);

        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Preparation_Ligne ph_preparationLigne : phPreparationLignes)
        {
            Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
            String zone = produit.getZone_PUI_Defaut();
            String emplacement = produit.getEmplacement_PUI_Defaut();
            String zoneemplacement = zone + "-" + emplacement;

            if(!listeZoneEmplacement.contains(zoneemplacement)) {
                listeZoneEmplacement.add(zoneemplacement);
                ph_preparation_ligne_preparationLotAdapter.addSectionHeaderItem(ph_preparationLigne);
            }

            ph_preparation_ligne_preparationLotAdapter.addItem(ph_preparationLigne);
        }

        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
        phPreparationLigne_ListView.setDivider(null);
        phPreparationLigne_ListView.setSelection(position_selectionne);
    }

    private void onClickTriCategorie()
    {
        tri_choisi = "Categorie";
        phPreparationLignes.sort((o1, o2) -> {
            String categorie1 = o1.getProduitCategorie();
            String categorie2 = o2.getProduitCategorie();

            if(categorie1 == null)
                categorie1 = "";

            if(categorie2 == null)
                categorie2 = "";

            return categorie1.compareTo(categorie2);
        });
        premierpassage = false;
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter2025_V2(DetailPreparation2025_V2Activity.this, db, utilisateurConnecte);

        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Preparation_Ligne ph_preparationLigne : phPreparationLignes)
        {
            Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
            String zone = produit.getZone_PUI_Defaut();
            String emplacement = produit.getEmplacement_PUI_Defaut();
            String zoneemplacement = zone + "-" + emplacement;

            if(!listeZoneEmplacement.contains(zoneemplacement)) {
                listeZoneEmplacement.add(zoneemplacement);
                ph_preparation_ligne_preparationLotAdapter.addSectionHeaderItem(ph_preparationLigne);
            }

            ph_preparation_ligne_preparationLotAdapter.addItem(ph_preparationLigne);
        }

        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
        phPreparationLigne_ListView.setDivider(null);
        phPreparationLigne_ListView.setSelection(position_selectionne);
    }

    private void onTriParPlace()
    {
        tri_choisi = "Place";
        phPreparationLignes.sort((oo1, oo2) -> {

            if(oo1 != null && oo2 != null)
            {
                String oo1EmplacementParDefaut = oo1.getEmplacementParDefaut();
                String oo2EmplacementParDefaut = oo2.getEmplacementParDefaut();
                Produit produit001 = ProduitOpenHelper.getProduitByID(db, oo1.getProduitID());
                Produit produit002 = ProduitOpenHelper.getProduitByID(db, oo2.getProduitID());

                oo1EmplacementParDefaut = produit001.getEmplacement_PUI_Defaut();
                oo2EmplacementParDefaut = produit002.getEmplacement_PUI_Defaut();

                String zone001 = produit001.getZone_PUI_Defaut();
                String zone002 = produit002.getZone_PUI_Defaut();

                if(zone001.contentEquals(zone002))
                {
                    if(oo1EmplacementParDefaut.contentEquals(oo2EmplacementParDefaut))
                    {
                        return oo1.getProduitDesignation().compareTo(oo2.getProduitDesignation());
                    }
                    else
                    {
                        return oo1EmplacementParDefaut.compareTo(oo2EmplacementParDefaut);
                    }
                }
                else
                {
                    return zone001.compareTo(zone002);
                }
            }
            else
            {
                return 1;
            }

        });
        premierpassage = false;
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter2025_V2(DetailPreparation2025_V2Activity.this, db, utilisateurConnecte);

        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Preparation_Ligne ph_preparationLigne : phPreparationLignes)
        {
            if(ph_preparationLigne != null)
            {
                Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
                String zone = produit.getZone_PUI_Defaut();
                String emplacement = produit.getEmplacement_PUI_Defaut();
                String zoneemplacement = zone + "-" + emplacement;

                if(!listeZoneEmplacement.contains(zoneemplacement)) {
                    listeZoneEmplacement.add(zoneemplacement);
                    ph_preparation_ligne_preparationLotAdapter.addSectionHeaderItem(ph_preparationLigne);
                }

                ph_preparation_ligne_preparationLotAdapter.addItem(ph_preparationLigne);
            }
        }

        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
        phPreparationLigne_ListView.setDivider(null);
        phPreparationLigne_ListView.setSelection(position_selectionne);
    }

    private void onTriParPoids()
    {
        tri_choisi = "Poids";
        phPreparationLignes.sort((oo1, oo2) -> {
            if(oo1.getPoidsTotal() == oo2.getPoidsTotal())
            {
                return oo1.getProduitDesignation().compareTo(oo2.getProduitDesignation());
            }
            else
            {
                return Double.compare(oo1.getPoidsTotal(), oo2.getPoidsTotal());
            }
        });
        premierpassage = false;
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter2025_V2(DetailPreparation2025_V2Activity.this, db, utilisateurConnecte);

        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Preparation_Ligne ph_preparationLigne : phPreparationLignes)
        {
            Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
            String zone = produit.getZone_PUI_Defaut();
            String emplacement = produit.getEmplacement_PUI_Defaut();
            String zoneemplacement = zone + "-" + emplacement;

            if(!listeZoneEmplacement.contains(zoneemplacement)) {
                listeZoneEmplacement.add(zoneemplacement);
                ph_preparation_ligne_preparationLotAdapter.addSectionHeaderItem(ph_preparationLigne);
            }

            ph_preparation_ligne_preparationLotAdapter.addItem(ph_preparationLigne);
        }

        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
        phPreparationLigne_ListView.setDivider(null);
        phPreparationLigne_ListView.setSelection(position_selectionne);
    }

    private void onMenuDatamatrixClick() {
        Intent detailPreparation_Intent = new Intent(DetailPreparation2025_V2Activity.this, BarcodePreparation2025Activity.class);

        List<PH_Preparation_Ligne> liste_ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesAPreparerParPHPreparation(db, ph_preparation_Selectionne);

        if(liste_ph_preparation_ligne.isEmpty())
        {
            Alerte.afficherAlerte(DetailPreparation2025_V2Activity.this, "Attention", "Toutes les références sont déjà préparées en intégralité", "alerte");
        }
        else
        {
            //gestion du zebra
            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google") || android.os.Build.MANUFACTURER.toLowerCase().contains("samsung"))
            {
                detailPreparation_Intent = new Intent(DetailPreparation2025_V2Activity.this, ScannerPreparation2025_V2Activity.class);
            }

            List<PH_Preparation_Ligne> preparationLignesBase = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, ph_preparation_Selectionne);

            Bundle detailPreparation_Bundle = DetailPreparation2025_V2Activity.super.getBundle();
            detailPreparation_Bundle.putString("contexte", String.valueOf(R.string.scannerContextPreparationMultiple));
            detailPreparation_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) liste_lot);
            detailPreparation_Bundle.putString("ordreTri", tri_choisi);
            detailPreparation_Bundle.putSerializable("liste_ph_preparation_ligne", (Serializable) preparationLignesBase);
            detailPreparation_Bundle.putString("Designation", ph_preparation_Selectionne.getListe());
            detailPreparation_Bundle.putBoolean("isBoutonSuppressionExistant", true);
            detailPreparation_Bundle.putInt("UserId", utilisateurConnecte.getId());
            detailPreparation_Bundle.putInt("preparationId", ph_preparation_Selectionne.getUID());
            detailPreparation_Bundle.putBoolean("modeRafale", true);

            detailPreparation_Intent.putExtras(detailPreparation_Bundle);
            // Appel de la prochaine activité
            DetailPreparation2025_V2Activity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_PREPARATION);
        }
    }

    public void onClick_ActionContenant() {
        final HashMap<Integer, Integer> resultat = new HashMap<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_preparation, null);

        LinearLayout LinearPalette = (LinearLayout) view.findViewById(R.id.LinearPalette);
        LinearLayout LinearColis = (LinearLayout) view.findViewById(R.id.LinearColis);
        LinearLayout LinearContainer = (LinearLayout) view.findViewById(R.id.LinearContainer);
        LinearLayout LinearScelle = (LinearLayout) view.findViewById(R.id.LinearScelle);
        TextView textViewNBPalette;
        TextView textViewNBCaisse;
        TextView textViewNBContainer;
        TextView textViewNBScelle;
        textViewNBPalette = (TextView) view.findViewById(R.id.nbPaletteSelectionne);
        textViewNBCaisse = (TextView) view.findViewById(R.id.nbColisSelectionne);
        textViewNBContainer = (TextView) view.findViewById(R.id.nbContainerSelectionne);
        textViewNBScelle = (TextView) view.findViewById(R.id.nbScelleSelectionne);
        final ImageView imagePalette = (ImageView) view.findViewById(R.id.iconPalette);
        final ImageView imageContainer = (ImageView) view.findViewById(R.id.iconContainer);
        final ImageView imageScelle = (ImageView) view.findViewById(R.id.iconScelle);

        //on calcule le nombre de colis
        int nbColis = 0;
        for(PH_Preparation_Ligne ph_preparation_ligne : phPreparationLignes)
        {
            if(ph_preparation_ligne.getQte_Demander() > 0 && ph_preparation_ligne.getQte_preparer() > 0)
            {
                Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne.getProduitID());
                int conditionnement = (int) produit.getCond_distrib();
                if(conditionnement == 0)
                {
                    conditionnement = 1;
                }
                int nbColisProduit = (int) ((int) ph_preparation_ligne.getQte_preparer() / (int) conditionnement);

                if(nbColisProduit == 0)
                {
                    nbColisProduit = 1;
                }
                nbColis = nbColis + nbColisProduit;
            }
        }

        textViewNBCaisse.setText(String.valueOf(nbColis));

        /* Gestion des numbers pickers */

        //gestion des colis
        textViewNBCaisse.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un NumberPicker
            String title = "Saisir le nombre de colis";
            String message = "Nombre de colis : ";
            int maxValue = 20;
            int value = 0;

            DialogInterface.OnClickListener onClickListener = (dialog, id) -> {

                int qteApres = aNumberPicker.getValue();
                textViewNBCaisse.setText(String.valueOf(qteApres));
                InputMethodManager imm = (InputMethodManager) DetailPreparation2025_V2Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
            };

            Alerte.afficherAlerteNumberPicker(DetailPreparation2025_V2Activity.this, title, message, value, maxValue, onClickListener);
        });


        //gestion des palettes
        LinearPalette.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un NumberPicker
            textViewNBPalette.performClick();
        });

        textViewNBPalette.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un NumberPicker
            String title = "Saisir le nombre de palette";
            String message = "Nombre de palettes : ";
            int maxValue = 15;
            int value = 0;

            DialogInterface.OnClickListener onClickListener = (dialog, id) -> {
                imagePalette.setVisibility(View.GONE);
                textViewNBPalette.setVisibility(View.VISIBLE);
                int qteApres = aNumberPicker.getValue();
                textViewNBPalette.setText(String.valueOf(qteApres));
                dialog.dismiss();
            };

            Alerte.afficherAlerteNumberPicker(DetailPreparation2025_V2Activity.this, title, message, value, maxValue, onClickListener);
        });

        //gestion des container
        LinearContainer.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un NumberPicker
            textViewNBContainer.performClick();
        });

        textViewNBContainer.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un NumberPicker
            String title = "Saisir le nombre de container";
            String message = "Nombre de container : ";
            int maxValue = 15;
            int value = 0;

            DialogInterface.OnClickListener onClickListener = (dialog, id) -> {
                imageContainer.setVisibility(View.GONE);
                textViewNBContainer.setVisibility(View.VISIBLE);
                int qteApres = aNumberPicker.getValue();
                textViewNBContainer.setText(String.valueOf(qteApres));
                dialog.dismiss();
            };

            Alerte.afficherAlerteNumberPicker(DetailPreparation2025_V2Activity.this, title, message, value, maxValue, onClickListener);
        });

        //gestion des scelles
        LinearScelle.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un NumberPicker
            textViewNBScelle.performClick();
        });

        textViewNBScelle.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un edittext
            String textscelle = Alerte.afficherAlerteEditText(DetailPreparation2025_V2Activity.this, "Numéro de scellé", "Saisir un numéro de scellé");
            imageScelle.setVisibility(View.GONE);
            textViewNBScelle.setVisibility(View.VISIBLE);
            textViewNBScelle.setText(textscelle);
        });

        ImageView ok = (ImageView) view.findViewById(R.id.ok);
        ImageView annuler = (ImageView) view.findViewById(R.id.annuler);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ok.setOnClickListener(v -> {
            int nbPalette = 0;
            if(!textViewNBPalette.getText().toString().contentEquals(""))
                nbPalette = Integer.parseInt(textViewNBPalette.getText().toString());

            int nbCaisse = 0;
            if(!textViewNBCaisse.getText().toString().contentEquals(""))
                nbCaisse = Integer.parseInt(textViewNBCaisse.getText().toString());

            int Conteneur_NB = 0;
            if(!textViewNBContainer.getText().toString().contentEquals(""))
                Conteneur_NB = Integer.parseInt(textViewNBContainer.getText().toString());

            String numero_scelle = "";
            textViewNBScelle.getText().toString();
            numero_scelle = textViewNBScelle.getText().toString();

            ph_preparation_Selectionne.setColisNB(nbCaisse);
            ph_preparation_Selectionne.setPaletteNB(nbPalette);
            ph_preparation_Selectionne.setConteneur_NB(Conteneur_NB);
            ph_preparation_Selectionne.setNumero_scelle(numero_scelle);
            PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation_Selectionne);
            alertDialog.dismiss();
            try {
                enregistrerPhPreparation();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        annuler.setOnClickListener(view1 -> alertDialog.dismiss());
    }

    @SuppressLint("SetTextI18n")
    public void AfficherAlertePreparation(List<String> produitNonRenseigne, final int nbTotalLotsAvecValeurSaisie)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_reliquat_preparation, null);

        TextView numeroPreparation = view.findViewById(R.id.numeroPreparation);
        TextView depotDestinataire = view.findViewById(R.id.depotDestinataire);
        LinearLayout annuler = view.findViewById(R.id.annuler);
        LinearLayout valider = view.findViewById(R.id.valider);
        ListView depot_ListView = (ListView) view.findViewById(R.id.listeView);

        AlertePreparationAdapter adapter = new AlertePreparationAdapter(DetailPreparation2025_V2Activity.this, produitNonRenseigne);
        depot_ListView.setAdapter(adapter);

        //Gestion des objets graphique
        numeroPreparation.setText("#"+ph_preparation_Selectionne.getUID());

        Depot depotDestinataireAlerte = DepotOpenHelper.getDepotParID(db, ph_preparation_Selectionne.getDepotDestinataireID());
        String textDepotAlerte = depotDestinataireAlerte.getDepot_Reference();
        if(utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depotDestinataireAlerte.getStructure().contentEquals("PAD"))
        {
            textDepotAlerte = "Patient - "+depotDestinataireAlerte.getPAD_IPP();
        }
        depotDestinataire.setText(textDepotAlerte);

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        annuler.setOnClickListener(view1 -> alertDialog.dismiss());

        valider.setOnClickListener(view12 -> {
            try {
                EnregistrerPreparation();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            alertDialog.dismiss();
        });
    }

    public void EnregistrerPreparation() throws JSONException {
        List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> listeLot = new ArrayList<>();
        //Création de l'action utilisateur
        Random randomaction = new Random();
        int actionId = randomaction.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateDestruction =new Date();
        String date_string = parseFormat.format(dateDestruction);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", ph_preparation_Selectionne.getUID(), "", "Preparation");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
        //fin de la création de l'action utilisateur

        //on supprime les lignes de préparation de base
        List<PH_Preparation_Ligne> listeLigneBase = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, ph_preparation_Selectionne);
        for(PH_Preparation_Ligne ligneBase : listeLigneBase)
        {
            PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ligneBase);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ligneBase.getPhiMR4UUID(), ligneBase.get_UID(), DBOpenHelper.ActionsEAS.SUPPR);
        }

        List<PH_Preparation_Ligne> listeLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);
        for(PH_Preparation_Ligne lignecourante : listeLigne)
        {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, lignecourante.getPhiMR4UUID(), lignecourante.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);

            Produit produit_temp = ProduitOpenHelper.getProduitByID(db, lignecourante.getProduitID());
            Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if(actionligneId > 0)
                actionligneId= actionligneId*-1;
            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", lignecourante.get_UID(), "", 0, (int)lignecourante.getQte_preparer(), lignecourante.getProduitDesignation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionUtilisateur_ligne.getPhiMR4UUID(), actionUtilisateur_ligne.getId(), DBOpenHelper.ActionsEAS.AJOUT);

        }

        List<PH_Serialisation> list_serialisation = PH_SerialisationOpenHelper.getAllPH_SerialisationByMvtId(db, String.valueOf(ph_preparation_Selectionne.getUID()));
        if(!list_serialisation.isEmpty())
        {
            for(PH_Serialisation serialisation : list_serialisation)
            {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, serialisation.getPhiMR4UUID(), serialisation.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                Random randomAUSeri = new Random();
                int actionSerId = randomAUSeri.nextInt();
                if(actionSerId > 0)
                    actionSerId= actionSerId*-1;
                ActionUtilisateur new_action_utilisateur_serialisation = new ActionUtilisateur(actionSerId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", serialisation.get_UID(), "", "Serialisation");
                ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur_serialisation);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur_serialisation.getPhiMR4UUID(), new_action_utilisateur_serialisation.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            }
        }

        Date dateJour = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        //différence
        Chronometer.FinChrono();
        Chronometer.getChrono();
        String TempsPreparation = Chronometer.heure+":"+Chronometer.minute+":"+Chronometer.seconde;

        ph_preparation_Selectionne.setTempsPreparation(TempsPreparation);
        ph_preparation_Selectionne.setPreparationDate(format.format(dateJour));
        ph_preparation_Selectionne.setStatut(getString(R.string.PreparationEffectuee));


        long rowID = PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation_Selectionne);
        if (rowID != -1)
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation_Selectionne.getPhiMR4UUID(), ph_preparation_Selectionne.getUID(), DBOpenHelper.ActionsEAS.MAJ);

        //véfication de la totalité de la préparation
        List<PH_Preparation_Ligne> listePhPreparationRALListe;
        listePhPreparationRALListe = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesRAL(db, ph_preparation_Selectionne);
        if (!listePhPreparationRALListe.isEmpty()) {
            Toast.makeText(DetailPreparation2025_V2Activity.this, "Préparation effectuée en partie", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DetailPreparation2025_V2Activity.this, "Préparation effectuée", Toast.LENGTH_SHORT).show();
        }

        // Si possible, on essaie de mettre à jour les éléments
        ElementASynchroniserOpenHelper.toutSynchroniser(DetailPreparation2025_V2Activity.this, db, utilisateurConnecte, true);
        //NewDetailPreparationActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS);
        Intent retourListeIntent = new Intent(DetailPreparation2025_V2Activity.this, ServicePreparationPufActivity.class);
        if(ph_preparation_Selectionne.getDepotDestinataireReference().contains("-PAD-"))
            retourListeIntent = new Intent(DetailPreparation2025_V2Activity.this, ServicePreparationPadActivity.class);

        if(utilisateurConnecte.getEtablissement().toUpperCase().contentEquals("ADH") && listeImprimanteEtiquette.size() > 0)
        {
            if(listeImprimanteEtiquette.size() == 1)
                envoyerImpressionZebra(ph_preparation_Selectionne, listeImprimanteEtiquette.get(0).getNom());
            else
                afficherAlerteChoixImprimante(DetailPreparation2025_V2Activity.this, DetailPreparation2025_V2Activity.this.getLayoutInflater());
        }
        else
        {
            Bundle extras = new Bundle();
            extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            extras.putInt("serviceSelectionneID", serviceActuel.getId());
            retourListeIntent.putExtras(extras);
            DetailPreparation2025_V2Activity.this.startActivity(retourListeIntent);
            DetailPreparation2025_V2Activity.this.finish();
            return;
        }


    }

    @SuppressLint("SetTextI18n")
    public void afficherAlerteConfirmationRetour(Context context, LayoutInflater inflater, final Bundle bundle) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_mail, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageTextView = (TextView) layout.findViewById(R.id.messageFin);
        messageTextView.setText("Vous allez quitter la préparation, confirmez vous ?");
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            retourService(bundle);
        });

        buttonAnnuler.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void retourService(final Bundle bundle)
    {
        Intent detailPreparationIntent = null;
        if (genre_preparation.contentEquals("PUF")) {
            detailPreparationIntent = new Intent(DetailPreparation2025_V2Activity.this, ServicePreparationPufActivity.class);
        } else {
            detailPreparationIntent = new Intent(DetailPreparation2025_V2Activity.this, ServicePreparationPadActivity.class);
        }
        Bundle detailPreparationBundle = super.getBundle();
        detailPreparationIntent.putExtras(detailPreparationBundle);
        DetailPreparation2025_V2Activity.this.startActivity(detailPreparationIntent);
        DetailPreparation2025_V2Activity.this.finish();
    }

    private void envoyerImpressionZebra(PH_Preparation ph_preparation, String nomImprimante) throws JSONException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(cal.getTime());

        JSONArray Etiquette_TO = new JSONArray();

        String DepotOrigineNom_VT = depotOrigine.getNom();

        Depot depotDestinataire = DepotOpenHelper.getDepotParID(db, ph_preparation.getDepotDestinataireID());
        String CPDestinataire_VT = depotDestinataire.getCP();
        String VilleDestinataire_VT = depotDestinataire.getVille();
        String nomDestinataire_VT = depotDestinataire.getNom();

        String preparerPar_VT= "";
        String validerPar_VT = "";
        if(!ph_preparation.getPreparateur().isEmpty())
        {
            String[] tabPreparateur = ph_preparation.getPreparateur().split("\\(");
            if(tabPreparateur.length > 1)
            {
                preparerPar_VT = tabPreparateur[0];
                validerPar_VT = tabPreparateur[1].substring(0,tabPreparateur[1].length()-1);
            }
            else
            {
                preparerPar_VT = ph_preparation.getPreparateur();
            }
        }

        JSONObject codeBarre_JO = new JSONObject();
        codeBarre_JO.put("type", "Datamatrix");
        codeBarre_JO.put("phitag", "DDS:"+ph_preparation.getPHIE_Tag());

        JSONObject etiquette_v1_JO = new JSONObject();
        etiquette_v1_JO.put("codeBarre", codeBarre_JO);
        etiquette_v1_JO.put("phiTag", String.valueOf(ph_preparation.getUID()));
        etiquette_v1_JO.put("titre", ph_preparation.getListe());
        etiquette_v1_JO.put("CPDestinataire", CPDestinataire_VT);
        etiquette_v1_JO.put("villeDestinataire", VilleDestinataire_VT);
        etiquette_v1_JO.put("destinataire", nomDestinataire_VT);
        etiquette_v1_JO.put("nbCartons", String.valueOf(ph_preparation.getColisNB()));
        etiquette_v1_JO.put("nbPalette", String.valueOf(ph_preparation.getPaletteNB()));
        etiquette_v1_JO.put("nbConteneur", String.valueOf(ph_preparation.getConteneur_NB()));
        etiquette_v1_JO.put("poids", String.valueOf(ph_preparation.getPoids()));
        etiquette_v1_JO.put("date", strDate);
        etiquette_v1_JO.put("etablissement", DepotOrigineNom_VT);
        etiquette_v1_JO.put("preparationvaliderpar", validerPar_VT);
        etiquette_v1_JO.put("preparationpreparerpar", preparerPar_VT);
        etiquette_v1_JO.put("numContenant", 1);
        etiquette_v1_JO.put("nbContenant", 1);

        List<PH_Preparation_Ligne> listeph_preparation_ligne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation);
        int compteur = 1;
        String tempAmbiante_VS = "false";
        String fragile_VS = "false";
        String abriLumiere_VS = "false";
        String medicamentARisque = "false";
        String numeroScelle = "";
        int tempMax_VN = 0;
        int tempMin_VN = 0;

        for(PH_Preparation_Ligne ligne_courante : listeph_preparation_ligne)
        {
            Produit produitcourant = ProduitOpenHelper.getProduitByID(db, ligne_courante.getProduitID());

            if(produitcourant.isTemperature_Ambiante())
                tempAmbiante_VS = "true";

            if(produitcourant.isConservation_abri())
                abriLumiere_VS = "true";

            if(tempMax_VN < produitcourant.getConservation_temperature_Max())
                tempMax_VN = (int) produitcourant.getConservation_temperature_Max();

            if(tempMin_VN > produitcourant.getConservation_temperature_min())
                tempMin_VN = (int) produitcourant.getConservation_temperature_min();

            if(produitcourant.isMedicament_Risque())
                medicamentARisque = "MEDICAMENT À RISQUE";

            compteur ++;
        }

        JSONObject refrigere_JO = new JSONObject();
        refrigere_JO.put("tempMin", String.valueOf(tempMin_VN));
        refrigere_JO.put("tempMax", String.valueOf(tempMax_VN));

        JSONObject symbole_JO = new JSONObject();
        symbole_JO.put("scelle", numeroScelle);
        symbole_JO.put("ambiante", tempAmbiante_VS);
        symbole_JO.put("fragile", fragile_VS);
        symbole_JO.put("abrilumiere", abriLumiere_VS);
        symbole_JO.put("refrigere", refrigere_JO);
        etiquette_v1_JO.put("symboles", symbole_JO);

        etiquette_v1_JO.put("medicamentrisque", medicamentARisque);

        Etiquette_TO.put(etiquette_v1_JO);

        String imprimante_VT = nomImprimante;
        String aImprimer = "true";
        String format = "Préparation";

        JSONObject body = new JSONObject();
        try {
            body.put("Imprimante", imprimante_VT);
            body.put("aImprimer", aImprimer);
            body.put("format", format);
            body.put("etiquettes", Etiquette_TO);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException :", e);
        }
        String urlRequete = ParametresServeurOpenHelper.getUrlsWeb(db) + DBOpenHelper.Urls.uriZebraImprimer;
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
            Toast.makeText(DetailPreparation2025_V2Activity.this, "Etiquette envoyée", Toast.LENGTH_SHORT).show();
            Intent retourListeIntent = new Intent(DetailPreparation2025_V2Activity.this, ServicePreparationPufActivity.class);
            if(ph_preparation_Selectionne.getDepotDestinataireReference().contains("-PAD-"))
                retourListeIntent = new Intent(DetailPreparation2025_V2Activity.this, ServicePreparationPadActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            extras.putInt("serviceSelectionneID", serviceActuel.getId());
            retourListeIntent.putExtras(extras);
            DetailPreparation2025_V2Activity.this.startActivity(retourListeIntent);
            DetailPreparation2025_V2Activity.this.finish();
        },
                error -> {
                    Log.e("Etiquette Volley", error.toString());
                    if(!error.toString().contains("\"isOk\":true"))
                    {
                        Alerte.afficherAlerte(DetailPreparation2025_V2Activity.this, "Erreur HTTP", "Erreur lors de l\'impression de l\'étiquette : "+error.toString(), "alerte");
                    }
                    else
                    {
                        Toast.makeText(DetailPreparation2025_V2Activity.this, "Etiquette envoyée", Toast.LENGTH_SHORT).show();
                    }
                    Intent retourListeIntent = new Intent(DetailPreparation2025_V2Activity.this, ServicePreparationPufActivity.class);
                    if(ph_preparation_Selectionne.getDepotDestinataireReference().contains("-PAD-"))
                        retourListeIntent = new Intent(DetailPreparation2025_V2Activity.this, ServicePreparationPadActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    extras.putInt("serviceSelectionneID", serviceActuel.getId());
                    retourListeIntent.putExtras(extras);
                    DetailPreparation2025_V2Activity.this.startActivity(retourListeIntent);
                    DetailPreparation2025_V2Activity.this.finish();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json;charset=utf-8");
                return params;
            }
        };
        RequestQueue requestQueueUtilisateur = Volley.newRequestQueue(this);
        requestQueueUtilisateur.add(obreq);
    }

    private void afficherAlerteChoixImprimante(Context context, LayoutInflater inflater)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_selection_imprimante, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.fermer_alerte_imprimante_zebra);
        Spinner spinnerImprimante = (Spinner) layout.findViewById(R.id.spinnerImprimante);

        List<String> ListNomImprimante = new ArrayList<>();
        for(ImprimanteEtiquette imprimante : listeImprimanteEtiquette)
        {
            ListNomImprimante.add(imprimante.getNom());
        }
        ArrayAdapter<String> adapterImprimante= new ArrayAdapter<String>(this,
                R.layout.inscription_spinner_item, ListNomImprimante);
        spinnerImprimante.setAdapter(adapterImprimante);


        builder.setView(layout);
        android.app.AlertDialog alertDialogListeImprimante = builder.create();
        Objects.requireNonNull(alertDialogListeImprimante.getWindow()).setGravity(Gravity.CENTER);
        alertDialogListeImprimante.show();

        zoneok.setOnClickListener(v -> {

            String nomImprimante = spinnerImprimante.getSelectedItem().toString();
            try {
                envoyerImpressionZebra(ph_preparation_Selectionne, nomImprimante);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        buttonAnnuler.setOnClickListener(v -> alertDialogListeImprimante.dismiss());
    }
}
