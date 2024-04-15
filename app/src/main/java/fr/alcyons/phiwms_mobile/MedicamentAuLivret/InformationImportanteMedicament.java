package fr.alcyons.phiwms_mobile.MedicamentAuLivret;

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
 * Created by quentinlanusse on 04/05/2017.
 */

public class InformationImportanteMedicament extends InformationMedicament {

    public ImageView photo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.information_importante_medicament, container, false);

        // Gestion des clics de changement de médicament
        v.findViewById(R.id.boutonFlecheDroite).setOnClickListener(onClickListener_Suivant);
        v.findViewById(R.id.boutonFlecheGauche).setOnClickListener(onClickListener_Precedent);

        // Remplissage du contenu de la page
        ((TextView) v.findViewById(R.id.nomMedicamentTitre)).setText(medicament_Selectionne.getDesignation_interne());
        ((TextView) v.findViewById(R.id.nomMedicament)).setText(medicament_Selectionne.getDesignation_interne());
        ((TextView) v.findViewById(R.id.nomFournisseur)).setText(medicament_Selectionne.getFournisseur());
        ((TextView) v.findViewById(R.id.informationsImportantes)).setText(medicament_Selectionne.getInformations_importantes());
        ((TextView) v.findViewById(R.id.contreIndications)).setText(medicament_Selectionne.getContre_indications());
        ((TextView) v.findViewById(R.id.effetsIndesirables)).setText(medicament_Selectionne.getEffets_indesirables());
        ((CheckBox) v.findViewById(R.id.isARisque)).setChecked(medicament_Selectionne.isMedicament_Risque());
        ((CheckBox) v.findViewById(R.id.isDotationUrgence)).setChecked(medicament_Selectionne.isMedicament_dotation_urgence());
        ((TextView) v.findViewById(R.id.numPUI)).setText(medicament_Selectionne.getZone_PUI_Defaut());
        ((TextView) v.findViewById(R.id.emplacementPUI)).setText(medicament_Selectionne.getEmplacement_PUI_Defaut());

        photo = (ImageView) v.findViewById(R.id.photo);
        photo.setOnClickListener(onClickListener_Prendre_Photo);


        Depot depot = DepotOpenHelper.getDepotPUI(db);
        if(!medicament_Selectionne.getDesignation_interne().contentEquals("Traceur_Medicament_ALCYONS"))
        {
            MedicalObjective medicalObjective = new MedicalObjective(getContext(), ((OriginalActivity) getContext()).utilisateurConnecte, depot, depot, medicament_Selectionne, true);
            medicalObjective.getPictureImage("MedicamentAuLivret");
        }
        return v;
    }
}
