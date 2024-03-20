package fr.alcyons.phimr4.PreparationPUFetPADScannee;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.material.snackbar.Snackbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.PH_Preparation;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.PH_Preparation_PreparationAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by olivier on 29/04/2019.
 */

public class ServicePreparationPadScanneeActivity extends ServiceAvecConnexionActivity {
    Context context;

    List<PH_Preparation> ph_preparation_List;
    ListView ph_preparation_ListView;
    PH_Preparation_PreparationAdapter ph_preparation_preparationAdapter;
    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_preparation);
        pm = ServicePreparationPadScanneeActivity.this.getPackageManager();
        context = ServicePreparationPadScanneeActivity.this;

        // Initialisation du titre
        ((TextView) findViewById(R.id.titre)).setText("Préparations PAD en attente");

        // Gestion de la listView
        ph_preparation_ListView = (ListView) findViewById(R.id.listeView);

    }

    @Override
    public void onResume() {
        super.onResume();
        ph_preparation_List = new ArrayList<>();
        ph_preparation_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PH_Preparation ph_preparation_Selectionne = (PH_Preparation) ph_preparation_preparationAdapter.getItem(position);

                Intent servicePreparationPad_Intent = new Intent(ServicePreparationPadScanneeActivity.this, DetailPreparationScanneeActivity.class);
                Bundle servicePreparationPad_Bundle = ServicePreparationPadScanneeActivity.super.getBundle();
                servicePreparationPad_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
                servicePreparationPad_Bundle.putInt("serviceSelectionneID", serviceActuel.getId());
                servicePreparationPad_Bundle.putString("genre", "PAD");
                servicePreparationPad_Intent.putExtras(servicePreparationPad_Bundle);
                ServicePreparationPadScanneeActivity.this.startActivity(servicePreparationPad_Intent);
            }
        });

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ServicePreparationPadScanneeActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServicePreparationPadScanneeActivity.this, "Veuillez patienter", "Synchronisation des Préparations PAD en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ServicePreparationPadScanneeActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePreparationPAD;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int nbResultat = response.getInt("resultCount");
                                if (nbResultat == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ServicePreparationPadScanneeActivity.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                        ServicePreparationPadScanneeActivity.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                        Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Préparation PAD", "alerte");
                                        ServicePreparationPadScanneeActivity.this.finishAffinity();
                                    }
                                } else {
                                    JSONArray ph_preparations_JSONArray = response.getJSONArray("PH_Preparations");
                                    viderTablesConcernees();

                                    long rowID = 0;
                                    for (int i = 0; i < ph_preparations_JSONArray.length(); i++) {
                                        JSONObject ph_preparation_JSONObject = ph_preparations_JSONArray.getJSONObject(i);
                                        PH_Preparation ph_preparation = new PH_Preparation(ph_preparation_JSONObject);

                                        rowID = gestionnairePH_Preparation.insererUnPH_PreparationEnBDD(db, ph_preparation);
                                        if (rowID != -1) {
                                            ph_preparation_List.add(ph_preparation);

                                            JSONArray ph_preparationLigne_JSONArray = ph_preparation_JSONObject.getJSONArray("ph_preparation_lignes");
                                            for (int k = 0; k < ph_preparationLigne_JSONArray.length(); k++) {
                                                JSONObject ph_preparationLigne_JSONObject = ph_preparationLigne_JSONArray.getJSONObject(k);
                                                rowID = gestionnairePH_Preparation_Ligne.insererUnPH_Preparation_LigneEnBDD(db, new PH_Preparation_Ligne(ph_preparationLigne_JSONArray.getJSONObject(k)));
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
                            Alerte.afficherAlerte(ServicePreparationPadScanneeActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Préparation PAD)", "alerte");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
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
            passageParOnCreate = false;
            lancerScan();
        } else {
            ph_preparation_List = gestionnairePH_Preparation.getAllPHPreparationPreparationPAD(db);
            if (ph_preparation_List.size() == 0) {
                connexionNecessaire();
                return;
            }
        }
        invalidateOptionsMenu();
    }

    public void viderTablesConcernees() {
        for (PH_Preparation ph_preparation : gestionnairePH_Preparation.getAllPHPreparationPreparationPAD(db))
        {
            if(!ph_preparation.getListe().contentEquals("ALCYONS_LISTE"))
            {
                for (PH_Preparation_Ligne ph_preparation_ligne : gestionnairePH_Preparation_Ligne.getAllPHPreparationLignesParPHPreparation(db, ph_preparation)
                        ) {
                    gestionnairePH_Preparation_Ligne.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
                    Produit produit = gestionnaireProduit.getProduitByID(db, ph_preparation_ligne.getProduitID());
                    Depot depot = gestionnaireDepot.getDepotParReference(db, ph_preparation.getDepotOrigineReference());

                    if (depot != null && produit != null) {
                        for (Stock_Lot_Emplacement_Light stockLotEmplacement : gestionnaireStock_Lot_Emplacement.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)
                                ) {
                            gestionnaireStock_Lot_Emplacement.supprimerUnStockLotEmplacement(db, stockLotEmplacement);
                        }
                    }
                }
                gestionnairePH_Preparation.supprimerUnPhPreparation(db, ph_preparation);
            }
        }
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, ph_preparation_preparationAdapter, null, "Rechercher...");
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

    public void lancerScan()
    {
        Bundle scanDocumentBundle = ServicePreparationPadScanneeActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent scanDocumentIntent = null;
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ServicePreparationPadScanneeActivity.this, ScannerSearchOnlyActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putBoolean("activerTextSuppression", true);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                scanDocumentIntent = new Intent(ServicePreparationPadScanneeActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServicePreparationPadScanneeActivity.this, ScannerSearchOnlyActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putBoolean("activerTextSuppression", true);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
                scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
            }
        }


        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServicePreparationPadScanneeActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_DOCUMENT: {
                if (data != null) {
                    String code = data.getExtras().getString("code");
                    if (code != null) {
                        int idPreparation = 0;
                        try {
                            idPreparation = Integer.parseInt(code);
                        } catch (NumberFormatException e) {
                            idPreparation = 0;
                        }
                        PH_Preparation ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, idPreparation);
                        if(ph_preparation_Selectionne == null)
                        {
                            if(!code.contentEquals(""))
                            {
                                afficherSnackBarPreparationPADScannee();
                            }
                            /* Code nécessaire à l'affichage de la liste */
                            //on récupère la preparation du jeu d'essai si elle existe
                            PH_Preparation preparation_alcyons = PH_PreparationOpenHelper.getPreparationEssaiAlcyons(db);
                            if(preparation_alcyons != null)
                            {
                                ph_preparation_List.add(preparation_alcyons);
                            }
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(ph_preparation_List.size()));
                            Collections.sort(ph_preparation_List, new Comparator<PH_Preparation>() {
                                @Override
                                public int compare(PH_Preparation o1, PH_Preparation o2) {
                                    return o1.getLivraisonPrevueDate().compareTo(o2.getLivraisonPrevueDate());
                                }
                            });
                            ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(ServicePreparationPadScanneeActivity.this, ph_preparation_List, db, utilisateurConnecte);
                            // Permet d'enlever le séparateur entre deux éléments d'une listeView
                            ph_preparation_ListView.setDivider(footer);
                            ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);

                            if (ph_preparation_List.size() == 0) {
                                vide = true;
                                nomServiceVide = "Préparation PAD";
                                ServicePreparationPadScanneeActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                        else
                        {
                            Intent servicePreparationPad_Intent = new Intent(ServicePreparationPadScanneeActivity.this, DetailPreparationScanneeActivity.class);
                            Bundle servicePreparationPad_Bundle = ServicePreparationPadScanneeActivity.super.getBundle();
                            servicePreparationPad_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
                            servicePreparationPad_Bundle.putInt("serviceSelectionneID", serviceActuel.getId());
                            servicePreparationPad_Bundle.putString("genre", "PAD");
                            servicePreparationPad_Intent.putExtras(servicePreparationPad_Bundle);
                            ServicePreparationPadScanneeActivity.this.startActivity(servicePreparationPad_Intent);
                            ServicePreparationPadScanneeActivity.this.finish();
                        }
                    } else {
                        /* Code nécessaire à l'affichage de la liste */
                        //on récupère la preparation du jeu d'essai si elle existe
                        PH_Preparation preparation_alcyons = PH_PreparationOpenHelper.getPreparationEssaiAlcyons(db);
                        if(preparation_alcyons != null)
                        {
                            ph_preparation_List.add(preparation_alcyons);
                        }
                        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(ph_preparation_List.size()));
                        Collections.sort(ph_preparation_List, new Comparator<PH_Preparation>() {
                            @Override
                            public int compare(PH_Preparation o1, PH_Preparation o2) {
                                return o1.getLivraisonPrevueDate().compareTo(o2.getLivraisonPrevueDate());
                            }
                        });
                        ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(ServicePreparationPadScanneeActivity.this, ph_preparation_List, db, utilisateurConnecte);
                        // Permet d'enlever le séparateur entre deux éléments d'une listeView
                        ph_preparation_ListView.setDivider(footer);
                        ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);

                        if (ph_preparation_List.size() == 0) {
                            vide = true;
                            nomServiceVide = "Préparation PAD";
                            ServicePreparationPadScanneeActivity.this.finish();
                        }

                        invalidateOptionsMenu();
                    }
                }
                break;
            }
        }
    }

    public void afficherSnackBarPreparationPADScannee() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);;

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}

