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
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jessica on 11/01/2018.
 */

public class OutilsGestionStockage {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean externalStorage;

    public OutilsGestionStockage(Boolean externalStorage) {
        this.externalStorage = externalStorage;
    }

    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void saveBitmap(Context context, Bitmap bitmap, String fileName, String extension) {
        fileName += "." + extension;

        FileOutputStream fileOutputStream = null;
        File cheminAccesFile;

        try {
            if (externalStorage) {
                verifyStoragePermissions((Activity) context);
                cheminAccesFile = new File(Environment.getExternalStorageDirectory(), fileName);
            } else {
                cheminAccesFile = new File(context.getFilesDir(), fileName);
            }

            fileOutputStream = new FileOutputStream(cheminAccesFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteFile(Context context, String fileName, String extension) {
        fileName += "." + extension;
        File fileToDelete;

        if (externalStorage) {
            verifyStoragePermissions((Activity) context);
            fileToDelete = new File(Environment.getExternalStorageDirectory(), fileName);
        } else {
            fileToDelete = new File(context.getFilesDir(), fileName);
        }

        return fileToDelete.delete();
    }

    public Bitmap loadBitmap(Context context, String fileName, String extension) {
        fileName += "." + extension;

        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;
        File fileToLoad;

        try {
            if (externalStorage) {
                verifyStoragePermissions((Activity) context);
                fileToLoad = new File(Environment.getExternalStorageDirectory(), fileName);
            } else {
                fileToLoad = new File(context.getFilesDir(), fileName);
            }

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
