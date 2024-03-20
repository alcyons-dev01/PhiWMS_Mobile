package fr.alcyons.phimr4.ReceptionPUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerPreparationActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phimr4.Classes.Commande;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ListViewAdapters.Lot_Reception_PUIAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_LOT;

/**
 * Created by olivier on 04/12/2017.
 */

public class ListeLotReceptionPuiActivity extends ServiceActivity {

    //gestion des variables
    Commande commandeSelectionne;
    PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte;
    Lot_Reception_PUIAdapter lotReceptionPuiAdapter;
    Depot depot;
    PH_Reliquat phReliquat;
    Produit produit;
    boolean scanProduit;
    int quantiteReliquat = 0;
    int quantiteRestant = 0;
    int quantiteLivree = 0;
    boolean numero_serie;
    boolean lotmanuel;

    //gestion des objets graphiques
    ListView lotReceptionPuiListView;
    EditText numLotEditText;
    TextView datePeremptionTextView;
    Lot_Reception_PUIAdapter.Lot_Reception_PUIViewHolder viewHolderAModifier;
    TextView numeroReceptionTextView;
    TextView nomFournisseurTextView;
    TextView designationProduitTextView;
    TextView referenceProduitTextView;
    TextView QteDemandeeTextView;
    TextView QtePreparerTextView;
    LinearLayout lancerScanLinearLayout;
    LinearLayout firstRowLinearLayout;

    //gestion des variables du support
    PackageManager pm;

    //gestion emplacement et produit précédent
    Depot_Emplacement emplacement_precedent;
    Produit produit_precedent;

    private void initObjetGraphique()
    {
        numeroReceptionTextView = ((TextView) findViewById(R.id.numeroReception));
        nomFournisseurTextView = ((TextView) findViewById(R.id.nomFournisseur));
        designationProduitTextView = ((TextView) findViewById(R.id.designationProduit));
        referenceProduitTextView = ((TextView) findViewById(R.id.referenceProduit));
        QteDemandeeTextView = ((TextView) findViewById(R.id.QteDemandee));
        QtePreparerTextView = ((TextView) findViewById(R.id.QtePreparer));
        lancerScanLinearLayout = ((LinearLayout) findViewById(R.id.lancerScan));
        firstRowLinearLayout = ((LinearLayout) findViewById(R.id.firstRow));
        lotReceptionPuiListView = (ListView) findViewById(R.id.liste_view_reception);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lot_reception_pui);

        //gestion des objets graphique
        initObjetGraphique();

        //gestion du package manager
        pm = ListeLotReceptionPuiActivity.this.getPackageManager();

        depot = gestionnaireDepot.getPUICourant(db);
        scanProduit = false;

        //gestion du booleen d'ajout manuel d'un lot
        lotmanuel = false;

        commandeSelectionne = gestionnaireCommande.getCommandeByID(db, intent.getExtras().getInt("commandeID_Selectionne"));
        phReliquatReceptionPUIAdapte = (PH_Reliquat_ReceptionPUI_Adapte) intent.getExtras().getSerializable("phReliquatReceptionPUIAdapte");

        //récupération de l'emplacement et du produit précédent
        emplacement_precedent = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
        produit_precedent = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");

        //on créé deux lot fictif pour la gestion du bouton d'ajout manuel de lot et d'annulation
        PH_Reliquat_ReceptionPUI_Adapte.Lot lot_temp = null;
        lot_temp = phReliquatReceptionPUIAdapte.new Lot("row_ajouter", "00/00/0000", "", "");
        phReliquatReceptionPUIAdapte.getlotList().add(lot_temp);

        lot_temp = phReliquatReceptionPUIAdapte.new Lot("row_annuler", "00/00/0000", "", "");
        phReliquatReceptionPUIAdapte.getlotList().add(lot_temp);

        //gestion de l'entête
        numeroReceptionTextView.setText("#"+commandeSelectionne.getNumero());
        nomFournisseurTextView.setText(commandeSelectionne.getFournisseur());

        if (phReliquatReceptionPUIAdapte != null) {

            //On initialise la liste

            phReliquat = gestionnairePH_Reliquat.getPH_ReliquatById(db, phReliquatReceptionPUIAdapte.getPhReliquatUID());

            if (phReliquat != null) {
                produit = gestionnaireProduit.getProduitByID(db, phReliquat.getProduitID());
                //Entête
                designationProduitTextView.setText(phReliquat.getdesignationCourte().trim());
                referenceProduitTextView.setText(phReliquat.getProduit_Reference().trim());
                quantiteReliquat = phReliquat.getQteCommande() - phReliquat.getQteLivraison();
                QteDemandeeTextView.setText(String.valueOf(phReliquat.getQteCommande()));
                QtePreparerTextView.setText(String.valueOf(phReliquat.getQteLivraison()));
            }

            if(intent.hasExtra("ListeResultat"))
            {
                Map<String, String> liste_scan = (Map<String, String>) intent.getExtras().getSerializable("ListeResultat");
                List<PH_Reliquat_ReceptionPUI_Adapte.Lot> listeLot = new ArrayList<>();
                listeLot.addAll(phReliquatReceptionPUIAdapte.getlotList());
                Date dateFournie = null;
                for(Map.Entry<String, String> entry : liste_scan.entrySet())
                {
                    String code = entry.getKey();
                    String resultat = entry.getValue();
                    Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                    if (gs1Decoupe.size() != 0) {
                        String num_serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                        String conditionnement = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
                        if(!num_serie.contentEquals(""))
                        {
                            num_serie = num_serie.substring(0, num_serie.length()-1);
                        }

                        String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                        lot = lot.substring(0, lot.length()-1);

                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                        Date date = new Date();

                        try {
                            date = dateFormat1.parse(gs1Decoupe.get(OutilsDecodage.dateDePeremption));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String dateFinale = dateFormat1.format(date);

                        DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

                        try {
                            dateFournie = dateDecodeur.parse(dateFinale);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        PH_Reliquat_ReceptionPUI_Adapte.Lot lot_courant = phReliquatReceptionPUIAdapte.new Lot(lot, dateFinale, num_serie, resultat);
                        PH_Reliquat reliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionPUIAdapte.getPhReliquatUID());
                        Produit produit = ProduitOpenHelper.getProduitByID(db, reliquat.getProduitID());
                        Depot_Zone zone = ZoneOpenHelper.getZoneByDepotEtNom(db, DepotOpenHelper.getDepotPUI(db),produit.getZone_PUI_Defaut());
                        if(zone == null)
                        {
                            zone = ZoneOpenHelper.getFirstZone(db, DepotOpenHelper.getDepotPUI(db));
                        }
                        Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone, produit.getEmplacement_PUI_Defaut());
                        if(emplacement == null)
                        {
                            emplacement = EmplacementOpenHelper.getFirstEmplacement(db, zone);
                        }
                        List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement>liste_zone_emplacement = new ArrayList<>();
                        PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionPUIAdapte.new ZoneEtEmplacement(zone.getZoneID(), zone.getZoneName(), emplacement.get_UID(), emplacement.getAdressage(), Integer.parseInt(conditionnement));
                        liste_zone_emplacement.add(zoneEtEmplacement);
                        lot_courant.setZoneEtEmplacementList(liste_zone_emplacement);
                        if(listeLot.indexOf(lot_courant) == -1)
                        {
                            listeLot.add(lot_courant);
                        }
                    }
                }
                phReliquatReceptionPUIAdapte.setlotList(listeLot);
            }

            //gestion du clic sur la zone du datamatrix -> ouverture de l'appareil photo pour scanner des lots de la référence
            lancerScanLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //on supprime les lots d'ajout de lot et d'annulation
                    MAJListeLot();
                    Intent listeLotReceptionPui_Intent = null;
                    Bundle listeLotReceptionPui_Bundle = new Bundle();

                    if(android.os.Build.MANUFACTURER.contains("Zebra Technologies")  || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") )
                    {
                        listeLotReceptionPui_Intent = new Intent(ListeLotReceptionPuiActivity.this, ScannerPreparationActivity.class);
                        listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContextUniqueNewReceptionPUI));
                        listeLotReceptionPui_Bundle.putInt("ReceptionID", commandeSelectionne.getID_commande());
                        listeLotReceptionPui_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        listeLotReceptionPui_Bundle.putSerializable("UniqueReceptionPUIAdapte", (Serializable) phReliquatReceptionPUIAdapte);
                        listeLotReceptionPui_Bundle.putSerializable("ReliquatCourant", (Serializable) phReliquat);
                    }
                    else
                    {
                        listeLotReceptionPui_Intent = new Intent(ListeLotReceptionPuiActivity.this, BarcodePreparationActivity.class);
                        listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContextUniqueNewReceptionPUI));
                        listeLotReceptionPui_Bundle.putInt("ReceptionID", commandeSelectionne.getID_commande());
                        listeLotReceptionPui_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        listeLotReceptionPui_Bundle.putSerializable("UniqueReceptionPUIAdapte", (Serializable) phReliquatReceptionPUIAdapte);
                        listeLotReceptionPui_Bundle.putSerializable("ReliquatCourant", (Serializable) phReliquat);
                    }

                    listeLotReceptionPui_Bundle.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
                    listeLotReceptionPui_Bundle.putSerializable("EmplacementPrecedent", emplacement_precedent);
                    listeLotReceptionPui_Bundle.putSerializable("ProduitPrecedent", produit_precedent);
                    listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
                    ListeLotReceptionPuiActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
                }
            });

        } else {
            ListeLotReceptionPuiActivity.this.finish();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        //gestion du click sur une row
        lotReceptionPuiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                //on check la row cliquer
                PH_Reliquat_ReceptionPUI_Adapte.Lot courant = phReliquatReceptionPUIAdapte.getlotList().get(position);

                if(position == phReliquatReceptionPUIAdapte.getlotList().size()-2)
                {
                    Bundle clicBoutonAjouterManuellement_Bundle = ListeLotReceptionPuiActivity.super.getBundle();
                    clicBoutonAjouterManuellement_Bundle.putInt("produitID", produit.getID_produit());
                    clicBoutonAjouterManuellement_Bundle.putInt("depotID", depot.getDepot_UID());
                    clicBoutonAjouterManuellement_Bundle.putInt("ReliquatID", phReliquat.getReliquat_UID());
                    clicBoutonAjouterManuellement_Bundle.putSerializable("phReliquatReceptionPUIAdapte", phReliquatReceptionPUIAdapte);

                    Intent clicBoutonAjouterManuellement_Intent = new Intent(ListeLotReceptionPuiActivity.this, CreationLotManuelReceptionPUIActivity.class);
                    clicBoutonAjouterManuellement_Intent.putExtras(clicBoutonAjouterManuellement_Bundle);
                    ListeLotReceptionPuiActivity.this.startActivityForResult(clicBoutonAjouterManuellement_Intent, RETOUR_LOT);
                }
                else if(position == phReliquatReceptionPUIAdapte.getlotList().size()-1)
                {
                    boolean confirmation = Alerte.afficherAlerte(ListeLotReceptionPuiActivity.this, "Confirmation", "Attention, tous les lots saisis seront supprimés, souhaitez-vous continuez ?", "OuiNon");

                    if(confirmation)
                    {
                        //si c'est le cas on cache les autres lignes
                        firstRowLinearLayout.setBackground(ListeLotReceptionPuiActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));

                        //reinitialisation de la liste des lots
                        phReliquatReceptionPUIAdapte.setlotList(new ArrayList<PH_Reliquat_ReceptionPUI_Adapte.Lot>());
                        phReliquatReceptionPUIAdapte.getlotList().add(phReliquatReceptionPUIAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
                        phReliquatReceptionPUIAdapte.getlotList().add(phReliquatReceptionPUIAdapte.new Lot("row_annuler", "00/00/0000", "", ""));
                        phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()+phReliquat.getQteLivraison());
                        phReliquat.setQteLivraison(0);
                        PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
                        QteDemandeeTextView.setTextColor(ListeLotReceptionPuiActivity.this.getResources().getColor(R.color.noir));
                        lancerScanLinearLayout.setVisibility(View.VISIBLE);

                        lotReceptionPuiAdapter.full = false;

                        lotReceptionPuiAdapter = new Lot_Reception_PUIAdapter(ListeLotReceptionPuiActivity.this, db, phReliquatReceptionPUIAdapte.getlotList(), phReliquatReceptionPUIAdapte.getPhReliquatUID(), numero_serie, false);
                        lotReceptionPuiListView.setDivider(footer);
                        lotReceptionPuiListView.setAdapter(lotReceptionPuiAdapter);
                        invalidateOptionsMenu();
                    }
                }
                else
                {
                    //on check si le lot n'est pas déjà n'est pas déjà sélectionner
                    Drawable couleur_valider = ListeLotReceptionPuiActivity.this.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne_valider);
                    if(courant.getZoneEtEmplacementList().get(0).getQuantite() != 0 && !lotReceptionPuiAdapter.viewHolderList.get(position).layout_qte_saisie_lot_preparation.getBackground().getConstantState().equals(couleur_valider.getConstantState()) || lotmanuel )
                    {
                        //on récupére la quantité de stock présent fans ce lot
                        int reste_a_preparer = phReliquat.getQteReliquat_X();
                        int quantite_stock_selectionne = courant.getZoneEtEmplacementList().get(0).getQuantite();

                        if(quantite_stock_selectionne > reste_a_preparer)
                        {
                            quantite_stock_selectionne = reste_a_preparer;
                        }
                        else if(quantite_stock_selectionne > phReliquat.getQteReliquat_X())
                        {
                            quantite_stock_selectionne = phReliquat.getQteReliquat_X();
                        }

                        //gestion du visuel
                        courant.getZoneEtEmplacementList().get(0).setQuantite(quantite_stock_selectionne);

                        phReliquat.setQteLivraison(phReliquat.getQteLivraison()+quantite_stock_selectionne);
                        phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()-quantite_stock_selectionne);
                        PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
                        QteDemandeeTextView.setText(String.valueOf(phReliquat.getQteCommande()));
                        lotReceptionPuiAdapter.viewHolderList.get(position).qteSaisie.setText(String.valueOf(quantite_stock_selectionne));
                        lotmanuel = false;
                    }
                    else
                    {
                        boolean confirmation = Alerte.afficherAlerte(ListeLotReceptionPuiActivity.this, "Confirmation", "Attention, le lot sélectionné sera définitivement supprimé, souhaitez-vous continuez ?", "OuiNon");
                        if(confirmation)
                        {
                            //si c'est le cas on cache les autres lignes
                            firstRowLinearLayout.setBackground(ListeLotReceptionPuiActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));

                            //reinitialisation de la liste des lots
                            PH_Reliquat_ReceptionPUI_Adapte.Lot lot_A_Supprimer = phReliquatReceptionPUIAdapte.getlotList().get(position);
                            phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()+lot_A_Supprimer.getZoneEtEmplacementList().get(0).getQuantite());
                            phReliquat.setQteLivraison(phReliquat.getQteLivraison()-lot_A_Supprimer.getZoneEtEmplacementList().get(0).getQuantite());
                            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
                            phReliquatReceptionPUIAdapte.getlotList().remove(position);
                            QteDemandeeTextView.setTextColor(ListeLotReceptionPuiActivity.this.getResources().getColor(R.color.noir));
                            lancerScanLinearLayout.setVisibility(View.VISIBLE);

                            lotReceptionPuiAdapter = new Lot_Reception_PUIAdapter(ListeLotReceptionPuiActivity.this, db, phReliquatReceptionPUIAdapte.getlotList(), phReliquatReceptionPUIAdapte.getPhReliquatUID(), numero_serie, false);
                            lotReceptionPuiListView.setDivider(footer);
                            lotReceptionPuiListView.setAdapter(lotReceptionPuiAdapter);
                            //invalidateOptionsMenu();
                        }
                    }
                    //on regarde si toute la quantité est préparer
                    checkEtatReception();
                }

            }
        });



        if(!scanProduit)
        {
            quantiteLivree = 0;
            for (PH_Reliquat_ReceptionPUI_Adapte.Lot lot : phReliquatReceptionPUIAdapte.getlotList()) {
                for (PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement : lot.getZoneEtEmplacementList()) {
                    quantiteLivree += zoneEtEmplacement.getQuantite();

                }
            }

            numero_serie = false;
            if(phReliquatReceptionPUIAdapte.isSuiviParSerieActif() && phReliquatReceptionPUIAdapte.isSerialiserReception())
            {
                numero_serie = true;
            }

            quantiteReliquat = phReliquat.getQteCommande() - phReliquat.getQteLivraison();
            lotReceptionPuiAdapter = new Lot_Reception_PUIAdapter(ListeLotReceptionPuiActivity.this, db, phReliquatReceptionPUIAdapte.getlotList(), phReliquatReceptionPUIAdapte.getPhReliquatUID(), numero_serie, false);
            lotReceptionPuiListView.setDivider(footer);
            lotReceptionPuiListView.setAdapter(lotReceptionPuiAdapter);
            invalidateOptionsMenu();
        }

        int nbColis = recupererNbColis(produit.getID_produit(), phReliquat.getQteReliquat_X());
        ((TextView) findViewById(R.id.colis)).setText(String.valueOf(nbColis));
    }


    // Permet de lancer l'activity BarcodeCaptureActivity afin de lire un codebarre
    public void decoderCodeBarre(EditText numLotEditText, TextView datePeremptionTextView, Lot_Reception_PUIAdapter.Lot_Reception_PUIViewHolder viewHolder, String contexte) {
        this.numLotEditText = numLotEditText;
        this.datePeremptionTextView = datePeremptionTextView;
        viewHolderAModifier = viewHolder;

        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            Intent listeLotReceptionPui_Intent = new Intent(ListeLotReceptionPuiActivity.this, BarcodeCaptureActivity.class);
            Bundle listeLotReceptionPui_Bundle = super.getBundle();
            if (contexte.contentEquals("emplacement")) {
                listeLotReceptionPui_Bundle.putString("bannerText", "Scanner un emplacement");
                listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
                if(scanProduit)
                    listeLotReceptionPui_Bundle.putBoolean("isBoutonSuppressionExistant", true);
                listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
                ListeLotReceptionPuiActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
            } else {
                String designation = phReliquat.getdesignationCourte();
                Produit produit = ProduitOpenHelper.getProduitByID(db, phReliquat.getProduitID());
                listeLotReceptionPui_Bundle.putBoolean("doitEtreIdentique", true);
                listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                listeLotReceptionPui_Bundle.putString("Designation", designation);
                listeLotReceptionPui_Bundle.putBoolean("isBoutonSuppressionExistant", false);
                listeLotReceptionPui_Bundle.putInt("UserId", utilisateurConnecte.getId());
                listeLotReceptionPui_Bundle.putInt("qteReliquat", quantiteReliquat);
                listeLotReceptionPui_Bundle.putInt("reliquat_uid", phReliquat.getReliquat_UID());
                listeLotReceptionPui_Bundle.putBoolean("modeRafale", false);
                listeLotReceptionPui_Bundle.putString("GTIN_courant", produit.getGTIN());

                listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
                ListeLotReceptionPuiActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RETOUR_CODE_GS1);
            }
        }
    }


    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    if (data != null) {
                        String codeComplet = data.getStringExtra("code");
                        if(codeComplet != null)
                        {
                            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeComplet);
                            if (gs1Decoupe.size() != 0) {
                                if (datePeremptionTextView != null && numLotEditText != null) {
                                    DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                    DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                    Date date = new Date();

                                    try {
                                        date = dateFormat1.parse(gs1Decoupe.get(OutilsDecodage.dateDePeremption));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    String dateFinale = dateFormat2.format(date);

                                    PH_Reliquat_ReceptionPUI_Adapte.Lot phReliquatLotReceptionPui = lotReceptionPuiAdapter.lotList.get(lotReceptionPuiAdapter.viewHolderList.indexOf(viewHolderAModifier));

                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

                                    try {
                                        Date dateFournie = dateDecodeur.parse(dateFinale);
                                        phReliquatLotReceptionPui.setDatePeremption(dateFormat.format(dateFournie));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    String serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                                    String last_char = serie.substring(serie.length()-1);
                                    if(last_char.contentEquals("@"))
                                        serie = serie.substring(0, serie.length()-1);

                                    String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                                    last_char = lot.substring(lot.length()-1);
                                    if(last_char.contentEquals("@"))
                                        lot = lot.substring(0, lot.length()-1);


                                    phReliquatLotReceptionPui.setNumeroLot(lot);
                                    phReliquatLotReceptionPui.setNumero_serie(serie);

                                }
                            } else {
                                Toast toast = Toast.makeText(ListeLotReceptionPuiActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                            scanProduit = true;
                            decoderCodeBarre(null, null, viewHolderAModifier, "emplacement");
                        }
                    }
                    invalidateOptionsMenu();
                    break;
                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    String code = data.getStringExtra("code");
                    if (code != null && !code.contentEquals("")) {
                        int emplacementID = 0;
                        try {
                            emplacementID = Integer.parseInt(code);
                        }catch (Exception e){

                        }
                        if(emplacementID!=0){
                            Depot_Emplacement emplacementRetourne = gestionnaireEmplacement.getUnEmplacementByID(db, emplacementID);
                            Depot_Zone zoneEmplacementRetourne = gestionnaireZone.getUneZoneByID(db, emplacementRetourne.getZoneID());
                            PH_Reliquat_ReceptionPUI_Adapte.Lot lot;

                            int index = lotReceptionPuiAdapter.viewHolderList.indexOf(viewHolderAModifier);

                            lot = lotReceptionPuiAdapter.lotList.get(index);

                            List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement> zoneEtEmplacementList = lot.getZoneEtEmplacementList();
                            if(zoneEtEmplacementList.size() == 1)
                            {
                                PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement courant = zoneEtEmplacementList.get(0);
                                if(courant.getQuantite() == 0)
                                {
                                    zoneEtEmplacementList = new ArrayList<>();
                                    lot.setZoneEtEmplacementList(zoneEtEmplacementList);
                                }
                            }
                            boolean emplacementPresent = false;
                            for (PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement : zoneEtEmplacementList) {
                                if (zoneEtEmplacement.getEmplacementId() == emplacementRetourne.get_UID() && zoneEtEmplacement.getZoneId() == zoneEmplacementRetourne.getZoneID()) {
                                    emplacementPresent = true;
                                }
                            }
                            if (emplacementPresent == false) {
                                PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionPUIAdapte.new ZoneEtEmplacement(zoneEmplacementRetourne.getZoneID(), zoneEmplacementRetourne.getZoneName(), emplacementRetourne.get_UID(), emplacementRetourne.getAdressage(), quantiteRestant);
                                lot.getZoneEtEmplacementList().add(zoneEtEmplacement);
                            }
                        }
                        else{
                            Toast toast = Toast.makeText(ListeLotReceptionPuiActivity.this, "Le code fourni n'est pas un code Emplacement, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                        scanProduit = false;
                    }
                    else
                    {
                        scanProduit = false;
                    }
                    break;
                case CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS:
                    PH_Reliquat_ReceptionPUI_Adapte.Lot phReliquatLotReceptionPuiRecu = (PH_Reliquat_ReceptionPUI_Adapte.Lot) data.getExtras().getSerializable("lotZoneEtEmplacement");
                    PH_Reliquat_ReceptionPUI_Adapte.Lot phReliquatLotReceptionPui = phReliquatReceptionPUIAdapte.getlotList().get(lotReceptionPuiAdapter.viewHolderList.indexOf(viewHolderAModifier));
                    phReliquatLotReceptionPui.setZoneEtEmplacementList(phReliquatLotReceptionPuiRecu.getZoneEtEmplacementList());
                    break;

                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    if(data != null)
                    {
                        //on recupere à nouveau le ph_reliquat courant pour capter les changements en bdd
                        phReliquatReceptionPUIAdapte = null;
                        phReliquatReceptionPUIAdapte = (PH_Reliquat_ReceptionPUI_Adapte) data.getExtras().getSerializable("reliquatAdapte");
                        phReliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionPUIAdapte.getPhReliquatUID());

                        //on remet les lignes d'ajout de lot et d'annulation
                        phReliquatReceptionPUIAdapte.getlotList().add(phReliquatReceptionPUIAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
                        phReliquatReceptionPUIAdapte.getlotList().add(phReliquatReceptionPUIAdapte.new Lot("row_annuler", "00/00/0000", "", ""));
                        lotReceptionPuiAdapter.notifyDataSetChanged();

                        //gestion de l'emplacement scanné
                        emplacement_precedent = (Depot_Emplacement) data.getExtras().getSerializable("EmplacementPrecedent");
                        produit_precedent = (Produit) data.getExtras().getSerializable("ProduitPrecedent");

                        //on gere le visuel
                        onMenuSaveClick();
                    }

                    break;
                case RETOUR_LOT:
                    lotmanuel = true;
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

                    MAJListeLot();

                    PH_Reliquat_ReceptionPUI_Adapte.Lot nouveau_lot = phReliquatReceptionPUIAdapte.new Lot(Lot, peremptionDate, "", "");

                    PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionPUIAdapte.new ZoneEtEmplacement(0, Zone, 0, Emplacement, (int)Qte);

                    // Insertion dans les objets
                    nouveau_lot.getZoneEtEmplacementList().add(zoneEtEmplacement);
                    phReliquatReceptionPUIAdapte.getlotList().add(nouveau_lot);
                    phReliquatReceptionPUIAdapte.getlotList().add(phReliquatReceptionPUIAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
                    phReliquatReceptionPUIAdapte.getlotList().add(phReliquatReceptionPUIAdapte.new Lot("row_annuler", "00/00/0000", "", ""));

                    lotReceptionPuiAdapter.notifyDataSetChanged();
                    lotReceptionPuiListView.performItemClick(lotReceptionPuiListView.getAdapter().getView(phReliquatReceptionPUIAdapte.getlotList().size()-3, null, null), phReliquatReceptionPUIAdapte.getlotList().size()-3, lotReceptionPuiListView.getAdapter().getItemId(phReliquatReceptionPUIAdapte.getlotList().size()-3));

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

    private void onMenuSaveClick()
    {
        MAJListeLot();
        if(phReliquatReceptionPUIAdapte.getlotList().size() ==0)
        {
            Intent resultIntent = new Intent();
            Bundle extras = ListeLotReceptionPuiActivity.super.getBundle();
            extras.putSerializable("lotList", (Serializable) phReliquatReceptionPUIAdapte.getlotList());
            extras.putSerializable("EmplacementPrecedent", emplacement_precedent);
            extras.putSerializable("ProduitPrecedent", produit_precedent);
            resultIntent.putExtras(extras);
            ListeLotReceptionPuiActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS, resultIntent);
            ListeLotReceptionPuiActivity.this.finish();
        }
        else
        {
            Boolean manqueLot = false;
            boolean quantite_ok = true;
            int quantite_total = 0;
            boolean result_alerte = true;
            if (result_alerte) {
                //On vérifie qu'il y a au moins un lot avec une quantité saisie
                if (phReliquatReceptionPUIAdapte.getlotList().size() == 0) {

                } else {
                    for (PH_Reliquat_ReceptionPUI_Adapte.Lot phReliquatLot : phReliquatReceptionPUIAdapte.getlotList()) {
                        if (phReliquatLot.getNumeroLot().contentEquals("")) {
                            Alerte.afficherAlerte(ListeLotReceptionPuiActivity.this, "Alerte", "Veuillez saisir ou scanner un numéro de lot s'il vous plaît", "alerte");
                            manqueLot = true;
                            break;
                        }
                    }
                }

                for (PH_Reliquat_ReceptionPUI_Adapte.Lot phReliquatLot : phReliquatReceptionPUIAdapte.getlotList()) {
                    for(PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement : phReliquatLot.getZoneEtEmplacementList())
                    {
                        quantite_total = quantite_total + zoneEtEmplacement.getQuantite();
                    }
                }

                if(phReliquat.getQteCommande() < quantite_total)
                {
                    quantite_ok = false;
                }

                if(quantite_ok)
                {
                    if (manqueLot == false) {
                        Intent resultIntent = new Intent();
                        Bundle extras = ListeLotReceptionPuiActivity.super.getBundle();
                        extras.putSerializable("lotList", (Serializable) phReliquatReceptionPUIAdapte.getlotList());
                        extras.putSerializable("EmplacementPrecedent", emplacement_precedent);
                        extras.putSerializable("ProduitPrecedent", produit_precedent);
                        resultIntent.putExtras(extras);
                        ListeLotReceptionPuiActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS, resultIntent);
                        ListeLotReceptionPuiActivity.this.finish();
                    }
                }
                else
                {
                    Alerte.afficherAlerte(ListeLotReceptionPuiActivity.this, "Alerte", "La quantité saisie est supérieur à la quantité attendu", "alerte");
                }
            }
            else
            {
                phReliquatReceptionPUIAdapte.getlotList().add(phReliquatReceptionPUIAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
                phReliquatReceptionPUIAdapte.getlotList().add(phReliquatReceptionPUIAdapte.new Lot("row_annuler", "00/00/0000", "", ""));
                onResume();
            }
        }

    }

    private void checkEtatReception()
    {
        if(phReliquat.getQteReliquat_X() == 0)
        {
            //si c'est le cas on cache les autres lignes
            firstRowLinearLayout.setBackground(ListeLotReceptionPuiActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
            QteDemandeeTextView.setTextColor(ListeLotReceptionPuiActivity.this.getResources().getColor(R.color.vert));
            lancerScanLinearLayout.setVisibility(View.GONE);
            QtePreparerTextView.setVisibility(View.INVISIBLE);

            lotReceptionPuiAdapter.full = true;
        }
        else
        {
            //si c'est le cas on cache les autres lignes
            firstRowLinearLayout.setBackground(ListeLotReceptionPuiActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
            lancerScanLinearLayout.setVisibility(View.VISIBLE);
            QtePreparerTextView.setText(String.valueOf(phReliquat.getQteLivraison()));
            QtePreparerTextView.setVisibility(View.VISIBLE);
            QteDemandeeTextView.setTextColor(ListeLotReceptionPuiActivity.this.getResources().getColor(R.color.noir));

            lotReceptionPuiAdapter.full = false;
        }
    }

    @Override
    public void onBackPressed()
    {
        onMenuSaveClick();
    }

    public void ClickNumberPicker(final int position)
    {
        Context context = ListeLotReceptionPuiActivity.this;
        PH_Reliquat_ReceptionPUI_Adapte.Lot courant = phReliquatReceptionPUIAdapte.getlotList().get(position);

        String title = lotReceptionPuiAdapter.viewHolderList.get(position).lot.getText().toString();
        String message = "Quantité placée : ";

        //gestion d'un stock déjà saisie
        if(courant.getZoneEtEmplacementList().get(0).getQuantite() > 0)
        {
            int qte_avant = courant.getZoneEtEmplacementList().get(0).getQuantite();
            courant.getZoneEtEmplacementList().get(0).setQuantite(0);
            phReliquat.setQteLivraison(phReliquat.getQteLivraison()-qte_avant);
            phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()+qte_avant);
            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
            checkEtatReception();
        }

        int value_max = (int)phReliquat.getQteReliquat_X();
        int maxValue = value_max;
        int value = maxValue;

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PH_Reliquat_ReceptionPUI_Adapte.Lot courant = phReliquatReceptionPUIAdapte.getlotList().get(position);
                int qteAprès = aNumberPicker.getValue()*phReliquat.getConditionnementAchat();
                phReliquat.setQteLivraison(phReliquat.getQteLivraison()+qteAprès);
                phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()-qteAprès);
                phReliquatReceptionPUIAdapte.getlotList().get(position).getZoneEtEmplacementList().get(0).setQuantite(qteAprès);
                PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
                QtePreparerTextView.setText(String.valueOf(phReliquat.getQteLivraison()));

                lotReceptionPuiAdapter.viewHolderList.get(position).qteSaisie.setText(String.valueOf(qteAprès));
                courant.getZoneEtEmplacementList().get(0).setQuantite(qteAprès);
                checkEtatReception();
                lotReceptionPuiAdapter.notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) ListeLotReceptionPuiActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
            }
        };

        Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, phReliquat.getConditionnementAchat());
    };

    private void MAJListeLot()
    {
        int index = -1;
        for(int i = 0; i < phReliquatReceptionPUIAdapte.getlotList().size()-1; i++)
        {
            if(phReliquatReceptionPUIAdapte.getlotList().get(i).getNumeroLot().contentEquals("row_ajouter"))
            {
                index = i;
                break;
            }
        }
        if(index != -1)
            phReliquatReceptionPUIAdapte.getlotList().remove(index);
        for(int i = 0; i < phReliquatReceptionPUIAdapte.getlotList().size()-1; i++)
        {
            if(phReliquatReceptionPUIAdapte.getlotList().get(i).getNumeroLot().contentEquals("row_annuler"))
            {
                index = i;
                break;
            }
        }
        if(index != -1)
            phReliquatReceptionPUIAdapte.getlotList().remove(index);
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
}