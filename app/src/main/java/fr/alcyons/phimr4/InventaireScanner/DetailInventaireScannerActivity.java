package fr.alcyons.phimr4.InventaireScanner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.Inventaire;
import fr.alcyons.phimr4.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ListViewAdapters.Inventaire_Ligne_TempAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.PlanDePlacement.ListeZonesActivity;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class DetailInventaireScannerActivity extends ServiceActivity {

    Depot depot;

    List<Inventaire_Ligne_Temp> inventaireLigneTempList;
    Inventaire_Ligne_TempAdapter adapter;
    Inventaire_Ligne_Temp inventaireLigneTempAModifier;
    ListView inventaireLigneTempListView;

    // Boutons
    FloatingActionMenu floatingMenu;
    FloatingActionButton boutonValider;
    FloatingActionButton boutonScanDatamatrix;

    PackageManager pm;


    // Définition de l'action à réaliser au Click sur le bouton valider
    View.OnClickListener clicBoutonValider = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int compteurReussite = 0;

            Random random = new Random();
            int inventaireID = random.nextInt();
            if (inventaireID > 0) {
                inventaireID = inventaireID * -1;
            }

            //Création de l'action utilisateur
            Random randomaction = new Random();
            int actionId = randomaction.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date =new Date();
            String date_string = parseFormat.format(date);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", inventaireID, "", "Inventaire Scanner");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            //fin de la création de l'action utilisateur

            if (inventaireLigneTempList.size() != 0) {
                for (Inventaire_Ligne_Temp inventaireLigneTemp : inventaireLigneTempList
                        ) {
                    inventaireLigneTemp.setInventaire_ID(inventaireID);
                    long rowID = gestionnaireInventaire_Ligne_Temp.mettreAJourInventaireLigneTemp(db, inventaireLigneTemp);
                    if (rowID != -1) {
                        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, inventaireLigneTemp.getPhiMR4UUID(), 0, DBOpenHelper.ActionsEAS.AJOUT);
                        compteurReussite++;
                        Random randomactionligne = new Random();
                        int actionligneId = randomactionligne.nextInt();
                        if(actionligneId > 0)
                            actionligneId= actionligneId*-1;

                        ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Inventaire Ligne", inventaireLigneTemp.get_UID(), "", 0, (int)inventaireLigneTemp.getStockPhysique(), inventaireLigneTemp.getDesignation());
                        ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                    }
                }
                if (compteurReussite == inventaireLigneTempList.size()) {
                    Date dateDuJour = new Date();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    int Inventaire_ID = inventaireID;
                    String Cycle = "";
                    String InventaireDate = dateFormat.format(dateDuJour);
                    String cycleDateDebut = "";
                    String _SYS_DT_MAJ = "";
                    String _SYS_USER_MAJ = "";
                    String _SYS_HEURE_MAJ = "";
                    String cycleDateFin = "";
                    boolean clotureActive = true;
                    String objet = "Inventaire Scanné " + depot.getDepot_Reference();
                    String Mode_Comptabilisation = "Inventaire scanné";
                    String depotReference = depot.getDepot_Reference().trim();
                    String depotNom = depot.getNom().trim();
                    String operateur = "";
                    int NBLignes = inventaireLigneTempList.size();
                    double Valeur_TTC = 0;
                    double Valeur_PUMP_TTC = 0;

                    Inventaire inventaire = new Inventaire(Inventaire_ID, Cycle, InventaireDate, cycleDateDebut, _SYS_DT_MAJ, _SYS_USER_MAJ, _SYS_HEURE_MAJ, cycleDateFin, clotureActive, objet, Mode_Comptabilisation, depotReference, depotNom, operateur, NBLignes, Valeur_TTC, Valeur_PUMP_TTC);

                    long rowID = gestionnaireInventaire.insererUnInventaireEnBDD(db, inventaire);
                    if (rowID != -1) {
                        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, InventaireOpenHelper.Constantes.TABLE_INVENTAIRE, inventaire.getPhiMR4UUID(), 0, DBOpenHelper.ActionsEAS.AJOUT);
                    }
                } else {
                    Alerte.afficherAlerte(DetailInventaireScannerActivity.this, "Alerte", "Une erreur est survenue, aucun changement ne sera effectué", "alerte");
                    gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
                }

                Toast.makeText(DetailInventaireScannerActivity.this, "Inventaire effectué", Toast.LENGTH_SHORT).show();

                if (OutilsGestionConnexionReseau.isServerAccessible(DetailInventaireScannerActivity.this)) {
                    gestionnaireElementASynchroniser.toutSynchroniser(DetailInventaireScannerActivity.this, db, utilisateurConnecte, true);
                }

                DetailInventaireScannerActivity.this.finish();
            } else {
                Alerte.afficherAlerte(DetailInventaireScannerActivity.this, "Attention", "Veuillez scanner au moins un produit pour valider votre inventaire", "alerte");
            }
        }
    };

    // Définition de l'action à réaliser au Click sur le bouton Datamatrix
    View.OnClickListener clicBoutonDatamatrix = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingMenu.close(true);
            List<String> listeCodesGS1 = new ArrayList<>();
            for (Inventaire_Ligne_Temp inventaireLigne : inventaireLigneTempList
                    ) {
                for (int i = 0; i < inventaireLigne.getStockPhysique(); i++) {
                    listeCodesGS1.add(inventaireLigne.getGS1(db));
                }
            }

            Intent detailInventaireScannerIntent = null;
            Bundle detailInventaireScannerExtras = DetailInventaireScannerActivity.super.getBundle();

            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                detailInventaireScannerIntent = new Intent(DetailInventaireScannerActivity.this, ScannerSearchOnlyActivity.class);
                detailInventaireScannerExtras.putBoolean("modeCumule", true);
            }
            else
            {
                detailInventaireScannerIntent = new Intent(DetailInventaireScannerActivity.this, BarcodeCaptureActivity.class);
            }
            detailInventaireScannerExtras.putBoolean("isBoutonSuppressionExistant", true);
            detailInventaireScannerExtras.putBoolean("modeRafale", true);
            detailInventaireScannerExtras.putStringArrayList("stringList", (ArrayList) listeCodesGS1);
            detailInventaireScannerIntent.putExtras(detailInventaireScannerExtras);

            DetailInventaireScannerActivity.this.startActivityForResult(detailInventaireScannerIntent, CodesEchangesActivites.RETOUR_LISTE_CODE_GS1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_inventaire_scanner);

        //gestion du package manager
        pm = DetailInventaireScannerActivity.this.getPackageManager();

        // Récupération du dépot en fonction de la variable globale
        depot = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotSelectionneID"));

        // Récupréation des lignes d'inventaire temporaire du dépot
        inventaireLigneTempList = gestionnaireInventaire_Ligne_Temp.getAllInventaireLigneTempByDepot(db, depot);

        inventaireLigneTempListView = (ListView) findViewById(R.id.listeView);

        //Récupération des objets graphiques
        floatingMenu = (FloatingActionMenu) findViewById(R.id.floatingMenu);
        boutonValider = (FloatingActionButton) findViewById(R.id.boutonValider);
        boutonScanDatamatrix = (FloatingActionButton) findViewById(R.id.boutonScanDatamatrix);

        boutonValider.setOnClickListener(clicBoutonValider);

        boutonScanDatamatrix.setOnClickListener(clicBoutonDatamatrix);

        if (inventaireLigneTempList.size() == 0) {
            boutonScanDatamatrix.performClick();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        // Affichage du nombre d'inventaire_ligne
        int size_liste = inventaireLigneTempList.size();
        String titre = "Références comptabilisées";
        if(size_liste < 2)
            titre = "Référence comptabilisée";

        ((TextView) findViewById(R.id.titre)).setText(titre);
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(inventaireLigneTempList.size()));

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Inventaire_Ligne_TempAdapter(DetailInventaireScannerActivity.this, db, inventaireLigneTempList);
        inventaireLigneTempListView.setAdapter(adapter);
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_CODE_GS1:
                    List<String> stringList = data.getExtras().getStringArrayList("listeString");
                    if(stringList == null)
                    {
                        stringList = data.getExtras().getStringArrayList("stringList");
                        if(stringList == null)
                        {
                            stringList = new ArrayList<>();
                        }
                    }
                    inventaireLigneTempList = new ArrayList<>();
                    gestionnaireInventaire_Ligne_Temp.supprimerTousLesInventaireLigneTempsParDepot(db, depot);
                    int compteurErreur = 0;
                    for (String gs1 : stringList) {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1);
                        String codeGTIN = gs1Decoupe.get(OutilsDecodage.codeGtin);
                        if (codeGTIN == null) {
                            codeGTIN = "";
                        }
                        List<Produit> produitList = gestionnaireProduit.getProduitsParGTIN(db, codeGTIN);

                        if (produitList.size() != 1) {
                            // Si la liste ne contient pas précisément un produit, une erreur est suvenue et le produit ne sera pas ajouté.
                            compteurErreur++;
                        } else {
                            Produit produit = produitList.get(0);
                            Inventaire_Ligne_Temp inventaireLigneTemp = new Inventaire_Ligne_Temp(produit, gs1Decoupe, depot);

                            boolean present = false;
                            int index = 0;

                            for(int i = 0; i < inventaireLigneTempList.size(); i++)
                            {
                                Inventaire_Ligne_Temp inventaireListe = inventaireLigneTempList.get(i);
                                if(inventaireListe.getProduitID() == inventaireLigneTemp.getProduitID() && inventaireListe.getLot().contentEquals(inventaireLigneTemp.getLot()) && inventaireListe.getEmplacement().contentEquals(inventaireLigneTemp.getEmplacement()) && inventaireListe.getZone().contentEquals(inventaireLigneTemp.getZone()))
                                {
                                    index = i;
                                    present = true;
                                    break;
                                }
                            }

                            if (present) {
                                Inventaire_Ligne_Temp inventaireLigneTempCourant = inventaireLigneTempList.get(index);
                                inventaireLigneTempCourant.setStockPhysique(inventaireLigneTempCourant.getStockPhysique() + inventaireLigneTemp.getStockPhysique());
                                gestionnaireInventaire_Ligne_Temp.mettreAJourInventaireLigneTemp(db, inventaireLigneTempCourant);
                            } else {
                                inventaireLigneTemp.setStockPhysique(1);
                                long rowID = gestionnaireInventaire_Ligne_Temp.insererUnInventaire_Ligne_TempEnBDD(db, inventaireLigneTemp);
                                if (rowID != -1) {
                                    inventaireLigneTempList.add(inventaireLigneTemp);
                                }
                            }
                        }
                    }
                    if (compteurErreur > 0) {
                        Alerte.afficherAlerte(DetailInventaireScannerActivity.this, "Alerte", "Impossible d'identifier " + String.valueOf(compteurErreur) + " produits.", "alerte");
                    }
                    break;
                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    if (inventaireLigneTempAModifier != null) {

                        Depot_Zone zone = gestionnaireZone.getUneZoneByID(db, data.getExtras().getInt("zoneSelectionneeID"));
                        Depot_Emplacement emplacement = gestionnaireEmplacement.getUnEmplacementByID(db, data.getExtras().getInt("emplacementSelectionneID"));

                        inventaireLigneTempAModifier.setZone(zone.getZoneName().trim());
                        inventaireLigneTempAModifier.setEmplacement(emplacement.getAdressage().trim());

                        gestionnaireInventaire_Ligne_Temp.mettreAJourInventaireLigneTemp(db, inventaireLigneTempAModifier);
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    // Fonction permettant de lancer l'activity de modification de zone et emplacement
    public void modifierZoneEtEmplacement(Inventaire_Ligne_Temp inventaireLigneTemp) {
        inventaireLigneTempAModifier = inventaireLigneTemp;

        Intent detailInventaireScannerIntent = new Intent(DetailInventaireScannerActivity.this, ListeZonesActivity.class);
        Bundle detailInventaireScannerBundle = super.getBundle();
        detailInventaireScannerBundle.putInt("depotSelectionneID", depot.getDepot_UID());
        detailInventaireScannerIntent.putExtras(detailInventaireScannerBundle);

        DetailInventaireScannerActivity.this.startActivityForResult(detailInventaireScannerIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Désignation produit...");
        return true;
    }
}
