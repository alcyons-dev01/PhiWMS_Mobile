package fr.alcyons.phiwms_mobile.BarcodeSearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.alcyons.phiwms_mobile.BarcodeSearch.camera.CameraSource;
import fr.alcyons.phiwms_mobile.BarcodeSearch.camera.CameraSourcePreview;
import fr.alcyons.phiwms_mobile.BarcodeSearch.camera.GraphicOverlay;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.NewReceptionPADContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.NewReceptionPUIContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.NewUniqueReceptionPUIContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.BarcodeCaptureNegativeActivity;
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
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;

import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeZoneCreationActivity;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeEmplacementCreationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class BarcodeReceptionActivity extends ServiceActivity {

    // CAMERA
    protected CameraSource mCameraSource;
    protected CameraSourcePreview mPreview;
    protected GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    // helper objects for detecting taps and pinches.
    protected ScaleGestureDetector scaleGestureDetector;
    protected GestureDetector gestureDetector;
    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    protected static final String TAG = "Barcode-reader";
    // intent request code to handle updating play services if needed.
    protected static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    protected static final int RC_HANDLE_CAMERA_PERM = 2;
    // CONTEXTE
    NewReceptionPUIContext newReceptionPUIContext;
    NewUniqueReceptionPUIContext newUniqueReceptionPUIContext;
    NewReceptionPADContext newReceptionPADContext;
    String scannerContextNewReceptionPUI = String.valueOf(R.string.scannerContextNewReceptionPUI);
    String scannerContextUniqueNewReceptionPUI = String.valueOf(R.string.scannerContextUniqueNewReceptionPUI);
    String scannerContextNewReceptionPAD = String.valueOf(R.string.scannerContextNewReceptionPAD);
    int scannerContexteInt = 0;

    // INTENT
    Intent intent;
    String scannerContexte = "";
    // Permet de savoir s'il faut lire les code en chaine ou non
    boolean modeCumule = true;
    boolean useFlash;

    // GRAPHIQUE
    Bitmap actualPicture;
    FloatingActionButton boutonSuppression;
    FloatingActionButton clavierMode;
    FloatingActionButton changeMode;
    TextView numPreparation;
    TextView depot;
    ImageView imageValidation;
    public int counter;
    CountDownTimer yourCountDownTimer;
    //Variable envoyé de l'activité de préparation
    public String designationProduitCourant;
    public String referenceProduitCourant;
    String ordreTri;
    int ph_preparation_ligne_id;
    List<String> listGtinScannee;
    List<String> liste_lot;

    //Gestion des variables d'une reception
    int receptionID;
    Commande commandeCourante;
    //pour un scan multiple
    List<PH_Reliquat_ReceptionPUI_Adapte> list_reliquat_receptionPuiAdapte;
    //pour un scan unitaire
    PH_Reliquat_ReceptionPUI_Adapte uniqueReceptionPUIAdapte;
    PH_Reliquat reliquatCourant;

    //Gestion des réception PAD
    List<ObjetReceptionScannee> listObjet_scanne;
    List<Integer> liste_id_reliquat;
    int qteDemander;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
    }

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_preparation_barcode);

        // INTENT
        intent = BarcodeReceptionActivity.this.getIntent();
        scannerContexte = intent.getExtras().getString("contexte");
        ph_preparation_ligne_id = intent.getExtras().getInt("preparationLigneId");
        liste_lot = intent.getExtras().getStringArrayList("liste_lot");
        ordreTri = intent.getExtras().getString("ordreTri");
        receptionID = intent.getExtras().getInt("ReceptionID");
        list_reliquat_receptionPuiAdapte = (List<PH_Reliquat_ReceptionPUI_Adapte>) intent.getExtras().getSerializable("ReceptionPUIAdapte");
        uniqueReceptionPUIAdapte = (PH_Reliquat_ReceptionPUI_Adapte) intent.getExtras().getSerializable("UniqueReceptionPUIAdapte");
        reliquatCourant = (PH_Reliquat) intent.getExtras().getSerializable("ReliquatCourant");
        listObjet_scanne = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
        liste_id_reliquat = (List<Integer>) intent.getExtras().getIntegerArrayList("liste_id_reliquat");


        // GRAPHIQUE
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        numPreparation = (TextView) findViewById(R.id.numPreparation);
        depot = (TextView) findViewById(R.id.depot);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);
        boutonSuppression = (FloatingActionButton) findViewById(R.id.boutonFermeture);
        clavierMode = (FloatingActionButton) findViewById(R.id.clavierMode);
        clavierMode.setVisibility(View.GONE);
        imageValidation = ((ImageView) findViewById(R.id.imageValidation));
        changeMode = (FloatingActionButton) findViewById(R.id.changeMode);

        changeMode.setLabelText("Datamatrix inversé");

        if(icicle != null){
            String messageText = icicle.getString("messageText");
        }

        //Initialisation des contexts
        if(scannerContexte != null)
        {
            scannerContexteInt = Integer.parseInt(scannerContexte);
        }

        //Affichage des informations de la préparation
        if(scannerContexteInt == R.string.scannerContextNewReceptionPUI || scannerContexteInt == R.string.scannerContextUniqueNewReceptionPUI || scannerContexteInt == R.string.scannerContextNewReceptionPAD)
        {
            commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
            numPreparation.setText("#"+commandeCourante.getNumero());
            depot.setText(commandeCourante.getFournisseur());
        }

        // Initialisation du CONTEXTE
        if (scannerContexte != null) {
            if(scannerContexteInt == R.string.scannerContextNewReceptionPUI)
            {
                //Récupération du dernier emplacement saisie
                Depot_Emplacement emplacement_precedent = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
                Produit produit_precedent = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");
                ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
                List<PH_Reliquat> liste_reliquat_commande_courante = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeCourante.getNumero());
                Collections.sort(liste_reliquat_commande_courante, new Comparator<PH_Reliquat>() {
                    @Override
                    public int compare(PH_Reliquat o1, PH_Reliquat o2) {
                        int tri = 0;
                        switch (ordreTri)
                        {
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
                newReceptionPUIContext = new NewReceptionPUIContext(this, db, utilisateurConnecte, listGtinScannee, utilisateurConnecte.getId(), commandeCourante.getID_commande(), liste_reliquat_commande_courante, list_reliquat_receptionPuiAdapte, emplacement_precedent, produit_precedent);

                ((TextView) findViewById(R.id.EmplacementLotProduit)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
                        Depot depotOrigine = DepotOpenHelper.getDepotPUI(db);
                        Intent newIntent = new Intent(BarcodeReceptionActivity.this, ListeZoneCreationActivity.class);
                        Bundle extras = BarcodeReceptionActivity.super.getBundle();
                        extras.putInt("depotID", depotOrigine.getDepot_UID());
                        newIntent.putExtras(extras);
                        BarcodeReceptionActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
                    }
                });

                PH_Reliquat premier_reliquat = liste_reliquat_commande_courante.get(0);

                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
            }
            else if(scannerContexteInt ==  R.string.scannerContextUniqueNewReceptionPUI)
            {
                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                Depot_Emplacement emplacement_precedent_unique = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
                Produit produit_precedent_unique = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");
                ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
                newUniqueReceptionPUIContext = new NewUniqueReceptionPUIContext(this, db, utilisateurConnecte, listGtinScannee, utilisateurConnecte.getId(), commandeCourante.getID_commande(), reliquatCourant, uniqueReceptionPUIAdapte, emplacement_precedent_unique, produit_precedent_unique);
                //affichage des premieres informations
                designationProduitCourant = reliquatCourant.getDesignationCourte();
                referenceProduitCourant = reliquatCourant.getProduit_Reference();
                qteDemander = reliquatCourant.getQteCommande();
                if(reliquatCourant.getQteLivraison() > 0)
                {
                    ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(reliquatCourant.getQteLivraison()));
                    ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                    ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(ContextCompat.getDrawable(BarcodeReceptionActivity.this, R.drawable.background_detail_preparation_orange));
                }
                ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));

                //gestion de l'emplacement par rapport à l'emplacement du produit par défaut
                Produit produitCourant = ProduitOpenHelper.getProduitByID(db, reliquatCourant.getProduitID());
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
                        Intent newIntent = new Intent(BarcodeReceptionActivity.this, ListeZoneCreationActivity.class);
                        Bundle extras = BarcodeReceptionActivity.super.getBundle();
                        extras.putInt("depotID", depotOrigine.getDepot_UID());
                        newIntent.putExtras(extras);
                        BarcodeReceptionActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
                    }
                });

            }
            else if(scannerContexteInt == R.string.scannerContextNewReceptionPAD)
            {
                ((TextView) findViewById(R.id.instruction)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                List<PH_Reliquat> liste_reliquat_commande_courante_pad= PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeCourante.getNumero());
                Collections.sort(liste_reliquat_commande_courante_pad, new Comparator<PH_Reliquat>() {
                    @Override
                    public int compare(PH_Reliquat o1, PH_Reliquat o2) {
                        int tri = 0;
                        switch (ordreTri)
                        {
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
                listGtinScannee = intent.getExtras().getStringArrayList("Liste_GTIN_Scannee");
                listObjet_scanne = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
                liste_id_reliquat = intent.getExtras().getIntegerArrayList("liste_id_reliquat");
                newReceptionPADContext = new NewReceptionPADContext(this,db, listGtinScannee, utilisateurConnecte, listObjet_scanne, liste_id_reliquat);

                //((LinearLayout) findViewById(R.id.LayoutEmplacementBarcode)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.INVISIBLE);
                //((ImageView) findViewById(R.id.LayoutVide)).setVisibility(View.VISIBLE);
            }
        }

        // ACTION GRAPHIQUE
        boutonSuppression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                Bundle extras = new Bundle();

                if(scannerContexteInt == R.string.scannerContextNewReceptionPUI)
                {
                    if(newReceptionPUIContext.nouveau_lot != null && newReceptionPUIContext.emplacement_courant != null)
                    {
                        boolean confirmation = Alerte.afficherAlerte(BarcodeReceptionActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
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

                    extras.putSerializable("EmplacementPrecedent", newReceptionPUIContext.emplacementPrecedent);
                    extras.putSerializable("ProduitPrecedent", newReceptionPUIContext.produitPrecedent);
                    extras.putSerializable("reliquatAdapteList", (Serializable) newReceptionPUIContext.list_reliquat_receptionPuiAdapte);
                }
                else if(scannerContexteInt == R.string.scannerContextUniqueNewReceptionPUI)
                {
                    if(newUniqueReceptionPUIContext.nouveau_lot != null && newUniqueReceptionPUIContext.emplacement_courant != null)
                    {
                        boolean confirmation = Alerte.afficherAlerte(BarcodeReceptionActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
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
                    extras.putSerializable("EmplacementPrecedent", newUniqueReceptionPUIContext.emplacementPrecedent);
                    extras.putSerializable("ProduitPrecedent", newUniqueReceptionPUIContext.produitPrecedent);
                    extras.putSerializable("reliquatAdapte", (Serializable) newUniqueReceptionPUIContext.phReliquatReceptionPUIAdapte_courant);
                }
                else if(scannerContexteInt == R.string.scannerContextNewReceptionPAD)
                {
                    if(!newReceptionPADContext.objetReceptionScanneeCourant.getGs1_scannee().contentEquals(""))
                    {
                        boolean confirmation = Alerte.afficherAlerte(BarcodeReceptionActivity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
                        if(confirmation)
                        {
                            newReceptionPADContext.AjoutDuProduit();
                        }
                    }
                    extras.putSerializable("listeString", (Serializable) newReceptionPADContext.list_result);
                }
                resultIntent.putExtras(extras);
                setResult(CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH, resultIntent);
                finish();
            }
        });
        boutonSuppression.setVisibility(View.VISIBLE);
        ((FloatingActionButton) findViewById(R.id.flash)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraSource.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
                // onBackPressed();
            }
        });

        changeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(BarcodeReceptionActivity.this, BarcodeCaptureNegativeActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
                extras.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
                extras.putString("contexte", intent.getExtras().getString("contexte"));
                extras.putBoolean("cumule", modeCumule);
                extras.putBoolean("doitEtreIdentique", intent.getBooleanExtra("doitEtreIdentique", false));
                extras.putString("Designation", intent.getStringExtra("Designation"));

                int codeEchangesActivites = 0;

                if(scannerContexteInt == R.string.scannerContextNewReceptionPUI)
                {
                    extras.putSerializable("EmplacementPrecedent", newReceptionPUIContext.emplacementPrecedent);
                    extras.putSerializable("ProduitPrecedent", newReceptionPUIContext.produitPrecedent);
                }

                newIntent.putExtras(extras);
                BarcodeReceptionActivity.this.startActivityForResult(newIntent, codeEchangesActivites);
            }
        });

        findViewById(R.id.takePictureButton).setVisibility(View.GONE);


        findViewById(R.id.scannerMode).setOnClickListener(v -> {
            Intent newIntent = new Intent(BarcodeReceptionActivity.this, ScannerPreparationActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
            extras.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
            extras.putString("contexte", scannerContexte);
            int codeEchangesActivites = CodesEchangesActivites.RETOUR_SCANNER;

            if(scannerContexteInt == R.string.scannerContextUniqueNewReceptionPUI || scannerContexteInt == R.string.scannerContextNewReceptionPUI)
            {
                extras.putInt("ReceptionID", receptionID);
                extras.putString("ordreTri", ordreTri);
                extras.putSerializable("ReceptionPUIAdapte", (Serializable) list_reliquat_receptionPuiAdapte);
                extras.putSerializable("UniqueReceptionPUIAdapte", (Serializable) uniqueReceptionPUIAdapte);
                extras.putSerializable("ReliquatCourant", (Serializable) reliquatCourant);

                if(scannerContexteInt == R.string.scannerContextNewReceptionPUI)
                {
                    extras.putSerializable("EmplacementPrecedent", newReceptionPUIContext.emplacementPrecedent);
                    extras.putSerializable("ProduitPrecedent", newReceptionPUIContext.produitPrecedent);
                }

                if(scannerContexteInt == R.string.scannerContextUniqueNewReceptionPUI)
                {
                    extras.putSerializable("EmplacementPrecedent", newUniqueReceptionPUIContext.emplacementPrecedent);
                    extras.putSerializable("ProduitPrecedent", newUniqueReceptionPUIContext.produitPrecedent);
                }
            }

            if(scannerContexteInt == R.string.scannerContextNewReceptionPAD)
            {
                extras.putInt("ReceptionID", receptionID);
                extras.putString("ordreTri", ordreTri);
                extras.putSerializable("ListeObjetScannee", (Serializable) listObjet_scanne);
                extras.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) listGtinScannee);
                extras.putIntegerArrayList("liste_id_reliquat", (ArrayList<Integer>) liste_id_reliquat);
            }

            newIntent.putExtras(extras);
            BarcodeReceptionActivity.this.startActivityForResult(newIntent, codeEchangesActivites);
        });

        findViewById(R.id.clavierMode).setOnClickListener(v -> {
            Intent clavierMode_Intent = new Intent(BarcodeReceptionActivity.this, KeyboardActivity.class);
            Bundle clavierMode_Bundle = new Bundle();
            clavierMode_Bundle.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
            clavierMode_Bundle.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
            clavierMode_Bundle.putString("contexte", intent.getExtras().getString("contexte"));
            clavierMode_Bundle.putString("dotationIntitule", intent.getExtras().getString("dotationIntitule"));
            clavierMode_Bundle.putStringArrayList("detailDotPleinVide_AdressageList",intent.getExtras().getStringArrayList("detailDotPleinVide_AdressageList"));
            clavierMode_Intent.putExtras(clavierMode_Bundle);
            BarcodeReceptionActivity.this.startActivityForResult(clavierMode_Intent, CodesEchangesActivites.RETOUR_LISTE_CODE_ADRESSAGE);
            onBackPressed();
        });

        // CAMERA
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, useFlash);
        } else {
            requestCameraPermission();
        }
        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if(scannerContexteInt == R.string.scannerContextUniqueNewReceptionPUI)
            {
                switch (requestCode)
                {
                    case CodesEchangesActivites.RESULT_ZONE:
                        int zoneid = data.getExtras().getInt("zoneid");
                        if(zoneid != -1)
                        {
                            Intent newIntent = new Intent(BarcodeReceptionActivity.this, ListeEmplacementCreationActivity.class);
                            Bundle extras = BarcodeReceptionActivity.super.getBundle();
                            extras.putInt("zoneid", zoneid);
                            newIntent.putExtras(extras);
                            BarcodeReceptionActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
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
                    case CodesEchangesActivites.RETOUR_SCANNER:
                        newUniqueReceptionPUIContext.onActivityResult(requestCode, data);
                        boutonSuppression.performClick();
                        break;
                }
            }
            else if(scannerContexteInt == R.string.scannerContextNewReceptionPUI)
            {
                switch (requestCode)
                {
                    case CodesEchangesActivites.RESULT_ZONE:
                        int zoneid = data.getExtras().getInt("zoneid");
                        if(zoneid != -1)
                        {
                            Intent newIntent = new Intent(BarcodeReceptionActivity.this, ListeEmplacementCreationActivity.class);
                            Bundle extras = BarcodeReceptionActivity.super.getBundle();
                            extras.putInt("zoneid", zoneid);
                            newIntent.putExtras(extras);
                            BarcodeReceptionActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
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
                    case CodesEchangesActivites.RETOUR_SCANNER:
                        newReceptionPUIContext.onActivityResult(requestCode, data);
                        boutonSuppression.performClick();
                        break;
                }
            }
            else if(scannerContexteInt == R.string.scannerContextNewReceptionPAD)
            {
                switch (requestCode)
                {
                    case CodesEchangesActivites.RETOUR_SCANNER:
                        newReceptionPADContext.onActivityResult(requestCode, data);
                        boutonSuppression.performClick();
                        break;
                }
            }
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    protected void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        findViewById(R.id.topLayout).setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     * <p>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    protected void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build(mGraphicOverlay);

    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    protected void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    public boolean onTap(float rawX, float rawY) {

        // Find tap point in preview frame coordinates.
        final int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        BarcodeGraphic bestGraphic = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                // Exact hit, no need to keep looking.
                best = barcode;
                bestGraphic = graphic;
                break;
            }

            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy);  // actually squared distance
            if (distance < bestDistance) {
                best = barcode;
                bestGraphic = graphic;
                bestDistance = distance;
            }
        }

        if (best != null && bestGraphic != null) {
            Rect bouncingBox = new Rect(best.getBoundingBox());
            bouncingBox.left = (int) bestGraphic.translateX(bouncingBox.left);
            bouncingBox.top = (int) bestGraphic.translateY(bouncingBox.top);
            bouncingBox.right = (int) bestGraphic.translateX(bouncingBox.right);
            bouncingBox.bottom = (int) bestGraphic.translateY(bouncingBox.bottom);

            //  if (mGraphicOverlay.getAutofocusRect().contains(bouncingBox)) {
            // On prend la photo si besoin lorsque un code a été détecté
            if (scannerContexte.contentEquals(scannerContextNewReceptionPUI)) {
                if (newReceptionPUIContext.onTap(best.rawValue)) {
                    if(newReceptionPUIContext.nouveau_lot != null && ((TextView) findViewById(R.id.EmplacementLotProduit)).getText().toString().contentEquals(""))
                    {
                        ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");
                        designationProduitCourant = newReceptionPUIContext.reliquat_courant.getDesignationCourte();
                        referenceProduitCourant = newReceptionPUIContext.reliquat_courant.getProduit_Reference();
                        qteDemander = newReceptionPUIContext.reliquat_courant.getQteCommande();
                        if(newReceptionPUIContext.reliquat_courant.getQteLivraison() > 0)
                        {
                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(newReceptionPUIContext.reliquat_courant.getQteLivraison()));
                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(BarcodeReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                        }
                        ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                        ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                        ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));
                        ((TextView) findViewById(R.id.numeroLot)).setText(newReceptionPUIContext.nouveau_lot.getNumeroLot());
                        if(newReceptionPUIContext.nouveau_lot.getNumero_serie() != null && !newReceptionPUIContext.nouveau_lot.getNumero_serie().contentEquals(""))
                        {
                            ((TextView) findViewById(R.id.numeroSerie)).setText(newReceptionPUIContext.nouveau_lot.getNumero_serie());
                        }
                        else
                        {
                            ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                        }

                        String dateDePeremption = newReceptionPUIContext.nouveau_lot.getDatePeremption();
                        String[] dateDePeremtpionTab = dateDePeremption.split("-");
                        dateDePeremption = dateDePeremtpionTab[2]+"/"+dateDePeremtpionTab[1]+"/"+dateDePeremtpionTab[0];

                        ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);

                        ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(newReceptionPUIContext.qte_lot_courant));

                        //gestion du numberPicker pour changer la quantité lors d'un scan
                        ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Context context = BarcodeReceptionActivity.this;

                                if(newReceptionPUIContext.emplacement_courant != null)
                                {
                                    yourCountDownTimer.cancel();
                                    ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                                    ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
                                }

                                String title = newReceptionPUIContext.nouveau_lot.getNumeroLot();
                                String message = "Quantité réceptionnée : ";
                                int value_max = (int)newReceptionPUIContext.reliquat_courant.getQteReliquat_X();
                                int maxValue = value_max;
                                int value = maxValue;

                                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int qteAprès = Alerte.aNumberPicker.getValue()*newReceptionPUIContext.conditionnement_achat;
                                        newReceptionPUIContext.qte_lot_courant = qteAprès;
                                        ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                        dialog.dismiss();

                                        if(newReceptionPUIContext.emplacement_courant != null)
                                        {
                                            ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                                            ((LinearLayout) findViewById(R.id.validationScan)).performClick();
                                        }
                                    }
                                };

                                Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, newReceptionPUIContext.conditionnement_achat);
                            }
                        });
                    }

                    if (newReceptionPUIContext.emplacement_courant != null && ((TextView) findViewById(R.id.EmplacementLotProduit)).getText().toString().contentEquals("")) {
                        ((TextView) findViewById(R.id.instruction)).setText("");
                        ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(newReceptionPUIContext.emplacement_courant.getAdressage());
                        blinkImage();

                        //initilisation du compteur
                        ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                        ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.instruction)).setVisibility(View.GONE);
                        counter = 4;
                        Counter();

                        ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.INVISIBLE);
                        ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newReceptionPUIContext.ValiderScan();
                                yourCountDownTimer.cancel();
                                boutonSuppression.performClick();
                            }
                        });
                    }
                }
            }
            if (scannerContexte.contentEquals(scannerContextUniqueNewReceptionPUI)) {
                if (newUniqueReceptionPUIContext.onTap(best.rawValue)) {
                    if(newUniqueReceptionPUIContext.nouveau_lot != null)
                    {
                        ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacement");

                        designationProduitCourant = newUniqueReceptionPUIContext.reliquat_courant.getDesignationCourte();
                        referenceProduitCourant = newUniqueReceptionPUIContext.reliquat_courant.getProduit_Reference();
                        qteDemander = newUniqueReceptionPUIContext.reliquat_courant.getQteCommande();
                        if(newUniqueReceptionPUIContext.reliquat_courant.getQteLivraison() > 0)
                        {
                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(newUniqueReceptionPUIContext.reliquat_courant.getQteLivraison()));
                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(BarcodeReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                        }
                        ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                        ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                        ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));
                        ((TextView) findViewById(R.id.numeroLot)).setText(newUniqueReceptionPUIContext.nouveau_lot.getNumeroLot());
                        if(newUniqueReceptionPUIContext.nouveau_lot.getNumero_serie() != null && !newUniqueReceptionPUIContext.nouveau_lot.getNumero_serie().contentEquals(""))
                        {
                            ((TextView) findViewById(R.id.numeroSerie)).setText(newUniqueReceptionPUIContext.nouveau_lot.getNumero_serie());
                        }
                        else
                        {
                            ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                        }

                        String dateDePeremption = newUniqueReceptionPUIContext.nouveau_lot.getDatePeremption();
                        String[] dateDePeremtpionTab = dateDePeremption.split("-");
                        dateDePeremption = dateDePeremtpionTab[2]+"/"+dateDePeremtpionTab[1]+"/"+dateDePeremtpionTab[0];

                        ((TextView) findViewById(R.id.datePeremptionLot)).setText(dateDePeremption);

                        ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(newUniqueReceptionPUIContext.qte_lot_courant));

                        //gestion du numberPicker pour changer la quantité lors d'un scan
                        ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Context context = BarcodeReceptionActivity.this;

                                if(newUniqueReceptionPUIContext.emplacement_courant != null)
                                {
                                    yourCountDownTimer.cancel();
                                    ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                                    ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
                                }


                                String title = newUniqueReceptionPUIContext.nouveau_lot.getNumeroLot();
                                String message = "Quantité réceptionnée : ";
                                int value_max = (int)newUniqueReceptionPUIContext.reliquat_courant.getQteReliquat_X();
                                int maxValue = value_max;
                                int value = maxValue;

                                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int qteAprès = Alerte.aNumberPicker.getValue()*newUniqueReceptionPUIContext.conditionnement_achat;
                                        newUniqueReceptionPUIContext.qte_lot_courant = qteAprès;
                                        ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(qteAprès));

                                        dialog.dismiss();

                                        if(newUniqueReceptionPUIContext.emplacement_courant != null)
                                        {
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
                        blinkImage();

                        //initilisation du compteur
                        ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.VISIBLE);
                        ((LinearLayout) findViewById(R.id.LinearLayoutBoutonBarcode)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.instruction)).setVisibility(View.GONE);
                        counter = 4;
                        Counter();

                        ((LinearLayout) findViewById(R.id.validationScan)).setVisibility(View.INVISIBLE);
                        ((LinearLayout) findViewById(R.id.validationScan)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newUniqueReceptionPUIContext.ValiderScan();
                                yourCountDownTimer.cancel();
                                boutonSuppression.performClick();
                            }
                        });
                    }
                }
            }
            if (scannerContexte.contentEquals(scannerContextNewReceptionPAD)) {
                if (newReceptionPADContext.onTap(best.rawValue)) {
                    /*if(newReceptionPADContext.nouveau_lot != null)
                    {*/

                    ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");

                    if(newReceptionPADContext.ph_reliquat_courant != null)
                    {
                        blinkImage();
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
                                boutonSuppression.performClick();
                            }
                        });
                        designationProduitCourant = newReceptionPADContext.ph_reliquat_courant.getDesignationCourte();
                        referenceProduitCourant = newReceptionPADContext.ph_reliquat_courant.getProduit_Reference();
                        qteDemander = newReceptionPADContext.ph_reliquat_courant.getQteCommande();
                        if(newReceptionPADContext.ph_reliquat_courant.getQteLivraison() > 0)
                        {
                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setText(String.valueOf(newReceptionPADContext.ph_reliquat_courant.getQteLivraison()));
                            ((TextView) findViewById(R.id.quantiteDejaPreparer)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.layoutInformations)).setBackground(BarcodeReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                        }
                        ((TextView) findViewById(R.id.designationProduit)).setText(designationProduitCourant);
                        ((TextView) findViewById(R.id.referenceProduit)).setText(referenceProduitCourant);
                        ((TextView) findViewById(R.id.quantiteProduit)).setText(String.valueOf(qteDemander));
                        ((TextView) findViewById(R.id.numeroLot)).setText(newReceptionPADContext.numeroLotProduitCourant);
                        ((TextView) findViewById(R.id.datePeremptionLot)).setText(newReceptionPADContext.peremptionProduitCourant);
                        ((TextView) findViewById(R.id.qteSaisie)).setText(String.valueOf(newReceptionPADContext.quantiteProduitCourant));
                        if(newReceptionPADContext.numeroSerieProduitCourant != null && !newReceptionPADContext.numeroSerieProduitCourant.contentEquals(""))
                        {
                            ((TextView) findViewById(R.id.numeroSerie)).setText(newReceptionPADContext.numeroSerieProduitCourant);
                        }
                        else
                        {
                            ((LinearLayout) findViewById(R.id.layoutSerie)).setVisibility(View.GONE);
                        }

                        //gestion du numberPicker pour changer la quantité lors d'un scan
                        ((LinearLayout) findViewById(R.id.layout_qte_saisie_lot_preparation)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Context context = BarcodeReceptionActivity.this;

                                String title = newReceptionPADContext.numeroLotProduitCourant;
                                String message = "Quantité réceptionnée : ";
                                int value_max = (int)newReceptionPADContext.ph_reliquat_courant.getQteReliquat_X();
                                int maxValue = value_max;
                                int value = maxValue;

                                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int qteAprès = Alerte.aNumberPicker.getValue()*newReceptionPADContext.conditionnementAchat;
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
                    }
                    else
                    {
                        if(!newReceptionPADContext.validation)
                        {
                            ((TextView) findViewById(R.id.designationProduit)).setText("");
                            ((TextView) findViewById(R.id.referenceProduit)).setText("");
                            ((TextView) findViewById(R.id.quantiteProduit)).setText("");
                            ((TextView) findViewById(R.id.numeroLot)).setText("");
                            ((TextView) findViewById(R.id.datePeremptionLot)).setText("");
                            ((TextView) findViewById(R.id.qteSaisie)).setText("");
                        }
                    }
                }
            }

            return true;
        }
        return false;
    }

    private void blinkImage() {
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

    public void afficherSnackBar(String message) {
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
        snackBarView.getChildAt(0).setLayoutParams(params);
        if(!snackbar.isShown())
        {
            snackbar.show();
        }

    }

    private void Counter()
    {
        yourCountDownTimer = new CountDownTimer(3000, 1000){
            public void onTick(long millisUntilFinished){
                ((TextView) findViewById(R.id.textViewCountDown)).setText(String.valueOf(counter));
                counter--;
            }
            public  void onFinish(){
                yourCountDownTimer.cancel();
                counter = 4;
                ((LinearLayout) findViewById(R.id.validationScan)).performClick();
            }

        }.start();

        ((LinearLayout) findViewById(R.id.layoutInformationScan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yourCountDownTimer.cancel();
                counter = 4;
                ((TextView) findViewById(R.id.textViewCountDown)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.imageValidationSeconde)).setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        boutonSuppression.performClick();
    }

    protected class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    protected class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }
}
