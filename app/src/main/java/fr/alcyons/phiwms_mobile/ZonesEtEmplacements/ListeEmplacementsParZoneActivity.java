package fr.alcyons.phiwms_mobile.ZonesEtEmplacements;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Depot_EmplacementAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ListeEmplacementsParZoneActivity extends ServiceActivity {

    public boolean modeConsultation;
    Depot_Zone zoneSelectionnee;
    Depot depotSelectionne;
    Depot_EmplacementAdapter adapter;
    ListView listViewEmplacements;
    List<Depot_Emplacement> listeDepotsEmplacements = new ArrayList<>();
    // Gestion du bouton de suppression
    MenuItem.OnMenuItemClickListener clickBoutonSuppression = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            SparseBooleanArray listeCheckedItems = listViewEmplacements.getCheckedItemPositions();
                /*\ ATTENTION, le parcours de la boucle doit se faire en sens inverse afin de prévenir la réorganisation des index de la liste
                 des emplacements suite à une suppression/*/
            for (int i = listeCheckedItems.size() - 1; i >= 0; i--) {
                if (listeCheckedItems.valueAt(i)) {
                    int positionElementASupprimer = listeCheckedItems.keyAt(i);
                    Depot_Emplacement emplacementASupprimer = (Depot_Emplacement) adapter.getItem(positionElementASupprimer);
                }
            }
            listViewEmplacements.setAdapter(adapter);
            invalidateOptionsMenu();

            TextView nbZones = (TextView) findViewById(R.id.nbElementInAdapter);
            nbZones.setText(String.valueOf(adapter.getCount()));
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_emplacements_par_zone);

        // Récupération de la zone et du dépôt sélectionnés
        zoneSelectionnee = ZoneOpenHelper.getUneZoneByID(db, Objects.requireNonNull(intent.getExtras()).getInt("zoneSelectionneeID"));
        zoneSelectionnee.setEmplacements(EmplacementOpenHelper.getEmplacementsParZone(db, zoneSelectionnee));
        depotSelectionne = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotSelectionneID"));
        modeConsultation = intent.getExtras().getBoolean("modeConsultation");

        // Modification des indicateurs
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(zoneSelectionnee.getEmplacements().size()));
        ((TextView) findViewById(R.id.nomZone)).setText(String.valueOf(zoneSelectionnee.getZoneName()));
        ((TextView) findViewById(R.id.nomDepot)).setText(String.valueOf(depotSelectionne.getNom()));

        // Récupération et remplissage de la listView
        listViewEmplacements = (ListView) findViewById(R.id.listeView);
        listViewEmplacements.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listViewEmplacements.setTextFilterEnabled(false);
        listeDepotsEmplacements = (List<Depot_Emplacement>) intent.getExtras().getSerializable("listeEmplacements");
        if (listeDepotsEmplacements == null) {
            listeDepotsEmplacements = EmplacementOpenHelper.getEmplacementsParZone(db, zoneSelectionnee);
        }
        adapter = new Depot_EmplacementAdapter(ListeEmplacementsParZoneActivity.this, db, listeDepotsEmplacements);
        listViewEmplacements.setAdapter(adapter);


        // Récupérer le bouton de création d'un nouvel emplacement
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(view -> {
            String nomNouvelEmplacement = Alerte.afficherAlerteEditText(ListeEmplacementsParZoneActivity.this, "Nouvel emplacement", "Entrez le nom du nouvel emplacement");
            if (nomNouvelEmplacement != null) {
                // Créer le nouvel Emplacement
                Depot_Emplacement nouvelEmplacement = new Depot_Emplacement(nomNouvelEmplacement, null, null, null, null, zoneSelectionnee.getZoneID(), depotSelectionne.getDepot_UID(), depotSelectionne.getDepot_Reference(), "");

                // Ajouter l'emplacement à la zone
                adapter.add(nouvelEmplacement);
                adapter.notifyDataSetChanged();

                // Modifier le nombre de zones trouvées
                TextView nbZones = (TextView) findViewById(R.id.nbElementInAdapter);
                nbZones.setText(String.valueOf(adapter.getCount()));
            }
        });

        // Lier le clic sur une zone à la fonction
        listViewEmplacements.setOnItemClickListener((parent, view, position, id) -> {

        });

        if (modeConsultation) {
            fabAdd.setVisibility(View.GONE);
        } else {
            listViewEmplacements.setOnItemLongClickListener((parent, view, position, id) -> {
                /*CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.setChecked(true);
                listViewEmplacements.setItemChecked(position, checkBox.isChecked());
                // On retourne true pour indiquer qu'on a géré le clic et qu'il ne faut pas appeler la méthode OnItemClicListener
                invalidateOptionsMenu();
                return true;*/

                return true;
            });
        }
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

    // Si les checkbox sont affichés alors cela les enlève
    // Si on est en mode consultation on arrete l'activity
    @Override
    public void onBackPressed() {
        if (modeConsultation) {
            super.onBackPressed();
            return;
        }
        Intent newIntent = new Intent(ListeEmplacementsParZoneActivity.this, DetailZoneSaisieActivity.class);
        Bundle extras = super.getBundle();
        extras.putSerializable("listeEmplacements", (Serializable) listeDepotsEmplacements);
        extras.putInt("depotSelectionneID", depotSelectionne.getDepot_UID());
        extras.putInt("zoneSelectionneeID", zoneSelectionnee.getZoneID());
        newIntent.putExtras(extras);

        ListeEmplacementsParZoneActivity.this.startActivity(newIntent);
        ListeEmplacementsParZoneActivity.this.finish();
    }

}
