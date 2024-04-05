package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 13/05/2019.
 */

public class ActionLigneAdapter extends BaseExpandableListAdapter {

    Map<String, List<ActionUtilisateur_Ligne>> tempObjetParentListItems;
    List<ActionUtilisateur_Ligne> tempObjetListe;
    private Activity context;
    private Map<String, List<ActionUtilisateur_Ligne>> objetParentListItems;
    private Map<String, List<ActionUtilisateur_Ligne>> objetOriginalParentListItems;
    private List<String> emplacementList;
    private Utilisateur utilisateurCourant;
    private SQLiteDatabase db;
    private String service;

    public ActionLigneAdapter(Activity context, SQLiteDatabase db, List<String> emplacementList, Map<String, List<ActionUtilisateur_Ligne>> objetParentListItems, Utilisateur utilisateurCourant, String service) {
        this.context = context;
        this.objetParentListItems = new LinkedHashMap<>();
        this.objetParentListItems.putAll(objetParentListItems);
        this.objetOriginalParentListItems = new LinkedHashMap<>();
        this.objetOriginalParentListItems.putAll(objetParentListItems);
        this.emplacementList = emplacementList;
        this.utilisateurCourant = utilisateurCourant;
        this.db = db;
        this.service = service;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return objetOriginalParentListItems.get(emplacementList.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View ListView, ViewGroup parent) {
        final ActionUtilisateur_Ligne actionCourante = (ActionUtilisateur_Ligne) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (ListView == null) {
            ListView = inflater.inflate(R.layout.row_action_ligne_utilisateur, null);
        }

        TextView nomChamps = (TextView) ListView.findViewById(R.id.nomChamps);
        TextView quantiteActionLigne = (TextView) ListView.findViewById(R.id.quantiteActionLigne);
        TextView numeroLotActionLigne = (TextView) ListView.findViewById(R.id.numeroLotActionLigne);
        TextView datePeremptionActionLigne = (TextView) ListView.findViewById(R.id.datePeremptionActionLigne);
        TextView numeroSerieActionLigne = (TextView) ListView.findViewById(R.id.numeroSerieActionLigne);

        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(actionCourante.getGS1());
        if(gs1Decoupe.size() > 1)
        {
            String gtinCourant = gs1Decoupe.get(OutilsDecodage.codeGtin);
            String lotCourant = gs1Decoupe.get(OutilsDecodage.numeroLot);
            String datePeremptionString = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
            String serieCourant = gs1Decoupe.get(OutilsDecodage.numeroSerie);
            DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
            DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

            Date date = new Date();
            try {
                date = dateFormat1.parse(datePeremptionString);
                datePeremptionString =  dateFormat2.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            PH_Reliquat ph_reliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, actionCourante.getNumChamps());
            Produit produit = null;
            String designation = "";

            if(ph_reliquat == null)
            {
                produit = ProduitOpenHelper.getUnProduitParGTIN(db, gtinCourant);
                if(produit != null)
                {
                    designation = produit.getDesignation_interne();
                }
            }
            else
            {
                designation = ph_reliquat.getDesignationCourte();
            }

            if(designation.contentEquals(""))
                designation = actionCourante.getNom_Produit();

            nomChamps.setText(designation);
            numeroLotActionLigne.setText(lotCourant);
            datePeremptionActionLigne.setText(datePeremptionString);
            quantiteActionLigne.setText(String.valueOf(actionCourante.getQuantite()));
            if(!serieCourant.contentEquals(""))
            {
                numeroSerieActionLigne.setVisibility(View.VISIBLE);
                numeroSerieActionLigne.setText(serieCourant);
            }
            else
            {
                numeroSerieActionLigne.setVisibility(View.GONE);
            }
        }
        else
        {

                nomChamps.setText(actionCourante.getNom_Produit());
                quantiteActionLigne.setText(String.valueOf(actionCourante.getQuantite()));
                numeroSerieActionLigne.setVisibility(View.GONE);
                numeroLotActionLigne.setVisibility(View.GONE);
                datePeremptionActionLigne.setVisibility(View.GONE);

        }

        return ListView;
    }

    public int getChildrenCount(int groupPosition) {
        return objetParentListItems.get(emplacementList.get(groupPosition)).size();
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
            ListView = infalInflater.inflate(R.layout.expandable_liste_groupe_action_ligne, null);
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
            tempObjetParentListItems = new LinkedHashMap<String, List<ActionUtilisateur_Ligne>>();

            for (Map.Entry<String, List<ActionUtilisateur_Ligne>> entry : objetOriginalParentListItems.entrySet()) {

                tempObjetListe = new ArrayList<ActionUtilisateur_Ligne>();
                String depotType = entry.getKey();
                List<ActionUtilisateur_Ligne> objettList = entry.getValue();
                for (int j = 0; j < objettList.size(); j++) {
                    if (objettList.get(j).getGS1().toLowerCase().contains(query)) {
                        tempObjetListe.add(objettList.get(j));
                    }
                }
                if (tempObjetListe.size() > 0) {
                    tempObjetParentListItems.put(depotType, tempObjetListe);
                } else {
                    tempObjetParentListItems.put(depotType, new ArrayList<ActionUtilisateur_Ligne>());
                }
            }
            if (tempObjetParentListItems.size() > 0) {
                objetParentListItems.putAll(tempObjetParentListItems);
            }
        }
    }
}