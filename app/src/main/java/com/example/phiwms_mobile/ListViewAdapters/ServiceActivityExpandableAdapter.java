package com.example.phiwms_mobile.ListViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import androidx.core.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.phiwms_mobile.Classes.PerimetreFonctionnel;
import com.example.phiwms_mobile.Classes.Service;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 11/04/2019.
 */

public class ServiceActivityExpandableAdapter  extends BaseExpandableListAdapter {

    private Activity context;
    private Map<PerimetreFonctionnel, List<Service>> navigationParentListItems;
    private Map<PerimetreFonctionnel, List<Service>> navigationOriginalParentListItems;
    private List<PerimetreFonctionnel> perimetreFonctionnelList;

    private List<String> serviceIndicateurNom;

    public ServiceActivityExpandableAdapter(Activity context, List<PerimetreFonctionnel> Items, Map<PerimetreFonctionnel, List<Service>> ParentListItems, List<String> serviceIndicateurNom) {
        this.context = context;
        this.navigationParentListItems = new LinkedHashMap<>();
        this.navigationParentListItems.putAll(ParentListItems);
        this.navigationOriginalParentListItems = new LinkedHashMap<>();
        this.navigationOriginalParentListItems.putAll(ParentListItems);
        this.perimetreFonctionnelList = Items;

        this.serviceIndicateurNom = serviceIndicateurNom;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return navigationParentListItems.get(perimetreFonctionnelList.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View ListView, ViewGroup parent) {
        final Service service = (Service) getChild(groupPosition, childPosition);

        if (ListView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            ListView = inflater.inflate(R.layout.expandable_list_item_service_activity, null);
        }

        TextView expandedListItemTextView = (TextView) ListView.findViewById(R.id.expandedListItem);
        ImageView statutServiceImageView = (ImageView) ListView.findViewById(R.id.statutService);
        expandedListItemTextView.setText(service.getNom());


        int color;
        switch (service.getStatut()) {
            case "PRODUCTION":
                color = context.getResources().getColor(R.color.vert, null);
                expandedListItemTextView.setTextColor(context.getResources().getColor(R.color.noir, null));
                break;
            case "PROTOTYPE":
                color = context.getResources().getColor(R.color.orange2, null);
                expandedListItemTextView.setTextColor(color);
                break;
            case "DESIGN":
                color = context.getResources().getColor(R.color.rouge2, null);
                expandedListItemTextView.setTextColor(color);
                break;
            default:
                color = context.getResources().getColor(R.color.bleu_clair_alcyons, null);
                expandedListItemTextView.setTextColor(color);
                break;
        }
        ViewCompat.setBackgroundTintList(statutServiceImageView, ColorStateList.valueOf(color));


        return ListView;
    }

    public int getChildrenCount(int groupPosition) {
        return navigationParentListItems.get(perimetreFonctionnelList.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return perimetreFonctionnelList.get(groupPosition);
    }

    public int getGroupCount() {
        return perimetreFonctionnelList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View ListView, ViewGroup parent) {
        if (ListView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListView = infalInflater.inflate(R.layout.expandable_liste_groupe_navigation, null);
        }

        PerimetreFonctionnel perimetreFonctionnel = (PerimetreFonctionnel) getGroup(groupPosition);

        ImageView icon = (ImageView) ListView.findViewById(R.id.iconeService);
        TextView item = (TextView) ListView.findViewById(R.id.listTitle);
        item.setText(perimetreFonctionnel.getNom());


        String nomPerimetre = perimetreFonctionnel.getNom();

        switch (nomPerimetre) {
            case "Commun":
                icon.setBackgroundResource(R.drawable.ic_build_black_24dp);
                if (isExpanded) {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.noir, null)));
                } else {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                }
                break;

            case "Pharmacien":
                icon.setBackgroundResource(R.drawable.ic_local_pharmacy_black);
                if (isExpanded) {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.noir, null)));
                } else {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                }
                break;

            case "Patient":
                icon.setBackgroundResource(R.drawable.ic_personne_black);
                if (isExpanded) {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.noir, null)));
                } else {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                }
                break;

            case "Magasinier":
                icon.setBackgroundResource(R.drawable.ic_colis);
                if (isExpanded) {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.noir, null)));
                } else {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                }
                break;

            case "Chauffeurs":
                icon.setBackgroundResource(R.drawable.ic_local_shipping);
                if (isExpanded) {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.noir, null)));
                } else {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                }
                break;

            case "Infirmiers":
                icon.setBackgroundResource(R.drawable.ic_assignment);
                if (isExpanded) {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.noir, null)));
                } else {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                }
                break;
            case "Préparateur":
                icon.setBackgroundResource(R.drawable.ic_inbox);
                if (isExpanded) {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.noir, null)));
                } else {
                    ViewCompat.setBackgroundTintList(icon, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                }
                break;
            default:
                break;
        }

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

        navigationParentListItems.clear();

        if (query.isEmpty()) {
            navigationParentListItems.putAll(navigationOriginalParentListItems);
        } else {
            Map<PerimetreFonctionnel, List<Service>> navigationParentListItemsTemp = new LinkedHashMap<>();

            for (Map.Entry<PerimetreFonctionnel, List<Service>> entry : navigationOriginalParentListItems.entrySet()) {

                List<Service> tempServiceList = new ArrayList<>();
                PerimetreFonctionnel perimetreFonctionnel = entry.getKey();
                List<Service> serviceList = entry.getValue();
                for (int j = 0; j < serviceList.size(); j++) {
                    if (serviceList.get(j).getNom().toLowerCase().contains(query)) {
                        tempServiceList.add(serviceList.get(j));
                    }
                }
                if (tempServiceList.size() > 0) {
                    navigationParentListItemsTemp.put(perimetreFonctionnel, tempServiceList);
                } else {
                    navigationParentListItemsTemp.put(perimetreFonctionnel, new ArrayList<Service>());
                }
            }
            if (navigationParentListItemsTemp.size() > 0) {
                navigationParentListItems.putAll(navigationParentListItemsTemp);
            }
        }
    }
}