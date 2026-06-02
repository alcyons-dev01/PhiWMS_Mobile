package fr.alcyons.phiwms_mobile.IdentificationParScan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.MenuItemCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Produit_IdentificationOpenHelper;
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
    boolean rechercheAuto = false;
    Menu optionsMenu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_identification_par_scan);
        imm = (InputMethodManager) ListeProduitsIdentificationParScanActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        listViewProduits = findViewById(R.id.listeView);
        listeAAfficher = new ArrayList<>();

        if (intent.getExtras().getString("codeGS1") != null) {
            final String codeGS1 = intent.getExtras().getString("codeGS1");
            listeAAfficher = ProduitOpenHelper.getProduitsParGTINAvecSansAI(db, codeGS1);
            if (listeAAfficher.isEmpty()) {
                listeAAfficher = ProduitOpenHelper.getProduitsParGTIN(db, codeGS1.substring(2));
            }
        } else if (intent.getExtras().getString("codeInconnue") != null) {
            codeInconnue = intent.getExtras().getString("codeInconnue");
            listeAAfficher = ProduitOpenHelper.getProduitsParCodeInconnue(db, codeInconnue);
        } else {
            ((LinearLayout) findViewById(R.id.linearProduitIdentifie)).setAlpha(0.5F);
            listeAAfficher = ProduitOpenHelper.getAllProduits(db);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ListeProduitsIdentificationParScanActivity.this, NavigationActivity.class);
                Bundle extras = ListeProduitsIdentificationParScanActivity.this.getBundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ListeProduitsIdentificationParScanActivity.this.startActivity(intent);
                ListeProduitsIdentificationParScanActivity.this.finish();
            }
        });

        int nbProduitBase = ProduitOpenHelper.getNbProduit(db);
        int nbProduitIdentifierDistinct = Produit_IdentificationOpenHelper.getNbProduitIdentifier(db);


        ((TextView) findViewById(R.id.nbIdentifier)).setText(String.valueOf(nbProduitIdentifierDistinct));
        ((TextView) findViewById(R.id.nbNonIdentifier)).setText(String.valueOf(nbProduitBase - nbProduitIdentifierDistinct));

        ((LinearLayout) findViewById(R.id.linearProduitIdentifie)).setOnClickListener(v -> {
            listeAAfficher = ProduitOpenHelper.getProduitsIdentifier(db);
            adapter.replaceData(listeAAfficher);
            ((LinearLayout) findViewById(R.id.linearProduitIdentifie)).setAlpha(1F);
            ((LinearLayout) findViewById(R.id.linearProduitNonIdentifie)).setAlpha(0.5F);
            listViewProduits.smoothScrollToPositionFromTop(0, 0, 250);
            ouvrirRechercheAuto();
        });

        ((LinearLayout) findViewById(R.id.linearProduitNonIdentifie)).setOnClickListener(v -> {
            listeAAfficher = ProduitOpenHelper.getProduitsNonIdentifier(db);
            adapter.replaceData(listeAAfficher);
            ((LinearLayout) findViewById(R.id.linearProduitNonIdentifie)).setAlpha(1F);
            ((LinearLayout) findViewById(R.id.linearProduitIdentifie)).setAlpha(0.5F);
            listViewProduits.smoothScrollToPositionFromTop(0, 0, 250);
            ouvrirRechercheAuto();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        if (listeAAfficher.size() == 1) {
            passerAuDetailProduit(listeAAfficher.get(0));
        } else if (listeAAfficher.size() > 1) {
            rechercheAuto = true;
        } else {
            listeAAfficher = ProduitOpenHelper.getAllProduits(db);
            rechercheAuto = true;
        }

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
        //Intent newIntent = new Intent(ListeProduitsIdentificationParScanActivity.this, DetailProduitIdentificationParScanActivity.class);
        Intent newIntent = new Intent(ListeProduitsIdentificationParScanActivity.this, DetailIdentificationParScan.class);
        Bundle extras = ListeProduitsIdentificationParScanActivity.super.getBundle();
        extras.putInt("produitSelectionneID", produitSelectionne.getID_produit());
        String codeGS1 = Objects.requireNonNull(intent.getExtras()).getString("codeGS1");
        extras.putString("codeGS1", codeGS1);
        extras.putString("codeInconnue", codeInconnue);
        newIntent.putExtras(extras);
        ListeProduitsIdentificationParScanActivity.this.startActivity(newIntent);
        ListeProduitsIdentificationParScanActivity.this.finish();
        listeAAfficher = new ArrayList<>();
        listeAAfficher.add(produitSelectionne);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Désignation, Référence, Fournisseur");
        this.optionsMenu = menu;

        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> {
            retourScan();
            return true;
        });

        // 👇 Ouverture automatique de la barre de recherche si rechercheAuto est vrai
        if (rechercheAuto) {
            rechercheAuto = false; // évite la répétition sur chaque invalidate
            getWindow().getDecorView().post(() -> ouvrirRechercheAuto());
        }

        return true;
    }

    private void ouvrirRechercheAuto() {
        if (optionsMenu == null) return; // sécurité
        MenuItem searchItem = optionsMenu.findItem(R.id.rechercheMenu);
        if (searchItem != null) {
            searchItem.expandActionView();
            androidx.appcompat.widget.SearchView searchView =
                    (androidx.appcompat.widget.SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.post(() -> {
                    searchView.requestFocus();
                    imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT);
                });
            }
        }
    }

    private void fermerRecherche() {
        if (optionsMenu == null) return;
        MenuItem searchItem = optionsMenu.findItem(R.id.rechercheMenu);
        if (searchItem != null && searchItem.isActionViewExpanded()) {
            androidx.appcompat.widget.SearchView searchView =
                    (androidx.appcompat.widget.SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setQuery("", false); // 👈 vide le texte sans déclencher le filtre
                searchView.clearFocus();
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 👈 ferme le clavier
            }
            searchItem.collapseActionView(); // 👈 ferme la barre de recherche
        }
    }

    private void retourScan() {
        Intent newIntent = new Intent(ListeProduitsIdentificationParScanActivity.this, ServiceIdentificationParScanActivity.class);
        newIntent.putExtras(ListeProduitsIdentificationParScanActivity.super.getBundle());
        ListeProduitsIdentificationParScanActivity.this.startActivity(newIntent);
        ListeProduitsIdentificationParScanActivity.this.finish();
    }
}