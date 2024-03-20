package fr.alcyons.phimr4.MedicamentAuLivret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.alcyons.phimr4.R;

/**
 * Created by quentinlanusse on 04/05/2017.
 */

public class InformationPrescriptionMedicament extends InformationMedicament {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.information_prescription_medicament, container, false);

        // Gestion des clics de changement de médicament
        v.findViewById(R.id.boutonFlecheDroite).setOnClickListener(onClickListener_Suivant);
        v.findViewById(R.id.boutonFlecheGauche).setOnClickListener(onClickListener_Precedent);

        // Remplissage des informations
        ((TextView) v.findViewById(R.id.nomMedicamentTitre)).setText(medicament_Selectionne.getDesignation_interne());
        ((TextView) v.findViewById(R.id.categorie)).setText(medicament_Selectionne.getCategorie());
        ((TextView) v.findViewById(R.id.posologie)).setText(medicament_Selectionne.getPosologie());
        ((TextView) v.findViewById(R.id.voie)).setText(medicament_Selectionne.getVoie());
        ((TextView) v.findViewById(R.id.indicationTherapeutique)).setText(medicament_Selectionne.getIndication_therapeutique());
        ((TextView) v.findViewById(R.id.UI)).setText(String.valueOf(medicament_Selectionne.getUI_Conversion()));
        ((TextView) v.findViewById(R.id.coutTraitementJournalier)).setText(String.valueOf(medicament_Selectionne.getMedicament_CTJ()));
        ((TextView) v.findViewById(R.id.prixUnitaire)).setText(String.valueOf(medicament_Selectionne.getPrix_unitaire()));

        return v;
    }
}
