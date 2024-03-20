package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;

import static fr.alcyons.phimr4.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by olivier on 11/04/2019.
 */

public class ActionUtilisateur implements Serializable, Comparable {

    private int Id;
    private int UserId;
    private String Date;
    private int ServiceId;
    private int EtablissementId;
    private String Statut;
    private int ChampsParentId;
    private String CheminPhoto;
    private String ActionName;
    private int phiMR4UUID = -1;

    public ActionUtilisateur(int Id, int UserId, String Date, int ServiceId, int EtablissementId, String Statut, int ChampsParentId, String CheminPhoto, String ActionName) {
        this.Id = Id;
        this.UserId = UserId;
        this.Date = Date;
        this.ServiceId = ServiceId;
        this.EtablissementId = EtablissementId;
        this.Statut = Statut;
        this.ChampsParentId = ChampsParentId;
        this.CheminPhoto = CheminPhoto;
        this.ActionName = ActionName;
    }

    public ActionUtilisateur(ActionUtilisateur actionUtilisateur) {
        this.Id = actionUtilisateur.getId();
        this.UserId = actionUtilisateur.getUserId();
        this.Date = actionUtilisateur.getDate();
        this.ServiceId = actionUtilisateur.getServiceId();
        this.EtablissementId = actionUtilisateur.getEtablissementId();
        this.Statut = actionUtilisateur.getStatut();
        this.ChampsParentId = actionUtilisateur.getChampsParentId();
        this.CheminPhoto = actionUtilisateur.getCheminPhoto();
        this.ActionName = actionUtilisateur.getActionName();
    }

    public ActionUtilisateur(int Id) {
        this.Id = Id;
        this.UserId = 0;
        this.Date = "";
        this.ServiceId = 0;
        this.EtablissementId = 0;
        this.Statut = "";
        this.ChampsParentId = 0;
        this.CheminPhoto = "";
        this.ActionName = "";
    }

    public ActionUtilisateur(JSONObject jsonObject) {
        try {
            this.Id = jsonObject.getInt("Id");
            this.UserId = jsonObject.getInt("UserId");
            this.Date = recupererString(jsonObject.getString("Date"));
            this.ServiceId = jsonObject.getInt("ServiceId");
            this.EtablissementId = jsonObject.getInt("EtablissementId");
            this.Statut = recupererString(jsonObject.getString("Statut"));
            this.ChampsParentId = jsonObject.getInt("ChampsParentId");
            this.CheminPhoto = jsonObject.getString("CheminPhoto");
            this.ActionName = jsonObject.getString("ActionName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ActionUtilisateur(Cursor cursor) {
        this.Id = cursor.getInt(ActionUtilisateurOpenHelper.Constantes.NUM_COL_ID_ACTION_UTILISATEUR);
        this.UserId = cursor.getInt(ActionUtilisateurOpenHelper.Constantes.NUM_COL_USERID_ACTION_UTILISATEUR);
        this.Date = cursor.getString(ActionUtilisateurOpenHelper.Constantes.NUM_COL_DATE_ACTION_UTILISATEUR);
        this.ServiceId = cursor.getInt(ActionUtilisateurOpenHelper.Constantes.NUM_COL_SERVICE_ID_ACTION_UTILISATEUR);
        this.EtablissementId = cursor.getInt(ActionUtilisateurOpenHelper.Constantes.NUM_COL_ETABLISSEMENT_ID_ACTION_UTILISATEUR);
        this.Statut = cursor.getString(ActionUtilisateurOpenHelper.Constantes.NUM_COL_STATUT_ACTION_UTILISATEUR);
        this.ChampsParentId = cursor.getInt(ActionUtilisateurOpenHelper.Constantes.NUM_COL_CHAMPS_PARENT_ID);
        this.CheminPhoto = cursor.getString(ActionUtilisateurOpenHelper.Constantes.NUM_COL_CHEMIN_PHOTO);
        this.ActionName = cursor.getString(ActionUtilisateurOpenHelper.Constantes.NUM_COL_ACTION_NAME);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getServiceId() {
        return ServiceId;
    }

    public void setServiceId(int serviceId) {
        ServiceId = serviceId;
    }

    public int getEtablissementId() {
        return EtablissementId;
    }

    public void setEtablissementId(int etablissementId) {
        EtablissementId = etablissementId;
    }

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }

    public int getChampsParentId() {
        return ChampsParentId;
    }

    public void setChampsParentId(int champsParentId) {
        ChampsParentId = champsParentId;
    }

    public String getCheminPhoto() {
        return CheminPhoto;
    }

    public void setCheminPhoto(String cheminPhoto) {
        CheminPhoto = cheminPhoto;
    }

    public String getActionName() {
        return ActionName;
    }

    public void setActionName(String actionName) {
        ActionName = actionName;
    }

    public JSONObject toJson() {
        JSONObject actionUtilisateurJson = new JSONObject();

        try {
            actionUtilisateurJson.put("Id", Id);
            actionUtilisateurJson.put("UserId", UserId);
            actionUtilisateurJson.put("Date", Date);
            actionUtilisateurJson.put("ServiceId", ServiceId);
            actionUtilisateurJson.put("EtablissementId", EtablissementId);
            actionUtilisateurJson.put("Statut", Statut);
            actionUtilisateurJson.put("ChampsParentId", ChampsParentId);
            actionUtilisateurJson.put("CheminPhoto", CheminPhoto);
            actionUtilisateurJson.put("ActionName", ActionName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return actionUtilisateurJson;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof ActionUtilisateur)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        ActionUtilisateur actionUtilisateur = (ActionUtilisateur) obj;

        if (this.getPhiMR4UUID() == actionUtilisateur.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getId() > actionUtilisateur.getId() ? 1 : -1;
        }
    }
}
