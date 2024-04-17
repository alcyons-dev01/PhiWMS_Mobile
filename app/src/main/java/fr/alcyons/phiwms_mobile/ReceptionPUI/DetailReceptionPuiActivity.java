package fr.alcyons.phiwms_mobile.ReceptionPUI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeReceptionActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerReceptionActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Reliquat_ReceptionPuiAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.PrisePhoto.PrisePhoto;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos.verifyStoragePermissions;

/**
 * Created by olivier on 17/04/2024.
 */

public class DetailReceptionPuiActivity extends ServiceActivity {
    Commande commandeSelectionne;
    List<PH_Reliquat> phReliquatList;
    List<PH_Reliquat_ReceptionPUI_Adapte> phReliquatReceptionPUIAdapteList;
    ListView phReliquatListView;
    Depot depotPUI;
    PH_Reliquat_ReceptionPuiAdapter phReliquatReceptionPuiAdapter;
    PH_Reliquat_ReceptionPuiAdapter.PH_Reliquat_ReceptionPuiViewHolder phReliquatReceptionPuiViewHolder;
    String bonLivraison = "";
    String subject = "";
    String body = "";
    String bonLivraisonPhotoName = "";
    Boolean orientation = false;
    boolean check_lot_present;
    boolean envoyerCopie;
    String EmailCopie;
    Bitmap bonLivraisonBitmap;
    String erreur ="";
    boolean second_passage_photo;
    PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapteSelectionne;
    List<String> listeProduitRAL = new ArrayList<>();
    PackageManager pm;
    String tri_choisi;
    LinearLayout lancerScan;
    Depot_Emplacement emplacement_precedent;
    Produit produitPrecedent;

    @SuppressLint("SimpleDateFormat")
    public void valider_reception()
    {
        //on check la connexion à internet pour l'envoie du mail
        boolean internet = checkInternetConnection();
        if(!internet)
        {
            Alerte.afficherAlerte(DetailReceptionPuiActivity.this, "Erreur", "Aucune connexion internet détectée, aucun envoi de mail possible","alerte");
        }
        else
        {
            //Construction mail
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date dateDuJour = new Date();
            String date = dateFormat.format(dateDuJour);
            Depot depotdest = DepotOpenHelper.getDepotParReference(db, commandeSelectionne.getRef_Depot_Dest());
            String depot_destinataire = "";
            if(depotdest != null)
            {
                depot_destinataire = depotdest.getNom();
            }



            subject = "PhiMR4 - "+depot_destinataire+" - "+commandeSelectionne.getFournisseur()+" - Réception PUI N°" + commandeSelectionne.getNumero() + " - " + date;
            if(bonLivraisonBitmap != null)
            {
                body = "Madame, Monsieur, \n \n" +
                        "La réception N°" + commandeSelectionne.getNumero() + " a été réalisée par "+utilisateurConnecte.getNom()+" "+utilisateurConnecte.getPrenom()+". \n" +
                        "Le numéro de bon de livraison saisi est le suivant : "+commandeSelectionne.getBLNumero()+"\n\n" +
                        "Vous pourrez trouver ci-joint le bon de livraison. \n\n" +
                        "Cordialement, \n\n" +
                        "L'équipe Phi \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre";
            }
            else
            {
                body = "Madame, Monsieur, \n \n" +
                        "La réception N°" + commandeSelectionne.getNumero() + " a été réalisée par "+utilisateurConnecte.getNom()+" "+utilisateurConnecte.getPrenom()+". \n" +
                        "Le numéro de bon de livraison saisi est le suivant : "+commandeSelectionne.getBLNumero()+"\n\n" +
                        "Cordialement, \n\n" +
                        "L'équipe Phi \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre";
            }


            //Sauvegarde de la signature dans une image
            if (bonLivraisonBitmap != null) {
                dateFormat = new SimpleDateFormat("yyyyMMdd");
                dateDuJour = new Date();
                date = dateFormat.format(dateDuJour);

                bonLivraisonPhotoName = commandeSelectionne.getNumero() + "_" + date + "_ReceptionPuiBonLivraison";

                verifyStoragePermissions(DetailReceptionPuiActivity.this);
            }

            // Récupération Mail Pharmacie
            String email = ParametresServeurOpenHelper.getMailPharmacie(db);
            if(utilisateurConnecte.getEtablissement().contentEquals("ADH"))
            {
                email = "reception.pui@adh-asso.net";
            }
            if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
            {
                email = "dev01@alcyons.fr";
            }
            afficherAlerteConfirmationMail(DetailReceptionPuiActivity.this, LayoutInflater.from(DetailReceptionPuiActivity.this), email);
        }


    }

    public void envoyerMail(boolean copieMail, final String email)
    {
       /* if (copieMail) {
            EmailCopie = utilisateurConnecte.getMail();
            if (EmailCopie == null || EmailCopie.contentEquals("")) {
                envoyerCopie = false;
            }
        }

        if (email != null) {
            //new SendEmailTask().execute(email);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            final Handler handler_mail = new Handler(Looper.getMainLooper());

            executor.execute(new Runnable() {
                @Override
                public void run() {

                    //Background work here

                    handler_mail.post(new Runnable() {
                        @Override
                        public void run() {
                            envoyerMail(email);
                            //UI Thread work here
                        }
                    });
                }
            });
        }*/

        Toast toast = Toast.makeText(DetailReceptionPuiActivity.this, "Réception effectuée", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        retourService(super.getBundle());
    }


    public void onMenuSaveClick() {
        afficherAlerteConfirmation(DetailReceptionPuiActivity.this, LayoutInflater.from(DetailReceptionPuiActivity.this));
    }

    private Boolean receptionner(Commande commande, List<PH_Reliquat_ReceptionPUI_Adapte> phReliquatReceptionPUIAdapteList) {
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", commande.getID_commande(), "", "Réception PUI");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);

        for (PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte : phReliquatReceptionPUIAdapteList) {

            PH_Reliquat phReliquatCourant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionPUIAdapte.getPhReliquatUID());

            if (phReliquatCourant != null) {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, phReliquatCourant.getPhiMR4UUID(), phReliquatCourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.SUPPR);
                gestionnairePH_Reliquat.supprimerUnPhReliquat(db, phReliquatCourant);

                for (PH_Reliquat_ReceptionPUI_Adapte.Lot lot : phReliquatReceptionPUIAdapte.getlotList()) {
                    for (PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement : lot.getZoneEtEmplacementList()) {

                        String numeroLot = lot.getNumeroLot();
                        String datePeremption = lot.getDatePeremption();
                        String zoneName = zoneEtEmplacement.getZoneName();
                        String emplacementName = zoneEtEmplacement.getEmplacementName();
                        String numero_Serie = lot.getNumero_serie();
                        int quantite = zoneEtEmplacement.getQuantite();

                        if(quantite == 0)
                        {
                            erreur = "Quantité";
                            listeProduitRAL.add(phReliquatCourant.getDesignationCourte()+" - "+ phReliquatCourant.getQteCommande());
                        }

                        if(numeroLot.contentEquals(""))
                        {
                            erreur = "Lot";
                            int index = listeProduitRAL.indexOf(phReliquatCourant.getDesignationCourte()+" - "+ phReliquatCourant.getQteCommande());
                            if(index == - 1)
                                listeProduitRAL.add(phReliquatCourant.getDesignationCourte()+" - "+ phReliquatCourant.getQteCommande());
                        }

                        phReliquatCourant.setLot(numeroLot.trim());
                        phReliquatCourant.setSerie(numero_Serie.trim());
                        phReliquatCourant.setPeremptionDate(datePeremption.trim());
                        phReliquatCourant.setZone(zoneName.trim());
                        phReliquatCourant.setEmplacement(emplacementName.trim());
                        phReliquatCourant.setQteLivraison(quantite);
                        phReliquatCourant.setBL_Numero(bonLivraison);
                        phReliquatCourant.setScanValue("");

                        long rowID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, phReliquatCourant);
                        if (rowID != -1) {
                            //gestion des actions lignes
                            Random randomactionligne = new Random();
                            int actionligneId = randomactionligne.nextInt();
                            if(actionligneId > 0)
                                actionligneId= actionligneId*-1;

                            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Reliquat", phReliquatCourant.getcommandeLigneID(), "", zoneEtEmplacement.getEmplacementId(), (int)phReliquatCourant.getQteLivraison(), phReliquatCourant.getDesignationCourte());
                            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, phReliquatCourant.getPhiMR4UUID(), phReliquatCourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
        }

        commande.setSituation("RM"); //R = Réception, M = Mobile
        long rowID = CommandeOpenHelper.mettreAJourUneCommande(db, commande);
        if (rowID != -1) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, CommandeOpenHelper.Constantes.TABLE_COMMANDE, commande.getPhiMR4UUID(), commande.getID_commande(), DBOpenHelper.ActionsEAS.MAJ);
            //on ajoute l'action utilisateur à synchroniser à la fin
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

            // Si possible, on essaie de mettre à jour les éléments
            if (OutilsGestionConnexionReseau.isServerAccessible(DetailReceptionPuiActivity.this)) {
                ElementASynchroniserOpenHelper.toutSynchroniser(DetailReceptionPuiActivity.this, db, utilisateurConnecte, true);
            }
            else
            {
                Alerte.afficherAlerte(DetailReceptionPuiActivity.this, "Erreur", "Serveur inaccessible. Votre action sera exécuté ultérieurement", "alerte");
            }

            return listeProduitRAL.isEmpty();
        } else {
            return false;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reception_pui);

        //initilisation de l'emplacement précédent
        emplacement_precedent = null;
        produitPrecedent = null;

        //gestion du package manager
        pm = DetailReceptionPuiActivity.this.getPackageManager();

        commandeSelectionne = CommandeOpenHelper.getCommandeByID(db, Objects.requireNonNull(intent.getExtras()).getInt("commandeID_Selectionne"));

        orientation = false;
        check_lot_present = false;
        second_passage_photo = false;

        //gestion de la rotation de l'écran
        if (savedInstanceState != null) {
            bonLivraison = savedInstanceState.getString("NomBonLivraison");
            phReliquatList = (List<PH_Reliquat>) savedInstanceState.getSerializable("listePHReliquat");
            orientation = savedInstanceState.getBoolean("Orientation");
            phReliquatReceptionPUIAdapteList = (List<PH_Reliquat_ReceptionPUI_Adapte>) savedInstanceState.getSerializable("listePuiAdapte");
            phReliquatListView = (ListView) findViewById(R.id.liste_view);
            onResume();
        }

        phReliquatListView = (ListView) findViewById(R.id.liste_view);
        phReliquatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                phReliquatReceptionPUIAdapteSelectionne = (PH_Reliquat_ReceptionPUI_Adapte) phReliquatReceptionPuiAdapter.getItem(position);

                if (phReliquatReceptionPUIAdapteSelectionne != null) {
                    phReliquatReceptionPuiViewHolder = phReliquatReceptionPuiAdapter.viewHolderList.get(position);

                    List<PH_Reliquat_ReceptionPUI_Adapte.Lot>liste_lot = phReliquatReceptionPUIAdapteSelectionne.getlotList();
                    for(PH_Reliquat_ReceptionPUI_Adapte.Lot courant : liste_lot)
                    {
                        if(!courant.getNumeroLot().contentEquals(""))
                        {
                            check_lot_present = true;
                        }
                    }

                    if(phReliquatReceptionPUIAdapteSelectionne.isSuiviParSerieActif() && phReliquatReceptionPUIAdapteSelectionne.isSerialiserReception() && !check_lot_present)
                    {
                        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                        {
                            Intent detailReceptionPui_Intent = new Intent(DetailReceptionPuiActivity.this, ListeLotReceptionPuiActivity.class);
                            Bundle detailReceptionPui_Bundle = DetailReceptionPuiActivity.super.getBundle();
                            detailReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                            detailReceptionPui_Bundle.putSerializable("phReliquatReceptionPUIAdapte", phReliquatReceptionPUIAdapteSelectionne);
                            detailReceptionPui_Bundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));
                            detailReceptionPui_Bundle.putSerializable("EmplacementPrecedent", emplacement_precedent);
                            detailReceptionPui_Bundle.putSerializable("ProduitPrecedent", produitPrecedent);

                            detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);

                            DetailReceptionPuiActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
                        }
                        else
                        {
                            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                            {
                                PH_Reliquat courant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionPUIAdapteSelectionne.getPhReliquatUID());
                                int quantiteReliquat = courant.getQteCommande() - courant.getQteLivraison();
                                Produit produit_selectionne = ProduitOpenHelper.getProduitByID(db, courant.getProduitID());
                                Intent detailReceptionPUIIntent = new Intent(DetailReceptionPuiActivity.this, BarcodeCaptureActivity.class);
                                Bundle detailReceptionPUIBundle = DetailReceptionPuiActivity.super.getBundle();
                                detailReceptionPUIBundle.putString("contexte", String.valueOf(R.string.scannerContexteFranceMVO));
                                detailReceptionPUIBundle.putBoolean("isBoutonSuppressionExistant", true);
                                detailReceptionPUIBundle.putInt("UserId", utilisateurConnecte.getId());
                                detailReceptionPUIBundle.putInt("qteReliquat", quantiteReliquat);
                                detailReceptionPUIBundle.putInt("reliquat_uid", courant.getReliquat_UID());
                                detailReceptionPUIBundle.putBoolean("modeRafale", true);
                                detailReceptionPUIBundle.putString("GTIN_courant", produit_selectionne.getGTIN());
                                detailReceptionPUIBundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));

                                detailReceptionPUIIntent.putExtras(detailReceptionPUIBundle);

                                DetailReceptionPuiActivity.this.startActivityForResult(detailReceptionPUIIntent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
                            }
                            else
                            {
                                Intent detailReceptionPui_Intent = new Intent(DetailReceptionPuiActivity.this, ListeLotReceptionPuiActivity.class);
                                Bundle detailReceptionPui_Bundle = DetailReceptionPuiActivity.super.getBundle();
                                detailReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                                detailReceptionPui_Bundle.putSerializable("phReliquatReceptionPUIAdapte", phReliquatReceptionPUIAdapteSelectionne);
                                detailReceptionPui_Bundle.putSerializable("EmplacementPrecedent", emplacement_precedent);
                                detailReceptionPui_Bundle.putSerializable("ProduitPrecedent", produitPrecedent);
                                detailReceptionPui_Bundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));

                                detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);

                                DetailReceptionPuiActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
                            }
                        }

                    }
                    else
                    {
                        Intent detailReceptionPui_Intent = new Intent(DetailReceptionPuiActivity.this, ListeLotReceptionPuiActivity.class);
                        Bundle detailReceptionPui_Bundle = DetailReceptionPuiActivity.super.getBundle();
                        detailReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                        detailReceptionPui_Bundle.putSerializable("phReliquatReceptionPUIAdapte", phReliquatReceptionPUIAdapteSelectionne);
                        detailReceptionPui_Bundle.putSerializable("EmplacementPrecedent", emplacement_precedent);
                        detailReceptionPui_Bundle.putSerializable("ProduitPrecedent", produitPrecedent);
                        detailReceptionPui_Bundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));

                        detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);

                        DetailReceptionPuiActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
                    }
                }
            }
        });

        // Récupération da la commande selectionné
        if (commandeSelectionne != null) {
            // Entete
            ((TextView) findViewById(R.id.nomFournisseur)).setText(commandeSelectionne.getFournisseur());
            ((TextView) findViewById(R.id.numCommande)).setText("#" + commandeSelectionne.getNumero());

            phReliquatList = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeSelectionne.getNumero());

            phReliquatList.sort(Comparator.comparing(PH_Reliquat::getdesignationCourte));

            depotPUI = DepotOpenHelper.getDepotPUI(db);

            phReliquatReceptionPUIAdapteList = new ArrayList<>();

            for (PH_Reliquat phReliquatCourant : phReliquatList) {
                PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte = new PH_Reliquat_ReceptionPUI_Adapte(phReliquatCourant.getReliquat_UID(), phReliquatCourant.getSerie(), phReliquatCourant.isSuiviParSerieActif(), phReliquatCourant.isSerialiserReception());

                phReliquatReceptionPUIAdapteList.add(phReliquatReceptionPUIAdapte);
            }

            //initi du tri
            tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriReliquat(db);
            if(tri_choisi == null)
            {
                ParametreUtilisateurOpenHelper.mettreAJourTriReliquat(db, 0,"Categorie");
                tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriReliquat(db);
            }

            switch (tri_choisi)
            {
                case "Categorie":
                    onClickTriCategorie();
                    break;
                case "Designation":
                    onClickTriDesignation();
                    break;

                case "Place":
                    onTriParPlace();
                    break;
            }

        } else {
            DetailReceptionPuiActivity.this.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        lancerScan = (LinearLayout) findViewById(R.id.lancerScan);
        tri_choisi = "Categorie";

        //gestion du bouton qui lance le scan
        lancerScan.setOnClickListener(view -> {
            Intent listeLotReceptionPui_Intent;
            Bundle listeLotReceptionPui_Bundle = new Bundle();
            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies")  || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                listeLotReceptionPui_Intent = new Intent(DetailReceptionPuiActivity.this, ScannerReceptionActivity.class);
            }
            else
            {
                listeLotReceptionPui_Intent = new Intent(DetailReceptionPuiActivity.this, BarcodeReceptionActivity.class);
            }

            listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContextNewReceptionPUI));
            listeLotReceptionPui_Bundle.putInt("ReceptionID", commandeSelectionne.getID_commande());
            listeLotReceptionPui_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            listeLotReceptionPui_Bundle.putString("ordreTri", tri_choisi);
            listeLotReceptionPui_Bundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));
            listeLotReceptionPui_Bundle.putSerializable("ReceptionPUIAdapte", (Serializable) phReliquatReceptionPUIAdapteList);
            listeLotReceptionPui_Bundle.putSerializable("EmplacementPrecedent", (Serializable) emplacement_precedent);
            listeLotReceptionPui_Bundle.putSerializable("ProduitPrecedent", (Serializable) produitPrecedent);
            listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
            DetailReceptionPuiActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
        });

        switch (tri_choisi)
        {
            case "Categorie":
                onClickTriCategorie();
                break;
            case "Designation":
                onClickTriDesignation();
                break;

            case "Place":
                onTriParPlace();
                break;
        }


        invalidateOptionsMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_LOTS:
                    PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte = phReliquatReceptionPuiAdapter.phReliquatReceptionPUIAdapteList.get(phReliquatReceptionPuiAdapter.viewHolderList.indexOf(phReliquatReceptionPuiViewHolder));
                    phReliquatReceptionPUIAdapte.setlotList((List<PH_Reliquat_ReceptionPUI_Adapte.Lot>) data.getExtras().getSerializable("lotList"));
                    emplacement_precedent = (Depot_Emplacement) data.getExtras().getSerializable("EmplacementPrecedent");
                    produitPrecedent = (Produit) data.getExtras().getSerializable("ProduitPrecedent");
                    onResume();
                    break;
                case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                    String photoProduits = Objects.requireNonNull(data.getExtras()).getString("photoProduit");
                    if (photoProduits == null || photoProduits.contentEquals("")) {
                        // Récupération du bon de livraison

                        if(second_passage_photo)
                        {
                            onMenuSaveClick();
                        }
                    } else {
                        try {
                            bonLivraisonBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoProduits));
                            if(second_passage_photo)
                            {
                                onMenuSaveClick();
                            }
                        } catch (IOException e) {
                            Log.e("IOException", Objects.requireNonNull(e.getMessage()));
                        }
                    }
                    break;
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    //récupération de la liste d'adapte
                    phReliquatReceptionPUIAdapteList = new ArrayList<>();
                    emplacement_precedent = (Depot_Emplacement) Objects.requireNonNull(data.getExtras()).getSerializable("EmplacementPrecedent");
                    produitPrecedent = (Produit) data.getExtras().getSerializable("ProduitPrecedent");
                    phReliquatReceptionPUIAdapteList = (List<PH_Reliquat_ReceptionPUI_Adapte>) data.getExtras().getSerializable("reliquatAdapteList");
                    onResume();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSave).setVisible(true);
        menu.findItem(R.id.menuPhoto).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSave);
        item.setOnMenuItemClickListener(item1 -> {
            onMenuSaveClick();
            return true;
        });

        MenuItem itemPhoto = menu.findItem(R.id.menuPhoto);
        itemPhoto.setOnMenuItemClickListener(item12 -> {
            prendrePhotoBL();
            return true;
        });
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outstate) {
        //gestion du commentaire pour ne pas effacer l'édit text pendant la rotation
        if (bonLivraison != null) {
            outstate.putString("NomBonLivraison", bonLivraison);
        }

        outstate.putBoolean("Orientation", true);

        outstate.putSerializable("listePuiAdapte", (Serializable) phReliquatReceptionPUIAdapteList);

        //gestion de la liste de retour ligne pour gérer le changement de valeur
        outstate.putSerializable("listePHReliquat", (Serializable) phReliquatList);

        super.onSaveInstanceState(outstate);
    }

    private Object envoyerMail(String email)
    {
        Mail sender;
        if(!envoyerCopie)
        {
            sender = new Mail(DetailReceptionPuiActivity.this, email, true, db);
        }
        else
        {
            sender = new Mail(DetailReceptionPuiActivity.this, email, EmailCopie, true, db);
        }
        try {
            // Envoi du mail avec pdf
            if(bonLivraisonPhotoName.contentEquals(""))
            {
                try {
                    sender.sendMailVerification(subject, body);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            else
            {
                try {
                    sender.sendMail(subject, body, "Documents/"+bonLivraisonPhotoName + ".jpeg");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (Exception e) {
            Log.e("Exception", Objects.requireNonNull(e.getMessage()));
        }

        return "executed";
    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!phReliquatReceptionPUIAdapteList.get(0).getlotList().isEmpty()) {
            afficherAlerteConfirmationRetour(DetailReceptionPuiActivity.this, LayoutInflater.from(DetailReceptionPuiActivity.this), super.getBundle());
        } else {
            retourService(super.getBundle());
        }
    }

    public void prendrePhotoBL()
    {
        Intent detailReceptionPui_Intent = new Intent(DetailReceptionPuiActivity.this, PrisePhoto.class);
        Bundle detailReceptionPui_Bundle = DetailReceptionPuiActivity.super.getBundle();
        // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
        detailReceptionPui_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        detailReceptionPui_Bundle.putString("CommandeNumero", commandeSelectionne.getNumero());
        detailReceptionPui_Bundle.putString("contexte", "priseDePhotoContexteBonDeLivraison");
        detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);
        DetailReceptionPuiActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
    }

    private void onClickTriDesignation()
    {
        tri_choisi = "Designation";
        phReliquatReceptionPUIAdapteList.sort((o1, o2) -> {

            PH_Reliquat oo1 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o1.getPhReliquatUID());
            PH_Reliquat oo2 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o2.getPhReliquatUID());

            return oo1.getDesignationCourte().toLowerCase().compareTo(oo2.getDesignationCourte().toLowerCase());
        });
        phReliquatReceptionPuiAdapter = new PH_Reliquat_ReceptionPuiAdapter(DetailReceptionPuiActivity.this, db, phReliquatReceptionPUIAdapteList);
        phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionPuiAdapter);
    }

    private void onClickTriCategorie()
    {
        tri_choisi = "Categorie";
        phReliquatReceptionPUIAdapteList.sort((o1, o2) -> {

            PH_Reliquat oo1 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o1.getPhReliquatUID());
            PH_Reliquat oo2 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o2.getPhReliquatUID());

            Produit produit1 = ProduitOpenHelper.getProduitByID(db, oo1.getProduitID());
            Produit produit2 = ProduitOpenHelper.getProduitByID(db, oo2.getProduitID());

            return produit1.getCategorie().toLowerCase().compareTo(produit2.getCategorie().toLowerCase());
        });
        phReliquatReceptionPuiAdapter = new PH_Reliquat_ReceptionPuiAdapter(DetailReceptionPuiActivity.this, db, phReliquatReceptionPUIAdapteList);
        phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionPuiAdapter);
    }

    private void onTriParPlace()
    {
        tri_choisi = "Place";
        phReliquatReceptionPUIAdapteList.sort((o1, o2) -> {

            PH_Reliquat oo1 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o1.getPhReliquatUID());
            PH_Reliquat oo2 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o2.getPhReliquatUID());
            String oo1EmplacementParDefaut = oo1.getEmplacement();
            String oo2EmplacementParDefaut = oo2.getEmplacement();

            if (oo1EmplacementParDefaut == null || oo1EmplacementParDefaut.contentEquals("")) {
                Produit produit = ProduitOpenHelper.getProduitByID(db, oo1.getProduitID());
                oo1EmplacementParDefaut = produit.getEmplacement_PUI_Defaut();

            }
            if (oo2EmplacementParDefaut == null || oo2EmplacementParDefaut.contentEquals("")) {
                Produit produit = ProduitOpenHelper.getProduitByID(db, oo2.getProduitID());
                oo2EmplacementParDefaut = produit.getEmplacement_PUI_Defaut();
            }

            return oo1EmplacementParDefaut.compareTo(oo2EmplacementParDefaut);
        });
        phReliquatReceptionPuiAdapter = new PH_Reliquat_ReceptionPuiAdapter(DetailReceptionPuiActivity.this, db, phReliquatReceptionPUIAdapteList);
        phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionPuiAdapter);
    }

    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_reception_pui, null);

        final LinearLayout buttonOk = (LinearLayout) layout.findViewById(R.id.buttonOk);
        final EditText numeroBLEdit = (EditText) layout.findViewById(R.id.numeroBL);
        final ImageView btn_photo = (ImageView) layout.findViewById(R.id.btnPhoto);
        final ImageView iconValidation = (ImageView) layout.findViewById(R.id.iconValidation);
        final LinearLayout boutonPhoto = (LinearLayout) layout.findViewById(R.id.boutonPhoto);

        Rect displayRectangle = new Rect();
        Window window = DetailReceptionPuiActivity.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        layout.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        layout.setMinimumHeight((int)(displayRectangle.height() * 0.6f));
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        buttonOk.setOnClickListener(v -> {
            buttonOk.setBackgroundColor(getResources().getColor(R.color.vert, null));
            ViewCompat.setBackgroundTintList(iconValidation, ColorStateList.valueOf(getResources().getColor(R.color.blanc, null)));
            String numeroBL = numeroBLEdit.getText().toString();
            commandeSelectionne.setBLNumero(numeroBL);
            bonLivraison = numeroBL;
            alertDialog.dismiss();
            //on vérifie qu'une saisie a été effectué avant d'enregistrer
            boolean saisie_effectuer = false;
            for (PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte : phReliquatReceptionPUIAdapteList)
            {
                for (PH_Reliquat_ReceptionPUI_Adapte.Lot lot : phReliquatReceptionPUIAdapte.getlotList()) {
                    for (PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement : lot.getZoneEtEmplacementList())
                    {
                        if(zoneEtEmplacement.getQuantite() != 0)
                        {
                            saisie_effectuer = true;
                            break;
                        }
                    }

                    if(saisie_effectuer)
                        break;
                }
            }

            if(saisie_effectuer)
            {
                second_passage_photo = false;
                Boolean receptionEffectuee = receptionner(commandeSelectionne, phReliquatReceptionPUIAdapteList);
                if (!receptionEffectuee) {
                    boolean continuer = false;
                    // Si une erreur est survenue, on annule les modifications en vidant la table ElementASynchroniser
                    if(erreur.contentEquals("Quantité"))
                    {
                        continuer = Alerte.afficherAlerteList(DetailReceptionPuiActivity.this, "Attention", "Les produits suivant n'ont pas été réceptionnés, continuer ? ", listeProduitRAL, "OuiNon");
                    }
                    else if(erreur.contentEquals("Lot"))
                    {
                        continuer = Alerte.afficherAlerteList(DetailReceptionPuiActivity.this, "Attention", "Les produits suivant n'ont pas été réceptionnés, continuer ?", listeProduitRAL, "OuiNon");
                    }
                    else
                    {
                        Alerte.afficherAlerte(DetailReceptionPuiActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
                        ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
                        DetailReceptionPuiActivity.this.finish();
                    }

                    if(continuer)
                        valider_reception();

                } else {

                    valider_reception();
                }
            }
            else
            {
                onBackPressed();
            }
        });

        btn_photo.setOnClickListener(v -> {
            boutonPhoto.setBackgroundColor(getResources().getColor(R.color.bleu_clair_alcyons, null));
            ViewCompat.setBackgroundTintList(btn_photo, ColorStateList.valueOf(getResources().getColor(R.color.blanc, null)));
            prendrePhotoBL();
        });
    }

    public void afficherAlerteConfirmationMail(Context context, LayoutInflater inflater, final String email) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_mail, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            envoyerMail(true, email);
        });

        buttonAnnuler.setOnClickListener(v -> {
            alertDialog.dismiss();
            envoyerMail(false, email);
        });
    }

    @SuppressLint("SetTextI18n")
    public void afficherAlerteConfirmationRetour(Context context, LayoutInflater inflater, final Bundle bundle) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_mail, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageTextView = (TextView) layout.findViewById(R.id.messageFin);
        messageTextView.setText("Vous allez quitter la réception, confirmez vous ?");
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            retourService(bundle);
        });

        buttonAnnuler.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void retourService(final Bundle bundle)
    {
        Intent detailReceptionPUIIntent = new Intent(DetailReceptionPuiActivity.this, ServiceReceptionPuiActivity.class);
        detailReceptionPUIIntent.putExtras(bundle);
        DetailReceptionPuiActivity.this.startActivity(detailReceptionPUIIntent);
        DetailReceptionPuiActivity.this.finish();
    }
}
