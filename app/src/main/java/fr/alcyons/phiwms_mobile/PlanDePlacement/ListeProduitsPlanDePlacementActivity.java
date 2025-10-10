package fr.alcyons.phiwms_mobile.PlanDePlacement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerEmplacementActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPlanDePlacementActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.IdentificationParScan.DetailProduitIdentificationParScanActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Produit_PlanDePlacementAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServicePlanDePlacementActivity;

public class ListeProduitsPlanDePlacementActivity extends ServiceActivity {

    Produit_PlanDePlacementAdapter adapter;
    List<Produit> produitList = new ArrayList<>();
    List<Produit> produitListScannes = new ArrayList<>();
    ListView produitListView;
    boolean passageParOnCreate;
    boolean placement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_produit_plan_placement);

        // Récupération de la liste_view à remplir
        produitListView = (ListView) findViewById(R.id.listeView);
        produitList = ProduitOpenHelper.getProduitPlace(db);
        produitListScannes.addAll((Collection<? extends Produit>) intent.getExtras().getSerializable("ListProduitScannes"));
        passageParOnCreate = true;
        placement = intent.getExtras().getBoolean("placement");

        ((LinearLayout) findViewById(R.id.linearProduitsPlaces)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) findViewById(R.id.linearProduitsPlaces)).setAlpha(1);
                ((LinearLayout) findViewById(R.id.linearProduitNonPlaces)).setAlpha(0.5F);
                produitList = ProduitOpenHelper.getProduitPlace(db);
                onResume();
            }
        });

        ((LinearLayout) findViewById(R.id.linearProduitNonPlaces)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) findViewById(R.id.linearProduitsPlaces)).setAlpha(0.5F);
                ((LinearLayout) findViewById(R.id.linearProduitNonPlaces)).setAlpha(1);
                produitList = ProduitOpenHelper.getProduitNonPlace(db);
                onResume();
            }
        });

        // Gestion des clics sur les produits
        gestionClickProduit();
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        ((TextView) findViewById(R.id.nbPlaces)).setText(String.valueOf(ProduitOpenHelper.getNbProduitPlace(db)));
        ((TextView) findViewById(R.id.nbNonPlace)).setText(String.valueOf(ProduitOpenHelper.getNbProduitNonPlace(db)));

        adapter = new Produit_PlanDePlacementAdapter(ListeProduitsPlanDePlacementActivity.this, produitList, produitListScannes);
        produitListView.setAdapter(adapter);
        produitListView.setDivider(null);

        if(!placement)
        {
            if(produitListScannes.size() > 0)
                ((Button) findViewById(R.id.boutonAction)).setVisibility(View.VISIBLE);
            else
                ((Button) findViewById(R.id.boutonAction)).setVisibility(View.GONE);

            ((Button) findViewById(R.id.boutonAction)).setText("Placer les "+produitListScannes.size()+" références");
        }

        ((Button) findViewById(R.id.boutonAction)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                produitListView.setOnItemClickListener(null);
                ((Button) findViewById(R.id.boutonAction)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.layoutboutonplacement)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.layoutBoutonPlacer)).setVisibility(View.VISIBLE);

                produitList = new ArrayList<>();
                produitList.addAll(produitListScannes);
                adapter = new Produit_PlanDePlacementAdapter(ListeProduitsPlanDePlacementActivity.this, produitList, produitListScannes);
                produitListView.setAdapter(adapter);
                produitListView.setDivider(null);

                ((Button) findViewById(R.id.boutonRetour)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        placement = false;
                        gestionClickProduit();
                        ((Button) findViewById(R.id.boutonAction)).setVisibility(View.VISIBLE);
                        ((LinearLayout) findViewById(R.id.layoutboutonplacement)).setVisibility(View.VISIBLE);
                        ((LinearLayout) findViewById(R.id.layoutBoutonPlacer)).setVisibility(View.GONE);
                        ((LinearLayout) findViewById(R.id.linearProduitsPlaces)).performClick();
                    }
                });

                ((Button) findViewById(R.id.boutonPlacer)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        placement = true;
                        Intent listeProduitsPlanDePlacementIntent = new Intent(ListeProduitsPlanDePlacementActivity.this, ScannerEmplacementActivity.class);
                        Bundle servicePlanDePlacementBundle = ListeProduitsPlanDePlacementActivity.this.getBundle();
                        listeProduitsPlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);
                        ListeProduitsPlanDePlacementActivity.this.startActivityForResult(listeProduitsPlanDePlacementIntent, CodesEchangesActivites.RETOUR_EMPLACEMENT);
                    }
                });
            }
        });

        passageParOnCreate = false;
    }

    public void passerAuDetailProduit(Produit produitSelectionne) {
        Intent listeProduitsPlanDePlacementIntent = new Intent(ListeProduitsPlanDePlacementActivity.this, DetailProduitPlanDePlacementActivity.class);
        Bundle listeProduitsPlanDePlacementBundle = ListeProduitsPlanDePlacementActivity.super.getBundle();
        listeProduitsPlanDePlacementBundle.putInt("produitSelectionneID", produitSelectionne.getID_produit());
        listeProduitsPlanDePlacementIntent.putExtras(listeProduitsPlanDePlacementBundle);
        ListeProduitsPlanDePlacementActivity.this.startActivity(listeProduitsPlanDePlacementIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
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
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                lancerScan();
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListeProduitsPlanDePlacementActivity.this.finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            ListeProduitsPlanDePlacementActivity.this.finish();
        } else {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    produitListScannes = new ArrayList<>();
                    produitListScannes.addAll((List<Produit>) data.getExtras().getSerializable("ListProduitScannes"));
                    produitList = ProduitOpenHelper.getProduitPlace(db);
                    onResume();
                    break;
                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    int emplacementid = data.getExtras().getInt("emplacementSelectionneID");

                    if(emplacementid != 0)
                    {
                        Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                        Depot_Zone zone = ZoneOpenHelper.getUneZoneByID(db, emplacement.getZoneID());
                        afficherAlerteConfirmation(ListeProduitsPlanDePlacementActivity.this, getLayoutInflater(), emplacement, zone);
                    }
                    break;
                case CodesEchangesActivites.RETOUR_EMPLACEMENT:
                    String emplacement = data.getExtras().getString("code");

                    if(emplacement.contentEquals("") || !emplacement.toUpperCase().startsWith("PHITAGPLACE"))
                    {
                        Intent listeProduitsPlanDePlacementIntent = new Intent(ListeProduitsPlanDePlacementActivity.this, ListeZonesActivity.class);
                        Bundle servicePlanDePlacementBundle = ListeProduitsPlanDePlacementActivity.this.getBundle();
                        listeProduitsPlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);
                        ListeProduitsPlanDePlacementActivity.this.startActivityForResult(listeProduitsPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                    else
                    {
                        String[] tabEmplacement = emplacement.split(":");
                        int emplacement_id = Integer.parseInt(tabEmplacement[tabEmplacement.length-1]);

                        Depot_Emplacement emplacementScanne = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_id);
                        Depot_Zone zoneScanne = ZoneOpenHelper.getUneZoneByID(db, emplacementScanne.getZoneID());

                        afficherAlerteConfirmation(ListeProduitsPlanDePlacementActivity.this, getLayoutInflater(), emplacementScanne, zoneScanne);
                    }

                    break;
            }
        }
    }

    public void lancerScan() {
        passageParOnCreate = true;
        Intent listeProduitsPlanDePlacementIntent = new Intent(ListeProduitsPlanDePlacementActivity.this, ScannerPlanDePlacementActivity.class);
        Bundle servicePlanDePlacementBundle = super.getBundle();
        servicePlanDePlacementBundle.putSerializable("ListProduitScannes", (Serializable) produitListScannes);
        listeProduitsPlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);
        ListeProduitsPlanDePlacementActivity.this.startActivityForResult(listeProduitsPlanDePlacementIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
    }

    private void gestionClickProduit()
    {
        produitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Produit produit = produitList.get(position);
                if(produit.getEmplacement_PUI_Defaut().contentEquals("EMPLACEMENT"))
                {
                    boolean suppression = false;
                    int index = 0;
                    for(Produit temp : produitListScannes)
                    {
                        if(temp.getID_produit() == produit.getID_produit())
                        {
                            produitListScannes.remove(index);
                            suppression = true;
                            break;
                        }

                        index ++;
                    }

                    if(!suppression)
                        produitListScannes.add(produitList.get(position));
                    if(produitListScannes.size() > 0)
                        ((Button) findViewById(R.id.boutonAction)).setVisibility(View.VISIBLE);
                    else
                        ((Button) findViewById(R.id.boutonAction)).setVisibility(View.GONE);

                    ((Button) findViewById(R.id.boutonAction)).setText("Placer les "+produitListScannes.size()+" références");
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater, Depot_Emplacement emplacementRetourne, Depot_Zone zoneEmplacementRetourne) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_quitter, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageFin = (TextView) layout.findViewById(R.id.messageFin);
        messageFin.setText("Placer les produits dans l'emplacement "+emplacementRetourne+" ("+zoneEmplacementRetourne+") ?");

        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(Produit produitAPlace : produitListScannes)
                {
                    produitAPlace.setEmplacement_PUI_Defaut(emplacementRetourne.getAdressage());
                    produitAPlace.setZone_PUI_Defaut(zoneEmplacementRetourne.getZoneName());
                    long rowId = ProduitOpenHelper.mettreAJourProduit(db, produitAPlace);

                    if (rowId != -1) {
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, produitAPlace.getPhiMR4UUID(), produitAPlace.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
                        Random randomaction = new Random();
                        int actionId = randomaction.nextInt();
                        if (actionId > 0)
                            actionId = actionId * -1;
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date dateAction = new Date();
                        String date_string = parseFormat.format(dateAction);
                        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", produitAPlace.getID_produit(), "", "Plan de placement");
                        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

                        Random randomactionligne = new Random();
                        int actionligneId = randomactionligne.nextInt();
                        if (actionligneId > 0)
                            actionligneId = actionligneId * -1;

                        ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Produit", produitAPlace.getID_produit(), produitAPlace.getGTIN(), 0, 0, produitAPlace.getDesignation_interne());
                        ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionUtilisateur_ligne.getPhiMR4UUID(), actionUtilisateur_ligne.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                        ElementASynchroniserOpenHelper.toutSynchroniser(ListeProduitsPlanDePlacementActivity.this, db, utilisateurConnecte, false);
                    }
                }

                produitList = ProduitOpenHelper.getProduitPlace(db);
                placement = false;
                produitListScannes = new ArrayList<>();
                ((Button) findViewById(R.id.boutonAction)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.layoutboutonplacement)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.layoutBoutonPlacer)).setVisibility(View.GONE);
                gestionClickProduit();
                ((LinearLayout) findViewById(R.id.linearProduitsPlaces)).performClick();
                onResume();
                alertDialog.dismiss();
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

}
