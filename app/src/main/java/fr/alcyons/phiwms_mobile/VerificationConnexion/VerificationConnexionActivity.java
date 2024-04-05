package fr.alcyons.phiwms_mobile.VerificationConnexion;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import com.example.phiwms_mobile.R;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by olivier on 05/03/2018.
 */

public class VerificationConnexionActivity extends AppCompatActivity {

    public SQLiteDatabase db;
    protected UtilisateurOpenHelper gestionnaireUtilisateur;
    protected DBOpenHelper gestionnaireBDD;
    EditText verification_code;
    TextView identiteUtilisateur;
    TextView message;
    Button verification_Button;
    Button renvoyer_code;
    Utilisateur utilisateurConnecte;
    String code_verification;
    String channel;
    List<String> channels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_connexion);

        channels = new ArrayList<>();

        //Gestion de la base de données
        gestionnaireUtilisateur = new UtilisateurOpenHelper(VerificationConnexionActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireBDD = new DBOpenHelper(VerificationConnexionActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        db = gestionnaireBDD.openDB();


        //Initialisation des objets graphiques
        verification_code = (EditText) findViewById(R.id.verification_code);
        verification_Button = (Button) findViewById(R.id.verification_Button);
        renvoyer_code = (Button) findViewById(R.id.renvoyer_code);
        identiteUtilisateur = (TextView) findViewById(R.id.identiteUtilisateur);
        message = (TextView) findViewById(R.id.message);


        verification_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeSaisie = verification_code.getText().toString();

                // Access the default SharedPreferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VerificationConnexionActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                int badgeCount = preferences.getInt("badgeCount", 0);
                if (badgeCount > 0) {
                    badgeCount--;
                    if (ShortcutBadger.isBadgeCounterSupported(VerificationConnexionActivity.this)) {
                        // Edit the saved preferences
                        editor.putInt("badgeCount", badgeCount);
                        editor.commit();
                        ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
                    }
                }

                if (codeSaisie == null || codeSaisie.equals("") || codeSaisie.length() != code_verification.length()) {
                    Alerte.afficherAlerte(VerificationConnexionActivity.this, "Erreur", "Veuillez saisir un code valide avant la vérification svp", "alerte");
                    verification_code.setText("");
                } else {
                    if (codeSaisie.equals(code_verification)) {
                        Intent resultIntent = new Intent();
                        Bundle extras = new Bundle();
                        extras.putBoolean("verifier", true);
                        resultIntent.putExtras(extras);
                        setResult(CodesEchangesActivites.RESULT_VERIFICATION_UTILISATEUR, resultIntent);
                        finish();
                    } else {
                        Intent resultIntent = new Intent();
                        Bundle extras = new Bundle();
                        extras.putBoolean("verifier", false);
                        resultIntent.putExtras(extras);
                        setResult(CodesEchangesActivites.RESULT_VERIFICATION_UTILISATEUR, resultIntent);
                        finish();
                    }
                }
            }
        });

        renvoyer_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Access the default SharedPreferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VerificationConnexionActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                int badgeCount = preferences.getInt("badgeCount", 0);
                if (badgeCount > 0) {
                    badgeCount--;
                    if (ShortcutBadger.isBadgeCounterSupported(VerificationConnexionActivity.this)) {
                        // Edit the saved preferences
                        editor.putInt("badgeCount", badgeCount);
                        editor.commit();
                        ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
                    }
                }
                onResume();
            }
        });


        channel = VerificationConnexionActivity.this.getIntent().getExtras().getString("channel");

        channels.add(channel);

        Integer utilisateurID = VerificationConnexionActivity.this.getIntent().getExtras().getInt("utilisateurConnecteID");
        utilisateurConnecte = gestionnaireUtilisateur.getUtilisateurByID(db, utilisateurID);
        identiteUtilisateur.setText("Vous souhaitez-vous connecter en tant que : \n" + utilisateurConnecte.getNom() + " " + utilisateurConnecte.getPrenom());

        message.setText("Vous avez opté pour l'authentification forte. \n Pour poursuivre la connexion, veuillez saisir le code reçu (par notification ou par mail) dans la zone prévue ci-dessous. \n");

        // Access the default SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VerificationConnexionActivity.this);

        // Récupération des identifiants
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String pubnubSubKey = preferences.getString(getString(R.string.pubnubSubscribeKey), "sub-c-76c656ba-6c93-11e7-85aa-0619f8945a4f");

        // Initialisation de l'objet pubnub
        //PNConfiguration pnConfiguration = new PNConfiguration().setSubscribeKey(pubnubSubKey).setSecure(true);
        PNConfiguration pnConfiguration = null;

        PubNub pubnub = new PubNub(pnConfiguration);

        pubnub.subscribe().channels(channels).execute();

        pubnub.addPushNotificationsOnChannels()
                .channels(channels)
                .deviceId(refreshedToken)
                .pushType(PNPushType.GCM)
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                    }
                });


    }

    @Override
    public void onResume() {
        super.onResume();

        /* Code nécessaire afin de réaliser une requête à l' API */
        JSONObject body = new JSONObject();

        try {
            body.put("channel", channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueueNotification = Volley.newRequestQueue(VerificationConnexionActivity.this);
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceNotification + "/verification";

        // Takes the response from the JSON request

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, body, new Response.Listener<JSONObject>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int resultCount = response.getInt("resultCount");
                    if (resultCount == 0) {
                        Alerte.afficherAlerte(VerificationConnexionActivity.this, "Erreur", "Code de vérification non envoyé !", "alerte");
                    } else {
                        code_verification = response.getString("codeVerification");
                        String email = utilisateurConnecte.getMail();
                        if(utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS"))
                        {
                            email = "dev01@alcyons.fr";
                        }
                        if (email != null) {
                            new SendEmailTask().execute(email);
                        }
                        ;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(VerificationConnexionActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP envoyer notification", "alerte");
                        VerificationConnexionActivity.this.finish();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                return headers;
            }
        };

        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 3000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 3000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueueNotification.add(obreq);

        invalidateOptionsMenu();

    }

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(VerificationConnexionActivity.this, email[0], true, db);
            try {
                // Envoi du mail de vérification
                sender.sendMailVerification("Verification", "Voici le code de vérification : " + code_verification);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }

}
