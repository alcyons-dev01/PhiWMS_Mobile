package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.RequiresApi;

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
import fr.alcyons.phiwms_mobile.Classes.ImprimanteEtiquette;
import fr.alcyons.phiwms_mobile.Classes.Parametres_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;

public class ImprimanteEtiquetteOpenHelper  extends DBOpenHelper {

    public ImprimanteEtiquetteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableImprimante_Etiquette(SQLiteDatabase db) {
        db.delete(ImprimanteEtiquetteOpenHelper.Constantes.TABLE_IMPRIMANTE_ETIQUETTE, null, null);
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        return db.delete(ImprimanteEtiquetteOpenHelper.Constantes.TABLE_IMPRIMANTE_ETIQUETTE, Constantes.CLE_COL_UID_IMPRIMANTE + "=?", new String[]{"ALCYONS_Imprimante"});
    }

    public static ImprimanteEtiquette getImprimanteByNom(SQLiteDatabase db, String nom) {
        ImprimanteEtiquette imprimante = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + ImprimanteEtiquetteOpenHelper.Constantes.TABLE_IMPRIMANTE_ETIQUETTE +" WHERE "+ Constantes.CLE_COL_NOM_IMPRIMANTE+"=?", new String[]{nom});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            imprimante = new ImprimanteEtiquette(cursor);
        }
        cursor.close();
        cursor = null;

        return imprimante;
    }

    public static ImprimanteEtiquette getImprimanteById(SQLiteDatabase db, int idImprimante) {
        ImprimanteEtiquette imprimante = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + ImprimanteEtiquetteOpenHelper.Constantes.TABLE_IMPRIMANTE_ETIQUETTE +" WHERE "+ Constantes.CLE_COL_UID_IMPRIMANTE+"=?", new String[]{String.valueOf(idImprimante)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            imprimante = new ImprimanteEtiquette(cursor);
        }
        cursor.close();
        cursor = null;

        return imprimante;
    }

    public static List<ImprimanteEtiquette> getAllImprimante(SQLiteDatabase db) {
        List<ImprimanteEtiquette> list_imprimante = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ImprimanteEtiquetteOpenHelper.Constantes.TABLE_IMPRIMANTE_ETIQUETTE, new String[]{});

        while (cursor.moveToNext()) {
            ImprimanteEtiquette imprimantecourante = new ImprimanteEtiquette(cursor);
            list_imprimante.add(imprimantecourante);
        }
        cursor.close();
        cursor = null;

        return list_imprimante;
    }

    public static long insererUneImprimanteEnBDD(SQLiteDatabase db, ImprimanteEtiquette imprimanteEtiquette) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_UID_IMPRIMANTE, imprimanteEtiquette.getId());
        contentValues.put(Constantes.CLE_COL_NOM_IMPRIMANTE, imprimanteEtiquette.getNom());
        contentValues.put(Constantes.CLE_COL_ADRESSEIP_IMPRIMANTE, imprimanteEtiquette.getAdresseIP());
        contentValues.put(Constantes.CLE_COL_PORT_IMPRIMANTE, imprimanteEtiquette.getPortIP());
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENTUID_IMPRIMANTE, imprimanteEtiquette.getEtablissementUID());

        // Insertion du dépot en BDD
        long rowId = db.insert(ImprimanteEtiquetteOpenHelper.Constantes.TABLE_IMPRIMANTE_ETIQUETTE, null, contentValues);

        imprimanteEtiquette.setphiwms_mobileUUID((int) rowId);

        return rowId;
    }

    public static void synchronisationImprimante_Etiquette(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        if (!statutConnexion) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        final String tableNom = "Imprimante Etiquette";
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriImprimanteEtiquette;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
                                    Alerte.afficherAlerte(context, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                    ((Activity) context).finishAffinity();
                                    Intent intent = new Intent(context, AuthentificationActivity.class);
                                    context.startActivity(intent);
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                    ((Activity) context).finishAffinity();
                                    Intent intent = new Intent(context, AuthentificationActivity.class);
                                    context.startActivity(intent);
                                } else if (!erreur.contentEquals("Aucune imprimante trouvée")) {
                                    Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocaleImprimanteEtiquette", "alerte");
                                }
                                else
                                {
                                    erreur = "";
                                    etat = true;
                                }
                            } else {
                                viderTableImprimante_Etiquette(db);
                                JSONArray imprimanteEtiquette_JSONArray = response.getJSONArray("Imprimante_Etiquette");
                                for (int i = 0; i < imprimanteEtiquette_JSONArray.length(); i++) {
                                    JSONObject imprimante_JSONObject = imprimanteEtiquette_JSONArray.getJSONObject(i);
                                    ImprimanteEtiquette imprimanteEtiquette = new ImprimanteEtiquette(imprimante_JSONObject);
                                    insererUneImprimanteEnBDD(db, imprimanteEtiquette);
                                }
                            }
                            String activity_name = context.getClass().getSimpleName();
                            if(activity_name.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
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
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLolcalImprimanteEtiquette", "alerte");
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
        public static final String TABLE_IMPRIMANTE_ETIQUETTE = "ImprimanteEtiquette";

        public static final String CLE_COL_UID_IMPRIMANTE = "id";
        public static final int NUM_COL_UID_IMPRIMANTE = 1;
        public static final String TYPE_COL_UID_IMPRIMANTE = "INTEGER";

        public static final String CLE_COL_NOM_IMPRIMANTE = "nom";
        public static final int NUM_COL_NOM_IMPRIMANTE = 2;
        public static final String TYPE_COL_NOM_IMPRIMANTE = "TEXT";

        public static final String CLE_COL_ADRESSEIP_IMPRIMANTE = "adresseIP";
        public static final int NUM_COL_ADRESSEIP_IMPRIMANTE = 3;
        public static final String TYPE_COL_ADRESSEIP_IMPRIMANTE = "TEXT";

        public static final String CLE_COL_PORT_IMPRIMANTE = "portIP";
        public static final int NUM_COL_PORT_IMPRIMANTE = 4;
        public static final String TYPE_COL_PORT_IMPRIMANTE = "TEXT";

        public static final String CLE_COL_ETABLISSEMENTUID_IMPRIMANTE = "idEtablissement";
        public static final int NUM_COL_ETABLISSEMENTUID_IMPRIMANTE = 5;
        public static final String TYPE_COL_ETABLISSEMENTUID_IMPRIMANTE  = "INTEGER";


        public static final String CREATION_TABLE_IMPRIMANTE_ETIQUETTE = "CREATE TABLE " + ImprimanteEtiquetteOpenHelper.Constantes.TABLE_IMPRIMANTE_ETIQUETTE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + ImprimanteEtiquetteOpenHelper.Constantes.CLE_COL_UID_IMPRIMANTE + " " + ImprimanteEtiquetteOpenHelper.Constantes.TYPE_COL_UID_IMPRIMANTE + " ,"
                + ImprimanteEtiquetteOpenHelper.Constantes.CLE_COL_NOM_IMPRIMANTE + " " + ImprimanteEtiquetteOpenHelper.Constantes.TYPE_COL_NOM_IMPRIMANTE + " ,"
                + ImprimanteEtiquetteOpenHelper.Constantes.CLE_COL_ADRESSEIP_IMPRIMANTE + " " + ImprimanteEtiquetteOpenHelper.Constantes.TYPE_COL_ADRESSEIP_IMPRIMANTE + " ,"
                + ImprimanteEtiquetteOpenHelper.Constantes.CLE_COL_PORT_IMPRIMANTE + " " + ImprimanteEtiquetteOpenHelper.Constantes.TYPE_COL_PORT_IMPRIMANTE + " ,"
                + ImprimanteEtiquetteOpenHelper.Constantes.CLE_COL_ETABLISSEMENTUID_IMPRIMANTE + " " + ImprimanteEtiquetteOpenHelper.Constantes.TYPE_COL_ETABLISSEMENTUID_IMPRIMANTE
                + ");";
    }
}
