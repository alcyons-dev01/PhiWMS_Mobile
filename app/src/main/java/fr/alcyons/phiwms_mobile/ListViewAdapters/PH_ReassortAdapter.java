package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.PH_Reassort;
import com.example.phiwms_mobile.R;

/**
 * Created by jessica on 05/10/2017.
 */

public class PH_ReassortAdapter extends ArrayAdapter {

    public List<PH_Reassort> PH_Reassorts;
    public List<PH_ReassortViewHolder> PH_ReassortsViewHolderList;
    Context context;
    SQLiteDatabase db;

    public PH_ReassortAdapter(Context context, List<PH_Reassort> PH_Reassorts, SQLiteDatabase db) {
        super(context, 0, PH_Reassorts);
        this.PH_Reassorts = PH_Reassorts;
        this.context = context;
        this.db = db;
        PH_ReassortsViewHolderList = new ArrayList<>();
        for (int i = 0; i < PH_Reassorts.size(); i++) {
            PH_ReassortViewHolder viewHolder = new PH_ReassortViewHolder();
            PH_ReassortsViewHolderList.add(viewHolder);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PH_ReassortViewHolder viewHolder = PH_ReassortsViewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_ph_reassort, parent, false);

        // Récupération des objets graphiques
        viewHolder.nom = (TextView) convertView.findViewById(R.id.intitulePH_Reassort);

        PH_Reassort PH_ReassortCourant = (PH_Reassort) getItem(position);

        // Affichage des valeurs
        viewHolder.nom.setText(PH_ReassortCourant.getListe());

        return convertView;
    }

    private class PH_ReassortViewHolder {
        public TextView nom;
    }
}