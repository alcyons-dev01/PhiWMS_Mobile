package fr.alcyons.phiwms_mobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MySettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Boolean retourAAuth = getIntent().getBooleanExtra("retourAAuth", false);
        final Intent retourAAuthentificationV2;
        if (retourAAuth){
            retourAAuthentificationV2 = new Intent(MySettingsActivity.this, AuthentificationV2Activity.class);
        }
        else {
            retourAAuthentificationV2 = null;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new MySettingsFragment(retourAAuthentificationV2))
                .commit();

    }
}