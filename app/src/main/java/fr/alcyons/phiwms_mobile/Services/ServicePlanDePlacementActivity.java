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
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.PlanDePlacement.ListeProduitsPlanDePlacementActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ServicePlanDePlacementActivity extends ServiceActivity {

    boolean firstPassage = true;
    PackageManager pm;

    List<Produit> listeProduitScannees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_plan_de_placement);
        pm = ServicePlanDePlacementActivity.this.getPackageManager();
        intent = ServicePlanDePlacementActivity.this.getIntent();
        listeProduitScannees = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        //Lance l'activite BarcodeCaptureActivity une seule fois
        if (firstPassage) {
            Intent servicePlanDePlacementIntent = null;
            Bundle servicePlanDePlacementBundle = super.getBundle();

            if (android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google")) {
                servicePlanDePlacementIntent = new Intent(ServicePlanDePlacementActivity.this, ScannerPlanDePlacementActivity.class);
            } else {
                servicePlanDePlacementIntent = new Intent(ServicePlanDePlacementActivity.this, BarcodeCaptureActivity.class);
            }

            servicePlanDePlacementBundle.putSerializable("ListProduitScannees", (Serializable) listeProduitScannees);
            servicePlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);
            ServicePlanDePlacementActivity.this.startActivityForResult(servicePlanDePlacementIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
            firstPassage = false;
        }
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            ServicePlanDePlacementActivity.this.finish();
        } else {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    listeProduitScannees = new ArrayList<>();
                    listeProduitScannees.addAll((List<Produit>) data.getExtras().getSerializable("ListProduitScannes"));

                    boolean placement = data.getExtras().getBoolean("placement");
                    Intent servicePlanDePlacementIntent = servicePlanDePlacementIntent = new Intent(ServicePlanDePlacementActivity.this, ListeProduitsPlanDePlacementActivity.class);
                    Bundle servicePlanDePlacementBundle = super.getBundle();
                    servicePlanDePlacementBundle.putSerializable("ListProduitScannes", (Serializable) listeProduitScannees);
                    servicePlanDePlacementBundle.putSerializable("placement", (Serializable) placement);
                    servicePlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);

                    ServicePlanDePlacementActivity.this.startActivity(servicePlanDePlacementIntent);
                    ServicePlanDePlacementActivity.this.finish();
                    break;
            }
            invalidateOptionsMenu();
        }
    }
}
