package fr.alcyons.phiwms_mobile.Livraison;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Preparation_Ligne_LivraisonAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Dialogue;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPDF;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

/**
 * Created by olivier on 26/12/2017.
 */

public class ListeDetailsLivraisonActivity extends ServiceActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    PH_Preparation ph_preparation_Selectionne;
    Context context;
    ListView ph_preparationLigne_ListView;
    List<PH_Preparation_Ligne> ph_preparation_ligne_List;
    List<PH_Preparation_Ligne> tempPh_preparation_ligne_List;
    PH_Preparation_Ligne_LivraisonAdapter ph_preparation_ligne_livraisonAdapter;
    String filename;
    String signatureName;
    String subject;
    String signatureChauffeur;
    String body;
    List<String> liste_produit_refuser;
    ActionUtilisateur new_action_utilisateur;
    Dialogue dialogue;
    View.OnClickListener onClickListenerValider = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Création de l'action utilisateur
            Random random = new Random();
            int actionId = random.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateDestruction =new Date();
            String date_string = parseFormat.format(dateDestruction);
            new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", ph_preparation_Selectionne.getUID(), "", "Livraison");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            //fin de la création de l'action utilisateur


            //mise à jour des PH_Preparations Ligne dans la BDD
            for(PH_Preparation_Ligne preparation_ligne_courant : ph_preparation_ligne_List)
            {
                if(preparation_ligne_courant.getQte_APreparer() != preparation_ligne_courant.getQte_livrer())
                {
                    liste_produit_refuser.add(preparation_ligne_courant.getProduitDesignation()+" -> Quantité à préparer : "+preparation_ligne_courant.getQte_APreparer()+" - Quantité livrée : "+preparation_ligne_courant.getQte_livrer());
                }

                PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, preparation_ligne_courant);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, preparation_ligne_courant.getPhiMR4UUID(), preparation_ligne_courant.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

                //gestion des actions lignes
                Random randomactionligne = new Random();
                int actionligneId = randomactionligne.nextInt();
                if(actionligneId > 0)
                    actionligneId= actionligneId*-1;

                ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Ph_Preparation_Ligne", preparation_ligne_courant.get_UID(), "", 0, (int)preparation_ligne_courant.getQte_livrer(), preparation_ligne_courant.getProduitDesignation());
                ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
            }


            //Construction mail
            Depot depot = DepotOpenHelper.getDepotParID(db, ph_preparation_Selectionne.getDepotDestinataireID());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date dateDuJour = new Date();
            String date = dateFormat.format(dateDuJour);
            String commentaire = ph_preparation_Selectionne.getCommentaires();
            if(commentaire == null || commentaire.contentEquals(""))
            {
                commentaire = "Pas de commentaire saisie";
            }

            subject = "phiwms_mobile - Livraisons N°" + ph_preparation_Selectionne.getUID() + " - " + date;
            if(liste_produit_refuser.size() == 0)
            {
                body = "Madame, Monsieur, \n \n" +
                        "La livraison N°" + ph_preparation_Selectionne.getUID() + " à destination de " + depot.getNom() + " a été réalisée. \n" +
                        "Préparé par "+ph_preparation_Selectionne.getPreparateur()+"\n"+
                        "Vous pourrez trouver ci-joint le bon de livraison signé. \n" +
                        "Commentaire : "+commentaire+"\n\n"+
                        "Cordialement, \n\n" +
                        "L'équipe Alcyons \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre";
            }
            else
            {
                String text_produit_refuser = "";
                for(String refus_courant : liste_produit_refuser)
                {
                    text_produit_refuser = text_produit_refuser+refus_courant+"\n";
                }
                body = "Madame, Monsieur, \n \n" +
                        "La livraison N°" + ph_preparation_Selectionne.getUID() + " à destination de " + depot.getNom() + " a été réalisée. \n" +
                        "Préparé par "+ph_preparation_Selectionne.getPreparateur()+"\n"+
                        "Les produits suivant n'ont pas étaient livrés ou sont livrés en partie : \n\n"+text_produit_refuser+"\n"+
                        "Vous pourrez trouver ci-joint le bon de livraison signé. \n" +
                        "Commentaire : "+commentaire+"\n\n"+
                        "Cordialement, \n\n" +
                        "L'équipe Alcyons \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre";
            }


            // Création du pdf
            dateFormat = new SimpleDateFormat("yyyyMMdd");
            dateDuJour = new Date();
            date = dateFormat.format(dateDuJour);

            filename = String.valueOf(ph_preparation_Selectionne.getUID()) + "_" + date + "_Livraison.pdf";
            signatureName = String.valueOf(ph_preparation_Selectionne.getUID()) + "_" + date + "_LivraisonSignature";

            //Sauvegarde de la signature dans une image
            verifyStoragePermissions(ListeDetailsLivraisonActivity.this);
            String content = "";
            Bitmap bitmap = dialogue.signaturePad.getSignatureBitmap();
            OutilsGestionPhotos.saveExternalStorageImageJPEG(ListeDetailsLivraisonActivity.this, bitmap, signatureName);

            if(bitmap != null)
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String img_str = Base64.encodeToString(byteArray, Base64.DEFAULT);
                ph_preparation_Selectionne.setSignature_Livraison(img_str);
            }


            try {
                OutilsGestionPDF outilsGestionPDF = new OutilsGestionPDF(true);
                outilsGestionPDF.createLivraisonV2(ListeDetailsLivraisonActivity.this, filename, signatureName, db, ph_preparation_Selectionne);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            // Récupération Mail Pharmacie
            String email = ParametresServeurOpenHelper.getMailPharmacie(db);
            if(utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS"))
            {
                email = "dev01@alcyons.fr";
            }

            if (email != null) {
                new SendEmailTask().execute(email);
            }

            dialogue.dialog.dismiss();
            ph_preparation_Selectionne.setLivreur_userID(utilisateurConnecte.getId());
            mettreAJourPhPreparation(ph_preparation_Selectionne);
            Toast.makeText(ListeDetailsLivraisonActivity.this, "Livraison effectuée", Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putBoolean("EtatLivraison", true);
            resultIntent.putExtras(extras);
            setResult(CodesEchangesActivites.RESULT_SIGNATURE, resultIntent);
            ListeDetailsLivraisonActivity.this.finish();
            return;
        }
    };
    public View.OnClickListener clicboutonSigner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogue = new Dialogue(ListeDetailsLivraisonActivity.this, onClickListenerValider, utilisateurConnecte);
            dialogue.signaturePadOpen(true);
        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_details_livraison);

        ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, intent.getIntExtra("ph_preparationUID_Selectionne", 0));

        //initialisation des listes
        liste_produit_refuser = new ArrayList<>();

        // Transformation de la date au format yyyy-MM-dd à dd/MM/yyyy
        String dateLivraison = "";
        Date dateLivraisonPrevue = null;

        DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            dateLivraisonPrevue = dateDecodeur.parse(ph_preparation_Selectionne.getLivraisonPrevueDate());

            if (dateLivraisonPrevue != null) {
                dateLivraison = dateFormat.format(dateLivraisonPrevue);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Affichage des informations de base
        ((TextView) findViewById(R.id.uidPHPreparation)).setText("N°" + String.valueOf(ph_preparation_Selectionne.getUID()));
        ((TextView) findViewById(R.id.livraisonPrevueDate)).setText(dateLivraison);

        // Récupération des ph_preparation_lignes
        tempPh_preparation_ligne_List = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);

        ph_preparation_ligne_List = new ArrayList<>();

        for (PH_Preparation_Ligne ph_preparation_ligne : tempPh_preparation_ligne_List) {
            if (ph_preparation_ligne.getQte_livrer() > 0) {
                ph_preparation_ligne_List.add(ph_preparation_ligne);
            }
        }

        Collections.sort(ph_preparation_ligne_List, new Comparator<PH_Preparation_Ligne>() {
            @Override
            public int compare(PH_Preparation_Ligne o1, PH_Preparation_Ligne o2) {
                return o1.getProduitDesignation().compareTo(o2.getProduitDesignation());
            }
        });

        findViewById(R.id.boutonSigner).setOnClickListener(clicboutonSigner);
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();


        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        ph_preparationLigne_ListView = (ListView) findViewById(R.id.listeView);
        ph_preparation_ligne_livraisonAdapter = new PH_Preparation_Ligne_LivraisonAdapter(ListeDetailsLivraisonActivity.this, ph_preparation_ligne_List);
        ph_preparationLigne_ListView.setDivider(footer);
        ph_preparationLigne_ListView.setAdapter(ph_preparation_ligne_livraisonAdapter);

        ph_preparationLigne_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ph_preparation_ligne_List.get(position).getAccepter()) {
                    ph_preparation_ligne_List.get(position).setAccepter(false);
                    gestionnairePH_Preparation_Ligne.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_List.get(position));
                    view.findViewById(R.id.Accepter).setVisibility(View.GONE);
                    view.findViewById(R.id.Refuser).setVisibility(View.VISIBLE);

                } else {
                    ph_preparation_ligne_List.get(position).setAccepter(true);
                    gestionnairePH_Preparation_Ligne.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_List.get(position));
                    view.findViewById(R.id.Accepter).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.Refuser).setVisibility(View.GONE);
                }
                ph_preparation_ligne_livraisonAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        PH_Preparation ph_preparation = ph_preparation_Selectionne;
        outstate.putInt("ph_preparationUID_Selectionne", ph_preparation.getUID());
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
        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

        // Tentative de lancer la sychronisation
        if (OutilsGestionConnexionReseau.isServerAccessible(ListeDetailsLivraisonActivity.this)) {
            ElementASynchroniserOpenHelper.toutSynchroniser(ListeDetailsLivraisonActivity.this, db, utilisateurConnecte, true);
        }
    }

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(ListeDetailsLivraisonActivity.this, email[0], true, db);
            try {
                // Envoi du mail avec pdf
                sender.sendMail(subject, body, "Documents/"+filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_commentaire, menu);
        menu.findItem(R.id.commentaire).setVisible(true);
        return true;
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.commentaire);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String commentaire = Alerte.afficherAlerteEditText(ListeDetailsLivraisonActivity.this, "Commentaire", "Saisir un commentaire sur le livraison");
                ph_preparation_Selectionne.setCommentaires(commentaire);
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListeDetailsLivraisonActivity.this.finish();
    }
}
