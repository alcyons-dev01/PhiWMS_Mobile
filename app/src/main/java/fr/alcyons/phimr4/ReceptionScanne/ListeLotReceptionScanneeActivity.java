package fr.alcyons.phimr4.ReceptionScanne;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
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

/**
 * Created by olivier on 04/12/2017.
 */

public class ListeLotReceptionScanneeActivity extends ServiceActivity {

    public ListView lotReceptionPuiListView;
    Commande commandeSelectionne;
    PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte;
    Lot_Reception_PUIAdapter lotReceptionPuiAdapter;

    TextView qteReliquat;
    FloatingActionButton boutonEnregistrer;

    EditText numLotEditText;
    TextView datePeremptionTextView;
    Lot_Reception_PUIAdapter.Lot_Reception_PUIViewHolder viewHolderAModifier;

    Depot depot;

    PH_Reliquat phReliquat;
    Produit produit;
    boolean scanProduit;
    boolean premierPassage = true;


    int quantiteReliquat = 0;
    int quantiteRestant = 0;
    int quantiteLivree = 0;

    boolean numero_serie;
    List<String> GTIN_Scanne = new ArrayList<>();
    LinkedHashMap<String, String> MapLotSerieScan = new LinkedHashMap<>();

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lot_reception_pui);

        //gestion du package manager
        pm = ListeLotReceptionScanneeActivity.this.getPackageManager();

        depot = gestionnaireDepot.getPUICourant(db);
        scanProduit = false;
        commandeSelectionne = gestionnaireCommande.getCommandeByID(db, intent.getExtras().getInt("commandeID_Selectionne"));
        phReliquatReceptionPUIAdapte = (PH_Reliquat_ReceptionPUI_Adapte) intent.getExtras().getSerializable("phReliquatReceptionPUIAdapte");

        if (phReliquatReceptionPUIAdapte != null) {

            //On initialise la liste
            lotReceptionPuiListView = (ListView) findViewById(R.id.liste_view_reception);

            phReliquat = gestionnairePH_Reliquat.getPH_ReliquatById(db, phReliquatReceptionPUIAdapte.getPhReliquatUID());
            produit = gestionnaireProduit.getProduitByID(db, phReliquat.getProduitID());

            if (phReliquat != null) {
                //Entête
                ((TextView) findViewById(R.id.designationProduit)).setText(phReliquat.getdesignationCourte().trim());

                qteReliquat = (TextView) findViewById(R.id.qteReliquat);
                quantiteReliquat = phReliquat.getQteCommande() - phReliquat.getQteLivraison();
                qteReliquat.setText(String.valueOf(quantiteReliquat));

                boutonEnregistrer = (FloatingActionButton) findViewById(R.id.boutonEnregistrer);
                boutonEnregistrer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean manqueLot = false;
                        boolean quantite_ok = true;
                        int quantite_total = 0;
                        boolean result_alerte = Alerte.afficherAlerte(ListeLotReceptionScanneeActivity.this, "Validation", "Voulez-vous valider les lots saisis ? ", "OuiNon");
                        if (result_alerte) {
                            //On vérifie qu'il y a au moins un lot avec une quantité saisie
                            if (phReliquatReceptionPUIAdapte.getlotList().size() == 0) {
                                Alerte.afficherAlerte(ListeLotReceptionScanneeActivity.this, "Alerte", "Veuillez saisir au moins un lot s'il vous plaît", "alerte");

                            } else {
                                for (PH_Reliquat_ReceptionPUI_Adapte.Lot phReliquatLot : phReliquatReceptionPUIAdapte.getlotList()) {
                                    if (phReliquatLot.getNumeroLot().contentEquals("")) {
                                        Alerte.afficherAlerte(ListeLotReceptionScanneeActivity.this, "Alerte", "Veuillez saisir ou scanner un numéro de lot s'il vous plaît", "alerte");
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

                            if(quantiteReliquat < quantite_total)
                            {
                                quantite_ok = false;
                            }

                            if(quantite_ok)
                            {
                                if (manqueLot == false) {
                                    Intent resultIntent = new Intent();
                                    Bundle extras = ListeLotReceptionScanneeActivity.super.getBundle();
                                    extras.putSerializable("lotList", (Serializable) phReliquatReceptionPUIAdapte.getlotList());
                                    resultIntent.putExtras(extras);
                                    ListeLotReceptionScanneeActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS, resultIntent);
                                    ListeLotReceptionScanneeActivity.this.finish();
                                }
                            }
                            else
                            {
                                Alerte.afficherAlerte(ListeLotReceptionScanneeActivity.this, "Alerte", "La quantité saisie est supérieur à la quantité attendu", "alerte");
                            }

                        }
                    }
                });
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
        } else {
            ListeLotReceptionScanneeActivity.this.finish();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(!scanProduit)
        {
            quantiteReliquat = phReliquat.getQteCommande() - phReliquat.getQteLivraison();
            qteReliquat.setText(String.valueOf(quantiteReliquat));
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

            quantiteRestant = quantiteReliquat - quantiteLivree;

            lotReceptionPuiAdapter = new Lot_Reception_PUIAdapter(ListeLotReceptionScanneeActivity.this, db, phReliquatReceptionPUIAdapte.getlotList(), phReliquatReceptionPUIAdapte.getPhReliquatUID(), numero_serie, true);
            lotReceptionPuiListView.setDivider(footer);
            lotReceptionPuiListView.setAdapter(lotReceptionPuiAdapter);
            invalidateOptionsMenu();
        }

        if(premierPassage)
        {
            premierPassage = false;
            onMenuDatamatrixAdd();
        }
    }

    // Permet de supprimer un élément de la liste
    public void supprimerLot(Lot_Reception_PUIAdapter.Lot_Reception_PUIViewHolder viewHolder) {
        viewHolderAModifier = viewHolder;
        int indexASupprimer = lotReceptionPuiAdapter.viewHolderList.indexOf(viewHolderAModifier);
        phReliquatReceptionPUIAdapte.getlotList().remove(indexASupprimer);
        GTIN_Scanne.remove(indexASupprimer);
        ArrayList listeCle = new ArrayList(MapLotSerieScan.keySet());
        String cle = listeCle.get(indexASupprimer).toString();
        MapLotSerieScan.remove(cle);
        onResume();
    }

    // Permet de lancer l'activity BarcodeCaptureActivity afin de lire un codebarre
    public void decoderCodeBarre(EditText numLotEditText, TextView datePeremptionTextView, Lot_Reception_PUIAdapter.Lot_Reception_PUIViewHolder viewHolder, String contexte) {
        this.numLotEditText = numLotEditText;
        this.datePeremptionTextView = datePeremptionTextView;
        viewHolderAModifier = viewHolder;

        Intent listeLotReceptionPui_Intent = new Intent(ListeLotReceptionScanneeActivity.this, BarcodeCaptureActivity.class);
        Bundle listeLotReceptionPui_Bundle = super.getBundle();
        if (contexte.contentEquals("emplacement")) {
            listeLotReceptionPui_Bundle.putString("bannerText", "Scanner un emplacement");
            listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacementReceptionScannee));
            listeLotReceptionPui_Bundle.putBoolean("isBoutonSuppressionExistant", true);
            listeLotReceptionPui_Bundle.putString("ProduitSelect", phReliquat.getDesignationCourte());
            listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
            ListeLotReceptionScanneeActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
        } else {
            String designation = phReliquat.getdesignationCourte();
            Produit produit = ProduitOpenHelper.getProduitByID(db, phReliquat.getProduitID());
            listeLotReceptionPui_Bundle.putBoolean("doitEtreIdentique", true);
            listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContexteProduitReceptionScannee));
            listeLotReceptionPui_Bundle.putString("Designation", designation);
            listeLotReceptionPui_Bundle.putBoolean("isBoutonSuppressionExistant", true);
            listeLotReceptionPui_Bundle.putInt("UserId", utilisateurConnecte.getId());
            listeLotReceptionPui_Bundle.putInt("qteReliquat", quantiteReliquat);
            listeLotReceptionPui_Bundle.putString("ProduitSelect", phReliquat.getDesignationCourte());
            listeLotReceptionPui_Bundle.putInt("reliquat_uid", phReliquat.getReliquat_UID());
            listeLotReceptionPui_Bundle.putBoolean("modeRafale", true);
            listeLotReceptionPui_Bundle.putInt("nbProdAReceptionner", phReliquat.getQteCommande());
            listeLotReceptionPui_Bundle.putInt("ConditionnementProduit", phReliquat.getConditionnementAchat());
            listeLotReceptionPui_Bundle.putString("GTIN_courant", produit.getGTIN());

            listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
            ListeLotReceptionScanneeActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
        }


    }

    // Permet de lancer l'activity ZoneEtEmplacementSelecteurMultiple
    public void lancerListeZoneEmplacement(Lot_Reception_PUIAdapter.Lot_Reception_PUIViewHolder viewHolder) {
        viewHolderAModifier = viewHolder;
        PH_Reliquat_ReceptionPUI_Adapte.Lot lot = lotReceptionPuiAdapter.lotList.get(lotReceptionPuiAdapter.viewHolderList.indexOf(viewHolderAModifier));
        Intent listeLotReceptionPui_Intent = new Intent(ListeLotReceptionScanneeActivity.this, ZoneEtEmplacementReceptionScanneeSelecteurMultipleActivity.class);
        Bundle listeLotReceptionPui_Bundle = super.getBundle();
        listeLotReceptionPui_Bundle.putInt("phReliquatUIDSelectionne", phReliquatReceptionPUIAdapte.getPhReliquatUID());
        listeLotReceptionPui_Bundle.putInt("quantiteReliquat", quantiteReliquat);
        listeLotReceptionPui_Bundle.putInt("quantiteRestant", quantiteRestant);
        listeLotReceptionPui_Bundle.putInt("quantiteLivree", quantiteLivree);
        listeLotReceptionPui_Bundle.putSerializable("lot", lot);
        listeLotReceptionPui_Bundle.putSerializable("phReliquatReceptionPUIAdapte", phReliquatReceptionPUIAdapte);
        listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
        ListeLotReceptionScanneeActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS);
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
                                    scanProduit = true;
                                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                                    {
                                        decoderCodeBarre(null, null, viewHolderAModifier, "emplacement");
                                    }
                                }
                            }
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
                            Toast toast = Toast.makeText(ListeLotReceptionScanneeActivity.this, "Le code fourni n'est pas un code Emplacement, veuillez réessayer.", Toast.LENGTH_SHORT);
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
                        Map<String, String> liste_scanner = (Map<String, String>) data.getExtras().getSerializable("listeString");
                        if(liste_scanner.size() != 0)
                        {
                            GTIN_Scanne = new ArrayList<>();
                            MapLotSerieScan = new LinkedHashMap<>();
                            checkLotVide();
                            for(String code_courant : liste_scanner.keySet())
                            {
                                Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code_courant);

                                if (gs1Decoupe.size() != 0) {

                                    //on ajoute le GTIN dans une liste pour ne pas le rescanner ensuite
                                    GTIN_Scanne.add(code_courant);

                                    DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                    DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                    Date date = new Date();

                                    try {
                                        date = dateFormat1.parse(gs1Decoupe.get(OutilsDecodage.dateDePeremption));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    String dateFinale = dateFormat2.format(date);

                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
                                    Date dateFournie = null;
                                    try {
                                        dateFournie = dateDecodeur.parse(dateFinale);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String numSerie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                                    String last_charactere = numSerie.substring(numSerie.length()-1);
                                    if(last_charactere.contentEquals("@"))
                                    {
                                        numSerie = numSerie.substring(0, numSerie.length()-1);
                                    }

                                    String numLot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                                    last_charactere = numLot.substring(numLot.length()-1);
                                    if(last_charactere.contentEquals("@"))
                                    {
                                        numLot = numLot.substring(0, numLot.length()-1);
                                    }

                                    //on stock les lots et serie déjà scannee
                                    MapLotSerieScan.put(numSerie, numLot);

                                    PH_Reliquat_ReceptionPUI_Adapte.Lot nouveau_lot = phReliquatReceptionPUIAdapte.new Lot(numLot, dateFormat.format(dateFournie), numSerie, liste_scanner.get(code_courant));
                                    PH_Reliquat ph_reliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionPUIAdapte.getPhReliquatUID());
                                    Produit produit = ProduitOpenHelper.getProduitByID(db, ph_reliquat.getProduitID());
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
                                    PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionPUIAdapte.new ZoneEtEmplacement(zone.getZoneID(), zone.getZoneName(), emplacement.get_UID(), emplacement.getAdressage(), phReliquat.getConditionnementAchat());
                                    liste_zone_emplacement.add(zoneEtEmplacement);
                                    nouveau_lot.setZoneEtEmplacementList(liste_zone_emplacement);

                                    phReliquatReceptionPUIAdapte.getlotList().add(nouveau_lot);

                                    lotReceptionPuiAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_datamatrix_add, menu);
        inflater.inflate(R.menu.menu_keyboard_add, menu);
        if(produit.isSuivi_Lot())
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                menu.findItem(R.id.datamtrix_add).setVisible(true);
            }
            else
            {
                menu.findItem(R.id.datamtrix_add).setVisible(false);
            }
        }
        else
        {
            menu.findItem(R.id.datamtrix_add).setVisible(false);
        }
        menu.findItem(R.id.keyboard_add).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.keyboard_add);
        MenuItem item_datamatrix = menu.findItem(R.id.datamtrix_add);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuAddClick();
                return true;
            }
        });

        item_datamatrix.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuDatamatrixAdd();
                return true;
            }
        });

        return true;
    }

    private void onMenuDatamatrixAdd()
    {
        Intent listeLotReceptionPui_Intent = new Intent(ListeLotReceptionScanneeActivity.this, BarcodeCaptureActivity.class);
        Bundle listeLotReceptionPui_Bundle = super.getBundle();
        String designation = phReliquat.getdesignationCourte();
        Produit produit = ProduitOpenHelper.getProduitByID(db, phReliquat.getProduitID());
        listeLotReceptionPui_Bundle.putBoolean("doitEtreIdentique", true);
        listeLotReceptionPui_Bundle.putString("contexte", String.valueOf(R.string.scannerContexteProduitReceptionScannee));
        listeLotReceptionPui_Bundle.putString("Designation", designation);
        listeLotReceptionPui_Bundle.putBoolean("isBoutonSuppressionExistant", true);
        listeLotReceptionPui_Bundle.putInt("UserId", utilisateurConnecte.getId());
        listeLotReceptionPui_Bundle.putInt("qteReliquat", quantiteReliquat);
        listeLotReceptionPui_Bundle.putString("ProduitSelect", phReliquat.getDesignationCourte());
        listeLotReceptionPui_Bundle.putInt("reliquat_uid", phReliquat.getReliquat_UID());
        listeLotReceptionPui_Bundle.putBoolean("modeRafale", true);
        listeLotReceptionPui_Bundle.putInt("nbProdAReceptionner", phReliquat.getQteCommande());
        listeLotReceptionPui_Bundle.putInt("ConditionnementProduit", phReliquat.getConditionnementAchat());
        listeLotReceptionPui_Bundle.putString("GTIN_courant", produit.getGTIN());
        listeLotReceptionPui_Bundle.putStringArrayList("Liste_GTIN_Scannee", (ArrayList<String>) GTIN_Scanne);
        listeLotReceptionPui_Bundle.putSerializable("MapSerieLot_Scannee", (Serializable) MapLotSerieScan);

        listeLotReceptionPui_Intent.putExtras(listeLotReceptionPui_Bundle);
        ListeLotReceptionScanneeActivity.this.startActivityForResult(listeLotReceptionPui_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }

    private void onMenuAddClick() {
        int depotZoneID = 0;
        String depotZoneName = "";
        int depotEmplacementID = 0;
        String depotEmplacementAdressage = "";
        PH_Reliquat_ReceptionPUI_Adapte.Lot lot = null;
        if(produit.isSuivi_Lot())
        {
            lot = phReliquatReceptionPUIAdapte.new Lot("", "00/00/0000", "", "");
        }
        else
        {
            DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
            Date date = new Date();
            String new_date = dateFormat.format(date);
            String numLot = "Phi"+new_date;
            lot = phReliquatReceptionPUIAdapte.new Lot(numLot, "00/00/0000", "", "");
        }

        Depot_Zone depotZone = gestionnaireZone.getZoneByDepotEtNom(db, depot, produit.getZone_PUI_Defaut());
        Depot_Emplacement depotEmplacement = gestionnaireEmplacement.getUnEmplacementZoneEtNom(db, depotZone, produit.getEmplacement_PUI_Defaut());

        if (depotZone != null) {
            depotZoneID = depotZone.getZoneID();
            depotZoneName = depotZone.getZoneName();
        }

        if (depotEmplacement != null) {
            depotEmplacementID = depotEmplacement.get_UID();
            depotEmplacementAdressage = depotEmplacement.getAdressage();
        }

        PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = phReliquatReceptionPUIAdapte.new ZoneEtEmplacement(depotZoneID, depotZoneName, depotEmplacementID, depotEmplacementAdressage, 0);

        lot.getZoneEtEmplacementList().add(zoneEtEmplacement);
        phReliquatReceptionPUIAdapte.getlotList().add(lot);
        onResume();
    }

    private void checkLotVide()
    {
        List<PH_Reliquat_ReceptionPUI_Adapte.Lot> list_temp =  phReliquatReceptionPUIAdapte.getlotList();
        for(int i = 0; i < list_temp.size(); i++)
        {
            List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement> listZone = phReliquatReceptionPUIAdapte.getlotList().get(i).getZoneEtEmplacementList();
            if(listZone.size() == 0)
            {
                phReliquatReceptionPUIAdapte.getlotList().remove(i);
            }
            else
            {
                int quantite = 0;
                for(int j = 0; j < listZone.size(); j++)
                {
                    quantite = quantite + listZone.get(j).getQuantite();
                }

                if(quantite==0)
                {
                    phReliquatReceptionPUIAdapte.getlotList().remove(i);
                }
            }
        }
    }
}