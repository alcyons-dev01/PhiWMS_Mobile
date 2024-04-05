package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import com.example.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 30/06/2017.
 */

public class Stock_Lot_EmplacementAdapter extends ArrayAdapter {

    public List<Stock_Lot_Emplacement_Light> stockLotEmplacementLightList;
    Context context;

    public Stock_Lot_EmplacementAdapter(Context context, List<Stock_Lot_Emplacement_Light> stockLotEmplacementLightList) {
        super(context, 0, stockLotEmplacementLightList);
        this.context = context;
        this.stockLotEmplacementLightList = new ArrayList<>();
        this.stockLotEmplacementLightList.addAll(stockLotEmplacementLightList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_stock_lot_emplacement, parent, false);
        }

        Stock_Lot_EmplacementViewHolder viewHolder = (Stock_Lot_EmplacementViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new Stock_Lot_EmplacementViewHolder();
            viewHolder.nomZone = (TextView) convertView.findViewById(R.id.nomZone);
            viewHolder.nomEmplacement = (TextView) convertView.findViewById(R.id.nomEmplacement);
            viewHolder.lot = (TextView) convertView.findViewById(R.id.lot);
            viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
            viewHolder.qteActuelle = (TextView) convertView.findViewById(R.id.qteActuelle);
            viewHolder.numSerie = (TextView) convertView.findViewById(R.id.numSerie);
            viewHolder.layout_serie = (LinearLayout) convertView.findViewById(R.id.layout_serie);
            convertView.setTag(viewHolder);
        }

        Stock_Lot_Emplacement_Light stockLotEmplacementLightCourant = (Stock_Lot_Emplacement_Light) getItem(position);
        if (stockLotEmplacementLightCourant != null) {
            viewHolder.nomZone.setText(stockLotEmplacementLightCourant.getZone());
            viewHolder.nomEmplacement.setText(stockLotEmplacementLightCourant.getEmplacement());
            viewHolder.lot.setText(stockLotEmplacementLightCourant.getLot());
            if(stockLotEmplacementLightCourant.getSerie().contentEquals(""))
            {
                viewHolder.layout_serie.setVisibility(View.GONE);
            }
            else
            {
                viewHolder.layout_serie.setVisibility(View.VISIBLE);
                viewHolder.numSerie.setText(stockLotEmplacementLightCourant.getSerie());
            }

            Date date = null;
            String dateAAfficher = "00/00/0000";
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            try {
                if (stockLotEmplacementLightCourant.getPeremptionDate().length() >= 10) {
                    date = dateDecodeur.parse(stockLotEmplacementLightCourant.getPeremptionDate().substring(0, 10));
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    dateAAfficher = dateFormat.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            viewHolder.datePeremption.setText(dateAAfficher);
            viewHolder.setDatePeremptionColor(date);

            viewHolder.qteActuelle.setText(String.valueOf((int) stockLotEmplacementLightCourant.getQte()));
        }
        return convertView;
    }

    @Override
    public void clear() {
        stockLotEmplacementLightList.clear();
    }

    private class Stock_Lot_EmplacementViewHolder {
        public TextView nomZone;
        public TextView nomEmplacement;
        public TextView lot;
        public TextView datePeremption;
        public TextView qteActuelle;
        public TextView numSerie;
        public LinearLayout layout_serie;

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
