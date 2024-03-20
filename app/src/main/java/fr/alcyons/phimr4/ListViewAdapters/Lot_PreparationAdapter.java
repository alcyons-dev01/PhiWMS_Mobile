package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.graphics.Color;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phimr4.PreparationPUFetPAD.ListeLotPreparationActivity;
import fr.alcyons.phimr4.R;


/**
 * Created by olivier on 28/09/2017.
 */

public class Lot_PreparationAdapter extends ArrayAdapter {

    public List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotPreparationsAdaptes;
    public List<Lot_PreparationAdapterViewHolder> viewHolders;
    public PH_Preparation_Ligne_Preparation_Adapte ph_preparation_ligne_preparation_adapte;
    Context context;
    public boolean full;

    public int quantiteAPreparer;

    public Lot_PreparationAdapter(Context context, List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotPreparationsAdaptes, PH_Preparation_Ligne_Preparation_Adapte ph_preparation_ligne_preparation_adapte, int quantiteAPreparer) {

        super(context, 0, lotPreparationsAdaptes);
        this.lotPreparationsAdaptes = lotPreparationsAdaptes;
        this.context = context;

        this.quantiteAPreparer = quantiteAPreparer;

        if(this.quantiteAPreparer == 0)
            full = true;

        viewHolders = new ArrayList<>();
        this.ph_preparation_ligne_preparation_adapte = ph_preparation_ligne_preparation_adapte;
        for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte : lotPreparationsAdaptes
                ) {
            viewHolders.add(new Lot_PreparationAdapterViewHolder());
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_lot_preparation, parent, false);
        }

        Lot_PreparationAdapterViewHolder viewHolder = (Lot_PreparationAdapterViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = viewHolders.get(position);
            viewHolder.nomEmplacement = (TextView) convertView.findViewById(R.id.nomEmplacement);
            viewHolder.lot = (TextView) convertView.findViewById(R.id.lot);
            viewHolder.dateExpiration = (TextView) convertView.findViewById(R.id.datePeremption);
            viewHolder.qteSaisie = (TextView) convertView.findViewById(R.id.qteSaisie);
            viewHolder.labelLot = (TextView) convertView.findViewById(R.id.labelLot);
            viewHolder.qteStock = (TextView) convertView.findViewById(R.id.qteStock);
            viewHolder.layoutPrincipal = (LinearLayout) convertView.findViewById(R.id.layoutPrincipal);
            viewHolder.layout_qte_saisie_lot_preparation = (LinearLayout) convertView.findViewById(R.id.layout_qte_saisie_lot_preparation);
            viewHolder.layout_ajouter_lot = (LinearLayout) convertView.findViewById(R.id.layout_ajouter_lot);
            viewHolder.layout_annuler_lot = (LinearLayout) convertView.findViewById(R.id.layout_annuler_lot);
            viewHolder.layoutStock = (LinearLayout) convertView.findViewById(R.id.layoutStock);
            viewHolder.separateur = (ImageView) convertView.findViewById(R.id.separateur);
        }

        PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte = (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte) getItem(position);

        viewHolder.layout_ajouter_lot.setVisibility(View.GONE);
        viewHolder.layout_annuler_lot.setVisibility(View.GONE);
        viewHolder.layoutPrincipal.setVisibility(View.GONE);

        if (lotAdapte != null) {
            if(lotAdapte.getNumLot().contentEquals("row_ajouter"))
            {
                if(!full)
                    viewHolder.layout_ajouter_lot.setVisibility(View.VISIBLE);

            }
            else if(lotAdapte.getNumLot().contentEquals("row_annuler"))
            {
                if(full)
                    viewHolder.layout_annuler_lot.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.layoutPrincipal.setVisibility(View.VISIBLE);
                //gestion de la taille de l'emplacement
                String emplacement = lotAdapte.getEmplacement();
                if(emplacement.length() < 26)
                {
                    viewHolder.nomEmplacement.setText(emplacement);
                }
                else if(emplacement.length() < 41)
                {
                    viewHolder.nomEmplacement.setText(emplacement);
                    viewHolder.nomEmplacement.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
                }
                else
                {
                    emplacement = emplacement.substring(0, 23)+"..."+emplacement.substring(emplacement.length()-23, emplacement.length()-1);
                    viewHolder.nomEmplacement.setText(emplacement);
                    viewHolder.nomEmplacement.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
                }

                viewHolder.lot.setText(lotAdapte.getNumLot());
                viewHolder.qteSaisie.setText(String.valueOf(lotAdapte.getQteSaisie()));
                viewHolder.qteStock.setText(String.valueOf(lotAdapte.getQteStock()));

                viewHolder.qteSaisie.setOnClickListener(new View.OnClickListener() {
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
                        dateExp = dateDecodeur.parse(lotAdapte.getDatePeremption().substring(0, 10));
                        viewHolder.dateExpiration.setText(dateFormat.format(dateExp));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                viewHolder.setDatePeremptionColor(dateExp);

                if(Integer.parseInt(viewHolder.qteSaisie.getText().toString()) == 0)
                {
                    if(full)
                    {
                        viewHolder.layoutPrincipal.setVisibility(View.GONE);
                    }
                    viewHolder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne));
                    viewHolder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                    viewHolder.layoutStock.setVisibility(View.VISIBLE);
                    viewHolder.separateur.setBackgroundColor(context.getResources().getColor(R.color.orange, null));
                }
                else
                {
                    viewHolder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne_valider));
                    viewHolder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
                    viewHolder.separateur.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
                    viewHolder.layoutStock.setVisibility(View.INVISIBLE);
                }
            }
        }


        return convertView;
    }

    @Override
    public void clear() {
        lotPreparationsAdaptes.clear();
    }

    public class Lot_PreparationAdapterViewHolder {
        public TextView nomEmplacement;
        public TextView lot;
        public TextView dateExpiration;
        public TextView qteSaisie;
        public TextView labelLot;
        public TextView qteStock;
        public LinearLayout layoutPrincipal;
        public LinearLayout layout_qte_saisie_lot_preparation;
        //public LinearLayout layout_emplacement_lot;
        public LinearLayout layout_ajouter_lot;
        public LinearLayout layout_annuler_lot;
        public LinearLayout layoutStock;
        public ImageView separateur;

        public void setDatePeremptionColor(Date date) {

            if (date != null) {

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
            }
        }
    }
}