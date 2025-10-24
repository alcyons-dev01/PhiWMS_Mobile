package fr.alcyons.phiwms_mobile.DepotSelecteur;

import android.annotation.SuppressLint;
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
import java.util.Objects;

import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;
public class DepotExpandableListAdapter extends BaseExpandableListAdapter {

    Map<String, List<Depot>> tempDepotParentListItems;
    List<Depot> tempDepotListe;
    private final Activity context;
    private final Map<String, List<Depot>> depotParentListItems;
    private final Map<String, List<Depot>> depotOriginalParentListItems;
    private final List<String> depotTypeList;
    private final Utilisateur utilisateurCourant;


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
        if(depotTypeList.get(groupPosition).contentEquals("PUI"))
        {
            return Objects.requireNonNull(depotParentListItems.get("PUI")).get(childPosition);
        }
        else
        {
            return Objects.requireNonNull(depotParentListItems.get("PUF")).get(childPosition);
        }
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @SuppressLint("InflateParams")
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View ListView, ViewGroup parent) {
        final Depot depotCourant = (Depot) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (ListView == null) {
            ListView = inflater.inflate(R.layout.expandable_liste_item_depot, null);
        }

        TextView depotNom = (TextView) ListView.findViewById(R.id.depotNom);
        TextView depotIndicateur = (TextView) ListView.findViewById(R.id.depotIndicateur);

        String nomDepot = getNomDepot(depotCourant);
        depotNom.setText(nomDepot);
        depotIndicateur.setText("");

        return ListView;
    }

    private String getNomDepot(Depot depotCourant) {
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
        return nomDepot;
    }

    public int getChildrenCount(int groupPosition) {
        if(depotTypeList.get(groupPosition).contentEquals("PUI"))
        {
            return Objects.requireNonNull(depotParentListItems.get("PUI")).size();
        }
        else
        {
            return Objects.requireNonNull(depotParentListItems.get("PUF")).size();
        }
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

    @SuppressLint("InflateParams")
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
            tempDepotParentListItems = new LinkedHashMap<>();

            for (Map.Entry<String, List<Depot>> entry : depotOriginalParentListItems.entrySet()) {

                tempDepotListe = new ArrayList<>();
                String depotType = entry.getKey();
                List<Depot> depotList = entry.getValue();
                for (int j = 0; j < depotList.size(); j++) {
                    if (depotList.get(j).getNom().toLowerCase().contains(query)) {
                        tempDepotListe.add(depotList.get(j));
                    }
                }
                if (!tempDepotListe.isEmpty()) {
                    tempDepotParentListItems.put(depotType, tempDepotListe);
                } else {
                    tempDepotParentListItems.put(depotType, new ArrayList<>());
                }
            }
            if (!tempDepotParentListItems.isEmpty()) {
                depotParentListItems.putAll(tempDepotParentListItems);
            }
        }
    }
}