package fr.alcyons.phiwms_mobile.AuthentificationTotp;

import static com.google.android.gms.vision.L.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import fr.alcyons.phiwms_mobile.AuthentificationV2Activity;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.WebView.WebViewActivity;
import fr.alcyons.phiwms_mobile.WebView.WebViewManager;

public class AuthentificationTotpActivity extends MainActivity {

    private static FrameLayout webviewConteneur;
    private static TextView affichageCode;
    private static String toCompleteTotp = "";
    private boolean vueOuverte = false;
    private boolean mdpOublie;
    private View.OnClickListener fonctionBouton;
    private WebView vueActuelle;
    private GestionTotp gestionnaireTotp;

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
        gestionnaireTotp = GestionTotp.getInstance();
        affichageCode = (TextView) findViewById(R.id.codeTotp);
        webviewConteneur = (FrameLayout) findViewById(R.id.webview_container);
        webviewConteneur.removeAllViews();
        vueActuelle = WebViewManager.getInstance(this).getOffscreenWebView();
        webviewConteneur.addView(vueActuelle);

        Intent monIntention = getIntent();
        mdpOublie = monIntention.getBooleanExtra("etat_mdp", false);
        if (mdpOublie){
            vueActuelle.evaluateJavascript("window.location ='/demandemotdepasseoublie';", null);
        }

        String identifiant = monIntention.getStringExtra("identifiant");
        WebViewManager manager = WebViewManager.getInstance(AuthentificationTotpActivity.this);

        Button boutonValider = findViewById(R.id.boutonEnvoi);
        boutonValider.setOnClickListener(v -> {
            if (gestionnaireTotp.totpCorrect(toCompleteTotp)){
                toCompleteTotp = "";
                affichageCode.setText("");
                if (!mdpOublie){
                    manager.authentification(identifiant, monIntention.getStringExtra("mdp"));
                }
                else {
                    Intent laWebview = new Intent(AuthentificationTotpActivity.this, WebViewActivity.class);
                    webviewConteneur.removeAllViews();
                    startActivity(laWebview);
                    finish();
                }
            }
            else {
                Toast toast = Toast.makeText(AuthentificationTotpActivity.this, "Code invalide !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        Button boutonTotp = findViewById(R.id.boutonTotp);
        boutonTotp.setOnClickListener(v -> {
            boolean fonctionne = gestionnaireTotp.lancerTotp(identifiant, mdpOublie, monIntention.getStringExtra("token"), AuthentificationTotpActivity.this);
            if (! fonctionne){
                Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete sendTotpCode", "alerte");
            }
            else {
                List<String> charList = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                Collections.shuffle(charList);
                for (int i = 0 ; i < 10 ; i ++){
                    int buttonId = getResources().getIdentifier("boutonNum" + i, "id", getPackageName());
                    Button bouton = (Button) findViewById(buttonId);
                    bouton.setText(charList.get(i));
                }
            }
        });

        Runnable onLogin = new Runnable() {
            @Override
            public void run() {
                vueOuverte = true;
                affichageCode.setText("");
                toCompleteTotp = "";
                gestionnaireTotp.totpDone();
                Intent laWebview = new Intent(AuthentificationTotpActivity.this, WebViewActivity.class);
                webviewConteneur.removeAllViews();
                startActivity(laWebview);
                finish();
            }
        };

        Runnable onLogout = new Runnable() {
            @Override
            public void run() {
                Intent backToAuth = new Intent(AuthentificationTotpActivity.this, AuthentificationV2Activity.class);
                backToAuth.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backToAuth);
                WebViewCompat.removeWebMessageListener(vueActuelle, "androidMessageHandler");
                WebViewManager.destroy();
                vueOuverte = false;
                webviewConteneur.removeAllViews();
                finish();
            }
        };

        Runnable onLogFailed = new Runnable() {
            @Override
            public void run() {
                //Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Erreur differenceMdpMobileWeb", "alerte");
                Intent backToAuth = new Intent(AuthentificationTotpActivity.this, AuthentificationV2Activity.class);
                backToAuth.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backToAuth);
                WebViewCompat.removeWebMessageListener(vueActuelle, "androidMessageHandler");
                WebViewManager.destroy();
                vueOuverte = false;
                webviewConteneur.removeAllViews();
                finish();
            }
        };

        manager.addUponLogin(onLogin);
        manager.addUponLogout(onLogout);
        manager.addUponLogFailed(onLogFailed);

        fonctionBouton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button boutonCourant = (Button) v;
                String valeur = boutonCourant.getText().toString();
                appendToCompleteTotp(valeur.charAt(0));
            }
        };

        boolean fonctionne = gestionnaireTotp.lancerTotp(identifiant, mdpOublie, getIntent().getStringExtra("token"), AuthentificationTotpActivity.this);
        if (! fonctionne){
            Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete sendTotpCode", "alerte");
        }
        else {
            List<String> charList = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
            Collections.shuffle(charList);
            for (int i = 0 ; i < 10 ; i ++){
                int buttonId = getResources().getIdentifier("boutonNum" + i, "id", getPackageName());
                Button bouton = (Button) findViewById(buttonId);
                bouton.setText(charList.get(i));
                bouton.setOnClickListener(fonctionBouton);
            }
        }

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

    @Override
    protected void onPause(){
        super.onPause();
        gestionnaireTotp.totpDone();
        toCompleteTotp = "";
        affichageCode.setText("");
    }

}
