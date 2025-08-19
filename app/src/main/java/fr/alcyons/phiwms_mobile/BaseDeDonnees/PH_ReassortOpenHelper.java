package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import static fr.alcyons.phiwms_mobile.BaseDeDonnees.DotationOpenHelper.viderTableDotation;

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

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.Classes.PH_Reassort;
import fr.alcyons.phiwms_mobile.Classes.PH_Reassort_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class PH_ReassortOpenHelper extends DBOpenHelper {

    public PH_ReassortOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static long insererPH_ReassortEnBDD(SQLiteDatabase db, PH_Reassort objet) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(Constantes.CLE_COL_CODE_PH_REASSORT, objet.getCode());
        contentValues.put(Constantes.CLE_COL_LISTE_PH_REASSORT, objet.getListe());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PH_REASSORT, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PH_REASSORT, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PH_REASSORT, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_PH_REASSORT, objet.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_FREQUENCE_PH_REASSORT, objet.getFrequence());
        contentValues.put(Constantes.CLE_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT, objet.isSynchroDM_Medicament());
        contentValues.put(Constantes.CLE_COL_SYNCHRODM_DMDMS_PH_REASSORT, objet.isSynchroDM_DMDMS());
        contentValues.put(Constantes.CLE_COL_VALORISATION_TTC_PH_REASSORT, objet.getValorisation_TTC());

        long rowID = db.insert(Constantes.TABLE_PH_REASSORT, null, contentValues);
        objet.setphiwms_mobileUUID((int) rowID);
        return rowID;
    }

    public static void insererBDDLocaleReassort(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        final String tableNom = "Dotations";
        final String erreurSynchronisationLibelle = "Dotations non synchronisées";

        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequetePH_Reassort;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        viderTableDotation(db);
        Detail_DotOpenHelper.viderTableDetail_Dot(db);

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
                            //DBOpenHelper.viderBasesDeDonnees(db);
                            erreur = "Votre session a expirée, veuillez vous reconnecter.";
                        } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                            erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                        } else if (!erreur.equals("Aucun PH_Reassort trouvé")) {
                            erreur = "Erreur API Réassort";
                        }
                        else{
                            etat = true;
                        }
                    } else {

                        JSONArray reassortJSONArray = response.getJSONArray("PH_Reassorts");

                        for (int i = 0; i < reassortJSONArray.length(); i++) {
                            JSONObject reassortJSONObject = reassortJSONArray.getJSONObject(i);
                            PH_Reassort reassort = new PH_Reassort(reassortJSONObject);

                            long rowID = insererPH_ReassortEnBDD(db, reassort);
                            if(reassortJSONObject.has("ph_reassort_lignes"))
                            {
                                JSONArray reassortLigneJsonArray = reassortJSONObject.getJSONArray("ph_reassort_lignes");
                                for (int j = 0; j < reassortLigneJsonArray.length(); j++) {
                                    JSONObject detailDotationJSONObject = reassortLigneJsonArray.getJSONObject(j);
                                    PH_Reassort_Ligne reassort_ligne = new PH_Reassort_Ligne(detailDotationJSONObject);
                                    long detailRowID = PH_Reassort_LigneOpenHelper.insererPH_Reassort_LigneEnBDD(db, reassort_ligne);
                                }
                            }
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
                        Log.e("Dotation volley", error.toString());
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


    public static PH_Reassort getPH_ReassortByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        PH_Reassort phReassort = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_REASSORT + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phReassort = new PH_Reassort(cursor);
        }
        cursor.close();
        cursor = null;
        return phReassort;
    }

    public static List<PH_Reassort> getPH_Reassort(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_REASSORT, new String[]{});

        List<PH_Reassort> phReassortList = new ArrayList<>();

        while (cursor.moveToNext()) {
            phReassortList.add(new PH_Reassort(cursor));
        }
        cursor.close();
        cursor = null;
        return phReassortList;
    }


    public List<PH_Reassort> getPH_ReassortByDepot(SQLiteDatabase db, String depot_Reference) {
        String critereRecherche = depot_Reference;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_REASSORT + " WHERE " + Constantes.CLE_COL_DEPOT_REFERENCE_PH_REASSORT + " LIKE ?", new String[]{critereRecherche});

        List<PH_Reassort> phReassortList = new ArrayList<>();

        while (cursor.moveToNext()) {
            phReassortList.add(new PH_Reassort(cursor));
        }
        cursor.close();
        cursor = null;
        return phReassortList;
    }


    public void supprimerUnePH_Reassort(SQLiteDatabase db, PH_Reassort ph_reassort) {
        db.delete(Constantes.TABLE_PH_REASSORT, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(ph_reassort.getPhiMR4UUID())});
    }

    public static class Constantes implements BaseColumns {

        public static final String TABLE_PH_REASSORT = "PH_Reassort";
        public static final String CLE_COL_CODE_PH_REASSORT = "Code";
        public static final int NUM_COL_CODE_PH_REASSORT = 1;
        public static final String TYPE_COL_CODE_PH_REASSORT = "INTEGER";
        public static final String CLE_COL_LISTE_PH_REASSORT = "Liste";
        public static final int NUM_COL_LISTE_PH_REASSORT = 2;
        public static final String TYPE_COL_LISTE_PH_REASSORT = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_PH_REASSORT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PH_REASSORT = 3;
        public static final String TYPE_COL_SYS_DT_MAJ_PH_REASSORT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PH_REASSORT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PH_REASSORT = 4;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PH_REASSORT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PH_REASSORT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PH_REASSORT = 5;
        public static final String TYPE_COL_SYS_USER_MAJ_PH_REASSORT = "TEXT";
        public static final String CLE_COL_DEPOT_REFERENCE_PH_REASSORT = "Depot_Reference";
        public static final int NUM_COL_DEPOT_REFERENCE_PH_REASSORT = 6;
        public static final String TYPE_COL_DEPOT_REFERENCE_PH_REASSORT = "TEXT";
        public static final String CLE_COL_FREQUENCE_PH_REASSORT = "Frequence";
        public static final int NUM_COL_FREQUENCE_PH_REASSORT = 7;
        public static final String TYPE_COL_FREQUENCE_PH_REASSORT = "TEXT";
        public static final String CLE_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT = "SynchroDM_Medicament";
        public static final int NUM_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT = 8;
        public static final String TYPE_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT = "INTEGER";
        public static final String CLE_COL_SYNCHRODM_DMDMS_PH_REASSORT = "SynchroDM_DMDMS";
        public static final int NUM_COL_SYNCHRODM_DMDMS_PH_REASSORT = 9;
        public static final String TYPE_COL_SYNCHRODM_DMDMS_PH_REASSORT = "INTEGER";
        public static final String CLE_COL_VALORISATION_TTC_PH_REASSORT = "Valorisation_TTC";
        public static final int NUM_COL_VALORISATION_TTC_PH_REASSORT = 10;
        public static final String TYPE_COL_VALORISATION_TTC_PH_REASSORT = "INTEGER";

        public static final String CREATION_TABLE_PH_REASSORT = " CREATE TABLE       " + Constantes.TABLE_PH_REASSORT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_CODE_PH_REASSORT + " " + Constantes.TYPE_COL_CODE_PH_REASSORT + " , "
                + Constantes.CLE_COL_LISTE_PH_REASSORT + " " + Constantes.TYPE_COL_LISTE_PH_REASSORT + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_PH_REASSORT + " " + Constantes.TYPE_COL_SYS_DT_MAJ_PH_REASSORT + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PH_REASSORT + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PH_REASSORT + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_PH_REASSORT + " " + Constantes.TYPE_COL_SYS_USER_MAJ_PH_REASSORT + " , "
                + Constantes.CLE_COL_DEPOT_REFERENCE_PH_REASSORT + " " + Constantes.TYPE_COL_DEPOT_REFERENCE_PH_REASSORT + " , "
                + Constantes.CLE_COL_FREQUENCE_PH_REASSORT + " " + Constantes.TYPE_COL_FREQUENCE_PH_REASSORT + " , "
                + Constantes.CLE_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT + " " + Constantes.TYPE_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT + " , "
                + Constantes.CLE_COL_SYNCHRODM_DMDMS_PH_REASSORT + " " + Constantes.TYPE_COL_SYNCHRODM_DMDMS_PH_REASSORT + " , "
                + Constantes.CLE_COL_VALORISATION_TTC_PH_REASSORT + " " + Constantes.TYPE_COL_VALORISATION_TTC_PH_REASSORT
                + " ); ";

    }
}
