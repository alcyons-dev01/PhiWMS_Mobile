package com.example.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.phiwms_mobile.Classes.Produit;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 04/10/2017.
 */

public class Demande_ParticuliereAdapter extends ArrayAdapter {

    public List<Produit> produit = new ArrayList<>();
    Produit prodCourant;
    Context context;
    SQLiteDatabase db;

    public Demande_ParticuliereAdapter(Context context, List<Produit> produit, SQLiteDatabase db) {
        super(context, 0, produit);
        this.produit = produit;
        this.context = context;
        this.db = db;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_demande_particuliere, parent, false);
        }
        // Récupération des objets graphiques
        ProduitViewHolder viewHolder = new ProduitViewHolder();
        viewHolder.nom = (TextView) convertView.findViewById(R.id.nomProduit);
        prodCourant = produit.get(position);
        viewHolder.nom.setText(prodCourant.getDesignation_interne());


        return convertView;
    }


    private class ProduitViewHolder {
        public TextView nom;
    }
}