package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

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
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Inventaire.DetailInventaireActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.InventaireAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.DetailPreparationActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceInventairePartielActivity extends ServiceAvecConnexionActivity {
    Context context;
    PackageManager pm;
    List<Inventaire> inventaireList;
    ListView inventaireListView;
    InventaireAdapter inventaireAdapter;
    boolean connexionDirecte;
    List<String> listeDepotLivraison;
    ArrayAdapter<String> spinnerArrayAdapter;
    Spinner spinner;
    List<Inventaire> inventaireListBase;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        pm = ServiceInventairePartielActivity.this.getPackageManager();
        context = ServiceInventairePartielActivity.this;

        // Gestion de la listView
        inventaireListView = (ListView) findViewById(R.id.listeView);
        inventaireListView.setOnItemClickListener((parent, view, position, id) -> {
            String[] inventaire_Selectionne = (String[]) inventaireAdapter.getItem(position);

            Intent serviceInventaire_Intent = new Intent(ServiceInventairePartielActivity.this, DetailInventaireActivity.class);
            Bundle serviceInventaire_Bundle = ServiceInventairePartielActivity.super.getBundle();
            serviceInventaire_Bundle.putInt("inventaireId", Integer.parseInt(inventaire_Selectionne[2]));
            serviceInventaire_Bundle.putString("zoneSelectionne", inventaire_Selectionne[0]);
            serviceInventaire_Bundle.putString("depotSelectionne", inventaire_Selectionne[3]);
            serviceInventaire_Intent.putExtras(serviceInventaire_Bundle);
            ServiceInventairePartielActivity.this.startActivity(serviceInventaire_Intent);
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceInventairePartielActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceInventairePartielActivity.this.startActivity(intent);
                ServiceInventairePartielActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        inventaireList = new ArrayList<>();
        inventaireListBase = new ArrayList<>();
        listeDepotLivraison = new ArrayList<>();
        listeDepotLivraison.add("Tous");
        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceInventairePartielActivity.this, LayoutInflater.from(ServiceInventairePartielActivity.this));
            }
            RequestQueue requestQueue = Volley.newRequestQueue(ServiceInventairePartielActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteInventaires;

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueue.add(obreq);
        }
        else
        {
            inventaireList = InventaireOpenHelper.getAllInventaire(db);
            inventaireListBase = InventaireOpenHelper.getAllInventaire(db);

            if (inventaireList.isEmpty()) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
                    ServiceInventairePartielActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceInventairePartielActivity.this.finish();
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
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceInventairePartielActivity.this, ServiceConnexionDirecteActivity.class);
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
                                Alerte.afficherAlerteInformation(ServiceInventairePartielActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est invalide, veuillez vous reconnecter.", false, true);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(ServiceInventairePartielActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est expirée, veuillez vous reconnecter.", false, true);
                            } else if (!erreur.contentEquals("Aucun Inventaire trouvé")) {
                                Alerte.afficherAlerteInformation(ServiceInventairePartielActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Partiel", false, true);
                            } else {
                                arreterSpinner();
                                Alerte.afficherAlerteInformation(ServiceInventairePartielActivity.this, getLayoutInflater(), "Information", "Aucun inventaire partiel à traiter", false, true);
                            }
                        } else {
                            JSONArray inventaire_JSONArray = response.getJSONArray("Inventaires");
                            viderTablesConcernees();
                            long rowID = 0;
                            for (int i = 0; i < inventaire_JSONArray.length(); i++) {
                                JSONObject inventaire_JSONObject = inventaire_JSONArray.getJSONObject(i);
                                Inventaire inventaire = new Inventaire(inventaire_JSONObject);
                                rowID = InventaireOpenHelper.insererUnInventaireEnBDD(db, inventaire);

                                if (rowID != -1) {
                                    //gestion de la liste des dépôts
                                    Depot depotDestinataire = DepotOpenHelper.getDepotParReference(db, inventaire.getDepotReference());
                                    if(depotDestinataire != null)
                                    {
                                        if(!listeDepotLivraison.contains(depotDestinataire.getNom()))
                                            listeDepotLivraison.add(depotDestinataire.getNom());
                                    }
                                    inventaireList.add(inventaire);
                                    inventaireListBase.add(inventaire);

                                    JSONArray inventaireLigneTemp_JSONArray = inventaire_JSONObject.getJSONArray("inventaire_ligne_temp");
                                    for (int k = 0; k < inventaireLigneTemp_JSONArray.length(); k++) {
                                        Inventaire_Ligne_TempOpenHelper.insererUnInventaire_Ligne_TempEnBDD(db, new Inventaire_Ligne_Temp(inventaireLigneTemp_JSONArray.getJSONObject(k)));
                                    }
                                }
                            }

                            if(passageParOnCreate)
                            {
                                /* Code nécessaire à l'affichage de la liste */
                                gestionAdapter();
                                invalidateOptionsMenu();
                            }

                            spinner = (Spinner) findViewById(R.id.optionTri);

                            spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listeDepotLivraison);
                            spinner.setAdapter(spinnerArrayAdapter);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                                boolean isFirstSelection = true; // drapeau pour ignorer le premier appel

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (isFirstSelection) {
                                        isFirstSelection = false; // on consomme le premier appel
                                        return; // ne rien faire au lancement
                                    }
                                    if(((TextView) parent.getChildAt(0)) != null)
                                    {
                                        ((TextView) parent.getChildAt(0)).setVisibility(View.INVISIBLE);
                                    }
                                    String depot = spinner.getItemAtPosition(position).toString();

                                    inventaireList = new ArrayList<>();

                                    if(depot.contentEquals("Tous"))
                                    {
                                        inventaireList.addAll(inventaireListBase);
                                    }
                                    else
                                    {
                                        for(Inventaire inventaireCourant : inventaireListBase)
                                        {
                                            Depot depotCourant = DepotOpenHelper.getDepotParReference(db, inventaireCourant.getDepotReference());
                                            if(depotCourant.getNom().contentEquals(depot))
                                            {
                                                inventaireList.add(inventaireCourant);
                                            }
                                        }
                                    }

                                    gestionAdapter();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub
                                }
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
                    Alerte.afficherAlerteInformation(ServiceInventairePartielActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Partiel", false, true);
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
        Bundle scanDocumentBundle = ServiceInventairePartielActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent = null;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(ServiceInventairePartielActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
            scanDocumentBundle.putString("Context", "Preparation");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServiceInventairePartielActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceInventairePartielActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
                scanDocumentBundle.putString("Context", "Preparation");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceInventairePartielActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void gestionAdapter()
    {
        inventaireList.sort(Comparator.comparing(Inventaire::getDepotNom));

        List<String[]> listeInventaire = new ArrayList<>();

        for(Inventaire inventaire : inventaireList)
        {
            List<Inventaire_Ligne_Temp> inventaireLigneTempList = Inventaire_Ligne_TempOpenHelper.getAllInventaireLigneTempByInventaire(db, inventaire.getInventaire_ID());
            inventaireLigneTempList.sort(Comparator.comparing(Inventaire_Ligne_Temp::getZone));

            String zonePrecedente = "";
            int nbLigneZone = 0;
            int nbLigneZoneSaisie = 0;
            String[] ligneInventaire = new String[7];
            int compteur = 0;
            for(Inventaire_Ligne_Temp inventaireLigneTemp : inventaireLigneTempList)
            {
                compteur ++;
                if(zonePrecedente.contentEquals(inventaireLigneTemp.getZone()))
                {
                    nbLigneZone = nbLigneZone + 1;
                    if(!inventaireLigneTemp.getInventaireDate().contentEquals("") && !inventaireLigneTemp.getInventaireDate().contentEquals("null") && !inventaireLigneTemp.getInventaireDate().contentEquals("0000-00-00"))
                        nbLigneZoneSaisie = nbLigneZoneSaisie + 1;

                    if(compteur == inventaireLigneTempList.size())
                    {
                        if(nbLigneZone != 0)
                        {
                            ligneInventaire[0] = zonePrecedente;
                            ligneInventaire[1] = String.valueOf(nbLigneZone);
                            ligneInventaire[2] = String.valueOf(inventaireLigneTemp.getInventaire_ID());
                            ligneInventaire[3] = inventaire.getDepotReference();
                            ligneInventaire[4] = inventaire.getClotureDate();
                            ligneInventaire[5] = String.valueOf(nbLigneZoneSaisie);
                            if(nbLigneZoneSaisie == nbLigneZone)
                                ligneInventaire[6] = "Saisie complère";
                            else
                                ligneInventaire[6] = "À saisir";

                            listeInventaire.add(ligneInventaire);
                        }
                    }
                }
                else
                {
                    if(nbLigneZone != 0)
                    {
                        ligneInventaire[0] = zonePrecedente;
                        ligneInventaire[1] = String.valueOf(nbLigneZone);
                        ligneInventaire[2] = String.valueOf(inventaireLigneTemp.getInventaire_ID());
                        ligneInventaire[3] = inventaire.getDepotReference();
                        ligneInventaire[4] = inventaire.getClotureDate();
                        ligneInventaire[5] = String.valueOf(nbLigneZoneSaisie);
                        if(nbLigneZoneSaisie == nbLigneZone)
                            ligneInventaire[6] = "Saisie complète";
                        else
                            ligneInventaire[6] = "À saisir";

                        listeInventaire.add(ligneInventaire);
                        nbLigneZoneSaisie = 0;
                        ligneInventaire = new String[7];
                    }

                    if(!inventaireLigneTemp.getInventaireDate().contentEquals("") && !inventaireLigneTemp.getInventaireDate().contentEquals("null") && !inventaireLigneTemp.getInventaireDate().contentEquals("0000-00-00"))
                        nbLigneZoneSaisie = nbLigneZoneSaisie + 1;
                    
                    nbLigneZone = 1;
                    zonePrecedente = inventaireLigneTemp.getZone();
                }
            }
        }

        inventaireAdapter = new InventaireAdapter(ServiceInventairePartielActivity.this, db, listeInventaire, utilisateurConnecte);
        // Permet d'enlever le séparateur entre deux éléments d'une listeView
        //ph_preparation_ListView.setDivider(footer);
        inventaireListView.setAdapter(inventaireAdapter);

        if (inventaireList.isEmpty()) {
            vide = true;
            nomServiceVide = "Inventaire partiel";
            ServiceInventairePartielActivity.this.finish();
        }
    }
}
