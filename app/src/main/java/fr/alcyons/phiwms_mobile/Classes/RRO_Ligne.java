package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class RRO_Ligne implements Serializable, Comparable {

    private int RRO_UID;
    private String Ref_Frs;
    private String Type_Pdt;
    private double Quantite;
    private double PU;
    private String Designation;
    private int Code_Pdt;
    private String Categorie;
    private String Unite;
    private double Conditionnement;
    private int Code_Frs;
    private String Fournisseur;
    private double Montant_HT;
    private int _UID;
    private double Tx_TVA;
    private double Mt_TTC_Ligne;
    private String Devise;
    private int ID_Mvt;
    private int Classe_N;
    private int phiwms_mobileUUID = -1;

    public RRO_Ligne(int RRO_UID, String ref_Frs, String type_Pdt, double quantite, double PU, String designation, int code_Pdt, String categorie, String unite, double conditionnement, int code_Frs, String fournisseur, double montant_HT, int _UID, double tx_TVA, double mt_TTC_Ligne, String devise, int ID_Mvt, int classe_N) {
        this.RRO_UID = RRO_UID;
        this.Ref_Frs = ref_Frs;
        this.Type_Pdt = type_Pdt;
        this.Quantite = quantite;
        this.PU = PU;
        this.Designation = designation;
        this.Code_Pdt = code_Pdt;
        this.Categorie = categorie;
        this.Unite = unite;
        this.Conditionnement = conditionnement;
        this.Code_Frs = code_Frs;
        this.Fournisseur = fournisseur;
        this.Montant_HT = montant_HT;
        this._UID = _UID;
        this.Tx_TVA = tx_TVA;
        this.Mt_TTC_Ligne = mt_TTC_Ligne;
        this.Devise = devise;
        this.ID_Mvt = ID_Mvt;
        this.Classe_N = classe_N;
    }

    public RRO_Ligne(JSONObject jsonObject) {
        try {
            this.RRO_UID = jsonObject.getInt("RRO_UID");
            this.Ref_Frs = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Frs"));
            this.Type_Pdt = OutilsGestionClasses.recupererString(jsonObject.getString("Type_Pdt"));
            this.Quantite = jsonObject.getDouble("Quantite");
            this.PU = jsonObject.getDouble("PU");
            this.Designation = OutilsGestionClasses.recupererString(jsonObject.getString("Designation"));
            this.Code_Pdt = jsonObject.getInt("Code_Pdt");
            this.Categorie = OutilsGestionClasses.recupererString(jsonObject.getString("Categorie"));
            this.Unite = OutilsGestionClasses.recupererString(jsonObject.getString("Unite"));
            this.Conditionnement = jsonObject.getDouble("Conditionnement");
            this.Code_Frs = jsonObject.getInt("Code_Frs");
            this.Fournisseur = OutilsGestionClasses.recupererString(jsonObject.getString("Fournisseur"));
            this.Montant_HT = jsonObject.getDouble("Montant_HT");
            this._UID = jsonObject.getInt("_UID");
            this.Tx_TVA = jsonObject.getDouble("Tx_TVA");
            this.Mt_TTC_Ligne = jsonObject.getDouble("Mt_TTC_Ligne");
            this.Devise = OutilsGestionClasses.recupererString(jsonObject.getString("Devise"));
            this.ID_Mvt = jsonObject.getInt("ID_Mvt");
            this.Classe_N = jsonObject.getInt("Classe_N");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public RRO_Ligne(Cursor cursor) {
        this.RRO_UID = cursor.getInt(RRO_LigneOpenHelper.Constantes.NUM_COL_RRO_UID_RRO_LIGNE);
        this.Ref_Frs = cursor.getString(RRO_LigneOpenHelper.Constantes.NUM_COL_REF_FRS_RRO_LIGNE);
        this.Type_Pdt = cursor.getString(RRO_LigneOpenHelper.Constantes.NUM_COL_TYPE_PDT_RRO_LIGNE);
        this.Quantite = cursor.getDouble(RRO_LigneOpenHelper.Constantes.NUM_COL_QUANTITE_RRO_LIGNE);
        this.PU = cursor.getDouble(RRO_LigneOpenHelper.Constantes.NUM_COL_PU_RRO_LIGNE);
        this.Designation = cursor.getString(RRO_LigneOpenHelper.Constantes.NUM_COL_DESIGNATION_RRO_LIGNE);
        this.Code_Pdt = cursor.getInt(RRO_LigneOpenHelper.Constantes.NUM_COL_CODE_PDT_RRO_LIGNE);
        this.Categorie = cursor.getString(RRO_LigneOpenHelper.Constantes.NUM_COL_CATEGORIE_RRO_LIGNE);
        this.Unite = cursor.getString(RRO_LigneOpenHelper.Constantes.NUM_COL_UNITE_RRO_LIGNE);
        this.Conditionnement = cursor.getDouble(RRO_LigneOpenHelper.Constantes.NUM_COL_CONDITIONNEMENT_RRO_LIGNE);
        this.Code_Frs = cursor.getInt(RRO_LigneOpenHelper.Constantes.NUM_COL_CODE_FRS_RRO_LIGNE);
        this.Fournisseur = cursor.getString(RRO_LigneOpenHelper.Constantes.NUM_COL_FOURNISSEUR_RRO_LIGNE);
        this.Montant_HT = cursor.getDouble(RRO_LigneOpenHelper.Constantes.NUM_COL_MONTANT_HT_RRO_LIGNE);
        this._UID = cursor.getInt(RRO_LigneOpenHelper.Constantes.NUM_COL__UID_RRO_LIGNE);
        this.Tx_TVA = cursor.getDouble(RRO_LigneOpenHelper.Constantes.NUM_COL_TX_TVA_RRO_LIGNE);
        this.Mt_TTC_Ligne = cursor.getDouble(RRO_LigneOpenHelper.Constantes.NUM_COL_MT_TTC_LIGNE_RRO_LIGNE);
        this.Devise = cursor.getString(RRO_LigneOpenHelper.Constantes.NUM_COL_DEVISE_RRO_LIGNE);
        this.ID_Mvt = cursor.getInt(RRO_LigneOpenHelper.Constantes.NUM_COL_ID_MVT_RRO_LIGNE);
        this.Classe_N = cursor.getInt(RRO_LigneOpenHelper.Constantes.NUM_COL_CLASSE_N_RRO_LIGNE);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getRRO_UID() {
        return RRO_UID;
    }

    public void setRRO_UID(int RRO_UID) {
        this.RRO_UID = RRO_UID;
    }

    public String getRef_Frs() {
        return Ref_Frs;
    }

    public void setRef_Frs(String ref_Frs) {
        Ref_Frs = ref_Frs;
    }

    public String getType_Pdt() {
        return Type_Pdt;
    }

    public void setType_Pdt(String type_Pdt) {
        Type_Pdt = type_Pdt;
    }

    public double getQuantite() {
        return Quantite;
    }

    public void setQuantite(double quantite) {
        Quantite = quantite;
    }

    public double getPU() {
        return PU;
    }

    public void setPU(double PU) {
        this.PU = PU;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public int getCode_Pdt() {
        return Code_Pdt;
    }

    public void setCode_Pdt(int code_Pdt) {
        Code_Pdt = code_Pdt;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public String getUnite() {
        return Unite;
    }

    public void setUnite(String unite) {
        Unite = unite;
    }

    public double getConditionnement() {
        return Conditionnement;
    }

    public void setConditionnement(double conditionnement) {
        Conditionnement = conditionnement;
    }

    public int getCode_Frs() {
        return Code_Frs;
    }

    public void setCode_Frs(int code_Frs) {
        Code_Frs = code_Frs;
    }

    public String getFournisseur() {
        return Fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        Fournisseur = fournisseur;
    }

    public double getMontant_HT() {
        return Montant_HT;
    }

    public void setMontant_HT(double montant_HT) {
        Montant_HT = montant_HT;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public double getTx_TVA() {
        return Tx_TVA;
    }

    public void setTx_TVA(double tx_TVA) {
        Tx_TVA = tx_TVA;
    }

    public double getMt_TTC_Ligne() {
        return Mt_TTC_Ligne;
    }

    public void setMt_TTC_Ligne(double mt_TTC_Ligne) {
        Mt_TTC_Ligne = mt_TTC_Ligne;
    }

    public String getDevise() {
        return Devise;
    }

    public void setDevise(String devise) {
        Devise = devise;
    }

    public int getID_Mvt() {
        return ID_Mvt;
    }

    public void setID_Mvt(int ID_Mvt) {
        this.ID_Mvt = ID_Mvt;
    }

    public int getClasse_N() {
        return Classe_N;
    }

    public void setClasse_N(int classe_N) {
        Classe_N = classe_N;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RRO_UID", RRO_UID);
            jsonObject.put("Ref_Frs", Ref_Frs);
            jsonObject.put("Type_Pdt", Type_Pdt);
            jsonObject.put("Quantite", Quantite);
            jsonObject.put("PU", PU);
            jsonObject.put("Designation", Designation);
            jsonObject.put("Code_Pdt", Code_Pdt);
            jsonObject.put("Categorie", Categorie);
            jsonObject.put("Unite", Unite);
            jsonObject.put("Conditionnement", Conditionnement);
            jsonObject.put("Code_Frs", Code_Frs);
            jsonObject.put("Fournisseur", Fournisseur);
            jsonObject.put("Montant_HT", Montant_HT);
            jsonObject.put("_UID", _UID);
            jsonObject.put("Tx_TVA", Tx_TVA);
            jsonObject.put("Mt_TTC_Ligne", Mt_TTC_Ligne);
            jsonObject.put("Devise", Devise);
            jsonObject.put("ID_Mvt", ID_Mvt);
            jsonObject.put("Classe_N", Classe_N);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof RRO_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        RRO_Ligne rro_ligne = (RRO_Ligne) obj;

        if (this.get_UID() == rro_ligne.get_UID()) {
            return 0;
        } else {
            return this.get_UID() > rro_ligne.get_UID() ? 1 : -1;
        }
    }
}
