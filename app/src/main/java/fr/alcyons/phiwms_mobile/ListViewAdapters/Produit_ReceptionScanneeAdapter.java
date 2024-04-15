package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by olivier on 20/05/2019.
 */

public class Produit_ReceptionScanneeAdapter extends BaseExpandableListAdapter {

    Map<String, List<ObjetReceptionScannee>> tempObjetParentListItems;
    List<ObjetReceptionScannee> tempObjetListe;
    private Activity context;
    private Map<String, List<ObjetReceptionScannee>> objetParentListItems;
    private Map<String, List<ObjetReceptionScannee>> objetOriginalParentListItems;
    private List<String> emplacementList;
    private Utilisateur utilisateurCourant;
    private SQLiteDatabase db;

    public Produit_ReceptionScanneeAdapter(Activity context, SQLiteDatabase db, List<String> emplacementList, Map<String, List<ObjetReceptionScannee>> objetParentListItems, Utilisateur utilisateurCourant) {
        this.context = context;
        this.objetParentListItems = new LinkedHashMap<>();
        this.objetParentListItems.putAll(objetParentListItems);
        this.objetOriginalParentListItems = new LinkedHashMap<>();
        this.objetOriginalParentListItems.putAll(objetParentListItems);
        this.emplacementList = emplacementList;
        this.utilisateurCourant = utilisateurCourant;
        this.db = db;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return objetOriginalParentListItems.get(emplacementList.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View ListView, ViewGroup parent) {
        final ObjetReceptionScannee objetReceptionScannee_courant = (ObjetReceptionScannee) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (ListView == null) {
            ListView = inflater.inflate(R.layout.row_serie_lot_reception_scannee, null);
        }

        TextView designation = (TextView) ListView.findViewById(R.id.designation);
        TextView quantite = (TextView) ListView.findViewById(R.id.quantite);
        TextView numeroLot = (TextView) ListView.findViewById(R.id.numeroLot);
        TextView datePeremption = (TextView) ListView.findViewById(R.id.datePeremption);
        TextView numSerie = (TextView) ListView.findViewById(R.id.numSerie);

        String gs1Courant = objetReceptionScannee_courant.getGs1_scannee();
        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1Courant);
        String gtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
        String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
        String serie= gs1Decoupe.get(OutilsDecodage.numeroSerie);
        String date = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
        if(date != null)
        {
            String[] date_tab = date.split("-");
            date = date_tab[2]+"/"+date_tab[1]+"/"+date_tab[0];
        }


    if(gtin != null)
    {
        Produit produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);

        if(produitCourant != null)
        {
            designation.setText(produitCourant.getDesignation_interne());
            quantite.setText(String.valueOf(objetReceptionScannee_courant.getQuantiteScannee()));
            numeroLot.setText(lot);
            numSerie.setText(serie);
            datePeremption.setText(date);
        }
    }
    else
    {
        Produit produitCourant = ProduitOpenHelper.getUnProduitByCodeInconnu(db, gs1Courant);

        if(produitCourant != null) {
            designation.setText(produitCourant.getDesignation_interne());
            quantite.setText(String.valueOf(objetReceptionScannee_courant.getQuantiteScannee()));
            numeroLot.setText(lot);
            numSerie.setText(serie);
            datePeremption.setText(date);
        }

    }


        return ListView;
    }

    public int getChildrenCount(int groupPosition) {
        if(objetParentListItems != null && objetParentListItems.size() != 0)
        {
            if(emplacementList.get(groupPosition) == null)
            {
                return 0;
            }
            else
            {
                if(objetOriginalParentListItems.get(emplacementList.get(groupPosition)) == null)
                {
                    return 0;
                }
                else
                {
                    return objetParentListItems.get(emplacementList.get(groupPosition)).size();
                }
            }
        }
        else
        {
            return 0;
        }
    }

    public Object getGroup(int groupPosition) {
        return emplacementList.get(groupPosition);
    }

    public int getGroupCount() {
        return emplacementList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View ListView, ViewGroup parent) {

        String depotTypeCourant = (String) getGroup(groupPosition);
        if (ListView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListView = infalInflater.inflate(R.layout.expandable_liste_groupe_receptionscanne, null);
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

        objetParentListItems.clear();

        if (query.isEmpty()) {
            objetParentListItems.putAll(objetOriginalParentListItems);
        } else {
            tempObjetParentListItems = new LinkedHashMap<String, List<ObjetReceptionScannee>>();

            for (Map.Entry<String, List<ObjetReceptionScannee>> entry : objetOriginalParentListItems.entrySet()) {

                tempObjetListe = new ArrayList<ObjetReceptionScannee>();
                String depotType = entry.getKey();
                List<ObjetReceptionScannee> objettList = entry.getValue();
                for (int j = 0; j < objettList.size(); j++) {
                    if (objettList.get(j).getGs1_scannee().toLowerCase().contains(query)) {
                        tempObjetListe.add(objettList.get(j));
                    }
                }
                if (tempObjetListe.size() > 0) {
                    tempObjetParentListItems.put(depotType, tempObjetListe);
                } else {
                    tempObjetParentListItems.put(depotType, new ArrayList<ObjetReceptionScannee>());
                }
            }
            if (tempObjetParentListItems.size() > 0) {
                objetParentListItems.putAll(tempObjetParentListItems);
            }
        }
    }
}