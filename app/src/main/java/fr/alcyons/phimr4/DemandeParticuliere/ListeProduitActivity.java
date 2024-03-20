package fr.alcyons.phimr4.DemandeParticuliere;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ListViewAdapters.Produit_DemandeParticuliereAdapter;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;


/**
 * Created by olivier on 02/10/2017.
 */

public class ListeProduitActivity extends ServiceActivity {

    Depot depot;

    List<Produit> produitList = new ArrayList<>();
    ListView produitListView;
    Produit_DemandeParticuliereAdapter produitDemandeParticuliereAdapter;

    boolean back = false;
    TextView nb_prod_selectTextView;
    private Spinner spinnerChoix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_produit_demande_particuliere);

        // Récupération du dépot grace aux variables globales
        depot = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotSelectionneID"));

        // Affichage des informations de base
        if(depot != null)
        {
            String nomDepot = depot.getNom();
            if(utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS") && depot.getStructure().contentEquals("PAD"))
            {
                String[] tab_nom = depot.getNom().split(" ");
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

        // Récupération et initialisation du Spinner permettant de trier les produits
        spinnerChoix = (Spinner) findViewById(R.id.spinnerChoix);
        spinnerChoix.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        // Récupération et initialisation du nombre de produit sélectionné
        nb_prod_selectTextView = (TextView) findViewById(R.id.nb_prod_select);
        nb_prod_selectTextView.setText("0");

        // Récupération de la listView
        produitListView = (ListView) findViewById(R.id.listeView);
    }

    @Override
    public void onResume() {
        super.onResume();

        back = intent.getBooleanExtra("Back", false);

        // Récupération de tous les produit
        produitList = gestionnaireProduit.getAllProduits(db);

        // Affichage du nombre de produit
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(produitList.size()));

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        List<Integer> tempListe = new ArrayList<>();
        produitDemandeParticuliereAdapter = new Produit_DemandeParticuliereAdapter(ListeProduitActivity.this, produitList, nb_prod_selectTextView, tempListe);
        produitListView.setAdapter(produitDemandeParticuliereAdapter);

        invalidateOptionsMenu();
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, produitDemandeParticuliereAdapter, null, "Désignation produit...");
        return true;
    }

    public void onClick_boutonSave(View v){
        Integer tailleListe = produitDemandeParticuliereAdapter.produitSelect.size();

        if (tailleListe == 0) {
            Toast toast = Toast.makeText(ListeProduitActivity.this, "Aucun produit sélectionné", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Bundle listeProduit_Bundle = ListeProduitActivity.super.getBundle();
            listeProduit_Bundle.putInt("depotSelectionneID", depot.getDepot_UID());
            listeProduit_Bundle.putIntegerArrayList("ListeProduit", (ArrayList<Integer>) produitDemandeParticuliereAdapter.produitSelect);

            Intent listeProduit_Intent = new Intent(ListeProduitActivity.this, ListeDemandeParticuliereActivity.class);
            listeProduit_Intent.putExtras(listeProduit_Bundle);
            ListeProduitActivity.this.startActivity(listeProduit_Intent);
        }
    }

    // Class permettant la gestion du sélecteur
    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = parent.getSelectedItem().toString();

            if (item.equals("Tous")) { //Trie par ordre alpabéthique
                Collections.sort(produitList, new Comparator<Produit>() {
                    @Override
                    public int compare(Produit p1, Produit p2) {
                        return p1.getDesignation_interne().compareTo(p2.getDesignation_interne());
                    }
                });
            } else if (item.equals("Classification")) { // Trie par classification : Médicament ou Dispositif
                Collections.sort(produitList, new Comparator<Produit>() {
                    @Override
                    public int compare(Produit p1, Produit p2) {
                        return String.valueOf(p1.getClasse_numero()).compareTo(String.valueOf(p2.getClasse_numero()));
                    }
                });

            } else if (item.equals("Catégorie")) { // Trie par catégorie : EPO, ...
                Collections.sort(produitList, new Comparator<Produit>() {
                    @Override
                    public int compare(Produit p1, Produit p2) {
                        return p1.getCategorie().compareTo(p2.getCategorie());
                    }
                });
            } else {
                Collections.sort(produitList, new Comparator<Produit>() {
                    @Override
                    public int compare(Produit p1, Produit p2) { // Trie par Nom de Fournisseur
                        return p1.getFournisseur().compareTo(p2.getFournisseur());
                    }
                });
            }

            Iterator<Produit> iterator = produitList.iterator();
            while (iterator.hasNext()) {
                Produit p = iterator.next();
                if (p.isArret_Dis()) {
                    iterator.remove();
                }
            }
            List<Integer> tempList;
            tempList = new ArrayList<>();

            // Affichage du nombre de produit
            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(produitList.size()));

            if (back) {
                tempList = intent.getIntegerArrayListExtra("ListeProduit");
            }

            // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
            produitDemandeParticuliereAdapter = new Produit_DemandeParticuliereAdapter(ListeProduitActivity.this, produitList, nb_prod_selectTextView, tempList);
            produitListView.setAdapter(produitDemandeParticuliereAdapter);

            invalidateOptionsMenu();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
