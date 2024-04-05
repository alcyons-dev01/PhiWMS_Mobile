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

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Parametres_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Parametres_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.SurveillanceReference;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.AGL;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.GestionCodeErreurNMVO;

/**
 * Created by olivier on 26/02/2019.
 */

public class WS_BULK {

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
    private static String url = "https://phir4.alcyons.fr/api/v5/WS_BULK/";

    public static String NMVS_G115_requestBulkVerifiing(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, String GTIN, String Sheme, String Batch_ID, String ExpDate, JSONArray numero_serie) {
        url = "https://phir4.alcyons.fr/api/v3/WS_BULK/";

        //Changement pour les test en v2
        //url = "https://phir4.alcyons.fr/api/v2/WS_BULK/";
        final String[] reponse = {""};
        if (numero_serie.length() > 0) {
            if (utilisateur.getToken() != "") {
                JSONArray ARRAY_TrxItem = new JSONArray();
                if(GTIN.length() != 14)
                    GTIN = GTIN.substring(2);

                Parametres_Serialisation parametresSerialisation = Parametres_SerialisationOpenHelper.getParametres_Serialisation(db);
                String ClientTrxId = parametresSerialisation.getFranceMVO_identifiant().replace("/", "-") + "-G195-" + AGL.AGL_AAMMJJ(Calendar.getInstance().getTime()) + AGL.AGL_HHMMSS(Calendar.getInstance().getTime());

                // Tentative de lancer la sychronisation
                if (haveNetworkConnection(context)) {
                    String urlRequete = url + "G115";
                    RequestQueue requestQueue = Volley.newRequestQueue(context);

                    JSONObject data = new JSONObject();
                    try {
                        JSONObject header = new JSONObject();
                        header.put("ClientTrxId", ClientTrxId);

                        JSONObject body = new JSONObject();
                        body.put("ProductCode_VALUE", GTIN);
                        body.put("ProductCode_SHEME", Sheme);
                        body.put("Batch_ID", Batch_ID);
                        body.put("Batch_EXPDATE", ExpDate);
                        body.put("ARRAY_PackSN", numero_serie);

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

                                    reponse[0] = NMVS_G188_requestBulkTransactio(context, db, utilisateur, NMVSTrxId);
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
                                    Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP G115", "alerte");
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
        while(reponse[0].contentEquals(""))
        {

        }

        return reponse[0];
    }

    public static String NMVS_G188_requestBulkTransactio(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, final String NMVSTrxId) {
        url = "https://phir4.alcyons.fr/api/v3/WS_BULK/";
        //url = "https://phir4.alcyons.fr/api/v2/WS_BULK/";

        final String[] reponse = {""};
        if (utilisateur.getToken() != "") {

            Parametres_Serialisation parametresSerialisation = Parametres_SerialisationOpenHelper.getParametres_Serialisation(db);
            String ClientTrxId = parametresSerialisation.getFranceMVO_identifiant().replace("/", "-") + "-G195-" + AGL.AGL_AAMMJJ(Calendar.getInstance().getTime()) + AGL.AGL_HHMMSS(Calendar.getInstance().getTime());

            // Tentative de lancer la sychronisation
            if (haveNetworkConnection(context)) {
                String urlRequete = url + "G188";
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
                            JSONObject Product = Body.getJSONObject("Product");
                            JSONObject Product_Code = Product.getJSONObject("ProductCode");
                            String GTIN_Courant = Product_Code.getString("_");
                            String Scheme_Courant = Product_Code.getString("scheme");

                            JSONObject Batch = Product.getJSONObject("Batch");
                            String Batch_Id = Batch.getString("Id");
                            String Batch_ExpDate = Batch.getString("ExpDate");

                            JSONObject Packs = Body.getJSONObject("Packs");
                            JSONArray Pack_Array = Packs.getJSONArray("Pack");

                            for(int compteur = 0; compteur< Pack_Array.length(); compteur++)
                            {
                                JSONObject Pack_Courant = Pack_Array.getJSONObject(compteur);
                                String Numero_de_serie = Pack_Courant.getString("sn");
                                String State = Pack_Courant.getString("state");

                                JSONObject ReturnCode = Pack_Courant.getJSONObject("ReturnCode");
                                String code = ReturnCode.getString("code");
                                String desc = ReturnCode.getString("desc");

                                String resultat = State;
                                String raison = "";

                                if(code.contentEquals("NMVS_SUCCESS"))
                                {
                                    if(State.contentEquals("INACTIVE"))
                                    {
                                        raison = Pack_Courant.getString("Reason");
                                    }
                                    else
                                    {
                                        raison = State;
                                    }
                                }
                                else
                                {
                                    if(!resultat.contentEquals("UNKNOWN"))
                                    {
                                        resultat = "ERREUR";
                                    }

                                    raison = desc;
                                    PH_Serialisation ph_serialisation = PH_SerialisationOpenHelper.getPH_SerialisationByMultiple(db, GTIN_Courant, Scheme_Courant, Batch_Id, Batch_ExpDate, Numero_de_serie);
                                    Produit produit_courant = ProduitOpenHelper.getProduitByID(db, ph_serialisation.getProduitUID());

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

                                    int produit_id = ph_serialisation.getProduitUID();
                                    int serialisationID = ph_serialisation.get_UID();
                                    String motif = GestionCodeErreurNMVO.getMessage(code);
                                    String actionAMener = "";
                                    String statut = "NON LU";
                                    String traitePar = utilisateur.getIdentifiant();
                                    String traiteDate = surveillanceDate;
                                    String traiteHeure = surveillanceHeure;
                                    String produitLot = ph_serialisation.getNumeroLot();
                                    String produitDatePéremption = ph_serialisation.getDatePeremptionAAMMJJ();
                                    String produitNumeroSerie = ph_serialisation.getNumeroSerie();

                                    SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                                    long rowUID_surveillance = SurveillanceReferenceOpenHelper.insererSurveillanceReferenceEnBDD(db, new_surveillance_reference);

                                    if (rowUID_surveillance != -1) {
                                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, SurveillanceReferenceOpenHelper.Constantes.TABLE_SURVEILLANCEREFERENCE, new_surveillance_reference.getSerialexpressUUID(), new_surveillance_reference.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                                        try {
                                            EnvoyerMailSurveillance class_mail = new EnvoyerMailSurveillance();
                                            class_mail.EnvoyerMailSerialisation(new_surveillance_reference.get_UID(), utilisateur.getMail(), db);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByMultiple(db, GTIN_Courant, Scheme_Courant, Batch_Id, Batch_ExpDate, Numero_de_serie);
                                phSerialisation.setStatut("Executer");
                                phSerialisation.setResultat(resultat);
                                phSerialisation.setRaison(raison);
                                phSerialisation.setNMVSTrxId(NMVSTrxId);

                                long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, phSerialisation);
                            }

                            reponse[0] = "Ok";

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

        while(reponse[0].contentEquals(""))
        {

        }
        return  reponse[0];
    }

    public static String NMVS_G195_submitMixedSinglePack(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, List<Integer> uidListe) {
        url = "https://phir4.alcyons.fr/api/v3/WS_MIXED_BULK/";

        //Changement pour les test avec la v2
        //url = "https://phir4.alcyons.fr/api/v2/WS_MIXED_BULK/";

        final String[] reponse = {""};
        if (uidListe.size() > 0) {
            if (utilisateur.getToken() != "") {
                JSONArray ARRAY_TrxItem = new JSONArray();

                for (Integer UID_Courant : uidListe) {
                    PH_Serialisation phSerialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, UID_Courant);
                    String produit_code_value = phSerialisation.getProduitCodeValue();
                    produit_code_value = produit_code_value.substring(2);
                    JSONArray trxItem = new JSONArray();
                    trxItem.put(produit_code_value);
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

                                    reponse[0] = NMVS_G196_requestBulkMixedSingleBulk(context, db, utilisateur, NMVSTrxId);
                                    while(reponse[0].contentEquals("Erreur"))
                                    {
                                        reponse[0] = NMVS_G196_requestBulkMixedSingleBulk(context, db, utilisateur, NMVSTrxId);
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

        while(reponse[0].contentEquals(""))
        {

        }
        return reponse[0];
    }

    public static String NMVS_G196_requestBulkMixedSingleBulk(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, final String NMVSTrxId) {
        url = "https://phir4.alcyons.fr/api/v3/WS_MIXED_BULK/";

        //Changement pour les test avec la v2
        //url = "https://phir4.alcyons.fr/api/v2/WS_MIXED_BULK/";
        final String[] reponse = {""};
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

                            if(code.contentEquals("NMVS_FE_TX_02")) {
                                reponse[0] = "Erreur";
                            }
                            else if (code.contentEquals("NMVS_SUCCESS")) {
                                JSONObject TrxList = Body.getJSONObject("TrxList");
                                //JSONObject TrxItem = TrxList.getJSONObject("TrxItem");

                                if(TrxList.length() > 1)
                                {

                                }
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

                                            long rowUID_surveillance = SurveillanceReferenceOpenHelper.insererSurveillanceReferenceEnBDD(db, new_surveillance_reference);

                                            if (rowUID_surveillance != -1) {
                                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, SurveillanceReferenceOpenHelper.Constantes.TABLE_SURVEILLANCEREFERENCE, new_surveillance_reference.getSerialexpressUUID(), new_surveillance_reference.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                                                try {
                                                    EnvoyerMailSurveillance class_mail = new EnvoyerMailSurveillance();
                                                    class_mail.EnvoyerMailSerialisation(new_surveillance_reference.get_UID(), utilisateur.getMail(), db);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        phSerialisation.setStatut("Executer");
                                        phSerialisation.setResultat(resultat);
                                        phSerialisation.setRaison(raison);
                                        phSerialisation.setNMVSTrxId(NMVSTrxId);

                                        long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, phSerialisation);
                                    }
                                }
                                reponse[0] = "Ok";
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

        while(reponse[0].contentEquals(""))
        {

        }

        return reponse[0];
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
