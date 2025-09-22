package fr.alcyons.phiwms_mobile.BarcodeSearch;

import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_PKI.checkApiAsync;
import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_SINGLE_PACK.serialisationVerificationSingle;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeEmplacementCreationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ScannerPreparation2025_V2Activity  extends ServiceActivity {
    // INTENT
    int preparationID;
    int ph_preparation_ligne_id;
    List<PH_Preparation_Ligne> liste_ph_preparation_ligne;
    List<String> listGTIN;
    PH_Preparation ph_preparation_courante;
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
    LinearLayout layoutValidationAutomatique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_preparation);

        //SERIALISATION
        serialisation = new Serialisation(ScannerPreparation2025_V2Activity.this, db, utilisateurConnecte);
        checkApiAsync(this).thenAccept(success -> {
            serialisationActive = success;
            if(success)
            {
                ((ImageView) findViewById(R.id.imageLogoFMVO)).setVisibility(View.VISIBLE);
            }
        });

        // INTENT
        intent = ScannerPreparation2025_V2Activity.this.getIntent();
        preparationID = intent.getExtras().getInt("preparationId");
        ph_preparation_ligne_id = intent.getExtras().getInt("preparationLigneId");
        liste_ph_preparation_ligne = (List<PH_Preparation_Ligne>) intent.getExtras().getSerializable("liste_ph_preparation_ligne");
        liste_lot = intent.getExtras().getStringArrayList("liste_lot");
        ordreTri = intent.getExtras().getString("ordreTri");
        listGTIN = new ArrayList<>();
//010340095567941717260630108445A@212X6NT7NCP6
        // GRAPHIQUE
        EditTextScanee = (EditText) findViewById(R.id.EditTextScanee);
        EditTextScanee.setBackground(getResources().getDrawable(R.drawable.background_scanner_preparation));

        //Affichage des informations de la préparation
        ph_preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparationID);
        String depotText = ph_preparation_courante.getDepotDestinataireReference();

        Depot depotdestinataire = DepotOpenHelper.getDepotParID(db, ph_preparation_courante.getDepotDestinataireID());
        if(depotdestinataire != null)
        {
            if (utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depotdestinataire.getStructure().contentEquals("PAD")) {
                depotText = "Patient - " + depotdestinataire.getPAD_IPP();
            }
            else
                depotText = depotdestinataire.getNom();
        }

        ((TextView) findViewById(R.id.depot)).setText(depotText);
        ((TextView) findViewById(R.id.numPreparation)).setText("#" + preparationID);
        layoutValidationAutomatique = findViewById(R.id.layoutValidationAutomatique);
        counter = 5;
        //on cache le clavier à chaque fois que l'éditText reprend le focus après l'avoir perdu
        EditTextScanee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) ScannerPreparation2025_V2Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) ScannerPreparation2025_V2Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
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

                if(!((TextView) findViewById(R.id.qteSaisie)).getText().toString().contentEquals(""))
                {
                    produitCourant = null;
                    emplacement_courant = null;
                    stock_courant = null;
                }

                codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerPreparation2025_V2Activity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                ScannerPreparation2025_V2Activity.this.finish();
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
                    String codeScanne = s.toString().substring(0, s.length() - 1);

                    if((codeScanne.startsWith("01") || codeScanne.startsWith("02")) && codeScanne.length() == 16)
                    {
                        tempCodeScanne = codeScanne;
                    }
                    else if(tempCodeScanne != null && !tempCodeScanne.contentEquals("") && !codeScanne.startsWith("01") && !codeScanne.startsWith("02"))
                    {
                        tempCodeScanne = tempCodeScanne +  codeScanne;
                        EditTextScanee.setText(tempCodeScanne+"\n");
                        tempCodeScanne = "";
                    }
                    else
                    {
                        tempCodeScanne = "";
                        String lot = "";
                        String serie;
                        String gtin_courant = "";
                        String gtin_courant_sans_ai = "";
                        String date_peremption_courant = "";
                        String date_peremption_serialisation = "";
                        PH_Preparation_Ligne ligne_base = null;
                        if(codeScanne.startsWith("PHITAGPLACE"))
                        {
                            serie = "";
                            String[] tab_emplacement = codeScanne.split(":");
                            int emplacement_uid = Integer.parseInt(tab_emplacement[tab_emplacement.length-1]);

                            emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_uid);

                            if(emplacement_courant != null)
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacement_courant.getAdressage().trim());

                            if(emplacement_courant != null && produitCourant != null)
                            {
                                if(stock_courant != null)
                                    verificationEmplacementProduit(emplacement_courant, produitCourant, stock_courant.getEmplacement());
                            }

                            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                            ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.VISIBLE);
                            ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.GONE);
                        }
                        else
                        {
                            if(emplacement_courant == null)
                            {
                                afficherSnackBar("Veuillez scanner un emplacement");
                            }
                            else
                            {
                                Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeScanne);

                                if (gs1Decoupe.size() != 1)
                                {
                                    //on récupère les informations du découpage du GS1
                                    lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                                    serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                                    gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
                                    gtin_courant_sans_ai = gs1Decoupe.get(OutilsDecodage.codeGtinSansAi);
                                    date_peremption_courant = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
                                    date_peremption_serialisation = gs1Decoupe.get(OutilsDecodage.dateDePeremptionSerialisation);

                                    //gestion format date
                                    String[] date_peremption_split = date_peremption_courant.split("-");
                                    if(date_peremption_split.length == 3)
                                        date_peremption_courant = date_peremption_split[2] + "/" + date_peremption_split[1] + "/" + date_peremption_split[0];

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
                                    //on vérifie que le produit courant fait partie de la liste des ph_preparation_ligne
                                    boolean produit_present = false;
                                    for(PH_Preparation_Ligne ligne : liste_ph_preparation_ligne)
                                    {
                                        if(ligne.getProduitID() == produitCourant.getID_produit())
                                        {
                                            ligne_base = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ligne.get_UID());
                                            produit_present = true;
                                            break;
                                        }
                                    }


                                    if(!produit_present)
                                    {
                                        afficherSnackBar("Produit non présent dans la liste");
                                    }
                                    else
                                    {
                                        List<PH_Preparation_Ligne> preparationLignesPreparer = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, ph_preparation_courante, ligne_base.getProduitID());
                                        int qte_demander = ligne_base.getQte_APreparer();
                                        int qte_preparer = 0;
                                        int qte_restante = 0;
                                        for(PH_Preparation_Ligne ligne_temp : preparationLignesPreparer)
                                        {
                                            qte_preparer = qte_preparer + ligne_temp.getQte_preparer();
                                        }
                                        qte_restante = qte_demander - qte_preparer;

                                        String designationProduit = ligne_base.getProduitDesignation();
                                        String referenceProduit = ligne_base.getProduitReference();
                                        String conditionnement = String.valueOf((int)ligne_base.getProduitCondDistrib());

                                        if(qte_restante == 0)
                                        {
                                            afficherSnackBar("Produit déjà préparé en intégralité");
                                        }
                                        else
                                        {
                                            boolean lotPresent = false;
                                            for(String lotCourant : liste_lot)
                                            {
                                                if(lotCourant.contentEquals(lot))
                                                {
                                                    lotPresent = true;
                                                    if(!ligne_base.isSerialiser_Reception() && ligne_base.isSuivi_Par_Serie()) {
                                                        if(serialisationActive)
                                                        {
                                                            int serialisationUID = (int) Serialisation.Serialisation_Creer(utilisateurConnecte.getId(), "G110", gtin_courant, "GTIN", lot, date_peremption_serialisation, serie, "DELIVRANCE", String.valueOf(preparationID));
                                                            int serialisationdispenserUID = (int) Serialisation.Serialisation_Creer(utilisateurConnecte.getId(), "G120", gtin_courant, "GTIN", lot, date_peremption_serialisation, serie, "DELIVRANCE", String.valueOf(preparationID));
                                                        }
                                                    }

                                                    break;
                                                }
                                            }

                                            if(!lotPresent)
                                            {
                                                afficherSnackBar("Lot non présent dans la liste");
                                            }
                                            else
                                            {
                                                stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByLotPeremptionEtDepotEmplacement(db, lot, date_peremption_courant, DepotOpenHelper.getDepotParID(db, ph_preparation_courante.getDepotOrigineID()), emplacement_courant.getAdressage());

                                                if(stock_courant == null)
                                                    stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByLotPeremptionEtDepot(db, lot, date_peremption_courant, DepotOpenHelper.getDepotParID(db, ph_preparation_courante.getDepotOrigineID()));

                                                if(stock_courant == null)
                                                {
                                                    afficherSnackBar("Lot non présent dans votre stock");
                                                    reinitialisationInterface();
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

                                                    ((TextView) findViewById(R.id.designationProduit)).setText(designationProduit);
                                                    ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduit);
                                                    ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qte_demander));
                                                    ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(qte_preparer));
                                                    ((TextView) findViewById(R.id.qteSaisie)).setText(conditionnement);
                                                    ((TextView) findViewById(R.id.numeroLot)).setText(lot);
                                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText(date_peremption_courant);

                                                    if(!serie.contentEquals(""))
                                                    {
                                                        findViewById(R.id.numeroSerie).setVisibility(View.VISIBLE);
                                                        ((TextView) findViewById(R.id.numeroSerie)).setText(serie);
                                                        if(!stock_courant.getSerie().contentEquals(serie))
                                                        {
                                                            Stock_Lot_Emplacement_Light stockTemp = stock_courant;
                                                            stock_courant = new Stock_Lot_Emplacement_Light(Integer.parseInt(conditionnement), stockTemp.getLot(), stockTemp.getPeremptionDate(), stockTemp.getEmplacement(), stockTemp.getDepot_Reference(), stockTemp.getZone(), stockTemp.getProduit_Code(), 0, serie);
                                                            Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stock_courant);
                                                        }
                                                    }

                                                    //findViewById(R.id.validationScan).setVisibility(View.VISIBLE);

                                                    //gestion du clic sur le compteur
                                                    int finalQte_restante = qte_restante;
                                                    findViewById(R.id.layout_qte_saisie_lot_preparation).setOnClickListener(view -> {
                                                        if(!produitCourant.isSuivi_Serialisation() || produitCourant.isSerialiser_Reception_Delivrance())
                                                        {
                                                            Context context = ScannerPreparation2025_V2Activity.this;

                                                            String title = stock_courant.getLot();
                                                            String message = "Quantité placée : ";
                                                            int value_max = finalQte_restante;

                                                            int maxValue = value_max;
                                                            int value = finalQte_restante;

                                                            DialogInterface.OnClickListener onClickListener = (dialog, id) -> {
                                                                int qteApres = Alerte.aNumberPicker.getValue() * Integer.parseInt(conditionnement);

                                                                if (stock_courant != null) {
                                                                    stock_courant.setQte_Preparer(qteApres);
                                                                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                                                }
                                                                ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteApres));

                                                                dialog.dismiss();
                                                            };

                                                            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, Integer.parseInt(conditionnement));
                                                        }
                                                    });

                                                    //gestion de la validation du scan
                                                    //blinkImageValidation();
                                                    PH_Preparation_Ligne finalLigne_base = ligne_base;
                                                    /*ValidationAutomatique();
                                                    findViewById(R.id.validationScan).setOnClickListener(v -> {
                                                        //gestion enregistrement du lot scannee
                                                        int quantiteSaisie = Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString());
                                                        stock_courant.setQte_Preparer(stock_courant.getQte_Preparer()+quantiteSaisie);
                                                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                                        enregistrementPreparationLigne(finalLigne_base, stock_courant);
                                                        produitCourant = null;
                                                        //emplacement_courant = null;
                                                        stock_courant = null;
                                                        reinitialisationInterface();
                                                        //findViewById(R.id.boutonFermeture).performClick();
                                                    });*/
                                                    ((LinearLayout) findViewById(R.id.layoutIconeValidation)).setVisibility(View.VISIBLE);
                                                    int quantiteSaisie = Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString());
                                                    stock_courant.setQte_Preparer(stock_courant.getQte_Preparer()+quantiteSaisie);
                                                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                                    enregistrementPreparationLigne(finalLigne_base, stock_courant);
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
                                    //le produit n'est pas trouvé en base
                                    afficherSnackBar("Produit non trouvé");
                                }
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
                        Intent newIntent = new Intent(ScannerPreparation2025_V2Activity.this, ListeEmplacementCreationActivity.class);
                        Bundle extras = ScannerPreparation2025_V2Activity.super.getBundle();
                        extras.putInt("zoneid", zoneid);
                        newIntent.putExtras(extras);
                        ScannerPreparation2025_V2Activity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                    }
                    break;

                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    int emplacementid = data.getExtras().getInt("emplacementId");
                    if(emplacementid != -1)
                    {
                       /* Depot_Emplacement emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                        ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacementSelectionner.getAdressage().trim());
                        ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                        preparationMultipleContext.emplacement_courant = emplacementSelectionner;
                        if(preparationMultipleContext.lot_courant != null)
                        {
                            if(preparationMultipleContext.emplacementLotVerifier(preparationMultipleContext.emplacement_courant.getAdressage(), preparationMultipleContext.lot_courant.getNumLot()))
                            {
                                blinkImageValidation();
                                ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        preparationMultipleContext.ValiderScan(Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString()));
                                        ((TextView) findViewById(R.id.quantiteProduit)).setText("");
                                        ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText("");
                                        ((TextView) findViewById(R.id.numeroLot)).setText("");
                                        ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                                        ((TextView) findViewById(R.id.qteSaisie)).setText("");
                                        ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.GONE);
                                        findViewById(R.id.boutonFermeture).performClick();
                                    }
                                });
                            }
                            else
                            {
                                afficherAlerteErreurEmplacement(ScannerPreparation2025_V2Activity.this, ScannerPreparation2025_V2Activity.this.getLayoutInflater(), preparationMultipleContext.emplacementDisponible, preparationMultipleContext.liste_emplacement_disponible);
                                preparationMultipleContext.emplacement_courant = null;
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("");
                                ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");
                            }
                        }
                        else
                        {
                            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                        }*/
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
        final InputMethodManager imm = (InputMethodManager)ScannerPreparation2025_V2Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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

        InputMethodManager imme = (InputMethodManager) ScannerPreparation2025_V2Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imme.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    private void reinitialisationInterface()
    {
        ((TextView) findViewById(R.id.designationProduit)).setText("");
        ((TextView) findViewById(R.id.referenceProduit)).setText("");
        ((TextView) findViewById(R.id.quantiteProduit)).setText("");
        ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText("");
        ((TextView) findViewById(R.id.qteSaisie)).setText("");
        ((TextView) findViewById(R.id.numeroLot)).setText("");
        ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
        ((TextView) findViewById(R.id.numeroSerie)).setText("");
        ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.layoutIconeValidation)).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout) findViewById(R.id.layoutIconeValidation)).setVisibility(View.GONE);
            }
        }, 500);
    }

    private void blinkImageValidation() {
        // set its background to our AnimationDrawable XML resource.
        ((ImageView) findViewById(R.id.imageValidation)).setBackgroundResource(R.drawable.animation_blinking);

        /*
         * Get the background, which has been compiled to an AnimationDrawable
         * object.
         */
        AnimationDrawable frameAnimation = (AnimationDrawable) ((ImageView) findViewById(R.id.imageValidation))
                .getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
    }

    private void verificationEmplacementProduit(Depot_Emplacement emplacement_courant, Produit produitCourant, String stockEmplacement)
    {
        if(stockEmplacement.contentEquals(""))
        {
            if(!emplacement_courant.getAdressage().contentEquals(produitCourant.getEmplacement_PUI_Defaut()))
            {
                afficherAlerteErreurEmplacement(ScannerPreparation2025_V2Activity.this, ScannerPreparation2025_V2Activity.this.getLayoutInflater(), emplacement_courant.getAdressage(), stock_courant, emplacement_courant);
            }
        }
        else if(!stockEmplacement.contentEquals(emplacement_courant.getAdressage()))
        {
            afficherAlerteErreurEmplacement(ScannerPreparation2025_V2Activity.this, ScannerPreparation2025_V2Activity.this.getLayoutInflater(), emplacement_courant.getAdressage(), stock_courant, emplacement_courant);
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
                    ElementASynchroniserOpenHelper.toutSynchroniser(ScannerPreparation2025_V2Activity.this, db, utilisateurConnecte, false);
                }
                alertDialog.dismiss();
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //preparationMultipleContext.emplacement_courant = null;
                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("");
                ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");
                ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.VISIBLE);
                emplacement_courant = null;
                alertDialog.dismiss();
            }
        });
    }

    private void enregistrementPreparationLigne(PH_Preparation_Ligne ph_preparationLigneCorrespondant, Stock_Lot_Emplacement_Light stockLotEmplacementLight)
    {
        /* on supprime les lignes déjà enregistrer qui ne sont pas les lignes de bases */
        int GlobalAPreparer = ph_preparationLigneCorrespondant.getQte_Demander();
        PH_Preparation_Ligne ph_preparationLigneCourant = null;

        ph_preparationLigneCourant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByProduitLotSerieNegPreparation(db, ph_preparationLigneCorrespondant.getProduitID(), ph_preparationLigneCorrespondant.getPreparationID(), stockLotEmplacementLight.getLot(), stockLotEmplacementLight.getSerie(), stockLotEmplacementLight.getEmplacement());

        if(ph_preparationLigneCourant != null)
        {
            GlobalAPreparer = GlobalAPreparer - stockLotEmplacementLight.getQte_Preparer();
            ph_preparationLigneCourant.setQte_RAL(GlobalAPreparer);
            ph_preparationLigneCourant.setQte_preparer(stockLotEmplacementLight.getQte_Preparer());
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparationLigneCourant);
        }
        else
        {
            ph_preparationLigneCourant = new PH_Preparation_Ligne(ph_preparationLigneCorrespondant);
            Random random = new Random();
            int new_id = random.nextInt();
            if(new_id > 0)
            {
                new_id= new_id*-1;
            }

            ph_preparationLigneCourant.set_UID(new_id);
            ph_preparationLigneCourant.setQte_Demander(GlobalAPreparer);
            GlobalAPreparer = GlobalAPreparer - stockLotEmplacementLight.getQte_Preparer();
            ph_preparationLigneCourant.setQte_RAL(GlobalAPreparer);
            ph_preparationLigneCourant.setQte_preparer(stockLotEmplacementLight.getQte_Preparer());
            ph_preparationLigneCourant.setLotNumero(stockLotEmplacementLight.getLot().trim());
            ph_preparationLigneCourant.setPeremptionDate(stockLotEmplacementLight.getPeremptionDate());
            ph_preparationLigneCourant.setZoneDepot(stockLotEmplacementLight.getZone().trim());
            ph_preparationLigneCourant.setEmplacementParDefaut(stockLotEmplacementLight.getEmplacement().trim());
            ph_preparationLigneCourant.setSerieNumero(stockLotEmplacementLight.getSerie().trim());
            ph_preparationLigneCourant.set_UID_4D(ph_preparationLigneCorrespondant.get_UID_4D());

            long rowID = PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparationLigneCourant);
        }
    }

    private void ValidationAutomatique()
    {
        ((LinearLayout) findViewById(R.id.validationScan)).performClick();

        /*layoutValidationAutomatique.setVisibility(View.VISIBLE);

        counter = 5; // on démarre à 5 secondes
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView textView = findViewById(R.id.couterValidation);
        ImageView checkProgress = findViewById(R.id.checkProgress);

        progressBar.setMax(4);
        progressBar.setProgress(0);
        textView.setText(String.valueOf(counter));
        checkProgress.setVisibility(View.GONE);

        yourCountDownTimer = new CountDownTimer(5000, 1000) { // 5 sec
            public void onTick(long millisUntilFinished) {
                int elapsed = 6 - counter; // pour progresser de 0 à 5
                setProgressSmoothly(progressBar, progressBar.getProgress(), elapsed);
                textView.setText(String.valueOf(counter));
                counter--;

                if (counter == 0) {
                    textView.setVisibility(View.GONE);
                    checkProgress.setVisibility(View.VISIBLE);
                    progressBar.setProgressDrawable(ContextCompat.getDrawable(ScannerPreparation2025_V2Activity.this, R.drawable.progress_drawable_validate));
                }
            }

            public void onFinish() {
                yourCountDownTimer.cancel();
                counter = 5;
                layoutValidationAutomatique.setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.validationScan)).performClick();
            }
        }.start();

        ((LinearLayout) findViewById(R.id.layoutInformationScan))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    yourCountDownTimer.cancel();
                    counter = 5;
                    textView.setVisibility(View.GONE);
                    layoutValidationAutomatique.setVisibility(View.GONE);
                }
        });*/
    }

    private void setProgressSmoothly(ProgressBar progressBar, int from, int to) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", from, to);
        animation.setDuration(900); // durée de l’animation entre deux ticks
        animation.setInterpolator(new DecelerateInterpolator()); // effet fluide
        animation.start();
    }
}
//PHITAGPLACEA2102:2757
//01034009565350571727093010G246224
//157312
//PHITAGPLACED2:11701
//010340093634076317271031104A497
//PHITAGPLACED3:11699
//010340093634076317271231105A302