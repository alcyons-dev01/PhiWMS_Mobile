package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.FactureOpenHelper;
import fr.alcyons.phimr4.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 27/06/2017.
 */

public class Facture implements Serializable, Comparable {

    private int ID_Facture;
    private String Numéro;
    private double Fact_HT;
    private double Fact_TVA;
    private double Fact_TTC;
    private double Com_HT;
    private double Com_TVA;
    private double Com_TTC;
    private int Code_fournisseu;
    private String Fournisseur;
    private String Date_fact;
    private String Echéance;
    private double Solde;
    private String Soldée_le;
    private String Statut;
    private double Ecart_I_HT;
    private double Ecart_I_TVA;
    private double Ecart_I_TTC;
    private double Calc_HT;
    private double Calc_TVA;
    private double Calc_TTC;
    private double Frais_HT;
    private double Frais_TVA;
    private double Frais_TTC;
    private String date_comptable;
    private String Date_ecriture;
    private String Piece;
    private String Devise;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String Mode_reglement;
    private Boolean Pharmacie;
    private Boolean Litige;
    private Boolean Transmise;
    private int Num_Semaine;
    private String Reception_Date;
    private int Bordereau_UID;
    private Boolean Import;
    private String CommandeNumero;
    private int phiMR4UUID = -1;

    public Facture(int ID_Facture, String numéro, double fact_HT, double fact_TVA, double fact_TTC, double com_HT, double com_TVA, double com_TTC, int code_fournisseu, String fournisseur, String date_fact, String echéance, double solde, String soldée_le, String statut, double ecart_I_HT, double ecart_I_TVA, double ecart_I_TTC, double calc_HT, double calc_TVA, double calc_TTC, double frais_HT, double frais_TVA, double frais_TTC, String date_comptable, String date_ecriture, String piece, String devise, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, String mode_reglement, Boolean pharmacie, Boolean litige, Boolean transmise, int num_Semaine, String reception_Date, int bordereau_UID, Boolean anImport, String commandeNumero) {
        this.ID_Facture = ID_Facture;
        this.Numéro = numéro;
        this.Fact_HT = fact_HT;
        this.Fact_TVA = fact_TVA;
        this.Fact_TTC = fact_TTC;
        this.Com_HT = com_HT;
        this.Com_TVA = com_TVA;
        this.Com_TTC = com_TTC;
        this.Code_fournisseu = code_fournisseu;
        this.Fournisseur = fournisseur;
        this.Date_fact = date_fact;
        this.Echéance = echéance;
        this.Solde = solde;
        this.Soldée_le = soldée_le;
        this.Statut = statut;
        this.Ecart_I_HT = ecart_I_HT;
        this.Ecart_I_TVA = ecart_I_TVA;
        this.Ecart_I_TTC = ecart_I_TTC;
        this.Calc_HT = calc_HT;
        this.Calc_TVA = calc_TVA;
        this.Calc_TTC = calc_TTC;
        this.Frais_HT = frais_HT;
        this.Frais_TVA = frais_TVA;
        this.Frais_TTC = frais_TTC;
        this.date_comptable = date_comptable;
        this.Date_ecriture = date_ecriture;
        this.Piece = piece;
        this.Devise = devise;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.Mode_reglement = mode_reglement;
        this.Pharmacie = pharmacie;
        this.Litige = litige;
        this.Transmise = transmise;
        this.Num_Semaine = num_Semaine;
        this.Reception_Date = reception_Date;
        this.Bordereau_UID = bordereau_UID;
        this.Import = anImport;
        this.CommandeNumero = commandeNumero;
    }

    public Facture(JSONObject jsonObject) {
        try {
            this.ID_Facture = jsonObject.getInt("ID_Facture");
            this.Numéro = OutilsGestionClasses.recupererString(jsonObject.getString("Numéro"));
            this.Fact_HT = jsonObject.getDouble("Fact_HT");
            this.Fact_TVA = jsonObject.getDouble("Fact_TVA");
            this.Fact_TTC = jsonObject.getDouble("Fact_TTC");
            this.Com_HT = jsonObject.getDouble("Com_HT");
            this.Com_TVA = jsonObject.getDouble("Com_TVA");
            this.Com_TTC = jsonObject.getDouble("Com_TTC");
            this.Code_fournisseu = jsonObject.getInt("Code_fournisseu");
            this.Fournisseur = OutilsGestionClasses.recupererString(jsonObject.getString("Fournisseur"));
            this.Date_fact = OutilsGestionClasses.recupererString(jsonObject.getString("Date_fact"));
            this.Echéance = OutilsGestionClasses.recupererString(jsonObject.getString("Echéance"));
            this.Solde = jsonObject.getDouble("Solde");
            this.Soldée_le = OutilsGestionClasses.recupererString(jsonObject.getString("Soldée_le"));
            this.Statut = OutilsGestionClasses.recupererString(jsonObject.getString("Statut"));
            this.Ecart_I_HT = jsonObject.getDouble("Ecart_I_HT");
            this.Ecart_I_TVA = jsonObject.getDouble("Ecart_I_TVA");
            this.Ecart_I_TTC = jsonObject.getDouble("Ecart_I_TTC");
            this.Calc_HT = jsonObject.getDouble("Calc_HT");
            this.Calc_TVA = jsonObject.getDouble("Calc_TVA");
            this.Calc_TTC = jsonObject.getDouble("Calc_TTC");
            this.Frais_HT = jsonObject.getDouble("Frais_HT");
            this.Frais_TVA = jsonObject.getDouble("Frais_TVA");
            this.Frais_TTC = jsonObject.getDouble("Frais_TTC");
            this.date_comptable = OutilsGestionClasses.recupererString(jsonObject.getString("date_comptable"));
            this.Date_ecriture = OutilsGestionClasses.recupererString(jsonObject.getString("Date_ecriture"));
            this.Piece = OutilsGestionClasses.recupererString(jsonObject.getString("Piece"));
            this.Devise = OutilsGestionClasses.recupererString(jsonObject.getString("Devise"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.Mode_reglement = OutilsGestionClasses.recupererString(jsonObject.getString("Mode_reglement"));
            this.Pharmacie = OutilsGestionClasses.recupererBooleen(jsonObject, "Pharmacie");
            this.Litige = OutilsGestionClasses.recupererBooleen(jsonObject, "Litige");
            this.Transmise = OutilsGestionClasses.recupererBooleen(jsonObject, "Transmise");
            this.Num_Semaine = jsonObject.getInt("Num_Semaine");
            this.Reception_Date = OutilsGestionClasses.recupererString(jsonObject.getString("Reception_Date"));
            this.Bordereau_UID = jsonObject.getInt("Bordereau_UID");
            this.Import = OutilsGestionClasses.recupererBooleen(jsonObject, "Import");
            this.CommandeNumero = OutilsGestionClasses.recupererString(jsonObject.getString("CommandeNumero"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Facture(Cursor cursor) {
        this.ID_Facture = cursor.getInt(FactureOpenHelper.Constantes.NUM_COL_ID_FACTURE_FACTURE);
        this.Numéro = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_NUMERO_FACTURE);
        this.Fact_HT = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_FACT_HT_FACTURE);
        this.Fact_TVA = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_FACT_TVA_FACTURE);
        this.Fact_TTC = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_FACT_TTC_FACTURE);
        this.Com_HT = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_COM_HT_FACTURE);
        this.Com_TVA = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_COM_TVA_FACTURE);
        this.Com_TTC = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_COM_TTC_FACTURE);
        this.Code_fournisseu = cursor.getInt(FactureOpenHelper.Constantes.NUM_COL_CODE_FOURNISSEU_FACTURE);
        this.Fournisseur = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_FOURNISSEUR_FACTURE);
        this.Date_fact = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_DATE_FACT_FACTURE);
        this.Echéance = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_ECHEANCE_FACTURE);
        this.Solde = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_SOLDE_FACTURE);
        this.Soldée_le = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_SOLDEE_LE_FACTURE);
        this.Statut = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_STATUT_FACTURE);
        this.Ecart_I_HT = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_ECART_I_HT_FACTURE);
        this.Ecart_I_TVA = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_ECART_I_TVA_FACTURE);
        this.Ecart_I_TTC = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_ECART_I_TTC_FACTURE);
        this.Calc_HT = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_CALC_HT_FACTURE);
        this.Calc_TVA = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_CALC_TVA_FACTURE);
        this.Calc_TTC = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_CALC_TTC_FACTURE);
        this.Frais_HT = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_FRAIS_HT_FACTURE);
        this.Frais_TVA = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_FRAIS_TVA_FACTURE);
        this.Frais_TTC = cursor.getDouble(FactureOpenHelper.Constantes.NUM_COL_FRAIS_TTC_FACTURE);
        this.date_comptable = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_DATE_COMPTABLE_FACTURE);
        this.Date_ecriture = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_DATE_ECRITURE_FACTURE);
        this.Piece = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_PIECE_FACTURE);
        this.Devise = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_DEVISE_FACTURE);
        this.SYS_DT_MAJ = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_FACTURE);
        this.SYS_HEURE_MAJ = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_FACTURE);
        this.SYS_USER_MAJ = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_FACTURE);
        this.Mode_reglement = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_MODE_REGLEMENT_FACTURE);
        this.Pharmacie = OutilsGestionClasses.recupererBooleen(cursor, FactureOpenHelper.Constantes.NUM_COL_PHARMACIE_FACTURE);
        this.Litige = OutilsGestionClasses.recupererBooleen(cursor, FactureOpenHelper.Constantes.NUM_COL_LITIGE_FACTURE);
        this.Transmise = OutilsGestionClasses.recupererBooleen(cursor, FactureOpenHelper.Constantes.NUM_COL_TRANSMISE_FACTURE);
        this.Num_Semaine = cursor.getInt(FactureOpenHelper.Constantes.NUM_COL_NUM_SEMAINE_FACTURE);
        this.Reception_Date = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_RECEPTION_DATE_FACTURE);
        this.Bordereau_UID = cursor.getInt(FactureOpenHelper.Constantes.NUM_COL_BORDEREAU_UID_FACTURE);
        this.Import = OutilsGestionClasses.recupererBooleen(cursor, FactureOpenHelper.Constantes.NUM_COL_IMPORT_FACTURE);
        this.CommandeNumero = cursor.getString(FactureOpenHelper.Constantes.NUM_COL_COMMANDENUMERO_FACTURE);
        this.phiMR4UUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public int getID_Facture() {
        return ID_Facture;
    }

    public void setID_Facture(int ID_Facture) {
        this.ID_Facture = ID_Facture;
    }

    public String getNuméro() {
        return Numéro;
    }

    public void setNuméro(String numéro) {
        Numéro = numéro;
    }

    public double getFact_HT() {
        return Fact_HT;
    }

    public void setFact_HT(double fact_HT) {
        Fact_HT = fact_HT;
    }

    public double getFact_TVA() {
        return Fact_TVA;
    }

    public void setFact_TVA(double fact_TVA) {
        Fact_TVA = fact_TVA;
    }

    public double getFact_TTC() {
        return Fact_TTC;
    }

    public void setFact_TTC(double fact_TTC) {
        Fact_TTC = fact_TTC;
    }

    public double getCom_HT() {
        return Com_HT;
    }

    public void setCom_HT(double com_HT) {
        Com_HT = com_HT;
    }

    public double getCom_TVA() {
        return Com_TVA;
    }

    public void setCom_TVA(double com_TVA) {
        Com_TVA = com_TVA;
    }

    public double getCom_TTC() {
        return Com_TTC;
    }

    public void setCom_TTC(double com_TTC) {
        Com_TTC = com_TTC;
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

    public String getDate_fact() {
        return Date_fact;
    }

    public void setDate_fact(String date_fact) {
        Date_fact = date_fact;
    }

    public String getEchéance() {
        return Echéance;
    }

    public void setEchéance(String echéance) {
        Echéance = echéance;
    }

    public double getSolde() {
        return Solde;
    }

    public void setSolde(double solde) {
        Solde = solde;
    }

    public String getSoldée_le() {
        return Soldée_le;
    }

    public void setSoldée_le(String soldée_le) {
        Soldée_le = soldée_le;
    }

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }

    public double getEcart_I_HT() {
        return Ecart_I_HT;
    }

    public void setEcart_I_HT(double ecart_I_HT) {
        Ecart_I_HT = ecart_I_HT;
    }

    public double getEcart_I_TVA() {
        return Ecart_I_TVA;
    }

    public void setEcart_I_TVA(double ecart_I_TVA) {
        Ecart_I_TVA = ecart_I_TVA;
    }

    public double getEcart_I_TTC() {
        return Ecart_I_TTC;
    }

    public void setEcart_I_TTC(double ecart_I_TTC) {
        Ecart_I_TTC = ecart_I_TTC;
    }

    public double getCalc_HT() {
        return Calc_HT;
    }

    public void setCalc_HT(double calc_HT) {
        Calc_HT = calc_HT;
    }

    public double getCalc_TVA() {
        return Calc_TVA;
    }

    public void setCalc_TVA(double calc_TVA) {
        Calc_TVA = calc_TVA;
    }

    public double getCalc_TTC() {
        return Calc_TTC;
    }

    public void setCalc_TTC(double calc_TTC) {
        Calc_TTC = calc_TTC;
    }

    public double getFrais_HT() {
        return Frais_HT;
    }

    public void setFrais_HT(double frais_HT) {
        Frais_HT = frais_HT;
    }

    public double getFrais_TVA() {
        return Frais_TVA;
    }

    public void setFrais_TVA(double frais_TVA) {
        Frais_TVA = frais_TVA;
    }

    public double getFrais_TTC() {
        return Frais_TTC;
    }

    public void setFrais_TTC(double frais_TTC) {
        Frais_TTC = frais_TTC;
    }

    public String getDate_comptable() {
        return date_comptable;
    }

    public void setDate_comptable(String date_comptable) {
        this.date_comptable = date_comptable;
    }

    public String getDate_ecriture() {
        return Date_ecriture;
    }

    public void setDate_ecriture(String date_ecriture) {
        Date_ecriture = date_ecriture;
    }

    public String getPiece() {
        return Piece;
    }

    public void setPiece(String piece) {
        Piece = piece;
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

    public String getMode_reglement() {
        return Mode_reglement;
    }

    public void setMode_reglement(String mode_reglement) {
        Mode_reglement = mode_reglement;
    }

    public Boolean getPharmacie() {
        return Pharmacie;
    }

    public void setPharmacie(Boolean pharmacie) {
        Pharmacie = pharmacie;
    }

    public Boolean getLitige() {
        return Litige;
    }

    public void setLitige(Boolean litige) {
        Litige = litige;
    }

    public Boolean getTransmise() {
        return Transmise;
    }

    public void setTransmise(Boolean transmise) {
        Transmise = transmise;
    }

    public int getNum_Semaine() {
        return Num_Semaine;
    }

    public void setNum_Semaine(int num_Semaine) {
        Num_Semaine = num_Semaine;
    }

    public String getReception_Date() {
        return Reception_Date;
    }

    public void setReception_Date(String reception_Date) {
        Reception_Date = reception_Date;
    }

    public int getBordereau_UID() {
        return Bordereau_UID;
    }

    public void setBordereau_UID(int bordereau_UID) {
        Bordereau_UID = bordereau_UID;
    }

    public Boolean getImport() {
        return Import;
    }

    public void setImport(Boolean anImport) {
        Import = anImport;
    }

    public String getCommandeNumero() {
        return CommandeNumero;
    }

    public void setCommandeNumero(String commandeNumero) {
        CommandeNumero = commandeNumero;
    }

    JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_Facture", ID_Facture);
            jsonObject.put("Numéro", Numéro);
            jsonObject.put("Fact_HT", Fact_HT);
            jsonObject.put("Fact_TVA", Fact_TVA);
            jsonObject.put("Fact_TTC", Fact_TTC);
            jsonObject.put("Com_HT", Com_HT);
            jsonObject.put("Com_TVA", Com_TVA);
            jsonObject.put("Com_TTC", Com_TTC);
            jsonObject.put("Code_fournisseu", Code_fournisseu);
            jsonObject.put("Fournisseur", Fournisseur);
            jsonObject.put("Date_fact", Date_fact);
            jsonObject.put("Echéance", Echéance);
            jsonObject.put("Solde", Solde);
            jsonObject.put("Soldée_le", Soldée_le);
            jsonObject.put("Statut", Statut);
            jsonObject.put("Ecart_I_HT", Ecart_I_HT);
            jsonObject.put("Ecart_I_TVA", Ecart_I_TVA);
            jsonObject.put("Ecart_I_TTC", Ecart_I_TTC);
            jsonObject.put("Calc_HT", Calc_HT);
            jsonObject.put("Calc_TVA", Calc_TVA);
            jsonObject.put("Calc_TTC", Calc_TTC);
            jsonObject.put("Frais_HT", Frais_HT);
            jsonObject.put("Frais_TVA", Frais_TVA);
            jsonObject.put("Frais_TTC", Frais_TTC);
            jsonObject.put("date_comptable", date_comptable);
            jsonObject.put("Date_ecriture", Date_ecriture);
            jsonObject.put("Piece", Piece);
            jsonObject.put("Devise", Devise);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("Mode_reglement", Mode_reglement);
            jsonObject.put("Pharmacie", Pharmacie);
            jsonObject.put("Litige", Litige);
            jsonObject.put("Transmise", Transmise);
            jsonObject.put("Num_Semaine", Num_Semaine);
            jsonObject.put("Reception_Date", Reception_Date);
            jsonObject.put("Bordereau_UID", Bordereau_UID);
            jsonObject.put("Import", Import);
            jsonObject.put("CommandeNumero", CommandeNumero);
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

        if (!(obj instanceof Facture)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Facture facture = (Facture) obj;

        if (this.getPhiMR4UUID() == facture.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getID_Facture() > facture.getID_Facture() ? 1 : -1;
        }
    }
}
