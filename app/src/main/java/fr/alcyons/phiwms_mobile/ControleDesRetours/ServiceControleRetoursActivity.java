package fr.alcyons.phiwms_mobile.ControleDesRetours;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceControleRetoursActivity extends ServiceAvecConnexionActivity {

    Context context;

    List<Retour> retourList;
    ListView retourListView;
    RetourAdapter retourAdapter;
    PackageManager pm;
    Spinner optionTri;
    String tri_choisi;

    boolean connexionDirecte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_retours_new);
        retourList = new ArrayList<>();
        context = ServiceControleRetoursActivity.this;
        pm = ServiceControleRetoursActivity.this.getPackageManager();
        optionTri = (Spinner) findViewById(R.id.optionTri);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.titre)).setText("Demandes de retour");

        // Initialisation de la liste et de l'action sur l'un de ses items
        retourListView = (ListView) findViewById(R.id.listeView);
        retourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Retour retourSelectionne = (Retour) retourAdapter.getItem(position);

                Bundle serviceControleRetours_Bundle = ServiceControleRetoursActivity.super.getBundle();
                serviceControleRetours_Bundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, DetailsControleRetoursActivity.class);
                serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                ServiceControleRetoursActivity.this.startActivity(serviceControleRetours_Intent);
                ServiceControleRetoursActivity.this.finish();
            }
        });

        //gestion du spinner de tri
        tri_choisi= "Numéro de retour";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.option_tri_retour, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        optionTri.setAdapter(adapter);
        if (tri_choisi != null) {
            int spinnerPosition = adapter.getPosition(tri_choisi);
            optionTri.setSelection(spinnerPosition);
        }
        optionTri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                ((TextView) arg0.getChildAt(0)).setVisibility(View.INVISIBLE);
                String optionSelect = arg0.getItemAtPosition(position).toString();
                switch (optionSelect)
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceControleRetoursActivity.this) && passageParOnCreate && !connexionDirecte) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServiceControleRetoursActivity.this, "Veuillez patienter", "Synchronisation des retours en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ServiceControleRetoursActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteControleRetours;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
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
                                        ServiceControleRetoursActivity.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                        ServiceControleRetoursActivity.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                        Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Contrôle des retours", "alerte");
                                        ServiceControleRetoursActivity.this.finishAffinity();
                                    }
                                } else {
                                    JSONArray retoursJSONArray = response.getJSONArray("PH_Retours");
                                    viderTablesConcernees();
                                    long rowID = 0;
                                    for (int i = 0; i < retoursJSONArray.length(); i++) {
                                        JSONObject retourJSONObject = retoursJSONArray.getJSONObject(i);
                                        Retour retour = new Retour(retourJSONObject);

                                        if (retour.getEn_Attente_de().equals(getString(R.string.RepriseDemandee))) {
                                            rowID = gestionnaireRetour.insererUnRetourEnBDD(db, retour);
                                            if (rowID != -1) {
                                                retourList.add(retour);

                                                JSONArray retourLignesJSONArray= retourJSONObject.getJSONArray("ph_retour_ligne");
                                                for (int k = 0; k < retourLignesJSONArray.length(); k++) {
                                                    JSONObject retourLigneJSONObject = retourLignesJSONArray.getJSONObject(k);
                                                    rowID = gestionnaireRetour_Ligne.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLignesJSONArray.getJSONObject(k)));
                                                    if (rowID != -1) {
                                                    }
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
                            Log.e("Volley CdR", error.toString());
                            Alerte.afficherAlerte(ServiceControleRetoursActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Contrôle des retours)", "alerte");
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
            if(passageParOnCreate)
            {
                //on récupère la preparation du jeu d'essai si elle existe
                Retour retour_alcyons = RetourOpenHelper.getRetourEssaiAlcyons(db);
                if(retour_alcyons != null)
                {
                    retourList.add(retour_alcyons);
                }

                //lancerScan();
                /* Code nécessaire à l'affichage de la liste */
                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList);
                // Permet d'enlever le séparateur entre deux éléments d'une listeView
                retourListView.setDivider(footer);
                retourListView.setAdapter(retourAdapter);

                if (retourList.size() == 0) {
                    Bundle serviceControleRetours_Bundle = new Bundle();
                    serviceControleRetours_Bundle.putBoolean("vide", true);
                    serviceControleRetours_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    serviceControleRetours_Bundle.putString("nomservice", "Contrôle des retours");

                    Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, NavigationActivity.class);
                    serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                    ServiceControleRetoursActivity.this.startActivity(intent);
                    ServiceControleRetoursActivity.this.finish();
                }

                invalidateOptionsMenu();
            }
            passageParOnCreate = false;
        } else {
            retourList = gestionnaireRetour.getRetoursByEnAttenteDe(db, getString(R.string.RepriseDemandee));
            if (retourList.size() == 0) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceControleRetoursActivity.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Quarantaine");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
                    ServiceControleRetoursActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceControleRetoursActivity.this.finish();
                }
                else
                {
                    connexionNecessaire();
                    return;
                }
            }
            else
            {
                passageParOnCreate = false;
                if(connexionDirecte)
                {
                    /* Code nécessaire à l'affichage de la liste */
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                    retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList);
                    // Permet d'enlever le séparateur entre deux éléments d'une listeView
                    retourListView.setDivider(footer);
                    retourListView.setAdapter(retourAdapter);

                    if (retourList.size() == 0) {
                        Bundle serviceControleRetours_Bundle = new Bundle();
                        serviceControleRetours_Bundle.putBoolean("vide", true);
                        serviceControleRetours_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        serviceControleRetours_Bundle.putString("nomservice", "Contrôle des retours");

                        Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, NavigationActivity.class);
                        serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                        ServiceControleRetoursActivity.this.startActivity(intent);
                        ServiceControleRetoursActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                    connexionDirecte = !connexionDirecte;
                }
            }
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
        for (Retour retour : gestionnaireRetour.getRetoursByEnAttenteDe(db, getString(R.string.RepriseDemandee))) {
            if(!retour.getIntitulé().contentEquals("Retour_ALCYONS"))
            {
                for (Retour_Ligne retourLigne : gestionnaireRetour_Ligne.getAllRetourLignesByRetour(db, retour)) {
                    gestionnaireRetour_Ligne.supprimerUnRetourLigne(db, retourLigne);
                    Produit produit = gestionnaireProduit.getProduitByID(db, retourLigne.getCode_produit());
                    Depot depot = gestionnaireDepot.getDepotParReference(db, retour.getRef_Depot_Origine());

                    if(produit != null && depot != null)
                    {
                        for (Stock_Lot_Emplacement_Light stockLotEmplacementLight : gestionnaireStock_Lot_Emplacement.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)) {
                            gestionnaireStock_Lot_Emplacement.supprimerUnStockLotEmplacement(db, stockLotEmplacementLight);
                        }
                    }
                }
                gestionnaireRetour.supprimerUnRetour(db, retour);
            }
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
        Bundle scanDocumentBundle = ServiceControleRetoursActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        scanDocumentBundle.putBoolean("modeRafale", false);

        Intent scanDocumentIntent = null;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ServiceControleRetoursActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                scanDocumentIntent = new Intent(ServiceControleRetoursActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceControleRetoursActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }


        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceControleRetoursActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
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
                            afficherSnackBarControleDesRetours();
                            /* Code nécessaire à l'affichage de la liste */
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                            retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList);
                            // Permet d'enlever le séparateur entre deux éléments d'une listeView
                            retourListView.setDivider(footer);
                            retourListView.setAdapter(retourAdapter);

                            if (retourList.size() == 0) {
                                Bundle serviceControleRetours_Bundle = new Bundle();
                                serviceControleRetours_Bundle.putBoolean("vide", true);
                                serviceControleRetours_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                serviceControleRetours_Bundle.putString("nomservice", "Contrôle des retours");

                                Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, NavigationActivity.class);
                                serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                                ServiceControleRetoursActivity.this.startActivity(intent);
                                ServiceControleRetoursActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                        else
                        {
                            Bundle serviceControleRetours_Bundle = ServiceControleRetoursActivity.super.getBundle();
                            serviceControleRetours_Bundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                            Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, DetailsControleRetoursActivity.class);
                            serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                            ServiceControleRetoursActivity.this.startActivity(serviceControleRetours_Intent);
                        }
                    } else {
                        /* Code nécessaire à l'affichage de la liste */
                        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                        retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList);
                        // Permet d'enlever le séparateur entre deux éléments d'une listeView
                        retourListView.setDivider(footer);
                        retourListView.setAdapter(retourAdapter);

                        if (retourList.size() == 0) {
                            Bundle serviceControleRetours_Bundle = new Bundle();
                            serviceControleRetours_Bundle.putBoolean("vide", true);
                            serviceControleRetours_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                            serviceControleRetours_Bundle.putString("nomservice", "Contrôle des retours");

                            Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, NavigationActivity.class);
                            serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                            ServiceControleRetoursActivity.this.startActivity(intent);
                            ServiceControleRetoursActivity.this.finish();
                        }

                        invalidateOptionsMenu();
                    }
                }
                else
                {
                     /* Code nécessaire à l'affichage de la liste */
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                    retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList);
                    // Permet d'enlever le séparateur entre deux éléments d'une listeView
                    retourListView.setDivider(footer);
                    retourListView.setAdapter(retourAdapter);

                    if (retourList.size() == 0) {
                        Bundle serviceControleRetours_Bundle = new Bundle();
                        serviceControleRetours_Bundle.putBoolean("vide", true);
                        serviceControleRetours_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        serviceControleRetours_Bundle.putString("nomservice", "Contrôle des retours");

                        Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, NavigationActivity.class);
                        serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                        ServiceControleRetoursActivity.this.startActivity(intent);
                        ServiceControleRetoursActivity.this.finish();
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
        Collections.sort(retourList, new Comparator<Retour>() {
            @Override
            public int compare(Retour o1, Retour o2) {
                return o1.getNumero().compareTo(o2.getNumero());
            }
        });

        retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList);
        retourListView.setDivider(footer);
        retourListView.setAdapter(retourAdapter);
    }

    public void onClickTriDate()
    {
        tri_choisi = "Date de retour";
        Collections.sort(retourList, new Comparator<Retour>() {
            @Override
            public int compare(Retour o1, Retour o2) {
                return o2.getDate_retour().compareTo(o1.getDate_retour());
            }
        });

        retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList);
        retourListView.setDivider(footer);
        retourListView.setAdapter(retourAdapter);
    }

    public void onClickTriDepot()
    {
        tri_choisi = "Dépôt Origine";
        Collections.sort(retourList, new Comparator<Retour>() {
            @Override
            public int compare(Retour o1, Retour o2) {
                return o1.getRef_Depot_Origine().compareTo(o2.getRef_Depot_Origine());
            }
        });

        retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList);
        retourListView.setDivider(footer);
        retourListView.setAdapter(retourAdapter);
    }

    public void afficherSnackBarControleDesRetours() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);;

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
