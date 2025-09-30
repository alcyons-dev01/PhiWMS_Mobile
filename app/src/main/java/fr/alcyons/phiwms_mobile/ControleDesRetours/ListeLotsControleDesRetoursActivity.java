package fr.alcyons.phiwms_mobile.ControleDesRetours;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_ControleRetour_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Lot_ControleDesRetoursScanneeAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_CODE_GS1;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LISTE_LOTS;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;

public class ListeLotsControleDesRetoursActivity extends ServiceActivity {
    public List<Retour_Ligne_ControleRetour_Adapte.LotAdapte> lotAdaptesList;
    public List<Retour_Ligne_ControleRetour_Adapte.LotAdapte> lotAdaptesListDeBase;
    public Retour_Ligne_ControleRetour_Adapte retourLigneAdapteSelectionne;
    public ListView listView;
    public Lot_ControleDesRetoursScanneeAdapter adapter;
    int quantiteRetournee = 0;
    int qteDeclaree;
    int quantiteRestant;

    Depot depot;
    Produit produit;

    LinearLayout boutonAjouterParScan;
    Retour_Ligne retourLigne;
    PackageManager pm;
    Retour retour_courant;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lots_controle_des_retours_new);

        //gestion du Package Manager
        pm = ListeLotsControleDesRetoursActivity.this.getPackageManager();

        // Récupération des variables globales
        retourLigneAdapteSelectionne = (Retour_Ligne_ControleRetour_Adapte) intent.getExtras().getSerializable("retourLigneAdapte");
        Retour_Ligne retour_ligne_courant = Retour_LigneOpenHelper.getRetourLigneByID(db, retourLigneAdapteSelectionne.getRetourLigneID());
        retour_courant = RetourOpenHelper.getRetourByID(db, retour_ligne_courant.getRetour_UID());
        depot = DepotOpenHelper.getDepotParReference(db, retour_courant.getRef_Depot_Origine());

        int id_produit = intent.getExtras().getInt("produitID");
        produit = ProduitOpenHelper.getProduitByID(db, id_produit);
        //depot = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotID"));

        // Récupération du Retour_Ligne sélectionné
        retourLigne = Retour_LigneOpenHelper.getRetourLigneByID(db, retourLigneAdapteSelectionne.getRetourLigneID());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ListeLotsControleDesRetoursActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (produit == null || depot == null) {
            afficherMessageAlerte(ListeLotsControleDesRetoursActivity.this, ListeLotsControleDesRetoursActivity.this.getLayoutInflater());
            return;
        }
        else
        {
            // Affichage des informations de base
            qteDeclaree = (int) retourLigne.getQte_Demander();
            quantiteRestant = qteDeclaree;

            ((TextView) findViewById(R.id.qteDeclaree)).setText(String.valueOf(qteDeclaree));

            // Récupération des lots du Retour_Ligne
            lotAdaptesList = new ArrayList<>();
            lotAdaptesListDeBase = retourLigneAdapteSelectionne.getLotAdaptes();

            if (lotAdaptesListDeBase.size() == 0) {
                List<Stock_Lot_Emplacement_Light> stockLotEmplacementLights = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);

                for (Stock_Lot_Emplacement_Light stockLotEmplacement : stockLotEmplacementLights) {
                    lotAdaptesListDeBase.add(retourLigneAdapteSelectionne.new LotAdapte(stockLotEmplacement));
                }
            } else {
                for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lotAdapte : lotAdaptesListDeBase) {
                    /*if (lotAdapte.getQteSaisie() > 0) {
                        quantiteRestant -= lotAdapte.getQteSaisie();
                        quantiteRetournee += lotAdapte.getQteSaisie();
                        lotAdaptesList.add(lotAdapte);
                    }*/
                    lotAdaptesList.add(lotAdapte);

                }
            }

            lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_ajouter"));
            lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_annuler"));


            ((TextView) findViewById(R.id.designationProduit)).setText(produit.getDesignation_interne());
            ((TextView) findViewById(R.id.referenceProduit)).setText(produit.getRef_fourni());
            ((TextView) findViewById(R.id.numeroRetour)).setText("#"+retour_courant.getNumero());
            ((TextView) findViewById(R.id.nomDepot)).setText(depot.getNom());

            boutonAjouterParScan = (LinearLayout) findViewById(R.id.boutonAddScan);
            boutonAjouterParScan.setOnClickListener(clicBoutonAjoutParScan);

            // Initialisation de la listView
            listView = (ListView) findViewById(R.id.listeView);
            listView.setItemsCanFocus(true);
            listView.setDivider(footer);



            invalidateOptionsMenu();
            // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
            adapter = new Lot_ControleDesRetoursScanneeAdapter(ListeLotsControleDesRetoursActivity.this, lotAdaptesList, db, retourLigneAdapteSelectionne, depot.getStructure(), quantiteRestant);
            listView.setDivider(footer);
            listView.setAdapter(adapter);
            ((TextView) findViewById(R.id.QteRetourner)).setText(String.valueOf((int)retourLigne.getQte_Retourner()));
            MAJVisuel();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {

                    Retour_Ligne_ControleRetour_Adapte.LotAdapte courant = lotAdaptesList.get(position);

                    if(courant.getNumLot().contentEquals("row_ajouter"))
                    {
                        gestionLot();

                        clicAjoutManuel();
                    }
                    else if(courant.getNumLot().contentEquals("row_annuler"))
                    {
                        ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotsControleDesRetoursActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));

                        for(int i = 0; i < lotAdaptesList.size()-1; i++)
                        {
                            if(!lotAdaptesList.get(i).getNumLot().contentEquals("row_ajouter") && !lotAdaptesList.get(i).getNumLot().contentEquals("row_annuler"))
                            {
                                lotAdaptesList.get(i).setQteSaisie(0);
                                Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lotAdaptesList.get(i).getStockLotEmplacementID());
                                if(stock_courant != null)
                                {
                                    stock_courant.setQte_Preparer(courant.getQteSaisie());
                                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                }
                                else
                                {
                                    if(lotAdaptesList.get(i) != null)
                                    {
                                        stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(db, produit, depot, lotAdaptesList.get(i).getNumLot(), lotAdaptesList.get(i).getNumSerie());
                                        if(stock_courant != null)
                                        {
                                            stock_courant.setQte_Preparer(courant.getQteSaisie());
                                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                        }
                                    }
                                }
                            }
                        }

                        /*lotAdaptesList = new ArrayList<>();
                        lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_ajouter"));
                        lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_annuler"));*/
                        retourLigne.setQte_Retourner(0);
                        quantiteRestant = (int)retourLigne.getQte_Demander();
                        adapter.quantiteARetourner = quantiteRestant;
                        Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne);
                        ((TextView) findViewById(R.id.QteRetourner)).setText(String.valueOf((int)retourLigne.getQte_Retourner()));
                        //((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.VISIBLE);
                        MAJVisuel();
                        adapter.full = false;
                        adapter = new Lot_ControleDesRetoursScanneeAdapter(ListeLotsControleDesRetoursActivity.this, lotAdaptesList, db, retourLigneAdapteSelectionne, depot.getStructure(), quantiteRestant);
                        listView.setDivider(footer);
                        listView.setAdapter(adapter);
                    }
                    else
                    {
                        //on check si le lot n'est pas déjà n'est pas déjà sélectionner
                        if(Integer.parseInt(adapter.viewHolders.get(position).qteSaisie.getText().toString()) == 0)
                        {
                            //on récupére la quantité de stock présent dans ce lot
                            int quantite_stock_selectionne = courant.getQteActuelle();
                            if(quantite_stock_selectionne > quantiteRestant)
                                quantite_stock_selectionne = quantiteRestant;
                            //gestion du visuel
                            courant.setQteSaisie(quantite_stock_selectionne);
                            Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, courant.getStockLotEmplacementID());
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

                            retourLigne.setQte_Retourner(retourLigne.getQte_Retourner()+quantite_stock_selectionne);
                            quantiteRestant = quantiteRestant - quantite_stock_selectionne;
                            adapter.quantiteARetourner = quantiteRestant;
                            Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne);
                            ((TextView) findViewById(R.id.QteRetourner)).setText(String.valueOf((int)retourLigne.getQte_Retourner()));
                            adapter.viewHolders.get(position).qteSaisie.setText(String.valueOf(quantite_stock_selectionne));
                        }
                        else
                        {
                            int qte_Saisie = Integer.parseInt(adapter.viewHolders.get(position).qteSaisie.getText().toString());
                            retourLigne.setQte_Retourner(retourLigne.getQte_Retourner()-qte_Saisie);
                            quantiteRestant = quantiteRestant + qte_Saisie;
                            Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne);
                            ((TextView) findViewById(R.id.QteRetourner)).setText(String.valueOf((int)retourLigne.getQte_Retourner()));
                            adapter.viewHolders.get(position).qteSaisie.setText("0");
                            courant.setQteSaisie(0);
                            Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, courant.getStockLotEmplacementID());
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
                            //lotAdaptesList.remove(position);
                        }

                        MAJVisuel();
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }

    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case RETOUR_CODE_GS1:
                    if(data.getExtras() != null)
                    {
                        String numLot = data.getExtras().getString("numLot");
                        String numSerie = data.getExtras().getString("numSerie");
                        String datePeremption = data.getExtras().getString("datePeremption");

                        if(numLot != null)
                        {
                            int index_lot_existant = -1;
                            int index_loop = -1;
                            for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lot : lotAdaptesListDeBase) {
                                index_loop++;
                                if (numLot.equals(lot.getNumLot()) && datePeremption.equals(lot.getDatePeremption()) && numSerie.equals(lot.getNumSerie())) {
                                    index_lot_existant = index_loop;
                                    break;
                                }
                            }

                            if(index_lot_existant == -1)
                            {
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

                                lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte(newStockLotEmplacement));
                                Toast.makeText(ListeLotsControleDesRetoursActivity.this, "L'élément a bien été ajouté à la liste", Toast.LENGTH_SHORT);
                                long rowID = Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, newStockLotEmplacement);
                                if (rowID != -1) {
                                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, newStockLotEmplacement.getPhiMR4UUID(), newStockLotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                                }
                            }
                            else
                            {
                                int quantite_saisie = data.getExtras().getInt("qteActuelle",1);
                                if(quantite_saisie != lotAdaptesListDeBase.get(index_lot_existant).getQteActuelle())
                                {
                                    lotAdaptesListDeBase.get(index_lot_existant).setQteActuelle(quantite_saisie);
                                    Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lotAdaptesListDeBase.get(index_lot_existant).getStockLotEmplacementID());
                                    if(stock_courant != null)
                                    {
                                        stock_courant.setQte(quantite_saisie);
                                        long rowID = Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                        if (rowID != -1) {
                                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, stock_courant.getPhiMR4UUID(), stock_courant.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                                        }
                                    }
                                }
                                lotAdaptesList.add(lotAdaptesListDeBase.get(index_lot_existant));
                            }

                            lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_ajouter"));
                            lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_annuler"));
                            adapter.notifyDataSetChanged();
                            listView.performItemClick(listView.getAdapter().getView(lotAdaptesList.size()-3, null, null), lotAdaptesList.size()-3, listView.getAdapter().getItemId(lotAdaptesList.size()-3));
                        }
                        else
                        {
                            lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_ajouter"));
                            lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_annuler"));
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_ajouter"));
                        lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_annuler"));
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case RETOUR_LOT:
                    if(data.getExtras() != null)
                    {
                        String numLot = data.getExtras().getString("numLot");
                        String numSerie = data.getExtras().getString("numSerie");
                        String datePeremption = data.getExtras().getString("datePeremption");

                        int index_lot_existant = -1;
                        int index_loop = -1;
                        for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lot : lotAdaptesListDeBase) {
                            index_loop++;
                            if (numLot.equals(lot.getNumLot()) && datePeremption.equals(lot.getDatePeremption()) && numSerie.equals(lot.getNumSerie())) {
                                index_lot_existant = index_loop;
                                break;
                            }
                        }

                        if(index_lot_existant == -1)
                        {
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

                            lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte(newStockLotEmplacement));
                            Toast.makeText(ListeLotsControleDesRetoursActivity.this, "L'élément a bien été ajouté à la liste", Toast.LENGTH_SHORT);
                            long rowID = Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, newStockLotEmplacement);
                            if (rowID != -1) {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, newStockLotEmplacement.getPhiMR4UUID(), newStockLotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        else
                        {
                            int quantite_saisie = data.getExtras().getInt("qteActuelle",1);
                            if(quantite_saisie != lotAdaptesListDeBase.get(index_lot_existant).getQteActuelle())
                            {
                                lotAdaptesListDeBase.get(index_lot_existant).setQteActuelle(quantite_saisie);
                                Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lotAdaptesListDeBase.get(index_lot_existant).getStockLotEmplacementID());
                                if(stock_courant != null)
                                {
                                    stock_courant.setQte(quantite_saisie);
                                    long rowID = Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                                    if (rowID != -1) {
                                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, stock_courant.getPhiMR4UUID(), stock_courant.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                                    }
                                }
                            }
                            lotAdaptesList.add(lotAdaptesListDeBase.get(index_lot_existant));
                        }

                        lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_ajouter"));
                        lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_annuler"));
                        adapter.notifyDataSetChanged();
                        listView.performItemClick(listView.getAdapter().getView(lotAdaptesList.size()-3, null, null), lotAdaptesList.size()-3, listView.getAdapter().getItemId(lotAdaptesList.size()-3));

                    }
                    else
                    {
                        lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_ajouter"));
                        lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte("row_annuler"));
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    private void clicAjoutManuel()
    {
        Bundle clicBoutonAjouterManuellement_Bundle = ListeLotsControleDesRetoursActivity.super.getBundle();
        clicBoutonAjouterManuellement_Bundle.putInt("produitID", produit.getID_produit());
        clicBoutonAjouterManuellement_Bundle.putInt("depotID", depot.getDepot_UID());

        Intent clicBoutonAjouterManuellement_Intent = new Intent(ListeLotsControleDesRetoursActivity.this, CreationLotControleDesRetoursActivity.class);
        clicBoutonAjouterManuellement_Intent.putExtras(clicBoutonAjouterManuellement_Bundle);
        ListeLotsControleDesRetoursActivity.this.startActivityForResult(clicBoutonAjouterManuellement_Intent, RETOUR_LOT);
    }

    private void gestionLot()
    {
        int index_a_supprimer = -1;
        for(Retour_Ligne_ControleRetour_Adapte.LotAdapte lot_courant : lotAdaptesList)
        {
            index_a_supprimer++;
            if(lot_courant.getNumLot().contentEquals("row_ajouter"))
            {
                break;
            }
        }

        if(index_a_supprimer != -1)
        {
            lotAdaptesList.remove(index_a_supprimer);
        }

        index_a_supprimer = -1;
        for(Retour_Ligne_ControleRetour_Adapte.LotAdapte lot_courant : lotAdaptesList)
        {
            index_a_supprimer++;
            if(lot_courant.getNumLot().contentEquals("row_annuler"))
            {
                break;
            }
        }

        if(index_a_supprimer != -1)
        {
            lotAdaptesList.remove(index_a_supprimer);
        }
    }

    public void ClickNumberPicker(final int position)
    {
        Context context = ListeLotsControleDesRetoursActivity.this;
        Retour_Ligne_ControleRetour_Adapte.LotAdapte courant = lotAdaptesList.get(position);
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

        String title = adapter.viewHolders.get(position).numLot.getText().toString();
        String message = "Quantité placée : ";

        //gestion d'un stock déjà saisie
        if(courant.getQteSaisie() > 0)
        {
            int qte_avant = courant.getQteSaisie();
            courant.setQteSaisie(0);
            if(stock_courant[0] != null)
            {
                stock_courant[0].setQte_Preparer(courant.getQteSaisie());
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant[0]);
            }
            retourLigne.setQte_Retourner(retourLigne.getQte_Retourner()-qte_avant);
            quantiteRestant = quantiteRestant + qte_avant;
            //ph_preparation_ligne_courant.setQte_APreparer(ph_preparation_ligne_courant.getQte_APreparer()+qte_avant);
            Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne);
            MAJVisuel();
            adapter.quantiteARetourner = quantiteRestant;
        }

        int value_max = (int)retourLigne.getQte_Demander();
        if(stock_courant[0] != null)
        {
            value_max = (int) stock_courant[0].getQte();
        }
        int reste = adapter.quantiteARetourner;
        if(value_max > reste)
            value_max = reste;

        int maxValue = value_max;
        int value = maxValue;

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Retour_Ligne_ControleRetour_Adapte.LotAdapte courant = lotAdaptesList.get(position);
                int qteAprès = aNumberPicker.getValue();
                retourLigne.setQte_Retourner(retourLigne.getQte_Retourner()+qteAprès);
                lotAdaptesList.get(position).setQteSaisie(qteAprès);
                quantiteRestant = quantiteRestant - qteAprès;
                adapter.quantiteARetourner = quantiteRestant;
                Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne);
                ((TextView) findViewById(R.id.QteRetourner)).setText(String.valueOf((int)retourLigne.getQte_Retourner()));

                adapter.viewHolders.get(position).qteSaisie.setText(String.valueOf(qteAprès));
                courant.setQteSaisie(qteAprès);
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
                adapter.quantiteARetourner = quantiteRestant;
                MAJVisuel();
                adapter.notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) ListeLotsControleDesRetoursActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
            }
        };

        Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);
    }

    private void MAJVisuel()
    {

        if(quantiteRestant == 0)
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotsControleDesRetoursActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
            ((TextView) findViewById(R.id.qteDeclaree)).setTextColor(ListeLotsControleDesRetoursActivity.this.getResources().getColor(R.color.vert));
            ((LinearLayout) findViewById(R.id.boutonAddScan)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.QteRetourner)).setVisibility(View.INVISIBLE);
            adapter.full = true;
        }
        else
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotsControleDesRetoursActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
            ((TextView) findViewById(R.id.qteDeclaree)).setTextColor(ListeLotsControleDesRetoursActivity.this.getResources().getColor(R.color.orange2));
            //((LinearLayout) findViewById(R.id.boutonAddScan)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.QteRetourner)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.qteDeclaree)).setTextColor(ListeLotsControleDesRetoursActivity.this.getResources().getColor(R.color.noir));


            adapter.full = false;
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

    public void onMenuSaveClick()
    {
        gestionLot();

        Bundle clicBoutonValider_Bundle = ListeLotsControleDesRetoursActivity.super.getBundle();
        clicBoutonValider_Bundle.putSerializable("lotAdaptesList", (Serializable) lotAdaptesList);

        Intent clicBoutonValider_Intent = new Intent();
        clicBoutonValider_Intent.putExtras(clicBoutonValider_Bundle);
        ListeLotsControleDesRetoursActivity.this.setResult(RETOUR_LISTE_LOTS, clicBoutonValider_Intent);
        ListeLotsControleDesRetoursActivity.this.finish();
    }

    View.OnClickListener clicBoutonAjoutParScan = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gestionLot();
            Bundle clicBoutonAjoutParScan_Bundle = ListeLotsControleDesRetoursActivity.super.getBundle();
            clicBoutonAjoutParScan_Bundle.putBoolean("doitEtreIdentique", true);
            clicBoutonAjoutParScan_Bundle.putString("Designation", produit.getDesignation_interne());
            clicBoutonAjoutParScan_Bundle.putBoolean("isBoutonSuppressionExistant", true);
            clicBoutonAjoutParScan_Bundle.putSerializable("RetourLigneCourant", retourLigne);
            clicBoutonAjoutParScan_Bundle.putSerializable("ListeAdapteRetour", (Serializable) lotAdaptesListDeBase);
            clicBoutonAjoutParScan_Bundle.putString("contexte", String.valueOf(R.string.scannerContextUniqueNewControleRetour));


            Intent clicBoutonAjoutParScan_intent = null;
            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
            {
                //clicBoutonAjoutParScan_intent = new Intent(ListeLotsControleDesRetoursActivity.this, ScannerPreparationActivity.class);
            }
            else
            {
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                {
                    //clicBoutonAjoutParScan_intent = new Intent(ListeLotsControleDesRetoursActivity.this, BarcodePreparationActivity.class);
                }
                else
                {
                    //clicBoutonAjoutParScan_intent = new Intent(ListeLotsControleDesRetoursActivity.this, ScannerPreparationActivity.class);
                }
            }
            clicBoutonAjoutParScan_intent.putExtras(clicBoutonAjoutParScan_Bundle);

            ListeLotsControleDesRetoursActivity.this.startActivityForResult(clicBoutonAjoutParScan_intent, RETOUR_CODE_GS1);
        }
    };

    private void afficherMessageAlerte(Context context, LayoutInflater inflater)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte, null);
        TextView text_tv = layout.findViewById(R.id.messageFin);
        LinearLayout valider_ll = layout.findViewById(R.id.buttonOk);
        TextView titre_tv = layout.findViewById(R.id.titre);

        titre_tv.setText("Erreur");
        text_tv.setText("Impossible de récupérer les stocks du dépôt d'origine");
        builder.setView(layout);
        final AlertDialog alertDialogErreur = builder.create();
        valider_ll.setOnClickListener(view -> {
            alertDialogErreur.dismiss();
            ListeLotsControleDesRetoursActivity.this.finish();
        });

        alertDialogErreur.setCanceledOnTouchOutside(false);
        alertDialogErreur.setCancelable(false);
        alertDialogErreur.show();
    }
}
