package fr.alcyons.phiwms_mobile.Outils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import android.Manifest;
import fr.alcyons.phiwms_mobile.OriginalActivity;

/**
 * Created by quentinlanusse on 20/04/2017.
 */


public class OutilsGestionConnexionReseau {

    public static boolean reponseRecue = false;
    public static boolean requeteTerminee = false;

    /**
     * Not Thread safe. Blocking thread. Returns true if it
     * can connect to URL, false and exception is logged.
     */

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getNetworkClass(Context context) {
        boolean permission = false;
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            permission = true;
        }

        if(permission)
        {
            TelephonyManager mTelephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            int networkType = mTelephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "CDMA";
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "TYPE_1xRTT";
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "IDEN";
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "EVDO_0";
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "EVDO_A";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "HSPA";
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return "EVDO_B";
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return "EHRPD";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "Wifi";
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4G";
                default:
                    return "Unknown";
            }
        }
        else
        {
            return "Unknown";
        }
    }

    public static boolean isServerAccessible(Context context) {

        reponseRecue = false;

        if (!getNetworkClass(context).contentEquals("")) {

            SQLiteDatabase db = null;
            try {
                db = ((OriginalActivity) context).db;
            } catch (ClassCastException e) {
                e.printStackTrace();
                db = ((AuthentificationActivity) context).db;
            }

            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + "serveur";

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message mesg) {
                    throw new RuntimeException();
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int nbProduitsEnBDDistante = response.getInt("resultCount");
                                if (nbProduitsEnBDDistante == 1) {
                                    reponseRecue = true;
                                }
                                handler.sendMessage(handler.obtainMessage());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                handler.sendMessage(handler.obtainMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error");
                            handler.sendMessage(handler.obtainMessage());
                        }
                    }
            );
            requestQueue.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return reponseRecue;
    }
}
