package fr.alcyons.phiwms_mobile.BarcodeSearch;

import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_PKI.checkApiAsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import fr.alcyons.phiwms_mobile.Inventaire.ListeLotInventaireActivity;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
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
                                        int qteProgress = (int)inventaireLigneTempCourant.getStockPhysique()+1;
                                        ((ProgressBar) findViewById(R.id.progressBarQuantite)).setMax((int)inventaireLigneTempCourant.getStockTheorique());
                                        ((ProgressBar) findViewById(R.id.progressBarQuantite)).setProgress(qteProgress);
                                        ((TextView) findViewById(R.id.designationValidation)).setText(inventaireLigneTempCourant.getDesignation());
                                        ((TextView) findViewById(R.id.quantiteValidation)).setText(String.valueOf(qteProgress));
                                        ((TextView) findViewById(R.id.lotValidation)).setText(lot);
                                        ((TextView) findViewById(R.id.peremptionValidation)).setText(date_peremption_courant);
                                        ((TextView) findViewById(R.id.emplacementValidation)).setText(inventaireLigneTempCourant.getEmplacement());
                                        ((LinearLayout) findViewById(R.id.layoutIconeValidation)).setVisibility(View.VISIBLE);

                                        if(inventaireLigneTempCourant.getStockPhysique() == -1)
                                            inventaireLigneTempCourant.setStockPhysique(1);
                                        else
                                            inventaireLigneTempCourant.setStockPhysique(inventaireLigneTempCourant.getStockPhysique()+1);

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

    public void afficherSnackBar(String message) {
        final InputMethodManager imm = (InputMethodManager) ScannerInventaireActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>" + message + "</b>", 0), Snackbar.LENGTH_LONG);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        if(message.contentEquals("Produit déjà préparé en intégralité"))
        {
            layout.setBackgroundColor(getResources().getColor(R.color.vert3, null));
        }
        else
        {
            layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        }

        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.setMargins(0, 50, 0, 0);
        snackBarView.setLayoutParams(params);
        snackbar.show();

        InputMethodManager imme = (InputMethodManager) ScannerInventaireActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imme.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
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
                Inventaire_Ligne_Temp nouvelInventaireLigneTemp = inventaireLigneTemp;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateDuJour = sdf.format(new Date());
                Random randominventairelignetemp = new Random();
                int inventairelignetempid = randominventairelignetemp.nextInt();
                if(inventairelignetempid > 0)
                    inventairelignetempid= inventairelignetempid*-1;

                nouvelInventaireLigneTemp.set_UID(inventairelignetempid);
                nouvelInventaireLigneTemp.setInventaireDate(dateDuJour);
                nouvelInventaireLigneTemp.setStockPhysique(1);
                nouvelInventaireLigneTemp.setLot(lot);
                nouvelInventaireLigneTemp.setPeremptionDate(peremptionDate);

                Inventaire_Ligne_TempOpenHelper.insererUnInventaire_Ligne_TempEnBDD(db, nouvelInventaireLigneTemp);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, nouvelInventaireLigneTemp.getPhiMR4UUID(), nouvelInventaireLigneTemp.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                ElementASynchroniserOpenHelper.toutSynchroniser(ScannerInventaireActivity.this, db, utilisateurConnecte, false);
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
                ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.VISIBLE);
                emplacement_courant = null;
                alertDialog.dismiss();
            }
        });
    }
}
