package fr.alcyons.phiwms_mobile.IdentificationParScan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
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
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceIdentificationParScanActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceReceptionPuiActivity;

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
        setContentView(R.layout.activity_liste_identification_par_scan);
        imm = (InputMethodManager) ListeProduitsIdentificationParScanActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Récupération de la liste_view à remplir
        listViewProduits = findViewById(R.id.listeView);
        listeAAfficher = new ArrayList<>();

        if (intent.getExtras().getString("codeGS1") != null) {
            final String codeGS1 = intent.getExtras().getString("codeGS1");
            listeAAfficher = ProduitOpenHelper.getProduitsParGTIN(db, codeGS1);

        } else if(intent.getExtras().getString("codeInconnue") != null){
            codeInconnue = intent.getExtras().getString("codeInconnue");
            listeAAfficher = ProduitOpenHelper.getProduitsParCodeInconnue(db, codeInconnue);
        }
        else
        {
            listeAAfficher = ProduitOpenHelper.getProduitsIdentifier(db);

        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //ListeProduitsIdentificationParScanActivity.this.finish();
                Intent intent = new Intent(ListeProduitsIdentificationParScanActivity.this, NavigationActivity.class);
                Bundle extras = ListeProduitsIdentificationParScanActivity.this.getBundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ListeProduitsIdentificationParScanActivity.this.startActivity(intent);
                ListeProduitsIdentificationParScanActivity.this.finish();
            }
        });

        int nombreProduitIdentifier = ProduitOpenHelper.getNbProduitIdentifier(db);
        int nombreProduitNonIdentifier = ProduitOpenHelper.getNbProduitNonIdentifier(db);

        ((TextView) findViewById(R.id.nbIdentifier)).setText(String.valueOf(nombreProduitIdentifier));
        ((TextView) findViewById(R.id.nbNonIdentifier)).setText(String.valueOf(nombreProduitNonIdentifier));

        ((LinearLayout) findViewById(R.id.linearProduitIdentifie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeAAfficher = ProduitOpenHelper.getProduitsIdentifier(db);
                adapter.replaceData(listeAAfficher);
                ((LinearLayout) findViewById(R.id.linearProduitIdentifie)).setAlpha(1F);
                ((LinearLayout) findViewById(R.id.linearProduitNonIdentifie)).setAlpha(0.5F);
                listViewProduits.smoothScrollToPositionFromTop(0, 0, 250);
            }
        });

        ((LinearLayout) findViewById(R.id.linearProduitNonIdentifie)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeAAfficher = ProduitOpenHelper.getProduitsNonIdentifier(db);
                adapter.replaceData(listeAAfficher);
                ((LinearLayout) findViewById(R.id.linearProduitNonIdentifie)).setAlpha(1F);
                ((LinearLayout) findViewById(R.id.linearProduitIdentifie)).setAlpha(0.5F);
                listViewProduits.smoothScrollToPositionFromTop(0, 0, 250);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        //gestion de la liste
        if (listeAAfficher.size() == 1) {
            passerAuDetailProduit(listeAAfficher.get(0));
        } else if (listeAAfficher.size() > 1) {
        } else {
            listeAAfficher = ProduitOpenHelper.getProduitsNonIdentifier(db);
        }

        // Affichage de la liste
        if (adapter == null) {
            adapter = new Produit_IdentificationParScanAdapter(this, listeAAfficher, db);
            listViewProduits.setAdapter(adapter);
        } else {
            adapter.replaceData(listeAAfficher);
        }

        listViewProduits.setOnItemClickListener((parent, view, position, id) -> {
            Produit produitSelectionne = (Produit) adapter.getItem(position);
            assert produitSelectionne != null;
            passerAuDetailProduit(produitSelectionne);
        });
    }

    public void passerAuDetailProduit(Produit produitSelectionne) {
        Intent newIntent = new Intent(ListeProduitsIdentificationParScanActivity.this, DetailProduitIdentificationParScanActivity.class);

        // Récupération des éléments à transmettre à la prochaine activité
        Bundle extras = ListeProduitsIdentificationParScanActivity.super.getBundle();
        extras.putInt("produitSelectionneID", produitSelectionne.getID_produit());
        String codeGS1 = Objects.requireNonNull(intent.getExtras()).getString("codeGS1");
        extras.putString("codeGS1", codeGS1);
        extras.putString("codeInconnue", codeInconnue);
        newIntent.putExtras(extras);

        // Appel de la prochaine activité
        ListeProduitsIdentificationParScanActivity.this.startActivity(newIntent);
        ListeProduitsIdentificationParScanActivity.this.finish();
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
        newIntent.putExtras(ListeProduitsIdentificationParScanActivity.super.getBundle());
        ListeProduitsIdentificationParScanActivity.this.startActivity(newIntent);
        ListeProduitsIdentificationParScanActivity.this.finish();
    }
}