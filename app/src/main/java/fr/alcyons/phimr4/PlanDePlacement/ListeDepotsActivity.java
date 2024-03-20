package fr.alcyons.phimr4.PlanDePlacement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.ListViewAdapters.DepotAdapter;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class ListeDepotsActivity extends ServiceActivity {

    List<Depot> depotList;
    ListView depotListView;
    DepotAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_depots);

        //Récupération de la variable globale
        String typeDepotChoisi = intent.getExtras().getString("typeDepotChoisi");

        // Gestion de l'adapter et de la listView
        depotList = gestionnaireDepot.getDepotsParType(db, typeDepotChoisi);
        depotListView = (ListView) findViewById(R.id.listeView);

        adapter = new DepotAdapter(ListeDepotsActivity.this, depotList, utilisateurConnecte);

        depotListView.setAdapter(adapter);

        // Modifier le nombre de zones trouvées
        TextView nbDepot = (TextView) findViewById(R.id.nbElementInAdapter);
        nbDepot.setText(String.valueOf(adapter.getCount()));

        depotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Depot depotSelectionne = (Depot) adapter.getItem(position);
                Intent listeDepotsIntent = new Intent(ListeDepotsActivity.this, ListeZonesActivity.class);
                Bundle listeDepotsBundle = ListeDepotsActivity.super.getBundle();
                listeDepotsBundle.putInt("depotSelectionneID", depotSelectionne.getDepot_UID());
                listeDepotsIntent.putExtras(listeDepotsBundle);

                ListeDepotsActivity.this.startActivityForResult(listeDepotsIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
            }
        });
        invalidateOptionsMenu();
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    Intent listeDepotsIntent = new Intent();
                    listeDepotsIntent.putExtras(data.getExtras());
                    setResult(RESULT_OK, listeDepotsIntent);
                    finish();
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Nom dépot...");
        return true;
    }
}
