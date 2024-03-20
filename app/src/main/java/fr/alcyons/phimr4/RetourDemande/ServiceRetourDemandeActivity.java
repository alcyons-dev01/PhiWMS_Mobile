package fr.alcyons.phimr4.RetourDemande;

import android.content.Intent;
import android.os.Bundle;

import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.SYS_User_RulesOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.SYS_User_Rules;
import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurMultipleActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by olivier on 05/01/2018.
 */

public class ServiceRetourDemandeActivity extends ServiceActivity {

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

        if(passageOnResume)
        {
            SYS_User_Rules sys_user_rules = SYS_User_RulesOpenHelper.getSYS_User_RulesByUser(db, utilisateurConnecte.getId());
            String depotType = "";
            String localisationParDefaut = sys_user_rules.getLocalisationParDefaut();
            if(localisationParDefaut.contentEquals(""))
            {
                depotType = "pufpad";
            }
            else if(localisationParDefaut.contains("@-PAD-@"))
            {
                depotType = "pad";
            }
            else if(localisationParDefaut.contains("@-PUF-@"))
            {
                depotType = "puf";
            }
            else if(localisationParDefaut.contains("@-PUI"))
            {
                depotType = "pufpad";
            }

            if(depotType.contentEquals(""))
            {
                Depot depot = DepotOpenHelper.getDepotByNom(db, localisationParDefaut);
                if(depot != null)
                {
                    serviceStockIntent = new Intent(ServiceRetourDemandeActivity.this, ListeProduitActivity.class);
                    serviceStockBundle = ServiceRetourDemandeActivity.super.getBundle();
                    serviceStockBundle.putInt("depotUID_Selectionne", depot.getDepot_UID());
                    serviceStockIntent.putExtras(serviceStockBundle);
                    ServiceRetourDemandeActivity.this.startActivity(serviceStockIntent);
                }
                else
                {
                    depotType = "pufpad";
                    serviceStockIntent = new Intent(ServiceRetourDemandeActivity.this, DepotSelecteurMultipleActivity.class);
                    serviceStockBundle = ServiceRetourDemandeActivity.super.getBundle();
                    serviceStockBundle.putString("depotType", depotType);
                    serviceStockIntent.putExtras(serviceStockBundle);
                    ServiceRetourDemandeActivity.this.startActivityForResult(serviceStockIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
                }
            }
            else
            {
                serviceStockIntent = new Intent(ServiceRetourDemandeActivity.this, DepotSelecteurMultipleActivity.class);
                serviceStockBundle = ServiceRetourDemandeActivity.super.getBundle();
                serviceStockBundle.putString("depotType", depotType);
                serviceStockIntent.putExtras(serviceStockBundle);
                ServiceRetourDemandeActivity.this.startActivityForResult(serviceStockIntent, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
            }
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
                        serviceStockIntent = new Intent(ServiceRetourDemandeActivity.this, ListeProduitActivity.class);
                        serviceStockBundle = ServiceRetourDemandeActivity.super.getBundle();
                        serviceStockBundle.putInt("depotUID_Selectionne", depotUID_Selectionne);
                        serviceStockIntent.putExtras(serviceStockBundle);
                        ServiceRetourDemandeActivity.this.startActivity(serviceStockIntent);
                    }
                } else {
                    ServiceRetourDemandeActivity.this.finish();
                }
                break;
            }
        }
    }
}
