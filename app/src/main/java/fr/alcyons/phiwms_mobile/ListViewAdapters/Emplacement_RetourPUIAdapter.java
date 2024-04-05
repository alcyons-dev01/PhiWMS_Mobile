package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_RetourPUI_Adapte;
import com.example.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 03/08/2017.
 */

public class Emplacement_RetourPUIAdapter extends ArrayAdapter {

    public List<Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte> emplacementAdapteList;

    public List<EmplacementViewHolder> viewHolderList;
    public Context context;
    public SQLiteDatabase db;
    public Retour_Ligne_RetourPUI_Adapte retourLigneAdapte;

    public List<Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte> emplacementAdapteDeBaseList;

    public HashMap<Integer, Boolean> emplacementAdaptesSelectedHasMap = new HashMap<>();
    public int qteRestanteARetourner;

    public Emplacement_RetourPUIAdapter(Context context, List<Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte> emplacementAdaptes, SQLiteDatabase db, Retour_Ligne_RetourPUI_Adapte retourLigneAdapte) {
        super(context, 0, emplacementAdaptes);
        this.viewHolderList = new ArrayList<>();
        this.context = context;
        this.emplacementAdapteList = emplacementAdaptes;
        this.db = db;
        this.retourLigneAdapte = retourLigneAdapte;

        this.emplacementAdapteDeBaseList = new ArrayList<>();
        for (Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapte : this.emplacementAdapteList
                ) {
            this.emplacementAdapteDeBaseList.add(emplacementAdapte);
            this.viewHolderList.add(new EmplacementViewHolder());
        }

        qteRestanteARetourner = 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_emplacement_retour_pui, parent, false);
        }

        EmplacementViewHolder viewHolder = (EmplacementViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = viewHolderList.get(position);
            viewHolder.nomEmplacement = (TextView) convertView.findViewById(R.id.nomEmplacement);
            viewHolder.nomZone = (TextView) convertView.findViewById(R.id.nomZone);
            viewHolder.qteRetournee = (TextView) convertView.findViewById(R.id.qteRetournee);
        }

        viewHolder.qteRetournee.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EmplacementViewHolder viewHolder = null;
                for (EmplacementViewHolder viewHolderCourant : viewHolderList
                        ) {
                    if (viewHolderCourant.qteRetournee == v) {
                        viewHolder = viewHolderCourant;
                    }
                }
                if (viewHolder != null) {
                    if (!viewHolder.qteRetournee.getText().toString().equals("")) {
                        retourLigneAdapte.getEmplacementAdaptes().get(position).setQte(Integer.parseInt(viewHolder.qteRetournee.getText().toString()));
                    }
                }
                return false;
            }
        });

        Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapte = (Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte) getItem(position);

        Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementAdapte.getEmplacementID());
        Depot_Zone zone = (Depot_Zone) ZoneOpenHelper.getUneZoneByID(db, emplacement.getZoneID());

        viewHolder.nomEmplacement.setText(emplacement.getAdressage());
        viewHolder.nomZone.setText(zone.getZoneName());
        if (retourLigneAdapte.getEmplacementAdaptes().get(position).getQte() == 0 && emplacementAdapteList.size() == 1) {
            Retour_Ligne retourLigneActuel = Retour_LigneOpenHelper.getRetourLigneByID(db, retourLigneAdapte.getRetourLigneID());
            retourLigneAdapte.getEmplacementAdaptes().get(position).setQte(0);
        }

        viewHolder.qteRetournee.setText(String.valueOf(retourLigneAdapte.getEmplacementAdaptes().get(position).getQte()));

        return convertView;
    }

    public void setNewSelection(int position, boolean value) {
        emplacementAdaptesSelectedHasMap.put(position, value);
    }

    public boolean isPositionChecked(int position) {
        Boolean result = emplacementAdaptesSelectedHasMap.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return emplacementAdaptesSelectedHasMap.keySet();
    }

    public void removeSelection(int position) {
        emplacementAdaptesSelectedHasMap.remove(position);
    }

    public void remove() {

       for (HashMap.Entry<Integer, Boolean> entry : emplacementAdaptesSelectedHasMap.entrySet()) {
            int position = entry.getKey();
            boolean checked = entry.getValue();
            if (checked) {
                Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapte = emplacementAdapteDeBaseList.get(position);
                qteRestanteARetourner = qteRestanteARetourner  + emplacementAdapte.getQte();
                emplacementAdapteList.remove(emplacementAdapte);
                emplacementAdapteDeBaseList.remove(emplacementAdapte);
                int position_view_holder = -1;
                Depot_Emplacement emplacementCourant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementAdapte.getEmplacementID());
                for(int i=0; i < viewHolderList.size(); i++)
                {
                    if(viewHolderList.get(i).nomEmplacement.getText().toString().contentEquals(emplacementCourant.getAdressage()))
                    {
                        position_view_holder = i;
                        break;
                    }
                }

                if(position_view_holder != -1)
                    viewHolderList.remove(viewHolderList.get(position_view_holder));
                this.notifyDataSetChanged();
            }
        }
        clearSelection();
    }

    public void clearSelection() {
        emplacementAdaptesSelectedHasMap = new HashMap<>();
    }

    public class EmplacementViewHolder {
        public TextView nomZone;
        public TextView nomEmplacement;
        public TextView qteRetournee;
    }

}
