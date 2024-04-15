package fr.alcyons.phiwms_mobile.MedicamentAuLivret;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.View;

import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;

/**
 * Created by quentinlanusse on 04/05/2017.
 */

public class InformationMedicament extends Fragment {

    Produit medicament_Selectionne;

    Intent intent;
    SQLiteDatabase db;

    List<Integer> produitID_List;

    View.OnClickListener onClickListener_Prendre_Photo;

    // Fonction permettant de passer à l'élément suivant
    View.OnClickListener onClickListener_Suivant = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intent = getActivity().getIntent();
            int position = produitID_List.indexOf(medicament_Selectionne.getID_produit());
            Produit medicament_Suivant = ProduitOpenHelper.getProduitByID(db, produitID_List.get(0));

            if (position != produitID_List.size() - 1) {
                medicament_Suivant = ProduitOpenHelper.getProduitByID(db, produitID_List.get(position + 1));
            }
            changerDeMedicament(medicament_Suivant);
        }
    };

    // Fonction permettant de passer à l'élément précédent
    View.OnClickListener onClickListener_Precedent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intent = getActivity().getIntent();
            int position = produitID_List.indexOf(medicament_Selectionne.getID_produit());
            Produit medicament_Precedant = ProduitOpenHelper.getProduitByID(db, produitID_List.get(produitID_List.size() - 1));

            if (position != 0) {
                medicament_Precedant = ProduitOpenHelper.getProduitByID(db, produitID_List.get(position - 1));
            }
            changerDeMedicament(medicament_Precedant);
        }
    };


    // Lance l'activité avec le nouvel élément
    public void changerDeMedicament(Produit medicamentCible) {
        Intent informationMedicament_Intent = new Intent(getActivity(), DetailMedicamentAuLivretActivity.class);
        Bundle informationMedicament_Bundle = intent.getExtras();
        informationMedicament_Bundle.putInt("produitID_Selectionne", medicamentCible.getID_produit());
        informationMedicament_Intent.putExtras(informationMedicament_Bundle);
        getActivity().startActivity(informationMedicament_Intent);
        getActivity().finish();
    }

    // Fonctions permettant au parent de nous transmettre des paramètres
    public void setParametres(Produit medicament_Selectionne, SQLiteDatabase database, View.OnClickListener onClickListener_Prendre_Photo, List<Integer> produitID_List) {
        this.medicament_Selectionne = medicament_Selectionne;
        db = database;
        this.onClickListener_Prendre_Photo = onClickListener_Prendre_Photo;
        this.produitID_List = produitID_List;

    }
}
