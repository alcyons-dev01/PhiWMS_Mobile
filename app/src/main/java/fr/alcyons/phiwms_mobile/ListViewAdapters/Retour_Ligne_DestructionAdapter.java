package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
    public final List<Retour_Ligne> mRetour_Lignes;

    // UI
    final Context mContext;
    private final List<Retour_LigneViewHolder> retourLigneViewHolderList;

    public Retour_Ligne_DestructionAdapter(final Context context, final List<Retour_Ligne> retour_Lignes)
    {
        super(context, 0, retour_Lignes);

        this.mRetour_Lignes = retour_Lignes;
        this.mContext = context;

        this.retourLigneViewHolderList = new ArrayList<>();
        for (int i = 0; i < this.mRetour_Lignes.size(); i++)
        {
            final Retour_LigneViewHolder viewHolder = new Retour_LigneViewHolder();
            this.retourLigneViewHolderList.add(viewHolder);
        }
    }

    @NonNull
    @Override public View getView(final int position, View convertView, @NonNull final ViewGroup parent)
    {
        final Retour_LigneViewHolder viewHolder = this.retourLigneViewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_a_detruire, parent, false);

        // Récupération des objets graphiques
        viewHolder.designation = convertView.findViewById(R.id.designationProduit);
        viewHolder.referenceProduit = convertView.findViewById(R.id.referenceProduit);
        viewHolder.fournisseur = convertView.findViewById(R.id.nomFournisseur);
        viewHolder.informationLot = convertView.findViewById(R.id.InformationLot_LL);
        viewHolder.lot = convertView.findViewById(R.id.lotRetourne);
        viewHolder.datePeremption = convertView.findViewById(R.id.datePeremption);
        viewHolder.serie = convertView.findViewById(R.id.numSerie);
        viewHolder.labelSerie = convertView.findViewById(R.id.labelSerie);
        viewHolder.bandeauQteADetruire = convertView.findViewById(R.id.bandeauQteADetruire);
        viewHolder.qteRetourner = convertView.findViewById(R.id.QteRetourner);

        final Retour_Ligne retour_LigneCourant = (Retour_Ligne) getItem(position);

        // Affichage des valeurs
        viewHolder.designation.setText(retour_LigneCourant.getProduit_Designation());
        viewHolder.referenceProduit.setText(retour_LigneCourant.getProduit_Reference());
        viewHolder.fournisseur.setText(retour_LigneCourant.getProduit_Fournisseur());
        viewHolder.informationLot.setVisibility(View.VISIBLE);
        viewHolder.lot.setText(retour_LigneCourant.getLot_Retourner());
        //gestion du numéro de série
        viewHolder.lot.setText(retour_LigneCourant.getSerie_Retourner());
        viewHolder.bandeauQteADetruire.setVisibility(View.VISIBLE);
        viewHolder.qteRetourner.setText((double) 0 == retour_LigneCourant.getQte_Retourner() ? "" : String.valueOf((int) retour_LigneCourant.getQte_Retourner()));

        Date date = null;
        String dateAAfficher = "";
        final DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            if (10 <= retour_LigneCourant.getPeremptionDate().length() && !retour_LigneCourant.getPeremptionDate().contentEquals("0000-00-00"))
            {
                date = dateDecodeur.parse(retour_LigneCourant.getPeremptionDate().substring(0, 10));
                final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                assert null != date;
                dateAAfficher = dateFormat.format(date);
            }
        }
        catch (final ParseException e) { e.printStackTrace(); }

        viewHolder.datePeremption.setText(dateAAfficher);
        viewHolder.setDatePeremptionColor(date);

        return convertView;
    }

    public class Retour_LigneViewHolder
    {
        public TextView designation = null;
        public TextView referenceProduit = null;
        public TextView fournisseur = null;
        public LinearLayout informationLot = null;
        public TextView lot = null;
        public TextView datePeremption = null;
        public TextView serie = null;
        public TextView labelSerie = null;
        public LinearLayout bandeauQteADetruire = null;
        TextView qteRetourner = null;

        public void setDatePeremptionColor(final Date date)
        {
            if (null != date)
            {

                final Date dateDuJour = new Date();
                final long diff = dateDuJour.getTime() - date.getTime();
                final int delai = (int) (diff / (long) (1000 * 60 * 60 * 24));

                final int delai30jours = -30;
                final int delai60jours = -60;

                if (delai30jours <= delai) { this.datePeremption.setTextColor(Retour_Ligne_DestructionAdapter.this.mContext.getResources().getColor(R.color.rouge2)); }
                else if (delai60jours <= delai) { this.datePeremption.setTextColor(Retour_Ligne_DestructionAdapter.this.mContext.getResources().getColor(R.color.orange2)); }
                else { this.datePeremption.setTextColor(Retour_Ligne_DestructionAdapter.this.mContext.getResources().getColor(R.color.vert)); }
            }
            else { this.datePeremption.setTextColor(Color.BLACK); }

        }
    }
}
