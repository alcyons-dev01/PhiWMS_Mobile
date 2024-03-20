package fr.alcyons.phimr4.DemandeDotationGlobale;

import android.content.Intent;
import android.os.Bundle;

import fr.alcyons.phimr4.DemandeReassort.ListeReassortServiceActivity;
import fr.alcyons.phimr4.DemandeReassort.ServiceDemandeReassortActivity;
import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurSimpleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;

public class ServiceDemandeDotationGlobaleActivity extends ServiceActivity {

    Boolean passageOnResume;
    Intent serviceDemandeReassortIntent;
    Bundle serviceDemandeReassortBundle;

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
            serviceDemandeReassortBundle = ServiceDemandeDotationGlobaleActivity.super.getBundle();
            serviceDemandeReassortBundle.putString("depotType", "PUF");
            serviceDemandeReassortBundle.putString("service", "Demande Reassort");
            serviceDemandeReassortBundle.putBoolean("vide", vide);
            serviceDemandeReassortIntent = new Intent(ServiceDemandeDotationGlobaleActivity.this, DepotSelecteurSimpleActivity.class);
            serviceDemandeReassortIntent.putExtras(serviceDemandeReassortBundle);
            ServiceDemandeDotationGlobaleActivity.this.startActivityForResult(serviceDemandeReassortIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
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
                        serviceDemandeReassortBundle = ServiceDemandeDotationGlobaleActivity.super.getBundle();
                        serviceDemandeReassortBundle.putInt("depotSelectionneID", depotUID_Selectionne);

                        serviceDemandeReassortIntent = new Intent(ServiceDemandeDotationGlobaleActivity.this, ListeReassortServiceActivity.class);
                        serviceDemandeReassortIntent.putExtras(serviceDemandeReassortBundle);
                        ServiceDemandeDotationGlobaleActivity.this.startActivity(serviceDemandeReassortIntent);
                    }
                } else {
                    ServiceDemandeDotationGlobaleActivity.this.finish();
                }
                break;
            }
        }
    }
}
