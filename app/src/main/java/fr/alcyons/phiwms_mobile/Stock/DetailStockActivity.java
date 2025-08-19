package fr.alcyons.phiwms_mobile.Stock;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Stock_Lot_EmplacementAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class DetailStockActivity extends ServiceAvecConnexionActivity {

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
        pm = DetailStockActivity.this.getPackageManager();
        nomDepotTextView = (TextView) findViewById(R.id.nomDepot);
        produitid = Objects.requireNonNull(intent.getExtras()).getInt("produitID");
        produitCourant = ProduitOpenHelper.getProduitByID(db, produitid);
        depotSelectionne = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotUID_Selectionne"));
        stockLotEmplacementLightListView = (ListView) findViewById(R.id.listeView);
        if (depotSelectionne != null) {
            String nomDepot = getNomDepot();
            nomDepotTextView.setText(nomDepot);
        }

        ((TextView) findViewById(R.id.nomProduit)).setText(produitCourant.getDesignation_interne());
        ((TextView) findViewById(R.id.referenceProduit)).setText(produitCourant.getRef_fourni());
        ((TextView) findViewById(R.id.nomFournisseur)).setText(produitCourant.getFournisseur());


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent serviceStockIntent = new Intent(DetailStockActivity.this, ListeReferenceActivity.class);
                Bundle serviceStockBundle = DetailStockActivity.super.getBundle();
                serviceStockBundle.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
                serviceStockIntent.putExtras(serviceStockBundle);
                DetailStockActivity.this.startActivity(serviceStockIntent);
                DetailStockActivity.this.finish();
            }
        });
    }

    private String getNomDepot() {
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
        return nomDepot;
    }

    @Override
    public void onResume() {
        super.onResume();

        stockList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(DetailStockActivity.this, LayoutInflater.from(DetailStockActivity.this));;
            }

            RequestQueue requestQueue = Volley.newRequestQueue(DetailStockActivity.this);
            String urlRequete = "";

            try {
                urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceStock + "/depot/" + URLEncoder.encode(depotSelectionne.getDepot_Reference(), "utf-8")+"/produit/"+String.valueOf(produitid);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            stockJSONArray = new JSONArray();

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    response -> {
                        try {
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                String erreur = response.getString("erreur");
                                if (erreur.equals(getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(DetailStockActivity.this, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                    DBOpenHelper.viderBasesDeDonnees(db);
                                    DetailStockActivity.this.finishAffinity();
                                    listeStockIntent = new Intent(DetailStockActivity.this, AuthentificationActivity.class);
                                    DetailStockActivity.this.startActivity(listeStockIntent);
                                } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                    Alerte.afficherAlerte(DetailStockActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", "alerte");
                                    DetailStockActivity.this.finishAffinity();
                                    listeStockIntent = new Intent(DetailStockActivity.this, AuthentificationActivity.class);
                                    DetailStockActivity.this.startActivity(listeStockIntent);
                                } else if (erreur.equals("Aucun PH_Stock trouvé")) {
                                    Toast toast = Toast.makeText(DetailStockActivity.this, "Aucun Stock trouvé", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                } else {
                                    Alerte.afficherAlerte(DetailStockActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete liste stock", "alerte");
                                }
                            } else {
                                stockJSONArray = response.getJSONArray("PH_Stocks");
                                for (int i = 0; i < stockJSONArray.length(); i++) {
                                    JSONObject stockJSONObject = stockJSONArray.getJSONObject(i);
                                    Stock stock = new Stock(stockJSONArray.getJSONObject(i));
                                    Stock stock_courant = StockOpenHelper.getStockByProduitEtDepot(db, produitCourant, depotSelectionne);
                                    if(stock_courant == null)
                                    {
                                        long rowID = StockOpenHelper.insererUnStockEnBDD(db, stock);
                                        if (rowID != -1) {
                                            stockList.add(stock);
                                            JSONArray ph_stock_lot_emplacement_JSONArray = stockJSONObject.getJSONArray("ph_stock_lot_emplacement");
                                            for (int k = 0; k < ph_stock_lot_emplacement_JSONArray.length(); k++) {
                                                Stock_Lot_Emplacement_Light stock_lot_emplacement_light_courant = new Stock_Lot_Emplacement_Light(ph_stock_lot_emplacement_JSONArray.getJSONObject(k));
                                                Stock_Lot_Emplacement_Light check_stock = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stock_lot_emplacement_light_courant.get_UID());
                                                if(check_stock == null)
                                                {
                                                    Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stock_lot_emplacement_light_courant);
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
                                                Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stock_lot_emplacement_light_courant);
                                            }
                                        }
                                    }
                                }

                                if (stockList != null && !stockList.isEmpty()) {
                                    double aDouble = stockList.get(0).getQuantite_Actuelle();
                                    int qteActuelle = (int) aDouble;
                                    ((TextView) findViewById(R.id.qteActuelle)).setText(String.valueOf(qteActuelle));
                                    stockLotEmplacementLightList = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByStock(db, stockList.get(0));
                                    stockLotEmplacementAdapter = new Stock_Lot_EmplacementAdapter(DetailStockActivity.this, stockLotEmplacementLightList);
                                    stockLotEmplacementLightListView.setAdapter(stockLotEmplacementAdapter);
                                    stockLotEmplacementLightListView.setDivider(footer);

                                    if(stockLotEmplacementLightList.isEmpty())
                                    {
                                        ((TextView) findViewById(R.id.aucunStock)).setVisibility(View.VISIBLE);
                                    }

                                    invalidateOptionsMenu();
                                    new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(DetailStockActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP liste stock", "alerte");
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    return headers;
                }
            };
            obreq.setRetryPolicy(retryPolicy);
            requestQueue.add(obreq);
            passageParOnCreate = false;
        } else {
            stockList = StockOpenHelper.getStockByProduit(db, produitCourant);
            if (stockList.isEmpty()) {
                connexionNecessaire();
            }

            if (stockList != null && !stockList.isEmpty()) {
                double aDouble = stockList.get(0).getQuantite_Actuelle();
                int qteActuelle = (int) aDouble;
                ((TextView) findViewById(R.id.qteActuelle)).setText(String.valueOf(qteActuelle));
                stockLotEmplacementLightList = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByStock(db, stockList.get(0));
                stockLotEmplacementAdapter = new Stock_Lot_EmplacementAdapter(DetailStockActivity.this, stockLotEmplacementLightList);
                stockLotEmplacementLightListView.setAdapter(stockLotEmplacementAdapter);
                stockLotEmplacementLightListView.setDivider(footer);

                if(stockLotEmplacementLightList.isEmpty())
                {
                    ((TextView) findViewById(R.id.aucunStock)).setVisibility(View.VISIBLE);
                }

                invalidateOptionsMenu();
            }
        }
    }
}
