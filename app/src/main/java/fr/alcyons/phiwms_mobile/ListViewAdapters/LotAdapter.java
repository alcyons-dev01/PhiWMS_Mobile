package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.ListeLotPreparation2025Activity;
import fr.alcyons.phiwms_mobile.R;

public class LotAdapter extends RecyclerView.Adapter<LotAdapter.LotViewHolder> {

    public Context context;
    private List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lots;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
    public int swipedPosition = -1;

    public LotAdapter(List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lots, OnDeleteClickListener listener, Context context) {
        this.lots = lots;
        this.deleteClickListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public LotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_lot_preparation, parent, false);
        return new LotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LotViewHolder holder, int position) {
        PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte = (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte) lots.get(position);

        if(lotAdapte.getNumLot().contentEquals("row_ajouter"))
        {

        }
        else
        {
            holder.layoutPrincipal.setVisibility(View.VISIBLE);
            //gestion de la taille de l'emplacement
            String emplacement = lotAdapte.getEmplacement();
            if(emplacement.length() < 26)
            {
                holder.nomEmplacement.setText(emplacement);
            }
            else if(emplacement.length() < 41)
            {
                holder.nomEmplacement.setText(emplacement);
                holder.nomEmplacement.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            }
            else
            {
                emplacement = emplacement.substring(0, 23)+"..."+emplacement.substring(emplacement.length()-23, emplacement.length()-1);
                holder.nomEmplacement.setText(emplacement);
                holder.nomEmplacement.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            }

            holder.lot.setText(lotAdapte.getNumLot());
            holder.qteSaisie.setText(String.valueOf(lotAdapte.getQteSaisie()));
            holder.qteStock.setText(String.valueOf(lotAdapte.getQteStock()));

            holder.qteSaisie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListeLotPreparation2025Activity) context).ClickNumberPicker(position);
                }
            });

            Date dateExp = null;
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String datePeremption = lotAdapte.getDatePeremption();

            if (datePeremption != null && !datePeremption.contentEquals("")) {
                try {
                    if(lotAdapte.getDatePeremption().length() == 10)
                    {
                        dateExp = dateDecodeur.parse(lotAdapte.getDatePeremption().substring(0, 10));
                        holder.dateExpiration.setText(dateFormat.format(dateExp));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            holder.setDatePeremptionColor(dateExp);

            if(Integer.parseInt(holder.qteSaisie.getText().toString()) == 0)
            {
                holder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne));
                holder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                holder.layoutStock.setVisibility(View.VISIBLE);
                holder.separateur.setBackgroundColor(context.getResources().getColor(R.color.orange, null));
            }
            else
            {
                holder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne_valider));
                holder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
                holder.separateur.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
                holder.layoutStock.setVisibility(View.INVISIBLE);
            }

            holder.btnDelete.setOnClickListener(v -> {
                Log.d("DELETE", "CLICK reçu ! position = " + holder.getAdapterPosition());
                Toast.makeText(context, "Delete click position " + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();

                // 1. Action personnalisée
                deleteClickListener.onDeleteClick(holder.getAdapterPosition());

                // 2. Animation fluide pour recentrer la ligne
                holder.contentLayout.animate()
                        .translationX(0)
                        .setDuration(200)
                        .start();
                holder.isSwipedOpen = false;
            });

            holder.layoutPrincipal.setOnClickListener(v -> {
                if (holder.contentLayout.getTranslationX() >= 100f) {
                    // Ligne swipée → recentrage
                    holder.contentLayout.animate().translationX(0).setDuration(200).start();
                } else {
                    // Action normale
                    if (lotAdapte.getQteSaisie() == 0) {
                        ((ListeLotPreparation2025Activity) context).ClickLigneLot(position);
                    }
                }
            });
        }

        if (holder.isSwipedOpen) {
            holder.contentLayout.setTranslationX(150f);
            holder.contentLayout.setClickable(false);
            holder.btnDelete.setClickable(true);
            holder.isSwipedOpen = false;
        } else {
            holder.contentLayout.setTranslationX(0);
            holder.contentLayout.setClickable(true);
            holder.btnDelete.setClickable(false);
            holder.isSwipedOpen = true;
        }
    }

    @Override
    public int getItemCount() {
        return lots.size();
    }

    public void resetItem(int position) {
        notifyItemChanged(position);
    }

    public static class LotViewHolder extends RecyclerView.ViewHolder {
        public TextView nomEmplacement;
        public TextView lot;
        public TextView dateExpiration;
        public TextView qteSaisie;
        public TextView labelLot;
        public TextView qteStock;
        public LinearLayout layoutPrincipal;
        public LinearLayout layout_qte_saisie_lot_preparation;
        public LinearLayout layout_ajouter_lot;
        public LinearLayout layout_annuler_lot;
        public LinearLayout layoutStock;
        public LinearLayout deleteLayout;
        public ImageView separateur;
        public ImageView btnDelete;
        public RelativeLayout contentLayout;
        public boolean isSwipedOpen = false;
        public void setDatePeremptionColor(Date date) {

            /*if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    dateExpiration.setTextColor(context.getResources().getColor(R.color.rouge2));
                } else if (delai >= delai60jours) {
                    dateExpiration.setTextColor(context.getResources().getColor(R.color.orange2));
                } else {
                    dateExpiration.setTextColor(context.getResources().getColor(R.color.vert));
                }
            } else {
                dateExpiration.setTextColor(Color.BLACK);
            }*/
        }
        public LotViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            contentLayout = itemView.findViewById(R.id.contentLayout);
            nomEmplacement = (TextView) itemView.findViewById(R.id.nomEmplacement);
            lot = (TextView) itemView.findViewById(R.id.lot);
            dateExpiration = (TextView) itemView.findViewById(R.id.datePeremption);
            qteSaisie = (TextView) itemView.findViewById(R.id.qteSaisie);
            labelLot = (TextView) itemView.findViewById(R.id.labelLot);
            qteStock = (TextView) itemView.findViewById(R.id.qteStock);
            layoutPrincipal = (LinearLayout) itemView.findViewById(R.id.layoutPrincipal);
            layout_qte_saisie_lot_preparation = (LinearLayout) itemView.findViewById(R.id.layout_qte_saisie_lot_preparation);
            layout_ajouter_lot = (LinearLayout) itemView.findViewById(R.id.layout_ajouter_lot);
            layout_annuler_lot = (LinearLayout) itemView.findViewById(R.id.layout_annuler_lot);
            deleteLayout = (LinearLayout) itemView.findViewById(R.id.deleteLayout);
            layoutStock = (LinearLayout) itemView.findViewById(R.id.layoutStock);
            separateur = (ImageView) itemView.findViewById(R.id.separateur);
        }
    }
}

