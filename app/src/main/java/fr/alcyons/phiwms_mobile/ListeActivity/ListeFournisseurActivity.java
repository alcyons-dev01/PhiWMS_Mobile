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
import fr.alcyons.phiwms_mobile.ListViewAdapters.FournisseurAdapter;
import fr.alcyons.phiwms_mobile.Services.ServiceMedicamentAuLivretActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ListeFournisseurActivity extends ServiceActivity {

    FournisseurAdapter fournisseurAdapter;
    ListView fournisseur_ListView;

    String fournisseur_Selectionne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        // Récupération des fournisseurs par rapport à la classeNumeroProduit ( Médicament ou Dispositif)
        List<String> fournisseur_List = ProduitOpenHelper.getAllFournisseurs(db, intent.getExtras().getString("produitClasse_numero"));

        // Récupération de la listeView des zones
        fournisseur_ListView = (ListView) findViewById(R.id.listeView);

        // Récupération de la liste des zones à afficher

        // Creation de l'adapter qui va gérer la liste des zones
        fournisseurAdapter = new FournisseurAdapter(ListeFournisseurActivity.this, fournisseur_List);

        // Modifier le nombre de zones trouvées
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(fournisseurAdapter.getCount()));

        // Remplir la vue
        fournisseur_ListView.setAdapter(fournisseurAdapter);

        fournisseur_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fournisseur_Selectionne = (String) fournisseurAdapter.getItem(position);

                Intent listeFournisseur_Intent = new Intent();
                Bundle listeFournisseur_Bundle = ListeFournisseurActivity.super.getBundle();
                listeFournisseur_Bundle.putString("fournisseur_Selectionne", fournisseur_Selectionne);
                listeFournisseur_Intent.putExtras(listeFournisseur_Bundle);
                setResult(ServiceMedicamentAuLivretActivity.RESULT_OK, listeFournisseur_Intent);

                ListeFournisseurActivity.this.finish();
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
        super.prepareOptionsMenu(menu, fournisseurAdapter, null, "Nom fournisseur...");
        return true;
    }
}