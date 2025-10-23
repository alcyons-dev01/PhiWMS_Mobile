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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Outils.GS1Parser;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ScannerPlanDePlacementActivity extends ServiceActivity {
    // GRAPHIQUE
    List<Produit> listeProduitScannee;
    EditText EditTextScanee;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable lectureTerminee = null;

    ArrayList<String> listDesignation=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView ListViewDesignation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_plan_de_placement);

        // INTENT
        intent = ScannerPlanDePlacementActivity.this.getIntent();
        // GRAPHIQUE
        EditTextScanee = (EditText) findViewById(R.id.EditTextScanee);
        ListViewDesignation = (ListView) findViewById(R.id.ListViewDesignation);
        EditTextScanee.setBackground(getResources().getDrawable(R.drawable.background_cadre_vert_fond_noir));
        listeProduitScannee = (List<Produit>) intent.getExtras().getSerializable("ListProduitScannes");

        if(listeProduitScannee == null)
            listeProduitScannee = new ArrayList<>();

        ((Button) findViewById(R.id.boutonPlacerReferences)).setText("Placer les " + listeProduitScannee.size() + " références");
        if (listeProduitScannee.size() > 0)
            ((Button) findViewById(R.id.boutonPlacerReferences)).setVisibility(View.VISIBLE);
        else
            ((Button) findViewById(R.id.boutonPlacerReferences)).setVisibility(View.INVISIBLE);

        for(Produit temp : listeProduitScannee)
        {
            listDesignation.add(temp.getDesignation_interne());
        }
        adapter=new ArrayAdapter<String>(this, R.layout.row_string_text_blanc, listDesignation);
        ListViewDesignation.setAdapter(adapter);
        //on cache le clavier à chaque fois que l'éditText reprend le focus après l'avoir perdu
        EditTextScanee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) ScannerPlanDePlacementActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) ScannerPlanDePlacementActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.requestFocus();

        // Mise à jour GRAPHIQUE
        findViewById(R.id.boutonFermeture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerSearchOnlyIntent = new Intent();
                Bundle scannerSearchOnlyBundle = ScannerPlanDePlacementActivity.super.getBundle();
                int codeEchangeActivity = 0;

                scannerSearchOnlyBundle.putSerializable("ListProduitScannes", (Serializable) listeProduitScannee);
                scannerSearchOnlyBundle.putBoolean("placement", false);
                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerPlanDePlacementActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                ScannerPlanDePlacementActivity.this.finish();
            }
        });

        findViewById(R.id.boutonFermeture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerSearchOnlyIntent = new Intent();
                Bundle scannerSearchOnlyBundle = ScannerPlanDePlacementActivity.super.getBundle();
                int codeEchangeActivity = 0;

                if (listeProduitScannee == null)
                    listeProduitScannee = new ArrayList<>();
                scannerSearchOnlyBundle.putSerializable("ListProduitScannes", (Serializable) listeProduitScannee);
                scannerSearchOnlyBundle.putBoolean("placement", false);
                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerPlanDePlacementActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                ScannerPlanDePlacementActivity.this.finish();
            }
        });

        findViewById(R.id.boutonPlacerReferences).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerSearchOnlyIntent = new Intent();
                Bundle scannerSearchOnlyBundle = ScannerPlanDePlacementActivity.super.getBundle();
                int codeEchangeActivity = 0;
                if (listeProduitScannee == null)
                    listeProduitScannee = new ArrayList<>();
                scannerSearchOnlyBundle.putSerializable("ListProduitScannes", (Serializable) listeProduitScannee);
                scannerSearchOnlyBundle.putBoolean("placement", true);
                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerPlanDePlacementActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                ScannerPlanDePlacementActivity.this.finish();
            }
        });

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
                        if(!texteBrut.contentEquals(""))
                        {
                            String chaineRetourner = "";
                            GS1Parser.GS1Result result = GS1Parser.parseGS1Code(texteBrut);

                            String code = result.productCode;
                            if (!code.contentEquals("")) {
                                chaineRetourner = code;
                            } else {
                                chaineRetourner = texteBrut.replaceAll("\u0000", "");
                            }

                            List<Produit> listeProduitIdentifie = ProduitOpenHelper.getProduitsByIdentification(db, chaineRetourner);

                            for(Produit tempIdentifie : listeProduitIdentifie)
                            {
                                boolean trouve = false;

                                for(Produit tempScanne : listeProduitScannee)
                                {
                                    if(tempScanne.getID_produit() == tempIdentifie.getID_produit())
                                    {
                                        trouve = true;
                                        break;
                                    }
                                }

                                if(!trouve)
                                {
                                    if(tempIdentifie.getEmplacement_PUI_Defaut().contentEquals("EMPLACEMENT"))
                                    {
                                        listeProduitScannee.add(tempIdentifie);
                                        listDesignation.add(0, tempIdentifie.getDesignation_interne());
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            ((Button) findViewById(R.id.boutonPlacerReferences)).setText("Placer les " + listeProduitScannee.size() + " références");
                            if (listeProduitScannee.size() > 0)
                                ((Button) findViewById(R.id.boutonPlacerReferences)).setVisibility(View.VISIBLE);
                            else
                                ((Button) findViewById(R.id.boutonPlacerReferences)).setVisibility(View.INVISIBLE);

                            EditTextScanee.getText().clear();

                            EditTextScanee.setShowSoftInputOnFocus(false);
                        }

                    }
                };

                // Lance le traitement 200 ms après la dernière frappe
                handler.postDelayed(lectureTerminee, 200);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.boutonFermeture).performClick();
    }
}

/**
 *03400939592756
 *03400939592527
 *03400939591865
 * 03400930186947
 */