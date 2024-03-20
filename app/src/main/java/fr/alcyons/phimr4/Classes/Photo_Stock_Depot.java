package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Photo_Stock_DepotOpenHelper;
import fr.alcyons.phimr4.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class Photo_Stock_Depot implements Serializable, Comparable {

    private int Code_Produit;
    private String Ref_Depot;
    private int Annee;
    private int Mois;
    private int STOCK_DEBUT;
    private int QTE_ENTREE_CDE;
    private int QTE_SORTIE_DELIV;
    private int QTE_ENTREE_DELIV;
    private int QTE_SORTIE_RETOUR;
    private int QTE_ENTREE_RETOUR;
    private int QTE_SORTIE_REGUL;
    private int QTE_ENTREE_REGUL;
    private int QTE_SORTIE_ECART;
    private int QTE_ENTREE_ECART;
    private int STOCK_FIN;
    private double VALEUR_STOCK_FIN;
    private double VALEUR_STOCK_DEBUT;
    private String CATEGORIE;
    private String REFERENCE;
    private String DESIGNATION;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private double Prix_Unit;
    private int Consommation_mensuelle;
    private int Qte_Entree;
    private int Qte_sortie;
    private String Ref_Cycle;
    private int Consommation_Trimestrielle;
    private Boolean Livraison_Directe;
    private int ID_Inv;
    private double Conso_Journaliere;
    private int Conso_Q1;
    private int Conso_Q2;
    private int Conso_Q3;
    private int Conso_Q4;
    private int Conso_S1;
    private int Conso_S2;
    private int Conso_S3;
    private int Conso_S4;
    private int Conso_S5;
    private double PMP_TTC_Fin_mois;
    private int Qte_Entree_Facturee;
    private double PMP_TTC_Debut_mois;
    private double Valeur_Achats_HT;
    private double Valeur_Achats_TTC;
    private String Ref_Cycle_Depot_Pdt;
    private String Ref_Depot_Pdt;
    private double TVA;
    private String Fournisseur;
    private int classe_id;
    private int QTE_Administrer;
    private int Seance_NB;
    private int QTE_Retour_PUI;
    private int QTE_Retour_FRS;
    private int QTE_Destruction;
    private int QTE_Ordonner;
    private int QTE_SeanceNB;
    private int _UID;
    private int QTE_COMMANDE;
    private int QTE_RAF;
    private int phiMR4UUID = -1;

    public Photo_Stock_Depot(int code_Produit, String ref_Depot, int annee, int mois, int STOCK_DEBUT, int QTE_ENTREE_CDE, int QTE_SORTIE_DELIV, int QTE_ENTREE_DELIV, int QTE_SORTIE_RETOUR, int QTE_ENTREE_RETOUR, int QTE_SORTIE_REGUL, int QTE_ENTREE_REGUL, int QTE_SORTIE_ECART, int QTE_ENTREE_ECART, int STOCK_FIN, double VALEUR_STOCK_FIN, double VALEUR_STOCK_DEBUT, String CATEGORIE, String REFERENCE, String DESIGNATION, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, double prix_Unit, int consommation_mensuelle, int qte_Entree, int qte_sortie, String ref_Cycle, int consommation_Trimestrielle, Boolean livraison_Directe, int ID_Inv, double conso_Journaliere, int conso_Q1, int conso_Q2, int conso_Q3, int conso_Q4, int conso_S1, int conso_S2, int conso_S3, int conso_S4, int conso_S5, double PMP_TTC_Fin_mois, int qte_Entree_Facturee, double PMP_TTC_Debut_mois, double valeur_Achats_HT, double valeur_Achats_TTC, String ref_Cycle_Depot_Pdt, String ref_Depot_Pdt, double TVA, String fournisseur, int classe_id, int QTE_Administrer, int seance_NB, int QTE_Retour_PUI, int QTE_Retour_FRS, int QTE_Destruction, int QTE_Ordonner, int QTE_SeanceNB, int _UID, int QTE_COMMANDE, int QTE_RAF) {
        this.Code_Produit = code_Produit;
        this.Ref_Depot = ref_Depot;
        this.Annee = annee;
        this.Mois = mois;
        this.STOCK_DEBUT = STOCK_DEBUT;
        this.QTE_ENTREE_CDE = QTE_ENTREE_CDE;
        this.QTE_SORTIE_DELIV = QTE_SORTIE_DELIV;
        this.QTE_ENTREE_DELIV = QTE_ENTREE_DELIV;
        this.QTE_SORTIE_RETOUR = QTE_SORTIE_RETOUR;
        this.QTE_ENTREE_RETOUR = QTE_ENTREE_RETOUR;
        this.QTE_SORTIE_REGUL = QTE_SORTIE_REGUL;
        this.QTE_ENTREE_REGUL = QTE_ENTREE_REGUL;
        this.QTE_SORTIE_ECART = QTE_SORTIE_ECART;
        this.QTE_ENTREE_ECART = QTE_ENTREE_ECART;
        this.STOCK_FIN = STOCK_FIN;
        this.VALEUR_STOCK_FIN = VALEUR_STOCK_FIN;
        this.VALEUR_STOCK_DEBUT = VALEUR_STOCK_DEBUT;
        this.CATEGORIE = CATEGORIE;
        this.REFERENCE = REFERENCE;
        this.DESIGNATION = DESIGNATION;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.Prix_Unit = prix_Unit;
        this.Consommation_mensuelle = consommation_mensuelle;
        this.Qte_Entree = qte_Entree;
        this.Qte_sortie = qte_sortie;
        this.Ref_Cycle = ref_Cycle;
        this.Consommation_Trimestrielle = consommation_Trimestrielle;
        this.Livraison_Directe = livraison_Directe;
        this.ID_Inv = ID_Inv;
        this.Conso_Journaliere = conso_Journaliere;
        this.Conso_Q1 = conso_Q1;
        this.Conso_Q2 = conso_Q2;
        this.Conso_Q3 = conso_Q3;
        this.Conso_Q4 = conso_Q4;
        this.Conso_S1 = conso_S1;
        this.Conso_S2 = conso_S2;
        this.Conso_S3 = conso_S3;
        this.Conso_S4 = conso_S4;
        this.Conso_S5 = conso_S5;
        this.PMP_TTC_Fin_mois = PMP_TTC_Fin_mois;
        this.Qte_Entree_Facturee = qte_Entree_Facturee;
        this.PMP_TTC_Debut_mois = PMP_TTC_Debut_mois;
        this.Valeur_Achats_HT = valeur_Achats_HT;
        this.Valeur_Achats_TTC = valeur_Achats_TTC;
        this.Ref_Cycle_Depot_Pdt = ref_Cycle_Depot_Pdt;
        this.Ref_Depot_Pdt = ref_Depot_Pdt;
        this.TVA = TVA;
        this.Fournisseur = fournisseur;
        this.classe_id = classe_id;
        this.QTE_Administrer = QTE_Administrer;
        this.Seance_NB = seance_NB;
        this.QTE_Retour_PUI = QTE_Retour_PUI;
        this.QTE_Retour_FRS = QTE_Retour_FRS;
        this.QTE_Destruction = QTE_Destruction;
        this.QTE_Ordonner = QTE_Ordonner;
        this.QTE_SeanceNB = QTE_SeanceNB;
        this._UID = _UID;
        this.QTE_COMMANDE = QTE_COMMANDE;
        this.QTE_RAF = QTE_RAF;
    }

    public Photo_Stock_Depot(JSONObject jsonObject) {
        try {
            this.Code_Produit = jsonObject.getInt("Code_Produit");
            this.Ref_Depot = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Depot"));
            this.Annee = jsonObject.getInt("Annee");
            this.Mois = jsonObject.getInt("Mois");
            this.STOCK_DEBUT = jsonObject.getInt("STOCK_DEBUT");
            this.QTE_ENTREE_CDE = jsonObject.getInt("QTE_ENTREE_CDE");
            this.QTE_SORTIE_DELIV = jsonObject.getInt("QTE_SORTIE_DELIV");
            this.QTE_ENTREE_DELIV = jsonObject.getInt("QTE_ENTREE_DELIV");
            this.QTE_SORTIE_RETOUR = jsonObject.getInt("QTE_SORTIE_RETOUR");
            this.QTE_ENTREE_RETOUR = jsonObject.getInt("QTE_ENTREE_RETOUR");
            this.QTE_SORTIE_REGUL = jsonObject.getInt("QTE_SORTIE_REGUL");
            this.QTE_ENTREE_REGUL = jsonObject.getInt("QTE_ENTREE_REGUL");
            this.QTE_SORTIE_ECART = jsonObject.getInt("QTE_SORTIE_ECART");
            this.QTE_ENTREE_ECART = jsonObject.getInt("QTE_ENTREE_ECART");
            this.STOCK_FIN = jsonObject.getInt("STOCK_FIN");
            this.VALEUR_STOCK_FIN = jsonObject.getDouble("VALEUR_STOCK_FIN");
            this.VALEUR_STOCK_DEBUT = jsonObject.getDouble("VALEUR_STOCK_DEBUT");
            this.CATEGORIE = OutilsGestionClasses.recupererString(jsonObject.getString("CATEGORIE"));
            this.REFERENCE = OutilsGestionClasses.recupererString(jsonObject.getString("REFERENCE"));
            this.DESIGNATION = OutilsGestionClasses.recupererString(jsonObject.getString("DESIGNATION"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.Prix_Unit = jsonObject.getDouble("Prix_Unit");
            this.Consommation_mensuelle = jsonObject.getInt("Consommation_mensuelle");
            this.Qte_Entree = jsonObject.getInt("Qte_Entree");
            this.Qte_sortie = jsonObject.getInt("Qte_sortie");
            this.Ref_Cycle = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Cycle"));
            this.Consommation_Trimestrielle = jsonObject.getInt("Consommation_Trimestrielle");
            this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(jsonObject, "Livraison_Directe");
            this.ID_Inv = jsonObject.getInt("ID_Inv");
            this.Conso_Journaliere = jsonObject.getDouble("Conso_Journaliere");
            this.Conso_Q1 = jsonObject.getInt("Conso_Q1");
            this.Conso_Q2 = jsonObject.getInt("Conso_Q2");
            this.Conso_Q3 = jsonObject.getInt("Conso_Q3");
            this.Conso_Q4 = jsonObject.getInt("Conso_Q4");
            this.Conso_S1 = jsonObject.getInt("Conso_S1");
            this.Conso_S2 = jsonObject.getInt("Conso_S2");
            this.Conso_S3 = jsonObject.getInt("Conso_S3");
            this.Conso_S4 = jsonObject.getInt("Conso_S4");
            this.Conso_S5 = jsonObject.getInt("Conso_S5");
            this.PMP_TTC_Fin_mois = jsonObject.getDouble("PMP_TTC_Fin_mois");
            this.Qte_Entree_Facturee = jsonObject.getInt("Qte_Entree_Facturee");
            this.PMP_TTC_Debut_mois = jsonObject.getDouble("PMP_TTC_Debut_mois");
            this.Valeur_Achats_HT = jsonObject.getDouble("Valeur_Achats_HT");
            this.Valeur_Achats_TTC = jsonObject.getDouble("Valeur_Achats_TTC");
            this.Ref_Cycle_Depot_Pdt = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Cycle_Depot_Pdt"));
            this.Ref_Depot_Pdt = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Depot_Pdt"));
            this.TVA = jsonObject.getDouble("TVA");
            this.Fournisseur = OutilsGestionClasses.recupererString(jsonObject.getString("Fournisseur"));
            this.classe_id = jsonObject.getInt("classe_id");
            this.QTE_Administrer = jsonObject.getInt("QTE_Administrer");
            this.Seance_NB = jsonObject.getInt("Seance_NB");
            this.QTE_Retour_PUI = jsonObject.getInt("QTE_Retour_PUI");
            this.QTE_Retour_FRS = jsonObject.getInt("QTE_Retour_FRS");
            this.QTE_Destruction = jsonObject.getInt("QTE_Destruction");
            this.QTE_Ordonner = jsonObject.getInt("QTE_Ordonner");
            this.QTE_SeanceNB = jsonObject.getInt("QTE_SeanceNB");
            this._UID = jsonObject.getInt("_UID");
            this.QTE_COMMANDE = jsonObject.getInt("QTE_COMMANDE");
            this.QTE_RAF = jsonObject.getInt("QTE_RAF");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Photo_Stock_Depot(Cursor cursor) {
        this.Code_Produit = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CODE_PRODUIT_PHOTO_STOCK_DEPOT);
        this.Ref_Depot = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_REF_DEPOT_PHOTO_STOCK_DEPOT);
        this.Annee = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_ANNEE_PHOTO_STOCK_DEPOT);
        this.Mois = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_MOIS_PHOTO_STOCK_DEPOT);
        this.STOCK_DEBUT = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_STOCK_DEBUT_PHOTO_STOCK_DEPOT);
        this.QTE_ENTREE_CDE = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_ENTREE_CDE_PHOTO_STOCK_DEPOT);
        this.QTE_SORTIE_DELIV = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_DEPOT);
        this.QTE_ENTREE_DELIV = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_DEPOT);
        this.QTE_SORTIE_RETOUR = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_DEPOT);
        this.QTE_ENTREE_RETOUR = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_DEPOT);
        this.QTE_SORTIE_REGUL = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_DEPOT);
        this.QTE_ENTREE_REGUL = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_DEPOT);
        this.QTE_SORTIE_ECART = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_SORTIE_ECART_PHOTO_STOCK_DEPOT);
        this.QTE_ENTREE_ECART = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_ENTREE_ECART_PHOTO_STOCK_DEPOT);
        this.STOCK_FIN = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_STOCK_FIN_PHOTO_STOCK_DEPOT);
        this.VALEUR_STOCK_FIN = cursor.getDouble(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_DEPOT);
        this.VALEUR_STOCK_DEBUT = cursor.getDouble(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_DEPOT);
        this.CATEGORIE = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CATEGORIE_PHOTO_STOCK_DEPOT);
        this.REFERENCE = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_REFERENCE_PHOTO_STOCK_DEPOT);
        this.DESIGNATION = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_DESIGNATION_PHOTO_STOCK_DEPOT);
        this.SYS_DT_MAJ = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PHOTO_STOCK_DEPOT);
        this.SYS_HEURE_MAJ = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PHOTO_STOCK_DEPOT);
        this.SYS_USER_MAJ = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PHOTO_STOCK_DEPOT);
        this.Prix_Unit = cursor.getDouble(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_PRIX_UNIT_PHOTO_STOCK_DEPOT);
        this.Consommation_mensuelle = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_DEPOT);
        this.Qte_Entree = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_ENTREE_PHOTO_STOCK_DEPOT);
        this.Qte_sortie = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_SORTIE_PHOTO_STOCK_DEPOT);
        this.Ref_Cycle = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_REF_CYCLE_PHOTO_STOCK_DEPOT);
        this.Consommation_Trimestrielle = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_DEPOT);
        this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(cursor, Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_DIRECTE_PHOTO_STOCK_DEPOT);
        this.ID_Inv = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_ID_INV_PHOTO_STOCK_DEPOT);
        this.Conso_Journaliere = cursor.getDouble(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSO_JOURNALIERE_PHOTO_STOCK_DEPOT);
        this.Conso_Q1 = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSO_Q1_PHOTO_STOCK_DEPOT);
        this.Conso_Q2 = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSO_Q2_PHOTO_STOCK_DEPOT);
        this.Conso_Q3 = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSO_Q3_PHOTO_STOCK_DEPOT);
        this.Conso_Q4 = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSO_Q4_PHOTO_STOCK_DEPOT);
        this.Conso_S1 = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSO_S1_PHOTO_STOCK_DEPOT);
        this.Conso_S2 = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSO_S2_PHOTO_STOCK_DEPOT);
        this.Conso_S3 = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSO_S3_PHOTO_STOCK_DEPOT);
        this.Conso_S4 = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSO_S4_PHOTO_STOCK_DEPOT);
        this.Conso_S5 = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CONSO_S5_PHOTO_STOCK_DEPOT);
        this.PMP_TTC_Fin_mois = cursor.getDouble(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_PMP_TTC_FIN_MOIS_PHOTO_STOCK_DEPOT);
        this.Qte_Entree_Facturee = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_ENTREE_FACTUREE_PHOTO_STOCK_DEPOT);
        this.PMP_TTC_Debut_mois = cursor.getDouble(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_PMP_TTC_DEBUT_MOIS_PHOTO_STOCK_DEPOT);
        this.Valeur_Achats_HT = cursor.getDouble(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_VALEUR_ACHATS_HT_PHOTO_STOCK_DEPOT);
        this.Valeur_Achats_TTC = cursor.getDouble(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_VALEUR_ACHATS_TTC_PHOTO_STOCK_DEPOT);
        this.Ref_Cycle_Depot_Pdt = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_REF_CYCLE_DEPOT_PDT_PHOTO_STOCK_DEPOT);
        this.Ref_Depot_Pdt = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_REF_DEPOT_PDT_PHOTO_STOCK_DEPOT);
        this.TVA = cursor.getDouble(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_TVA_PHOTO_STOCK_DEPOT);
        this.Fournisseur = cursor.getString(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_FOURNISSEUR_PHOTO_STOCK_DEPOT);
        this.classe_id = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_CLASSE_ID_PHOTO_STOCK_DEPOT);
        this.QTE_Administrer = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_ADMINISTRER_PHOTO_STOCK_DEPOT);
        this.Seance_NB = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_SEANCE_NB_PHOTO_STOCK_DEPOT);
        this.QTE_Retour_PUI = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_RETOUR_PUI_PHOTO_STOCK_DEPOT);
        this.QTE_Retour_FRS = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_RETOUR_FRS_PHOTO_STOCK_DEPOT);
        this.QTE_Destruction = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_DESTRUCTION_PHOTO_STOCK_DEPOT);
        this.QTE_Ordonner = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_ORDONNER_PHOTO_STOCK_DEPOT);
        this.QTE_SeanceNB = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_SEANCENB_PHOTO_STOCK_DEPOT);
        this._UID = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL__UID_PHOTO_STOCK_DEPOT);
        this.QTE_COMMANDE = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_COMMANDE_PHOTO_STOCK_DEPOT);
        this.QTE_RAF = cursor.getInt(Photo_Stock_DepotOpenHelper.Constantes.NUM_COL_QTE_RAF_PHOTO_STOCK_DEPOT);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getCode_Produit() {
        return Code_Produit;
    }

    public void setCode_Produit(int code_Produit) {
        Code_Produit = code_Produit;
    }

    public String getRef_Depot() {
        return Ref_Depot;
    }

    public void setRef_Depot(String ref_Depot) {
        Ref_Depot = ref_Depot;
    }

    public int getAnnee() {
        return Annee;
    }

    public void setAnnee(int annee) {
        Annee = annee;
    }

    public int getMois() {
        return Mois;
    }

    public void setMois(int mois) {
        Mois = mois;
    }

    public int getSTOCK_DEBUT() {
        return STOCK_DEBUT;
    }

    public void setSTOCK_DEBUT(int STOCK_DEBUT) {
        this.STOCK_DEBUT = STOCK_DEBUT;
    }

    public int getQTE_ENTREE_CDE() {
        return QTE_ENTREE_CDE;
    }

    public void setQTE_ENTREE_CDE(int QTE_ENTREE_CDE) {
        this.QTE_ENTREE_CDE = QTE_ENTREE_CDE;
    }

    public int getQTE_SORTIE_DELIV() {
        return QTE_SORTIE_DELIV;
    }

    public void setQTE_SORTIE_DELIV(int QTE_SORTIE_DELIV) {
        this.QTE_SORTIE_DELIV = QTE_SORTIE_DELIV;
    }

    public int getQTE_ENTREE_DELIV() {
        return QTE_ENTREE_DELIV;
    }

    public void setQTE_ENTREE_DELIV(int QTE_ENTREE_DELIV) {
        this.QTE_ENTREE_DELIV = QTE_ENTREE_DELIV;
    }

    public int getQTE_SORTIE_RETOUR() {
        return QTE_SORTIE_RETOUR;
    }

    public void setQTE_SORTIE_RETOUR(int QTE_SORTIE_RETOUR) {
        this.QTE_SORTIE_RETOUR = QTE_SORTIE_RETOUR;
    }

    public int getQTE_ENTREE_RETOUR() {
        return QTE_ENTREE_RETOUR;
    }

    public void setQTE_ENTREE_RETOUR(int QTE_ENTREE_RETOUR) {
        this.QTE_ENTREE_RETOUR = QTE_ENTREE_RETOUR;
    }

    public int getQTE_SORTIE_REGUL() {
        return QTE_SORTIE_REGUL;
    }

    public void setQTE_SORTIE_REGUL(int QTE_SORTIE_REGUL) {
        this.QTE_SORTIE_REGUL = QTE_SORTIE_REGUL;
    }

    public int getQTE_ENTREE_REGUL() {
        return QTE_ENTREE_REGUL;
    }

    public void setQTE_ENTREE_REGUL(int QTE_ENTREE_REGUL) {
        this.QTE_ENTREE_REGUL = QTE_ENTREE_REGUL;
    }

    public int getQTE_SORTIE_ECART() {
        return QTE_SORTIE_ECART;
    }

    public void setQTE_SORTIE_ECART(int QTE_SORTIE_ECART) {
        this.QTE_SORTIE_ECART = QTE_SORTIE_ECART;
    }

    public int getQTE_ENTREE_ECART() {
        return QTE_ENTREE_ECART;
    }

    public void setQTE_ENTREE_ECART(int QTE_ENTREE_ECART) {
        this.QTE_ENTREE_ECART = QTE_ENTREE_ECART;
    }

    public int getSTOCK_FIN() {
        return STOCK_FIN;
    }

    public void setSTOCK_FIN(int STOCK_FIN) {
        this.STOCK_FIN = STOCK_FIN;
    }

    public double getVALEUR_STOCK_FIN() {
        return VALEUR_STOCK_FIN;
    }

    public void setVALEUR_STOCK_FIN(double VALEUR_STOCK_FIN) {
        this.VALEUR_STOCK_FIN = VALEUR_STOCK_FIN;
    }

    public double getVALEUR_STOCK_DEBUT() {
        return VALEUR_STOCK_DEBUT;
    }

    public void setVALEUR_STOCK_DEBUT(double VALEUR_STOCK_DEBUT) {
        this.VALEUR_STOCK_DEBUT = VALEUR_STOCK_DEBUT;
    }

    public String getCATEGORIE() {
        return CATEGORIE;
    }

    public void setCATEGORIE(String CATEGORIE) {
        this.CATEGORIE = CATEGORIE;
    }

    public String getREFERENCE() {
        return REFERENCE;
    }

    public void setREFERENCE(String REFERENCE) {
        this.REFERENCE = REFERENCE;
    }

    public String getDESIGNATION() {
        return DESIGNATION;
    }

    public void setDESIGNATION(String DESIGNATION) {
        this.DESIGNATION = DESIGNATION;
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

    public double getPrix_Unit() {
        return Prix_Unit;
    }

    public void setPrix_Unit(double prix_Unit) {
        Prix_Unit = prix_Unit;
    }

    public int getConsommation_mensuelle() {
        return Consommation_mensuelle;
    }

    public void setConsommation_mensuelle(int consommation_mensuelle) {
        Consommation_mensuelle = consommation_mensuelle;
    }

    public int getQte_Entree() {
        return Qte_Entree;
    }

    public void setQte_Entree(int qte_Entree) {
        Qte_Entree = qte_Entree;
    }

    public int getQte_sortie() {
        return Qte_sortie;
    }

    public void setQte_sortie(int qte_sortie) {
        Qte_sortie = qte_sortie;
    }

    public String getRef_Cycle() {
        return Ref_Cycle;
    }

    public void setRef_Cycle(String ref_Cycle) {
        Ref_Cycle = ref_Cycle;
    }

    public int getConsommation_Trimestrielle() {
        return Consommation_Trimestrielle;
    }

    public void setConsommation_Trimestrielle(int consommation_Trimestrielle) {
        Consommation_Trimestrielle = consommation_Trimestrielle;
    }

    public Boolean getLivraison_Directe() {
        return Livraison_Directe;
    }

    public void setLivraison_Directe(Boolean livraison_Directe) {
        Livraison_Directe = livraison_Directe;
    }

    public int getID_Inv() {
        return ID_Inv;
    }

    public void setID_Inv(int ID_Inv) {
        this.ID_Inv = ID_Inv;
    }

    public double getConso_Journaliere() {
        return Conso_Journaliere;
    }

    public void setConso_Journaliere(double conso_Journaliere) {
        Conso_Journaliere = conso_Journaliere;
    }

    public int getConso_Q1() {
        return Conso_Q1;
    }

    public void setConso_Q1(int conso_Q1) {
        Conso_Q1 = conso_Q1;
    }

    public int getConso_Q2() {
        return Conso_Q2;
    }

    public void setConso_Q2(int conso_Q2) {
        Conso_Q2 = conso_Q2;
    }

    public int getConso_Q3() {
        return Conso_Q3;
    }

    public void setConso_Q3(int conso_Q3) {
        Conso_Q3 = conso_Q3;
    }

    public int getConso_Q4() {
        return Conso_Q4;
    }

    public void setConso_Q4(int conso_Q4) {
        Conso_Q4 = conso_Q4;
    }

    public int getConso_S1() {
        return Conso_S1;
    }

    public void setConso_S1(int conso_S1) {
        Conso_S1 = conso_S1;
    }

    public int getConso_S2() {
        return Conso_S2;
    }

    public void setConso_S2(int conso_S2) {
        Conso_S2 = conso_S2;
    }

    public int getConso_S3() {
        return Conso_S3;
    }

    public void setConso_S3(int conso_S3) {
        Conso_S3 = conso_S3;
    }

    public int getConso_S4() {
        return Conso_S4;
    }

    public void setConso_S4(int conso_S4) {
        Conso_S4 = conso_S4;
    }

    public int getConso_S5() {
        return Conso_S5;
    }

    public void setConso_S5(int conso_S5) {
        Conso_S5 = conso_S5;
    }

    public double getPMP_TTC_Fin_mois() {
        return PMP_TTC_Fin_mois;
    }

    public void setPMP_TTC_Fin_mois(double PMP_TTC_Fin_mois) {
        this.PMP_TTC_Fin_mois = PMP_TTC_Fin_mois;
    }

    public int getQte_Entree_Facturee() {
        return Qte_Entree_Facturee;
    }

    public void setQte_Entree_Facturee(int qte_Entree_Facturee) {
        Qte_Entree_Facturee = qte_Entree_Facturee;
    }

    public double getPMP_TTC_Debut_mois() {
        return PMP_TTC_Debut_mois;
    }

    public void setPMP_TTC_Debut_mois(double PMP_TTC_Debut_mois) {
        this.PMP_TTC_Debut_mois = PMP_TTC_Debut_mois;
    }

    public double getValeur_Achats_HT() {
        return Valeur_Achats_HT;
    }

    public void setValeur_Achats_HT(double valeur_Achats_HT) {
        Valeur_Achats_HT = valeur_Achats_HT;
    }

    public double getValeur_Achats_TTC() {
        return Valeur_Achats_TTC;
    }

    public void setValeur_Achats_TTC(double valeur_Achats_TTC) {
        Valeur_Achats_TTC = valeur_Achats_TTC;
    }

    public String getRef_Cycle_Depot_Pdt() {
        return Ref_Cycle_Depot_Pdt;
    }

    public void setRef_Cycle_Depot_Pdt(String ref_Cycle_Depot_Pdt) {
        Ref_Cycle_Depot_Pdt = ref_Cycle_Depot_Pdt;
    }

    public String getRef_Depot_Pdt() {
        return Ref_Depot_Pdt;
    }

    public void setRef_Depot_Pdt(String ref_Depot_Pdt) {
        Ref_Depot_Pdt = ref_Depot_Pdt;
    }

    public double getTVA() {
        return TVA;
    }

    public void setTVA(double TVA) {
        this.TVA = TVA;
    }

    public String getFournisseur() {
        return Fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        Fournisseur = fournisseur;
    }

    public int getClasse_id() {
        return classe_id;
    }

    public void setClasse_id(int classe_id) {
        this.classe_id = classe_id;
    }

    public int getQTE_Administrer() {
        return QTE_Administrer;
    }

    public void setQTE_Administrer(int QTE_Administrer) {
        this.QTE_Administrer = QTE_Administrer;
    }

    public int getSeance_NB() {
        return Seance_NB;
    }

    public void setSeance_NB(int seance_NB) {
        Seance_NB = seance_NB;
    }

    public int getQTE_Retour_PUI() {
        return QTE_Retour_PUI;
    }

    public void setQTE_Retour_PUI(int QTE_Retour_PUI) {
        this.QTE_Retour_PUI = QTE_Retour_PUI;
    }

    public int getQTE_Retour_FRS() {
        return QTE_Retour_FRS;
    }

    public void setQTE_Retour_FRS(int QTE_Retour_FRS) {
        this.QTE_Retour_FRS = QTE_Retour_FRS;
    }

    public int getQTE_Destruction() {
        return QTE_Destruction;
    }

    public void setQTE_Destruction(int QTE_Destruction) {
        this.QTE_Destruction = QTE_Destruction;
    }

    public int getQTE_Ordonner() {
        return QTE_Ordonner;
    }

    public void setQTE_Ordonner(int QTE_Ordonner) {
        this.QTE_Ordonner = QTE_Ordonner;
    }

    public int getQTE_SeanceNB() {
        return QTE_SeanceNB;
    }

    public void setQTE_SeanceNB(int QTE_SeanceNB) {
        this.QTE_SeanceNB = QTE_SeanceNB;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public int getQTE_COMMANDE() {
        return QTE_COMMANDE;
    }

    public void setQTE_COMMANDE(int QTE_COMMANDE) {
        this.QTE_COMMANDE = QTE_COMMANDE;
    }

    public int getQTE_RAF() {
        return QTE_RAF;
    }

    public void setQTE_RAF(int QTE_RAF) {
        this.QTE_RAF = QTE_RAF;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Code_Produit", Code_Produit);
            jsonObject.put("Ref_Depot", Ref_Depot);
            jsonObject.put("Annee", Annee);
            jsonObject.put("Mois", Mois);
            jsonObject.put("STOCK_DEBUT", STOCK_DEBUT);
            jsonObject.put("QTE_ENTREE_CDE", QTE_ENTREE_CDE);
            jsonObject.put("QTE_SORTIE_DELIV", QTE_SORTIE_DELIV);
            jsonObject.put("QTE_ENTREE_DELIV", QTE_ENTREE_DELIV);
            jsonObject.put("QTE_SORTIE_RETOUR", QTE_SORTIE_RETOUR);
            jsonObject.put("QTE_ENTREE_RETOUR", QTE_ENTREE_RETOUR);
            jsonObject.put("QTE_SORTIE_REGUL", QTE_SORTIE_REGUL);
            jsonObject.put("QTE_ENTREE_REGUL", QTE_ENTREE_REGUL);
            jsonObject.put("QTE_SORTIE_ECART", QTE_SORTIE_ECART);
            jsonObject.put("QTE_ENTREE_ECART", QTE_ENTREE_ECART);
            jsonObject.put("STOCK_FIN", STOCK_FIN);
            jsonObject.put("VALEUR_STOCK_FIN", VALEUR_STOCK_FIN);
            jsonObject.put("VALEUR_STOCK_DEBUT", VALEUR_STOCK_DEBUT);
            jsonObject.put("CATEGORIE", CATEGORIE);
            jsonObject.put("REFERENCE", REFERENCE);
            jsonObject.put("DESIGNATION", DESIGNATION);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("Prix_Unit", Prix_Unit);
            jsonObject.put("Consommation_mensuelle", Consommation_mensuelle);
            jsonObject.put("Qte_Entree", Qte_Entree);
            jsonObject.put("Qte_sortie", Qte_sortie);
            jsonObject.put("Ref_Cycle", Ref_Cycle);
            jsonObject.put("Consommation_Trimestrielle", Consommation_Trimestrielle);
            jsonObject.put("Livraison_Directe", Livraison_Directe);
            jsonObject.put("ID_Inv", ID_Inv);
            jsonObject.put("Conso_Journaliere", Conso_Journaliere);
            jsonObject.put("Conso_Q1", Conso_Q1);
            jsonObject.put("Conso_Q2", Conso_Q2);
            jsonObject.put("Conso_Q3", Conso_Q3);
            jsonObject.put("Conso_Q4", Conso_Q4);
            jsonObject.put("Conso_S1", Conso_S1);
            jsonObject.put("Conso_S2", Conso_S2);
            jsonObject.put("Conso_S3", Conso_S3);
            jsonObject.put("Conso_S4", Conso_S4);
            jsonObject.put("Conso_S5", Conso_S5);
            jsonObject.put("PMP_TTC_Fin_mois", PMP_TTC_Fin_mois);
            jsonObject.put("Qte_Entree_Facturee", Qte_Entree_Facturee);
            jsonObject.put("PMP_TTC_Debut_mois", PMP_TTC_Debut_mois);
            jsonObject.put("Valeur_Achats_HT", Valeur_Achats_HT);
            jsonObject.put("Valeur_Achats_TTC", Valeur_Achats_TTC);
            jsonObject.put("Ref_Cycle_Depot_Pdt", Ref_Cycle_Depot_Pdt);
            jsonObject.put("Ref_Depot_Pdt", Ref_Depot_Pdt);
            jsonObject.put("TVA", TVA);
            jsonObject.put("Fournisseur", Fournisseur);
            jsonObject.put("classe_id", classe_id);
            jsonObject.put("QTE_Administrer", QTE_Administrer);
            jsonObject.put("Seance_NB", Seance_NB);
            jsonObject.put("QTE_Retour_PUI", QTE_Retour_PUI);
            jsonObject.put("QTE_Retour_FRS", QTE_Retour_FRS);
            jsonObject.put("QTE_Destruction", QTE_Destruction);
            jsonObject.put("QTE_Ordonner", QTE_Ordonner);
            jsonObject.put("QTE_SeanceNB", QTE_SeanceNB);
            jsonObject.put("_UID", _UID);
            jsonObject.put("QTE_COMMANDE", QTE_COMMANDE);
            jsonObject.put("QTE_RAF", QTE_RAF);
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

        if (!(obj instanceof Photo_Stock_Depot)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Photo_Stock_Depot photo_stock_depot = (Photo_Stock_Depot) obj;

        if (this.getPhiMR4UUID() == photo_stock_depot.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > photo_stock_depot.get_UID() ? 1 : -1;
        }
    }
}
