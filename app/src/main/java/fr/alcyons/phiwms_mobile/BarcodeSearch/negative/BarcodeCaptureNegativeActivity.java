package fr.alcyons.phiwms_mobile.BarcodeSearch.negative;

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
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.KeyboardActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.AuthentificationContext;
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
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.camera.CameraSource;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.camera.CameraSourcePreview;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.camera.GraphicOverlay;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.graphic.BarcodeGraphic;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.graphic.BarcodeTrackerFactory;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

/**
 * Created by jessica on 19/02/2018.
 */

public class BarcodeCaptureNegativeActivity extends ServiceActivity {

    // CAMERA
    protected CameraSource mCameraSource;
    protected CameraSourcePreview mPreview;
    protected GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    protected CameraSourcePreview mCameraSourcePreview;
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
    ToneGenerator toneGen1;

    // CONTEXTE
    ProduitContexte produitContexte;
    EmplacementContexte emplacementContexte;
    PleinVideContexte pleinVideContexte;
    PleinVideLocalisationContexte pleinVideLocalisationContexte;
    String scannerContexteEmplacement = String.valueOf(R.string.scannerContexteEmplacement);
    String scannerContexteProduit = String.valueOf(R.string.scannerContexteProduit);
    String scannerContextePleinVide = String.valueOf(R.string.scannerContextePleinVide);
    String scannerContextePleinVideLocalisation = String.valueOf(R.string.scannerContextePleinVideLocalisation);
    int scannerContexteInt = 0;
    PreparationScanneeContexte preparationScanneeContexte;
    ServiceContexte serviceContexte;
    DocumentContext documentContext;
    FranceMVOContexte franceMVOContexte;
    AuthentificationContext authentificationContext;
    PreparationContexte preparationContexte;
    EmplacementReceptionScanneeContexte emplacementReceptionScanneeContexte;
    ProduitReceptionScanneeContexte produitReceptionScanneeContexte;
    ReceptionPADContexte receptionPADContexte;
    PreparationScanneeScanProduitContext preparationScanneeScanProduitContext;
    String scannerContexteFranceMVO = String.valueOf(R.string.scannerContexteFranceMVO);
    String scannerContextePreparation = String.valueOf(R.string.scannerContextePreparation);
    String scannerContexteDocument = String.valueOf(R.string.scannerContexteDocument);
    String scannerContexteAuthentification = String.valueOf(R.string.scannerContexteAuthentification);
    String scannerContextService = String.valueOf(R.string.scannerContexteService);
    String scannerContextePreparationADH = String.valueOf(R.string.scannerContextePreparationADH);
    String scannerContexteEmplacementReceptionScannee = String.valueOf(R.string.scannerContexteEmplacementReceptionScannee);
    String scannerContexteProduitReceptionScannee = String.valueOf(R.string.scannerContexteProduitReceptionScannee);
    String scannerContexteReceptionPAD = String.valueOf(R.string.scannerContexteReceptionPAD);

    // INTENT
    Intent intent;
    String scannerContexte = "";
        // Permet de savoir s'il faut lire les code en chaine ou non
    boolean modeRafale = false;
    boolean modePhoto = false;
    boolean modeCumule = true;
    boolean useNegative = false;
    boolean serialisation = false;
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
    FloatingActionMenu floatingActionMenu;
    TextView compteurScan;
    TextView message;
    TextView rayonnageSelect;
    TextView nomEmplacementProduitReceptionScannee;
    TextView nomZoneProduitReceptionScannee;
    TextView textPresentationRefProduitScannee;
    TextView referenceProduitReceptionScannee;
    TextView designationProduitReceptionScannee;
    TextView peremptionProduitReceptionScannee;
    TextView numeroLotProduitReceptionScannee;
    TextView qteProduitReceptionScannee;
    TextView DesignationProduitScannee;
    TextView messageFranceMVO;
    TextView compteurReliquat;
    LinearLayout linearRayonnage;
    LinearLayout layout_emplacement_scannee;
    LinearLayout LinearTextProduitReceptionScanne;
    LinearLayout LinearZoneEmplacementProduitReceptionScanne;
    LinearLayout layoutProduitPreparationADH;
    LinearLayout layoutlogoscan;
    LinearLayout layout_image_datamatrix;
    LinearLayout layout_image_document;
    LinearLayout layout_image_codebarre;
    ImageView effacerEmplacement;
    ImageView boutonModifierEmplacementReceptionScanne;
    ImageView boutonValiderProduitReceptionScannee;
    ImageView imageViewArmature1;
    ImageView imageViewArmature2;
    EditText quantiteProduitScannee;
    androidx.appcompat.app.AlertDialog alertDialogFranceMVO;
    String bannerTexte;

    boolean pleinVideAjouter;
    String code;
    List<String>Liste_GTIN;
    List<PH_Preparation_Ligne_Preparation_Adapte> listedejascanne;
    int preparationId;
    int conditionnementProduit;
    int userId;
    String GTIN_courant;
    int qteReliquat;
    int reliquat_uid;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now

        if(scannerContexteInt == R.string.scannerContexteEmplacement)
        {

        }
        else if(scannerContexteInt == R.string.scannerContextePleinVide)
        {
            stringList = pleinVideContexte.stringList;
        }
        else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
        {

        }
        else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
        {
            stringList = receptionPADContexte.stringList;
        }
        else
        {
            stringList = produitContexte.stringList;
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
        setContentView(R.layout.activity_barcode_capture_negative);

        // SON
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        // INTENT
        intent = BarcodeCaptureNegativeActivity.this.getIntent();
        scannerContexte = intent.getExtras().getString("contexte");
        modeRafale = intent.getExtras().getBoolean("modeRafale");
        modePhoto = intent.getExtras().getBoolean("modePhoto");
        modeCumule = intent.getBooleanExtra("cumule", true);
        useFlash = intent.getBooleanExtra(UseFlash, false);
        GTIN_courant =intent.getStringExtra("GTIN_courant");
        isBoutonSuppressionExistant = intent.getExtras().getBoolean("isBoutonSuppressionExistant");
        doitEtreIdentique = intent.getBooleanExtra("doitEtreIdentique", false);
        designation = intent.getStringExtra("Designation");
        serialisation = intent.getBooleanExtra("serialisation", false);
        Liste_GTIN = intent.getExtras().getStringArrayList("ListGTIN");
        preparationId =intent.getExtras().getInt("preparationId");
        int nb_produit_scanne = intent.getExtras().getInt("nb_produit_scanne");
        boolean ADH = intent.getBooleanExtra("ADH", false);
        //listedejascanne = (List<PH_Preparation_Ligne_Preparation_Adapte>) intent.getExtras().getSerializable("listedejascanne");
        final boolean service_serialisation = intent.getBooleanExtra("serialisation", false);
        conditionnementProduit = intent.getExtras().getInt("ConditionnementProduit");
        final int actionId = intent.getIntExtra("ActionId", 0);
        final List<Integer> liste_id_reliquat = intent.getExtras().getIntegerArrayList("liste_id_reliquat");
        userId =intent.getIntExtra("UserId", 0);
        qteReliquat =intent.getExtras().getInt("qteReliquat");
        reliquat_uid =intent.getExtras().getInt("reliquat_uid");
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
        mCameraSourcePreview = (CameraSourcePreview) findViewById(R.id.preview);
        boutonSuppression = (FloatingActionButton) findViewById(R.id.boutonFermeture);
        clavierMode = (FloatingActionButton) findViewById(R.id.clavierMode);
        clavierMode.setVisibility(View.GONE);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floatingMenu);
        compteurScan = (TextView) findViewById(R.id.compteurScan);
        message = (TextView) findViewById(R.id.message);
        rayonnageSelect = (TextView) findViewById(R.id.rayonnageSelect);
        DesignationProduitScannee = (TextView) findViewById(R.id.DesignationProduitScannee);
        nomZoneProduitReceptionScannee = (TextView) findViewById(R.id.nomZoneProduitReceptionScannee);
        nomEmplacementProduitReceptionScannee = (TextView) findViewById(R.id.nomEmplacementProduitReceptionScannee);
        textPresentationRefProduitScannee = (TextView) findViewById(R.id.textPresentationRefProduitScannee);
        referenceProduitReceptionScannee = (TextView) findViewById(R.id.referenceProduitReceptionScannee);
        designationProduitReceptionScannee = (TextView) findViewById(R.id.designationProduitReceptionScannee);
        numeroLotProduitReceptionScannee = (TextView) findViewById(R.id.numeroLotProduitReceptionScannee);
        peremptionProduitReceptionScannee = (TextView) findViewById(R.id.peremptionProduitReceptionScannee);
        qteProduitReceptionScannee = (TextView) findViewById(R.id.qteProduitReceptionScannee);
        messageFranceMVO = (TextView) findViewById(R.id.messageFranceMVO);
        compteurReliquat = (TextView) findViewById(R.id.compteurReliquat);
        changeMode = (FloatingActionButton) findViewById(R.id.changeMode);
        message.setVisibility(View.GONE);
        linearRayonnage = (LinearLayout) findViewById(R.id.linearRayonnage);
        layoutlogoscan = (LinearLayout) findViewById(R.id.layoutlogoscan);
        layout_image_datamatrix = (LinearLayout) findViewById(R.id.layout_image_datamatrix);
        layout_emplacement_scannee = (LinearLayout) findViewById(R.id.layout_emplacement_scannee);
        layout_image_document = (LinearLayout) findViewById(R.id.layout_image_document);
        LinearZoneEmplacementProduitReceptionScanne = (LinearLayout) findViewById(R.id.LinearZoneEmplacementProduitReceptionScanne);
        LinearTextProduitReceptionScanne = (LinearLayout) findViewById(R.id.LinearTextProduitReceptionScanne);
        layoutProduitPreparationADH = (LinearLayout) findViewById(R.id.layoutProduitPreparationADH);
        layout_image_codebarre = (LinearLayout) findViewById(R.id.layout_image_codebarre);
        effacerEmplacement = (ImageView) findViewById(R.id.effacerEmplacement);
        boutonModifierEmplacementReceptionScanne = (ImageView) findViewById(R.id.boutonModifierEmplacementReceptionScanne);
        boutonValiderProduitReceptionScannee = (ImageView) findViewById(R.id.boutonValiderProduitReceptionScannee);
        imageViewArmature1 = (ImageView) findViewById(R.id.imageViewArmature1);
        imageViewArmature2 = (ImageView) findViewById(R.id.imageViewArmature2);
        quantiteProduitScannee = (EditText) findViewById(R.id.quantiteProduitScannee);


        changeMode.setLabelText("Datamatrix normal");

        if(icicle != null){
            String messageText = icicle.getString("messageText");
            if(!messageText.isEmpty()){
                message.setText(messageText);
                message.setVisibility(View.VISIBLE);
            }
        }

        // CONTEXTE
        produitContexte = new ProduitContexte(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, serialisation);
        emplacementContexte = new EmplacementContexte(this, db, ADH, utilisateurConnecte);
        pleinVideContexte = new PleinVideContexte(this, message);
        pleinVideLocalisationContexte = new PleinVideLocalisationContexte(this, db, message);


        if(Liste_GTIN != null && preparationId != 0 && !ADH)
        {
            preparationContexte = new PreparationContexte(this, db, Liste_GTIN, utilisateurConnecte.getId(), message, messageFranceMVO, boutonSuppression, preparationId, listedejascanne, nb_produit_scanne);
        }

        if(Liste_GTIN != null && preparationId != 0 && ADH)
        {
            preparationScanneeContexte = new PreparationScanneeContexte(this, db, Liste_GTIN, utilisateurConnecte.getId(), message, messageFranceMVO, boutonSuppression, preparationId, listedejascanne, nb_produit_scanne);
        }
        produitReceptionScanneeContexte = new ProduitReceptionScanneeContexte(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, service_serialisation, conditionnementProduit, utilisateurConnecte, actionId);
        documentContext = new DocumentContext(this, db);
        serviceContexte = new ServiceContexte(this, db);
        authentificationContext = new AuthentificationContext(this, db);
        emplacementReceptionScanneeContexte = new EmplacementReceptionScanneeContexte(this, db, ADH);
        receptionPADContexte = new ReceptionPADContexte(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, service_serialisation, conditionnementProduit, utilisateurConnecte, actionId, liste_id_reliquat);

        if(!service_serialisation && userId != 0)
            franceMVOContexte = new FranceMVOContexte(this, db, GTIN_courant, userId, message, messageFranceMVO, compteurReliquat, qteReliquat, boutonSuppression, reliquat_uid);


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
                clavierMode.setVisibility(View.VISIBLE);
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
            else if(scannerContexteInt == R.string.scannerContextePreparation)
            {
                compteurReliquat.setVisibility(View.GONE);
                bannerTexte = preparationContexte.bannerTexte;
                scannerContexte = String.valueOf(R.string.scannerContextePreparation);
                preparationContexte.stringList = stringList;
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
                if(receptionPADContexte.stringList == null)
                {
                    receptionPADContexte.stringList = new ArrayList<>();
                }
                receptionPADContexte.list_result = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
                if(receptionPADContexte.list_result == null)
                {
                    receptionPADContexte.list_result = new ArrayList<>();
                }
                rayonnageSelect.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                bannerTexte = "Scannez une référence";
            }
            else if(scannerContexteInt == R.string.scannerContextePreparationADH)
            {
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

                if(intent.hasExtra("EmplacementScanne"))
                {
                    preparationScanneeContexte.scanEmplacement = true;
                    preparationScanneeContexte.emplacementProduitCourant = intent.getExtras().getString("EmplacementScanne");
                    preparationScanneeContexte.zoneProduitCourant = intent.getExtras().getString("ZoneScanne");

                    nomZoneProduitReceptionScannee.setText(preparationScanneeContexte.emplacementProduitCourant);
                    nomEmplacementProduitReceptionScannee.setText(preparationScanneeContexte.zoneProduitCourant);
                    bannerTexte = "Scanner un produit";
                    effacerEmplacement.setVisibility(View.VISIBLE);
                    boutonModifierEmplacementReceptionScanne.setVisibility(View.VISIBLE);
                }
                else
                {
                    bannerTexte = "Scanner un emplacement";
                    bannerTexte = preparationScanneeContexte.bannerTexte;
                    effacerEmplacement.setVisibility(View.GONE);
                }

                compteurReliquat.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
                layout_emplacement_scannee.setVisibility(View.GONE);
                layoutlogoscan.setVisibility(View.GONE);
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
            }
            else if(scannerContexteInt == R.string.scannerContexteDocument)
            {
                layout_image_codebarre.setVisibility(View.GONE);
                layout_image_datamatrix.setVisibility(View.GONE);
                layout_image_document.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                bannerTexte = documentContext.bannerTexte;
            }
            else if(scannerContexteInt == R.string.scannerContexteAuthentification)
            {
                ((ImageView)findViewById(R.id.changeMode)).setVisibility(View.GONE);
                ((ImageView)findViewById(R.id.scannerMode)).setVisibility(View.GONE);
                layout_image_codebarre.setVisibility(View.GONE);
                layout_image_document.setVisibility(View.GONE);
                layout_image_datamatrix.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                bannerTexte = authentificationContext.bannerTexte;
            }
            else if(scannerContexteInt == R.string.scannerContexteService)
            {
                layout_image_codebarre.setVisibility(View.GONE);
                layout_image_document.setVisibility(View.GONE);
                layout_image_datamatrix.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                bannerTexte = serviceContexte.bannerTexte;
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
                int uidEmplacement = intent.getExtras().getInt("uidEmplacement");
                if(uidEmplacement != 0)
                {
                    produitReceptionScanneeContexte.GestionEmplacement(uidEmplacement);
                    nomEmplacementProduitReceptionScannee.setVisibility(View.VISIBLE);
                    nomZoneProduitReceptionScannee.setVisibility(View.VISIBLE);
                    nomEmplacementProduitReceptionScannee.setText(produitReceptionScanneeContexte.emplacementProduitCourant);
                    nomZoneProduitReceptionScannee.setText(produitReceptionScanneeContexte.zoneProduitCourant);
                    produitReceptionScanneeContexte.scanEmplacement = false;
                    bannerTexte = "Scanner une référence";

                }
                if(produitReceptionScanneeContexte.stringList == null)
                {
                    produitReceptionScanneeContexte.stringList = new ArrayList<>();
                }
                produitReceptionScanneeContexte.list_result = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
                if(produitReceptionScanneeContexte.list_result == null)
                {
                    produitReceptionScanneeContexte.list_result = new ArrayList<>();
                }
                rayonnageSelect.setVisibility(View.GONE);
                compteurScan.setVisibility(View.GONE);
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

        if(modeRafale && !scannerContexte.contentEquals(String.valueOf(R.string.scannerContexteProduitReceptionScannee)) && !scannerContexte.contentEquals(String.valueOf(R.string.scannerContextePreparationADHScanProduit)) && !scannerContexte.contentEquals(String.valueOf(R.string.scannerContexteReceptionPAD))){
            compteurScan.setVisibility(View.VISIBLE);
            compteurScan.setText(String.valueOf(stringList.size()) + " produit(s) scanné(s)");
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
                    else if(scannerContexteInt ==  R.string.scannerContexteDocument)
                    {
                        extras.putString("Code", "");
                    }
                    else if(scannerContexteInt == R.string.scannerContexteService)
                    {
                        extras.putString("Code","");
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
                    else if(scannerContexteInt == R.string.scannerContextePreparation)
                    {
                        extras.putSerializable("listeString", (Serializable) preparationContexte.liste_preparation_liste_adapte);
                        extras.putInt("nbProduitScanne", preparationContexte.nb_produit_scanne);
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                    {
                        stringList = produitReceptionScanneeContexte.stringList;
                        extras.putSerializable("listeObjet", (Serializable) preparationScanneeContexte.liste_resultat);
                        extras.putStringArrayList("listecodescannee", (ArrayList<String>) preparationScanneeContexte.liste_code_scanne);
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
                onBackPressed();
            }
        });

        changeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                Bundle extras = new Bundle();

                if(scannerContexteInt == R.string.scannerContexteEmplacement)
                {

                }
                else if(scannerContexteInt == R.string.scannerContextePleinVide)
                {
                    stringList = pleinVideContexte.stringList;
                }
                else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
                {

                }
                else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                {
                    stringList = preparationScanneeContexte.stringList;
                }
                else
                {
                    stringList = produitContexte.stringList;
                }

                if (modeRafale) {
                    extras.putStringArrayList("stringList", (ArrayList) stringList);
                    resultIntent.putExtras(extras);
                } else {
                    resultIntent.putExtra("code", "");
                }
                setResult(BarcodeCaptureActivity.RESULT_OK, resultIntent);
                finish();
            }
        });

        if (!modePhoto) {

            ((FloatingActionButton) findViewById(R.id.takePictureButton)).setVisibility(View.GONE);

            ((FloatingActionButton) findViewById(R.id.scannerMode)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newIntent = new Intent(BarcodeCaptureNegativeActivity.this, ScannerSearchOnlyActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
                    extras.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
                    extras.putBoolean("modePhoto", modePhoto);
                    extras.putBoolean("cumule", modeCumule);
                    extras.putBoolean("modeRafale", modeRafale);
                    extras.putBoolean("doitEtreIdentique", intent.getBooleanExtra("doitEtreIdentique", false));
                    extras.putString("Designation", intent.getStringExtra("Designation"));
                    extras.putInt("scannerContexteInt",scannerContexteInt);
                    int codeEchangesActivites = 0;
                    if (modeRafale) {
                        if(scannerContexteInt == R.string.scannerContextePleinVide)
                        {
                            stringList = pleinVideContexte.stringList;
                            extras.putString("dotationIntitule", intent.getExtras().getString("dotationIntitule"));
                            extras.putStringArrayList("detailDotPleinVide_AdressageList",intent.getExtras().getStringArrayList("detailDotPleinVide_AdressageList"));
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_LISTE_CODE_ADRESSAGE;
                        }
                        else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                        {
                            stringList = receptionPADContexte.stringList;
                            int uidEmplacementScanne = receptionPADContexte.uidEmplacementCourant;
                            extras.putInt("uidEmplacement", uidEmplacementScanne);
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
                        else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
                        {
                            codeEchangesActivites = CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION;
                        }
                        else
                        {
                            codeEchangesActivites = CodesEchangesActivites.RETOUR_CODE_GS1;
                        }
                    }
                    newIntent.putExtras(extras);
                    BarcodeCaptureNegativeActivity.this.startActivityForResult(newIntent, codeEchangesActivites);
                    onBackPressed();
                }
            });

            ((FloatingActionButton) findViewById(R.id.clavierMode)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent clavierMode_Intent = new Intent(BarcodeCaptureNegativeActivity.this, KeyboardActivity.class);
                    Bundle clavierMode_Bundle = new Bundle();
                    clavierMode_Bundle.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
                    clavierMode_Bundle.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
                    clavierMode_Bundle.putString("contexte", intent.getExtras().getString("contexte"));
                    clavierMode_Bundle.putString("dotationIntitule", intent.getExtras().getString("dotationIntitule"));
                    clavierMode_Bundle.putStringArrayList("detailDotPleinVide_AdressageList",intent.getExtras().getStringArrayList("detailDotPleinVide_AdressageList"));                    clavierMode_Bundle.putStringArrayList("stringList", (ArrayList<String>) pleinVideContexte.stringList);
                    clavierMode_Intent.putExtras(clavierMode_Bundle);
                    BarcodeCaptureNegativeActivity.this.startActivityForResult(clavierMode_Intent, CodesEchangesActivites.RETOUR_LISTE_CODE_ADRESSAGE);
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
                        compteurScan.setText(String.valueOf(pleinVideContexte.stringList.size()) + " produit(s) scanné(s)");
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
                    else if(scannerContexteInt == R.string.scannerContexteService)
                    {
                        serviceContexte.onTextWatcher(s);
                        code = serviceContexte.code;
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
                    else if(scannerContexteInt == R.string.scannerContextePreparation)
                    {
                        preparationContexte.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(preparationContexte.nb_produit_scanne)+" produit(s) scanné(s)");
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                    {
                        preparationScanneeContexte.onTextWatcher(s);
                        layoutProduitPreparationADH.setVisibility(View.VISIBLE);
                        DesignationProduitScannee.setText(preparationScanneeContexte.designationProduitScanne);
                        quantiteProduitScannee.setVisibility(View.VISIBLE);
                        quantiteProduitScannee.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if(actionId == EditorInfo.IME_ACTION_DONE)
                                {
                                    String text = quantiteProduitScannee.getText().toString();
                                    if(!text.contentEquals(""))
                                    {
                                        int quantite_saisie = Integer.parseInt(text);
                                        if(quantite_saisie > preparationScanneeContexte.nbMaxQuantite)
                                            quantite_saisie = preparationScanneeContexte.nbMaxQuantite;
                                        code = preparationScanneeContexte.uid_preparationLigneCourant+":"+quantite_saisie+":"+ preparationScanneeContexte.code;
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
                        produitReceptionScanneeContexte.onTextWatcher(s);
                        code = produitReceptionScanneeContexte.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                    {
                        receptionPADContexte.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(receptionPADContexte.stringList.size()) + " produit(s) scanné(s)");
                        code = receptionPADContexte.code;
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
            createCameraSource(true, useFlash, useNegative);
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
            if(scannerContexteInt == R.string.scannerContexteEmplacement)
            {
                emplacementContexte.onActivityResult(requestCode, data);
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
                if(!documentContext.code.isEmpty()){
                    ((EditText) findViewById(R.id.contenuCode)).setText(documentContext.code + "\n");
                }
            }
            else if(scannerContexteInt == R.string.scannerContexteAuthentification)
            {
                authentificationContext.onActivityResult(requestCode, data);
                if(!authentificationContext.username.isEmpty() && !authentificationContext.password.isEmpty())
                {
                    ((EditText) findViewById(R.id.contenuCode)).setText("username:"+authentificationContext.username+":password:"+authentificationContext.password+"\n");
                }
            }
            else if(scannerContexteInt == R.string.scannerContexteFranceMVO)
            {
                franceMVOContexte.onActivityResult(requestCode, data);
                if(!franceMVOContexte.code.isEmpty()){
                    ((EditText) findViewById(R.id.contenuCode)).setText(franceMVOContexte.code + "\n");
                }
            }
            else if(scannerContexteInt == R.string.scannerContextePreparation)
            {
                preparationContexte.onActivityResult(requestCode, data);
                if (!preparationContexte.code.isEmpty()) {
                    ((EditText) findViewById(R.id.contenuCode)).setText(preparationContexte.code + "\n");
                }
            }
            else if(scannerContexteInt == R.string.scannerContextePreparationADH)
            {
                preparationScanneeContexte.onActivityResult(requestCode, data);
                if(!preparationScanneeContexte.code.isEmpty())
                {
                    ((EditText) findViewById(R.id.contenuCode)).setText(preparationScanneeContexte.code+"\n");
                }
            }
            else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
            {
                receptionPADContexte.onActivityResult(requestCode, data);
                boutonSuppression.callOnClick();
            }
            else if(scannerContexteInt == R.string.scannerContexteService)
            {
                serviceContexte.onActivityResult(requestCode, data);
                if(!serviceContexte.code.isEmpty())
                    ((EditText) findViewById(R.id.contenuCode)).setText(serviceContexte.code+"\n");
            }
            else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
            {
                produitReceptionScanneeContexte.onActivityResult(requestCode, data);
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
            if (close)
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
    protected void createCameraSource(boolean autoFocus, boolean useFlash, boolean useNegative) {
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
                .build(mGraphicOverlay, mCameraSourcePreview, useNegative);

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
            createCameraSource(autoFocus, useFlash, useNegative);
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

            if (mGraphicOverlay.getAutofocusRect().contains(bouncingBox)) {
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
                                            Context context = BarcodeCaptureNegativeActivity.this;
                                            String title = preparationScanneeContexte.designationProduitScanne;
                                            String message = "Changer la quantité: ";
                                            int maxValue = preparationScanneeContexte.quantite_max_number_picker;
                                            int value = preparationScanneeContexte.quantiteAAfficher;

                                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    int qteAprès = Alerte.aNumberPicker.getValue();
                                                    qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                                    preparationScanneeContexte.quantiteAAfficher = qteAprès;
                                                    //adapter.notifyDataSetChanged();
                                                    InputMethodManager imm = (InputMethodManager) BarcodeCaptureNegativeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                                        Toast toast = Toast.makeText(BarcodeCaptureNegativeActivity.this, "Échec", Toast.LENGTH_LONG);
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
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                    ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                }
                if(scannerContexte.contentEquals(scannerContexteDocument)){
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                    ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                }

                if(scannerContexte.contentEquals(scannerContexteAuthentification)){
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                    ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                }

                if(scannerContexte.contentEquals(scannerContextService)){
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                    ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                }

                if(scannerContexte.contentEquals(scannerContexteProduitReceptionScannee)){
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                    ((EditText) findViewById(R.id.contenuCode)).setText(best.rawValue + "\n");
                    bannerTexte = produitReceptionScanneeContexte.bannerTexte;
                    if(produitReceptionScanneeContexte.scanEmplacement)
                    {
                        nomEmplacementProduitReceptionScannee.setVisibility(View.VISIBLE);
                        nomZoneProduitReceptionScannee.setVisibility(View.VISIBLE);
                        nomEmplacementProduitReceptionScannee.setText(produitReceptionScanneeContexte.emplacementProduitCourant);
                        nomZoneProduitReceptionScannee.setText(produitReceptionScanneeContexte.zoneProduitCourant);
                        produitReceptionScanneeContexte.scanEmplacement = false;
                        boutonModifierEmplacementReceptionScanne.setVisibility(View.VISIBLE);
                        if(!referenceProduitReceptionScannee.getText().toString().contentEquals(""))
                        {
                            boutonValiderProduitReceptionScannee.setVisibility(View.VISIBLE);
                        }
                        bannerTexte = "Scanner une référence";
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
                                    Context context = BarcodeCaptureNegativeActivity.this;
                                    String title = produitReceptionScanneeContexte.designationProduitCourant;
                                    String message = "Changer la quantité: ";
                                    int maxValue = 10000;
                                    int value = produitReceptionScanneeContexte.quantite_a_afficher;

                                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            int qteAprès = Alerte.aNumberPicker.getValue();
                                            qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                            produitReceptionScanneeContexte.quantite_a_afficher = qteAprès;
                                            //adapter.notifyDataSetChanged();
                                            InputMethodManager imm = (InputMethodManager) BarcodeCaptureNegativeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                                    Toast toast = Toast.makeText(BarcodeCaptureNegativeActivity.this, "Échec", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        });
                        bannerTexte = "Scanner une référence";
                    }

                    ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                }
                if(scannerContexte.contentEquals(scannerContexteReceptionPAD)){
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
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
                                Context context = BarcodeCaptureNegativeActivity.this;
                                String title = receptionPADContexte.designationProduitCourant;
                                String message = "Changer la quantité: ";
                                int maxValue = receptionPADContexte.quantiteMaxNumberPicker;
                                int value = receptionPADContexte.quantite_a_afficher;

                                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        int qteAprès = Alerte.aNumberPicker.getValue();
                                        qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                        receptionPADContexte.quantite_a_afficher = qteAprès;
                                        //adapter.notifyDataSetChanged();
                                        InputMethodManager imm = (InputMethodManager) BarcodeCaptureNegativeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                                Toast toast = Toast.makeText(BarcodeCaptureNegativeActivity.this, "Échec", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    });
                    bannerTexte = "Scanner une référence";
                    ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
/*        if (floatingActionMenu.isOpened()) {
            floatingActionMenu.close(true);
        } else */if (modeRafale) {
            // Dans le cas du mode rafale on subodore que le fait de backPresser est équivalent à cliquer sur bouton suppression
            boutonSuppression.callOnClick();
        } else {
            super.onBackPressed();
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
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getChildAt(0).getLayoutParams();
        params.gravity = Gravity.FILL_HORIZONTAL | Gravity.BOTTOM;
        snackBarView.getChildAt(0).setLayoutParams(params);
        snackbar.show();
    }

    public void afficherAlerteFranceMVO(String produitDesignation, String resultat, String numeroSerie, String motif)
    {
        toneGen1.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_SSL,250);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(BarcodeCaptureNegativeActivity.this);
        LayoutInflater inflater = BarcodeCaptureNegativeActivity.this.getLayoutInflater();
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
