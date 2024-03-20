package fr.alcyons.phimr4.DepotSelecteur;

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

import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.R;

/**
 * Created by jessica on 24/11/2017.
 */

public class DepotExpandableListAdapter extends BaseExpandableListAdapter {

    Map<String, List<Depot>> tempDepotParentListItems;
    List<Depot> tempDepotListe;
    private Activity context;
    private Map<String, List<Depot>> depotParentListItems;
    private Map<String, List<Depot>> depotOriginalParentListItems;
    private List<String> depotTypeList;
    private Utilisateur utilisateurCourant;


    public DepotExpandableListAdapter(Activity context, List<String> depotTypeList, Map<String, List<Depot>> depotParentListItems, Utilisateur utilisateurCourant) {
        this.context = context;
        this.depotParentListItems = new LinkedHashMap<>();
        this.depotParentListItems.putAll(depotParentListItems);
        this.depotOriginalParentListItems = new LinkedHashMap<>();
        this.depotOriginalParentListItems.putAll(depotParentListItems);
        this.depotTypeList = depotTypeList;
        this.utilisateurCourant = utilisateurCourant;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return depotParentListItems.get(depotTypeList.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View ListView, ViewGroup parent) {
        final Depot depotCourant = (Depot) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (ListView == null) {
            ListView = inflater.inflate(R.layout.expandable_liste_item_depot, null);
        }

        TextView depotNom = (TextView) ListView.findViewById(R.id.depotNom);
        TextView depotIndicateur = (TextView) ListView.findViewById(R.id.depotIndicateur);

        String nomDepot = depotCourant.getNom();
        if(utilisateurCourant.getIdentifiant().contentEquals("ALCYONS") && depotCourant.getStructure().contentEquals("PAD"))
        {
            String[] tab_nom = depotCourant.getNom().split(" ");
            String nom = tab_nom[0];
            if(nom.length() > 2)
            {
                nom = nom.substring(0, 3)+"...";
            }
            else
            {
                nom = nom +"...";
            }
            String prenom = tab_nom[1];
            if(prenom.length() > 2)
            {
                prenom = prenom.substring(0, 3)+"...";
            }
            else
            {
                prenom = prenom+"...";
            }
           nomDepot = nom+" "+prenom;
        }
        depotNom.setText(nomDepot);
        depotIndicateur.setText("");

        return ListView;
    }

    public int getChildrenCount(int groupPosition) {
        return depotParentListItems.get(depotTypeList.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return depotTypeList.get(groupPosition);
    }

    public int getGroupCount() {
        return depotTypeList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View ListView, ViewGroup parent) {

        String depotTypeCourant = (String) getGroup(groupPosition);
        if (ListView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListView = infalInflater.inflate(R.layout.expandable_liste_groupe_depot, null);
        }

        TextView depotType = (TextView) ListView.findViewById(R.id.depotType);
        depotType.setText(depotTypeCourant);
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

        depotParentListItems.clear();

        if (query.isEmpty()) {
            depotParentListItems.putAll(depotOriginalParentListItems);
        } else {
            tempDepotParentListItems = new LinkedHashMap<String, List<Depot>>();

            for (Map.Entry<String, List<Depot>> entry : depotOriginalParentListItems.entrySet()) {

                tempDepotListe = new ArrayList<Depot>();
                String depotType = entry.getKey();
                List<Depot> depotList = entry.getValue();
                for (int j = 0; j < depotList.size(); j++) {
                    if (depotList.get(j).getNom().toLowerCase().contains(query)) {
                        tempDepotListe.add(depotList.get(j));
                    }
                }
                if (tempDepotListe.size() > 0) {
                    tempDepotParentListItems.put(depotType, tempDepotListe);
                } else {
                    tempDepotParentListItems.put(depotType, new ArrayList<Depot>());
                }
            }
            if (tempDepotParentListItems.size() > 0) {
                depotParentListItems.putAll(tempDepotParentListItems);
            }
        }
    }
}