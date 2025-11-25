package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Inventaire;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class InventaireAdapter extends ArrayAdapter implements Filterable {

    public List<String[]> inventaireList;
    public List<Inventaire> inventaireListBase;
    Context context;
    SQLiteDatabase db;
    Utilisateur utilisateur;

    public InventaireAdapter(Context context, SQLiteDatabase database, List<String[]> inventaireList, Utilisateur utilisateur) {
        super(context, 0, inventaireList);
        this.context = context;
        this.db = database;
        this.inventaireList = inventaireList;
        this.utilisateur = utilisateur;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_inventaire, parent, false);
        }

        InventaireViewHolder viewHolder = (InventaireViewHolder) convertView.getTag();
        if (viewHolder == null) {
            // Récupération des objets graphiques
            viewHolder = new InventaireViewHolder();
            viewHolder.statutInventaire_TV = (TextView) convertView.findViewById(R.id.statutInventaire_TV);
            viewHolder.dateCloture_TV = (TextView) convertView.findViewById(R.id.dateCloture_TV);
            viewHolder.codeInventaire_TV = (TextView) convertView.findViewById(R.id.codeInventaire_TV);
            viewHolder.zoneInventaire_TV = (TextView) convertView.findViewById(R.id.zoneInventaire_TV);
            viewHolder.depotInventaire_TV = (TextView) convertView.findViewById(R.id.depotInventaire_TV);
            viewHolder.inventaireLigneCompte_TV = (TextView) convertView.findViewById(R.id.inventaireLigneCompte_TV);
            viewHolder.inventaireLigneTotal_TV = (TextView) convertView.findViewById(R.id.inventaireLigneTotal_TV);
            viewHolder.layoutPrincipal_LL = (LinearLayout) convertView.findViewById(R.id.layoutPrincipal_LL);
            convertView.setTag(viewHolder);
        }

        String[] inventaire = (String[]) getItem(position);

        if (inventaire != null) {
            viewHolder.statutInventaire_TV.setText(inventaire[6]);

            if(inventaire[6].contentEquals("Saisie complète"))
            {
                viewHolder.layoutPrincipal_LL.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_vert));
                viewHolder.statutInventaire_TV.setTextColor(context.getResources().getColor(R.color.vert));
            }

            viewHolder.codeInventaire_TV.setText("#"+inventaire[2]);

            viewHolder.zoneInventaire_TV.setText(inventaire[0]);
            viewHolder.depotInventaire_TV.setText(inventaire[3]);

            viewHolder.inventaireLigneCompte_TV.setText(String.valueOf(inventaire[5]));
            viewHolder.inventaireLigneTotal_TV.setText(String.valueOf(inventaire[1]));

            // Gestion des dates
            String dateCloture = inventaire[4];
            String[] tabDateCloture = dateCloture.split("-");

            if(tabDateCloture.length == 3)
                dateCloture = tabDateCloture[2] + "/" + tabDateCloture[1] + "/" + tabDateCloture[0];
            viewHolder.dateCloture_TV.setText("Avant le " + dateCloture);

        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<Inventaire> tempInventaireList = new ArrayList<>();
                inventaireListBase = InventaireOpenHelper.getAllInventaire(db);

                if (constraint != null && inventaireListBase != null) {
                    for (Inventaire inventaire : inventaireListBase) {
                        if (constraint.length() == 0) {
                            tempInventaireList.add(inventaire);
                        } else {
                            Depot depot = DepotOpenHelper.getDepotParReference(db, inventaire.getDepotReference());
                            if (depot.getNom().toLowerCase().contains(constraint)) {
                                tempInventaireList.add(inventaire);

                            } else if (String.valueOf(inventaire.getInventaire_ID()).contains(constraint)) {
                                tempInventaireList.add(inventaire);
                            }
                        }
                    }
                    filterResults.values = tempInventaireList;
                    filterResults.count = tempInventaireList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                inventaireList.clear();
                inventaireList.addAll((List<String[]>) results.values);
                notifyDataSetChanged();
            }

        };
    }

    @Override
    public void clear() {
        inventaireList.clear();
    }

    private class InventaireViewHolder {
        public TextView statutInventaire_TV;
        public TextView dateCloture_TV;
        public TextView codeInventaire_TV;
        public TextView zoneInventaire_TV;
        public TextView depotInventaire_TV;
        public TextView inventaireLigneCompte_TV;
        public TextView inventaireLigneTotal_TV;
        public LinearLayout layoutPrincipal_LL;
    }
}

