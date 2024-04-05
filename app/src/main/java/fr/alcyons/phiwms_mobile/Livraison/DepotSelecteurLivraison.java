package fr.alcyons.phiwms_mobile.Livraison;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class DepotSelecteurLivraison extends ServiceAvecConnexionActivity {
    Context context;
    List<Depot> depot_List;
    ListView depot_ListView;
    Depot_Livraison_Adapter depot_livraison_adapter;
    List<String> listeDate;

    JSONArray depot_livraison_JSONArray;

    boolean connexionDirecte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_depot_livraison);
        context = DepotSelecteurLivraison.this;

        //Gestion de la listView
        depot_ListView = (ListView) findViewById(R.id.listeView);
        depot_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Depot depot_selectionner = (Depot)depot_livraison_adapter.getItem(position);
                    Intent serviceLivraison_Intent = new Intent(DepotSelecteurLivraison.this, NewServiceLivraison.class);
                    Bundle serviceLivraison_Bundle = DepotSelecteurLivraison.super.getBundle();
                    serviceLivraison_Bundle.putInt("depot_selectionner", depot_selectionner.getDepot_UID());
                    serviceLivraison_Intent.putExtras(serviceLivraison_Bundle);
                    DepotSelecteurLivraison.this.startActivity(serviceLivraison_Intent);
            }
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);
    }

    @Override
    public void onResume() {
        super.onResume();
        depot_List = new ArrayList<>();
        listeDate = new ArrayList<>();
        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(DepotSelecteurLivraison.this)  && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(DepotSelecteurLivraison.this, "Veuillez patienter", "Synchronisation des dépôts de livraisons en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(DepotSelecteurLivraison.this);


            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceDepotLivraison;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int resultCount = response.getInt("resultCount");
                                if (resultCount == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        DepotSelecteurLivraison.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                        DepotSelecteurLivraison.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                        vide = true;
                                        nomServiceVide = "Livraison";
                                        DepotSelecteurLivraison.this.finish();
                                    }
                                } else {
                                    depot_livraison_JSONArray = response.getJSONArray("PH_Depot");
                                    List<String> tempString = new ArrayList<>();
                                    for (int i = 0; i < depot_livraison_JSONArray.length(); i++) {
                                        JSONArray temp = depot_livraison_JSONArray.getJSONArray(i);
                                        JSONObject ph_depot_JSONObject = temp.getJSONObject(0);
                                        Depot depot = new Depot(ph_depot_JSONObject);
                                        if(tempString.indexOf(depot.getNom()) == -1)
                                        {
                                            tempString.add(depot.getNom());
                                            depot_List.add(depot);
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
                            Alerte.afficherAlerte(DepotSelecteurLivraison.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Livraison)", "alerte");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
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
            if (depot_List.size() == 0) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(DepotSelecteurLivraison.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Livraison");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
                    DepotSelecteurLivraison.this.startActivity(retourVersServiceConnexionDirectIntent);
                    DepotSelecteurLivraison.this.finish();
                    return;
                }
                else
                {
                    connexionNecessaire();
                    return;
                }
            }

            if(connexionDirecte)
                connexionDirecte = !connexionDirecte;

        }
        /* Code nécessaire à l'affichage de la liste */
        int size_liste = depot_List.size();
        String titre = "Dépôts de livraison";
        if(size_liste < 2)
            titre = "Dépôt de livraison";

        ((TextView) findViewById(R.id.titre)).setText(titre);
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(depot_List.size()));
        // Tri par Date : de la plus récente à la plus ancienne
        Collections.sort(depot_List, new Comparator<Depot>() {
            @Override
            public int compare(Depot o1, Depot o2) {
                return o1.getNom().compareTo(o2.getNom());
            }
        });


        depot_livraison_adapter = new Depot_Livraison_Adapter(DepotSelecteurLivraison.this, depot_List);


        depot_ListView.setDivider(footer);
        depot_ListView.setAdapter(depot_livraison_adapter);

        if (depot_List.size() == 0) {
            vide = true;
            nomServiceVide = "Livraison";
            DepotSelecteurLivraison.this.finish();
        }

        invalidateOptionsMenu();

    }
}