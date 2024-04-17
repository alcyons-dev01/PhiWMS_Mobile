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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Parametres_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Parametres_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.SurveillanceReference;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.AGL;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.GestionCodeErreurNMVO;

/**
 * Created by olivier on 26/02/2019.
 */

public class WS_MIXED_BULK {

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
    private static String url = "https://phir4.alcyons.fr/api/v5/WS_MIXED_BULK/";

    public static void NMVS_G195_submitMixedSinglePack(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, List<Integer> uidListe) {

        if (uidListe.size() > 0) {
            if (utilisateur.getToken() != "") {
                JSONArray ARRAY_TrxItem = new JSONArray();

                for (Integer UID_Courant : uidListe) {
                    PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, UID_Courant);
                    if(phSerialisation == null)
                    {
                        phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, UID_Courant);
                    }
                    JSONArray trxItem = new JSONArray();
                    trxItem.put(phSerialisation.getProduitCodeValue());
                    trxItem.put(phSerialisation.getProduitCodeSheme());
                    trxItem.put(phSerialisation.getNumeroLot());
                    trxItem.put(phSerialisation.getDatePeremptionAAMMJJ());
                    trxItem.put(phSerialisation.getNumeroSerie());
                    trxItem.put(phSerialisation.getRefClientTrxId());
                    trxItem.put(phSerialisation.getReqType());
                    ARRAY_TrxItem.put(trxItem);
                }

                Parametres_Serialisation parametresSerialisation = Parametres_SerialisationOpenHelper.getParametres_Serialisation(db);
                String ClientTrxId = parametresSerialisation.getFranceMVO_identifiant().replace("/", "-") + "-G195-" + AGL.AGL_AAMMJJ(Calendar.getInstance().getTime()) + AGL.AGL_HHMMSS(Calendar.getInstance().getTime());


                // Tentative de lancer la sychronisation
                if (haveNetworkConnection(context)) {
                    String urlRequete = url + "G195";
                    RequestQueue requestQueue = Volley.newRequestQueue(context);

                    JSONObject data = new JSONObject();
                    try {
                        JSONObject header = new JSONObject();
                        header.put("ClientTrxId", ClientTrxId);

                        JSONObject body = new JSONObject();
                        body.put("ARRAY_TrxItem", ARRAY_TrxItem);

                        data.put("header", header);
                        data.put("body", body);

                    } catch (JSONException e) {
                        e.printStackTrace();
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
                                String desc = ReturnCode.getString("desc");

                                String message = "";

                                if (code.contentEquals("NMVS_SUCCESS")) {
                                    JSONObject Header = response.getJSONObject("Header");
                                    JSONObject Transaction = Header.getJSONObject("Transaction");
                                    String NMVSTrxId = Transaction.getString("NMVSTrxId");

                                    NMVS_G196_requestBulkMixedSingl(context, db, utilisateur, NMVSTrxId);
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
                                    Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP G195", "alerte");
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", utilisateur.getToken());
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
    }

    public static void NMVS_G196_requestBulkMixedSingl(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, final String NMVSTrxId) {

        if (utilisateur.getToken() != "") {

            Parametres_Serialisation parametresSerialisation = Parametres_SerialisationOpenHelper.getParametres_Serialisation(db);
            String ClientTrxId = parametresSerialisation.getFranceMVO_identifiant().replace("/", "-") + "-G195-" + AGL.AGL_AAMMJJ(Calendar.getInstance().getTime()) + AGL.AGL_HHMMSS(Calendar.getInstance().getTime());

            // Tentative de lancer la sychronisation
            if (haveNetworkConnection(context)) {
                String urlRequete = url + "G196";
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                JSONObject data = new JSONObject();
                try {
                    JSONObject header = new JSONObject();
                    header.put("ClientTrxId", ClientTrxId);

                    JSONObject body = new JSONObject();
                    body.put("RefClientTrxId", NMVSTrxId);

                    data.put("header", header);
                    data.put("body", body);

                } catch (JSONException e) {
                    e.printStackTrace();
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

                            if (code.contentEquals("NMVS_SUCCESS")) {
                                JSONObject TrxList = Body.getJSONObject("TrxList");
                                //JSONObject TrxItem = TrxList.getJSONObject("TrxItem");
                                Object next = TrxList.get("TrxItem");

                                if(next instanceof JSONArray)
                                {
                                    JSONArray TrxItemArray = TrxList.getJSONArray("TrxItem");
                                    for (int i = 0; i < TrxItemArray.length(); i++) {
                                        JSONObject TrxItem = TrxItemArray.getJSONObject(i);

                                        //PACK
                                        JSONObject Pack = TrxItem.getJSONObject("Pack");
                                        String sn = Pack.getString("sn");
                                        String state = Pack.getString("state");
                                        ReturnCode = Pack.getJSONObject("ReturnCode");
                                        code = ReturnCode.getString("code");
                                        String desc = ReturnCode.getString("desc");

                                        String resultat = state;
                                        String raison;
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
                                        }

                                        //PRODUCT
                                        JSONObject Product = TrxItem.getJSONObject("Product");
                                        JSONObject Batch = Product.getJSONObject("Batch");
                                        String ExpDate = Batch.getString("ExpDate");
                                        String Id = Batch.getString("Id");
                                        JSONObject ProductCode = Product.getJSONObject("ProductCode");
                                        String productValue = ProductCode.getString("_");
                                        String productScheme = ProductCode.getString("scheme");

                                        //REQTYPE
                                        String reqType = TrxItem.getString("reqType");

                                        List<PH_Serialisation> ph_serialisationList = PH_SerialisationOpenHelper.getListePH_SerialisationG196(db, sn, productScheme, productValue, reqType);

                                        for (PH_Serialisation phSerialisation : ph_serialisationList) {

                                            if(resultat.contentEquals("UNKNOWN") || resultat.contentEquals("ERREUR"))
                                            {
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

                                                int produit_id = phSerialisation.getProduitUID();
                                                int serialisationID = phSerialisation.get_UID();
                                                String motif = GestionCodeErreurNMVO.getMessage(code);
                                                String actionAMener = "";
                                                String statut = "NON LU";
                                                String traitePar = utilisateur.getIdentifiant();
                                                String traiteDate = surveillanceDate;
                                                String traiteHeure = surveillanceHeure;
                                                String produitLot = phSerialisation.getNumeroLot();
                                                String produitDatePéremption = phSerialisation.getDatePeremptionAAMMJJ();
                                                String produitNumeroSerie = phSerialisation.getNumeroSerie();

                                                SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);
                                            }

                                            phSerialisation.setStatut("Executer");
                                            phSerialisation.setResultat(resultat);
                                            phSerialisation.setRaison(raison);
                                            phSerialisation.setNMVSTrxId(NMVSTrxId);

                                            long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, phSerialisation);
                                        }
                                    }
                                }
                                else
                                {
                                    JSONObject TrxItem = TrxList.getJSONObject("TrxItem");
                                    //PACK
                                    JSONObject Pack = TrxItem.getJSONObject("Pack");
                                    String sn = Pack.getString("sn");
                                    String state = Pack.getString("state");
                                    ReturnCode = Pack.getJSONObject("ReturnCode");
                                    code = ReturnCode.getString("code");
                                    String desc = ReturnCode.getString("desc");

                                    String resultat = state;
                                    String raison;
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
                                    }

                                    //PRODUCT
                                    JSONObject Product = TrxItem.getJSONObject("Product");
                                    JSONObject Batch = Product.getJSONObject("Batch");
                                    String ExpDate = Batch.getString("ExpDate");
                                    String Id = Batch.getString("Id");
                                    JSONObject ProductCode = Product.getJSONObject("ProductCode");
                                    String productValue = ProductCode.getString("_");
                                    String productScheme = ProductCode.getString("scheme");

                                    //REQTYPE
                                    String reqType = TrxItem.getString("reqType");

                                    List<PH_Serialisation> ph_serialisationList = PH_SerialisationOpenHelper.getListePH_SerialisationG196(db, sn, productScheme, productValue, reqType);

                                    for (PH_Serialisation phSerialisation : ph_serialisationList) {

                                        if (resultat.contentEquals("UNKNOWN") || resultat.contentEquals("ERREUR")) {
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

                                            int produit_id = phSerialisation.getProduitUID();
                                            int serialisationID = phSerialisation.get_UID();
                                            String motif = GestionCodeErreurNMVO.getMessage(code);
                                            String actionAMener = "";
                                            String statut = "NON LU";
                                            String traitePar = utilisateur.getIdentifiant();
                                            String traiteDate = surveillanceDate;
                                            String traiteHeure = surveillanceHeure;
                                            String produitLot = phSerialisation.getNumeroLot();
                                            String produitDatePéremption = phSerialisation.getDatePeremptionAAMMJJ();
                                            String produitNumeroSerie = phSerialisation.getNumeroSerie();

                                            SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);
                                        }

                                        phSerialisation.setStatut("Executer");
                                        phSerialisation.setResultat(resultat);
                                        phSerialisation.setRaison(raison);
                                        phSerialisation.setNMVSTrxId(NMVSTrxId);

                                        long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, phSerialisation);
                                    }
                                }
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
                                Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP G196", "alerte");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", utilisateur.getToken());
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


}
