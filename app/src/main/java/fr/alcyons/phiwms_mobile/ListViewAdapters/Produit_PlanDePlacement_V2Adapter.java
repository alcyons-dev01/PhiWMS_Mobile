package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.R;

public class Produit_PlanDePlacement_V2Adapter extends BaseExpandableListAdapter {

    private final Context context;
    private final SQLiteDatabase db;
    private final Depot depotCourant;
    private final List<Produit> produitsScannes;

    private final List<Integer> idsProduitsGroupes;
    private final Map<Integer, List<PlanPlacementLigne>> enfantsParProduit;
    private Button boutonAction;
    public Produit_PlanDePlacement_V2Adapter(SQLiteDatabase db, Context context, ArrayList<String> produits, List<Produit> produitsScannes, Depot depotCourant, Button boutonAction) {
        this.context = context;
        this.db = db;
        this.depotCourant = depotCourant;
        this.produitsScannes = produitsScannes;

        this.idsProduitsGroupes = new ArrayList<>();
        this.enfantsParProduit = new LinkedHashMap<>();
        this.boutonAction = boutonAction;
        construireGroupes(produits);
    }

    private void construireGroupes(ArrayList<String> produits) {
        for (String ligne : produits) {
            String[] tabLigne = ligne.split("_");

            if (tabLigne.length >= 3) {
                int idProduit = Integer.parseInt(tabLigne[0]);
                String zone = tabLigne[1];
                String emplacement = tabLigne[2];

                if (!enfantsParProduit.containsKey(idProduit)) {
                    enfantsParProduit.put(idProduit, new ArrayList<PlanPlacementLigne>());
                    idsProduitsGroupes.add(idProduit);
                }

                enfantsParProduit.get(idProduit).add(new PlanPlacementLigne(idProduit, zone, emplacement));
            }
        }
    }

    @Override
    public int getGroupCount() {
        return idsProduitsGroupes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int idProduit = idsProduitsGroupes.get(groupPosition);
        List<PlanPlacementLigne> liste = enfantsParProduit.get(idProduit);
        return liste != null ? liste.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return idsProduitsGroupes.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        int idProduit = idsProduitsGroupes.get(groupPosition);
        return enfantsParProduit.get(idProduit).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return idsProduitsGroupes.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_produit_plan_placement_group, parent, false);
            viewHolder = new GroupViewHolder();
            viewHolder.nom = convertView.findViewById(R.id.nomProduit);
            viewHolder.refProduit = convertView.findViewById(R.id.refProduit);
            viewHolder.linearPrincipal = convertView.findViewById(R.id.linearPrincipal);
            viewHolder.nbEmplacements_TV = convertView.findViewById(R.id.nbEmplacements_TV);
            viewHolder.layoutIdEmplacement = convertView.findViewById(R.id.layoutIdEmplacement);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        int idProduit = idsProduitsGroupes.get(groupPosition);
        Produit produitCourant = ProduitOpenHelper.getProduitByID(db, idProduit);

        int nbEnfant = getChildrenCount(groupPosition);
        if(nbEnfant == 1)
            viewHolder.nbEmplacements_TV.setText(nbEnfant+" Emplacement");
        else
            viewHolder.nbEmplacements_TV.setText(nbEnfant+" Emplacements");

        viewHolder.nom.setText(produitCourant.getDesignation_interne());
        viewHolder.refProduit.setText(produitCourant.getRef_fourni());

        boolean selection = false;
        for (Produit temp : produitsScannes) {
            if (temp.getID_produit() == produitCourant.getID_produit()) {
                selection = true;
                break;
            }
        }

        viewHolder.layoutIdEmplacement.setOnClickListener(v -> {
            ExpandableListView expandableListView = (ExpandableListView) parent;
            if (isExpanded) {
                expandableListView.collapseGroup(groupPosition);
            } else {
                expandableListView.expandGroup(groupPosition);
            }
        });

        // ✅ Clic ailleurs (linearPrincipal) → autre action
        viewHolder.linearPrincipal.setOnClickListener(v -> {
            Produit produitCourant2 = ProduitOpenHelper.getProduitByID(db, idsProduitsGroupes.get(groupPosition));

            boolean suppression = false;
            int index = 0;

            for (Produit temp : produitsScannes) {
                if (temp.getID_produit() == produitCourant2.getID_produit()) {
                    produitsScannes.remove(index);
                    suppression = true;
                    break;
                }
                index++;
            }

            if (!suppression)
                produitsScannes.add(produitCourant2);

            // ✅ Mettre à jour le bouton — à adapter selon comment vous accédez à l'Activity
            if (produitsScannes.size() > 0)
                boutonAction.setVisibility(View.VISIBLE);
            else
                boutonAction.setVisibility(View.GONE);

            boutonAction.setText("Placer les " + produitsScannes.size() + " références");

            notifyDataSetChanged();
        });

        if (selection) {
            viewHolder.linearPrincipal.setBackgroundResource(R.drawable.background_element_liste_selection);
        } else {
            viewHolder.linearPrincipal.setBackgroundResource(R.drawable.background_element_liste);
            if (isExpanded) {
                viewHolder.linearPrincipal.setBackgroundResource(R.drawable.bg_group_expanded);
            } else {
                viewHolder.linearPrincipal.setBackgroundResource(R.drawable.bg_group_collapsed);
            }
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_produit_plan_placement_child, parent, false);
            viewHolder = new ChildViewHolder();
            viewHolder.zonePUI = convertView.findViewById(R.id.nomZonePUI);
            viewHolder.emplacementPUI = convertView.findViewById(R.id.nomEmplacementPUI);
            viewHolder.linearChildRoot = convertView.findViewById(R.id.linearChildRoot);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        PlanPlacementLigne ligne = (PlanPlacementLigne) getChild(groupPosition, childPosition);

        viewHolder.zonePUI.setText(ligne.getZone());
        viewHolder.emplacementPUI.setText(ligne.getEmplacement());

        int nbEnfants = getChildrenCount(groupPosition);

        if (nbEnfants == 1) {
            viewHolder.linearChildRoot.setBackgroundResource(R.drawable.bg_child_single);
        } else if (childPosition == nbEnfants - 1) {
            viewHolder.linearChildRoot.setBackgroundResource(R.drawable.bg_child_last);
        } else {
            viewHolder.linearChildRoot.setBackgroundResource(R.drawable.bg_child_middle);
        }
        convertView.setBackgroundColor(Color.WHITE);
        convertView.setPadding(0, 0, 0, 0);

// Supprimez le margin du LinearLayout enfant
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                viewHolder.linearChildRoot.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        viewHolder.linearChildRoot.setLayoutParams(params);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class GroupViewHolder {
        TextView nom;
        TextView refProduit;
        LinearLayout linearPrincipal;
        TextView nbEmplacements_TV;
        LinearLayout layoutIdEmplacement;
    }

    private static class ChildViewHolder {
        TextView zonePUI;
        TextView emplacementPUI;
        LinearLayout linearChildRoot;
    }

    private class PlanPlacementLigne {
        private final int idProduit;
        private final String zone;
        private final String emplacement;

        public PlanPlacementLigne(int idProduit, String zone, String emplacement) {
            this.idProduit = idProduit;
            this.zone = zone;
            this.emplacement = emplacement;
        }

        public int getIdProduit() {
            return idProduit;
        }

        public String getZone() {
            return zone;
        }

        public String getEmplacement() {
            return emplacement;
        }
    }
}