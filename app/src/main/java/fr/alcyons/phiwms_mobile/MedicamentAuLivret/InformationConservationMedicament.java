package fr.alcyons.phiwms_mobile.MedicamentAuLivret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import fr.alcyons.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 04/05/2017.
 */

public class InformationConservationMedicament extends InformationMedicament {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.information_conservation_medicament, container, false);

        // Gestion des clics de changement de médicament
        v.findViewById(R.id.boutonFlecheDroite).setOnClickListener(onClickListener_Suivant);
        v.findViewById(R.id.boutonFlecheGauche).setOnClickListener(onClickListener_Precedent);

        // Remplissage du contenu de la page
        ((TextView) v.findViewById(R.id.nomMedicamentTitre)).setText(medicament_Selectionne.getDesignation_interne());
        ((CheckBox) v.findViewById(R.id.isRefrigeree)).setChecked(medicament_Selectionne.isTemperature_Refrigere());
        ((CheckBox) v.findViewById(R.id.isAmbiante)).setChecked(medicament_Selectionne.isTemperature_Ambiante());
        ((CheckBox) v.findViewById(R.id.isAbriLumiere)).setChecked(medicament_Selectionne.isConservation_abri());
        ((CheckBox) v.findViewById(R.id.isSec)).setChecked(medicament_Selectionne.isConservation_sec());
        ((TextView) v.findViewById(R.id.tempMin)).setText(String.valueOf(medicament_Selectionne.getConservation_temperature_min()));
        ((TextView) v.findViewById(R.id.tempMax)).setText(String.valueOf(medicament_Selectionne.getConservation_temperature_Max()));
        ((TextView) v.findViewById(R.id.reglesConservation)).setText(medicament_Selectionne.getConservation());
        ((TextView) v.findViewById(R.id.numPUI)).setText(medicament_Selectionne.getZone_PUI_Defaut());
        ((TextView) v.findViewById(R.id.emplacementPUI)).setText(medicament_Selectionne.getEmplacement_PUI_Defaut());

        return v;
    }
}
