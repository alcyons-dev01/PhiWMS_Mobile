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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
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
import fr.alcyons.phiwms_mobile.Reception.DetailReception2025Activity;
import fr.alcyons.phiwms_mobile.Reception.DetailReceptionActivity;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

import static fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper.viderTableCommandes;
import static fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper.viderTablePH_Reliquat;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

public class ServiceReceptionPuiActivity extends ServiceAvecConnexionActivity {

    //déclaration des variables
    String tri_choisi;
    JSONArray commandeJSONArray;
    JSONArray phReliquatJSONArray;
    List<Commande> commandeList;
    Depot depotPUI;
    boolean connexionDirecte;
    ReceptionAdapter commandeReceptionPUIAdapter;
    ListView commandeListView;
    PackageManager pm;
    ActivityResultLauncher<Intent> resultScanDocument;
    Spinner optionTri;

    @SuppressLint("SetTextI18n")
    private void initObjetGraphique()
    {
        optionTri = (Spinner) findViewById(R.id.optionTri);
        commandeListView = findViewById(R.id.listeView);
        pm = ServiceReceptionPuiActivity.this.getPackageManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);

        //initialisation des objets graphque
        initObjetGraphique();

        //gestion du spinner de tri
        tri_choisi= ParametreUtilisateurOpenHelper.getChoixTriReception(db);
        if(tri_choisi == null)
        {
            ParametreUtilisateurOpenHelper.mettreAJourTriReception(db, 0, "Numéro de commande");
            tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriReception(db);
        }

        depotPUI = DepotOpenHelper.getDepotPUI(db);

        commandeListView.setOnItemClickListener((parent, view, position, id) -> {
            Commande commandeSelectionne = (Commande) commandeReceptionPUIAdapter.getItem(position);

            if (commandeSelectionne != null) {
                Intent serviceReceptionPui_Intent = new Intent(ServiceReceptionPuiActivity.this, DetailReception2025Activity.class);
                Bundle serviceReceptionPui_Bundle = ServiceReceptionPuiActivity.super.getBundle();
                serviceReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                serviceReceptionPui_Intent.putExtras(serviceReceptionPui_Bundle);
                ServiceReceptionPuiActivity.this.startActivity(serviceReceptionPui_Intent);
                ServiceReceptionPuiActivity.this.finish();
            }
        });

        optionTri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                ParametreUtilisateurOpenHelper.mettreAJourTriReception(db, 0, tri_choisi);

                switch (tri_choisi)
                {
                    case "Numéro de commande":
                        onClickTriNumero();
                        break;

                    case "Date de livraison":
                        onClickTriDate();
                        break;

                    case "Fournisseur":
                        onClickTriFournisseur();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                                        afficherSnackBarPreparationReceptionPUI();
                                        /* Code nécessaire à l'affichage de la liste */
                                        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                                        commandeReceptionPUIAdapter = new ReceptionAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
                                        commandeListView.setDivider(footer);
                                        commandeListView.setAdapter(commandeReceptionPUIAdapter);

                                        if (commandeList.isEmpty()) {
                                            vide = true;
                                            nomServiceVide = "Réception PUI";
                                            Intent intent = new Intent(ServiceReceptionPuiActivity.this, NavigationActivity.class);
                                            Bundle extras = new Bundle();
                                            extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                            intent.putExtras(extras);
                                            ServiceReceptionPuiActivity.this.startActivity(intent);
                                            ServiceReceptionPuiActivity.this.finish();
                                        }

                                        invalidateOptionsMenu();
                                    } else {
                                        Intent serviceReceptionPui_Intent = new Intent(ServiceReceptionPuiActivity.this, DetailReception2025Activity.class);
                                        Bundle serviceReceptionPui_Bundle = ServiceReceptionPuiActivity.super.getBundle();
                                        serviceReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                                        serviceReceptionPui_Intent.putExtras(serviceReceptionPui_Bundle);
                                        ServiceReceptionPuiActivity.this.startActivity(serviceReceptionPui_Intent);
                                        ServiceReceptionPuiActivity.this.finish();
                                    }
                                } else {
                                    /* Code nécessaire à l'affichage de la liste */
                                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                                    commandeReceptionPUIAdapter = new ReceptionAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
                                    commandeListView.setDivider(footer);
                                    commandeListView.setAdapter(commandeReceptionPUIAdapter);

                                    if (commandeList.isEmpty()) {
                                        vide = true;
                                        nomServiceVide = "Réception PUI";
                                        Intent intent = new Intent(ServiceReceptionPuiActivity.this, NavigationActivity.class);
                                        Bundle extras = new Bundle();
                                        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                        intent.putExtras(extras);
                                        ServiceReceptionPuiActivity.this.startActivity(intent);
                                        ServiceReceptionPuiActivity.this.finish();
                                    }

                                    invalidateOptionsMenu();
                                }
                            } else {
                                /* Code nécessaire à l'affichage de la liste */
                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                                commandeReceptionPUIAdapter = new ReceptionAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
                                commandeListView.setDivider(footer);
                                commandeListView.setAdapter(commandeReceptionPUIAdapter);

                                if (commandeList.isEmpty()) {
                                    vide = true;
                                    nomServiceVide = "Réception PUI";
                                    Intent intent = new Intent(ServiceReceptionPuiActivity.this, NavigationActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                    intent.putExtras(extras);
                                    ServiceReceptionPuiActivity.this.startActivity(intent);
                                    ServiceReceptionPuiActivity.this.finish();
                                }

                                invalidateOptionsMenu();
                            }
                        }
                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceReceptionPuiActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceReceptionPuiActivity.this.startActivity(intent);
                ServiceReceptionPuiActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        commandeList = new ArrayList<>();

        //* Code nécessaire afin de réaliser une requête à l' API *//*
        if (statutConnexion && passageParOnCreate && !connexionDirecte)
        {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceReceptionPuiActivity.this, LayoutInflater.from(ServiceReceptionPuiActivity.this));
            }

            RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ServiceReceptionPuiActivity.this);
            String urlRequete = null;
            try {
                urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteCommandes + "depotreference/" + URLEncoder.encode(depotPUI.getDepot_Reference(), "utf-8");
            } catch (Throwable e) {
                Log.e(TAG, "Error UnsupportedEncodingException :", e);
            }

            JsonObjectRequest obreq = getObjectRequest(urlRequete);
            requestQueueDestructionUtilisateur.add(obreq);
        }
        else
        {
            commandeList = CommandeOpenHelper.getAllCommandesPUI(db, depotPUI.getDepot_Reference());
            if (commandeList.isEmpty()) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
                    ServiceReceptionPuiActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceReceptionPuiActivity.this.finish();
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
                    connexionDirecte = false;
                }
            }

            commandeReceptionPUIAdapter = new ReceptionAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));
            commandeListView.setDivider(footer);
            commandeListView.setAdapter(commandeReceptionPUIAdapter);
            invalidateOptionsMenu();

            switch (tri_choisi)
            {
                case "Numéro de commande":
                    onClickTriNumero();
                    break;

                case "Date de livraison":
                    onClickTriDate();
                    break;

                case "Fournisseur":
                    onClickTriFournisseur();
                    break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    private JsonObjectRequest getObjectRequest(String urlRequete) {
        return new JsonObjectRequest
                (Request.Method.GET, urlRequete, null, response -> {
                    try {
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            String erreur = response.getString("erreur");
                            if (erreur.equals(getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerte(ServiceReceptionPuiActivity.this, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                DBOpenHelper.viderBasesDeDonnees(db);
                                ServiceReceptionPuiActivity.this.finishAffinity();
                                Intent intent = new Intent(ServiceReceptionPuiActivity.this, AuthentificationActivity.class);
                                ServiceReceptionPuiActivity.this.startActivity(intent);
                            } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerte(ServiceReceptionPuiActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", "alerte");
                                ServiceReceptionPuiActivity.this.finishAffinity();
                                Intent intent = new Intent(ServiceReceptionPuiActivity.this, AuthentificationActivity.class);
                                ServiceReceptionPuiActivity.this.startActivity(intent);
                            } else if (erreur.contentEquals("Aucun PH_Commande trouvé")) {
                                arreterSpinner();
                                Alerte.afficherAlerte(ServiceReceptionPuiActivity.this, "Alerte", "Aucune réception PUI à traiter", "alerte");
                                retourNavigation(ServiceReceptionPuiActivity.this);
                            } else {
                                Alerte.afficherAlerte(ServiceReceptionPuiActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Reception PUI", "alerte");
                            }
                        } else {
                            commandeJSONArray = response.getJSONArray("PH_Commandes");
                            viderTableCommandes(db);
                            List<PH_Reliquat> listeReliquatASupprimer= PH_ReliquatOpenHelper.getPH_ReliquatBase(db);
                            for (PH_Reliquat reliquat : listeReliquatASupprimer) {
                                PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquat);
                            }

                            for (int i = 0; i < commandeJSONArray.length(); i++) {
                                JSONObject commandeJSONObject = commandeJSONArray.getJSONObject(i);

                                Commande commandeCourant = new Commande(commandeJSONObject);

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
                                        commandeList.add(commandeCourant);
                                    }
                                }
                            }

                            passageParOnCreate = false;

                            commandeReceptionPUIAdapter = new ReceptionAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));
                            ((TextView) findViewById(R.id.titre)).setText("Réceptions");
                            commandeListView.setDivider(footer);
                            commandeListView.setAdapter(commandeReceptionPUIAdapter);
                            invalidateOptionsMenu();

                            switch (tri_choisi)
                            {
                                case "Numéro de commande":
                                    onClickTriNumero();
                                    break;

                                case "Date de livraison":
                                    onClickTriDate();
                                    break;

                                case "Fournisseur":
                                    onClickTriFournisseur();
                                    break;
                            }
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
                    Alerte.afficherAlerte(ServiceReceptionPuiActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP Service Récupération Quarantaine", "alerte");
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                return headers;
            }
        };
    }


    @NonNull
    private Intent getRetourVersServiceConnexionDirectIntent() {
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceReceptionPuiActivity.this, ServiceConnexionDirecteActivity.class);
        Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", "Commande");

        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
        return retourVersServiceConnexionDirectIntent;
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

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, commandeReceptionPUIAdapter, null, "Produit, Numéro, Fournisseur");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> {
            lancerScan();
            return true;
        });
        return true;
    }
    public void lancerScan()
    {
        Bundle scanDocumentBundle = ServiceReceptionPuiActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent scanDocumentIntent;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(ServiceReceptionPuiActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une réception");
            scanDocumentBundle.putString("Context", "Reception");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServiceReceptionPuiActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceReceptionPuiActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une réception");
                scanDocumentBundle.putString("Context", "Reception");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        resultScanDocument.launch(scanDocumentIntent);
    }

    public void afficherSnackBarPreparationReceptionPUI() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }

    public void onClickTriNumero()
    {
        tri_choisi = "Numéro de commande";
        commandeList.sort(Comparator.comparing(Commande::getNumero));

        commandeReceptionPUIAdapter = new ReceptionAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
        commandeListView.setAdapter(commandeReceptionPUIAdapter);
    }

    public void onClickTriDate()
    {
        tri_choisi = "Date de livraison";
        commandeList.sort(Comparator.comparing(Commande::getDate_Liv));

        commandeReceptionPUIAdapter = new ReceptionAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
        commandeListView.setAdapter(commandeReceptionPUIAdapter);
    }

    public void onClickTriFournisseur()
    {
        tri_choisi = "Fournisseur";
        commandeList.sort(Comparator.comparing(Commande::getFournisseur));

        commandeReceptionPUIAdapter = new ReceptionAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
        commandeListView.setAdapter(commandeReceptionPUIAdapter);
    }
}
