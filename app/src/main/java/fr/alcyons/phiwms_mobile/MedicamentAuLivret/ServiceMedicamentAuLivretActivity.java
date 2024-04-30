package fr.alcyons.phiwms_mobile.MedicamentAuLivret;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.view.menu.ActionMenuItemView;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Collections;
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
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_medicament_au_livret);

        //gestion du package manager
        pm = ServiceMedicamentAuLivretActivity.this.getPackageManager();

        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);

        // Récupération de la liste des médicaments
        if (medicamentList == null) {
            medicamentList = ProduitOpenHelper.getAllMedicaments(db);
        }

        // Afficher le nombre de médicaments
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(medicamentList.size()));

        // Création de l'adapter et Affichage de la liste
        medicamentAdapter = new MedicamentAdapter(ServiceMedicamentAuLivretActivity.this, medicamentList);
        medicamentListView = (ListView) findViewById(R.id.listeView);
        medicamentListView.setDivider(footer);
        medicamentListView.setAdapter(medicamentAdapter);

        //Récupérer le bouton pour activer la recherche par nom
        boutonRechercheNom = (FloatingActionButton) findViewById(R.id.boutonRechercheNom);
        boutonRechercheNom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionMenuItemView actionMenuItemView = (ActionMenuItemView) findViewById(R.id.rechercheMenu);

                if (actionMenuItemView != null) {
                    actionMenuItemView.callOnClick();
                }
                floatingActionMenu.close(true);
            }
        });

        // Récupérer le bouton de filtrage des médicaments par fournisseur et le gérer
        fabTriFournisseur = (FloatingActionButton) findViewById(R.id.fabTriFournisseur);
        fabTriFournisseur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, ListeFournisseurActivity.class);
                Bundle serviceMedicamentAuLivretBundle = ServiceMedicamentAuLivretActivity.super.getBundle();
                serviceMedicamentAuLivretBundle.putString("produitClasse_numero", "1");
                serviceMedicamentAuLivretIntent.putExtras(serviceMedicamentAuLivretBundle);
                ServiceMedicamentAuLivretActivity.this.startActivityForResult(serviceMedicamentAuLivretIntent, CodesEchangesActivites.RETOUR_NOM_FOURNISSEUR);
                floatingActionMenu.close(true);
            }
        });

        // Récupérer le bouton de filtrage des médicaments par catégorie et le gérer
        fabTriCategorie = (FloatingActionButton) findViewById(R.id.fabTriCategorie);
        fabTriCategorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, ListeCategorieActivity.class);
                Bundle serviceMedicamentAuLivretBundle = ServiceMedicamentAuLivretActivity.super.getBundle();
                serviceMedicamentAuLivretBundle.putString("produitClasse_numero", "1");
                serviceMedicamentAuLivretIntent.putExtras(serviceMedicamentAuLivretBundle);
                ServiceMedicamentAuLivretActivity.this.startActivityForResult(serviceMedicamentAuLivretIntent, CodesEchangesActivites.RETOUR_NOM_CATEGORIE);
                floatingActionMenu.close(true);
            }
        });

        // Gérer le clic sur un élément
        medicamentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Produit medicament_Selectionne = (Produit) medicamentAdapter.getItem(position);
                appelerDetailMedicament(medicament_Selectionne);
            }
        });

        // Gérer la recherche par DataMatrix
        boutonRechercheDataMatrix = (FloatingActionButton) findViewById(R.id.boutonRechercheDataMatrix);


            boutonRechercheDataMatrix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent serviceMedicamentAuLivretIntent = null;
                    Bundle scanMedicamentBundle = ServiceMedicamentAuLivretActivity.super.getBundle();
                    scanMedicamentBundle.putBoolean("isBoutonSuppressionExistant", true);

                    if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                    {
                        serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, ScannerSearchOnlyActivity.class);
                    }
                    else
                    {
                        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                        {
                            serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, BarcodeCaptureActivity.class);
                        }
                        else
                        {
                            serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, ScannerSearchOnlyActivity.class);
                        }
                    }

                    serviceMedicamentAuLivretIntent.putExtras(scanMedicamentBundle);
                    ServiceMedicamentAuLivretActivity.this.startActivityForResult(serviceMedicamentAuLivretIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                    floatingActionMenu.close(true);
                }

            });

    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_CODE_GS1: {
                if (resultCode == ServiceMedicamentAuLivretActivity.RESULT_OK) {
                    String code = data.getStringExtra("code");
                    if(!code.contentEquals(""))
                    {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                        List<Produit> produit_List = new ArrayList<>();

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
                break;
            }
            case CodesEchangesActivites.RETOUR_NOM_FOURNISSEUR: {
                if (resultCode == ServiceMedicamentAuLivretActivity.RESULT_OK) {
                    String fournisseur_Selectionne = data.getStringExtra("fournisseur_Selectionne");
                    List<Produit> produit_List = new ArrayList<>();
                    produit_List = ProduitOpenHelper.getMedicamentsParFournisseur(db, fournisseur_Selectionne);
                    if (produit_List.size() >= 1) {
                        medicamentList = produit_List;
                        Collections.sort(medicamentList, new Comparator<Produit>() {
                            @Override
                            public int compare(Produit o1, Produit o2) {
                                return o1.getDesignation_interne().compareTo(o2.getDesignation_interne());
                            }
                        });
                        onResume();
                    } else {
                        Toast toast = Toast.makeText(ServiceMedicamentAuLivretActivity.this, "Aucun médicament ne correspond", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                break;
            }
            case CodesEchangesActivites.RETOUR_NOM_CATEGORIE: {
                if (resultCode == ServiceMedicamentAuLivretActivity.RESULT_OK) {
                    String categorie_Selectionne = data.getStringExtra("categorie_Selectionne");
                    List<Produit> produit_List = new ArrayList<>();
                    produit_List = ProduitOpenHelper.getMedicamentsParCategorie(db, categorie_Selectionne);
                    if (produit_List.size() >= 1) {
                        medicamentList = produit_List;
                        Collections.sort(medicamentList, new Comparator<Produit>() {
                            @Override
                            public int compare(Produit o1, Produit o2) {
                                return o1.getDesignation_interne().compareTo(o2.getDesignation_interne());
                            }
                        });
                        onResume();
                    } else {
                        Toast toast = Toast.makeText(ServiceMedicamentAuLivretActivity.this, "Aucun médicament ne correspond", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                break;
            }
        }
        invalidateOptionsMenu();
    }

    // Lance l'activity avec le produit sélectionné
    public void appelerDetailMedicament(Produit medicamentSelectionne) {
        Intent serviceMedicamentAuLivretIntent = new Intent(ServiceMedicamentAuLivretActivity.this, DetailMedicamentAuLivretActivity.class);
        Bundle serviceMedicamentAuLivretBundle = super.getBundle();
        List<Integer> produitID_List = new ArrayList<>();

        for (int i = 0; i < medicamentAdapter.getCount(); i++) {
            Produit produit = (Produit) medicamentAdapter.getItem(i);
            produitID_List.add(produit.getID_produit());
        }
        serviceMedicamentAuLivretBundle.putIntegerArrayList("produitID_List", (ArrayList<Integer>) produitID_List);
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

    // Ferme le floatingActionMenu si ouvert sinon arrete l'activity
    @Override
    public void onBackPressed() {
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
            super.onBackPressed();
        }
    }
}
