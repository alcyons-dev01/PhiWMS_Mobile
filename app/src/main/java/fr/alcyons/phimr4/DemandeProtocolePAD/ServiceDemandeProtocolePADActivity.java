package fr.alcyons.phimr4.DemandeProtocolePAD;

import android.content.Intent;
import android.os.Bundle;
import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurSimpleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by jessica on 09/10/2017.
 */

public class ServiceDemandeProtocolePADActivity extends ServiceActivity {

    Boolean passageOnResume;
    Intent serviceDemandeProtocolePADIntent;
    Bundle serviceDemandeProtocolePADBundle;

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
            serviceDemandeProtocolePADBundle = ServiceDemandeProtocolePADActivity.super.getBundle();
            serviceDemandeProtocolePADBundle.putString("depotType", "PAD");
            serviceDemandeProtocolePADBundle.putString("service", "Protocole PAD");
            serviceDemandeProtocolePADBundle.putBoolean("vide", vide);
            serviceDemandeProtocolePADIntent = new Intent(ServiceDemandeProtocolePADActivity.this, DepotSelecteurSimpleActivity.class);
            serviceDemandeProtocolePADIntent.putExtras(serviceDemandeProtocolePADBundle);
            ServiceDemandeProtocolePADActivity.this.startActivityForResult(serviceDemandeProtocolePADIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
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
                        serviceDemandeProtocolePADBundle = ServiceDemandeProtocolePADActivity.super.getBundle();
                        serviceDemandeProtocolePADBundle.putInt("depotSelectionneID", depotUID_Selectionne);

                        serviceDemandeProtocolePADIntent = new Intent(ServiceDemandeProtocolePADActivity.this, ListeProtocolePatientActivity.class);
                        serviceDemandeProtocolePADIntent.putExtras(serviceDemandeProtocolePADBundle);
                        ServiceDemandeProtocolePADActivity.this.startActivity(serviceDemandeProtocolePADIntent);
                    }
                } else {
                    ServiceDemandeProtocolePADActivity.this.finish();
                }
                break;
            }
        }
    }
}