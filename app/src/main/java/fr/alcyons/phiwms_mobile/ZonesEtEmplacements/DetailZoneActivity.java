package fr.alcyons.phiwms_mobile.ZonesEtEmplacements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class DetailZoneActivity extends ServiceActivity {

    Depot_Zone depotZoneSelectionne;
    Depot depotSelectionne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_zone_non_modifiable);

        // Mettre en titre le dépôt sélectionné
        depotSelectionne = DepotOpenHelper.getDepotParID(db, Objects.requireNonNull(intent.getExtras()).getInt("depotSelectionneID"));
        if (depotSelectionne != null) {
            String nomDepot = depotSelectionne.getNom();
            if(utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS") && depotSelectionne.getStructure().contentEquals("PAD"))
            {
                String[] tab_nom = depotSelectionne.getNom().split(" ");
                String nom = tab_nom[0];
                if(nom.length() > 2)
                {
                    nom = nom.substring(0, 3)+"...";
                }
                else
                {
                    nom = nom +"...";
                }
                String prenom = tab_nom[1];
                if(prenom.length() > 2)
                {
                    prenom = prenom.substring(0, 3)+"...";
                }
                else
                {
                    prenom = prenom+"...";
                }
                nomDepot = nom+" "+prenom;
            }
            ((TextView) findViewById(R.id.nomDepot)).setText(nomDepot);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        // Afficher les valeurs de la zone sélectionnée
        depotZoneSelectionne = ZoneOpenHelper.getUneZoneByID(db, Objects.requireNonNull(intent.getExtras()).getInt("zoneSelectionneeID"));

        if (depotZoneSelectionne != null) {
            depotZoneSelectionne.setEmplacements(EmplacementOpenHelper.getEmplacementsParZone(db, depotZoneSelectionne));
            ((TextView) findViewById(R.id.nomZone)).setText(depotZoneSelectionne.getZoneName());
            ((TextView) findViewById(R.id.zoneId)).setText(String.valueOf(depotZoneSelectionne.getZoneID()));

            //Caractéristique
            String caracteristique = depotZoneSelectionne.getConservation();
            findViewById(R.id.controle_hydrometrie).setVisibility(View.GONE);
            findViewById(R.id.controle_refrigeration).setVisibility(View.GONE);
            findViewById(R.id.controle_radiation).setVisibility(View.GONE);
            findViewById(R.id.controle_temperature).setVisibility(View.GONE);
            findViewById(R.id.controle_luminosite).setVisibility(View.GONE);

            if (!caracteristique.equals("null")) {
                if (caracteristique.contentEquals(getString(R.string.controle_hydrometrie))) {
                    findViewById(R.id.controle_hydrometrie).setVisibility(View.VISIBLE);
                } else if (caracteristique.contentEquals(getString(R.string.controle_refrigeration))) {
                    findViewById(R.id.controle_refrigeration).setVisibility(View.VISIBLE);
                } else if (caracteristique.contentEquals(getString(R.string.controle_radiation))) {
                    findViewById(R.id.controle_radiation).setVisibility(View.VISIBLE);
                } else if (caracteristique.contentEquals(getString(R.string.controle_temperature))) {
                    findViewById(R.id.controle_temperature).setVisibility(View.VISIBLE);
                } else if (caracteristique.contentEquals(getString(R.string.controle_luminosite))) {
                    findViewById(R.id.controle_luminosite).setVisibility(View.VISIBLE);
                }

            }

            ((TextView) findViewById(R.id.caracteristique)).setText(String.valueOf(depotZoneSelectionne.getConservation()).equals("null") ? "" : String.valueOf(depotZoneSelectionne.getConservation()));
            ((TextView) findViewById(R.id.typesDeRangements)).setText(String.valueOf(depotZoneSelectionne.getType_Emplacement()).equals("null") ? "" : String.valueOf(depotZoneSelectionne.getType_Emplacement()));
            ((TextView) findViewById(R.id.identifiantCodeBarre)).setText(String.valueOf(depotZoneSelectionne.getDataMatrixReference()).equals("null") ? "" : String.valueOf(depotZoneSelectionne.getDataMatrixReference()));


            List<Depot_Emplacement> depotEmplacementList = EmplacementOpenHelper.getEmplacementsParZone(db, depotZoneSelectionne);

            ((LinearLayout) findViewById(R.id.listEmplacement)).removeAllViews();
            for (Depot_Emplacement depotEmplacement : depotEmplacementList) {

                @SuppressLint("InflateParams") View vi = getLayoutInflater().inflate(R.layout.row_depot_emplacement, null);

                ((TextView) vi.findViewById(R.id.nomEmplacement)).setText(depotEmplacement.getAdressage());
                ((TextView) vi.findViewById(R.id.dataMatrixEmplacement)).setText(depotEmplacement.getCode_GLN());

                ((LinearLayout) findViewById(R.id.listEmplacement)).addView(vi);
            }

            String nbEmplacements = depotEmplacementList.size() + " emplacements";
            ((TextView) findViewById(R.id.nbEmplacements)).setText(nbEmplacements);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu action et utilisation de l'item ADD
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuEdit).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Récupération de l'item ADD et affectation de l'action à réaliser lors d'un clic
        MenuItem item = menu.findItem(R.id.menuEdit);
        item.setOnMenuItemClickListener(item1 -> {
            onMenuEditClick();
            return true;
        });
        return true;
    }

    private void onMenuEditClick() {
        Intent detailZoneIntent = new Intent(DetailZoneActivity.this, DetailZoneSaisieActivity.class);
        Bundle detailZoneBundle = DetailZoneActivity.super.getBundle();
        detailZoneBundle.putInt("depotSelectionneID", depotSelectionne.getDepot_UID());
        detailZoneBundle.putInt("depotZoneSelectionneID", depotZoneSelectionne.getZoneID());
        detailZoneIntent.putExtras(detailZoneBundle);
        DetailZoneActivity.this.startActivity(detailZoneIntent);
    }
}
