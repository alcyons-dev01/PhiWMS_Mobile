package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Preparation_Ligne implements Serializable, Comparable {

    private int _UID;
    private String Du;
    private String Au;
    private String Réf_depot;
    private String Code_IPP;
    private String Nom;
    private int Code_prod;
    private int Code_frs;
    private String Frs;
    private String Produit;
    private String Ref_prod;
    private double Conso_prévue;
    private double Conso_Cond;
    private String Cycle;
    private String Statut;
    private double RAD;
    private int Code_Cycle;
    private Boolean Livraison_directe;
    private double RAC;
    private int Code_depot;
    private String Catégorie;
    private String N_Commande;
    private int Code_ligne_com;
    private double Stock_Actuel;
    private double Stock_Final;
    private Boolean Prioritaire;
    private double Stock_sécurité;
    private String Date_commande;
    private String Date_livraison;
    private String Date_Délivrance;
    private double Cond_Achat;
    private double Cond_Distribution;
    private Boolean Respect_cond_achat;
    private double Stock_Idéal;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private Boolean Patient;
    private String Réf_Antenne;
    private int ID_Prevision;
    private double Pxrix_Unit;
    private double Mt_HT;
    private double Tx_TVA;
    private double Mt_TTC;
    private Boolean Dotation_Protocole;
    private double Reste_A_Consomme;
    private double Qte_Besoin;
    private Boolean Prescription;
    private int Nb_Patient;
    private double Qte_CommandeLiv1;
    private double Qte_CommandeLiv2;
    private double Reliquat;
    private double Qte_CommandeLiv3;
    private double Qte_CommandeLiv4;
    private double Qte_CommandeLiv5;
    private int phiMR4UUID = -1;

    public Preparation_Ligne(int _UID, String du, String au, String réf_depot, String code_IPP, String nom, int code_prod, int code_frs, String frs, String produit, String ref_prod, double conso_prévue, double conso_Cond, String cycle, String statut, double RAD, int code_Cycle, Boolean livraison_directe, double RAC, int code_depot, String catégorie, String n_Commande, int code_ligne_com, double stock_Actuel, double stock_Final, Boolean prioritaire, double stock_sécurité, String date_commande, String date_livraison, String date_Délivrance, double cond_Achat, double cond_Distribution, Boolean respect_cond_achat, double stock_Idéal, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, Boolean patient, String réf_Antenne, int ID_Prevision, double pxrix_Unit, double mt_HT, double tx_TVA, double mt_TTC, Boolean dotation_Protocole, double reste_A_Consomme, double qte_Besoin, Boolean prescription, int nb_Patient, double qte_CommandeLiv1, double qte_CommandeLiv2, double reliquat, double qte_CommandeLiv3, double qte_CommandeLiv4, double qte_CommandeLiv5) {
        this._UID = _UID;
        this.Du = du;
        this.Au = au;
        this.Réf_depot = réf_depot;
        this.Code_IPP = code_IPP;
        this.Nom = nom;
        this.Code_prod = code_prod;
        this.Code_frs = code_frs;
        this.Frs = frs;
        this.Produit = produit;
        this.Ref_prod = ref_prod;
        this.Conso_prévue = conso_prévue;
        this.Conso_Cond = conso_Cond;
        this.Cycle = cycle;
        this.Statut = statut;
        this.RAD = RAD;
        this.Code_Cycle = code_Cycle;
        this.Livraison_directe = livraison_directe;
        this.RAC = RAC;
        this.Code_depot = code_depot;
        this.Catégorie = catégorie;
        this.N_Commande = n_Commande;
        this.Code_ligne_com = code_ligne_com;
        this.Stock_Actuel = stock_Actuel;
        this.Stock_Final = stock_Final;
        this.Prioritaire = prioritaire;
        this.Stock_sécurité = stock_sécurité;
        this.Date_commande = date_commande;
        this.Date_livraison = date_livraison;
        this.Date_Délivrance = date_Délivrance;
        this.Cond_Achat = cond_Achat;
        this.Cond_Distribution = cond_Distribution;
        this.Respect_cond_achat = respect_cond_achat;
        this.Stock_Idéal = stock_Idéal;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.Patient = patient;
        this.Réf_Antenne = réf_Antenne;
        this.ID_Prevision = ID_Prevision;
        this.Pxrix_Unit = pxrix_Unit;
        this.Mt_HT = mt_HT;
        this.Tx_TVA = tx_TVA;
        this.Mt_TTC = mt_TTC;
        this.Dotation_Protocole = dotation_Protocole;
        this.Reste_A_Consomme = reste_A_Consomme;
        this.Qte_Besoin = qte_Besoin;
        this.Prescription = prescription;
        this.Nb_Patient = nb_Patient;
        this.Qte_CommandeLiv1 = qte_CommandeLiv1;
        this.Qte_CommandeLiv2 = qte_CommandeLiv2;
        this.Reliquat = reliquat;
        this.Qte_CommandeLiv3 = qte_CommandeLiv3;
        this.Qte_CommandeLiv4 = qte_CommandeLiv4;
        this.Qte_CommandeLiv5 = qte_CommandeLiv5;
    }

    public Preparation_Ligne() {
    }

    public Preparation_Ligne(JSONObject jsonObject) {
        try {
            this._UID = jsonObject.getInt("_UID");
            this.Du = OutilsGestionClasses.recupererString(jsonObject.getString("Du"));
            this.Au = OutilsGestionClasses.recupererString(jsonObject.getString("Au"));
            this.Réf_depot = OutilsGestionClasses.recupererString(jsonObject.getString("Réf_depot"));
            this.Code_IPP = OutilsGestionClasses.recupererString(jsonObject.getString("Code_IPP"));
            this.Nom = OutilsGestionClasses.recupererString(jsonObject.getString("Nom"));
            this.Code_prod = jsonObject.getInt("Code_prod");
            this.Code_frs = jsonObject.getInt("Code_frs");
            this.Frs = OutilsGestionClasses.recupererString(jsonObject.getString("Frs"));
            this.Produit = OutilsGestionClasses.recupererString(jsonObject.getString("Produit"));
            this.Ref_prod = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_prod"));
            this.Conso_prévue = jsonObject.getDouble("Conso_prévue");
            this.Conso_Cond = jsonObject.getDouble("Conso_Cond");
            this.Cycle = OutilsGestionClasses.recupererString(jsonObject.getString("Cycle"));
            this.Statut = OutilsGestionClasses.recupererString(jsonObject.getString("Statut"));
            this.RAD = jsonObject.getDouble("RAD");
            this.Code_Cycle = jsonObject.getInt("Code_Cycle");
            this.Livraison_directe = OutilsGestionClasses.recupererBooleen(jsonObject, "Livraison_directe");
            this.RAC = jsonObject.getDouble("RAC");
            this.Code_depot = jsonObject.getInt("Code_depot");
            this.Catégorie = OutilsGestionClasses.recupererString(jsonObject.getString("Catégorie"));
            this.N_Commande = OutilsGestionClasses.recupererString(jsonObject.getString("N_Commande"));
            this.Code_ligne_com = jsonObject.getInt("Code_ligne_com");
            this.Stock_Actuel = jsonObject.getDouble("Stock_Actuel");
            this.Stock_Final = jsonObject.getDouble("Stock_Final");
            this.Prioritaire = OutilsGestionClasses.recupererBooleen(jsonObject, "Prioritaire");
            this.Stock_sécurité = jsonObject.getDouble("Stock_sécurité");
            this.Date_commande = OutilsGestionClasses.recupererString(jsonObject.getString("Date_commande"));
            this.Date_livraison = OutilsGestionClasses.recupererString(jsonObject.getString("Date_livraison"));
            this.Date_Délivrance = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Délivrance"));
            this.Cond_Achat = jsonObject.getDouble("Cond_Achat");
            this.Cond_Distribution = jsonObject.getDouble("Cond_Distribution");
            this.Respect_cond_achat = OutilsGestionClasses.recupererBooleen(jsonObject, "Respect_cond_achat");
            this.Stock_Idéal = jsonObject.getDouble("Stock_Idéal");
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.Patient = OutilsGestionClasses.recupererBooleen(jsonObject, "Patient");
            this.Réf_Antenne = OutilsGestionClasses.recupererString(jsonObject.getString("Réf_Antenne"));
            this.ID_Prevision = jsonObject.getInt("ID_Prevision");
            this.Pxrix_Unit = jsonObject.getDouble("Pxrix_Unit");
            this.Mt_HT = jsonObject.getDouble("Mt_HT");
            this.Tx_TVA = jsonObject.getDouble("Tx_TVA");
            this.Mt_TTC = jsonObject.getDouble("Mt_TTC");
            this.Dotation_Protocole = OutilsGestionClasses.recupererBooleen(jsonObject, "Dotation_Protocole");
            this.Reste_A_Consomme = jsonObject.getDouble("Reste_A_Consomme");
            this.Qte_Besoin = jsonObject.getDouble("Qte_Besoin");
            this.Prescription = OutilsGestionClasses.recupererBooleen(jsonObject, "Prescription");
            this.Nb_Patient = jsonObject.getInt("Nb_Patient");
            this.Qte_CommandeLiv1 = jsonObject.getDouble("Qte_CommandeLiv1");
            this.Qte_CommandeLiv2 = jsonObject.getDouble("Qte_CommandeLiv2");
            this.Reliquat = jsonObject.getDouble("Reliquat");
            this.Qte_CommandeLiv3 = jsonObject.getDouble("Qte_CommandeLiv3");
            this.Qte_CommandeLiv4 = jsonObject.getDouble("Qte_CommandeLiv4");
            this.Qte_CommandeLiv5 = jsonObject.getDouble("Qte_CommandeLiv5");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Preparation_Ligne(Cursor cursor) {
        this._UID = cursor.getInt(Preparation_LigneOpenHelper.Constantes.NUM_COL__UID_PREPARATION_LIGNE);
        this.Du = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_DU_PREPARATION_LIGNE);
        this.Au = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_AU_PREPARATION_LIGNE);
        this.Réf_depot = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_REF_DEPOT_PREPARATION_LIGNE);
        this.Code_IPP = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_CODE_IPP_PREPARATION_LIGNE);
        this.Nom = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_NOM_PREPARATION_LIGNE);
        this.Code_prod = cursor.getInt(Preparation_LigneOpenHelper.Constantes.NUM_COL_CODE_PROD_PREPARATION_LIGNE);
        this.Code_frs = cursor.getInt(Preparation_LigneOpenHelper.Constantes.NUM_COL_CODE_FRS_PREPARATION_LIGNE);
        this.Frs = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_FRS_PREPARATION_LIGNE);
        this.Produit = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_PREPARATION_LIGNE);
        this.Ref_prod = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_REF_PROD_PREPARATION_LIGNE);
        this.Conso_prévue = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_CONSO_PREVUE_PREPARATION_LIGNE);
        this.Conso_Cond = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_CONSO_COND_PREPARATION_LIGNE);
        this.Cycle = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_CYCLE_PREPARATION_LIGNE);
        this.Statut = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_STATUT_PREPARATION_LIGNE);
        this.RAD = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_RAD_PREPARATION_LIGNE);
        this.Code_Cycle = cursor.getInt(Preparation_LigneOpenHelper.Constantes.NUM_COL_CODE_CYCLE_PREPARATION_LIGNE);
        this.Livraison_directe = OutilsGestionClasses.recupererBooleen(cursor, Preparation_LigneOpenHelper.Constantes.NUM_COL_LIVRAISON_DIRECTE_PREPARATION_LIGNE);
        this.RAC = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_RAC_PREPARATION_LIGNE);
        this.Code_depot = cursor.getInt(Preparation_LigneOpenHelper.Constantes.NUM_COL_CODE_DEPOT_PREPARATION_LIGNE);
        this.Catégorie = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_CATEGORIE_PREPARATION_LIGNE);
        this.N_Commande = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_N_COMMANDE_PREPARATION_LIGNE);
        this.Code_ligne_com = cursor.getInt(Preparation_LigneOpenHelper.Constantes.NUM_COL_CODE_LIGNE_COM_PREPARATION_LIGNE);
        this.Stock_Actuel = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_STOCK_ACTUEL_PREPARATION_LIGNE);
        this.Stock_Final = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_STOCK_FINAL_PREPARATION_LIGNE);
        this.Prioritaire = OutilsGestionClasses.recupererBooleen(cursor, Preparation_LigneOpenHelper.Constantes.NUM_COL_PRIORITAIRE_PREPARATION_LIGNE);
        this.Stock_sécurité = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_STOCK_SECURITE_PREPARATION_LIGNE);
        this.Date_commande = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_DATE_COMMANDE_PREPARATION_LIGNE);
        this.Date_livraison = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_DATE_LIVRAISON_PREPARATION_LIGNE);
        this.Date_Délivrance = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_DATE_DELIVRANCE_PREPARATION_LIGNE);
        this.Cond_Achat = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_COND_ACHAT_PREPARATION_LIGNE);
        this.Cond_Distribution = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_COND_DISTRIBUTION_PREPARATION_LIGNE);
        this.Respect_cond_achat = OutilsGestionClasses.recupererBooleen(cursor, Preparation_LigneOpenHelper.Constantes.NUM_COL_RESPECT_COND_ACHAT_PREPARATION_LIGNE);
        this.Stock_Idéal = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_STOCK_IDEAL_PREPARATION_LIGNE);
        this.SYS_DT_MAJ = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PREPARATION_LIGNE);
        this.SYS_HEURE_MAJ = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PREPARATION_LIGNE);
        this.SYS_USER_MAJ = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PREPARATION_LIGNE);
        this.Patient = OutilsGestionClasses.recupererBooleen(cursor, Preparation_LigneOpenHelper.Constantes.NUM_COL_PATIENT_PREPARATION_LIGNE);
        this.Réf_Antenne = cursor.getString(Preparation_LigneOpenHelper.Constantes.NUM_COL_REF_ANTENNE_PREPARATION_LIGNE);
        this.ID_Prevision = cursor.getInt(Preparation_LigneOpenHelper.Constantes.NUM_COL_ID_PREVISION_PREPARATION_LIGNE);
        this.Pxrix_Unit = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_PXRIX_UNIT_PREPARATION_LIGNE);
        this.Mt_HT = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_MT_HT_PREPARATION_LIGNE);
        this.Tx_TVA = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_TX_TVA_PREPARATION_LIGNE);
        this.Mt_TTC = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_MT_TTC_PREPARATION_LIGNE);
        this.Dotation_Protocole = OutilsGestionClasses.recupererBooleen(cursor, Preparation_LigneOpenHelper.Constantes.NUM_COL_DOTATION_PROTOCOLE_PREPARATION_LIGNE);
        this.Reste_A_Consomme = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_RESTE_A_CONSOMME_PREPARATION_LIGNE);
        this.Qte_Besoin = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_BESOIN_PREPARATION_LIGNE);
        this.Prescription = OutilsGestionClasses.recupererBooleen(cursor, Preparation_LigneOpenHelper.Constantes.NUM_COL_PRESCRIPTION_PREPARATION_LIGNE);
        this.Nb_Patient = cursor.getInt(Preparation_LigneOpenHelper.Constantes.NUM_COL_NB_PATIENT_PREPARATION_LIGNE);
        this.Qte_CommandeLiv1 = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_COMMANDELIV1_PREPARATION_LIGNE);
        this.Qte_CommandeLiv2 = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_COMMANDELIV2_PREPARATION_LIGNE);
        this.Reliquat = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_RELIQUAT_PREPARATION_LIGNE);
        this.Qte_CommandeLiv3 = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_COMMANDELIV3_PREPARATION_LIGNE);
        this.Qte_CommandeLiv4 = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_COMMANDELIV4_PREPARATION_LIGNE);
        this.Qte_CommandeLiv5 = cursor.getDouble(Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_COMMANDELIV5_PREPARATION_LIGNE);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getDu() {
        return Du;
    }

    public void setDu(String du) {
        Du = du;
    }

    public String getAu() {
        return Au;
    }

    public void setAu(String au) {
        Au = au;
    }

    public String getRéf_depot() {
        return Réf_depot;
    }

    public void setRéf_depot(String réf_depot) {
        Réf_depot = réf_depot;
    }

    public String getCode_IPP() {
        return Code_IPP;
    }

    public void setCode_IPP(String code_IPP) {
        Code_IPP = code_IPP;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public int getCode_prod() {
        return Code_prod;
    }

    public void setCode_prod(int code_prod) {
        Code_prod = code_prod;
    }

    public int getCode_frs() {
        return Code_frs;
    }

    public void setCode_frs(int code_frs) {
        Code_frs = code_frs;
    }

    public String getFrs() {
        return Frs;
    }

    public void setFrs(String frs) {
        Frs = frs;
    }

    public String getProduit() {
        return Produit;
    }

    public void setProduit(String produit) {
        Produit = produit;
    }

    public String getRef_prod() {
        return Ref_prod;
    }

    public void setRef_prod(String ref_prod) {
        Ref_prod = ref_prod;
    }

    public double getConso_prévue() {
        return Conso_prévue;
    }

    public void setConso_prévue(double conso_prévue) {
        Conso_prévue = conso_prévue;
    }

    public double getConso_Cond() {
        return Conso_Cond;
    }

    public void setConso_Cond(double conso_Cond) {
        Conso_Cond = conso_Cond;
    }

    public String getCycle() {
        return Cycle;
    }

    public void setCycle(String cycle) {
        Cycle = cycle;
    }

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }

    public double getRAD() {
        return RAD;
    }

    public void setRAD(double RAD) {
        this.RAD = RAD;
    }

    public int getCode_Cycle() {
        return Code_Cycle;
    }

    public void setCode_Cycle(int code_Cycle) {
        Code_Cycle = code_Cycle;
    }

    public Boolean getLivraison_directe() {
        return Livraison_directe;
    }

    public void setLivraison_directe(Boolean livraison_directe) {
        Livraison_directe = livraison_directe;
    }

    public double getRAC() {
        return RAC;
    }

    public void setRAC(double RAC) {
        this.RAC = RAC;
    }

    public int getCode_depot() {
        return Code_depot;
    }

    public void setCode_depot(int code_depot) {
        Code_depot = code_depot;
    }

    public String getCatégorie() {
        return Catégorie;
    }

    public void setCatégorie(String catégorie) {
        Catégorie = catégorie;
    }

    public String getN_Commande() {
        return N_Commande;
    }

    public void setN_Commande(String n_Commande) {
        N_Commande = n_Commande;
    }

    public int getCode_ligne_com() {
        return Code_ligne_com;
    }

    public void setCode_ligne_com(int code_ligne_com) {
        Code_ligne_com = code_ligne_com;
    }

    public double getStock_Actuel() {
        return Stock_Actuel;
    }

    public void setStock_Actuel(double stock_Actuel) {
        Stock_Actuel = stock_Actuel;
    }

    public double getStock_Final() {
        return Stock_Final;
    }

    public void setStock_Final(double stock_Final) {
        Stock_Final = stock_Final;
    }

    public Boolean getPrioritaire() {
        return Prioritaire;
    }

    public void setPrioritaire(Boolean prioritaire) {
        Prioritaire = prioritaire;
    }

    public double getStock_sécurité() {
        return Stock_sécurité;
    }

    public void setStock_sécurité(double stock_sécurité) {
        Stock_sécurité = stock_sécurité;
    }

    public String getDate_commande() {
        return Date_commande;
    }

    public void setDate_commande(String date_commande) {
        Date_commande = date_commande;
    }

    public String getDate_livraison() {
        return Date_livraison;
    }

    public void setDate_livraison(String date_livraison) {
        Date_livraison = date_livraison;
    }

    public String getDate_Délivrance() {
        return Date_Délivrance;
    }

    public void setDate_Délivrance(String date_Délivrance) {
        Date_Délivrance = date_Délivrance;
    }

    public double getCond_Achat() {
        return Cond_Achat;
    }

    public void setCond_Achat(double cond_Achat) {
        Cond_Achat = cond_Achat;
    }

    public double getCond_Distribution() {
        return Cond_Distribution;
    }

    public void setCond_Distribution(double cond_Distribution) {
        Cond_Distribution = cond_Distribution;
    }

    public Boolean getRespect_cond_achat() {
        return Respect_cond_achat;
    }

    public void setRespect_cond_achat(Boolean respect_cond_achat) {
        Respect_cond_achat = respect_cond_achat;
    }

    public double getStock_Idéal() {
        return Stock_Idéal;
    }

    public void setStock_Idéal(double stock_Idéal) {
        Stock_Idéal = stock_Idéal;
    }

    public String getSYS_DT_MAJ() {
        return SYS_DT_MAJ;
    }

    public void setSYS_DT_MAJ(String SYS_DT_MAJ) {
        this.SYS_DT_MAJ = SYS_DT_MAJ;
    }

    public String getSYS_HEURE_MAJ() {
        return SYS_HEURE_MAJ;
    }

    public void setSYS_HEURE_MAJ(String SYS_HEURE_MAJ) {
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
    }

    public String getSYS_USER_MAJ() {
        return SYS_USER_MAJ;
    }

    public void setSYS_USER_MAJ(String SYS_USER_MAJ) {
        this.SYS_USER_MAJ = SYS_USER_MAJ;
    }

    public Boolean getPatient() {
        return Patient;
    }

    public void setPatient(Boolean patient) {
        Patient = patient;
    }

    public String getRéf_Antenne() {
        return Réf_Antenne;
    }

    public void setRéf_Antenne(String réf_Antenne) {
        Réf_Antenne = réf_Antenne;
    }

    public int getID_Prevision() {
        return ID_Prevision;
    }

    public void setID_Prevision(int ID_Prevision) {
        this.ID_Prevision = ID_Prevision;
    }

    public double getPxrix_Unit() {
        return Pxrix_Unit;
    }

    public void setPxrix_Unit(double pxrix_Unit) {
        Pxrix_Unit = pxrix_Unit;
    }

    public double getMt_HT() {
        return Mt_HT;
    }

    public void setMt_HT(double mt_HT) {
        Mt_HT = mt_HT;
    }

    public double getTx_TVA() {
        return Tx_TVA;
    }

    public void setTx_TVA(double tx_TVA) {
        Tx_TVA = tx_TVA;
    }

    public double getMt_TTC() {
        return Mt_TTC;
    }

    public void setMt_TTC(double mt_TTC) {
        Mt_TTC = mt_TTC;
    }

    public Boolean getDotation_Protocole() {
        return Dotation_Protocole;
    }

    public void setDotation_Protocole(Boolean dotation_Protocole) {
        Dotation_Protocole = dotation_Protocole;
    }

    public double getReste_A_Consomme() {
        return Reste_A_Consomme;
    }

    public void setReste_A_Consomme(double reste_A_Consomme) {
        Reste_A_Consomme = reste_A_Consomme;
    }

    public double getQte_Besoin() {
        return Qte_Besoin;
    }

    public void setQte_Besoin(double qte_Besoin) {
        Qte_Besoin = qte_Besoin;
    }

    public Boolean getPrescription() {
        return Prescription;
    }

    public void setPrescription(Boolean prescription) {
        Prescription = prescription;
    }

    public int getNb_Patient() {
        return Nb_Patient;
    }

    public void setNb_Patient(int nb_Patient) {
        Nb_Patient = nb_Patient;
    }

    public double getQte_CommandeLiv1() {
        return Qte_CommandeLiv1;
    }

    public void setQte_CommandeLiv1(double qte_CommandeLiv1) {
        Qte_CommandeLiv1 = qte_CommandeLiv1;
    }

    public double getQte_CommandeLiv2() {
        return Qte_CommandeLiv2;
    }

    public void setQte_CommandeLiv2(double qte_CommandeLiv2) {
        Qte_CommandeLiv2 = qte_CommandeLiv2;
    }

    public double getReliquat() {
        return Reliquat;
    }

    public void setReliquat(double reliquat) {
        Reliquat = reliquat;
    }

    public double getQte_CommandeLiv3() {
        return Qte_CommandeLiv3;
    }

    public void setQte_CommandeLiv3(double qte_CommandeLiv3) {
        Qte_CommandeLiv3 = qte_CommandeLiv3;
    }

    public double getQte_CommandeLiv4() {
        return Qte_CommandeLiv4;
    }

    public void setQte_CommandeLiv4(double qte_CommandeLiv4) {
        Qte_CommandeLiv4 = qte_CommandeLiv4;
    }

    public double getQte_CommandeLiv5() {
        return Qte_CommandeLiv5;
    }

    public void setQte_CommandeLiv5(double qte_CommandeLiv5) {
        Qte_CommandeLiv5 = qte_CommandeLiv5;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", _UID);
            jsonObject.put("Du", Du);
            jsonObject.put("Au", Au);
            jsonObject.put("Réf_depot", Réf_depot);
            jsonObject.put("Code_IPP", Code_IPP);
            jsonObject.put("Nom", Nom);
            jsonObject.put("Code_prod", Code_prod);
            jsonObject.put("Code_frs", Code_frs);
            jsonObject.put("Frs", Frs);
            jsonObject.put("Produit", Produit);
            jsonObject.put("Ref_prod", Ref_prod);
            jsonObject.put("Conso_prévue", Conso_prévue);
            jsonObject.put("Conso_Cond", Conso_Cond);
            jsonObject.put("Cycle", Cycle);
            jsonObject.put("Statut", Statut);
            jsonObject.put("RAD", RAD);
            jsonObject.put("Code_Cycle", Code_Cycle);
            jsonObject.put("Livraison_directe", Livraison_directe);
            jsonObject.put("RAC", RAC);
            jsonObject.put("Code_depot", Code_depot);
            jsonObject.put("Catégorie", Catégorie);
            jsonObject.put("N_Commande", N_Commande);
            jsonObject.put("Code_ligne_com", Code_ligne_com);
            jsonObject.put("Stock_Actuel", Stock_Actuel);
            jsonObject.put("Stock_Final", Stock_Final);
            jsonObject.put("Prioritaire", Prioritaire);
            jsonObject.put("Stock_sécurité", Stock_sécurité);
            jsonObject.put("Date_commande", Date_commande);
            jsonObject.put("Date_livraison", Date_livraison);
            jsonObject.put("Date_Délivrance", Date_Délivrance);
            jsonObject.put("Cond_Achat", Cond_Achat);
            jsonObject.put("Cond_Distribution", Cond_Distribution);
            jsonObject.put("Respect_cond_achat", Respect_cond_achat);
            jsonObject.put("Stock_Idéal", Stock_Idéal);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("Patient", Patient);
            jsonObject.put("Réf_Antenne", Réf_Antenne);
            jsonObject.put("ID_Prevision", ID_Prevision);
            jsonObject.put("Pxrix_Unit", Pxrix_Unit);
            jsonObject.put("Mt_HT", Mt_HT);
            jsonObject.put("Tx_TVA", Tx_TVA);
            jsonObject.put("Mt_TTC", Mt_TTC);
            jsonObject.put("Dotation_Protocole", Dotation_Protocole);
            jsonObject.put("Reste_A_Consomme", Reste_A_Consomme);
            jsonObject.put("Qte_Besoin", Qte_Besoin);
            jsonObject.put("Prescription", Prescription);
            jsonObject.put("Patient", Patient);
            jsonObject.put("Qte_CommandeLiv1", Qte_CommandeLiv1);
            jsonObject.put("Qte_CommandeLiv2", Qte_CommandeLiv2);
            jsonObject.put("Reliquat", Reliquat);
            jsonObject.put("Qte_CommandeLiv3", Qte_CommandeLiv3);
            jsonObject.put("Qte_CommandeLiv4", Qte_CommandeLiv4);

        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Preparation_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Preparation_Ligne preparation_ligne = (Preparation_Ligne) obj;

        if (this.getPhiMR4UUID() == preparation_ligne.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > preparation_ligne.get_UID() ? 1 : -1;
        }
    }
}
