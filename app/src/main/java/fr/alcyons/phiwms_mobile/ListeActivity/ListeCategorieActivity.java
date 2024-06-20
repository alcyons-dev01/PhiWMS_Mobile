package fr.alcyons.phiwms_mobile.ListeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.ListViewAdapters.CategorieAdapter;
import fr.alcyons.phiwms_mobile.Services.ServiceMedicamentAuLivretActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
public class ListeCategorieActivity extends ServiceActivity {

    CategorieAdapter categorieAdapter;
    ListView categorie_ListView;

    String categorie_Selectionne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        // Récupération des fournisseurs par rapport à la classeNumeroProduit ( Médicament ou Dispositif)
        List<String> categories = ProduitOpenHelper.getAllCategories(db, intent.getExtras().getString("produitClasse_numero"));

        // Récupération de la listeView des zones
        categorie_ListView = (ListView) findViewById(R.id.listeView);

        // Récupération de la liste des zones à afficher

        // Creation de l'adapter qui va gérer la liste des zones
        categorieAdapter = new CategorieAdapter(ListeCategorieActivity.this, categories);

        // Modifier le nombre de zones trouvées
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(categorieAdapter.getCount()));

        // Remplir la vue
        categorie_ListView.setAdapter(categorieAdapter);

        categorie_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categorie_Selectionne = (String) categorieAdapter.getItem(position);

                Intent listeCategorie_Intent = new Intent();
                Bundle listeCategorie_Bundle = ListeCategorieActivity.super.getBundle();
                listeCategorie_Bundle.putString("categorie_Selectionne", categorie_Selectionne);
                listeCategorie_Intent.putExtras(listeCategorie_Bundle);
                setResult(ServiceMedicamentAuLivretActivity.RESULT_OK, listeCategorie_Intent);

                ListeCategorieActivity.this.finish();
            }
        });

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, categorieAdapter, null, "Nom catégorie...");
        return true;
    }
}