package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.OriginalActivity;
import fr.alcyons.phiwms_mobile.Outils.MedicalObjective;
import fr.alcyons.phiwms_mobile.R;

public class Produit_IdentificationParScanAdapter extends ArrayAdapter {

    public List<Produit> produits;
    public List<Produit> ListeBaseProduits;
    Context context;
    SQLiteDatabase db;
    ReferenceFilter filter;

    public Produit_IdentificationParScanAdapter(Context context, List<Produit> produits, SQLiteDatabase db) {
        super(context, 0, produits);
        this.produits = produits;
        this.context = context;
        this.db = db;
        this.filter = null;
        this.ListeBaseProduits = new ArrayList<>();
        this.ListeBaseProduits.addAll(produits);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_produit_identification_scan, parent, false);
        }

        ProduitViewHolder viewHolder = (ProduitViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ProduitViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nomProduit);
            viewHolder.refProduit = (TextView) convertView.findViewById(R.id.refProduit);
            viewHolder.fournisseurProduit = (TextView) convertView.findViewById(R.id.fournisseurProduit);
            viewHolder.gtinProduit = (TextView) convertView.findViewById(R.id.gtinProduit);
            viewHolder.photoProduit = (ImageView) convertView.findViewById(R.id.photoProduit);
            viewHolder.layoutDesignation = (LinearLayout) convertView.findViewById(R.id.layoutDesignation);
            convertView.setTag(viewHolder);
        }

        Produit produitCourant = (Produit) getItem(position);

        //Récupération de la photo
        Depot depot = DepotOpenHelper.getDepotPUI(db);
        MedicalObjective medicalObjective = new MedicalObjective(getContext(), ((OriginalActivity) getContext()).utilisateurConnecte, depot, depot, produitCourant, true);
        Bitmap photo = null;

        if(photo != null)
        {
            viewHolder.photoProduit.setImageBitmap(photo);
        }
        else
        {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    viewHolder.layoutDesignation.getLayoutParams();
            params.weight = 4.0f;
            viewHolder.layoutDesignation.setLayoutParams(params);

            viewHolder.photoProduit.setVisibility(View.GONE);
        }

        viewHolder.nom.setText(produitCourant.getDesignation_interne());
        viewHolder.refProduit.setText(produitCourant.getRef_fourni());
        viewHolder.fournisseurProduit.setText(produitCourant.getFournisseur());
        if(produitCourant.getGTIN().contentEquals(""))
        {
            viewHolder.gtinProduit.setText(produitCourant.getCodeInconnue());
            viewHolder.gtinProduit.setTextColor(context.getResources().getColorStateList(R.color.orange));
        }
        else
        {
            viewHolder.gtinProduit.setText(produitCourant.getGTIN());
            viewHolder.gtinProduit.setTextColor(context.getResources().getColorStateList(R.color.vert));
        }

        return convertView;
    }

    public void replaceData(List<Produit> nouveaux) {
        ListeBaseProduits.clear();
        ListeBaseProduits.addAll(nouveaux);
        produits.clear();
        produits.addAll(nouveaux);
        notifyDataSetChanged();
    }

    private class ProduitViewHolder {
        public TextView nom;
        public TextView refProduit;
        public TextView fournisseurProduit;
        public TextView gtinProduit;
        public ImageView photoProduit;
        public LinearLayout layoutDesignation;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new ReferenceFilter();

        return filter;
    }

    private class ReferenceFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String chaineToSearch = Normalizer.normalize(constraint.toString().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            FilterResults filterResults = new FilterResults();

            produits.clear();

            for (Produit produitCourant : ListeBaseProduits) {
                produits.add(produitCourant);
            }

            if (chaineToSearch != null && chaineToSearch.toString().length() > 0) {

                List<Produit> produitTrouveList = new ArrayList<>();

                for (Produit produitCourant : produits) {
                    // Vérifie le début du premier mot
                    String produitDesignation = Normalizer.normalize(produitCourant.getDesignation_interne().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
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
                filterResults.values = ListeBaseProduits;
                filterResults.count = ListeBaseProduits.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            produits.clear();
            if (results.values != null) {
                for (Produit produitCourant : (List<Produit>) results.values) {
                    add(produitCourant);
                }
            }
            notifyDataSetChanged();
        }
    }
}
