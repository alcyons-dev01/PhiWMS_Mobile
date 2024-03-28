package com.example.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.example.phiwms_mobile.Classes.Depot;
import com.example.phiwms_mobile.Classes.Utilisateur;
import com.example.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 25/04/2017.
 */

public class DepotAdapter extends ArrayAdapter {

    public List<Depot> depots;
    Context context;
    Utilisateur utilisateur;

    public DepotAdapter(Context context, List<Depot> depots, Utilisateur utilisateur) {
        super(context, 0, depots);
        this.depots = depots;
        this.context = context;
        this.utilisateur = utilisateur;
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


        String nom_depot = depotCourant.getNom();
        String structure = depotCourant.getStructure();
        if(structure.contentEquals("PAD") && utilisateur.getIdentifiant().contentEquals("ALCYONS"))
        {
            String[] tab_nom = nom_depot.split(" ");
            String nom1 = tab_nom[0];
            String nom2 = tab_nom[1];

            if(nom1.length() >= 3)
            {
                nom1 = nom1.substring(0,2)+"... ";
            }
            else
            {
                nom1 = nom1+"... ";
            }

            if(nom2.length() >= 3)
            {
                nom2 = nom2.substring(0,2)+"... ";
            }
            else
            {
                nom2 = nom2+"... ";
            }

            nom_depot = nom1+nom2;
        }

        viewHolder.nom.setText(nom_depot);

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
