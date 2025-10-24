package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.ListeLotPreparationActivity;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stock_lot_selection, parent, false);
        return new LotViewHolder(view, this);
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
            if(!lotAdapte.getNumSerie().contentEquals(""))
            {
                holder.zoneSerie.setVisibility(View.VISIBLE);
                holder.serie.setText(lotAdapte.getNumSerie());
            }
            holder.qteSaisie.setText(String.valueOf(lotAdapte.getQteSaisie()));
            holder.qteStock.setText(String.valueOf(lotAdapte.getQteStock()));

            holder.qteSaisie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListeLotPreparationActivity) context).ClickNumberPicker(position);
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
                holder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_orange));
                holder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_orange));
                holder.layoutStock.setVisibility(View.VISIBLE);
                holder.separateur.setBackgroundColor(context.getResources().getColor(R.color.orange, null));
            }
            else
            {
                holder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_vert));
                holder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_vert));
                holder.separateur.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
                holder.layoutStock.setVisibility(View.INVISIBLE);
            }

            holder.btnDelete.setOnClickListener(v -> {
                deleteClickListener.onDeleteClick(holder.getAdapterPosition());

                holder.contentLayout.animate()
                        .translationX(0f)
                        .setDuration(200)
                        .start();

                // Réinitialiser les états swipe
                holder.isSwipedOpen = false;
                holder.btnDelete.setClickable(false);
                swipedPosition = -1;
            });

            /*holder.layoutPrincipal.setOnClickListener(v -> {
                if (holder.contentLayout.getTranslationX() >= 100f) {
                    // Ligne swipée → recentrage
                    holder.contentLayout.animate().translationX(0).setDuration(200).start();
                } else {
                    // Action normale
                    if (lotAdapte.getQteSaisie() == 0) {
                        ((ListeLotPreparation2025Activity) context).ClickLigneLot(position);
                    }
                }
            });*/

            if (lotAdapte.getQteSaisie() == 0) {
                holder.layoutPrincipal.setClickable(true);
                holder.layoutPrincipal.setOnClickListener(v -> {
                    ((ListeLotPreparationActivity) context).ClickLigneLot(position);
                });
                holder.contentLayout.setOnClickListener(null);

            } else {
                holder.layoutPrincipal.setOnClickListener(null); // désactive le clic
                holder.layoutPrincipal.setClickable(false);
                holder.contentLayout.setOnClickListener(v -> {
                    if (holder.contentLayout.getTranslationX() > 0f) {
                        // Recentrer la ligne
                        holder.contentLayout.animate().translationX(0f).setDuration(200).start();
                        holder.btnDelete.setClickable(false);
                        holder.isSwipedOpen = false;
                        swipedPosition = -1;
                    }
                });
            }
        }

        if (position == swipedPosition) {
            holder.contentLayout.setTranslationX(200f);
            holder.btnDelete.setClickable(true);
            holder.isSwipedOpen = true;
        } else {
            holder.contentLayout.setTranslationX(0);
            holder.btnDelete.setClickable(false);
            holder.isSwipedOpen = false;
        }
        holder.enableSwipeIfQteNotZero();
    }

    @Override
    public int getItemCount() {
        return lots.size();
    }

    public void resetItem(int position) {
        notifyItemChanged(position);
    }

    public class LotViewHolder extends RecyclerView.ViewHolder {
        public TextView nomEmplacement;
        public TextView lot;
        public TextView dateExpiration;
        public TextView qteSaisie;
        public TextView labelLot;
        public TextView qteStock;
        public TextView serie;
        public LinearLayout layoutPrincipal;
        public LinearLayout layout_qte_saisie_lot_preparation;
        public LinearLayout layout_ajouter_lot;
        public LinearLayout layout_annuler_lot;
        public LinearLayout layoutStock;
        public LinearLayout deleteLayout;
        public LinearLayout zoneSerie;
        public ImageView separateur;
        public ImageView btnDelete;
        public RelativeLayout contentLayout;
        public boolean isSwipedOpen = false;

        private float downX;
        private final int SWIPE_THRESHOLD = 100;
        private final LotAdapter adapter;

        public LotViewHolder(@NonNull View itemView, LotAdapter adapter) {
            super(itemView);
            this.adapter = adapter;

            btnDelete = itemView.findViewById(R.id.btn_delete);
            contentLayout = itemView.findViewById(R.id.contentLayout);
            nomEmplacement = itemView.findViewById(R.id.nomEmplacement);
            lot = itemView.findViewById(R.id.lot);
            dateExpiration = itemView.findViewById(R.id.datePeremption);
            qteSaisie = itemView.findViewById(R.id.qteSaisie);
            labelLot = itemView.findViewById(R.id.labelLot);
            qteStock = itemView.findViewById(R.id.qteStock);
            layoutPrincipal = itemView.findViewById(R.id.layoutPrincipal);
            layout_qte_saisie_lot_preparation = itemView.findViewById(R.id.layout_qte_saisie_lot_preparation);
            layout_ajouter_lot = itemView.findViewById(R.id.layout_ajouter_lot);
            layout_annuler_lot = itemView.findViewById(R.id.layout_annuler_lot);
            deleteLayout = itemView.findViewById(R.id.deleteLayout);
            layoutStock = itemView.findViewById(R.id.layoutStock);
            separateur = itemView.findViewById(R.id.separateur);
            zoneSerie = itemView.findViewById(R.id.zoneSerie);
            serie = itemView.findViewById(R.id.serie);
        }

        public void enableSwipeIfQteNotZero() {
            contentLayout.setOnTouchListener(null); // désactive tout swipe par défaut

            PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant = adapter.getLotAt(getAdapterPosition());
            int qte = courant.getQteSaisie();

            if (qte > 0) {
                setupSwipeTouch();
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setupSwipeTouch() {
            contentLayout.setClickable(true);
            contentLayout.setFocusable(true);
            contentLayout.setOnTouchListener((v, event) -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getX() - downX;
                        if (deltaX >= 0 && deltaX <= 200) {
                            contentLayout.setTranslationX(deltaX);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        float totalDeltaX = event.getX() - downX;
                        if (totalDeltaX > SWIPE_THRESHOLD) {
                            contentLayout.animate().translationX(200f).setDuration(200).start();
                            isSwipedOpen = true;
                            btnDelete.setClickable(true);
                            adapter.swipedPosition = getAdapterPosition();
                            adapter.notifyDataSetChanged();
                        } else {
                            contentLayout.animate().translationX(0).setDuration(200).start();
                            isSwipedOpen = false;
                            btnDelete.setClickable(false);
                        }
                        return true;
                }
                return false;
            });
        }

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
    }

    public PH_Preparation_Ligne_Preparation_Adapte.LotAdapte getLotAt(int position) {
        return lots.get(position);
    }
}

