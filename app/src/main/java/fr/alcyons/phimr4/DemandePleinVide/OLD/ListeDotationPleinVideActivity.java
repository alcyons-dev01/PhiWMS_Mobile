package fr.alcyons.phimr4.DemandePleinVide.OLD;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Dotation;
import fr.alcyons.phimr4.DemandePleinVide.DetailDotationPleinVideActivity;
import fr.alcyons.phimr4.ListViewAdapters.DotationAdapter;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by jessica on 31/01/2018.
 */

public class ListeDotationPleinVideActivity extends ServiceActivity {

    Depot depot;
    List<Dotation> dotationList;

    DotationAdapter dotationAdapter;
    ListView dotationListView;

    int depotUID_Selectionne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_dotation_pleinvide);

        // Récupération des variables globales
        depotUID_Selectionne = intent.getExtras().getInt("depotUID_Selectionne");
        // Récupération du dépot sélectionné
        depot = gestionnaireDepot.getDepotParID(db, depotUID_Selectionne);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.titre)).setText("Dotation PleinVide - " + depot.getNom());

        // Récupération et initialisation de l'action de la listView
        dotationListView = (ListView) findViewById(R.id.listeView);
        dotationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dotation Dotation_Selection = (Dotation) dotationAdapter.getItem(position);

                Bundle listeDotationPleinVide_Bundle = ListeDotationPleinVideActivity.super.getBundle();
                listeDotationPleinVide_Bundle.putInt("depotUID_Selectionne", depotUID_Selectionne);
                listeDotationPleinVide_Bundle.putInt("Dotation_Selection_PhiMR4UUID", Dotation_Selection.getPhiMR4UUID());

                Intent listeDotationPleinVide_Intent = new Intent(ListeDotationPleinVideActivity.this, DetailDotationPleinVideActivity.class);
                listeDotationPleinVide_Intent.putExtras(listeDotationPleinVide_Bundle);
                ListeDotationPleinVideActivity.this.startActivity(listeDotationPleinVide_Intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        dotationList = new ArrayList<>();
        dotationList = gestionnaireDotation.getDotationPleinByDepot(db, depot.getDepot_UID());

        if (dotationList.size() == 0) {
            vide = true;
            nomServiceVide = "Dotation Plein Vide";
            ListeDotationPleinVideActivity.this.finish();
        }
        else if (dotationList.size() == 1) {
            Dotation Dotation_Selection = dotationList.get(0);

            Bundle listeDotationPleinVide_Bundle = ListeDotationPleinVideActivity.super.getBundle();
            listeDotationPleinVide_Bundle.putInt("depotUID_Selectionne", depotUID_Selectionne);
            listeDotationPleinVide_Bundle.putInt("Dotation_Selection_PhiMR4UUID", Dotation_Selection.getPhiMR4UUID());
            Intent listeDotationPleinVide_Intent = new Intent(ListeDotationPleinVideActivity.this, DetailDotationPleinVideActivity.class);
            listeDotationPleinVide_Intent.putExtras(listeDotationPleinVide_Bundle);
            ListeDotationPleinVideActivity.this.startActivity(listeDotationPleinVide_Intent);
            ListeDotationPleinVideActivity.this.finish();
        } else {
            /* Code nécessaire à l'affichage de la liste */
            dotationAdapter = new DotationAdapter(ListeDotationPleinVideActivity.this, dotationList, db);
            dotationListView.setAdapter(dotationAdapter);
        }
        invalidateOptionsMenu();
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, dotationAdapter, null, "Intitulé dotation...");
        return true;
    }
}
