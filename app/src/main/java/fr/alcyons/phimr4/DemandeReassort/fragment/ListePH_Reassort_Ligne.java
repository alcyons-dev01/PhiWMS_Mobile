package fr.alcyons.phimr4.DemandeReassort.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Reassort_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phimr4.Classes.Demande_PUI;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.PH_Reassort;
import fr.alcyons.phimr4.Classes.PH_Reassort_Ligne;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.ListViewAdapters.Demande_PUIAdapter;
import fr.alcyons.phimr4.MenuActivity;
import fr.alcyons.phimr4.R;

/**
 * Created by jessica on 05/10/2017.
 */

public class ListePH_Reassort_Ligne extends Fragment {

    PH_Reassort ph_reassort;

    Context context;

    List<Demande_PUI> listeDemandePUI;
    ListView listViewDetailDots;
    Demande_PUIAdapter adapter;

    // Fonctions permettant au parent de nous transmettre des paramètres
    public void setParametres(PH_Reassort ph_reassort) {
        this.ph_reassort = ph_reassort;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.liste_ph_reassort_ligne, container, false);

        context = getContext();

        // Affichage des informations de base
        ((TextView) v.findViewById(R.id.nomDemande)).setText(String.valueOf(ph_reassort.getListe()));

        // Récupération de la listView et initialisation de la liste contenant les Demande PUI
        listViewDetailDots = ((ListView) v.findViewById(R.id.listeView));
        listeDemandePUI = new ArrayList<>();

        // Récupération des ph_reassort_ligne du PH_Reassort
        for (PH_Reassort_Ligne ph_reassort_ligne : PH_Reassort_LigneOpenHelper.getAllPH_Reassort_LigneParPH_Reassort(((MenuActivity) context).db, ph_reassort)) {

            Produit produit = ProduitOpenHelper.getProduitByID(((MenuActivity) context).db, ph_reassort_ligne.getProduit_ID());
            Depot depotPUI = DepotOpenHelper.getDepotPUI(((MenuActivity) context).db);
            Depot depotDestinataire = DepotOpenHelper.getDepotParReference(((MenuActivity) context).db, ph_reassort.getDepot_Reference());

            Stock stockPUI = StockOpenHelper.getStockByProduitEtDepot(((MenuActivity) context).db, produit, depotPUI);
            Stock stockDestinataire = StockOpenHelper.getStockByProduitEtDepot(((MenuActivity) context).db, produit, depotDestinataire);

            double qteStockPui = 0;
            double qteStockDestinataire = 0;

            if (stockPUI != null)
                qteStockPui = stockPUI.getQuantite_Actuelle();
            if (stockDestinataire != null)
                qteStockDestinataire = stockDestinataire.getQuantite_Actuelle();

            Demande_PUI demande_pui = new Demande_PUI(ph_reassort_ligne, produit, qteStockPui, qteStockDestinataire);

            listeDemandePUI.add(demande_pui);
        }

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Demande_PUIAdapter(getActivity(), listeDemandePUI);

        listViewDetailDots.setAdapter(adapter);
        listViewDetailDots.setItemsCanFocus(true);
        listViewDetailDots.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        listViewDetailDots.setAdapter(adapter);
    }

}