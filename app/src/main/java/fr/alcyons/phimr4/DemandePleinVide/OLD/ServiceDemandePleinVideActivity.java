package fr.alcyons.phimr4.DemandePleinVide.OLD;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurSimpleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by jessica on 31/01/2018.
 */

public class ServiceDemandePleinVideActivity extends ServiceActivity {
    Boolean passageOnResume;
    Intent serviceDemandePleinVideIntent;
    Bundle serviceDemandePleinVideBundle;
    List<String> depotReferenceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passageOnResume = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        depotReferenceList = new ArrayList<>();
        depotReferenceList = gestionnaireDotation.getDepotDotationPleinVide(db);


        if (depotReferenceList.size() != 0) {
            if (passageOnResume) {
                serviceDemandePleinVideBundle = ServiceDemandePleinVideActivity.super.getBundle();
                serviceDemandePleinVideBundle.putString("depotType", "PUF");
                serviceDemandePleinVideBundle.putBoolean("listeReduite", true);
                serviceDemandePleinVideBundle.putStringArrayList("depotReferenceList", (ArrayList) depotReferenceList);
                serviceDemandePleinVideIntent = new Intent(ServiceDemandePleinVideActivity.this, DepotSelecteurSimpleActivity.class);
                serviceDemandePleinVideIntent.putExtras(serviceDemandePleinVideBundle);
                ServiceDemandePleinVideActivity.this.startActivityForResult(serviceDemandePleinVideIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
            }
            passageOnResume = true;
        } else {
            vide = true;
            nomServiceVide = "Dotation Plein Vide";
            ServiceDemandePleinVideActivity.this.finish();
        }
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RESULT_SELECTION_DEPOT: {
                if (data != null) {
                    int depotUID_Selectionne = data.getExtras().getInt("depotUID_Selectionne");
                    if (depotUID_Selectionne != 0) {
                        passageOnResume = false;
                        serviceDemandePleinVideBundle = ServiceDemandePleinVideActivity.super.getBundle();
                        serviceDemandePleinVideBundle.putInt("depotUID_Selectionne", depotUID_Selectionne);

                        serviceDemandePleinVideIntent = new Intent(ServiceDemandePleinVideActivity.this, ListeDotationPleinVideActivity.class);
                        serviceDemandePleinVideIntent.putExtras(serviceDemandePleinVideBundle);
                        ServiceDemandePleinVideActivity.this.startActivity(serviceDemandePleinVideIntent);
                    }
                } else {
                    ServiceDemandePleinVideActivity.this.finish();
                }
                break;
            }
        }
    }
}
