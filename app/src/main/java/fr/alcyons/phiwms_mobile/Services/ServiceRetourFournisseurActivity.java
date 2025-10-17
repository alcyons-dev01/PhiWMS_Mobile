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
import android.widget.LinearLayout;
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
import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.RetourFournisseur.DetailRetourFournisseurActivity;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceRetourFournisseurActivity extends ServiceAvecConnexionActivity {
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

        // Modification du titre
        pm = ServiceRetourFournisseurActivity.this.getPackageManager();
        listViewRetours = findViewById(R.id.listeView);
        listViewRetours.setOnItemClickListener((parent, view, position, id) -> {
            Retour retourSelectionne = (Retour) adapter.getItem(position);

            Intent newIntent = new Intent(ServiceRetourFournisseurActivity.this, DetailRetourFournisseurActivity.class);
            Bundle extras = ServiceRetourFournisseurActivity.super.getBundle();
            assert retourSelectionne != null;
            extras.putInt("retourSelectionneID", retourSelectionne.get_UID());

            newIntent.putExtras(extras);
            ServiceRetourFournisseurActivity.this.startActivity(newIntent);
            ServiceRetourFournisseurActivity.this.finish();
        });

        ((LinearLayout) findViewById(R.id.triListe)).setVisibility(ListView.GONE);

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        resultScanDocument = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == CodesEchangesActivites.RESULT_OK) {
                        if (data != null) {
                            String code = Objects.requireNonNull(data.getExtras()).getString("code");
                            if (code != null) {
                                Retour retourSelectionne = RetourOpenHelper.getRetourByNumero(db, code);
                                if (retourSelectionne == null) {
                                    if (!code.contentEquals("")) {
                                        afficherSnackBarRetourFournisseur();
                                    }
                                    /* Code nécessaire à l'affichage de la liste */
                                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                                    ((TextView) findViewById(R.id.titre)).setText("Retours Fournisseur");
                                    adapter = new RetourAdapter(ServiceRetourFournisseurActivity.this, db, listeRetours, utilisateurConnecte);
                                    //listViewRetours.setDivider(footer);
                                    listViewRetours.setAdapter(adapter);

                                    if (listeRetours.isEmpty()) {
                                        vide = true;
                                        nomServiceVide = "Retour fournisseur";
                                        ServiceRetourFournisseurActivity.this.finish();
                                    }
                                    invalidateOptionsMenu();
                                } else {
                                    Intent newIntent = new Intent(ServiceRetourFournisseurActivity.this, DetailRetourFournisseurActivity.class);
                                    Bundle extras = ServiceRetourFournisseurActivity.super.getBundle();
                                    extras.putInt("retourSelectionneID", retourSelectionne.get_UID());

                                    newIntent.putExtras(extras);
                                    ServiceRetourFournisseurActivity.this.startActivity(newIntent);
                                    ServiceRetourFournisseurActivity.this.finish();
                                }
                            } else {
                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                                adapter = new RetourAdapter(ServiceRetourFournisseurActivity.this, db, listeRetours, utilisateurConnecte);
                                //listViewRetours.setDivider(footer);
                                listViewRetours.setAdapter(adapter);

                                if (listeRetours.isEmpty()) {
                                    vide = true;
                                    nomServiceVide = "Retour fournisseur";
                                    ServiceRetourFournisseurActivity.this.finish();
                                }

                                invalidateOptionsMenu();
                            }
                        } else {
                            /* Code nécessaire à l'affichage de la liste */
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                            adapter = new RetourAdapter(ServiceRetourFournisseurActivity.this, db, listeRetours, utilisateurConnecte);
                            //listViewRetours.setDivider(footer);
                            listViewRetours.setAdapter(adapter);

                            if (listeRetours.isEmpty()) {
                                vide = true;
                                nomServiceVide = "Retour fournisseur";
                                ServiceRetourFournisseurActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                    }
                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceRetourFournisseurActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceRetourFournisseurActivity.this.startActivity(intent);
                ServiceRetourFournisseurActivity.this.finish();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceRetourFournisseurActivity.this,  LayoutInflater.from(ServiceRetourFournisseurActivity.this));;
            }
     
            RequestQueue requestQueueRetourFournisseur = Volley.newRequestQueue(ServiceRetourFournisseurActivity.this);

            listeRetours = new ArrayList<>();
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteRetourFournisseur;

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueueRetourFournisseur.add(obreq);
        } else {
            listeRetours = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.RetourFRSDemande));
            if (listeRetours.isEmpty()) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
                    ServiceRetourFournisseurActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceRetourFournisseurActivity.this.finish();
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
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                    ((TextView) findViewById(R.id.titre)).setText("Retours Fournisseur");
                    adapter = new RetourAdapter(ServiceRetourFournisseurActivity.this, db, listeRetours, utilisateurConnecte);
                    //listViewRetours.setDivider(footer);
                    listViewRetours.setAdapter(adapter);

                    if (listeRetours.isEmpty()) {
                        vide = true;
                        nomServiceVide = "Retour fournisseur";
                        ServiceRetourFournisseurActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                    connexionDirecte = !connexionDirecte;
                }
            }
        }

        invalidateOptionsMenu();
    }
    @NonNull
    private JsonObjectRequest getJsonObjectRequest(String urlRequete) {
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                response -> {
                    try {
                        int nbResultat = response.getInt("resultCount");
                        if (nbResultat == 0) {
                            String string = response.getString("erreur");
                            if (string.equals(getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerte(ServiceRetourFournisseurActivity.this, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                //DBOpenHelper.viderBasesDeDonnees(db);
                                ServiceRetourFournisseurActivity.this.finishAffinity();
                                Intent intent = new Intent(ServiceRetourFournisseurActivity.this, AuthentificationActivity.class);
                                ServiceRetourFournisseurActivity.this.startActivity(intent);
                            } else {
                                arreterSpinner();
                                vide = true;
                                nomServiceVide = "Retour fournisseur";
                                retourNavigation(ServiceRetourFournisseurActivity.this);
                            }
                        } else {
                            retoursJson = response.getJSONArray("PH_Retours");
                            viderTablesConcernees();
                            for (int i = 0; i < retoursJson.length(); i++) {
                                JSONObject retourJson = retoursJson.getJSONObject(i);
                                Retour retour = new Retour(retourJson);

                                if (retour.getEn_Attente_de().equals(getString(R.string.RetourFRSDemande)) && retour.getStatut().equals(getString(R.string.statutEncours))) {
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
                                nomServiceVide = "Retour fournisseur";
                                ServiceRetourFournisseurActivity.this.finish();
                            }
                            else
                            {
                                /* Code nécessaire à l'affichage de la liste */
                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                                ((TextView) findViewById(R.id.titre)).setText("Retours Fournisseur");
                                adapter = new RetourAdapter(ServiceRetourFournisseurActivity.this, db, listeRetours, utilisateurConnecte);
                                //listViewRetours.setDivider(footer);
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
                    Alerte.afficherAlerte(ServiceRetourFournisseurActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Retour Fournisseur)", "alerte");
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
        return obreq;
    }
    @NonNull
    private Intent getRetourVersServiceConnexionDirectIntent() {
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceRetourFournisseurActivity.this, ServiceConnexionDirecteActivity.class);
        Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", "Retour fournisseur");

        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
        return retourVersServiceConnexionDirectIntent;
    }
    public void viderTablesConcernees() {
        for (Retour retour : RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.RetourFRSDemande))
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
        MenuItem itemScan = menu.findItem(R.id.menuDatamatrix);
        itemScan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                lancerScan();
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);
        return true;
    }
    public void lancerScan()
    {
        Bundle scanDocumentBundle = ServiceRetourFournisseurActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(ServiceRetourFournisseurActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServiceRetourFournisseurActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceRetourFournisseurActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        resultScanDocument.launch(scanDocumentIntent);
    }
    public void afficherSnackBarRetourFournisseur() {
        Snackbar snackbar;
        snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
