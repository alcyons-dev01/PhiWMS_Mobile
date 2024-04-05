package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Histo_Mvt_StockOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

/**
 * Created by quentinlanusse on 27/06/2017.
 */

public class Histo_Mvt_Stock implements Serializable, Comparable {

    private int HMV_ID;
    private int HMV_PDT_CODE;
    private String HMV_PDT_REF;
    private String HMV_REF_DEPOT;
    private String HMV_TYD_CODE;
    private String HMV_NUM_DOC;
    private String HMV_TYM_CODE;
    private double HMV_QTE;
    private double HMV_STOCK_AVANT;
    private double HMV_STOCK_APRES;
    private double HMV_PRIX_COM;
    private double HMV_PRIX_AVANT;
    private double HMV_PRIX_APRES;
    private String HMV_DT_CREAT;
    private String HMV_DT_MVT;
    private String SYS_DT_MAJ;
    private String SYS_USER_MAJ;
    private String SYS_HEURE_MAJ;
    private String HMV_AAAAMM;
    private String HMV_DepotProduit;
    private Boolean Retour_Frs;
    private String Abreviation_Prescripteur;
    private String Date_Prescription;
    private int Num_Ordonancier;
    private String Prescripteur;
    private int HMV_ID_Commande_Ligne;
    private double HMV_PRIX_Fact;
    private Boolean Liquidé;
    private double HMV_Qté_Fact;
    private double HMV_Total_Qté_Fact;
    private int HMV_Id_Patient;
    private String HMV_Nom_Patient;
    private String HMV_DEPOT_DEST_REF;
    private double TVA;
    private int phiMR4UUID = -1;

    public Histo_Mvt_Stock(int HMV_ID, int HMV_PDT_CODE, String HMV_PDT_REF, String HMV_REF_DEPOT, String HMV_TYD_CODE, String HMV_NUM_DOC, String HMV_TYM_CODE, double HMV_QTE, double HMV_STOCK_AVANT, double HMV_STOCK_APRES, double HMV_PRIX_COM, double HMV_PRIX_AVANT, double HMV_PRIX_APRES, String HMV_DT_CREAT, String HMV_DT_MVT, String SYS_DT_MAJ, String SYS_USER_MAJ, String SYS_HEURE_MAJ, String HMV_AAAAMM, String HMV_DepotProduit, Boolean retour_Frs, String abreviation_Prescripteur, String date_Prescription, int num_Ordonancier, String prescripteur, int HMV_ID_Commande_Ligne, double HMV_PRIX_Fact, Boolean liquidé, double HMV_Qté_Fact, double HMV_Total_Qté_Fact, int HMV_Id_Patient, String HMV_Nom_Patient, String HMV_DEPOT_DEST_REF, double TVA) {
        this.HMV_ID = HMV_ID;
        this.HMV_PDT_CODE = HMV_PDT_CODE;
        this.HMV_PDT_REF = HMV_PDT_REF;
        this.HMV_REF_DEPOT = HMV_REF_DEPOT;
        this.HMV_TYD_CODE = HMV_TYD_CODE;
        this.HMV_NUM_DOC = HMV_NUM_DOC;
        this.HMV_TYM_CODE = HMV_TYM_CODE;
        this.HMV_QTE = HMV_QTE;
        this.HMV_STOCK_AVANT = HMV_STOCK_AVANT;
        this.HMV_STOCK_APRES = HMV_STOCK_APRES;
        this.HMV_PRIX_COM = HMV_PRIX_COM;
        this.HMV_PRIX_AVANT = HMV_PRIX_AVANT;
        this.HMV_PRIX_APRES = HMV_PRIX_APRES;
        this.HMV_DT_CREAT = HMV_DT_CREAT;
        this.HMV_DT_MVT = HMV_DT_MVT;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.HMV_AAAAMM = HMV_AAAAMM;
        this.HMV_DepotProduit = HMV_DepotProduit;
        this.Retour_Frs = retour_Frs;
        this.Abreviation_Prescripteur = abreviation_Prescripteur;
        this.Date_Prescription = date_Prescription;
        this.Num_Ordonancier = num_Ordonancier;
        this.Prescripteur = prescripteur;
        this.HMV_ID_Commande_Ligne = HMV_ID_Commande_Ligne;
        this.HMV_PRIX_Fact = HMV_PRIX_Fact;
        this.Liquidé = liquidé;
        this.HMV_Qté_Fact = HMV_Qté_Fact;
        this.HMV_Total_Qté_Fact = HMV_Total_Qté_Fact;
        this.HMV_Id_Patient = HMV_Id_Patient;
        this.HMV_Nom_Patient = HMV_Nom_Patient;
        this.HMV_DEPOT_DEST_REF = HMV_DEPOT_DEST_REF;
        this.TVA = TVA;
    }

    public Histo_Mvt_Stock(JSONObject jsonObject) {
        try {
            this.HMV_ID = jsonObject.getInt("HMV_ID");
            this.HMV_PDT_CODE = jsonObject.getInt("HMV_PDT_CODE");
            this.HMV_PDT_REF = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_PDT_REF"));
            this.HMV_REF_DEPOT = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_REF_DEPOT"));
            this.HMV_TYD_CODE = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_TYD_CODE"));
            this.HMV_NUM_DOC = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_NUM_DOC"));
            this.HMV_TYM_CODE = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_TYM_CODE"));
            this.HMV_QTE = jsonObject.getDouble("HMV_QTE");
            this.HMV_STOCK_AVANT = jsonObject.getDouble("HMV_STOCK_AVANT");
            this.HMV_STOCK_APRES = jsonObject.getDouble("HMV_STOCK_APRES");
            this.HMV_PRIX_COM = jsonObject.getDouble("HMV_PRIX_COM");
            this.HMV_PRIX_AVANT = jsonObject.getDouble("HMV_PRIX_AVANT");
            this.HMV_PRIX_APRES = jsonObject.getDouble("HMV_PRIX_APRES");
            this.HMV_DT_CREAT = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_DT_CREAT"));
            this.HMV_DT_MVT = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_DT_MVT"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.HMV_AAAAMM = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_AAAAMM"));
            this.HMV_DepotProduit = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_DepotProduit"));
            this.Retour_Frs = OutilsGestionClasses.recupererBooleen(jsonObject, "Retour_Frs");
            this.Abreviation_Prescripteur = OutilsGestionClasses.recupererString(jsonObject.getString("Abreviation_Prescripteur"));
            this.Date_Prescription = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Prescription"));
            this.Num_Ordonancier = jsonObject.getInt("Num_Ordonancier");
            this.Prescripteur = OutilsGestionClasses.recupererString(jsonObject.getString("Prescripteur"));
            this.HMV_ID_Commande_Ligne = jsonObject.getInt("HMV_ID_Commande_Ligne");
            this.HMV_PRIX_Fact = jsonObject.getDouble("HMV_PRIX_Fact");
            this.Liquidé = OutilsGestionClasses.recupererBooleen(jsonObject, "Liquidé");
            this.HMV_Qté_Fact = jsonObject.getDouble("HMV_Qté_Fact");
            this.HMV_Total_Qté_Fact = jsonObject.getDouble("HMV_Total_Qté_Fact");
            this.HMV_Id_Patient = jsonObject.getInt("HMV_Id_Patient");
            this.HMV_Nom_Patient = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_Nom_Patient"));
            this.HMV_DEPOT_DEST_REF = OutilsGestionClasses.recupererString(jsonObject.getString("HMV_DEPOT_DEST_REF"));
            this.TVA = jsonObject.getDouble("TVA");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Histo_Mvt_Stock(Cursor cursor) {
        this.HMV_ID = cursor.getInt(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_ID_HISTO_MVT_STOCK);
        this.HMV_PDT_CODE = cursor.getInt(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_PDT_CODE_HISTO_MVT_STOCK);
        this.HMV_PDT_REF = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_PDT_REF_HISTO_MVT_STOCK);
        this.HMV_REF_DEPOT = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_REF_DEPOT_HISTO_MVT_STOCK);
        this.HMV_TYD_CODE = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_TYD_CODE_HISTO_MVT_STOCK);
        this.HMV_NUM_DOC = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_NUM_DOC_HISTO_MVT_STOCK);
        this.HMV_TYM_CODE = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_TYM_CODE_HISTO_MVT_STOCK);
        this.HMV_QTE = cursor.getDouble(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_QTE_HISTO_MVT_STOCK);
        this.HMV_STOCK_AVANT = cursor.getDouble(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_STOCK_AVANT_HISTO_MVT_STOCK);
        this.HMV_STOCK_APRES = cursor.getDouble(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_STOCK_APRES_HISTO_MVT_STOCK);
        this.HMV_PRIX_COM = cursor.getDouble(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_PRIX_COM_HISTO_MVT_STOCK);
        this.HMV_PRIX_AVANT = cursor.getDouble(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_PRIX_AVANT_HISTO_MVT_STOCK);
        this.HMV_PRIX_APRES = cursor.getDouble(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_PRIX_APRES_HISTO_MVT_STOCK);
        this.HMV_DT_CREAT = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_DT_CREAT_HISTO_MVT_STOCK);
        this.HMV_DT_MVT = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_DT_MVT_HISTO_MVT_STOCK);
        this.SYS_DT_MAJ = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_HISTO_MVT_STOCK);
        this.SYS_USER_MAJ = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_HISTO_MVT_STOCK);
        this.SYS_HEURE_MAJ = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_HISTO_MVT_STOCK);
        this.HMV_AAAAMM = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_AAAAMM_HISTO_MVT_STOCK);
        this.HMV_DepotProduit = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_DEPOTPRODUIT_HISTO_MVT_STOCK);
        this.Retour_Frs = OutilsGestionClasses.recupererBooleen(cursor, Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_RETOUR_FRS_HISTO_MVT_STOCK);
        this.Abreviation_Prescripteur = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_ABREVIATION_PRESCRIPTEUR_HISTO_MVT_STOCK);
        this.Date_Prescription = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_DATE_PRESCRIPTION_HISTO_MVT_STOCK);
        this.Num_Ordonancier = cursor.getInt(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_NUM_ORDONANCIER_HISTO_MVT_STOCK);
        this.Prescripteur = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_PRESCRIPTEUR_HISTO_MVT_STOCK);
        this.HMV_ID_Commande_Ligne = cursor.getInt(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_ID_COMMANDE_LIGNE_HISTO_MVT_STOCK);
        this.HMV_PRIX_Fact = cursor.getDouble(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_PRIX_FACT_HISTO_MVT_STOCK);
        this.Liquidé = OutilsGestionClasses.recupererBooleen(cursor, Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_LIQUIDE_HISTO_MVT_STOCK);
        this.HMV_Qté_Fact = cursor.getDouble(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_QTE_FACT_HISTO_MVT_STOCK);
        this.HMV_Total_Qté_Fact = cursor.getDouble(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_TOTAL_QTE_FACT_HISTO_MVT_STOCK);
        this.HMV_Id_Patient = cursor.getInt(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_ID_PATIENT_HISTO_MVT_STOCK);
        this.HMV_Nom_Patient = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_NOM_PATIENT_HISTO_MVT_STOCK);
        this.HMV_DEPOT_DEST_REF = cursor.getString(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_HMV_DEPOT_DEST_REF_HISTO_MVT_STOCK);
        this.TVA = cursor.getDouble(Histo_Mvt_StockOpenHelper.Constantes.NUM_COL_TVA_HISTO_MVT_STOCK);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getHMV_ID() {
        return HMV_ID;
    }

    public void setHMV_ID(int HMV_ID) {
        this.HMV_ID = HMV_ID;
    }

    public int getHMV_PDT_CODE() {
        return HMV_PDT_CODE;
    }

    public void setHMV_PDT_CODE(int HMV_PDT_CODE) {
        this.HMV_PDT_CODE = HMV_PDT_CODE;
    }

    public String getHMV_PDT_REF() {
        return HMV_PDT_REF;
    }

    public void setHMV_PDT_REF(String HMV_PDT_REF) {
        this.HMV_PDT_REF = HMV_PDT_REF;
    }

    public String getHMV_REF_DEPOT() {
        return HMV_REF_DEPOT;
    }

    public void setHMV_REF_DEPOT(String HMV_REF_DEPOT) {
        this.HMV_REF_DEPOT = HMV_REF_DEPOT;
    }

    public String getHMV_TYD_CODE() {
        return HMV_TYD_CODE;
    }

    public void setHMV_TYD_CODE(String HMV_TYD_CODE) {
        this.HMV_TYD_CODE = HMV_TYD_CODE;
    }

    public String getHMV_NUM_DOC() {
        return HMV_NUM_DOC;
    }

    public void setHMV_NUM_DOC(String HMV_NUM_DOC) {
        this.HMV_NUM_DOC = HMV_NUM_DOC;
    }

    public String getHMV_TYM_CODE() {
        return HMV_TYM_CODE;
    }

    public void setHMV_TYM_CODE(String HMV_TYM_CODE) {
        this.HMV_TYM_CODE = HMV_TYM_CODE;
    }

    public double getHMV_QTE() {
        return HMV_QTE;
    }

    public void setHMV_QTE(double HMV_QTE) {
        this.HMV_QTE = HMV_QTE;
    }

    public double getHMV_STOCK_AVANT() {
        return HMV_STOCK_AVANT;
    }

    public void setHMV_STOCK_AVANT(double HMV_STOCK_AVANT) {
        this.HMV_STOCK_AVANT = HMV_STOCK_AVANT;
    }

    public double getHMV_STOCK_APRES() {
        return HMV_STOCK_APRES;
    }

    public void setHMV_STOCK_APRES(double HMV_STOCK_APRES) {
        this.HMV_STOCK_APRES = HMV_STOCK_APRES;
    }

    public double getHMV_PRIX_COM() {
        return HMV_PRIX_COM;
    }

    public void setHMV_PRIX_COM(double HMV_PRIX_COM) {
        this.HMV_PRIX_COM = HMV_PRIX_COM;
    }

    public double getHMV_PRIX_AVANT() {
        return HMV_PRIX_AVANT;
    }

    public void setHMV_PRIX_AVANT(double HMV_PRIX_AVANT) {
        this.HMV_PRIX_AVANT = HMV_PRIX_AVANT;
    }

    public double getHMV_PRIX_APRES() {
        return HMV_PRIX_APRES;
    }

    public void setHMV_PRIX_APRES(double HMV_PRIX_APRES) {
        this.HMV_PRIX_APRES = HMV_PRIX_APRES;
    }

    public String getHMV_DT_CREAT() {
        return HMV_DT_CREAT;
    }

    public void setHMV_DT_CREAT(String HMV_DT_CREAT) {
        this.HMV_DT_CREAT = HMV_DT_CREAT;
    }

    public String getHMV_DT_MVT() {
        return HMV_DT_MVT;
    }

    public void setHMV_DT_MVT(String HMV_DT_MVT) {
        this.HMV_DT_MVT = HMV_DT_MVT;
    }

    public String getSYS_DT_MAJ() {
        return SYS_DT_MAJ;
    }

    public void setSYS_DT_MAJ(String SYS_DT_MAJ) {
        this.SYS_DT_MAJ = SYS_DT_MAJ;
    }

    public String getSYS_USER_MAJ() {
        return SYS_USER_MAJ;
    }

    public void setSYS_USER_MAJ(String SYS_USER_MAJ) {
        this.SYS_USER_MAJ = SYS_USER_MAJ;
    }

    public String getSYS_HEURE_MAJ() {
        return SYS_HEURE_MAJ;
    }

    public void setSYS_HEURE_MAJ(String SYS_HEURE_MAJ) {
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
    }

    public String getHMV_AAAAMM() {
        return HMV_AAAAMM;
    }

    public void setHMV_AAAAMM(String HMV_AAAAMM) {
        this.HMV_AAAAMM = HMV_AAAAMM;
    }

    public String getHMV_DepotProduit() {
        return HMV_DepotProduit;
    }

    public void setHMV_DepotProduit(String HMV_DepotProduit) {
        this.HMV_DepotProduit = HMV_DepotProduit;
    }

    public Boolean getRetour_Frs() {
        return Retour_Frs;
    }

    public void setRetour_Frs(Boolean retour_Frs) {
        Retour_Frs = retour_Frs;
    }

    public String getAbreviation_Prescripteur() {
        return Abreviation_Prescripteur;
    }

    public void setAbreviation_Prescripteur(String abreviation_Prescripteur) {
        Abreviation_Prescripteur = abreviation_Prescripteur;
    }

    public String getDate_Prescription() {
        return Date_Prescription;
    }

    public void setDate_Prescription(String date_Prescription) {
        Date_Prescription = date_Prescription;
    }

    public int getNum_Ordonancier() {
        return Num_Ordonancier;
    }

    public void setNum_Ordonancier(int num_Ordonancier) {
        Num_Ordonancier = num_Ordonancier;
    }

    public String getPrescripteur() {
        return Prescripteur;
    }

    public void setPrescripteur(String prescripteur) {
        Prescripteur = prescripteur;
    }

    public int getHMV_ID_Commande_Ligne() {
        return HMV_ID_Commande_Ligne;
    }

    public void setHMV_ID_Commande_Ligne(int HMV_ID_Commande_Ligne) {
        this.HMV_ID_Commande_Ligne = HMV_ID_Commande_Ligne;
    }

    public double getHMV_PRIX_Fact() {
        return HMV_PRIX_Fact;
    }

    public void setHMV_PRIX_Fact(double HMV_PRIX_Fact) {
        this.HMV_PRIX_Fact = HMV_PRIX_Fact;
    }

    public Boolean getLiquidé() {
        return Liquidé;
    }

    public void setLiquidé(Boolean liquidé) {
        Liquidé = liquidé;
    }

    public double getHMV_Qté_Fact() {
        return HMV_Qté_Fact;
    }

    public void setHMV_Qté_Fact(double HMV_Qté_Fact) {
        this.HMV_Qté_Fact = HMV_Qté_Fact;
    }

    public double getHMV_Total_Qté_Fact() {
        return HMV_Total_Qté_Fact;
    }

    public void setHMV_Total_Qté_Fact(double HMV_Total_Qté_Fact) {
        this.HMV_Total_Qté_Fact = HMV_Total_Qté_Fact;
    }

    public int getHMV_Id_Patient() {
        return HMV_Id_Patient;
    }

    public void setHMV_Id_Patient(int HMV_Id_Patient) {
        this.HMV_Id_Patient = HMV_Id_Patient;
    }

    public String getHMV_Nom_Patient() {
        return HMV_Nom_Patient;
    }

    public void setHMV_Nom_Patient(String HMV_Nom_Patient) {
        this.HMV_Nom_Patient = HMV_Nom_Patient;
    }

    public String getHMV_DEPOT_DEST_REF() {
        return HMV_DEPOT_DEST_REF;
    }

    public void setHMV_DEPOT_DEST_REF(String HMV_DEPOT_DEST_REF) {
        this.HMV_DEPOT_DEST_REF = HMV_DEPOT_DEST_REF;
    }

    public double getTVA() {
        return TVA;
    }

    public void setTVA(double TVA) {
        this.TVA = TVA;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("HMV_ID", HMV_ID);
            jsonObject.put("HMV_PDT_CODE", HMV_PDT_CODE);
            jsonObject.put("HMV_PDT_REF", HMV_PDT_REF);
            jsonObject.put("HMV_REF_DEPOT", HMV_REF_DEPOT);
            jsonObject.put("HMV_TYD_CODE", HMV_TYD_CODE);
            jsonObject.put("HMV_NUM_DOC", HMV_NUM_DOC);
            jsonObject.put("HMV_TYM_CODE", HMV_TYM_CODE);
            jsonObject.put("HMV_QTE", HMV_QTE);
            jsonObject.put("HMV_STOCK_AVANT", HMV_STOCK_AVANT);
            jsonObject.put("HMV_STOCK_APRES", HMV_STOCK_APRES);
            jsonObject.put("HMV_PRIX_COM", HMV_PRIX_COM);
            jsonObject.put("HMV_PRIX_AVANT", HMV_PRIX_AVANT);
            jsonObject.put("HMV_PRIX_APRES", HMV_PRIX_APRES);
            jsonObject.put("HMV_DT_CREAT", HMV_DT_CREAT);
            jsonObject.put("HMV_DT_MVT", HMV_DT_MVT);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("HMV_AAAAMM", HMV_AAAAMM);
            jsonObject.put("HMV_DepotProduit", HMV_DepotProduit);
            jsonObject.put("Retour_Frs", Retour_Frs);
            jsonObject.put("Abreviation_Prescripteur", Abreviation_Prescripteur);
            jsonObject.put("Date_Prescription", Date_Prescription);
            jsonObject.put("Num_Ordonancier", Num_Ordonancier);
            jsonObject.put("Prescripteur", Prescripteur);
            jsonObject.put("HMV_ID_Commande_Ligne", HMV_ID_Commande_Ligne);
            jsonObject.put("HMV_PRIX_Fact", HMV_PRIX_Fact);
            jsonObject.put("Liquidé", Liquidé);
            jsonObject.put("HMV_Qté_Fact", HMV_Qté_Fact);
            jsonObject.put("HMV_Total_Qté_Fact", HMV_Total_Qté_Fact);
            jsonObject.put("HMV_Id_Patient", HMV_Id_Patient);
            jsonObject.put("HMV_Nom_Patient", HMV_Nom_Patient);
            jsonObject.put("HMV_DEPOT_DEST_REF", HMV_DEPOT_DEST_REF);
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

        if (!(obj instanceof Histo_Mvt_Stock)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Histo_Mvt_Stock histo_mvt_stock = (Histo_Mvt_Stock) obj;

        if (this.getPhiMR4UUID() == histo_mvt_stock.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getHMV_ID() > histo_mvt_stock.getHMV_ID() ? 1 : -1;
        }
    }
}
