package fr.alcyons.phimr4.Stock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.Stock_Lot_EmplacementAdapter;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class DetailStockActivity extends ServiceActivity {

    Depot depotSelectionne;
    Produit produitSelectionne;
    String numero_serie;
    Stock stockSelectionne;

    boolean contexteUtiliser;

    List<Stock_Lot_Emplacement_Light> stockLotEmplacementLightList = new ArrayList<>();
    ListView stockLotEmplacementLightListView;

    Stock_Lot_EmplacementAdapter stockLotEmplacementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stock);

        // Récupéation du dépot, du stock et du produit
        depotSelectionne = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotUID_Selectionne"));
        stockSelectionne = (Stock) intent.getExtras().getSerializable("stock_Selectionne");
        produitSelectionne = gestionnaireProduit.getProduitByID(db, stockSelectionne.getProduit_UID());
        contexteUtiliser = intent.getBooleanExtra("contexteUtiliser",false);
        numero_serie = intent.getStringExtra("Serie");

        // Affichage des informations de base
        if (depotSelectionne != null) {
            String nomDepot = depotSelectionne.getNom();
            if(utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS") && depotSelectionne.getStructure().contentEquals("PAD"))
            {
                String[] tab_nom = depotSelectionne.getNom().split(" ");
                String nom = tab_nom[0];
                if(nom.length() > 2)
                {
                    nom = nom.substring(0, 3)+"...";
                }
                else
                {
                    nom = nom +"...";
                }
                String prenom = tab_nom[1];
                if(prenom.length() > 2)
                {
                    prenom = prenom.substring(0, 3)+"...";
                }
                else
                {
                    prenom = prenom+"...";
                }
                nomDepot = nom+" "+prenom;
            }
            ((TextView) findViewById(R.id.nomDepot)).setText(nomDepot);
        }
        if (stockSelectionne != null) {
            ((TextView) findViewById(R.id.referenceProduit)).setText(stockSelectionne.getProduit_Reference());
            ((TextView) findViewById(R.id.nomFournisseur)).setText(stockSelectionne.getFournisseur());

            Double aDouble = new Double(stockSelectionne.getQuantite_Actuelle());
            int qteActuelle = aDouble.intValue();
            ((TextView) findViewById(R.id.qteActuelle)).setText(String.valueOf(qteActuelle));
        }
        if (produitSelectionne != null) {
            ((TextView) findViewById(R.id.nomProduit)).setText(String.valueOf(produitSelectionne.getDesignation_interne()));
        }

        // Gestion de la listView
        stockLotEmplacementLightListView = (ListView) findViewById(R.id.listeView);
        stockLotEmplacementLightListView.setDivider(footer);

        if(contexteUtiliser){
            stockLotEmplacementLightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Stock_Lot_Emplacement_Light stockLotEmplacementLight = (Stock_Lot_Emplacement_Light) stockLotEmplacementAdapter.getItem(position);

                    if (stockLotEmplacementLight != null) {
                        Bundle detailStock_Bundle = new Bundle();
                        detailStock_Bundle.putString("numeroLot", stockLotEmplacementLight.getLot());
                        detailStock_Bundle.putString("datePeremption", stockLotEmplacementLight.getPeremptionDate());
                        detailStock_Bundle.putInt("produitID", stockLotEmplacementLight.getProduit_Code());
                        detailStock_Bundle.putString("zoneNom",stockLotEmplacementLight.getZone());
                        detailStock_Bundle.putString("emplacementNom",stockLotEmplacementLight.getEmplacement());
                        Intent detailStock_Intent = new Intent();
                        detailStock_Intent.putExtras(detailStock_Bundle);
                        setResult(CodesEchangesActivites.RETOUR_STOCK_LOT_EMPLACEMENT, detailStock_Intent);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stockSelectionne != null) {
            stockLotEmplacementLightList = gestionnaireStock_Lot_Emplacement.getStockLotEmplacementByStock(db, stockSelectionne);
        }

        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(stockLotEmplacementLightList.size()));
        stockLotEmplacementAdapter = new Stock_Lot_EmplacementAdapter(DetailStockActivity.this, stockLotEmplacementLightList);
        stockLotEmplacementLightListView.setAdapter(stockLotEmplacementAdapter);
        invalidateOptionsMenu();
    }
}
