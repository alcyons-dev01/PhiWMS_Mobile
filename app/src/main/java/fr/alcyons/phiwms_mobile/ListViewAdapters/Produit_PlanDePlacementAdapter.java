package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.R;
public class Produit_PlanDePlacementAdapter extends ArrayAdapter {

    public ArrayList<String> produits;
    public ArrayList<String> produitsOriginal;
    public List<Produit> produitsScannes;
    Context context;
    Depot depotCourant;
    private ArrayList<String> produitsAffiches;
    SQLiteDatabase db;
    PlanPlacementFilter filter;


    public Produit_PlanDePlacementAdapter(SQLiteDatabase db, Context context, ArrayList<String> produits, List<Produit> produitsScannes, Depot depotCourant) {
        super(context, 0, produits);
        this.produits = produits;
        this.produitsScannes = produitsScannes;
        this.context = context;
        this.depotCourant = depotCourant;
        this.produitsAffiches = new ArrayList<>();
        this.db = db;
        this.filter = null;
        this.produitsOriginal = new ArrayList<>();
        this.produitsOriginal.addAll(produits);
    }

    @Override
    public PlanPlacementFilter getFilter() {
        if (filter == null)
            filter = new PlanPlacementFilter();
        return filter;
    }

    @Override
    public int getCount() {
        return produits.size();
    }

    @Override
    public String getItem(int position) {
        return produits.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_produit_plan_placement, parent, false);
        }

        ProduitViewHolder viewHolder = (ProduitViewHolder) convertView.getTag();
        if (viewHolder == null) {
            // Récupération des éléments graphiques
            viewHolder = new ProduitViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nomProduit);
            viewHolder.refProduit = (TextView) convertView.findViewById(R.id.refProduit);
            viewHolder.zonePUI = (TextView) convertView.findViewById(R.id.nomZonePUI);
            viewHolder.emplacementPUI = (TextView) convertView.findViewById(R.id.nomEmplacementPUI);
            viewHolder.linearPrincipal = (LinearLayout) convertView.findViewById(R.id.linearPrincipal);
            convertView.setTag(viewHolder);
        }

        String ligne = getItem(position);
        String[] tabLigne = ligne.split("_");

        Produit produitCourant = ProduitOpenHelper.getProduitByID(db, Integer.parseInt(tabLigne[0]));


        // Affichage des valeurs du produit
        viewHolder.nom.setText(produitCourant.getDesignation_interne());
        viewHolder.refProduit.setText(produitCourant.getRef_fourni());


        viewHolder.zonePUI.setText(tabLigne[1]);
        viewHolder.emplacementPUI.setText(tabLigne[2]);

        boolean selection = false;
        for(Produit temp : produitsScannes)
        {
            if(temp.getID_produit() == produitCourant.getID_produit())
            {
                selection = true;
                break;
            }
        }

        if(selection)
            viewHolder.linearPrincipal.setBackgroundResource(R.drawable.background_element_liste_selection);
        else
            viewHolder.linearPrincipal.setBackgroundResource(R.drawable.background_element_liste);

        return convertView;
    }

    private class ProduitViewHolder {
        public TextView nom;
        public TextView refProduit;
        public TextView zonePUI;
        public TextView emplacementPUI;
        public LinearLayout linearPrincipal;
    }

    private class PlanPlacementFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            produits.clear();
            for (String produitCourant : produitsOriginal
            ) {
                produits.add(produitCourant);
            }
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {
                List<String> founded = new ArrayList<>();
                for (String item : produits) {
                    String[] tabLigne = item.split("_");
                    Produit produitCourant = ProduitOpenHelper.getProduitByID(db, Integer.parseInt(tabLigne[0]));

                    if(String.valueOf(produitCourant.getID_produit()).contentEquals(String.valueOf(constraint))){
                        founded.add(item);
                    }
                    else if(produitCourant.getDesignation_interne().toLowerCase().contains(String.valueOf(constraint).toLowerCase())) {
                        founded.add(item);
                    } else if (produitCourant.getRef_fourni().toLowerCase().contains(String.valueOf(constraint).toLowerCase())) {
                        founded.add(item);
                    }
                }
                result.values = founded;
                result.count = founded.size();
            } else {
                result.values = produitsOriginal;
                result.count = produitsOriginal.size();
            }
            return result;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results != null && results.values != null) {
                produits.clear();
                for (String item : (List<String>) results.values) {
                    add(item);
                }

                notifyDataSetChanged();
            }
        }

    }

}
