package fr.alcyons.phimr4.ControleDesRetoursScannee;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.ListViewAdapters.Depot_ZoneAdapter;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RESULT_ZONE;

/**
 * Created by olivier on 14/06/2019.
 */

public class ListeZoneCreationScanneeActivity  extends ServiceActivity {
    Depot depotSelectionne;

    Depot_ZoneAdapter adapter;
    ListView zoneListView;
    List<Depot_Zone> depotZoneList;
    TextView nbElementInAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_zones_par_depot);

        // Récupération du dépot sélectionné par l'utilisateur
        depotSelectionne = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotID"));

        if (depotSelectionne != null) {
            // Afficher le nom du dépôt sélectionné
            ((TextView) findViewById(R.id.nomDepot)).setText(depotSelectionne.getNom());
        }


        nbElementInAdapter = (TextView) findViewById(R.id.nbElementInAdapter);

        // Récupération de la listeView des zones
        zoneListView = (ListView) findViewById(R.id.listeView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        // Récupération de la liste des zones à afficher
        depotZoneList = gestionnaireZone.getZonesEtEmplacementsParDepot(db, depotSelectionne);

        // Modifier le nombre de zones trouvées
        nbElementInAdapter.setText(String.valueOf(depotZoneList.size()));

        // Creation de l'adapter qui va gérer la liste des zones
        adapter = new Depot_ZoneAdapter(ListeZoneCreationScanneeActivity.this, db, depotZoneList, zoneListView);

        // Lier le clic sur une zone à la fonction
        zoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Depot_Zone zoneSelectionnee = (Depot_Zone) adapter.getItem(position);

                if (zoneSelectionnee != null) {
                    // Récupérer les éléments nécessaires à la prochaine activité
                    Intent resultIntent = new Intent();

                    Bundle extras = ListeZoneCreationScanneeActivity.super.getBundle();
                    extras.putInt("zoneid", zoneSelectionnee.getZoneID());
                    resultIntent.putExtras(extras);

                    ListeZoneCreationScanneeActivity.this.setResult(RESULT_ZONE, resultIntent);
                    ListeZoneCreationScanneeActivity.this.finish();
                }
            }
        });

        zoneListView.setAdapter(adapter);
    }

    // Permet de gérer si dans le menu se trouve ou non le bouton de suppression
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Nom zone...");
        return true;
    }
}
