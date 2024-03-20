package fr.alcyons.phimr4.ReceptionScanne;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import fr.alcyons.phimr4.R;

/**
 * Created by olivier on 06/05/2019.
 */

public class ZonetEtEmplacementReceptionScanneeExpandableAdapter extends BaseExpandableListAdapter {

    Map<Depot_Zone, List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement>> ParentListItemsTemp;
    List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement> tempListeEmplacement;
    int quantiteLivree;
    int quantiteReliquat;
    private Activity context;
    private Map<Depot_Zone, List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement>> ParentListItems;
    private Map<Depot_Zone, List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement>> OriginalParentListItems;
    private List<Depot_Zone> Items;

    public ZonetEtEmplacementReceptionScanneeExpandableAdapter(Activity context, List<Depot_Zone> Items, Map<Depot_Zone, List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement>> ParentListItems, int quantiteLivree, int quantiteReliquat) {
        this.context = context;
        this.Items = Items;
        this.ParentListItems = new LinkedHashMap<>();
        this.ParentListItems.putAll(ParentListItems);
        this.OriginalParentListItems = new LinkedHashMap<>();
        this.OriginalParentListItems.putAll(ParentListItems);

        this.quantiteLivree = quantiteLivree;
        this.quantiteReliquat = quantiteReliquat;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return ParentListItems.get(Items.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View ListView, ViewGroup parent) {

        final PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement = (PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement) getChild(groupPosition, childPosition);

        if (ListView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            ListView = inflater.inflate(R.layout.expandable_liste_item_zonetemplacement, null);
        }

        TextView item = (TextView) ListView.findViewById(R.id.emplacementNom);
        TextView qte_reliquat = (TextView) ListView.findViewById(R.id.qte_reliquat);

        item.setText(zoneEtEmplacement.getEmplacementName());
        if (zoneEtEmplacement.getQuantite() == 0) {
            qte_reliquat.setText("");
        } else {
            qte_reliquat.setText(String.valueOf(zoneEtEmplacement.getQuantite()).trim());
        }

        return ListView;
    }

    public int getChildrenCount(int groupPosition) {
        return ParentListItems.get(Items.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return Items.get(groupPosition);
    }

    public int getGroupCount() {
        return Items.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View ListView, ViewGroup parent) {

        Depot_Zone CoursesFull = (Depot_Zone) getGroup(groupPosition);
        if (ListView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListView = infalInflater.inflate(R.layout.expandable_liste_groupe_zoneetemplacement, null);
        }

        TextView item = (TextView) ListView.findViewById(R.id.nomZone);
        item.setText(CoursesFull.getZoneName());
        return ListView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filter(String query) {

        query = query.toLowerCase();

        ParentListItems.clear();

        if (query.isEmpty()) {
            ParentListItems.putAll(OriginalParentListItems);
        } else {
            ParentListItemsTemp = new LinkedHashMap<>();

            for (Map.Entry<Depot_Zone, List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement>> entry : OriginalParentListItems.entrySet()) {

                tempListeEmplacement = new ArrayList<>();
                Depot_Zone depotZone = entry.getKey();
                List<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement> emplacementList = entry.getValue();
                for (int j = 0; j < emplacementList.size(); j++) {
                    if (emplacementList.get(j).getEmplacementName().toLowerCase().contains(query)) {
                        tempListeEmplacement.add(emplacementList.get(j));
                    }
                }
                if (tempListeEmplacement.size() > 0) {
                    ParentListItemsTemp.put(depotZone, tempListeEmplacement);
                } else {
                    ParentListItemsTemp.put(depotZone, new ArrayList<PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement>());
                }
            }
            if (ParentListItemsTemp.size() > 0) {
                ParentListItems.putAll(ParentListItemsTemp);
            }
        }
    }

}