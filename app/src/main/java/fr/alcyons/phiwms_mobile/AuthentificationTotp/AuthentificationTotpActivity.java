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

    private static String toCompleteTotp = "";
    private int nbEssais = 5;

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

        // On place la WebView en arrière plan
        GestionTotp gestionnaireTotp = GestionTotp.getInstance();
        FrameLayout webviewConteneur = (FrameLayout) findViewById(R.id.webview_container);
        webviewConteneur.removeAllViews();
        WebView vueActuelle = WebViewManager.getInstance(this).getOffscreenWebView();
        webviewConteneur.addView(vueActuelle);

        // Si le mot de passe a été oublié, on change le lien que la webview affiche
        Intent monIntention = getIntent();
        boolean mdpOublie = monIntention.getBooleanExtra("etat_mdp", false);
        if (mdpOublie){
            vueActuelle.evaluateJavascript("window.location ='/demandemotdepasseoublie';", null);
        }

        // On récupère le minuteur de l'affichage
        MinuteurView leMinuteur = (MinuteurView) findViewById(R.id.minuteur);

        // On créé la fonction qui modifie l'affichage pour montrer l'avancée de la saisie du code TOTP
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

        // On créé une fonction à exécuter quand le site web envoie un message de connexion réussie
        Runnable onLogin = new Runnable() {
            @Override
            public void run() {
                updateAffichageTotp.run();
                toCompleteTotp = "";
                gestionnaireTotp.totpDone();
                Intent laWebview = new Intent(AuthentificationTotpActivity.this, WebViewActivity.class);
                webviewConteneur.removeAllViews();
                startActivity(laWebview);
                finish();
            }
        };

        // On créé une fonction à exécuter quand le site web envoie un message de déconnexion
        Runnable onLogout = new Runnable() {
            @Override
            public void run() {
                Intent backToAuth = new Intent(AuthentificationTotpActivity.this, AuthentificationV2Activity.class);
                backToAuth.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                WebViewManager.getInstance(AuthentificationTotpActivity.this).deconnexion();
                WebViewManager.destroy();
                startActivity(backToAuth);
                finish();
            }
        };

        // On créé une fonction à exécuter quand le site web envoie un message de connexion échouée
        Runnable onLogFailed = new Runnable() {
            @Override
            public void run() {
                Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Erreur differenceMdpMobileWeb", "alerte");
                Intent backToAuth = new Intent(AuthentificationTotpActivity.this, AuthentificationV2Activity.class);
                backToAuth.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backToAuth);
                WebViewCompat.removeWebMessageListener(vueActuelle, "androidMessageHandler");
                WebViewManager.destroy();
                webviewConteneur.removeAllViews();
                finish();
            }
        };

        // On définit ce qu'il se passe quand l'utilisateur demande à renvoyer un code TOTP
        Button boutonTotp = findViewById(R.id.boutonTotp);
        boutonTotp.setOnClickListener(v -> {
            // On relance la création d'un code TOTP
            boolean fonctionne = gestionnaireTotp.lancerTotp(identifiant, mdpOublie, monIntention.getStringExtra("token"), AuthentificationTotpActivity.this, leMinuteur);
            if (! fonctionne){
                Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete sendTotpCode", "alerte");
            }
            else {
                // On place les valeurs des boutons de manière aléatoire
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

        // On ajoute les fonctions traitant les messages envoyés par la WebView
        manager.addUponLogin(onLogin);
        manager.addUponLogFailed(onLogFailed);

        // On créé la fonction à exécuter quand un code à 6 caractères a été saisi
        Runnable onTotpComplete = new Runnable() {
            @Override
            public void run() {
                // On vérifie que le code est le bon
                if (gestionnaireTotp.totpCorrect(toCompleteTotp)){
                    // On vide le code
                    toCompleteTotp = "";
                    updateAffichageTotp.run();
                    // Si le mot de passe n'avait pas été oublié
                    if (!mdpOublie){
                        manager.authentification(identifiant, monIntention.getStringExtra("mdp"));
                        manager.addUponLogout(onLogout);
                    }
                    else{
                        // Dans le cas où le mot de passe a été oublié, le lien a été modifié et on part vers la webview
                        Intent laWebview = new Intent(AuthentificationTotpActivity.this, WebViewActivity.class);
                        Runnable passwordRecovered = new Runnable() {
                            @Override
                            public void run() {
                                Intent intention = new Intent(AuthentificationTotpActivity.this, AuthentificationV2Activity.class);
                                startActivity(intention);
                            }
                        };
                        manager.setPasswordRecovered(passwordRecovered);
                        webviewConteneur.removeAllViews();
                        startActivity(laWebview);
                    }
                }
                else {
                    if (nbEssais <= 1){
                        Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Authentification Forte", "5 essais écoulés, veuillez demander un nouveau code.", "alerte");
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
                    toCompleteTotp = "";
                    updateAffichageTotp.run();
                }
            }
        };

        // On créé la fonction qui définie le comportement des boutons remplissant le code TOTP
        View.OnClickListener fonctionBouton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button boutonCourant = (Button) v;
                String valeur = boutonCourant.getText().toString();
                appendToCompleteTotp(valeur.charAt(0));
                updateAffichageTotp.run();
                if (toCompleteTotp.length() == 6){
                    onTotpComplete.run();
                }
            }
        };

        // On lance la création d'un code TOTP
        boolean fonctionne = gestionnaireTotp.lancerTotp(identifiant, mdpOublie, getIntent().getStringExtra("token"), AuthentificationTotpActivity.this, leMinuteur);
        if (! fonctionne){
            Alerte.afficherAlerte(AuthentificationTotpActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete sendTotpCode", "alerte");
        }
        else {
            // On place les valeurs des boutons de manière aléatoire
            List<String> charList = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
            Collections.shuffle(charList);
            for (int i = 0 ; i < 10 ; i ++){
                int buttonId = getResources().getIdentifier("boutonNum" + i, "id", getPackageName());
                Button bouton = (Button) findViewById(buttonId);
                bouton.setText(charList.get(i));
                bouton.setOnClickListener(fonctionBouton);
            }
        }

        // On définie ce qu'il se passe quand on appuie sur le bouton permettant de coller depuis le presse-papiers
        findViewById(R.id.boutonColler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On récupère le contenu du presse-papiers
                ClipboardManager pressePapier = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData dernierElement = pressePapier.getPrimaryClip();
                ClipData.Item item = dernierElement.getItemAt(0);
                String copiedText = item.getText().toString().replaceAll("\\s", "");

                // On vérifie si le code est correct
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
                    onTotpComplete.run();
                }
            }
        });

        // On définie ce qu'il se passe quand on appuie sur le bouton annuler
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
                Intent backToAuth = new Intent(AuthentificationTotpActivity.this, AuthentificationV2Activity.class);
                backToAuth.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                WebViewManager.destroy();
                startActivity(backToAuth);
                finish();
            }
        });

        // On définie ce qu'il se passe quand l'utilisateur appuie sur la croix
        findViewById(R.id.imageRetour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // On définie ce qu'il se passe quand l'utilisateur appuie sur le bouton pour envoyer un message au support ALCYONS
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
