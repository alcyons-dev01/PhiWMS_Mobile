package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phimr4.BaseDeDonnees.Commande_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 19/06/2017.
 */

public class Commande_Ligne implements Serializable, Comparable {

    private int commande_UID;
    private int Rien;
    private String produit_Reference;
    private String Type_produit;
    private double Qté_COM;
    private double PU_Com;
    private double Remise;
    private String Désignation;
    private int Code_produit;
    private String Catégorie;
    private String Unité;
    private double Conditionnement;
    private int Code_fournisseu;
    private String Fournisseur;
    private double Montant_HT;
    private int _UID;
    private double Tx_TVA;
    private double MTTC_ligne;
    private Boolean Peremption;
    private double Qte_RAL;
    private double Qte_RAF;
    private Boolean Gratuit;
    private String Devise;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String Num_BL;
    private double RAFF_TTC;
    private String Section_Analytique;
    private int ID_MVT;
    private double PU_FACT;
    private double Qte_Fact;
    private int Classe_N;
    private int NbColis;
    private int NbPalette;
    private double Poids;
    private double RAF_HT;
    private int phiMR4UUID = -1;


    public Commande_Ligne(int commande_UID, int rien, String produit_Reference, String type_produit, double qté_COM, double PU_Com, double remise, String désignation, int code_produit, String catégorie, String unité, double conditionnement, int code_fournisseu, String fournisseur, double montant_HT, int _UID, double tx_TVA, double MTTC_ligne, Boolean peremption, double qte_RAL, double qte_RAF, Boolean gratuit, String devise, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, String num_BL, double RAFF_TTC, String section_Analytique, int ID_MVT, double PU_FACT, double qte_Fact, int classe_N, int nbColis, int nbPalette, double poids, double RAF_HT) {
        this.commande_UID = commande_UID;
        this.Rien = rien;
        this.produit_Reference = produit_Reference;
        this.Type_produit = type_produit;
        this.Qté_COM = qté_COM;
        this.PU_Com = PU_Com;
        this.Remise = remise;
        this.Désignation = désignation;
        this.Code_produit = code_produit;
        this.Catégorie = catégorie;
        this.Unité = unité;
        this.Conditionnement = conditionnement;
        this.Code_fournisseu = code_fournisseu;
        this.Fournisseur = fournisseur;
        this.Montant_HT = montant_HT;
        this._UID = _UID;
        this.Tx_TVA = tx_TVA;
        this.MTTC_ligne = MTTC_ligne;
        this.Peremption = peremption;
        this.Qte_RAL = qte_RAL;
        this.Qte_RAF = qte_RAF;
        this.Gratuit = gratuit;
        this.Devise = devise;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.Num_BL = num_BL;
        this.RAFF_TTC = RAFF_TTC;
        this.Section_Analytique = section_Analytique;
        this.ID_MVT = ID_MVT;
        this.PU_FACT = PU_FACT;
        this.Qte_Fact = qte_Fact;
        this.Classe_N = classe_N;
        this.NbColis = nbColis;
        this.NbPalette = nbPalette;
        this.Poids = poids;
        this.RAF_HT = RAF_HT;
    }

    public Commande_Ligne(JSONObject commande_ligneJson) {
        try {
            this.commande_UID = commande_ligneJson.getInt("commande_UID");
            this.Rien = commande_ligneJson.getInt("Rien");
            this.produit_Reference = OutilsGestionClasses.recupererString(commande_ligneJson.getString("produit_Reference"));
            this.Type_produit = OutilsGestionClasses.recupererString(commande_ligneJson.getString("Type_produit"));
            this.Qté_COM = commande_ligneJson.getDouble("Qté_COM");
            this.PU_Com = commande_ligneJson.getDouble("PU_Com");
            this.Remise = commande_ligneJson.getDouble("Remise");
            this.Désignation = OutilsGestionClasses.recupererString(commande_ligneJson.getString("Désignation"));
            this.Code_produit = commande_ligneJson.getInt("Code_produit");
            this.Catégorie = OutilsGestionClasses.recupererString(commande_ligneJson.getString("Catégorie"));
            this.Unité = OutilsGestionClasses.recupererString(commande_ligneJson.getString("Unité"));
            this.Conditionnement = commande_ligneJson.getDouble("Conditionnement");
            this.Code_fournisseu = commande_ligneJson.getInt("Code_fournisseu");
            this.Fournisseur = OutilsGestionClasses.recupererString(commande_ligneJson.getString("Fournisseur"));
            this.Montant_HT = commande_ligneJson.getDouble("Montant_HT");
            this._UID = commande_ligneJson.getInt("_UID");
            this.Tx_TVA = commande_ligneJson.getDouble("Tx_TVA");
            this.MTTC_ligne = commande_ligneJson.getDouble("MTTC_ligne");
            this.Peremption = OutilsGestionClasses.recupererBooleen(commande_ligneJson, "Peremption");
            this.Qte_RAL = commande_ligneJson.getDouble("Qte_RAL");
            this.Qte_RAF = commande_ligneJson.getDouble("Qte_RAF");
            this.Gratuit = OutilsGestionClasses.recupererBooleen(commande_ligneJson, "Gratuit");
            this.Devise = OutilsGestionClasses.recupererString(commande_ligneJson.getString("Devise"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(commande_ligneJson.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(commande_ligneJson.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(commande_ligneJson.getString("SYS_USER_MAJ"));
            this.Num_BL = OutilsGestionClasses.recupererString(commande_ligneJson.getString("Num_BL"));
            this.RAFF_TTC = commande_ligneJson.getDouble("RAFF_TTC");
            this.Section_Analytique = OutilsGestionClasses.recupererString(commande_ligneJson.getString("Section_Analytique"));
            this.ID_MVT = commande_ligneJson.getInt("ID_MVT");
            this.PU_FACT = commande_ligneJson.getDouble("PU_FACT");
            this.Qte_Fact = commande_ligneJson.getDouble("Qte_Fact");
            this.Classe_N = commande_ligneJson.getInt("Classe_N");
            this.NbColis = commande_ligneJson.getInt("NbColis");
            this.NbPalette = commande_ligneJson.getInt("NbPalette");
            this.Poids = commande_ligneJson.getDouble("Poids");
            this.RAF_HT = commande_ligneJson.getDouble("RAF_HT");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Commande_Ligne(Cursor cursorCommande_ligne) {
        this.commande_UID = cursorCommande_ligne.getInt(Commande_LigneOpenHelper.Constantes.NUM_COL_COMMANDE_UID_COMMANDE_LIGNE);
        this.Rien = cursorCommande_ligne.getInt(Commande_LigneOpenHelper.Constantes.NUM_COL_RIEN_COMMANDE_LIGNE);
        this.produit_Reference = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_PRODUIT_REFERENCE_COMMANDE_LIGNE);
        this.Type_produit = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_TYPE_PRODUIT_COMMANDE_LIGNE);
        this.Qté_COM = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_QTE_COM_COMMANDE_LIGNE);
        this.PU_Com = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_PU_COM_COMMANDE_LIGNE);
        this.Remise = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_REMISE_COMMANDE_LIGNE);
        this.Désignation = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_DESIGNATION_COMMANDE_LIGNE);
        this.Code_produit = cursorCommande_ligne.getInt(Commande_LigneOpenHelper.Constantes.NUM_COL_CODE_PRODUIT_COMMANDE_LIGNE);
        this.Catégorie = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_CATEGORIE_COMMANDE_LIGNE);
        this.Unité = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_UNITE_COMMANDE_LIGNE);
        this.Conditionnement = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_CONDITIONNEMENT_COMMANDE_LIGNE);
        this.Code_fournisseu = cursorCommande_ligne.getInt(Commande_LigneOpenHelper.Constantes.NUM_COL_CODE_FOURNISSEU_COMMANDE_LIGNE);
        this.Fournisseur = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_FOURNISSEUR_COMMANDE_LIGNE);
        this.Montant_HT = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_MONTANT_HT_COMMANDE_LIGNE);
        this._UID = cursorCommande_ligne.getInt(Commande_LigneOpenHelper.Constantes.NUM_COL__UID_COMMANDE_LIGNE);
        this.Tx_TVA = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_TX_TVA_COMMANDE_LIGNE);
        this.MTTC_ligne = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_MTTC_LIGNE_COMMANDE_LIGNE);
        this.Peremption = OutilsGestionClasses.recupererBooleen(cursorCommande_ligne, Commande_LigneOpenHelper.Constantes.NUM_COL_PEREMPTION_COMMANDE_LIGNE);
        this.Qte_RAL = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_QTE_RAL_COMMANDE_LIGNE);
        this.Qte_RAF = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_QTE_RAF_COMMANDE_LIGNE);
        this.Gratuit = OutilsGestionClasses.recupererBooleen(cursorCommande_ligne, Commande_LigneOpenHelper.Constantes.NUM_COL_GRATUIT_COMMANDE_LIGNE);
        this.Devise = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_DEVISE_COMMANDE_LIGNE);
        this.SYS_DT_MAJ = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_COMMANDE_LIGNE);
        this.SYS_HEURE_MAJ = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_COMMANDE_LIGNE);
        this.SYS_USER_MAJ = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_COMMANDE_LIGNE);
        this.Num_BL = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_NUM_BL_COMMANDE_LIGNE);
        this.RAFF_TTC = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_RAFF_TTC_COMMANDE_LIGNE);
        this.Section_Analytique = cursorCommande_ligne.getString(Commande_LigneOpenHelper.Constantes.NUM_COL_SECTION_ANALYTIQUE_COMMANDE_LIGNE);
        this.ID_MVT = cursorCommande_ligne.getInt(Commande_LigneOpenHelper.Constantes.NUM_COL_ID_MVT_COMMANDE_LIGNE);
        this.PU_FACT = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_PU_FACT_COMMANDE_LIGNE);
        this.Qte_Fact = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_QTE_FACT_COMMANDE_LIGNE);
        this.Classe_N = cursorCommande_ligne.getInt(Commande_LigneOpenHelper.Constantes.NUM_COL_CLASSE_N_COMMANDE_LIGNE);
        this.NbColis = cursorCommande_ligne.getInt(Commande_LigneOpenHelper.Constantes.NUM_COL_NBCOLIS_COMMANDE_LIGNE);
        this.NbPalette = cursorCommande_ligne.getInt(Commande_LigneOpenHelper.Constantes.NUM_COL_NBPALETTE_COMMANDE_LIGNE);
        this.Poids = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_POIDS_COMMANDE_LIGNE);
        this.RAF_HT = cursorCommande_ligne.getDouble(Commande_LigneOpenHelper.Constantes.NUM_COL_RAF_HT_COMMANDE_LIGNE);
        this.phiMR4UUID = cursorCommande_ligne.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getCommande_UID() {
        return commande_UID;
    }

    public void setCommande_UID(int commande_UID) {
        this.commande_UID = commande_UID;
    }

    public int getRien() {
        return Rien;
    }

    public void setRien(int rien) {
        Rien = rien;
    }

    public String getProduit_Reference() {
        return produit_Reference;
    }

    public void setProduit_Reference(String produit_Reference) {
        this.produit_Reference = produit_Reference;
    }

    public String getType_produit() {
        return Type_produit;
    }

    public void setType_produit(String type_produit) {
        Type_produit = type_produit;
    }

    public double getQté_COM() {
        return Qté_COM;
    }

    public void setQté_COM(double qté_COM) {
        Qté_COM = qté_COM;
    }

    public double getPU_Com() {
        return PU_Com;
    }

    public void setPU_Com(double PU_Com) {
        this.PU_Com = PU_Com;
    }

    public double getRemise() {
        return Remise;
    }

    public void setRemise(double remise) {
        Remise = remise;
    }

    public String getDésignation() {
        return Désignation;
    }

    public void setDésignation(String désignation) {
        Désignation = désignation;
    }

    public int getCode_produit() {
        return Code_produit;
    }

    public void setCode_produit(int code_produit) {
        Code_produit = code_produit;
    }

    public String getCatégorie() {
        return Catégorie;
    }

    public void setCatégorie(String catégorie) {
        Catégorie = catégorie;
    }

    public String getUnité() {
        return Unité;
    }

    public void setUnité(String unité) {
        Unité = unité;
    }

    public double getConditionnement() {
        return Conditionnement;
    }

    public void setConditionnement(double conditionnement) {
        Conditionnement = conditionnement;
    }

    public int getCode_fournisseu() {
        return Code_fournisseu;
    }

    public void setCode_fournisseu(int code_fournisseu) {
        Code_fournisseu = code_fournisseu;
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

    public double getMTTC_ligne() {
        return MTTC_ligne;
    }

    public void setMTTC_ligne(double MTTC_ligne) {
        this.MTTC_ligne = MTTC_ligne;
    }

    public Boolean getPeremption() {
        return Peremption;
    }

    public void setPeremption(Boolean peremption) {
        Peremption = peremption;
    }

    public double getQte_RAL() {
        return Qte_RAL;
    }

    public void setQte_RAL(double qte_RAL) {
        Qte_RAL = qte_RAL;
    }

    public double getQte_RAF() {
        return Qte_RAF;
    }

    public void setQte_RAF(double qte_RAF) {
        Qte_RAF = qte_RAF;
    }

    public Boolean getGratuit() {
        return Gratuit;
    }

    public void setGratuit(Boolean gratuit) {
        Gratuit = gratuit;
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

    public String getNum_BL() {
        return Num_BL;
    }

    public void setNum_BL(String num_BL) {
        Num_BL = num_BL;
    }

    public double getRAFF_TTC() {
        return RAFF_TTC;
    }

    public void setRAFF_TTC(double RAFF_TTC) {
        this.RAFF_TTC = RAFF_TTC;
    }

    public String getSection_Analytique() {
        return Section_Analytique;
    }

    public void setSection_Analytique(String section_Analytique) {
        Section_Analytique = section_Analytique;
    }

    public int getID_MVT() {
        return ID_MVT;
    }

    public void setID_MVT(int ID_MVT) {
        this.ID_MVT = ID_MVT;
    }

    public double getPU_FACT() {
        return PU_FACT;
    }

    public void setPU_FACT(double PU_FACT) {
        this.PU_FACT = PU_FACT;
    }

    public double getQte_Fact() {
        return Qte_Fact;
    }

    public void setQte_Fact(double qte_Fact) {
        Qte_Fact = qte_Fact;
    }

    public int getClasse_N() {
        return Classe_N;
    }

    public void setClasse_N(int classe_N) {
        Classe_N = classe_N;
    }

    public int getNbColis() {
        return NbColis;
    }

    public void setNbColis(int nbColis) {
        NbColis = nbColis;
    }

    public int getNbPalette() {
        return NbPalette;
    }

    public void setNbPalette(int nbPalette) {
        NbPalette = nbPalette;
    }

    public double getPoids() {
        return Poids;
    }

    public void setPoids(double poids) {
        Poids = poids;
    }

    public double getRAF_HT() {
        return RAF_HT;
    }

    public void setRAF_HT(double RAF_HT) {
        this.RAF_HT = RAF_HT;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Commande_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Commande_Ligne commande_ligne = (Commande_Ligne) obj;

        if (this.get_UID() == commande_ligne.get_UID()) {
            return 0;
        } else {
            return this.get_UID() > commande_ligne.get_UID() ? 1 : -1;
        }
    }
}
