package fr.alcyons.phiwms_mobile.DispositifAuLivret;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.view.menu.ActionMenuItemView;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.DispositifAdapter;
import fr.alcyons.phiwms_mobile.ListeActivity.ListeCategorieActivity;
import fr.alcyons.phiwms_mobile.ListeActivity.ListeFournisseurActivity;
import fr.alcyons.phiwms_mobile.MedicamentAuLivret.ServiceMedicamentAuLivretActivity;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;


public class ServiceDispositifAuLivretActivity extends ServiceActivity {

    DispositifAdapter dispositifAdapter;
    ListView dispositifListView;
    List<Produit> dispositifList;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton boutonRechercheNom;
    FloatingActionButton fabTriFournisseur;
    FloatingActionButton fabTriCategorie;
    FloatingActionButton boutonRecherche;
    ActivityResultLauncher<Intent> activityResultLauncherGS1;
    ActivityResultLauncher<Intent> activityResultLauncherFournisseur;
    ActivityResultLauncher<Intent> activityResultLauncherCategorie;
    PackageManager pm;

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        // Récupération de la liste des dispositifs
        if (dispositifList == null) {
            dispositifList = ProduitOpenHelper.getAllDispositifs(db);
            dispositifAdapter.notifyDataSetChanged();
        } else {
            dispositifAdapter = new DispositifAdapter(ServiceDispositifAuLivretActivity.this, dispositifList);
            dispositifListView.setAdapter(dispositifAdapter);
        }
        // Afficher le nombre de dispositifs
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(dispositifList.size()));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_floating);

        //gestion du package manager
        pm = ServiceDispositifAuLivretActivity.this.getPackageManager();

        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);

        // Récupération de la liste des dispositifs
        if (dispositifList == null) {
            dispositifList = ProduitOpenHelper.getAllDispositifs(db);
        }

        // Afficher le nombre de dispositifs
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(dispositifList.size()));
        ((TextView) findViewById(R.id.titre)).setText("Dispositif Médical");

        // Création de l'adapter
        dispositifAdapter = new DispositifAdapter(ServiceDispositifAuLivretActivity.this, dispositifList);

        // Affichage de la liste
        dispositifListView = (ListView) findViewById(R.id.listeView);
        dispositifListView.setDivider(footer);
        dispositifListView.setAdapter(dispositifAdapter);

        //Récupérer le bouton pour activer la recherche par nom
        boutonRechercheNom = (FloatingActionButton) findViewById(R.id.boutonRechercheNom);
        boutonRechercheNom.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                ((ActionMenuItemView) findViewById(R.id.rechercheMenu)).callOnClick();
                floatingActionMenu.close(true);
            }
        });

        // Récupérer le bouton de filtrage des médicaments par fournisseur et le gérer
        fabTriFournisseur = (FloatingActionButton) findViewById(R.id.fabTriFournisseur);
        fabTriFournisseur.setOnClickListener(v -> {
            Intent serviceDispositifAuLivretIntent = new Intent(ServiceDispositifAuLivretActivity.this, ListeFournisseurActivity.class);
            Bundle serviceDispositifAuLivretBundle = ServiceDispositifAuLivretActivity.super.getBundle();
            serviceDispositifAuLivretBundle.putString("produitClasse_numero", "2");
            serviceDispositifAuLivretIntent.putExtras(serviceDispositifAuLivretBundle);
            activityResultLauncherFournisseur.launch(serviceDispositifAuLivretIntent);
            floatingActionMenu.close(true);
        });

        // Récupérer le bouton de filtrage des médicaments par catégorie et le gérer
        fabTriCategorie = (FloatingActionButton) findViewById(R.id.fabTriCategorie);
        fabTriCategorie.setOnClickListener(v -> {

            Intent serviceDispositifAuLivretIntent = new Intent(ServiceDispositifAuLivretActivity.this, ListeCategorieActivity.class);
            Bundle serviceDispositifAuLivretBundle = ServiceDispositifAuLivretActivity.super.getBundle();
            serviceDispositifAuLivretBundle.putString("produitClasse_numero", "2");
            serviceDispositifAuLivretIntent.putExtras(serviceDispositifAuLivretBundle);
            activityResultLauncherCategorie.launch(serviceDispositifAuLivretIntent);
            floatingActionMenu.close(true);
        });

        // Gérer le clic sur un élément
        dispositifListView.setOnItemClickListener((parent, view, position, id) -> {
            Produit dispositif_Selectionne = (Produit) dispositifAdapter.getItem(position);
            appelerDetailDispositif(dispositif_Selectionne);
        });

        // Gérer la recherche par DataMatrix
        boutonRecherche = (FloatingActionButton) findViewById(R.id.boutonRechercheDataMatrix);

        boutonRecherche.setOnClickListener(v -> {

            Intent serviceDispositifAuLivretIntent;
            Bundle scanDispositifBundle = ServiceDispositifAuLivretActivity.super.getBundle();
            scanDispositifBundle.putBoolean("isBoutonSuppressionExistant", true);

            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                serviceDispositifAuLivretIntent = new Intent(ServiceDispositifAuLivretActivity.this, ScannerSearchOnlyActivity.class);
            }
            else
            {
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                {
                    serviceDispositifAuLivretIntent = new Intent(ServiceDispositifAuLivretActivity.this, BarcodeCaptureActivity.class);
                }
                else
                {
                    serviceDispositifAuLivretIntent = new Intent(ServiceDispositifAuLivretActivity.this, ScannerSearchOnlyActivity.class);
                }
            }

            serviceDispositifAuLivretIntent.putExtras(scanDispositifBundle);
            activityResultLauncherGS1.launch(serviceDispositifAuLivretIntent);
            floatingActionMenu.close(true);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(dispositifList.size()));

                if (floatingActionMenu.isOpened()) {
                    floatingActionMenu.close(true);
                    return;
                }
                List<Produit> medicamentsEnBDD_List = ProduitOpenHelper.getAllMedicaments(db);
                if (dispositifList.size() != medicamentsEnBDD_List.size()) {
                    dispositifList = medicamentsEnBDD_List;
                    onResume();
                } else {
                    Intent intent = new Intent(ServiceDispositifAuLivretActivity.this, NavigationActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    intent.putExtras(extras);
                    ServiceDispositifAuLivretActivity.this.startActivity(intent);
                    ServiceDispositifAuLivretActivity.this.finish();
                }
            }
        });

        activityResultLauncherGS1 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == ServiceMedicamentAuLivretActivity.RESULT_OK) {
                        assert data != null;
                        String code = data.getStringExtra("code");
                        assert code != null;
                        if(!code.contentEquals(""))
                        {
                            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                            List<Produit> produit_List;

                            if (gs1Decoupe.size() != 1) {
                                produit_List = ProduitOpenHelper.getMedicamentsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                                if (produit_List.size() == 1) {
                                    appelerDetailDispositif(produit_List.get(0));
                                } else if (produit_List.size() > 1) {
                                    Alerte.afficherAlerte(ServiceDispositifAuLivretActivity.this, "Attention", "Plusieurs médicaments correspondent à ce code", "alerte");
                                    dispositifList = produit_List;
                                    onResume();
                                } else {
                                    Toast toast = Toast.makeText(ServiceDispositifAuLivretActivity.this, "Le produit scanné n'est pas un médicament", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            } else {
                                produit_List = ProduitOpenHelper.getProduitsParCodeInconnue(db, code);
                                if (produit_List.size() == 1) {
                                    appelerDetailDispositif(produit_List.get(0));
                                } else if (produit_List.size() > 1) {
                                    Alerte.afficherAlerte(ServiceDispositifAuLivretActivity.this, "Attention", "Plusieurs médicaments correspondent à ce code", "alerte");
                                    dispositifList = produit_List;
                                    onResume();
                                } else {
                                    Toast toast = Toast.makeText(ServiceDispositifAuLivretActivity.this, "Le produit scanné n'est pas un médicament", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        }
                    }
                });

        activityResultLauncherFournisseur = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == ServiceMedicamentAuLivretActivity.RESULT_OK) {
                        assert data != null;
                        String fournisseur_Selectionne = data.getStringExtra("fournisseur_Selectionne");
                        List<Produit> produit_List;
                        produit_List = ProduitOpenHelper.getMedicamentsParFournisseur(db, fournisseur_Selectionne);
                        if (!produit_List.isEmpty()) {
                            dispositifList = produit_List;
                            dispositifList.sort(Comparator.comparing(Produit::getDesignation_interne));
                            onResume();
                        } else {
                            Toast toast = Toast.makeText(ServiceDispositifAuLivretActivity.this, "Aucun médicament ne correspond", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

        activityResultLauncherCategorie = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == ServiceMedicamentAuLivretActivity.RESULT_OK) {
                        assert data != null;
                        String categorie_Selectionne = data.getStringExtra("categorie_Selectionne");
                        List<Produit> produit_List;
                        produit_List = ProduitOpenHelper.getMedicamentsParCategorie(db, categorie_Selectionne);
                        if (!produit_List.isEmpty()) {
                            dispositifList = produit_List;
                            dispositifList.sort(Comparator.comparing(Produit::getDesignation_interne));
                            onResume();
                        } else {
                            Toast toast = Toast.makeText(ServiceDispositifAuLivretActivity.this, "Aucun médicament ne correspond", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, dispositifAdapter, null, "Désignation produit...");
        return true;
    }

    public void appelerDetailDispositif(Produit dispositifSelectionne) {
        // Appel de l'activité de détail d'un dispositif (fiche d'un dispositif)
        Intent serviceDispositifAuLivretIntent = new Intent(ServiceDispositifAuLivretActivity.this, DetailDispositifAuLivretActivity.class);
        Bundle serviceDispositifAuLivretBundle = new Bundle();
        ArrayList<Integer> produitID_List = new ArrayList<>();

        for (int i = 0; i < dispositifAdapter.getCount(); i++) {
            Produit produit = (Produit) dispositifAdapter.getItem(i);
            assert produit != null;
            produitID_List.add(produit.getID_produit());
        }
        serviceDispositifAuLivretBundle.putIntegerArrayList("produitID_List", produitID_List);
        serviceDispositifAuLivretBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        serviceDispositifAuLivretBundle.putInt("serviceSelectionneID", serviceActuel.getId());
        serviceDispositifAuLivretBundle.putInt("produitID_Selectionne", dispositifSelectionne.getID_produit());
        serviceDispositifAuLivretIntent.putExtras(serviceDispositifAuLivretBundle);
        ServiceDispositifAuLivretActivity.this.startActivity(serviceDispositifAuLivretIntent);
    }
}
