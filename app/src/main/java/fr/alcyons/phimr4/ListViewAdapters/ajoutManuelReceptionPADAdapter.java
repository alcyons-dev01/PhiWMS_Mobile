package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.R;

public class ajoutManuelReceptionPADAdapter extends ArrayAdapter {

    public List<PH_Reliquat> list_reliquat;
    Context context;
    SQLiteDatabase db;

    public ajoutManuelReceptionPADAdapter(Context context, List<PH_Reliquat> liste_reliquat, SQLiteDatabase db) {
        super(context, 0, liste_reliquat);
        this.list_reliquat = liste_reliquat;
        this.context = context;
        this.db = db;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_produit_reception_pad_add_manuel, parent, false);
        }

        ProduitReceptionPADViewHolder viewHolder = (ProduitReceptionPADViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ProduitReceptionPADViewHolder();
            viewHolder.designationProduit = (TextView) convertView.findViewById(R.id.designationProduit);
            viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
            viewHolder.conditionnementProduit = (TextView) convertView.findViewById(R.id.conditionnementProduit);
            viewHolder.colis = (TextView) convertView.findViewById(R.id.colis);
            convertView.setTag(viewHolder);
        }

        PH_Reliquat reliquat_courant = (PH_Reliquat) getItem(position);

        int nb_colis = recupererNbColis(reliquat_courant.getProduitID(), reliquat_courant.getQteReliquat_X());
        viewHolder.colis.setText(String.valueOf(nb_colis));
        viewHolder.designationProduit.setText(reliquat_courant.getDesignationCourte());
        viewHolder.referenceProduit.setText(reliquat_courant.getProduit_Reference());
        viewHolder.conditionnementProduit.setText(String.valueOf(reliquat_courant.getQteReliquat_X()));

        return convertView;
    }

    @Override
    public void clear() {
        list_reliquat.clear();
    }

    private class ProduitReceptionPADViewHolder {
        public TextView designationProduit;
        public TextView referenceProduit;
        public TextView conditionnementProduit;
        public TextView colis;
    }

    public int recupererNbColis(int produitID, double qte) {
        int nbColis = 0;

        int conditionnementAchat = 0;
        int quantite = (int) qte;

        if (produitID != 0) {
            Produit produitCorrespondant = ProduitOpenHelper.getProduitByID(db, produitID);
            conditionnementAchat = produitCorrespondant.getCond_achat();
            if (conditionnementAchat == 0) {
                conditionnementAchat = (int) produitCorrespondant.getCond_distrib();
            }
        }
        if (quantite != 0 && conditionnementAchat != 0) {
            nbColis = quantite / conditionnementAchat;
            nbColis = (int) Math.ceil(nbColis);
        }
        if (quantite != 0) {
            if (nbColis == 0) {
                nbColis = 1;
            }
        }

        return nbColis;
    }
}