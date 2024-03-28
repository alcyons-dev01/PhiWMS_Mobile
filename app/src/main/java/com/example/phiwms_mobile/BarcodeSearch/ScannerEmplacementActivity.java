package com.example.phiwms_mobile.BarcodeSearch;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.example.phiwms_mobile.BarcodeSearch.contexte.AuthentificationContext;
import com.example.phiwms_mobile.BarcodeSearch.contexte.ControleDesRetourScanContext;
import com.example.phiwms_mobile.BarcodeSearch.contexte.DocumentScannerContext;
import com.example.phiwms_mobile.BarcodeSearch.contexte.EmplacementContexte;
import com.example.phiwms_mobile.BarcodeSearch.contexte.PleinVideContexte;
import com.example.phiwms_mobile.BarcodeSearch.contexte.PleinVideLocalisationContexte;
import com.example.phiwms_mobile.BarcodeSearch.contexte.PreparationScanneeScanProduitScannerSearchContexte;
import com.example.phiwms_mobile.BarcodeSearch.contexte.ProduitContexte;
import com.example.phiwms_mobile.BarcodeSearch.contexte.ProduitReceptionScanneeScannerSearchContexte;
import com.example.phiwms_mobile.BarcodeSearch.contexte.ReceptionPADScannerContexte;
import com.example.phiwms_mobile.BarcodeSearch.contexte.ServiceContexte;
import com.example.phiwms_mobile.BarcodeSearch.contexte.ZoneEtEmplacementContext;
import com.example.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.TableTraceOpenHelper;
import com.example.phiwms_mobile.Classes.ObjetReceptionPAD;
import com.example.phiwms_mobile.Classes.ObjetReceptionScannee;
import com.example.phiwms_mobile.Classes.PH_Reliquat;
import com.example.phiwms_mobile.Classes.Produit;
import com.example.phiwms_mobile.Classes.TableTrace;
import com.example.phiwms_mobile.ListViewAdapters.Produit_PreparationScanneeAdapter;
import com.example.phiwms_mobile.ListViewAdapters.Produit_ReceptionPADAdapter;
import com.example.phiwms_mobile.ListViewAdapters.Produit_ReceptionScanneeAdapter;
import com.example.phiwms_mobile.Outils.Alerte;
import com.example.phiwms_mobile.Outils.Mail;
import com.example.phiwms_mobile.Outils.OutilsDecodage;
import com.example.phiwms_mobile.R;
import com.example.phiwms_mobile.ServiceActivity;

import static android.view.View.GONE;
import static com.example.phiwms_mobile.Outils.Alerte.aNumberPicker;

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
        switch (scannerContexteInt) {
            case R.string.scannerContexteEmplacement:
                bannerTexte = emplacementContexte.bannerTexte;
                if(intent.getExtras().containsKey("designationProduit"))
                {
                    TextView designationProduit_Scan = (TextView) findViewById(R.id.designationProduit_Scan);
                    designationProduit_Scan.setVisibility(View.VISIBLE);
                    designationProduit_Scan.setText(intent.getExtras().getString("designationProduit"));
                }
                break;
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
                    switch (scannerContexteInt) {
                        case R.string.scannerContexteEmplacement:
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
                            break;
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
        findViewById(R.id.boutonFermetureManuel).performClick();
    }

}
