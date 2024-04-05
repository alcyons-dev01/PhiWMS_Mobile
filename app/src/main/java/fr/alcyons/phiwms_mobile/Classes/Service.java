package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ServiceOpenHelper;

/**
 * Created by quentinlanusse on 13/04/2017.
 */

public class Service implements Serializable, Comparable {
    private int id;
    private String nom;
    private String statut;
    private int indicateur;
    private int ordre;
    private int idPerimetreFonctionnel;
    private String nomPerimetrefonctionnel;
    private String description;
    private String lien_video;
    private String whitePaper;
    private int score;
    private int phiwms_mobileUUID = -1;

    public Service(int id, String nom, int ordre, int idPerimetreFonctionnel, String nomPerimetrefonctionnel, String statut, int indicateur, String description, String lien_video, String whitePaper, int score) {
        this.id = id;
        this.nom = nom;
        this.ordre = ordre;
        this.idPerimetreFonctionnel = idPerimetreFonctionnel;
        this.nomPerimetrefonctionnel = nomPerimetrefonctionnel;
        this.statut = statut;
        this.indicateur = indicateur;
        this.lien_video = lien_video;
        this.description = description;
        this.whitePaper = whitePaper;
        this.score = score;
    }

    public Service(int id, String nom, int ordre, int idPerimetreFonctionnel, String nomPerimetrefonctionnel, String statut, int indicateur, String description, String lien_video, String whitePaper, int score, int phiwms_mobileUUID) {
        this.id = id;
        this.nom = nom;
        this.ordre = ordre;
        this.idPerimetreFonctionnel = idPerimetreFonctionnel;
        this.nomPerimetrefonctionnel = nomPerimetrefonctionnel;
        this.statut = statut;
        this.indicateur = indicateur;
        this.lien_video = lien_video;
        this.description = description;
        this.whitePaper = whitePaper;
        this.score = score;
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public Service(Cursor cursor) {
        this.id = cursor.getInt(ServiceOpenHelper.Constantes.NUM_COL_ID_SERVICE);
        this.nom = cursor.getString(ServiceOpenHelper.Constantes.NUM_COL_NOM_SERVICE);
        this.ordre = cursor.getInt(ServiceOpenHelper.Constantes.NUM_COL_ORDRE_SERVICE);
        this.idPerimetreFonctionnel = cursor.getInt(ServiceOpenHelper.Constantes.NUM_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE);
        this.nomPerimetrefonctionnel = cursor.getString(ServiceOpenHelper.Constantes.NUM_COL_NOM_PERIMETRE_FONCTIONNEL_SERVICE);
        this.statut = cursor.getString(ServiceOpenHelper.Constantes.NUM_COL_STATUT_SERVICE);
        this.indicateur = cursor.getInt(ServiceOpenHelper.Constantes.NUM_COL_INDICATEUR_SERVICE);
        this.description = cursor.getString(ServiceOpenHelper.Constantes.NUM_COL_DESCRIPTION_SERVICE);
        this.lien_video = cursor.getString(ServiceOpenHelper.Constantes.NUM_COL_VIDEO_SERVICE);
        this.whitePaper = cursor.getString(ServiceOpenHelper.Constantes.NUM_COL_WHITEPAPER_SERVICE);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
        this.score = cursor.getInt(ServiceOpenHelper.Constantes.NUM_COL_SCORE);
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public int getIdPerimetreFonctionnel() {
        return idPerimetreFonctionnel;
    }

    public void setIdPerimetreFonctionnel(int idPerimetreFonctionnel) {
        this.idPerimetreFonctionnel = idPerimetreFonctionnel;
    }

    public String getNomPerimetrefonctionnel() {
        return nomPerimetrefonctionnel;
    }

    public void setNomPerimetrefonctionnel(String nomPerimetrefonctionnel) {
        this.nomPerimetrefonctionnel = nomPerimetrefonctionnel;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLien_video() {
        return lien_video;
    }

    public void setLien_video(String lienvideo) {
        this.lien_video = lienvideo;
    }

    public String getWhitePaper() {
        return whitePaper;
    }

    public void setWhitePaper(String white_paper) {
        this.whitePaper = white_paper;
    }

    public int getIndicateur() {
        return indicateur;
    }

    public void setIndicateur(int indicateur) {
        this.indicateur = indicateur;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean existe(SQLiteDatabase db, ServiceOpenHelper gestionnaireBDD) {
        return gestionnaireBDD.verifierExistanceService(db, this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Service)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Service service = (Service) obj;

        if (this.getphiwms_mobileUUID() == ((Service) obj).getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getId() > ((Service) obj).getId() ? 1 : -1;
        }
    }

    @Override
    public String toString() {
        return this.getNom();
    }

    public String toJsonString() {
        String service = "{"
                + "\"id\":" + String.valueOf(this.getId()) + ","
                + "\"nom\":" + "\"" + this.getNom() + "\"" + ","
                + "\"ordre\":" + String.valueOf(this.getOrdre()) + ","
                + "\"statut\":" + "\"" + this.getStatut() + "\"" + ","
                + "\"indicateur\":" + String.valueOf(this.getIndicateur()) + ","
                + "\"idPerimetreFonctionnel\":" + String.valueOf(this.getIdPerimetreFonctionnel()) + ","
                + "\"nomPerimetrefonctionnel\":" + "\"" + this.getNomPerimetrefonctionnel() + "\""
                + "}";
        return service;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", id);
            jsonObject.put("Score", score);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }
}
