package fr.alcyons.phimr4.ReceptionScanne;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Commande;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.ObjetReceptionScannee;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ListViewAdapters.ScanProduitExpandableAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.PrisePhoto.PrisePhoto;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;



/**
 * Created by olivier on 10/05/2019.
 */

public class ScanProduitActivity extends ServiceActivity {

    Bitmap bonLivraisonBitmap;
    List<String> GTIN_Scannee;
    List<Integer> list_id_produit;
    ExpandableListView liste_viewProduitReceptionScannee;
    boolean second_passage_photo;
    String photoProduitsChemin;
    List<ObjetReceptionScannee> listObjet_scanne;

    ScanProduitExpandableAdapter scanProduitExpandableAdapter;
    List<String> listZoneEmplacement;
    List<ObjetReceptionScannee> list_same_Emplacement;
    Map<String, List<ObjetReceptionScannee>> mapAdapter;
    PackageManager pm;
    ActionUtilisateur action;

    TextView nomReceptionPAD;
    TextView numeroCommande;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_produit_reception_scannee);

        //initialisation des variables
        GTIN_Scannee = new ArrayList<>();
        list_id_produit = new ArrayList<>();
        photoProduitsChemin = "chemin";
        second_passage_photo = false;
        listObjet_scanne = new ArrayList<>();
        listZoneEmplacement = new ArrayList<>();
        mapAdapter = new LinkedHashMap<>();
        list_same_Emplacement = new ArrayList<>();
        //initialisation des objets graphique
        liste_viewProduitReceptionScannee = (ExpandableListView) findViewById(R.id.liste_viewProduitReceptionScannee);

        //gestion des objets graphiques
        nomReceptionPAD = (TextView)findViewById(R.id.nomReceptionPAD);
        numeroCommande = (TextView)findViewById(R.id.numeroCommande);
        nomReceptionPAD.setVisibility(View.GONE);
        numeroCommande.setVisibility(View.GONE);

        //création de l'action
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;

        action = new ActionUtilisateur(actionId);


        pm = ScanProduitActivity.this.getPackageManager();
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            prendrePhotoBL();
        }
        else
        {
            lancerScanProduit();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        scanProduitExpandableAdapter = new ScanProduitExpandableAdapter(ScanProduitActivity.this, db, listZoneEmplacement, mapAdapter, utilisateurConnecte);
        liste_viewProduitReceptionScannee.setAdapter(scanProduitExpandableAdapter);
        liste_viewProduitReceptionScannee.setDivider(footer);
        if(list_same_Emplacement.size() !=0)
        {
            expandAll();
        }
    }

    public void supprimerScan(final int groupKey, final int ChildKey)
    {
        boolean confirmer = Alerte.afficherAlerte(ScanProduitActivity.this, "Confirmation", "Souhaitez-vous supprimer la ligne sélectionnée ?", "OuiNon");
        if (confirmer) {
            String key = getKeyMap(mapAdapter, groupKey);
            List<ObjetReceptionScannee> list_group_select = mapAdapter.get(key);
            ObjetReceptionScannee objet_a_supprimer = list_group_select.get(ChildKey);
            list_group_select.remove(ChildKey);
            int index = -1;
            for(ObjetReceptionScannee objet_courant : listObjet_scanne)
            {
                index ++;
                if(objet_courant.getEmplacement_uid() == objet_a_supprimer.getEmplacement_uid() && objet_courant.getQuantiteScannee() == objet_a_supprimer.getQuantiteScannee() && objet_courant.getGs1_scannee().contentEquals(objet_a_supprimer.getGs1_scannee()))
                {
                    break;
                }
            }

            String gs1_remove = listObjet_scanne.get(index).getGs1_scannee();
            int index_gs1 = GTIN_Scannee.indexOf(gs1_remove);
            GTIN_Scannee.remove(index_gs1);
            listObjet_scanne.remove(index);
            list_id_produit.remove(index);
            if (list_group_select.size() == 0) {
                listZoneEmplacement.remove(groupKey);
            }
            onResume();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    if(data != null)
                    {
                        listObjet_scanne = (List<ObjetReceptionScannee>) data.getExtras().getSerializable("listeString");
                        if(listObjet_scanne.size()!= 0)
                        {
                            mapAdapter = new LinkedHashMap<>();
                            liste_viewProduitReceptionScannee.setAdapter((BaseExpandableListAdapter)null);
                            listZoneEmplacement = new ArrayList<>();
                            list_same_Emplacement = new ArrayList<>();
                            GTIN_Scannee = new ArrayList<>();
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
                                    String lotCourant = "";
                                    String gtinCourant = "";
                                    if(gs1DecoupeCourant.size()!=1)
                                    {
                                        lotCourant = gs1DecoupeCourant.get(OutilsDecodage.numeroLot);
                                        gtinCourant = gs1DecoupeCourant.get(OutilsDecodage.codeGtin);
                                    }
                                    int quantiteCourant = objetCourant.getQuantiteScannee();
                                    int uidEmplacementCourant = objetCourant.getEmplacement_uid();
                                    int index = 0;
                                    boolean trouver = false;
                                    ObjetReceptionScannee objetAAjouter = new ObjetReceptionScannee(objetCourant);
                                    for(ObjetReceptionScannee objetTemp : listTempParLot)
                                    {
                                        Map<String, String> gs1DecoupeTemp = OutilsDecodage.decouperGTIN(objetTemp.getGs1_scannee());
                                        if(gs1DecoupeTemp.size() != 1)
                                        {
                                            String lotTemp = gs1DecoupeTemp.get(OutilsDecodage.numeroLot);
                                            String gtinTemp = gs1DecoupeTemp.get(OutilsDecodage.codeGtin);
                                            int quantiteTemp = objetTemp.getQuantiteScannee();
                                            int uidEmplacementTemp = objetTemp.getEmplacement_uid();

                                            if(lotCourant.contentEquals(""))
                                            {
                                                if(objetCourant.getGs1_scannee().contentEquals(objetTemp.getGs1_scannee()))
                                                {
                                                    trouver = true;
                                                    break;
                                                }
                                            }
                                            else
                                            {
                                                if(lotTemp.contentEquals(lotCourant) && gtinTemp.contentEquals(gtinCourant) && uidEmplacementTemp == uidEmplacementCourant)
                                                {
                                                    objetAAjouter.setQuantiteScannee(quantiteCourant+quantiteTemp);
                                                    trouver =true;
                                                    break;
                                                }
                                            }
                                        }
                                        else
                                        {
                                            if(objetCourant.getGs1_scannee().contentEquals(objetTemp.getGs1_scannee()))
                                            {
                                                trouver = true;
                                                break;
                                            }
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
                            String ancienZoneEmplacement = "";

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
                                    List<Produit> list = ProduitOpenHelper.getProduitByCodeInconnu(db, objetReceptionScannee_courant.getGs1_scannee());
                                    if(list.size() == 1)
                                        produit_courant = list.get(0);
                                }

                                if(produit_courant != null)
                                {
                                    list_id_produit.add(produit_courant.getID_produit());
                                }

                                //Récupération des emplacements
                                String zoneEmplacement = "";
                                int emplacement_uid = objetReceptionScannee_courant.getEmplacement_uid();
                                Depot_Emplacement emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_uid);
                                if(emplacement_courant != null)
                                {
                                    Depot_Zone depotZone = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());
                                    if(depotZone != null)
                                    {
                                        zoneEmplacement = depotZone.getZoneName()+" - "+emplacement_courant.getAdressage();
                                        if(iteration == size)
                                        {
                                            if(listZoneEmplacement.indexOf(zoneEmplacement) != -1)
                                            {
                                                if(!ancienZoneEmplacement.contentEquals("") && list_same_Emplacement.size()!=0)
                                                {
                                                    mapAdapter.put(ancienZoneEmplacement, list_same_Emplacement);
                                                }

                                                list_same_Emplacement = mapAdapter.get(zoneEmplacement);
                                                list_same_Emplacement.add(objetReceptionScannee_courant);
                                            }
                                            else
                                            {
                                                if(!ancienZoneEmplacement.contentEquals("") && list_same_Emplacement.size()!=0)
                                                {
                                                    mapAdapter.put(ancienZoneEmplacement, list_same_Emplacement);
                                                }
                                                list_same_Emplacement = new ArrayList<>();
                                                list_same_Emplacement.add(objetReceptionScannee_courant);
                                                listZoneEmplacement.add(zoneEmplacement);
                                            }
                                            mapAdapter.put(zoneEmplacement, list_same_Emplacement);

                                        }
                                        else if(!premier)
                                        {
                                            if(listZoneEmplacement.indexOf(zoneEmplacement) != -1)
                                            {
                                                list_same_Emplacement.add(objetReceptionScannee_courant);
                                            }
                                            else
                                            {
                                                listZoneEmplacement.add(zoneEmplacement);
                                                if(!ancienZoneEmplacement.contentEquals("") && list_same_Emplacement.size()!=0)
                                                {
                                                    mapAdapter.put(ancienZoneEmplacement, list_same_Emplacement);
                                                }
                                                list_same_Emplacement = new ArrayList<>();
                                                list_same_Emplacement.add(objetReceptionScannee_courant);
                                                ancienZoneEmplacement = zoneEmplacement;
                                            }
                                        }
                                        else
                                        {
                                            ancienZoneEmplacement = zoneEmplacement;
                                            list_same_Emplacement.add(objetReceptionScannee_courant);
                                            listZoneEmplacement.add(zoneEmplacement);
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
                        SauvegarderReception();
                    }
                    else
                    {
                        lancerScanProduit();
                    }
                    break;
            }
        }
    }

    private void lancerScanProduit()
    {
        Intent listeLotReceptionPui_Intent = null;
        Bundle listeLotReceptionPui_Bundle = super.getBundle();

        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            listeLotReceptionPui_Intent = new Intent(ScanProduitActivity.this, ScannerSearchOnlyActivity.class);
            listeLotReceptionPui_Bundle.putInt("scannerContexteInt", R.string.scannerContexteProduitReceptionScannee);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                listeLotReceptionPui_Intent = new Intent(ScanProduitActivity.this, BarcodeCaptureActivity.class);

            }
            else
            {
                listeLotReceptionPui_Intent = new Intent(ScanProduitActivity.this, ScannerSearchOnlyActivity.class);
                listeLotReceptionPui_Bundle.putInt("scannerContexteInt", R.string.scannerContexteProduitReceptionScannee);
            }
        }

        listeLotReceptionPui_Bundle.putBoolean("doitEtreIdentique", false);
        listeLotReceptionPui_Bundle.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) GTIN_Scannee);
        listeLotReceptionPui_Bundle.putSerializable("ListeObjetScannee", (Serializable) listObjet_scanne);
        listeLotReceptionPui_Bundle.putInt("ActionId", action.getId());
        listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContexteProduitReceptionScannee));
        listeLotReceptionPui_Bundle.putBoolean("isBoutonSuppressionExistant", true);
        listeLotReceptionPui_Bundle.putInt("UserId", utilisateurConnecte.getId());
        listeLotReceptionPui_Bundle.putBoolean("modeRafale", true);

        listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
        ScanProduitActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSave).setVisible(true);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem item = menu.findItem(R.id.menuSave);
        MenuItem itemData = menu.findItem(R.id.menuDatamatrix);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuSaveClick();
                return true;
            }
        });

        itemData.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                lancerScanProduit();
                return true;
            }
        });
        return true;
    }

    public void onMenuSaveClick()
    {
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            if(bonLivraisonBitmap == null)
            {
                second_passage_photo = true;
                prendrePhotoBL();
            }
            else
            {
                SauvegarderReception();
            }
        }
        else
        {
            SauvegarderReception();
        }
    }

    public void SauvegarderReception()
    {
        if(list_id_produit.size() > 0)
        {
            //on recherche les réceptions qui contiennent les produits scannées
            List<Commande> liste_commande_produit_present = CommandeOpenHelper.getCommandeByProduit(db, list_id_produit);
            Commande commande_courante = null;
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date =new Date();
            String date_string = parseFormat.format(date);
            String commandeSelectNumero = null;


            if(liste_commande_produit_present.size() == 1)
            {
                commande_courante = liste_commande_produit_present.get(0);
            }

            if(commande_courante != null)
            {
                commandeSelectNumero = commande_courante.getNumero();
                boolean confirmation_commande = Alerte.afficherAlerte(ScanProduitActivity.this, "Attention", "Les produits scannés concernent la commande :"+commandeSelectNumero+" ? \n Confirmation ?", "OuiNon");
                if(!confirmation_commande)
                {
                    commandeSelectNumero = null;
                }
            }
            else
            {
                List<String> liste_numero_commande = new ArrayList<>();
                if(liste_commande_produit_present.size() > 1)
                {
                    for(Commande commandeCourante : liste_commande_produit_present)
                    {
                        liste_numero_commande.add(commandeCourante.getNumero());
                    }

                    commandeSelectNumero = Alerte.afficherAlerteListView(ScanProduitActivity.this, "Sélectionnez la commande correspondante", liste_numero_commande);
                }
            }

            if(commandeSelectNumero != null) {
                commande_courante = CommandeOpenHelper.getCommandeByNumero(db, commandeSelectNumero);

                List<PH_Reliquat> liste_reliquat = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeSelectNumero);

                if(list_id_produit.size() != liste_reliquat.size())
                {
                    afficherAlerteConfirmation(ScanProduitActivity.this, ScanProduitActivity.this.getLayoutInflater(), date_string, commandeSelectNumero, commande_courante, "Réception incomplète");
                }
                else
                {
                    saveRecepetion(date_string, commandeSelectNumero, commande_courante);
                }
            }
            else
            {
                afficherAlerteConfirmation(ScanProduitActivity.this, ScanProduitActivity.this.getLayoutInflater(), date_string, commandeSelectNumero, commande_courante, "Aucune commande");
            }
        }
        else
        {
            Alerte.afficherAlerte(ScanProduitActivity.this, "Erreur", "Veuillez scanner au moins un produit s'il vous plaît", "alerte");
        }
    }

    public void prendrePhotoBL()
    {
        Intent detailReceptionPui_Intent = new Intent(ScanProduitActivity.this, PrisePhoto.class);
        Bundle detailReceptionPui_Bundle = ScanProduitActivity.super.getBundle();
        // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
        detailReceptionPui_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        detailReceptionPui_Bundle.putString("contexte", "photoAction");
        detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);
        ScanProduitActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
    }

    @Override
    public void onBackPressed()
    {
        //ScanProduitActivity.this.finish();
        super.onBackPressed();
    }

    private static String getKeyMap(Map Map, int index)
    {

        String key = null;
        Map <String,Object> hs = Map;
        int pos=0;
        for(Map.Entry<String, Object> entry : hs.entrySet())
        {
            if(index==pos){
                key=entry.getKey();
            }
            pos++;
        }
        return key;
    }

    //method to expand all groups
    private void expandAll()
    {
        int count = scanProduitExpandableAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            liste_viewProduitReceptionScannee.expandGroup(i);
        }
    }

    private String LastCharSerieLot(String string)
    {
        String lastChar = string.substring(string.length()-1);
        if(lastChar.contentEquals("@"))
            string = string.substring(0, string.length()-1);

        return string;
    }


    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater, final String date_string, final String commandeSelectNumero, final Commande commande_courante, final String context_alerte)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_quitter, null);

        //gestion du context pour l'apparition de l'alerte
        String text = "";
        switch (context_alerte)
        {
            case "Aucune commande":
                text = "Attention aucune commande ne correspond aux produits scannés, souhaitez-vous continuer ?";
                break;
            case "Réception incomplète":
                text = "La commande sélectionné n'a pas été réceptionnée entièrement, souhaitez-vous finir la réception manuellement ?";
                break;
        }

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageFin = (TextView) layout.findViewById(R.id.messageFin);
        messageFin.setText(text);
        builder.setView(layout);




        final AlertDialog alertDialog = builder.create();
        //agrandissement de l'alerte
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //effacement de l'alerte
                alertDialog.dismiss();

                //continuation du service suivant le contexte
                switch (context_alerte)
                {
                    case "Aucune commande":
                        saveRecepetion(date_string, commandeSelectNumero, commande_courante);
                        break;
                    case "Réception incomplète":
                        Intent intent_vers_ajout_manuel = new Intent(ScanProduitActivity.this, AjoutManuelReceptionScanneeActivity.class);
                        Bundle bundle_vers_ajout_manuel = new Bundle();
                        bundle_vers_ajout_manuel.putString("NumeroCommande", commandeSelectNumero);
                        bundle_vers_ajout_manuel.putString("CheminPhoto", photoProduitsChemin);
                        bundle_vers_ajout_manuel.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        bundle_vers_ajout_manuel.putInt("serviceSelectionneID", serviceActuel.getId());
                        bundle_vers_ajout_manuel.putSerializable("listeProduitScannee", (Serializable) listObjet_scanne);
                        intent_vers_ajout_manuel.putExtras(bundle_vers_ajout_manuel);
                        ScanProduitActivity.this.startActivity(intent_vers_ajout_manuel);
                        ScanProduitActivity.this.finish();
                        break;
                }
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                switch (context_alerte)
                {
                    case "Réception incomplète":
                        saveRecepetion(date_string, commandeSelectNumero, commande_courante);
                        break;
                }
            }
        });
    }

    public void saveRecepetion(String date_string, String commandeSelectNumero, Commande commande_courante)
    {
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
            action.setChampsParentId(commande_courante.getID_commande());
        }
        else
        {
            action.setChampsParentId(0);
        }

        action.setCheminPhoto(photoProduitsChemin);
        action.setActionName("Réception");


        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, action);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, action.getPhiMR4UUID(), action.getId(), DBOpenHelper.ActionsEAS.AJOUT);
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
                if(gs1AAjouter.size() == 1)
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

                            if(tempLot != null)
                            {
                                if(tempLot.contentEquals(lot) && uidEmpl == tempUidEmp)
                                {
                                    newObjet.setQuantiteScannee(tempQuantite+quantiteCourante);
                                    tempSupprimer = true;
                                    break;
                                }
                            }
                            else
                            {
                                if(temp.getGs1_scannee().contentEquals(courant.getGs1_scannee()))
                                {
                                    newObjet.setQuantiteScannee(tempQuantite+quantiteCourante);
                                    tempSupprimer = true;
                                    break;
                                }
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

            if(gs1Decoupe.size() != 1)
            {
                produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
            }
            else
            {
                List<Produit> list = ProduitOpenHelper.getProduitsParCodeInconnue(db, objet_courant.getGs1_scannee());
                if(list.size() == 1)
                    produit_courant = list.get(0);
            }

            if(produit_courant != null)
            {
                String GS1 = "";
                if(produit_courant.isSerialiser_Reception_Delivrance())
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
                        Random randomactionligne = new Random();
                        int actionligneId = randomactionligne.nextInt();
                        if(actionligneId > 0)
                            actionligneId= actionligneId*-1;

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

        onBackPressed();
    }
}
