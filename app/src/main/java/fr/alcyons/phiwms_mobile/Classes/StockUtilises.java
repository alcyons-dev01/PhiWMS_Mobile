package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockUtilisesOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;

public class StockUtilises {

    public String numeroDocument;
    public int idProduit;
    public int stockId;

    public String numeroLot;
    public String datePeremption;
    public int depotId;
    public String zone;
    public String emplacement;
    public int quantite;
    public int userId;
    public String dateCreation;
    public int phiwms_mobileUUID;
    public int etablissementID;

    public StockUtilises()
    {
        this.numeroDocument = "";
        this.idProduit = 0;
        this.stockId = 0;
        this.numeroLot = "";
        this.datePeremption = "";
        this.depotId = 0;
        this.zone = "";
        this.emplacement = "";
        this.quantite = 0;
        this.userId = 0;
        this.dateCreation = "";
        this.etablissementID = 0;
    }

    public StockUtilises(String numeroDocument, int idProduit, int stockId, String numeroLot, String datePeremption, int depotId, String zone, String emplacement, int quantite, int userId,  String dateCreation, int etablissementID)
    {
        this.numeroDocument = numeroDocument;
        this.idProduit = idProduit;
        this.stockId = stockId;
        this.numeroLot = numeroLot;
        this.datePeremption = datePeremption;
        this.depotId = depotId;
        this.zone = zone;
        this.emplacement = emplacement;
        this.quantite = quantite;
        this.userId = userId;
        this.dateCreation = dateCreation;
        this.etablissementID = etablissementID;
    }

    public StockUtilises(JSONObject stockUtiliseJson) {
        this.numeroDocument = stockUtiliseJson.optString("NumeroDocument");
        this.idProduit = stockUtiliseJson.optInt("IdProduit");
        this.stockId = stockUtiliseJson.optInt("Stock_UID");
        this.numeroLot = stockUtiliseJson.optString("NumeroLot");
        this.datePeremption = stockUtiliseJson.optString("DatePeremption");
        this.depotId = stockUtiliseJson.optInt("DepotId");
        this.zone = stockUtiliseJson.optString("Zone");
        this.emplacement = stockUtiliseJson.optString("Emplacement");
        this.quantite = stockUtiliseJson.optInt("Quantite");
        this.userId = stockUtiliseJson.optInt("UserId");
        this.dateCreation = stockUtiliseJson.optString("DateCreation");
    }

    public StockUtilises(Cursor stockUtiliseCursor) {
        this.numeroDocument = stockUtiliseCursor.getString(StockUtilisesOpenHelper.Constantes.NUM_COL_NUMERO_DOCUMENT);
        this.idProduit = stockUtiliseCursor.getInt(StockUtilisesOpenHelper.Constantes.NUM_COL_ID_PRODUIT);
        this.stockId = stockUtiliseCursor.getInt(StockUtilisesOpenHelper.Constantes.NUM_COL_ID_STOCK);
        this.numeroLot = stockUtiliseCursor.getString(StockUtilisesOpenHelper.Constantes.NUM_COL_NUM_LOT);
        this.datePeremption = stockUtiliseCursor.getString(StockUtilisesOpenHelper.Constantes.NUM_COL_DATE_PEREMPTION);
        this.depotId = stockUtiliseCursor.getInt(StockUtilisesOpenHelper.Constantes.NUM_COL_DEPOT_ID);
        this.zone = stockUtiliseCursor.getString(StockUtilisesOpenHelper.Constantes.NUM_COL_ZONE);
        this.emplacement = stockUtiliseCursor.getString(StockUtilisesOpenHelper.Constantes.NUM_COL_EMPLACEMENT);
        this.quantite = stockUtiliseCursor.getInt(StockUtilisesOpenHelper.Constantes.NUM_COL_QUANTITE);
        this.userId = stockUtiliseCursor.getInt(StockUtilisesOpenHelper.Constantes.NUM_COL_USER_ID);
        this.dateCreation = stockUtiliseCursor.getString(StockUtilisesOpenHelper.Constantes.NUM_COL_DATE_CREATION);
        this.etablissementID = stockUtiliseCursor.getInt(StockUtilisesOpenHelper.Constantes.NUM_COL_ETABLISSEMENT_ID);
        this.phiwms_mobileUUID = stockUtiliseCursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public String getNumeroDocument() {
        return numeroDocument;
    }

    public void setNumeroDocument(String numeroDocument) {
        this.numeroDocument = numeroDocument;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public String getNumeroLot() {
        return numeroLot;
    }

    public void setNumeroLot(String numeroLot) {
        this.numeroLot = numeroLot;
    }

    public String getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(String datePeremption) {
        this.datePeremption = datePeremption;
    }

    public int getDepotId() {
        return depotId;
    }

    public void setDepotId(int depotId) {
        this.depotId = depotId;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }
    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getEtablissementID() {
        return etablissementID;
    }

    public void setEtablissementID(int etablissementID) {
        this.etablissementID = etablissementID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("numeroDocument", numeroDocument);
            jsonObject.put("idProduit", idProduit);
            jsonObject.put("stockId", stockId);
            jsonObject.put("numeroLot", numeroLot);
            jsonObject.put("datePeremption", datePeremption);
            jsonObject.put("depotId", depotId);
            jsonObject.put("zone", zone);
            jsonObject.put("emplacement", emplacement);
            jsonObject.put("quantite", quantite);
            jsonObject.put("userId", userId);
            jsonObject.put("dateCreation", dateCreation);
            jsonObject.put("Etablissement_UID", etablissementID);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }
}
