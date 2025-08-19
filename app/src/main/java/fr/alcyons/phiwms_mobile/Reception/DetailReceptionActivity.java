package fr.alcyons.phiwms_mobile.Reception;

import static com.google.android.gms.vision.L.TAG;

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
import android.os.AsyncTask;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeReceptionActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerReception2025Activity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerReceptionActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.FournisseurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Fournisseur;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Reliquat_ReceptionAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.DetailPreparation2025Activity;
import fr.alcyons.phiwms_mobile.PrisePhoto.PrisePhoto;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServicePreparationPadActivity;
import fr.alcyons.phiwms_mobile.Services.ServicePreparationPufActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceReceptionPadActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceReceptionPuiActivity;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos.verifyStoragePermissions;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailReceptionActivity extends ServiceActivity {
    Commande commandeSelectionne;
    List<PH_Reliquat> phReliquatList;
    List<PH_Reliquat_Reception_Adapte> phReliquatReceptionAdapteList;
    ListView phReliquatListView;
    Depot depotPUI;
    PH_Reliquat_ReceptionAdapter phReliquatReceptionAdapter;
    PH_Reliquat_ReceptionAdapter.PH_Reliquat_ReceptionViewHolder phReliquatReceptionViewHolder;
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
    PH_Reliquat_Reception_Adapte phReliquatReceptionAdapteSelectionne;
    List<String> listeProduitRAL = new ArrayList<>();
    PackageManager pm;
    String tri_choisi;
    LinearLayout lancerScan;
    Depot_Emplacement emplacement_precedent;
    Produit produitPrecedent;
    MenuItem item;
    @SuppressLint("SimpleDateFormat")
    public void valider_reception()
    {
        //on check la connexion à internet pour l'envoie du mail
        boolean internet = checkInternetConnection();
        if(!internet)
        {
            Alerte.afficherAlerte(DetailReceptionActivity.this, "Erreur", "Aucune connexion internet détectée, aucun envoi de mail possible","alerte");
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

            subject = "PhiWMS Mobile - "+depot_destinataire+" - "+commandeSelectionne.getFournisseur()+" - Réception PUI N°" + commandeSelectionne.getNumero() + " - " + date;

            if(commandeSelectionne.getRef_Depot_Dest().contains("-PAD"))
                subject = "PhiWMS Mobile - "+depot_destinataire+" - "+commandeSelectionne.getFournisseur()+" - Réception PAD N°" + commandeSelectionne.getNumero() + " - " + date;

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

                verifyStoragePermissions(DetailReceptionActivity.this);
            }

            // Récupération Mail Pharmacie
            String email = ParametresServeurOpenHelper.getMailPharmacie(db);
            /*if(utilisateurConnecte.getEtablissement().contentEquals("ADH"))
            {
                email = "reception.pui@adh-asso.net";
            }*/
            if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
            {
                email = "dev01@alcyons.fr";
            }
            afficherAlerteConfirmationMail(DetailReceptionActivity.this, LayoutInflater.from(DetailReceptionActivity.this), email);
        }


    }

    public void envoyerMail(boolean copieMail, final String email) throws JSONException {
        if (copieMail) {
            EmailCopie = utilisateurConnecte.getMail();
            if (EmailCopie == null || EmailCopie.contentEquals("")) {
                envoyerCopie = false;
            }
        }

        if (email != null) {
            new SendEmailTask().execute(email);

            /*ExecutorService executor = Executors.newSingleThreadExecutor();
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
            });*/
        }
        if(utilisateurConnecte.getEtablissement().toUpperCase().contentEquals("ARAUCO"))
        {
            FirebaseCrashlytics.getInstance().log("Validation réception ARAUCO");
            FirebaseCrashlytics.getInstance().recordException(new Exception("Exception déclencher à la validation réception"));
        }
        Toast toast = Toast.makeText(DetailReceptionActivity.this, "Réception effectuée", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        if(utilisateurConnecte.getEtablissement().toUpperCase().contentEquals("ADH") || utilisateurConnecte.getEtablissement().toUpperCase().contentEquals("ALCYONS"))
        {
            List<PH_Reliquat> listeReliquatReceptionnee = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeSelectionne.getNumero());

            for(PH_Reliquat reliquat : listeReliquatReceptionnee)
            {
                if(reliquat.getQteLivraison() > 0)
                {
                    envoyerImpressionZebra(reliquat);
                }
            }
        }
        retourService(super.getBundle());
    }


    public void onMenuSaveClick() {
        boolean validationPossible = false;
        List<PH_Reliquat> listePHReliquat = new ArrayList<>();
        listePHReliquat = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeSelectionne.getNumero());
        for(PH_Reliquat courant : listePHReliquat)
        {
            if(courant.getQteLivraison() > 0)
            {
                validationPossible = true;
                break;
            }
        }

        if(validationPossible)
        {
            afficherAlerteConfirmation(DetailReceptionActivity.this, LayoutInflater.from(DetailReceptionActivity.this));
            item.setVisible(false);
        }
        else
        {
            Toast.makeText(DetailReceptionActivity.this, "Aucune ligne à valider", Toast.LENGTH_SHORT).show();
            item.setVisible(true);
        }
    }

    private Boolean receptionner(Commande commande, List<PH_Reliquat_Reception_Adapte> phReliquatReceptionAdapteList) {
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", commande.getID_commande(), "", "Réception PUI");
        if(commandeSelectionne.getRef_Depot_Dest().contains("-PAD"))
            new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", commande.getID_commande(), "", "Réception PAD");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);

        for (PH_Reliquat_Reception_Adapte phReliquatReceptionAdapte : phReliquatReceptionAdapteList) {

            PH_Reliquat phReliquatCourant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionAdapte.getPhReliquatUID());

            if (phReliquatCourant != null) {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, phReliquatCourant.getPhiMR4UUID(), phReliquatCourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.SUPPR);
                PH_ReliquatOpenHelper.supprimerUnPhReliquat(db, phReliquatCourant);

                List<PH_Reliquat> listePHReliquatReceptionne = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, phReliquatCourant.getcommandeNumero(), phReliquatCourant.getProduitID());

                for(PH_Reliquat reliquat_temp : listePHReliquatReceptionne)
                {
                    reliquat_temp.setBL_Numero(bonLivraison);
                    PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquat_temp);

                    /**
                     * TODO : action utilisateur PH_Serialisation
                     * */
                    //    public PH_Serialisation(int _UID, int UserID, String reqType, String ClientTrxId, String ProductCode_VALUE_VA, String ProductCode_SHEME_VA, String Batch_ID_VA, String Batch_EXPDATE_VA, String Pack_SN_VA, String MVT_Type, String MVT_UID, int ProduitUID) {
                    Produit produitCourant = ProduitOpenHelper.getProduitByID(db, reliquat_temp.getProduitID());
                    if(produitCourant.isSuivi_Serialisation() && produitCourant.isSerialiser_Reception_Delivrance())
                    {
                        Random randomserialisation = new Random();
                        int serialisationId = randomserialisation.nextInt();
                        if(serialisationId > 0)
                            serialisationId= serialisationId*-1;

                        String[] datePeremptionTab = reliquat_temp.getPeremptionDate().split("-");
                        String peremptionDate = reliquat_temp.getPeremptionDate();
                        if(datePeremptionTab.length == 3)
                            peremptionDate = datePeremptionTab[0].substring(2)+datePeremptionTab[1]+datePeremptionTab[2];

                        PH_Serialisation serialisation = new PH_Serialisation(serialisationId, utilisateurConnecte.getId(), "G110", "", produitCourant.getGTIN(), "GTIN", reliquat_temp.getLot(), peremptionDate, reliquat_temp.getSerie(), "CDE", phReliquatCourant.getCommandeNumero(), produitCourant.getID_produit());
                        serialisation.setStatut("En attente");
                        serialisation.setRaison("");
                        serialisation.setResultat("");
                        PH_SerialisationOpenHelper.insererPH_SerialisationEnBDD(db, serialisation);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, serialisation.getPhiMR4UUID(), serialisation.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);

                        Random randomAUSeri = new Random();
                        int actionSerId = randomAUSeri.nextInt();
                        if(actionSerId > 0)
                            actionSerId= actionSerId*-1;
                        ActionUtilisateur new_action_utilisateur_serialisation = new ActionUtilisateur(actionSerId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", serialisation.get_UID(), "", "Serialisation");
                        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur_serialisation);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur_serialisation.getPhiMR4UUID(), new_action_utilisateur_serialisation.getId(), DBOpenHelper.ActionsEAS.AJOUT);

                    }
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, reliquat_temp.getPhiMR4UUID(), reliquat_temp.getReliquat_UID(), DBOpenHelper.ActionsEAS.AJOUT);

                    Random randomactionligne = new Random();
                    int actionligneId = randomactionligne.nextInt();
                    if(actionligneId > 0)
                        actionligneId= actionligneId*-1;

                    ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Reliquat", reliquat_temp.getcommandeLigneID(), "", 0, (int)phReliquatCourant.getQteLivraison(), phReliquatCourant.getDesignationCourte());
                    ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionUtilisateur_ligne.getPhiMR4UUID(), actionUtilisateur_ligne.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                }

                /*for (PH_Reliquat_Reception_Adapte.Lot lot : phReliquatReceptionAdapte.getlotList()) {
                    for (PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement : lot.getZoneEtEmplacementList()) {

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

                        if(commandeSelectionne.getRef_Depot_Dest().contains("-PAD"))
                        {
                            phReliquatCourant.setZone("RECEPTION");
                            phReliquatCourant.setEmplacement("RECEPTION-"+commandeSelectionne.getNumero()+"-"+commandeSelectionne.getPatient_identite());
                        }
                        else
                        {
                            phReliquatCourant.setZone(zoneName.trim());
                            phReliquatCourant.setEmplacement(emplacementName.trim());
                        }
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
                }*/
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
            if (statutConnexion) {
                ElementASynchroniserOpenHelper.toutSynchroniser(DetailReceptionActivity.this, db, utilisateurConnecte, true);
            }
            else
            {
                Alerte.afficherAlerte(DetailReceptionActivity.this, "Erreur", "Serveur inaccessible. Votre action sera exécuté ultérieurement", "alerte");
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
        setContentView(R.layout.activity_detail_reception);

        //initilisation de l'emplacement précédent
        emplacement_precedent = null;
        produitPrecedent = null;

        //gestion du package manager
        pm = DetailReceptionActivity.this.getPackageManager();

        commandeSelectionne = CommandeOpenHelper.getCommandeByID(db, Objects.requireNonNull(intent.getExtras()).getInt("commandeID_Selectionne"));

        orientation = false;
        check_lot_present = false;
        second_passage_photo = false;

        //gestion de la rotation de l'écran
        if (savedInstanceState != null) {
            bonLivraison = savedInstanceState.getString("NomBonLivraison");
            phReliquatList = (List<PH_Reliquat>) savedInstanceState.getSerializable("listePHReliquat");
            orientation = savedInstanceState.getBoolean("Orientation");
            phReliquatReceptionAdapteList = (List<PH_Reliquat_Reception_Adapte>) savedInstanceState.getSerializable("listePuiAdapte");
            phReliquatListView = (ListView) findViewById(R.id.liste_view);
            onResume();
        }

        phReliquatListView = (ListView) findViewById(R.id.liste_view);
        phReliquatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                phReliquatReceptionAdapteSelectionne = (PH_Reliquat_Reception_Adapte) phReliquatReceptionAdapter.getItem(position);
                PH_Reliquat reliquatcourant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionAdapteSelectionne.getPhReliquatUID());

                if (phReliquatReceptionAdapteSelectionne != null && reliquatcourant != null) {
                    phReliquatReceptionViewHolder = phReliquatReceptionAdapter.viewHolderList.get(position);

                    List<PH_Reliquat_Reception_Adapte.Lot>liste_lot = phReliquatReceptionAdapteSelectionne.getlotList();
                    for(PH_Reliquat_Reception_Adapte.Lot courant : liste_lot)
                    {
                        if(!courant.getNumeroLot().contentEquals(""))
                        {
                            check_lot_present = true;
                        }
                    }
                    
                    Intent detailReception_Intent = new Intent(DetailReceptionActivity.this, ListeLotReception2025Activity.class);
                    Bundle detailReception_Bundle = DetailReceptionActivity.super.getBundle();
                    detailReception_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                    detailReception_Bundle.putSerializable("phReliquatReceptionAdapte", phReliquatReceptionAdapteSelectionne);
                    detailReception_Bundle.putSerializable("EmplacementPrecedent", emplacement_precedent);
                    detailReception_Bundle.putSerializable("ProduitPrecedent", produitPrecedent);
                    detailReception_Bundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));

                    detailReception_Intent.putExtras(detailReception_Bundle);

                    DetailReceptionActivity.this.startActivityForResult(detailReception_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
                }
            }
        });

        // Récupération da la commande selectionné
        if (commandeSelectionne != null) {
            // Entete
            ((TextView) findViewById(R.id.nomFournisseur)).setText(commandeSelectionne.getFournisseur());
            ((TextView) findViewById(R.id.numCommande)).setText("#" + commandeSelectionne.getNumero());

            phReliquatList = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, commandeSelectionne.getNumero());

            phReliquatList.sort(Comparator.comparing(PH_Reliquat::getdesignationCourte));

            depotPUI = DepotOpenHelper.getDepotPUI(db);

            phReliquatReceptionAdapteList = new ArrayList<>();

            for (PH_Reliquat phReliquatCourant : phReliquatList) {
                if(phReliquatCourant != null)
                {
                    PH_Reliquat_Reception_Adapte phReliquatReceptionAdapte = new PH_Reliquat_Reception_Adapte(phReliquatCourant.getReliquat_UID(), phReliquatCourant.getSerie(), phReliquatCourant.isSuiviParSerieActif(), phReliquatCourant.isSerialiserReception());
                    List<PH_Reliquat_Reception_Adapte.Lot> listeLot = new ArrayList<>();

                    List<PH_Reliquat> listeReliquatReceptionner = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeSelectionne.getNumero(), phReliquatCourant.getProduitID());

                    if(!listeReliquatReceptionner.isEmpty())
                    {
                        for(PH_Reliquat phReliquat : listeReliquatReceptionner)
                        {
                            PH_Reliquat_Reception_Adapte.Lot lotCourant = phReliquatReceptionAdapte.new Lot(phReliquat.getLot(), phReliquat.getPeremptionDate(), phReliquat.getSerie(), "true");
                            List<PH_Reliquat_Reception_Adapte.ZoneEtEmplacement> listeZoneEtEmplacement = new ArrayList<>();
                            String zone_string = "";
                            String emplacement_string = "";
                            int zoneID = 0;
                            int emplacementID = 0;
                            if(commandeSelectionne.getRef_Depot_Dest().contains("-PAD"))
                            {
                                phReliquatCourant.setZone("RECEPTION");
                                zone_string = "RECEPTION";
                                emplacement_string = "RECEPTION-" + commandeSelectionne.getNumero() + "-" + commandeSelectionne.getPatient_identite();
                            }
                            else
                            {
                                Depot_Zone zone = ZoneOpenHelper.getZoneByDepotEtNom(db, depotPUI, phReliquat.getZone());
                                if(zone != null)
                                {
                                    Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone, phReliquat.getEmplacement());
                                    if(emplacement != null)
                                    {
                                        emplacement_string = emplacement.getAdressage();
                                        emplacementID = emplacement.get_UID();
                                    }

                                    zone_string = zone.getZoneName();
                                    zoneID = zone.getZoneID();
                                }
                            }

                            listeZoneEtEmplacement.add(phReliquatReceptionAdapte.new ZoneEtEmplacement(zoneID, zone_string, emplacementID, emplacement_string, phReliquat.getQteLivraison()));
                            lotCourant.setZoneEtEmplacementList(listeZoneEtEmplacement);
                            listeLot.add(lotCourant);
                        }
                    }

                    phReliquatReceptionAdapte.setlotList(listeLot);
                    phReliquatReceptionAdapteList.add(phReliquatReceptionAdapte);
                }
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
            DetailReceptionActivity.this.finish();
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!phReliquatReceptionAdapteList.get(0).getlotList().isEmpty()) {
                    afficherAlerteConfirmationRetour(DetailReceptionActivity.this, LayoutInflater.from(DetailReceptionActivity.this), DetailReceptionActivity.super.getBundle());
                } else {
                    retourService(DetailReceptionActivity.super.getBundle());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        lancerScan = (LinearLayout) findViewById(R.id.lancerScan);
        tri_choisi = "Categorie";

        //gestion du bouton qui lance le scan
        lancerScan.setOnClickListener(view -> {
            Intent listeLotReception_Intent;
            Bundle listeLotReception_Bundle = new Bundle();
            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies")  || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
            {
                listeLotReception_Intent = new Intent(DetailReceptionActivity.this, ScannerReception2025Activity.class);
            }
            else
            {
                listeLotReception_Intent = new Intent(DetailReceptionActivity.this, BarcodeReceptionActivity.class);
            }

            listeLotReception_Bundle.putString("contexte", String.valueOf(R.string.scannerContextReceptionListe));
            listeLotReception_Bundle.putInt("ReceptionID", commandeSelectionne.getID_commande());
            listeLotReception_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            listeLotReception_Bundle.putString("ordreTri", tri_choisi);
            listeLotReception_Bundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));
            listeLotReception_Bundle.putSerializable("ReceptionPUIAdapte", (Serializable) phReliquatReceptionAdapteList);
            listeLotReception_Bundle.putSerializable("EmplacementPrecedent", (Serializable) emplacement_precedent);
            listeLotReception_Bundle.putSerializable("ProduitPrecedent", (Serializable) produitPrecedent);
            listeLotReception_Intent.putExtras(listeLotReception_Bundle);
            DetailReceptionActivity.this.startActivityForResult(listeLotReception_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
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
                    if(phReliquatReceptionAdapter.viewHolderList.contains(phReliquatReceptionViewHolder))
                    {
                        PH_Reliquat_Reception_Adapte phReliquatReceptionAdapte = phReliquatReceptionAdapter.phReliquatReceptionAdapteList.get(phReliquatReceptionAdapter.viewHolderList.indexOf(phReliquatReceptionViewHolder));
                        phReliquatReceptionAdapte.setlotList((List<PH_Reliquat_Reception_Adapte.Lot>) data.getExtras().getSerializable("lotList"));
                        emplacement_precedent = (Depot_Emplacement) data.getExtras().getSerializable("EmplacementPrecedent");
                        produitPrecedent = (Produit) data.getExtras().getSerializable("ProduitPrecedent");
                        enregistrerPhReliquat(phReliquatReceptionAdapte);
                        onResume();
                    }
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
                    phReliquatReceptionAdapteList = new ArrayList<>();
                    emplacement_precedent = (Depot_Emplacement) Objects.requireNonNull(data.getExtras()).getSerializable("EmplacementPrecedent");
                    produitPrecedent = (Produit) data.getExtras().getSerializable("ProduitPrecedent");
                    phReliquatReceptionAdapteList = (List<PH_Reliquat_Reception_Adapte>) data.getExtras().getSerializable("reliquatAdapteList");
                    for(PH_Reliquat_Reception_Adapte phReliquatReceptionAdapte : phReliquatReceptionAdapteList)
                    {
                        enregistrerPhReliquat(phReliquatReceptionAdapte);
                    }
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
        item = menu.findItem(R.id.menuSave);
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

        outstate.putSerializable("listePuiAdapte", (Serializable) phReliquatReceptionAdapteList);

        //gestion de la liste de retour ligne pour gérer le changement de valeur
        outstate.putSerializable("listePHReliquat", (Serializable) phReliquatList);

        super.onSaveInstanceState(outstate);
    }

    private Object envoyerMail(String email)
    {
        Mail sender;
        if(!envoyerCopie)
        {
            sender = new Mail(DetailReceptionActivity.this, email, false, db, utilisateurConnecte);
        }
        else
        {
            sender = new Mail(DetailReceptionActivity.this, email, EmailCopie, false, db, utilisateurConnecte);
        }
        try {
            // Envoi du mail avec pdf
            if(bonLivraisonPhotoName.contentEquals(""))
            {
                try {
                    sender.sendMail(subject, body, "");
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
            Log.e("Exception", e.getMessage());
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


    public void prendrePhotoBL()
    {
        Intent detailReception_Intent = new Intent(DetailReceptionActivity.this, PrisePhoto.class);
        Bundle detailReception_Bundle = DetailReceptionActivity.super.getBundle();
        // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
        detailReception_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        detailReception_Bundle.putString("CommandeNumero", commandeSelectionne.getNumero());
        detailReception_Bundle.putString("contexte", "priseDePhotoContexteBonDeLivraison");
        detailReception_Intent.putExtras(detailReception_Bundle);
        DetailReceptionActivity.this.startActivityForResult(detailReception_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
    }

    private void onClickTriDesignation()
    {
        tri_choisi = "Designation";
        phReliquatReceptionAdapteList.sort((o1, o2) -> {

            PH_Reliquat oo1 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o1.getPhReliquatUID());
            PH_Reliquat oo2 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o2.getPhReliquatUID());

            if(oo2 == null || oo1 == null)
                return 1;
            else
                return oo1.getDesignationCourte().toLowerCase().compareTo(oo2.getDesignationCourte().toLowerCase());

        });
        phReliquatReceptionAdapter = new PH_Reliquat_ReceptionAdapter(DetailReceptionActivity.this, db, phReliquatReceptionAdapteList);
        phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionAdapter);
    }

    private void onClickTriCategorie()
    {
        tri_choisi = "Categorie";
        phReliquatReceptionAdapteList.sort((o1, o2) -> {

            PH_Reliquat oo1 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o1.getPhReliquatUID());
            PH_Reliquat oo2 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o2.getPhReliquatUID());

            if(oo1 == null || oo2 == null)
            {
                return 1;
            }
            else
            {
                Produit produit1 = ProduitOpenHelper.getProduitByID(db, oo1.getProduitID());
                Produit produit2 = ProduitOpenHelper.getProduitByID(db, oo2.getProduitID());

                return produit1.getCategorie().toLowerCase().compareTo(produit2.getCategorie().toLowerCase());
            }
        });
        phReliquatReceptionAdapter = new PH_Reliquat_ReceptionAdapter(DetailReceptionActivity.this, db, phReliquatReceptionAdapteList);
        phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionAdapter);
    }

    private void onTriParPlace()
    {
        tri_choisi = "Place";
        phReliquatReceptionAdapteList.sort((o1, o2) -> {

            PH_Reliquat oo1 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o1.getPhReliquatUID());
            PH_Reliquat oo2 = PH_ReliquatOpenHelper.getPH_ReliquatById(db, o2.getPhReliquatUID());

            if(oo2 == null || oo1 == null)
                return 1;
            else
            {
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
            }
        });
        phReliquatReceptionAdapter = new PH_Reliquat_ReceptionAdapter(DetailReceptionActivity.this, db, phReliquatReceptionAdapteList);
        phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionAdapter);
    }

    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_reception_pui, null);

        final LinearLayout buttonOk = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout zonefermer = (LinearLayout) layout.findViewById(R.id.fermer_alerte_reception);
        final EditText numeroBLEdit = (EditText) layout.findViewById(R.id.numeroBL);
        final ImageView btn_photo = (ImageView) layout.findViewById(R.id.btnPhoto);
        final ImageView iconValidation = (ImageView) layout.findViewById(R.id.iconValidation);
        final LinearLayout boutonPhoto = (LinearLayout) layout.findViewById(R.id.boutonPhoto);

        Rect displayRectangle = new Rect();
        Window window = DetailReceptionActivity.this.getWindow();
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.setCancelable(false);
        alertDialog.show();

        zonefermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                item.setVisible(true);
            }
        });

        buttonOk.setOnClickListener(v -> {

            if(utilisateurConnecte.getEtablissement().toUpperCase().contentEquals("ARAUCO"))
            {
                FirebaseCrashlytics.getInstance().log("Bouton validation réception par l'utilisateur ARAUCO");
                FirebaseCrashlytics.getInstance().recordException(new Exception("Exception simulée après clic validation réception"));
            }

            buttonOk.setBackgroundColor(getResources().getColor(R.color.vert, null));
            ViewCompat.setBackgroundTintList(iconValidation, ColorStateList.valueOf(getResources().getColor(R.color.blanc, null)));
            String numeroBL = numeroBLEdit.getText().toString();
            commandeSelectionne.setBLNumero(numeroBL);
            bonLivraison = numeroBL;
            alertDialog.dismiss();
            //on vérifie qu'une saisie a été effectué avant d'enregistrer
            boolean saisie_effectuer = false;
            for (PH_Reliquat_Reception_Adapte phReliquatReceptionAdapte : phReliquatReceptionAdapteList)
            {
                for (PH_Reliquat_Reception_Adapte.Lot lot : phReliquatReceptionAdapte.getlotList()) {
                    for (PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement : lot.getZoneEtEmplacementList())
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
                Boolean receptionEffectuee = receptionner(commandeSelectionne, phReliquatReceptionAdapteList);
                if (!receptionEffectuee) {
                    boolean continuer = false;
                    // Si une erreur est survenue, on annule les modifications en vidant la table ElementASynchroniser
                    if(erreur.contentEquals("Quantité"))
                    {
                        continuer = Alerte.afficherAlerteList(DetailReceptionActivity.this, "Attention", "Les produits suivant n'ont pas été réceptionnés, continuer ? ", listeProduitRAL, "OuiNon");
                    }
                    else if(erreur.contentEquals("Lot"))
                    {
                        continuer = Alerte.afficherAlerteList(DetailReceptionActivity.this, "Attention", "Les produits suivant n'ont pas été réceptionnés, continuer ?", listeProduitRAL, "OuiNon");
                    }
                    else
                    {
                        Alerte.afficherAlerte(DetailReceptionActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
                        ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
                        DetailReceptionActivity.this.finish();
                    }

                    if(continuer)
                        valider_reception();

                } else {

                    valider_reception();
                }
            }
            else
            {
                //onBackPressed();
            }
        });

        boutonPhoto.setOnClickListener(v -> {
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
            try {
                envoyerMail(true, email);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        buttonAnnuler.setOnClickListener(v -> {
            alertDialog.dismiss();
            try {
                envoyerMail(false, email);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
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
        Intent detailReceptionIntent = new Intent(DetailReceptionActivity.this, ServiceReceptionPuiActivity.class);
        if(commandeSelectionne.getRef_Depot_Dest().contains("-PAD"))
            detailReceptionIntent = new Intent(DetailReceptionActivity.this, ServiceReceptionPadActivity.class);  
        detailReceptionIntent.putExtras(bundle);
        DetailReceptionActivity.this.startActivity(detailReceptionIntent);
        DetailReceptionActivity.this.finish();
    }

    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(DetailReceptionActivity.this, "dev01@alcyons.fr", true, db, utilisateurConnecte);
            try {
                // Envoi du mail avec pdf
                //sender.sendMailVerification(subject, body);
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

    private void enregistrerPhReliquat(PH_Reliquat_Reception_Adapte reliquatReceptionAdapte)
    {
        listeProduitRAL = new ArrayList<>();
        PH_Reliquat ph_reliquat_base = PH_ReliquatOpenHelper.getPH_ReliquatById(db, reliquatReceptionAdapte.getPhReliquatUID());

        List<PH_Reliquat> listeReliquatAjouter = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeSelectionne.getNumero(), ph_reliquat_base.getProduitID());
        for(PH_Reliquat reliquatcourant : listeReliquatAjouter)
        {
            PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquatcourant);
            //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, reliquatcourant.getPhiMR4UUID(), reliquatcourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.SUPPR);
        }

        for(PH_Reliquat_Reception_Adapte.Lot lot : reliquatReceptionAdapte.getlotList()) {
            for (PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement : lot.getZoneEtEmplacementList()) {
                Random randomreliquat = new Random();
                int reliquatId = randomreliquat.nextInt();
                if (reliquatId > 0)
                    reliquatId = reliquatId * -1;

                PH_Reliquat phReliquatCourant = ph_reliquat_base;
                phReliquatCourant.setReliquat_UID(reliquatId);
                String numeroLot = lot.getNumeroLot();
                String datePeremption = lot.getDatePeremption();
                String[] datePeremptionTab = datePeremption.split("/");
                if(datePeremptionTab.length == 3)
                    datePeremption = datePeremptionTab[2] + "-" + datePeremptionTab[1] + "-" + datePeremptionTab[0];
                String zoneName = zoneEtEmplacement.getZoneName();
                String emplacementName = zoneEtEmplacement.getEmplacementName();
                String numero_Serie = lot.getNumero_serie();
                int quantite = zoneEtEmplacement.getQuantite();

                if (quantite == 0) {
                    erreur = "Quantité";
                    listeProduitRAL.add(phReliquatCourant.getDesignationCourte() + " - " + phReliquatCourant.getQteCommande());
                }

                if (numeroLot.contentEquals("")) {
                    erreur = "Lot";
                    int index = listeProduitRAL.indexOf(phReliquatCourant.getDesignationCourte() + " - " + phReliquatCourant.getQteCommande());
                    if (index == -1)
                        listeProduitRAL.add(phReliquatCourant.getDesignationCourte() + " - " + phReliquatCourant.getQteCommande());
                }

                phReliquatCourant.setLot(numeroLot.trim());
                phReliquatCourant.setSerie(numero_Serie.trim());
                phReliquatCourant.setPeremptionDate(datePeremption.trim());

                if (commandeSelectionne.getRef_Depot_Dest().contains("-PAD")) {
                    phReliquatCourant.setZone("RECEPTION");
                    phReliquatCourant.setEmplacement("RECEPTION-" + commandeSelectionne.getNumero() + "-" + commandeSelectionne.getPatient_identite());
                } else {
                    phReliquatCourant.setZone(zoneName.trim());
                    phReliquatCourant.setEmplacement(emplacementName.trim());
                }
                phReliquatCourant.setQteLivraison(quantite);
                phReliquatCourant.setBL_Numero(bonLivraison);
                phReliquatCourant.setScanValue("");

                long rowID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, phReliquatCourant);
                if(rowID != -1)
                {
                    //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, phReliquatCourant.getPhiMR4UUID(), phReliquatCourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                }
            }
        }

        //ElementASynchroniserOpenHelper.toutSynchroniser(DetailReceptionActivity.this, db, utilisateurConnecte, false);
    }

    private void envoyerImpressionZebra(PH_Reliquat reliquatCourant) throws JSONException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(cal.getTime());

        JSONArray Etiquette_TO = new JSONArray();

        String designationProduit = reliquatCourant.getDesignationCourte();
        String numeroLot = reliquatCourant.getLot();
        String numeroSerie = reliquatCourant.getSerie();
        String datePeremption = reliquatCourant.getPeremptionDate();

        if(designationProduit.length() > 30)
            designationProduit = designationProduit.substring(0, 30);

        JSONObject codeBarrJO = new JSONObject();
        codeBarrJO.put("type", "Datamatrix");
        codeBarrJO.put("phitag", "PHIBCF:"+commandeSelectionne.getNumero());

        JSONObject etiquette_v1_JO = new JSONObject();
        etiquette_v1_JO.put("codeBarre", codeBarrJO);
        etiquette_v1_JO.put("nomfournisseur", commandeSelectionne.getFournisseur());
        etiquette_v1_JO.put("phiTag", commandeSelectionne.getNumero());
        etiquette_v1_JO.put("date", strDate);
        etiquette_v1_JO.put("designation", designationProduit);
        etiquette_v1_JO.put("qtereceptionne",reliquatCourant.getQteLivraison());
        etiquette_v1_JO.put("numlot", numeroLot);
        etiquette_v1_JO.put("dateperemption", datePeremption);

        Etiquette_TO.put(etiquette_v1_JO);

        String imprimante_VT = "Zebra";
        String aImprimer = "true";
        String format = "Réception";

        JSONObject body = new JSONObject();
        try {
            body.put("Imprimante", imprimante_VT);
            body.put("aImprimer", aImprimer);
            body.put("format", format);
            body.put("etiquettes", Etiquette_TO);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException :", e);
        }
        String urlRequete = ParametresServeurOpenHelper.getUrlsWeb(db) + DBOpenHelper.Urls.uriZebraImprimer;
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
            Toast.makeText(DetailReceptionActivity.this, "Etiquette envoyée", Toast.LENGTH_SHORT).show();
        },
                error -> {
                    Log.e("Etiquette Volley", error.toString());
                    if(!error.toString().contains("\"isOk\":true"))
                    {
                        Alerte.afficherAlerte(DetailReceptionActivity.this, "Erreur HTTP", "Erreur lors de l\'impression de l\'étiquette : "+error.toString(), "alerte");
                    }
                    else
                    {
                        Toast.makeText(DetailReceptionActivity.this, "Etiquette envoyée", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json;charset=utf-8");
                return params;
            }
        };
        RequestQueue requestQueueUtilisateur = Volley.newRequestQueue(this);
        requestQueueUtilisateur.add(obreq);
    }
}
