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

public class EnvoyerMailSurveillance extends ServiceActivity {

    public static String subject_mail;
    public static String body_mail;
    public static String filename = "";

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
