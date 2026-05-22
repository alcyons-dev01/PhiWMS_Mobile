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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourFournisseurAdapter;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.RetourFournisseur.DetailRetourFournisseurActivity;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceRetourFournisseurActivity extends ServiceAvecConnexionActivity
{
    private static final String SERVICE_NAME = "Retour fournisseur";

    // OTHERS
    private List<Retour> retours = null;
    private PackageManager packageManager = null;
    private ActivityResultLauncher<Intent> scanDocumentLauncher = null;
    private boolean connexionDirecte = false;

    // UI
    private RetourFournisseurAdapter adapter = null;
    private ListView listViewRetours = null;
    private List<String> listeDepotLivraison;
    private ArrayAdapter<String> autoCompleteAdapter;
    private AutoCompleteTextView autoComplete;
    private List<Retour> retours_base;

    @SuppressLint("SetTextI18n")
    @Override protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_liste_refresh);

        this.packageManager = this.getPackageManager();

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
                final Intent intent = new Intent(ServiceRetourFournisseurActivity.this, NavigationActivity.class);
                final Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", ServiceRetourFournisseurActivity.this.utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceRetourFournisseurActivity.this.startActivity(intent);
                ServiceRetourFournisseurActivity.this.finish();
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
        this.retours_base = new ArrayList<>();
        this.listeDepotLivraison = new ArrayList<>();
        this.listeDepotLivraison.add("Tous");

        final RequestQueue requestQueueRetourFournisseurUtilisateur = Volley.newRequestQueue(ServiceRetourFournisseurActivity.this);
        final String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(this.db) + DBOpenHelper.Urls.uriRequeteRetourFournisseur;
        final JsonObjectRequest obreq = this.getJsonObjectRequest(urlRequete);
        requestQueueRetourFournisseurUtilisateur.add(obreq);
    }

    private void showSpinnerIfNeeded() { if (!this.swipeRefreshLayout.isRefreshing()) { this.afficherSpinner(ServiceRetourFournisseurActivity.this, LayoutInflater.from(ServiceRetourFournisseurActivity.this)); } }

    private void loadRetoursFromLocalDatabase()
    {
        this.retours = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, this.getString(R.string.statutEncours), this.getString(R.string.RetourFRSDemande));
        this.retours_base = new ArrayList<>(this.retours);

        for (int i = 0; i < this.retours_base.size(); i++) { final Retour retour = this.retours_base.get(i); }
        this.listeDepotLivraison = new ArrayList<>();
        this.listeDepotLivraison.add("Tous");

        // Populate depot list from local retours
        for (final Retour retour : this.retours_base)
        {
            String depot_origine = retour.getRef_Depot_Dest();
            if(retour.getRef_Depot_Origine().contains("-PAD-") && this.utilisateurConnecte.getIdentifiant().toLowerCase().contains("alcyons")) { depot_origine = "XXX-PAD-XXX"; }
            if (!this.listeDepotLivraison.contains(depot_origine)) { this.listeDepotLivraison.add(depot_origine); }
        }

        if (this.retours.isEmpty())
        {
            this.handleNoLocalRetours();
            return;
        }

        this.handleAvailableLocalRetours();
        this.initializeAutoComplete();
    }

    private void initializeAutoComplete()
    {
        this.autoComplete = this.findViewById(R.id.listeFiltre);

        // Sort the list but keep "Tous" at the first position
        this.listeDepotLivraison.sort((a, b) -> {
            if (a.equals("Tous")) return -1;
            if (b.equals("Tous")) return 1;
            return a.compareTo(b);
        });

        this.autoCompleteAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_depot, this.listeDepotLivraison);
        this.autoComplete.setAdapter(this.autoCompleteAdapter);
        this.autoComplete.setThreshold(100);

        if (!this.listeDepotLivraison.isEmpty()) { this.autoComplete.setText(this.listeDepotLivraison.get(0), false); }

        final int hauteurEcran = this.getResources().getDisplayMetrics().heightPixels;
        this.autoComplete.setDropDownHeight(hauteurEcran / 3);
        this.autoComplete.setDropDownBackgroundResource(android.R.color.white);

        this.autoComplete.post(() -> {
            final int dpToPx = (int) (12.0F * this.getResources().getDisplayMetrics().density);
            this.autoComplete.setDropDownWidth(findViewById(R.id.listeFiltre_LL).getWidth() - dpToPx);
        });

        this.autoComplete.setOnClickListener(v -> this.autoComplete.showDropDown());

        this.findViewById(R.id.chevronFiltre).setOnClickListener(v -> this.autoComplete.showDropDown());

        this.autoComplete.setOnItemClickListener((parent, view, position, id) -> {
            final String depotNom = this.listeDepotLivraison.get(position);
            this.autoComplete.setText(depotNom, false);
            this.autoComplete.dismissDropDown();

            this.retours = new ArrayList<>();

            if (depotNom.contentEquals("Tous")) { this.retours.addAll(this.retours_base); }
            else
            {
                for (final Retour retour_courant : this.retours_base)
                {
                    String depot_origine = retour_courant.getRef_Depot_Dest();
                    if(retour_courant.getRef_Depot_Origine().contains("-PAD-") && this.utilisateurConnecte.getIdentifiant().toLowerCase().contains("alcyons")) { depot_origine = "XXX-PAD-XXX"; }

                    if (depot_origine.contentEquals(depotNom)) { this.retours.add(retour_courant); }
                }
            }

            this.configureAdapter();
        });
    }

    private void handleNoLocalRetours()
    {
        if (this.connexionDirecte)
        {
            final Intent retourVersServiceConnexionDirectIntent = this.getBackToDirectConnectionServiceIntent();
            ServiceRetourFournisseurActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
            ServiceRetourFournisseurActivity.this.finish();
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
        final Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceRetourFournisseurActivity.this, ServiceConnexionDirecteActivity.class);
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
                    if (chaine.equals(this.getString(R.string.tokenInvalide))) { Alerte.afficherAlerteInformation(ServiceRetourFournisseurActivity.this, this.getLayoutInflater(), "Alerte", "Votre session a expirée, veuillez vous reconnecter.", false, true); }
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
                    this.initializeAutoComplete();
                    this.passageParOnCreate = false;

                    new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500L);
                }
            }
            catch (final JSONException e) { e.printStackTrace(); }
        },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(ServiceRetourFournisseurActivity.this, this.getLayoutInflater(),"Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Retour fournisseur)", false, true);
                })
        {

            /**
             * Passing some request headers
             */
            @Override public Map<String, String> getHeaders()
            {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", ServiceRetourFournisseurActivity.this.utilisateurConnecte.getToken());
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
        final String scannedCode = ServiceRetourFournisseurActivity.extractScannedCode(data);
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
        final Intent detailIntent = new Intent(ServiceRetourFournisseurActivity.this, DetailRetourFournisseurActivity.class);
        final Bundle extras = ServiceRetourFournisseurActivity.super.getBundle();
        extras.putInt("retourSelectionneID", retour.get_UID());
        detailIntent.putExtras(extras);
        this.startActivity(detailIntent);
        this.finish();
    }

    private void displayRetoursOrClose()
    {
        this.configureAdapter();

        if (this.retours.isEmpty())
        {
            this.markServiceAsEmpty();
            this.finish();
            return;
        }

        this.invalidateOptionsMenu();
    }

    private void configureAdapter()
    {
        this.adapter = new RetourFournisseurAdapter(ServiceRetourFournisseurActivity.this, this.db, this.retours, this.utilisateurConnecte);
        this.listViewRetours.setAdapter(this.adapter);
    }

    private void markServiceAsEmpty()
    {
        MenuActivity.vide = Boolean.TRUE;
        MenuActivity.nomServiceVide = ServiceRetourFournisseurActivity.SERVICE_NAME;
    }

    private void hydrateRetoursFromResponse(final JSONArray retoursJsonArray) throws JSONException
    {
        this.retours.clear();

        for (int indexRetour = 0; indexRetour < retoursJsonArray.length(); indexRetour++)
        {
            final JSONObject retourJson = retoursJsonArray.getJSONObject(indexRetour);
            final Retour retour = new Retour(retourJson);
            if (!this.isRetourFournisseurRetourInProgress(retour)) { continue; }

            this.retours.add(retour);
            this.retours_base.add(retour);  // Also add to base list for filtering
            final long rowId = RetourOpenHelper.insererUnRetourEnBDD(this.db, retour);
            if (-1L == rowId) { continue; }

            String depot_origine = retour.getRef_Depot_Dest();
            if(retour.getRef_Depot_Origine().contains("-PAD-") && this.utilisateurConnecte.getIdentifiant().toLowerCase().contains("alcyons")) { depot_origine = "XXX-PAD-XXX"; }
            this.listeDepotLivraison.add(depot_origine);

            final JSONArray retourLignesJson = retourJson.getJSONArray("ph_retour_ligne");
            for (int indexLigne = 0; indexLigne < retourLignesJson.length(); indexLigne++) { Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(this.db, new Retour_Ligne(retourLignesJson.getJSONObject(indexLigne))); }
        }
    }

    private boolean isRetourFournisseurRetourInProgress(final Retour retour) { return this.getString(R.string.RetourFRSDemande).equals(retour.getEn_Attente_de()) && this.getString(R.string.statutEncours).equals(retour.getStatut()); }

    private final void clearConcernedTables()
    {
        for (final Retour retour : RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, this.getString(R.string.statutEncours), this.getString(R.string.RetourFRSDemande)))
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
        menu.findItem(R.id.menuDatamatrix).setVisible(false);

        return true;
    }

    private void launchScan()
    {
        final Bundle scanDocumentBundle = ServiceRetourFournisseurActivity.super.getBundle();
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
            return new Intent(ServiceRetourFournisseurActivity.this, ScannerDocumentActivity.class);
        }

        return new Intent(ServiceRetourFournisseurActivity.this, BarcodeCaptureActivity.class);
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
