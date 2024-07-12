package fr.alcyons.phiwms_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import fr.alcyons.phiwms_mobile.WebView.WebViewActivity;


public class AuthentificationV2Activity extends MainActivity {

    private Button boutonConnexion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent laWebview = new Intent(AuthentificationV2Activity.this, WebViewActivity.class);
        laWebview.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        setContentView(R.layout.activity_authentification);
        boutonConnexion = findViewById(R.id.boutonConnexion);
        boutonConnexion.setOnClickListener(v -> {
            EditText identifiant = findViewById(R.id.identifiant);
            EditText mdp = findViewById(R.id.motDePasse);
            startActivityIfNeeded(laWebview, 0);
            WebViewActivity.authentification(identifiant.getText().toString(), mdp.getText().toString());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}