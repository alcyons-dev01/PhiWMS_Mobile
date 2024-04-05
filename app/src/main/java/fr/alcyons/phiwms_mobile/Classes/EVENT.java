package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by jessica on 09/10/2017.
 */

public class EVENT implements Serializable, Comparable {

    private int _UID;
    private String Date_event;
    private int ID_Ressource;
    private String Jour_event;
    private int Semaine_event;
    private String mois_de;
    private String Jour_de;
    private String annee_de;
    private String Mois_livraison;
    private String moisReference;
    private int TourneeID;
    private int phiwms_mobileUUID = -1;

    public EVENT(JSONObject jsonObject) {
        try {
            this._UID = jsonObject.getInt("_UID");
            this.Date_event = OutilsGestionClasses.recupererString(jsonObject.getString("Date_event"));
            this.ID_Ressource = jsonObject.getInt("ID_Ressource");
            this.Jour_event = OutilsGestionClasses.recupererString(jsonObject.getString("Jour_event"));
            this.Semaine_event = jsonObject.getInt("Semaine_event");
            this.mois_de = OutilsGestionClasses.recupererString(jsonObject.getString("mois_de"));
            this.Jour_de = OutilsGestionClasses.recupererString(jsonObject.getString("Jour_de"));
            this.annee_de = OutilsGestionClasses.recupererString(jsonObject.getString("annee_de"));
            this.Mois_livraison = OutilsGestionClasses.recupererString(jsonObject.getString("Mois_livraison"));
            this.moisReference = OutilsGestionClasses.recupererString(jsonObject.getString("moisReference"));
            this.TourneeID = jsonObject.getInt("TourneeID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public EVENT(Cursor cursor) {
        this._UID = cursor.getInt(EVENTOpenHelper.Constantes.NUM_COL__UID_EVENT);
        this.Date_event = cursor.getString(EVENTOpenHelper.Constantes.NUM_COL_DATE_EVENT_EVENT);
        this.ID_Ressource = cursor.getInt(EVENTOpenHelper.Constantes.NUM_COL_ID_RESSOURCE_EVENT);
        this.Jour_event = cursor.getString(EVENTOpenHelper.Constantes.NUM_COL_JOUR_EVENT_EVENT);
        this.Semaine_event = cursor.getInt(EVENTOpenHelper.Constantes.NUM_COL_SEMAINE_EVENT_EVENT);
        this.mois_de = cursor.getString(EVENTOpenHelper.Constantes.NUM_COL_MOIS_DE_EVENT);
        this.Jour_de = cursor.getString(EVENTOpenHelper.Constantes.NUM_COL_JOUR_DE_EVENT);
        this.annee_de = cursor.getString(EVENTOpenHelper.Constantes.NUM_COL_ANNEE_DE_EVENT);
        this.Mois_livraison = cursor.getString(EVENTOpenHelper.Constantes.NUM_COL_MOIS_LIVRAISON_EVENT);
        this.moisReference = cursor.getString(EVENTOpenHelper.Constantes.NUM_COL_MOISREFERENCE_EVENT);
        this.TourneeID = cursor.getInt(EVENTOpenHelper.Constantes.NUM_COL_TOURNEEID_EVENT);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getDate_event() {
        return Date_event;
    }

    public void setDate_event(String date_event) {
        Date_event = date_event;
    }

    public int getID_Ressource() {
        return ID_Ressource;
    }

    public void setID_Ressource(int ID_Ressource) {
        this.ID_Ressource = ID_Ressource;
    }

    public String getJour_event() {
        return Jour_event;
    }

    public void setJour_event(String jour_event) {
        Jour_event = jour_event;
    }

    public int getSemaine_event() {
        return Semaine_event;
    }

    public void setSemaine_event(int semaine_event) {
        Semaine_event = semaine_event;
    }

    public String getMois_de() {
        return mois_de;
    }

    public void setMois_de(String mois_de) {
        this.mois_de = mois_de;
    }

    public String getJour_de() {
        return Jour_de;
    }

    public void setJour_de(String jour_de) {
        Jour_de = jour_de;
    }

    public String getAnnee_de() {
        return annee_de;
    }

    public void setAnnee_de(String annee_de) {
        this.annee_de = annee_de;
    }

    public String getMois_livraison() {
        return Mois_livraison;
    }

    public void setMois_livraison(String mois_livraison) {
        Mois_livraison = mois_livraison;
    }

    public String getMoisReference() {
        return moisReference;
    }

    public void setMoisReference(String moisReference) {
        this.moisReference = moisReference;
    }

    public int getTourneeID() {
        return TourneeID;
    }

    public void setTourneeID(int tourneeID) {
        TourneeID = tourneeID;
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", _UID);
            jsonObject.put("Date_event", Date_event);
            jsonObject.put("ID_Ressource", ID_Ressource);
            jsonObject.put("Jour_event", Jour_event);
            jsonObject.put("Semaine_event", Semaine_event);
            jsonObject.put("mois_de", mois_de);
            jsonObject.put("Jour_de", Jour_de);
            jsonObject.put("annee_de", annee_de);
            jsonObject.put("Mois_livraison", Mois_livraison);
            jsonObject.put("moisReference", moisReference);
            jsonObject.put("TourneeID", TourneeID);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return "EVENT{" +
                "_UID=" + _UID +
                ", Date_event='" + Date_event + '\'' +
                ", ID_Ressource=" + ID_Ressource +
                ", Jour_event='" + Jour_event + '\'' +
                ", Semaine_event=" + Semaine_event +
                ", mois_de='" + mois_de + '\'' +
                ", Jour_de='" + Jour_de + '\'' +
                ", annee_de='" + annee_de + '\'' +
                ", Mois_livraison='" + Mois_livraison + '\'' +
                ", moisReference='" + moisReference + '\'' +
                ", TourneeID=" + TourneeID +
                ", phiwms_mobileUUID=" + phiwms_mobileUUID +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        EVENT event = (EVENT) obj;

        if (this.getphiwms_mobileUUID() == event.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getphiwms_mobileUUID() > event.getphiwms_mobileUUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((EVENT) obj).getphiwms_mobileUUID() == this.getphiwms_mobileUUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof EVENT)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }


}
