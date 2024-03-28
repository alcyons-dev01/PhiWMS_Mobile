package com.example.phiwms_mobile.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import com.example.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.PH_ReassortOpenHelper;

import static com.example.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;
import static com.example.phiwms_mobile.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by jessica on 02/10/2017.
 */

public class PH_Reassort implements Serializable, Comparable {

    private int Code;
    private String Liste;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String Depot_Reference;
    private String Frequence;
    private boolean SynchroDM_Medicament;
    private boolean SynchroDM_DMDMS;
    private int Valorisation_TTC;
    private int phiMR4UUID = -1;

    public PH_Reassort(JSONObject jsonObject) {

        try {
            this.Code = jsonObject.getInt("Code");
            this.Liste = recupererString(jsonObject.getString("Liste"));
            this.SYS_DT_MAJ = recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.Depot_Reference = recupererString(jsonObject.getString("Depot_Reference"));
            this.Frequence = recupererString(jsonObject.getString("Frequence"));
            this.SynchroDM_Medicament = recupererBooleen(jsonObject, "SynchroDM_Medicament");
            this.SynchroDM_DMDMS = recupererBooleen(jsonObject, "SynchroDM_DMDMS");
            this.Valorisation_TTC = jsonObject.getInt("Valorisation_TTC");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public PH_Reassort(Cursor cursor) {
        this.Code = cursor.getInt(PH_ReassortOpenHelper.Constantes.NUM_COL_CODE_PH_REASSORT);
        this.Liste = cursor.getString(PH_ReassortOpenHelper.Constantes.NUM_COL_LISTE_PH_REASSORT);
        this.SYS_DT_MAJ = cursor.getString(PH_ReassortOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PH_REASSORT);
        this.SYS_HEURE_MAJ = cursor.getString(PH_ReassortOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PH_REASSORT);
        this.SYS_USER_MAJ = cursor.getString(PH_ReassortOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PH_REASSORT);
        this.Depot_Reference = cursor.getString(PH_ReassortOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_PH_REASSORT);
        this.Frequence = cursor.getString(PH_ReassortOpenHelper.Constantes.NUM_COL_FREQUENCE_PH_REASSORT);
        this.SynchroDM_Medicament = recupererBooleen(cursor, PH_ReassortOpenHelper.Constantes.NUM_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT);
        this.SynchroDM_DMDMS = recupererBooleen(cursor, PH_ReassortOpenHelper.Constantes.NUM_COL_SYNCHRODM_DMDMS_PH_REASSORT);
        this.Valorisation_TTC = cursor.getInt(PH_ReassortOpenHelper.Constantes.NUM_COL_VALORISATION_TTC_PH_REASSORT);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getListe() {
        return Liste;
    }

    public void setListe(String liste) {
        Liste = liste;
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

    public String getDepot_Reference() {
        return Depot_Reference;
    }

    public void setDepot_Reference(String depot_Reference) {
        Depot_Reference = depot_Reference;
    }

    public String getFrequence() {
        return Frequence;
    }

    public void setFrequence(String frequence) {
        Frequence = frequence;
    }

    public boolean isSynchroDM_Medicament() {
        return SynchroDM_Medicament;
    }

    public void setSynchroDM_Medicament(boolean synchroDM_Medicament) {
        SynchroDM_Medicament = synchroDM_Medicament;
    }

    public boolean isSynchroDM_DMDMS() {
        return SynchroDM_DMDMS;
    }

    public void setSynchroDM_DMDMS(boolean synchroDM_DMDMS) {
        SynchroDM_DMDMS = synchroDM_DMDMS;
    }

    public int getValorisation_TTC() {
        return Valorisation_TTC;
    }

    public void setValorisation_TTC(int valorisation_TTC) {
        Valorisation_TTC = valorisation_TTC;
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
            jsonObject.put("Code", Code);
            jsonObject.put("Liste", Liste);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("Depot_Reference", Depot_Reference);
            jsonObject.put("Frequence", Frequence);
            jsonObject.put("SynchroDM_Medicament", SynchroDM_Medicament);
            jsonObject.put("SynchroDM_DMDMS", SynchroDM_DMDMS);
            jsonObject.put("Valorisation_TTC", Valorisation_TTC);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return "PH_Reassort{" +
                "Code=" + Code +
                ", Liste='" + Liste + '\'' +
                ", SYS_DT_MAJ='" + SYS_DT_MAJ + '\'' +
                ", SYS_HEURE_MAJ='" + SYS_HEURE_MAJ + '\'' +
                ", SYS_USER_MAJ='" + SYS_USER_MAJ + '\'' +
                ", Depot_Reference='" + Depot_Reference + '\'' +
                ", Frequence='" + Frequence + '\'' +
                ", SynchroDM_Medicament=" + SynchroDM_Medicament +
                ", SynchroDM_DMDMS=" + SynchroDM_DMDMS +
                ", Valorisation_TTC=" + Valorisation_TTC +
                ", phiMR4UUID=" + phiMR4UUID +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        PH_Reassort ph_reassort = (PH_Reassort) obj;

        if (this.getPhiMR4UUID() == ph_reassort.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getPhiMR4UUID() > ph_reassort.getPhiMR4UUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((PH_Reassort) obj).getPhiMR4UUID() == this.getPhiMR4UUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_Reassort)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }
}
