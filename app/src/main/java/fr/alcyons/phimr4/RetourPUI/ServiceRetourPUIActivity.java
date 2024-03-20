package fr.alcyons.phimr4.RetourPUI;

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

import androidx.annotation.RequiresApi;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phimr4.Classes.Retour;
import fr.alcyons.phimr4.Classes.Retour_Ligne;
import fr.alcyons.phimr4.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phimr4.ListViewAdapters.RetourAdapter;
import fr.alcyons.phimr4.Navigation.NavigationActivity;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

public class ServiceRetourPUIActivity extends ServiceAvecConnexionActivity {

    List<Retour> retourList;
    ListView retourListView;
    RetourAdapter adapter;
    PackageManager pm;
    JSONArray retourJSONArray;
    NavigationActivity navigationActivity;

    Context context;

    boolean connexionDirecte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_retours);

        // Modification du titre
        ((TextView) findViewById(R.id.titre)).setText("Retours PUI demandés");
        pm = ServiceRetourPUIActivity.this.getPackageManager();
        // Gestion de la listView
        retourListView = (ListView) findViewById(R.id.listeView);
        retourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Retour retourSelectionne = (Retour) adapter.getItem(position);

                Intent serviceRetourPuiIntent = new Intent(ServiceRetourPUIActivity.this, DetailRetourPUIActivity.class);
                Bundle serviceRetourPuiBundle = ServiceRetourPUIActivity.super.getBundle();
                serviceRetourPuiBundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                serviceRetourPuiIntent.putExtras(serviceRetourPuiBundle);
                ServiceRetourPUIActivity.this.startActivity(serviceRetourPuiIntent);
                ServiceRetourPUIActivity.this.finish();
            }
        });
        navigationActivity = new NavigationActivity();
        context = navigationActivity;

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);
    }

    @Override
    public void onResume() {
        super.onResume();

        retourList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceRetourPUIActivity.this) && passageParOnCreate && !connexionDirecte) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServiceRetourPUIActivity.this, "Veuillez patienter", "Synchronisation des retours en cours");
            }

            RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ServiceRetourPUIActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteRetourPUI;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int resultCount = response.getInt("resultCount");
                                if (resultCount == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(ServiceRetourPUIActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ServiceRetourPUIActivity.this.finishAffinity();
                                        Intent serviceNotificationsIntent = new Intent(ServiceRetourPUIActivity.this, AuthentificationActivity.class);
                                        ServiceRetourPUIActivity.this.startActivity(serviceNotificationsIntent);
                                    } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(ServiceRetourPUIActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", "alerte");
                                        ServiceRetourPUIActivity.this.finishAffinity();
                                        Intent serviceNotificationsIntent = new Intent(ServiceRetourPUIActivity.this, AuthentificationActivity.class);
                                        ServiceRetourPUIActivity.this.startActivity(serviceNotificationsIntent);
                                    } else if (!erreur.equals(getString(R.string.aucunRetour))) {
                                        Alerte.afficherAlerte(ServiceRetourPUIActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete service retour pui", "alerte");
                                    }
                                } else {
                                    viderTablesConcernees();

                                    retourJSONArray = response.getJSONArray("PH_Retours");

                                    for (int i = 0; i < retourJSONArray.length(); i++) {
                                        JSONObject retourJSONObject = retourJSONArray.getJSONObject(i);
                                        Retour retour = new Retour(retourJSONObject);

                                        if (retour.getEn_Attente_de().equals(getString(R.string.RetourPUIDemande)) && retour.getStatut().equals(getString(R.string.statutEncours))) {
                                            retourList.add(retour);
                                            long rowID = gestionnaireRetour.insererUnRetourEnBDD(db, retour);
                                            if (rowID != -1) {
                                                JSONArray retourLigneJSONArray = retourJSONObject.getJSONArray("ph_retour_ligne");
                                                for (int k = 0; k < retourLigneJSONArray.length(); k++) {
                                                    JSONObject retourLigneJSONObject = retourLigneJSONArray.getJSONObject(k);
                                                    Retour_Ligne retourLigne = new Retour_Ligne(retourLigneJSONObject);
                                                    gestionnaireRetour_Ligne.insererUnRetour_LigneEnBDD(db, retourLigne);
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
                            Alerte.afficherAlerte(ServiceRetourPUIActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP service retour pui", "alerte");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    return headers;
                }
            };
            obreq.setRetryPolicy(retryPolicy);
            requestQueueDestructionUtilisateur.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            if (retourList.size() == 0) {

                vide = true;
                nomServiceVide = "Retour PUI";
                ServiceRetourPUIActivity.this.finish();
            }
            else
            {
                if(passageParOnCreate)
                {
                    /* Code nécessaire à l'affichage de la liste */
                    int size_liste = retourList.size();
                    String titre = "Retours PUI demandés";
                    if(size_liste < 2)
                        titre = "Retour PUI demandé";

                    ((TextView) findViewById(R.id.titre)).setText(titre);
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                    adapter = new RetourAdapter(ServiceRetourPUIActivity.this, db, retourList);
                    retourListView.setDivider(footer);
                    retourListView.setAdapter(adapter);

                    invalidateOptionsMenu();
                }

                passageParOnCreate = false;
            }
        } else {
            retourList = gestionnaireRetour.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.RetourPUIDemande));
            if (retourList.size() == 0) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceRetourPUIActivity.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Retour PUI");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
                    ServiceRetourPUIActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceRetourPUIActivity.this.finish();
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
                    int size_liste = retourList.size();
                    String titre = "Retours PUI demandés";
                    if(size_liste < 2)
                        titre = "Retour PUI demandé";

                    ((TextView) findViewById(R.id.titre)).setText(titre);
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                    adapter = new RetourAdapter(ServiceRetourPUIActivity.this, db, retourList);
                    retourListView.setDivider(footer);
                    retourListView.setAdapter(adapter);

                    if (retourList.size() == 0) {

                        vide = true;
                        nomServiceVide = "Retour PUI";
                        ServiceRetourPUIActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                    connexionDirecte = !connexionDirecte;
                }
            }
        }
        invalidateOptionsMenu();
    }

    public void viderTablesConcernees() {
        for (Retour retour : gestionnaireRetour.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.RetourPUIDemande))
                ) {
            List<Retour_Ligne> retourLigneList = gestionnaireRetour_Ligne.getAllRetourLignesByRetour(db, retour);
            for (Retour_Ligne retourLigne : retourLigneList
                    ) {
                gestionnaireRetour_Ligne.supprimerUnRetourLigne(db, retourLigne);
            }
            gestionnaireRetour.supprimerUnRetour(db, retour);
        }
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Produit, Intitulé, N°...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
        menu.findItem(R.id.menuDatamatrix).setVisible(false);
        return true;
    }

    public void lancerScan()
    {
        Bundle scanDocumentBundle = ServiceRetourPUIActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent = null;
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies"))
        {
            scanDocumentIntent = new Intent(ServiceRetourPUIActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                scanDocumentIntent = new Intent(ServiceRetourPUIActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceRetourPUIActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceRetourPUIActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
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
                                afficherSnackBarRetourPUI();
                            }
                            /* Code nécessaire à l'affichage de la liste */
                            int size_liste = retourList.size();
                            String titre = "Retours PUI demandés";
                            if(size_liste < 2)
                                titre = "Retour PUI demandé";

                            ((TextView) findViewById(R.id.titre)).setText(titre);
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                            adapter = new RetourAdapter(ServiceRetourPUIActivity.this, db, retourList);
                            retourListView.setDivider(footer);
                            retourListView.setAdapter(adapter);

                            if (retourList.size() == 0) {

                                vide = true;
                                nomServiceVide = "Retour PUI";
                                ServiceRetourPUIActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                        else
                        {
                            Intent serviceRetourPuiIntent = new Intent(ServiceRetourPUIActivity.this, DetailRetourPUIActivity.class);
                            Bundle serviceRetourPuiBundle = ServiceRetourPUIActivity.super.getBundle();
                            serviceRetourPuiBundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                            serviceRetourPuiIntent.putExtras(serviceRetourPuiBundle);
                            ServiceRetourPUIActivity.this.startActivity(serviceRetourPuiIntent);
                            ServiceRetourPUIActivity.this.finish();
                        }
                    } else {
                        /* Code nécessaire à l'affichage de la liste */
                        int size_liste = retourList.size();
                        String titre = "Retours PUI demandés";
                        if(size_liste < 2)
                            titre = "Retour PUI demandé";

                        ((TextView) findViewById(R.id.titre)).setText(titre);
                        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                        adapter = new RetourAdapter(ServiceRetourPUIActivity.this, db, retourList);
                        retourListView.setDivider(footer);
                        retourListView.setAdapter(adapter);

                        if (retourList.size() == 0) {

                            vide = true;
                            nomServiceVide = "Retour PUI";
                            ServiceRetourPUIActivity.this.finish();
                        }

                        invalidateOptionsMenu();
                    }
                }
                else
                {
                    /* Code nécessaire à l'affichage de la liste */
                    int size_liste = retourList.size();
                    String titre = "Retours PUI demandés";
                    if(size_liste < 2)
                        titre = "Retour PUI demandé";

                    ((TextView) findViewById(R.id.titre)).setText(titre);
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                    adapter = new RetourAdapter(ServiceRetourPUIActivity.this, db, retourList);
                    retourListView.setDivider(footer);
                    retourListView.setAdapter(adapter);

                    if (retourList.size() == 0) {

                        vide = true;
                        nomServiceVide = "Retour PUI";
                        ServiceRetourPUIActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                }
                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void afficherSnackBarRetourPUI() {
        Snackbar snackbar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);
        }
        ;

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
