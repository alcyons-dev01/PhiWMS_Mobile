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
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

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
import fr.alcyons.phiwms_mobile.RetourPUI.DetailRetourPUIActivity;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceRetourPUIActivity extends ServiceAvecConnexionActivity {
    List<Retour> retourList;
    ListView retourListView;
    RetourAdapter adapter;
    PackageManager pm;
    JSONArray retourJSONArray;
    NavigationActivity navigationActivity;
    Context context;
    boolean connexionDirecte;
    ActivityResultLauncher<Intent> resultScanDocument;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);

        // Modification du titre
        pm = ServiceRetourPUIActivity.this.getPackageManager();
        // Gestion de la listView
        retourListView = (ListView) findViewById(R.id.listeView);
        retourListView.setOnItemClickListener((parent, view, position, id) -> {
            Retour retourSelectionne = (Retour) adapter.getItem(position);

            Intent serviceRetourPuiIntent = new Intent(ServiceRetourPUIActivity.this, DetailRetourPUIActivity.class);
            Bundle serviceRetourPuiBundle = ServiceRetourPUIActivity.super.getBundle();
            assert retourSelectionne != null;
            serviceRetourPuiBundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

            serviceRetourPuiIntent.putExtras(serviceRetourPuiBundle);
            ServiceRetourPUIActivity.this.startActivity(serviceRetourPuiIntent);
            ServiceRetourPUIActivity.this.finish();
        });
        navigationActivity = new NavigationActivity();
        context = navigationActivity;

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
                                        afficherSnackBarRetourPUI();
                                    }
                                    /* Code nécessaire à l'affichage de la liste */
                                    adapter = new RetourAdapter(ServiceRetourPUIActivity.this, db, retourList, utilisateurConnecte);
                                    retourListView.setAdapter(adapter);

                                    if (retourList.isEmpty()) {
                                        vide = true;
                                        nomServiceVide = "Retour PUI";
                                        ServiceRetourPUIActivity.this.finish();
                                    }

                                    invalidateOptionsMenu();
                                } else {
                                    Intent serviceRetourPuiIntent = new Intent(ServiceRetourPUIActivity.this, DetailRetourPUIActivity.class);
                                    Bundle serviceRetourPuiBundle = ServiceRetourPUIActivity.super.getBundle();
                                    serviceRetourPuiBundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                                    serviceRetourPuiIntent.putExtras(serviceRetourPuiBundle);
                                    ServiceRetourPUIActivity.this.startActivity(serviceRetourPuiIntent);
                                    ServiceRetourPUIActivity.this.finish();
                                }
                            } else {
                                adapter = new RetourAdapter(ServiceRetourPUIActivity.this, db, retourList, utilisateurConnecte);
                                retourListView.setAdapter(adapter);

                                if (retourList.isEmpty()) {

                                    vide = true;
                                    nomServiceVide = "Retour PUI";
                                    ServiceRetourPUIActivity.this.finish();
                                }

                                invalidateOptionsMenu();
                            }
                        } else {
                            /* Code nécessaire à l'affichage de la liste */
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                            adapter = new RetourAdapter(ServiceRetourPUIActivity.this, db, retourList, utilisateurConnecte);
                            retourListView.setAdapter(adapter);

                            if (retourList.isEmpty()) {

                                vide = true;
                                nomServiceVide = "Retour PUI";
                                ServiceRetourPUIActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                    }
                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceRetourPUIActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceRetourPUIActivity.this.startActivity(intent);
                ServiceRetourPUIActivity.this.finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        retourList = new ArrayList<>();

        if (statutConnexion && passageParOnCreate && !connexionDirecte) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceRetourPUIActivity.this, LayoutInflater.from(ServiceRetourPUIActivity.this));
            }

            RequestQueue requestQueueRetourPUI = Volley.newRequestQueue(ServiceRetourPUIActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteRetourPUI;

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueueRetourPUI.add(obreq);
        }
        else
        {
            retourList = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.RetourPUIDemande));
            if (retourList.isEmpty()) {
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
                    adapter = new RetourAdapter(ServiceRetourPUIActivity.this, db, retourList, utilisateurConnecte);
                    retourListView.setAdapter(adapter);

                    if (retourList.isEmpty()) {
                        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                        vide = true;
                        nomServiceVide = "Retour PUI";
                        ServiceRetourPUIActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                    connexionDirecte = !connexionDirecte;
                }
            }

            invalidateOptionsMenu();
            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
        }
    }

    @NonNull
    private JsonObjectRequest getJsonObjectRequest(String urlRequete) {
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,null,
                response -> {
                    try {
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            String erreur = response.getString("erreur");
                            if (erreur.equals(getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, getLayoutInflater(), "Alerte", "Votre session a expirée, veuillez vous reconnecter.", false, true);
                            } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, getLayoutInflater(), "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", false, true);
                            } else if (!erreur.equals(getString(R.string.aucunRetour))) {
                                Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, getLayoutInflater(), "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete service retour pui", false, true);
                            } else {
                                arreterSpinner();
                                Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, getLayoutInflater(), "Alerte", "Aucun Retour PUI à traiter", false, true);
                            }
                        } else {
                            viderTablesConcernees();

                            retourJSONArray = response.getJSONArray("PH_Retours");

                            for (int i = 0; i < retourJSONArray.length(); i++) {
                                JSONObject retourJSONObject = retourJSONArray.getJSONObject(i);
                                Retour retour = new Retour(retourJSONObject);

                                if (retour.getEn_Attente_de().equals(getString(R.string.RetourPUIDemande)) && retour.getStatut().equals(getString(R.string.statutEncours))) {
                                    retourList.add(retour);
                                    long rowID = RetourOpenHelper.insererUnRetourEnBDD(db, retour);
                                    if (rowID != -1) {
                                        JSONArray retourLigneJSONArray = retourJSONObject.getJSONArray("ph_retour_ligne");
                                        for (int k = 0; k < retourLigneJSONArray.length(); k++) {
                                            JSONObject retourLigneJSONObject = retourLigneJSONArray.getJSONObject(k);
                                            Retour_Ligne retourLigne = new Retour_Ligne(retourLigneJSONObject);
                                            Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, retourLigne);
                                        }
                                    }
                                }
                            }

                            if (retourList.isEmpty()) {
                                new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                                vide = true;
                                nomServiceVide = "Retour PUI";
                                ServiceRetourPUIActivity.this.finish();
                            }
                            else
                            {
                                adapter = new RetourAdapter(ServiceRetourPUIActivity.this, db, retourList, utilisateurConnecte);
                                retourListView.setAdapter(adapter);
                                new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                                passageParOnCreate = false;
                            }

                            invalidateOptionsMenu();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, getLayoutInflater(), "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP service retour pui", false, true);
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
        return obreq;
    }

    public void viderTablesConcernees() {
        for (Retour retour : RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.RetourPUIDemande))
                ) {
            List<Retour_Ligne> retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retour);
            for (Retour_Ligne retourLigne : retourLigneList
                    ) {
                Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigne);
            }
            RetourOpenHelper.supprimerUnRetour(db, retour);
        }
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Produit, Intitulé, N°...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
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
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(ServiceRetourPUIActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
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
        resultScanDocument.launch(scanDocumentIntent);
    }

    public void afficherSnackBarRetourPUI() {
        Snackbar snackbar = null;
        snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
