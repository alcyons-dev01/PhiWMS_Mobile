package fr.alcyons.phiwms_mobile.Classes;

import android.database.sqlite.SQLiteDatabase;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;

/**
 * Created by quentinlanusse on 06/07/2017.
 */

public class PH_Preparation_Ligne_VerrouPharmacie_Adapte {

    private String designation;
    private String produitRef;
    private String nbColis;
    private String qteRALLot;
    private String qteParLot;
    private String numLot;
    private String peremptionDate;
    private String emplacementAdressage;
    private Stock_Lot_Emplacement_Light stockLotEmplacement;
    private PH_Preparation_Ligne phPreparationLigne;
    private PH_Preparation phPreparation;
    private Produit produitCorrespondant;
    private boolean Suivi_Par_Serie;
    private boolean Serialiser_Reception;
    private String SerieNumero;
    private boolean Verouiller;
    private SQLiteDatabase db;


    public PH_Preparation_Ligne_VerrouPharmacie_Adapte(String designation, String produitRef, String nbColis, String qteRALLot, String qteParLot, String numLot, String peremptionDate, String emplacementAdressage, Stock_Lot_Emplacement_Light stockLotEmplacement, PH_Preparation_Ligne phPreparationLigne, PH_Preparation phPreparation, boolean Suivi_Par_Serie, boolean Serialiser_Reception, String SerieNumero, SQLiteDatabase db, boolean Verrouiller) {
        this.designation = designation;
        this.produitRef = produitRef;
        this.nbColis = nbColis;
        this.qteRALLot = qteRALLot;
        this.qteParLot = qteParLot;
        this.numLot = numLot;
        this.peremptionDate = peremptionDate;
        this.emplacementAdressage = emplacementAdressage;
        this.stockLotEmplacement = stockLotEmplacement;
        this.phPreparationLigne = phPreparationLigne;
        this.phPreparation = phPreparation;
        this.Suivi_Par_Serie = Suivi_Par_Serie;
        this.Serialiser_Reception = Serialiser_Reception;
        this.SerieNumero = SerieNumero;
        this.db = db;
        this.Verouiller = Verrouiller;

        this.produitCorrespondant = ProduitOpenHelper.getProduitByID(db, stockLotEmplacement.getProduit_Code());
    }

    public PH_Preparation_Ligne_VerrouPharmacie_Adapte(String designation, String produitRef, String nbColis, String qteRALLot, String qteParLot, String numLot, String peremptionDate, PH_Preparation_Ligne phPreparationLigne, PH_Preparation phPreparation, boolean Suivi_Par_Serie, boolean Serialiser_Reception, String SerieNumero, SQLiteDatabase db, boolean Verrouiller) {
        this.designation = designation;
        this.produitRef = produitRef;
        this.nbColis = nbColis;
        this.qteRALLot = qteRALLot;
        this.qteParLot = qteParLot;
        this.numLot = numLot;
        this.peremptionDate = peremptionDate;
        this.phPreparationLigne = phPreparationLigne;
        this.phPreparation = phPreparation;
        this.Suivi_Par_Serie = Suivi_Par_Serie;
        this.Serialiser_Reception = Serialiser_Reception;
        this.SerieNumero = SerieNumero;
        this.db = db;
        this.Verouiller = Verrouiller;

        this.produitCorrespondant = ProduitOpenHelper.getProduitByID(db, phPreparationLigne.getProduitID());
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getProduitRef() {
        return produitRef;
    }

    public void setProduitRef(String produitRef) {
        this.produitRef = produitRef;
    }

    public String getNbColis() {
        return nbColis;
    }

    public void setNbColis(String nbColis) {
        this.nbColis = nbColis;
    }

    public String getQteRALLot() {
        return qteRALLot;
    }

    public void setQteRALLot(String qteRALLot) {
        this.qteRALLot = qteRALLot;
    }

    public String getQteParLot() {
        return qteParLot;
    }

    public void setQteParLot(String qteParLot) {
        this.qteParLot = qteParLot;
    }

    public String getNumLot() {
        return numLot;
    }

    public void setNumLot(String numLot) {
        this.numLot = numLot;
    }

    public String getPeremptionDate() {
        return peremptionDate;
    }

    public void setPeremptionDate(String peremptionDate) {
        this.peremptionDate = peremptionDate;
    }

    public String getEmplacementAdressage() {
        return emplacementAdressage;
    }

    public void setEmplacementAdressage(String emplacementAdressage) {
        this.emplacementAdressage = emplacementAdressage;
    }

    public Stock_Lot_Emplacement_Light getStockLotEmplacement() {
        return stockLotEmplacement;
    }

    public void setStockLotEmplacement(Stock_Lot_Emplacement_Light stockLotEmplacement) {
        this.stockLotEmplacement = stockLotEmplacement;
    }

    public PH_Preparation_Ligne getPhPreparationLigne() {
        return phPreparationLigne;
    }

    public void setPhPreparationLigne(PH_Preparation_Ligne phPreparationLigne) {
        this.phPreparationLigne = phPreparationLigne;
    }

    public PH_Preparation getPhPreparation() {
        return phPreparation;
    }

    public void setPhPreparation(PH_Preparation phPreparation) {
        this.phPreparation = phPreparation;
    }

    public boolean isSuivi_Par_Serie() {
        return Suivi_Par_Serie;
    }

    public void setSuivi_Par_Serie(boolean suivi_Par_Serie) {
        Suivi_Par_Serie = suivi_Par_Serie;
    }

    public boolean isSerialiser_Reception() {
        return Serialiser_Reception;
    }

    public void setSerialiser_Reception(boolean serialiser_Reception) {
        Serialiser_Reception = serialiser_Reception;
    }

    public String getSerieNumero() {
        return SerieNumero;
    }

    public void setSerieNumero(String serieNumero) {
        SerieNumero = serieNumero;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public Produit getProduitCorrespondant() {
        return produitCorrespondant;
    }

    public void setProduitCorrespondant(Produit produitCorrespondant) {
        this.produitCorrespondant = produitCorrespondant;
    }

    public boolean isVerouiller() {
        return Verouiller;
    }

    public void setVerouiller(boolean verouiller) {
        Verouiller = verouiller;
    }
}
