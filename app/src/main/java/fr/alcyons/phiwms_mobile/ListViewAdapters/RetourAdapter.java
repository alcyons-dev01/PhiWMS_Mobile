package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.annotation.SuppressLint;
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

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.R;

public class RetourAdapter extends ArrayAdapter implements Filterable {

    public List<Retour> retourList;
    public List<Retour> retourDeBaseList;
    public List<Retour_Ligne> retourLigneList;
    Context context;
    SQLiteDatabase db;
    RetourFilter filter;

    public RetourAdapter(Context context, SQLiteDatabase database, List<Retour> retourList) {
        super(context, 0, retourList);
        this.context = context;
        this.db = database;

        this.retourList = retourList;
        this.retourDeBaseList = new ArrayList<>();
        this.retourDeBaseList.addAll(retourList);

        this.filter = null;
    }


    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new RetourFilter();

        return filter;
    }

    @NonNull
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_retour, parent, false);
        }

        RetourViewHolder viewHolder = (RetourViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new RetourViewHolder();
            viewHolder.intitule = (TextView) convertView.findViewById(R.id.intitule);
            viewHolder.sommeProduit = (TextView) convertView.findViewById(R.id.sommeProduit);
            viewHolder.motif = (TextView) convertView.findViewById(R.id.motif);
            viewHolder.numero = (TextView) convertView.findViewById(R.id.numero);
            viewHolder.depotOrigine = (TextView) convertView.findViewById(R.id.depotOrigine);
            convertView.setTag(viewHolder);
        }

        Retour retour = (Retour) getItem(position);

        if (retour != null) {
            retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retour);


            int size = retourLigneList.size();

            String[] intitule_tab = retour.getIntitule().split(":");
            String intitule_split = intitule_tab[intitule_tab.length-1];
            String depot_origine = intitule_tab[0];

            viewHolder.sommeProduit.setText(String.valueOf(size));
            viewHolder.intitule.setText(intitule_split);
            viewHolder.motif.setText(retour.getMotif());
            viewHolder.numero.setText("#"+retour.getNumero());
            viewHolder.depotOrigine.setText(depot_origine);
        }

        return convertView;
    }

    private class RetourFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            retourList.clear();
            retourList.addAll(retourDeBaseList);
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();

            if (!constraint.toString().isEmpty()) {
                List<Retour> founded = new ArrayList<>();
                for (Retour retour : retourList) {
                    // Vérifie le début du premier mot
                    String valueText = retour.getChaineFiltreQuarantaine(db).toLowerCase();
                    if (valueText.startsWith(String.valueOf(constraint))) {
                        founded.add(retour);
                    } else if (retour.getIntitule().toLowerCase().contains(constraint)) {
                        founded.add(retour);
                    } else {
                        /* Vérifie le début de chaque mot */
                        final String[] words = valueText.split(" ");
                        for (String word : words) {
                            if (word.startsWith(String.valueOf(constraint))) {
                                founded.add(retour);
                                break;
                            }
                        }
                    }
                }

                result.values = founded;
                result.count = founded.size();
            } else {
                result.values = retourDeBaseList;
                result.count = retourDeBaseList.size();
            }
            return result;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.values != null) {
                retourList.clear();
                for (Retour retour : (List<Retour>) results.values) {
                    add(retour);
                }
                notifyDataSetChanged();
            }
        }

    }
    private static class RetourViewHolder {
        public TextView intitule;
        public TextView motif;
        public TextView numero;
        public TextView sommeProduit;
        public TextView depotOrigine;
    }

}
