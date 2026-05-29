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
import fr.alcyons.phiwms_mobile.Classes.PH_Demande_Motif;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;

public class PH_Demande_MotifOpenHelper extends DBOpenHelper {
    public PH_Demande_MotifOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public static List<String> getDemandeMotif(SQLiteDatabase db) {
        List<String> motifList = new ArrayList<>();
        motifList.add("Sélectionnez un motif");
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEMANDE_MOTIF+" ORDER BY "+ Constantes.CLE_COL_MOTIF_DEMANDE_MOTIF+"  COLLATE NOCASE", new String[]{});

        while (cursor.moveToNext()) {
            PH_Demande_Motif motif = new PH_Demande_Motif(cursor);
            motifList.add(motif.getMotif());
        }
        cursor.close();
        cursor = null;
        return motifList;
    }
    public static void viderTablePHDemande(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_DEMANDE_MOTIF, null, null);
    }
    public static long insererUnMotifEnBDD(SQLiteDatabase db, PH_Demande_Motif phDemandeMotif) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_DEMANDE_MOTIF, phDemandeMotif.getId());
        contentValues.put(Constantes.CLE_COL_MOTIF_DEMANDE_MOTIF, phDemandeMotif.getMotif());
        contentValues.put(Constantes.CLE_COL_ORDRE_DEMANDE_MOTIF, phDemandeMotif.getOrdre());
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_UID_DEMANDE_MOTIF, phDemandeMotif.getEtablissementUID());
        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_DEMANDE_MOTIF, null, contentValues);
        return rowId;
    }

    public static void insererBDDLocaleDemandeMotif(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        final String tableNom = "PH_Demande_Motif";
        final String erreurSynchronisationLibelle = "Demande Motif non synchronisés";

        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteDemandeMotif;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

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
                                    erreur = "Votre session a expirée, veuillez vous reconnecter.";
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                } else if (!erreur.equals("Aucun PH_Depots trouvé")) {
                                    erreur = "Erreur API Dépots";
                                } else {
                                    etat = true;
                                }
                                // ⬇️ Pas d'insertion → mise à jour directe de la modale sur le thread UI
                                String activityName = context.getClass().getSimpleName();
                                if (activityName.contentEquals("AuthentificationActivity")) {
                                    ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, etat, erreur);
                                } else if (activityName.contentEquals("ServiceConnexionDirecteActivity")) {
                                    ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                                }

                            } else {
                                // Parsing sur le thread UI (rapide)
                                JSONArray motifJSONArrayAll = response.getJSONArray("PH_Demande_Motif");
                                final List<PH_Demande_Motif> listeMotifs = new ArrayList<>();
                                for (int j = 0; j < motifJSONArrayAll.length(); j++) {
                                    listeMotifs.add(new PH_Demande_Motif(motifJSONArrayAll.getJSONObject(j)));
                                }

                                final int finalResultCount = resultCount;

                                // Insertion sur un thread background
                                new Thread(() -> {
                                    boolean etatThread = true;
                                    String erreurThread = "";
                                    int compteurReussite = 0;

                                    viderTablePHDemande(db);

                                    db.beginTransaction();
                                    try {
                                        for (PH_Demande_Motif motif : listeMotifs) {
                                            long rowID = insererUnMotifEnBDD(db, motif);
                                            if (rowID != -1) {
                                                compteurReussite++;
                                            }
                                        }
                                        db.setTransactionSuccessful();
                                    } catch (Exception e) {
                                        etatThread = false;
                                        erreurThread = "Erreur lors de l'insertion des motifs";
                                        e.printStackTrace();
                                    } finally {
                                        db.endTransaction();
                                    }

                                    if (finalResultCount != compteurReussite) {
                                        erreurThread = String.valueOf(finalResultCount - compteurReussite) + " motif n'ont pas été insérés.";
                                        etatThread = false;
                                    }

                                    // ⬇️ Mise à jour de la modale sur le thread UI une fois tout terminé
                                    final boolean resultatFinal = etatThread;
                                    final String erreurFinale = erreurThread;
                                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                                        String activityName = context.getClass().getSimpleName();
                                        if (activityName.contentEquals("AuthentificationActivity")) {
                                            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, resultatFinal, erreurFinale);
                                        } else if (activityName.contentEquals("ServiceConnexionDirecteActivity")) {
                                            ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                                        }
                                    });

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
                        Log.e("Dépôt volley", error.toString());
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
    public static class Constantes implements BaseColumns {
        public static final String TABLE_DEMANDE_MOTIF = "PH_Demande_Motif";

        public static final String CLE_COL_ID_DEMANDE_MOTIF= "id";
        public static final int NUM_COL_ID_DEMANDE_MOTIF = 1;
        public static final String TYPE_COL_ID_DEMANDE_MOTIF = "INTEGER";
        public static final String CLE_COL_MOTIF_DEMANDE_MOTIF = "Motif";
        public static final int NUM_COL_MOTIF_DEMANDE_MOTIF = 2;
        public static final String TYPE_COL_MOTIF_DEMANDE_MOTIF = "TEXT";
        public static final String CLE_COL_ORDRE_DEMANDE_MOTIF = "Ordre";
        public static final int NUM_COL_ORDRE_DEMANDE_MOTIF = 3;
        public static final String TYPE_COL_ORDRE_DEMANDE_MOTIF = "INTEGER";
        public static final String CLE_COL_ETABLISSEMENT_UID_DEMANDE_MOTIF = "Etablissement_UID";
        public static final int NUM_COL_ETABLISSEMENT_UID_DEMANDE_MOTIF = 4;
        public static final String TYPE_COL_ETABLISSEMENT_UID_DEMANDE_MOTIF = "INTEGER";

        public static final String CREATION_TABLE_DEMANDE_MOTIF= "CREATE TABLE "
                + Constantes.TABLE_DEMANDE_MOTIF + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_ID_DEMANDE_MOTIF + " " + Constantes.TYPE_COL_ID_DEMANDE_MOTIF + ","
                + Constantes.CLE_COL_MOTIF_DEMANDE_MOTIF + " " + Constantes.TYPE_COL_MOTIF_DEMANDE_MOTIF + ","
                + Constantes.CLE_COL_ORDRE_DEMANDE_MOTIF + " " + Constantes.TYPE_COL_ORDRE_DEMANDE_MOTIF + ","
                + Constantes.CLE_COL_ETABLISSEMENT_UID_DEMANDE_MOTIF + " " + Constantes.TYPE_COL_ETABLISSEMENT_UID_DEMANDE_MOTIF
                + ");";

    }
}
