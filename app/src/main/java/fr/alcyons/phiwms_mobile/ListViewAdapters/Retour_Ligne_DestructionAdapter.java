package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.R;

public class Retour_Ligne_DestructionAdapter extends ArrayAdapter
{
    // OTHERS
    public List<Retour_Ligne> retour_Lignes;

    // UI
    Context context;
    public List<Retour_LigneViewHolder> retourLigneViewHolderList;

    public Retour_Ligne_DestructionAdapter(Context context, List<Retour_Ligne> retour_Lignes)
    {
        super(context, 0, retour_Lignes);

        this.retour_Lignes = retour_Lignes;
        this.context = context;

        this.retourLigneViewHolderList = new ArrayList<>();
        for (int i = 0; i < retour_Lignes.size(); i++)
        {
            Retour_LigneViewHolder viewHolder = new Retour_LigneViewHolder();
            this.retourLigneViewHolderList.add(viewHolder);
        }
    }

    @NonNull
    @Override public View getView(final int position, View convertView, @NonNull ViewGroup parent)
    {
        final Retour_LigneViewHolder viewHolder = retourLigneViewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_retour_ligne_destruction, parent, false);

        // Récupération des objets graphiques
        viewHolder.designation = convertView.findViewById(R.id.designationProduit);
        viewHolder.referenceProduit = convertView.findViewById(R.id.referenceProduit);
        viewHolder.fournisseur = convertView.findViewById(R.id.nomFournisseur);
        viewHolder.qteRetourner = convertView.findViewById(R.id.QteRetourner);
        viewHolder.lot = convertView.findViewById(R.id.lotRetourne);
        viewHolder.datePeremption = convertView.findViewById(R.id.datePeremption);
        viewHolder.serie = convertView.findViewById(R.id.numSerie);
        viewHolder.labelSerie = convertView.findViewById(R.id.labelSerie);

        final Retour_Ligne retour_LigneCourant = (Retour_Ligne) getItem(position);

        // Affichage des valeurs
        viewHolder.designation.setText(retour_LigneCourant.getProduit_Designation());
        viewHolder.referenceProduit.setText(retour_LigneCourant.getProduit_Reference());
        viewHolder.fournisseur.setText(retour_LigneCourant.getProduit_Fournisseur());
        viewHolder.qteRetourner.setText(retour_LigneCourant.getQte_Retourner() == 0 ? "" : String.valueOf((int) retour_LigneCourant.getQte_Retourner()));
        viewHolder.lot.setText(retour_LigneCourant.getLot_Retourner());

        //gestion du numéro de série
        if(retour_LigneCourant.getSerie_Retourner().contentEquals(""))
        {
            viewHolder.serie.setVisibility(View.GONE);
            viewHolder.labelSerie.setVisibility(View.GONE);
        }
        else { viewHolder.lot.setText(retour_LigneCourant.getSerie_Retourner()); }

        Date date = null;
        String dateAAfficher = "";
        DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            if (retour_LigneCourant.getPeremptionDate().length() >= 10 && !retour_LigneCourant.getPeremptionDate().contentEquals("0000-00-00"))
            {
                date = dateDecodeur.parse(retour_LigneCourant.getPeremptionDate().substring(0, 10));
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                assert date != null;
                dateAAfficher = dateFormat.format(date);
            }
        }
        catch (ParseException e) { e.printStackTrace(); }

        viewHolder.datePeremption.setText(dateAAfficher);
        viewHolder.setDatePeremptionColor(date);

        return convertView;
    }

    public class Retour_LigneViewHolder
    {
        public TextView designation;
        public TextView referenceProduit;
        public TextView fournisseur;
        public TextView qteRetourner;
        public TextView lot;
        public TextView datePeremption;
        public TextView serie;
        public TextView labelSerie;

        public void setDatePeremptionColor(Date date)
        {
            if (date != null)
            {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) { this.datePeremption.setTextColor(Retour_Ligne_DestructionAdapter.this.context.getResources().getColor(R.color.rouge2)); }
                else if (delai >= delai60jours) { this.datePeremption.setTextColor(Retour_Ligne_DestructionAdapter.this.context.getResources().getColor(R.color.orange2)); }
                else { this.datePeremption.setTextColor(Retour_Ligne_DestructionAdapter.this.context.getResources().getColor(R.color.vert)); }
            }
            else { this.datePeremption.setTextColor(Color.BLACK); }

        }
    }
}
