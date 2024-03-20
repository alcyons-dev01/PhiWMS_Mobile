package fr.alcyons.phimr4.OutilsSerialisation;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.HashMap;
import java.util.Map;

import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;

/**
 * Created by olivier on 26/02/2019.
 */

public class WS_PKI {

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
    private static String url = "https://phir4.alcyons.fr/api/v5/WS_PKI/";

    public static void NMVS_G615_downloadClientCertifi(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, String TAN) {

        if (!TAN.isEmpty()) {

            if (!utilisateur.getToken().contentEquals("")) {

/*
                UserRules userRules = UserRulesOpenHelper.getUserRulesByUser(db, utilisateur.getId());
                String ClientTrxId = userRules.getSerialisation_identifiant().replace("/", "-") + "-G615-" + AGL.AGL_AAMMJJ(Calendar.getInstance().getTime()) + AGL.AGL_HHMMSS(Calendar.getInstance().getTime());
*/

                String ClientTrxId = "";

                // Tentative de lancer la sychronisation
                if (OutilsGestionConnexionReseau.isServerAccessible(context)) {
                    String urlRequete = url + "G615";
                    RequestQueue requestQueue = Volley.newRequestQueue(context);

                    JSONObject data = new JSONObject();
                    try {
                        JSONObject header = new JSONObject();
                        header.put("ClientTrxId", ClientTrxId);

                        JSONObject body = new JSONObject();
                        body.put("TAN", TAN);

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

                                if (code.contentEquals("NMVS_SUCCESS")) {
                                    //Mot de passe modifier
                                } else {
                                    //Erreur
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
                                    Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP G615", "alerte");
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

}
