package fr.alcyons.phimr4.ControleDesRetours;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.ListViewAdapters.Depot_EmplacementAdapter;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT;

/**
 * Created by olivier on 27/12/2017.
 */

public class ListeEmplacementCreationActivity extends ServiceActivity {

    Depot_Zone zoneSelectionnee;
    Depot_EmplacementAdapter adapter;
    ListView listViewEmplacements;
    List<Depot_Emplacement> listeDepotsEmplacements = new ArrayList<>();
    FloatingActionButton fabAdd;
    List<Integer> listeEmplacementCourant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_emplacements_par_zone);

        // Récupération de la zone et du dépôt sélectionnés
        if(intent.hasExtra("zoneid"))
        {
            zoneSelectionnee = gestionnaireZone.getUneZoneByID(db, intent.getExtras().getInt("zoneid"));
            zoneSelectionnee.setEmplacements(gestionnaireEmplacement.getEmplacementsParZone(db, zoneSelectionnee));
            // Modification des indicateurs
            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(zoneSelectionnee.getEmplacements().size()));
            ((TextView) findViewById(R.id.nomZone)).setText(String.valueOf(zoneSelectionnee.getZoneName()));
        }
        else if(intent.hasExtra("listeEmplacement"))
        {
            listeEmplacementCourant = new ArrayList<>();
            listeEmplacementCourant = intent.getExtras().getIntegerArrayList("listeEmplacement");
        }

        // Récupération et remplissage de la listView
        listViewEmplacements = (ListView) findViewById(R.id.listeView);
        listViewEmplacements.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setVisibility(View.INVISIBLE);
        listViewEmplacements.setTextFilterEnabled(false);
        listeDepotsEmplacements = (List<Depot_Emplacement>) intent.getExtras().getSerializable("listeEmplacements");
        if (listeDepotsEmplacements == null && listeEmplacementCourant == null) {
            listeDepotsEmplacements = gestionnaireEmplacement.getEmplacementsParZone(db, zoneSelectionnee);
        }
        else
        {
            listeDepotsEmplacements = new ArrayList<>();

            for(int i = 0; i < listeEmplacementCourant.size(); i++)
            {
                Depot_Emplacement courant = EmplacementOpenHelper.getUnEmplacementByID(db, listeEmplacementCourant.get(i));
                listeDepotsEmplacements.add(courant);
            }
            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeDepotsEmplacements.size()));
            ((TextView) findViewById(R.id.nomZone)).setText("");
        }
        adapter = new Depot_EmplacementAdapter(ListeEmplacementCreationActivity.this, db, listeDepotsEmplacements);
        listViewEmplacements.setAdapter(adapter);


        // Lier le clic sur une zone à la fonction
        listViewEmplacements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Depot_Emplacement emplacementSelectionner = (Depot_Emplacement) adapter.getItem(position);

                if (emplacementSelectionner != null) {
                    // Récupérer les éléments nécessaires à la prochaine activité
                    Intent resultIntent = new Intent();

                    Bundle extras = ListeEmplacementCreationActivity.super.getBundle();
                    extras.putInt("emplacementId", emplacementSelectionner.get_UID());
                    resultIntent.putExtras(extras);

                    ListeEmplacementCreationActivity.this.setResult(RETOUR_CODE_EMPLACEMENT, resultIntent);
                    ListeEmplacementCreationActivity.this.finish();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    // Permet de gérer si dans le menu se trouve ou non le bouton de suppression
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.prepareOptionsMenu(menu, adapter, null, "Nom emplacement...");

        return true;
    }

    @Override
    public void onBackPressed()
    {
        // Récupérer les éléments nécessaires à la prochaine activité
        Intent resultIntent = new Intent();

        Bundle extras = ListeEmplacementCreationActivity.super.getBundle();
        extras.putInt("emplacementId", -1);
        resultIntent.putExtras(extras);

        ListeEmplacementCreationActivity.this.setResult(RETOUR_CODE_EMPLACEMENT, resultIntent);
        ListeEmplacementCreationActivity.this.finish();
    }

}