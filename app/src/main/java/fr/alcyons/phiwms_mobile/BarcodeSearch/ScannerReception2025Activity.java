package fr.alcyons.phiwms_mobile.BarcodeSearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ReceptionListeContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ReceptionPADContext2025;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ReceptionUniqueContext;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeEmplacementCreationActivity;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeZoneCreationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.Reception.DetailReceptionActivity;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ScannerReception2025Activity extends ServiceActivity {
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
    Produit produitCourant;

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
    Depot_Emplacement emplacement_precedent;
    Depot_Emplacement emplacement_courant;
    PH_Reliquat_Reception_Adapte.Lot nouveau_lot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_preparation);

        // INTENT
        intent = ScannerReception2025Activity.this.getIntent();
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


        if(list_reliquat_receptionPuiAdapte.size() == 1)
        {
            uniqueReceptionPUIAdapte = list_reliquat_receptionPuiAdapte.get(0);
            reliquatCourant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, list_reliquat_receptionPuiAdapte.get(0).getPhReliquatUID());

            List<PH_Reliquat> temp = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, reliquatCourant.getcommandeNumero(), reliquatCourant.getProduitID());
            int qte_receptionne = 0;
            for(PH_Reliquat ligne_temp : temp)
            {
                qte_receptionne = qte_receptionne + ligne_temp.getQteLivraison();
            }

            ((TextView) findViewById(R.id.designationProduit)).setText(reliquatCourant.getdesignationCourte());
            ((TextView) findViewById(R.id.referenceProduit)).setText(reliquatCourant.getProduit_Reference());
            ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(reliquatCourant.getQteCommande()));
            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(qte_receptionne));
        }

        // CONTEXTE
        emplacement_precedent = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
        ((TextView) findViewById(R.id.EmplacementLotProduit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
                Depot depotOrigine = DepotOpenHelper.getDepotPUI(db);
                Intent newIntent = new Intent(ScannerReception2025Activity.this, ListeZoneCreationActivity.class);
                Bundle extras = ScannerReception2025Activity.super.getBundle();
                extras.putInt("depotID", depotOrigine.getDepot_UID());
                newIntent.putExtras(extras);
                ScannerReception2025Activity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
            }
        });

        ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");

        //on cache le clavier à chaque fois que l'éditText reprend le focus après l'avoir perdu
        EditTextScanee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) ScannerReception2025Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) ScannerReception2025Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
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

                if(nouveau_lot != null && emplacement_courant != null)
                {
                    boolean confirmation = Alerte.afficherAlerte(ScannerReception2025Activity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
                    if(confirmation)
                    {
                        uniqueReceptionPUIAdapte.getlotList().add(nouveau_lot);
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

                scannerSearchOnlyBundle.putSerializable("EmplacementPrecedent", (Serializable) emplacement_courant);
                scannerSearchOnlyBundle.putSerializable("ProduitPrecedent", (Serializable) produitCourant);
                scannerSearchOnlyBundle.putSerializable("reliquatAdapteList", (Serializable) list_reliquat_receptionPuiAdapte);
                codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;

                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerReception2025Activity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                ScannerReception2025Activity.this.finish();
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
                    String lot;
                    String serie;
                    String gtin_courant = "";
                    String gtin_courant_sans_ai = "";
                    String date_peremption_courant = "";
                    if(codeScanne.startsWith("PHITAGPLACE+"))
                    {
                        serie = "";
                        lot = "";
                        String[] tab_emplacement = codeScanne.split(":");
                        int emplacement_uid = Integer.parseInt(tab_emplacement[tab_emplacement.length-1]);

                        emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_uid);

                        if(emplacement_courant != null)
                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacement_courant.getAdressage().trim());


                        ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                        ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.VISIBLE);
                        ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.GONE);

                        if(produitCourant != null)
                        {
                            //initilisation du compteur
                            ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.instruction)).setVisibility(View.GONE);
                            counter = 5;
                            Counter();
                        }
                    }
                    else {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeScanne);

                        if (gs1Decoupe.size() != 1)
                        {
                            //on récupère les informations du découpage du GS1
                            lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                            serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                            gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
                            gtin_courant_sans_ai = gs1Decoupe.get(OutilsDecodage.codeGtinSansAi);
                            date_peremption_courant = gs1Decoupe.get(OutilsDecodage.dateDePeremption);

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
                            lot = "";
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
                            for(PH_Reliquat_Reception_Adapte reliquatReceptionAdapte : list_reliquat_receptionPuiAdapte)
                            {
                                PH_Reliquat ligne = PH_ReliquatOpenHelper.getPH_ReliquatById(db, reliquatReceptionAdapte.getPhReliquatUID());

                                if(ligne.getProduitID() == produitCourant.getID_produit())
                                {
                                    reliquatCourant = ligne;
                                    uniqueReceptionPUIAdapte = reliquatReceptionAdapte;
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
                                List<PH_Reliquat> reliquatPreparer = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, reliquatCourant.getcommandeNumero(), reliquatCourant.getProduitID());
                                int qte_demander = reliquatCourant.getQteCommande();
                                int qte_receptionne = 0;
                                int qte_restante = 0;
                                for(PH_Reliquat ligne_temp : reliquatPreparer)
                                {
                                    qte_receptionne = qte_receptionne + ligne_temp.getQteLivraison();
                                }
                                qte_restante = qte_demander - qte_receptionne;

                                String designationProduit = reliquatCourant.getDesignationCourte();
                                String referenceProduit = reliquatCourant.getProduit_Reference();
                                String conditionnement = String.valueOf((int)reliquatCourant.getConditionnementAchat());

                                if(qte_restante == 0)
                                {
                                    afficherSnackBar("Produit déjà réceptionné en intégralité");
                                }
                                else
                                {
                                    if(emplacement_courant != null || commandeCourante.getRef_Depot_Dest().contains("-PAD"))
                                    {
                                        ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                        ((TextView) findViewById(R.id.instruction)).setText("");

                                        if (commandeCourante.getRef_Depot_Dest().contains("-PAD")) {
                                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("RECEPTION-" + commandeCourante.getNumero() + "-" + commandeCourante.getPatient_identite());
                                        } else {
                                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacement_courant.getAdressage());
                                        }
                                        blinkImageValidation();

                                        //initilisation du compteur
                                        ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.GONE);
                                        ((TextView) findViewById(R.id.instruction)).setVisibility(View.GONE);
                                        counter = 5;
                                        Counter();
                                    }
                                    else
                                    {
                                        ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.GONE);
                                        ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.VISIBLE);
                                        ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacment");
                                    }

                                    ((TextView) findViewById(R.id.designationProduit)).setText(designationProduit);
                                    ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduit);
                                    ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qte_demander));
                                    ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(qte_receptionne));
                                    ((TextView) findViewById(R.id.qteSaisie)).setText(conditionnement);
                                    ((TextView) findViewById(R.id.numeroLot)).setText(lot);
                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText(date_peremption_courant);

                                    for(PH_Reliquat_Reception_Adapte.Lot lotCourant : uniqueReceptionPUIAdapte.getlotList())
                                    {
                                        if(lotCourant.getNumeroLot().equals(lot))
                                        {
                                            nouveau_lot = lotCourant;
                                        }
                                    }

                                    if(nouveau_lot == null)
                                        nouveau_lot = uniqueReceptionPUIAdapte.new Lot(lot, date_peremption_courant, serie, "");

                                    if(!serie.contentEquals(""))
                                    {
                                        findViewById(R.id.numeroSerie).setVisibility(View.VISIBLE);
                                        ((TextView) findViewById(R.id.numeroSerie)).setText(serie);
                                    }

                                    findViewById(R.id.validationScan).setVisibility(View.VISIBLE);

                                    //gestion du clic sur le compteur
                                    int finalQte_restante = qte_restante;
                                    findViewById(R.id.layout_qte_saisie_lot_preparation).setOnClickListener(view -> {
                                        if(yourCountDownTimer != null)
                                            yourCountDownTimer.cancel();

                                        Context context = ScannerReception2025Activity.this;

                                        String title = lot;
                                        String message = "Quantité placée : ";
                                        int value_max = finalQte_restante;

                                        int maxValue = value_max;
                                        int value = finalQte_restante;

                                        DialogInterface.OnClickListener onClickListener = (dialog, id) -> {
                                            int qteApres = Alerte.aNumberPicker.getValue() * Integer.parseInt(conditionnement);

                                            ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteApres));

                                            dialog.dismiss();
                                        };

                                        Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, Integer.parseInt(conditionnement));
                                    });

                                    //gestion de la validation du scan
                                    blinkImageValidation();
                                    String finalDate_peremption_courant = date_peremption_courant;
                                    findViewById(R.id.validationScan).setOnClickListener(v -> {
                                        //gestion enregistrement du lot scannee
                                        int quantiteSaisie = Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString());

                                        String zone_string = "";
                                        int zoneId = 0;
                                        String emplacement_string = "";
                                        int emplacementId = 0;
                                        if (commandeCourante.getRef_Depot_Dest().contains("-PAD")) {
                                            zone_string = "RECEPTION";
                                            emplacement_string = "RECEPTION-" + commandeCourante.getNumero() + "-" + commandeCourante.getPatient_identite();
                                        } else {
                                            Depot_Zone zoneCourante = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());
                                            zone_string = zoneCourante.getZoneName();
                                            zoneId = zoneCourante.getZoneID();
                                            emplacement_string = emplacement_courant.getAdressage();
                                            emplacementId = emplacement_courant.get_UID();
                                        }

                                        boolean lotPresent = false;
                                        boolean emplacementexiste = false;
                                        for(PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement : nouveau_lot.getZoneEtEmplacementList())
                                        {
                                            if(zoneEtEmplacement.getZoneName().contentEquals(zone_string) && emplacement_string.contentEquals(zoneEtEmplacement.getEmplacementName()))
                                            {
                                                zoneEtEmplacement.setQuantite(zoneEtEmplacement.getQuantite()+quantiteSaisie);
                                                emplacementexiste = true;
                                                break;
                                            }
                                        }

                                        if(!emplacementexiste)
                                        {
                                            nouveau_lot = uniqueReceptionPUIAdapte.new Lot(lot, finalDate_peremption_courant, serie, "");
                                            nouveau_lot.setZoneEtEmplacementList(new ArrayList<>());
                                            nouveau_lot.getZoneEtEmplacementList().add(uniqueReceptionPUIAdapte.new ZoneEtEmplacement(zoneId, zone_string, emplacementId, emplacement_string, quantiteSaisie));
                                        }
                                        else
                                        {
                                            for(PH_Reliquat_Reception_Adapte.Lot lotcourant : uniqueReceptionPUIAdapte.getlotList())
                                            {
                                                if(lotcourant.getNumeroLot().contentEquals(nouveau_lot.getNumeroLot()))
                                                    lotPresent = true;
                                            }
                                        }

                                        if(!lotPresent)
                                            uniqueReceptionPUIAdapte.getlotList().add(nouveau_lot);

                                        enregistrerPhReliquat(uniqueReceptionPUIAdapte);
                                        produitCourant = null;
                                        nouveau_lot = null;
                                        reinitialisationInterface();
                                    });
                                }
                            }
                        }
                        else
                        {
                            //le produit n'est pas trouvé en base
                            afficherSnackBar("Produit non trouvé");
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
            /** TODO revoir */
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_ZONE:
                    int zoneid = data.getExtras().getInt("zoneid");
                    if(zoneid != -1)
                    {
                        Intent newIntent = new Intent(ScannerReception2025Activity.this, ListeEmplacementCreationActivity.class);
                        Bundle extras = ScannerReception2025Activity.super.getBundle();
                        extras.putInt("zoneid", zoneid);
                        newIntent.putExtras(extras);
                        ScannerReception2025Activity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                    }
                    break;

                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    int emplacementid = data.getExtras().getInt("emplacementId");
                    if(emplacementid != -1)
                    {
                        Depot_Emplacement emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                        ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacementSelectionner.getAdressage().trim());
                        emplacement_courant = emplacementSelectionner;
                        ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.boutonFermeture).performClick();
    }


    public void afficherSnackBar(String message) {
        final InputMethodManager imm = (InputMethodManager)ScannerReception2025Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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

        InputMethodManager imme = (InputMethodManager) ScannerReception2025Activity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
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
        ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.VISIBLE);
    }

    private void enregistrerPhReliquat(PH_Reliquat_Reception_Adapte reliquatReceptionAdapte)
    {
        PH_Reliquat ph_reliquat_base = PH_ReliquatOpenHelper.getPH_ReliquatById(db, reliquatReceptionAdapte.getPhReliquatUID());

        List<PH_Reliquat> listeReliquatAjouter = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeCourante.getNumero(), ph_reliquat_base.getProduitID());
        for(PH_Reliquat reliquatcourant : listeReliquatAjouter)
        {
            PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquatcourant);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, reliquatcourant.getPhiMR4UUID(), reliquatcourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.SUPPR);
        }

        for(PH_Reliquat_Reception_Adapte.Lot lot : reliquatReceptionAdapte.getlotList()) {
            for (PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement : lot.getZoneEtEmplacementList()) {
                Random randomreliquat = new Random();
                int reliquatId = randomreliquat.nextInt();
                if (reliquatId > 0)
                    reliquatId = reliquatId * -1;

                PH_Reliquat phReliquatCourant = ph_reliquat_base;
                phReliquatCourant.setReliquat_UID(reliquatId);
                String numeroLot = lot.getNumeroLot();
                String datePeremption = lot.getDatePeremption();
                String zoneName = zoneEtEmplacement.getZoneName();
                String emplacementName = zoneEtEmplacement.getEmplacementName();
                String numero_Serie = lot.getNumero_serie();
                int quantite = zoneEtEmplacement.getQuantite();

                phReliquatCourant.setLot(numeroLot.trim());
                phReliquatCourant.setSerie(numero_Serie.trim());
                phReliquatCourant.setPeremptionDate(datePeremption.trim());

                if (commandeCourante.getRef_Depot_Dest().contains("-PAD")) {
                    phReliquatCourant.setZone("RECEPTION");
                    phReliquatCourant.setEmplacement("RECEPTION-" + commandeCourante.getNumero() + "-" + commandeCourante.getPatient_identite());
                } else {
                    phReliquatCourant.setZone(zoneName.trim());
                    phReliquatCourant.setEmplacement(emplacementName.trim());
                }
                phReliquatCourant.setQteLivraison(quantite);
                phReliquatCourant.setBL_Numero("");
                phReliquatCourant.setScanValue("");

                long rowID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, phReliquatCourant);
                if(rowID != -1)
                {
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, phReliquatCourant.getPhiMR4UUID(), phReliquatCourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                }
            }
        }


        ElementASynchroniserOpenHelper.toutSynchroniser(ScannerReception2025Activity.this, db, utilisateurConnecte, false);
    }
}
