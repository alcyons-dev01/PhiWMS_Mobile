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
import java.util.HashMap;
import java.util.Map;
import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.Produit_Place;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class ProduitPlaceOpenHelper extends DBOpenHelper {

    public ProduitPlaceOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableProduitPlace(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PRODUIT_PLACE, null, null);
    }

    public static long insererProduitPlaceEnBDD(SQLiteDatabase db, Produit_Place objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_PRODUIT_PRODUIT_PLACE, objet.getProduitID());
        contentValues.put(Constantes.CLE_COL_ID_DEPOT_PRODUIT_PLACE, objet.getDepotID());
        contentValues.put(Constantes.CLE_COL_ID_ZONE_PRODUIT_PLACE, objet.getZoneID());
        contentValues.put(Constantes.CLE_COL_ID_PLACE_PRODUIT_PLACE, objet.getPlaceID());
        contentValues.put(Constantes.CLE_COL_NOM_DEPOT_PRODUIT_PLACE, objet.getDepotReference());
        contentValues.put(Constantes.CLE_COL_NOM_ZONE_PRODUIT_PLACE, objet.getZoneNom());
        contentValues.put(Constantes.CLE_COL_NOM_PLACE_PRODUIT_PLACE, objet.getPlaceNom());
        contentValues.put(Constantes.CLE_COL_NOM_PRODUIT_PRODUIT_PLACE, objet.getProduitNom());

        long rowID = db.insert(Constantes.TABLE_PRODUIT_PLACE, null, contentValues);
        objet.setphiwms_mobileUUID((int) rowID);
        return rowID;
    }

    public static long mettreAJourProduitPlace(SQLiteDatabase db, Produit_Place produitPlace) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_PRODUIT_PRODUIT_PLACE, produitPlace.getProduitID());
        contentValues.put(Constantes.CLE_COL_ID_DEPOT_PRODUIT_PLACE, produitPlace.getDepotID());
        contentValues.put(Constantes.CLE_COL_ID_ZONE_PRODUIT_PLACE, produitPlace.getZoneID());
        contentValues.put(Constantes.CLE_COL_ID_PLACE_PRODUIT_PLACE, produitPlace.getPlaceID());
        contentValues.put(Constantes.CLE_COL_NOM_DEPOT_PRODUIT_PLACE, produitPlace.getDepotReference());
        contentValues.put(Constantes.CLE_COL_NOM_ZONE_PRODUIT_PLACE, produitPlace.getZoneNom());
        contentValues.put(Constantes.CLE_COL_NOM_PLACE_PRODUIT_PLACE, produitPlace.getPlaceNom());
        contentValues.put(Constantes.CLE_COL_NOM_PRODUIT_PRODUIT_PLACE, produitPlace.getProduitNom());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, produitPlace.getPhiwms_mobileUUID());

        long rowId = db.update(Constantes.TABLE_PRODUIT_PLACE, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + produitPlace.getPhiwms_mobileUUID(), null);

        return rowId;
    }

    public static Produit_Place getProduitPlaceByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        Produit_Place produitPlace = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PRODUIT_PLACE + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " =? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            produitPlace = new Produit_Place(cursor);
        }
        cursor.close();
        cursor = null;
        return produitPlace;
    }

    public static Produit_Place getProduitPlaceParProduitDepotZonePlace(SQLiteDatabase db, Integer id_produit, Integer id_depot, Integer id_zone, Integer id_place) {
        Produit_Place produitPlace = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT_PLACE + " WHERE " + Constantes.CLE_COL_ID_PRODUIT_PRODUIT_PLACE + "=? AND "+ Constantes.CLE_COL_ID_DEPOT_PRODUIT_PLACE+"=? AND "+ Constantes.CLE_COL_ID_ZONE_PRODUIT_PLACE+"=? AND "+ Constantes.CLE_COL_ID_PLACE_PRODUIT_PLACE+"=?", new String[]{String.valueOf(id_produit), String.valueOf(id_depot), String.valueOf(id_zone), String.valueOf(id_place)});

        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            produitPlace = new Produit_Place(cursor);
        }
        cursor.close();
        cursor = null;

        return produitPlace;
    }

    public static void insererBDDLocaleActionUtilisatuer(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        final String tableNom = "ProduitPlace";
        final String erreurSynchronisationLibelle = "ProduitPlace non synchronisés";

        if (!statutConnexion) {
            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
        }
        else{
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteActionUtilisateur;
            RequestQueue requestQueue = new Volley().newRequestQueue(context);

            viderTableProduitPlace(db);

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

                            viderTableProduitPlace(db);
                            JSONArray produitPlaceJSONArray = response.getJSONArray("ProduitPlace");

                            for (int i = 0; i < produitPlaceJSONArray.length(); i++) {
                                JSONObject actionUtilisateurJSONObject = produitPlaceJSONArray.getJSONObject(i);
                                Produit_Place produitPlace = new Produit_Place(actionUtilisateurJSONObject);

                                long rowID = insererProduitPlaceEnBDD(db, produitPlace);
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
        public static final String TABLE_PRODUIT_PLACE = "ProduitPlace";

        public static final String CLE_COL_ID_PRODUIT_PRODUIT_PLACE = "ProduitID";
        public static final int NUM_COL_ID_PRODUIT_PRODUIT_PLACE = 1;
        public static final String TYPE_COL_ID_PRODUIT_PRODUIT_PLACE = "INTEGER";
        public static final String CLE_COL_ID_DEPOT_PRODUIT_PLACE = "DepotID";
        public static final int NUM_COL_ID_DEPOT_PRODUIT_PLACE = 2;
        public static final String TYPE_COL_ID_DEPOT_PRODUIT_PLACE = "INTEGER";
        public static final String CLE_COL_ID_ZONE_PRODUIT_PLACE = "ZoneID";
        public static final int NUM_COL_ID_ZONE_PRODUIT_PLACE = 3;
        public static final String TYPE_COL_ID_ZONE_PRODUIT_PLACE = "INTEGER";
        public static final String CLE_COL_ID_PLACE_PRODUIT_PLACE = "PlaceID";
        public static final int NUM_COL_ID_PLACE_PRODUIT_PLACE = 4;
        public static final String TYPE_COL_ID_PLACE_PRODUIT_PLACE = "INTEGER";
        public static final String CLE_COL_NOM_DEPOT_PRODUIT_PLACE = "DepotReference";
        public static final int NUM_COL_NOM_DEPOT_PRODUIT_PLACE = 5;
        public static final String TYPE_COL_NOM_DEPOT_PRODUIT_PLACE = "TEXT";
        public static final String CLE_COL_NOM_ZONE_PRODUIT_PLACE = "ZoneNom";
        public static final int NUM_COL_NOM_ZONE_PRODUIT_PLACE = 6;
        public static final String TYPE_COL_NOM_ZONE_PRODUIT_PLACE = "TEXT";
        public static final String CLE_COL_NOM_PLACE_PRODUIT_PLACE = "PlaceNom";
        public static final int NUM_COL_NOM_PLACE_PRODUIT_PLACE = 7;
        public static final String TYPE_COL_NOM_PLACE_PRODUIT_PLACE = "TEXT";
        public static final String CLE_COL_NOM_PRODUIT_PRODUIT_PLACE = "ProduitNom";
        public static final int NUM_COL_NOM_PRODUIT_PRODUIT_PLACE = 8;
        public static final String TYPE_COL_NOM_PRODUIT_PRODUIT_PLACE = "TEXT";

        public static final String CREATION_TABLE_PRODUIT_PLACE = " CREATE TABLE  " + Constantes.TABLE_PRODUIT_PLACE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_ID_PRODUIT_PRODUIT_PLACE + " " + Constantes.TYPE_COL_ID_PRODUIT_PRODUIT_PLACE + " , "
                + Constantes.CLE_COL_ID_DEPOT_PRODUIT_PLACE + " " + Constantes.TYPE_COL_ID_DEPOT_PRODUIT_PLACE + " , "
                + Constantes.CLE_COL_ID_ZONE_PRODUIT_PLACE + " " + Constantes.TYPE_COL_ID_ZONE_PRODUIT_PLACE + " , "
                + Constantes.CLE_COL_ID_PLACE_PRODUIT_PLACE + " " + Constantes.TYPE_COL_ID_PLACE_PRODUIT_PLACE + " , "
                + Constantes.CLE_COL_NOM_DEPOT_PRODUIT_PLACE + " " + Constantes.TYPE_COL_NOM_DEPOT_PRODUIT_PLACE + " , "
                + Constantes.CLE_COL_NOM_ZONE_PRODUIT_PLACE + " " + Constantes.TYPE_COL_NOM_ZONE_PRODUIT_PLACE + " , "
                + Constantes.CLE_COL_NOM_PLACE_PRODUIT_PLACE + " " + Constantes.TYPE_COL_NOM_PLACE_PRODUIT_PLACE + " , "
                + Constantes.CLE_COL_NOM_PRODUIT_PRODUIT_PLACE + " " + Constantes.TYPE_COL_NOM_PRODUIT_PRODUIT_PLACE
                + " ); ";

    }
}

