package fr.alcyons.phimr4.DispositifAuLivret;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.View;

import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.Produit;

/**
 * Created by quentinlanusse on 09/05/2017.
 */

public class InformationDispositif extends Fragment {

    Produit dispositif_Selectionne;

    Intent intent;
    SQLiteDatabase db;

    View.OnClickListener onClickListener_Prendre_Photo;

    List<Integer> produitID_List;

    // Définition de la fonction permettant à l'élement suivant
    View.OnClickListener onClickListener_Suivant = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intent = getActivity().getIntent();
            int position = produitID_List.indexOf(dispositif_Selectionne.getID_produit());
            Produit dispositif_Suivant = ProduitOpenHelper.getProduitByID(db, produitID_List.get(0));

            if (position != produitID_List.size() - 1) {
                dispositif_Suivant = ProduitOpenHelper.getProduitByID(db, produitID_List.get(position + 1));
            }
            changerDeDispositif(dispositif_Suivant);
        }
    };

    // Définition de la fonction permettant à l'élement précédent
    View.OnClickListener onClickListener_Precedent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intent = getActivity().getIntent();
            int position = produitID_List.indexOf(dispositif_Selectionne.getID_produit());
            Produit dispositif_Precedant = ProduitOpenHelper.getProduitByID(db, produitID_List.get(produitID_List.size() - 1));
            if (position != 0) {
                dispositif_Precedant = ProduitOpenHelper.getProduitByID(db, produitID_List.get(position - 1));
            }
            changerDeDispositif(dispositif_Precedant);
        }
    };

    // Rappel de l'activité actuelle avec un nouveau produit
    public void changerDeDispositif(Produit dispositifCible) {
        Intent informationDispositif_Intent = new Intent(getActivity(), DetailDispositifAuLivretActivity.class);
        Bundle informationDispositif_Bundle = intent.getExtras();
        informationDispositif_Bundle.putInt("produitID_Selectionne", dispositifCible.getID_produit());
        informationDispositif_Intent.putExtras(informationDispositif_Bundle);
        getActivity().startActivity(informationDispositif_Intent);
        getActivity().finish();
    }

    // Fonctions permettant au parent de nous transmettre des paramètres
    public void setParametres(Produit dispositif_Selectionne, SQLiteDatabase database, View.OnClickListener onClickListener_Prendre_Photo, List<Integer> produitID_List) {
        this.dispositif_Selectionne = dispositif_Selectionne;
        db = database;
        this.onClickListener_Prendre_Photo = onClickListener_Prendre_Photo;
        this.produitID_List = produitID_List;
    }
}
