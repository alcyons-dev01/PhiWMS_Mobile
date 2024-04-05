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
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class NewServiceLivraison  extends ServiceAvecConnexionActivity {
    Context context;
    List<PH_Preparation> ph_preparation_List;
    ListView ph_preparation_ListView;
    PH_Preparation_LivraisonAdapter ph_preparation_livraisonAdapter;
    List<String> listeDate;

    JSONArray ph_preparation_JSONArray;

    boolean connexionDirecte;
    int depot_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_livraison);
        context = NewServiceLivraison.this;
        depot_uid = intent.getExtras().getInt("depot_selectionner");
        //Gestion de la listView
        ph_preparation_ListView = (ListView) findViewById(R.id.listeView);
        ph_preparation_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (ph_preparation_livraisonAdapter.sectionHeader.contains(position) == false) {
                    PH_Preparation ph_preparation_Selectionne = ph_preparation_livraisonAdapter.getItem(position);
                    Intent serviceLivraison_Intent = new Intent(NewServiceLivraison.this, InformationLivraisonActivity.class);
                    Bundle serviceLivraison_Bundle = NewServiceLivraison.super.getBundle();
                    serviceLivraison_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
                    serviceLivraison_Intent.putExtras(serviceLivraison_Bundle);
                    NewServiceLivraison.this.startActivity(serviceLivraison_Intent);
                }
            }
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);
        passageParOnCreate = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        ph_preparation_List = new ArrayList<>();
        listeDate = new ArrayList<>();
        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(NewServiceLivraison.this) && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(NewServiceLivraison.this, "Veuillez patienter", "Synchronisation des livraisons en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(NewServiceLivraison.this);


            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceLivraisonByDepot + "/" + depot_uid;

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
                                        NewServiceLivraison.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                        NewServiceLivraison.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                        Alerte.afficherAlerte(context, "Attention", "Aucune livraison à traiter", "alerte");
                                        NewServiceLivraison.this.finishAffinity();
                                    }
                                } else {
                                    ph_preparation_JSONArray = response.getJSONArray("PH_Preparations");
                                    viderTablesConcernees();
                                    for (int i = 0; i < ph_preparation_JSONArray.length(); i++) {
                                        JSONObject ph_preparation_JSONObject = ph_preparation_JSONArray.getJSONObject(i);
                                        PH_Preparation ph_preparation = new PH_Preparation(ph_preparation_JSONObject);

                                        ph_preparation_List.add(ph_preparation);
                                        long rowID = gestionnairePH_Preparation.insererUnPH_PreparationEnBDD(db, ph_preparation);
                                        if (rowID != -1) {
                                            JSONArray ph_preparationLignesJson = ph_preparation_JSONObject.getJSONArray("ph_preparation_lignes");
                                            for (int k = 0; k < ph_preparationLignesJson.length(); k++) {
                                                gestionnairePH_Preparation_Ligne.insererUnPH_Preparation_LigneEnBDD(db, new PH_Preparation_Ligne(ph_preparationLignesJson.getJSONObject(k)));
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
                            Alerte.afficherAlerte(NewServiceLivraison.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Livraison)", "alerte");
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
            ph_preparation_List = gestionnairePH_Preparation.getAllPHPreparationLivraisons(db, ParametresServeurOpenHelper.getModuleTransport(db), this.utilisateurConnecte.getId());
            if (ph_preparation_List.size() == 0) {
                if (connexionDirecte) {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(NewServiceLivraison.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Livraison");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
                    NewServiceLivraison.this.startActivity(retourVersServiceConnexionDirectIntent);
                    NewServiceLivraison.this.finish();
                    return;
                } else {
                    connexionNecessaire();
                    return;
                }
            }

            if (connexionDirecte)
                connexionDirecte = !connexionDirecte;

        }
        /* Code nécessaire à l'affichage de la liste */
        int taille_liste = ph_preparation_List.size();
        String titre = "Livraisons";
        if(taille_liste < 2)
            titre = "Livraison";

        ((TextView) findViewById(R.id.titre)).setText(titre);
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(ph_preparation_List.size()));
        // Tri par Date : de la plus récente à la plus ancienne
        Collections.sort(ph_preparation_List, new Comparator<PH_Preparation>() {
            @Override
            public int compare(PH_Preparation o1, PH_Preparation o2) {
                return o1.getLivraisonPrevueDate().compareTo(o2.getLivraisonPrevueDate());
            }
        });


        ph_preparation_livraisonAdapter = new PH_Preparation_LivraisonAdapter(NewServiceLivraison.this, db, utilisateurConnecte);

        for (PH_Preparation ph_courant : ph_preparation_List) {
            if (listeDate.indexOf(ph_courant.getLivraisonPrevueDate()) == -1) {
                listeDate.add(ph_courant.getLivraisonPrevueDate());
                ph_preparation_livraisonAdapter.addSectionHeaderItem(ph_courant);
            }

            ph_preparation_livraisonAdapter.addItem(ph_courant);
        }

        ph_preparation_ListView.setDivider(footer);
        ph_preparation_ListView.setAdapter(ph_preparation_livraisonAdapter);

        if (ph_preparation_List.size() == 0) {
            vide = true;
            nomServiceVide = "Livraison";
            NewServiceLivraison.this.finish();
        }

        invalidateOptionsMenu();

    }

    public void viderTablesConcernees() {
        for (PH_Preparation ph_preparation : gestionnairePH_Preparation.getAllPHPreparationLivraisons(db, ParametresServeurOpenHelper.getModuleTransport(db), this.utilisateurConnecte.getId())
        ) {
            List<PH_Preparation_Ligne> ph_preparation_lignes = gestionnairePH_Preparation_Ligne.getAllPHPreparationLignesParPHPreparation(db, ph_preparation);
            for (PH_Preparation_Ligne ph_preparation_ligne : ph_preparation_lignes
            ) {
                gestionnairePH_Preparation_Ligne.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
            }
            gestionnairePH_Preparation.supprimerUnPhPreparation(db, ph_preparation);
        }
    }
}