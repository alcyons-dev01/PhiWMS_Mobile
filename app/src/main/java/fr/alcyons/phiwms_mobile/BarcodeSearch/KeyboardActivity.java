package fr.alcyons.phiwms_mobile.BarcodeSearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PleinVideContexte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

/**
 * Created by jessica on 07/06/2018.
 */

public class KeyboardActivity extends ServiceActivity {

    List<String> detailDotPleinVide_AdressageList;
    List<String> stringList;

    TextView compteurScan;
    TextView message;
    EditText contenuCode;

    PleinVideContexte pleinVideContexte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        detailDotPleinVide_AdressageList = new ArrayList<>();
        stringList = new ArrayList<>();


        // GRAPHIQUE
        message = (TextView) findViewById(R.id.message);
        message.setVisibility(View.GONE);
        compteurScan = (TextView) findViewById(R.id.compteurScan);

        // INTENT
        Intent intent = getIntent();
        ((TextView) findViewById(R.id.banner)).setText(intent.getExtras().getString("dotationIntitule"));
        detailDotPleinVide_AdressageList = intent.getStringArrayListExtra("detailDotPleinVide_AdressageList");
        stringList = intent.getStringArrayListExtra("stringList");

        // CONTEXTE
        pleinVideContexte = new PleinVideContexte(this,message);
        pleinVideContexte.stringList = stringList;
        pleinVideContexte.detailDotPleinVide_AdressageList = detailDotPleinVide_AdressageList;

        compteurScan.setText(String.valueOf(stringList.size()) + " produit(s) scanné(s)");

        findViewById(R.id.boutonFermeture).setVisibility(View.VISIBLE);
        findViewById(R.id.boutonFermeture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerSearchOnlyIntent = new Intent();
                Bundle scannerSearchOnlyBundle = new Bundle();
                scannerSearchOnlyBundle.putStringArrayList("stringList", (ArrayList) pleinVideContexte.stringList);
                scannerSearchOnlyBundle.putBoolean("close", true);
                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                KeyboardActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
                KeyboardActivity.this.finish();
            }
        });

        contenuCode = (EditText) findViewById(R.id.contenuCode);

        contenuCode.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (event != null) {
                            // if shift key is down, then we want to insert the '\n' char in the TextView;
                            // otherwise, the default action is to send the message.
                            if (!event.isShiftPressed()) {
                                String code = contenuCode.getText().toString();
                                if(!code.contentEquals("")){
                                    pleinVideContexte.onTextWatcher(contenuCode.getText());
                                    compteurScan.setText(String.valueOf(pleinVideContexte.stringList.size()) + " produit(s) scanné(s)");
                                    contenuCode.setText("");
                                }

                                return true;
                            }
                            return false;
                        }

                        return true;
                    }
                });
        contenuCode.requestFocus();
    }

    @Override
    public void onBackPressed() {
        Intent scannerSearchOnlyIntent = new Intent();
        Bundle scannerSearchOnlyBundle = new Bundle();
        scannerSearchOnlyBundle.putStringArrayList("stringList", (ArrayList) pleinVideContexte.stringList);
        scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
        KeyboardActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
        KeyboardActivity.this.finish();
    }
}