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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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

    // Fonction permettant de tester les paramètres de connexion à l'API
    public void TesteConnexion(View v) {
        if (checkConnectivity()) {
            Toast toast = Toast.makeText(ServiceParametresServeurActivity.this, "Succès lors du test de connexion", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Toast toast = Toast.makeText(ServiceParametresServeurActivity.this, messageErreur, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

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

        // Récupération et affichage des paramètres serveurs
        if (gestionnaireParametresServeur.getNbParametresServeur(db) == 1) {
            ip = gestionnaireParametresServeur.getIPServeur(db);
            port = gestionnaireParametresServeur.getPortServeur(db);
            version = gestionnaireParametresServeur.getAPIVersion(db);
        }

        ipServeurEditText.setText(ip);
        numeroPortEditText.setText(port);
        versionAPIEditText.setText(version.length() > 0 ? version.substring(1) : "");


        //gestion du bouton alcyons
        if(!Build.MANUFACTURER.contains("Zebra Technologies") && !Build.MANUFACTURER.toLowerCase().contains("honeywell") && !Build.MANUFACTURER.toLowerCase().contains("google")) {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(ServiceParametresServeurActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            String device_id = "";

            if (tm != null && !Build.MANUFACTURER.toLowerCase().contains("huawei") && !Build.MANUFACTURER.toLowerCase().contains("samsung")) {
                device_id = tm.getDeviceId();
            } else {
                String androidId = Settings.Secure.getString(ServiceParametresServeurActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                device_id = androidId;
            }

            if (device_id != null) {
                if (device_id.contentEquals("359467074856616") || device_id.contentEquals("865545031537572") || device_id.contentEquals("358439079740070") || device_id.contentEquals("e76b2dc0dc33f6b2") || device_id.contentEquals("baad6c7f647267d2")||device_id.contentEquals("66e4d0b5f734a6e7")|| device_id.contentEquals("356672848915688") || device_id.contentEquals("351921588915688")|| device_id.contentEquals("7db4057f77ad69c0") || device_id.contentEquals("352681302875720")) {
                    boutonAlcyonsParametre.setVisibility(View.VISIBLE);
                    boutonAlcyonsParametre.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ipServeurEditText.setText("192.81.222.83");
                            numeroPortEditText.setText("89");
                            versionAPIEditText.setText("1");
                        }
                    });
                }
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
        String base = "http://";
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
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        handler.sendMessage(handler.obtainMessage());
                        messageErreur = error.getMessage();
                    }
                }
        );

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
            Mail sender = new Mail(ServiceParametresServeurActivity.this, email[0], true, db);
            try {
                sender.sendMailVerification("Test mail", "Test envoi de mail");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }
}
