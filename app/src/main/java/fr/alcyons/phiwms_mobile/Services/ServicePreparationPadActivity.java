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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPhotoIdentificationDocument;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Preparation_PreparationAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.DetailPreparationActivity;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.DetailPreparationV2;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServicePreparationPadActivity extends ServiceAvecConnexionActivity {

    Context context;
    List<PH_Preparation> ph_preparation_List;
    ListView ph_preparation_ListView;
    PH_Preparation_PreparationAdapter ph_preparation_preparationAdapter;
    PackageManager pm;
    boolean connexionDirecte;
    List<String> listeDepotLivraison;
    ArrayAdapter<String> autoCompleteAdapter;
    AutoCompleteTextView autoComplete;
    List<PH_Preparation> ph_preparation_List_base;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        pm = ServicePreparationPadActivity.this.getPackageManager();
        context = ServicePreparationPadActivity.this;

        ph_preparation_ListView = (ListView) findViewById(R.id.listeView);
        ph_preparation_ListView.setOnItemClickListener((parent, view, position, id) -> {
            PH_Preparation ph_preparation_Selectionne = (PH_Preparation) ph_preparation_preparationAdapter.getItem(position);

            Intent servicePreparationPad_Intent = new Intent(ServicePreparationPadActivity.this, DetailPreparationV2.class);
            Bundle servicePreparationPad_Bundle = ServicePreparationPadActivity.super.getBundle();
            assert ph_preparation_Selectionne != null;
            servicePreparationPad_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
            servicePreparationPad_Bundle.putString("genre", "PAD");
            servicePreparationPad_Intent.putExtras(servicePreparationPad_Bundle);
            ServicePreparationPadActivity.this.startActivity(servicePreparationPad_Intent);
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServicePreparationPadActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServicePreparationPadActivity.this.startActivity(intent);
                ServicePreparationPadActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ph_preparation_List = new ArrayList<>();
        ph_preparation_List_base = new ArrayList<>();
        listeDepotLivraison = new ArrayList<>();
        listeDepotLivraison.add("Tous");

        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServicePreparationPadActivity.this, LayoutInflater.from(ServicePreparationPadActivity.this));
            }
            RequestQueue requestQueue = Volley.newRequestQueue(ServicePreparationPadActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePreparationPAD;
            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueue.add(obreq);
        } else {
            ph_preparation_List = PH_PreparationOpenHelper.getAllPHPreparationPreparationPAD(db);
            ph_preparation_List_base = PH_PreparationOpenHelper.getAllPHPreparationPreparationPAD(db);

            if (ph_preparation_List.isEmpty()) {
                if (connexionDirecte) {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
                    ServicePreparationPadActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServicePreparationPadActivity.this.finish();
                } else {
                    connexionNecessaire();
                    return;
                }
            } else {
                passageParOnCreate = false;
                if (connexionDirecte) {
                    gestionAdapter();
                    invalidateOptionsMenu();
                    connexionDirecte = !connexionDirecte;
                } else {
                    gestionAdapter();
                }
            }

            initialiserAutoComplete();
            invalidateOptionsMenu();
        }
    }

    private void initialiserAutoComplete() {
        autoComplete = findViewById(R.id.listeFiltre);

        listeDepotLivraison.sort(String::compareTo);

        autoCompleteAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_depot, listeDepotLivraison);
        autoComplete.setAdapter(autoCompleteAdapter);
        autoComplete.setThreshold(100);

        if (!listeDepotLivraison.isEmpty()) {
            autoComplete.setText(listeDepotLivraison.get(0), false);
        }

        int hauteurEcran = getResources().getDisplayMetrics().heightPixels;
        autoComplete.setDropDownHeight(hauteurEcran / 3);
        autoComplete.setDropDownBackgroundResource(android.R.color.white);

        autoComplete.post(() -> {
            int dpToPx = (int) (12 * getResources().getDisplayMetrics().density);
            autoComplete.setDropDownWidth(findViewById(R.id.listeFiltre_LL).getWidth() - dpToPx);
        });

        autoComplete.setOnClickListener(v -> autoComplete.showDropDown());

        findViewById(R.id.chevronFiltre).setOnClickListener(v -> autoComplete.showDropDown());

        autoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String depot = listeDepotLivraison.get(position);
            autoComplete.setText(depot, false);
            autoComplete.dismissDropDown();

            ph_preparation_List = new ArrayList<>();

            if (depot.contentEquals("Tous")) {
                ph_preparation_List.addAll(ph_preparation_List_base);
            } else {
                for (PH_Preparation preparation_courant : ph_preparation_List_base) {
                    Depot depotCourant = DepotOpenHelper.getDepotParReference(db, preparation_courant.getDepotDestinataireReference());
                    if (depotCourant != null && depotCourant.getNom().contentEquals(depot)) {
                        ph_preparation_List.add(preparation_courant);
                    }
                }
            }

            gestionAdapter();
        });
    }

    @NonNull
    private Intent getRetourVersServiceConnexionDirectIntent() {
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServicePreparationPadActivity.this, ServiceConnexionDirecteActivity.class);
        Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", "Préparation");
        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
        return retourVersServiceConnexionDirectIntent;
    }

    @NonNull
    private JsonObjectRequest getJsonObjectRequest(String urlRequete) {
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                response -> {
                    try {
                        int nbResultat = response.getInt("resultCount");
                        if (nbResultat == 0) {
                            String erreur = response.getString("erreur");
                            if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerteInformation(ServicePreparationPadActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est invalide, veuillez vous reconnecter", false, true);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(ServicePreparationPadActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est expirée, veuillez vous reconnecter", false, true);
                            } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                Alerte.afficherAlerteInformation(ServicePreparationPadActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Préparation PAD)", false, true);
                            } else {
                                arreterSpinner();
                                Alerte.afficherAlerteInformation(ServicePreparationPadActivity.this, getLayoutInflater(), "Information", "Aucune préparation PAD à traiter", false, true);
                            }
                        } else {
                            JSONArray ph_preparations_JSONArray = response.getJSONArray("PH_Preparations");
                            viderTablesConcernees();

                            long rowID;
                            for (int i = 0; i < ph_preparations_JSONArray.length(); i++) {
                                JSONObject ph_preparation_JSONObject = ph_preparations_JSONArray.getJSONObject(i);
                                PH_Preparation ph_preparation = new PH_Preparation(ph_preparation_JSONObject);

                                rowID = PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);
                                if (rowID != -1) {
                                    Depot depotDestinataire = DepotOpenHelper.getDepotParReference(db, ph_preparation.getDepotDestinataireReference());
                                    if (depotDestinataire != null) {
                                        if (!listeDepotLivraison.contains(depotDestinataire.getNom()))
                                            listeDepotLivraison.add(depotDestinataire.getNom());
                                    }
                                    ph_preparation_List.add(ph_preparation);
                                    ph_preparation_List_base.add(ph_preparation);

                                    JSONArray ph_preparationLigne_JSONArray = ph_preparation_JSONObject.getJSONArray("ph_preparation_lignes");
                                    for (int k = 0; k < ph_preparationLigne_JSONArray.length(); k++) {
                                        PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, new PH_Preparation_Ligne(ph_preparationLigne_JSONArray.getJSONObject(k)));
                                    }
                                }
                            }

                            PH_Preparation preparation_alcyons = PH_PreparationOpenHelper.getPreparationEssaiAlcyons(db);
                            if (preparation_alcyons != null) {
                                ph_preparation_List.add(preparation_alcyons);
                            }

                            gestionAdapter();
                            initialiserAutoComplete();

                            invalidateOptionsMenu();
                            passageParOnCreate = false;
                            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                        }
                    } catch (JSONException e) {
                        Log.e("JSON Exception", Objects.requireNonNull(e.getMessage()));
                    }
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(ServicePreparationPadActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Préparation PAD)", false, true);
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

    public void viderTablesConcernees() {
        for (PH_Preparation ph_preparation : PH_PreparationOpenHelper.getAllPHPreparationPreparationPAD(db)) {
            if (!ph_preparation.getListe().contentEquals("ALCYONS_LISTE")) {
                for (PH_Preparation_Ligne ph_preparation_ligne : PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, ph_preparation)) {
                    PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
                    Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne.getProduitID());
                    Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation.getDepotOrigineReference());

                    if (depot != null && produit != null) {
                        for (Stock_Lot_Emplacement_Light stockLotEmplacement : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)) {
                            Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stockLotEmplacement);
                        }
                    }
                }
                PH_PreparationOpenHelper.supprimerUnPhPreparation(db, ph_preparation);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, ph_preparation_preparationAdapter, null, "Rechercher...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> {
            lancerScan();
            return true;
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

    public void lancerScan() {
        Bundle scanDocumentBundle = ServicePreparationPadActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent;
        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google")) {
            scanDocumentIntent = new Intent(ServicePreparationPadActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
            scanDocumentBundle.putString("Context", "Preparation");
        } else {
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                scanDocumentIntent = new Intent(ServicePreparationPadActivity.this, ScannerPhotoIdentificationDocument.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            } else {
                scanDocumentIntent = new Intent(ServicePreparationPadActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
                scanDocumentBundle.putString("Context", "Preparation");
            }
        }
        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServicePreparationPadActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodesEchangesActivites.RETOUR_DOCUMENT) {
            if (data != null) {
                String code = data.getStringExtra("numeroDocument");
                if (code != null) {
                    int idPreparation = 0;
                    try {
                        idPreparation = Integer.parseInt(code);
                    } catch (NumberFormatException ignored) {
                    }
                    PH_Preparation ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, idPreparation);
                    if (ph_preparation_Selectionne == null) {
                        if (!code.contentEquals("")) {
                            afficherSnackBarPreparationPAD();
                        }
                        gestionAdapter();
                        invalidateOptionsMenu();
                    } else {
                        Intent servicePreparationPad_Intent = getIntent(ph_preparation_Selectionne);
                        ServicePreparationPadActivity.this.startActivity(servicePreparationPad_Intent);
                        ServicePreparationPadActivity.this.finish();
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
    }

    @NonNull
    private Intent getIntent(PH_Preparation ph_preparation_Selectionne) {
        Intent servicePreparationPad_Intent = new Intent(ServicePreparationPadActivity.this, DetailPreparationV2.class);
        Bundle servicePreparationPad_Bundle = ServicePreparationPadActivity.super.getBundle();
        servicePreparationPad_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
        servicePreparationPad_Bundle.putString("genre", "PAD");
        servicePreparationPad_Intent.putExtras(servicePreparationPad_Bundle);
        return servicePreparationPad_Intent;
    }

    public void afficherSnackBarPreparationPAD() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }

    private void gestionAdapter() {
        ph_preparation_List.sort(Comparator.comparing(PH_Preparation::getLivraisonPrevueDate));
        ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(ServicePreparationPadActivity.this, ph_preparation_List, db, utilisateurConnecte);
        ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);

        if (ph_preparation_List.isEmpty()) {
            vide = true;
            nomServiceVide = "Préparation PAD";
            ServicePreparationPadActivity.this.finish();
        }
    }
}