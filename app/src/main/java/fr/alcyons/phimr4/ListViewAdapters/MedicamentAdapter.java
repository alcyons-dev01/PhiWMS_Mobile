package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.R;

/**
 * Created by quentinlanusse on 04/05/2017.
 */

public class MedicamentAdapter extends ArrayAdapter implements Filterable {

    public List<Produit> medicamentList;
    public List<Produit> medicamentDeBaseList;
    Context context;
    MedicamentAdapter.MedicamentFilter filter;

    public MedicamentAdapter(Context context, List<Produit> medicamentList) {
        super(context, 0, medicamentList);
        this.context = context;
        this.medicamentList = medicamentList;
        this.filter = null;
        // Permet de garder une version de la liste complète des éléments
        this.medicamentDeBaseList = new ArrayList<>();

        // La boucle foreach permet d'insérer les valeurs d'un objet dans l'autre, "medicamentsDeBase = medicaments" aurait "fusionné" les deux objets et modifier l'un des deux aurait modifié l'autre
        for (Produit medicament : medicamentList
                ) {
            this.medicamentDeBaseList.add(medicament);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_medicament, parent, false);
        }

        MedicamentAdapter.MedicamentViewHolder viewHolder = (MedicamentAdapter.MedicamentViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new MedicamentAdapter.MedicamentViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nomMedicament);
            viewHolder.alerte = (TextView) convertView.findViewById(R.id.alerte);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            convertView.setTag(viewHolder);
        }

        Produit medicament = (Produit) getItem(position);

        if (medicament != null) {
            viewHolder.nom.setText(medicament.getDesignation_interne());
            viewHolder.type.setText(medicament.getCategorie());
            String alerte = "";
            if (medicament.isArret_Commande()) {
                alerte += "ARRET-COMMANDE";
                if (medicament.isArret_Dis()) {
                    alerte += " & ";
                }
            }
            if (medicament.isArret_Dis()) {
                alerte += "ARRET-DISTRIBUTION";
            }
            if (alerte.contentEquals("")) {
                viewHolder.alerte.setVisibility(View.GONE);
            } else {
                viewHolder.alerte.setText(alerte);
                viewHolder.alerte.setVisibility(View.VISIBLE);
            }
        }


        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new MedicamentAdapter.MedicamentFilter();

        return filter;
    }

    private class MedicamentViewHolder {
        public TextView nom;
        public TextView type;
        public TextView alerte;
    }

    private class MedicamentFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String chaineToSearch = Normalizer.normalize(constraint.toString().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            FilterResults filterResults = new FilterResults();

            medicamentList.clear();

            for (Produit produitCourant : medicamentDeBaseList) {
                medicamentList.add(produitCourant);
            }

            if (chaineToSearch != null && chaineToSearch.toString().length() > 0) {

                List<Produit> produitTrouveList = new ArrayList<>();

                for (Produit produitCourant : medicamentList) {
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
                filterResults.values = medicamentDeBaseList;
                filterResults.count = medicamentDeBaseList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            medicamentList.clear();
            if (results.values != null) {
                for (Produit produitCourant : (List<Produit>) results.values) {
                    add(produitCourant);
                }
            }
            notifyDataSetChanged();
        }
    }
}
