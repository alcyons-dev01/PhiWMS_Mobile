package fr.alcyons.phiwms_mobile.Services;

import static com.google.android.gms.vision.L.TAG;

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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ReceptionAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.Reception.DetailReception_V2;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceReceptionPadActivity extends ServiceAvecConnexionActivity {

    Depot depotPUIPAD;
    ListView commandeListView;
    ReceptionAdapter commandeReceptionPADAdapter;
    JSONArray commandeJSONArray;
    JSONArray phReliquatJSONArray;
    List<Commande> commandeList;
    List<Commande> commandeListBase;
    PackageManager pm;
    ActivityResultLauncher<Intent> resultScanDocument;
    boolean connexionDirecte;

    ArrayAdapter<String> autoCompleteAdapter;
    AutoCompleteTextView autoComplete;
    List<String> listeFournisseurReception;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        pm = ServiceReceptionPadActivity.this.getPackageManager();
        depotPUIPAD = DepotOpenHelper.getDepotPUIPAD(db);

        commandeListView = (ListView) findViewById(R.id.listeView);
        commandeListView.setOnItemClickListener((parent, view, position, id) -> {
            Commande commandeSelectionne = (Commande) commandeReceptionPADAdapter.getItem(position);

            if (commandeSelectionne != null) {
                Intent serviceReceptionPui_Intent = new Intent(ServiceReceptionPadActivity.this, DetailReception_V2.class);
                Bundle serviceReceptionPui_Bundle = ServiceReceptionPadActivity.super.getBundle();
                serviceReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                serviceReceptionPui_Bundle.putInt("serviceSelectionneID", serviceActuel.getId());
                serviceReceptionPui_Intent.putExtras(serviceReceptionPui_Bundle);
                ServiceReceptionPadActivity.this.startActivity(serviceReceptionPui_Intent);
                ServiceReceptionPadActivity.this.finish();
            }
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        resultScanDocument = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == CodesEchangesActivites.RESULT_OK) {
                        if (data != null) {
                            String code = Objects.requireNonNull(data.getExtras()).getString("code");
                            if (code != null) {
                                Commande commandeSelectionne = CommandeOpenHelper.getCommandeByNumero(db, code);
                                if (commandeSelectionne == null) {
                                    if (!code.contentEquals("")) {
                                        afficherSnackBarPreparationReceptionPAD();
                                    }

                                    Commande commande_essai = CommandeOpenHelper.getCommandeTestAlcyons(db);
                                    if (commande_essai != null) {
                                        commandeList.add(commande_essai);
                                    }

                                    gestionAdapter();

                                    if (commandeList.isEmpty()) {
                                        vide = true;
                                        nomServiceVide = "Réception PAD";
                                        Intent intent = new Intent(ServiceReceptionPadActivity.this, NavigationActivity.class);
                                        Bundle extras = new Bundle();
                                        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                        intent.putExtras(extras);
                                        ServiceReceptionPadActivity.this.startActivity(intent);
                                        ServiceReceptionPadActivity.this.finish();
                                    }

                                    invalidateOptionsMenu();
                                } else {
                                    Intent serviceReceptionPui_Intent = new Intent(ServiceReceptionPadActivity.this, DetailReception_V2.class);
                                    Bundle serviceReceptionPui_Bundle = ServiceReceptionPadActivity.super.getBundle();
                                    serviceReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                                    serviceReceptionPui_Intent.putExtras(serviceReceptionPui_Bundle);
                                    ServiceReceptionPadActivity.this.startActivity(serviceReceptionPui_Intent);
                                    ServiceReceptionPadActivity.this.finish();
                                }
                            } else {
                                Commande commande_essai = CommandeOpenHelper.getCommandeTestAlcyons(db);
                                if (commande_essai != null) {
                                    commandeList.add(commande_essai);
                                }

                                gestionAdapter();

                                if (commandeList.isEmpty()) {
                                    vide = true;
                                    nomServiceVide = "Réception PAD";
                                    Intent intent = new Intent(ServiceReceptionPadActivity.this, NavigationActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                    intent.putExtras(extras);
                                    ServiceReceptionPadActivity.this.startActivity(intent);
                                    ServiceReceptionPadActivity.this.finish();
                                }

                                invalidateOptionsMenu();
                            }
                        } else {
                            gestionAdapter();

                            if (commandeList.isEmpty()) {
                                vide = true;
                                nomServiceVide = "Réception PAD";
                                Intent intent = new Intent(ServiceReceptionPadActivity.this, NavigationActivity.class);
                                Bundle extras = new Bundle();
                                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                intent.putExtras(extras);
                                ServiceReceptionPadActivity.this.startActivity(intent);
                                ServiceReceptionPadActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                    }
                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceReceptionPadActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceReceptionPadActivity.this.startActivity(intent);
                ServiceReceptionPadActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        commandeList = new ArrayList<>();
        commandeListBase = new ArrayList<>();
        listeFournisseurReception = new ArrayList<>();
        listeFournisseurReception.add("Tous les fournisseurs");

        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceReceptionPadActivity.this, LayoutInflater.from(ServiceReceptionPadActivity.this));
            }

            RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ServiceReceptionPadActivity.this);
            String urlRequete = null;
            try {
                urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteCommandes + "depotreference/" + URLEncoder.encode(depotPUIPAD.getDepot_Reference(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JsonObjectRequest obreq = getObjectRequest(urlRequete);
            requestQueueDestructionUtilisateur.add(obreq);
        } else {
            commandeList = CommandeOpenHelper.getAllCommandesPUI(db, depotPUIPAD.getDepot_Reference());
            commandeListBase = new ArrayList<>(commandeList);

            if (commandeList.isEmpty()) {
                if (connexionDirecte) {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceReceptionPadActivity.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Commande");
                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
                    ServiceReceptionPadActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceReceptionPadActivity.this.finish();
                } else {
                    connexionNecessaire();
                    return;
                }
            } else {
                passageParOnCreate = false;
                if (connexionDirecte) {
                    lancerScan();
                    connexionDirecte = !connexionDirecte;
                }
            }

            // Alimente la liste des fournisseurs
            for (Commande commande : commandeListBase) {
                if (!listeFournisseurReception.contains(commande.getFournisseur())) {
                    listeFournisseurReception.add(commande.getFournisseur());
                }
            }

            initialiserAutoComplete();
            gestionAdapter();
            invalidateOptionsMenu();
        }
    }

    private void initialiserAutoComplete() {
        autoComplete = findViewById(R.id.listeFiltre);

        // Trie en gardant "Tous les fournisseurs" en tête
        String premierElement = listeFournisseurReception.get(0);
        List<String> sansPremiereEntree = listeFournisseurReception.subList(1, listeFournisseurReception.size());
        Collections.sort(sansPremiereEntree);
        listeFournisseurReception = new ArrayList<>();
        listeFournisseurReception.add(premierElement);
        listeFournisseurReception.addAll(sansPremiereEntree);

        autoCompleteAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_depot, listeFournisseurReception);
        autoComplete.setAdapter(autoCompleteAdapter);
        autoComplete.setThreshold(100);

        if (!listeFournisseurReception.isEmpty()) {
            autoComplete.setText(listeFournisseurReception.get(0), false);
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
            String fournisseur = listeFournisseurReception.get(position);
            autoComplete.setText(fournisseur, false);
            autoComplete.dismissDropDown();

            commandeList = new ArrayList<>();

            if (fournisseur.contentEquals("Tous les fournisseurs")) {
                commandeList.addAll(commandeListBase);
            } else {
                for (Commande commande_courant : commandeListBase) {
                    if (commande_courant.getFournisseur().contentEquals(fournisseur)) {
                        commandeList.add(commande_courant);
                    }
                }
            }

            gestionAdapter();
        });
    }

    private void gestionAdapter() {

        commandeList.sort(Comparator.comparing(Commande::getNumero));

        commandeReceptionPADAdapter = new ReceptionAdapter(ServiceReceptionPadActivity.this, db, commandeList);
        commandeListView.setDivider(footer);
        commandeListView.setAdapter(commandeReceptionPADAdapter);

        if (commandeList.isEmpty()) {
            vide = true;
            nomServiceVide = "Réception PAD";
            ServiceReceptionPadActivity.this.finish();
        }
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    private JsonObjectRequest getObjectRequest(String urlRequete) {
        return new JsonObjectRequest(Request.Method.GET, urlRequete, null, response -> {
            try {
                int resultCount = response.getInt("resultCount");
                if (resultCount == 0) {
                    String erreur = response.getString("erreur");
                    if (erreur.equals(getString(R.string.tokenInvalide))) {
                        Alerte.afficherAlerteInformation(ServiceReceptionPadActivity.this, getLayoutInflater(), "Alerte", "Votre session est invalide, veuillez vous reconnecter.", false, true);
                    } else if (erreur.equals(getString(R.string.tokenExpire))) {
                        Alerte.afficherAlerteInformation(ServiceReceptionPadActivity.this, getLayoutInflater(), "Alerte", "Votre session a expirée, veuillez vous reconnecter.", false, true);
                    } else {
                        arreterSpinner();
                        Alerte.afficherAlerteInformation(ServiceReceptionPadActivity.this, getLayoutInflater(), "Erreur", "Aucune réception PAD à traiter", false, true);
                    }
                } else {
                    commandeJSONArray = response.getJSONArray("PH_Commandes");
                    viderTablesConcernees();

                    for (int i = 0; i < commandeJSONArray.length(); i++) {
                        JSONObject commandeJSONObject = commandeJSONArray.getJSONObject(i);
                        Commande commandeCourant = new Commande(commandeJSONObject);

                        // Alimente la liste des fournisseurs
                        if (!listeFournisseurReception.contains(commandeCourant.getFournisseur())) {
                            listeFournisseurReception.add(commandeCourant.getFournisseur());
                        }

                        phReliquatJSONArray = commandeJSONObject.getJSONArray("ph_reliquat");
                        boolean phReliquatPresent = false;

                        for (int j = 0; j < phReliquatJSONArray.length(); j++) {
                            PH_Reliquat reliquatCourant = new PH_Reliquat((phReliquatJSONArray.getJSONObject(j)));
                            long phReliquatPHiMR4ID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, reliquatCourant);
                            if (phReliquatPHiMR4ID != -1) {
                                phReliquatPresent = true;
                            }
                        }

                        if (phReliquatPresent) {
                            long rowID = CommandeOpenHelper.insererUneCommandeEnBDD(db, commandeCourant);
                            if (rowID != -1) {
                                if (commandeCourant.getRef_Depot_Dest().contains("-PAD")) {
                                    commandeList.add(commandeCourant);
                                    commandeListBase.add(commandeCourant);
                                }
                            }
                        }
                    }

                    if (commandeList.isEmpty()) {
                        vide = true;
                        nomServiceVide = "Réception PAD";
                        retourNavigation();
                    }

                    initialiserAutoComplete();
                    gestionAdapter();

                    passageParOnCreate = false;
                    new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                    invalidateOptionsMenu();
                }
            } catch (Throwable t) {
                Log.e(TAG, "Error JSON", t);
            }
        }, error -> {
            Log.e("Volley", "Error");
            Alerte.afficherAlerteInformation(ServiceReceptionPadActivity.this, getLayoutInflater(), "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP Service Récupération Quarantaine", false, true);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                return headers;
            }
        };
    }

    public void viderTablesConcernees() {
        for (Commande commande : CommandeOpenHelper.getAllCommandes(db)) {
            if (!commande.getNumero().contentEquals("RECALCYONS01")) {
                for (PH_Reliquat ph_reliquat : PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, commande.getNumero())) {
                    PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, ph_reliquat);
                }
                CommandeOpenHelper.supprimerUneCommande(db, commande);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, commandeReceptionPADAdapter, null, "Rechercher...");
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
        Bundle scanDocumentBundle = ServiceReceptionPadActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent scanDocumentIntent;
        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell")) {
            scanDocumentIntent = new Intent(ServiceReceptionPadActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une réception");
            scanDocumentBundle.putString("Context", "Reception");
        } else {
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                scanDocumentIntent = new Intent(ServiceReceptionPadActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            } else {
                scanDocumentIntent = new Intent(ServiceReceptionPadActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une réception");
                scanDocumentBundle.putString("Context", "Reception");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        resultScanDocument.launch(scanDocumentIntent);
    }

    public void afficherSnackBarPreparationReceptionPAD() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}