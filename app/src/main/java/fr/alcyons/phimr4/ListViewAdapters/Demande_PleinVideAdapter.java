package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import android.widget.Filter;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Detail_DotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phimr4.Classes.Demande_PleinVide;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Detail_Dot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.R;

/**
 * Created by jessica on 31/01/2018.
 */

public class Demande_PleinVideAdapter extends ArrayAdapter {

    public List<Demande_PleinVide> demandePleinVideList;
    public List<Demande_PleinVide> demandePleinVideOriginalList;
    public List<Demande_PleinVideViewHolder> demandePleinVideViewHolderList;
    Context context;
    SQLiteDatabase db;

    Demande_PleinVideFilter filter;
    public List<Detail_Dot> detailDotList;
    public List<Detail_Dot> detailDotListOriginal;


    public Demande_PleinVideAdapter(Context context, SQLiteDatabase db, List<Demande_PleinVide> demandePleinVideList, List<Detail_Dot> listDetailDot) {
        super(context, 0, listDetailDot);
        this.demandePleinVideList = demandePleinVideList;
        this.context = context;
        this.db = db;
        this.detailDotList = listDetailDot;
        this.detailDotListOriginal = new ArrayList<>();
        this.detailDotListOriginal.addAll(detailDotList);

        demandePleinVideOriginalList = new ArrayList<>();
        demandePleinVideOriginalList.addAll(demandePleinVideList);
        
        demandePleinVideViewHolderList = new ArrayList<>();
        for (int i = 0; i < listDetailDot.size(); i++) {
            Demande_PleinVideViewHolder viewHolder = new Demande_PleinVideViewHolder();
            demandePleinVideViewHolderList.add(viewHolder);
        }

        filter = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Demande_PleinVideViewHolder viewHolder = demandePleinVideViewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_demande_pleinvide, parent, false);

        viewHolder.designationProduit = (TextView) convertView.findViewById(R.id.designationProduit);
        viewHolder.adressagePleinVide = (TextView) convertView.findViewById(R.id.adressagePleinVide);
        viewHolder.quantitePleinVide = (TextView) convertView.findViewById(R.id.quantitePleinVide);
        viewHolder.layoutPrincipal = (LinearLayout) convertView.findViewById(R.id.layoutPrincipal);
        viewHolder.cartouchePleinVide = (RelativeLayout) convertView.findViewById(R.id.cartouchePleinVide);
        Depot depot = DepotOpenHelper.getDepotPUI(db);
        Detail_Dot detail_dot = (Detail_Dot) getItem(position);
        Produit produit = ProduitOpenHelper.getProduitByID(db, detail_dot.getCode_produit());
        Stock stock = StockOpenHelper.getStockByProduitEtDepot(db, produit, depot);

        viewHolder.designationProduit.setText(produit.getDesignation_interne());
        viewHolder.adressagePleinVide.setText(detail_dot.getPleinVide_Adressage());
        viewHolder.quantitePleinVide.setText(String.valueOf(detail_dot.getQte()) + " U");

        int stockQuantiteActuelle = 0;
        if (stock != null) {
            stockQuantiteActuelle = (int) stock.getQuantite_Actuelle();
        }

        if (stockQuantiteActuelle < detail_dot.getQte()) {
            viewHolder.quantitePleinVide.setTextColor(Color.RED);
        }

        boolean present = false;
        for(Demande_PleinVide demandePleinVide : demandePleinVideList)
        {
            if(detail_dot.get_UID() == demandePleinVide.getDetailDot_UID())
            {
                present = true;
                break;
            }
        }

        if(present)
        {
            viewHolder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_plain_vert));
        }
        else
        {

        }

        return convertView;
    }

    public class Demande_PleinVideViewHolder {
        TextView designationProduit;
        public LinearLayout layoutPrincipal;
        public RelativeLayout cartouchePleinVide;
        TextView referenceProduit;
        TextView categorieProduit;
        TextView conditionnementProduit;
        TextView uniteProduit;
        TextView qteStockPUI;
        TextView qteStockDestinataire;
        TextView qte_a_preparer;


        TextView adressagePleinVide;
        TextView quantitePleinVide;
    }

    public Filter getFilter(CharSequence s) {
        if (filter == null)
            filter = new Demande_PleinVideFilter();

        filter.performFiltering(s);

        return filter;
    }

    private class Demande_PleinVideFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String chaineAtrouver = constraint.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            demandePleinVideList.clear();
            for (Demande_PleinVide demandePleinVide : demandePleinVideOriginalList
                    ) {
                demandePleinVideList.add(demandePleinVide);
            }

            if (chaineAtrouver != null && chaineAtrouver.toString().length() > 0) {
                List<Demande_PleinVide> founded = new ArrayList<>();

                for (Demande_PleinVide demandePleinVide : demandePleinVideList) {

                    Detail_Dot detailDot = Detail_DotOpenHelper.getDetailDotById(db, demandePleinVide.getDetailDot_UID());
                    // Vérifie le début du premier mot
                    String Demande_PleinVideProduitDesignation = detailDot.getDesignation().toLowerCase();
                    String CategoriePleinVide = detailDot.getCategorie().toLowerCase();
                    if (Demande_PleinVideProduitDesignation.startsWith(String.valueOf(chaineAtrouver))) {
                        founded.add(demandePleinVide);
                    } else if (detailDot.getCategorie().toLowerCase().contains(chaineAtrouver)) {
                        founded.add(demandePleinVide);
                    } else {
                        /* Vérifie le début de chaque mot*/
                        final String[] words = Demande_PleinVideProduitDesignation.split(" ");
                        for (String word : words) {
                            if (word.startsWith(String.valueOf(chaineAtrouver))) {
                                founded.add(demandePleinVide);
                                break;
                            }
                        }
                    }
                }

                filterResults.values = founded;
                filterResults.count = founded.size();
            } else {
                filterResults.values = demandePleinVideOriginalList;
                filterResults.count = demandePleinVideOriginalList.size();
            }
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            demandePleinVideList.clear();
            for (Demande_PleinVide demandePleinVide : (List<Demande_PleinVide>) results.values) {
                demandePleinVideList.add(demandePleinVide);
            }
            notifyDataSetChanged();
        }

    }
}
