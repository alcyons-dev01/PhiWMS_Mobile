package com.example.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import com.example.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;

import static com.example.phiwms_mobile.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class Stock_Lot_Emplacement implements Serializable, Comparable {

    private String SYS;
    private int _UID;
    private int Produit_Code;
    private String Depot_Reference;
    private String Zone;
    private String Emplacement;
    private String Lot;
    private double Qte;
    private String peremptionDate;
    private String Produit_Designation;
    private String SYS_UniqueID;
    private int Qte_Deplacer;
    private String date_dernier_controle;
    private String Serie;
    private int phiMR4UUID = -1;


    public Stock_Lot_Emplacement(String SYS, int _UID, int produit_Code, String depot_Reference, String zone, String emplacement, String lot, double qte, String peremptionDate, String produit_Designation, String SYS_UniqueID, int qte_Deplacer, String date_dernier_controle) {
        this.SYS = SYS;
        this._UID = _UID;
        this.Produit_Code = produit_Code;
        this.Depot_Reference = depot_Reference;
        this.Zone = zone;
        this.Emplacement = emplacement;
        this.Lot = lot;
        this.Qte = qte;
        this.peremptionDate = peremptionDate;
        this.Produit_Designation = produit_Designation;
        this.SYS_UniqueID = SYS_UniqueID;
        this.Qte_Deplacer = qte_Deplacer;
        this.date_dernier_controle = date_dernier_controle;
    }

    public Stock_Lot_Emplacement(JSONObject jsonObject) {
        try {
            this.SYS = recupererString(jsonObject.getString("SYS"));
            this._UID = jsonObject.getInt("_UID");
            this.Produit_Code = jsonObject.getInt("Produit_Code");
            this.Depot_Reference = recupererString(jsonObject.getString("Depot_Reference"));
            this.Zone = recupererString(jsonObject.getString("Zone"));
            this.Emplacement = recupererString(jsonObject.getString("Emplacement"));
            this.Lot = recupererString(jsonObject.getString("Lot"));
            this.Qte = jsonObject.getDouble("Qte");
            this.peremptionDate = recupererString(jsonObject.getString("peremptionDate"));
            this.Produit_Designation = recupererString(jsonObject.getString("Produit_Designation"));
            this.SYS_UniqueID = recupererString(jsonObject.getString("SYS_UniqueID"));
            this.Qte_Deplacer = jsonObject.getInt("Qte_Deplacer");
            this.date_dernier_controle = recupererString(jsonObject.getString("date_dernier_controle"));
            this.Serie = recupererString(jsonObject.getString("Serie"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Stock_Lot_Emplacement(Cursor cursor) {
        this._UID = cursor.getInt(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL__UID_STOCK_LOT_EMPLACEMENT);
        this.Produit_Code = cursor.getInt(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT);
        this.Depot_Reference = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT);
        this.Zone = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_ZONE_STOCK_LOT_EMPLACEMENT);
        this.Emplacement = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_EMPLACEMENT_STOCK_LOT_EMPLACEMENT);
        this.Lot = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_LOT_STOCK_LOT_EMPLACEMENT);
        this.Qte = cursor.getDouble(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_QTE_STOCK_LOT_EMPLACEMENT);
        this.peremptionDate = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_PEREMPTIONDATE_STOCK_LOT_EMPLACEMENT);
        this.Serie = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_SERIE);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public String getSYS() {
        return SYS;
    }

    public void setSYS(String SYS) {
        this.SYS = SYS;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public int getProduit_Code() {
        return Produit_Code;
    }

    public void setProduit_Code(int produit_Code) {
        Produit_Code = produit_Code;
    }

    public String getDepot_Reference() {
        return Depot_Reference;
    }

    public void setDepot_Reference(String depot_Reference) {
        Depot_Reference = depot_Reference;
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

    public String getLot() {
        return Lot;
    }

    public void setLot(String lot) {
        Lot = lot;
    }

    public double getQte() {
        return Qte;
    }

    public void setQte(double qte) {
        Qte = qte;
    }

    public String getPeremptionDate() {
        return peremptionDate;
    }

    public void setPeremptionDate(String peremptionDate) {
        this.peremptionDate = peremptionDate;
    }

    public String getProduit_Designation() {
        return Produit_Designation;
    }

    public void setProduit_Designation(String produit_Designation) {
        Produit_Designation = produit_Designation;
    }

    public String getSYS_UniqueID() {
        return SYS_UniqueID;
    }

    public void setSYS_UniqueID(String SYS_UniqueID) {
        this.SYS_UniqueID = SYS_UniqueID;
    }

    public int getQte_Deplacer() {
        return Qte_Deplacer;
    }

    public void setQte_Deplacer(int qte_Deplacer) {
        Qte_Deplacer = qte_Deplacer;
    }

    public String getDate_dernier_controle() {
        return date_dernier_controle;
    }

    public void setDate_dernier_controle(String date_dernier_controle) {
        this.date_dernier_controle = date_dernier_controle;
    }

    public String getSerie() {
        return Serie;
    }

    public void setSerie(String serie) {
        Serie = serie;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Stock_Lot_Emplacement)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Stock_Lot_Emplacement stock_lot_emplacement = (Stock_Lot_Emplacement) obj;

        if (this.getPhiMR4UUID() == stock_lot_emplacement.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > stock_lot_emplacement.get_UID() ? 1 : -1;
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("SYS", SYS);
            jsonObject.put("_UID", _UID);
            jsonObject.put("Produit_Code", Produit_Code);
            jsonObject.put("Depot_Reference", Depot_Reference);
            jsonObject.put("Zone", Zone);
            jsonObject.put("Emplacement", Emplacement);
            jsonObject.put("Lot", Lot);
            jsonObject.put("Qte", Qte);
            jsonObject.put("peremptionDate", peremptionDate);
            jsonObject.put("Produit_Designation", Produit_Designation);
            jsonObject.put("SYS_UniqueID", SYS_UniqueID);
            jsonObject.put("Qte_Deplacer", Qte_Deplacer);
            jsonObject.put("date_dernier_controle", date_dernier_controle);
            jsonObject.put("Serie", Serie);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }
}
