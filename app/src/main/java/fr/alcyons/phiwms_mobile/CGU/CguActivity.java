package fr.alcyons.phiwms_mobile.CGU;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.phiwms_mobile.R;

public class CguActivity extends AppCompatActivity {

    Button boutonRetour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_cgu);

        boutonRetour = findViewById(R.id.boutonRetourCgu);

        boutonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CguActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
