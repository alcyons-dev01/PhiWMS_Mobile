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
import android.widget.ListView;
import android.widget.TextView;
import com.github.clans.fab.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerEmplacementActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_RetourPUI_Adapte;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Emplacement_RetourPUIAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.SimpleMultiChoiceModeListener;
import fr.alcyons.phiwms_mobile.PlanDePlacement.ListeZonesActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

/**
 * Created by olivier on 16/04/2024.
 */
public class ListeEmplacementRetourPUIActivity extends ServiceActivity {
    Retour_Ligne retourLigne;
    Retour_Ligne_RetourPUI_Adapte retourLigneSelectionne;
    Produit produit;
    Depot depot;
    ListView emplacementListView;
    List<Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte> emplacementAdapteList;
    Emplacement_RetourPUIAdapter adapter;
    boolean premierPassageScan;
    FloatingActionButton boutonValider;
    FloatingActionButton boutonAjoutEmplacmeent;
    FloatingActionButton boutonSupprimerEmplacement;
    int quantiteARetourner = 0;
    int quantiteRestantARetourner = 0;
    int quantiteRetourner = 0;
    boolean lot_ajouter = false;
    PackageManager pm;
    ActivityResultLauncher<Intent> resultListeEmplacementRetour;

    View.OnClickListener clicBoutonValider = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int sommeQteRetournee = 0;

            Retour_Ligne retourLigne = Retour_LigneOpenHelper.getRetourLigneByID(db, retourLigneSelectionne.getRetourLigneID());

            List<Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte> emplacementsARetirer = new ArrayList<>();

            // On vérifie que chaque emplacement sélectionné a une quantité à retourner sinon on enlève cet emplacement de la liste
            for (Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapte : emplacementAdapteList
                    ) {
                if (emplacementAdapte.getQte() != 0) {
                    sommeQteRetournee += emplacementAdapte.getQte();
                } else {
                    emplacementsARetirer.add(emplacementAdapte);
                }
            }

            // Si la somme des quantités n'est pas bonne, on préviens l'utilisateur
            if (sommeQteRetournee != retourLigne.getQte_avant_retour()) {
                Alerte.afficherAlerte(ListeEmplacementRetourPUIActivity.this, "Alerte", "La somme des quantités n'est pas égale à la quantité à retourner", "alerte");
                return;
            }

            emplacementAdapteList.removeAll(emplacementsARetirer);
            retourLigneSelectionne.setEmplacementAdaptes(emplacementAdapteList);
            Intent listeEmplacementRetourPuiIntent = new Intent();
            Bundle listeEmplacementRetourPuiBundle = ListeEmplacementRetourPUIActivity.super.getBundle();
            listeEmplacementRetourPuiBundle.putSerializable("retourLigneAdapte", retourLigneSelectionne);
            listeEmplacementRetourPuiIntent.putExtras(listeEmplacementRetourPuiBundle);
            ListeEmplacementRetourPUIActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS, listeEmplacementRetourPuiIntent);
            ListeEmplacementRetourPUIActivity.this.finish();
        }
    };
    View.OnClickListener clicBoutonAjoutEmplacement = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent listeEmplacementRetourPuiIntent = new Intent(ListeEmplacementRetourPUIActivity.this, ListeZonesActivity.class);
            Bundle listeEmplacementRetourPuiBundle = ListeEmplacementRetourPUIActivity.super.getBundle();
            listeEmplacementRetourPuiBundle.putInt("depotSelectionneID", depot.getDepot_UID());
            listeEmplacementRetourPuiBundle.putString("designationProduit", retourLigne.getProduit_Designation());
            listeEmplacementRetourPuiIntent.putExtras(listeEmplacementRetourPuiBundle);

            resultListeEmplacementRetour.launch(listeEmplacementRetourPuiIntent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_emplacement_retour_pui);

        //gestion du package manager
        pm = ListeEmplacementRetourPUIActivity.this.getPackageManager();

        premierPassageScan = true;
        // Récupération des éléments nécessaires à la consitituion de la liste des emplacement
        retourLigneSelectionne = (Retour_Ligne_RetourPUI_Adapte) Objects.requireNonNull(intent.getExtras()).getSerializable("retourLigneAdapte");
        assert retourLigneSelectionne != null;
        retourLigne = Retour_LigneOpenHelper.getRetourLigneByID(db, retourLigneSelectionne.getRetourLigneID());
        produit = ProduitOpenHelper.getProduitByID(db, intent.getExtras().getInt("produitID"));
        depot = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotID"));

        quantiteARetourner = (int) retourLigne.getQte_avant_retour();
        quantiteRestantARetourner = (int) retourLigne.getQte_avant_retour();
        quantiteRetourner = 0;

        // Constitution de la liste emplacementAdaptes
        emplacementAdapteList = retourLigneSelectionne.getEmplacementAdaptes();

        if (emplacementAdapteList.isEmpty()) {
            Depot_Zone zone = ZoneOpenHelper.getZoneByDepotEtNom(db, depot, produit.getZone_PUI_Defaut());
            if (zone != null) {
                Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone, produit.getEmplacement_PUI_Defaut());
                if (emplacement != null) {
                    quantiteRetourner = quantiteARetourner;
                    quantiteRestantARetourner = 0;
                    Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapte = retourLigneSelectionne.new EmplacementAdapte(emplacement.get_UID(), 0);
                    emplacementAdapteList.add(emplacementAdapte);
                }
            }
        }
        else if(emplacementAdapteList.size() == 1)
        {
            emplacementAdapteList.get(0).setQte(0);
        }
        else {
            for (Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapte : emplacementAdapteList) {
                quantiteRetourner += emplacementAdapte.getQte();
                quantiteRestantARetourner -= emplacementAdapte.getQte();
            }
        }

        ((TextView) findViewById(R.id.qteRetournee)).setText(String.valueOf((int) retourLigne.getQte_avant_retour()));
        ((TextView) findViewById(R.id.designationProduit)).setText(produit.getDesignation_interne());

        // Récupération et affectation des bouton
        boutonValider = findViewById(R.id.boutonValider);
        boutonAjoutEmplacmeent = findViewById(R.id.boutonAjoutEmplacmeent);
        boutonSupprimerEmplacement = findViewById(R.id.boutonSupprimerEmplacement);
        boutonValider.setOnClickListener(clicBoutonValider);
        boutonAjoutEmplacmeent.setOnClickListener(clicBoutonAjoutEmplacement);

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
                                Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapte = retourLigneSelectionne.new EmplacementAdapte(Objects.requireNonNull(data.getExtras()).getInt("emplacementSelectionneID"), 0);
                                boolean emplacementExistant = false;
                                for (Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapteCourant : adapter.emplacementAdapteList) {
                                    if (emplacementAdapteCourant.getEmplacementID() == emplacementAdapte.getEmplacementID()) {
                                        emplacementExistant = true;
                                        break;
                                    }
                                }
                                if (!emplacementExistant) {
                                    lot_ajouter = true;
                                    adapter.emplacementAdapteList.add(emplacementAdapte);
                                    adapter.viewHolderList.add(adapter.new EmplacementViewHolder());
                                    adapter.notifyDataSetChanged();
                                    emplacementListView.performItemClick(emplacementListView.getAdapter().getView(adapter.viewHolderList.size()-1, null, null), adapter.viewHolderList.size()-1, emplacementListView.getAdapter().getItemId(adapter.viewHolderList.size()-1));
                                    InputMethodManager imm = (InputMethodManager) ListeEmplacementRetourPUIActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                }
                                break;
                            case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                                String emplacement_scanne = Objects.requireNonNull(data.getExtras()).getString("code");
                                Depot_Emplacement emplacement = null;
                                if(emplacement_scanne != null && !emplacement_scanne.contentEquals(""))
                                {
                                    if(emplacement_scanne.startsWith("PHITAGPLACE+"))
                                    {
                                        String[] tab_emplacement = emplacement_scanne.split(":");
                                        emplacement_scanne = tab_emplacement[tab_emplacement.length-1];
                                    }
                                    emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, Integer.parseInt(emplacement_scanne));
                                }
                                if(emplacement != null)
                                {
                                    Depot_Zone zone_concernee = ZoneOpenHelper.getUneZoneByID(db, emplacement.getZoneID());
                                    if(zone_concernee.getDepotID() != depot.getDepot_UID())
                                    {
                                        Alerte.afficherAlerte(ListeEmplacementRetourPUIActivity.this, "Attention", "L'emplacement scannée n'appartient pas à la PUI", "alerte");
                                        premierPassageScan = true;
                                        onResume();
                                        break;
                                    }
                                    Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapteScan = retourLigneSelectionne.new EmplacementAdapte(emplacement.get_UID(), 0);
                                    boolean emplacementExistantScan = false;
                                    for (Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapteCourant : adapter.emplacementAdapteList) {
                                        if (emplacementAdapteCourant.getEmplacementID() == emplacementAdapteScan.getEmplacementID()) {
                                            emplacementExistantScan = true;
                                            break;
                                        }
                                    }
                                    if (!emplacementExistantScan) {
                                        adapter.emplacementAdapteList.add(emplacementAdapteScan);
                                        premierPassageScan = true;
                                        adapter.notifyDataSetChanged();
                                        lot_ajouter = true;
                                    }
                                }
                                else
                                {
                                    boutonAjoutEmplacmeent.performClick();
                                }
                                break;
                        }
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Emplacement_RetourPUIAdapter(ListeEmplacementRetourPUIActivity.this, emplacementAdapteList, db, retourLigneSelectionne);
        emplacementListView.setAdapter(adapter);
        emplacementListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        final SimpleMultiChoiceModeListener cml = new SimpleMultiChoiceModeListener(ListeEmplacementRetourPUIActivity.this, adapter, boutonAjoutEmplacmeent, boutonSupprimerEmplacement);
        emplacementListView.setMultiChoiceModeListener(cml);
        emplacementListView.setOnItemLongClickListener((adapterView, view, position, l) -> {
            boutonAjoutEmplacmeent.setVisibility(View.GONE);
            boutonSupprimerEmplacement.setVisibility(View.VISIBLE);
            view.setActivated(true);
            ((ListView) view).setItemChecked(position, !((Emplacement_RetourPUIAdapter) adapterView.getAdapter()).isPositionChecked(position));
            return false;
        });

        emplacementListView.setOnItemClickListener((parent, view, position, id) -> {
            final Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapte = (Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte) adapter.getItem(position);
            //on recalcule les quantité retourner et a retourner
            quantiteRetourner=0;
            quantiteRestantARetourner=quantiteARetourner;
            for(Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapteCourant : emplacementAdapteList)
            {
                quantiteRetourner += emplacementAdapteCourant.getQte();
                quantiteRestantARetourner -= emplacementAdapteCourant.getQte();
            }
            assert emplacementAdapte != null;
            Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementAdapte.getEmplacementID());
            Depot_Zone zone = ZoneOpenHelper.getUneZoneByID(db, emplacement.getZoneID());

            // Ouvre une boite de dialogue avec un NumberPicker
            Context context = ListeEmplacementRetourPUIActivity.this;
            String title = zone.getZoneName() + " - " + emplacement.getAdressage();
            String message = "Quantité retournée : ";
            int maxValue = quantiteARetourner;
            int value = quantiteRestantARetourner;

            if (emplacementAdapte.getQte() > 0) {
                value = emplacementAdapte.getQte();
            }
            DialogInterface.OnClickListener onClickListener = (dialog, id1) -> {

                int qteAvant = emplacementAdapte.getQte();
                String[] displayValue = aNumberPicker.getDisplayedValues();
                int qteApres = Integer.parseInt(displayValue[aNumberPicker.getValue()]);
                int difference;
                difference = qteApres - qteAvant;
                quantiteRetourner = quantiteRetourner + difference;
                int result = quantiteRetourner - quantiteARetourner;
                if (result > 0) {
                    qteApres = qteApres - result;
                }

                emplacementAdapte.setQte(qteApres);
                adapter.qteRestanteARetourner = quantiteARetourner - quantiteRetourner;
                adapter.notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) ListeEmplacementRetourPUIActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                dialog.dismiss();

                if(quantiteRetourner == quantiteARetourner)
                {
                    boutonAjoutEmplacmeent.setVisibility(View.GONE);
                    boutonValider.performClick();
                }
                else
                {
                    boutonAjoutEmplacmeent.setVisibility(View.VISIBLE);
                }
            };

            Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, (int)produit.getCond_distrib());
        });

        if(emplacementAdapteList.isEmpty() && premierPassageScan)
        {
            premierPassageScan = false;
            onMenuDatamatrixClick();
        }


        if(emplacementAdapteList.size() == 1 && !lot_ajouter)
        {
            emplacementListView.performItemClick(emplacementListView.getAdapter().getView(0, null, null), 0, emplacementListView.getAdapter().getItemId(0));
            InputMethodManager imm = (InputMethodManager) ListeEmplacementRetourPUIActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu action et utilisation de l'item ADD
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuAdd).setVisible(false);

        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
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
        return true;
    }
    private void onMenuDatamatrixClick()
    {
        Intent scanEmplacement_Intent;
        Bundle listeLotPreparation_Bundle = super.getBundle();

        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListeEmplacementRetourPUIActivity.this.finish();
    }
}
