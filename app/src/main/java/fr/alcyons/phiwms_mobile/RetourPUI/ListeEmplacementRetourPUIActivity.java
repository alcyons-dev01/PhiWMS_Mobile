package fr.alcyons.phiwms_mobile.RetourPUI;

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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.github.clans.fab.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerEmplacementActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_RetourPUI_Adapte;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Emplacement_RetourPUIAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.PlanDePlacement.ListeZonesActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class ListeEmplacementRetourPUIActivity extends ServiceActivity {
    Retour_Ligne retourLigne;
    Produit produit;
    Depot depot;
    ListView emplacementListView;
    Emplacement_RetourPUIAdapter adapter;
    boolean premierPassageScan;
    int quantiteARetourner = 0;
    int quantiteRestantARetourner = 0;
    int quantiteRetourner = 0;
    boolean lot_ajouter = false;
    PackageManager pm;
    ActivityResultLauncher<Intent> resultListeEmplacementRetour;
    List<Retour_Ligne> retourLigneRetourner;
    Retour retourCourant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_emplacement_retour_pui);

        //gestion du package manager
        pm = ListeEmplacementRetourPUIActivity.this.getPackageManager();

        premierPassageScan = true;
        // Récupération des éléments nécessaires à la consitituion de la liste des emplacement
        retourLigne = (Retour_Ligne) Objects.requireNonNull(intent.getExtras()).getSerializable("retourLigne");
        retourCourant = RetourOpenHelper.getRetourByID(db, retourLigne.getRetour_UID());
        retourLigneRetourner = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourCourant, retourLigne.getCode_produit());

        produit = ProduitOpenHelper.getProduitByID(db, intent.getExtras().getInt("produitID"));
        depot = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotID"));

        quantiteARetourner = (int) retourLigne.getQte_avant_retour();
        quantiteRestantARetourner = (int) retourLigne.getQte_avant_retour();
        quantiteRetourner = 0;
        for(Retour_Ligne retourLigneTemp : retourLigneRetourner)
        {
            quantiteRetourner += retourLigneTemp.getQte_Retourner();
            quantiteRestantARetourner -= retourLigneTemp.getQte_Retourner();
        }

        ((TextView) findViewById(R.id.qteARetourner)).setText(String.valueOf((int) retourLigne.getQte_avant_retour()));
        ((TextView) findViewById(R.id.designationProduit)).setText(produit.getDesignation_interne());
        ((TextView) findViewById(R.id.referenceProduit)).setText(produit.getRef_fourni());
        int nbColis = 0;
        if (produit.getCond_distrib() > 0) {
            nbColis = (int) Math.ceil(retourLigne.getQte_avant_retour() / produit.getCond_distrib());
        }

        ((TextView) findViewById(R.id.colis)).setText(String.valueOf(nbColis));
        majVisuel();
        //Gestion de la listView
        emplacementListView = findViewById(R.id.listeView);
        emplacementListView.setItemsCanFocus(true);
        emplacementListView.setDivider(footer);


        resultListeEmplacementRetour = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null) {
                        switch (result.getResultCode()) {
                           case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                                int emplacementID = Objects.requireNonNull(data.getExtras()).getInt("emplacementSelectionneID");
                                Retour_Ligne ajoutRetourLigneEmplacement = creationRetourLigne(retourLigne, emplacementID);
                                retourLigneRetourner.add(ajoutRetourLigneEmplacement);
                                onResume();
                                emplacementListView.performItemClick(emplacementListView.getAdapter().getView(adapter.viewHolderList.size()-1, null, null), adapter.viewHolderList.size()-1, emplacementListView.getAdapter().getItemId(adapter.viewHolderList.size()-1));
                                break;
                            case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                                String emplacement_scanne = Objects.requireNonNull(data.getExtras()).getString("code");
                                Depot_Emplacement emplacement = null;
                                if(emplacement_scanne != null && !emplacement_scanne.contentEquals(""))
                                {
                                    if(emplacement_scanne.startsWith("PHITAGPLACE"))
                                    {
                                        String[] tab_emplacement = emplacement_scanne.split(":");
                                        emplacement_scanne = tab_emplacement[tab_emplacement.length-1];
                                    }
                                    emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, Integer.parseInt(emplacement_scanne));
                                }
                                if(emplacement != null)
                                {
                                    Retour_Ligne newRetourLigneEmplacement = creationRetourLigne(retourLigne, emplacement.get_UID());
                                    retourLigneRetourner.add(newRetourLigneEmplacement);
                                    onResume();
                                    emplacementListView.performItemClick(emplacementListView.getAdapter().getView(adapter.viewHolderList.size()-1, null, null), adapter.viewHolderList.size()-1, emplacementListView.getAdapter().getItemId(adapter.viewHolderList.size()-1));
                                }
                                break;
                        }
                    }
                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ListeEmplacementRetourPUIActivity.this.finish();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Emplacement_RetourPUIAdapter(ListeEmplacementRetourPUIActivity.this, retourLigneRetourner, db);
        emplacementListView.setAdapter(adapter);
        emplacementListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

        emplacementListView.setOnItemClickListener((parent, view, position, id) -> {
            final Retour_Ligne retourLigne = (Retour_Ligne) adapter.getItem(position);
            //on recalcule les quantité retourner et a retourner
            quantiteRetourner=0;
            quantiteRestantARetourner=quantiteARetourner;

            List<Retour_Ligne> listRetourLigneTemp = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourCourant, retourLigne.getCode_produit());

            for(Retour_Ligne retourLigneTemp : listRetourLigneTemp)
            {
                if(retourLigneTemp.get_UID() != retourLigne.get_UID())
                {
                    quantiteRetourner += retourLigneTemp.getQte_Retourner();
                    quantiteRestantARetourner -= retourLigneTemp.getQte_Retourner();
                }
            }
            // Ouvre une boite de dialogue avec un NumberPicker
            Context context = ListeEmplacementRetourPUIActivity.this;
            String title = retourLigne.getRetourPUI_Zone() + " - " + retourLigne.getRetourPUI_Emplacement();
            String message = "Quantité retournée : ";
            int maxValue = quantiteRestantARetourner;
            int value = quantiteRestantARetourner;

            if (retourLigne.getQte_Retourner() > 0) {
                value = (int) retourLigne.getQte_Retourner();
            }
            DialogInterface.OnClickListener onClickListener = (dialog, id1) -> {

                int qteAvant = (int) retourLigne.getQte_Retourner();
                String[] displayValue = aNumberPicker.getDisplayedValues();
                int qteApres = Integer.parseInt(displayValue[aNumberPicker.getValue()]);
                int result = quantiteRetourner - quantiteARetourner;
                if (result > 0) {
                    qteApres = qteApres - result;
                }

                retourLigne.setQte_Retourner(qteApres);
                Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne);
                adapter.qteRestanteARetourner = quantiteARetourner - quantiteRetourner;
                majVisuel();
                adapter.notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) ListeEmplacementRetourPUIActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                dialog.dismiss();
            };

            int pasNumberPicker = (int) produit.getCond_distrib();

            if(pasNumberPicker == 0 || pasNumberPicker >= maxValue)
            {
                pasNumberPicker = 1;
            }

            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, pasNumberPicker);
        });

        ((Button) findViewById(R.id.btnAjoutManuel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listeEmplacementRetourPuiIntent = new Intent(ListeEmplacementRetourPUIActivity.this, ListeZonesActivity.class);
                Bundle listeEmplacementRetourPuiBundle = ListeEmplacementRetourPUIActivity.super.getBundle();
                listeEmplacementRetourPuiBundle.putInt("depotSelectionneID", depot.getDepot_UID());
                listeEmplacementRetourPuiBundle.putString("designationProduit", retourLigne.getProduit_Designation());
                listeEmplacementRetourPuiIntent.putExtras(listeEmplacementRetourPuiBundle);

                resultListeEmplacementRetour.launch(listeEmplacementRetourPuiIntent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu action et utilisation de l'item ADD
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuAdd).setVisible(false);
        menu.findItem(R.id.menuSaveCircle).setVisible(true);

        if(quantiteRestantARetourner != 0)
        {
            menu.findItem(R.id.menuDatamatrix).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.menuDatamatrix).setVisible(false);
        }

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Récupération de l'item ADD et affectation de l'action à réaliser lors d'un clic
        MenuItem itemdata = menu.findItem(R.id.menuDatamatrix);
        itemdata.setOnMenuItemClickListener(item -> {
            onMenuDatamatrixClick();
            return true;
        });

        MenuItem itemSave = menu.findItem(R.id.menuSaveCircle);
        itemSave.setOnMenuItemClickListener(item -> {

            List<Retour_Ligne> retourLigneRetourner = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourCourant, retourLigne.getCode_produit());

            for(Retour_Ligne retour_ligne_temp : retourLigneRetourner)
            {
                if(retour_ligne_temp.getQte_Retourner() == 0)
                {
                    Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retour_ligne_temp);
                }
            }

            Intent listeEmplacementRetourPuiIntent = new Intent();
            Bundle listeEmplacementRetourPuiBundle = ListeEmplacementRetourPUIActivity.super.getBundle();
            listeEmplacementRetourPuiIntent.putExtras(listeEmplacementRetourPuiBundle);
            ListeEmplacementRetourPUIActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS, listeEmplacementRetourPuiIntent);
            ListeEmplacementRetourPUIActivity.this.finish();

            return true;
        });
        return true;
    }
    private void onMenuDatamatrixClick()
    {
        Intent scanEmplacement_Intent;
        Bundle listeLotPreparation_Bundle = super.getBundle();

        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanEmplacement_Intent = new Intent(ListeEmplacementRetourPUIActivity.this, ScannerEmplacementActivity.class);
            listeLotPreparation_Bundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
            listeLotPreparation_Bundle.putBoolean("activerTextSuppression", true);
            listeLotPreparation_Bundle.putString("TextBannerManuel", "Scannez le datamatrix d'un emplacement");
            listeLotPreparation_Bundle.putString("designationProduit", retourLigne.getProduit_Designation());
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanEmplacement_Intent = new Intent(ListeEmplacementRetourPUIActivity.this, BarcodeCaptureActivity.class);
                listeLotPreparation_Bundle.putBoolean("modeRafale", false);
                listeLotPreparation_Bundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
                listeLotPreparation_Bundle.putString("designationProduit", retourLigne.getProduit_Designation());
            }
            else
            {
                scanEmplacement_Intent = new Intent(ListeEmplacementRetourPUIActivity.this, ScannerEmplacementActivity.class);
                listeLotPreparation_Bundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
                listeLotPreparation_Bundle.putBoolean("activerTextSuppression", true);
                listeLotPreparation_Bundle.putString("TextBannerManuel", "Scannez le datamatrix d'un emplacement");
                listeLotPreparation_Bundle.putString("designationProduit", retourLigne.getProduit_Designation());
            }
        }

        listeLotPreparation_Bundle.putBoolean("doitEtreIdentique", false);
        listeLotPreparation_Bundle.putBoolean("ADH", true);
        listeLotPreparation_Bundle.putBoolean("isBoutonSuppressionExistant", true);
        listeLotPreparation_Bundle.putInt("UserId", utilisateurConnecte.getId());
        listeLotPreparation_Bundle.putBoolean("modeRafale", false);
        scanEmplacement_Intent.putExtras(listeLotPreparation_Bundle);

        resultListeEmplacementRetour.launch(scanEmplacement_Intent);
    }

    private void majVisuel()
    {
        quantiteRetourner = 0;
        quantiteRestantARetourner = (int) retourLigne.getQte_avant_retour();
        retourLigneRetourner = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourCourant, retourLigne.getCode_produit());
        for(Retour_Ligne retourLigneTemp : retourLigneRetourner)
        {
            quantiteRetourner += retourLigneTemp.getQte_Retourner();
            quantiteRestantARetourner -= retourLigneTemp.getQte_Retourner();
        }

        ((TextView) findViewById(R.id.QteRetourner)).setText(String.valueOf(quantiteRetourner));

        if(quantiteRestantARetourner == 0)
        {
            ((Button) findViewById(R.id.btnAjoutManuel)).setVisibility(View.INVISIBLE);
        }
        else
        {
            ((Button) findViewById(R.id.btnAjoutManuel)).setVisibility(View.VISIBLE);
        }

        invalidateOptionsMenu();
    }

    private Retour_Ligne creationRetourLigne(Retour_Ligne retourLigneBase, int emplacementID)
    {
        Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementID);
        Depot_Zone zone = ZoneOpenHelper.getUneZoneByID(db, emplacement.getZoneID());

        Random random = new Random();
        int retourLigneId = random.nextInt();
        if(retourLigneId > 0)
            retourLigneId = retourLigneId*-1;

        Retour_Ligne retourLigneCourant = new Retour_Ligne(retourLigneBase);
        retourLigneCourant.set_UID(retourLigneId);
        retourLigneCourant.setRetourPUI_Zone(zone.getZoneName());
        retourLigneCourant.setRetourPUI_Emplacement(emplacement.getAdressage());
        retourLigneCourant.setQte_Retourner(0);

        long rowID = Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, retourLigneCourant);

        return retourLigneCourant;
    }
}
