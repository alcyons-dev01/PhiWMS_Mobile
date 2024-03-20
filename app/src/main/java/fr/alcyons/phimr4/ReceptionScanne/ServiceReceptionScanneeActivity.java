package fr.alcyons.phimr4.ReceptionScanne;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.material.snackbar.Snackbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Classes.Commande;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.ListViewAdapters.CommandeReceptionPUIAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

import static fr.alcyons.phimr4.BaseDeDonnees.CommandeOpenHelper.viderTableCommandes;
import static fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper.viderTablePH_Reliquat;

/**
 * Created by olivier on 27/11/2017.
 */

public class ServiceReceptionScanneeActivity extends ServiceAvecConnexionActivity {

    Depot depotPUI;
    ListView commandeListView;
    CommandeReceptionPUIAdapter commandeReceptionPUIAdapter;
    JSONArray commandeJSONArray;
    JSONArray phReliquatJSONArray;
    List<Commande> commandeList;
    PackageManager pm;

    boolean connexionDirecte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_reception_pui);

        depotPUI = gestionnaireDepot.getDepotPUI(db);
        pm = ServiceReceptionScanneeActivity.this.getPackageManager();
        // Gestion de la liste
        commandeListView = (ListView) findViewById(R.id.listeView);
        commandeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commande commandeSelectionne = (Commande) commandeReceptionPUIAdapter.getItem(position);

                if (commandeSelectionne != null) {
                    Intent serviceReceptionPui_Intent = new Intent(ServiceReceptionScanneeActivity.this, DetailReceptionScanneeActivity.class);

                    Bundle serviceReceptionPui_Bundle = ServiceReceptionScanneeActivity.super.getBundle();
                    serviceReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                    serviceReceptionPui_Intent.putExtras(serviceReceptionPui_Bundle);
                    ServiceReceptionScanneeActivity.this.startActivity(serviceReceptionPui_Intent);
                    ServiceReceptionScanneeActivity.this.finish();
                }
            }
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);
    }

    @Override
    public void onResume() {
        super.onResume();

        commandeList = new ArrayList<>();

        //* Code nécessaire afin de réaliser une requête à l' API *//*
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceReceptionScanneeActivity.this) && passageParOnCreate && !connexionDirecte) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServiceReceptionScanneeActivity.this, "Veuillez patienter", "Synchronisation des commandes en cours");
            }

            RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ServiceReceptionScanneeActivity.this);
            String urlRequete = null;
            try {
                urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteCommandes + "depotreference/" + URLEncoder.encode(depotPUI.getDepot_Reference(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int resultCount = response.getInt("resultCount");
                                if (resultCount == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(ServiceReceptionScanneeActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ServiceReceptionScanneeActivity.this.finishAffinity();
                                        Intent intent = new Intent(ServiceReceptionScanneeActivity.this, AuthentificationActivity.class);
                                        ServiceReceptionScanneeActivity.this.startActivity(intent);
                                    } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(ServiceReceptionScanneeActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", "alerte");
                                        ServiceReceptionScanneeActivity.this.finishAffinity();
                                        Intent intent = new Intent(ServiceReceptionScanneeActivity.this, AuthentificationActivity.class);
                                        ServiceReceptionScanneeActivity.this.startActivity(intent);
                                    } else if (erreur.contentEquals("Aucun PH_Commande trouvé")) {
                                        Toast toast = Toast.makeText(ServiceReceptionScanneeActivity.this, "Aucune Commande trouvé", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } else {
                                        Alerte.afficherAlerte(ServiceReceptionScanneeActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Reception PUI", "alerte");
                                    }
                                } else {
                                    commandeJSONArray = response.getJSONArray("PH_Commandes");
                                    viderTableCommandes(db);
                                    viderTablePH_Reliquat(db);

                                    for (int i = 0; i < commandeJSONArray.length(); i++) {
                                        JSONObject commandeJSONObject = commandeJSONArray.getJSONObject(i);

                                        Commande commandeCourant = new Commande(commandeJSONObject);

                                        phReliquatJSONArray = commandeJSONObject.getJSONArray("ph_reliquat");

                                        boolean phReliquatPresent = false;

                                        for (int j = 0; j < phReliquatJSONArray.length(); j++) {

                                            PH_Reliquat reliquatCourant = new PH_Reliquat((phReliquatJSONArray.getJSONObject(j)));


                                            long phReliquatPHiMR4ID = gestionnairePH_Reliquat.insererPH_ReliquatEnBDD(db, reliquatCourant);
                                            if (phReliquatPHiMR4ID != -1) {
                                                phReliquatPresent = true;
                                            }
                                        }

                                        if (phReliquatPresent) {
                                            long rowID = gestionnaireCommande.insererUneCommandeEnBDD(db, commandeCourant);
                                            if (rowID != -1) {
                                                commandeList.add(commandeCourant);
                                            }
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            handler.sendMessage(handler.obtainMessage());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error");
                            Alerte.afficherAlerte(ServiceReceptionScanneeActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP Service Reception PUI", "alerte");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    return headers;
                }
            };
            obreq.setRetryPolicy(retryPolicy);
            requestQueueDestructionUtilisateur.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            passageParOnCreate = false;
            //lancerScan();
            lancerActivitySuivante();
        } else {
            commandeList = gestionnaireCommande.getAllCommandesPUI(db, depotPUI.getDepot_Reference());
            if (commandeList.size() == 0) {
                connexionNecessaire();
                return;
            }
            else
            {
                if(connexionDirecte)
                    connexionDirecte = !connexionDirecte;
                lancerActivitySuivante();
            }
        }

        Collections.sort(commandeList, new Comparator<Commande>() {
            @Override
            public int compare(Commande o1, Commande o2) {
                return o2.getDate_Liv().compareTo(o1.getDate_Liv());
            }
        });

        invalidateOptionsMenu();
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, commandeReceptionPUIAdapter, null, "Rechercher...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
        Bundle scanDocumentBundle = ServiceReceptionScanneeActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent = null;
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ServiceReceptionScanneeActivity.this, ScannerSearchOnlyActivity.class);
            scanDocumentBundle.putInt("scannerContextInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putBoolean("activerTextSuppression", true);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                scanDocumentIntent = new Intent(ServiceReceptionScanneeActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceReceptionScanneeActivity.this, ScannerSearchOnlyActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putBoolean("activerTextSuppression", true);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceReceptionScanneeActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }

    public void lancerActivitySuivante()
    {
        Bundle scanProduitBundle = ServiceReceptionScanneeActivity.super.getBundle();

        Intent scanProduitIntent = new Intent(ServiceReceptionScanneeActivity.this, ScanProduitActivity.class);
        scanProduitIntent.putExtras(scanProduitBundle);
        ServiceReceptionScanneeActivity.this.startActivity(scanProduitIntent);
        ServiceReceptionScanneeActivity.this.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_DOCUMENT: {
                if (data != null) {
                    String code = data.getExtras().getString("code");
                    if (code != null) {
                        Commande commandeSelectionne = CommandeOpenHelper.getCommandeByNumero(db, code);
                        if(commandeSelectionne == null)
                        {
                            afficherSnackBarPreparationReceptionScannee();
                            /* Code nécessaire à l'affichage de la liste */
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                            commandeReceptionPUIAdapter = new CommandeReceptionPUIAdapter(ServiceReceptionScanneeActivity.this, db, commandeList);
                            commandeListView.setDivider(footer);
                            commandeListView.setAdapter(commandeReceptionPUIAdapter);

                            if (commandeList.size() == 0) {
                                vide = true;
                                nomServiceVide = "Réception PUI";
                                ServiceReceptionScanneeActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                        else
                        {
                            Intent serviceReceptionPui_Intent = new Intent(ServiceReceptionScanneeActivity.this, DetailReceptionScanneeActivity.class);
                            Bundle serviceReceptionPui_Bundle = ServiceReceptionScanneeActivity.super.getBundle();
                            serviceReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                            serviceReceptionPui_Intent.putExtras(serviceReceptionPui_Bundle);
                            ServiceReceptionScanneeActivity.this.startActivity(serviceReceptionPui_Intent);
                            ServiceReceptionScanneeActivity.this.finish();
                        }
                    } else {
                               /* Code nécessaire à l'affichage de la liste */
                        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                        commandeReceptionPUIAdapter = new CommandeReceptionPUIAdapter(ServiceReceptionScanneeActivity.this, db, commandeList);
                        commandeListView.setDivider(footer);
                        commandeListView.setAdapter(commandeReceptionPUIAdapter);

                        if (commandeList.size() == 0) {
                            vide = true;
                            nomServiceVide = "Réception PUI";
                            ServiceReceptionScanneeActivity.this.finish();
                        }

                        invalidateOptionsMenu();
                    }
                }
                else
                {
                    /* Code nécessaire à l'affichage de la liste */
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                    commandeReceptionPUIAdapter = new CommandeReceptionPUIAdapter(ServiceReceptionScanneeActivity.this, db, commandeList);
                    commandeListView.setDivider(footer);
                    commandeListView.setAdapter(commandeReceptionPUIAdapter);

                    if (commandeList.size() == 0) {
                        vide = true;
                        nomServiceVide = "Réception PUI";
                        ServiceReceptionScanneeActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                }
                break;
            }
        }
    }

    public void afficherSnackBarPreparationReceptionScannee() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);;

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }

}
