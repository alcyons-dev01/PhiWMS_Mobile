package fr.alcyons.phiwms_mobile.ZonesEtEmplacements;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import fr.alcyons.phiwms_mobile.DepotSelecteur.DepotSelecteurMultipleActivity;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.ServiceActivity;

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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodesEchangesActivites.RESULT_SELECTION_DEPOT) {
            if (data != null) {
                int depotUID_Selectionne = data.getIntExtra("depotUID_Selectionne", 0);
                if (depotUID_Selectionne != 0) {
                    passageOnResume = false;
                    Intent serviceZonesEtEmplacementsIntent = getServiceZonesEtEmplacementsIntent(depotUID_Selectionne);
                    ServiceZonesEtEmplacementsActivity.this.startActivity(serviceZonesEtEmplacementsIntent);
                }
            } else {
                ServiceZonesEtEmplacementsActivity.this.finish();
            }
        }
    }

    @NonNull
    private Intent getServiceZonesEtEmplacementsIntent(int depotUID_Selectionne) {
        Intent serviceZonesEtEmplacementsIntent = new Intent(ServiceZonesEtEmplacementsActivity.this, ListeZonesParDepotActivity.class);
        Bundle serviceZonesEtEmplacementsBundle = new Bundle();
        serviceZonesEtEmplacementsBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        serviceZonesEtEmplacementsBundle.putInt("serviceSelectionneID", serviceActuel.getId());
        serviceZonesEtEmplacementsBundle.putInt("depotSelectionneID", depotUID_Selectionne);
        serviceZonesEtEmplacementsIntent.putExtras(serviceZonesEtEmplacementsBundle);
        return serviceZonesEtEmplacementsIntent;
    }
}
