package fr.alcyons.phiwms_mobile.AuthentificationTotp;

import static com.google.android.gms.vision.L.TAG;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import fr.alcyons.phiwms_mobile.MinuteurView;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.SupportActivity;
import fr.alcyons.phiwms_mobile.WebView.WebViewActivity;
import fr.alcyons.phiwms_mobile.WebView.WebViewManager;

public class AuthentificationTotpActivity extends MainActivity {

    private static FrameLayout webviewConteneur;
    private static String toCompleteTotp = "";
    private boolean vueOuverte = false;
    private boolean mdpOublie;
    private int nbEssais = 5;
    private WebView vueActuelle;
    private GestionTotp gestionnaireTotp;

    private static boolean appendToCompleteTotp(Character c){
        if (toCompleteTotp.length() == 6){
            toCompleteTotp = toCompleteTotp.substring(1);
        }
        toCompleteTotp += c;
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
        webviewConteneur = (FrameLayout) findViewById(R.id.webview_container);
        webviewConteneur.removeAllViews();
        vueActuelle = WebViewManager.getInstance(this).getOffscreenWebView();
        webviewConteneur.addView(vueActuelle);

        Intent monIntention = getIntent();
        mdpOublie = monIntention.getBooleanExtra("etat_mdp", false);
        if (mdpOublie){
            vueActuelle.evaluateJavascript("window.location ='/demandemotdepasseoublie';", null);
        }

        MinuteurView leMinuteur = (MinuteurView) findViewById(R.id.minuteur);

        Runnable updateAffichageTotp = new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 6; i++){
                    int imageId = getResources().getIdentifier("char" + i, "id", getPackageName());
                    ImageView image = (ImageView) findViewById(imageId);
                    if (i <= toCompleteTotp.length()){
                        image.setImageResource(R.mipmap.ic_cercle_plein);
                    }
                    else {
                        image.setImageResource(R.mipmap.ic_cercle_vide);
                    }
                }
            }
        };

        String identifiant = monIntention.getStringExtra("identifiant");
        WebViewManager manager = WebViewManager.getInstance(AuthentificationTotpActivity.this);

        Runnable onLogin = new Runnable() {
            @Override
            public void run() {
                vueOuverte = true;
                updateAffichageTotp.run();
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
                Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Erreur differenceMdpMobileWeb", "alerte");
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

        Button boutonValider = findViewById(R.id.boutonEnvoi);
        boutonValider.setOnClickListener(v -> {
            if (gestionnaireTotp.totpCorrect(toCompleteTotp)){
                toCompleteTotp = "";
                updateAffichageTotp.run();
                if (!mdpOublie){
                    manager.authentification(identifiant, monIntention.getStringExtra("mdp"));
                }
                manager.addUponLogin(onLogin);
                manager.addUponLogout(onLogout);
                manager.addUponLogFailed(onLogFailed);
            }
            else {
                if (nbEssais <= 1){
                    Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Authentification Forte", "5 essais écoulés, veuillez demander un nouveau code.", "alerte");
                    toCompleteTotp = "";
                }
                else {
                    Toast toast = Toast.makeText(AuthentificationTotpActivity.this, "Code invalide !", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                if (nbEssais > 0){
                    nbEssais --;
                    TextView vue = (TextView) findViewById(R.id.textNbEssais);
                    vue.setText("Il vous reste " + nbEssais + " essais.");
                }
                updateAffichageTotp.run();
            }
        });

        Button boutonTotp = findViewById(R.id.boutonTotp);
        boutonTotp.setOnClickListener(v -> {
            boolean fonctionne = gestionnaireTotp.lancerTotp(identifiant, mdpOublie, monIntention.getStringExtra("token"), AuthentificationTotpActivity.this, leMinuteur);
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
                nbEssais = 5;
                TextView vue = (TextView) findViewById(R.id.textNbEssais);
                vue.setText("Il vous reste " + nbEssais + " essais.");
            }
        });

        View.OnClickListener fonctionBouton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button boutonCourant = (Button) v;
                String valeur = boutonCourant.getText().toString();
                appendToCompleteTotp(valeur.charAt(0));
                updateAffichageTotp.run();
            }
        };

        boolean fonctionne = gestionnaireTotp.lancerTotp(identifiant, mdpOublie, getIntent().getStringExtra("token"), AuthentificationTotpActivity.this, leMinuteur);
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

        findViewById(R.id.boutonColler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager pressePapier = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData dernierElement = pressePapier.getPrimaryClip();
                ClipData.Item item = dernierElement.getItemAt(0);
                String copiedText = item.getText().toString();
                Boolean codeCorrect = true;
                if (copiedText.length() != 6){
                    codeCorrect = false;
                }
                try {
                    Integer.valueOf(copiedText);
                } catch (NumberFormatException e) {
                    codeCorrect = false;
                }
                if (!codeCorrect){
                    Toast toast = Toast.makeText(AuthentificationTotpActivity.this, "Element copié invalide !", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    toCompleteTotp = copiedText;
                    updateAffichageTotp.run();
                }
            }
        });

        findViewById(R.id.boutonAnnuler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCompleteTotp = "";
                updateAffichageTotp.run();
            }
        });

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

        findViewById(R.id.imageRetour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        findViewById(R.id.boutonSupport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent toSupport = new Intent(AuthentificationTotpActivity.this, SupportActivity.class);
                toSupport.putExtra("token", monIntention.getStringExtra("token"));
                gestionnaireTotp.totpDone();
                leMinuteur.stopTimer();
                startActivity(toSupport);
                */
            }
        });

    }

}
