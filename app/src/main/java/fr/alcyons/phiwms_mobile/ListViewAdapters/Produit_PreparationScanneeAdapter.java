package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import com.example.phiwms_mobile.R;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

/**
 * Created by olivier on 06/06/2019.
 */

public class Produit_PreparationScanneeAdapter extends BaseExpandableListAdapter {

    Map<String, List<ObjetReceptionScannee>> tempObjetParentListItems;
    List<ObjetReceptionScannee> tempObjetListe;
    private Activity context;
    public Map<String, List<ObjetReceptionScannee>> objetParentListItems;
    Map<String, String> Map_Zone_Emplacement;
    private Map<String, List<ObjetReceptionScannee>> objetOriginalParentListItems;
    public List<String> produitListe;
    private Utilisateur utilisateurCourant;
    private SQLiteDatabase db;

    public Produit_PreparationScanneeAdapter(Activity context, SQLiteDatabase db, List<String> produitListe, Map<String, List<ObjetReceptionScannee>> objetParentListItems, Utilisateur utilisateurCourant, Map<String, String> Map_Zone_Emplacement) {
        this.context = context;
        this.objetParentListItems = new LinkedHashMap<>();
        this.objetOriginalParentListItems = new LinkedHashMap<>();
        this.Map_Zone_Emplacement = new LinkedHashMap<>();
        this.Map_Zone_Emplacement = Map_Zone_Emplacement;
        this.produitListe = new ArrayList<>();
        this.utilisateurCourant = utilisateurCourant;
        this.db = db;

        for(Map.Entry<String, List<ObjetReceptionScannee>> entry : objetParentListItems.entrySet())
        {
            String key = entry.getKey();
            String[] tab_key = key.split("-");
            String value = tab_key[tab_key.length-1];
            if(!value.contentEquals("0"))
            {
                this.objetParentListItems.put(entry.getKey(), entry.getValue());
                this.objetOriginalParentListItems.put(entry.getKey(), entry.getValue());
                this.produitListe.add(key);
            }
        }

    }

    public Object getChild(int groupPosition, int childPosition) {
        return objetOriginalParentListItems.get(produitListe.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View ListView, ViewGroup parent) {
        final ObjetReceptionScannee objetReceptionScannee_courant = (ObjetReceptionScannee) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (ListView == null) {
            ListView = inflater.inflate(R.layout.row_zone_emplacement_preparation_scannee, null);
        }

        TextView zone = (TextView) ListView.findViewById(R.id.zone);
        TextView emplacement = (TextView) ListView.findViewById(R.id.emplacement);
        final TextView quantite = (TextView) ListView.findViewById(R.id.quantite);
        TextView numeroLot = (TextView) ListView.findViewById(R.id.numeroLot);
        TextView numeroSerie = (TextView) ListView.findViewById(R.id.numeroSerie);
        TextView datePeremption = (TextView) ListView.findViewById(R.id.datePeremption);


        String gs1Courant = objetReceptionScannee_courant.getGs1_scannee();
        String gtin = "";
        String serie = "";
        String lot = "";
        String peremption = "";
        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1Courant);
        Produit produitCourant = null;
        if(gs1Decoupe.size() != 1 && !objetReceptionScannee_courant.getGs1_scannee().startsWith("ci"))
        {
            gtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
            serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
            lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
            peremption = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
            produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
        }
        else
        {
            String code_inconnu = objetReceptionScannee_courant.getGs1_scannee();
            if(code_inconnu.startsWith("ci"))
            {
                Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(code_inconnu);
                code_inconnu = MapInconnu.get("Code_Inconnu");
                lot = MapInconnu.get("Lot");
                peremption = MapInconnu.get("Date");
            }

            produitCourant = ProduitOpenHelper.getUnProduitByCodeInconnu(db, code_inconnu);
        }


        Depot_Emplacement emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, objetReceptionScannee_courant.getEmplacement_uid());
        if(emplacement_courant != null)
        {
            Depot_Zone zone_courant = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());
            zone.setText(zone_courant.getZoneName());
            emplacement.setText(emplacement_courant.getAdressage());
        }

        numeroLot.setText(lot);
        numeroSerie.setText(serie);

        //gestion affichage date
        if(!peremption.contentEquals(""))
        {
            String[] tab_date = peremption.split("-");
            peremption = tab_date[tab_date.length-1]+"/"+tab_date[1]+"/"+tab_date[0];
        }
        datePeremption.setText(peremption);
        quantite.setText(String.valueOf(objetReceptionScannee_courant.getQuantiteScannee()));
        int quantiteDejaScan = 0;
        int quantiteRestant = 0;
        String phraseGroup = getGroup(groupPosition).toString();
        String[] tab_phrase_groupe = phraseGroup.split("-");
        int quantite_total = Integer.parseInt(tab_phrase_groupe[tab_phrase_groupe.length-1]);
        if(objetParentListItems.get(produitListe.get(groupPosition)) != null)
        {
            List<ObjetReceptionScannee> listEnfant = objetParentListItems.get(produitListe.get(groupPosition));
            for(int i = 0; i < listEnfant.size(); i++)
            {
                if(i < listEnfant.size()-1)
                {
                    ObjetReceptionScannee objetCourant = listEnfant.get(i);
                    quantiteDejaScan = quantiteDejaScan+objetCourant.getQuantiteScannee();
                }
            }
        }

        quantiteRestant = quantite_total - quantiteDejaScan;

        final int finalQuantiteRestant = quantiteRestant;
        final Produit finalProduitCourant = produitCourant;
        quantite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvre une boite de dialogue avec un NumberPicker
                String title = finalProduitCourant.getDesignation_interne();
                String message = "Changer la quantité: ";
                int maxValue = finalQuantiteRestant;
                int value = objetReceptionScannee_courant.getQuantiteScannee();

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = aNumberPicker.getValue();
                        quantite.setText(String.valueOf(qteAprès).trim());
                        objetReceptionScannee_courant.setQuantiteScannee(qteAprès);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                        ((ScannerSearchOnlyActivity)context).GestionAffichagePreparation();
                        ((ScannerSearchOnlyActivity)context).expandGroupAfterQuantite(finalProduitCourant.getDesignation_interne());
                    }
                };

                Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

            }
        });

        return ListView;
    }

    public int getChildrenCount(int groupPosition) {
        if(objetParentListItems != null && objetParentListItems.size() != 0)
        {
            if( objetParentListItems.get(produitListe.get(groupPosition)) != null)
            {
                return objetParentListItems.get(produitListe.get(groupPosition)).size();
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return 0;
        }
    }

    public Object getGroup(int groupPosition) {
        return produitListe.get(groupPosition);
    }

    public int getGroupCount() {
        return produitListe.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View ListView, ViewGroup parent) {

        String produitCourant = (String) getGroup(groupPosition);
        if (ListView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListView = infalInflater.inflate(R.layout.expandable_liste_groupe_preparationscannee, null);
        }

        String[] tabProduit = produitCourant.split("-");
        String designation = tabProduit[0];
        String quantiteAAfficher = tabProduit[tabProduit.length-1];

        LinearLayout layoutparent = (LinearLayout) ListView.findViewById(R.id.layoutparent);
        TextView nomProduit = (TextView) ListView.findViewById(R.id.designationProduit);
        TextView compteurQuantite = (TextView) ListView.findViewById(R.id.compteurQuantite);
        TextView ZoneParDefaut = (TextView) ListView.findViewById(R.id.ZoneParDefaut);
        TextView EmplacementParDefaut = (TextView) ListView.findViewById(R.id.EmplacementParDefaut);

        String chaine_zone_emplacement_defaut = Map_Zone_Emplacement.get(designation);
        if(chaine_zone_emplacement_defaut != null)
        {
            String[] tab_zone_emplacement = chaine_zone_emplacement_defaut.split("%");
            String zone_defaut = tab_zone_emplacement[0];
            String emplacement_defaut = tab_zone_emplacement[tab_zone_emplacement.length-1];
            ZoneParDefaut.setText(zone_defaut);
            EmplacementParDefaut.setText(emplacement_defaut);
        }

        nomProduit.setText(designation);
        int quantiteEnfant = 0;
        if(objetParentListItems.get(produitListe.get(groupPosition)) != null)
        {
            List<ObjetReceptionScannee> listEnfant = objetParentListItems.get(produitListe.get(groupPosition));
            for(ObjetReceptionScannee objetCourant : listEnfant)
            {
                quantiteEnfant = quantiteEnfant+objetCourant.getQuantiteScannee();
            }
        }

        compteurQuantite.setText(quantiteEnfant+"/"+quantiteAAfficher);
        if(quantiteEnfant == Integer.parseInt(quantiteAAfficher))
        {
            compteurQuantite.setBackgroundResource(R.color.vert3);
            layoutparent.setBackgroundResource(R.color.vert3);
        }
        else
        {
            layoutparent.setBackgroundResource(R.color.orange);
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