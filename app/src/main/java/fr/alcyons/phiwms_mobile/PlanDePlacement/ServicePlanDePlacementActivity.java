package fr.alcyons.phiwms_mobile.PlanDePlacement;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ServicePlanDePlacementActivity extends ServiceActivity {

    boolean firstPassage = true;
    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_plan_de_placement);
        pm = ServicePlanDePlacementActivity.this.getPackageManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        //Lance l'activite BarcodeCaptureActivity une seule fois
        if (firstPassage) {
            Intent servicePlanDePlacementIntent = null;
            Bundle servicePlanDePlacementBundle = super.getBundle();

            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                servicePlanDePlacementIntent = new Intent(ServicePlanDePlacementActivity.this, ScannerSearchOnlyActivity.class);
                servicePlanDePlacementBundle.putBoolean("activerTextSuppression", true);
                servicePlanDePlacementBundle.putString("TextBannerManuel", "Scannez le datamatrix d'une référence");
                servicePlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
            }
            else
            {
                servicePlanDePlacementIntent = new Intent(ServicePlanDePlacementActivity.this, BarcodeCaptureActivity.class);
            }
            servicePlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
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
                    if (resultCode == BarcodeCaptureActivity.RESULT_OK) {
                        String codeComplet = data.getStringExtra("code");
                        if(codeComplet.contentEquals(""))
                        {
                            // On envoie volontairement une liste vide
                            List<Integer> produitIDs = new ArrayList<>();

                            Intent servicePlanDePlacementIntent = new Intent(ServicePlanDePlacementActivity.this, ListeProduitsPlanDePlacementActivity.class);
                            Bundle servicePlanDePlacementBundle = super.getBundle();
                            servicePlanDePlacementBundle.putSerializable("produitIDs", (Serializable) produitIDs);
                            servicePlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);

                            ServicePlanDePlacementActivity.this.startActivity(servicePlanDePlacementIntent);
                            ServicePlanDePlacementActivity.this.finish();
                        }
                        else
                        {
                            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeComplet);
                            if (gs1Decoupe.size() != 0) {
                                List<Produit> produitsConcernes = gestionnaireProduit.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                                List<Integer> produitIDs = new ArrayList<>();
                                for (Produit produit : produitsConcernes
                                ) {
                                    produitIDs.add(produit.getID_produit());
                                }

                                Intent servicePlanDePlacementIntent = new Intent(ServicePlanDePlacementActivity.this, ListeProduitsPlanDePlacementActivity.class);
                                Bundle servicePlanDePlacementBundle = super.getBundle();
                                servicePlanDePlacementBundle.putSerializable("produitIDs", (Serializable) produitIDs);
                                servicePlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);

                                ServicePlanDePlacementActivity.this.startActivity(servicePlanDePlacementIntent);
                                ServicePlanDePlacementActivity.this.finish();
                            } else {
                                Toast toast = Toast.makeText(ServicePlanDePlacementActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                firstPassage = true;
                                ServicePlanDePlacementActivity.this.onRestart();
                            }
                        }
                    } else if (resultCode == CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH) {
                        // On envoie volontairement une liste vide
                        List<Integer> produitIDs = new ArrayList<>();

                        Intent servicePlanDePlacementIntent = new Intent(ServicePlanDePlacementActivity.this, ListeProduitsPlanDePlacementActivity.class);
                        Bundle servicePlanDePlacementBundle = super.getBundle();
                        servicePlanDePlacementBundle.putSerializable("produitIDs", (Serializable) produitIDs);
                        servicePlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);

                        ServicePlanDePlacementActivity.this.startActivity(servicePlanDePlacementIntent);
                        ServicePlanDePlacementActivity.this.finish();
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    }
}
