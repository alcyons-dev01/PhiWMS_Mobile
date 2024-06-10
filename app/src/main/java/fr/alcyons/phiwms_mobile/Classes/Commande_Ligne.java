package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Commande_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

public class Commande_Ligne implements Serializable, Comparable {

    private int commande_UID;
    private int Rien;
    private String produit_Reference;
    private String Type_produit;
    private double Qte_COM;
    private double PU_Com;
    private double Remise;
    private String Designation;
    private int Code_produit;
    private String Categorie;
    private String Unite;
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
    private int phiwms_mobileUUID = -1;

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
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

    public double getQte_COM() {
        return Qte_COM;
    }

    public void setQte_COM(double qte_COM) {
        Qte_COM = qte_COM;
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

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public int getCode_produit() {
        return Code_produit;
    }

    public void setCode_produit(int code_produit) {
        Code_produit = code_produit;
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
        return obj == this;
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
