package fr.alcyons.phiwms_mobile.Livraison;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPDF;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

/**
 * Created by olivier on 26/12/2017.
 */

public class SignatureLivraisonActivity extends ServiceActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    PH_Preparation ph_preparation_Selectionne;

    Context context;
    SignaturePad signaturePad;

    Button boutonEffacer;
    Button boutonValider;

    String filename;
    String signatureName;
    String subject;
    String body;

    // Fonction permettant de vérifier la permissions de stockage
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

    // Fonctions permettant au parent de nous transmettre des paramètres
    public void setParametres(PH_Preparation ph_preparation_Selectionne) {
        this.ph_preparation_Selectionne = ph_preparation_Selectionne;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signature_livraison);

        if (savedInstanceState != null) {
            ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, savedInstanceState.getInt("ph_preparationUID_Selectionne"));
        }

        signaturePad = (SignaturePad) findViewById(R.id.signaturePad);

        boutonEffacer = (Button) findViewById(R.id.boutonEffacer);
        boutonEffacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

        boutonValider = (Button) findViewById(R.id.boutonValider);
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Construction mail
                ph_preparation_Selectionne.setLivreur_userID(utilisateurConnecte.getId());
                Depot depot = DepotOpenHelper.getDepotParID(db, ph_preparation_Selectionne.getDepotDestinataireID());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date dateDuJour = new Date();
                String date = dateFormat.format(dateDuJour);

                subject = "phiwms_mobile - Livraisons N°" + ph_preparation_Selectionne.getUID() + " - " + date;
                body = "Madame, Monsieur, \n \n" +
                        "La livraison N°" + ph_preparation_Selectionne.getUID() + " à destination de " + depot.getNom() + " a été réalisée. \n" +
                        "Vous pourrez trouver ci-joint le bon de livraison signé. \n\n" +
                        "Cordialement, \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre";

                // Création du pdf
                dateFormat = new SimpleDateFormat("yyyyMMdd");
                dateDuJour = new Date();
                date = dateFormat.format(dateDuJour);

                filename = String.valueOf(ph_preparation_Selectionne.getUID()) + "_" + date + "_Livraison.pdf";
                signatureName = String.valueOf(ph_preparation_Selectionne.getUID()) + "_" + date + "_LivraisonSignature";

                //Sauvegarde de la signature dans une image
                verifyStoragePermissions(SignatureLivraisonActivity.this);
                String content = "";
                Bitmap bitmap = signaturePad.getSignatureBitmap();
                OutilsGestionPhotos.saveExternalStorageImageJPG(SignatureLivraisonActivity.this, bitmap, signatureName);

                try {
                    OutilsGestionPDF outilsGestionPDF = new OutilsGestionPDF(true);
                    outilsGestionPDF.createLivraison(SignatureLivraisonActivity.this, filename, signatureName, db, ph_preparation_Selectionne);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                // Récupération Mail Pharmacie
                String email = ParametresServeurOpenHelper.getMailPharmacie(db);
                if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
                {
                    email = "dev01@alcyons.fr";
                }
                if (email != null) {
                    new SendEmailTask().execute(email);
                }

                // Demande à l'utilisateur si envoi de copie
                boolean confirmation = Alerte.afficherAlerte(SignatureLivraisonActivity.this, "Alerte", "Voulez-vous recevoir une copie du mail ?", "OuiNon");
                if (confirmation) {
                    String utilisateurEmail = ((MenuActivity) context).utilisateurConnecte.getMail();
                    if (utilisateurEmail != null && !utilisateurEmail.contentEquals("")) {
                        new SendEmailTask().execute(utilisateurEmail);
                    }
                }

                mettreAJourPhPreparation(ph_preparation_Selectionne);
                Toast.makeText(SignatureLivraisonActivity.this, "Livraison effectuée", Toast.LENGTH_SHORT).show();
                SignatureLivraisonActivity.this.finish();
                return;
            }
        });

    }

    public void mettreAJourPhPreparation(PH_Preparation ph_preparation) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateDuJour = new Date();
        String date = dateFormat.format(dateDuJour);

        ph_preparation.setLivree(true);
        ph_preparation.setLivraisonDate(date);
        if (ph_preparation.getStatut().contains("en")) {
            ph_preparation.setStatut("Délivrée en partie");
        } else {
            ph_preparation.setStatut("Délivrée");
        }

        PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation);

        // Ajout du PH_Preparation au ElementASynchroniser
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation.getPhiMR4UUID(), ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

        // Tentative de lancer la sychronisation
        if (OutilsGestionConnexionReseau.isServerAccessible(SignatureLivraisonActivity.this)) {
            ElementASynchroniserOpenHelper.toutSynchroniser(SignatureLivraisonActivity.this, db, utilisateurConnecte, true);
        }
    }

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(SignatureLivraisonActivity.this, email[0], true, db);
            try {
                // Envoi du mail avec pdf
                sender.sendMail(subject, body, filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }
}

