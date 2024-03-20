package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.SYS_Mvt_Stock_TypeOpenHelper;

import static fr.alcyons.phimr4.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class SYS_Mvt_Stock_Type implements Serializable, Comparable {

    int TYM_ID;
    String TYM_RUID;
    String TYM_LIB;
    int TYM_SENS_ENTREE;
    int TYM_SENS_SORTIE;
    int TYM_SENS_INVENTAIRE;
    String TYM_TYPE_MAJ;
    String TYM_DT_CREATION;
    String SYS_DT_MAJ;
    String SYS_USER_MAJ;
    int TYM_SENS_ATTENDU;
    String SYS_HEURE_MAJ;
    String XSYS_USER_MAJ;
    private int phiMR4UUID = -1;

    public SYS_Mvt_Stock_Type(int TYM_ID, String TYM_RUID, String TYM_LIB, int TYM_SENS_ENTREE, int TYM_SENS_SORTIE, int TYM_SENS_INVENTAIRE, String TYM_TYPE_MAJ, String TYM_DT_CREATION, String SYS_DT_MAJ, String SYS_USER_MAJ, int TYM_SENS_ATTENDU, String SYS_HEURE_MAJ, String XSYS_USER_MAJ) {
        this.TYM_ID = TYM_ID;
        this.TYM_RUID = TYM_RUID;
        this.TYM_LIB = TYM_LIB;
        this.TYM_SENS_ENTREE = TYM_SENS_ENTREE;
        this.TYM_SENS_SORTIE = TYM_SENS_SORTIE;
        this.TYM_SENS_INVENTAIRE = TYM_SENS_INVENTAIRE;
        this.TYM_TYPE_MAJ = TYM_TYPE_MAJ;
        this.TYM_DT_CREATION = TYM_DT_CREATION;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.TYM_SENS_ATTENDU = TYM_SENS_ATTENDU;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.XSYS_USER_MAJ = XSYS_USER_MAJ;
    }

    public SYS_Mvt_Stock_Type(JSONObject jsonObject) {
        try {
            TYM_ID = jsonObject.getInt("TYM_ID");
            TYM_RUID = recupererString(jsonObject.getString("TYM_RUID"));
            TYM_LIB = recupererString(jsonObject.getString("TYM_LIB"));
            TYM_SENS_ENTREE = jsonObject.getInt("TYM_SENS_ENTREE");
            TYM_SENS_SORTIE = jsonObject.getInt("TYM_SENS_SORTIE");
            TYM_SENS_INVENTAIRE = jsonObject.getInt("TYM_SENS_INVENTAIRE");
            TYM_TYPE_MAJ = recupererString(jsonObject.getString("TYM_TYPE_MAJ"));
            TYM_DT_CREATION = recupererString(jsonObject.getString("TYM_DT_CREATION"));
            SYS_DT_MAJ = recupererString(jsonObject.getString("SYS_DT_MAJ"));
            SYS_USER_MAJ = recupererString(jsonObject.getString("SYS_USER_MAJ"));
            TYM_SENS_ATTENDU = jsonObject.getInt("TYM_SENS_ATTENDU");
            SYS_HEURE_MAJ = recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            XSYS_USER_MAJ = recupererString(jsonObject.getString("XSYS_USER_MAJ"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SYS_Mvt_Stock_Type(Cursor cursor) {
        TYM_ID = cursor.getInt(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_TYM_ID_SYS_MVT_STOCK_TYPE);
        TYM_RUID = cursor.getString(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_TYM_RUID_SYS_MVT_STOCK_TYPE);
        TYM_LIB = cursor.getString(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_TYM_LIB_SYS_MVT_STOCK_TYPE);
        TYM_SENS_ENTREE = cursor.getInt(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_TYM_SENS_ENTREE_SYS_MVT_STOCK_TYPE);
        TYM_SENS_SORTIE = cursor.getInt(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_TYM_SENS_SORTIE_SYS_MVT_STOCK_TYPE);
        TYM_SENS_INVENTAIRE = cursor.getInt(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_TYM_SENS_INVENTAIRE_SYS_MVT_STOCK_TYPE);
        TYM_TYPE_MAJ = cursor.getString(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_TYM_TYPE_MAJ_SYS_MVT_STOCK_TYPE);
        TYM_DT_CREATION = cursor.getString(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_TYM_DT_CREATION_SYS_MVT_STOCK_TYPE);
        SYS_DT_MAJ = cursor.getString(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_SYS_MVT_STOCK_TYPE);
        SYS_USER_MAJ = cursor.getString(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_SYS_MVT_STOCK_TYPE);
        TYM_SENS_ATTENDU = cursor.getInt(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_TYM_SENS_ATTENDU_SYS_MVT_STOCK_TYPE);
        SYS_HEURE_MAJ = cursor.getString(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_SYS_MVT_STOCK_TYPE);
        XSYS_USER_MAJ = cursor.getString(SYS_Mvt_Stock_TypeOpenHelper.Constantes.NUM_COL_XSYS_USER_MAJ_SYS_MVT_STOCK_TYPE);
        phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getTYM_ID() {
        return TYM_ID;
    }

    public void setTYM_ID(int TYM_ID) {
        this.TYM_ID = TYM_ID;
    }

    public String getTYM_RUID() {
        return TYM_RUID;
    }

    public void setTYM_RUID(String TYM_RUID) {
        this.TYM_RUID = TYM_RUID;
    }

    public String getTYM_LIB() {
        return TYM_LIB;
    }

    public void setTYM_LIB(String TYM_LIB) {
        this.TYM_LIB = TYM_LIB;
    }

    public int getTYM_SENS_ENTREE() {
        return TYM_SENS_ENTREE;
    }

    public void setTYM_SENS_ENTREE(int TYM_SENS_ENTREE) {
        this.TYM_SENS_ENTREE = TYM_SENS_ENTREE;
    }

    public int getTYM_SENS_SORTIE() {
        return TYM_SENS_SORTIE;
    }

    public void setTYM_SENS_SORTIE(int TYM_SENS_SORTIE) {
        this.TYM_SENS_SORTIE = TYM_SENS_SORTIE;
    }

    public int getTYM_SENS_INVENTAIRE() {
        return TYM_SENS_INVENTAIRE;
    }

    public void setTYM_SENS_INVENTAIRE(int TYM_SENS_INVENTAIRE) {
        this.TYM_SENS_INVENTAIRE = TYM_SENS_INVENTAIRE;
    }

    public String getTYM_TYPE_MAJ() {
        return TYM_TYPE_MAJ;
    }

    public void setTYM_TYPE_MAJ(String TYM_TYPE_MAJ) {
        this.TYM_TYPE_MAJ = TYM_TYPE_MAJ;
    }

    public String getTYM_DT_CREATION() {
        return TYM_DT_CREATION;
    }

    public void setTYM_DT_CREATION(String TYM_DT_CREATION) {
        this.TYM_DT_CREATION = TYM_DT_CREATION;
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

    public int getTYM_SENS_ATTENDU() {
        return TYM_SENS_ATTENDU;
    }

    public void setTYM_SENS_ATTENDU(int TYM_SENS_ATTENDU) {
        this.TYM_SENS_ATTENDU = TYM_SENS_ATTENDU;
    }

    public String getSYS_HEURE_MAJ() {
        return SYS_HEURE_MAJ;
    }

    public void setSYS_HEURE_MAJ(String SYS_HEURE_MAJ) {
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
    }

    public String getXSYS_USER_MAJ() {
        return XSYS_USER_MAJ;
    }

    public void setXSYS_USER_MAJ(String XSYS_USER_MAJ) {
        this.XSYS_USER_MAJ = XSYS_USER_MAJ;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("TYM_ID", TYM_ID);
            jsonObject.put("TYM_RUID", TYM_RUID);
            jsonObject.put("TYM_LIB", TYM_LIB);
            jsonObject.put("TYM_SENS_ENTREE", TYM_SENS_ENTREE);
            jsonObject.put("TYM_SENS_SORTIE", TYM_SENS_SORTIE);
            jsonObject.put("TYM_SENS_INVENTAIRE", TYM_SENS_INVENTAIRE);
            jsonObject.put("TYM_TYPE_MAJ", TYM_TYPE_MAJ);
            jsonObject.put("TYM_DT_CREATION", TYM_DT_CREATION);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("TYM_SENS_ATTENDU", TYM_SENS_ATTENDU);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("XSYS_USER_MAJ", XSYS_USER_MAJ);
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

        if (!(obj instanceof SYS_Mvt_Stock_Type)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        SYS_Mvt_Stock_Type sys_mvt_stock_type = (SYS_Mvt_Stock_Type) obj;

        if (this.getPhiMR4UUID() == sys_mvt_stock_type.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getTYM_ID() > sys_mvt_stock_type.getTYM_ID() ? 1 : -1;
        }
    }
}
