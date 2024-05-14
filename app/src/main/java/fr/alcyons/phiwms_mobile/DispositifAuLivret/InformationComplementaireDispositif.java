package fr.alcyons.phiwms_mobile.DispositifAuLivret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.alcyons.phiwms_mobile.R;

public class InformationComplementaireDispositif extends InformationDispositif {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.information_complementaire_dispositif, container, false);

        // Gestion des clics de changement de médicament
        v.findViewById(R.id.boutonFlecheDroite).setOnClickListener(onClickListener_Suivant);
        v.findViewById(R.id.boutonFlecheGauche).setOnClickListener(onClickListener_Precedent);

        // Remplissage du contenu de la page
        ((TextView) v.findViewById(R.id.nomDispositifTitre)).setText(dispositif_Selectionne.getDesignation_interne());
        ((TextView) v.findViewById(R.id.forme)).setText(dispositif_Selectionne.getForme());
        ((TextView) v.findViewById(R.id.contenant)).setText(dispositif_Selectionne.getContenant());
        ((TextView) v.findViewById(R.id.materiaux)).setText(dispositif_Selectionne.getMateriaux());
        ((TextView) v.findViewById(R.id.statut)).setText(dispositif_Selectionne.getStatut());
        ((TextView) v.findViewById(R.id.secteur)).setText(dispositif_Selectionne.getSecteur());
        ((TextView) v.findViewById(R.id.commentaire)).setText(dispositif_Selectionne.getCommentaire());

        return v;
    }
}
