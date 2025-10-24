package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock;
import fr.alcyons.phiwms_mobile.R;

public class Liste_ReferenceAdapter extends ArrayAdapter implements Filterable {

    public List<Produit> produitList;
    public List<Produit> produitDeBaseList;
    Context context;
    SQLiteDatabase db;
    ReferenceFilter referenceFilter;
    Depot depotCourant;

    public Liste_ReferenceAdapter(Context context, SQLiteDatabase database, List<Produit> produitList, Depot depotCourant) {
        super(context, 0, produitList);
        this.context = context;
        this.db = database;
        this.produitList = new ArrayList<>();
        this.produitList = produitList;

        this.produitDeBaseList = new ArrayList<>();
        this.produitDeBaseList.addAll(produitList);
        this.referenceFilter = null;
        this.depotCourant = depotCourant;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_reference, parent, false);
        }

        ReferenceViewHolder viewHolder = (ReferenceViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ReferenceViewHolder();
            viewHolder.designationProduit = (TextView) convertView.findViewById(R.id.designationProduit);
            viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
            viewHolder.nomFournisseur = (TextView) convertView.findViewById(R.id.nomFournisseur);
            viewHolder.StockTheorique = (TextView) convertView.findViewById(R.id.StockTheorique);
            convertView.setTag(viewHolder);
        }

        Produit produit = (Produit) getItem(position);

        if (produit != null) {
            Stock stockCourant = StockOpenHelper.getStockByProduitEtDepot(db, produit, depotCourant);

            if (stockCourant != null) {
                viewHolder.StockTheorique.setText(String.valueOf((int)stockCourant.getQuantite_Actuelle()));
            }
            else
            {
                viewHolder.StockTheorique.setText("");
            }

            viewHolder.designationProduit.setText(produit.getDesignation_interne());
            viewHolder.referenceProduit.setText(produit.getRef_fourni());
            viewHolder.nomFournisseur.setText(produit.getFournisseur());
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (referenceFilter == null)
            referenceFilter = new ReferenceFilter();

        return referenceFilter;
    }

    private class ReferenceFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            CharSequence chaineToSearch = constraint.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            produitList.clear();

            produitList.addAll(produitDeBaseList);

            if (!chaineToSearch.toString().isEmpty()) {

                List<Produit> produitTrouveList = new ArrayList<>();

                for (Produit produit : produitList) {
                    // Vérifie le début du premier mot
                    String produitDesignation = produit.getDesignation_interne().toLowerCase();
                    if (produitDesignation.startsWith(String.valueOf(constraint))) {
                        produitTrouveList.add(produit);
                    } else {
                        // Vérifie le début de chaque mot
                        final String[] words = produitDesignation.split(" ");
                        for (String word : words) {
                            if (word.startsWith(String.valueOf(constraint))) {
                                produitTrouveList.add(produit);
                                break;
                            }
                        }
                    }
                }

                filterResults.values = produitTrouveList;
                filterResults.count = produitTrouveList.size();
            } else {
                filterResults.values = produitDeBaseList;
                filterResults.count = produitList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            produitList.clear();
            if (results.values != null) {
                for (Produit produit : (List<Produit>) results.values) {
                    add(produit);
                }
            }
            notifyDataSetChanged();
        }
    }

    private static class ReferenceViewHolder {
        public TextView designationProduit;
        public TextView referenceProduit;
        public TextView nomFournisseur;
        public TextView StockTheorique;
    }

}
