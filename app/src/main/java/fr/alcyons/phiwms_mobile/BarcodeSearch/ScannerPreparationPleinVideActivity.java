package fr.alcyons.phiwms_mobile.BarcodeSearch;

import static android.view.View.GONE;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.DocumentScannerContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PleinVideContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PleinVideLocalisationContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ZoneEtEmplacementContext;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.TableTrace;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ScannerPreparationPleinVideActivity extends ServiceActivity {

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
    PleinVideContexte pleinVideContexte;
    PleinVideLocalisationContexte pleinVideLocalisationContexte;

    // GRAPHIQUE
    EditText contenuCode;
    EditText contenuCodeManuel;
    TextView message;
    TextView compteurScan;
    RelativeLayout LinearResultatFranceMVO;
    ToneGenerator toneGen1;
    AlertDialog alertDialogFranceMVO;

    //MAIL
    String subject = "";
    String body = "";

    //Pour la réception scannée
    LinearLayout LinearReceptionScanne;
    LinearLayout layoutBasic;
    LinearLayout LinearZoneEmplacementReceptionScannee;
    TextView nomZoneReceptionScannee;
    TextView nomEmplacementReceptionScannee;
    ExpandableListView ListViewProduitReceptionScannee;
    fr.alcyons.phiwms_mobile.ListViewAdapters.Produit_ReceptionScanneeAdapter Produit_ReceptionScanneeAdapter;
    Produit_ReceptionPADAdapter receptionPADExpandableAdapter;
    Produit_PreparationScanneeAdapter produitPreparationScanneeAdapter;
    DocumentScannerContext documentScannerContext;
    ZoneEtEmplacementContext zoneEtEmplacementContext;
    LinearLayout LayoutResultatScanProduit;
    TextView referenceProduitReceptionScannee;
    TextView textPresentationRefProduitScannee;
    TextView numeroLotProduitReceptionScannee;
    TextView peremptionProduitReceptionScannee;
    TextView qteProduitReceptionScannee;
    TextView designationProduitReceptionScannee;
    TextView nomListePreparation;
    ImageView ImageSeparateur;
    List<Integer> liste_id_retour_ligne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_search_only);

        stringList = new ArrayList<>();
        bannerTexte = "";
        code = "";
        // INTENT
        liste_id_retour_ligne = intent.getExtras().getIntegerArrayList("liste_id_retour_ligne");
        modeRafale = intent.getExtras().getBoolean("modeRafale");
        modePhoto = intent.getExtras().getBoolean("modePhoto");
        modeCumule = intent.getExtras().getBoolean("modeCumule");
        bouton_suppression = intent.getBooleanExtra("isBoutonSuppressionExistant", false);
        activerTextSuppression = intent.getBooleanExtra("activerTextSuppression", false);
        doitEtreIdentique = intent.getExtras().getBoolean("doitEtreIdentique");
        designation = intent.getExtras().getString("Designation");
        scannerContexteInt = intent.getIntExtra("scannerContexteInt", 0);
        stringList = intent.getStringArrayListExtra("stringList");
        serialisation = intent.getBooleanExtra("serialisation", false);
        conditionnementProduit = intent.getExtras().getInt("ConditionnementProduit");
        int PreparationID = intent.getExtras().getInt("PreparationID");
        //Pour la réception scannée
        int actionId = intent.getIntExtra("ActionId", 0);
        boolean ADH = intent.getBooleanExtra("ADH", false);
        final boolean service_serialisation = intent.getBooleanExtra("serialisation", false);
        List<Integer> liste_id_reliquat = intent.getExtras().getIntegerArrayList("liste_id_reliquat");

        // GRAPHIQUE
        message = (TextView) findViewById(R.id.message);
        message.setVisibility(GONE);
        compteurScan = (TextView) findViewById(R.id.compteurScan);
        compteurScan.setVisibility(GONE);
        if(modeRafale || modeCumule){
            compteurScan.setVisibility(View.VISIBLE);
            if(stringList != null)
            {
                compteurScan.setText(String.valueOf(stringList.size()) + " produit(s) scanné(s)");
            }
            else
            {
                compteurScan.setText("0 produit scanné");
            }
        }
        //toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        //Objet graphique pour la réception scannée
        LinearReceptionScanne = (LinearLayout) findViewById(R.id.LinearReceptionScanne);
        layoutBasic = (LinearLayout) findViewById(R.id.layoutBasic);
        LinearZoneEmplacementReceptionScannee = (LinearLayout) findViewById(R.id.LinearZoneEmplacementReceptionScannee);
        LinearResultatFranceMVO = (RelativeLayout) findViewById(R.id.LinearResultatFranceMVO);
        nomZoneReceptionScannee = (TextView) findViewById(R.id.nomZoneReceptionScannee);
        nomEmplacementReceptionScannee = (TextView) findViewById(R.id.nomEmplacementReceptionScannee);
        nomListePreparation = (TextView) findViewById(R.id.nomListePreparation);
        textPresentationRefProduitScannee = (TextView) findViewById(R.id.textPresentationRefProduitScannee);
        ListViewProduitReceptionScannee = (ExpandableListView) findViewById(R.id.ListViewProduitReceptionScannee);
        contenuCode = (EditText) findViewById(R.id.contenuCode);
        contenuCodeManuel = (EditText) findViewById(R.id.contenuCodeManuel);
        LayoutResultatScanProduit = (LinearLayout) findViewById(R.id.LayoutResultatScanProduit);
        referenceProduitReceptionScannee = (TextView) findViewById(R.id.referenceProduitReceptionScannee);
        numeroLotProduitReceptionScannee = (TextView) findViewById(R.id.numeroLotProduitReceptionScannee);
        peremptionProduitReceptionScannee = (TextView) findViewById(R.id.peremptionProduitReceptionScannee);
        qteProduitReceptionScannee = (TextView) findViewById(R.id.qteProduitReceptionScannee);
        designationProduitReceptionScannee = (TextView) findViewById(R.id.designationProduitReceptionScannee);
        ImageSeparateur = (ImageView) findViewById(R.id.ImageSeparateur);

        //on donne le focus sur l'input
        contenuCode.requestFocus();

        // CONTEXTE
       pleinVideContexte = new PleinVideContexte(this, message);
        zoneEtEmplacementContext = new ZoneEtEmplacementContext(this, db, utilisateurConnecte);
        pleinVideLocalisationContexte = new PleinVideLocalisationContexte(this, db, message);
        documentScannerContext = new DocumentScannerContext(this, db);

        // Initialisation du CONTEXTE
        switch (scannerContexteInt) {
            case R.string.scannerContextePleinVide:
                bannerTexte = intent.getExtras().getString("dotationIntitule");
                pleinVideContexte.stringList = stringList;
                pleinVideContexte.detailDotPleinVide_AdressageList = intent.getExtras().getStringArrayList("detailDotPleinVide_AdressageList");
                break;
            case R.string.scannerContextePleinVideLocalisation:
                bannerTexte = pleinVideLocalisationContexte.bannerTexte;
                break;

            case R.string.scannerContexteService:
                message.setVisibility(GONE);
                compteurScan.setVisibility(GONE);
                bannerTexte = "Scannez un service";
                break;
            default:

                break;
        }
        // Mise à jour GRAPHIQUE
        ((TextView) findViewById(R.id.banner)).setText(bannerTexte);

        if (modeRafale) {
            findViewById(R.id.boutonFermeture).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stringList = pleinVideContexte.stringList;
                    Intent scannerSearchOnlyIntent = new Intent();
                    Bundle scannerSearchOnlyBundle = new Bundle();

                    if(scannerContexteInt == R.string.scannerContextePleinVide)
                    {
                        stringList = pleinVideContexte.stringList;
                    }
                    scannerSearchOnlyBundle.putStringArrayList("listeString", (ArrayList) stringList);
                    scannerSearchOnlyBundle.putBoolean("close", true);
                    scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                    ScannerPreparationPleinVideActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
                    ScannerPreparationPleinVideActivity.this.finish();
                }
            });
            findViewById(R.id.boutonFermeture).setVisibility(View.VISIBLE);
            if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee|| scannerContexteInt == R.string.scannerContexteReceptionPAD)
            {
                findViewById(R.id.boutonFermeture).setVisibility(GONE);
            }
        }
        else if(bouton_suppression)
        {
            if(activerTextSuppression)
            {
                findViewById(R.id.boutonFermeture).setVisibility(GONE);
                ((TextView)findViewById(R.id.textManuelScan)).setText(intent.getExtras().getString("TextBannerManuel"));
                if(scannerContexteInt == R.string.scannerContexteAuthentification)
                {
                    Button boutonFermeture = (Button) findViewById(R.id.boutonFermetureManuel);
                    boutonFermeture.setText("Connexion manuelle");
                }
                findViewById(R.id.layoutBasic).setVisibility(GONE);
                findViewById(R.id.banner).setVisibility(GONE);
                findViewById(R.id.linearScanManuel).setVisibility(View.VISIBLE);
                findViewById(R.id.boutonFermetureManuel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent scannerSearchOnlyIntent = new Intent();
                        Bundle scannerSearchOnlyBundle = new Bundle();
                        scannerSearchOnlyBundle.putString("code", code);
                        scannerSearchOnlyBundle.putBoolean("close", true);
                        scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                        ScannerPreparationPleinVideActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
                        ScannerPreparationPleinVideActivity.this.finish();
                    }
                });
            }
            else
            {

            }
        }

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
                        case R.string.scannerContextePleinVide:
                            pleinVideContexte.onTextWatcher(s);
                            compteurScan.setText(String.valueOf(pleinVideContexte.stringList.size()) + " produit(s) scanné(s)");
                            break;
                        case R.string.scannerContextePleinVideLocalisation:
                            pleinVideLocalisationContexte.onTextWatcher(s);
                            code = pleinVideLocalisationContexte.code;
                            break;
                        default:

                            break;
                    }


                    if(code != null && !code.isEmpty()){
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("code", code);
                        setResult(BarcodeCaptureActivity.RESULT_OK, resultIntent);
                        ScannerPreparationPleinVideActivity.this.finish();
                    }
                    else{
                        contenuCode.getText().clear();
                        contenuCodeManuel.getText().clear();
                    }
                }
            }
        });
        contenuCode.addTextChangedListener(new TextWatcher() {
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
                        case R.string.scannerContextePleinVide:
                            pleinVideContexte.onTextWatcher(s);
                            compteurScan.setText(String.valueOf(pleinVideContexte.stringList.size()) + " produit(s) scanné(s)");
                            break;
                        case R.string.scannerContextePleinVideLocalisation:
                            pleinVideLocalisationContexte.onTextWatcher(s);
                            code = pleinVideLocalisationContexte.code;
                            break;
                        default:
                            break;
                    }


                    if(!code.isEmpty()){
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("code", code);
                        setResult(BarcodeCaptureActivity.RESULT_OK, resultIntent);
                        ScannerPreparationPleinVideActivity.this.finish();
                    }
                    else{
                        contenuCode.getText().clear();
                        contenuCodeManuel.getText().clear();
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent scannerSearchOnlyIntent = new Intent();
        Bundle scannerSearchOnlyBundle = new Bundle();
        scannerSearchOnlyBundle.putString("code","");
        scannerSearchOnlyBundle.putStringArrayList("stringList", (ArrayList) pleinVideContexte.stringList);
        scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
        ScannerPreparationPleinVideActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
        ScannerPreparationPleinVideActivity.this.finish();
    }

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(ScannerPreparationPleinVideActivity.this, email[0], true, db);
            try {
                sender.sendMailVerification(subject, body);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }

    //method to expand all groups
    private void expandAll() {
        int count = Produit_ReceptionScanneeAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            ListViewProduitReceptionScannee.expandGroup(i);
        }
    }

    //method to expand all groups
    private void expandAllPreparation(List<Integer> listIndex) {
        for (int i = 0; i < listIndex.size(); i++) {
            int indexExpand = listIndex.get(i);
            ListViewProduitReceptionScannee.expandGroup(indexExpand);
        }
    }

    //method to expand all groups
    private void expandAllReceptionPAD() {
        int count = receptionPADExpandableAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            ListViewProduitReceptionScannee.expandGroup(i);
        }
    }

    public void afficherAlerteFranceMVO(String produitDesignation, String resultat, String numeroSerie, String motif)
    {
       /* toneGen1.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_SSL,250);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }*/

/*        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ScannerSearchOnlyActivity.this);
        LayoutInflater inflater = ScannerSearchOnlyActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.alerte_france_mvo, null);*/

        TextView DesignationProduitFranceMVO = (TextView) findViewById(R.id.DesignationProduitFranceMVO);
        TextView NumeroSerieFranceMVO = (TextView) findViewById(R.id.NumeroSerieFranceMVO);
        TextView ResultatFranceMVO = (TextView) findViewById(R.id.ResultatFranceMVO);
        TextView MotifFranceMVO = (TextView) findViewById(R.id.MotifFranceMVO);

        DesignationProduitFranceMVO.setText(produitDesignation);
        NumeroSerieFranceMVO.setText(numeroSerie);
        ResultatFranceMVO.setText(resultat);
        MotifFranceMVO.setText(motif);
        LinearResultatFranceMVO.setVisibility(View.VISIBLE);
    }

    public void afficherSnackBar(String message) {
        final InputMethodManager imm = (InputMethodManager)ScannerPreparationPleinVideActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>" + message + "</b>", 0), Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        if(message.contentEquals("Produit préparé"))
        {
            layout.setBackgroundColor(getResources().getColor(R.color.vert3, null));
        }
        else
        {
            layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        }

        if(message.contentEquals("Produit déjà préparé en intégralité"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                v.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }

        TextView textView = (TextView) layout.findViewById(R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.setMargins(0, 50, 0, 0);
        snackBarView.setLayoutParams(params);
        snackbar.show();
    }

    public void GestionAffichage()
    {

    }

    public void onClick_Child_Preparation_Scannee(final ObjetReceptionScannee objetCourant, final int quantiteRestante) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_preparation_uf_adh, null);

        TextView titre = (TextView) view.findViewById(R.id.titre);
        final EditText numeroSerie = (EditText) view.findViewById(R.id.numeroSerie);
        final EditText numeroLot = (EditText) view.findViewById(R.id.numeroLot);
        final TextView datePeremption = (TextView) view.findViewById(R.id.datePeremption);
        final TextView quantite = (TextView) view.findViewById(R.id.quantite);

        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(objetCourant.getGs1_scannee());
        final String gtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
        String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
        String serie= gs1Decoupe.get(OutilsDecodage.numeroSerie);
        String dateString = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
        String[] date_tab = dateString.split("-");
        dateString = date_tab[2]+"/"+date_tab[1]+"/"+date_tab[0];


        final Produit produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
        titre.setText(produit_courant.getDesignation_interne());
        numeroSerie.setText(serie);
        numeroLot.setText(lot);
        datePeremption.setText(dateString);
        quantite.setText(String.valueOf(objetCourant.getQuantiteScannee()));

        /* Gestion des numbers pickers */
        quantite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvre une boite de dialogue avec un NumberPicker
                String title = "Saisir la quantite";
                String message = "Nouvelle quantite : ";
                int maxValue = quantiteRestante;
                int value = objetCourant.getQuantiteScannee();

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = Alerte.aNumberPicker.getValue();
                        quantite.setText(String.valueOf(qteAprès));
                        objetCourant.setQuantiteScannee(qteAprès);
                        InputMethodManager imm = (InputMethodManager) ScannerPreparationPleinVideActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    }
                };

                Alerte.afficherAlerteNumberPicker(ScannerPreparationPleinVideActivity.this, title, message, value, maxValue, onClickListener);
            }
        });

        final Calendar date = Calendar.getInstance();;
        final DatePickerDialog.OnDateSetListener dateRepriseDuDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, monthOfYear);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

                datePeremption.setText(sdf.format(date.getTime()));
            }

        };

        //Récupération de la date
        final String dateMinString = dateString;
        final int year = date.get(Calendar.YEAR);
        final int month = date.get(Calendar.MONTH);
        final int day = date.get(Calendar.DAY_OF_MONTH);


        if(dateString != null && !dateString.contentEquals(""))
        {
            datePeremption.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    DatePickerDialog dateReprisePickerDialog = new DatePickerDialog(ScannerPreparationPleinVideActivity.this, dateRepriseDuDatePicker, year, month, day);

                    dateReprisePickerDialog.show();
                }
            });
        }

        LinearLayout fermerAlerteLinearLayout = (LinearLayout) view.findViewById(R.id.fermerAlerteLinearLayout);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        fermerAlerteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date_tab = datePeremption.getText().toString().split("/");
                String nouvelle_date = date_tab[2].substring(0, 2)+date_tab[1]+date_tab[0];
                String new_gs1 = gtin+"21"+numeroSerie.getText().toString()+"@17"+nouvelle_date+"10"+numeroLot.getText().toString();
                objetCourant.setGs1_scannee(new_gs1);
                alertDialog.dismiss();
                expandGroupAfterQuantite(produit_courant.getDesignation_interne());
            }
        });

    }

    public void expandGroupAfterQuantite(String designationProduit)
    {
        for(int i = 0; i < produitPreparationScanneeAdapter.getGroupCount(); i++)
        {
            String designationCourante = produitPreparationScanneeAdapter.getGroup(i).toString();
            String[] tab_designation_courante = designationCourante.split("-");
            designationCourante = "";
            for(int j = 0;j < tab_designation_courante.length-1; j++)
            {
                designationCourante = designationCourante+" "+tab_designation_courante[j];
            }

            if(designationCourante.startsWith(" "))
                designationCourante = designationCourante.substring(1);

            if(designationProduit.trim().contentEquals(designationCourante.trim()))
            {
                ListViewProduitReceptionScannee.expandGroup(i);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee|| scannerContexteInt == R.string.scannerContexteReceptionPAD || scannerContexteInt == R.string.scannerContextePreparationADH)
            menu.findItem(R.id.menuSave).setVisible(true);
        else
            menu.findItem(R.id.menuSave).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSave);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onBackPressed();
                return true;
            }
        });
        return true;
    }
}
