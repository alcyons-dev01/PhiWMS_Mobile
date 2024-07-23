package fr.alcyons.phiwms_mobile;

import static com.google.android.gms.vision.L.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import fr.alcyons.phiwms_mobile.CGU.CguActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.WebView.WebViewActivity;
import fr.alcyons.phiwms_mobile.WebView.WebViewManager;


public class AuthentificationV2Activity extends MainActivity {

    private Button boutonConnexion;
    private Button boutonInscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebViewManager.getInstance(this);
        Intent laWebview = new Intent(AuthentificationV2Activity.this, AuthentificationTotpActivity.class);
        setContentView(R.layout.activity_authentificationv2);

        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String ipServ = sharedPreferences.getString("ipServeur", "");
        String numPort = sharedPreferences.getString("numPort", "");
        String versAPI = sharedPreferences.getString("versAPI", "");

        boutonConnexion = findViewById(R.id.boutonConnexion);

        boutonConnexion.setOnClickListener(v -> {
            EditText identifiant = findViewById(R.id.identifiant);
            String idStr = identifiant.getText().toString();
            EditText mdp = findViewById(R.id.motDePasse);
            String mdpStr = "";
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
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
            String urlRequete = "http://" + ipServ + ":" + numPort + "/api/v" + versAPI + "/utilisateurs/connexion";

            JSONObject body = new JSONObject();
            try {
                body.put("identifiant", idStr);
                body.put("mdp", mdpStr);
                body.put("etat_mdp", false);
            } catch (JSONException e) {
                Log.e(TAG, "JSONException :", e);
            }

            JsonObjectRequest requeteAuth = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
                try {
                    int nbResultats = response.getInt("resultCount");
                    //Log.d("test", String.valueOf(nbResultats));
                    if (nbResultats != 1) {
                        if (nbResultats > 1) {
                            Alerte.afficherAlerte(AuthentificationV2Activity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete identificationUtilisateur", "alerte");
                            boutonConnexion.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        if (nbResultats == 0) {
                            Toast toast = Toast.makeText(AuthentificationV2Activity.this, "Identifiant ou mot de passe incorrect !", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            boutonConnexion.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        String token = response.getString("token");
                        laWebview.putExtra("identifiant", idStr);
                        laWebview.putExtra("mdp", mdp.getText().toString());
                        laWebview.putExtra("token", token);
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

        boutonInscription = findViewById(R.id.boutonInscription);

        boutonInscription.setOnClickListener(v -> {
            Intent toInscription = new Intent(AuthentificationV2Activity.this, InscriptionActivity.class);

            String urlRequete = "http://" + ipServ + "/inscription";

            JSONObject body = new JSONObject();
            try {
                body.put("app_name", "PhiWMS Android");
            } catch (JSONException e) {
                Log.e(TAG, "JSONException :", e);
            }

            JsonObjectRequest requeteInscript = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
                try {
                    JSONArray profils = response.getJSONArray("profils");
                    JSONArray etablissements = response.getJSONArray("etablissements");
                    String[] listeProfils = new String[profils.length()];
                    for (int i = 0; i < profils.length() ; i ++){
                        listeProfils[i] = profils.getString(i);
                    }String[] listeEtablissements = new String[etablissements.length()];
                    for (int i = 0; i < etablissements.length() ; i ++){
                        listeEtablissements[i] = etablissements.getString(i);
                    }
                    toInscription.putExtra("profils", listeProfils);
                    toInscription.putExtra("etablissements", listeEtablissements);
                    startActivity(toInscription);
                } catch (JSONException exception) {
                    Alerte.afficherAlerte(AuthentificationV2Activity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete obtentionInfosInscription", "alerte");
                    Log.e(TAG, "JSONException :", exception);
                }
            },
                    error -> {
                        Log.e("Identifcation Volley", error.toString());
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
            requestQueueUtilisateur.add(requeteInscript);

        });

        //ouverture CGU
        findViewById(R.id.versCGU).setOnClickListener(v -> {
            Intent versCGU = new Intent(AuthentificationV2Activity.this, CguActivity.class);
            startActivity(versCGU);
        });

        findViewById(R.id.mdpOublie).setOnClickListener(v -> {
            Boolean mdpOublie = true;
            String identifiant = ((EditText) findViewById(R.id.identifiant)).getText().toString();
            laWebview.putExtra("identifiant", identifiant);
            laWebview.putExtra("etat_mdp", mdpOublie);
            startActivity(laWebview);
        });
    }

}