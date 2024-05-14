package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;
public class Depot_Zone implements Serializable, Comparable {
    private int ZoneID;
    private String ZoneName;
    private double ZoneLongitude;
    private double ZoneLatitude;
    private String DataMatrixReference;
    private int DepotID;
    private String Conservation;
    private String Depot_Reference;
    private String Type_Emplacement;
    private List<Depot_Emplacement> emplacements;
    private int phiwms_mobileUUID = -1;

    public Depot_Zone(int id, String zoneName, double zoneLongitude, double zoneLatitude, String dataMatrixReference, int depotID, String conservation, String depot_Reference, String type_Emplacement) {
        this.ZoneID = id;
        this.ZoneName = zoneName;
        this.ZoneLongitude = zoneLongitude;
        this.ZoneLatitude = zoneLatitude;
        this.DataMatrixReference = dataMatrixReference;
        this.DepotID = depotID;
        this.Conservation = conservation;
        this.Depot_Reference = depot_Reference;
        this.Type_Emplacement = type_Emplacement;
        this.emplacements = new ArrayList<>();
    }

    public Depot_Zone(int id, String zoneName, double zoneLongitude, double zoneLatitude, String dataMatrixReference, int depotID, String conservation, String depot_Reference, String type_Emplacement, List<Depot_Emplacement> emplacements) {
        this.ZoneID = id;
        this.ZoneName = zoneName;
        this.ZoneLongitude = zoneLongitude;
        this.ZoneLatitude = zoneLatitude;
        this.DataMatrixReference = dataMatrixReference;
        this.DepotID = depotID;
        this.Conservation = conservation;
        this.Depot_Reference = depot_Reference;
        this.Type_Emplacement = type_Emplacement;
        this.setEmplacements(emplacements);
    }

    public Depot_Zone(JSONObject depotZoneJson) {
        try {
            this.ZoneID = depotZoneJson.getInt("ZoneID");
            this.ZoneName = OutilsGestionClasses.recupererString(depotZoneJson.getString("ZoneName"));
            this.ZoneLongitude = depotZoneJson.getDouble("ZoneLongitude");
            this.ZoneLatitude = depotZoneJson.getDouble("ZoneLatitude");
            this.DataMatrixReference = OutilsGestionClasses.recupererString(depotZoneJson.getString("DataMatrixReference"));
            this.DepotID = depotZoneJson.getInt("DepotID");
            this.Conservation = OutilsGestionClasses.recupererString(depotZoneJson.getString("Conservation"));
            this.Depot_Reference = OutilsGestionClasses.recupererString(depotZoneJson.getString("Depot_Reference"));
            this.Type_Emplacement = OutilsGestionClasses.recupererString(depotZoneJson.getString("Type_Emplacement"));
            this.emplacements = new ArrayList<>();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Depot_Zone(Cursor cursorZones) {
        this.ZoneID = cursorZones.getInt(ZoneOpenHelper.Constantes.NUM_COL_ID_DEPOT_ZONE);
        this.ZoneName = cursorZones.getString(ZoneOpenHelper.Constantes.NUM_COL_NOM_DEPOT_ZONE);
        this.ZoneLongitude = cursorZones.getDouble(ZoneOpenHelper.Constantes.NUM_COL_LONGITUDE_DEPOT_ZONE);
        this.ZoneLatitude = cursorZones.getDouble(ZoneOpenHelper.Constantes.NUM_COL_LATITUDE_DEPOT_ZONE);
        this.DataMatrixReference = cursorZones.getString(ZoneOpenHelper.Constantes.NUM_COL_DATA_MATRIX_REFERENCE_DEPOT_ZONE);
        this.DepotID = cursorZones.getInt(ZoneOpenHelper.Constantes.NUM_COL_DEPOT_ID_DEPOT_ZONE);
        this.Conservation = cursorZones.getString(ZoneOpenHelper.Constantes.NUM_COL_CONSERVATION_DEPOT_ZONE);
        this.Depot_Reference = cursorZones.getString(ZoneOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_DEPOT_ZONE);
        this.Type_Emplacement = cursorZones.getString(ZoneOpenHelper.Constantes.NUM_COL_TYPE_EMPLACEMENT_DEPOT_ZONE);
        this.emplacements = new ArrayList<>();
        this.phiwms_mobileUUID = cursorZones.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getZoneID() {
        return ZoneID;
    }

    public void setZoneID(int zoneID) {
        ZoneID = zoneID;
    }

    public String getZoneName() {
        return ZoneName;
    }

    public void setZoneName(String zoneName) {
        ZoneName = zoneName;
    }

    public double getZoneLongitude() {
        return ZoneLongitude;
    }

    public void setZoneLongitude(double zoneLongitude) {
        ZoneLongitude = zoneLongitude;
    }

    public double getZoneLatitude() {
        return ZoneLatitude;
    }

    public void setZoneLatitude(double zoneLatitude) {
        ZoneLatitude = zoneLatitude;
    }

    public String getDataMatrixReference() {
        return DataMatrixReference;
    }

    public void setDataMatrixReference(String dataMatrixReference) {
        DataMatrixReference = dataMatrixReference;
    }

    public int getDepotID() {
        return DepotID;
    }

    public void setDepotID(int depotID) {
        DepotID = depotID;
    }

    public String getConservation() {
        return Conservation;
    }

    public void setConservation(String conservation) {
        Conservation = conservation;
    }

    public String getDepot_Reference() {
        return Depot_Reference;
    }

    public void setDepot_Reference(String depot_Reference) {
        Depot_Reference = depot_Reference;
    }

    public String getType_Emplacement() {
        return Type_Emplacement;
    }

    public void setType_Emplacement(String type_Emplacement) {
        Type_Emplacement = type_Emplacement;
    }

    public List<Depot_Emplacement> getEmplacements() {
        return emplacements;
    }

    public void setEmplacements(List<Depot_Emplacement> emplacements) {
        if (emplacements != null) {
            this.emplacements = emplacements;
        }
    }

    public void addEmplacement(Depot_Emplacement emplacement) {
        if (this.emplacements == null) {
            this.emplacements = new ArrayList<>();
        }
        this.emplacements.add(emplacement);
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Depot_Zone)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Depot_Zone depot_zone = (Depot_Zone) obj;

        if (this.getPhiMR4UUID() == depot_zone.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getZoneID() > depot_zone.getZoneID() ? 1 : -1;
        }
    }

    @Override
    public String toString() {
        return this.getZoneName();
    }
}
