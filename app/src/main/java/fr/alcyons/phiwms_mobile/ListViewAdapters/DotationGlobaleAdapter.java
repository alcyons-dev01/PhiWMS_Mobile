package fr.alcyons.phiwms_mobile.ListViewAdapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Demande_PUI;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.DemandeDotationGlobale.InformationDotationServiceActivity;
import fr.alcyons.phiwms_mobile.R;

public class DotationGlobaleAdapter extends ArrayAdapter {
    public List<PH_Preparation_Ligne> phPreparationLigneList;
    public List<PH_Preparation_Ligne> phPreparationLigneOriginalList;
    Context context;
    Demande_PUIFilter filter;
    Demande_PUIViewHolder viewHolderAModifier;
    Demande_PUI demandePUI;
    SQLiteDatabase db;
    Utilisateur utilisateur;
    public DotationGlobaleAdapter(Context context, List<PH_Preparation_Ligne> phPreparationLigneList, SQLiteDatabase db, Utilisateur utilisateur) {
        super(context, 0, phPreparationLigneList);
        this.phPreparationLigneList = phPreparationLigneList;
        this.context = context;

        this.db = db;
        this.utilisateur = utilisateur;
        this.phPreparationLigneOriginalList = new ArrayList<>();
        phPreparationLigneOriginalList.addAll(this.phPreparationLigneList);

        filter = null;
        viewHolderAModifier = null;
        demandePUI = null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_dotation_globale, parent, false);

        Demande_PUIViewHolder viewHolder = (Demande_PUIViewHolder) convertView.getTag();
        if(viewHolder == null)
        {
            // Récupération des objets graphiques
            viewHolder = new Demande_PUIViewHolder();
            viewHolder.designation = convertView.findViewById(R.id.designation);
            viewHolder.reference = convertView.findViewById(R.id.reference);
            viewHolder.qteConditionnement = convertView.findViewById(R.id.qteConditionnement);
            viewHolder.qte_a_preparer = convertView.findViewById(R.id.qte_a_preparer);
            viewHolder.separateur_iv = convertView.findViewById(R.id.separateur);
            convertView.setTag(viewHolder);
        }

        final PH_Preparation_Ligne PreparationLigneCourant = (PH_Preparation_Ligne) getItem(position);

        if(PreparationLigneCourant != null)
        {
            // Affichage des valeurs
            viewHolder.designation.setText(PreparationLigneCourant.getProduitDesignation());
            viewHolder.reference.setText(PreparationLigneCourant.getProduitReference());
            viewHolder.qteConditionnement.setText("(x"+ (int) PreparationLigneCourant.getProduitCondDistrib() +")");

            if(PreparationLigneCourant.getQte_StockSaisie() == -1)
            {
                viewHolder.separateur_iv.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.background_gestion_statut_action_soumis, null));
                viewHolder.qte_a_preparer.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.bleu_clair_alcyons,null));
            }
            else
            {
                viewHolder.separateur_iv.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.background_gestion_statut_action_valider, null));
                viewHolder.qte_a_preparer.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.vert,null));
            }

            viewHolder.qte_a_preparer.setText(String.valueOf(PreparationLigneCourant.getQte_Demander()));
            viewHolder.qte_a_preparer.clearFocus();
        }

        ((InformationDotationServiceActivity) context).gestionCompteur();

        return convertView;
    }

    public static class Demande_PUIViewHolder {

        public TextView designation;

        public TextView reference;
        public TextView qteConditionnement;
        public TextView qte_a_preparer;

        public ImageView separateur_iv;

    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new Demande_PUIFilter();

        return filter;
    }

    private class Demande_PUIFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String chaineAtrouver = constraint.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            phPreparationLigneList.clear();
            for (PH_Preparation_Ligne preparation_ligne_courant : phPreparationLigneOriginalList
            ) {
                phPreparationLigneList.add(preparation_ligne_courant);
            }

            if (!chaineAtrouver.isEmpty()) {
                List<PH_Preparation_Ligne> founded = new ArrayList<>();

                for (PH_Preparation_Ligne preparation_ligne_courant : phPreparationLigneList) {

                    // Vérifie le début du premier mot
                    String produitDesignation = preparation_ligne_courant.getProduitDesignation().toLowerCase();

                    if (produitDesignation.startsWith(chaineAtrouver)) {
                        founded.add(preparation_ligne_courant);
                    } else if (preparation_ligne_courant.getProduitCategorie().toLowerCase().contains(chaineAtrouver)) {
                        founded.add(preparation_ligne_courant);
                    } else {
                        /* Vérifie le début de chaque mot */
                        final String[] words = produitDesignation.split(" ");
                        for (String word : words) {
                            if (word.startsWith(chaineAtrouver)) {
                                founded.add(preparation_ligne_courant);
                                break;
                            }
                        }
                    }
                }

                filterResults.values = founded;
                filterResults.count = founded.size();
            } else {
                filterResults.values = phPreparationLigneOriginalList;
                filterResults.count = phPreparationLigneOriginalList.size();
            }
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            phPreparationLigneList.clear();
            if(results.values != null)
            {
                for (PH_Preparation_Ligne preparation_ligne : (List<PH_Preparation_Ligne>) results.values) {
                    add(preparation_ligne);
                }
            }

            notifyDataSetChanged();
        }

    }
}