package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

public class PH_Lot_Ligne implements Serializable, Comparable {

    private int UID;
    private int docLigneId;
    private String numLot;
    private String datePeremption;
    private int quantite;
    private String numSerie;
    private boolean verrouiller;
    private int phiwms_mobileUUID = -1;


    public PH_Lot_Ligne(JSONObject jsonObject) {
        this.UID = jsonObject.optInt("UID");
        this.docLigneId = jsonObject.optInt("DocLigneId");
        this.numLot = jsonObject.optString("NumLot");
        this.datePeremption = jsonObject.optString("DatePeremption");
        this.quantite = jsonObject.optInt("Quantite");
        this.numSerie = jsonObject.optString("NumSerie");
        this.verrouiller = true;
    }

    public PH_Lot_Ligne(Cursor cursor) {
        this.UID = cursor.getInt(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_UID);
        this.docLigneId = cursor.getInt(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_NUM_DOC);
        this.numLot = cursor.getString(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_NUM_LOT);
        this.datePeremption = cursor.getString(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_DATE_PEREMPTION);
        this.quantite = cursor.getInt(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_QUANTITE);
        this.numSerie = cursor.getString(PH_Lot_LigneOpenHelper.Constantes.NUM_COL_NUM_SERIE);
        this.verrouiller = OutilsGestionClasses.recupererBooleen(cursor, PH_Lot_LigneOpenHelper.Constantes.NUM_COL_NUM_VERROUILLER);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public int getdocLigneId() {
        return docLigneId;
    }

    public void setdocLigneId(int docLigneId) {
        this.docLigneId = docLigneId;
    }

    public String getNumLot() {
        return numLot;
    }

    public void setNumLot(String numLot) {
        this.numLot = numLot;
    }

    public String getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(String datePeremption) {
        this.datePeremption = datePeremption;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getDocLigneId() {
        return docLigneId;
    }

    public void setDocLigneId(int docLigneId) {
        this.docLigneId = docLigneId;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public boolean isVerrouiller() {
        return verrouiller;
    }

    public void setVerrouiller(boolean verrouiller) {
        this.verrouiller = verrouiller;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_Lot_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        PH_Lot_Ligne ph_lot_ligne = (PH_Lot_Ligne) obj;

        if (this.getPhiMR4UUID() == ph_lot_ligne.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getUID() > ph_lot_ligne.getUID() ? 1 : -1;
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UID", UID);
            jsonObject.put("docLigneId", docLigneId);
            jsonObject.put("numLot", numLot);
            jsonObject.put("datePeremption", datePeremption);
            jsonObject.put("quantite", quantite);
            jsonObject.put("numSerie", numSerie);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }
}
