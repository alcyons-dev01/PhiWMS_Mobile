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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
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
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.DetailPreparation2025Activity;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.DetailPreparation2025_V2Activity;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.DetailPreparationActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServicePreparationPufActivity extends ServiceAvecConnexionActivity {

    Context context;
    PackageManager pm;
    List<PH_Preparation> ph_preparation_List;
    ListView ph_preparation_ListView;
    PH_Preparation_PreparationAdapter ph_preparation_preparationAdapter;
    boolean connexionDirecte;
    List<String> listeDepotLivraison;
    ArrayAdapter<String> spinnerArrayAdapter;
    Spinner spinner;
    List<PH_Preparation> ph_preparation_List_base;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        pm = ServicePreparationPufActivity.this.getPackageManager();
        context = ServicePreparationPufActivity.this;

        // Gestion de la listView
        ph_preparation_ListView = (ListView) findViewById(R.id.listeView);
        ph_preparation_ListView.setOnItemClickListener((parent, view, position, id) -> {
            PH_Preparation ph_preparation_Selectionne = (PH_Preparation) ph_preparation_preparationAdapter.getItem(position);

            Intent servicePreparationPuf_Intent = new Intent(ServicePreparationPufActivity.this, DetailPreparation2025_V2Activity.class);
            Bundle servicePreparationPuf_Bundle = ServicePreparationPufActivity.super.getBundle();
            assert ph_preparation_Selectionne != null;
            servicePreparationPuf_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
            servicePreparationPuf_Bundle.putString("genre", "PUF");
            servicePreparationPuf_Intent.putExtras(servicePreparationPuf_Bundle);
            ServicePreparationPufActivity.this.startActivity(servicePreparationPuf_Intent);
            ServicePreparationPufActivity.this.finish();
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServicePreparationPufActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServicePreparationPufActivity.this.startActivity(intent);
                ServicePreparationPufActivity.this.finish();
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
        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServicePreparationPufActivity.this, LayoutInflater.from(ServicePreparationPufActivity.this));
            }
            RequestQueue requestQueue = Volley.newRequestQueue(ServicePreparationPufActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePreparationPUF;

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueue.add(obreq);
        }
        else
        {
            ph_preparation_List = PH_PreparationOpenHelper.getAllPHPreparationPreparationPUF(db);
            ph_preparation_List_base = PH_PreparationOpenHelper.getAllPHPreparationPreparationPUF(db);

            if (ph_preparation_List.isEmpty()) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
                    ServicePreparationPufActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServicePreparationPufActivity.this.finish();
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
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(ph_preparation_List.size()));
                    ph_preparation_List.sort((o1, o2) -> o2.getLivraisonPrevueDate().compareTo(o1.getLivraisonPrevueDate()));
                    ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(ServicePreparationPufActivity.this, ph_preparation_List, db, utilisateurConnecte);
                    // Permet d'enlever le séparateur entre deux éléments d'une listeView
                    ph_preparation_ListView.setDivider(footer);
                    ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);

                    if (ph_preparation_List.isEmpty()) {
                        vide = true;
                        nomServiceVide = "Préparation PUF";
                        ServicePreparationPufActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                    connexionDirecte = !connexionDirecte;
                }
                else
                {
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(ph_preparation_List.size()));
                    ph_preparation_List.sort((o1, o2) -> o2.getLivraisonPrevueDate().compareTo(o1.getLivraisonPrevueDate()));
                    ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(ServicePreparationPufActivity.this, ph_preparation_List, db, utilisateurConnecte);
                    // Permet d'enlever le séparateur entre deux éléments d'une listeView
                    ph_preparation_ListView.setDivider(footer);
                    ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);
                }
            }

            spinner = (Spinner) findViewById(R.id.optionTri);

            spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listeDepotLivraison);
            spinner.setAdapter(spinnerArrayAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(((TextView) parent.getChildAt(0)) != null)
                    {
                        ((TextView) parent.getChildAt(0)).setVisibility(View.INVISIBLE);
                    }
                    String depot = spinner.getItemAtPosition(position).toString();

                    ph_preparation_List = new ArrayList<>();

                    if(depot.contentEquals("Tous"))
                    {
                        ph_preparation_List.addAll(ph_preparation_List_base);
                    }
                    else
                    {
                        for(PH_Preparation preparation_courant : ph_preparation_List_base)
                        {
                            Depot depotCourant = DepotOpenHelper.getDepotParReference(db, preparation_courant.getDepotDestinataireReference());
                            if(depotCourant.getNom().contentEquals(depot))
                            {
                                ph_preparation_List.add(preparation_courant);
                            }
                        }
                    }

                    ph_preparation_List.sort((o1, o2) -> o2.getLivraisonPrevueDate().compareTo(o1.getLivraisonPrevueDate()));

                    ph_preparation_preparationAdapter.phPreparationPreparation.clear();
                    ph_preparation_preparationAdapter.phPreparationPreparation.addAll(ph_preparation_List);
                    ph_preparation_preparationAdapter.notifyDataSetChanged();

                    int taille_liste = ph_preparation_List.size();

                    /* Code nécessaire à l'affichage de la liste */
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(taille_liste));
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

            invalidateOptionsMenu();
        }
    }

    @NonNull
    private Intent getRetourVersServiceConnexionDirectIntent() {
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServicePreparationPufActivity.this, ServiceConnexionDirecteActivity.class);
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
                                Alerte.afficherAlerte(context, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                //DBOpenHelper.viderBasesDeDonnees(db);
                                ServicePreparationPufActivity.this.finishAffinity();
                                Intent intent = new Intent(context, AuthentificationActivity.class);
                                context.startActivity(intent);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                ServicePreparationPufActivity.this.finishAffinity();
                                Intent intent = new Intent(context, AuthentificationActivity.class);
                                context.startActivity(intent);
                            } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Préparation PUF", "alerte");
                                ServicePreparationPufActivity.this.finishAffinity();
                            } else {
                                arreterSpinner();
                                Alerte.afficherAlerte(ServicePreparationPufActivity.this, "Alerte", "Aucune préparation UF à traiter", "alerte");
                                retourNavigation(ServicePreparationPufActivity.this);
                            }
                        } else {
                            JSONArray ph_preparation_JSONArray = response.getJSONArray("PH_Preparations");
                            viderTablesConcernees();


                            long rowID = 0;
                            for (int i = 0; i < ph_preparation_JSONArray.length(); i++) {
                                JSONObject ph_preparation_JSONObject = ph_preparation_JSONArray.getJSONObject(i);
                                PH_Preparation ph_preparation = new PH_Preparation(ph_preparation_JSONObject);

                                rowID = PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);

                                if (rowID != -1) {
                                    //gestion de la liste des dépôts
                                    Depot depotDestinataire = DepotOpenHelper.getDepotParReference(db, ph_preparation.getDepotDestinataireReference());
                                    if(depotDestinataire != null)
                                    {
                                        if(!listeDepotLivraison.contains(depotDestinataire.getNom()))
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

                            if(passageParOnCreate)
                            {
                                //on récupère la preparation du jeu d'essai si elle existe
                                PH_Preparation preparation_alcyons = PH_PreparationOpenHelper.getPreparationEssaiAlcyons(db);
                                if(preparation_alcyons != null)
                                {
                                    ph_preparation_List.add(preparation_alcyons);
                                }

                                /* Code nécessaire à l'affichage de la liste */
                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(ph_preparation_List.size()));
                                ((TextView) findViewById(R.id.titre)).setText("Préparations");

                                ph_preparation_List.sort((o1, o2) -> o2.getLivraisonPrevueDate().compareTo(o1.getLivraisonPrevueDate()));
                                ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(ServicePreparationPufActivity.this, ph_preparation_List, db, utilisateurConnecte);
                                // Permet d'enlever le séparateur entre deux éléments d'une listeView
                                ph_preparation_ListView.setDivider(footer);
                                ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);

                                if (ph_preparation_List.isEmpty()) {
                                    vide = true;
                                    nomServiceVide = "Préparation PUF";
                                    ServicePreparationPufActivity.this.finish();
                                }

                                invalidateOptionsMenu();
                            }

                            spinner = (Spinner) findViewById(R.id.optionTri);

                            spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listeDepotLivraison);
                            spinner.setAdapter(spinnerArrayAdapter);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if(((TextView) parent.getChildAt(0)) != null)
                                    {
                                        ((TextView) parent.getChildAt(0)).setVisibility(View.INVISIBLE);
                                    }
                                    String depot = spinner.getItemAtPosition(position).toString();

                                    ph_preparation_List = new ArrayList<>();

                                    if(depot.contentEquals("Tous"))
                                    {
                                        ph_preparation_List.addAll(ph_preparation_List_base);
                                    }
                                    else
                                    {
                                        for(PH_Preparation preparation_courant : ph_preparation_List_base)
                                        {
                                            Depot depotCourant = DepotOpenHelper.getDepotParReference(db, preparation_courant.getDepotDestinataireReference());
                                            if(depotCourant.getNom().contentEquals(depot))
                                            {
                                                ph_preparation_List.add(preparation_courant);
                                            }
                                        }
                                    }

                                    ph_preparation_List.sort((o1, o2) -> o2.getLivraisonPrevueDate().compareTo(o1.getLivraisonPrevueDate()));

                                    ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(ServicePreparationPufActivity.this, ph_preparation_List, db, utilisateurConnecte);

                                    ph_preparation_ListView.setDivider(footer);
                                    ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);


                                    int taille_liste = ph_preparation_List.size();

                                    /* Code nécessaire à l'affichage de la liste */
                                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(taille_liste));
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
                    Alerte.afficherAlerte(ServicePreparationPufActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Préparation PUF)", "alerte");
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
        for (PH_Preparation ph_preparation : PH_PreparationOpenHelper.getAllPHPreparationPreparationPUF(db))
        {
            if(!ph_preparation.getListe().contentEquals("ALCYONS_LISTE"))
            {
                for (PH_Preparation_Ligne ph_preparation_ligne : PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, ph_preparation))
                {
                    PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
                    Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne.getProduitID());
                    Depot depot = DepotOpenHelper.getDepotParID(db, ph_preparation.getDepotOrigineID());

                    if (depot != null && produit != null) {
                        for (Stock_Lot_Emplacement_Light stockLotEmplacement : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)
                                ) {
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
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);
        return true;
    }
    public void lancerScan()
    {
        Bundle scanDocumentBundle = ServicePreparationPufActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent = null;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(ServicePreparationPufActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
            scanDocumentBundle.putString("Context", "Preparation");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServicePreparationPufActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServicePreparationPufActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une préparation");
                scanDocumentBundle.putString("Context", "Preparation");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServicePreparationPufActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodesEchangesActivites.RETOUR_DOCUMENT) {
            if (data != null) {
                String code = Objects.requireNonNull(data.getExtras()).getString("code");
                if (code != null) {
                    int idPreparation = 0;
                    try {
                        idPreparation = Integer.parseInt(code);
                    } catch (NumberFormatException ignored) {
                    }
                    PH_Preparation ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, idPreparation);
                    if (ph_preparation_Selectionne == null) {
                        if (!code.contentEquals("")) {
                            afficherSnackBarPreparationPUF();
                        }
                        /* Code nécessaire à l'affichage de la liste */
                        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(ph_preparation_List.size()));
                        ph_preparation_List.sort((o1, o2) -> o2.getLivraisonPrevueDate().compareTo(o1.getLivraisonPrevueDate()));
                        ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(ServicePreparationPufActivity.this, ph_preparation_List, db, utilisateurConnecte);
                        // Permet d'enlever le séparateur entre deux éléments d'une listeView
                        ph_preparation_ListView.setDivider(footer);
                        ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);

                        if (ph_preparation_List.isEmpty()) {
                            vide = true;
                            nomServiceVide = "Préparation PUF";
                            ServicePreparationPufActivity.this.finish();
                        }

                        invalidateOptionsMenu();
                    } else {
                        Intent servicePreparationPuf_Intent = getIntent(ph_preparation_Selectionne);
                        ServicePreparationPufActivity.this.startActivity(servicePreparationPuf_Intent);
                        ServicePreparationPufActivity.this.finish();
                    }
                } else {
                    /* Code nécessaire à l'affichage de la liste */
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(ph_preparation_List.size()));
                    ph_preparation_List.sort((o1, o2) -> o2.getLivraisonPrevueDate().compareTo(o1.getLivraisonPrevueDate()));
                    ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(ServicePreparationPufActivity.this, ph_preparation_List, db, utilisateurConnecte);
                    // Permet d'enlever le séparateur entre deux éléments d'une listeView
                    ph_preparation_ListView.setDivider(footer);
                    ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);

                    if (ph_preparation_List.isEmpty()) {
                        vide = true;
                        nomServiceVide = "Préparation PUF";
                        ServicePreparationPufActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                }
            } else {
                /* Code nécessaire à l'affichage de la liste */
                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(ph_preparation_List.size()));
                ph_preparation_List.sort((o1, o2) -> o2.getLivraisonPrevueDate().compareTo(o1.getLivraisonPrevueDate()));
                ph_preparation_preparationAdapter = new PH_Preparation_PreparationAdapter(ServicePreparationPufActivity.this, ph_preparation_List, db, utilisateurConnecte);
                // Permet d'enlever le séparateur entre deux éléments d'une listeView
                ph_preparation_ListView.setDivider(footer);
                ph_preparation_ListView.setAdapter(ph_preparation_preparationAdapter);

                if (ph_preparation_List.isEmpty()) {
                    vide = true;
                    nomServiceVide = "Préparation PUF";
                    ServicePreparationPufActivity.this.finish();
                }

                invalidateOptionsMenu();
            }
        }
    }

    @NonNull
    private Intent getIntent(PH_Preparation ph_preparation_Selectionne) {
        Intent servicePreparationPuf_Intent = new Intent(ServicePreparationPufActivity.this, DetailPreparation2025_V2Activity.class);
        Bundle servicePreparationPuf_Bundle = ServicePreparationPufActivity.super.getBundle();
        servicePreparationPuf_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
        servicePreparationPuf_Bundle.putString("genre", "PUF");
        servicePreparationPuf_Intent.putExtras(servicePreparationPuf_Bundle);
        return servicePreparationPuf_Intent;
    }

    public void afficherSnackBarPreparationPUF() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);;

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}