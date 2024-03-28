package com.example.phiwms_mobile.BarcodeSearch;

import androidx.core.app.ActivityCompat;
/*import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.phiwms_mobile.BarcodeSearch.camera.CameraSource;
import com.example.phiwms_mobile.Outils.Alerte;
import com.example.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import com.example.phiwms_mobile.Outils.PackageManagerUtils;
import com.example.phiwms_mobile.R;


public class BarcodeCaptureWithGoogleVisionSearchActivity extends BarcodeCaptureActivity {

    public static final String urlRequete = "https://vision.googleapis.com/v1/images:annotate?key=";
    private static final String API_KEY = "AIzaSyCfEcVk10L-Ql2ZFKxmms9MTLypkQQzDBM";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    Image base64EncodedImage;
    byte[] receivedData;

    public BarcodeCaptureWithGoogleVisionSearchActivity() throws IOException {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_barcode_with_google_vision_search);

        FloatingActionButton takePictureButton = (FloatingActionButton) findViewById(R.id.takePictureButton);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OutilsGestionConnexionReseau.isOnline()) {
                    Toast toast = Toast.makeText(BarcodeCaptureWithGoogleVisionSearchActivity.this, "Veuillez vous connecter à internet avant de retenter.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    BarcodeCaptureWithGoogleVisionSearchActivity.super.onBackPressed();
                    return;
                }

                if (mGraphicOverlay.getGraphics().size() == 0) {
                    Toast.makeText(BarcodeCaptureWithGoogleVisionSearchActivity.this, "Océrisation en cours", Toast.LENGTH_SHORT).show();

                    CameraSource.ShutterCallback shutterCallback = new CameraSource.ShutterCallback() {
                        @Override
                        public void onShutter() {
                        }
                    };

                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message mesg) {
                            throw new RuntimeException();
                        }
                    };

                    CameraSource.PictureCallback pictureCallback = new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data) {
                            receivedData = data;
                            // Add the image
                            base64EncodedImage = new Image();
                            base64EncodedImage.encodeContent(receivedData);
                            mCameraSource.stop();
                            handler.sendMessage(handler.obtainMessage());
                        }
                    };

                    mCameraSource.takePicture(shutterCallback, pictureCallback);

                    try {
                        Looper.loop();
                    } catch (RuntimeException e) {
                    }


                    new AsyncTask<Object, Void, String>() {
                        @Override
                        protected String doInBackground(Object... params) {
                            try {
                                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                                VisionRequestInitializer requestInitializer =
                                        new VisionRequestInitializer(API_KEY) {

                                            // We override this so we can inject important identifying fields into the HTTP
                                            // headers. This enables use of a restricted cloud platform API key.

                                            @Override
                                            protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                                    throws IOException {
                                                super.initializeVisionRequest(visionRequest);

                                                String packageName = getPackageName();
                                                visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                                String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                                visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                            }
                                        };

                                Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                                builder.setVisionRequestInitializer(requestInitializer);

                                Vision vision = builder.build();

                                BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                                        new BatchAnnotateImagesRequest();
                                batchAnnotateImagesRequest.setRequests(new ArrayList<com.google.api.services.vision.v1.model.AnnotateImageRequest>() {{
                                    com.google.api.services.vision.v1.model.AnnotateImageRequest annotateImageRequest = new com.google.api.services.vision.v1.model.AnnotateImageRequest();


                                    annotateImageRequest.setImage(base64EncodedImage);

                                    // add the features we want
                                    annotateImageRequest.setFeatures(new ArrayList<com.google.api.services.vision.v1.model.Feature>() {{
                                        com.google.api.services.vision.v1.model.Feature labelDetection = new com.google.api.services.vision.v1.model.Feature();
                                        labelDetection.setType("TEXT_DETECTION");
                                        labelDetection.setMaxResults(10);
                                        add(labelDetection);
                                    }});

                                    // Add the list of one thing to the request
                                    add(annotateImageRequest);
                                }});

                                Vision.Images.Annotate annotateRequest =
                                        vision.images().annotate(batchAnnotateImagesRequest);
                                // Due to a bug: requests to Vision API containing large images fail when GZipped.
                                annotateRequest.setDisableGZipContent(true);
                                Log.d(TAG, "created Cloud Vision request object, sending request");

                                com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse response = annotateRequest.execute();
                                return convertResponseToString(response);

                            } catch (GoogleJsonResponseException e) {
                                Log.d(TAG, "failed to make API request because " + e.getContent());
                            } catch (IOException e) {
                                Log.d(TAG, "failed to make API request because of other IOException " +
                                        e.getMessage());
                            }
                            return "Cloud Vision API request failed. Check logs for details.";
                        }

                        protected void onPostExecute(String result) {
                            Alerte.afficherAlerte(BarcodeCaptureWithGoogleVisionSearchActivity.this, "Resultat", result, "alerte");
                            if (ActivityCompat.checkSelfPermission(BarcodeCaptureWithGoogleVisionSearchActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            try {
                                mCameraSource.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute();


                } else {
                    BarcodeCaptureWithGoogleVisionSearchActivity.super.onTap(getResources().getDisplayMetrics().widthPixels / 2, getResources().getDisplayMetrics().heightPixels / 2);
                }
                BarcodeCaptureWithGoogleVisionSearchActivity.super.onBackPressed();
            }
        });

        takePictureButton.setVisibility(View.VISIBLE);

    }

    private String convertResponseToString(com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<com.google.api.services.vision.v1.model.EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            for (com.google.api.services.vision.v1.model.EntityAnnotation label : labels) {
                message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }

        return message;
    }
}*/
