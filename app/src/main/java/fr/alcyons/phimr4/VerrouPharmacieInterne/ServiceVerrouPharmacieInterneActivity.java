package fr.alcyons.phimr4.VerrouPharmacieInterne;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.PH_Lot_Ligne;
import fr.alcyons.phimr4.Classes.PH_Preparation;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phimr4.ListViewAdapters.VerrouInterneAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

public class ServiceVerrouPharmacieInterneActivity extends ServiceAvecConnexionActivity {

    Context context;

    List<PH_Preparation> phPreparationList;
    ListView phPreparationListView;
    VerrouInterneAdapter VerrouInterneAdapter;
    PackageManager pm;
    JSONArray phPreparationJSONArray;

    long rowID = 0;

    boolean connexionDirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_verrou_pharmacie_interne);

        context = ServiceVerrouPharmacieInterneActivity.this;
        pm = ServiceVerrouPharmacieInterneActivity.this.getPackageManager();
        // Gestion de la liste
        phPreparationListView = (ListView) findViewById(R.id.listeView);
        phPreparationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PH_Preparation phPreparationSelectionne = (PH_Preparation) VerrouInterneAdapter.getItem(position);

                if (phPreparationSelectionne != null) {
                    Bundle serviceVerrouPharmacieBundle = ServiceVerrouPharmacieInterneActivity.super.getBundle();
                    serviceVerrouPharmacieBundle.putInt("phPreparationSelectionneID", phPreparationSelectionne.getUID());

                    Intent serviceVerrouPharmacieIntent = new Intent(ServiceVerrouPharmacieInterneActivity.this, DetailVerrouPharmacieInterneActivity.class);
                    serviceVerrouPharmacieIntent.putExtras(serviceVerrouPharmacieBundle);
                    ServiceVerrouPharmacieInterneActivity.this.startActivity(serviceVerrouPharmacieIntent);
                }
            }
        });

        connexionDirect = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);
    }

    @Override
    public void onResume() {
        super.onResume();
        phPreparationList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceVerrouPharmacieInterneActivity.this) && passageParOnCreate && !connexionDirect) {

            mProgressDialog = null;
            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServiceVerrouPharmacieInterneActivity.this, "Veuillez patienter", "Synchronisation des préparations en cours");
            }
            CommandeOpenHelper.insererBDDLocaleCommandeReceptionPAD(ServiceVerrouPharmacieInterneActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte);

            RequestQueue requestQueueVerrouPharmacieUtilisateur = Volley.newRequestQueue(ServiceVerrouPharmacieInterneActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteVerrouPharmacieInterne;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {

                        // Takes the response from the JSON request
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
                                        ServiceVerrouPharmacieInterneActivity.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                        ServiceVerrouPharmacieInterneActivity.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                        Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Verrou Pharmacie", "alerte");
                                        ServiceVerrouPharmacieInterneActivity.this.finishAffinity();
                                    }
                                } else {
                                    PH_SerialisationOpenHelper.viderTablePH_Serialisation(db);

                                    phPreparationJSONArray = response.getJSONArray("PH_Preparations");
                                    viderTablesConcernees();
                                    for (int i = 0; i < phPreparationJSONArray.length(); i++) {
                                        JSONObject phPreparationJSONObject = phPreparationJSONArray.getJSONObject(i);
                                        PH_Preparation phPreparation = new PH_Preparation(phPreparationJSONObject);

                                        Depot depot = gestionnaireDepot.getDepotParID(db, phPreparation.getDepotDestinataireID());

                                        if (depot != null) {
                                            rowID = gestionnairePH_Preparation.insererUnPH_PreparationEnBDD(db, phPreparation);
                                            if (rowID != -1) {
                                                phPreparationList.add(phPreparation);
                                            }

                                            JSONArray PH_Preparation_LigneArray = phPreparationJSONObject.getJSONArray("ph_preparation_lignes");
                                            for(int j = 0; j < PH_Preparation_LigneArray.length(); j++)
                                            {
                                                JSONObject preparationLigneObject = PH_Preparation_LigneArray.getJSONObject(j);
                                                PH_Preparation_Ligne preparation_ligne = new PH_Preparation_Ligne(preparationLigneObject);
                                                gestionnairePH_Preparation_Ligne.insererUnPH_Preparation_LigneEnBDD(db, preparation_ligne);

                                                JSONArray ph_lot_ligne_json_array = preparationLigneObject.getJSONArray("ph_lot_ligne");
                                                for(int l = 0; l < ph_lot_ligne_json_array.length(); l++)
                                                {
                                                    JSONObject ph_lot_ligne_courant = ph_lot_ligne_json_array.getJSONObject(l);
                                                    PH_Lot_Ligne lot_ligne = new PH_Lot_Ligne(ph_lot_ligne_courant);
                                                    long rowId = PH_Lot_LigneOpenHelper.insererUnPH_Lot_LigneBDD(db, lot_ligne);
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
                            Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP Service Verrou Pharmacie Interne", "alerte");
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
            requestQueueVerrouPharmacieUtilisateur.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            if (phPreparationList.size() == 0) {
                vide = true;
                nomServiceVide = "Verrou Pharmacie Interne";
                onBackPressed();
            }
            else
            {
                if(passageParOnCreate)
                {
                    //lancerScan();
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(phPreparationList.size()));
                    Collections.sort(phPreparationList, new Comparator<PH_Preparation>() {
                        @Override
                        public int compare(PH_Preparation o1, PH_Preparation o2) {
                            return o1.getLivraisonPrevueDate().compareTo(o2.getLivraisonPrevueDate());
                        }
                    });

                    VerrouInterneAdapter = new VerrouInterneAdapter(ServiceVerrouPharmacieInterneActivity.this, db, phPreparationList, utilisateurConnecte);
                    phPreparationListView.setDivider(footer);
                    phPreparationListView.setAdapter(VerrouInterneAdapter);

                    if (phPreparationList.size() == 0) {
                        vide = true;
                        nomServiceVide = "Verrou Pharmacie Interne";
                        ServiceVerrouPharmacieInterneActivity.this.finish();
                    }
                }

                passageParOnCreate = false;

            }
        } else {
            phPreparationList = gestionnairePH_Preparation.getAllDelivrance(db);
            if (phPreparationList.size() == 0) {
                if(connexionDirect)
                {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceVerrouPharmacieInterneActivity.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Verrou Pharmacie Interne");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
                    ServiceVerrouPharmacieInterneActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceVerrouPharmacieInterneActivity.this.finish();
                }
                else
                {
                    connexionNecessaire();
                    return;
                }
            }
            else
            {
                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(phPreparationList.size()));
                Collections.sort(phPreparationList, new Comparator<PH_Preparation>() {
                    @Override
                    public int compare(PH_Preparation o1, PH_Preparation o2) {
                        return o1.getLivraisonPrevueDate().compareTo(o2.getLivraisonPrevueDate());
                    }
                });

                VerrouInterneAdapter = new VerrouInterneAdapter(ServiceVerrouPharmacieInterneActivity.this, db, phPreparationList, utilisateurConnecte);
                phPreparationListView.setDivider(footer);
                phPreparationListView.setAdapter(VerrouInterneAdapter);

                if (phPreparationList.size() == 0) {
                    vide = true;
                    nomServiceVide = "Verrou Pharmacie Interne";
                    ServiceVerrouPharmacieInterneActivity.this.finish();
                }

                passageParOnCreate = false;
                if(connexionDirect)
                {
                    connexionDirect = !connexionDirect;
                }

                invalidateOptionsMenu();
            }
        }
    }

    public void viderTablesConcernees() {
        for (PH_Preparation ph_preparation : gestionnairePH_Preparation.getAllPHPreparationVerrouPharmacieInterne(db))
        {
            if(!ph_preparation.getListe().contentEquals("ALCYONS_VERROU"))
            {
                for (PH_Preparation_Ligne ph_preparation_ligne : gestionnairePH_Preparation_Ligne.getAllPHPreparationLignesParPHPreparation(db, ph_preparation))
                {
                    gestionnairePH_Lot_Ligne.supprimerPH_LotLigne(db, ph_preparation_ligne.get_UID());
                    gestionnairePH_Preparation_Ligne.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
                    Produit produit = gestionnaireProduit.getProduitByID(db, ph_preparation_ligne.getProduitID());
                    Depot depot = gestionnaireDepot.getDepotParID(db, ph_preparation.getDepotOrigineID());

                    if (depot != null && produit != null) {
                        for (Stock_Lot_Emplacement_Light stockLotEmplacement : gestionnaireStock_Lot_Emplacement.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)
                        ) {
                            gestionnaireStock_Lot_Emplacement.supprimerUnStockLotEmplacement(db, stockLotEmplacement);
                        }
                    }
                }
                gestionnairePH_Preparation.supprimerUnPhPreparation(db, ph_preparation);
            }
        }
    }
}
