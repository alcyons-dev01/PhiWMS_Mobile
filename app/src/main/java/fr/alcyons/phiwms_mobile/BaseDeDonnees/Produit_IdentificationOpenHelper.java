package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
import fr.alcyons.phiwms_mobile.Classes.Produit_Identification;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class Produit_IdentificationOpenHelper extends DBOpenHelper {

    public Produit_IdentificationOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableIdentificationReference(SQLiteDatabase db) {
        db.delete(Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE, null, null);
    }

    public static long insererUneIdentificationEnBDD(SQLiteDatabase db, Produit_Identification produitIdentification) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Produit_IdentificationOpenHelper.Constantes.CLE_COL_CODE_PRODUIT, produitIdentification.getCodeProduit());
        contentValues.put(Produit_IdentificationOpenHelper.Constantes.CLE_COL_IDENTIFICATION, produitIdentification.getIdentification());
        contentValues.put(Produit_IdentificationOpenHelper.Constantes.CLE_COL_TYPE_CODE, produitIdentification.getTypeCode());
        contentValues.put(Produit_IdentificationOpenHelper.Constantes.CLE_COL_NATURE_IDENTIFICATION, produitIdentification.getNatureIdentification());
        contentValues.put(Produit_IdentificationOpenHelper.Constantes.CLE_COL_ETABLISSEMENT_UID, produitIdentification.getEtablissementUID());

        // Insertion du dépot en BDD
        long rowId = db.insert(Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE, null, contentValues);

        produitIdentification.setPhiwms_mobileUUID((int) rowId);

        return rowId;
    }

    public static void insererBDDLocaleIdentificationReference(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        final String tableNom = "Identification_Reference";
        final String erreurSynchronisationLibelle = "Identifications non synchronisées";
        if (!statutConnexion) {
            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
        }
        else{
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteDepotsEmplacements;
            RequestQueue requestQueue = new Volley().newRequestQueue(context);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean etat = true;
                                String erreur = "";
                                int resultCount = response.getInt("resultCount");

                                if (resultCount == 0) {
                                    erreur = response.getString("erreur");
                                    etat = false;
                                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                        erreur = "Votre session a expirée, veuillez vous reconnecter.";
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    } else if (!erreur.equals("Aucune Identification trouvée")) {
                                        erreur = "Erreur API Identification";
                                    }
                                    // ⬇️ Pas d'insertion → mise à jour directe de la modale sur le thread UI
                                    ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, etat, erreur);

                                } else {
                                    // Parsing sur le thread UI (rapide)
                                    final List<Produit_Identification> produitIdentificationList = new ArrayList<>();
                                    JSONArray produitIdentificationJSONArray = response.getJSONArray("Produit_Identification");
                                    for (int i = 0; i < produitIdentificationJSONArray.length(); i++) {
                                        produitIdentificationList.add(new Produit_Identification(produitIdentificationJSONArray.getJSONObject(i)));
                                    }
                                    final int finalResultCount = resultCount;

                                    // Insertion sur un thread background
                                    new Thread(() -> {
                                        boolean etatThread = true;
                                        String erreurThread = "";
                                        int compteurReussite = 0;

                                        viderTableIdentificationReference(db);

                                        // Compiler le statement une seule fois avant la boucle
                                        SQLiteStatement stmt = db.compileStatement(
                                                "INSERT INTO " + Constantes.TABLE_IDENTIFICATION_REFERENCE + " ("
                                                        + Constantes.CLE_COL_CODE_PRODUIT + ","
                                                        + Constantes.CLE_COL_IDENTIFICATION + ","
                                                        + Constantes.CLE_COL_TYPE_CODE + ","
                                                        + Constantes.CLE_COL_NATURE_IDENTIFICATION + ","
                                                        + Constantes.CLE_COL_ETABLISSEMENT_UID
                                                        + ") VALUES (?,?,?,?,?)"
                                        );

                                        db.beginTransaction();
                                        try {
                                            for (Produit_Identification produitIdentification : produitIdentificationList) {
                                                stmt.clearBindings();

                                                try { stmt.bindLong(1, produitIdentification.getCodeProduit()); }
                                                catch (Exception e) { stmt.bindNull(1); }

                                                // TEXT — bindString ou bindNull
                                                if (produitIdentification.getIdentification() != null)
                                                    stmt.bindString(2, produitIdentification.getIdentification());
                                                else stmt.bindNull(2);

                                                if (produitIdentification.getTypeCode() != null)
                                                    stmt.bindString(3, produitIdentification.getTypeCode());
                                                else stmt.bindNull(3);

                                                if (produitIdentification.getNatureIdentification() != null)
                                                    stmt.bindString(4, produitIdentification.getNatureIdentification());
                                                else stmt.bindNull(4);

                                                try { stmt.bindLong(5, produitIdentification.getEtablissementUID()); }
                                                catch (Exception e) { stmt.bindNull(5); }

                                                long rowID = stmt.executeInsert();
                                                if (rowID != -1) {
                                                    compteurReussite++;
                                                }
                                            }
                                            db.setTransactionSuccessful();
                                        } catch (Exception e) {
                                            etatThread = false;
                                            erreurThread = "Erreur lors de l'insertion des identifications";
                                            e.printStackTrace();
                                        } finally {
                                            db.endTransaction();
                                            stmt.close();
                                        }

                                        if (finalResultCount != compteurReussite) {
                                            erreurThread = String.valueOf(finalResultCount - compteurReussite) + " identifications n'ont pas été insérées.";
                                            etatThread = false;
                                        }

                                        final boolean resultatFinal = etatThread;
                                        final String erreurFinale = erreurThread;
                                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                                                ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, resultatFinal, erreurFinale)
                                        );

                                    }).start();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Emplacement volley", error.toString());
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

    public static long supprimerUneIdentificationEnBDD(SQLiteDatabase db, Produit_Identification produitIdentification) {
        return db.delete(Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(produitIdentification.getPhiwms_mobileUUID())});
    }

    public static long mettreAJourIdentificationReference(SQLiteDatabase db, Produit_Identification produitIdentification) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Produit_IdentificationOpenHelper.Constantes.CLE_COL_CODE_PRODUIT, produitIdentification.getCodeProduit());
        contentValues.put(Produit_IdentificationOpenHelper.Constantes.CLE_COL_IDENTIFICATION, produitIdentification.getIdentification());
        contentValues.put(Produit_IdentificationOpenHelper.Constantes.CLE_COL_TYPE_CODE, produitIdentification.getTypeCode());
        contentValues.put(Produit_IdentificationOpenHelper.Constantes.CLE_COL_NATURE_IDENTIFICATION, produitIdentification.getNatureIdentification());
        contentValues.put(Produit_IdentificationOpenHelper.Constantes.CLE_COL_ETABLISSEMENT_UID, produitIdentification.getEtablissementUID());

        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, produitIdentification.getPhiwms_mobileUUID());

        // Insertion du dépot en BDD
        return db.update(Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + String.valueOf(produitIdentification.getPhiwms_mobileUUID()), null);
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_IDENTIFICATION_REFERENCE = "Identification_Reference";

        public static final String CLE_COL_CODE_PRODUIT = "codeProduit";
        public static final int NUM_COL_CODE_PRODUIT = 1;
        public static final String TYPE_COL_CODE_PRODUIT = "INTEGER";

        public static final String CLE_COL_IDENTIFICATION = "identification";
        public static final int NUM_COL_IDENTIFICATION = 2;
        public static final String TYPE_COL_IDENTIFICATION = "TEXT";

        public static final String CLE_COL_TYPE_CODE = "typeCode";
        public static final int NUM_COL_TYPE_CODE = 3;
        public static final String TYPE_COL_TYPE_CODE = "TEXT";

        public static final String CLE_COL_NATURE_IDENTIFICATION = "natureIdentification";
        public static final int NUM_COL_NATURE_IDENTIFICATION  = 4;
        public static final String TYPE_COL_NATURE_IDENTIFICATION  = "TEXT";

        public static final String CLE_COL_ETABLISSEMENT_UID = "Etablissement_UID";
        public static final int NUM_COL_ETABLISSEMENT_UID = 5;
        public static final String TYPE_COL_ETABLISSEMENT_UID = "INTEGER";

        public static final String CREATION_TABLE_PRODUIT_IDENTIFICATION = "CREATE TABLE "
                + Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Produit_IdentificationOpenHelper.Constantes.CLE_COL_CODE_PRODUIT + " " + Produit_IdentificationOpenHelper.Constantes.TYPE_COL_CODE_PRODUIT + ","
                + Produit_IdentificationOpenHelper.Constantes.CLE_COL_IDENTIFICATION + " " + Produit_IdentificationOpenHelper.Constantes.TYPE_COL_IDENTIFICATION + ","
                + Produit_IdentificationOpenHelper.Constantes.CLE_COL_TYPE_CODE + " " + Produit_IdentificationOpenHelper.Constantes.TYPE_COL_TYPE_CODE + ","
                + Produit_IdentificationOpenHelper.Constantes.CLE_COL_NATURE_IDENTIFICATION + " " + Produit_IdentificationOpenHelper.Constantes.TYPE_COL_NATURE_IDENTIFICATION + ","
                + Produit_IdentificationOpenHelper.Constantes.CLE_COL_ETABLISSEMENT_UID + " " + Produit_IdentificationOpenHelper.Constantes.TYPE_COL_ETABLISSEMENT_UID
                + ");";
    }
}
