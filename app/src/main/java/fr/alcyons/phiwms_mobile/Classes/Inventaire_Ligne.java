package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

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
    private int phiwms_mobileUUID = -1;

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
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
