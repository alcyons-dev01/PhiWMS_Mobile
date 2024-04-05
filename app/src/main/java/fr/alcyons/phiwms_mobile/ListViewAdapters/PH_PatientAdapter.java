package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.PH_Patient;
import com.example.phiwms_mobile.R;

/**
 * Created by jessica on 06/10/2017.
 */

public class PH_PatientAdapter extends ArrayAdapter {

    public List<PH_Patient> PH_Patients;
    public List<PH_PatientViewHolder> PH_PatientsViewHolderList;
    Context context;
    SQLiteDatabase db;

    public PH_PatientAdapter(Context context, List<PH_Patient> PH_Patients, SQLiteDatabase db) {
        super(context, 0, PH_Patients);
        this.PH_Patients = PH_Patients;
        this.context = context;
        this.db = db;
        PH_PatientsViewHolderList = new ArrayList<>();
        for (int i = 0; i < PH_Patients.size(); i++) {
            PH_PatientViewHolder viewHolder = new PH_PatientViewHolder();
            PH_PatientsViewHolderList.add(viewHolder);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PH_PatientViewHolder viewHolder = PH_PatientsViewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_ph_patient, parent, false);

        // Récupération des objets graphiques
        viewHolder.nom = (TextView) convertView.findViewById(R.id.identitePH_Patient);

        PH_Patient PH_PatientCourant = (PH_Patient) getItem(position);

        // Affichage des valeurs
        viewHolder.nom.setText(PH_PatientCourant.getNom_Usuel() + " " + PH_PatientCourant.getPrénom());

        return convertView;
    }

    private class PH_PatientViewHolder {
        public TextView nom;
    }
}