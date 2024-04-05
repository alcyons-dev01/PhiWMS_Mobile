package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.NotificationOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 27/07/2017.
 */

public class Notification implements Serializable, Comparable {

    private int id;
    private String titre;
    private String body;
    private String color;
    private String tag;
    private Boolean aEteGeree = false;
    private String date;
    private String channel;
    private int phiwms_mobileUUID = -1;


    public Notification(int ID, String Titre, String Body, String Color, String Tag, Boolean AEteGeree, String Date, String Channel) {
        this.id = ID;
        this.titre = Titre;
        this.body = Body;
        this.color = Color;
        this.tag = Tag;
        this.aEteGeree = AEteGeree;
        this.date = Date;
        this.channel = Channel;
    }

    public Notification(JSONObject notificationJson) {
        try {
            this.id = notificationJson.getInt("id");
            this.titre = OutilsGestionClasses.recupererString(notificationJson.getString("titre"));
            this.body = OutilsGestionClasses.recupererString(notificationJson.getString("body"));
            this.color = OutilsGestionClasses.recupererString(notificationJson.getString("color"));
            this.tag = OutilsGestionClasses.recupererString(notificationJson.getString("tag"));
            this.aEteGeree = OutilsGestionClasses.recupererBooleen(notificationJson, "aEteGeree");
            this.date = notificationJson.getString("date");
            this.channel = notificationJson.getString("channel");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public Notification(Cursor notificationCursor) {
        this.id = notificationCursor.getInt(NotificationOpenHelper.Constantes.NUM_COL_ID_NOTIFICATION);
        this.titre = notificationCursor.getString(NotificationOpenHelper.Constantes.NUM_COL_TITRE_NOTIFICATION);
        this.body = notificationCursor.getString(NotificationOpenHelper.Constantes.NUM_COL_BODY_NOTIFICATION);
        this.color = notificationCursor.getString(NotificationOpenHelper.Constantes.NUM_COL_COLOR_NOTIFICATION);
        this.tag = notificationCursor.getString(NotificationOpenHelper.Constantes.NUM_COL_TAG_NOTIFICATION);
        this.aEteGeree = OutilsGestionClasses.recupererBooleen(notificationCursor, NotificationOpenHelper.Constantes.NUM_COL_AETEGEREE_NOTIFICATION);
        this.date = notificationCursor.getString(NotificationOpenHelper.Constantes.NUM_COL_DATE_NOTIFICATION);
        this.channel = notificationCursor.getString(NotificationOpenHelper.Constantes.NUM_COL_CHANNEL_NOTIFICATION);
        this.phiwms_mobileUUID = notificationCursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Boolean isaEteGeree() {
        return aEteGeree;
    }

    public void setaEteGeree(Boolean aEteGeree) {
        this.aEteGeree = aEteGeree;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Notification)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Notification notification = (Notification) obj;

        if (this.getphiwms_mobileUUID() == notification.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getId() > notification.getId() ? 1 : -1;
        }
    }
}
