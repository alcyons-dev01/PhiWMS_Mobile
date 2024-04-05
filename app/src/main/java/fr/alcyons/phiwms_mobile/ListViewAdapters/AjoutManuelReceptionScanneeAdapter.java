package fr.alcyons.phiwms_mobile.ListViewAdapters;

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

import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import com.example.phiwms_mobile.R;
import com.example.phiwms_mobile.ReceptionScanne.AjoutManuelReceptionScanneeActivity;

public class AjoutManuelReceptionScanneeAdapter extends BaseExpandableListAdapter {

    Map<PH_Reliquat, List<ObjetReceptionScannee>> tempObjetParentListItems;
    List<ObjetReceptionScannee> tempObjetListe;
    private Activity context;
    private Map<PH_Reliquat, List<ObjetReceptionScannee>> objetParentListItems;
    private Map<PH_Reliquat, List<ObjetReceptionScannee>> objetOriginalParentListItems;
    private List<PH_Reliquat> designationList;
    private Utilisateur utilisateurCourant;
    private SQLiteDatabase db;

    public AjoutManuelReceptionScanneeAdapter(Activity context, SQLiteDatabase db, List<PH_Reliquat> designationList, Map<PH_Reliquat, List<ObjetReceptionScannee>> objetParentListItems, Utilisateur utilisateurCourant) {
        this.context = context;
        this.objetParentListItems = new LinkedHashMap<>();
        this.objetParentListItems.putAll(objetParentListItems);
        this.objetOriginalParentListItems = new LinkedHashMap<>();
        this.objetOriginalParentListItems.putAll(objetParentListItems);
        this.designationList = designationList;
        this.utilisateurCourant = utilisateurCourant;
        this.db = db;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return objetOriginalParentListItems.get(designationList.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View ListView, ViewGroup parent) {
        final ObjetReceptionScannee objetReceptionScannee_courant = (ObjetReceptionScannee) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (ListView == null) {
            ListView = inflater.inflate(R.layout.row_ajout_manuel_produit_reception_scannee, null);
        }

        TextView qteProduitScannee = (TextView) ListView.findViewById(R.id.qteProduitScannee);
        TextView ZoneObjetCourant = (TextView) ListView.findViewById(R.id.ZoneObjetCourant);
        TextView EmplacementObjetCourant = (TextView) ListView.findViewById(R.id.EmplacementObjetCourant);
        TextView numLotProduitScannee = (TextView) ListView.findViewById(R.id.numLotProduitScannee);
        TextView datePeremptionProduitScannee = (TextView) ListView.findViewById(R.id.datePeremptionProduitScannee);
        LinearLayout supprimerScan = (LinearLayout) ListView.findViewById(R.id.supprimerScan);

        String gs1Courant = objetReceptionScannee_courant.getGs1_scannee();
        int emplacement_uid_courant = objetReceptionScannee_courant.getEmplacement_uid();
        Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_uid_courant);
        if(emplacement != null)
        {
            Depot_Zone zone = ZoneOpenHelper.getUneZoneByID(db, emplacement.getZoneID());
            ZoneObjetCourant.setText(zone.getZoneName());
            EmplacementObjetCourant.setText(emplacement.getAdressage());
        }
        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1Courant);
        String lot = "";
        String datePeremption = "";

        if(gs1Decoupe.size() > 1 && !gs1Courant.startsWith("ci"))
        {
            lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
            datePeremption = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
        }
        else
        {
            Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(gs1Courant);
            lot = MapInconnu.get("Lot");
            datePeremption = MapInconnu.get("Date");
        }

        qteProduitScannee.setText(String.valueOf(objetReceptionScannee_courant.getQuantiteScannee()));
        numLotProduitScannee.setText(lot);
        //Gestion du format de la date de péremption
        if(!datePeremption.contentEquals(""))
        {
            String[] date_tab = datePeremption.split("-");
            String new_date = date_tab[date_tab.length-1]+"/"+date_tab[1]+"/"+date_tab[0];
            datePeremptionProduitScannee.setText(new_date);
        }


        supprimerScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AjoutManuelReceptionScanneeActivity) context).supprimerScan(groupPosition, childPosition);
            }
        });

        return ListView;
    }

    public int getChildrenCount(int groupPosition) {
        if(objetParentListItems != null)
            return objetParentListItems.get(designationList.get(groupPosition)).size();
        else
            return 0;
    }

    public Object getGroup(int groupPosition) {
        return designationList.get(groupPosition);
    }

    public int getGroupCount() {
        return designationList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View ListView, ViewGroup parent) {

        PH_Reliquat ph_reliquat = (PH_Reliquat) getGroup(groupPosition);
        if (ListView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListView = infalInflater.inflate(R.layout.expandable_liste_ajout_manuel_reception_scannee, null);
        }

        LinearLayout layout_parent = (LinearLayout) ListView.findViewById(R.id.layout_parent);
        TextView designationProduitTextView = (TextView) ListView.findViewById(R.id.designationProduit);
        TextView qteReception = (TextView) ListView.findViewById(R.id.qteReception);
        qteReception.setText(String.valueOf(ph_reliquat.getQteLivraison()+"/"+ph_reliquat.getQteCommande()));
        designationProduitTextView.setText(ph_reliquat.getDesignationCourte());

        if(ph_reliquat.getQteLivraison() == ph_reliquat.getQteCommande())
        {
            layout_parent.setBackgroundResource(R.color.vert3);
        }
        else
        {
            layout_parent.setBackgroundResource(R.color.orange);
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

        objetParentListItems.clear();

        if (query.isEmpty()) {
            objetParentListItems.putAll(objetOriginalParentListItems);
        } else {
            tempObjetParentListItems = new LinkedHashMap<PH_Reliquat, List<ObjetReceptionScannee>>();

            for (Map.Entry<PH_Reliquat, List<ObjetReceptionScannee>> entry : objetOriginalParentListItems.entrySet()) {

                tempObjetListe = new ArrayList<ObjetReceptionScannee>();
                PH_Reliquat reliquat = entry.getKey();
                List<ObjetReceptionScannee> objettList = entry.getValue();
                for (int j = 0; j < objettList.size(); j++) {
                    if (objettList.get(j).getGs1_scannee().toLowerCase().contains(query)) {
                        tempObjetListe.add(objettList.get(j));
                    }
                }
                if (tempObjetListe.size() > 0) {
                    tempObjetParentListItems.put(reliquat, tempObjetListe);
                } else {
                    tempObjetParentListItems.put(reliquat, new ArrayList<ObjetReceptionScannee>());
                }
            }
            if (tempObjetParentListItems.size() > 0) {
                objetParentListItems.putAll(tempObjetParentListItems);
            }
        }
    }
}