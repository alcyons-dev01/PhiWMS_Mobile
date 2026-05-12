package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class RetourQuarantaineAdapter extends ArrayAdapter implements Filterable {

    public List<Retour> retourList;
    public List<Retour> retourDeBaseList;
    public List<Retour_Ligne> retourLigneList;
    Context context;
    SQLiteDatabase db;
    Utilisateur utilisateur;
    public RetourQuarantaineAdapter(Context context, SQLiteDatabase database, List<Retour> retourList, Utilisateur utilisateur) {
        super(context, 0, retourList);
        this.context = context;
        this.db = database;

        this.retourList = retourList;
        this.retourDeBaseList = new ArrayList<>();
        this.retourDeBaseList.addAll(retourList);

        this.utilisateur = utilisateur;
    }


    @NonNull
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_retour_quarantaine, parent, false);
        }

        RetourViewHolder viewHolder = (RetourViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new RetourViewHolder();
            viewHolder.intitule = (TextView) convertView.findViewById(R.id.intitule);
            viewHolder.sommeProduit = (TextView) convertView.findViewById(R.id.sommeProduit);
            viewHolder.motif = (TextView) convertView.findViewById(R.id.motif);
            viewHolder.numero = (TextView) convertView.findViewById(R.id.numero);
            viewHolder.depotOrigine = (TextView) convertView.findViewById(R.id.depotOrigine);
            convertView.setTag(viewHolder);
        }

        Retour retour = (Retour) getItem(position);

        if (retour != null) {
            retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retour);


            int size = retourLigneList.size();

            String[] intitule_tab = retour.getIntitule().split(":");
            String intitule_split = intitule_tab[intitule_tab.length-1].trim();
            String depot_origine = intitule_tab[0];

            viewHolder.sommeProduit.setText(String.valueOf(size));
            viewHolder.intitule.setText(intitule_split);
            viewHolder.motif.setText(retour.getMotif());
            viewHolder.numero.setText("#"+retour.getNumero());

            if(retour.getRef_Depot_Origine().contains("-PAD-") && utilisateur.getIdentifiant().toLowerCase().contains("alcyons"))
            {
                depot_origine = "XXX-PAD-XXX";
            }
            viewHolder.depotOrigine.setText(depot_origine);
        }

        return convertView;
    }

    private static class RetourViewHolder {
        public TextView intitule;
        public TextView motif;
        public TextView numero;
        public TextView sommeProduit;
        public TextView depotOrigine;
    }

}
