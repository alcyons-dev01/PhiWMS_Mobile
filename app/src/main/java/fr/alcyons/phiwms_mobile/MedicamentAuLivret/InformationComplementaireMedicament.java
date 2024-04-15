package fr.alcyons.phiwms_mobile.MedicamentAuLivret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.alcyons.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 04/05/2017.
 */

public class InformationComplementaireMedicament extends InformationMedicament {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.information_complementaire_medicament, container, false);

        // Gestion des clics de changement de médicament
        v.findViewById(R.id.boutonFlecheDroite).setOnClickListener(onClickListener_Suivant);
        v.findViewById(R.id.boutonFlecheGauche).setOnClickListener(onClickListener_Precedent);

        // Remplissage du contenu de la page
        ((TextView) v.findViewById(R.id.nomMedicamentTitre)).setText(medicament_Selectionne.getDesignation_interne());
        ((TextView) v.findViewById(R.id.forme)).setText(medicament_Selectionne.getForme());
        ((TextView) v.findViewById(R.id.contenant)).setText(medicament_Selectionne.getContenant());
        ((TextView) v.findViewById(R.id.materiaux)).setText(medicament_Selectionne.getMateriaux());
        ((TextView) v.findViewById(R.id.statut)).setText(medicament_Selectionne.getStatut());
        ((TextView) v.findViewById(R.id.secteur)).setText(medicament_Selectionne.getSecteur());
        ((TextView) v.findViewById(R.id.commentaire)).setText(medicament_Selectionne.getCommentaire());

        return v;
    }
}
