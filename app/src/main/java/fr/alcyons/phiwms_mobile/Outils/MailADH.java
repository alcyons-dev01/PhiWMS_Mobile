package fr.alcyons.phiwms_mobile.Outils;

import android.content.Context;
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
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailADH extends javax.mail.Authenticator {

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

    //TODO PASSER EN PARAMETRE ADRESSE EMETEUR
    private String mailhost = "mail.adh-asso.net";
    /*private String user = "phir4@alcyons.fr";
    private String password = "65ken64btz";*/
    // Adresse mail d'envoi
    private String recipients;
    private Session session;
    private String cc;

    private Context context;
    private boolean externalStorage;

    public MailADH(Context context, String recipients, boolean externalStorage) {
        this.context = context;
        this.recipients = recipients;
        this.externalStorage = externalStorage;

       /* Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.host", mailhost);
        properties.put("mail.smtp.ssl.enable", "false");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(properties, null);*/

        Properties props = System.getProperties();

        props.put("mail.smtp.host", mailhost);
        session = Session.getDefaultInstance(props, null);
    }

    public MailADH(Context context, String recipients, String cc, boolean externalStorage) {
        this.context = context;
        this.recipients = recipients;
        this.externalStorage = externalStorage;
        this.cc = cc;

       /* Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtps");
        properties.setProperty("mail.host", mailhost);
        properties.put("mail.smtp.ssl.enable", "false");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.quitwait", "false");*/
        Properties props = System.getProperties();

        props.put("mail.smtp.host", mailhost);
        session = Session.getDefaultInstance(props, null);
    }

  /*  protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }*/

    public synchronized void sendMail(String subject, String body, String filename) throws Exception {
        try {
            MimeMessage message = new MimeMessage(session);
            /*TODO PASSE EN PARAMETRE ADRESSE EMETEUR*/
            message.setFrom(new InternetAddress("alcyons@adh-asso.net"));
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

        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    public synchronized void sendMailVerification(String subject, String body) throws Exception {
        try {
            MimeMessage message = new MimeMessage(session);
            //TODO PASSER EN PARAMETRE ADRESSE EMETEUR
            //message.setFrom(new InternetAddress("pharmacie@adh-asso.net"));
            message.setFrom(new InternetAddress("service.pharmacie@adh-asso.net"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            message.setSubject(subject);

            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(body);


            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);

            message.setContent(multipart);

            Transport.send(message);

        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
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