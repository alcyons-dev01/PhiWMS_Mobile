package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Produit;
import com.example.phiwms_mobile.R;


/**
 * Created by olivier on 02/10/2017.
 */

public class Produit_DemandeParticuliereAdapter extends ArrayAdapter {

    public List<Produit> produits = new ArrayList<>();
    public List<Integer> produitSelect;
    public int compteur = 0;
    Context context;
    TextView nb_prod;
    View view = null;
    Boolean present = false;

    public Produit_DemandeParticuliereAdapter(Context context, List<Produit> produits, TextView nb_prod, List<Integer> produitSelect) {
        super(context, 0, produits);
        this.produits = produits;
        this.context = context;
        this.nb_prod = nb_prod;
        this.compteur = Integer.parseInt(nb_prod.getText().toString());
        this.produitSelect = produitSelect;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_produit_demande_particuliere, parent, false);
        }

        if (produitSelect != null) {
            compteur = produitSelect.size();
            nb_prod.setText(String.valueOf(produitSelect.size()));
        } else {
            nb_prod.setText(String.valueOf(compteur));
        }


        ProduitViewHolder viewHolder = (ProduitViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = new ProduitViewHolder();
            viewHolder.nomProduit = (TextView) convertView.findViewById(R.id.nomProduit);
            viewHolder.produit_select = (CheckBox) convertView.findViewById(R.id.nb_prod_select);

        } else {

            view = convertView;
            ((ProduitViewHolder) view.getTag()).produit_select.setTag(produits.get(position));
        }

        final Produit ProduitCourant = (Produit) getItem(position);
        final ProduitViewHolder finalViewHolder = viewHolder;

        viewHolder.produit_select.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;
                present = false;
                for (Integer i : produitSelect) {
                    if (i == ProduitCourant.getID_produit()) {
                        present = true;
                    }
                }

                if (present == false) {
                    compteur++;
                    produitSelect.add(ProduitCourant.getID_produit());
                    finalViewHolder.produit_select.setChecked(true);
                } else {
                    for (int i = 0; i < produitSelect.size(); i++) {
                        Integer produit = produitSelect.get(i);
                        if (produit == ProduitCourant.getID_produit()) {
                            index = i;
                        }
                    }

                    compteur--;
                    produitSelect.remove(index);
                    finalViewHolder.produit_select.setChecked(false);
                }

                nb_prod.setText(String.valueOf(compteur));
            }
        });


        convertView.setTag(viewHolder);

        if (produitSelect.size() != 0) {
            if (produitSelect.indexOf(ProduitCourant.getID_produit()) != -1) {
                viewHolder.produit_select.setChecked(true);
            } else {
                viewHolder.produit_select.setChecked(false);
            }
        }

        viewHolder.nomProduit.setText(ProduitCourant.getDesignation_interne());

        return convertView;
    }


    private class ProduitViewHolder {
        public TextView nomProduit;
        public CheckBox produit_select;
    }

}
