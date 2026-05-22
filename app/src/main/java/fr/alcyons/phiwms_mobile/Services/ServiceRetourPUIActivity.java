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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourPUIAdapter;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.RetourPUI.DetailRetourPUIActivity;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceRetourPUIActivity extends ServiceAvecConnexionActivity
{
    private static final String SERVICE_NAME = "Retour PUI";

    private List<Retour> retours = null;
    private ListView listViewRetours = null;
    private RetourPUIAdapter adapter = null;
    private PackageManager packageManager = null;
    private boolean connexionDirecte = false;
    private ActivityResultLauncher<Intent> scanDocumentLauncher = null;
    private List<String> listeDepotLivraison = null;
    private ArrayAdapter<String> autoCompleteAdapter = null;
    private AutoCompleteTextView autoComplete = null;
    private List<Retour> retoursBase = null;

    @SuppressLint("SetTextI18n")
    @Override protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_liste_refresh);

        this.packageManager = this.getPackageManager();
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
                final Intent intent = new Intent(ServiceRetourPUIActivity.this, NavigationActivity.class);
                final Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", ServiceRetourPUIActivity.this.utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceRetourPUIActivity.this.startActivity(intent);
                ServiceRetourPUIActivity.this.finish();
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
        this.retours = new ArrayList<>();
        this.retoursBase = new ArrayList<>();
        this.listeDepotLivraison = new ArrayList<>();
        this.listeDepotLivraison.add("Tous");

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

        final RequestQueue requestQueueRetourPUI = Volley.newRequestQueue(ServiceRetourPUIActivity.this);
        final String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(this.db) + DBOpenHelper.Urls.uriRequeteRetourPUI;
        final JsonObjectRequest obreq = this.getJsonObjectRequest(urlRequete);
        requestQueueRetourPUI.add(obreq);
    }

    private void showSpinnerIfNeeded() { if (!this.swipeRefreshLayout.isRefreshing()) { this.afficherSpinner(ServiceRetourPUIActivity.this, LayoutInflater.from(ServiceRetourPUIActivity.this)); } }

    private void loadRetoursFromLocalDatabase()
    {
        this.retours = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, this.getString(R.string.statutEncours), this.getString(R.string.RetourPUIDemande));
        this.retoursBase = new ArrayList<>(this.retours);
        this.populateDepotList(this.retoursBase);

        if (this.retours.isEmpty()) { this.handleNoLocalRetours(); }
        else
        {
            this.handleAvailableLocalRetours();
            this.initializeAutoComplete();
        }

        this.invalidateOptionsMenu();
        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500L);
    }

    private void populateDepotList(final List<Retour> retoursSource)
    {
        for (final Retour retour : retoursSource)
        {
            final String depotOrigine = this.getDepotLivraison(retour);
            if (!this.listeDepotLivraison.contains(depotOrigine)) { this.listeDepotLivraison.add(depotOrigine); }
        }
    }

    private void initializeAutoComplete()
    {
        this.autoComplete = this.findViewById(R.id.listeFiltre);
        if (null == this.autoComplete) { return; }

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
            this.autoComplete.setDropDownWidth(this.findViewById(R.id.listeFiltre_LL).getWidth() - dpToPx);
        });

        this.autoComplete.setOnClickListener(v -> this.autoComplete.showDropDown());
        this.findViewById(R.id.chevronFiltre).setOnClickListener(v -> this.autoComplete.showDropDown());
        this.autoComplete.setOnItemClickListener((parent, view, position, id) -> this.filterRetoursByDepot(position));
    }

    private void filterRetoursByDepot(final int position)
    {
        final String depotNom = this.listeDepotLivraison.get(position);
        this.autoComplete.setText(depotNom, false);
        this.autoComplete.dismissDropDown();

        this.retours = new ArrayList<>();

        if (depotNom.contentEquals("Tous")) { this.retours.addAll(this.retoursBase); }
        else
        {
            for (final Retour retourCourant : this.retoursBase)
            {
                if (this.getDepotLivraison(retourCourant).contentEquals(depotNom)) { this.retours.add(retourCourant); }
            }
        }

        this.configureAdapter();
    }

    private String getDepotLivraison(final Retour retour)
    {
        String[] intitule_tab = retour.getIntitule().split(":");
        String depotOrigine = intitule_tab[0];
        if (retour.getRef_Depot_Origine().contains("-PAD-") && this.utilisateurConnecte.getIdentifiant().toLowerCase().contains("alcyons")) { depotOrigine = "XXX-PAD-XXX"; }
        return depotOrigine;
    }

    private void handleNoLocalRetours()
    {
        if (this.connexionDirecte)
        {
            this.startActivity(this.getBackToDirectConnectionServiceIntent());
            this.finish();
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
        final Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceRetourPUIActivity.this, ServiceConnexionDirecteActivity.class);
        final Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", this.utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", ServiceRetourPUIActivity.SERVICE_NAME);
        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
        return retourVersServiceConnexionDirectIntent;
    }

    @NonNull private JsonObjectRequest getJsonObjectRequest(final String urlRequete)
    {
        final JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null, response -> {
            try
            {
                final int resultCount = response.getInt("resultCount");
                if (0 == resultCount)
                {
                    final String erreur = response.getString("erreur");
                    if (erreur.equals(this.getString(R.string.tokenInvalide))) { Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, this.getLayoutInflater(), "Alerte", "Votre session a expirée, veuillez vous reconnecter.", false, true); }
                    else if (erreur.equals(this.getString(R.string.tokenExpire))) { Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, this.getLayoutInflater(), "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", false, true); }
                    else if (!erreur.equals(this.getString(R.string.aucunRetour))) { Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, this.getLayoutInflater(), "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete service retour pui", false, true); }
                    else
                    {
                        this.arreterSpinner();
                        Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, this.getLayoutInflater(), "Alerte", "Aucun Retour PUI à traiter", false, true);
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

                this.invalidateOptionsMenu();
            }
            catch (final JSONException e) { e.printStackTrace(); }
        },
        error -> {
            Log.e("Volley", "Error");
            Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, this.getLayoutInflater(), "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP service retour pui", false, true);
        })
        {
            @Override public Map<String, String> getHeaders() throws AuthFailureError
            {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", ServiceRetourPUIActivity.this.utilisateurConnecte.getToken());
                return headers;
            }
        };

        obreq.setRetryPolicy(this.retryPolicy);
        return obreq;
    }

    private void handleDocumentScanResult(final Intent data)
    {
        final String scannedCode = ServiceRetourPUIActivity.extractScannedCode(data);
        if (null == scannedCode)
        {
            this.updateResultCount();
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
        final Intent serviceRetourPuiIntent = new Intent(ServiceRetourPUIActivity.this, DetailRetourPUIActivity.class);
        final Bundle serviceRetourPuiBundle = ServiceRetourPUIActivity.super.getBundle();
        serviceRetourPuiBundle.putInt("retourSelectionneID", retour.get_UID());
        serviceRetourPuiIntent.putExtras(serviceRetourPuiBundle);
        this.startActivity(serviceRetourPuiIntent);
        this.finish();
    }

    private void displayRetoursOrClose()
    {
        this.configureAdapter();

        if (this.retours.isEmpty())
        {
            ServiceRetourPUIActivity.markServiceAsEmpty();
            this.finish();
            return;
        }

        this.invalidateOptionsMenu();
    }

    private void configureAdapter()
    {
        this.updateResultCount();
        this.adapter = new RetourPUIAdapter(ServiceRetourPUIActivity.this, this.db, this.retours, this.utilisateurConnecte);
        this.listViewRetours.setAdapter(this.adapter);
    }

    private void updateResultCount()
    {
        final TextView nbElementInAdapter = this.findViewById(R.id.nbElementInAdapter);
        if (null != nbElementInAdapter) { nbElementInAdapter.setText(String.valueOf(this.retours.size())); }
    }

    private static void markServiceAsEmpty()
    {
        MenuActivity.vide = Boolean.TRUE;
        MenuActivity.nomServiceVide = ServiceRetourPUIActivity.SERVICE_NAME;
    }

    private void hydrateRetoursFromResponse(final JSONArray retoursJsonArray) throws JSONException
    {
        this.retours.clear();

        for (int indexRetour = 0; indexRetour < retoursJsonArray.length(); indexRetour++)
        {
            final JSONObject retourJSONObject = retoursJsonArray.getJSONObject(indexRetour);
            final Retour retour = new Retour(retourJSONObject);
            if (!this.isRetourPUIInProgress(retour)) { continue; }

            this.retours.add(retour);
            this.retoursBase.add(retour);
            final long rowId = RetourOpenHelper.insererUnRetourEnBDD(this.db, retour);
            if (-1L == rowId) { continue; }

            final String depotOrigine = this.getDepotLivraison(retour);
            if (!this.listeDepotLivraison.contains(depotOrigine)) { this.listeDepotLivraison.add(depotOrigine); }

            final JSONArray retourLigneJSONArray = retourJSONObject.getJSONArray("ph_retour_ligne");
            for (int indexLigne = 0; indexLigne < retourLigneJSONArray.length(); indexLigne++)
            {
                final JSONObject retourLigneJSONObject = retourLigneJSONArray.getJSONObject(indexLigne);
                final Retour_Ligne retourLigne = new Retour_Ligne(retourLigneJSONObject);
                Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(this.db, retourLigne);
            }
        }
    }

    private boolean isRetourPUIInProgress(final Retour retour) { return this.getString(R.string.RetourPUIDemande).equals(retour.getEn_Attente_de()) && this.getString(R.string.statutEncours).equals(retour.getStatut()); }

    public void clearConcernedTables()
    {
        for (final Retour retour : RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, this.getString(R.string.statutEncours), this.getString(R.string.RetourPUIDemande)))
        {
            final List<Retour_Ligne> retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(this.db, retour);
            for (final Retour_Ligne retourLigne : retourLigneList) { Retour_LigneOpenHelper.supprimerUnRetourLigne(this.db, retourLigne); }
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

    public void launchScan()
    {
        final Bundle scanDocumentBundle = ServiceRetourPUIActivity.super.getBundle();
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
            return new Intent(ServiceRetourPUIActivity.this, ScannerDocumentActivity.class);
        }

        scanDocumentBundle.putBoolean("modeRafale", false);
        return new Intent(ServiceRetourPUIActivity.this, BarcodeCaptureActivity.class);
    }

    private boolean shouldUseDedicatedDocumentScanner()
    {
        final String manufacturer = Build.MANUFACTURER.toLowerCase(Locale.ROOT);
        return Build.MANUFACTURER.contains("Zebra Technologies") || manufacturer.contains("honeywell") || manufacturer.contains("google") || !this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void showUnknownDocumentSnackbar()
    {
        final Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi")
        final Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(this.getResources().getColor(R.color.rouge2, null));
        final TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8.0F);
        snackbar.show();
    }
}
