package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Lot_ReceptionAdapter extends ArrayAdapter {
    public List<PH_Reliquat_Reception_Adapte.Lot> lotList;
    public List<PH_Reliquat_Reception_Adapte.ZoneEtEmplacement> zoneEtEmplacementList;
    public List<Lot_ReceptionViewHolder> viewHolderList;
    public Lot_ReceptionViewHolder viewHolder;
    Context context;
    SQLiteDatabase db;
    int phReliquatUID;
    private boolean num_serie_present;
    boolean ADH;
    public boolean full;

    public Lot_ReceptionAdapter(Context context, SQLiteDatabase db, List<PH_Reliquat_Reception_Adapte.Lot> lotList, int phReliquatUID, boolean numero_serie_present, boolean ADH) {
        super(context, 0, lotList);
        this.context = context;
        this.db = db;
        this.lotList = lotList;
        this.phReliquatUID = phReliquatUID;
        this.num_serie_present = numero_serie_present;
        this.ADH = ADH;
        viewHolderList = new ArrayList<>();
        for (PH_Reliquat_Reception_Adapte.Lot phReliquatLotReceptionPui : lotList) {
            viewHolderList.add(new Lot_ReceptionViewHolder());
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_lot_reception, parent, false);
        }

        viewHolder = (Lot_ReceptionViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = viewHolderList.get(position);
            viewHolder.nomEmplacement = (TextView) convertView.findViewById(R.id.nomEmplacement);
            viewHolder.lot = (TextView) convertView.findViewById(R.id.lot);
            viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
            viewHolder.qteSaisie = (TextView) convertView.findViewById(R.id.qteSaisie);
            viewHolder.layout_ajouter_lot = (LinearLayout) convertView.findViewById(R.id.layout_ajouter_lot);
            viewHolder.layout_annuler_lot = (LinearLayout) convertView.findViewById(R.id.layout_annuler_lot);
            viewHolder.layoutPrincipal = (LinearLayout) convertView.findViewById(R.id.layoutPrincipal);
            viewHolder.layout_qte_saisie_lot_preparation = (LinearLayout) convertView.findViewById(R.id.layout_qte_saisie_lot_preparation);
            viewHolder.layout_emplacement_lot = (LinearLayout) convertView.findViewById(R.id.layout_emplacement_lot);
        }

        PH_Reliquat_Reception_Adapte.Lot lot = (PH_Reliquat_Reception_Adapte.Lot) getItem(position);
        PH_Reliquat reliquat_courant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phReliquatUID);

        //on cache tous les layout
        viewHolder.layout_ajouter_lot.setVisibility(GONE);
        viewHolder.layout_annuler_lot.setVisibility(GONE);
        viewHolder.layoutPrincipal.setVisibility(GONE);

        if (lot != null) {
            if(lot.getNumeroLot().contentEquals("row_ajouter"))
            {
                if(reliquat_courant.getQteReliquat_X() != 0)
                    viewHolder.layout_ajouter_lot.setVisibility(View.VISIBLE);

            }
            else if(lot.getNumeroLot().contentEquals("row_annuler"))
            {
                if(reliquat_courant.getQteReliquat_X() == 0)
                    viewHolder.layout_annuler_lot.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.layoutPrincipal.setVisibility(VISIBLE);
                viewHolder.nomEmplacement.setText(lot.getZoneEtEmplacementList().get(0).getEmplacementName());
                viewHolder.lot.setText(lot.getNumeroLot());
                viewHolder.qteSaisie.setText(String.valueOf(lot.getZoneEtEmplacementList().get(0).getQuantite()));
                viewHolder.qteSaisie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //((ListeLotReceptionActivity) context).ClickNumberPicker(position);
                    }
                });

                Date peremption_Date = null;

                DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    String datePeremption = lot.getDatePeremption();

                    if (!datePeremption.contentEquals("0000-00-00")  && !datePeremption.contentEquals("")) {
                        peremption_Date = dateDecodeur.parse(datePeremption);
                        viewHolder.datePeremption.setText(dateFormat.format(peremption_Date));
                    } else {
                        viewHolder.datePeremption.setText("00/00/0000");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                viewHolder.setDatePeremptionColor(peremption_Date);


                if(Integer.parseInt(viewHolder.qteSaisie.getText().toString()) == 0)
                {
                    viewHolder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne));
                    viewHolder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_orange));
                    viewHolder.layout_emplacement_lot.setBackground(context.getResources().getDrawable(R.drawable.background_preparation_emplacement_orange));
                }
                else
                {
                    viewHolder.layout_qte_saisie_lot_preparation.setBackground(context.getResources().getDrawable(R.drawable.background_qte_lot_phpreparationligne_valider));
                    viewHolder.layoutPrincipal.setBackground(context.getResources().getDrawable(R.drawable.background_detail_preparation_vert));
                    viewHolder.layout_emplacement_lot.setBackground(context.getResources().getDrawable(R.drawable.background_preparation_emplacement_vert));
                }
            }
        }

        return convertView;
    }

    @Override
    public void clear()
    {
        lotList.clear();
    }

    public void addView(PH_Reliquat_Reception_Adapte phcourant, String numLot, String numSerie, String date, String resultat, String conditionnement)
    {
        PH_Reliquat_Reception_Adapte.Lot nouveauLot = phcourant.new Lot(numLot, date, numSerie, resultat);
        PH_Reliquat ph_reliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, phcourant.getPhReliquatUID());
        Produit produit = ProduitOpenHelper.getProduitByID(db, ph_reliquat.getProduitID());
        Depot_Zone zone = ZoneOpenHelper.getZoneByDepotEtNom(db, DepotOpenHelper.getDepotPUI(db),produit.getZone_PUI_Defaut());
        if(zone == null)
        {
            zone = ZoneOpenHelper.getFirstZone(db, DepotOpenHelper.getDepotPUI(db));
        }
        Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone, produit.getEmplacement_PUI_Defaut());
        if(emplacement == null)
        {
            emplacement = EmplacementOpenHelper.getFirstEmplacement(db, zone);
        }
        List<PH_Reliquat_Reception_Adapte.ZoneEtEmplacement>liste_zone_emplacement = new ArrayList<>();
        PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement = phcourant.new ZoneEtEmplacement(zone.getZoneID(), zone.getZoneName(), emplacement.get_UID(), emplacement.getAdressage(), Integer.parseInt(conditionnement));
        liste_zone_emplacement.add(zoneEtEmplacement);
        nouveauLot.setZoneEtEmplacementList(liste_zone_emplacement);

        lotList.add(nouveauLot);

    }

    /* Appel et gestion du Widget Android de sélection de date */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        Lot_ReceptionViewHolder viewHolder;
        SQLiteDatabase db;
        PH_Reliquat_Reception_Adapte.Lot phReliquatLotReceptionPui;

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        // Je sais pas pourquoi mais les mois commencent à 0 et les jours à 1...
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String monthString = month < 9 ? "0" + String.valueOf(month + 1) : String.valueOf(month + 1);
            String dayString = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
            String date = dayString + "/" + monthString + "/" + String.valueOf(year);
            viewHolder.datePeremption.setText(date);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date dateFournie = dateDecodeur.parse(date);
                viewHolder.setDatePeremptionColor(dateFournie);
                phReliquatLotReceptionPui.setDatePeremption(dateFormat.format(dateFournie));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void setViewHolder(Lot_ReceptionViewHolder view, SQLiteDatabase database, PH_Reliquat_Reception_Adapte.Lot phReliquatLotReceptionPui) {
            this.viewHolder = view;
            this.db = database;
            this.phReliquatLotReceptionPui = phReliquatLotReceptionPui;
        }
    }

    public class Lot_ReceptionViewHolder {
        public TextView nomEmplacement;
        public TextView lot;
        public TextView datePeremption;
        public TextView qteSaisie;
        public LinearLayout layout_ajouter_lot;
        public LinearLayout layout_annuler_lot;
        public LinearLayout layoutPrincipal;
        public LinearLayout layout_qte_saisie_lot_preparation;
        public LinearLayout layout_emplacement_lot;

        public void setDatePeremptionColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.rouge2));
                } else if (delai >= delai60jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.orange2));
                } else {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.vert));
                }
            } else {
                datePeremption.setTextColor(Color.BLACK);
            }
        }
    }
}
