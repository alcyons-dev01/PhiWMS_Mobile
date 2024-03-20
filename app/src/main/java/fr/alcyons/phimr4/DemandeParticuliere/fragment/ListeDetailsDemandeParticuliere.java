package fr.alcyons.phimr4.DemandeParticuliere.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phimr4.Classes.Demande_PUI;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.ListViewAdapters.Demande_PUIAdapter;
import fr.alcyons.phimr4.MenuActivity;
import fr.alcyons.phimr4.R;

/**
 * Created by olivier on 04/10/2017.
 */

public class ListeDetailsDemandeParticuliere extends Fragment {

    Depot depot;

    Context context;

    List<Integer> listeProduitID;
    List<Produit> listeProduit;
    List<Demande_PUI> listeDemandePUI;
    ListView listViewDemandePUI;
    Demande_PUIAdapter adapter;

    // Fonctions permettant au parent de nous transmettre des paramètres
    public void setParametres(List<Integer> produit, Depot depot) {
        this.listeProduitID = produit;
        this.depot = depot;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.liste_produit, container, false);

        context = getContext();

        // Récupération de la listView et initialisation de la liste contenant les Demande PUI
        listViewDemandePUI = ((ListView) v.findViewById(R.id.listeView));
        listeDemandePUI = new ArrayList<>();

        // Récupération de la liste de produit afin de les transformer par la suite en Demande_PUI
        listeProduit = new ArrayList<>();
        for (Integer i : listeProduitID) {
            for (Produit produitCourant : ProduitOpenHelper.getAllProduits(((MenuActivity) context).db)) {
                if (produitCourant.getID_produit() == i) {
                    listeProduit.add(produitCourant);
                }
            }
        }

        for (Produit detail_produit : listeProduit) {

            Produit produit = ProduitOpenHelper.getProduitByID(((MenuActivity) context).db, detail_produit.getID_produit());
            Depot depotPUI = DepotOpenHelper.getDepotPUI(((MenuActivity) context).db);
            Depot depotDestinataire = DepotOpenHelper.getDepotParID(((MenuActivity) context).db, depot.getDepot_UID());


            Stock stockPUI = StockOpenHelper.getStockByProduitEtDepot(((MenuActivity) context).db, produit, depotPUI);
            Stock stockDestinataire = StockOpenHelper.getStockByProduitEtDepot(((MenuActivity) context).db, produit, depotDestinataire);

            double qteStockDestinataire = 0;
            double qteStockPui = 0;

            if (stockPUI != null)
                qteStockPui = stockPUI.getQuantite_Actuelle();
            if (stockDestinataire != null)
                qteStockDestinataire = stockDestinataire.getQuantite_Actuelle();

            Demande_PUI demande_pui = new Demande_PUI(produit, qteStockPui, qteStockDestinataire);

            listeDemandePUI.add(demande_pui);
        }

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Demande_PUIAdapter(getActivity(), listeDemandePUI);

        listViewDemandePUI.setAdapter(adapter);
        listViewDemandePUI.setItemsCanFocus(true);
        listViewDemandePUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Effacer le clavier
                Demande_PUIAdapter.Demande_PUIViewHolder viewHolder = adapter.Demande_PUIViewHolderList.get(position);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MenuActivity) context).invalidateOptionsMenu();
        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Demande_PUIAdapter(getActivity(), listeDemandePUI);
        listViewDemandePUI.setAdapter(adapter);
    }

}