package fr.alcyons.phiwms_mobile.ReceptionPAD;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionPAD;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ReceptionPADExpandableAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.PrisePhoto.PrisePhoto;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceReceptionPadActivity;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos.verifyStoragePermissions;

public class DetailReceptionPadActivity extends ServiceActivity {

    Bitmap bonLivraisonBitmap;
    List<String> GTIN_Scannee; //a supprimer
    List<Integer> list_id_produit;
    ExpandableListView liste_viewProduitReceptionPAD;
    boolean second_passage_photo;
    String photoProduitsChemin;
    List<ObjetReceptionScannee> listObjet_scanne;

    ReceptionPADExpandableAdapter receptionPADExpandableAdapter;
    List<String> listeDesignation;
    List<ObjetReceptionScannee> list_same_designation;
    Map<ObjetReceptionPAD, List<ObjetReceptionScannee>> mapAdapter;
    PackageManager pm;
    ActionUtilisateur action;
    int id_commande;
    Commande commandeCourante;
    List<PH_Reliquat> listeReliquat;
    List<Integer> liste_id_reliquat;
    List<ObjetReceptionPAD> liste_objetReception;
    String commandeSelectNumero;
    TextView nomReceptionPAD;
    TextView numeroCommande;
    LinearLayout linearBouton;
    Button boutonAddByScan;
    Button boutonAddByManuel;

    boolean enregistrer;

    //gestion des variables des mails
    boolean envoyerCopie;
    String EmailCopie;
    String bonLivraisonPhotoName;
    String body;
    String subject;
    String numeroBonLivraison;

    boolean premierPassage;

    LinearLayout lancerScan;

    /**
     * TODO : pour prendre plusieurs photo d'un bon de livraison (recto-verso)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_produit_reception_scannee);

        //initialisation des variables
        enregistrer = false;
        GTIN_Scannee = new ArrayList<>();
        liste_id_reliquat = new ArrayList<>();
        photoProduitsChemin = "chemin";
        second_passage_photo = false;
        listObjet_scanne = new ArrayList<>();
        liste_objetReception = new ArrayList<>();
        list_id_produit = new ArrayList<>();
        listeDesignation = new ArrayList<>();
        mapAdapter = new LinkedHashMap<>();
        list_same_designation = new ArrayList<>();
        listeReliquat = new ArrayList<>();
        liste_id_reliquat = new ArrayList<>();
        //initialisation des objets graphique
        liste_viewProduitReceptionPAD = (ExpandableListView) findViewById(R.id.liste_viewProduitReceptionScannee);

        //récupération de l'id de la commande
        id_commande = intent.getExtras().getInt("commandeID_Selectionne");
        commandeCourante = CommandeOpenHelper.getCommandeByID(db,id_commande);

        //affichage du nom de la réception en cours
        nomReceptionPAD = (TextView) findViewById(R.id.nomReceptionPAD);
        numeroCommande = (TextView) findViewById(R.id.numeroCommande);
        linearBouton = (LinearLayout) findViewById(R.id.linearBouton);
        boutonAddByScan = (Button) findViewById(R.id.boutonAddByScan);
        boutonAddByManuel = (Button) findViewById(R.id.boutonAddByManuel);
        lancerScan = (LinearLayout) findViewById(R.id.lancerScan);

        //gestion des objets graphiques
        boutonAddByScan.setOnClickListener(view -> lancerScanProduit());

        boutonAddByManuel.setOnClickListener(view -> onMenuAddClick());

        lancerScan.setOnClickListener(v -> lancerScanProduit());

        nomReceptionPAD.setText(commandeCourante.getFournisseur());
        numeroCommande.setText(commandeCourante.getNumero());

        //création de l'action
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;

        action = new ActionUtilisateur(actionId);

        pm = DetailReceptionPadActivity.this.getPackageManager();
        premierPassage = true;

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                afficherAlerteConfirmation(DetailReceptionPadActivity.this, DetailReceptionPadActivity.this.getLayoutInflater());
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(commandeCourante != null)
        {
            commandeSelectNumero = commandeCourante.getNumero();
            listeReliquat = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeCourante.getNumero());
            if(!listeReliquat.isEmpty())
            {
                for(PH_Reliquat reliquat : listeReliquat)
                {
                    liste_id_reliquat.add(reliquat.getReliquat_UID());
                }
            }
        }

        if(mapAdapter.isEmpty() && premierPassage)
        {
            linearBouton.setVisibility(View.VISIBLE);
            liste_viewProduitReceptionPAD.setVisibility(View.GONE);
            boutonAddByManuel.performClick();
            premierPassage = false;
        }
        else
        {
            if(liste_objetReception.isEmpty())
            {
                Intent intent = new Intent(DetailReceptionPadActivity.this, ServiceReceptionPadActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                extras.putInt("serviceSelectionneID", serviceActuel.getId());
                intent.putExtras(extras);
                DetailReceptionPadActivity.this.startActivity(intent);
                DetailReceptionPadActivity.this.finish();
            }
            else
            {
                linearBouton.setVisibility(View.GONE);
                liste_viewProduitReceptionPAD.setVisibility(View.VISIBLE);
                receptionPADExpandableAdapter = new ReceptionPADExpandableAdapter(DetailReceptionPadActivity.this, db, liste_objetReception, mapAdapter, utilisateurConnecte);
                liste_viewProduitReceptionPAD.setAdapter(receptionPADExpandableAdapter);
                liste_viewProduitReceptionPAD.setDivider(footer);
                expandAll();
            }
        }
    }

    public void supprimerScan(final int groupKey, final int ChildKey)
    {

        boolean confirmer = Alerte.afficherAlerte(DetailReceptionPadActivity.this, "Confirmation", "Souhaitez-vous supprimer la ligne sélectionnée ?", "OuiNon");
        if (confirmer) {
            ObjetReceptionPAD key = getKeyMap(mapAdapter, groupKey);
            List<ObjetReceptionScannee> list_group_select = mapAdapter.get(key);
            ObjetReceptionScannee objet_a_supprimer = list_group_select.get(ChildKey);
            list_group_select.remove(ChildKey);
            key.setQuantite_receptionner(key.getQuantite_receptionner()-objet_a_supprimer.getQuantiteScannee());
            ArrayList<Integer> index_suppression = new ArrayList<>();
            int index = -1;
            int index_produit = -1;
            for(ObjetReceptionScannee courant : listObjet_scanne)
            {
                index ++;
                if(courant.getGs1_scannee().contentEquals(objet_a_supprimer.getGs1_scannee()))
                {
                    index_suppression.add(index);
                    if(index_produit == -1)
                    {
                        index_produit = index;
                    }
                }
            }
            String gs1_remove = listObjet_scanne.get(index).getGs1_scannee();
            int index_gs1 = GTIN_Scannee.indexOf(gs1_remove);
            GTIN_Scannee.remove(index_gs1);
            for(int i = index_suppression.size()-1; i >= 0; i--)
            {
                int index_courant = index_suppression.get(i);
                listObjet_scanne.remove(index_courant);
            }
            list_id_produit.remove(index_produit);

            Map<String, String> gs1decoupe = OutilsDecodage.decouperGTIN(objet_a_supprimer.getGs1_scannee());
            Produit produitcourant = null;
            if(gs1decoupe.size() != 1 && !objet_a_supprimer.getGs1_scannee().startsWith("ci"))
            {
                produitcourant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1decoupe.get(OutilsDecodage.codeGtin));
            }
            else
            {
                String codeinconnu = objet_a_supprimer.getGs1_scannee();
                if(codeinconnu.startsWith("ci"))
                {
                    Map<String, String> mapInconnu = OutilsDecodage.decouperCodeInconnnu(objet_a_supprimer.getGs1_scannee());
                    codeinconnu = mapInconnu.get("Code_Inconnu");
                }

                produitcourant = ProduitOpenHelper.getUnProduitByCodeInconnu(db, codeinconnu);
            }
            if (list_group_select.size() == 0) {
                listeDesignation.remove(groupKey);
                List<Integer> index_a_supprimer = new ArrayList<>();
                for(int i = 0; i < liste_objetReception.size(); i++)
                {
                    ObjetReceptionPAD objetcourant = liste_objetReception.get(i);

                    if(produitcourant != null)
                    {
                        if(produitcourant.getDesignation_ext().contentEquals(objetcourant.getDesignation_produit()) || produitcourant.getDesignation_interne().contentEquals(objetcourant.getDesignation_produit()))
                        {
                            for(PH_Reliquat reliquat : listeReliquat)
                            {
                                if(reliquat.getProduitID() == produitcourant.getID_produit())
                                {
                                    reliquat.setQteReliquat_X(reliquat.getQteReliquat_X()+objet_a_supprimer.getQuantiteScannee());
                                    PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquat);
                                    break;
                                }
                            }
                            index_a_supprimer.add(i);
                        }
                    }
                }

                for(Integer index_courant : index_a_supprimer)
                {
                    liste_objetReception.remove((int)index_courant);
                }

                mapAdapter.remove(key);
            }
            else
            {
                for(PH_Reliquat reliquat : listeReliquat)
                {
                    if(reliquat.getProduitID() == produitcourant.getID_produit())
                    {
                        reliquat.setQteReliquat_X(reliquat.getQteReliquat_X()+objet_a_supprimer.getQuantiteScannee());
                        PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquat);
                        break;
                    }
                }
            }
        }
        onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    if(data != null)
                    {
                        if(data.getExtras().getBoolean("lancerScan", false))
                        {
                            lancerScanProduit();
                        }
                        else
                        {
                            listObjet_scanne = new ArrayList<>();
                            listObjet_scanne = (List<ObjetReceptionScannee>) data.getExtras().getSerializable("listeString");
                            if(listObjet_scanne != null && listObjet_scanne.size()!= 0)
                            {

                                Collections.sort(listObjet_scanne, new Comparator<ObjetReceptionScannee>(){
                                    public int compare(ObjetReceptionScannee obj1, ObjetReceptionScannee obj2) {
                                        // ## Ascending order
                                        return obj1.getGs1_scannee().compareToIgnoreCase(obj2.getGs1_scannee()); // To compare string values

                                    }
                                });


                                mapAdapter = new LinkedHashMap<>();
                                liste_objetReception = new ArrayList<>();
                                GTIN_Scannee = new ArrayList<>();
                                liste_viewProduitReceptionPAD.setAdapter((BaseExpandableListAdapter)null);
                                listeDesignation = new ArrayList<>();
                                list_same_designation = new ArrayList<>();
                                boolean premier = true;

                                List<ObjetReceptionScannee> listTempParLot = new ArrayList<>();
                                for(ObjetReceptionScannee objetCourant : listObjet_scanne)
                                {
                                    GTIN_Scannee.add(objetCourant.getGs1_scannee());

                                    if(premier)
                                    {
                                        listTempParLot.add(objetCourant);
                                        premier = false;
                                    }
                                    else
                                    {
                                        Map<String, String> gs1DecoupeCourant = OutilsDecodage.decouperGTIN(objetCourant.getGs1_scannee());
                                        String lotCourant = gs1DecoupeCourant.get(OutilsDecodage.numeroLot);
                                        String gtinCourant = gs1DecoupeCourant.get(OutilsDecodage.codeGtin);
                                        if(lotCourant == null)
                                        {
                                            lotCourant = "";
                                        }

                                        if(gtinCourant == null)
                                        {
                                            gtinCourant = "";
                                        }

                                        int quantiteCourant = objetCourant.getQuantiteScannee();
                                        int uidEmplacementCourant = objetCourant.getEmplacement_uid();
                                        int index = 0;
                                        boolean trouver = false;
                                        ObjetReceptionScannee objetAAjouter = new ObjetReceptionScannee(objetCourant);
                                        for(ObjetReceptionScannee objetTemp : listTempParLot)
                                        {
                                            Map<String, String> gs1DecoupeTemp = OutilsDecodage.decouperGTIN(objetTemp.getGs1_scannee());
                                            String lotTemp = gs1DecoupeTemp.get(OutilsDecodage.numeroLot);
                                            String gtinTemp = gs1DecoupeTemp.get(OutilsDecodage.codeGtin);
                                            if(lotTemp == null)
                                            {
                                                Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(objetTemp.getGs1_scannee());
                                                lotTemp = MapInconnu.get("Lot");
                                            }

                                            if(gtinTemp == null)
                                            {
                                                Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(objetTemp.getGs1_scannee());
                                                gtinTemp = MapInconnu.get("Code_Inconnu");
                                            }


                                            int quantiteTemp = objetTemp.getQuantiteScannee();
                                            int uidEmplacementTemp = objetTemp.getEmplacement_uid();


                                            if(lotTemp.contentEquals(lotCourant) && gtinTemp.contentEquals(gtinCourant) && uidEmplacementTemp == uidEmplacementCourant)
                                            {
                                                objetAAjouter.setQuantiteScannee(quantiteCourant+quantiteTemp);

                                                trouver =true;
                                                break;
                                            }
                                            index ++;
                                        }
                                        listTempParLot.add(objetAAjouter);
                                        if(trouver)
                                        {
                                            listTempParLot.remove(index);
                                        }
                                    }
                                }

                                premier = true;
                                int size = listTempParLot.size();
                                int iteration = 0;
                                String ancienneDesignation = "";
                                Produit ancienProduit = null;
                                PH_Reliquat ancienReliquat = null;
                                for(ObjetReceptionScannee objetReceptionScannee_courant : listTempParLot)
                                {
                                    iteration ++;
                                    //gestion de la liste des produits scannee
                                    Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(objetReceptionScannee_courant.getGs1_scannee());
                                    Produit produit_courant = null;
                                    if(gs1Decoupe.size() != 1)
                                    {
                                        produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                                    }
                                    else
                                    {
                                        String code_inconnu = objetReceptionScannee_courant.getGs1_scannee();
                                        if(code_inconnu.startsWith("ci"))
                                        {
                                            Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(objetReceptionScannee_courant.getGs1_scannee());
                                            code_inconnu = MapInconnu.get("Code_Inconnu");
                                        }

                                        List<Produit> list = ProduitOpenHelper.getProduitByCodeInconnu(db, code_inconnu);
                                        if(list.size() == 1)
                                            produit_courant = list.get(0);
                                    }

                                    if(produit_courant != null)
                                    {
                                        list_id_produit.add(produit_courant.getID_produit());
                                    }

                                    //Récupération des emplacements
                                    if(produit_courant != null)
                                    {
                                        //récupération du ph_Reliquat courant
                                        PH_Reliquat reliquat_courant = null;
                                        for(Integer id_reliquat_courant : liste_id_reliquat)
                                        {
                                            PH_Reliquat reliquat_temp = PH_ReliquatOpenHelper.getPH_ReliquatById(db, id_reliquat_courant);
                                            if(reliquat_temp.getProduitID() == produit_courant.getID_produit())
                                            {
                                                reliquat_courant = reliquat_temp;
                                                break;
                                            }
                                        }

                                        String designation = produit_courant.getDesignation_interne();
                                        if(iteration == size)
                                        {
                                            if(listeDesignation.indexOf(designation) != -1)
                                            {

                                                if(!ancienneDesignation.contentEquals("") && list_same_designation.size()!=0)
                                                {
                                                    int quantitereceptionner = ancienReliquat.getQteCommande()-ancienReliquat.getQteReliquat_X();
                                                    ObjetReceptionPAD objetReceptionPAD = new ObjetReceptionPAD(ancienProduit.getDesignation_interne(), ancienReliquat.getQteCommande(), quantitereceptionner, ancienReliquat.getProduit_Reference());
                                                    liste_objetReception.add(objetReceptionPAD);
                                                    mapAdapter.put(objetReceptionPAD, list_same_designation);
                                                }

                                                for (Map.Entry<ObjetReceptionPAD, List<ObjetReceptionScannee>>entry : mapAdapter.entrySet()) {
                                                    ObjetReceptionPAD key = entry.getKey();
                                                    String designation_courante = key.getDesignation_produit();
                                                    List<ObjetReceptionScannee> list = entry.getValue();
                                                    if(designation_courante.contentEquals(designation))
                                                    {
                                                        list_same_designation = new ArrayList<>();
                                                        list_same_designation.addAll(list);
                                                        list_same_designation.add(objetReceptionScannee_courant);
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                if(!ancienneDesignation.contentEquals("") && list_same_designation.size()!=0)
                                                {
                                                    int quantitereceptionner = ancienReliquat.getQteCommande()-ancienReliquat.getQteReliquat_X();
                                                    ObjetReceptionPAD objetReceptionPAD = new ObjetReceptionPAD(ancienProduit.getDesignation_interne(), ancienReliquat.getQteCommande(), quantitereceptionner, ancienReliquat.getProduit_Reference());
                                                    liste_objetReception.add(objetReceptionPAD);
                                                    mapAdapter.put(objetReceptionPAD, list_same_designation);
                                                }
                                                list_same_designation = new ArrayList<>();
                                                list_same_designation.add(objetReceptionScannee_courant);
                                                listeDesignation.add(designation);
                                            }
                                            ancienProduit = produit_courant;
                                            ancienReliquat = reliquat_courant;
                                            int quantitereceptionner = reliquat_courant.getQteCommande()-reliquat_courant.getQteReliquat_X();
                                            ObjetReceptionPAD objetReceptionPAD = new ObjetReceptionPAD(produit_courant.getDesignation_interne(), reliquat_courant.getQteCommande(), quantitereceptionner, ancienReliquat.getProduit_Reference());
                                            boolean trouver = false;
                                            for (Map.Entry<ObjetReceptionPAD, List<ObjetReceptionScannee>>entry : mapAdapter.entrySet()) {
                                                ObjetReceptionPAD key = entry.getKey();
                                                List<ObjetReceptionScannee> list = entry.getValue();
                                                if(key.getDesignation_produit().contentEquals(objetReceptionPAD.getDesignation_produit()) && key.getQuantite_commander() == objetReceptionPAD.getQuantite_commander() && key.getQuantite_receptionner() == objetReceptionPAD.getQuantite_receptionner())
                                                {
                                                    objetReceptionPAD = key;
                                                    trouver = true;
                                                    break;
                                                }
                                            }

                                            if(!trouver)
                                            {
                                                liste_objetReception.add(objetReceptionPAD);
                                            }

                                            mapAdapter.put(objetReceptionPAD, list_same_designation);
                                        }
                                        else if(!premier)
                                        {
                                            if(listeDesignation.indexOf(designation) != -1)
                                            {
                                                list_same_designation.add(objetReceptionScannee_courant);
                                            }
                                            else
                                            {
                                                listeDesignation.add(designation);
                                                if(!ancienneDesignation.contentEquals("") && list_same_designation.size()!=0)
                                                {
                                                    int quantitereceptionner = ancienReliquat.getQteCommande()-ancienReliquat.getQteReliquat_X();
                                                    ObjetReceptionPAD objetReceptionPAD = new ObjetReceptionPAD(ancienProduit.getDesignation_interne(), ancienReliquat.getQteCommande(), quantitereceptionner, ancienReliquat.getProduit_Reference());
                                                    liste_objetReception.add(objetReceptionPAD);
                                                    mapAdapter.put(objetReceptionPAD, list_same_designation);
                                                }
                                                list_same_designation = new ArrayList<>();
                                                list_same_designation.add(objetReceptionScannee_courant);
                                                ancienneDesignation = designation;
                                                ancienProduit = produit_courant;
                                                ancienReliquat = reliquat_courant;
                                            }
                                        }
                                        else
                                        {
                                            ancienneDesignation = designation;
                                            ancienProduit = produit_courant;
                                            ancienReliquat = reliquat_courant;
                                            list_same_designation.add(objetReceptionScannee_courant);
                                            listeDesignation.add(designation);
                                            premier = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                    photoProduitsChemin = data.getExtras().getString("photoProduit");
                    if (photoProduitsChemin == null || photoProduitsChemin.contentEquals("")) {

                    } else {
                        try {
                            bonLivraisonBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoProduitsChemin));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(second_passage_photo)
                    {
                    }
                    else
                    {
                    }
                    break;
            }
        }
    }

    public void lancerScanProduit()
    {
        Intent listeLotReceptionPui_Intent = null;
        Bundle listeLotReceptionPui_Bundle = super.getBundle();
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            listeLotReceptionPui_Intent = new Intent(DetailReceptionPadActivity.this, ScannerPreparationActivity.class);
            listeLotReceptionPui_Bundle.putInt("scannerContexteInt", R.string.scannerContexteReceptionPAD);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                listeLotReceptionPui_Intent = new Intent(DetailReceptionPadActivity.this, BarcodePreparationActivity.class);

            }
            else
            {
                listeLotReceptionPui_Intent = new Intent(DetailReceptionPadActivity.this, ScannerPreparationActivity.class);
                listeLotReceptionPui_Bundle.putInt("scannerContexteInt", R.string.scannerContexteReceptionPAD);
            }
        }

        listeLotReceptionPui_Bundle.putBoolean("doitEtreIdentique", false);
        listeLotReceptionPui_Bundle.putIntegerArrayList("liste_id_reliquat", (ArrayList<Integer>) liste_id_reliquat);
        listeLotReceptionPui_Bundle.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) GTIN_Scannee);
        listeLotReceptionPui_Bundle.putSerializable("ListeObjetScannee", (Serializable) listObjet_scanne);
        listeLotReceptionPui_Bundle.putInt("ActionId", action.getId());
        listeLotReceptionPui_Bundle.putString("ordreTri", "Designation");
        listeLotReceptionPui_Bundle.putInt("ReceptionID", commandeCourante.getID_commande());
        listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContextNewReceptionPAD));
        listeLotReceptionPui_Bundle.putString("Fournisseur_Reception", commandeCourante.getFournisseur());
        listeLotReceptionPui_Bundle.putBoolean("isBoutonSuppressionExistant", true);
        listeLotReceptionPui_Bundle.putBoolean("modeCumule", true);
        listeLotReceptionPui_Bundle.putInt("UserId", utilisateurConnecte.getId());
        listeLotReceptionPui_Bundle.putBoolean("modeRafale", true);

        listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
        DetailReceptionPadActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        boolean complet = CheckReferenceRestante(mapAdapter);
        inflater.inflate(R.menu.menu_action, menu);
        if(complet)
        {
            menu.findItem(R.id.menuAdd).setVisible(false);
        }
        else
        {
            menu.findItem(R.id.menuAdd).setVisible(true);
        }
        menu.findItem(R.id.menuSave).setVisible(true);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem item = menu.findItem(R.id.menuSave);
        MenuItem itemadd = menu.findItem(R.id.menuAdd);

        itemadd.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onMenuAddClick();
                return true;
            }
        });

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuSaveClick();
                return true;
            }
        });

        return true;
    }

    //ajout manuel d'un produit
    public void onMenuAddClick()
    {
        Intent intent_vers_selection_manuel = new Intent(DetailReceptionPadActivity.this, AjoutManuelProduitReceptionPADActivity.class);
        Bundle bundle_vers_selection_manuel = new Bundle();
        bundle_vers_selection_manuel.putInt("CommandeId", commandeCourante.getID_commande());
        bundle_vers_selection_manuel.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        bundle_vers_selection_manuel.putInt("serviceSelectionneID", serviceActuel.getId());
        bundle_vers_selection_manuel.putSerializable("ListeObjetScannee", (Serializable) listObjet_scanne);

        intent_vers_selection_manuel.putExtras(bundle_vers_selection_manuel);
        DetailReceptionPadActivity.this.startActivityForResult(intent_vers_selection_manuel, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }

    public void onMenuSaveClick()
    {
        afficherAlerteConfirmationBL(DetailReceptionPadActivity.this, DetailReceptionPadActivity.this.getLayoutInflater());

    }

    public void SauvegarderReception()
    {
            if(list_id_produit.size() > 0)
            {
                SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date =new Date();
                String date_string = parseFormat.format(date);

                if(photoProduitsChemin == null)
                {
                    photoProduitsChemin = "";
                }

                //gestion de l'action
                action.setUserId(utilisateurConnecte.getId());
                action.setDate(date_string);
                action.setServiceId(serviceActuel.getId());
                action.setEtablissementId(Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)));
                action.setStatut("En attente");
                if(commandeSelectNumero != null)
                {
                    action.setChampsParentId(commandeCourante.getID_commande());
                }
                else
                {
                    action.setChampsParentId(0);
                }

                action.setCheminPhoto(photoProduitsChemin);
                action.setActionName("Réception PAD");


                ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, action);
                List<ObjetReceptionScannee> listAEnvoyer = new ArrayList<>();
                boolean premierPassageListe = true;
                for(ObjetReceptionScannee courant : listObjet_scanne)
                {
                    if(premierPassageListe)
                    {
                        listAEnvoyer.add(courant);
                        premierPassageListe = false;
                    }
                    else
                    {
                        Map<String, String> gs1AAjouter = OutilsDecodage.decouperGTIN(courant.getGs1_scannee());
                        if(gs1AAjouter.size() == 0)
                        {
                            listAEnvoyer.add(courant);
                        }
                        else
                        {
                            String gtin = gs1AAjouter.get(OutilsDecodage.codeGtin);
                            Produit produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
                            if(produitCourant.isSuivi_Serialisation() && produitCourant.isSerialiser_Reception_Delivrance())
                            {
                                listAEnvoyer.add(courant);
                            }
                            else
                            {
                                String lot = gs1AAjouter.get(OutilsDecodage.numeroLot);
                                int uidEmpl = courant.getEmplacement_uid();
                                int quantiteCourante = courant.getQuantiteScannee();
                                ObjetReceptionScannee newObjet = new ObjetReceptionScannee(courant);
                                int indexTemp = 0;
                                boolean tempSupprimer = false;
                                for(ObjetReceptionScannee temp : listAEnvoyer)
                                {
                                    Map<String, String> decoupeTemp = OutilsDecodage.decouperGTIN(temp.getGs1_scannee());
                                    String tempLot = decoupeTemp.get(OutilsDecodage.numeroLot);
                                    int tempUidEmp = temp.getEmplacement_uid();
                                    int tempQuantite = temp.getQuantiteScannee();

                                    if(tempLot.contentEquals(lot) && uidEmpl == tempUidEmp)
                                    {
                                        newObjet.setQuantiteScannee(tempQuantite+quantiteCourante);
                                        tempSupprimer = true;
                                        break;
                                    }

                                    indexTemp++;
                                }

                                if(tempSupprimer)
                                    listAEnvoyer.remove(indexTemp);

                                listAEnvoyer.add(newObjet);
                            }
                        }
                    }
                }

                for(ObjetReceptionScannee objet_courant : listAEnvoyer)
                {
                    Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(objet_courant.getGs1_scannee());

                    PH_Reliquat ph_reliquat_courant = null;
                    Produit produit_courant = null;
                    String numLot = "";
                    String numSerie = "";
                    String datePeremption = "";

                    if(gs1Decoupe.size() != 1 && !objet_courant.getGs1_scannee().startsWith("ci"))
                    {
                        numLot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                        numSerie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                        datePeremption = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
                        produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                    }
                    else
                    {
                        String code_inconnu = objet_courant.getGs1_scannee();
                        if(code_inconnu.startsWith("ci"))
                        {
                            Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(code_inconnu);
                            code_inconnu = MapInconnu.get("Code_Inconnu");
                            numLot = MapInconnu.get("Lot");
                            datePeremption = MapInconnu.get("Date");
                        }

                        List<Produit> list = ProduitOpenHelper.getProduitsParCodeInconnue(db, code_inconnu);
                        if(list.size() == 1)
                            produit_courant = list.get(0);
                    }

                    if(produit_courant != null)
                    {
                        String GS1 = "";
                        if(produit_courant.isSuivi_Serialisation())
                        {
                            GS1 = gs1Decoupe.get(OutilsDecodage.gtin_Reconstruit_AvecSerie);
                        }
                        else
                        {
                            GS1 = gs1Decoupe.get(OutilsDecodage.gtin_Reconstruit_SansSerie);
                        }

                        if(GS1 == null)
                        {
                            GS1 = objet_courant.getGs1_scannee();
                        }

                        if(commandeSelectNumero != null)
                        {
                            ph_reliquat_courant = PH_ReliquatOpenHelper.getPH_ReliquatByUnIdProduitetNumero(db, produit_courant.getID_produit(), commandeSelectNumero);

                            if(ph_reliquat_courant != null)
                            {
                                if(ph_reliquat_courant.getZone() == null && ph_reliquat_courant.getEmplacement() == null && ph_reliquat_courant.getReliquat_UID() >0)
                                {
                                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, ph_reliquat_courant.getPhiMR4UUID(), ph_reliquat_courant.getReliquat_UID(), DBOpenHelper.ActionsEAS.SUPPR);
                                    PH_ReliquatOpenHelper.supprimerUnPhReliquat(db, ph_reliquat_courant);
                                }

                                Random randomactionReliquat = new Random();
                                int reliquatUID = randomactionReliquat.nextInt();
                                if(reliquatUID > 0)
                                    reliquatUID= reliquatUID*-1;

                                ph_reliquat_courant.setReliquat_UID(reliquatUID);
                                ph_reliquat_courant.setLot(numLot);
                                ph_reliquat_courant.setSerie(numSerie);
                                ph_reliquat_courant.setPeremptionDate(datePeremption);
                                ph_reliquat_courant.setZone("RECEPTION");
                                ph_reliquat_courant.setEmplacement("RECEPTION-"+commandeCourante.getNumero()+"-"+commandeCourante.getPatient_identite());
                                ph_reliquat_courant.setQteLivraison(objet_courant.getQuantiteScannee());
                                if(numeroBonLivraison != null)
                                {
                                    ph_reliquat_courant.setBL_Numero(numeroBonLivraison);
                                }
                                else
                                {
                                    ph_reliquat_courant.setBL_Numero("");
                                }
                                if(objet_courant.getGs1_scannee() != null && !objet_courant.getGs1_scannee().contentEquals(""))
                                {
                                    ph_reliquat_courant.setScanValue(objet_courant.getGs1_scannee());
                                }
                                else
                                {
                                    ph_reliquat_courant.setScanValue("");
                                }


                                Random randomactionligne = new Random();
                                int actionligneId = randomactionligne.nextInt();
                                if(actionligneId > 0)
                                    actionligneId= actionligneId*-1;

                                PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, ph_reliquat_courant);
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, ph_reliquat_courant.getPhiMR4UUID(), ph_reliquat_courant.getReliquat_UID(), DBOpenHelper.ActionsEAS.AJOUT);

                                ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, action.getId(), "PH_Reliquat", ph_reliquat_courant.getReliquat_UID(), GS1, objet_courant.getEmplacement_uid(), objet_courant.getQuantiteScannee(), produit_courant.getDesignation_interne());
                                ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                            }
                        }
                        else
                        {
                            Random randomactionligne = new Random();
                            int actionligneId = randomactionligne.nextInt();
                            if(actionligneId > 0)
                                actionligneId= actionligneId*-1;

                            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, action.getId(), "PH_Produits", produit_courant.getID_produit(), GS1, objet_courant.getEmplacement_uid(), objet_courant.getQuantiteScannee(), produit_courant.getDesignation_interne());
                            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                        }
                    }
                }

                commandeCourante.setSituation("RM");
                CommandeOpenHelper.mettreAJourUneCommande(db, commandeCourante);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, CommandeOpenHelper.Constantes.TABLE_COMMANDE, commandeCourante.getPhiMR4UUID(), commandeCourante.getID_commande(), DBOpenHelper.ActionsEAS.MAJ);
                enregistrer = true;
                EnvoiMailReception();
            }
            else
            {
                Alerte.afficherAlerte(DetailReceptionPadActivity.this, "Erreur", "Veuillez scanner au moins un produit s'il vous plaît", "alerte");
            }
    }

    public void prendrePhotoBL()
    {
        Intent detailReceptionPui_Intent = new Intent(DetailReceptionPadActivity.this, PrisePhoto.class);
        Bundle detailReceptionPui_Bundle = DetailReceptionPadActivity.super.getBundle();
        // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
        detailReceptionPui_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        detailReceptionPui_Bundle.putInt("serviceSelectionneID", serviceActuel.getId());
        detailReceptionPui_Bundle.putString("CommandeNumero", commandeCourante.getNumero());
        detailReceptionPui_Bundle.putString("contexte", "priseDePhotoContexteBonDeLivraison");
        detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);
        DetailReceptionPadActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
    }

    private static ObjetReceptionPAD getKeyMap(Map Map, int index)
    {

        ObjetReceptionPAD key = null;
        Map <ObjetReceptionPAD,Object> hs = Map;
        int pos=0;
        for(Map.Entry<ObjetReceptionPAD, Object> entry : hs.entrySet())
        {
            if(index==pos){
                key = entry.getKey();
            }
            pos++;
        }
        return key;
    }

    //on check si il reste des produits à scanner ou pas pour afficher les boutons d'ajout
    private boolean CheckReferenceRestante(Map<ObjetReceptionPAD, List<ObjetReceptionScannee>> map_a_afficher)
    {
        List<PH_Reliquat> ph_reliquatList = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeCourante.getNumero());
        boolean complet = true;
        if(map_a_afficher.size() != 0 && map_a_afficher.size() == ph_reliquatList.size())
        {
            for(Map.Entry<ObjetReceptionPAD, List<ObjetReceptionScannee>> entry : map_a_afficher.entrySet())
            {
                ObjetReceptionPAD cle = entry.getKey();

                if(cle.getQuantite_receptionner() != cle.getQuantite_commander())
                {
                    complet = false;
                    break;
                }
            }
        }
        else
        {
            complet = false;
        }

        return complet;
    }

    //method to expand all groups
    private void expandAll()
    {
        int count = receptionPADExpandableAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            liste_viewProduitReceptionPAD.expandGroup(i);
        }
    }

    private String LastCharSerieLot(String string)
    {
        String lastChar = string.substring(string.length()-1);
        if(lastChar.contentEquals("@"))
            string = string.substring(0, string.length()-1);

        return string;
    }

    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_quitter, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent(DetailReceptionPadActivity.this, ServiceReceptionPadActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            extras.putInt("serviceSelectionneID", serviceActuel.getId());
            intent.putExtras(extras);
            DetailReceptionPadActivity.this.startActivity(intent);
            DetailReceptionPadActivity.this.finish();
        });

        buttonAnnuler.setOnClickListener(v -> alertDialog.dismiss());
    }

    public void afficherAlerteValidationReception(Context context, LayoutInflater inflater)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_validation, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            SauvegarderReception();
            alertDialog.dismiss();
        });

        buttonAnnuler.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void EnvoiMailReception()
    {
        //on check la connexion à internet pour l'envoie du mail
        boolean internet = checkInternetConnection();
        if(!internet)
        {
            Alerte.afficherAlerte(DetailReceptionPadActivity.this, "Erreur", "Aucune connexion internet détectée, aucun envoi de mail possible","alerte");
        }
        else
        {
            //Construction mail
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date dateDuJour = new Date();
            String date = dateFormat.format(dateDuJour);

            Depot depotdest = DepotOpenHelper.getDepotParReference(db, commandeCourante.getRef_Depot_Dest());
            String depot_destinataire = "";
            if(depotdest != null)
            {
                depot_destinataire = depotdest.getNom();
            }

            subject = "PhiMR4 - "+depot_destinataire+" - "+commandeCourante.getFournisseur()+" - Réception PAD N°" + commandeCourante.getNumero() + " - " + date;

            if(bonLivraisonBitmap != null)
            {
                body = "Madame, Monsieur, \n \n" +
                        "La réception PAD N°" + commandeCourante.getNumero() + " a été réalisée par "+utilisateurConnecte.getNom()+" "+utilisateurConnecte.getPrenom()+". \n" +
                        "Le numéro de bon de livraison saisi est le suivant : "+numeroBonLivraison+"\n\n" +
                        "Vous pourrez trouver ci-joint le bon de livraison. \n\n" +
                        "Cordialement, \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre";
            }
            else
            {
                body = "Madame, Monsieur, \n \n" +
                        "La réception PAD N°" + commandeCourante.getNumero() + " a été réalisée par "+utilisateurConnecte.getNom()+" "+utilisateurConnecte.getPrenom()+". \n" +
                        "Le numéro de bon de livraison saisi est le suivant : "+numeroBonLivraison+"\n\n" +
                        "Cordialement, \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre";
            }


            //Sauvegarde de la signature dans une image
            if (bonLivraisonBitmap != null) {

                dateFormat = new SimpleDateFormat("yyyyMMdd");
                dateDuJour = new Date();
                date = dateFormat.format(dateDuJour);

                bonLivraisonPhotoName = String.valueOf(commandeCourante.getNumero()) + "_" + date + "_ReceptionPuiBonLivraison";

                verifyStoragePermissions(DetailReceptionPadActivity.this);
            }

            // Récupération Mail Pharmacie
            String email = ParametresServeurOpenHelper.getMailPharmacie(db);
            if(utilisateurConnecte.getEtablissement().contentEquals("ADH"))
            {
                email = "reception.pui@adh-asso.net";
            }
            if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
            {
                email = null;
            }
            // Demande à l'utilisateur si envoi de copie
            envoyerCopie = Alerte.afficherAlerte(DetailReceptionPadActivity.this, "Alerte", "Voulez-vous recevoir une copie du mail ?", "OuiNon");

            if (envoyerCopie) {
                EmailCopie = utilisateurConnecte.getMail();
                if (EmailCopie == null || EmailCopie.contentEquals("")) {
                    envoyerCopie = false;
                }
            }

            if (email != null) {
                new SendEmailTask().execute(email);
            }

        }
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, action.getPhiMR4UUID(), action.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            Toast toast = Toast.makeText(DetailReceptionPadActivity.this, "Réception PAD effectuée", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Intent retourVersService = new Intent(DetailReceptionPadActivity.this, ServiceReceptionPadActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            bundle.putInt("serviceSelectionneID", serviceActuel.getId());
            retourVersService.putExtras(bundle);
            ElementASynchroniserOpenHelper.toutSynchroniser(DetailReceptionPadActivity.this, db, utilisateurConnecte, true);
            DetailReceptionPadActivity.this.startActivity(retourVersService);
            DetailReceptionPadActivity.this.finish();
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

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {
            Mail sender = null;
            if(!envoyerCopie)
            {
                sender = new Mail(DetailReceptionPadActivity.this, email[0], true, db);
            }
            else
            {
                sender = new Mail(DetailReceptionPadActivity.this, email[0], EmailCopie, true, db);
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

    public void afficherAlerteConfirmationBL(Context context, LayoutInflater inflater) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_reception_pui, null);

        final LinearLayout buttonOk = (LinearLayout) layout.findViewById(R.id.buttonOk);
        final EditText numeroBLEdit = (EditText) layout.findViewById(R.id.numeroBL);
        final ImageView btn_photo = (ImageView) layout.findViewById(R.id.btnPhoto);
        final ImageView iconValidation = (ImageView) layout.findViewById(R.id.iconValidation);
        final LinearLayout boutonPhoto = (LinearLayout) layout.findViewById(R.id.boutonPhoto);

        Rect displayRectangle = new Rect();
        Window window = DetailReceptionPadActivity.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        layout.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        layout.setMinimumHeight((int)(displayRectangle.height() * 0.6f));
        builder.setView(layout);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOk.setBackgroundColor(getResources().getColor(R.color.vert));
                ViewCompat.setBackgroundTintList(iconValidation, ColorStateList.valueOf(getResources().getColor(R.color.blanc, null)));

                String numeroBL = numeroBLEdit.getText().toString();
                commandeCourante.setBLNumero(numeroBL);
                numeroBonLivraison = numeroBL;
                alertDialog.dismiss();
                afficherAlerteValidationReception(DetailReceptionPadActivity.this, DetailReceptionPadActivity.this.getLayoutInflater());
            }
        });

        boutonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boutonPhoto.setBackgroundColor(getResources().getColor(R.color.bleu_clair_alcyons));
                ViewCompat.setBackgroundTintList(btn_photo, ColorStateList.valueOf(getResources().getColor(R.color.blanc, null)));
                prendrePhotoBL();
            }
        });
    }
}
