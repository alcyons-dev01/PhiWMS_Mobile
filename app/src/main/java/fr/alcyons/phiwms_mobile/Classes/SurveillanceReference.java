package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by olivier on 26/02/2019.
 */

public class SurveillanceReference {

    private int _UID;
    private String surveillanceDate;
    private String surveillanceHeure;
    private int produitID;
    private int serialisationID;
    private String motif;
    private String actionAMener;
    private String statut;
    private String traitePar;
    private String traiteDate;
    private String traiteHeure;
    private String produitLot;
    private String produitDatePéremption;
    private String produitNumeroSerie;
    private int serialexpressUUID = -1;

    public SurveillanceReference(int _UID, String surveillanceDate, String surveillanceHeure, int produitID, int serialisationID, String motif, String actionAMener, String statut, String traitePar, String traiteDate, String traiteHeure, String produitLot, String produitDatePéremption, String produitNumeroSerie) {
        this._UID = _UID;
        this.surveillanceDate = surveillanceDate;
        this.surveillanceHeure = surveillanceHeure;
        this.produitID = produitID;
        this.serialisationID = serialisationID;
        this.motif = motif;
        this.actionAMener = actionAMener;
        this.statut = statut;
        this.traitePar = traitePar;
        this.traiteDate = traiteDate;
        this.traiteHeure = traiteHeure;
        this.produitLot = produitLot;
        this.produitDatePéremption = produitDatePéremption;
        this.produitNumeroSerie = produitNumeroSerie;
    }

    public SurveillanceReference(JSONObject jsonObject) {
        try {
            _UID = jsonObject.getInt("_UID");
            surveillanceDate = OutilsGestionClasses.recupererString(jsonObject.getString("surveillanceDate"));
            surveillanceHeure = OutilsGestionClasses.recupererString(jsonObject.getString("surveillanceHeure"));
            produitID = jsonObject.getInt("produitID");
            serialisationID = jsonObject.getInt("serialisationID");
            motif = OutilsGestionClasses.recupererString(jsonObject.getString("motif"));
            actionAMener = OutilsGestionClasses.recupererString(jsonObject.getString("actionAMener"));
            statut = OutilsGestionClasses.recupererString(jsonObject.getString("statut"));
            traitePar = OutilsGestionClasses.recupererString(jsonObject.getString("traitePar"));
            traiteDate = OutilsGestionClasses.recupererString(jsonObject.getString("traiteDate"));
            traiteHeure = OutilsGestionClasses.recupererString(jsonObject.getString("traiteHeure"));
            produitLot = OutilsGestionClasses.recupererString(jsonObject.getString("produitLot"));
            produitDatePéremption = OutilsGestionClasses.recupererString(jsonObject.getString("produitDatePéremption"));
            produitNumeroSerie = OutilsGestionClasses.recupererString(jsonObject.getString("produitNumeroSerie"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", _UID);
            jsonObject.put("surveillanceDate", surveillanceDate);
            jsonObject.put("surveillanceHeure", surveillanceHeure);
            jsonObject.put("produitID", produitID);
            jsonObject.put("serialisationID", serialisationID);
            jsonObject.put("motif", motif);
            jsonObject.put("actionAMener", actionAMener);
            jsonObject.put("statut", statut);
            jsonObject.put("traitePar", traitePar);
            jsonObject.put("traiteDate", traiteDate);
            jsonObject.put("traiteHeure", traiteHeure);
            jsonObject.put("produitLot", produitLot);
            jsonObject.put("produitDatePéremption", produitDatePéremption);
            jsonObject.put("produitNumeroSerie", produitNumeroSerie);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getSurveillanceDate() {
        return surveillanceDate;
    }

    public void setSurveillanceDate(String surveillanceDate) {
        this.surveillanceDate = surveillanceDate;
    }

    public String getSurveillanceHeure() {
        return surveillanceHeure;
    }

    public void setSurveillanceHeure(String surveillanceHeure) {
        this.surveillanceHeure = surveillanceHeure;
    }

    public int getProduitID() {
        return produitID;
    }

    public void setProduitID(int produitID) {
        this.produitID = produitID;
    }

    public int getSerialisationID() {
        return serialisationID;
    }

    public void setSerialisationID(int serialisationID) {
        this.serialisationID = serialisationID;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getActionAMener() {
        return actionAMener;
    }

    public void setActionAMener(String actionAMener) {
        this.actionAMener = actionAMener;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getTraitePar() {
        return traitePar;
    }

    public void setTraitePar(String traitePar) {
        this.traitePar = traitePar;
    }

    public String getTraiteDate() {
        return traiteDate;
    }

    public void setTraiteDate(String traiteDate) {
        this.traiteDate = traiteDate;
    }

    public String getTraiteHeure() {
        return traiteHeure;
    }

    public void setTraiteHeure(String traiteHeure) {
        this.traiteHeure = traiteHeure;
    }

    public String getProduitLot() {
        return produitLot;
    }

    public void setProduitLot(String produitLot) {
        this.produitLot = produitLot;
    }

    public String getProduitDatePéremption() {
        return produitDatePéremption;
    }

    public void setProduitDatePéremption(String produitDatePéremption) {
        this.produitDatePéremption = produitDatePéremption;
    }

    public String getProduitNumeroSerie() {
        return produitNumeroSerie;
    }

    public void setProduitNumeroSerie(String produitNumeroSerie) {
        this.produitNumeroSerie = produitNumeroSerie;
    }

    public int getSerialexpressUUID() {
        return serialexpressUUID;
    }

    public void setSerialexpressUUID(int serialexpressUUID) {
        this.serialexpressUUID = serialexpressUUID;
    }
}
