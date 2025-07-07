package fr.alcyons.phiwms_mobile.Reception;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Lot_ReceptionAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

import androidx.activity.OnBackPressedCallback;

public class ListeLotReceptionActivity extends ServiceActivity {

    //gestion des variables
    Commande commandeSelectionne;
    PH_Reliquat_Reception_Adapte phReliquatReceptionAdapte;
    Lot_ReceptionAdapter lotReceptionAdapter;
    Depot depot;
    PH_Reliquat phReliquat;
    Produit produit;
    boolean scanProduit;
    int quantiteReliquat = 0;
    int quantiteRestant = 0;
    int quantiteLivree = 0;
    boolean numero_serie;
    boolean lotmanuel;
    ListView lotReceptionListView;
    EditText numLotEditText;
    TextView datePeremptionTextView;
    Lot_ReceptionAdapter.Lot_ReceptionViewHolder viewHolderAModifier;
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
        lotReceptionListView = (ListView) findViewById(R.id.liste_view_reception);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lot_reception);

        //gestion des objets graphique
        initObjetGraphique();

        //gestion du package manager
        pm = ListeLotReceptionActivity.this.getPackageManager();

        depot = DepotOpenHelper.getPUICourant(db);
        scanProduit = false;

        //gestion du booleen d'ajout manuel d'un lot
        lotmanuel = false;

        commandeSelectionne = CommandeOpenHelper.getCommandeByID(db, Objects.requireNonNull(intent.getExtras()).getInt("commandeID_Selectionne"));
        phReliquatReceptionAdapte = (PH_Reliquat_Reception_Adapte) intent.getExtras().getSerializable("phReliquatReceptionAdapte");

        //récupération de l'emplacement et du produit précédent
        emplacement_precedent = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
        produit_precedent = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");

        //on créé deux lot fictif pour la gestion du bouton d'ajout manuel de lot et d'annulation
        PH_Reliquat_Reception_Adapte.Lot lot_temp;
        lot_temp = phReliquatReceptionAdapte.new Lot("row_ajouter", "00/00/0000", "", "");
        phReliquatReceptionAdapte.getlotList().add(lot_temp);

        lot_temp = phReliquatReceptionAdapte.new Lot("row_annuler", "00/00/0000", "", "");
        phReliquatReceptionAdapte.getlotList().add(lot_temp);

        //gestion de l'entête
        numeroReceptionTextView.setText("#"+commandeSelectionne.getNumero());
        nomFournisseurTextView.setText(commandeSelectionne.getFournisseur());

        if (phReliquatReceptionAdapte != null) {

            //On initialise la liste

            phReliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionAdapte.getPhReliquatUID());

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
                List<PH_Reliquat_Reception_Adapte.Lot> listeLot = new ArrayList<>(phReliquatReceptionAdapte.getlotList());
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
                        
                        PH_Reliquat_Reception_Adapte.Lot lot_courant = phReliquatReceptionAdapte.new Lot(lot, dateFinale, num_serie, resultat);
                        PH_Reliquat reliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionAdapte.getPhReliquatUID());
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
                        List<PH_Reliquat_Reception_Adapte.ZoneEtEmplacement>liste_zone_emplacement = new ArrayList<>();
                        assert conditionnement != null;
                        PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionAdapte.new ZoneEtEmplacement(zone.getZoneID(), zone.getZoneName(), emplacement.get_UID(), emplacement.getAdressage(), Integer.parseInt(conditionnement));
                        liste_zone_emplacement.add(zoneEtEmplacement);
                        lot_courant.setZoneEtEmplacementList(liste_zone_emplacement);
                        if(!listeLot.contains(lot_courant))
                        {
                            listeLot.add(lot_courant);
                        }
                    }
                }
                phReliquatReceptionAdapte.setlotList(listeLot);
            }

            //gestion du clic sur la zone du datamatrix -> ouverture de l'appareil photo pour scanner des lots de la référence
            lancerScanLinearLayout.setOnClickListener(view -> {

                //on supprime les lots d'ajout de lot et d'annulation
                MAJListeLot();
                Intent listeLotReception_Intent;
                Bundle listeLotReception_Bundle = new Bundle();

                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies")  || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") )
                {
                    listeLotReception_Intent = new Intent(ListeLotReceptionActivity.this, ScannerReceptionActivity.class);
                    listeLotReception_Bundle.putString("contexte", String.valueOf(R.string.scannerContextReceptionUnique));
                }
                else
                {
                    listeLotReception_Intent = new Intent(ListeLotReceptionActivity.this, BarcodeReceptionActivity.class);
                    listeLotReception_Bundle.putString("contexte", String.valueOf(R.string.scannerContextReceptionUnique));
                }
                listeLotReception_Bundle.putInt("ReceptionID", commandeSelectionne.getID_commande());
                listeLotReception_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                listeLotReception_Bundle.putSerializable("UniqueReceptionPUIAdapte", (Serializable) phReliquatReceptionAdapte);
                listeLotReception_Bundle.putSerializable("ReliquatCourant", (Serializable) phReliquat);

                listeLotReception_Bundle.putInt("serviceSelectionneID", intent.getExtras().getInt("serviceSelectionneID"));
                listeLotReception_Bundle.putSerializable("EmplacementPrecedent", emplacement_precedent);
                listeLotReception_Bundle.putSerializable("ProduitPrecedent", produit_precedent);
                listeLotReception_Intent.putExtras(listeLotReception_Bundle);
                ListeLotReceptionActivity.this.startActivityForResult(listeLotReception_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
            });

        } else {
            ListeLotReceptionActivity.this.finish();
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onMenuSaveClick();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onResume() {
        super.onResume();

        //gestion du click sur une row
        lotReceptionListView.setOnItemClickListener((parent, view, position, id) -> {
            //on check la row cliquer
            PH_Reliquat_Reception_Adapte.Lot courant = phReliquatReceptionAdapte.getlotList().get(position);

            if(position == phReliquatReceptionAdapte.getlotList().size()-2)
            {
                Bundle clicBoutonAjouterManuellement_Bundle = ListeLotReceptionActivity.super.getBundle();
                clicBoutonAjouterManuellement_Bundle.putInt("produitID", produit.getID_produit());
                clicBoutonAjouterManuellement_Bundle.putInt("depotID", depot.getDepot_UID());
                clicBoutonAjouterManuellement_Bundle.putInt("ReliquatID", phReliquat.getReliquat_UID());
                clicBoutonAjouterManuellement_Bundle.putSerializable("phReliquatReceptionAdapte", phReliquatReceptionAdapte);

                Intent clicBoutonAjouterManuellement_Intent = new Intent(ListeLotReceptionActivity.this, CreationLotManuelReceptionActivity.class);
                clicBoutonAjouterManuellement_Intent.putExtras(clicBoutonAjouterManuellement_Bundle);
                ListeLotReceptionActivity.this.startActivityForResult(clicBoutonAjouterManuellement_Intent, RETOUR_LOT);
            }
            else if(position == phReliquatReceptionAdapte.getlotList().size()-1)
            {
                /*boolean confirmation = Alerte.afficherAlerte(ListeLotReceptionActivity.this, "Confirmation", "Attention, tous les lots saisis seront supprimés, souhaitez-vous continuez ?", "OuiNon");

                if(confirmation)
                {
                    //si c'est le cas on cache les autres lignes
                    firstRowLinearLayout.setBackground(ListeLotReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange, null));

                    //reinitialisation de la liste des lots
                    phReliquatReceptionAdapte.setlotList(new ArrayList<>());
                    phReliquatReceptionAdapte.getlotList().add(phReliquatReceptionAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
                    phReliquatReceptionAdapte.getlotList().add(phReliquatReceptionAdapte.new Lot("row_annuler", "00/00/0000", "", ""));
                    phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()+phReliquat.getQteLivraison());
                    phReliquat.setQteLivraison(0);
                    PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
                    QteDemandeeTextView.setTextColor(ListeLotReceptionActivity.this.getResources().getColor(R.color.noir, null));
                    lancerScanLinearLayout.setVisibility(View.VISIBLE);

                    lotReceptionAdapter.full = false;

                    lotReceptionAdapter = new Lot_ReceptionAdapter(ListeLotReceptionActivity.this, db, phReliquatReceptionAdapte.getlotList(), phReliquatReceptionAdapte.getPhReliquatUID(), numero_serie, false);
                    lotReceptionListView.setDivider(footer);
                    lotReceptionListView.setAdapter(lotReceptionAdapter);
                    invalidateOptionsMenu();
                }*/

                afficherAlerteConfirmationSuppression(ListeLotReceptionActivity.this, ListeLotReceptionActivity.this.getLayoutInflater());
            }
            else
            {
                //on check si le lot n'est pas déjà n'est pas déjà sélectionner
                Drawable couleur_valider = ListeLotReceptionActivity.this.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne_valider, null);
                if(courant.getZoneEtEmplacementList().get(0).getQuantite() != 0 && !Objects.equals(lotReceptionAdapter.viewHolderList.get(position).layout_qte_saisie_lot_preparation.getBackground().getConstantState(), couleur_valider.getConstantState()) || lotmanuel )
                {
                    int quantite_stock_selectionne = getQuantiteStockSelectionne(courant);
                    //gestion du visuel
                    courant.getZoneEtEmplacementList().get(0).setQuantite(quantite_stock_selectionne);

                    phReliquat.setQteLivraison(phReliquat.getQteLivraison()+quantite_stock_selectionne);
                    phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()-quantite_stock_selectionne);
                    PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
                    QteDemandeeTextView.setText(String.valueOf(phReliquat.getQteCommande()));
                    lotReceptionAdapter.viewHolderList.get(position).qteSaisie.setText(String.valueOf(quantite_stock_selectionne));
                    lotmanuel = false;
                }
                else
                {
                    /*boolean confirmation = Alerte.afficherAlerte(ListeLotReceptionActivity.this, "Confirmation", "Attention, le lot sélectionné sera définitivement supprimé, souhaitez-vous continuez ?", "OuiNon");
                    if(confirmation)
                    {
                        //si c'est le cas on cache les autres lignes
                        firstRowLinearLayout.setBackground(ListeLotReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange, null));

                        //reinitialisation de la liste des lots
                        PH_Reliquat_Reception_Adapte.Lot lot_A_Supprimer = phReliquatReceptionAdapte.getlotList().get(position);
                        phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()+lot_A_Supprimer.getZoneEtEmplacementList().get(0).getQuantite());
                        phReliquat.setQteLivraison(phReliquat.getQteLivraison()-lot_A_Supprimer.getZoneEtEmplacementList().get(0).getQuantite());
                        PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
                        phReliquatReceptionAdapte.getlotList().remove(position);
                        QteDemandeeTextView.setTextColor(ListeLotReceptionActivity.this.getResources().getColor(R.color.noir, null));
                        lancerScanLinearLayout.setVisibility(View.VISIBLE);

                        lotReceptionAdapter = new Lot_ReceptionAdapter(ListeLotReceptionActivity.this, db, phReliquatReceptionAdapte.getlotList(), phReliquatReceptionAdapte.getPhReliquatUID(), numero_serie, false);
                        lotReceptionListView.setDivider(footer);
                        lotReceptionListView.setAdapter(lotReceptionAdapter);
                    }*/
                    afficherAlerteConfirmationSuppression(ListeLotReceptionActivity.this, ListeLotReceptionActivity.this.getLayoutInflater());

                }
                //on regarde si toute la quantité est préparer
                checkEtatReception();
            }

        });
        
        if(!scanProduit)
        {
            quantiteLivree = 0;
            for (PH_Reliquat_Reception_Adapte.Lot lot : phReliquatReceptionAdapte.getlotList()) {
                for (PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement : lot.getZoneEtEmplacementList()) {
                    quantiteLivree += zoneEtEmplacement.getQuantite();
                }
            }
            numero_serie = phReliquatReceptionAdapte.isSuiviParSerieActif() && phReliquatReceptionAdapte.isSerialiserReception();

            quantiteReliquat = phReliquat.getQteCommande() - phReliquat.getQteLivraison();
            lotReceptionAdapter = new Lot_ReceptionAdapter(ListeLotReceptionActivity.this, db, phReliquatReceptionAdapte.getlotList(), phReliquatReceptionAdapte.getPhReliquatUID(), numero_serie, false);
            lotReceptionListView.setDivider(footer);
            lotReceptionListView.setAdapter(lotReceptionAdapter);
            invalidateOptionsMenu();
        }

        int nbColis = recupererNbColis(produit.getID_produit(), phReliquat.getQteReliquat_X());
        ((TextView) findViewById(R.id.colis)).setText(String.valueOf(nbColis));
    }

    private int getQuantiteStockSelectionne(PH_Reliquat_Reception_Adapte.Lot courant) {
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
    
    public void decoderCodeBarre(EditText numLotEditText, TextView datePeremptionTextView, Lot_ReceptionAdapter.Lot_ReceptionViewHolder viewHolder, String contexte) {
        this.numLotEditText = numLotEditText;
        this.datePeremptionTextView = datePeremptionTextView;
        viewHolderAModifier = viewHolder;

        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            Intent listeLotReception_Intent = new Intent(ListeLotReceptionActivity.this, BarcodeCaptureActivity.class);
            Bundle listeLotReception_Bundle = super.getBundle();
            if (contexte.contentEquals("emplacement")) {
                listeLotReception_Bundle.putString("bannerText", "Scanner un emplacement");
                listeLotReception_Bundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
                if(scanProduit)
                    listeLotReception_Bundle.putBoolean("isBoutonSuppressionExistant", true);
                listeLotReception_Intent.putExtras(listeLotReception_Bundle);
                ListeLotReceptionActivity.this.startActivityForResult(listeLotReception_Intent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
            } else {
                String designation = phReliquat.getdesignationCourte();
                Produit produit = ProduitOpenHelper.getProduitByID(db, phReliquat.getProduitID());
                listeLotReception_Bundle.putBoolean("doitEtreIdentique", true);
                listeLotReception_Bundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                listeLotReception_Bundle.putString("Designation", designation);
                listeLotReception_Bundle.putBoolean("isBoutonSuppressionExistant", false);
                listeLotReception_Bundle.putInt("UserId", utilisateurConnecte.getId());
                listeLotReception_Bundle.putInt("qteReliquat", quantiteReliquat);
                listeLotReception_Bundle.putInt("reliquat_uid", phReliquat.getReliquat_UID());
                listeLotReception_Bundle.putBoolean("modeRafale", false);
                listeLotReception_Bundle.putString("GTIN_courant", produit.getGTIN());

                listeLotReception_Intent.putExtras(listeLotReception_Bundle);
                ListeLotReceptionActivity.this.startActivityForResult(listeLotReception_Intent, CodesEchangesActivites.RETOUR_CODE_GS1);
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

                                PH_Reliquat_Reception_Adapte.Lot phReliquatLotReception = lotReceptionAdapter.lotList.get(lotReceptionAdapter.viewHolderList.indexOf(viewHolderAModifier));

                                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

                                try {
                                    Date dateFournie = dateDecodeur.parse(dateFinale);
                                    assert dateFournie != null;
                                    phReliquatLotReception.setDatePeremption(dateFormat.format(dateFournie));
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
                                
                                phReliquatLotReception.setNumeroLot(lot);
                                phReliquatLotReception.setNumero_serie(serie);
                            }
                        } else {
                            Toast toast = Toast.makeText(ListeLotReceptionActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
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
                            PH_Reliquat_Reception_Adapte.Lot lot;

                            int index = lotReceptionAdapter.viewHolderList.indexOf(viewHolderAModifier);

                            lot = lotReceptionAdapter.lotList.get(index);

                            List<PH_Reliquat_Reception_Adapte.ZoneEtEmplacement> zoneEtEmplacementList = lot.getZoneEtEmplacementList();
                            if(zoneEtEmplacementList.size() == 1)
                            {
                                PH_Reliquat_Reception_Adapte.ZoneEtEmplacement courant = zoneEtEmplacementList.get(0);
                                if(courant.getQuantite() == 0)
                                {
                                    zoneEtEmplacementList = new ArrayList<>();
                                    lot.setZoneEtEmplacementList(zoneEtEmplacementList);
                                }
                            }
                            boolean emplacementPresent = false;
                            for (PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement : zoneEtEmplacementList) {
                                if (zoneEtEmplacement.getEmplacementId() == emplacementRetourne.get_UID() && zoneEtEmplacement.getZoneId() == zoneEmplacementRetourne.getZoneID()) {
                                    emplacementPresent = true;
                                    break;
                                }
                            }
                            if (!emplacementPresent) {
                                PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionAdapte.new ZoneEtEmplacement(zoneEmplacementRetourne.getZoneID(), zoneEmplacementRetourne.getZoneName(), emplacementRetourne.get_UID(), emplacementRetourne.getAdressage(), quantiteRestant);
                                lot.getZoneEtEmplacementList().add(zoneEtEmplacement);
                            }
                        }
                        else{
                            Toast toast = Toast.makeText(ListeLotReceptionActivity.this, "Le code fourni n'est pas un code Emplacement, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    }
                    scanProduit = false;
                    break;
                case CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS:
                    PH_Reliquat_Reception_Adapte.Lot phReliquatLotReceptionRecu = (PH_Reliquat_Reception_Adapte.Lot) Objects.requireNonNull(data.getExtras()).getSerializable("lotZoneEtEmplacement");
                    PH_Reliquat_Reception_Adapte.Lot phReliquatLotReception = phReliquatReceptionAdapte.getlotList().get(lotReceptionAdapter.viewHolderList.indexOf(viewHolderAModifier));
                    assert phReliquatLotReceptionRecu != null;
                    phReliquatLotReception.setZoneEtEmplacementList(phReliquatLotReceptionRecu.getZoneEtEmplacementList());
                    break;

                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    //on recupere à nouveau le ph_reliquat courant pour capter les changements en bdd
                    phReliquatReceptionAdapte = null;
                    phReliquatReceptionAdapte = (PH_Reliquat_Reception_Adapte) Objects.requireNonNull(data.getExtras()).getSerializable("reliquatAdapte");
                    assert phReliquatReceptionAdapte != null;
                    phReliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionAdapte.getPhReliquatUID());

                    //on remet les lignes d'ajout de lot et d'annulation
                    phReliquatReceptionAdapte.getlotList().add(phReliquatReceptionAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
                    phReliquatReceptionAdapte.getlotList().add(phReliquatReceptionAdapte.new Lot("row_annuler", "00/00/0000", "", ""));
                    lotReceptionAdapter.notifyDataSetChanged();

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

                    PH_Reliquat_Reception_Adapte.Lot nouveau_lot = phReliquatReceptionAdapte.new Lot(numLot, datePeremption, "", "");

                    PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionAdapte.new ZoneEtEmplacement(0, Zone, 0, Emplacement, (int)Qte);

                    // Insertion dans les objets
                    nouveau_lot.getZoneEtEmplacementList().add(zoneEtEmplacement);
                    phReliquatReceptionAdapte.getlotList().add(nouveau_lot);
                    phReliquatReceptionAdapte.getlotList().add(phReliquatReceptionAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
                    phReliquatReceptionAdapte.getlotList().add(phReliquatReceptionAdapte.new Lot("row_annuler", "00/00/0000", "", ""));

                    lotReceptionAdapter.notifyDataSetChanged();
                    lotReceptionListView.performItemClick(lotReceptionListView.getAdapter().getView(phReliquatReceptionAdapte.getlotList().size()-3, null, null), phReliquatReceptionAdapte.getlotList().size()-3, lotReceptionListView.getAdapter().getItemId(phReliquatReceptionAdapte.getlotList().size()-3));
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
        if(phReliquatReceptionAdapte.getlotList().isEmpty())
        {
            Intent resultIntent = new Intent();
            Bundle extras = ListeLotReceptionActivity.super.getBundle();
            extras.putSerializable("lotList", (Serializable) phReliquatReceptionAdapte.getlotList());
            extras.putSerializable("EmplacementPrecedent", emplacement_precedent);
            extras.putSerializable("ProduitPrecedent", produit_precedent);
            resultIntent.putExtras(extras);
            ListeLotReceptionActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS, resultIntent);
            ListeLotReceptionActivity.this.finish();
        }
        else
        {
            boolean manqueLot = false;
            boolean quantite_ok = true;
            int quantite_total = 0;
            //On vérifie qu'il y a au moins un lot avec une quantité saisie
            if (!phReliquatReceptionAdapte.getlotList().isEmpty()) {
                for (PH_Reliquat_Reception_Adapte.Lot phReliquatLot : phReliquatReceptionAdapte.getlotList()) {
                    if (phReliquatLot.getNumeroLot().contentEquals("")) {
                        Alerte.afficherAlerte(ListeLotReceptionActivity.this, "Alerte", "Veuillez saisir ou scanner un numéro de lot s'il vous plaît", "alerte");
                        manqueLot = true;
                        break;
                    }
                }
            }
            for (PH_Reliquat_Reception_Adapte.Lot phReliquatLot : phReliquatReceptionAdapte.getlotList()) {
                for(PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement : phReliquatLot.getZoneEtEmplacementList())
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
                    Bundle extras = ListeLotReceptionActivity.super.getBundle();
                    extras.putSerializable("lotList", (Serializable) phReliquatReceptionAdapte.getlotList());
                    extras.putSerializable("EmplacementPrecedent", emplacement_precedent);
                    extras.putSerializable("ProduitPrecedent", produit_precedent);
                    resultIntent.putExtras(extras);
                    ListeLotReceptionActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS, resultIntent);
                    ListeLotReceptionActivity.this.finish();
                }
            }
            else
            {
                Alerte.afficherAlerte(ListeLotReceptionActivity.this, "Alerte", "La quantité saisie est supérieur à la quantité attendu", "alerte");
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void checkEtatReception()
    {
        if(phReliquat.getQteReliquat_X() == 0)
        {
            //si c'est le cas on cache les autres lignes
            firstRowLinearLayout.setBackground(ListeLotReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_vert, null));
            QteDemandeeTextView.setTextColor(ListeLotReceptionActivity.this.getResources().getColor(R.color.vert, null));
            lancerScanLinearLayout.setVisibility(View.GONE);
            QtePreparerTextView.setVisibility(View.INVISIBLE);

            lotReceptionAdapter.full = true;
        }
        else
        {
            //si c'est le cas on cache les autres lignes
            firstRowLinearLayout.setBackground(ListeLotReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange, null));
            lancerScanLinearLayout.setVisibility(View.VISIBLE);
            QtePreparerTextView.setText(String.valueOf(phReliquat.getQteLivraison()));
            QtePreparerTextView.setVisibility(View.VISIBLE);
            QteDemandeeTextView.setTextColor(ListeLotReceptionActivity.this.getResources().getColor(R.color.noir, null));

            lotReceptionAdapter.full = false;
        }
    }


    public void ClickNumberPicker(final int position)
    {
        Context context = ListeLotReceptionActivity.this;
        PH_Reliquat_Reception_Adapte.Lot courant = phReliquatReceptionAdapte.getlotList().get(position);

        String title = lotReceptionAdapter.viewHolderList.get(position).lot.getText().toString();
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
            PH_Reliquat_Reception_Adapte.Lot courant1 = phReliquatReceptionAdapte.getlotList().get(position);
            int qteApres = aNumberPicker.getValue()*phReliquat.getConditionnementAchat();
            phReliquat.setQteLivraison(phReliquat.getQteLivraison()+qteApres);
            phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()-qteApres);
            phReliquatReceptionAdapte.getlotList().get(position).getZoneEtEmplacementList().get(0).setQuantite(qteApres);
            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
            QtePreparerTextView.setText(String.valueOf(phReliquat.getQteLivraison()));

            lotReceptionAdapter.viewHolderList.get(position).qteSaisie.setText(String.valueOf(qteApres));
            courant1.getZoneEtEmplacementList().get(0).setQuantite(qteApres);
            checkEtatReception();
            lotReceptionAdapter.notifyDataSetChanged();
            InputMethodManager imm = (InputMethodManager) ListeLotReceptionActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            dialog.dismiss();
        };

        Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, maxValue, maxValue, onClickListener, phReliquat.getConditionnementAchat());
    }

    private void MAJListeLot()
    {
        int index = -1;
        for(int i = 0; i < phReliquatReceptionAdapte.getlotList().size()-1; i++)
        {
            if(phReliquatReceptionAdapte.getlotList().get(i).getNumeroLot().contentEquals("row_ajouter"))
            {
                index = i;
                break;
            }
        }
        if(index != -1)
            phReliquatReceptionAdapte.getlotList().remove(index);
        for(int i = 0; i < phReliquatReceptionAdapte.getlotList().size()-1; i++)
        {
            if(phReliquatReceptionAdapte.getlotList().get(i).getNumeroLot().contentEquals("row_annuler"))
            {
                index = i;
                break;
            }
        }
        if(index != -1)
            phReliquatReceptionAdapte.getlotList().remove(index);
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

    @SuppressLint("SetTextI18n")
    public void afficherAlerteConfirmationSuppression(Context context, LayoutInflater inflater) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_mail, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageTextView = (TextView) layout.findViewById(R.id.messageFin);
        messageTextView.setText("Attention, tous les lots saisis seront supprimés, souhaitez-vous continuez ?");
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            firstRowLinearLayout.setBackground(ListeLotReceptionActivity.this.getResources().getDrawable(R.drawable.background_detail_preparation_orange, null));

            //reinitialisation de la liste des lots
            phReliquatReceptionAdapte.setlotList(new ArrayList<>());
            phReliquatReceptionAdapte.getlotList().add(phReliquatReceptionAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
            phReliquatReceptionAdapte.getlotList().add(phReliquatReceptionAdapte.new Lot("row_annuler", "00/00/0000", "", ""));
            phReliquat.setQteReliquat_X(phReliquat.getQteReliquat_X()+phReliquat.getQteLivraison());
            phReliquat.setQteLivraison(0);
            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, phReliquat);
            QteDemandeeTextView.setTextColor(ListeLotReceptionActivity.this.getResources().getColor(R.color.noir, null));
            lancerScanLinearLayout.setVisibility(View.VISIBLE);

            lotReceptionAdapter.full = false;

            lotReceptionAdapter = new Lot_ReceptionAdapter(ListeLotReceptionActivity.this, db, phReliquatReceptionAdapte.getlotList(), phReliquatReceptionAdapte.getPhReliquatUID(), numero_serie, false);
            lotReceptionListView.setDivider(footer);
            lotReceptionListView.setAdapter(lotReceptionAdapter);
            invalidateOptionsMenu();
        });

        buttonAnnuler.setOnClickListener(v -> alertDialog.dismiss());
    }

}