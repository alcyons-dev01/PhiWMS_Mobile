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
import fr.alcyons.phiwms_mobile.RetourPUI.RetourPUIQuantiteHelper;

public class Retour_Ligne_ControleRetoursAdapter_2025 extends ArrayAdapter {

    public List<Retour_Ligne> retour_Lignes;
    public List<Retour_LigneViewHolder> retourLigneViewHolderList;
    Context context;
    SQLiteDatabase db;
    Retour retourCourant;

    public Retour_Ligne_ControleRetoursAdapter_2025(Context context, List<Retour_Ligne> retour_Lignes, SQLiteDatabase db, Retour retouCourant) {
        super(context, 0, retour_Lignes);
        this.retour_Lignes = retour_Lignes;
        this.context = context;
        this.db = db;
        retourCourant = retouCourant;
        retourLigneViewHolderList = new ArrayList<>();
        for (int i = 0; i < retour_Lignes.size(); i++) {
            Retour_LigneViewHolder viewHolder = new Retour_LigneViewHolder();
            retourLigneViewHolderList.add(viewHolder);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Retour_LigneViewHolder viewHolder = retourLigneViewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_retour_ligne_controle_retours_new, parent, false);

        viewHolder.designation = (TextView) convertView.findViewById(R.id.designationProduit);
        viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
        viewHolder.fournisseur = (TextView) convertView.findViewById(R.id.nomFournisseur);
        viewHolder.qteRetourner = (TextView) convertView.findViewById(R.id.QteRetourner);
        viewHolder.qteAControler = (TextView) convertView.findViewById(R.id.QteARetourner);
        viewHolder.labelQteBandeau = (TextView) convertView.findViewById(R.id.labelQteBandeau);
        viewHolder.linearLayoutLotDatePeremption = (LinearLayout) convertView.findViewById(R.id.linearLayoutLotDatePeremption);
        viewHolder.labelLot = (TextView) convertView.findViewById(R.id.labelLot);
        viewHolder.lotRetourne = (TextView) convertView.findViewById(R.id.lotRetourne);
        viewHolder.labelDatePeremption = (TextView) convertView.findViewById(R.id.labelDatePeremption);
        viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
        viewHolder.numSerieProduit = (TextView) convertView.findViewById(R.id.numSerie);
        viewHolder.labelSerie = (TextView) convertView.findViewById(R.id.labelSerie);
        viewHolder.textEmplacement = (TextView) convertView.findViewById(R.id.textEmplacement);
        viewHolder.textZone = (TextView) convertView.findViewById(R.id.textZone);
        viewHolder.layoutSerie = (LinearLayout) convertView.findViewById(R.id.layoutSerie);
        viewHolder.layoutZoneEmplacement = (LinearLayout) convertView.findViewById(R.id.layoutZoneEmplacement);
        viewHolder.layoutQteRetour = (LinearLayout) convertView.findViewById(R.id.layoutQteRetour);
        viewHolder.layoutPrincipal = (RelativeLayout) convertView.findViewById(R.id.layoutPrincipal);
        viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
        viewHolder.bottomDivider = convertView.findViewById(R.id.bottomDivider);
        Retour_Ligne retourLigne = (Retour_Ligne) getItem(position);

        if(retourLigne != null)
        {
            viewHolder.designation.setText(retourLigne.getProduit_Designation());
            viewHolder.referenceProduit.setText(retourLigne.getProduit_Reference());
            viewHolder.fournisseur.setText(retourLigne.getProduit_Fournisseur());
            viewHolder.numSerieProduit.setText(retourLigne.getSerie_Retourner());

            if(retourLigne.getSerie_Retourner() == null || retourLigne.getSerie_Retourner().contentEquals(""))
            {
                viewHolder.labelSerie.setVisibility(View.GONE);
                viewHolder.numSerieProduit.setVisibility(View.GONE);
            }

            Date date = null;
            String dateAAfficher = "";
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                if (retourLigne.getPeremptionDate() != null && retourLigne.getPeremptionDate().length() >= 10)
                {
                    date = dateDecodeur.parse(retourLigne.getPeremptionDate().substring(0, 10));
                    DateFormat dateFormat = new SimpleDateFormat("MM/yy");
                    dateAAfficher = dateFormat.format(date);
                }
            }
            catch (ParseException e) {e.printStackTrace();}

            if(retourLigne.get_UID() < 0)
            {
                viewHolder.layoutZoneEmplacement.setVisibility(View.VISIBLE);
                viewHolder.textEmplacement.setText(retourLigne.getRetourPUI_Emplacement());
                viewHolder.textZone.setText(retourLigne.getRetourPUI_Zone());
                viewHolder.layoutQteRetour.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
                viewHolder.labelQteBandeau.setText("Quantité contrôlée");
                viewHolder.qteRetourner.setVisibility(View.VISIBLE);
                viewHolder.qteRetourner.setText(String.valueOf((int) retourLigne.getQte_Retourner()));
                viewHolder.qteAControler.setVisibility(View.GONE);
                viewHolder.bottomDivider.setVisibility(View.VISIBLE);

                viewHolder.lotRetourne.setText(RetourPUIQuantiteHelper.getDisplayedLot(retourLigne));
                viewHolder.datePeremption.setText(dateAAfficher);
                viewHolder.setDatePeremptionColor(date);
            }
            else
            {
                int qteRetourner = 0;
                List<Retour_Ligne> retourLigneRetourner = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourCourant, retourLigne.getCode_produit());
                for(Retour_Ligne temp : retourLigneRetourner) { qteRetourner += (int) temp.getQte_Retourner(); }

                viewHolder.layoutZoneEmplacement.setVisibility(View.GONE);
                viewHolder.layoutQteRetour.setBackgroundColor(context.getResources().getColor(R.color.rouge2, null));
                viewHolder.labelQteBandeau.setText("Quantité à contrôler");
                viewHolder.qteRetourner.setVisibility(View.GONE);
                viewHolder.qteAControler.setVisibility(View.VISIBLE);
                viewHolder.qteAControler.setText(String.valueOf(Math.max(0, (int) retourLigne.getQte_Demander() - qteRetourner)));
                viewHolder.bottomDivider.setVisibility(View.GONE);

                viewHolder.linearLayoutLotDatePeremption.setVisibility(View.GONE);
                viewHolder.labelLot.setVisibility(View.GONE);
                viewHolder.lotRetourne.setVisibility(View.GONE);
                viewHolder.labelDatePeremption.setVisibility(View.GONE);
                viewHolder.datePeremption.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public class Retour_LigneViewHolder {
        public TextView designation;
        public TextView referenceProduit;
        public TextView fournisseur;
        public TextView labelQteBandeau;
        public TextView qteRetourner;
        public TextView qteAControler;
        public LinearLayout linearLayoutLotDatePeremption;
        public TextView labelLot;
        public TextView lotRetourne;
        public TextView labelDatePeremption;
        public TextView datePeremption;
        public TextView numSerieProduit;
        public TextView labelSerie;
        public TextView textEmplacement;
        public TextView textZone;
        public RelativeLayout layoutPrincipal;
        public ProgressBar progressBar;
        public LinearLayout layoutSerie;
        public LinearLayout layoutZoneEmplacement;
        public LinearLayout layoutQteRetour;
        public View bottomDivider;

        public void setDatePeremptionColor(Date date)
        {
            if (date != null)
            {
                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) { datePeremption.setTextColor(context.getResources().getColor(R.color.rouge2, null)); }
                else if (delai >= delai60jours) { datePeremption.setTextColor(context.getResources().getColor(R.color.orange2, null));}
                else { datePeremption.setTextColor(context.getResources().getColor(R.color.noir, null));}
            }
            else { datePeremption.setTextColor(Color.BLACK); }
        }
    }
}
