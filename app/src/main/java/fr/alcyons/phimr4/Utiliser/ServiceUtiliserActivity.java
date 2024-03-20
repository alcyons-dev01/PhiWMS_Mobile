package fr.alcyons.phimr4.Utiliser;

import android.content.Intent;
import android.os.Bundle;

import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurSimpleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;

public class ServiceUtiliserActivity extends ServiceActivity {

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
            Bundle serviceUtiliser_Bundle = ServiceUtiliserActivity.super.getBundle();
            serviceUtiliser_Bundle.putString("depotType", "PUF");

            Intent serviceUtiliser_Intent = new Intent(ServiceUtiliserActivity.this, DepotSelecteurSimpleActivity.class);
            serviceUtiliser_Intent.putExtras(serviceUtiliser_Bundle);
            ServiceUtiliserActivity.this.startActivityForResult(serviceUtiliser_Intent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
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
                        Bundle serviceUtiliser_Bundle = new Bundle();
                        // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                        serviceUtiliser_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        serviceUtiliser_Bundle.putInt("serviceSelectionneID", serviceActuel.getId());

                        serviceUtiliser_Bundle.putInt("depotSelectionneID", depotUID_Selectionne);

                        Intent serviceUtiliser_Intent = new Intent(ServiceUtiliserActivity.this, DetailUtiliserActivity.class);

                        serviceUtiliser_Intent.putExtras(serviceUtiliser_Bundle);
                        ServiceUtiliserActivity.this.startActivity(serviceUtiliser_Intent);
                    }
                } else {
                    ServiceUtiliserActivity.this.finish();
                }
                break;
            }
        }
    }
}
