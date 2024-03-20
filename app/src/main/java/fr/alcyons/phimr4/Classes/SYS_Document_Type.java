package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.SYS_Document_TypeOpenHelper;
import fr.alcyons.phimr4.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class SYS_Document_Type implements Serializable, Comparable {

    private int TYD_ID;
    private String TYD_CODE;
    private String TYD_DEPOT;
    private String TYD_TYM_CODE1;
    private String TYD_TYM_CODE2;
    private String TYD_DT_CREAT;
    private String SYS_DT_MAJ;
    private String SYS_USER_MAJ;
    private String TYD_DESIGNATION;
    private String SYS_HEURE_MAJ;
    private String rien;
    private int phiMR4UUID = -1;

    public SYS_Document_Type(int TYD_ID, String TYD_CODE, String TYD_DEPOT, String TYD_TYM_CODE1, String TYD_TYM_CODE2, String TYD_DT_CREAT, String SYS_DT_MAJ, String SYS_USER_MAJ, String TYD_DESIGNATION, String SYS_HEURE_MAJ, String rien) {
        this.TYD_ID = TYD_ID;
        this.TYD_CODE = TYD_CODE;
        this.TYD_DEPOT = TYD_DEPOT;
        this.TYD_TYM_CODE1 = TYD_TYM_CODE1;
        this.TYD_TYM_CODE2 = TYD_TYM_CODE2;
        this.TYD_DT_CREAT = TYD_DT_CREAT;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.TYD_DESIGNATION = TYD_DESIGNATION;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.rien = rien;
    }

    public SYS_Document_Type(JSONObject jsonObject) {
        try {
            this.TYD_ID = jsonObject.getInt("TYD_ID");
            this.TYD_CODE = OutilsGestionClasses.recupererString(jsonObject.getString("TYD_CODE"));
            this.TYD_DEPOT = OutilsGestionClasses.recupererString(jsonObject.getString("TYD_DEPOT"));
            this.TYD_TYM_CODE1 = OutilsGestionClasses.recupererString(jsonObject.getString("TYD_TYM_CODE1"));
            this.TYD_TYM_CODE2 = OutilsGestionClasses.recupererString(jsonObject.getString("TYD_TYM_CODE2"));
            this.TYD_DT_CREAT = OutilsGestionClasses.recupererString(jsonObject.getString("TYD_DT_CREAT"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.TYD_DESIGNATION = OutilsGestionClasses.recupererString(jsonObject.getString("TYD_DESIGNATION"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.rien = OutilsGestionClasses.recupererString(jsonObject.getString("rien"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SYS_Document_Type(Cursor cursor) {
        this.TYD_ID = cursor.getInt(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_TYD_ID_SYS_DOCUMENT_TYPE);
        this.TYD_CODE = cursor.getString(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_TYD_CODE_SYS_DOCUMENT_TYPE);
        this.TYD_DEPOT = cursor.getString(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_TYD_DEPOT_SYS_DOCUMENT_TYPE);
        this.TYD_TYM_CODE1 = cursor.getString(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_TYD_TYM_CODE1_SYS_DOCUMENT_TYPE);
        this.TYD_TYM_CODE2 = cursor.getString(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_TYD_TYM_CODE2_SYS_DOCUMENT_TYPE);
        this.TYD_DT_CREAT = cursor.getString(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_TYD_DT_CREAT_SYS_DOCUMENT_TYPE);
        this.SYS_DT_MAJ = cursor.getString(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_SYS_DOCUMENT_TYPE);
        this.SYS_USER_MAJ = cursor.getString(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_SYS_DOCUMENT_TYPE);
        this.TYD_DESIGNATION = cursor.getString(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_TYD_DESIGNATION_SYS_DOCUMENT_TYPE);
        this.SYS_HEURE_MAJ = cursor.getString(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_SYS_DOCUMENT_TYPE);
        this.rien = cursor.getString(SYS_Document_TypeOpenHelper.Constantes.NUM_COL_RIEN_SYS_DOCUMENT_TYPE);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getTYD_ID() {
        return TYD_ID;
    }

    public void setTYD_ID(int TYD_ID) {
        this.TYD_ID = TYD_ID;
    }

    public String getTYD_CODE() {
        return TYD_CODE;
    }

    public void setTYD_CODE(String TYD_CODE) {
        this.TYD_CODE = TYD_CODE;
    }

    public String getTYD_DEPOT() {
        return TYD_DEPOT;
    }

    public void setTYD_DEPOT(String TYD_DEPOT) {
        this.TYD_DEPOT = TYD_DEPOT;
    }

    public String getTYD_TYM_CODE1() {
        return TYD_TYM_CODE1;
    }

    public void setTYD_TYM_CODE1(String TYD_TYM_CODE1) {
        this.TYD_TYM_CODE1 = TYD_TYM_CODE1;
    }

    public String getTYD_TYM_CODE2() {
        return TYD_TYM_CODE2;
    }

    public void setTYD_TYM_CODE2(String TYD_TYM_CODE2) {
        this.TYD_TYM_CODE2 = TYD_TYM_CODE2;
    }

    public String getTYD_DT_CREAT() {
        return TYD_DT_CREAT;
    }

    public void setTYD_DT_CREAT(String TYD_DT_CREAT) {
        this.TYD_DT_CREAT = TYD_DT_CREAT;
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

    public String getTYD_DESIGNATION() {
        return TYD_DESIGNATION;
    }

    public void setTYD_DESIGNATION(String TYD_DESIGNATION) {
        this.TYD_DESIGNATION = TYD_DESIGNATION;
    }

    public String getSYS_HEURE_MAJ() {
        return SYS_HEURE_MAJ;
    }

    public void setSYS_HEURE_MAJ(String SYS_HEURE_MAJ) {
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
    }

    public String getRien() {
        return rien;
    }

    public void setRien(String rien) {
        this.rien = rien;
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("TYD_ID", TYD_ID);
            jsonObject.put("TYD_CODE", TYD_CODE);
            jsonObject.put("TYD_DEPOT", TYD_DEPOT);
            jsonObject.put("TYD_TYM_CODE1", TYD_TYM_CODE1);
            jsonObject.put("TYD_TYM_CODE2", TYD_TYM_CODE2);
            jsonObject.put("TYD_DT_CREAT", TYD_DT_CREAT);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("TYD_DESIGNATION", TYD_DESIGNATION);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("rien", rien);
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

        if (!(obj instanceof SYS_Document_Type)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        SYS_Document_Type sys_document_type = (SYS_Document_Type) obj;

        if (this.getPhiMR4UUID() == sys_document_type.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getTYD_ID() > sys_document_type.getTYD_ID() ? 1 : -1;
        }
    }
}
