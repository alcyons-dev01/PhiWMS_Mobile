package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.snackbar.Snackbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_PreparationAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
import fr.alcyons.phiwms_mobile.VerrouPharmacie.DetailVerrouPharmacieActivity;

public class ServiceVerrouPharmacieActivity extends ServiceAvecConnexionActivity {

    Context context;

    List<PH_Preparation> phPreparationList;
    ListView phPreparationListView;
    PH_PreparationAdapter phPreparationAdapter;
    PackageManager pm;
    JSONArray phPreparationJSONArray;

    long rowID = 0;

    boolean connexionDirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);

        context = ServiceVerrouPharmacieActivity.this;
        pm = ServiceVerrouPharmacieActivity.this.getPackageManager();
        // Gestion de la liste
        phPreparationListView = (ListView) findViewById(R.id.listeView);
        phPreparationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PH_Preparation phPreparationSelectionne = (PH_Preparation) phPreparationAdapter.getItem(position);

                if (phPreparationSelectionne != null) {
                    Bundle serviceVerrouPharmacieBundle = ServiceVerrouPharmacieActivity.super.getBundle();
                    serviceVerrouPharmacieBundle.putInt("phPreparationSelectionneID", phPreparationSelectionne.getUID());

                    Intent serviceVerrouPharmacieIntent = new Intent(ServiceVerrouPharmacieActivity.this, DetailVerrouPharmacieActivity.class);
                    serviceVerrouPharmacieIntent.putExtras(serviceVerrouPharmacieBundle);
                    ServiceVerrouPharmacieActivity.this.startActivity(serviceVerrouPharmacieIntent);
                    ServiceVerrouPharmacieActivity.this.finish();
                }
            }
        });

        connexionDirect = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceVerrouPharmacieActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceVerrouPharmacieActivity.this.startActivity(intent);
                ServiceVerrouPharmacieActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        phPreparationList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate && !connexionDirect) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceVerrouPharmacieActivity.this, LayoutInflater.from(ServiceVerrouPharmacieActivity.this));;
            }
            CommandeOpenHelper.insererBDDLocaleCommandeReceptionPAD(ServiceVerrouPharmacieActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);

            RequestQueue requestQueueVerrouPharmacieUtilisateur = Volley.newRequestQueue(ServiceVerrouPharmacieActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteVerrouPharmacie;

            JsonObjectRequest obreq = getObjectRequest(urlRequete);
            requestQueueVerrouPharmacieUtilisateur.add(obreq);
        } else {
            phPreparationList = PH_PreparationOpenHelper.getAllPHPreparationVerrouPharmacie(db);
            if (phPreparationList.isEmpty()) {
                if(connexionDirect)
                {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceVerrouPharmacieActivity.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Verrou Pharmacie");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
                    ServiceVerrouPharmacieActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceVerrouPharmacieActivity.this.finish();
                }
                else
                {
                    connexionNecessaire();
                }
            }
            else
            {
                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(phPreparationList.size()));
                phPreparationList.sort(Comparator.comparing(PH_Preparation::getLivraisonPrevueDate));

                phPreparationAdapter = new PH_PreparationAdapter(ServiceVerrouPharmacieActivity.this, db, phPreparationList, utilisateurConnecte);
                phPreparationListView.setDivider(footer);
                phPreparationListView.setAdapter(phPreparationAdapter);

                if (phPreparationList.isEmpty()) {
                    vide = true;
                    nomServiceVide = "Verrou Pharmacie";
                    ServiceVerrouPharmacieActivity.this.finish();
                }

                passageParOnCreate = false;
                if(connexionDirect)
                {
                    connexionDirect = false;
                }

                invalidateOptionsMenu();
            }
        }
    }

    @NonNull
    private JsonObjectRequest getObjectRequest(String urlRequete) {
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                response -> {
                    try {
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            String erreur = response.getString("erreur");
                            if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerte(context, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                //DBOpenHelper.viderBasesDeDonnees(db);
                                ServiceVerrouPharmacieActivity.this.finishAffinity();
                                Intent intent = new Intent(context, AuthentificationActivity.class);
                                context.startActivity(intent);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                ServiceVerrouPharmacieActivity.this.finishAffinity();
                                Intent intent = new Intent(context, AuthentificationActivity.class);
                                context.startActivity(intent);
                            } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Verrou Pharmacie", "alerte");
                                ServiceVerrouPharmacieActivity.this.finishAffinity();
                            }
                        } else {
                            /*PH_SerialisationOpenHelper.viderTablePH_Serialisation(db);
                            JSONArray SerialisationJSONArray = response.getJSONArray("Ph_Serialisation");
                            for (int s = 0; s < SerialisationJSONArray.length(); s++) {
                                JSONObject serialisationJSONObject = SerialisationJSONArray.getJSONObject(s);
                                PH_Serialisation serialisation = new PH_Serialisation(serialisationJSONObject);
                                PH_SerialisationOpenHelper.insererPH_SerialisationEnBDD(db, serialisation);
                            }*/

                            phPreparationJSONArray = response.getJSONArray("PH_Preparations");
                            viderTablesConcernees();
                            for (int i = 0; i < phPreparationJSONArray.length(); i++) {
                                JSONObject phPreparationJSONObject = phPreparationJSONArray.getJSONObject(i);
                                PH_Preparation phPreparation = new PH_Preparation(phPreparationJSONObject);

                                Depot depot = DepotOpenHelper.getDepotParID(db, phPreparation.getDepotDestinataireID());

                                if (depot != null) {

                                    if (depot.getStructure().contains("PAD")) {
                                        if (phPreparation.getStatut().equals(getString(R.string.statutVerrouillée))) {
                                            rowID = PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, phPreparation);
                                            if (rowID != -1) {
                                                phPreparationList.add(phPreparation);
                                                remplirTablesPHPreparationLigneEtStockLotEmplacement(phPreparationJSONObject);
                                            }
                                        }
                                    } else if (depot.getStructure().contains("PUF")) {
                                        if (!phPreparation.getListe().contains("nominative")) {
                                            if (!phPreparation.getValidee()) {
                                                rowID = PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, phPreparation);
                                                if (rowID != -1) {
                                                    phPreparationList.add(phPreparation);
                                                    remplirTablesPHPreparationLigneEtStockLotEmplacement(phPreparationJSONObject);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (phPreparationList.isEmpty()) {
                                vide = true;
                                nomServiceVide = "Verrou Pharmacie";
                                Intent intent = new Intent(ServiceVerrouPharmacieActivity.this, NavigationActivity.class);
                                Bundle extras = new Bundle();
                                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                intent.putExtras(extras);
                                ServiceVerrouPharmacieActivity.this.startActivity(intent);
                                ServiceVerrouPharmacieActivity.this.finish();
                            }
                            else
                            {
                                if(passageParOnCreate)
                                {
                                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(phPreparationList.size()));
                                    ((TextView) findViewById(R.id.titre)).setText("Verrous");
                                    phPreparationList.sort(Comparator.comparing(PH_Preparation::getLivraisonPrevueDate));

                                    phPreparationAdapter = new PH_PreparationAdapter(ServiceVerrouPharmacieActivity.this, db, phPreparationList, utilisateurConnecte);
                                    phPreparationListView.setDivider(footer);
                                    phPreparationListView.setAdapter(phPreparationAdapter);

                                    if (phPreparationList.isEmpty()) {
                                        vide = true;
                                        nomServiceVide = "Verrou Pharmacie";
                                        ServiceVerrouPharmacieActivity.this.finish();
                                    }
                                }

                                passageParOnCreate = false;
                            }
                            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP Service Verrou Pharmacie", "alerte");
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

    public void lancerScan()
    {
        Bundle scanDocumentBundle = ServiceVerrouPharmacieActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);


        Intent scanDocumentIntent = null;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ServiceVerrouPharmacieActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                scanDocumentIntent = new Intent(ServiceVerrouPharmacieActivity.this, BarcodeCaptureActivity.class);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceVerrouPharmacieActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }


        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceVerrouPharmacieActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }

    public void viderTablesConcernees() {

        for (PH_Preparation ph_preparation : PH_PreparationOpenHelper.getAllPHPreparationVerrouPharmacie(db))
        {
            if(!ph_preparation.getListe().contentEquals("ALCYONS_VERROU"))
            {
                for (PH_Preparation_Ligne ph_preparation_ligne : PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation))
                {
                    PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
                    Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne.getProduitID());
                    Depot depot = DepotOpenHelper.getDepotParID(db, ph_preparation.getDepotOrigineID());

                    if (depot != null && produit != null) {
                        for (Stock_Lot_Emplacement_Light stockLotEmplacement : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)
                        ) {
                            Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stockLotEmplacement);
                        }
                    }
                }
                PH_PreparationOpenHelper.supprimerUnPhPreparation(db, ph_preparation);
            }
        }
    }

    // Permet d'enregistrer les ph_stock_lot_emplacements contenu dans ph_preparation_lignes
    public void remplirTablesPHPreparationLigneEtStockLotEmplacement(JSONObject phPrep) {
        try {
            JSONArray phPreparationLignesJSONArray = phPrep.getJSONArray("ph_preparation_lignes");
            for (int i = 0; i < phPreparationLignesJSONArray.length(); i++) {
                JSONObject phPreparationLignesJSONObject = phPreparationLignesJSONArray.getJSONObject(i);
                PH_Preparation_Ligne phPreparationLigne = new PH_Preparation_Ligne(phPreparationLignesJSONObject);
                long rowID = PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, phPreparationLigne);
                JSONArray phStockLotEmplacementJSONArray = phPreparationLignesJSONObject.getJSONArray("ph_stock_lot_emplacements");
                for (int k = 0; k < phStockLotEmplacementJSONArray.length(); k++) {
                    JSONObject phStockLotEmplacementJSONObject = phStockLotEmplacementJSONArray.getJSONObject(k);
                    Stock_Lot_Emplacement_Light stockLotEmplacementLight = new Stock_Lot_Emplacement_Light(phStockLotEmplacementJSONObject);
                    Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stockLotEmplacementLight);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, phPreparationAdapter, null, "Nom dépot, N°...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                lancerScan();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_DOCUMENT: {
                if (data != null) {
                    String code = data.getExtras().getString("code");
                    if (code != null) {
                        PH_Preparation phPreparationSelectionne = PH_PreparationOpenHelper.getPH_PreparationByNumeroCommande(db, code);
                        if(phPreparationSelectionne == null)
                        {
                            if(!code.contentEquals(""))
                            {
                                afficherSnackBarVerrouPharmacie();
                            }
                            //on récupère la preparation du jeu d'essai si elle existe
                            PH_Preparation verrou_alcyons = PH_PreparationOpenHelper.getVerrouEssaiAlcyons(db);
                            if(verrou_alcyons != null)
                            {
                                phPreparationList.add(verrou_alcyons);
                            }
                            /* Code nécessaire à l'affichage de la liste */
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(phPreparationList.size()));
                            Collections.sort(phPreparationList, new Comparator<PH_Preparation>() {
                                @Override
                                public int compare(PH_Preparation o1, PH_Preparation o2) {
                                    return o1.getLivraisonPrevueDate().compareTo(o2.getLivraisonPrevueDate());
                                }
                            });

                            phPreparationAdapter = new PH_PreparationAdapter(ServiceVerrouPharmacieActivity.this, db, phPreparationList, utilisateurConnecte);
                            phPreparationListView.setDivider(footer);
                            phPreparationListView.setAdapter(phPreparationAdapter);

                            if (phPreparationList.size() == 0) {
                                vide = true;
                                nomServiceVide = "Verrou Pharmacie";
                                ServiceVerrouPharmacieActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                        else
                        {
                            Bundle serviceVerrouPharmacieBundle = ServiceVerrouPharmacieActivity.super.getBundle();
                            serviceVerrouPharmacieBundle.putInt("phPreparationSelectionneID", phPreparationSelectionne.getUID());

                            Intent serviceVerrouPharmacieIntent = new Intent(ServiceVerrouPharmacieActivity.this, DetailVerrouPharmacieActivity.class);
                            serviceVerrouPharmacieIntent.putExtras(serviceVerrouPharmacieBundle);
                            ServiceVerrouPharmacieActivity.this.startActivity(serviceVerrouPharmacieIntent);
                            ServiceVerrouPharmacieActivity.this.finish();
                        }
                    } else {
                        //on récupère la preparation du jeu d'essai si elle existe
                        PH_Preparation verrou_alcyons = PH_PreparationOpenHelper.getVerrouEssaiAlcyons(db);
                        if(verrou_alcyons != null)
                        {
                            phPreparationList.add(verrou_alcyons);
                        }
                        /* Code nécessaire à l'affichage de la liste */
                        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(phPreparationList.size()));
                        Collections.sort(phPreparationList, new Comparator<PH_Preparation>() {
                            @Override
                            public int compare(PH_Preparation o1, PH_Preparation o2) {
                                return o1.getLivraisonPrevueDate().compareTo(o2.getLivraisonPrevueDate());
                            }
                        });

                        phPreparationAdapter = new PH_PreparationAdapter(ServiceVerrouPharmacieActivity.this, db, phPreparationList, utilisateurConnecte);
                        phPreparationListView.setDivider(footer);
                        phPreparationListView.setAdapter(phPreparationAdapter);

                        if (phPreparationList.size() == 0) {
                            vide = true;
                            nomServiceVide = "Verrou Pharmacie";
                            ServiceVerrouPharmacieActivity.this.finish();
                        }

                        invalidateOptionsMenu();
                    }
                }
                else
                {
                    /* Code nécessaire à l'affichage de la liste */
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(phPreparationList.size()));
                    Collections.sort(phPreparationList, new Comparator<PH_Preparation>() {
                        @Override
                        public int compare(PH_Preparation o1, PH_Preparation o2) {
                            return o1.getLivraisonPrevueDate().compareTo(o2.getLivraisonPrevueDate());
                        }
                    });

                    phPreparationAdapter = new PH_PreparationAdapter(ServiceVerrouPharmacieActivity.this, db, phPreparationList, utilisateurConnecte);
                    phPreparationListView.setDivider(footer);
                    phPreparationListView.setAdapter(phPreparationAdapter);

                    if (phPreparationList.size() == 0) {
                        vide = true;
                        nomServiceVide = "Verrou Pharmacie";
                        ServiceVerrouPharmacieActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                }
                break;
            }
        }
    }

    public void afficherSnackBarVerrouPharmacie() {
        Snackbar snackbar;
        snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
