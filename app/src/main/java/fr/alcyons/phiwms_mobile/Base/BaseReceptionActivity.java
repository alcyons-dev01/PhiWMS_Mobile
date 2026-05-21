package fr.alcyons.phiwms_mobile.Base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import android.text.Html;

import java.net.URLEncoder;
import java.util.List;

import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPhotoIdentificationDocument;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Callbacks.ReceptionApiCallbacks;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Handler.ScanResultHandler;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ReceptionAdapter;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Helper.NavigationHelper;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.Reception.DetailReception_V2;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
import fr.alcyons.phiwms_mobile.ViewModel.ReceptionPuiViewModel;

/**
 * Classe abstraite commune à ServiceReceptionPuiActivity et ServiceReceptionPadActivity.
 * Tout le code partagé (cycle de vie, scan, AutoComplete, adapter, navigation)
 * vit ici. Les sous-classes n'implémentent que ce qui les différencie.
 */
public abstract class BaseReceptionActivity extends ServiceAvecConnexionActivity {

    private static final String TAG = "BaseReceptionActivity";

    // ── UI ──────────────────────────────────────────────────────────────────
    protected ListView commandeListView;
    private AutoCompleteTextView autoComplete;
    private ReceptionAdapter commandeAdapter;

    // ── Données ─────────────────────────────────────────────────────────────
    protected ReceptionPuiViewModel viewModel;
    protected Depot depot;
    protected boolean connexionDirecte;
    private PackageManager pm;

    // ── Scan ─────────────────────────────────────────────────────────────────
    private ActivityResultLauncher<Intent> resultScanDocument;
    private ScanResultHandler scanResultHandler;

    // ────────────────────────────────────────────────────────────────────────
    // Contrat des sous-classes
    // ────────────────────────────────────────────────────────────────────────

    /** Dépôt à utiliser pour charger les commandes (PUI ou PAD). */
    protected abstract Depot chargerDepot();

    /** Nom affiché dans les messages / snackbars (ex. "Réception PUI"). */
    protected abstract String getNomService();

    /**
     * Construit la requête Volley adaptée au service.
     * Appelée avec l'URL déjà encodée.
     */
    protected abstract JsonObjectRequest creerRequeteApi(
            String url,
            ReceptionPuiViewModel viewModel,
            ReceptionApiCallbacks.OnSuccessCallback onSuccess,
            ReceptionApiCallbacks.OnErreurCallback onErreur);

    /**
     * Permet à la sous-classe d'enrichir le Bundle avant d'ouvrir DetailReception_V2
     * (ex. PAD ajoute serviceActuel.getId()).
     */
    protected void enrichirBundleDetail(Bundle bundle, Commande commande) {
        // Par défaut : rien. Surchargé par PAD.
    }

    /**
     * Permet à la sous-classe d'exécuter une action après un scan annulé/inconnu
     * avant le rafraîchissement de la liste (ex. PAD ajoute getCommandeTestAlcyons).
     */
    protected void onAvantRafraichissement() {
        // Par défaut : rien. Surchargé par PAD.
    }

    // ────────────────────────────────────────────────────────────────────────
    // Cycle de vie
    // ────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);

        commandeListView = findViewById(R.id.listeView);
        pm = getPackageManager();

        viewModel = new ViewModelProvider(this).get(ReceptionPuiViewModel.class);
        depot = chargerDepot();
        connexionDirecte = fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper
                .getConnexionDirecte(db);

        scanResultHandler = new ScanResultHandler(
                this, db, utilisateurConnecte.getId(),
                new ScanResultHandler.AdapterCallback() {
                    @Override public void gestionAdapter() { rafraichirAdapter(); }
                    @Override public List<Commande> getCommandeList() {
                        return viewModel.getCommandesVisibles().getValue();
                    }
                    @Override public Bundle getBaseBundle() {
                        return BaseReceptionActivity.super.getBundle();
                    }
                }
        );

        observerViewModel();
        enregistrerScanLauncher();
        configurerListView();
        configurerRetourArriere();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            chargerDepuisApi();
        } else {
            chargerDepuisBdd();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Observation ViewModel
    // ────────────────────────────────────────────────────────────────────────

    private void observerViewModel() {
        viewModel.getCommandesVisibles().observe(this, commandes -> rafraichirAdapter());
        viewModel.getFournisseurs().observe(this, this::initialiserAutoComplete);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Chargement
    // ────────────────────────────────────────────────────────────────────────

    private void chargerDepuisApi() {
        if (!swipeRefreshLayout.isRefreshing()) {
            afficherSpinner(this, LayoutInflater.from(this));
        }
        try {
            String url = ParametresServeurOpenHelper.getPartieCommuneUrls(db)
                    + DBOpenHelper.Urls.uriRequeteCommandes
                    + "depotreference/"
                    + URLEncoder.encode(depot.getDepot_Reference(), "utf-8");

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(creerRequeteApi(url, viewModel, this::onApiTerminee, this::onApiErreur));
        } catch (Throwable e) {
            Log.e(TAG, "Erreur encodage URL", e);
        }
    }

    private void chargerDepuisBdd() {
        List<Commande> commandes = CommandeOpenHelper.getAllCommandesPUI(db, depot.getDepot_Reference());
        if (commandes.isEmpty()) {
            if (connexionDirecte) {
                startActivity(buildRetourConnexionDirecteIntent());
                finish();
            } else {
                connexionNecessaire();
            }
            return;
        }
        passageParOnCreate = false;
        if (connexionDirecte) {
            connexionDirecte = false;
            lancerScan();
        }
        viewModel.setCommandes(commandes);
        invalidateOptionsMenu();
    }

    // ────────────────────────────────────────────────────────────────────────
    // Callbacks API
    // ────────────────────────────────────────────────────────────────────────

    private void onApiTerminee() {
        passageParOnCreate = false;
        invalidateOptionsMenu();
        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
    }

    private void onApiErreur(String message) {
        fr.alcyons.phiwms_mobile.Outils.Alerte.afficherAlerteInformation(
                this, getLayoutInflater(), "Erreur", message, false, true);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Scan
    // ────────────────────────────────────────────────────────────────────────

    private void enregistrerScanLauncher() {
        resultScanDocument = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    onAvantRafraichissement();

                    if (result.getResultCode() != CodesEchangesActivites.RESULT_OK
                            || result.getData() == null) {
                        rafraichirOuTerminer(null);
                        return;
                    }
                    String code = result.getData().getStringExtra("numeroDocument");
                    rafraichirOuTerminer(code);
                    invalidateOptionsMenu();
                });
    }

    /**
     * Tente d'ouvrir directement la commande scannée,
     * sinon rafraîchit la liste (ou redirige si vide).
     */
    private void rafraichirOuTerminer(String code) {
        if (code != null) {
            Commande trouvee = CommandeOpenHelper.getCommandeByNumero(db, code);
            if (trouvee != null) {
                ouvrirDetailReception(trouvee);
                return;
            }
            if (!code.isEmpty()) {
                afficherSnackBarDocumentInconnu();
            }
        }
        rafraichirAdapter();
        if (viewModel.estVide()) {
            vide = true;
            nomServiceVide = getNomService();
            NavigationHelper.allerVersNavigation(this, utilisateurConnecte.getId());
            finish();
        }
    }

    public void lancerScan() {
        Bundle bundle = super.getBundle();
        bundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        bundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent intent = buildScanIntent(bundle);
        intent.putExtras(bundle);
        resultScanDocument.launch(intent);
    }

    @NonNull
    private Intent buildScanIntent(Bundle bundle) {
        boolean isZebraOuHoneywell = Build.MANUFACTURER.contains("Zebra Technologies")
                || Build.MANUFACTURER.toLowerCase().contains("honeywell")
                || Build.MANUFACTURER.toLowerCase().contains("google");

        if (isZebraOuHoneywell || !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            bundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            bundle.putString("TextBannerManuel", "Scannez le datamatrix d'une réception");
            bundle.putString("Context", "Reception");
            return new Intent(this, ScannerDocumentActivity.class);
        } else {
            bundle.putBoolean("modeRafale", false);
            return new Intent(this, ScannerPhotoIdentificationDocument.class);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // UI
    // ────────────────────────────────────────────────────────────────────────

    private void configurerListView() {
        commandeListView.setOnItemClickListener((parent, view, position, id) -> {
            Commande commande = (Commande) commandeAdapter.getItem(position);
            if (commande == null) return;
            ouvrirDetailReception(commande);
        });
    }

    private void ouvrirDetailReception(Commande commande) {
        Intent intent = new Intent(this, DetailReception_V2.class);
        Bundle bundle = super.getBundle();
        bundle.putInt("commandeID_Selectionne", commande.getID_commande());
        enrichirBundleDetail(bundle, commande);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void rafraichirAdapter() {
        List<Commande> liste = viewModel.getCommandesVisibles().getValue();
        if (liste == null) return;
        commandeAdapter = new ReceptionAdapter(this, db, liste);
        commandeListView.setAdapter(commandeAdapter);
    }

    private void initialiserAutoComplete(List<String> fournisseurs) {
        autoComplete = findViewById(R.id.listeFiltre);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_depot, fournisseurs);
        autoComplete.setAdapter(adapter);
        autoComplete.setThreshold(100);

        if (!fournisseurs.isEmpty()) {
            autoComplete.setText(fournisseurs.get(0), false);
        }

        int hauteurEcran = getResources().getDisplayMetrics().heightPixels;
        autoComplete.setDropDownHeight(hauteurEcran / 3);
        int dpToPx = (int) (12 * getResources().getDisplayMetrics().density);
        autoComplete.post(() ->
                autoComplete.setDropDownWidth(
                        findViewById(R.id.listeFiltre_LL).getWidth() - dpToPx));
        autoComplete.setDropDownBackgroundResource(android.R.color.white);

        autoComplete.setOnClickListener(v -> autoComplete.showDropDown());
        findViewById(R.id.chevronFiltre).setOnClickListener(v -> autoComplete.showDropDown());

        autoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String choix = fournisseurs.get(position);
            autoComplete.setText(choix, false);
            autoComplete.dismissDropDown();
            viewModel.filtrerParFournisseur(choix);
        });
    }

    private void configurerRetourArriere() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavigationHelper.allerVersNavigation(
                        BaseReceptionActivity.this, utilisateurConnecte.getId());
                finish();
            }
        });
    }

    // ────────────────────────────────────────────────────────────────────────
    // Menu
    // ────────────────────────────────────────────────────────────────────────

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, commandeAdapter, null, "Produit, Numéro, Fournisseur");
        menu.findItem(R.id.menuDatamatrix).setOnMenuItemClickListener(item -> {
            lancerScan();
            return true;
        });
        return true;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Utilitaires
    // ────────────────────────────────────────────────────────────────────────

    public void afficherSnackBarDocumentInconnu() {
        @SuppressLint("RestrictedApi")
        Snackbar snackbar = Snackbar.make(
                getWindow().getDecorView().findViewById(android.R.id.content),
                Html.fromHtml("<b>Document scanné inconnu</b>", 0),
                Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        ((TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text))
                .setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }

    @NonNull
    private Intent buildRetourConnexionDirecteIntent() {
        Intent intent = new Intent(this, ServiceConnexionDirecteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        bundle.putBoolean("snackBar", true);
        bundle.putString("nomService", "Commande");
        intent.putExtras(bundle);
        return intent;
    }
}