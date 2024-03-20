package fr.alcyons.phimr4.ListViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.ObjetReceptionScannee;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ReceptionScanne.ScanProduitActivity;

/**
 * Created by olivier on 17/05/2019.
 */

public class ScanProduitExpandableAdapter extends BaseExpandableListAdapter {

    Map<String, List<ObjetReceptionScannee>> tempObjetParentListItems;
    List<ObjetReceptionScannee> tempObjetListe;
    private Activity context;
    private Map<String, List<ObjetReceptionScannee>> objetParentListItems;
    private Map<String, List<ObjetReceptionScannee>> objetOriginalParentListItems;
    private List<String> emplacementList;
    private Utilisateur utilisateurCourant;
    private SQLiteDatabase db;

    public ScanProduitExpandableAdapter(Activity context, SQLiteDatabase db, List<String> emplacementList, Map<String, List<ObjetReceptionScannee>> objetParentListItems, Utilisateur utilisateurCourant) {
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
            ListView = inflater.inflate(R.layout.row_scan_produit_reception_scannee, null);
        }

        TextView qteProduitScannee = (TextView) ListView.findViewById(R.id.qteProduitScannee);
        TextView DesignationProduitScannee = (TextView) ListView.findViewById(R.id.DesignationProduitScannee);
        TextView numLotProduitScannee = (TextView) ListView.findViewById(R.id.numLotProduitScannee);
        TextView datePeremptionProduitScannee = (TextView) ListView.findViewById(R.id.datePeremptionProduitScannee);
        LinearLayout supprimerScan = (LinearLayout) ListView.findViewById(R.id.supprimerScan);

        String gs1Courant = objetReceptionScannee_courant.getGs1_scannee();
        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1Courant);
        String gtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
        String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
        String datePeremption = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
        Produit produitCourant = null;

        if(gs1Decoupe.size() != 1)
        {
            produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
        }
        else
        {
            List<Produit> list = ProduitOpenHelper.getProduitsParCodeInconnue(db, gs1Courant);
            if(list.size() == 1)
                produitCourant = list.get(0);
        }

        qteProduitScannee.setText(String.valueOf(objetReceptionScannee_courant.getQuantiteScannee()));
        DesignationProduitScannee.setText(produitCourant.getDesignation_interne());
        numLotProduitScannee.setText(lot);

        //Gestion du format de la date de péremption
        if(datePeremption != null && datePeremption.contentEquals(""))
        {
            String[] date_tab = datePeremption.split("-");
            String new_date = date_tab[date_tab.length-1]+"/"+date_tab[1]+"/"+date_tab[0];
            datePeremptionProduitScannee.setText(new_date);
        }

        supprimerScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ScanProduitActivity) context).supprimerScan(groupPosition, childPosition);
            }
        });

        return ListView;
    }

    public int getChildrenCount(int groupPosition) {
        if(objetParentListItems != null)
            return objetParentListItems.get(emplacementList.get(groupPosition)).size();
        else
            return 0;
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