package fr.alcyons.phiwms_mobile.BarcodeSearch;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.EmplacementContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ProduitContexte;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.TableTraceOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.TableTrace;

import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ScannerEmplacementActivity  extends ServiceActivity {

    List<String> stringList;
    String bannerTexte;
    String code;

    // INTENT
    boolean modeRafale;
    boolean modePhoto;
    boolean modeCumule;
    boolean activerTextSuppression;
    boolean doitEtreIdentique;
    boolean serialisation;
    boolean bouton_suppression;
    String designation;
    int scannerContexteInt;
    int conditionnementProduit;

    // CONTEXTE
    ProduitContexte produitContexte;
    EmplacementContexte emplacementContexte;

    // GRAPHIQUE
    EditText contenuCodeManuel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_emplacement);

        stringList = new ArrayList<>();
        bannerTexte = "";
        code = "";
        // INTENT
        modeRafale = intent.getExtras().getBoolean("modeRafale");
        modePhoto = intent.getExtras().getBoolean("modePhoto");
        modeCumule = intent.getExtras().getBoolean("modeCumule");
        bouton_suppression = intent.getBooleanExtra("isBoutonSuppressionExistant", false);
        activerTextSuppression = intent.getBooleanExtra("activerTextSuppression", false);
        doitEtreIdentique = intent.getExtras().getBoolean("doitEtreIdentique");
        scannerContexteInt = intent.getIntExtra("scannerContexteInt", 0);
        stringList = intent.getStringArrayListExtra("stringList");
        //Pour la réception scannée
        boolean ADH = intent.getBooleanExtra("ADH", false);

        //Objet graphique pour la réception scannée
        contenuCodeManuel = (EditText) findViewById(R.id.contenuCodeManuel);
        //on donne le focus sur l'input
        contenuCodeManuel.requestFocus();

        // CONTEXTE
        emplacementContexte = new EmplacementContexte(this, db, ADH, utilisateurConnecte);

        // Initialisation du CONTEXTE
            if(scannerContexteInt == R.string.scannerContexteEmplacement)
            {
                bannerTexte = emplacementContexte.bannerTexte;
                if(intent.getExtras().containsKey("designationProduit"))
                {
                    TextView designationProduit_Scan = (TextView) findViewById(R.id.designationProduit_Scan);
                    designationProduit_Scan.setVisibility(View.VISIBLE);
                    designationProduit_Scan.setText(intent.getExtras().getString("designationProduit"));
                }
            }

        ((TextView)findViewById(R.id.textManuelScan)).setText(intent.getExtras().getString("TextBannerManuel"));
        findViewById(R.id.linearScanManuel).setVisibility(View.VISIBLE);
        findViewById(R.id.boutonFermetureManuel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerSearchOnlyIntent = new Intent();
                Bundle scannerSearchOnlyBundle = new Bundle();
                scannerSearchOnlyBundle.putString("code", code);
                scannerSearchOnlyBundle.putBoolean("close", true);
                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerEmplacementActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
                ScannerEmplacementActivity.this.finish();
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
                    if(scannerContexteInt == R.string.scannerContexteEmplacement)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Emplacement", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        code = s.toString().substring(0, s.length() - 1);
                    }


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
        findViewById(R.id.boutonFermetureManuel).performClick();
    }

}
