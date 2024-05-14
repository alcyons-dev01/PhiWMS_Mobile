package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

public class Depot_Emplacement implements Serializable, Comparable {

    private int _UID = 0;
    private String Adressage;
    private String Hall;
    private String Paletier;
    private String Alveole;
    private String Niveau;
    private int ZoneID;
    private int DepotID;
    private String Depot_Reference;
    private String Code_GLN;
    private int phiwms_mobileUUID = -1;

    public Depot_Emplacement(String adressage, String hall, String paletier, String alveole, String niveau, int zoneID, int depotID, String depot_Reference, String Code_GLN) {
        this.Adressage = adressage;
        this.Hall = hall;
        this.Paletier = paletier;
        this.Alveole = alveole;
        this.Niveau = niveau;
        this.ZoneID = zoneID;
        this.DepotID = depotID;
        this.Depot_Reference = depot_Reference;
        this.Code_GLN = Code_GLN;
    }


    public Depot_Emplacement(JSONObject depotEmplacementJson) {
        try {
            this._UID = depotEmplacementJson.getInt("_UID");
            this.Adressage = OutilsGestionClasses.recupererString(depotEmplacementJson.getString("Adressage"));
            this.Hall = OutilsGestionClasses.recupererString(depotEmplacementJson.getString("Hall"));
            this.Paletier = OutilsGestionClasses.recupererString(depotEmplacementJson.getString("Paletier"));
            this.Alveole = OutilsGestionClasses.recupererString(depotEmplacementJson.getString("Alveole"));
            this.Niveau = OutilsGestionClasses.recupererString(depotEmplacementJson.getString("Niveau"));
            this.ZoneID = depotEmplacementJson.getInt("ZoneID");
            this.DepotID = depotEmplacementJson.getInt("DepotID");
            this.Depot_Reference = OutilsGestionClasses.recupererString(depotEmplacementJson.getString("Depot_Reference"));
            this.Code_GLN = OutilsGestionClasses.recupererString(depotEmplacementJson.getString("Code_GLN"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public Depot_Emplacement(Cursor cursor) {
        this._UID = cursor.getInt(EmplacementOpenHelper.Constantes.NUM_COL_UID_DEPOT_EMPLACEMENT);
        this.Adressage = cursor.getString(EmplacementOpenHelper.Constantes.NUM_COL_ADRESSAGE_DEPOT_EMPLACEMENT);
        this.Hall = cursor.getString(EmplacementOpenHelper.Constantes.NUM_COL_HALL_DEPOT_EMPLACEMENT);
        this.Paletier = cursor.getString(EmplacementOpenHelper.Constantes.NUM_COL_PALETIER_DEPOT_EMPLACEMENT);
        this.Alveole = cursor.getString(EmplacementOpenHelper.Constantes.NUM_COL_ALVEOLE_DEPOT_EMPLACEMENT);
        this.Niveau = cursor.getString(EmplacementOpenHelper.Constantes.NUM_COL_NIVEAU_DEPOT_EMPLACEMENT);
        this.ZoneID = cursor.getInt(EmplacementOpenHelper.Constantes.NUM_COL_ZONE_ID_DEPOT_EMPLACEMENT);
        this.DepotID = cursor.getInt(EmplacementOpenHelper.Constantes.NUM_COL_DEPOT_ID_DEPOT_EMPLACEMENT);
        this.Depot_Reference = cursor.getString(EmplacementOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_DEPOT_EMPLACEMENT);
        this.Depot_Reference = cursor.getString(EmplacementOpenHelper.Constantes.NUM_COL_CODE_GLN_DEPOT_EMPLACEMENT);
        this.Code_GLN = cursor.getString(EmplacementOpenHelper.Constantes.NUM_COL_CODE_GLN_DEPOT_EMPLACEMENT);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getAdressage() {
        return Adressage;
    }

    public void setAdressage(String adressage) {
        Adressage = adressage;
    }

    public String getHall() {
        return Hall;
    }

    public void setHall(String hall) {
        Hall = hall;
    }

    public String getPaletier() {
        return Paletier;
    }

    public void setPaletier(String paletier) {
        Paletier = paletier;
    }

    public String getAlveole() {
        return Alveole;
    }

    public void setAlveole(String alveole) {
        Alveole = alveole;
    }

    public String getNiveau() {
        return Niveau;
    }

    public void setNiveau(String niveau) {
        Niveau = niveau;
    }

    public int getZoneID() {
        return ZoneID;
    }

    public void setZoneID(int zoneID) {
        ZoneID = zoneID;
    }

    public int getDepotID() {
        return DepotID;
    }

    public void setDepotID(int depotID) {
        DepotID = depotID;
    }

    public String getDepot_Reference() {
        return Depot_Reference;
    }

    public void setDepot_Reference(String depot_Reference) {
        Depot_Reference = depot_Reference;
    }

    public String getCode_GLN() {
        return Code_GLN;
    }

    public void setCode_GLN(String code_GLN) {
        Code_GLN = code_GLN;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Depot_Emplacement)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Depot_Emplacement depot_emplacement = (Depot_Emplacement) obj;

        if (this.getPhiMR4UUID() == depot_emplacement.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > depot_emplacement.get_UID() ? 1 : -1;
        }
    }

    @Override
    public String toString() {
        return this.getAdressage();
    }
}
