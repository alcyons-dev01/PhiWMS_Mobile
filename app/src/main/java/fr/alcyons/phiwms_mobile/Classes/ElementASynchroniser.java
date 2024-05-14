package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;

public class ElementASynchroniser {
    private int phiwms_mobileUUID;
    private String tableConcernee;
    private String action;
    private int idDansTableConcernee;
    private int idOrigine4D;

    public ElementASynchroniser(String tableConcernee, String action, int idDansTableConcernee, int idOrigine4D) {
        this.tableConcernee = tableConcernee;
        this.action = action;
        this.idDansTableConcernee = idDansTableConcernee;
        this.idOrigine4D = idOrigine4D;
    }

    public ElementASynchroniser(Cursor cursor) {
        this.tableConcernee = cursor.getString(ElementASynchroniserOpenHelper.Constantes.NUM_COL_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER);
        this.action = cursor.getString(ElementASynchroniserOpenHelper.Constantes.NUM_COL_ACTION_ELEMENT_A_SYNCHRONISER);
        this.idDansTableConcernee = cursor.getInt(ElementASynchroniserOpenHelper.Constantes.NUM_COL_ID_DANS_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER);
        this.idOrigine4D = cursor.getInt(ElementASynchroniserOpenHelper.Constantes.NUM_COL_ID_ORIGINE_4D);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getIdOrigine4D() {
        return idOrigine4D;
    }

    public void setIdOrigine4D(int idOrigine4D) {
        this.idOrigine4D = idOrigine4D;
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public String getTableConcernee() {
        return tableConcernee;
    }

    public void setTableConcernee(String tableConcernee) {
        this.tableConcernee = tableConcernee;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getIdDansTableConcernee() {
        return idDansTableConcernee;
    }

    public void setIdDansTableConcernee(int idDansTableConcernee) {
        this.idDansTableConcernee = idDansTableConcernee;
    }
}
