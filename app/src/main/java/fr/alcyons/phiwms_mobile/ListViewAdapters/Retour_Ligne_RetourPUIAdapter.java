package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.RetourPUI.RetourPUIQuantiteHelper;

public class Retour_Ligne_RetourPUIAdapter extends RecyclerView.Adapter<Retour_Ligne_RetourPUIAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Retour_Ligne item);
    }

    private final Context context;
    private final SQLiteDatabase db;
    private final List<Retour_Ligne> mRetour_Lignes;
    private final Retour retourCourant;
    private final OnItemClickListener onItemClickListener;
    public boolean shouldShowQteARetourner;
    public boolean shouldAggregateByProduit;

    public Retour_Ligne_RetourPUIAdapter(Context context, SQLiteDatabase db, List<Retour_Ligne> liste, Retour retourCourant, boolean shouldShowQteARetourner, boolean shouldAggregateByProduit, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.db = db;
        this.mRetour_Lignes = liste;
        this.retourCourant = retourCourant;
        this.shouldShowQteARetourner = shouldShowQteARetourner;
        this.shouldAggregateByProduit = shouldAggregateByProduit;
        this.onItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView designationProduit;
        public final TextView referenceProduit;
        public final TextView nomFournisseur;
        public final TextView labelQteBandeau;
        public final TextView qteRetourner;
        public final TextView qteARetourner;
        public final TextView lotRetourne;
        public final TextView datePeremption;
        public final TextView numSerieProduit;
        public final TextView labelSerie;
        public final TextView textEmplacement;
        public final TextView textZone;
        public final RelativeLayout layoutPrincipal;
        public final ProgressBar progressBar;
        public final LinearLayout layoutSerie;
        public final LinearLayout layoutZoneEmplacement;
        public final LinearLayout layoutQteRetour;
        public final View bottomDivider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            designationProduit   = itemView.findViewById(R.id.designationProduit);
            referenceProduit     = itemView.findViewById(R.id.referenceProduit);
            nomFournisseur       = itemView.findViewById(R.id.nomFournisseur);
            labelQteBandeau      = itemView.findViewById(R.id.labelQteBandeau);
            qteRetourner         = itemView.findViewById(R.id.QteRetourner);
            qteARetourner        = itemView.findViewById(R.id.QteARetourner);
            lotRetourne          = itemView.findViewById(R.id.lotRetourne);
            datePeremption       = itemView.findViewById(R.id.datePeremption);
            numSerieProduit      = itemView.findViewById(R.id.numSerie);
            labelSerie           = itemView.findViewById(R.id.labelSerie);
            textEmplacement      = itemView.findViewById(R.id.textEmplacement);
            textZone             = itemView.findViewById(R.id.textZone);
            layoutPrincipal      = itemView.findViewById(R.id.layoutPrincipal);
            progressBar          = itemView.findViewById(R.id.progressBar);
            layoutSerie          = itemView.findViewById(R.id.layoutSerie);
            layoutZoneEmplacement = itemView.findViewById(R.id.layoutZoneEmplacement);
            layoutQteRetour      = itemView.findViewById(R.id.layoutQteRetour);
            bottomDivider        = itemView.findViewById(R.id.bottomDivider);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_retour_ligne_retour_pui, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Retour_Ligne item = mRetour_Lignes.get(position);

        holder.designationProduit.setText(item.getProduit_Designation());
        holder.referenceProduit.setText(item.getProduit_Reference());
        holder.nomFournisseur.setText(item.getProduit_Fournisseur());
        holder.lotRetourne.setText(RetourPUIQuantiteHelper.getDisplayedLot(item));
        holder.numSerieProduit.setText(item.getSerie_Retourner());

        // Gestion numéro de série
        if (item.getSerie_Retourner().contentEquals("")) {
            holder.labelSerie.setVisibility(View.GONE);
            holder.numSerieProduit.setVisibility(View.GONE);
        }

        // Gestion de la date de péremption
        Date dateParsed = null;
        String dateAAfficher = "";
        try {
            String peremption = item.getPeremptionDate();
            if (peremption != null && peremption.length() >= 10) {
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

        // Gestion quantité et emplacement
        int quantiteRetourner;
        if (item.get_UID() > 0) {
            List<Retour_Ligne> retourLignesNegatives = Retour_LigneOpenHelper.getAllRetourLignesNegByRetour(db, retourCourant);
            List<Retour_Ligne> retourLignesBaseCourante = RetourPUIQuantiteHelper.getNegativeLinesForBase(retourLignesNegatives, item);
            List<String> listEmplacement = new ArrayList<>();
            List<String> listZone = new ArrayList<>();

            quantiteRetourner = RetourPUIQuantiteHelper.getAllocatedQuantityForBase(retourLignesNegatives, item);

            for (Retour_Ligne retourLigneTemp : retourLignesBaseCourante) {
                if (retourLigneTemp.getQte_Retourner() <= 0) continue;
                if (!listEmplacement.contains(retourLigneTemp.getRetourPUI_Emplacement()))
                    listEmplacement.add(retourLigneTemp.getRetourPUI_Emplacement());
                if (!listZone.contains(retourLigneTemp.getRetourPUI_Zone()))
                    listZone.add(retourLigneTemp.getRetourPUI_Zone());
            }

            if (listEmplacement.size() == 1)       holder.textEmplacement.setText(listEmplacement.get(0));
            else if (listEmplacement.size() > 1)   holder.textEmplacement.setText(listEmplacement.size() + " Emp.");
            else                                   holder.textEmplacement.setText(item.getRetourPUI_Emplacement());

            if (listZone.size() == 1)              holder.textZone.setText(listZone.get(0));
            else if (listZone.size() > 1)          holder.textZone.setText(listZone.size() + " Zones");
            else                                   holder.textZone.setText(item.getRetourPUI_Zone());
        } else {
            quantiteRetourner = (int) item.getQte_Retourner();
            holder.textEmplacement.setText(item.getRetourPUI_Emplacement());
            holder.textZone.setText(item.getRetourPUI_Zone());
        }

        // Gestion affichage selon shouldShowQteARetourner
        if (!shouldShowQteARetourner) {
            holder.layoutZoneEmplacement.setVisibility(View.VISIBLE);
            holder.layoutQteRetour.setVisibility(View.VISIBLE);
            holder.layoutQteRetour.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
            holder.labelQteBandeau.setText("Quantité retournée");
            holder.qteRetourner.setVisibility(View.VISIBLE);
            holder.qteRetourner.setText(String.valueOf(quantiteRetourner));
            holder.qteARetourner.setVisibility(View.GONE);
            holder.bottomDivider.setVisibility(View.VISIBLE);
        } else {
            holder.layoutZoneEmplacement.setVisibility(View.GONE);
            holder.layoutQteRetour.setVisibility(View.VISIBLE);
            holder.layoutQteRetour.setBackgroundColor(context.getResources().getColor(R.color.rouge2, null));
            holder.labelQteBandeau.setText("Quantité à retourner");
            holder.qteRetourner.setVisibility(View.GONE);
            holder.qteARetourner.setVisibility(View.VISIBLE);
            int quantiteRestante = (int) item.getQte_avant_retour() - quantiteRetourner;
            holder.qteARetourner.setText(String.valueOf(Math.max(0, quantiteRestante)));
            holder.bottomDivider.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return mRetour_Lignes.size();
    }

    private int getCouleurPeremption(Date date) {
        if (date == null) return Color.BLACK;

        long diff = (new Date().getTime() - date.getTime()) / (1000 * 60 * 60 * 24);

        if (diff >= -30) return context.getResources().getColor(R.color.rouge2, null);
        if (diff >= -60) return context.getResources().getColor(R.color.orange2, null);
        return context.getResources().getColor(R.color.noir, null);
    }
}