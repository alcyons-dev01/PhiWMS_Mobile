package fr.alcyons.phimr4.TestMail;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Outils.Mail;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class TestMailActivity extends ServiceActivity {

    EditText mailDestinataireET;
    EditText mailExpediteurET;
    EditText serveurSMTPET;
    EditText portSMTPET;
    EditText emetteurIdET;
    EditText emetteurPsswdET;
    ImageView ImageTestMailIV;

    String smtphost;
    String mailEmetteur;
    String psswordEmetteur;
    int smtp_port;
    String loginEmetteur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test_mail);

        //récupération objet graphique
        mailDestinataireET = (EditText) findViewById(R.id.mailDestinataire);
        mailExpediteurET = (EditText) findViewById(R.id.mailExpediteur);
        serveurSMTPET = (EditText) findViewById(R.id.serveurSMTP);
        portSMTPET = (EditText) findViewById(R.id.portSMTP);
        emetteurIdET = (EditText) findViewById(R.id.emetteurId);
        emetteurPsswdET= (EditText) findViewById(R.id.emetteurPsswd);
        ImageTestMailIV = (ImageView) findViewById(R.id.ImageTestMail);

        smtphost = ParametresServeurOpenHelper.getSMTPServeur(db);
        mailEmetteur = ParametresServeurOpenHelper.getMailEmetteur(db);
        psswordEmetteur = ParametresServeurOpenHelper.getMDPEmetteur(db);
        smtp_port = ParametresServeurOpenHelper.getSMTPPort(db);
        loginEmetteur = ParametresServeurOpenHelper.getMailEmetteur(db);

        if(utilisateurConnecte.getEtablissement().contentEquals("ADH"))
        {
            smtphost = "mail.adh-asso.net";
            mailEmetteur = "pharmacie@adh-asso.net";
            psswordEmetteur = "gbx55df1";
            loginEmetteur = "pharmaadh@adh-asso.local";

        }

        mailDestinataireET.setText("phir4.support@alcyons.fr");
        mailExpediteurET.setText(mailEmetteur);
        serveurSMTPET.setText(smtphost);
        portSMTPET.setText("25");
        emetteurIdET.setText(loginEmetteur);
        emetteurPsswdET.setText(psswordEmetteur);
    }

    @Override
    public void onResume() {
        super.onResume();


        ImageTestMailIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ParametresServeurOpenHelper.getMailPharmacie(db);
                email = "phir4.support@alcyons.fr";
                email = String.valueOf(mailDestinataireET.getText());

                if (email != null) {
                    new SendEmailTask().execute(email);
                    Toast.makeText(TestMailActivity.this,  "Mail Envoyé", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_preparation_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem valider = menu.findItem(R.id.boutonValider);
        valider.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
               String new_Mail_Emetteur = mailExpediteurET.getText().toString();
               String new_MDP_Emetteur = emetteurPsswdET.getText().toString();
               int new_SMTP_Port = Integer.parseInt(portSMTPET.getText().toString());
               String new_SMTP_Serveur = serveurSMTPET.getText().toString();
               ParametresServeurOpenHelper.updateParametreMailEnBDD(db, new_Mail_Emetteur, new_MDP_Emetteur, new_SMTP_Port, new_SMTP_Serveur);
               TestMailActivity.this.finish();
               return true;
            }
        });

        return true;
    }

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {
            Mail sender = new Mail(TestMailActivity.this, email[0], true, db);
            try {
                sender.sendMailVerification("Test mail", "Ceci est un test d'envoi de mail PhiMR4");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }
}
