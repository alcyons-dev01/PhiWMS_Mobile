package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.TableTraceOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.TableTrace;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by olivier on 27/06/2019.
 */

public class ZoneEtEmplacementContext {

    private Context context;
    private SQLiteDatabase db;

    public String bannerTexte;
    public String scannerContextZoneEtEmplacement;
    public String code;
    public Utilisateur utilisateur;
    boolean modeTrace;

    public ZoneEtEmplacementContext(final Context context, final SQLiteDatabase db, final Utilisateur utilisateur) {
        this.context = context;
        this.db = db;
        this.utilisateur = utilisateur;
        this.modeTrace = ParametreUtilisateurOpenHelper.getModeTrace(db);
        bannerTexte = "Scanner un datamatrix d'une zone";
        scannerContextZoneEtEmplacement = String.valueOf(R.string.scannerContexteZoneEtEmplacement);
    }

    public void onTextWatcher(final Editable s){
        if(modeTrace)
        {
            Random random = new Random();
            int id = random.nextInt();
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            TableTrace tableTrace = new TableTrace(id, date, "Context_zone", "Récupération après scan", s.toString(), utilisateur.getIdentifiant(), utilisateur.getId());
            long rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
            if(rowId != -1)
            {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getphiwms_mobileUUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            }
        }
        code = gestionPrefixe(s.toString());
    }

    public void onActivityResult(int requestCode, Intent data){
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    String codeRetourne = data.getStringExtra("code");
                    if (!codeRetourne.contentEquals(""))
                    {
                        code = codeRetourne;
                    }

                    break;
            }
        }
    }

    public boolean onTap(String chaine){
        //int id, String date, String service, String situation, String codeRetourne, String user, int userID
        Random random = new Random();
        int id = random.nextInt();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        if(modeTrace)
        {
            TableTrace tableTrace = new TableTrace(id, date, "Context_zone", "Récupération après scan", chaine, utilisateur.getIdentifiant(), utilisateur.getId());
            long rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
            if(rowId != -1)
            {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getphiwms_mobileUUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            }
        }
        code = gestionPrefixe(chaine);
        return true;
    }

    public String gestionPrefixe(String chaine)
    {
        String[] tab_chaine = chaine.split(":");
        chaine = tab_chaine[1];
        String last_char = chaine.substring(chaine.length()-1);
        if(last_char.contentEquals("\n"))
            chaine = chaine.substring(0, chaine.length()-1);
        return chaine;
    }
}
