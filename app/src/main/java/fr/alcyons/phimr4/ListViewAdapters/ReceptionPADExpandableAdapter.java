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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.ObjetReceptionPAD;
import fr.alcyons.phimr4.Classes.ObjetReceptionScannee;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ReceptionPAD.DetailReceptionPadActivity;

/**
 * Created by olivier on 12/06/2019.
 */

public class ReceptionPADExpandableAdapter extends BaseExpandableListAdapter {

    Map<String, List<ObjetReceptionScannee>> tempObjetParentListItems;
    List<ObjetReceptionScannee> tempObjetListe;
    private Activity context;
    private Map<ObjetReceptionPAD, List<ObjetReceptionScannee>> objetParentListItems;
    private Map<ObjetReceptionPAD, List<ObjetReceptionScannee>> objetOriginalParentListItems;
    private List<ObjetReceptionPAD> objetReceptionPAD;
    private Utilisateur utilisateurCourant;
    private SQLiteDatabase db;

    public ReceptionPADExpandableAdapter(Activity context, SQLiteDatabase db, List<ObjetReceptionPAD> objetReceptionPAD, Map<ObjetReceptionPAD, List<ObjetReceptionScannee>> objetParentListItems, Utilisateur utilisateurCourant) {
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
            ListView = inflater.inflate(R.layout.row_scan_produit_reception_pad, null);
        }

        TextView qteProduitScannee = (TextView) ListView.findViewById(R.id.qteProduitScannee);
        TextView numLotProduitScannee = (TextView) ListView.findViewById(R.id.numLotProduitScannee);
        TextView datePeremptionProduitScannee = (TextView) ListView.findViewById(R.id.datePeremptionProduitScannee);
        final LinearLayout supprimerScan = (LinearLayout) ListView.findViewById(R.id.supprimerScan);
        LinearLayout layout_information_lot = (LinearLayout) ListView.findViewById(R.id.layout_information_lot);

        String gs1Courant = objetReceptionScannee_courant.getGs1_scannee();
        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1Courant);
        String gtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
        String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
        String datePeremption = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
        Produit produitCourant = null;

        if(gs1Decoupe.size() != 1 && !gs1Courant.startsWith("ci"))
        {
            produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
        }
        else
        {
            String codeinconnu = gs1Courant;
            if(codeinconnu.startsWith("ci"))
            {
                Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(objetReceptionScannee_courant.getGs1_scannee());
                codeinconnu = MapInconnu.get("Code_Inconnu");
                lot = MapInconnu.get("Lot");
                datePeremption = MapInconnu.get("Date");
            }

            List<Produit> list = ProduitOpenHelper.getProduitsParCodeInconnue(db, codeinconnu);
            if(list.size() == 1)
                produitCourant = list.get(0);
        }

        qteProduitScannee.setText(String.valueOf(objetReceptionScannee_courant.getQuantiteScannee()));
        numLotProduitScannee.setText(lot);

        if(datePeremption != null && !datePeremption.contentEquals(""))
        {
            String[] tab_date_peremption = datePeremption.split("-");
            String new_date = tab_date_peremption[2]+"/"+tab_date_peremption[1]+"/"+tab_date_peremption[0];
            datePeremptionProduitScannee.setText(new_date);
        }

        supprimerScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DetailReceptionPadActivity) context).supprimerScan(groupPosition, childPosition);
            }
        });

        layout_information_lot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(supprimerScan.getVisibility() == View.VISIBLE)
                {
                    supprimerScan.setVisibility(View.INVISIBLE);
                }
                else
                {
                    supprimerScan.setVisibility(View.VISIBLE);
                }
            }
        });

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
            ListView = infalInflater.inflate(R.layout.expandable_liste_groupe_reception_pad, null);
        }

        TextView desingationProduit = (TextView) ListView.findViewById(R.id.designationProduit);
        TextView referenceProduit = (TextView) ListView.findViewById(R.id.referenceProduit);
        TextView quantiteProduitReceptionPad = (TextView) ListView.findViewById(R.id.quantiteProduitReceptionPad);
        TextView quantiteCommanderProduitReceptionPad = (TextView) ListView.findViewById(R.id.quantiteCommanderProduitReceptionPad);
        LinearLayout layoutQteProduit = (LinearLayout) ListView.findViewById(R.id.layoutProduit);
        desingationProduit.setText(objetCourant.getDesignation_produit());
        referenceProduit.setText(objetCourant.getReferenceProduit());
        quantiteProduitReceptionPad.setText(String.valueOf(objetCourant.getQuantite_receptionner()));
        quantiteCommanderProduitReceptionPad.setText(String.valueOf(objetCourant.getQuantite_commander()));

        //gestion colorimétrie
        if(objetCourant.getQuantite_commander() == objetCourant.getQuantite_receptionner())
        {
            quantiteProduitReceptionPad.setTextColor(context.getResources().getColor(R.color.vert, null));
            layoutQteProduit.setBackground(context.getResources().getDrawable(R.drawable.background_reception_pad_vert, null));
        }
        else
        {
            quantiteProduitReceptionPad.setTextColor(context.getResources().getColor(R.color.orange, null));
            layoutQteProduit.setBackground(context.getResources().getDrawable(R.drawable.background_reception_pad_orange, null));
        }

        return ListView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}