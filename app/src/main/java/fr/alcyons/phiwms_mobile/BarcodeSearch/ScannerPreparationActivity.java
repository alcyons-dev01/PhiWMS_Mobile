package fr.alcyons.phiwms_mobile.BarcodeSearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import com.google.android.material.snackbar.Snackbar;
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
import android.widget.TextView;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.NewControleRetourMultipleContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.NewControleRetourUniqueContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.NewReceptionPADContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.NewReceptionPUIContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.NewUniqueReceptionPUIContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PreparationMultipleContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PreparationSimpleContext;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
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
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_ControleRetour_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeEmplacementCreationActivity;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeZoneCreationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;


public class ScannerPreparationActivity extends ServiceActivity {
    // INTENT
    String scannerContexte;
    int scannerContexteInt;
    int preparationID;
    int ph_preparation_ligne_id;
    List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotAdapteList;
    List<PH_Preparation_Ligne_Preparation_Adapte> phPreparationLignePreparationAdapte_List;
    PH_Preparation_Ligne_Preparation_Adapte phPreparationLignePreparationAdapte;
    List<String> listGTIN;
    public PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotCourant;
    String designationProduitCourant;
    String referenceProduitCourant;
    int qteDemander;
    int qtePreparerProduitCourant;
    PH_Preparation preparation_courante;
    List<String> liste_lot;
    String ordreTri;
    int counter;
    CountDownTimer yourCountDownTimer;

    //Gestion des réceptions
    int receptionID;
    Commande commandeCourante;
    //pour un scan multiple
        List<PH_Reliquat_ReceptionPUI_Adapte> list_reliquat_receptionPuiAdapte;
    //pour un scan unitaire
        PH_Reliquat_ReceptionPUI_Adapte uniqueReceptionPUIAdapte;
        PH_Reliquat reliquatCourant;

    // CONTEXTE
    PreparationSimpleContext preparationSimpleContext;
    PreparationMultipleContext preparationMultipleContext;
    NewReceptionPUIContext newReceptionPUIContext;
    NewUniqueReceptionPUIContext newUniqueReceptionPUIContext;
    NewReceptionPADContext newReceptionPADContext;
    NewControleRetourUniqueContext newControleRetourUniqueContext;
    NewControleRetourMultipleContext newControleRetourMultipleContext;

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

    String subject;
    String body;

    //Gestion reception PAD
    List<ObjetReceptionScannee> listObjet_scanne;
    List<Integer> liste_id_reliquat;
    ImageView imageValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_preparation);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // INTENT
        intent = ScannerPreparationActivity.this.getIntent();
        scannerContexte = intent.getExtras().getString("contexte");
        scannerContexteInt = Integer.parseInt(scannerContexte);
        preparationID = intent.getExtras().getInt("preparationId");
        ph_preparation_ligne_id = intent.getExtras().getInt("preparationLigneId");
        lotAdapteList = (List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>) intent.getExtras().getSerializable("lotAdapteList");
        phPreparationLignePreparationAdapte_List = (List<PH_Preparation_Ligne_Preparation_Adapte>) intent.getExtras().getSerializable("lotAdapteList");
        phPreparationLignePreparationAdapte = (PH_Preparation_Ligne_Preparation_Adapte) intent.getExtras().getSerializable("ph_preparationLigneAdapte");
        liste_lot = intent.getExtras().getStringArrayList("liste_lot");
        ordreTri = intent.getExtras().getString("ordreTri");
        receptionID = intent.getExtras().getInt("ReceptionID");
        list_reliquat_receptionPuiAdapte = (List<PH_Reliquat_ReceptionPUI_Adapte>) intent.getExtras().getSerializable("ReceptionPUIAdapte");
        uniqueReceptionPUIAdapte = (PH_Reliquat_ReceptionPUI_Adapte) intent.getExtras().getSerializable("UniqueReceptionPUIAdapte");
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
        if (scannerContexteInt == R.string.scannerContextPreparationSimple || scannerContexteInt == R.string.scannerContextPreparationMultiple) {
            preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparationID);
            Depot depotdestinataire = DepotOpenHelper.getDepotParID(db, preparation_courante.getDepotDestinataireID());
            String depotText = depotdestinataire.getNom();
            if (utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depotdestinataire.getStructure().contentEquals("PAD")) {
                depotText = "Patient - " + depotdestinataire.getPAD_IPP();
            }

            depot.setText(depotText);
            numPreparation.setText("#" + preparationID);
        } else if (scannerContexteInt == R.string.scannerContextUniqueNewReceptionPUI || scannerContexteInt == R.string.scannerContextNewReceptionPUI || scannerContexteInt == R.string.scannerContextNewReceptionPAD) {
            commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
            numPreparation.setText("#" + commandeCourante.getNumero());
            depot.setText(commandeCourante.getFournisseur());
        }


        // CONTEXTE
        if(scannerContexteInt == R.string.scannerContextPreparationSimple)
        {
            preparationSimpleContext = new PreparationSimpleContext(this, db, listGTIN, utilisateurConnecte.getId(), ph_preparation_ligne_id, lotAdapteList, phPreparationLignePreparationAdapte, liste_lot);
            PH_Preparation_Ligne courant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparation_ligne_id);
            final PH_Preparation prepa_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparationID);
            //affichage des premieres informations
            designationProduitCourant = courant.getProduitDesignation();
            referenceProduitCourant = courant.getProduitReference();
            qteDemander = courant.getQte_RAL();
            ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
            ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
            ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));

            //on check si une quantité et déjà préparer et on adapte l'affichage
            if (courant.getQte_preparer() != 0) {
                qtePreparerProduitCourant = courant.getQte_RAL() - courant.getQte_APreparer();
                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(qtePreparerProduitCourant));
                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));

            }

            ((TextView) findViewById(R.id.EmplacementLotProduit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Depot depotOrigine = DepotOpenHelper.getDepotParID(db, prepa_courante.getDepotOrigineID());
                    Intent newIntent = new Intent(ScannerPreparationActivity.this, ListeZoneCreationActivity.class);
                    Bundle extras = ScannerPreparationActivity.super.getBundle();
                    extras.putInt("depotID", depotOrigine.getDepot_UID());
                    newIntent.putExtras(extras);
                    ScannerPreparationActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
                }
            });

            if(utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons"))
            {
                ((TextView) findViewById(R.id.instruction)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditTextScanee.setText("010340093567813317230731101129241A\n");
                    }
                });
            }

            if (lotAdapteList.size() > 0)
                lotCourant = lotAdapteList.get(0);

            if (lotCourant != null) {
                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(lotCourant.getEmplacement());
            }
        }
        else if(scannerContexteInt == R.string.scannerContextPreparationMultiple)
        {
            preparationMultipleContext = new PreparationMultipleContext(this, db, listGTIN, utilisateurConnecte.getId(), preparationID, phPreparationLignePreparationAdapte_List, liste_lot);
            final PH_Preparation preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparationID);
            List<PH_Preparation_Ligne> list_preparation_ligne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesAPreparerParPHPreparation(db, preparation_courante);
            PH_Preparation_Ligne first_ligne = list_preparation_ligne.get(0);
            //affichage des premieres informations
            designationProduitCourant = first_ligne.getProduitDesignation();
            referenceProduitCourant = first_ligne.getProduitReference();
            qteDemander = first_ligne.getQte_RAL();
            ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);

            //gestion du clic sur l'emplacement
            ((TextView) findViewById(R.id.EmplacementLotProduit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Depot depotOrigine = DepotOpenHelper.getDepotParID(db, preparation_courante.getDepotOrigineID());
                    Intent newIntent = new Intent(ScannerPreparationActivity.this, ListeZoneCreationActivity.class);
                    Bundle extras = ScannerPreparationActivity.super.getBundle();
                    extras.putInt("depotID", depotOrigine.getDepot_UID());
                    newIntent.putExtras(extras);
                    ScannerPreparationActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
                }
            });

            if(utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons"))
            {
                ((TextView) findViewById(R.id.instruction)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditTextScanee.setText("010340093567813317230731101129241A\n");
                    }
                });
            }
        }
        else if(scannerContexteInt == R.string.scannerContextNewReceptionPUI)
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
            newReceptionPUIContext = new NewReceptionPUIContext(this, db, utilisateurConnecte, listGtinScannee, utilisateurConnecte.getId(), commandeCourante.getID_commande(), liste_reliquat_commande_courante, list_reliquat_receptionPuiAdapte, emplacement_precedent, produit_precedent);
            ((TextView) findViewById(R.id.EmplacementLotProduit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
                    Depot depotOrigine = DepotOpenHelper.getDepotPUI(db);
                    Intent newIntent = new Intent(ScannerPreparationActivity.this, ListeZoneCreationActivity.class);
                    Bundle extras = ScannerPreparationActivity.super.getBundle();
                    extras.putInt("depotID", depotOrigine.getDepot_UID());
                    newIntent.putExtras(extras);
                    ScannerPreparationActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
                }
            });

            PH_Reliquat premier_reliquat = liste_reliquat_commande_courante.get(0);
            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");

        }
        else if(scannerContexteInt == R.string.scannerContextUniqueNewReceptionPUI)
        {
            Depot_Emplacement emplacement_precedent_unique = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
            Produit produit_precedent_unique = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");
            ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
            List<String> listGtinScanneeUnique = new ArrayList<>();
            newUniqueReceptionPUIContext = new NewUniqueReceptionPUIContext(this, db, utilisateurConnecte, listGtinScanneeUnique, utilisateurConnecte.getId(), commandeCourante.getID_commande(), reliquatCourant, uniqueReceptionPUIAdapte, emplacement_precedent_unique, produit_precedent_unique);
            //affichage des premieres informations
            designationProduitCourant = reliquatCourant.getDesignationCourte();
            referenceProduitCourant = reliquatCourant.getProduit_Reference();
            qteDemander = reliquatCourant.getQteCommande();
            if (reliquatCourant.getQteLivraison() > 0) {
                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(reliquatCourant.getQteLivraison()));
                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
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
                    newUniqueReceptionPUIContext.emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone_courante, emplacemement_pui_defaut);
                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(produitCourant.getEmplacement_PUI_Defaut());
                    ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                }
            }
            ((TextView) findViewById(R.id.EmplacementLotProduit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
                    Depot depotOrigine = DepotOpenHelper.getDepotPUI(db);
                    Intent newIntent = new Intent(ScannerPreparationActivity.this, ListeZoneCreationActivity.class);
                    Bundle extras = ScannerPreparationActivity.super.getBundle();
                    extras.putInt("depotID", depotOrigine.getDepot_UID());
                    newIntent.putExtras(extras);
                    ScannerPreparationActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
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
            newReceptionPADContext = new NewReceptionPADContext(this, db, listGtinScannee, utilisateurConnecte, listObjet_scanne, liste_id_reliquat);


            ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.INVISIBLE);
        }
        else if(scannerContexteInt == R.string.scannerContextUniqueNewControleRetour)
        {
            ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
            Retour_Ligne retour_ligne = (Retour_Ligne) intent.getExtras().getSerializable("RetourLigneCourant");
            List<Retour_Ligne_ControleRetour_Adapte.LotAdapte> lotAdapteList = (List<Retour_Ligne_ControleRetour_Adapte.LotAdapte>) intent.getExtras().getSerializable("ListeAdapteRetour");
            newControleRetourUniqueContext = new NewControleRetourUniqueContext(this, db, utilisateurConnecte, retour_ligne, lotAdapteList);
            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
            if(retour_ligne != null)
            {
                ((TextView) findViewById(R.id.designationProduit)).setText(retour_ligne.getProduit_Designation());
                ((TextView) findViewById(R.id.referenceProduit)).setText(retour_ligne.getProduit_Reference());
                ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf((int)retour_ligne.getQte_Demander()));
                ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf((int)retour_ligne.getQte_Retourner()));
            }
        }
        else if(scannerContexteInt == R.string.scannerContextMultipleNewControleRetour)
        {
            ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
            int retour_id = intent.getExtras().getInt("RetourId");
            newControleRetourMultipleContext = new NewControleRetourMultipleContext(this, db, utilisateurConnecte, retour_id);
        }

            //on cache le clavier à chaque fois que l'éditText reprend le focus après l'avoir perdu
            EditTextScanee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    InputMethodManager imm = (InputMethodManager) ScannerPreparationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });

            EditTextScanee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) ScannerPreparationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
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
                    if(scannerContexteInt == R.string.scannerContextPreparationSimple)
                    {
                        if (!((TextView) findViewById(R.id.qteSaisie)).getText().toString().contentEquals(""))
                            preparationSimpleContext.ValiderScan(Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString()));
                        scannerSearchOnlyBundle.putSerializable("lotAdapteList", (Serializable) preparationSimpleContext.liste_preparation_liste_adapte);
                        scannerSearchOnlyBundle.putStringArrayList("liste_lot", (ArrayList<String>) preparationSimpleContext.liste_lot);
                        codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                    }
                    else if(scannerContexteInt == R.string.scannerContextPreparationMultiple)
                    {
                        if (!((TextView) findViewById(R.id.qteSaisie)).getText().toString().contentEquals(""))
                            preparationMultipleContext.ValiderScan(Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString()));
                        scannerSearchOnlyBundle.putSerializable("lotAdapteList", (Serializable) preparationMultipleContext.phPreparationLignePreparationAdapte_List);
                        scannerSearchOnlyBundle.putStringArrayList("liste_lot", (ArrayList<String>) preparationMultipleContext.liste_lot);
                        codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                    }
                    else if(scannerContexteInt == R.string.scannerContextNewReceptionPUI)
                    {
                        if(newReceptionPUIContext.nouveau_lot != null && newReceptionPUIContext.emplacement_courant != null)
                        {
                            boolean confirmation = Alerte.afficherAlerte(ScannerPreparationActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
                            if(confirmation)
                            {
                                newReceptionPUIContext.ValiderScan();
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
                        scannerSearchOnlyBundle.putSerializable("EmplacementPrecedent", (Serializable) newReceptionPUIContext.emplacementPrecedent);
                        scannerSearchOnlyBundle.putSerializable("ProduitPrecedent", (Serializable) newReceptionPUIContext.produitPrecedent);
                        scannerSearchOnlyBundle.putSerializable("reliquatAdapteList", (Serializable) newReceptionPUIContext.list_reliquat_receptionPuiAdapte);
                        codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                    }
                    else if(scannerContexteInt == R.string.scannerContextUniqueNewReceptionPUI)
                    {
                        if(newUniqueReceptionPUIContext.nouveau_lot != null && newUniqueReceptionPUIContext.emplacement_courant != null)
                        {
                            boolean confirmation = Alerte.afficherAlerte(ScannerPreparationActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
                            if(confirmation)
                            {
                                newUniqueReceptionPUIContext.ValiderScan();
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
                        scannerSearchOnlyBundle.putSerializable("EmplacementPrecedent", (Serializable) newUniqueReceptionPUIContext.emplacementPrecedent);
                        scannerSearchOnlyBundle.putSerializable("ProduitPrecedent", (Serializable) newUniqueReceptionPUIContext.produitPrecedent);
                        scannerSearchOnlyBundle.putSerializable("reliquatAdapte", (Serializable) newUniqueReceptionPUIContext.phReliquatReceptionPUIAdapte_courant);
                        codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                    }
                    else if(scannerContexteInt == R.string.scannerContextNewReceptionPAD)
                    {
                        if(!newReceptionPADContext.objetReceptionScanneeCourant.getGs1_scannee().contentEquals(""))
                        {
                            boolean confirmation = Alerte.afficherAlerte(ScannerPreparationActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
                            if(confirmation)
                            {
                                newReceptionPADContext.AjoutDuProduit();
                            }
                        }
                        //stringList = newReceptionPADContext.stringList;
                        scannerSearchOnlyBundle.putSerializable("listeString", (Serializable) newReceptionPADContext.list_result);
                        codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;
                    }
                    else if(scannerContexteInt == R.string.scannerContextUniqueNewControleRetour)
                    {
                        if(!newControleRetourUniqueContext.validation)
                        {
                            boolean confirmation = Alerte.afficherAlerte(ScannerPreparationActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
                            if(confirmation)
                            {
                                scannerSearchOnlyBundle.putString("numLot", newControleRetourUniqueContext.lot);
                                scannerSearchOnlyBundle.putString("numSerie", newControleRetourUniqueContext.serie);
                                scannerSearchOnlyBundle.putString("datePeremption", newControleRetourUniqueContext.date_peremption_courant);
                                int quantite = Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString());
                                scannerSearchOnlyBundle.putInt("qteActuelle", quantite);
                            }
                        }
                        else if(!newControleRetourUniqueContext.lot.contentEquals(""))
                        {
                            scannerSearchOnlyBundle.putString("numLot", newControleRetourUniqueContext.lot);
                            scannerSearchOnlyBundle.putString("numSerie", newControleRetourUniqueContext.serie);
                            scannerSearchOnlyBundle.putString("datePeremption", newControleRetourUniqueContext.date_peremption_courant);
                            scannerSearchOnlyBundle.putInt("qteActuelle", newControleRetourUniqueContext.quantiteAAfficher);
                        }
                    }
                    else if(scannerContexteInt == R.string.scannerContextMultipleNewControleRetour)
                    {
                        if(!newControleRetourMultipleContext.validation)
                        {
                            boolean confirmation = Alerte.afficherAlerte(ScannerPreparationActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
                            if(confirmation)
                            {
                                scannerSearchOnlyBundle.putString("numLot", newControleRetourMultipleContext.lot);
                                scannerSearchOnlyBundle.putString("numSerie", newControleRetourMultipleContext.serie);
                                scannerSearchOnlyBundle.putString("datePeremption", newControleRetourMultipleContext.date_peremption_courant);
                                int quantite = Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString());
                                scannerSearchOnlyBundle.putInt("qteActuelle", quantite);
                                scannerSearchOnlyBundle.putInt("retourLigneId", newControleRetourMultipleContext.retour_ligne_courant.get_UID());
                            }
                        }
                        else if(!newControleRetourMultipleContext.lot.contentEquals(""))
                        {
                            scannerSearchOnlyBundle.putString("numLot", newControleRetourMultipleContext.lot);
                            scannerSearchOnlyBundle.putString("numSerie", newControleRetourMultipleContext.serie);
                            scannerSearchOnlyBundle.putString("datePeremption", newControleRetourMultipleContext.date_peremption_courant);
                            scannerSearchOnlyBundle.putInt("qteActuelle", newControleRetourMultipleContext.quantiteAAfficher);
                            scannerSearchOnlyBundle.putInt("retourLigneId", newControleRetourMultipleContext.retour_ligne_courant.get_UID());
                        }
                    }

                    scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                    ScannerPreparationActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                    ScannerPreparationActivity.this.finish();
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
                            if(scannerContexteInt == R.string.scannerContextPreparationSimple)
                            {
                                preparationSimpleContext.onTextWatcher(s);

                                if (preparationSimpleContext.lot_courant != null) {
                                    if(preparationSimpleContext.emplacementLotVerifierSimple(preparationSimpleContext.emplacement_courant.getAdressage(), preparationSimpleContext.lot_courant.getNumLot()))
                                    {
                                        blinkImageValidation();
                                        ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                preparationSimpleContext.ValiderScan(Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString()));
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
                                        //si l'emplacement scanné est différent de l'emplacement du lot scanné on affiche un message d'erreur
                                        afficherAlerteErreurEmplacement(ScannerPreparationActivity.this, ScannerPreparationActivity.this.getLayoutInflater(), preparationSimpleContext.emplacementDisponible, preparationSimpleContext.liste_emplacement_disponible);
                                    }

                                    if (preparationSimpleContext.emplacement_courant != null) {
                                        ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(preparationSimpleContext.emplacement_courant.getAdressage());
                                        if(preparationSimpleContext.lot_courant != null)
                                        {
                                            blinkImageValidation();
                                            ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.VISIBLE);
                                            ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    preparationSimpleContext.ValiderScan(Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString()));
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
                                            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                        }
                                    }

                                    final PH_Preparation_Ligne ligne_courante = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparation_ligne_id);
                                    int qte_restante = ligne_courante.getQte_APreparer();
                                    //gestion de l'affichage de la date de péremption
                                    lotCourant = preparationSimpleContext.lot_courant;

                                    String dateDePeremption = lotCourant.getDatePeremption();
                                    String[] dateDePeremtpionTab = dateDePeremption.split("-");
                                    if(dateDePeremtpionTab.length == 3)
                                    {
                                        dateDePeremption = dateDePeremtpionTab[2] + "/" + dateDePeremtpionTab[1] + "/" + dateDePeremtpionTab[0];
                                    }


                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);
                                    //((TextView) findViewById(R.id.EmplacementLotProduit)).setText(lotCourant.getEmplacement());


                                    if (preparationSimpleContext.produit.getCond_distrib() < qte_restante)
                                        qte_restante = (int) preparationSimpleContext.produit.getCond_distrib();
                                    ((TextView) findViewById(R.id.numeroLot)).setText(lotCourant.getNumLot());
                                    if (lotCourant.getNumSerie() != null && !lotCourant.getNumSerie().contentEquals("")) {
                                        ((TextView) findViewById(R.id.numeroSerie)).setText(lotCourant.getNumSerie());
                                    } else {
                                        ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                                    }

                                    //lotCourant.setQteSaisie(qte_restante);
                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qte_restante));

                                    int qtePreparerProduitCourant = ligne_courante.getQte_RAL() - ligne_courante.getQte_APreparer();
                                    ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(qtePreparerProduitCourant));
                                    ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                                    ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));

                                    //gestion du clic sur le numberPicker
                                    final int finalQte_restante = qte_restante;
                                    ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Context context = ScannerPreparationActivity.this;
                                            final Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lotCourant.getStockLotEmplacementID());

                                            String title = lotCourant.getNumLot();
                                            String message = "Quantité placée : ";
                                            int maxValue = ligne_courante.getQte_APreparer();
                                            int value = finalQte_restante;

                                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    int qteAprès = (int) (Alerte.aNumberPicker.getValue() * preparationSimpleContext.produit.getCond_distrib());
                                                    //lotCourant.setQteSaisie(qteAprès);
                                                    //((TextView) findViewById(R.id.QteDemandee)).setText(String.valueOf(ligne_courante.getQte_preparer()));

                                                    if (stock_courant != null) {
                                                        stock_courant.setQte_Preparer(lotCourant.getQteSaisie());
                                                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                                    }
                                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                                    dialog.dismiss();
                                                }
                                            };

                                            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, (int) preparationSimpleContext.produit.getCond_distrib());
                                        }
                                    });

                                } else if (preparationSimpleContext.emplacement_courant != null) {
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(preparationSimpleContext.emplacement_courant.getAdressage());
                                    ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                } else {
                                    lotCourant = null;
                                    ((TextView) findViewById(R.id.numeroLot)).setText("");
                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                                    ((TextView) findViewById(R.id.qteSaisie)).setText("");
                                    EditTextScanee.setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_scanner_inside_preparation));
                                }
                            }
                            else if(scannerContexteInt == R.string.scannerContextPreparationMultiple)
                            {
                                preparationMultipleContext.onTextWatcher(s);
                                ph_preparation_ligne_id = preparationMultipleContext.preparation_ligne_id;
                                final PH_Preparation_Ligne ligne_courante = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparation_ligne_id);
                                //affichage des premieres informations
                                if (ligne_courante != null) {
                                    designationProduitCourant = ligne_courante.getProduitDesignation();
                                    referenceProduitCourant = ligne_courante.getProduitReference();
                                    qteDemander = ligne_courante.getQte_RAL();
                                    ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                                    ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                                    ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));

                                    if (preparationMultipleContext.emplacement_courant != null) {
                                        ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(preparationMultipleContext.emplacement_courant.getAdressage());
                                    }

                                    if (preparationMultipleContext.lot_courant != null) {
                                        if(preparationMultipleContext.emplacementLotVerifier(preparationMultipleContext.emplacement_courant.getAdressage(), preparationMultipleContext.lot_courant.getNumLot()))
                                        {
                                            blinkImageValidation();
                                            ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.VISIBLE);
                                            ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    preparationMultipleContext.ValiderScan(Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString()));
                                                    ((TextView) findViewById(R.id.designationProduit)).setText("");
                                                    ((TextView) findViewById(R.id.referenceProduit)).setText("");
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
                                            afficherAlerteErreurEmplacement(ScannerPreparationActivity.this, ScannerPreparationActivity.this.getLayoutInflater(), preparationMultipleContext.emplacementDisponible, preparationMultipleContext.liste_emplacement_disponible);
                                            preparationMultipleContext.emplacement_courant = null;
                                            //preparationMultipleContext.lot_courant.setEmplacement(null);
                                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("");
                                            ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");
                                        }


                                        int qte_restante = ligne_courante.getQte_APreparer();
                                        lotCourant = preparationMultipleContext.lot_courant;
                                        if (preparationMultipleContext.produit.getCond_distrib() <= qte_restante)
                                            qte_restante = (int) preparationMultipleContext.produit.getCond_distrib();
                                        ((TextView) findViewById(R.id.numeroLot)).setText(lotCourant.getNumLot());


                                        //gestion de l'affichage de la date de péremption
                                        String dateDePeremption = lotCourant.getDatePeremption();
                                        String[] dateDePeremtpionTab = dateDePeremption.split("-");
                                        dateDePeremption = dateDePeremtpionTab[2] + "/" + dateDePeremtpionTab[1] + "/" + dateDePeremtpionTab[0];


                                        ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);
                                        //((TextView) findViewById(R.id.EmplacementLotProduit)).setText(lotCourant.getEmplacement());

                                        ((TextView) findViewById(R.id.numeroLot)).setText(lotCourant.getNumLot());

                                        if (lotCourant.getNumSerie() != null && !lotCourant.getNumSerie().contentEquals("")) {
                                            ((TextView) findViewById(R.id.numeroSerie)).setText(lotCourant.getNumSerie());
                                        } else {
                                            ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                                        }


                                        ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qte_restante));

                                        if (qte_restante == 0) {
                                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(qtePreparerProduitCourant));
                                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.GONE);
                                            ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(ligne_courante.getQte_RAL() - ligne_courante.getQte_APreparer()));
                                            ((TextView) findViewById(R.id.quantiteProduit)).setTextColor(ScannerPreparationActivity.this.getResources().getColor(R.color.vert));
                                            ((TextView) findViewById(R.id.quantiteProduit)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);

                                            ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
                                            ((TextView) findViewById(R.id.numeroLot)).setText("");
                                            ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                                            ((TextView) findViewById(R.id.qteSaisie)).setText("");

                                            afficherSnackBar("Produit déjà préparé en intégralité");
                                        } else {
                                            int qtePreparerProduitCourant = ligne_courante.getQte_RAL() - ligne_courante.getQte_APreparer();
                                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(qtePreparerProduitCourant));
                                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                                            ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                                        }


                                        //gestion du clic sur le numberPicker
                                        final int finalQte_restante = qte_restante;
                                        ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Context context = ScannerPreparationActivity.this;
                                                final Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lotCourant.getStockLotEmplacementID());

                                                String title = lotCourant.getNumLot();
                                                String message = "Quantité placée : ";
                                                int value_max = ligne_courante.getQte_APreparer();

                                                int maxValue = value_max;
                                                int value = finalQte_restante;

                                                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        int qteAprès = (int) (Alerte.aNumberPicker.getValue() * preparationMultipleContext.produit.getCond_distrib());
                                                        //lotCourant.setQteSaisie(qteAprès);
                                                        //((TextView) findViewById(R.id.QteDemandee)).setText(String.valueOf(ligne_courante.getQte_preparer()));

                                                        if (stock_courant != null) {
                                                            stock_courant.setQte_Preparer(lotCourant.getQteSaisie());
                                                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                                        }
                                                        ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                                        dialog.dismiss();
                                                    }
                                                };

                                                Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, (int) preparationMultipleContext.produit.getCond_distrib());
                                            }
                                        });

                                    } else {
                                        lotCourant = null;
                                        ((TextView) findViewById(R.id.numeroLot)).setText("");
                                        ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                                        ((TextView) findViewById(R.id.qteSaisie)).setText("");
                                        EditTextScanee.setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_scanner_inside_preparation));
                                    }
                                } else if (preparationMultipleContext.emplacement_courant != null) {
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(preparationMultipleContext.emplacement_courant.getAdressage());
                                    ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                } else {
                                    ((TextView) findViewById(R.id.designationProduit)).setText("");
                                    ((TextView) findViewById(R.id.referenceProduit)).setText("");
                                    ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(""));
                                    lotCourant = null;
                                    ((TextView) findViewById(R.id.numeroLot)).setText("");
                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                                    ((TextView) findViewById(R.id.qteSaisie)).setText("");
                                    EditTextScanee.setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_scanner_inside_preparation));
                                }
                            }
                            else if(scannerContexteInt == R.string.scannerContextNewReceptionPUI)
                            {
                                newReceptionPUIContext.onTextWatcher(s);
                                if (newReceptionPUIContext.nouveau_lot != null) {
                                    ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");

                                    designationProduitCourant = newReceptionPUIContext.reliquat_courant.getDesignationCourte();
                                    referenceProduitCourant = newReceptionPUIContext.reliquat_courant.getProduit_Reference();
                                    qteDemander = newReceptionPUIContext.reliquat_courant.getQteCommande();
                                    if (newReceptionPUIContext.reliquat_courant.getQteLivraison() > 0) {
                                        ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(newReceptionPUIContext.reliquat_courant.getQteLivraison()));
                                        ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                                    }
                                    ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                                    ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                                    ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));
                                    ((TextView) findViewById(R.id.numeroLot)).setText(newReceptionPUIContext.nouveau_lot.getNumeroLot());
                                    if (newReceptionPUIContext.nouveau_lot.getNumero_serie() != null && !newReceptionPUIContext.nouveau_lot.getNumero_serie().contentEquals("")) {
                                        ((TextView) findViewById(R.id.numeroSerie)).setText(newReceptionPUIContext.nouveau_lot.getNumero_serie());
                                    } else {
                                        ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                                    }

                                    String dateDePeremption = newReceptionPUIContext.nouveau_lot.getDatePeremption();
                                    String[] dateDePeremtpionTab = dateDePeremption.split("-");
                                    if(dateDePeremtpionTab.length > 1 )
                                    {
                                        dateDePeremption = dateDePeremtpionTab[2] + "/" + dateDePeremtpionTab[1] + "/" + dateDePeremtpionTab[0];
                                    }

                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);

                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(newReceptionPUIContext.qte_lot_courant));

                                    //gestion du numberPicker pour changer la quantité lors d'un scan
                                    ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Context context = ScannerPreparationActivity.this;
                                            if (newReceptionPUIContext.emplacement_courant != null) {
                                                yourCountDownTimer.cancel();
                                                ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                                                ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
                                            }
                                            String title = newReceptionPUIContext.nouveau_lot.getNumeroLot();
                                            String message = "Quantité réceptionnée : ";
                                            int value_max = (int) newReceptionPUIContext.reliquat_courant.getQteReliquat_X();
                                            int maxValue = value_max;
                                            int value = maxValue;

                                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    int qteAprès = Alerte.aNumberPicker.getValue() * newReceptionPUIContext.conditionnement_achat;
                                                    newReceptionPUIContext.qte_lot_courant = qteAprès;
                                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                                    dialog.dismiss();

                                                    if (newReceptionPUIContext.emplacement_courant != null) {
                                                        ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                                                        ((LinearLayout) findViewById(R.id.validationScan)).performClick();
                                                    }
                                                }
                                            };

                                            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, newReceptionPUIContext.conditionnement_achat);
                                        }
                                    });

                                }

                                if (newReceptionPUIContext.emplacement_courant != null) {
                                    ((TextView) findViewById(R.id.instruction)).setText("");
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(newReceptionPUIContext.emplacement_courant.getAdressage());
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
                                            newReceptionPUIContext.ValiderScan();
                                            yourCountDownTimer.cancel();
                                            findViewById(R.id.boutonFermeture).performClick();
                                        }
                                    });
                                }
                            }
                            else if(scannerContexteInt == R.string.scannerContextUniqueNewReceptionPUI)
                            {
                                newUniqueReceptionPUIContext.onTextWatcher(s);
                                if (newUniqueReceptionPUIContext.nouveau_lot != null) {
                                    ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");
                                    designationProduitCourant = newUniqueReceptionPUIContext.reliquat_courant.getDesignationCourte();
                                    referenceProduitCourant = newUniqueReceptionPUIContext.reliquat_courant.getProduit_Reference();
                                    qteDemander = newUniqueReceptionPUIContext.reliquat_courant.getQteCommande();
                                    if (newUniqueReceptionPUIContext.reliquat_courant.getQteLivraison() > 0) {
                                        ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(newUniqueReceptionPUIContext.reliquat_courant.getQteLivraison()));
                                        ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                                    }
                                    ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                                    ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                                    ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));
                                    ((TextView) findViewById(R.id.numeroLot)).setText(newUniqueReceptionPUIContext.nouveau_lot.getNumeroLot());
                                    if (newUniqueReceptionPUIContext.nouveau_lot.getNumero_serie() != null && !newUniqueReceptionPUIContext.nouveau_lot.getNumero_serie().contentEquals("")) {
                                        ((TextView) findViewById(R.id.numeroSerie)).setText(newUniqueReceptionPUIContext.nouveau_lot.getNumero_serie());
                                    } else {
                                        ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                                    }

                                    String dateDePeremption = newUniqueReceptionPUIContext.nouveau_lot.getDatePeremption();
                                    String[] dateDePeremtpionTab = dateDePeremption.split("-");
                                    dateDePeremption = dateDePeremtpionTab[2] + "/" + dateDePeremtpionTab[1] + "/" + dateDePeremtpionTab[0];

                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);

                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(newUniqueReceptionPUIContext.qte_lot_courant));

                                    //gestion du numberPicker pour changer la quantité lors d'un scan
                                    ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Context context = ScannerPreparationActivity.this;
                                            if (newUniqueReceptionPUIContext.emplacement_courant != null) {
                                                yourCountDownTimer.cancel();
                                                ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                                                ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
                                            }
                                            String title = newUniqueReceptionPUIContext.nouveau_lot.getNumeroLot();
                                            String message = "Quantité réceptionnée : ";
                                            int value_max = (int) newUniqueReceptionPUIContext.reliquat_courant.getQteReliquat_X();
                                            int maxValue = value_max;
                                            int value = maxValue;

                                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    int qteAprès = Alerte.aNumberPicker.getValue() * newUniqueReceptionPUIContext.conditionnement_achat;
                                                    newUniqueReceptionPUIContext.qte_lot_courant = qteAprès;
                                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                                    dialog.dismiss();

                                                    if (newUniqueReceptionPUIContext.emplacement_courant != null) {
                                                        ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                                                        ((LinearLayout) findViewById(R.id.validationScan)).performClick();
                                                    }


                                                }
                                            };

                                            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, newUniqueReceptionPUIContext.conditionnement_achat);
                                        }
                                    });

                                }

                                if (newUniqueReceptionPUIContext.emplacement_courant != null) {
                                    ((TextView) findViewById(R.id.instruction)).setText("");
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(newUniqueReceptionPUIContext.emplacement_courant.getAdressage());
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
                                            newUniqueReceptionPUIContext.ValiderScan();
                                            yourCountDownTimer.cancel();
                                            findViewById(R.id.boutonFermeture).performClick();
                                        }
                                    });
                                }
                            }
                            else if(scannerContexteInt == R.string.scannerContextNewReceptionPAD)
                            {
                                newReceptionPADContext.onTextWatcher(s);
                                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");

                                if (newReceptionPADContext.ph_reliquat_courant != null) {
                                    blinkImageValidation();
                                    ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.VISIBLE);
                                    ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            newReceptionPADContext.AjoutDuProduit();
                                            ((TextView) findViewById(R.id.designationProduit)).setText("");
                                            ((TextView) findViewById(R.id.referenceProduit)).setText("");
                                            ((TextView) findViewById(R.id.quantiteProduit)).setText("");
                                            ((TextView) findViewById(R.id.numeroLot)).setText("");
                                            ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                                            ((TextView) findViewById(R.id.qteSaisie)).setText("");
                                            ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.GONE);
                                            /*findViewById(R.id.boutonFermeture).performClick();*/
                                        }
                                    });
                                    designationProduitCourant = newReceptionPADContext.ph_reliquat_courant.getDesignationCourte();
                                    referenceProduitCourant = newReceptionPADContext.ph_reliquat_courant.getProduit_Reference();
                                    qteDemander = newReceptionPADContext.ph_reliquat_courant.getQteCommande();
                                    if (newReceptionPADContext.ph_reliquat_courant.getQteLivraison() > 0) {
                                        ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(newReceptionPADContext.ph_reliquat_courant.getQteLivraison()));
                                        ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                                    }
                                    ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                                    ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                                    ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));
                                    ((TextView) findViewById(R.id.numeroLot)).setText(newReceptionPADContext.numeroLotProduitCourant);
                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText(newReceptionPADContext.peremptionProduitCourant);
                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(newReceptionPADContext.quantiteProduitCourant));
                                    if (newReceptionPADContext.numeroSerieProduitCourant != null && !newReceptionPADContext.numeroSerieProduitCourant.contentEquals("")) {
                                        ((TextView) findViewById(R.id.numeroSerie)).setText(newReceptionPADContext.numeroSerieProduitCourant);
                                    } else {
                                        ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                                    }


                                    //gestion du numberPicker pour changer la quantité lors d'un scan
                                    ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Context context = ScannerPreparationActivity.this;

                                            String title = newReceptionPADContext.numeroLotProduitCourant;
                                            String message = "Quantité réceptionnée : ";
                                            int value_max = (int) newReceptionPADContext.ph_reliquat_courant.getQteReliquat_X();
                                            int maxValue = value_max;
                                            int value = maxValue;

                                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    int qteAprès = Alerte.aNumberPicker.getValue() * newReceptionPADContext.conditionnementAchat;
                                                    //newReceptionPADContext.objetReceptionScanneeCourant.setQuantiteScannee(qteAprès);
                                                    newReceptionPADContext.quantite_a_afficher = qteAprès;
                                                    //newReceptionPADContext.ModificationDuProduit();
                                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                                    dialog.dismiss();
                                                }
                                            };

                                            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, newReceptionPADContext.conditionnementAchat);
                                        }
                                    });
                                } else {
                                    ((TextView) findViewById(R.id.designationProduit)).setText("");
                                    ((TextView) findViewById(R.id.referenceProduit)).setText("");
                                    ((TextView) findViewById(R.id.quantiteProduit)).setText("");
                                    ((TextView) findViewById(R.id.numeroLot)).setText("");
                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                                    ((TextView) findViewById(R.id.qteSaisie)).setText("");
                                }
                            }
                            else if(scannerContexteInt == R.string.scannerContextUniqueNewControleRetour)
                            {
                                newControleRetourUniqueContext.onTextWatcher(s);
                                if(!newControleRetourUniqueContext.validation)
                                {
                                    blinkImageValidation();
                                    ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.VISIBLE);
                                    ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            newControleRetourUniqueContext.validation = true;
                                            findViewById(R.id.boutonFermeture).performClick();
                                        }
                                    });

                                    ((TextView) findViewById(R.id.numeroLot)).setText(newControleRetourUniqueContext.lot);
                                    String dateDePeremption = (newControleRetourUniqueContext.date_peremption_courant);
                                    String[] dateDePeremtpionTab = dateDePeremption.split("-");
                                    dateDePeremption = dateDePeremtpionTab[2] + "/" + dateDePeremtpionTab[1] + "/" + dateDePeremtpionTab[0];

                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);

                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(newControleRetourUniqueContext.quantiteAAfficher));

                                    //gestion du numberPicker pour changer la quantité lors d'un scan
                                    ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Context context = ScannerPreparationActivity.this;

                                            String title = newControleRetourUniqueContext.lot;
                                            String message = "Quantité retournée : ";
                                            int value_max = (int) newControleRetourUniqueContext.quantiteRestante;
                                            int maxValue = value_max;
                                            int value = maxValue;

                                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    int qteAprès = Alerte.aNumberPicker.getValue() * newControleRetourUniqueContext.conditionnementDistribution;
                                                    newControleRetourUniqueContext.quantiteAAfficher = qteAprès;
                                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                                    dialog.dismiss();
                                                }
                                            };

                                            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, newControleRetourUniqueContext.conditionnementDistribution);
                                        }
                                    });
                                }
                            }
                            else if(scannerContexteInt == R.string.scannerContextMultipleNewControleRetour)
                            {
                                newControleRetourMultipleContext.onTextWatcher(s);
                                if(!newControleRetourMultipleContext.validation)
                                {
                                    blinkImageValidation();
                                    ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.VISIBLE);
                                    ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            newControleRetourMultipleContext.validation = true;
                                            findViewById(R.id.boutonFermeture).performClick();
                                        }
                                    });
                                    ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.designationProduit)).setText(newControleRetourMultipleContext.retour_ligne_courant.getProduit_Designation());
                                    ((TextView) findViewById(R.id.referenceProduit)).setText(newControleRetourMultipleContext.retour_ligne_courant.getProduit_Reference());
                                    ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf((int)newControleRetourMultipleContext.retour_ligne_courant.getQte_Demander()));
                                    ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf((int)newControleRetourMultipleContext.retour_ligne_courant.getQte_Retourner()));
                                    ((TextView) findViewById(R.id.numeroLot)).setText(newControleRetourMultipleContext.lot);
                                    String dateDePeremption = (newControleRetourMultipleContext.date_peremption_courant);
                                    String[] dateDePeremtpionTab = dateDePeremption.split("-");
                                    dateDePeremption = dateDePeremtpionTab[2] + "/" + dateDePeremtpionTab[1] + "/" + dateDePeremtpionTab[0];

                                    ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);

                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(newControleRetourMultipleContext.quantiteAAfficher));

                                    //gestion du numberPicker pour changer la quantité lors d'un scan
                                    ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Context context = ScannerPreparationActivity.this;

                                            String title = newControleRetourMultipleContext.lot;
                                            String message = "Quantité retournée : ";
                                            int value_max = (int) newControleRetourMultipleContext.quantiteRestante;
                                            int maxValue = value_max;
                                            int value = maxValue;

                                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    int qteAprès = Alerte.aNumberPicker.getValue() * newControleRetourMultipleContext.conditionnementDistribution;
                                                    newControleRetourMultipleContext.quantiteAAfficher = qteAprès;
                                                    ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                                    dialog.dismiss();
                                                }
                                            };

                                            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, newControleRetourMultipleContext.conditionnementDistribution);
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

            if(scannerContexteInt == R.string.scannerContextPreparationSimple)
            {
                switch (requestCode)
                {
                    case CodesEchangesActivites.RESULT_ZONE:
                        int zoneid = data.getExtras().getInt("zoneid");
                        if(zoneid != -1)
                        {
                            Intent newIntent = new Intent(ScannerPreparationActivity.this, ListeEmplacementCreationActivity.class);
                            Bundle extras = ScannerPreparationActivity.super.getBundle();
                            extras.putInt("zoneid", zoneid);
                            newIntent.putExtras(extras);
                            ScannerPreparationActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                        }
                        break;

                    case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                        int emplacementid = data.getExtras().getInt("emplacementId");
                        if(emplacementid != -1)
                        {
                            Depot_Emplacement emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacementSelectionner.getAdressage().trim());
                            preparationSimpleContext.emplacement_courant = emplacementSelectionner;
                            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                            //lotCourant.setEmplacement(emplacementSelectionner.getAdressage().trim());
                            if(preparationSimpleContext.lot_courant != null)
                            {
                                if(preparationSimpleContext.emplacementLotVerifierSimple(preparationSimpleContext.emplacement_courant.getAdressage(), preparationSimpleContext.lot_courant.getNumLot()))
                                {
                                    blinkImageValidation();
                                    ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.VISIBLE);
                                    ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            preparationSimpleContext.ValiderScan(Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString()));
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
                                    afficherAlerteErreurEmplacement(ScannerPreparationActivity.this, ScannerPreparationActivity.this.getLayoutInflater(), preparationSimpleContext.emplacementDisponible, preparationSimpleContext.liste_emplacement_disponible);
                                    preparationSimpleContext.emplacement_courant = null;
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("");
                                    ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");
                                }
                            }
                            else
                            {
                                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                            }
                        }
                        break;
                }
            }
            else if(scannerContexteInt == R.string.scannerContextPreparationMultiple)
            {
                switch (requestCode)
                {
                    case CodesEchangesActivites.RESULT_ZONE:
                        int zoneid = data.getExtras().getInt("zoneid");
                        if(zoneid != -1)
                        {
                            Intent newIntent = new Intent(ScannerPreparationActivity.this, ListeEmplacementCreationActivity.class);
                            Bundle extras = ScannerPreparationActivity.super.getBundle();
                            extras.putInt("zoneid", zoneid);
                            newIntent.putExtras(extras);
                            ScannerPreparationActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                        }
                        break;

                    case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                        int emplacementid = data.getExtras().getInt("emplacementId");
                        if(emplacementid != -1)
                        {
                            Depot_Emplacement emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacementSelectionner.getAdressage().trim());
                            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                           /* lotCourant.setEmplacement(emplacementSelectionner.getAdressage().trim());
                            Stock_Lot_Emplacement_Light stock_lot_emplacement_light = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lotCourant.getStockLotEmplacementID());
                            stock_lot_emplacement_light.setEmplacement(emplacementSelectionner.getAdressage());
                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_lot_emplacement_light);*/
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
                                    afficherAlerteErreurEmplacement(ScannerPreparationActivity.this, ScannerPreparationActivity.this.getLayoutInflater(), preparationMultipleContext.emplacementDisponible, preparationMultipleContext.liste_emplacement_disponible);
                                    preparationMultipleContext.emplacement_courant = null;
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("");
                                    ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");
                                }
                            }
                            else
                            {
                                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                            }
                            //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement_light.getPhiMR4UUID(), stock_lot_emplacement_light.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                        }
                        break;
                }
            }
            else if(scannerContexteInt == R.string.scannerContextUniqueNewReceptionPUI)
            {
                switch (requestCode) {
                    case CodesEchangesActivites.RESULT_ZONE:
                        int zoneid = data.getExtras().getInt("zoneid");
                        if(zoneid != -1)
                        {
                            Intent newIntent = new Intent(ScannerPreparationActivity.this, ListeEmplacementCreationActivity.class);
                            Bundle extras = ScannerPreparationActivity.super.getBundle();
                            extras.putInt("zoneid", zoneid);
                            newIntent.putExtras(extras);
                            ScannerPreparationActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                        }
                        break;

                    case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                        int emplacementid = data.getExtras().getInt("emplacementId");
                        if(emplacementid != -1)
                        {
                            Depot_Emplacement emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacementSelectionner.getAdressage().trim());
                            newUniqueReceptionPUIContext.emplacement_courant = emplacementSelectionner;
                            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                        }
                        break;
                }
            }
            else if(scannerContexteInt == R.string.scannerContextNewReceptionPUI)
            {
                switch (requestCode) {
                    case CodesEchangesActivites.RESULT_ZONE:
                        int zoneid = data.getExtras().getInt("zoneid");
                        if(zoneid != -1)
                        {
                            Intent newIntent = new Intent(ScannerPreparationActivity.this, ListeEmplacementCreationActivity.class);
                            Bundle extras = ScannerPreparationActivity.super.getBundle();
                            extras.putInt("zoneid", zoneid);
                            newIntent.putExtras(extras);
                            ScannerPreparationActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                        }
                        break;

                    case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                        int emplacementid = data.getExtras().getInt("emplacementId");
                        if(emplacementid != -1)
                        {
                            Depot_Emplacement emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                            ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacementSelectionner.getAdressage().trim());
                            newReceptionPUIContext.emplacement_courant = emplacementSelectionner;
                            ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                        }
                        break;
                }
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
        final InputMethodManager imm = (InputMethodManager)ScannerPreparationActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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

/*        if(message.contentEquals("Produit déjà préparé en intégralité"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                v.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }*/

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

        InputMethodManager imme = (InputMethodManager) ScannerPreparationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imme.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Drawable vert = ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_scanner_preparation);
                        if(EditTextScanee.getBackground().getConstantState().equals(vert.getConstantState())){
                            EditTextScanee.setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_scanner_preparation_noir));
                        }else{
                            EditTextScanee.setBackground(ScannerPreparationActivity.this.getResources().getDrawable(R.drawable.background_scanner_preparation));
                        }
                        blink();
                    }
                });
            }
        }).start();
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
                counter = 3;
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

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(ScannerPreparationActivity.this, "dev01@alcyons.fr", true, db);
            try {
                // Envoi du mail avec pdf

                sender.sendMailVerification(subject, body);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }

    public void afficherAlerteErreurEmplacement(Context context, LayoutInflater inflater, String emplacement, final List<Integer> listeEmplacementLot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation, null);

        ImageView buttonOk = (ImageView) layout.findViewById(R.id.buttonOk);
        TextView messageFin = (TextView) layout.findViewById(R.id.messageFin);
        TextView titre = (TextView) layout.findViewById(R.id.titre);

        titre.setText("Erreur");
        messageFin.setText("L'emplacement scanné ne correspond pas au lot scanné (Emplacement du lot : "+emplacement+")");
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if(preparationSimpleContext != null)
                    preparationSimpleContext.emplacement_courant = null;
                if(preparationMultipleContext != null)
                    preparationMultipleContext.emplacement_courant = null;

                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("");
                ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");
                if(listeEmplacementLot.size() == 0)
                {
                    ((TextView) findViewById(R.id.EmplacementLotProduit)).performClick();
                }
                else
                {
                    Intent newIntent = new Intent(ScannerPreparationActivity.this, ListeEmplacementCreationActivity.class);
                    Bundle extras = ScannerPreparationActivity.super.getBundle();
                    extras.putIntegerArrayList("listeEmplacement", (ArrayList<Integer>) listeEmplacementLot);
                    newIntent.putExtras(extras);
                    ScannerPreparationActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                }
            }
        });
    }
}
