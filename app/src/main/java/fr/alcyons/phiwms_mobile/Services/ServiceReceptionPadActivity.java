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
import fr.alcyons.phiwms_mobile.Reception.DetailReceptionActivity;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
public class ServiceReceptionPadActivity extends ServiceAvecConnexionActivity {

    Depot depotPUIPAD;
    ListView commandeListView;
    ReceptionAdapter commandeReceptionPADAdapter;
    JSONArray commandeJSONArray;
    JSONArray phReliquatJSONArray;
    List<Commande> commandeList;
    PackageManager pm;

    String tri_choisi;

    ActivityResultLauncher<Intent> resultScanDocument;
    boolean connexionDirecte;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        pm = ServiceReceptionPadActivity.this.getPackageManager();
        depotPUIPAD = DepotOpenHelper.getDepotPUIPAD(db);
        tri_choisi =  ParametreUtilisateurOpenHelper.getChoixTriReception(db);

        if(tri_choisi == null)
        {
            ParametreUtilisateurOpenHelper.mettreAJourTriReception(db, 0, "Numéro de commande");
            tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriReception(db);
        }

        // Gestion de la liste
        commandeListView = (ListView) findViewById(R.id.listeView);
        commandeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commande commandeSelectionne = (Commande) commandeReceptionPADAdapter.getItem(position);

                if (commandeSelectionne != null) {
                    Intent serviceReceptionPui_Intent = new Intent(ServiceReceptionPadActivity.this, DetailReceptionActivity.class);

                    Bundle serviceReceptionPui_Bundle = ServiceReceptionPadActivity.super.getBundle();
                    serviceReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                    serviceReceptionPui_Bundle.putInt("serviceSelectionneID", serviceActuel.getId());
                    serviceReceptionPui_Intent.putExtras(serviceReceptionPui_Bundle);
                    ServiceReceptionPadActivity.this.startActivity(serviceReceptionPui_Intent);
                    ServiceReceptionPadActivity.this.finish();
                }
            }
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        resultScanDocument = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == CodesEchangesActivites.RESULT_OK) {
                        if (data != null)
                        {
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
                                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                                    commandeReceptionPADAdapter = new ReceptionAdapter(ServiceReceptionPadActivity.this, db, commandeList);
                                    commandeListView.setDivider(footer);
                                    commandeListView.setAdapter(commandeReceptionPADAdapter);

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
                                    Intent serviceReceptionPui_Intent = new Intent(ServiceReceptionPadActivity.this, DetailReceptionActivity.class);
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
                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                                commandeReceptionPADAdapter = new ReceptionAdapter(ServiceReceptionPadActivity.this, db, commandeList);
                                commandeListView.setDivider(footer);
                                commandeListView.setAdapter(commandeReceptionPADAdapter);

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
                        else
                        {
                            /* Code nécessaire à l'affichage de la liste */
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                            commandeReceptionPADAdapter = new ReceptionAdapter(ServiceReceptionPadActivity.this, db, commandeList);
                            commandeListView.setDivider(footer);
                            commandeListView.setAdapter(commandeReceptionPADAdapter);

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

        //* Code nécessaire afin de réaliser une requête à l' API *//*
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
        }
        else
        {
            commandeList = CommandeOpenHelper.getAllCommandesPUI(db, depotPUIPAD.getDepot_Reference());
            if (commandeList.isEmpty()) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceReceptionPadActivity.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Commande");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
                    ServiceReceptionPadActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceReceptionPadActivity.this.finish();
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
                    lancerScan();
                    connexionDirecte = !connexionDirecte;
                }
            }

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
            invalidateOptionsMenu();

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
                                Alerte.afficherAlerteInformation(ServiceReceptionPadActivity.this, getLayoutInflater(), "Alerte", "Votre session est invalide, veuillez vous reconnecter.", false, true);
                            } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(ServiceReceptionPadActivity.this, getLayoutInflater(), "Alerte", "Votre session a expirée, veuillez vous reconnecter.", false, true);
                            }  else {
                                arreterSpinner();
                                Alerte.afficherAlerteInformation(ServiceReceptionPadActivity.this, getLayoutInflater(), "Erreur", "Aucune réception PAD à traiter", false, true);
                            }
                        } else {
                            commandeJSONArray = response.getJSONArray("PH_Commandes");

                            viderTablesConcernees();

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
                                        if(commandeCourant.getRef_Depot_Dest().contains("-PAD"))
                                        {
                                            commandeList.add(commandeCourant);
                                        }
                                    }
                                }
                            }

                            if(passageParOnCreate && !commandeList.isEmpty())
                            {
                                //lancerScan();
                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));
                                ((TextView) findViewById(R.id.titre)).setText("Réceptions");
                                commandeReceptionPADAdapter = new ReceptionAdapter(ServiceReceptionPadActivity.this, db, commandeList);
                                commandeListView.setDivider(footer);
                                commandeListView.setAdapter(commandeReceptionPADAdapter);
                            }
                            else if(commandeList.isEmpty())
                            {
                                vide = true;
                                nomServiceVide = "Réception PAD";
                                retourNavigation();
                            }

                            passageParOnCreate = false;
                            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
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
                            invalidateOptionsMenu();
                        }
                    }
                    catch (Throwable t)
                    {
                        Log.e(TAG, "Error JSON", t);
                    }
                }, error -> {
                    // TODO: Handle error
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
        for (Commande commande : CommandeOpenHelper.getAllCommandes(db))
        {
            if(!commande.getNumero().contentEquals("RECALCYONS01"))
            {
                for (PH_Reliquat ph_reliquat : PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, commande.getNumero()))
                {
                    PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, ph_reliquat);
                }
                CommandeOpenHelper.supprimerUneCommande(db, commande);
            }
        }
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, commandeReceptionPADAdapter, null, "Rechercher...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                lancerScan();
                return true;
            }
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
        Bundle scanDocumentBundle = ServiceReceptionPadActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent scanDocumentIntent = null;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ServiceReceptionPadActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une réception");
            scanDocumentBundle.putString("Context", "Reception");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServiceReceptionPadActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
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
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);;

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }

    public void onClickTriNumero()
    {
        tri_choisi = "Numéro de commande";
        commandeReceptionPADAdapter.commandeList.sort(new Comparator<Commande>() {
            @Override
            public int compare(Commande o1, Commande o2) {
                return o1.getNumero().compareTo(o2.getNumero());
            }
        });


        commandeReceptionPADAdapter.notifyDataSetChanged();
    }

    public void onClickTriDate()
    {
        tri_choisi = "Date de livraison";
        commandeReceptionPADAdapter.commandeList.sort(new Comparator<Commande>() {
            @Override
            public int compare(Commande o1, Commande o2) {
                return o2.getDate_Liv().compareTo(o1.getDate_Liv());
            }
        });

        commandeReceptionPADAdapter.notifyDataSetChanged();
    }

    public void onClickTriFournisseur()
    {
        tri_choisi = "Fournisseur";
        commandeReceptionPADAdapter.commandeList.sort(new Comparator<Commande>() {
            @Override
            public int compare(Commande o1, Commande o2) {
                return o1.getFournisseur().compareTo(o2.getFournisseur());
            }
        });

        commandeReceptionPADAdapter.notifyDataSetChanged();
    }
}