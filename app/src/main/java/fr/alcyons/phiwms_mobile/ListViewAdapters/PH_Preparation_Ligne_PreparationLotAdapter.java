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

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by jessica on 27/09/2017.
 */

public class PH_Preparation_Ligne_PreparationLotAdapter extends ArrayAdapter {

    public List<PH_Preparation_Ligne_Preparation_Adapte> ph_preparation_lignes_Adaptes;

    public List<PH_PreparationLigneViewHolder> ph_preparation_ligneViewHolderList;
    public PH_PreparationLigneViewHolder viewHolder;
    public PH_Preparation_Ligne ph_preparationLigne;
    Context context;
    SQLiteDatabase db;

    public PH_Preparation_Ligne_PreparationLotAdapter(Context context, List<PH_Preparation_Ligne_Preparation_Adapte> ph_preparation_lignes_Adaptes, SQLiteDatabase db) {
        super(context, 0, ph_preparation_lignes_Adaptes);
        this.ph_preparation_lignes_Adaptes = ph_preparation_lignes_Adaptes;
        this.context = context;
        this.db = db;
        ph_preparation_ligneViewHolderList = new ArrayList<>();
        for (int i = 0; i < ph_preparation_lignes_Adaptes.size(); i++) {
            PH_PreparationLigneViewHolder viewHolder = new PH_PreparationLigneViewHolder();
            ph_preparation_ligneViewHolderList.add(viewHolder);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = null;
        if (convertView == null) {

            viewHolder = ph_preparation_ligneViewHolderList.get(position);

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_ph_preparation_ligne_preparation_lot, parent, false);

            // Récupération des objets graphiques
            viewHolder.designationProduit = (TextView) convertView.findViewById(R.id.designationProduit);
            viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
            viewHolder.QteDemandee = (TextView) convertView.findViewById(R.id.QteDemandee);
            viewHolder.emplacementParDefaut = (TextView) convertView.findViewById(R.id.emplacementParDefaut);
            viewHolder.QtePreparer = (TextView) convertView.findViewById(R.id.QtePreparer);
            viewHolder.linear_principal = (LinearLayout) convertView.findViewById(R.id.linear_principal);
            viewHolder.colis = (TextView) convertView.findViewById(R.id.colis);

            PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapteCourant = (PH_Preparation_Ligne_Preparation_Adapte) getItem(position);
            ph_preparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigneAdapteCourant.getPh_preparationLigneID());

            // Affichage des valeurs
            viewHolder.designationProduit.setText(ph_preparationLigne.getProduitDesignation());
            viewHolder.referenceProduit.setText(ph_preparationLigne.getProduitReference());
            viewHolder.QteDemandee.setText(String.valueOf((int) ph_preparationLigne.getQte_RAL()));

            Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
            String emplacementpardefaut = produit.getEmplacement_PUI_Defaut();
            if(emplacementpardefaut == null || emplacementpardefaut.contentEquals(""))
            {
                PH_Preparation preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparationLigne.getPreparationID());
                Depot depot = DepotOpenHelper.getDepotParReference(db, preparation_courante.getDepotOrigineReference());
                List<Stock_Lot_Emplacement_Light> stockLotEmplacementLights = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
                if(stockLotEmplacementLights.size() >0)
                {
                    emplacementpardefaut = stockLotEmplacementLights.get(0).getEmplacement();
                }
                else
                {
                    viewHolder.emplacementParDefaut.setVisibility(View.GONE);
                }
            }

            viewHolder.emplacementParDefaut.setText(emplacementpardefaut);

            if(ph_preparationLigne.getQte_APreparer() != 0)
            {
                viewHolder.QtePreparer.setText(String.valueOf(ph_preparationLigne.getQte_RAL()-ph_preparationLigne.getQte_APreparer()));
                viewHolder.QtePreparer.setTextColor(context.getResources().getColor(R.color.orange2));
                viewHolder.linear_principal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                viewHolder.emplacementParDefaut.setVisibility(View.GONE);
            }
            else if(ph_preparationLigne.getQte_APreparer() == 0)
            {
                viewHolder.QtePreparer.setText(String.valueOf(ph_preparationLigne.getQte_RAL()));
                viewHolder.QtePreparer.setTextColor(context.getResources().getColor(R.color.vert));
                viewHolder.QteDemandee.setVisibility(View.GONE);
                viewHolder.linear_principal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
                viewHolder.emplacementParDefaut.setVisibility(View.GONE);
            }
            else
            {
                viewHolder.QtePreparer.setVisibility(View.GONE);
            }

            int nombreColisProduit = recupererNbColis(ph_preparationLigne.getProduitID(), ph_preparationLigne.getQte_APreparer());
            viewHolder.colis.setText(String.valueOf(nombreColisProduit));


        }
        return convertView;
    }

    // Permet de calculer les nombre de colis en fonction du conditionnement du produit
    public int recupererNbColis(int produitID, double qte) {
        int nbColis = 0;

        Produit produitCorrespondant = null;

        int conditionnementAchat = 0;

        if (produitID != 0) {
            produitCorrespondant = ProduitOpenHelper.getProduitByID(db, produitID);
            conditionnementAchat = produitCorrespondant.getCond_achat();
            if (conditionnementAchat == 0) {
                conditionnementAchat = (int) produitCorrespondant.getCond_distrib();
            }
        }
        if (qte != 0 && conditionnementAchat != 0) {
            nbColis = (int) (qte / conditionnementAchat);
            nbColis = (int) Math.ceil(nbColis);
        }
        if (qte != 0) {
            if (nbColis == 0) {
                nbColis = 1;
            }
        }

        return nbColis;
    }


    public class PH_PreparationLigneViewHolder {
        public TextView designationProduit;
        public TextView referenceProduit;
        public TextView QteDemandee;
        public TextView emplacementParDefaut;
        public TextView QtePreparer;
        public LinearLayout linear_principal;
        public TextView colis;
      //  public LinearLayout listLots;

    }
}