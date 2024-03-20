package fr.alcyons.phimr4.ReceptionScanne;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phimr4.Classes.Commande;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ListViewAdapters.PH_Reliquat_ReceptionPuiAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.Mail;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.PrisePhoto.PrisePhoto;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.Outils.OutilsGestionPhotos.verifyStoragePermissions;

/**
 * Created by olivier on 27/11/2017.
 */

public class DetailReceptionScanneeActivity extends ServiceActivity {
    Commande commandeSelectionne;
    List<PH_Reliquat> phReliquatList;
    List<PH_Reliquat_ReceptionPUI_Adapte> phReliquatReceptionPUIAdapteList;

    ListView phReliquatListView;
    TextView bonLivraisonTextView;

    Depot depotPUI;

    //TextView nbLotsLivresTotal;
    PH_Reliquat_ReceptionPuiAdapter phReliquatReceptionPuiAdapter;

    PH_Reliquat_ReceptionPuiAdapter.PH_Reliquat_ReceptionPuiViewHolder phReliquatReceptionPuiViewHolder;

    String bonLivraison = "";
    String subject = "";
    String body = "";
    String filename = "";
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
    Spinner optionTri;
    String tri_choisi;


    public void valider_reception()
    {
        //on check la connexion à internet pour l'envoie du mail
        boolean internet = checkInternetConnection();
        if(!internet)
        {
            Alerte.afficherAlerte(DetailReceptionScanneeActivity.this, "Erreur", "Aucune connexion internet détectée, aucun envoi de mail possible","alerte");
        }
        else
        {
            //Construction mail
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date dateDuJour = new Date();
            String date = dateFormat.format(dateDuJour);

            subject = "PhiMR4 - Réception PUI N°" + commandeSelectionne.getNumero() + " - " + date;
            body = "Madame, Monsieur, \n \n" +
                    "La réception N°" + commandeSelectionne.getNumero() + " a été réalisée. \n" +
                    "Vous pourrez trouver ci-joint le bon de livraison. \n\n" +
                    "Cordialement, \n\n" +
                    "L'équipe Alcyons \n\n" +
                    "Ceci est un message automatique merci de ne pas répondre";

            //Sauvegarde de la signature dans une image
            if (bonLivraisonBitmap != null) {

                dateFormat = new SimpleDateFormat("yyyyMMdd");
                dateDuJour = new Date();
                date = dateFormat.format(dateDuJour);

                bonLivraisonPhotoName = String.valueOf(commandeSelectionne.getNumero()) + "_" + date + "_ReceptionPuiBonLivraison";

                verifyStoragePermissions(DetailReceptionScanneeActivity.this);
            }

            // Récupération Mail Pharmacie
            String email = ParametresServeurOpenHelper.getMailPharmacie(db);
            if(utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS"))
            {
                email = "dev01@alcyons.fr";
            }
            // Demande à l'utilisateur si envoi de copie
            envoyerCopie = Alerte.afficherAlerte(DetailReceptionScanneeActivity.this, "Alerte", "Voulez-vous recevoir une copie du mail ?", "OuiNon");
            if (envoyerCopie) {
                EmailCopie = utilisateurConnecte.getMail();
                if (EmailCopie == null || EmailCopie.contentEquals("")) {
                    envoyerCopie = false;
                }
            }

            if (email != null) {
                new SendEmailTask().execute(email);
            }

            Toast toast = Toast.makeText(DetailReceptionScanneeActivity.this, "Réception effectuée", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            DetailReceptionScanneeActivity.this.finish();


            return;
        }
    }

    public void onMenuSaveClick() {

        if(bonLivraisonBitmap == null && !second_passage_photo)
        {
            second_passage_photo = true;
            prendrePhotoBL();
        }
        else
        {
            second_passage_photo = false;
            Boolean receptionEffectuee = receptionner(commandeSelectionne, phReliquatReceptionPUIAdapteList);
            if (!receptionEffectuee) {
                boolean continuer = false;
                // Si une erreur est survenue, on annule les modifications en vidant la table ElementASynchroniser
                if(erreur.contentEquals("Quantité"))
                {
                    continuer = Alerte.afficherAlerteList(DetailReceptionScanneeActivity.this, "Attention", "Les produits suivant n'ont pas été réceptionnés, continuer ? ", listeProduitRAL, "OuiNon");
                }
                else if(erreur.contentEquals("Lot"))
                {
                    continuer = Alerte.afficherAlerteList(DetailReceptionScanneeActivity.this, "Attention", "Les produits suivant n'ont pas été réceptionnés, continuer ?", listeProduitRAL, "OuiNon");
                }
                else
                {
                    Alerte.afficherAlerte(DetailReceptionScanneeActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
                    ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
                    DetailReceptionScanneeActivity.this.finish();
                }

                if(continuer)
                    valider_reception();

            } else {

                valider_reception();
            }
        }
    }

    private Boolean receptionner(Commande commande, List<PH_Reliquat_ReceptionPUI_Adapte> phReliquatReceptionPUIAdapteList) {

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
                            listeProduitRAL.add(phReliquatCourant.getDesignationCourte()+" - "+String.valueOf(phReliquatCourant.getQteCommande()));
                        }

                        if(numeroLot.contentEquals(""))
                        {
                            erreur = "Lot";
                            int index = listeProduitRAL.indexOf(phReliquatCourant.getDesignationCourte()+" - "+String.valueOf(phReliquatCourant.getQteCommande()));
                            if(index == - 1)
                                listeProduitRAL.add(phReliquatCourant.getDesignationCourte()+" - "+String.valueOf(phReliquatCourant.getQteCommande()));
                        }

                        phReliquatCourant.setLot(numeroLot.trim());
                        phReliquatCourant.setSerie(numero_Serie.trim());
                        phReliquatCourant.setPeremptionDate(datePeremption.trim());
                        phReliquatCourant.setZone(zoneName.trim());
                        phReliquatCourant.setEmplacement(emplacementName.trim());
                        phReliquatCourant.setQteLivraison(quantite);
                        phReliquatCourant.setBL_Numero(bonLivraison);

                        long rowID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, phReliquatCourant);
                        if (rowID != -1) {
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

            // Si possible, on essaie de mettre à jour les éléments
            if (OutilsGestionConnexionReseau.isServerAccessible(DetailReceptionScanneeActivity.this)) {
                ElementASynchroniserOpenHelper.toutSynchroniser(DetailReceptionScanneeActivity.this, db, utilisateurConnecte, true);
            }
            if(listeProduitRAL.size() != 0)
            {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reception_pui);

        //gestion du package manager
        pm = DetailReceptionScanneeActivity.this.getPackageManager();

        commandeSelectionne = CommandeOpenHelper.getCommandeByID(db, intent.getExtras().getInt("commandeID_Selectionne"));

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

        if (orientation == false) {
            prendrePhotoBL();
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
                        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                        {
                            PH_Reliquat courant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionPUIAdapteSelectionne.getPhReliquatUID());
                            int quantiteReliquat = courant.getQteCommande() - courant.getQteLivraison();
                            Produit produit_selectionne = ProduitOpenHelper.getProduitByID(db, courant.getProduitID());
                            Intent detailReceptionPUIIntent = new Intent(DetailReceptionScanneeActivity.this, BarcodeCaptureActivity.class);
                            Bundle detailReceptionPUIBundle = DetailReceptionScanneeActivity.super.getBundle();
                            detailReceptionPUIBundle.putString("contexte", String.valueOf(R.string.scannerContexteFranceMVO));
                            detailReceptionPUIBundle.putBoolean("isBoutonSuppressionExistant", true);
                            detailReceptionPUIBundle.putInt("UserId", utilisateurConnecte.getId());
                            detailReceptionPUIBundle.putInt("qteReliquat", quantiteReliquat);
                            detailReceptionPUIBundle.putInt("reliquat_uid", courant.getReliquat_UID());
                            detailReceptionPUIBundle.putBoolean("modeRafale", true);
                            detailReceptionPUIBundle.putString("GTIN_courant", produit_selectionne.getGTIN());

                            detailReceptionPUIIntent.putExtras(detailReceptionPUIBundle);

                            DetailReceptionScanneeActivity.this.startActivityForResult(detailReceptionPUIIntent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
                        }
                        else
                        {
                            Intent detailReceptionPui_Intent = new Intent(DetailReceptionScanneeActivity.this, ListeLotReceptionScanneeActivity.class);
                            Bundle detailReceptionPui_Bundle = DetailReceptionScanneeActivity.super.getBundle();
                            detailReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                            detailReceptionPui_Bundle.putSerializable("phReliquatReceptionPUIAdapte", phReliquatReceptionPUIAdapteSelectionne);
                            detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);

                            DetailReceptionScanneeActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
                        }
                    }
                    else
                    {
                        Intent detailReceptionPui_Intent = new Intent(DetailReceptionScanneeActivity.this, ListeLotReceptionScanneeActivity.class);
                        Bundle detailReceptionPui_Bundle = DetailReceptionScanneeActivity.super.getBundle();
                        detailReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                        detailReceptionPui_Bundle.putSerializable("phReliquatReceptionPUIAdapte", phReliquatReceptionPUIAdapteSelectionne);
                        detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);

                        DetailReceptionScanneeActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
                    }
                }
            }
        });




        // Récupération da la commande selectionné
        if (commandeSelectionne != null) {
            // Entete
            ((TextView) findViewById(R.id.nomFournisseur)).setText(commandeSelectionne.getFournisseur());
            ((TextView) findViewById(R.id.numCommande)).setText("N° " + String.valueOf(commandeSelectionne.getID_commande()));

            phReliquatList = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeSelectionne.getNumero());

            Collections.sort(phReliquatList, new Comparator<PH_Reliquat>() {
                @Override
                public int compare(PH_Reliquat o1, PH_Reliquat o2) {
                    return o1.getdesignationCourte().compareTo(o2.getdesignationCourte());
                }
            });

            depotPUI = DepotOpenHelper.getDepotPUI(db);

            phReliquatReceptionPUIAdapteList = new ArrayList<>();

            for (PH_Reliquat phReliquatCourant : phReliquatList) {
                // Récupération Zone et Emplacement par défaut du produit en reliquat
                Produit produit = ProduitOpenHelper.getProduitByID(db, phReliquatCourant.getProduitID());
                // Nombre de reliquat
                int zoneId = 0;
                String zoneName = "";
                int emplacementId = 0;
                String emplacementName = "";

                int quantiteReliquat = phReliquatCourant.getQteCommande() - phReliquatCourant.getQteLivraison();

                // Création d'un ph_reliquat adpaté pour la reception PUI
                PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte = new PH_Reliquat_ReceptionPUI_Adapte(phReliquatCourant.getReliquat_UID(), phReliquatCourant.getSerie(), phReliquatCourant.isSuiviParSerieActif(), phReliquatCourant.isSerialiserReception());
                // Création d'un numéro de lot à blanc, en attante de saisie
                PH_Reliquat_ReceptionPUI_Adapte.Lot lot = null;
                if(produit.isSuivi_Lot())
                {
                    lot = phReliquatReceptionPUIAdapte.new Lot("", "00/00/0000", "", "");
                }
                else
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                    Date date = new Date();
                    String new_date = dateFormat.format(date);
                    String numLot = "Phi"+new_date;
                    lot = phReliquatReceptionPUIAdapte.new Lot(numLot, "00/00/0000", "", "");
                }


                if (produit != null) {
                    Depot_Zone depotZone = ZoneOpenHelper.getZoneByDepotEtNom(db, depotPUI, produit.getZone_PUI_Defaut());

                    if (depotZone != null) {
                        zoneId = depotZone.getZoneID();
                        zoneName = depotZone.getZoneName();

                        Depot_Emplacement depotEmplacement = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, depotZone, produit.getEmplacement_PUI_Defaut());
                        if (depotEmplacement != null) {
                            emplacementId = depotEmplacement.get_UID();
                            emplacementName = depotEmplacement.getAdressage();
                        } else {
                            quantiteReliquat = 0;
                        }
                    }
                }

                // Création d'une zone et emplacement par rapport à celle par défaut du produit
                PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionPUIAdapte.new ZoneEtEmplacement(zoneId, zoneName, emplacementId, emplacementName, quantiteReliquat);

                // Insertion dans les objets
                lot.getZoneEtEmplacementList().add(zoneEtEmplacement);
                phReliquatReceptionPUIAdapte.getlotList().add(lot);

                phReliquatReceptionPUIAdapteList.add(phReliquatReceptionPUIAdapte);
            }


            if(!second_passage_photo)
            {
                phReliquatReceptionPuiAdapter = new PH_Reliquat_ReceptionPuiAdapter(DetailReceptionScanneeActivity.this, db, phReliquatReceptionPUIAdapteList);
            }

        } else {
            DetailReceptionScanneeActivity.this.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int nbLotslivres = 0;
        for (PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte : phReliquatReceptionPUIAdapteList) {
            nbLotslivres += phReliquatReceptionPUIAdapte.getlotList().size();
        }

        phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionPuiAdapter);

        invalidateOptionsMenu();
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_LOTS:
                    PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte = phReliquatReceptionPuiAdapter.phReliquatReceptionPUIAdapteList.get(phReliquatReceptionPuiAdapter.viewHolderList.indexOf(phReliquatReceptionPuiViewHolder));
                    phReliquatReceptionPUIAdapte.setlotList((List<PH_Reliquat_ReceptionPUI_Adapte.Lot>) data.getExtras().getSerializable("lotList"));
                    break;
                case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                    String photoProduits = data.getExtras().getString("photoProduit");
                    if (photoProduits == null || photoProduits.contentEquals("")) {
                        // Récupération du bon de livraison
                        bonLivraison = Alerte.afficherAlerteEditText(this, "Bon de livraison", "Veuillez saisir le numéro du bon de livraison s'il vous plaît");
                        bonLivraisonTextView.setText(bonLivraison);
                    } else {
                        try {
                            bonLivraisonBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoProduits));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(second_passage_photo)
                    {
                        onMenuSaveClick();
                    }
                    break;
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    Bundle bundle_temp = data.getExtras();
                    Map<String, String> tableau_renvoyer = (Map<String, String>) bundle_temp.getSerializable("listeString");

                    Intent detailReceptionPui_Intent = new Intent(DetailReceptionScanneeActivity.this, ListeLotReceptionScanneeActivity.class);
                    Bundle detailReceptionPui_Bundle = DetailReceptionScanneeActivity.super.getBundle();
                    detailReceptionPui_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                    detailReceptionPui_Bundle.putInt("UserId", utilisateurConnecte.getId());
                    detailReceptionPui_Bundle.putSerializable("ListeResultat", (Serializable) tableau_renvoyer);
                    detailReceptionPui_Bundle.putSerializable("phReliquatReceptionPUIAdapte", phReliquatReceptionPUIAdapteSelectionne);
                    detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);

                    DetailReceptionScanneeActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
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
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSave);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuSaveClick();
                return true;
            }
        });
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
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

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {
            Mail sender = null;
            if(!envoyerCopie)
            {
                sender = new Mail(DetailReceptionScanneeActivity.this, email[0], true, db);
            }
            else
            {
                sender = new Mail(DetailReceptionScanneeActivity.this, email[0], EmailCopie, true, db);
            }
            try {
                // Envoi du mail avec pdf
                if(bonLivraisonPhotoName.contentEquals(""))
                {
                    sender.sendMailVerification(subject, body);
                }
                else
                {
                    sender.sendMail(subject, body, "Documents/"+bonLivraisonPhotoName + ".jpeg");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent detailReceptionPUIIntent = new Intent(DetailReceptionScanneeActivity.this, ServiceReceptionScanneeActivity.class);
        Bundle detailReceptionPUIBundle = super.getBundle();
        detailReceptionPUIIntent.putExtras(detailReceptionPUIBundle);
        DetailReceptionScanneeActivity.this.startActivity(detailReceptionPUIIntent);
        DetailReceptionScanneeActivity.this.finish();
    }

    public void prendrePhotoBL()
    {
        Intent detailReceptionPui_Intent = new Intent(DetailReceptionScanneeActivity.this, PrisePhoto.class);
        Bundle detailReceptionPui_Bundle = DetailReceptionScanneeActivity.super.getBundle();
        // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
        detailReceptionPui_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        detailReceptionPui_Bundle.putString("CommandeNumero", commandeSelectionne.getNumero());
        detailReceptionPui_Bundle.putString("contexte", "priseDePhotoContexteBonDeLivraison");
        detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);
        DetailReceptionScanneeActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
    }
}
