package fr.alcyons.phimr4.DemandeProtocolePAD;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Composants_patientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Composants_patient;
import fr.alcyons.phimr4.Classes.Demande_PUI;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.PH_Patient;
import fr.alcyons.phimr4.Classes.PH_Preparation;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Protocoles_Patients;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.ListViewAdapters.Demande_PUIAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.AuthentificationActivity.hasPermissions;

/**
 * Created by olivier on 02/01/2018.
 */

public class InformationProtocolePatientActivity extends ServiceActivity {

    Protocoles_Patients protocoles_patients;
    PH_Patient ph_patient;
    Depot depot;

    Calendar calendar;

    MenuItem action_demandeUrgenteMenuItem;

    TextView datePrevisionDuTextView;
    TextView datePrevisionAuTextView;
    TextView dateLivraisonProchaineTextView;
    TextView dateLivraisonSuivanteTextView;

    Demande_PUIAdapter demandePuiAdapter;

    ListView demandePuiListView;
    List<Demande_PUI> demandePuiList;

    String depotAdresse;
    String commentaire = "";

    String dateInventaireString;

    String datePrevisionDuString;
    String datePrevisionAuString;
    String dateLivraisonProchaineString;
    String dateLivraisonSuivanteString;

    String datePrevisionDuMysqlString;
    String datePrevisionAuMysqlString;
    String dateLivraisonProchaineMysqlString;
    String dateLivraisonSuivanteMysqlString;


    Integer jours_de_réserve_par_livraison;
    int depotId;
    int patientId;
    int multiplicateur = 0;

    boolean demandeUrgente = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_demande_protocole_patient);

        // Récupération des intents
        depotId = intent.getIntExtra("depotSelectionneID", 0);
        patientId = intent.getIntExtra("PH_PatientSelectionneID", 0);
        dateInventaireString = intent.getStringExtra("dateInventaire");
        dateLivraisonProchaineString = intent.getStringExtra("dateLivraisonProchaine");
        dateLivraisonSuivanteString = intent.getStringExtra("dateLivraisonSuivante");

        // Récupération des ressources
        depot = gestionnaireDepot.getDepotParID(db, depotId);
        ph_patient = gestionnairePH_Patient.getPH_PatientByPhiMR4UUID(db, patientId);
        protocoles_patients = gestionnaireProtocoles_Patients.getProtocoles_PatientsByIPP(db, ph_patient.getIPP());

        // Récupération du " Calendar " du téléphone
        calendar = Calendar.getInstance();

        String format = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);

        // Si dans l'écran précédent initialisation de la date d'inventaire sinon date du jour
        if (dateInventaireString.contentEquals("00/00/0000")) {
            dateInventaireString = simpleDateFormat.format(calendar.getTime());
        }

        datePrevisionDuString = dateInventaireString;
        datePrevisionAuString = dateLivraisonSuivanteString;

        datePrevisionDuMysqlString = updateDateFormatMysql(datePrevisionDuString);
        datePrevisionAuMysqlString = updateDateFormatMysql(datePrevisionAuString);
        dateLivraisonProchaineMysqlString = updateDateFormatMysql(dateLivraisonProchaineString);
        dateLivraisonSuivanteMysqlString = updateDateFormatMysql(dateLivraisonSuivanteString);

        // Elements graphique
        TextView nomDemandeTextView = (TextView) findViewById(R.id.nomDemande);
        nomDemandeTextView.setText(depot.getNom());
        demandePuiListView = ((ListView) findViewById(R.id.listeView));
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Calcul du multiplicateur
        *
        * La demande de protocole patient a une particularité !
        * Le besoin d'un produit dépend de la quantité par séance mutliplié par le nombre de jours à dialyser.
        * Le nombre de jours à dialyser depénd :
        *   - du nombre de jours entre le date d'inventaire et la date de livraison suivante + le nombre de jour de reserve
        *   - la fréquence de dialyse du patient
        *
        * */
        android.icu.text.SimpleDateFormat dateParser2 = new android.icu.text.SimpleDateFormat("dd/MM/yyyy");
        try {
            jours_de_réserve_par_livraison = depot.getJours_de_réserve_par_livraison();
            //Calcul date de fin = dateLivraisonSuivante + jours_de_reserve
            Date date = dateParser2.parse(dateLivraisonSuivanteString);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, jours_de_réserve_par_livraison);
            //  c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + jours_de_réserve_par_livraison);
            Date dateFin = c.getTime();

            // Date debut = dateInventaire
            date = dateParser2.parse(dateInventaireString);
            c = Calendar.getInstance();
            c.setTime(date);
            Date dateDebut = c.getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateDebut);
            Calendar calMax = Calendar.getInstance();
            calMax.setTime(dateFin);

            /* Calendar.DAY_OF_WEEK retourne un int compris entre 1 et 7
            *  Le tableau " days " nous permet d'associer le Calendar.DAY_OF_WEEK à sa représentation dans " frequence "
            *  Le premier élément du tableau est volontairement mis à blanc afin de faire correspondre " days " au Calendar.DAY_OF_WEEK
            *  Sinon ERREUR index en dehors du tableau
            * */
            String[] days = new String[]{"", "D", "L", "Ma", "Me", "J", "V", "S"};

            // Récupération de la fréquence de dialyse du patient
            String frequence = protocoles_patients.getFrequence();

            // Parcours d'une plage de date
            for (; calendar.before(calMax); calendar.add(Calendar.DATE, 1)) {
                String day = days[calendar.get(Calendar.DAY_OF_WEEK)];

                if (frequence.contains(day)) {
                    multiplicateur++;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        demandePuiList = new ArrayList<>();

        for (Composants_patient composants_patient : Composants_patientOpenHelper.getComposants_patientByProcotolesPatients(db, protocoles_patients.get_UID())) {

            Produit produit = ProduitOpenHelper.getProduitByID(db, composants_patient.getCode_produit());
            Depot depotPUI = DepotOpenHelper.getDepotPUI(db);
            Depot depotDestinataire = DepotOpenHelper.getDepotParReference(db, protocoles_patients.getDepot_Reference());

            if(produit != null)
            {
                Stock stockPUI = StockOpenHelper.getStockByProduitEtDepot(db, produit, depotPUI);
                Stock stockDestinataire = StockOpenHelper.getStockByProduitEtDepot(db, produit, depotDestinataire);

                double qteStockPui = 0;
                double qteStockDestinataire = 0;

                if (stockPUI != null)
                    qteStockPui = stockPUI.getQuantite_Actuelle();
                if (stockDestinataire != null)
                    qteStockDestinataire = stockDestinataire.getQuantite_Actuelle();

                Demande_PUI demande_pui = new Demande_PUI(composants_patient, produit, qteStockPui, qteStockDestinataire, multiplicateur);

                demandePuiList.add(demande_pui);
            }
        }

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        demandePuiAdapter = new Demande_PUIAdapter(InformationProtocolePatientActivity.this, demandePuiList);

        demandePuiListView.setAdapter(demandePuiAdapter);
        demandePuiListView.setDivider(footer);
        demandePuiListView.setItemsCanFocus(true);
        invalidateOptionsMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu action et utilisation de l'item ADD
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_demande_action, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Récupération de l'item ADD et affectation de l'action à réaliser lors d'un clic
        MenuItem action_informationsMenuItem = menu.findItem(R.id.action_informations);
        action_informationsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_Action_informations();
                return true;
            }
        });

        MenuItem action_prochaineLivraisonMenuItem = menu.findItem(R.id.action_prochaineLivraison);
        action_prochaineLivraisonMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_Action_prochaineLivraison();
                return true;
            }
        });

        action_demandeUrgenteMenuItem = menu.findItem(R.id.action_demandeUrgente);
        action_demandeUrgenteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_Action_demandeUrgente();
                return true;
            }
        });

        MenuItem action_commentaireMenuItem = menu.findItem(R.id.action_commentaire);
        action_commentaireMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_Action_commentaire();
                return true;
            }
        });

        MenuItem action_envoyerDemandeMenuItem = menu.findItem(R.id.action_envoyerDemande);
        action_envoyerDemandeMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_Action_envoyerDemande();
                return true;
            }
        });

        super.prepareOptionsMenu(menu, demandePuiAdapter, null, "Désignation, Catégorie...");

        return true;
    }

    public void onClick_Action_informations() {
        String depotNom = depot.getNom();
        String depotTelephone = depot.getTel();
        String depotFax = depot.getFax();

        // Récupération de l'adresse du dépot, attention dans le cas d'un PAD vérification de l'utilisation ou non de son adresse de vacance
        if (depot.getDepot_Reference().contains("PAD") && depot.isPAD_Utiliser_Adresse_Vacances()) {
            depotAdresse = depot.getPAD_Vacances_Adr1() + ", ";
            if (depot.getPAD_Vacances_Adr2().length() > 1) {
                depotAdresse += depot.getPAD_Vacances_Adr2() + ", ";
            }
            depotAdresse += depot.getPAD_Vacances_CP() + " " + depot.getPAD_Vacances_Ville();
        } else {
            depotAdresse = depot.getAdresse1() + ", ";
            if (depot.getAdresse2().length() > 1) {
                depotAdresse += depot.getAdresse2() + ", ";
            }
            depotAdresse += depot.getCP() + " " + depot.getVille();
        }

        Alerte.afficherAlerteInfoDepot(InformationProtocolePatientActivity.this, InformationProtocolePatientActivity.this.getLayoutInflater(), depotNom, depotAdresse, depotTelephone, depotFax);
    }

    public void onClick_Action_prochaineLivraison() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_prochaine_livraison, null);

        datePrevisionDuTextView = (TextView) view.findViewById(R.id.datePrevisionDu);
        datePrevisionAuTextView = (TextView) view.findViewById(R.id.datePrevisionAu);
        dateLivraisonProchaineTextView = (TextView) view.findViewById(R.id.dateLivraisonProchaine);
        dateLivraisonSuivanteTextView = (TextView) view.findViewById(R.id.dateLivraisonSuivante);

        /*  Gestion des dates et des DatePicker */
        // Récupération du " Calendar " du téléphone
        calendar = Calendar.getInstance();

        datePrevisionDuTextView.setText(datePrevisionDuString);

        // Initialisation de datePrevisionAu à dateLivraisonSuivanteString + jours_de_réserve_par_livraison du dépot sélectionnée
        SimpleDateFormat dateParser = new SimpleDateFormat("dd/MM/yyyy");
        try {
            jours_de_réserve_par_livraison = depot.getJours_de_réserve_par_livraison();
            Date myDate = dateParser.parse(datePrevisionAuString);
            Calendar c = Calendar.getInstance();
            c.setTime(myDate);
            c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + jours_de_réserve_par_livraison);
            Date newDate = c.getTime();
            String newFormattedDate = dateParser.format(newDate);
            datePrevisionAuTextView.setText(newFormattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateLivraisonProchaineTextView.setText(dateLivraisonProchaineString);
        dateLivraisonSuivanteTextView.setText(dateLivraisonSuivanteString);

        LinearLayout fermerAlerteLinearLayout = (LinearLayout) view.findViewById(R.id.fermerAlerteLinearLayout);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        fermerAlerteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void onClick_Action_demandeUrgente() {
        if (demandeUrgente) {
            action_demandeUrgenteMenuItem.setTitle("Demande Urgente");
            findViewById(R.id.isUrgent).setVisibility(View.GONE);
        } else {
            action_demandeUrgenteMenuItem.setTitle("Demande Non Urgente");
            findViewById(R.id.isUrgent).setVisibility(View.VISIBLE);
        }
        demandeUrgente = !demandeUrgente;
    }

    public void onClick_Action_commentaire() {
        commentaire = Alerte.afficherAlerteInfoCommantaire(this, commentaire);
    }

    public void onClick_Action_envoyerDemande() {

        boolean confirmer = Alerte.afficherAlerte(this, "Envoyer", "Êtes-vous sûr de vouloir envoyer cette demande ?", "OuiNon");
        if (confirmer) {

            boolean toutazero = true;
            for (Demande_PUI demande_pui : demandePuiAdapter.demandePuiList) {
                if(demande_pui.getA_preparer() != 0)
                {
                    toutazero = false;
                }
            }

            if(toutazero)
            {
                Alerte.afficherAlerte(this, "Erreur", "Aucune quantité n'a été renseignée", "alerte");
            }
            else
            {
                int compteurReussite = 0;

                // Récupération du dépot PUI
                Depot depotPUI = DepotOpenHelper.getDepotPUI(db);

                Random phPreparationRandom = new Random();
                int phPreparationID = phPreparationRandom.nextInt();
                if (phPreparationID > 0) {
                    phPreparationID = phPreparationID * -1;
                }

                // Initialisation des données permettant de créer un PH_Préparation
                int UID = phPreparationID;
                String Service = "";
                Boolean Erreur_Valid = false;
                String PHIE_Tag = "";
                String Saisie_Le = "";
                String A_tel_heure = "";
                int produitID = 0;
                String produitDesignation = "";
                double Qte_demandee = 0;
                Boolean Livree = false;
                Boolean Validee = false;
                String Origine = "";
                String Liste = "Dotation Patient : " + ph_patient.getNom_Usuel() + " " + ph_patient.getPrénom();
                int depotDestinataireID = depot.getDepot_UID();
                String depotDestinataireReference = depot.getDepot_Reference();
                String SYS_DT_MAJ = "";
                String SYS_HEURE_MAJ = "";
                String SYS_USER_MAJ = "";
                String PrescripteurReference = "";
                String Prescription_date = "";
                String PrescripteurNom = "";
                String depotOrigineReference = depotPUI.getDepot_Reference();
                int depotOrigineID = depotPUI.getDepot_UID();
                String Commentaires = commentaire;
                String PreparationDate = "";
                String LivraisonPrevueDate = dateLivraisonProchaineMysqlString;
                String DN_Groupe = "";
                double Montant_HT = 0;
                double Montant_TTC = 0;
                double Poids = 0;
                int Commande_ID = 0;
                String Preparateur = "";
                String Statut = "En attente";
                String PHIE_SYNCHRO = "";
                String receptionUFNonComforme = "";
                String livraisonDate = "";
                String Frequence = "";
                String previsionDateDebut = datePrevisionDuMysqlString;
                String previsionDateFin = datePrevisionAuMysqlString;
                Boolean URGENT = demandeUrgente;
                String Motif = "";
                int preparateur_userID = 0;
                int pharmacien_userID = 0;
                double Volume = 0;
                int PaletteNB = 0;
                int CaisseNB = 0;
                int Conteneur_NB = 0;
                String numero_scelle = "";

                // Création et insertion en base du PH_Preparation
                PH_Preparation ph_preparation = new PH_Preparation(UID, Service, Erreur_Valid, PHIE_Tag, Saisie_Le, A_tel_heure, produitID, produitDesignation, Qte_demandee, Livree, Validee, Origine, Liste, depotDestinataireID, depotDestinataireReference, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, PrescripteurReference, Prescription_date, PrescripteurNom, depotOrigineReference, depotOrigineID, Commentaires, PreparationDate, LivraisonPrevueDate, DN_Groupe, Montant_HT, Montant_TTC, Poids, Commande_ID, Preparateur, Statut, PHIE_SYNCHRO, receptionUFNonComforme, livraisonDate, Frequence, previsionDateDebut, previsionDateFin, URGENT, Motif, preparateur_userID, pharmacien_userID, Volume, PaletteNB, CaisseNB, Conteneur_NB, numero_scelle);
                int ph_preparationPHIMR4uid = (int) PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);

                int phPreparationLigneID = ph_preparation.getUID() + 1;

                // Parcours des demande_pui afin d'en faire des PH_Preparation_Ligne
                for (Demande_PUI demande_pui : demandePuiAdapter.demandePuiList) {

                    phPreparationLigneID++;

                    // Initialisation des données permettant de créer un PH_Préparation_Ligne
                    int PreparationID = ph_preparation.getUID();
                    int _UID = phPreparationLigneID;
                    produitID = demande_pui.getCode_produit();
                    produitDesignation = demande_pui.getDésignation();
                    int Qte_APreparer = demande_pui.getA_preparer();
                    int Qte_livrer = 0;
                    Boolean Livrer = false;
                    Boolean Valider = false;
                    String ValidationDate = "";
                    String produitReference = demande_pui.getRéférence();
                    String ZoneDepot = "";
                    String produitCategorie = demande_pui.getCatégorie();
                    int Qte_RAL = Qte_APreparer;
                    SYS_DT_MAJ = "";
                    SYS_HEURE_MAJ = "";
                    SYS_USER_MAJ = "";
                    double produitCondDistrib = demande_pui.getConditionnement();
                    double produitPUHT = 0;
                    Boolean Suivi_Par_Lot = false;
                    int patientID = 0;
                    String PatientNom = "";
                    PrescripteurNom = "";
                    String prescripteurReference = "";
                    int Ordre_Impression = 0;
                    int Prescription_ID = 0;
                    String LotNumero = "";
                    String PeremptionDate = "";
                    double produitPoids = 0;
                    double produitTVA = 0;
                    Montant_HT = 0;
                    Montant_TTC = 0;
                    double PoidsTotal = 0;
                    String depot_Destinataire_Reference = depot.getDepot_Reference();
                    String utilisation_Date_Prevue = "";
                    int Qte_besoin = demande_pui.getBesoin();
                    int Qte_StockSaisie = (int) demande_pui.getStock_destinataire();
                    int Qte_Demander = Qte_APreparer;
                    String EmplacementParDefaut = "";
                    int Qte_preparer = Qte_APreparer;
                    boolean accepter = true;

                    // Création et insertion en base du PH_Preparation_Ligne
                    PH_Preparation_Ligne ph_preparation_ligne = new PH_Preparation_Ligne(PreparationID, _UID, produitID, produitDesignation, Qte_APreparer, Qte_livrer, Livrer, Valider, ValidationDate, produitReference, ZoneDepot, produitCategorie, Qte_RAL, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, produitCondDistrib, produitPUHT, Suivi_Par_Lot, patientID, PatientNom, PrescripteurNom, prescripteurReference, Ordre_Impression, Prescription_ID, LotNumero, PeremptionDate, produitPoids, produitTVA, Montant_HT, Montant_TTC, PoidsTotal, depot_Destinataire_Reference, utilisation_Date_Prevue, Qte_besoin, Qte_StockSaisie, Qte_Demander, EmplacementParDefaut, Qte_preparer, accepter, ph_preparation.getUID());
                    int ph_preparation_lignePHIMR4uid = (int) gestionnairePH_Preparation_Ligne.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_ligne);

                    //Création de l'action utilisateur
                    Random random = new Random();
                    int actionId = random.nextInt();
                    if(actionId > 0)
                        actionId= actionId*-1;
                    SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date =new Date();
                    String date_string = parseFormat.format(date);
                    ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", ph_preparation.getUID(), "", "Demande Protocol PAD");
                    ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
                    gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                    //fin de la création de l'action utilisateur

                    long rowId = gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_lignePHIMR4uid, ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
                    if (rowId != -1) {
                        compteurReussite++;
                        //gestion des actions lignes
                        Random randomactionligne = new Random();
                        int actionligneId = randomactionligne.nextInt();
                        if(actionligneId > 0)
                            actionligneId= actionligneId*-1;

                        ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", ph_preparation_ligne.get_UID(), "", 0, (int)ph_preparation_ligne.getQte_APreparer(), ph_preparation_ligne.getProduitDesignation());
                        ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                    }
                }
                if (compteurReussite == demandePuiAdapter.demandePuiList.size()) {
                    // Ajout du PH_Preparation au ElementASynchroniser
                    gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparationPHIMR4uid, ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

                    // Tentative de lancer la sychronisation
                    if (OutilsGestionConnexionReseau.isServerAccessible(InformationProtocolePatientActivity.this)) {
                        gestionnaireElementASynchroniser.toutSynchroniser(InformationProtocolePatientActivity.this, db, utilisateurConnecte, true);
                    }

                    Toast.makeText(InformationProtocolePatientActivity.this, "Demande Protocole Patient effectuée", Toast.LENGTH_SHORT).show();
                    InformationProtocolePatientActivity.this.finish();
                    return;
                } else {
                    Alerte.afficherAlerte(InformationProtocolePatientActivity.this, "Alerte", "Une erreur est survenue", "alerte");
                    gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
                    InformationProtocolePatientActivity.this.finish();
                    return;
                }
            }
        }

    }

    private String updateDateFormatMysql(String dateString) {
        Date date;
        DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateAAfficher = "";
        try {
            date = dateDecodeur.parse(dateString);
            dateAAfficher = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateAAfficher;
    }

    //clic sur l'adresse du dépôt ouvre Google Maps
    public void maps(View v) {
        String map = "https://www.google.com/maps/search/?api=1&query=" + depotAdresse;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        startActivity(i);
    }

    //clic sur le numéro de téléphone ouvre le téléphone pour appeler le correspondant
    public void telephoner(View v) {
        // Demande des autorisations
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.CALL_PHONE
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + depot.getTel()));
            startActivity(intent);
        }
    }

}
