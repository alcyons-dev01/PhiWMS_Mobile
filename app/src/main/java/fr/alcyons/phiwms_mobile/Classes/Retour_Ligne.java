package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Retour_Ligne implements Serializable, Comparable {

    private int retour_UID;
    private int Code_produit;
    private String produit_Reference;
    private double Qte_Retourner;
    private String produit_Fournisseur;
    private double produit_PUHT;
    private double produit_TVA;
    private String produit_Designation;
    private double Montant_TTC;
    private int piece_Code;
    private String Date_validation;
    private String Destination;
    private String Devise;
    private double Qte_avant_retour;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String Lot;
    private String patientIdentite;
    private int _UID;
    private double Qte_Demander;
    private String Lot_Retourner;
    private String peremptionDate;
    private int Destruction_Qte;
    private int RetourPui_Qte;
    private int RetourFrs_Qte;
    private int Quarantaine_Qte_Demander;
    private String RetourPUI_Emplacement;
    private String RetourPUI_Zone;
    private String emplacementOrigine;
    String Serie_Retourner;
    private int patientID;
    private int phiwms_mobileUUID = -1;

    public Retour_Ligne(int retour_UID, int code_produit, String produit_Reference, double qte_Retourner, String produit_Fournisseur, double produit_PUHT, double produit_TVA, String produit_Designation, double montant_TTC, int piece_Code, String date_validation, String destination, String devise, double qte_avant_retour, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, String lot, String patientIdentite, int _UID, double qte_Demander, String lot_Retourner, String peremptionDate, int destruction_Qte, int retourPui_Qte, int retourFrs_Qte, int quarantaine_Qte_Demander, String retourPUI_Emplacement, String retourPUI_Zone, String emplacementOrigine, int patientID) {
        this.retour_UID = retour_UID;
        this.Code_produit = code_produit;
        this.produit_Reference = produit_Reference;
        this.Qte_Retourner = qte_Retourner;
        this.produit_Fournisseur = produit_Fournisseur;
        this.produit_PUHT = produit_PUHT;
        this.produit_TVA = produit_TVA;
        this.produit_Designation = produit_Designation;
        this.Montant_TTC = montant_TTC;
        this.piece_Code = piece_Code;
        this.Date_validation = date_validation;
        this.Destination = destination;
        this.Devise = devise;
        this.Qte_avant_retour = qte_avant_retour;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.Lot = lot;
        this.patientIdentite = patientIdentite;
        this._UID = _UID;
        this.Qte_Demander = qte_Demander;
        this.Lot_Retourner = lot_Retourner;
        this.peremptionDate = peremptionDate;
        this.Destruction_Qte = destruction_Qte;
        this.RetourPui_Qte = retourPui_Qte;
        this.RetourFrs_Qte = retourFrs_Qte;
        this.Quarantaine_Qte_Demander = quarantaine_Qte_Demander;
        this.RetourPUI_Emplacement = retourPUI_Emplacement;
        this.RetourPUI_Zone = retourPUI_Zone;
        this.emplacementOrigine = emplacementOrigine;
        this.patientID = patientID;
    }

    public Retour_Ligne(int _UID, int retour_UID, int qte_Demander, int code_produit, String produit_Reference, String produit_Fournisseur, int produit_PUHT, int produit_TVA, String produit_Designation, int qte_avant_retour, int montant_TTC) {
        this._UID = _UID;
        this.retour_UID = retour_UID;
        this.Qte_Demander = qte_Demander;
        this.Code_produit = code_produit;
        this.produit_Reference = produit_Reference;
        this.produit_Fournisseur = produit_Fournisseur;
        this.produit_PUHT = produit_PUHT;
        this.produit_TVA = produit_TVA;
        this.produit_Designation = produit_Designation;
        this.Qte_avant_retour = qte_avant_retour;
        this.Montant_TTC = montant_TTC;

    }

    public Retour_Ligne(Retour_Ligne retourLigne) {
        this.retour_UID = retourLigne.getRetour_UID();
        this.Code_produit = retourLigne.getCode_produit();
        this.produit_Reference = retourLigne.getProduit_Reference();
        this.Qte_Retourner = retourLigne.getQte_Retourner();
        this.produit_Fournisseur = retourLigne.getProduit_Fournisseur();
        this.produit_PUHT = retourLigne.getProduit_PUHT();
        this.produit_TVA = retourLigne.getProduit_TVA();
        this.produit_Designation = retourLigne.getProduit_Designation();
        this.Montant_TTC = retourLigne.getMontant_TTC();
        this.piece_Code = retourLigne.getPiece_Code();
        this.Date_validation = retourLigne.getDate_validation();
        this.Destination = retourLigne.getDestination();
        this.Devise = retourLigne.getDevise();
        this.Qte_avant_retour = retourLigne.getQte_avant_retour();
        this.SYS_DT_MAJ = retourLigne.getSYS_DT_MAJ();
        this.SYS_HEURE_MAJ = retourLigne.getSYS_HEURE_MAJ();
        this.SYS_USER_MAJ = retourLigne.getSYS_USER_MAJ();
        this.Lot = retourLigne.getLot();
        this.patientIdentite = retourLigne.getPatientIdentite();
        this._UID = retourLigne.get_UID();
        this.Qte_Demander = retourLigne.getQte_Demander();
        this.Lot_Retourner = retourLigne.getLot_Retourner();
        this.peremptionDate = retourLigne.getPeremptionDate();
        this.Destruction_Qte = retourLigne.getDestruction_Qte();
        this.RetourPui_Qte = retourLigne.getRetourPui_Qte();
        this.RetourFrs_Qte = retourLigne.getRetourFrs_Qte();
        this.Quarantaine_Qte_Demander = retourLigne.getQuarantaine_Qte_Demander();
        this.RetourPUI_Emplacement = retourLigne.getRetourPUI_Emplacement();
        this.RetourPUI_Zone = retourLigne.getRetourPUI_Zone();
        this.emplacementOrigine = retourLigne.getEmplacementOrigine();
        this.patientID = retourLigne.getPatientID();
        this.Serie_Retourner = retourLigne.getSerie_Retourner();
    }

    public Retour_Ligne(int uid) {
        this.retour_UID = 0;
        this.Code_produit = 0;
        this.produit_Reference = "";
        this.Qte_Retourner = 0;
        this.produit_Fournisseur = "";
        this.produit_PUHT = 0;
        this.produit_TVA = 0;
        this.produit_Designation = "";
        this.Montant_TTC = 0;
        this.piece_Code = 0;
        this.Date_validation = "";
        this.Destination = "";
        this.Devise = "";
        this.Qte_avant_retour = 0;
        this.SYS_DT_MAJ = "";
        this.SYS_HEURE_MAJ = "";
        this.SYS_USER_MAJ = "";
        this.Lot = "";
        this.patientIdentite = "";
        this._UID = uid;
        this.Qte_Demander = 0;
        this.Lot_Retourner = "";
        this.peremptionDate = "";
        this.Destruction_Qte = 0;
        this.RetourPui_Qte = 0;
        this.RetourFrs_Qte = 0;
        this.Quarantaine_Qte_Demander = 0;
        this.RetourPUI_Emplacement = "";
        this.RetourPUI_Zone = "";
        this.emplacementOrigine = "";
        this.Serie_Retourner = "";
        this.patientID = 0;
    }

/*    public Retour_Ligne(JSONObject jsonObject) {
        try {
            this.retour_UID = jsonObject.getInt("retour_UID");
            this.Code_produit = jsonObject.getInt("Code_produit");
            this.produit_Reference = recupererString(jsonObject.getString("produit_Reference"));
            this.Qte_Retourner = jsonObject.getDouble("Qte_Retourner");
            this.produit_Fournisseur = recupererString(jsonObject.getString("produit_Fournisseur"));
            this.produit_PUHT = jsonObject.getDouble("produit_PUHT");
            this.produit_TVA = jsonObject.getDouble("produit_TVA");
            this.produit_Designation = recupererString(jsonObject.getString("produit_Designation"));
            this.Montant_TTC = jsonObject.getDouble("Montant_TTC");
            this.piece_Code = jsonObject.getInt("piece_Code");
            this.Date_validation = recupererString(jsonObject.getString("Date_validation"));
            this.Destination = recupererString(jsonObject.getString("Destination"));
            this.Devise = recupererString(jsonObject.getString("Devise"));
            this.Qte_avant_retour = jsonObject.getDouble("Qte_avant_retour");
            this.SYS_DT_MAJ = recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.Lot = recupererString(jsonObject.getString("Lot"));
            this.patientIdentite = recupererString(jsonObject.getString("patientIdentite"));
            this._UID = jsonObject.getInt("_UID");
            this.Qte_Demander = jsonObject.getDouble("Qte_Demander");
            this.Lot_Retourner = recupererString(jsonObject.getString("Lot_Retourner"));
            this.peremptionDate = recupererString(jsonObject.getString("peremptionDate"));
            this.Destruction_Qte = jsonObject.getInt("Destruction_Qte");
            this.RetourPui_Qte = jsonObject.getInt("RetourPui_Qte");
            this.RetourFrs_Qte = jsonObject.getInt("RetourFrs_Qte");
            this.Quarantaine_Qte_Demander = jsonObject.getInt("Quarantaine_Qte_Demander");
            this.RetourPUI_Emplacement = recupererString(jsonObject.getString("RetourPUI_Emplacement"));
            this.RetourPUI_Zone = recupererString(jsonObject.getString("RetourPUI_Zone"));
            this.emplacementOrigine = recupererString(jsonObject.getString("emplacementOrigine"));
            this.patientID = jsonObject.getInt("patientID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public Retour_Ligne(JSONObject jsonObject) {
        try {
            this.retour_UID = jsonObject.getInt("retour_UID");
            this.produit_Reference = OutilsGestionClasses.recupererString(jsonObject.getString("produit_Reference"));
            this.Code_produit = jsonObject.getInt("Code_produit");
            this.Qte_Retourner = jsonObject.getDouble("Qte_Retourner");
            this.produit_Fournisseur = OutilsGestionClasses.recupererString(jsonObject.getString("produit_Fournisseur"));
            this.produit_Designation = OutilsGestionClasses.recupererString(jsonObject.getString("produit_Designation"));
            this.Destination = OutilsGestionClasses.recupererString(jsonObject.getString("Destination"));
            this.Qte_avant_retour = jsonObject.getDouble("Qte_avant_retour");
            this._UID = jsonObject.getInt("_UID");
            this.Qte_Demander = jsonObject.getDouble("Qte_Demander");
            this.Lot_Retourner = OutilsGestionClasses.recupererString(jsonObject.getString("Lot_Retourner"));
            this.peremptionDate = OutilsGestionClasses.recupererString(jsonObject.getString("peremptionDate"));
            this.Destruction_Qte = jsonObject.getInt("Destruction_Qte");
            this.RetourPui_Qte = jsonObject.getInt("RetourPui_Qte");
            this.RetourFrs_Qte = jsonObject.getInt("RetourFrs_Qte");
            this.Serie_Retourner = jsonObject.getString("Serie_Retourner");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/*    public Retour_Ligne(Cursor cursor) {
        this.retour_UID = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_RETOUR_UID_RETOUR_LIGNE);
        this.Code_produit = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_CODE_PRODUIT_RETOUR_LIGNE);
        this.produit_Reference = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_REFERENCE_RETOUR_LIGNE);
        this.Qte_Retourner = cursor.getDouble(Retour_LigneOpenHelper.Constantes.NUM_COL_QTE_RETOURNER_RETOUR_LIGNE);
        this.produit_Fournisseur = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_FOURNISSEUR_RETOUR_LIGNE);
        this.produit_PUHT = cursor.getDouble(Retour_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_PUHT_RETOUR_LIGNE);
        this.produit_TVA = cursor.getDouble(Retour_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_TVA_RETOUR_LIGNE);
        this.produit_Designation = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_DESIGNATION_RETOUR_LIGNE);
        this.Montant_TTC = cursor.getDouble(Retour_LigneOpenHelper.Constantes.NUM_COL_MONTANT_TTC_RETOUR_LIGNE);
        this.piece_Code = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_PIECE_CODE_RETOUR_LIGNE);
        this.Date_validation = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_DATE_VALIDATION_RETOUR_LIGNE);
        this.Destination = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_DESTINATION_RETOUR_LIGNE);
        this.Devise = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_DEVISE_RETOUR_LIGNE);
        this.Qte_avant_retour = cursor.getDouble(Retour_LigneOpenHelper.Constantes.NUM_COL_QTE_AVANT_RETOUR_RETOUR_LIGNE);
        this.SYS_DT_MAJ = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_RETOUR_LIGNE);
        this.SYS_HEURE_MAJ = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_RETOUR_LIGNE);
        this.SYS_USER_MAJ = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_RETOUR_LIGNE);
        this.Lot = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_LOT_RETOUR_LIGNE);
        this.patientIdentite = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_PATIENTIDENTITE_RETOUR_LIGNE);
        this._UID = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL__UID_RETOUR_LIGNE);
        this.Qte_Demander = cursor.getDouble(Retour_LigneOpenHelper.Constantes.NUM_COL_QTE_DEMANDER_RETOUR_LIGNE);
        this.Lot_Retourner = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_LOT_RETOURNER_RETOUR_LIGNE);
        this.peremptionDate = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_PEREMPTIONDATE_RETOUR_LIGNE);
        this.Destruction_Qte = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_DESTRUCTION_QTE_RETOUR_LIGNE);
        this.RetourPui_Qte = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_RETOURPUI_QTE_RETOUR_LIGNE);
        this.RetourFrs_Qte = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_RETOURFRS_QTE_RETOUR_LIGNE);
        this.Quarantaine_Qte_Demander = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_QUARANTAINE_QTE_DEMANDER_RETOUR_LIGNE);
        this.RetourPUI_Emplacement = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_RETOURPUI_EMPLACEMENT_RETOUR_LIGNE);
        this.RetourPUI_Zone = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_RETOURPUI_ZONE_RETOUR_LIGNE);
        this.emplacementOrigine = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_EMPLACEMENTORIGINE_RETOUR_LIGNE);
        this.patientID = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_PATIENTID_RETOUR_LIGNE);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }*/

    public Retour_Ligne(Cursor cursor) {
        this.retour_UID = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_RETOUR_UID_RETOUR_LIGNE);
        this.produit_Reference = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_REFERENCE_RETOUR_LIGNE);
        this.Code_produit = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_CODE_PRODUIT_RETOUR_LIGNE);
        this.Qte_Retourner = cursor.getDouble(Retour_LigneOpenHelper.Constantes.NUM_COL_QTE_RETOURNER_RETOUR_LIGNE);
        this.produit_Fournisseur = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_FOURNISSEUR_RETOUR_LIGNE);
        this.produit_Designation = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_DESIGNATION_RETOUR_LIGNE);
        this.Destination = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_DESTINATION_RETOUR_LIGNE);
        this.Qte_avant_retour = cursor.getDouble(Retour_LigneOpenHelper.Constantes.NUM_COL_QTE_AVANT_RETOUR_RETOUR_LIGNE);
        this._UID = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL__UID_RETOUR_LIGNE);
        this.Qte_Demander = cursor.getDouble(Retour_LigneOpenHelper.Constantes.NUM_COL_QTE_DEMANDER_RETOUR_LIGNE);
        this.Lot_Retourner = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_LOT_RETOURNER_RETOUR_LIGNE);
        this.peremptionDate = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_PEREMPTIONDATE_RETOUR_LIGNE);
        this.Destruction_Qte = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_DESTRUCTION_QTE_RETOUR_LIGNE);
        this.RetourPui_Qte = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_RETOURPUI_QTE_RETOUR_LIGNE);
        this.RetourFrs_Qte = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_RETOURFRS_QTE_RETOUR_LIGNE);
        this.Quarantaine_Qte_Demander = cursor.getInt(Retour_LigneOpenHelper.Constantes.NUM_COL_QUARANTAINE_QTE_DEMANDER_RETOUR_LIGNE);
        this.Serie_Retourner = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_SERIE_RETOURNER);
        this.RetourPUI_Emplacement = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_RETOURPUI_EMPLACEMENT_RETOUR_LIGNE);
        this.RetourPUI_Zone = cursor.getString(Retour_LigneOpenHelper.Constantes.NUM_COL_RETOURPUI_ZONE_RETOUR_LIGNE);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getRetour_UID() {
        return retour_UID;
    }

    public void setRetour_UID(int retour_UID) {
        this.retour_UID = retour_UID;
    }

    public int getCode_produit() {
        return Code_produit;
    }

    public void setCode_produit(int code_produit) {
        Code_produit = code_produit;
    }

    public String getProduit_Reference() {
        return produit_Reference;
    }

    public void setProduit_Reference(String produit_Reference) {
        this.produit_Reference = produit_Reference;
    }

    public double getQte_Retourner() {
        return Qte_Retourner;
    }

    public void setQte_Retourner(double qte_Retourner) {
        Qte_Retourner = qte_Retourner;
    }

    public String getProduit_Fournisseur() {
        return produit_Fournisseur;
    }

    public void setProduit_Fournisseur(String produit_Fournisseur) {
        this.produit_Fournisseur = produit_Fournisseur;
    }

    public double getProduit_PUHT() {
        return produit_PUHT;
    }

    public void setProduit_PUHT(double produit_PUHT) {
        this.produit_PUHT = produit_PUHT;
    }

    public double getProduit_TVA() {
        return produit_TVA;
    }

    public void setProduit_TVA(double produit_TVA) {
        this.produit_TVA = produit_TVA;
    }

    public String getProduit_Designation() {
        return produit_Designation;
    }

    public void setProduit_Designation(String produit_Designation) {
        this.produit_Designation = produit_Designation;
    }

    public double getMontant_TTC() {
        return Montant_TTC;
    }

    public void setMontant_TTC(double montant_TTC) {
        Montant_TTC = montant_TTC;
    }

    public int getPiece_Code() {
        return piece_Code;
    }

    public void setPiece_Code(int piece_Code) {
        this.piece_Code = piece_Code;
    }

    public String getDate_validation() {
        return Date_validation;
    }

    public void setDate_validation(String date_validation) {
        Date_validation = date_validation;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getDevise() {
        return Devise;
    }

    public void setDevise(String devise) {
        Devise = devise;
    }

    public double getQte_avant_retour() {
        return Qte_avant_retour;
    }

    public void setQte_avant_retour(double qte_avant_retour) {
        Qte_avant_retour = qte_avant_retour;
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

    public String getLot() {
        return Lot;
    }

    public void setLot(String lot) {
        Lot = lot;
    }

    public String getPatientIdentite() {
        return patientIdentite;
    }

    public void setPatientIdentite(String patientIdentite) {
        this.patientIdentite = patientIdentite;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public double getQte_Demander() {
        return Qte_Demander;
    }

    public void setQte_Demander(double qte_Demander) {
        Qte_Demander = qte_Demander;
    }

    public String getLot_Retourner() {
        return Lot_Retourner;
    }

    public void setLot_Retourner(String lot_Retourner) {
        Lot_Retourner = lot_Retourner;
    }

    public String getPeremptionDate() {
        return peremptionDate;
    }

    public void setPeremptionDate(String peremptionDate) {
        this.peremptionDate = peremptionDate;
    }

    public int getDestruction_Qte() {
        return Destruction_Qte;
    }

    public void setDestruction_Qte(int destruction_Qte) {
        Destruction_Qte = destruction_Qte;
    }

    public int getRetourPui_Qte() {
        return RetourPui_Qte;
    }

    public void setRetourPui_Qte(int retourPui_Qte) {
        RetourPui_Qte = retourPui_Qte;
    }

    public int getRetourFrs_Qte() {
        return RetourFrs_Qte;
    }

    public void setRetourFrs_Qte(int retourFrs_Qte) {
        RetourFrs_Qte = retourFrs_Qte;
    }

    public int getQuarantaine_Qte_Demander() {
        return Quarantaine_Qte_Demander;
    }

    public void setQuarantaine_Qte_Demander(int quarantaine_Qte_Demander) {
        Quarantaine_Qte_Demander = quarantaine_Qte_Demander;
    }

    public String getRetourPUI_Emplacement() {
        return RetourPUI_Emplacement;
    }

    public void setRetourPUI_Emplacement(String retourPUI_Emplacement) {
        RetourPUI_Emplacement = retourPUI_Emplacement;
    }

    public String getRetourPUI_Zone() {
        return RetourPUI_Zone;
    }

    public void setRetourPUI_Zone(String retourPUI_Zone) {
        RetourPUI_Zone = retourPUI_Zone;
    }

    public String getEmplacementOrigine() {
        return emplacementOrigine;
    }

    public void setEmplacementOrigine(String emplacementOrigine) {
        this.emplacementOrigine = emplacementOrigine;
    }

    public String getSerie_Retourner() {
        return Serie_Retourner;
    }

    public void setSerie_Retourner(String serie_Retourner) {
        Serie_Retourner = serie_Retourner;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public JSONObject toJson() {
        JSONObject retour_ligneJson = new JSONObject();

        try {
            retour_ligneJson.put("retour_UID", retour_UID);
            retour_ligneJson.put("Code_produit", Code_produit);
            retour_ligneJson.put("produit_Reference", produit_Reference);
            retour_ligneJson.put("Qte_Retourner", Qte_Retourner);
            retour_ligneJson.put("produit_Fournisseur", produit_Fournisseur);
            retour_ligneJson.put("produit_PUHT", produit_PUHT);
            retour_ligneJson.put("produit_TVA", produit_TVA);
            retour_ligneJson.put("produit_Designation", produit_Designation);
            retour_ligneJson.put("Montant_TTC", Montant_TTC);
            retour_ligneJson.put("piece_Code", piece_Code);
            retour_ligneJson.put("Date_validation", Date_validation);
            retour_ligneJson.put("Destination", Destination);
            retour_ligneJson.put("Devise", Devise);
            retour_ligneJson.put("Qte_avant_retour", Qte_avant_retour);
            retour_ligneJson.put("SYS_DT_MAJ", SYS_DT_MAJ);
            retour_ligneJson.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            retour_ligneJson.put("SYS_USER_MAJ", SYS_USER_MAJ);
            retour_ligneJson.put("Lot", Lot);
            retour_ligneJson.put("patientIdentite", patientIdentite);
            retour_ligneJson.put("_UID", _UID);
            retour_ligneJson.put("Qte_Demander", Qte_Demander);
            retour_ligneJson.put("Lot_Retourner", Lot_Retourner);
            retour_ligneJson.put("peremptionDate", peremptionDate);
            retour_ligneJson.put("Destruction_Qte", Destruction_Qte);
            retour_ligneJson.put("RetourPui_Qte", RetourPui_Qte);
            retour_ligneJson.put("RetourFrs_Qte", RetourFrs_Qte);
            retour_ligneJson.put("Quarantaine_Qte_Demander", Quarantaine_Qte_Demander);
            retour_ligneJson.put("RetourPUI_Emplacement", RetourPUI_Emplacement);
            retour_ligneJson.put("RetourPUI_Zone", RetourPUI_Zone);
            retour_ligneJson.put("emplacementOrigine", emplacementOrigine);
            retour_ligneJson.put("patientID", patientID);
            retour_ligneJson.put("Serie_Retourner", Serie_Retourner);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retour_ligneJson;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Retour_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Retour_Ligne retour_ligne = (Retour_Ligne) obj;

        if (this.getphiwms_mobileUUID() == retour_ligne.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.get_UID() > retour_ligne.get_UID() ? 1 : -1;
        }
    }
}
