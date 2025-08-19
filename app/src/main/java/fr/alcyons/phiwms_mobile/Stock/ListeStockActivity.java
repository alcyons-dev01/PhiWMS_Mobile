package fr.alcyons.phiwms_mobile.Stock;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
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
import fr.alcyons.phiwms_mobile.ListViewAdapters.StockAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ListeStockActivity extends ServiceAvecConnexionActivity {

    Depot depotSelectionne;
    boolean contexteUtiliser;
    List<Stock> stockList;
    ListView stocklistView;
    StockAdapter stockAdapter;
    TextView nomDepotTextView;
    ImageView icone_gelocImageView;
    JSONArray stockJSONArray;

    Intent listeStockIntent;
    Bundle listeStockBundle;

    PackageManager pm;

    FloatingActionButton boutonRechercheDataMatrix;

    // Clic sur le stock renvoi à l'activité précédente
    View.OnClickListener onClickListenerDepotChoisir = v -> ListeStockActivity.this.finish();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_stock);

        //gestion du package manager
        pm = ListeStockActivity.this.getPackageManager();

        depotSelectionne = DepotOpenHelper.getDepotParID(db, Objects.requireNonNull(intent.getExtras()).getInt("depotUID_Selectionne"));
        contexteUtiliser = intent.getBooleanExtra("contexteUtiliser",false);

        nomDepotTextView = ((TextView) findViewById(R.id.nomDepot));
        nomDepotTextView.setOnClickListener(onClickListenerDepotChoisir);

        if (depotSelectionne != null) {
            String nomDepot = getNomDepot();
            nomDepotTextView.setText(nomDepot);
        }

        // Clic sur l'icone géolocalisation de la banniere
        icone_gelocImageView = ((ImageView) findViewById(R.id.icone_geloc));
        icone_gelocImageView.setOnClickListener(onClickListenerDepotChoisir);

        stocklistView = (ListView) findViewById(R.id.listeView);

        stocklistView.setOnItemClickListener((parent, view, position, id) -> {
            Stock stock_Selectionne = (Stock) stockAdapter.getItem(position);

            if (stock_Selectionne != null) {
                if (stock_Selectionne.getQuantite_Actuelle() > 0) {
                    listeStockIntent = new Intent(ListeStockActivity.this, DetailStockActivity.class);
                    listeStockBundle = ListeStockActivity.super.getBundle();
                    listeStockBundle.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
                    listeStockBundle.putSerializable("stock_Selectionne", stock_Selectionne);
                    listeStockBundle.putBoolean("contexteUtiliser",contexteUtiliser);
                    listeStockIntent.putExtras(listeStockBundle);
                    ListeStockActivity.this.startActivityForResult(listeStockIntent, CodesEchangesActivites.RETOUR_STOCK_LOT_EMPLACEMENT);
                }
            }
        });

        boutonRechercheDataMatrix = (FloatingActionButton) findViewById(R.id.boutonRechercheDataMatrix);

        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) && !Build.MANUFACTURER.contains("Zebra Technologies") && !Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            boutonRechercheDataMatrix.setOnClickListener(v -> {
                listeStockIntent = new Intent(ListeStockActivity.this, BarcodeCaptureActivity.class);
                listeStockBundle = ListeStockActivity.super.getBundle();
                listeStockIntent.putExtras(listeStockBundle);
                ListeStockActivity.this.startActivityForResult(listeStockIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
            });
        }
        else
        {
            boutonRechercheDataMatrix.setVisibility(View.INVISIBLE);
        }

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
                afficherSpinner(ListeStockActivity.this, LayoutInflater.from(ListeStockActivity.this));;
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ListeStockActivity.this);
            String urlRequete = "";

            try {
                urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceStock + "/depot/" + URLEncoder.encode(depotSelectionne.getDepot_Reference(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            stockJSONArray = new JSONArray();

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    response -> {
                        try {
                            int resultCount = response.getInt("resultCount");
                            stockJSONArray = response.getJSONArray("PH_Stocks");
                            if (resultCount == 0) {
                                String erreur = response.getString("erreur");
                                if (erreur.equals(getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(ListeStockActivity.this, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                    DBOpenHelper.viderBasesDeDonnees(db);
                                    ListeStockActivity.this.finishAffinity();
                                    listeStockIntent = new Intent(ListeStockActivity.this, AuthentificationActivity.class);
                                    ListeStockActivity.this.startActivity(listeStockIntent);
                                } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                    Alerte.afficherAlerte(ListeStockActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", "alerte");
                                    ListeStockActivity.this.finishAffinity();
                                    listeStockIntent = new Intent(ListeStockActivity.this, AuthentificationActivity.class);
                                    ListeStockActivity.this.startActivity(listeStockIntent);
                                } else if (erreur.equals("Aucun PH_Stock trouvé")) {
                                    Toast toast = Toast.makeText(ListeStockActivity.this, "Aucun Stock trouvé", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                } else {
                                    Alerte.afficherAlerte(ListeStockActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete liste stock", "alerte");
                                }
                                new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                            } else {
                                viderTablesConcernees();
                                for (int i = 0; i < stockJSONArray.length(); i++) {
                                    JSONObject stockJSONObject = stockJSONArray.getJSONObject(i);
                                    Stock stock = new Stock(stockJSONArray.getJSONObject(i));
                                    long rowID = StockOpenHelper.insererUnStockEnBDD(db, stock);
                                    if (rowID != -1) {
                                        stockList.add(stock);
                                        JSONArray ph_stock_lot_emplacement_JSONArray = stockJSONObject.getJSONArray("ph_stock_lot_emplacement");
                                        for (int k = 0; k < ph_stock_lot_emplacement_JSONArray.length(); k++) {
                                            Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, new Stock_Lot_Emplacement_Light(ph_stock_lot_emplacement_JSONArray.getJSONObject(k)));
                                        }
                                    }
                                }


                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(stockList.size()));
                                stockList.sort(Comparator.comparing(Stock::getDesignation));

                                stockAdapter = new StockAdapter(ListeStockActivity.this, db, stockList);
                                stocklistView.setDivider(footer);
                                stocklistView.setAdapter(stockAdapter);
                                passageParOnCreate = false;
                                invalidateOptionsMenu();
                                new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error");
                            Alerte.afficherAlerte(ListeStockActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP liste stock", "alerte");
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

        } else {
            stockList = StockOpenHelper.getStockByDepot(db, depotSelectionne);
            if (stockList.isEmpty()) {
                connexionNecessaire();
                return;
            }

            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(stockList.size()));
            stockList.sort(Comparator.comparing(Stock::getDesignation));

            stockAdapter = new StockAdapter(ListeStockActivity.this, db, stockList);
            stocklistView.setDivider(footer);
            stocklistView.setAdapter(stockAdapter);
            invalidateOptionsMenu();
        }
    }

    public void viderTablesConcernees() {
        StockOpenHelper.viderTableStocks(db);
        Stock_Lot_EmplacementLightOpenHelper.viderTableStock_Lot_Emplacements(db);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_CODE_GS1: {
                if (resultCode == ListeStockActivity.RESULT_OK) {
                    String code = data.getStringExtra("code");
                    String numero_serie = "";
                    assert code != null;
                    Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                    numero_serie = OutilsDecodage.numeroSerie;

                    if (!gs1Decoupe.isEmpty()) {
                        List<Produit> produitList = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                        if (produitList.size() == 1) {
                            Produit produit = produitList.get(0);
                            Stock stockSelectionne = null;

                            for (Stock stockCourant : stockList
                                    ) {
                                if (stockCourant.getProduit_UID() == produit.getID_produit()) {
                                    stockSelectionne = stockCourant;
                                    break;
                                }
                            }

                            if (stockSelectionne != null) {
                                listeStockIntent = new Intent(ListeStockActivity.this, DetailStockActivity.class);
                                listeStockBundle = ListeStockActivity.super.getBundle();
                                listeStockBundle.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
                                listeStockBundle.putSerializable("stock_Selectionne", stockSelectionne);
                                listeStockBundle.putString("Serie", numero_serie);

                                listeStockIntent.putExtras(listeStockBundle);
                                ListeStockActivity.this.startActivity(listeStockIntent);
                            } else {
                                Toast toast = Toast.makeText(ListeStockActivity.this, "Produit absent de votre stock", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } else if (produitList.size() > 1) {
                            Alerte.afficherAlerte(ListeStockActivity.this, "Attention", "Un problème est survenu, impossible d'identifier le produit.", "alerte");
                        } else {
                            Toast toast = Toast.makeText(ListeStockActivity.this, "Aucun produit ne correspond à ce code", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(ListeStockActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                break;
            }
            case CodesEchangesActivites.RETOUR_STOCK_LOT_EMPLACEMENT: {
                if(contexteUtiliser){
                    Bundle listeStock_Bundle = new Bundle();
                    listeStock_Bundle.putString("numeroLot", data.getStringExtra("numeroLot"));
                    listeStock_Bundle.putString("datePeremption", data.getStringExtra("datePeremption"));
                    listeStock_Bundle.putInt("produitID", data.getIntExtra("produitID", 0));
                    listeStock_Bundle.putString("zoneNom",data.getStringExtra("zoneNom"));
                    listeStock_Bundle.putString("emplacementNom",data.getStringExtra("emplacementNom"));
                    listeStock_Bundle.putString("Serie",data.getStringExtra("Serie"));
                    Intent listeStock_Intent = new Intent();
                    listeStock_Intent.putExtras(listeStock_Bundle);
                    setResult(CodesEchangesActivites.RETOUR_PRISE_PHOTO, listeStock_Intent);
                    finish();
                }
            }
        }
        invalidateOptionsMenu();
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, stockAdapter, null, "Désignation produit...");
        return true;
    }
}
