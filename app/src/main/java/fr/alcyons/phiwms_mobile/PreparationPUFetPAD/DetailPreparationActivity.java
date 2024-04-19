package fr.alcyons.phiwms_mobile.PreparationPUFetPAD;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;

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

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
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
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.AlertePreparationAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Preparation_Ligne_PreparationLotAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.Chronometer;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

/**
 * Created by olivier on 27/06/2019.
 */

public class DetailPreparationActivity extends ServiceAvecConnexionActivity {
    public PH_Preparation ph_preparation_Selectionne;
    Serialisation serialisation;
    PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapteRetourListeLot;
    boolean utilisation_scan;
    List<String> listeGTIN;
    public List<PH_Preparation_Ligne_Preparation_Adapte> phPreparationLignePreparationAdapte_List;
    public ListView phPreparationLigne_ListView;
    PH_Preparation_Ligne_PreparationLotAdapter ph_preparation_ligne_preparationLotAdapter;
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

    public void enregistrerPhPreparation()
    {
        int nbTotalLotsAvecValeurSaisie = 0;

        int nbLigneSansValeur = 0;
        List<String> produitNonRenseigne = new ArrayList<>();

        // On vérifie que toutes les ph_preparations_lignes ont une quantité pour au moins un lot
        for (PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte : ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes) {
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

        if(nbLigneSansValeur > 0)
        {
            AfficherAlertePreparation(produitNonRenseigne, nbTotalLotsAvecValeurSaisie);
        }
        else
        {
            EnregistrerPreparation(nbTotalLotsAvecValeurSaisie);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_preparation_modifiable);

        //gestion du package manager
        pm = DetailPreparationActivity.this.getPackageManager();

        //gestion date début
        Chronometer.LancementChrono();
        //Gestion spinner
        //initi du tri
        tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriPreparation(db);
        if(tri_choisi == null)
        {
            ParametreUtilisateurOpenHelper.mettreAJourTriPreparation(db, 0, "Designation");
            tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriPreparation(db);
        }

        liste_lot = new ArrayList<>();
        context = DetailPreparationActivity.this;
        premierpassage = true;
        // Récupération des variables globales
        ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, Objects.requireNonNull(intent.getExtras()).getInt("ph_preparationUID_Selectionne"));
        genre_preparation = intent.getExtras().getString("genre");
        utilisation_scan = false;
        serialisation = new Serialisation(DetailPreparationActivity.this, db, utilisateurConnecte);
        listeGTIN = new ArrayList<>();
        // Affichage des informations de base
        Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotDestinataireReference());

        String intitule = "#" + String.valueOf(ph_preparation_Selectionne.getUID());
        ((TextView) findViewById(R.id.intitule)).setText(intitule);

        String textDepot = depot.getNom();
        if(utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depot.getStructure().contentEquals("PAD"))
        {
            textDepot = "Patient - "+depot.getPAD_IPP();
        }
        ((TextView) findViewById(R.id.depot)).setText(textDepot);

        ((LinearLayout) findViewById(R.id.lancerScan)).setOnClickListener(view -> onMenuDatamatrixClick());

        // Gestion de la listView
        phPreparationLigne_ListView = (ListView) findViewById(R.id.listeView);
        phPreparationLignePreparationAdapte_List = new ArrayList<>();

        phPreparationLignes = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);

        phPreparationLignePreparationAdapte_List = new ArrayList<>();

        nbLotPreparer = 0;
        phPreparationLignes.sort((o1, o2) -> Double.compare(o2.getProduitPoids(), o1.getProduitPoids()));

        phPreparationLigne_ListView.setOnItemClickListener((parent, view, position, id) -> {
            utilisation_scan = false;
            PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte = ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.get(position);
            PH_Preparation_Ligne ph_preparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigneAdapte.getPh_preparationLigneID());

            ph_preparationLigneViewHolder = ph_preparation_ligne_preparationLotAdapter.ph_preparation_ligneViewHolderList.get(position);
            Intent detailPreparation_Intent = new Intent(DetailPreparationActivity.this, ListeLotPreparationActivity.class);
            Bundle detailPreparation_Bundle = DetailPreparationActivity.super.getBundle();
            detailPreparation_Bundle.putInt("produitID", ph_preparationLigne.getProduitID());
            detailPreparation_Bundle.putSerializable("ph_preparationLigneAdapte", ph_preparationLigneAdapte);
            detailPreparation_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) liste_lot);

            Depot depot1 = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());

            detailPreparation_Bundle.putInt("depotID", depot1.getDepot_UID());
            detailPreparation_Intent.putExtras(detailPreparation_Bundle);

            DetailPreparationActivity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if(!ph_preparation_Selectionne.getListe().contentEquals("ALCYONS_LISTE"))
        {
            if (OutilsGestionConnexionReseau.isServerAccessible(DetailPreparationActivity.this) && passageParOnCreate)
            {

                if (!swipeRefreshLayout.isRefreshing()) {
                    mProgressDialog = ProgressDialog.show(DetailPreparationActivity.this, "Veuillez patienter", "Synchronisation des stocks en cours");
                }

                RequestQueue requestQueue = Volley.newRequestQueue(DetailPreparationActivity.this);
                String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePreparationDetail+ph_preparation_Selectionne.getUID();

                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int nbResultat = response.getInt("resultCount");
                                    if (nbResultat == 0) {
                                        String erreur = response.getString("erreur");
                                        if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                            Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
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
                                                    if (stock_lot_emplacement_light.getQte() > 0) {
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
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                handler.sendMessage(handler.obtainMessage());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(DetailPreparationActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Préparation PAD)", "alerte");
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
                requestQueue.add(obreq);
                try {
                    Looper.loop();
                } catch (RuntimeException e) {
                    e.printStackTrace();
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

                            stock_lot_emplacement_lightList.sort(Comparator.comparing(Stock_Lot_Emplacement_Light::getLot));
                            stock_lot_emplacement_lightList.sort(Comparator.comparing(Stock_Lot_Emplacement_Light::getPeremptionDate));

                            for (Stock_Lot_Emplacement_Light stockLotEmplacement : stock_lot_emplacement_lightList) {
                                if (stockLotEmplacement.getQte() > 0) {
                                    if(phPrepLigne.getQte_preparer() == 0)
                                    {
                                        stockLotEmplacement.setQte_Preparer(0);
                                    }
                                    else
                                    {
                                        if(stockLotEmplacement.getQte() < phPrepLigne.getQte_preparer())
                                        {
                                            stockLotEmplacement.setQte_Preparer((int)stockLotEmplacement.getQte());
                                        }
                                        else
                                        {
                                            stockLotEmplacement.setQte_Preparer((int)phPrepLigne.getQte_preparer());
                                        }
                                    }
                                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacement);
                                    ph_preparationLigneAdapte.getLotAdaptes().add(ph_preparationLigneAdapte.new LotAdapte(stockLotEmplacement));
                                }
                            }
                            phPreparationLignePreparationAdapte_List.add(ph_preparationLigneAdapte);
                        }
                    }
                }

                passageParOnCreate = false;
            }
        }

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
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
                        if(ph_preparationLigneViewHolder != null)
                        {
                            int position = ph_preparation_ligne_preparationLotAdapter.ph_preparation_ligneViewHolderList.indexOf(ph_preparationLigneViewHolder);
                            if(position != -1) {
                                ph_preparationLigneAdapteRetourListeLot = ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.get(position);
                            }
                        }
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

                    if(ph_preparation_ligne_preparationLotAdapter != null)
                    {
                        ph_preparation_ligne_preparationLotAdapter.notifyDataSetChanged();
                    }

                    break;
                case CodesEchangesActivites.RETOUR_CODE_GS1: {
                    if (resultCode == DetailPreparationActivity.RESULT_OK) {
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

                                    Intent detailPreparation_Intent = new Intent(DetailPreparationActivity.this, ListeLotPreparationActivity.class);
                                    Bundle detailPreparation_Bundle = DetailPreparationActivity.super.getBundle();
                                    detailPreparation_Bundle.putInt("produitID", produit_List.get(0).getID_produit());
                                    detailPreparation_Bundle.putSerializable("ph_preparationLigneAdapte", ph_preparationLigneAdapteSelectionne);
                                    detailPreparation_Bundle.putString("numeroLot", numeroLot);
                                    detailPreparation_Bundle.putString("dateDePeremption", dateDePeremption);
                                    detailPreparation_Bundle.putString("numSerie", numeroSerie);

                                    Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());

                                    detailPreparation_Bundle.putInt("depotID", depot.getDepot_UID());
                                    detailPreparation_Intent.putExtras(detailPreparation_Bundle);

                                    DetailPreparationActivity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
                                } else {
                                    Alerte.afficherAlerte(DetailPreparationActivity.this, "Attention", "Ce produit n'est pas présent dans la préparation", "alerte");
                                    onResume();
                                }
                            } else if (produit_List.size() > 1) {
                                Alerte.afficherAlerte(DetailPreparationActivity.this, "Attention", "Plusieurs médicaments correspondent à ce code", "alerte");
                                onResume();
                            } else {
                                Toast toast = Toast.makeText(DetailPreparationActivity.this, "Aucun médicament ne correspond", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(DetailPreparationActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                    break;
                }
                case CodesEchangesActivites.RETOUR_PREPARATION:{
                    liste_lot = new ArrayList<>();
                    liste_lot = Objects.requireNonNull(data.getExtras()).getStringArrayList("liste_lot");
                    phPreparationLignePreparationAdapte_List = new ArrayList<>();
                    phPreparationLignePreparationAdapte_List = (List<PH_Preparation_Ligne_Preparation_Adapte>) data.getExtras().getSerializable("lotAdapteList");
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
                Alerte.afficherAlerte(DetailPreparationActivity.this, "Attention", "Vous n'avez pas saisie de ligne", "alerte");
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
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter(DetailPreparationActivity.this, phPreparationLignePreparationAdapte_List, db);
        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
    }

    private void onTriParPlace()
    {
        tri_choisi = "Place";
        phPreparationLignePreparationAdapte_List.sort((o1, o2) -> {

            PH_Preparation_Ligne oo1 = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, o1.getPh_preparationLigneID());
            PH_Preparation_Ligne oo2 = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, o2.getPh_preparationLigneID());
            String oo1EmplacementParDefaut = oo1.getEmplacementParDefaut();
            String oo2EmplacementParDefaut = oo2.getEmplacementParDefaut();

            if (oo1EmplacementParDefaut.contentEquals("")) {
                Produit produit = ProduitOpenHelper.getProduitByID(db, oo1.getProduitID());
                Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());
                List<Stock_Lot_Emplacement_Light> stockLotEmplacementLights = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
                if (!stockLotEmplacementLights.isEmpty()) {
                    oo1EmplacementParDefaut = stockLotEmplacementLights.get(0).getEmplacement();
                }
            }
            if (oo2EmplacementParDefaut.contentEquals("")) {
                Produit produit = ProduitOpenHelper.getProduitByID(db, oo2.getProduitID());
                Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());
                List<Stock_Lot_Emplacement_Light> stockLotEmplacementLights = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
                if (!stockLotEmplacementLights.isEmpty()) {
                    oo2EmplacementParDefaut = stockLotEmplacementLights.get(0).getEmplacement();
                }
            }

            return oo1EmplacementParDefaut.compareTo(oo2EmplacementParDefaut);
        });
        premierpassage = false;
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter(DetailPreparationActivity.this, phPreparationLignePreparationAdapte_List, db);
        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
    }

    private void onTriParPoids()
    {
        tri_choisi = "Poids";
        phPreparationLignePreparationAdapte_List.sort((o1, o2) -> {

            PH_Preparation_Ligne oo1 = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, o1.getPh_preparationLigneID());
            PH_Preparation_Ligne oo2 = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, o2.getPh_preparationLigneID());

            return Double.compare(oo1.getPoidsTotal(), oo2.getPoidsTotal());
        });
        premierpassage = false;
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter(DetailPreparationActivity.this, phPreparationLignePreparationAdapte_List, db);
        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
    }

    private void onMenuDatamatrixClick() {
        Intent detailPreparation_Intent = new Intent(DetailPreparationActivity.this, BarcodePreparationActivity.class);

        List<PH_Preparation_Ligne> liste_ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesAPreparerParPHPreparation(db, ph_preparation_Selectionne);

        if(liste_ph_preparation_ligne.isEmpty())
        {
            Alerte.afficherAlerte(DetailPreparationActivity.this, "Attention", "Toutes les références sont déjà préparées en intégralité", "alerte");
        }
        else
        {
            //gestion du zebra
            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                detailPreparation_Intent = new Intent(DetailPreparationActivity.this, ScannerPreparationActivity.class);
            }

            // Récupération des éléments à transmettre à la prochaine activité
            Bundle detailPreparation_Bundle = DetailPreparationActivity.super.getBundle();
            detailPreparation_Bundle.putString("contexte", String.valueOf(R.string.scannerContextPreparationMultiple));
            detailPreparation_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) liste_lot);
            detailPreparation_Bundle.putString("ordreTri", tri_choisi);
            if(!premierpassage)
            {
                detailPreparation_Bundle.putInt("nb_produit_scanne", nb_produit_scanne);
            }
            else
            {
                //boolean plan_De_Cueillette = gestionnaireParametresServeur.getPlanDeCueilletteActif(db);
                boolean plan_De_Cueillette = true;
                if(plan_De_Cueillette)
                {
                    nbLotPreparer = 0;
                    List<PH_Preparation_Ligne_Preparation_Adapte>listemp = phPreparationLignePreparationAdapte_List;
                    phPreparationLignePreparationAdapte_List = new ArrayList<>();
                    for(PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte : listemp)
                    {
                        List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotAdaptes = null;

                        PH_Preparation_Ligne phPrepLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigneAdapte.getPh_preparationLigneID());
                        Produit produit = ProduitOpenHelper.getProduitByID(db, phPrepLigne.getProduitID());

                        listeGTIN.add(produit.getGTIN());
                        Depot depotOrigine = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());
                        List<Stock_Lot_Emplacement_Light> stock_lot_emplacement_lightList = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depotOrigine);

                        stock_lot_emplacement_lightList.sort(Comparator.comparing(Stock_Lot_Emplacement_Light::getLot));
                        stock_lot_emplacement_lightList.sort(Comparator.comparing(Stock_Lot_Emplacement_Light::getPeremptionDate));

                        double qteDemander = phPrepLigne.getQte_Demander();
                        double qtePreparer;
                        boolean lotAssigne = false;
                        List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> nouvelleListe = new ArrayList<>();
                        ph_preparationLigneAdapte.setLotAdaptes(nouvelleListe);
                        if(!phPrepLigne.isSuivi_Par_Serie())
                        {

                            for (Stock_Lot_Emplacement_Light stockLotEmplacement : stock_lot_emplacement_lightList) {
                                if (stockLotEmplacement.getQte() > 0) {
                                    if (!lotAssigne) {
                                        if (qteDemander > 0) {
                                            nbLotPreparer++;
                                        }
                                        if (stockLotEmplacement.getQte() < qteDemander) {
                                            qteDemander = qteDemander - stockLotEmplacement.getQte();
                                            qtePreparer = stockLotEmplacement.getQte();
                                        } else {
                                            lotAssigne = true;
                                            qtePreparer = qteDemander;
                                        }
                                        stockLotEmplacement.setQte_Preparer((int) qtePreparer);
                                    }
                                    ph_preparationLigneAdapte.getLotAdaptes().add(ph_preparationLigneAdapte.new LotAdapte(stockLotEmplacement));
                                }
                            }
                        }
                        phPreparationLignePreparationAdapte_List.add(ph_preparationLigneAdapte);
                    }

                    detailPreparation_Bundle.putSerializable("listedejascanne", (Serializable) phPreparationLignePreparationAdapte_List);
                }
            }
            detailPreparation_Bundle.putSerializable("lotAdapteList", (Serializable) phPreparationLignePreparationAdapte_List);
            detailPreparation_Bundle.putString("Designation", ph_preparation_Selectionne.getListe());
            detailPreparation_Bundle.putBoolean("isBoutonSuppressionExistant", true);
            detailPreparation_Bundle.putInt("UserId", utilisateurConnecte.getId());
            detailPreparation_Bundle.putInt("preparationId", ph_preparation_Selectionne.getUID());
            detailPreparation_Bundle.putBoolean("modeRafale", true);
            detailPreparation_Bundle.putStringArrayList("ListGTIN", (ArrayList<String>) listeGTIN);

            detailPreparation_Intent.putExtras(detailPreparation_Bundle);
            // Appel de la prochaine activité
            DetailPreparationActivity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_PREPARATION);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.isEmpty()) {
            afficherAlerteConfirmationRetour(DetailPreparationActivity.this, LayoutInflater.from(DetailPreparationActivity.this), super.getBundle());
        } else {
            retourService(super.getBundle());
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
                InputMethodManager imm = (InputMethodManager) DetailPreparationActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
            };

            Alerte.afficherAlerteNumberPicker(DetailPreparationActivity.this, title, message, value, maxValue, onClickListener);
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

            Alerte.afficherAlerteNumberPicker(DetailPreparationActivity.this, title, message, value, maxValue, onClickListener);
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

            Alerte.afficherAlerteNumberPicker(DetailPreparationActivity.this, title, message, value, maxValue, onClickListener);
        });

        //gestion des scelles
        LinearScelle.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un NumberPicker
            textViewNBScelle.performClick();
        });

        textViewNBScelle.setOnClickListener(v -> {
            // Ouvre une boite de dialogue avec un edittext
            String textscelle = Alerte.afficherAlerteEditText(DetailPreparationActivity.this, "Numéro de scellé", "Saisir un numéro de scellé");
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
            enregistrerPhPreparation();
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

        AlertePreparationAdapter adapter = new AlertePreparationAdapter(DetailPreparationActivity.this, produitNonRenseigne);
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
            EnregistrerPreparation(nbTotalLotsAvecValeurSaisie);
            alertDialog.dismiss();
        });
    }

    public void EnregistrerPreparation(int nbTotalLotsAvecValeurSaisie)
    {
        List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotsSaisis;
        PH_Preparation_Ligne ph_preparationLigneCorrespondant = null;
        List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> listeLot = new ArrayList<>();
        int compteurReussiteGlobale = 0;
        int compteurReussiteParLotAvecValeur = 0;
        //Création de l'action utilisateur
        Random randomaction = new Random();
        int actionId = randomaction.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateDestruction =new Date();
        String date_string = parseFormat.format(dateDestruction);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", ph_preparation_Selectionne.getUID(), "", "Preparation");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
        //fin de la création de l'action utilisateur

        // Enregistrement des PH_Preparation_Ligne
        for (PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte : ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes) {

            ph_preparationLigneCorrespondant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigneAdapte.getPh_preparationLigneID());

            if(ph_preparationLigneCorrespondant != null)
            {
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
                        compteurReussiteParLotAvecValeur++;
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparationLigneCourant.getPhiMR4UUID(), ph_preparationLigneCourant.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                        //gestion des actions lignes
                        Random randomactionligne = new Random();
                        int actionligneId = randomactionligne.nextInt();
                        if(actionligneId > 0)
                            actionligneId= actionligneId*-1;

                        ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", ph_preparationLigneCourant.get_UID(), "", 0, (int)ph_preparationLigneCourant.getQte_preparer(), ph_preparationLigneCourant.getProduitDesignation());
                        ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);

                        //gestion de la mise à jour des PH_Stock_Lot_Emplacement
                        Stock_Lot_Emplacement_Light stock_lot_emplacement = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lot.getStockLotEmplacementID());
                        if(stock_lot_emplacement != null)
                        {
                            stock_lot_emplacement.setQte(stock_lot_emplacement.getQte()-stock_lot_emplacement.getQte_Preparer());
                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_lot_emplacement);
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getPhiMR4UUID(), stock_lot_emplacement.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                        }
                    }
                }
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparationLigneCorrespondant.getPhiMR4UUID(), ph_preparationLigneCorrespondant.get_UID(), DBOpenHelper.ActionsEAS.SUPPR);
                PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ph_preparationLigneCorrespondant);
                compteurReussiteGlobale++;
                if (GlobalAPreparer > 0) {
                    PH_Preparation_Ligne ph_preparation_reliquat = new PH_Preparation_Ligne(ph_preparationLigneCorrespondant);
                    ph_preparation_reliquat.setQte_Demander(GlobalAPreparer);
                    ph_preparation_reliquat.setQte_RAL(GlobalAPreparer);
                    ph_preparation_reliquat.setQte_preparer(0);
                    PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_reliquat);
                }
            }
        }

        // Si tout est ok alors on met à jour le PH_Preparation
        if (compteurReussiteParLotAvecValeur == nbTotalLotsAvecValeurSaisie) {

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
            if (rowID != -1) {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation_Selectionne.getPhiMR4UUID(), ph_preparation_Selectionne.getUID(), DBOpenHelper.ActionsEAS.MAJ);
            } else {
                // Si le retour n'est pas mis à jour, on remet le compteur à 0
                compteurReussiteGlobale = 0;
            }
        } else {
            compteurReussiteGlobale = 0;
        }

        if (compteurReussiteGlobale != ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.size()) {
            // Si une erreur est survenue, on annule les modifications en vidant la table ElementASynchroniser
            Alerte.afficherAlerte(DetailPreparationActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
            ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);

            DetailPreparationActivity.this.finish();
            return;
        }


        //véfication de la totalité de la préparation
        List<PH_Preparation_Ligne> listePhPreparationRALListe;
        listePhPreparationRALListe = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesRAL(db, ph_preparation_Selectionne);
        if (!listePhPreparationRALListe.isEmpty()) {
            Toast.makeText(DetailPreparationActivity.this, "Préparation effectuée en partie", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DetailPreparationActivity.this, "Préparation effectuée", Toast.LENGTH_SHORT).show();
        }

        // Si possible, on essaie de mettre à jour les éléments
        if (OutilsGestionConnexionReseau.isServerAccessible(DetailPreparationActivity.this)) {
            ElementASynchroniserOpenHelper.toutSynchroniser(DetailPreparationActivity.this, db, utilisateurConnecte, true);
        }
        //NewDetailPreparationActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS);
        Intent retourListeIntent = new Intent(DetailPreparationActivity.this, ServicePreparationPufActivity.class);
        if(ph_preparation_Selectionne.getDepotDestinataireReference().contains("-PAD-"))
            retourListeIntent = new Intent(DetailPreparationActivity.this, ServicePreparationPadActivity.class);

        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        extras.putInt("serviceSelectionneID", serviceActuel.getId());
        retourListeIntent.putExtras(extras);
        DetailPreparationActivity.this.startActivity(retourListeIntent);
        DetailPreparationActivity.this.finish();
        return;
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
        if(genre_preparation.contentEquals("PUF"))
        {
            detailPreparationIntent = new Intent(DetailPreparationActivity.this, ServicePreparationPufActivity.class);
        }
        else
        {
            detailPreparationIntent = new Intent(DetailPreparationActivity.this, ServicePreparationPadActivity.class);
        }
        Bundle detailPreparationBundle = super.getBundle();
        detailPreparationIntent.putExtras(detailPreparationBundle);
        DetailPreparationActivity.this.startActivity(detailPreparationIntent);
        DetailPreparationActivity.this.finish();
    }
}
