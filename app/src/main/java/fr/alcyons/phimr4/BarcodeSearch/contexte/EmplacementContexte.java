package fr.alcyons.phimr4.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.TableTraceOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.TableTrace;
import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.R;

/**
 * Created by jessica on 07/06/2018.
 */

public class EmplacementContexte {

    private Context context;
    private SQLiteDatabase db;

    public String bannerTexte;
    public String scannerContexteEmplacement;
    public String code;
    public Utilisateur utilisateur;
    private boolean ADH;
    boolean modeTrace;

    public EmplacementContexte(final Context context, final SQLiteDatabase db, boolean ADH, Utilisateur utilisateur) {
        this.context = context;
        this.db = db;
        this.modeTrace = ParametreUtilisateurOpenHelper.getModeTrace(db);
        this.utilisateur = utilisateur;
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
        if(modeTrace)
        {
            //int id, String date, String service, String situation, String codeRetourne, String user, int userID
            Random random = new Random();
            int id = random.nextInt();
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            TableTrace tableTrace = new TableTrace(id, date, "Context_zone", "Récupération après scan", chaine, utilisateur.getIdentifiant(), utilisateur.getId());
            long rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
            if(rowId != -1)
            {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            }
        }
        if (chaine.startsWith("PHITAGPLACE+")) {
            String[] tabchaine = chaine.split(":");
            chaine = tabchaine[1];
        }

        try
        {
            Integer.parseInt(chaine);
        }
        catch (NumberFormatException e)
        {
            chaine = "";
        }
        boolean confirmation = false;

        if(!chaine.contentEquals(""))
        {
            Depot_Emplacement depot_emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, Integer.parseInt(chaine));

            if (depot_emplacement != null) {
                /*if(!ADH)
                {
                    if (depot_emplacement != null) {
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
                }*/
                confirmation = true;
            } else {
                confirmation = Alerte.afficherAlerte(context, "Attention", "Code trouvé :\n" + chaine + "\nUn code a été trouvé mais aucun emplacement n'y correspond. \n\nContinuer ?", "OuiNon");
            }
            code = chaine;
        }
        return confirmation;
    }
}
