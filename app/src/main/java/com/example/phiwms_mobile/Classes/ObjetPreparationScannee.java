package com.example.phiwms_mobile.Classes;


import org.json.JSONException;
import org.json.JSONObject;

import static com.example.phiwms_mobile.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by olivier on 04/06/2019.
 */

public class ObjetPreparationScannee implements Comparable{
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
    private int phiMR4UUID = -1;

    public ObjetPreparationScannee(JSONObject jsonObject) {
        try {
            this._UID = jsonObject.getInt("_UID");
            this.Produit_Code = jsonObject.getInt("Produit_Code");
            this.Depot_Reference = recupererString(jsonObject.getString("Depot_Reference"));
            this.Zone = recupererString(jsonObject.getString("Zone"));
            this.Emplacement = recupererString(jsonObject.getString("Emplacement"));
            this.Lot = recupererString(jsonObject.getString("Lot"));
            this.Qte = jsonObject.getDouble("Qte");
            this.peremptionDate = recupererString(jsonObject.getString("peremptionDate"));
            this.Serie = recupererString(jsonObject.getString("Serie"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ObjetPreparationScannee(double qte, String lot, String peremptionDate, String emplacement, String depot_Reference, String zone, int produit_Code, int qtePrep, String numSerie) {
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

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
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

        if (this.getPhiMR4UUID() == ((ObjetPreparationScannee) obj).getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > ((ObjetPreparationScannee) obj).get_UID() ? 1 : -1;
        }
    }

}
