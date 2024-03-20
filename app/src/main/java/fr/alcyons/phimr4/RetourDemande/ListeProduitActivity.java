package fr.alcyons.phimr4.RetourDemande;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.Produit_RetourDemandeAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by olivier on 05/01/2018.
 */

public class ListeProduitActivity extends ServiceAvecConnexionActivity {

    Depot depotSelectionne;

    JSONArray stockJSONArray;

    JSONArray dateJSONArray;

    Intent listeStockIntent;

    Boolean back;

    String dateProchaineLivraison;

    List<String> listeProchaineDate;

    List<Stock> listeStock;
    ListView listViewProduit;
    Produit_RetourDemandeAdapter adapter;

    PackageManager pm;

    public View.OnClickListener clicBoutonNext = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Integer tailleListe = adapter.produitSelect.size();

            if (tailleListe == 0) {
                Toast toast = Toast.makeText(ListeProduitActivity.this, "Aucun produit sélectionné", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                if (back == false) {
                    Intent newIntent = new Intent(ListeProduitActivity.this, DetailRetourDemandeActivity.class);
                    Bundle extras = ListeProduitActivity.super.getBundle();
                    extras.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
                    extras.putIntegerArrayList("ListeProduit", (ArrayList<Integer>) adapter.produitSelect);
                    if (listeProchaineDate != null || listeProchaineDate.size() != 0) {
                        extras.putStringArrayList("ListeDate", (ArrayList<String>) listeProchaineDate);
                    }

                    newIntent.putExtras(extras);
                    ListeProduitActivity.this.startActivity(newIntent);
                    ListeProduitActivity.this.finish();
                } else {
                    Intent resultIntent = new Intent();
                    Bundle extras = new Bundle();
                    extras.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
                    extras.putIntegerArrayList("ListeProduit", (ArrayList<Integer>) adapter.produitSelect);
                    if (listeProchaineDate != null || listeProchaineDate.size() != 0) {
                        extras.putStringArrayList("ListeDate", (ArrayList<String>) listeProchaineDate);
                    }
                    extras.putBoolean("Finish", false);
                    resultIntent.putExtras(extras);
                    setResult(CodesEchangesActivites.RESULT_RETOUR_DEMANDE, resultIntent);
                    ListeProduitActivity.this.finish();
                }

            }
        }
    };
    TextView nb_prod;
    FloatingActionButton boutonNext;
    LinearLayout SelectionParScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_produit_retour_demande);

        //Gestion du package manager
        pm = ListeProduitActivity.this.getPackageManager();

        // Récupération du dépot grace aux variables globales
        depotSelectionne = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotUID_Selectionne"));

        if (depotSelectionne != null) {
            // Affichage des informations de base
            ((TextView) findViewById(R.id.nomDepot)).setText(depotSelectionne.getDepot_Reference());

            // Récupération et initialisation du nombre de produit sélectionné
            nb_prod = (TextView) findViewById(R.id.nb_prod_select);
            nb_prod.setText("0");

            //Récupération LinearLayout permettant d'ouvrir la page de Scan
            SelectionParScan = ((LinearLayout) findViewById(R.id.SelectionParScan));

            SelectionParScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent ListeProduitIntent = null;
                    Bundle ListeProcduitBundle = new Bundle();

                    if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                    {
                        ListeProduitIntent = new Intent(ListeProduitActivity.this, ScannerSearchOnlyActivity.class);
                        ListeProcduitBundle.putBoolean("modeCumule", true);
                    }
                    else
                    {
                        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                        {
                            ListeProduitIntent = new Intent(ListeProduitActivity.this, BarcodeCaptureActivity.class);
                            ListeProcduitBundle.putBoolean("cumule", true);
                        }
                        else
                        {
                            ListeProduitIntent = new Intent(ListeProduitActivity.this, ScannerSearchOnlyActivity.class);
                            ListeProcduitBundle.putBoolean("modeCumule", true);
                        }
                    }

                    ListeProcduitBundle.putBoolean("modeRafale", true);
                    ListeProcduitBundle.putBoolean("isBoutonSuppressionExistant", true);
                    ListeProcduitBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    ListeProcduitBundle.putInt("serviceSelectionneID", serviceActuel.getId());
                    ListeProduitIntent.putExtras(ListeProcduitBundle);
                    ListeProduitActivity.this.startActivityForResult(ListeProduitIntent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
                }
            });



            // Récupération et initialisation du bouton permettant de passer à l'activité suivante
            boutonNext = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.boutonSave);
            boutonNext.setOnClickListener(clicBoutonNext);

            // Récupération de la listView
            listViewProduit = (ListView) findViewById(R.id.listeView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        listeStock = new ArrayList<>();

        listeProchaineDate = new ArrayList<>();

        back = intent.getBooleanExtra("Back", false);

     /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ListeProduitActivity.this) && passageParOnCreate && !back) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ListeProduitActivity.this, "Veuillez patienter", "Synchronisation des stocks en cours");
            }

            com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(ListeProduitActivity.this);
            String urlRequete = "";

            urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceStock + "/retourDemande/depot/" + depotSelectionne.getDepot_UID();

            stockJSONArray = new JSONArray();
            dateJSONArray = new JSONArray();

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int resultCount = response.getInt("resultCount");
                                stockJSONArray = response.getJSONArray("PH_Stocks");
                                if (resultCount == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(ListeProduitActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ListeProduitActivity.this.finishAffinity();
                                        listeStockIntent = new Intent(ListeProduitActivity.this, AuthentificationActivity.class);
                                        ListeProduitActivity.this.startActivity(listeStockIntent);
                                    } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(ListeProduitActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", "alerte");
                                        ListeProduitActivity.this.finishAffinity();
                                        listeStockIntent = new Intent(ListeProduitActivity.this, AuthentificationActivity.class);
                                        ListeProduitActivity.this.startActivity(listeStockIntent);
                                    } else if (erreur.equals("Aucun PH_Stock trouvé")) {
                                        Toast toast = Toast.makeText(ListeProduitActivity.this, "Aucun Stock trouvé", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } else if (erreur.equals("Problème récupération dépôt")) {
                                        Toast toast = Toast.makeText(ListeProduitActivity.this, "Aucun dépôt trouvé", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        Alerte.afficherAlerte(ListeProduitActivity.this, "Erreur Requete", "Aucun dépôt trouvé", "alerte");
                                        ListeProduitActivity.this.finish();
                                    } else {
                                        Alerte.afficherAlerte(ListeProduitActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete liste stock", "alerte");
                                    }
                                } else {
                                    viderTablesConcernees();
                                    for (int i = 0; i < stockJSONArray.length(); i++) {
                                        JSONObject stockJSONObject = stockJSONArray.getJSONObject(i);
                                        Stock stock = new Stock(stockJSONArray.getJSONObject(i));
                                        long rowID = gestionnaireStock.insererUnStockEnBDD(db, stock);
                                        if (rowID != -1) {
                                            JSONArray ph_stock_lot_emplacement_JSONArray = stockJSONObject.getJSONArray("ph_stock_lot_emplacement");
                                            for (int k = 0; k < ph_stock_lot_emplacement_JSONArray.length(); k++) {
                                                gestionnaireStock_Lot_Emplacement.insererUnStock_Lot_EmplacementEnBDD(db, new Stock_Lot_Emplacement_Light(ph_stock_lot_emplacement_JSONArray.getJSONObject(k)));
                                            }
                                        }
                                    }

                                    // Date de prochaine livraison
                                    dateJSONArray = response.getJSONArray("EVENTS");
                                    Date date = new Date();
                                    DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        for (int i = 0; i < dateJSONArray.length(); i++) {
                                            JSONObject dateCourante = dateJSONArray.getJSONObject(i);
                                            String dateEvent = dateCourante.getString("Date_event");
                                            date = dateDecodeur.parse(dateEvent);
                                            dateProchaineLivraison = dateFormat.format(date);
                                            listeProchaineDate.add(dateProchaineLivraison);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
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
                            Alerte.afficherAlerte(ListeProduitActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP liste stock", "alerte");
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
            requestQueue.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            passageParOnCreate = false;
        } else {
            listeStock = gestionnaireStock.getStockByDepot(db, depotSelectionne);
            if (listeStock.size() == 0) {
                connexionNecessaire();
                return;
            }
        }

        // Récupération de tous les produit
        listeStock = gestionnaireStock.getStockByDepot(db, depotSelectionne);


        Collections.sort(listeStock, new Comparator<Stock>() {
            @Override
            public int compare(Stock o1, Stock o2) {
                return o1.getDesignation().compareTo(o2.getDesignation());
            }
        });

        // Affichage du nombre de produit
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeStock.size()));

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        List<Integer> tempListe = new ArrayList<>();
        if (back) {
            tempListe = intent.getIntegerArrayListExtra("ListeProduit");
        }
        adapter = new Produit_RetourDemandeAdapter(ListeProduitActivity.this, listeStock, nb_prod, tempListe, db);
        listViewProduit.setAdapter(adapter);

        invalidateOptionsMenu();

    }

    public void viderTablesConcernees() {
        gestionnaireStock.viderTableStocks(db);
        gestionnaireStock_Lot_Emplacement.viderTableStock_Lot_Emplacements(db);
    }


    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Désignation produit...");
        return true;
    }

    @Override
    public void onBackPressed() {
        if (back == false) {
            ListeProduitActivity.this.finish();
        } else {
            Intent resultIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putBoolean("Finish", true);
            resultIntent.putExtras(extras);
            setResult(CodesEchangesActivites.RESULT_RETOUR_DEMANDE, resultIntent);
            ListeProduitActivity.this.finish();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH: {
                List<String> listDesignation = new ArrayList<>();
                List<Produit> produit;
                produit = new ArrayList<>();
                listDesignation = data.getStringArrayListExtra("listeString");
                if(listDesignation == null)
                {
                    listDesignation = data.getStringArrayListExtra("stringList");
                }

                if(listDesignation != null)
                {
                    Iterator<String> iterator = listDesignation.iterator();
                    while (iterator.hasNext()) {
                        String i = iterator.next();

                        Map<String, String> codeGTIN = OutilsDecodage.decouperGTIN(i);
                        if (codeGTIN.size() != 1) {
                            produit = gestionnaireProduit.getProduitsParGTIN(db, codeGTIN.get(OutilsDecodage.codeGtin));
                        }
                        else
                        {
                            produit = gestionnaireProduit.getProduitsParCodeInconnue(db, i);
                        }

                        for (Produit produitCourant : produit) {
                            Iterator<Stock> stockIterator = listeStock.iterator();
                            while (stockIterator.hasNext()) {
                                Stock StockCourant = stockIterator.next();
                                if (StockCourant.getProduit_UID() == produitCourant.getID_produit()) {
                                    if (adapter.produitSelect.indexOf(StockCourant.getProduit_UID()) == -1) {
                                        adapter.produitSelect.add(StockCourant.getProduit_UID());
                                    }

                                    break;
                                }
                            }
                        }
                    }

                    if (adapter.produitSelect.size() > 0) {
                        Intent produitScannerIntent = new Intent(ListeProduitActivity.this, DetailRetourDemandeActivity.class);
                        Bundle extras = ListeProduitActivity.super.getBundle();
                        extras.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
                        extras.putIntegerArrayList("ListeProduit", (ArrayList<Integer>) adapter.produitSelect);
                        if (listeProchaineDate != null || listeProchaineDate.size() != 0) {
                            extras.putStringArrayList("ListeDate", (ArrayList<String>) listeProchaineDate);
                        }
                        produitScannerIntent.putExtras(extras);
                        ListeProduitActivity.this.startActivity(produitScannerIntent);
                        ListeProduitActivity.this.finish();
                    }
                    else
                    {
                        Toast.makeText(ListeProduitActivity.this, "Référence non présente en stock", Toast.LENGTH_SHORT).show();

                    }
                }

                break;
            }
        }
    }

}
