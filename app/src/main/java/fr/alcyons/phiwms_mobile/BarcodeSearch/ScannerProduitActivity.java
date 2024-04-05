package fr.alcyons.phiwms_mobile.BarcodeSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ProduitContexte;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;

import com.example.phiwms_mobile.ControleDesRetours.ListeEmplacementCreationActivity;
import com.example.phiwms_mobile.ControleDesRetours.ListeZoneCreationActivity;
import com.example.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ScannerProduitActivity extends ServiceActivity {
    // INTENT
    String scannerContexte;
    String designation;
    int scannerContexteInt;
    int preparationID;
    List<String> listGTIN;
    public PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotCourant;
    PH_Preparation preparation_courante;


    // CONTEXTE
    ProduitContexte produitContexte;

    // GRAPHIQUE
    EditText EditTextScanee;
    LinearLayout layoutInformations;
    TextView designationProduit;
    TextView referenceProduit;
    TextView quantiteDejaPreparer;
    TextView quantiteProduit;
    TextView EmplacementLotProduit;
    TextView numeroLot;
    TextView datePeremptionLot;
    TextView qteSaisie;
    TextView numPreparation;
    TextView depot;

    //Gestion reception PAD
    ImageView imageValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_produit);

        // INTENT
        intent = ScannerProduitActivity.this.getIntent();
        scannerContexte = intent.getExtras().getString("contexte");
        designation = intent.getExtras().getString("Designation");
        scannerContexteInt = Integer.parseInt(scannerContexte);
        preparationID = intent.getExtras().getInt("preparationId");
        listGTIN = new ArrayList<>();

        // GRAPHIQUE
        EditTextScanee = (EditText) findViewById(R.id.EditTextScanee);
        layoutInformations = (LinearLayout) findViewById(R.id.layoutInformations);
        designationProduit = (TextView) findViewById(R.id.designationProduit);
        referenceProduit = (TextView) findViewById(R.id.referenceProduit);
        quantiteDejaPreparer = (TextView) findViewById(R.id.quantiteDejaPreparer);
        quantiteProduit = (TextView) findViewById(R.id.quantiteProduit);
        EmplacementLotProduit = (TextView) findViewById(R.id.EmplacementLotProduit);
        numeroLot = (TextView) findViewById(R.id.numeroLot);
        datePeremptionLot = (TextView) findViewById(R.id.datePeremptionLot);
        qteSaisie = (TextView) findViewById(R.id.qteSaisie);
        numPreparation = (TextView) findViewById(R.id.numPreparation);
        depot = (TextView) findViewById(R.id.depot);
        EditTextScanee.setBackground(getResources().getDrawable(R.drawable.background_scanner_preparation));
        imageValidation = ((ImageView) findViewById(R.id.imageValidation));

        //Affichage des informations de la préparation
        preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparationID);
        if(preparation_courante != null)
        {
            Depot depotdestinataire = DepotOpenHelper.getDepotParID(db, preparation_courante.getDepotDestinataireID());
            String depotText = depotdestinataire.getNom();
            if (utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depotdestinataire.getStructure().contentEquals("PAD")) {
                depotText = "Patient - " + depotdestinataire.getPAD_IPP();
            }

            depot.setText(depotText);
            numPreparation.setText("#" + preparationID);
        }


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
                Bundle scannerSearchOnlyBundle = new Bundle();
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
                //EditTextScanee.setText("01040462410085001721123110alcyons2\n");
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
                if (s.toString().endsWith("\n")) {
                    produitContexte.onTextWatcher(s);
                    Intent scannerSearchOnlyIntent = new Intent();
                    Bundle scannerSearchOnlyBundle = new Bundle();
                    int codeEchangeActivity = 0;

                    scannerSearchOnlyBundle.putString("code", produitContexte.code);
                    scannerSearchOnlyBundle.putString("numLot", "");
                    scannerSearchOnlyBundle.putString("numSerie", "");
                    scannerSearchOnlyBundle.putString("datePeremption", "");
                    scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                    ScannerProduitActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                    ScannerProduitActivity.this.finish();

                    EditTextScanee.getText().clear();
                }
                EditTextScanee.setShowSoftInputOnFocus(false);
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
    public void onBackPressed()
    {
        findViewById(R.id.boutonFermeture).performClick();
    }
}
