package fr.alcyons.phimr4.Outils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import fr.alcyons.phimr4.ParametresUtilisateur.ServiceParametreUtilisateurActivity;
import fr.alcyons.phimr4.R;


public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentT = new Intent(TestActivity.this, ServiceParametreUtilisateurActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("utilisateurConnecteID", 0);
                intentT.putExtras(bundle);
                TestActivity.this.startActivity(intentT);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
