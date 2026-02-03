package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitPlaceOpenHelper;

public class Produit_Place implements Serializable, Comparable {

    private int produitID;
    private int depotID;
    private int zoneID;
    private int placeID;
    private String depotReference;
    private String zoneNom;
    private String placeNom;
    private String produitNom;
    private int phiwms_mobileUUID = -1;

    public Produit_Place(int produitID, int depotID, int zoneID, int placeID, String depotReference, String zoneNom, String placeNom, String produitNom, int phiwms_mobileUUID) {
        this.produitID = produitID;
        this.depotID = depotID;
        this.zoneID = zoneID;
        this.placeID = placeID;
        this.depotReference = depotReference;
        this.zoneNom = zoneNom;
        this.placeNom = placeNom;
        this.produitNom = produitNom;
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public Produit_Place(Produit_Place produitPlace) {
        this.produitID = produitPlace.getProduitID();
        this.depotID = produitPlace.getDepotID();
        this.zoneID = produitPlace.getZoneID();
        this.placeID = produitPlace.getPlaceID();
        this.depotReference = produitPlace.getDepotReference();
        this.zoneNom = produitPlace.getZoneNom();
        this.placeNom = produitPlace.getPlaceNom();
        this.produitNom = produitPlace.getProduitNom();
        this.phiwms_mobileUUID = produitPlace.getPhiwms_mobileUUID();
    }

    public Produit_Place() {
        this.produitID = 0;
        this.depotID = 0;
        this.zoneID = 0;
        this.placeID = 0;
        this.depotReference = "";
        this.zoneNom = "";
        this.placeNom = "";
        this.produitNom = "";
        this.phiwms_mobileUUID = 0;
    }

    public Produit_Place(JSONObject jsonObject) {
        this.produitID = jsonObject.optInt("Produit_ID");
        this.depotID = jsonObject.optInt("Depot_ID");
        this.zoneID = jsonObject.optInt("Zone_ID");
        this.placeID = jsonObject.optInt("Place_ID");
        this.depotReference = jsonObject.optString("Depot_Reference");
        this.zoneNom = jsonObject.optString("Zone_Nom");
        this.placeNom = jsonObject.optString("Place_Nom");
        this.produitNom = jsonObject.optString("Produit_Nom");
    }

    public Produit_Place(Cursor cursor) {
        this.produitID = cursor.getInt(ProduitPlaceOpenHelper.Constantes.NUM_COL_ID_PRODUIT_PRODUIT_PLACE);
        this.depotID = cursor.getInt(ProduitPlaceOpenHelper.Constantes.NUM_COL_ID_DEPOT_PRODUIT_PLACE);
        this.zoneID = cursor.getInt(ProduitPlaceOpenHelper.Constantes.NUM_COL_ID_ZONE_PRODUIT_PLACE);
        this.placeID = cursor.getInt(ProduitPlaceOpenHelper.Constantes.NUM_COL_ID_PLACE_PRODUIT_PLACE);
        this.depotReference = cursor.getString(ProduitPlaceOpenHelper.Constantes.NUM_COL_NOM_DEPOT_PRODUIT_PLACE);
        this.zoneNom = cursor.getString(ProduitPlaceOpenHelper.Constantes.NUM_COL_NOM_ZONE_PRODUIT_PLACE);
        this.placeNom = cursor.getString(ProduitPlaceOpenHelper.Constantes.NUM_COL_NOM_PLACE_PRODUIT_PLACE);
        this.produitNom = cursor.getString(ProduitPlaceOpenHelper.Constantes.NUM_COL_NOM_PRODUIT_PRODUIT_PLACE);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getPhiWMMSID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getProduitID() {
        return produitID;
    }

    public void setProduitID(int produitID) {
        this.produitID = produitID;
    }

    public int getDepotID() {
        return depotID;
    }

    public void setDepotID(int depotID) {
        this.depotID = depotID;
    }

    public int getZoneID() {
        return zoneID;
    }

    public void setZoneID(int zoneID) {
        this.zoneID = zoneID;
    }

    public int getPlaceID() {
        return placeID;
    }

    public void setPlaceID(int placeID) {
        this.placeID = placeID;
    }

    public String getDepotReference() {
        return depotReference;
    }

    public void setDepotReference(String depotReference) {
        this.depotReference = depotReference;
    }

    public String getZoneNom() {
        return zoneNom;
    }

    public void setZoneNom(String zoneNom) {
        this.zoneNom = zoneNom;
    }

    public String getPlaceNom() {
        return placeNom;
    }

    public void setPlaceNom(String placeNom) {
        this.placeNom = placeNom;
    }

    public String getProduitNom() {
        return produitNom;
    }

    public void setProduitNom(String produitNom) {
        this.produitNom = produitNom;
    }

    public int getPhiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setPhiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public JSONObject toJson() {
        JSONObject produitPlaceJson = new JSONObject();

        try {
            produitPlaceJson.put("Produit_ID", produitID);
            produitPlaceJson.put("Depot_ID", depotID);
            produitPlaceJson.put("Zone_ID", zoneID);
            produitPlaceJson.put("Place_ID", placeID);
            produitPlaceJson.put("Depot_Reference", depotReference);
            produitPlaceJson.put("Zone_Nom", zoneNom);
            produitPlaceJson.put("Place_Nom", placeNom);
            produitPlaceJson.put("Produit_Nom", produitNom);
            produitPlaceJson.put("Id", phiwms_mobileUUID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return produitPlaceJson;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Produit_Place)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Produit_Place produitPlace = (Produit_Place) obj;

        if (this.getPhiwms_mobileUUID() == produitPlace.getPhiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getPhiwms_mobileUUID() > produitPlace.getPhiwms_mobileUUID() ? 1 : -1;
        }
    }
}
