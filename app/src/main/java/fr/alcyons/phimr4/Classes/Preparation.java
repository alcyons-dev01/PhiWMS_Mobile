package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PreparationOpenHelper;
import fr.alcyons.phimr4.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Preparation implements Serializable, Comparable {

    private int ID;
    private String Ref_depot;
    private String Cycle;
    private int ID_Cycle;
    private int Annee;
    private int Mois;
    private double Montant_HT;
    private double Montant_TTC;
    private Boolean Validée;
    private Boolean Proposée;
    private String Cycle_Depot;
    private Boolean Domicile;
    private String Statut;
    private String Date_inventaire;
    private String Date_Livraison2;
    private double Montant_TVA;
    private int ID_Depot;
    private String Num_Bon_Prev;
    private String Date_Livraison1;
    private Boolean Preparation_Nominative;
    private String IPP_Patient;
    private String Nom_Patient;
    private String CB_Bon_Commande_Patient;
    private String Date_Prevision;
    private String Date_Livraison3;
    private String Date_Livraison4;
    private String Date_Livraison5;
    private int phiMR4UUID = -1;


    public Preparation(int ID, String ref_depot, String cycle, int ID_Cycle, int annee, int mois, double montant_HT, double montant_TTC, Boolean validée, Boolean proposée, String cycle_Depot, Boolean domicile, String statut, String date_inventaire, String date_Livraison2, double montant_TVA, int ID_Depot, String num_Bon_Prev, String date_Livraison1, Boolean preparation_Nominative, String IPP_Patient, String nom_Patient, String CB_Bon_Commande_Patient, String date_Prevision, String date_Livraison3, String date_Livraison4, String date_Livraison5) {
        this.ID = ID;
        this.Ref_depot = ref_depot;
        this.Cycle = cycle;
        this.ID_Cycle = ID_Cycle;
        this.Annee = annee;
        this.Mois = mois;
        this.Montant_HT = montant_HT;
        this.Montant_TTC = montant_TTC;
        this.Validée = validée;
        this.Proposée = proposée;
        this.Cycle_Depot = cycle_Depot;
        this.Domicile = domicile;
        this.Statut = statut;
        this.Date_inventaire = date_inventaire;
        this.Date_Livraison2 = date_Livraison2;
        this.Montant_TVA = montant_TVA;
        this.ID_Depot = ID_Depot;
        this.Num_Bon_Prev = num_Bon_Prev;
        this.Date_Livraison1 = date_Livraison1;
        this.Preparation_Nominative = preparation_Nominative;
        this.IPP_Patient = IPP_Patient;
        this.Nom_Patient = nom_Patient;
        this.CB_Bon_Commande_Patient = CB_Bon_Commande_Patient;
        this.Date_Prevision = date_Prevision;
        this.Date_Livraison3 = date_Livraison3;
        this.Date_Livraison4 = date_Livraison4;
        this.Date_Livraison5 = date_Livraison5;
    }

    public Preparation() {
    }

    public Preparation(JSONObject jsonObject) {
        try {
            this.ID = jsonObject.getInt("ID");
            this.Ref_depot = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_depot"));
            this.Cycle = OutilsGestionClasses.recupererString(jsonObject.getString("Cycle"));
            this.ID_Cycle = jsonObject.getInt("ID_Cycle");
            this.Annee = jsonObject.getInt("Annee");
            this.Mois = jsonObject.getInt("Mois");
            this.Montant_HT = jsonObject.getDouble("Montant_HT");
            this.Montant_TTC = jsonObject.getDouble("Montant_TTC");
            this.Validée = OutilsGestionClasses.recupererBooleen(jsonObject, "Validée");
            this.Proposée = OutilsGestionClasses.recupererBooleen(jsonObject, "Proposée");
            this.Cycle_Depot = OutilsGestionClasses.recupererString(jsonObject.getString("Cycle_Depot"));
            this.Domicile = OutilsGestionClasses.recupererBooleen(jsonObject, "Domicile");
            this.Statut = OutilsGestionClasses.recupererString(jsonObject.getString("Statut"));
            this.Date_inventaire = OutilsGestionClasses.recupererString(jsonObject.getString("Date_inventaire"));
            this.Date_Livraison2 = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Livraison2"));
            this.Montant_TVA = jsonObject.getDouble("Montant_TVA");
            this.ID_Depot = jsonObject.getInt("ID_Depot");
            this.Num_Bon_Prev = OutilsGestionClasses.recupererString(jsonObject.getString("Num_Bon_Prev"));
            this.Date_Livraison1 = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Livraison1"));
            this.Preparation_Nominative = OutilsGestionClasses.recupererBooleen(jsonObject, "Preparation_Nominative");
            this.IPP_Patient = OutilsGestionClasses.recupererString(jsonObject.getString("IPP_Patient"));
            this.Nom_Patient = OutilsGestionClasses.recupererString(jsonObject.getString("Nom_Patient"));
            this.CB_Bon_Commande_Patient = OutilsGestionClasses.recupererString(jsonObject.getString("CB_Bon_Commande_Patient"));
            this.Date_Prevision = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Prevision"));
            this.Date_Livraison3 = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Livraison3"));
            this.Date_Livraison4 = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Livraison4"));
            this.Date_Livraison5 = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Livraison5"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Preparation(Cursor cursor) {
        this.ID = cursor.getInt(PreparationOpenHelper.Constantes.NUM_COL_ID_PREPARATION);
        this.Ref_depot = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_REF_DEPOT_PREPARATION);
        this.Cycle = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_CYCLE_PREPARATION);
        this.ID_Cycle = cursor.getInt(PreparationOpenHelper.Constantes.NUM_COL_ID_CYCLE_PREPARATION);
        this.Annee = cursor.getInt(PreparationOpenHelper.Constantes.NUM_COL_ANNEE_PREPARATION);
        this.Mois = cursor.getInt(PreparationOpenHelper.Constantes.NUM_COL_MOIS_PREPARATION);
        this.Montant_HT = cursor.getDouble(PreparationOpenHelper.Constantes.NUM_COL_MONTANT_HT_PREPARATION);
        this.Montant_TTC = cursor.getDouble(PreparationOpenHelper.Constantes.NUM_COL_MONTANT_TTC_PREPARATION);
        this.Validée = OutilsGestionClasses.recupererBooleen(cursor, PreparationOpenHelper.Constantes.NUM_COL_VALIDEE_PREPARATION);
        this.Proposée = OutilsGestionClasses.recupererBooleen(cursor, PreparationOpenHelper.Constantes.NUM_COL_PROPOSEE_PREPARATION);
        this.Cycle_Depot = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_CYCLE_DEPOT_PREPARATION);
        this.Domicile = OutilsGestionClasses.recupererBooleen(cursor, PreparationOpenHelper.Constantes.NUM_COL_DOMICILE_PREPARATION);
        this.Statut = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_STATUT_PREPARATION);
        this.Date_inventaire = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_DATE_INVENTAIRE_PREPARATION);
        this.Date_Livraison2 = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_DATE_LIVRAISON2_PREPARATION);
        this.Montant_TVA = cursor.getDouble(PreparationOpenHelper.Constantes.NUM_COL_MONTANT_TVA_PREPARATION);
        this.ID_Depot = cursor.getInt(PreparationOpenHelper.Constantes.NUM_COL_ID_DEPOT_PREPARATION);
        this.Num_Bon_Prev = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_NUM_BON_PREV_PREPARATION);
        this.Date_Livraison1 = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_DATE_LIVRAISON1_PREPARATION);
        this.Preparation_Nominative = OutilsGestionClasses.recupererBooleen(cursor, PreparationOpenHelper.Constantes.NUM_COL_PREPARATION_NOMINATIVE_PREPARATION);
        this.IPP_Patient = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_IPP_PATIENT_PREPARATION);
        this.Nom_Patient = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_NOM_PATIENT_PREPARATION);
        this.CB_Bon_Commande_Patient = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_CB_BON_COMMANDE_PATIENT_PREPARATION);
        this.Date_Prevision = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_DATE_PREVISION_PREPARATION);
        this.Date_Livraison3 = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_DATE_LIVRAISON3_PREPARATION);
        this.Date_Livraison4 = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_DATE_LIVRAISON4_PREPARATION);
        this.Date_Livraison5 = cursor.getString(PreparationOpenHelper.Constantes.NUM_COL_DATE_LIVRAISON5_PREPARATION);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getRef_depot() {
        return Ref_depot;
    }

    public void setRef_depot(String ref_depot) {
        Ref_depot = ref_depot;
    }

    public String getCycle() {
        return Cycle;
    }

    public void setCycle(String cycle) {
        Cycle = cycle;
    }

    public int getID_Cycle() {
        return ID_Cycle;
    }

    public void setID_Cycle(int ID_Cycle) {
        this.ID_Cycle = ID_Cycle;
    }

    public int getAnnee() {
        return Annee;
    }

    public void setAnnee(int annee) {
        Annee = annee;
    }

    public int getMois() {
        return Mois;
    }

    public void setMois(int mois) {
        Mois = mois;
    }

    public double getMontant_HT() {
        return Montant_HT;
    }

    public void setMontant_HT(double montant_HT) {
        Montant_HT = montant_HT;
    }

    public double getMontant_TTC() {
        return Montant_TTC;
    }

    public void setMontant_TTC(double montant_TTC) {
        Montant_TTC = montant_TTC;
    }

    public Boolean getValidée() {
        return Validée;
    }

    public void setValidée(Boolean validée) {
        Validée = validée;
    }

    public Boolean getProposée() {
        return Proposée;
    }

    public void setProposée(Boolean proposée) {
        Proposée = proposée;
    }

    public String getCycle_Depot() {
        return Cycle_Depot;
    }

    public void setCycle_Depot(String cycle_Depot) {
        Cycle_Depot = cycle_Depot;
    }

    public Boolean getDomicile() {
        return Domicile;
    }

    public void setDomicile(Boolean domicile) {
        Domicile = domicile;
    }

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }

    public String getDate_inventaire() {
        return Date_inventaire;
    }

    public void setDate_inventaire(String date_inventaire) {
        Date_inventaire = date_inventaire;
    }

    public String getDate_Livraison2() {
        return Date_Livraison2;
    }

    public void setDate_Livraison2(String date_Livraison2) {
        Date_Livraison2 = date_Livraison2;
    }

    public double getMontant_TVA() {
        return Montant_TVA;
    }

    public void setMontant_TVA(double montant_TVA) {
        Montant_TVA = montant_TVA;
    }

    public int getID_Depot() {
        return ID_Depot;
    }

    public void setID_Depot(int ID_Depot) {
        this.ID_Depot = ID_Depot;
    }

    public String getNum_Bon_Prev() {
        return Num_Bon_Prev;
    }

    public void setNum_Bon_Prev(String num_Bon_Prev) {
        Num_Bon_Prev = num_Bon_Prev;
    }

    public String getDate_Livraison1() {
        return Date_Livraison1;
    }

    public void setDate_Livraison1(String date_Livraison1) {
        Date_Livraison1 = date_Livraison1;
    }

    public Boolean getPreparation_Nominative() {
        return Preparation_Nominative;
    }

    public void setPreparation_Nominative(Boolean preparation_Nominative) {
        Preparation_Nominative = preparation_Nominative;
    }

    public String getIPP_Patient() {
        return IPP_Patient;
    }

    public void setIPP_Patient(String IPP_Patient) {
        this.IPP_Patient = IPP_Patient;
    }

    public String getNom_Patient() {
        return Nom_Patient;
    }

    public void setNom_Patient(String nom_Patient) {
        Nom_Patient = nom_Patient;
    }

    public String getCB_Bon_Commande_Patient() {
        return CB_Bon_Commande_Patient;
    }

    public void setCB_Bon_Commande_Patient(String CB_Bon_Commande_Patient) {
        this.CB_Bon_Commande_Patient = CB_Bon_Commande_Patient;
    }

    public String getDate_Prevision() {
        return Date_Prevision;
    }

    public void setDate_Prevision(String date_Prevision) {
        Date_Prevision = date_Prevision;
    }

    public String getDate_Livraison3() {
        return Date_Livraison3;
    }

    public void setDate_Livraison3(String date_Livraison3) {
        Date_Livraison3 = date_Livraison3;
    }

    public String getDate_Livraison4() {
        return Date_Livraison4;
    }

    public void setDate_Livraison4(String date_Livraison4) {
        Date_Livraison4 = date_Livraison4;
    }

    public String getDate_Livraison5() {
        return Date_Livraison5;
    }

    public void setDate_Livraison5(String date_Livraison5) {
        Date_Livraison5 = date_Livraison5;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Preparation)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", ID);
            jsonObject.put("Ref_depot", Ref_depot);
            jsonObject.put("Cycle", Cycle);
            jsonObject.put("ID_Cycle", ID_Cycle);
            jsonObject.put("Annee", Annee);
            jsonObject.put("Mois", Mois);
            jsonObject.put("Montant_HT", Montant_HT);
            jsonObject.put("Montant_TTC", Montant_TTC);
            jsonObject.put("Validée", Validée);
            jsonObject.put("Proposée", Proposée);
            jsonObject.put("Cycle_Depot", Cycle_Depot);
            jsonObject.put("Domicile", Domicile);
            jsonObject.put("Statut", Statut);
            jsonObject.put("Date_inventaire", Date_inventaire);
            jsonObject.put("Date_Livraison2", Date_Livraison2);
            jsonObject.put("Montant_TVA", Montant_TVA);
            jsonObject.put("ID_Depot", ID_Depot);
            jsonObject.put("Num_Bon_Prev", Num_Bon_Prev);
            jsonObject.put("Date_Livraison1", Date_Livraison1);
            jsonObject.put("Preparation_Nominative", Preparation_Nominative);
            jsonObject.put("IPP_Patient", IPP_Patient);
            jsonObject.put("Nom_Patient", Nom_Patient);
            jsonObject.put("CB_Bon_Commande_Patient", CB_Bon_Commande_Patient);
            jsonObject.put("Date_Prevision", Date_Prevision);
            jsonObject.put("Date_Livraison3", Date_Livraison3);
            jsonObject.put("Date_Livraison4", Date_Livraison4);
            jsonObject.put("Date_Livraison5", Date_Livraison5);

        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public int compareTo(Object obj) {
        Preparation preparation = (Preparation) obj;

        if (this.getPhiMR4UUID() == preparation.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getID() > preparation.getID() ? 1 : -1;
        }
    }
}
