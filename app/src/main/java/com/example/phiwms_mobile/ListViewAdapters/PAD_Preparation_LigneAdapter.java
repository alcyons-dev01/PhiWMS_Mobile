package com.example.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import com.example.phiwms_mobile.Classes.Preparation_Ligne;
import com.example.phiwms_mobile.Classes.Produit;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 07/03/2018.
 */

public class PAD_Preparation_LigneAdapter extends ArrayAdapter implements Filterable {
    public List<PADViewHolder> viewHoldersList;
    public List<Preparation_Ligne> preparationLigneList;
    Context context;
    SQLiteDatabase db;
    ProduitFilter produitFilter;
    List<Preparation_Ligne> preparationLigneListDeBaseList;

    public PAD_Preparation_LigneAdapter(Context context, SQLiteDatabase database, List<Preparation_Ligne> preparationLigneList) {
        super(context, 0, preparationLigneList);
        this.context = context;
        this.db = database;
        this.preparationLigneList = preparationLigneList;

        this.preparationLigneListDeBaseList = new ArrayList<>();
        for (Preparation_Ligne preparationLigne : preparationLigneList) {
            this.preparationLigneListDeBaseList.add(preparationLigne);
        }
        this.produitFilter = null;

        viewHoldersList = new ArrayList<>();
        for (Preparation_Ligne preparationLigne : preparationLigneList) {
            viewHoldersList.add(new PADViewHolder());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_produit_pad, parent, false);
        }

        PADViewHolder viewHolder = (PADViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = viewHoldersList.get(position);
            viewHolder.nomProduit = (TextView) convertView.findViewById(R.id.nomProduit);
            viewHolder.qteSaisie = (TextView) convertView.findViewById(R.id.qteSaisie);
            viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
            viewHolder.conditionnementProduit = (TextView) convertView.findViewById(R.id.conditionnementProduit);
            viewHolder.unite = (TextView) convertView.findViewById(R.id.unite);
            convertView.setTag(viewHolder);
        }

        Preparation_Ligne preparationLigne = (Preparation_Ligne) getItem(position);

        if (preparationLigne != null) {
            Produit produit = ProduitOpenHelper.getProduitByID(db,preparationLigne.getCode_prod());

            if(produit!=null){
                viewHolder.nomProduit.setText(produit.getDesignation_ext());
                viewHolder.referenceProduit.setText(produit.getRef_fourni());
                viewHolder.conditionnementProduit.setText("(x" + String.valueOf(produit.getCond_achat()) + ")");
                viewHolder.qteSaisie.setVisibility(View.VISIBLE);
                viewHolder.unite.setTextColor(ContextCompat.getColor(context, R.color.noir));

                if(preparationLigne.getRAC()==0){
                    viewHolder.qteSaisie.setText("0");
                    viewHolder.qteSaisie.setVisibility(View.GONE);
                    viewHolder.unite.setVisibility(View.VISIBLE);
                    viewHolder.unite.setText("Non commandé");
                    viewHolder.unite.setTextColor(ContextCompat.getColor(context, R.color.rouge));
                }
                else{
                    viewHolder.qteSaisie.setText(String.valueOf((int) preparationLigne.getRAC()));
                    viewHolder.unite.setVisibility(View.VISIBLE);
                    viewHolder.unite.setText("commandés");
                }

                if (Integer.parseInt(viewHolder.qteSaisie.getText().toString()) > 0) {
                    viewHolder.qteSaisie.setTextColor(ContextCompat.getColor(context, R.color.vert2));
                    viewHolder.qteSaisie.setTypeface(viewHolder.qteSaisie.getTypeface(), Typeface.BOLD);
                } else {
                    viewHolder.qteSaisie.setTextColor(ContextCompat.getColor(context, R.color.noir));
                    viewHolder.qteSaisie.setTypeface(viewHolder.qteSaisie.getTypeface(), Typeface.NORMAL);
                }
            }
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (produitFilter == null)
            produitFilter = new ProduitFilter();

        return produitFilter;
    }

    public class PADViewHolder {
        public TextView nomProduit;
        public TextView qteSaisie;
        public TextView referenceProduit;
        public TextView conditionnementProduit;
        public TextView unite;
    }

    private class ProduitFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            CharSequence chaineToSearch = constraint.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            preparationLigneList.clear();

            for (Preparation_Ligne preparationLigne : preparationLigneListDeBaseList) {
                preparationLigneList.add(preparationLigne);
            }

            if (chaineToSearch != null && chaineToSearch.toString().length() > 0) {

                List<Preparation_Ligne> preparationLigneTrouveList = new ArrayList<>();

                for (Preparation_Ligne preparationLigne : preparationLigneList) {
                    // Vérifie le début du premier mot
                    Produit produit = ProduitOpenHelper.getProduitByID(db,preparationLigne.getCode_prod());
                    String produitDesignation = produit.getDesignation_ext().toLowerCase();
                    if (produitDesignation.startsWith(String.valueOf(constraint))) {
                        preparationLigneTrouveList.add(preparationLigne);
                    } else {
                        // Vérifie le début de chaque mot
                        final String[] words = produitDesignation.split(" ");
                        for (String word : words) {
                            if (word.startsWith(String.valueOf(constraint))) {
                                preparationLigneTrouveList.add(preparationLigne);
                                break;
                            }
                        }
                    }
                }

                filterResults.values = preparationLigneTrouveList;
                filterResults.count = preparationLigneTrouveList.size();
            } else {
                filterResults.values = preparationLigneListDeBaseList;
                filterResults.count = preparationLigneListDeBaseList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            preparationLigneList.clear();
            if (results.values != null) {
                for (Preparation_Ligne preparationLigne : (List<Preparation_Ligne>) results.values) {
                    add(preparationLigne);
                }
            }
            notifyDataSetChanged();
        }
    }

}
