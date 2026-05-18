package fr.alcyons.phiwms_mobile.DepotSelecteur;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GPSTracker;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class DepotSelecteurMultipleActivity extends ServiceActivity {

    Map<String, List<Depot>> depotParentListItems;
    ExpandableListView depotExpandablelistView;
    DepotExpandableListAdapter depotExpandableListAdapter;
    List<String> depotParentList;

    List<Depot> depotList;
    List<Depot> depotPuiList;
    List<Depot> depotPufList;

    FloatingActionButton boutonGeolocalisation;

    int depotNombre = 0;
    String depotType = "";

    int nbDepotType;
    protected DepotOpenHelper gestionnaireDepot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot_selecteur_multiple);
        gestionnaireDepot = new DepotOpenHelper(DepotSelecteurMultipleActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(DepotSelecteurMultipleActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                DepotSelecteurMultipleActivity.this.startActivity(intent);
                DepotSelecteurMultipleActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        depotType = Objects.requireNonNull(getIntent().getExtras()).getString("depotType");

        depotParentList = new ArrayList<>();
        depotParentListItems = new LinkedHashMap<>();
        depotList = new ArrayList<>();

        if (depotType.contentEquals("all")) {
            depotPuiList = DepotOpenHelper.getDepotsParType(db, "PUI");
            depotParentList.add("PUI");
            depotParentListItems.put("PUI", depotPuiList);
            depotList.addAll(depotPuiList);
        }

        if(depotType.contentEquals("pufpad"))
        {
            depotPufList = DepotOpenHelper.getDepotsParType(db, "PUF");
            depotParentList.add(depotPufList.size()+" Unités fonctionnelles");
            depotParentListItems.put("PUF", depotPufList);
            depotList.addAll(depotPufList);

            /*depotParentList.add("PAD");
            depotPadList = DepotOpenHelper.getDepotsParType(db, "PAD");
            depotParentListItems.put("PAD", depotPadList);
            depotList.addAll(depotPadList);*/
        }

        if(depotType.contentEquals("puf"))
        {
            depotPufList = DepotOpenHelper.getDepotsParType(db, "PUF");
            depotParentList.add(depotPufList.size()+" Unités fonctionnelles");
            depotParentListItems.put("PUF", depotPufList);
            depotList.addAll(depotPufList);
        }

        /*if(depotType.contentEquals("pad"))
        {
            depotParentList.add("PAD");
            depotPadList = DepotOpenHelper.getDepotsParType(db, "PAD");
            depotParentListItems.put("PAD", depotPadList);
            depotList.addAll(depotPadList);
        }*/

        if (depotType.contentEquals("tous")) {
            depotParentList.add("PUI");
            depotPuiList = DepotOpenHelper.getDepotsParType(db, "PUI");
            depotParentListItems.put("PUI", depotPuiList);
            depotList.addAll(depotPuiList);
            depotPufList = DepotOpenHelper.getDepotsParType(db, "PUF");
            depotParentList.add(depotPufList.size()+" Unités fonctionnelles");
            depotParentListItems.put("PUF", depotPufList);
            depotList.addAll(depotPufList);
            /* depotParentList.add("PAD");
            depotPadList = DepotOpenHelper.getDepotsParType(db, "PAD");
            depotParentListItems.put("PAD", depotPadList);
            depotList.addAll(depotPadList);*/
        }

        nbDepotType = depotParentList.size();
        depotNombre = depotList.size();
        depotExpandablelistView = findViewById(R.id.expandableListViewDepot);

        depotExpandableListAdapter = new DepotExpandableListAdapter(this, depotParentList, depotParentListItems, utilisateurConnecte);
        depotExpandablelistView.setAdapter(depotExpandableListAdapter);

        depotExpandablelistView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            Depot depotSelectionne = (Depot) depotExpandableListAdapter.getChild(groupPosition, childPosition);

            Intent resultIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
            resultIntent.putExtras(extras);
            setResult(CodesEchangesActivites.RESULT_SELECTION_DEPOT, resultIntent);
            DepotSelecteurMultipleActivity.this.finish();
            return false;
        });

        //boutonGeolocalisation = findViewById(R.id.boutonGeolocalisation);
        /*boutonGeolocalisation.setOnClickListener(view -> {

            double latitude = 0;
            double longitude = 0;

            // create class object
            GPSTracker gps = new GPSTracker(DepotSelecteurMultipleActivity.this);

            // check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

                Toast.makeText(getApplicationContext(), "Votre localisation est - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                //gps.showSettingsAlert();
            }

            ArrayList<DepotOpenHelper.CustomObject> depotLesPlusProche = gestionnaireDepot.getDepotLesPlusProche(latitude, longitude, depotList);
            if (latitude == 0 && longitude == 0) {
                Alerte.afficherAlerte(DepotSelecteurMultipleActivity.this, "Alerte", "Nous n'avons pas réussi à vous géolocaliser. Veuillez sélectionner manuellement votre dépot.", "alerte");
            } else if (depotLesPlusProche.isEmpty()) {
                Alerte.afficherAlerte(DepotSelecteurMultipleActivity.this, "Alerte", "Aucun dépot n'est situé à moins de 1km. Veuillez sélectionner manuellement votre dépot.", "alerte");
            } else if (depotLesPlusProche.size() == 1) {
                Depot depotLePlusProche = DepotOpenHelper.getDepotParID(db, depotLesPlusProche.get(0).getKey());
                boolean choixUtilisateur = Alerte.afficherAlerte(DepotSelecteurMultipleActivity.this, "Alerte", "Vous allez être redirigé vers le dépôt " + depotLePlusProche.getNom() + ". Voulez vous continuer ?", "OuiNon");

                if (choixUtilisateur) {
                    Intent resultIntent = new Intent();
                    Bundle extras = new Bundle();
                    extras.putInt("depotUID_Selectionne", depotLePlusProche.getDepot_UID());
                    resultIntent.putExtras(extras);
                    setResult(CodesEchangesActivites.RESULT_SELECTION_DEPOT, resultIntent);
                    finish();
                }
            } else {
                Alerte.afficherAlerte(DepotSelecteurMultipleActivity.this, "Alerte", "Plusieurs dépots se situent à moins de 1km. Veuillez sélectionner manuellement votre dépot.", "alerte");
                depotParentList = new ArrayList<>();
                depotParentListItems = new LinkedHashMap<>();
                depotList = new ArrayList<>();

                if (depotType.contentEquals("all")) {
                    depotParentList.add("PUI");
                    depotPuiList = new ArrayList<>();

                }
                depotPufList = new ArrayList<>();

                //depotParentList.add("PAD");
                //depotPadList = new ArrayList<>();

                for (DepotOpenHelper.CustomObject customObject : depotLesPlusProche) {
                    Depot depotLePlusProche = DepotOpenHelper.getDepotParID(db, customObject.getKey());
                    if (depotLePlusProche.getStructure().contentEquals("PUI")) {
                        depotPuiList.add(depotLePlusProche);
                    } else if (depotLePlusProche.getStructure().contentEquals("PUF")) {
                        depotPufList.add(depotLePlusProche);
                        depotParentList.add(depotPufList.size()+" Unités fonctionnelles");
                    } else if (depotLePlusProche.getStructure().contentEquals("PAD")) {
                        //depotPadList.add(depotLePlusProche);
                    }
                }
                if (depotType.contentEquals("all")) {
                    depotParentListItems.put("PUI", depotPuiList);
                    depotList.addAll(depotPuiList);
                }
                depotParentListItems.put("PUF", depotPufList);
                depotList.addAll(depotPufList);
                //depotParentListItems.put("PAD", depotPadList);
                //depotList.addAll(depotPadList);
                depotNombre = depotList.size();
                depotExpandablelistView = findViewById(R.id.expandableListView);

                depotExpandableListAdapter = new DepotExpandableListAdapter(DepotSelecteurMultipleActivity.this, depotParentList, depotParentListItems, utilisateurConnecte);
                depotExpandablelistView.setAdapter(depotExpandableListAdapter);
            }
        });*/
        invalidateOptionsMenu();

        if(nbDepotType == 1)
        {
            expandAll();
        }

        if(depotList.size() == 1)
        {
            Depot depotSelectionne = depotList.get(0);

            Intent resultIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
            resultIntent.putExtras(extras);
            setResult(CodesEchangesActivites.RESULT_SELECTION_DEPOT, resultIntent);
            DepotSelecteurMultipleActivity.this.finish();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem searchMenuItem = menu.findItem(R.id.rechercheMenu);
        searchMenuItem.setVisible(true);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                depotExpandableListAdapter.filter(query);
                //display the list
                depotExpandablelistView.setAdapter(depotExpandableListAdapter);
                //expand all Groups
                expandAll();
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                runOnUiThread(() -> {
                    depotExpandableListAdapter.filter(newText);
                    //display the list
                    depotExpandablelistView.setAdapter(depotExpandableListAdapter);
                    //expand all Groups
                    expandAll();
                });
                return false;
            }
        });

        searchView.setQueryHint("Nom dépot");

        searchView.setOnCloseListener(() -> {
            searchView.setQuery("", true);
            return false;
        });

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                collapseAll();
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                expandAll();
                return true;  // Return true to expand action view
            }
        });

        return true;
    }

    private void expandAll() {
        for (int i = 0; i < nbDepotType; i++) {
            depotExpandablelistView.expandGroup(i);
        }
    }

    private void collapseAll() {
        for (int i = 0; i < nbDepotType; i++) {
            depotExpandablelistView.collapseGroup(i);
        }
    }
}
