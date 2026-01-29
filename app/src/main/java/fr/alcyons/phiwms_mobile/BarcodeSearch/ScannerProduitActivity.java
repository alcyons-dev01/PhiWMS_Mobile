package fr.alcyons.phiwms_mobile.BarcodeSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ProduitContexte;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Outils.GS1Parser;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ScannerProduitActivity extends ServiceActivity {
    // INTENT
    String scannerContexte;
    String designation;
    int scannerContexteInt;
    String numerodocument;
    int depotdestinataireid;
    List<String> listGTIN;

    // CONTEXTE
    ProduitContexte produitContexte;

    // GRAPHIQUE
    EditText EditTextScanee;
    TextView numDocTextView;
    TextView depot;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable lectureTerminee = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_produit);

        // INTENT
        intent = ScannerProduitActivity.this.getIntent();
        scannerContexte = intent.getExtras().getString("contexte");
        designation = intent.getExtras().getString("Designation");
        numerodocument = intent.getExtras().getString("numerodocument");
        depotdestinataireid = intent.getExtras().getInt("depotdestinataireid");

        listGTIN = new ArrayList<>();

        // GRAPHIQUE
        EditTextScanee = (EditText) findViewById(R.id.EditTextScanee);
        numDocTextView = (TextView) findViewById(R.id.numDocument);
        depot = (TextView) findViewById(R.id.depot);
        EditTextScanee.setBackground(getResources().getDrawable(R.drawable.background_cadre_vert_fond_noir));

        //Affichage des informations de la préparation
        Depot depotdestinataire = DepotOpenHelper.getDepotParID(db, depotdestinataireid);
        String depotText = intent.getExtras().getString("depotRef");
        if(depotdestinataire != null)
        {
            depotText = depotdestinataire.getNom();
            if (utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depotdestinataire.getStructure().contentEquals("PAD")) {
                depotText = "Patient - " + depotdestinataire.getPAD_IPP();
            }
        }


        depot.setText(depotText);
        if(numerodocument != null)
            numDocTextView.setText("#" + numerodocument);
        else
            numDocTextView.setText("");
        // CONTEXTE
        produitContexte = new ProduitContexte(this,db, false, false, false, true, designation, false);

        //on cache le clavier à chaque fois que l'éditText reprend le focus après l'avoir perdu
        EditTextScanee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) ScannerProduitActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) ScannerProduitActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.requestFocus();

        // Mise à jour GRAPHIQUE
        findViewById(R.id.boutonFermeture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerSearchOnlyIntent = new Intent();
                Bundle scannerSearchOnlyBundle = ScannerProduitActivity.super.getBundle();
                int codeEchangeActivity = 0;

                scannerSearchOnlyBundle.putString("code", "");
                scannerSearchOnlyBundle.putString("numLot", "");
                scannerSearchOnlyBundle.putString("numSerie", "");
                scannerSearchOnlyBundle.putString("datePeremption", "");
                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerProduitActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                ScannerProduitActivity.this.finish();
            }
        });

        findViewById(R.id.scannerMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        findViewById(R.id.scannerMode).setVisibility(View.INVISIBLE);

        EditTextScanee.setShowSoftInputOnFocus(false);

        EditTextScanee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) return;

                final String texteBrut = s.toString();

                // Annule tout déclenchement précédent
                if (lectureTerminee != null) {
                    handler.removeCallbacks(lectureTerminee);
                }

                // Planifie un déclenchement différé
                lectureTerminee = new Runnable() {
                    @Override
                    public void run() {

                        String chaineRetourner = "";
                        GS1Parser.GS1Result result = GS1Parser.parseGS1Code(texteBrut);

                        String code = result.productCode;
                        String lot = result.lotNumber;
                        String date = result.expirationDate;
                        String dateSQLFormat = result.expirationDateSQLFormat;
                        String serie = result.serie;
                        boolean gtin = false;
                        if(!code.contentEquals(""))
                        {
                            chaineRetourner = code;
                            gtin = true;
                        }
                        else
                        {
                            String texteNettoye = texteBrut.replaceAll("\u0000", "");
                            chaineRetourner = texteNettoye;
                            gtin = false;
                        }

                        // Détection d’un GTIN (AI 01) ou d’un retour ligne
                        Intent scannerSearchOnlyIntent = new Intent();
                        Bundle scannerSearchOnlyBundle = ScannerProduitActivity.super.getBundle();
                        int codeEchangeActivity = 0;

                        scannerSearchOnlyBundle.putString("code", chaineRetourner.trim());
                        scannerSearchOnlyBundle.putString("numLot", lot);
                        scannerSearchOnlyBundle.putString("numSerie", serie);
                        scannerSearchOnlyBundle.putString("datePeremption", date);
                        scannerSearchOnlyBundle.putString("datePeremptionSqlFormat", dateSQLFormat);
                        scannerSearchOnlyBundle.putBoolean("gtin", gtin);
                        scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);

                        ScannerProduitActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                        ScannerProduitActivity.this.finish();

                        EditTextScanee.getText().clear();

                        EditTextScanee.setShowSoftInputOnFocus(false);
                    }
                };

                // Lance le traitement 200 ms après la dernière frappe
                handler.postDelayed(lectureTerminee, 200);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            boolean close = data.getBooleanExtra("close", false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.boutonFermeture).performClick();
    }
}
