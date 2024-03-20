package fr.alcyons.phimr4.ZonesEtEmplacements;

import android.content.Intent;
import android.os.Bundle;

import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurMultipleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;

public class ServiceZonesEtEmplacementsActivity extends ServiceActivity {

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
            Intent serviceZonesEtEmplacementsIntent = new Intent(ServiceZonesEtEmplacementsActivity.this, DepotSelecteurMultipleActivity.class);
            Bundle serviceZonesEtEmplacementsBundle = ServiceZonesEtEmplacementsActivity.super.getBundle();
            serviceZonesEtEmplacementsBundle.putString("depotType", "all");
            serviceZonesEtEmplacementsIntent.putExtras(serviceZonesEtEmplacementsBundle);
            ServiceZonesEtEmplacementsActivity.this.startActivityForResult(serviceZonesEtEmplacementsIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
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
                        Intent serviceZonesEtEmplacementsIntent = new Intent(ServiceZonesEtEmplacementsActivity.this, ListeZonesParDepotActivity.class);
                        Bundle serviceZonesEtEmplacementsBundle = new Bundle();
                        // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                        serviceZonesEtEmplacementsBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        serviceZonesEtEmplacementsBundle.putInt("serviceSelectionneID", serviceActuel.getId());
                        serviceZonesEtEmplacementsBundle.putInt("depotSelectionneID", depotUID_Selectionne);
                        serviceZonesEtEmplacementsIntent.putExtras(serviceZonesEtEmplacementsBundle);
                        ServiceZonesEtEmplacementsActivity.this.startActivity(serviceZonesEtEmplacementsIntent);
                    }
                } else {
                    ServiceZonesEtEmplacementsActivity.this.finish();
                }
                break;
            }
        }
    }
}
