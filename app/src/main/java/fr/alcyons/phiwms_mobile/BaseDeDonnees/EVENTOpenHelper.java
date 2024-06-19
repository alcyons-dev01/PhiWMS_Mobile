package fr.alcyons.phiwms_mobile.BaseDeDonnees;
import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.Classes.EVENT;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class EVENTOpenHelper extends DBOpenHelper {

    public EVENTOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static String getDateProchaineLivraison(SQLiteDatabase db, int depotUID)
    {
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        String dateProchaineLivraison = "";
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_EVENT + " WHERE " + Constantes.CLE_COL_DATE_EVENT_EVENT + " >? AND "+Constantes.CLE_COL_ID_RESSOURCE_EVENT+" =?", new String[]{fDate, String.valueOf(depotUID)});

        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            EVENT eventCourant = new EVENT(cursor);

            dateProchaineLivraison = eventCourant.getJour_de()+"/"+eventCourant.getMois_de()+"/"+eventCourant.getAnnee_de();
        }
        cursor.close();
        cursor = null;
        return dateProchaineLivraison;
    }

    public static String getDateSecondeProchaineLivraison(SQLiteDatabase db, int depotUID, String dateLivraisonSuivante)
    {
        String[] tabDate = dateLivraisonSuivante.split("/");
        dateLivraisonSuivante = tabDate[tabDate.length]+"-"+tabDate[1]+"-"+tabDate[0];
        String dateProchaineLivraison = "";
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_EVENT + " WHERE " + Constantes.CLE_COL_DATE_EVENT_EVENT + " >? AND "+Constantes.CLE_COL_ID_RESSOURCE_EVENT+" =?", new String[]{dateLivraisonSuivante, String.valueOf(depotUID)});

        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            EVENT eventCourant = new EVENT(cursor);

            dateProchaineLivraison = eventCourant.getJour_de()+"/"+eventCourant.getMois_de()+"/"+eventCourant.getAnnee_de();
        }
        cursor.close();
        cursor = null;
        return dateProchaineLivraison;
    }

    public static long insererEVENTEnBDD(SQLiteDatabase db, EVENT objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_EVENT, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_DATE_EVENT_EVENT, objet.getDate_event());
        contentValues.put(Constantes.CLE_COL_ID_RESSOURCE_EVENT, objet.getID_Ressource());
        contentValues.put(Constantes.CLE_COL_JOUR_EVENT_EVENT, objet.getJour_event());
        contentValues.put(Constantes.CLE_COL_SEMAINE_EVENT_EVENT, objet.getSemaine_event());
        contentValues.put(Constantes.CLE_COL_MOIS_DE_EVENT, objet.getMois_de());
        contentValues.put(Constantes.CLE_COL_JOUR_DE_EVENT, objet.getJour_de());
        contentValues.put(Constantes.CLE_COL_ANNEE_DE_EVENT, objet.getAnnee_de());
        contentValues.put(Constantes.CLE_COL_MOIS_LIVRAISON_EVENT, objet.getMois_livraison());
        contentValues.put(Constantes.CLE_COL_MOISREFERENCE_EVENT, objet.getMoisReference());
        contentValues.put(Constantes.CLE_COL_TOURNEEID_EVENT, objet.getTourneeID());
        long rowID = db.insert(Constantes.TABLE_EVENT, null, contentValues);
        objet.setphiwms_mobileUUID((int) rowID);
        return rowID;
    }

    public static void insererBDDLocaleEvent(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        final String tableNom = "Events";
        final String erreurSynchronisationLibelle = "Events non synchronisées";

        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteEvent;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null, response -> {
            try {
                String erreur = "";
                boolean etat = true;
                int resultCount = response.getInt("resultCount");
                if (resultCount == 0) {
                    etat = false;
                    erreur = response.getString("erreur");
                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                        DBOpenHelper.viderBasesDeDonnees(db);
                        erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                        erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                    } else if (!erreur.equals("Aucun Events trouvé")) {
                    }
                    else{
                        etat = true;
                    }
                } else {
                    JSONArray eventJSONArray = response.getJSONArray("EVENTS");
                    for (int i = 0; i < eventJSONArray.length(); i++) {
                        JSONObject eventJSONObject = eventJSONArray.getJSONObject(i);
                        EVENT event = new EVENT(eventJSONObject);

                        long rowID = EVENTOpenHelper.insererEVENTEnBDD(db, event);
                    }
                }
                ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, etat, erreur);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
                error -> {
                    Log.e("Event volley", error.toString());
                    ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
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


    public static class Constantes implements BaseColumns {
        public static final String TABLE_EVENT = "EVENT";
        public static final String CLE_COL__UID_EVENT = "_UID";
        public static final int NUM_COL__UID_EVENT = 1;
        public static final String TYPE_COL__UID_EVENT = "INTEGER";
        public static final String CLE_COL_DATE_EVENT_EVENT = "Date_event";
        public static final int NUM_COL_DATE_EVENT_EVENT = 2;
        public static final String TYPE_COL_DATE_EVENT_EVENT = "TEXT";
        public static final String CLE_COL_ID_RESSOURCE_EVENT = "ID_Ressource";
        public static final int NUM_COL_ID_RESSOURCE_EVENT = 3;
        public static final String TYPE_COL_ID_RESSOURCE_EVENT = "INTEGER";
        public static final String CLE_COL_JOUR_EVENT_EVENT = "Jour_event";
        public static final int NUM_COL_JOUR_EVENT_EVENT = 4;
        public static final String TYPE_COL_JOUR_EVENT_EVENT = "TEXT";
        public static final String CLE_COL_SEMAINE_EVENT_EVENT = "Semaine_event";
        public static final int NUM_COL_SEMAINE_EVENT_EVENT = 5;
        public static final String TYPE_COL_SEMAINE_EVENT_EVENT = "INTEGER";
        public static final String CLE_COL_MOIS_DE_EVENT = "mois_de";
        public static final int NUM_COL_MOIS_DE_EVENT = 6;
        public static final String TYPE_COL_MOIS_DE_EVENT = "TEXT";
        public static final String CLE_COL_JOUR_DE_EVENT = "Jour_de";
        public static final int NUM_COL_JOUR_DE_EVENT = 7;
        public static final String TYPE_COL_JOUR_DE_EVENT = "TEXT";
        public static final String CLE_COL_ANNEE_DE_EVENT = "annee_de";
        public static final int NUM_COL_ANNEE_DE_EVENT = 8;
        public static final String TYPE_COL_ANNEE_DE_EVENT = "TEXT";
        public static final String CLE_COL_MOIS_LIVRAISON_EVENT = "Mois_livraison";
        public static final int NUM_COL_MOIS_LIVRAISON_EVENT = 9;
        public static final String TYPE_COL_MOIS_LIVRAISON_EVENT = "TEXT";
        public static final String CLE_COL_MOISREFERENCE_EVENT = "moisReference";
        public static final int NUM_COL_MOISREFERENCE_EVENT = 10;
        public static final String TYPE_COL_MOISREFERENCE_EVENT = "TEXT";
        public static final String CLE_COL_TOURNEEID_EVENT = "TourneeID";
        public static final int NUM_COL_TOURNEEID_EVENT = 11;
        public static final String TYPE_COL_TOURNEEID_EVENT = "INTEGER";

        public static final String CREATION_TABLE_EVENT = " CREATE TABLE       " + Constantes.TABLE_EVENT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL__UID_EVENT + " " + Constantes.TYPE_COL__UID_EVENT + " , "
                + Constantes.CLE_COL_DATE_EVENT_EVENT + " " + Constantes.TYPE_COL_DATE_EVENT_EVENT + " , "
                + Constantes.CLE_COL_ID_RESSOURCE_EVENT + " " + Constantes.TYPE_COL_ID_RESSOURCE_EVENT + " , "
                + Constantes.CLE_COL_JOUR_EVENT_EVENT + " " + Constantes.TYPE_COL_JOUR_EVENT_EVENT + " , "
                + Constantes.CLE_COL_SEMAINE_EVENT_EVENT + " " + Constantes.TYPE_COL_SEMAINE_EVENT_EVENT + " , "
                + Constantes.CLE_COL_MOIS_DE_EVENT + " " + Constantes.TYPE_COL_MOIS_DE_EVENT + " , "
                + Constantes.CLE_COL_JOUR_DE_EVENT + " " + Constantes.TYPE_COL_JOUR_DE_EVENT + " , "
                + Constantes.CLE_COL_ANNEE_DE_EVENT + " " + Constantes.TYPE_COL_ANNEE_DE_EVENT + " , "
                + Constantes.CLE_COL_MOIS_LIVRAISON_EVENT + " " + Constantes.TYPE_COL_MOIS_LIVRAISON_EVENT + " , "
                + Constantes.CLE_COL_MOISREFERENCE_EVENT + " " + Constantes.TYPE_COL_MOISREFERENCE_EVENT + " , "
                + Constantes.CLE_COL_TOURNEEID_EVENT + " " + Constantes.TYPE_COL_TOURNEEID_EVENT
                + " ); ";

    }
}
