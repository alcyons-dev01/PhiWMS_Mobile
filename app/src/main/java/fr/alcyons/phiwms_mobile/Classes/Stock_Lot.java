package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_LotOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Stock_Lot implements Serializable, Comparable {

    private int _UID;
    private String Ref_Depot;
    private int ID_Produit;
    private String Ref_Produit;
    private String Numero_Lot;
    private String Peremption_AAAAMM;
    private double Qte_Invent;
    private double Qte_Entree;
    private double Qte_Sortie;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private double Qte_Actuelle;
    private String Date_Creation;
    private String Date_Der_Entree;
    private String Date_Der_Sortie;
    private String Date_Inventaire;
    private double Qte_Av_Inventaire;
    private String rien;
    private String Peremption_date;
    private String Quarantaine;
    private int phiMR4UUID = -1;

    public Stock_Lot(int _UID, String ref_Depot, int ID_Produit, String ref_Produit, String numero_Lot, String peremption_AAAAMM, double qte_Invent, double qte_Entree, double qte_Sortie, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, double qte_Actuelle, String date_Creation, String date_Der_Entree, String date_Der_Sortie, String date_Inventaire, double qte_Av_Inventaire, String rien, String peremption_date, String quarantaine) {
        this._UID = _UID;
        this.Ref_Depot = ref_Depot;
        this.ID_Produit = ID_Produit;
        this.Ref_Produit = ref_Produit;
        this.Numero_Lot = numero_Lot;
        this.Peremption_AAAAMM = peremption_AAAAMM;
        this.Qte_Invent = qte_Invent;
        this.Qte_Entree = qte_Entree;
        this.Qte_Sortie = qte_Sortie;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.Qte_Actuelle = qte_Actuelle;
        this.Date_Creation = date_Creation;
        this.Date_Der_Entree = date_Der_Entree;
        this.Date_Der_Sortie = date_Der_Sortie;
        this.Date_Inventaire = date_Inventaire;
        this.Qte_Av_Inventaire = qte_Av_Inventaire;
        this.rien = rien;
        this.Peremption_date = peremption_date;
        this.Quarantaine = quarantaine;
    }

    public Stock_Lot(JSONObject jsonObject) {
        try {
            this._UID = jsonObject.getInt("_UID");
            this.Ref_Depot = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Depot"));
            this.ID_Produit = jsonObject.getInt("ID_Produit");
            this.Ref_Produit = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Produit"));
            this.Numero_Lot = OutilsGestionClasses.recupererString(jsonObject.getString("Numero_Lot"));
            this.Peremption_AAAAMM = OutilsGestionClasses.recupererString(jsonObject.getString("Peremption_AAAAMM"));
            this.Qte_Invent = jsonObject.getDouble("Qte_Invent");
            this.Qte_Entree = jsonObject.getDouble("Qte_Entree");
            this.Qte_Sortie = jsonObject.getDouble("Qte_Sortie");
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.Qte_Actuelle = jsonObject.getDouble("Qte_Actuelle");
            this.Date_Creation = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Creation"));
            this.Date_Der_Entree = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Der_Entree"));
            this.Date_Der_Sortie = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Der_Sortie"));
            this.Date_Inventaire = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Inventaire"));
            this.Qte_Av_Inventaire = jsonObject.getDouble("Qte_Av_Inventaire");
            this.rien = OutilsGestionClasses.recupererString(jsonObject.getString("rien"));
            this.Peremption_date = OutilsGestionClasses.recupererString(jsonObject.getString("Peremption_date"));
            this.Quarantaine = OutilsGestionClasses.recupererString(jsonObject.getString("Quarantaine"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Stock_Lot(Cursor cursor) {
        this._UID = cursor.getInt(Stock_LotOpenHelper.Constantes.NUM_COL__UID_STOCK_LOT);
        this.Ref_Depot = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_REF_DEPOT_STOCK_LOT);
        this.ID_Produit = cursor.getInt(Stock_LotOpenHelper.Constantes.NUM_COL_ID_PRODUIT_STOCK_LOT);
        this.Ref_Produit = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_REF_PRODUIT_STOCK_LOT);
        this.Numero_Lot = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_NUMERO_LOT_STOCK_LOT);
        this.Peremption_AAAAMM = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_PEREMPTION_AAAAMM_STOCK_LOT);
        this.Qte_Invent = cursor.getDouble(Stock_LotOpenHelper.Constantes.NUM_COL_QTE_INVENT_STOCK_LOT);
        this.Qte_Entree = cursor.getDouble(Stock_LotOpenHelper.Constantes.NUM_COL_QTE_ENTREE_STOCK_LOT);
        this.Qte_Sortie = cursor.getDouble(Stock_LotOpenHelper.Constantes.NUM_COL_QTE_SORTIE_STOCK_LOT);
        this.SYS_DT_MAJ = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_STOCK_LOT);
        this.SYS_HEURE_MAJ = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_STOCK_LOT);
        this.SYS_USER_MAJ = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_STOCK_LOT);
        this.Qte_Actuelle = cursor.getDouble(Stock_LotOpenHelper.Constantes.NUM_COL_QTE_ACTUELLE_STOCK_LOT);
        this.Date_Creation = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_DATE_CREATION_STOCK_LOT);
        this.Date_Der_Entree = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_DATE_DER_ENTREE_STOCK_LOT);
        this.Date_Der_Sortie = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_DATE_DER_SORTIE_STOCK_LOT);
        this.Date_Inventaire = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_DATE_INVENTAIRE_STOCK_LOT);
        this.Qte_Av_Inventaire = cursor.getDouble(Stock_LotOpenHelper.Constantes.NUM_COL_QTE_AV_INVENTAIRE_STOCK_LOT);
        this.rien = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_RIEN_STOCK_LOT);
        this.Peremption_date = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_PEREMPTION_DATE_STOCK_LOT);
        this.Quarantaine = cursor.getString(Stock_LotOpenHelper.Constantes.NUM_COL_QUARANTAINE_STOCK_LOT);
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

    public String getRef_Depot() {
        return Ref_Depot;
    }

    public void setRef_Depot(String ref_Depot) {
        Ref_Depot = ref_Depot;
    }

    public int getID_Produit() {
        return ID_Produit;
    }

    public void setID_Produit(int ID_Produit) {
        this.ID_Produit = ID_Produit;
    }

    public String getRef_Produit() {
        return Ref_Produit;
    }

    public void setRef_Produit(String ref_Produit) {
        Ref_Produit = ref_Produit;
    }

    public String getNumero_Lot() {
        return Numero_Lot;
    }

    public void setNumero_Lot(String numero_Lot) {
        Numero_Lot = numero_Lot;
    }

    public String getPeremption_AAAAMM() {
        return Peremption_AAAAMM;
    }

    public void setPeremption_AAAAMM(String peremption_AAAAMM) {
        Peremption_AAAAMM = peremption_AAAAMM;
    }

    public double getQte_Invent() {
        return Qte_Invent;
    }

    public void setQte_Invent(double qte_Invent) {
        Qte_Invent = qte_Invent;
    }

    public double getQte_Entree() {
        return Qte_Entree;
    }

    public void setQte_Entree(double qte_Entree) {
        Qte_Entree = qte_Entree;
    }

    public double getQte_Sortie() {
        return Qte_Sortie;
    }

    public void setQte_Sortie(double qte_Sortie) {
        Qte_Sortie = qte_Sortie;
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

    public double getQte_Actuelle() {
        return Qte_Actuelle;
    }

    public void setQte_Actuelle(double qte_Actuelle) {
        Qte_Actuelle = qte_Actuelle;
    }

    public String getDate_Creation() {
        return Date_Creation;
    }

    public void setDate_Creation(String date_Creation) {
        Date_Creation = date_Creation;
    }

    public String getDate_Der_Entree() {
        return Date_Der_Entree;
    }

    public void setDate_Der_Entree(String date_Der_Entree) {
        Date_Der_Entree = date_Der_Entree;
    }

    public String getDate_Der_Sortie() {
        return Date_Der_Sortie;
    }

    public void setDate_Der_Sortie(String date_Der_Sortie) {
        Date_Der_Sortie = date_Der_Sortie;
    }

    public String getDate_Inventaire() {
        return Date_Inventaire;
    }

    public void setDate_Inventaire(String date_Inventaire) {
        Date_Inventaire = date_Inventaire;
    }

    public double getQte_Av_Inventaire() {
        return Qte_Av_Inventaire;
    }

    public void setQte_Av_Inventaire(double qte_Av_Inventaire) {
        Qte_Av_Inventaire = qte_Av_Inventaire;
    }

    public String getRien() {
        return rien;
    }

    public void setRien(String rien) {
        this.rien = rien;
    }

    public String getPeremption_date() {
        return Peremption_date;
    }

    public void setPeremption_date(String peremption_date) {
        Peremption_date = peremption_date;
    }

    public String getQuarantaine() {
        return Quarantaine;
    }

    public void setQuarantaine(String quarantaine) {
        Quarantaine = quarantaine;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Stock_Lot)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Stock_Lot stock_lot = (Stock_Lot) obj;

        if (this.getPhiMR4UUID() == stock_lot.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > stock_lot.get_UID() ? 1 : -1;
        }
    }
}
