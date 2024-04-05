package fr.alcyons.phiwms_mobile.OutilsSerialisation;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.SurveillanceReference;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.ServiceActivity;

/**
 * Created by olivier on 26/02/2019.
 */

public class EnvoyerMailSurveillance extends ServiceActivity {

    public static String subject_mail;
    public static String body_mail;
    public static String filename = "";

    public void EnvoyerMailSerialisation(int id_surveillance, String email, SQLiteDatabase db)
    {
        SurveillanceReference surveillanceReference_courante = SurveillanceReferenceOpenHelper.getPH_SerialisationByid(db, id_surveillance);
        Produit produit_courant = ProduitOpenHelper.getProduitByID(db, surveillanceReference_courante.getProduitID());

        subject_mail = "PhiR4 - Surveillance Référence";

        body_mail = "Madame, Monsieur, \n \n" +
                "Suite à l'action menée, une surveillance a été déclenchée.\n\n" +
                "Motif de la surveillance : "+surveillanceReference_courante.getMotif() +"\n\n" +
                "Le produit concerné est le suivant : \n - Désignation du produit : "+produit_courant.getDesignation_ext()+"\n" +
                "- Numéro de lot : "+surveillanceReference_courante.getProduitLot()+"\n" +
                "- Date de péremption : "+surveillanceReference_courante.getProduitDatePéremption()+"\n" +
                "- Numéro de série : "+surveillanceReference_courante.getProduitNumeroSerie()+"\n\n" +
                "Cordialement \n\n" +
                "L'équipe Alcyons \n\n" +
                "Ceci est un message automatique merci de ne pas répondre";

        if (email != null) {
            new SendEmailTask().execute(email);
        }
    }

    public void EnvoyerMailMultipleSurveillance(List<Integer> liste_id_surveillance, String email, final SQLiteDatabase db)
    {
        subject_mail = "PhiR4 - Récapitulatif Surveillance Référence";
        body_mail = "Madame, Monsieur, \n \n" +
                "Suite à à votre demande, voici le détail des "+ liste_id_surveillance.size() +" surveillances référence sélectionnées.\n\n";

        final List<SurveillanceReference> liste_surveillance = new ArrayList<>();
        for(Integer id_surveillance : liste_id_surveillance)
        {
            SurveillanceReference surveillanceReference_courante = SurveillanceReferenceOpenHelper.getPH_SerialisationByid(db, id_surveillance);
            liste_surveillance.add(surveillanceReference_courante);
        }


        File folder = new File(Environment.getExternalStorageDirectory()+"/"+"Folder");

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(calendar.getTime());

        filename ="/"+date+"Récapitulatif_Surveillance.csv";

        final String filePath = folder.toString()+filename;


        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };

        new Thread() {
            public void run() {
                try {

                    FileWriter fw = new FileWriter(filePath);

                    fw.append("Motif");
                    fw.append(',');

                    fw.append("Désignation produit");
                    fw.append(',');

                    fw.append("Numéro de lot");
                    fw.append(',');

                    fw.append("Date de péremption");
                    fw.append(',');

                    fw.append("Numéro de série");
                    fw.append(',');

                    fw.append("Date de la surveillance");
                    fw.append(',');

                    fw.append("Statut");

                    fw.append('\n');

                    for(SurveillanceReference surveillance_courant : liste_surveillance)
                    {
                        Produit produit_courant = ProduitOpenHelper.getProduitByID(db, surveillance_courant.getProduitID());
                        String designation_externe = "";

                        if(produit_courant != null)
                        {
                            designation_externe = produit_courant.getDesignation_ext();
                        }

                        //suppression des potentiels virgules dans le nom d'un produit
                        designation_externe = designation_externe.replaceAll(",", " ");

                        fw.append(surveillance_courant.getMotif());
                        fw.append(',');

                        fw.append(designation_externe);
                        fw.append(',');

                        fw.append(surveillance_courant.getProduitLot());
                        fw.append(',');

                        fw.append(surveillance_courant.getProduitDatePéremption());
                        fw.append(',');

                        fw.append(surveillance_courant.getProduitNumeroSerie());
                        fw.append(',');

                        fw.append(surveillance_courant.getSurveillanceDate());
                        fw.append(',');

                        fw.append(surveillance_courant.getStatut());

                        fw.append('\n');
                    }

                    fw.flush();
                    fw.close();

                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
                //handler.sendEmptyMessage(0);
            }
        }.start();


        if (email != null) {
            new SendEmailTask().execute(email);
        }
    }

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(EnvoyerMailSurveillance.this, email[0], true, db);
            try {
                // Envoi du mail avec pdf
                if(!filename.contentEquals(""))
                    sender.sendMail(subject_mail, body_mail, filename);
                else
                    sender.sendMailVerification(subject_mail, body_mail);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }
}
