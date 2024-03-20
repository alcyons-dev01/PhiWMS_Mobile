package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.R;

/**
 * Created by olivier on 05/01/2018.
 */

public class Produit_RetourDemandeAdapter extends ArrayAdapter {

    public List<Stock> stocks = new ArrayList<>();
    public List<Stock> stockDeBase = new ArrayList<>();
    public List<Integer> produitSelect;
    public int compteur = 0;
    Context context;
    TextView nb_prod;
    View view = null;
    SQLiteDatabase db;
    Boolean present = false;

    Produit_RetourDemandeAdapter.Produit_RetourFilter ProduitRetourFilter;


    public Produit_RetourDemandeAdapter(Context context, List<Stock> stocks, TextView nb_prod, List<Integer> produitSelect, SQLiteDatabase db) {
        super(context, 0, stocks);
        this.stocks = stocks;
        this.context = context;
        this.nb_prod = nb_prod;
        this.compteur = Integer.parseInt(nb_prod.getText().toString());
        this.produitSelect = produitSelect;
        this.db = db;

        this.stockDeBase = new ArrayList<>();
        for (Stock stock : stocks) {
            this.stockDeBase.add(stock);
        }
        this.ProduitRetourFilter = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_produit_retour_demande, parent, false);
        }

        if (produitSelect != null) {
            compteur = produitSelect.size();
            nb_prod.setText(String.valueOf(produitSelect.size()));
        } else {
            nb_prod.setText(String.valueOf(compteur));
        }


        ProduitViewHolder viewHolder = (ProduitViewHolder) convertView.getTag();

        if (viewHolder == null) {

            viewHolder = new ProduitViewHolder();
            viewHolder.nomProduit = (TextView) convertView.findViewById(R.id.nomProduit);
            viewHolder.produit_select = (CheckBox) convertView.findViewById(R.id.nb_prod_select);
            viewHolder.categorie = (TextView) convertView.findViewById(R.id.categorie);
            viewHolder.reference2 = (TextView) convertView.findViewById(R.id.reference2);
            viewHolder.nbColis = (TextView) convertView.findViewById(R.id.nbColis);
            viewHolder.StockTheorique = (TextView) convertView.findViewById(R.id.StockTheorique);
            viewHolder.zoneReference = (LinearLayout) convertView.findViewById(R.id.zoneReferences);

        } else {

            view = convertView;
            ((ProduitViewHolder) view.getTag()).produit_select.setTag(stocks.get(position));
        }

        final Stock StockCourant = (Stock) getItem(position);
        final ProduitViewHolder finalViewHolder = viewHolder;

        viewHolder.zoneReference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;
                present = false;
                for (Integer i : produitSelect) {
                    if (i == StockCourant.getProduit_UID()) {
                        present = true;
                    }
                }

                if (present == false) {
                    compteur++;
                    produitSelect.add(StockCourant.getProduit_UID());
                    finalViewHolder.produit_select.setChecked(true);
                } else {
                    for (int i = 0; i < produitSelect.size(); i++) {
                        Integer produit = produitSelect.get(i);
                        if (produit == StockCourant.getProduit_UID()) {
                            index = i;
                        }
                    }

                    compteur--;
                    produitSelect.remove(index);
                    finalViewHolder.produit_select.setChecked(false);
                }

                nb_prod.setText(String.valueOf(compteur));
            }
        });


        convertView.setTag(viewHolder);


        if (produitSelect.indexOf(StockCourant.getProduit_UID()) != -1) {
            viewHolder.produit_select.setChecked(true);
        } else {
            viewHolder.produit_select.setChecked(false);
        }


        int nbColisVE = recupererNbColis(StockCourant.getProduit_UID(), StockCourant.getQuantite_Actuelle());
        viewHolder.nomProduit.setText(StockCourant.getDesignation());
        if(StockCourant.getCategorie() == null)
        {
            viewHolder.categorie.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.categorie.setText(StockCourant.getCategorie());
        }

        viewHolder.reference2.setText(StockCourant.getProduit_Reference());
        viewHolder.nbColis.setText(String.valueOf(nbColisVE));
        viewHolder.StockTheorique.setText(String.valueOf((int) StockCourant.getQuantite_Actuelle()));


        return convertView;
    }

    // Permet de calculer les nombre de colis en fonction du conditionnement du produit
    public int recupererNbColis(int produitID, double qte) {
        int nbColis = 0;

        Produit produitCorrespondant = null;

        int conditionnementAchat = 0;

        if (produitID != 0) {
            produitCorrespondant = ProduitOpenHelper.getProduitByID(db, produitID);
            if (produitCorrespondant != null) {
                conditionnementAchat = produitCorrespondant.getCond_achat();
                if (conditionnementAchat == 0) {
                    conditionnementAchat = (int) produitCorrespondant.getCond_distrib();
                }
            } else {
                conditionnementAchat = 0;
            }

        }
        if (qte != 0 && conditionnementAchat != 0) {
            nbColis = (int) (qte / conditionnementAchat);
            nbColis = (int) Math.ceil(nbColis);
        }
        if (qte != 0) {
            if (nbColis == 0) {
                nbColis = 1;
            }
        }

        return nbColis;
    }

    @Override
    public Filter getFilter() {
        if (ProduitRetourFilter == null)
            ProduitRetourFilter = new Produit_RetourDemandeAdapter.Produit_RetourFilter();

        return ProduitRetourFilter;
    }

    private class ProduitViewHolder {
        public TextView nomProduit;
        public CheckBox produit_select;
        public TextView categorie;
        public TextView reference2;
        public TextView nomFournisseur;
        public TextView nbColis;
        public TextView StockTheorique;
        public LinearLayout zoneReference;
    }

    private class Produit_RetourFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            CharSequence chaineToSearch = constraint.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            stocks.clear();

            for (Stock stock : stockDeBase
                    ) {
                stocks.add(stock);
            }

            if (chaineToSearch != null && chaineToSearch.toString().length() > 0) {

                List<Stock> stockTrouveList = new ArrayList<>();

                for (Stock stock : stocks) {
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
                filterResults.values = stockDeBase;
                filterResults.count = stockDeBase.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            stocks.clear();
            if (results.values != null) {
                for (Stock stock : (List<Stock>) results.values) {
                    add(stock);
                }
            }
            notifyDataSetChanged();
        }
    }
}
