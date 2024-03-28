package com.example.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.example.phiwms_mobile.Classes.Depot;
import com.example.phiwms_mobile.R;

public class Depot_Livraison_Adapter extends ArrayAdapter {

    public List<Depot> depots;
    Context context;

    public Depot_Livraison_Adapter(Context context, List<Depot> depots) {
        super(context, 0, depots);
        this.depots = depots;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_depot, parent, false);
        }


        DepotViewHolder viewHolder = (DepotViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new DepotViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nomDepot);
            convertView.setTag(viewHolder);
        }

        Depot depotCourant = (Depot) getItem(position);

        viewHolder.nom.setText(depotCourant.getNom());

        return convertView;
    }

    @Override
    public void clear() {
        depots.clear();
    }

    private class DepotViewHolder {
        public TextView nom;
    }
}
