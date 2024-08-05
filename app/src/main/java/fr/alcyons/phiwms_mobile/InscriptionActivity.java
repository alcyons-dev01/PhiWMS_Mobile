package fr.alcyons.phiwms_mobile;

import static com.google.android.gms.vision.L.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.WebView.WebViewActivity;
import fr.alcyons.phiwms_mobile.WebView.WebViewManager;

public class InscriptionActivity extends MainActivity {

    private EditText inputEmail;
    private Spinner selecteurProfil;
    private Spinner selecteurEtablissement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        Intent monIntention = getIntent();
        String[] profils = monIntention.getStringArrayExtra("profils");
        String[] etablissements = monIntention.getStringArrayExtra("etablissements");

        inputEmail = (EditText) findViewById(R.id.email);
        selecteurProfil = (Spinner) findViewById(R.id.profil);
        selecteurEtablissement = (Spinner) findViewById(R.id.etablissement);

        ArrayAdapter<String> adapterProfil = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, profils);
        ArrayAdapter<String> adapterEtablissement = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, etablissements);

        selecteurProfil.setAdapter(adapterProfil);
        selecteurEtablissement.setAdapter(adapterEtablissement);

        Button envoiDemande = (Button) findViewById(R.id.boutonDemande);
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String ipServ = sharedPreferences.getString("ipServeur", "");

        envoiDemande.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();
            String profil = selecteurProfil.getSelectedItem().toString();
            String etablissement = selecteurEtablissement.getSelectedItem().toString();
            if (email != null && email != "" && profil != null && profil != "" && etablissement != null && etablissement != ""){
                String urlRequete = "http://" + ipServ + "/demandeInscription";

                JSONObject body = new JSONObject();
                try {
                    body.put("email", email);
                    body.put("profil", profil);
                    body.put("etablissement", etablissement);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException :", e);
                }

                JsonObjectRequest requeteInscript = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
                    try {
                        if (response.getBoolean("success")){
                            Intent backToAuth = new Intent(InscriptionActivity.this, AuthentificationV2Activity.class);
                            backToAuth.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(backToAuth);
                            finish();
                        }
                        else {
                            Alerte.afficherAlerte(InscriptionActivity.this, "Erreur demande", response.getString("reason"), "alerte");
                        }
                    } catch (JSONException exception) {
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
            }
        });

        findViewById(R.id.imageRetour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

}
