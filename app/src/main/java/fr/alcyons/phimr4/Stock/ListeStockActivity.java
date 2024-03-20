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
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.StockAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

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
    View.OnClickListener onClickListenerDepotChoisir = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ListeStockActivity.this.finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_stock);

        //gestion du package manager
        pm = ListeStockActivity.this.getPackageManager();

        depotSelectionne = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotUID_Selectionne"));
        contexteUtiliser = intent.getBooleanExtra("contexteUtiliser",false);

        nomDepotTextView = ((TextView) findViewById(R.id.nomDepot));
        nomDepotTextView.setOnClickListener(onClickListenerDepotChoisir);

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

        // Clic sur l'icone géolocalisation de la banniere
        icone_gelocImageView = ((ImageView) findViewById(R.id.icone_geloc));
        icone_gelocImageView.setOnClickListener(onClickListenerDepotChoisir);

        stocklistView = (ListView) findViewById(R.id.listeView);

        stocklistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            }
        });

        boutonRechercheDataMatrix = (FloatingActionButton) findViewById(R.id.boutonRechercheDataMatrix);

        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            boutonRechercheDataMatrix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listeStockIntent = new Intent(ListeStockActivity.this, BarcodeCaptureActivity.class);
                    listeStockBundle = ListeStockActivity.super.getBundle();
                    listeStockIntent.putExtras(listeStockBundle);
                    ListeStockActivity.this.startActivityForResult(listeStockIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                }
            });
        }
        else
        {
            boutonRechercheDataMatrix.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        stockList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ListeStockActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ListeStockActivity.this, "Veuillez patienter", "Synchronisation des stocks en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ListeStockActivity.this);
            String urlRequete = "";

            try {
                urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceStock + "/depot/" + URLEncoder.encode(depotSelectionne.getDepot_Reference(), "utf-8");
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
                                stockJSONArray = response.getJSONArray("PH_Stocks");
                                if (resultCount == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(ListeStockActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
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
                                } else {
                                    viderTablesConcernees();
                                    for (int i = 0; i < stockJSONArray.length(); i++) {
                                        JSONObject stockJSONObject = stockJSONArray.getJSONObject(i);
                                        Stock stock = new Stock(stockJSONArray.getJSONObject(i));
                                        long rowID = gestionnaireStock.insererUnStockEnBDD(db, stock);
                                        if (rowID != -1) {
                                            stockList.add(stock);
                                            JSONArray ph_stock_lot_emplacement_JSONArray = stockJSONObject.getJSONArray("ph_stock_lot_emplacement");
                                            for (int k = 0; k < ph_stock_lot_emplacement_JSONArray.length(); k++) {
                                                gestionnaireStock_Lot_Emplacement.insererUnStock_Lot_EmplacementEnBDD(db, new Stock_Lot_Emplacement_Light(ph_stock_lot_emplacement_JSONArray.getJSONObject(k)));
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
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            passageParOnCreate = false;
        } else {
            stockList = gestionnaireStock.getStockByDepot(db, depotSelectionne);
            if (stockList.size() == 0) {
                connexionNecessaire();
                return;
            }
        }

        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(stockList.size()));
        Collections.sort(stockList, new Comparator<Stock>() {
            @Override
            public int compare(Stock o1, Stock o2) {
                return o1.getDesignation().compareTo(o2.getDesignation());
            }
        });

        stockAdapter = new StockAdapter(ListeStockActivity.this, db, stockList);
        stocklistView.setDivider(footer);
        stocklistView.setAdapter(stockAdapter);

        invalidateOptionsMenu();
    }

    public void viderTablesConcernees() {
        gestionnaireStock.viderTableStocks(db);
        gestionnaireStock_Lot_Emplacement.viderTableStock_Lot_Emplacements(db);
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_CODE_GS1: {
                if (resultCode == ListeStockActivity.RESULT_OK) {
                    String code = data.getStringExtra("code");
                    String numero_serie = "";
                    Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                    numero_serie = OutilsDecodage.numeroSerie;

                    if (gs1Decoupe.size() != 0) {
                        List<Produit> produitList = gestionnaireProduit.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
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
