package fr.alcyons.phimr4.DemandeParticuliere;

import android.content.Intent;
import android.os.Bundle;

import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurMultipleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;


/**
 * Created by olivier on 02/10/2017.
 */

public class ServiceDemandeParticuliereActivity extends ServiceActivity {

    Boolean passageOnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passageOnResume = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (passageOnResume) {
            Bundle serviceDemandeParticuliere_Bundle = ServiceDemandeParticuliereActivity.super.getBundle();
            serviceDemandeParticuliere_Bundle.putString("depotType", "pufpad");

            Intent serviceDemandeParticuliere_Intent = new Intent(ServiceDemandeParticuliereActivity.this, DepotSelecteurMultipleActivity.class);
            serviceDemandeParticuliere_Intent.putExtras(serviceDemandeParticuliere_Bundle);
            ServiceDemandeParticuliereActivity.this.startActivityForResult(serviceDemandeParticuliere_Intent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
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
                        Bundle serviceDemandeParticuliere_Bundle = ServiceDemandeParticuliereActivity.super.getBundle();
                        serviceDemandeParticuliere_Bundle.putInt("depotSelectionneID", depotUID_Selectionne);

                        Intent serviceDemandeParticuliere_Intent = new Intent(ServiceDemandeParticuliereActivity.this, ListeProduitActivity.class);
                        serviceDemandeParticuliere_Intent.putExtras(serviceDemandeParticuliere_Bundle);
                        ServiceDemandeParticuliereActivity.this.startActivity(serviceDemandeParticuliere_Intent);
                    }
                } else {
                    ServiceDemandeParticuliereActivity.this.finish();
                }

                break;
            }
        }
    }
}
