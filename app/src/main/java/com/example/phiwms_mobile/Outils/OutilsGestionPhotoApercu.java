package com.example.phiwms_mobile.Outils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import com.example.phiwms_mobile.Classes.ActionUtilisateur;
import com.example.phiwms_mobile.PrisePhoto.PrisePhoto;
import com.example.phiwms_mobile.R;
import com.example.phiwms_mobile.ServiceActivity;

/**
 * Created by olivier on 24/04/2019.
 */

public class OutilsGestionPhotoApercu extends ServiceActivity implements View.OnTouchListener{

    Bitmap photo_courante;
    ImageView imageView;
    ImageView retourPhoto;
    ImageView rotationPhoto;
    ImageView rognerPhoto;
    ImageView validerPhoto;
    File dir;
    boolean apercu;
    String lienPhoto;

    //variable pour gérer le ZOOM
    private static final String TAG = "Touch";

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    boolean zoomActif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_apercu_photo);

        //init de la méthode pour le zoom
        imageView = (ImageView) findViewById(R.id.photo);
        zoomActif = false;

        retourPhoto = (ImageView) findViewById(R.id.retourAppareilPhoto);
        rotationPhoto = (ImageView) findViewById(R.id.rotationPhoto);
        rognerPhoto = (ImageView) findViewById(R.id.rognerPhoto);
        validerPhoto = (ImageView) findViewById(R.id.validerPhoto);

        lienPhoto = intent.getStringExtra("image");
        apercu = intent.getBooleanExtra("apercu", false);

        try {
            photo_courante = MediaStore.Images.Media.getBitmap(OutilsGestionPhotoApercu.this.getContentResolver(), Uri.parse(lienPhoto));
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(photo_courante);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // make the image fit to the center.
        imageView.setOnTouchListener(this);

        if(!apercu)
        {
            retourPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailReceptionPui_Intent = new Intent(OutilsGestionPhotoApercu.this, PrisePhoto.class);
                    Bundle detailReceptionPui_Bundle = OutilsGestionPhotoApercu.super.getBundle();
                    // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                    detailReceptionPui_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    detailReceptionPui_Bundle.putString("contexte", "photoAction");
                    detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);
                    OutilsGestionPhotoApercu.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
                }
            });


            rotationPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Matrix matrix = new Matrix();

                    matrix.postRotate(90);

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(photo_courante, photo_courante.getWidth(), photo_courante.getHeight(), true);

                    photo_courante = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                    imageView.setImageBitmap(photo_courante);
                }
            });

            rognerPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            validerPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File filetodelete = new File(lienPhoto);
                    if(filetodelete.exists())
                    {
                        if(filetodelete.delete())
                        {
                            Log.e("ImageStatut : ", "Supprimer");
                        }
                        else
                        {
                            Log.e("ImageStatut : ", "Non Supprimer");
                        }
                    }
                    else
                    {
                        Log.e("ImageStatut : ", "Non trouvé");
                    }

                    photo_courante = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    String root = getFilesDir().getAbsolutePath()+File.separator;
                    dir = new File(root + "PhotosAction/");
                    boolean cree = dir.mkdir();
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
                    Date date = new Date();
                    String new_date = dateFormat.format(date);
                    String name = "Action"+new_date;
                    File file = new File(dir, name);
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        //appelle la fonction qui gère la rotation
                        photo_courante = getRotatedBitmap(dir + "/" + name, photo_courante);

                        photo_courante.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        Toast.makeText(OutilsGestionPhotoApercu.this, "Image sauvegardée", Toast.LENGTH_SHORT).show();
                        out.flush();
                        out.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(photo_courante != null && !lienPhoto.contentEquals(""))
                    {
                        ActionUtilisateur action = new ActionUtilisateur(2, utilisateurConnecte.getId(), "2019-05-24 15:00:48", 1405, Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En cours", 21536, lienPhoto, "Réception");
                        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, action);
                    }
                    OutilsGestionPhotoApercu.this.finish();
                }
            });
        }
        else
        {
            validerPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photo_courante = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                    String root = getFilesDir().getAbsolutePath()+File.separator;
                    dir = new File(root + "PhotosAction/");
                    String[] tab_name = lienPhoto.split("/");
                    String name = tab_name[tab_name.length-1];
                    File file = new File(dir, name);
                    if(file.exists())
                        file.delete();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        //appelle la fonction qui gère la rotation
                        photo_courante = getRotatedBitmap(dir + "/" + name, photo_courante);

                        photo_courante.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        Toast.makeText(OutilsGestionPhotoApercu.this, "Image sauvegardée", Toast.LENGTH_SHORT).show();
                        out.flush();
                        out.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent retourLienIntent = new Intent();
                    Bundle retourLienBundle = new Bundle();
                    Uri contentUri = Uri.fromFile(file);
                    retourLienBundle.putString("LienPhoto",  contentUri.toString());
                    retourLienIntent.putExtras(retourLienBundle);
                    setResult(CodesEchangesActivites.RETOUR_PRISE_PHOTO, retourLienIntent);
                    OutilsGestionPhotoApercu.this.finish();
                }
            });

            rognerPhoto.setVisibility(View.GONE);

            rotationPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Matrix matrix = new Matrix();

                    matrix.postRotate(90);

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(photo_courante, photo_courante.getWidth(), photo_courante.getHeight(), true);

                    photo_courante = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                    imageView.setImageBitmap(photo_courante);
                }
            });

            retourPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

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

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        // make the image scalable as a matrix
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: //first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG" );
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP: //first finger lifted
            case MotionEvent.ACTION_POINTER_UP: //second finger lifted
                mode = NONE;
                Log.d(TAG, "mode=NONE" );
                break;
            case MotionEvent.ACTION_POINTER_DOWN: //second finger down
                oldDist = spacing(event); // calculates the distance between two points where user touched.
                Log.d(TAG, "oldDist=" + oldDist);
                // minimal distance between both the fingers
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event); // sets the mid-point of the straight line between two points where user touched.
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM" );
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG)
                { //movement of first finger
                    matrix.set(savedMatrix);
                    if (view.getLeft() >= -392 && zoomActif)
                    {
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    }
                }
                else if (mode == ZOOM) { //pinch zooming
                    zoomActif = true;
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist/oldDist; //thinking I need to play around with this value to limit it**
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        // Perform the transformation
        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}
