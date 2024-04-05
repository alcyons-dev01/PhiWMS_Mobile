package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DotationOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by jessica on 07/06/2018.
 */

public class PleinVideLocalisationContexte {

    private Context context;
    private SQLiteDatabase db;
    private TextView message;

    public String bannerTexte;
    public String scannerContextePleinVideLocalisation;
    public String code;

    public PleinVideLocalisationContexte(final Context context, final SQLiteDatabase db, TextView message){
        this.context = context;
        this.db = db;
        this.message = message;

        bannerTexte = "Scanner un datamatrix Localisation";
        scannerContextePleinVideLocalisation = String.valueOf(R.string.scannerContextePleinVideLocalisation);
        code = "";
    }

    public void onTextWatcher(final Editable s){
        String messageTexte;
        int messageColor;
        String PHITAGLOCALISATION = s.toString();
        if(PHITAGLOCALISATION.contains("\n"))
            PHITAGLOCALISATION = PHITAGLOCALISATION.substring(0, s.length() - 1);

        String dotationId = PHITAGLOCALISATION.replace("PHITAGLOCALISATION_", "");

        Dotation dotation = DotationOpenHelper.getDotationPleinByStringId(db, dotationId);

        if (dotation == null) {
            messageTexte = "Localisation inconnue";
            messageColor = Color.RED;
        }
        else{
            boolean confirmation = Alerte.afficherAlerte(context, "Confirmation", "Vous allez réaliser une Demande PleinVide pour "+ dotation.getIntitulé() +" \n\nContinuer ?", "OuiNon");
            if(confirmation){
                messageTexte = "Localisation trouvée";
                messageColor = Color.GREEN;
                code = PHITAGLOCALISATION;
            }
            else{
                messageTexte = "Mauvaise localisation";
                messageColor = context.getResources().getColor(R.color.orange);
            }

        }
        message.setVisibility(View.VISIBLE);
        message.setText(messageTexte);
        message.setBackgroundColor(messageColor);
    }

    public void onActivityResult(int requestCode, Intent data){
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION:
                    String codeRetourne = data.getStringExtra("code");
                    if (!codeRetourne.contentEquals(""))
                        code = codeRetourne;
                    break;
            }
        }
    }
}
