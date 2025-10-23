package fr.alcyons.phiwms_mobile.ControleDesRetours;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_CODE_GS1;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LISTE_LOTS;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerRetourActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.LotControleDesRetoursAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.PlanDePlacement.ListeZonesActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ListeLotsControleDesRetoursActivity extends ServiceActivity {

    public LotControleDesRetoursAdapter adapter;
    int quantiteRetournee = 0;
    int qteDeclaree;
    int quantiteRestant;

    Depot depot;
    Produit produit;

    Retour_Ligne retourLigne;
    PackageManager pm;
    Retour retour_courant;

    RecyclerView recyclerView;
    List<Stock_Lot_Emplacement_Light> listeStockLotEmplacement;
    List<String> listelot;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lots_controle_des_retours_2025);

        //gestion du Package Manager
        pm = ListeLotsControleDesRetoursActivity.this.getPackageManager();

        // Récupération des variables globales
        retourLigne = Retour_LigneOpenHelper.getRetourLigneByID(db, intent.getExtras().getInt("retourLigneId"));
        retour_courant = RetourOpenHelper.getRetourByID(db, retourLigne.getRetour_UID());
        depot = DepotOpenHelper.getDepotParReference(db, retour_courant.getRef_Depot_Origine());

        int id_produit = intent.getExtras().getInt("produitID");
        produit = ProduitOpenHelper.getProduitByID(db, id_produit);
        listelot = new ArrayList<>();
        listeStockLotEmplacement = new ArrayList<>();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ListeLotsControleDesRetoursActivity.this.finish();
            }
        });

        if(depot != null && produit != null)
        {
            listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
        }

        for(Stock_Lot_Emplacement_Light tempLot : listeStockLotEmplacement)
        {
            listelot.add(tempLot.getLot());
        }

        ((Button) findViewById(R.id.btnAjoutManuel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle clicBoutonAjouterManuellement_Bundle = ListeLotsControleDesRetoursActivity.super.getBundle();
                clicBoutonAjouterManuellement_Bundle.putInt("produitID", produit.getID_produit());
                clicBoutonAjouterManuellement_Bundle.putInt("depotID", depot.getDepot_UID());
                clicBoutonAjouterManuellement_Bundle.putInt("retourUID", retour_courant.get_UID());
                clicBoutonAjouterManuellement_Bundle.putInt("retourLigneID", retourLigne.get_UID());

                Intent clicBoutonAjouterManuellement_Intent = new Intent(ListeLotsControleDesRetoursActivity.this, CreationLotControleDesRetoursActivity.class);
                clicBoutonAjouterManuellement_Intent.putExtras(clicBoutonAjouterManuellement_Bundle);
                ListeLotsControleDesRetoursActivity.this.startActivityForResult(clicBoutonAjouterManuellement_Intent, RETOUR_LOT);
            }
        });

        ((LinearLayout) findViewById(R.id.lancerScan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Retour_Ligne> listeScannerRetourLigne = new ArrayList<>();
                listeScannerRetourLigne.add(retourLigne);

                Bundle clicBoutonAjoutParScan_Bundle = ListeLotsControleDesRetoursActivity.super.getBundle();
                clicBoutonAjoutParScan_Bundle.putBoolean("isBoutonSuppressionExistant", true);
                clicBoutonAjoutParScan_Bundle.putSerializable("RetourCourant", (Serializable) retour_courant);
                clicBoutonAjoutParScan_Bundle.putSerializable("DepotOrigine", (Serializable) depot);
                clicBoutonAjoutParScan_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) listelot);
                clicBoutonAjoutParScan_Bundle.putSerializable("ListeRetourLigne", (Serializable) listeScannerRetourLigne);
                clicBoutonAjoutParScan_Bundle.putBoolean("EmplacementUF", true);

                Intent clicBoutonAjoutParScan_intent = null;
                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
                {
                    clicBoutonAjoutParScan_intent = new Intent(ListeLotsControleDesRetoursActivity.this, ScannerRetourActivity.class);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        //clicBoutonAjoutParScan_intent = new Intent(ListeLotsControleDesRetours2025Activity.this, BarcodePreparationActivity.class);
                    }
                    else
                    {
                        clicBoutonAjoutParScan_intent = new Intent(ListeLotsControleDesRetoursActivity.this, ScannerRetourActivity.class);
                    }
                }
                clicBoutonAjoutParScan_intent.putExtras(clicBoutonAjoutParScan_Bundle);

                ListeLotsControleDesRetoursActivity.this.startActivityForResult(clicBoutonAjoutParScan_intent, RETOUR_CODE_GS1);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (produit == null || depot == null) {
            Alerte.afficherAlerteInformation(ListeLotsControleDesRetoursActivity.this, getLayoutInflater(), "Alerte","Impossible de récupérer les stocks du dépôt d'origine", true, false);
        }
        else
        {
            // Affichage des informations de base
            qteDeclaree = (int) retourLigne.getQte_Demander();
            quantiteRestant = qteDeclaree;
            quantiteRetournee = 0;
            List<Retour_Ligne> retourLigneRetourner = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retour_courant, retourLigne.getCode_produit());
            for(Retour_Ligne temp : retourLigneRetourner)
            {
                quantiteRetournee = quantiteRetournee + (int) temp.getQte_Retourner();
                quantiteRestant = quantiteRestant - (int) temp.getQte_Retourner();
            }

            int nbColis = 0;
            if (produit.getCond_distrib() > 0) {
                nbColis = (int) Math.ceil(quantiteRestant / produit.getCond_distrib());
            }
            ((TextView) findViewById(R.id.QteRetourner)).setText(String.valueOf(quantiteRetournee));
            ((TextView) findViewById(R.id.qteDeclaree)).setText(String.valueOf(qteDeclaree));
            ((TextView) findViewById(R.id.designationProduit)).setText(produit.getDesignation_interne());
            ((TextView) findViewById(R.id.referenceProduit)).setText(produit.getRef_fourni());
            ((TextView) findViewById(R.id.numeroRetour)).setText("#"+retour_courant.getNumero());
            //((TextView) findViewById(R.id.motif)).setText(retour_courant.getMotif());
            ((TextView) findViewById(R.id.nomDepot)).setText(depot.getNom());
            ((TextView) findViewById(R.id.colis)).setText(String.valueOf(nbColis));

            recyclerView = (RecyclerView) findViewById(R.id.liste_view_lot_retour_ligne);
            int decorationCount = recyclerView.getItemDecorationCount();
            for (int i = 0; i < decorationCount; i++) {
                recyclerView.removeItemDecorationAt(0);
            }
            DividerItemDecoration divider = new DividerItemDecoration(ListeLotsControleDesRetoursActivity.this, DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(ListeLotsControleDesRetoursActivity.this, R.drawable.recycler_divider));
            recyclerView.addItemDecoration(divider);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LotControleDesRetoursAdapter(listeStockLotEmplacement, position -> {
                Toast.makeText(this, "Supprimer " + listeStockLotEmplacement.get(position), Toast.LENGTH_SHORT).show();
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null && viewHolder instanceof LotControleDesRetoursAdapter.LotViewHolder) {
                    Stock_Lot_Emplacement_Light stock_courant = listeStockLotEmplacement.get(position);
                    LotControleDesRetoursAdapter.LotViewHolder monViewHolder = (LotControleDesRetoursAdapter.LotViewHolder) viewHolder;
                    int qte_Saisie = Integer.parseInt(monViewHolder.qteSaisie.getText().toString());
                    MAJValues(false, qte_Saisie);
                    monViewHolder.qteSaisie.setText("0");
                    stock_courant.setQte_Preparer(0);
                    Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                    ((LotControleDesRetoursAdapter.LotViewHolder) viewHolder).isSwipedOpen = false;
                    supprimerUnRetourLigne(retourLigne, stock_courant);
                    adapter.notifyItemChanged(position);

                    MAJVisuel();
                    onResume();
                }

                // Tu peux appeler confirm dialog ici
            }, ListeLotsControleDesRetoursActivity.this);

            recyclerView.setAdapter(adapter);
            MAJVisuel();
            invalidateOptionsMenu();
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
                    listeStockLotEmplacement = new ArrayList<>();
                    listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);

                    listelot = new ArrayList<>();
                    for(Stock_Lot_Emplacement_Light tempLot : listeStockLotEmplacement)
                    {
                        listelot.add(tempLot.getLot());
                    }
                    onResume();
                    break;
                case RETOUR_LOT:
                    listeStockLotEmplacement = new ArrayList<>();
                    listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);

                    listelot = new ArrayList<>();
                    for(Stock_Lot_Emplacement_Light tempLot : listeStockLotEmplacement)
                    {
                        listelot.add(tempLot.getLot());
                    }
                    onResume();
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    public void ClickNumberPicker(final int position)
    {
        Context context = ListeLotsControleDesRetoursActivity.this;
        Stock_Lot_Emplacement_Light stock_courant = adapter.getLotAt(position);

        String title = stock_courant.getLot();
        String message = "Quantité placée : ";

        //gestion d'un stock déjà saisie
        if(stock_courant.getQte_Preparer() > 0)
        {
            int qte_avant = stock_courant.getQte_Preparer();
            stock_courant.setQte_Preparer(0);
            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);

            supprimerUnRetourLigne(retourLigne, stock_courant);
            quantiteRestant = quantiteRestant + qte_avant;
            MAJVisuel();
        }

        int value_max = (int)retourLigne.getQte_Demander();
        if(stock_courant.getQte() < value_max)
        {
            value_max = (int) stock_courant.getQte();
        }
        int reste = quantiteRestant;
        if(value_max > reste)
            value_max = reste;

        int maxValue = value_max;
        int value = maxValue;

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Stock_Lot_Emplacement_Light courant = adapter.getLotAt(position);
                int qteAprès = aNumberPicker.getValue();
                courant.setQte_Preparer(qteAprès);
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);
                MAJValues(true, qteAprès);

                enregistrementRetourLigne(retourLigne, courant);
                onResume();
                InputMethodManager imm = (InputMethodManager) ListeLotsControleDesRetoursActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
            }
        };

        Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);
    }

    public void ClickLigneLot(int position)
    {
        Stock_Lot_Emplacement_Light courant = listeStockLotEmplacement.get(position);
        if(courant.getLot().contentEquals(""))
        {
            Alerte.afficherAlerteInformation(ListeLotsControleDesRetoursActivity.this, getLayoutInflater(),"Erreur", "Vous ne pouvez pas préparer un lot vide.", false, false);
        }
        else
        {
            if(quantiteRestant != 0 && courant.getQte() != 0)
            {
                int quantite_stock_selectionne = (int) courant.getQte();

                if(quantite_stock_selectionne > quantiteRestant)
                {
                    quantite_stock_selectionne = quantiteRestant;
                }

                //gestion du visuel
                courant.setQte_Preparer(quantite_stock_selectionne);
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);

                MAJValues(true, quantite_stock_selectionne);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null && viewHolder instanceof LotControleDesRetoursAdapter.LotViewHolder) {
                    LotControleDesRetoursAdapter.LotViewHolder monViewHolder = (LotControleDesRetoursAdapter.LotViewHolder) viewHolder;
                    monViewHolder.qteSaisie.setText((String.valueOf(quantite_stock_selectionne)));
                    adapter.notifyItemChanged(position);
                }
                if(courant.getEmplacement().contentEquals(""))
                {
                    Intent listeZonesIntent = new Intent(ListeLotsControleDesRetoursActivity.this, ListeZonesActivity.class);
                    Bundle listeZonesBundle = ListeLotsControleDesRetoursActivity.super.getBundle();
                    Depot depotpui = DepotOpenHelper.getDepotPUI(db);
                    listeZonesBundle.putInt("depotSelectionneID", depotpui.getDepot_UID());
                    listeZonesIntent.putExtras(listeZonesBundle);
                    ListeLotsControleDesRetoursActivity.this.startActivityForResult(listeZonesIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }
                enregistrementRetourLigne(retourLigne, courant);
                MAJVisuel();
            }
        }
    }

    private void MAJVisuel()
    {
        if(quantiteRestant == 0)
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotsControleDesRetoursActivity.this.getResources().getDrawable(R.drawable.background_cadre_vert));
            ((TextView) findViewById(R.id.qteDeclaree)).setTextColor(ListeLotsControleDesRetoursActivity.this.getResources().getColor(R.color.vert));
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.QteRetourner)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.btnAjoutManuel)).setVisibility(View.INVISIBLE);
        }
        else
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotsControleDesRetoursActivity.this.getResources().getDrawable(R.drawable.background_cadre_orange));
            ((TextView) findViewById(R.id.qteDeclaree)).setTextColor(ListeLotsControleDesRetoursActivity.this.getResources().getColor(R.color.orange2));
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.QteRetourner)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.qteDeclaree)).setTextColor(ListeLotsControleDesRetoursActivity.this.getResources().getColor(R.color.noir));
            ((Button) findViewById(R.id.btnAjoutManuel)).setVisibility(View.VISIBLE);
        }
    }

    private void MAJValues(boolean ajout, int quantiteamodifier) {
        if(ajout)
        {
            quantiteRetournee = quantiteRetournee + quantiteamodifier;
        }
        else
        {
            quantiteRetournee = quantiteRetournee - quantiteamodifier;
        }

        quantiteRestant = qteDeclaree - quantiteRetournee;
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
        MenuItem item = menu.findItem(R.id.menuSaveCircle);
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
        Bundle clicBoutonValider_Bundle = ListeLotsControleDesRetoursActivity.super.getBundle();
        Intent clicBoutonValider_Intent = new Intent();
        clicBoutonValider_Intent.putExtras(clicBoutonValider_Bundle);
        ListeLotsControleDesRetoursActivity.this.setResult(RETOUR_LISTE_LOTS, clicBoutonValider_Intent);
        ListeLotsControleDesRetoursActivity.this.finish();
    }

    @Override
    public void retourService(Bundle bundle) {
        ListeLotsControleDesRetoursActivity.this.finish();
    }

    private void enregistrementRetourLigne(Retour_Ligne retourLigneBase, Stock_Lot_Emplacement_Light stockCourant)
    {
        Retour_Ligne retourLigneCourant = new Retour_Ligne(retourLigneBase);
        Random randomactionPhRetourLigne = new Random();
        int phRetourLigneUID = randomactionPhRetourLigne.nextInt();
        if(phRetourLigneUID > 0)
            phRetourLigneUID = phRetourLigneUID*-1;
        retourLigneCourant.set_UID(phRetourLigneUID);
        retourLigneCourant.setQte_Retourner(stockCourant.getQte_Preparer());
        retourLigneCourant.setLot_Retourner(stockCourant.getLot());
        retourLigneCourant.setSerie_Retourner(stockCourant.getSerie());
        retourLigneCourant.setPeremptionDate(stockCourant.getPeremptionDate());

        long rowID = Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, retourLigneCourant);
    }

    private void supprimerUnRetourLigne(Retour_Ligne retourLigneBase, Stock_Lot_Emplacement_Light stockCourant)
    {
        Retour_Ligne retourLigneASupprimer = Retour_LigneOpenHelper.getRetourLigneNegByProduitLot(db, retourLigneBase.getRetour_UID(), retourLigneBase.getCode_produit(), stockCourant.getLot(), stockCourant.getSerie());

        if(retourLigneASupprimer != null)
            Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigneASupprimer);
    }
}
