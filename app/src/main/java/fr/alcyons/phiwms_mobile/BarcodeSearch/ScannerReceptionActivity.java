package fr.alcyons.phiwms_mobile.BarcodeSearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.google.android.material.snackbar.Snackbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ReceptionListeContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ReceptionUniqueContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ReceptionPADContext2025;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeEmplacementCreationActivity;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeZoneCreationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;


public class ScannerReceptionActivity extends ServiceActivity {
    // INTENT
    String scannerContexte;
    int scannerContexteInt;

    List<String> listGTIN;
    String designationProduitCourant;
    String referenceProduitCourant;
    int qteDemander;
    List<String> liste_lot;
    String ordreTri;
    int counter;
    CountDownTimer yourCountDownTimer;

    //Gestion des réceptions
    int receptionID;
    Commande commandeCourante;
    //pour un scan multiple
    List<PH_Reliquat_Reception_Adapte> list_reliquat_receptionPuiAdapte;
    //pour un scan unitaire
    PH_Reliquat_Reception_Adapte uniqueReceptionPUIAdapte;
    PH_Reliquat reliquatCourant;

    // CONTEXTE
    ReceptionListeContext receptionListeContext;
    ReceptionUniqueContext receptionUniqueContext;
    ReceptionPADContext2025 newReceptionPADContext;

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
    List<ObjetReceptionScannee> listObjet_scanne;
    List<Integer> liste_id_reliquat;
    ImageView imageValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_preparation);

        // INTENT
        intent = ScannerReceptionActivity.this.getIntent();
        scannerContexte = intent.getExtras().getString("contexte");
        scannerContexteInt = Integer.parseInt(scannerContexte);
        liste_lot = intent.getExtras().getStringArrayList("liste_lot");
        ordreTri = intent.getExtras().getString("ordreTri");
        receptionID = intent.getExtras().getInt("ReceptionID");
        list_reliquat_receptionPuiAdapte = (List<PH_Reliquat_Reception_Adapte>) intent.getExtras().getSerializable("ReceptionPUIAdapte");
        uniqueReceptionPUIAdapte = (PH_Reliquat_Reception_Adapte) intent.getExtras().getSerializable("UniqueReceptionPUIAdapte");
        reliquatCourant = (PH_Reliquat) intent.getExtras().getSerializable("ReliquatCourant");
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
        if (scannerContexteInt == R.string.scannerContextReceptionUnique || scannerContexteInt == R.string.scannerContextReceptionListe || scannerContexteInt == R.string.scannerContextNewReceptionPAD) {
            commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
            numPreparation.setText("#" + commandeCourante.getNumero());
            depot.setText(commandeCourante.getFournisseur());
        }


        // CONTEXTE
        if(scannerContexteInt == R.string.scannerContextReceptionListe)
        {
            Depot_Emplacement emplacement_precedent = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
            Produit produit_precedent = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");

            ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
            List<PH_Reliquat> liste_reliquat_commande_courante = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeCourante.getNumero());
            Collections.sort(liste_reliquat_commande_courante, new Comparator<PH_Reliquat>() {
                @Override
                public int compare(PH_Reliquat o1, PH_Reliquat o2) {
                    int tri = 0;
                    switch (ordreTri) {
                        case "Designation":
                            tri = o1.getDesignationCourte().compareTo(o2.getDesignationCourte());
                            break;
                        case "Place":
                            tri = o1.getEmplacement().compareTo(o2.getEmplacement());
                            break;
                    }
                    return tri;
                }
            });

            //initialisation du context
            List<String> listGtinScannee = new ArrayList<>();
            receptionListeContext = new ReceptionListeContext(this, db, utilisateurConnecte, listGtinScannee, utilisateurConnecte.getId(), commandeCourante.getID_commande(), liste_reliquat_commande_courante, list_reliquat_receptionPuiAdapte, emplacement_precedent, produit_precedent);
            ((TextView) findViewById(R.id.EmplacementLotProduit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
                    Depot depotOrigine = DepotOpenHelper.getDepotPUI(db);
                    Intent newIntent = new Intent(ScannerReceptionActivity.this, ListeZoneCreationActivity.class);
                    Bundle extras = ScannerReceptionActivity.super.getBundle();
                    extras.putInt("depotID", depotOrigine.getDepot_UID());
                    newIntent.putExtras(extras);
                    ScannerReceptionActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
                }
            });

            PH_Reliquat premier_reliquat = liste_reliquat_commande_courante.get(0);
            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");

        }
        else if(scannerContexteInt == R.string.scannerContextReceptionUnique)
        {
            Depot_Emplacement emplacement_precedent_unique = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
            Produit produit_precedent_unique = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");
            ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
            List<String> listGtinScanneeUnique = new ArrayList<>();
            receptionUniqueContext = new ReceptionUniqueContext(this, db, utilisateurConnecte, listGtinScanneeUnique, utilisateurConnecte.getId(), commandeCourante.getID_commande(), reliquatCourant, uniqueReceptionPUIAdapte, emplacement_precedent_unique, produit_precedent_unique);
            //affichage des premieres informations
            designationProduitCourant = reliquatCourant.getDesignationCourte();
            referenceProduitCourant = reliquatCourant.getProduit_Reference();
            qteDemander = reliquatCourant.getQteCommande();
            if (reliquatCourant.getQteLivraison() > 0) {
                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(reliquatCourant.getQteLivraison()));
                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
            }
            ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
            ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
            ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));

            Produit produitCourant = ProduitOpenHelper.getProduitByID(db, reliquatCourant.getProduitID());

            //gestion de l'emplacement par rapport à l'emplacement du produit par défaut
            Depot depot_pui = DepotOpenHelper.getDepotPUI(db);
            String zone_pui_defaut = produitCourant.getZone_PUI_Defaut();
            String emplacemement_pui_defaut = produitCourant.getEmplacement_PUI_Defaut();

            if(zone_pui_defaut != null && !zone_pui_defaut.contentEquals("") && depot_pui != null)
            {
                Depot_Zone zone_courante = ZoneOpenHelper.getZoneByDepotEtNom(db, depot_pui, zone_pui_defaut);

                if(zone_courante!=null && emplacemement_pui_defaut != null && !emplacemement_pui_defaut.contentEquals(""))
                {
                    receptionUniqueContext.emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone_courante, emplacemement_pui_defaut);
                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(produitCourant.getEmplacement_PUI_Defaut());
                    ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                }
            }
            ((TextView) findViewById(R.id.EmplacementLotProduit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
                    Depot depotOrigine = DepotOpenHelper.getDepotPUI(db);
                    Intent newIntent = new Intent(ScannerReceptionActivity.this, ListeZoneCreationActivity.class);
                    Bundle extras = ScannerReceptionActivity.super.getBundle();
                    extras.putInt("depotID", depotOrigine.getDepot_UID());
                    newIntent.putExtras(extras);
                    ScannerReceptionActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
                }
            });

            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
        }
        else if(scannerContexteInt == R.string.scannerContextNewReceptionPAD)
        {
            ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
            List<PH_Reliquat> liste_reliquat_commande_courante_pad = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeCourante.getNumero());
            Collections.sort(liste_reliquat_commande_courante_pad, new Comparator<PH_Reliquat>() {
                @Override
                public int compare(PH_Reliquat o1, PH_Reliquat o2) {
                    int tri = 0;
                    switch (ordreTri) {
                        case "Designation":
                            tri = o1.getDesignationCourte().compareTo(o2.getDesignationCourte());
                            break;
                        case "Place":
                            tri = o1.getEmplacement().compareTo(o2.getEmplacement());
                            break;
                    }
                    return tri;
                }
            });

            //initialisation du context
            List<String> listGtinScannee = new ArrayList<>();
            listGtinScannee = intent.getExtras().getStringArrayList("Liste_GTIN_Scannee");
            listObjet_scanne = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
            liste_id_reliquat = intent.getExtras().getIntegerArrayList("liste_id_reliquat");
            Depot_Emplacement emplacement_precedent = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
            Produit produit_precedent = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");
            newReceptionPADContext = new ReceptionPADContext2025(this, db, utilisateurConnecte, listGtinScannee, utilisateurConnecte.getId(), commandeCourante.getID_commande(), liste_reliquat_commande_courante_pad, list_reliquat_receptionPuiAdapte, emplacement_precedent, produit_precedent);


            ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.INVISIBLE);
        }

        //on cache le clavier à chaque fois que l'éditText reprend le focus après l'avoir perdu
        EditTextScanee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) ScannerReceptionActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) ScannerReceptionActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
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
                if(scannerContexteInt == R.string.scannerContextReceptionListe)
                {
                    if(receptionListeContext.nouveau_lot != null && receptionListeContext.emplacement_courant != null)
                    {
                        boolean confirmation = Alerte.afficherAlerte(ScannerReceptionActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
                        if(confirmation)
                        {
                            receptionListeContext.ValiderScan();
                        }
                        else
                        {
                            ((TextView) findViewById(R.id.designationProduit)).setText("");
                            ((TextView) findViewById(R.id.referenceProduit)).setText("");
                            ((TextView) findViewById(R.id.quantiteProduit)).setText("");
                            ((TextView) findViewById(R.id.numeroLot)).setText("");
                            ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                            ((TextView) findViewById(R.id.qteSaisie)).setText("");
                            ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.GONE);
                        }
                    }
                    //newReceptionPUIContext.ValiderScan();
                    scannerSearchOnlyBundle.putSerializable("EmplacementPrecedent", (Serializable) receptionListeContext.emplacementPrecedent);
                    scannerSearchOnlyBundle.putSerializable("ProduitPrecedent", (Serializable) receptionListeContext.produitPrecedent);
                    scannerSearchOnlyBundle.putSerializable("reliquatAdapteList", (Serializable) receptionListeContext.list_reliquat_receptionPuiAdapte);
                    codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                }
                else if(scannerContexteInt == R.string.scannerContextReceptionUnique)
                {
                    if(receptionUniqueContext.nouveau_lot != null && receptionUniqueContext.emplacement_courant != null)
                    {
                        boolean confirmation = Alerte.afficherAlerte(ScannerReceptionActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
                        if(confirmation)
                        {
                            receptionUniqueContext.ValiderScan();
                        }
                        else
                        {
                            ((TextView) findViewById(R.id.designationProduit)).setText("");
                            ((TextView) findViewById(R.id.referenceProduit)).setText("");
                            ((TextView) findViewById(R.id.quantiteProduit)).setText("");
                            ((TextView) findViewById(R.id.numeroLot)).setText("");
                            ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                            ((TextView) findViewById(R.id.qteSaisie)).setText("");
                            ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.GONE);
                        }
                    }
                    //newReceptionPUIContext.ValiderScan();
                    scannerSearchOnlyBundle.putSerializable("EmplacementPrecedent", (Serializable) receptionUniqueContext.emplacementPrecedent);
                    scannerSearchOnlyBundle.putSerializable("ProduitPrecedent", (Serializable) receptionUniqueContext.produitPrecedent);
                    scannerSearchOnlyBundle.putSerializable("reliquatAdapte", (Serializable) receptionUniqueContext.phReliquatReceptionPUIAdapte_courant);
                    codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                }
                else if(scannerContexteInt == R.string.scannerContextNewReceptionPAD)
                {
                    if(newReceptionPADContext.nouveau_lot != null && newReceptionPADContext.emplacement_courant != null)
                    {
                        boolean confirmation = Alerte.afficherAlerte(ScannerReceptionActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
                        if(confirmation)
                        {
                            newReceptionPADContext.ValiderScan();
                        }
                    }
                    //stringList = newReceptionPADContext.stringList;
                    scannerSearchOnlyBundle.putSerializable("EmplacementPrecedent", (Serializable) receptionListeContext.emplacementPrecedent);
                    scannerSearchOnlyBundle.putSerializable("ProduitPrecedent", (Serializable) receptionListeContext.produitPrecedent);
                    scannerSearchOnlyBundle.putSerializable("reliquatAdapteList", (Serializable) receptionListeContext.list_reliquat_receptionPuiAdapte);
                    codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                }

                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerReceptionActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                ScannerReceptionActivity.this.finish();
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
                    if(scannerContexteInt == R.string.scannerContextReceptionListe)
                    {
                        receptionListeContext.onTextWatcher(s);
                        if (receptionListeContext.nouveau_lot != null) {
                            ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");

                            designationProduitCourant = receptionListeContext.reliquat_courant.getDesignationCourte();
                            referenceProduitCourant = receptionListeContext.reliquat_courant.getProduit_Reference();
                            qteDemander = receptionListeContext.reliquat_courant.getQteCommande();
                            if (receptionListeContext.reliquat_courant.getQteLivraison() > 0) {
                                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(receptionListeContext.reliquat_courant.getQteLivraison()));
                                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                            }
                            ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                            ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                            ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));
                            ((TextView) findViewById(R.id.numeroLot)).setText(receptionListeContext.nouveau_lot.getNumeroLot());
                            if (receptionListeContext.nouveau_lot.getNumero_serie() != null && !receptionListeContext.nouveau_lot.getNumero_serie().contentEquals("")) {
                                ((TextView) findViewById(R.id.numeroSerie)).setText(receptionListeContext.nouveau_lot.getNumero_serie());
                            } else {
                                ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                            }

                            String dateDePeremption = receptionListeContext.nouveau_lot.getDatePeremption();
                            String[] dateDePeremtpionTab = dateDePeremption.split("-");
                            if(dateDePeremtpionTab.length > 1 )
                            {
                                dateDePeremption = dateDePeremtpionTab[2] + "/" + dateDePeremtpionTab[1] + "/" + dateDePeremtpionTab[0];
                            }

                            ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);

                            ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(receptionListeContext.qte_lot_courant));

                            //gestion du numberPicker pour changer la quantité lors d'un scan
                            ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(receptionListeContext.nouveau_lot != null)
                                    {
                                        Context context = ScannerReceptionActivity.this;
                                        if (receptionListeContext.emplacement_courant != null) {
                                            yourCountDownTimer.cancel();
                                            ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                                            ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
                                        }
                                        String title = receptionListeContext.nouveau_lot.getNumeroLot();
                                        String message = "Quantité réceptionnée : ";
                                        int value_max = (int) receptionListeContext.reliquat_courant.getQteReliquat_X();
                                        int maxValue = value_max;
                                        int value = maxValue;

                                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                int qteAprès = Alerte.aNumberPicker.getValue() * receptionListeContext.conditionnement_achat;
                                                receptionListeContext.qte_lot_courant = qteAprès;
                                                ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                                dialog.dismiss();

                                                if (receptionListeContext.emplacement_courant != null) {
                                                    ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                                                    ((LinearLayout) findViewById(R.id.validationScan)).performClick();
                                                }
                                            }
                                        };

                                        Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, receptionListeContext.conditionnement_achat);
                                    }
                                }
                            });

                        }

                        if (receptionListeContext.emplacement_courant != null) {
                            ((TextView) findViewById(R.id.instruction)).setText("");
                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(receptionListeContext.emplacement_courant.getAdressage());
                            blinkImageValidation();

                            //initilisation du compteur
                            ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.instruction)).setVisibility(View.GONE);
                            counter = 5;
                            Counter();

                            ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.INVISIBLE);
                            ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    receptionListeContext.ValiderScan();
                                    yourCountDownTimer.cancel();
                                    ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.designationProduit)).setText("");
                                    ((TextView) findViewById(R.id.referenceProduit)).setText("");
                                    ((TextView) findViewById(R.id.quantiteProduit)).setText("");
                                    ((TextView) findViewById(R.id.numeroLot)).setText("");
                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                                    ((TextView) findViewById(R.id.qteSaisie)).setText("");
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("");
                                    ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText("");
                                    ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                                    ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
                                    //findViewById(R.id.boutonFermeture).performClick();
                                }
                            });
                        }
                    }
                    else if(scannerContexteInt == R.string.scannerContextReceptionUnique)
                    {
                        receptionUniqueContext.onTextWatcher(s);
                        if (receptionUniqueContext.nouveau_lot != null) {
                            ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");
                            designationProduitCourant = receptionUniqueContext.reliquat_courant.getDesignationCourte();
                            referenceProduitCourant = receptionUniqueContext.reliquat_courant.getProduit_Reference();
                            qteDemander = receptionUniqueContext.reliquat_courant.getQteCommande();
                            if (receptionUniqueContext.reliquat_courant.getQteLivraison() > 0) {
                                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(receptionUniqueContext.reliquat_courant.getQteLivraison()));
                                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                            }
                            ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                            ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                            ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));
                            ((TextView) findViewById(R.id.numeroLot)).setText(receptionUniqueContext.nouveau_lot.getNumeroLot());
                            if (receptionUniqueContext.nouveau_lot.getNumero_serie() != null && !receptionUniqueContext.nouveau_lot.getNumero_serie().contentEquals("")) {
                                ((TextView) findViewById(R.id.numeroSerie)).setText(receptionUniqueContext.nouveau_lot.getNumero_serie());
                            } else {
                                ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                            }

                            String dateDePeremption = receptionUniqueContext.nouveau_lot.getDatePeremption();
                            String[] dateDePeremtpionTab = dateDePeremption.split("-");
                            if(dateDePeremtpionTab.length > 1 )
                                dateDePeremption = dateDePeremtpionTab[2] + "/" + dateDePeremtpionTab[1] + "/" + dateDePeremtpionTab[0];

                            ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);

                            ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(receptionUniqueContext.qte_lot_courant));

                            //gestion du numberPicker pour changer la quantité lors d'un scan
                            ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(receptionUniqueContext.nouveau_lot != null)
                                    {
                                        Context context = ScannerReceptionActivity.this;
                                        if (receptionUniqueContext.emplacement_courant != null) {
                                            yourCountDownTimer.cancel();
                                            ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                                            ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
                                        }
                                        String title = receptionUniqueContext.nouveau_lot.getNumeroLot();
                                        String message = "Quantité réceptionnée : ";
                                        int value_max = (int) receptionUniqueContext.reliquat_courant.getQteReliquat_X();
                                        int maxValue = value_max;
                                        int value = maxValue;

                                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                int qteAprès = Alerte.aNumberPicker.getValue() * receptionUniqueContext.conditionnement_achat;
                                                receptionUniqueContext.qte_lot_courant = qteAprès;
                                                ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                                dialog.dismiss();

                                                if (receptionUniqueContext.emplacement_courant != null) {
                                                    ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                                                    ((LinearLayout) findViewById(R.id.validationScan)).performClick();
                                                }


                                            }
                                        };

                                        Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, receptionUniqueContext.conditionnement_achat);
                                    }
                                }
                            });

                        }

                        if (receptionUniqueContext.emplacement_courant != null) {
                            ((TextView) findViewById(R.id.instruction)).setText("");
                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(receptionUniqueContext.emplacement_courant.getAdressage());
                            blinkImageValidation();

                            //initilisation du compteur
                            ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.instruction)).setVisibility(View.GONE);
                            counter = 3;
                            Counter();

                            ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.INVISIBLE);
                            ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    receptionUniqueContext.ValiderScan();
                                    ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                                    ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                    else if(scannerContexteInt == R.string.scannerContextNewReceptionPAD)
                    {
                        ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");

                        designationProduitCourant = newReceptionPADContext.reliquat_courant.getDesignationCourte();
                        referenceProduitCourant = newReceptionPADContext.reliquat_courant.getProduit_Reference();
                        qteDemander = newReceptionPADContext.reliquat_courant.getQteCommande();
                        if (newReceptionPADContext.reliquat_courant.getQteLivraison() > 0) {
                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(newReceptionPADContext.reliquat_courant.getQteLivraison()));
                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                        }
                        ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                        ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                        ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));
                        ((TextView) findViewById(R.id.numeroLot)).setText(newReceptionPADContext.nouveau_lot.getNumeroLot());
                        if (newReceptionPADContext.nouveau_lot.getNumero_serie() != null && !newReceptionPADContext.nouveau_lot.getNumero_serie().contentEquals("")) {
                            ((TextView) findViewById(R.id.numeroSerie)).setText(newReceptionPADContext.nouveau_lot.getNumero_serie());
                        } else {
                            ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                        }

                        String dateDePeremption = newReceptionPADContext.nouveau_lot.getDatePeremption();
                        String[] dateDePeremtpionTab = dateDePeremption.split("-");
                        if(dateDePeremtpionTab.length > 1 )
                        {
                            dateDePeremption = dateDePeremtpionTab[2] + "/" + dateDePeremtpionTab[1] + "/" + dateDePeremtpionTab[0];
                        }

                        ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);

                        ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(newReceptionPADContext.qte_lot_courant));

                        //gestion du numberPicker pour changer la quantité lors d'un scan
                        ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(newReceptionPADContext.nouveau_lot != null)
                                {
                                    Context context = ScannerReceptionActivity.this;
                                    if (newReceptionPADContext.emplacement_courant != null) {
                                        yourCountDownTimer.cancel();
                                        ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                                        ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
                                    }
                                    String title = newReceptionPADContext.nouveau_lot.getNumeroLot();
                                    String message = "Quantité réceptionnée : ";
                                    int value_max = (int) newReceptionPADContext.reliquat_courant.getQteReliquat_X();
                                    int maxValue = value_max;
                                    int value = maxValue;

                                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            int qteAprès = Alerte.aNumberPicker.getValue() * newReceptionPADContext.conditionnement_achat;
                                            newReceptionPADContext.qte_lot_courant = qteAprès;
                                            ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                            dialog.dismiss();

                                            if (newReceptionPADContext.emplacement_courant != null) {
                                                ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                                                ((LinearLayout) findViewById(R.id.validationScan)).performClick();
                                            }
                                        }
                                    };

                                    Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, newReceptionPADContext.conditionnement_achat);
                                }
                            }
                        });



                    if (newReceptionPADContext.emplacement_courant != null) {
                        ((TextView) findViewById(R.id.instruction)).setText("");
                        ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(newReceptionPADContext.emplacement_courant.getAdressage());
                        blinkImageValidation();

                        //initilisation du compteur
                        ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                        ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.instruction)).setVisibility(View.GONE);
                        counter = 3;
                        Counter();

                        ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.INVISIBLE);
                        ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newReceptionPADContext.ValiderScan();
                                yourCountDownTimer.cancel();
                                ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.designationProduit)).setText("");
                                ((TextView) findViewById(R.id.referenceProduit)).setText("");
                                ((TextView) findViewById(R.id.quantiteProduit)).setText("");
                                ((TextView) findViewById(R.id.numeroLot)).setText("");
                                ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                                ((TextView) findViewById(R.id.qteSaisie)).setText("");
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("");
                                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText("");
                                ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                                ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
                                //findViewById(R.id.boutonFermeture).performClick();
                            }
                        });
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

            if(scannerContexteInt == R.string.scannerContextReceptionUnique)
            {
                switch (requestCode) {
                    case CodesEchangesActivites.RESULT_ZONE:
                        int zoneid = data.getExtras().getInt("zoneid");
                        if(zoneid != -1)
                        {
                            Intent newIntent = new Intent(ScannerReceptionActivity.this, ListeEmplacementCreationActivity.class);
                            Bundle extras = ScannerReceptionActivity.super.getBundle();
                            extras.putInt("zoneid", zoneid);
                            newIntent.putExtras(extras);
                            ScannerReceptionActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                        }
                        break;

                    case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                        int emplacementid = data.getExtras().getInt("emplacementId");
                        if(emplacementid != -1)
                        {
                            Depot_Emplacement emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacementSelectionner.getAdressage().trim());
                            receptionUniqueContext.emplacement_courant = emplacementSelectionner;
                            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                        }
                        break;
                }
            }
            else if(scannerContexteInt == R.string.scannerContextReceptionListe)
            {
                switch (requestCode) {
                    case CodesEchangesActivites.RESULT_ZONE:
                        int zoneid = data.getExtras().getInt("zoneid");
                        if(zoneid != -1)
                        {
                            Intent newIntent = new Intent(ScannerReceptionActivity.this, ListeEmplacementCreationActivity.class);
                            Bundle extras = ScannerReceptionActivity.super.getBundle();
                            extras.putInt("zoneid", zoneid);
                            newIntent.putExtras(extras);
                            ScannerReceptionActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                        }
                        break;

                    case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                        int emplacementid = data.getExtras().getInt("emplacementId");
                        if(emplacementid != -1)
                        {
                            Depot_Emplacement emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacementSelectionner.getAdressage().trim());
                            receptionListeContext.emplacement_courant = emplacementSelectionner;
                            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                        }
                        break;
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.boutonFermeture).performClick();
    }


    public void afficherSnackBar(String message) {
        final InputMethodManager imm = (InputMethodManager)ScannerReceptionActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>" + message + "</b>", 0), Snackbar.LENGTH_LONG);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));

        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.setMargins(0, 50, 0, 0);
        snackBarView.setLayoutParams(params);
        snackbar.show();

        InputMethodManager imme = (InputMethodManager) ScannerReceptionActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imme.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
    private void Counter()
    {
        yourCountDownTimer = new CountDownTimer(2000, 1000){
            public void onTick(long millisUntilFinished){
                ((TextView) findViewById(R.id.textViewCountDown)).setText(String.valueOf(counter));
                counter--;
            }
            public  void onFinish(){
                yourCountDownTimer.cancel();
                counter = 5;
                ((LinearLayout) findViewById(R.id.validationScan)).performClick();
            }

        }.start();

        ((LinearLayout) findViewById(R.id.layoutInformationScan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yourCountDownTimer.cancel();
                counter = 3;
                ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
            }
        });
    }

    private void blinkImageValidation() {
        // set its background to our AnimationDrawable XML resource.
        imageValidation.setBackgroundResource(R.drawable.animation_blinking);

        /*
         * Get the background, which has been compiled to an AnimationDrawable
         * object.
         */
        AnimationDrawable frameAnimation = (AnimationDrawable) imageValidation
                .getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
    }

}
