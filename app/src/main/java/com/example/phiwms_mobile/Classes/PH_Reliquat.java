package com.example.phiwms_mobile.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import com.example.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;

import static com.example.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;
import static com.example.phiwms_mobile.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by olivier on 28/11/2017.
 */

public class PH_Reliquat implements Serializable, Comparable {

    private int Reliquat_UID;
    private int ProduitID;
    private String Produit_Reference;
    private int PU_commande;
    private String designationCourte;
    private String unite;
    private String fournisseurNom;
    private int FournisseurID;
    private int QteCommande;
    private int QteLivraison;
    private int QteReliquat_X;
    private boolean reliquatEncours;
    private String entreeDate;
    private boolean selection;
    private String commandeNumero;
    private String commandeDate;
    private int ConditionnementAchat;
    private int ConditionnementDistribution;
    private String peremptionDate;
    private String lot;
    private String scanReference;
    private boolean peremptionActive;
    private int commandeLigneID;
    private int Pu_facture;
    private int Repris;
    private int QteMouvement;
    private int QteReliquat_Y;
    private boolean ProduitGratuits;
    private String Devise;
    private String DepotReference;
    private String _SYS_DT_MAJ;
    private String _SYS_HEURE_MAJ;
    private String _SYS_USER_MAJ;
    private boolean SuiviParLotActif;
    private String SynchroTimeStamp;
    private String SynchroStatut;
    private String Finness;
    private String ScanValue;
    private String PatientIPP;
    private String PatientNom;
    private int tech_UID;
    private String Zone;
    private String Emplacement;
    private String IPP;
    private boolean SuiviParSerieActif;
    private boolean SerialiserReception;
    private String Serie;
    private String BL_Numero;
    private int phiMR4UUID = -1;

    public PH_Reliquat(int reliquat_UID, int produitID, String produit_Reference, int PU_commande, String designationCourte, String unite, String fournisseurNom, int fournisseurID, int qteCommande, int qteLivraison, int qteReliquat_X, Boolean reliquatEncours, String entreeDate, String commandeNumero, String commandeDate, int conditionnementAchat, int conditionnementDistribution, int commandeLigneID, int qteReliquat_Y, String devise, String depotReference, String _SYS_DT_MAJ, String _SYS_HEURE_MAJ, String patientIPP, String patientNom)
    {
        this.Reliquat_UID = reliquat_UID;
        this.ProduitID = produitID;
        this.Produit_Reference = produit_Reference;
        this.PU_commande = PU_commande;
        this.designationCourte = designationCourte;
        this.unite = unite;
        this.fournisseurNom = fournisseurNom;
        this.FournisseurID = fournisseurID;
        this.QteCommande = qteCommande;
        this.QteLivraison = qteLivraison;
        this.QteReliquat_X = qteReliquat_X;
        this.reliquatEncours = reliquatEncours;
        this.entreeDate = entreeDate;
        this.commandeNumero = commandeNumero;
        this.commandeDate = commandeDate;
        this.ConditionnementAchat = conditionnementAchat;
        this.ConditionnementDistribution = conditionnementDistribution;
        this.commandeLigneID = commandeLigneID;
        this.QteReliquat_Y = qteReliquat_Y;
        this.Devise = devise;
        this.DepotReference = depotReference;
        this._SYS_DT_MAJ = _SYS_DT_MAJ;
        this._SYS_HEURE_MAJ = _SYS_HEURE_MAJ;
        this.PatientIPP = patientIPP;
        this.PatientNom = patientNom;
    }

/*    public PH_Reliquat(JSONObject jsonObject) {
        try {
            this.Reliquat_UID = jsonObject.getInt("Reliquat_UID");
            this.ProduitID = jsonObject.getInt("ProduitID");
            this.Produit_Reference = recupererString(jsonObject.getString("Produit_Reference"));
            this.PU_commande = jsonObject.getInt("PU_commande");
            this.designationCourte = recupererString(jsonObject.getString("designationCourte"));
            this.unite = recupererString(jsonObject.getString("unite"));
            this.fournisseurNom = recupererString(jsonObject.getString("fournisseurNom"));
            this.FournisseurID = jsonObject.getInt("FournisseurID");
            this.QteCommande = jsonObject.getInt("QteCommande");
            this.QteLivraison = jsonObject.getInt("QteLivraison");
            this.QteReliquat_X = jsonObject.getInt("QteReliquat_X");
            this.reliquatEncours = recupererBooleen(jsonObject, "reliquatEncours");
            this.entreeDate = recupererString(jsonObject.getString("entreeDate"));
            this.selection = recupererBooleen(jsonObject, "selection");
            this.commandeNumero = recupererString(jsonObject.getString("commandeNumero"));
            this.commandeDate = recupererString(jsonObject.getString("commandeDate"));
            this.ConditionnementAchat = jsonObject.getInt("ConditionnementAchat");
            this.ConditionnementDistribution = jsonObject.getInt("ConditionnementDistribution");
            this.peremptionDate = recupererString(jsonObject.getString("peremptionDate"));
            this.lot = recupererString(jsonObject.getString("lot"));
            this.scanReference = recupererString(jsonObject.getString("scanReference"));
            this.peremptionActive = recupererBooleen(jsonObject, "peremptionActive");
            this.commandeLigneID = jsonObject.getInt("commandeLigneID");
            this.Pu_facture = jsonObject.getInt("Pu_facture");
            this.Repris = jsonObject.getInt("Repris");
            this.QteMouvement = jsonObject.getInt("QteMouvement");
            this.QteReliquat_Y = jsonObject.getInt("QteReliquat_Y");
            this.ProduitGratuits = recupererBooleen(jsonObject, "ProduitGratuits");
            this.Devise = recupererString(jsonObject.getString("Devise"));
            this.DepotReference = recupererString(jsonObject.getString("DepotReference"));
            this._SYS_DT_MAJ = recupererString(jsonObject.getString("_SYS_DT_MAJ"));
            this._SYS_HEURE_MAJ = recupererString(jsonObject.getString("_SYS_HEURE_MAJ"));
            this._SYS_USER_MAJ = recupererString(jsonObject.getString("_SYS_USER_MAJ"));
            this.SuiviParLotActif = recupererBooleen(jsonObject, "SuiviParLotActif");
            this.SynchroTimeStamp = recupererString(jsonObject.getString("SynchroTimeStamp"));
            this.SynchroStatut = recupererString(jsonObject.getString("SynchroStatut"));
            this.Finness = recupererString(jsonObject.getString("Finness"));
            this.ScanValue = recupererString(jsonObject.getString("ScanValue"));
            this.PatientIPP = recupererString(jsonObject.getString("PatientIPP"));
            this.PatientNom = recupererString(jsonObject.getString("PatientNom"));
            this.tech_UID = jsonObject.getInt("tech_UID");
            this.Zone = recupererString(jsonObject.getString("Zone"));
            this.Emplacement = recupererString(jsonObject.getString("Emplacement"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public PH_Reliquat(JSONObject jsonObject) {
        try {
            this.Reliquat_UID = jsonObject.getInt("Reliquat_UID");
            this.designationCourte = recupererString(jsonObject.getString("designationCourte"));
            this.Produit_Reference = recupererString(jsonObject.getString("Produit_Reference"));
            this.ProduitID = jsonObject.getInt("ProduitID");
            this.QteCommande = jsonObject.getInt("QteCommande");
            this.QteLivraison = jsonObject.getInt("QteLivraison");
            this.QteReliquat_X = jsonObject.getInt("QteReliquat_X");
            this.commandeLigneID = jsonObject.getInt("commandeLigneID");
            this.ConditionnementAchat = jsonObject.getInt("ConditionnementAchat");
            this.commandeNumero = recupererString(jsonObject.getString("commandeNumero"));
            this.peremptionDate = recupererString(jsonObject.getString("peremptionDate"));
           // this.IPP = recupererString(jsonObject.getString("IPP"));
            this.SuiviParSerieActif = recupererBooleen(jsonObject, "SuiviParSerieActif");
            this.SerialiserReception = recupererBooleen(jsonObject, "SerialiserReception");
            this.Serie = recupererString(jsonObject.getString("Serie"));
            this.BL_Numero = recupererString("BL_Numero");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/*    public PH_Reliquat(Cursor cursor) {
        this.Reliquat_UID = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_RELIQUAT_UID_PH_RELIQUAT);
        this.ProduitID = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_PRODUITID_PH_RELIQUAT);
        this.Produit_Reference = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_PRODUIT_REFERENCE_PH_RELIQUAT);
        this.PU_commande = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_PU_COMMANDE_PH_RELIQUAT);
        this.designationCourte = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_DESIGNATIONCOURTE_PH_RELIQUAT);
        this.unite = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_UNITE_PH_RELIQUAT);
        this.fournisseurNom = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_FOURNISSEURNOM_PH_RELIQUAT);
        this.FournisseurID = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_FOURNISSEURID_PH_RELIQUAT);
        this.QteCommande = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_QTECOMMANDE_PH_RELIQUAT);
        this.QteLivraison = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_QTELIVRAISON_PH_RELIQUAT);
        this.QteReliquat_X = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_QTERELIQUAT_X_PH_RELIQUAT);
        this.reliquatEncours = recupererBooleen(cursor, PH_ReliquatOpenHelper.Constantes.NUM_COL_RELIQUATENCOURS_PH_RELIQUAT);
        this.entreeDate = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_ENTREEDATE_PH_RELIQUAT);
        this.selection = recupererBooleen(cursor, PH_ReliquatOpenHelper.Constantes.NUM_COL_SELECTION_PH_RELIQUAT);
        this.commandeNumero = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_COMMANDENUMERO_PH_RELIQUAT);
        this.commandeDate = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_COMMANDEDATE_PH_RELIQUAT);
        this.ConditionnementAchat = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_CONDITIONNEMENTACHAT_PH_RELIQUAT);
        this.ConditionnementDistribution = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_CONDITIONNEMENTDISTRIBUTION_PH_RELIQUAT);
        this.peremptionDate = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_PEREMPTIONDATE_PH_RELIQUAT);
        this.lot = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_LOT_PH_RELIQUAT);
        this.scanReference = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_SCANREFERENCE_PH_RELIQUAT);
        this.peremptionActive = recupererBooleen(cursor, PH_ReliquatOpenHelper.Constantes.NUM_COL_PEREMPTIONACTIVE_PH_RELIQUAT);
        this.commandeLigneID = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_COMMANDELIGNEID_PH_RELIQUAT);
        this.Pu_facture = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_PU_FACTURE_PH_RELIQUAT);
        this.Repris = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_REPRIS_PH_RELIQUAT);
        this.QteMouvement = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_QTEMOUVEMENT_PH_RELIQUAT);
        this.QteReliquat_Y = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_QTERELIQUAT_Y_PH_RELIQUAT);
        this.ProduitGratuits = recupererBooleen(cursor, PH_ReliquatOpenHelper.Constantes.NUM_COL_PRODUITGRATUITS_PH_RELIQUAT);
        this.Devise = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_DEVISE_PH_RELIQUAT);
        this.DepotReference = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_DEPOTREFERENCE_PH_RELIQUAT);
        this._SYS_DT_MAJ = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL__SYS_DT_MAJ_PH_RELIQUAT);
        this._SYS_HEURE_MAJ = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL__SYS_HEURE_MAJ_PH_RELIQUAT);
        this._SYS_USER_MAJ = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL__SYS_USER_MAJ_PH_RELIQUAT);
        this.SuiviParLotActif = recupererBooleen(cursor, PH_ReliquatOpenHelper.Constantes.NUM_COL_SUIVIPARLOTACTIF_PH_RELIQUAT);
        this.SynchroTimeStamp = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_SYNCHROTIMESTAMP_PH_RELIQUAT);
        this.SynchroStatut = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_SYNCHROSTATUT_PH_RELIQUAT);
        this.Finness = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_FINNESS_PH_RELIQUAT);
        this.ScanValue = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_SCANVALUE_PH_RELIQUAT);
        this.PatientIPP = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_PATIENTIPP_PH_RELIQUAT);
        this.PatientNom = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_PATIENTNOM_PH_RELIQUAT);
        this.tech_UID = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_TECH_UID_PH_RELIQUAT);
        this.Zone = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_ZONE_PH_RELIQUAT);
        this.Emplacement = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_EMPLACEMENT_PH_RELIQUAT);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);

    }*/

    public PH_Reliquat(Cursor cursor) {
        this.Reliquat_UID = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_RELIQUAT_UID_PH_RELIQUAT);
        this.Produit_Reference = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_PRODUIT_REFERENCE_PH_RELIQUAT);
        this.designationCourte = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_DESIGNATIONCOURTE_PH_RELIQUAT);
        this.ProduitID = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_PRODUITID_PH_RELIQUAT);
        this.QteCommande = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_QTECOMMANDE_PH_RELIQUAT);
        this.commandeNumero = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_COMMANDENUMERO_PH_RELIQUAT);
        this.QteLivraison = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_QTELIVRAISON_PH_RELIQUAT);
        this.QteReliquat_X = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_QTERELIQUAT_X_PH_RELIQUAT);
        this.ConditionnementAchat = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_CONDITIONNEMENTACHAT_PH_RELIQUAT);
        this.IPP = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_IPP_PH_RELIQUAT);
        this.peremptionDate = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_PEREMPTIONDATE_PH_RELIQUAT);
        this.SuiviParSerieActif = recupererBooleen(cursor, PH_ReliquatOpenHelper.Constantes.NUM_COL_SUIVI_PAR_SERIE_ACTIF);
        this.SerialiserReception = recupererBooleen(cursor, PH_ReliquatOpenHelper.Constantes.NUM_COL_SERIALISATION_RECEPTION);
        this.Serie = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_SERIE);
        this.commandeLigneID = cursor.getInt(PH_ReliquatOpenHelper.Constantes.NUM_COL_COMMANDELIGNEID_PH_RELIQUAT);
        this.lot = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_LOT_PH_RELIQUAT);
        this.Zone = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_ZONE_PH_RELIQUAT);
        this.Emplacement = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_EMPLACEMENT_PH_RELIQUAT);
        this.BL_Numero = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_BL_Numero);
        this.ScanValue = cursor.getString(PH_ReliquatOpenHelper.Constantes.NUM_COL_SCANVALUE_PH_RELIQUAT);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);

    }

    public PH_Reliquat(int reliquat_UID) {
        Reliquat_UID = reliquat_UID;
    }

    public int getReliquat_UID() {
        return Reliquat_UID;
    }

    public void setReliquat_UID(int reliquat_UID) {
        Reliquat_UID = reliquat_UID;
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getPU_commande() {
        return PU_commande;
    }

    public void setPU_commande(int PU_commande) {
        this.PU_commande = PU_commande;
    }

    public String getdesignationCourte() {
        return designationCourte;
    }

    public String getunite() {
        return unite;
    }

    public String getfournisseurNom() {
        return fournisseurNom;
    }

    public boolean getreliquatEncours() {
        return reliquatEncours;
    }

    public String getentreeDate() {
        return entreeDate;
    }

    public boolean getselection() {
        return selection;
    }

    public String getcommandeNumero() {
        return commandeNumero;
    }

    public String getcommandeDate() {
        return commandeDate;
    }

    public String getperemptionDate() {
        return peremptionDate;
    }

    public String getlot() {
        return lot;
    }


    public String getscanReference() {
        return scanReference;
    }


    public boolean getperemptionActive() {
        return peremptionActive;
    }


    public int getcommandeLigneID() {
        return commandeLigneID;
    }


    public String get_SYS_DT_MAJ() {
        return _SYS_DT_MAJ;
    }

    public void set_SYS_DT_MAJ(String _SYS_DT_MAJ) {
        this._SYS_DT_MAJ = _SYS_DT_MAJ;
    }

    public String get_SYS_HEURE_MAJ() {
        return _SYS_HEURE_MAJ;
    }

    public void set_SYS_HEURE_MAJ(String _SYS_HEURE_MAJ) {
        this._SYS_HEURE_MAJ = _SYS_HEURE_MAJ;
    }

    public String get_SYS_USER_MAJ() {
        return _SYS_USER_MAJ;
    }

    public void set_SYS_USER_MAJ(String _SYS_USER_MAJ) {
        this._SYS_USER_MAJ = _SYS_USER_MAJ;
    }

    public int gettech_UID() {
        return tech_UID;
    }

    public int getProduitID() {
        return ProduitID;
    }

    public void setProduitID(int produitID) {
        ProduitID = produitID;
    }

    public String getProduit_Reference() {
        return Produit_Reference;
    }

    public void setProduit_Reference(String produit_Reference) {
        Produit_Reference = produit_Reference;
    }

    public int getFournisseurID() {
        return FournisseurID;
    }

    public void setFournisseurID(int fournisseurID) {
        FournisseurID = fournisseurID;
    }

    public int getQteCommande() {
        return QteCommande;
    }

    public void setQteCommande(int qteCommande) {
        QteCommande = qteCommande;
    }

    public int getQteLivraison() {
        return QteLivraison;
    }

    public void setQteLivraison(int qteLivraison) {
        QteLivraison = qteLivraison;
    }

    public int getQteReliquat_X() {
        return QteReliquat_X;
    }

    public void setQteReliquat_X(int qteReliquat_X) {
        QteReliquat_X = qteReliquat_X;
    }

    public int getConditionnementAchat() {
        return ConditionnementAchat;
    }

    public void setConditionnementAchat(int conditionnementAchat) {
        ConditionnementAchat = conditionnementAchat;
    }

    public int getConditionnementDistribution() {
        return ConditionnementDistribution;
    }

    public void setConditionnementDistribution(int conditionnementDistribution) {
        ConditionnementDistribution = conditionnementDistribution;
    }

    public int getPu_facture() {
        return Pu_facture;
    }

    public void setPu_facture(int pu_facture) {
        Pu_facture = pu_facture;
    }

    public int getRepris() {
        return Repris;
    }

    public void setRepris(int repris) {
        Repris = repris;
    }

    public int getQteMouvement() {
        return QteMouvement;
    }

    public void setQteMouvement(int qteMouvement) {
        QteMouvement = qteMouvement;
    }

    public int getQteReliquat_Y() {
        return QteReliquat_Y;
    }

    public void setQteReliquat_Y(int qteReliquat_Y) {
        QteReliquat_Y = qteReliquat_Y;
    }

    public boolean getProduitGratuits() {
        return ProduitGratuits;
    }

    public String getDevise() {
        return Devise;
    }

    public void setDevise(String devise) {
        Devise = devise;
    }

    public String getDepotReference() {
        return DepotReference;
    }

    public void setDepotReference(String depotReference) {
        DepotReference = depotReference;
    }

    public boolean getSuiviParLotActif() {
        return SuiviParLotActif;
    }

    public String getSynchroTimeStamp() {
        return SynchroTimeStamp;
    }

    public void setSynchroTimeStamp(String synchroTimeStamp) {
        SynchroTimeStamp = synchroTimeStamp;
    }

    public String getSynchroStatut() {
        return SynchroStatut;
    }

    public void setSynchroStatut(String synchroStatut) {
        SynchroStatut = synchroStatut;
    }

    public String getFinness() {
        return Finness;
    }

    public void setFinness(String finness) {
        Finness = finness;
    }

    public String getScanValue() {
        return ScanValue;
    }

    public void setScanValue(String scanValue) {
        ScanValue = scanValue;
    }

    public String getPatientIPP() {
        return PatientIPP;
    }

    public void setPatientIPP(String patientIPP) {
        PatientIPP = patientIPP;
    }

    public String getPatientNom() {
        return PatientNom;
    }

    public void setPatientNom(String patientNom) {
        PatientNom = patientNom;
    }

    public String getZone() {
        return Zone;
    }

    public void setZone(String zone) {
        Zone = zone;
    }

    public String getEmplacement() {
        return Emplacement;
    }

    public void setEmplacement(String emplacement) {
        Emplacement = emplacement;
    }

    public String getDesignationCourte() {
        return designationCourte;
    }

    public void setDesignationCourte(String designationCourte) {
        this.designationCourte = designationCourte;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public String getFournisseurNom() {
        return fournisseurNom;
    }

    public void setFournisseurNom(String fournisseurNom) {
        this.fournisseurNom = fournisseurNom;
    }

    public boolean isReliquatEncours() {
        return reliquatEncours;
    }

    public void setReliquatEncours(boolean reliquatEncours) {
        this.reliquatEncours = reliquatEncours;
    }

    public String getEntreeDate() {
        return entreeDate;
    }

    public void setEntreeDate(String entreeDate) {
        this.entreeDate = entreeDate;
    }

    public boolean isSelection() {
        return selection;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }

    public String getCommandeNumero() {
        return commandeNumero;
    }

    public void setCommandeNumero(String commandeNumero) {
        this.commandeNumero = commandeNumero;
    }

    public String getCommandeDate() {
        return commandeDate;
    }

    public void setCommandeDate(String commandeDate) {
        this.commandeDate = commandeDate;
    }

    public String getPeremptionDate() {
        return peremptionDate;
    }

    public void setPeremptionDate(String date) {
        peremptionDate = date;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getScanReference() {
        return scanReference;
    }

    public void setScanReference(String scanReference) {
        this.scanReference = scanReference;
    }

    public boolean isPeremptionActive() {
        return peremptionActive;
    }

    public void setPeremptionActive(boolean peremptionActive) {
        this.peremptionActive = peremptionActive;
    }

    public int getCommandeLigneID() {
        return commandeLigneID;
    }

    public void setCommandeLigneID(int commandeLigneID) {
        this.commandeLigneID = commandeLigneID;
    }

    public boolean isProduitGratuits() {
        return ProduitGratuits;
    }

    public void setProduitGratuits(boolean produitGratuits) {
        ProduitGratuits = produitGratuits;
    }

    public boolean isSuiviParLotActif() {
        return SuiviParLotActif;
    }

    public void setSuiviParLotActif(boolean suiviParLotActif) {
        SuiviParLotActif = suiviParLotActif;
    }

    public int getTech_UID() {
        return tech_UID;
    }

    public void setTech_UID(int tech_UID) {
        this.tech_UID = tech_UID;
    }

    public String getIPP() {
        return this.IPP;
    }

    public void setIPP(String IPP) {
        this.IPP = IPP;
    }

    public boolean isSuiviParSerieActif() {
        return SuiviParSerieActif;
    }

    public void setSuiviParSerieActif(boolean suiviParSerieActif) {
        SuiviParSerieActif = suiviParSerieActif;
    }

    public boolean isSerialiserReception() {
        return SerialiserReception;
    }

    public void setSerialiserReception(boolean serialiserReception) {
        SerialiserReception = serialiserReception;
    }

    public String getBL_Numero() {
        return BL_Numero;
    }

    public void setBL_Numero(String BL_Numero) {
        this.BL_Numero = BL_Numero;
    }

    public String getSerie() {
        return Serie;
    }

    public void setSerie(String serie) {
        Serie = serie;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Reliquat_UID", Reliquat_UID);
            jsonObject.put("ProduitID", ProduitID);
            jsonObject.put("Produit_Reference", Produit_Reference);
            jsonObject.put("PU_commande", PU_commande);
            jsonObject.put("designationCourte", designationCourte);
            jsonObject.put("unite", unite);
            jsonObject.put("fournisseurNom", fournisseurNom);
            jsonObject.put("FournisseurID", FournisseurID);
            jsonObject.put("QteCommande", QteCommande);
            jsonObject.put("QteLivraison", QteLivraison);
            jsonObject.put("QteReliquat_X", QteReliquat_X);
            jsonObject.put("reliquatEncours", reliquatEncours);
            jsonObject.put("entreeDate", entreeDate);
            jsonObject.put("selection", selection);
            jsonObject.put("commandeNumero", commandeNumero);
            jsonObject.put("commandeDate", commandeDate);
            jsonObject.put("ConditionnementAchat", ConditionnementAchat);
            jsonObject.put("ConditionnementDistribution", ConditionnementDistribution);
            jsonObject.put("peremptionDate", peremptionDate);
            jsonObject.put("lot", lot);
            jsonObject.put("scanReference", scanReference);
            jsonObject.put("peremptionActive", peremptionActive);
            jsonObject.put("commandeLigneID", commandeLigneID);
            jsonObject.put("Pu_facture", Pu_facture);
            jsonObject.put("Repris", Repris);
            jsonObject.put("QteMouvement", QteMouvement);
            jsonObject.put("QteReliquat_Y", QteReliquat_Y);
            jsonObject.put("ProduitGratuits", ProduitGratuits);
            jsonObject.put("Devise", Devise);
            jsonObject.put("DepotReference", DepotReference);
            jsonObject.put("_SYS_DT_MAJ", _SYS_DT_MAJ);
            jsonObject.put("_SYS_HEURE_MAJ", _SYS_HEURE_MAJ);
            jsonObject.put("_SYS_USER_MAJ", _SYS_USER_MAJ);
            jsonObject.put("SuiviParLotActif", SuiviParLotActif);
            jsonObject.put("SynchroTimeStamp", SynchroTimeStamp);
            jsonObject.put("SynchroStatut", SynchroStatut);
            jsonObject.put("Finness", Finness);
            jsonObject.put("ScanValue", ScanValue);
            jsonObject.put("PatientIPP", PatientIPP);
            jsonObject.put("PatientNom", PatientNom);
            jsonObject.put("tech_UID", tech_UID);
            jsonObject.put("Zone", Zone);
            jsonObject.put("Emplacement", Emplacement);
            jsonObject.put("IPP", IPP);
            jsonObject.put("SuiviParSerieActif", SuiviParSerieActif);
            jsonObject.put("SerialiserReception", SerialiserReception);
            jsonObject.put("Serie", Serie);
            jsonObject.put("BL_Numero", BL_Numero);

        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        PH_Reliquat ph_reliquat = (PH_Reliquat) obj;

        if (this.getPhiMR4UUID() == ph_reliquat.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getReliquat_UID() > ph_reliquat.getReliquat_UID() ? 1 : -1;
        }
    }
}
