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

import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.R;

/**
 * Created by quentinlanusse on 29/06/2017.
 */

public class StockAdapter extends ArrayAdapter implements Filterable {

    public List<Stock> stockList;
    public List<Stock> stocksDeBaseList;
    Context context;
    SQLiteDatabase db;
    StockAdapter.StockFilter stockFilter;

    public StockAdapter(Context context, SQLiteDatabase database, List<Stock> stockList) {
        super(context, 0, stockList);
        this.context = context;
        this.db = database;
        this.stockList = new ArrayList<>();
        this.stockList = stockList;

        this.stocksDeBaseList = new ArrayList<>();
        for (Stock stock : stockList) {
            this.stocksDeBaseList.add(stock);
        }
        this.stockFilter = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_stock, parent, false);
        }

        StockViewHolder viewHolder = (StockViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new StockViewHolder();
            viewHolder.designationProduit = (TextView) convertView.findViewById(R.id.designationProduit);
            viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
            viewHolder.nomFournisseur = (TextView) convertView.findViewById(R.id.nomFournisseur);
            viewHolder.numColis = (TextView) convertView.findViewById(R.id.colis);
            viewHolder.qteStock = (TextView) convertView.findViewById(R.id.qteStock);
            convertView.setTag(viewHolder);
        }

        Stock stock = (Stock) getItem(position);

        if (stock != null) {
            viewHolder.designationProduit.setText(stock.getDesignation());
            viewHolder.referenceProduit.setText(stock.getProduit_Reference());
            viewHolder.nomFournisseur.setText(stock.getFournisseur());
            viewHolder.numColis.setText(String.valueOf(stock.getNombreColis(db, "Distribution")));
            viewHolder.qteStock.setText(String.valueOf((int) stock.getQuantite_Actuelle()));
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (stockFilter == null)
            stockFilter = new StockAdapter.StockFilter();

        return stockFilter;
    }

    private class StockFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            CharSequence chaineToSearch = constraint.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            stockList.clear();

            for (Stock stock : stocksDeBaseList
                    ) {
                stockList.add(stock);
            }

            if (chaineToSearch != null && chaineToSearch.toString().length() > 0) {

                List<Stock> stockTrouveList = new ArrayList<>();

                for (Stock stock : stockList) {
                    // Vérifie le début du premier mot
                    String stockDesignation = stock.getDesignation().toLowerCase();
                    if (stockDesignation.startsWith(String.valueOf(constraint))) {
                        stockTrouveList.add(stock);
                    } else {
                        // Vérifie le début de chaque mot
                        final String[] words = stockDesignation.split(" ");
                        for (String word : words) {
                            if (word.startsWith(String.valueOf(constraint))) {
                                stockTrouveList.add(stock);
                                break;
                            }
                        }
                    }
                }

                filterResults.values = stockTrouveList;
                filterResults.count = stockTrouveList.size();
            } else {
                filterResults.values = stocksDeBaseList;
                filterResults.count = stocksDeBaseList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            stockList.clear();
            if (results.values != null) {
                for (Stock stock : (List<Stock>) results.values) {
                    add(stock);
                }
            }
            notifyDataSetChanged();
        }
    }

    private class StockViewHolder {
        public TextView designationProduit;
        public TextView referenceProduit;
        public TextView nomFournisseur;
        public TextView numColis;
        public TextView qteStock;
    }

}
