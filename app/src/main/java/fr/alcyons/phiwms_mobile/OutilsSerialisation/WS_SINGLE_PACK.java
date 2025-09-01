package fr.alcyons.phiwms_mobile.OutilsSerialisation;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Parametres_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Parametres_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.SurveillanceReference;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.GestionCodeErreurNMVO;
import fr.alcyons.phiwms_mobile.Outils.OutilsEncodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import okhttp3.internal.Util;

public class WS_SINGLE_PACK extends MainActivity {

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message mesg) {
            throw new RuntimeException();
        }
    };
    public static RetryPolicy retryPolicy = new RetryPolicy() {
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
    };
    private static String url = "https://phir4.alcyons.fr/api/v5/WS_SINGLE_PACK/";

    //changement pour test avec la v2
    //private static String url = "https://phir4.alcyons.fr/api/v2/WS_SINGLE_PACK/";

    public static void NMVS_G110_verifySinglePack(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, int serialisationUID, final String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA) {
        final String tokenLdap = AuthentificationLDAP(context, db, utilisateur);
        if (!utilisateur.getToken().contentEquals("")) {
            PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, serialisationUID);
            if(phSerialisation == null)
                phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, serialisationUID);

            // Tentative de lancer la sychronisation
            if (statutConnexion) {
                String urlRequete = url + "G110";
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                JSONObject data = new JSONObject();
                try {
                    JSONObject header = new JSONObject();
                    header.put("ClientTrxId", phSerialisation.getRefClientTrxId());

                    JSONObject body = new JSONObject();
                    body.put("ProductCode_VALUE", ProductCode_VALUE_VA);
                    body.put("ProductCode_SHEME", ProductCode_SHEME_VA);
                    body.put("Batch_ID", Batch_ID_VA);
                    body.put("Batch_EXPDATE", Batch_EXPDATE_VA);
                    body.put("Pack_SN", Pack_SN_VA);

                    data.put("header", header);
                    data.put("body", body);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final PH_Serialisation finalPhSerialisation = phSerialisation;
                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, data, new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject Header = response.getJSONObject("Header");
                            JSONObject Transaction = Header.getJSONObject("Transaction");
                            String NMVSTrxId = Transaction.getString("NMVSTrxId");

                            JSONObject Body = response.getJSONObject("Body");
                            JSONObject ReturnCode = Body.getJSONObject("ReturnCode");
                            String code = ReturnCode.getString("code");
                            String desc = ReturnCode.getString("desc");

                            JSONObject Pack = Body.getJSONObject("Pack");
                            String state = Pack.getString("state");

                            String resultat = state;
                            String raison = "";

                            if (code.contentEquals("NMVS_SUCCESS")) {
                                if (state.contentEquals("INACTIVE")) {
                                    raison = Pack.getString("Reason");
                                } else {
                                    raison = state;
                                }
                            } else {
                                if (!resultat.contentEquals("UNKNOWN")) {
                                    resultat = "ERREUR";
                                }
                                raison = desc;


                                // TRACE LOG
                                Random SurveillanceReferenceRandom = new Random();
                                int id_surveillance = SurveillanceReferenceRandom.nextInt();
                                if (id_surveillance > 0) {
                                    id_surveillance = id_surveillance * -1;
                                }
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String surveillanceDate = sdf.format(calendar.getTime());

                                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                                String surveillanceHeure = mdformat.format(calendar.getTime());

                                int produit_id = finalPhSerialisation.getProduitUID();
                                int serialisationID = finalPhSerialisation.get_UID();
                                String motif = GestionCodeErreurNMVO.getMessage(code);
                                String actionAMener = "";
                                String statut = "NON LU";
                                String traitePar = utilisateur.getIdentifiant();
                                String traiteDate = surveillanceDate;
                                String traiteHeure = surveillanceHeure;
                                String produitLot = finalPhSerialisation.getNumeroLot();
                                String produitDatePéremption = finalPhSerialisation.getDatePeremptionAAMMJJ();
                                String produitNumeroSerie = finalPhSerialisation.getNumeroSerie();

                                SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);


                            }

                            finalPhSerialisation.setStatut("Executer");
                            finalPhSerialisation.setResultat(resultat);
                            finalPhSerialisation.setRaison(raison);
                            finalPhSerialisation.setNMVSTrxId(NMVSTrxId);

                            long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, finalPhSerialisation);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                       // handler.sendMessage(handler.obtainMessage());
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter Alcyons !\nOpération de vérification impossible.", "alerte");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", tokenLdap);
                        headers.put("Content-Type", "application/json;charset=utf-8");
                        return headers;
                    }
                };
                obreq.setRetryPolicy(retryPolicy);
                requestQueue.add(obreq);
                try {
                    Looper.loop();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void NMVS_G120_dispenseSinglePack(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, int serialisationUID, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA) {
        final String tokenLdap = AuthentificationLDAP(context, db, utilisateur);
        if (!utilisateur.getToken().contentEquals("")) {
            PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, serialisationUID);
            if(phSerialisation == null)
                phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, serialisationUID);

            // Tentative de lancer la sychronisation
            if (haveNetworkConnection(context)) {
                String urlRequete = url + "G120";
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                JSONObject data = new JSONObject();
                try {
                    JSONObject header = new JSONObject();
                    header.put("ClientTrxId", phSerialisation.getRefClientTrxId());

                    JSONObject body = new JSONObject();
                    body.put("ProductCode_VALUE", ProductCode_VALUE_VA);
                    body.put("ProductCode_SHEME", ProductCode_SHEME_VA);
                    body.put("Batch_ID", Batch_ID_VA);
                    body.put("Batch_EXPDATE", Batch_EXPDATE_VA);
                    body.put("Pack_SN", Pack_SN_VA);

                    data.put("header", header);
                    data.put("body", body);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final PH_Serialisation finalPhSerialisation = phSerialisation;
                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, data, new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject Header = response.getJSONObject("Header");
                            JSONObject Transaction = Header.getJSONObject("Transaction");
                            String NMVSTrxId = Transaction.getString("NMVSTrxId");

                            JSONObject Body = response.getJSONObject("Body");
                            JSONObject ReturnCode = Body.getJSONObject("ReturnCode");
                            String code = ReturnCode.getString("code");
                            String desc = ReturnCode.getString("desc");

                            JSONObject Pack = Body.getJSONObject("Pack");
                            String state = Pack.getString("state");

                            String resultat = state;
                            String raison = "";

                            if (code.contentEquals("NMVS_SUCCESS")) {
                                if (state.contentEquals("INACTIVE")) {
                                    raison = Pack.getString("Reason");
                                } else {
                                    raison = state;
                                }
                            } else {
                                if (!resultat.contentEquals("UNKNOWN")) {
                                    resultat = "ERREUR";
                                }
                                raison = desc;
                                // TRACE LOG
                                Random SurveillanceReferenceRandom = new Random();
                                int id_surveillance = SurveillanceReferenceRandom.nextInt();
                                if (id_surveillance > 0) {
                                    id_surveillance = id_surveillance * -1;
                                }
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String surveillanceDate = sdf.format(calendar.getTime());

                                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                                String surveillanceHeure = mdformat.format(calendar.getTime());

                                int produit_id = finalPhSerialisation.getProduitUID();
                                int serialisationID = finalPhSerialisation.get_UID();
                                String motif = GestionCodeErreurNMVO.getMessage(code);
                                String actionAMener = "";
                                String statut = "NON LU";
                                String traitePar = utilisateur.getIdentifiant();
                                String traiteDate = surveillanceDate;
                                String traiteHeure = surveillanceHeure;
                                String produitLot = finalPhSerialisation.getNumeroLot();
                                String produitDatePéremption = finalPhSerialisation.getDatePeremptionAAMMJJ();
                                String produitNumeroSerie = finalPhSerialisation.getNumeroSerie();

                                SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                            }

                            finalPhSerialisation.setStatut("Executer");
                            finalPhSerialisation.setResultat(resultat);
                            finalPhSerialisation.setRaison(raison);
                            finalPhSerialisation.setNMVSTrxId(NMVSTrxId);

                            long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, finalPhSerialisation);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(handler.obtainMessage());
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter SerialExpress!\nOpération de dispensation impossible.", "alerte");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", tokenLdap);
                        headers.put("Content-Type", "application/json;charset=utf-8");
                        return headers;
                    }
                };
                obreq.setRetryPolicy(retryPolicy);
                requestQueue.add(obreq);
                try {
                    Looper.loop();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void NMVS_G150_sampleSinglePack(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, int serialisationUID, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA) {
        final String tokenLdap = AuthentificationLDAP(context, db, utilisateur);
        if (!utilisateur.getToken().contentEquals("")) {
            PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, serialisationUID);
            if(phSerialisation == null)
                phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, serialisationUID);

            // Tentative de lancer la sychronisation
            if (haveNetworkConnection(context)) {
                String urlRequete = url + "G150";
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                JSONObject data = new JSONObject();
                try {
                    JSONObject header = new JSONObject();
                    header.put("ClientTrxId", phSerialisation.getRefClientTrxId());

                    JSONObject body = new JSONObject();
                    body.put("ProductCode_VALUE", ProductCode_VALUE_VA);
                    body.put("ProductCode_SHEME", ProductCode_SHEME_VA);
                    body.put("Batch_ID", Batch_ID_VA);
                    body.put("Batch_EXPDATE", Batch_EXPDATE_VA);
                    body.put("Pack_SN", Pack_SN_VA);

                    data.put("header", header);
                    data.put("body", body);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final PH_Serialisation finalPhSerialisation = phSerialisation;
                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, data, new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject Header = response.getJSONObject("Header");
                            JSONObject Transaction = Header.getJSONObject("Transaction");
                            String NMVSTrxId = Transaction.getString("NMVSTrxId");

                            JSONObject Body = response.getJSONObject("Body");
                            JSONObject ReturnCode = Body.getJSONObject("ReturnCode");
                            String code = ReturnCode.getString("code");
                            String desc = ReturnCode.getString("desc");

                            JSONObject Pack = Body.getJSONObject("Pack");
                            String state = Pack.getString("state");

                            String resultat = state;
                            String raison = "";

                            if (code.contentEquals("NMVS_SUCCESS")) {
                                if (state.contentEquals("INACTIVE")) {
                                    raison = Pack.getString("Reason");
                                } else {
                                    raison = state;
                                }
                            } else {
                                if (!resultat.contentEquals("UNKNOWN")) {
                                    resultat = "ERREUR";
                                }
                                raison = desc;

                                // TRACE LOG
                                Random SurveillanceReferenceRandom = new Random();
                                int id_surveillance = SurveillanceReferenceRandom.nextInt();
                                if (id_surveillance > 0) {
                                    id_surveillance = id_surveillance * -1;
                                }
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String surveillanceDate = sdf.format(calendar.getTime());

                                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                                String surveillanceHeure = mdformat.format(calendar.getTime());

                                int produit_id = finalPhSerialisation.getProduitUID();
                                int serialisationID = finalPhSerialisation.get_UID();
                                String motif = GestionCodeErreurNMVO.getMessage(code);
                                String actionAMener = "";
                                String statut = "NON LU";
                                String traitePar = utilisateur.getIdentifiant();
                                String traiteDate = surveillanceDate;
                                String traiteHeure = surveillanceHeure;
                                String produitLot = finalPhSerialisation.getNumeroLot();
                                String produitDatePéremption = finalPhSerialisation.getDatePeremptionAAMMJJ();
                                String produitNumeroSerie = finalPhSerialisation.getNumeroSerie();

                                SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                            }

                            finalPhSerialisation.setStatut("Executer");
                            finalPhSerialisation.setResultat(resultat);
                            finalPhSerialisation.setRaison(raison);
                            finalPhSerialisation.setNMVSTrxId(NMVSTrxId);

                            long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, finalPhSerialisation);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(handler.obtainMessage());
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter SerialExpress !\nOpération de dispensation impossible.", "alerte");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", tokenLdap);
                        headers.put("Content-Type", "application/json;charset=utf-8");
                        return headers;
                    }
                };
                obreq.setRetryPolicy(retryPolicy);
                requestQueue.add(obreq);
                try {
                    Looper.loop();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void NMVS_G130_destroySinglePack(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, int serialisationUID, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA) {
        final String tokenLdap = AuthentificationLDAP(context, db, utilisateur);
        if (!utilisateur.getToken().contentEquals("")) {
            PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, serialisationUID);
            if(phSerialisation == null)
                phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, serialisationUID);

            // Tentative de lancer la sychronisation
            if (haveNetworkConnection(context)) {
                String urlRequete = url + "G130";
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                JSONObject data = new JSONObject();
                try {
                    JSONObject header = new JSONObject();
                    header.put("ClientTrxId", phSerialisation.getRefClientTrxId());

                    JSONObject body = new JSONObject();
                    body.put("ProductCode_VALUE", ProductCode_VALUE_VA);
                    body.put("ProductCode_SHEME", ProductCode_SHEME_VA);
                    body.put("Batch_ID", Batch_ID_VA);
                    body.put("Batch_EXPDATE", Batch_EXPDATE_VA);
                    body.put("Pack_SN", Pack_SN_VA);

                    data.put("header", header);
                    data.put("body", body);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final PH_Serialisation finalPhSerialisation = phSerialisation;
                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, data, new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject Header = response.getJSONObject("Header");
                            JSONObject Transaction = Header.getJSONObject("Transaction");
                            String NMVSTrxId = Transaction.getString("NMVSTrxId");

                            JSONObject Body = response.getJSONObject("Body");
                            JSONObject ReturnCode = Body.getJSONObject("ReturnCode");
                            String code = ReturnCode.getString("code");
                            String desc = ReturnCode.getString("desc");

                            JSONObject Pack = Body.getJSONObject("Pack");
                            String state = Pack.getString("state");

                            String resultat = state;
                            String raison = "";

                            if (code.contentEquals("NMVS_SUCCESS")) {
                                if (state.contentEquals("INACTIVE")) {
                                    raison = Pack.getString("Reason");
                                } else {
                                    raison = state;
                                }
                            } else {
                                if (!resultat.contentEquals("UNKNOWN")) {
                                    resultat = "ERREUR";
                                }
                                raison = desc;

                                // TRACE LOG
                                Random SurveillanceReferenceRandom = new Random();
                                int id_surveillance = SurveillanceReferenceRandom.nextInt();
                                if (id_surveillance > 0) {
                                    id_surveillance = id_surveillance * -1;
                                }
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String surveillanceDate = sdf.format(calendar.getTime());

                                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                                String surveillanceHeure = mdformat.format(calendar.getTime());

                                int produit_id = finalPhSerialisation.getProduitUID();
                                int serialisationID = finalPhSerialisation.get_UID();
                                String motif = GestionCodeErreurNMVO.getMessage(code);
                                String actionAMener = "";
                                String statut = "NON LU";
                                String traitePar = utilisateur.getIdentifiant();
                                String traiteDate = surveillanceDate;
                                String traiteHeure = surveillanceHeure;
                                String produitLot = finalPhSerialisation.getNumeroLot();
                                String produitDatePéremption = finalPhSerialisation.getDatePeremptionAAMMJJ();
                                String produitNumeroSerie = finalPhSerialisation.getNumeroSerie();

                                SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                            }

                            finalPhSerialisation.setStatut("Executer");
                            finalPhSerialisation.setResultat(resultat);
                            finalPhSerialisation.setRaison(raison);
                            finalPhSerialisation.setNMVSTrxId(NMVSTrxId);

                            long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, finalPhSerialisation);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(handler.obtainMessage());
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter SerialExpress !\nOpération de destruction impossible.", "alerte");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", tokenLdap);
                        headers.put("Content-Type", "application/json;charset=utf-8");
                        return headers;
                    }
                };
                obreq.setRetryPolicy(retryPolicy);
                requestQueue.add(obreq);
                try {
                    Looper.loop();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void NMVS_G121_undoDispenseSinglePac(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, int serialisationUID, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA) {
        final String tokenLdap = AuthentificationLDAP(context, db, utilisateur);
        if (!utilisateur.getToken().contentEquals("")) {
            PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, serialisationUID);
            if(phSerialisation == null)
                phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, serialisationUID);

            // Tentative de lancer la sychronisation
            if (haveNetworkConnection(context)) {
                String urlRequete = url + "G121";
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                JSONObject data = new JSONObject();
                try {
                    JSONObject header = new JSONObject();
                    header.put("ClientTrxId", phSerialisation.getRefClientTrxId());

                    JSONObject body = new JSONObject();
                    body.put("ProductCode_VALUE", ProductCode_VALUE_VA);
                    body.put("ProductCode_SHEME", ProductCode_SHEME_VA);
                    body.put("Batch_ID", Batch_ID_VA);
                    body.put("Batch_EXPDATE", Batch_EXPDATE_VA);
                    body.put("Pack_SN", Pack_SN_VA);

                    data.put("header", header);
                    data.put("body", body);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final PH_Serialisation finalPhSerialisation = phSerialisation;
                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, data, new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject Header = response.getJSONObject("Header");
                            JSONObject Transaction = Header.getJSONObject("Transaction");
                            String NMVSTrxId = Transaction.getString("NMVSTrxId");

                            JSONObject Body = response.getJSONObject("Body");
                            JSONObject ReturnCode = Body.getJSONObject("ReturnCode");
                            String code = ReturnCode.getString("code");
                            String desc = ReturnCode.getString("desc");

                            JSONObject Pack = Body.getJSONObject("Pack");
                            String state = Pack.getString("state");

                            String resultat = state;
                            String raison = "";

                            if (code.contentEquals("NMVS_SUCCESS")) {
                                if (state.contentEquals("INACTIVE")) {
                                    raison = Pack.getString("Reason");
                                } else {
                                    raison = state;
                                }
                            } else {
                                if (!resultat.contentEquals("UNKNOWN")) {
                                    resultat = "ERREUR";
                                }
                                raison = desc;

                                // TRACE LOG
                                Random SurveillanceReferenceRandom = new Random();
                                int id_surveillance = SurveillanceReferenceRandom.nextInt();
                                if (id_surveillance > 0) {
                                    id_surveillance = id_surveillance * -1;
                                }
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String surveillanceDate = sdf.format(calendar.getTime());

                                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                                String surveillanceHeure = mdformat.format(calendar.getTime());

                                int produit_id = finalPhSerialisation.getProduitUID();
                                int serialisationID = finalPhSerialisation.get_UID();
                                String motif = GestionCodeErreurNMVO.getMessage(code);
                                String actionAMener = "";
                                String statut = "NON LU";
                                String traitePar = utilisateur.getIdentifiant();
                                String traiteDate = surveillanceDate;
                                String traiteHeure = surveillanceHeure;
                                String produitLot = finalPhSerialisation.getNumeroLot();
                                String produitDatePéremption = finalPhSerialisation.getDatePeremptionAAMMJJ();
                                String produitNumeroSerie = finalPhSerialisation.getNumeroSerie();

                                SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                            }

                            finalPhSerialisation.setStatut("Executer");
                            finalPhSerialisation.setResultat(resultat);
                            finalPhSerialisation.setRaison(raison);
                            finalPhSerialisation.setNMVSTrxId(NMVSTrxId);

                            long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, finalPhSerialisation);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(handler.obtainMessage());
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter SerialExpress!\nOpération de retour impossible.", "alerte");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", tokenLdap);
                        headers.put("Content-Type", "application/json;charset=utf-8");
                        return headers;
                    }
                };
                obreq.setRetryPolicy(retryPolicy);
                requestQueue.add(obreq);
                try {
                    Looper.loop();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void NMVS_G151_undoSampleSinglePack(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, int serialisationUID, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA) {
        final String tokenLdap = AuthentificationLDAP(context, db, utilisateur);
        if (!utilisateur.getToken().contentEquals("")) {
            PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, serialisationUID);
            if(phSerialisation == null)
                phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, serialisationUID);

            // Tentative de lancer la sychronisation
            if (haveNetworkConnection(context)) {
                String urlRequete = url + "G151";
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                JSONObject data = new JSONObject();
                try {
                    JSONObject header = new JSONObject();
                    header.put("ClientTrxId", phSerialisation.getRefClientTrxId());

                    JSONObject body = new JSONObject();
                    body.put("ProductCode_VALUE", ProductCode_VALUE_VA);
                    body.put("ProductCode_SHEME", ProductCode_SHEME_VA);
                    body.put("Batch_ID", Batch_ID_VA);
                    body.put("Batch_EXPDATE", Batch_EXPDATE_VA);
                    body.put("Pack_SN", Pack_SN_VA);

                    data.put("header", header);
                    data.put("body", body);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final PH_Serialisation finalPhSerialisation = phSerialisation;
                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, data, new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject Header = response.getJSONObject("Header");
                            JSONObject Transaction = Header.getJSONObject("Transaction");
                            String NMVSTrxId = Transaction.getString("NMVSTrxId");

                            JSONObject Body = response.getJSONObject("Body");
                            JSONObject ReturnCode = Body.getJSONObject("ReturnCode");
                            String code = ReturnCode.getString("code");
                            String desc = ReturnCode.getString("desc");

                            JSONObject Pack = Body.getJSONObject("Pack");
                            String state = Pack.getString("state");

                            String resultat = state;
                            String raison = "";

                            if (code.contentEquals("NMVS_SUCCESS")) {
                                if (state.contentEquals("INACTIVE")) {
                                    raison = Pack.getString("Reason");
                                } else {
                                    raison = state;
                                }
                            } else {
                                if (!resultat.contentEquals("UNKNOWN")) {
                                    resultat = "ERREUR";
                                }
                                raison = desc;

                                // TRACE LOG
                                Random SurveillanceReferenceRandom = new Random();
                                int id_surveillance = SurveillanceReferenceRandom.nextInt();
                                if (id_surveillance > 0) {
                                    id_surveillance = id_surveillance * -1;
                                }
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String surveillanceDate = sdf.format(calendar.getTime());

                                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                                String surveillanceHeure = mdformat.format(calendar.getTime());

                                int produit_id = finalPhSerialisation.getProduitUID();
                                int serialisationID = finalPhSerialisation.get_UID();
                                String motif = GestionCodeErreurNMVO.getMessage(code);
                                String actionAMener = "";
                                String statut = "NON LU";
                                String traitePar = utilisateur.getIdentifiant();
                                String traiteDate = surveillanceDate;
                                String traiteHeure = surveillanceHeure;
                                String produitLot = finalPhSerialisation.getNumeroLot();
                                String produitDatePéremption = finalPhSerialisation.getDatePeremptionAAMMJJ();
                                String produitNumeroSerie = finalPhSerialisation.getNumeroSerie();

                                SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                            }

                            finalPhSerialisation.setStatut("Executer");
                            finalPhSerialisation.setResultat(resultat);
                            finalPhSerialisation.setRaison(raison);
                            finalPhSerialisation.setNMVSTrxId(NMVSTrxId);

                            long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, finalPhSerialisation);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(handler.obtainMessage());
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter SerialExpress!\nOpération de retour impossible.", "alerte");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", tokenLdap);
                        headers.put("Content-Type", "application/json;charset=utf-8");
                        return headers;
                    }
                };
                obreq.setRetryPolicy(retryPolicy);
                requestQueue.add(obreq);
                try {
                    Looper.loop();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String AuthentificationLDAP(final Context context, final SQLiteDatabase db, Utilisateur utilisateurconnecte) {
        final String[] token_LDAP = {""};

            // Tentative de lancer la sychronisation
            if (haveNetworkConnection(context)) {
                String urlRequete = "https://phir4.alcyons.fr/api/ldap/authentification";
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                JSONObject data = new JSONObject();

                Parametres_Serialisation parametresSerialisation = Parametres_SerialisationOpenHelper.getParametres_Serialisation(db);
                String etablissementUtilisateur = utilisateurconnecte.getEtablissement();

                if(parametresSerialisation != null)
                {
                    String mdp_md5 = OutilsEncodage.recupererHashageMD5(parametresSerialisation.getFranceMVO_mdp());
                    try {
                        JSONObject body = new JSONObject();
                        body.put("user", "FranceMVO");
                        body.put("password", mdp_md5);
                        body.put("compagny", etablissementUtilisateur);
                        body.put("softwareName", "PihR4");
                        body.put("softwareSupplier", "Alcyons");
                        body.put("softwareVersion", "1812");

                        data.put("body", body);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        JSONObject body = new JSONObject();
                        body.put("user", "");
                        body.put("password", "");
                        body.put("compagny", "");
                        body.put("softwareName", "");
                        body.put("softwareSupplier", "");
                        body.put("softwareVersion", "");

                        data.put("body", body);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, data, new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject Body = response.getJSONObject("Body");
                            JSONObject ReturnCode = Body.getJSONObject("ReturnCode");
                            String code = ReturnCode.getString("code");
                            if(code.equals("API_SUCCESS"))
                            {
                                token_LDAP[0] = Body.getString("token");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(handler.obtainMessage());
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter SerialExpress!\nOpération de retour impossible.", "alerte");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json;charset=utf-8");
                        return headers;
                    }
                };
                obreq.setRetryPolicy(retryPolicy);
                requestQueue.add(obreq);
                try {
                    Looper.loop();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }

        return token_LDAP[0];
    }

    private static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static CompletableFuture<Boolean> serialisationVerificationSingle(Context context, SQLiteDatabase db, Utilisateur utilisateur, int serialisationUID, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA) {

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        final String tokenLdap = AuthentificationLDAP(context, db, utilisateur);
        PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, serialisationUID);
        if (phSerialisation == null)
            phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, serialisationUID);

        String urlRequete = url + "G110";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject data = new JSONObject();
        try {
            JSONObject header = new JSONObject();
            header.put("ClientTrxId", phSerialisation.getRefClientTrxId());

            JSONObject body = new JSONObject();
            body.put("ProductCode_VALUE", ProductCode_VALUE_VA);
            body.put("ProductCode_SHEME", ProductCode_SHEME_VA);
            body.put("Batch_ID", Batch_ID_VA);
            body.put("Batch_EXPDATE", Batch_EXPDATE_VA);
            body.put("Pack_SN", Pack_SN_VA);

            data.put("header", header);
            data.put("body", body);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final PH_Serialisation finalPhSerialisation = phSerialisation;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, urlRequete, data, new Response.Listener<JSONObject>() {

            // Takes the response from the JSON request
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject Header = response.getJSONObject("Header");
                    JSONObject Transaction = Header.getJSONObject("Transaction");
                    String NMVSTrxId = Transaction.getString("NMVSTrxId");

                    JSONObject Body = response.getJSONObject("Body");
                    JSONObject ReturnCode = Body.getJSONObject("ReturnCode");
                    String code = ReturnCode.getString("code");
                    String desc = ReturnCode.getString("desc");

                    JSONObject Pack = Body.getJSONObject("Pack");
                    String state = Pack.getString("state");

                    String resultat = state;
                    String raison = "";

                    if (code.contentEquals("NMVS_SUCCESS")) {
                        if (state.contentEquals("INACTIVE")) {
                            raison = Pack.getString("Reason");
                        } else {
                            raison = state;
                        }
                    } else {
                        if (!resultat.contentEquals("UNKNOWN")) {
                            resultat = "ERREUR";
                        }
                        raison = desc;


                        // TRACE LOG
                        Random SurveillanceReferenceRandom = new Random();
                        int id_surveillance = SurveillanceReferenceRandom.nextInt();
                        if (id_surveillance > 0) {
                            id_surveillance = id_surveillance * -1;
                        }
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String surveillanceDate = sdf.format(calendar.getTime());

                        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                        String surveillanceHeure = mdformat.format(calendar.getTime());

                        int produit_id = finalPhSerialisation.getProduitUID();
                        int serialisationID = finalPhSerialisation.get_UID();
                        String motif = GestionCodeErreurNMVO.getMessage(code);
                        String actionAMener = "";
                        String statut = "NON LU";
                        String traitePar = utilisateur.getIdentifiant();
                        String traiteDate = surveillanceDate;
                        String traiteHeure = surveillanceHeure;
                        String produitLot = finalPhSerialisation.getNumeroLot();
                        String produitDatePéremption = finalPhSerialisation.getDatePeremptionAAMMJJ();
                        String produitNumeroSerie = finalPhSerialisation.getNumeroSerie();

                        SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);
                    }

                    finalPhSerialisation.setStatut("Executer");
                    finalPhSerialisation.setResultat(resultat);
                    finalPhSerialisation.setRaison(raison);
                    finalPhSerialisation.setNMVSTrxId(NMVSTrxId);

                    long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, finalPhSerialisation);

                    boolean ok = code.contentEquals("NMVS_SUCCESS");
                    future.complete(ok);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //handler.sendMessage(handler.obtainMessage());
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        future.complete(false);
                        Log.e("Volley", "Error");
                        //Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter Alcyons !\nOpération de vérification impossible.", "alerte");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", tokenLdap);
                headers.put("Content-Type", "application/json;charset=utf-8");
                return headers;
            }
        };

        Volley.newRequestQueue(context.getApplicationContext()).add(req);
        return future;
    }

    public static CompletableFuture<Boolean> serialisationDispenserSingle(Context context, SQLiteDatabase db, Utilisateur utilisateur, int serialisationUID, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA) {

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        final String tokenLdap = AuthentificationLDAP(context, db, utilisateur);
        PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, serialisationUID);
        if (phSerialisation == null)
            phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, serialisationUID);

        String urlRequete = url + "G120";

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject data = new JSONObject();
        try {
            JSONObject header = new JSONObject();
            header.put("ClientTrxId", phSerialisation.getRefClientTrxId());

            JSONObject body = new JSONObject();
            body.put("ProductCode_VALUE", ProductCode_VALUE_VA);
            body.put("ProductCode_SHEME", ProductCode_SHEME_VA);
            body.put("Batch_ID", Batch_ID_VA);
            body.put("Batch_EXPDATE", Batch_EXPDATE_VA);
            body.put("Pack_SN", Pack_SN_VA);

            data.put("header", header);
            data.put("body", body);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final PH_Serialisation finalPhSerialisation = phSerialisation;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, urlRequete, data, new Response.Listener<JSONObject>() {

            // Takes the response from the JSON request
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject Header = response.getJSONObject("Header");
                    JSONObject Transaction = Header.getJSONObject("Transaction");
                    String NMVSTrxId = Transaction.getString("NMVSTrxId");

                    JSONObject Body = response.getJSONObject("Body");
                    JSONObject ReturnCode = Body.getJSONObject("ReturnCode");
                    String code = ReturnCode.getString("code");
                    String desc = ReturnCode.getString("desc");

                    JSONObject Pack = Body.getJSONObject("Pack");
                    String state = Pack.getString("state");

                    String resultat = state;
                    String raison = "";

                    if (code.contentEquals("NMVS_SUCCESS")) {
                        if (state.contentEquals("INACTIVE")) {
                            raison = Pack.getString("Reason");
                        } else {
                            raison = state;
                        }
                    } else {
                        if (!resultat.contentEquals("UNKNOWN")) {
                            resultat = "ERREUR";
                        }
                        raison = desc;
                        // TRACE LOG
                        Random SurveillanceReferenceRandom = new Random();
                        int id_surveillance = SurveillanceReferenceRandom.nextInt();
                        if (id_surveillance > 0) {
                            id_surveillance = id_surveillance * -1;
                        }
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String surveillanceDate = sdf.format(calendar.getTime());

                        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                        String surveillanceHeure = mdformat.format(calendar.getTime());

                        int produit_id = finalPhSerialisation.getProduitUID();
                        int serialisationID = finalPhSerialisation.get_UID();
                        String motif = GestionCodeErreurNMVO.getMessage(code);
                        String actionAMener = "";
                        String statut = "NON LU";
                        String traitePar = utilisateur.getIdentifiant();
                        String traiteDate = surveillanceDate;
                        String traiteHeure = surveillanceHeure;
                        String produitLot = finalPhSerialisation.getNumeroLot();
                        String produitDatePéremption = finalPhSerialisation.getDatePeremptionAAMMJJ();
                        String produitNumeroSerie = finalPhSerialisation.getNumeroSerie();

                        SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                    }

                    finalPhSerialisation.setStatut("Executer");
                    finalPhSerialisation.setResultat(resultat);
                    finalPhSerialisation.setRaison(raison);
                    finalPhSerialisation.setNMVSTrxId(NMVSTrxId);

                    long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, finalPhSerialisation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //handler.sendMessage(handler.obtainMessage());
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter SerialExpress!\nOpération de dispensation impossible.", "alerte");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", tokenLdap);
                headers.put("Content-Type", "application/json;charset=utf-8");
                return headers;
            }
        };

        Volley.newRequestQueue(context.getApplicationContext()).add(req);
        return future;
    }
}
