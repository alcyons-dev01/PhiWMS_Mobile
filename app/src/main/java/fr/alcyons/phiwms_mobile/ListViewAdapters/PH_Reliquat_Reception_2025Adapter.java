package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class PH_Reliquat_Reception_2025Adapter extends BaseAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    public ArrayList<PH_Reliquat> listePhReliquat = new ArrayList<PH_Reliquat>();
    public TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    Context context;
    SQLiteDatabase db;
    private LayoutInflater mInflater;
    Utilisateur utilisateur;
    public List<PH_Reliquat> ph_reliquat_liste;
    public int compteurItem;
    public List<PH_Reliquat_Reception_2025Adapter.PH_Reliquat_ReceptionViewHolder> ph_reliquatViewHolderList;

    public PH_Reliquat_Reception_2025Adapter(Context context, SQLiteDatabase database, Utilisateur utilisateur) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = database;
        this.context = context;
        this.utilisateur = utilisateur;
        this.ph_reliquat_liste = new ArrayList<>();
        this.compteurItem = 0;
        this.ph_reliquatViewHolderList = new ArrayList<>();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void addItem(final PH_Reliquat item) {
        ph_reliquat_liste.add(item);
        compteurItem ++;
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final PH_Reliquat item) {
        ph_reliquat_liste.add(item);
        sectionHeader.add(ph_reliquat_liste.size() - 1);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getCount() {
        return ph_reliquat_liste.size();
    }

    @Override
    public PH_Reliquat getItem(int position) {
        return ph_reliquat_liste.get(position);
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
        PH_Reliquat_Reception_2025Adapter.PH_Reliquat_ReceptionViewHolder viewHolder = (PH_Reliquat_Reception_2025Adapter.PH_Reliquat_ReceptionViewHolder) convertView.getTag();

        if(viewHolder == null)
        {
            viewHolder = new PH_Reliquat_Reception_2025Adapter.PH_Reliquat_ReceptionViewHolder();
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

            ph_reliquatViewHolderList.add(viewHolder);
            convertView.setTag(viewHolder);
        }

        if (rowType == TYPE_ITEM) {
            PH_Reliquat ph_reliquat = (PH_Reliquat) getItem(position);
            if(ph_reliquat != null)
            {
                Commande commandeCourante = CommandeOpenHelper.getCommandeByNumero(db, ph_reliquat.getCommandeNumero());
                viewHolder.designationProduit.setText(ph_reliquat.getDesignationCourte().trim());
                viewHolder.referenceProduit.setText(ph_reliquat.getProduit_Reference().trim());
                viewHolder.QteDemandee.setText(String.valueOf((int) ph_reliquat.getQteReliquat_X()));
                int nbDetail = 0;

                Produit produit = ProduitOpenHelper.getProduitByID(db, ph_reliquat.getProduitID());
                String emplacementpardefaut = produit.getEmplacement_PUI_Defaut();
                if (emplacementpardefaut == null || emplacementpardefaut.contentEquals("")) {
                    Depot depot = DepotOpenHelper.getDepotParReference(db, commandeCourante.getRef_Depot_Dest());
                    List<Stock_Lot_Emplacement_Light> stockLotEmplacementLights = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
                    if (stockLotEmplacementLights.size() > 0) {
                        emplacementpardefaut = stockLotEmplacementLights.get(0).getEmplacement();
                    } else {
                        viewHolder.emplacementParDefaut.setVisibility(View.GONE);
                    }
                }

                viewHolder.emplacementParDefaut.setText(emplacementpardefaut);

                List<PH_Reliquat> listPhReliquat = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeCourante.getNumero(), ph_reliquat.getProduitID());
                int qteReceptionnee = 0;
                for(PH_Reliquat reliquatCourant : listPhReliquat)
                {
                    qteReceptionnee = qteReceptionnee + reliquatCourant.getQteLivraison();
                }

                if (ph_reliquat.getQteReliquat_X() != qteReceptionnee) {
                    viewHolder.QtePreparer.setText(String.valueOf(qteReceptionnee));
                    viewHolder.QtePreparer.setTextColor(context.getResources().getColor(R.color.orange2));
                    viewHolder.linear_principal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                    viewHolder.emplacementParDefaut.setVisibility(View.GONE);
                    viewHolder.QteDemandee.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.QtePreparer.setText(String.valueOf(ph_reliquat.getQteReliquat_X()));
                    viewHolder.QtePreparer.setTextColor(context.getResources().getColor(R.color.vert));
                    viewHolder.QteDemandee.setVisibility(View.GONE);
                    viewHolder.linear_principal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
                    viewHolder.emplacementParDefaut.setVisibility(View.GONE);
                }

                int nombreColisProduit = recupererNbColis(ph_reliquat.getProduitID(), (ph_reliquat.getQteReliquat_X()-qteReceptionnee));
                viewHolder.colis.setText(String.valueOf(nombreColisProduit));
            }
        } else if (rowType == TYPE_HEADER) {
            // Gestion des dates
            PH_Reliquat ph_reliquat = (PH_Reliquat) getItem(position);
            Produit produitcourant = ProduitOpenHelper.getProduitByID(db, ph_reliquat.getProduitID());
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

        int conditionnementAchat = 0;

        if (produitID != 0) {
            produitCorrespondant = ProduitOpenHelper.getProduitByID(db, produitID);
            conditionnementAchat = (int) produitCorrespondant.getCond_achat();

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

    public class PH_Reliquat_ReceptionViewHolder {
        public TextView designationProduit;
        public TextView referenceProduit;
        public TextView QteDemandee;
        public TextView emplacementParDefaut;
        public TextView QtePreparer;
        public TextView textZoneEmplacement;
        public LinearLayout linear_principal;
        public TextView colis;
    }
}