package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;

public class EmplacementOpenHelper extends DBOpenHelper {

    public EmplacementOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableDepotEmplacements(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_DEPOT_EMPLACEMENT, null, null);
    }

    public static Depot_Emplacement getEmplacementEssaiAlcyons(SQLiteDatabase db, String adressage)
    {
        Depot_Emplacement emplacement = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT + "=?", new String[]{adressage});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            emplacement = new Depot_Emplacement(cursor);
        }
        cursor.close();
        cursor = null;
        return emplacement;
    }

    public static List<Depot_Emplacement> getEmplacementsParZone(SQLiteDatabase db, Depot_Zone depotZone) {
        List<Depot_Emplacement> depotEmplacementList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT + "=? ORDER BY "+Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT, new String[]{String.valueOf(depotZone.getZoneID())});

        while (cursor.moveToNext()) {
            Depot_Emplacement emplacement = new Depot_Emplacement(cursor);
            depotEmplacementList.add(emplacement);
        }
        cursor.close();
        cursor = null;
        return depotEmplacementList;
    }

    public static List<String> getNomEmplacementsParZone(SQLiteDatabase db, Depot_Zone depotZone) {
        List<String> depotEmplacementList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT + "=?", new String[]{String.valueOf(depotZone.getZoneID())});

        while (cursor.moveToNext()) {
            Depot_Emplacement emplacement = new Depot_Emplacement(cursor);
            depotEmplacementList.add(emplacement.getAdressage());
        }
        cursor.close();
        cursor = null;
        return depotEmplacementList;
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        db.delete(Constantes.TABLE_DEPOT_EMPLACEMENT, Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT + "=?", new String[]{"EMPLACEMENT_UF_ALCYONS_ESSAI"});
        return db.delete(Constantes.TABLE_DEPOT_EMPLACEMENT, Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT + "=?", new String[]{"EMPLACEMENT_PUI_ALCYONS_ESSAI"});
    }

    public static long insererUnDepotEmplacementEnBDD(SQLiteDatabase db, Depot_Emplacement depotEmplacement) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_UID_DEPOT_EMPLACEMENT, depotEmplacement.get_UID());
        contentValues.put(Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT, depotEmplacement.getAdressage());
        contentValues.put(Constantes.CLE_COL_HALL_DEPOT_EMPLACEMENT, depotEmplacement.getHall());
        contentValues.put(Constantes.CLE_COL_PALETIER_DEPOT_EMPLACEMENT, depotEmplacement.getPaletier());
        contentValues.put(Constantes.CLE_COL_ALVEOLE_DEPOT_EMPLACEMENT, depotEmplacement.getAlveole());
        contentValues.put(Constantes.CLE_COL_NIVEAU_DEPOT_EMPLACEMENT, depotEmplacement.getNiveau());
        contentValues.put(Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT, depotEmplacement.getZoneID());
        contentValues.put(Constantes.CLE_COL_DEPOT_ID_DEPOT_EMPLACEMENT, depotEmplacement.getDepotID());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT_EMPLACEMENT, depotEmplacement.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_CODE_GLN_DEPOT_EMPLACEMENT, depotEmplacement.getCode_GLN());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_DEPOT_EMPLACEMENT, null, contentValues);

        depotEmplacement.setphiwms_mobileUUID((int) rowId);

        return rowId;
    }

    public static void insererBDDLocaleDepotsEmplacements(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        final String tableNom = "Emplacements";
        final String erreurSynchronisationLibelle = "Emplacements non synchronisées";
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
                                    } else if (!erreur.equals("Aucun Depot_Emplacement trouvé")) {
                                        erreur = "Erreur API Emplacements";
                                    }
                                    // ⬇️ Pas d'insertion → mise à jour directe de la modale sur le thread UI
                                    ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, etat, erreur);

                                } else {
                                    // Parsing sur le thread UI (rapide)
                                    final List<Depot_Emplacement> listeEmplacement = new ArrayList<>();
                                    JSONArray depotEmplacementJSONArray = response.getJSONArray("Depot_Emplacements");
                                    for (int i = 0; i < depotEmplacementJSONArray.length(); i++) {
                                        listeEmplacement.add(new Depot_Emplacement(depotEmplacementJSONArray.getJSONObject(i)));
                                    }
                                    final int finalResultCount = resultCount;

                                    // Insertion sur un thread background
                                    new Thread(() -> {
                                        boolean etatThread = true;
                                        String erreurThread = "";
                                        int compteurReussite = 0;

                                        viderTableDepotEmplacements(db);

                                        // Compiler le statement une seule fois avant la boucle
                                        SQLiteStatement stmt = db.compileStatement(
                                                "INSERT INTO " + EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT + " ("
                                                        + EmplacementOpenHelper.Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT + ","
                                                        + EmplacementOpenHelper.Constantes.CLE_COL_HALL_DEPOT_EMPLACEMENT + ","
                                                        + EmplacementOpenHelper.Constantes.CLE_COL_PALETIER_DEPOT_EMPLACEMENT + ","
                                                        + EmplacementOpenHelper.Constantes.CLE_COL_ALVEOLE_DEPOT_EMPLACEMENT + ","
                                                        + EmplacementOpenHelper.Constantes.CLE_COL_NIVEAU_DEPOT_EMPLACEMENT + ","
                                                        + EmplacementOpenHelper.Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT + ","
                                                        + EmplacementOpenHelper.Constantes.CLE_COL_DEPOT_ID_DEPOT_EMPLACEMENT + ","
                                                        + EmplacementOpenHelper.Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT_EMPLACEMENT + ","
                                                        + EmplacementOpenHelper.Constantes.CLE_COL_UID_DEPOT_EMPLACEMENT + ","
                                                        + EmplacementOpenHelper.Constantes.CLE_COL_CODE_GLN_DEPOT_EMPLACEMENT
                                                        + ") VALUES (?,?,?,?,?,?,?,?,?,?)"
                                        );

                                        db.beginTransaction();
                                        try {
                                            for (Depot_Emplacement depotEmplacement : listeEmplacement) {
                                                stmt.clearBindings();

                                                // TEXT — bindString ou bindNull
                                                if (depotEmplacement.getAdressage() != null)
                                                    stmt.bindString(1, depotEmplacement.getAdressage());
                                                else stmt.bindNull(1);

                                                if (depotEmplacement.getHall() != null)
                                                    stmt.bindString(2, depotEmplacement.getHall());
                                                else stmt.bindNull(2);

                                                if (depotEmplacement.getPaletier() != null)
                                                    stmt.bindString(3, depotEmplacement.getPaletier());
                                                else stmt.bindNull(3);

                                                if (depotEmplacement.getAlveole() != null)
                                                    stmt.bindString(4, depotEmplacement.getAlveole());
                                                else stmt.bindNull(4);

                                                if (depotEmplacement.getNiveau() != null)
                                                    stmt.bindString(5, depotEmplacement.getNiveau());
                                                else stmt.bindNull(5);

                                                // INTEGER — bindLong ou bindNull
                                                // ZoneID
                                                try { stmt.bindLong(6, depotEmplacement.getZoneID()); }
                                                catch (Exception e) { stmt.bindNull(6); }

                                                // DepotID
                                                try { stmt.bindLong(7, depotEmplacement.getDepotID()); }
                                                catch (Exception e) { stmt.bindNull(7); }

                                                if (depotEmplacement.getDepot_Reference() != null)
                                                    stmt.bindString(8, depotEmplacement.getDepot_Reference());
                                                else stmt.bindNull(8);

                                                // _UID
                                                try { stmt.bindLong(9, depotEmplacement.get_UID()); }
                                                catch (Exception e) { stmt.bindNull(9); }

                                                if (depotEmplacement.getCode_GLN() != null)
                                                    stmt.bindString(10, depotEmplacement.getCode_GLN());
                                                else stmt.bindNull(10);

                                                long rowID = stmt.executeInsert();
                                                if (rowID != -1) {
                                                    compteurReussite++;
                                                }
                                            }
                                            db.setTransactionSuccessful();
                                        } catch (Exception e) {
                                            etatThread = false;
                                            erreurThread = "Erreur lors de l'insertion des emplacements";
                                            e.printStackTrace();
                                        } finally {
                                            db.endTransaction();
                                            stmt.close(); // ✅ Toujours fermer le statement
                                        }

                                        if (finalResultCount != compteurReussite) {
                                            erreurThread = String.valueOf(finalResultCount - compteurReussite) + " emplacements n'ont pas été insérés.";
                                            etatThread = false;
                                        }

                                        // ⬇️ Mise à jour de la modale sur le thread UI une fois l'insertion terminée
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

    public static long supprimerUnEmplacementEnBDD(SQLiteDatabase db, Depot_Emplacement emplacement) {
        return db.delete(Constantes.TABLE_DEPOT_EMPLACEMENT, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(emplacement.getPhiMR4UUID())});
    }

    public static Depot_Emplacement getUnEmplacementZoneEtNom(SQLiteDatabase db, Depot_Zone depotZone, String depotEmplacementAdressage) {
        Depot_Emplacement depotEmplacement = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT + "=? and " + Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT + "=?", new String[]{String.valueOf(depotZone.getZoneID()), depotEmplacementAdressage});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            depotEmplacement = new Depot_Emplacement(cursor);
        }

        cursor.close();
        cursor = null;
        return depotEmplacement;
    }

    public static Depot_Emplacement getFirstEmplacement(SQLiteDatabase db, Depot_Zone depotZone) {
        Depot_Emplacement depotEmplacement = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT + "=? ", new String[]{String.valueOf(depotZone.getZoneID())});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            depotEmplacement = new Depot_Emplacement(cursor);
        }

        cursor.close();
        cursor = null;
        return depotEmplacement;
    }

    public static Depot_Emplacement getUnEmplacementByID(SQLiteDatabase db, int id) {
        Depot_Emplacement depotEmplacement = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_UID_DEPOT_EMPLACEMENT + "=?", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            depotEmplacement = new Depot_Emplacement(cursor);
        }
        cursor.close();
        cursor = null;
        return depotEmplacement;
    }

    public static List<Depot_Emplacement> getEmplacementsParZoneID(SQLiteDatabase db, int zoneID) {
        List<Depot_Emplacement> depotEmplacementList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT + "=" + String.valueOf(zoneID), null);

        while (cursor.moveToNext()) {
            depotEmplacementList.add(new Depot_Emplacement(cursor));
        }
        cursor.close();
        cursor = null;
        return depotEmplacementList;
    }

    public static List<Depot_Emplacement> getUnEmplacementParAdressage(SQLiteDatabase db, String depotEmplacementAdressage) {
        List<Depot_Emplacement> depotEmplacementList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT + "=?", new String[]{String.valueOf(depotEmplacementAdressage)});

        while (cursor.moveToNext()) {
            depotEmplacementList.add(new Depot_Emplacement(cursor));
        }
        cursor.close();
        cursor = null;
        return depotEmplacementList;
    }

    public static long mettreAJourEmplacement(SQLiteDatabase db, Depot_Emplacement emplacement) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT, emplacement.getAdressage());
        contentValues.put(Constantes.CLE_COL_HALL_DEPOT_EMPLACEMENT, emplacement.getHall());
        contentValues.put(Constantes.CLE_COL_PALETIER_DEPOT_EMPLACEMENT, emplacement.getPaletier());
        contentValues.put(Constantes.CLE_COL_ALVEOLE_DEPOT_EMPLACEMENT, emplacement.getAlveole());
        contentValues.put(Constantes.CLE_COL_NIVEAU_DEPOT_EMPLACEMENT, emplacement.getNiveau());
        contentValues.put(Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT, emplacement.getZoneID());
        contentValues.put(Constantes.CLE_COL_DEPOT_ID_DEPOT_EMPLACEMENT, emplacement.getDepotID());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT_EMPLACEMENT, emplacement.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_CODE_GLN_DEPOT_EMPLACEMENT, emplacement.getCode_GLN());

        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, emplacement.getPhiMR4UUID());

        // Insertion du dépot en BDD
        return db.update(Constantes.TABLE_DEPOT_EMPLACEMENT, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + String.valueOf(emplacement.getPhiMR4UUID()), null);
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_DEPOT_EMPLACEMENT = "Depot_Emplacement";


        public static final String CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT = "Adressage";
        public static final int NUM_COL_ADRESSAGE_DEPOT_EMPLACEMENT = 1;
        public static final String TYPE_COL_ADRESSAGE_DEPOT_EMPLACEMENT = "TEXT";

        public static final String CLE_COL_HALL_DEPOT_EMPLACEMENT = "Hall";
        public static final int NUM_COL_HALL_DEPOT_EMPLACEMENT = 2;
        public static final String TYPE_COL_HALL_DEPOT_EMPLACEMENT = "TEXT";

        public static final String CLE_COL_PALETIER_DEPOT_EMPLACEMENT = "Paletier";
        public static final int NUM_COL_PALETIER_DEPOT_EMPLACEMENT = 3;
        public static final String TYPE_COL_PALETIER_DEPOT_EMPLACEMENT = "TEXT";

        public static final String CLE_COL_ALVEOLE_DEPOT_EMPLACEMENT = "Alveole";
        public static final int NUM_COL_ALVEOLE_DEPOT_EMPLACEMENT = 4;
        public static final String TYPE_COL_ALVEOLE_DEPOT_EMPLACEMENT = "TEXT";

        public static final String CLE_COL_NIVEAU_DEPOT_EMPLACEMENT = "Niveau";
        public static final int NUM_COL_NIVEAU_DEPOT_EMPLACEMENT = 5;
        public static final String TYPE_COL_NIVEAU_DEPOT_EMPLACEMENT = "TEXT";

        public static final String CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT = "ZoneID";
        public static final int NUM_COL_ZONE_ID_DEPOT_EMPLACEMENT = 6;
        public static final String TYPE_COL_ZONE_ID_DEPOT_EMPLACEMENT = "INTEGER";

        public static final String CLE_COL_DEPOT_ID_DEPOT_EMPLACEMENT = "DepotID";
        public static final int NUM_COL_DEPOT_ID_DEPOT_EMPLACEMENT = 7;
        public static final String TYPE_COL_DEPOT_ID_DEPOT_EMPLACEMENT = "INTEGER";

        public static final String CLE_COL_DEPOT_REFERENCE_DEPOT_EMPLACEMENT = "Depot_Reference";
        public static final int NUM_COL_DEPOT_REFERENCE_DEPOT_EMPLACEMENT = 8;
        public static final String TYPE_COL_DEPOT_REFERENCE_DEPOT_EMPLACEMENT = "TEXT";

        public static final String CLE_COL_UID_DEPOT_EMPLACEMENT = "_UID";
        public static final int NUM_COL_UID_DEPOT_EMPLACEMENT = 9;
        public static final String TYPE_COL_UID_DEPOT_EMPLACEMENT = "INTEGER";

        public static final String CLE_COL_CODE_GLN_DEPOT_EMPLACEMENT = "Code_GLN";
        public static final int NUM_COL_CODE_GLN_DEPOT_EMPLACEMENT = 10;
        public static final String TYPE_COL_CODE_GLN_DEPOT_EMPLACEMENT = "TEXT";

        public static final String CREATION_TABLE_DEPOT_EMPLACEMENT = "CREATE TABLE "
                + Constantes.TABLE_DEPOT_EMPLACEMENT + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT + " " + Constantes.TYPE_COL_ADRESSAGE_DEPOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_HALL_DEPOT_EMPLACEMENT + " " + Constantes.TYPE_COL_HALL_DEPOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_PALETIER_DEPOT_EMPLACEMENT + " " + Constantes.TYPE_COL_PALETIER_DEPOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_ALVEOLE_DEPOT_EMPLACEMENT + " " + Constantes.TYPE_COL_ALVEOLE_DEPOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_NIVEAU_DEPOT_EMPLACEMENT + " " + Constantes.TYPE_COL_NIVEAU_DEPOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT + " " + Constantes.TYPE_COL_ZONE_ID_DEPOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_DEPOT_ID_DEPOT_EMPLACEMENT + " " + Constantes.TYPE_COL_DEPOT_ID_DEPOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT_EMPLACEMENT + " " + Constantes.TYPE_COL_DEPOT_REFERENCE_DEPOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_UID_DEPOT_EMPLACEMENT + " " + Constantes.TYPE_COL_UID_DEPOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_CODE_GLN_DEPOT_EMPLACEMENT + " " + Constantes.TYPE_COL_CODE_GLN_DEPOT_EMPLACEMENT
                + ");";

    }
}
