package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class PH_Preparation_Ligne_PreparationLotAdapter2025 extends BaseAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    public ArrayList<PH_Preparation_Ligne> listePreparationLigne = new ArrayList<PH_Preparation_Ligne>();
    public TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    Context context;
    SQLiteDatabase db;
    private LayoutInflater mInflater;
    Utilisateur utilisateur;
    public List<PH_Preparation_Ligne_Preparation_Adapte> ph_preparation_lignes_Adaptes;
    public int compteurItem;
    public List<PH_PreparationLigneViewHolder> ph_preparation_ligneViewHolderList;

    public PH_Preparation_Ligne_PreparationLotAdapter2025(Context context, SQLiteDatabase database, Utilisateur utilisateur) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = database;
        this.context = context;
        this.utilisateur = utilisateur;
        this.ph_preparation_lignes_Adaptes = new ArrayList<>();
        this.compteurItem = 0;
        this.ph_preparation_ligneViewHolderList = new ArrayList<>();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void addItem(final PH_Preparation_Ligne_Preparation_Adapte item) {
        ph_preparation_lignes_Adaptes.add(item);
        compteurItem ++;
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final PH_Preparation_Ligne_Preparation_Adapte item) {
        ph_preparation_lignes_Adaptes.add(item);
        sectionHeader.add(ph_preparation_lignes_Adaptes.size() - 1);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getCount() {
        return ph_preparation_lignes_Adaptes.size();
    }

    @Override
    public PH_Preparation_Ligne_Preparation_Adapte getItem(int position) {
        return ph_preparation_lignes_Adaptes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int rowType = getItemViewType(position);

        if (convertView == null) {
            switch (rowType) {
                case TYPE_ITEM:
                    convertView =  mInflater.inflate(R.layout.row_dotation, parent, false);
                    break;
                case TYPE_HEADER:
                    convertView = mInflater.inflate(R.layout.row_header_ph_preparation_plein_vide, parent, false);
                    break;
            }
        }
        PH_PreparationLigneViewHolder viewHolder = (PH_PreparationLigneViewHolder) convertView.getTag();

        if(viewHolder == null)
        {
            viewHolder = new PH_PreparationLigneViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView =  mInflater.inflate(R.layout.row_ph_preparation_ligne_preparation_lot, parent, false);
                    // Récupération des objets graphiques
                    viewHolder.designationProduit = (TextView) convertView.findViewById(R.id.designationProduit);
                    viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
                    viewHolder.QteDemandee = (TextView) convertView.findViewById(R.id.QteDemandee);
                    viewHolder.emplacementParDefaut = (TextView) convertView.findViewById(R.id.emplacementParDefaut);
                    viewHolder.QtePreparer = (TextView) convertView.findViewById(R.id.QtePreparer);
                    viewHolder.linear_principal = (LinearLayout) convertView.findViewById(R.id.linear_principal);
                    viewHolder.colis = (TextView) convertView.findViewById(R.id.colis);
                    break;
                case TYPE_HEADER:
                    convertView = mInflater.inflate(R.layout.row_header_zone, parent, false);
                    viewHolder.textZoneEmplacement = (TextView) convertView.findViewById(R.id.textZoneEmplacement);
                    break;
            }

            ph_preparation_ligneViewHolderList.add(viewHolder);
            convertView.setTag(viewHolder);
        }

        if (rowType == TYPE_ITEM) {
            PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigne_adapte = (PH_Preparation_Ligne_Preparation_Adapte) getItem(position);
            PH_Preparation_Ligne ph_preparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigne_adapte.getPh_preparationLigneID());
            viewHolder.designationProduit.setText(ph_preparationLigne.getProduitDesignation());
            viewHolder.referenceProduit.setText(ph_preparationLigne.getProduitReference());
            viewHolder.QteDemandee.setText(String.valueOf((int) ph_preparationLigne.getQte_RAL()));
            int nbDetail = 0;

            Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
            String emplacementpardefaut = produit.getEmplacement_PUI_Defaut();
            if (emplacementpardefaut == null || emplacementpardefaut.contentEquals("")) {
                PH_Preparation preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparationLigne.getPreparationID());
                Depot depot = DepotOpenHelper.getDepotParReference(db, preparation_courante.getDepotOrigineReference());
                List<Stock_Lot_Emplacement_Light> stockLotEmplacementLights = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
                if (stockLotEmplacementLights.size() > 0) {
                    emplacementpardefaut = stockLotEmplacementLights.get(0).getEmplacement();
                } else {
                    viewHolder.emplacementParDefaut.setVisibility(View.GONE);
                }
            }

            viewHolder.emplacementParDefaut.setText(emplacementpardefaut);

            if (ph_preparationLigne.getQte_APreparer() != 0) {
                viewHolder.QtePreparer.setText(String.valueOf(ph_preparationLigne.getQte_RAL() - ph_preparationLigne.getQte_APreparer()));
                viewHolder.QtePreparer.setTextColor(context.getResources().getColor(R.color.orange2));
                viewHolder.linear_principal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                viewHolder.emplacementParDefaut.setVisibility(View.GONE);
            } else if (ph_preparationLigne.getQte_APreparer() == 0) {
                viewHolder.QtePreparer.setText(String.valueOf(ph_preparationLigne.getQte_RAL()));
                viewHolder.QtePreparer.setTextColor(context.getResources().getColor(R.color.vert));
                viewHolder.QteDemandee.setVisibility(View.GONE);
                viewHolder.linear_principal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
                viewHolder.emplacementParDefaut.setVisibility(View.GONE);
            } else {
                viewHolder.QtePreparer.setVisibility(View.GONE);
            }

            int nombreColisProduit = recupererNbColis(ph_preparationLigne.getProduitID(), ph_preparationLigne.getQte_APreparer());
            viewHolder.colis.setText(String.valueOf(nombreColisProduit));

        } else if (rowType == TYPE_HEADER) {
            // Gestion des dates
            PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigne_adapte = (PH_Preparation_Ligne_Preparation_Adapte) getItem(position);
            PH_Preparation_Ligne ph_preparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigne_adapte.getPh_preparationLigneID());
            Produit produitcourant = ProduitOpenHelper.getProduitByID(db, ph_preparationLigne.getProduitID());
            String textZone = "";
            if(produitcourant != null)
            {
                String zonecourante = produitcourant.getZone_PUI_Defaut();
                String emplacementcourant = produitcourant.getEmplacement_PUI_Defaut();

                if(zonecourante != null && !zonecourante.contentEquals(""))
                    textZone = textZone + zonecourante + " - ";

                if(emplacementcourant != null && !emplacementcourant.contentEquals(""))
                    textZone = textZone + emplacementcourant;
            }

            viewHolder.textZoneEmplacement.setText(textZone);
        }
        return convertView;
    }

    public int recupererNbColis(int produitID, double qte) {
        int nbColis = 0;

        Produit produitCorrespondant = null;

        int conditionnementDistrib = 0;

        if (produitID != 0) {
            produitCorrespondant = ProduitOpenHelper.getProduitByID(db, produitID);
            conditionnementDistrib = (int) produitCorrespondant.getCond_distrib();

        }
        if (qte != 0 && conditionnementDistrib != 0) {
            nbColis = (int) (qte / conditionnementDistrib);
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
        public TextView textZoneEmplacement;
        public LinearLayout linear_principal;
        public TextView colis;
        //  public LinearLayout listLots;

    }
}
