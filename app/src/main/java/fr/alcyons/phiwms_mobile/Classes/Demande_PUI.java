package fr.alcyons.phiwms_mobile.Classes;

import java.io.Serializable;

/**
 * Created by jessica on 03/10/2017.
 */

public class Demande_PUI implements Serializable {

    private String catégorie;
    private String désignation;
    private String référence;
    private String unité;
    private double stock_pui;
    private double stock_destinataire;
    private int a_preparer;
    private int besoin;
    private int conditionnement;
    private int UID_Reference;
    private int code_produit;

    //Dotation Service
    public Demande_PUI(Dotation dotation, Detail_Dot detail_dot, Produit produit, double stock_pui, double stock_destinataire) {
        this.code_produit = detail_dot.getCode_produit();
        this.catégorie = detail_dot.getCategorie();
        this.désignation = detail_dot.getDesignation();
        this.référence = produit.getRef_fourni();
        this.unité = produit.getUnite();
        this.stock_pui = stock_pui;
        this.stock_destinataire = stock_destinataire;
        this.conditionnement = (int) produit.getCond_distrib();
        this.UID_Reference = detail_dot.get_UID();

        if (dotation.getTauxStockIdeal() < 0) {
            this.besoin = detail_dot.getQte() * dotation.getTauxStockIdeal();
        } else {
            this.besoin = detail_dot.getQte();
        }

        this.a_preparer = this.besoin;

        if (this.stock_destinataire >= this.a_preparer) {
            this.a_preparer = 0;
        } else {
            this.a_preparer = (int) (this.a_preparer - this.stock_destinataire);
        }

        this.a_preparer = (int) Conditionnement_Calcul(this.a_preparer, this.conditionnement);

    }

    // Demande Particulière
    public Demande_PUI(Produit produit, double stock_pui, double stock_destinataire) {
        this.code_produit = produit.getID_produit();
        this.catégorie = produit.getCategorie();
        this.désignation = produit.getDesignation_interne();
        this.référence = produit.getRef_fourni();
        this.unité = produit.getUnite();
        this.stock_pui = stock_pui;
        this.stock_destinataire = stock_destinataire;
        this.conditionnement = (int) produit.getCond_distrib();
        this.UID_Reference = produit.getID_produit();
        this.besoin = 0;
        this.a_preparer = (int) produit.getCond_distrib();
    }

    //Réassort de service
    public Demande_PUI(PH_Reassort_Ligne ph_reassort_ligne, Produit produit, double stock_pui, double stock_destinataire) {
        this.code_produit = ph_reassort_ligne.getProduit_ID();
        this.catégorie = ph_reassort_ligne.getCategorie();
        this.désignation = produit.getDesignation_interne();
        this.référence = produit.getRef_fourni();
        this.unité = produit.getUnite();
        this.stock_pui = stock_pui;
        this.stock_destinataire = stock_destinataire;
        this.conditionnement = (int) produit.getCond_distrib();
        this.UID_Reference = ph_reassort_ligne.get_UID();

        this.besoin = ph_reassort_ligne.getQuantite();

        this.a_preparer = this.besoin;

        if (this.stock_destinataire >= this.a_preparer) {
            this.a_preparer = 0;
        } else {
            this.a_preparer = (int) (this.a_preparer - this.stock_destinataire);
        }

        this.a_preparer = (int) Conditionnement_Calcul(this.a_preparer, this.conditionnement);

    }

    // Demande Dotation PAD
    public Demande_PUI(Dotation_Patient dotation_patient, Produit produit, double stock_pui, double stock_destinataire) {
        this.code_produit = dotation_patient.getCode_Produit();
        this.catégorie = dotation_patient.getCategorie();
        this.désignation = dotation_patient.getDesignation();
        this.référence = produit.getRef_fourni();
        this.unité = produit.getUnite();
        this.stock_pui = stock_pui;
        this.stock_destinataire = stock_destinataire;
        this.conditionnement = (int) produit.getCond_distrib();
        this.UID_Reference = dotation_patient.get_UID();

        this.besoin = (int) dotation_patient.getQté();

        this.a_preparer = this.besoin;

        if (this.stock_destinataire >= this.a_preparer) {
            this.a_preparer = 0;
        } else {
            this.a_preparer = (int) (this.a_preparer - this.stock_destinataire);
        }

        this.a_preparer = (int) Conditionnement_Calcul(this.a_preparer, this.conditionnement);

    }

    // Demande Protocole PAD
    public Demande_PUI(Composants_patient composants_patient, Produit produit, double stock_pui, double stock_destinataire, int multiplicateur) {
        this.code_produit = composants_patient.getCode_produit();
        this.catégorie = composants_patient.getCatégorie();
        this.désignation = composants_patient.getDésignation();
        this.référence = produit.getRef_fourni();
        this.unité = produit.getUnite();
        this.stock_pui = stock_pui;
        this.stock_destinataire = stock_destinataire;
        this.conditionnement = (int) produit.getCond_distrib();
        this.UID_Reference = composants_patient.get_UID();

        this.besoin = (int) composants_patient.getQté() * multiplicateur;

        this.a_preparer = this.besoin;

        if (this.stock_destinataire >= this.a_preparer) {
            this.a_preparer = 0;
        } else {
            this.a_preparer = (int) (this.a_preparer - this.stock_destinataire);
        }

        this.a_preparer = (int) Conditionnement_Calcul(this.a_preparer, this.conditionnement);

    }


    public double Conditionnement_Calcul(Integer qté, Integer conditionnement) {
        double qte_conditionnee = 0;

        switch (conditionnement) {
            case 0:
                qte_conditionnee = qté;
                break;
            case 1:
                qte_conditionnee = qté;
                break;
            default:
                Integer reste = mod(qté, conditionnement);
                if (reste == 0) {
                    qte_conditionnee = qté;
                } else {
                    double nb_conditionnement = Math.ceil(qté / conditionnement);
                    qte_conditionnee = (nb_conditionnement + 1) * conditionnement;
                }
                break;
        }

        return qte_conditionnee;
    }

    private int mod(int x, int y) {
        int result = x % y;
        if (result < 0)
            result += y;
        return result;
    }

    public int getUID_Reference() {
        return UID_Reference;
    }

    public void setUID_Reference(int UID_Reference) {
        this.UID_Reference = UID_Reference;
    }

    public int getCode_produit() {
        return code_produit;
    }

    public void setCode_produit(int code_produit) {
        this.code_produit = code_produit;
    }

    public String getCatégorie() {
        return catégorie;
    }

    public void setCatégorie(String catégorie) {
        this.catégorie = catégorie;
    }

    public String getDésignation() {
        return désignation;
    }

    public void setDésignation(String désignation) {
        this.désignation = désignation;
    }

    public String getRéférence() {
        return référence;
    }

    public void setRéférence(String référence) {
        this.référence = référence;
    }

    public String getUnité() {
        return unité;
    }

    public void setUnité(String unité) {
        this.unité = unité;
    }

    public double getStock_pui() {
        return stock_pui;
    }

    public void setStock_pui(int stock_pui) {
        this.stock_pui = stock_pui;
    }

    public double getStock_destinataire() {
        return stock_destinataire;
    }

    public void setStock_destinataire(int stock_destinataire) {
        this.stock_destinataire = stock_destinataire;
    }

    public int getA_preparer() {
        return a_preparer;
    }

    public void setA_preparer(int a_preparer) {
        this.a_preparer = a_preparer;
    }

    public int getBesoin() {
        return besoin;
    }

    public void setBesoin(int besoin) {
        this.besoin = besoin;
    }

    public int getConditionnement() {
        return conditionnement;
    }

    public void setConditionnement(int conditionnement) {
        this.conditionnement = conditionnement;
    }
}