package fr.alcyons.phiwms_mobile.AuthentificationTotp;

import static com.google.android.gms.vision.L.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.hash.Hashing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fr.alcyons.phiwms_mobile.MinuteurView;
import fr.alcyons.phiwms_mobile.Outils.Alerte;

public class GestionTotp {

    private static GestionTotp instance;
    private static String totp;
    private Timer timer;

    private String calculerTotp() throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String totp = "";
        int numberOfCodeDigits = 6;
        String secretKey = "Alcyons64BtzPhiWMSiOS2024";
        String validityInterval = String.format("%64s", Long.toBinaryString((System.currentTimeMillis() / 1000 / 30))).replace(' ', '0');

        byte [] arrayOctets = new byte[8];
        for (int i = 0; i < 64 ; i += 8){
            String byteString = validityInterval.substring(i, i + 8);
            byte b = (byte) Integer.parseInt(byteString, 2);
            arrayOctets[i / 8] = b;
        }

        String hashCode = Hashing.hmacSha256(secretKey.getBytes(StandardCharsets.UTF_8)).hashBytes(arrayOctets).toString();
        byte [] byteArray = hashCode.getBytes();
        //Log.d("test", hashCode);
        int taille = hashCode.length();
        int offset = ((int) Integer.parseInt(hashCode.substring(taille - 2, taille), 16)) & 0x0f;

        //Log.d("test", String.valueOf(offset));
        String truncatedHashCode = hashCode.substring(offset * 2, (offset + 4) * 2);
        //Log.d("test", truncatedHashCode);

        int numericHashCode = (int) (Long.parseLong(truncatedHashCode, 16) % Math.pow(10 , numberOfCodeDigits));
        totp = String.valueOf(numericHashCode);
        if (totp.length() < numberOfCodeDigits){
            for (int i = totp.length() ; i < numberOfCodeDigits ; i ++){
                totp = "0" + totp;
            }
        }

        return totp;
    }

    public boolean lancerTotp(String identifiant, Boolean mdpOublie, String token, Context context, MinuteurView minuteur){
        totp = "AAAAAA";
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String ipServ = sharedPreferences.getString("ipServeur", "");
        String urlRequete = "http://" + ipServ + "/sendTotpCode";
        JSONObject body = new JSONObject();
        try {
            body.put("identifiant", identifiant);
            body.put("app_name", "PhiWMS Android");
            body.put("for_recovery", mdpOublie);
            body.put("device_info", Build.DEVICE);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException :", e);
        }

        try {
            totp = calculerTotp();
            minuteur.stopTimer();
            minuteur.startTimer(5, 0, this::totpDone);
            //Log.d("test", totp);
            JsonObjectRequest requeteAuth = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
                try {
                    boolean success = response.getBoolean("success");
                    if (! success){
                        totp = "AAAAAA";
                    }
                } catch (JSONException exception) {
                    Log.e(TAG, "JSONException :", exception);
                }
            },
                    error -> {
                        Log.e("Idenitifcation Volley", error.toString());
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json;charset=utf-8");
                    params.put("Authorization", token);
                    return params;
                }
            };
            RequestQueue requestQueueUtilisateur = Volley.newRequestQueue(context);
            requestQueueUtilisateur.add(requeteAuth);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return !totp.equals("AAAAAA");
    }

    public boolean totpCorrect(String totp){
        return this.totp.equals(totp);
    }

    public void logTotp(){
        Log.d("GestionTotp", totp);
    }

    public void totpDone(){
        totp = "AAAAAA";
    }

    public static GestionTotp getInstance(){
        if (instance == null){
            instance = new GestionTotp();
        }
        return instance;
    }

}
