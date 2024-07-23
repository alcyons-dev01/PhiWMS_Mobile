package fr.alcyons.phiwms_mobile;

import static com.google.android.gms.vision.L.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.webkit.JavaScriptReplyProxy;
import androidx.webkit.WebMessageCompat;
import androidx.webkit.WebViewCompat;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.WebView.WebViewActivity;
import fr.alcyons.phiwms_mobile.WebView.WebViewManager;

public class AuthentificationTotpActivity extends MainActivity {

    private static FrameLayout webviewConteneur;
    private static TextView affichageCode;
    private static String toCompleteTotp = "";
    private static String totp = "";
    private boolean vueOuverte = false;
    private boolean premiereDefinitionTotp = true;
    private boolean mdpOublie;
    private View.OnClickListener fonctionBouton;
    private String ipServ;
    private Timer timer;
    private WebView vueActuelle;

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

    private void lancerTotp(String identifiant){
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
            if (! premiereDefinitionTotp){
                timer.cancel();
                timer.purge();
            }
            timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    Log.d("test", "suppression totp");
                    totp = "AAAAAA";
                    timer.cancel();
                }
            };
            timer.schedule(doAsynchronousTask, 300000, 1);
            Log.d("test", totp);
            JsonObjectRequest requeteAuth = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
                try {
                    boolean success = response.getBoolean("success");
                    if (success){
                        List<String> charList = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                        Collections.shuffle(charList);
                        for (int i = 0 ; i < 10 ; i ++){
                            int buttonId = getResources().getIdentifier("boutonNum" + i, "id", getPackageName());
                            Button bouton = (Button) findViewById(buttonId);
                            bouton.setText(charList.get(i));
                            if (premiereDefinitionTotp) {
                                bouton.setOnClickListener(fonctionBouton);
                            }
                        }
                        premiereDefinitionTotp = false;
                    }
                    else {
                        Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete sendTotpCode", "alerte");
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
                    params.put("Authorization", getIntent().getStringExtra("token"));
                    return params;
                }
            };
            RequestQueue requestQueueUtilisateur = Volley.newRequestQueue(this);
            requestQueueUtilisateur.add(requeteAuth);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean appendToCompleteTotp(Character c){
        if (toCompleteTotp.length() == 6){
            toCompleteTotp = toCompleteTotp.substring(1);
        }
        toCompleteTotp += c;
        affichageCode.setText(toCompleteTotp);
        boolean retour;
        if (toCompleteTotp.length() == 6){
            retour = true;
        }
        else {
            retour = false;
        }
        return retour;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification_totp);
        affichageCode = (TextView) findViewById(R.id.codeTotp);
        webviewConteneur = (FrameLayout) findViewById(R.id.webview_container);
        webviewConteneur.removeAllViews();
        vueActuelle = WebViewManager.getInstance(null).getOffscreenWebView();
        webviewConteneur.addView(vueActuelle);

        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        ipServ = sharedPreferences.getString("ipServeur", "");

        Intent monIntention = getIntent();
        mdpOublie = monIntention.getBooleanExtra("etat_mdp", false);
        if (mdpOublie){
            vueActuelle.evaluateJavascript("window.location ='/demandemotdepasseoublie';", null);
        }

        String identifiant = monIntention.getStringExtra("identifiant");
        WebViewManager manager = WebViewManager.getInstance(this);

        Button boutonValider = findViewById(R.id.boutonEnvoi);
        boutonValider.setOnClickListener(v -> {
            if (affichageCode.getText().toString().equals(totp) && totp != ""){
                if (!mdpOublie){
                    manager.authentification(identifiant, monIntention.getStringExtra("mdp"));
                }
                else {
                    Intent laWebview = new Intent(AuthentificationTotpActivity.this, WebViewActivity.class);
                    webviewConteneur.removeAllViews();
                    startActivity(laWebview);
                }
            }
        });

        Button boutonTotp = findViewById(R.id.boutonTotp);
        boutonTotp.setOnClickListener(v -> {
            lancerTotp(identifiant);
        });

        Set<String> allowedOrigins = new HashSet<>();
        allowedOrigins.add("http://10.0.2.2:8000");
        allowedOrigins.add("http://phiwms.alcyons.fr");
        allowedOrigins.add("http://" + ipServ);
        WebViewCompat.WebMessageListener myListener = new WebViewCompat.WebMessageListener() {
            @Override
            public void onPostMessage(WebView view, WebMessageCompat message, Uri sourceOrigin,
                                      boolean isMainFrame, JavaScriptReplyProxy replyProxy) {
                if (message.getData().equals("userIsLoggedOut")){
                    Intent backToAuth = new Intent(AuthentificationTotpActivity.this, AuthentificationV2Activity.class);
                    backToAuth.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    WebViewManager.destroy();
                    vueOuverte = false;
                    startActivity(backToAuth);
                    finish();
                } else if (message.getData().equals("userLoginFailed")){
                    Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Erreur differenceMdpMobileWeb", "alerte");
                } else if (message.getData().equals("userIsLogged")) {
                    vueOuverte = true;
                    affichageCode.setText("");
                    toCompleteTotp = "";
                    Intent laWebview = new Intent(AuthentificationTotpActivity.this, WebViewActivity.class);
                    webviewConteneur.removeAllViews();
                    startActivity(laWebview);
                }

            }
        };

        WebViewCompat.addWebMessageListener(vueActuelle, "androidMessageHandler", allowedOrigins, myListener);

        fonctionBouton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button boutonCourant = (Button) v;
                String valeur = boutonCourant.getText().toString();
                appendToCompleteTotp(valeur.charAt(0));
            }
        };

        lancerTotp(identifiant);

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (! vueOuverte){
                    Intent backToAuth = new Intent(AuthentificationTotpActivity.this, AuthentificationV2Activity.class);
                    backToAuth.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    WebViewManager.destroy();
                    startActivity(backToAuth);
                    finish();
                }
            }
        });

    }

}
