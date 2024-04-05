package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.MVT_DepotsOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by olivier on 12/03/2018.
 */

public class MVT_Depots {

    int Code;
    String Depot_Reference;
    int Code_dépot;
    int Code_produit;
    String Référence_F;
    int PU_com;
    String Désignation;
    String Unité;
    String Fournisseur;
    int Code_frs;
    int Qté_com;
    int Qté_livrée;
    int Qté_reste;
    boolean Reliquat;
    String Date_entrée;
    boolean Select;
    String Commande;
    String Date_com;
    int Condi_achat;
    int Condi_distri;
    String Date_péremption;
    String Num_lot;
    String Code_barre;
    boolean Péremption;
    int id_ligne_commande;
    int Pu_fact;
    int Repris;
    int Qté_Mvt;
    int Qté_RAL;
    boolean Gratuits;
    String Devise;
    String SYS_DT_MAJ;
    String SYS_HEURE_MAJ;
    String SYS_USER_MAJ;
    boolean Suivi_Par_Lot;
    int _UID;
    private int phiMR4UUID = -1;

    public MVT_Depots(JSONObject jsonObject) {
        try {
            Code = jsonObject.getInt("Code");
            Depot_Reference = OutilsGestionClasses.recupererString(jsonObject.getString("Depot_Reference"));
            Code_dépot = jsonObject.getInt("Code_dépot");
            Code_produit = jsonObject.getInt("Code_produit");
            Référence_F = OutilsGestionClasses.recupererString(jsonObject.getString("Référence_F"));
            PU_com = jsonObject.getInt("PU_com");
            Désignation = OutilsGestionClasses.recupererString(jsonObject.getString("Désignation"));
            Unité = OutilsGestionClasses.recupererString(jsonObject.getString("Unité"));
            Fournisseur = OutilsGestionClasses.recupererString(jsonObject.getString("Fournisseur"));
            Code_frs = jsonObject.getInt("Code_frs");
            Qté_com = jsonObject.getInt("Qté_com");
            Qté_livrée = jsonObject.getInt("Qté_livrée");
            Qté_reste = jsonObject.getInt("Qté_reste");
            Reliquat = OutilsGestionClasses.recupererBooleen(jsonObject, "Reliquat");
            Date_entrée = OutilsGestionClasses.recupererString(jsonObject.getString("Date_entrée"));
            Select = OutilsGestionClasses.recupererBooleen(jsonObject, "Select");
            Commande = OutilsGestionClasses.recupererString(jsonObject.getString("Commande"));
            Date_com = OutilsGestionClasses.recupererString(jsonObject.getString("Date_com"));
            Condi_achat = jsonObject.getInt("Condi_achat");
            Condi_distri = jsonObject.getInt("Condi_distri");
            Date_péremption = OutilsGestionClasses.recupererString(jsonObject.getString("Date_péremption"));
            Num_lot = OutilsGestionClasses.recupererString(jsonObject.getString("Num_lot"));
            Code_barre = OutilsGestionClasses.recupererString(jsonObject.getString("Code_barre"));
            Péremption = OutilsGestionClasses.recupererBooleen(jsonObject, "Péremption");
            id_ligne_commande = jsonObject.getInt("id_ligne_commande");
            Pu_fact = jsonObject.getInt("Pu_fact");
            Repris = jsonObject.getInt("Repris");
            Qté_Mvt = jsonObject.getInt("Qté_Mvt");
            Qté_RAL = jsonObject.getInt("Qté_RAL");
            Gratuits = OutilsGestionClasses.recupererBooleen(jsonObject, "Gratuits");
            Devise = OutilsGestionClasses.recupererString(jsonObject.getString("Devise"));
            SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            Suivi_Par_Lot = OutilsGestionClasses.recupererBooleen(jsonObject, "Suivi_Par_Lot");
            _UID = jsonObject.getInt("_UID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MVT_Depots(Cursor cursor) {
        Code = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_CODE_MVT_DEPOTS);
        Depot_Reference = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_MVT_DEPOTS);
        Code_dépot = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_CODE_DEPOT_MVT_DEPOTS);
        Code_produit = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_CODE_PRODUIT_MVT_DEPOTS);
        Référence_F = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_REFERENCE_F_MVT_DEPOTS);
        PU_com = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_PU_COM_MVT_DEPOTS);
        Désignation = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_DESIGNATION_MVT_DEPOTS);
        Unité = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_UNITE_MVT_DEPOTS);
        Fournisseur = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_FOURNISSEUR_MVT_DEPOTS);
        Code_frs = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_CODE_FRS_MVT_DEPOTS);
        Qté_com = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_QTE_COM_MVT_DEPOTS);
        Qté_livrée = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_QTE_LIVREE_MVT_DEPOTS);
        Qté_reste = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_QTE_RESTE_MVT_DEPOTS);
        Reliquat = OutilsGestionClasses.recupererBooleen(cursor, MVT_DepotsOpenHelper.Constantes.NUM_COL_RELIQUAT_MVT_DEPOTS);
        Date_entrée = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_DATE_ENTREE_MVT_DEPOTS);
        Commande = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_COMMANDE_MVT_DEPOTS);
        Date_com = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_DATE_COM_MVT_DEPOTS);
        Condi_achat = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_CONDI_ACHAT_MVT_DEPOTS);
        Condi_distri = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_CONDI_DISTRI_MVT_DEPOTS);
        Date_péremption = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_DATE_PEREMPTION_MVT_DEPOTS);
        Num_lot = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_NUM_LOT_MVT_DEPOTS);
        Code_barre = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_CODE_BARRE_MVT_DEPOTS);
        Péremption = OutilsGestionClasses.recupererBooleen(cursor, MVT_DepotsOpenHelper.Constantes.NUM_COL_PEREMPTION_MVT_DEPOTS);
        id_ligne_commande = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_ID_LIGNE_COMMANDE_MVT_DEPOTS);
        Pu_fact = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_PU_FACT_MVT_DEPOTS);
        Repris = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_REPRIS_MVT_DEPOTS);
        Qté_Mvt = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_QTE_MVT_MVT_DEPOTS);
        Qté_RAL = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL_QTE_RAL_MVT_DEPOTS);
        Gratuits = OutilsGestionClasses.recupererBooleen(cursor, MVT_DepotsOpenHelper.Constantes.NUM_COL_GRATUITS_MVT_DEPOTS);
        Devise = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_DEVISE_MVT_DEPOTS);
        SYS_DT_MAJ = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_MVT_DEPOTS);
        SYS_HEURE_MAJ = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_MVT_DEPOTS);
        SYS_USER_MAJ = cursor.getString(MVT_DepotsOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_MVT_DEPOTS);
        Suivi_Par_Lot = OutilsGestionClasses.recupererBooleen(cursor, MVT_DepotsOpenHelper.Constantes.NUM_COL_SUIVI_PAR_LOT_MVT_DEPOTS);
        _UID = cursor.getInt(MVT_DepotsOpenHelper.Constantes.NUM_COL__UID_MVT_DEPOTS);
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getDepot_Reference() {
        return Depot_Reference;
    }

    public void setDepot_Reference(String depot_Reference) {
        Depot_Reference = depot_Reference;
    }

    public int getCode_dépot() {
        return Code_dépot;
    }

    public void setCode_dépot(int code_dépot) {
        Code_dépot = code_dépot;
    }

    public int getCode_produit() {
        return Code_produit;
    }

    public void setCode_produit(int code_produit) {
        Code_produit = code_produit;
    }

    public String getRéférence_F() {
        return Référence_F;
    }

    public void setRéférence_F(String référence_F) {
        Référence_F = référence_F;
    }

    public int getPU_com() {
        return PU_com;
    }

    public void setPU_com(int PU_com) {
        this.PU_com = PU_com;
    }

    public String getDésignation() {
        return Désignation;
    }

    public void setDésignation(String désignation) {
        Désignation = désignation;
    }

    public String getUnité() {
        return Unité;
    }

    public void setUnité(String unité) {
        Unité = unité;
    }

    public String getFournisseur() {
        return Fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        Fournisseur = fournisseur;
    }

    public int getCode_frs() {
        return Code_frs;
    }

    public void setCode_frs(int code_frs) {
        Code_frs = code_frs;
    }

    public int getQté_com() {
        return Qté_com;
    }

    public void setQté_com(int qté_com) {
        Qté_com = qté_com;
    }

    public int getQté_livrée() {
        return Qté_livrée;
    }

    public void setQté_livrée(int qté_livrée) {
        Qté_livrée = qté_livrée;
    }

    public int getQté_reste() {
        return Qté_reste;
    }

    public void setQté_reste(int qté_reste) {
        Qté_reste = qté_reste;
    }

    public boolean isReliquat() {
        return Reliquat;
    }

    public void setReliquat(boolean reliquat) {
        Reliquat = reliquat;
    }

    public String getDate_entrée() {
        return Date_entrée;
    }

    public void setDate_entrée(String date_entrée) {
        Date_entrée = date_entrée;
    }

    public boolean isSelect() {
        return Select;
    }

    public void setSelect(boolean select) {
        Select = select;
    }

    public String getCommande() {
        return Commande;
    }

    public void setCommande(String commande) {
        Commande = commande;
    }

    public String getDate_com() {
        return Date_com;
    }

    public void setDate_com(String date_com) {
        Date_com = date_com;
    }

    public int getCondi_achat() {
        return Condi_achat;
    }

    public void setCondi_achat(int condi_achat) {
        Condi_achat = condi_achat;
    }

    public int getCondi_distri() {
        return Condi_distri;
    }

    public void setCondi_distri(int condi_distri) {
        Condi_distri = condi_distri;
    }

    public String getDate_péremption() {
        return Date_péremption;
    }

    public void setDate_péremption(String date_péremption) {
        Date_péremption = date_péremption;
    }

    public String getNum_lot() {
        return Num_lot;
    }

    public void setNum_lot(String num_lot) {
        Num_lot = num_lot;
    }

    public String getCode_barre() {
        return Code_barre;
    }

    public void setCode_barre(String code_barre) {
        Code_barre = code_barre;
    }

    public boolean isPéremption() {
        return Péremption;
    }

    public void setPéremption(boolean péremption) {
        Péremption = péremption;
    }

    public int getId_ligne_commande() {
        return id_ligne_commande;
    }

    public void setId_ligne_commande(int id_ligne_commande) {
        this.id_ligne_commande = id_ligne_commande;
    }

    public int getPu_fact() {
        return Pu_fact;
    }

    public void setPu_fact(int pu_fact) {
        Pu_fact = pu_fact;
    }

    public int getRepris() {
        return Repris;
    }

    public void setRepris(int repris) {
        Repris = repris;
    }

    public int getQté_Mvt() {
        return Qté_Mvt;
    }

    public void setQté_Mvt(int qté_Mvt) {
        Qté_Mvt = qté_Mvt;
    }

    public int getQté_RAL() {
        return Qté_RAL;
    }

    public void setQté_RAL(int qté_RAL) {
        Qté_RAL = qté_RAL;
    }

    public boolean isGratuits() {
        return Gratuits;
    }

    public void setGratuits(boolean gratuits) {
        Gratuits = gratuits;
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

    public boolean isSuivi_Par_Lot() {
        return Suivi_Par_Lot;
    }

    public void setSuivi_Par_Lot(boolean suivi_Par_Lot) {
        Suivi_Par_Lot = suivi_Par_Lot;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Code", Code);
            jsonObject.put("Depot_Reference", Depot_Reference);
            jsonObject.put("Code_dépot", Code_dépot);
            jsonObject.put("Code_produit", Code_produit);
            jsonObject.put("Référence_F", Référence_F);
            jsonObject.put("PU_com", PU_com);
            jsonObject.put("Désignation", Désignation);
            jsonObject.put("Unité", Unité);
            jsonObject.put("Fournisseur", Fournisseur);
            jsonObject.put("Code_frs", Code_frs);
            jsonObject.put("Qté_com", Qté_com);
            jsonObject.put("Qté_livrée", Qté_livrée);
            jsonObject.put("Qté_reste", Qté_reste);
            jsonObject.put("Reliquat", Reliquat);
            jsonObject.put("Date_entrée", Date_entrée);
            jsonObject.put("Select", Select);
            jsonObject.put("Commande", Commande);
            jsonObject.put("Date_com", Date_com);
            jsonObject.put("Condi_achat", Condi_achat);
            jsonObject.put("Condi_distri", Condi_distri);
            jsonObject.put("Date_péremption", Date_péremption);
            jsonObject.put("Num_lot", Num_lot);
            jsonObject.put("Code_barre", Code_barre);
            jsonObject.put("Péremption", Péremption);
            jsonObject.put("id_ligne_commande", id_ligne_commande);
            jsonObject.put("Pu_fact", Pu_fact);
            jsonObject.put("Repris", Repris);
            jsonObject.put("Qté_Mvt", Qté_Mvt);
            jsonObject.put("Qté_RAL", Qté_RAL);
            jsonObject.put("Gratuits", Gratuits);
            jsonObject.put("Devise", Devise);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("Suivi_Par_Lot", Suivi_Par_Lot);
            jsonObject.put("_UID", _UID);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }
}
