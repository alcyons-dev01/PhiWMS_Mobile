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

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.Classes.PH_RetourMotif;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by jessica on 29/11/2017.
 */

public class PH_RetourMotifOpenHelper extends DBOpenHelper {

    public PH_RetourMotifOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTablePH_RetourMotif(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PH_RETOURMOTIF, null, null);
    }

    public static long insererPH_RetourMotifEnBDD(SQLiteDatabase db, PH_RetourMotif objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_PH_RETOURMOTIF, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_MOTIFRETOUR_PH_RETOURMOTIF, objet.getMotifRetour());
        long rowID = db.insert(Constantes.TABLE_PH_RETOURMOTIF, null, contentValues);
        objet.setphiwms_mobileUUID((int) rowID);
        return rowID;
    }

    public static List<PH_RetourMotif> getAllPH_RetourMotif(SQLiteDatabase db) {
        List<PH_RetourMotif> phRetourMotifList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_RETOURMOTIF, null);

        while (cursor.moveToNext()) {
            phRetourMotifList.add(new PH_RetourMotif(cursor));
        }

        cursor.close();
        cursor = null;
        return phRetourMotifList;
    }

    public void insererBDDLocalePH_RetourMotif(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        final String tableNom = "Motifs de retour";
        final String erreurSynchronisationLibelle = "Motifs de retour non synchronisés";

        if (!OutilsGestionConnexionReseau.isServerAccessible(context)) {
            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
        }
        else{
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteRetourMotif;
            RequestQueue requestQueue = new Volley().newRequestQueue(context);

            viderTablePH_RetourMotif(db);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null, new Response.Listener<JSONObject>() {

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
                            } else if (!erreur.contentEquals("Aucun PH_RetourMotif trouvé")) {
                                erreur = "Erreur API Motifs de retour";
                            }
                        } else {

                            JSONArray phRetourMotifJSONArray = response.getJSONArray("PH_RetourMotif");

                            for (int i = 0; i < phRetourMotifJSONArray.length(); i++) {
                                JSONObject phRetourMotifJSONObject = phRetourMotifJSONArray.getJSONObject(i);

                                PH_RetourMotif phRetourMotif = new PH_RetourMotif(phRetourMotifJSONObject);

                                // insertion du service en bdd
                                long rowID = insererPH_RetourMotifEnBDD(db, phRetourMotif);
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
                            Log.e("Retour motif volley", error.toString());
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
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PH_RETOURMOTIF = "PH_RetourMotif";
        public static final String CLE_COL__UID_PH_RETOURMOTIF = "_UID";
        public static final int NUM_COL__UID_PH_RETOURMOTIF = 1;
        public static final String TYPE_COL__UID_PH_RETOURMOTIF = "INTEGER";
        public static final String CLE_COL_MOTIFRETOUR_PH_RETOURMOTIF = "motifRetour";
        public static final int NUM_COL_MOTIFRETOUR_PH_RETOURMOTIF = 2;
        public static final String TYPE_COL_MOTIFRETOUR_PH_RETOURMOTIF = "TEXT";

        public static final String CREATION_TABLE_PH_RETOURMOTIF = " CREATE TABLE       " + Constantes.TABLE_PH_RETOURMOTIF
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL__UID_PH_RETOURMOTIF + " " + Constantes.TYPE_COL__UID_PH_RETOURMOTIF + " , "
                + Constantes.CLE_COL_MOTIFRETOUR_PH_RETOURMOTIF + " " + Constantes.TYPE_COL_MOTIFRETOUR_PH_RETOURMOTIF
                + " ); ";

    }

}
