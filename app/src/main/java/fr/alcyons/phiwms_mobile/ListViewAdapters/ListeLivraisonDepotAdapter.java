package fr.alcyons.phiwms_mobile.ListViewAdapters;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;

public class ListeLivraisonDepotAdapter extends ArrayAdapter {

    public List<PH_Preparation> ph_preparation;
    Context context;

    public ListeLivraisonDepotAdapter(Context context, List<PH_Preparation> ph_preparation) {
        super(context, 0, ph_preparation);
        this.ph_preparation = ph_preparation;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_livraison_depot, parent, false);
        }

        PH_PreparationViewHolder viewHolder = (PH_PreparationViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new PH_PreparationViewHolder();
            viewHolder.nomDepot = (TextView) convertView.findViewById(R.id.nomDepot);
            viewHolder.uidPHPreparation = (TextView) convertView.findViewById(R.id.uidPHPreparation);
            viewHolder.isUrgent = (TextView) convertView.findViewById(R.id.isUrgent);
            viewHolder.liste = (TextView) convertView.findViewById(R.id.liste);
            viewHolder.zoneIsUrgent = (LinearLayout) convertView.findViewById(R.id.zoneIsUrgent);
            viewHolder.origine = (TextView) convertView.findViewById(R.id.origine);
        }

        final PH_Preparation phPreparations = (PH_Preparation) getItem(position);

        Depot depot = DepotOpenHelper.getDepotParReference(((MenuActivity) context).db, phPreparations.getDepotDestinataireReference());
        if(depot != null)
            viewHolder.nomDepot.setText(String.valueOf(depot.getNom()));
        else
            viewHolder.nomDepot.setText(String.valueOf(""));

        viewHolder.origine.setText(String.valueOf(phPreparations.getDepotOrigineReference()));
        viewHolder.uidPHPreparation.setText("N° " + String.valueOf(phPreparations.getUID()));
        viewHolder.liste.setText(String.valueOf(phPreparations.getListe()));

        String urgent = "";
        if (phPreparations.isURGENT()) {
            urgent += "URGENT";

        }
        viewHolder.isUrgent.setText(urgent);
        if (urgent.contentEquals("")) {
            viewHolder.zoneIsUrgent.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class PH_PreparationViewHolder {
        public TextView nomDepot;
        public TextView uidPHPreparation;
        public TextView isUrgent;
        public TextView liste;
        public TextView origine;
        public TextView dateLivraison;
        public LinearLayout zoneIsUrgent;
        public LinearLayout zoneDate;

        public void setDateLivraisonColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int demain = -1;

                if (delai > 0) {
                    zoneDate.setBackgroundColor(context.getResources().getColor(R.color.grey_color_fonce, null));
                } else if (delai == 0) {
                    zoneDate.setBackgroundColor(context.getResources().getColor(R.color.rouge2, null));
                } else if (delai == demain) {
                    zoneDate.setBackgroundColor(context.getResources().getColor(R.color.orange2, null));
                } else {
                    zoneDate.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
                }
            } else {
                zoneDate.setBackgroundColor(context.getResources().getColor(R.color.noir, null));
            }

        }
    }

}
