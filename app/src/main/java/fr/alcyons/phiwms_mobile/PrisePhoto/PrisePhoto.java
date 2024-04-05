
package fr.alcyons.phiwms_mobile.PrisePhoto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.alcyons.phiwms_mobile.OriginalActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.PrisePhoto.camera.CameraSource;
import fr.alcyons.phiwms_mobile.PrisePhoto.camera.CameraSourcePreview;
import fr.alcyons.phiwms_mobile.PrisePhoto.camera.GraphicOverlay;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by jessica on 08/11/2017.
 */

public class PrisePhoto extends OriginalActivity {
    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    protected static final String TAG = "Prise-Photo";

    // intent request code to handle updating play services if needed.
    protected static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    protected static final int RC_HANDLE_CAMERA_PERM = 2;

    // Liste utilisée dans le cas de scan en chaine
    protected CameraSource mCameraSource;
    protected CameraSourcePreview mPreview;
    protected GraphicOverlay<PrisePhotoGraphic> mGraphicOverlay;

    Intent intent;

    FloatingActionButton boutonSuppression;
    FloatingActionButton boutonFlash;
    FloatingActionButton boutonPhoto;

    byte[] photoProduit = null;

    String priseDePhotoContexte;
    String bannerTexte = "Prendre la photo du produit";

    String priseDePhotoContexteProduit = String.valueOf(R.string.priseDePhotoContexteProduit);
    String priseDePhotoContexteBonDeLivraison = String.valueOf(R.string.priseDePhotoContexteBonDeLivraison);

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Récupération du Layout utilisé pour cette activité
        setContentView(R.layout.activity_prise_photo);

        // Récupération de l'intent
        intent = PrisePhoto.this.getIntent();

        priseDePhotoContexte = intent.getExtras().getString("contexte");
        if (priseDePhotoContexte == null) {
            priseDePhotoContexte = String.valueOf(R.string.scannerContexteProduit);
            ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
        }
        if (priseDePhotoContexte.contentEquals("priseDePhotoContexteBonDeLivraison")) {
            ((TextView) findViewById(R.id.banner)).setText("Prendre la photo du bon de livraison");
        }

        if (priseDePhotoContexte.contentEquals("priseDePhotoLivraison")) {
            ((TextView) findViewById(R.id.banner)).setText("Prendre la photo de la livraison");
        }

        if (priseDePhotoContexte.contentEquals("photoAction")) {
            ((TextView) findViewById(R.id.banner)).setText("Prendre la photo d'un document");
        }

        // Récupération des boutons et association des actions
        boutonSuppression = (FloatingActionButton) findViewById(R.id.boutonFermeture);
        boutonSuppression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                Bundle extras = new Bundle();
                resultIntent.putExtras(extras);
                setResult(CodesEchangesActivites.RETOUR_PRISE_PHOTO, resultIntent);
                PrisePhoto.this.finish();
            }
        });

        boutonFlash = (FloatingActionButton) findViewById(R.id.flash);
        boutonFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraSource.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
            }
        });

        boutonPhoto = (FloatingActionButton) findViewById(R.id.takePictureButton);
        boutonPhoto.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGraphicOverlay.getGraphics().size() == 0) {
                    //prendre une photo
                    mCameraSource.takePicture(new CameraSource.ShutterCallback() {
                        @Override
                        public void onShutter() {
                        }
                    }, new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data) {
                            String name = null;

                            if(priseDePhotoContexte.contentEquals("photoProduit"))
                            {
                                // Nom de l'image
                                String nomProduit = intent.getStringExtra("nomProduit");
                                if (nomProduit == null) {
                                    nomProduit = intent.getStringExtra("nomDispositif");
                                }
                                nomProduit = nomProduit.replaceAll(" ", "");
                                nomProduit = nomProduit.replaceAll("/", "-");
                                name = nomProduit + ".jpeg";

                                // Sauvegarde de l'image
                                String root = Environment.getExternalStorageDirectory().toString();
                                root = getFilesDir().getAbsolutePath()+File.separator;
                                Bitmap pictureTaken = BitmapFactory.decodeByteArray(data, 0, data.length);

                                File dir = null;

                                dir = new File(root + "Photos/PhotoASynchroniser");

                                ImageView imageView = new ImageView(getApplicationContext());
                                imageView.setImageBitmap(pictureTaken);
                                setContentView(imageView);

                                if(!dir.exists())
                                {
                                    boolean dossier = dir.mkdirs();
                                    Log.e("Creer dossier", String.valueOf(dossier));
                                }

                                File file = new File(dir, name);

                                if(!file.exists())
                                {
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }


                                try {
                                    FileOutputStream out = new FileOutputStream(file);
                                    //appelle la fonction qui gère la rotation
                                    pictureTaken = getRotatedBitmap(dir + "/" + name, pictureTaken);

                                    pictureTaken.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                    // Toast.makeText(PrisePhoto.this, "Image sauvegardée", Toast.LENGTH_SHORT).show();
                                    out.flush();
                                    out.close();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Fin de l'activité
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri contentUri = Uri.fromFile(file);
                                mediaScanIntent.putExtra("photoProduit", contentUri.toString());
                                mediaScanIntent.putExtra("nomPhoto", name);
                                getApplicationContext().sendBroadcast(mediaScanIntent);
                                setResult(RESULT_OK, mediaScanIntent);
                                PrisePhoto.this.finish();
                            }
                            else if(!priseDePhotoContexte.contentEquals("priseDePhotoContexteBonDeLivraison") && !priseDePhotoContexte.contentEquals("priseDePhotoLivraison") && !priseDePhotoContexte.contentEquals("photoAction"))
                            {
                                boolean validation = Alerte.afficherAlerte(PrisePhoto.this, "Validation", "Souhaitez-vous publier la photo sur MédicalObjective ?", "OuiNon");

                                if(validation)
                                {
                                    // Nom de l'image
                                    String nomProduit = intent.getStringExtra("nomProduit");
                                    if (nomProduit == null) {
                                        nomProduit = intent.getStringExtra("nomDispositif");
                                    }
                                    nomProduit = nomProduit.replaceAll(" ", "");
                                    nomProduit = nomProduit.replaceAll("/", "-");
                                    name = nomProduit + ".jpeg";

                                    // Sauvegarde de l'image
                                    String root = Environment.getExternalStorageDirectory().toString();
                                    root = getFilesDir().getAbsolutePath()+File.separator;
                                    Bitmap pictureTaken = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    File dir = null;

                                    if (OutilsGestionConnexionReseau.isServerAccessible(PrisePhoto.this)) {
                                        dir = new File(root + "Photos/MedicalObjective/");
                                    } else {
                                        dir = new File(root + "Photos/PhotoASynchroniser");

                                        ImageView imageView = new ImageView(getApplicationContext());
                                        imageView.setImageBitmap(pictureTaken);
                                        setContentView(imageView);
                                    }

                                    if(!dir.exists())
                                    {
                                        boolean dossier = dir.mkdirs();
                                        Log.e("Creer dossier", String.valueOf(dossier));
                                    }

                                    File file = new File(dir, name);

                                    if(!file.exists())
                                    {
                                        try {
                                            file.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }


                                    try {
                                        FileOutputStream out = new FileOutputStream(file);
                                        //appelle la fonction qui gère la rotation
                                        pictureTaken = getRotatedBitmap(dir + "/" + name, pictureTaken);

                                        pictureTaken.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                       // Toast.makeText(PrisePhoto.this, "Image sauvegardée", Toast.LENGTH_SHORT).show();
                                        out.flush();
                                        out.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    // Fin de l'activité
                                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri contentUri = Uri.fromFile(file);
                                    mediaScanIntent.putExtra("photoProduit", contentUri.toString());
                                    mediaScanIntent.putExtra("nomPhoto", name);
                                    getApplicationContext().sendBroadcast(mediaScanIntent);
                                    setResult(CodesEchangesActivites.RETOUR_PRISE_PHOTO, mediaScanIntent);
                                    PrisePhoto.this.finish();
                                }
                                else
                                {
                                    finish();
                                    startActivity(getIntent());
                                }
                            }
                            else
                            {
                                if(priseDePhotoContexte.contentEquals("priseDePhotoContexteBonDeLivraison"))
                                {
                                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                                    Date date = new Date();
                                    String new_date = dateFormat.format(date);
                                    String CommandeNumero = intent.getExtras().getString("CommandeNumero");
                                    name = CommandeNumero+ "_" + new_date + "_ReceptionPuiBonLivraison.jpeg";
                                }
                                else if(priseDePhotoContexte.contentEquals("priseDePhotoLivraison"))
                                {
                                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                                    Date date = new Date();
                                    String new_date = dateFormat.format(date);
                                    String preparationUID = String.valueOf(intent.getExtras().getInt("preparationUID"));
                                    name = preparationUID+ "_" + new_date + "_LivraisonPhoto.jpeg";
                                }
                                else if(priseDePhotoContexte.contentEquals("photoAction"))
                                {
                                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                                    Date date = new Date();
                                    String new_date = dateFormat.format(date);
                                    name = "Action"+new_date;
                                }
                                else
                                {
                                    // Nom de l'image
                                    String nomProduit = intent.getStringExtra("nomProduit");
                                    if (nomProduit == null) {
                                        nomProduit = intent.getStringExtra("nomDispositif");
                                    }
                                    nomProduit = nomProduit.replaceAll(" ", "");
                                    nomProduit = nomProduit.replaceAll("/", "-");
                                    name = nomProduit + ".jpeg";
                                }


                                // Sauvegarde de l'image
                                String root = Environment.getExternalStorageDirectory().toString();
                                root = getFilesDir().getAbsolutePath()+File.separator;
                                Bitmap pictureTaken = BitmapFactory.decodeByteArray(data, 0, data.length);

                                File dir = null;

                                if(priseDePhotoContexte.contentEquals("priseDePhotoContexteBonDeLivraison"))
                                {
                                    dir = new File(root + "Documents/");
                                }
                                else if(priseDePhotoContexte.contentEquals("photoAction"))
                                {
                                    dir = new File(root + "PhotosAction/");
                                }

                                else if(priseDePhotoContexte.contentEquals("priseDePhotoLivraison"))
                                {
                                    dir = new File(root + "Documents/");
                                }
                                else
                                {
                                    if (OutilsGestionConnexionReseau.isServerAccessible(PrisePhoto.this)) {
                                        dir = new File(root + "Photos/MedicalObjective/");
                                    } else {
                                        dir = new File(root + "Photos/PhotoASynchroniser");

                                        ImageView imageView = new ImageView(getApplicationContext());
                                        imageView.setImageBitmap(pictureTaken);
                                        setContentView(imageView);
                                    }
                                }

                                if(!dir.exists())
                                {
                                    boolean dossier = dir.mkdirs();
                                    Log.e("Creer dossier", String.valueOf(dossier));
                                }

                                File file = new File(dir, name);

                                if(!file.exists())
                                {
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }


                                try {
                                    FileOutputStream out = new FileOutputStream(file);
                                    //appelle la fonction qui gère la rotation
                                    pictureTaken = getRotatedBitmap(dir + "/" + name, pictureTaken);

                                    pictureTaken.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                    Toast.makeText(PrisePhoto.this, "Image sauvegardée", Toast.LENGTH_SHORT).show();
                                    out.flush();
                                    out.close();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Fin de l'activité
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri contentUri = Uri.fromFile(file);
                                mediaScanIntent.putExtra("photoProduit", contentUri.toString());
                                mediaScanIntent.putExtra("nomPhoto", name);
                                getApplicationContext().sendBroadcast(mediaScanIntent);
                                setResult(CodesEchangesActivites.RETOUR_PRISE_PHOTO, mediaScanIntent);
                                PrisePhoto.this.finish();
                            }

                        }
                    });
                }
            }
        }));

        // Récupération de la preview de la caméra
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<PrisePhotoGraphic>) findViewById(R.id.graphicOverlay);

        // Récupération dans l'intent du boolean UseFlash
        boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);

        // Vérification de la permission d'utilitation de la camra.
        // Si permission non accordé, demande à l'utilisateur
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, useFlash);
        } else {
            requestCameraPermission();
        }

    }

    /**
     * Permet de demander à l'utilisateur son autorisation d'utiliser la caméra
     */
    protected void requestCameraPermission() {

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
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


    /**
     * Création et lancement de la caméra
     */
    @SuppressLint("InlinedApi")
    protected void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // Création de la caméra
        // Utilisation de la caméra de derrière
        // Spécification de la taille de la preview
        // spécification de la fréquence d'image démandée par seconde
        CameraSource.Builder builder = new CameraSource.Builder(context)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);


        // Vérification que l'auto focus est une option disponible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        // Lancement de la caméra
        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build(mGraphicOverlay);

    }

    /**
     * Relance la caméra.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stop la caméra.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Détruit et libère toutes les ressources liés à la caméra
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    //Gérer la rotation de l'image après la prise de photo
    private Bitmap getRotatedBitmap(String path, Bitmap bitmap) {
        Bitmap rotatedBitmap = null;
        Matrix m = new Matrix();

        if (bitmap.getWidth() > bitmap.getHeight()) {
            m.setRotate(90);
        }

        // Rotates the image according to the orientation
        rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);


        return rotatedBitmap;
    }
}