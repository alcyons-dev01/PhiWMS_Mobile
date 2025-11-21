package fr.alcyons.phiwms_mobile.Inventaire;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.Inventaire;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.ListViewAdapters.DetailInventaireAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.PreparationPUFetPAD.DetailPreparationActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class DetailInventaireActivity extends ServiceAvecConnexionActivity {
    Context context;
    Inventaire inventaireCourant;
    List<Inventaire_Ligne_Temp> inventaireLigneTempList;
    String zoneCourante;
    ListView inventaireListView;
    DetailInventaireAdapter adapter;

    MenuItem valider_item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_inventaire);
        context = DetailInventaireActivity.this;

        // Récupération de l'inventaire courant depuis les extras de l'intent
        inventaireCourant = InventaireOpenHelper.getInventaireById(db, intent.getExtras().getInt("inventaireId"));
        zoneCourante = intent.getExtras().getString("zoneSelectionne");

        ((TextView) findViewById(R.id.intitule)).setText(inventaireCourant.getObjet());
        ((TextView) findViewById(R.id.zone)).setText(zoneCourante);
        inventaireListView = (ListView) findViewById(R.id.listeView);

        inventaireListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String[] ligneSelectionnee = (String[]) adapterView.getItemAtPosition(position);

                Intent serviceInventaire_Intent = new Intent(DetailInventaireActivity.this, ListeLotInventaireActivity.class);
                Bundle serviceInventaire_Bundle = DetailInventaireActivity.super.getBundle();
                serviceInventaire_Bundle.putInt("inventaireId", inventaireCourant.getInventaire_ID());
                serviceInventaire_Bundle.putString("zoneSelectionne", zoneCourante);
                serviceInventaire_Bundle.putInt("produitId", Integer.parseInt(ligneSelectionnee[5]));
                serviceInventaire_Intent.putExtras(serviceInventaire_Bundle);
                DetailInventaireActivity.this.startActivity(serviceInventaire_Intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        inventaireLigneTempList = Inventaire_Ligne_TempOpenHelper.getAllInventaireLigneTempByInventaireEtZone(db, inventaireCourant.getInventaire_ID(), zoneCourante);

        List<String[]> tabInventaireLigneTemp = new ArrayList<>();

        String[] tabTemp = new String[8];
        int compteur = 0;
        String produitPrecedent = "";
        int qteStockTheorique = 0;
        int qteStockSaisie = -1;
        String designation = "";
        String reference = "";
        String fournisseur = "";
        String idProduit = "";
        boolean complet = true;
        for (Inventaire_Ligne_Temp ligne : inventaireLigneTempList) {
            compteur++;

            if (ligne.getDesignation().contentEquals(produitPrecedent))
            {
                if(compteur == inventaireLigneTempList.size())
                {
                    if(!ligne.getInventaireDate().contentEquals("") && !ligne.getInventaireDate().contentEquals("null")  && !ligne.getInventaireDate().contentEquals("0000-00-00"))
                    {
                        if(qteStockSaisie == -1)
                            qteStockSaisie = 0;
                        qteStockSaisie += ligne.getStockPhysique();
                    }
                    else
                    {
                        complet = false;
                    }

                    tabTemp[0] = reference;
                    tabTemp[1] = designation;
                    tabTemp[2] = fournisseur;
                    tabTemp[3] = String.valueOf(qteStockTheorique);
                    tabTemp[4] = String.valueOf(qteStockSaisie);
                    tabTemp[5] = idProduit;
                    tabTemp[6] = String.valueOf(complet);

                    tabInventaireLigneTemp.add(tabTemp);
                }
                else
                {
                    qteStockTheorique += ligne.getStockTheorique();
                    if(!ligne.getInventaireDate().contentEquals("") && !ligne.getInventaireDate().contentEquals("null")  && !ligne.getInventaireDate().contentEquals("0000-00-00"))
                    {
                        if(qteStockSaisie == -1)
                            qteStockSaisie = 0;
                        qteStockSaisie += ligne.getStockPhysique();
                    }
                    else
                    {
                        complet = false;
                    }
                }
            }
            else
            {
                if(!produitPrecedent.contentEquals(""))
                {
                    tabTemp[0] = reference;
                    tabTemp[1] = designation;
                    tabTemp[2] = fournisseur;
                    tabTemp[3] = String.valueOf(qteStockTheorique);
                    tabTemp[4] = String.valueOf(qteStockSaisie);
                    tabTemp[5] = idProduit;
                    tabTemp[6] = String.valueOf(complet);

                    tabInventaireLigneTemp.add(tabTemp);
                }
                complet = true;
                produitPrecedent = ligne.getDesignation();
                designation = ligne.getDesignation();
                reference = ligne.getProduitReference();
                fournisseur = ligne.getFournisseurNom();
                idProduit = String.valueOf(ligne.getProduitID());
                tabTemp = new String[8];
                qteStockTheorique = 0;
                qteStockSaisie = -1;
                qteStockTheorique += ligne.getStockTheorique();
                if(!ligne.getInventaireDate().contentEquals("") && !ligne.getInventaireDate().contentEquals("null") && !ligne.getInventaireDate().contentEquals("0000-00-00"))
                {
                    if(qteStockSaisie == -1)
                        qteStockSaisie = 0;
                    qteStockSaisie += ligne.getStockPhysique();
                }
                else
                {
                    complet = false;
                }

                if(compteur == inventaireLigneTempList.size())
                {
                    tabTemp[0] = reference;
                    tabTemp[1] = designation;
                    tabTemp[2] = fournisseur;
                    tabTemp[3] = String.valueOf(qteStockTheorique);
                    tabTemp[4] = String.valueOf(qteStockSaisie);
                    tabTemp[5] = idProduit;
                    tabTemp[6] = String.valueOf(complet);

                    tabInventaireLigneTemp.add(tabTemp);
                }
            }
        }

        adapter = new DetailInventaireAdapter(DetailInventaireActivity.this, tabInventaireLigneTemp, db);
        inventaireListView.setAdapter(adapter);

        invalidateOptionsMenu();
        verificationEtatInvtentaire();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_preparation_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, null, null, "Désignation référence");

        valider_item = menu.findItem(R.id.boutonValider);
        valider_item.setOnMenuItemClickListener(menuItem -> {
            Random randomaction = new Random();
            int actionId = randomaction.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateDestruction =new Date();
            String date_string = parseFormat.format(dateDestruction);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", inventaireCourant.getInventaire_ID(), "", "Inventaire Partiel à traiter");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            ElementASynchroniserOpenHelper.toutSynchroniser(DetailInventaireActivity.this, db, utilisateurConnecte, false);
            DetailInventaireActivity.this.finish();
            return true;
        });

        return true;
    }

    public void verificationEtatInvtentaire()
    {
        boolean inventaireComplet = true;
        for (Inventaire_Ligne_Temp ligne : inventaireLigneTempList) {
            if(ligne.getInventaireDate().contentEquals("") || ligne.getInventaireDate().contentEquals("null") || ligne.getInventaireDate().contentEquals("0000-00-00"))
            {
                inventaireComplet = false;
                break;
            }
        }

        if(valider_item != null)
        {
            if(inventaireComplet)
                valider_item.setVisible(true);
            else
                valider_item.setVisible(false);
        }
    }
}
