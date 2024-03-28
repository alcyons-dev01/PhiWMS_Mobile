package com.example.phiwms_mobile.BarcodeSearch.contexte;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.phiwms_mobile.AuthentificationActivity;
import com.example.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import com.example.phiwms_mobile.Outils.Alerte;
import com.example.phiwms_mobile.Outils.CodesEchangesActivites;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 10/04/2019.
 */

public class AuthentificationContext {

    private Context context;
    private SQLiteDatabase db;

    public String bannerTexte;
    public String scannerContextAuthentification;
    public String code;
    public String username;
    public String password;

    public AuthentificationContext(final Context context, final SQLiteDatabase db) {
        this.context = context;
        this.db = db;
        bannerTexte = "Scanner un datamatrix d'authentification";
        scannerContextAuthentification = String.valueOf(R.string.scannerContexteAuthentification);
    }

    public void onTextWatcher(final Editable s){
        String chaine = s.toString();
        String fin_de_chaine = chaine.substring(chaine.length()-1);
        if(fin_de_chaine.contentEquals("\n"))
            chaine = chaine.substring(0, chaine.length()-1);
        String[] tab_chaine = chaine.split("PHITAGMDP:");
        if(tab_chaine.length == 2)
        {
            String[] tab_username = tab_chaine[0].split(":");
            code = chaine;
            username = tab_username[1];
            password = tab_chaine[1];
        }
        else
        {
            //Alerte.afficherAlerte(context, "Erreur", "Le datamatrix scanné n'est pas un datamatrix d'authentification", "alerte");
            afficherAlerteErreur(context, LayoutInflater.from(context));
        }
    }

    public void onActivityResult(int requestCode, Intent data){
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_AUTHENTIFICATION:
                    String codeRetourne = data.getStringExtra("code");
                    if(!codeRetourne.contentEquals(""))
                    {
                        String fin_de_chaine = codeRetourne.substring(codeRetourne.length()-1);
                        if(fin_de_chaine.contentEquals("\n"))
                            codeRetourne = codeRetourne.substring(0, codeRetourne.length()-1);
                        String[] tab_chaine = codeRetourne.split("PHITAGMDP:");
                        if(tab_chaine.length == 2)
                        {
                            String[] tab_username = tab_chaine[0].split(":");
                            username = tab_username[1];
                            password = tab_chaine[1];
                        }
                        else
                        {
                            //Alerte.afficherAlerte(context, "Erreur", "Le datamatrix scanné n'est pas un datamatrix d'authentification", "alerte");
                            afficherAlerteErreur(context, LayoutInflater.from(context));
                        }
                    }
                    break;
            }
        }
    }

    public boolean onTap(String chaine){
        String fin_de_chaine = chaine.substring(chaine.length()-1);
        if(fin_de_chaine.contentEquals("\n"))
            chaine = chaine.substring(0, chaine.length()-1);
        String[] tab_chaine = chaine.split("PHITAGMDP:");
        if(tab_chaine.length == 2)
        {
            String[] tab_username = tab_chaine[0].split(":");
            username = tab_username[1];
            password = tab_chaine[1];
        }
        else
        {
            //Alerte.afficherAlerte(context, "Erreur", "Le datamatrix scanné n'est pas un datamatrix d'authentification", "alerte");
            afficherAlerteErreur(context, LayoutInflater.from(context));
        }
        return true;
    }

    public void afficherAlerteErreur(Context context, LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation, null);

        ImageView buttonOk = (ImageView) layout.findViewById(R.id.buttonOk);
        TextView messageFin = (TextView) layout.findViewById(R.id.messageFin);
        TextView titre = (TextView) layout.findViewById(R.id.titre);

        titre.setText("Erreur d'authentification");
        messageFin.setText("Le datamatrix scanné n'est pas un datamatrix d'authentification");
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

}
