package fr.alcyons.phiwms_mobile;

import static com.google.android.gms.vision.L.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.WebView.WebViewActivity;
import fr.alcyons.phiwms_mobile.WebView.WebViewManager;


public class AuthentificationV2Activity extends MainActivity {

    private Button boutonConnexion;
    private WebViewManager manager;

    private String calculerTotp() throws InvalidKeyException, NoSuchAlgorithmException{
        final Charset asciiCs = Charset.forName("US-ASCII");
        String totp = "";
        int numberOfCodeDigits = 6;
        String secretKey = "Alcyons64BtzPhiWMSiOS2024";
        String validityInterval = Long.toBinaryString((System.currentTimeMillis() / 1000 / 30));
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] byteArray = mac.doFinal(asciiCs.encode(validityInterval).array());
        String hashCode = "";
        for (final byte element : byteArray)
        {
            hashCode += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
        }
        int offset = ((int) hashCode.charAt(hashCode.length() - 1)) & 0x0f;
        String truncatedHashCode = "";
        for (int i = offset; i < offset + 4; i ++)
        {
            truncatedHashCode += Integer.toString((byteArray[i] & 0xff) + 0x100, 16).substring(1);
        }
        int numericHashCode = (int) (Long.parseLong(truncatedHashCode, 16) % Math.pow(10 , numberOfCodeDigits));
        totp = String.valueOf(numericHashCode);
        if (totp.length() < numberOfCodeDigits){
            for (int i = totp.length() ; i < numberOfCodeDigits ; i ++){
                totp = "0" + totp;
            }
        }

        return totp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = WebViewManager.getInstance(this);
        Intent laWebview = new Intent(AuthentificationV2Activity.this, WebViewActivity.class);
        //laWebview.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        setContentView(R.layout.activity_authentification);
        boutonConnexion = findViewById(R.id.boutonConnexion);

        //calculerTotp();
        boutonConnexion.setOnClickListener(v -> {
            EditText identifiant = findViewById(R.id.identifiant);
            String idStr = identifiant.getText().toString();
            EditText mdp = findViewById(R.id.motDePasse);
            String mdpStr = "";
            try {
                MessageDigest md = null;
                md = MessageDigest.getInstance("MD5");
                byte[] theMD5digest = md.digest(new String(mdp.getText().toString()).getBytes("UTF-8"));
                for (final byte element : theMD5digest)
                {
                    mdpStr += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
                }
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            String urlRequete = "http://192.81.222.83:89/api/v1/utilisateurs/connexion";

            JSONObject body = new JSONObject();
            try {
                body.put("identifiant", idStr);
                body.put("mdp", "26c7dbc0993cff610de54c4d059333f2");
            } catch (JSONException e) {
                Log.e(TAG, "JSONException :", e);
            }

            JsonObjectRequest requeteAuth = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
                try {
                    int nbResultats = response.getInt("resultCount");
                    Log.d("test", String.valueOf(nbResultats));
                    if (nbResultats != 1) {

                    }
                    else {
                        manager.authentification(idStr, mdp.getText().toString());
                        startActivity(laWebview);
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
                    return params;
                }
            };
            RequestQueue requestQueueUtilisateur = Volley.newRequestQueue(this);
            requestQueueUtilisateur.add(requeteAuth);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}