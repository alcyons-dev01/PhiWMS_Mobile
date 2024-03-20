package fr.alcyons.phimr4.PlanDePlacement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.ListViewAdapters.Depot_ZoneAdapter;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class ListeZonesActivity extends ServiceActivity {

    Depot depotSelectionne;
    Depot_Zone zoneSelectionnee;

    Depot_ZoneAdapter adapter;
    ListView depotZoneListView;
    TextView designationProduit;
    String produit_Designation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_zones);

        // Récupération du dépot avec la variable globale
        depotSelectionne = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotSelectionneID"));

        //gestion du produit designation
        designationProduit = (TextView) findViewById(R.id.designationProduit);
        produit_Designation = "";
        if(intent.getExtras().containsKey("designationProduit"))
        {
            produit_Designation = intent.getExtras().getString("designationProduit");
        }

        if(produit_Designation.contentEquals(""))
        {
            designationProduit.setVisibility(View.GONE);
        }
        else
        {
            designationProduit.setText(produit_Designation);
        }

        // Récupération de la listeView des zones
        depotZoneListView = (ListView) findViewById(R.id.listeView);
        depotZoneListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        // Récupération de la liste des zones à afficher
        List<Depot_Zone> zonesAAfficher = gestionnaireZone.getZonesEtEmplacementsParDepot(db, depotSelectionne);
        if(zonesAAfficher.size() != 0)
        {
            // Creation de l'adapter qui va gérer la liste des zones
            adapter = new Depot_ZoneAdapter(ListeZonesActivity.this, db, zonesAAfficher, depotZoneListView);

            // Modifier le nombre de zones trouvées
            TextView nbZones = (TextView) findViewById(R.id.nbElementInAdapter);
            nbZones.setText(String.valueOf(adapter.getCount()));

            // Afficher le nom du dépôt sélectionné
            TextView nomDepot = (TextView) findViewById(R.id.nomDepot);
            nomDepot.setText(depotSelectionne.getNom());

            // Remplir la vue
            depotZoneListView.setAdapter(adapter);

            depotZoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    zoneSelectionnee = (Depot_Zone) adapter.getItem(position);

                    Intent listeZonesIntent = new Intent(ListeZonesActivity.this, ListeEmplacementActivity.class);
                    Bundle listeZonesBundle = ListeZonesActivity.super.getBundle();
                    listeZonesBundle.putInt("zoneSelectionneeID", zoneSelectionnee.getZoneID());
                    listeZonesBundle.putInt("depotSelectionneID", depotSelectionne.getDepot_UID());
                    if(!produit_Designation.contentEquals(""))
                    {
                        listeZonesBundle.putString("designationProduit", produit_Designation);
                    }
                    listeZonesIntent.putExtras(listeZonesBundle);

                    ListeZonesActivity.this.startActivityForResult(listeZonesIntent, CodesEchangesActivites.RETOUR_EMPLACEMENT);
                }
            });
            invalidateOptionsMenu();
        }
        else
        {
            setResult(Activity.RESULT_OK, null);
            finish();
        }
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_EMPLACEMENT:
                    Depot_Emplacement emplacementSelectionne = gestionnaireEmplacement.getUnEmplacementByID(db, data.getExtras().getInt("emplacementSelectionneID"));
                    Intent listeZonesIntent = new Intent();
                    Bundle listeZonesBundle = super.getBundle();
                    listeZonesBundle.putInt("emplacementSelectionneID", emplacementSelectionne.get_UID());
                    listeZonesBundle.putInt("zoneSelectionneeID", zoneSelectionnee.getZoneID());
                    listeZonesBundle.putInt("depotSelectionneID", depotSelectionne.getDepot_UID());
                    listeZonesIntent.putExtras(listeZonesBundle);
                    setResult(Activity.RESULT_OK, listeZonesIntent);
                    finish();
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Nom zone...");
        return true;
    }

    @Override
    public void onBackPressed()
    {
        ListeZonesActivity.this.finish();
    }
}
