package fr.alcyons.phimr4.PlanDePlacement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ListViewAdapters.Produit_PlanDePlacementAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class ListeProduitsPlanDePlacementActivity extends ServiceActivity {

    Produit_PlanDePlacementAdapter adapter;
    List<Produit> produitList = new ArrayList<>();
    ListView produitListView;
    boolean passageParOnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_produits_plan_de_placement);

        // Récupération de la liste_view à remplir
        produitListView = (ListView) findViewById(R.id.listeView);

        List<Integer> produitIDs = (List<Integer>) intent.getExtras().getSerializable("produitIDs");
        if(produitIDs == null)
        {
            produitIDs = new ArrayList<>();
        }
        produitList = new ArrayList<>();
        for (int id : produitIDs
                ) {
            produitList.add(gestionnaireProduit.getProduitByID(db, id));
        }

        passageParOnCreate = true;

        if (produitList.size() == 1) {
            passerAuDetailProduit(produitList.get(0));
        } else if (produitList.size() == 0) {
            produitList = gestionnaireProduit.getAllProduits(db);
        } else {
            List<Produit> listeProduitsBd = gestionnaireProduit.getAllProduits(db);
            if (!listeProduitsBd.equals(produitList)) {
                Alerte.afficherAlerte(ListeProduitsPlanDePlacementActivity.this, "Attention", "Plusieurs produits correspondent à ce code.", "alerte");
            } else {
                Toast toast = Toast.makeText(ListeProduitsPlanDePlacementActivity.this, "Aucun produit ne correspond au code fourni", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

        adapter = new Produit_PlanDePlacementAdapter(ListeProduitsPlanDePlacementActivity.this, produitList);
        produitListView.setDivider(footer);
        produitListView.setAdapter(adapter);
        // Gestion des clics sur les produits
        produitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                passerAuDetailProduit((Produit) adapter.getItem(position));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        if (produitList.size() == 1 && !passageParOnCreate) {
            onBackPressed();
        } else {
            List<Produit> tempProduitList = new ArrayList<>();
            for (Produit produitCourant : produitList
                    ) {
                tempProduitList.add(gestionnaireProduit.getProduitByID(db, produitCourant.getID_produit()));
            }
            produitList = tempProduitList;
            adapter = new Produit_PlanDePlacementAdapter(ListeProduitsPlanDePlacementActivity.this, produitList);
            produitListView.setAdapter(adapter);
        }

        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(adapter.getCount()));

        passageParOnCreate = false;
    }

    public void passerAuDetailProduit(Produit produitSelectionne) {
        Intent listeProduitsPlanDePlacementIntent = new Intent(ListeProduitsPlanDePlacementActivity.this, DetailProduitPlanDePlacementActivity.class);
        Bundle listeProduitsPlanDePlacementBundle = ListeProduitsPlanDePlacementActivity.super.getBundle();
        listeProduitsPlanDePlacementBundle.putInt("produitSelectionneID", produitSelectionne.getID_produit());
        listeProduitsPlanDePlacementIntent.putExtras(listeProduitsPlanDePlacementBundle);
        ListeProduitsPlanDePlacementActivity.this.startActivity(listeProduitsPlanDePlacementIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);
        return true;
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Désignation produit...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                lancerScan();
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        ListeProduitsPlanDePlacementActivity.this.finish();
    }

    public void lancerScan()
    {
        passageParOnCreate = true;
        Intent listeProduitsPlanDePlacementIntent = new Intent(ListeProduitsPlanDePlacementActivity.this, ServicePlanDePlacementActivity.class);
        listeProduitsPlanDePlacementIntent.putExtras(super.getBundle());
        ListeProduitsPlanDePlacementActivity.this.startActivity(listeProduitsPlanDePlacementIntent);
        ListeProduitsPlanDePlacementActivity.this.finish();
    }
}
