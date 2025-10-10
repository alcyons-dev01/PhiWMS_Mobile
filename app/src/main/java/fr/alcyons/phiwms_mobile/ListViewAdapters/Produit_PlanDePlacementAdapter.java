package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.R;
public class Produit_PlanDePlacementAdapter extends ArrayAdapter {

    public List<Produit> produits;
    public List<Produit> produitsScannes;
    Context context;

    public Produit_PlanDePlacementAdapter(Context context, List<Produit> produits, List<Produit> produitsScannes) {
        super(context, 0, produits);
        this.produits = produits;
        this.produitsScannes = produitsScannes;
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
            viewHolder.zonePUI = (TextView) convertView.findViewById(R.id.nomZonePUI);
            viewHolder.emplacementPUI = (TextView) convertView.findViewById(R.id.nomEmplacementPUI);
            viewHolder.linearPrincipal = (LinearLayout) convertView.findViewById(R.id.linearPrincipal);
            convertView.setTag(viewHolder);
        }

        Produit produitCourant = (Produit) getItem(position);

        // Affichage des valeurs du produit
        viewHolder.nom.setText(produitCourant.getDesignation_interne());
        viewHolder.refProduit.setText(produitCourant.getRef_fourni());
        viewHolder.zonePUI.setText(produitCourant.getZone_PUI_Defaut());
        viewHolder.emplacementPUI.setText(produitCourant.getEmplacement_PUI_Defaut());

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
}
