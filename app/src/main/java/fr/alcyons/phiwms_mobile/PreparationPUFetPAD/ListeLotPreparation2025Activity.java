package fr.alcyons.phiwms_mobile.PreparationPUFetPAD;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.Alerte.afficherAlerte;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LISTE_LOTS;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
import fr.alcyons.phiwms_mobile.PlanDePlacement.ListeZonesActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ListeLotPreparation2025Activity  extends ServiceAvecConnexionActivity {

    public PH_Preparation_Ligne_Preparation_Adapte phPreparationLignePreparationAdapte;
    PH_Preparation_Ligne ph_preparation_ligne_base;
    public List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotAdapteList;
    RecyclerView recyclerView;
    Depot depot;
    Produit produit;
    //Lot_PreparationAdapter lotPreparationAdapter;
    LotAdapter adapter;
    boolean camera_first;
    List<String> list_lot;
    Stock_Lot_Emplacement_Light stock_courant;
    PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant;
    PackageManager pm;
    Context context;

    boolean produitsuiviserie;
    boolean produitserialiserreception;
    int quantiteDemandeeBase;
    int restantAPrepaper;
    Integer qteDejaPreparer;
    List<PH_Preparation_Ligne> phPreparationLignesPreparer;
    PH_Preparation ph_preparation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lot_prepration);
        context = ListeLotPreparation2025Activity.this;
        pm = ListeLotPreparation2025Activity.this.getPackageManager();
        camera_first = false;

        // Récupération du ph_preparation_ligne, produit, depot sélectionné
        phPreparationLignePreparationAdapte = (PH_Preparation_Ligne_Preparation_Adapte) intent.getExtras().getSerializable("ph_preparationLigneAdapte");
        ph_preparation_ligne_base = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, phPreparationLignePreparationAdapte.getPh_preparationLigneID());
        ph_preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparation_ligne_base.getPreparationID());
        phPreparationLignesPreparer = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, ph_preparation, ph_preparation_ligne_base.getProduitID());
        produit = ProduitOpenHelper.getProduitByID(db, intent.getExtras().getInt("produitID"));
        depot = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotID"));
        list_lot = intent.getExtras().getStringArrayList("liste_lot");
        String depotText = ph_preparation.getDepotDestinataireReference();
        Depot depot_destinataire = DepotOpenHelper.getDepotParReference(db, ph_preparation.getDepotDestinataireReference());

        if (produit == null || depot == null) {
            Alerte.afficherAlerte(ListeLotPreparation2025Activity.this, "Alerte", "Un problème a été constaté en Base de données, veuillez synchroniser l'application ou contacter la société Alcyons (service Préparation", "alerte");
            ListeLotPreparation2025Activity.this.finish();
            return;
        }

        produitserialiserreception = produit.isSerialiser_Reception_Delivrance();
        produitsuiviserie = produit.isSuivi_Serialisation();

        quantiteDemandeeBase = ph_preparation_ligne_base.getQte_APreparer();
        qteDejaPreparer = 0;
        for(PH_Preparation_Ligne lignecourante : phPreparationLignesPreparer)
        {
            qteDejaPreparer = qteDejaPreparer + lignecourante.getQte_preparer();
        }
        restantAPrepaper = quantiteDemandeeBase - qteDejaPreparer;

        if(depot_destinataire != null)
        {
            depotText = depot_destinataire.getNom();

            if(utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depot_destinataire.getStructure().contentEquals("PAD"))
            {
                depotText = "Patient - "+depot_destinataire.getPAD_IPP();
            }
        }

        ((TextView) findViewById(R.id.depot)).setText(depotText);
        ((TextView) findViewById(R.id.intitule)).setText("#"+ph_preparation.getUID());
        ((TextView) findViewById(R.id.designationProduit)).setText(produit.getDesignation_interne());
        ((TextView) findViewById(R.id.referenceProduit)).setText(produit.getRef_fourni());

        ((LinearLayout) findViewById(R.id.lancerScan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuDatamatrixClick();
            }
        });

        // Récupéeration des LOTS
        lotAdapteList = phPreparationLignePreparationAdapte.getLotAdaptes();
        boolean gtin_ok = !produit.getGTIN().contentEquals("");

        if(!produitsuiviserie || produitserialiserreception || !gtin_ok)
        {
            if (lotAdapteList.isEmpty()) {
                List<Stock_Lot_Emplacement_Light> stockLotEmplacementLights = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
                Collections.sort(stockLotEmplacementLights, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getPeremptionDate().compareTo(o2.getPeremptionDate());
                    }
                });

                for (Stock_Lot_Emplacement_Light stockLotEmplacement : stockLotEmplacementLights) {
                    PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte = phPreparationLignePreparationAdapte.new LotAdapte(stockLotEmplacement);
                    lotAdapteList.add(lotAdapte);
                }
            }

            if (lotAdapteList.isEmpty()) {
                afficherAlerteAucunLot(ListeLotPreparation2025Activity.this, LayoutInflater.from(ListeLotPreparation2025Activity.this));
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
                    //qtePreparer = courant.getQteSaisie()+qtePreparer;
                }
            }

            lotAdapteList = new ArrayList<>();

            if(!list_temp.isEmpty()) {
                lotAdapteList.addAll(list_temp);
            }
            else
            {
                //qtePreparer = 0;
            }
        }

        //lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_ajouter"));
        //lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_annuler"));

        // Initialisation de quantiteDemandee, quantitePreparee, restantAPre


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(!gtin_ok && produitsuiviserie)
        {
            Alerte.afficherAlerte(ListeLotPreparation2025Activity.this, "Erreur", "Aucun GTIN renseigné pour le produit sélectionné, impossible d'ouvrir le scan", "alerte");
        }
        else
        {
            if(produitsuiviserie && !produitserialiserreception)
            {
                if(qteDejaPreparer < quantiteDemandeeBase)
                {
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

        adapter = new LotAdapter(lotAdapteList, position -> {
            Toast.makeText(this, "Supprimer " + lotAdapteList.get(position), Toast.LENGTH_SHORT).show();



            // Tu peux appeler confirm dialog ici
        }, ListeLotPreparation2025Activity.this);

        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Ne supprime pas
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                View foreground = ((LotAdapter.LotViewHolder) viewHolder).contentLayout;
                float clampedDx = Math.min(dX, 300); // limite le déplacement
                foreground.setTranslationX(clampedDx);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.5f; // Ne déclenche pas la suppression native
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        /*lotAdapteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                //on check la row cliquer
                courant = lotAdapteList.get(position);

                if(position == lotAdapteList.size()-1)
                {
                    //si c'est le cas on cache les autres lignes
                    ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparation2025Activity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));

                    for(int i = 0; i < lotAdapteList.size()-1; i++)
                    {
                        if(!lotAdapteList.get(i).getNumLot().contentEquals("row_ajouter") && !lotAdapteList.get(i).getNumLot().contentEquals("row_annuler"))
                        {
                            lotAdapteList.get(i).setQteStock(lotAdapteList.get(i).getQteStock() + lotAdapteList.get(i).getQteSaisie());
                            lotAdapteList.get(i).setQteSaisie(0);
                            Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lotAdapteList.get(i).getStockLotEmplacementID());
                            if(stock_courant != null)
                            {
                                stock_courant.setQte_Preparer(courant.getQteSaisie());
                                stock_courant.setQte(lotAdapteList.get(i).getQteStock());
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
                                        stock_courant.setQte(lotAdapteList.get(i).getQteStock());
                                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                    }
                                }
                            }
                        }
                    }

                    MAJValues(false, qteDejaPreparer);
                    ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.VISIBLE);
                    MAJVisuel();
                    lotPreparationAdapter.quantiteAPreparer = restantAPrepaper;
                    lotPreparationAdapter.full = false;
                }
                else if(courant.getNumLot().contentEquals("row_ajouter"))
                {
                    MAJListeLot();
                    clicAjoutManuel();
                }
                else
                {
                    //on check si le lot n'est pas déjà n'est pas déjà sélectionner
                    if(Integer.parseInt(lotPreparationAdapter.viewHolders.get(position).qteSaisie.getText().toString()) == 0)
                    {
                        //on récupére la quantité de stock présent fans ce lot
                        if(lotPreparationAdapter.viewHolders.get(position).lot.getText().toString().contentEquals(""))
                        {
                            afficherAlerte(ListeLotPreparation2025Activity.this, "Erreur", "Vous ne pouvez pas sélectionner un lot vide", "alerte");
                        }
                        else
                        {
                            int quantite_stock_selectionne = courant.getQteStock();

                            if(quantite_stock_selectionne > restantAPrepaper)
                            {
                                quantite_stock_selectionne = restantAPrepaper;
                            }

                            //gestion du visuel
                            courant.setQteSaisie(quantite_stock_selectionne);
                            courant.setQteStock(courant.getQteStock()-quantite_stock_selectionne);
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

                            MAJValues(true, quantite_stock_selectionne);
                            lotPreparationAdapter.viewHolders.get(position).qteSaisie.setText(String.valueOf(quantite_stock_selectionne));

                            if(stock_courant.getEmplacement().contentEquals(""))
                            {
                                Intent listeZonesIntent = new Intent(ListeLotPreparation2025Activity.this, ListeZonesActivity.class);
                                Bundle listeZonesBundle = ListeLotPreparation2025Activity.super.getBundle();
                                Depot depotpui = DepotOpenHelper.getDepotPUI(db);
                                listeZonesBundle.putInt("depotSelectionneID", depotpui.getDepot_UID());
                                listeZonesIntent.putExtras(listeZonesBundle);
                                ListeLotPreparation2025Activity.this.startActivityForResult(listeZonesIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                            }
                        }
                    }
                    else
                    {
                        int qte_Saisie = Integer.parseInt(lotPreparationAdapter.viewHolders.get(position).qteSaisie.getText().toString());
                        MAJValues(false, qte_Saisie);
                        lotPreparationAdapter.viewHolders.get(position).qteSaisie.setText("0");
                        lotPreparationAdapter.quantiteAPreparer = restantAPrepaper;
                        courant.setQteSaisie(0);
                        courant.setQteStock(courant.getQteStock()+qte_Saisie);
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

                    //on regarde si toute la quantité est préparer
                    MAJVisuel();
                }

                lotPreparationAdapter.notifyDataSetChanged();
            }
        });*/

        if(!camera_first)
        {
            int index_ajout = -1;
            int index_suppression = -1;
            int compteur = 0;
            for(PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant : lotAdapteList)
            {
                if(courant.getNumLot().contentEquals("row_ajouter"))
                    index_ajout = compteur;

                if(courant.getNumLot().contentEquals("row_annuler"))
                    index_suppression = compteur;

                compteur ++;
            }

            if(index_ajout == -1)
                lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_ajouter"));

            if(index_suppression == -1)
                lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_annuler"));

            /*lotPreparationAdapter = new Lot_PreparationAdapter(ListeLotPreparation2025Activity.this, lotAdapteList, phPreparationLignePreparationAdapte, restantAPrepaper);
            lotAdapteListView.setDivider(footer);
            lotAdapteListView.setAdapter(lotPreparationAdapter);*/
            MAJVisuel();
        }

        int nbColis = recupererNbColis(produit.getID_produit(), ph_preparation_ligne_base.getQte_APreparer());
        ((TextView) findViewById(R.id.colis)).setText(String.valueOf(nbColis));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    int EmplacementId = data.getExtras().getInt("emplacementSelectionneID");
                    Depot_Emplacement emplacementSelectionne = EmplacementOpenHelper.getUnEmplacementByID(db, EmplacementId);
                    stock_courant.setEmplacement(emplacementSelectionne.getAdressage());
                    Depot_Zone zoneSelectionne = ZoneOpenHelper.getUneZoneByID(db, emplacementSelectionne.getZoneID());
                    stock_courant.setZone(zoneSelectionne.getZoneName());
                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                    courant.setEmplacement(emplacementSelectionne.getAdressage());
                    int position = lotAdapteList.indexOf(courant);
                    if(position != -1)
                    {
                        //lotPreparationAdapter.viewHolders.get(position).nomEmplacement.setText(emplacementSelectionne.getAdressage());
                    }
                    break;
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    if(data != null)
                    {
                        camera_first = false;
                        list_lot = new ArrayList<>();
                        list_lot = data.getExtras().getStringArrayList("liste_lot");
                        lotAdapteList = new ArrayList<>();
                        lotAdapteList = (List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>) data.getExtras().getSerializable("lotAdapteList");
                        lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_ajouter"));
                        lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_annuler"));
                        phPreparationLignePreparationAdapte.setLotAdaptes(lotAdapteList);
                        ph_preparation_ligne_base = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, phPreparationLignePreparationAdapte.getPh_preparationLigneID());
                        MAJVisuel();
                        //lotPreparationAdapter.quantiteAPreparer = restantAPrepaper;
                        onResume();
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

                    MAJListeLot();
                    long rowID = Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, newStockLotEmplacement);
                    if (rowID != -1) {
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, newStockLotEmplacement.getPhiMR4UUID(), newStockLotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                        ElementASynchroniserOpenHelper.toutSynchroniser(ListeLotPreparation2025Activity.this, db, utilisateurConnecte, true);
                        newStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByPhiMR4UUID(db, newStockLotEmplacement.getPhiMR4UUID());
                    }
                    lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte(newStockLotEmplacement));
                    //lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_ajouter"));
                    //lotAdapteList.add(phPreparationLignePreparationAdapte.new LotAdapte("row_annuler"));
                    //lotPreparationAdapter.notifyDataSetChanged();
                    //lotAdapteListView.performItemClick(lotAdapteListView.getAdapter().getView(lotAdapteList.size()-3, null, null), lotAdapteList.size()-3, lotAdapteListView.getAdapter().getItemId(lotAdapteList.size()-3));

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

        itemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onMenuSaveClick();
                return true;
            }
        });

        return true;
    }

    private void onMenuSaveClick() {
        MAJListeLot();
        String erreur = verificationDisponibilite();

        if(erreur.contentEquals(""))
        {
            Intent resultIntent = new Intent();

            Bundle extras = ListeLotPreparation2025Activity.super.getBundle();
            if(lotAdapteList!= null)
                extras.putSerializable("lotAdaptes", (Serializable) lotAdapteList);
            if(list_lot != null)
                extras.putStringArrayList("liste_lot", (ArrayList<String>) list_lot);
            resultIntent.putExtras(extras);

            ListeLotPreparation2025Activity.this.setResult(RETOUR_LISTE_LOTS, resultIntent);
            ListeLotPreparation2025Activity.this.finish();
        }
        else
        {
            Alerte.afficherAlerte(context, "Erreur", erreur, "alerte");
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparation2025Activity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));

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

            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.VISIBLE);
            MAJVisuel();
            MAJValues(false, qteDejaPreparer);
           // lotPreparationAdapter.quantiteAPreparer = restantAPrepaper;
            // lotPreparationAdapter.full = false;
            Intent resultIntent = new Intent();

            Bundle extras = ListeLotPreparation2025Activity.super.getBundle();
            if(lotAdapteList!= null)
                extras.putSerializable("lotAdaptes", (Serializable) lotAdapteList);
            if(list_lot != null)
                extras.putStringArrayList("liste_lot", (ArrayList<String>) list_lot);
            resultIntent.putExtras(extras);

            ListeLotPreparation2025Activity.this.setResult(RETOUR_LISTE_LOTS, resultIntent);
            ListeLotPreparation2025Activity.this.finish();
        }
    }

    private void onMenuDatamatrixClick() {
        int index = -1;
        MAJListeLot();

        Intent listeLotPreparation_Intent = new Intent(ListeLotPreparation2025Activity.this, BarcodePreparationActivity.class);
        //gestion du zebra
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            listeLotPreparation_Intent = new Intent(ListeLotPreparation2025Activity.this, ScannerPreparationActivity.class);
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
        listeLotPreparation_Bundle.putInt("preparationId", ph_preparation_ligne_base.getPreparationID());
        listeLotPreparation_Bundle.putInt("preparationLigneId", ph_preparation_ligne_base.get_UID());
        listeLotPreparation_Bundle.putSerializable("lotAdapteList", (Serializable) phPreparationLignePreparationAdapte.getLotAdaptes());
        listeLotPreparation_Bundle.putSerializable("ph_preparationLigneAdapte", (Serializable) phPreparationLignePreparationAdapte);
        listeLotPreparation_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) list_lot);

        listeLotPreparation_Intent.putExtras(listeLotPreparation_Bundle);
        ListeLotPreparation2025Activity.this.startActivityForResult(listeLotPreparation_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }

    public void ClickNumberPicker(final int position) {
        Context context = ListeLotPreparation2025Activity.this;
        PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant = lotAdapteList.get(position);
        if(courant.getNumLot().contentEquals(""))
        {
            Alerte.afficherAlerte(ListeLotPreparation2025Activity.this, "Erreur", "Vous ne pouvez pas préparer un lot vide.", "alerte");
        }
        else
        {
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

            String title = lotAdapteList.get(position).getNumLot();
            String message = "Quantité placée : ";

            //gestion d'un stock déjà saisie
            if(courant.getQteSaisie() > 0)
            {
                int qte_avant = courant.getQteSaisie();
                courant.setQteSaisie(0);
                stock_courant[0].setQte_Preparer(courant.getQteSaisie());
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant[0]);

                MAJVisuel();
                MAJValues(false, qte_avant);
                //lotPreparationAdapter.quantiteAPreparer = restantAPrepaper;
            }

            int value_max = (int) stock_courant[0].getQte();
            int reste = quantiteDemandeeBase - qteDejaPreparer;
            if(value_max > reste)
                value_max = reste;

            int maxValue = value_max;
            int value = maxValue;

            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant = lotAdapteList.get(position);
                    int qteApres = aNumberPicker.getValue();
                    qteApres = qteApres * (int)ph_preparation_ligne_base.getProduitCondDistrib();
                    MAJValues(true, qteApres);
                    lotAdapteList.get(position).setQteSaisie(qteApres);

                    //lotPreparationAdapter.viewHolders.get(position).qteSaisie.setText(String.valueOf(qteApres));
                    courant.setQteSaisie(qteApres);
                    if(stock_courant[0] != null)
                    {
                        stock_courant[0].setQte_Preparer(courant.getQteSaisie());
                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant[0]);
                    }
                    else
                    {
                        stock_courant[0] = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(db, produit, depot, courant.getNumLot(), courant.getNumSerie());
                        if(stock_courant[0] != null)
                        {
                            stock_courant[0].setQte_Preparer(courant.getQteSaisie());
                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant[0]);
                        }
                    }
                   // lotPreparationAdapter.quantiteAPreparer = restantAPrepaper;
                    MAJVisuel();
                   // lotPreparationAdapter.notifyDataSetChanged();
                    InputMethodManager imm = (InputMethodManager) ListeLotPreparation2025Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    dialog.dismiss();
                }
            };

            //Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);
            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, (int)ph_preparation_ligne_base.getProduitCondDistrib());
        }

    };

    private void MAJListeLot() {
        int index = -1;
        for(int i = 0; i < lotAdapteList.size(); i++)
        {
            if(lotAdapteList.get(i).getNumLot().contentEquals("row_ajouter"))
            {
                index = i;
                break;
            }
        }
        if(index != -1)
            lotAdapteList.remove(index);

        index = -1;
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

    private void MAJVisuel() {
        ((TextView) findViewById(R.id.QteDemandee)).setText(String.valueOf(quantiteDemandeeBase));
        ((TextView) findViewById(R.id.QtePreparer)).setText(String.valueOf(qteDejaPreparer));

        if(quantiteDemandeeBase == qteDejaPreparer)
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparation2025Activity.this.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
            ((TextView) findViewById(R.id.QteDemandee)).setTextColor(ListeLotPreparation2025Activity.this.getResources().getColor(R.color.vert));
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.QtePreparer)).setVisibility(View.INVISIBLE);

           // lotPreparationAdapter.full = true;
        }
        else
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparation2025Activity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
            ((TextView) findViewById(R.id.QtePreparer)).setTextColor(ListeLotPreparation2025Activity.this.getResources().getColor(R.color.orange2));
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.QtePreparer)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.QteDemandee)).setTextColor(ListeLotPreparation2025Activity.this.getResources().getColor(R.color.noir));


           // lotPreparationAdapter.full = false;
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
            nbColis = (int) Math.ceil(nbColis);
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
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ListeLotPreparation2025Activity.this.finish();
            }
        });
    }

    private String verificationDisponibilite() {
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

                if (statutConnexion) {
                    RequestQueue requestQueue = Volley.newRequestQueue(ListeLotPreparation2025Activity.this);
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
                                Alerte.afficherAlerte(ListeLotPreparation2025Activity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : vérification stock)", "alerte");
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", utilisateurConnecte.getToken());
                            return headers;
                        }
                    };
                }
            }
        }

        return disponible[0];
    }

    private void clicAjoutManuel() {
        Bundle clicBoutonAjouterManuellement_Bundle = ListeLotPreparation2025Activity.super.getBundle();
        clicBoutonAjouterManuellement_Bundle.putInt("produitID", produit.getID_produit());
        clicBoutonAjouterManuellement_Bundle.putInt("depotID", depot.getDepot_UID());
        clicBoutonAjouterManuellement_Bundle.putInt("PreparationID", ph_preparation_ligne_base.getPreparationID());
        clicBoutonAjouterManuellement_Bundle.putInt("PreparationLigneID", ph_preparation_ligne_base.get_UID());

        Intent clicBoutonAjouterManuellement_Intent = new Intent(ListeLotPreparation2025Activity.this, CreationLotActivity.class);
        clicBoutonAjouterManuellement_Intent.putExtras(clicBoutonAjouterManuellement_Bundle);
        ListeLotPreparation2025Activity.this.startActivityForResult(clicBoutonAjouterManuellement_Intent, RETOUR_LOT);
    }

    private void MAJValues(boolean ajout, int quantiteamodifier) {
        if(ajout)
        {
            qteDejaPreparer = qteDejaPreparer + quantiteamodifier;
        }
        else
        {
            qteDejaPreparer = qteDejaPreparer - quantiteamodifier;
        }

        restantAPrepaper = quantiteDemandeeBase - qteDejaPreparer;
    }
}
