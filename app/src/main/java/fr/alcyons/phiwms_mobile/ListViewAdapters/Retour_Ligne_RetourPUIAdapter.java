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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Retour_Ligne_RetourPUIAdapter extends ArrayAdapter {

    public List<Retour_Ligne> retourLigne;
    public List<Retour_LigneViewHolder> viewHolderList;
    Retour retourCourant;
    Context context;
    SQLiteDatabase db;
    public boolean shouldShowQteARetourner = false;
    public boolean shouldAggregateByProduit = true;

    public Retour_Ligne_RetourPUIAdapter(Context context, SQLiteDatabase db, List<Retour_Ligne> retourLigne, Retour retourCourant, final boolean shouldShowQteARetourner, final boolean shouldAggregateByProduit) {
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
        this.shouldShowQteARetourner = shouldShowQteARetourner;
        this.shouldAggregateByProduit = shouldAggregateByProduit;
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
        viewHolder.labelQteBandeau = (TextView) convertView.findViewById(R.id.labelQteBandeau);
        viewHolder.lotRetourne = (TextView) convertView.findViewById(R.id.lotRetourne);
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
        viewHolder.bottomDivider = (View) convertView.findViewById(R.id.bottomDivider);

        Retour_Ligne retourLigne = (Retour_Ligne) getItem(position);

        viewHolder.designationProduit.setText(retourLigne.getProduit_Designation());
        viewHolder.referenceProduit.setText(retourLigne.getProduit_Reference());
        viewHolder.nomFournisseur.setText(retourLigne.getProduit_Fournisseur());
        viewHolder.lotRetourne.setText(RetourPUIQuantiteHelper.getDisplayedLot(retourLigne));
        viewHolder.numSerieProduit.setText(retourLigne.getSerie_Retourner());
        if(retourLigne.getSerie_Retourner().contentEquals(""))
        {
            viewHolder.labelSerie.setVisibility(View.GONE);
            viewHolder.numSerieProduit.setVisibility(View.GONE);
        }
        Date date = null;
        String dateAAfficher = "";
        DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            if (retourLigne.getPeremptionDate().length() >= 10)
            {
                date = dateDecodeur.parse(retourLigne.getPeremptionDate().substring(0, 10));
                DateFormat dateFormat = new SimpleDateFormat("MM/yy");
                dateAAfficher = dateFormat.format(date);
            }
        }
        catch (ParseException e) {e.printStackTrace();}

        viewHolder.datePeremption.setText(dateAAfficher);
        viewHolder.setDatePeremptionColor(date);
        int quantiteRetourner;
        if(retourLigne.get_UID() > 0)
        {
            List<Retour_Ligne> retourLignesNegatives = Retour_LigneOpenHelper.getAllRetourLignesNegByRetour(db, retourCourant);
            List<Retour_Ligne> retourLignesBaseCourante = RetourPUIQuantiteHelper.getNegativeLinesForBase(retourLignesNegatives, retourLigne);
            List<String> listEmplacement = new ArrayList<>();
            List<String> listZone = new ArrayList<>();

            quantiteRetourner = RetourPUIQuantiteHelper.getAllocatedQuantityForBase(retourLignesNegatives, retourLigne);

            for(Retour_Ligne retourLigneTemp : retourLignesBaseCourante)
            {
                if(retourLigneTemp.getQte_Retourner() <= 0) {continue;}
                if(!listEmplacement.contains(retourLigneTemp.getRetourPUI_Emplacement())) {listEmplacement.add(retourLigneTemp.getRetourPUI_Emplacement());}
                if(!listZone.contains(retourLigneTemp.getRetourPUI_Zone())) {listZone.add(retourLigneTemp.getRetourPUI_Zone());}
            }

            if(listEmplacement.size() == 1) {viewHolder.textEmplacement.setText(listEmplacement.get(0));}
            else if(listEmplacement.size() > 1) {viewHolder.textEmplacement.setText(listEmplacement.size() + " Emp.");}
            else {viewHolder.textEmplacement.setText(retourLigne.getRetourPUI_Emplacement());}

            if(listZone.size() == 1) {viewHolder.textZone.setText(listZone.get(0));}
            else if(listZone.size() > 1) {viewHolder.textZone.setText(listZone.size() + " Zones");}
            else {viewHolder.textZone.setText(retourLigne.getRetourPUI_Zone());}
        }
        else
        {
            quantiteRetourner = (int) retourLigne.getQte_Retourner();
            viewHolder.textEmplacement.setText(retourLigne.getRetourPUI_Emplacement());
            viewHolder.textZone.setText(retourLigne.getRetourPUI_Zone());
        }

        if(this.shouldShowQteARetourner)
        {
            viewHolder.layoutZoneEmplacement.setVisibility(VISIBLE);
            viewHolder.layoutQteRetour.setVisibility(VISIBLE);
            viewHolder.layoutQteRetour.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
            viewHolder.labelQteBandeau.setText("Quantité retournée");
            viewHolder.qteRetourner.setVisibility(VISIBLE);
            viewHolder.qteRetourner.setText(String.valueOf(quantiteRetourner));
            viewHolder.QteARetourner.setVisibility(GONE);
            viewHolder.bottomDivider.setVisibility(VISIBLE);
        }
        else
        {
            viewHolder.layoutZoneEmplacement.setVisibility(GONE);
            viewHolder.layoutQteRetour.setVisibility(VISIBLE);
            viewHolder.layoutQteRetour.setBackgroundColor(context.getResources().getColor(R.color.rouge2, null));
            viewHolder.labelQteBandeau.setText("Quantité à retourner");
            viewHolder.qteRetourner.setVisibility(GONE);
            viewHolder.QteARetourner.setVisibility(VISIBLE);
            viewHolder.QteARetourner.setText(String.valueOf((int)retourLigne.getQte_avant_retour()));
            viewHolder.bottomDivider.setVisibility(GONE);
        }

        return convertView;
    }

    public class Retour_LigneViewHolder
    {
        public TextView designationProduit;
        public TextView referenceProduit;
        public TextView nomFournisseur;
        public TextView labelQteBandeau;
        public TextView qteRetourner;
        public TextView QteARetourner;
        public TextView lotRetourne;
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
