package fr.alcyons.phiwms_mobile.ReceptionPUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeReceptionActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerReceptionActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Lot_Reception_PUIAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

/**
 * Created by olivier on 17/04/2024.
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
    PackageManager pm;
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

    @SuppressLint("SetTextI18n")
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

        commandeSelectionne = CommandeOpenHelper.getCommandeByID(db, Objects.requireNonNull(intent.getExtras()).getInt("commandeID_Selectionne"));
        phReliquatReceptionPUIAdapte = (PH_Reliquat_ReceptionPUI_Adapte) intent.getExtras().getSerializable("phReliquatReceptionPUIAdapte");

        //récupération de l'emplacement et du produit précédent
        emplacement_precedent = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
        produit_precedent = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");

        //on créé deux lot fictif pour la gestion du bouton d'ajout manuel de lot et d'annulation
        PH_Reliquat_ReceptionPUI_Adapte.Lot lot_temp;
        lot_temp = phReliquatReceptionPUIAdapte.new Lot("row_ajouter", "00/00/0000", "", "");
        phReliquatReceptionPUIAdapte.getlotList().add(lot_temp);

        lot_temp = phReliquatReceptionPUIAdapte.new Lot("row_annuler", "00/00/0000", "", "");
        phReliquatReceptionPUIAdapte.getlotList().add(lot_temp);

        //gestion de l'entête
        numeroReceptionTextView.setText("#"+commandeSelectionne.getNumero());
        nomFournisseurTextView.setText(commandeSelectionne.getFournisseur());

        if (phReliquatReceptionPUIAdapte != null) {

            //On initialise la liste

            phReliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionPUIAdapte.getPhReliquatUID());

            if (phReliquat != null) {
                produit = ProduitOpenHelper.getProduitByID(db, phReliquat.getProduitID());
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
                List<PH_Reliquat_ReceptionPUI_Adapte.Lot> listeLot = new ArrayList<>(phReliquatReceptionPUIAdapte.getlotList());
                assert liste_scan != null;
                for(Map.Entry<String, String> entry : liste_scan.entrySet())
                {
                    String code = entry.getKey();
                    String resultat = entry.getValue();
                    Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                    if (!gs1Decoupe.isEmpty()) {
                        String num_serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                        String conditionnement = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
                        assert num_serie != null;
                        if(!num_serie.contentEquals(""))
                        {
                            num_serie = num_serie.substring(0, num_serie.length()-1);
                        }

                        String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                        assert lot != null;
                        lot = lot.substring(0, lot.length()-1);

                        @SuppressLint("SimpleDateFormat") DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

                        Date date = new Date();

                        try {
                            date = dateFormat1.parse(Objects.requireNonNull(gs1Decoupe.get(OutilsDecodage.dateDePeremption)));
                        } catch (ParseException e) {
                            Log.e("Parse Exception", Objects.requireNonNull(e.getMessage()));
                        }
                        assert date != null;
                        String dateFinale = dateFormat1.format(date);
                        
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
                        assert conditionnement != null;
                        PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionPUIAdapte.new ZoneEtEmplacement(zone.getZoneID(), zone.getZoneName(), emplacement.get_UID(), emplacement.getAdressage(), Integer.parseInt(conditionnement));
                        liste_zone_emplacement.add(zoneEtEmplacement);
                        lot_courant.setZoneEtEmplacementList(liste_zone_emplacement);
                        if(!listeLot.contains(lot_courant))
                        {
                            listeLot.add(lot_courant);
                        }
                    }
                }
                phReliquatReceptionPUIAdapte.setlotList(listeLot);
            }

            //gestion du clic sur la zone du datamatrix -> ouverture de l'appareil photo pour scanner des lots de la référence
            lancerScanLinearLayout.setOnClickListener(view -> {

                //on supprime les lots d'ajout de lot et d'annulation
                MAJListeLot();
                Intent listeLotReceptionPui_Intent;
                Bundle listeLotReceptionPui_Bundle = new Bundle();

                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies")  || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") )
                {
                    listeLotReceptionPui_Intent = new Intent(ListeLotReceptionPuiActivity.this, ScannerReceptionActivity.class);
                    listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContextUniqueNewReceptionPUI));
                }
                else
                {
                    listeLotReceptionPui_Intent = new Intent(ListeLotReceptionPuiActivity.this, BarcodeReceptionActivity.class);
                    listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContextUniqueNewReceptionPUI));
                }
                listeLotReceptionPui_Bundle.putInt("ReceptionID", commandeSelectionne.getID_commande());
                listeLotReceptionPui_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                listeLotReceptionPui_Bundle.putSerializable("UniqueReceptionPUIAdapte", (Serializable) phReliquatReceptionPUIAdapte);
                listeLotReceptionPui_Bundle.putSerializable("ReliquatCourant", (Serializable) phReliquat);

                listeLotReceptionPui_Bundle.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
                listeLotReceptionPui_Bundle.putSerializable("EmplacementPrecedent", emplacement_precedent);
                listeLotReceptionPui_Bundle.putSerializable("ProduitPrecedent", produit_precedent);
                listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
                ListeLotReceptionPuiActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
            });

        } else {
            ListeLotReceptionPuiActivity.this.finish();
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onResume() {
        super.onResume();

        //gestion du click sur une row
        lotReceptionPuiListView.setOnItemClickListener((parent, view, position, id) -> {
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
                    firstRowLinearLayout.setBackground(ListeLotReceptionPuiActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange, null));

                    //reinitialisation de la liste des lots
                    phReliquatReceptionPUIAdapte.setlotList(new ArrayList<>());
                    phReliquatReceptionPUIAdapte.getlotList().add(phReliquatReceptionPUIAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
                    phReliquatReceptionPUIAdapte.getlotList().add(phReliquatReceptionPUIAdapte.new Lot("row_annuler", "00/00/0000", "", ""));
                    phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()+phReliquat.getQteLivraison());
                    phReliquat.setQteLivraison(0);
                    PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
                    QteDemandeeTextView.setTextColor(ListeLotReceptionPuiActivity.this.getResources().getColor(R.color.noir, null));
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
                Drawable couleur_valider = ListeLotReceptionPuiActivity.this.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne_valider, null);
                if(courant.getZoneEtEmplacementList().get(0).getQuantite() != 0 && !Objects.equals(lotReceptionPuiAdapter.viewHolderList.get(position).layout_qte_saisie_lot_preparation.getBackground().getConstantState(), couleur_valider.getConstantState()) || lotmanuel )
                {
                    int quantite_stock_selectionne = getQuantiteStockSelectionne(courant);
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
                        firstRowLinearLayout.setBackground(ListeLotReceptionPuiActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange, null));

                        //reinitialisation de la liste des lots
                        PH_Reliquat_ReceptionPUI_Adapte.Lot lot_A_Supprimer = phReliquatReceptionPUIAdapte.getlotList().get(position);
                        phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()+lot_A_Supprimer.getZoneEtEmplacementList().get(0).getQuantite());
                        phReliquat.setQteLivraison(phReliquat.getQteLivraison()-lot_A_Supprimer.getZoneEtEmplacementList().get(0).getQuantite());
                        PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
                        phReliquatReceptionPUIAdapte.getlotList().remove(position);
                        QteDemandeeTextView.setTextColor(ListeLotReceptionPuiActivity.this.getResources().getColor(R.color.noir, null));
                        lancerScanLinearLayout.setVisibility(View.VISIBLE);

                        lotReceptionPuiAdapter = new Lot_Reception_PUIAdapter(ListeLotReceptionPuiActivity.this, db, phReliquatReceptionPUIAdapte.getlotList(), phReliquatReceptionPUIAdapte.getPhReliquatUID(), numero_serie, false);
                        lotReceptionPuiListView.setDivider(footer);
                        lotReceptionPuiListView.setAdapter(lotReceptionPuiAdapter);
                    }
                }
                //on regarde si toute la quantité est préparer
                checkEtatReception();
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
            numero_serie = phReliquatReceptionPUIAdapte.isSuiviParSerieActif() && phReliquatReceptionPUIAdapte.isSerialiserReception();

            quantiteReliquat = phReliquat.getQteCommande() - phReliquat.getQteLivraison();
            lotReceptionPuiAdapter = new Lot_Reception_PUIAdapter(ListeLotReceptionPuiActivity.this, db, phReliquatReceptionPUIAdapte.getlotList(), phReliquatReceptionPUIAdapte.getPhReliquatUID(), numero_serie, false);
            lotReceptionPuiListView.setDivider(footer);
            lotReceptionPuiListView.setAdapter(lotReceptionPuiAdapter);
            invalidateOptionsMenu();
        }

        int nbColis = recupererNbColis(produit.getID_produit(), phReliquat.getQteReliquat_X());
        ((TextView) findViewById(R.id.colis)).setText(String.valueOf(nbColis));
    }

    private int getQuantiteStockSelectionne(PH_Reliquat_ReceptionPUI_Adapte.Lot courant) {
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
        return quantite_stock_selectionne;
    }
    
    public void decoderCodeBarre(EditText numLotEditText, TextView datePeremptionTextView, Lot_Reception_PUIAdapter.Lot_Reception_PUIViewHolder viewHolder, String contexte) {
        this.numLotEditText = numLotEditText;
        this.datePeremptionTextView = datePeremptionTextView;
        viewHolderAModifier = viewHolder;

        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    String codeComplet = data.getStringExtra("code");
                    if(codeComplet != null)
                    {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeComplet);
                        if (!gs1Decoupe.isEmpty()) {
                            if (datePeremptionTextView != null && numLotEditText != null) {
                                @SuppressLint("SimpleDateFormat") DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                @SuppressLint("SimpleDateFormat") DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                Date date = new Date();

                                try {
                                    date = dateFormat1.parse(Objects.requireNonNull(gs1Decoupe.get(OutilsDecodage.dateDePeremption)));
                                } catch (ParseException e) {
                                    Log.e("Parse Exception", Objects.requireNonNull(e.getMessage()));
                                }

                                assert date != null;
                                String dateFinale = dateFormat2.format(date);

                                PH_Reliquat_ReceptionPUI_Adapte.Lot phReliquatLotReceptionPui = lotReceptionPuiAdapter.lotList.get(lotReceptionPuiAdapter.viewHolderList.indexOf(viewHolderAModifier));

                                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

                                try {
                                    Date dateFournie = dateDecodeur.parse(dateFinale);
                                    assert dateFournie != null;
                                    phReliquatLotReceptionPui.setDatePeremption(dateFormat.format(dateFournie));
                                } catch (ParseException e) {
                                    Log.e("Parse Exception", Objects.requireNonNull(e.getMessage()));
                                }

                                String serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                                assert serie != null;
                                String last_char = serie.substring(serie.length()-1);
                                if(last_char.contentEquals("@"))
                                    serie = serie.substring(0, serie.length()-1);

                                String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                                assert lot != null;
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
                    invalidateOptionsMenu();
                    break;
                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    String code = data.getStringExtra("code");
                    if (code != null && !code.contentEquals("")) {
                        int emplacementID = 0;
                        try {
                            emplacementID = Integer.parseInt(code);
                        }catch (Exception ignored){

                        }
                        if(emplacementID!=0){
                            Depot_Emplacement emplacementRetourne = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementID);
                            Depot_Zone zoneEmplacementRetourne = ZoneOpenHelper.getUneZoneByID(db, emplacementRetourne.getZoneID());
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
                                    break;
                                }
                            }
                            if (!emplacementPresent) {
                                PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionPUIAdapte.new ZoneEtEmplacement(zoneEmplacementRetourne.getZoneID(), zoneEmplacementRetourne.getZoneName(), emplacementRetourne.get_UID(), emplacementRetourne.getAdressage(), quantiteRestant);
                                lot.getZoneEtEmplacementList().add(zoneEtEmplacement);
                            }
                        }
                        else{
                            Toast toast = Toast.makeText(ListeLotReceptionPuiActivity.this, "Le code fourni n'est pas un code Emplacement, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    }
                    scanProduit = false;
                    break;
                case CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS:
                    PH_Reliquat_ReceptionPUI_Adapte.Lot phReliquatLotReceptionPuiRecu = (PH_Reliquat_ReceptionPUI_Adapte.Lot) Objects.requireNonNull(data.getExtras()).getSerializable("lotZoneEtEmplacement");
                    PH_Reliquat_ReceptionPUI_Adapte.Lot phReliquatLotReceptionPui = phReliquatReceptionPUIAdapte.getlotList().get(lotReceptionPuiAdapter.viewHolderList.indexOf(viewHolderAModifier));
                    assert phReliquatLotReceptionPuiRecu != null;
                    phReliquatLotReceptionPui.setZoneEtEmplacementList(phReliquatLotReceptionPuiRecu.getZoneEtEmplacementList());
                    break;

                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    //on recupere à nouveau le ph_reliquat courant pour capter les changements en bdd
                    phReliquatReceptionPUIAdapte = null;
                    phReliquatReceptionPUIAdapte = (PH_Reliquat_ReceptionPUI_Adapte) Objects.requireNonNull(data.getExtras()).getSerializable("reliquatAdapte");
                    assert phReliquatReceptionPUIAdapte != null;
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

                    break;
                case RETOUR_LOT:
                    lotmanuel = true;
                    String numLot = Objects.requireNonNull(data.getExtras()).getString("numLot");
                    String datePeremption = data.getExtras().getString("datePeremption");
                    double Qte = data.getExtras().getInt("qteActuelle",1);
                    String Emplacement = data.getExtras().getString("nomEmplacement","");
                    String Zone = data.getExtras().getString("nomZone","");

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

                    PH_Reliquat_ReceptionPUI_Adapte.Lot nouveau_lot = phReliquatReceptionPUIAdapte.new Lot(numLot, datePeremption, "", "");

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
        item.setOnMenuItemClickListener(item1 -> {
            onMenuSaveClick();
            return true;
        });
        return true;
    }

    private void onMenuSaveClick()
    {
        MAJListeLot();
        if(phReliquatReceptionPUIAdapte.getlotList().isEmpty())
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
            boolean manqueLot = false;
            boolean quantite_ok = true;
            int quantite_total = 0;
            //On vérifie qu'il y a au moins un lot avec une quantité saisie
            if (!phReliquatReceptionPUIAdapte.getlotList().isEmpty()) {
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
                if (!manqueLot) {
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

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void checkEtatReception()
    {
        if(phReliquat.getQteReliquat_X() == 0)
        {
            //si c'est le cas on cache les autres lignes
            firstRowLinearLayout.setBackground(ListeLotReceptionPuiActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_vert, null));
            QteDemandeeTextView.setTextColor(ListeLotReceptionPuiActivity.this.getResources().getColor(R.color.vert, null));
            lancerScanLinearLayout.setVisibility(View.GONE);
            QtePreparerTextView.setVisibility(View.INVISIBLE);

            lotReceptionPuiAdapter.full = true;
        }
        else
        {
            //si c'est le cas on cache les autres lignes
            firstRowLinearLayout.setBackground(ListeLotReceptionPuiActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange, null));
            lancerScanLinearLayout.setVisibility(View.VISIBLE);
            QtePreparerTextView.setText(String.valueOf(phReliquat.getQteLivraison()));
            QtePreparerTextView.setVisibility(View.VISIBLE);
            QteDemandeeTextView.setTextColor(ListeLotReceptionPuiActivity.this.getResources().getColor(R.color.noir, null));

            lotReceptionPuiAdapter.full = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

        int maxValue = (int)phReliquat.getQteReliquat_X();

        DialogInterface.OnClickListener onClickListener = (dialog, id) -> {
            PH_Reliquat_ReceptionPUI_Adapte.Lot courant1 = phReliquatReceptionPUIAdapte.getlotList().get(position);
            int qteApres = aNumberPicker.getValue()*phReliquat.getConditionnementAchat();
            phReliquat.setQteLivraison(phReliquat.getQteLivraison()+qteApres);
            phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()-qteApres);
            phReliquatReceptionPUIAdapte.getlotList().get(position).getZoneEtEmplacementList().get(0).setQuantite(qteApres);
            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
            QtePreparerTextView.setText(String.valueOf(phReliquat.getQteLivraison()));

            lotReceptionPuiAdapter.viewHolderList.get(position).qteSaisie.setText(String.valueOf(qteApres));
            courant1.getZoneEtEmplacementList().get(0).setQuantite(qteApres);
            checkEtatReception();
            lotReceptionPuiAdapter.notifyDataSetChanged();
            InputMethodManager imm = (InputMethodManager) ListeLotReceptionPuiActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            dialog.dismiss();
        };

        Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, maxValue, maxValue, onClickListener, phReliquat.getConditionnementAchat());
    }

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
            nbColis = (int) (double) nbColis;
        }
        if (quantite != 0) {
            if (nbColis == 0) {
                nbColis = 1;
            }
        }

        return nbColis;
    }
}