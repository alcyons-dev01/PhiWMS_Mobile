package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.R;

public class Retour_Ligne_RetourFournisseurAdapter extends ArrayAdapter {

    public List<Retour_Ligne> retour_Lignes;
    public List<Retour_LigneViewHolder> retourLigneViewHolderList;
    Context context;

    public Retour_Ligne_RetourFournisseurAdapter(Context context, List<Retour_Ligne> retour_Lignes) {
        super(context, 0, retour_Lignes);
        this.retour_Lignes = retour_Lignes;
        this.context = context;
        retourLigneViewHolderList = new ArrayList<>();
        for (int i = 0; i < retour_Lignes.size(); i++) {
            Retour_LigneViewHolder viewHolder = new Retour_LigneViewHolder();
            retourLigneViewHolderList.add(viewHolder);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Retour_LigneViewHolder viewHolder = retourLigneViewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_retour_ligne_retour_fournisseur, parent, false);

        // Récupération des objets graphiques
        viewHolder.designation = (TextView) convertView.findViewById(R.id.designationProduit);
        viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
        viewHolder.qteRetourner = (TextView) convertView.findViewById(R.id.QteRetourner);
        viewHolder.lot = (TextView) convertView.findViewById(R.id.lotRetourne);
        viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
        viewHolder.serie = (TextView) convertView.findViewById(R.id.numSerie);
        viewHolder.Labelserie = (TextView) convertView.findViewById(R.id.labelSerie);

        Retour_Ligne retour_LigneCourant = (Retour_Ligne) getItem(position);

        // Affichage des valeurs
        viewHolder.designation.setText(retour_LigneCourant.getProduit_Designation());
        viewHolder.referenceProduit.setText(retour_LigneCourant.getProduit_Reference());
        viewHolder.qteRetourner.setText(retour_LigneCourant.getQte_Retourner() == 0 ? "" : String.valueOf((int) retour_LigneCourant.getQte_Retourner()));
        viewHolder.lot.setText(retour_LigneCourant.getLot_Retourner());

        if(retour_LigneCourant.getSerie_Retourner().contentEquals(""))
        {
            viewHolder.serie.setVisibility(View.GONE);
            viewHolder.Labelserie.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.serie.setText(retour_LigneCourant.getSerie_Retourner());
        }

        Date date = null;
        String dateAAfficher = "";
        DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (retour_LigneCourant.getPeremptionDate().length() >= 10) {
                date = dateDecodeur.parse(retour_LigneCourant.getPeremptionDate().substring(0, 10));
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dateAAfficher = dateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.datePeremption.setText(dateAAfficher);
        viewHolder.setDatePeremptionColor(date);

        return convertView;
    }

    public class Retour_LigneViewHolder {
        public TextView designation;
        public TextView referenceProduit;
        //public TextView fournisseur;
        public TextView qteRetourner;
        public TextView qteAvantRetour;
        public TextView lot;
        public TextView serie;
        public TextView Labelserie;
        public TextView datePeremption;

        public void setDatePeremptionColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.rouge2));
                } else if (delai >= delai60jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.orange2));
                } else {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.vert));
                }
            } else {
                datePeremption.setTextColor(Color.BLACK);
            }

        }
    }
}
