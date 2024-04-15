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

import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_RetourPUI_Adapte;
import fr.alcyons.phiwms_mobile.R;

import static android.view.View.GONE;

/**
 * Created by quentinlanusse on 04/07/2017.
 */

public class Retour_Ligne_RetourPUIAdapter extends ArrayAdapter {

    public List<Retour_Ligne_RetourPUI_Adapte> retourLigneRetourPUIAdapteList;
    public List<Retour_LigneViewHolder> viewHolderList;
    public List<Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte> emplacementAdapteList;
    Context context;
    SQLiteDatabase db;

    public Retour_Ligne_RetourPUIAdapter(Context context, SQLiteDatabase db, List<Retour_Ligne_RetourPUI_Adapte> retourLigneRetourPUIAdapteList) {
        super(context, 0, retourLigneRetourPUIAdapteList);
        this.context = context;
        this.db = db;
        this.retourLigneRetourPUIAdapteList = retourLigneRetourPUIAdapteList;

        viewHolderList = new ArrayList<>();
        for (int i = 0; i < retourLigneRetourPUIAdapteList.size(); i++) {
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
        viewHolder.lotRetourne = (TextView) convertView.findViewById(R.id.lotRetourne);
        viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
        viewHolder.numSerieProduit = (TextView) convertView.findViewById(R.id.numSerie);
        viewHolder.labelSerie = (TextView) convertView.findViewById(R.id.labelSerie);
        viewHolder.textEmplacement = (TextView) convertView.findViewById(R.id.textEmplacement);
        viewHolder.listZoneEtEmplacement = (LinearLayout) convertView.findViewById(R.id.listZoneEtEmplacement);
        viewHolder.layoutSerie = (LinearLayout) convertView.findViewById(R.id.layoutSerie);
        viewHolder.layoutPrincipal = (RelativeLayout) convertView.findViewById(R.id.layoutPrincipal);
        viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);


        Retour_Ligne_RetourPUI_Adapte retour_LigneAdapteCourant = (Retour_Ligne_RetourPUI_Adapte) getItem(position);
        Retour_Ligne retourLigne = Retour_LigneOpenHelper.getRetourLigneByID(db, retour_LigneAdapteCourant.getRetourLigneID());

        viewHolder.designationProduit.setText(retourLigne.getProduit_Designation());
        viewHolder.referenceProduit.setText(retourLigne.getProduit_Reference());
        viewHolder.nomFournisseur.setText(retourLigne.getProduit_Fournisseur());
        viewHolder.lotRetourne.setText(retourLigne.getLot_Retourner());
        viewHolder.numSerieProduit.setText(retourLigne.getSerie_Retourner());
        if(retourLigne.getSerie_Retourner().contentEquals(""))
        {
            viewHolder.labelSerie.setVisibility(GONE);
            viewHolder.numSerieProduit.setVisibility(GONE);
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
        emplacementAdapteList = new ArrayList<>();
        emplacementAdapteList.addAll(retour_LigneAdapteCourant.getEmplacementAdaptes());


        for (int i = 0; i < emplacementAdapteList.size(); i++) {

            Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapte = emplacementAdapteList.get(i);

            Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementAdapte.getEmplacementID());
            Depot_Zone zone = ZoneOpenHelper.getUneZoneByID(db, emplacement.getZoneID());

            quantiteRetourner += emplacementAdapte.getQte();
        }

        if(emplacementAdapteList.size() == 1)
        {
            Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementAdapteList.get(0).getEmplacementID());
            viewHolder.textEmplacement.setText(emplacement.getAdressage());
        }
        else
        {
            String nb_emplacement = emplacementAdapteList.size()+" emplacements";
            viewHolder.textEmplacement.setText(nb_emplacement);
        }

        viewHolder.qteRetourner.setText(String.valueOf(quantiteRetourner));


        return convertView;
    }

    public class Retour_LigneViewHolder {
        public TextView designationProduit;
        public TextView referenceProduit;
        public TextView nomFournisseur;
        public TextView qteRetourner;
        public TextView lotRetourne;
        public TextView datePeremption;
        public TextView numSerieProduit;
        public TextView labelSerie;
        public TextView textEmplacement;
        public LinearLayout listZoneEtEmplacement;
        public RelativeLayout layoutPrincipal;
        public ProgressBar progressBar;
        public LinearLayout layoutSerie;

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
