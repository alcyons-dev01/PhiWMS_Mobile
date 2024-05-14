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

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.Classes.PerimetreFonctionnel;
import fr.alcyons.phiwms_mobile.Classes.Service;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
public class ServiceOpenHelper extends DBOpenHelper {

    public ServiceOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static boolean verifierExistanceService(SQLiteDatabase db, Service service) {
        Cursor curseur = db.rawQuery("SELECT * FROM " + Constantes.TABLE_SERVICE + " WHERE id = ?", new String[]{String.valueOf(service.getId())});

        int nbService = curseur.getCount();
        curseur.close();
        curseur = null;

        // Si on trouve moins d'un résultat, c'est que le service n'existe pas
        return nbService >= 1;
    }

    public static List<Service> recupererListeServiceUtilisateur(Utilisateur utilisateur, SQLiteDatabase db) {
        List<Service> serviceList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + UtilisateurOpenHelper.Constantes.TABLE_UTILISATEUR + " WHERE id = ? ", new String[]{String.valueOf(utilisateur.getId())});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            try {
                String contenuListeService = cursor.getString(UtilisateurOpenHelper.Constantes.NUM_COL_SERVICES_HABILITES_UTILISATEUR);
                if (contenuListeService != null) {
                    JSONObject serviceJSONObject = new JSONObject(contenuListeService);
                    JSONArray serviceJSONArray = serviceJSONObject.getJSONArray("Services");
                    for (int i = 0; i < serviceJSONArray.length(); i++) {
                        JSONObject serviceJSONObjectCourant = serviceJSONArray.getJSONObject(i);

                        int id = serviceJSONObjectCourant.getInt("id");
                        String nom = OutilsGestionClasses.recupererString(serviceJSONObjectCourant.getString("nom"));
                        int ordre = serviceJSONObjectCourant.getInt("ordre");
                        int idPerimetreFonctionnel = serviceJSONObjectCourant.getInt("idPerimetreFonctionnel");
                        String nomPerimetrefonctionnel = OutilsGestionClasses.recupererString(serviceJSONObjectCourant.getString("nomPerimetrefonctionnel"));
                        String statut = OutilsGestionClasses.recupererString(serviceJSONObjectCourant.getString("statut"));
                        int indicateur = 0;
                        String descriptionServiceCourant = "";
                        String videoServiceCourant = "";
                        String whitePaperServiceCourant= "";
                        int score = 0;

                        Service serviceBDD = ServiceOpenHelper.getServiceByID(db, id);
                        int phiwms_mobileuuid = 0;
                        if(serviceBDD != null)
                        {
                            phiwms_mobileuuid = serviceBDD.getPhiMR4UUID();
                            descriptionServiceCourant = serviceBDD.getDescription();
                            videoServiceCourant = serviceBDD.getLien_video();
                            whitePaperServiceCourant = serviceBDD.getWhitePaper();
                            score = serviceBDD.getScore();
                        }
                        Service service = new Service(id, nom, ordre, idPerimetreFonctionnel, nomPerimetrefonctionnel, statut, indicateur, descriptionServiceCourant, videoServiceCourant, whitePaperServiceCourant, score, phiwms_mobileuuid);
                        serviceList.add(service);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        cursor = null;
        return serviceList;
    }

    public static Cursor getServicesSeparesParPerimetreFonctionnel(SQLiteDatabase db) {
        return db.query(true, Constantes.TABLE_SERVICE, new String[]{Constantes.CLE_COL_ID_SERVICE, Constantes.CLE_COL_NOM_SERVICE, Constantes.CLE_COL_ORDRE_SERVICE, Constantes.CLE_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE, Constantes.CLE_COL_NOM_PERIMETRE_FONCTIONNEL_SERVICE, Constantes.CLE_COL_STATUT_SERVICE, Constantes.CLE_COL_INDICATEUR_SERVICE}, null, null, Constantes.CLE_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE, null, null, null);
    }

    public static List<Service> getServiceParPerimetreFonctionnel(SQLiteDatabase db, PerimetreFonctionnel perimetreFonctionnel) {
        List<Service> serviceList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_SERVICE + " WHERE " + Constantes.CLE_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE + "=? ORDER BY "+Constantes.CLE_COL_ORDRE_SERVICE, new String[]{String.valueOf(perimetreFonctionnel.getId())});

        while (cursor.moveToNext()) {
            serviceList.add(new Service(cursor));
        }

        cursor.close();
        cursor = null;
        return serviceList;
    }

    public static Service getServiceByID(SQLiteDatabase db, int id) {
        Service service = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_SERVICE + " WHERE " + Constantes.CLE_COL_ID_SERVICE + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            service = new Service(cursor);
        }

        cursor.close();
        cursor = null;
        return service;
    }

    public static Service getServiceByName(SQLiteDatabase db, String name) {
        Service service = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_SERVICE + " WHERE " + Constantes.CLE_COL_NOM_SERVICE + "=?", new String[]{name});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            service = new Service(cursor);
        }
        cursor.close();
        cursor = null;

        return service;
    }

    public static long insererUnServiceEnBD(SQLiteDatabase db, Service service) {
        // Récupération des valeurs du service à insérer
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_SERVICE, service.getId());
        contentValues.put(Constantes.CLE_COL_NOM_SERVICE, service.getNom());
        contentValues.put(Constantes.CLE_COL_ORDRE_SERVICE, service.getOrdre());
        contentValues.put(Constantes.CLE_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE, service.getIdPerimetreFonctionnel());
        contentValues.put(Constantes.CLE_COL_NOM_PERIMETRE_FONCTIONNEL_SERVICE, service.getNomPerimetrefonctionnel());
        contentValues.put(Constantes.CLE_COL_STATUT_SERVICE, service.getStatut());
        contentValues.put(Constantes.CLE_COL_INDICATEUR_SERVICE, service.getIndicateur());
        contentValues.put(Constantes.CLE_COL_DESCRIPTION_SERVICE, service.getDescription());
        contentValues.put(Constantes.CLE_COL_WHITEPAPER_SERVICE, service.getWhitePaper());
        contentValues.put(Constantes.CLE_COL_VIDEO_SERVICE, service.getLien_video());
        contentValues.put(Constantes.CLE_COL_SCORE, service.getScore());

        // Insertion du service en BDD
        long rowID = db.insert(Constantes.TABLE_SERVICE, null, contentValues);

        service.setphiwms_mobileUUID((int) rowID);

        return rowID;
    }

    public static long mettreAJourUnServiceEnBD(SQLiteDatabase db, Service service) {
        // Récupération des valeurs du service à insérer
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_SERVICE, service.getId());
        contentValues.put(Constantes.CLE_COL_NOM_SERVICE, service.getNom());
        contentValues.put(Constantes.CLE_COL_ORDRE_SERVICE, service.getOrdre());
        contentValues.put(Constantes.CLE_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE, service.getIdPerimetreFonctionnel());
        contentValues.put(Constantes.CLE_COL_NOM_PERIMETRE_FONCTIONNEL_SERVICE, service.getNomPerimetrefonctionnel());
        contentValues.put(Constantes.CLE_COL_STATUT_SERVICE, service.getStatut());
        contentValues.put(Constantes.CLE_COL_INDICATEUR_SERVICE, service.getIndicateur());
        contentValues.put(Constantes.CLE_COL_DESCRIPTION_SERVICE, service.getDescription());
        contentValues.put(Constantes.CLE_COL_WHITEPAPER_SERVICE, service.getWhitePaper());
        contentValues.put(Constantes.CLE_COL_VIDEO_SERVICE, service.getLien_video());
        contentValues.put(Constantes.CLE_COL_SCORE, service.getScore());

        // Insertion du service en BDD
        return db.update(Constantes.TABLE_SERVICE, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + service.getPhiMR4UUID(), null);

    }

    public static void viderTableService(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_SERVICE, null, null);
    }

    public static void insererBDDLocaleServicesEtPerimetresFonctionnelsphiwms_mobile(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        final String tableNom = "Service";
        final String erreurSynchronisationLibelle = "Services non synchronisés";

        if (!statutConnexion) {
            String activityName = context.getClass().getSimpleName();
            if(activityName.contentEquals("AuthentificationActivity"))
            {
                ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
            }
        }
        else{
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteServices;
            RequestQueue requestQueueServices = Volley.newRequestQueue(context);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String erreur = "";
                                boolean etat = true;
                                int resultCount = response.getInt("resultCount");
                                if (resultCount == 0) {
                                    erreur = response.getString("erreur");
                                    etat = false;
                                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                        viderBasesDeDonnees(db);
                                        erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    } else if (!erreur.contentEquals("Aucun SYS_Services trouvé")) {
                                        erreur = "Erreur API Services";
                                    }
                                } else {
                                    viderTableService(db);
                                    JSONArray serviceJSONArray = response.getJSONArray("Services");
                                    int compteurReussite = 0;

                                    for (int i = 0; i < serviceJSONArray.length(); i++) {
                                        // Récupération du service courant
                                        JSONObject serviceJSONObject = serviceJSONArray.getJSONObject(i);

                                        // Récupération des attributs du service courant
                                        int idServiceCourant = serviceJSONObject.getInt("_UID");
                                        String nomServiceCourant = serviceJSONObject.getString("name");
                                        int ordreServiceCourant = serviceJSONObject.getInt("ordre");
                                        int idPerimetreFonctionnelServiceCourant = serviceJSONObject.getInt("perimetreFonctionnel_id");
                                        String nomPerimetreFonctionnelServiceCourant = serviceJSONObject.getString("perimetreFonctionnel");
                                        String statutServiceCourant = serviceJSONObject.getString("statut");
                                        int indicateurServiceCourant = 0;
                                        String descriptionServiceCourant = serviceJSONObject.getString("description");
                                        String videoServiceCourant = serviceJSONObject.getString("video");
                                        String whitePaperServiceCourant= serviceJSONObject.getString("whitePaper");
                                        int score = serviceJSONObject.getInt("score");

                                        // Création du service
                                        Service service = new Service(idServiceCourant, nomServiceCourant, ordreServiceCourant, idPerimetreFonctionnelServiceCourant, nomPerimetreFonctionnelServiceCourant, statutServiceCourant, indicateurServiceCourant,descriptionServiceCourant, videoServiceCourant, whitePaperServiceCourant, score);

                                        // insertion du service en bdd
                                        long rowID = insererUnServiceEnBD(db, service);
                                        if (rowID != -1) {
                                            compteurReussite++;
                                        }
                                    }
                                    if (resultCount != compteurReussite) {
                                        erreur = "Veuillez contacter la société Alcyons ! \n " + String.valueOf(resultCount - compteurReussite) + " services n'ont pas été insérés.";
                                        etat = false;
                                    }
                                    // Vider en BDD locale la liste des périmètres fonctionnels
                                    PerimetreFonctionnelOpenHelper.viderTablePerimetreFonctionnel(db);

                                    // Inserer en BDD locales les périmètres fonctionnels en fonction des services
                                    PerimetreFonctionnelOpenHelper.insererPerimetresFonctionnels(db);
                                }
                                String activityName = context.getClass().getSimpleName();
                                if(activityName.contentEquals("AuthentificationActivity"))
                                {
                                    ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, etat, erreur);
                                }
                                else if(activityName.contentEquals("ServiceConnexionDirecteActivity"))
                                {
                                    ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Service volley", error.toString());
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                                ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
                            }
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
            requestQueueServices.add(obreq);
        }

    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_SERVICE = "Service";


        public static final String CLE_COL_NOM_SERVICE = "nom";
        public static final int NUM_COL_NOM_SERVICE = 1;
        public static final String TYPE_COL_NOM_SERVICE = "TEXT";

        public static final String CLE_COL_ORDRE_SERVICE = "ordre";
        public static final int NUM_COL_ORDRE_SERVICE = 2;
        public static final String TYPE_COL_ORDRE_SERVICE = "INTEGER";

        public static final String CLE_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE = "idPerimetreFonctionnel";
        public static final int NUM_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE = 3;
        public static final String TYPE_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE = "INTEGER";

        public static final String CLE_COL_NOM_PERIMETRE_FONCTIONNEL_SERVICE = "nomPerimetreFonctionnel";
        public static final int NUM_COL_NOM_PERIMETRE_FONCTIONNEL_SERVICE = 4;
        public static final String TYPE_COL_NOM_PERIMETRE_FONCTIONNEL_SERVICE = "TEXT";

        public static final String CLE_COL_ID_SERVICE = "id";
        public static final int NUM_COL_ID_SERVICE = 5;
        public static final String TYPE_COL_ID_SERVICE = "INTEGER";

        public static final String CLE_COL_STATUT_SERVICE = "statut";
        public static final int NUM_COL_STATUT_SERVICE = 6;
        public static final String TYPE_COL_STATUT_SERVICE = "TEXT";

        public static final String CLE_COL_INDICATEUR_SERVICE = "indicateur";
        public static final int NUM_COL_INDICATEUR_SERVICE = 7;
        public static final String TYPE_COL_INDICATEUR_SERVICE = "INTEGER";

        public static final String CLE_COL_DESCRIPTION_SERVICE = "description";
        public static final int NUM_COL_DESCRIPTION_SERVICE = 8;
        public static final String TYPE_COL_DESCRIPTION_SERVICE = "TEXT";

        public static final String CLE_COL_VIDEO_SERVICE = "lien_video";
        public static final int NUM_COL_VIDEO_SERVICE = 9;
        public static final String TYPE_COL_VIDEO_SERVICE = "TEXT";

        public static final String CLE_COL_WHITEPAPER_SERVICE = "whitePaper";
        public static final int NUM_COL_WHITEPAPER_SERVICE = 10;
        public static final String TYPE_COL_WHITEPAPER_SERVICE = "TEXT";

        public static final String CLE_COL_SCORE = "score";
        public static final int NUM_COL_SCORE = 11;
        public static final String TYPE_COL_SCORE = "INTEGER";


        public static final String CREATION_TABLE_SERVICE = "CREATE TABLE "
                + Constantes.TABLE_SERVICE + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_NOM_SERVICE + " " + Constantes.TYPE_COL_NOM_SERVICE + ","
                + Constantes.CLE_COL_ORDRE_SERVICE + " " + Constantes.TYPE_COL_ORDRE_SERVICE + ","
                + Constantes.CLE_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE + " " + Constantes.TYPE_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE + ","
                + Constantes.CLE_COL_NOM_PERIMETRE_FONCTIONNEL_SERVICE + " " + Constantes.TYPE_COL_NOM_PERIMETRE_FONCTIONNEL_SERVICE + ","
                + Constantes.CLE_COL_ID_SERVICE + " " + Constantes.TYPE_COL_ID_SERVICE + ","
                + Constantes.CLE_COL_STATUT_SERVICE + " " + Constantes.TYPE_COL_STATUT_SERVICE + ","
                + Constantes.CLE_COL_INDICATEUR_SERVICE + " " + Constantes.TYPE_COL_INDICATEUR_SERVICE + ","
                + Constantes.CLE_COL_DESCRIPTION_SERVICE + " " + Constantes.TYPE_COL_DESCRIPTION_SERVICE + ","
                + Constantes.CLE_COL_VIDEO_SERVICE + " " + Constantes.TYPE_COL_VIDEO_SERVICE + ","
                + Constantes.CLE_COL_WHITEPAPER_SERVICE + " " + Constantes.TYPE_COL_WHITEPAPER_SERVICE + ","
                + Constantes.CLE_COL_SCORE + " " + Constantes.TYPE_COL_SCORE
                + ");";

    }
}
