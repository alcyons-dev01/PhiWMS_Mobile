package fr.alcyons.phiwms_mobile.Services;

import static com.google.android.gms.vision.L.TAG;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ControleDesRetours.DetailControleDesRetoursActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceControleRetoursActivity extends ServiceAvecConnexionActivity {
    Context context;
    List<Retour> retourList;
    ListView retourListView;
    RetourAdapter retourAdapter;
    PackageManager pm;
    Spinner optionTri;
    LinearLayout triListe;
    String tri_choisi;
    boolean connexionDirecte;
    ActivityResultLauncher<Intent> resultScanDocument;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        retourList = new ArrayList<>();
        context = ServiceControleRetoursActivity.this;
        pm = ServiceControleRetoursActivity.this.getPackageManager();
        optionTri = findViewById(R.id.optionTri);
        triListe = findViewById(R.id.triListe);
        triListe.setVisibility(View.VISIBLE);

        // Initialisation de la liste et de l'action sur l'un de ses items
        retourListView = findViewById(R.id.listeView);
        retourListView.setOnItemClickListener((parent, view, position, id) -> {
            Retour retourSelectionne = (Retour) retourAdapter.getItem(position);

            Bundle serviceControleRetours_Bundle = ServiceControleRetoursActivity.super.getBundle();
            assert retourSelectionne != null;
            serviceControleRetours_Bundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

            Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, DetailControleDesRetoursActivity.class);
            serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
            ServiceControleRetoursActivity.this.startActivity(serviceControleRetours_Intent);
            ServiceControleRetoursActivity.this.finish();
        });

        //gestion du spinner de tri
        tri_choisi= ParametreUtilisateurOpenHelper.getChoixTriRetour(db);
        if(tri_choisi == null)
        {
            ParametreUtilisateurOpenHelper.mettreAJourTriRetour(db, 0, "Numéro de retour");
            tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriRetour(db);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.option_tri_retour, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        optionTri.setAdapter(adapter);
        optionTri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            boolean isFirstSelection = true; // drapeau pour ignorer le premier appel

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false; // on consomme le premier appel
                    return; // ne rien faire au lancement
                }
                if (((TextView) parent.getChildAt(0)) != null) {
                    ((TextView) parent.getChildAt(0)).setVisibility(View.INVISIBLE);
                }
                tri_choisi = optionTri.getItemAtPosition(position).toString();
                ParametreUtilisateurOpenHelper.mettreAJourTriRetour(db, 0, tri_choisi);
                switch (tri_choisi)
                {
                    case "Numéro de retour":
                        onClickTriNumero();
                        break;

                    case "Date de retour":
                        onClickTriDate();
                        break;

                    case "Dépôt Origine":
                        onClickTriDepot();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        resultScanDocument = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == ServiceControleRetoursActivity.RESULT_OK) {
                        if (data != null) {
                            String code = Objects.requireNonNull(data.getExtras()).getString("code");
                            if (code != null) {
                                Retour retourSelectionne = RetourOpenHelper.getRetourByNumero(db, code);
                                if (retourSelectionne == null) {
                                    afficherSnackBarControleDesRetours();
                                    /* Code nécessaire à l'affichage de la liste */
                                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                                    retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList, utilisateurConnecte);
                                    // Permet d'enlever le séparateur entre deux éléments d'une listeView
                                    retourListView.setDivider(footer);
                                    retourListView.setAdapter(retourAdapter);

                                    if (retourList.isEmpty()) {
                                        vide = true;
                                        nomServiceVide = "Contrôle des retours";
                                        ServiceControleRetoursActivity.this.finish();
                                    }
                                    else
                                    {
                                        invalidateOptionsMenu();
                                    }
                                } else {
                                    Bundle serviceControleRetours_Bundle = ServiceControleRetoursActivity.super.getBundle();
                                    serviceControleRetours_Bundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                                    Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, DetailControleDesRetoursActivity.class);
                                    serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                                    ServiceControleRetoursActivity.this.startActivity(serviceControleRetours_Intent);
                                    ServiceControleRetoursActivity.this.finish();
                                }
                            } else {
                                /* Code nécessaire à l'affichage de la liste */
                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                                retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList, utilisateurConnecte);
                                // Permet d'enlever le séparateur entre deux éléments d'une listeView
                                retourListView.setDivider(footer);
                                retourListView.setAdapter(retourAdapter);

                                if (retourList.isEmpty()) {
                                    vide = true;
                                    nomServiceVide = "Contrôle des retours";
                                    ServiceControleRetoursActivity.this.finish();
                                }
                                else
                                {
                                    invalidateOptionsMenu();
                                }
                            }
                        } else {
                            /* Code nécessaire à l'affichage de la liste */
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                            retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList, utilisateurConnecte);
                            // Permet d'enlever le séparateur entre deux éléments d'une listeView
                            retourListView.setDivider(footer);
                            retourListView.setAdapter(retourAdapter);

                            if (retourList.isEmpty()) {
                                vide = true;
                                nomServiceVide = "Contrôle des retours";
                                ServiceControleRetoursActivity.this.finish();
                            }
                            else
                            {
                                invalidateOptionsMenu();
                            }
                        }
                    }
                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                retourNavigation();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (statutConnexion && passageParOnCreate && !connexionDirecte) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceControleRetoursActivity.this, LayoutInflater.from(ServiceControleRetoursActivity.this));
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ServiceControleRetoursActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteControleRetours;

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueue.add(obreq);
        } else {
            retourList = RetourOpenHelper.getRetoursByEnAttenteDe(db, getString(R.string.RepriseDemandee));
            if (retourList.isEmpty()) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
                    ServiceControleRetoursActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceControleRetoursActivity.this.finish();
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
                    /* Code nécessaire à l'affichage de la liste */
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                    retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList, utilisateurConnecte);
                    // Permet d'enlever le séparateur entre deux éléments d'une listeView
                    retourListView.setDivider(footer);
                    retourListView.setAdapter(retourAdapter);

                    if (retourList.isEmpty()) {
                        vide = true;
                        nomServiceVide = "Contrôle des retours";
                        ServiceControleRetoursActivity.this.finish();
                    }
                    else
                    {
                        invalidateOptionsMenu();
                        connexionDirecte = !connexionDirecte;
                    }
                }
            }
        }

        invalidateOptionsMenu();
    }

    @NonNull
    private Intent getRetourVersServiceConnexionDirectIntent() {
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceControleRetoursActivity.this, ServiceConnexionDirecteActivity.class);
        Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", "Quarantaine");

        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
        return retourVersServiceConnexionDirectIntent;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    private JsonObjectRequest getJsonObjectRequest(String urlRequete) {
        return new JsonObjectRequest
                (Request.Method.GET, urlRequete, null, response -> {
                    try {
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            String erreur = response.getString("erreur");
                            if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerteInformation(context, getLayoutInflater(), "Alerte", "Votre session est invalide, veuillez vous reconnecter.", false, true);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(context, getLayoutInflater(), "Alerte", "Votre session est expirée, veuillez vous reconnecter.", false, true);
                            } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                Alerte.afficherAlerteInformation(context, getLayoutInflater(), "Alerte", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Contrôle des retours", false, true);
                            }else {
                                arreterSpinner();
                                vide = true;
                                nomServiceVide = "Contrôle des retours";
                                retourNavigation();
                   }
                        } else {
                            JSONArray retoursJSONArray = response.getJSONArray("PH_Retours");
                            viderTablesConcernees();
                            long rowID;
                            for (int i = 0; i < retoursJSONArray.length(); i++) {
                                JSONObject retourJSONObject = retoursJSONArray.getJSONObject(i);
                                Retour retour = new Retour(retourJSONObject);

                                if (retour.getEn_Attente_de().equals(getString(R.string.RepriseDemandee))) {
                                    rowID = RetourOpenHelper.insererUnRetourEnBDD(db, retour);
                                    if (rowID != -1) {
                                        retourList.add(retour);

                                        JSONArray retourLignesJSONArray= retourJSONObject.getJSONArray("ph_retour_ligne");
                                        for (int k = 0; k < retourLignesJSONArray.length(); k++) {
                                            Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLignesJSONArray.getJSONObject(k)));
                                        }
                                    }
                                }
                            }

                            Retour retour_alcyons = RetourOpenHelper.getRetourEssaiAlcyons(db);
                            if(retour_alcyons != null)
                            {
                                retourList.add(retour_alcyons);
                            }

                            //lancerScan();
                            /* Code nécessaire à l'affichage de la liste */
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                            ((TextView) findViewById(R.id.titre)).setText("Demandes de retour");
                            retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList, utilisateurConnecte);
                            // Permet d'enlever le séparateur entre deux éléments d'une listeView
                            retourListView.setDivider(footer);
                            retourListView.setAdapter(retourAdapter);

                            if (retourList.isEmpty()) {
                                vide = true;
                                nomServiceVide = "Contrôle des retours";
                                ServiceControleRetoursActivity.this.finish();
                            }
                            else
                            {
                                switch (tri_choisi)
                                {
                                    case "Numéro de retour":
                                        onClickTriNumero();
                                        break;

                                    case "Date de retour":
                                        onClickTriDate();
                                        break;

                                    case "Dépôt Origine":
                                        onClickTriDepot();
                                        break;
                                }
                                invalidateOptionsMenu();
                            }
                            passageParOnCreate = false;
                            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                        }
                    }
                    catch (Throwable t)
                    {
                        Log.e(TAG, "Error JSON", t);
                    }
                }, error -> {
                    // TODO: Handle error
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(context, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Contrôle des retours", false, true);

                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                return headers;
            }
        };
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, retourAdapter, null, "Produit, Intitulé, N°...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> {
            lancerScan();
            return true;
        });
        return true;
    }

    public void viderTablesConcernees() {
        for (Retour retour : RetourOpenHelper.getRetoursByEnAttenteDe(db, getString(R.string.RepriseDemandee))) {
            if(!retour.getIntitule().contentEquals("Retour_ALCYONS"))
            {
                for (Retour_Ligne retourLigne : Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retour)) {
                    Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigne);
                    Produit produit = ProduitOpenHelper.getProduitByID(db, retourLigne.getCode_produit());
                    Depot depot = DepotOpenHelper.getDepotParReference(db, retour.getRef_Depot_Origine());

                    if(produit != null && depot != null)
                    {
                        for (Stock_Lot_Emplacement_Light stockLotEmplacementLight : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)) {
                            //Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stockLotEmplacementLight);
                        }
                    }
                }
                RetourOpenHelper.supprimerUnRetour(db, retour);
            }
        }
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
        Bundle scanDocumentBundle = ServiceControleRetoursActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        scanDocumentBundle.putBoolean("modeRafale", false);

        Intent scanDocumentIntent;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(ServiceControleRetoursActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServiceControleRetoursActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceControleRetoursActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }


        scanDocumentIntent.putExtras(scanDocumentBundle);
        resultScanDocument.launch(scanDocumentIntent);
    }
    public void onClickTriNumero()
    {
        tri_choisi = "Numéro de retour";
        retourList.sort(Comparator.comparing(Retour::getNumero));

        retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList, utilisateurConnecte);
        retourListView.setDivider(footer);
        retourListView.setAdapter(retourAdapter);
    }

    public void onClickTriDate()
    {
        tri_choisi = "Date de retour";
        retourList.sort((o1, o2) -> o1.getDate_retour().compareTo(o2.getDate_retour()));

        retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList, utilisateurConnecte);
        retourListView.setDivider(footer);
        retourListView.setAdapter(retourAdapter);
    }

    public void onClickTriDepot()
    {
        tri_choisi = "Dépôt Origine";
        retourList.sort(Comparator.comparing(Retour::getRef_Depot_Origine));

        retourAdapter = new RetourAdapter(ServiceControleRetoursActivity.this, db, retourList, utilisateurConnecte);
        retourListView.setDivider(footer);
        retourListView.setAdapter(retourAdapter);
    }

    public void afficherSnackBarControleDesRetours() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
