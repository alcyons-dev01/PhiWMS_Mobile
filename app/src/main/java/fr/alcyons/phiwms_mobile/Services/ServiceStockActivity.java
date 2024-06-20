package fr.alcyons.phiwms_mobile.Services;

import android.content.Intent;
import android.os.Bundle;

import fr.alcyons.phiwms_mobile.DepotSelecteur.DepotSelecteurMultipleActivity;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Stock.ListeReferenceActivity;

public class ServiceStockActivity extends ServiceActivity {

    Boolean passageOnResume;
    Intent serviceStockIntent;
    Bundle serviceStockBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passageOnResume = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (passageOnResume) {
            serviceStockIntent = new Intent(ServiceStockActivity.this, DepotSelecteurMultipleActivity.class);
            serviceStockBundle = ServiceStockActivity.super.getBundle();
            serviceStockBundle.putString("depotType", "tous");
            serviceStockIntent.putExtras(serviceStockBundle);
            ServiceStockActivity.this.startActivityForResult(serviceStockIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
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
                        //serviceStockIntent = new Intent(ServiceStockActivity.this, ListeStockActivity.class);
                        serviceStockIntent = new Intent(ServiceStockActivity.this, ListeReferenceActivity.class);
                        serviceStockBundle = ServiceStockActivity.super.getBundle();
                        serviceStockBundle.putInt("depotUID_Selectionne", depotUID_Selectionne);
                        serviceStockIntent.putExtras(serviceStockBundle);
                        ServiceStockActivity.this.startActivity(serviceStockIntent);
                    }
                } else {
                    ServiceStockActivity.this.finish();
                }
                break;
            }
        }
    }
}
