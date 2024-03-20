package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.R;

/**
 * Created by quentinlanusse on 22/05/2017.
 */

public class Produit_PlanDePlacementAdapter extends ArrayAdapter {

    public List<Produit> produits;
    Context context;

    public Produit_PlanDePlacementAdapter(Context context, List<Produit> produits) {
        super(context, 0, produits);
        this.produits = produits;
        this.context = context;
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
            viewHolder.alerte = (TextView) convertView.findViewById(R.id.alerte);
            viewHolder.zonePUI = (TextView) convertView.findViewById(R.id.nomZonePUI);
            viewHolder.emplacementPUI = (TextView) convertView.findViewById(R.id.nomEmplacementPUI);
            convertView.setTag(viewHolder);
        }

        Produit produitCourant = (Produit) getItem(position);

        // Affichage des valeurs du produit
        viewHolder.nom.setText(produitCourant.getDesignation_interne());
        viewHolder.refProduit.setText(produitCourant.getRef_fourni());
        if (produitCourant.isArret_Commande() || produitCourant.isArret_Dis()) {
            if (produitCourant.isArret_Dis())
                viewHolder.alerte.setText("ARRET-DISTRIBUTION");
            else
                viewHolder.alerte.setText("ARRET-COMMANDE");
        } else {
            viewHolder.alerte.setVisibility(View.GONE);
        }
        viewHolder.zonePUI.setText(produitCourant.getZone_PUI_Defaut());
        viewHolder.emplacementPUI.setText(produitCourant.getEmplacement_PUI_Defaut());

        return convertView;
    }

    private class ProduitViewHolder {
        public TextView nom;
        public TextView refProduit;
        public TextView alerte;
        public TextView zonePUI;
        public TextView emplacementPUI;
    }
}
