package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Stock implements Serializable, Comparable {

    private int Produit_UID;
    private String Depot_Reference;
    private String STO_DT_INVENTAIRE;
    private double STO_QTE_INVENT;
    private String STO_DT_DER_ENTREE;
    private double STO_QTE_ENTREE;
    private String STO_DT_DER_SORTIE;
    private double STO_QTE_SORTIE;
    private double STO_QTE_AVANT_INVENT;
    private double STO_QTE_ATTENDUE;
    private double Quantite_Actuelle;
    private double STO_PRIX;
    private double STO_VAL_ENTREES;
    private double STO_VAL_SORTIES;
    private double STO_VAL_INVENT;
    private int STO_JOURS_ROTATION;
    private String STO_DT_CREAT;
    private String SYS_DT_MAJ;
    private String SYS_USER_MAJ;
    private String SYS_HEURE_MAJ;
    private double TVA;
    private String Categorie;
    private String produit_Reference;
    private String Designation;
    private Boolean Arret_distribution;
    private double Valeur_HT;
    private double Valeur_TTC;
    private String ZONE_STOCKAGE;
    private String Fournisseur;
    private Boolean Livraison_Directe;
    private double PUMP_HT_Derniere_cloture;
    private double Valeur_PUMP_HT;
    private double Valeur_PUMP_TTC;
    private double PUMP_TTC_Derniere_cloture;
    private int Classification;
    private Boolean Inventaire_Fin_de_Mois;
    private Boolean RAZ_Stock_Inventaire;
    private double SeuilAlerte;
    private Boolean produitSelect;
    private int _UID;
    private int phiwms_mobileUUID = -1;

    public Stock(int produit_UID, String depot_Reference, String STO_DT_INVENTAIRE, double STO_QTE_INVENT, String STO_DT_DER_ENTREE, double STO_QTE_ENTREE, String STO_DT_DER_SORTIE, double STO_QTE_SORTIE, double STO_QTE_AVANT_INVENT, double STO_QTE_ATTENDUE, double quantite_Actuelle, double STO_PRIX, double STO_VAL_ENTREES, double STO_VAL_SORTIES, double STO_VAL_INVENT, int STO_JOURS_ROTATION, String STO_DT_CREAT, String SYS_DT_MAJ, String SYS_USER_MAJ, String SYS_HEURE_MAJ, double TVA, String categorie, String produit_Reference, String designation, Boolean arret_distribution, double valeur_HT, double valeur_TTC, String ZONE_STOCKAGE, String fournisseur, Boolean livraison_Directe, double PUMP_HT_Derniere_cloture, double valeur_PUMP_HT, double valeur_PUMP_TTC, double PUMP_TTC_Derniere_cloture, int classification, Boolean inventaire_Fin_de_Mois, Boolean RAZ_Stock_Inventaire, double seuilAlerte, int _UID) {
        this.Produit_UID = produit_UID;
        this.Depot_Reference = depot_Reference;
        this.STO_DT_INVENTAIRE = STO_DT_INVENTAIRE;
        this.STO_QTE_INVENT = STO_QTE_INVENT;
        this.STO_DT_DER_ENTREE = STO_DT_DER_ENTREE;
        this.STO_QTE_ENTREE = STO_QTE_ENTREE;
        this.STO_DT_DER_SORTIE = STO_DT_DER_SORTIE;
        this.STO_QTE_SORTIE = STO_QTE_SORTIE;
        this.STO_QTE_AVANT_INVENT = STO_QTE_AVANT_INVENT;
        this.STO_QTE_ATTENDUE = STO_QTE_ATTENDUE;
        this.Quantite_Actuelle = quantite_Actuelle;
        this.STO_PRIX = STO_PRIX;
        this.STO_VAL_ENTREES = STO_VAL_ENTREES;
        this.STO_VAL_SORTIES = STO_VAL_SORTIES;
        this.STO_VAL_INVENT = STO_VAL_INVENT;
        this.STO_JOURS_ROTATION = STO_JOURS_ROTATION;
        this.STO_DT_CREAT = STO_DT_CREAT;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.TVA = TVA;
        this.Categorie = categorie;
        this.produit_Reference = produit_Reference;
        this.Designation = designation;
        this.Arret_distribution = arret_distribution;
        this.Valeur_HT = valeur_HT;
        this.Valeur_TTC = valeur_TTC;
        this.ZONE_STOCKAGE = ZONE_STOCKAGE;
        this.Fournisseur = fournisseur;
        this.Livraison_Directe = livraison_Directe;
        this.PUMP_HT_Derniere_cloture = PUMP_HT_Derniere_cloture;
        this.Valeur_PUMP_HT = valeur_PUMP_HT;
        this.Valeur_PUMP_TTC = valeur_PUMP_TTC;
        this.PUMP_TTC_Derniere_cloture = PUMP_TTC_Derniere_cloture;
        this.Classification = classification;
        this.Inventaire_Fin_de_Mois = inventaire_Fin_de_Mois;
        this.RAZ_Stock_Inventaire = RAZ_Stock_Inventaire;
        this.SeuilAlerte = seuilAlerte;
        this._UID = _UID;
    }

    public Stock(int produit_UID, String depot_Reference, String STO_DT_INVENTAIRE, double STO_QTE_INVENT, String STO_DT_DER_ENTREE, double STO_QTE_ENTREE, String STO_DT_DER_SORTIE, double STO_QTE_SORTIE, double STO_QTE_AVANT_INVENT, double STO_QTE_ATTENDUE, double quantite_Actuelle, double STO_PRIX, double STO_VAL_ENTREES, double STO_VAL_SORTIES, double STO_VAL_INVENT, int STO_JOURS_ROTATION, String STO_DT_CREAT, String SYS_DT_MAJ, String SYS_USER_MAJ, String SYS_HEURE_MAJ, double TVA, String categorie, String produit_Reference, String designation, Boolean arret_distribution, double valeur_HT, double valeur_TTC, String ZONE_STOCKAGE, String fournisseur, Boolean livraison_Directe, double PUMP_HT_Derniere_cloture, double valeur_PUMP_HT, double valeur_PUMP_TTC, double PUMP_TTC_Derniere_cloture, int classification, Boolean inventaire_Fin_de_Mois, Boolean RAZ_Stock_Inventaire, double seuilAlerte, int _UID, Boolean produitSelect) {
        this.Produit_UID = produit_UID;
        this.Depot_Reference = depot_Reference;
        this.STO_DT_INVENTAIRE = STO_DT_INVENTAIRE;
        this.STO_QTE_INVENT = STO_QTE_INVENT;
        this.STO_DT_DER_ENTREE = STO_DT_DER_ENTREE;
        this.STO_QTE_ENTREE = STO_QTE_ENTREE;
        this.STO_DT_DER_SORTIE = STO_DT_DER_SORTIE;
        this.STO_QTE_SORTIE = STO_QTE_SORTIE;
        this.STO_QTE_AVANT_INVENT = STO_QTE_AVANT_INVENT;
        this.STO_QTE_ATTENDUE = STO_QTE_ATTENDUE;
        this.Quantite_Actuelle = quantite_Actuelle;
        this.STO_PRIX = STO_PRIX;
        this.STO_VAL_ENTREES = STO_VAL_ENTREES;
        this.STO_VAL_SORTIES = STO_VAL_SORTIES;
        this.STO_VAL_INVENT = STO_VAL_INVENT;
        this.STO_JOURS_ROTATION = STO_JOURS_ROTATION;
        this.STO_DT_CREAT = STO_DT_CREAT;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.TVA = TVA;
        this.Categorie = categorie;
        this.produit_Reference = produit_Reference;
        this.Designation = designation;
        this.Arret_distribution = arret_distribution;
        this.Valeur_HT = valeur_HT;
        this.Valeur_TTC = valeur_TTC;
        this.ZONE_STOCKAGE = ZONE_STOCKAGE;
        this.Fournisseur = fournisseur;
        this.Livraison_Directe = livraison_Directe;
        this.PUMP_HT_Derniere_cloture = PUMP_HT_Derniere_cloture;
        this.Valeur_PUMP_HT = valeur_PUMP_HT;
        this.Valeur_PUMP_TTC = valeur_PUMP_TTC;
        this.PUMP_TTC_Derniere_cloture = PUMP_TTC_Derniere_cloture;
        this.Classification = classification;
        this.Inventaire_Fin_de_Mois = inventaire_Fin_de_Mois;
        this.RAZ_Stock_Inventaire = RAZ_Stock_Inventaire;
        this.SeuilAlerte = seuilAlerte;
        this._UID = _UID;
        this.produitSelect = produitSelect;
    }

    /*public Stock(JSONObject jsonObject) {
        try {
            this.Produit_UID = jsonObject.getInt("Produit_UID");
            this.Depot_Reference = OutilsGestionClasses.recupererString(jsonObject.getString("Depot_Reference"));
            this.STO_DT_INVENTAIRE = OutilsGestionClasses.recupererString(jsonObject.getString("STO_DT_INVENTAIRE"));
            this.STO_QTE_INVENT = jsonObject.getDouble("STO_QTE_INVENT");
            this.STO_DT_DER_ENTREE = OutilsGestionClasses.recupererString(jsonObject.getString("STO_DT_DER_ENTREE"));
            this.STO_QTE_ENTREE = jsonObject.getDouble("STO_QTE_ENTREE");
            this.STO_DT_DER_SORTIE = OutilsGestionClasses.recupererString(jsonObject.getString("STO_DT_DER_SORTIE"));
            this.STO_QTE_SORTIE = jsonObject.getDouble("STO_QTE_SORTIE");
            this.STO_QTE_AVANT_INVENT = jsonObject.getDouble("STO_QTE_AVANT_INVENT");
            this.STO_QTE_ATTENDUE = jsonObject.getDouble("STO_QTE_ATTENDUE");
            this.Quantite_Actuelle = jsonObject.getDouble("Quantite_Actuelle");
            this.STO_PRIX = jsonObject.getDouble("STO_PRIX");
            this.STO_VAL_ENTREES = jsonObject.getDouble("STO_VAL_ENTREES");
            this.STO_VAL_SORTIES = jsonObject.getDouble("STO_VAL_SORTIES");
            this.STO_VAL_INVENT = jsonObject.getDouble("STO_VAL_INVENT");
            this.STO_JOURS_ROTATION = jsonObject.getInt("STO_JOURS_ROTATION");
            this.STO_DT_CREAT = OutilsGestionClasses.recupererString(jsonObject.getString("STO_DT_CREAT"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.TVA = jsonObject.getDouble("TVA");
            this.Categorie = OutilsGestionClasses.recupererString(jsonObject.getString("Categorie"));
            this.produit_Reference = OutilsGestionClasses.recupererString(jsonObject.getString("produit_Reference"));
            this.Designation = OutilsGestionClasses.recupererString(jsonObject.getString("Designation"));
            this.Arret_distribution = OutilsGestionClasses.recupererBooleen(jsonObject, "Arret_distribution");
            this.Valeur_HT = jsonObject.getDouble("Valeur_HT");
            this.Valeur_TTC = jsonObject.getDouble("Valeur_TTC");
            this.ZONE_STOCKAGE = OutilsGestionClasses.recupererString(jsonObject.getString("ZONE_STOCKAGE"));
            this.Fournisseur = OutilsGestionClasses.recupererString(jsonObject.getString("Fournisseur"));
            this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(jsonObject, "Livraison_Directe");
            this.PUMP_HT_Derniere_cloture = jsonObject.getDouble("PUMP_HT_Derniere_cloture");
            this.Valeur_PUMP_HT = jsonObject.getDouble("Valeur_PUMP_HT");
            this.Valeur_PUMP_TTC = jsonObject.getDouble("Valeur_PUMP_TTC");
            this.PUMP_TTC_Derniere_cloture = jsonObject.getDouble("PUMP_TTC_Derniere_cloture");
            this.Classification = jsonObject.getInt("Classification");
            this.Inventaire_Fin_de_Mois = OutilsGestionClasses.recupererBooleen(jsonObject, "Inventaire_Fin_de_Mois");
            this.RAZ_Stock_Inventaire = OutilsGestionClasses.recupererBooleen(jsonObject, "RAZ_Stock_Inventaire");
            this.SeuilAlerte = jsonObject.getDouble("SeuilAlerte");
            this.produitSelect = OutilsGestionClasses.recupererBooleen(jsonObject, "ProduitSelect");
            this._UID = jsonObject.getInt("_UID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public Stock(JSONObject jsonObject) {
        try {
            this.Produit_UID = jsonObject.getInt("Produit_UID");
            this.Depot_Reference = OutilsGestionClasses.recupererString(jsonObject.getString("Depot_Reference"));
            this.Quantite_Actuelle = jsonObject.getDouble("Quantite_Actuelle");
            this.produit_Reference = OutilsGestionClasses.recupererString(jsonObject.getString("produit_Reference"));
            this.Designation = OutilsGestionClasses.recupererString(jsonObject.getString("Designation"));
            this.Fournisseur = OutilsGestionClasses.recupererString(jsonObject.getString("Fournisseur"));
            this._UID = jsonObject.getInt("_UID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/*    public Stock(Cursor cursor) {
        this.Produit_UID = cursor.getInt(StockOpenHelper.Constantes.NUM_COL_PRODUIT_UID_STOCK);
        this.Depot_Reference = cursor.getString(StockOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_STOCK);
        this.STO_DT_INVENTAIRE = cursor.getString(StockOpenHelper.Constantes.NUM_COL_STO_DT_INVENTAIRE_STOCK);
        this.STO_QTE_INVENT = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_STO_QTE_INVENT_STOCK);
        this.STO_DT_DER_ENTREE = cursor.getString(StockOpenHelper.Constantes.NUM_COL_STO_DT_DER_ENTREE_STOCK);
        this.STO_QTE_ENTREE = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_STO_QTE_ENTREE_STOCK);
        this.STO_DT_DER_SORTIE = cursor.getString(StockOpenHelper.Constantes.NUM_COL_STO_DT_DER_SORTIE_STOCK);
        this.STO_QTE_SORTIE = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_STO_QTE_SORTIE_STOCK);
        this.STO_QTE_AVANT_INVENT = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_STO_QTE_AVANT_INVENT_STOCK);
        this.STO_QTE_ATTENDUE = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_STO_QTE_ATTENDUE_STOCK);
        this.Quantite_Actuelle = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_QUANTITE_ACTUELLE_STOCK);
        this.STO_PRIX = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_STO_PRIX_STOCK);
        this.STO_VAL_ENTREES = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_STO_VAL_ENTREES_STOCK);
        this.STO_VAL_SORTIES = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_STO_VAL_SORTIES_STOCK);
        this.STO_VAL_INVENT = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_STO_VAL_INVENT_STOCK);
        this.STO_JOURS_ROTATION = cursor.getInt(StockOpenHelper.Constantes.NUM_COL_STO_JOURS_ROTATION_STOCK);
        this.STO_DT_CREAT = cursor.getString(StockOpenHelper.Constantes.NUM_COL_STO_DT_CREAT_STOCK);
        this.SYS_DT_MAJ = cursor.getString(StockOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_STOCK);
        this.SYS_USER_MAJ = cursor.getString(StockOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_STOCK);
        this.SYS_HEURE_MAJ = cursor.getString(StockOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_STOCK);
        this.TVA = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_TVA_STOCK);
        this.Categorie = cursor.getString(StockOpenHelper.Constantes.NUM_COL_CATEGORIE_STOCK);
        this.produit_Reference = cursor.getString(StockOpenHelper.Constantes.NUM_COL_PRODUIT_REFERENCE_STOCK);
        this.Designation = cursor.getString(StockOpenHelper.Constantes.NUM_COL_DESIGNATION_STOCK);
        this.Arret_distribution = OutilsGestionClasses.recupererBooleen(cursor, StockOpenHelper.Constantes.NUM_COL_ARRET_DISTRIBUTION_STOCK);
        this.Valeur_HT = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_VALEUR_HT_STOCK);
        this.Valeur_TTC = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_VALEUR_TTC_STOCK);
        this.ZONE_STOCKAGE = cursor.getString(StockOpenHelper.Constantes.NUM_COL_ZONE_STOCKAGE_STOCK);
        this.Fournisseur = cursor.getString(StockOpenHelper.Constantes.NUM_COL_FOURNISSEUR_STOCK);
        this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(cursor, StockOpenHelper.Constantes.NUM_COL_LIVRAISON_DIRECTE_STOCK);
        this.PUMP_HT_Derniere_cloture = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_PUMP_HT_DERNIERE_CLOTURE_STOCK);
        this.Valeur_PUMP_HT = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_VALEUR_PUMP_HT_STOCK);
        this.Valeur_PUMP_TTC = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_VALEUR_PUMP_TTC_STOCK);
        this.PUMP_TTC_Derniere_cloture = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_PUMP_TTC_DERNIERE_CLOTURE_STOCK);
        this.Classification = cursor.getInt(StockOpenHelper.Constantes.NUM_COL_CLASSIFICATION_STOCK);
        this.Inventaire_Fin_de_Mois = OutilsGestionClasses.recupererBooleen(cursor, StockOpenHelper.Constantes.NUM_COL_INVENTAIRE_FIN_DE_MOIS_STOCK);
        this.RAZ_Stock_Inventaire = OutilsGestionClasses.recupererBooleen(cursor, StockOpenHelper.Constantes.NUM_COL_RAZ_STOCK_INVENTAIRE_STOCK);
        this.SeuilAlerte = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_SEUILALERTE_STOCK);
        this._UID = cursor.getInt(StockOpenHelper.Constantes.NUM_COL__UID_STOCK);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }*/

    public Stock(Cursor cursor) {
        this.Produit_UID = cursor.getInt(StockOpenHelper.Constantes.NUM_COL_PRODUIT_UID_STOCK);
        this.Depot_Reference = cursor.getString(StockOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_STOCK);
        this.Quantite_Actuelle = cursor.getDouble(StockOpenHelper.Constantes.NUM_COL_QUANTITE_ACTUELLE_STOCK);
        this.produit_Reference = cursor.getString(StockOpenHelper.Constantes.NUM_COL_PRODUIT_REFERENCE_STOCK);
        this.Designation = cursor.getString(StockOpenHelper.Constantes.NUM_COL_DESIGNATION_STOCK);
        this.Fournisseur = cursor.getString(StockOpenHelper.Constantes.NUM_COL_FOURNISSEUR_STOCK);
        this._UID = cursor.getInt(StockOpenHelper.Constantes.NUM_COL__UID_STOCK);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getProduit_UID() {
        return Produit_UID;
    }

    public void setProduit_UID(int produit_UID) {
        Produit_UID = produit_UID;
    }

    public String getDepot_Reference() {
        return Depot_Reference;
    }

    public void setDepot_Reference(String depot_Reference) {
        Depot_Reference = depot_Reference;
    }

    public String getSTO_DT_INVENTAIRE() {
        return STO_DT_INVENTAIRE;
    }

    public void setSTO_DT_INVENTAIRE(String STO_DT_INVENTAIRE) {
        this.STO_DT_INVENTAIRE = STO_DT_INVENTAIRE;
    }

    public double getSTO_QTE_INVENT() {
        return STO_QTE_INVENT;
    }

    public void setSTO_QTE_INVENT(double STO_QTE_INVENT) {
        this.STO_QTE_INVENT = STO_QTE_INVENT;
    }

    public String getSTO_DT_DER_ENTREE() {
        return STO_DT_DER_ENTREE;
    }

    public void setSTO_DT_DER_ENTREE(String STO_DT_DER_ENTREE) {
        this.STO_DT_DER_ENTREE = STO_DT_DER_ENTREE;
    }

    public double getSTO_QTE_ENTREE() {
        return STO_QTE_ENTREE;
    }

    public void setSTO_QTE_ENTREE(double STO_QTE_ENTREE) {
        this.STO_QTE_ENTREE = STO_QTE_ENTREE;
    }

    public String getSTO_DT_DER_SORTIE() {
        return STO_DT_DER_SORTIE;
    }

    public void setSTO_DT_DER_SORTIE(String STO_DT_DER_SORTIE) {
        this.STO_DT_DER_SORTIE = STO_DT_DER_SORTIE;
    }

    public double getSTO_QTE_SORTIE() {
        return STO_QTE_SORTIE;
    }

    public void setSTO_QTE_SORTIE(double STO_QTE_SORTIE) {
        this.STO_QTE_SORTIE = STO_QTE_SORTIE;
    }

    public double getSTO_QTE_AVANT_INVENT() {
        return STO_QTE_AVANT_INVENT;
    }

    public void setSTO_QTE_AVANT_INVENT(double STO_QTE_AVANT_INVENT) {
        this.STO_QTE_AVANT_INVENT = STO_QTE_AVANT_INVENT;
    }

    public double getSTO_QTE_ATTENDUE() {
        return STO_QTE_ATTENDUE;
    }

    public void setSTO_QTE_ATTENDUE(double STO_QTE_ATTENDUE) {
        this.STO_QTE_ATTENDUE = STO_QTE_ATTENDUE;
    }

    public double getQuantite_Actuelle() {
        return Quantite_Actuelle;
    }

    public void setQuantite_Actuelle(double quantite_Actuelle) {
        Quantite_Actuelle = quantite_Actuelle;
    }

    public double getSTO_PRIX() {
        return STO_PRIX;
    }

    public void setSTO_PRIX(double STO_PRIX) {
        this.STO_PRIX = STO_PRIX;
    }

    public double getSTO_VAL_ENTREES() {
        return STO_VAL_ENTREES;
    }

    public void setSTO_VAL_ENTREES(double STO_VAL_ENTREES) {
        this.STO_VAL_ENTREES = STO_VAL_ENTREES;
    }

    public double getSTO_VAL_SORTIES() {
        return STO_VAL_SORTIES;
    }

    public void setSTO_VAL_SORTIES(double STO_VAL_SORTIES) {
        this.STO_VAL_SORTIES = STO_VAL_SORTIES;
    }

    public double getSTO_VAL_INVENT() {
        return STO_VAL_INVENT;
    }

    public void setSTO_VAL_INVENT(double STO_VAL_INVENT) {
        this.STO_VAL_INVENT = STO_VAL_INVENT;
    }

    public int getSTO_JOURS_ROTATION() {
        return STO_JOURS_ROTATION;
    }

    public void setSTO_JOURS_ROTATION(int STO_JOURS_ROTATION) {
        this.STO_JOURS_ROTATION = STO_JOURS_ROTATION;
    }

    public String getSTO_DT_CREAT() {
        return STO_DT_CREAT;
    }

    public void setSTO_DT_CREAT(String STO_DT_CREAT) {
        this.STO_DT_CREAT = STO_DT_CREAT;
    }

    public String getSYS_DT_MAJ() {
        return SYS_DT_MAJ;
    }

    public void setSYS_DT_MAJ(String SYS_DT_MAJ) {
        this.SYS_DT_MAJ = SYS_DT_MAJ;
    }

    public String getSYS_USER_MAJ() {
        return SYS_USER_MAJ;
    }

    public void setSYS_USER_MAJ(String SYS_USER_MAJ) {
        this.SYS_USER_MAJ = SYS_USER_MAJ;
    }

    public String getSYS_HEURE_MAJ() {
        return SYS_HEURE_MAJ;
    }

    public void setSYS_HEURE_MAJ(String SYS_HEURE_MAJ) {
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
    }

    public double getTVA() {
        return TVA;
    }

    public void setTVA(double TVA) {
        this.TVA = TVA;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public String getProduit_Reference() {
        return produit_Reference;
    }

    public void setProduit_Reference(String produit_Reference) {
        this.produit_Reference = produit_Reference;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public Boolean getArret_distribution() {
        return Arret_distribution;
    }

    public void setArret_distribution(Boolean arret_distribution) {
        Arret_distribution = arret_distribution;
    }

    public double getValeur_HT() {
        return Valeur_HT;
    }

    public void setValeur_HT(double valeur_HT) {
        Valeur_HT = valeur_HT;
    }

    public double getValeur_TTC() {
        return Valeur_TTC;
    }

    public void setValeur_TTC(double valeur_TTC) {
        Valeur_TTC = valeur_TTC;
    }

    public String getZONE_STOCKAGE() {
        return ZONE_STOCKAGE;
    }

    public void setZONE_STOCKAGE(String ZONE_STOCKAGE) {
        this.ZONE_STOCKAGE = ZONE_STOCKAGE;
    }

    public String getFournisseur() {
        return Fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        Fournisseur = fournisseur;
    }

    public Boolean getLivraison_Directe() {
        return Livraison_Directe;
    }

    public void setLivraison_Directe(Boolean livraison_Directe) {
        Livraison_Directe = livraison_Directe;
    }

    public double getPUMP_HT_Derniere_cloture() {
        return PUMP_HT_Derniere_cloture;
    }

    public void setPUMP_HT_Derniere_cloture(double PUMP_HT_Derniere_cloture) {
        this.PUMP_HT_Derniere_cloture = PUMP_HT_Derniere_cloture;
    }

    public double getValeur_PUMP_HT() {
        return Valeur_PUMP_HT;
    }

    public void setValeur_PUMP_HT(double valeur_PUMP_HT) {
        Valeur_PUMP_HT = valeur_PUMP_HT;
    }

    public double getValeur_PUMP_TTC() {
        return Valeur_PUMP_TTC;
    }

    public void setValeur_PUMP_TTC(double valeur_PUMP_TTC) {
        Valeur_PUMP_TTC = valeur_PUMP_TTC;
    }

    public double getPUMP_TTC_Derniere_cloture() {
        return PUMP_TTC_Derniere_cloture;
    }

    public void setPUMP_TTC_Derniere_cloture(double PUMP_TTC_Derniere_cloture) {
        this.PUMP_TTC_Derniere_cloture = PUMP_TTC_Derniere_cloture;
    }

    public int getClassification() {
        return Classification;
    }

    public void setClassification(int classification) {
        Classification = classification;
    }

    public Boolean getInventaire_Fin_de_Mois() {
        return Inventaire_Fin_de_Mois;
    }

    public void setInventaire_Fin_de_Mois(Boolean inventaire_Fin_de_Mois) {
        Inventaire_Fin_de_Mois = inventaire_Fin_de_Mois;
    }

    public Boolean getRAZ_Stock_Inventaire() {
        return RAZ_Stock_Inventaire;
    }

    public void setRAZ_Stock_Inventaire(Boolean RAZ_Stock_Inventaire) {
        this.RAZ_Stock_Inventaire = RAZ_Stock_Inventaire;
    }

    public double getSeuilAlerte() {
        return SeuilAlerte;
    }

    public void setSeuilAlerte(double seuilAlerte) {
        SeuilAlerte = seuilAlerte;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public Boolean getProduitSelect() {
        return produitSelect;
    }

    public void setProduitSelect(Boolean produitSelect) {
        this.produitSelect = produitSelect;
    }

    public int getNombreColis(SQLiteDatabase db, String contexte) {
        int nombreColis = 0;
        int conditionnementSet;

        Produit produit = ProduitOpenHelper.getProduitByID(db, this.getProduit_UID());
        int qte = (int) this.getQuantite_Actuelle();

        if (produit != null) {


            switch (contexte) {
                case "Achats":
                    conditionnementSet = produit.getCond_achat();
                    break;
                case "Achats Palette":
                    conditionnementSet = (int) produit.getCond_Achat_Gros_volume();
                    break;
                case "Distribution":
                    conditionnementSet = (int) produit.getCond_distrib();
                    break;
                case "Distribution Palette":
                    conditionnementSet = (int) produit.getCond_Achat_Gros_volume();
                    break;
                default:
                    conditionnementSet = (int) produit.getCond_distrib();
                    break;
            }

            if (qte > 0 && conditionnementSet > 0) {
                nombreColis = qte / conditionnementSet;
                if ((qte % conditionnementSet) != 0) {
                    nombreColis++;
                }
            }
            if (qte > 0) {
                if (nombreColis == 0) {
                    nombreColis = 1;
                }
            }


            return nombreColis;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Stock)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Stock stock = (Stock) obj;

        if (this.getphiwms_mobileUUID() == stock.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.get_UID() > stock.get_UID() ? 1 : -1;
        }
    }
}
