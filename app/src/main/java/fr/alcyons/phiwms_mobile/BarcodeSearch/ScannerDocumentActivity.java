package fr.alcyons.phiwms_mobile.BarcodeSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.DocumentScannerContext;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.TableTraceOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.TableTrace;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;


public class ScannerDocumentActivity extends ServiceActivity {

    String code;

    // INTENT
    int scannerContexteInt;

    // CONTEXTE
    DocumentScannerContext documentScannerContext;

    // GRAPHIQUE
    EditText contenuCodeManuel;
    //ToneGenerator toneGen1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_document);

        code = "";

        // GRAPHIQUE
        //toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        //Objet graphique pour la réception scannée
        contenuCodeManuel = (EditText) findViewById(R.id.EditTextScanee);

        //on donne le focus sur l'input
        contenuCodeManuel.requestFocus();

        documentScannerContext = new DocumentScannerContext(this, db);

        //on cache le clavier à chaque fois que l'éditText reprend le focus après l'avoir perdu
        contenuCodeManuel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) ScannerDocumentActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        contenuCodeManuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) ScannerDocumentActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        ((TextView)findViewById(R.id.instruction)).setText(intent.getExtras().getString("TextBannerManuel"));

        findViewById(R.id.boutonFermeture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerSearchOnlyIntent = new Intent();
                Bundle scannerSearchOnlyBundle = new Bundle();
                scannerSearchOnlyBundle.putString("code", code);
                scannerSearchOnlyBundle.putBoolean("close", true);
                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerDocumentActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
                ScannerDocumentActivity.this.finish();
            }
        });


        contenuCodeManuel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean modeTrace = ParametreUtilisateurOpenHelper.getModeTrace(db);
                Random random = new Random();
                String stringint = String.valueOf(random.nextInt());
                int id = Integer.parseInt(stringint.substring(0, 5))*-1;
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                TableTrace tableTrace = null;
                long rowId = 0;
                if (s.toString().endsWith("\n")) {

                    code = "";

                    if(modeTrace)
                    {
                        tableTrace = new TableTrace(id, date, "Context_Document", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                        rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                        if(rowId != -1)
                        {
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                        }
                    }
                    documentScannerContext.onTextWatcher(s);
                    code = documentScannerContext.code;


                    if(code != null && !code.isEmpty()){
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("code", code);
                        setResult(BarcodeCaptureActivity.RESULT_OK, resultIntent);
                        finish();
                    }
                    else{
                        contenuCodeManuel.getText().clear();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent scannerSearchOnlyIntent = new Intent();
        Bundle scannerSearchOnlyBundle = new Bundle();
        scannerSearchOnlyBundle.putString("code", "");
        scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
        ScannerDocumentActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
        ScannerDocumentActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }
}
