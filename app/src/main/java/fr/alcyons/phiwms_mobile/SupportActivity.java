package fr.alcyons.phiwms_mobile;

import static com.google.android.gms.vision.L.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.alcyons.phiwms_mobile.WebView.WebViewManager;

public class SupportActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        Intent monIntention = getIntent();

        findViewById(R.id.boutonContactSupport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = ((EditText) findViewById(R.id.textMessageSupport)).getText().toString();

                String urlRequete = "http://10.0.2.2:8000" +/* ipServ +*/ "/envoimailsos";
                JSONObject body = new JSONObject();
                try {
                    body.put("textDemande", message);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException :", e);
                }

                JsonObjectRequest requeteAuth = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {

                },
                    error -> {
                        Log.e("Idenitifcation Volley", error.toString());
                    }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json;charset=utf-8");
                        params.put("Authorization", monIntention.getStringExtra("token"));
                        return params;
                    }
                };
                RequestQueue requestQueueUtilisateur = Volley.newRequestQueue(SupportActivity.this);
                requestQueueUtilisateur.add(requeteAuth);
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
