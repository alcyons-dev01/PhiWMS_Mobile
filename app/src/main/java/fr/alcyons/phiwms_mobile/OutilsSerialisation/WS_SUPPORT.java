package fr.alcyons.phiwms_mobile.OutilsSerialisation;

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

import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;

public class WS_SUPPORT extends MainActivity {

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
    private static String url = "https://phir4.alcyons.fr/api/v5/WS_SUPPORT/";

    public static void NMVS_G445_changePassword(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, String motDePasseActuel, String motDePasseNouveau) {

        if (!motDePasseActuel.isEmpty() && !motDePasseNouveau.isEmpty()) {

            if (!utilisateur.getToken().contentEquals("")) {

/*
                UserRules userRules = UserRulesOpenHelper.getUserRulesByUser(db, utilisateur.getId());
                String ClientTrxId = userRules.getSerialisation_identifiant().replace("/", "-") + "-G445-" + AGL.AGL_AAMMJJ(Calendar.getInstance().getTime()) + AGL.AGL_HHMMSS(Calendar.getInstance().getTime());
*/

                String ClientTrxId = "";

                // Tentative de lancer la sychronisation
                if (statutConnexion) {
                    String urlRequete = url + "G445";
                    RequestQueue requestQueue = Volley.newRequestQueue(context);

                    JSONObject data = new JSONObject();
                    try {
                        JSONObject header = new JSONObject();
                        header.put("ClientTrxId", ClientTrxId);

                        JSONObject body = new JSONObject();
                        body.put("PASSWORD", motDePasseActuel);
                        body.put("NEWPASSWORD", motDePasseNouveau);

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
                                    Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP G445", "alerte");
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

    public static void NMVS_G482_loadTermsAndCondition(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur) {

        if (!utilisateur.getToken().contentEquals("")) {

/*
            UserRules userRules = UserRulesOpenHelper.getUserRulesByUser(db, utilisateur.getId());
            String ClientTrxId = userRules.getSerialisation_identifiant().replace("/", "-") + "-G482-" + AGL.AGL_AAMMJJ(Calendar.getInstance().getTime()) + AGL.AGL_HHMMSS(Calendar.getInstance().getTime());
*/
            String ClientTrxId = "";

            // Tentative de lancer la sychronisation
            if (statutConnexion) {
                String urlRequete = url + "G482";
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                JSONObject data = new JSONObject();
                try {
                    JSONObject header = new JSONObject();
                    header.put("ClientTrxId", ClientTrxId);
                    data.put("header", header);

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
                                JSONObject TermsAndConditions = Body.getJSONObject("TermsAndConditions");
                                String Text = TermsAndConditions.getString("Text");
                                String Accepted = TermsAndConditions.getString("Accepted");
                                String Version = TermsAndConditions.getString("Version");
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
                                Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP G482", "alerte");
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

    public static void NMVS_G483_confirmTermsAndCondit(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur, String version) {


        if (!utilisateur.getToken().contentEquals("")) {

/*
            UserRules userRules = UserRulesOpenHelper.getUserRulesByUser(db, utilisateur.getId());
            String ClientTrxId = userRules.getSerialisation_identifiant().replace("/", "-") + "-G483-" + AGL.AGL_AAMMJJ(Calendar.getInstance().getTime()) + AGL.AGL_HHMMSS(Calendar.getInstance().getTime());
*/
            String ClientTrxId = "";

            // Tentative de lancer la sychronisation
            if (statutConnexion) {
                String urlRequete = url + "G483";
                RequestQueue requestQueue = Volley.newRequestQueue(context);

                JSONObject data = new JSONObject();
                try {
                    JSONObject header = new JSONObject();
                    header.put("ClientTrxId", ClientTrxId);

                    JSONObject body = new JSONObject();
                    body.put("version", version);

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
                                //Termes et conditions acceptés
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
                                Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP G483", "alerte");
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
