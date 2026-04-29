package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceDestructionActivity extends ServiceAvecConnexionActivity
{
    private static final String SERVICE_NAME = "Destruction";
    private static final String SCREEN_TITLE = "Demandes de destruction";

    // OTHERS
    private List<Retour> retours = null;
    private PackageManager packageManager = null;
    private ActivityResultLauncher<Intent> scanDocumentLauncher = null;
    private boolean connexionDirecte = false;

    // UI
    private RetourAdapter adapter = null;
    private ListView listViewRetours = null;

    @SuppressLint("SetTextI18n")
    @Override protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_liste_refresh);

        this.packageManager = this.getPackageManager();

        // Affichage des informations de base
        this.findViewById(R.id.triListe).setVisibility(View.GONE);

        // Gestion de la listView
        this.listViewRetours = this.findViewById(R.id.listeView);
        this.listViewRetours.setOnItemClickListener((parent, view, position, id) -> {
            final Retour retourSelectionne = (Retour) this.adapter.getItem(position);
            assert null != retourSelectionne;
            this.openRetourDetail(retourSelectionne);
        });

        this.connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(this.db).booleanValue();

        this.scanDocumentLauncher = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            final Intent data = result.getData();
            if (CodesEchangesActivites.RESULT_OK == result.getResultCode()) { this.handleDocumentScanResult(data); }
        });

        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true)
        {
            @Override public void handleOnBackPressed()
            {
                final Intent intent = new Intent(ServiceDestructionActivity.this, NavigationActivity.class);
                final Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", ServiceDestructionActivity.this.utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceDestructionActivity.this.startActivity(intent);
                ServiceDestructionActivity.this.finish();
            }
        });
    }

    @Override public void onResume()
    {
        super.onResume();
        this.connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(this.db).booleanValue();
        this.refreshRetours();
        this.invalidateOptionsMenu();
    }

    private void refreshRetours()
    {
        if (this.shouldLoadFromServer())
        {
            this.loadRetoursFromServer();
            return;
        }

        this.loadRetoursFromLocalDatabase();
    }

    private boolean shouldLoadFromServer() { return MainActivity.statutConnexion && this.passageParOnCreate && !this.connexionDirecte; }

    private void loadRetoursFromServer()
    {
        this.showSpinnerIfNeeded();
        this.retours = new ArrayList<>();

        final RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ServiceDestructionActivity.this);
        final String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(this.db) + DBOpenHelper.Urls.uriRequeteDestruction;
        final JsonObjectRequest obreq = this.getJsonObjectRequest(urlRequete);
        requestQueueDestructionUtilisateur.add(obreq);
    }

    private void showSpinnerIfNeeded() { if (!this.swipeRefreshLayout.isRefreshing()) { this.afficherSpinner(ServiceDestructionActivity.this, LayoutInflater.from(ServiceDestructionActivity.this)); } }

    private void loadRetoursFromLocalDatabase()
    {
        this.retours = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, this.getString(R.string.statutEncours), this.getString(R.string.DestructionDemandee));
        if (this.retours.isEmpty())
        {
            this.handleNoLocalRetours();
            return;
        }

        this.handleAvailableLocalRetours();
    }

    private void handleNoLocalRetours()
    {
        if (this.connexionDirecte)
        {
            final Intent retourVersServiceConnexionDirectIntent = this.getBackToDirectConnectionServiceIntent();
            ServiceDestructionActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
            ServiceDestructionActivity.this.finish();
            return;
        }

        this.connexionNecessaire();
    }

    private void handleAvailableLocalRetours()
    {
        this.passageParOnCreate = false;

        if (!this.connexionDirecte) { return; }

        this.displayRetoursOrClose();
        this.connexionDirecte = !this.connexionDirecte;
    }

    @NonNull private Intent getBackToDirectConnectionServiceIntent()
    {
        final Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceDestructionActivity.this, ServiceConnexionDirecteActivity.class);
        final Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", this.utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", SERVICE_NAME);
        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);

        return retourVersServiceConnexionDirectIntent;
    }
    @NonNull private JsonObjectRequest getJsonObjectRequest(final String urlRequete)
    {
        // Takes the response from the JSON request
        final JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null, response -> {
            try
            {
                final int nbResultat = response.getInt("resultCount");
                if (0 == nbResultat)
                {
                    final String chaine = response.getString("erreur");
                    if (chaine.equals(this.getString(R.string.tokenInvalide))) { Alerte.afficherAlerteInformation(ServiceDestructionActivity.this, this.getLayoutInflater(), "Alerte", "Votre session a expirée, veuillez vous reconnecter.", false, true); }
                    else
                    {
                        this.arreterSpinner();
                        this.markServiceAsEmpty();
                        this.retourNavigation();
                    }
                }
                else
                {
                    this.clearConcernedTables();
                    this.hydrateRetoursFromResponse(response.getJSONArray("PH_Retours"));
                    this.displayRetoursOrClose();
                    this.passageParOnCreate = false;

                    new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500L);
                }
            }
            catch (final JSONException e) { e.printStackTrace(); }
        },
        error -> {
            Log.e("Volley", "Error");
            Alerte.afficherAlerteInformation(ServiceDestructionActivity.this, this.getLayoutInflater(),"Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Destruction)", false, true);
        })
        {

            /**
             * Passing some request headers
             */
            @Override public Map<String, String> getHeaders()
            {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", ServiceDestructionActivity.this.utilisateurConnecte.getToken());
                return headers;
            }
        };

        obreq.setRetryPolicy(new RetryPolicy() {
            @Override public int getCurrentTimeout() { return 50000; }
            @Override public int getCurrentRetryCount() { return 50000; }
            @Override public void retry(final VolleyError error) {}
        });

        return obreq;
    }

    private void handleDocumentScanResult(final Intent data)
    {
        final String scannedCode = ServiceDestructionActivity.extractScannedCode(data);
        if (null == scannedCode)
        {
            this.displayRetoursOrClose();
            return;
        }

        final Retour retourSelectionne = RetourOpenHelper.getRetourByNumero(this.db, scannedCode);
        if (null != retourSelectionne)
        {
            this.openRetourDetail(retourSelectionne);
            return;
        }

        if (!scannedCode.isEmpty()) { this.showUnknownDocumentSnackbar(); }
        this.displayRetoursOrClose();
    }

    @Nullable private static String extractScannedCode(final Intent data)
    {
        if (null == data || null == data.getExtras()) { return null; }
        return Objects.requireNonNull(data.getExtras()).getString("code");
    }

    private void openRetourDetail(@NonNull final Retour retour)
    {
        final Intent detailIntent = new Intent(ServiceDestructionActivity.this, DetailDestructionActivity.class);
        final Bundle extras = ServiceDestructionActivity.super.getBundle();
        extras.putInt("retourSelectionneID", retour.get_UID());
        detailIntent.putExtras(extras);
        this.startActivity(detailIntent);
        this.finish();
    }

    private void displayRetoursOrClose()
    {
        this.updateCountersAndTitle();
        this.configureAdapter();

        if (this.retours.isEmpty())
        {
            this.markServiceAsEmpty();
            this.finish();
            return;
        }

        this.invalidateOptionsMenu();
    }

    private void updateCountersAndTitle()
    {
        ((TextView) this.findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(this.retours.size()));
        ((TextView) this.findViewById(R.id.titre)).setText(SCREEN_TITLE);
    }

    private void configureAdapter()
    {
        this.adapter = new RetourAdapter(ServiceDestructionActivity.this, this.db, this.retours, this.utilisateurConnecte);
        this.listViewRetours.setAdapter(this.adapter);
    }

    private void markServiceAsEmpty()
    {
        MenuActivity.vide = Boolean.TRUE;
        MenuActivity.nomServiceVide = SERVICE_NAME;
    }

    private void hydrateRetoursFromResponse(final JSONArray retoursJsonArray) throws JSONException
    {
        this.retours.clear();

        for (int indexRetour = 0; indexRetour < retoursJsonArray.length(); indexRetour++)
        {
            final JSONObject retourJson = retoursJsonArray.getJSONObject(indexRetour);
            final Retour retour = new Retour(retourJson);
            if (!this.isDestructionRetourInProgress(retour)) { continue; }

            this.retours.add(retour);
            final long rowId = RetourOpenHelper.insererUnRetourEnBDD(this.db, retour);
            if (-1L == rowId) { continue; }

            final JSONArray retourLignesJson = retourJson.getJSONArray("ph_retour_ligne");
            for (int indexLigne = 0; indexLigne < retourLignesJson.length(); indexLigne++) { Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(this.db, new Retour_Ligne(retourLignesJson.getJSONObject(indexLigne))); }
        }
    }

    private boolean isDestructionRetourInProgress(final Retour retour) { return this.getString(R.string.DestructionDemandee).equals(retour.getEn_Attente_de()) && this.getString(R.string.statutEncours).equals(retour.getStatut()); }

    private final void clearConcernedTables()
    {
        for (final Retour retour : RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, this.getString(R.string.statutEncours), this.getString(R.string.DestructionDemandee)))
        {
            final List<Retour_Ligne> retourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(this.db, retour);
            for (final Retour_Ligne retourLigne : retourLignes) { Retour_LigneOpenHelper.supprimerUnRetourLigne(this.db, retourLigne); }
            RetourOpenHelper.supprimerUnRetour(this.db, retour);
        }
    }

    @Override public boolean onPrepareOptionsMenu(final Menu menu)
    {
        super.prepareOptionsMenu(menu, this.adapter, null, "Produit, Intitulé, N°...");

        final MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> true);

        final MenuItem itemScan = menu.findItem(R.id.menuDatamatrix);
        itemScan.setOnMenuItemClickListener(menuItem -> {
            this.launchScan();
            return true;
        });

        return true;
    }

    @Override public boolean onCreateOptionsMenu(final Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        final MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);

        return true;
    }

    private void launchScan()
    {
        final Bundle scanDocumentBundle = ServiceDestructionActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);

        final Intent scanDocumentIntent = this.createScanDocumentIntent(scanDocumentBundle);

        scanDocumentIntent.putExtras(scanDocumentBundle);
        this.scanDocumentLauncher.launch(scanDocumentIntent);
    }

    @NonNull private Intent createScanDocumentIntent(final Bundle scanDocumentBundle)
    {
        if (this.shouldUseDedicatedDocumentScanner())
        {
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            return new Intent(ServiceDestructionActivity.this, ScannerDocumentActivity.class);
        }

        return new Intent(ServiceDestructionActivity.this, BarcodeCaptureActivity.class);
    }

    private boolean shouldUseDedicatedDocumentScanner()
    {
        final String manufacturer = Build.MANUFACTURER.toLowerCase();
        return Build.MANUFACTURER.contains("Zebra Technologies") || manufacturer.contains("honeywell") || manufacturer.contains("google") || !this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private final void showUnknownDocumentSnackbar()
    {
        final Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), BaseTransientBottomBar.LENGTH_LONG);

        @SuppressLint("RestrictedApi")
        final Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(this.getResources().getColor(R.color.rouge2, null));
        final TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8.0F);
        snackbar.show();
    }
}
