package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Detail_DotOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by jessica on 02/10/2017.
 */

public class Detail_Dot implements Serializable, Comparable {
    private int dotation_UID;
    private int Code_produit;
    private String Designation;
    private int Cond;
    private int Qte;
    private String Ref_four;
    private String Categorie;
    private boolean Livraison_Directe;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private int _UID;
    private int Valeur_TTC;
    private int Stock_minimum;
    private String PleinVide_Adressage;
    private int phiwms_mobileUUID = -1;

    public Detail_Dot(JSONObject jsonObject) {
        try {
            this.dotation_UID = jsonObject.getInt("dotation_UID");
            this.Code_produit = jsonObject.getInt("Code_produit");
            this.Designation = OutilsGestionClasses.recupererString(jsonObject.getString("Designation"));
            this.Cond = jsonObject.getInt("Cond");
            this.Qte = jsonObject.getInt("Qte");
            this.Ref_four = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_four"));
            this.Categorie = OutilsGestionClasses.recupererString(jsonObject.getString("Categorie"));
            this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(jsonObject, "Livraison_Directe");
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this._UID = jsonObject.getInt("_UID");
            this.Valeur_TTC = jsonObject.getInt("Valeur_TTC");
            this.Stock_minimum = jsonObject.getInt("Stock_minimum");
            this.PleinVide_Adressage = OutilsGestionClasses.recupererString(jsonObject.getString("PleinVide_Adressage"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Detail_Dot(Cursor cursor) {
        this.dotation_UID = cursor.getInt(Detail_DotOpenHelper.Constantes.NUM_COL_DOTATION_UID_DETAIL_DOT);
        this.Code_produit = cursor.getInt(Detail_DotOpenHelper.Constantes.NUM_COL_CODE_PRODUIT_DETAIL_DOT);
        this.Designation = cursor.getString(Detail_DotOpenHelper.Constantes.NUM_COL_DESIGNATION_DETAIL_DOT);
        this.Cond = cursor.getInt(Detail_DotOpenHelper.Constantes.NUM_COL_COND_DETAIL_DOT);
        this.Qte = cursor.getInt(Detail_DotOpenHelper.Constantes.NUM_COL_QTE_DETAIL_DOT);
        this.Ref_four = cursor.getString(Detail_DotOpenHelper.Constantes.NUM_COL_REF_FOUR_DETAIL_DOT);
        this.Categorie = cursor.getString(Detail_DotOpenHelper.Constantes.NUM_COL_CATEGORIE_DETAIL_DOT);
        this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(cursor, Detail_DotOpenHelper.Constantes.NUM_COL_LIVRAISON_DIRECTE_DETAIL_DOT);
        this.SYS_DT_MAJ = cursor.getString(Detail_DotOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_DETAIL_DOT);
        this.SYS_HEURE_MAJ = cursor.getString(Detail_DotOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_DETAIL_DOT);
        this.SYS_USER_MAJ = cursor.getString(Detail_DotOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_DETAIL_DOT);
        this._UID = cursor.getInt(Detail_DotOpenHelper.Constantes.NUM_COL__UID_DETAIL_DOT);
        this.Valeur_TTC = cursor.getInt(Detail_DotOpenHelper.Constantes.NUM_COL_VALEUR_TTC_DETAIL_DOT);
        this.Stock_minimum = cursor.getInt(Detail_DotOpenHelper.Constantes.NUM_COL_STOCK_MINIMUM_DETAIL_DOT);
        this.PleinVide_Adressage = cursor.getString(Detail_DotOpenHelper.Constantes.NUM_COL_PLEINVIDE_ADRESSAGE_DETAIL_DOT);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getDotation_UID() {
        return dotation_UID;
    }

    public void setDotation_UID(int dotation_UID) {
        this.dotation_UID = dotation_UID;
    }

    public int getCode_produit() {
        return Code_produit;
    }

    public void setCode_produit(int code_produit) {
        Code_produit = code_produit;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public int getCond() {
        return Cond;
    }

    public void setCond(int cond) {
        Cond = cond;
    }

    public int getQte() {
        return Qte;
    }

    public void setQte(int qte) {
        Qte = qte;
    }

    public String getRef_four() {
        return Ref_four;
    }

    public void setRef_four(String ref_four) {
        Ref_four = ref_four;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public boolean isLivraison_Directe() {
        return Livraison_Directe;
    }

    public void setLivraison_Directe(boolean livraison_Directe) {
        Livraison_Directe = livraison_Directe;
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

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public int getValeur_TTC() {
        return Valeur_TTC;
    }

    public void setValeur_TTC(int valeur_TTC) {
        Valeur_TTC = valeur_TTC;
    }

    public int getStock_minimum() {
        return Stock_minimum;
    }

    public void setStock_minimum(int stock_minimum) {
        Stock_minimum = stock_minimum;
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public String getPleinVide_Adressage() {
        return PleinVide_Adressage;
    }

    public void setPleinVide_Adressage(String pleinVide_Adressage) {
        PleinVide_Adressage = pleinVide_Adressage;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dotation_UID", dotation_UID);
            jsonObject.put("Code_produit", Code_produit);
            jsonObject.put("Designation", Designation);
            jsonObject.put("Cond", Cond);
            jsonObject.put("Qte", Qte);
            jsonObject.put("Ref_four", Ref_four);
            jsonObject.put("Categorie", Categorie);
            jsonObject.put("Livraison_Directe", Livraison_Directe);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("_UID", _UID);
            jsonObject.put("Valeur_TTC", Valeur_TTC);
            jsonObject.put("Stock_minimum", Stock_minimum);
            jsonObject.put("PleinVide_Adressage", PleinVide_Adressage);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        Detail_Dot detail_dot = (Detail_Dot) obj;

        if (this.getphiwms_mobileUUID() == detail_dot.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getphiwms_mobileUUID() > detail_dot.getphiwms_mobileUUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((Detail_Dot) obj).getphiwms_mobileUUID() == this.getphiwms_mobileUUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Detail_Dot)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }
}
