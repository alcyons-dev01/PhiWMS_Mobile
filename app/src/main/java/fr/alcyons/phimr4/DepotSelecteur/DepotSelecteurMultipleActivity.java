package fr.alcyons.phimr4.DepotSelecteur;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.GPSTracker;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by jessica on 24/11/2017.
 */

public class DepotSelecteurMultipleActivity extends ServiceActivity {

    Map<String, List<Depot>> depotParentListItems;
    ExpandableListView depotExpandablelistView;
    DepotExpandableListAdapter depotExpandableListAdapter;
    List<String> depotParentList;

    List<Depot> depotList;
    List<Depot> depotPuiList;
    List<Depot> depotPufList;
    List<Depot> depotPadList;

    TextView nbElementInAdapterTextView;
    FloatingActionButton boutonGeolocalisation;

    int depotNombre = 0;
    String depotType = "";

    int nbDepotType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot_selecteur_multiple);
    }

    @Override
    public void onResume() {
        super.onResume();
        depotType = getIntent().getExtras().getString("depotType");

        depotParentList = new ArrayList<>();
        depotParentListItems = new LinkedHashMap<>();
        depotList = new ArrayList<>();

        if (depotType.contentEquals("all")) {
            depotParentList.add("PUI");
            depotPuiList = gestionnaireDepot.getDepotsParType(db, "PUI");
            depotParentListItems.put("PUI", depotPuiList);
            depotList.addAll(depotPuiList);
        }

        if(depotType.contentEquals("pufpad"))
        {
            depotParentList.add("PUF");
            depotPufList = gestionnaireDepot.getDepotsParType(db, "PUF");
            depotParentListItems.put("PUF", depotPufList);
            depotList.addAll(depotPufList);

            depotParentList.add("PAD");
            depotPadList = gestionnaireDepot.getDepotsParType(db, "PAD");
            depotParentListItems.put("PAD", depotPadList);
            depotList.addAll(depotPadList);
        }

        if(depotType.contentEquals("puf"))
        {
            depotParentList.add("PUF");
            depotPufList = gestionnaireDepot.getDepotsParType(db, "PUF");
            depotParentListItems.put("PUF", depotPufList);
            depotList.addAll(depotPufList);
        }

        if(depotType.contentEquals("pad"))
        {
            depotParentList.add("PAD");
            depotPadList = gestionnaireDepot.getDepotsParType(db, "PAD");
            depotParentListItems.put("PAD", depotPadList);
            depotList.addAll(depotPadList);
        }

        if (depotType.contentEquals("tous")) {
            depotParentList.add("PUI");
            depotPuiList = gestionnaireDepot.getDepotsParType(db, "PUI");
            depotParentListItems.put("PUI", depotPuiList);
            depotList.addAll(depotPuiList);
            depotParentList.add("PUF");
            depotPufList = gestionnaireDepot.getDepotsParType(db, "PUF");
            depotParentListItems.put("PUF", depotPufList);
            depotList.addAll(depotPufList);
            depotParentList.add("PAD");
            depotPadList = gestionnaireDepot.getDepotsParType(db, "PAD");
            depotParentListItems.put("PAD", depotPadList);
            depotList.addAll(depotPadList);
        }

        nbDepotType = depotParentList.size();

        nbElementInAdapterTextView = (TextView) findViewById(R.id.nbElementInAdapter);
        depotNombre = depotList.size();
        nbElementInAdapterTextView.setText(String.valueOf(depotNombre));

        depotExpandablelistView = (ExpandableListView) findViewById(R.id.expandableListViewDepot);

        depotExpandableListAdapter = new DepotExpandableListAdapter(this, depotParentList, depotParentListItems, utilisateurConnecte);
        depotExpandablelistView.setAdapter(depotExpandableListAdapter);

        depotExpandablelistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Depot depotSelectionne = (Depot) depotExpandableListAdapter.getChild(groupPosition, childPosition);

                Intent resultIntent = new Intent();
                Bundle extras = new Bundle();
                extras.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
                resultIntent.putExtras(extras);
                setResult(CodesEchangesActivites.RESULT_SELECTION_DEPOT, resultIntent);
                DepotSelecteurMultipleActivity.this.finish();
                return false;
            }
        });

        boutonGeolocalisation = (FloatingActionButton) findViewById(R.id.boutonGeolocalisation);
        boutonGeolocalisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                    gps.showSettingsAlert();
                }

                ArrayList<DepotOpenHelper.CustomObject> depotLesPlusProche = gestionnaireDepot.getDepotLesPlusProche(latitude, longitude, depotList);
                if (latitude == 0 && longitude == 0) {
                    Alerte.afficherAlerte(DepotSelecteurMultipleActivity.this, "Alerte", "Nous n'avons pas réussi à vous géolocaliser. Veuillez sélectionner manuellement votre dépot.", "alerte");
                } else if (depotLesPlusProche.size() == 0) {
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
                    depotParentList.add("PUF");
                    depotPufList = new ArrayList<>();

                    depotParentList.add("PAD");
                    depotPadList = new ArrayList<>();

                    for (DepotOpenHelper.CustomObject customObject : depotLesPlusProche) {
                        Depot depotLePlusProche = DepotOpenHelper.getDepotParID(db, customObject.getKey());
                        if (depotLePlusProche.getStructure().contentEquals("PUI")) {
                            depotPuiList.add(depotLePlusProche);
                        } else if (depotLePlusProche.getStructure().contentEquals("PUF")) {
                            depotPufList.add(depotLePlusProche);
                        } else if (depotLePlusProche.getStructure().contentEquals("PAD")) {
                            depotPadList.add(depotLePlusProche);
                        }
                    }
                    if (depotType.contentEquals("all")) {
                        depotParentListItems.put("PUI", depotPuiList);
                        depotList.addAll(depotPuiList);
                    }
                    depotParentListItems.put("PUF", depotPufList);
                    depotList.addAll(depotPufList);
                    depotParentListItems.put("PAD", depotPadList);
                    depotList.addAll(depotPadList);

                    nbElementInAdapterTextView = (TextView) findViewById(R.id.nbElementInAdapter);
                    depotNombre = depotList.size();
                    nbElementInAdapterTextView.setText(String.valueOf(depotNombre));

                    depotExpandablelistView = (ExpandableListView) findViewById(R.id.expandableListView);

                    depotExpandableListAdapter = new DepotExpandableListAdapter(DepotSelecteurMultipleActivity.this, depotParentList, depotParentListItems, utilisateurConnecte);
                    depotExpandablelistView.setAdapter(depotExpandableListAdapter);
                }
            }
        });
        invalidateOptionsMenu();

        if(nbDepotType == 1)
        {
            expandAll();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem searchMenuItem = menu.findItem(R.id.rechercheMenu);
        searchMenuItem.setVisible(true);
        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(searchMenuItem);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                depotExpandableListAdapter.filter(query);
                //display the list
                depotExpandablelistView.setAdapter(depotExpandableListAdapter);
                //expand all Groups
                expandAll();

                if (nbElementInAdapterTextView != null) {
                    nbElementInAdapterTextView.setText(String.valueOf(expandableListAdapterCount()));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        depotExpandableListAdapter.filter(newText);
                        //display the list
                        depotExpandablelistView.setAdapter(depotExpandableListAdapter);
                        //expand all Groups
                        expandAll();
                    }
                });
                return false;
            }
        });

        searchView.setQueryHint("Nom dépot");

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setQuery("", true);
                if (nbElementInAdapterTextView != null) {
                    nbElementInAdapterTextView.setText(String.valueOf(expandableListAdapterCount()));
                }
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (nbElementInAdapterTextView != null) {
                    nbElementInAdapterTextView.setText(String.valueOf(expandableListAdapterCount()));
                }
                collapseAll();
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (nbElementInAdapterTextView != null) {
                    nbElementInAdapterTextView.setText(String.valueOf(expandableListAdapterCount()));
                }
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

    private int expandableListAdapterCount() {
        int itemCount = 0;
        int gourpCount = depotExpandableListAdapter.getGroupCount();
        for (int i = 0; i < gourpCount; i++) {
            itemCount = itemCount + depotExpandableListAdapter.getChildrenCount(i);
        }

        return itemCount;
    }
}
