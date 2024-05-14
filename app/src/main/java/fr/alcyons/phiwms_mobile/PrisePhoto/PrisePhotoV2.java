package fr.alcyons.phiwms_mobile.PrisePhoto;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.OriginalActivity;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class PrisePhotoV2  extends ServiceActivity {

    Button button_validationPhoto;
    ImageView vue1;
    ImageView vue1Plus;
    ImageView vue2;
    ImageView vue2Plus;
    ImageView vue3;
    ImageView vue3Plus;
    ImageView vue4;
    ImageView vue4Plus;
    TextView designationproduit;
    TextView Referenceproduit;
    TextView textVue1;
    TextView textVue2;
    TextView textVue3;
    TextView textVue4;
    LinearLayout layoutSuppressionVue1;
    LinearLayout layoutSuppressionVue2;
    LinearLayout layoutSuppressionVue3;
    LinearLayout layoutSuppressionVue4;

    Bitmap bitmap1;
    Bitmap bitmap2;
    Bitmap bitmap3;
    Bitmap bitmap4;

    int vueCourante;
    String text_vue;
    boolean modeSuppression;
    Produit produitCourant;
    List<Bitmap> listASupprimer;

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prise_photo_v2);

        this.modeSuppression = false;
        this.listASupprimer = new ArrayList<>();
        this.text_vue = "";

        this.vue1 = (ImageView) this.findViewById(R.id.vue1);
        this.vue1Plus = (ImageView) this.findViewById(R.id.vue1Plus);
        this.vue2 = (ImageView) this.findViewById(R.id.vue2);
        this.vue2Plus = (ImageView) this.findViewById(R.id.vue2Plus);
        this.vue3 = (ImageView) this.findViewById(R.id.vue3);
        this.vue3Plus = (ImageView) this.findViewById(R.id.vue3Plus);
        this.vue4 = (ImageView) this.findViewById(R.id.vue4);
        this.vue4Plus = (ImageView) this.findViewById(R.id.vue4Plus);
        this.designationproduit = (TextView) this.findViewById(R.id.Designationproduit);
        this.Referenceproduit = (TextView) this.findViewById(R.id.Referenceproduit);
        this.textVue1 = (TextView) this.findViewById(R.id.textVue1);
        this.textVue2 = (TextView) this.findViewById(R.id.textVue2);
        this.textVue3 = (TextView) this.findViewById(R.id.textVue3);
        this.textVue4 = (TextView) this.findViewById(R.id.textVue4);
        this.button_validationPhoto = (Button) this.findViewById(R.id.button_validationPhoto);
        this.layoutSuppressionVue1 = (LinearLayout) this.findViewById(R.id.layoutSuppressionVue1);
        this.layoutSuppressionVue2 = (LinearLayout) this.findViewById(R.id.layoutSuppressionVue2);
        this.layoutSuppressionVue3 = (LinearLayout) this.findViewById(R.id.layoutSuppressionVue3);
        this.layoutSuppressionVue4 = (LinearLayout) this.findViewById(R.id.layoutSuppressionVue4);


        String produitDesignation = intent.getExtras().getString("nomProduit");
        int produitId = intent.getExtras().getInt("id_Produit");

        if(produitId != 0)
        {
            produitCourant = ProduitOpenHelper.getProduitByID(db, produitId);
            if(produitCourant != null)
            {
                this.designationproduit.setText(produitCourant.getDesignation_interne());
                this.Referenceproduit.setText(produitCourant.getRef_fourni());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.vue1Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!modeSuppression)
                {
                    vueCourante = 1;
                    text_vue = "Photo de 3/4";
                    captureImage();
                }
            }
        });

        this.vue2Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!modeSuppression)
                {
                    vueCourante = 2;
                    text_vue = "Photo du code barre ";
                    captureImage();
                }
            }
        });

        this.vue3Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!modeSuppression)
                {
                    vueCourante = 3;
                    text_vue = "Photo du dessus";
                    captureImage();
                }
            }
        });

        this.vue4Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!modeSuppression)
                {
                    vueCourante = 4;
                    text_vue = "Photo latérale";
                    captureImage();
                }
            }
        });

        this.vue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modeSuppression)
                {
                    vue1.setAlpha(1f);
                    layoutSuppressionVue1.setVisibility(View.GONE);
                    modeSuppression = false;
                }
                else
                {
                    vueCourante = 1;
                    text_vue = "Photo de 3/4";
                    captureImage();
                }
            }
        });

        this.vue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modeSuppression)
                {
                    vue2.setAlpha(1f);
                    layoutSuppressionVue2.setVisibility(View.GONE);
                    modeSuppression = false;
                }
                else
                {
                    vueCourante = 2;
                    text_vue = "Photo du code barre";
                    captureImage();
                }
            }
        });

        this.vue3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modeSuppression)
                {
                    vue3.setAlpha(1f);
                    layoutSuppressionVue3.setVisibility(View.GONE);
                    modeSuppression = false;
                }
                else
                {
                    vueCourante = 3;
                    text_vue = "Photo du dessus";
                    captureImage();
                }
            }
        });

        this.vue4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modeSuppression)
                {
                    vue4.setAlpha(1f);
                    layoutSuppressionVue4.setVisibility(View.GONE);
                    modeSuppression = false;
                }
                else
                {
                    vueCourante = 4;
                    text_vue = "Photo latérale";
                    captureImage();
                }
            }
        });

        //gestion du longClick sur une photo
        this.vue1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(bitmap1 != null)
                {
                    modeSuppression = true;
                    vue1.setAlpha(0.5f);
                    layoutSuppressionVue1.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        this.vue2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(bitmap2 != null)
                {
                    modeSuppression = true;
                    vue2.setAlpha(0.5f);
                    layoutSuppressionVue2.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        this.vue3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(bitmap3 != null)
                {
                    modeSuppression = true;
                    vue3.setAlpha(0.5f);
                    layoutSuppressionVue3.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        this.vue4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(bitmap4 != null)
                {
                    modeSuppression = true;
                    vue4.setAlpha(0.5f);
                    layoutSuppressionVue4.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        this.layoutSuppressionVue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap1 = null;
                vue1.setImageBitmap(null);
                vue1.setAlpha(1f);
                layoutSuppressionVue1.setVisibility(View.GONE);
                modeSuppression = false;
                gestionBoutonValidation();
            }
        });

        this.layoutSuppressionVue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap2 = null;
                vue2.setImageBitmap(null);
                vue2.setAlpha(1f);
                layoutSuppressionVue2.setVisibility(View.GONE);
                modeSuppression = false;
                gestionBoutonValidation();
            }
        });

        this.layoutSuppressionVue3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap3 = null;
                vue3.setImageBitmap(null);
                vue3.setAlpha(1f);
                layoutSuppressionVue3.setVisibility(View.GONE);
                modeSuppression = false;
                gestionBoutonValidation();
            }
        });

        this.layoutSuppressionVue4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap4 = null;
                vue4.setImageBitmap(null);
                vue4.setAlpha(1f);
                layoutSuppressionVue4.setVisibility(View.GONE);
                modeSuppression = false;
                gestionBoutonValidation();
            }
        });


        this.button_validationPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Nom de l'image
                List<Bitmap> arrayImage = new ArrayList<>();
                List<String> listUri = new ArrayList<>();
                List<String> listName = new ArrayList<>();
                if(bitmap1 != null)
                {
                    arrayImage.add(bitmap1);
                }

                if(bitmap2 != null)
                {
                    arrayImage.add(bitmap2);
                }

                if(bitmap3 != null)
                {
                    arrayImage.add(bitmap3);
                }

                if(bitmap4 != null)
                {
                    arrayImage.add(bitmap4);
                }

                String name = null;
                String nomProduit = intent.getStringExtra("nomProduit");
                if (nomProduit == null) {
                    nomProduit = intent.getStringExtra("nomDispositif");
                }
                nomProduit = nomProduit.replaceAll(" ", "");
                nomProduit = nomProduit.replaceAll("/", "-");


                // Sauvegarde de l'image
                String root = Environment.getExternalStorageDirectory().toString();
                root = getFilesDir().getAbsolutePath()+File.separator;

                for(int i = 0; i < arrayImage.size(); i++)
                {
                    name = nomProduit + "vue_"+String.valueOf(i+1)+".jpeg";
                    Bitmap pictureTaken = arrayImage.get(i);

                    File dir = null;

                    if (statutConnexion) {
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

                        Uri contentUri = Uri.fromFile(file);
                        listUri.add(contentUri.toString());
                        listName.add(name);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Bitmap pictureTaken = BitmapFactory.decodeByteArray(data, 0, data.length);
                // Fin de l'activité
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //mediaScanIntent.putExtra("photoProduit", contentUri.toString());
                mediaScanIntent.putExtra("photoProduit", (ArrayList) listUri);
                mediaScanIntent.putExtra("nomPhoto",(ArrayList) listName);
                getApplicationContext().sendBroadcast(mediaScanIntent);
                setResult(CodesEchangesActivites.RETOUR_PRISE_PHOTO, mediaScanIntent);
                PrisePhotoV2.this.finish();
            }
        });

    }

    void captureImage() {
        // Create an implicit intent, for image capture.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Toast toast = Toast.makeText(PrisePhotoV2.this, produitCourant.getDesignation_interne()+"\n"+text_vue, Toast.LENGTH_LONG);
        toast.show();
        this.startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
    }


    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_READ_WRITE_PERMISSION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (read/write).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
                }
                // Cancelled or denied.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    // When results returned
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {

                switch (vueCourante)
                {
                    case 1 :
                        bitmap1 = (Bitmap) data.getExtras().get("data");
                        this.vue1.setImageBitmap(bitmap1);
                        break;
                    case 2 :
                        bitmap2 = (Bitmap) data.getExtras().get("data");
                        this.vue2.setImageBitmap(bitmap2);
                        break;
                    case 3:
                        bitmap3 = (Bitmap) data.getExtras().get("data");
                        this.vue3.setImageBitmap(bitmap3);
                        break;
                    case 4:
                        bitmap4 = (Bitmap) data.getExtras().get("data");
                        this.vue4.setImageBitmap(bitmap4);
                        break;
                }

                gestionBoutonValidation();

            } else if (resultCode == RESULT_CANCELED) {
            } else {
                Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }
        }
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

    void gestionBoutonValidation()
    {
        if(bitmap1 != null || bitmap2 != null || bitmap3 != null || bitmap4 != null)
        {
            button_validationPhoto.setVisibility(View.VISIBLE);
        }
        else
        {
            button_validationPhoto.setVisibility(View.GONE);
        }
    }
}