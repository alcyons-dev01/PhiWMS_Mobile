package fr.alcyons.phimr4.DotationService;

import android.content.Intent;
import android.os.Bundle;
import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurSimpleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by jessica on 03/10/2017.
 */

public class ServiceDotationServiceActivity extends ServiceActivity {

    Boolean passageOnResume;
    Intent serviceDotationServiceIntent;
    Bundle serviceDotationServiceBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passageOnResume = true;
        vide = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (passageOnResume) {
            serviceDotationServiceBundle = ServiceDotationServiceActivity.super.getBundle();
            serviceDotationServiceBundle.putString("depotType", "PUF");
            serviceDotationServiceBundle.putString("service", "Dotation Service");
            serviceDotationServiceBundle.putBoolean("vide", vide);
            serviceDotationServiceIntent = new Intent(ServiceDotationServiceActivity.this, DepotSelecteurSimpleActivity.class);
            serviceDotationServiceIntent.putExtras(serviceDotationServiceBundle);
            ServiceDotationServiceActivity.this.startActivityForResult(serviceDotationServiceIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
        }
        vide = false;
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
                        serviceDotationServiceBundle = ServiceDotationServiceActivity.super.getBundle();
                        serviceDotationServiceBundle.putInt("depotSelectionneID", depotUID_Selectionne);

                        serviceDotationServiceIntent = new Intent(ServiceDotationServiceActivity.this, ListeDotationServiceActivity.class);
                        serviceDotationServiceIntent.putExtras(serviceDotationServiceBundle);
                        ServiceDotationServiceActivity.this.startActivity(serviceDotationServiceIntent);
                    }
                } else {
                    ServiceDotationServiceActivity.this.finish();
                }
                break;
            }
        }
    }
}