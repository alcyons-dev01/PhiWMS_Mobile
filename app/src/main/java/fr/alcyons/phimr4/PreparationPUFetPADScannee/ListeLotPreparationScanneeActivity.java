package fr.alcyons.phimr4.PreparationPUFetPADScannee;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ListView;
import android.widget.TextView;


import com.github.clans.fab.FloatingActionButton;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.ObjetPreparationScannee;
import fr.alcyons.phimr4.Classes.ObjetReceptionScannee;
import fr.alcyons.phimr4.Classes.PH_Preparation;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.Lot_PreparationAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;


import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_LISTE_LOTS;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_LOT;

/**
 * Created by olivier on 29/04/2019.
 */

public class ListeLotPreparationScanneeActivity extends ServiceActivity {

    public PH_Preparation_Ligne_Preparation_Adapte phPreparationLignePreparationAdapte;
    PH_Preparation_Ligne ph_preparation_ligne_courant;
    public List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotAdapteList;
    public ListView lotAdapteListView;
    Depot depot;
    Produit produit;
    Lot_PreparationAdapter lotPreparationAdapter;
    int quantiteDemandee;
    int restantAPre;
    FloatingActionButton boutonSave;
    Integer qtePreparer;
    String numeroLot;
    String dateDePeremption;
    String numeroSerie;
    boolean camera_first;
    MenuItem itemDatamatrix;
    MenuItem itemAdd;
    boolean menuAddClick;
    List<ObjetReceptionScannee> liste_objet_scanne;
    List<String> list_code_scanne;
    boolean Modification_Effectuee;
    TextView CodeListeCourante;

    PackageManager pm;
    // Permet de sauvegarder la répartition des quantités par lot
    public View.OnClickListener clicBoutonSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int compteurLots = 0;
            List<Integer> index_a_supprimer = new ArrayList<>();
            List<Integer> liste_index_objet_supr = new ArrayList<>();

            int index = -1;
            if(Modification_Effectuee)
            {
                for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot : lotAdapteList) {
                    index ++;
                    if (lot.getQteSaisie() != 0) {
                        String gtin = produit.getGTIN();
                        String numSerie = lot.getNumSerie();
                        String numLot = lot.getNumLot();
                        String datePeremption = lot.getDatePeremption();

                        //gestion du format de la date de peremption
                        if(!datePeremption.contentEquals(""))
                        {
                            String[] tab_date = datePeremption.split("-");
                            datePeremption = tab_date[0].substring(2)+tab_date[1]+tab_date[2];
                        }

                        String gs1Reconstruit = "";

                        //gestion du gtin
                        if(gtin.contentEquals(""))
                        {
                            String code_inconnu = produit.getCodeInconnue();
                            if(code_inconnu.contentEquals(""))
                            {
                                code_inconnu = produit.getDesignation_interne();
                            }

                            if(!datePeremption.contentEquals(""))
                            {
                                gtin = "ci"+code_inconnu+"dp17"+datePeremption+"nl10"+numLot;
                            }
                            else
                            {
                                gtin = "ci"+code_inconnu+"nl10"+numLot;
                            }
                            gs1Reconstruit = gs1Reconstruit+gtin;
                        }
                        else
                        {
                            gs1Reconstruit = gs1Reconstruit+gtin;
                            //gestion du numero de serie
                            if(!numSerie.contentEquals(""))
                            {
                                gs1Reconstruit = gs1Reconstruit+"21"+numSerie+"@";
                            }

                            gs1Reconstruit =gs1Reconstruit+"17"+datePeremption+"10"+numLot;
                        }

                        int uid_emplacement = 0;
                        List<Depot_Zone> liste_zone_depot = ZoneOpenHelper.getZonesParDepot(db, depot);
                        for(Depot_Zone zone_courante : liste_zone_depot)
                        {
                            if(zone_courante.getZoneName().toUpperCase().contentEquals(lot.getZone().toUpperCase()))
                            {
                                List<Depot_Emplacement> emplacementList = EmplacementOpenHelper.getEmplacementsParZone(db, zone_courante);
                                for(Depot_Emplacement emplacement_courant : emplacementList)
                                {
                                    if(emplacement_courant.getAdressage().toUpperCase().contentEquals(lot.getEmplacement().toUpperCase()))
                                    {
                                        uid_emplacement = emplacement_courant.get_UID();
                                        break;
                                    }
                                }
                            }

                            if(uid_emplacement != 0)
                            {
                                break;
                            }
                        }
                        ObjetReceptionScannee objetReceptionScannee = new ObjetReceptionScannee(gs1Reconstruit, uid_emplacement, lot.getQteSaisie(), "");

                        int index_a_supprimer_objet = -1;
                        for(ObjetReceptionScannee objetCourant : liste_objet_scanne)
                        {
                            index_a_supprimer_objet ++;

                            if(objetReceptionScannee.getGs1_scannee().contentEquals(objetCourant.getGs1_scannee()))
                            {
                                int index_into_liste = liste_index_objet_supr.indexOf(index_a_supprimer_objet);
                                if(index_into_liste == -1)
                                {
                                    liste_index_objet_supr.add(index_a_supprimer_objet);
                                }
                                break;
                            }

                        }

                        liste_objet_scanne.add(objetReceptionScannee);
                        compteurLots++;
                    }
                    else
                    {
                        index_a_supprimer.add(index);
                    }
                }

                if(liste_index_objet_supr.size() != 0)
                {
                    for(int i = liste_index_objet_supr.size()-1; i >= 0; i--)
                    {
                        int indexcourant = liste_index_objet_supr.get(i);
                        liste_objet_scanne.remove(indexcourant);
                    }
                }

                if(index_a_supprimer.size() != 0)
                {
                    for(int i = index_a_supprimer.size()-1; i >= 0; i--)
                    {
                        int indexcourant = index_a_supprimer.get(i);
                        lotAdapteList.remove(indexcourant);
                    }

                    phPreparationLignePreparationAdapte.setLotAdaptes(lotAdapteList);
                }
            }


            if (compteurLots > 0 || !Modification_Effectuee) {
                Intent resultIntent = new Intent();

                Bundle extras = ListeLotPreparationScanneeActivity.super.getBundle();
                extras.putSerializable("lotAdaptes", (Serializable) lotAdapteList);
                extras.putSerializable("listeObjet", (Serializable) liste_objet_scanne);
                resultIntent.putExtras(extras);

                ListeLotPreparationScanneeActivity.this.setResult(RETOUR_LISTE_LOTS, resultIntent);
                ListeLotPreparationScanneeActivity.this.finish();
            }
            else {
                onBackPressed();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lot_prepration);
        qtePreparer = 0;
        menuAddClick = false;
        Modification_Effectuee = false;

        //gestion du package manager
        pm = ListeLotPreparationScanneeActivity.this.getPackageManager();

        // Récupération et initialisation du bouton de cadenas
        boutonSave = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.boutonSave);
        boutonSave.setOnClickListener(clicBoutonSave);
        camera_first = false;
        liste_objet_scanne = new ArrayList<>();
        list_code_scanne = new ArrayList<>();
        liste_objet_scanne = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ObjetDejaScanne");
        list_code_scanne = intent.getExtras().getStringArrayList("CodeDejaScannee");
        if(liste_objet_scanne == null)
        {
            liste_objet_scanne = new ArrayList<>();
        }

        if(list_code_scanne == null)
        {
            list_code_scanne = new ArrayList<>();
        }


        // Récupération du ph_preparation_ligne, produit, depot sélectionné
        phPreparationLignePreparationAdapte = (PH_Preparation_Ligne_Preparation_Adapte) intent.getExtras().getSerializable("ph_preparationLigneAdapte");
        ph_preparation_ligne_courant = gestionnairePH_Preparation_Ligne.getPH_Preparation_LigneByID(db, phPreparationLignePreparationAdapte.getPh_preparationLigneID());
        produit = gestionnaireProduit.getProduitByID(db, intent.getExtras().getInt("produitID"));
        depot = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotID"));

        numeroLot = intent.getExtras().getString("numeroLot");
        numeroSerie = intent.getExtras().getString("numSerie");
        dateDePeremption = intent.getExtras().getString("dateDePeremption");

        // Affichage des informations de base
        PH_Preparation preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparation_ligne_courant.getPreparationID());
        Depot depot = DepotOpenHelper.getDepotParReference(db, preparation_courante.getDepotOrigineReference());
        CodeListeCourante.setText(depot.getNom() + " N° " + String.valueOf(preparation_courante.getUID()));

        ((TextView) findViewById(R.id.designationProduit)).setText(produit.getDesignation_interne());

        if (produit == null || depot == null) {
            Alerte.afficherAlerte(ListeLotPreparationScanneeActivity.this, "Alerte", "Un problème a été constaté en Base de données, veuillez synchroniser l'application ou contacter la société Alcyons (service Préparation PAD", "alerte");
            ListeLotPreparationScanneeActivity.this.finish();
            return;
        }

        // Récupéeration des LOTS
        lotAdapteList = phPreparationLignePreparationAdapte.getLotAdaptes();
        boolean gtin_ok = false;
        if(!produit.getGTIN().contentEquals(""))
        {
            gtin_ok = true;
        }

        if(!ph_preparation_ligne_courant.isSuivi_Par_Serie() || ph_preparation_ligne_courant.isSerialiser_Reception() || !gtin_ok)
        {
                List<Stock_Lot_Emplacement_Light> stockLotEmplacementLights = gestionnaireStock_Lot_Emplacement.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
                Collections.sort(stockLotEmplacementLights, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getPeremptionDate().compareTo(o2.getPeremptionDate());
                    }
                });

                for (Stock_Lot_Emplacement_Light stockLotEmplacement : stockLotEmplacementLights) {
                    boolean trouver = false;
                    for(PH_Preparation_Ligne_Preparation_Adapte.LotAdapte adapte_courant : lotAdapteList)
                    {
                        if(adapte_courant.getNumLot().toUpperCase().contentEquals(stockLotEmplacement.getLot().toUpperCase()) && adapte_courant.getEmplacement().toUpperCase().contentEquals(stockLotEmplacement.getEmplacement().toUpperCase()))
                        {
                            trouver = true;
                        }
                    }

                    if(!trouver)
                    {
                        PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte = phPreparationLignePreparationAdapte.new LotAdapte(stockLotEmplacement);
                        lotAdapteList.add(lotAdapte);
                    }
                }
            //}

            if (lotAdapteList.size() == 0) {
                Bundle clicBoutonAjouterManuellement_Bundle = ListeLotPreparationScanneeActivity.super.getBundle();
                clicBoutonAjouterManuellement_Bundle.putInt("produitID", produit.getID_produit());
                clicBoutonAjouterManuellement_Bundle.putInt("depotID", depot.getDepot_UID());

                Intent clicBoutonAjouterManuellement_Intent = new Intent(ListeLotPreparationScanneeActivity.this, Creation_Lot_Stock_Emplacement.class);
                clicBoutonAjouterManuellement_Intent.putExtras(clicBoutonAjouterManuellement_Bundle);
                ListeLotPreparationScanneeActivity.this.startActivityForResult(clicBoutonAjouterManuellement_Intent, RETOUR_LOT);
            }

            qtePreparer = 0;
            for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte unListe_Stock_Et_Emplacement : lotAdapteList) {
                qtePreparer = qtePreparer + unListe_Stock_Et_Emplacement.getQteSaisie();
            }
        }
        else
        {
            List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> list_temp = new ArrayList<>();
            for(PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant : lotAdapteList)
            {
                if(!courant.getNumSerie().contentEquals(""))
                {
                    list_temp.add(courant);
                    qtePreparer = courant.getQteSaisie()+qtePreparer;
                }
            }

            lotAdapteList = new ArrayList<>();

            if(list_temp.size() != 0) {
                lotAdapteList.addAll(list_temp);
            }
            else
            {
                qtePreparer = 0;
            }
        }

        // Initialisation de quantiteDemandee, quantitePreparee, restantAPre
        quantiteDemandee = ph_preparation_ligne_courant.getQte_APreparer() - ph_preparation_ligne_courant.getQte_preparer();

        restantAPre = quantiteDemandee - qtePreparer;

        lotAdapteListView = (ListView) findViewById(R.id.listeView);
        lotAdapteListView.setItemsCanFocus(true);

        if(!gtin_ok && ph_preparation_ligne_courant.isSuivi_Par_Serie())
        {
            Alerte.afficherAlerte(ListeLotPreparationScanneeActivity.this, "Erreur", "Aucun GTIN renseigné pour le produit sélectionné, impossible d'ouvrir le scan", "alerte");
        }
        else
        {
            if(ph_preparation_ligne_courant.isSuivi_Par_Serie() && !ph_preparation_ligne_courant.isSerialiser_Reception())
            {
                if(qtePreparer < quantiteDemandee)
                {
                    camera_first = true;
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        onMenuDatamatrixClick();
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();


        if(!camera_first)
        {
            lotPreparationAdapter = new Lot_PreparationAdapter(ListeLotPreparationScanneeActivity.this, lotAdapteList, phPreparationLignePreparationAdapte, ph_preparation_ligne_courant.getQte_APreparer());
            lotAdapteListView.setDivider(footer);
            lotAdapteListView.setAdapter(lotPreparationAdapter);
        }
    }


    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    if(data != null)
                    {
                        camera_first = false;
                       // Map<String, String> liste_scanner = (Map<String, String>) data.getExtras().getSerializable("listeString");
                        liste_objet_scanne = (ArrayList<ObjetReceptionScannee>) data.getExtras().getSerializable("listeString");
                        if(liste_objet_scanne.size() != 0)
                        {
                            for(ObjetReceptionScannee objetReceptionScannee : liste_objet_scanne)
                            {
                                String code_courant = objetReceptionScannee.getGs1_scannee();
                                Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code_courant);
                                if (gs1Decoupe.size() != 0) {
                                    DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                    DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                    Date date = new Date();

                                    try {
                                        date = dateFormat1.parse(gs1Decoupe.get(OutilsDecodage.dateDePeremption));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    String dateFinale = dateFormat2.format(date);

                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
                                    Date dateFournie = null;
                                    try {
                                        dateFournie = dateDecodeur.parse(dateFinale);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String numSerie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                                    String last_charactere = numSerie.substring(numSerie.length()-1);
                                    if(last_charactere.contentEquals("@"))
                                    {
                                        numSerie = numSerie.substring(0, numSerie.length()-1);
                                    }

                                    String numLot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                                    last_charactere = numLot.substring(numLot.length()-1);
                                    if(last_charactere.contentEquals("@"))
                                    {
                                        numLot = numLot.substring(0, numLot.length()-1);
                                    }

                                    ObjetPreparationScannee objetPreparationScannee = new ObjetPreparationScannee(produit.getCond_achat(), numLot, dateFormat.format(dateFournie), produit.getEmplacement_PUI_Defaut(), depot.getDepot_Reference(), produit.getZone_PUI_Defaut(), produit.getID_produit(), objetReceptionScannee.getQuantiteScannee(), numSerie);
                                    PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte = phPreparationLignePreparationAdapte.new LotAdapte(objetPreparationScannee);

                                    boolean present = false;
                                    for(int i = 0; i < lotAdapteList.size(); i++)
                                    {
                                        String serie = lotAdapteList.get(i).getNumSerie().trim();
                                        if(serie.contentEquals(lotAdapte.getNumSerie().trim()))
                                        {
                                            present = true;
                                        }
                                    }

                                    if(!present)
                                    {
                                        lotAdapteList.add(lotAdapte);
                                    }
                                    phPreparationLignePreparationAdapte.setLotAdaptes(lotAdapteList);
                                    qtePreparer = objetPreparationScannee.getQte_Preparer();
                                    restantAPre = restantAPre - objetPreparationScannee.getQte_Preparer();
                                    ph_preparation_ligne_courant.setQte_preparer(qtePreparer);
                                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
                                    onResume();
                                }
                            }
                        }
                    }
                    break;
                case RETOUR_LOT:
                    String numLot = data.getExtras().getString("numLot");
                    String numSerie = data.getExtras().getString("numSerie");
                    String datePeremption = data.getExtras().getString("datePeremption");

                    double Qte = data.getExtras().getInt("qteActuelle",1);
                    String Lot = numLot;
                    String serie = numSerie;
                    String peremptionDate = datePeremption;
                    String Emplacement = data.getExtras().getString("nomEmplacement","");
                    String Depot_Reference = depot.getDepot_Reference();
                    String Zone = data.getExtras().getString("nomZone","");
                    int Produit_Code = produit.getID_produit();
                    int Qte_Preparer = 0;

                    if(Emplacement.contentEquals("")){
                        if(depot.getStructure().contentEquals("PUF")){
                            Emplacement = produit.getEmplacement_UF_Defaut();
                        }
                        else if(depot.getStructure().contentEquals("PAD")){
                            Emplacement = produit.getEmplacement_PAD_Defaut();
                        }
                        else{
                            Emplacement = produit.getEmplacement_PUI_Defaut();
                        }
                    }
                    if(Zone.contentEquals("")){
                        if(depot.getStructure().contentEquals("PUF")){
                            Zone = produit.getZone_UF_Defaut();
                        }
                        else if(depot.getStructure().contentEquals("PAD")){
                            Zone = produit.getZone_PAD_Defaut();
                        }
                        else{
                            Zone = produit.getZone_PUI_Defaut();
                        }
                    }


                    Stock_Lot_Emplacement_Light newStockLotEmplacement = new Stock_Lot_Emplacement_Light(Qte, Lot, peremptionDate, Emplacement, Depot_Reference, Zone, Produit_Code, Qte_Preparer, numSerie);

                    lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte(newStockLotEmplacement));
                    long rowID = gestionnaireStock_Lot_Emplacement.insererUnStock_Lot_EmplacementEnBDD(db, newStockLotEmplacement);
                    if (rowID != -1) {
                        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, newStockLotEmplacement.getPhiMR4UUID(), newStockLotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                    }
                    lotPreparationAdapter.notifyDataSetChanged();

                    break;
            }
        }
        invalidateOptionsMenu();
    }

    // On remet les quantités à 0 et on quitte l'activité
    @Override
    public void onBackPressed() {
        for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot : lotAdapteList
                ) {
            lot.setQteSaisie(0);
        }
        //super.onBackPressed();
        ListeLotPreparationScanneeActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        if(restantAPre == 0)
        {
            menu.findItem(R.id.menuDatamatrix).setVisible(false);
            menu.findItem(R.id.menuAdd).setVisible(false);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                menu.findItem(R.id.menuDatamatrix).setVisible(true);
            }
            else
            {
                menu.findItem(R.id.menuDatamatrix).setVisible(false);
            }

            menu.findItem(R.id.menuAdd).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        itemDatamatrix = menu.findItem(R.id.menuDatamatrix);
        if(qtePreparer < quantiteDemandee || !ph_preparation_ligne_courant.isSuivi_Par_Serie())
        {
            itemDatamatrix.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    onMenuDatamatrixClick();
                    return true;
                }
            });
        }
        else
        {
            itemDatamatrix.setVisible(false);
        }

        itemAdd = menu.findItem(R.id.menuAdd);
        itemAdd.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuAddClick();
                return true;
            }
        });

        prepareOptionsMenu(menu, lotPreparationAdapter, null, "Numéro de lot...");
        return true;
    }

    private void onMenuDatamatrixClick() {
        preparerListCodeScannee();
        Intent listeLotPreparation_Intent = new Intent(ListeLotPreparationScanneeActivity.this, BarcodeCaptureActivity.class);
        Bundle listeLotPreparation_Bundle = super.getBundle();
        String designation = produit.getDesignation_interne();
        listeLotPreparation_Bundle.putBoolean("doitEtreIdentique", true);
        listeLotPreparation_Bundle.putString("contexte", String.valueOf(R.string.scannerContextePreparationADHScanProduit));
        listeLotPreparation_Bundle.putString("Designation", designation);
        listeLotPreparation_Bundle.putBoolean("isBoutonSuppressionExistant", true);
        listeLotPreparation_Bundle.putInt("UserId", utilisateurConnecte.getId());
        listeLotPreparation_Bundle.putBoolean("modeRafale", true);
        listeLotPreparation_Bundle.putBoolean("ADH", true);
        listeLotPreparation_Bundle.putString("GTIN_courant", produit.getGTIN());
        listeLotPreparation_Bundle.putInt("preparationId", ph_preparation_ligne_courant.getPreparationID());
        listeLotPreparation_Bundle.putInt("preparationLigneId", ph_preparation_ligne_courant.get_UID());
        listeLotPreparation_Bundle.putStringArrayList("listeCodeScanne", (ArrayList<String>) list_code_scanne);

        listeLotPreparation_Intent.putExtras(listeLotPreparation_Bundle);
        ListeLotPreparationScanneeActivity.this.startActivityForResult(listeLotPreparation_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }

    private void onMenuAddClick()
    {
        Bundle clicBoutonAjouterManuellement_Bundle = ListeLotPreparationScanneeActivity.super.getBundle();
        clicBoutonAjouterManuellement_Bundle.putInt("produitID", produit.getID_produit());
        clicBoutonAjouterManuellement_Bundle.putInt("depotID", depot.getDepot_UID());

        Intent clicBoutonAjouterManuellement_Intent = new Intent(ListeLotPreparationScanneeActivity.this, Creation_Lot_Stock_Emplacement.class);
        clicBoutonAjouterManuellement_Intent.putExtras(clicBoutonAjouterManuellement_Bundle);
        ListeLotPreparationScanneeActivity.this.startActivityForResult(clicBoutonAjouterManuellement_Intent, RETOUR_LOT);
    }

    private void preparerListCodeScannee()
    {
        List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> listeCourante = phPreparationLignePreparationAdapte.getLotAdaptes();
        for(PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot : listeCourante)
        {
            if(!lot.getNumSerie().contentEquals(""))
            {
                String serie = lot.getNumSerie();
                String lotString = lot.getNumLot();
                String GTIN = produit.getGTIN();
                String peremption = lot.getDatePeremption();

                DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dateFormat2 = new SimpleDateFormat("yyMMdd");

                Date date = new Date();

                try {
                    date = dateFormat1.parse(peremption);
                    peremption =  dateFormat2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String nouveauGS1 = GTIN+"21"+serie+"@17"+peremption+"10"+lotString;

                list_code_scanne.add(nouveauGS1);
            }
        }
    }
}
