package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;
public class PH_Preparation_LivraisonAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    public ArrayList<PH_Preparation> phPreparations = new ArrayList<PH_Preparation>();
    public TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    Context context;
    SQLiteDatabase db;
    private LayoutInflater mInflater;
    Utilisateur utilisateur;

    public PH_Preparation_LivraisonAdapter(Context context, SQLiteDatabase database, Utilisateur utilisateur) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = database;
        this.context = context;
        this.utilisateur = utilisateur;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void addItem(final PH_Preparation item) {
        phPreparations.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final PH_Preparation item) {
        phPreparations.add(item);
        sectionHeader.add(phPreparations.size() - 1);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getCount() {
        return phPreparations.size();
    }

    @Override
    public PH_Preparation getItem(int position) {
        return phPreparations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PH_PreparationViewHolder viewHolder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            viewHolder = new PH_PreparationViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.row_ph_preparation_livraison, parent, false);
                    viewHolder.nomDepot = (TextView) convertView.findViewById(R.id.nomDepot);
                    viewHolder.uidPHPreparation = (TextView) convertView.findViewById(R.id.uidPHPreparation);
                    viewHolder.isUrgent = (TextView) convertView.findViewById(R.id.isUrgent);
                    viewHolder.liste = (TextView) convertView.findViewById(R.id.liste);
                    viewHolder.zoneIsUrgent = (LinearLayout) convertView.findViewById(R.id.zoneIsUrgent);
                    viewHolder.origine = (TextView) convertView.findViewById(R.id.origine);
                    break;
                case TYPE_HEADER:
                    convertView = mInflater.inflate(R.layout.row_header_ph_preparation_livraison, parent, false);
                    viewHolder.zoneDate = (LinearLayout) convertView.findViewById(R.id.zoneDate);
                    viewHolder.dateLivraison = (TextView) convertView.findViewById(R.id.textSeparator);
                    viewHolder.zoneDate.setClickable(false);
                    viewHolder.dateLivraison.setEnabled(false);
                    viewHolder.dateLivraison.setClickable(false);
                    viewHolder.zoneDate.setEnabled(false);
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PH_PreparationViewHolder) convertView.getTag();
        }

        if (rowType == TYPE_ITEM) {
            Depot depot = DepotOpenHelper.getDepotParReference(db, phPreparations.get(position).getDepotDestinataireReference());
            if(depot != null)
                viewHolder.nomDepot.setText(String.valueOf(depot.getNom()));
            else
                viewHolder.nomDepot.setText(String.valueOf(""));

            viewHolder.origine.setText(String.valueOf(phPreparations.get(position).getDepotOrigineReference()));
            viewHolder.uidPHPreparation.setText("N° " + String.valueOf(phPreparations.get(position).getUID()));
            viewHolder.liste.setText(String.valueOf(phPreparations.get(position).getListe()));

            String urgent = "";
            if (phPreparations.get(position).isURGENT()) {
                urgent += "URGENT";

            }
            viewHolder.isUrgent.setText(urgent);
            if (urgent.contentEquals("")) {
                viewHolder.zoneIsUrgent.setVisibility(View.GONE);
            }

        } else if (rowType == TYPE_HEADER) {
            // Gestion des dates
            Date dateLivraison = null;
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                dateLivraison = dateDecodeur.parse(phPreparations.get(position).getLivraisonDate().substring(0, 10));

                if(phPreparations.get(position).getLivraisonDate().contentEquals("0000-00-00"))
                {
                    dateLivraison = dateDecodeur.parse(phPreparations.get(position).getLivraisonPrevueDate().substring(0, 10));
                }

                viewHolder.setDateLivraisonColor(dateLivraison);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (dateLivraison != null) {
                viewHolder.dateLivraison.setText(dateFormat.format(dateLivraison));
            }
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
