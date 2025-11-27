package fr.alcyons.phiwms_mobile.BarcodeSearch;

import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_PKI.checkApiAsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockUtilisesOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Inventaire;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.StockUtilises;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeEmplacementCreationActivity;
import fr.alcyons.phiwms_mobile.Inventaire.CreationLotManuelleActivity;
import fr.alcyons.phiwms_mobile.Inventaire.ListeLotInventaireActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.InventaireLigneTempAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.DecodageDataAlcyons;
import fr.alcyons.phiwms_mobile.Outils.GS1Parser;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ScannerInventaireActivity extends ServiceActivity {
    // INTENT
    int inventaireID;
    List<Inventaire_Ligne_Temp> inventaireLigneTempList;
    Inventaire inventaireCourant;
    Produit produitCourant = null;
    String zoneCourante = "";
    // GRAPHIQUE
    EditText EditTextScanee;
    String tempCodeScanne;
    public int counter;

    Depot_Emplacement emplacement_courant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_inventaire);

        // INTENT
        intent = ScannerInventaireActivity.this.getIntent();
        inventaireID = intent.getExtras().getInt("inventaireID");
        inventaireLigneTempList = (List<Inventaire_Ligne_Temp>) intent.getExtras().getSerializable("inventaireLigneTempList");

        // GRAPHIQUE
        EditTextScanee = (EditText) findViewById(R.id.EditTextScanee);
        EditTextScanee.setBackground(getResources().getDrawable(R.drawable.background_cadre_vert_fond_noir));

        //Affichage des informations de la préparation
        inventaireCourant = InventaireOpenHelper.getInventaireById(db, inventaireID);
        String depotText = inventaireCourant.getObjet();

        ((TextView) findViewById(R.id.depot)).setText(depotText);
        ((TextView) findViewById(R.id.numInventaire)).setText("#" + inventaireID);
        counter = 5;
        //on cache le clavier à chaque fois que l'éditText reprend le focus après l'avoir perdu
        EditTextScanee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) ScannerInventaireActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) ScannerInventaireActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
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

                if(!((TextView) findViewById(R.id.quantiteValidation)).getText().toString().contentEquals(""))
                {
                    produitCourant = null;
                }

                codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerInventaireActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                ScannerInventaireActivity.this.finish();
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
                if (s.toString().endsWith("\n")) {
                    String codeScanne = s.toString().substring(0, s.length() - 1);

                    if((codeScanne.startsWith("01") || codeScanne.startsWith("02")) && codeScanne.length() == 16)
                    {
                        tempCodeScanne = codeScanne;

                        Produit produitTemp = ProduitOpenHelper.getUnProduitParGTIN(db, codeScanne);
                        if(produitTemp == null)
                            produitTemp = ProduitOpenHelper.getUnProduitParGTIN(db, codeScanne.substring(2));

                        if(produitTemp != null)
                        {
                            if(emplacement_courant == null)
                            {
                                Depot depotPui = DepotOpenHelper.getDepotPUI(db);
                                Depot_Zone zoneCourante = ZoneOpenHelper.getZoneByDepotEtNom(db, depotPui, produitTemp.getZone_PUI_Defaut());
                                if(zoneCourante != null)
                                    emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zoneCourante, produitTemp.getEmplacement_PUI_Defaut());
                            }
                            if(emplacement_courant != null) {
                                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.VISIBLE);
                                ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacement_courant.getAdressage().trim());
                            }
                        }

                        ((TextView) findViewById(R.id.instruction)).setText("Scannez la deuxième partie du code scindé");
                    }
                    else if(tempCodeScanne != null && !tempCodeScanne.contentEquals("") && !codeScanne.startsWith("01") && !codeScanne.startsWith("02"))
                    {
                        tempCodeScanne = tempCodeScanne +  codeScanne;
                        EditTextScanee.setText(tempCodeScanne+"\n");
                        tempCodeScanne = "";
                    }
                    else
                    {
                        ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                        tempCodeScanne = "";
                        String lot = "";
                        String serie;
                        String gtin_courant = "";
                        String gtin_courant_sans_ai = "";
                        String date_peremption_courant = "";
                        String date_peremption_serialisation = "";
                        String expiration_date_sql = "";
                        PH_Preparation_Ligne ligne_base = null;
                        if(codeScanne.startsWith("PHITAGPLACE"))
                        {
                            serie = "";
                            String[] tab_emplacement = codeScanne.split(":");
                            int emplacement_uid = Integer.parseInt(tab_emplacement[tab_emplacement.length-1]);

                            emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_uid);

                            if(emplacement_courant != null)
                            {
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.VISIBLE);
                                ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacement_courant.getAdressage().trim());
                                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                ((LinearLayout) findViewById(R.id.layoutScannerEmplacementInconnu)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.layoutScannerEmplacementInconnu)).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((LinearLayout) findViewById(R.id.layoutScannerEmplacementInconnu)).setVisibility(View.INVISIBLE);
                                    }
                                }, 2500);
                            }
                        }
                        else if(codeScanne.startsWith("PHITAGTIN"))
                        {
                            DecodageDataAlcyons.DecodageDataAlcyonsResult result = DecodageDataAlcyons.parseDataAlcyons(codeScanne, db);
                            Inventaire_Ligne_Temp inventaireLigneTempCourant = null;
                            for(Inventaire_Ligne_Temp inventaireLigneTemp : inventaireLigneTempList)
                            {
                                if(inventaireLigneTemp.getProduitID() == result.productCode)
                                {
                                    inventaireLigneTempCourant = inventaireLigneTemp;
                                    break;
                                }
                            }

                            if(inventaireLigneTempCourant == null)
                            {
                                ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).setVisibility(View.INVISIBLE);
                                    }
                                }, 2000);
                                produitCourant = null;
                                reinitialisationInterface();
                            }
                            else
                            {
                                produitCourant = ProduitOpenHelper.getProduitByID(db, result.productCode);
                                AffichageAlerteSaisieLot(ScannerInventaireActivity.this, getLayoutInflater(), inventaireLigneTempCourant);
                            }
                        }
                        else if(codeScanne.startsWith("PHITAGREF"))
                        {
                            DecodageDataAlcyons.DecodageDataAlcyonsResult result = DecodageDataAlcyons.parseDataAlcyons(codeScanne, db);
                            Inventaire_Ligne_Temp inventaireLigneTempCourant = null;
                            for(Inventaire_Ligne_Temp inventaireLigneTemp : inventaireLigneTempList)
                            {
                                if(inventaireLigneTemp.getProduitID() == result.productCode)
                                {
                                    inventaireLigneTempCourant = inventaireLigneTemp;
                                    break;
                                }
                            }

                            if(inventaireLigneTempCourant == null)
                            {
                                ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).setVisibility(View.INVISIBLE);
                                    }
                                }, 2000);
                                produitCourant = null;
                                reinitialisationInterface();
                            }
                            else
                            {
                                produitCourant = ProduitOpenHelper.getProduitByID(db, result.productCode);
                                AffichageAlerteSaisieLot(ScannerInventaireActivity.this, getLayoutInflater(), inventaireLigneTempCourant);
                            }
                        }
                        else
                        {
                            GS1Parser.GS1Result result = GS1Parser.parseGS1Code(codeScanne);

                            if (!result.productCode.contentEquals(""))
                            {
                                //on récupère les informations du découpage du GS1
                                lot = result.lotNumber;
                                serie = result.serie;
                                gtin_courant = "01"+result.productCode;
                                gtin_courant_sans_ai = result.productCode;
                                date_peremption_courant = result.expirationDateAffichage;
                                date_peremption_serialisation = result.expirationDate;
                                expiration_date_sql = result.expirationDateSQLFormat;

                                //récucpération du produit avec le GTIN
                                List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gtin_courant);

                                //Si la liste est vide on essaye avec le GTIN sans AI
                                if(produits.isEmpty())
                                {
                                    produits = ProduitOpenHelper.getProduitsParGTIN(db, gtin_courant_sans_ai);
                                }

                                //Si la liste n'est pas vide on réupère le produit courant
                                if(!produits.isEmpty())
                                    produitCourant = produits.get(0);
                            }
                            else
                            {
                                serie = "";
                                //on essaye de récupérer via le code inconnu
                                List<Produit> produits  = ProduitOpenHelper.getProduitByCodeInconnu(db, s.toString().substring(0, s.length()-1));
                                if (produits.size() == 1) {
                                    produitCourant = produits.get(0);
                                }
                            }

                            if(produitCourant != null)
                            {
                                boolean produitPresent = false;

                                for(Inventaire_Ligne_Temp inventaireLigneTemp : inventaireLigneTempList)
                                {
                                    if(inventaireLigneTemp.getProduitID() == produitCourant.getID_produit())
                                    {
                                        produitPresent = true;
                                        break;
                                    }
                                }

                                if(produitPresent)
                                {
                                    boolean lotPresent = false;
                                    Inventaire_Ligne_Temp inventaireLigneTempCourant = null;
                                    for(Inventaire_Ligne_Temp inventaireLigneTemp : inventaireLigneTempList)
                                    {
                                        if(inventaireLigneTemp.getLot().contentEquals(lot) && inventaireLigneTemp.getPeremptionDate().contentEquals(expiration_date_sql))
                                        {
                                            inventaireLigneTempCourant = inventaireLigneTemp;
                                            lotPresent = true;
                                            break;
                                        }
                                    }

                                    if(lotPresent)
                                    {
                                        if(inventaireLigneTempCourant.getStockPhysique() == -1 || inventaireLigneTempCourant.getInventaireDate().contentEquals("") || inventaireLigneTempCourant.getInventaireDate().contentEquals("null") || inventaireLigneTempCourant.getInventaireDate().contentEquals("0000-00-00"))
                                            inventaireLigneTempCourant.setStockPhysique(1);
                                        else
                                            inventaireLigneTempCourant.setStockPhysique(inventaireLigneTempCourant.getStockPhysique()+1);

                                        int qteProgress = (int)inventaireLigneTempCourant.getStockPhysique();
                                        ((ProgressBar) findViewById(R.id.progressBarQuantite)).setMax((int)inventaireLigneTempCourant.getStockTheorique());
                                        ((ProgressBar) findViewById(R.id.progressBarQuantite)).setProgress(qteProgress);
                                        ((TextView) findViewById(R.id.designationValidation)).setText(inventaireLigneTempCourant.getDesignation());
                                        ((TextView) findViewById(R.id.quantiteValidation)).setText(String.valueOf(qteProgress));
                                        ((TextView) findViewById(R.id.lotValidation)).setText(lot);
                                        ((TextView) findViewById(R.id.peremptionValidation)).setText(date_peremption_courant);
                                        ((TextView) findViewById(R.id.emplacementValidation)).setText(inventaireLigneTempCourant.getEmplacement());
                                        ((LinearLayout) findViewById(R.id.layoutIconeValidation)).setVisibility(View.VISIBLE);

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        String dateDuJour = sdf.format(new Date());
                                        inventaireLigneTempCourant.setInventaireDate(dateDuJour);
                                        Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(db, inventaireLigneTempCourant);
                                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, inventaireLigneTempCourant.getPhiMR4UUID(), inventaireLigneTempCourant.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                                        ElementASynchroniserOpenHelper.toutSynchroniser(ScannerInventaireActivity.this, db, utilisateurConnecte, false);
                                        reinitialisationInterface();
                                    }
                                    else
                                    {
                                        Inventaire_Ligne_Temp inventaireLigneTempBase = null;
                                        for(Inventaire_Ligne_Temp inventaireLigneTemp : inventaireLigneTempList)
                                        {
                                            if(inventaireLigneTemp.getProduitID() == produitCourant.getID_produit())
                                            {
                                                inventaireLigneTempCourant = inventaireLigneTemp;
                                                break;
                                            }
                                        }

                                        afficherAlerteNouvelleLigne(ScannerInventaireActivity.this, getLayoutInflater(), inventaireLigneTempCourant, lot, expiration_date_sql);
                                    }
                                }
                                else
                                {
                                    ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).setVisibility(View.VISIBLE);
                                    ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).setVisibility(View.INVISIBLE);
                                        }
                                    }, 2000);
                                    produitCourant = null;
                                    reinitialisationInterface();
                                }
                            }
                            else
                            {
                                ((LinearLayout) findViewById(R.id.layoutProduitInconnu)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.layoutProduitInconnu)).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((LinearLayout) findViewById(R.id.layoutProduitInconnu)).setVisibility(View.INVISIBLE);
                                    }
                                }, 3000);
                                produitCourant = null;
                                reinitialisationInterface();
                            }

                        }
                    }

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

            switch (requestCode)
            {
                case CodesEchangesActivites.RESULT_ZONE:
                    int zoneid = data.getExtras().getInt("zoneid");
                    if(zoneid != -1)
                    {
                        Intent newIntent = new Intent(ScannerInventaireActivity.this, ListeEmplacementCreationActivity.class);
                        Bundle extras = ScannerInventaireActivity.super.getBundle();
                        extras.putInt("zoneid", zoneid);
                        newIntent.putExtras(extras);
                        ScannerInventaireActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                    }
                    break;

                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    int emplacementid = data.getExtras().getInt("emplacementId");
                    if(emplacementid != -1)
                    {

                    }
                    break;
            }
            boolean close = data.getBooleanExtra("close", false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.boutonFermeture).performClick();
    }
    private void reinitialisationInterface()
    {
        ((LinearLayout) findViewById(R.id.layoutIconeValidation)).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout) findViewById(R.id.layoutIconeValidation)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.designationValidation)).setText("");
                ((TextView) findViewById(R.id.quantiteValidation)).setText("");
                ((TextView) findViewById(R.id.lotValidation)).setText("");
                ((TextView) findViewById(R.id.peremptionValidation)).setText("");
                ((TextView) findViewById(R.id.emplacementValidation)).setText("");
                ((TextView) findViewById(R.id.serieValidation)).setText("");
                produitCourant = null;
            }
        }, 1500);
    }


    public void afficherAlerteNouvelleLigne(Context context, LayoutInflater inflater, Inventaire_Ligne_Temp inventaireLigneTemp, String lot, String peremptionDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_validation, null);

        LinearLayout buttonOk = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageFin = (TextView) layout.findViewById(R.id.messageFin);
        TextView titre = (TextView) layout.findViewById(R.id.titre);

        titre.setText("Attention");
        messageFin.setText("Le lot n'est pas présent dans l'inventaire, souhaitez-vous l'ajouter ?");
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Création action utilisateur
                Inventaire_Ligne_Temp nouvelInventaireLigneTemp = new Inventaire_Ligne_Temp(inventaireLigneTemp);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateDuJour = sdf.format(new Date());
                Random randominventairelignetemp = new Random();
                int inventairelignetempid = randominventairelignetemp.nextInt();
                if(inventairelignetempid > 0)
                    inventairelignetempid= inventairelignetempid*-1;

                nouvelInventaireLigneTemp.set_UID(inventairelignetempid);
                nouvelInventaireLigneTemp.setInventaireDate(dateDuJour);
                nouvelInventaireLigneTemp.setStockPhysique(1);
                nouvelInventaireLigneTemp.setStockTheorique(1);
                nouvelInventaireLigneTemp.setLot(lot);
                nouvelInventaireLigneTemp.setPeremptionDate(peremptionDate);

                Inventaire_Ligne_TempOpenHelper.insererUnInventaire_Ligne_TempEnBDD(db, nouvelInventaireLigneTemp);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, nouvelInventaireLigneTemp.getPhiMR4UUID(), nouvelInventaireLigneTemp.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                ElementASynchroniserOpenHelper.toutSynchroniser(ScannerInventaireActivity.this, db, utilisateurConnecte, false);
                inventaireLigneTempList.add(nouvelInventaireLigneTemp);
                alertDialog.dismiss();

                ((ProgressBar) findViewById(R.id.progressBarQuantite)).setMax(1);
                ((ProgressBar) findViewById(R.id.progressBarQuantite)).setProgress(1);
                ((TextView) findViewById(R.id.designationValidation)).setText(inventaireLigneTemp.getDesignation());
                ((TextView) findViewById(R.id.quantiteValidation)).setText(String.valueOf(1));
                ((TextView) findViewById(R.id.lotValidation)).setText(lot);
                ((TextView) findViewById(R.id.peremptionValidation)).setText(peremptionDate);
                ((TextView) findViewById(R.id.emplacementValidation)).setText(inventaireLigneTemp.getEmplacement());
                ((LinearLayout) findViewById(R.id.layoutIconeValidation)).setVisibility(View.VISIBLE);
                reinitialisationInterface();
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //preparationMultipleContext.emplacement_courant = null;
                ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("");
                emplacement_courant = null;
                alertDialog.dismiss();
            }
        });
    }
    public void AffichageAlerteSaisieLot(Context context, LayoutInflater inflater, Inventaire_Ligne_Temp courant) {
        Produit produitCourant = ProduitOpenHelper.getProduitByID(db, courant.getProduitID());
        final int[] conditionnement = {produitCourant.getCond_achat()};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_inventaire_comptage, null);

        TextView designationReference_TV = view.findViewById(R.id.designationReference_TV);
        TextView zoneLot_TV = view.findViewById(R.id.zoneLot_TV);
        TextView emplacementLot_TV = view.findViewById(R.id.emplacementLot_TV);
        TextView textCartonFermer_TV = view.findViewById(R.id.textCartonFermer_TV);
        EditText numeroLot_ET = view.findViewById(R.id.numeroLot_ET);
        EditText quantiteComptee_ET = view.findViewById(R.id.quantiteComptee_ET);
        LinearLayout layoutCartonFermer_LL = view.findViewById(R.id.layoutCartonFermer_LL);
        LinearLayout layoutCartonOuvert_LL = view.findViewById(R.id.layoutCartonOuvert_LL);
        LinearLayout layoutMoins_LL = view.findViewById(R.id.layoutMoins_LL);
        LinearLayout layoutPlus_LL = view.findViewById(R.id.layoutPlus_LL);
        LinearLayout layoutValider_LL = view.findViewById(R.id.layoutValider_LL);
        LinearLayout layout_gestion_conditionnement_LL = view.findViewById(R.id.layout_gestion_conditionnement_LL);
        ImageView quitterModale_IV = view.findViewById(R.id.quitterModale_IV);

        Spinner spinnerMoisDatePeremption_SP = view.findViewById(R.id.selecteurDateMois_SP);
        ArrayAdapter<String> adapterMoisPeremption = new ArrayAdapter<>(ScannerInventaireActivity.this, R.layout.spinner_date_item, getListeMoisDatePicker());
        adapterMoisPeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoisDatePeremption_SP.setAdapter(adapterMoisPeremption);

        Spinner spinnerAnneeDatePeremption_SP = view.findViewById(R.id.selecteurDateAnnee_SP);
        ArrayAdapter<String> adapterAnneePeremption = new ArrayAdapter<>(ScannerInventaireActivity.this, R.layout.spinner_date_item, getListeAnneeDatePicker());
        adapterAnneePeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnneeDatePeremption_SP.setAdapter(adapterAnneePeremption);
        spinnerAnneeDatePeremption_SP.setSelection(3);

        designationReference_TV.setText(courant.getDesignation());
        zoneLot_TV.setText(courant.getZone());
        emplacementLot_TV.setText(courant.getEmplacement());
        numeroLot_ET.setText("");
        quantiteComptee_ET.setText("0");
        textCartonFermer_TV.setText(textCartonFermer_TV.getText()+"(x"+produitCourant.getCond_achat()+")");

        if(produitCourant.getCond_achat() == 1)
        {
            layout_gestion_conditionnement_LL.setVisibility(View.GONE);
        }

        layoutCartonFermer_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conditionnement[0] = produitCourant.getCond_achat();
                layoutCartonOuvert_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ScannerInventaireActivity.this, R.color.blanc)));
                layoutCartonFermer_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ScannerInventaireActivity.this, R.color.vertTransparent)));
            }
        });

        layoutCartonOuvert_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conditionnement[0] = 1;
                layoutCartonOuvert_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ScannerInventaireActivity.this, R.color.vertTransparent)));
                layoutCartonFermer_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ScannerInventaireActivity.this, R.color.blanc)));
            }
        });

        layoutPlus_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qteActuelle = Integer.parseInt(quantiteComptee_ET.getText().toString());
                qteActuelle += conditionnement[0];
                quantiteComptee_ET.setText(String.valueOf(qteActuelle));
            }
        });

        layoutMoins_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qteActuelle = Integer.parseInt(quantiteComptee_ET.getText().toString());
                qteActuelle -= conditionnement[0];
                if(qteActuelle < 0)
                    qteActuelle = 0;
                quantiteComptee_ET.setText(String.valueOf(qteActuelle));
            }
        });

        builder.setView(view);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        layoutValider_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vérification des données saisies
                String numeroLotTemp = numeroLot_ET.getText().toString().trim();
                String zoneTemp = zoneLot_TV.getText().toString();
                String emplacementTemp = emplacementLot_TV.getText().toString();
                boolean donneesOk = false;

                //gestion de la date de péremption avec le spinner
                String anneeSelection = spinnerAnneeDatePeremption_SP.getSelectedItem().toString();
                String moisSelection = spinnerMoisDatePeremption_SP.getSelectedItem().toString();
                String dateExpirationLotTemp = getDateDepuisMoisAnnee(moisSelection, anneeSelection);

                if(produitCourant.isSuivi_Lot() && !numeroLotTemp.contentEquals("") && !dateExpirationLotTemp.contentEquals(""))
                {
                    donneesOk = true;
                }
                else if(!produitCourant.isSuivi_Lot())
                {
                    donneesOk = true;
                    numeroLotTemp = "LOT NON TRACE";
                    dateExpirationLotTemp = "0000-00-00";
                }

                if(donneesOk)
                {
                    //vérification du numéro de lot et de la date de péremption
                    Inventaire_Ligne_Temp inventaireLigneTemp = null;
                    String[] dateParts = dateExpirationLotTemp.split("/");
                    if(dateParts.length == 3)
                        dateExpirationLotTemp = dateParts[2]+"-"+dateParts[1]+"-"+dateParts[0];

                    for(Inventaire_Ligne_Temp inventaire_ligne_temp : inventaireLigneTempList)
                    {
                        if(inventaire_ligne_temp.getLot().contentEquals(numeroLotTemp) && inventaire_ligne_temp.getPeremptionDate().contentEquals(dateExpirationLotTemp) && inventaire_ligne_temp.getZone().contentEquals(zoneTemp) && inventaire_ligne_temp.getEmplacement().contentEquals(emplacementTemp))
                        {
                            inventaireLigneTemp = inventaire_ligne_temp;
                            break;
                        }
                    }

                    if(inventaireLigneTemp != null)
                    {
                        inventaireLigneTemp.setStockPhysique(Integer.parseInt(quantiteComptee_ET.getText().toString()));
                        inventaireLigneTemp.setInventaireDate(getDateDuJour());
                        Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(db, inventaireLigneTemp);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, inventaireLigneTemp.getPhiMR4UUID(), inventaireLigneTemp.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                    }
                    else
                    {
                        inventaireLigneTemp = new Inventaire_Ligne_Temp(courant);
                        inventaireLigneTemp.set_UID(getIdInventaireLigneTemp());
                        inventaireLigneTemp.setZone(zoneTemp);
                        inventaireLigneTemp.setEmplacement(emplacementTemp);
                        inventaireLigneTemp.setLot(numeroLotTemp);
                        inventaireLigneTemp.setPeremptionDate(dateExpirationLotTemp);
                        inventaireLigneTemp.setStockPhysique(Integer.parseInt(quantiteComptee_ET.getText().toString()));
                        inventaireLigneTemp.setInventaireDate(getDateDuJour());
                        Inventaire_Ligne_TempOpenHelper.insererUnInventaire_Ligne_TempEnBDD(db, inventaireLigneTemp);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, inventaireLigneTemp.getPhiMR4UUID(), inventaireLigneTemp.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                    }

                    ElementASynchroniserOpenHelper.toutSynchroniser(ScannerInventaireActivity.this, db, utilisateurConnecte, false);
                    alertDialog.dismiss();
                }
                else
                {
                    Alerte.afficherAlerteInformation(ScannerInventaireActivity.this, getLayoutInflater(), "Données incorrectes", "Veuillez saisir un numéro de lot et une date de péremption valide", false, false);
                }

            }
        });

        quitterModale_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }


    private int getIdInventaireLigneTemp()
    {
        Random randominventairelignetemp = new Random();
        int inventairelignetempid = randominventairelignetemp.nextInt();
        if(inventairelignetempid > 0)
            inventairelignetempid= inventairelignetempid*-1;

        return inventairelignetempid;
    }

    private String getDateDuJour()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateDuJour = sdf.format(new Date());

        return dateDuJour;
    }
}
