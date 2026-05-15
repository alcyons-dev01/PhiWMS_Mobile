package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.R;

public class Retour_Ligne_RetourFournisseurAdapter extends RecyclerView.Adapter<Retour_Ligne_RetourFournisseurAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Retour_Ligne item);
    }

    private final Context context;
    public final List<Retour_Ligne> mRetour_Lignes;

    public Retour_Ligne_RetourFournisseurAdapter(Context context, List<Retour_Ligne> liste) {
        this.context = context;
        this.mRetour_Lignes = liste;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView designation;
        public final TextView referenceProduit;
        public final TextView fournisseur;
        public final LinearLayout informationLot;
        public final TextView lot;
        public final TextView datePeremption;
        public final TextView serie;
        public final TextView labelSerie;
        public final LinearLayout bandeauQteARetournerFournisseur;
        public final TextView qteRetourner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            designation                  = itemView.findViewById(R.id.designationProduit);
            referenceProduit             = itemView.findViewById(R.id.referenceProduit);
            fournisseur                  = itemView.findViewById(R.id.nomFournisseur);
            informationLot               = itemView.findViewById(R.id.InformationLot_LL);
            lot                          = itemView.findViewById(R.id.lotRetourne);
            datePeremption               = itemView.findViewById(R.id.datePeremption);
            serie                        = itemView.findViewById(R.id.numSerie);
            labelSerie                   = itemView.findViewById(R.id.labelSerie);
            bandeauQteARetournerFournisseur = itemView.findViewById(R.id.bandeauQteARetournerFournisseur);
            qteRetourner                 = itemView.findViewById(R.id.QteRetourner);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_a_retourner_fournisseur, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Retour_Ligne item = mRetour_Lignes.get(position);

        holder.designation.setText(item.getProduit_Designation());
        holder.referenceProduit.setText(item.getProduit_Reference());
        holder.fournisseur.setText(item.getProduit_Fournisseur());
        holder.informationLot.setVisibility(View.VISIBLE);
        holder.lot.setText(item.getLot_Retourner());
        holder.serie.setText(item.getSerie_Retourner());
        holder.bandeauQteARetournerFournisseur.setVisibility(View.VISIBLE);
        holder.qteRetourner.setText(item.getQte_Retourner() == 0.0 ? "" : String.valueOf((int) item.getQte_Retourner()));

        // Gestion de la date de péremption avec couleur
        Date dateParsed = null;
        String dateAAfficher = "";

        try {
            String peremption = item.getPeremptionDate();
            if (peremption != null && peremption.length() >= 10 && !peremption.contentEquals("0000-00-00")) {
                SimpleDateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
                dateParsed = dateDecodeur.parse(peremption.substring(0, 10));
                if (dateParsed != null) {
                    dateAAfficher = new SimpleDateFormat("MM/yy").format(dateParsed);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.datePeremption.setText(dateAAfficher);
        holder.datePeremption.setTextColor(getCouleurPeremption(dateParsed));
    }

    @Override
    public int getItemCount() {
        return mRetour_Lignes.size();
    }

    private int getCouleurPeremption(Date date) {
        if (date == null) return Color.BLACK;

        long diff = (new Date().getTime() - date.getTime()) / (1000 * 60 * 60 * 24);

        if (diff >= -30) return context.getResources().getColor(R.color.rouge2);
        if (diff >= -60) return context.getResources().getColor(R.color.orange2);
        return context.getResources().getColor(R.color.vert);
    }
}