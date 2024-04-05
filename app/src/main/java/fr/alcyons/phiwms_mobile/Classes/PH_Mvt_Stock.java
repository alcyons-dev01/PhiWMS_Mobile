package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Mvt_StockOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class PH_Mvt_Stock implements Serializable, Comparable {

    private int MVT_ID;
    private int MVT_PDT_CODE;
    private String MVT_DEP_ORIG;
    private String MVT_DEP_DEST;
    private String MVT_TYD_CODE;
    private String MVT_NUM_DOC;
    private double MVT_QTE;
    private double MVT_PRIX_UNIT;
    private String MVT_DT_CREAT;
    private String MVT_DT_MVT;
    private String SYS_DT_MAJ;
    private String SYS_USER_MAJ;
    private String Rien;
    private String SYS_HEURE_MAJ;
    private int MVT_ID_Commande_Ligne;
    private double HMV_PRIX_UNITAIRE_TTC_FACT;
    private String MVT_Stock_Zone;
    private String MVT_stock_emplacement;
    private int _UID;
    private int phiMR4UUID = -1;

    public PH_Mvt_Stock(int MVT_ID, int MVT_PDT_CODE, String MVT_DEP_ORIG, String MVT_DEP_DEST, String MVT_TYD_CODE, String MVT_NUM_DOC, double MVT_QTE, double MVT_PRIX_UNIT, String MVT_DT_CREAT, String MVT_DT_MVT, String SYS_DT_MAJ, String SYS_USER_MAJ, String rien, String SYS_HEURE_MAJ, int MVT_ID_Commande_Ligne, double HMV_PRIX_UNITAIRE_TTC_FACT, String MVT_Stock_Zone, String MVT_stock_emplacement, int _UID) {
        this.MVT_ID = MVT_ID;
        this.MVT_PDT_CODE = MVT_PDT_CODE;
        this.MVT_DEP_ORIG = MVT_DEP_ORIG;
        this.MVT_DEP_DEST = MVT_DEP_DEST;
        this.MVT_TYD_CODE = MVT_TYD_CODE;
        this.MVT_NUM_DOC = MVT_NUM_DOC;
        this.MVT_QTE = MVT_QTE;
        this.MVT_PRIX_UNIT = MVT_PRIX_UNIT;
        this.MVT_DT_CREAT = MVT_DT_CREAT;
        this.MVT_DT_MVT = MVT_DT_MVT;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.Rien = rien;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.MVT_ID_Commande_Ligne = MVT_ID_Commande_Ligne;
        this.HMV_PRIX_UNITAIRE_TTC_FACT = HMV_PRIX_UNITAIRE_TTC_FACT;
        this.MVT_Stock_Zone = MVT_Stock_Zone;
        this.MVT_stock_emplacement = MVT_stock_emplacement;
        this._UID = _UID;
    }

    public PH_Mvt_Stock(JSONObject jsonObject) {
        try {
            this.MVT_ID = jsonObject.getInt("MVT_ID");
            this.MVT_PDT_CODE = jsonObject.getInt("MVT_PDT_CODE");
            this.MVT_DEP_ORIG = OutilsGestionClasses.recupererString(jsonObject.getString("MVT_DEP_ORIG"));
            this.MVT_DEP_DEST = OutilsGestionClasses.recupererString(jsonObject.getString("MVT_DEP_DEST"));
            this.MVT_TYD_CODE = OutilsGestionClasses.recupererString(jsonObject.getString("MVT_TYD_CODE"));
            this.MVT_NUM_DOC = OutilsGestionClasses.recupererString(jsonObject.getString("MVT_NUM_DOC"));
            this.MVT_QTE = jsonObject.getDouble("MVT_QTE");
            this.MVT_PRIX_UNIT = jsonObject.getDouble("MVT_PRIX_UNIT");
            this.MVT_DT_CREAT = OutilsGestionClasses.recupererString(jsonObject.getString("MVT_DT_CREAT"));
            this.MVT_DT_MVT = OutilsGestionClasses.recupererString(jsonObject.getString("MVT_DT_MVT"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.Rien = OutilsGestionClasses.recupererString(jsonObject.getString("Rien"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.MVT_ID_Commande_Ligne = jsonObject.getInt("MVT_ID_Commande_Ligne");
            this.HMV_PRIX_UNITAIRE_TTC_FACT = jsonObject.getDouble("HMV_PRIX_UNITAIRE_TTC_FACT");
            this.MVT_Stock_Zone = OutilsGestionClasses.recupererString(jsonObject.getString("MVT_Stock_Zone"));
            this.MVT_stock_emplacement = OutilsGestionClasses.recupererString(jsonObject.getString("MVT_stock_emplacement"));
            this._UID = jsonObject.getInt("_UID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public PH_Mvt_Stock(Cursor cursor) {
        this.MVT_ID = cursor.getInt(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_ID_PH_MVT_STOCK);
        this.MVT_PDT_CODE = cursor.getInt(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_PDT_CODE_PH_MVT_STOCK);
        this.MVT_DEP_ORIG = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_DEP_ORIG_PH_MVT_STOCK);
        this.MVT_DEP_DEST = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_DEP_DEST_PH_MVT_STOCK);
        this.MVT_TYD_CODE = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_TYD_CODE_PH_MVT_STOCK);
        this.MVT_NUM_DOC = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_NUM_DOC_PH_MVT_STOCK);
        this.MVT_QTE = cursor.getDouble(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_QTE_PH_MVT_STOCK);
        this.MVT_PRIX_UNIT = cursor.getDouble(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_PRIX_UNIT_PH_MVT_STOCK);
        this.MVT_DT_CREAT = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_DT_CREAT_PH_MVT_STOCK);
        this.MVT_DT_MVT = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_DT_MVT_PH_MVT_STOCK);
        this.SYS_DT_MAJ = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PH_MVT_STOCK);
        this.SYS_USER_MAJ = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PH_MVT_STOCK);
        this.Rien = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_RIEN_PH_MVT_STOCK);
        this.SYS_HEURE_MAJ = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PH_MVT_STOCK);
        this.MVT_ID_Commande_Ligne = cursor.getInt(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_ID_COMMANDE_LIGNE_PH_MVT_STOCK);
        this.HMV_PRIX_UNITAIRE_TTC_FACT = cursor.getDouble(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_PRIX_UNITAIRE_TTC_FACT_PH_MVT_STOCK);
        this.MVT_Stock_Zone = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_STOCK_ZONE_PH_MVT_STOCK);
        this.MVT_stock_emplacement = cursor.getString(PH_Mvt_StockOpenHelper.Constantes.NUM_COL_MVT_STOCK_EMPLACEMENT_PH_MVT_STOCK);
        this._UID = cursor.getInt(PH_Mvt_StockOpenHelper.Constantes.NUM_COL__UID_PH_MVT_STOCK);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getMVT_ID() {
        return MVT_ID;
    }

    public void setMVT_ID(int MVT_ID) {
        this.MVT_ID = MVT_ID;
    }

    public int getMVT_PDT_CODE() {
        return MVT_PDT_CODE;
    }

    public void setMVT_PDT_CODE(int MVT_PDT_CODE) {
        this.MVT_PDT_CODE = MVT_PDT_CODE;
    }

    public String getMVT_DEP_ORIG() {
        return MVT_DEP_ORIG;
    }

    public void setMVT_DEP_ORIG(String MVT_DEP_ORIG) {
        this.MVT_DEP_ORIG = MVT_DEP_ORIG;
    }

    public String getMVT_DEP_DEST() {
        return MVT_DEP_DEST;
    }

    public void setMVT_DEP_DEST(String MVT_DEP_DEST) {
        this.MVT_DEP_DEST = MVT_DEP_DEST;
    }

    public String getMVT_TYD_CODE() {
        return MVT_TYD_CODE;
    }

    public void setMVT_TYD_CODE(String MVT_TYD_CODE) {
        this.MVT_TYD_CODE = MVT_TYD_CODE;
    }

    public String getMVT_NUM_DOC() {
        return MVT_NUM_DOC;
    }

    public void setMVT_NUM_DOC(String MVT_NUM_DOC) {
        this.MVT_NUM_DOC = MVT_NUM_DOC;
    }

    public double getMVT_QTE() {
        return MVT_QTE;
    }

    public void setMVT_QTE(double MVT_QTE) {
        this.MVT_QTE = MVT_QTE;
    }

    public double getMVT_PRIX_UNIT() {
        return MVT_PRIX_UNIT;
    }

    public void setMVT_PRIX_UNIT(double MVT_PRIX_UNIT) {
        this.MVT_PRIX_UNIT = MVT_PRIX_UNIT;
    }

    public String getMVT_DT_CREAT() {
        return MVT_DT_CREAT;
    }

    public void setMVT_DT_CREAT(String MVT_DT_CREAT) {
        this.MVT_DT_CREAT = MVT_DT_CREAT;
    }

    public String getMVT_DT_MVT() {
        return MVT_DT_MVT;
    }

    public void setMVT_DT_MVT(String MVT_DT_MVT) {
        this.MVT_DT_MVT = MVT_DT_MVT;
    }

    public String getSYS_DT_MAJ() {
        return SYS_DT_MAJ;
    }

    public void setSYS_DT_MAJ(String SYS_DT_MAJ) {
        this.SYS_DT_MAJ = SYS_DT_MAJ;
    }

    public String getSYS_USER_MAJ() {
        return SYS_USER_MAJ;
    }

    public void setSYS_USER_MAJ(String SYS_USER_MAJ) {
        this.SYS_USER_MAJ = SYS_USER_MAJ;
    }

    public String getRien() {
        return Rien;
    }

    public void setRien(String rien) {
        Rien = rien;
    }

    public String getSYS_HEURE_MAJ() {
        return SYS_HEURE_MAJ;
    }

    public void setSYS_HEURE_MAJ(String SYS_HEURE_MAJ) {
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
    }

    public int getMVT_ID_Commande_Ligne() {
        return MVT_ID_Commande_Ligne;
    }

    public void setMVT_ID_Commande_Ligne(int MVT_ID_Commande_Ligne) {
        this.MVT_ID_Commande_Ligne = MVT_ID_Commande_Ligne;
    }

    public double getHMV_PRIX_UNITAIRE_TTC_FACT() {
        return HMV_PRIX_UNITAIRE_TTC_FACT;
    }

    public void setHMV_PRIX_UNITAIRE_TTC_FACT(double HMV_PRIX_UNITAIRE_TTC_FACT) {
        this.HMV_PRIX_UNITAIRE_TTC_FACT = HMV_PRIX_UNITAIRE_TTC_FACT;
    }

    public String getMVT_Stock_Zone() {
        return MVT_Stock_Zone;
    }

    public void setMVT_Stock_Zone(String MVT_Stock_Zone) {
        this.MVT_Stock_Zone = MVT_Stock_Zone;
    }

    public String getMVT_stock_emplacement() {
        return MVT_stock_emplacement;
    }

    public void setMVT_stock_emplacement(String MVT_stock_emplacement) {
        this.MVT_stock_emplacement = MVT_stock_emplacement;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MVT_ID", MVT_ID);
            jsonObject.put("MVT_PDT_CODE", MVT_PDT_CODE);
            jsonObject.put("MVT_DEP_ORIG", MVT_DEP_ORIG);
            jsonObject.put("MVT_DEP_DEST", MVT_DEP_DEST);
            jsonObject.put("MVT_TYD_CODE", MVT_TYD_CODE);
            jsonObject.put("MVT_NUM_DOC", MVT_NUM_DOC);
            jsonObject.put("MVT_QTE", MVT_QTE);
            jsonObject.put("MVT_PRIX_UNIT", MVT_PRIX_UNIT);
            jsonObject.put("MVT_DT_CREAT", MVT_DT_CREAT);
            jsonObject.put("MVT_DT_MVT", MVT_DT_MVT);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("Rien", Rien);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("MVT_ID_Commande_Ligne", MVT_ID_Commande_Ligne);
            jsonObject.put("HMV_PRIX_UNITAIRE_TTC_FACT", HMV_PRIX_UNITAIRE_TTC_FACT);
            jsonObject.put("MVT_Stock_Zone", MVT_Stock_Zone);
            jsonObject.put("MVT_stock_emplacement", MVT_stock_emplacement);
            jsonObject.put("_UID", _UID);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_Mvt_Stock)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        PH_Mvt_Stock ph_mvt_stock = (PH_Mvt_Stock) obj;

        if (this.getPhiMR4UUID() == ph_mvt_stock.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > ph_mvt_stock.get_UID() ? 1 : -1;
        }
    }
}
