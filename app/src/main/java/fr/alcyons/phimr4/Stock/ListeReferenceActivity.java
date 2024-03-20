package fr.alcyons.phimr4.Stock;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ListViewAdapters.Liste_ReferenceAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class ListeReferenceActivity extends ServiceActivity {

    Liste_ReferenceAdapter listeReferenceAdapter;
    List<Produit> listProduit;
    String tri_choisi;
    ArrayAdapter<CharSequence> Spinneradapter;

    Spinner optionTri;
    ListView referenceListeView;

    FloatingActionButton boutonRechercheDataMatrix;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_reference);

        //gestion du package manager
        pm = ListeReferenceActivity.this.getPackageManager();

        //initialisation
        referenceListeView = (ListView) findViewById(R.id.listeView);
        optionTri = (Spinner) findViewById(R.id.optionTri);
        listProduit = new ArrayList<>();

        listProduit = ProduitOpenHelper.getAllProduits(db);
        listeReferenceAdapter = new Liste_ReferenceAdapter(ListeReferenceActivity.this, db, listProduit);
        referenceListeView.setAdapter(listeReferenceAdapter);
        referenceListeView.setDivider(footer);
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listProduit.size()));
        boutonRechercheDataMatrix = ((FloatingActionButton) findViewById(R.id.boutonRechercheDataMatrix));

        tri_choisi= "Désignation";
        Spinneradapter = ArrayAdapter.createFromResource(this, R.array.option_tri_reference, android.R.layout.simple_spinner_item);
        Spinneradapter.setDropDownViewResource(R.layout.spinner_item);
        optionTri.setAdapter(Spinneradapter);

        if (tri_choisi != null) {
            int spinnerPosition = Spinneradapter.getPosition(tri_choisi);
            optionTri.setSelection(spinnerPosition);
        }
        optionTri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                ((TextView) arg0.getChildAt(0)).setVisibility(View.INVISIBLE);
                String optionSelect = arg0.getItemAtPosition(position).toString();
                switch (optionSelect)
                {
                    case "Désignation":
                        onClickTriDesignation();
                        break;

                    case "Catégorie":
                        onClickTriCategorie();
                        break;

                    case "Fournisseur":
                        onClickTriFournisseur();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        referenceListeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Produit produitSelectionne = (Produit) listeReferenceAdapter.getItem(position);

                Bundle serviceStock_Bundle = ListeReferenceActivity.super.getBundle();
                serviceStock_Bundle.putInt("produitID", produitSelectionne.getID_produit());
                serviceStock_Bundle.putInt("depotUID_Selectionne", intent.getExtras().getInt("depotUID_Selectionne"));
                Intent serviceControleRetours_Intent = new Intent(ListeReferenceActivity.this, newDetailStockActivity.class);
                serviceControleRetours_Intent.putExtras(serviceStock_Bundle);
                ListeReferenceActivity.this.startActivity(serviceControleRetours_Intent);
                ListeReferenceActivity.this.finish();
            }
        });

        boutonRechercheDataMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent rechercheProduitIntent = null;
                Bundle rechercheProduiBundle = ListeReferenceActivity.super.getBundle();
                rechercheProduiBundle.putBoolean("isBoutonSuppressionExistant", true);

                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    rechercheProduitIntent = new Intent(ListeReferenceActivity.this, ScannerSearchOnlyActivity.class);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        rechercheProduitIntent = new Intent(ListeReferenceActivity.this, BarcodeCaptureActivity.class);
                    }
                    else
                    {
                        rechercheProduitIntent = new Intent(ListeReferenceActivity.this, ScannerSearchOnlyActivity.class);
                    }
                }

                rechercheProduitIntent.putExtras(rechercheProduiBundle);
                ListeReferenceActivity.this.startActivityForResult(rechercheProduitIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_CODE_GS1: {
                if (resultCode == ListeStockActivity.RESULT_OK) {
                    String code = data.getStringExtra("code");

                    if(!code.contentEquals(""))
                    {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);

                        if (gs1Decoupe.size() > 1) {
                            List<Produit> produitList = gestionnaireProduit.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                            if (produitList.size() == 1) {
                                Produit produit = produitList.get(0);
                                Intent selectionProduitIntent = new Intent(ListeReferenceActivity.this, newDetailStockActivity.class);
                                Bundle selectionProduitBundle = ListeReferenceActivity.super.getBundle();
                                selectionProduitBundle.putInt("depotUID_Selectionne", intent.getExtras().getInt("depotUID_Selectionne"));
                                selectionProduitBundle.putInt("produitID", produit.getID_produit());
                                selectionProduitIntent.putExtras(selectionProduitBundle);
                                ListeReferenceActivity.this.startActivity(selectionProduitIntent);
                            } else if (produitList.size() > 1) {
                                Alerte.afficherAlerte(ListeReferenceActivity.this, "Attention", "Un problème est survenu, impossible d'identifier le produit.", "alerte");
                            } else {
                                Toast toast = Toast.makeText(ListeReferenceActivity.this, "Aucun produit ne correspond à ce code", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(ListeReferenceActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                }
                break;
            }
        }
        invalidateOptionsMenu();
    }

    public void onClickTriDesignation()
    {
        tri_choisi = "Désignation";
        Collections.sort(listeReferenceAdapter.produitList, new Comparator<Produit>() {
            @Override
            public int compare(Produit o1, Produit o2) {
                return o1.getDesignation_interne().compareTo(o2.getDesignation_interne());
            }
        });
        listeReferenceAdapter.notifyDataSetChanged();
    }

    public void onClickTriCategorie()
    {
        tri_choisi = "Catégorie";
        Collections.sort(listeReferenceAdapter.produitList, new Comparator<Produit>() {
            @Override
            public int compare(Produit o1, Produit o2) {
                return o2.getCategorie().compareTo(o1.getCategorie());
            }
        });
        listeReferenceAdapter.notifyDataSetChanged();
    }

    public void onClickTriFournisseur()
    {
        tri_choisi = "Fournisseur";
        Collections.sort(listeReferenceAdapter.produitList, new Comparator<Produit>() {
            @Override
            public int compare(Produit o1, Produit o2) {
                return o1.getFournisseur().compareTo(o2.getFournisseur());
            }
        });
        listeReferenceAdapter.notifyDataSetChanged();
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, listeReferenceAdapter, null, "Désignation produit...");
        return true;
    }

    //gestion du back
    @Override
    public void onBackPressed()
    {
        ListeReferenceActivity.this.finish();
    }
}
