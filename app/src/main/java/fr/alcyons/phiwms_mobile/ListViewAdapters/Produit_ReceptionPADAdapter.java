package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionPAD;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by olivier on 12/06/2019.
 */

public class Produit_ReceptionPADAdapter  extends BaseExpandableListAdapter {

    Map<String, List<ObjetReceptionScannee>> tempObjetParentListItems;
    List<ObjetReceptionScannee> tempObjetListe;
    private Activity context;
    private Map<ObjetReceptionPAD, List<ObjetReceptionScannee>> objetParentListItems;
    private Map<ObjetReceptionPAD, List<ObjetReceptionScannee>> objetOriginalParentListItems;
    private List<ObjetReceptionPAD> objetReceptionPAD;
    private Utilisateur utilisateurCourant;
    private SQLiteDatabase db;

    public Produit_ReceptionPADAdapter(Activity context, SQLiteDatabase db, List<ObjetReceptionPAD> objetReceptionPAD, Map<ObjetReceptionPAD, List<ObjetReceptionScannee>> objetParentListItems, Utilisateur utilisateurCourant) {
        this.context = context;
        this.objetParentListItems = new LinkedHashMap<>();
        this.objetParentListItems.putAll(objetParentListItems);
        this.objetOriginalParentListItems = new LinkedHashMap<>();
        this.objetOriginalParentListItems.putAll(objetParentListItems);
        this.objetReceptionPAD = objetReceptionPAD;
        this.utilisateurCourant = utilisateurCourant;
        this.db = db;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return objetOriginalParentListItems.get(objetReceptionPAD.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View ListView, ViewGroup parent) {
        final ObjetReceptionScannee objetReceptionScannee_courant = (ObjetReceptionScannee) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (ListView == null) {
            ListView = inflater.inflate(R.layout.row_scan_produit_reception_pad_scanner, null);
        }

        TextView qteProduitScannee = (TextView) ListView.findViewById(R.id.qteProduitScannee);
        TextView numLotProduitScannee = (TextView) ListView.findViewById(R.id.numLotProduitScannee);
        TextView datePeremptionProduitScannee = (TextView) ListView.findViewById(R.id.datePeremptionProduitScannee);

        String gs1Courant = objetReceptionScannee_courant.getGs1_scannee();
        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1Courant);
        String gtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
        String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
        String datePeremption = gs1Decoupe.get(OutilsDecodage.dateDePeremption);

        qteProduitScannee.setText(String.valueOf(objetReceptionScannee_courant.getQuantiteScannee()));
        numLotProduitScannee.setText(lot);
        datePeremptionProduitScannee.setText(datePeremption);

        return ListView;
    }

    public int getChildrenCount(int groupPosition) {
        if(objetParentListItems != null)
            return objetParentListItems.get(objetReceptionPAD.get(groupPosition)).size();
        else
            return 0;
    }

    public Object getGroup(int groupPosition) {
        return objetReceptionPAD.get(groupPosition);
    }

    public int getGroupCount() {
        return objetReceptionPAD.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View ListView, ViewGroup parent) {

        ObjetReceptionPAD objetCourant = (ObjetReceptionPAD) getGroup(groupPosition);

        if (ListView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListView = infalInflater.inflate(R.layout.expandable_liste_groupe_reception_pad_scanner, null);
        }

        TextView desingationProduit = (TextView) ListView.findViewById(R.id.designationProduit);
        TextView quantiteProduitReceptionPad = (TextView) ListView.findViewById(R.id.quantiteProduitReceptionPad);
        desingationProduit.setText(objetCourant.getDesignation_produit());
        quantiteProduitReceptionPad.setText(objetCourant.getQuantite_receptionner()+"/"+objetCourant.getQuantite_commander());
        return ListView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}