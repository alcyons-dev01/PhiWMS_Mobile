package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_ControleRetour_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeLotsControleDesRetoursActivity;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by olivier on 14/06/2019.
 */

public class Lot_ControleDesRetoursScanneeAdapter extends ArrayAdapter {

    public List<Retour_Ligne_ControleRetour_Adapte.LotAdapte> lotAdaptes;
    public List<LotViewHolder> viewHolders;
    public Context context;
    public SQLiteDatabase db;
    public Retour_Ligne_ControleRetour_Adapte retourLigneAdapte;
    private String depotStructure;
    public boolean full;
    public int quantiteARetourner;

    public Lot_ControleDesRetoursScanneeAdapter(Context context, List<Retour_Ligne_ControleRetour_Adapte.LotAdapte> lots, SQLiteDatabase db, Retour_Ligne_ControleRetour_Adapte retourLigneAdapte, String depotStructure, int quantiteARetourner) {
        super(context, 0, lots);
        viewHolders = new ArrayList<>();
        this.context = context;
        this.lotAdaptes = lots;
        this.db = db;
        this.depotStructure = depotStructure;
        this.retourLigneAdapte = retourLigneAdapte;
        for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lotAdapte : lots
                ) {
            viewHolders.add(new LotViewHolder());
        }

        this.quantiteARetourner = quantiteARetourner;
        if(quantiteARetourner == 0)
            full = true;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        Collections.sort(lotAdaptes);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_lot_controle_des_retours_new, parent, false);
        }

        LotViewHolder viewHolder = (LotViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = viewHolders.get(position);
            viewHolder.numLot = (TextView) convertView.findViewById(R.id.numLot);
            viewHolder.numSerie = (TextView) convertView.findViewById(R.id.numSerie);
            viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
            viewHolder.qteSaisie = (TextView) convertView.findViewById(R.id.qteSaisie);
            viewHolder.nomEmplacement = (TextView) convertView.findViewById(R.id.nomEmplacement);
            viewHolder.layout_ajouter_lot = (LinearLayout) convertView.findViewById(R.id.layout_ajouter_lot);
            viewHolder.layout_annuler_lot = (LinearLayout) convertView.findViewById(R.id.layout_annuler_lot);
            viewHolder.layoutPrincipal = (LinearLayout) convertView.findViewById(R.id.layoutPrincipal);
            viewHolder.zoneSerie = (LinearLayout) convertView.findViewById(R.id.zoneSerie);
            viewHolder.layout_qte_saisie_lot_preparation = (LinearLayout) convertView.findViewById(R.id.layout_qte_saisie_lot_preparation);
        }

        Retour_Ligne_ControleRetour_Adapte.LotAdapte lotAdapte = (Retour_Ligne_ControleRetour_Adapte.LotAdapte) getItem(position);

        viewHolder.layout_ajouter_lot.setVisibility(View.GONE);
        viewHolder.layout_annuler_lot.setVisibility(View.GONE);
        viewHolder.layoutPrincipal.setVisibility(View.GONE);

        if(lotAdapte!=null){
            if(lotAdapte.getNumLot().contentEquals("row_ajouter"))
            {
                if(!full)
                    viewHolder.layout_ajouter_lot.setVisibility(View.VISIBLE);

            }
            else if(lotAdapte.getNumLot().contentEquals("row_annuler"))
            {
                if(full)
                    viewHolder.layout_annuler_lot.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.layoutPrincipal.setVisibility(View.VISIBLE);
                viewHolder.numLot.setText(lotAdapte.getNumLot());
                Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, lotAdapte.getStockLotEmplacementID());
                if(stock_courant != null)
                {
                    viewHolder.nomEmplacement.setText(stock_courant.getEmplacement());
                }
                if(lotAdapte.getNumSerie().contentEquals("") || lotAdapte.getNumSerie() == null)
                {
                    viewHolder.zoneSerie.setVisibility(View.GONE);
                }
                else
                {
                    viewHolder.numSerie.setText(lotAdapte.getNumSerie());
                }
                viewHolder.qteSaisie.setText(String.valueOf(lotAdapte.getQteSaisie()));

                viewHolder.qteSaisie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ListeLotsControleDesRetoursActivity) context).ClickNumberPicker(position);
                    }
                });


                String dateAAfficher = "";

                Date date = new Date();
                DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    date = dateDecodeur.parse(lotAdapte.getDatePeremption());
                    dateAAfficher = dateFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                viewHolder.datePeremption.setText(dateAAfficher);
                viewHolder.setDatePeremptionColor(date);
                if(Integer.parseInt(viewHolder.qteSaisie.getText().toString()) == 0)
                {
                    if(full)
                    {
                        viewHolder.layoutPrincipal.setVisibility(View.GONE);
                    }
                    viewHolder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne));
                    viewHolder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                     }
                else
                {
                    viewHolder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne_valider));
                    viewHolder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
                }
            }
        }

        return convertView;
    }

    public class LotViewHolder {
        public TextView numLot;
        public TextView numSerie;
        public TextView datePeremption;
        public TextView qteSaisie;
        public TextView qteActuelle;
        public TextView nomEmplacement;
        public LinearLayout layout_ajouter_lot;
        public LinearLayout layout_annuler_lot;
        public LinearLayout layoutPrincipal;
        public LinearLayout zoneSerie;
        public LinearLayout layout_qte_saisie_lot_preparation;
        public LinearLayout layoutZoneEmplacement;
        public TextView TextViewZone;
        public TextView TextViewEmplacement;
        public ImageView SeparationZoneEmplacement;

        public void setDatePeremptionColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.rouge2));
                } else if (delai >= delai60jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.orange2));
                } else {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.vert));
                }
            } else {
                datePeremption.setTextColor(Color.BLACK);
            }

        }
    }
}
