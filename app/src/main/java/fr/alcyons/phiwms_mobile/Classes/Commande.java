package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 19/06/2017.
 */

public class Commande implements Serializable, Comparable {

    private int ID_commande;
    private String Numero;
    private int ID_Frs;
    private String Commentaire;
    private double Mt_ht;
    private double Mt_TVA;
    private double Taux_TVA;
    private String Date_Cde;
    private String Date_Liv;
    private String Fournisseur;
    private String Ville_Frs;
    private String Devise;
    private double Frais_de_port;
    private String Situation;
    private String Date_echeance;
    private String Modalités;
    private String Facture_Date;
    private double Mt_TTC;
    private String Situation2;
    private String Ref_Depot_Dest;
    private String Ville_Dest;
    private int ID_Depot;
    private String Struct_depot;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private int Delai_Livraison;
    private Boolean Urgent;
    private String Date_Liv2;
    private String CB_Bon_Commande;
    private String Livraison_Autre;
    private Boolean Depot_adresse_2;
    private String Code_analytique;
    private int Protocole_Patient_ID;
    private String Patient_identite;
    private String Patient_IPP;
    private String LivrerDate;
    private String BLNumero;
    private String FactureDate;
    private String FactureNumero;
    private int NbColisTotal_CE;
    private int NbPaletteTotal_CE;
    private int PoidsTotal_CE;
    private String Avaliser_Par_UserInitiale;
    private int Avaliser_Par_UserID;
    private String Avaliser_Le;
    private int Volume_Total;
    private Boolean Import;
    private String Transitaire_Metropole;
    private String Transitaire_Local;
    private String Transport_Type;
    private int phiwms_mobileUUID = -1;

    public Commande(int ID_commande, String numero, int ID_Frs, String commentaire, double mt_ht, double mt_TVA, double taux_TVA, String date_Cde, String date_Liv, String fournisseur, String ville_Frs, String devise, double frais_de_port, String situation, String date_echeance, String modalités, String facture_Date, double mt_TTC, String situation2, String ref_Depot_Dest, String ville_Dest, int ID_Depot, String struct_depot, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, int delai_Livraison, Boolean urgent, String date_Liv2, String CB_Bon_Commande, String livraison_Autre, Boolean depot_adresse_2, String code_analytique, int protocole_Patient_ID, String patient_identite, String patient_IPP, String livrerDate, String BLNumero, String factureDate, String factureNumero, int nbColisTotal_CE, int nbPaletteTotal_CE, int poidsTotal_CE, String avaliser_Par_UserInitiale, int avaliser_Par_UserID, String avaliser_Le, int volume_Total, Boolean anImport, String transitaire_Metropole, String transitaire_Local, String transport_Type) {
        this.ID_commande = ID_commande;
        this.Numero = numero;
        this.ID_Frs = ID_Frs;
        this.Commentaire = commentaire;
        this.Mt_ht = mt_ht;
        this.Mt_TVA = mt_TVA;
        this.Taux_TVA = taux_TVA;
        this.Date_Cde = date_Cde;
        this.Date_Liv = date_Liv;
        this.Fournisseur = fournisseur;
        this.Ville_Frs = ville_Frs;
        this.Devise = devise;
        this.Frais_de_port = frais_de_port;
        this.Situation = situation;
        this.Date_echeance = date_echeance;
        this.Modalités = modalités;
        this.Facture_Date = facture_Date;
        this.Mt_TTC = mt_TTC;
        this.Situation2 = situation2;
        this.Ref_Depot_Dest = ref_Depot_Dest;
        this.Ville_Dest = ville_Dest;
        this.ID_Depot = ID_Depot;
        this.Struct_depot = struct_depot;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.Delai_Livraison = delai_Livraison;
        this.Urgent = urgent;
        this.Date_Liv2 = date_Liv2;
        this.CB_Bon_Commande = CB_Bon_Commande;
        this.Livraison_Autre = livraison_Autre;
        this.Depot_adresse_2 = depot_adresse_2;
        this.Code_analytique = code_analytique;
        this.Protocole_Patient_ID = protocole_Patient_ID;
        this.Patient_identite = patient_identite;
        this.Patient_IPP = patient_IPP;
        this.LivrerDate = livrerDate;
        this.BLNumero = BLNumero;
        this.FactureDate = factureDate;
        this.FactureNumero = factureNumero;
        this.NbColisTotal_CE = nbColisTotal_CE;
        this.NbPaletteTotal_CE = nbPaletteTotal_CE;
        this.PoidsTotal_CE = poidsTotal_CE;
        this.Avaliser_Par_UserInitiale = avaliser_Par_UserInitiale;
        this.Avaliser_Par_UserID = avaliser_Par_UserID;
        this.Avaliser_Le = avaliser_Le;
        this.Volume_Total = volume_Total;
        this.Import = anImport;
        this.Transitaire_Metropole = transitaire_Metropole;
        this.Transitaire_Local = transitaire_Local;
        this.Transport_Type = transport_Type;
    }

    public Commande(int ID_commande, String numero, int ID_Frs, String commentaire, double mt_ht, double mt_TVA, double taux_TVA, String date_Cde, String date_Liv, String fournisseur, String situation, double mt_TTC, String ref_Depot_Dest, int ID_Depot, String struct_depot, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, int delai_Livraison, Boolean urgent, String CB_Bon_Commande, String code_analytique, int protocole_Patient_ID, String patient_identite, String patient_IPP, String livrerDate, String BLNumero, int nbColisTotal_CE, int nbPaletteTotal_CE, int poidsTotal_CE, String avaliser_Par_UserInitiale, int avaliser_Par_UserID, String avaliser_Le) {
        this.ID_commande = ID_commande;
        this.Numero = numero;
        this.ID_Frs = ID_Frs;
        this.Commentaire = commentaire;
        this.Mt_ht = mt_ht;
        this.Mt_TVA = mt_TVA;
        this.Taux_TVA = taux_TVA;
        this.Date_Cde = date_Cde;
        this.Date_Liv = date_Liv;
        this.Fournisseur = fournisseur;
        this.Situation = situation;
        this.Mt_TTC = mt_TTC;
        this.Ref_Depot_Dest = ref_Depot_Dest;
        this.ID_Depot = ID_Depot;
        this.Struct_depot = struct_depot;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.Delai_Livraison = delai_Livraison;
        this.Urgent = urgent;
        this.CB_Bon_Commande = CB_Bon_Commande;
        this.Code_analytique = code_analytique;
        this.Protocole_Patient_ID = protocole_Patient_ID;
        this.Patient_identite = patient_identite;
        this.Patient_IPP = patient_IPP;
        this.LivrerDate = livrerDate;
        this.BLNumero = BLNumero;
        this.NbColisTotal_CE = nbColisTotal_CE;
        this.NbPaletteTotal_CE = nbPaletteTotal_CE;
        this.PoidsTotal_CE = poidsTotal_CE;
        this.Avaliser_Par_UserInitiale = avaliser_Par_UserInitiale;
        this.Avaliser_Par_UserID = avaliser_Par_UserID;
        this.Avaliser_Le = avaliser_Le;
    }

/*    public Commande(JSONObject commandeJson) {
        try {
            this.ID_commande = commandeJson.getInt("ID_commande");
            this.Numero = recupererString(commandeJson.getString("Numero"));
            this.ID_Frs = commandeJson.getInt("ID_Frs");
            this.Commentaire = recupererString(commandeJson.getString("Commentaire"));
            this.Mt_ht = commandeJson.getDouble("Mt_ht");
            this.Mt_TVA = commandeJson.getDouble("Mt_TVA");
            this.Taux_TVA = commandeJson.getDouble("Taux_TVA");
            this.Date_Cde = recupererString(commandeJson.getString("Date_Cde"));
            this.Date_Liv = recupererString(commandeJson.getString("Date_Liv"));
            this.Fournisseur = recupererString(commandeJson.getString("Fournisseur"));
            this.Ville_Frs = recupererString(commandeJson.getString("Ville_Frs"));
            this.Devise = recupererString(commandeJson.getString("Devise"));
            this.Frais_de_port = commandeJson.getDouble("Frais_de_port");
            this.Situation = recupererString(commandeJson.getString("Situation"));
            this.Date_echeance = recupererString(commandeJson.getString("Date_echeance"));
            this.Modalités = recupererString(commandeJson.getString("Modalités"));
            this.Facture_Date = recupererString(commandeJson.getString("Facture_Date"));
            this.Mt_TTC = commandeJson.getDouble("Mt_TTC");
            this.Situation2 = recupererString(commandeJson.getString("Situation2"));
            this.Ref_Depot_Dest = recupererString(commandeJson.getString("Ref_Depot_Dest"));
            this.Ville_Dest = recupererString(commandeJson.getString("Ville_Dest"));
            this.ID_Depot = commandeJson.getInt("ID_Depot");
            this.Struct_depot = recupererString(commandeJson.getString("Struct_depot"));
            this.SYS_DT_MAJ = recupererString(commandeJson.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = recupererString(commandeJson.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = recupererString(commandeJson.getString("SYS_USER_MAJ"));
            this.Delai_Livraison = commandeJson.getInt("Delai_Livraison");
            this.Urgent = recupererBooleen(commandeJson, "Urgent");
            this.Date_Liv2 = recupererString(commandeJson.getString("Date_Liv2"));
            this.CB_Bon_Commande = recupererString(commandeJson.getString("CB_Bon_Commande"));
            this.Livraison_Autre = recupererString(commandeJson.getString("Livraison_Autre"));
            this.Depot_adresse_2 = recupererBooleen(commandeJson, "Depot_adresse_2");
            this.Code_analytique = recupererString(commandeJson.getString("Code_analytique"));
            this.Protocole_Patient_ID = commandeJson.getInt("Protocole_Patient_ID");
            this.Patient_identite = recupererString(commandeJson.getString("Patient_identite"));
            this.Patient_IPP = recupererString(commandeJson.getString("Patient_IPP"));
            this.LivrerDate = recupererString(commandeJson.getString("LivrerDate"));
            this.BLNumero = recupererString(commandeJson.getString("BLNumero"));
            this.FactureDate = recupererString(commandeJson.getString("FactureDate"));
            this.FactureNumero = recupererString(commandeJson.getString("FactureNumero"));
            this.NbColisTotal_CE = commandeJson.getInt("NbColisTotal_CE");
            this.NbPaletteTotal_CE = commandeJson.getInt("NbPaletteTotal_CE");
            this.PoidsTotal_CE = commandeJson.getInt("PoidsTotal_CE");
            this.Avaliser_Par_UserInitiale = recupererString(commandeJson.getString("Avaliser_Par_UserInitiale"));
            this.Avaliser_Par_UserID = commandeJson.getInt("Avaliser_Par_UserID");
            this.Avaliser_Le = recupererString(commandeJson.getString("Avaliser_Le"));
            this.Volume_Total = commandeJson.getInt("Volume_Total");
            this.Import = recupererBooleen(commandeJson, "Import");
            this.Transitaire_Metropole = recupererString(commandeJson.getString("Transitaire_Metropole"));
            this.Transitaire_Local = recupererString(commandeJson.getString("Transitaire_Local"));
            this.Transport_Type = recupererString(commandeJson.getString("Transport_Type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public Commande(JSONObject commandeJson) {
        try {
            this.ID_commande = commandeJson.getInt("ID_commande");
            this.Numero = OutilsGestionClasses.recupererString(commandeJson.getString("Numero"));
            this.Fournisseur = OutilsGestionClasses.recupererString(commandeJson.getString("Fournisseur"));
            this.Situation = OutilsGestionClasses.recupererString(commandeJson.getString("Situation"));
            this.Date_Liv = OutilsGestionClasses.recupererString(commandeJson.getString("Date_Liv"));
            this.Ref_Depot_Dest = OutilsGestionClasses.recupererString(commandeJson.getString("Ref_Depot_Dest"));
            this.Patient_identite = OutilsGestionClasses.recupererString(commandeJson.getString("Patient_identite"));
            this.NbColisTotal_CE = commandeJson.getInt("NbColisTotal_CE");
            this.PoidsTotal_CE = commandeJson.getInt("PoidsTotal_CE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/*    public Commande(Cursor commandeCursor) {
        this.ID_commande = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_ID_COMMANDE_COMMANDE);
        this.Numero = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_NUMERO_COMMANDE);
        this.ID_Frs = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_ID_FRS_COMMANDE);
        this.Commentaire = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_COMMENTAIRE_COMMANDE);
        this.Mt_ht = commandeCursor.getDouble(CommandeOpenHelper.Constantes.NUM_COL_MT_HT_COMMANDE);
        this.Mt_TVA = commandeCursor.getDouble(CommandeOpenHelper.Constantes.NUM_COL_MT_TVA_COMMANDE);
        this.Taux_TVA = commandeCursor.getDouble(CommandeOpenHelper.Constantes.NUM_COL_TAUX_TVA_COMMANDE);
        this.Date_Cde = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_DATE_CDE_COMMANDE);
        this.Date_Liv = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_DATE_LIV_COMMANDE);
        this.Fournisseur = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_FOURNISSEUR_COMMANDE);
        this.Ville_Frs = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_VILLE_FRS_COMMANDE);
        this.Devise = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_DEVISE_COMMANDE);
        this.Frais_de_port = commandeCursor.getDouble(CommandeOpenHelper.Constantes.NUM_COL_FRAIS_DE_PORT_COMMANDE);
        this.Situation = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_SITUATION_COMMANDE);
        this.Date_echeance = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_DATE_ECHEANCE_COMMANDE);
        this.Modalités = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_MODALITES_COMMANDE);
        this.Facture_Date = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_FACTURE_DATE_COMMANDE);
        this.Mt_TTC = commandeCursor.getDouble(CommandeOpenHelper.Constantes.NUM_COL_MT_TTC_COMMANDE);
        this.Situation2 = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_SITUATION2_COMMANDE);
        this.Ref_Depot_Dest = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_REF_DEPOT_DEST_COMMANDE);
        this.Ville_Dest = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_VILLE_DEST_COMMANDE);
        this.ID_Depot = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_ID_DEPOT_COMMANDE);
        this.Struct_depot = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_STRUCT_DEPOT_COMMANDE);
        this.SYS_DT_MAJ = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_COMMANDE);
        this.SYS_HEURE_MAJ = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_COMMANDE);
        this.SYS_USER_MAJ = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_COMMANDE);
        this.Delai_Livraison = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_DELAI_LIVRAISON_COMMANDE);
        this.Urgent = recupererBooleen(commandeCursor, CommandeOpenHelper.Constantes.NUM_COL_URGENT_COMMANDE);
        this.Date_Liv2 = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_DATE_LIV2_COMMANDE);
        this.CB_Bon_Commande = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_CB_BON_COMMANDE_COMMANDE);
        this.Livraison_Autre = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_LIVRAISON_AUTRE_COMMANDE);
        this.Depot_adresse_2 = recupererBooleen(commandeCursor, CommandeOpenHelper.Constantes.NUM_COL_DEPOT_ADRESSE_2_COMMANDE);
        this.Code_analytique = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_CODE_ANALYTIQUE_COMMANDE);
        this.Protocole_Patient_ID = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_PROTOCOLE_PATIENT_ID_COMMANDE);
        this.Patient_identite = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_PATIENT_IDENTITE_COMMANDE);
        this.Patient_IPP = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_PATIENT_IPP_COMMANDE);
        this.LivrerDate = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_LIVRERDATE_COMMANDE);
        this.BLNumero = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_BLNUMERO_COMMANDE);
        this.FactureDate = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_FACTUREDATE_COMMANDE);
        this.FactureNumero = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_FACTURENUMERO_COMMANDE);
        this.NbColisTotal_CE = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_NBCOLISTOTAL_CE_COMMANDE);
        this.NbPaletteTotal_CE = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_NBPALETTETOTAL_CE_COMMANDE);
        this.PoidsTotal_CE = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_POIDSTOTAL_CE_COMMANDE);
        this.Avaliser_Par_UserInitiale = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_AVALISER_PAR_USERINITIALE_COMMANDE);
        this.Avaliser_Par_UserID = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_AVALISER_PAR_USERID_COMMANDE);
        this.Avaliser_Le = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_AVALISER_LE_COMMANDE);
        this.Volume_Total = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_VOLUME_TOTAL_COMMANDE);
        this.Import = recupererBooleen(commandeCursor, CommandeOpenHelper.Constantes.NUM_COL_IMPORT_COMMANDE);
        this.Transitaire_Metropole = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_TRANSITAIRE_METROPOLE_COMMANDE);
        this.Transitaire_Local = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_TRANSITAIRE_LOCAL_COMMANDE);
        this.Transport_Type = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_TRANSPORT_TYPE_COMMANDE);
        this.phiwms_mobileUUID = commandeCursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }*/

    public Commande(Cursor commandeCursor) {
        this.ID_commande = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_ID_COMMANDE_COMMANDE);
        this.Numero = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_NUMERO_COMMANDE);
        this.Date_Liv = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_DATE_LIV_COMMANDE);
        this.Fournisseur = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_FOURNISSEUR_COMMANDE);
        this.Situation = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_SITUATION_COMMANDE);
        this.Ref_Depot_Dest = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_REF_DEPOT_DEST_COMMANDE);
        this.BLNumero = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_BLNUMERO_COMMANDE);
        this.Patient_identite = commandeCursor.getString(CommandeOpenHelper.Constantes.NUM_COL_PATIENT_IDENTITE_COMMANDE);
        this.NbColisTotal_CE = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_NBCOLISTOTAL_CE_COMMANDE);
        this.PoidsTotal_CE = commandeCursor.getInt(CommandeOpenHelper.Constantes.NUM_COL_POIDSTOTAL_CE_COMMANDE);
        this.phiwms_mobileUUID = commandeCursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getID_commande() {
        return ID_commande;
    }

    public void setID_commande(int ID_commande) {
        this.ID_commande = ID_commande;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public int getID_Frs() {
        return ID_Frs;
    }

    public void setID_Frs(int ID_Frs) {
        this.ID_Frs = ID_Frs;
    }

    public String getCommentaire() {
        return Commentaire;
    }

    public void setCommentaire(String commentaire) {
        Commentaire = commentaire;
    }

    public double getMt_ht() {
        return Mt_ht;
    }

    public void setMt_ht(double mt_ht) {
        Mt_ht = mt_ht;
    }

    public double getMt_TVA() {
        return Mt_TVA;
    }

    public void setMt_TVA(double mt_TVA) {
        Mt_TVA = mt_TVA;
    }

    public double getTaux_TVA() {
        return Taux_TVA;
    }

    public void setTaux_TVA(double taux_TVA) {
        Taux_TVA = taux_TVA;
    }

    public String getDate_Cde() {
        return Date_Cde;
    }

    public void setDate_Cde(String date_Cde) {
        Date_Cde = date_Cde;
    }

    public String getDate_Liv() {
        return Date_Liv;
    }

    public void setDate_Liv(String date_Liv) {
        Date_Liv = date_Liv;
    }

    public String getFournisseur() {
        return Fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        Fournisseur = fournisseur;
    }

    public String getVille_Frs() {
        return Ville_Frs;
    }

    public void setVille_Frs(String ville_Frs) {
        Ville_Frs = ville_Frs;
    }

    public String getDevise() {
        return Devise;
    }

    public void setDevise(String devise) {
        Devise = devise;
    }

    public double getFrais_de_port() {
        return Frais_de_port;
    }

    public void setFrais_de_port(double frais_de_port) {
        Frais_de_port = frais_de_port;
    }

    public String getSituation() {
        return Situation;
    }

    public void setSituation(String situation) {
        Situation = situation;
    }

    public String getDate_echeance() {
        return Date_echeance;
    }

    public void setDate_echeance(String date_echeance) {
        Date_echeance = date_echeance;
    }

    public String getModalités() {
        return Modalités;
    }

    public void setModalités(String modalités) {
        Modalités = modalités;
    }

    public String getFacture_Date() {
        return Facture_Date;
    }

    public void setFacture_Date(String facture_Date) {
        Facture_Date = facture_Date;
    }

    public double getMt_TTC() {
        return Mt_TTC;
    }

    public void setMt_TTC(double mt_TTC) {
        Mt_TTC = mt_TTC;
    }

    public String getSituation2() {
        return Situation2;
    }

    public void setSituation2(String situation2) {
        Situation2 = situation2;
    }

    public String getRef_Depot_Dest() {
        return Ref_Depot_Dest;
    }

    public void setRef_Depot_Dest(String ref_Depot_Dest) {
        Ref_Depot_Dest = ref_Depot_Dest;
    }

    public String getVille_Dest() {
        return Ville_Dest;
    }

    public void setVille_Dest(String ville_Dest) {
        Ville_Dest = ville_Dest;
    }

    public int getID_Depot() {
        return ID_Depot;
    }

    public void setID_Depot(int ID_Depot) {
        this.ID_Depot = ID_Depot;
    }

    public String getStruct_depot() {
        return Struct_depot;
    }

    public void setStruct_depot(String struct_depot) {
        Struct_depot = struct_depot;
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

    public int getDelai_Livraison() {
        return Delai_Livraison;
    }

    public void setDelai_Livraison(int delai_Livraison) {
        Delai_Livraison = delai_Livraison;
    }

    public Boolean getUrgent() {
        return Urgent;
    }

    public void setUrgent(Boolean urgent) {
        Urgent = urgent;
    }

    public String getDate_Liv2() {
        return Date_Liv2;
    }

    public void setDate_Liv2(String date_Liv2) {
        Date_Liv2 = date_Liv2;
    }

    public String getCB_Bon_Commande() {
        return CB_Bon_Commande;
    }

    public void setCB_Bon_Commande(String CB_Bon_Commande) {
        this.CB_Bon_Commande = CB_Bon_Commande;
    }

    public String getLivraison_Autre() {
        return Livraison_Autre;
    }

    public void setLivraison_Autre(String livraison_Autre) {
        Livraison_Autre = livraison_Autre;
    }

    public Boolean getDepot_adresse_2() {
        return Depot_adresse_2;
    }

    public void setDepot_adresse_2(Boolean depot_adresse_2) {
        Depot_adresse_2 = depot_adresse_2;
    }

    public String getCode_analytique() {
        return Code_analytique;
    }

    public void setCode_analytique(String code_analytique) {
        Code_analytique = code_analytique;
    }

    public int getProtocole_Patient_ID() {
        return Protocole_Patient_ID;
    }

    public void setProtocole_Patient_ID(int protocole_Patient_ID) {
        Protocole_Patient_ID = protocole_Patient_ID;
    }

    public String getPatient_identite() {
        return Patient_identite;
    }

    public void setPatient_identite(String patient_identite) {
        Patient_identite = patient_identite;
    }

    public String getPatient_IPP() {
        return Patient_IPP;
    }

    public void setPatient_IPP(String patient_IPP) {
        Patient_IPP = patient_IPP;
    }

    public String getLivrerDate() {
        return LivrerDate;
    }

    public void setLivrerDate(String livrerDate) {
        LivrerDate = livrerDate;
    }

    public String getBLNumero() {
        return BLNumero;
    }

    public void setBLNumero(String BLNumero) {
        this.BLNumero = BLNumero;
    }

    public String getFactureDate() {
        return FactureDate;
    }

    public void setFactureDate(String factureDate) {
        FactureDate = factureDate;
    }

    public String getFactureNumero() {
        return FactureNumero;
    }

    public void setFactureNumero(String factureNumero) {
        FactureNumero = factureNumero;
    }

    public int getNbColisTotal_CE() {
        return NbColisTotal_CE;
    }

    public void setNbColisTotal_CE(int nbColisTotal_CE) {
        NbColisTotal_CE = nbColisTotal_CE;
    }

    public int getNbPaletteTotal_CE() {
        return NbPaletteTotal_CE;
    }

    public void setNbPaletteTotal_CE(int nbPaletteTotal_CE) {
        NbPaletteTotal_CE = nbPaletteTotal_CE;
    }

    public int getPoidsTotal_CE() {
        return PoidsTotal_CE;
    }

    public void setPoidsTotal_CE(int poidsTotal_CE) {
        PoidsTotal_CE = poidsTotal_CE;
    }

    public String getAvaliser_Par_UserInitiale() {
        return Avaliser_Par_UserInitiale;
    }

    public void setAvaliser_Par_UserInitiale(String avaliser_Par_UserInitiale) {
        Avaliser_Par_UserInitiale = avaliser_Par_UserInitiale;
    }

    public int getAvaliser_Par_UserID() {
        return Avaliser_Par_UserID;
    }

    public void setAvaliser_Par_UserID(int avaliser_Par_UserID) {
        Avaliser_Par_UserID = avaliser_Par_UserID;
    }

    public String getAvaliser_Le() {
        return Avaliser_Le;
    }

    public void setAvaliser_Le(String avaliser_Le) {
        Avaliser_Le = avaliser_Le;
    }

    public int getVolume_Total() {
        return Volume_Total;
    }

    public void setVolume_Total(int volume_Total) {
        Volume_Total = volume_Total;
    }

    public Boolean getImport() {
        return Import;
    }

    public void setImport(Boolean anImport) {
        Import = anImport;
    }

    public String getTransitaire_Metropole() {
        return Transitaire_Metropole;
    }

    public void setTransitaire_Metropole(String transitaire_Metropole) {
        Transitaire_Metropole = transitaire_Metropole;
    }

    public String getTransitaire_Local() {
        return Transitaire_Local;
    }

    public void setTransitaire_Local(String transitaire_Local) {
        Transitaire_Local = transitaire_Local;
    }

    public String getTransport_Type() {
        return Transport_Type;
    }

    public void setTransport_Type(String transport_Type) {
        Transport_Type = transport_Type;
    }

    public String getChaineFiltreProduit(SQLiteDatabase db, String commandeNumero) {
        String filter = this.toString() + " ";

        List<PH_Reliquat> phReliquatList = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeNumero);
        for (int i = 0; i < phReliquatList.size(); i++) {
            PH_Reliquat phReliquatCourant = phReliquatList.get(i);
            filter += phReliquatCourant.getdesignationCourte();
        }

        return filter;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Commande)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Commande commande = (Commande) obj;

        if (this.getphiwms_mobileUUID() == commande.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getID_commande() > commande.getID_commande() ? 1 : -1;
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID_commande", ID_commande);
            jsonObject.put("Numero", Numero);
            jsonObject.put("ID_Frs", ID_Frs);
            jsonObject.put("Commentaire", Commentaire);
            jsonObject.put("Mt_ht", Mt_ht);
            jsonObject.put("Mt_TVA", Mt_TVA);
            jsonObject.put("Taux_TVA", Taux_TVA);
            jsonObject.put("Date_Cde", Date_Cde);
            jsonObject.put("Date_Liv", Date_Liv);
            jsonObject.put("Fournisseur", Fournisseur);
            jsonObject.put("Ville_Frs", Ville_Frs);
            jsonObject.put("Devise", Devise);
            jsonObject.put("Frais_de_port", Frais_de_port);
            jsonObject.put("Situation", Situation);
            jsonObject.put("Date_echeance", Date_echeance);
            jsonObject.put("Modalités", Modalités);
            jsonObject.put("Facture_Date", Facture_Date);
            jsonObject.put("Mt_TTC", Mt_TTC);
            jsonObject.put("Situation2", Situation2);
            jsonObject.put("Ref_Depot_Dest", Ref_Depot_Dest);
            jsonObject.put("Ville_Dest", Ville_Dest);
            jsonObject.put("ID_Depot", ID_Depot);
            jsonObject.put("Struct_depot", Struct_depot);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("Delai_Livraison", Delai_Livraison);
            jsonObject.put("Urgent", Urgent);
            jsonObject.put("Date_Liv2", Date_Liv2);
            jsonObject.put("CB_Bon_Commande", CB_Bon_Commande);
            jsonObject.put("Livraison_Autre", Livraison_Autre);
            jsonObject.put("Depot_adresse_2", Depot_adresse_2);
            jsonObject.put("Code_analytique", Code_analytique);
            jsonObject.put("Protocole_Patient_ID", Protocole_Patient_ID);
            jsonObject.put("Patient_identite", Patient_identite);
            jsonObject.put("Patient_IPP", Patient_IPP);
            jsonObject.put("LivrerDate", LivrerDate);
            jsonObject.put("BLNumero", BLNumero);
            jsonObject.put("FactureDate", FactureDate);
            jsonObject.put("FactureNumero", FactureNumero);
            jsonObject.put("NbColisTotal_CE", NbColisTotal_CE);
            jsonObject.put("NbPaletteTotal_CE", NbPaletteTotal_CE);
            jsonObject.put("PoidsTotal_CE", PoidsTotal_CE);
            jsonObject.put("Avaliser_Par_UserInitiale", Avaliser_Par_UserInitiale);
            jsonObject.put("Avaliser_Par_UserID", Avaliser_Par_UserID);
            jsonObject.put("Avaliser_Le", Avaliser_Le);
            jsonObject.put("Volume_Total", Volume_Total);
            jsonObject.put("Import", Import);
            jsonObject.put("Transitaire_Metropole", Transitaire_Metropole);
            jsonObject.put("Transitaire_Local", Transitaire_Local);
            jsonObject.put("Transport_Type", Transport_Type);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }

}
