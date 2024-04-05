package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by jessica on 02/10/2017.
 */

public class Dotation_Patient implements Serializable, Comparable {

    private int protocolePatient_UID;
    private int Code_Produit;
    private String Designation;
    private int Cond;
    private double Qté;
    private String Ref_four;
    private String Categorie;
    private boolean Livraison_Directe;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private int PU_HT;
    private int PU_TTC;
    private int Montant_TTC_Ligne;
    private int Montant_HT_Ligne;
    private double Qte_Commande;
    private int _UID;
    private int phiwms_mobileUUID = -1;

    public Dotation_Patient(JSONObject jsonObject) {
        try {
            this.protocolePatient_UID = jsonObject.getInt("protocolePatient_UID");
            this.Code_Produit = jsonObject.getInt("Code_Produit");
            this.Designation = OutilsGestionClasses.recupererString(jsonObject.getString("Designation"));
            this.Cond = jsonObject.getInt("Cond");
            this.Qté = jsonObject.getDouble("Qté");
            this.Ref_four = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_four"));
            this.Categorie = OutilsGestionClasses.recupererString(jsonObject.getString("Categorie"));
            this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(jsonObject, "Livraison_Directe");
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.PU_HT = jsonObject.getInt("PU_HT");
            this.PU_TTC = jsonObject.getInt("PU_TTC");
            this.Montant_TTC_Ligne = jsonObject.getInt("Montant_TTC_Ligne");
            this.Montant_HT_Ligne = jsonObject.getInt("Montant_HT_Ligne");
            this.Qte_Commande = jsonObject.getDouble("Qte_Commande");
            this._UID = jsonObject.getInt("_UID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Dotation_Patient(Cursor cursor) {
        this.protocolePatient_UID = cursor.getInt(Dotation_PatientOpenHelper.Constantes.NUM_COL_PROTOCOLEPATIENT_UID_DOTATION_PATIENT);
        this.Code_Produit = cursor.getInt(Dotation_PatientOpenHelper.Constantes.NUM_COL_CODE_PRODUIT_DOTATION_PATIENT);
        this.Designation = cursor.getString(Dotation_PatientOpenHelper.Constantes.NUM_COL_DESIGNATION_DOTATION_PATIENT);
        this.Cond = cursor.getInt(Dotation_PatientOpenHelper.Constantes.NUM_COL_COND_DOTATION_PATIENT);
        this.Qté = cursor.getDouble(Dotation_PatientOpenHelper.Constantes.NUM_COL_QTE_DOTATION_PATIENT);
        this.Ref_four = cursor.getString(Dotation_PatientOpenHelper.Constantes.NUM_COL_REF_FOUR_DOTATION_PATIENT);
        this.Categorie = cursor.getString(Dotation_PatientOpenHelper.Constantes.NUM_COL_CATEGORIE_DOTATION_PATIENT);
        this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(cursor, Dotation_PatientOpenHelper.Constantes.NUM_COL_LIVRAISON_DIRECTE_DOTATION_PATIENT);
        this.SYS_DT_MAJ = cursor.getString(Dotation_PatientOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_DOTATION_PATIENT);
        this.SYS_HEURE_MAJ = cursor.getString(Dotation_PatientOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_DOTATION_PATIENT);
        this.SYS_USER_MAJ = cursor.getString(Dotation_PatientOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_DOTATION_PATIENT);
        this.PU_HT = cursor.getInt(Dotation_PatientOpenHelper.Constantes.NUM_COL_PU_HT_DOTATION_PATIENT);
        this.PU_TTC = cursor.getInt(Dotation_PatientOpenHelper.Constantes.NUM_COL_PU_TTC_DOTATION_PATIENT);
        this.Montant_TTC_Ligne = cursor.getInt(Dotation_PatientOpenHelper.Constantes.NUM_COL_MONTANT_TTC_LIGNE_DOTATION_PATIENT);
        this.Montant_HT_Ligne = cursor.getInt(Dotation_PatientOpenHelper.Constantes.NUM_COL_MONTANT_HT_LIGNE_DOTATION_PATIENT);
        this.Qte_Commande = cursor.getDouble(Dotation_PatientOpenHelper.Constantes.NUM_COL_QTE_COMMANDE_DOTATION_PATIENT);
        this._UID = cursor.getInt(Dotation_PatientOpenHelper.Constantes.NUM_COL__UID_DOTATION_PATIENT);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getProtocolePatient_UID() {
        return protocolePatient_UID;
    }

    public void setProtocolePatient_UID(int protocolePatient_UID) {
        this.protocolePatient_UID = protocolePatient_UID;
    }

    public int getCode_Produit() {
        return Code_Produit;
    }

    public void setCode_Produit(int code_Produit) {
        Code_Produit = code_Produit;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public int getCond() {
        return Cond;
    }

    public void setCond(int cond) {
        Cond = cond;
    }

    public double getQté() {
        return Qté;
    }

    public void setQté(double qté) {
        Qté = qté;
    }

    public String getRef_four() {
        return Ref_four;
    }

    public void setRef_four(String ref_four) {
        Ref_four = ref_four;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public boolean isLivraison_Directe() {
        return Livraison_Directe;
    }

    public void setLivraison_Directe(boolean livraison_Directe) {
        Livraison_Directe = livraison_Directe;
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

    public int getPU_HT() {
        return PU_HT;
    }

    public void setPU_HT(int PU_HT) {
        this.PU_HT = PU_HT;
    }

    public int getPU_TTC() {
        return PU_TTC;
    }

    public void setPU_TTC(int PU_TTC) {
        this.PU_TTC = PU_TTC;
    }

    public int getMontant_TTC_Ligne() {
        return Montant_TTC_Ligne;
    }

    public void setMontant_TTC_Ligne(int montant_TTC_Ligne) {
        Montant_TTC_Ligne = montant_TTC_Ligne;
    }

    public int getMontant_HT_Ligne() {
        return Montant_HT_Ligne;
    }

    public void setMontant_HT_Ligne(int montant_HT_Ligne) {
        Montant_HT_Ligne = montant_HT_Ligne;
    }

    public double getQte_Commande() {
        return Qte_Commande;
    }

    public void setQte_Commande(double qte_Commande) {
        Qte_Commande = qte_Commande;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
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
            jsonObject.put("protocolePatient_UID", protocolePatient_UID);
            jsonObject.put("Code_Produit", Code_Produit);
            jsonObject.put("Designation", Designation);
            jsonObject.put("Cond", Cond);
            jsonObject.put("Qté", Qté);
            jsonObject.put("Ref_four", Ref_four);
            jsonObject.put("Categorie", Categorie);
            jsonObject.put("Livraison_Directe", Livraison_Directe);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("PU_HT", PU_HT);
            jsonObject.put("PU_TTC", PU_TTC);
            jsonObject.put("Montant_TTC_Ligne", Montant_TTC_Ligne);
            jsonObject.put("Montant_HT_Ligne", Montant_HT_Ligne);
            jsonObject.put("Qte_Commande", Qte_Commande);
            jsonObject.put("_UID", _UID);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }


    @Override
    public String toString() {
        return "Dotation_Patient{" +
                "protocolePatient_UID=" + protocolePatient_UID +
                ", Code_Produit=" + Code_Produit +
                ", Designation='" + Designation + '\'' +
                ", Cond=" + Cond +
                ", Qté=" + Qté +
                ", Ref_four='" + Ref_four + '\'' +
                ", Categorie='" + Categorie + '\'' +
                ", Livraison_Directe=" + Livraison_Directe +
                ", SYS_DT_MAJ='" + SYS_DT_MAJ + '\'' +
                ", SYS_HEURE_MAJ='" + SYS_HEURE_MAJ + '\'' +
                ", SYS_USER_MAJ='" + SYS_USER_MAJ + '\'' +
                ", PU_HT=" + PU_HT +
                ", PU_TTC=" + PU_TTC +
                ", Montant_TTC_Ligne=" + Montant_TTC_Ligne +
                ", Montant_HT_Ligne=" + Montant_HT_Ligne +
                ", Qte_Commande=" + Qte_Commande +
                ", _UID=" + _UID +
                ", phiwms_mobileUUID=" + phiwms_mobileUUID +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        Dotation_Patient dotation_patient = (Dotation_Patient) obj;

        if (this.getphiwms_mobileUUID() == dotation_patient.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getphiwms_mobileUUID() > dotation_patient.getphiwms_mobileUUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((Dotation_Patient) obj).getphiwms_mobileUUID() == this.getphiwms_mobileUUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Dotation_Patient)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }
}
