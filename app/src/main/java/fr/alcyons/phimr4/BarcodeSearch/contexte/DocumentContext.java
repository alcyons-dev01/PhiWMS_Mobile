package fr.alcyons.phimr4.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Editable;
import android.view.View;

import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.R;

/**
 * Created by olivier on 09/04/2019.
 */

public class DocumentContext {
    private Context context;
    private SQLiteDatabase db;

    public String bannerTexte;
    public String scannerContextDocument;
    public String code;

    public DocumentContext(final Context context, final SQLiteDatabase db) {
        this.context = context;
        this.db = db;
        bannerTexte = "Scanner un datamatrix d'un document";
        scannerContextDocument = String.valueOf(R.string.scannerContexteDocument);
    }

    public void onTextWatcher(final Editable s){
        String chaine = s.toString();
        code = "";
        for(int i = 0; i < chaine.length(); i++)
        {
            char caractere_courant = chaine.charAt(i);
            int ascii_caract_courant = (int) caractere_courant;
            if(ascii_caract_courant < 48)
            {
                caractere_courant= ' ';
            }
            else if(ascii_caract_courant > 57 && ascii_caract_courant < 65)
            {
                caractere_courant = ' ';
            }
            else if(ascii_caract_courant > 90 && ascii_caract_courant < 97)
            {
                caractere_courant= ' ';
            }
            else if(ascii_caract_courant > 122)
            {
                caractere_courant= ' ';
            }
            else
            {
                code = code+caractere_courant;
            }
        }
        code = gestionPrefixe(code);
    }

    public void onActivityResult(int requestCode, Intent data){
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_DOCUMENT:
                    String codeRetourne = data.getStringExtra("code");
                    if (!codeRetourne.contentEquals(""))
                    {
                        code = "";
                        for(int i = 0; i < codeRetourne.length(); i++)
                        {
                            char caractere_courant = codeRetourne.charAt(i);
                            int ascii_caract_courant = (int) caractere_courant;
                            if(ascii_caract_courant < 48)
                            {
                                caractere_courant= ' ';
                            }
                            else if(ascii_caract_courant > 57 && ascii_caract_courant < 65)
                            {
                                caractere_courant = ' ';
                            }
                            else if(ascii_caract_courant > 90 && ascii_caract_courant < 97)
                            {
                                caractere_courant= ' ';
                            }
                            else if(ascii_caract_courant > 122)
                            {
                                caractere_courant= ' ';
                            }
                            else
                            {
                                code = code+caractere_courant;
                            }
                        }
                        code = gestionPrefixe(code);
                    }

                    break;
            }
        }
    }

    public boolean onTap(String chaine){
        code = "";
        for(int i = 0; i < chaine.length(); i++)
        {
            char caractere_courant = chaine.charAt(i);
            int ascii_caract_courant = (int) caractere_courant;
            if(ascii_caract_courant < 48)
            {
                caractere_courant= ' ';
            }
            else if(ascii_caract_courant > 57 && ascii_caract_courant < 65)
            {
                caractere_courant = ' ';
            }
            else if(ascii_caract_courant > 90 && ascii_caract_courant < 97)
            {
                caractere_courant= ' ';
            }
            else if(ascii_caract_courant > 122)
            {
                caractere_courant= ' ';
            }
            else
            {
                code = code+caractere_courant;
            }
        }

        code = gestionPrefixe(code);
        return true;
    }

    public String gestionPrefixe(String chaine)
    {
        String[] tab_chaine = null;
        if(chaine.toUpperCase().startsWith("PHIBCF"))
        {
            tab_chaine = chaine.toUpperCase().split("PHIBCF");
        }
        else if(chaine.toUpperCase().startsWith("DDS"))
        {
            tab_chaine = chaine.toUpperCase().split("DDS");
        }
        else
        {
            tab_chaine = chaine.toUpperCase().split("PHIBCF");
        }
        if(tab_chaine.length == 1)
        {
            tab_chaine = chaine.split("\\*");
            if(tab_chaine.length == 1)
            {
                tab_chaine = chaine.split("PHI");
                if(tab_chaine.length != 1)
                {
                    chaine = tab_chaine[1];
                }
            }
            else
            {
                chaine = tab_chaine[1];
            }
        }
        else
        {
            chaine = tab_chaine[1];
        }
        return chaine;
    }
}
