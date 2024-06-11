package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

public class Stock_Lot_Emplacement_Light implements Comparable {
    private double Qte;
    private String Lot;
    private String peremptionDate;
    private String Emplacement;
    private String Depot_Reference;
    private String Zone;
    private int Produit_Code;
    private int Qte_Preparer = 0;
    private String Serie;
    private int _UID = 0;
    private int phiwms_mobileUUID = -1;

    public Stock_Lot_Emplacement_Light(JSONObject jsonObject) {
        this._UID = jsonObject.optInt("_UID");
        this.Produit_Code = jsonObject.optInt("Produit_Code");
        this.Depot_Reference = jsonObject.optString("Depot_Reference");
        this.Zone = jsonObject.optString("Zone");
        this.Emplacement = jsonObject.optString("Emplacement");
        this.Lot = jsonObject.optString("Lot");
        this.Qte = jsonObject.optDouble("Qte");
        this.peremptionDate = jsonObject.optString("peremptionDate");
        this.Serie = jsonObject.optString("Serie");
    }

    public Stock_Lot_Emplacement_Light(double qte, String lot, String peremptionDate, String emplacement, String depot_Reference, String zone, int produit_Code, int qtePrep, String numSerie) {
        this.Qte = qte;
        this.Lot = lot;
        this.peremptionDate = peremptionDate;
        this.Emplacement = emplacement;
        this.Depot_Reference = depot_Reference;
        this.Zone = zone;
        this.Produit_Code = produit_Code;
        this.Qte_Preparer = qtePrep;
        this.Serie = numSerie;
    }

    public Stock_Lot_Emplacement_Light(Cursor cursor) {
        this._UID = cursor.getInt(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL__UID_STOCK_LOT_EMPLACEMENT);
        this.Qte = cursor.getInt(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_QTE_STOCK_LOT_EMPLACEMENT);
        this.Lot = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_LOT_STOCK_LOT_EMPLACEMENT);
        this.peremptionDate = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_PEREMPTIONDATE_STOCK_LOT_EMPLACEMENT);
        this.Emplacement = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_EMPLACEMENT_STOCK_LOT_EMPLACEMENT);
        this.Depot_Reference = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT);
        this.Zone = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_ZONE_STOCK_LOT_EMPLACEMENT);
        this.Produit_Code = cursor.getInt(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT);
        this.Qte_Preparer = cursor.getInt(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_QTE_PREPARER_STOCK_LOT_EMPLACEMENT);
        this.Serie = cursor.getString(Stock_Lot_EmplacementLightOpenHelper.Constantes.NUM_COL_SERIE);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getQte_Preparer() {
        return Qte_Preparer;
    }

    public void setQte_Preparer(int qte_Preparer) {
        Qte_Preparer = qte_Preparer;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public double getQte() {
        return Qte;
    }

    public void setQte(double qte) {
        Qte = qte;
    }

    public String getLot() {
        return Lot;
    }

    public void setLot(String lot) {
        Lot = lot;
    }

    public String getPeremptionDate() {
        return peremptionDate;
    }

    public void setPeremptionDate(String peremptionDate) {
        this.peremptionDate = peremptionDate;
    }

    public String getEmplacement() {
        return Emplacement;
    }

    public void setEmplacement(String emplacement) {
        Emplacement = emplacement;
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

    public int getProduit_Code() {
        return Produit_Code;
    }

    public void setProduit_Code(int produit_Code) {
        Produit_Code = produit_Code;
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
            jsonObject.put("_UID", _UID);
            jsonObject.put("Produit_Code", Produit_Code);
            jsonObject.put("Depot_Reference", Depot_Reference);
            jsonObject.put("Zone", Zone);
            jsonObject.put("Emplacement", Emplacement);
            jsonObject.put("Lot", Lot);
            jsonObject.put("Qte", Qte);
            jsonObject.put("peremptionDate", peremptionDate);
            jsonObject.put("Serie", Serie);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public int compareTo(Object obj) {

        if (this.getPhiMR4UUID() == ((Stock_Lot_Emplacement_Light) obj).getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > ((Stock_Lot_Emplacement_Light) obj).get_UID() ? 1 : -1;
        }
    }
}
