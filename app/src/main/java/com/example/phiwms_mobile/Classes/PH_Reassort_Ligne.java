package com.example.phiwms_mobile.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import com.example.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.PH_Reassort_LigneOpenHelper;

import static com.example.phiwms_mobile.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by jessica on 02/10/2017.
 */

public class PH_Reassort_Ligne implements Serializable, Comparable {
    private int reassort_UID;
    private int produit_ID;
    private String Designation_int;
    private int Conditionnement;
    private int Quantite;
    private String produit_Reference;
    private String Zone_stockage;
    private String Categorie;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private int stock_Minimum;
    private int _UID;
    private int phiMR4UUID = -1;

    public PH_Reassort_Ligne(JSONObject jsonObject) {
        try {
            this.reassort_UID = jsonObject.getInt("reassort_UID");
            this.produit_ID = jsonObject.getInt("produit_ID");
            this.Designation_int = recupererString(jsonObject.getString("Designation_int"));
            this.Conditionnement = jsonObject.getInt("Conditionnement");
            this.Quantite = jsonObject.getInt("Quantite");
            this.produit_Reference = recupererString(jsonObject.getString("produit_Reference"));
            this.Zone_stockage = recupererString(jsonObject.getString("Zone_stockage"));
            this.Categorie = recupererString(jsonObject.getString("Categorie"));
            this.SYS_DT_MAJ = recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.stock_Minimum = jsonObject.getInt("stock_Minimum");
            this._UID = jsonObject.getInt("_UID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public PH_Reassort_Ligne(Cursor cursor) {
        this.reassort_UID = cursor.getInt(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_REASSORT_UID_PH_REASSORT_LIGNE);
        this.produit_ID = cursor.getInt(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_ID_PH_REASSORT_LIGNE);
        this.Designation_int = cursor.getString(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_DESIGNATION_INT_PH_REASSORT_LIGNE);
        this.Conditionnement = cursor.getInt(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_CONDITIONNEMENT_PH_REASSORT_LIGNE);
        this.Quantite = cursor.getInt(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_QUANTITE_PH_REASSORT_LIGNE);
        this.produit_Reference = cursor.getString(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_REFERENCE_PH_REASSORT_LIGNE);
        this.Zone_stockage = cursor.getString(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_ZONE_STOCKAGE_PH_REASSORT_LIGNE);
        this.Categorie = cursor.getString(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_CATEGORIE_PH_REASSORT_LIGNE);
        this.SYS_DT_MAJ = cursor.getString(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PH_REASSORT_LIGNE);
        this.SYS_HEURE_MAJ = cursor.getString(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PH_REASSORT_LIGNE);
        this.SYS_USER_MAJ = cursor.getString(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PH_REASSORT_LIGNE);
        this.stock_Minimum = cursor.getInt(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL_STOCK_MINIMUM_PH_REASSORT_LIGNE);
        this._UID = cursor.getInt(PH_Reassort_LigneOpenHelper.Constantes.NUM_COL__UID_PH_REASSORT_LIGNE);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getReassort_UID() {
        return reassort_UID;
    }

    public void setReassort_UID(int reassort_UID) {
        this.reassort_UID = reassort_UID;
    }

    public int getProduit_ID() {
        return produit_ID;
    }

    public void setProduit_ID(int produit_ID) {
        this.produit_ID = produit_ID;
    }

    public String getDesignation_int() {
        return Designation_int;
    }

    public void setDesignation_int(String designation_int) {
        Designation_int = designation_int;
    }

    public int getConditionnement() {
        return Conditionnement;
    }

    public void setConditionnement(int conditionnement) {
        Conditionnement = conditionnement;
    }

    public int getQuantite() {
        return Quantite;
    }

    public void setQuantite(int quantite) {
        Quantite = quantite;
    }

    public String getProduit_Reference() {
        return produit_Reference;
    }

    public void setProduit_Reference(String produit_Reference) {
        this.produit_Reference = produit_Reference;
    }

    public String getZone_stockage() {
        return Zone_stockage;
    }

    public void setZone_stockage(String zone_stockage) {
        Zone_stockage = zone_stockage;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public String getSYS_DT_MAJ() {
        return SYS_DT_MAJ;
    }

    public void setSYS_DT_MAJ(String SYS_DT_MAJ) {
        this.SYS_DT_MAJ = SYS_DT_MAJ;
    }

    public String getSYS_HEURE_MAJ() {
        return SYS_HEURE_MAJ;
    }

    public void setSYS_HEURE_MAJ(String SYS_HEURE_MAJ) {
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
    }

    public String getSYS_USER_MAJ() {
        return SYS_USER_MAJ;
    }

    public void setSYS_USER_MAJ(String SYS_USER_MAJ) {
        this.SYS_USER_MAJ = SYS_USER_MAJ;
    }

    public int getStock_Minimum() {
        return stock_Minimum;
    }

    public void setStock_Minimum(int stock_Minimum) {
        this.stock_Minimum = stock_Minimum;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reassort_UID", reassort_UID);
            jsonObject.put("produit_ID", produit_ID);
            jsonObject.put("Designation_int", Designation_int);
            jsonObject.put("Conditionnement", Conditionnement);
            jsonObject.put("Quantite", Quantite);
            jsonObject.put("produit_Reference", produit_Reference);
            jsonObject.put("Zone_stockage", Zone_stockage);
            jsonObject.put("Categorie", Categorie);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("stock_Minimum", stock_Minimum);
            jsonObject.put("_UID", _UID);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        PH_Reassort_Ligne ph_reassort_ligne = (PH_Reassort_Ligne) obj;

        if (this.getPhiMR4UUID() == ph_reassort_ligne.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getPhiMR4UUID() > ph_reassort_ligne.getPhiMR4UUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((PH_Reassort_Ligne) obj).getPhiMR4UUID() == this.getPhiMR4UUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_Reassort_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public String toString() {
        return "PH_Reassort_Ligne{" +
                "reassort_UID=" + reassort_UID +
                ", produit_ID=" + produit_ID +
                ", Designation_int='" + Designation_int + '\'' +
                ", Conditionnement=" + Conditionnement +
                ", Quantite=" + Quantite +
                ", produit_Reference='" + produit_Reference + '\'' +
                ", Zone_stockage='" + Zone_stockage + '\'' +
                ", Categorie='" + Categorie + '\'' +
                ", SYS_DT_MAJ='" + SYS_DT_MAJ + '\'' +
                ", SYS_HEURE_MAJ='" + SYS_HEURE_MAJ + '\'' +
                ", SYS_USER_MAJ='" + SYS_USER_MAJ + '\'' +
                ", stock_Minimum=" + stock_Minimum +
                ", _UID=" + _UID +
                ", phiMR4UUID=" + phiMR4UUID +
                '}';
    }
}
