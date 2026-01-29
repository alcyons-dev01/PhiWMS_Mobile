package fr.alcyons.phiwms_mobile.Services;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPlanDePlacementActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.DepotSelecteur.DepotSelecteurMultipleActivity;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.PlanDePlacement.ListeProduitsPlanDePlacementActivity;
import fr.alcyons.phiwms_mobile.PlanDePlacement.ScanPlanDePlacementActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Stock.ListeReferenceActivity;

public class ServicePlanDePlacementActivity extends ServiceActivity {
    Boolean passageOnResume;
    Intent serviceStockIntent;
    Bundle serviceStockBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passageOnResume = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (passageOnResume) {
            serviceStockIntent = new Intent(ServicePlanDePlacementActivity.this, DepotSelecteurMultipleActivity.class);
            serviceStockBundle = ServicePlanDePlacementActivity.super.getBundle();
            serviceStockBundle.putString("depotType", "tous");
            serviceStockIntent.putExtras(serviceStockBundle);
            ServicePlanDePlacementActivity.this.startActivityForResult(serviceStockIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
        }
        passageOnResume = true;
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RESULT_SELECTION_DEPOT: {
                if (data != null) {
                    int depotUID_Selectionne = data.getIntExtra("depotUID_Selectionne", 0);
                    if (depotUID_Selectionne != 0) {
                        passageOnResume = false;
                        serviceStockIntent = new Intent(ServicePlanDePlacementActivity.this, ScanPlanDePlacementActivity.class);
                        serviceStockBundle = ServicePlanDePlacementActivity.super.getBundle();
                        serviceStockBundle.putInt("depotUID_Selectionne", depotUID_Selectionne);
                        serviceStockIntent.putExtras(serviceStockBundle);
                        ServicePlanDePlacementActivity.this.startActivity(serviceStockIntent);
                    }
                } else {
                    ServicePlanDePlacementActivity.this.finish();
                }
                break;
            }
        }
    }
}
