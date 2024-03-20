package fr.alcyons.phimr4.Stock;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;

import android.view.View;
import android.widget.ImageView;
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
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.StockAdapter;
import fr.alcyons.phimr4.ListViewAdapters.Stock_Lot_EmplacementAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

public class newDetailStockActivity extends ServiceAvecConnexionActivity {

    Depot depotSelectionne;
    List<Stock> stockList;
    TextView nomDepotTextView;
    JSONArray stockJSONArray;

    Intent listeStockIntent;

    PackageManager pm;

    int produitid;
    Produit produitCourant;
    List<Stock_Lot_Emplacement_Light> stockLotEmplacementLightList = new ArrayList<>();
    ListView stockLotEmplacementLightListView;
    Stock_Lot_EmplacementAdapter stockLotEmplacementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stock);

        //gestion du package manager
        pm = newDetailStockActivity.this.getPackageManager();
        nomDepotTextView = (TextView) findViewById(R.id.nomDepot);
        produitid = intent.getExtras().getInt("produitID");
        produitCourant = ProduitOpenHelper.getProduitByID(db, produitid);
        depotSelectionne = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotUID_Selectionne"));
        stockLotEmplacementLightListView = (ListView) findViewById(R.id.listeView);
        if (depotSelectionne != null) {
            String nomDepot = depotSelectionne.getNom();
            if(utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS") && depotSelectionne.getStructure().contentEquals("PAD"))
            {
                String[] tab_nom = depotSelectionne.getNom().split(" ");
                String nom = tab_nom[0];
                if(nom.length() > 2)
                {
                    nom = nom.substring(0, 3)+"...";
                }
                else
                {
                    nom = nom +"...";
                }
                String prenom = tab_nom[1];
                if(prenom.length() > 2)
                {
                    prenom = prenom.substring(0, 3)+"...";
                }
                else
                {
                    prenom = prenom+"...";
                }
                nomDepot = nom+" "+prenom;
            }
            nomDepotTextView.setText(nomDepot);
        }

        ((TextView) findViewById(R.id.nomProduit)).setText(produitCourant.getDesignation_interne());
        ((TextView) findViewById(R.id.referenceProduit)).setText(produitCourant.getRef_fourni());
        ((TextView) findViewById(R.id.nomFournisseur)).setText(produitCourant.getFournisseur());
        //boutonRechercheDataMatrix = (FloatingActionButton) findViewById(R.id.boutonRechercheDataMatrix);

    }

    @Override
    public void onResume() {
        super.onResume();

        stockList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(newDetailStockActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(newDetailStockActivity.this, "Veuillez patienter", "Synchronisation des stocks en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(newDetailStockActivity.this);
            String urlRequete = "";

            try {
                urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceStock + "/depot/" + URLEncoder.encode(depotSelectionne.getDepot_Reference(), "utf-8")+"/produit/"+String.valueOf(produitid);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            stockJSONArray = new JSONArray();

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
                                        Alerte.afficherAlerte(newDetailStockActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        newDetailStockActivity.this.finishAffinity();
                                        listeStockIntent = new Intent(newDetailStockActivity.this, AuthentificationActivity.class);
                                        newDetailStockActivity.this.startActivity(listeStockIntent);
                                    } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(newDetailStockActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", "alerte");
                                        newDetailStockActivity.this.finishAffinity();
                                        listeStockIntent = new Intent(newDetailStockActivity.this, AuthentificationActivity.class);
                                        newDetailStockActivity.this.startActivity(listeStockIntent);
                                    } else if (erreur.equals("Aucun PH_Stock trouvé")) {
                                        Toast toast = Toast.makeText(newDetailStockActivity.this, "Aucun Stock trouvé", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } else {
                                        Alerte.afficherAlerte(newDetailStockActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete liste stock", "alerte");
                                    }
                                } else {
                                    //viderTablesConcernees();
                                    stockJSONArray = response.getJSONArray("PH_Stocks");
                                    for (int i = 0; i < stockJSONArray.length(); i++) {
                                        JSONObject stockJSONObject = stockJSONArray.getJSONObject(i);
                                        Stock stock = new Stock(stockJSONArray.getJSONObject(i));
                                        Stock stock_courant = StockOpenHelper.getStockByProduitEtDepot(db, produitCourant, depotSelectionne);
                                        if(stock_courant == null)
                                        {
                                            long rowID = gestionnaireStock.insererUnStockEnBDD(db, stock);
                                            if (rowID != -1) {
                                                stockList.add(stock);
                                                JSONArray ph_stock_lot_emplacement_JSONArray = stockJSONObject.getJSONArray("ph_stock_lot_emplacement");
                                                for (int k = 0; k < ph_stock_lot_emplacement_JSONArray.length(); k++) {
                                                    Stock_Lot_Emplacement_Light stock_lot_emplacement_light_courant = new Stock_Lot_Emplacement_Light(ph_stock_lot_emplacement_JSONArray.getJSONObject(k));
                                                    Stock_Lot_Emplacement_Light check_stock = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stock_lot_emplacement_light_courant.get_UID());
                                                    if(check_stock == null)
                                                    {
                                                        gestionnaireStock_Lot_Emplacement.insererUnStock_Lot_EmplacementEnBDD(db, stock_lot_emplacement_light_courant);
                                                    }
                                                }
                                            }
                                        }
                                        else
                                        {
                                            stockList.add(stock_courant);
                                            JSONArray ph_stock_lot_emplacement_JSONArray = stockJSONObject.getJSONArray("ph_stock_lot_emplacement");
                                            for (int k = 0; k < ph_stock_lot_emplacement_JSONArray.length(); k++) {
                                                Stock_Lot_Emplacement_Light stock_lot_emplacement_light_courant = new Stock_Lot_Emplacement_Light(ph_stock_lot_emplacement_JSONArray.getJSONObject(k));
                                                Stock_Lot_Emplacement_Light check_stock = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stock_lot_emplacement_light_courant.get_UID());
                                                if(check_stock == null)
                                                {
                                                    gestionnaireStock_Lot_Emplacement.insererUnStock_Lot_EmplacementEnBDD(db, stock_lot_emplacement_light_courant);
                                                }
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
                            Alerte.afficherAlerte(newDetailStockActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP liste stock", "alerte");
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
            stockList = gestionnaireStock.getStockByProduit(db, produitCourant);
            if (stockList.size() == 0) {
                connexionNecessaire();
                return;
            }
        }

        if (stockList != null && stockList.size() != 0) {
            double aDouble = stockList.get(0).getQuantite_Actuelle();
            int qteActuelle = (int) aDouble;
            ((TextView) findViewById(R.id.qteActuelle)).setText(String.valueOf(qteActuelle));
            stockLotEmplacementLightList = gestionnaireStock_Lot_Emplacement.getStockLotEmplacementByStock(db, stockList.get(0));
            stockLotEmplacementAdapter = new Stock_Lot_EmplacementAdapter(newDetailStockActivity.this, stockLotEmplacementLightList);
            stockLotEmplacementLightListView.setAdapter(stockLotEmplacementAdapter);
            stockLotEmplacementLightListView.setDivider(footer);

            if(stockLotEmplacementLightList.size() == 0)
            {
                ((TextView) findViewById(R.id.aucunStock)).setVisibility(View.VISIBLE);
            }

            invalidateOptionsMenu();
        }

    }


    @Override
    public void onBackPressed()
    {
        Intent serviceStockIntent = new Intent(newDetailStockActivity.this, ListeReferenceActivity.class);
        Bundle serviceStockBundle = newDetailStockActivity.super.getBundle();
        serviceStockBundle.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
        serviceStockIntent.putExtras(serviceStockBundle);
        newDetailStockActivity.this.startActivity(serviceStockIntent);
        newDetailStockActivity.this.finish();
    }
}
