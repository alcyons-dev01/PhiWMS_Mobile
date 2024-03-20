package fr.alcyons.phimr4.Destruction;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.android.volley.RetryPolicy;
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
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

public class ServiceDestructionActivity extends ServiceAvecConnexionActivity {

    List<Retour> listeRetours;
    ListView listViewRetours;
    RetourAdapter adapter;
    PackageManager pm;
    JSONArray retoursJson;

    boolean connexionDirecte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_retours);
        pm = ServiceDestructionActivity.this.getPackageManager();
        // Affichage des informations de base
        ((TextView) findViewById(R.id.titre)).setText("Demandes de destruction");

        // Gestion de la listView
        listViewRetours = (ListView) findViewById(R.id.listeView);
        listViewRetours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Retour retourSelectionne = (Retour) adapter.getItem(position);

                Intent newIntent = new Intent(ServiceDestructionActivity.this, DetailDestructionActivity.class);
                Bundle extras = ServiceDestructionActivity.super.getBundle();
                extras.putInt("retourSelectionneID", retourSelectionne.get_UID());

                newIntent.putExtras(extras);
                ServiceDestructionActivity.this.startActivity(newIntent);
                ServiceDestructionActivity.this.finish();
            }
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Code nécessaire afin de réaliser une requête à l' API */
        // Si on est connecté, on met à jour la liste des retours concernés dans la BDD locale
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceDestructionActivity.this) && passageParOnCreate && !connexionDirecte) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServiceDestructionActivity.this, "Veuillez patienter", "Synchronisation des retours en cours");
            }
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message mesg) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    throw new RuntimeException();
                }
            };

            RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ServiceDestructionActivity.this);

            listeRetours = new ArrayList<>();
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteDestruction;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {

                        // Takes the response from the JSON request
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int nbResultat = response.getInt("resultCount");
                                retoursJson = response.getJSONArray("PH_Retours");
                                if (nbResultat == 0) {
                                    String string = response.getString("erreur");
                                    if (string.equals(getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(ServiceDestructionActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ServiceDestructionActivity.this.finishAffinity();
                                        Intent intent = new Intent(ServiceDestructionActivity.this, AuthentificationActivity.class);
                                        ServiceDestructionActivity.this.startActivity(intent);
                                    } else {
                                        Alerte.afficherAlerte(ServiceDestructionActivity.this, "Alerte", "Aucune Destruction à traiter", "alerte");
                                    }
                                } else {
                                    viderTablesConcernees();
                                    for (int i = 0; i < retoursJson.length(); i++) {
                                        JSONObject retourJson = retoursJson.getJSONObject(i);
                                        Retour retour = new Retour(retourJson);

                                        if (retour.getEn_Attente_de().equals(getString(R.string.DestructionDemandée)) && retour.getStatut().equals(getString(R.string.statutEncours))) {
                                            listeRetours.add(retour);
                                            long rowID = gestionnaireRetour.insererUnRetourEnBDD(db, retour);
                                            if (rowID != -1) {
                                                JSONArray retourLignesJson = retourJson.getJSONArray("ph_retour_ligne");

                                                for (int k = 0; k < retourLignesJson.length(); k++) {
                                                    gestionnaireRetour_Ligne.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLignesJson.getJSONObject(k)));
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
                            Alerte.afficherAlerte(ServiceDestructionActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Destruction)", "alerte");
                        }
                    }
            ) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    //headers.put("Content-Type", "application/json");
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    return headers;
                }
            };
            obreq.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            requestQueueDestructionUtilisateur.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            if (listeRetours.size() == 0) {
                vide = true;
                nomServiceVide = "Destruction";
                ServiceDestructionActivity.this.finish();
            }
            else
            {
                if(passageParOnCreate)
                {
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                    adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours);
                    listViewRetours.setDivider(footer);
                    listViewRetours.setAdapter(adapter);

                    invalidateOptionsMenu();
                }

                passageParOnCreate = false;
            }
        } else {
            listeRetours = gestionnaireRetour.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.DestructionDemandée));
            if (listeRetours.size() == 0) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceDestructionActivity.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Destruction");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
                    ServiceDestructionActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceDestructionActivity.this.finish();
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
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                    adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours);
                    listViewRetours.setDivider(footer);
                    listViewRetours.setAdapter(adapter);

                    if (listeRetours.size() == 0) {
                        vide = true;
                        nomServiceVide = "Destruction";
                        ServiceDestructionActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                    connexionDirecte = !connexionDirecte;
                }
            }
        }

        invalidateOptionsMenu();
    }


    public void viderTablesConcernees() {
        for (Retour retour : gestionnaireRetour.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.DestructionDemandée))
                ) {
            List<Retour_Ligne> retourLignes = gestionnaireRetour_Ligne.getAllRetourLignesByRetour(db, retour);
            for (Retour_Ligne retourLigne : retourLignes
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
        Bundle scanDocumentBundle = ServiceDestructionActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent scanDocumentIntent = null;
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ServiceDestructionActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                scanDocumentIntent = new Intent(ServiceDestructionActivity.this, BarcodeCaptureActivity.class);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceDestructionActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceDestructionActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
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
                                afficherSnackBarDestruction();
                            }
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                            adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours);
                            listViewRetours.setDivider(footer);
                            listViewRetours.setAdapter(adapter);

                            if (listeRetours.size() == 0) {
                                vide = true;
                                nomServiceVide = "Destruction";
                                ServiceDestructionActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                        else
                        {
                            Intent newIntent = new Intent(ServiceDestructionActivity.this, DetailDestructionActivity.class);
                            Bundle extras = ServiceDestructionActivity.super.getBundle();
                            extras.putInt("retourSelectionneID", retourSelectionne.get_UID());

                            newIntent.putExtras(extras);
                            ServiceDestructionActivity.this.startActivity(newIntent);
                            ServiceDestructionActivity.this.finish();
                        }
                    } else {
                        /* Code nécessaire à l'affichage de la liste */
                        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                        adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours);
                        listViewRetours.setDivider(footer);
                        listViewRetours.setAdapter(adapter);

                        if (listeRetours.size() == 0) {
                            vide = true;
                            nomServiceVide = "Destruction";
                            ServiceDestructionActivity.this.finish();
                        }

                        invalidateOptionsMenu();
                    }
                }
                else
                {
                    /* Code nécessaire à l'affichage de la liste */
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                    adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours);
                    listViewRetours.setDivider(footer);
                    listViewRetours.setAdapter(adapter);

                    if (listeRetours.size() == 0) {
                        vide = true;
                        nomServiceVide = "Destruction";
                        ServiceDestructionActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                }
                break;
            }
        }
    }

    public void afficherSnackBarDestruction() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);;

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
