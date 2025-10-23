package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Detail_DotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Detail_Dot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock;
import fr.alcyons.phiwms_mobile.R;

public class DetailPleinVideAdapter extends ArrayAdapter {
    public List<PH_Preparation_Ligne> ph_preparation_ligneList;
    public List<PH_Preparation_Ligne> ph_preparation_ligneListOriginal;
    public List<New_DetailPleinVideViewHolder> demandePleinVideViewHolderList;
    Context context;
    SQLiteDatabase db;
    int dotationId;


    public DetailPleinVideAdapter(Context context, SQLiteDatabase db, List<PH_Preparation_Ligne> ph_preparation_ligneList, int dotationId) {
        super(context, 0, ph_preparation_ligneList);
        this.ph_preparation_ligneList = ph_preparation_ligneList;
        this.context = context;
        this.db = db;
        this.dotationId = dotationId;

        ph_preparation_ligneListOriginal = new ArrayList<>();
        ph_preparation_ligneListOriginal.addAll(ph_preparation_ligneList);
    }
    @NonNull
    @SuppressLint({"UseCompatLoadingForDrawables", "ViewHolder", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_demande_pleinvide, parent, false);

        New_DetailPleinVideViewHolder viewHolder = (New_DetailPleinVideViewHolder) convertView.getTag();

        if(viewHolder == null)
        {
            viewHolder.designationProduit = convertView.findViewById(R.id.designationProduit);
            viewHolder.adressagePleinVide = convertView.findViewById(R.id.adressagePleinVide);
            viewHolder.quantitePleinVide = convertView.findViewById(R.id.quantitePleinVide);
            viewHolder.layoutPrincipal = convertView.findViewById(R.id.layoutPrincipal);
            viewHolder.cartouchePleinVide = convertView.findViewById(R.id.cartouchePleinVide);
            viewHolder.conditionnement = convertView.findViewById(R.id.conditionnement);
            convertView.setTag(viewHolder);
        }


        Depot depot = DepotOpenHelper.getDepotPUI(db);
        PH_Preparation_Ligne ph_preparation_ligne = (PH_Preparation_Ligne) getItem(position);
        assert ph_preparation_ligne != null;
        Detail_Dot detail_dot = Detail_DotOpenHelper.getDetailDotByProduitAndDotation(db, ph_preparation_ligne.getProduitID(), dotationId);
        Produit produit = ProduitOpenHelper.getProduitByID(db, detail_dot.getCode_produit());
        //TODO : charger le stock avant
        Stock stock = StockOpenHelper.getStockByProduitEtDepot(db, produit, depot);

        viewHolder.designationProduit.setText(produit.getDesignation_interne());
        viewHolder.adressagePleinVide.setText(detail_dot.getPleinVide_Adressage());
        viewHolder.quantitePleinVide.setText(String.valueOf(ph_preparation_ligne.getQte_Demander()));
        viewHolder.conditionnement.setText("x("+detail_dot.getQte()+")");

        /*int stockQuantiteActuelle = 0;
        if (stock != null) {
            stockQuantiteActuelle = (int) stock.getQuantite_Actuelle();
        }

        if (stockQuantiteActuelle < detail_dot.getQte()) {
            viewHolder.quantitePleinVide.setTextColor(Color.RED);
        }*/

        if(ph_preparation_ligne.getQte_APreparer() > 0)
        {
            viewHolder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_plein_vert_radius, null));
        }

        return convertView;
    }

    public static class New_DetailPleinVideViewHolder{
        TextView designationProduit;
        public LinearLayout layoutPrincipal;
        public RelativeLayout cartouchePleinVide;
        TextView adressagePleinVide;
        TextView quantitePleinVide;
        TextView conditionnement;
    }
}
