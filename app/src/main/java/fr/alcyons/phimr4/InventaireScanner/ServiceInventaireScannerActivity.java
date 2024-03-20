package fr.alcyons.phimr4.InventaireScanner;

import android.content.Intent;
import android.os.Bundle;

import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurMultipleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;

public class ServiceInventaireScannerActivity extends ServiceActivity {

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
            Intent serviceInventaireScannerIntent = new Intent(ServiceInventaireScannerActivity.this, DepotSelecteurMultipleActivity.class);
            Bundle serviceInventaireScannerBundle = ServiceInventaireScannerActivity.super.getBundle();
            serviceInventaireScannerBundle.putString("depotType", "tous");
            serviceInventaireScannerIntent.putExtras(serviceInventaireScannerBundle);
            ServiceInventaireScannerActivity.this.startActivityForResult(serviceInventaireScannerIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
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
                        Intent serviceInventaireScannerIntent = new Intent(ServiceInventaireScannerActivity.this, DetailInventaireScannerActivity.class);
                        Bundle serviceInventaireScannerBundle = ServiceInventaireScannerActivity.super.getBundle();
                        // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                        serviceInventaireScannerBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        serviceInventaireScannerBundle.putInt("serviceSelectionneID", serviceActuel.getId());
                        serviceInventaireScannerBundle.putInt("depotSelectionneID", depotUID_Selectionne);
                        serviceInventaireScannerIntent.putExtras(serviceInventaireScannerBundle);
                        ServiceInventaireScannerActivity.this.startActivity(serviceInventaireScannerIntent);
                    }
                } else {
                    ServiceInventaireScannerActivity.this.finish();
                }
                break;
            }
        }
    }
}
