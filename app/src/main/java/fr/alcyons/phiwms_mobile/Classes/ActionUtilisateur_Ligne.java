package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by olivier on 11/04/2019.
 */

public class ActionUtilisateur_Ligne implements Serializable, Comparable {

    private int Id;
    private int IdActionUtilisateur;
    private String TableConcerne;
    private int NumChamps;
    private String GS1;
    private int EmplacementId;
    private int Quantite;
    private String Nom_Produit;
    private int phiwms_mobileUUID = -1;

    public ActionUtilisateur_Ligne(int Id, int IdActionUtilisateur, String TableConcerne, int NumChamps, String GS1, int EmplacementId, int Quantite, String Nom_Produit) {
        this.Id = Id;
        this.IdActionUtilisateur = IdActionUtilisateur;
        this.TableConcerne = TableConcerne;
        this.NumChamps = NumChamps;
        this.GS1 = GS1;
        this.EmplacementId = EmplacementId;
        this.Quantite = Quantite;
        this.Nom_Produit = Nom_Produit;
    }



    public ActionUtilisateur_Ligne(ActionUtilisateur_Ligne actionUtilisateur_ligne) {
        this.Id = actionUtilisateur_ligne.getId();
        this.IdActionUtilisateur = actionUtilisateur_ligne.getIdActionUtilisateur();
        this.TableConcerne = actionUtilisateur_ligne.getTableConcerne();
        this.NumChamps = actionUtilisateur_ligne.getNumChamps();
        this.GS1 = actionUtilisateur_ligne.getGS1();
        this.EmplacementId = actionUtilisateur_ligne.getEmplacementId();
        this.Quantite = actionUtilisateur_ligne.getQuantite();
        this.Nom_Produit = actionUtilisateur_ligne.getNom_Produit();
    }

    public ActionUtilisateur_Ligne(int Id) {
        this.Id = Id;
        this.IdActionUtilisateur = 0;
        this.TableConcerne = "";
        this.NumChamps = 0;
        this.GS1 = "";
        this.EmplacementId = 0;
        this.Quantite = 0;
        this.Nom_Produit = "";
    }

    public ActionUtilisateur_Ligne(JSONObject jsonObject) {
        try {
            this.Id = jsonObject.getInt("Id");
            this.IdActionUtilisateur = jsonObject.getInt("IdActionUtilisateur");
            this.TableConcerne = OutilsGestionClasses.recupererString(jsonObject.getString("TableConcerne"));
            this.NumChamps = jsonObject.getInt("NumChamps");
            this.GS1 = jsonObject.getString("GS1");
            this.EmplacementId = jsonObject.getInt("EmplacementId");
            this.Quantite = jsonObject.getInt("Quantite");
            this.Nom_Produit = jsonObject.getString("Nom_Produit");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ActionUtilisateur_Ligne(Cursor cursor) {
        this.Id = cursor.getInt(ActionUtilisateur_LigneOpenHelper.Constantes.NUM_COL_ID_ACTION_UTILISATEUR_LIGNE);
        this.IdActionUtilisateur = cursor.getInt(ActionUtilisateur_LigneOpenHelper.Constantes.NUM_COL_ID_ACTION_UTILISATEUR);
        this.TableConcerne = cursor.getString(ActionUtilisateur_LigneOpenHelper.Constantes.NUM_COL_TABLE_CONCERNEE);
        this.NumChamps = cursor.getInt(ActionUtilisateur_LigneOpenHelper.Constantes.NUM_COL_NUM_CHAMPS);
        this.GS1 = cursor.getString(ActionUtilisateur_LigneOpenHelper.Constantes.NUM_COL_GS1);
        this.EmplacementId = cursor.getInt(ActionUtilisateur_LigneOpenHelper.Constantes.NUM_COL_EMPLACEMENT_ID);
        this.Quantite = cursor.getInt(ActionUtilisateur_LigneOpenHelper.Constantes.NUM_COL_NUM_QUANTITE);
        this.Nom_Produit = cursor.getString(ActionUtilisateur_LigneOpenHelper.Constantes.NUM_COL_NUM_NOM_PRODUIT);

        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getIdActionUtilisateur() {
        return IdActionUtilisateur;
    }

    public void setIdActionUtilisateur(int idActionUtilisateur) {
        IdActionUtilisateur = idActionUtilisateur;
    }

    public String getTableConcerne() {
        return TableConcerne;
    }

    public void setTableConcerne(String tableConcerne) {
        TableConcerne = tableConcerne;
    }

    public int getNumChamps() {
        return NumChamps;
    }

    public void setNumChamps(int numChamps) {
        NumChamps = numChamps;
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public String getGS1() {
        return GS1;
    }

    public void setGS1(String GS1) {
        this.GS1 = GS1;
    }

    public int getEmplacementId() {
        return EmplacementId;
    }

    public void setEmplacementId(int emplacementId) {
        EmplacementId = emplacementId;
    }

    public int getQuantite() {
        return Quantite;
    }

    public void setQuantite(int quantite) {
        Quantite = quantite;
    }

    public String getNom_Produit() {
        return Nom_Produit;
    }

    public void setNom_Produit(String nom_Produit) {
        Nom_Produit = nom_Produit;
    }

    public JSONObject toJson() {
        JSONObject actionUtilisateurJson = new JSONObject();

        try {
            actionUtilisateurJson.put("Id", Id);
            actionUtilisateurJson.put("IdActionUtilisateur", IdActionUtilisateur);
            actionUtilisateurJson.put("TableConcerne", TableConcerne);
            actionUtilisateurJson.put("NumChamps", NumChamps);
            actionUtilisateurJson.put("GS1", GS1);
            actionUtilisateurJson.put("EmplacementId", EmplacementId);
            actionUtilisateurJson.put("Quantite", Quantite);
            actionUtilisateurJson.put("Nom_Produit", Nom_Produit);

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

        if (!(obj instanceof ActionUtilisateur_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        ActionUtilisateur_Ligne actionUtilisateur_ligne = (ActionUtilisateur_Ligne) obj;

        if (this.getPhiMR4UUID() == actionUtilisateur_ligne.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getId() > actionUtilisateur_ligne.getId() ? 1 : -1;
        }
    }
}