package fr.alcyons.phimr4.ReceptionScanne;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.Outils.Alerte.aNumberPicker;

/**
 * Created by olivier on 06/05/2019.
 */

public class ZoneEtEmplacementReceptionScanneeSelecteurMultipleActivity extends ServiceActivity {

    Map<Depot_Zone, List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement>> ParentListItems;
    List<Depot_Zone> ParentList;

    ExpandableListView expandablelistView;
    ZonetEtEmplacementReceptionScanneeExpandableAdapter expListAdapter;

    List<Depot_Zone> depotZoneList;
    List<Depot_Emplacement> depotEmplacementList;
    List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement> zoneEtEmplacementList;
    List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement> lotZoneEtEmplacementList;

    PH_Reliquat phReliquat;
    PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte;
    PH_Reliquat_ReceptionPUI_Adapte.Lot lot;

    FloatingActionButton boutonValider;

    int quantiteReliquat = 0;
    int quantiteRestant = 0;
    int quantiteLivree = 0;

    View.OnClickListener clicBoutonValider = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            for (Map.Entry<Depot_Zone, List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement>> entry : ParentListItems.entrySet()) {

                List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement> emplacementList = entry.getValue();

                for (int j = 0; j < emplacementList.size(); j++) {

                    boolean present = false;

                    PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = emplacementList.get(j);

                    for (int i = 0; i < lotZoneEtEmplacementList.size(); i++) {

                        PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement lotZoneEtEmplacement = lotZoneEtEmplacementList.get(i);

                        if (zoneEtEmplacement.getZoneId() == lotZoneEtEmplacement.getZoneId() && zoneEtEmplacement.getEmplacementId() == lotZoneEtEmplacement.getEmplacementId()) {
                            present = true;

                            if (zoneEtEmplacement.getQuantite() == 0) {
                                lotZoneEtEmplacementList.remove(i);
                            } else {
                                lotZoneEtEmplacement.setQuantite(zoneEtEmplacement.getQuantite());
                            }
                        }
                    }

                    if (zoneEtEmplacement.getQuantite() > 0 && present == false) {
                        lotZoneEtEmplacementList.add(zoneEtEmplacement);
                    }
                }
            }
            lot.setZoneEtEmplacementList(lotZoneEtEmplacementList);

            for (int i = 0; i < lotZoneEtEmplacementList.size(); i++) {
                PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement lotZoneEtEmplacement = lotZoneEtEmplacementList.get(i);
                if (lotZoneEtEmplacement.getQuantite() == 0) {
                    lotZoneEtEmplacementList.remove(i);
                }
            }


            Intent resultIntent = new Intent();
            Bundle extras = ZoneEtEmplacementReceptionScanneeSelecteurMultipleActivity.super.getBundle();
            extras.putSerializable("lotZoneEtEmplacement", (Serializable) lot);
            resultIntent.putExtras(extras);
            ZoneEtEmplacementReceptionScanneeSelecteurMultipleActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS, resultIntent);
            ZoneEtEmplacementReceptionScanneeSelecteurMultipleActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoneetemplacement_selecteur_multiple_recepetion_scannee);


        phReliquatReceptionPUIAdapte = (PH_Reliquat_ReceptionPUI_Adapte) intent.getExtras().getSerializable("phReliquatReceptionPUIAdapte");
        lot = (PH_Reliquat_ReceptionPUI_Adapte.Lot) intent.getExtras().getSerializable("lot");
        lotZoneEtEmplacementList = lot.getZoneEtEmplacementList();


        //Entete et bas de page
        phReliquat = gestionnairePH_Reliquat.getPH_ReliquatById(db, intent.getExtras().getInt("phReliquatUIDSelectionne"));
        ((TextView) findViewById(R.id.designationProduit)).setText(phReliquat.getdesignationCourte());

        quantiteReliquat = intent.getExtras().getInt("quantiteReliquat");
        ((TextView) findViewById(R.id.qteReliquat)).setText(String.valueOf(quantiteReliquat));
        quantiteRestant = intent.getExtras().getInt("quantiteRestant");
        quantiteLivree = intent.getExtras().getInt("quantiteLivree");


        //initialisation Bouton Valider
        boutonValider = (FloatingActionButton) findViewById(R.id.boutonSave);
        boutonValider.setOnClickListener(clicBoutonValider);

        //on note le nombre d'emplacement disponnible
        Depot depot = gestionnaireDepot.getPUICourant(db);
        depotZoneList = new ArrayList<>();
        depotZoneList = gestionnaireZone.getZonesEtEmplacementsParDepot(db, depot);

        ParentList = new ArrayList<>();
        ParentListItems = new LinkedHashMap<>();

        depotEmplacementList = new ArrayList<>();

        int nbEmplacement = 0;
        for (Depot_Zone zoneCourant : depotZoneList) {
            ParentList.add(zoneCourant);

            depotEmplacementList = gestionnaireEmplacement.getEmplacementsParZoneID(db, zoneCourant.getZoneID());

            zoneEtEmplacementList = new ArrayList<>();

            for (Depot_Emplacement emplacementCourant : depotEmplacementList) {

                PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacementCourant = phReliquatReceptionPUIAdapte.new ZoneEtEmplacement(zoneCourant.getZoneID(), zoneCourant.getZoneName(), emplacementCourant.get_UID(), emplacementCourant.getAdressage(), 0);

                for (PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement : lotZoneEtEmplacementList) {
                    if (zoneCourant.getZoneID() == zoneEtEmplacement.getZoneId() && emplacementCourant.get_UID() == zoneEtEmplacement.getEmplacementId()) {
                        zoneEtEmplacementCourant.setQuantite(zoneEtEmplacement.getQuantite());
                    }
                }
                zoneEtEmplacementList.add(zoneEtEmplacementCourant);
            }

            nbEmplacement += zoneEtEmplacementList.size();
            ParentListItems.put(zoneCourant, zoneEtEmplacementList);

        }

        //on note le nombre d'emplacement disponnible
        ((TextView) findViewById(R.id.nbZone)).setText(String.valueOf(nbEmplacement));


        expandablelistView = (ExpandableListView) findViewById(R.id.expandableListView_ZoneReceptionScannee);

        expListAdapter = new ZonetEtEmplacementReceptionScanneeExpandableAdapter(ZoneEtEmplacementReceptionScanneeSelecteurMultipleActivity.this, ParentList, ParentListItems, quantiteLivree, quantiteReliquat);

        expandablelistView.setAdapter(expListAdapter);

        expandablelistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                final PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = (PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement) expListAdapter.getChild(groupPosition, childPosition);

                // Ouvre une boite de dialogue avec un NumberPicker
                Context context = ZoneEtEmplacementReceptionScanneeSelecteurMultipleActivity.this;
                String title = zoneEtEmplacement.getZoneName() + " - " + zoneEtEmplacement.getEmplacementName();
                String message = "Quantité placée : ";
                int maxValue = quantiteReliquat;
                int value = quantiteRestant;
                if (zoneEtEmplacement.getQuantite() > 0) {
                    value = zoneEtEmplacement.getQuantite();
                }
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int qteAvant = zoneEtEmplacement.getQuantite();
                        int qteAprès = aNumberPicker.getValue();
                        int difference = 0;
                        difference = qteAprès - qteAvant;
                        quantiteLivree = quantiteLivree + difference;
                        int result = quantiteLivree - quantiteReliquat;
                        if (result > 0) {
                            qteAprès = qteAprès - result;
                        } else {
                            result = 0;
                        }
                        quantiteLivree = quantiteLivree - result;
                        quantiteRestant = quantiteReliquat - quantiteLivree;
                        zoneEtEmplacement.setQuantite(qteAprès);
                        expListAdapter.notifyDataSetChanged();
                        InputMethodManager imm = (InputMethodManager) ZoneEtEmplacementReceptionScanneeSelecteurMultipleActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();

                    }
                };

                Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

                return false;
            }
        });

        // Ouvre les zones sélectionnées précèdent
        if (ParentList.size() > 0) {
            for (int i = 0; i < lotZoneEtEmplacementList.size(); i++) {
                for (int j = 0; j < ParentList.size(); j++) {
                    if (ParentList.get(j).getZoneName().equals(lotZoneEtEmplacementList.get(i).getZoneName())) {
                        expandablelistView.expandGroup(j);
                    }
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_deconnexion, menu);

        menu.findItem(R.id.deconnexion).setVisible(false);
        menu.findItem(R.id.deleteMenu).setVisible(false);
        menu.findItem(R.id.rechercheMenu).setVisible(true);
        return true;
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem searchMenuItem = menu.findItem(R.id.rechercheMenu);

        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.rechercheMenu));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        expListAdapter.filter(newText);

                        //display the list
                        expandablelistView.setAdapter(expListAdapter);
                        //expand all Groups
                        expandAll();
                    }
                });
                return false;
            }
        });

        searchView.setQueryHint("Nom emplacement");

        searchView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                expandablelistView.setAdapter(expListAdapter);
                int count = expListAdapter.getGroupCount();
                for (int i = 0; i < count; i++) {
                    expandablelistView.collapseGroup(i);
                }
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                expandablelistView.setAdapter(expListAdapter);
                int count = expListAdapter.getGroupCount();
                for (int i = 0; i < count; i++) {
                    expandablelistView.collapseGroup(i);
                }
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;  // Return true to expand action view
            }
        });
        return true;
    }

    //method to expand all groups
    private void expandAll() {
        int count = expListAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandablelistView.expandGroup(i);
        }
    }
}
