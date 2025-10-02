package fr.alcyons.phiwms_mobile.PreparationPUFetPAD;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LISTE_LOTS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparation2025Activity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparation2025_V2Activity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.LotAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.LotAdapter_V2;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.PlanDePlacement.ListeZonesActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ListeLotPreparation2025_V2Activity  extends ServiceAvecConnexionActivity {

    PH_Preparation_Ligne ph_preparation_ligne_base;
    RecyclerView recyclerView;
    Depot depot;
    Produit produit;
    //Lot_PreparationAdapter lotPreparationAdapter;
    LotAdapter_V2 adapter;
    boolean camera_first;
    PackageManager pm;
    Context context;

    boolean produitsuiviserie;
    boolean produitserialiserreception;
    int quantiteDemandeeBase;
    int restantAPrepaper;
    Integer qteDejaPreparer;
    List<PH_Preparation_Ligne> phPreparationLignesPreparer;
    PH_Preparation ph_preparation;
    List<Stock_Lot_Emplacement_Light> listeStockLotEmplacement;
    List<String> listelot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lot_prepration);
        context = ListeLotPreparation2025_V2Activity.this;
        pm = ListeLotPreparation2025_V2Activity.this.getPackageManager();
        camera_first = false;

        // Récupération du ph_preparation_ligne, produit, depot sélectionné
        listelot = new ArrayList<>();
        ph_preparation_ligne_base = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, intent.getExtras().getInt("phPreparationLigneId"));
        ph_preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparation_ligne_base.getPreparationID());
        phPreparationLignesPreparer = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, ph_preparation, ph_preparation_ligne_base.getProduitID());
        produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne_base.getProduitID());
        depot = DepotOpenHelper.getDepotParID(db, ph_preparation.getDepotOrigineID());
        String depotText = ph_preparation.getDepotDestinataireReference();
        Depot depot_destinataire = DepotOpenHelper.getDepotParReference(db, ph_preparation.getDepotDestinataireReference());

        if (produit == null || depot == null) {
            Alerte.afficherAlerte(ListeLotPreparation2025_V2Activity.this, "Alerte", "Un problème a été constaté en Base de données, veuillez synchroniser l'application ou contacter la société Alcyons (service Préparation", "alerte");
            ListeLotPreparation2025_V2Activity.this.finish();
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
        listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
        boolean gtin_ok = !produit.getGTIN().contentEquals("");

        if (!listeStockLotEmplacement.isEmpty()) {
            Collections.sort(listeStockLotEmplacement, new Comparator<Stock_Lot_Emplacement_Light>() {
                @Override
                public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                    return o1.getPeremptionDate().compareTo(o2.getPeremptionDate());
                }
            });

            for(Stock_Lot_Emplacement_Light courant: listeStockLotEmplacement)
            {
                listelot.add(courant.getLot());
            }

            if(!produitserialiserreception && produitsuiviserie)
            {
                listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepotSerie(db, produit, depot);
            }
        }
        else
        {
            afficherAlerteAucunLot(ListeLotPreparation2025_V2Activity.this, LayoutInflater.from(ListeLotPreparation2025_V2Activity.this));
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(!gtin_ok && produitsuiviserie)
        {
            Alerte.afficherAlerte(ListeLotPreparation2025_V2Activity.this, "Erreur", "Aucun GTIN renseigné pour le produit sélectionné, impossible d'ouvrir le scan", "alerte");
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

        adapter = new LotAdapter_V2(listeStockLotEmplacement, position -> {
            Toast.makeText(this, "Supprimer " + listeStockLotEmplacement.get(position), Toast.LENGTH_SHORT).show();
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null && viewHolder instanceof LotAdapter_V2.LotViewHolder) {
                Stock_Lot_Emplacement_Light stock_courant = listeStockLotEmplacement.get(position);
                LotAdapter_V2.LotViewHolder monViewHolder = (LotAdapter_V2.LotViewHolder) viewHolder;
                int qte_Saisie = Integer.parseInt(monViewHolder.qteSaisie.getText().toString());
                MAJValues(false, qte_Saisie);
                monViewHolder.qteSaisie.setText("0");
                stock_courant.setQte_Preparer(0);
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                ((LotAdapter_V2.LotViewHolder) viewHolder).isSwipedOpen = false;
                supprimerPhPreparationLigne(ph_preparation_ligne_base, stock_courant);

                listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);

                Collections.sort(listeStockLotEmplacement, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getPeremptionDate().compareTo(o2.getPeremptionDate());
                    }
                });

                for(Stock_Lot_Emplacement_Light courant: listeStockLotEmplacement)
                {
                    listelot.add(courant.getLot());
                }

                if(!produitserialiserreception && produitsuiviserie)
                {
                    listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepotSerie(db, produit, depot);
                }
                adapter.notifyItemChanged(position);

                MAJVisuel();
                onResume();
            }

            // Tu peux appeler confirm dialog ici
        }, ListeLotPreparation2025_V2Activity.this);

        recyclerView.setAdapter(adapter);

        if(!camera_first)
        {
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
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
                    listelot = new ArrayList<>();
                    if(!listeStockLotEmplacement.isEmpty())
                    {
                        for(Stock_Lot_Emplacement_Light courant: listeStockLotEmplacement)
                        {
                            listelot.add(courant.getLot());
                        }
                    }

                    if(!produitserialiserreception && produitsuiviserie)
                    {
                        listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepotSerie(db, produit, depot);
                    }
                    qteDejaPreparer = 0;
                    for(Stock_Lot_Emplacement_Light courant: listeStockLotEmplacement)
                    {
                        qteDejaPreparer = qteDejaPreparer + courant.getQte_Preparer();
                    }
                    MAJValues(true, 0);
                    MAJVisuel();
                    onResume();
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
        //String erreur = verificationDisponibilite();
        String erreur = "";

        if(erreur.contentEquals(""))
        {
            Intent resultIntent = new Intent();

            Bundle extras = ListeLotPreparation2025_V2Activity.super.getBundle();
            resultIntent.putExtras(extras);

            ListeLotPreparation2025_V2Activity.this.setResult(RETOUR_LISTE_LOTS, resultIntent);
            ListeLotPreparation2025_V2Activity.this.finish();
        }
    }

    private void onMenuDatamatrixClick() {
        int index = -1;
        MAJListeLot();

        Intent listeLotPreparation_Intent = new Intent(ListeLotPreparation2025_V2Activity.this, BarcodePreparation2025Activity.class);
        //gestion du zebra
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            listeLotPreparation_Intent = new Intent(ListeLotPreparation2025_V2Activity.this, ScannerPreparation2025_V2Activity.class);
        }

        List<PH_Preparation_Ligne> listePhPreparationLigne = new ArrayList<>();
        listePhPreparationLigne.add(ph_preparation_ligne_base);

        Bundle listeLotPreparation_Bundle = super.getBundle();
        listeLotPreparation_Bundle.putInt("UserId", utilisateurConnecte.getId());
        listeLotPreparation_Bundle.putInt("preparationId", ph_preparation_ligne_base.getPreparationID());
        listeLotPreparation_Bundle.putInt("preparationLigneId", ph_preparation_ligne_base.get_UID());
        listeLotPreparation_Bundle.putSerializable("liste_ph_preparation_ligne", (Serializable) listePhPreparationLigne);
        listeLotPreparation_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) listelot);

        listeLotPreparation_Intent.putExtras(listeLotPreparation_Bundle);
        ListeLotPreparation2025_V2Activity.this.startActivityForResult(listeLotPreparation_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }

    public void ClickNumberPicker(final int position) {
        Context context = ListeLotPreparation2025_V2Activity.this;
        if(!produit.isSuivi_Serialisation() || produit.isSerialiser_Reception_Delivrance())
        {
            if(restantAPrepaper != 0)
            {
                Stock_Lot_Emplacement_Light courant = listeStockLotEmplacement.get(position);
                if(courant.getLot().contentEquals(""))
                {
                    Alerte.afficherAlerte(ListeLotPreparation2025_V2Activity.this, "Erreur", "Vous ne pouvez pas préparer un lot vide.", "alerte");
                }
                else
                {
                    int max = 0;
                    max = (int) courant.getQte();

                    String title = courant.getLot();
                    String message = "Quantité placée : ";

                    //gestion d'un stock déjà saisie
                    if(courant.getQte_Preparer() > 0)
                    {
                        int qte_avant = courant.getQte_Preparer();
                        courant.setQte_Preparer(0);
                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);

                        MAJVisuel();
                        MAJValues(false, qte_avant);
                    }

                    int value_max = (int) max;
                    int reste = quantiteDemandeeBase - qteDejaPreparer;
                    if(value_max > reste)
                        value_max = reste;

                    int maxValue = value_max;
                    int value = maxValue;

                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int qteApres = aNumberPicker.getValue();
                            qteApres = qteApres * (int)ph_preparation_ligne_base.getProduitCondDistrib();
                            MAJValues(true, qteApres);

                            courant.setQte_Preparer(qteApres);
                            // lotPreparationAdapter.quantiteAPreparer = restantAPrepaper;
                            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                            if (viewHolder instanceof LotAdapter_V2.LotViewHolder) {
                                LotAdapter_V2.LotViewHolder monViewHolder = (LotAdapter_V2.LotViewHolder) viewHolder;
                                monViewHolder.qteSaisie.setText((String.valueOf(qteApres)));
                                adapter.notifyItemChanged(position);
                            }
                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);

                            if(qteApres == 0)
                                supprimerPhPreparationLigne(ph_preparation_ligne_base, courant);
                            else
                            {
                                PH_Preparation_Ligne ligneCourante = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByProduitLotPreparationSerieEmplacement(db, ph_preparation_ligne_base.getProduitID(), ph_preparation_ligne_base.getPreparationID(), courant.getLot(), courant.getSerie(), courant.getEmplacement());
                                if(ligneCourante == null)
                                    enregistrementPreparationLigne(ph_preparation_ligne_base, courant);
                                else
                                    modifierPreparationLigne(ph_preparation_ligne_base, ligneCourante, courant);
                            }
                            MAJVisuel();
                            // lotPreparationAdapter.notifyDataSetChanged();
                            InputMethodManager imm = (InputMethodManager) ListeLotPreparation2025_V2Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            dialog.dismiss();
                        }
                    };

                    //Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);
                    Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, (int)ph_preparation_ligne_base.getProduitCondDistrib());
                }
            }

        }
    };

    public void ClickLigneLot(int position)
    {
        if(!produit.isSuivi_Serialisation() || produit.isSerialiser_Reception_Delivrance())
        {
            Context context = ListeLotPreparation2025_V2Activity.this;
            Stock_Lot_Emplacement_Light courant = listeStockLotEmplacement.get(position);
            if(courant.getLot().contentEquals(""))
            {
                Alerte.afficherAlerte(ListeLotPreparation2025_V2Activity.this, "Erreur", "Vous ne pouvez pas préparer un lot vide.", "alerte");
            }
            else
            {
                int quantite_stock_selectionne = (int) courant.getQte();

                if(quantite_stock_selectionne > restantAPrepaper)
                {
                    quantite_stock_selectionne = restantAPrepaper;
                }

                //gestion du visuel
                courant.setQte_Preparer(quantite_stock_selectionne);
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);

                MAJValues(true, quantite_stock_selectionne);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null && viewHolder instanceof LotAdapter_V2.LotViewHolder) {
                    LotAdapter_V2.LotViewHolder monViewHolder = (LotAdapter_V2.LotViewHolder) viewHolder;
                    monViewHolder.qteSaisie.setText((String.valueOf(quantite_stock_selectionne)));
                    adapter.notifyItemChanged(position);
                }
                if(courant.getEmplacement().contentEquals(""))
                {
                    Intent listeZonesIntent = new Intent(ListeLotPreparation2025_V2Activity.this, ListeZonesActivity.class);
                    Bundle listeZonesBundle = ListeLotPreparation2025_V2Activity.super.getBundle();
                    Depot depotpui = DepotOpenHelper.getDepotPUI(db);
                    listeZonesBundle.putInt("depotSelectionneID", depotpui.getDepot_UID());
                    listeZonesIntent.putExtras(listeZonesBundle);
                    ListeLotPreparation2025_V2Activity.this.startActivityForResult(listeZonesIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }
                enregistrementPreparationLigne(ph_preparation_ligne_base, courant);
                MAJVisuel();
            }
        }
    }

    private void MAJListeLot() {

    }

    private void MAJVisuel() {
        ((TextView) findViewById(R.id.QteDemandee)).setText(String.valueOf(quantiteDemandeeBase));
        ((TextView) findViewById(R.id.QtePreparer)).setText(String.valueOf(qteDejaPreparer));

        if(quantiteDemandeeBase == qteDejaPreparer)
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparation2025_V2Activity.this.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
            ((TextView) findViewById(R.id.QteDemandee)).setTextColor(ListeLotPreparation2025_V2Activity.this.getResources().getColor(R.color.vert));
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.QtePreparer)).setVisibility(View.INVISIBLE);
        }
        else
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparation2025_V2Activity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
            ((TextView) findViewById(R.id.QtePreparer)).setTextColor(ListeLotPreparation2025_V2Activity.this.getResources().getColor(R.color.orange2));
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.QtePreparer)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.QteDemandee)).setTextColor(ListeLotPreparation2025_V2Activity.this.getResources().getColor(R.color.noir));
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
                ListeLotPreparation2025_V2Activity.this.finish();
            }
        });
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

    public void fermerLigne(int position) {
        RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(position);
        if (vh != null && vh instanceof LotAdapter.LotViewHolder) {
            ((LotAdapter.LotViewHolder) vh).contentLayout.animate()
                    .translationX(0)
                    .setDuration(200)
                    .start();
        }
    }

    private void modifierPreparationLigne(PH_Preparation_Ligne ph_preparationLigneCorrespondant, PH_Preparation_Ligne ph_preparation_ligne, Stock_Lot_Emplacement_Light stockLotEmplacementLight)
    {
        int GlobalAPreparer = ph_preparationLigneCorrespondant.getQte_Demander();
        Random random = new Random();
        int new_id = random.nextInt();
        if(new_id > 0)
        {
            new_id= new_id*-1;
        }

        ph_preparation_ligne.set_UID(new_id);
        ph_preparation_ligne.setQte_Demander(GlobalAPreparer);
        GlobalAPreparer = GlobalAPreparer - stockLotEmplacementLight.getQte_Preparer();
        ph_preparation_ligne.setQte_RAL(GlobalAPreparer);
        ph_preparation_ligne.setQte_preparer(stockLotEmplacementLight.getQte_Preparer());

        long rowID = PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
    }

    private void enregistrementPreparationLigne(PH_Preparation_Ligne ph_preparationLigneCorrespondant, Stock_Lot_Emplacement_Light stockLotEmplacementLight)
    {
        /* on supprime les lignes déjà enregistrer qui ne sont pas les lignes de bases */
       int GlobalAPreparer = ph_preparationLigneCorrespondant.getQte_Demander();
        PH_Preparation_Ligne ph_preparationLigneCourant = null;

        ph_preparationLigneCourant = new PH_Preparation_Ligne(ph_preparationLigneCorrespondant);
        Random random = new Random();
        int new_id = random.nextInt();
        if(new_id > 0)
        {
            new_id= new_id*-1;
        }

        ph_preparationLigneCourant.set_UID(new_id);
        ph_preparationLigneCourant.setQte_Demander(GlobalAPreparer);
        GlobalAPreparer = GlobalAPreparer - stockLotEmplacementLight.getQte_Preparer();
        ph_preparationLigneCourant.setQte_RAL(GlobalAPreparer);
        ph_preparationLigneCourant.setQte_preparer(stockLotEmplacementLight.getQte_Preparer());
        ph_preparationLigneCourant.setLotNumero(stockLotEmplacementLight.getLot().trim());
        ph_preparationLigneCourant.setPeremptionDate(stockLotEmplacementLight.getPeremptionDate());
        ph_preparationLigneCourant.setZoneDepot(stockLotEmplacementLight.getZone().trim());
        ph_preparationLigneCourant.setEmplacementParDefaut(stockLotEmplacementLight.getEmplacement().trim());
        ph_preparationLigneCourant.setSerieNumero(stockLotEmplacementLight.getSerie().trim());
        ph_preparationLigneCourant.set_UID_4D(ph_preparationLigneCorrespondant.get_UID_4D());

        long rowID = PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparationLigneCourant);
    }

    private void supprimerPhPreparationLigne(PH_Preparation_Ligne ligne_base, Stock_Lot_Emplacement_Light stockLotEmplacementLight)
    {
        PH_Preparation_Ligne ligne_a_supprimer = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByProduitLotPreparationSerieEmplacement(db, ligne_base.getProduitID(), ligne_base.getPreparationID(), stockLotEmplacementLight.getLot(), stockLotEmplacementLight.getSerie(), stockLotEmplacementLight.getEmplacement());

        if(ligne_a_supprimer != null)
            PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ligne_a_supprimer);

        if(produitsuiviserie && !produitserialiserreception)
        {
            if(!stockLotEmplacementLight.getSerie().contentEquals(""))
                Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stockLotEmplacementLight);
        }
    }
}
