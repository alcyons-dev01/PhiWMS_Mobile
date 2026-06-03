package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Produit_IdentificationOpenHelper;

public class Produit_Identification implements Serializable, Comparable {

    private int codeProduit;
    private String identification;
    private String typeCode;
    private String natureIdentification;
    private int etablissementUID;
    private int idPhiWMS;
    private int phiwms_mobileUUID = -1;

    public Produit_Identification(int codeProduit, String identification, String typeCode, String natureIdentification, int etablissementUID, int phiwms_mobileUUID) {
        this.codeProduit = codeProduit;
        this.identification = identification;
        this.typeCode = typeCode;
        this.natureIdentification = natureIdentification;
        this.etablissementUID = etablissementUID;
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public Produit_Identification(JSONObject produitIdentificationJson) {
        this.codeProduit = produitIdentificationJson.optInt("codeProduit");
        this.identification = produitIdentificationJson.optString("identification");
        this.typeCode = produitIdentificationJson.optString("typeCode");
        this.natureIdentification = produitIdentificationJson.optString("natureIdentification");
        this.etablissementUID = produitIdentificationJson.optInt("Etablissement_UID");
        this.idPhiWMS = produitIdentificationJson.optInt("idPhiWMS");
    }

    public Produit_Identification(Cursor cursor) {
        this.codeProduit = cursor.getInt(Produit_IdentificationOpenHelper.Constantes.NUM_COL_CODE_PRODUIT);
        this.identification = cursor.getString(Produit_IdentificationOpenHelper.Constantes.NUM_COL_IDENTIFICATION);
        this.typeCode = cursor.getString(Produit_IdentificationOpenHelper.Constantes.NUM_COL_TYPE_CODE);
        this.natureIdentification = cursor.getString(Produit_IdentificationOpenHelper.Constantes.NUM_COL_NATURE_IDENTIFICATION);
        this.etablissementUID = cursor.getInt(Produit_IdentificationOpenHelper.Constantes.NUM_COL_ETABLISSEMENT_UID);
        this.idPhiWMS = cursor.getInt(Produit_IdentificationOpenHelper.Constantes.NUM_COL_ID_PHIWMS);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getCodeProduit() {
        return codeProduit;
    }

    public void setCodeProduit(int codeProduit) {
        this.codeProduit = codeProduit;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getNatureIdentification() {
        return natureIdentification;
    }

    public void setNatureIdentification(String natureIdentification) {
        this.natureIdentification = natureIdentification;
    }

    public int getEtablissementUID() {
        return etablissementUID;
    }

    public void setEtablissementUID(int etablissementUID) {
        this.etablissementUID = etablissementUID;
    }

    public int getPhiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setPhiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getIdPhiWMS() {
        return idPhiWMS;
    }

    public void setIdPhiWMS(int idPhiWMS) {
        this.idPhiWMS = idPhiWMS;
    }

    public JSONObject toJson() {
        JSONObject produitPlaceJson = new JSONObject();

        try {
            produitPlaceJson.put("codeProduit", codeProduit);
            produitPlaceJson.put("identification", identification);
            produitPlaceJson.put("typeCode", typeCode);
            produitPlaceJson.put("natureIdentification", natureIdentification);
            produitPlaceJson.put("Etablissement_UID", etablissementUID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return produitPlaceJson;
    }


    @Override
    public boolean equals(Object obj) {

        return obj == this;
    }

    @Override
    public int compareTo(Object obj) {
        Produit_Identification IdentificationReference = (Produit_Identification) obj;

        if (this.getPhiwms_mobileUUID() == IdentificationReference.getPhiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getCodeProduit() > IdentificationReference.getCodeProduit() ? 1 : -1;
        }
    }

    @Override
    public String toString() {
        return this.getIdentification();
    }
}
