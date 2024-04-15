package fr.alcyons.phiwms_mobile.Livraison;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.DocumentException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ListeLivraisonDepotAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Dialogue;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPDF;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ListeLivraisonDepot  extends ServiceAvecConnexionActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Context context;
    List<PH_Preparation> ph_preparation_List;
    ListView ph_preparation_ListView;
    ListeLivraisonDepotAdapter ph_preparation_livraisonAdapter;
    PackageManager pm;

    boolean connexionDirecte;

    List<String> listeDepotLivraison;
    Dialogue dialogue;
    String signatureNameChauffeur;
    String filename;
    Bitmap photoLivraisonBitmap;
    String photoLivraisonPhotoName;
    String subject;
    String body;
    Dialog alertePatientezDialog;

    String depotReference;
    String dateLivraison;

    Depot depot;
    
    FloatingActionMenu floatingMenu;
    FloatingActionButton boutonSignatureLivreur;
    FloatingActionButton boutonPatientAbsent;
    FloatingActionButton boutonToutRefuser;
    ActionUtilisateur new_action_utilisateur;

    public View.OnClickListener clicboutonSignerLivreur = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingMenu.close(true);
            afficherAlerteValidationAllLivraison(ListeLivraisonDepot.this, ListeLivraisonDepot.this.getLayoutInflater());
        }
    };

    public View.OnClickListener clicboutonToutRefuser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingMenu.close(true);
            for(PH_Preparation ph_preparation_courant : ph_preparation_List)
            {
                List<PH_Preparation_Ligne> liste_ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation_courant);
                for(PH_Preparation_Ligne ph_preparation_ligne : liste_ph_preparation_ligne)
                {
                    ph_preparation_ligne.setAccepter(false);
                    ph_preparation_ligne.setQte_livrer(0);
                    gestionnairePH_Preparation_Ligne.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                }
            }
            AfficherSignature();
        }
    };

    public View.OnClickListener clicboutonPatientAbsent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingMenu.close(true);
            boolean confirmer = Alerte.afficherAlerte(ListeLivraisonDepot.this, "Confirmer", "Confirmez-vous que le patient est absent ?", "OuiNon");
            if(confirmer)
            {
                for(PH_Preparation ph_preparation_Selectionne : ph_preparation_List) {
                    ph_preparation_Selectionne.setStatut("Refuser");
                    ph_preparation_Selectionne.setMotif("Patient absent");
                    List<PH_Preparation_Ligne> liste_ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);

                    for (PH_Preparation_Ligne preparation_ligne : liste_ph_preparation_ligne) {
                        preparation_ligne.setQte_livrer(0);
                        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, preparation_ligne);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, preparation_ligne.getPhiMR4UUID(), preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                    }
                    PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation_Selectionne);

                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation_Selectionne.getPhiMR4UUID(), ph_preparation_Selectionne.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

                    // Tentative de lancer la sychronisation
                    if (OutilsGestionConnexionReseau.isServerAccessible(ListeLivraisonDepot.this)) {
                        ElementASynchroniserOpenHelper.toutSynchroniser(ListeLivraisonDepot.this, db, utilisateurConnecte, true);
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                    Date dateDuJour = new Date();
                    String date = dateFormat.format(dateDuJour);
                    dateFormat = new SimpleDateFormat("yyyyMMdd");
                    dateDuJour = new Date();
                    date = dateFormat.format(dateDuJour);

                    subject = "phiwms_mobile - " + depot.getNom() + " - Livraisons N°" + ph_preparation_Selectionne.getUID() + " Refusé - " + date;
                    /**TODO : envoyer un mail*/
                    body = "Madame, Monsieur, \n \n" +
                            "La livraison N°" + ph_preparation_Selectionne.getUID() + " à destination de " + depot.getNom() + " a été refusé car le patient est absent. \n" +
                            "Ceci est un message automatique merci de ne pas répondre\n\n";

                    // Récupération Mail Pharmacie
                    String email = ParametresServeurOpenHelper.getMailPharmacie(db);
                    if (utilisateurConnecte.getEtablissement().contentEquals("ADH")) {
                        email = "livraison.pui@adh-asso.net";
                    }
                    if (utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS")) {
                        email = "dev01@alcyons.fr";
                    }

                    if (email != null) {
                        new SendEmailTask().execute(email);
                        onBackPressed();
                    }
                }
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_livraison_v2);
        context = ListeLivraisonDepot.this;
        floatingMenu = (FloatingActionMenu) findViewById(R.id.floatingMenu);
        boutonSignatureLivreur = (FloatingActionButton) findViewById(R.id.boutonSignatureLivreur);
        boutonPatientAbsent = (FloatingActionButton) findViewById(R.id.boutonPatientAbsent);
        boutonToutRefuser = (FloatingActionButton) findViewById(R.id.boutonToutRefuser);

        boutonSignatureLivreur.setOnClickListener(clicboutonSignerLivreur);

        boutonPatientAbsent.setOnClickListener(clicboutonPatientAbsent);

        boutonToutRefuser.setOnClickListener(clicboutonToutRefuser);

        depotReference = intent.getStringExtra("depotRef");
        dateLivraison = intent.getStringExtra("dateLivraison");
        ph_preparation_List = PH_PreparationOpenHelper.getAllLivraisonByDepotAndDate(db, depotReference, dateLivraison);
        depot = DepotOpenHelper.getDepotParReference(db, depotReference);

        pm = ListeLivraisonDepot.this.getPackageManager();
        listeDepotLivraison = new ArrayList<>();
        listeDepotLivraison.add("Tous");
        //Gestion de la listView
        ph_preparation_ListView = (ListView) findViewById(R.id.listeView);
        ph_preparation_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PH_Preparation ph_preparation_Selectionne = (PH_Preparation) ph_preparation_livraisonAdapter.getItem(position);
                Intent serviceLivraison_Intent = new Intent(ListeLivraisonDepot.this, InformationLivraisonActivity.class);
                Bundle serviceLivraison_Bundle = ListeLivraisonDepot.super.getBundle();
                serviceLivraison_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
                serviceLivraison_Intent.putExtras(serviceLivraison_Bundle);
                ListeLivraisonDepot.this.startActivity(serviceLivraison_Intent);
            }
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        passageParOnCreate = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        ph_preparation_livraisonAdapter = new ListeLivraisonDepotAdapter(ListeLivraisonDepot.this, ph_preparation_List);

        ph_preparation_ListView.setDivider(footer);
        ph_preparation_ListView.setAdapter(ph_preparation_livraisonAdapter);

        int taille_liste = ph_preparation_List.size();
        /* Code nécessaire à l'affichage de la liste */
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(taille_liste));
        if(depot != null)
            ((TextView) findViewById(R.id.titre)).setText(depot.getNom());
        else
            ((TextView) findViewById(R.id.titre)).setText("");

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                lancerScan();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);
        return true;
    }

    public void toutValider()
    {
        afficherAlerteValidationAllLivraison(ListeLivraisonDepot.this, ListeLivraisonDepot.this.getLayoutInflater());
    }

    public void afficherAlerteValidationAllLivraison(Context context, LayoutInflater inflater)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_validation, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView textDialog = (TextView) layout.findViewById(R.id.messageFin);
        textDialog.setText("Souhaitez-validez toutes les livraisons ?");
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AfficherSignature();
                alertDialog.dismiss();
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void AfficherSignature()
    {
        dialogue = new Dialogue(ListeLivraisonDepot.this, clicValidationSignatureAllLivraison, utilisateurConnecte);
        dialogue.signaturePadOpen(true);
    }

    public View.OnClickListener clicValidationSignatureAllLivraison = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Création du pdf
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date dateDuJour = new Date();
            String date = dateFormat.format(dateDuJour);
            dateFormat = new SimpleDateFormat("yyyyMMdd");
            dateDuJour = new Date();
            date = dateFormat.format(dateDuJour);

            for(PH_Preparation ph_preparation_courant : ph_preparation_List)
            {
                filename = String.valueOf(ph_preparation_courant.getUID()) + "_" + date + "_Livraison.pdf";
                signatureNameChauffeur = String.valueOf(ph_preparation_courant.getUID()) + "_" + date + "_LivraisonSignature";

                //Sauvegarde de la signature dans une image
                verifyStoragePermissions(ListeLivraisonDepot.this);
                String content = "";
                Bitmap bitmap = dialogue.signaturePad.getSignatureBitmap();
                OutilsGestionPhotos.saveExternalStorageImageJPEG(ListeLivraisonDepot.this, bitmap, signatureNameChauffeur);

                if(bitmap != null)
                {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    String img_str = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    ph_preparation_courant.setSignature_Livraison(img_str);
                }
            }

            dialogue.dialog.dismiss();

            dialogue = new Dialogue(ListeLivraisonDepot.this, onClickListenerValiderAllLivraison, utilisateurConnecte);
            dialogue.padCommentairePhotoLivraison();
        }
    };

    View.OnClickListener onClickListenerValiderAllLivraison = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            alertePatientezDialog = new Dialog(ListeLivraisonDepot.this);
            alertePatientezDialog.setContentView(R.layout.alerte_patientez);
            alertePatientezDialog.setCancelable(false);
            WindowManager.LayoutParams layoutParams = alertePatientezDialog.getWindow().getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            if (!alertePatientezDialog.isShowing()) {
                alertePatientezDialog.show();
            }
            //Récupération du commentaire
            String commentaireSaisie = dialogue.commentaireEditText.getText().toString();
            for(PH_Preparation ph_preparation_Selectionne : ph_preparation_List) {
                if (!commentaireSaisie.contentEquals("")) {
                    ph_preparation_Selectionne.setCommentaires(commentaireSaisie);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dateDuJour = new Date();
                String date = dateFormat.format(dateDuJour);
                ph_preparation_Selectionne.setLivraisonDate(date);
                ph_preparation_Selectionne.setLivree(true);
                PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation_Selectionne);

                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation_Selectionne.getPhiMR4UUID(), ph_preparation_Selectionne.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

                // Tentative de lancer la sychronisation
                if (OutilsGestionConnexionReseau.isServerAccessible(ListeLivraisonDepot.this)) {
                    ElementASynchroniserOpenHelper.toutSynchroniser(ListeLivraisonDepot.this, db, utilisateurConnecte, true);
                }

                //Création de l'action utilisateur
                Random random = new Random();
                int actionId = random.nextInt();
                if (actionId > 0)
                    actionId = actionId * -1;
                SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateDestruction = new Date();
                String date_string = parseFormat.format(dateDestruction);
                new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", ph_preparation_Selectionne.getUID(), "", "Livraison");
                ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
                //fin de la création de l'action utilisateur

                List<PH_Preparation_Ligne> ph_preparation_ligne_List = PH_Preparation_LigneOpenHelper.getALivrerPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);
                //mise à jour des PH_Preparations Ligne dans la BDD
                for (PH_Preparation_Ligne preparation_ligne_courant : ph_preparation_ligne_List) {
                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, preparation_ligne_courant);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, preparation_ligne_courant.getPhiMR4UUID(), preparation_ligne_courant.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

                    //gestion des actions lignes
                    Random randomactionligne = new Random();
                    int actionligneId = randomactionligne.nextInt();
                    if (actionligneId > 0)
                        actionligneId = actionligneId * -1;

                    ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Ph_Preparation_Ligne", preparation_ligne_courant.get_UID(), "", 0, (int) preparation_ligne_courant.getQte_livrer(), preparation_ligne_courant.getProduitDesignation());
                    ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                }

                /**
                 * TODO : une boucle PDF
                 * Répertoire partage sur le serveur, envoyer le PDF dessus
                 * Envoyer le mail depuis le serveur
                 */
                //Construction mail
                Depot depot = DepotOpenHelper.getDepotParID(db, ph_preparation_Selectionne.getDepotDestinataireID());
                String commentaire = ph_preparation_Selectionne.getCommentaires();
                String depotNom = "";

                if(depot != null)
                    depotNom = depot.getNom();
                if (commentaire == null || commentaire.contentEquals("")) {
                    commentaire = "Pas de commentaire saisie";
                }


                subject = "phiwms_mobile - " + depotNom + " - Livraisons N°" + ph_preparation_Selectionne.getUID() + " - " + date;

                //Sauvegarde de la signature dans une image
                if (photoLivraisonBitmap != null) {

                    dateFormat = new SimpleDateFormat("yyyyMMdd");
                    dateDuJour = new Date();
                    date = dateFormat.format(dateDuJour);

                    photoLivraisonPhotoName = String.valueOf(ph_preparation_Selectionne.getUID()) + "_" + date + "_LivraisonPhoto";

                    verifyStoragePermissions(ListeLivraisonDepot.this);
                }

                String preparer_par = "";
                String valider_par = "";
                String livrer_par = "";
                Utilisateur userLivreur = UtilisateurOpenHelper.getUtilisateurByID(db, ph_preparation_Selectionne.getLivreur_userID());
                if (userLivreur != null) {
                    livrer_par = userLivreur.getNom() + " " + userLivreur.getPrenom();
                }
                if (ph_preparation_Selectionne.getPreparateur() != null) {
                    String[] tab_preparateur = ph_preparation_Selectionne.getPreparateur().split("\\(");
                    preparer_par = tab_preparateur[0];
                    String[] tab_valider_par = tab_preparateur[1].split("\\)");
                    valider_par = tab_valider_par[0];
                }

                //Verifier le livré par
                body = "Madame, Monsieur, \n \n" +
                        "La livraison N°" + ph_preparation_Selectionne.getUID() + " à destination de " + depotNom + " a été réalisée. \n" +
                        "Préparé par " + preparer_par + "\n" +
                        "Validé par " + valider_par + "\n" +
                        "Livré par " + livrer_par + "\n" +
                        "Vous pourrez trouver ci-joint le bon de livraison signé. \n" +
                        "Commentaire : " + commentaire + "\n\n" +
                        "Ceci est un message automatique merci de ne pas répondre\n\n";

                try {
                    OutilsGestionPDF outilsGestionPDF = new OutilsGestionPDF(true);
                    outilsGestionPDF.createLivraisonV2(ListeLivraisonDepot.this, filename, signatureNameChauffeur, db, ph_preparation_Selectionne);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                // Récupération Mail Pharmacie
                String email = ParametresServeurOpenHelper.getMailPharmacie(db);
                if (utilisateurConnecte.getEtablissement().contentEquals("ADH")) {
                    email = "livraison.pui@adh-asso.net";
                }
                if (utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS")) {
                    email = "dev01@alcyons.fr";
                }

                if (email != null) {
                    new SendEmailTask().execute(email);
                }

                dialogue.dialog.dismiss();
                ph_preparation_Selectionne.setLivreur_userID(utilisateurConnecte.getId());
                mettreAJourPhPreparation(ph_preparation_Selectionne);
                Toast.makeText(ListeLivraisonDepot.this, "Livraison effectuée", Toast.LENGTH_SHORT).show();
            }
            alertePatientezDialog.dismiss();

            onBackPressed();
        }
    };

    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(ListeLivraisonDepot.this, email[0], true, db);
            try {
                if(filename == null || filename.contentEquals(""))
                {
                    sender.sendMailVerification(subject, body);
                }
                else if(photoLivraisonPhotoName == null || photoLivraisonPhotoName.contentEquals(""))
                {
                    sender.sendMail(subject, body, "Documents/"+filename);
                }
                else
                {
                    sender.sendMailPDFAndPhoto(subject, body, "Documents/"+filename, "Documents/"+photoLivraisonPhotoName + ".jpeg");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
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
        if (OutilsGestionConnexionReseau.isServerAccessible(ListeLivraisonDepot.this)) {
            ElementASynchroniserOpenHelper.toutSynchroniser(ListeLivraisonDepot.this, db, utilisateurConnecte, true);
        }
    }

    public void lancerScan()
    {
        Bundle scanDocumentBundle = ListeLivraisonDepot.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent = null;
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ListeLivraisonDepot.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                scanDocumentIntent = new Intent(ListeLivraisonDepot.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ListeLivraisonDepot.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        ListeLivraisonDepot.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_DOCUMENT: {
                if (data != null) {
                    String code_recu = data.getExtras().getString("code");
                    if (code_recu != null) {
                        String code ="";
                        if(code_recu.startsWith("DDS"))
                        {
                            code = code_recu.substring(3);
                        }
                        int idPreparation = 0;
                        try {
                            idPreparation = Integer.parseInt(code);
                        } catch (NumberFormatException e) {
                            idPreparation = 0;
                        }
                        PH_Preparation ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, idPreparation);
                        if(ph_preparation_Selectionne != null)
                        {
                            Intent serviceLivraison_Intent = new Intent(ListeLivraisonDepot.this, ListeLivraisonDepot.class);
                            Bundle serviceLivraison_Bundle = ListeLivraisonDepot.super.getBundle();
                            serviceLivraison_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
                            serviceLivraison_Intent.putExtras(serviceLivraison_Bundle);
                            ListeLivraisonDepot.this.startActivity(serviceLivraison_Intent);
                            ListeLivraisonDepot.this.finish();

                            invalidateOptionsMenu();
                        }
                        else
                        {
                            if(code_recu.startsWith("DDS"))
                            {
                                afficherSnackBarLivraison();
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    public void afficherSnackBarLivraison() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);;

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }

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
    public void onBackPressed() {
        super.onBackPressed();
        Intent serviceLivraison_Intent = new Intent(ListeLivraisonDepot.this, ListePointDeLivraison.class);
        Bundle serviceLivraison_Bundle = ListeLivraisonDepot.super.getBundle();
        serviceLivraison_Intent.putExtras(serviceLivraison_Bundle);
        ListeLivraisonDepot.this.startActivity(serviceLivraison_Intent);
        ListeLivraisonDepot.this.finish();
    }
}