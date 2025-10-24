package fr.alcyons.phiwms_mobile.Reception;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeReceptionActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerReceptionActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.LotReception_Adapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ListeLotReceptionActivity extends ServiceActivity {
    //gestion des variables
    Commande commandeSelectionne;
    LotReception_Adapter lotReceptionAdapter;
    Depot depot;
    PH_Reliquat phReliquat;
    Produit produit;
    boolean scanProduit;
    int quantiteReliquat = 0;
    int quantiteRestant = 0;
    int quantiteLivree = 0;
    boolean numero_serie;
    boolean lotmanuel;
    EditText numLotEditText;
    TextView datePeremptionTextView;
    LotReception_Adapter.LotViewHolder viewHolderAModifier;
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
    RecyclerView recyclerView;

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
        recyclerView = (RecyclerView) findViewById(R.id.liste_view_reception);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int decorationCount = recyclerView.getItemDecorationCount();
        for (int i = 0; i < decorationCount; i++) {
            recyclerView.removeItemDecorationAt(0);
        }
        DividerItemDecoration divider = new DividerItemDecoration(ListeLotReceptionActivity.this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(ListeLotReceptionActivity.this, R.drawable.recycler_divider));
        recyclerView.addItemDecoration(divider);
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
        phReliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, intent.getExtras().getInt("phReliquatId"));

        //récupération de l'emplacement et du produit précédent
        emplacement_precedent = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
        produit_precedent = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");

        //gestion de l'entête
        numeroReceptionTextView.setText("#"+commandeSelectionne.getNumero());
        nomFournisseurTextView.setText(commandeSelectionne.getFournisseur());

        if (phReliquat != null) {
            produit = ProduitOpenHelper.getProduitByID(db, phReliquat.getProduitID());
            numero_serie = produit.isSuivi_Serialisation() && produit.isSerialiser_Reception_Delivrance();

            //calcul quantite restante à réceptionner
            List<PH_Reliquat> reliquatReceptionne = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeSelectionne.getNumero(), phReliquat.getProduitID());
            quantiteRestant = phReliquat.getQteReliquat_X();
            for(PH_Reliquat reliquatCourant : reliquatReceptionne)
            {
                quantiteRestant = quantiteRestant - reliquatCourant.getQteLivraison();
            }

            //Entête
            designationProduitTextView.setText(phReliquat.getdesignationCourte().trim());
            referenceProduitTextView.setText(phReliquat.getProduit_Reference().trim());
            QteDemandeeTextView.setText(String.valueOf(phReliquat.getQteReliquat_X()));

            calculQuantiteReception();
            //gestion du clic sur la zone du datamatrix -> ouverture de l'appareil photo pour scanner des lots de la référence
            lancerScanLinearLayout.setOnClickListener(view -> {

                Intent listeLotReception_Intent;
                Bundle listeLotReception_Bundle = new Bundle();

                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies")  || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
                {
                    listeLotReception_Intent = new Intent(ListeLotReceptionActivity.this, ScannerReceptionActivity.class);
                }
                else
                {
                    listeLotReception_Intent = new Intent(ListeLotReceptionActivity.this, BarcodeReceptionActivity.class);
                }

                listeLotReception_Bundle.putInt("ReceptionID", commandeSelectionne.getID_commande());
                listeLotReception_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                listeLotReception_Bundle.putString("ordreTri", "");
                listeLotReception_Bundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));
                listeLotReception_Bundle.putSerializable("EmplacementPrecedent", (Serializable) emplacement_precedent);
                listeLotReception_Bundle.putSerializable("ProduitPrecedent", (Serializable) produit_precedent);

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

        ((Button) findViewById(R.id.btnAjoutManuel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creationManuelleLot();
            }
        });

        if(numero_serie)
            if(quantiteRestant > 0)
                lancerScanLinearLayout.performClick();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onResume() {
        super.onResume();

        if(!scanProduit)
        {
            quantiteLivree = 0;
            List<PH_Reliquat> reliquatReceptionne = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeSelectionne.getNumero(), phReliquat.getProduitID());
            for (PH_Reliquat reliquatCourant : reliquatReceptionne) {
                quantiteLivree += reliquatCourant.getQteLivraison();
            }

            if(!numero_serie)
                ((Button) findViewById(R.id.btnAjoutManuel)).setVisibility(View.VISIBLE);

            quantiteReliquat = phReliquat.getQteReliquat_X() - phReliquat.getQteLivraison();

            lotReceptionAdapter = new LotReception_Adapter(reliquatReceptionne, position -> {
                PH_Reliquat courantasupprimer = lotReceptionAdapter.getReliquatAt(position);
                if(courantasupprimer != null)
                {
                    PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, courantasupprimer);
                    lotReceptionAdapter.notifyItemRemoved(position);
                    calculQuantiteReception();
                    checkEtatReception();
                    lotReceptionAdapter.notifyItemChanged(position);
                    onResume();
                }
            }, ListeLotReceptionActivity.this);

            recyclerView.setAdapter(lotReceptionAdapter);
            invalidateOptionsMenu();
        }

        calculQuantiteReception();
        checkEtatReception();

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

                                //PH_Reliquat_Reception_Adapte.Lot phReliquatLotReception = lotReceptionAdapter.lotList.get(lotReceptionAdapter.viewHolderList.indexOf(viewHolderAModifier));
                                PH_Reliquat_Reception_Adapte.Lot phReliquatLotReception = null;

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
                    }
                    invalidateOptionsMenu();
                    break;
                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
//                    String code = data.getStringExtra("code");
//                    if (code != null && !code.contentEquals("")) {
//                        int emplacementID = 0;
//                        try {
//                            emplacementID = Integer.parseInt(code);
//                        }catch (Exception ignored){
//
//                        }
//                        if(emplacementID!=0){
//                            Depot_Emplacement emplacementRetourne = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementID);
//                            Depot_Zone zoneEmplacementRetourne = ZoneOpenHelper.getUneZoneByID(db, emplacementRetourne.getZoneID());
//                            PH_Reliquat_Reception_Adapte.Lot lot;
//
//                            /*int index = lotReceptionAdapter.viewHolderList.indexOf(viewHolderAModifier);
//
//                            lot = lotReceptionAdapter.lotList.get(index);*/
//                            List<PH_Reliquat_Reception_Adapte.ZoneEtEmplacement> zoneEtEmplacementList = lot.getZoneEtEmplacementList();
//                            if(zoneEtEmplacementList.size() == 1)
//                            {
//                                PH_Reliquat_Reception_Adapte.ZoneEtEmplacement courant = zoneEtEmplacementList.get(0);
//                                if(courant.getQuantite() == 0)
//                                {
//                                    zoneEtEmplacementList = new ArrayList<>();
//                                    lot.setZoneEtEmplacementList(zoneEtEmplacementList);
//                                }
//                            }
//                            boolean emplacementPresent = false;
//                            for (PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement : zoneEtEmplacementList) {
//                                if (zoneEtEmplacement.getEmplacementId() == emplacementRetourne.get_UID() && zoneEtEmplacement.getZoneId() == zoneEmplacementRetourne.getZoneID()) {
//                                    emplacementPresent = true;
//                                    break;
//                                }
//                            }
//                            if (!emplacementPresent) {
//                                PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionAdapte.new ZoneEtEmplacement(zoneEmplacementRetourne.getZoneID(), zoneEmplacementRetourne.getZoneName(), emplacementRetourne.get_UID(), emplacementRetourne.getAdressage(), quantiteRestant);
//                                lot.getZoneEtEmplacementList().add(zoneEtEmplacement);
//                            }
//                        }
//                        else{
//                            Toast toast = Toast.makeText(ListeLotReception2025_V2Activity.this, "Le code fourni n'est pas un code Emplacement, veuillez réessayer.", Toast.LENGTH_SHORT);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//                        }
//
//                    }
//                    scanProduit = false;
                    break;
                case CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS:
                    PH_Reliquat_Reception_Adapte.Lot phReliquatLotReceptionRecu = (PH_Reliquat_Reception_Adapte.Lot) Objects.requireNonNull(data.getExtras()).getSerializable("lotZoneEtEmplacement");
                    // PH_Reliquat_Reception_Adapte.Lot phReliquatLotReception = phReliquatReceptionAdapte.getlotList().get(lotReceptionAdapter.viewHolderList.indexOf(viewHolderAModifier));
                    assert phReliquatLotReceptionRecu != null;
                    //phReliquatLotReception.setZoneEtEmplacementList(phReliquatLotReceptionRecu.getZoneEtEmplacementList());
                    break;

                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
//                    //on recupere à nouveau le ph_reliquat courant pour capter les changements en bdd
//                    phReliquatReceptionAdapte = null;
//
//                    List<PH_Reliquat_Reception_Adapte> temp_list = new ArrayList<>();
//                    temp_list = (List<PH_Reliquat_Reception_Adapte>) data.getExtras().getSerializable("reliquatAdapteList");
//                    phReliquatReceptionAdapte = temp_list.get(0);
//                    assert phReliquatReceptionAdapte != null;
//                    phReliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionAdapte.getPhReliquatUID());
//
//                    //on remet les lignes d'ajout de lot et d'annulation
//                    if(!produit.isSuivi_Serialisation() || !produit.isSerialiser_Reception_Delivrance())
//                        phReliquatReceptionAdapte.getlotList().add(phReliquatReceptionAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
//                    lotReceptionAdapter.notifyDataSetChanged();
//
//                    //gestion de l'emplacement scanné
//                    emplacement_precedent = (Depot_Emplacement) data.getExtras().getSerializable("EmplacementPrecedent");
//                    produit_precedent = (Produit) data.getExtras().getSerializable("ProduitPrecedent");
//
//                    //on gere le visuel
//                    onMenuSaveClick();
                        onResume();
                    break;
                case RETOUR_LOT:
                    onResume();
//                    lotmanuel = true;
//                    String numLot = Objects.requireNonNull(data.getExtras()).getString("numLot");
//                    String datePeremption = data.getExtras().getString("datePeremption");
//                    double Qte = data.getExtras().getInt("qteActuelle",1);
//                    String Emplacement = data.getExtras().getString("nomEmplacement","");
//                    String Zone = data.getExtras().getString("nomZone","");
//
//                    if(Emplacement.contentEquals("")){
//                        if(depot.getStructure().contentEquals("PUF")){
//                            Emplacement = produit.getEmplacement_UF_Defaut();
//                        }
//                        else if(depot.getStructure().contentEquals("PAD")){
//                            Emplacement = produit.getEmplacement_PAD_Defaut();
//                        }
//                        else{
//                            Emplacement = produit.getEmplacement_PUI_Defaut();
//                        }
//                    }
//                    if(Zone.contentEquals("")){
//                        if(depot.getStructure().contentEquals("PUF")){
//                            Zone = produit.getZone_UF_Defaut();
//                        }
//                        else if(depot.getStructure().contentEquals("PAD")){
//                            Zone = produit.getZone_PAD_Defaut();
//                        }
//                        else{
//                            Zone = produit.getZone_PUI_Defaut();
//                        }
//                    }
//
//                    MAJListeLot();
//
//                    PH_Reliquat_Reception_Adapte.Lot nouveau_lot = null;
//
//                    for(PH_Reliquat_Reception_Adapte.Lot lot : phReliquatReceptionAdapte.getlotList())
//                    {
//                        if(lot.getNumeroLot().contentEquals(numLot) && lot.getDatePeremption().contentEquals(datePeremption))
//                            nouveau_lot = lot;
//                    }
//
//                    boolean ajoutLot = false;
//                    if(nouveau_lot == null)
//                    {
//                        nouveau_lot = phReliquatReceptionAdapte.new Lot(numLot, datePeremption, "", "");
//                        ajoutLot = true;
//                    }
//
//                    PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement = null;
//
//                    for(PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacementCourant : nouveau_lot.getZoneEtEmplacementList())
//                    {
//                        if(zoneEtEmplacementCourant.getEmplacementName().contentEquals(Emplacement) && zoneEtEmplacementCourant.getZoneName().contentEquals(Zone))
//                        {
//                            zoneEtEmplacement = zoneEtEmplacementCourant;
//                            zoneEtEmplacementCourant.setQuantite(zoneEtEmplacement.getQuantite() + (int)Qte);
//                        }
//                    }
//
//                    boolean ajoutZone = false;
//                    if(zoneEtEmplacement == null)
//                    {
//                        zoneEtEmplacement = phReliquatReceptionAdapte.new ZoneEtEmplacement(0, Zone, 0, Emplacement, (int)Qte);
//                        ajoutZone = true;
//                        nouveau_lot = phReliquatReceptionAdapte.new Lot(numLot, datePeremption, "", "");
//                        ajoutLot = true;
//                    }
//
//                    // Insertion dans les objets
//                    if(ajoutZone)
//                        nouveau_lot.getZoneEtEmplacementList().add(zoneEtEmplacement);
//                    if(ajoutLot)
//                        phReliquatReceptionAdapte.getlotList().add(nouveau_lot);
//                    if(!produit.isSuivi_Serialisation() || !produit.isSerialiser_Reception_Delivrance())
//                        phReliquatReceptionAdapte.getlotList().add(phReliquatReceptionAdapte.new Lot("row_ajouter", "00/00/0000", "", ""));
//                    gestionPhReliquatBDD(phReliquatReceptionAdapte.getlotList());
//                    calculQuantiteReception();
//                    checkEtatReception();
//                    lotReceptionAdapter.notifyDataSetChanged();
                    //lotReceptionListView.performItemClick(lotReceptionListView.getAdapter().getView(phReliquatReceptionAdapte.getlotList().size()-3, null, null), phReliquatReceptionAdapte.getlotList().size()-3, lotReceptionListView.getAdapter().getItemId(phReliquatReceptionAdapte.getlotList().size()-3));
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
        boolean quantite_ok = true;
        int quantite_total = 0;
        List<PH_Reliquat> reliquatReceptionne = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, phReliquat.getcommandeNumero(), phReliquat.getProduitID());
        for(PH_Reliquat courant : reliquatReceptionne)
        {
            quantite_total += courant.getQteLivraison();
        }

        if(phReliquat.getQteReliquat_X() < quantite_total)
        {
            quantite_ok = false;
        }

        if(quantite_ok)
        {
            Intent resultIntent = new Intent();
            Bundle extras = ListeLotReceptionActivity.super.getBundle();
            ListeLotReceptionActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS, resultIntent);
            ListeLotReceptionActivity.this.finish();
        }
        else
        {
            Alerte.afficherAlerte(ListeLotReceptionActivity.this, "Alerte", "La quantité saisie est supérieur à la quantité attendu", "alerte");
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void checkEtatReception()
    {
        int qteReceptionne = Integer.parseInt(QtePreparerTextView.getText().toString());
        if(phReliquat.getQteReliquat_X() == qteReceptionne)
        {
            //si c'est le cas on cache les autres lignes
            firstRowLinearLayout.setBackground(ListeLotReceptionActivity.this.getResources().getDrawable(R.drawable.background_cadre_vert, null));
            QteDemandeeTextView.setTextColor(ListeLotReceptionActivity.this.getResources().getColor(R.color.vert, null));
            lancerScanLinearLayout.setVisibility(View.GONE);
            QtePreparerTextView.setVisibility(View.INVISIBLE);
            lotReceptionAdapter.receptionComplete = true;
            ((Button) findViewById(R.id.btnAjoutManuel)).setVisibility(View.INVISIBLE);
        }
        else {
            //si c'est le cas on cache les autres lignes
            firstRowLinearLayout.setBackground(ListeLotReceptionActivity.this.getResources().getDrawable(R.drawable.background_cadre_orange, null));
            lancerScanLinearLayout.setVisibility(View.VISIBLE);
            //QtePreparerTextView.setText(String.valueOf(phReliquat.getQteLivraison()));
            QtePreparerTextView.setVisibility(View.VISIBLE);
            QteDemandeeTextView.setTextColor(ListeLotReceptionActivity.this.getResources().getColor(R.color.noir, null));
            lotReceptionAdapter.receptionComplete = false;

            if(!numero_serie)
                ((Button) findViewById(R.id.btnAjoutManuel)).setVisibility(View.VISIBLE);
        }
    }

    public void ClickNumberPicker(final int position, PH_Reliquat reliquatAModifier)
    {
        if(!produit.isSuivi_Serialisation() || !produit.isSerialiser_Reception_Delivrance())
        {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

            Context context = ListeLotReceptionActivity.this;

            String title = "";
            if (viewHolder != null && viewHolder instanceof LotReception_Adapter.LotViewHolder) {
                title = ((LotReception_Adapter.LotViewHolder) viewHolder).lot.getText().toString();
            }
            String message = "Quantité placée : ";

            int qteAttendu = (int)phReliquat.getQteReliquat_X() - phReliquat.getQteLivraison();
            int maxValue = qteAttendu;
            String emplacementcourant = lotReceptionAdapter.getReliquatAt(position).getEmplacement();
            String lotcourant = lotReceptionAdapter.getReliquatAt(position).getLot();
            List<PH_Reliquat> reliquatDejaReception = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, phReliquat.getcommandeNumero(), phReliquat.getProduitID());

            if(reliquatDejaReception.size() > 1)
            {
                for(PH_Reliquat reliquat : reliquatDejaReception)
                {
                    if(!reliquat.getLot().contentEquals(lotcourant) || (!reliquat.getEmplacement().contentEquals(emplacementcourant) && reliquat.getLot().contentEquals(lotcourant)))
                    {
                        maxValue = maxValue - reliquat.getQteLivraison();
                    }
                }
            }

            DialogInterface.OnClickListener onClickListener = (dialog, id) -> {
                int qteApres = aNumberPicker.getValue()*phReliquat.getConditionnementAchat();
                lotReceptionAdapter.getReliquatAt(position).setQteLivraison(qteApres);

                if (viewHolder != null && viewHolder instanceof LotReception_Adapter.LotViewHolder) {
                    ((LotReception_Adapter.LotViewHolder) viewHolder).qteSaisie.setText(String.valueOf(qteApres));
                }

                modifierReliquatBDD(reliquatAModifier, qteApres);

                calculQuantiteReception();
                checkEtatReception();
                lotReceptionAdapter.notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) ListeLotReceptionActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
            };

            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, maxValue, maxValue, onClickListener, phReliquat.getConditionnementAchat());
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

    private void calculQuantiteReception()
    {
        List<PH_Reliquat> reliquatReceptionne = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeSelectionne.getNumero(), produit.getID_produit());
        int qte_receptionne = 0;
        for(PH_Reliquat ph_reliquat : reliquatReceptionne)
        {
            qte_receptionne += ph_reliquat.getQteLivraison();
        }
        QtePreparerTextView.setText(String.valueOf(qte_receptionne));
    }

    private void creationManuelleLot()
    {
        Bundle clicBoutonAjouterManuellement_Bundle = ListeLotReceptionActivity.super.getBundle();
        clicBoutonAjouterManuellement_Bundle.putInt("produitID", produit.getID_produit());
        clicBoutonAjouterManuellement_Bundle.putInt("depotID", depot.getDepot_UID());
        clicBoutonAjouterManuellement_Bundle.putInt("ReliquatID", phReliquat.getReliquat_UID());

        Intent clicBoutonAjouterManuellement_Intent = new Intent(ListeLotReceptionActivity.this, CreationLotManuelReceptionActivity.class);
        clicBoutonAjouterManuellement_Intent.putExtras(clicBoutonAjouterManuellement_Bundle);
        ListeLotReceptionActivity.this.startActivityForResult(clicBoutonAjouterManuellement_Intent, RETOUR_LOT);
    }

    private void modifierReliquatBDD(PH_Reliquat reliquatAModifier, int quantiteAModifier) {
        reliquatAModifier.setQteLivraison(quantiteAModifier);

        long rowID = PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquatAModifier);
        if (rowID != -1) {

        }
    }
}