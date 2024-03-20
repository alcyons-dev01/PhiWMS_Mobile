package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.R;

public class Liste_ReferenceAdapter extends ArrayAdapter implements Filterable {

    public List<Produit> produitList;
    public List<Produit> produitDeBaseList;
    Context context;
    SQLiteDatabase db;
    ReferenceFilter referenceFilter;

    public Liste_ReferenceAdapter(Context context, SQLiteDatabase database, List<Produit> produitList) {
        super(context, 0, produitList);
        this.context = context;
        this.db = database;
        this.produitList = new ArrayList<>();
        this.produitList = produitList;

        this.produitDeBaseList = new ArrayList<>();
        for (Produit produit : produitList) {
            this.produitDeBaseList.add(produit);
        }
        this.referenceFilter = null;
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

            List<Stock> stockList = StockOpenHelper.getStockByProduit(db, produit);

            if (stockList != null && stockList.size() != 0) {
                double aDouble = stockList.get(0).getQuantite_Actuelle();
                int qteActuelle = (int) aDouble;
                viewHolder.StockTheorique.setText(String.valueOf(qteActuelle));
            }
            else
            {
                viewHolder.StockTheorique.setText("0");
            }

            viewHolder.designationProduit.setText(produit.getDesignation_interne());
            viewHolder.referenceProduit.setText(produit.getRef_fourni());
            viewHolder.nomFournisseur.setText(produit.getFournisseur());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (referenceFilter == null)
            referenceFilter = new ReferenceFilter();

        return referenceFilter;
    }

    private class ReferenceFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            CharSequence chaineToSearch = constraint.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            produitList.clear();

            for (Produit produit : produitDeBaseList
            ) {
                produitList.add(produit);
            }

            if (chaineToSearch != null && chaineToSearch.toString().length() > 0) {

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

    private class ReferenceViewHolder {
        public TextView designationProduit;
        public TextView referenceProduit;
        public TextView nomFournisseur;
        public TextView StockTheorique;
    }

}
