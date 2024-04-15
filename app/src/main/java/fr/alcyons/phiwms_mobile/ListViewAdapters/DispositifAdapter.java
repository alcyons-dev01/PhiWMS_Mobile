package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 09/05/2017.
 */

public class DispositifAdapter extends ArrayAdapter {

    public List<Produit> dispositifList = new ArrayList<>();
    public List<Produit> dispositifDeBaseList = new ArrayList<>();
    Context context;
    boolean tableRowHautVisible;
    boolean tableRowBasVisible;
    DispositifFilter filter;

    public DispositifAdapter(Context context, List<Produit> dispositifList) {
        super(context, 0, dispositifList);
        this.context = context;
        this.dispositifList = dispositifList;
        this.filter = null;
        for (Produit dispositif : dispositifList) {
            this.dispositifDeBaseList.add(dispositif);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_dispositif, parent, false);
        }

        DispositifViewHolder viewHolder = (DispositifViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new DispositifViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nomDispositif);
            viewHolder.fournisseur = (TextView) convertView.findViewById(R.id.nomFournisseur);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);

            viewHolder.separateur1 = (ImageView) convertView.findViewById(R.id.separateur1);

            viewHolder.tableRowHaut = (TableRow) convertView.findViewById(R.id.tableRowHaut);
            viewHolder.logoPosition1 = (ImageView) convertView.findViewById(R.id.symboleusageunique);
            viewHolder.logoPosition2 = (ImageView) convertView.findViewById(R.id.symbolefragile);
            viewHolder.logoPosition3 = (ImageView) convertView.findViewById(R.id.symboletemperature);
            viewHolder.logoPosition4 = (ImageView) convertView.findViewById(R.id.symboleausec);
            viewHolder.logoPosition5 = (ImageView) convertView.findViewById(R.id.symboleabrilumiere);

            viewHolder.tableRowBas = (TableRow) convertView.findViewById(R.id.tableRowBas);
            viewHolder.logoPosition6 = (ImageView) convertView.findViewById(R.id.symbolesterilisation);
            viewHolder.logoPosition7 = (ImageView) convertView.findViewById(R.id.symbolenepasresteriliser);
            viewHolder.logoPosition8 = (ImageView) convertView.findViewById(R.id.symbolerisquesubstancepresancelatex);
            viewHolder.logoPosition9 = (ImageView) convertView.findViewById(R.id.symbolerisquesubstancepresancepht);
            viewHolder.logoPosition10 = (ImageView) convertView.findViewById(R.id.symbolerisquesubstancepresance);
            convertView.setTag(viewHolder);
        }

        Produit dispositif = (Produit) getItem(position);

        if (dispositif != null) {
            viewHolder.nom.setText(dispositif.getDesignation_interne());
            viewHolder.type.setText(dispositif.getCategorie());
            viewHolder.fournisseur.setText(dispositif.getFournisseur());

            tableRowHautVisible = false;

            if (dispositif.isCondition_usage_unique()) {
                viewHolder.logoPosition1.setVisibility(View.VISIBLE);
                tableRowHautVisible = true;
            }

            if (dispositif.isCondition_Fragile()) {
                viewHolder.logoPosition2.setVisibility(View.VISIBLE);
                tableRowHautVisible = true;
            }

            if (dispositif.isTemperature_Ambiante()) {
                viewHolder.logoPosition3.setBackgroundResource(R.drawable.ic_symboletemperatureambiante);
                viewHolder.logoPosition3.setVisibility(View.VISIBLE);
                tableRowHautVisible = true;
            } else if (dispositif.isTemperature_Refrigere()) {
                viewHolder.logoPosition3.setBackgroundResource(R.drawable.ic_symboletemperaturerefrigere);
                viewHolder.logoPosition3.setVisibility(View.VISIBLE);
                tableRowHautVisible = true;
            }

            if (dispositif.isConservation_sec()) {
                viewHolder.logoPosition4.setVisibility(View.VISIBLE);
                tableRowHautVisible = true;
            }

            if (dispositif.isConservation_abri()) {
                viewHolder.logoPosition5.setVisibility(View.VISIBLE);
                tableRowHautVisible = true;
            }

            if (tableRowHautVisible) {
                viewHolder.tableRowHaut.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tableRowHaut.setVisibility(View.GONE);
            }

            tableRowBasVisible = false;
            if (!dispositif.isSterile()) {
                viewHolder.logoPosition6.setVisibility(View.VISIBLE);
                tableRowBasVisible = true;
            } else {
            }

            if (dispositif.isNePasResteriliser()) {
                viewHolder.logoPosition7.setVisibility(View.VISIBLE);
                tableRowBasVisible = true;
            }

            if (dispositif.isRisque_latex()) {
                viewHolder.logoPosition8.setVisibility(View.VISIBLE);
                tableRowBasVisible = true;
            }

            if (dispositif.isRisque_PHT()) {
                viewHolder.logoPosition9.setVisibility(View.VISIBLE);
                tableRowBasVisible = true;
            }

            // TO-DO gérer le symbole absence/presence
            if (tableRowBasVisible) {
                viewHolder.tableRowBas.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tableRowBas.setVisibility(View.GONE);
            }

            if (tableRowHautVisible || tableRowBasVisible) {
                viewHolder.separateur1.setVisibility(View.VISIBLE);
            } else {
                viewHolder.separateur1.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new DispositifFilter();

        return filter;
    }


    private class DispositifViewHolder {
        public TextView nom;
        public TextView type;
        public TextView fournisseur;
        public ImageView separateur1;
        public TableRow tableRowHaut;
        public TableRow tableRowBas;
        public ImageView logoPosition1;
        public ImageView logoPosition2;
        public ImageView logoPosition3;
        public ImageView logoPosition4;
        public ImageView logoPosition5;
        public ImageView logoPosition6;
        public ImageView logoPosition7;
        public ImageView logoPosition8;
        public ImageView logoPosition9;
        public ImageView logoPosition10;
    }

    private class DispositifFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String chaineToSearch = Normalizer.normalize(constraint.toString().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            FilterResults filterResults = new FilterResults();

            dispositifList.clear();

            for (Produit produitCourant : dispositifDeBaseList) {
                dispositifList.add(produitCourant);
            }

            if (chaineToSearch != null && chaineToSearch.toString().length() > 0) {

                List<Produit> produitTrouveList = new ArrayList<>();

                for (Produit produitCourant : dispositifList) {
                    // Vérifie le début du premier mot
                    String produitDesignation = Normalizer.normalize(produitCourant.getDesignation_ext().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                    if (produitDesignation.startsWith(chaineToSearch)) {
                        produitTrouveList.add(produitCourant);
                    } else {
                        // Vérifie le début de chaque mot
                        final String[] words = produitDesignation.split(" ");
                        for (String word : words) {
                            if (word.startsWith(chaineToSearch)) {
                                produitTrouveList.add(produitCourant);
                                break;
                            }
                        }
                    }
                }

                filterResults.values = produitTrouveList;
                filterResults.count = produitTrouveList.size();
            } else {
                filterResults.values = dispositifDeBaseList;
                filterResults.count = dispositifDeBaseList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dispositifList.clear();
            if (results.values != null) {
                for (Produit produitCourant : (List<Produit>) results.values) {
                    add(produitCourant);
                }
            }
            notifyDataSetChanged();
        }
    }
}
