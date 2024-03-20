package fr.alcyons.phimr4.ControleDesRetours;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Retour;
import fr.alcyons.phimr4.Classes.Retour_Ligne;
import fr.alcyons.phimr4.Classes.Retour_Ligne_ControleRetour_Adapte;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.Lot_ControleDesRetoursAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_CODE_GS1;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_LISTE_LOTS;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_LOT;

public class ListeLotsControleDesRetoursActivity extends ServiceActivity {

    public List<Retour_Ligne_ControleRetour_Adapte.LotAdapte> lotAdaptesList;
    public Retour_Ligne_ControleRetour_Adapte retourLigneAdapteSelectionne;
    public ListView listView;
    public Lot_ControleDesRetoursAdapter adapter;
    int quantiteRetournee = 0;
    int qteDeclaree;
    int quantiteRestant;

    Depot depot;
    Produit produit;

    LinearLayout boutonAjouterParScan;

    PackageManager pm;

    // Définition des actions à réaliser au Click sur une FloatingActionButton
    View.OnClickListener clicBoutonValider = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int compteurLots = 0;
            for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lot : lotAdaptesList) {
                if (lot.getQteSaisie() != 0) {
                    compteurLots++;
                }
            }
            if (compteurLots > 0) {

                Bundle clicBoutonValider_Bundle = ListeLotsControleDesRetoursActivity.super.getBundle();
                clicBoutonValider_Bundle.putSerializable("lotAdaptesList", (Serializable) lotAdaptesList);

                Intent clicBoutonValider_Intent = new Intent();
                clicBoutonValider_Intent.putExtras(clicBoutonValider_Bundle);
                ListeLotsControleDesRetoursActivity.this.setResult(RETOUR_LISTE_LOTS, clicBoutonValider_Intent);
                ListeLotsControleDesRetoursActivity.this.finish();
            } else {
                Toast.makeText(ListeLotsControleDesRetoursActivity.this, "Aucun élément n'a été saisi.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener clicBoutonAjouterManuellement = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle clicBoutonAjouterManuellement_Bundle = ListeLotsControleDesRetoursActivity.super.getBundle();
            clicBoutonAjouterManuellement_Bundle.putInt("produitID", produit.getID_produit());
            clicBoutonAjouterManuellement_Bundle.putInt("depotID", depot.getDepot_UID());

            Intent clicBoutonAjouterManuellement_Intent = new Intent(ListeLotsControleDesRetoursActivity.this, CreationLotControleDesRetoursActivity.class);
            clicBoutonAjouterManuellement_Intent.putExtras(clicBoutonAjouterManuellement_Bundle);
            ListeLotsControleDesRetoursActivity.this.startActivityForResult(clicBoutonAjouterManuellement_Intent, RETOUR_LOT);
        }
    };


    View.OnClickListener clicBoutonAjoutParScan = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Bundle clicBoutonAjoutParScan_Bundle = ListeLotsControleDesRetoursActivity.super.getBundle();
            clicBoutonAjoutParScan_Bundle.putBoolean("doitEtreIdentique", true);
            clicBoutonAjoutParScan_Bundle.putString("Designation", produit.getDesignation_interne());
            clicBoutonAjoutParScan_Bundle.putBoolean("isBoutonSuppressionExistant", true);

            Intent clicBoutonAjoutParScan_intent = null;

            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                clicBoutonAjoutParScan_intent = new Intent(ListeLotsControleDesRetoursActivity.this, ScannerSearchOnlyActivity.class);
            }
            else
            {
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                {
                    clicBoutonAjoutParScan_intent = new Intent(ListeLotsControleDesRetoursActivity.this, BarcodeCaptureActivity.class);
                }
                else
                {
                    clicBoutonAjoutParScan_intent = new Intent(ListeLotsControleDesRetoursActivity.this, ScannerSearchOnlyActivity.class);
                }
            }
            clicBoutonAjoutParScan_intent.putExtras(clicBoutonAjoutParScan_Bundle);

            ListeLotsControleDesRetoursActivity.this.startActivityForResult(clicBoutonAjoutParScan_intent, RETOUR_CODE_GS1);
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lots_controle_des_retours_new);

        //gestion du Package Manager
        pm = ListeLotsControleDesRetoursActivity.this.getPackageManager();

        // Récupération des variables globales
        retourLigneAdapteSelectionne = (Retour_Ligne_ControleRetour_Adapte) intent.getExtras().getSerializable("retourLigneAdapte");
        produit = gestionnaireProduit.getProduitByID(db, intent.getExtras().getInt("produitID"));
        depot = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotID"));
        Retour_Ligne retour_ligne_courant = Retour_LigneOpenHelper.getRetourLigneByID(db, retourLigneAdapteSelectionne.getRetourLigneID());
        Retour retour_courant = RetourOpenHelper.getRetourByID(db, retour_ligne_courant.getRetour_UID());

        // Récupération du Retour_Ligne sélectionné
        final Retour_Ligne retourLigne = gestionnaireRetour_Ligne.getRetourLigneByID(db, retourLigneAdapteSelectionne.getRetourLigneID());

        if (produit == null || depot == null) {
            Alerte.afficherAlerte(ListeLotsControleDesRetoursActivity.this, "Alerte", "Un problème a été constaté en Base de données, veuillez synchroniser l'application ou contacter la société Alcyons (service Contrôle des retours", "alerte");
            ListeLotsControleDesRetoursActivity.this.finish();
            return;
        }

        // Affichage des informations de base
        qteDeclaree = (int) retourLigne.getQte_Demander();
        quantiteRestant = qteDeclaree;

        ((TextView) findViewById(R.id.qteDeclaree)).setText(String.valueOf(qteDeclaree));
        ((TextView) findViewById(R.id.referenceProduit)).setText(produit.getRef_fourni());
        ((TextView) findViewById(R.id.numeroRetour)).setText("#"+retour_courant.getNumero());
        ((TextView) findViewById(R.id.nomDepot)).setText(depot.getNom());
        ((TextView) findViewById(R.id.designationProduit)).setText(produit.getDesignation_interne());

        // Récupération des lots du Retour_Ligne
        lotAdaptesList = retourLigneAdapteSelectionne.getLotAdaptes();

        if (lotAdaptesList.size() == 0) {
            List<Stock_Lot_Emplacement_Light> stockLotEmplacementLights = gestionnaireStock_Lot_Emplacement.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);

            for (Stock_Lot_Emplacement_Light stockLotEmplacement : stockLotEmplacementLights) {
                lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte(stockLotEmplacement));
            }
        } else {
            for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lotAdapte : lotAdaptesList) {
                if (lotAdapte.getQteSaisie() > 0) {
                    quantiteRestant -= lotAdapte.getQteSaisie();
                    quantiteRetournee += lotAdapte.getQteSaisie();
                }
            }
        }

        boutonAjouterParScan = (LinearLayout) findViewById(R.id.boutonAddScan);
        boutonAjouterParScan.setOnClickListener(clicBoutonAjoutParScan);


        // Initialisation de la listView
        listView = (ListView) findViewById(R.id.listeView);
        listView.setItemsCanFocus(true);
        listView.setDivider(footer);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {

                // Ouvre une boite de dialogue avec un NumberPicker
                Context context = ListeLotsControleDesRetoursActivity.this;
                String title = retourLigne.getDestination();
                String message = "Quantité placée : ";
                int maxValue = qteDeclaree;
                int value = quantiteRestant;
                if (lotAdaptesList.get(position).getQteSaisie() > 0) {
                    value = lotAdaptesList.get(position).getQteSaisie();
                }
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        int qteAvant = lotAdaptesList.get(position).getQteSaisie();
                        int qteAprès = aNumberPicker.getValue();
                        int difference = 0;
                        difference = qteAprès - qteAvant;
                        quantiteRetournee = quantiteRetournee + difference;
                        int result = quantiteRetournee - qteDeclaree;
                        if (result > 0) {
                            qteAprès = qteAprès - result;
                        } else {
                            result = 0;
                        }
                        quantiteRetournee = quantiteRetournee - result;
                        quantiteRestant = qteDeclaree - quantiteRetournee;
                        adapter.viewHolders.get(position).qteSaisie.setText(String.valueOf(qteAprès).trim());
                        lotAdaptesList.get(position).setQteSaisie(qteAprès);
                        //adapter.notifyDataSetChanged();
                        InputMethodManager imm = (InputMethodManager)ListeLotsControleDesRetoursActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                        GestionCompteurQteRetourner();
                    }
                };

                Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

            }
        });

    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case RETOUR_CODE_GS1:
                    String code = data.getStringExtra("code");
                    if (code != null) {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                        if (gs1Decoupe.size() != 0) {
                            for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lot : lotAdaptesList) {
                                if (lot.getNumLot().equals(gs1Decoupe.get(OutilsDecodage.numeroLot)) && lot.getDatePeremption().equals(gs1Decoupe.get(OutilsDecodage.dateDePeremption))) {
                                    Alerte.afficherAlerte(ListeLotsControleDesRetoursActivity.this, "Attention", "Le numéro de lot et la date de péremption correspondent dajà à un élément de la liste. La création a été annulée.", "alerte");
                                    return;
                                }
                            }

                            List<Produit> produits = gestionnaireProduit.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                            if (produits.size() == 1) {
                                Produit produit = produits.get(0);
                                Retour_Ligne retourLigne = gestionnaireRetour_Ligne.getRetourLigneByID(db, retourLigneAdapteSelectionne.getRetourLigneID());

                                if (produit.getID_produit() == this.produit.getID_produit()) {
                                    Bundle extras = super.getBundle();
                                    extras.putInt("produitID", produit.getID_produit());
                                    extras.putInt("depotID", intent.getExtras().getInt("depotID"));
                                    extras.putString("numLot", gs1Decoupe.get(OutilsDecodage.numeroLot));
                                    extras.putString("numSerie", gs1Decoupe.get(OutilsDecodage.numeroSerie));
                                    extras.putString("datePeremption", gs1Decoupe.get(OutilsDecodage.dateDePeremption));

                                    Intent newIntent = new Intent(ListeLotsControleDesRetoursActivity.this, CreationLotControleDesRetoursActivity.class);
                                    newIntent.putExtras(extras);
                                    ListeLotsControleDesRetoursActivity.this.startActivityForResult(newIntent, RETOUR_LOT);
                                } else {
                                    Toast.makeText(ListeLotsControleDesRetoursActivity.this, "Le produit scanné n'est pas le bon.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ListeLotsControleDesRetoursActivity.this, "Plusieurs produits correspondent à ce code, impossible d'identifer le produit.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(ListeLotsControleDesRetoursActivity.this, "Le code fourni n'est pas un code GS1.", Toast.LENGTH_SHORT).show();
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

                    lotAdaptesList.add(retourLigneAdapteSelectionne.new LotAdapte(newStockLotEmplacement));
                    Toast.makeText(ListeLotsControleDesRetoursActivity.this, "L'élément a bien été ajouté à la liste", Toast.LENGTH_SHORT);
                    long rowID = gestionnaireStock_Lot_Emplacement.insererUnStock_Lot_EmplacementEnBDD(db, newStockLotEmplacement);
                    if (rowID != -1) {
                        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, newStockLotEmplacement.getPhiMR4UUID(), newStockLotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                    }
                    adapter.notifyDataSetChanged();

                    break;
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Lot_ControleDesRetoursAdapter(ListeLotsControleDesRetoursActivity.this, lotAdaptesList, db, retourLigneAdapteSelectionne, depot.getStructure());
        listView.setDivider(footer);
        listView.setAdapter(adapter);
        GestionCompteurQteRetourner();
    }

    // Si le FloatingMenu est ouvert, au clic sur bouton Back le ferme sinon arret de l'activity
    @Override
    public void onBackPressed() {
        for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lot : lotAdaptesList) {
            lot.setQteSaisie(0);
        }
        ListeLotsControleDesRetoursActivity.this.finish();
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
        int compteurLots = 0;
        for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lot : lotAdaptesList) {
            if (lot.getQteSaisie() != 0) {
                compteurLots++;
            }
        }
        if (compteurLots > 0) {

            Bundle clicBoutonValider_Bundle = ListeLotsControleDesRetoursActivity.super.getBundle();
            clicBoutonValider_Bundle.putSerializable("lotAdaptesList", (Serializable) lotAdaptesList);

            Intent clicBoutonValider_Intent = new Intent();
            clicBoutonValider_Intent.putExtras(clicBoutonValider_Bundle);
            ListeLotsControleDesRetoursActivity.this.setResult(RETOUR_LISTE_LOTS, clicBoutonValider_Intent);
            ListeLotsControleDesRetoursActivity.this.finish();
        } else {
            Toast.makeText(ListeLotsControleDesRetoursActivity.this, "Aucun élément n'a été saisi.", Toast.LENGTH_SHORT).show();
        }
    }

    private void GestionCompteurQteRetourner()
    {
        //Gestion de la quantité déjà retourner
        int qte_deja_retourner = 0;
        for(Retour_Ligne_ControleRetour_Adapte.LotAdapte lotcourant : retourLigneAdapteSelectionne.getLotAdaptes())
        {
            qte_deja_retourner = qte_deja_retourner + lotcourant.getQteSaisie();
        }

        ((TextView) findViewById(R.id.QteRetourner)).setText(String.valueOf(qte_deja_retourner));
    }
}
