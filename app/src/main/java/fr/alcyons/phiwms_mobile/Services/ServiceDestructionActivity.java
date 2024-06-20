package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import java.util.Objects;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Destruction.DetailDestructionActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
public class ServiceDestructionActivity extends ServiceAvecConnexionActivity {
    List<Retour> listeRetours;
    ListView listViewRetours;
    RetourAdapter adapter;
    PackageManager pm;
    JSONArray retoursJson;
    boolean connexionDirecte;
    ActivityResultLauncher<Intent> resultScanDocument;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        pm = ServiceDestructionActivity.this.getPackageManager();
        // Affichage des informations de base

        // Gestion de la listView
        listViewRetours = findViewById(R.id.listeView);
        listViewRetours.setOnItemClickListener((parent, view, position, id) -> {
            Retour retourSelectionne = (Retour) adapter.getItem(position);

            Intent newIntent = new Intent(ServiceDestructionActivity.this, DetailDestructionActivity.class);
            Bundle extras = ServiceDestructionActivity.super.getBundle();
            assert retourSelectionne != null;
            extras.putInt("retourSelectionneID", retourSelectionne.get_UID());
            newIntent.putExtras(extras);
            ServiceDestructionActivity.this.startActivity(newIntent);
            ServiceDestructionActivity.this.finish();
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        resultScanDocument = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == CodesEchangesActivites.RETOUR_DOCUMENT) {
                        if (data != null) {
                            String code = Objects.requireNonNull(data.getExtras()).getString("code");
                            if (code != null) {
                                Retour retourSelectionne = RetourOpenHelper.getRetourByNumero(db, code);
                                if (retourSelectionne == null) {
                                    if (!code.contentEquals("")) {
                                        afficherSnackBarDestruction();
                                    }
                                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                                    adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours);
                                    listViewRetours.setDivider(footer);
                                    listViewRetours.setAdapter(adapter);

                                    if (listeRetours.isEmpty()) {
                                        vide = true;
                                        nomServiceVide = "Destruction";
                                        ServiceDestructionActivity.this.finish();
                                    }

                                    invalidateOptionsMenu();
                                } else {
                                    Intent newIntent = new Intent(ServiceDestructionActivity.this, DetailDestructionActivity.class);
                                    Bundle extras = ServiceDestructionActivity.super.getBundle();
                                    extras.putInt("retourSelectionneID", retourSelectionne.get_UID());

                                    newIntent.putExtras(extras);
                                    ServiceDestructionActivity.this.startActivity(newIntent);
                                    ServiceDestructionActivity.this.finish();
                                }
                            } else {
                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                                ((TextView) findViewById(R.id.titre)).setText("Demandes de destruction");
                                adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours);
                                listViewRetours.setDivider(footer);
                                listViewRetours.setAdapter(adapter);

                                if (listeRetours.isEmpty()) {
                                    vide = true;
                                    nomServiceVide = "Destruction";
                                    ServiceDestructionActivity.this.finish();
                                }

                                invalidateOptionsMenu();
                            }
                        } else {
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                            ((TextView) findViewById(R.id.titre)).setText("Demandes de destruction");
                            adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours);
                            listViewRetours.setDivider(footer);
                            listViewRetours.setAdapter(adapter);

                            if (listeRetours.isEmpty()) {
                                vide = true;
                                nomServiceVide = "Destruction";
                                ServiceDestructionActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                    }
                });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceDestructionActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceDestructionActivity.this.startActivity(intent);
                ServiceDestructionActivity.this.finish();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (statutConnexion && passageParOnCreate && !connexionDirecte) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceDestructionActivity.this, LayoutInflater.from(ServiceDestructionActivity.this));
            }
      
            RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ServiceDestructionActivity.this);

            listeRetours = new ArrayList<>();
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteDestruction;

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueueDestructionUtilisateur.add(obreq);
        } else {
            listeRetours = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.DestructionDemandee));
            if (listeRetours.isEmpty()) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
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
                    ((TextView) findViewById(R.id.titre)).setText("Demandes de destruction");
                    adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours);
                    listViewRetours.setDivider(footer);
                    listViewRetours.setAdapter(adapter);

                    if (listeRetours.isEmpty()) {
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
    @NonNull
    private Intent getRetourVersServiceConnexionDirectIntent() {
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceDestructionActivity.this, ServiceConnexionDirecteActivity.class);
        Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", "Destruction");

        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
        return retourVersServiceConnexionDirectIntent;
    }
    @NonNull
    private JsonObjectRequest getJsonObjectRequest(String urlRequete) {
        // Takes the response from the JSON request
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                response -> {
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

                                if (retour.getEn_Attente_de().equals(getString(R.string.DestructionDemandee)) && retour.getStatut().equals(getString(R.string.statutEncours))) {
                                    listeRetours.add(retour);
                                    long rowID = RetourOpenHelper.insererUnRetourEnBDD(db, retour);
                                    if (rowID != -1) {
                                        JSONArray retourLignesJson = retourJson.getJSONArray("ph_retour_ligne");

                                        for (int k = 0; k < retourLignesJson.length(); k++) {
                                            Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLignesJson.getJSONObject(k)));
                                        }
                                    }
                                }
                            }

                            if (listeRetours.isEmpty()) {
                                vide = true;
                                nomServiceVide = "Destruction";
                                ServiceDestructionActivity.this.finish();
                            }
                            else
                            {
                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                                ((TextView) findViewById(R.id.titre)).setText("Demandes de destruction");
                                adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours);
                                listViewRetours.setDivider(footer);
                                listViewRetours.setAdapter(adapter);
                                invalidateOptionsMenu();
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
                    Alerte.afficherAlerte(ServiceDestructionActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Destruction)", "alerte");
                }
        ) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
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
            public void retry(VolleyError error) {

            }
        });
        return obreq;
    }
    public void viderTablesConcernees() {
        for (Retour retour : RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.DestructionDemandee))
                ) {
            List<Retour_Ligne> retourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour);
            for (Retour_Ligne retourLigne : retourLignes
                    ) {
                Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigne);
            }
            RetourOpenHelper.supprimerUnRetour(db, retour);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Produit, Intitulé, N°...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> true);
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
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

        Intent scanDocumentIntent;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ServiceDestructionActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
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
        resultScanDocument.launch(scanDocumentIntent);
    }
    public void afficherSnackBarDestruction() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
