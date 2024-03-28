package com.example.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.phiwms_mobile.BaseDeDonnees.FrequencesOpenHelper;

import static com.example.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;
import static com.example.phiwms_mobile.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by olivier on 12/03/2018.
 */

public class Frequences {

    int _UID;
    String ident;
    int Codage;
    boolean L1;
    boolean Ma;
    boolean Mer;
    boolean J;
    boolean V;
    boolean S;
    boolean D;
    boolean L2;
    String Commentaire;
    boolean _01;
    boolean _02;
    boolean _03;
    boolean _04;
    boolean _05;
    boolean _06;
    boolean _07;
    boolean _08;
    boolean _09;
    boolean _10;
    boolean _11;
    boolean _12;
    String SYS_DT_MAJ;
    String SYS_HEURE_MAJ;
    String SYS_USER_MAJ;
    private int phiMR4UUID = -1;

    public Frequences(JSONObject jsonObject) {
        try {
            _UID = jsonObject.getInt("_UID");
            ident = recupererString(jsonObject.getString("ident"));
            Codage = jsonObject.getInt("Codage");
            L1 = recupererBooleen(jsonObject, "L1");
            Ma = recupererBooleen(jsonObject, "Ma");
            Mer = recupererBooleen(jsonObject, "Mer");
            J = recupererBooleen(jsonObject, "J");
            V = recupererBooleen(jsonObject, "V");
            S = recupererBooleen(jsonObject, "S");
            D = recupererBooleen(jsonObject, "D");
            L2 = recupererBooleen(jsonObject, "L2");
            Commentaire = recupererString(jsonObject.getString("Commentaire"));
            _01 = recupererBooleen(jsonObject, "01");
            _02 = recupererBooleen(jsonObject, "02");
            _03 = recupererBooleen(jsonObject, "03");
            _04 = recupererBooleen(jsonObject, "04");
            _05 = recupererBooleen(jsonObject, "05");
            _06 = recupererBooleen(jsonObject, "06");
            _07 = recupererBooleen(jsonObject, "07");
            _08 = recupererBooleen(jsonObject, "08");
            _09 = recupererBooleen(jsonObject, "09");
            _10 = recupererBooleen(jsonObject, "10");
            _11 = recupererBooleen(jsonObject, "11");
            _12 = recupererBooleen(jsonObject, "12");
            SYS_DT_MAJ = recupererString(jsonObject.getString("SYS_DT_MAJ"));
            SYS_HEURE_MAJ = recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            SYS_USER_MAJ = recupererString(jsonObject.getString("SYS_USER_MAJ"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Frequences(Cursor cursor) {
        _UID = cursor.getInt(FrequencesOpenHelper.Constantes.NUM_COL__UID_FREQUENCES);
        ident = cursor.getString(FrequencesOpenHelper.Constantes.NUM_COL_IDENT_FREQUENCES);
        Codage = cursor.getInt(FrequencesOpenHelper.Constantes.NUM_COL_CODAGE_FREQUENCES);
        L1 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_L1_FREQUENCES);
        Ma = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_MA_FREQUENCES);
        Mer = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_MER_FREQUENCES);
        J = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_J_FREQUENCES);
        V = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_V_FREQUENCES);
        S = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_S_FREQUENCES);
        D = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_D_FREQUENCES);
        L2 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_L2_FREQUENCES);
        Commentaire = cursor.getString(FrequencesOpenHelper.Constantes.NUM_COL_COMMENTAIRE_FREQUENCES);
        _01 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_01_FREQUENCES);
        _02 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_02_FREQUENCES);
        _03 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_03_FREQUENCES);
        _04 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_04_FREQUENCES);
        _05 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_05_FREQUENCES);
        _06 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_06_FREQUENCES);
        _07 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_07_FREQUENCES);
        _08 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_08_FREQUENCES);
        _09 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_09_FREQUENCES);
        _10 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_10_FREQUENCES);
        _11 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_11_FREQUENCES);
        _12 = recupererBooleen(cursor, FrequencesOpenHelper.Constantes.NUM_COL_12_FREQUENCES);
        SYS_DT_MAJ = cursor.getString(FrequencesOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_FREQUENCES);
        SYS_HEURE_MAJ = cursor.getString(FrequencesOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_FREQUENCES);
        SYS_USER_MAJ = cursor.getString(FrequencesOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_FREQUENCES);
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public int getCodage() {
        return Codage;
    }

    public void setCodage(int codage) {
        Codage = codage;
    }

    public boolean isL1() {
        return L1;
    }

    public void setL1(boolean l1) {
        L1 = l1;
    }

    public boolean isMa() {
        return Ma;
    }

    public void setMa(boolean ma) {
        Ma = ma;
    }

    public boolean isMer() {
        return Mer;
    }

    public void setMer(boolean mer) {
        Mer = mer;
    }

    public boolean isJ() {
        return J;
    }

    public void setJ(boolean j) {
        J = j;
    }

    public boolean isV() {
        return V;
    }

    public void setV(boolean v) {
        V = v;
    }

    public boolean isS() {
        return S;
    }

    public void setS(boolean s) {
        S = s;
    }

    public boolean isD() {
        return D;
    }

    public void setD(boolean d) {
        D = d;
    }

    public boolean isL2() {
        return L2;
    }

    public void setL2(boolean l2) {
        L2 = l2;
    }

    public String getCommentaire() {
        return Commentaire;
    }

    public void setCommentaire(String commentaire) {
        Commentaire = commentaire;
    }

    public boolean is_01() {
        return _01;
    }

    public void set_01(boolean _01) {
        this._01 = _01;
    }

    public boolean is_02() {
        return _02;
    }

    public void set_02(boolean _02) {
        this._02 = _02;
    }

    public boolean is_03() {
        return _03;
    }

    public void set_03(boolean _03) {
        this._03 = _03;
    }

    public boolean is_04() {
        return _04;
    }

    public void set_04(boolean _04) {
        this._04 = _04;
    }

    public boolean is_05() {
        return _05;
    }

    public void set_05(boolean _05) {
        this._05 = _05;
    }

    public boolean is_06() {
        return _06;
    }

    public void set_06(boolean _06) {
        this._06 = _06;
    }

    public boolean is_07() {
        return _07;
    }

    public void set_07(boolean _07) {
        this._07 = _07;
    }

    public boolean is_08() {
        return _08;
    }

    public void set_08(boolean _08) {
        this._08 = _08;
    }

    public boolean is_09() {
        return _09;
    }

    public void set_09(boolean _09) {
        this._09 = _09;
    }

    public boolean is_10() {
        return _10;
    }

    public void set_10(boolean _10) {
        this._10 = _10;
    }

    public boolean is_11() {
        return _11;
    }

    public void set_11(boolean _11) {
        this._11 = _11;
    }

    public boolean is_12() {
        return _12;
    }

    public void set_12(boolean _12) {
        this._12 = _12;
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

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", _UID);
            jsonObject.put("ident", ident);
            jsonObject.put("Codage", Codage);
            jsonObject.put("L1", L1);
            jsonObject.put("Ma", Ma);
            jsonObject.put("Mer", Mer);
            jsonObject.put("J", J);
            jsonObject.put("V", V);
            jsonObject.put("S", S);
            jsonObject.put("D", D);
            jsonObject.put("L2", L2);
            jsonObject.put("Commentaire", Commentaire);
            jsonObject.put("01", _01);
            jsonObject.put("02", _02);
            jsonObject.put("03", _03);
            jsonObject.put("04", _04);
            jsonObject.put("05", _05);
            jsonObject.put("06", _06);
            jsonObject.put("07", _07);
            jsonObject.put("08", _08);
            jsonObject.put("09", _09);
            jsonObject.put("10", _10);
            jsonObject.put("11", _11);
            jsonObject.put("12", _12);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }
}
