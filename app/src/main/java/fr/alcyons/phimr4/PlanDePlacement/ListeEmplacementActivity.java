package fr.alcyons.phimr4.PlanDePlacement;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.ListViewAdapters.Depot_EmplacementAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class ListeEmplacementActivity extends ServiceActivity {

    Depot depotSelectionne;
    Depot_Zone zoneSelectionnee;
    Depot_Emplacement emplacementSelectionne;

    ListView emplacementListView;
    TextView designationProduit;
    List<Depot_Emplacement> depotEmplacementList;
    Depot_EmplacementAdapter adapter;

    String produit_designation;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_emplacement);

        //gestion du package manager
        pm = ListeEmplacementActivity.this.getPackageManager();

        //gestion du nom du produit sélectionner pour un retour PUI
        designationProduit = (TextView) findViewById(R.id.designationProduit);
        produit_designation = "";

        if(intent.getExtras().containsKey("designationProduit"))
        {
            produit_designation = intent.getExtras().getString("designationProduit");
        }

        if(produit_designation.contentEquals(""))
        {
            designationProduit.setVisibility(View.GONE);
        }
        else
        {
            designationProduit.setText(produit_designation);
        }

        // Récupération de la zone et du dépôt sélectionnés
        zoneSelectionnee = gestionnaireZone.getUneZoneByID(db, intent.getExtras().getInt("zoneSelectionneeID"));
        depotSelectionne = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotSelectionneID"));
        depotEmplacementList = gestionnaireEmplacement.getEmplacementsParZone(db, zoneSelectionnee);
        if (depotEmplacementList.size() == 0) {
            Depot_Emplacement depotEmplacement = new Depot_Emplacement("emplacement", "", "", "", "", zoneSelectionnee.getZoneID(), zoneSelectionnee.getDepotID(), zoneSelectionnee.getDepot_Reference(), "");
            long rowID = gestionnaireEmplacement.insererUnDepotEmplacementEnBDD(db, depotEmplacement);
            if (rowID != -1) {
                gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, depotEmplacement.getPhiMR4UUID(), depotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                emplacementSelectionne = depotEmplacement;
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                {
                    Intent listeEmplacementIntent = new Intent();
                    Bundle listeEmplacementBundle = ListeEmplacementActivity.super.getBundle();
                    listeEmplacementBundle.putInt("emplacementSelectionneID", emplacementSelectionne.get_UID());
                    listeEmplacementBundle.putInt("zoneSelectionneeID", zoneSelectionnee.getZoneID());
                    listeEmplacementBundle.putInt("depotSelectionneID", depotSelectionne.getDepot_UID());
                    listeEmplacementIntent.putExtras(listeEmplacementBundle);
                    setResult(BarcodeCaptureActivity.RESULT_OK, listeEmplacementIntent);
                    ListeEmplacementActivity.this.finish();
                }
                return;
            } else {
                Alerte.afficherAlerte(ListeEmplacementActivity.this, "Alerte", "Une erreur dans l'insertion de l'emplacement est survenue", "alerte");
            }
        }
        zoneSelectionnee.setEmplacements(depotEmplacementList);

        // Modification des indicateurs
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(zoneSelectionnee.getEmplacements().size()));
        ((TextView) findViewById(R.id.nomZone)).setText(String.valueOf(zoneSelectionnee.getZoneName()));
        ((TextView) findViewById(R.id.nomDepot)).setText(String.valueOf(depotSelectionne.getNom()));

        // Récupération et remplissage de la listView
        emplacementListView = (ListView) findViewById(R.id.listeView);
        emplacementListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        adapter = new Depot_EmplacementAdapter(ListeEmplacementActivity.this, db, depotEmplacementList);
        emplacementListView.setAdapter(adapter);

        emplacementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                emplacementSelectionne = (Depot_Emplacement) adapter.getItem(position);

                Intent listeEmplacementIntent = new Intent();
                Bundle listeEmplacementBundle = ListeEmplacementActivity.super.getBundle();
                listeEmplacementBundle.putInt("emplacementSelectionneID", emplacementSelectionne.get_UID());
                listeEmplacementBundle.putInt("zoneSelectionneeID", zoneSelectionnee.getZoneID());
                listeEmplacementBundle.putInt("depotSelectionneID", depotSelectionne.getDepot_UID());
                listeEmplacementIntent.putExtras(listeEmplacementBundle);
                setResult(BarcodeCaptureActivity.RESULT_OK, listeEmplacementIntent);

                ListeEmplacementActivity.this.finish();
            }
        });

        invalidateOptionsMenu();
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Nom emplacement...");
        return true;
    }

    @Override
    public void onBackPressed()
    {
        ListeEmplacementActivity.this.finish();
    }
}
