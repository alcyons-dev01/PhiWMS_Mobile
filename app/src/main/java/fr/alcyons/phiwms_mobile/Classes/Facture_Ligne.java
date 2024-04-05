package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 27/06/2017.
 */

public class Facture_Ligne implements Serializable, Comparable {

    private int _UID;
    private int Facture_UID;
    private String Ref_Frs;
    private double Qté_FACT;
    private double Qté_Com;
    private double Qté_LIV;
    private double PU_COM;
    private double Taux_Tva;
    private double MHT_com;
    private double MHT_Fact;
    private double PU_Fact;
    private String Commande;
    private String Désignation;
    private double TTC_Com;
    private double TTC_Fact;
    private int Code_produit;
    private int Code_piece;
    private String Categorie;
    private double Fact_tva;
    private int Code_ligne_C;
    private int Code_ligne_ST;
    private String Devise;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String Section_Analytique;
    private double PU_FactDevise;
    private int phiwms_mobileUUID = -1;


    public Facture_Ligne(int _UID, int facture_UID, String ref_Frs, double qté_FACT, double qté_Com, double qté_LIV, double PU_COM, double taux_Tva, double MHT_com, double MHT_Fact, double PU_Fact, String commande, String désignation, double TTC_Com, double TTC_Fact, int code_produit, int code_piece, String categorie, double fact_tva, int code_ligne_C, int code_ligne_ST, String devise, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, String section_Analytique, double PU_FactDevise) {
        this._UID = _UID;
        this.Facture_UID = facture_UID;
        this.Ref_Frs = ref_Frs;
        this.Qté_FACT = qté_FACT;
        this.Qté_Com = qté_Com;
        this.Qté_LIV = qté_LIV;
        this.PU_COM = PU_COM;
        this.Taux_Tva = taux_Tva;
        this.MHT_com = MHT_com;
        this.MHT_Fact = MHT_Fact;
        this.PU_Fact = PU_Fact;
        this.Commande = commande;
        this.Désignation = désignation;
        this.TTC_Com = TTC_Com;
        this.TTC_Fact = TTC_Fact;
        this.Code_produit = code_produit;
        this.Code_piece = code_piece;
        this.Categorie = categorie;
        this.Fact_tva = fact_tva;
        this.Code_ligne_C = code_ligne_C;
        this.Code_ligne_ST = code_ligne_ST;
        this.Devise = devise;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.Section_Analytique = section_Analytique;
        this.PU_FactDevise = PU_FactDevise;
    }

    public Facture_Ligne(JSONObject jsonObject) {
        try {
            this._UID = jsonObject.getInt("_UID");
            this.Facture_UID = jsonObject.getInt("Facture_UID");
            this.Ref_Frs = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Frs"));
            this.Qté_FACT = jsonObject.getDouble("Qté_FACT");
            this.Qté_Com = jsonObject.getDouble("Qté_Com");
            this.Qté_LIV = jsonObject.getDouble("Qté_LIV");
            this.PU_COM = jsonObject.getDouble("PU_COM");
            this.Taux_Tva = jsonObject.getDouble("Taux_Tva");
            this.MHT_com = jsonObject.getDouble("MHT_com");
            this.MHT_Fact = jsonObject.getDouble("MHT_Fact");
            this.PU_Fact = jsonObject.getDouble("PU_Fact");
            this.Commande = OutilsGestionClasses.recupererString(jsonObject.getString("Commande"));
            this.Désignation = OutilsGestionClasses.recupererString(jsonObject.getString("Désignation"));
            this.TTC_Com = jsonObject.getDouble("TTC_Com");
            this.TTC_Fact = jsonObject.getDouble("TTC_Fact");
            this.Code_produit = jsonObject.getInt("Code_produit");
            this.Code_piece = jsonObject.getInt("Code_piece");
            this.Categorie = OutilsGestionClasses.recupererString(jsonObject.getString("Categorie"));
            this.Fact_tva = jsonObject.getDouble("Fact_tva");
            this.Code_ligne_C = jsonObject.getInt("Code_ligne_C");
            this.Code_ligne_ST = jsonObject.getInt("Code_ligne_ST");
            this.Devise = OutilsGestionClasses.recupererString(jsonObject.getString("Devise"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.Section_Analytique = OutilsGestionClasses.recupererString(jsonObject.getString("Section_Analytique"));
            this.PU_FactDevise = jsonObject.getDouble("PU_FactDevise");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public Facture_Ligne(Cursor cursor) {
        this._UID = cursor.getInt(Facture_LigneOpenHelper.Constantes.NUM_COL__UID_FACTURE_LIGNE);
        this.Facture_UID = cursor.getInt(Facture_LigneOpenHelper.Constantes.NUM_COL_FACTURE_UID_FACTURE_LIGNE);
        this.Ref_Frs = cursor.getString(Facture_LigneOpenHelper.Constantes.NUM_COL_REF_FRS_FACTURE_LIGNE);
        this.Qté_FACT = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_QTE_FACT_FACTURE_LIGNE);
        this.Qté_Com = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_QTE_COM_FACTURE_LIGNE);
        this.Qté_LIV = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_QTE_LIV_FACTURE_LIGNE);
        this.PU_COM = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_PU_COM_FACTURE_LIGNE);
        this.Taux_Tva = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_TAUX_TVA_FACTURE_LIGNE);
        this.MHT_com = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_MHT_COM_FACTURE_LIGNE);
        this.MHT_Fact = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_MHT_FACT_FACTURE_LIGNE);
        this.PU_Fact = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_PU_FACT_FACTURE_LIGNE);
        this.Commande = cursor.getString(Facture_LigneOpenHelper.Constantes.NUM_COL_COMMANDE_FACTURE_LIGNE);
        this.Désignation = cursor.getString(Facture_LigneOpenHelper.Constantes.NUM_COL_DESIGNATION_FACTURE_LIGNE);
        this.TTC_Com = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_TTC_COM_FACTURE_LIGNE);
        this.TTC_Fact = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_TTC_FACT_FACTURE_LIGNE);
        this.Code_produit = cursor.getInt(Facture_LigneOpenHelper.Constantes.NUM_COL_CODE_PRODUIT_FACTURE_LIGNE);
        this.Code_piece = cursor.getInt(Facture_LigneOpenHelper.Constantes.NUM_COL_CODE_PIECE_FACTURE_LIGNE);
        this.Categorie = cursor.getString(Facture_LigneOpenHelper.Constantes.NUM_COL_CATEGORIE_FACTURE_LIGNE);
        this.Fact_tva = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_FACT_TVA_FACTURE_LIGNE);
        this.Code_ligne_C = cursor.getInt(Facture_LigneOpenHelper.Constantes.NUM_COL_CODE_LIGNE_C_FACTURE_LIGNE);
        this.Code_ligne_ST = cursor.getInt(Facture_LigneOpenHelper.Constantes.NUM_COL_CODE_LIGNE_ST_FACTURE_LIGNE);
        this.Devise = cursor.getString(Facture_LigneOpenHelper.Constantes.NUM_COL_DEVISE_FACTURE_LIGNE);
        this.SYS_DT_MAJ = cursor.getString(Facture_LigneOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_FACTURE_LIGNE);
        this.SYS_HEURE_MAJ = cursor.getString(Facture_LigneOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_FACTURE_LIGNE);
        this.SYS_USER_MAJ = cursor.getString(Facture_LigneOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_FACTURE_LIGNE);
        this.Section_Analytique = cursor.getString(Facture_LigneOpenHelper.Constantes.NUM_COL_SECTION_ANALYTIQUE_FACTURE_LIGNE);
        this.PU_FactDevise = cursor.getDouble(Facture_LigneOpenHelper.Constantes.NUM_COL_PU_FACTDEVISE_FACTURE_LIGNE);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public int getFacture_UID() {
        return Facture_UID;
    }

    public void setFacture_UID(int facture_UID) {
        Facture_UID = facture_UID;
    }

    public String getRef_Frs() {
        return Ref_Frs;
    }

    public void setRef_Frs(String ref_Frs) {
        Ref_Frs = ref_Frs;
    }

    public double getQté_FACT() {
        return Qté_FACT;
    }

    public void setQté_FACT(double qté_FACT) {
        Qté_FACT = qté_FACT;
    }

    public double getQté_Com() {
        return Qté_Com;
    }

    public void setQté_Com(double qté_Com) {
        Qté_Com = qté_Com;
    }

    public double getQté_LIV() {
        return Qté_LIV;
    }

    public void setQté_LIV(double qté_LIV) {
        Qté_LIV = qté_LIV;
    }

    public double getPU_COM() {
        return PU_COM;
    }

    public void setPU_COM(double PU_COM) {
        this.PU_COM = PU_COM;
    }

    public double getTaux_Tva() {
        return Taux_Tva;
    }

    public void setTaux_Tva(double taux_Tva) {
        Taux_Tva = taux_Tva;
    }

    public double getMHT_com() {
        return MHT_com;
    }

    public void setMHT_com(double MHT_com) {
        this.MHT_com = MHT_com;
    }

    public double getMHT_Fact() {
        return MHT_Fact;
    }

    public void setMHT_Fact(double MHT_Fact) {
        this.MHT_Fact = MHT_Fact;
    }

    public double getPU_Fact() {
        return PU_Fact;
    }

    public void setPU_Fact(double PU_Fact) {
        this.PU_Fact = PU_Fact;
    }

    public String getCommande() {
        return Commande;
    }

    public void setCommande(String commande) {
        Commande = commande;
    }

    public String getDésignation() {
        return Désignation;
    }

    public void setDésignation(String désignation) {
        Désignation = désignation;
    }

    public double getTTC_Com() {
        return TTC_Com;
    }

    public void setTTC_Com(double TTC_Com) {
        this.TTC_Com = TTC_Com;
    }

    public double getTTC_Fact() {
        return TTC_Fact;
    }

    public void setTTC_Fact(double TTC_Fact) {
        this.TTC_Fact = TTC_Fact;
    }

    public int getCode_produit() {
        return Code_produit;
    }

    public void setCode_produit(int code_produit) {
        Code_produit = code_produit;
    }

    public int getCode_piece() {
        return Code_piece;
    }

    public void setCode_piece(int code_piece) {
        Code_piece = code_piece;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public double getFact_tva() {
        return Fact_tva;
    }

    public void setFact_tva(double fact_tva) {
        Fact_tva = fact_tva;
    }

    public int getCode_ligne_C() {
        return Code_ligne_C;
    }

    public void setCode_ligne_C(int code_ligne_C) {
        Code_ligne_C = code_ligne_C;
    }

    public int getCode_ligne_ST() {
        return Code_ligne_ST;
    }

    public void setCode_ligne_ST(int code_ligne_ST) {
        Code_ligne_ST = code_ligne_ST;
    }

    public String getDevise() {
        return Devise;
    }

    public void setDevise(String devise) {
        Devise = devise;
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

    public String getSection_Analytique() {
        return Section_Analytique;
    }

    public void setSection_Analytique(String section_Analytique) {
        Section_Analytique = section_Analytique;
    }

    public double getPU_FactDevise() {
        return PU_FactDevise;
    }

    public void setPU_FactDevise(double PU_FactDevise) {
        this.PU_FactDevise = PU_FactDevise;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Facture_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Facture_Ligne facture_ligne = (Facture_Ligne) obj;

        if (this.getphiwms_mobileUUID() == facture_ligne.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.get_UID() > facture_ligne.get_UID() ? 1 : -1;
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("_UID", this.get_UID());
            jsonObject.put("Facture_UID", this.getFacture_UID());
            jsonObject.put("Ref_Frs", this.getRef_Frs());
            jsonObject.put("Qté_FACT", this.getQté_FACT());
            jsonObject.put("Qté_Com", this.getQté_Com());
            jsonObject.put("Qté_LIV", this.getQté_LIV());
            jsonObject.put("PU_COM", this.getPU_COM());
            jsonObject.put("Taux_Tva", this.getTaux_Tva());
            jsonObject.put("MHT_com", this.getMHT_com());
            jsonObject.put("MHT_Fact", this.getMHT_Fact());
            jsonObject.put("PU_Fact", this.getPU_Fact());
            jsonObject.put("Commande", this.getCommande());
            jsonObject.put("Désignation", this.getDésignation());
            jsonObject.put("TTC_Com", this.getTTC_Com());
            jsonObject.put("TTC_Fact", this.getTTC_Fact());
            jsonObject.put("Code_produit", this.getCode_produit());
            jsonObject.put("Code_piece", this.getCode_piece());
            jsonObject.put("Categorie", this.getCategorie());
            jsonObject.put("Fact_tva", this.getFact_tva());
            jsonObject.put("Code_ligne_C", this.getCode_ligne_C());
            jsonObject.put("Code_ligne_ST", this.getCode_ligne_ST());
            jsonObject.put("Devise", this.getDevise());
            jsonObject.put("SYS_DT_MAJ", this.getSYS_DT_MAJ());
            jsonObject.put("SYS_HEURE_MAJ", this.getSYS_HEURE_MAJ());
            jsonObject.put("SYS_USER_MAJ", this.getSYS_USER_MAJ());
            jsonObject.put("Section_Analytique", this.getSection_Analytique());
            jsonObject.put("PU_FactDevise", this.getPU_FactDevise());
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }
}
