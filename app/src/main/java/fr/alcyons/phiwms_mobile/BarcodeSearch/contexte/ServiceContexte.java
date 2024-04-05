package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;

import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 11/04/2019.
 */

public class ServiceContexte {

    private Context context;
    private SQLiteDatabase db;

    public String bannerTexte;
    public String scannerContextService;
    public String code = "";

    public ServiceContexte(final Context context, final SQLiteDatabase db) {
        this.context = context;
        this.db = db;
        bannerTexte = "Scanner un datamatrix d'un service";
        scannerContextService = String.valueOf(R.string.scannerContexteService);
    }

    public void onTextWatcher(final Editable s){
        String chaine = s.toString();

        code = gestionPrefixe(chaine);
    }

    public void onActivityResult(int requestCode, Intent data){
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_SERVICE:
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
        code = gestionPrefixe(chaine);
        return true;
    }

    public String gestionPrefixe(String chaine)
    {
        String[] tab_chaine = chaine.split(":");
        if(tab_chaine.length > 1)
        {
            chaine = tab_chaine[1];
            String last_char = chaine.substring(chaine.length()-1);
            if(last_char.contentEquals("\n"))
                chaine = chaine.substring(0, chaine.length()-1);
        }
        return chaine;
    }
}
