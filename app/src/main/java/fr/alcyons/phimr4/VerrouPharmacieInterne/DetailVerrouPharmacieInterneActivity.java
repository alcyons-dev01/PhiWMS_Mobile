package fr.alcyons.phimr4.VerrouPharmacieInterne;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Commande;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.PH_Lot_Ligne;
import fr.alcyons.phimr4.Classes.PH_Preparation;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne_VerrouPharmacie_Adapte;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.PH_Preparation_Ligne_VerrouPharmacieInterneAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.Mail;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;


public class DetailVerrouPharmacieInterneActivity extends ServiceActivity {

    public PH_Preparation phPreparationSelectionne;

    public List<PH_Preparation_Ligne_VerrouPharmacie_Adapte> phPreparationLigneVerrouPharmacieAdaptes;
    public List<Stock_Lot_Emplacement_Light> stockLotEmplacementLightList;

    public PH_Preparation_Ligne_VerrouPharmacieInterneAdapter phPreparationLigneVerrouPharmacieAdapter;

    public ListView phPreparationLigneListView;
    public PH_Preparation_Ligne_VerrouPharmacieInterneAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolderAModifier;
    public TextView datePeremptionTextView;
    public EditText numeroLotEditText;

    public TextView nbColisTotalReceptionnerTextView;
    public TextView nbColisTotalTextView;
    public Integer nbColisTotal = 0;
    public Integer NbColisTotalReceptionner = 0;

    String subjectMail;
    String bodyMail;

    PackageManager pm;

    // Permet de lancer l'activity BarcodeCaptureActivity afin de lire un codebarre
    public void decoderCodeBarre(EditText numLot, TextView date, PH_Preparation_Ligne_VerrouPharmacieInterneAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder) {
        datePeremptionTextView = date;
        numeroLotEditText = numLot;
        viewHolderAModifier = viewHolder;

        PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte = phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.get(phPreparationLigneVerrouPharmacieAdapter.viewHolderList.indexOf(viewHolderAModifier));
        String designation = phPreparationLigneVerrouPharmacieAdapte.getProduitCorrespondant().getDesignation_interne();

        Bundle detailVerrouPharmacieBundle = super.getBundle();
        detailVerrouPharmacieBundle.putBoolean("doitEtreIdentique", true);
        detailVerrouPharmacieBundle.putString("Designation", designation);
        detailVerrouPharmacieBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent detailVerrouPharmacieIntent = null;

        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            detailVerrouPharmacieIntent = new Intent(DetailVerrouPharmacieInterneActivity.this, ScannerSearchOnlyActivity.class);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                detailVerrouPharmacieIntent = new Intent(DetailVerrouPharmacieInterneActivity.this, BarcodeCaptureActivity.class);
            }
            else
            {
                detailVerrouPharmacieIntent = new Intent(DetailVerrouPharmacieInterneActivity.this, ScannerSearchOnlyActivity.class);

            }
        }

        detailVerrouPharmacieIntent.putExtras(detailVerrouPharmacieBundle);
        DetailVerrouPharmacieInterneActivity.this.startActivityForResult(detailVerrouPharmacieIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_verrou_pharmacie);

        //gestion du package manager
        pm = DetailVerrouPharmacieInterneActivity.this.getPackageManager();

        // Récupération da la préparation selectionné
        phPreparationSelectionne = gestionnairePH_Preparation.getPH_PreparationByID(db, intent.getExtras().getInt("phPreparationSelectionneID"));

        // Entete
        Depot depot = gestionnaireDepot.getDepotParID(db, phPreparationSelectionne.getDepotDestinataireID());
        ((TextView) findViewById(R.id.depotNom)).setText(depot.getNom());
        String preparationNumero = "N° " + String.valueOf(phPreparationSelectionne.getUID());
        ((TextView) findViewById(R.id.preparationNumero)).setText(preparationNumero);

        ((TextView) findViewById(R.id.commandeNumero)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.fournisseurCommande)).setVisibility(View.GONE);

        nbColisTotalReceptionnerTextView = findViewById(R.id.nbColisTotalReceptionner);
        nbColisTotalTextView = findViewById(R.id.nbColisTotal);
        // Gestion de la listView
        phPreparationLigneListView = (ListView) findViewById(R.id.listeView);
        phPreparationLigneListView.setDivider(footer);
        phPreparationLigneListView.setItemsCanFocus(true);

        // Génération de la liste de PH_Preparation_Ligne_VerrouPharmacieAdapter
        phPreparationLigneVerrouPharmacieAdaptes = new ArrayList<>();
        Commande commandeCourante = CommandeOpenHelper.getCommandeByID(db, phPreparationSelectionne.getCommande_ID());

        List<PH_Preparation_Ligne> phPreparationLignes = gestionnairePH_Preparation_Ligne.getAllPHPreparationLignesParPHPreparation(db, phPreparationSelectionne);
        for (PH_Preparation_Ligne phPreparationLigne : phPreparationLignes) {
            if (phPreparationLigne.getQte_preparer() > 0)
            {
                List<PH_Lot_Ligne> liste_ph_lot_ligne = gestionnairePH_Lot_Ligne.getListePH_Lot_LigneByPreparationLigne(db, phPreparationLigne.get_UID());
                Produit produit = gestionnaireProduit.getProduitByID(db, phPreparationLigne.getProduitID());
                stockLotEmplacementLightList = gestionnaireStock_Lot_Emplacement.getAllStock_Lot_EmplacementsByProduitIDEtCommandeNumero(db, produit, commandeCourante.getNumero());

                if (liste_ph_lot_ligne.size() > 0) {
                    for (PH_Lot_Ligne lot_ligne_courant : liste_ph_lot_ligne) {
                        int qteParLot = (int) lot_ligne_courant.getQuantite();

                        String numLot = lot_ligne_courant.getNumLot();
                        String datePeremption = lot_ligne_courant.getDatePeremption();
                        String numSerie = lot_ligne_courant.getNumSerie();
                        boolean verrouiller = lot_ligne_courant.isVerrouiller();
                        if(numSerie.contentEquals("null"))
                        {
                            numSerie = "";
                        }

                        int nbColis = recupererNbColis(phPreparationLigne.getProduitID(), qteParLot, db);
                        if(verrouiller)
                            nbColisTotal = nbColisTotal + nbColis;
                        else
                            NbColisTotalReceptionner = NbColisTotalReceptionner + nbColis;

                        phPreparationLigneVerrouPharmacieAdaptes.add(new PH_Preparation_Ligne_VerrouPharmacie_Adapte(phPreparationLigne.getProduitDesignation(), phPreparationLigne.getProduitReference(), String.valueOf(nbColis), String.valueOf(qteParLot), String.valueOf(qteParLot), numLot, datePeremption, phPreparationLigne, phPreparationSelectionne, phPreparationLigne.isSuivi_Par_Serie(), phPreparationLigne.isSerialiser_Reception(), numSerie, db, verrouiller));

                    }
                } else {
                    int qteParLot = (int) phPreparationLigne.getQte_preparer();

                    String numLot = "";
                    String datePeremption = "";
                    boolean verrouiller = false;
                    int nbColis = recupererNbColis(phPreparationLigne.getProduitID(), qteParLot, db);
                    nbColisTotal = nbColisTotal + nbColis;

                    phPreparationLigneVerrouPharmacieAdaptes.add(new PH_Preparation_Ligne_VerrouPharmacie_Adapte(phPreparationLigne.getProduitDesignation(), phPreparationLigne.getProduitReference(), String.valueOf(nbColis), String.valueOf(qteParLot), String.valueOf(qteParLot), numLot, datePeremption, phPreparationLigne, phPreparationSelectionne, phPreparationLigne.isSuivi_Par_Serie(), phPreparationLigne.isSerialiser_Reception(), phPreparationLigne.getSerieNumero(), db, verrouiller));
                }
            }
        }

        nbColisTotalTextView.setText(String.valueOf(nbColisTotal));
        nbColisTotalReceptionnerTextView.setText(String.valueOf(NbColisTotalReceptionner));
    }

    @Override
    public void onResume() {
        super.onResume();
        Collections.sort(phPreparationLigneVerrouPharmacieAdaptes, new Comparator<PH_Preparation_Ligne_VerrouPharmacie_Adapte>() {
            @Override
            public int compare(PH_Preparation_Ligne_VerrouPharmacie_Adapte o1, PH_Preparation_Ligne_VerrouPharmacie_Adapte o2) {
                return o1.getDesignation().compareTo(o2.getDesignation());
            }
        });

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        phPreparationLigneVerrouPharmacieAdapter = new PH_Preparation_Ligne_VerrouPharmacieInterneAdapter(DetailVerrouPharmacieInterneActivity.this, db, phPreparationLigneVerrouPharmacieAdaptes);

        // Permet de mettre à jour les données se trouvant pas dans l'adapter mais dépend de lui
        phPreparationLigneVerrouPharmacieAdapter.setOnDataChangeListener(new PH_Preparation_Ligne_VerrouPharmacieInterneAdapter.OnDataChangeListener() {
            public void onDataChanged(int quantitéAvant, int quantitéAprès) {
                Integer nbColisTotal = Integer.parseInt(((TextView) findViewById(R.id.nbColisTotal)).getText().toString());
                Integer somme = nbColisTotal - quantitéAvant + quantitéAprès;
                ((TextView) findViewById(R.id.nbColisTotal)).setText(String.valueOf(somme));
            }
        });

        phPreparationLigneListView.setAdapter(phPreparationLigneVerrouPharmacieAdapter);

        invalidateOptionsMenu();
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    String codeComplet = data.getStringExtra("code");
                    if(codeComplet != null && !codeComplet.contentEquals(""))
                    {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeComplet);
                        if (gs1Decoupe.size() != 1) {
                            if (datePeremptionTextView != null && numeroLotEditText != null)
                            {
                                PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte = phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.get(phPreparationLigneVerrouPharmacieAdapter.viewHolderList.indexOf(viewHolderAModifier));

                                Produit produitScanner = ProduitOpenHelper.getUnProduitParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                                if(produitScanner != null)
                                {
                                    if(produitScanner.getID_produit() == phPreparationLigneVerrouPharmacieAdapte.getProduitCorrespondant().getID_produit())
                                    {
                                        if(OutilsDecodage.numeroLot.contentEquals(phPreparationLigneVerrouPharmacieAdapte.getNumLot()))
                                        {
                                            DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                            DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                            Date date = new Date();

                                            try {
                                                date = dateFormat1.parse(gs1Decoupe.get(OutilsDecodage.dateDePeremption));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            String dateFinale = dateFormat2.format(date);

                                           if(dateFinale.contentEquals(phPreparationLigneVerrouPharmacieAdapte.getPeremptionDate()))
                                           {
                                               datePeremptionTextView.setText(dateFinale);
                                               numeroLotEditText.setText(gs1Decoupe.get(OutilsDecodage.numeroLot));


                                               viewHolderAModifier.valeurDate = dateFinale;
                                               viewHolderAModifier.valeurLot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                                               mettreAJourPHPrepLigne(viewHolderAModifier, phPreparationLigneVerrouPharmacieAdapte);
                                           }
                                           else
                                           {
                                               Toast toast = Toast.makeText(DetailVerrouPharmacieInterneActivity.this, "La date de péremtpion ne correspond pas à la date attendu", Toast.LENGTH_SHORT);
                                               toast.setGravity(Gravity.CENTER, 0, 0);
                                               toast.show();
                                           }
                                        }
                                        else
                                        {
                                            Toast toast = Toast.makeText(DetailVerrouPharmacieInterneActivity.this, "Le lot scanné ne correspond pas au lot sélectionné", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }
                                    }
                                    else
                                    {
                                        Toast toast = Toast.makeText(DetailVerrouPharmacieInterneActivity.this, "Le produit scanné ne correspond pas au produit attendu", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                }
                                else
                                {
                                    Toast toast = Toast.makeText(DetailVerrouPharmacieInterneActivity.this, "Le produit scanné inconnu", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                            else
                            {
                                Toast toast = Toast.makeText(DetailVerrouPharmacieInterneActivity.this, "Le produit scanné ne correspond pas au produit attendu", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                        else
                        {
                            Toast toast = Toast.makeText(DetailVerrouPharmacieInterneActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                    else
                    {

                    }
                    invalidateOptionsMenu();
                    break;
            }
        }
    }

    // Permet de calculer les nombre de colis en fonction du conditionnement du produit
    public static int recupererNbColis(int produitID, double qte, SQLiteDatabase db) {
        int nbColis = 0;

        int conditionnementAchat = 0;
        int quantite = (int) qte;

        if (produitID != 0) {
            Produit produitCorrespondant = ProduitOpenHelper.getProduitByID(db, produitID);
            conditionnementAchat = produitCorrespondant.getCond_achat();
            if (conditionnementAchat == 0) {
                conditionnementAchat = (int) produitCorrespondant.getCond_distrib();
            }
        }
        if (quantite != 0 && conditionnementAchat != 0) {
            nbColis = quantite / conditionnementAchat;
            nbColis = (int) Math.ceil(nbColis);
        }
        if (quantite != 0) {
            if (nbColis == 0) {
                nbColis = 1;
            }
        }

        return nbColis;
    }

    // Permet de mettre à jour un PH_Preparation_Ligne
    public void mettreAJourPHPrepLigne(PH_Preparation_Ligne_VerrouPharmacieInterneAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder, PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte) {
        PH_Preparation_Ligne phPreparationLigne = phPreparationLigneVerrouPharmacieAdapte.getPhPreparationLigne();
        Stock_Lot_Emplacement_Light stockLotEmplacementLight = phPreparationLigneVerrouPharmacieAdapte.getStockLotEmplacement();
        phPreparationLigneVerrouPharmacieAdapte.setVerouiller(false);
        PH_Lot_Ligne lot_ligne_courant = PH_Lot_LigneOpenHelper.getListePH_Lot_LigneByPreparationLigneLotSerie(db, phPreparationLigne, phPreparationLigneVerrouPharmacieAdapte.getNumLot(), phPreparationLigneVerrouPharmacieAdapte.getSerieNumero());
        if(lot_ligne_courant != null)
        {
            lot_ligne_courant.setVerrouiller(false);
            PH_Lot_LigneOpenHelper.mettreAJourPHLotLigne(db, lot_ligne_courant);
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

        if(stockLotEmplacementLight != null)
        {
            stockLotEmplacementLight.setLot(viewHolder.valeurLot);
            stockLotEmplacementLight.setQte_Preparer(viewHolder.valeurQteParLot);
        }
        try {
            Date dateFournie = dateDecodeur.parse(viewHolder.valeurDate);
            if(stockLotEmplacementLight != null)
                stockLotEmplacementLight.setPeremptionDate(dateFormat.format(dateFournie));
            phPreparationLigneVerrouPharmacieAdapte.setPeremptionDate(dateFormat.format(dateFournie));
            phPreparationLigne.setPeremptionDate(dateFormat.format(dateFournie));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(stockLotEmplacementLight != null)
            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacementLight);

        phPreparationLigneVerrouPharmacieAdapte.setNumLot(viewHolder.valeurLot);
        phPreparationLigneVerrouPharmacieAdapte.setQteParLot(String.valueOf(viewHolder.valeurQteParLot));

        phPreparationLigne.setLotNumero(viewHolder.valeurLot);
        phPreparationLigne.setQte_RAL(viewHolder.valeurQteParLot);

        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, phPreparationLigne);
    }

    @Override
    public void onBackPressed() {

        DetailVerrouPharmacieInterneActivity.this.finish();
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

                boolean deverouiller = true;
                int compteurVerrouiller = 0;
                for(int i = 0; i < phPreparationLigneVerrouPharmacieAdapter.viewHolderList.size(); i++)
                {
                    PH_Preparation_Ligne_VerrouPharmacieInterneAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder = phPreparationLigneVerrouPharmacieAdapter.viewHolderList.get(i);

                    String statut = viewHolder.valeurStatut;

                    if(statut.contentEquals("Vérouillée"))
                    {
                        compteurVerrouiller ++;
                        deverouiller = false;
                    }
                }

                if(deverouiller)
                {
                    onClick_ActionValider();
                }
                else if(compteurVerrouiller == phPreparationLigneVerrouPharmacieAdapter.viewHolderList.size())
                {
                    afficherAlerteConfirmation(DetailVerrouPharmacieInterneActivity.this, DetailVerrouPharmacieInterneActivity.this.getLayoutInflater(), "all");
                }
                else
                {
                    afficherAlerteConfirmation(DetailVerrouPharmacieInterneActivity.this, DetailVerrouPharmacieInterneActivity.this.getLayoutInflater(), "reliquat");
                }
                return true;
            }
        });

        return true;
    }

    public void onClick_ActionValider()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDuJour = Calendar.getInstance().getTime();

        boolean confirmation;
        confirmation = Alerte.afficherAlerte(DetailVerrouPharmacieInterneActivity.this, "Validation", "Êtes-vous sur de vouloir valider ?", "OuiNon");

        if (confirmation) {

            //création de l'action
            Random random = new Random();
            int actionId = random.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date =new Date();
            String date_string = parseFormat.format(date);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", phPreparationSelectionne.getUID(), "", "Verrou Pharmacie interne");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            //fin de la création de l'action utilisateur

            int compteurReussite = 0;

            for (PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte : phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList) {
                PH_Preparation_Ligne_VerrouPharmacieInterneAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder = phPreparationLigneVerrouPharmacieAdapter.viewHolderList.get(phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.indexOf(phPreparationLigneVerrouPharmacieAdapte));
                PH_Preparation_Ligne phPreparationLigne = phPreparationLigneVerrouPharmacieAdapte.getPhPreparationLigne();

                compteurReussite++;
                //gestiond des action utilisateur ligne pour le verrou pharmacie
                Random randomactionligne = new Random();
                int actionligneId = randomactionligne.nextInt();
                if(actionligneId > 0)
                    actionligneId= actionligneId*-1;

                ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", phPreparationLigne.get_UID(), "", 0, phPreparationLigne.getQte_RAL(), phPreparationLigne.getProduitDesignation());
                ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
            }

            if (compteurReussite == phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.size()) {

                String statutCourant = phPreparationSelectionne.getStatut();
                if(statutCourant.contentEquals("Préparée"))
                {
                    phPreparationSelectionne.setStatut(getString(R.string.statutDélivrer));
                }
                else
                {
                    phPreparationSelectionne.setStatut(getString(R.string.statutDélivrerEnPartie));
                }

                phPreparationSelectionne.setSYS_USER_MAJ(utilisateurConnecte.getIdentifiant());

                long rowID = gestionnairePH_Preparation.mettreAJourUnPHPreparation(db, phPreparationSelectionne);
                if (rowID != -1) {
                    gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, phPreparationSelectionne.getPhiMR4UUID(), phPreparationSelectionne.getUID(), DBOpenHelper.ActionsEAS.MAJ);
                } else {
                    compteurReussite = 0;
                }
            }

            if (compteurReussite != phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.size()) {
                Alerte.afficherAlerte(DetailVerrouPharmacieInterneActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
                gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
                DetailVerrouPharmacieInterneActivity.this.finish();
                return;
            }


            Toast.makeText(DetailVerrouPharmacieInterneActivity.this, "Préparation interne déverrouillée", Toast.LENGTH_SHORT).show();

            if (OutilsGestionConnexionReseau.isServerAccessible(DetailVerrouPharmacieInterneActivity.this)) {
                gestionnaireElementASynchroniser.toutSynchroniser(DetailVerrouPharmacieInterneActivity.this, db, utilisateurConnecte, true);
            }
            onBackPressed();
        }
    }

    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater, final String statut) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_quitter, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageFin = (TextView) layout.findViewById(R.id.messageFin);
        TextView titreConfirmation = (TextView) layout.findViewById(R.id.TitreConfirmation);
        TextView titreAnnulation = (TextView) layout.findViewById(R.id.TitreAnnulation);

        if(statut.contentEquals("all"))
        {
            messageFin.setText(Html.fromHtml("<b>Aucune ligne dévérouillée.</b><br> Souhaitez-vous notifier le refus de  cette préparation ?"));
            titreConfirmation.setText("Oui");
            titreAnnulation.setText("Non en attente");
        }
        else
        {
            messageFin.setText(Html.fromHtml("<b>Toutes les lignes n\'ont pas été déverrouillée.</b><br> Cette préraration restera partiellement vérrouillée ?"));
            titreConfirmation.setText("Oui");
            titreAnnulation.setText("Annuler");
        }

        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.setCancelable(false);
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statut.contentEquals("all"))
                {
                    subjectMail = "PhiMR4 - Délivrance n°" + phPreparationSelectionne.getUID();

                    bodyMail = "Madame, Monsieur, \n \n" +
                            "La délivrance N°" + phPreparationSelectionne.getUID()+ " a été refusée par "+utilisateurConnecte.getNom()+" "+utilisateurConnecte.getPrenom()+". \n" +
                            "Veuillez annuler la délivrance sur PhiR4 et refaire une demande de préparation \n\n" +
                            "Cordialement, \n\n" +
                            "Support Alcyons \n\n" +
                            "Ceci est un message automatique merci de ne pas répondre";

                    // Récupération Mail Pharmacie
                    String email = ParametresServeurOpenHelper.getMailPharmacie(db);

                    if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
                    {
                        email = "dev01@alcyons.fr";
                    }

                    if (email != null) {
                        new SendEmailTask().execute(email);
                    }
                }
                else
                {

                }
                alertDialog.dismiss();
                DetailVerrouPharmacieInterneActivity.this.finish();
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if(statut.contentEquals("all"))
                    DetailVerrouPharmacieInterneActivity.this.finish();
            }
        });
    }

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {
            Mail sender = null;
            sender = new Mail(DetailVerrouPharmacieInterneActivity.this, email[0], true, db);

            try {
                sender.sendMailVerification(subjectMail, bodyMail);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }

    public void majColis(PH_Lot_Ligne ph_lot_ligne, boolean verrouiller)
    {
        PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, ph_lot_ligne.getDocLigneId());
        int nbColis = recupererNbColis(ph_preparation_ligne.getProduitID(), ph_lot_ligne.getQuantite(), db);

        if(verrouiller)
        {
            nbColisTotal = nbColisTotal + nbColis;
            NbColisTotalReceptionner = NbColisTotalReceptionner - nbColis;
        }
        else
        {
            NbColisTotalReceptionner = NbColisTotalReceptionner + nbColis;
            nbColisTotal = nbColisTotal - nbColis;
        }

        nbColisTotalTextView.setText(String.valueOf(nbColisTotal));
        nbColisTotalReceptionnerTextView.setText(String.valueOf(NbColisTotalReceptionner));
    }
}
