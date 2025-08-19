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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;

public class ActionUtilisateurOpenHelper extends DBOpenHelper {

    public ActionUtilisateurOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableActionUtilisateur(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_ACTION_UTILISATEUR, null, null);
    }

    public static long insererActionUtilisateurEnBDD(SQLiteDatabase db, ActionUtilisateur objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_ACTION_UTILISATEUR, objet.getId());
        contentValues.put(Constantes.CLE_COL_USERID_ACTION_UTILISATEUR, objet.getUserId());
        contentValues.put(Constantes.CLE_COL_DATE_ACTION_UTILISATEUR, objet.getDate());
        contentValues.put(Constantes.CLE_COL_SERVICE_ID_ACTION_UTILISATEUR, objet.getServiceId());
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_ID_ACTION_UTILISATEUR, objet.getEtablissementId());
        contentValues.put(Constantes.CLE_COL_STATUT_ACTION_UTILISATEUR, objet.getStatut());
        contentValues.put(Constantes.CLE_COL_CHAMPS_PARENT_ID, objet.getChampsParentId());
        contentValues.put(Constantes.CLE_COL_CHEMIN_PHOTO, objet.getCheminPhoto());
        contentValues.put(Constantes.CLE_COL_ACTION_NAME, objet.getActionName());

        long rowID = db.insert(Constantes.TABLE_ACTION_UTILISATEUR, null, contentValues);
        objet.setphiwms_mobileUUID((int) rowID);
        return rowID;
    }

    public static long mettreAJourActionUtilisateur(SQLiteDatabase db, ActionUtilisateur actionUtilisateur) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_ACTION_UTILISATEUR, actionUtilisateur.getId());
        contentValues.put(Constantes.CLE_COL_USERID_ACTION_UTILISATEUR, actionUtilisateur.getUserId());
        contentValues.put(Constantes.CLE_COL_DATE_ACTION_UTILISATEUR, actionUtilisateur.getDate());
        contentValues.put(Constantes.CLE_COL_SERVICE_ID_ACTION_UTILISATEUR, actionUtilisateur.getServiceId());
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_ID_ACTION_UTILISATEUR, actionUtilisateur.getEtablissementId());
        contentValues.put(Constantes.CLE_COL_STATUT_ACTION_UTILISATEUR, actionUtilisateur.getStatut());
        contentValues.put(Constantes.CLE_COL_CHAMPS_PARENT_ID, actionUtilisateur.getChampsParentId());
        contentValues.put(Constantes.CLE_COL_CHEMIN_PHOTO, actionUtilisateur.getCheminPhoto());
        contentValues.put(Constantes.CLE_COL_ACTION_NAME, actionUtilisateur.getActionName());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, actionUtilisateur.getPhiMR4UUID());

        long rowId = db.update(Constantes.TABLE_ACTION_UTILISATEUR, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + actionUtilisateur.getPhiMR4UUID(), null);

        return rowId;
    }

    public static ActionUtilisateur getActionUtilisateurByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        ActionUtilisateur actionUtilisateur = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_ACTION_UTILISATEUR + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " =? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            actionUtilisateur = new ActionUtilisateur(cursor);
        }
        cursor.close();
        cursor = null;
        return actionUtilisateur;
    }

    public static ActionUtilisateur getActionUtilisateurByid(SQLiteDatabase db, int id) {
        ActionUtilisateur actionUtilisateur = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_ACTION_UTILISATEUR + "      WHERE " + Constantes.CLE_COL_ID_ACTION_UTILISATEUR + " =? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            actionUtilisateur = new ActionUtilisateur(cursor);
        }
        cursor.close();
        cursor = null;
        return actionUtilisateur;
    }


    public static List<ActionUtilisateur> getAllAction(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_ACTION_UTILISATEUR, null);

        List<ActionUtilisateur> actionList = new ArrayList<>();

        while (cursor.moveToNext()) {
            ActionUtilisateur actionUtilisateur = new ActionUtilisateur(cursor);
            actionList.add(actionUtilisateur);
        }
        cursor.close();
        cursor = null;
        return actionList;
    }

    public static List<ActionUtilisateur> getAllActionNonTerminee(SQLiteDatabase db, int userId) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_ACTION_UTILISATEUR + " WHERE "+Constantes.CLE_COL_USERID_ACTION_UTILISATEUR+"=?", new String[]{String.valueOf(userId)});

        List<ActionUtilisateur> actionList = new ArrayList<>();

        while (cursor.moveToNext()) {
            ActionUtilisateur actionUtilisateur = new ActionUtilisateur(cursor);
            if(actionUtilisateur.getStatut().contentEquals("Terminée") || actionUtilisateur.getStatut().contentEquals("Annulée"))
            {
                Date date1 = new Date();
                Date date2 = null;
                String dateString = actionUtilisateur.getDate() ;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date2 = format.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = date1.getTime() - date2.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if(days < 7)
                {
                    actionList.add(actionUtilisateur);
                }
            }
            else
            {
                actionList.add(actionUtilisateur);
            }

        }
        cursor.close();
        cursor = null;
        return actionList;
    }


    public static Integer getNbActionNonTerminee(SQLiteDatabase db, int userId) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_ACTION_UTILISATEUR + " WHERE "+Constantes.CLE_COL_USERID_ACTION_UTILISATEUR+"=?", new String[]{String.valueOf(userId)});

        int compteur = 0;

        while (cursor.moveToNext()) {
            ActionUtilisateur actionUtilisateur = new ActionUtilisateur(cursor);
            if(actionUtilisateur.getStatut().contentEquals("Terminée") || actionUtilisateur.getStatut().contentEquals("Annulée"))
            {
                Date date1 = new Date();
                Date date2 = null;
                String dateString = actionUtilisateur.getDate() ;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date2 = format.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = date1.getTime() - date2.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if(days < 7)
                {
                   compteur ++;
                }
            }
            else
            {
                compteur ++;
            }

        }
        cursor.close();
        cursor = null;
        return compteur;
    }

    public static void insererBDDLocaleActionUtilisatuer(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        final String tableNom = "ActionUtilisateur";
        final String erreurSynchronisationLibelle = "Actions utilisateurs non synchronisés";

        if (!statutConnexion) {
            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
        }
        else{
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteActionUtilisateur;
            RequestQueue requestQueue = new Volley().newRequestQueue(context);

            viderTableActionUtilisateur(db);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String erreur = "";
                        boolean etat = true;
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            etat = false;
                            erreur = response.getString("erreur");
                            if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                //viderBasesDeDonnees(db);
                                erreur = "Votre session a expirée, veuillez vous reconnecter.";
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                            } else if (!erreur.equals("Aucune Action Utilisateur trouvée")) {
                                erreur = "";
                                etat = true;

                            }
                            else{
                                etat = true;
                            }
                        } else {

                            viderTableActionUtilisateur(db);
                            JSONArray actionUtilisateurJSONArray = response.getJSONArray("ActionUtilisateur");

                            for (int i = 0; i < actionUtilisateurJSONArray.length(); i++) {
                                JSONObject actionUtilisateurJSONObject = actionUtilisateurJSONArray.getJSONObject(i);
                                ActionUtilisateur actionUtilisateur = new ActionUtilisateur(actionUtilisateurJSONObject);

                                long rowID = insererActionUtilisateurEnBDD(db, actionUtilisateur);
                            }
                        }
                        ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, etat, erreur);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error");
                            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
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
                    return 70000;
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
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_ACTION_UTILISATEUR = "ActionUtilisateur";

        public static final String CLE_COL_ID_ACTION_UTILISATEUR = "Id";
        public static final int NUM_COL_ID_ACTION_UTILISATEUR = 1;
        public static final String TYPE_COL_ID_ACTION_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_USERID_ACTION_UTILISATEUR = "UserId";
        public static final int NUM_COL_USERID_ACTION_UTILISATEUR = 2;
        public static final String TYPE_COL_USERID_ACTION_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_DATE_ACTION_UTILISATEUR = "Date";
        public static final int NUM_COL_DATE_ACTION_UTILISATEUR = 3;
        public static final String TYPE_COL_DATE_ACTION_UTILISATEUR = "TEXT";

        public static final String CLE_COL_SERVICE_ID_ACTION_UTILISATEUR = "ServiceId";
        public static final int NUM_COL_SERVICE_ID_ACTION_UTILISATEUR = 4;
        public static final String TYPE_COL_SERVICE_ID_ACTION_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_ETABLISSEMENT_ID_ACTION_UTILISATEUR = "EtablissementId";
        public static final int NUM_COL_ETABLISSEMENT_ID_ACTION_UTILISATEUR = 5;
        public static final String TYPE_COL_ETABLISSEMENT_ID_ACTION_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_STATUT_ACTION_UTILISATEUR = "Statut";
        public static final int NUM_COL_STATUT_ACTION_UTILISATEUR = 6;
        public static final String TYPE_COL_STATUT_ACTION_UTILISATEUR = "TEXT";

        public static final String CLE_COL_CHAMPS_PARENT_ID = "Champs_Parent_Id";
        public static final int NUM_COL_CHAMPS_PARENT_ID = 7;
        public static final String TYPE_COL_CHAMPS_PARENT_ID = "INTEGER";

        public static final String CLE_COL_CHEMIN_PHOTO = "CheminPhoto";
        public static final int NUM_COL_CHEMIN_PHOTO = 8;
        public static final String TYPE_COL_CHEMIN_PHOTO = "TEXT";

        public static final String CLE_COL_ACTION_NAME = "ActionName";
        public static final int NUM_COL_ACTION_NAME = 9;
        public static final String TYPE_COL_ACTION_NAME = "TEXT";

        public static final String CREATION_TABLE_ACTION_UTILISATEUR = " CREATE TABLE  " + Constantes.TABLE_ACTION_UTILISATEUR
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_ID_ACTION_UTILISATEUR + " " + Constantes.TYPE_COL_ID_ACTION_UTILISATEUR + " , "
                + Constantes.CLE_COL_USERID_ACTION_UTILISATEUR + " " + Constantes.TYPE_COL_USERID_ACTION_UTILISATEUR + " , "
                + Constantes.CLE_COL_DATE_ACTION_UTILISATEUR + " " + Constantes.TYPE_COL_DATE_ACTION_UTILISATEUR + " , "
                + Constantes.CLE_COL_SERVICE_ID_ACTION_UTILISATEUR + " " + Constantes.TYPE_COL_SERVICE_ID_ACTION_UTILISATEUR + " , "
                + Constantes.CLE_COL_ETABLISSEMENT_ID_ACTION_UTILISATEUR + " " + Constantes.TYPE_COL_ETABLISSEMENT_ID_ACTION_UTILISATEUR + " , "
                + Constantes.CLE_COL_STATUT_ACTION_UTILISATEUR + " " + Constantes.TYPE_COL_STATUT_ACTION_UTILISATEUR + " , "
                + Constantes.CLE_COL_CHAMPS_PARENT_ID + " " + Constantes.TYPE_COL_CHAMPS_PARENT_ID + " , "
                + Constantes.CLE_COL_CHEMIN_PHOTO + " " + Constantes.TYPE_COL_CHEMIN_PHOTO + " , "
                + Constantes.CLE_COL_ACTION_NAME + " " + Constantes.TYPE_COL_ACTION_NAME
                + " ); ";

    }
}

