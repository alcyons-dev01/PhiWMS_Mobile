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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import fr.alcyons.phiwms_mobile.AuthentificationTotp.AuthentificationTotpActivity;
import fr.alcyons.phiwms_mobile.CGU.CguActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.ParametresServeur.ServiceParametresServeurActivity;
import fr.alcyons.phiwms_mobile.WebView.WebViewManager;


public class AuthentificationV2Activity extends MainActivity {

    private Button boutonConnexion;
    private Button boutonInscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentificationv2);

        // On créé l'instance de WebViewManager ici car sinon l'instance ne fonctionne pas
        WebViewManager.destroy();
        WebViewManager.getInstance(this);
        // On prépare l'intention pour se rendre sur l'activité d'authentification par TOTP
        Intent goToTotp = new Intent(AuthentificationV2Activity.this, AuthentificationTotpActivity.class);

        // On récupère les paramètres de serveur
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String ipServ = sharedPreferences.getString("ipServeur", "");
        String numPort = sharedPreferences.getString("numPort", "");
        String versAPI = sharedPreferences.getString("versAPI", "");
        // Si l'un de ces paramètres n'existe pas, on envoie l'utilisateur à la page de paramètres
        if(ipServ.isEmpty() || numPort.isEmpty() || versAPI.isEmpty())
        {
            Intent newIntent = new Intent(AuthentificationV2Activity.this, MySettingsActivity.class);
            newIntent.putExtra("retourAAuth", true);
            AuthentificationV2Activity.this.startActivity(newIntent);
        }

        // On définit ce qu'il se passe lorsque l'utilisateur appuie sur le bouton de connexion
        boutonConnexion = findViewById(R.id.boutonConnexion);
        boutonConnexion.setOnClickListener(v -> {
            // On récupère les informations de connexion
            EditText identifiant = findViewById(R.id.identifiant);
            String idStr = identifiant.getText().toString();
            EditText mdp = findViewById(R.id.motDePasse);
            StringBuilder mdpStr = new StringBuilder();
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            // On passe le mot de passe dans l'algorithme de hashage
            try {
                MessageDigest md = null;
                md = MessageDigest.getInstance("MD5");
                byte[] theMD5digest = md.digest(mdp.getText().toString().getBytes(StandardCharsets.UTF_8));
                for (final byte element : theMD5digest)
                {
                    mdpStr.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
                }
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            // On prépare la requête au serveur pour s'y connecter
            String urlRequete = "http://" + ipServ + ":" + numPort + "/api/v" + versAPI + "/utilisateurs/connexion";

            JSONObject body = new JSONObject();
            try {
                body.put("identifiant", idStr);
                body.put("mdp", mdpStr.toString());
                body.put("etat_mdp", false);
            } catch (JSONException e) {
                Log.e(TAG, "JSONException :", e);
            }

            JsonObjectRequest requeteAuth = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
                try {
                    int nbResultats = response.getInt("resultCount");
                    //Log.d("test", String.valueOf(nbResultats));
                    if (nbResultats != 1) {
                        // Si plus d'un compte utilisateur correspond, il y a une erreur du côté d'ALCYONS
                        if (nbResultats > 1) {
                            Alerte.afficherAlerte(AuthentificationV2Activity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete identificationUtilisateur", "alerte");
                            boutonConnexion.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        // Si aucun compte utilisateur ne correspond, on renvoie un message concernant l'erreur de connexion
                        if (nbResultats == 0) {
                            Toast toast = Toast.makeText(AuthentificationV2Activity.this, "Identifiant ou mot de passe incorrect !", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            boutonConnexion.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    else {
                        // On passe les informations nécessaires à l'authentification TOTP
                        progressBar.setVisibility(View.GONE);
                        String token = response.getString("token");
                        goToTotp.putExtra("identifiant", idStr);
                        goToTotp.putExtra("mdp", mdp.getText().toString());
                        goToTotp.putExtra("token", token);
                        startActivity(goToTotp);
                        finish();
                    }
                } catch (JSONException exception) {
                    Log.e(TAG, "JSONException :", exception);
                    boutonConnexion.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }
            },
                    error -> {
                        Log.e("Identification Volley", error.toString());
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

        // On définit ce qu'il se passe lorsque l'utilisateur appuie sur le bouton d'inscription
        boutonInscription = findViewById(R.id.boutonInscription);
        boutonInscription.setOnClickListener(v -> {
            // On créé l'intention de se rendre dans l'activité d'inscription
            Intent toInscription = new Intent(AuthentificationV2Activity.this, InscriptionActivity.class);

            // On prépare la requête pour obtenir les informations nécessaires à l'inscription
            String urlRequete = "http://" + ipServ + "/inscription";

            JSONObject body = new JSONObject();
            try {
                body.put("app_name", "PhiWMS Android");
            } catch (JSONException e) {
                Log.e(TAG, "JSONException :", e);
            }

            JsonObjectRequest requeteInscript = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
                try {
                    // On récupère les listes de profils et d'établissements puis on les traduit dans un type que l'on peut transmettre facilement
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
                    finish();
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
            finish();
        });

        // Mot de passe oublié
        findViewById(R.id.mdpOublie).setOnClickListener(v -> {
            Boolean mdpOublie = true;
            String identifiant = ((EditText) findViewById(R.id.identifiant)).getText().toString();
            goToTotp.putExtra("identifiant", identifiant);
            goToTotp.putExtra("etat_mdp", mdpOublie);
            startActivity(goToTotp);
            finish();
        });
    }

}