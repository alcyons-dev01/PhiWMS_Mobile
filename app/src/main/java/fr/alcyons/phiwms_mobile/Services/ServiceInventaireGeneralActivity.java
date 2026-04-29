package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Inventaire;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Inventaire.DetailInventaireActivity;
import fr.alcyons.phiwms_mobile.Inventaire.DetailInventaire_V2Activity;
import fr.alcyons.phiwms_mobile.Inventaire.DetailInventaire_V3;
import fr.alcyons.phiwms_mobile.Inventaire.InventaireZoneActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.DepotAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.InventaireAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceInventaireGeneralActivity extends ServiceAvecConnexionActivity {
    Context context;
    PackageManager pm;
    ListView depotListView;
    DepotAdapter depotAdapter;
    boolean connexionDirecte;
    ArrayList<Depot> arrayDepot;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        pm = ServiceInventaireGeneralActivity.this.getPackageManager();
        context = ServiceInventaireGeneralActivity.this;

        // Gestion de la listView
        depotListView = (ListView) findViewById(R.id.listeView);
        depotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Inventaire inventaireGeneral = InventaireOpenHelper.getInventaireGeneral(db);
                Depot depotSelectionne = arrayDepot.get(position);
                Intent serviceInventaire_Intent = new Intent(ServiceInventaireGeneralActivity.this, InventaireZoneActivity.class);
                Bundle serviceInventaire_Bundle = ServiceInventaireGeneralActivity.super.getBundle();
                serviceInventaire_Bundle.putInt("inventaireId", inventaireGeneral.getInventaire_ID());
                serviceInventaire_Bundle.putInt("depotId", depotSelectionne.getDepot_UID());
                serviceInventaire_Intent.putExtras(serviceInventaire_Bundle);
                ServiceInventaireGeneralActivity.this.startActivity(serviceInventaire_Intent);
            }
        });

        arrayDepot = new ArrayList<>();
        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceInventaireGeneralActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceInventaireGeneralActivity.this.startActivity(intent);
                ServiceInventaireGeneralActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceInventaireGeneralActivity.this, LayoutInflater.from(ServiceInventaireGeneralActivity.this));
            }
            RequestQueue requestQueue = Volley.newRequestQueue(ServiceInventaireGeneralActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteInventaireGeneral+"/depot";

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueue.add(obreq);
        }
        else
        {
            invalidateOptionsMenu();
        }
    }

    @NonNull
    private JsonObjectRequest getJsonObjectRequest(String urlRequete) {
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                response -> {
                    try {
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            String erreur = response.getString("erreur");
                            if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerteInformation(ServiceInventaireGeneralActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est invalide, veuillez vous reconnecter.", false, true);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(ServiceInventaireGeneralActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est expirée, veuillez vous reconnecter.", false, true);
                            } else if (!erreur.contentEquals("Aucun Inventaire trouvé")) {
                                Alerte.afficherAlerteInformation(ServiceInventaireGeneralActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Général", false, true);
                            } else {
                                arreterSpinner();
                                Alerte.afficherAlerteInformation(ServiceInventaireGeneralActivity.this, getLayoutInflater(), "Information", "Aucun inventaire général à traiter", false, true);
                            }
                        } else {
                            InventaireOpenHelper.viderTableInventaire(db);
                            JSONArray inventaireArray = response.getJSONArray("Inventaire");

                            for(int i = 0; i < inventaireArray.length(); i++)
                            {
                                Inventaire inventaire = new Inventaire(inventaireArray.getJSONObject(i));
                                InventaireOpenHelper.insererUnInventaireEnBDD(db, inventaire);
                            }

                            JSONArray depotId = response.getJSONArray("DepotId");
                            for(int j = 0; j < depotId.length(); j++)
                            {
                                int depotIdInt = depotId.getInt(j);
                                Depot depot = DepotOpenHelper.getDepotParID(db, depotIdInt);

                                if(depot != null)
                                {
                                    arrayDepot.add(depot);
                                }
                            }

                            arrayDepot.sort(Comparator.comparing(Depot::getNom));
                            gestionAdapter();
                            passageParOnCreate = false;
                            arreterSpinner();
                            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                        }
                    } catch (JSONException e) {
                        Log.e("JSON Exception", Objects.requireNonNull(e.getMessage()));
                    }
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(ServiceInventaireGeneralActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Inventaire Général", false, true);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                headers.put("EtablissementId", String.valueOf(utilisateurConnecte.getEtablissementId()));
                headers.put("UserId", String.valueOf(utilisateurConnecte.getId()));
                return headers;
            }
        };
        return obreq;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, depotAdapter, null, "Rechercher...");
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(false);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void gestionAdapter()
    {
        if(depotAdapter == null) {
            depotAdapter = new DepotAdapter(ServiceInventaireGeneralActivity.this, arrayDepot, utilisateurConnecte);
        }

        depotListView.setAdapter(depotAdapter);
    }
}
