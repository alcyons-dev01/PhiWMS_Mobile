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

import java.util.HashMap;
import java.util.Map;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Lot_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Parametres_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;

public class Parametres_SerialisationOpenHelper  extends DBOpenHelper {

    private static final int MY_SOCKET_TIMEOUT_MS = 100;

    public Parametres_SerialisationOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableParametres_Serialisation(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PARAMETRES_SERIALISATION, null, null);
    }

    public static Parametres_Serialisation getParametres_Serialisation(SQLiteDatabase db){
        Parametres_Serialisation objet=null;
        Cursor cursor=db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERIALISATION, null);
        if(cursor.getCount()==1){
            cursor.moveToFirst();
            objet=new Parametres_Serialisation(cursor);
        }

        return objet;
    }

    public static long insererParametres_Serialisation(SQLiteDatabase db, Parametres_Serialisation objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_ID_PARAMETRES_SERIALISATION, objet.getID());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION, objet.getServeurAPI_host());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION, objet.getServeurLDAP_host());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION, objet.isCommunicationDiffere());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION, objet.isDispenserReception());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION, objet.isDispenserDelivrance());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION, objet.isStockParNumeroDeSerie());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION, objet.getServeurLDAP_port());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION, objet.getServeurLDAP_login());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION, objet.getServeurLDAP_password());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION, objet.getServeurLDAP_nomDomaine());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_DOSSIERVISION_PARAMETRES_SERIALISATION, objet.getDossierVision());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION, objet.getFranceMVO_identifiant());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION, objet.getFranceMVO_mdp());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION, objet.getFranceMVO_tan());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION, objet.isFranceMVO_termesEtConditions());

        long rowID = db.insert(Constantes.TABLE_PARAMETRES_SERIALISATION, null, contentValues);
        objet.setphiwms_mobileUUID((int) rowID);
        return rowID;
    }

    public static long mettreAJourParametreSerialisation(SQLiteDatabase db, Parametres_Serialisation objet) {
        // Récupération des valeurs à mettre à jour
        ContentValues contentValues = new ContentValues();
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_ID_PARAMETRES_SERIALISATION, objet.getID());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION, objet.getServeurAPI_host());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION, objet.getServeurLDAP_host());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION, objet.isCommunicationDiffere());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION, objet.isDispenserReception());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION, objet.isDispenserDelivrance());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION, objet.isStockParNumeroDeSerie());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION, objet.getServeurLDAP_port());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION, objet.getServeurLDAP_login());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION, objet.getServeurLDAP_password());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION, objet.getServeurLDAP_nomDomaine());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_DOSSIERVISION_PARAMETRES_SERIALISATION, objet.getDossierVision());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION, objet.getFranceMVO_identifiant());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION, objet.getFranceMVO_mdp());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION, objet.getFranceMVO_tan());
        contentValues.put(Parametres_SerialisationOpenHelper.Constantes.CLE_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION, objet.isFranceMVO_termesEtConditions());
        contentValues.put(Constantes.CLE_COL_TOKEN_SERIALISATION, objet.getTokenSerialisation());

        return db.update(Constantes.TABLE_PARAMETRES_SERIALISATION, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + String.valueOf(objet.getPhiMR4UUID()), null);
    }

    public static void synchronisationParametres_Serialisation(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        if (!statutConnexion) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        final String tableNom = "Paramètres Sérialisation";
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteParametreSerialisation;
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
                                } else if (!erreur.contentEquals("Aucun Paramètres sérialisation trouvé")) {
                                    Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocaleParametreSerialisation", "alerte");
                                }
                            } else {
                                viderTableParametres_Serialisation(db);
                                JSONArray serialisation_JSONArray = response.getJSONArray("Parametres_Serialisation");
                                for (int i = 0; i < serialisation_JSONArray.length(); i++) {
                                    JSONObject serialisation_JSONObject = serialisation_JSONArray.getJSONObject(i);
                                    Parametres_Serialisation parametresSerialisation = new Parametres_Serialisation(serialisation_JSONObject);
                                    Parametres_SerialisationOpenHelper.insererParametres_Serialisation(db, parametresSerialisation);
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
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocaleSerialisation", "alerte");
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




    public static class Constantes implements BaseColumns{
        public static final String TABLE_PARAMETRES_SERIALISATION="Parametres_Serialisation";
        public static final String CLE_COL_ID_PARAMETRES_SERIALISATION="ID";
        public static final int NUM_COL_ID_PARAMETRES_SERIALISATION=1;
        public static final String TYPE_COL_ID_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION="serveurAPI_host";
        public static final int NUM_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION=2;
        public static final String TYPE_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION="serveurLDAP_host";
        public static final int NUM_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION=3;
        public static final String TYPE_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION="communicationDiffere";
        public static final int NUM_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION=4;
        public static final String TYPE_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION="dispenserReception";
        public static final int NUM_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION=5;
        public static final String TYPE_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION="dispenserDelivrance";
        public static final int NUM_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION=6;
        public static final String TYPE_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION="stockParNumeroDeSerie";
        public static final int NUM_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION=7;
        public static final String TYPE_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION="serveurLDAP_port";
        public static final int NUM_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION=8;
        public static final String TYPE_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION="serveurLDAP_login";
        public static final int NUM_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION=9;
        public static final String TYPE_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION="serveurLDAP_password";
        public static final int NUM_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION=10;
        public static final String TYPE_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION="serveurLDAP_nomDomaine";
        public static final int NUM_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION=11;
        public static final String TYPE_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_DOSSIERVISION_PARAMETRES_SERIALISATION="dossierVision";
        public static final int NUM_COL_DOSSIERVISION_PARAMETRES_SERIALISATION=12;
        public static final String TYPE_COL_DOSSIERVISION_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION="franceMVO_identifiant";
        public static final int NUM_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION=13;
        public static final String TYPE_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION="franceMVO_mdp";
        public static final int NUM_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION=14;
        public static final String TYPE_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION="franceMVO_tan";
        public static final int NUM_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION=15;
        public static final String TYPE_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION="franceMVO_termesEtConditions";
        public static final int NUM_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION=16;
        public static final String TYPE_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_MODULEVISION_PARAMETRES_SERIALISATION="moduleVision";
        public static final int NUM_COL_MODULEVISION_PARAMETRES_SERIALISATION=17;
        public static final String TYPE_COL_MODULEVISION_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_TOKEN_SERIALISATION="tokenSerialisation";
        public static final int NUM_COL_TOKEN_SERIALISATION=18;
        public static final String TYPE_COL_TOKEN_SERIALISATION="TEXT";

        public static final String CREATION_TABLE_PARAMETRES_SERIALISATION = " CREATE TABLE       " + Constantes.TABLE_PARAMETRES_SERIALISATION
                +"("+
                DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID+"    PRIMARY KEY,"
                + Constantes.CLE_COL_ID_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_ID_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_DOSSIERVISION_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_DOSSIERVISION_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_MODULEVISION_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_MODULEVISION_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_TOKEN_SERIALISATION + "   " + Constantes.TYPE_COL_TOKEN_SERIALISATION
                + " ); ";

    }
}
