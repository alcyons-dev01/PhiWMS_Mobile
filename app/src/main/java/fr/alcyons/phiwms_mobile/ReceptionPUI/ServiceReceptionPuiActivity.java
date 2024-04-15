package fr.alcyons.phiwms_mobile.ReceptionPUI;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.CommandeReceptionPUIAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

import static fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper.viderTableCommandes;
import static fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper.viderTablePH_Reliquat;

/**
 * Created by olivier on 27/11/2017.
 */

public class ServiceReceptionPuiActivity extends ServiceAvecConnexionActivity {

    //déclaration des variables
    String tri_choisi;
    JSONArray commandeJSONArray;
    JSONArray phReliquatJSONArray;
    List<Commande> commandeList;
    Depot depotPUI;
    boolean connexionDirecte;
    CommandeReceptionPUIAdapter commandeReceptionPUIAdapter;


    //déclaration des objets graphique
    ListView commandeListView;
    Spinner optionTri;
    ArrayAdapter<CharSequence> Spinneradapter;
    //déclaration des objets du support
    PackageManager pm;


    //initialisation des objets graphique
    private void initObjetGraphique()
    {
        //optionTri = (Spinner) findViewById(R.id.optionTri);
        commandeListView = (ListView) findViewById(R.id.listeView);
        pm = ServiceReceptionPuiActivity.this.getPackageManager();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_reception_pui);

        //initialisation des objets graphque
        initObjetGraphique();

        //gestion du spinner de tri
        tri_choisi= ParametreUtilisateurOpenHelper.getChoixTriReception(db);
        if(tri_choisi == null)
        {
            ParametreUtilisateurOpenHelper.mettreAJourTriReception(db, 0, "Numéro de commande");
            tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriReception(db);
        }


        depotPUI = gestionnaireDepot.getDepotPUI(db);
        // Gestion de la liste
        commandeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commande commandeSelectionne = (Commande) commandeReceptionPUIAdapter.getItem(position);

                if (commandeSelectionne != null) {
                    Intent serviceReceptionPui_Intent = new Intent(ServiceReceptionPuiActivity.this, DetailReceptionPuiActivity.class);
                    Bundle serviceReceptionPui_Bundle = ServiceReceptionPuiActivity.super.getBundle();
                    serviceReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                    serviceReceptionPui_Intent.putExtras(serviceReceptionPui_Bundle);
                    ServiceReceptionPuiActivity.this.startActivity(serviceReceptionPui_Intent);
                    ServiceReceptionPuiActivity.this.finish();
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
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceReceptionPuiActivity.this) && passageParOnCreate && !connexionDirecte) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServiceReceptionPuiActivity.this, "Veuillez patienter", "Synchronisation des commandes en cours");
            }

            RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ServiceReceptionPuiActivity.this);
            String urlRequete = null;
            try {
                urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteCommandes + "depotreference/" + URLEncoder.encode(depotPUI.getDepot_Reference(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    new Response.Listener<JSONObject>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int resultCount = response.getInt("resultCount");
                                if (resultCount == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(ServiceReceptionPuiActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
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
                                        Toast toast = Toast.makeText(ServiceReceptionPuiActivity.this, "Aucune Commande trouvé", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } else {
                                        Alerte.afficherAlerte(ServiceReceptionPuiActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Reception PUI", "alerte");
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
                            Alerte.afficherAlerte(ServiceReceptionPuiActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP Service Reception PUI", "alerte");
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
            if(passageParOnCreate)
            {
            }
            invalidateOptionsMenu();

            passageParOnCreate = false;
        } else {
            commandeList = gestionnaireCommande.getAllCommandesPUI(db, depotPUI.getDepot_Reference());
            if (commandeList.size() == 0) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceReceptionPuiActivity.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Commande");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
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
                    //lancerScan();
                    connexionDirecte = !connexionDirecte;
                }
            }
        }


        commandeReceptionPUIAdapter = new CommandeReceptionPUIAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
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
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                lancerScan();
                return true;
            }
        });
        return true;
    }


    public void lancerScan()
    {
        Bundle scanDocumentBundle = ServiceReceptionPuiActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent scanDocumentIntent = null;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ServiceReceptionPuiActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                scanDocumentIntent = new Intent(ServiceReceptionPuiActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceReceptionPuiActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }


        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceReceptionPuiActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
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
                            afficherSnackBarPreparationReceptionPUI();
                            /* Code nécessaire à l'affichage de la liste */
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                            commandeReceptionPUIAdapter = new CommandeReceptionPUIAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
                            commandeListView.setDivider(footer);
                            commandeListView.setAdapter(commandeReceptionPUIAdapter);

                            if (commandeList.size() == 0) {
                                vide = true;
                                nomServiceVide = "Réception PUI";
                                ServiceReceptionPuiActivity.this.finish();
                            }

                            invalidateOptionsMenu();
                        }
                        else
                        {
                            Intent serviceReceptionPui_Intent = new Intent(ServiceReceptionPuiActivity.this, DetailReceptionPuiActivity.class);
                            Bundle serviceReceptionPui_Bundle = ServiceReceptionPuiActivity.super.getBundle();
                            serviceReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                            serviceReceptionPui_Intent.putExtras(serviceReceptionPui_Bundle);
                            ServiceReceptionPuiActivity.this.startActivity(serviceReceptionPui_Intent);
                            ServiceReceptionPuiActivity.this.finish();
                        }
                    } else {
                               /* Code nécessaire à l'affichage de la liste */
                        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                        commandeReceptionPUIAdapter = new CommandeReceptionPUIAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
                        commandeListView.setDivider(footer);
                        commandeListView.setAdapter(commandeReceptionPUIAdapter);

                        if (commandeList.size() == 0) {
                            vide = true;
                            nomServiceVide = "Réception PUI";
                            ServiceReceptionPuiActivity.this.finish();
                        }

                        invalidateOptionsMenu();
                    }
                }
                else
                {
                    /* Code nécessaire à l'affichage de la liste */
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(commandeList.size()));

                    commandeReceptionPUIAdapter = new CommandeReceptionPUIAdapter(ServiceReceptionPuiActivity.this, db, commandeList);
                    commandeListView.setDivider(footer);
                    commandeListView.setAdapter(commandeReceptionPUIAdapter);

                    if (commandeList.size() == 0) {
                        vide = true;
                        nomServiceVide = "Réception PUI";
                        ServiceReceptionPuiActivity.this.finish();
                    }

                    invalidateOptionsMenu();
                }
                break;
            }
        }
    }

    public void afficherSnackBarPreparationReceptionPUI() {
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
        Collections.sort(commandeReceptionPUIAdapter.commandeList, new Comparator<Commande>() {
            @Override
            public int compare(Commande o1, Commande o2) {
                return o1.getNumero().compareTo(o2.getNumero());
            }
        });

        commandeReceptionPUIAdapter.notifyDataSetChanged();
    }

    public void onClickTriDate()
    {
        tri_choisi = "Date de livraison";
        Collections.sort(commandeReceptionPUIAdapter.commandeList, new Comparator<Commande>() {
            @Override
            public int compare(Commande o1, Commande o2) {
                return o2.getDate_Liv().compareTo(o1.getDate_Liv());
            }
        });

        commandeReceptionPUIAdapter.notifyDataSetChanged();
    }

    public void onClickTriFournisseur()
    {
        tri_choisi = "Fournisseur";
        Collections.sort(commandeReceptionPUIAdapter.commandeList, new Comparator<Commande>() {
            @Override
            public int compare(Commande o1, Commande o2) {
                return o1.getFournisseur().compareTo(o2.getFournisseur());
            }
        });

        commandeReceptionPUIAdapter.notifyDataSetChanged();
    }
}
