package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.Inventaire.DetailInventaire_V2Activity;
import fr.alcyons.phiwms_mobile.R;

public class DetailInventaireAdapter extends ArrayAdapter {

    public List<Inventaire_Ligne_Temp> inventaireLigneTempList;
    public List<Inventaire_Ligne_Temp> inventaireLigneTempListBase;
    Context context;
    SQLiteDatabase db;
    ReferenceFilter filter;

    public DetailInventaireAdapter(Context context, List<Inventaire_Ligne_Temp> inventaireLigneTempList, SQLiteDatabase db) {
        super(context, 0, inventaireLigneTempList);
        this.inventaireLigneTempList = inventaireLigneTempList;
        this.context = context;
        this.db = db;
        this.filter = null;
        this.inventaireLigneTempListBase = new ArrayList<>();
        this.inventaireLigneTempListBase.addAll(inventaireLigneTempList);
    }

    public void updateList(List<Inventaire_Ligne_Temp> newList) {
        this.inventaireLigneTempList.clear();
        this.inventaireLigneTempList.addAll(newList);
        this.inventaireLigneTempListBase.clear();
        this.inventaireLigneTempListBase.addAll(newList);
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
            viewHolder.layoutPrincipal_LL = (LinearLayout) convertView.findViewById(R.id.layoutPrincipal_LL);
            viewHolder.layoutAjoutManuelle = (LinearLayout) convertView.findViewById(R.id.layoutAjoutManuelle);
            viewHolder.layoutDetail = (LinearLayout) convertView.findViewById(R.id.layoutDetail);
            viewHolder.progressBarReference_PB = (ProgressBar) convertView.findViewById(R.id.progressBarReference_PB);
            convertView.setTag(viewHolder);
        }

        Inventaire_Ligne_Temp inventaireLigneTemp = (Inventaire_Ligne_Temp) getItem(position);
        viewHolder.nomProduit.setText(inventaireLigneTemp.getDesignation());
        viewHolder.refProduit.setText(inventaireLigneTemp.getProduitReference());
        viewHolder.fournisseurProduit.setText(inventaireLigneTemp.getFournisseurNom());
        viewHolder.progressBarReference_PB.setVisibility(View.GONE);

        int qteStockPhysique = Inventaire_Ligne_TempOpenHelper.getQteInventorieByInventaireProduitZoneDepot(db, inventaireLigneTemp.getInventaire_ID(), inventaireLigneTemp.getProduitID(), inventaireLigneTemp.getZone(), inventaireLigneTemp.getDepotReference());
        if(qteStockPhysique == -1)
        {
            viewHolder.stockSaisie.setText("-1");
            viewHolder.stockSaisie.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.rouge,null));
        }
        else
        {
            viewHolder.stockSaisie.setText(String.valueOf(qteStockPhysique));
            viewHolder.stockSaisie.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.vert,null));
        }

        viewHolder.layoutAjoutManuelle.setOnClickListener(view -> ((DetailInventaire_V2Activity)context).ajoutManuel(position));

        viewHolder.layoutDetail.setOnClickListener(view -> ((DetailInventaire_V2Activity)context).versDetail(position));

        viewHolder.stockSaisie.setOnClickListener(view -> ((DetailInventaire_V2Activity)context).versDetail(position));
        return convertView;
    }

    public void replaceData(List<Inventaire_Ligne_Temp> nouveaux) {
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
        public LinearLayout layoutPrincipal_LL;
        public LinearLayout layoutAjoutManuelle;
        public LinearLayout layoutDetail;
        ProgressBar progressBarReference_PB;
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

            String chaineToSearch = Normalizer.normalize(constraint.toString().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
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
                    }
                    else if(chaineToSearch.contentEquals("-1"))
                    {
                        inventaireLigneTempTrouveList.add(inventaireLigneTempCourant);
                    }
                    else {
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
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            inventaireLigneTempList.clear();
            if (results.values != null) {
                for (String[] inventaireLigneTemp : (List<String[]>) results.values) {
                    add(inventaireLigneTemp);
                }
            }
            notifyDataSetChanged();
        }
    }
}
