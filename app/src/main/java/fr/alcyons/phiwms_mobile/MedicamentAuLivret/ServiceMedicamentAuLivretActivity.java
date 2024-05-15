package fr.alcyons.phiwms_mobile.MedicamentAuLivret;

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
import fr.alcyons.phiwms_mobile.ListViewAdapters.MedicamentAdapter;
import fr.alcyons.phiwms_mobile.ListeActivity.ListeCategorieActivity;
import fr.alcyons.phiwms_mobile.ListeActivity.ListeFournisseurActivity;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ServiceMedicamentAuLivretActivity extends ServiceActivity {

    MedicamentAdapter medicamentAdapter;
    ListView medicamentListView;
    List<Produit> medicamentList;

    FloatingActionMenu floatingActionMenu;
    FloatingActionButton boutonRechercheNom;
    FloatingActionButton fabTriFournisseur;
    FloatingActionButton fabTriCategorie;
    FloatingActionButton boutonRechercheDataMatrix;

    PackageManager pm;
    ActivityResultLauncher<Intent> activityResultLauncherGS1;
    ActivityResultLauncher<Intent> activityResultLauncherFournisseur;
    ActivityResultLauncher<Intent> activityResultLauncherCategorie;


    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        // Récupération de la liste des médicaments
        if (medicamentList == null) {
            medicamentList = ProduitOpenHelper.getAllMedicaments(db);
            medicamentAdapter.notifyDataSetChanged();
        } else {
            medicamentAdapter = new MedicamentAdapter(ServiceMedicamentAuLivretActivity.this, medicamentList);
            medicamentListView.setAdapter(medicamentAdapter);
        }

        // Afficher le nombre de médicaments
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(medicamentList.size()));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_floating);

        //gestion du package manager
        pm = ServiceMedicamentAuLivretActivity.this.getPackageManager();

        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);

        // Récupération de la liste des médicaments
        if (medicamentList == null) {
            medicamentList = ProduitOpenHelper.getAllMedicaments(db);
        }

        // Afficher le nombre de médicaments
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(medicamentList.size()));
        ((TextView) findViewById(R.id.titre)).setText("Médicaments au livret");

        // Création de l'adapter et Affichage de la liste
        medicamentAdapter = new MedicamentAdapter(ServiceMedicamentAuLivretActivity.this, medicamentList);
        medicamentListView = (ListView) findViewById(R.id.listeView);
        medicamentListView.setDivider(footer);
        medicamentListView.setAdapter(medicamentAdapter);

        //Récupérer le bouton pour activer la recherche par nom
        boutonRechercheNom = (FloatingActionButton) findViewById(R.id.boutonRechercheNom);
        boutonRechercheNom.setOnClickListener(v -> {
            @SuppressLint("RestrictedApi") ActionMenuItemView actionMenuItemView = (ActionMenuItemView) findViewById(R.id.rechercheMenu);

            if (actionMenuItemView != null) {
                actionMenuItemView.callOnClick();
            }
            floatingActionMenu.close(true);
        });

        // Récupérer le bouton de filtrage des médicaments par fournisseur et le gérer
        fabTriFournisseur = (FloatingActionButton) findViewById(R.id.fabTriFournisseur);
        fabTriFournisseur.setOnClickListener(v -> {
            Intent serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, ListeFournisseurActivity.class);
            Bundle serviceMedicamentAuLivretBundle = ServiceMedicamentAuLivretActivity.super.getBundle();
            serviceMedicamentAuLivretBundle.putString("produitClasse_numero", "1");
            serviceMedicamentAuLivretIntent.putExtras(serviceMedicamentAuLivretBundle);
            activityResultLauncherFournisseur.launch(serviceMedicamentAuLivretIntent);
            floatingActionMenu.close(true);
        });

        // Récupérer le bouton de filtrage des médicaments par catégorie et le gérer
        fabTriCategorie = (FloatingActionButton) findViewById(R.id.fabTriCategorie);
        fabTriCategorie.setOnClickListener(v -> {
            Intent serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, ListeCategorieActivity.class);
            Bundle serviceMedicamentAuLivretBundle = ServiceMedicamentAuLivretActivity.super.getBundle();
            serviceMedicamentAuLivretBundle.putString("produitClasse_numero", "1");
            serviceMedicamentAuLivretIntent.putExtras(serviceMedicamentAuLivretBundle);
            activityResultLauncherCategorie.launch(serviceMedicamentAuLivretIntent);
            floatingActionMenu.close(true);
        });

        // Gérer le clic sur un élément
        medicamentListView.setOnItemClickListener((parent, view, position, id) -> {
            Produit medicament_Selectionne = (Produit) medicamentAdapter.getItem(position);
            appelerDetailMedicament(medicament_Selectionne);
        });

        // Gérer la recherche par DataMatrix
        boutonRechercheDataMatrix = (FloatingActionButton) findViewById(R.id.boutonRechercheDataMatrix);

        boutonRechercheDataMatrix.setOnClickListener(v -> {
            Intent serviceMedicamentAuLivretIntent;
            Bundle scanMedicamentBundle = ServiceMedicamentAuLivretActivity.super.getBundle();
            scanMedicamentBundle.putBoolean("isBoutonSuppressionExistant", true);

            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, ScannerSearchOnlyActivity.class);
            }
            else
            {
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                {
                    serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, BarcodeCaptureActivity.class);
                }
                else
                {
                    serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, ScannerSearchOnlyActivity.class);
                }
            }

            serviceMedicamentAuLivretIntent.putExtras(scanMedicamentBundle);
            activityResultLauncherGS1.launch(serviceMedicamentAuLivretIntent);

            floatingActionMenu.close(true);
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
                                    appelerDetailMedicament(produit_List.get(0));
                                } else if (produit_List.size() > 1) {
                                    Alerte.afficherAlerte(ServiceMedicamentAuLivretActivity.this, "Attention", "Plusieurs médicaments correspondent à ce code", "alerte");
                                    medicamentList = produit_List;
                                    onResume();
                                } else {
                                    Toast toast = Toast.makeText(ServiceMedicamentAuLivretActivity.this, "Le produit scanné n'est pas un médicament", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            } else {
                                produit_List = ProduitOpenHelper.getProduitsParCodeInconnue(db, code);
                                if (produit_List.size() == 1) {
                                    appelerDetailMedicament(produit_List.get(0));
                                } else if (produit_List.size() > 1) {
                                    Alerte.afficherAlerte(ServiceMedicamentAuLivretActivity.this, "Attention", "Plusieurs médicaments correspondent à ce code", "alerte");
                                    medicamentList = produit_List;
                                    onResume();
                                } else {
                                    Toast toast = Toast.makeText(ServiceMedicamentAuLivretActivity.this, "Le produit scanné n'est pas un médicament", Toast.LENGTH_LONG);
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
                            medicamentList = produit_List;
                            medicamentList.sort(Comparator.comparing(Produit::getDesignation_interne));
                            onResume();
                        } else {
                            Toast toast = Toast.makeText(ServiceMedicamentAuLivretActivity.this, "Aucun médicament ne correspond", Toast.LENGTH_LONG);
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
                            medicamentList = produit_List;
                            medicamentList.sort(Comparator.comparing(Produit::getDesignation_interne));
                            onResume();
                        } else {
                            Toast toast = Toast.makeText(ServiceMedicamentAuLivretActivity.this, "Aucun médicament ne correspond", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(medicamentList.size()));

                if (floatingActionMenu.isOpened()) {
                    floatingActionMenu.close(true);
                    return;
                }
                List<Produit> medicamentsEnBDD_List = ProduitOpenHelper.getAllMedicaments(db);
                if (medicamentList.size() != medicamentsEnBDD_List.size()) {
                    medicamentList = medicamentsEnBDD_List;
                    onResume();
                } else {
                    Intent intent = new Intent(ServiceMedicamentAuLivretActivity.this, NavigationActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    intent.putExtras(extras);
                    ServiceMedicamentAuLivretActivity.this.startActivity(intent);
                    ServiceMedicamentAuLivretActivity.this.finish();
                }
            }
        });
    }

    // Lance l'activity avec le produit sélectionné
    public void appelerDetailMedicament(Produit medicamentSelectionne) {
        Intent serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, DetailMedicamentAuLivretActivity.class);
        Bundle serviceMedicamentAuLivretBundle = super.getBundle();
        ArrayList<Integer> produitID_List = new ArrayList<>();

        for (int i = 0; i < medicamentAdapter.getCount(); i++) {
            Produit produit = (Produit) medicamentAdapter.getItem(i);
            assert produit != null;
            produitID_List.add(produit.getID_produit());
        }
        serviceMedicamentAuLivretBundle.putIntegerArrayList("produitID_List", produitID_List);
        serviceMedicamentAuLivretBundle.putInt("produitID_Selectionne", medicamentSelectionne.getID_produit());
        serviceMedicamentAuLivretIntent.putExtras(serviceMedicamentAuLivretBundle);
        ServiceMedicamentAuLivretActivity.this.startActivity(serviceMedicamentAuLivretIntent);
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, medicamentAdapter, null, "Désignation produit...");
        return true;
    }
}
