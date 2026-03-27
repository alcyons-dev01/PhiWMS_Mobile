package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Inventaire.DetailInventaireActivity;
import fr.alcyons.phiwms_mobile.Inventaire.DetailInventaire_V2Activity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.InventaireAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceInventaireGeneralActivity extends ServiceAvecConnexionActivity {
    Context context;
    PackageManager pm;
    List<Inventaire_Ligne_Temp> inventaireLigneTempList;
    ListView inventaireListView;
    InventaireAdapter inventaireAdapter;
    boolean connexionDirecte;
    List<String> listeDepotInventaire;
    ArrayAdapter<String> spinnerArrayAdapter;
    Spinner spinner;
    List<Inventaire_Ligne_Temp> inventaireLigneTempListBase;
    Inventaire inventaireCourant;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        pm = ServiceInventaireGeneralActivity.this.getPackageManager();
        context = ServiceInventaireGeneralActivity.this;

        // Gestion de la listView
        inventaireListView = (ListView) findViewById(R.id.listeView);
        inventaireListView.setOnItemClickListener((parent, view, position, id) -> {
            String[] inventaire_Selectionne = (String[]) inventaireAdapter.getItem(position);

            Intent serviceInventaire_Intent = new Intent(ServiceInventaireGeneralActivity.this, DetailInventaire_V2Activity.class);
            Bundle serviceInventaire_Bundle = ServiceInventaireGeneralActivity.super.getBundle();
            serviceInventaire_Bundle.putInt("inventaireId", Integer.parseInt(inventaire_Selectionne[2]));
            serviceInventaire_Bundle.putString("zoneSelectionne", inventaire_Selectionne[0]);
            serviceInventaire_Bundle.putString("depotSelectionne", inventaire_Selectionne[7]);
            serviceInventaire_Intent.putExtras(serviceInventaire_Bundle);
            ServiceInventaireGeneralActivity.this.startActivity(serviceInventaire_Intent);
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceInventaireGeneralActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceInventaireGeneralActivity.this.startActivity(intent);
                ServiceInventaireGeneralActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        inventaireLigneTempList = new ArrayList<>();
        inventaireLigneTempListBase = new ArrayList<>();
        listeDepotInventaire = new ArrayList<>();
        listeDepotInventaire.add("Tous");
        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceInventaireGeneralActivity.this, LayoutInflater.from(ServiceInventaireGeneralActivity.this));
            }
            RequestQueue requestQueue = Volley.newRequestQueue(ServiceInventaireGeneralActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteInventaireGeneral;

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
                    ServiceInventaireGeneralActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceInventaireGeneralActivity.this.finish();
                }
                else
                {
                    connexionNecessaire();
                    return;
                }
            }
            else
            {
                inventaireLigneTempList =Inventaire_Ligne_TempOpenHelper.getInventaireInfoById(db, inventaireGeneral.getInventaire_ID());
                inventaireLigneTempListBase = Inventaire_Ligne_TempOpenHelper.getInventaireInfoById(db, inventaireGeneral.getInventaire_ID());
                passageParOnCreate = false;
                if(connexionDirecte)
                {
                    //lancerScan();
                    /* Code nécessaire à l'affichage de la liste */
                    gestionAdapter();
                    invalidateOptionsMenu();
                    connexionDirecte = !connexionDirecte;
                }
                else
                {
                    gestionAdapter();
                }
            }

            spinner = (Spinner) findViewById(R.id.optionTri);

            invalidateOptionsMenu();
        }
    }

    @NonNull
    private Intent getRetourVersServiceConnexionDirectIntent() {
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceInventaireGeneralActivity.this, ServiceConnexionDirecteActivity.class);
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
                                Alerte.afficherAlerteInformation(ServiceInventaireGeneralActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est invalide, veuillez vous reconnecter.", false, true);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(ServiceInventaireGeneralActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est expirée, veuillez vous reconnecter.", false, true);
                            } else if (!erreur.contentEquals("Aucun Inventaire trouvé")) {
                                Alerte.afficherAlerteInformation(ServiceInventaireGeneralActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Général", false, true);
                            } else {
                                arreterSpinner();
                                Alerte.afficherAlerteInformation(ServiceInventaireGeneralActivity.this, getLayoutInflater(), "Information", "Aucun inventaire général à traiter", false, true);
                            }
                        } else {
                            // 1. Construire une Map depotRef -> Depot une seule fois (évite N requêtes BDD)
                            Map<String, Depot> depotCache = new HashMap<>();

                            JSONArray inventaire_JSONArray = response.getJSONArray("Inventaires");
                            viderTablesConcernees();

                            for (int i = 0; i < inventaire_JSONArray.length(); i++) {
                                JSONObject inventaire_JSONObject = inventaire_JSONArray.getJSONObject(i);
                                inventaireCourant = new Inventaire(inventaire_JSONObject);
                                long rowID = InventaireOpenHelper.insererUnInventaireEnBDD(db, inventaireCourant);

                                if (rowID == -1) continue; // simplifie l'imbrication

                                JSONArray lignesArray = inventaire_JSONObject.getJSONArray("inventaire_ligne_temp");
                                for (int k = 0; k < lignesArray.length(); k++) {
                                    Inventaire_Ligne_Temp ligne = new Inventaire_Ligne_Temp(lignesArray.getJSONObject(k));
                                    Inventaire_Ligne_TempOpenHelper.insererUnInventaire_Ligne_TempEnBDD(db, ligne);

                                    // Mise en cache du dépôt si pas encore chargé
                                    String ref = ligne.getDepotReference();
                                    if (!depotCache.containsKey(ref)) {
                                        Depot depot = DepotOpenHelper.getDepotParReference(db, ref);
                                        if (depot != null) depotCache.put(ref, depot);
                                    }

                                    Depot depot = depotCache.get(ref);
                                    if (depot != null && !listeDepotInventaire.contains(depot.getNom())) {
                                        listeDepotInventaire.add(depot.getNom());
                                    }
                                }
                            }

                            // 2. Charger les lignes une seule fois
                            inventaireLigneTempListBase = Inventaire_Ligne_TempOpenHelper
                                    .getInventaireInfoById(db, inventaireCourant.getInventaire_ID());
                            inventaireLigneTempList = new ArrayList<>(inventaireLigneTempListBase);

                            // 3. Pré-indexer les lignes par nom de dépôt (évite la boucle dans le listener)
                            Map<String, List<Inventaire_Ligne_Temp>> lignesParDepot = new HashMap<>();
                            for (Inventaire_Ligne_Temp ligne : inventaireLigneTempListBase) {
                                Depot depot = depotCache.get(ligne.getDepotReference());
                                if (depot != null) {
                                    lignesParDepot
                                            .computeIfAbsent(depot.getNom(), k -> new ArrayList<>())
                                            .add(ligne);
                                }
                            }

                            if (passageParOnCreate) {
                                gestionAdapter();
                                invalidateOptionsMenu();
                            }

// 4. Setup spinner simplifié
                            spinner = findViewById(R.id.optionTri);
                            spinnerArrayAdapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_dropdown_item, listeDepotInventaire);
                            spinner.setAdapter(spinnerArrayAdapter);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                boolean isFirstSelection = true;

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (isFirstSelection) { isFirstSelection = false; return; }

                                    TextView tv = (TextView) parent.getChildAt(0);
                                    if (tv != null) tv.setVisibility(View.INVISIBLE);

                                    String depot = spinner.getItemAtPosition(position).toString();

                                    // Lecture directe depuis la Map — plus de boucle BDD
                                    inventaireLigneTempList = depot.equals("Tous")
                                            ? new ArrayList<>(inventaireLigneTempListBase)
                                            : lignesParDepot.getOrDefault(depot, new ArrayList<>());

                                    gestionAdapter();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {}
                            });

                            passageParOnCreate = false;
                            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                        }
                    } catch (JSONException e) {
                        Log.e("JSON Exception", Objects.requireNonNull(e.getMessage()));
                    }
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(ServiceInventaireGeneralActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Général", false, true);
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
        for (Inventaire inventaire : InventaireOpenHelper.getAllInventaire(db))
        {
            for (Inventaire_Ligne_Temp inventaireLigneTemp : Inventaire_Ligne_TempOpenHelper.getAllInventaireLigneTempByInventaire(db, inventaire.getInventaire_ID()))
            {
                Inventaire_Ligne_TempOpenHelper.supprimerInventaireLigneTempEnBDD(db, inventaireLigneTemp);
            }
            InventaireOpenHelper.supprimerInventaire(db, inventaire);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, inventaireAdapter, null, "Rechercher...");
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
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);
        return true;
    }
    public void lancerScan()
    {
        Bundle scanDocumentBundle = ServiceInventaireGeneralActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent = null;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(ServiceInventaireGeneralActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
            scanDocumentBundle.putString("Context", "Preparation");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServiceInventaireGeneralActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceInventaireGeneralActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
                scanDocumentBundle.putString("Context", "Preparation");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceInventaireGeneralActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void gestionAdapter()
    {
        View dialogView = ServiceInventaireGeneralActivity.this.getLayoutInflater().inflate(R.layout.progressbar_modale, null);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        TextView tvProgress = dialogView.findViewById(R.id.tvProgress);

        progressBar.setMax(inventaireLigneTempList.size());

        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceInventaireGeneralActivity.this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        List<String[]> listeInventaire = new ArrayList<>();

        inventaireLigneTempList.sort(Comparator.comparing(Inventaire_Ligne_Temp::getDepotReference).thenComparing(Inventaire_Ligne_Temp::getZone));

        new Thread(() -> {
            String zonePrecedente = "";
            String depotPrecedent = "";
            String depotReferencePrecedent = "";
            int nbLigneZone = 0;
            int nbLigneZoneSaisie = 0;
            String[] ligneInventaire = new String[8];

            for (int i = 0; i < inventaireLigneTempList.size(); i++) {
                final int progress = i + 1;

                runOnUiThread(() -> {
                    progressBar.setProgress(progress);
                    tvProgress.setText(progress + " / " + inventaireLigneTempList.size());
                });
                Inventaire_Ligne_Temp inventaireLigneTemp = inventaireLigneTempList.get(i);
                boolean estDernierElement = (i == inventaireLigneTempList.size() - 1);

                inventaireCourant = InventaireOpenHelper.getInventaireById(db, inventaireLigneTemp.getInventaire_ID());
                Depot depotCourant = DepotOpenHelper.getDepotParReference(db, inventaireLigneTemp.getDepotReference());

                boolean memeDepot = depotPrecedent.contentEquals(depotCourant.getNom());
                boolean memeZone  = zonePrecedente.contentEquals(inventaireLigneTemp.getZone());
                boolean dateSaisie = !inventaireLigneTemp.getInventaireDate().contentEquals("")
                        && !inventaireLigneTemp.getInventaireDate().contentEquals("null")
                        && !inventaireLigneTemp.getInventaireDate().contentEquals("0000-00-00");

                // Changement de zone ou de dépôt : on flush la ligne précédente
                if ((!memeDepot || !memeZone) && nbLigneZone != 0) {
                    ligneInventaire[0] = zonePrecedente;
                    ligneInventaire[1] = String.valueOf(nbLigneZone);
                    ligneInventaire[2] = String.valueOf(inventaireLigneTemp.getInventaire_ID());
                    ligneInventaire[3] = depotPrecedent;
                    ligneInventaire[4] = inventaireCourant.getClotureDate();
                    ligneInventaire[5] = String.valueOf(nbLigneZoneSaisie);
                    ligneInventaire[6] = (nbLigneZoneSaisie == nbLigneZone) ? "Saisie complète" : "À saisir";
                    ligneInventaire[7] = depotReferencePrecedent;

                    listeInventaire.add(ligneInventaire);
                    ligneInventaire = new String[8];
                    nbLigneZone = 0;
                    nbLigneZoneSaisie = 0;
                }

                // Mise à jour des compteurs pour l'élément courant
                nbLigneZone++;
                if (Inventaire_Ligne_TempOpenHelper.isInventaireLigneTempCompte(db, inventaireLigneTemp.getInventaire_ID(), inventaireLigneTemp.getZone(), inventaireLigneTemp.getProduitID(), inventaireLigneTemp.getDepotReference())) nbLigneZoneSaisie++;

                zonePrecedente  = inventaireLigneTemp.getZone();
                depotPrecedent  = depotCourant.getNom();
                depotReferencePrecedent = depotCourant.getDepot_Reference();

                // Dernier élément : on flush
                if (estDernierElement && nbLigneZone != 0) {
                    ligneInventaire[0] = zonePrecedente;
                    ligneInventaire[1] = String.valueOf(nbLigneZone);
                    ligneInventaire[2] = String.valueOf(inventaireLigneTemp.getInventaire_ID());
                    ligneInventaire[3] = depotCourant.getNom();
                    ligneInventaire[4] = inventaireCourant.getClotureDate();
                    ligneInventaire[5] = String.valueOf(nbLigneZoneSaisie);
                    ligneInventaire[6] = (nbLigneZoneSaisie == nbLigneZone) ? "Saisie complète" : "À saisir";
                    ligneInventaire[7] = depotReferencePrecedent;

                    listeInventaire.add(ligneInventaire);
                }
            }

            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeDepotInventaire.size()-1));
                ((TextView) findViewById(R.id.titre)).setText("dépôt à inventorier");
                inventaireAdapter = new InventaireAdapter(ServiceInventaireGeneralActivity.this, db, listeInventaire, utilisateurConnecte);
                inventaireListView.setAdapter(inventaireAdapter);
                alertDialog.dismiss();
            });
        }).start();

        if (inventaireLigneTempList.isEmpty()) {
            vide = true;
            nomServiceVide = "Inventaire Général";
            ServiceInventaireGeneralActivity.this.finish();
        }
    }
}
