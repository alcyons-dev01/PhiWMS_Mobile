package fr.alcyons.phimr4.DemandeDotationPAD;

import android.content.Intent;
import android.os.Bundle;

import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurSimpleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by jessica on 06/10/2017.
 */

public class ServiceDemandeDotationPADActivity extends ServiceActivity {

    Boolean passageOnResume;
    Intent serviceDemandeDotationPADIntent;
    Bundle serviceDemandeDotationPADBundle;

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
            serviceDemandeDotationPADBundle = ServiceDemandeDotationPADActivity.super.getBundle();
            serviceDemandeDotationPADBundle.putString("depotType", "PAD");
            serviceDemandeDotationPADBundle.putString("service", "Dotation PAD");
            serviceDemandeDotationPADBundle.putBoolean("vide", vide);
            serviceDemandeDotationPADIntent = new Intent(ServiceDemandeDotationPADActivity.this, DepotSelecteurSimpleActivity.class);
            serviceDemandeDotationPADIntent.putExtras(serviceDemandeDotationPADBundle);
            ServiceDemandeDotationPADActivity.this.startActivityForResult(serviceDemandeDotationPADIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
        }
        passageOnResume = true;
        vide = false;
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
                        serviceDemandeDotationPADBundle = ServiceDemandeDotationPADActivity.super.getBundle();
                        serviceDemandeDotationPADBundle.putInt("depotSelectionneID", depotUID_Selectionne);
                        serviceDemandeDotationPADIntent = new Intent(ServiceDemandeDotationPADActivity.this, ListeDotationPatientActivity.class);
                        serviceDemandeDotationPADIntent.putExtras(serviceDemandeDotationPADBundle);
                        ServiceDemandeDotationPADActivity.this.startActivity(serviceDemandeDotationPADIntent);
                    }
                } else {
                    ServiceDemandeDotationPADActivity.this.finish();
                }
                break;
            }
        }
    }
}