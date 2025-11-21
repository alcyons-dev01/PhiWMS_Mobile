package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.R;

public class DetailInventaireAdapter extends ArrayAdapter {

    public List<String[]> inventaireLigneTempList;
    public List<String[]> inventaireLigneTempListBase;
    Context context;
    SQLiteDatabase db;
    ReferenceFilter filter;

    public DetailInventaireAdapter(Context context, List<String[]> inventaireLigneTempList, SQLiteDatabase db) {
        super(context, 0, inventaireLigneTempList);
        this.inventaireLigneTempList = inventaireLigneTempList;
        this.context = context;
        this.db = db;
        this.filter = null;
        this.inventaireLigneTempListBase = new ArrayList<>();
        this.inventaireLigneTempListBase.addAll(inventaireLigneTempList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_detail_inventaire, parent, false);
        }

        DetailInventaireAdapter.ProduitViewHolder viewHolder = (DetailInventaireAdapter.ProduitViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new DetailInventaireAdapter.ProduitViewHolder();

            viewHolder.nomProduit = (TextView) convertView.findViewById(R.id.nomProduit);
            viewHolder.refProduit = (TextView) convertView.findViewById(R.id.refProduit);
            viewHolder.fournisseurProduit = (TextView) convertView.findViewById(R.id.fournisseurProduit);
            viewHolder.stockSaisie = (TextView) convertView.findViewById(R.id.stockSaisie);
            viewHolder.imageStatutSaisie_IV = (ImageView) convertView.findViewById(R.id.imageStatutSaisie_IV);
            convertView.setTag(viewHolder);
        }

        String[] inventaireLigneTemp = (String[]) getItem(position);
        viewHolder.nomProduit.setText(inventaireLigneTemp[1]);
        viewHolder.refProduit.setText(inventaireLigneTemp[0]);
        viewHolder.fournisseurProduit.setText(inventaireLigneTemp[2]);

        if(inventaireLigneTemp[6].contentEquals("true"))
        {
            viewHolder.imageStatutSaisie_IV.setBackground(context.getResources().getDrawable(R.drawable.ic_check_circle_green,null));
            ViewCompat.setBackgroundTintList(viewHolder.imageStatutSaisie_IV, ColorStateList.valueOf(context.getResources().getColor(R.color.vert, null)));
        }
        else
        {
            viewHolder.imageStatutSaisie_IV.setBackground(context.getResources().getDrawable(R.drawable.ic_edit_black,null));
            ViewCompat.setBackgroundTintList(viewHolder.imageStatutSaisie_IV, ColorStateList.valueOf(context.getResources().getColor(R.color.bleu_clair_alcyons, null)));
        }

        if(Integer.parseInt(inventaireLigneTemp[4]) == -1)
        {
            viewHolder.stockSaisie.setText("-1");
            viewHolder.stockSaisie.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.rouge,null));
        }
        else
        {
            viewHolder.stockSaisie.setText(inventaireLigneTemp[4]);
            viewHolder.stockSaisie.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.vert,null));
        }
        return convertView;
    }

    public void replaceData(List<String[]> nouveaux) {
        inventaireLigneTempListBase.clear();
        inventaireLigneTempListBase.addAll(nouveaux);
        inventaireLigneTempList.clear();
        inventaireLigneTempList.addAll(nouveaux);
        notifyDataSetChanged();
    }

    private class ProduitViewHolder {
        public TextView nomProduit;
        public TextView refProduit;
        public TextView fournisseurProduit;
        public TextView stockSaisie;
        public ImageView imageStatutSaisie_IV;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new DetailInventaireAdapter.ReferenceFilter();

        return filter;
    }

    private class ReferenceFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

           /* String chaineToSearch = Normalizer.normalize(constraint.toString().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            FilterResults filterResults = new FilterResults();

            inventaireLigneTempList.clear();

            for (Inventaire_Ligne_Temp inventaireLigneTemp : inventaireLigneTempListBase) {
                inventaireLigneTempList.add(inventaireLigneTemp);
            }

            if (chaineToSearch != null && chaineToSearch.toString().length() > 0) {

                List<Inventaire_Ligne_Temp> inventaireLigneTempTrouveList = new ArrayList<>();

                for (Inventaire_Ligne_Temp inventaireLigneTempCourant : inventaireLigneTempList) {
                    // Vérifie le début du premier mot
                    String produitDesignation = Normalizer.normalize(inventaireLigneTempCourant.getDesignation().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                    if (produitDesignation.startsWith(chaineToSearch)) {
                        inventaireLigneTempTrouveList.add(inventaireLigneTempCourant);
                    } else {
                        // Vérifie le début de chaque mot
                        final String[] words = produitDesignation.split(" ");
                        for (String word : words) {
                            if (word.startsWith(chaineToSearch)) {
                                inventaireLigneTempTrouveList.add(inventaireLigneTempCourant);
                                break;
                            }
                        }
                    }
                }

                filterResults.values = inventaireLigneTempTrouveList;
                filterResults.count = inventaireLigneTempTrouveList.size();
            } else {
                filterResults.values = inventaireLigneTempListBase;
                filterResults.count = inventaireLigneTempListBase.size();
            }
            return filterResults;*/
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            inventaireLigneTempList.clear();
            if (results.values != null) {
                for (Inventaire_Ligne_Temp inventaireLigneTemp : (List<Inventaire_Ligne_Temp>) results.values) {
                    add(inventaireLigneTemp);
                }
            }
            notifyDataSetChanged();
        }
    }
}
