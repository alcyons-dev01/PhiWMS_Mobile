package fr.alcyons.phiwms_mobile.PreparationPUFetPAD;

import static com.google.android.gms.vision.L.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Lot_PreparationAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.PlanDePlacement.ListeZonesActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LISTE_LOTS;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by olivier on 18/04/2024.
 */

public class ListeLotPreparationActivity extends ServiceAvecConnexionActivity {

    public PH_Preparation_Ligne_Preparation_Adapte phPreparationLignePreparationAdapte;
    PH_Preparation_Ligne ph_preparation_ligne_courant;
    public List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotAdapteList;
    public ListView lotAdapteListView;
    Depot depot;
    Produit produit;
    Lot_PreparationAdapter lotPreparationAdapter;
    int quantiteDemandee;
    int restantAPre;
    Integer qtePreparer;
    String numeroLot;
    String dateDePeremption;
    String numeroSerie;
    boolean camera_first;
    List<String> list_lot;
    Stock_Lot_Emplacement_Light stock_courant;
    PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant;
    PackageManager pm;

    Context context;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lot_prepration);
        qtePreparer = 0;
        context = ListeLotPreparationActivity.this;
        //gestion du package manager
        pm = ListeLotPreparationActivity.this.getPackageManager();
        camera_first = false;

        // Récupération du ph_preparation_ligne, produit, depot sélectionné
        phPreparationLignePreparationAdapte = (PH_Preparation_Ligne_Preparation_Adapte) Objects.requireNonNull(intent.getExtras()).getSerializable("ph_preparationLigneAdapte");
        assert phPreparationLignePreparationAdapte != null;
        ph_preparation_ligne_courant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, phPreparationLignePreparationAdapte.getPh_preparationLigneID());
        produit = ProduitOpenHelper.getProduitByID(db, intent.getExtras().getInt("produitID"));
        depot = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotID"));

        numeroLot = intent.getExtras().getString("numeroLot");
        numeroSerie = intent.getExtras().getString("numSerie");
        dateDePeremption = intent.getExtras().getString("dateDePeremption");
        list_lot = intent.getExtras().getStringArrayList("liste_lot");

        // Affichage des informations de base
        PH_Preparation ph_preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparation_ligne_courant.getPreparationID());
        Depot depot_destinataire = DepotOpenHelper.getDepotParReference(db, ph_preparation.getDepotDestinataireReference());

        ((TextView) findViewById(R.id.intitule)).setText("#"+ph_preparation.getUID());

        String depotText = depot_destinataire.getNom();
        if(utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depot_destinataire.getStructure().contentEquals("PAD"))
        {
            depotText = "Patient - "+depot_destinataire.getPAD_IPP();
        }

        ((TextView) findViewById(R.id.depot)).setText(depotText);
        ((LinearLayout) findViewById(R.id.lancerScan)).setOnClickListener(view -> onMenuDatamatrixClick());

        ((TextView) findViewById(R.id.designationProduit)).setText(produit.getDesignation_interne());
        ((TextView) findViewById(R.id.referenceProduit)).setText(produit.getRef_fourni());

        if (produit == null || depot == null) {
            Alerte.afficherAlerte(ListeLotPreparationActivity.this, "Alerte", "Un problème a été constaté en Base de données, veuillez synchroniser l'application ou contacter la société Alcyons (service Préparation PAD", "alerte");
            ListeLotPreparationActivity.this.finish();
            return;
        }

        // Récupéeration des LOTS
        lotAdapteList = phPreparationLignePreparationAdapte.getLotAdaptes();
        boolean gtin_ok = !produit.getGTIN().contentEquals("");

        if(!ph_preparation_ligne_courant.isSuivi_Par_Serie() || ph_preparation_ligne_courant.isSerialiser_Reception() || !gtin_ok)
        {
            if (lotAdapteList.isEmpty()) {
                List<Stock_Lot_Emplacement_Light> stockLotEmplacementLights = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
                stockLotEmplacementLights.sort(Comparator.comparing(Stock_Lot_Emplacement_Light::getPeremptionDate));

                for (Stock_Lot_Emplacement_Light stockLotEmplacement : stockLotEmplacementLights) {
                    PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte = phPreparationLignePreparationAdapte.new LotAdapte(stockLotEmplacement);
                    lotAdapteList.add(lotAdapte);
                }
            }

            if (lotAdapteList.isEmpty()) {

                afficherAlerteAucunLot(ListeLotPreparationActivity.this, LayoutInflater.from(ListeLotPreparationActivity.this));

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

            if(!list_temp.isEmpty()) {
                lotAdapteList.addAll(list_temp);
            }
            else
            {
                qtePreparer = 0;
            }
        }

        lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_annuler"));

        quantiteDemandee = ph_preparation_ligne_courant.getQte_APreparer();

        restantAPre = quantiteDemandee;

        lotAdapteListView = (ListView) findViewById(R.id.listeView);
        lotAdapteListView.setItemsCanFocus(true);

        if(!gtin_ok && ph_preparation_ligne_courant.isSuivi_Par_Serie())
        {
            Alerte.afficherAlerte(ListeLotPreparationActivity.this, "Erreur", "Aucun GTIN renseigné pour le produit sélectionné, impossible d'ouvrir le scan", "alerte");
        }
        else
        {
            if(ph_preparation_ligne_courant.isSuivi_Par_Serie() && !ph_preparation_ligne_courant.isSerialiser_Reception())
            {
                if(qtePreparer < quantiteDemandee)
                {
                    //camera_first = true;
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                    {
                        onMenuDatamatrixClick();
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        lotAdapteListView.setOnItemClickListener((parent, view, position, id) -> {
            //on check la row cliquer
            courant = lotAdapteList.get(position);

            if(position == lotAdapteList.size()-1)
            {
                //si c'est le cas on cache les autres lignes
                ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange, null));

                for(int i = 0; i < lotAdapteList.size()-1; i++)
                {
                    if(!lotAdapteList.get(i).getNumLot().contentEquals("row_ajouter") && !lotAdapteList.get(i).getNumLot().contentEquals("row_annuler"))
                    {
                        lotAdapteList.get(i).setQteSaisie(0);
                        Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lotAdapteList.get(i).getStockLotEmplacementID());
                        if(stock_courant != null)
                        {
                            stock_courant.setQte_Preparer(courant.getQteSaisie());
                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                        }
                        else
                        {
                            if(courant != null)
                            {
                                String numLot = courant.getNumLot();
                                String numSerie = courant.getNumSerie();
                                if(numLot == null)
                                    numLot = "";

                                if(numSerie == null)
                                    numSerie = "";

                                stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(db, produit, depot, numLot, numSerie);
                                if(stock_courant != null)
                                {
                                    stock_courant.setQte_Preparer(courant.getQteSaisie());
                                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                }
                            }
                        }
                    }
                }

                ph_preparation_ligne_courant.setQte_APreparer(ph_preparation_ligne_courant.getQte_RAL());
                ph_preparation_ligne_courant.setQte_preparer(0);
                PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
                ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.VISIBLE);
                MAJVisuel();
                lotPreparationAdapter.quantiteAPreparer = ph_preparation_ligne_courant.getQte_APreparer();
                lotPreparationAdapter.full = false;
            }
            else
            {
                //on check si le lot n'est pas déjà n'est pas déjà sélectionner
                if(Integer.parseInt(lotPreparationAdapter.viewHolders.get(position).qteSaisie.getText().toString()) == 0)
                {
                    int quantite_stock_selectionne = getQuantiteStockSelectionne();

                    //gestion du visuel
                    courant.setQteSaisie(quantite_stock_selectionne);
                    stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, courant.getStockLotEmplacementID());
                    if(stock_courant != null)
                    {
                        stock_courant.setQte_Preparer(courant.getQteSaisie());
                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                    }
                    else
                    {
                        stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(db, produit, depot, courant.getNumLot(), courant.getNumSerie());
                        if(stock_courant != null)
                        {
                            stock_courant.setQte_Preparer(courant.getQteSaisie());
                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                        }
                    }

                    ph_preparation_ligne_courant.setQte_preparer(ph_preparation_ligne_courant.getQte_preparer()+quantite_stock_selectionne);
                    ph_preparation_ligne_courant.setQte_APreparer(ph_preparation_ligne_courant.getQte_APreparer()-quantite_stock_selectionne);
                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
                    ((TextView) findViewById(R.id.QtePreparer)).setText(String.valueOf(ph_preparation_ligne_courant.getQte_RAL()-ph_preparation_ligne_courant.getQte_APreparer()));
                    lotPreparationAdapter.viewHolders.get(position).qteSaisie.setText(String.valueOf(quantite_stock_selectionne));

                    if(stock_courant.getEmplacement().contentEquals(""))
                    {
                        Intent listeZonesIntent = new Intent(ListeLotPreparationActivity.this, ListeZonesActivity.class);
                        Bundle listeZonesBundle = ListeLotPreparationActivity.super.getBundle();
                        Depot depotpui = DepotOpenHelper.getDepotPUI(db);
                        listeZonesBundle.putInt("depotSelectionneID", depotpui.getDepot_UID());
                        listeZonesIntent.putExtras(listeZonesBundle);
                        ListeLotPreparationActivity.this.startActivityForResult(listeZonesIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                 }
                else
                {
                    int qte_Saisie = Integer.parseInt(lotPreparationAdapter.viewHolders.get(position).qteSaisie.getText().toString());
                    ph_preparation_ligne_courant.setQte_preparer(ph_preparation_ligne_courant.getQte_preparer()-qte_Saisie);
                    ph_preparation_ligne_courant.setQte_APreparer(ph_preparation_ligne_courant.getQte_APreparer()+qte_Saisie);
                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
                    ((TextView) findViewById(R.id.QtePreparer)).setText(String.valueOf(ph_preparation_ligne_courant.getQte_RAL()-ph_preparation_ligne_courant.getQte_APreparer()));
                    lotPreparationAdapter.viewHolders.get(position).qteSaisie.setText("0");
                    lotPreparationAdapter.quantiteAPreparer = ph_preparation_ligne_courant.getQte_APreparer();
                    courant.setQteSaisie(0);
                    stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, courant.getStockLotEmplacementID());
                    if(stock_courant != null)
                    {
                        stock_courant.setQte_Preparer(courant.getQteSaisie());
                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                    }
                    else
                    {
                        stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(db, produit, depot, courant.getNumLot(), courant.getNumSerie());
                        if(stock_courant != null)
                        {
                            stock_courant.setQte_Preparer(courant.getQteSaisie());
                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                        }
                    }
                }
                MAJVisuel();
            }

            lotPreparationAdapter.notifyDataSetChanged();
        });

        if(!camera_first)
        {
            lotPreparationAdapter = new Lot_PreparationAdapter(ListeLotPreparationActivity.this, lotAdapteList, phPreparationLignePreparationAdapte, ph_preparation_ligne_courant.getQte_APreparer());
            lotAdapteListView.setDivider(footer);
            lotAdapteListView.setAdapter(lotPreparationAdapter);
            MAJVisuel();
        }

        int nbColis = recupererNbColis(produit.getID_produit(), ph_preparation_ligne_courant.getQte_APreparer());
        ((TextView) findViewById(R.id.colis)).setText(String.valueOf(nbColis));
    }

    private int getQuantiteStockSelectionne() {
        int reste_a_preparer = ph_preparation_ligne_courant.getQte_APreparer();
        int quantite_stock_selectionne = courant.getQteStock();

        if(quantite_stock_selectionne > reste_a_preparer)
        {
            quantite_stock_selectionne = reste_a_preparer;
        }
        else if(quantite_stock_selectionne > ph_preparation_ligne_courant.getQte_APreparer())
        {
            quantite_stock_selectionne = ph_preparation_ligne_courant.getQte_APreparer();
        }
        return quantite_stock_selectionne;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    int EmplacementId = Objects.requireNonNull(data.getExtras()).getInt("emplacementSelectionneID");
                    Depot_Emplacement emplacementSelectionne = EmplacementOpenHelper.getUnEmplacementByID(db, EmplacementId);
                    stock_courant.setEmplacement(emplacementSelectionne.getAdressage());
                    Depot_Zone zoneSelectionne = ZoneOpenHelper.getUneZoneByID(db, emplacementSelectionne.getZoneID());
                    stock_courant.setZone(zoneSelectionne.getZoneName());
                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                    courant.setEmplacement(emplacementSelectionne.getAdressage());
                    int position = lotAdapteList.indexOf(courant);
                    if(position != -1)
                    {
                        lotPreparationAdapter.viewHolders.get(position).nomEmplacement.setText(emplacementSelectionne.getAdressage());
                    }
                    break;
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    camera_first = false;
                    list_lot = new ArrayList<>();
                    list_lot = Objects.requireNonNull(data.getExtras()).getStringArrayList("liste_lot");
                    lotAdapteList = new ArrayList<>();
                    lotAdapteList = (List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>) data.getExtras().getSerializable("lotAdapteList");
                    assert lotAdapteList != null;
                    lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_ajouter"));
                    lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_annuler"));
                    phPreparationLignePreparationAdapte.setLotAdaptes(lotAdapteList);
                    ph_preparation_ligne_courant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, phPreparationLignePreparationAdapte.getPh_preparationLigneID());
                    ((TextView) findViewById(R.id.QtePreparer)).setText(String.valueOf(ph_preparation_ligne_courant.getQte_preparer()));
                    MAJVisuel();
                    lotPreparationAdapter.quantiteAPreparer = ph_preparation_ligne_courant.getQte_APreparer();
                    onResume();

                    break;
                    case RETOUR_LOT:
                        String numLot = Objects.requireNonNull(data.getExtras()).getString("numLot");
                        String numSerie = data.getExtras().getString("numSerie");
                        String datePeremption = data.getExtras().getString("datePeremption");

                        double Qte = data.getExtras().getInt("qteActuelle",1);
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
                        
                        Stock_Lot_Emplacement_Light newStockLotEmplacement = new Stock_Lot_Emplacement_Light(Qte, numLot, datePeremption, Emplacement, Depot_Reference, Zone, Produit_Code, Qte_Preparer, numSerie);

                        MAJListeLot();
                        long rowID = Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, newStockLotEmplacement);
                        if (rowID != -1) {
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, newStockLotEmplacement.getPhiMR4UUID(), newStockLotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                            ElementASynchroniserOpenHelper.toutSynchroniser(ListeLotPreparationActivity.this, db, utilisateurConnecte, true);
                            newStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByPhiMR4UUID(db, newStockLotEmplacement.getPhiMR4UUID());
                        }
                        lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte(newStockLotEmplacement));
                        lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_ajouter"));
                        lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_annuler"));
                        lotPreparationAdapter.notifyDataSetChanged();
                        lotAdapteListView.performItemClick(lotAdapteListView.getAdapter().getView(lotAdapteList.size()-3, null, null), lotAdapteList.size()-3, lotAdapteListView.getAdapter().getItemId(lotAdapteList.size()-3));

                        break;
                }
            }
            invalidateOptionsMenu();
        }

    // On remet les quantités à 0 et on quitte l'activité
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onMenuSaveClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);

        menu.findItem(R.id.menuSaveCircle).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemSave = menu.findItem(R.id.menuSaveCircle);

        itemSave.setOnMenuItemClickListener(menuItem -> {
            onMenuSaveClick();
            return true;
        });

        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void onMenuSaveClick()
    {
        MAJListeLot();
        String erreur = verificationDisponibilite();

        if(erreur.contentEquals(""))
        {
            Intent resultIntent = new Intent();

            Bundle extras = ListeLotPreparationActivity.super.getBundle();
            if(lotAdapteList!= null)
                extras.putSerializable("lotAdaptes", (Serializable) lotAdapteList);
            if(list_lot != null)
                extras.putStringArrayList("liste_lot", (ArrayList<String>) list_lot);
            resultIntent.putExtras(extras);

            ListeLotPreparationActivity.this.setResult(RETOUR_LISTE_LOTS, resultIntent);
            ListeLotPreparationActivity.this.finish();
        }
        else
        {
            Alerte.afficherAlerte(context, "Erreur", erreur, "alerte");
           ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange, null));

            for(int i = 0; i < lotAdapteList.size()-1; i++)
            {
                if(!lotAdapteList.get(i).getNumLot().contentEquals("row_ajouter") && !lotAdapteList.get(i).getNumLot().contentEquals("row_annuler"))
                {
                    lotAdapteList.get(i).setQteSaisie(0);
                    Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lotAdapteList.get(i).getStockLotEmplacementID());
                    if(stock_courant != null)
                    {
                        stock_courant.setQte_Preparer(courant.getQteSaisie());
                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                    }
                    else
                    {
                        if(courant != null)
                        {
                            String numLot = courant.getNumLot();
                            String numSerie = courant.getNumSerie();
                            if(numLot == null)
                                numLot = "";

                            if(numSerie == null)
                                numSerie = "";

                            stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(db, produit, depot, numLot, numSerie);
                            if(stock_courant != null)
                            {
                                stock_courant.setQte_Preparer(courant.getQteSaisie());
                                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                            }
                        }
                    }
                }
            }

            ph_preparation_ligne_courant.setQte_APreparer(ph_preparation_ligne_courant.getQte_RAL());
            ph_preparation_ligne_courant.setQte_preparer(0);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.VISIBLE);
            MAJVisuel();
            lotPreparationAdapter.quantiteAPreparer = ph_preparation_ligne_courant.getQte_APreparer();
            lotPreparationAdapter.full = false;
            Intent resultIntent = new Intent();

            Bundle extras = ListeLotPreparationActivity.super.getBundle();
            if(lotAdapteList!= null)
                extras.putSerializable("lotAdaptes", (Serializable) lotAdapteList);
            if(list_lot != null)
                extras.putStringArrayList("liste_lot", (ArrayList<String>) list_lot);
            resultIntent.putExtras(extras);

            ListeLotPreparationActivity.this.setResult(RETOUR_LISTE_LOTS, resultIntent);
            ListeLotPreparationActivity.this.finish();
        }
    }

    private void onMenuDatamatrixClick() {
        MAJListeLot();

        Intent listeLotPreparation_Intent = new Intent(ListeLotPreparationActivity.this, BarcodePreparationActivity.class);
        //gestion du zebra
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            listeLotPreparation_Intent = new Intent(ListeLotPreparationActivity.this, ScannerPreparationActivity.class);
        }
        Bundle listeLotPreparation_Bundle = super.getBundle();
        String designation = produit.getDesignation_interne();
        listeLotPreparation_Bundle.putBoolean("doitEtreIdentique", true);
        listeLotPreparation_Bundle.putString("contexte", String.valueOf(R.string.scannerContextPreparationSimple));
        listeLotPreparation_Bundle.putString("Designation", designation);
        listeLotPreparation_Bundle.putBoolean("isBoutonSuppressionExistant", true);
        listeLotPreparation_Bundle.putInt("UserId", utilisateurConnecte.getId());
        listeLotPreparation_Bundle.putBoolean("modeRafale", true);
        listeLotPreparation_Bundle.putString("GTIN_courant", produit.getGTIN());
        listeLotPreparation_Bundle.putInt("preparationId", ph_preparation_ligne_courant.getPreparationID());
        listeLotPreparation_Bundle.putInt("preparationLigneId", ph_preparation_ligne_courant.get_UID());
        listeLotPreparation_Bundle.putSerializable("lotAdapteList", (Serializable) phPreparationLignePreparationAdapte.getLotAdaptes());
        listeLotPreparation_Bundle.putSerializable("ph_preparationLigneAdapte", (Serializable) phPreparationLignePreparationAdapte);
        listeLotPreparation_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) list_lot);

        listeLotPreparation_Intent.putExtras(listeLotPreparation_Bundle);
        ListeLotPreparationActivity.this.startActivityForResult(listeLotPreparation_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }


    public void ClickNumberPicker(final int position)
    {
        Context context = ListeLotPreparationActivity.this;
        PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant = lotAdapteList.get(position);
        final Stock_Lot_Emplacement_Light[] stock_courant = {Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, courant.getStockLotEmplacementID())};

        if(stock_courant[0] == null)
        {
            stock_courant[0] = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(db, produit, depot, courant.getNumLot(), courant.getNumSerie());
            if(stock_courant[0] != null)
            {
                stock_courant[0].setQte_Preparer(courant.getQteSaisie());
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant[0]);
            }
        }

        String title = lotPreparationAdapter.viewHolders.get(position).lot.getText().toString();
        String message = "Quantité placée : ";

        //gestion d'un stock déjà saisie
        if(courant.getQteSaisie() > 0)
        {
            int qte_avant = courant.getQteSaisie();
            courant.setQteSaisie(0);
            stock_courant[0].setQte_Preparer(courant.getQteSaisie());
            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant[0]);
            ph_preparation_ligne_courant.setQte_preparer(ph_preparation_ligne_courant.getQte_preparer()-qte_avant);
            ph_preparation_ligne_courant.setQte_APreparer(ph_preparation_ligne_courant.getQte_APreparer()+qte_avant);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
            MAJVisuel();
            lotPreparationAdapter.quantiteAPreparer = ph_preparation_ligne_courant.getQte_APreparer();
        }

        int value_max = (int) stock_courant[0].getQte();
        int reste = lotPreparationAdapter.quantiteAPreparer;
        if(value_max > reste)
            value_max = reste;

        int maxValue = value_max;

        DialogInterface.OnClickListener onClickListener = (dialog, id) -> {
            PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant1 = lotAdapteList.get(position);
            int qteApres = aNumberPicker.getValue();
            ph_preparation_ligne_courant.setQte_preparer(ph_preparation_ligne_courant.getQte_preparer()+qteApres);
            ph_preparation_ligne_courant.setQte_APreparer(ph_preparation_ligne_courant.getQte_APreparer()-qteApres);
            lotAdapteList.get(position).setQteSaisie(qteApres);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
            ((TextView) findViewById(R.id.QtePreparer)).setText(String.valueOf(ph_preparation_ligne_courant.getQte_preparer()));

            lotPreparationAdapter.viewHolders.get(position).qteSaisie.setText(String.valueOf(qteApres));
            courant1.setQteSaisie(qteApres);
            if(stock_courant[0] != null)
            {
                stock_courant[0].setQte_Preparer(courant1.getQteSaisie());
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant[0]);
            }
            else
            {
                stock_courant[0] = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(db, produit, depot, courant1.getNumLot(), courant1.getNumSerie());
                if(stock_courant[0] != null)
                {
                    stock_courant[0].setQte_Preparer(courant1.getQteSaisie());
                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant[0]);
                }
            }
            lotPreparationAdapter.quantiteAPreparer = ph_preparation_ligne_courant.getQte_APreparer();
            MAJVisuel();
            lotPreparationAdapter.notifyDataSetChanged();
            InputMethodManager imm = (InputMethodManager) ListeLotPreparationActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            dialog.dismiss();
        };

        Alerte.afficherAlerteNumberPicker(context, title, message, maxValue, maxValue, onClickListener);
    }

    private void MAJListeLot()
    {
        int index = -1;
        for(int i = 0; i < lotAdapteList.size(); i++)
        {
            if(lotAdapteList.get(i).getNumLot().contentEquals("row_annuler"))
            {
                index = i;
                break;
            }
        }
        if(index != -1)
            lotAdapteList.remove(index);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void MAJVisuel()
    {
        ((TextView) findViewById(R.id.QteDemandee)).setText(String.valueOf(ph_preparation_ligne_courant.getQte_RAL()));
        ((TextView) findViewById(R.id.QtePreparer)).setText(String.valueOf(ph_preparation_ligne_courant.getQte_RAL()-ph_preparation_ligne_courant.getQte_APreparer()));

        if(ph_preparation_ligne_courant.getQte_APreparer() == 0)
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_vert, null));
            ((TextView) findViewById(R.id.QteDemandee)).setTextColor(ListeLotPreparationActivity.this.getResources().getColor(R.color.vert, null));
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.QtePreparer)).setVisibility(View.INVISIBLE);

            lotPreparationAdapter.full = true;
        }
        else
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparationActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange, null));
            ((TextView) findViewById(R.id.QtePreparer)).setTextColor(ListeLotPreparationActivity.this.getResources().getColor(R.color.orange2, null));
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.QtePreparer)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.QteDemandee)).setTextColor(ListeLotPreparationActivity.this.getResources().getColor(R.color.noir, null));


            lotPreparationAdapter.full = false;
        }
    }

    public int recupererNbColis(int produitID, double qte) {
        int nbColis = 0;

        int conditionnementAchat = 0;
        int quantite = (int) qte;

        if (produitID != 0) {
            Produit produitCorrespondant = ProduitOpenHelper.getProduitByID(db, produitID);
            conditionnementAchat = produitCorrespondant.getCond_achat();
            if (conditionnementAchat == 0) {
                conditionnementAchat = (int) produitCorrespondant.getCond_distrib();
            }
        }
        if (quantite != 0 && conditionnementAchat != 0) {
            nbColis = quantite / conditionnementAchat;
            nbColis = (int) (double) nbColis;
        }
        if (quantite != 0) {
            if (nbColis == 0) {
                nbColis = 1;
            }
        }

        return nbColis;
    }


    public void afficherAlerteAucunLot(Context context, LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_aucun_lot, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            ListeLotPreparationActivity.this.finish();
        });
    }

    private String verificationDisponibilite()
    {
        final String[] disponible = {""};

        for(final PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotcourant : lotAdapteList)
        {
            if(lotcourant.getQteSaisie() != 0)
            {
                final String lot = lotcourant.getNumLot();
                final String emplacement = lotcourant.getEmplacement();
                int produitCode = produit.getID_produit();
                String depotReference = depot.getDepot_Reference();
                final int quantiteSaisie = lotcourant.getQteSaisie();
                final String nomReference = produit.getDesignation_interne();

                if (OutilsGestionConnexionReseau.isServerAccessible(ListeLotPreparationActivity.this))
                {
                    RequestQueue requestQueue = Volley.newRequestQueue(ListeLotPreparationActivity.this);
                    String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteStock_Lot_Emplacements+"produit/"+ produitCode +"/depot/"+depotReference+"/"+lot+"/"+emplacement+"/verification";
                    JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                            response -> {
                                try {
                                    int nbResultat = response.getInt("resultCount");
                                    if (nbResultat == 0) {
                                        String erreur = response.getString("erreur");
                                        if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                            Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                        } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                            Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                        } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                            Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Aucune ligne trouvée", "alerte");
                                        }
                                    } else {
                                        JSONArray ph_stock_lot_JSONArray = response.getJSONArray("PH_Stock_Lot_Emplacements");
                                        for (int k = 0; k < ph_stock_lot_JSONArray.length(); k++) {
                                            JSONObject ph_stock_lot_JSONObject = ph_stock_lot_JSONArray.getJSONObject(k);
                                            int qte_stock = ph_stock_lot_JSONObject.getInt("Qte");
                                            if(qte_stock < quantiteSaisie)
                                            {
                                                disponible[0] = disponible[0] +" "+nomReference+" - Lot : "+lot+" stock insufisant";
                                                lotcourant.setQteStock(qte_stock);
                                                lotcourant.setQteSaisie(0);
                                                Stock_Lot_Emplacement_Light stock = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(db, produit, depot, courant.getNumLot(), courant.getNumSerie());
                                                if(stock != null)
                                                {
                                                    stock.setQte(qte_stock);
                                                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock);
                                                }

                                                for(PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapteCourant : lotAdapteList)
                                                {
                                                    if(lotAdapteCourant.getNumLot().contentEquals(lot) && lotAdapteCourant.getEmplacement().contentEquals(emplacement))
                                                    {
                                                        lotAdapteCourant.setQteStock(qte_stock);
                                                    }
                                                }

                                                phPreparationLignePreparationAdapte.setLotAdaptes(lotAdapteList);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.e("JSON Exception", Objects.requireNonNull(e.getMessage()));
                                }
                                handler.sendMessage(handler.obtainMessage());
                            },
                            error -> {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(ListeLotPreparationActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : vérification stock)", "alerte");
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", utilisateurConnecte.getToken());
                            return headers;
                        }
                    };
                    obreq.setRetryPolicy(retryPolicy);
                    requestQueue.add(obreq);
                    try {
                        Looper.loop();
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Error Looper :", e);
                    }
                }
            }

        }

        return disponible[0];
    }
}
