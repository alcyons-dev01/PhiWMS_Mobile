package fr.alcyons.phiwms_mobile.IdentificationParScan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Produit_IdentificationParScanAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceIdentificationParScanActivity;

public class ListeProduitsIdentificationParScanActivity extends ServiceActivity {

    public Produit_IdentificationParScanAdapter adapter;
    List<Produit> listeAAfficher = new ArrayList<>();
    String codeInconnue = null;
    InputMethodManager imm;
    ListView listViewProduits;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);
        imm = (InputMethodManager) ListeProduitsIdentificationParScanActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Récupération de la liste_view à remplir
        listViewProduits = findViewById(R.id.listeView);
        codeInconnue = Objects.requireNonNull(intent.getExtras()).getString("codeInconnue");
        if(codeInconnue == null)
        {
            codeInconnue = "";
        }

        listeAAfficher = new ArrayList<>();

        if (intent.getExtras().getString("codeGS1") != null) {
            final String codeGS1 = intent.getExtras().getString("codeGS1");
            assert codeGS1 != null;
            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeGS1);
            // Récupération et gestion de la liste des produits correspondants en fonction de la longueur de cette liste
            if(gs1Decoupe.size() > 1)
            {
                listeAAfficher = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
            }
            else
            {
                listeAAfficher = ProduitOpenHelper.getProduitsParCodeInconnue(db, codeGS1);
            }
        } else if(codeInconnue != null && !codeInconnue.contentEquals("")){
            // Récupération et gestion de la liste des produits correspondants en fonction de la longueur de cette liste
            listeAAfficher = ProduitOpenHelper.getProduitsParCodeInconnue(db, codeInconnue);
        }

        //gestion de la liste
        if (listeAAfficher.size() == 1) {
            passerAuDetailProduit(listeAAfficher.get(0));
        } else if (listeAAfficher.size() > 1) {
            Alerte.afficherAlerte(ListeProduitsIdentificationParScanActivity.this, "Attention", "Plusieurs produits correspondent à ce code.", "alerte");
        } else {
            listeAAfficher = ProduitOpenHelper.getAllProduits(db);
            if(codeInconnue != null && !codeInconnue.contentEquals(""))
            {
                Toast toast = Toast.makeText(ListeProduitsIdentificationParScanActivity.this, "Aucun produit ne correspond au code fourni", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ListeProduitsIdentificationParScanActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        listeAAfficher = new ArrayList<>();
        listeAAfficher = ProduitOpenHelper.getAllProduits(db);

        // Affichage de la liste
        adapter = new Produit_IdentificationParScanAdapter(ListeProduitsIdentificationParScanActivity.this, listeAAfficher, db);
        listViewProduits.setDivider(footer);
        listViewProduits.setAdapter(adapter);

        listViewProduits.setOnItemClickListener((parent, view, position, id) -> {
            Produit produitSelectionne = (Produit) adapter.getItem(position);
            assert produitSelectionne != null;
            passerAuDetailProduit(produitSelectionne);
        });
        // Mise à jour du nombre de produits
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeAAfficher.size()));
    }

    public void passerAuDetailProduit(Produit produitSelectionne) {
        Intent newIntent = new Intent(ListeProduitsIdentificationParScanActivity.this, DetailProduitIdentificationParScanActivity.class);

        // Récupération des éléments à transmettre à la prochaine activité
        Bundle extras = super.getBundle();
        extras.putInt("produitSelectionneID", produitSelectionne.getID_produit());
        String codeGS1 = Objects.requireNonNull(intent.getExtras()).getString("codeGS1");
        extras.putString("codeGS1", codeGS1);
        extras.putString("codeInconnue", codeInconnue);
        newIntent.putExtras(extras);

        // Appel de la prochaine activité
        ListeProduitsIdentificationParScanActivity.this.startActivity(newIntent);
        listeAAfficher = new ArrayList<>();
        listeAAfficher.add(produitSelectionne);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
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
        item.setOnMenuItemClickListener(item1 -> {
            retourScan();
            return true;
        });
        return true;
    }
    private void retourScan()
    {
        Intent newIntent = new Intent(ListeProduitsIdentificationParScanActivity.this, ServiceIdentificationParScanActivity.class);
        newIntent.putExtras(super.getBundle());
        ListeProduitsIdentificationParScanActivity.this.startActivity(newIntent);
        ListeProduitsIdentificationParScanActivity.this.finish();
    }
}