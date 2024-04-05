package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Inventaire implements Serializable, Comparable {


    private int Inventaire_ID;
    private String Cycle;
    private String InventaireDate;
    private String cycleDateDebut;
    private String _SYS_DT_MAJ;
    private String _SYS_USER_MAJ;
    private String _SYS_HEURE_MAJ;
    private String cycleDateFin;
    private boolean clotureActive;
    private String objet;
    private String Mode_Comptabilisation;
    private String depotReference;
    private String depotNom;
    private String operateur;
    private int NBLignes;
    private double Valeur_TTC;
    private double Valeur_PUMP_TTC;
    private int phiwms_mobileUUID = -1;

    public Inventaire(int inventaire_ID, String cycle, String inventaireDate, String cycleDateDebut, String _SYS_DT_MAJ, String _SYS_USER_MAJ, String _SYS_HEURE_MAJ, String cycleDateFin, Boolean clotureActive, String objet, String mode_Comptabilisation, String depotReference, String depotNom, String operateur, int NBLignes, double valeur_TTC, double valeur_PUMP_TTC) {
        this.Inventaire_ID = inventaire_ID;
        this.Cycle = cycle;
        this.InventaireDate = inventaireDate;
        this.cycleDateDebut = cycleDateDebut;
        this._SYS_DT_MAJ = _SYS_DT_MAJ;
        this._SYS_USER_MAJ = _SYS_USER_MAJ;
        this._SYS_HEURE_MAJ = _SYS_HEURE_MAJ;
        this.cycleDateFin = cycleDateFin;
        this.clotureActive = clotureActive;
        this.objet = objet;
        this.Mode_Comptabilisation = mode_Comptabilisation;
        this.depotReference = depotReference;
        this.depotNom = depotNom;
        this.operateur = operateur;
        this.NBLignes = NBLignes;
        this.Valeur_TTC = valeur_TTC;
        this.Valeur_PUMP_TTC = valeur_PUMP_TTC;
    }

    public Inventaire(JSONObject jsonObject) {
        try {
            this.Inventaire_ID = jsonObject.getInt("Inventaire_ID");
            this.Cycle = OutilsGestionClasses.recupererString(jsonObject.getString("Cycle"));
            this.InventaireDate = OutilsGestionClasses.recupererString(jsonObject.getString("InventaireDate"));
            this.cycleDateDebut = OutilsGestionClasses.recupererString(jsonObject.getString("cycleDateDebut"));
            this._SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("_SYS_DT_MAJ"));
            this._SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("_SYS_USER_MAJ"));
            this._SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("_SYS_HEURE_MAJ"));
            this.cycleDateFin = OutilsGestionClasses.recupererString(jsonObject.getString("cycleDateFin"));
            this.clotureActive = OutilsGestionClasses.recupererBooleen(jsonObject, "clotureActive");
            this.objet = OutilsGestionClasses.recupererString(jsonObject.getString("objet"));
            this.Mode_Comptabilisation = OutilsGestionClasses.recupererString(jsonObject.getString("Mode_Comptabilisation"));
            this.depotReference = OutilsGestionClasses.recupererString(jsonObject.getString("depotReference"));
            this.depotNom = OutilsGestionClasses.recupererString(jsonObject.getString("depotNom"));
            this.operateur = OutilsGestionClasses.recupererString(jsonObject.getString("operateur"));
            this.NBLignes = jsonObject.getInt("NBLignes");
            this.Valeur_TTC = jsonObject.getDouble("Valeur_TTC");
            this.Valeur_PUMP_TTC = jsonObject.getDouble("Valeur_PUMP_TTC");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Inventaire(Cursor cursor) {
        this.Inventaire_ID = cursor.getInt(InventaireOpenHelper.Constantes.NUM_COL_INVENTAIRE_ID_INVENTAIRE);
        this.Cycle = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL_CYCLE_INVENTAIRE);
        this.InventaireDate = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL_INVENTAIREDATE_INVENTAIRE);
        this.cycleDateDebut = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL_CYCLEDATEDEBUT_INVENTAIRE);
        this._SYS_DT_MAJ = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL__SYS_DT_MAJ_INVENTAIRE);
        this._SYS_USER_MAJ = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL__SYS_USER_MAJ_INVENTAIRE);
        this._SYS_HEURE_MAJ = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL__SYS_HEURE_MAJ_INVENTAIRE);
        this.cycleDateFin = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL_CYCLEDATEFIN_INVENTAIRE);
        this.clotureActive = OutilsGestionClasses.recupererBooleen(cursor, InventaireOpenHelper.Constantes.NUM_COL_CLOTUREACTIVE_INVENTAIRE);
        this.objet = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL_OBJET_INVENTAIRE);
        this.Mode_Comptabilisation = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL_MODE_COMPTABILISATION_INVENTAIRE);
        this.depotReference = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL_DEPOTREFERENCE_INVENTAIRE);
        this.depotNom = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL_DEPOTNOM_INVENTAIRE);
        this.operateur = cursor.getString(InventaireOpenHelper.Constantes.NUM_COL_OPERATEUR_INVENTAIRE);
        this.NBLignes = cursor.getInt(InventaireOpenHelper.Constantes.NUM_COL_NBLIGNES_INVENTAIRE);
        this.Valeur_TTC = cursor.getDouble(InventaireOpenHelper.Constantes.NUM_COL_VALEUR_TTC_INVENTAIRE);
        this.Valeur_PUMP_TTC = cursor.getDouble(InventaireOpenHelper.Constantes.NUM_COL_VALEUR_PUMP_TTC_INVENTAIRE);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getInventaire_ID() {
        return Inventaire_ID;
    }

    public void setInventaire_ID(int inventaire_ID) {
        Inventaire_ID = inventaire_ID;
    }

    public String getCycle() {
        return Cycle;
    }

    public void setCycle(String cycle) {
        Cycle = cycle;
    }

    public String getInventaireDate() {
        return InventaireDate;
    }

    public void setInventaireDate(String inventaireDate) {
        InventaireDate = inventaireDate;
    }

    public String getCycleDateDebut() {
        return cycleDateDebut;
    }

    public void setCycleDateDebut(String cycleDateDebut) {
        this.cycleDateDebut = cycleDateDebut;
    }

    public String get_SYS_DT_MAJ() {
        return _SYS_DT_MAJ;
    }

    public void set_SYS_DT_MAJ(String _SYS_DT_MAJ) {
        this._SYS_DT_MAJ = _SYS_DT_MAJ;
    }

    public String get_SYS_USER_MAJ() {
        return _SYS_USER_MAJ;
    }

    public void set_SYS_USER_MAJ(String _SYS_USER_MAJ) {
        this._SYS_USER_MAJ = _SYS_USER_MAJ;
    }

    public String get_SYS_HEURE_MAJ() {
        return _SYS_HEURE_MAJ;
    }

    public void set_SYS_HEURE_MAJ(String _SYS_HEURE_MAJ) {
        this._SYS_HEURE_MAJ = _SYS_HEURE_MAJ;
    }

    public String getCycleDateFin() {
        return cycleDateFin;
    }

    public void setCycleDateFin(String cycleDateFin) {
        this.cycleDateFin = cycleDateFin;
    }

    public Boolean getClotureActive() {
        return clotureActive;
    }

    public void setClotureActive(Boolean clotureActive) {
        this.clotureActive = clotureActive;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getMode_Comptabilisation() {
        return Mode_Comptabilisation;
    }

    public void setMode_Comptabilisation(String mode_Comptabilisation) {
        Mode_Comptabilisation = mode_Comptabilisation;
    }

    public String getDepotReference() {
        return depotReference;
    }

    public void setDepotReference(String depotReference) {
        this.depotReference = depotReference;
    }

    public String getDepotNom() {
        return depotNom;
    }

    public void setDepotNom(String depotNom) {
        this.depotNom = depotNom;
    }

    public String getOperateur() {
        return operateur;
    }

    public void setOperateur(String operateur) {
        this.operateur = operateur;
    }

    public int getNBLignes() {
        return NBLignes;
    }

    public void setNBLignes(int NBLignes) {
        this.NBLignes = NBLignes;
    }

    public double getValeur_TTC() {
        return Valeur_TTC;
    }

    public void setValeur_TTC(double valeur_TTC) {
        Valeur_TTC = valeur_TTC;
    }

    public double getValeur_PUMP_TTC() {
        return Valeur_PUMP_TTC;
    }

    public void setValeur_PUMP_TTC(double valeur_PUMP_TTC) {
        Valeur_PUMP_TTC = valeur_PUMP_TTC;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Inventaire)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Inventaire inventaire = (Inventaire) obj;

        if (this.getphiwms_mobileUUID() == inventaire.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getInventaire_ID() > inventaire.getInventaire_ID() ? 1 : -1;
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Inventaire_ID", Inventaire_ID);
            jsonObject.put("Cycle", Cycle);
            jsonObject.put("InventaireDate", InventaireDate);
            jsonObject.put("cycleDateDebut", cycleDateDebut);
            jsonObject.put("_SYS_DT_MAJ", _SYS_DT_MAJ);
            jsonObject.put("_SYS_USER_MAJ", _SYS_USER_MAJ);
            jsonObject.put("_SYS_HEURE_MAJ", _SYS_HEURE_MAJ);
            jsonObject.put("cycleDateFin", cycleDateFin);
            jsonObject.put("clotureActive", clotureActive);
            jsonObject.put("objet", objet);
            jsonObject.put("Mode_Comptabilisation", Mode_Comptabilisation);
            jsonObject.put("depotReference", depotReference);
            jsonObject.put("depotNom", depotNom);
            jsonObject.put("operateur", operateur);
            jsonObject.put("NBLignes", NBLignes);
            jsonObject.put("Valeur_TTC", Valeur_TTC);
            jsonObject.put("Valeur_PUMP_TTC", Valeur_PUMP_TTC);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }
}
