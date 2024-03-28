package com.example.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.example.phiwms_mobile.BaseDeDonnees.DotationOpenHelper;
import com.example.phiwms_mobile.Classes.Dotation;
import com.example.phiwms_mobile.Outils.Alerte;
import com.example.phiwms_mobile.Outils.CodesEchangesActivites;
import com.example.phiwms_mobile.R;

/**
 * Created by jessica on 07/06/2018.
 */

public class PleinVideContexte {

    private Context context;
    private TextView message;
    private ToneGenerator toneGen1;

    public String scannerContextePleinVide;

    public List<String> detailDotPleinVide_AdressageList;
    public List<String> stringList;
    public List<String> referenceList;
    public List<String> detailDotSupprime;
    public HashMap<String, String> mapPleinVide;

    public String dotationId;

    Boolean aSupprime;

    public PleinVideContexte(final Context context, TextView message){
        this.context = context;
        this.message = message;
       // toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        scannerContextePleinVide = String.valueOf(R.string.scannerContextePleinVide);

        detailDotPleinVide_AdressageList = new ArrayList<>();
        stringList = new ArrayList<>();
        detailDotSupprime = new ArrayList<>();
        referenceList = new ArrayList<>();
        mapPleinVide = new HashMap<>();

        aSupprime = false;

        dotationId = "";
    }

    public void onTextWatcher(final Editable s){
        String messageTexte;
        int messageColor;

        String scanne = s.toString();
        if(scanne.contains("\n"))
            scanne = scanne.substring(0, s.length() - 1);

        if(scanne.contains("PHITAGACTION_")){
           // toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
            String action = scanne.replace("PHITAGACTION_","");
            messageTexte = "Action " + action;
            messageColor = context.getResources().getColor(R.color.bleu_clair_alcyons);
            if(action.contains("Supprimer"))
                aSupprime = true;
            else if(action.contains("Valider"))
                stringList.add(scanne);
        }
        else if(scanne.toUpperCase().contains("PHITAGPVREF:"))
        {
            String codescanner = scanne.toUpperCase().replace("PHITAGPVREF:","");
            if(detailDotSupprime.indexOf(codescanner)!=-1){
                messageTexte = "Produit déjà supprimé";
                messageColor = context.getResources().getColor(R.color.orange);
            }
            else if(stringList.indexOf(codescanner)!=-1){
                messageTexte = "Produit déjà ajouté";
                messageColor = context.getResources().getColor(R.color.orange);

                if(aSupprime){
                    aSupprime = false;
                    detailDotSupprime.add(codescanner);
                    stringList.remove(stringList.indexOf(codescanner));
                    messageTexte = "Produit supprimé";
                    messageColor = Color.RED;
                }
            }
            else{
                stringList.add(codescanner);
                String designation = mapPleinVide.get(codescanner);
                if(!referenceList.contains(designation))
                {
                    referenceList.add(designation);
                }
                //  toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                messageTexte = "Produit ajouté";
                messageColor = Color.GREEN;
            }
        }
        else if(detailDotPleinVide_AdressageList.contains(scanne)){
            if(detailDotSupprime.contains(scanne)){
                messageTexte = "Produit déjà supprimé";
                messageColor = context.getResources().getColor(R.color.orange);
            }
            else if(stringList.contains(scanne)){
                messageTexte = "Produit déjà ajouté";
                messageColor = context.getResources().getColor(R.color.orange);

                if(aSupprime){
                    aSupprime = false;
                    detailDotSupprime.add(scanne);
                    stringList.remove(stringList.indexOf(scanne));
                    messageTexte = "Produit supprimé";
                    messageColor = Color.RED;
                }
            }
            else{
                stringList.add(scanne);
                String designation = mapPleinVide.get(scanne);
                if(!referenceList.contains(designation))
                {
                    referenceList.add(designation);
                }
              //  toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                messageTexte = "Produit ajouté";
                messageColor = Color.GREEN;
            }
        }
        else if(scanne.contains("PHITAGLOCALISATION_"))
        {
            messageTexte = "";
            messageColor = Color.GREEN;
            dotationId = scanne.replace("PHITAGLOCALISATION_", "");
        }
        else{
           /* messageTexte = "Produit inconnu";
            messageColor = Color.RED;*/
            if(detailDotSupprime.indexOf(scanne)!=-1){
                messageTexte = "Produit déjà supprimé";
                messageColor = context.getResources().getColor(R.color.orange);
            }
            else if(stringList.indexOf(scanne)!=-1){
                messageTexte = "Produit déjà ajouté";
                messageColor = context.getResources().getColor(R.color.orange);

                if(aSupprime){
                    aSupprime = false;
                    detailDotSupprime.add(scanne);
                    stringList.remove(stringList.indexOf(scanne));
                    messageTexte = "Produit supprimé";
                    messageColor = Color.RED;
                }
            }
            else{
                stringList.add(scanne);
                String designation = mapPleinVide.get(scanne);
                if(!referenceList.contains(designation))
                {
                    referenceList.add(designation);
                }
                //  toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                messageTexte = "Produit ajouté";
                messageColor = Color.GREEN;
            }
        }

        message.setVisibility(View.VISIBLE);
        message.setText(messageTexte);
        message.setBackgroundColor(messageColor);
    }

    public void onActivityResult(int requestCode, Intent data){
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_CODE_ADRESSAGE:
                    stringList.clear();
                    List<String> stringRetourList = data.getExtras().getStringArrayList("stringList");
                    stringList.addAll(stringRetourList);
                    break;
            }
        }
    }
}
