/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BarcodeSearch.camera.CameraSource;
import fr.alcyons.phiwms_mobile.BarcodeSearch.camera.CameraSourcePreview;
import fr.alcyons.phiwms_mobile.BarcodeSearch.camera.GraphicOverlay;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.AuthentificationContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ControleDesRetourScanContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.DocumentContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.EmplacementContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.EmplacementReceptionScanneeContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.FranceMVOContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PleinVideContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PleinVideLocalisationContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PreparationScanneeContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PreparationScanneeScanProduitContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PreparationContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ProduitContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ProduitReceptionScanneeContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ReceptionPADContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ServiceContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ZoneEtEmplacementContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.BarcodeCaptureNegativeActivity;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PleinVideBarcodeAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public class BarcodeCaptureActivity extends ServiceActivity {

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
    public static final String BarcodeObject = "Barcode";
    protected static final String TAG = "Barcode-reader";
    // intent request code to handle updating play services if needed.
    protected static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    protected static final int RC_HANDLE_CAMERA_PERM = 2;

    // SON
    //ToneGenerator toneGen1;

    // CONTEXTE
    ProduitContexte produitContexte;
    PreparationScanneeContexte preparationScanneeContexte;
    ServiceContexte serviceContexte;
    DocumentContext documentContext;
    FranceMVOContexte franceMVOContexte;
    EmplacementContexte emplacementContexte;
    AuthentificationContext authentificationContext;
    PreparationContexte preparationContexte;
    PleinVideContexte pleinVideContexte;
    PleinVideLocalisationContexte pleinVideLocalisationContexte;
    EmplacementReceptionScanneeContexte emplacementReceptionScanneeContexte;
    ProduitReceptionScanneeContexte produitReceptionScanneeContexte;
    PreparationScanneeScanProduitContext preparationScanneeScanProduitContext;
    ReceptionPADContexte receptionPADContexte;
    ControleDesRetourScanContext controleDesRetourScanContext;
    ZoneEtEmplacementContext zoneEtEmplacementContext;
    String scannerContexteEmplacement = String.valueOf(R.string.scannerContexteEmplacement);
    String scannerContexteProduit = String.valueOf(R.string.scannerContexteProduit);
    String scannerContextePleinVide = String.valueOf(R.string.scannerContextePleinVide);
    String scannerContexteFranceMVO = String.valueOf(R.string.scannerContexteFranceMVO);
    String scannerContextePleinVideLocalisation = String.valueOf(R.string.scannerContextePleinVideLocalisation);
    String scannerContextePreparation = String.valueOf(R.string.scannerContextePreparation);
    String scannerContexteDocument = String.valueOf(R.string.scannerContexteDocument);
    String scannerContexteAuthentification = String.valueOf(R.string.scannerContexteAuthentification);
    String scannerContextService = String.valueOf(R.string.scannerContexteService);
    String scannerContextePreparationADH = String.valueOf(R.string.scannerContextePreparationADH);
    String scannerContexteEmplacementReceptionScannee = String.valueOf(R.string.scannerContexteEmplacementReceptionScannee);
    String scannerContexteProduitReceptionScannee = String.valueOf(R.string.scannerContexteProduitReceptionScannee);
    String scannerContextePreparationADHScanProdui = String.valueOf(R.string.scannerContextePreparationADHScanProduit);
    String scannerContexteReceptionPAD = String.valueOf(R.string.scannerContexteReceptionPAD);
    String scannerContexteControleDesRetours = String.valueOf(R.string.scannerContexteControleDesRetours);
    String scannerContexteZoneEtEmplacement = String.valueOf(R.string.scannerContexteZoneEtEmplacement);
    int scannerContexteInt = 0;

    // INTENT
    Intent intent;
    String scannerContexte = "";
    // Permet de savoir s'il faut lire les code en chaine ou non
    boolean modeRafale = false;
    boolean modePhoto = false;
    boolean modeCumule = true;
    boolean useFlash;
    boolean isBoutonSuppressionExistant;
    public List<String> stringList;
    boolean doitEtreIdentique;
    String designation = "";

    // GRAPHIQUE
    Bitmap actualPicture;
    FloatingActionButton boutonSuppression;
    FloatingActionButton clavierMode;
    FloatingActionButton changeMode;
    //FloatingActionMenu floatingActionMenu;
    TextView compteurScan;
    TextView message;
    TextView messageFranceMVO;
    TextView compteurReliquat;
    TextView rayonnageSelect;
    LinearLayout layoutlogoscan;
    LinearLayout linearRayonnage;
    ImageView effacerEmplacement;
    TextView DesignationProduitScannee;
    TextView CodeListe;
    LinearLayout layoutProduitPreparationADH;
    LinearLayout listeReferencePleinVide;
    EditText quantiteProduitScannee;
    String bannerTexte;

    //Pour France MVO
    String GTIN_courant;
    int userId;
    int qteReliquat;
    int reliquat_uid;

    //Pour une reception scanée
    int conditionnementProduit;
    int nbProduitAScanner;
    LinearLayout LinearTextProduitReceptionScanne;
    LinearLayout layout_emplacement_scannee;
    LinearLayout LinearZoneEmplacementProduitReceptionScanne;
    TextView designationProduitReceptionScannee;
    TextView referenceProduitReceptionScannee;
    TextView qteProduitReceptionScannee;
    TextView numeroLotProduitReceptionScannee;
    TextView peremptionProduitReceptionScannee;
    TextView textPresentationRefProduitScannee;
    TextView nomEmplacementProduitReceptionScannee;
    TextView nomZoneProduitReceptionScannee;
    ImageView boutonValiderProduitReceptionScannee;
    ImageView imageViewArmature2;
    ImageView imageViewArmature1;
    ImageView boutonModifierEmplacementReceptionScanne;
    androidx.appcompat.app.AlertDialog alertDialogFranceMVO;

    //pour le contexte de preparation
    List<String>Liste_GTIN;
    List<PH_Preparation_Ligne_Preparation_Adapte> listedejascanne;
    int preparationId;

//    boolean press_zebra = false;

    boolean pleinVideAjouter;
    String code;

    //gestion des images affichés
    public LinearLayout layout_image_datamatrix;
    public LinearLayout layout_image_document;
    public LinearLayout layout_image_codebarre;

    PleinVideBarcodeAdapter pleinVideBarcodeAdapter;
    ListView listViewDemandePleinVide;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now

        if(scannerContexteInt == R.string.scannerContextePleinVide)
        {
            stringList = pleinVideContexte.stringList;
        }
        else if(scannerContexteInt == R.string.scannerContexteFranceMVO)
        {
            stringList = franceMVOContexte.stringList;
        }
        else if(scannerContexteInt == R.string.scannerContextePreparation)
        {
            stringList = preparationContexte.stringList;
        }
        else if(scannerContexteInt == R.string.scannerContextePreparationADH)
        {
            stringList = preparationScanneeContexte.stringList;
        }
        else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
        {
            stringList = receptionPADContexte.stringList;
        }
        else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
        {
            stringList = receptionPADContexte.stringList;
        }
        else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
        {
            stringList = produitReceptionScanneeContexte.stringList;
        }
        outState.putStringArrayList("stringList", (ArrayList<String>) stringList);
        outState.putString("messageText", message.getText().toString());
    }

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_barcode_capture);

        //initialisation des images pour le scan attendu
        layout_image_datamatrix = (LinearLayout) findViewById(R.id.layout_image_datamatrix);
        layout_image_document = (LinearLayout) findViewById(R.id.layout_image_document);
        layout_image_codebarre = (LinearLayout) findViewById(R.id.layout_image_codebarre);

        // SON
        //toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        // INTENT
        intent = BarcodeCaptureActivity.this.getIntent();
        scannerContexte = intent.getExtras().getString("contexte");
        modeRafale = intent.getExtras().getBoolean("modeRafale");
        modePhoto = intent.getExtras().getBoolean("modePhoto");
        modeCumule = intent.getBooleanExtra("cumule", true);
        useFlash = intent.getBooleanExtra(UseFlash, false);
        isBoutonSuppressionExistant = intent.getExtras().getBoolean("isBoutonSuppressionExistant");
        doitEtreIdentique = intent.getBooleanExtra("doitEtreIdentique", false);
        designation = intent.getStringExtra("Designation");
        // CONTEXTE
        final boolean service_serialisation = intent.getBooleanExtra("serialisation", false);
        //pour France MVO
        GTIN_courant =intent.getStringExtra("GTIN_courant");
        userId =intent.getIntExtra("UserId", 0);
        qteReliquat =intent.getExtras().getInt("qteReliquat");
        reliquat_uid =intent.getExtras().getInt("reliquat_uid");
        conditionnementProduit = intent.getExtras().getInt("ConditionnementProduit");
        nbProduitAScanner = intent.getExtras().getInt("nbProdAReceptionner");
        int preparationLigneId = intent.getIntExtra("preparationLigneId", 0);
        //Pour la réception scannée
        final int actionId = intent.getIntExtra("ActionId", 0);
        //pour la réception PAD
        final List<Integer> liste_id_reliquat = intent.getExtras().getIntegerArrayList("liste_id_reliquat");
        final List<Integer> liste_id_retour_ligne = intent.getExtras().getIntegerArrayList("liste_id_retour_ligne");
        //pour le contexte de preparation
        Liste_GTIN = intent.getExtras().getStringArrayList("ListGTIN");
        preparationId =intent.getExtras().getInt("preparationId");
        int nb_produit_scanne = intent.getExtras().getInt("nb_produit_scanne");
        boolean ADH = intent.getBooleanExtra("ADH", false);
        //listedejascanne = (List<PH_Preparation_Ligne_Preparation_Adapte>) intent.getExtras().getSerializable("listedejascanne");

        if (icicle != null) {
            stringList = icicle.getStringArrayList("stringList");
        }
        else{
            stringList = intent.getExtras().getStringArrayList("stringList");
            if (stringList == null) {
                stringList = new ArrayList<>();
            }
        }

        // GRAPHIQUE
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);
        boutonSuppression = (FloatingActionButton) findViewById(R.id.boutonFermeture);
        clavierMode = (FloatingActionButton) findViewById(R.id.clavierMode);
        clavierMode.setVisibility(View.GONE);
        //floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floatingMenu);
        compteurScan = (TextView) findViewById(R.id.compteurScan);
        message = (TextView) findViewById(R.id.message);
        messageFranceMVO = (TextView) findViewById(R.id.messageFranceMVO);
        compteurReliquat = (TextView) findViewById(R.id.compteurReliquat);
        rayonnageSelect = (TextView) findViewById(R.id.rayonnageSelect);
        layoutlogoscan = (LinearLayout) findViewById(R.id.layoutlogoscan);
        linearRayonnage = (LinearLayout) findViewById(R.id.linearRayonnage);
        changeMode = (FloatingActionButton) findViewById(R.id.changeMode);
        effacerEmplacement = (ImageView) findViewById(R.id.effacerEmplacement);
        DesignationProduitScannee = (TextView) findViewById(R.id.DesignationProduitScannee);
        layoutProduitPreparationADH = (LinearLayout) findViewById(R.id.layoutProduitPreparationADH);
        quantiteProduitScannee = (EditText) findViewById(R.id.quantiteProduitScannee);
        message.setVisibility(View.GONE);
        LinearTextProduitReceptionScanne = (LinearLayout) findViewById(R.id.LinearTextProduitReceptionScanne);
        layout_emplacement_scannee = (LinearLayout) findViewById(R.id.layout_emplacement_scannee);
        //pour la réception scannée
        designationProduitReceptionScannee = (TextView) findViewById(R.id.designationProduitReceptionScannee);
        CodeListe = (TextView) findViewById(R.id.CodeListe);
        referenceProduitReceptionScannee = (TextView) findViewById(R.id.referenceProduitReceptionScannee);
        qteProduitReceptionScannee = (TextView) findViewById(R.id.qteProduitReceptionScannee);
        numeroLotProduitReceptionScannee = (TextView) findViewById(R.id.numeroLotProduitReceptionScannee);
        peremptionProduitReceptionScannee = (TextView) findViewById(R.id.peremptionProduitReceptionScannee);
        textPresentationRefProduitScannee = (TextView) findViewById(R.id.textPresentationRefProduitScannee);
        nomEmplacementProduitReceptionScannee = (TextView) findViewById(R.id.nomEmplacementProduitReceptionScannee);
        nomZoneProduitReceptionScannee = (TextView) findViewById(R.id.nomZoneProduitReceptionScannee);
        boutonValiderProduitReceptionScannee = (ImageView) findViewById(R.id.boutonValiderProduitReceptionScannee);
        LinearZoneEmplacementProduitReceptionScanne = (LinearLayout) findViewById(R.id.LinearZoneEmplacementProduitReceptionScanne);
        imageViewArmature2 = (ImageView) findViewById(R.id.imageViewArmature2);
        imageViewArmature1 = (ImageView) findViewById(R.id.imageViewArmature1);
        boutonModifierEmplacementReceptionScanne = (ImageView) findViewById(R.id.boutonModifierEmplacementReceptionScanne);
        listeReferencePleinVide = (LinearLayout) findViewById(R.id.listeReferencePleinVide);
        listViewDemandePleinVide = (ListView) findViewById(R.id.listViewDemandePleinVide);

        changeMode.setLabelText("Datamatrix inversé");

        if(icicle != null){
            String messageText = icicle.getString("messageText");
            if(!messageText.isEmpty()){
                message.setText(messageText);
                message.setVisibility(View.VISIBLE);
            }
        }


        if(Liste_GTIN != null && preparationId != 0 && !ADH)
        {
            preparationContexte = new PreparationContexte(this, db, Liste_GTIN, utilisateurConnecte.getId(), message, messageFranceMVO, boutonSuppression, preparationId, listedejascanne, nb_produit_scanne);
        }

        if(Liste_GTIN != null && preparationId != 0 && ADH)
        {
            preparationScanneeContexte = new PreparationScanneeContexte(this, db, Liste_GTIN, utilisateurConnecte.getId(), message, messageFranceMVO, boutonSuppression, preparationId, listedejascanne, nb_produit_scanne);
        }

        produitContexte = new ProduitContexte(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, service_serialisation);
        produitReceptionScanneeContexte = new ProduitReceptionScanneeContexte(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, service_serialisation, conditionnementProduit, utilisateurConnecte, actionId);
        receptionPADContexte = new ReceptionPADContexte(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, service_serialisation, conditionnementProduit, utilisateurConnecte, actionId, liste_id_reliquat);
        controleDesRetourScanContext = new ControleDesRetourScanContext(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, service_serialisation, conditionnementProduit, utilisateurConnecte, actionId, liste_id_retour_ligne);
        documentContext = new DocumentContext(this, db);
        serviceContexte = new ServiceContexte(this, db);
        zoneEtEmplacementContext = new ZoneEtEmplacementContext(this, db, utilisateurConnecte);
        authentificationContext = new AuthentificationContext(this, db);
        emplacementContexte = new EmplacementContexte(this, db, ADH, utilisateurConnecte);
        pleinVideContexte = new PleinVideContexte(this, message);
        pleinVideLocalisationContexte = new PleinVideLocalisationContexte(this, db, message);
        emplacementReceptionScanneeContexte = new EmplacementReceptionScanneeContexte(this, db, ADH);

        if(!service_serialisation && userId != 0 && !ADH)
        {
            franceMVOContexte = new FranceMVOContexte(this, db, GTIN_courant, userId, message, messageFranceMVO, compteurReliquat, qteReliquat, boutonSuppression, reliquat_uid);
        }

        if(!service_serialisation && userId != 0 && ADH)
        {
            preparationScanneeScanProduitContext = new PreparationScanneeScanProduitContext(this, db, GTIN_courant, userId, message, messageFranceMVO, compteurReliquat, qteReliquat, boutonSuppression, preparationLigneId);
        }

        // Initialisation du CONTEXTE
        if (scannerContexte != null) {
            scannerContexteInt = Integer.parseInt(scannerContexte);

            if(scannerContexteInt == R.string.scannerContexteEmplacement)
            {
                bannerTexte = emplacementContexte.bannerTexte;
                compteurReliquat.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
            }
            else if(scannerContexteInt == R.string.scannerContexteEmplacementReceptionScannee)
            {
                bannerTexte = emplacementReceptionScanneeContexte.bannerTexte;
                compteurReliquat.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                layoutlogoscan.setVisibility(View.GONE);
                linearRayonnage.setVisibility(View.VISIBLE);
                rayonnageSelect.setText(intent.getExtras().getString("ProduitSelect"));
            }
            else if(scannerContexteInt == R.string.scannerContextePleinVide)
            {
                bannerTexte = intent.getExtras().getString("dotationIntitule");
                pleinVideContexte.stringList = stringList;
                pleinVideContexte.detailDotPleinVide_AdressageList = intent.getExtras().getStringArrayList("detailDotPleinVide_AdressageList");
                pleinVideContexte.mapPleinVide.putAll((HashMap<String, String>) intent.getSerializableExtra("designationArrayList"));
                compteurReliquat.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                clavierMode.setVisibility(View.VISIBLE);
                compteurScan.setVisibility(View.VISIBLE);
                listeReferencePleinVide.setVisibility(View.VISIBLE);
                pleinVideBarcodeAdapter = new PleinVideBarcodeAdapter(BarcodeCaptureActivity.this, pleinVideContexte.referenceList);
                listViewDemandePleinVide.setAdapter(pleinVideBarcodeAdapter);
            }
            else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
            {
                bannerTexte = pleinVideLocalisationContexte.bannerTexte;
            }
            else if(scannerContexteInt == R.string.scannerContexteFranceMVO)
            {
                if(reliquat_uid == 0)
                {
                    compteurReliquat.setVisibility(View.GONE);
                }
                scannerContexte = String.valueOf(R.string.scannerContexteFranceMVO);
                bannerTexte = franceMVOContexte.bannerTexte;
                franceMVOContexte.stringList = stringList;
            }
            else if(scannerContexteInt == R.string.scannerContextePreparationADHScanProduit)
            {
                compteurReliquat.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                scannerContexte = String.valueOf(R.string.scannerContextePreparationADHScanProduit);
                bannerTexte = preparationScanneeScanProduitContext.bannerTexte;
                preparationScanneeScanProduitContext.liste_code_scanne = intent.getExtras().getStringArrayList("listeCodeScanne");
                if(preparationScanneeScanProduitContext.liste_code_scanne == null)
                {
                    preparationScanneeScanProduitContext.liste_code_scanne = new ArrayList<>();
                }
                compteurReliquat.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                layout_emplacement_scannee.setVisibility(View.GONE);
                layoutlogoscan.setVisibility(View.GONE);
                effacerEmplacement.setVisibility(View.GONE);
                linearRayonnage.setVisibility(View.VISIBLE);
                LinearZoneEmplacementProduitReceptionScanne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bannerTexte = "Scanner un emplacement";
                        boutonValiderProduitReceptionScannee.setVisibility(View.GONE);
                        nomZoneProduitReceptionScannee.setText("");
                        nomEmplacementProduitReceptionScannee.setText("");
                        preparationScanneeScanProduitContext.emplacementProduitCourant = "";
                        preparationScanneeScanProduitContext.zoneProduitCourant = "";
                        LinearZoneEmplacementProduitReceptionScanne.setBackgroundResource(android.R.color.transparent);
                        ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                        boutonModifierEmplacementReceptionScanne.setVisibility(View.GONE);
                        preparationScanneeScanProduitContext.scanEmplacement = true;
                    }
                });
                bannerTexte = "Scanner un emplacement";
            }
            else if(scannerContexteInt == R.string.scannerContextePreparation)
            {
                compteurReliquat.setVisibility(View.GONE);
                bannerTexte = preparationContexte.bannerTexte;
                scannerContexte = String.valueOf(R.string.scannerContextePreparation);
                preparationContexte.stringList = stringList;
            }
            else if(scannerContexteInt == R.string.scannerContextePreparationADH)
            {
                CodeListe.setVisibility(View.VISIBLE);
                CodeListe.setText(intent.getExtras().getString("Preparation_Code"));
                preparationScanneeContexte.liste_resultat = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("Liste_Objet_Scanne");
                if (preparationScanneeContexte.liste_resultat == null)
                {
                    preparationScanneeContexte.liste_resultat = new ArrayList<>();
                }

                preparationScanneeContexte.liste_code_scanne = intent.getExtras().getStringArrayList("ListString");
                if(preparationScanneeContexte.liste_code_scanne == null)
                {
                    preparationScanneeContexte.liste_code_scanne = new ArrayList<>();
                }
                compteurReliquat.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                layout_emplacement_scannee.setVisibility(View.GONE);
                layoutlogoscan.setVisibility(View.GONE);
                effacerEmplacement.setVisibility(View.GONE);
                bannerTexte = preparationScanneeContexte.bannerTexte;
                scannerContexte = String.valueOf(R.string.scannerContextePreparationADH);
                preparationScanneeContexte.stringList = stringList;
                linearRayonnage.setVisibility(View.VISIBLE);
                LinearZoneEmplacementProduitReceptionScanne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bannerTexte = "Scanner un emplacement";
                        boutonValiderProduitReceptionScannee.setVisibility(View.GONE);
                        nomZoneProduitReceptionScannee.setText("");
                        nomEmplacementProduitReceptionScannee.setText("");
                        preparationScanneeContexte.emplacementProduitCourant = "";
                        preparationScanneeContexte.zoneProduitCourant = "";
                        LinearZoneEmplacementProduitReceptionScanne.setBackgroundResource(android.R.color.transparent);
                        ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                        boutonModifierEmplacementReceptionScanne.setVisibility(View.GONE);
                        preparationScanneeContexte.scanEmplacement = true;
                    }
                });
                rayonnageSelect.setVisibility(View.GONE);
                layout_emplacement_scannee.setVisibility(View.GONE);
                bannerTexte = produitReceptionScanneeContexte.bannerTexte;
            }
            else if(scannerContexteInt == R.string.scannerContexteDocument)
            {
                ((ImageView)findViewById(R.id.changeMode)).setVisibility(View.GONE);
                layout_image_codebarre.setVisibility(View.GONE);
                layout_image_datamatrix.setVisibility(View.GONE);
                layout_image_document.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                bannerTexte = documentContext.bannerTexte;
            }
            else if(scannerContexteInt == R.string.scannerContexteAuthentification)
            {
                ((ImageView)findViewById(R.id.changeMode)).setVisibility(View.GONE);
                //((ImageView)findViewById(R.id.scannerMode)).setVisibility(View.GONE);
                layout_image_codebarre.setVisibility(View.GONE);
                layout_image_document.setVisibility(View.GONE);
                layout_image_datamatrix.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                bannerTexte = authentificationContext.bannerTexte;
            }
            else if(scannerContexteInt == R.string.scannerContexteService)
            {
                ((ImageView)findViewById(R.id.changeMode)).setVisibility(View.GONE);
                layout_image_codebarre.setVisibility(View.GONE);
                layout_image_document.setVisibility(View.GONE);
                layout_image_datamatrix.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                bannerTexte = serviceContexte.bannerTexte;
            }
            else if(scannerContexteInt == R.string.scannerContexteZoneEtEmplacement)
            {
                ((ImageView)findViewById(R.id.changeMode)).setVisibility(View.GONE);
                layout_image_codebarre.setVisibility(View.GONE);
                layout_image_document.setVisibility(View.GONE);
                layout_image_datamatrix.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                bannerTexte = zoneEtEmplacementContext.bannerTexte;
            }
            else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
            {
                scannerContexte = String.valueOf(R.string.scannerContexteReceptionPAD);
                compteurReliquat.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                layout_emplacement_scannee.setVisibility(View.GONE);
                layoutlogoscan.setVisibility(View.GONE);
                effacerEmplacement.setVisibility(View.GONE);
                linearRayonnage.setVisibility(View.VISIBLE);
                receptionPADContexte.stringList = intent.getExtras().getStringArrayList("Liste_GTIN_Scannee");
                receptionPADContexte.list_result = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
                if(receptionPADContexte.list_result == null)
                {
                    receptionPADContexte.list_result = new ArrayList<>();
                }
                rayonnageSelect.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                bannerTexte = "Scannez une référence";
            }
            else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
            {
                scannerContexte = String.valueOf(R.string.scannerContexteControleDesRetours);
                compteurReliquat.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                layout_emplacement_scannee.setVisibility(View.GONE);
                layoutlogoscan.setVisibility(View.GONE);
                effacerEmplacement.setVisibility(View.GONE);
                linearRayonnage.setVisibility(View.VISIBLE);
                controleDesRetourScanContext.stringList = intent.getExtras().getStringArrayList("Liste_GTIN_Scannee");
                if(controleDesRetourScanContext.stringList == null)
                {
                    controleDesRetourScanContext.stringList = new ArrayList<>();
                }
                controleDesRetourScanContext.list_result = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
                if(controleDesRetourScanContext.list_result == null)
                {
                    controleDesRetourScanContext.list_result = new ArrayList<>();
                }
                rayonnageSelect.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                bannerTexte = "Scannez une référence";
            }
            else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
            {
                scannerContexte = String.valueOf(R.string.scannerContexteProduitReceptionScannee);
                compteurReliquat.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                layout_emplacement_scannee.setVisibility(View.GONE);
                layoutlogoscan.setVisibility(View.GONE);
                effacerEmplacement.setVisibility(View.GONE);
                linearRayonnage.setVisibility(View.VISIBLE);
                produitReceptionScanneeContexte.stringList = intent.getExtras().getStringArrayList("Liste_GTIN_Scannee");
                produitReceptionScanneeContexte.list_result = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
                if(produitReceptionScanneeContexte.list_result == null)
                {
                    produitReceptionScanneeContexte.list_result = new ArrayList<>();
                }

                LinearZoneEmplacementProduitReceptionScanne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bannerTexte = "Scanner un emplacement";
                        boutonValiderProduitReceptionScannee.setVisibility(View.GONE);
                        nomZoneProduitReceptionScannee.setText("");
                        nomEmplacementProduitReceptionScannee.setText("");
                        produitReceptionScanneeContexte.emplacementProduitCourant = "";
                        produitReceptionScanneeContexte.zoneProduitCourant = "";
                        LinearZoneEmplacementProduitReceptionScanne.setBackgroundResource(android.R.color.transparent);
                        ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                        boutonModifierEmplacementReceptionScanne.setVisibility(View.GONE);
                        produitReceptionScanneeContexte.scanEmplacement = true;
                    }
                });
                rayonnageSelect.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                bannerTexte = produitReceptionScanneeContexte.bannerTexte;
            }
            else
            {
                scannerContexte = String.valueOf(R.string.scannerContexteProduit);
                layout_image_codebarre.setVisibility(View.GONE);
                layout_image_document.setVisibility(View.GONE);
                produitContexte.stringList = stringList;
                compteurReliquat.setVisibility(View.GONE);
                layout_image_datamatrix.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                bannerTexte = produitContexte.bannerTexte;
            }

        } else {
            scannerContexte = String.valueOf(R.string.scannerContexteProduit);
            produitContexte.stringList = stringList;
            bannerTexte = produitContexte.bannerTexte;
        }

        // Mise à jour GRAPHIQUE
        ((TextView) findViewById(R.id.banner)).setText(bannerTexte);

        // MODE RAFALE ?
        compteurScan.setVisibility(View.GONE);
        if(modeRafale  && !scannerContexte.contentEquals(String.valueOf(R.string.scannerContexteProduitReceptionScannee)) && !scannerContexte.contentEquals(String.valueOf(R.string.scannerContexteControleDesRetours)) && !scannerContexte.contentEquals(String.valueOf(R.string.scannerContextePreparationADHScanProduit)) && !scannerContexte.contentEquals(String.valueOf(R.string.scannerContexteReceptionPAD))){
            compteurScan.setVisibility(View.VISIBLE);
            if(stringList.size() > 0)
            {
                compteurScan.setText(String.valueOf(stringList.size()) + " produit(s) scanné(s)");
            }
        }

        if(scannerContexte.contentEquals(String.valueOf(R.string.scannerContextePleinVide)))
        {
/*            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10,10,10,25);
            compteurScan.setLayoutParams(params);*/
        }

        // ACTION GRAPHIQUE
        if (isBoutonSuppressionExistant) {
            boutonSuppression.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resultIntent = new Intent();
                    Bundle extras = new Bundle();

                    if(scannerContexteInt == R.string.scannerContexteEmplacement)
                    {
                        extras.putString("code", code);
                    }
                    else if(scannerContexteInt == R.string.scannerContexteEmplacementReceptionScannee)
                    {

                    }
                    else if(scannerContexteInt == R.string.scannerContextePleinVide)
                    {
                        stringList = pleinVideContexte.stringList;
                        extras.putStringArrayList("listeString", (ArrayList) stringList);
                    }
                    else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
                    {

                    }
                    else if(scannerContexteInt == R.string.scannerContexteDocument)
                    {
                        extras.putString("Code", "");
                    }
                    else if(scannerContexteInt == R.string.scannerContexteService)
                    {
                        extras.putString("code",serviceContexte.code);
                    }
                    else if(scannerContexteInt == R.string.scannerContexteZoneEtEmplacement)
                    {
                        extras.putString("code",zoneEtEmplacementContext.code);
                    }
                    else if(scannerContexteInt == R.string.scannerContexteAuthentification)
                    {
                        extras.putString("username", "");
                        extras.putString("password", "");
                    }
                    else if(scannerContexteInt == R.string.scannerContexteFranceMVO)
                    {
                        extras.putSerializable("listeString", (Serializable) franceMVOContexte.tableau_renvoyer);
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADHScanProduit)
                    {
                        extras.putSerializable("listeString", (Serializable) preparationScanneeScanProduitContext.liste_resultat);
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparation)
                    {
                        extras.putSerializable("listeString", (Serializable) preparationContexte.liste_preparation_liste_adapte);
                        extras.putInt("nbProduitScanne", preparationContexte.nb_produit_scanne);
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                    {
                        stringList = produitReceptionScanneeContexte.stringList;
                        extras.putSerializable("listeObjet", (Serializable) preparationScanneeContexte.liste_resultat);
                        extras.putStringArrayList("listeString", (ArrayList<String>) preparationScanneeContexte.liste_code_scanne);
                    }
                    else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
                    {
                        stringList = produitReceptionScanneeContexte.stringList;
                        extras.putSerializable("listeString", (Serializable) produitReceptionScanneeContexte.list_result);
                    }
                    else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                    {
                        stringList = receptionPADContexte.stringList;
                        extras.putSerializable("listeString", (Serializable) receptionPADContexte.list_result);
                    }
                    else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                    {
                        stringList = controleDesRetourScanContext.stringList;
                        extras.putSerializable("listeRetourScan", (Serializable) controleDesRetourScanContext.list_result);
                    }
                    else
                    {
                        stringList = produitContexte.stringList;
                        extras.putStringArrayList("listeString", (ArrayList) stringList);
                    }

                    resultIntent.putExtras(extras);
                    setResult(CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH, resultIntent);
                    finish();
                }
            });
            boutonSuppression.setVisibility(View.VISIBLE);
        }

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
                Intent newIntent = new Intent(BarcodeCaptureActivity.this, BarcodeCaptureNegativeActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
                extras.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
                extras.putString("contexte", intent.getExtras().getString("contexte"));
                extras.putBoolean("modePhoto", modePhoto);
                extras.putBoolean("cumule", modeCumule);
                extras.putBoolean("modeRafale", modeRafale);
                extras.putBoolean("serialisation", service_serialisation);
                extras.putBoolean("doitEtreIdentique", intent.getBooleanExtra("doitEtreIdentique", false));
                extras.putString("Designation", intent.getStringExtra("Designation"));
                extras.putBoolean("isBoutonSuppressionExistant", isBoutonSuppressionExistant);


                if(scannerContexteInt == R.string.scannerContextePreparationADH)
                {
                    extras.putString("Preparation_Code", intent.getExtras().getString("Preparation_Code"));

                    if(!preparationScanneeContexte.scanEmplacement)
                    {
                        extras.putString("EmplacementScanne", preparationScanneeContexte.emplacementProduitCourant);
                        extras.putString("ZoneScanne", preparationScanneeContexte.zoneProduitCourant);
                    }
                }
                int codeEchangesActivites = 0;
                if (modeRafale) {
                    if(scannerContexteInt == R.string.scannerContextePleinVide)
                    {
                        stringList = pleinVideContexte.stringList;
                        extras.putString("dotationIntitule", intent.getExtras().getString("dotationIntitule"));
                        extras.putStringArrayList("detailDotPleinVide_AdressageList",intent.getExtras().getStringArrayList("detailDotPleinVide_AdressageList"));
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_ADRESSAGE;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteFranceMVO)
                    {
                        stringList = franceMVOContexte.stringList;
                        codeEchangesActivites = CodesEchangesActivites.CONTEXTE_FRANCE_MVO;
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADHScanProduit)
                    {
                        stringList = franceMVOContexte.stringList;
                        codeEchangesActivites = CodesEchangesActivites.CONTEXTE_FRANCE_MVO;
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparation)
                    {
                        stringList = preparationContexte.stringList;
                        codeEchangesActivites = CodesEchangesActivites.CONTEXTE_PREPARATION;
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                    {
                        stringList = preparationScanneeContexte.stringList;
                        codeEchangesActivites = CodesEchangesActivites.CONTEXTE_PREPARATION;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
                    {
                        stringList = produitReceptionScanneeContexte.stringList;
                        int uidEmplacement = produitReceptionScanneeContexte.uidEmplacementCourant;
                        extras.putInt("uidEmplacement", uidEmplacement);
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_GS1;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                    {
                        stringList = receptionPADContexte.stringList;
                        int uidEmplacementScanne = receptionPADContexte.uidEmplacementCourant;
                        extras.putInt("uidEmplacement", uidEmplacementScanne);
                        extras.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) receptionPADContexte.stringList);
                        extras.putIntegerArrayList("liste_id_reliquat", (ArrayList<Integer>) liste_id_reliquat);
                        extras.putSerializable("ListeObjetScannee", (Serializable) receptionPADContexte.list_result);
                        extras.putBoolean("modeCumule", true);
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_GS1;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                    {
                        stringList = controleDesRetourScanContext.stringList;
                        extras.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) controleDesRetourScanContext.stringList);
                        extras.putIntegerArrayList("liste_id_reliquat", (ArrayList<Integer>) liste_id_reliquat);
                        extras.putSerializable("ListeObjetScannee", (Serializable) controleDesRetourScanContext.list_result);
                        extras.putBoolean("modeCumule", true);
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_GS1;
                    }
                    else
                    {
                        stringList = produitContexte.stringList;
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_GS1;
                    }

                    extras.putStringArrayList("stringList", (ArrayList<String>) stringList);
                } else {
                    if(scannerContexteInt == R.string.scannerContexteEmplacement)
                    {
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteEmplacementReceptionScannee)
                    {
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT;
                    }
                    else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
                    {
                        codeEchangesActivites = CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteDocument)
                    {
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_DOCUMENT;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteAuthentification)
                    {
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_AUTHENTIFICATION;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteService)
                    {
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_SERVICE;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteZoneEtEmplacement)
                    {
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT;
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                    {
                        extras.putStringArrayList("ListGTIN", (ArrayList<String>) Liste_GTIN);
                        extras.putInt("preparationId", preparationId);
                        extras.putBoolean("ADH", true);
                        codeEchangesActivites = CodesEchangesActivites.CONTEXTE_PREPARATION;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
                    {
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_GS1;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                    {
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_GS1;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                    {
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_GS1;
                    }
                    else
                    {
                        codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_GS1;
                    }
                }
                newIntent.putExtras(extras);
                BarcodeCaptureActivity.this.startActivityForResult(newIntent, codeEchangesActivites);
                //onBackPressed();
            }
        });

        if (!modePhoto) {

            ((FloatingActionButton) findViewById(R.id.takePictureButton)).setVisibility(View.GONE);

            ((FloatingActionButton) findViewById(R.id.scannerMode)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newIntent = new Intent(BarcodeCaptureActivity.this, ScannerSearchOnlyActivity.class);
                    if(scannerContexte.contentEquals(scannerContexteDocument))
                    {
                        newIntent = new Intent(BarcodeCaptureActivity.this, ScannerDocumentActivity.class);
                    }

                    Bundle extras = new Bundle();
                    extras.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
                    extras.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
                    extras.putBoolean("modePhoto", modePhoto);
                    extras.putBoolean("cumule", modeCumule);
                    extras.putBoolean("modeRafale", modeRafale);
                    extras.putBoolean("serialisation", service_serialisation);
                    extras.putBoolean("doitEtreIdentique", intent.getBooleanExtra("doitEtreIdentique", false));
                    extras.putString("Designation", intent.getStringExtra("Designation"));
                    extras.putInt("scannerContexteInt",scannerContexteInt);
                    extras.putInt("ActionId",actionId);

                    if(scannerContexte.contentEquals(scannerContexteDocument))
                    {
                        extras.putBoolean("activerTextSuppression", true);
                        extras.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
                        extras.putBoolean("isBoutonSuppressionExistant", true);
                    }

                    if(scannerContexte.contentEquals(scannerContexteAuthentification))
                    {
                        extras.putBoolean("activerTextSuppression", true);
                        extras.putString("TextBannerManuel", "Scannez le datamatrix d'authentification");
                        extras.putBoolean("isBoutonSuppressionExistant", true);
                    }

                    int codeEchangesActivites = 0;
                    if (modeRafale) {
                        if(scannerContexteInt == R.string.scannerContextePleinVide)
                        {
                            stringList = pleinVideContexte.stringList;
                            extras.putString("dotationIntitule", intent.getExtras().getString("dotationIntitule"));
                            extras.putStringArrayList("detailDotPleinVide_AdressageList",intent.getExtras().getStringArrayList("detailDotPleinVide_AdressageList"));
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_ADRESSAGE;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
                        {
                            extras.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) produitReceptionScanneeContexte.stringList);
                            extras.putSerializable("ListeObjetScannee", (Serializable) produitReceptionScanneeContexte.list_result);
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_GS1;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                        {
                            extras.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) receptionPADContexte.stringList);
                            extras.putIntegerArrayList("liste_id_reliquat", (ArrayList<Integer>) liste_id_reliquat);
                            extras.putSerializable("ListeObjetScannee", (Serializable) receptionPADContexte.list_result);
                            extras.putBoolean("modeCumule", true);
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_GS1;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                        {
                            extras.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) controleDesRetourScanContext.stringList);
                            extras.putIntegerArrayList("liste_id_reliquat", (ArrayList<Integer>) liste_id_reliquat);
                            extras.putSerializable("ListeObjetScannee", (Serializable) controleDesRetourScanContext.list_result);
                            extras.putBoolean("modeCumule", true);
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_GS1;
                        }
                        else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                        {
                            extras.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) preparationScanneeContexte.stringList);
                            extras.putSerializable("ListeObjetScannee", (Serializable) preparationScanneeContexte.liste_resultat);
                            extras.putInt("PreparationID", preparationId);
                            extras.putString("Preparation_Code", intent.getExtras().getString("Preparation_Code"));
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_GS1;
                        }
                        else
                        {
                            stringList = produitContexte.stringList;
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_GS1;
                        }
                        extras.putStringArrayList("stringList", (ArrayList<String>) stringList);
                    } else {

                        if(scannerContexteInt == R.string.scannerContexteEmplacement)
                        {
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteEmplacementReceptionScannee)
                        {
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT;
                        }
                        else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
                        {
                            codeEchangesActivites = CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteDocument)
                        {
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_DOCUMENT;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteZoneEtEmplacement)
                        {
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteAuthentification)
                        {
                            extras.putString("ServiceCourant", "Authentification");
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_AUTHENTIFICATION;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteFranceMVO)
                        {
                            codeEchangesActivites = CodesEchangesActivites.CONTEXTE_FRANCE_MVO;
                        }
                        else if(scannerContexteInt == R.string.scannerContextePreparationADHScanProduit)
                        {
                            codeEchangesActivites = CodesEchangesActivites.CONTEXTE_FRANCE_MVO;
                        }
                        else if(scannerContexteInt == R.string.scannerContextePreparation)
                        {
                            codeEchangesActivites = CodesEchangesActivites.CONTEXTE_PREPARATION;
                        }
                        else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                        {
                            codeEchangesActivites = CodesEchangesActivites.CONTEXTE_PREPARATION;
                            extras.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) preparationScanneeContexte.stringList);
                            extras.putSerializable("ListeObjetScannee", (Serializable) preparationScanneeContexte.liste_resultat);
                            extras.putInt("PreparationID", preparationId);
                            extras.putString("Preparation_Code", intent.getExtras().getString("Preparation_Code"));
                        }
                        else if(scannerContexteInt == R.string.scannerContexteService)
                        {
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_SERVICE;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
                        {
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_GS1;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                        {
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_GS1;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                        {
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_GS1;
                        }
                        else
                        {
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_GS1;
                        }
                    }
                    newIntent.putExtras(extras);
                    BarcodeCaptureActivity.this.startActivityForResult(newIntent, codeEchangesActivites);
                    //onBackPressed();
                }
            });

            ((FloatingActionButton) findViewById(R.id.clavierMode)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent clavierMode_Intent = new Intent(BarcodeCaptureActivity.this, KeyboardActivity.class);
                    Bundle clavierMode_Bundle = new Bundle();
                    clavierMode_Bundle.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
                    clavierMode_Bundle.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
                    clavierMode_Bundle.putString("contexte", intent.getExtras().getString("contexte"));
                    clavierMode_Bundle.putString("dotationIntitule", intent.getExtras().getString("dotationIntitule"));
                    clavierMode_Bundle.putStringArrayList("detailDotPleinVide_AdressageList",intent.getExtras().getStringArrayList("detailDotPleinVide_AdressageList"));
                    clavierMode_Bundle.putStringArrayList("stringList", (ArrayList<String>) pleinVideContexte.stringList);
                    clavierMode_Intent.putExtras(clavierMode_Bundle);
                    BarcodeCaptureActivity.this.startActivityForResult(clavierMode_Intent, CodesEchangesActivites.RETOUR_LISTE_CODE_ADRESSAGE);
                    onBackPressed();
                }
            });

        } else {
            ((FloatingActionButton) findViewById(R.id.scannerMode)).setVisibility(View.GONE);
        }

        ((EditText) findViewById(R.id.contenuCode)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.toString().endsWith("\n")) {
                    final Intent resultIntent = new Intent();
                    code = "";
                    if(scannerContexteInt == R.string.scannerContexteEmplacement)
                    {
                        code = s.toString().substring(0, s.length() - 1);
                    }
                    else if(scannerContexteInt == R.string.scannerContexteEmplacementReceptionScannee)
                    {
                        code = s.toString().substring(0, s.length()-1);
                    }
                    else if(scannerContexteInt == R.string.scannerContextePleinVide)
                    {
                        pleinVideContexte.onTextWatcher(s);

                        if(pleinVideContexte.dotationId.contentEquals(""))
                        {
                            if(pleinVideContexte.stringList.size() > 0)
                            {
                                compteurScan.setText(String.valueOf(pleinVideContexte.stringList.size()) + " produit(s) scanné(s)");
                            }

                            pleinVideBarcodeAdapter.referenceDesignation = new ArrayList<>();
                            pleinVideBarcodeAdapter.referenceDesignation.addAll(pleinVideContexte.referenceList);
                            pleinVideBarcodeAdapter.notifyDataSetChanged();
                        }
                        else
                            code = pleinVideContexte.dotationId;
                    }
                    else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
                    {
                        pleinVideLocalisationContexte.onTextWatcher(s);
                        code = pleinVideLocalisationContexte.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteDocument)
                    {
                        documentContext.onTextWatcher(s);
                        code = documentContext.code;
                    }
                    else if(scannerContexteInt ==  R.string.scannerContexteService)
                    {
                        serviceContexte.onTextWatcher(s);
                        code = serviceContexte.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteZoneEtEmplacement)
                    {
                        zoneEtEmplacementContext.onTextWatcher(s);
                        code = zoneEtEmplacementContext.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteAuthentification)
                    {
                        authentificationContext.onTextWatcher(s);
                        String username = authentificationContext.username;
                        String password = authentificationContext.password;
                        if(username != null && password != null)
                        {
                            code = "ok";
                        }
                        else
                            code = "";

                        resultIntent.putExtra("username", username);
                        resultIntent.putExtra("password", password);
                    }
                    else if(scannerContexteInt == R.string.scannerContexteFranceMVO)
                    {
                        franceMVOContexte.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(franceMVOContexte.stringList.size())+" produit(s) scanné(s)");
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADHScanProduit)
                    {
                        if(s.toString().startsWith("PHITAGPLACE"))
                        {
                            preparationScanneeScanProduitContext.scanEmplacement = true;
                        }
                        preparationScanneeScanProduitContext.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(preparationScanneeScanProduitContext.stringList.size())+" produit(s) scanné(s)");
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparation)
                    {
                        preparationContexte.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(preparationContexte.nb_produit_scanne)+" produit(s) scanné(s)");
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                    {
                        preparationScanneeContexte.onTextWatcher(s);
                        //compteurScan.setText(String.valueOf(preparationScanneeContexte.nb_produit_scanne)+" produit(s) scanné(s)");
                        //code = preparationScanneeContexte.code;
                        layoutProduitPreparationADH.setVisibility(View.VISIBLE);
                        DesignationProduitScannee.setText(preparationScanneeContexte.designationProduitScanne);
                        quantiteProduitScannee.setVisibility(View.VISIBLE);
                        quantiteProduitScannee.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    String text = quantiteProduitScannee.getText().toString();
                                    if (!text.contentEquals("")) {
                                        int quantite_saisie = Integer.parseInt(text);
                                        if (quantite_saisie > preparationScanneeContexte.nbMaxQuantite)
                                            quantite_saisie = preparationScanneeContexte.nbMaxQuantite;
                                        code = preparationScanneeContexte.uid_preparationLigneCourant + ":" + quantite_saisie + ":" + preparationScanneeContexte.code;
                                        resultIntent.putExtra("code", code);
                                        setResult(BarcodeCaptureActivity.RESULT_OK, resultIntent);
                                        finish();
                                    }
                                }
                                return false;
                            }
                        });
                    }
                    else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
                    {
                        if(s.toString().substring(0, s.length()-1).startsWith("PHITAGPLACE+"))
                        {
                            produitReceptionScanneeContexte.scanEmplacement = true;
                        }
                        produitReceptionScanneeContexte.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(produitReceptionScanneeContexte.stringList.size()) + " produit(s) scanné(s)");
                        code = produitReceptionScanneeContexte.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                    {
                        receptionPADContexte.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(receptionPADContexte.stringList.size()) + " produit(s) scanné(s)");
                        code = receptionPADContexte.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                    {
                        controleDesRetourScanContext.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(controleDesRetourScanContext.stringList.size()) + " produit(s) scanné(s)");
                        code = controleDesRetourScanContext.code;
                    }
                    else
                    {
                        produitContexte.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(produitContexte.stringList.size()) + " produit(s) scanné(s)");
                        code = produitContexte.code;
                    }

                    if(!code.isEmpty()){
                        resultIntent.putExtra("code", code);
                        setResult(BarcodeCaptureActivity.RESULT_OK, resultIntent);
                        finish();
                    }
                    else{
                        ((EditText) findViewById(R.id.contenuCode)).setText("");
                    }
                }
            }
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

        //on efface l'emplacement lors d'une préparation ADH
        effacerEmplacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("code", "");
                setResult(BarcodeCaptureActivity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if(scannerContexteInt == R.string.scannerContexteEmplacement)
            {
                emplacementContexte.onActivityResult(requestCode, data);
                code = emplacementContexte.code;
            }
            else if(scannerContexteInt == R.string.scannerContexteEmplacementReceptionScannee)
            {
                emplacementReceptionScanneeContexte.onActivityResult(requestCode, data);
            }
            else if(scannerContexteInt == R.string.scannerContextePleinVide)
            {
                pleinVideContexte.onActivityResult(requestCode, data);
                pleinVideAjouter = false;
            }
            else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
            {
                pleinVideLocalisationContexte.onActivityResult(requestCode, data);
                if(!pleinVideLocalisationContexte.code.isEmpty()){
                    ((EditText) findViewById(R.id.contenuCode)).setText(pleinVideLocalisationContexte.code + "\n");
                }
            }
            else if(scannerContexteInt == R.string.scannerContexteDocument)
            {
                documentContext.onActivityResult(requestCode, data);
                if(documentContext.code != null && !documentContext.code.isEmpty()){
                    ((EditText) findViewById(R.id.contenuCode)).setText(documentContext.code + "\n");
                }
            }
            else if(scannerContexteInt == R.string.scannerContexteAuthentification)
            {
                authentificationContext.onActivityResult(requestCode, data);
                if(authentificationContext.username != null && authentificationContext.password != null)
                {
                    if(!authentificationContext.username.isEmpty() && !authentificationContext.password.isEmpty())
                    {
                        ((EditText) findViewById(R.id.contenuCode)).setText("PHITAGID:"+authentificationContext.username+"PHITAGMDP:"+authentificationContext.password+"\n");
                    }
                }
            }
            else if(scannerContexteInt == R.string.scannerContexteFranceMVO)
            {
                franceMVOContexte.onActivityResult(requestCode, data);
                if(!franceMVOContexte.code.isEmpty()){
                    ((EditText) findViewById(R.id.contenuCode)).setText(franceMVOContexte.code + "\n");
                }
            }
            else if(scannerContexteInt == R.string.scannerContextePreparationADHScanProduit)
            {
                preparationScanneeScanProduitContext.onActivityResult(requestCode, data);
                if(!preparationScanneeScanProduitContext.code.isEmpty()){
                    ((EditText) findViewById(R.id.contenuCode)).setText(preparationScanneeScanProduitContext.code + "\n");
                }
            }
            else if(scannerContexteInt == R.string.scannerContextePreparation)
            {
                preparationContexte.onActivityResult(requestCode, data);
                if(!preparationContexte.code.isEmpty())
                {
                    ((EditText) findViewById(R.id.contenuCode)).setText(preparationContexte.code+"\n");
                }
            }
            else if(scannerContexteInt == R.string.scannerContextePreparationADH)
            {
                preparationScanneeContexte.onActivityResult(requestCode, data);
                if(!preparationScanneeContexte.code.isEmpty())
                {
                    ((EditText) findViewById(R.id.contenuCode)).setText(preparationScanneeContexte.code+"\n");
                }
                boutonSuppression.callOnClick();
            }
            else if(scannerContexteInt == R.string.scannerContexteService)
            {
                serviceContexte.onActivityResult(requestCode, data);
                if(!serviceContexte.code.isEmpty())
                    boutonSuppression.callOnClick();
            }
            else if(scannerContexteInt == R.string.scannerContexteZoneEtEmplacement)
            {
                zoneEtEmplacementContext.onActivityResult(requestCode, data);
                if(!zoneEtEmplacementContext.code.isEmpty())
                    boutonSuppression.callOnClick();
            }
            else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
            {
                produitReceptionScanneeContexte.onActivityResult(requestCode, data);
                boutonSuppression.callOnClick();
            }
            else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
            {
                receptionPADContexte.onActivityResult(requestCode, data);
                boutonSuppression.callOnClick();
            }
            else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
            {
                controleDesRetourScanContext.onActivityResult(requestCode, data);
                boutonSuppression.callOnClick();
            }
            else
            {
                produitContexte.onActivityResult(requestCode, data);
                if(!produitContexte.code.isEmpty()){
                    ((EditText) findViewById(R.id.contenuCode)).setText(produitContexte.code + "\n");
                }
            }
            boolean close = data.getBooleanExtra("close", false);
            if (close || scannerContexte.contentEquals(scannerContexteEmplacement))
                boutonSuppression.callOnClick();
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
//        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !press_zebra)
//        {
//            onBackPressed();
//        }
//        press_zebra = false;
        startCameraSource();
        int nb_produit;
        if(scannerContexteInt == R.string.scannerContextePleinVide)
        {
            stringList = pleinVideContexte.stringList;
            nb_produit = stringList.size();
        }
        else if(scannerContexteInt == R.string.scannerContexteFranceMVO)
        {
            stringList = franceMVOContexte.stringList;
            nb_produit = stringList.size();
        }
        else if(scannerContexteInt == R.string.scannerContextePreparationADHScanProduit)
        {
            stringList = preparationScanneeScanProduitContext.stringList;
            nb_produit = stringList.size();
        }
        else if(scannerContexteInt == R.string.scannerContextePreparation)
        {
            stringList = preparationContexte.stringList;
            nb_produit = preparationContexte.nb_produit_scanne;
        }
        else if(scannerContexteInt == R.string.scannerContextePreparationADH)
        {
            stringList = preparationScanneeContexte.stringList;
            nb_produit = preparationScanneeContexte.nb_produit_scanne;
        }
        else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
        {
            stringList = produitReceptionScanneeContexte.stringList;
            nb_produit = stringList.size();
        }
        else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
        {
            stringList = receptionPADContexte.stringList;
            nb_produit = stringList.size();
        }
        else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
        {
            stringList = controleDesRetourScanContext.stringList;
            nb_produit = stringList.size();
        }
        else
        {
            stringList = produitContexte.stringList;
            nb_produit = stringList.size();
        }

        if(nb_produit > 0)
            compteurScan.setText(String.valueOf(nb_produit) + " produit(s) scanné(s)");
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
        int[] location = new int[2];
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
            if (modePhoto) {
                AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                mCameraSource.takePicture(new CameraSource.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        int test = 1;
                        test++;
                    }
                }, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data) {
                        produitContexte.actualPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
                        mCameraSource.pause(true);
                    }
                });
            }

            if (scannerContexte.contentEquals(scannerContexteProduit)) {
                if (produitContexte.onTap(best.rawValue)) {
                    ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                }
            }
            if (scannerContexte.contentEquals(scannerContexteFranceMVO)) {
                if (franceMVOContexte.onTap(best.rawValue)) {
                    ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                }
            }
            if (scannerContexte.contentEquals(scannerContextePreparationADHScanProdui)) {
                if(best.rawValue.startsWith("PHITAGPLACE"))
                {
                    preparationScanneeScanProduitContext.scanEmplacement = true;
                }
                if (preparationScanneeScanProduitContext.onTap(best.rawValue)) {
                    ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                    if(preparationScanneeScanProduitContext.scanEmplacement)
                    {
                        nomEmplacementProduitReceptionScannee.setVisibility(View.VISIBLE);
                        nomZoneProduitReceptionScannee.setVisibility(View.VISIBLE);
                        nomEmplacementProduitReceptionScannee.setText(preparationScanneeScanProduitContext.emplacementProduitCourant);
                        nomZoneProduitReceptionScannee.setText(preparationScanneeScanProduitContext.zoneProduitCourant);
                        preparationScanneeScanProduitContext.scanEmplacement = false;
                        boutonModifierEmplacementReceptionScanne.setVisibility(View.VISIBLE);
                        if(!referenceProduitReceptionScannee.getText().toString().contentEquals(""))
                        {
                            boutonValiderProduitReceptionScannee.setVisibility(View.VISIBLE);
                        }
                        bannerTexte = "Scanner une référence";
                    }
                    else
                    {
                        referenceProduitReceptionScannee.setText(preparationScanneeScanProduitContext.referenceProduitScanne);
                        designationProduitReceptionScannee.setText(preparationScanneeScanProduitContext.designationProduitScanne);
                        peremptionProduitReceptionScannee.setText(preparationScanneeScanProduitContext.peremptionProduitScanne);
                        numeroLotProduitReceptionScannee.setText(preparationScanneeScanProduitContext.numeroLotProduitScanne);
                        if(preparationScanneeScanProduitContext.referenceProduitScanne != null && !preparationScanneeScanProduitContext.referenceProduitScanne.contentEquals(""))
                        {
                            boutonValiderProduitReceptionScannee.setVisibility(View.VISIBLE);
                            imageViewArmature1.setVisibility(View.VISIBLE);
                            qteProduitReceptionScannee.setText(String.valueOf(preparationScanneeScanProduitContext.quantiteAAfficher));
                            if(!preparationScanneeScanProduitContext.serialisation_preparation)
                            {
                                qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Ouvre une boite de dialogue avec un NumberPicker
                                        Context context = BarcodeCaptureActivity.this;
                                        String title = preparationScanneeScanProduitContext.designationProduitScanne;
                                        String message = "Changer la quantité: ";
                                        int maxValue = preparationScanneeScanProduitContext.quantite_max_number_picker;
                                        int value = preparationScanneeScanProduitContext.quantiteAAfficher;

                                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                int qteAprès = aNumberPicker.getValue();
                                                qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                                preparationScanneeScanProduitContext.quantiteAAfficher = qteAprès;
                                                //adapter.notifyDataSetChanged();
                                                InputMethodManager imm = (InputMethodManager) BarcodeCaptureActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                                dialog.dismiss();
                                            }
                                        };

                                        Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

                                    }
                                });
                            }
                            imageViewArmature2.setVisibility(View.VISIBLE);
                        }
                        boutonValiderProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean ajout = preparationScanneeScanProduitContext.ajouterProduit();
                                if(ajout)
                                {
                                    boutonValiderProduitReceptionScannee.setVisibility(View.GONE);
                                    imageViewArmature1.setVisibility(View.GONE);
                                    imageViewArmature2.setVisibility(View.GONE);
                                    peremptionProduitReceptionScannee.setText("");
                                    designationProduitReceptionScannee.setText("");
                                    numeroLotProduitReceptionScannee.setText("");
                                    referenceProduitReceptionScannee.setText("");
                                    qteProduitReceptionScannee.setText("");
                                    preparationScanneeScanProduitContext.referenceProduitScanne = "";
                                    preparationScanneeScanProduitContext.designationProduitScanne = "";
                                    preparationScanneeScanProduitContext.peremptionProduitScanne = "";
                                    preparationScanneeScanProduitContext.numeroLotProduitScanne = "";
                                    preparationScanneeScanProduitContext.quantiteAAfficher = 0;
                                }
                                else
                                {
                                    Toast toast = Toast.makeText(BarcodeCaptureActivity.this, "Échec", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        });
                    }
                    ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                }
            }
            if (scannerContexte.contentEquals(scannerContexteEmplacement)) {
                if (emplacementContexte.onTap(best.rawValue)) {
                    ((EditText) findViewById(R.id.contenuCode)).setText(emplacementContexte.code + "\n");
                }
            }
            if (scannerContexte.contentEquals(scannerContexteEmplacementReceptionScannee)) {
                if (emplacementReceptionScanneeContexte.onTap(best.rawValue)) {
                    ((EditText) findViewById(R.id.contenuCode)).setText(emplacementReceptionScanneeContexte.code + "\n");
                }
            }
            if (scannerContexte.contentEquals(scannerContextePreparation)) {
                if (preparationContexte.onTap(best.rawValue)) {
                    ((EditText) findViewById(R.id.contenuCode)).setText(preparationContexte.code + "\n");
                }
            }
            if (scannerContexte.contentEquals(scannerContextePreparationADH)) {
                if(best.rawValue.startsWith("PHITAGPLACE"))
                {
                    preparationScanneeContexte.scanEmplacement = true;
                }
                if (preparationScanneeContexte.onTap(best.rawValue)) {
                    ((EditText) findViewById(R.id.contenuCode)).setText(preparationScanneeContexte.code + "\n");
                    if(preparationScanneeContexte.scanEmplacement)
                    {
                        nomEmplacementProduitReceptionScannee.setVisibility(View.VISIBLE);
                        nomZoneProduitReceptionScannee.setVisibility(View.VISIBLE);
                        nomEmplacementProduitReceptionScannee.setText(preparationScanneeContexte.emplacementProduitCourant);
                        nomZoneProduitReceptionScannee.setText(preparationScanneeContexte.zoneProduitCourant);
                        preparationScanneeContexte.scanEmplacement = false;
                        boutonModifierEmplacementReceptionScanne.setVisibility(View.VISIBLE);
                        if(!referenceProduitReceptionScannee.getText().toString().contentEquals(""))
                        {
                            boutonValiderProduitReceptionScannee.setVisibility(View.VISIBLE);
                        }
                        bannerTexte = "Scanner une référence";
                    }
                    else
                    {
                        referenceProduitReceptionScannee.setText(preparationScanneeContexte.referenceProduitScanne);
                        designationProduitReceptionScannee.setText(preparationScanneeContexte.designationProduitScanne);
                        peremptionProduitReceptionScannee.setText(preparationScanneeContexte.peremptionProduitScanne);
                        numeroLotProduitReceptionScannee.setText(preparationScanneeContexte.numeroLotProduitScanne);
                        if(preparationScanneeContexte.referenceProduitScanne != null && !preparationScanneeContexte.referenceProduitScanne.contentEquals(""))
                        {
                            boutonValiderProduitReceptionScannee.setVisibility(View.VISIBLE);
                            imageViewArmature1.setVisibility(View.VISIBLE);
                            qteProduitReceptionScannee.setText(String.valueOf(preparationScanneeContexte.quantiteAAfficher));
                            if(!preparationScanneeContexte.serialisation_preparation)
                            {
                                qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Ouvre une boite de dialogue avec un NumberPicker
                                        Context context = BarcodeCaptureActivity.this;
                                        String title = preparationScanneeContexte.designationProduitScanne;
                                        String message = "Changer la quantité: ";
                                        int maxValue = preparationScanneeContexte.quantite_max_number_picker;
                                        int value = preparationScanneeContexte.quantiteAAfficher;

                                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                int qteAprès = aNumberPicker.getValue();
                                                qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                                preparationScanneeContexte.quantiteAAfficher = qteAprès;
                                                //adapter.notifyDataSetChanged();
                                                InputMethodManager imm = (InputMethodManager) BarcodeCaptureActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                                dialog.dismiss();
                                            }
                                        };

                                        Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

                                    }
                                });
                            }
                            imageViewArmature2.setVisibility(View.VISIBLE);
                        }
                        boutonValiderProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean ajout = preparationScanneeContexte.ajouterProduit();
                                if(ajout)
                                {
                                    boutonValiderProduitReceptionScannee.setVisibility(View.GONE);
                                    imageViewArmature1.setVisibility(View.GONE);
                                    imageViewArmature2.setVisibility(View.GONE);
                                    peremptionProduitReceptionScannee.setText("");
                                    designationProduitReceptionScannee.setText("");
                                    numeroLotProduitReceptionScannee.setText("");
                                    referenceProduitReceptionScannee.setText("");
                                    qteProduitReceptionScannee.setText("");
                                    preparationScanneeContexte.referenceProduitScanne = "";
                                    preparationScanneeContexte.designationProduitScanne = "";
                                    preparationScanneeContexte.peremptionProduitScanne = "";
                                    preparationScanneeContexte.numeroLotProduitScanne = "";
                                    preparationScanneeContexte.quantiteAAfficher = 0;
                                }
                                else
                                {
                                    Toast toast = Toast.makeText(BarcodeCaptureActivity.this, "Échec", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        });
                    }
                    ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                }
            }
            if (scannerContexte.contentEquals(scannerContextePleinVide)) {
                if(best.rawValue.contentEquals("PHITAGACTION_Ajouter") && !pleinVideAjouter){
                    pleinVideAjouter = true;
                    ((FloatingActionButton) findViewById(R.id.clavierMode)).callOnClick();
                }
                else {
                    ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                    if(best.rawValue.contentEquals("PHITAGACTION_Valider")){
                        boutonSuppression.callOnClick();
                    }
                }
            }
            if(scannerContexte.contentEquals(scannerContextePleinVideLocalisation)){
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
            }
            if(scannerContexte.contentEquals(scannerContexteDocument)){
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
            }
            if(scannerContexte.contentEquals(scannerContexteAuthentification)){
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
            }
            if(scannerContexte.contentEquals(scannerContextService)){
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
            }
            if(scannerContexte.contentEquals(scannerContexteZoneEtEmplacement)){
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
            }
            if(scannerContexte.contentEquals(scannerContexteProduitReceptionScannee)){
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                bannerTexte = produitReceptionScanneeContexte.bannerTexte;

                if(produitReceptionScanneeContexte.scanEmplacement)
                {
                    nomEmplacementProduitReceptionScannee.setVisibility(View.VISIBLE);
                    nomZoneProduitReceptionScannee.setVisibility(View.VISIBLE);
                    nomEmplacementProduitReceptionScannee.setText(produitReceptionScanneeContexte.emplacementProduitCourant);
                    nomZoneProduitReceptionScannee.setText(produitReceptionScanneeContexte.zoneProduitCourant);
                    produitReceptionScanneeContexte.scanEmplacement = false;
                    LinearZoneEmplacementProduitReceptionScanne.setBackgroundColor(Color.parseColor("#54b65f"));
                    boutonModifierEmplacementReceptionScanne.setVisibility(View.VISIBLE);
                    if(!referenceProduitReceptionScannee.getText().toString().contentEquals(""))
                    {
                        boutonValiderProduitReceptionScannee.setVisibility(View.VISIBLE);
                    }
                    bannerTexte = "Scanner une référence";
                }
                else if(nomZoneProduitReceptionScannee.getText().toString().contentEquals("") && nomEmplacementProduitReceptionScannee.getText().toString().contentEquals(""))
                {
                    afficherSnackBar("Scannez un emplacement");
                    bannerTexte = "Scanner un emplacement";
                    produitReceptionScanneeContexte.scanEmplacement = true;
                }
                else
                {
                    referenceProduitReceptionScannee.setText(produitReceptionScanneeContexte.referenceProduitCourant);
                    designationProduitReceptionScannee.setText(produitReceptionScanneeContexte.designationProduitCourant);
                    peremptionProduitReceptionScannee.setText(produitReceptionScanneeContexte.peremptionProduitCourant);
                    numeroLotProduitReceptionScannee.setText(produitReceptionScanneeContexte.numeroLotProduitCourant);
                    if(produitReceptionScanneeContexte.referenceProduitCourant != null && !produitReceptionScanneeContexte.referenceProduitCourant.contentEquals(""))
                    {
                        boutonValiderProduitReceptionScannee.setVisibility(View.VISIBLE);
                        imageViewArmature1.setVisibility(View.VISIBLE);
                        qteProduitReceptionScannee.setText(String.valueOf(produitReceptionScanneeContexte.quantite_a_afficher));
                        qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Ouvre une boite de dialogue avec un NumberPicker
                                Context context = BarcodeCaptureActivity.this;
                                String title = produitReceptionScanneeContexte.designationProduitCourant;
                                String message = "Changer la quantité: ";
                                int maxValue = 10000;
                                int value = produitReceptionScanneeContexte.quantite_a_afficher;

                                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        int qteAprès = aNumberPicker.getValue()*produitReceptionScanneeContexte.cond_achat;
                                        qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                        produitReceptionScanneeContexte.quantite_a_afficher = qteAprès;
                                        //adapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                        //InputMethodManager imm = (InputMethodManager) BarcodeCaptureActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                    }
                                };

                                Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, produitReceptionScanneeContexte.cond_achat);

                            }
                        });
                        imageViewArmature2.setVisibility(View.VISIBLE);
                    }
                    boutonValiderProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean ajout = produitReceptionScanneeContexte.AjoutDuProduit();
                            if(ajout)
                            {
                                boutonValiderProduitReceptionScannee.setVisibility(View.GONE);
                                imageViewArmature1.setVisibility(View.GONE);
                                imageViewArmature2.setVisibility(View.GONE);
                                peremptionProduitReceptionScannee.setText("");
                                designationProduitReceptionScannee.setText("");
                                numeroLotProduitReceptionScannee.setText("");
                                referenceProduitReceptionScannee.setText("");
                                qteProduitReceptionScannee.setText("");
                                produitReceptionScanneeContexte.referenceProduitCourant = "";
                                produitReceptionScanneeContexte.designationProduitCourant = "";
                                produitReceptionScanneeContexte.peremptionProduitCourant = "";
                                produitReceptionScanneeContexte.numeroLotProduitCourant = "";
                                produitReceptionScanneeContexte.quantite_a_afficher = 0;
                            }
                            else
                            {
                                Toast toast = Toast.makeText(BarcodeCaptureActivity.this, "Échec", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    });
                    bannerTexte = "Scanner une référence";
                }

                ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
            }
            if(scannerContexte.contentEquals(scannerContexteReceptionPAD)){
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                bannerTexte = receptionPADContexte.bannerTexte;

                referenceProduitReceptionScannee.setText(receptionPADContexte.referenceProduitCourant);
                designationProduitReceptionScannee.setText(receptionPADContexte.designationProduitCourant);
                peremptionProduitReceptionScannee.setText(receptionPADContexte.peremptionProduitCourant);
                numeroLotProduitReceptionScannee.setText(receptionPADContexte.numeroLotProduitCourant);
                if(receptionPADContexte.referenceProduitCourant != null && !receptionPADContexte.referenceProduitCourant.contentEquals(""))
                {
                    boutonValiderProduitReceptionScannee.setVisibility(View.VISIBLE);
                    imageViewArmature1.setVisibility(View.VISIBLE);
                    qteProduitReceptionScannee.setText(String.valueOf(receptionPADContexte.quantite_a_afficher));
                    qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Ouvre une boite de dialogue avec un NumberPicker
                            Context context = BarcodeCaptureActivity.this;
                            String title = receptionPADContexte.designationProduitCourant;
                            String message = "Changer la quantité: ";
                            int maxValue = receptionPADContexte.quantiteMaxNumberPicker;
                            int value = receptionPADContexte.quantite_a_afficher;

                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    int qteAprès = aNumberPicker.getValue();
                                    qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                    receptionPADContexte.quantite_a_afficher = qteAprès;
                                    //adapter.notifyDataSetChanged();
                                    InputMethodManager imm = (InputMethodManager) BarcodeCaptureActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                    dialog.dismiss();
                                }
                            };

                            Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

                        }
                    });
                    imageViewArmature2.setVisibility(View.VISIBLE);
                }
                boutonValiderProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean ajout = receptionPADContexte.AjoutDuProduit();
                        if(ajout)
                        {
                            boutonValiderProduitReceptionScannee.setVisibility(View.GONE);
                            imageViewArmature1.setVisibility(View.GONE);
                            imageViewArmature2.setVisibility(View.GONE);
                            peremptionProduitReceptionScannee.setText("");
                            designationProduitReceptionScannee.setText("");
                            numeroLotProduitReceptionScannee.setText("");
                            referenceProduitReceptionScannee.setText("");
                            qteProduitReceptionScannee.setText("");
                            receptionPADContexte.referenceProduitCourant = "";
                            receptionPADContexte.designationProduitCourant = "";
                            receptionPADContexte.peremptionProduitCourant = "";
                            receptionPADContexte.numeroLotProduitCourant = "";
                            receptionPADContexte.quantite_a_afficher = 0;
                        }
                        else
                        {
                            Toast toast = Toast.makeText(BarcodeCaptureActivity.this, "Échec", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
                bannerTexte = "Scanner une référence";
                ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
            }
            if(scannerContexte.contentEquals(scannerContexteControleDesRetours)){
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                bannerTexte = controleDesRetourScanContext.bannerTexte;

                referenceProduitReceptionScannee.setText(controleDesRetourScanContext.referenceProduitCourant);
                designationProduitReceptionScannee.setText(controleDesRetourScanContext.designationProduitCourant);
                peremptionProduitReceptionScannee.setText(controleDesRetourScanContext.peremptionProduitCourant);
                numeroLotProduitReceptionScannee.setText(controleDesRetourScanContext.numeroLotProduitCourant);
                if(controleDesRetourScanContext.referenceProduitCourant != null && !controleDesRetourScanContext.referenceProduitCourant.contentEquals(""))
                {
                    boutonValiderProduitReceptionScannee.setVisibility(View.VISIBLE);
                    imageViewArmature1.setVisibility(View.VISIBLE);
                    qteProduitReceptionScannee.setText(String.valueOf(controleDesRetourScanContext.quantite_a_afficher));
                    qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Ouvre une boite de dialogue avec un NumberPicker
                            Context context = BarcodeCaptureActivity.this;
                            String title = controleDesRetourScanContext.designationProduitCourant;
                            String message = "Changer la quantité: ";
                            int maxValue = controleDesRetourScanContext.quantiteMaxNumberPicker;
                            int value = controleDesRetourScanContext.quantite_a_afficher;

                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    int qteAprès = aNumberPicker.getValue();
                                    qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                    controleDesRetourScanContext.quantite_a_afficher = qteAprès;
                                    //adapter.notifyDataSetChanged();
                                    InputMethodManager imm = (InputMethodManager) BarcodeCaptureActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                    dialog.dismiss();
                                }
                            };

                            Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

                        }
                    });
                    imageViewArmature2.setVisibility(View.VISIBLE);
                }
                boutonValiderProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean ajout = controleDesRetourScanContext.AjoutDuProduit();
                        if(ajout)
                        {
                            boutonValiderProduitReceptionScannee.setVisibility(View.GONE);
                            imageViewArmature1.setVisibility(View.GONE);
                            imageViewArmature2.setVisibility(View.GONE);
                            peremptionProduitReceptionScannee.setText("");
                            designationProduitReceptionScannee.setText("");
                            numeroLotProduitReceptionScannee.setText("");
                            referenceProduitReceptionScannee.setText("");
                            qteProduitReceptionScannee.setText("");
                            controleDesRetourScanContext.referenceProduitCourant = "";
                            controleDesRetourScanContext.designationProduitCourant = "";
                            controleDesRetourScanContext.peremptionProduitCourant = "";
                            controleDesRetourScanContext.numeroLotProduitCourant = "";
                            controleDesRetourScanContext.quantite_a_afficher = 0;
                            Toast toast = Toast.makeText(BarcodeCaptureActivity.this, "Produit Ajouté", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else
                        {
                            Toast toast = Toast.makeText(BarcodeCaptureActivity.this, "Échec", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
                bannerTexte = "Scanner une référence";
                ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
            }
            //}
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
/*        if (floatingActionMenu.isOpened()) {
            floatingActionMenu.close(true);
        } else*/
        if(scannerContexte.contentEquals(scannerContexteAuthentification))
        {
            BarcodeCaptureActivity.this.finish();
        }
        else if(scannerContexte.contentEquals(scannerContextePleinVideLocalisation))
        {
            Intent resultIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putString("code", "");
            resultIntent.putExtras(extras);
            setResult(CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION, resultIntent);
            finish();
        }
        else if (modeRafale) {
            // Dans le cas du mode rafale on subodore que le fait de backPresser est équivalent à cliquer sur bouton suppression
            boutonSuppression.callOnClick();
        } else {
            //super.onBackPressed();
            boutonSuppression.callOnClick();
        }
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

    public void afficherSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>" + message + "</b>", 0), Snackbar.LENGTH_LONG);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        if(message.contentEquals("Produit préparé"))
        {
            layout.setBackgroundColor(getResources().getColor(R.color.vert3, null));

        }
        else if(message.contentEquals("Produit déjà préparé en intégralité"))
        {
            layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
            //toneGen1.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_SSL,250);
            //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //    v.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE));
            //} else {
            //   //deprecated in API 26
            //   v.vibrate(500);
            //}
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
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getChildAt(0).getLayoutParams();
        params.gravity = Gravity.FILL_HORIZONTAL | Gravity.BOTTOM;
        snackBarView.getChildAt(0).setLayoutParams(params);
        if(!snackbar.isShown())
        {
            snackbar.show();
        }

    }

    public void afficherAlerteFranceMVO(String produitDesignation, String resultat, String numeroSerie, String motif)
    {
        //toneGen1.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_SSL,250);
        //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            v.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE));
        //       } else {
        //deprecated in API 26
        //         v.vibrate(500);
        //    }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(BarcodeCaptureActivity.this);
        LayoutInflater inflater = BarcodeCaptureActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.alerte_france_mvo, null);

        TextView DesignationProduitFranceMVO = (TextView) layout.findViewById(R.id.DesignationProduitFranceMVO);
        TextView NumeroSerieFranceMVO = (TextView) layout.findViewById(R.id.NumeroSerieFranceMVO);
        TextView ResultatFranceMVO = (TextView) layout.findViewById(R.id.ResultatFranceMVO);
        TextView MotifFranceMVO = (TextView) layout.findViewById(R.id.MotifFranceMVO);
        LinearLayout fermerAlerteLinearLayout = (LinearLayout) layout.findViewById(R.id.fermerAlerteLinearLayout);

        DesignationProduitFranceMVO.setText(produitDesignation);
        NumeroSerieFranceMVO.setText(numeroSerie);
        ResultatFranceMVO.setText(resultat);
        MotifFranceMVO.setText(motif);
        fermerAlerteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialogFranceMVO.isShowing())
                    alertDialogFranceMVO.dismiss();
            }
        });

        builder.setView(layout);
        alertDialogFranceMVO = builder.create();
        alertDialogFranceMVO.getWindow().setGravity(Gravity.CENTER);
        alertDialogFranceMVO.setCancelable(false);
        if (alertDialogFranceMVO.isShowing() == false) {

            alertDialogFranceMVO.show();
        }
    }
}
