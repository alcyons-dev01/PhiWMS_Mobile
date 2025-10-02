package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.OriginalActivity;
import fr.alcyons.phiwms_mobile.Outils.MedicalObjective;
import fr.alcyons.phiwms_mobile.R;

public class Produit_IdentificationParScanAdapter extends ArrayAdapter {

    public List<Produit> produits;
    Context context;
    SQLiteDatabase db;

    public Produit_IdentificationParScanAdapter(Context context, List<Produit> produits, SQLiteDatabase db) {
        super(context, 0, produits);
        this.produits = produits;
        this.context = context;
        this.db = db;
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
            viewHolder.gtinProduit.setTextColor(context.getResources().getColorStateList(R.color.noir));
        }
        else
        {
            viewHolder.gtinProduit.setText(produitCourant.getGTIN());
            viewHolder.gtinProduit.setTextColor(context.getResources().getColorStateList(R.color.vert3));
        }

        return convertView;
    }

    private class ProduitViewHolder {
        public TextView nom;
        public TextView refProduit;
        public TextView fournisseurProduit;
        public TextView gtinProduit;
        public ImageView photoProduit;
        public LinearLayout layoutDesignation;
    }
}
