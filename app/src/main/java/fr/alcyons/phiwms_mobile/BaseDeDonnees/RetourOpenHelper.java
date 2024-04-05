package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import com.example.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class RetourOpenHelper extends DBOpenHelper {

    public RetourOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static Retour getRetourEssaiAlcyons(SQLiteDatabase db)
    {
        Retour retour_alcyons = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR + " WHERE " + Constantes.CLE_COL_INTITULE_RETOUR + "=?", new String[]{"Retour_ALCYONS"});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            retour_alcyons = new Retour(cursor);
        }
        cursor.close();
        cursor = null;

        return retour_alcyons;
    }

    public static Retour getQuarantaineEssai(SQLiteDatabase db)
    {
        Retour retour_alcyons = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR + " WHERE " + Constantes.CLE_COL_INTITULE_RETOUR + "=?", new String[]{"Quarantaine_ALCYONS"});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            retour_alcyons = new Retour(cursor);
        }
        cursor.close();
        cursor = null;

        return retour_alcyons;
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        db.delete(Constantes.TABLE_RETOUR, Constantes.CLE_COL_INTITULE_RETOUR + "=?", new String[]{"Retour_ALCYONS"});
        return db.delete(Constantes.TABLE_RETOUR, Constantes.CLE_COL_INTITULE_RETOUR + "=?", new String[]{"Quarantaine_ALCYONS"});
    }

    public static long insererUnRetourEnBDD(SQLiteDatabase db, Retour retour) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_RETOUR, retour.get_UID());
        contentValues.put(Constantes.CLE_COL_NUMERO_RETOUR, retour.getNumero());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_ORIGINE_RETOUR, retour.getRef_Depot_Origine());
        contentValues.put(Constantes.CLE_COL_CODE_PATIENT_RETOUR, retour.getCode_Patient());
        contentValues.put(Constantes.CLE_COL_INTITULE_RETOUR, retour.getIntitulé());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_DEST_RETOUR, retour.getRef_Depot_Dest());
        contentValues.put(Constantes.CLE_COL_STATUT_RETOUR, retour.getStatut());
        contentValues.put(Constantes.CLE_COL_DATE_RETOUR_RETOUR, retour.getDate_retour());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_RETOUR, retour.getMontant_TTC());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_RETOUR, retour.getCommentaire());
        contentValues.put(Constantes.CLE_COL_MOTIF_RETOUR, retour.getMotif());
        contentValues.put(Constantes.CLE_COL_DEVISE_RETOUR, retour.getDevise());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_RETOUR, retour.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_RETOUR, retour.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_RETOUR, retour.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_EN_ATTENTE_DE_RETOUR, retour.getEn_Attente_de());
        contentValues.put(Constantes.CLE_COL_DATE_REPRISE_RETOUR, retour.getDate_Reprise());
        contentValues.put(Constantes.CLE_COL_DATE_VALIDATION_RETOUR, retour.getDate_Validation());
        contentValues.put(Constantes.CLE_COL_PROVENANCE_REFERENCE_RETOUR, retour.getProvenance_Reference());
        contentValues.put(Constantes.CLE_COL_AVOIR_ATTENDU_RETOUR, retour.getAvoir_Attendu());
        contentValues.put(Constantes.CLE_COL_NOM_CHAUFFEUR_RETOUR, retour.getNom_Chauffeur());
        contentValues.put(Constantes.CLE_COL_PRENOM_CHAUFFEUR_RETOUR, retour.getPrenom_Chauffeur());
        contentValues.put(Constantes.CLE_COL_TRANSPORTEUR_RETOUR, retour.getTransporteur());
        contentValues.put(Constantes.CLE_COL_SIGNATURE_CHAUFFEUR, retour.getSignature_Chauffeur());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_RETOUR, null, contentValues);

        retour.setPhiMR4UUID((int) rowId);

        return rowId;
    }

    public static void insererRetourQuarantaine(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        if (!OutilsGestionConnexionReseau.isServerAccessible(context)) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteQuarantaine;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String erreur = "";
                            boolean etat = true;

                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    viderBasesDeDonnees(db);
                                    erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                    erreur = "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocaleRetours";
                                    etat = false;
                                }
                            } else {
                                JSONArray retourJSONArray = response.getJSONArray("PH_Retours");

                                List<Retour> retourList = getRetoursByEnAttenteDe(db, context.getString(R.string.Quarantaine));
                                retourList.addAll(getAllRetoursByStatutEtEnAttenteDe(db, context.getString(R.string.statutEncours), context.getString(R.string.MiseEnQuarantaine)));

                                for (Retour retour : retourList
                                        ) {
                                    List<Retour_Ligne> retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour);
                                    for (Retour_Ligne retourLigne : retourLigneList
                                            ) {
                                        Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigne);
                                    }
                                    supprimerUnRetour(db, retour);
                                }



                                for (int i = 0; i < retourJSONArray.length(); i++)
                                {
                                    JSONObject retourJSONObject = retourJSONArray.getJSONObject(i);

                                    if (retourJSONObject.getString("En_Attente_de").equals("Mise en quarantaine"))
                                    {
                                        Retour retour = new Retour(retourJSONObject);
                                        retourList.add(retour);
                                        insererUnRetourEnBDD(db, retour);
                                        JSONArray retourLigneJSONArray = retourJSONObject.getJSONArray("ph_retour_ligne");

                                        for (int k = 0; k < retourLigneJSONArray.length(); k++)
                                        {
                                            Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLigneJSONArray.getJSONObject(k)));
                                        }
                                    }
                                }

                                JSONArray serialisationJSONArray = response.getJSONArray("PH_Serialisation");
                                for(int j = 0; j < serialisationJSONArray.length(); j++)
                                {
                                    JSONObject serialisationObject = serialisationJSONArray.getJSONObject(j);
                                    PH_Serialisation serialisation = new PH_Serialisation(serialisationObject);
                                    PH_SerialisationOpenHelper.insererPH_SerialisationEnBDD(db, serialisation);
                                }

                            }
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                               // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                            }
                            else if(activityName.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocaleRetours", "alerte");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                headers.put("UserId", String.valueOf(utilisateur.getId()));
                headers.put("EtablissementId", String.valueOf(utilisateur.getEtablissementId()));
                return headers;
            }
        };
        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(obreq);
    }

    public static void insererRetourControleDesRetour(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        if (!OutilsGestionConnexionReseau.isServerAccessible(context)) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteControleRetours;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String erreur = "";
                            boolean etat = true;

                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    viderBasesDeDonnees(db);
                                    erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                    erreur = "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocaleRetours";
                                    etat = false;
                                }
                            } else {
                                JSONArray retoursJSONArray = response.getJSONArray("PH_Retours");
                                for (Retour retour : getRetoursByEnAttenteDe(db, context.getString(R.string.RepriseDemandee))) {
                                    for (Retour_Ligne retourLigne : Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour)) {
                                        Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigne);
                                        Produit produit = ProduitOpenHelper.getProduitByID(db, retourLigne.getCode_produit());
                                        Depot depot = DepotOpenHelper.getDepotParReference(db, retour.getRef_Depot_Origine());

                                        if(produit != null && depot != null)
                                        {
                                            for (Stock_Lot_Emplacement_Light stockLotEmplacementLight : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)) {
                                                Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stockLotEmplacementLight);
                                            }
                                        }
                                    }
                                    supprimerUnRetour(db, retour);
                                }
                                long rowID = 0;
                                for (int i = 0; i < retoursJSONArray.length(); i++) {
                                    JSONObject retourJSONObject = retoursJSONArray.getJSONObject(i);
                                    Retour retour = new Retour(retourJSONObject);

                                    if (retour.getEn_Attente_de().equals(context.getString(R.string.RepriseDemandee))) {
                                        rowID = insererUnRetourEnBDD(db, retour);
                                        if (rowID != -1) {
                                            JSONArray retourLignesJSONArray= retourJSONObject.getJSONArray("ph_retour_ligne");
                                            for (int k = 0; k < retourLignesJSONArray.length(); k++) {
                                                JSONObject retourLigneJSONObject = retourLignesJSONArray.getJSONObject(k);
                                                rowID = Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLignesJSONArray.getJSONObject(k)));
/*                                                if (rowID != -1) {
                                                    JSONArray stockLotEmplacementsJSONArray = retourLigneJSONObject.getJSONArray("ph_stock_lot_emplacements");

                                                    for (int y = 0; y < stockLotEmplacementsJSONArray.length(); y++) {
                                                        Stock_Lot_Emplacement_Light stockLotEmplacementLight = new Stock_Lot_Emplacement_Light(stockLotEmplacementsJSONArray.getJSONObject(y));
                                                        Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stockLotEmplacementLight);
                                                    }
                                                }*/
                                            }
                                        }
                                    }
                                }
                            }
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                                // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                            }
                            else if(activityName.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocaleRetours", "alerte");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                headers.put("UserId", String.valueOf(utilisateur.getId()));
                headers.put("EtablissementId", String.valueOf(utilisateur.getEtablissementId()));
                return headers;
            }
        };
        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(obreq);
    }

    public static void insererRetourRetourPUI(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        if (!OutilsGestionConnexionReseau.isServerAccessible(context)) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteRetourPUI;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String erreur = "";
                            boolean etat = true;

                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    viderBasesDeDonnees(db);
                                    erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                    erreur = "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocaleRetours";
                                    etat = false;
                                }
                            } else {
                                for (Retour retour : getAllRetoursByStatutEtEnAttenteDe(db, context.getString(R.string.statutEncours), context.getString(R.string.RetourPUIDemande))
                                        ) {
                                    List<Retour_Ligne> retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour);
                                    for (Retour_Ligne retourLigne : retourLigneList
                                            ) {
                                        Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigne);
                                    }
                                    supprimerUnRetour(db, retour);
                                }

                                JSONArray retourJSONArray = response.getJSONArray("PH_Retours");

                                for (int i = 0; i < retourJSONArray.length(); i++) {
                                    JSONObject retourJSONObject = retourJSONArray.getJSONObject(i);
                                    Retour retour = new Retour(retourJSONObject);

                                    if (retour.getEn_Attente_de().equals(context.getString(R.string.RetourPUIDemande)) && retour.getStatut().equals(context.getString(R.string.statutEncours))) {
                                        long rowID = insererUnRetourEnBDD(db, retour);
                                        if (rowID != -1) {
                                            JSONArray retourLigneJSONArray = retourJSONObject.getJSONArray("ph_retour_ligne");
                                            for (int k = 0; k < retourLigneJSONArray.length(); k++) {
                                                JSONObject retourLigneJSONObject = retourLigneJSONArray.getJSONObject(k);
                                                Retour_Ligne retourLigne = new Retour_Ligne(retourLigneJSONObject);
                                                Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, retourLigne);
                                            }
                                        }
                                    }
                                }
                            }
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                                // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                            }
                            else if(activityName.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocaleRetours", "alerte");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                headers.put("UserId", String.valueOf(utilisateur.getId()));
                headers.put("EtablissementId", String.valueOf(utilisateur.getEtablissementId()));
                return headers;
            }
        };
        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(obreq);
    }

    public static void insererRetourRetourFournisseur(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        if (!OutilsGestionConnexionReseau.isServerAccessible(context)) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteRetourFournisseur;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String erreur = "";
                            boolean etat = true;

                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    viderBasesDeDonnees(db);
                                    erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                    erreur = "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocaleRetours";
                                    etat = false;
                                }
                            } else {
                                JSONArray retoursJson = response.getJSONArray("PH_Retours");
                                for (Retour retour : getAllRetoursByStatutEtEnAttenteDe(db, context.getString(R.string.statutEncours), context.getString(R.string.RetourFRSDemandé))
                                        ) {
                                    List<Retour_Ligne> retourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour);
                                    for (Retour_Ligne retourLigne : retourLignes
                                            ) {
                                        Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigne);
                                    }
                                    supprimerUnRetour(db, retour);
                                }
                                for (int i = 0; i < retoursJson.length(); i++) {
                                    JSONObject retourJson = retoursJson.getJSONObject(i);
                                    Retour retour = new Retour(retourJson);

                                    if (retour.getEn_Attente_de().equals(context.getString(R.string.RetourFRSDemandé)) && retour.getStatut().equals(context.getString(R.string.statutEncours))) {
                                        long rowID = insererUnRetourEnBDD(db, retour);
                                        if (rowID != -1) {
                                            JSONArray retourLignesJson = retourJson.getJSONArray("ph_retour_ligne");
                                            for (int k = 0; k < retourLignesJson.length(); k++) {
                                                Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLignesJson.getJSONObject(k)));
                                            }
                                        }
                                    }
                                }
                            }
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                                // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                            }
                            else if(activityName.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocaleRetours", "alerte");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                headers.put("UserId", String.valueOf(utilisateur.getId()));
                headers.put("EtablissementId", String.valueOf(utilisateur.getEtablissementId()));
                return headers;
            }
        };
        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(obreq);
    }

    public static void insererRetourDestruction(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur)
    {
        if (!OutilsGestionConnexionReseau.isServerAccessible(context)) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteDestruction;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String erreur = "";
                            boolean etat = true;

                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    viderBasesDeDonnees(db);
                                    erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                    erreur = "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocaleRetours";
                                    etat = false;
                                }
                            } else {
                                JSONArray retoursJson = response.getJSONArray("PH_Retours");;
                                for (Retour retour : getAllRetoursByStatutEtEnAttenteDe(db, context.getString(R.string.statutEncours), context.getString(R.string.DestructionDemandée))
                                        ) {
                                    List<Retour_Ligne> retourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour);
                                    for (Retour_Ligne retourLigne : retourLignes
                                            ) {
                                        Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigne);
                                    }
                                    supprimerUnRetour(db, retour);
                                }
                                for (int i = 0; i < retoursJson.length(); i++) {
                                    JSONObject retourJson = retoursJson.getJSONObject(i);
                                    Retour retour = new Retour(retourJson);

                                    if (retour.getEn_Attente_de().equals(context.getString(R.string.DestructionDemandée)) && retour.getStatut().equals(context.getString(R.string.statutEncours))) {
                                        long rowID = insererUnRetourEnBDD(db, retour);
                                        if (rowID != -1) {
                                            JSONArray retourLignesJson = retourJson.getJSONArray("ph_retour_ligne");

                                            for (int k = 0; k < retourLignesJson.length(); k++) {
                                                Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLignesJson.getJSONObject(k)));
                                            }
                                        }
                                    }
                                }
                            }
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                                // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                            }
                            else if(activityName.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocaleRetours", "alerte");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                headers.put("UserId", String.valueOf(utilisateur.getId()));
                headers.put("EtablissementId", String.valueOf(utilisateur.getEtablissementId()));
                return headers;
            }
        };
        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(obreq);
    }

    public static List<Retour> getAllRetoursByStatutEtEnAttenteDe(SQLiteDatabase db, String statut, String enAttenteDe) {
        List<Retour> retourList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR + " WHERE " + Constantes.CLE_COL_STATUT_RETOUR + "=? and " + Constantes.CLE_COL_EN_ATTENTE_DE_RETOUR + "=?", new String[]{statut, enAttenteDe});

        while (cursor.moveToNext()) {
            retourList.add(new Retour(cursor));
        }

        cursor.close();
        cursor = null;
        return retourList;
    }

    public static Retour getRetourByID(SQLiteDatabase db, int id) {
        Retour retour = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR + " WHERE " + Constantes.CLE_COL__UID_RETOUR + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            retour = new Retour(cursor);
        }

        cursor.close();
        cursor = null;
        return retour;
    }

    public static Retour getRetourByNumero(SQLiteDatabase db, String numero) {
        Retour retour = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR + " WHERE " + Constantes.CLE_COL_NUMERO_RETOUR + "=?", new String[]{numero});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            retour = new Retour(cursor);
        }

        cursor.close();
        cursor = null;
        return retour;
    }

    public static Retour getRetourByPhiMR4UUID(SQLiteDatabase db, int id) {
        Retour retour = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR + " WHERE " + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            retour = new Retour(cursor);
        }

        cursor.close();
        cursor = null;
        return retour;
    }

    public long mettreAJourRetour(SQLiteDatabase db, Retour retour) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_RETOUR, retour.get_UID());
        contentValues.put(Constantes.CLE_COL_NUMERO_RETOUR, retour.getNumero());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_ORIGINE_RETOUR, retour.getRef_Depot_Origine());
        contentValues.put(Constantes.CLE_COL_CODE_PATIENT_RETOUR, retour.getCode_Patient());
        contentValues.put(Constantes.CLE_COL_INTITULE_RETOUR, retour.getIntitulé());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_DEST_RETOUR, retour.getRef_Depot_Dest());
        contentValues.put(Constantes.CLE_COL_STATUT_RETOUR, retour.getStatut());
        contentValues.put(Constantes.CLE_COL_DATE_RETOUR_RETOUR, retour.getDate_retour());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_RETOUR, retour.getMontant_TTC());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_RETOUR, retour.getCommentaire());
        contentValues.put(Constantes.CLE_COL_MOTIF_RETOUR, retour.getMotif());
        contentValues.put(Constantes.CLE_COL_DEVISE_RETOUR, retour.getDevise());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_RETOUR, retour.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_RETOUR, retour.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_RETOUR, retour.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_EN_ATTENTE_DE_RETOUR, retour.getEn_Attente_de());
        contentValues.put(Constantes.CLE_COL_DATE_REPRISE_RETOUR, retour.getDate_Reprise());
        contentValues.put(Constantes.CLE_COL_DATE_VALIDATION_RETOUR, retour.getDate_Validation());
        contentValues.put(Constantes.CLE_COL_PROVENANCE_REFERENCE_RETOUR, retour.getProvenance_Reference());
        contentValues.put(Constantes.CLE_COL_AVOIR_ATTENDU_RETOUR, retour.getAvoir_Attendu());
        contentValues.put(Constantes.CLE_COL_NOM_CHAUFFEUR_RETOUR, retour.getNom_Chauffeur());
        contentValues.put(Constantes.CLE_COL_PRENOM_CHAUFFEUR_RETOUR, retour.getPrenom_Chauffeur());
        contentValues.put(Constantes.CLE_COL_TRANSPORTEUR_RETOUR, retour.getTransporteur());
        contentValues.put(Constantes.CLE_COL_SIGNATURE_CHAUFFEUR, retour.getSignature_Chauffeur());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID, retour.getPhiMR4UUID());

        return db.update(Constantes.TABLE_RETOUR, contentValues, DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=" + String.valueOf(retour.getPhiMR4UUID()), null);
    }

    public static void supprimerUnRetour(SQLiteDatabase db, Retour retour) {
        db.delete(Constantes.TABLE_RETOUR, DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(retour.getPhiMR4UUID())});
    }

    public static List<Retour> getRetoursByEnAttenteDe(SQLiteDatabase db, String critereEnAttenteDe) {
        List<Retour> retourList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR + " WHERE " + Constantes.CLE_COL_EN_ATTENTE_DE_RETOUR + "=?", new String[]{critereEnAttenteDe});

        while (cursor.moveToNext()) {
            Retour retour = new Retour(cursor);

            retourList.add(retour);
        }

        cursor.close();
        cursor = null;
        return retourList;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_RETOUR = "Retour";

        public static final String CLE_COL_NUMERO_RETOUR = "Numero";
        public static final int NUM_COL_NUMERO_RETOUR = 1;
        public static final String TYPE_COL_NUMERO_RETOUR = "TEXT";
        public static final String CLE_COL_REF_DEPOT_ORIGINE_RETOUR = "Ref_Depot_Origine";
        public static final int NUM_COL_REF_DEPOT_ORIGINE_RETOUR = 2;
        public static final String TYPE_COL_REF_DEPOT_ORIGINE_RETOUR = "TEXT";
        public static final String CLE_COL_CODE_PATIENT_RETOUR = "Code_Patient";
        public static final int NUM_COL_CODE_PATIENT_RETOUR = 3;
        public static final String TYPE_COL_CODE_PATIENT_RETOUR = "INTEGER";
        public static final String CLE_COL_INTITULE_RETOUR = "Intitulé";
        public static final int NUM_COL_INTITULE_RETOUR = 4;
        public static final String TYPE_COL_INTITULE_RETOUR = "TEXT";
        public static final String CLE_COL_REF_DEPOT_DEST_RETOUR = "Ref_Depot_Dest";
        public static final int NUM_COL_REF_DEPOT_DEST_RETOUR = 5;
        public static final String TYPE_COL_REF_DEPOT_DEST_RETOUR = "TEXT";
        public static final String CLE_COL_STATUT_RETOUR = "Statut";
        public static final int NUM_COL_STATUT_RETOUR = 6;
        public static final String TYPE_COL_STATUT_RETOUR = "TEXT";
        public static final String CLE_COL_DATE_RETOUR_RETOUR = "Date_retour";
        public static final int NUM_COL_DATE_RETOUR_RETOUR = 7;
        public static final String TYPE_COL_DATE_RETOUR_RETOUR = "TEXT";
        public static final String CLE_COL_MONTANT_TTC_RETOUR = "Montant_TTC";
        public static final int NUM_COL_MONTANT_TTC_RETOUR = 8;
        public static final String TYPE_COL_MONTANT_TTC_RETOUR = "REAL";
        public static final String CLE_COL_COMMENTAIRE_RETOUR = "Commentaire";
        public static final int NUM_COL_COMMENTAIRE_RETOUR = 9;
        public static final String TYPE_COL_COMMENTAIRE_RETOUR = "TEXT";
        public static final String CLE_COL_MOTIF_RETOUR = "Motif";
        public static final int NUM_COL_MOTIF_RETOUR = 10;
        public static final String TYPE_COL_MOTIF_RETOUR = "TEXT";
        public static final String CLE_COL_DEVISE_RETOUR = "Devise";
        public static final int NUM_COL_DEVISE_RETOUR = 11;
        public static final String TYPE_COL_DEVISE_RETOUR = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_RETOUR = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_RETOUR = 12;
        public static final String TYPE_COL_SYS_DT_MAJ_RETOUR = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_RETOUR = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_RETOUR = 13;
        public static final String TYPE_COL_SYS_HEURE_MAJ_RETOUR = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_RETOUR = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_RETOUR = 14;
        public static final String TYPE_COL_SYS_USER_MAJ_RETOUR = "TEXT";
        public static final String CLE_COL_EN_ATTENTE_DE_RETOUR = "En_Attente_de";
        public static final int NUM_COL_EN_ATTENTE_DE_RETOUR = 15;
        public static final String TYPE_COL_EN_ATTENTE_DE_RETOUR = "TEXT";
        public static final String CLE_COL_DATE_REPRISE_RETOUR = "Date_Reprise";
        public static final int NUM_COL_DATE_REPRISE_RETOUR = 16;
        public static final String TYPE_COL_DATE_REPRISE_RETOUR = "TEXT";
        public static final String CLE_COL_DATE_VALIDATION_RETOUR = "Date_Validation";
        public static final int NUM_COL_DATE_VALIDATION_RETOUR = 17;
        public static final String TYPE_COL_DATE_VALIDATION_RETOUR = "TEXT";
        public static final String CLE_COL_PROVENANCE_REFERENCE_RETOUR = "Provenance_Reference";
        public static final int NUM_COL_PROVENANCE_REFERENCE_RETOUR = 18;
        public static final String TYPE_COL_PROVENANCE_REFERENCE_RETOUR = "TEXT";
        public static final String CLE_COL_AVOIR_ATTENDU_RETOUR = "Avoir_Attendu";
        public static final int NUM_COL_AVOIR_ATTENDU_RETOUR = 19;
        public static final String TYPE_COL_AVOIR_ATTENDU_RETOUR = "INTEGER";
        public static final String CLE_COL__UID_RETOUR = "_UID";
        public static final int NUM_COL__UID_RETOUR = 20;
        public static final String TYPE_COL__UID_RETOUR = "INTEGER";
        public static final String CLE_COL_NOM_CHAUFFEUR_RETOUR = "Nom_Chauffeur";
        public static final int NUM_COL_NOM_CHAUFFEUR_RETOUR = 21;
        public static final String TYPE_COL_NOM_CHAUFFEUR_RETOUR = "TEXT";
        public static final String CLE_COL_PRENOM_CHAUFFEUR_RETOUR = "Prenom_Chauffeur";
        public static final int NUM_COL_PRENOM_CHAUFFEUR_RETOUR = 22;
        public static final String TYPE_COL_PRENOM_CHAUFFEUR_RETOUR = "TEXT";
        public static final String CLE_COL_TRANSPORTEUR_RETOUR = "Transporteur";
        public static final int NUM_COL_TRANSPORTEUR_RETOUR = 23;
        public static final String TYPE_COL_TRANSPORTEUR_RETOUR = "TEXT";
        public static final String CLE_COL_SIGNATURE_CHAUFFEUR = "Signature_Chauffeur";
        public static final int NUM_COL_SIGNATURE_CHAUFFEUR = 24;
        public static final String TYPE_COL_SIGNATURE_CHAUFFEUR = "TEXT";

        public static final String CREATION_TABLE_RETOUR = "CREATE TABLE " + TABLE_RETOUR
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_NUMERO_RETOUR + " " + Constantes.TYPE_COL_NUMERO_RETOUR + " ,"
                + Constantes.CLE_COL_REF_DEPOT_ORIGINE_RETOUR + " " + Constantes.TYPE_COL_REF_DEPOT_ORIGINE_RETOUR + " ,"
                + Constantes.CLE_COL_CODE_PATIENT_RETOUR + " " + Constantes.TYPE_COL_CODE_PATIENT_RETOUR + " ,"
                + Constantes.CLE_COL_INTITULE_RETOUR + " " + Constantes.TYPE_COL_INTITULE_RETOUR + " ,"
                + Constantes.CLE_COL_REF_DEPOT_DEST_RETOUR + " " + Constantes.TYPE_COL_REF_DEPOT_DEST_RETOUR + " ,"
                + Constantes.CLE_COL_STATUT_RETOUR + " " + Constantes.TYPE_COL_STATUT_RETOUR + " ,"
                + Constantes.CLE_COL_DATE_RETOUR_RETOUR + " " + Constantes.TYPE_COL_DATE_RETOUR_RETOUR + " ,"
                + Constantes.CLE_COL_MONTANT_TTC_RETOUR + " " + Constantes.TYPE_COL_MONTANT_TTC_RETOUR + " ,"
                + Constantes.CLE_COL_COMMENTAIRE_RETOUR + " " + Constantes.TYPE_COL_COMMENTAIRE_RETOUR + " ,"
                + Constantes.CLE_COL_MOTIF_RETOUR + " " + Constantes.TYPE_COL_MOTIF_RETOUR + " ,"
                + Constantes.CLE_COL_DEVISE_RETOUR + " " + Constantes.TYPE_COL_DEVISE_RETOUR + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_RETOUR + " " + Constantes.TYPE_COL_SYS_DT_MAJ_RETOUR + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_RETOUR + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_RETOUR + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_RETOUR + " " + Constantes.TYPE_COL_SYS_USER_MAJ_RETOUR + " ,"
                + Constantes.CLE_COL_EN_ATTENTE_DE_RETOUR + " " + Constantes.TYPE_COL_EN_ATTENTE_DE_RETOUR + " ,"
                + Constantes.CLE_COL_DATE_REPRISE_RETOUR + " " + Constantes.TYPE_COL_DATE_REPRISE_RETOUR + " ,"
                + Constantes.CLE_COL_DATE_VALIDATION_RETOUR + " " + Constantes.TYPE_COL_DATE_VALIDATION_RETOUR + " ,"
                + Constantes.CLE_COL_PROVENANCE_REFERENCE_RETOUR + " " + Constantes.TYPE_COL_PROVENANCE_REFERENCE_RETOUR + " ,"
                + Constantes.CLE_COL_AVOIR_ATTENDU_RETOUR + " " + Constantes.TYPE_COL_AVOIR_ATTENDU_RETOUR + ","
                + Constantes.CLE_COL__UID_RETOUR + " " + Constantes.TYPE_COL__UID_RETOUR + ","
                + Constantes.CLE_COL_NOM_CHAUFFEUR_RETOUR + " " + Constantes.TYPE_COL_NOM_CHAUFFEUR_RETOUR + ","
                + Constantes.CLE_COL_PRENOM_CHAUFFEUR_RETOUR + " " + Constantes.TYPE_COL_PRENOM_CHAUFFEUR_RETOUR + ","
                + Constantes.CLE_COL_TRANSPORTEUR_RETOUR + " " + Constantes.TYPE_COL_TRANSPORTEUR_RETOUR + ","
                + Constantes.CLE_COL_SIGNATURE_CHAUFFEUR + " " + Constantes.TYPE_COL_SIGNATURE_CHAUFFEUR
                + ");";
    }
}