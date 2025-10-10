package fr.alcyons.phiwms_mobile.Outils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;

public class Mail extends javax.mail.Authenticator {

    // A ajouter dans l'activité où l'on souhaite envoyer un mail
        /*new SendEmailTask().execute();
        private class SendEmailTask extends AsyncTask<Object, Object, Object> {
            @Override
            protected Object doInBackground(Object... arg0) {
                Mail sender = new Mail("admin@alcyons.fr");
                try {
                    String filename = "example.pdf"; // Fichier à envoyer en pièce jointe
                    String subject = "This is Subject"; // Sujet du mail
                    String body = "This is Body"; // Corps du mail

                    sender.sendMail(subject, body, filename);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "executed";
            }
        }*/

    static {
        Security.addProvider(new JSSEProvider());
    }

    // Paramètres SMTP
   /* private String mailhost = "smtp.alcyons.fr";
    private String user = "phir4@alcyons.fr";
    private String password = "65ken64btz";*/
    private String smtphost;
    private String mailEmetteur;
    private String psswordEmetteur;
    private String loginEmetteur;
    private int smtp_port;
    // Adresse mail d'envoi
    private String recipients;
    private Session session;
    private String cc;

    private Context context;
    private boolean externalStorage;
    private SQLiteDatabase db;

    public Mail(Context context, String recipients, boolean externalStorage, SQLiteDatabase db, Utilisateur utilisateur) {
        this.context = context;
        this.recipients = recipients;
        this.externalStorage = externalStorage;

        this.db = db;
        this.smtphost = ParametresServeurOpenHelper.getSMTPServeur(db);
        this.mailEmetteur = ParametresServeurOpenHelper.getMailEmetteur(db);
        this.psswordEmetteur = ParametresServeurOpenHelper.getMDPEmetteur(db);
        this.smtp_port = ParametresServeurOpenHelper.getSMTPPort(db);
        this.loginEmetteur = ParametresServeurOpenHelper.getLoginEmetteur(db);
        Properties properties = new Properties();
        if(smtphost != null)
        {
            properties.setProperty("mail.host", smtphost);
        }

        if(utilisateur != null && utilisateur.getEtablissement().contentEquals("ADH"))
        {
            smtphost = "mail.adh-asso.net";
            mailEmetteur = "pharmacie@adh-asso.net";
            psswordEmetteur = "gbx55df1";
            loginEmetteur = "pharmaadh@adh-asso.local";
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "25");
            properties.put("mail.smtp.socketFactory.port", "25");
            session = Session.getDefaultInstance(properties, this);
        }
        else if(utilisateur.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
        {
            smtphost = "217.70.178.3";
            loginEmetteur = "noreply@phiwms.com";
            mailEmetteur = "noreply@phiwms.com";
            psswordEmetteur = "PACT!automata5zoom";
            /*properties.setProperty("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "25");
            properties.put("mail.smtp.socketFactory.port", "25");*/
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");
            properties.setProperty("mail.smtp.quitwait", "false");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.socketFactory.port", "465");
            session = Session.getDefaultInstance(properties, this);
        }
        else
        {
//            properties.setProperty("mail.transport.protocol", "smtps");
//            properties.put("mail.smtp.auth", "true");
//            properties.put("mail.smtp.ssl.enable", "true");
//            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            properties.put("mail.smtp.socketFactory.fallback", "false");
//            properties.setProperty("mail.smtp.quitwait", "false");
//            properties.put("mail.smtp.starttls.enable", "true");
//            properties.put("mail.smtp.port", "465");
//            properties.put("mail.smtp.socketFactory.port", "465");
//            session = Session.getDefaultInstance(properties, this);
            smtphost = "217.70.178.3";
            loginEmetteur = "noreply@phiwms.com";
            mailEmetteur = "noreply@phiwms.com";
            psswordEmetteur = "PACT!automata5zoom";
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");
            properties.setProperty("mail.smtp.quitwait", "false");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.socketFactory.port", "465");
            session = Session.getDefaultInstance(properties, this);
        }
    }

    public Mail(Context context, String recipients, String cc, boolean externalStorage, SQLiteDatabase db, Utilisateur utilisateur) {
        this.context = context;
        this.recipients = recipients;
        this.externalStorage = externalStorage;
        this.cc = cc;

        this.db = db;
        this.smtphost = ParametresServeurOpenHelper.getSMTPServeur(db);
        this.mailEmetteur = ParametresServeurOpenHelper.getMailEmetteur(db);
        this.psswordEmetteur = ParametresServeurOpenHelper.getMDPEmetteur(db);
        this.loginEmetteur = ParametresServeurOpenHelper.getLoginEmetteur(db);

        Properties properties = new Properties();
        properties.setProperty("mail.host", smtphost);

        if(utilisateur != null && utilisateur.getEtablissement().contentEquals("ADH"))
        {
            smtphost = "mail.adh-asso.net";
            mailEmetteur = "pharmacie@adh-asso.net";
            psswordEmetteur = "gbx55df1";
            loginEmetteur = "pharmaadh@adh-asso.local";
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "25");
            properties.put("mail.smtp.socketFactory.port", "25");
            session = Session.getDefaultInstance(properties, this);
        }
        else if(utilisateur.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
        {
            smtphost = "217.70.178.3";
            loginEmetteur = "noreply@phiwms.com";
            mailEmetteur = "noreply@phiwms.com";
            psswordEmetteur = "PACT!automata5zoom";
            /*properties.setProperty("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", "25");
            properties.put("mail.smtp.socketFactory.port", "25");*/
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");
            properties.setProperty("mail.smtp.quitwait", "false");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.socketFactory.port", "465");
            session = Session.getDefaultInstance(properties, this);
        }
        else
        {
//            properties.setProperty("mail.transport.protocol", "smtps");
//            properties.put("mail.smtp.auth", "true");
//            properties.put("mail.smtp.ssl.enable", "true");
//            properties.put("mail.smtp.starttls.enable", "true");
//            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            properties.put("mail.smtp.socketFactory.fallback", "false");
//            properties.setProperty("mail.smtp.quitwait", "false");
//            properties.put("mail.smtp.port", "465");
//            properties.put("mail.smtp.socketFactory.port", "465");
//            session = Session.getDefaultInstance(properties, this);

            smtphost = "217.70.178.3";
            loginEmetteur = "noreply@phiwms.com";
            mailEmetteur = "noreply@phiwms.com";
            psswordEmetteur = "PACT!automata5zoom";
            properties.setProperty("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");
            properties.setProperty("mail.smtp.quitwait", "false");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.socketFactory.port", "465");
            session = Session.getDefaultInstance(properties, this);
        }
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(loginEmetteur, psswordEmetteur);
    }

    public synchronized void sendMail(String subject, String body, String filename) throws Exception {
        try {
            MimeMessage message = new MimeMessage(session);
            if(mailEmetteur.contentEquals(""))
            {
                String adresseEmetrice = ParametresServeurOpenHelper.getMailPharmacie(db);
                message.setFrom(new InternetAddress(adresseEmetrice));
            }
            else
            {
                message.setFrom(new InternetAddress(mailEmetteur));
            }
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            if(cc!=null && !cc.contentEquals(""))
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
            message.setSubject(subject);

            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(body);

            MimeBodyPart messageBodyPart2 = new MimeBodyPart();
            if (!filename.contentEquals("")) {

                String path;
                if (externalStorage) {
                   // path = Environment.getExternalStorageDirectory().toString();
                    path = context.getFilesDir().getAbsolutePath();
                } else {
                    path = context.getFilesDir().toString();
                }

                String fileDataSource = path + "/" + filename;
                DataSource source = new FileDataSource(fileDataSource);
                messageBodyPart2.setDataHandler(new DataHandler(source));
                messageBodyPart2.setFileName(filename);
            }

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);

            message.setContent(multipart);

            Transport.send(message);

        }
        catch (AuthenticationFailedException e) {
            Log.e("EmailError", "Échec de l'authentification: " + e.getMessage());
        } catch (SendFailedException e) {
            Log.e("EmailError", "Échec de l'envoi: " + e.getMessage());
        } catch (MessagingException e) {
            Log.e("EmailError", "Erreur de messagerie: " + e.getMessage());
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    public synchronized void sendMailPDFAndPhoto(String subject, String body, String filename, String photo) throws Exception {
        try {
            MimeMessage message = new MimeMessage(session);
            if(mailEmetteur.contentEquals(""))
            {
                String adresseEmetrice = ParametresServeurOpenHelper.getMailPharmacie(db);
                message.setFrom(new InternetAddress(adresseEmetrice));
            }
            else
            {
                message.setFrom(new InternetAddress(mailEmetteur));
            }
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            if(cc!=null && !cc.contentEquals(""))
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
            message.setSubject(subject);

            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(body);

            MimeBodyPart messageBodyPart2 = new MimeBodyPart();
            if (!filename.contentEquals("")) {

                String path;
                if (externalStorage) {
                    // path = Environment.getExternalStorageDirectory().toString();
                    path = context.getFilesDir().getAbsolutePath();
                } else {
                    path = context.getFilesDir().toString();
                }

                String fileDataSource = path + "/" + filename;
                DataSource source = new FileDataSource(fileDataSource);
                messageBodyPart2.setDataHandler(new DataHandler(source));
                messageBodyPart2.setFileName(filename);
            }
            MimeBodyPart messageBodyPart3 = new MimeBodyPart();

            if (!photo.contentEquals("")) {

                String path;
                if (externalStorage) {
                    path = context.getFilesDir().getAbsolutePath();
                } else {
                    path = context.getFilesDir().toString();
                }

                String fileDataSource = path + "/" + photo;
                DataSource source = new FileDataSource(fileDataSource);
                messageBodyPart3.setDataHandler(new DataHandler(source));
                messageBodyPart3.setFileName(photo);
            }


            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);
            multipart.addBodyPart(messageBodyPart3);

            message.setContent(multipart);

            Transport.send(message);

        }
        catch (AddressException e) {
            throw new Error("bad address");
        }
        catch (MessagingException e){
            throw new Error("bad message data");
        }
        catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    public synchronized void sendMailVerification(String subject, String body) throws Exception {
        try {
            MimeMessage message = new MimeMessage(session);
            if(mailEmetteur.contentEquals(""))
            {
                String adresseEmetrice = ParametresServeurOpenHelper.getMailPharmacie(db);
                message.setFrom(new InternetAddress(adresseEmetrice));
            }
            else
            {
                message.setFrom(new InternetAddress(mailEmetteur));
            }
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            message.setSubject(subject);

            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(body);


            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);

            message.setContent(multipart);

            Transport.send(message);

        }
        catch (AddressException e) {
            throw new Error("bad address");
        }
        catch (MessagingException e){
            Log.e("MessagingException", e.getMessage(), e);
        }
        catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            Alerte.afficherAlerte(context, "Erreur", e.getMessage(), "alerte");
        }
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}