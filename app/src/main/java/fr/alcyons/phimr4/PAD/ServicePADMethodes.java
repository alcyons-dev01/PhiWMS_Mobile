package fr.alcyons.phimr4.PAD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Composants_patientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Dotation_PatientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.FrequencesOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.MVT_DepotsOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Prescription_patientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Protocoles_PatientsOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Composants_patient;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Dotation_Patient;
import fr.alcyons.phimr4.Classes.Frequences;
import fr.alcyons.phimr4.Classes.MVT_Depots;
import fr.alcyons.phimr4.Classes.PAD_Proposition_Ligne;
import fr.alcyons.phimr4.Classes.PH_Patient;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.Preparation;
import fr.alcyons.phimr4.Classes.Preparation_Ligne;
import fr.alcyons.phimr4.Classes.Prescription_patient;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Protocoles_Patients;
import fr.alcyons.phimr4.Classes.Service;
import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.Outils.Alerte;


/*
 * Created by olivier on 09/03/2018.
*/


public class ServicePADMethodes {


    public Date Vdate_dim_liv;
    public Date VD_Livraison_Max;

    public Date Vdate_inv;
    public Date Vdate_liv;
    public Date Vdate_liv_suivante;

    public Date Vdate_debut;

    Preparation preparation;

    SQLiteDatabase db;

    int Vnb_jours_reserve;

    List<Produit> T_Fprod;
    List<Integer> T_prod = new ArrayList();

    List<String> Tref_depot_tab;
    List<String> T_ref;
    List<Double> T_qte;
    List<Double> T_Stock;
    List<String> T_ipp;
    List<String> T_patient;
    List<Boolean> T_Dotation;
    List<Double> T_reste_a_consome;
    List<Double> T_nb_patient;
    List<Double> T_Reliquat;
    List<Boolean> T_prescription;
    List<String> ListeDate;

    Preparation_Ligne preparation_ligne;

    List<PAD_Proposition_Ligne> padPropositionLigneList;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat recupJour = new SimpleDateFormat("EEEE");

    String referenceCycle;
    int idcycle;
    String CycleDu;
    String CycleAu;

    String IPP;
    PH_Patient patient;
    Depot depotPatient;
    List<Protocoles_Patients> protocolePatient;

    Context context;


    public ServicePADMethodes(Context context, SQLiteDatabase db, String referenceCycle, int idcycle, String CycleDu, String CycleAu, String IPP, PH_Patient patient, Depot depotPatient, List<Protocoles_Patients> protocolePatient, List<String> ListeDate, List<PAD_Proposition_Ligne> padPropositionLigneList) {
        Tref_depot_tab = new ArrayList<>();
        T_ref = new ArrayList<>();
        T_qte = new ArrayList<>();
        T_Stock = new ArrayList<>();
        T_ipp = new ArrayList<>();
        T_patient = new ArrayList<>();
        T_Dotation = new ArrayList<>();
        T_reste_a_consome = new ArrayList<>();
        T_nb_patient = new ArrayList<>();
        T_Reliquat = new ArrayList<>();
        T_prescription = new ArrayList<>();

        this.db = db;
        this.referenceCycle = referenceCycle;
        this.idcycle = idcycle;
        this.CycleDu = CycleDu;
        this.CycleAu = CycleAu;
        this.IPP = IPP;
        this.patient = patient;
        this.depotPatient = depotPatient;
        this.protocolePatient = protocolePatient;
        this.ListeDate = ListeDate;
        this.context = context;
        this.padPropositionLigneList = padPropositionLigneList;

    }

    private Date recupererProchaineLivraison(List<String> listeDate) throws ParseException {

        Date dateProchaineLivraison = null;
        Date dateDuJour = new Date();
        for (String dateEnCours : listeDate) {
            Date dateEnCoursDate = format.parse(dateEnCours);
            if (dateDuJour.compareTo(dateEnCoursDate) <= 0) {
                dateProchaineLivraison = dateEnCoursDate;
                break;
            }
        }
        return dateProchaineLivraison;
    }

    private Date recupererDateLivraisonSuivante(List<String> listeDate, Date Vdate_liv) throws ParseException {
        Date dateLivraisonSuivante = null;

        for (String dateEnCours : listeDate) {
            Date dateEnCoursDate = format.parse(dateEnCours);
            if (Vdate_liv.compareTo(dateEnCoursDate) < 0) {
                dateLivraisonSuivante = dateEnCoursDate;
                break;
            }
        }
        return dateLivraisonSuivante;
    }

    public int prevision_calculer(Date previsionDate) throws ParseException {
        Vnb_jours_reserve = depotPatient.getJours_de_réserve_par_livraison();
        int depot_en_cours = 0;
        int num_depot = depotPatient.getDepot_UID();
        Vdate_dim_liv = previsionDate;
        Vdate_liv = recupererProchaineLivraison(ListeDate);
        Vdate_liv_suivante = recupererDateLivraisonSuivante(ListeDate, Vdate_liv);
        Vdate_inv = new Date();
        List<Preparation_Ligne> preparationLigne = new ArrayList<>();


        int compteur = 0;
        for (int i = 0; i <= Tref_depot_tab.size(); i++) {
            String statut = "";
            String message = "";
            if (depotPatient.getStructure().equals("PAD")) {
                //Requete du protocole avec DepotUID, Interruption=false, Archiver == false
                List<Protocoles_Patients> protocolePatient = new ArrayList<>();
                protocolePatient = Protocoles_PatientsOpenHelper.getProtocoles_PatientsByDepot(db, depotPatient.getDepot_UID());

                if (protocolePatient == null) {
                    message = "Veuillez renseigner un protocole";
                    statut = "impossible";
                }
            }

            if (statut.equals("impossible")) {
                Alerte.afficherAlerte(context, "Erreur", message, "alerte");
            } else if (format.format(Vdate_liv).equals("0000-00-00") || format.format(Vdate_dim_liv).equals("0000-00-00")) {
                Alerte.afficherAlerte(context, "Erreur", "Les dates de livraisons ne semblent pas correctes. Veuillez les controler.", "alerte");
            } else {
                //créer une préparation (lecture-ecriture préparation)
                //chercher préparation par refdepot et par prevision date
                preparation = PreparationOpenHelper.getPreparationByDepot(db, depotPatient.getDepot_UID());

                if (preparation == null) {
                    //créer une nouvelle preparation
                    //id générer random
                    Random random = new Random();
                    int preparationID = random.nextInt();
                    if (preparationID > 0) {
                        preparationID = preparationID * -1;
                    }
                    preparation = new Preparation();
                    preparation.setID(preparationID);
                    preparation.setDomicile(true);


                    preparation.setMontant_HT(0);
                    preparation.setMontant_TTC(0);
                    preparation.setRef_depot(depotPatient.getDepot_Reference().trim());
                    preparation.setID_Depot(num_depot);
                    preparation.setNom_Patient(depotPatient.getPAD_Patient().trim());
                    preparation.setIPP_Patient(depotPatient.getPAD_IPP().trim());
                    preparation.setDate_Prevision(format.format(previsionDate));
                    preparation.setStatut("mobile");
                }

                preparation.setValidée(false);
                preparation.setID_Cycle(idcycle);
                preparation.setCycle(referenceCycle.trim());

                preparation.setAnnee(Integer.parseInt(preparation.getCycle().substring(0, 4)));
                preparation.setMois(Integer.parseInt(preparation.getCycle().substring(4, 6)));

                preparation.setCycle_Depot(referenceCycle + depotPatient.getDepot_Reference().trim());

                preparation.setDate_inventaire(format.format(Vdate_inv));


                preparation.setDate_Livraison1(format.format(Vdate_liv));
                preparation.setDate_Livraison2(format.format(Vdate_liv_suivante));



                PreparationOpenHelper.insererUnPreparationEnBDD(db, preparation);

                Vdate_debut = Vdate_inv;

                List<Preparation_Ligne> preparation_ligneList = new ArrayList<>();

                preparation_ligneList = Preparation_LigneOpenHelper.getPreparationLigneByPreparation(db, preparation.getID());

                if (depotPatient.getStructure().equals("PAD")) {
                    T_Fprod = ProduitOpenHelper.getProduitPAD(db);

                    for (Produit produit : T_Fprod) {

                        T_prod.add(produit.getID_produit());
                    }

                    P_calc_Prev_pat(Vdate_debut, previsionDate);

                    //utiliser ensemble "mes protos"
                    preparationLigne = Preparation_LigneOpenHelper.getPreparationLigneByPreparation(db, preparation.getID());
                    Prevision_valeur_Calculer();
                    PreparationOpenHelper.insererUnPreparationEnBDD(db, preparation);


                    T_prod = new ArrayList<>();
                    T_Fprod = new ArrayList<>();
                }
            }
        }

        return preparation.getID();
    }

    public void elementASynchroniser_inserer(Utilisateur utilisateur, Service service){
        //Création de l'action utilisateur
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateur.getId(), date_string, service.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", preparation.getID(), "", "PAD");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
        //fin de la création de l'action utilisateur

        for (Preparation_Ligne preparation_ligne : Preparation_LigneOpenHelper.getPreparationLigneByPreparation(db, preparation.getID())) {
            long rowId = ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Preparation_LigneOpenHelper.Constantes.TABLE_PREPARATION_LIGNE, preparation_ligne.getPhiMR4UUID(), preparation_ligne.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
            if (rowId == -1) {
                Alerte.afficherAlerte(context, "Erreur", "Erreur d'ajout à la table de synchronisation pour la preparation ligne, abandon de l'action", "alerte");
                ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
            }
            else
            {
                //gestion des actions lignes
                Random randomactionligne = new Random();
                int actionligneId = randomactionligne.nextInt();
                if(actionligneId > 0)
                    actionligneId= actionligneId*-1;

                ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", preparation_ligne.get_UID(), "", 0, (int)preparation_ligne.getQte_Besoin(), preparation_ligne.getProduit());
                ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
            }
        }

        //ajout de la synchronisation de la preparation
        long rowID = ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PreparationOpenHelper.Constantes.TABLE_PREPARATION, preparation.getPhiMR4UUID(), preparation.getID(), DBOpenHelper.ActionsEAS.AJOUT);
        if (rowID == -1) {
            Alerte.afficherAlerte(context, "Erreur", "Erreur d'ajout à la table de synchronisation pour la preparation, abandon de l'action", "alerte");
            ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);

        }
    }

    public void P_calc_Prev_pat(Date date_debut, Date date_fin) throws ParseException {
        int qte = 0;
        int coeff = 0;
        int coeff_prochaine_liv = 0;


        List<Composants_patient> Proto_Encours = new ArrayList<>();
        List<Dotation_Patient> Dot_Encours = new ArrayList<>();
        List<Prescription_patient> liste_precription = new ArrayList<>();
        List<Prescription_patient> Ordres_Presc_Encours = new ArrayList<>();

        //protocolePatient récupérer au début de l'activité
        for (Protocoles_Patients protocoles_patients : protocolePatient) {
            coeff_prochaine_liv = P_calc_Prev_coef(Vdate_inv, Vdate_liv, null);
            coeff = P_calc_Prev_coef(Vdate_debut, date_fin, null);


            coeff = coeff + Vnb_jours_reserve;
            coeff_prochaine_liv = coeff_prochaine_liv + Vnb_jours_reserve;


            //*********** COMPOSANT PATIENT ************
            Proto_Encours = Composants_patientOpenHelper.getComposants_patientByProcotolesPatients(db, protocoles_patients.get_UID());
            for (Composants_patient composant_en_cours : Proto_Encours) {
                P_calcul_Proto_Composant(coeff, coeff_prochaine_liv, composant_en_cours, protocoles_patients);
            }


            //*********** PRESCRIPTION PATIENT ************

            liste_precription = Prescription_patientOpenHelper.getPrescriptionByDate(db, date_debut);

            for (Prescription_patient prescription_patient : liste_precription) {
                P_Calcul_Proto_Prescription(prescription_patient);
            }

        }


        //*********** DOTATION PATIENT ************
        Dot_Encours = Dotation_PatientOpenHelper.getDotation_PatientByProcotolesPatients(db, protocolePatient.get(0).get_UID());
        for (Dotation_Patient dotation_en_cours : Dot_Encours) {
            P_Calcul_Proto_Dotation_Patient(coeff_prochaine_liv, dotation_en_cours);
        }


        for (int i = 0; i < T_ref.size(); i++)

        {
            int depot = Integer.parseInt(T_ref.get(i).substring(0, 10));
            String ref_depot = T_ref.get(i).substring(21);
            int prod = Integer.parseInt(T_ref.get(i).substring(11, 21));
            int liv_dir = Integer.parseInt(T_ref.get(i).substring(10, 11)); //$liv_dir:=(Num(Sous chaine(T_ref{$i};11;1))=1)
            P_creer_flux_part(prod, depot, ref_depot, T_qte.get(i), liv_dir, "", "", T_Stock.get(i), T_Dotation.get(i), T_reste_a_consome.get(i), T_nb_patient.get(i), T_Reliquat.get(i), T_prescription.get(i));
        }

    }

    public int P_calc_Prev_coef(Date Date_debut_coeff, Date Date_fin, String frequence) {

        Calendar c = new GregorianCalendar();
        int index = 0;
        Date date_en_cours = c.getTime();
        Date_fin.setTime(Date_fin.getTime() + 1 * 24 * 60 * 60 * 1000);


        Boolean TLJ = false;

        if (Date_debut_coeff == null && Date_fin == null) {
            Date_debut_coeff = new Date();
            Date_fin = new Date();
        }

        double coefReturn = 0;

        if (frequence == null) {
            frequence = protocolePatient.get(0).getFrequence();
        }

        //récupérer "ident"
        Frequences frequenceIdent = FrequencesOpenHelper.getFrequencesByIdent(db, frequence);

        if (frequenceIdent == null) {
            coefReturn = 0;
        } else if (format.format(Date_debut_coeff).equals("0000-00-00") && format.format(Date_fin).equals("0000-00-00")) {
            Alerte.afficherAlerte(context, "Erreur", "Ce dépôt n'a pas de livraison programmée !", "alerte");
        } else {
            date_en_cours = Date_debut_coeff;

            while (date_en_cours.before(Date_fin)) {

                String jour = (String) recupJour.format(date_en_cours);

                if (frequenceIdent.isL1() && jour.equals("lundi")) {
                    coefReturn = coefReturn + 1;
                }

                if (frequenceIdent.isMa() && jour.toLowerCase().equals("mardi")) {
                    coefReturn = coefReturn + 1;
                }

                if (frequenceIdent.isMer() && jour.toLowerCase().equals("mercredi")) {
                    coefReturn = coefReturn + 1;
                }

                if (frequenceIdent.isJ() && jour.toLowerCase().equals("jeudi")) {
                    coefReturn = coefReturn + 1;
                }

                if (frequenceIdent.isV() && jour.toLowerCase().equals("vendredi")) {
                    coefReturn = coefReturn + 1;
                }

                if (frequenceIdent.isS() && jour.toLowerCase().equals("samedi")) {
                    coefReturn = coefReturn + 1;
                }

                if (frequenceIdent.isD() && jour.toLowerCase().equals("dimanche")) {
                    coefReturn = coefReturn + 1;
                }

                date_en_cours.setTime(date_en_cours.getTime() + 1 * 24 * 60 * 60 * 1000);
                index++;
            }

            if (frequenceIdent.isL1() && frequenceIdent.isMa() && frequenceIdent.isMer() && frequenceIdent.isJ() && frequenceIdent.isV() && frequenceIdent.isS() && frequenceIdent.isD()) {
                TLJ = true;
            }

            if (TLJ && frequenceIdent.isL2()) {
                coefReturn = coefReturn / 2;
                if (coefReturn > 0) {
                    coefReturn = (int) coefReturn + 1;
                }
            }
        }

        int coeff = (int) coefReturn;

        date_en_cours.setTime(date_en_cours.getTime() - index * 24 * 60 * 60 * 1000);
        return coeff;
    }

    public void P_calcul_Proto_Composant(int coeff, int coeff_prochaine_liv, Composants_patient composants_patient, Protocoles_Patients protocoles_patients) {
        int depotID = protocoles_patients.getDepot_Code();
        String ref_depot = protocoles_patients.getDepot_Reference();
        String IPP = protocoles_patients.getIPP();
        String patient = protocoles_patients.getPatient_Identite();
        Boolean liv_dir_Bool = composants_patient.isLivraison_Directe();
        String liv_dir = "0";
        if (liv_dir_Bool) {
            liv_dir = "1";
        }

        String depot_string = String.valueOf(depotID);
        while (depot_string.length() < 10) {
            depot_string = "0" + depot_string;
        }

        int prodID = composants_patient.getCode_produit();
        String prod_string = String.valueOf(prodID);
        while (prod_string.length() < 10) {
            prod_string = "0" + prod_string;
        }

        String ref = depot_string + liv_dir + prod_string + ref_depot;

        double qte = composants_patient.getQté() * coeff;
        double stock = P_rapprochement_stock_previs(prodID, ref_depot);
        double reste_a_consome = Math.round(composants_patient.getQté() * coeff_prochaine_liv);
        double qte_reliquat = P_reliquat(depotID, prodID, IPP);

        int x = T_ref.indexOf(ref);
        if (x == -1) {
            T_ref.add(ref);
            T_ipp.add(IPP);
            T_patient.add(patient);
            T_qte.add(qte);
            T_Stock.add(stock);
            T_Reliquat.add(qte_reliquat);
            T_Dotation.add(false);
            T_reste_a_consome.add(reste_a_consome);
            T_nb_patient.add(1.0);
            T_prescription.add(false);
        } else {
            T_qte.set(x, T_qte.get(x) + qte);
            T_reste_a_consome.set(x, T_reste_a_consome.get(x) + reste_a_consome);
            T_nb_patient.set(x, T_nb_patient.get(x) + 1);
        }
    }

    public int P_rapprochement_stock_previs(int ProduitID, String DepotReference) {
        int qteRetourne = 0;


        for (PAD_Proposition_Ligne courant : padPropositionLigneList) {
            if (courant.produitID == ProduitID) {
                if (courant.dotationID == 0) {
                    qteRetourne = (courant.qteConditionnementAchat * courant.qteCartonFerme) + courant.qteUnite;
                }
                break;
            }
        }
        return qteRetourne;
    }

    public int P_reliquat(int depot, int prod, String IPP) {
        int qteReliquatRetournee = 0;
        List<MVT_Depots> mvtDepotList = new ArrayList<>();
        List<PH_Reliquat> ph_reliquat = new ArrayList<>();


        if (IPP == null) {
            IPP = "";
        }

        mvtDepotList = MVT_DepotsOpenHelper.getMVT_DepotsPAD(db, depot, prod);
        if (mvtDepotList.size() > 0) {
            for (MVT_Depots mvt_depots : mvtDepotList) {
                if (mvt_depots.getQté_RAL() > 0) {
                    qteReliquatRetournee = qteReliquatRetournee + mvt_depots.getQté_RAL();
                } else {
                    qteReliquatRetournee = qteReliquatRetournee + mvt_depots.getQté_com() - mvt_depots.getQté_livrée();
                }
            }
        } else if (!IPP.equals("")) {
            ph_reliquat = PH_ReliquatOpenHelper.getPH_ReliquatByIPP(db, prod, IPP);
            if (ph_reliquat != null && ph_reliquat.size() > 0) {
                for (PH_Reliquat reliquatCourant : ph_reliquat) {
                    if (reliquatCourant.getQteReliquat_X() > 0) {
                        qteReliquatRetournee = qteReliquatRetournee + reliquatCourant.getQteReliquat_X();
                    } else {
                        qteReliquatRetournee = qteReliquatRetournee + reliquatCourant.getQteCommande() - reliquatCourant.getQteLivraison();
                    }
                }
            }
        }

        return qteReliquatRetournee;
    }

    public void P_Calcul_Proto_Prescription(Prescription_patient prescription_patient) throws ParseException {
        int coeff_prochaine_liv = 0;
        int coeff = 0;

        String dateDebut = prescription_patient.getDate_Début();

        Date dateDebutDate = format.parse(dateDebut);

        if (dateDebutDate.compareTo(Vdate_debut) < 0) {
            dateDebutDate = Vdate_debut;
        }

        String dateFin = prescription_patient.getDate_Fin();
        Date dateFinDate = format.parse(dateFin);
        if (Vdate_liv.compareTo(dateFinDate) < 0) {
            dateFinDate = Vdate_liv;
        }

        coeff_prochaine_liv = P_calc_Prev_nb_semaine_Prescrip(prescription_patient.getDate_Début(), prescription_patient.getDate_Fin(), Vdate_inv, Vdate_liv);

        dateDebut = prescription_patient.getDate_Début();
        dateDebutDate = format.parse(dateDebut);

        if (dateDebutDate.compareTo(Vdate_debut) < 0) {
            dateDebutDate = Vdate_debut;
        }

        dateFin = prescription_patient.getDate_Fin();
        dateFinDate = format.parse(dateFin);
        if (Vdate_dim_liv.compareTo(dateFinDate) < 0) {
            dateFinDate = Vdate_dim_liv;
        }

        coeff = P_calc_Prev_nb_semaine_Prescrip(prescription_patient.getDate_Début(), prescription_patient.getDate_Fin(), Vdate_inv, VD_Livraison_Max);

        //on utilise le protocole récupérer en début de service
        int depotID = protocolePatient.get(0).getDepot_Code();
        String refDepot = protocolePatient.get(0).getDepot_Reference();
        String IPP = protocolePatient.get(0).getIPP();
        String patient = protocolePatient.get(0).getPatient_Identite();
        Boolean liv_dir_Bool = prescription_patient.isLivraison_Directe();

        String liv_dir = "0";
        if (liv_dir_Bool) {
            liv_dir = "1";
        }

        String depot_string = String.valueOf(depotID);
        while (depot_string.length() < 10) {
            depot_string = "0" + depot_string;
        }

        int prodID = prescription_patient.getCode_Produit();
        String prod_string = String.valueOf(prodID);
        while (prod_string.length() < 10) {
            prod_string = "0" + prod_string;
        }
        String ref = depot_string + liv_dir + prod_string + refDepot;
        double qte = prescription_patient.getQuantite() * prescription_patient.getFrequence_Hebdomadaire() * coeff;
        double stock = P_rapprochement_stock_previs(prodID, refDepot);
        double qteReliquat = P_reliquat(depotID, prodID, IPP);
        double reste_a_consomme = Math.round(prescription_patient.getQuantite() * prescription_patient.getFrequence_Hebdomadaire() * coeff_prochaine_liv);

        int x = T_ref.indexOf(ref);
        if (x == -1) {
            T_ref.add(ref);
            T_ipp.add(IPP);
            T_patient.add(patient);
            T_qte.add(qte);
            T_Stock.add(stock);
            T_Reliquat.add(qteReliquat);
            T_reste_a_consome.add(reste_a_consomme);
            T_nb_patient.add(1.0);
            T_prescription.add(true);
        } else {
            T_qte.set(x, qte);
            T_reste_a_consome.set(x, T_reste_a_consome.get(x) + reste_a_consomme);
            T_nb_patient.set(x, T_nb_patient.get(x) + 1);
        }
    }

    public int P_calc_Prev_nb_semaine_Prescrip(String date_debut_prescription, String date_fin_prescription, Date date_inv, Date date_liv) throws ParseException {
        int nbSemaine = 0;
        String date_en_cours = date_debut_prescription;

        Date date_en_cours_Date = format.parse(date_en_cours);
        Date date_debut_prescription_Date = format.parse(date_debut_prescription);
        Date date_fin_prescription_Date = format.parse(date_debut_prescription);
        String date_fin;

        if (date_debut_prescription_Date.compareTo(date_inv) <= 0) {
            if (nbSemaine > 0) {
                nbSemaine++;
                Calendar c = Calendar.getInstance();
                c.setTime(date_en_cours_Date);
                c.add(Calendar.DAY_OF_MONTH, 7 * nbSemaine - 1);
                date_en_cours_Date = c.getTime();
            } else {
                date_en_cours_Date = date_inv;
            }
        } else {
            date_en_cours = date_debut_prescription;
        }

        if (date_fin_prescription_Date.compareTo(date_liv) < 0) {
            date_fin = date_fin_prescription;
            long calcul = date_fin_prescription_Date.getTime() - date_en_cours_Date.getTime() / 7;
            nbSemaine = (int) calcul;
        } else {
            date_fin = date_liv.toString();
            Date date_fin_Date = format.parse(date_fin);
            long calcul = date_fin_Date.getTime() - date_en_cours_Date.getTime() / 7;
            nbSemaine = (int) calcul;
        }

        if (nbSemaine > 0) {
            if ((double) nbSemaine > 0) {
                nbSemaine++;
            }
        } else {
            nbSemaine = 0;
        }

        return nbSemaine;
    }

    public void P_Calcul_Proto_Dotation_Patient(int coeff_prochaine_liv, Dotation_Patient dotation_patient) {
        //on utilise le protocole sélectionner en début de service
        int depotID = protocolePatient.get(0).getDepot_Code();
        String refDepot = protocolePatient.get(0).getDepot_Reference();
        String IPP = protocolePatient.get(0).getIPP();
        String patient = protocolePatient.get(0).getPatient_Identite();

        Boolean liv_dir_Bool = dotation_patient.isLivraison_Directe();
        String liv_dir = "0";
        if (liv_dir_Bool) {
            liv_dir = "1";
        }

        String depot_string = String.valueOf(depotID);
        while (depot_string.length() < 10) {
            depot_string = "0" + depot_string;
        }

        int prodID = dotation_patient.getCode_Produit();
        String prod_string = String.valueOf(prodID);
        while (prod_string.length() < 10) {
            prod_string = "0" + prod_string;
        }
        String ref = depot_string + liv_dir + prod_string + refDepot;
        double qte = dotation_patient.getQte_Commande();
        double stock = qte;
        for (PAD_Proposition_Ligne courant : padPropositionLigneList) {
            if (courant.produitID == prodID) {
                if (courant.dotationID != 0) {
                    if (courant.recevoirProduit) {
                        if (courant.choisirQteProduit) {
                            stock = stock - courant.qteALivrer;
                        } else {
                            stock = 0;
                        }
                    }
                }
                break;
            }
        }
        stock = Math.ceil(stock);
        double qte_reliquat = P_reliquat(depotID, prodID, IPP);
        double reste_a_consommer = 0;

        if (dotation_patient.getQté() == 0) {
            reste_a_consommer = 0;
        } else {
            if (coeff_prochaine_liv != 0) {
                reste_a_consommer = dotation_patient.getQte_Commande() / coeff_prochaine_liv;
            }

            if (reste_a_consommer < 1) {
                reste_a_consommer = 1;
            }
        }
        reste_a_consommer = Math.round(reste_a_consommer);

        int x = T_ref.indexOf(ref);
        if (x == -1) {
            T_ref.add(ref);
            T_ipp.add(IPP);
            T_patient.add(patient);
            T_qte.add(qte);
            T_Stock.add(stock);
            T_Reliquat.add(qte_reliquat);
            T_Dotation.add(true);
            T_reste_a_consome.add(reste_a_consommer);
            T_nb_patient.add(1.0);
            T_prescription.add(false);
        } else {
            T_qte.set(x, T_qte.get(x) + qte);
            T_reste_a_consome.set(x, T_reste_a_consome.get(x) + reste_a_consommer);
        }
    }

    public void P_creer_flux_part(int prod, int depot, String ref_depot, double qte, int liv_dir, String Code_IPP, String Nom, double Stock, Boolean dotation, double reste_a_consommer, double nb_patient, double reliquat, Boolean prescription) {
        int x = T_prod.indexOf(prod);
        preparation_ligne = new Preparation_Ligne();

        if (x != 1) {
            Produit produit = T_Fprod.get(x);
            //uid random
            Random random = new Random();
            int preparation_ligneID = random.nextInt();
            if (preparation_ligneID > 0) {
                preparation_ligneID = preparation_ligneID * -1;
            }
            preparation_ligne.set_UID(preparation_ligneID);
            preparation_ligne.setPatient(true);
            preparation_ligne.setRéf_Antenne(depotPatient.getPAD_Lieu_Traitement().trim());
            preparation_ligne.setDu(CycleDu);
            preparation_ligne.setAu(CycleAu);
            preparation_ligne.setCode_prod(produit.getID_produit());
            preparation_ligne.setCode_frs(produit.getCode_fourn());
            preparation_ligne.setFrs(produit.getFournisseur().trim());
            preparation_ligne.setProduit(produit.getDesignation_interne().trim());
            preparation_ligne.setRef_prod(produit.getRef_fourni().trim());
            preparation_ligne.setCatégorie(produit.getCategorie().trim());
            preparation_ligne.setPxrix_Unit(produit.getPrix_unitaire());
            preparation_ligne.setTx_TVA(produit.getTaux_de_TVA());

            preparation_ligne.setCond_Achat(produit.getCond_achat());
            preparation_ligne.setCond_Distribution(produit.getCond_distrib());
            preparation_ligne.setRespect_cond_achat(produit.isRespect_Cond_Achat());
            preparation_ligne.setCode_Cycle(idcycle);
            preparation_ligne.setCycle(referenceCycle);
            preparation_ligne.setCode_depot(depot);
            preparation_ligne.setRéf_depot(ref_depot.trim());

            preparation_ligne.setConso_prévue(qte);
            if (liv_dir == 0) {
                preparation_ligne.setLivraison_directe(false);
            } else {
                preparation_ligne.setLivraison_directe(true);
            }
            preparation_ligne.setStock_Actuel(Stock);
            preparation_ligne.setDotation_Protocole(dotation);
            preparation_ligne.setReste_A_Consomme(reste_a_consommer);

            Double comparateur_Double = new Double(nb_patient);
            if (comparateur_Double != null) {
                preparation_ligne.setNb_Patient((int) nb_patient);
            }

            comparateur_Double = new Double(reliquat);
            if (comparateur_Double != null) {
                preparation_ligne.setReliquat(reliquat);
            }

            if (prescription != null) {
                preparation_ligne.setPrescription(prescription);
            }

            P_Qtes_Com_Prevision_UF(preparation_ligne, preparation, depot, qte);

            preparation_ligne.setCode_IPP(Code_IPP.trim());

            preparation_ligne.setNom(Nom.trim());

            Preparation_LigneOpenHelper.insererUnPreparation_LigneEnBDD(db, preparation_ligne);
        }
    }

    public void P_Qtes_Com_Prevision_UF(Preparation_Ligne preparation_ligne, Preparation preparation, int id_depot, double quantite) {
        int qte_reliquat;
        Boolean Reliquats_pour_prevision = ParametresServeurOpenHelper.getReliquats_pour_prevision(db);
        Boolean Liv_indirecte_egal_Cond_achat = ParametresServeurOpenHelper.getLiv_indirecte_egal_Cond_achat(db);

        if (Reliquats_pour_prevision) {
            qte_reliquat = P_reliquat(preparation_ligne.getCode_depot(), preparation_ligne.getCode_prod(), preparation.getIPP_Patient());
        } else {
            qte_reliquat = 0;
        }

        preparation_ligne.setReliquat(qte_reliquat);

        if (preparation_ligne.getLivraison_directe()) {
            preparation_ligne.setConso_Cond(P_Calc_Prev_Cond(preparation_ligne.getConso_prévue(), preparation_ligne.getCond_Achat(), preparation_ligne.getRespect_cond_achat()));
            preparation_ligne.setStock_Idéal(preparation_ligne.getConso_prévue() + preparation_ligne.getStock_sécurité() - qte_reliquat);

            if (preparation_ligne.getStock_Actuel() >= preparation_ligne.getStock_Idéal()) {
                preparation_ligne.setRAC(0);
            } else {
                preparation_ligne.setRAC(P_Calc_Prev_Cond(preparation_ligne.getStock_Idéal() - preparation_ligne.getStock_Actuel(), preparation_ligne.getCond_Achat(), preparation_ligne.getRespect_cond_achat()));
            }

            preparation_ligne.setRAD(0);
        } else {
            preparation_ligne.setConso_prévue(quantite);
            preparation_ligne.setConso_Cond(P_Calc_Prev_Cond(preparation_ligne.getConso_prévue(), preparation_ligne.getCond_Achat(), preparation_ligne.getRespect_cond_achat()));
            preparation_ligne.setStock_Idéal(preparation_ligne.getConso_prévue() + preparation_ligne.getStock_sécurité() - qte_reliquat);

            if (preparation_ligne.getStock_Actuel() >= preparation_ligne.getStock_Idéal()) {
                preparation_ligne.setRAC(0);
            } else {
                preparation_ligne.setRAC(preparation_ligne.getStock_Idéal() - preparation_ligne.getStock_Actuel());
            }

            if (Liv_indirecte_egal_Cond_achat) {
                preparation_ligne.setRAD(P_Calc_Prev_Cond(preparation_ligne.getRAC(), preparation_ligne.getCond_Achat(), preparation_ligne.getRespect_cond_achat()));
            } else {
                preparation_ligne.setRAD(P_Calc_Prev_Cond(preparation_ligne.getRAC(), preparation_ligne.getCond_Distribution(), preparation_ligne.getRespect_cond_achat()));
            }
        }

        double dec = preparation_ligne.getRAC() - (int) preparation_ligne.getRAC();

        if (dec > 0) {
            preparation_ligne.setRAC((int) preparation_ligne.getRAC() + 1);
        }

        preparation_ligne.setStock_Final(preparation_ligne.getStock_Actuel() + preparation_ligne.getRAC() - preparation_ligne.getConso_prévue());
        preparation_ligne.setID_Prevision(preparation.getID());

        double mt_HT_arrondi = (double) Math.round(preparation_ligne.getPxrix_Unit() * preparation_ligne.getRAD() * 100) / 100;
        preparation_ligne.setMt_HT(mt_HT_arrondi);

        double mt_TTC_arrondi = (double) Math.round(preparation_ligne.getMt_HT() * (1 + preparation_ligne.getTx_TVA() / 100) * 100) / 100;
        preparation_ligne.setMt_TTC(mt_TTC_arrondi);
    }

    public double P_Calc_Prev_Cond(double qte_condition, double conditionnement, Boolean respect_cond_achat) {
        double reste;
        double modulo;
        double valeurRetourner;

        if (qte_condition > 0) {
            double etape = qte_condition / conditionnement;

            reste = etape - (int) etape;

            if (reste == 0) {
                modulo = 0;
            } else {
                modulo = 1 - reste;
            }

            switch ((int) conditionnement) {
                case 0: //conditionnement nul
                    valeurRetourner = Math.round(qte_condition);
                    break;

                case 1: //conditionnement unitaire
                    valeurRetourner = Math.round(qte_condition);
                    break;

                default: //conditionnement autre
                    if (modulo == 0) {
                        valeurRetourner = qte_condition;
                    } else {
                        reste = Math.round(conditionnement * modulo);
                        valeurRetourner = ((int) qte_condition + reste);
                    }
            }
        } else {
            valeurRetourner = 0;
        }

        return valeurRetourner;
    }

    public void Prevision_valeur_Calculer() {
        List<Preparation_Ligne> preparation_ligneList = new ArrayList<>();
        double total_montant_ht = 0;
        double total_montant_ttc = 0;

        preparation_ligneList = Preparation_LigneOpenHelper.getPreparationLigneByPreparation(db, preparation.getID());

        for (Preparation_Ligne preparationCourant : preparation_ligneList) {
            total_montant_ht = total_montant_ht + preparationCourant.getMt_HT();
            total_montant_ttc = total_montant_ttc + preparationCourant.getMt_TTC();
        }

        double mt_HT_arrondi = (double) Math.round(total_montant_ht * 100) / 100;
        preparation.setMontant_HT(mt_HT_arrondi);

        double mt_TTC_arrondi = (double) Math.round(total_montant_ttc * 100) / 100;
        preparation.setMontant_TTC(mt_TTC_arrondi);

        preparation.setMontant_TVA(preparation.getMontant_TTC() - preparation.getMontant_HT());
    }
}


