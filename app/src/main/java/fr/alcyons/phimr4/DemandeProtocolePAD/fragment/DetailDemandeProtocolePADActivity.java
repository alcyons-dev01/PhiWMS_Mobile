package fr.alcyons.phimr4.DemandeProtocolePAD.fragment;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by jessica on 09/10/2017.
 */

public class DetailDemandeProtocolePADActivity extends ServiceActivity {

   /* Protocoles_Patients protocoles_patients;
    PH_Patient ph_patient;
    Depot depot;

    // Fragment
    InformationProtocolePatient informationProtocolePatient;
    ListeComposantPatient listeComposantPatient;

    *//* Définition de l'action sur le bouton Save se trouvant dans le Fragment InformationDotationPatient
    *
    * Afin d'avoir une meilleure visualisation de l'ensemble des données, l'Activity a été séparé en deux Fragment.
    * Cependant pour sauvegarder les informations se trouvant dans les deux Fragment, nous avons besoin de définir la fonction dans le parent.
    * En effet nous avons accès aux informations des deux Fragment directement dans le parent, les Fragment ne pouvant accéder aux informations des uns des autres.
    * Cette fonction sera par la suite passée en paramètre au fragment afin de lui affecter son bouton
    *
    * *//*
    public View.OnClickListener clicBoutonSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int compteurReussite = 0;

            // Récupération du dépot PUI
            Depot depotPUI = DepotOpenHelper.getDepotPUI(db);

            // Initialisation des données permettant de créer un PH_Préparation
            int UID = 0;
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
            String Commentaires = informationProtocolePatient.commentaire.getText().toString();
            String PreparationDate = "";
            String LivraisonPrevueDate = informationProtocolePatient.dateLivraisonProchaine.getText().toString();
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
            String previsionDateDebut = informationProtocolePatient.datePrevisionDu.getText().toString();
            String previsionDateFin = informationProtocolePatient.datePrevisionAu.getText().toString();
            Boolean URGENT = informationProtocolePatient.urgent.isChecked();
            String Motif = "";
            int preparateur_userID = 0;
            int pharmacien_userID = 0;
            double Volume = 0;
            int PaletteNB = 0;

            // Pour enregistrer les dates en base de données nous avons besoin de les transformer au format yyyy-MM-dd
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date dateFournie = dateDecodeur.parse(LivraisonPrevueDate);
                LivraisonPrevueDate = dateFormat.format(dateFournie);

                dateFournie = dateDecodeur.parse(previsionDateDebut);
                previsionDateDebut = dateFormat.format(dateFournie);

                dateFournie = dateDecodeur.parse(previsionDateFin);
                previsionDateFin = dateFormat.format(dateFournie);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Création et insertion en base du PH_Preparation
            PH_Preparation ph_preparation = new PH_Preparation(UID, Service, Erreur_Valid, PHIE_Tag, Saisie_Le, A_tel_heure, produitID, produitDesignation, Qte_demandee, Livree, Validee, Origine, Liste, depotDestinataireID, depotDestinataireReference, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, PrescripteurReference, Prescription_date, PrescripteurNom, depotOrigineReference, depotOrigineID, Commentaires, PreparationDate, LivraisonPrevueDate, DN_Groupe, Montant_HT, Montant_TTC, Poids, Commande_ID, Preparateur, Statut, PHIE_SYNCHRO, receptionUFNonComforme, livraisonDate, Frequence, previsionDateDebut, previsionDateFin, URGENT, Motif, preparateur_userID, pharmacien_userID, Volume, PaletteNB);
            int ph_preparationPHIMR4uid = (int) PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);

            // Parcours des demande_pui afin d'en faire des PH_Preparation_Ligne
            for (Demande_PUI demande_pui : listeComposantPatient.adapter.demandePuiList
                    ) {
                // Initialisation des données permettant de créer un PH_Préparation_Ligne
                int PreparationID = ph_preparation.getUID();
                int _UID = 0;
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
                PH_Preparation_Ligne ph_preparation_ligne = new PH_Preparation_Ligne(PreparationID, _UID, produitID, produitDesignation, Qte_APreparer, Qte_livrer, Livrer, Valider, ValidationDate, produitReference, ZoneDepot, produitCategorie, Qte_RAL, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, produitCondDistrib, produitPUHT, Suivi_Par_Lot, patientID, PatientNom, PrescripteurNom, prescripteurReference, Ordre_Impression, Prescription_ID, LotNumero, PeremptionDate, produitPoids, produitTVA, Montant_HT, Montant_TTC, PoidsTotal, depot_Destinataire_Reference, utilisation_Date_Prevue, Qte_besoin, Qte_StockSaisie, Qte_Demander, EmplacementParDefaut, Qte_preparer, accepter);
                int ph_preparation_lignePHIMR4uid = (int) gestionnairePH_Preparation_Ligne.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_ligne);

                long rowId = gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_lignePHIMR4uid, ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
                if (rowId != -1) {
                    compteurReussite++;
                }
            }
            if (compteurReussite == listeComposantPatient.adapter.demandePuiList.size()) {
                // Ajout du PH_Preparation au ElementASynchroniser
                gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparationPHIMR4uid, ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

                // Tentative de lancer la sychronisation
                if (OutilsGestionConnexionReseau.isServerAccessible(DetailDemandeProtocolePADActivity.this)) {
                    gestionnaireElementASynchroniser.toutSynchroniser(DetailDemandeProtocolePADActivity.this, db, utilisateurConnecte.getToken());
                }

                Toast.makeText(DetailDemandeProtocolePADActivity.this, "Demande Protocole Patient effectuée", Toast.LENGTH_SHORT).show();
                DetailDemandeProtocolePADActivity.this.finish();
                return;
            } else {
                Alerte.afficherAlerte(DetailDemandeProtocolePADActivity.this, "Alerte", "Une erreur est survenue", "alerte");
                gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
                DetailDemandeProtocolePADActivity.this.finish();
                return;
            }

        }
    };
    String dateInventaire;
    String dateLivraisonProchaine;
    String dateLivraisonSuivante;
    Integer PH_PatientSelectionneID;
    *//**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     *//*
    private SectionsPagerAdapter mSectionsPagerAdapter;
    *//**
     * The {@link ViewPager} that will host the section contents.
     *//*
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        *//* Code nécessaire à la mise en place d'une Activity contenant des Fragment*//*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_demande_protocole_patient);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        *//* Code nécessaire à l'exécution du service *//*
        PH_PatientSelectionneID = intent.getExtras().getInt("PH_PatientSelectionneID");
        ph_patient = gestionnairePH_Patient.getPH_PatientByPhiMR4UUID(db, PH_PatientSelectionneID);

        Integer depotSelectionneID = intent.getExtras().getInt("depotSelectionneID");
        depot = gestionnaireDepot.getDepotParID(db, depotSelectionneID);

        protocoles_patients = gestionnaireProtocoles_Patients.getProtocoles_PatientsByIPP(db, ph_patient.getIPP());

        dateInventaire = intent.getExtras().getString("dateInventaire");
        dateLivraisonProchaine = intent.getExtras().getString("dateLivraisonProchaine");
        dateLivraisonSuivante = intent.getExtras().getString("dateLivraisonSuivante");

        invalidateOptionsMenu();
    }

    *//**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     *//*
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Initialisation des Fragment
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    informationProtocolePatient = new InformationProtocolePatient();
                    informationProtocolePatient.setParametres(protocoles_patients, ph_patient, dateInventaire, dateLivraisonProchaine, dateLivraisonSuivante, depot.getJours_de_réserve_par_livraison());
                    informationProtocolePatient.setParametres(clicBoutonSave);
                    return informationProtocolePatient;
                case 1:
                    listeComposantPatient = new ListeComposantPatient();
                    listeComposantPatient.setParametres(protocoles_patients, ph_patient, dateInventaire, dateLivraisonSuivante, depot.getJours_de_réserve_par_livraison());
                    return listeComposantPatient;
            }
            return null;
        }

        // Définition du nombre de Fragment
        @Override
        public int getCount() {
            return 2;
        }

        // Définition des libellées des Fragment
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Informations";
                case 1:
                    return "Liste";
            }
            return null;
        }
    }*/
}