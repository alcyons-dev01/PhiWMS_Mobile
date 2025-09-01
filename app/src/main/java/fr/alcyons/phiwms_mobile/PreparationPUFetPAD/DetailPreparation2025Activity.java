package fr.alcyons.phiwms_mobile.PreparationPUFetPAD;

import static com.google.android.gms.vision.L.TAG;
import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_SINGLE_PACK.serialisationDispenserSingle;
import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_SINGLE_PACK.serialisationVerificationSingle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparation2025Activity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparation2025Activity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
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
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.AlertePreparationAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Preparation_Ligne_PreparationLotAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Preparation_Ligne_PreparationLotAdapter2025;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.Chronometer;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
import fr.alcyons.phiwms_mobile.Services.ServicePreparationPadActivity;
import fr.alcyons.phiwms_mobile.Services.ServicePreparationPufActivity;
import java.util.Calendar;

public class DetailPreparation2025Activity  extends ServiceAvecConnexionActivity {
    public PH_Preparation ph_preparation_Selectionne;
    Serialisation serialisation;
    PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapteRetourListeLot;
    boolean utilisation_scan;
    List<String> listeGTIN;
    public List<PH_Preparation_Ligne_Preparation_Adapte> phPreparationLignePreparationAdapte_List;
    public ListView phPreparationLigne_ListView;
    PH_Preparation_Ligne_PreparationLotAdapter2025 ph_preparation_ligne_preparationLotAdapter;
    boolean premierpassage;
    int nb_produit_scanne;
    Integer nbLotPreparer;
    String genre_preparation;
    TextView textViewNBPalette;
    TextView textViewNBCaisse;
    TextView textViewNBContainer;
    TextView textViewNBScelle;
    List<PH_Preparation_Ligne> phPreparationLignes;
    List<String> liste_lot;
    PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapteSelectionne;
    PH_Preparation_Ligne_PreparationLotAdapter.PH_PreparationLigneViewHolder ph_preparationLigneViewHolder;
    Context context;
    String tri_choisi;
    PackageManager pm;
    int position_selectionne;
    Depot depotOrigine;

    public void enregistrerPhPreparation() throws JSONException {
        int nbTotalLotsAvecValeurSaisie = 0;

        int nbLigneSansValeur = 0;
        List<String> produitNonRenseigne = new ArrayList<>();
        List<Integer> listeIdPreparationLigne = new ArrayList<>();
        // On vérifie que toutes les ph_preparations_lignes ont une quantité pour au moins un lot
        for (PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte : ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes) {
            if(!listeIdPreparationLigne.contains(ph_preparationLigneAdapte.getPh_preparationLigneID()))
            {
                listeIdPreparationLigne.add(ph_preparationLigneAdapte.getPh_preparationLigneID());
                int nbLignesAvecValeur = 0;

                for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot : ph_preparationLigneAdapte.getLotAdaptes())
                {
                    if (lot.getQteSaisie() != 0) {
                        nbLignesAvecValeur++;
                        nbTotalLotsAvecValeurSaisie++;
                    }
                }
                if (nbLignesAvecValeur == 0) {
                    nbLigneSansValeur ++;
                    PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigneAdapte.getPh_preparationLigneID());
                    produitNonRenseigne.add(ph_preparation_ligne.getProduitDesignation());
                }
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

        //gestion du package manager
        pm = DetailPreparation2025Activity.this.getPackageManager();

        //gestion date début
        Chronometer.LancementChrono();
        //Gestion spinner
        //initi du tri
        tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriPreparation(db);
        if(tri_choisi == null)
        {
            ParametreUtilisateurOpenHelper.mettreAJourTriPreparation(db, 0, "Place");
            tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriPreparation(db);
        }

        liste_lot = new ArrayList<>();
        context = DetailPreparation2025Activity.this;
        premierpassage = true;
        // Récupération des variables globales
        ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, Objects.requireNonNull(intent.getExtras()).getInt("ph_preparationUID_Selectionne"));
        genre_preparation = intent.getExtras().getString("genre");
        utilisation_scan = false;
        serialisation = new Serialisation(DetailPreparation2025Activity.this, db, utilisateurConnecte);
        listeGTIN = new ArrayList<>();
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
        phPreparationLignePreparationAdapte_List = new ArrayList<>();

        phPreparationLignes = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, ph_preparation_Selectionne);

        phPreparationLignePreparationAdapte_List = new ArrayList<>();

        nbLotPreparer = 0;
        phPreparationLignes.sort((o1, o2) -> Double.compare(o2.getProduitPoids(), o1.getProduitPoids()));

        phPreparationLigne_ListView.setOnItemClickListener((parent, view, position, id) -> {
            utilisation_scan = false;
            position_selectionne = position;
            PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte = ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.get(position);
            PH_Preparation_Ligne ph_preparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigneAdapte.getPh_preparationLigneID());
            List<PH_Preparation_Ligne> listPhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, ph_preparation_Selectionne, ph_preparationLigne.getProduitID());
            for(PH_Preparation_Ligne courant : listPhPreparationLigne)
            {
                for(PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte : ph_preparationLigneAdapte.getLotAdaptes())
                {
                    if(lotAdapte.getNumLot().contentEquals(courant.getLotNumero()) && lotAdapte.getEmplacement().contentEquals(courant.getEmplacementParDefaut()))
                    {
                        lotAdapte.setQteSaisie(courant.getQte_preparer());
                    }
                }
            }

            Intent detailPreparation_Intent = new Intent(DetailPreparation2025Activity.this, ListeLotPreparation2025Activity.class);
            Bundle detailPreparation_Bundle = DetailPreparation2025Activity.super.getBundle();
            detailPreparation_Bundle.putInt("produitID", ph_preparationLigne.getProduitID());
            detailPreparation_Bundle.putSerializable("ph_preparationLigneAdapte", ph_preparationLigneAdapte);
            detailPreparation_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) liste_lot);

            Depot depot1 = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());

            detailPreparation_Bundle.putInt("depotID", depot1.getDepot_UID());
            detailPreparation_Intent.putExtras(detailPreparation_Bundle);

            DetailPreparation2025Activity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
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
                RequestQueue requestQueue = Volley.newRequestQueue(DetailPreparation2025Activity.this);
                String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePreparationDetail+ph_preparation_Selectionne.getUID();

                JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
                requestQueue.add(obreq);
            }
            else
            {
                ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter2025(DetailPreparation2025Activity.this, db, utilisateurConnecte);

                List<String> listeZoneEmplacement = new ArrayList<>();
                for(PH_Preparation_Ligne_Preparation_Adapte courant : phPreparationLignePreparationAdapte_List)
                {
                    PH_Preparation_Ligne ph_preparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, courant.getPh_preparationLigneID());
                    Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
                    String zone = produit.getZone_PUI_Defaut();
                    String emplacement = produit.getEmplacement_PUI_Defaut();
                    String zoneemplacement = zone + "-" + emplacement;

                    if(!listeZoneEmplacement.contains(zoneemplacement)) {
                        listeZoneEmplacement.add(zoneemplacement);
                        ph_preparation_ligne_preparationLotAdapter.addSectionHeaderItem(courant);
                    }

                    ph_preparation_ligne_preparationLotAdapter.addItem(courant);
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
                    if (!ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.isEmpty()) {
                        afficherAlerteConfirmationRetour(DetailPreparation2025Activity.this, LayoutInflater.from(DetailPreparation2025Activity.this), DetailPreparation2025Activity.super.getBundle());
                    } else {
                        retourService(DetailPreparation2025Activity.super.getBundle());
                    }
                }
                else
                {
                    retourService(DetailPreparation2025Activity.super.getBundle());
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

                                phPreparationLignePreparationAdapte_List = new ArrayList<>();
                                for (PH_Preparation_Ligne phPrepLigne : phPreparationLignes) {
                                    if ((phPrepLigne.getQte_APreparer() > 0 || phPrepLigne.getQte_Demander() == phPrepLigne.getQte_preparer()) && phPrepLigne.getQte_APreparer() != 0) {
                                        List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotAdaptes = null;

                                        PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte = new PH_Preparation_Ligne_Preparation_Adapte(phPrepLigne.get_UID());
                                        Produit produit = ProduitOpenHelper.getProduitByID(db, phPrepLigne.getProduitID());
                                        if(produit != null)
                                        {
                                            listeGTIN.add(produit.getGTIN());
                                            Depot depotOrigine = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());
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

                                                    if(produit.isSuivi_Serialisation() && !produit.isSerialiser_Reception_Delivrance())
                                                    {
                                                        if(!stockLotEmplacement.getSerie().contentEquals(""))
                                                        {
                                                            ph_preparationLigneAdapte.getLotAdaptes().add(ph_preparationLigneAdapte.new LotAdapte(stockLotEmplacement));
                                                        }
                                                    }
                                                    else
                                                    {
                                                        ph_preparationLigneAdapte.getLotAdaptes().add(ph_preparationLigneAdapte.new LotAdapte(stockLotEmplacement));
                                                    }

                                                    liste_lot.add(stockLotEmplacement.getLot());
                                                }
                                            }

                                            phPreparationLignePreparationAdapte_List.add(ph_preparationLigneAdapte);
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
                        Alerte.afficherAlerte(DetailPreparation2025Activity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Préparation PAD)", "alerte");
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
                    liste_lot = new ArrayList<>();
                    liste_lot = Objects.requireNonNull(data.getExtras()).getStringArrayList("liste_lot");

                    if(!utilisation_scan)
                    {
                        if(ph_preparation_ligne_preparationLotAdapter != null)
                            ph_preparationLigneAdapteRetourListeLot = ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.get(position_selectionne);
                    }

                    if(ph_preparationLigneAdapteRetourListeLot != null)
                    {
                        int quantiteAvant = 0;
                        for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte : ph_preparationLigneAdapteRetourListeLot.getLotAdaptes()) {
                            if (lotAdapte.getQteSaisie() > 0) {
                                quantiteAvant++;
                            }
                        }

                        ph_preparationLigneAdapteRetourListeLot.setLotAdaptes((List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>) data.getExtras().getSerializable("lotAdaptes"));

                        int quantiteApres = 0;
                        for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte : ph_preparationLigneAdapteRetourListeLot.getLotAdaptes()) {
                            if (lotAdapte.getQteSaisie() > 0) {
                                quantiteApres++;
                            }
                        }
                    }

                    if(ph_preparationLigneAdapteRetourListeLot != null)
                    {
                        enregistrementPreparationLigne(ph_preparationLigneAdapteRetourListeLot);
                        // phPreparationLignePreparationAdapte_List.get(position_selectionne).setLotAdaptes(ph_preparationLigneAdapteRetourListeLot.getLotAdaptes());
                    }

                    if(ph_preparation_ligne_preparationLotAdapter != null)
                    {
                        ph_preparation_ligne_preparationLotAdapter.notifyDataSetChanged();
                    }

                    break;
                case CodesEchangesActivites.RETOUR_CODE_GS1: {
                    if (resultCode == DetailPreparation2025Activity.RESULT_OK) {
                        String code = data.getStringExtra("code");
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                        List<Produit> produit_List = new ArrayList<>();

                        if (!gs1Decoupe.isEmpty()) {
                            String codeGtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
                            String numeroLot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                            String dateDePeremption = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
                            String numeroSerie = gs1Decoupe.get(OutilsDecodage.numeroSerie);

                            assert numeroSerie != null;
                            String last_char = numeroSerie.substring(numeroSerie.length() -1);
                            if(last_char.contentEquals("@"))
                            {
                                numeroSerie = numeroSerie.substring(0, numeroSerie.length()-1);
                            }

                            assert numeroLot != null;
                            last_char = numeroLot.substring(numeroLot.length()-1);
                            if(last_char.contentEquals("@"))
                            {
                                numeroLot = numeroLot.substring(0, numeroLot.length()-1);
                            }

                            String gtin = codeGtin;
                            assert gtin != null;
                            if(gtin.length() > 14)
                            {
                                gtin = gtin.substring(2);
                            }
                            String peremption = "";
                            Date peremption_temp = null;
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("yyMMdd");
                            try {
                                assert dateDePeremption != null;
                                peremption_temp = input.parse(dateDePeremption);                 // parse input
                                assert peremption_temp != null;
                                peremption = output.format(peremption_temp);    // format output
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long uid = Serialisation.Serialisation_Verifier(utilisateurConnecte.getId(), false, false, gtin, "GTIN", numeroLot, peremption, numeroSerie, "Vérification", gtin, "", "");


                            produit_List = ProduitOpenHelper.getMedicamentsParGTIN(db, codeGtin);
                            if (produit_List.size() == 1) {
                                Produit produit = produit_List.get(0);
                                utilisation_scan = true;
                                for(int j = 0; j < phPreparationLignePreparationAdapte_List.size(); j++)
                                {
                                    int id = phPreparationLignePreparationAdapte_List.get(j).getPh_preparationLigneID();
                                    PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, id);
                                    String designation = ph_preparation_ligne.getProduitDesignation();

                                    if(designation.contentEquals(produit.getDesignation_ext()) || designation.contentEquals(produit.getDesignation_interne()))
                                    {
                                        ph_preparationLigneAdapteRetourListeLot = ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.get(j);
                                        break;
                                    }
                                }

                                PH_Preparation_Ligne ph_preparation_ligne_courant = null;
                                for (PH_Preparation_Ligne_Preparation_Adapte phPreparationLignePreparationAdapte : phPreparationLignePreparationAdapte_List) {
                                    PH_Preparation_Ligne phPreparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, phPreparationLignePreparationAdapte.getPh_preparationLigneID());
                                    if (phPreparationLigne.getProduitID() == produit_List.get(0).getID_produit()) {
                                        ph_preparationLigneAdapteSelectionne = phPreparationLignePreparationAdapte;
                                        ph_preparation_ligne_courant = phPreparationLigne;
                                    }
                                }
                                if (ph_preparationLigneAdapteSelectionne != null) {
                                    ph_preparationLigneAdapteSelectionne.getLotAdaptes().removeAll(ph_preparationLigneAdapteSelectionne.getLotAdaptes());
                                    //on regarde si le lot existe déjà
                                    List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> list = new ArrayList<>(ph_preparationLigneAdapteSelectionne.getLotAdaptes());
                                    if(!list.contains(numeroLot))
                                    {
                                        int qte = produit.getCond_achat();
                                        if(ph_preparation_ligne_courant != null)
                                        {
                                            if(qte > ph_preparation_ligne_courant.getQte_APreparer())
                                                qte = ph_preparation_ligne_courant.getQte_APreparer();
                                        }
                                        Stock_Lot_Emplacement_Light newstock = new Stock_Lot_Emplacement_Light(produit.getCond_achat(), numeroLot, dateDePeremption, produit.getEmplacement_PUI_Defaut(), produit.getZone_PUI_Defaut(), ph_preparation_Selectionne.getDepotOrigineReference(), produit.getID_produit(), qte, numeroSerie);
                                        PH_Preparation_Ligne_Preparation_Adapte.LotAdapte new_lot = ph_preparationLigneAdapteSelectionne.new LotAdapte(newstock);
                                        ph_preparationLigneAdapteSelectionne.getLotAdaptes().add(new_lot);
                                    }

                                    Intent detailPreparation_Intent = new Intent(DetailPreparation2025Activity.this, ListeLotPreparation2025Activity.class);
                                    Bundle detailPreparation_Bundle = DetailPreparation2025Activity.super.getBundle();
                                    detailPreparation_Bundle.putInt("produitID", produit_List.get(0).getID_produit());
                                    detailPreparation_Bundle.putSerializable("ph_preparationLigneAdapte", ph_preparationLigneAdapteSelectionne);
                                    detailPreparation_Bundle.putString("numeroLot", numeroLot);
                                    detailPreparation_Bundle.putString("dateDePeremption", dateDePeremption);
                                    detailPreparation_Bundle.putString("numSerie", numeroSerie);

                                    Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());

                                    detailPreparation_Bundle.putInt("depotID", depot.getDepot_UID());
                                    detailPreparation_Intent.putExtras(detailPreparation_Bundle);

                                    DetailPreparation2025Activity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
                                } else {
                                    Alerte.afficherAlerte(DetailPreparation2025Activity.this, "Attention", "Ce produit n'est pas présent dans la préparation", "alerte");
                                    onResume();
                                }
                            } else if (produit_List.size() > 1) {
                                Alerte.afficherAlerte(DetailPreparation2025Activity.this, "Attention", "Plusieurs médicaments correspondent à ce code", "alerte");
                                onResume();
                            } else {
                                Toast toast = Toast.makeText(DetailPreparation2025Activity.this, "Aucun médicament ne correspond", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(DetailPreparation2025Activity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                    break;
                }
                case CodesEchangesActivites.RETOUR_PREPARATION:{
                    liste_lot = new ArrayList<>();
                    liste_lot = data.getExtras().getStringArrayList("liste_lot");
                    phPreparationLignePreparationAdapte_List = new ArrayList<>();
                    phPreparationLignePreparationAdapte_List = (List<PH_Preparation_Ligne_Preparation_Adapte>) data.getExtras().getSerializable("lotAdapteList");

                    for(PH_Preparation_Ligne_Preparation_Adapte courant : phPreparationLignePreparationAdapte_List)
                    {
                        enregistrementPreparationLigne(courant);
                    }

                    premierpassage = false;
                    onResume();
                    break;
                }
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
            for (PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte : ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes) {
                int nbLignesAvecValeur = 0;

                for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot : ph_preparationLigneAdapte.getLotAdaptes())
                {
                    if (lot.getQteSaisie() != 0) {
                        nbLignesAvecValeur++;
                    }
                }
                if (nbLignesAvecValeur == 0) {
                    nbLigneSansValeur ++;
                }
            }
            if(nbLigneSansValeur == ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.size())
            {
                Alerte.afficherAlerte(DetailPreparation2025Activity.this, "Attention", "Vous n'avez pas saisie de ligne", "alerte");
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
        phPreparationLignePreparationAdapte_List.sort((o1, o2) -> {

            PH_Preparation_Ligne oo1 = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, o1.getPh_preparationLigneID());
            PH_Preparation_Ligne oo2 = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, o2.getPh_preparationLigneID());

            return oo1.getProduitDesignation().compareTo(oo2.getProduitDesignation());
        });
        premierpassage = false;
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter2025(DetailPreparation2025Activity.this, db, utilisateurConnecte);

        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Preparation_Ligne_Preparation_Adapte courant : phPreparationLignePreparationAdapte_List)
        {
            PH_Preparation_Ligne ph_preparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, courant.getPh_preparationLigneID());
            Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
            String zone = produit.getZone_PUI_Defaut();
            String emplacement = produit.getEmplacement_PUI_Defaut();
            String zoneemplacement = zone + "-" + emplacement;

            if(!listeZoneEmplacement.contains(zoneemplacement)) {
                listeZoneEmplacement.add(zoneemplacement);
                ph_preparation_ligne_preparationLotAdapter.addSectionHeaderItem(courant);
            }

            ph_preparation_ligne_preparationLotAdapter.addItem(courant);
        }

        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
        phPreparationLigne_ListView.setDivider(null);
        phPreparationLigne_ListView.setSelection(position_selectionne);
    }

    private void onTriParPlace()
    {
        tri_choisi = "Place";
        phPreparationLignePreparationAdapte_List.sort((o1, o2) -> {

            PH_Preparation_Ligne oo1 = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, o1.getPh_preparationLigneID());
            PH_Preparation_Ligne oo2 = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, o2.getPh_preparationLigneID());
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
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter2025(DetailPreparation2025Activity.this, db, utilisateurConnecte);

        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Preparation_Ligne_Preparation_Adapte courant : phPreparationLignePreparationAdapte_List)
        {
            PH_Preparation_Ligne ph_preparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, courant.getPh_preparationLigneID());
            if(ph_preparationLigne != null)
            {
                Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
                String zone = produit.getZone_PUI_Defaut();
                String emplacement = produit.getEmplacement_PUI_Defaut();
                String zoneemplacement = zone + "-" + emplacement;

                if(!listeZoneEmplacement.contains(zoneemplacement)) {
                    listeZoneEmplacement.add(zoneemplacement);
                    ph_preparation_ligne_preparationLotAdapter.addSectionHeaderItem(courant);
                }

                ph_preparation_ligne_preparationLotAdapter.addItem(courant);
            }
        }

        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
        phPreparationLigne_ListView.setDivider(null);
        phPreparationLigne_ListView.setSelection(position_selectionne);
    }

    private void onTriParPoids()
    {
        tri_choisi = "Poids";
        phPreparationLignePreparationAdapte_List.sort((o1, o2) -> {

            PH_Preparation_Ligne oo1 = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, o1.getPh_preparationLigneID());
            PH_Preparation_Ligne oo2 = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, o2.getPh_preparationLigneID());

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
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter2025(DetailPreparation2025Activity.this, db, utilisateurConnecte);

        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Preparation_Ligne_Preparation_Adapte courant : phPreparationLignePreparationAdapte_List)
        {
            PH_Preparation_Ligne ph_preparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, courant.getPh_preparationLigneID());
            Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
            String zone = produit.getZone_PUI_Defaut();
            String emplacement = produit.getEmplacement_PUI_Defaut();
            String zoneemplacement = zone + "-" + emplacement;

            if(!listeZoneEmplacement.contains(zoneemplacement)) {
                listeZoneEmplacement.add(zoneemplacement);
                ph_preparation_ligne_preparationLotAdapter.addSectionHeaderItem(courant);
            }

            ph_preparation_ligne_preparationLotAdapter.addItem(courant);
        }

        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
        phPreparationLigne_ListView.setDivider(null);
        phPreparationLigne_ListView.setSelection(position_selectionne);
    }

    private void onMenuDatamatrixClick() {
        Intent detailPreparation_Intent = new Intent(DetailPreparation2025Activity.this, BarcodePreparation2025Activity.class);

        List<PH_Preparation_Ligne> liste_ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesAPreparerParPHPreparation(db, ph_preparation_Selectionne);

        if(liste_ph_preparation_ligne.isEmpty())
        {
            Alerte.afficherAlerte(DetailPreparation2025Activity.this, "Attention", "Toutes les références sont déjà préparées en intégralité", "alerte");
        }
        else
        {
            //gestion du zebra
            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
            {
                detailPreparation_Intent = new Intent(DetailPreparation2025Activity.this, ScannerPreparation2025Activity.class);
            }

            List<PH_Preparation_Ligne> preparationLignesBase = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, ph_preparation_Selectionne);

            Bundle detailPreparation_Bundle = DetailPreparation2025Activity.super.getBundle();
            detailPreparation_Bundle.putString("contexte", String.valueOf(R.string.scannerContextPreparationMultiple));
            detailPreparation_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) liste_lot);
            detailPreparation_Bundle.putString("ordreTri", tri_choisi);
            detailPreparation_Bundle.putSerializable("lotAdapteList", (Serializable) phPreparationLignePreparationAdapte_List);
            detailPreparation_Bundle.putSerializable("liste_ph_preparation_ligne", (Serializable) preparationLignesBase);
            detailPreparation_Bundle.putString("Designation", ph_preparation_Selectionne.getListe());
            detailPreparation_Bundle.putBoolean("isBoutonSuppressionExistant", true);
            detailPreparation_Bundle.putInt("UserId", utilisateurConnecte.getId());
            detailPreparation_Bundle.putInt("preparationId", ph_preparation_Selectionne.getUID());
            detailPreparation_Bundle.putBoolean("modeRafale", true);
            detailPreparation_Bundle.putStringArrayList("ListGTIN", (ArrayList<String>) listeGTIN);

            detailPreparation_Intent.putExtras(detailPreparation_Bundle);
            // Appel de la prochaine activité
            DetailPreparation2025Activity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_PREPARATION);
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
        textViewNBPalette = (TextView) view.findViewById(R.id.nbPaletteSelectionne);
        textViewNBCaisse = (TextView) view.findViewById(R.id.nbColisSelectionne);
        textViewNBContainer = (TextView) view.findViewById(R.id.nbContainerSelectionne);
        textViewNBScelle = (TextView) view.findViewById(R.id.nbScelleSelectionne);
        final ImageView imagePalette = (ImageView) view.findViewById(R.id.iconPalette);
        final ImageView imageContainer = (ImageView) view.findViewById(R.id.iconContainer);
        final ImageView imageScelle = (ImageView) view.findViewById(R.id.iconScelle);

        //on calcule le nombre de colis
        int nbColis = 0;
        for(PH_Preparation_Ligne_Preparation_Adapte courant : phPreparationLignePreparationAdapte_List)
        {
            PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, courant.getPh_preparationLigneID());
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
                InputMethodManager imm = (InputMethodManager) DetailPreparation2025Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
            };

            Alerte.afficherAlerteNumberPicker(DetailPreparation2025Activity.this, title, message, value, maxValue, onClickListener);
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

            Alerte.afficherAlerteNumberPicker(DetailPreparation2025Activity.this, title, message, value, maxValue, onClickListener);
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

            Alerte.afficherAlerteNumberPicker(DetailPreparation2025Activity.this, title, message, value, maxValue, onClickListener);
        });

        //gestion des scelles
        LinearScelle.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un NumberPicker
            textViewNBScelle.performClick();
        });

        textViewNBScelle.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un edittext
            String textscelle = Alerte.afficherAlerteEditText(DetailPreparation2025Activity.this, "Numéro de scellé", "Saisir un numéro de scellé");
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

        AlertePreparationAdapter adapter = new AlertePreparationAdapter(DetailPreparation2025Activity.this, produitNonRenseigne);
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
            if(produit_temp.isSuivi_Serialisation() && !produit_temp.isSerialiser_Reception_Delivrance())
            {
                Random randomserialisation = new Random();
                int serialisationId = randomserialisation.nextInt();
                if(serialisationId > 0)
                    serialisationId= serialisationId*-1;

                String[] datePeremptionTab = lignecourante.getPeremptionDate().split("-");
                String peremptionDate = lignecourante.getPeremptionDate();
                if(datePeremptionTab.length == 3)
                    peremptionDate = datePeremptionTab[0].substring(2)+datePeremptionTab[1]+datePeremptionTab[2];

                /*PH_Serialisation serialisation = new PH_Serialisation(serialisationId, utilisateurConnecte.getId(), "G110", "", produit_temp.getGTIN(), "GTIN", lignecourante.getLotNumero(), peremptionDate, lignecourante.getSerieNumero(), "DELIVRANCE", String.valueOf(lignecourante.getPreparationID()), produit_temp.getID_produit());
                serialisation.setStatut("En attente");
                serialisation.setRaison("");
                serialisation.setResultat("");
                PH_SerialisationOpenHelper.insererPH_SerialisationEnBDD(db, serialisation);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, serialisation.getPhiMR4UUID(), serialisation.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);*/
                PH_Serialisation serialisation = PH_SerialisationOpenHelper.getPH_SerialisationByMultiple(db, produit_temp.getGTIN(), "GTIN", lignecourante.getLotNumero(), peremptionDate, lignecourante.getSerieNumero());
                if(serialisation != null)
                {
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, serialisation.getPhiMR4UUID(), serialisation.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                    /**
                     * TODO : création requete G120 de sérialisation
                     */
                    int serialisationUID = (int) Serialisation.Serialisation_Creer(utilisateurConnecte.getId(), "G110", produit_temp.getGTIN(), "GTIN", lignecourante.getLotNumero(), peremptionDate, lignecourante.getSerieNumero(), "DELIVRANCE", String.valueOf(lignecourante.getPreparationID()));
                    serialisationDispenserSingle(DetailPreparation2025Activity.this, db, utilisateurConnecte, serialisationUID, produit_temp.getGTIN(), "GTIN", lignecourante.getLotNumero(), peremptionDate, lignecourante.getSerieNumero()).thenAccept(success -> {
                        if(!success)
                        {
                            Log.e("Erreur serialisation", "Erreur lors de la dispensiation de la serialisation");
                        }
                        PH_Serialisation serialisationDispenser = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, serialisationUID);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, serialisationDispenser.getPhiMR4UUID(), serialisationDispenser.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                    });
                }

                Random randomAUSeri = new Random();
                int actionSerId = randomAUSeri.nextInt();
                if(actionSerId > 0)
                    actionSerId= actionSerId*-1;
                ActionUtilisateur new_action_utilisateur_serialisation = new ActionUtilisateur(actionSerId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", serialisation.get_UID(), "", "Serialisation");
                ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur_serialisation);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur_serialisation.getPhiMR4UUID(), new_action_utilisateur_serialisation.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            }

            Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if(actionligneId > 0)
                actionligneId= actionligneId*-1;
            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", lignecourante.get_UID(), "", 0, (int)lignecourante.getQte_preparer(), lignecourante.getProduitDesignation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionUtilisateur_ligne.getPhiMR4UUID(), actionUtilisateur_ligne.getId(), DBOpenHelper.ActionsEAS.AJOUT);

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
            Toast.makeText(DetailPreparation2025Activity.this, "Préparation effectuée en partie", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DetailPreparation2025Activity.this, "Préparation effectuée", Toast.LENGTH_SHORT).show();
        }

        // Si possible, on essaie de mettre à jour les éléments
        if (statutConnexion) {
            ElementASynchroniserOpenHelper.toutSynchroniser(DetailPreparation2025Activity.this, db, utilisateurConnecte, true);
        }
        //NewDetailPreparationActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS);
        Intent retourListeIntent = new Intent(DetailPreparation2025Activity.this, ServicePreparationPufActivity.class);
        if(ph_preparation_Selectionne.getDepotDestinataireReference().contains("-PAD-"))
            retourListeIntent = new Intent(DetailPreparation2025Activity.this, ServicePreparationPadActivity.class);

        if(utilisateurConnecte.getEtablissement().toUpperCase().contentEquals("ADH"))
        {
            envoyerImpressionZebra(ph_preparation_Selectionne);
        }
        else
        {
            Bundle extras = new Bundle();
            extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            extras.putInt("serviceSelectionneID", serviceActuel.getId());
            retourListeIntent.putExtras(extras);
            DetailPreparation2025Activity.this.startActivity(retourListeIntent);
            DetailPreparation2025Activity.this.finish();
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
            detailPreparationIntent = new Intent(DetailPreparation2025Activity.this, ServicePreparationPufActivity.class);
        } else {
            detailPreparationIntent = new Intent(DetailPreparation2025Activity.this, ServicePreparationPadActivity.class);
        }
        Bundle detailPreparationBundle = super.getBundle();
        detailPreparationIntent.putExtras(detailPreparationBundle);
        DetailPreparation2025Activity.this.startActivity(detailPreparationIntent);
        DetailPreparation2025Activity.this.finish();
    }

    private void enregistrementPreparationLigne(PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte)
    {
        // Enregistrement des PH_Preparation_Ligne
        List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotsSaisis;

        PH_Preparation_Ligne ph_preparationLigneCorrespondant = null;

        ph_preparationLigneCorrespondant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigneAdapte.getPh_preparationLigneID());

        /* on supprime les lignes déjà enregistrer qui ne sont pas les lignes de bases */
        PH_Preparation ph_preparationCorrespondant = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparationLigneCorrespondant.getPreparationID());
        List<PH_Preparation_Ligne> listePHPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, ph_preparationCorrespondant,ph_preparationLigneCorrespondant.getProduitID());
        for(PH_Preparation_Ligne courante : listePHPreparationLigne)
        {
            //on supprime le PH_Preparation_Ligne
            PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, courante);
            //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, courante.getPhiMR4UUID(), courante.get_UID(), DBOpenHelper.ActionsEAS.SUPPR);
        }
        //ElementASynchroniserOpenHelper.toutSynchroniser(DetailPreparation2025Activity.this, db, utilisateurConnecte, true);

        lotsSaisis = new ArrayList<>();
        for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot : ph_preparationLigneAdapte.getLotAdaptes() ) {
            if (lot.getQteSaisie() != 0) {
                lotsSaisis.add(lot);
            }
        }

        int GlobalAPreparer = ph_preparationLigneCorrespondant.getQte_Demander();
        boolean origine = true;
        PH_Preparation_Ligne ph_preparationLigneCourant = null;

        for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot : lotsSaisis) {

            if(origine)
            {
                ph_preparationLigneCourant = new PH_Preparation_Ligne(ph_preparationLigneCorrespondant);
                Random random = new Random();
                int new_id = random.nextInt();
                if(new_id > 0)
                {
                    new_id= new_id*-1;
                }
                ph_preparationLigneCourant.set_UID(new_id);
                origine = false;
            }
            else
            {
                ph_preparationLigneCourant = new PH_Preparation_Ligne(ph_preparationLigneCourant);
                Random random = new Random();
                int new_id = random.nextInt();
                if(new_id > 0)
                {
                    new_id= new_id*-1;
                }
                ph_preparationLigneCourant.set_UID(new_id);
            }

            ph_preparationLigneCourant.setQte_Demander(GlobalAPreparer);
            GlobalAPreparer = GlobalAPreparer - lot.getQteSaisie();
            ph_preparationLigneCourant.setQte_RAL(GlobalAPreparer);
            ph_preparationLigneCourant.setQte_preparer(lot.getQteSaisie());
            ph_preparationLigneCourant.setLotNumero(lot.getNumLot().trim());
            ph_preparationLigneCourant.setPeremptionDate(lot.getDatePeremption());
            ph_preparationLigneCourant.setZoneDepot(lot.getZone().trim());
            ph_preparationLigneCourant.setEmplacementParDefaut(lot.getEmplacement().trim());
            ph_preparationLigneCourant.setSerieNumero(lot.getNumSerie().trim());
            ph_preparationLigneCourant.set_UID_4D(ph_preparationLigneCorrespondant.get_UID_4D());

            long rowID = PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparationLigneCourant);
            if (rowID != -1) {
                //compteurReussiteParLotAvecValeur++;
                //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparationLigneCourant.getPhiMR4UUID(), ph_preparationLigneCourant.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                //gestion des actions lignes
                //Random randomactionligne = new Random();
                //int actionligneId = randomactionligne.nextInt();
                //if(actionligneId > 0)
                //    actionligneId= actionligneId*-1;

                //ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", ph_preparationLigneCourant.get_UID(), "", 0, (int)ph_preparationLigneCourant.getQte_preparer(), ph_preparationLigneCourant.getProduitDesignation());
                //ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);

                //gestion de la mise à jour des PH_Stock_Lot_Emplacement
                Stock_Lot_Emplacement_Light stock_lot_emplacement = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lot.getStockLotEmplacementID());
                if(stock_lot_emplacement != null)
                {
                    stock_lot_emplacement.setQte(stock_lot_emplacement.getQte()-stock_lot_emplacement.getQte_Preparer());
                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_lot_emplacement);
                    //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getPhiMR4UUID(), stock_lot_emplacement.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                }
            }
        }
        //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparationLigneCorrespondant.getPhiMR4UUID(), ph_preparationLigneCorrespondant.get_UID(), DBOpenHelper.ActionsEAS.SUPPR);
        //PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ph_preparationLigneCorrespondant);
        //compteurReussiteGlobale++;
        //if (GlobalAPreparer > 0) {
        //    PH_Preparation_Ligne ph_preparation_reliquat = new PH_Preparation_Ligne(ph_preparationLigneCorrespondant);
        //    ph_preparation_reliquat.setQte_Demander(GlobalAPreparer);
        //    ph_preparation_reliquat.setQte_RAL(GlobalAPreparer);
        //    ph_preparation_reliquat.setQte_preparer(0);
        //    PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_reliquat);
        //}

        //ElementASynchroniserOpenHelper.toutSynchroniser(DetailPreparation2025Activity.this, db, utilisateurConnecte, false);
    }

    private void envoyerImpressionZebra(PH_Preparation ph_preparation) throws JSONException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(cal.getTime());

        JSONArray Etiquette_TO = new JSONArray();

        Depot depotOrigine = DepotOpenHelper.getDepotParID(db, ph_preparation.getDepotOrigineID());
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

        String imprimante_VT = "Zebra";
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
            Toast.makeText(DetailPreparation2025Activity.this, "Etiquette envoyée", Toast.LENGTH_SHORT).show();
            Intent retourListeIntent = new Intent(DetailPreparation2025Activity.this, ServicePreparationPufActivity.class);
            if(ph_preparation_Selectionne.getDepotDestinataireReference().contains("-PAD-"))
                retourListeIntent = new Intent(DetailPreparation2025Activity.this, ServicePreparationPadActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            extras.putInt("serviceSelectionneID", serviceActuel.getId());
            retourListeIntent.putExtras(extras);
            DetailPreparation2025Activity.this.startActivity(retourListeIntent);
            DetailPreparation2025Activity.this.finish();
        },
                error -> {
                    Log.e("Etiquette Volley", error.toString());
                    if(!error.toString().contains("\"isOk\":true"))
                    {
                        Alerte.afficherAlerte(DetailPreparation2025Activity.this, "Erreur HTTP", "Erreur lors de l\'impression de l\'étiquette : "+error.toString(), "alerte");
                    }
                    else
                    {
                        Toast.makeText(DetailPreparation2025Activity.this, "Etiquette envoyée", Toast.LENGTH_SHORT).show();
                    }
                    Intent retourListeIntent = new Intent(DetailPreparation2025Activity.this, ServicePreparationPufActivity.class);
                    if(ph_preparation_Selectionne.getDepotDestinataireReference().contains("-PAD-"))
                        retourListeIntent = new Intent(DetailPreparation2025Activity.this, ServicePreparationPadActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    extras.putInt("serviceSelectionneID", serviceActuel.getId());
                    retourListeIntent.putExtras(extras);
                    DetailPreparation2025Activity.this.startActivity(retourListeIntent);
                    DetailPreparation2025Activity.this.finish();
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
}
