package com.example.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.TableTraceOpenHelper;

public class TableTrace {

    int id;
    String date;
    String service;
    String situation;
    String codeRetourne;
    String user;
    int userID;
    private int phiMR4UUID = -1;

    public TableTrace(int id, String date, String service, String situation, String codeRetourne, String user, int userID)
    {
        this.id = id;
        this.date = date;
        this.service = service;
        this.situation = situation;
        this.codeRetourne = codeRetourne;
        this.user = user;
        this.userID = userID;
    }

    public TableTrace(Cursor cursor) {
        this.id = cursor.getInt(TableTraceOpenHelper.Constantes.NUM_COL_ID_TABLE_TRACE);
        this.date = cursor.getString(TableTraceOpenHelper.Constantes.NUM_COL_DATE);
        this.service = cursor.getString(TableTraceOpenHelper.Constantes.NUM_COL_SERVICE);
        this.situation = cursor.getString(TableTraceOpenHelper.Constantes.NUM_COL_SITUATION);
        this.codeRetourne = cursor.getString(TableTraceOpenHelper.Constantes.NUM_COL_CODE_RETOURNE);
        this.user = cursor.getString(TableTraceOpenHelper.Constantes.NUM_COL_USER);
        this.userID = cursor.getInt(TableTraceOpenHelper.Constantes.NUM_COL_USERID);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getCodeRetourne() {
        return codeRetourne;
    }

    public void setCodeRetourne(String codeRetourne) {
        this.codeRetourne = codeRetourne;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public JSONObject toJson() {
        JSONObject TableTraceJson = new JSONObject();

        try {
            TableTraceJson.put("id", id);
            TableTraceJson.put("date", date);
            TableTraceJson.put("service", service);
            TableTraceJson.put("situation", situation);
            TableTraceJson.put("codeRetourne", codeRetourne);
            TableTraceJson.put("user", user);
            TableTraceJson.put("userID", userID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return TableTraceJson;
    }
}
