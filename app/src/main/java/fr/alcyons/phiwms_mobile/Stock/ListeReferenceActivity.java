package fr.alcyons.phiwms_mobile.Stock;

import static com.google.android.gms.vision.L.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import androidx.activity.OnBackPressedCallback;
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

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerProduitActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Liste_ReferenceAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GS1Parser;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ListeReferenceActivity extends ServiceAvecConnexionActivity {
    Liste_ReferenceAdapter listeReferenceAdapter;
    List<Produit> listProduit;
    ListView referenceListeView;
    PackageManager pm;
    Depot depotCourant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_reference);

        //gestion du package manager
        pm = ListeReferenceActivity.this.getPackageManager();

        //initialisation
        referenceListeView = (ListView) findViewById(R.id.listeView);
        listProduit = new ArrayList<>();
        depotCourant = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotUID_Selectionne"));
        ((TextView) findViewById(R.id.nomDepot)).setText(depotCourant.getNom());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ListeReferenceActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //* Code nécessaire afin de réaliser une requête à l' API *//*
        if (statutConnexion && passageParOnCreate)
        {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ListeReferenceActivity.this, LayoutInflater.from(ListeReferenceActivity.this));
            }

            RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ListeReferenceActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteStocks + "depot/" + depotCourant.getDepot_Reference()+"/stock";


            JsonObjectRequest obreq = getObjectRequest(urlRequete);
            requestQueueDestructionUtilisateur.add(obreq);
        }
        else
        {
            listProduit = ProduitOpenHelper.getAllProduits(db);
            listeReferenceAdapter = new Liste_ReferenceAdapter(ListeReferenceActivity.this, db, listProduit, depotCourant);
            referenceListeView.setAdapter(listeReferenceAdapter);
        }

        referenceListeView.setOnItemClickListener((parent, view, position, id) -> {
            Produit produitSelectionne = (Produit) listeReferenceAdapter.getItem(position);

            Bundle serviceStock_Bundle = ListeReferenceActivity.super.getBundle();
            assert produitSelectionne != null;
            serviceStock_Bundle.putInt("produitID", produitSelectionne.getID_produit());
            serviceStock_Bundle.putInt("depotUID_Selectionne", Objects.requireNonNull(intent.getExtras()).getInt("depotUID_Selectionne"));
            Intent serviceControleRetours_Intent = new Intent(ListeReferenceActivity.this, DetailStockActivity.class);
            serviceControleRetours_Intent.putExtras(serviceStock_Bundle);
            ListeReferenceActivity.this.startActivity(serviceControleRetours_Intent);
        });

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
                                Alerte.afficherAlerteInformation(ListeReferenceActivity.this, getLayoutInflater(), "Alerte", "Votre session de connexion est invalide, veuillez vous reconnecter", false, true);
                            } else if (erreur.equals(getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(ListeReferenceActivity.this, getLayoutInflater(), "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", false, true);
                            } else if (erreur.contentEquals("Aucun PH_Commande trouvé")) {
                                arreterSpinner();
                                Alerte.afficherAlerteInformation(ListeReferenceActivity.this, getLayoutInflater(), "Erreur", "Aucune réception PUI à traiter", false, true);
                            } else {
                                Alerte.afficherAlerteInformation(ListeReferenceActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Stock", false, true);
                            }
                        } else {
                            JSONArray stockJSONArray = response.getJSONArray("PH_Stocks");
                            StockOpenHelper.viderTableStocksDepot(db, depotCourant.getDepot_Reference());

                            for (int i = 0; i < stockJSONArray.length(); i++) {
                                JSONObject stockJSONObject = stockJSONArray.getJSONObject(i);

                                Stock stock_temp = new Stock(stockJSONObject);

                                long row_id = StockOpenHelper.insererUnStockEnBDD(db, stock_temp);
                            }

                            passageParOnCreate = false;

                            listProduit = ProduitOpenHelper.getAllProduits(db);
                            listeReferenceAdapter = new Liste_ReferenceAdapter(ListeReferenceActivity.this, db, listProduit, depotCourant);
                            referenceListeView.setAdapter(listeReferenceAdapter);
                            invalidateOptionsMenu();

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
                    Alerte.afficherAlerteInformation(ListeReferenceActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Stock", false, true);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodesEchangesActivites.RETOUR_CODE_GS1) {
            String code = data.getStringExtra("code");
            assert code != null;
            if (!code.contentEquals("")) {
                if(code.toUpperCase().startsWith("PHITAGREF:"))
                {
                    String[] codeDecoupe = code.split(":");
                    if(codeDecoupe.length > 1) {
                        String codeProduit = codeDecoupe[1];
                        Produit produit = ProduitOpenHelper.getProduitByID(db, Integer.parseInt(codeProduit));
                        if (produit != null) {
                            Intent selectionProduitIntent = new Intent(ListeReferenceActivity.this, DetailStockActivity.class);
                            Bundle selectionProduitBundle = ListeReferenceActivity.super.getBundle();
                            selectionProduitBundle.putInt("depotUID_Selectionne", Objects.requireNonNull(intent.getExtras()).getInt("depotUID_Selectionne"));
                            selectionProduitBundle.putInt("produitID", produit.getID_produit());
                            selectionProduitIntent.putExtras(selectionProduitBundle);
                            ListeReferenceActivity.this.startActivity(selectionProduitIntent);
                        } else {
                            Toast toast = Toast.makeText(ListeReferenceActivity.this, "Aucun produit ne correspond à ce code", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                    else {
                        Toast toast = Toast.makeText(ListeReferenceActivity.this, "Le code fourni n'est pas un code PHITAGREF, veuillez réessayer.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                else if(code.toUpperCase().startsWith("PHITAGTIN:"))
                {
                    String[] codeDecoupe = code.split(":");
                    if(codeDecoupe.length > 1) {
                        String codeProduit = codeDecoupe[1];
                        List<Produit> produitList = ProduitOpenHelper.getProduitsParGTINAvecSansAI(db, codeProduit);
                        if (produitList.size() == 1) {
                            Produit produit = produitList.get(0);
                            Intent selectionProduitIntent = new Intent(ListeReferenceActivity.this, DetailStockActivity.class);
                            Bundle selectionProduitBundle = ListeReferenceActivity.super.getBundle();
                            selectionProduitBundle.putInt("depotUID_Selectionne", Objects.requireNonNull(intent.getExtras()).getInt("depotUID_Selectionne"));
                            selectionProduitBundle.putInt("produitID", produit.getID_produit());
                            selectionProduitIntent.putExtras(selectionProduitBundle);
                            ListeReferenceActivity.this.startActivity(selectionProduitIntent);
                        } else if (produitList.size() > 1) {
                            Alerte.afficherAlerte(ListeReferenceActivity.this, "Attention", "Un problème est survenu, impossible d'identifier le produit.", "alerte");
                        } else {

                            Toast toast = Toast.makeText(ListeReferenceActivity.this, "Aucun produit ne correspond à ce code", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                    else {
                        Toast toast = Toast.makeText(ListeReferenceActivity.this, "Le code fourni n'est pas un code PHITAGREF, veuillez réessayer.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                else
                {
                    List<Produit> produitList = ProduitOpenHelper.getProduitsParGTINAvecSansAI(db, code);
                    if (produitList.size() == 1) {
                        Produit produit = produitList.get(0);
                        Intent selectionProduitIntent = new Intent(ListeReferenceActivity.this, DetailStockActivity.class);
                        Bundle selectionProduitBundle = ListeReferenceActivity.super.getBundle();
                        selectionProduitBundle.putInt("depotUID_Selectionne", Objects.requireNonNull(intent.getExtras()).getInt("depotUID_Selectionne"));
                        selectionProduitBundle.putInt("produitID", produit.getID_produit());
                        selectionProduitIntent.putExtras(selectionProduitBundle);
                        ListeReferenceActivity.this.startActivity(selectionProduitIntent);
                    } else if (produitList.size() > 1) {
                        Alerte.afficherAlerte(ListeReferenceActivity.this, "Attention", "Un problème est survenu, impossible d'identifier le produit.", "alerte");
                    } else {

                        Toast toast = Toast.makeText(ListeReferenceActivity.this, "Aucun produit ne correspond à ce code", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }
            else {
                Toast toast = Toast.makeText(ListeReferenceActivity.this, "Le code fourni est inconnu, veuillez réessayer.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
        invalidateOptionsMenu();
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
        super.prepareOptionsMenu(menu, listeReferenceAdapter, null, "Désignation produit...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> {
            lancerScan();
            return true;
        });
        return true;
    }

    private void lancerScan()
    {
        Intent rechercheProduitIntent;
        Bundle rechercheProduiBundle = ListeReferenceActivity.super.getBundle();
        rechercheProduiBundle.putBoolean("isBoutonSuppressionExistant", true);

        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            rechercheProduitIntent = new Intent(ListeReferenceActivity.this, ScannerProduitActivity.class);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                rechercheProduitIntent = new Intent(ListeReferenceActivity.this, BarcodeCaptureActivity.class);
            }
            else
            {
                rechercheProduitIntent = new Intent(ListeReferenceActivity.this, ScannerProduitActivity.class);
            }
        }

        rechercheProduitIntent.putExtras(rechercheProduiBundle);
        ListeReferenceActivity.this.startActivityForResult(rechercheProduitIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
    }
}
