package fr.alcyons.phimr4.DemandeReassort;

import android.content.Intent;
import android.os.Bundle;

import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurSimpleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by jessica on 05/10/2017.
 */

public class ServiceDemandeReassortActivity extends ServiceActivity {

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
            serviceDemandeReassortBundle = ServiceDemandeReassortActivity.super.getBundle();
            serviceDemandeReassortBundle.putString("depotType", "PUF");
            serviceDemandeReassortBundle.putString("service", "Demande Reassort");
            serviceDemandeReassortBundle.putBoolean("vide", vide);
            serviceDemandeReassortIntent = new Intent(ServiceDemandeReassortActivity.this, DepotSelecteurSimpleActivity.class);
            serviceDemandeReassortIntent.putExtras(serviceDemandeReassortBundle);
            ServiceDemandeReassortActivity.this.startActivityForResult(serviceDemandeReassortIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
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
                        serviceDemandeReassortBundle = ServiceDemandeReassortActivity.super.getBundle();
                        serviceDemandeReassortBundle.putInt("depotSelectionneID", depotUID_Selectionne);

                        serviceDemandeReassortIntent = new Intent(ServiceDemandeReassortActivity.this, ListeReassortServiceActivity.class);
                        serviceDemandeReassortIntent.putExtras(serviceDemandeReassortBundle);
                        ServiceDemandeReassortActivity.this.startActivity(serviceDemandeReassortIntent);
                    }
                } else {
                    ServiceDemandeReassortActivity.this.finish();
                }
                break;
            }
        }
    }
}
