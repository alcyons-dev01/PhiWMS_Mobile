package fr.alcyons.phimr4.BarcodeSearch;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;

import java.io.IOException;
import java.util.Date;

import fr.alcyons.phimr4.BarcodeSearch.camera.CameraSource;
import fr.alcyons.phimr4.Outils.OutilsGestionPhotos;
import fr.alcyons.phimr4.R;


/**
 * Created by jessica on 25/09/2017.
 */

public class BarcodeCaptureWithTakePicture extends BarcodeCaptureActivity {

    Bitmap bitmap;

    public BarcodeCaptureWithTakePicture() throws IOException {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton takePictureButton = (FloatingActionButton) findViewById(R.id.takePictureButton);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGraphicOverlay.getGraphics().size() == 0) {

                    // Prendre une photo
                    mCameraSource.takePicture(new CameraSource.ShutterCallback() {
                        @Override
                        public void onShutter() {
                        }
                    }, new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data) {
                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Date date = new Date();
                            final String imageNom = String.valueOf(date.getTime());
                            mCameraSource.pause(true);
                            final ProgressDialog progressDialog = ProgressDialog.show(BarcodeCaptureWithTakePicture.this, "Veuillez patienter", "Enregistrement de la photo en cours");
                            Thread mThread = new Thread() {
                                @Override
                                public void run() {
                                    OutilsGestionPhotos.saveExternalStorageImageJPG(BarcodeCaptureWithTakePicture.this, bitmap, imageNom);
                                    progressDialog.dismiss();
                                }
                            };
                            mThread.start();

                            ((EditText) findViewById(R.id.contenuCode)).setText(imageNom + "\n");
                        }
                    });

                } else {
                    BarcodeCaptureWithTakePicture.super.onTap(getResources().getDisplayMetrics().widthPixels / 2, getResources().getDisplayMetrics().heightPixels / 2);
                }
                BarcodeCaptureWithTakePicture.super.onBackPressed();
            }
        });

        takePictureButton.setVisibility(View.VISIBLE);

    }
}
