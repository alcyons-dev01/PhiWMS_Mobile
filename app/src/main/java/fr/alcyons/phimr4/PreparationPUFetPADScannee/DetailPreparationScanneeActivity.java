package fr.alcyons.phimr4.PreparationPUFetPADScannee;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.ObjetReceptionScannee;
import fr.alcyons.phimr4.Classes.PH_Preparation;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phimr4.Classes.PH_Serialisation;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.ObjetPreparationScannee;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.PH_Preparation_Ligne_PreparationLotAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.OutilsSerialisation.Serialisation;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

import static fr.alcyons.phimr4.Outils.Alerte.aNumberPicker;

/**
 * Created by olivier on 29/04/2019.
 */

public class DetailPreparationScanneeActivity extends ServiceAvecConnexionActivity {

    public PH_Preparation ph_preparation_Selectionne;
    Serialisation serialisation;
    PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapteRetourListeLot;
    boolean utilisation_scan;
    List<String> listeGTIN;
    public List<PH_Preparation_Ligne_Preparation_Adapte> phPreparationLignePreparationAdapte_List;
    public ListView phPreparationLigne_ListView;
    PH_Preparation_Ligne_PreparationLotAdapter ph_preparation_ligne_preparationLotAdapter;
    boolean premierpassage;
    int nb_produit_scanne;
    Integer nbLotPréparer;
    String genre_preparation;
    List<PH_Preparation_Ligne> phPreparationLignes;
    TextView textViewNBPalette;
    TextView textViewNBCaisse;

    PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapteSelectionne;
    TextView livraisonPrevueDate_TextView;
    PH_Preparation_Ligne_PreparationLotAdapter.PH_PreparationLigneViewHolder ph_preparationLigneViewHolder;
    FloatingActionButton boutonSave;

    Depot_Emplacement depotEmplacement;
    boolean versScanProduit;

    List<ObjetReceptionScannee> liste_resultat;
    List<String> liste_code_scannee;
    ActionUtilisateur actionUtilisateur;
    String chemin_photo_document;
    PackageManager pm;

    Context context;

    // Permet de sauvegarder la préparation
    public void enregistrerPhPreparation(){
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, actionUtilisateur);
        List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotsSaisis;
        PH_Preparation_Ligne ph_preparationLigneCorrespondant = null;
        List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> listeLot = new ArrayList<>();
        int compteurReussiteGlobale = 0;
        int nbTotalLotsAvecValeurSaisie = 0;
        int compteurReussiteParLotAvecValeur = 0;
        int nbLigneSansValeur = 0;
        List<String> produitNonRenseigne = new ArrayList<>();

        // On vérifie que toutes les ph_preparations_lignes ont une quantité pour au moins un lot
        for (PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte : ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes) {
            int nbLignesAvecValeur = 0;

            for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot : ph_preparationLigneAdapte.getLotAdaptes())
            {
                if (lot.getQteSaisie() != 0) {
                    nbLignesAvecValeur++;
                    nbTotalLotsAvecValeurSaisie++;
                }
            }
            if (nbLignesAvecValeur == 0) {
                nbLigneSansValeur ++;
                PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigneAdapte.getPh_preparationLigneID());
                produitNonRenseigne.add(ph_preparation_ligne.getProduitDesignation());
            }
        }

        if(nbLigneSansValeur > 0)
        {
            Alerte.afficherAlerteListView(DetailPreparationScanneeActivity.this, "Les produits suivant n'ont pas été préparés", produitNonRenseigne);
            return;
        }

        // Enregistrement des PH_Preparation_Ligne
        for (PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte : ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes) {

            ph_preparationLigneCorrespondant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigneAdapte.getPh_preparationLigneID());

            lotsSaisis = new ArrayList<>();
            for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot : ph_preparationLigneAdapte.getLotAdaptes() ) {
                if (lot.getQteSaisie() != 0) {
                    lotsSaisis.add(lot);
                }
            }

            int GlobalAPreparer = ph_preparationLigneCorrespondant.getQte_Demander();
            boolean origine = true;
            PH_Preparation_Ligne ph_preparationLigneCourant = null;

            for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot : lotsSaisis) {

                if(origine)
                {
                    ph_preparationLigneCourant = new PH_Preparation_Ligne(ph_preparationLigneCorrespondant);
                    Random random = new Random();
                    int new_id = random.nextInt();
                    if(new_id > 0)
                    {
                        new_id= new_id*-1;
                    }
                    ph_preparationLigneCourant.set_UID(new_id);
                    origine = false;
                }
                else
                {
                    ph_preparationLigneCourant = new PH_Preparation_Ligne(ph_preparationLigneCourant);
                    Random random = new Random();
                    int new_id = random.nextInt();
                    if(new_id > 0)
                    {
                        new_id= new_id*-1;
                    }
                    ph_preparationLigneCourant.set_UID(new_id);
                }

                ph_preparationLigneCourant.setQte_Demander(GlobalAPreparer);
                GlobalAPreparer = GlobalAPreparer - lot.getQteSaisie();
                ph_preparationLigneCourant.setQte_RAL(GlobalAPreparer);
                ph_preparationLigneCourant.setQte_preparer(lot.getQteSaisie());
                ph_preparationLigneCourant.setLotNumero(lot.getNumLot().trim());
                ph_preparationLigneCourant.setPeremptionDate(lot.getDatePeremption());
                ph_preparationLigneCourant.setZoneDepot(lot.getZone().trim());
                ph_preparationLigneCourant.setEmplacementParDefaut(lot.getEmplacement().trim());
                ph_preparationLigneCourant.setSerieNumero(lot.getNumSerie().trim());
                long rowID = PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparationLigneCourant);
                if (rowID != -1) {

                    //gestion de l'action ligne utilisateur
                    Random randomActionLigneUtilisateur = new Random();
                    int id_action_ligne_utilisateur = randomActionLigneUtilisateur.nextInt();
                    if(id_action_ligne_utilisateur > 0)
                    {
                        id_action_ligne_utilisateur = id_action_ligne_utilisateur * -1;
                    }

                    Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparationLigneCourant.getProduitID());
                    String gtin = produit.getGTIN();
                    String serie = ph_preparationLigneCourant.getSerieNumero();
                    String lotLigneCourant = ph_preparationLigneCourant.getLotNumero();
                    String date = ph_preparationLigneCourant.getPeremptionDate();

                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yymmdd");
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-mm-dd");
                    Date dateDate = null;
                    try {
                        dateDate = dateFormat2.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(dateDate != null)
                        date = dateFormat1.format(dateDate);
                    else
                        date = "000000";

                    String gs1 = gtin+"21"+serie+"@17"+date+"10"+lotLigneCourant;

                    ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(id_action_ligne_utilisateur, actionUtilisateur.getId(), "PH_Preparation_Ligne", ph_preparationLigneCourant.get_UID(), gs1, 0, ph_preparationLigneCourant.getQte_preparer(), ph_preparationLigneCourant.getProduitDesignation());
                    ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);

                    compteurReussiteParLotAvecValeur++;
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparationLigneCourant.getPhiMR4UUID(), ph_preparationLigneCourant.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                }
            }
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparationLigneCorrespondant.getPhiMR4UUID(), ph_preparationLigneCorrespondant.get_UID(), DBOpenHelper.ActionsEAS.SUPPR);
            PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ph_preparationLigneCorrespondant);
            compteurReussiteGlobale++;
            if (GlobalAPreparer > 0) {
                PH_Preparation_Ligne ph_preparation_reliquat = new PH_Preparation_Ligne(ph_preparationLigneCorrespondant);
                ph_preparation_reliquat.setQte_Demander(GlobalAPreparer);
                ph_preparation_reliquat.setQte_RAL(GlobalAPreparer);
                ph_preparation_reliquat.setQte_preparer(0);
                PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_reliquat);
            }
        }

        // Si tout est ok alors on met à jour le PH_Preparation
        if (compteurReussiteParLotAvecValeur == nbTotalLotsAvecValeurSaisie) {

            Date dateJour = new Date();
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

            ph_preparation_Selectionne.setPreparationDate(format.format(dateJour));
            ph_preparation_Selectionne.setStatut(getString(R.string.PreparationEffectuée));

            long rowID = PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation_Selectionne);
            if (rowID != -1) {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation_Selectionne.getPhiMR4UUID(), ph_preparation_Selectionne.getUID(), DBOpenHelper.ActionsEAS.MAJ);
            } else {
                // Si le retour n'est pas mis à jour, on remet le compteur à 0
                compteurReussiteGlobale = 0;
            }
        } else {
            compteurReussiteGlobale = 0;
        }

        if (compteurReussiteGlobale != ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.size()) {
            // Si une erreur est survenue, on annule les modifications en vidant la table ElementASynchroniser
            Alerte.afficherAlerte(DetailPreparationScanneeActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
            ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);

            DetailPreparationScanneeActivity.this.finish();
            return;
        }


        //véfication de la totalité de la préparation
        List<PH_Preparation_Ligne> listePhPreparationRALListe = new ArrayList<>();
        listePhPreparationRALListe = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesRAL(db, ph_preparation_Selectionne);
        if (listePhPreparationRALListe.size() != 0) {
            Random random = new Random();
            int preparationID = random.nextInt();
            if (preparationID > 0) {
                preparationID = preparationID * -1;
            }

            PH_Preparation phPreparationDuplique = ph_preparation_Selectionne;
            phPreparationDuplique.setUID(preparationID);
            phPreparationDuplique.setStatut("En Reliquat");
            phPreparationDuplique.setListe("Reliquat Préparation N°" + ph_preparation_Selectionne.getUID());
            phPreparationDuplique.setPreparationDate("0000-00-00");
            phPreparationDuplique.setPreparateur("");
            phPreparationDuplique.setPreparateur_userID(0);
            phPreparationDuplique.setValidee(false);
            phPreparationDuplique.setLivree(false);
            phPreparationDuplique.setDepotDestinataireReference(ph_preparation_Selectionne.getDepotDestinataireReference().trim());

            //enregistrement du nouveau PH_Préparation
            if (phPreparationDuplique != null) {
                PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, phPreparationDuplique);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, phPreparationDuplique.getPhiMR4UUID(), phPreparationDuplique.getUID(), DBOpenHelper.ActionsEAS.AJOUT);
                int test = phPreparationDuplique.getUID();
            }

            for (PH_Preparation_Ligne ph_preparation_ligne : listePhPreparationRALListe) {
                Random random2 = new Random();
                int preparationLigneID = random2.nextInt();
                if (preparationLigneID > 0) {
                    preparationLigneID = preparationLigneID * -1;
                }

                PH_Preparation_Ligne newPhPreparationLigne = ph_preparation_ligne;
                newPhPreparationLigne.setPreparationID(phPreparationDuplique.getUID());
                newPhPreparationLigne.set_UID(preparationLigneID);
                newPhPreparationLigne.setQte_APreparer(ph_preparation_ligne.getQte_RAL());
                newPhPreparationLigne.setQte_livrer(0);

                //enregistrement du nouveau PH_PreparationLigne
                if (newPhPreparationLigne != null) {
                    PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, newPhPreparationLigne);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, newPhPreparationLigne.getPhiMR4UUID(), newPhPreparationLigne.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                }
            }

            Toast.makeText(DetailPreparationScanneeActivity.this, "Préparation effectuée en partie", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DetailPreparationScanneeActivity.this, "Préparation effectuée", Toast.LENGTH_SHORT).show();
        }

        // Si possible, on essaie de mettre à jour les éléments
        if (OutilsGestionConnexionReseau.isServerAccessible(DetailPreparationScanneeActivity.this)) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, actionUtilisateur.getPhiMR4UUID(), actionUtilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            ElementASynchroniserOpenHelper.toutSynchroniser(DetailPreparationScanneeActivity.this, db, utilisateurConnecte, true);
        }
        //DetailPreparationScanneeActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS);
        Intent retourListeIntent = new Intent(DetailPreparationScanneeActivity.this, ServicePreparationPufScanneeActivity.class);
        if(ph_preparation_Selectionne.getDepotDestinataireReference().contains("-PAD-"))
            retourListeIntent = new Intent(DetailPreparationScanneeActivity.this, ServicePreparationPadScanneeActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        extras.putInt("serviceSelectionneID", serviceActuel.getId());
        retourListeIntent.putExtras(extras);
        DetailPreparationScanneeActivity.this.startActivity(retourListeIntent);
        DetailPreparationScanneeActivity.this.finish();
    }

    public View.OnClickListener clicBoutonSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClick_ActionContenant();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_preparation_modifiable);

        // Récupération des variables globales
        ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, intent.getExtras().getInt("ph_preparationUID_Selectionne"));
        genre_preparation = intent.getExtras().getString("genre");
        utilisation_scan = false;
        serialisation = new Serialisation(DetailPreparationScanneeActivity.this, db, utilisateurConnecte);
        listeGTIN = new ArrayList<>();
        liste_code_scannee = new ArrayList<>();

        //initialisation des variables globales
        versScanProduit = true;
        premierpassage = true;
        liste_resultat = new ArrayList<>();

        // Affichage des informations de base
        Depot depot = gestionnaireDepot.getDepotParReference(db, ph_preparation_Selectionne.getDepotDestinataireReference());

        String intitule = "#" + String.valueOf(ph_preparation_Selectionne.getUID());
        ((TextView) findViewById(R.id.intitule)).setText(intitule);

        String textDepot = depot.getNom();
        if(utilisateurConnecte.getIdentifiant().contentEquals("alcyons") || utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS") && depot.getStructure().contentEquals("PAD"))
        {
            textDepot = "Patient - "+depot.getPAD_IPP();
        }
        ((TextView) findViewById(R.id.depot)).setText(textDepot);

        ((LinearLayout) findViewById(R.id.lancerScan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuDatamatrixClick();
            }
        });


        // Gestion de la listView
        phPreparationLigne_ListView = (ListView) findViewById(R.id.listeView);
        phPreparationLignePreparationAdapte_List = new ArrayList<>();

        phPreparationLignes = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);
        Collections.sort(phPreparationLignes, new Comparator<PH_Preparation_Ligne>() {
            @Override
            public int compare(PH_Preparation_Ligne o1, PH_Preparation_Ligne o2) {
                return o1.getProduitDesignation().compareTo(o2.getProduitDesignation());
            }
        });


        nbLotPréparer = 0;

        // Créer autant de ligne que de lot à préparer
        //gestion du tri de la liste par poids
        Collections.sort(phPreparationLignes, new Comparator<PH_Preparation_Ligne>() {
            @Override
            public int compare(PH_Preparation_Ligne o1, PH_Preparation_Ligne o2) {
                if(o2.getProduitPoids() > o1.getProduitPoids())
                    return 1;
                else if(o2.getProduitPoids() < o1.getProduitPoids())
                    return -1;
                else
                    return 0;
            }
        });

        for (PH_Preparation_Ligne phPrepLigne : phPreparationLignes) {

            if (phPrepLigne.getQte_Demander() > 0) {
                List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotAdaptes = null;

                PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte = new PH_Preparation_Ligne_Preparation_Adapte(phPrepLigne.get_UID());
                Produit produit = gestionnaireProduit.getProduitByID(db, phPrepLigne.getProduitID());
                listeGTIN.add(produit.getGTIN());
                Depot depotOrigine = gestionnaireDepot.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());
                List<Stock_Lot_Emplacement_Light> stock_lot_emplacement_lightList = gestionnaireStock_Lot_Emplacement.getAllStockLotEmplacementByProduitEtDepot(db, produit, depotOrigine);

                Collections.sort(stock_lot_emplacement_lightList, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getLot().compareTo(o2.getLot());
                    }
                });
                Collections.sort(stock_lot_emplacement_lightList, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getPeremptionDate().compareTo(o2.getPeremptionDate());
                    }
                });

                double qteDemander = phPrepLigne.getQte_Demander();
                double qtePreparer = phPrepLigne.getQte_Demander();
                Boolean lotAssigne = false;

                for (Stock_Lot_Emplacement_Light stockLotEmplacement : stock_lot_emplacement_lightList) {
                    if (stockLotEmplacement.getQte() > 0) {
                        if (!lotAssigne) {
                            if (qteDemander > 0) {
                                nbLotPréparer++;
                            }
                            if (stockLotEmplacement.getQte() < qteDemander) {
                                qteDemander = qteDemander - stockLotEmplacement.getQte();
                                qtePreparer = stockLotEmplacement.getQte();
                            } else {
                                lotAssigne = true;
                                qtePreparer = qteDemander;
                            }
                            stockLotEmplacement.setQte_Preparer((int) qtePreparer);

                            if(premierpassage)
                            {
                                stockLotEmplacement.setQte_Preparer(0);
                            }
                        }
                        ph_preparationLigneAdapte.getLotAdaptes().add(ph_preparationLigneAdapte.new LotAdapte(stockLotEmplacement));
                    }
                }
                phPreparationLignePreparationAdapte_List.add(ph_preparationLigneAdapte);
            }

        }

        phPreparationLignePreparationAdapte_List = new ArrayList<>();

        phPreparationLigne_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                utilisation_scan = false;
                PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte = ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.get(position);
                PH_Preparation_Ligne ph_preparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_preparationLigneAdapte.getPh_preparationLigneID());

                ph_preparationLigneViewHolder = ph_preparation_ligne_preparationLotAdapter.ph_preparation_ligneViewHolderList.get(position);
                Intent detailPreparation_Intent = new Intent(DetailPreparationScanneeActivity.this, ListeLotPreparationScanneeActivity.class);
                Bundle detailPreparation_Bundle = DetailPreparationScanneeActivity.super.getBundle();
                detailPreparation_Bundle.putInt("produitID", ph_preparationLigne.getProduitID());
                detailPreparation_Bundle.putSerializable("ph_preparationLigneAdapte", ph_preparationLigneAdapte);
                detailPreparation_Bundle.putSerializable("ObjetDejaScanne", (Serializable) liste_resultat);
                detailPreparation_Bundle.putStringArrayList("CodeDejaScannee", (ArrayList<String>) liste_code_scannee);

                Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());

                detailPreparation_Bundle.putInt("depotID", depot.getDepot_UID());
                detailPreparation_Intent.putExtras(detailPreparation_Bundle);

                DetailPreparationScanneeActivity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //initialisation de l'action utilisateur
        Random randomActionUtilisateur = new Random();
        int id_action_utilisateur = randomActionUtilisateur.nextInt();
        if(id_action_utilisateur > 0)
        {
            id_action_utilisateur = id_action_utilisateur * -1;
        }

        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);
        chemin_photo_document = "";
        actionUtilisateur = new ActionUtilisateur(id_action_utilisateur,utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "Soumise", ph_preparation_Selectionne.getUID(), chemin_photo_document, "Préparation");

        invalidateOptionsMenu();
        context = DetailPreparationScanneeActivity.this;
        pm = DetailPreparationScanneeActivity.this.getPackageManager();

        if(!ph_preparation_Selectionne.getListe().contentEquals("ALCYONS_LISTE"))
        {
            if (OutilsGestionConnexionReseau.isServerAccessible(DetailPreparationScanneeActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(DetailPreparationScanneeActivity.this, "Veuillez patienter", "Synchronisation des stocks en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(DetailPreparationScanneeActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePreparationDetail+ph_preparation_Selectionne.getUID();

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int nbResultat = response.getInt("resultCount");
                                if (nbResultat == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");;
                                    } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                        Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : aucune référence trouvé", "alerte");
                                    }
                                } else {
                                    JSONArray ph_preparationLigne_JSONArray = response.getJSONArray("PH_Preparation_Ligne");
                                    for (int k = 0; k < ph_preparationLigne_JSONArray.length(); k++) {
                                        JSONObject ph_preparationLigne_JSONObject = ph_preparationLigne_JSONArray.getJSONObject(k);
                                        JSONArray phStockLotEmplacement_JSONArray = ph_preparationLigne_JSONObject.getJSONArray("ph_stock_lot_emplacements");

                                        for (int y = 0; y < phStockLotEmplacement_JSONArray.length(); y++) {
                                            Stock_Lot_Emplacement_Light stock_lot_emplacement_light = new Stock_Lot_Emplacement_Light(phStockLotEmplacement_JSONArray.getJSONObject(y));
                                            if (gestionnaireStock_Lot_Emplacement.getStock_Lot_EmplacementByID(db, stock_lot_emplacement_light.get_UID()) == null) {
                                                if (stock_lot_emplacement_light.getQte() > 0) {
                                                    gestionnaireStock_Lot_Emplacement.insererUnStock_Lot_EmplacementEnBDD(db, stock_lot_emplacement_light);
                                                }
                                            }
                                        }
                                    }

                                    if(versScanProduit)
                                    {
                                        versScanProduit = false;
                                        onMenuDatamatrixClick();
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            handler.sendMessage(handler.obtainMessage());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error");
                            Alerte.afficherAlerte(DetailPreparationScanneeActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Préparation PAD)", "alerte");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    return headers;
                }
            };
            obreq.setRetryPolicy(retryPolicy);
            requestQueue.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            passageParOnCreate = false;
        }
        }
        else
        {
            if(passageParOnCreate)
            {
                onMenuDatamatrixClick();
                passageParOnCreate = false;
            }
        }
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_LOTS:
                    liste_resultat = new ArrayList<>();
                    if(!utilisation_scan)
                    {
                        int position = ph_preparation_ligne_preparationLotAdapter.ph_preparation_ligneViewHolderList.indexOf(ph_preparationLigneViewHolder);
                        if(position != -1) {
                            ph_preparationLigneAdapteRetourListeLot = ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.get(position);
                        }
                    }

                    Integer quantiteAvant = 0;
                    for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte : ph_preparationLigneAdapteRetourListeLot.getLotAdaptes()) {
                        if (lotAdapte.getQteSaisie() > 0) {
                            quantiteAvant++;
                        }
                    }

                    ph_preparationLigneAdapteRetourListeLot.setLotAdaptes((List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>) data.getExtras().getSerializable("lotAdaptes"));
                    List<ObjetReceptionScannee> listenouveauxobjet = (List<ObjetReceptionScannee>) data.getExtras().getSerializable("listeObjet");

                    if(listenouveauxobjet.size() != 0)
                    {
                        for(ObjetReceptionScannee objetCourant : listenouveauxobjet)
                        {
                            if(liste_code_scannee == null)
                            {
                                liste_code_scannee = new ArrayList<>();
                            }
                            liste_code_scannee.add(objetCourant.getGs1_scannee());

                            if(liste_resultat == null)
                            {
                                liste_resultat = new ArrayList<>();
                            }
                            liste_resultat.add(objetCourant);
                        }
                    }

                    Integer quantiteApres = 0;
                    for (PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lotAdapte : ph_preparationLigneAdapteRetourListeLot.getLotAdaptes()) {
                        if (lotAdapte.getQteSaisie() > 0) {
                            quantiteApres++;
                        }
                    }
                    Integer somme = 0;

                    ph_preparation_ligne_preparationLotAdapter.notifyDataSetChanged();

                    break;
                case CodesEchangesActivites.RETOUR_CODE_GS1: {
                    if (resultCode == DetailPreparationScanneeActivity.RESULT_OK) {
                        String code = data.getStringExtra("code");
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                        List<Produit> produit_List = new ArrayList<>();

                        if (gs1Decoupe.size() != 0) {
                            String codeGtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
                            String numeroLot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                            String dateDePeremption = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
                            String numeroSerie = gs1Decoupe.get(OutilsDecodage.numeroSerie);

                            String last_char = numeroSerie.substring(numeroSerie.length() -1);
                            if(last_char.contentEquals("@"))
                            {
                                numeroSerie = numeroSerie.substring(0, numeroSerie.length()-1);
                            }

                            last_char = numeroLot.substring(numeroLot.length()-1);
                            if(last_char.contentEquals("@"))
                            {
                                numeroLot = numeroLot.substring(0, numeroLot.length()-1);
                            }

                            String gtin = codeGtin;
                            if(gtin.length() > 14)
                            {
                                gtin = gtin.substring(2);
                            }
                            String peremption = "";
                            Date peremption_temp = null;
                            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat output = new SimpleDateFormat("yyMMdd");
                            try {
                                peremption_temp = input.parse(dateDePeremption);                 // parse input
                                peremption = output.format(peremption_temp);    // format output
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long uid = Serialisation.Serialisation_Verifier(utilisateurConnecte.getId(), false, false, gtin, "GTIN", numeroLot, peremption, numeroSerie, "Vérification", gtin, "", "");
                            PH_Serialisation ph_serialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, (int) uid);
                            if(ph_serialisation != null)
                            {
                                if(ph_serialisation.getResultat().contentEquals("ERREUR") || ph_serialisation.getResultat().contentEquals("INACTIVE"))
                                {
                                    Alerte.afficherAlerte(DetailPreparationScanneeActivity.this, "Erreur", "Le produit scanné est déjà inactif", "alerte" );
                                    break;
                                }
                            }

                            produit_List = ProduitOpenHelper.getMedicamentsParGTIN(db, codeGtin);
                            if (produit_List.size() == 1) {
                                Produit produit = produit_List.get(0);
                                utilisation_scan = true;
                                for(int j = 0; j < phPreparationLignePreparationAdapte_List.size(); j++)
                                {
                                    int id = phPreparationLignePreparationAdapte_List.get(j).getPh_preparationLigneID();
                                    PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, id);
                                    String designation = ph_preparation_ligne.getProduitDesignation();

                                    if(designation.contentEquals(produit.getDesignation_ext()) || designation.contentEquals(produit.getDesignation_interne()))
                                    {
                                        ph_preparationLigneAdapteRetourListeLot = ph_preparation_ligne_preparationLotAdapter.ph_preparation_lignes_Adaptes.get(j);
                                        break;
                                    }
                                }

                                for (PH_Preparation_Ligne_Preparation_Adapte phPreparationLignePreparationAdapte : phPreparationLignePreparationAdapte_List) {
                                    PH_Preparation_Ligne phPreparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, phPreparationLignePreparationAdapte.getPh_preparationLigneID());
                                    if (phPreparationLigne.getProduitID() == produit_List.get(0).getID_produit()) {
                                        ph_preparationLigneAdapteSelectionne = phPreparationLignePreparationAdapte;
                                    }
                                }
                                if (ph_preparationLigneAdapteSelectionne != null) {
                                    ph_preparationLigneAdapteSelectionne.getLotAdaptes().removeAll(ph_preparationLigneAdapteSelectionne.getLotAdaptes());
                                    //on regarde si le lot existe déjà
                                    List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> list = new ArrayList<>();
                                    list.addAll(ph_preparationLigneAdapteSelectionne.getLotAdaptes());
                                    if(list.indexOf(numeroLot) == -1)
                                    {
                                        ObjetPreparationScannee newstock = new ObjetPreparationScannee(produit.getCond_achat(), numeroLot, dateDePeremption, produit.getEmplacement_PUI_Defaut(), produit.getZone_PUI_Defaut(), ph_preparation_Selectionne.getDepotOrigineReference(), produit.getID_produit(), produit.getCond_achat(), numeroSerie);
                                        PH_Preparation_Ligne_Preparation_Adapte.LotAdapte new_lot = ph_preparationLigneAdapteSelectionne.new LotAdapte(newstock);
                                        ph_preparationLigneAdapteSelectionne.getLotAdaptes().add(new_lot);
                                    }

                                    Intent detailPreparation_Intent = new Intent(DetailPreparationScanneeActivity.this, ListeLotPreparationScanneeActivity.class);
                                    Bundle detailPreparation_Bundle = DetailPreparationScanneeActivity.super.getBundle();
                                    detailPreparation_Bundle.putInt("produitID", produit_List.get(0).getID_produit());
                                    detailPreparation_Bundle.putSerializable("ph_preparationLigneAdapte", ph_preparationLigneAdapteSelectionne);
                                    detailPreparation_Bundle.putString("numeroLot", numeroLot);
                                    detailPreparation_Bundle.putString("dateDePeremption", dateDePeremption);
                                    detailPreparation_Bundle.putString("numSerie", numeroSerie);

                                    Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());

                                    detailPreparation_Bundle.putInt("depotID", depot.getDepot_UID());
                                    detailPreparation_Intent.putExtras(detailPreparation_Bundle);

                                    DetailPreparationScanneeActivity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
                                } else {
                                    Alerte.afficherAlerte(DetailPreparationScanneeActivity.this, "Attention", "Ce produit n'est pas présent dans la préparation", "alerte");
                                    onResume();
                                }
                            } else if (produit_List.size() > 1) {
                                Alerte.afficherAlerte(DetailPreparationScanneeActivity.this, "Attention", "Plusieurs médicaments correspondent à ce code", "alerte");
                                onResume();
                            } else {
                                Toast toast = Toast.makeText(DetailPreparationScanneeActivity.this, "Aucun médicament ne correspond", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(DetailPreparationScanneeActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                    break;
                }
                case CodesEchangesActivites.RETOUR_PREPARATION:{
                    List<PH_Preparation_Ligne> listePreparationScannee = new ArrayList<>();
                    List<PH_Preparation_Ligne> listPhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);
                    liste_code_scannee = new ArrayList<>();
                    phPreparationLignePreparationAdapte_List = new ArrayList<>();
                    liste_resultat = new ArrayList<>();
                    nbLotPréparer = 0;
                    liste_code_scannee = data.getExtras().getStringArrayList("listeString");
                    liste_resultat = (List<ObjetReceptionScannee>) data.getExtras().getSerializable("listeObjet");
                    if(liste_resultat == null)
                    {
                        liste_resultat = (List<ObjetReceptionScannee>) data.getExtras().getSerializable("listeString");
                        liste_code_scannee = data.getExtras().getStringArrayList("listecodescannee");
                    }
                    if(liste_resultat != null && liste_resultat.size() != 0)
                    {
                        if(listPhPreparationLigne.size() != 0)
                        {
                            for(ObjetReceptionScannee objetReceptionScannee_courant : liste_resultat)
                            {
                                boolean presentListeAdapte = false;
                                Produit produit_courant = null;
                                Map<String, String> gs1DecoupeCourant = OutilsDecodage.decouperGTIN(objetReceptionScannee_courant.getGs1_scannee());
                                String serie_courant = "";
                                String lot_courant = "";
                                String datePeremption = "";
                                Depot_Zone zone_courante = null;
                                Depot depot_courant = null;
                                Depot_Emplacement emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, objetReceptionScannee_courant.getEmplacement_uid());
                                if(emplacement_courant != null)
                                {
                                    zone_courante = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());
                                    if(zone_courante != null)
                                    {
                                        depot_courant = DepotOpenHelper.getDepotParID(db, zone_courante.getDepotID());
                                    }
                                }
                                if(gs1DecoupeCourant.size() != 1 && !objetReceptionScannee_courant.getGs1_scannee().startsWith("ci"))
                                {
                                    serie_courant = gs1DecoupeCourant.get(OutilsDecodage.numeroSerie);
                                    lot_courant = gs1DecoupeCourant.get(OutilsDecodage.numeroLot);
                                    String date_peremption_courant = gs1DecoupeCourant.get(OutilsDecodage.dateDePeremption);
                                    DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

                                    Date date = new Date();

                                    try {
                                        date = dateFormat1.parse(date_peremption_courant);
                                        datePeremption =  dateFormat2.format(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1DecoupeCourant.get(OutilsDecodage.codeGtin));
                                }
                                else
                                {
                                    String code_inconnu = objetReceptionScannee_courant.getGs1_scannee();
                                    if(code_inconnu.startsWith("ci"))
                                    {
                                        Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(code_inconnu);
                                        code_inconnu = MapInconnu.get("Code_Inconnu");
                                        lot_courant = MapInconnu.get("Lot");
                                        datePeremption = MapInconnu.get("Date");
                                    }
                                    produit_courant = ProduitOpenHelper.getUnProduitByCodeInconnu(db, code_inconnu);

                                    if(produit_courant == null)
                                    {
                                        for(PH_Preparation_Ligne ligne_courante : listPhPreparationLigne)
                                        {
                                            if(ligne_courante.getProduitDesignation().contentEquals(code_inconnu))
                                            {
                                                produit_courant = ProduitOpenHelper.getProduitByID(db, ligne_courante.getProduitID());
                                            }
                                        }
                                    }
                                }

                                if(produit_courant != null)
                                {
                                    for(PH_Preparation_Ligne ligne_courante : listPhPreparationLigne)
                                    {
                                        if(ligne_courante.getProduitID() == produit_courant.getID_produit())
                                        {
                                            int stock = objetReceptionScannee_courant.getQuantiteScannee();
                                            List<Stock_Lot_Emplacement_Light> list_stock_lot = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit_courant, depot_courant);
                                            for(Stock_Lot_Emplacement_Light stock_courant : list_stock_lot)
                                            {
                                                if(stock_courant.getLot().toUpperCase().contentEquals(lot_courant.toUpperCase()) && stock_courant.getEmplacement().toUpperCase().contentEquals(emplacement_courant.getAdressage().toUpperCase()) && stock_courant.getZone().toUpperCase().contentEquals(zone_courante.getZoneName().toUpperCase()))
                                                {
                                                    stock = (int) stock_courant.getQte();
                                                }
                                            }

                                            ObjetPreparationScannee stock_lot_emplacement_light = new ObjetPreparationScannee(stock, lot_courant, datePeremption, emplacement_courant.getAdressage(), depot_courant.getDepot_Reference(), zone_courante.getZoneName(),  ligne_courante.getProduitID(), objetReceptionScannee_courant.getQuantiteScannee(), serie_courant);
                                            PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte = new PH_Preparation_Ligne_Preparation_Adapte(ligne_courante.get_UID());
                                            int index = -1;
                                            for(PH_Preparation_Ligne_Preparation_Adapte courant:phPreparationLignePreparationAdapte_List)
                                            {
                                                index++;
                                                if(courant.getPh_preparationLigneID() == ph_preparationLigneAdapte.getPh_preparationLigneID())
                                                {
                                                    ph_preparationLigneAdapte = courant;
                                                    presentListeAdapte = true;
                                                    break;
                                                }
                                            }

                                            if(presentListeAdapte)
                                            {
                                                phPreparationLignePreparationAdapte_List.remove(index);
                                            }
                                            else
                                            {
                                                listePreparationScannee.add(ligne_courante);
                                            }

                                            ph_preparationLigneAdapte.getLotAdaptes().add(ph_preparationLigneAdapte.new LotAdapte(stock_lot_emplacement_light));
                                            if(ligne_courante.getQte_APreparer() != 0)
                                            {
                                                phPreparationLignePreparationAdapte_List.add(ph_preparationLigneAdapte);
                                            }
                                        }
                                    }
                                    nbLotPréparer ++;
                                }
                                else
                                {
                                    break;
                                }
                            }
                        }
                        else
                        {
                            Alerte.afficherAlerte(DetailPreparationScanneeActivity.this, "Attention", "La préparation sélectionnée ne contient pas de produit à préparer", "alerte");
                        }
                    }

                    if(listePreparationScannee.size() != listPhPreparationLigne.size())
                    {
                        boolean lignescannee = false;

                        for(PH_Preparation_Ligne ligneCourante : listPhPreparationLigne)
                        {
                            for(PH_Preparation_Ligne ligneTemp : listePreparationScannee)
                            {
                                lignescannee = false;
                                if(ligneCourante.getProduitID() == ligneTemp.getProduitID())
                                {
                                    lignescannee = true;
                                    break;
                                }
                            }
                            if(!lignescannee)
                            {
                                PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte = new PH_Preparation_Ligne_Preparation_Adapte(ligneCourante.get_UID());
                                if(ligneCourante.getQte_APreparer() != 0)
                                {
                                    phPreparationLignePreparationAdapte_List.add(ph_preparationLigneAdapte);
                                }
                            }
                        }
                    }

                    phPreparationLigne_ListView.setDivider(footer);
                    ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter(DetailPreparationScanneeActivity.this, phPreparationLignePreparationAdapte_List, db);
                    phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
                }
                break;
                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:{
                    String id_emplacement = data.getExtras().getString("code");
                    List<Depot_Emplacement> listedepotEmplacement = EmplacementOpenHelper.getUnEmplacementParAdressage(db, id_emplacement);
                    if(listedepotEmplacement.size() > 0)
                        depotEmplacement = listedepotEmplacement.get(0);
                    else
                        depotEmplacement = EmplacementOpenHelper.getUnEmplacementByID(db, Integer.parseInt(id_emplacement));
                    versScanProduit = true;
                    onResume();
                    break;
                }
            }
            invalidateOptionsMenu();
        }
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
        MenuItem valider = menu.findItem(R.id.boutonValider);
        valider.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onClick_ActionContenant();
                return true;
            }
        });

        return true;
    }

    private void onClickTriDesignation()
    {
        Collections.sort(phPreparationLignes, new Comparator<PH_Preparation_Ligne>() {
            @Override
            public int compare(PH_Preparation_Ligne o1, PH_Preparation_Ligne o2) {
                return o1.getProduitDesignation().compareTo(o2.getProduitDesignation());
            }
        });

        phPreparationLignePreparationAdapte_List = new ArrayList<>();
        for (PH_Preparation_Ligne phPrepLigne : phPreparationLignes) {

            if (phPrepLigne.getQte_Demander() > 0) {
                List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotAdaptes = null;

                PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte = new PH_Preparation_Ligne_Preparation_Adapte(phPrepLigne.get_UID());
                Produit produit = gestionnaireProduit.getProduitByID(db, phPrepLigne.getProduitID());
                listeGTIN.add(produit.getGTIN());
                Depot depotOrigine = gestionnaireDepot.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());
                List<Stock_Lot_Emplacement_Light> stock_lot_emplacement_lightList = gestionnaireStock_Lot_Emplacement.getAllStockLotEmplacementByProduitEtDepot(db, produit, depotOrigine);

                Collections.sort(stock_lot_emplacement_lightList, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getLot().compareTo(o2.getLot());
                    }
                });
                Collections.sort(stock_lot_emplacement_lightList, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getPeremptionDate().compareTo(o2.getPeremptionDate());
                    }
                });

                double qteDemander = phPrepLigne.getQte_Demander();
                double qtePreparer = phPrepLigne.getQte_Demander();
                Boolean lotAssigne = false;

                for (Stock_Lot_Emplacement_Light stockLotEmplacement : stock_lot_emplacement_lightList) {
                    if (stockLotEmplacement.getQte() > 0) {
                        if (!lotAssigne) {
                            if (qteDemander > 0) {
                                nbLotPréparer++;
                            }
                            if (stockLotEmplacement.getQte() < qteDemander) {
                                qteDemander = qteDemander - stockLotEmplacement.getQte();
                                qtePreparer = stockLotEmplacement.getQte();
                            } else {
                                lotAssigne = true;
                                qtePreparer = qteDemander;
                            }
                            stockLotEmplacement.setQte_Preparer((int) qtePreparer);
                            if(premierpassage)
                            {
                                stockLotEmplacement.setQte_Preparer(0);
                            }
                        }
                        ph_preparationLigneAdapte.getLotAdaptes().add(ph_preparationLigneAdapte.new LotAdapte(stockLotEmplacement));
                    }
                }
                phPreparationLignePreparationAdapte_List.add(ph_preparationLigneAdapte);
            }

        }
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter(DetailPreparationScanneeActivity.this, phPreparationLignePreparationAdapte_List, db);
        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
    }

    private void onTriParPlace()
    {
        Collections.sort(phPreparationLignes, new Comparator<PH_Preparation_Ligne>() {
            @Override
            public int compare(PH_Preparation_Ligne o1, PH_Preparation_Ligne o2) {
                return o1.getEmplacementParDefaut().compareTo(o2.getEmplacementParDefaut());
            }
        });

        phPreparationLignePreparationAdapte_List = new ArrayList<>();
        for (PH_Preparation_Ligne phPrepLigne : phPreparationLignes) {

            if (phPrepLigne.getQte_Demander() > 0) {
                List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotAdaptes = null;

                PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte = new PH_Preparation_Ligne_Preparation_Adapte(phPrepLigne.get_UID());
                Produit produit = gestionnaireProduit.getProduitByID(db, phPrepLigne.getProduitID());
                listeGTIN.add(produit.getGTIN());
                Depot depotOrigine = gestionnaireDepot.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());
                List<Stock_Lot_Emplacement_Light> stock_lot_emplacement_lightList = gestionnaireStock_Lot_Emplacement.getAllStockLotEmplacementByProduitEtDepot(db, produit, depotOrigine);

                Collections.sort(stock_lot_emplacement_lightList, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getLot().compareTo(o2.getLot());
                    }
                });
                Collections.sort(stock_lot_emplacement_lightList, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getPeremptionDate().compareTo(o2.getPeremptionDate());
                    }
                });

                double qteDemander = phPrepLigne.getQte_Demander();
                double qtePreparer = phPrepLigne.getQte_Demander();
                Boolean lotAssigne = false;

                for (Stock_Lot_Emplacement_Light stockLotEmplacement : stock_lot_emplacement_lightList) {
                    if (stockLotEmplacement.getQte() > 0) {
                        if (!lotAssigne) {
                            if (qteDemander > 0) {
                                nbLotPréparer++;
                            }
                            if (stockLotEmplacement.getQte() < qteDemander) {
                                qteDemander = qteDemander - stockLotEmplacement.getQte();
                                qtePreparer = stockLotEmplacement.getQte();
                            } else {
                                lotAssigne = true;
                                qtePreparer = qteDemander;
                            }
                            stockLotEmplacement.setQte_Preparer((int) qtePreparer);
                            if(premierpassage)
                            {
                                stockLotEmplacement.setQte_Preparer(0);
                            }
                        }
                        ph_preparationLigneAdapte.getLotAdaptes().add(ph_preparationLigneAdapte.new LotAdapte(stockLotEmplacement));
                    }
                }
                phPreparationLignePreparationAdapte_List.add(ph_preparationLigneAdapte);
            }

        }
        ph_preparation_ligne_preparationLotAdapter.clear();
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter(DetailPreparationScanneeActivity.this, phPreparationLignePreparationAdapte_List, db);
        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
    }

    private void onTriParPoids()
    {
        Collections.sort(phPreparationLignes, new Comparator<PH_Preparation_Ligne>() {
            @Override
            public int compare(PH_Preparation_Ligne o1, PH_Preparation_Ligne o2) {
                return Double.compare(o1.getPoidsTotal(), o2.getPoidsTotal());
            }
        });

        phPreparationLignePreparationAdapte_List = new ArrayList<>();
        for (PH_Preparation_Ligne phPrepLigne : phPreparationLignes) {

            if (phPrepLigne.getQte_Demander() > 0) {
                List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> lotAdaptes = null;

                PH_Preparation_Ligne_Preparation_Adapte ph_preparationLigneAdapte = new PH_Preparation_Ligne_Preparation_Adapte(phPrepLigne.get_UID());
                Produit produit = gestionnaireProduit.getProduitByID(db, phPrepLigne.getProduitID());
                listeGTIN.add(produit.getGTIN());
                Depot depotOrigine = gestionnaireDepot.getDepotParReference(db, ph_preparation_Selectionne.getDepotOrigineReference());
                List<Stock_Lot_Emplacement_Light> stock_lot_emplacement_lightList = gestionnaireStock_Lot_Emplacement.getAllStockLotEmplacementByProduitEtDepot(db, produit, depotOrigine);

                Collections.sort(stock_lot_emplacement_lightList, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getLot().compareTo(o2.getLot());
                    }
                });
                Collections.sort(stock_lot_emplacement_lightList, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getPeremptionDate().compareTo(o2.getPeremptionDate());
                    }
                });

                double qteDemander = phPrepLigne.getQte_Demander();
                double qtePreparer = phPrepLigne.getQte_Demander();
                Boolean lotAssigne = false;

                for (Stock_Lot_Emplacement_Light stockLotEmplacement : stock_lot_emplacement_lightList) {
                    if (stockLotEmplacement.getQte() > 0) {
                        if (!lotAssigne) {
                            if (qteDemander > 0) {
                                nbLotPréparer++;
                            }
                            if (stockLotEmplacement.getQte() < qteDemander) {
                                qteDemander = qteDemander - stockLotEmplacement.getQte();
                                qtePreparer = stockLotEmplacement.getQte();
                            } else {
                                lotAssigne = true;
                                qtePreparer = qteDemander;
                            }
                            stockLotEmplacement.setQte_Preparer((int) qtePreparer);
                            if(premierpassage)
                            {
                                stockLotEmplacement.setQte_Preparer(0);
                            }
                        }
                        ph_preparationLigneAdapte.getLotAdaptes().add(ph_preparationLigneAdapte.new LotAdapte(stockLotEmplacement));
                    }
                }
                phPreparationLignePreparationAdapte_List.add(ph_preparationLigneAdapte);
            }

        }
        ph_preparation_ligne_preparationLotAdapter.clear();
        ph_preparation_ligne_preparationLotAdapter = new PH_Preparation_Ligne_PreparationLotAdapter(DetailPreparationScanneeActivity.this, phPreparationLignePreparationAdapte_List, db);
        phPreparationLigne_ListView.setAdapter(ph_preparation_ligne_preparationLotAdapter);
    }

    private void onMenuDatamatrixClick() {
        Intent detailPreparation_Intent = new Intent(DetailPreparationScanneeActivity.this, BarcodeCaptureActivity.class);
        // Récupération des éléments à transmettre à la prochaine activité
        Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation_Selectionne.getDepotDestinataireReference());
        String code_liste = depot.getNom() + " N° " + String.valueOf(ph_preparation_Selectionne.getUID());

        Bundle detailPreparation_Bundle = super.getBundle();

        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies")  || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            detailPreparation_Intent = new Intent(DetailPreparationScanneeActivity.this, ScannerSearchOnlyActivity.class);
            detailPreparation_Bundle.putInt("scannerContexteInt", R.string.scannerContextePreparationADH);
            detailPreparation_Bundle.putInt("PreparationID", ph_preparation_Selectionne.getUID());
            detailPreparation_Bundle.putBoolean("isBoutonSuppressionExistant", false);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                detailPreparation_Intent = new Intent(DetailPreparationScanneeActivity.this, BarcodeCaptureActivity.class);
                detailPreparation_Bundle.putBoolean("isBoutonSuppressionExistant", true);
            }
            else
            {
                detailPreparation_Intent = new Intent(DetailPreparationScanneeActivity.this, ScannerSearchOnlyActivity.class);
                detailPreparation_Bundle.putInt("scannerContexteInt", R.string.scannerContextePreparationADH);
                detailPreparation_Bundle.putInt("PreparationID", ph_preparation_Selectionne.getUID());
                detailPreparation_Bundle.putBoolean("isBoutonSuppressionExistant", false);
            }
        }


        detailPreparation_Bundle.putBoolean("doitEtreIdentique", true);
        detailPreparation_Bundle.putString("contexte", String.valueOf(R.string.scannerContextePreparationADH));

        detailPreparation_Bundle.putSerializable("Liste_Objet_Scanne", (Serializable) liste_resultat);
        detailPreparation_Bundle.putStringArrayList("ListString", (ArrayList<String>) liste_code_scannee);

        detailPreparation_Bundle.putString("Designation", ph_preparation_Selectionne.getListe());
        detailPreparation_Bundle.putInt("UserId", utilisateurConnecte.getId());
        detailPreparation_Bundle.putInt("preparationId", ph_preparation_Selectionne.getUID());
        detailPreparation_Bundle.putString("Preparation_Code", code_liste);
        detailPreparation_Bundle.putBoolean("modeRafale", false);
        detailPreparation_Bundle.putBoolean("ADH", true);
        detailPreparation_Bundle.putStringArrayList("ListGTIN", (ArrayList<String>) listeGTIN);

        detailPreparation_Intent.putExtras(detailPreparation_Bundle);
        // Appel de la prochaine activité
        DetailPreparationScanneeActivity.this.startActivityForResult(detailPreparation_Intent, CodesEchangesActivites.RETOUR_PREPARATION);
    }

    private void lancerScanEmplacement()
    {
        Intent scanEmplacement_Intent = new Intent(DetailPreparationScanneeActivity.this, BarcodeCaptureActivity.class);
        Bundle listeLotPreparation_Bundle = super.getBundle();
        listeLotPreparation_Bundle.putBoolean("doitEtreIdentique", false);
        listeLotPreparation_Bundle.putBoolean("ADH", true);
        listeLotPreparation_Bundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
        listeLotPreparation_Bundle.putBoolean("isBoutonSuppressionExistant", false);
        listeLotPreparation_Bundle.putInt("UserId", utilisateurConnecte.getId());
        listeLotPreparation_Bundle.putBoolean("modeRafale", false);

        scanEmplacement_Intent.putExtras(listeLotPreparation_Bundle);
        DetailPreparationScanneeActivity.this.startActivityForResult(scanEmplacement_Intent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
    }

    @Override
    public void onBackPressed() {
        Intent detailPreparationIntent = null;
        if(genre_preparation.contentEquals("PUF"))
        {
            detailPreparationIntent = new Intent(DetailPreparationScanneeActivity.this, ServicePreparationPufScanneeActivity.class);
        }
        else
        {
            detailPreparationIntent = new Intent(DetailPreparationScanneeActivity.this, ServicePreparationPadScanneeActivity.class);
        }
        Bundle detailPreparationBundle = super.getBundle();
        detailPreparationIntent.putExtras(detailPreparationBundle);
        DetailPreparationScanneeActivity.this.startActivity(detailPreparationIntent);
        DetailPreparationScanneeActivity.this.finish();
    }

    public void onClick_ActionContenant() {
        final HashMap<Integer, Integer> resultat = new HashMap<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_preparation, null);

        textViewNBPalette = view.findViewById(R.id.nbPaletteSelectionne);
        textViewNBCaisse = view.findViewById(R.id.nbColisSelectionne);

        /* Gestion des numbers pickers */
        textViewNBCaisse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvre une boite de dialogue avec un NumberPicker
                String title = "Saisir le nombre de caisse";
                String message = "Nombre de caisses : ";
                int maxValue = 20;
                int value = 0;

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = aNumberPicker.getValue();
                        textViewNBCaisse.setText(String.valueOf(qteAprès));
                        InputMethodManager imm = (InputMethodManager) DetailPreparationScanneeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    }
                };

                Alerte.afficherAlerteNumberPicker(DetailPreparationScanneeActivity.this, title, message, value, maxValue, onClickListener);
            }
        });

        textViewNBPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvre une boite de dialogue avec un NumberPicker
                String title = "Saisir le nombre de palette";
                String message = "Nombre de palettes : ";
                int maxValue = 20;
                int value = 0;

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = aNumberPicker.getValue();
                        textViewNBPalette.setText(String.valueOf(qteAprès));
                        InputMethodManager imm = (InputMethodManager) DetailPreparationScanneeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    }
                };

                Alerte.afficherAlerteNumberPicker(DetailPreparationScanneeActivity.this, title, message, value, maxValue, onClickListener);
            }
        });

        LinearLayout fermerAlerteLinearLayout = view.findViewById(R.id.fermerAlerteLinearLayout);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        fermerAlerteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nbPalette = 0;
                if(!textViewNBPalette.getText().toString().contentEquals(""))
                    nbPalette = Integer.parseInt(textViewNBPalette.getText().toString());

                int nbCaisse = 0;
                if(!textViewNBCaisse.getText().toString().contentEquals(""))
                    nbCaisse = Integer.parseInt(textViewNBCaisse.getText().toString());

                ph_preparation_Selectionne.setColisNB(nbCaisse);
                ph_preparation_Selectionne.setPaletteNB(nbPalette);
                alertDialog.dismiss();
                enregistrerPhPreparation();
            }
        });

    }
}
