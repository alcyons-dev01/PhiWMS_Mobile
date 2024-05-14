package fr.alcyons.phiwms_mobile.DispositifAuLivret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import fr.alcyons.phiwms_mobile.R;
public class InformationRisqueDispositif extends InformationDispositif {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.information_risques_dispositif, container, false);

        // Gestion des clics de changement de médicament
        v.findViewById(R.id.boutonFlecheDroite).setOnClickListener(onClickListener_Suivant);
        v.findViewById(R.id.boutonFlecheGauche).setOnClickListener(onClickListener_Precedent);

        // Remplissage des informations
        ((TextView) v.findViewById(R.id.nomDispositifTitre)).setText(dispositif_Selectionne.getDesignation_interne());
        ((CheckBox) v.findViewById(R.id.isPresencePHT)).setChecked(dispositif_Selectionne.isRisque_PHT());
        ((CheckBox) v.findViewById(R.id.isPresenceLatex)).setChecked(dispositif_Selectionne.isRisque_latex());
        ((TextView) v.findViewById(R.id.substancesPresentes)).setText(dispositif_Selectionne.getRisque_Substance_presence());
        ((TextView) v.findViewById(R.id.substancesAbsentes)).setText(dispositif_Selectionne.getRisque_Substance_absence());
        ((TextView) v.findViewById(R.id.commentaire)).setText(dispositif_Selectionne.getCommentaire());

        return v;
    }
}
