package fr.alcyons.phiwms_mobile.Inventaire;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Inventaire;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.InventaireAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.InventaireZoneAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceInventaireGeneralActivity;

public class InventaireZoneActivity extends ServiceAvecConnexionActivity {
    Context context;
    PackageManager pm;
    List<JSONObject> listeZoneInventaire;
    List<JSONObject> listeZoneInventaireBase;
    ListView inventaireListView;
    InventaireZoneAdapter inventaireZoneAdapter;
    boolean connexionDirecte;
    List<String> listeDepotInventaire;
    List<Inventaire_Ligne_Temp> inventaireLigneTempListBase;
    Inventaire inventaireCourant;
    Depot depotSelectionne;

    ArrayAdapter<String> autoCompleteAdapter;
    AutoCompleteTextView autoComplete;
    ArrayList<String> listeZoneNom;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        pm = InventaireZoneActivity.this.getPackageManager();
        context = InventaireZoneActivity.this;

        // Gestion de la listView
        inventaireListView = (ListView) findViewById(R.id.listeView);
        inventaireListView.setOnItemClickListener((parent, view, position, id) -> {
            JSONObject inventaire_Selectionne = (JSONObject) inventaireZoneAdapter.getItem(position);

            Intent serviceInventaire_Intent = new Intent(InventaireZoneActivity.this, DetailInventaire_V3.class);
            Bundle serviceInventaire_Bundle = InventaireZoneActivity.super.getBundle();
            serviceInventaire_Bundle.putInt("inventaireId", inventaireCourant.getInventaire_ID());
            serviceInventaire_Bundle.putString("zoneSelectionne", inventaire_Selectionne.optString("zone"));
            serviceInventaire_Bundle.putString("depotSelectionne", depotSelectionne.getDepot_Reference());
            serviceInventaire_Intent.putExtras(serviceInventaire_Bundle);
            InventaireZoneActivity.this.startActivity(serviceInventaire_Intent);
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        depotSelectionne = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotId"));
        inventaireCourant = InventaireOpenHelper.getInventaireById(db, intent.getExtras().getInt("inventaireId"));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(InventaireZoneActivity.this, ServiceInventaireGeneralActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                InventaireZoneActivity.this.startActivity(intent);
                InventaireZoneActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        listeZoneInventaire = new ArrayList<>();
        listeZoneInventaireBase = new ArrayList<>();
        listeZoneNom = new ArrayList<>();
        listeZoneNom.add("Toutes les zones");
        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(InventaireZoneActivity.this, LayoutInflater.from(InventaireZoneActivity.this));
            }
            RequestQueue requestQueue = Volley.newRequestQueue(InventaireZoneActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteInventaireGeneral+"/"+depotSelectionne.getDepot_Reference()+"/"+inventaireCourant.getInventaire_ID()+"/zone";

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueue.add(obreq);
        }
        else
        {
            Inventaire inventaireGeneral = InventaireOpenHelper.getInventaireGeneral(db);

            if (inventaireGeneral == null) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
                    InventaireZoneActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    InventaireZoneActivity.this.finish();
                }
                else
                {
                    connexionNecessaire();
                    return;
                }
            }
            else
            {

            }


            invalidateOptionsMenu();
        }
    }

    @NonNull
    private Intent getRetourVersServiceConnexionDirectIntent() {
        Intent retourVersServiceConnexionDirectIntent = new Intent(InventaireZoneActivity.this, ServiceConnexionDirecteActivity.class);
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
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            String erreur = response.getString("erreur");
                            if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerteInformation(InventaireZoneActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est invalide, veuillez vous reconnecter.", false, true);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(InventaireZoneActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est expirée, veuillez vous reconnecter.", false, true);
                            } else if (!erreur.contentEquals("Aucun Inventaire trouvé")) {
                                Alerte.afficherAlerteInformation(InventaireZoneActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Général", false, true);
                            } else {
                                arreterSpinner();
                                Alerte.afficherAlerteInformation(InventaireZoneActivity.this, getLayoutInflater(), "Information", "Aucun inventaire général à traiter", false, true);
                            }
                        } else {

                            JSONArray arrayZone = response.getJSONArray("Zone");
                            for(int i = 0; i < arrayZone.length(); i++) {
                                JSONObject zoneJsonObject = arrayZone.getJSONObject(i);
                                listeZoneInventaire.add(zoneJsonObject);
                                listeZoneInventaireBase.add(zoneJsonObject);
                                if(!zoneJsonObject.optString("zone").isEmpty())
                                    listeZoneNom.add(zoneJsonObject.optString("zone"));
                            }

                            initialiserAutoComplete();
                            gestionAdapter();

                            if (passageParOnCreate) {
                                invalidateOptionsMenu();
                            }

                            passageParOnCreate = false;
                            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                        }
                    } catch (JSONException e) {
                        Log.e("JSON Exception", Objects.requireNonNull(e.getMessage()));
                    }
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(InventaireZoneActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Général", false, true);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                return headers;
            }
        };
        return obreq;
    }

    public void viderTablesConcernees() {
        for (Inventaire_Ligne_Temp inventaireLigneTemp : Inventaire_Ligne_TempOpenHelper.getAllInventaireLigneTempByInventaire(db, inventaireCourant.getInventaire_ID()))
        {
            Inventaire_Ligne_TempOpenHelper.supprimerInventaireLigneTempEnBDD(db, inventaireLigneTemp);
        }
    }

    public void lancerScan()
    {
        Bundle scanDocumentBundle = InventaireZoneActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent = null;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(InventaireZoneActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
            scanDocumentBundle.putString("Context", "Preparation");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(InventaireZoneActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(InventaireZoneActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
                scanDocumentBundle.putString("Context", "Preparation");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        InventaireZoneActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void gestionAdapter()
    {
        inventaireZoneAdapter = new InventaireZoneAdapter(InventaireZoneActivity.this, db, listeZoneInventaire, utilisateurConnecte, new ArrayList(), inventaireCourant, depotSelectionne);
        inventaireListView.setAdapter(inventaireZoneAdapter);
    }

    private void initialiserAutoComplete() {
        autoComplete = findViewById(R.id.listeFiltre);
        String premierElement = listeZoneNom.get(0);

        // Trie la liste sans le premier élément
        List<String> sansPremiereEntree = listeZoneNom.subList(1, listeZoneNom.size());
        Collections.sort(sansPremiereEntree);

        // Reconstruit la liste avec le premier élément en tête
        listeZoneNom = new ArrayList<>();
        listeZoneNom.add(premierElement);
        listeZoneNom.addAll(sansPremiereEntree);

        autoCompleteAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_depot, listeZoneNom);
        autoComplete.setAdapter(autoCompleteAdapter);
        autoComplete.setThreshold(100); // Empêche le filtrage automatique

        // Affiche le premier élément par défaut
        if (!listeZoneNom.isEmpty()) {
            autoComplete.setText(listeZoneNom.get(0), false);
        }

        // Hauteur = 1/3 de l'écran
        int hauteurEcran = getResources().getDisplayMetrics().heightPixels;
        autoComplete.setDropDownHeight(hauteurEcran / 3);
        int dpToPx = (int) (12 * getResources().getDisplayMetrics().density);
        autoComplete.post(() -> autoComplete.setDropDownWidth(findViewById(R.id.listeFiltre_LL).getWidth() - dpToPx));
        autoComplete.setDropDownBackgroundResource(android.R.color.white);

        // Ouvre la liste au clic
        autoComplete.setOnClickListener(v -> autoComplete.showDropDown());

        // Chevron ouvre aussi la liste
        findViewById(R.id.chevronFiltre).setOnClickListener(v -> autoComplete.showDropDown());

        // Gère la sélection
        autoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String zone = listeZoneNom.get(position);
            autoComplete.setText(zone, false);
            autoComplete.dismissDropDown();

            listeZoneInventaire = new ArrayList<>();

            if (zone.contentEquals("Tous les dépôts")) {
                listeZoneInventaire.addAll(listeZoneInventaireBase);
            } else {
                for (JSONObject objetCourant : listeZoneInventaireBase) {
                    if (objetCourant.optString("zone").contentEquals(zone)) {
                        listeZoneInventaire.add(objetCourant);
                    }
                }
            }

            gestionAdapter();
        });
    }
}