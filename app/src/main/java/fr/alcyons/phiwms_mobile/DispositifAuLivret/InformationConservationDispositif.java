package fr.alcyons.phiwms_mobile.DispositifAuLivret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import fr.alcyons.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 09/05/2017.
 */

public class InformationConservationDispositif extends InformationDispositif {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.information_conservation_dispositif, container, false);

        // Gestion des clics de changement de médicament
        v.findViewById(R.id.boutonFlecheDroite).setOnClickListener(onClickListener_Suivant);
        v.findViewById(R.id.boutonFlecheGauche).setOnClickListener(onClickListener_Precedent);

        // Remplissage du contenu de la page
        ((TextView) v.findViewById(R.id.nomDispositifTitre)).setText(dispositif_Selectionne.getDesignation_interne().trim());
        ((CheckBox) v.findViewById(R.id.isRefrigeree)).setChecked(dispositif_Selectionne.isTemperature_Refrigere());
        ((CheckBox) v.findViewById(R.id.isAmbiante)).setChecked(dispositif_Selectionne.isTemperature_Ambiante());
        ((CheckBox) v.findViewById(R.id.isAbriLumiere)).setChecked(dispositif_Selectionne.isConservation_abri());
        ((CheckBox) v.findViewById(R.id.isSec)).setChecked(dispositif_Selectionne.isConservation_sec());
        ((CheckBox) v.findViewById(R.id.isFragile)).setChecked(dispositif_Selectionne.isCondition_Fragile());
        ((TextView) v.findViewById(R.id.tempMin)).setText(String.valueOf(dispositif_Selectionne.getConservation_temperature_min()));
        ((TextView) v.findViewById(R.id.tempMax)).setText(String.valueOf(dispositif_Selectionne.getConservation_temperature_Max()));
        ((TextView) v.findViewById(R.id.reglesConservation)).setText(dispositif_Selectionne.getConservation());
        ((TextView) v.findViewById(R.id.numPUI)).setText(dispositif_Selectionne.getZone_PUI_Defaut().trim());
        ((TextView) v.findViewById(R.id.emplacementPUI)).setText(dispositif_Selectionne.getEmplacement_PUI_Defaut().trim());

        return v;
    }
}
