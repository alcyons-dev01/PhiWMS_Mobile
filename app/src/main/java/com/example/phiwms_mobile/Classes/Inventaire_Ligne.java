package com.example.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import com.example.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.Inventaire_LigneOpenHelper;
import com.example.phiwms_mobile.Outils.OutilsGestionClasses;

import static com.example.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Inventaire_Ligne implements Serializable, Comparable {

    private int ProduitID;
    private String Produit_Reference;
    private String Fournisseur;
    private String Categorie;
    private String Designation;
    private double stock_theorique;
    private double stock_Physique;
    private String depotReference;
    private String _SYS_DT_MAJ;
    private String _SYS_HEURE_MAJ;
    private String _SYS_USER_MAJ;
    private String Zone;
    private int ID_Inv;
    private Boolean _NePasImprimer;
    private double PUHT;
    private double tvaTx;
    private Boolean suspendu;
    private double valeur_TTC;
    private double ecart;
    private String unite;
    private double Conditionnement_Achat;
    private int ID;
    private int _UID;
    private String Classe;
    private int phiMR4UUID = -1;

    public Inventaire_Ligne(int produitID, String produit_Reference, String fournisseur, String categorie, String designation, double stock_theorique, double stock_Physique, String depotReference, String _SYS_DT_MAJ, String _SYS_HEURE_MAJ, String _SYS_USER_MAJ, String zone, int ID_Inv, Boolean _NePasImprimer, double PUHT, double tvaTx, Boolean suspendu, double valeur_TTC, double ecart, String unite, double conditionnement_Achat, int ID, int _UID, String classe) {
        this.ProduitID = produitID;
        this.Produit_Reference = produit_Reference;
        this.Fournisseur = fournisseur;
        this.Categorie = categorie;
        this.Designation = designation;
        this.stock_theorique = stock_theorique;
        this.stock_Physique = stock_Physique;
        this.depotReference = depotReference;
        this._SYS_DT_MAJ = _SYS_DT_MAJ;
        this._SYS_HEURE_MAJ = _SYS_HEURE_MAJ;
        this._SYS_USER_MAJ = _SYS_USER_MAJ;
        this.Zone = zone;
        this.ID_Inv = ID_Inv;
        this._NePasImprimer = _NePasImprimer;
        this.PUHT = PUHT;
        this.tvaTx = tvaTx;
        this.suspendu = suspendu;
        this.valeur_TTC = valeur_TTC;
        this.ecart = ecart;
        this.unite = unite;
        this.Conditionnement_Achat = conditionnement_Achat;
        this.ID = ID;
        this._UID = _UID;
        this.Classe = classe;
    }

    public Inventaire_Ligne(int ID_Inv, int produitID, String produit_Reference, String fournisseur, String categorie, String designation, double stock_Physique, String depotReference, String unite) {
        this.ID_Inv = ID_Inv;
        this.ProduitID = produitID;
        this.Produit_Reference = produit_Reference;
        this.Fournisseur = fournisseur;
        this.Categorie = categorie;
        this.Designation = designation;
        this.stock_Physique = stock_Physique;
        this.depotReference = depotReference;
        this.unite = unite;
    }

    public Inventaire_Ligne(JSONObject jsonObject) {
        try {
            this.ProduitID = jsonObject.getInt("ProduitID");
            this.Produit_Reference = OutilsGestionClasses.recupererString(jsonObject.getString("Produit_Reference"));
            this.Fournisseur = OutilsGestionClasses.recupererString(jsonObject.getString("Fournisseur"));
            this.Categorie = OutilsGestionClasses.recupererString(jsonObject.getString("Categorie"));
            this.Designation = OutilsGestionClasses.recupererString(jsonObject.getString("Designation"));
            this.stock_theorique = jsonObject.getDouble("stock_theorique");
            this.stock_Physique = jsonObject.getDouble("stock_Physique");
            this.depotReference = OutilsGestionClasses.recupererString(jsonObject.getString("depotReference"));
            this._SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("_SYS_DT_MAJ"));
            this._SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("_SYS_HEURE_MAJ"));
            this._SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("_SYS_USER_MAJ"));
            this.Zone = OutilsGestionClasses.recupererString(jsonObject.getString("Zone"));
            this.ID_Inv = jsonObject.getInt("ID_Inv");
            this._NePasImprimer = OutilsGestionClasses.recupererBooleen(jsonObject, "_NePasImprimer");
            this.PUHT = jsonObject.getDouble("PUHT");
            this.tvaTx = jsonObject.getDouble("tvaTx");
            this.suspendu = OutilsGestionClasses.recupererBooleen(jsonObject, "suspendu");
            this.valeur_TTC = jsonObject.getDouble("valeur_TTC");
            this.ecart = jsonObject.getDouble("ecart");
            this.unite = OutilsGestionClasses.recupererString(jsonObject.getString("unite"));
            this.Conditionnement_Achat = jsonObject.getDouble("Conditionnement_Achat");
            this.ID = jsonObject.getInt("ID");
            this._UID = jsonObject.getInt("_UID");
            this.Classe = OutilsGestionClasses.recupererString(jsonObject.getString("Classe"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Inventaire_Ligne(Cursor cursor) {
        this.ProduitID = cursor.getInt(Inventaire_LigneOpenHelper.Constantes.NUM_COL_PRODUITID_INVENTAIRE_LIGNE);
        this.Produit_Reference = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_REFERENCE_INVENTAIRE_LIGNE);
        this.Fournisseur = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL_FOURNISSEUR_INVENTAIRE_LIGNE);
        this.Categorie = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL_CATEGORIE_INVENTAIRE_LIGNE);
        this.Designation = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL_DESIGNATION_INVENTAIRE_LIGNE);
        this.stock_theorique = cursor.getDouble(Inventaire_LigneOpenHelper.Constantes.NUM_COL_STOCK_THEORIQUE_INVENTAIRE_LIGNE);
        this.stock_Physique = cursor.getDouble(Inventaire_LigneOpenHelper.Constantes.NUM_COL_STOCK_PHYSIQUE_INVENTAIRE_LIGNE);
        this.depotReference = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE);
        this._SYS_DT_MAJ = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE);
        this._SYS_HEURE_MAJ = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE);
        this._SYS_USER_MAJ = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE);
        this.Zone = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL_ZONE_INVENTAIRE_LIGNE);
        this.ID_Inv = cursor.getInt(Inventaire_LigneOpenHelper.Constantes.NUM_COL_ID_INV_INVENTAIRE_LIGNE);
        this._NePasImprimer = recupererBooleen(cursor, Inventaire_LigneOpenHelper.Constantes.NUM_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE);
        this.PUHT = cursor.getDouble(Inventaire_LigneOpenHelper.Constantes.NUM_COL_PUHT_INVENTAIRE_LIGNE);
        this.tvaTx = cursor.getDouble(Inventaire_LigneOpenHelper.Constantes.NUM_COL_TVATX_INVENTAIRE_LIGNE);
        this.suspendu = recupererBooleen(cursor, Inventaire_LigneOpenHelper.Constantes.NUM_COL_SUSPENDU_INVENTAIRE_LIGNE);
        this.valeur_TTC = cursor.getDouble(Inventaire_LigneOpenHelper.Constantes.NUM_COL_VALEUR_TTC_INVENTAIRE_LIGNE);
        this.ecart = cursor.getDouble(Inventaire_LigneOpenHelper.Constantes.NUM_COL_ECART_INVENTAIRE_LIGNE);
        this.unite = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL_UNITE_INVENTAIRE_LIGNE);
        this.Conditionnement_Achat = cursor.getDouble(Inventaire_LigneOpenHelper.Constantes.NUM_COL_CONDITIONNEMENT_ACHAT_INVENTAIRE_LIGNE);
        this.ID = cursor.getInt(Inventaire_LigneOpenHelper.Constantes.NUM_COL_ID_INVENTAIRE_LIGNE);
        this._UID = cursor.getInt(Inventaire_LigneOpenHelper.Constantes.NUM_COL__UID_INVENTAIRE_LIGNE);
        this.Classe = cursor.getString(Inventaire_LigneOpenHelper.Constantes.NUM_COL_CLASSE_INVENTAIRE_LIGNE);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
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

    public String getFournisseur() {
        return Fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        Fournisseur = fournisseur;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public double getStock_theorique() {
        return stock_theorique;
    }

    public void setStock_theorique(double stock_theorique) {
        this.stock_theorique = stock_theorique;
    }

    public double getStock_Physique() {
        return stock_Physique;
    }

    public void setStock_Physique(double stock_Physique) {
        this.stock_Physique = stock_Physique;
    }

    public String getDepotReference() {
        return depotReference;
    }

    public void setDepotReference(String depotReference) {
        this.depotReference = depotReference;
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

    public String getZone() {
        return Zone;
    }

    public void setZone(String zone) {
        Zone = zone;
    }

    public int getID_Inv() {
        return ID_Inv;
    }

    public void setID_Inv(int ID_Inv) {
        this.ID_Inv = ID_Inv;
    }

    public Boolean get_NePasImprimer() {
        return _NePasImprimer;
    }

    public void set_NePasImprimer(Boolean _NePasImprimer) {
        this._NePasImprimer = _NePasImprimer;
    }

    public double getPUHT() {
        return PUHT;
    }

    public void setPUHT(double PUHT) {
        this.PUHT = PUHT;
    }

    public double getTvaTx() {
        return tvaTx;
    }

    public void setTvaTx(double tvaTx) {
        this.tvaTx = tvaTx;
    }

    public Boolean getSuspendu() {
        return suspendu;
    }

    public void setSuspendu(Boolean suspendu) {
        this.suspendu = suspendu;
    }

    public double getValeur_TTC() {
        return valeur_TTC;
    }

    public void setValeur_TTC(double valeur_TTC) {
        this.valeur_TTC = valeur_TTC;
    }

    public double getEcart() {
        return ecart;
    }

    public void setEcart(double ecart) {
        this.ecart = ecart;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public double getConditionnement_Achat() {
        return Conditionnement_Achat;
    }

    public void setConditionnement_Achat(double conditionnement_Achat) {
        Conditionnement_Achat = conditionnement_Achat;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getClasse() {
        return Classe;
    }

    public void setClasse(String classe) {
        Classe = classe;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Inventaire_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Inventaire_Ligne inventaire_ligne = (Inventaire_Ligne) obj;

        if (this.getPhiMR4UUID() == inventaire_ligne.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > inventaire_ligne.get_UID() ? 1 : -1;
        }
    }
}
