package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 06/05/2019.
 */

public class EmplacementReceptionScanneeContexte {
    private Context context;
    private SQLiteDatabase db;

    public String bannerTexte;
    public String scannerContexteEmplacement;
    public String code;
    private boolean ADH;

    public EmplacementReceptionScanneeContexte(final Context context, final SQLiteDatabase db, boolean ADH) {
        this.context = context;
        this.db = db;
        bannerTexte = "Scanner un datamatrix d'un emplacement";
        scannerContexteEmplacement = String.valueOf(R.string.scannerContexteEmplacement);
        this.ADH = ADH;
    }

    public void onActivityResult(int requestCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    String codeRetourne = data.getStringExtra("code");
                    if (!codeRetourne.contentEquals(""))
                        code = codeRetourne;
                    break;
            }
        }
    }

    public boolean onTap(String chaine){
        if (chaine.startsWith("PhiR4:")) {
            chaine = chaine.substring(6, chaine.length());
        }
        List<Depot_Emplacement> depot_emplacements = EmplacementOpenHelper.getUnEmplacementParAdressage(db, chaine);
        boolean confirmation = true;

        if (depot_emplacements != null) {
            if(!ADH)
            {
                if (depot_emplacements.size() == 1) {
                    Depot_Emplacement depot_emplacement = depot_emplacements.get(0);
                    Depot_Zone depot_zone = ZoneOpenHelper.getUneZoneByID(db, depot_emplacement.getZoneID());
                    Depot depot = DepotOpenHelper.getDepotParID(db, depot_zone.getDepotID());


                    String messageAlerte = "Code trouvé :\n" + chaine;
                    messageAlerte += "Un code a été trouvé et correspond à l'emplacement suivant :\n";
                    messageAlerte += "Depot : " + depot.getNom() + "\n";
                    messageAlerte += "Zone : " + depot_zone.getZoneName() + "\n";
                    messageAlerte += "Emplacement : " + depot_emplacement.getAdressage() + "\n";
                    messageAlerte += "\n\nContinuer ?";

                    confirmation = Alerte.afficherAlerte(context, "Attention", messageAlerte, "OuiNon");

                    chaine = String.valueOf(depot_emplacement.get_UID());
                } else {
                    confirmation = Alerte.afficherAlerte(context, "Attention", "Code trouvé :\n" + chaine + "\nUn code a été trouvé et correspond à plusieurs emplacements. \n\nContinuer ?", "OuiNon");
                }
            }
        } else {
            confirmation = Alerte.afficherAlerte(context, "Attention", "Code trouvé :\n" + chaine + "\nUn code a été trouvé mais aucun emplacement n'y correspond. \n\nContinuer ?", "OuiNon");
        }
        code = chaine;
        return confirmation;
    }
}
