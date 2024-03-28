package com.example.phiwms_mobile.ListViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import com.example.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import com.example.phiwms_mobile.InventaireScanner.DetailInventaireScannerActivity;
import com.example.phiwms_mobile.Outils.Alerte;
import com.example.phiwms_mobile.R;

import static com.example.phiwms_mobile.Outils.Alerte.aNumberPicker;

/**
 * Created by quentinlanusse on 17/08/2017.
 */

public class Inventaire_Ligne_TempAdapter extends ArrayAdapter {
    public SQLiteDatabase db;
    public List<Inventaire_Ligne_Temp> inventaireLigneTempList;
    public List<InventaireLigneTempViewHolder> viewHolderList;
    Context context;

    public Inventaire_Ligne_TempAdapter(Context context, SQLiteDatabase db, List<Inventaire_Ligne_Temp> inventaireLigneTemps) {
        super(context, 0, inventaireLigneTemps);
        this.context = context;
        this.db = db;
        this.inventaireLigneTempList = inventaireLigneTemps;

        viewHolderList = new ArrayList<>();
        for (Inventaire_Ligne_Temp inventaireLigneTemp : inventaireLigneTempList
                ) {
            viewHolderList.add(new InventaireLigneTempViewHolder());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View currentFocus = ((Activity) context).getCurrentFocus();

        final Inventaire_Ligne_Temp inventaireLigneTempCourant = (Inventaire_Ligne_Temp) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_inventaire_ligne_temp, parent, false);
        }

        InventaireLigneTempViewHolder viewHolder = (InventaireLigneTempViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = viewHolderList.get(inventaireLigneTempList.indexOf(inventaireLigneTempCourant));
            convertView.setTag(viewHolder);
        }
        // Récupération des objets graphiques
        viewHolder.designation = (TextView) convertView.findViewById(R.id.designationProduit);
        viewHolder.qte = (TextView) convertView.findViewById(R.id.qteSaisie);
        viewHolder.numLot = (TextView) convertView.findViewById(R.id.numLot);
        viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
        viewHolder.emplacement = (TextView) convertView.findViewById(R.id.nomEmplacement);
        viewHolder.zone = (TextView) convertView.findViewById(R.id.nomZone);
        viewHolder.boutonSuppression = (ImageView) convertView.findViewById(R.id.boutonSuppression);
        viewHolder.boutonModif = (ImageView) convertView.findViewById(R.id.boutonModif);

        // Affichage des valeurs
        viewHolder.designation.setText(inventaireLigneTempCourant.getDesignation());
        viewHolder.qte.setText(String.valueOf((int) inventaireLigneTempCourant.getStockPhysique()));
        viewHolder.numLot.setText(inventaireLigneTempCourant.getLot());

        viewHolder.zone.setText(inventaireLigneTempCourant.getZone());
        viewHolder.emplacement.setText(inventaireLigneTempCourant.getEmplacement());

        Date date = null;
        String dateAAfficher = "";
        DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (inventaireLigneTempCourant.getPeremptionDate().length() >= 10) {
                date = dateDecodeur.parse(inventaireLigneTempCourant.getPeremptionDate().substring(0, 10));
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dateAAfficher = dateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.datePeremption.setText(dateAAfficher);
        viewHolder.setDatePeremptionColor(date);

        // Gestion des actions sur les éléments graphiques
        final InventaireLigneTempViewHolder finalViewHolder = viewHolder;
        viewHolder.qte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvre une boite de dialogue avec un NumberPicker
                String title = "Saisir la quantite";
                String message = "Nouvelle quantite : ";
                int maxValue = 999999;
                int value = (int)inventaireLigneTempCourant.getStockPhysique();

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = aNumberPicker.getValue();
                        inventaireLigneTempCourant.setStockPhysique(qteAprès);
                        Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(db, inventaireLigneTempCourant);
                        finalViewHolder.qte.setText(String.valueOf(qteAprès));
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    }
                };

                Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);
            }
        });

        viewHolder.boutonSuppression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (InventaireLigneTempViewHolder viewHolder : viewHolderList) {
                    if (viewHolder.boutonSuppression == v) {
                        Inventaire_Ligne_Temp inventaireLigneTemp = (Inventaire_Ligne_Temp) getItem(viewHolderList.indexOf(viewHolder));
                        Inventaire_Ligne_TempOpenHelper.supprimerInventaireLigneTempEnBDD(db, inventaireLigneTemp);
                        inventaireLigneTempList.remove(inventaireLigneTemp);
                        notifyDataSetChanged();
                        ((TextView) ((AppCompatActivity) context).findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(inventaireLigneTempList.size()));
                    }
                    return;
                }
            }
        });
        viewHolder.boutonModif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (InventaireLigneTempViewHolder viewHolder : viewHolderList) {
                    if (viewHolder.boutonModif == v) {
                        Inventaire_Ligne_Temp inventaire_Ligne_Temp = inventaireLigneTempList.get(viewHolderList.indexOf(viewHolder));
                        ((DetailInventaireScannerActivity) context).modifierZoneEtEmplacement(inventaire_Ligne_Temp);
                        return;
                    }
                }
            }
        });

        return convertView;
    }

    private class InventaireLigneTempViewHolder {
        TextView designation;
        TextView qte;
        TextView numLot;
        TextView datePeremption;
        TextView emplacement;
        TextView zone;

        ImageView boutonSuppression;
        ImageView boutonModif;

        public void setDatePeremptionColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.rouge2, null));
                } else if (delai >= delai60jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.orange2, null));
                } else {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.vert, null));
                }
            } else {
                datePeremption.setTextColor(Color.BLACK);
            }

        }
    }
}
