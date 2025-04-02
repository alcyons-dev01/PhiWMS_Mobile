package fr.alcyons.phiwms_mobile.Livraison;

import static com.google.android.gms.vision.L.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.itextpdf.text.DocumentException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Preparation_Ligne_LivraisonAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Dialogue;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPDF;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos;
import fr.alcyons.phiwms_mobile.PrisePhoto.PrisePhoto;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceLivraisonActivity;

import static fr.alcyons.phiwms_mobile.AuthentificationActivity.hasPermissions;

public class InformationLivraisonActivity extends ServiceActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    List<String> liste_produit_refuser;
    PH_Preparation ph_preparation_Selectionne;
    Dialogue dialogue;
    String filename;
    String signatureNameChauffeur;
    String subject;
    String body;

    // Boutons
    FloatingActionMenu floatingMenu;
    FloatingActionButton boutonSignatureLivreur;
    FloatingActionButton boutonPatientAbsent;
    FloatingActionButton boutonToutRefuser;

    Depot depot;
    String adresse;
    List<PH_Preparation_Ligne> ph_preparation_ligne_List;

    ListView ph_preparationLigne_ListView;
    PH_Preparation_Ligne_LivraisonAdapter ph_preparation_ligne_livraisonAdapter;
    List<PH_Preparation_Ligne> tempPh_preparation_ligne_List;

    //gestion de la photo
    String photoProduitsChemin;
    Bitmap photoLivraisonBitmap;
    String photoLivraisonPhotoName;
    ActionUtilisateur new_action_utilisateur;

    public View.OnClickListener clicValidationSignature = new View.OnClickListener() {
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onClick(View v) {

            // Création du pdf
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date dateDuJour = new Date();
            dateFormat.format(dateDuJour);
            String date;
            dateFormat = new SimpleDateFormat("yyyyMMdd");
            dateDuJour = new Date();
            date = dateFormat.format(dateDuJour);

            filename = ph_preparation_Selectionne.getUID() + "_" + date + "_Livraison.pdf";
            signatureNameChauffeur = ph_preparation_Selectionne.getUID() + "_" + date + "_LivraisonSignature";

            //Sauvegarde de la signature dans une image
            verifyStoragePermissions(InformationLivraisonActivity.this);
            Bitmap bitmap = dialogue.signaturePad.getSignatureBitmap();
            OutilsGestionPhotos.saveExternalStorageImageJPEG(InformationLivraisonActivity.this, bitmap, signatureNameChauffeur);

            if(bitmap != null)
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String img_str = Base64.encodeToString(byteArray, Base64.DEFAULT);
                ph_preparation_Selectionne.setSignature_Livraison(img_str);
            }

            dialogue.dialog.dismiss();

            dialogue = new Dialogue(InformationLivraisonActivity.this, onClickListenerValider, utilisateurConnecte);
            dialogue.padCommentairePhotoLivraison();
        }
    };

    View.OnClickListener onClickListenerValider = new View.OnClickListener() {
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onClick(View v) {

            //Récupération du commentaire
            String commentaireSaisie = dialogue.commentaireEditText.getText().toString();
            if(!commentaireSaisie.contentEquals(""))
            {
                ph_preparation_Selectionne.setCommentaires(commentaireSaisie);
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateDuJour = new Date();
            String date = dateFormat.format(dateDuJour);
            ph_preparation_Selectionne.setLivraisonDate(date);
            ph_preparation_Selectionne.setLivree(true);
            PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation_Selectionne);

            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation_Selectionne.getPhiMR4UUID(), ph_preparation_Selectionne.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

            // Tentative de lancer la sychronisation
            if (statutConnexion) {
                ElementASynchroniserOpenHelper.toutSynchroniser(InformationLivraisonActivity.this, db, utilisateurConnecte, true);
            }

            //Création de l'action utilisateur
            Random random = new Random();
            int actionId = random.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateDestruction =new Date();
            String date_string = parseFormat.format(dateDestruction);
            new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", ph_preparation_Selectionne.getUID(), "", "Livraison");
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

                ActionUtilisateur_Ligne actionUtilisateur_ligne = getActionUtilisateurLigne(preparation_ligne_courant);
                ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
            }


            //Construction mail
            Depot depot = DepotOpenHelper.getDepotParID(db, ph_preparation_Selectionne.getDepotDestinataireID());
            String commentaire = ph_preparation_Selectionne.getCommentaires();
            if(commentaire == null || commentaire.contentEquals(""))
            {
                commentaire = "Pas de commentaire saisie";
            }

            subject = "phiwms_mobile - "+ depot.getNom() + " - Livraisons N°" + ph_preparation_Selectionne.getUID() + " - " + date;

            //Sauvegarde de la signature dans une image
            if (photoLivraisonBitmap != null) {

                dateFormat = new SimpleDateFormat("yyyyMMdd");
                dateDuJour = new Date();
                date = dateFormat.format(dateDuJour);

                photoLivraisonPhotoName = ph_preparation_Selectionne.getUID() + "_" + date + "_LivraisonPhoto";

                verifyStoragePermissions(InformationLivraisonActivity.this);
            }

            String preparer_par = "";
            String valider_par = "";
            String livrer_par = "";
            Utilisateur userLivreur = UtilisateurOpenHelper.getUtilisateurByID(db, ph_preparation_Selectionne.getLivreur_userID());
            if(userLivreur != null)
            {
                livrer_par = userLivreur.getNom()+" "+userLivreur.getPrenom();
            }
            if(ph_preparation_Selectionne.getPreparateur() != null)
            {
                String[] tab_preparateur = ph_preparation_Selectionne.getPreparateur().split("\\(");
                preparer_par = tab_preparateur[0];
                String[] tab_valider_par = tab_preparateur[1].split("\\)");
                valider_par = tab_valider_par[0];
            }

            //Verifier le livré par
            if(liste_produit_refuser.isEmpty())
            {
                body = "Madame, Monsieur, \n \n" +
                        "La livraison N°" + ph_preparation_Selectionne.getUID() + " à destination de " + depot.getNom() + " a été réalisée. \n" +
                        "Préparé par "+preparer_par+"\n"+
                        "Validé par "+valider_par+"\n"+
                        "Livré par "+livrer_par+"\n"+
                        "Vous pourrez trouver ci-joint le bon de livraison signé. \n" +
                        "Commentaire : "+commentaire+"\n\n"+
                        "Ceci est un message automatique merci de ne pas répondre\n\n";
            }
            else
            {
                StringBuilder text_produit_refuser = new StringBuilder();
                for(String refus_courant : liste_produit_refuser)
                {
                    text_produit_refuser.append(refus_courant).append("\n");
                }
                body = "Madame, Monsieur, \n \n" +
                        "La livraison N°" + ph_preparation_Selectionne.getUID() + " à destination de " + depot.getNom() + " a été réalisée. \n" +
                        "Préparé par "+preparer_par+"\n"+
                        "Validé par "+valider_par+"\n"+
                        "Livré par "+livrer_par+"\n"+
                        "Les produits suivant n'ont pas étaient livrés ou sont livrés en partie : \n\n"+text_produit_refuser+"\n"+
                        "Vous pourrez trouver ci-joint le bon de livraison signé. \n" +
                        "Commentaire : "+commentaire+"\n\n"+

                        "Ceci est un message automatique merci de ne pas répondre\n\n";
            }


            try {
                OutilsGestionPDF outilsGestionPDF = new OutilsGestionPDF(true);
                outilsGestionPDF.createLivraisonV2(InformationLivraisonActivity.this, filename, signatureNameChauffeur, db, ph_preparation_Selectionne);
            } catch (IOException e) {
                Log.e(TAG, "IOException :", e);
            } catch (DocumentException e) {
                Log.e(TAG, "DocumentException :", e);
            }

            // Récupération Mail Pharmacie
            String email = ParametresServeurOpenHelper.getMailPharmacie(db);
            if(utilisateurConnecte.getEtablissement().contentEquals("ADH"))
            {
                email = "livraison.pui@adh-asso.net";
            }
            if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
            {
                email = "dev01@alcyons.fr";
            }

            if (email != null) {
                new SendEmailTask().execute(email);
            }

            dialogue.dialog.dismiss();
            ph_preparation_Selectionne.setLivreur_userID(utilisateurConnecte.getId());
            mettreAJourPhPreparation(ph_preparation_Selectionne);
            Toast.makeText(InformationLivraisonActivity.this, "Livraison effectuée", Toast.LENGTH_SHORT).show();

            onBackPressed();
        }
    };

    @NonNull
    private ActionUtilisateur_Ligne getActionUtilisateurLigne(PH_Preparation_Ligne preparation_ligne_courant) {
        Random randomactionligne = new Random();
        int actionligneId = randomactionligne.nextInt();
        if(actionligneId > 0)
            actionligneId= actionligneId*-1;

        return new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Ph_Preparation_Ligne", preparation_ligne_courant.get_UID(), "", 0, preparation_ligne_courant.getQte_livrer(), preparation_ligne_courant.getProduitDesignation());
    }

    public View.OnClickListener clicboutonSignerLivreur = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingMenu.close(true);
            dialogue = new Dialogue(InformationLivraisonActivity.this, clicValidationSignature, utilisateurConnecte);
            dialogue.signaturePadOpen(true);
        }
    };

    public View.OnClickListener clicboutonToutRefuser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingMenu.close(true);
            for(int i = 0; i < ph_preparation_ligne_livraisonAdapter.getCount(); i++)
            {
                View view = ph_preparationLigne_ListView.getChildAt(i);
                view.findViewById(R.id.Accepter).setVisibility(View.GONE);
                view.findViewById(R.id.Refuser).setVisibility(View.VISIBLE);
                view.findViewById(R.id.layoutValidation).setBackgroundColor(InformationLivraisonActivity.this.getColor(R.color.rouge2));
                ph_preparation_ligne_List.get(i).setAccepter(false);
                ph_preparation_ligne_List.get(i).setQte_livrer(0);

                PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_List.get(i));
                ph_preparation_ligne_livraisonAdapter.notifyDataSetChanged();
            }

        }
    };

    public View.OnClickListener clicboutonPatientAbsent = new View.OnClickListener() {
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onClick(View v) {
            floatingMenu.close(true);
            boolean confirmer = Alerte.afficherAlerte(InformationLivraisonActivity.this, "Confirmer", "Confirmez-vous que le patient est absent ?", "OuiNon");
            if(confirmer)
            {
                ph_preparation_Selectionne.setStatut("Refuser");
                ph_preparation_Selectionne.setMotif("Patient absent");
                for(PH_Preparation_Ligne preparation_ligne : ph_preparation_ligne_List)
                {
                    preparation_ligne.setQte_livrer(0);
                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, preparation_ligne);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, preparation_ligne.getPhiMR4UUID(), preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                }
                PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation_Selectionne);

                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation_Selectionne.getPhiMR4UUID(), ph_preparation_Selectionne.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

                // Tentative de lancer la sychronisation
                if (statutConnexion) {
                    ElementASynchroniserOpenHelper.toutSynchroniser(InformationLivraisonActivity.this, db, utilisateurConnecte, true);
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date dateDuJour = new Date();
                dateFormat.format(dateDuJour);
                String date;
                dateFormat = new SimpleDateFormat("yyyyMMdd");
                dateDuJour = new Date();
                date = dateFormat.format(dateDuJour);

                subject = "phiwms_mobile - "+ depot.getNom() + " - Livraisons N°" + ph_preparation_Selectionne.getUID() + " Refusé - " + date;
                body = "Madame, Monsieur, \n \n" +
                        "La livraison N°" + ph_preparation_Selectionne.getUID() + " à destination de " + depot.getNom() + " a été refusé car le patient est absent. \n" +
                        "Ceci est un message automatique merci de ne pas répondre\n\n";

                // Récupération Mail Pharmacie
                String email = ParametresServeurOpenHelper.getMailPharmacie(db);
                if(utilisateurConnecte.getEtablissement().contentEquals("ADH"))
                {
                    email = "livraison.pui@adh-asso.net";
                }
                if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
                {
                    email = "dev01@alcyons.fr";
                }

                if (email != null) {
                    new SendEmailTask().execute(email);
                    onBackPressed();
                }

            }
        }
    };

    //clic sur le numéro de téléphone ouvre le téléphone pour appeler le correspondant
    public void telephoneDepot(View v) {
        // Demande des autorisations
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.CALL_PHONE
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + depot.getTel()));
            startActivity(intent);
        }
    }

    //clic sur l'adresse du dépôt ouvre Google Maps
    public void adresseDepot(View v) {
        String map = "https://www.google.com/maps/search/?api=1&query=" + adresse;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        startActivity(i);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_livraison);

        //gestion de la photo
        photoProduitsChemin = "chemin";

        ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, intent.getIntExtra("ph_preparationUID_Selectionne", 0));
        ph_preparation_ligne_List = PH_Preparation_LigneOpenHelper.getALivrerPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);

        //gestion des floating boutons et du menu
        floatingMenu = findViewById(R.id.floatingMenu);
        boutonSignatureLivreur = findViewById(R.id.boutonSignatureLivreur);
        boutonPatientAbsent = findViewById(R.id.boutonPatientAbsent);
        boutonToutRefuser = findViewById(R.id.boutonToutRefuser);

        boutonSignatureLivreur.setOnClickListener(clicboutonSignerLivreur);

        boutonPatientAbsent.setOnClickListener(clicboutonPatientAbsent);

        boutonToutRefuser.setOnClickListener(clicboutonToutRefuser);

        //gestion liste
        //initialisation des listes
        liste_produit_refuser = new ArrayList<>();
        ph_preparationLigne_ListView = findViewById(R.id.listeView);
        tempPh_preparation_ligne_List = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);

        ph_preparation_ligne_List = new ArrayList<>();

        for (PH_Preparation_Ligne ph_preparation_ligne : tempPh_preparation_ligne_List) {
            if (ph_preparation_ligne.getQte_livrer() > 0) {
                ph_preparation_ligne_List.add(ph_preparation_ligne);
            }
        }

        ph_preparation_ligne_List.sort(Comparator.comparing(PH_Preparation_Ligne::getProduitDesignation));

        if (savedInstanceState != null) {
            ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, savedInstanceState.getInt("ph_preparationUID_Selectionne"));
        }

        for (PH_Preparation_Ligne ph_preparation_ligne : ph_preparation_ligne_List) {
            ph_preparation_ligne.setAccepter(true);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
        }

        // Transformation de la date au format yyyy-MM-dd à dd/MM/yyyy
        String dateLivraison = "";
        Date dateLivraisonPrevue;

        @SuppressLint("SimpleDateFormat") DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            dateLivraisonPrevue = dateDecodeur.parse(ph_preparation_Selectionne.getLivraisonPrevueDate());

            if (dateLivraisonPrevue != null) {
                dateLivraison = dateFormat.format(dateLivraisonPrevue);
            }
        } catch (ParseException e) {
            Log.e(TAG, "ParseException :", e);
        }

        //Récupération du dépot concerné par la livraison
        depot = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotDestinataireReference());

        // Affichage des informations de base
        ((TextView) findViewById(R.id.uidPHPreparation)).setText("N°" + String.valueOf(ph_preparation_Selectionne.getUID()).trim());
        ((TextView) findViewById(R.id.livraisonPrevueDate)).setText(dateLivraison);

        ((TextView) findViewById(R.id.identiteClient)).setText(depot.getNom().trim());

        ((TextView) findViewById(R.id.telephone)).setText(depot.getTel().trim());

        if (!ph_preparation_Selectionne.getURGENT()) {
            findViewById(R.id.isUrgent).setVisibility(View.GONE);
        }

        if(!ph_preparation_Selectionne.getCommentaires().contentEquals("") && ph_preparation_Selectionne.getCommentaires() != null)
        {
            ((TextView) findViewById(R.id.commentaire)).setText(ph_preparation_Selectionne.getCommentaires().trim());
        }
        else
        {
            findViewById(R.id.zoneCommentaire).setVisibility(View.GONE);
            findViewById(R.id.separateur2).setVisibility(View.GONE);
        }

        adresse = "";

        if (depot.getDepot_Reference().contains("PAD") && depot.isPAD_Utiliser_Adresse_Vacances()) {
            adresse = depot.getPAD_Vacances_Adr1() + ", ";
            if (depot.getPAD_Vacances_Adr2().length() > 1) {
                adresse += depot.getPAD_Vacances_Adr2() + ", ";
            }
            adresse += depot.getPAD_Vacances_CP() + " " + depot.getPAD_Vacances_Ville();
        } else {
            adresse = depot.getAdresse1() + ", ";
            if (depot.getAdresse2().length() > 1) {
                adresse += depot.getAdresse2() + ", ";
            }
            adresse += depot.getCP() + " " + depot.getVille();
        }

        ((TextView) findViewById(R.id.adresse)).setText(adresse.trim());
        findViewById(R.id.layoutIdentite).setOnClickListener(v -> {
            if(findViewById(R.id.layoutAdresse).getVisibility() == View.GONE)
            {
                findViewById(R.id.layoutAdresse).setVisibility(View.VISIBLE);
                findViewById(R.id.deployerAdresse).setVisibility(View.GONE);
                findViewById(R.id.replierAdresse).setVisibility(View.VISIBLE);
            }
            else
            {
                findViewById(R.id.layoutAdresse).setVisibility(View.GONE);
                findViewById(R.id.deployerAdresse).setVisibility(View.VISIBLE);
                findViewById(R.id.replierAdresse).setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        int nbColisVE = 0;

        List<PH_Preparation_Ligne> tempPh_preparation_ligne_List;
        tempPh_preparation_ligne_List = PH_Preparation_LigneOpenHelper.getALivrerPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);


        for (PH_Preparation_Ligne ph_preparation_ligne : tempPh_preparation_ligne_List) {
            int qte = ph_preparation_ligne.getQte_livrer();
            Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne.getProduitID());

            if (produit != null) {

                int conditionnementSet = produit.getCond_achat();
                int nombreColis = 0;

                if (qte > 0 && conditionnementSet > 0) {
                    nombreColis = qte / conditionnementSet;
                    if ((qte % conditionnementSet) != 0) {
                        nombreColis++;
                    }
                }
                if (qte > 0) {
                    if (nombreColis == 0) {
                        nombreColis = 1;
                    }
                }

                nbColisVE += nombreColis;
            }
        }
        ((TextView) findViewById(R.id.montantTTC)).setText(String.valueOf((int)ph_preparation_Selectionne.getMontant_TTC()));
        ((TextView) findViewById(R.id.poidsTotal)).setText(String.valueOf((int)ph_preparation_Selectionne.getPoids()));
        ((TextView) findViewById(R.id.volume)).setText(String.valueOf((int)ph_preparation_Selectionne.getVolume()));
        ((TextView) findViewById(R.id.nbRef)).setText(String.valueOf(tempPh_preparation_ligne_List.size()));
        ((TextView) findViewById(R.id.nbColis)).setText(String.valueOf(nbColisVE));

        ph_preparation_ligne_livraisonAdapter = new PH_Preparation_Ligne_LivraisonAdapter(InformationLivraisonActivity.this, ph_preparation_ligne_List);
        ph_preparationLigne_ListView.setDivider(footer);
        ph_preparationLigne_ListView.setAdapter(ph_preparation_ligne_livraisonAdapter);

        ph_preparationLigne_ListView.setOnItemClickListener((parent, view, position, id) -> {
            if(floatingMenu.isOpened())
            {
                floatingMenu.close(true);
            }
            else
            {
                if (ph_preparation_ligne_List.get(position).getAccepter()) {
                    ph_preparation_ligne_List.get(position).setAccepter(false);
                    ph_preparation_ligne_List.get(position).setQte_livrer(0);
                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_List.get(position));

                    view.findViewById(R.id.Accepter).setVisibility(View.GONE);
                    view.findViewById(R.id.Refuser).setVisibility(View.VISIBLE);

                } else {
                    ph_preparation_ligne_List.get(position).setAccepter(true);
                    ph_preparation_ligne_List.get(position).setQte_livrer(ph_preparation_ligne_List.get(position).getQte_APreparer());
                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_List.get(position));
                    view.findViewById(R.id.Accepter).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.Refuser).setVisibility(View.GONE);
                }
                ph_preparation_ligne_livraisonAdapter.notifyDataSetChanged();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    public void onClickMenuPhoto()
    {
        Intent informationLivraison_Intent = new Intent(InformationLivraisonActivity.this, PrisePhoto.class);
        Bundle informationLivraison_Bundle = InformationLivraisonActivity.super.getBundle();
        informationLivraison_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        informationLivraison_Bundle.putInt("serviceSelectionneID", serviceActuel.getId());
        informationLivraison_Bundle.putInt("preparationUID", ph_preparation_Selectionne.getUID());
        informationLivraison_Bundle.putString("contexte", "priseDePhotoLivraison");
        informationLivraison_Intent.putExtras(informationLivraison_Bundle);
        InformationLivraisonActivity.this.startActivityForResult(informationLivraison_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RESULT_SIGNATURE: {
                if (data != null) {
                    InformationLivraisonActivity.this.finish();
                }
            }
            break;
            case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                if(data != null)
                {
                    photoProduitsChemin = Objects.requireNonNull(data.getExtras()).getString("photoProduit");
                    if (photoProduitsChemin != null && !photoProduitsChemin.contentEquals("")) {
                        try {
                            photoLivraisonBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoProduitsChemin));
                        } catch (IOException e) {
                            Log.e(TAG, "IOException :", e);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent serviceLivraison_Intent = new Intent(InformationLivraisonActivity.this, ServiceLivraisonActivity.class);
        Bundle serviceLivraison_Bundle = InformationLivraisonActivity.super.getBundle();
        serviceLivraison_Intent.putExtras(serviceLivraison_Bundle);
        InformationLivraisonActivity.this.startActivity(serviceLivraison_Intent);
        InformationLivraisonActivity.this.finish();
    }

    public void mettreAJourPhPreparation(PH_Preparation ph_preparation) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
        if (statutConnexion) {
            ElementASynchroniserOpenHelper.toutSynchroniser(InformationLivraisonActivity.this, db, utilisateurConnecte, true);
        }
    }

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

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(InformationLivraisonActivity.this, email[0], true, db, utilisateurConnecte);
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
                Log.e(TAG, "Exception :", e);
            }
            return "executed";
        }
    }
}

