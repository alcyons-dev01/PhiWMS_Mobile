package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.R;

import static android.view.View.GONE;

public class Retour_Ligne_RetourPUIAdapter extends ArrayAdapter {

    public List<Retour_Ligne> retourLigne;
    public List<Retour_LigneViewHolder> viewHolderList;
    Retour retourCourant;
    Context context;
    SQLiteDatabase db;

    public Retour_Ligne_RetourPUIAdapter(Context context, SQLiteDatabase db, List<Retour_Ligne> retourLigne, Retour retourCourant) {
        super(context, 0, retourLigne);
        this.context = context;
        this.db = db;
        this.retourLigne = retourLigne;
        this.retourCourant = retourCourant;
        viewHolderList = new ArrayList<>();
        for (int i = 0; i < retourLigne.size(); i++) {
            Retour_LigneViewHolder viewHolder = new Retour_LigneViewHolder();
            viewHolderList.add(viewHolder);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Retour_LigneViewHolder viewHolder = viewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_retour_ligne_retour_pui, parent, false);


        viewHolder.designationProduit = (TextView) convertView.findViewById(R.id.designationProduit);
        viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
        viewHolder.nomFournisseur = (TextView) convertView.findViewById(R.id.nomFournisseur);
        viewHolder.qteRetourner = (TextView) convertView.findViewById(R.id.QteRetourner);
        viewHolder.QteARetourner = (TextView) convertView.findViewById(R.id.QteARetourner);
        viewHolder.lotRetourne = (TextView) convertView.findViewById(R.id.lotRetourne);
        viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
        viewHolder.numSerieProduit = (TextView) convertView.findViewById(R.id.numSerie);
        viewHolder.labelSerie = (TextView) convertView.findViewById(R.id.labelSerie);
        viewHolder.textEmplacement = (TextView) convertView.findViewById(R.id.textEmplacement);
        viewHolder.layoutSerie = (LinearLayout) convertView.findViewById(R.id.layoutSerie);
        viewHolder.layoutQteARetourner = (LinearLayout) convertView.findViewById(R.id.layoutQteARetourner);
        viewHolder.layoutPrincipal = (RelativeLayout) convertView.findViewById(R.id.layoutPrincipal);
        viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

        Retour_Ligne retourLigne = (Retour_Ligne) getItem(position);

        viewHolder.designationProduit.setText(retourLigne.getProduit_Designation());
        viewHolder.referenceProduit.setText(retourLigne.getProduit_Reference());
        viewHolder.nomFournisseur.setText(retourLigne.getProduit_Fournisseur());
        viewHolder.lotRetourne.setText(retourLigne.getLot_Retourner());
        viewHolder.numSerieProduit.setText(retourLigne.getSerie_Retourner());
        if(retourLigne.getSerie_Retourner().contentEquals(""))
        {
            viewHolder.labelSerie.setVisibility(View.GONE);
            viewHolder.numSerieProduit.setVisibility(View.GONE);
        }
        Date date = null;
        String dateAAfficher = "";
        DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (retourLigne.getPeremptionDate().length() >= 10) {
                date = dateDecodeur.parse(retourLigne.getPeremptionDate().substring(0, 10));
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dateAAfficher = dateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.datePeremption.setText(dateAAfficher);
        viewHolder.setDatePeremptionColor(date);


        int quantiteRetourner = 0;

        List<Retour_Ligne> retourLigneProduitCourant = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourCourant, retourLigne.getCode_produit());
        List<String> listEmplacement = new ArrayList<>();

        for(Retour_Ligne retour_ligne_temp : retourLigneProduitCourant)
        {
            if(!listEmplacement.contains(retour_ligne_temp.getRetourPUI_Emplacement()) && retour_ligne_temp.getQte_Retourner() > 0)
            {
                listEmplacement.add(retour_ligne_temp.getRetourPUI_Emplacement());
            }

            quantiteRetourner = (int) (quantiteRetourner + retour_ligne_temp.getQte_Retourner());
        }

        if(listEmplacement.size() == 1)
        {
            viewHolder.textEmplacement.setText(listEmplacement.get(0));
        }
        else
        {
            String nb_emplacement = listEmplacement.size()+" Emp.";
            viewHolder.textEmplacement.setText(nb_emplacement);
        }

        viewHolder.qteRetourner.setText(String.valueOf(quantiteRetourner));
        viewHolder.QteARetourner.setText(String.valueOf((int)retourLigne.getQte_avant_retour()));

        if(quantiteRetourner == retourLigne.getQte_avant_retour())
            viewHolder.layoutQteARetourner.setVisibility(GONE);

        return convertView;
    }

    public class Retour_LigneViewHolder {
        public TextView designationProduit;
        public TextView referenceProduit;
        public TextView nomFournisseur;
        public TextView qteRetourner;
        public TextView QteARetourner;
        public TextView lotRetourne;
        public TextView datePeremption;
        public TextView numSerieProduit;
        public TextView labelSerie;
        public TextView textEmplacement;
        public RelativeLayout layoutPrincipal;
        public ProgressBar progressBar;
        public LinearLayout layoutSerie;
        public LinearLayout layoutQteARetourner;

        public void setDatePeremptionColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.rouge2, null));
                } else if (delai >= delai60jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.orange2, null));
                } else {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.vert, null));
                }
            } else {
                datePeremption.setTextColor(Color.BLACK);
            }

        }
    }
}
