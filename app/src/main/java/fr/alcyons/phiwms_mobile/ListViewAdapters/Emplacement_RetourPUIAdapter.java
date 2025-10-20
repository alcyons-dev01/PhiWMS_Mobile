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

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_RetourPUI_Adapte;
import fr.alcyons.phiwms_mobile.R;

public class Emplacement_RetourPUIAdapter extends ArrayAdapter {


    public List<EmplacementViewHolder> viewHolderList;
    public Context context;
    public SQLiteDatabase db;
    public List<Retour_Ligne> retourLigneList;
    public int qteRestanteARetourner;

    public Emplacement_RetourPUIAdapter(Context context, List<Retour_Ligne> retourLigneList, SQLiteDatabase db) {
        super(context, 0, retourLigneList);
        this.viewHolderList = new ArrayList<>();
        this.context = context;
        this.retourLigneList = retourLigneList;
        this.db = db;

        for (Retour_Ligne retourLigneTemp : this.retourLigneList)
        {
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

        Retour_Ligne retourLigne = (Retour_Ligne) getItem(position);
        viewHolder.nomEmplacement.setText(retourLigne.getRetourPUI_Emplacement());
        viewHolder.nomZone.setText(retourLigne.getRetourPUI_Zone());

        viewHolder.qteRetournee.setText(String.valueOf((int)retourLigne.getQte_Retourner()));

        return convertView;
    }

    public class EmplacementViewHolder {
        public TextView nomZone;
        public TextView nomEmplacement;
        public TextView qteRetournee;
    }

}
