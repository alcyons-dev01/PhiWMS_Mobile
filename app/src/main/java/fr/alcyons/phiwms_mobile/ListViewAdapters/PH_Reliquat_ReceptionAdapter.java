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

import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.R;

public class PH_Reliquat_ReceptionAdapter extends ArrayAdapter {

    public List<PH_Reliquat_Reception_Adapte> phReliquatReceptionAdapteList;
    public List<PH_Reliquat_Reception_Adapte.Lot> lotList;
    public List<PH_Reliquat_Reception_Adapte.ZoneEtEmplacement> zoneEtEmplacementList;
    public List<PH_Reliquat_ReceptionViewHolder> viewHolderList;
    public PH_Reliquat_ReceptionViewHolder viewHolder;
    Context context;
    SQLiteDatabase db;
    PH_Reliquat phReliquat;
    PH_Reliquat_Reception_Adapte phReliquatReceptionAdapte;


    public PH_Reliquat_ReceptionAdapter(Context context, SQLiteDatabase db, List<PH_Reliquat_Reception_Adapte> phReliquatReceptionAdapteList) {
        super(context, 0, phReliquatReceptionAdapteList);
        this.context = context;
        this.db = db;
        this.phReliquatReceptionAdapteList = new ArrayList<>();
        this.phReliquatReceptionAdapteList = phReliquatReceptionAdapteList;

        this.viewHolderList = new ArrayList<>();
        for (PH_Reliquat_Reception_Adapte phReliquatReceptionAdapte : phReliquatReceptionAdapteList) {
            PH_Reliquat_ReceptionViewHolder viewHolder = new PH_Reliquat_ReceptionViewHolder();
            viewHolderList.add(viewHolder);
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = null;
        phReliquatReceptionAdapte = (PH_Reliquat_Reception_Adapte) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_ph_reliquat_reception_pui, parent, false);

            viewHolder = viewHolderList.get(position);

            // Récupération des objets graphiques
            viewHolder.designation = (TextView) convertView.findViewById(R.id.designationProduit);
            viewHolder.reference = (TextView) convertView.findViewById(R.id.reference);
            viewHolder.nbReliquat = (TextView) convertView.findViewById(R.id.nbReliquat);
            viewHolder.QteDemandee = (TextView) convertView.findViewById(R.id.QteDemandee);
            viewHolder.emplacementParDefaut = (TextView) convertView.findViewById(R.id.emplacementParDefaut);
            viewHolder.colis = (TextView) convertView.findViewById(R.id.colis);
            viewHolder.linear_principal = (LinearLayout) convertView.findViewById(R.id.linear_principal);
            viewHolder.layoutColis = (LinearLayout) convertView.findViewById(R.id.layoutColis);

            phReliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatReceptionAdapte.getPhReliquatUID());

            if(phReliquat != null)
            {
                Produit produit_courant = ProduitOpenHelper.getProduitByID(db, phReliquat.getProduitID());

                lotList = new ArrayList<>();
                lotList.addAll(phReliquatReceptionAdapte.getlotList());

                viewHolder.designation.setText(phReliquat.getdesignationCourte());
                viewHolder.reference.setText(phReliquat.getProduit_Reference());
                viewHolder.emplacementParDefaut.setText(produit_courant.getEmplacement_PUI_Defaut());
                viewHolder.nbReliquat.setText(String.valueOf(phReliquat.getQteLivraison()));
                if(phReliquat.getQteLivraison() == 0)
                {
                    viewHolder.emplacementParDefaut.setVisibility(View.VISIBLE);
                    viewHolder.linear_principal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                }
                else
                {
                    viewHolder.emplacementParDefaut.setVisibility(View.GONE);
                    viewHolder.linear_principal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                    viewHolder.nbReliquat.setVisibility(View.VISIBLE);
                }
                viewHolder.QteDemandee.setText(String.valueOf(phReliquat.getQteCommande()));

                if(phReliquat.getQteReliquat_X() == 0)
                {
                    viewHolder.nbReliquat.setTextColor(context.getResources().getColor(R.color.vert));
                    viewHolder.QteDemandee.setVisibility(View.GONE);
                    viewHolder.linear_principal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
                    viewHolder.emplacementParDefaut.setVisibility(View.GONE);
                }

                int nombreColisProduit = recupererNbColis(phReliquat.getProduitID(), phReliquat.getQteReliquat_X());
                viewHolder.colis.setText(String.valueOf(nombreColisProduit));

                if(nombreColisProduit == 0)
                {
                }
                else
                {
                    viewHolder.layoutColis.setVisibility(View.VISIBLE);
                }
            }
        }
        return convertView;
    }

    public class PH_Reliquat_ReceptionViewHolder {
        public TextView designation;
        public TextView reference;
        public TextView nbReliquat;
        public TextView QteDemandee;
        public TextView emplacementParDefaut;
        public TextView colis;
        public LinearLayout linear_principal;
        public LinearLayout layoutColis;
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