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
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPhotoIdentificationDocument;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Preparation_PreparationAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Helper.NavigationHelper;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.DetailPreparationV2;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

/**
 * Classe abstraite commune à ServicePreparationPadActivity et ServicePreparationPufActivity.
 */
public abstract class BasePreparationActivity extends ServiceAvecConnexionActivity {

    private static final String TAG = "BasePreparationActivity";

    // ── UI ──────────────────────────────────────────────────────────────────
    protected ListView ph_preparation_ListView;
    private AutoCompleteTextView autoComplete;
    private PH_Preparation_PreparationAdapter ph_preparation_preparationAdapter;

    // ── Données ─────────────────────────────────────────────────────────────
    protected List<PH_Preparation> ph_preparation_List;
    protected List<PH_Preparation> ph_preparation_List_base;
    protected List<String> listeDepotLivraison;
    protected boolean connexionDirecte;
    private PackageManager pm;

    // ────────────────────────────────────────────────────────────────────────
    // Contrat des sous-classes
    // ────────────────────────────────────────────────────────────────────────

    /** URL de l'endpoint API spécifique au service. */
    protected abstract String getUrlRequete();

    /** Nom du service affiché dans les messages (ex. "Préparation PAD"). */
    protected abstract String getNomService();

    /** Genre passé dans le bundle vers DetailPreparationV2 (ex. "PAD" ou "PUF"). */
    protected abstract String getGenre();

    /** Label du premier élément du filtre dépôt (ex. "Tous" ou "Tous les dépôts"). */
    protected abstract String getLabelTousDepots();

    /** Charge toutes les préparations depuis la BDD locale. */
    protected abstract List<PH_Preparation> chargerDepuisBdd();

    /** Vide les tables avant de persister les données de l'API. */
    protected abstract void viderTablesConcernees();

    /** Tri appliqué à la liste avant affichage dans l'adapter. */
    protected abstract void trierListe(List<PH_Preparation> liste);

    /**
     * Hook appelé après la persistance API, avant gestionAdapter().
     * Permet à PAD d'injecter getPreparationEssaiAlcyons().
     */
    protected void onApresChargementApi() {
        // Par défaut : rien. Surchargé par PAD.
    }

    // ────────────────────────────────────────────────────────────────────────
    // Cycle de vie
    // ────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);

        pm = getPackageManager();
        ph_preparation_ListView = findViewById(R.id.listeView);
        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        configurerListView();
        configurerRetourArriere();
    }

    @Override
    public void onResume() {
        super.onResume();

        ph_preparation_List = new ArrayList<>();
        ph_preparation_List_base = new ArrayList<>();
        listeDepotLivraison = new ArrayList<>();
        listeDepotLivraison.add(getLabelTousDepots());

        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            chargerDepuisApi();
        } else {
            chargerDepuisBddEtAfficher();
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Chargement
    // ────────────────────────────────────────────────────────────────────────

    private void chargerDepuisApi() {
        if (!swipeRefreshLayout.isRefreshing()) {
            afficherSpinner(this, LayoutInflater.from(this));
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(creerRequeteApi(getUrlRequete()));
    }

    private void chargerDepuisBddEtAfficher() {
        ph_preparation_List = chargerDepuisBdd();
        ph_preparation_List_base = new ArrayList<>(ph_preparation_List);

        if (ph_preparation_List.isEmpty()) {
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
        }
        gestionAdapter();
        initialiserAutoComplete();
        invalidateOptionsMenu();
    }

    // ────────────────────────────────────────────────────────────────────────
    // Requête API
    // ────────────────────────────────────────────────────────────────────────

    private JsonObjectRequest creerRequeteApi(String urlRequete) {
        JsonObjectRequest obreq = new JsonObjectRequest(
                com.android.volley.Request.Method.GET, urlRequete, null,
                response -> {
                    try {
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            traiterErreurApi(response);
                        } else {
                            persisterEtAfficher(response);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                },
                error -> {
                    Log.e(TAG, "Erreur réseau", error);
                    Alerte.afficherAlerteInformation(this, getLayoutInflater(), "Erreur",
                            "Veuillez contacter la société Alcyons ! \n Référence : " + getNomService(),
                            false, true);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                return headers;
            }
        };
        obreq.setRetryPolicy(retryPolicy);
        return obreq;
    }

    private void traiterErreurApi(JSONObject response) throws JSONException {
        String erreur = response.getString("erreur");
        if (erreur.equals(getString(R.string.tokenInvalide))) {
            Alerte.afficherAlerteInformation(this, getLayoutInflater(), "Erreur",
                    "Votre session de connexion est invalide, veuillez vous reconnecter", false, true);
        } else if (erreur.equals(getString(R.string.tokenExpire))) {
            Alerte.afficherAlerteInformation(this, getLayoutInflater(), "Erreur",
                    "Votre session de connexion est expirée, veuillez vous reconnecter", false, true);
        } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
            Alerte.afficherAlerteInformation(this, getLayoutInflater(), "Erreur",
                    "Veuillez contacter la société Alcyons ! \n Référence : " + getNomService(),
                    false, true);
        } else {
            arreterSpinner();
            Alerte.afficherAlerteInformation(this, getLayoutInflater(), "Information",
                    "Aucune préparation " + getNomService() + " à traiter", false, true);
        }
    }

    private void persisterEtAfficher(JSONObject response) throws JSONException {
        JSONArray ph_preparation_JSONArray = response.getJSONArray("PH_Preparations");
        viderTablesConcernees();

        List<String> tempListeDepot = new ArrayList<>();

        for (int i = 0; i < ph_preparation_JSONArray.length(); i++) {
            JSONObject ph_preparation_JSONObject = ph_preparation_JSONArray.getJSONObject(i);
            PH_Preparation ph_preparation = new PH_Preparation(ph_preparation_JSONObject);

            long rowID = PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);
            if (rowID != -1) {
                Depot depotDestinataire = DepotOpenHelper.getDepotParReference(
                        db, ph_preparation.getDepotDestinataireReference());
                if (depotDestinataire != null
                        && !tempListeDepot.contains(depotDestinataire.getNom())) {
                    tempListeDepot.add(depotDestinataire.getNom());
                }

                ph_preparation_List.add(ph_preparation);
                ph_preparation_List_base.add(ph_preparation);

                JSONArray lignesArray = ph_preparation_JSONObject.getJSONArray("ph_preparation_lignes");
                for (int k = 0; k < lignesArray.length(); k++) {
                    PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(
                            db, new PH_Preparation_Ligne(lignesArray.getJSONObject(k)));
                }
            }
        }

        Collections.sort(tempListeDepot);
        listeDepotLivraison.addAll(tempListeDepot);

        // Hook : PAD y ajoute getPreparationEssaiAlcyons()
        onApresChargementApi();

        gestionAdapter();
        initialiserAutoComplete();
        invalidateOptionsMenu();

        passageParOnCreate = false;
        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
    }

    // ────────────────────────────────────────────────────────────────────────
    // UI
    // ────────────────────────────────────────────────────────────────────────

    private void configurerListView() {
        ph_preparation_ListView.setOnItemClickListener((parent, view, position, id) -> {
            PH_Preparation selectionne = (PH_Preparation) ph_preparation_preparationAdapter.getItem(position);
            if (selectionne == null) return;
            ouvrirDetailPreparation(selectionne);
        });
    }

    private void ouvrirDetailPreparation(PH_Preparation preparation) {
        Intent intent = new Intent(this, DetailPreparationV2.class);
        Bundle bundle = super.getBundle();
        bundle.putInt("ph_preparationUID_Selectionne", preparation.getUID());
        bundle.putString("genre", getGenre());
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void gestionAdapter() {
        trierListe(ph_preparation_List);
        ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(
                this, ph_preparation_List, db, utilisateurConnecte);
        ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);

        if (ph_preparation_List.isEmpty()) {
            vide = true;
            nomServiceVide = getNomService();
            finish();
        }
    }

    private void initialiserAutoComplete() {
        autoComplete = findViewById(R.id.listeFiltre);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item_depot, listeDepotLivraison);
        autoComplete.setAdapter(adapter);
        autoComplete.setThreshold(100);

        if (!listeDepotLivraison.isEmpty()) {
            autoComplete.setText(listeDepotLivraison.get(0), false);
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
            String depot = listeDepotLivraison.get(position);
            autoComplete.setText(depot, false);
            autoComplete.dismissDropDown();

            ph_preparation_List = new ArrayList<>();

            if (depot.contentEquals(getLabelTousDepots())) {
                ph_preparation_List.addAll(ph_preparation_List_base);
            } else {
                for (PH_Preparation preparation : ph_preparation_List_base) {
                    Depot depotCourant = DepotOpenHelper.getDepotParReference(
                            db, preparation.getDepotDestinataireReference());
                    if (depotCourant != null && depotCourant.getNom().contentEquals(depot)) {
                        ph_preparation_List.add(preparation);
                    }
                }
            }

            gestionAdapter();
        });
    }

    private void configurerRetourArriere() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavigationHelper.allerVersNavigation(
                        BasePreparationActivity.this, utilisateurConnecte.getId());
                finish();
            }
        });
    }

    // ────────────────────────────────────────────────────────────────────────
    // Scan
    // ────────────────────────────────────────────────────────────────────────

    public void lancerScan() {
        Bundle bundle = super.getBundle();
        bundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        bundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent intent = buildScanIntent(bundle);
        intent.putExtras(bundle);
        startActivityForResult(intent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }

    @NonNull
    private Intent buildScanIntent(Bundle bundle) {
        boolean isZebraOuHoneywell = Build.MANUFACTURER.contains("Zebra Technologies")
                || Build.MANUFACTURER.toLowerCase().contains("honeywell")
                || Build.MANUFACTURER.toLowerCase().contains("google");

        if (isZebraOuHoneywell || !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            bundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            bundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
            bundle.putString("Context", "Preparation");
            return new Intent(this, ScannerDocumentActivity.class);
        } else {
            bundle.putBoolean("modeRafale", false);
            return new Intent(this, ScannerPhotoIdentificationDocument.class);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != CodesEchangesActivites.RETOUR_DOCUMENT) return;

        if (data != null) {
            String code = data.getStringExtra("numeroDocument");
            if (code != null) {
                int idPreparation = 0;
                try {
                    idPreparation = Integer.parseInt(code);
                } catch (NumberFormatException ignored) {
                }
                PH_Preparation selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, idPreparation);
                if (selectionne == null) {
                    if (!code.contentEquals("")) {
                        afficherSnackBarDocumentInconnu();
                    }
                    gestionAdapter();
                    invalidateOptionsMenu();
                } else {
                    ouvrirDetailPreparation(selectionne);
                }
            } else {
                gestionAdapter();
                invalidateOptionsMenu();
            }
        } else {
            gestionAdapter();
            invalidateOptionsMenu();
        }
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
        super.prepareOptionsMenu(menu, ph_preparation_preparationAdapter, null, "Rechercher...");
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
        bundle.putString("nomService", "Préparation");
        intent.putExtras(bundle);
        return intent;
    }
}