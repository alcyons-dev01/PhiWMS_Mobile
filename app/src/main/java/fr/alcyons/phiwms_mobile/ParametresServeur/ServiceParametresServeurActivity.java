package fr.alcyons.phiwms_mobile.ParametresServeur;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.R;

public class ServiceParametresServeurActivity extends AppCompatActivity {

    public SQLiteDatabase db;
    public ParametresServeurOpenHelper gestionnaireParametresServeur;
    public DBOpenHelper gestionnaireBDD;
    public boolean reponseRecue = false;
    String ip = "";
    String port = "";
    String version = "";

    EditText ipServeurEditText;
    EditText numeroPortEditText;
    EditText versionAPIEditText;
    Button boutonAlcyonsParametre;

    String messageErreur;

    Button testConnexionButton;
    Spinner alcyons_choix_client;

    // Fonction permettant d'enregistrer les paramètres de connexion à l'API
    public void Connexion(View v) {

        String ipServeurSaisie = ipServeurEditText.getText().toString();
        String numeroPortSaisie = numeroPortEditText.getText().toString();
        String versionApiSaisie = "v" + versionAPIEditText.getText().toString();
        Boolean Reliquats_pour_prevision = gestionnaireParametresServeur.getReliquats_pour_prevision(db);
        Boolean Liv_indirecte_egal_Cond_achat = gestionnaireParametresServeur.getLiv_indirecte_egal_Cond_achat(db);
        Boolean planDeCueilletteActif = gestionnaireParametresServeur.getPlanDeCueilletteActif(db);
        Boolean module_transport = gestionnaireParametresServeur.getModuleTransport(db);
        String mail_emetteur = gestionnaireParametresServeur.getMailEmetteur(db);
        String mdp_emetteur = gestionnaireParametresServeur.getMDPEmetteur(db);
        int smtp_port = gestionnaireParametresServeur.getSMTPPort(db);
        String smtp_serveur = gestionnaireParametresServeur.getSMTPServeur(db);
        int smtp_session = gestionnaireParametresServeur.getSMTPSession(db);
        String loginEmetteur  = gestionnaireParametresServeur.getLoginEmetteur(db);

        if (!checkConnectivity()) {
            Toast.makeText(ServiceParametresServeurActivity.this, "Les paramètres fournis ne permettent pas de se connecter à la base", Toast.LENGTH_SHORT).show();
        } else {
            if (!ipServeurSaisie.contentEquals(ip) || !numeroPortSaisie.contentEquals(port) || !versionApiSaisie.contentEquals(version)) {
                gestionnaireBDD.viderBasesDeDonnees(db);

                long rowID = gestionnaireParametresServeur.updateParametresServeurEnBDD(db, ipServeurSaisie, numeroPortSaisie, versionApiSaisie, "", "", "", "", 0, "", Reliquats_pour_prevision, Liv_indirecte_egal_Cond_achat, planDeCueilletteActif,module_transport, mail_emetteur, mdp_emetteur, smtp_port, smtp_serveur, smtp_session, loginEmetteur);
                if (rowID != -1) {
                    ServiceParametresServeurActivity.this.finishAffinity();
                    Intent serviceParametresServeurIntent = new Intent(ServiceParametresServeurActivity.this, AuthentificationActivity.class);
                    ServiceParametresServeurActivity.this.startActivity(serviceParametresServeurIntent);
                }
            } else {
                onBackPressed();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_parametres_serveur);

        boutonAlcyonsParametre = (Button) findViewById(R.id.boutonAlcyonsParametre);

        gestionnaireBDD = new DBOpenHelper(ServiceParametresServeurActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireParametresServeur = new ParametresServeurOpenHelper(ServiceParametresServeurActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        db = gestionnaireBDD.openDB();

        ipServeurEditText = (EditText) findViewById(R.id.ipServeur);
        numeroPortEditText = (EditText) findViewById(R.id.numeroPort);
        versionAPIEditText = (EditText) findViewById(R.id.versionAPI);
        alcyons_choix_client = (Spinner) findViewById(R.id.alcyons_choix_client);

        // Récupération et affichage des paramètres serveurs
        if (gestionnaireParametresServeur.getNbParametresServeur(db) == 1) {
            ip = gestionnaireParametresServeur.getIPServeur(db);
            port = gestionnaireParametresServeur.getPortServeur(db);
            version = gestionnaireParametresServeur.getAPIVersion(db);
        }

        ipServeurEditText.setText(ip);
        numeroPortEditText.setText(port);
        versionAPIEditText.setText(version.length() > 0 ? version.substring(1) : "");

        testConnexionButton = findViewById(R.id.boutonTestConnexion);

        testConnexionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConnexionButton.setBackground(getResources().getDrawable(R.color.gris));
                if (checkConnectivity()) {
                    Toast toast = Toast.makeText(ServiceParametresServeurActivity.this, "Succès lors du test de connexion", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    testConnexionButton.setBackground(getResources().getDrawable(R.color.vert));
                } else {
                    Toast toast = Toast.makeText(ServiceParametresServeurActivity.this, messageErreur, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    testConnexionButton.setBackground(getResources().getDrawable(R.color.vert));
                }
            }
        });

        //gestion du bouton alcyons
        // simulateur google macAir Olivier : 95004b3de3fc203b
        // Samsung XCover 5 : a964f85c6e890958
        // HoneyWell CT37 alcyons: 57010b2fdbf2b021
        // Samsung XCover 6 Pro : 6327572c7741bb99
        // Galaxy Tab Active4 Pro : ca67efeec851db73
        String device_id = Settings.Secure.getString(ServiceParametresServeurActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (device_id != null) {
            if (device_id.contentEquals("66daff2c2aa90621") || device_id.contentEquals("0db4cceba9d4085b") || device_id.contentEquals("00de9a1ef0755a92") || device_id.contentEquals("7c9ff7c3df4bc9e0") || device_id.contentEquals("95004b3de3fc203b") || device_id.contentEquals("a964f85c6e890958") || device_id.contentEquals("57010b2fdbf2b021") || device_id.contentEquals("6327572c7741bb99") || device_id.contentEquals("ca67efeec851db73") || device_id.contentEquals("80f8f5570900fcb2") || device_id.contentEquals("08f1ad94f577e406")) {
                //boutonAlcyonsParametre.setVisibility(View.VISIBLE);
                alcyons_choix_client.setVisibility(View.VISIBLE);
                /*boutonAlcyonsParametre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ipServeurEditText.setText("192.81.222.83");
                        numeroPortEditText.setText("89");
                        versionAPIEditText.setText("1");
                    }
                });*/
                alcyons_choix_client.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        // position 0 = "Choisir une base" → ignorée
                        if (position == 0) {
                            return;
                        }

                        String valeur = parent.getItemAtPosition(position).toString();
                        int resId = getResources().getIdentifier(
                                valeur,
                                "string",
                                getPackageName()
                        );

                        if (resId != 0) {
                            String port = getString(resId);
                            if(Integer.parseInt(port) < 100)
                                ipServeurEditText.setText("192.81.222.83");
                            else
                                ipServeurEditText.setText("phiwms.alcyons.fr");

                            numeroPortEditText.setText(port);
                            versionAPIEditText.setText("1");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // rien à faire
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int nbUtilisateur = ParametreUtilisateurOpenHelper.getNbUtilisateur(db);
        if (nbUtilisateur == 0) {
            ParametreUtilisateurOpenHelper.insererParametreUtilisateurEnBDD(db, 0, false, false, false, "Designation", "Numéro de commande", "Categorie", "Numéro de retour", "Designation");
        }

        ((ImageView) findViewById(R.id.flecheRetour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    // Fonction permettant de vérifier la connexion à l'API
    public boolean checkConnectivity() {
        reponseRecue = false;
        messageErreur = "";
        String api = "/api/";


        String ipServeurTest = ipServeurEditText.getText().toString();
        String numeroPortTest = numeroPortEditText.getText().toString();
        String versionApiTest = "v" + versionAPIEditText.getText().toString();

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        String base = "https://";
        if(Integer.parseInt(numeroPortTest) < 100 || Integer.parseInt(numeroPortTest) == 8081)
            base = "http://";

        String urlRequete = base + ipServeurTest + ":" + numeroPortTest + api + versionApiTest + "/serveur";

        RequestQueue requestQueue = Volley.newRequestQueue(ServiceParametresServeurActivity.this);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 1) {
                                reponseRecue = true;
                            }
                            handler.sendMessage(handler.obtainMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.sendMessage(handler.obtainMessage());
                            messageErreur = "Erreur lors de la récupération des données";
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        handler.sendMessage(handler.obtainMessage());
                        messageErreur = "Erreur lors de la connexion du serveur";
                    }
                }
        );
        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                handler.sendMessage(handler.obtainMessage());
                messageErreur = "Impossible d'atteindre le serveur";
                Toast toast = Toast.makeText(ServiceParametresServeurActivity.this, messageErreur, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        requestQueue.add(obreq);
        try {
            Looper.loop();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return reponseRecue;
    }

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {
            Mail sender = new Mail(ServiceParametresServeurActivity.this, email[0], true, db, null);
            try {
                sender.sendMailVerification("Test mail", "Test envoi de mail");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }
}
