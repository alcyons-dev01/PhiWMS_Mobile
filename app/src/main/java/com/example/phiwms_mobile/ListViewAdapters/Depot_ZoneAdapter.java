package com.example.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.example.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import com.example.phiwms_mobile.Classes.Depot_Zone;
import com.example.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 26/04/2017.
 */

public class Depot_ZoneAdapter extends ArrayAdapter implements Filterable {

    public List<Depot_Zone> depotZoneList;
    public ListView depotZoneListView;
    public List<Depot_Zone> depotZoneDeBaseList;
    public HashMap<Integer, Boolean> depotZoneSelectedHasMap = new HashMap<>();
    Context context;
    SQLiteDatabase db;
    Depot_ZoneFilter filter;

    public Depot_ZoneAdapter(Context context, SQLiteDatabase db, List<Depot_Zone> zones, ListView listView) {
        super(context, 0, zones);
        this.context = context;
        this.db = db;
        this.depotZoneList = zones;
        this.depotZoneListView = listView;

        // Permet de garder une version de la liste complète des éléments
        depotZoneDeBaseList = new ArrayList<>();

        // La boucle foreach permet d'insérer les valeurs d'un objet dans l'autre, "zonesDeBase = zones" aurait "fusionné" les deux objets et modifier l'un des deux aurait modifié l'autre
        for (Depot_Zone zoneCourante : zones
                ) {
            depotZoneDeBaseList.add(zoneCourante);
        }
        this.filter = null;
    }


    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new Depot_ZoneFilter();

        return filter;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_depot_zone, parent, false);
        }

        Depot_ZoneViewHolder viewHolder = (Depot_ZoneViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new Depot_ZoneViewHolder();
            viewHolder.nomZone = (TextView) convertView.findViewById(R.id.nomZone);
            viewHolder.nbEmplacements = (TextView) convertView.findViewById(R.id.nbEmplacements);
            convertView.setTag(viewHolder);
        }

        Depot_Zone zoneCourante = (Depot_Zone) getItem(position);

        viewHolder.nomZone.setText(zoneCourante.getZoneName());
        viewHolder.nbEmplacements.setText(String.valueOf(zoneCourante.getEmplacements().size()));

        return convertView;
    }

    public void setNewSelection(int position, boolean value) {
        depotZoneSelectedHasMap.put(position, value);
    }

    public boolean isPositionChecked(int position) {
        Boolean result = depotZoneSelectedHasMap.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return depotZoneSelectedHasMap.keySet();
    }

    public void removeSelection(int position) {
        depotZoneSelectedHasMap.remove(position);
    }

    public Integer remove() {
        int nbDepotZone = 0;

        for (HashMap.Entry<Integer, Boolean> entry : depotZoneSelectedHasMap.entrySet()) {
            int position = entry.getKey();
            boolean checked = entry.getValue();
            if (checked) {
                Depot_Zone depotZone = depotZoneDeBaseList.get(position);
                depotZoneList.remove(depotZone);
                if (ZoneOpenHelper.supprimerUneZoneEnBDD(db, depotZone) != -1) {
                    nbDepotZone++;
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ZoneOpenHelper.Constantes.TABLE_DEPOT_ZONE, depotZone.getPhiMR4UUID(), depotZone.getZoneID(), DBOpenHelper.ActionsEAS.SUPPR);
                }
            }
        }
        clearSelection();
        return nbDepotZone;
    }

    public void clearSelection() {
        depotZoneSelectedHasMap = new HashMap<>();
    }

    private class Depot_ZoneFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            depotZoneList.clear();
            for (Depot_Zone zoneCourante : depotZoneDeBaseList
                    ) {
                depotZoneList.add(zoneCourante);
            }
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {
                List<Depot_Zone> founded = new ArrayList<Depot_Zone>();
                for (Depot_Zone item : depotZoneList) {
                    // Vérifie le début du premier mot

                    // Valeur à modifier si on souhaite filtrer sur autre chose que zone.toString()
                    String valueText = item.toString().toLowerCase();

                    if (valueText.startsWith(String.valueOf(constraint))) {
                        founded.add(item);
                    } else {
                        /* Vérifie le début de chaque mot */
                        final String[] words = valueText.split(" ");
                        for (String word : words) {
                            if (word.startsWith(String.valueOf(constraint))) {
                                founded.add(item);
                                break;
                            }
                        }
                    }
                }
                result.values = founded;
                result.count = founded.size();
            } else {
                // Si le texte à filtrer (constraint) est vide, on remet les zones de base
                result.values = depotZoneDeBaseList;
                result.count = depotZoneDeBaseList.size();
            }
            return result;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            depotZoneList.clear();
            for (Depot_Zone item : (List<Depot_Zone>) results.values) {
                add(item);
            }
            notifyDataSetChanged();
        }
    }

    private class Depot_ZoneViewHolder {
        public TextView nomZone;
        public TextView nbEmplacements;
    }
}
