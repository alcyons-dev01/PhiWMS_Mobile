package fr.alcyons.phiwms_mobile.BarcodeSearch;

import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_PKI.checkApiAsync;
import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_SINGLE_PACK.serialisationVerificationSingle;

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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.camera.CameraSource;
import fr.alcyons.phiwms_mobile.BarcodeSearch.camera.CameraSourcePreview;
import fr.alcyons.phiwms_mobile.BarcodeSearch.camera.GraphicOverlay;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ReceptionPADContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.BarcodeCaptureNegativeActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class BarcodeReception2025Activity extends ServiceActivity {

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

    // INTENT
    Intent intent;
    boolean modeCumule = true;
    boolean useFlash;

    // GRAPHIQUE
    Bitmap actualPicture;
    FloatingActionButton boutonSuppression;
    FloatingActionButton clavierMode;
    FloatingActionButton changeMode;
    TextView numReception;
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

    List<Integer> liste_id_reliquat;

    String tempCodeScanne;
    Depot_Emplacement emplacement_courant;
    Produit produitCourant;
    PH_Reliquat reliquatCourant;
    PH_Reliquat_Reception_Adapte uniqueReceptionPUIAdapte;
    List<PH_Reliquat_Reception_Adapte> list_reliquat_receptionPuiAdapte;
    PH_Reliquat_Reception_Adapte.Lot nouveau_lot;
    List<String> listGTIN;
    Serialisation serialisation;
    boolean serialisationActive;
    boolean validationEnCours;
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

        intent = BarcodeReception2025Activity.this.getIntent();

        liste_lot = intent.getExtras().getStringArrayList("liste_lot");
        ordreTri = intent.getExtras().getString("ordreTri");
        receptionID = intent.getExtras().getInt("ReceptionID");
        commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
        list_reliquat_receptionPuiAdapte = (List<PH_Reliquat_Reception_Adapte>) intent.getExtras().getSerializable("ReceptionPUIAdapte");
        uniqueReceptionPUIAdapte = (PH_Reliquat_Reception_Adapte) intent.getExtras().getSerializable("UniqueReceptionPUIAdapte");
        reliquatCourant = (PH_Reliquat) intent.getExtras().getSerializable("ReliquatCourant");
        listGTIN = new ArrayList<>();
        validationEnCours = false;
        // GRAPHIQUE
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        numReception = (TextView) findViewById(R.id.numPreparation);
        depot = (TextView) findViewById(R.id.depot);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);
        boutonSuppression = (FloatingActionButton) findViewById(R.id.boutonFermeture);
        clavierMode = (FloatingActionButton) findViewById(R.id.clavierMode);
        clavierMode.setVisibility(View.GONE);
        imageValidation = ((ImageView) findViewById(R.id.imageValidation));
        changeMode = (FloatingActionButton) findViewById(R.id.changeMode);

        numReception.setText("N°"+commandeCourante.getNumero());
        depot.setText(commandeCourante.getFournisseur());

        changeMode.setLabelText("Datamatrix inversé");
        ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");

        serialisation = new Serialisation(BarcodeReception2025Activity.this, db, utilisateurConnecte);
        checkApiAsync(this).thenAccept(success -> {
            serialisationActive = success;
            if(success)
            {
                ((ImageView) findViewById(R.id.imageLogoFMVO)).setVisibility(View.VISIBLE);
            }
        });

        if(icicle != null){
            String messageText = icicle.getString("messageText");
        }

        // ACTION GRAPHIQUE
        boutonSuppression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerSearchOnlyIntent = new Intent();
                Bundle scannerSearchOnlyBundle = new Bundle();
                int codeEchangeActivity = 0;

                if(nouveau_lot != null && emplacement_courant != null)
                {
                    boolean confirmation = Alerte.afficherAlerte(BarcodeReception2025Activity.this, "Attention", "Valider le dernier lot scanné ?", "OuiNon");
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
                BarcodeReception2025Activity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                BarcodeReception2025Activity.this.finish();
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
                Intent newIntent = new Intent(BarcodeReception2025Activity.this, BarcodeCaptureNegativeActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
                extras.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
                extras.putString("contexte", intent.getExtras().getString("contexte"));
                extras.putBoolean("cumule", modeCumule);
                extras.putBoolean("doitEtreIdentique", intent.getBooleanExtra("doitEtreIdentique", false));
                extras.putString("Designation", intent.getStringExtra("Designation"));

                int codeEchangesActivites = 0;

                newIntent.putExtras(extras);
                BarcodeReception2025Activity.this.startActivityForResult(newIntent, codeEchangesActivites);
            }
        });

        findViewById(R.id.takePictureButton).setVisibility(View.GONE);

        findViewById(R.id.scannerMode).setOnClickListener(v -> {
            Intent newIntent = new Intent(BarcodeReception2025Activity.this, ScannerPreparationActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
            extras.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
            int codeEchangesActivites = CodesEchangesActivites.RETOUR_SCANNER;

            newIntent.putExtras(extras);
            BarcodeReception2025Activity.this.startActivityForResult(newIntent, codeEchangesActivites);
        });

        findViewById(R.id.clavierMode).setOnClickListener(v -> {
            Intent clavierMode_Intent = new Intent(BarcodeReception2025Activity.this, KeyboardActivity.class);
            Bundle clavierMode_Bundle = new Bundle();
            clavierMode_Bundle.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
            clavierMode_Bundle.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
            clavierMode_Bundle.putString("contexte", intent.getExtras().getString("contexte"));
            clavierMode_Bundle.putString("dotationIntitule", intent.getExtras().getString("dotationIntitule"));
            clavierMode_Bundle.putStringArrayList("detailDotPleinVide_AdressageList",intent.getExtras().getStringArrayList("detailDotPleinVide_AdressageList"));
            clavierMode_Intent.putExtras(clavierMode_Bundle);
            BarcodeReception2025Activity.this.startActivityForResult(clavierMode_Intent, CodesEchangesActivites.RETOUR_LISTE_CODE_ADRESSAGE);
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
        gestureDetector = new GestureDetector(this, new BarcodeReception2025Activity.CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new BarcodeReception2025Activity.ScaleListener());

        mGraphicOverlay.setClickable(true);
        mGraphicOverlay.setOnTouchListener((v, ev) -> gestureDetector.onTouchEvent(ev));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
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
            //
            String codeScanne = best.rawValue;
            if((codeScanne.startsWith("01") || codeScanne.startsWith("02")) && codeScanne.length() == 16)
            {
                tempCodeScanne = codeScanne;
            }
            else if(tempCodeScanne != null && !tempCodeScanne.contentEquals("") && !codeScanne.startsWith("01") && !codeScanne.startsWith("02"))
            {
                tempCodeScanne = tempCodeScanne +  codeScanne;
                /**
                 * TODO : déclencher le scan
                 */
                //EditTextScanee.setText(tempCodeScanne+"\n");
                tempCodeScanne = "";
            }
            else
            {
                tempCodeScanne = "";
                String lot;
                String serie;
                String gtin_courant = "";
                String gtin_courant_sans_ai = "";
                String date_peremption_courant = "";
                String date_peremption_serialisation = "";
                if(codeScanne.startsWith("PHITAGPLACE"))
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
                        lot = "";
                        //on essaye de récupérer via le code inconnu
                        List<Produit> produits  = ProduitOpenHelper.getProduitByCodeInconnu(db, best.rawValue.toString().substring(0, best.rawValue.length()-1));
                        if (produits.size() == 1) {
                            produitCourant = produits.get(0);
                        }
                    }

                    if(produitCourant != null)
                    {
                        if(produitCourant.isSuivi_Serialisation() && produitCourant.isSerialiser_Reception_Delivrance())
                        {
                            //Serialisation.Serialisation_Verifier(utilisateurConnecte.getId(), true, false, gtin_courant, "GTIN", lot, date_peremption_courant, serie, "RECEPTION", String.valueOf(receptionID), commandeCourante.getNumero(), "RECEPTION");
                        }

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
                        else if(produitCourant.isSuivi_Serialisation() && produitCourant.isSerialiser_Reception_Delivrance() && serie.contentEquals(""))
                        {
                            afficherSnackBar("Numéro de série non scanné");
                        }
                        else
                        {
                            List<PH_Reliquat> reliquatPreparer = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, reliquatCourant.getcommandeNumero(), reliquatCourant.getProduitID());
                            int qte_demander = reliquatCourant.getQteCommande() - reliquatCourant.getQteLivraison();
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
                                boolean seriedejascanne = false;
                                for(PH_Reliquat_Reception_Adapte.Lot lotCourant : uniqueReceptionPUIAdapte.getlotList())
                                {
                                    if(lotCourant.getNumeroLot().equals(lot))
                                    {
                                        if(produitCourant.isSuivi_Serialisation() || produitCourant.isSerialiser_Reception_Delivrance())
                                        {
                                            if(lotCourant.getNumero_serie().contentEquals(""))
                                            {
                                                nouveau_lot = lotCourant;
                                            }
                                            else if(lotCourant.getNumero_serie().contentEquals(serie))
                                            {
                                                seriedejascanne = true;
                                            }
                                            else
                                            {
                                                nouveau_lot = null;
                                            }
                                        }
                                        else
                                        {
                                            nouveau_lot = lotCourant;
                                        }
                                    }
                                }

                                if(seriedejascanne)
                                {
                                    produitCourant = null;
                                    nouveau_lot = null;
                                    if(yourCountDownTimer != null)
                                        yourCountDownTimer.cancel();

                                    reinitialisationInterface();
                                    afficherSnackBar("Numéro de série déjà scanné");
                                }
                                else
                                {
                                    if(nouveau_lot == null)
                                        nouveau_lot = uniqueReceptionPUIAdapte.new Lot(lot, date_peremption_courant, serie, "");

                                    if(!serie.contentEquals(""))
                                    {
                                        /**
                                         * TODO : vérification du statut du numéro de série lors du scan
                                         * */
                                        //Création de la serialisation
                                        if(serialisationActive)
                                        {
                                            int serialisationUID = (int) Serialisation.Serialisation_Creer(utilisateurConnecte.getId(), "G110", gtin_courant, "GTIN", lot, date_peremption_serialisation, serie, "RECEPTION", commandeCourante.getNumero());
                                            serialisationVerificationSingle(this, db, utilisateurConnecte, serialisationUID, gtin_courant, "GTIN", lot, date_peremption_serialisation, serie).thenAccept(success -> {
                                                if(!success)
                                                {
                                                    Log.e("Erreur serialisation", "Erreur lors de la création de la serialisation");
                                                }
                                            });
                                        }

                                        findViewById(R.id.numeroSerie).setVisibility(View.VISIBLE);
                                        ((TextView) findViewById(R.id.numeroSerie)).setText(serie);
                                    }

                                    findViewById(R.id.validationScan).setVisibility(View.VISIBLE);

                                    //gestion du clic sur le compteur
                                    int finalQte_restante = qte_restante;
                                    if(!produitCourant.isSuivi_Serialisation() || !produitCourant.isSerialiser_Reception_Delivrance())
                                    {
                                        findViewById(R.id.layout_qte_saisie_lot_preparation).setOnClickListener(view -> {
                                            if(yourCountDownTimer != null)
                                                yourCountDownTimer.cancel();

                                            Context context = BarcodeReception2025Activity.this;

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
                                    }

                                    //gestion de la validation du scan
                                    blinkImageValidation();
                                    String finalDate_peremption_courant = date_peremption_courant;
                                    findViewById(R.id.validationScan).setOnClickListener(v -> {
                                        if(validationEnCours)
                                        {
                                            //gestion enregistrement du lot scannee
                                            int quantiteSaisie = 0;
                                            try{
                                                quantiteSaisie = Integer.parseInt(((TextView) findViewById(R.id.qteSaisie)).getText().toString());
                                            }
                                            catch (Exception ignored)
                                            {
                                            }

                                            String zone_string = "";
                                            int zoneId = 0;
                                            String emplacement_string = "";
                                            int emplacementId = 0;
                                            if (commandeCourante.getRef_Depot_Dest().contains("-PAD")) {
                                                zone_string = "RECEPTION";
                                                emplacement_string = "RECEPTION-" + commandeCourante.getNumero() + "-" + commandeCourante.getPatient_identite();
                                            } else {
                                                if(emplacement_courant != null)
                                                {
                                                    Depot_Zone zoneCourante = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());
                                                    if(zoneCourante != null)
                                                    {
                                                        zone_string = zoneCourante.getZoneName();
                                                        zoneId = zoneCourante.getZoneID();
                                                    }
                                                    emplacement_string = emplacement_courant.getAdressage();
                                                    emplacementId = emplacement_courant.get_UID();
                                                }
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
                                        }
                                    });
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
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
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
        validationEnCours = false;
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
        validationEnCours = true;
    }

    private void enregistrerPhReliquat(PH_Reliquat_Reception_Adapte reliquatReceptionAdapte)
    {
        PH_Reliquat ph_reliquat_base = PH_ReliquatOpenHelper.getPH_ReliquatById(db, reliquatReceptionAdapte.getPhReliquatUID());

        List<PH_Reliquat> listeReliquatAjouter = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeCourante.getNumero(), ph_reliquat_base.getProduitID());
        for(PH_Reliquat reliquatcourant : listeReliquatAjouter)
        {
            PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquatcourant);
            //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, reliquatcourant.getPhiMR4UUID(), reliquatcourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.SUPPR);
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
                String[] datePeremptionTab = datePeremption.split("/");
                if(datePeremptionTab.length == 3)
                    datePeremption = datePeremptionTab[2] + "-" + datePeremptionTab[1] + "-" + datePeremptionTab[0];

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
                    //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, phReliquatCourant.getPhiMR4UUID(), phReliquatCourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                }
            }
        }


        //ElementASynchroniserOpenHelper.toutSynchroniser(ScannerReception2025Activity.this, db, utilisateurConnecte, false);
    }
}
