package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class PH_Preparation_Ligne implements Serializable, Comparable {

    private int PreparationID;
    private int _UID;
    private int produitID;
    private String produitDesignation;
    private int Qte_APreparer;
    private int Qte_livrer;
    private Boolean Livrer;
    private Boolean Valider;
    private String ValidationDate;
    private String produitReference;
    private String ZoneDepot;
    private String produitCategorie;
    private int Qte_RAL;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private double produitCondDistrib;
    private double produitPUHT;
    private Boolean Suivi_Par_Lot;
    private int patientID;
    private String PatientNom;
    private String PrescripteurNom;
    private String prescripteurReference;
    private int Ordre_Impression;
    private int Prescription_ID;
    private String LotNumero;
    private String PeremptionDate;
    private double produitPoids;
    private double produitTVA;
    private double Montant_HT;
    private double Montant_TTC;
    private double PoidsTotal;
    private String depot_Destinataire_Reference;
    private String utilisation_Date_Prevue;
    private int Qte_besoin;
    private int Qte_StockSaisie;
    private int Qte_Demander;
    private String EmplacementParDefaut;
    private int Qte_preparer;
    private boolean accepter;
    private boolean Suivi_Par_Serie;
    private boolean Serialiser_Reception;
    private String SerieNumero;
    private Integer _UID_4D;
    private boolean Verrouiller;
    private int phiwms_mobileUUID = -1;

    public PH_Preparation_Ligne(int preparationID, int _UID, int produitID, String produitDesignation, int qte_APreparer, int qte_livrer, String produitReference, String zoneDepot, String produitCategorie, int qte_RAL, int produitCondDistrib, boolean suivi_Par_Lot, String lotNumero, String depot_Destinataire_Reference, int qte_besoin, int qte_Demander, int qte_preparer)
    {
        this.PreparationID = preparationID;
        this._UID = _UID;
        this.produitID = produitID;
        this.produitDesignation = produitDesignation;
        this.Qte_APreparer = qte_APreparer;
        this.Qte_livrer = qte_livrer;
        this.produitReference = produitReference;
        this.ZoneDepot = zoneDepot;
        this.produitCategorie = produitCategorie;
        this.Qte_RAL = qte_RAL;
        this.produitCondDistrib = produitCondDistrib;
        this.Suivi_Par_Lot = suivi_Par_Lot;
        this.LotNumero = lotNumero;
        this.depot_Destinataire_Reference = depot_Destinataire_Reference;
        this.Qte_besoin = qte_besoin;
        this.Qte_Demander = qte_Demander;
        this.Qte_preparer = qte_preparer;
    }

    public PH_Preparation_Ligne(int preparationID, int _UID, int produitID, String produitDesignation, int qte_APreparer, int qte_livrer, Boolean livrer, Boolean valider, String validationDate, String produitReference, String zoneDepot, String produitCategorie, int qte_RAL, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, double produitCondDistrib, double produitPUHT, Boolean suivi_Par_Lot, int patientID, String patientNom, String prescripteurNom, String prescripteurReference, int ordre_Impression, int prescription_ID, String lotNumero, String peremptionDate, double produitPoids, double produitTVA, double montant_HT, double montant_TTC, double poidsTotal, String depot_Destinataire_Reference, String utilisation_Date_Prevue, int qte_besoin, int qte_StockSaisie, int qte_Demander, String emplacementParDefaut, int qte_preparer, boolean accepter, int _UID_4D) {
        this.PreparationID = preparationID;
        this._UID = _UID;
        this.produitID = produitID;
        this.produitDesignation = produitDesignation;
        this.Qte_APreparer = qte_APreparer;
        this.Qte_livrer = qte_livrer;
        this.Livrer = livrer;
        this.Valider = valider;
        this.ValidationDate = validationDate;
        this.produitReference = produitReference;
        this.ZoneDepot = zoneDepot;
        this.produitCategorie = produitCategorie;
        this.Qte_RAL = qte_RAL;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.produitCondDistrib = produitCondDistrib;
        this.produitPUHT = produitPUHT;
        this.Suivi_Par_Lot = suivi_Par_Lot;
        this.patientID = patientID;
        this.PatientNom = patientNom;
        this.PrescripteurNom = prescripteurNom;
        this.prescripteurReference = prescripteurReference;
        this.Ordre_Impression = ordre_Impression;
        this.Prescription_ID = prescription_ID;
        this.LotNumero = lotNumero;
        this.PeremptionDate = peremptionDate;
        this.produitPoids = produitPoids;
        this.produitTVA = produitTVA;
        this.Montant_HT = montant_HT;
        this.Montant_TTC = montant_TTC;
        this.PoidsTotal = poidsTotal;
        this.depot_Destinataire_Reference = depot_Destinataire_Reference;
        this.utilisation_Date_Prevue = utilisation_Date_Prevue;
        this.Qte_besoin = qte_besoin;
        this.Qte_StockSaisie = qte_StockSaisie;
        this.Qte_Demander = qte_Demander;
        this.EmplacementParDefaut = emplacementParDefaut;
        this.Qte_preparer = qte_preparer;
        this.accepter = accepter;
        this._UID_4D = _UID_4D;
        this.Verrouiller = true;
    }

    public PH_Preparation_Ligne(PH_Preparation_Ligne ph_preparation_ligne) {
        this.PreparationID = ph_preparation_ligne.getPreparationID();
        this._UID = ph_preparation_ligne.get_UID();
        this.produitID = ph_preparation_ligne.getProduitID();
        this.produitDesignation = ph_preparation_ligne.getProduitDesignation();
        this.Qte_APreparer = ph_preparation_ligne.getQte_APreparer();
        this.Qte_livrer = ph_preparation_ligne.getQte_livrer();
        this.Livrer = ph_preparation_ligne.getLivrer();
        this.Valider = ph_preparation_ligne.getValider();
        this.ValidationDate = ph_preparation_ligne.getValidationDate();
        this.produitReference = ph_preparation_ligne.getProduitReference();
        this.ZoneDepot = ph_preparation_ligne.getZoneDepot();
        this.produitCategorie = ph_preparation_ligne.getProduitCategorie();
        this.Qte_RAL = ph_preparation_ligne.getQte_RAL();
        this.SYS_DT_MAJ = ph_preparation_ligne.getSYS_DT_MAJ();
        this.SYS_HEURE_MAJ = ph_preparation_ligne.getSYS_HEURE_MAJ();
        this.SYS_USER_MAJ = ph_preparation_ligne.getSYS_USER_MAJ();
        this.produitCondDistrib = ph_preparation_ligne.getProduitCondDistrib();
        this.produitPUHT = ph_preparation_ligne.getProduitPUHT();
        this.Suivi_Par_Lot = ph_preparation_ligne.getSuivi_Par_Lot();
        this.patientID = ph_preparation_ligne.getPatientID();
        this.PatientNom = ph_preparation_ligne.getPatientNom();
        this.PrescripteurNom = ph_preparation_ligne.getPrescripteurNom();
        this.prescripteurReference = ph_preparation_ligne.getPrescripteurReference();
        this.Ordre_Impression = ph_preparation_ligne.getOrdre_Impression();
        this.Prescription_ID = ph_preparation_ligne.getPrescription_ID();
        this.LotNumero = ph_preparation_ligne.getLotNumero();
        this.PeremptionDate = ph_preparation_ligne.getPeremptionDate();
        this.produitPoids = ph_preparation_ligne.getProduitPoids();
        this.produitTVA = ph_preparation_ligne.getProduitTVA();
        this.Montant_HT = ph_preparation_ligne.getMontant_HT();
        this.Montant_TTC = ph_preparation_ligne.getMontant_TTC();
        this.PoidsTotal = ph_preparation_ligne.getPoidsTotal();
        this.depot_Destinataire_Reference = ph_preparation_ligne.getDepot_Destinataire_Reference();
        this.utilisation_Date_Prevue = ph_preparation_ligne.getUtilisation_Date_Prevue();
        this.Qte_besoin = ph_preparation_ligne.getQte_besoin();
        this.Qte_StockSaisie = ph_preparation_ligne.getQte_StockSaisie();
        this.Qte_Demander = ph_preparation_ligne.getQte_Demander();
        this.EmplacementParDefaut = ph_preparation_ligne.getEmplacementParDefaut();
        this.Qte_preparer = ph_preparation_ligne.getQte_preparer();
        this.accepter = ph_preparation_ligne.getAccepter();
        this.Suivi_Par_Serie = ph_preparation_ligne.isSuivi_Par_Serie();
        this.Serialiser_Reception = ph_preparation_ligne.isSerialiser_Reception();
        this.SerieNumero = ph_preparation_ligne.getSerieNumero();
        this.phiwms_mobileUUID = ph_preparation_ligne.getphiwms_mobileUUID();
        this._UID_4D = ph_preparation_ligne.get_UID_4D();
        this.Verrouiller = ph_preparation_ligne.isVerrouiller();
    }

/*
    public PH_Preparation_Ligne(JSONObject jsonObject) {
        try {
            this.PreparationID = jsonObject.getInt("PreparationID");
            this._UID = jsonObject.getInt("_UID");
            this.produitID = jsonObject.getInt("produitID");
            this.produitDesignation = recupererString(jsonObject.getString("produitDesignation"));
            this.Qte_APreparer = jsonObject.getInt("Qte_APreparer");
            this.Qte_livrer = jsonObject.getInt("Qte_livrer");
            this.Livrer = recupererBooleen(jsonObject, "Livrer");
            this.Valider = recupererBooleen(jsonObject, "Valider");
            this.ValidationDate = recupererString(jsonObject.getString("ValidationDate"));
            this.produitReference = recupererString(jsonObject.getString("produitReference"));
            this.ZoneDepot = recupererString(jsonObject.getString("ZoneDepot"));
            this.produitCategorie = recupererString(jsonObject.getString("produitCategorie"));
            this.Qte_RAL = jsonObject.getInt("Qte_RAL");
            this.SYS_DT_MAJ = recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.produitCondDistrib = jsonObject.getDouble("produitCondDistrib");
            this.produitPUHT = jsonObject.getDouble("produitPUHT");
            this.Suivi_Par_Lot = recupererBooleen(jsonObject, "Suivi_Par_Lot");
            this.patientID = jsonObject.getInt("patientID");
            this.PatientNom = recupererString(jsonObject.getString("PatientNom"));
            this.PrescripteurNom = recupererString(jsonObject.getString("PrescripteurNom"));
            this.prescripteurReference = recupererString(jsonObject.getString("prescripteurReference"));
            this.Ordre_Impression = jsonObject.getInt("Ordre_Impression");
            this.Prescription_ID = jsonObject.getInt("Prescription_ID");
            this.LotNumero = recupererString(jsonObject.getString("LotNumero"));
            this.PeremptionDate = recupererString(jsonObject.getString("PeremptionDate"));
            this.produitPoids = jsonObject.getDouble("produitPoids");
            this.produitTVA = jsonObject.getDouble("produitTVA");
            this.Montant_HT = jsonObject.getDouble("Montant_HT");
            this.Montant_TTC = jsonObject.getDouble("Montant_TTC");
            this.PoidsTotal = jsonObject.getDouble("PoidsTotal");
            this.depot_Destinataire_Reference = recupererString(jsonObject.getString("depot_Destinataire_Reference"));
            this.utilisation_Date_Prevue = recupererString(jsonObject.getString("utilisation_Date_Prevue"));
            this.Qte_besoin = jsonObject.getInt("Qte_besoin");
            this.Qte_StockSaisie = jsonObject.getInt("Qte_StockSaisie");
            this.Qte_Demander = jsonObject.getInt("Qte_Demander");
            this.EmplacementParDefaut = recupererString(jsonObject.getString("EmplacementParDefaut"));
            this.Qte_preparer = jsonObject.getInt("Qte_preparer");
            this.accepter = jsonObject.getBoolean("Accepter");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
*/

    public PH_Preparation_Ligne(JSONObject jsonObject) {
        try {
            this.PreparationID = jsonObject.getInt("PreparationID");
            this._UID = jsonObject.getInt("_UID");
            this.produitID = jsonObject.getInt("produitID");
            this.produitDesignation = OutilsGestionClasses.recupererString(jsonObject.getString("produitDesignation"));
            if(jsonObject.isNull("Qte_APreparer"))
            {
                this.Qte_APreparer = 0;
            }
            else
            {
                this.Qte_APreparer = jsonObject.getInt("Qte_APreparer");
            }

            if(jsonObject.isNull("Qte_livrer"))
            {
                this.Qte_livrer = 0;
            }
            else
            {
                this.Qte_livrer = jsonObject.getInt("Qte_livrer");
            }

            if(jsonObject.isNull("Qte_Demander"))
            {
                this.Qte_Demander = 0;
            }
            else
            {
                this.Qte_Demander = jsonObject.getInt("Qte_Demander");
            }

            if(jsonObject.isNull("Qte_preparer"))
            {
                this.Qte_preparer = 0;
            }
            else
            {
                this.Qte_preparer = jsonObject.getInt("Qte_preparer");
            }

            if(jsonObject.isNull("produitPoids"))
            {
                this.produitPoids = 0;
            }
            else
            {
                this.produitPoids = jsonObject.getDouble("produitPoids");
            }

            if(jsonObject.isNull("PoidsTotal"))
            {
                this.PoidsTotal = 0;
            }
            else
            {
                this.PoidsTotal = jsonObject.getDouble("PoidsTotal");
            }

            if(jsonObject.isNull("Qte_RAL"))
            {
                this.Qte_RAL = 0;
            }
            else
            {
                this.Qte_RAL = jsonObject.getInt("Qte_RAL");
            }

            if(jsonObject.isNull("_UID_4D"))
            {
                this._UID_4D = 0;
            }
            else
            {
                this._UID_4D = jsonObject.getInt("_UID_4D");
            }

            this.produitReference = OutilsGestionClasses.recupererString(jsonObject.getString("produitReference"));
            this.LotNumero = OutilsGestionClasses.recupererString(jsonObject.getString("LotNumero"));
            this.PeremptionDate = OutilsGestionClasses.recupererString(jsonObject.getString("PeremptionDate"));
            this.ZoneDepot = OutilsGestionClasses.recupererString(jsonObject.getString("ZoneDepot"));
            this.EmplacementParDefaut = OutilsGestionClasses.recupererString(jsonObject.getString("EmplacementParDefaut"));
            this.Suivi_Par_Lot = OutilsGestionClasses.recupererBooleen(jsonObject,"Suivi_Par_Lot");
            this.Suivi_Par_Serie = OutilsGestionClasses.recupererBooleen(jsonObject,"Suivi_Par_Serie");
            this.Serialiser_Reception = OutilsGestionClasses.recupererBooleen(jsonObject,"Serialiser_Reception");
            this.SerieNumero = OutilsGestionClasses.recupererString(jsonObject.getString("SerieNumero"));
            this.Verrouiller = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public PH_Preparation_Ligne(JSONObject jsonObject, int PreparationID) {
        try {
            this.PreparationID = PreparationID;
            this._UID = jsonObject.getInt("_UID");
            this.produitID = jsonObject.getInt("produitID");
            this.produitDesignation = OutilsGestionClasses.recupererString(jsonObject.getString("produitDesignation"));
            if(jsonObject.isNull("Qte_APreparer"))
            {
                this.Qte_APreparer = 0;
            }
            else
            {
                this.Qte_APreparer = jsonObject.getInt("Qte_APreparer");
            }

            if(jsonObject.isNull("Qte_livrer"))
            {
                this.Qte_livrer = 0;
            }
            else
            {
                this.Qte_livrer = jsonObject.getInt("Qte_livrer");
            }

            if(jsonObject.isNull("Qte_Demander"))
            {
                this.Qte_Demander = 0;
            }
            else
            {
                this.Qte_Demander = jsonObject.getInt("Qte_Demander");
            }

            if(jsonObject.isNull("Qte_preparer"))
            {
                this.Qte_preparer = 0;
            }
            else
            {
                this.Qte_preparer = jsonObject.getInt("Qte_preparer");
            }

            if(jsonObject.isNull("produitPoids"))
            {
                this.produitPoids = 0;
            }
            else
            {
                this.produitPoids = jsonObject.getDouble("produitPoids");
            }

            if(jsonObject.isNull("PoidsTotal"))
            {
                this.PoidsTotal = 0;
            }
            else
            {
                this.PoidsTotal = jsonObject.getDouble("PoidsTotal");
            }

            if(jsonObject.isNull("Qte_RAL"))
            {
                this.Qte_RAL = 0;
            }
            else
            {
                this.Qte_RAL = jsonObject.getInt("Qte_RAL");
            }

            if(jsonObject.isNull("_UID_4D"))
            {
                this._UID_4D = 0;
            }
            else
            {
                this._UID_4D = jsonObject.getInt("_UID_4D");
            }

            this.produitReference = OutilsGestionClasses.recupererString(jsonObject.getString("produitReference"));
            this.LotNumero = OutilsGestionClasses.recupererString(jsonObject.getString("LotNumero"));
            this.PeremptionDate = OutilsGestionClasses.recupererString(jsonObject.getString("PeremptionDate"));
            this.ZoneDepot = OutilsGestionClasses.recupererString(jsonObject.getString("ZoneDepot"));
            this.EmplacementParDefaut = OutilsGestionClasses.recupererString(jsonObject.getString("EmplacementParDefaut"));
            this.Suivi_Par_Lot = OutilsGestionClasses.recupererBooleen(jsonObject,"Suivi_Par_Lot");
            this.Suivi_Par_Serie = OutilsGestionClasses.recupererBooleen(jsonObject,"Suivi_Par_Serie");
            this.Serialiser_Reception = OutilsGestionClasses.recupererBooleen(jsonObject,"Serialiser_Reception");
            this.SerieNumero = OutilsGestionClasses.recupererString(jsonObject.getString("SerieNumero"));
            this.Verrouiller = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public PH_Preparation_Ligne(Cursor cursor) {
        this.PreparationID = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PREPARATIONID_PH_PREPARATION_LIGNE);
        this._UID = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL__UID_PH_PREPARATION_LIGNE);
        this.produitID = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITID_PH_PREPARATION_LIGNE);
        this.produitDesignation = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE);
        this.Qte_APreparer = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_APREPARER_PH_PREPARATION_LIGNE);
        this.Qte_livrer = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_LIVRER_PH_PREPARATION_LIGNE);
        this.Livrer = OutilsGestionClasses.recupererBooleen(cursor, PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_LIVRER_PH_PREPARATION_LIGNE);
        this.Valider = OutilsGestionClasses.recupererBooleen(cursor, PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_VALIDER_PH_PREPARATION_LIGNE);
        this.ValidationDate = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_VALIDATIONDATE_PH_PREPARATION_LIGNE);
        this.produitReference = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITREFERENCE_PH_PREPARATION_LIGNE);
        this.ZoneDepot = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_ZONEDEPOT_PH_PREPARATION_LIGNE);
        this.produitCategorie = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE);
        this.Qte_RAL = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_RAL_PH_PREPARATION_LIGNE);
        this.SYS_DT_MAJ = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PH_PREPARATION_LIGNE);
        this.SYS_HEURE_MAJ = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PH_PREPARATION_LIGNE);
        this.SYS_USER_MAJ = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PH_PREPARATION_LIGNE);
        this.produitCondDistrib = cursor.getDouble(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITCONDDISTRIB_PH_PREPARATION_LIGNE);
        this.produitPUHT = cursor.getDouble(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITPUHT_PH_PREPARATION_LIGNE);
        this.Suivi_Par_Lot = OutilsGestionClasses.recupererBooleen(cursor, PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_SUIVI_PAR_LOT_PH_PREPARATION_LIGNE);
        this.patientID = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PATIENTID_PH_PREPARATION_LIGNE);
        this.PatientNom = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PATIENTNOM_PH_PREPARATION_LIGNE);
        this.PrescripteurNom = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRESCRIPTEURNOM_PH_PREPARATION_LIGNE);
        this.prescripteurReference = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION_LIGNE);
        this.Ordre_Impression = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_ORDRE_IMPRESSION_PH_PREPARATION_LIGNE);
        this.Prescription_ID = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRESCRIPTION_ID_PH_PREPARATION_LIGNE);
        this.LotNumero = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_LOTNUMERO_PH_PREPARATION_LIGNE);
        this.PeremptionDate = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PEREMPTIONDATE_PH_PREPARATION_LIGNE);
        this.produitPoids = cursor.getDouble(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITPOIDS_PH_PREPARATION_LIGNE);
        this.produitTVA = cursor.getDouble(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITTVA_PH_PREPARATION_LIGNE);
        this.Montant_HT = cursor.getDouble(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_MONTANT_HT_PH_PREPARATION_LIGNE);
        this.Montant_TTC = cursor.getDouble(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_MONTANT_TTC_PH_PREPARATION_LIGNE);
        this.PoidsTotal = cursor.getDouble(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_POIDSTOTAL_PH_PREPARATION_LIGNE);
        this.depot_Destinataire_Reference = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_DEPOT_DESTINATAIRE_REFERENCE_PH_PREPARATION_LIGNE);
        this.utilisation_Date_Prevue = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_UTILISATION_DATE_PREVUE_PH_PREPARATION_LIGNE);
        this.Qte_besoin = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_BESOIN_PH_PREPARATION_LIGNE);
        this.Qte_StockSaisie = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_STOCKSAISIE_PH_PREPARATION_LIGNE);
        this.Qte_Demander = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_DEMANDER_PH_PREPARATION_LIGNE);
        this.EmplacementParDefaut = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_EMPLACEMENTPARDEFAUT_PH_PREPARATION_LIGNE);
        this.Qte_preparer = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_PREPARER_PH_PREPARATION_LIGNE);
        this.accepter = OutilsGestionClasses.recupererBooleen(cursor, PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_ACCEPTER_PH_PREPARATION_LIGNE);
        this.Suivi_Par_Serie = OutilsGestionClasses.recupererBooleen(cursor, PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_SUIVI_PAR_SERIE);
        this.Serialiser_Reception = OutilsGestionClasses.recupererBooleen(cursor, PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_SERIALISER_RECEPTION);
        this.SerieNumero = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_SERIE_NUMERO);
        this._UID_4D = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_UID_4D);
        this.Verrouiller = OutilsGestionClasses.recupererBooleen(cursor, PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_VERROUILLER);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }
/*

    public PH_Preparation_Ligne(Cursor cursor) {
        this.PreparationID = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PREPARATIONID_PH_PREPARATION_LIGNE);
        this._UID = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL__UID_PH_PREPARATION_LIGNE);
        this.produitID = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITID_PH_PREPARATION_LIGNE);
        this.produitDesignation = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE);
        this.Qte_APreparer = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_APREPARER_PH_PREPARATION_LIGNE);
        this.Qte_livrer = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_LIVRER_PH_PREPARATION_LIGNE);
        this.produitReference = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PRODUITREFERENCE_PH_PREPARATION_LIGNE);
        this.Qte_Demander = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_DEMANDER_PH_PREPARATION_LIGNE);
        this.accepter = recupererBooleen(cursor, PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_ACCEPTER_PH_PREPARATION_LIGNE);
        this.ZoneDepot = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_ZONEDEPOT_PH_PREPARATION_LIGNE);
        this.EmplacementParDefaut = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_EMPLACEMENTPARDEFAUT_PH_PREPARATION_LIGNE);
        this.PeremptionDate = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_PEREMPTIONDATE_PH_PREPARATION_LIGNE);
        this.Qte_preparer = cursor.getInt(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_QTE_PREPARER_PH_PREPARATION_LIGNE);
        this.LotNumero = cursor.getString(PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_LOTNUMERO_PH_PREPARATION_LIGNE);
        this.Suivi_Par_Lot = recupererBooleen(cursor, PH_Preparation_LigneOpenHelper.Constantes.NUM_COL_SUIVI_PAR_LOT_PH_PREPARATION_LIGNE);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }
*/


    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getPreparationID() {
        return PreparationID;
    }

    public void setPreparationID(int preparationID) {
        PreparationID = preparationID;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public int getProduitID() {
        return produitID;
    }

    public void setProduitID(int produitID) {
        this.produitID = produitID;
    }

    public String getProduitDesignation() {
        return produitDesignation;
    }

    public void setProduitDesignation(String produitDesignation) {
        this.produitDesignation = produitDesignation;
    }

    public int getQte_APreparer() {
        return Qte_APreparer;
    }

    public void setQte_APreparer(int qte_APreparer) {
        Qte_APreparer = qte_APreparer;
    }

    public int getQte_livrer() {
        return Qte_livrer;
    }

    public void setQte_livrer(int qte_livrer) {
        Qte_livrer = qte_livrer;
    }

    public Boolean getLivrer() {
        return Livrer;
    }

    public void setLivrer(Boolean livrer) {
        Livrer = livrer;
    }

    public Boolean getValider() {
        return Valider;
    }

    public void setValider(Boolean valider) {
        Valider = valider;
    }

    public String getValidationDate() {
        return ValidationDate;
    }

    public void setValidationDate(String validationDate) {
        ValidationDate = validationDate;
    }

    public String getProduitReference() {
        return produitReference;
    }

    public void setProduitReference(String produitReference) {
        this.produitReference = produitReference;
    }

    public String getZoneDepot() {
        return ZoneDepot;
    }

    public void setZoneDepot(String zoneDepot) {
        ZoneDepot = zoneDepot;
    }

    public String getProduitCategorie() {
        return produitCategorie;
    }

    public void setProduitCategorie(String produitCategorie) {
        this.produitCategorie = produitCategorie;
    }

    public int getQte_RAL() {
        return Qte_RAL;
    }

    public void setQte_RAL(int qte_RAL) {
        Qte_RAL = qte_RAL;
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

    public double getProduitCondDistrib() {
        return produitCondDistrib;
    }

    public void setProduitCondDistrib(double produitCondDistrib) {
        this.produitCondDistrib = produitCondDistrib;
    }

    public double getProduitPUHT() {
        return produitPUHT;
    }

    public void setProduitPUHT(double produitPUHT) {
        this.produitPUHT = produitPUHT;
    }

    public Boolean getSuivi_Par_Lot() {
        return Suivi_Par_Lot;
    }

    public void setSuivi_Par_Lot(Boolean suivi_Par_Lot) {
        Suivi_Par_Lot = suivi_Par_Lot;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public String getPatientNom() {
        return PatientNom;
    }

    public void setPatientNom(String patientNom) {
        PatientNom = patientNom;
    }

    public String getPrescripteurNom() {
        return PrescripteurNom;
    }

    public void setPrescripteurNom(String prescripteurNom) {
        PrescripteurNom = prescripteurNom;
    }

    public String getPrescripteurReference() {
        return prescripteurReference;
    }

    public void setPrescripteurReference(String prescripteurReference) {
        this.prescripteurReference = prescripteurReference;
    }

    public int getOrdre_Impression() {
        return Ordre_Impression;
    }

    public void setOrdre_Impression(int ordre_Impression) {
        Ordre_Impression = ordre_Impression;
    }

    public int getPrescription_ID() {
        return Prescription_ID;
    }

    public void setPrescription_ID(int prescription_ID) {
        Prescription_ID = prescription_ID;
    }

    public String getLotNumero() {
        return LotNumero;
    }

    public void setLotNumero(String lotNumero) {
        LotNumero = lotNumero;
    }

    public String getPeremptionDate() {
        return PeremptionDate;
    }

    public void setPeremptionDate(String peremptionDate) {
        PeremptionDate = peremptionDate;
    }

    public double getProduitPoids() {
        return produitPoids;
    }

    public void setProduitPoids(double produitPoids) {
        this.produitPoids = produitPoids;
    }

    public double getProduitTVA() {
        return produitTVA;
    }

    public void setProduitTVA(double produitTVA) {
        this.produitTVA = produitTVA;
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

    public double getPoidsTotal() {
        return PoidsTotal;
    }

    public void setPoidsTotal(double poidsTotal) {
        PoidsTotal = poidsTotal;
    }

    public String getDepot_Destinataire_Reference() {
        return depot_Destinataire_Reference;
    }

    public void setDepot_Destinataire_Reference(String depot_Destinataire_Reference) {
        this.depot_Destinataire_Reference = depot_Destinataire_Reference;
    }

    public String getUtilisation_Date_Prevue() {
        return utilisation_Date_Prevue;
    }

    public void setUtilisation_Date_Prevue(String utilisation_Date_Prevue) {
        this.utilisation_Date_Prevue = utilisation_Date_Prevue;
    }

    public int getQte_besoin() {
        return Qte_besoin;
    }

    public void setQte_besoin(int qte_besoin) {
        Qte_besoin = qte_besoin;
    }

    public int getQte_StockSaisie() {
        return Qte_StockSaisie;
    }

    public void setQte_StockSaisie(int qte_StockSaisie) {
        Qte_StockSaisie = qte_StockSaisie;
    }

    public int getQte_Demander() {
        return Qte_Demander;
    }

    public void setQte_Demander(int qte_Demander) {
        Qte_Demander = qte_Demander;
    }

    public String getEmplacementParDefaut() {
        return EmplacementParDefaut;
    }

    public void setEmplacementParDefaut(String emplacementParDefaut) {
        EmplacementParDefaut = emplacementParDefaut;
    }

    public int getQte_preparer() {
        return Qte_preparer;
    }

    public void setQte_preparer(int qte_preparer) {
        Qte_preparer = qte_preparer;
    }

    public boolean getAccepter() {
        return accepter;
    }

    public void setAccepter(boolean accepter) {
        this.accepter = accepter;
    }

    public boolean isAccepter() {
        return accepter;
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

    public Integer get_UID_4D() {
        return _UID_4D;
    }

    public void set_UID_4D(Integer _UID_4D) {
        this._UID_4D = _UID_4D;
    }

    public boolean isVerrouiller() {
        return Verrouiller;
    }

    public void setVerrouiller(boolean verrouiller) {
        Verrouiller = verrouiller;
    }

    @Override
    public String toString() {
        return getProduitDesignation();
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_Preparation_Ligne)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        PH_Preparation_Ligne ph_preparation_ligne = (PH_Preparation_Ligne) obj;

        if (this.getphiwms_mobileUUID() == ph_preparation_ligne.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.get_UID() > ph_preparation_ligne.get_UID() ? 1 : -1;
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PreparationID", PreparationID);
            jsonObject.put("_UID", _UID);
            jsonObject.put("produitID", produitID);
            jsonObject.put("produitDesignation", produitDesignation);
            jsonObject.put("Qte_APreparer", Qte_APreparer);
            jsonObject.put("Qte_livrer", Qte_livrer);
            jsonObject.put("Livrer", Livrer);
            jsonObject.put("Valider", Valider);
            jsonObject.put("ValidationDate", ValidationDate);
            jsonObject.put("produitReference", produitReference);
            jsonObject.put("ZoneDepot", ZoneDepot);
            jsonObject.put("produitCategorie", produitCategorie);
            jsonObject.put("Qte_RAL", Qte_RAL);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("produitCondDistrib", produitCondDistrib);
            jsonObject.put("produitPUHT", produitPUHT);
            jsonObject.put("Suivi_Par_Lot", Suivi_Par_Lot);
            jsonObject.put("patientID", patientID);
            jsonObject.put("PatientNom", PatientNom);
            jsonObject.put("PrescripteurNom", PrescripteurNom);
            jsonObject.put("prescripteurReference", prescripteurReference);
            jsonObject.put("Ordre_Impression", Ordre_Impression);
            jsonObject.put("Prescription_ID", Prescription_ID);
            jsonObject.put("LotNumero", LotNumero);
            jsonObject.put("PeremptionDate", PeremptionDate);
            jsonObject.put("produitPoids", produitPoids);
            jsonObject.put("produitTVA", produitTVA);
            jsonObject.put("Montant_HT", Montant_HT);
            jsonObject.put("Montant_TTC", Montant_TTC);
            jsonObject.put("PoidsTotal", PoidsTotal);
            jsonObject.put("depot_Destinataire_Reference", depot_Destinataire_Reference);
            jsonObject.put("utilisation_Date_Prevue", utilisation_Date_Prevue);
            jsonObject.put("Qte_besoin", Qte_besoin);
            jsonObject.put("Qte_StockSaisie", Qte_StockSaisie);
            jsonObject.put("Qte_Demander", Qte_Demander);
            jsonObject.put("EmplacementParDefaut", EmplacementParDefaut);
            jsonObject.put("Qte_preparer", Qte_preparer);
            jsonObject.put("accepter", accepter);
            jsonObject.put("Suivi_Par_Serie", Suivi_Par_Serie);
            jsonObject.put("Serialiser_Reception", Serialiser_Reception);
            jsonObject.put("SerieNumero", SerieNumero);
            jsonObject.put("_UID_4D", _UID_4D);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }
}
