package fr.alcyons.phiwms_mobile.Outils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import com.example.phiwms_mobile.DispositifAuLivret.DetailDispositifAuLivretActivity;
import com.example.phiwms_mobile.IdentificationParScan.DetailProduitIdentificationParScanActivity;
import com.example.phiwms_mobile.MedicamentAuLivret.DetailMedicamentAuLivretActivity;
import fr.alcyons.phiwms_mobile.OriginalActivity;
import com.example.phiwms_mobile.PAD.DetailPADActivity;
import com.example.phiwms_mobile.PAD.DetailPADComposantPrescriptionActivity;
import com.example.phiwms_mobile.PAD.DetailPADDotationActivity;

/**
 * Created by jessica on 08/11/2017.
 */

public class MedicalObjective {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String urlBase = "https://medicalobjective.alcyons.fr";
    private String urlSave = "/api/produits/phimr4/";
    private String urlGetImage = "/api/produits/phimr4/image/";
    private String urlGetURL = "/api/produits/phimr4/url/";
    private SQLiteDatabase db;
    private Utilisateur utilisateurConnecte;
    private Context context;
    private String depotReference;
    private Depot depotPUI;
    private Depot depotSelectionne;
    private Produit produit;
    private String produit_UID;
    private String produit_GTIN;
    private String produit_UCD;
    private String etablissement_UID;
    private String etablissement_PUI;
    private String utilisateur_UID;
    private String utilisateur_lat;
    private String utilisateur_lng;
    private String service;
    private String depot_Reference;
    private Bitmap b;
    private boolean externalStorage;

    public MedicalObjective(Context context, Utilisateur utilisateur, Depot depotPUI, Depot depotSelectionne, Produit produit, boolean externalStorage) {
        this.db = ((OriginalActivity) context).db;
        this.context = context;
        this.utilisateurConnecte = utilisateur;
        this.depotPUI = depotPUI;
        this.depotSelectionne = depotSelectionne;
        this.produit = produit;
        this.externalStorage = externalStorage;
    }

    public void saveUtiliser(String filename, String image_id) {

        String path;

        if (externalStorage) {
            verifyStoragePermissions((Activity) context);
            path = Environment.getExternalStorageDirectory().toString();
        } else {
            path = context.getFilesDir().toString();
        }

        String url = urlBase + urlSave + image_id;

        etablissement_UID = String.valueOf(depotPUI.getEtablissement_UID());
        etablissement_PUI = depotPUI.getDepot_Reference();
        if(produit != null){
            produit_UID = String.valueOf(produit.getID_produit());
            produit_GTIN = produit.getGTIN();
            produit_UCD = produit.getUCD_Code();
        }

        utilisateur_UID = String.valueOf(utilisateurConnecte.getId());
        utilisateur_lat = String.valueOf(utilisateurConnecte.getLatitude());
        utilisateur_lng = String.valueOf(utilisateurConnecte.getLongitude());
        service = "Utiliser";
        depot_Reference = String.valueOf(depotSelectionne.getDepot_UID());

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };

        //Récupérer l'image dans un fichier
        final File f = new File(path + "/" + filename + ".jpg");
        b = BitmapFactory.decodeFile(path + "/" + filename + ".jpg");

        int targetWidth = 1280;
        if(b != null)
        {
            double sourceWidth = b.getWidth();
            double sourceHeight = b.getHeight();
            double aspectRatio = sourceHeight / sourceWidth;
            double targetHeight = (targetWidth * aspectRatio);

            b = b.createScaledBitmap(b, targetWidth, (int) targetHeight, false);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            b.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);


            byte[] byteArray = byteArrayOutputStream.toByteArray();

            try {
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(byteArray);
                fos.flush();
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            HashMap<String, String> headers = new HashMap<>();
            headers.put("etablissementUID", etablissement_UID);
            headers.put("etablissementPUI", etablissement_PUI);
            headers.put("utilisateurUID", utilisateur_UID);
            headers.put("utilisateurLat", utilisateur_lat);
            headers.put("utilisateurLng", utilisateur_lng);
            headers.put("produitUID", produit_UID);
            headers.put("produitGTIN", produit_GTIN);
            headers.put("produitUCD", produit_UCD);
            headers.put("service", service);
            headers.put("depotReference", depot_Reference);

            MultipartRequest multipartRequest = new MultipartRequest(url, headers, f, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    f.delete();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            int socketTimeout = 30000;//30 seconds - change to what you want

            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            multipartRequest.setRetryPolicy(policy);

            final RequestQueue requestQueue = Volley.newRequestQueue(context, hurlStack);
            requestQueue.add(multipartRequest);
        }
    }

    public void savePicture(Bitmap bitmap, String image_id, String serviceName, boolean alcyons) {
        String path;

        if (externalStorage) {
            verifyStoragePermissions((Activity) context);
            path = Environment.getExternalStorageDirectory().toString();
        } else {
            path = context.getFilesDir().toString();
        }

        String url = urlBase + urlSave + image_id;

        if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS") || alcyons){
            etablissement_UID = "0";
            etablissement_PUI = "ALCYONS";
            produit_UID = produit.getGTIN() + "_" + produit.getUCD_Code();
        }
        else{
            etablissement_UID = String.valueOf(depotPUI.getEtablissement_UID());
            etablissement_PUI = depotPUI.getDepot_Reference();
            produit_UID = String.valueOf(produit.getID_produit());
        }

        utilisateur_UID = String.valueOf(utilisateurConnecte.getId());
        utilisateur_lat = String.valueOf(utilisateurConnecte.getLatitude());
        utilisateur_lng = String.valueOf(utilisateurConnecte.getLongitude());
        produit_GTIN = produit.getGTIN();
        produit_UCD = produit.getUCD_Code();
        service = serviceName;
        depot_Reference = String.valueOf(depotSelectionne.getDepot_UID());

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };

        //Récupérer l'image dans un fichier
        String filename = etablissement_UID + '_' + etablissement_PUI + '_' + produit_UID;

        final File f = new File(path + "/" + filename + ".jpg");

        int targetWidth = 1280;
        double sourceWidth = bitmap.getWidth();
        double sourceHeight = bitmap.getHeight();
        double aspectRatio = sourceHeight / sourceWidth;
        double targetHeight = (targetWidth * aspectRatio);

        b = bitmap.createScaledBitmap(bitmap, targetWidth, (int) targetHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        b.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);


        byte[] byteArray = byteArrayOutputStream.toByteArray();

        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(byteArray);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, String> headers = new HashMap<>();
        headers.put("etablissementUID", etablissement_UID);
        headers.put("etablissementPUI", etablissement_PUI);
        headers.put("utilisateurUID", utilisateur_UID);
        headers.put("utilisateurLat", utilisateur_lat);
        headers.put("utilisateurLng", utilisateur_lng);
        headers.put("produitUID", produit_UID);
        headers.put("produitGTIN", produit_GTIN);
        headers.put("produitUCD", produit_UCD);
        headers.put("service", service);
        headers.put("depotReference", depot_Reference);

        MultipartRequest multipartRequest = new MultipartRequest(url, headers, f, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Toast toast = Toast.makeText(context, "Succès de l'enregistrement !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                f.delete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(context, "Échec de l'enregistrement !\r\n" + error.toString(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        int socketTimeout = 30000;//30 seconds - change to what you want

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        multipartRequest.setRetryPolicy(policy);

        final RequestQueue requestQueue = Volley.newRequestQueue(context, hurlStack);
        requestQueue.add(multipartRequest);

        if (serviceName.equals("MedicamentAuLivret")) {
            ((DetailMedicamentAuLivretActivity) context).informationImportanteMedicament.photo.setImageBitmap(b);
            ((DetailMedicamentAuLivretActivity) context).informationImportanteMedicament.photo.setVisibility(View.VISIBLE);
        } else if (serviceName.equals("DispositifAuLivret")) {
            ((DetailDispositifAuLivretActivity) context).informationImportanteDispositif.photo.setImageBitmap(b);
            ((DetailDispositifAuLivretActivity) context).informationImportanteDispositif.photo.setVisibility(View.VISIBLE);
        } else if (serviceName.equals("PADComposantPrescription")) {
            ((DetailPADComposantPrescriptionActivity) context).photoImageView.setImageBitmap(b);
        }
        else if (serviceName.equals("PADDotation")) {
            ((DetailPADDotationActivity) context).photoImageView.setImageBitmap(b);
        }
    }

    public void getPictureImage(String serviceName) {
        String url = urlBase + urlGetImage;
        //Utilisation d'un boolean pour la gestion de la connexion et la récupération des photos en local
        final Boolean[] connexion = new Boolean[1];
        connexion[0] = false;
        etablissement_UID = String.valueOf(depotPUI.getEtablissement_UID());
        etablissement_PUI = depotPUI.getDepot_Reference();
        utilisateur_UID = String.valueOf(utilisateurConnecte.getId());
        utilisateur_lat = String.valueOf(utilisateurConnecte.getLatitude());
        utilisateur_lng = String.valueOf(utilisateurConnecte.getLongitude());
        produit_UID = String.valueOf(produit.getID_produit());
        produit_GTIN = produit.getGTIN();
        produit_UCD = produit.getUCD_Code();
        service = serviceName;
        depot_Reference = String.valueOf(depotSelectionne.getDepot_UID());

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                connexion[0] = true;
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                    httpsURLConnection.setRequestMethod("GET");
                    httpsURLConnection.setRequestProperty("etablissementUID", etablissement_UID);
                    httpsURLConnection.setRequestProperty("etablissementPUI", etablissement_PUI);
                    httpsURLConnection.setRequestProperty("produitUID", produit_UID);
                    httpsURLConnection.setRequestProperty("produitGTIN", produit_GTIN);
                    httpsURLConnection.setRequestProperty("produitUCD", produit_UCD);
                    httpsURLConnection.setRequestProperty("service", service);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };

        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                String activityName = context.getClass().getSimpleName();
                if (activityName.contentEquals("DetailMedicamentAuLivretActivity")) {
                    ((DetailMedicamentAuLivretActivity) context).informationImportanteMedicament.photo.setImageBitmap(response);
                    ((DetailMedicamentAuLivretActivity) context).informationImportanteMedicament.photo.setVisibility(View.VISIBLE);
                }
                if (activityName.contentEquals("DetailDispositifAuLivretActivity")) {
                    ((DetailDispositifAuLivretActivity) context).informationImportanteDispositif.photo.setImageBitmap(response);
                    ((DetailDispositifAuLivretActivity) context).informationImportanteDispositif.photo.setVisibility(View.VISIBLE);
                }
                if(activityName.contentEquals("DetailPADComposantPrescriptionActivity")){
                    ((DetailPADComposantPrescriptionActivity) context).photoImageView.setImageBitmap(response);
                }
                if(activityName.contentEquals("DetailPADDotationActivity")){
                    ((DetailPADDotationActivity) context).photoImageView.setImageBitmap(response);
                }
                if(activityName.contentEquals("DetailPADActivity")){
                    ((DetailPADActivity) context).photoImageView.setImageBitmap(response);
                }
                if(activityName.contentEquals("DetailProduitIdentificationParScanActivity")){
                    ((DetailProduitIdentificationParScanActivity) context).photo.setImageBitmap(response);
                    ((DetailProduitIdentificationParScanActivity) context).photo.setVisibility(View.VISIBLE);
                }
            }
        }, 0, 0, null, null);

        String activityName = context.getClass().getSimpleName();
        //Récupération des photos en local si on est pas connecté à internet
        if (activityName.contentEquals("DetailMedicamentAuLivretActivity")) {
            if (!connexion[0]) {
                String root;

                if (externalStorage) {
                    verifyStoragePermissions((Activity) context);
                    root = Environment.getExternalStorageDirectory().toString();
                } else {
                    root = context.getFilesDir().toString();
                }
                String nomProduit = produit.getDesignation_ext();
                nomProduit = nomProduit.replaceAll(" ", "");
                nomProduit = nomProduit.replaceAll("/", "-");
                String name = nomProduit + ".jpeg";
                File dir = new File(root + "/DCIM/PhotoASynchroniser/" + name);
                FileInputStream in;
                BufferedInputStream buf;
                try {
                    in = new FileInputStream(dir);
                    buf = new BufferedInputStream(in);
                    byte[] bMapArray = new byte[buf.available()];
                    buf.read(bMapArray);
                    Bitmap bMap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
                    ((DetailMedicamentAuLivretActivity) context).informationImportanteMedicament.photo.setImageBitmap(bMap);
                    ((DetailMedicamentAuLivretActivity) context).informationImportanteMedicament.photo.setVisibility(View.VISIBLE);
                    in.close();
                    buf.close();
                } catch (Exception e) {
                    Log.e("Error reading file", e.toString());
                }
            }
        }

        final RequestQueue requestQueue = Volley.newRequestQueue(context, hurlStack);
        requestQueue.add(ir);
    }

    private boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean isPermission = true;
        if (permission != PackageManager.PERMISSION_GRANTED) {
            isPermission = false;
        }
        return isPermission;
    }

    /*
    * Nécessaire pour effectuer une requete HTTPS
    * */
    // Let's assume your server app is hosting inside a server machine
    // which has a server certificate in which "Issued to" is "localhost",for example.
    // Then, inside verify method you can verify "localhost".
    // If not, you can temporarily return true
    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("*.alcyons.fr", session);
            }
        };
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        AssetManager assetManager = context.getAssets();
        InputStream caInput = null;
        try {
            caInput = assetManager.open("cert.pem");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Certificate ca = cf.generateCertificate(caInput);
        if (caInput != null) {
            caInput.close();
        }

        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }
}
