package fr.alcyons.phimr4.ControleDesRetoursScannee;

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
import fr.alcyons.phimr4.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Retour;
import fr.alcyons.phimr4.Classes.Retour_Ligne;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.RetourAdapter;
import fr.alcyons.phimr4.Navigation.NavigationActivity;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by olivier on 14/06/2019.
 */

public class ServiceControleRetoursScanneeActivity extends ServiceAvecConnexionActivity {

    Context context;

    List<Retour> retourList;
    ListView retourListView;
    RetourAdapter retourAdapter;
    PackageManager pm;
    String tri_choisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_retours_new);
        retourList = new ArrayList<>();
        context = ServiceControleRetoursScanneeActivity.this;
        pm = ServiceControleRetoursScanneeActivity.this.getPackageManager();

        // Affichage des informations de base
        ((TextView) findViewById(R.id.titre)).setText("Demandes de retour");

        // Initialisation de la liste et de l'action sur l'un de ses items
        retourListView = (ListView) findViewById(R.id.listeView);
        retourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Retour retourSelectionne = (Retour) retourAdapter.getItem(position);

                Bundle serviceControleRetours_Bundle = ServiceControleRetoursScanneeActivity.super.getBundle();
                serviceControleRetours_Bundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursScanneeActivity.this, DetailControleRetoursScanneeActivity.class);
                serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                ServiceControleRetoursScanneeActivity.this.startActivity(serviceControleRetours_Intent);
                ServiceControleRetoursScanneeActivity.this.finish();
            }
        });

        //gestion du spinner de tri
        tri_choisi= ParametreUtilisateurOpenHelper.getChoixTriRetour(db);
        String triRetour = ParametreUtilisateurOpenHelper.getChoixTriRetour(db);
        if(triRetour == null)
        {
            ParametreUtilisateurOpenHelper.mettreAJourTriRetour(db, 0, "Numéro de retour");
            tri_choisi= ParametreUtilisateurOpenHelper.getChoixTriRetour(db);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceControleRetoursScanneeActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServiceControleRetoursScanneeActivity.this, "Veuillez patienter", "Synchronisation des retours en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ServiceControleRetoursScanneeActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteControleRetours;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {

                        // Takes the response from the JSON request
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
                                        ServiceControleRetoursScanneeActivity.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                        ServiceControleRetoursScanneeActivity.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                        Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Contrôle des retours", "alerte");
                                        ServiceControleRetoursScanneeActivity.this.finishAffinity();
                                    }
                                } else {
                                    viderTablesConcernees();
                                    JSONArray retoursJSONArray = response.getJSONArray("PH_Retours");
                                    long rowID = 0;
                                    for (int i = 0; i < retoursJSONArray.length(); i++) {
                                        JSONObject retourJSONObject = retoursJSONArray.getJSONObject(i);
                                        Retour retour = new Retour(retourJSONObject);

                                        if (retour.getEn_Attente_de().equals(getString(R.string.RepriseDemandee))) {
                                            Retour check_retour_existe = gestionnaireRetour.getRetourByID(db, retour.get_UID());
                                            if(check_retour_existe != null)
                                            {
                                                gestionnaireRetour.supprimerUnRetour(db, check_retour_existe);
                                            }
                                            rowID = gestionnaireRetour.insererUnRetourEnBDD(db, retour);
                                            if (rowID != -1) {
                                                retourList.add(retour);

                                                List<Retour_Ligne> list_temp = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour);

                                                JSONArray retourLignesJSONArray= retourJSONObject.getJSONArray("ph_retour_ligne");
                                                for (int k = 0; k < retourLignesJSONArray.length(); k++) {
                                                    JSONObject retourLigneJSONObject = retourLignesJSONArray.getJSONObject(k);
                                                    rowID = gestionnaireRetour_Ligne.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLigneJSONObject));
                                                    if (rowID != -1) {

                                                    }
                                                }

                                                list_temp = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour);
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
                            Alerte.afficherAlerte(ServiceControleRetoursScanneeActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Contrôle des retours)", "alerte");
                        }
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
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            passageParOnCreate = false;
            //lancerScan();
            int size_liste = retourList.size();
            String titre = "Demandes de retour";
            if(size_liste < 2)
                titre = "Demande de retour";
            ((TextView) findViewById(R.id.titre)).setText(titre);
            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
            retourAdapter = new RetourAdapter(ServiceControleRetoursScanneeActivity.this, db, retourList);
            // Permet d'enlever le séparateur entre deux éléments d'une listeView
            retourListView.setDivider(footer);
            retourListView.setAdapter(retourAdapter);

            if (retourList.size() == 0) {
                Bundle serviceControleRetours_Bundle = new Bundle();
                serviceControleRetours_Bundle.putBoolean("vide", true);
                serviceControleRetours_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                serviceControleRetours_Bundle.putString("nomservice", "Contrôle des retours");

                Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursScanneeActivity.this, NavigationActivity.class);
                serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                ServiceControleRetoursScanneeActivity.this.startActivity(intent);
                ServiceControleRetoursScanneeActivity.this.finish();
            }
        } else {
            retourList = gestionnaireRetour.getRetoursByEnAttenteDe(db, getString(R.string.RepriseDemandee));
            if (retourList.size() == 0) {
                connexionNecessaire();
                return;
            }
        }

        //gestion du tri de la liste
        switch (tri_choisi)
        {
            case "Numéro de retour":
                onClickTriNumero();
                break;

            case "Date de retour":
                onClickTriDate();
                break;

            case "Dépôt Origine":
                onClickTriDepot();
                break;
        }

        invalidateOptionsMenu();
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, retourAdapter, null, "Produit, Intitulé, N°...");
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

    // Permet de supprimer en base de donnée les données afin d'éviter les duplicats
    public void viderTablesConcernees() {
        for (Retour retour : RetourOpenHelper.getRetoursByEnAttenteDe(db, getString(R.string.RepriseDemandee))) {
            for (Retour_Ligne retourLigne : Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour)) {
                Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigne);
                Produit produit = ProduitOpenHelper.getProduitByID(db, retourLigne.getCode_produit());
                Depot depot = DepotOpenHelper.getDepotParReference(db, retour.getRef_Depot_Origine());

                if(produit != null && depot != null)
                {
                    for (Stock_Lot_Emplacement_Light stockLotEmplacementLight : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)) {
                        Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stockLotEmplacementLight);
                    }
                }
            }
            RetourOpenHelper.supprimerUnRetour(db, retour);
        }
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
        Bundle scanDocumentBundle = ServiceControleRetoursScanneeActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent = null;
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("huawei"))
        {
            scanDocumentIntent = new Intent(ServiceControleRetoursScanneeActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                scanDocumentIntent = new Intent(ServiceControleRetoursScanneeActivity.this, BarcodeCaptureActivity.class);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceControleRetoursScanneeActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }

        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceControleRetoursScanneeActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_DOCUMENT: {
                if (data != null) {
                    String code = data.getExtras().getString("code");
                    if (code != null) {
                        Retour retourSelectionne = RetourOpenHelper.getRetourByNumero(db, code);
                        if(retourSelectionne == null)
                        {
                            if(!code.contentEquals(""))
                            {
                                afficherSnackBarControleDesRetoursScanne();
                            }
                            /* Code nécessaire à l'affichage de la liste */
                            int size_liste = retourList.size();
                            String titre = "Demandes de retour";
                            if(size_liste < 2)
                                titre = "Demande de retour";

                            ((TextView) findViewById(R.id.titre)).setText(titre);
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                            retourAdapter = new RetourAdapter(ServiceControleRetoursScanneeActivity.this, db, retourList);
                            // Permet d'enlever le séparateur entre deux éléments d'une listeView
                            retourListView.setDivider(footer);
                            retourListView.setAdapter(retourAdapter);

                            if (retourList.size() == 0) {
                                Bundle serviceControleRetours_Bundle = new Bundle();
                                serviceControleRetours_Bundle.putBoolean("vide", true);
                                serviceControleRetours_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                serviceControleRetours_Bundle.putString("nomservice", "Contrôle des retours");

                                Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursScanneeActivity.this, NavigationActivity.class);
                                serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                                ServiceControleRetoursScanneeActivity.this.startActivity(intent);
                                ServiceControleRetoursScanneeActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                        else
                        {
                            Bundle serviceControleRetours_Bundle = ServiceControleRetoursScanneeActivity.super.getBundle();
                            serviceControleRetours_Bundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                            Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursScanneeActivity.this, DetailControleRetoursScanneeActivity.class);
                            serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                            ServiceControleRetoursScanneeActivity.this.startActivity(serviceControleRetours_Intent);
                        }
                    } else {
                        /* Code nécessaire à l'affichage de la liste */
                        int size_liste = retourList.size();
                        String titre = "Demandes de retour";
                        if(size_liste < 2)
                            titre = "Demande de retour";

                        ((TextView) findViewById(R.id.titre)).setText(titre);
                        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                        retourAdapter = new RetourAdapter(ServiceControleRetoursScanneeActivity.this, db, retourList);
                        // Permet d'enlever le séparateur entre deux éléments d'une listeView
                        retourListView.setDivider(footer);
                        retourListView.setAdapter(retourAdapter);

                        if (retourList.size() == 0) {
                            Bundle serviceControleRetours_Bundle = new Bundle();
                            serviceControleRetours_Bundle.putBoolean("vide", true);
                            serviceControleRetours_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                            serviceControleRetours_Bundle.putString("nomservice", "Contrôle des retours");

                            Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursScanneeActivity.this, NavigationActivity.class);
                            serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                            ServiceControleRetoursScanneeActivity.this.startActivity(intent);
                            ServiceControleRetoursScanneeActivity.this.finish();
                        }

                        invalidateOptionsMenu();
                    }
                }
                else
                {
                    /* Code nécessaire à l'affichage de la liste */
                    int size_liste = retourList.size();
                    String titre = "Demandes de retour";
                    if(size_liste < 2)
                        titre = "Demande de retour";

                    ((TextView) findViewById(R.id.titre)).setText(titre);
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                    retourAdapter = new RetourAdapter(ServiceControleRetoursScanneeActivity.this, db, retourList);
                    // Permet d'enlever le séparateur entre deux éléments d'une listeView
                    retourListView.setDivider(footer);
                    retourListView.setAdapter(retourAdapter);

                    if (retourList.size() == 0) {
                        Bundle serviceControleRetours_Bundle = new Bundle();
                        serviceControleRetours_Bundle.putBoolean("vide", true);
                        serviceControleRetours_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        serviceControleRetours_Bundle.putString("nomservice", "Contrôle des retours");

                        Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursScanneeActivity.this, NavigationActivity.class);
                        serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                        ServiceControleRetoursScanneeActivity.this.startActivity(intent);
                        ServiceControleRetoursScanneeActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                }
                break;
            }
        }
    }

    public void onClickTriNumero()
    {
        tri_choisi = "Numéro de retour";
        Collections.sort(retourAdapter.retourList, new Comparator<Retour>() {
            @Override
            public int compare(Retour o1, Retour o2) {
                return o1.getNumero().compareTo(o2.getNumero());
            }
        });
        retourAdapter.notifyDataSetChanged();
    }

    public void onClickTriDate()
    {
        tri_choisi = "Date de retour";
        Collections.sort(retourAdapter.retourList, new Comparator<Retour>() {
            @Override
            public int compare(Retour o1, Retour o2) {
                return o2.getDate_retour().compareTo(o1.getDate_retour());
            }
        });

        retourAdapter.notifyDataSetChanged();
    }

    public void onClickTriDepot()
    {
        tri_choisi = "Dépôt Origine";
        Collections.sort(retourAdapter.retourList, new Comparator<Retour>() {
            @Override
            public int compare(Retour o1, Retour o2) {
                return o1.getRef_Depot_Origine().compareTo(o2.getRef_Depot_Origine());
            }
        });

        retourAdapter.notifyDataSetChanged();
    }

    public void afficherSnackBarControleDesRetoursScanne() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);;

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
