package fr.alcyons.phiwms_mobile.Outils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by quentinlanusse on 10/08/2017.
 */

public class OutilsGestionPhotos {

    /*
    * https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android#answer-19339672
    * */

    // Il est possible d'enregistrer sous d'autres format (Gif, PNG) mais à priori JPEG est le plus rapide

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    // ExternalStorage
    public static void saveExternalStorageImageJPG(Context context, Bitmap bitmap, String name) {
        name = name + ".jpg";
        FileOutputStream fileOutputStream = null;
        try {
            verifyStoragePermissions((Activity) context);
            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveExternalStorageImageJPEG(Context context, Bitmap bitmap, String name) {
        name = name + ".jpeg";
        FileOutputStream fileOutputStream = null;
        try {
            verifyStoragePermissions((Activity) context);
            //fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + name);
            String root = context.getFilesDir().getAbsolutePath()+File.separator;
            File dir = new File(root + "Documents/");
            if(!dir.exists())
            {
                dir.mkdir();
            }
            fileOutputStream = new FileOutputStream(context.getFilesDir().getAbsolutePath()+File.separator + "Documents/" + name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteExternalStorageImage(Context context, String name, String extension) {
        name = name + "." + extension;
        verifyStoragePermissions((Activity) context);
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path + "/" + name);
        boolean delete = file.delete();

        return delete;
    }


    public static Bitmap loadExternalStorageImageBitmap(Context context, String name, String extension) {
        name = name + "." + extension;

        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;
        try {
            verifyStoragePermissions((Activity) context);
            String path = Environment.getExternalStorageDirectory().toString();
            File file = new File(path + "/" + name);
            fileInputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    // InternalStorage
    public static void saveInternalStorageImageJPG(Context context, Bitmap bitmap, String name) {

        String fileName = name + ".jpg";
        FileOutputStream fileOutputStream = null;

        File cheminAccesFile = new File(context.getFilesDir(), fileName);

        try {
            fileOutputStream = new FileOutputStream(cheminAccesFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteInternalStorageImage(Context context, String name, String extension) {
        String fileName = name + "." + extension;
        File fileToDelete = new File(context.getFilesDir(), fileName);
        boolean delete = fileToDelete.delete();
        return delete;
    }

    public static Bitmap loadInternalStorageImageBitmap(Context context, String name, String extension) {
        String fileName = name + "." + extension;
        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;

        try {
            File fileToLoad = new File(context.getFilesDir(), fileName);
            fileInputStream = new FileInputStream(fileToLoad);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
