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
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;

import static fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper.getEmplacementsParZone;
import static fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper.supprimerUnEmplacementEnBDD;

public class ZoneOpenHelper extends DBOpenHelper {

    public ZoneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static List<Depot_Zone> getZonesEtEmplacementsParDepot(SQLiteDatabase db, Depot depot) {
        List<Depot_Zone> depotZoneList = new ArrayList<>();

        // Récupérations de la liste des zones
        Cursor cursorZones = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_ZONE + " WHERE " + Constantes.CLE_COL_DEPOT_ID_DEPOT_ZONE + "=? AND ZoneName != \"ZONE\"", new String[]{String.valueOf(depot.getDepot_UID())});

        while (cursorZones.moveToNext()) {
            Depot_Zone depotZone = new Depot_Zone(cursorZones);

            // Récupérer la liste des emplacements correspondants à la zoneCourante
            depotZone.setEmplacements(getEmplacementsParZone(db, depotZone));

            // Ajout de la zone dans la liste à retourner
            depotZoneList.add(depotZone);
        }
        cursorZones.close();
        cursorZones = null;
        return depotZoneList;
    }

    public static List<Depot_Zone> getZonesParDepot(SQLiteDatabase db, Depot depot) {
        List<Depot_Zone> depotZoneList = new ArrayList<>();

        // Récupérations de la liste des zones
        Cursor cursorZones = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_ZONE + " WHERE " + Constantes.CLE_COL_DEPOT_ID_DEPOT_ZONE + "=? AND ZoneName != \"ZONE\"", new String[]{String.valueOf(depot.getDepot_UID())});

        while (cursorZones.moveToNext()) {
            Depot_Zone depotZone = new Depot_Zone(cursorZones);
            // Ajout de la zone dans la liste à retourner
            depotZoneList.add(depotZone);
        }
        cursorZones.close();
        cursorZones = null;
        return depotZoneList;
    }

    public static int creerUnNouvelIdDeZone(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_ZONE, null);

        int id = -1;

        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            id = cursor.getInt(Constantes.NUM_COL_ID_DEPOT_ZONE) + 1;
        }

        cursor.close();
        cursor = null;
        return id;
    }

    public static Depot_Zone getZoneByDepotEtNom(SQLiteDatabase db, Depot depot, String zoneName) {
        Depot_Zone depotZone = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_ZONE + " WHERE " + Constantes.CLE_COL_DEPOT_ID_DEPOT_ZONE + "=? and " + Constantes.CLE_COL_NOM_DEPOT_ZONE + "=?", new String[]{String.valueOf(depot.getDepot_UID()), zoneName});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            depotZone = new Depot_Zone(cursor);
        }

        cursor.close();
        cursor = null;
        return depotZone;
    }

    public static Depot_Zone getUneZoneByID(SQLiteDatabase db, int id) {
        Depot_Zone depotZone = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_ZONE + " WHERE " + Constantes.CLE_COL_ID_DEPOT_ZONE + "=?", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            depotZone = new Depot_Zone(cursor);
        }
        cursor.close();
        cursor = null;
        return depotZone;
    }

    public static Depot_Zone getFirstZone(SQLiteDatabase db, Depot depot) {
        Depot_Zone depotZone = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_ZONE + " WHERE " + Constantes.CLE_COL_DEPOT_ID_DEPOT_ZONE + "=?", new String[]{String.valueOf(depot.getDepot_UID())});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            depotZone = new Depot_Zone(cursor);
        }
        cursor.close();
        cursor = null;
        return depotZone;
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        db.delete(Constantes.TABLE_DEPOT_ZONE, Constantes.CLE_COL_NOM_DEPOT_ZONE + "=?", new String[]{"ZONE_UF_ALCYONS_ESSAI"});
        return db.delete(Constantes.TABLE_DEPOT_ZONE, Constantes.CLE_COL_NOM_DEPOT_ZONE + "=?", new String[]{"ZONE_PUI_ALCYONS_ESSAI"});
    }

    public static long supprimerUneZoneEnBDD(SQLiteDatabase db, Depot_Zone zoneASupprimer) {
        // Récupération de la liste des emplacements à supprimer
        List<Depot_Emplacement> depotEmplacementList = getEmplacementsParZone(db, zoneASupprimer);
        int nbEmplacementASupprimer = depotEmplacementList.size();
        int nbEmplacementsSupprimes = 0;
        for (Depot_Emplacement depotEmplacement : depotEmplacementList
                ) {
            long rowId = supprimerUnEmplacementEnBDD(db, depotEmplacement);
            if (rowId != -1) {
                nbEmplacementsSupprimes += 1;
            }
        }
        int valeurARetourner = -1;
        if (nbEmplacementASupprimer == nbEmplacementsSupprimes) {
            valeurARetourner = db.delete(Constantes.TABLE_DEPOT_ZONE, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(zoneASupprimer.getZoneID())});
        }
        return valeurARetourner;
    }

    public static int getNbDepotsZones(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT_ZONE, null);
        int nbDepotsZones = cursor.getCount();
        cursor.close();
        cursor = null;
        return nbDepotsZones;
    }

    public static void viderTableDepotZones(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_DEPOT_ZONE, null, null);
    }

    public static long insererUnDepotZoneEnBDD(SQLiteDatabase db, Depot_Zone depotZone) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_DEPOT_ZONE, depotZone.getZoneID());
        contentValues.put(Constantes.CLE_COL_NOM_DEPOT_ZONE, depotZone.getZoneName());
        contentValues.put(Constantes.CLE_COL_LONGITUDE_DEPOT_ZONE, depotZone.getZoneLongitude());
        contentValues.put(Constantes.CLE_COL_LATITUDE_DEPOT_ZONE, depotZone.getZoneLatitude());
        contentValues.put(Constantes.CLE_COL_DATA_MATRIX_REFERENCE_DEPOT_ZONE, depotZone.getDataMatrixReference());
        contentValues.put(Constantes.CLE_COL_DEPOT_ID_DEPOT_ZONE, depotZone.getDepotID());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_DEPOT_ZONE, depotZone.getConservation());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT_ZONE, depotZone.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_TYPE_EMPLACEMENT_DEPOT_ZONE, depotZone.getType_Emplacement());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_DEPOT_ZONE, null, contentValues);

        depotZone.setPhiMR4UUID((int) rowId);

        return rowId;
    }

    public static void insererBDDLocaleDepotsZones(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        final String tableNom = "Zones";
        final String erreurSynchronisationLibelle = "Zones non synchronisées";

        if (!statutConnexion) {
            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
        }
        else{
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteDepotsZones;
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
                                        //viderBasesDeDonnees(db);
                                        erreur = "Votre session a expirée, veuillez vous reconnecter.";
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    } else if (!erreur.contentEquals("Aucun Depot_Zone trouvé")) {
                                        erreur = "Erreur API Zones";
                                    }
                                } else {
                                    int nbZone = getNbDepotsZones(db);
                                    if(nbZone != resultCount)
                                    {
                                        viderTableDepotZones(db);
                                        JSONArray depotZoneJSONArray = response.getJSONArray("Depot_Zones");
                                        int compteurReussite = 0;

                                        for (int i = 0; i < depotZoneJSONArray.length(); i++) {
                                            // Récupération du service courant
                                            JSONObject depotZoneJSONObject = depotZoneJSONArray.getJSONObject(i);

                                            Depot_Zone depotZone = new Depot_Zone(depotZoneJSONObject);

                                            // insertion du service en bdd
                                            long rowID = insererUnDepotZoneEnBDD(db, depotZone);

                                            if (rowID != -1) {
                                                compteurReussite++;
                                            }
                                        }
                                        if (resultCount != compteurReussite) {
                                            erreur = String.valueOf(resultCount - compteurReussite) + " zones n'ont pas été insérées.";
                                            etat = false;
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
                            Log.e("Zone volley", error.toString());
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
            requestQueue.add(obreq);
        }
    }

    public static long mettreAJourZone(SQLiteDatabase db, Depot_Zone zone) {
        // Récupération des valeurs à mettre à jour
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_NOM_DEPOT_ZONE, zone.getZoneName());
        contentValues.put(Constantes.CLE_COL_LONGITUDE_DEPOT_ZONE, zone.getZoneLongitude());
        contentValues.put(Constantes.CLE_COL_LATITUDE_DEPOT_ZONE, zone.getZoneLatitude());
        contentValues.put(Constantes.CLE_COL_DATA_MATRIX_REFERENCE_DEPOT_ZONE, zone.getDataMatrixReference());
        contentValues.put(Constantes.CLE_COL_DEPOT_ID_DEPOT_ZONE, zone.getDepotID());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_DEPOT_ZONE, zone.getConservation());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT_ZONE, zone.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_TYPE_EMPLACEMENT_DEPOT_ZONE, zone.getType_Emplacement());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, zone.getPhiMR4UUID());

        return db.update(Constantes.TABLE_DEPOT_ZONE, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + String.valueOf(zone.getPhiMR4UUID()), null);
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_DEPOT_ZONE = "Depot_Zone";


        public static final String CLE_COL_NOM_DEPOT_ZONE = "ZoneName";
        public static final int NUM_COL_NOM_DEPOT_ZONE = 1;
        public static final String TYPE_COL_NOM_DEPOT_ZONE = "TEXT";

        public static final String CLE_COL_LONGITUDE_DEPOT_ZONE = "ZoneLongitude";
        public static final int NUM_COL_LONGITUDE_DEPOT_ZONE = 2;
        public static final String TYPE_COL_LONGITUDE_DEPOT_ZONE = "REAL";

        public static final String CLE_COL_LATITUDE_DEPOT_ZONE = "ZoneLatitude";
        public static final int NUM_COL_LATITUDE_DEPOT_ZONE = 3;
        public static final String TYPE_COL_LATITUDE_DEPOT_ZONE = "REAL";

        public static final String CLE_COL_DATA_MATRIX_REFERENCE_DEPOT_ZONE = "DataMatrixReference";
        public static final int NUM_COL_DATA_MATRIX_REFERENCE_DEPOT_ZONE = 4;
        public static final String TYPE_COL_DATA_MATRIX_REFERENCE_DEPOT_ZONE = "TEXT";

        public static final String CLE_COL_DEPOT_ID_DEPOT_ZONE = "DepotID";
        public static final int NUM_COL_DEPOT_ID_DEPOT_ZONE = 5;
        public static final String TYPE_COL_DEPOT_ID_DEPOT_ZONE = "INTEGER";

        public static final String CLE_COL_CONSERVATION_DEPOT_ZONE = "Conservation";
        public static final int NUM_COL_CONSERVATION_DEPOT_ZONE = 6;
        public static final String TYPE_COL_CONSERVATION_DEPOT_ZONE = "TEXT";

        public static final String CLE_COL_DEPOT_REFERENCE_DEPOT_ZONE = "Depot_Reference";
        public static final int NUM_COL_DEPOT_REFERENCE_DEPOT_ZONE = 7;
        public static final String TYPE_COL_DEPOT_REFERENCE_DEPOT_ZONE = "TEXT";

        public static final String CLE_COL_TYPE_EMPLACEMENT_DEPOT_ZONE = "Type_Emplacement";
        public static final int NUM_COL_TYPE_EMPLACEMENT_DEPOT_ZONE = 8;
        public static final String TYPE_COL_TYPE_EMPLACEMENT_DEPOT_ZONE = "TEXT";

        public static final String CLE_COL_ID_DEPOT_ZONE = "ZoneID";
        public static final int NUM_COL_ID_DEPOT_ZONE = 9;
        public static final String TYPE_COL_ID_DEPOT_ZONE = "INTEGER";


        public static final String CREATION_TABLE_DEPOT_ZONE = "CREATE TABLE "
                + Constantes.TABLE_DEPOT_ZONE + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_NOM_DEPOT_ZONE + " " + Constantes.TYPE_COL_NOM_DEPOT_ZONE + ","
                + Constantes.CLE_COL_LONGITUDE_DEPOT_ZONE + " " + Constantes.TYPE_COL_LONGITUDE_DEPOT_ZONE + ","
                + Constantes.CLE_COL_LATITUDE_DEPOT_ZONE + " " + Constantes.TYPE_COL_LATITUDE_DEPOT_ZONE + ","
                + Constantes.CLE_COL_DATA_MATRIX_REFERENCE_DEPOT_ZONE + " " + Constantes.TYPE_COL_DATA_MATRIX_REFERENCE_DEPOT_ZONE + ","
                + Constantes.CLE_COL_DEPOT_ID_DEPOT_ZONE + " " + Constantes.TYPE_COL_DEPOT_ID_DEPOT_ZONE + ","
                + Constantes.CLE_COL_CONSERVATION_DEPOT_ZONE + " " + Constantes.TYPE_COL_CONSERVATION_DEPOT_ZONE + ","
                + Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT_ZONE + " " + Constantes.TYPE_COL_DEPOT_REFERENCE_DEPOT_ZONE + ","
                + Constantes.CLE_COL_TYPE_EMPLACEMENT_DEPOT_ZONE + " " + Constantes.TYPE_COL_TYPE_EMPLACEMENT_DEPOT_ZONE + ","
                + Constantes.CLE_COL_ID_DEPOT_ZONE + " " + Constantes.TYPE_COL_ID_DEPOT_ZONE

                + ");";
    }
}
