package fr.alcyons.phiwms_mobile.DispositifAuLivret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.OriginalActivity;
import fr.alcyons.phiwms_mobile.Outils.MedicalObjective;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 09/05/2017.
 */

public class InformationImportanteDispositif extends InformationDispositif {

    public ImageView photo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.information_importante_dispositif, container, false);

        // Gestion des clics de changement de médicament
        v.findViewById(R.id.boutonFlecheDroite).setOnClickListener(onClickListener_Suivant);
        v.findViewById(R.id.boutonFlecheGauche).setOnClickListener(onClickListener_Precedent);

        // Remplissage du contenu de la page
        ((TextView) v.findViewById(R.id.nomDispositifTitre)).setText(dispositif_Selectionne.getDesignation_interne().trim());
        ((TextView) v.findViewById(R.id.nomFournisseur)).setText(dispositif_Selectionne.getFournisseur().trim());
        ((TextView) v.findViewById(R.id.categorie)).setText(dispositif_Selectionne.getCategorie().trim());
        ((TextView) v.findViewById(R.id.informationsImportantes)).setText(dispositif_Selectionne.getInformations_importantes());
        ((CheckBox) v.findViewById(R.id.isUsageUnique)).setChecked(dispositif_Selectionne.isCondition_usage_unique());
        ((CheckBox) v.findViewById(R.id.isSterile)).setChecked(dispositif_Selectionne.isSterile());
        ((TextView) v.findViewById(R.id.infosSterilisation)).setText(dispositif_Selectionne.getSterilisation_Mode());
        ((CheckBox) v.findViewById(R.id.isNePasResteriliser)).setChecked(dispositif_Selectionne.isNePasResteriliser());
        ((TextView) v.findViewById(R.id.prixUnitaire)).setText(String.valueOf(dispositif_Selectionne.getPrix_unitaire()));
        ((TextView) v.findViewById(R.id.numPUI)).setText(dispositif_Selectionne.getZone_PUI_Defaut());
        ((TextView) v.findViewById(R.id.emplacementPUI)).setText(dispositif_Selectionne.getEmplacement_PUI_Defaut());

        photo = (ImageView) v.findViewById(R.id.photo);
        photo.setOnClickListener(onClickListener_Prendre_Photo);

        Depot depot = DepotOpenHelper.getDepotPUI(db);
        if(!dispositif_Selectionne.getDesignation_interne().contentEquals("Traceur_Dispositif_ALCYONS"))
        {
            MedicalObjective medicalObjective = new MedicalObjective(getContext(), ((OriginalActivity) getContext()).utilisateurConnecte, depot, depot, dispositif_Selectionne, true);
            medicalObjective.getPictureImage("DispositifAuLivret");
        }

        return v;
    }
}
