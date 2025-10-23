package fr.alcyons.phiwms_mobile.BarcodeSearch;

import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_PKI.checkApiAsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GS1Parser;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ScannerRetourActivity extends ServiceActivity {
    // INTENT
    Depot depotOrigine;
    List<Retour_Ligne> liste_retour_ligne;
    List<String> listGTIN;
    Retour retour_courant;
    List<String> liste_lot;
    String ordreTri;
    Produit produitCourant = null;
    Depot_Emplacement emplacement_courant = null;
    Stock_Lot_Emplacement_Light stock_courant = null;

    // GRAPHIQUE
    EditText EditTextScanee;
    String tempCodeScanne;
    boolean serialisationActive;
    Serialisation serialisation;
    CountDownTimer yourCountDownTimer;
    public int counter;

    boolean checkEmplacementUF = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_retour);

        //SERIALISATION
        serialisation = new Serialisation(ScannerRetourActivity.this, db, utilisateurConnecte);
        checkApiAsync(this).thenAccept(success -> {
            serialisationActive = success;
            if(success)
            {
                ((ImageView) findViewById(R.id.imageLogoFMVO)).setVisibility(View.VISIBLE);
            }
        });

        // INTENT
        intent = ScannerRetourActivity.this.getIntent();
        retour_courant = (Retour) intent.getExtras().getSerializable("RetourCourant");
        liste_retour_ligne = (List<Retour_Ligne>) intent.getExtras().getSerializable("ListeRetourLigne");
        depotOrigine = (Depot) intent.getExtras().getSerializable("DepotOrigine");
        liste_lot = intent.getExtras().getStringArrayList("liste_lot");
        checkEmplacementUF = intent.getExtras().getBoolean("EmplacementUF");
        listGTIN = new ArrayList<>();

        // GRAPHIQUE
        EditTextScanee = (EditText) findViewById(R.id.EditTextScanee);
        EditTextScanee.setBackground(getResources().getDrawable(R.drawable.background_cadre_vert_fond_noir));

        //Affichage des informations de la préparation
        String depotText = depotOrigine.getNom();
        if (utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depotOrigine.getStructure().contentEquals("PAD"))
        {
            depotText = "Patient - " + depotOrigine.getPAD_IPP();
        }
        ((TextView) findViewById(R.id.depot)).setText(depotText);
        ((TextView) findViewById(R.id.numRetour)).setText("#" + retour_courant.getNumero());
        counter = 5;
        //on cache le clavier à chaque fois que l'éditText reprend le focus après l'avoir perdu
        EditTextScanee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) ScannerRetourActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) ScannerRetourActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
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
                    emplacement_courant = null;
                    stock_courant = null;
                }

                codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerRetourActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                ScannerRetourActivity.this.finish();
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
                                if(checkEmplacementUF)
                                {
                                    Depot depotCourant = DepotOpenHelper.getDepotParReference(db, retour_courant.getRef_Depot_Origine());
                                    Depot_Zone zoneCourante = ZoneOpenHelper.getZoneByDepotEtNom(db, depotCourant, produitTemp.getZone_UF_Defaut());
                                    if(zoneCourante != null)
                                        emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zoneCourante, produitTemp.getEmplacement_UF_Defaut());
                                }
                                else
                                {
                                    Depot depotPui = DepotOpenHelper.getDepotPUI(db);
                                    Depot_Zone zoneCourante = ZoneOpenHelper.getZoneByDepotEtNom(db, depotPui, produitTemp.getZone_PUI_Defaut());
                                    if(zoneCourante != null)
                                        emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zoneCourante, produitTemp.getEmplacement_PUI_Defaut());
                                }

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
                        Retour_Ligne ligne_base = null;
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

                            if(emplacement_courant != null && produitCourant != null)
                            {
                                if(stock_courant != null)
                                    verificationEmplacementProduit(emplacement_courant, produitCourant, stock_courant.getEmplacement());
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
                                if(emplacement_courant == null)
                                {
                                    if(checkEmplacementUF)
                                    {
                                        Depot depotCourant = DepotOpenHelper.getDepotParReference(db, retour_courant.getRef_Depot_Origine());
                                        Depot_Zone zoneCourante = ZoneOpenHelper.getZoneByDepotEtNom(db, depotCourant, produitCourant.getZone_UF_Defaut());
                                        if(zoneCourante != null)
                                            emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zoneCourante, produitCourant.getEmplacement_UF_Defaut());
                                    }
                                    else
                                    {
                                        Depot depotPui = DepotOpenHelper.getDepotPUI(db);
                                        Depot_Zone zoneCourante = ZoneOpenHelper.getZoneByDepotEtNom(db, depotPui, produitCourant.getZone_PUI_Defaut());
                                        if(zoneCourante != null)
                                            emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zoneCourante, produitCourant.getEmplacement_PUI_Defaut());
                                    }

                                }

                                if(emplacement_courant != null)
                                {
                                    ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                    ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.VISIBLE);
                                    ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacement_courant.getAdressage().trim());

                                    //on vérifie que le produit courant fait partie de la liste des retour_ligne
                                    boolean produit_present = false;
                                    for(Retour_Ligne ligne : liste_retour_ligne)
                                    {
                                        if(ligne.getCode_produit() == produitCourant.getID_produit())
                                        {
                                            ligne_base = Retour_LigneOpenHelper.getRetourLigneByID(db, ligne.get_UID());
                                            produit_present = true;
                                            break;
                                        }
                                    }

                                    if(!produit_present)
                                    {
                                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.EFFECT_TICK));
                                        }
                                        else
                                        {
                                            v.vibrate(500);
                                        }

                                        ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).setVisibility(View.INVISIBLE);
                                            }
                                        }, 2000);
                                        produitCourant = null;
                                        stock_courant = null;
                                        reinitialisationInterface();
                                    }
                                    else
                                    {
                                        List<Retour_Ligne> retourLigneRetourner = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retour_courant, ligne_base.getCode_produit());
                                        int qte_demander = (int) ligne_base.getQte_Demander();
                                        int qte_retourner = 0;
                                        int qte_restante = 0;
                                        for(Retour_Ligne ligne_temp : retourLigneRetourner)
                                        {
                                            qte_retourner = (int) (qte_retourner + ligne_temp.getQte_Retourner());
                                        }
                                        qte_restante = qte_demander - qte_retourner;

                                        String designationProduit = ligne_base.getProduit_Designation();
                                        String referenceProduit = ligne_base.getProduit_Reference();
                                        String conditionnement = String.valueOf((int)produitCourant.getCond_distrib());
                                        if((int)produitCourant.getCond_distrib() > qte_restante)
                                            conditionnement = String.valueOf(qte_restante);

                                        if(qte_restante == 0)
                                        {
                                            ((LinearLayout) findViewById(R.id.layoutProduitComplet)).setVisibility(View.VISIBLE);
                                            ((TextView) findViewById(R.id.designationComplete)).setText(designationProduit);
                                            ((TextView) findViewById(R.id.quantiteComplete)).setText(qte_retourner+" / "+qte_demander);
                                            ((LinearLayout) findViewById(R.id.layoutProduitComplet)).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((LinearLayout) findViewById(R.id.layoutProduitComplet)).setVisibility(View.INVISIBLE);
                                                    ((TextView) findViewById(R.id.quantiteComplete)).setText("");
                                                    ((TextView) findViewById(R.id.designationComplete)).setText("");
                                                }
                                            }, 2000);
                                            produitCourant = null;
                                            stock_courant = null;
                                            reinitialisationInterface();
                                        }
                                        else
                                        {
                                            boolean lotPresent = false;
                                            for(String lotCourant : liste_lot)
                                            {
                                                if(lotCourant.contentEquals(lot))
                                                {
                                                    lotPresent = true;
                                                    if(!produitCourant.isSerialiser_Reception_Delivrance() && produitCourant.isSuivi_Serialisation()) {
                                                        if(serialisationActive)
                                                        {
                                                            int serialisationUID = (int) Serialisation.Serialisation_Creer(utilisateurConnecte.getId(), "G110", gtin_courant, "GTIN", lot, date_peremption_serialisation, serie, "RETOUR", String.valueOf(retour_courant.get_UID()));
                                                            int serialisationretournerUID = (int) Serialisation.Serialisation_Creer(utilisateurConnecte.getId(), "G121", gtin_courant, "GTIN", lot, date_peremption_serialisation, serie, "RETOUR", String.valueOf(retour_courant.get_UID()));
                                                        }
                                                    }

                                                    break;
                                                }
                                            }

                                            if(!lotPresent)
                                            {
                                                ((LinearLayout) findViewById(R.id.layoutLotAbsent)).setVisibility(View.VISIBLE);
                                                ((LinearLayout) findViewById(R.id.layoutLotAbsent)).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ((LinearLayout) findViewById(R.id.layoutLotAbsent)).setVisibility(View.INVISIBLE);
                                                        reinitialisationInterface();
                                                    }
                                                }, 2500);
                                            }
                                            else
                                            {
                                                stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByLotPeremptionEtDepotEmplacement(db, lot, date_peremption_courant, depotOrigine, emplacement_courant.getAdressage());

                                                if(stock_courant == null)
                                                    stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByLotPeremptionEtDepot(db, lot, date_peremption_courant, depotOrigine);

                                                if(stock_courant == null)
                                                {
                                                    ((LinearLayout) findViewById(R.id.layoutLotAbsent)).setVisibility(View.VISIBLE);
                                                    ((LinearLayout) findViewById(R.id.layoutLotAbsent)).postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ((LinearLayout) findViewById(R.id.layoutLotAbsent)).setVisibility(View.INVISIBLE);
                                                            reinitialisationInterface();
                                                        }
                                                    }, 2500);
                                                }
                                                else
                                                {
                                                    if(emplacement_courant != null)
                                                    {
                                                        if(stock_courant != null)
                                                            verificationEmplacementProduit(emplacement_courant, produitCourant, stock_courant.getEmplacement());
                                                    }
                                                    else
                                                    {
                                                        ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                                        ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.GONE);
                                                        ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.VISIBLE);
                                                        ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacment");
                                                    }

                                                    int qteProgress = qte_retourner+Integer.parseInt(conditionnement);
                                                    ((ProgressBar) findViewById(R.id.progressBarQuantite)).setMax(qte_demander);
                                                    ((ProgressBar) findViewById(R.id.progressBarQuantite)).setProgress(qteProgress);
                                                    ((TextView) findViewById(R.id.designationValidation)).setText(designationProduit);
                                                    ((TextView) findViewById(R.id.quantiteValidation)).setText(qteProgress+" / "+qte_demander);
                                                    ((TextView) findViewById(R.id.lotValidation)).setText(lot);
                                                    ((TextView) findViewById(R.id.peremptionValidation)).setText(date_peremption_courant);
                                                    ((TextView) findViewById(R.id.emplacementValidation)).setText(emplacement_courant.getAdressage());

                                                    if(!serie.contentEquals(""))
                                                    {
                                                        //findViewById(R.id.numeroSerie).setVisibility(View.VISIBLE);
                                                        //((TextView) findViewById(R.id.numeroSerie)).setText(serie);
                                                        findViewById(R.id.layoutSerieValidation).setVisibility(View.VISIBLE);
                                                        ((TextView) findViewById(R.id.serieValidation)).setText(serie);
                                                        if(!stock_courant.getSerie().contentEquals(serie))
                                                        {
                                                            Stock_Lot_Emplacement_Light stockTemp = stock_courant;
                                                            stock_courant = new Stock_Lot_Emplacement_Light(Integer.parseInt(conditionnement), stockTemp.getLot(), stockTemp.getPeremptionDate(), stockTemp.getEmplacement(), stockTemp.getDepot_Reference(), stockTemp.getZone(), stockTemp.getProduit_Code(), 0, serie);
                                                            Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stock_courant);
                                                        }
                                                    }

                                                    //gestion du clic sur le compteur
                                                    int finalQte_restante = qte_restante;
                                                    //gestion de la validation du scan
                                                    Retour_Ligne finalLigne_base = ligne_base;
                                                    ((LinearLayout) findViewById(R.id.layoutIconeValidation)).setVisibility(View.VISIBLE);
                                                    int quantiteSaisie = Integer.parseInt(conditionnement);
                                                    stock_courant.setQte_Preparer(stock_courant.getQte_Preparer()+quantiteSaisie);
                                                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                                    ajoutRetourLigneBDD(finalLigne_base, stock_courant);
                                                    produitCourant = null;
                                                    stock_courant = null;
                                                    reinitialisationInterface();
                                                }
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    ((LinearLayout) findViewById(R.id.layoutScannerEmplacement)).setVisibility(View.VISIBLE);
                                    ((LinearLayout) findViewById(R.id.layoutScannerEmplacement)).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((LinearLayout) findViewById(R.id.layoutScannerEmplacement)).setVisibility(View.INVISIBLE);
                                        }
                                    }, 2500);
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
            }
        }, 1500);
    }

    private void verificationEmplacementProduit(Depot_Emplacement emplacement_courant, Produit produitCourant, String stockEmplacement)
    {
        if(stockEmplacement.contentEquals(""))
        {
            if(checkEmplacementUF)
            {
                if(!emplacement_courant.getAdressage().contentEquals(produitCourant.getEmplacement_UF_Defaut()))
                {
                    afficherAlerteErreurEmplacement(ScannerRetourActivity.this, ScannerRetourActivity.this.getLayoutInflater(), emplacement_courant.getAdressage(), stock_courant, emplacement_courant);
                }
            }
            else
            {
                if(!emplacement_courant.getAdressage().contentEquals(produitCourant.getEmplacement_PUI_Defaut()))
                {
                    afficherAlerteErreurEmplacement(ScannerRetourActivity.this, ScannerRetourActivity.this.getLayoutInflater(), emplacement_courant.getAdressage(), stock_courant, emplacement_courant);
                }
            }
        }
        else if(!stockEmplacement.contentEquals(emplacement_courant.getAdressage()))
        {
            afficherAlerteErreurEmplacement(ScannerRetourActivity.this, ScannerRetourActivity.this.getLayoutInflater(), emplacement_courant.getAdressage(), stock_courant, emplacement_courant);
        }
    }

    public void afficherAlerteErreurEmplacement(Context context, LayoutInflater inflater, String emplacement, Stock_Lot_Emplacement_Light stock_courant, Depot_Emplacement depotEmplacement) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_validation, null);

        LinearLayout buttonOk = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageFin = (TextView) layout.findViewById(R.id.messageFin);
        TextView titre = (TextView) layout.findViewById(R.id.titre);

        titre.setText("Erreur");
        messageFin.setText("L'emplacement scanné ne correspond pas au lot scanné (Emplacement du lot : "+emplacement+") \n Souhaitez-vous effectuer un déplacement de stock ?");
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        /**
         * TODO : action utilisateur déplacement de stock
         */
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Création action utilisateur
                String numeroLot = ((TextView) findViewById(R.id.numeroLot)).getText().toString();
                String datePeremption = ((TextView) findViewById(R.id.datePeremptionLot)).getText().toString();
                Depot depot = DepotOpenHelper.getDepotPUI(db);
                //Stock_Lot_Emplacement_Light stockCourant = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByLotPeremptionEtDepot(db, numeroLot, datePeremption, depot);

                if(stock_courant != null)
                {
                    stock_courant.setEmplacement(depotEmplacement.getAdressage());
                    Depot_Zone zone = ZoneOpenHelper.getUneZoneByID(db, depotEmplacement.getZoneID());
                    stock_courant.setZone(zone.getZoneName());
                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                    Random randomaction = new Random();
                    int actionId = randomaction.nextInt();
                    if(actionId > 0)
                        actionId= actionId*-1;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dateAction =new Date();
                    String date_string = parseFormat.format(dateAction);
                    ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", stock_courant.get_UID(), "", "Deplacement Stock");
                    ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getId(), new_action_utilisateur.getPhiMR4UUID(), DBOpenHelper.ActionsEAS.AJOUT);
                    ElementASynchroniserOpenHelper.toutSynchroniser(ScannerRetourActivity.this, db, utilisateurConnecte, false);
                }
                alertDialog.dismiss();
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //preparationMultipleContext.emplacement_courant = null;
                ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("");
                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.VISIBLE);
                emplacement_courant = null;
                alertDialog.dismiss();
            }
        });
    }

    private void ajoutRetourLigneBDD(Retour_Ligne retourLigneBase, Stock_Lot_Emplacement_Light stock_courant) {
        //on regarde si un reliquat existe déjà avec ces informations
        List<Retour_Ligne> retourLigneListe = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retour_courant, retourLigneBase.getCode_produit());
        Retour_Ligne retourLigneTemp  = retourLigneBase;

        boolean existe = false;
        for(Retour_Ligne retourligne : retourLigneListe)
        {
            String datePeremption = stock_courant.getPeremptionDate();
            String[] datePeremptionTab = datePeremption.split("/");
            if (datePeremptionTab.length == 3)
                datePeremption = datePeremptionTab[2] + "-" + datePeremptionTab[1] + "-" + datePeremptionTab[0];

            if(retourligne.getLot_Retourner().trim().contentEquals(stock_courant.getLot()) && retourligne.getPeremptionDate().trim().contentEquals(datePeremption))
            {
                retourLigneTemp = retourligne;
                existe = true;
            }
        }

        if(existe)
        {
            int quantite = stock_courant.getQte_Preparer();

            /**
             * MAJ retourLigne
             */
            retourLigneTemp.setQte_Retourner(quantite);
            long rowID = Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigneTemp);
        }
        else
        {
            String numeroLot = stock_courant.getLot();
            String datePeremption = stock_courant.getPeremptionDate();
            String[] datePeremptionTab = datePeremption.split("/");
            if (datePeremptionTab.length == 3)
                datePeremption = datePeremptionTab[2] + "-" + datePeremptionTab[1] + "-" + datePeremptionTab[0];

            String zoneName = stock_courant.getZone();
            String emplacementName = stock_courant.getEmplacement();
            String numero_Serie = stock_courant.getSerie();
            int quantite = stock_courant.getQte_Preparer();
            Random randomretourLigne = new Random();
            int retourLigneId = randomretourLigne.nextInt();
            if (retourLigneId > 0)
                retourLigneId = retourLigneId * -1;

            retourLigneTemp.set_UID(retourLigneId);
            retourLigneTemp.setLot_Retourner(numeroLot.trim());
            retourLigneTemp.setSerie_Retourner(numero_Serie.trim());
            retourLigneTemp.setPeremptionDate(datePeremption.trim());

            retourLigneTemp.setRetourPUI_Zone(zoneName.trim());
            retourLigneTemp.setRetourPUI_Emplacement(emplacementName.trim());
            retourLigneTemp.setQte_Retourner(quantite);

            long rowID = Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, retourLigneTemp);
            if (rowID != -1) {

            }
        }
    }
}

/*
01036608121365321726113010F7UM244 //inconnu
01040393611559951726113010F7UM244
 */