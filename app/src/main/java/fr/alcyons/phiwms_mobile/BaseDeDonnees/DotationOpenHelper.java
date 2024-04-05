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
import fr.alcyons.phiwms_mobile.Classes.Detail_Dot;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.EVENT;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import com.example.phiwms_mobile.R;

/**
 * Created by jessica on 02/10/2017.
 */

public class DotationOpenHelper extends DBOpenHelper {
    public DotationOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableDotation(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_DOTATION, null, null);
    }

    public static long insererDotationEnBDD(SQLiteDatabase db, Dotation objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_DOTATION, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_INTITULE_DOTATION, objet.getIntitulé());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_DOTATION, objet.getRef_Depot());
        contentValues.put(Constantes.CLE_COL_DEBUT_DOTATION, objet.getDébut());
        contentValues.put(Constantes.CLE_COL_FIN_DOTATION, objet.getFin());
        contentValues.put(Constantes.CLE_COL_INTERROMPU_DOTATION, objet.isInterrompu());
        contentValues.put(Constantes.CLE_COL_NB_SEMAINE_DOTATION, objet.getNB_Semaine());
        contentValues.put(Constantes.CLE_COL_VALORISATION_TTC_DOTATION, objet.getValorisation_TTC());
        contentValues.put(Constantes.CLE_COL_DOTATION_STD_DOTATION, objet.getDotation_Std());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_DOTATION, objet.getCommentaire());
        contentValues.put(Constantes.CLE_COL_DEPOT_UID_DOTATION, objet.getDepot_UID());
        contentValues.put(Constantes.CLE_COL_NB_PATIENTS_DOTATION, objet.getNb_patients());
        contentValues.put(Constantes.CLE_COL_TOURNEE_REFERENCE_DOTATION, objet.getTournee_Reference());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_DOTATION, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_DOTATION, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_DOTATION, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_TECH_UID_DOTATION, objet.getTech_UID());
        contentValues.put(Constantes.CLE_COL_URGENCE_DOTATION, objet.isURGENCE());
        contentValues.put(Constantes.CLE_COL_SECURISE_DOTATION, objet.isSECURISE());
        contentValues.put(Constantes.CLE_COL_TAUXSTOCKIDEAL_DOTATION, objet.getTauxStockIdeal());
        contentValues.put(Constantes.CLE_COL_INSTALLATION_DOTATION, objet.isINSTALLATION());
        contentValues.put(Constantes.CLE_COL_PLEINVIDE_DOTATION, objet.isPLEINVIDE());
        contentValues.put(Constantes.CLE_COL_PROTOCOLE_UID_DOTATION, objet.getProtocole_UID());
        long rowID = db.insert(Constantes.TABLE_DOTATION, null, contentValues);
        objet.setPhiMR4UUID((int) rowID);
        return rowID;
    }

    public static Dotation getDotationByPhiMR4UUID(SQLiteDatabase db, int id) {
        Dotation dotation = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_DOTATION + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " =? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            dotation = new Dotation(cursor);
        }
        cursor.close();
        cursor = null;
        return dotation;
    }

    public List<Dotation> getDotationByDepot(SQLiteDatabase db, Integer depotUID) {
        String critereRecherche = String.valueOf(depotUID);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DOTATION + " WHERE " + Constantes.CLE_COL_DEPOT_UID_DOTATION + " LIKE ?", new String[]{critereRecherche});

        List<Dotation> dotationList = new ArrayList<>();

        while (cursor.moveToNext()) {
            Dotation dotation = new Dotation(cursor);
            if(!dotation.isPLEINVIDE()){
                dotationList.add(dotation);
            }
        }
        cursor.close();
        cursor = null;
        return dotationList;
    }

    public List<Dotation> getDotationPleinByDepot(SQLiteDatabase db, Integer depotUID) {
        String critereRecherche = String.valueOf(depotUID);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DOTATION + " WHERE " + Constantes.CLE_COL_DEPOT_UID_DOTATION + " LIKE ? AND " + Constantes.CLE_COL_PLEINVIDE_DOTATION + "=1", new String[]{critereRecherche});

        List<Dotation> dotationList = new ArrayList<>();

        while (cursor.moveToNext()) {
            dotationList.add(new Dotation(cursor));
        }
        cursor.close();
        cursor = null;
        return dotationList;
    }

    public static Dotation getDotationPleinByStringId(SQLiteDatabase db, String id) {
        Dotation dotationCorrespondant = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DOTATION + " WHERE " + Constantes.CLE_COL__UID_DOTATION + "= ? ", new String[]{id});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            Dotation dotation = new Dotation(cursor);
            if (dotation.isPLEINVIDE()) {
                dotationCorrespondant = dotation;
            }

        }
        cursor.close();
        cursor = null;
        return dotationCorrespondant;
    }

    public static List<Dotation> getAllDotationPleinVide(SQLiteDatabase db)
    {
        List<Dotation> listDotation = new ArrayList<>();
        Cursor cursor = db.rawQuery("Select * FROM " + Constantes.TABLE_DOTATION + " Where " + Constantes.CLE_COL_PLEINVIDE_DOTATION + "=1", null);

        while(cursor.moveToNext())
        {
            Dotation courante = new Dotation(cursor);
            listDotation.add(courante);
        }

        cursor.close();
        cursor = null;

        return listDotation;
    }

    public List<String> getDepotDotationPleinVide(SQLiteDatabase db) {
        List<String> listeDepot = new ArrayList<>();
        Cursor cursor = db.rawQuery("Select * FROM " + Constantes.TABLE_DOTATION + " Where " + Constantes.CLE_COL_PLEINVIDE_DOTATION + "=1 group by " + Constantes.CLE_COL_REF_DEPOT_DOTATION, null);


        while (cursor.moveToNext()) {
            listeDepot.add(String.valueOf(cursor.getString(Constantes.NUM_COL_REF_DEPOT_DOTATION)));
        }

        cursor.close();
        cursor = null;
        return listeDepot;
    }

    public void insererBDDLocaleDotation(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        final String tableNom = "Dotations";
        final String erreurSynchronisationLibelle = "Dotations non synchronisées";

        if (!OutilsGestionConnexionReseau.isServerAccessible(context)) {
            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
        }
        else{
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteDotationUF;
            RequestQueue requestQueue = new Volley().newRequestQueue(context);

            viderTableDotation(db);
            Detail_DotOpenHelper.viderTableDetail_Dot(db);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, new Response.Listener<JSONObject>() {
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
                                viderBasesDeDonnees(db);
                                erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                            } else if (!erreur.equals("Aucune Dotation trouvée")) {
                                erreur = "Erreur API Dotations";
                            }
                            else{
                                etat = true;
                            }
                        } else {

                            JSONArray dotationJSONArray = response.getJSONArray("Dotations");

                            for (int i = 0; i < dotationJSONArray.length(); i++) {
                                JSONObject dotationJSONObject = dotationJSONArray.getJSONObject(i);
                                Dotation dotation = new Dotation(dotationJSONObject);

                                long rowID = insererDotationEnBDD(db, dotation);
                                if(dotationJSONObject.has("detail_dots"))
                                {
                                    JSONArray DetailDotJsonArray = dotationJSONObject.getJSONArray("detail_dots");
                                    for (int j = 0; j < DetailDotJsonArray.length(); j++) {
                                        JSONObject detailDotationJSONObject = DetailDotJsonArray.getJSONObject(j);
                                        Detail_Dot detail_dot = new Detail_Dot(detailDotationJSONObject);
                                        long detailRowID = Detail_DotOpenHelper.insererDetail_DotEnBDD(db, detail_dot);
                                    }
                                }
                            }
                            if(response.has("Events")) {
                                JSONArray eventJSONArray = response.getJSONArray("Events");
                                for (int i = 0; i < eventJSONArray.length(); i++) {
                                    JSONObject eventJSONObject = eventJSONArray.getJSONObject(i);
                                    EVENT event = new EVENT(eventJSONObject);

                                    long rowID = EVENTOpenHelper.insererEVENTEnBDD(db, event);
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
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_DOTATION = "Dotation";
        public static final String CLE_COL__UID_DOTATION = "_UID";
        public static final int NUM_COL__UID_DOTATION = 1;
        public static final String TYPE_COL__UID_DOTATION = "INTEGER";
        public static final String CLE_COL_INTITULE_DOTATION = "Intitulé";
        public static final int NUM_COL_INTITULE_DOTATION = 2;
        public static final String TYPE_COL_INTITULE_DOTATION = "TEXT";
        public static final String CLE_COL_REF_DEPOT_DOTATION = "Ref_Depot";
        public static final int NUM_COL_REF_DEPOT_DOTATION = 3;
        public static final String TYPE_COL_REF_DEPOT_DOTATION = "TEXT";
        public static final String CLE_COL_DEBUT_DOTATION = "Début";
        public static final int NUM_COL_DEBUT_DOTATION = 4;
        public static final String TYPE_COL_DEBUT_DOTATION = "TEXT";
        public static final String CLE_COL_FIN_DOTATION = "Fin";
        public static final int NUM_COL_FIN_DOTATION = 5;
        public static final String TYPE_COL_FIN_DOTATION = "TEXT";
        public static final String CLE_COL_INTERROMPU_DOTATION = "Interrompu";
        public static final int NUM_COL_INTERROMPU_DOTATION = 6;
        public static final String TYPE_COL_INTERROMPU_DOTATION = "INTEGER";
        public static final String CLE_COL_NB_SEMAINE_DOTATION = "NB_Semaine";
        public static final int NUM_COL_NB_SEMAINE_DOTATION = 7;
        public static final String TYPE_COL_NB_SEMAINE_DOTATION = "INTEGER";
        public static final String CLE_COL_VALORISATION_TTC_DOTATION = "Valorisation_TTC";
        public static final int NUM_COL_VALORISATION_TTC_DOTATION = 8;
        public static final String TYPE_COL_VALORISATION_TTC_DOTATION = "INTEGER";
        public static final String CLE_COL_DOTATION_STD_DOTATION = "Dotation_Std";
        public static final int NUM_COL_DOTATION_STD_DOTATION = 9;
        public static final String TYPE_COL_DOTATION_STD_DOTATION = "TEXT";
        public static final String CLE_COL_COMMENTAIRE_DOTATION = "Commentaire";
        public static final int NUM_COL_COMMENTAIRE_DOTATION = 10;
        public static final String TYPE_COL_COMMENTAIRE_DOTATION = "TEXT";
        public static final String CLE_COL_DEPOT_UID_DOTATION = "depot_UID";
        public static final int NUM_COL_DEPOT_UID_DOTATION = 11;
        public static final String TYPE_COL_DEPOT_UID_DOTATION = "INTEGER";
        public static final String CLE_COL_NB_PATIENTS_DOTATION = "nb_patients";
        public static final int NUM_COL_NB_PATIENTS_DOTATION = 12;
        public static final String TYPE_COL_NB_PATIENTS_DOTATION = "INTEGER";
        public static final String CLE_COL_TOURNEE_REFERENCE_DOTATION = "Tournee_Reference";
        public static final int NUM_COL_TOURNEE_REFERENCE_DOTATION = 13;
        public static final String TYPE_COL_TOURNEE_REFERENCE_DOTATION = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_DOTATION = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_DOTATION = 14;
        public static final String TYPE_COL_SYS_DT_MAJ_DOTATION = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_DOTATION = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_DOTATION = 15;
        public static final String TYPE_COL_SYS_HEURE_MAJ_DOTATION = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_DOTATION = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_DOTATION = 16;
        public static final String TYPE_COL_SYS_USER_MAJ_DOTATION = "TEXT";
        public static final String CLE_COL_TECH_UID_DOTATION = "tech_UID";
        public static final int NUM_COL_TECH_UID_DOTATION = 17;
        public static final String TYPE_COL_TECH_UID_DOTATION = "INTEGER";
        public static final String CLE_COL_URGENCE_DOTATION = "URGENCE";
        public static final int NUM_COL_URGENCE_DOTATION = 18;
        public static final String TYPE_COL_URGENCE_DOTATION = "INTEGER";
        public static final String CLE_COL_SECURISE_DOTATION = "SECURISE";
        public static final int NUM_COL_SECURISE_DOTATION = 19;
        public static final String TYPE_COL_SECURISE_DOTATION = "INTEGER";
        public static final String CLE_COL_TAUXSTOCKIDEAL_DOTATION = "TauxStockIdeal";
        public static final int NUM_COL_TAUXSTOCKIDEAL_DOTATION = 20;
        public static final String TYPE_COL_TAUXSTOCKIDEAL_DOTATION = "INTEGER";
        public static final String CLE_COL_INSTALLATION_DOTATION = "INSTALLATION";
        public static final int NUM_COL_INSTALLATION_DOTATION = 21;
        public static final String TYPE_COL_INSTALLATION_DOTATION = "INTEGER";
        public static final String CLE_COL_PLEINVIDE_DOTATION = "PLEINVIDE";
        public static final int NUM_COL_PLEINVIDE_DOTATION = 22;
        public static final String TYPE_COL_PLEINVIDE_DOTATION = "INTEGER";
        public static final String CLE_COL_PROTOCOLE_UID_DOTATION = "protocole_UID";
        public static final int NUM_COL_PROTOCOLE_UID_DOTATION = 23;
        public static final String TYPE_COL_PROTOCOLE_UID_DOTATION = "INTEGER";

        public static final String CREATION_TABLE_DOTATION = " CREATE TABLE       " + Constantes.TABLE_DOTATION
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL__UID_DOTATION + " " + Constantes.TYPE_COL__UID_DOTATION + " , "
                + Constantes.CLE_COL_INTITULE_DOTATION + " " + Constantes.TYPE_COL_INTITULE_DOTATION + " , "
                + Constantes.CLE_COL_REF_DEPOT_DOTATION + " " + Constantes.TYPE_COL_REF_DEPOT_DOTATION + " , "
                + Constantes.CLE_COL_DEBUT_DOTATION + " " + Constantes.TYPE_COL_DEBUT_DOTATION + " , "
                + Constantes.CLE_COL_FIN_DOTATION + " " + Constantes.TYPE_COL_FIN_DOTATION + " , "
                + Constantes.CLE_COL_INTERROMPU_DOTATION + " " + Constantes.TYPE_COL_INTERROMPU_DOTATION + " , "
                + Constantes.CLE_COL_NB_SEMAINE_DOTATION + " " + Constantes.TYPE_COL_NB_SEMAINE_DOTATION + " , "
                + Constantes.CLE_COL_VALORISATION_TTC_DOTATION + " " + Constantes.TYPE_COL_VALORISATION_TTC_DOTATION + " , "
                + Constantes.CLE_COL_DOTATION_STD_DOTATION + " " + Constantes.TYPE_COL_DOTATION_STD_DOTATION + " , "
                + Constantes.CLE_COL_COMMENTAIRE_DOTATION + " " + Constantes.TYPE_COL_COMMENTAIRE_DOTATION + " , "
                + Constantes.CLE_COL_DEPOT_UID_DOTATION + " " + Constantes.TYPE_COL_DEPOT_UID_DOTATION + " , "
                + Constantes.CLE_COL_NB_PATIENTS_DOTATION + " " + Constantes.TYPE_COL_NB_PATIENTS_DOTATION + " , "
                + Constantes.CLE_COL_TOURNEE_REFERENCE_DOTATION + " " + Constantes.TYPE_COL_TOURNEE_REFERENCE_DOTATION + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_DOTATION + " " + Constantes.TYPE_COL_SYS_DT_MAJ_DOTATION + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_DOTATION + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_DOTATION + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_DOTATION + " " + Constantes.TYPE_COL_SYS_USER_MAJ_DOTATION + " , "
                + Constantes.CLE_COL_TECH_UID_DOTATION + " " + Constantes.TYPE_COL_TECH_UID_DOTATION + " , "
                + Constantes.CLE_COL_URGENCE_DOTATION + " " + Constantes.TYPE_COL_URGENCE_DOTATION + " , "
                + Constantes.CLE_COL_SECURISE_DOTATION + " " + Constantes.TYPE_COL_SECURISE_DOTATION + " , "
                + Constantes.CLE_COL_TAUXSTOCKIDEAL_DOTATION + " " + Constantes.TYPE_COL_TAUXSTOCKIDEAL_DOTATION + " , "
                + Constantes.CLE_COL_INSTALLATION_DOTATION + " " + Constantes.TYPE_COL_INSTALLATION_DOTATION + " , "
                + Constantes.CLE_COL_PLEINVIDE_DOTATION + " " + Constantes.TYPE_COL_PLEINVIDE_DOTATION + " , "
                + Constantes.CLE_COL_PROTOCOLE_UID_DOTATION + " " + Constantes.TYPE_COL_PROTOCOLE_UID_DOTATION
                + " ); ";

    }
}

