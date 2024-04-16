package fr.alcyons.phiwms_mobile.ControleDesRetours;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.List;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Depot_ZoneAdapter;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RESULT_ZONE;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT;

/**
 * Created by olivier on 16/04/2024.
 */
public class ListeZoneCreationActivity extends ServiceActivity {
    Depot depotSelectionne;

    Depot_ZoneAdapter adapter;
    ListView zoneListView;
    List<Depot_Zone> depotZoneList;
    TextView nbElementInAdapter;

    FloatingActionButton boutonRechercheDataMatrix;
    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_zones_par_depot);

        //gestion du bouton de recherche par datamatrix
        boutonRechercheDataMatrix = (FloatingActionButton) findViewById(R.id.boutonRechercheDataMatrix);
        pm = ListeZoneCreationActivity.this.getPackageManager();

        //gestion du clic sur le bouton de datamatrix
        boutonRechercheDataMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    Bundle detailProduitPlanDePlacementBundle = ListeZoneCreationActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                    detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    Intent detailProduitPlanDePlacementIntent = new Intent(ListeZoneCreationActivity.this, ScannerSearchOnlyActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    ListeZoneCreationActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        Bundle detailProduitPlanDePlacementBundle = ListeZoneCreationActivity.super.getBundle();
                        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                        detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
                        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent detailProduitPlanDePlacementIntent = new Intent(ListeZoneCreationActivity.this, BarcodeCaptureActivity.class);
                        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                        ListeZoneCreationActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                    else
                    {
                        Bundle detailProduitPlanDePlacementBundle = ListeZoneCreationActivity.super.getBundle();
                        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                        detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
                        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent detailProduitPlanDePlacementIntent = new Intent(ListeZoneCreationActivity.this, ScannerSearchOnlyActivity.class);
                        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                        ListeZoneCreationActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                }
            }
        });

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
        adapter = new Depot_ZoneAdapter(ListeZoneCreationActivity.this, db, depotZoneList, zoneListView);

        // Lier le clic sur une zone à la fonction
        zoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Depot_Zone zoneSelectionnee = (Depot_Zone) adapter.getItem(position);

                if (zoneSelectionnee != null) {
                    // Récupérer les éléments nécessaires à la prochaine activité
                    Intent resultIntent = new Intent();

                    Bundle extras = ListeZoneCreationActivity.super.getBundle();
                    extras.putInt("zoneid", zoneSelectionnee.getZoneID());
                    resultIntent.putExtras(extras);

                    ListeZoneCreationActivity.this.setResult(RESULT_ZONE, resultIntent);
                    ListeZoneCreationActivity.this.finish();
                }
            }
        });

        zoneListView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case RESULT_ZONE:
                    int zoneid = data.getExtras().getInt("zoneid");

                    break;
                case RETOUR_CODE_EMPLACEMENT:
                    int emplacementid = data.getExtras().getInt("emplacementId");

                    break;

                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    String code = data.getExtras().getString("code");
                    if(!code.contentEquals(""))
                    {
                        String[] tab_code = code.split(":");
                        int id_emplacement = Integer.parseInt(tab_code[tab_code.length-1]);
                        Depot_Emplacement depotEmplacement = gestionnaireEmplacement.getUnEmplacementByID(db, id_emplacement);
                        if(depotEmplacement != null)
                        {
                            Depot_Zone depotZone = gestionnaireZone.getUneZoneByID(db, depotEmplacement.getZoneID());
                            if(depotEmplacement!= null && depotZone != null){
                                Intent resultIntent = new Intent();

                                Bundle extras = ListeZoneCreationActivity.super.getBundle();
                                extras.putInt("zoneid", depotZone.getZoneID());
                                extras.putInt("emplacementid", depotEmplacement.get_UID());
                                resultIntent.putExtras(extras);

                                ListeZoneCreationActivity.this.setResult(RESULT_ZONE, resultIntent);
                                ListeZoneCreationActivity.this.finish();
                            }
                        }
                        else
                        {
                            Toast.makeText(ListeZoneCreationActivity.this, "Emplacement scanné inconnu", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    // Permet de gérer si dans le menu se trouve ou non le bouton de suppression
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Nom zone...");
        return true;
    }

    //surcharge du onBackPressed
    @Override
    public void onBackPressed() {
        // Récupérer les éléments nécessaires à la prochaine activité
        super.onBackPressed();
        Intent resultIntent = new Intent();

        Bundle extras = ListeZoneCreationActivity.super.getBundle();
        extras.putInt("zoneid", -1);
        resultIntent.putExtras(extras);

        ListeZoneCreationActivity.this.setResult(RESULT_ZONE, resultIntent);
        ListeZoneCreationActivity.this.finish();
    }
}
