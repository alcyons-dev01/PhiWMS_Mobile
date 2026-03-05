package fr.alcyons.phiwms_mobile.ListViewAdapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.ListeLotPreparationActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.Reception.ListeLotReceptionActivity;

public class LotReception_Adapter extends RecyclerView.Adapter<LotReception_Adapter.LotViewHolder> {

    public Context context;
    private List<PH_Reliquat> list_ph_reliquat;
    private LotAdapter.OnDeleteClickListener deleteClickListener;
    public boolean receptionComplete;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
    public int swipedPosition = -1;

    public LotReception_Adapter(List<PH_Reliquat> list_ph_reliquat, LotAdapter.OnDeleteClickListener listener, Context context) {
        this.list_ph_reliquat = list_ph_reliquat;
        this.deleteClickListener = listener;
        this.context = context;
        this.receptionComplete = false;
    }

    @NonNull
    @Override
    public LotReception_Adapter.LotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_lot_reception, parent, false);
        return new LotReception_Adapter.LotViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LotReception_Adapter.LotViewHolder viewHolder, int position) {
        PH_Reliquat reliquatCourant = (PH_Reliquat) list_ph_reliquat.get(position);

        if(reliquatCourant.getLot().contentEquals("row_ajouter"))
        {
            if(!receptionComplete)
            {
                viewHolder.layout_ajouter_lot.setVisibility(VISIBLE);
                viewHolder.layout_ajouter_lot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //((ListeLotReception2025_V2Activity) context).ajoutLotManuelReception();
                    }
                });
            }
        }
        else
        {
            //on cache tous les layout
            viewHolder.layout_ajouter_lot.setVisibility(GONE);
            viewHolder.layoutPrincipal.setVisibility(GONE);

            if (reliquatCourant != null) {
                if(!reliquatCourant.getSerie().contentEquals("") && reliquatCourant.getSerie() != null)
                {
                    viewHolder.zoneSerie.setVisibility(VISIBLE);
                    viewHolder.serie.setText(reliquatCourant.getSerie());
                }
                else
                {
                    viewHolder.zoneSerie.setVisibility(GONE);
                }
                viewHolder.layoutPrincipal.setVisibility(VISIBLE);
                viewHolder.nomEmplacement.setText(reliquatCourant.getEmplacement());
                viewHolder.lot.setText(reliquatCourant.getLot());
                viewHolder.qteSaisie.setText(String.valueOf(reliquatCourant.getQteLivraison()));

                viewHolder.qteSaisie.setTag(viewHolder);

                viewHolder.qteSaisie.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (hasFocus) {
                            EditText editText = (EditText) v;

                            // sélectionne tout le texte
                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    editText.selectAll();
                                }
                            });
                        }
                    }
                });

                viewHolder.qteSaisie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            ((ListeLotReceptionActivity) context).fermerClavierEtRelayout();
                            if(!viewHolder.qteSaisie.getText().toString().contentEquals(""))
                            {
                                // 1) enlever le focus d'abord
                                textView.clearFocus();
                                View root = viewHolder.itemView.getRootView();
                                root.requestFocus();

                                // 2) fermer le clavier
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                                int pos = viewHolder.getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    ((ListeLotReceptionActivity) context).validerQuantiteReception(position, reliquatCourant, Integer.parseInt(viewHolder.qteSaisie.getText().toString()));
                                }
                            }

                            return true;
                        }

                        return false;
                    }
                });

                /*viewHolder.qteSaisie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ListeLotReceptionActivity) context).ClickNumberPicker(viewHolder.getAdapterPosition(), reliquatCourant);
                    }
                });*/

                String datePeremption = reliquatCourant.getPeremptionDate();
                String[] datePeremptionTab = datePeremption.split("-");
                if(datePeremptionTab.length == 3)
                    datePeremption = datePeremptionTab[2] + "/" + datePeremptionTab[1] + "/" + datePeremptionTab[0];
                viewHolder.datePeremption.setText(datePeremption);
                //viewHolder.setDatePeremptionColor(peremption_Date);


                if(Integer.parseInt(viewHolder.qteSaisie.getText().toString()) == 0)
                {
                    viewHolder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_orange));
                    viewHolder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_orange));
                    viewHolder.layout_emplacement_lot.setBackground(context.getResources().getDrawable(R.drawable.background_plein_orange_radius));
                }
                else
                {
                    viewHolder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_vert));
                    viewHolder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_vert));
                    viewHolder.layout_emplacement_lot.setBackground(context.getResources().getDrawable(R.drawable.background_plein_vert_radius));
                }
            }
            viewHolder.btnDelete.setOnClickListener(v -> {
                deleteClickListener.onDeleteClick(viewHolder.getAdapterPosition());

                viewHolder.contentLayout.animate()
                        .translationX(0f)
                        .setDuration(200)
                        .start();

                // Réinitialiser les états swipe
                viewHolder.isSwipedOpen = false;
                viewHolder.btnDelete.setClickable(false);
                swipedPosition = -1;
            });

            if (reliquatCourant.getQteLivraison() == 0) {
                viewHolder.layoutPrincipal.setClickable(true);
                viewHolder.layoutPrincipal.setOnClickListener(v -> {
                    ((ListeLotPreparationActivity) context).ClickLigneLot(position);
                });
                viewHolder.contentLayout.setOnClickListener(null);

            } else {
                viewHolder.layoutPrincipal.setOnClickListener(null); // désactive le clic
                viewHolder.layoutPrincipal.setClickable(false);
                viewHolder.contentLayout.setOnClickListener(v -> {
                    if (viewHolder.contentLayout.getTranslationX() > 0f) {
                        // Recentrer la ligne
                        viewHolder.contentLayout.animate().translationX(0f).setDuration(200).start();
                        viewHolder.btnDelete.setClickable(false);
                        viewHolder.isSwipedOpen = false;
                        swipedPosition = -1;
                    }
                });
            }
        }

        if (position == swipedPosition) {
            viewHolder.contentLayout.setTranslationX(200f);
            viewHolder.btnDelete.setClickable(true);
            viewHolder.isSwipedOpen = true;
        } else {
            viewHolder.contentLayout.setTranslationX(0);
            viewHolder.btnDelete.setClickable(false);
            viewHolder.isSwipedOpen = false;
        }
        viewHolder.enableSwipeIfQteNotZero();
    }

    @Override
    public int getItemCount() {
        return list_ph_reliquat.size();
    }

    public void resetItem(int position) {
        notifyItemChanged(position);
    }

    public class LotViewHolder extends RecyclerView.ViewHolder {
        public TextView nomEmplacement;
        public TextView lot;
        public TextView serie;
        public TextView datePeremption;
        public TextView qteSaisie;
        public LinearLayout layout_ajouter_lot;
        public LinearLayout layoutPrincipal;
        public LinearLayout layout_qte_saisie_lot_preparation;
        public LinearLayout layout_emplacement_lot;
        public LinearLayout deleteLayout;
        public LinearLayout zoneSerie;
        public ImageView separateur;
        public ImageView btnDelete;
        public RelativeLayout contentLayout;
        public boolean isSwipedOpen = false;

        private float downX;
        private final int SWIPE_THRESHOLD = 100;
        private final LotReception_Adapter adapter;

        public LotViewHolder(@NonNull View itemView, LotReception_Adapter adapter) {
            super(itemView);
            this.adapter = adapter;

            btnDelete = itemView.findViewById(R.id.btn_delete);
            contentLayout = itemView.findViewById(R.id.contentLayout);
            nomEmplacement = itemView.findViewById(R.id.nomEmplacement);
            lot = itemView.findViewById(R.id.lot);
            serie = itemView.findViewById(R.id.numeroserie);
            datePeremption = itemView.findViewById(R.id.datePeremption);
            qteSaisie = itemView.findViewById(R.id.qteSaisie);
            layoutPrincipal = itemView.findViewById(R.id.layoutPrincipal);
            layout_qte_saisie_lot_preparation = itemView.findViewById(R.id.layout_qte_saisie_lot_preparation);
            layout_ajouter_lot = itemView.findViewById(R.id.layout_ajouter_lot);
            layout_emplacement_lot = itemView.findViewById(R.id.layout_emplacement_lot);
            deleteLayout = itemView.findViewById(R.id.deleteLayout);
            separateur = itemView.findViewById(R.id.separateur);
            zoneSerie = itemView.findViewById(R.id.zoneSerie);
        }

        public void enableSwipeIfQteNotZero() {
            contentLayout.setOnTouchListener(null); // désactive tout swipe par défaut

            PH_Reliquat courant = adapter.getReliquatAt(getAdapterPosition());
            if(courant != null && !courant.getLot().contentEquals("row_ajouter"))
            {
                int qte = courant.getQteLivraison();

                if (qte > 0) {
                    setupSwipeTouch();
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setupSwipeTouch() {
            contentLayout.setClickable(true);
            contentLayout.setFocusable(true);
            contentLayout.setOnTouchListener(new View.OnTouchListener() {
                float downX = 0f;
                float downY = 0f;
                boolean isSwiping = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = event.getX();
                            downY = event.getY();
                            isSwiping = false;
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return false; // laisser passer pour maintenant

                        case MotionEvent.ACTION_MOVE:
                            float deltaX = event.getX() - downX;
                            float deltaY = event.getY() - downY;

                            if (!isSwiping && Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 20) {
                                isSwiping = true;
                                v.getParent().requestDisallowInterceptTouchEvent(true); // bloquer le scroll
                            }

                            if (isSwiping) {
                                if (deltaX >= 0 && deltaX <= 200) {
                                    contentLayout.setTranslationX(deltaX);
                                }
                                return true;
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            if (isSwiping) {
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
                            break;
                    }
                    return false; // laisser le RecyclerView gérer les scrolls
                }
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

    public PH_Reliquat getReliquatAt(int position) {
        return list_ph_reliquat.get(position);
    }
}
