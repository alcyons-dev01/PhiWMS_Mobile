package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

public class PH_Preparation implements Serializable, Comparable {

    private int UID;
    private String Service;
    private Boolean Erreur_Valid;
    private String PHIE_Tag;
    private String Saisie_Le;
    private String A_tel_heure;
    private int produitID;
    private String produitDesignation;
    private double Qte_demandee;
    private Boolean Livree;
    private Boolean Validee;
    private String Origine;
    private String Liste;
    private int depotDestinataireID;
    private String depotDestinataireReference;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String PrescripteurReference;
    private String Prescription_date;
    private String PrescripteurNom;
    private String depotOrigineReference;
    private int depotOrigineID;
    private String Commentaires;
    private String PreparationDate;
    private String LivraisonPrevueDate;
    private String DN_Groupe;
    private double Montant_HT;
    private double Montant_TTC;
    private double Poids;
    private int Commande_ID;
    private String Preparateur;
    private String Statut;
    private String PHIE_SYNCHRO;
    private String receptionUFNonComforme;
    private String livraisonDate;
    private String Frequence;
    private String previsionDateDebut;
    private String previsionDateFin;
    private Boolean URGENT;
    private String Motif;
    private int preparateur_userID;
    private int pharmacien_userID;
    private double Volume;
    private int PaletteNB;
    private int ColisNB;
    private int Conteneur_NB;
    private String numero_scelle;
    private int livreur_userID;
    private String Signature_Livraison;
    private String TempsPreparation;
    private String delivranceValider_A;
    private String delivranceValider_Le;
    private int delivranceValider_Par;
    private int phiwms_mobileUUID = -1;

    public PH_Preparation(int UID, String PHIE_Tag, String saisie_Le, String a_tel_heure, int qte_demandee, boolean livree, boolean validee, String origine, String liste, int depotDestinataireID, String depotDestinataireReference, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, String prescription_date, String depotOrigineReference, int depotOrigineID, String commentaires, String preparationDate, String livraisonPrevueDate, int montant_HT, int montant_TTC, int poids, int commande_ID, String preparateur, String statut, String livraisonDate, String previsionDateDebut, String previsionDateFin, boolean URGENT, int preparateur_userID, int pharmacien_userID, int volume, int paletteNB, int ColisNB, int Conteneur_NB, String numero_scelle)
    {
        this.UID = UID;
        this.PHIE_Tag = PHIE_Tag;
        this.Saisie_Le = saisie_Le;
        this.A_tel_heure = a_tel_heure;
        this.Qte_demandee = qte_demandee;
        this.Livree = livree;
        this.Validee = validee;
        this.Origine = origine;
        this.Liste = liste;
        this.depotDestinataireID = depotDestinataireID;
        this.depotDestinataireReference = depotDestinataireReference;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.PreparationDate = prescription_date;
        this.depotOrigineReference = depotOrigineReference;
        this.depotOrigineID = depotOrigineID;
        this.Commentaires = commentaires;
        this.PreparationDate = preparationDate;
        this.LivraisonPrevueDate = livraisonPrevueDate;
        this.Montant_HT = montant_HT;
        this.Montant_TTC = montant_TTC;
        this.Poids = poids;
        this.Commande_ID = commande_ID;
        this.Preparateur = preparateur;
        this.Statut = statut;
        this.livraisonDate = livraisonDate;
        this.previsionDateDebut = previsionDateDebut;
        this.previsionDateFin = previsionDateFin;
        this.URGENT = URGENT;
        this.preparateur_userID = preparateur_userID;
        this.pharmacien_userID = pharmacien_userID;
        this.Volume = volume;
        this.PaletteNB = paletteNB;
        this.ColisNB = ColisNB;
        this.Conteneur_NB = Conteneur_NB;
        this.numero_scelle = numero_scelle;
    }

    public PH_Preparation(int UID, String service, Boolean erreur_Valid, String PHIE_Tag, String saisie_Le, String a_tel_heure, int produitID, String produitDesignation, double qte_demandee, Boolean livree, Boolean validee, String origine, String liste, int depotDestinataireID, String depotDestinataireReference, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, String prescripteurReference, String prescription_date, String prescripteurNom, String depotOrigineReference, int depotOrigineID, String commentaires, String preparationDate, String livraisonPrevueDate, String DN_Groupe, double montant_HT, double montant_TTC, double poids, int commande_ID, String preparateur, String statut, String PHIE_SYNCHRO, String receptionUFNonComforme, String livraisonDate, String frequence, String previsionDateDebut, String previsionDateFin, Boolean URGENT, String motif, int preparateur_userID, int pharmacien_userID, double volume, int paletteNB, int ColisNB, int Conteneur_NB, String numero_scelle) {
        this.UID = UID;
        this.Service = service;
        this.Erreur_Valid = erreur_Valid;
        this.PHIE_Tag = PHIE_Tag;
        this.Saisie_Le = saisie_Le;
        this.A_tel_heure = a_tel_heure;
        this.produitID = produitID;
        this.produitDesignation = produitDesignation;
        this.Qte_demandee = qte_demandee;
        this.Livree = livree;
        this.Validee = validee;
        this.Origine = origine;
        this.Liste = liste;
        this.depotDestinataireID = depotDestinataireID;
        this.depotDestinataireReference = depotDestinataireReference;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.PrescripteurReference = prescripteurReference;
        this.Prescription_date = prescription_date;
        this.PrescripteurNom = prescripteurNom;
        this.depotOrigineReference = depotOrigineReference;
        this.depotOrigineID = depotOrigineID;
        this.Commentaires = commentaires;
        this.PreparationDate = preparationDate;
        this.LivraisonPrevueDate = livraisonPrevueDate;
        this.DN_Groupe = DN_Groupe;
        this.Montant_HT = montant_HT;
        this.Montant_TTC = montant_TTC;
        this.Poids = poids;
        this.Commande_ID = commande_ID;
        this.Preparateur = preparateur;
        this.Statut = statut;
        this.PHIE_SYNCHRO = PHIE_SYNCHRO;
        this.receptionUFNonComforme = receptionUFNonComforme;
        this.livraisonDate = livraisonDate;
        this.Frequence = frequence;
        this.previsionDateDebut = previsionDateDebut;
        this.previsionDateFin = previsionDateFin;
        this.URGENT = URGENT;
        this.Motif = motif;
        this.preparateur_userID = preparateur_userID;
        this.pharmacien_userID = pharmacien_userID;
        this.Volume = volume;
        this.PaletteNB = paletteNB;
        this.ColisNB = ColisNB;
        this.Conteneur_NB = Conteneur_NB;
        this.numero_scelle = numero_scelle;
    }

/*    public PH_Preparation(JSONObject jsonObject) {
        try {
            this.UID = jsonObject.getInt("UID");
            this.Service = OutilsGestionClasses.recupererString(jsonObject.getString("Service"));
            this.Erreur_Valid = OutilsGestionClasses.recupererBooleen(jsonObject, "Erreur_Valid");
            this.PHIE_Tag = OutilsGestionClasses.recupererString(jsonObject.getString("PHIE_Tag"));
            this.Saisie_Le = OutilsGestionClasses.recupererString(jsonObject.getString("Saisie_Le"));
            this.A_tel_heure = OutilsGestionClasses.recupererString(jsonObject.getString("A_tel_heure"));
            this.produitID = jsonObject.getInt("produitID");
            this.produitDesignation = OutilsGestionClasses.recupererString(jsonObject.getString("produitDesignation"));
            this.Qte_demandee = jsonObject.getDouble("Qte_demandee");
            this.Livree = OutilsGestionClasses.recupererBooleen(jsonObject, "Livree");
            this.Validee = OutilsGestionClasses.recupererBooleen(jsonObject, "Validee");
            this.Origine = OutilsGestionClasses.recupererString(jsonObject.getString("Origine"));
            this.Liste = OutilsGestionClasses.recupererString(jsonObject.getString("Liste"));
            this.depotDestinataireID = jsonObject.getInt("depotDestinataireID");
            this.depotDestinataireReference = OutilsGestionClasses.recupererString(jsonObject.getString("depotDestinataireReference"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.PrescripteurReference = OutilsGestionClasses.recupererString(jsonObject.getString("PrescripteurReference"));
            this.Prescription_date = OutilsGestionClasses.recupererString(jsonObject.getString("Prescription_date"));
            this.PrescripteurNom = OutilsGestionClasses.recupererString(jsonObject.getString("PrescripteurNom"));
            this.depotOrigineReference = OutilsGestionClasses.recupererString(jsonObject.getString("depotOrigineReference"));
            this.depotOrigineID = jsonObject.getInt("depotOrigineID");
            this.Commentaires = OutilsGestionClasses.recupererString(jsonObject.getString("Commentaires"));
            this.PreparationDate = OutilsGestionClasses.recupererString(jsonObject.getString("PreparationDate"));
            this.LivraisonPrevueDate = OutilsGestionClasses.recupererString(jsonObject.getString("LivraisonPrevueDate"));
            this.DN_Groupe = OutilsGestionClasses.recupererString(jsonObject.getString("DN_Groupe"));
            this.Montant_HT = jsonObject.getDouble("Montant_HT");
            this.Montant_TTC = jsonObject.getDouble("Montant_TTC");
            this.Poids = jsonObject.getDouble("Poids");
            this.Commande_ID = jsonObject.getInt("Commande_ID");
            this.Preparateur = OutilsGestionClasses.recupererString(jsonObject.getString("Preparateur"));
            this.Statut = OutilsGestionClasses.recupererString(jsonObject.getString("Statut"));
            this.PHIE_SYNCHRO = OutilsGestionClasses.recupererString(jsonObject.getString("PHIE_SYNCHRO"));
            this.receptionUFNonComforme = OutilsGestionClasses.recupererString(jsonObject.getString("receptionUFNonComforme"));
            this.livraisonDate = OutilsGestionClasses.recupererString(jsonObject.getString("livraisonDate"));
            this.Frequence = OutilsGestionClasses.recupererString(jsonObject.getString("Frequence"));
            this.previsionDateDebut = OutilsGestionClasses.recupererString(jsonObject.getString("previsionDateDebut"));
            this.previsionDateFin = OutilsGestionClasses.recupererString(jsonObject.getString("previsionDateFin"));
            this.URGENT = OutilsGestionClasses.recupererBooleen(jsonObject, "URGENT");
            this.Motif = OutilsGestionClasses.recupererString(jsonObject.getString("Motif"));
            this.preparateur_userID = jsonObject.getInt("preparateur_userID");
            this.pharmacien_userID = jsonObject.getInt("pharmacien_userID");
            this.Volume = jsonObject.getDouble("Volume");
            this.PaletteNB = jsonObject.getInt("PaletteNB");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    public PH_Preparation(JSONObject jsonObject) {
        try {
            this.UID = jsonObject.getInt("UID");
            this.Validee = OutilsGestionClasses.recupererBooleen(jsonObject, "Validee");
            this.Liste = OutilsGestionClasses.recupererString(jsonObject.getString("Liste"));
            this.depotDestinataireID = jsonObject.getInt("depotDestinataireID");
            this.depotDestinataireReference = OutilsGestionClasses.recupererString(jsonObject.getString("depotDestinataireReference"));
            this.depotOrigineReference = OutilsGestionClasses.recupererString(jsonObject.getString("depotOrigineReference"));
            this.depotOrigineID = jsonObject.getInt("depotOrigineID");
            this.Commentaires = OutilsGestionClasses.recupererString(jsonObject.getString("Commentaires"));
            this.LivraisonPrevueDate = OutilsGestionClasses.recupererString(jsonObject.getString("LivraisonPrevueDate"));
            this.Statut = OutilsGestionClasses.recupererString(jsonObject.getString("Statut"));
            this.livraisonDate = OutilsGestionClasses.recupererString(jsonObject.getString("livraisonDate"));
            this.URGENT = OutilsGestionClasses.recupererBooleen(jsonObject, "URGENT");
            this.Motif = OutilsGestionClasses.recupererString(jsonObject.getString("Motif"));
            this.Volume = jsonObject.getDouble("Volume");
            this.livreur_userID = jsonObject.getInt("livreur_userID");
            this.Livree = OutilsGestionClasses.recupererBooleen(jsonObject, "Livree");
            this.Preparateur = OutilsGestionClasses.recupererString(jsonObject.getString("Preparateur"));
            this.ColisNB = jsonObject.getInt("ColisNB");
            this.PaletteNB = jsonObject.getInt("PaletteNB");
            this.Conteneur_NB = jsonObject.getInt("Conteneur_NB");
            this.TempsPreparation = OutilsGestionClasses.recupererString(jsonObject.getString("TempsPreparation"));
            this.Montant_HT = jsonObject.getDouble("Montant_HT");
            this.Montant_TTC = jsonObject.getDouble("Montant_TTC");
            this.Poids = jsonObject.getDouble("Poids");
            this.Commande_ID = jsonObject.getInt("Commande_ID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/*    public PH_Preparation(Cursor cursor) {
        this.UID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_UID_PH_PREPARATION);
        this.Service = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_SERVICE_PH_PREPARATION);
        this.Erreur_Valid = OutilsGestionClasses.recupererBooleen(cursor, PH_PreparationOpenHelper.Constantes.NUM_COL_ERREUR_VALID_PH_PREPARATION);
        this.PHIE_Tag = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PHIE_TAG_PH_PREPARATION);
        this.Saisie_Le = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_SAISIE_LE_PH_PREPARATION);
        this.A_tel_heure = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_A_TEL_HEURE_PH_PREPARATION);
        this.produitID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_PRODUITID_PH_PREPARATION);
        this.produitDesignation = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PRODUITDESIGNATION_PH_PREPARATION);
        this.Qte_demandee = cursor.getDouble(PH_PreparationOpenHelper.Constantes.NUM_COL_QTE_DEMANDEE_PH_PREPARATION);
        this.Livree = OutilsGestionClasses.recupererBooleen(cursor, PH_PreparationOpenHelper.Constantes.NUM_COL_LIVREE_PH_PREPARATION);
        this.Validee = OutilsGestionClasses.recupererBooleen(cursor, PH_PreparationOpenHelper.Constantes.NUM_COL_VALIDEE_PH_PREPARATION);
        this.Origine = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_ORIGINE_PH_PREPARATION);
        this.Liste = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_LISTE_PH_PREPARATION);
        this.depotDestinataireID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_DEPOTDESTINATAIREID_PH_PREPARATION);
        this.depotDestinataireReference = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_DEPOTDESTINATAIREREFERENCE_PH_PREPARATION);
        this.SYS_DT_MAJ = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PH_PREPARATION);
        this.SYS_HEURE_MAJ = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PH_PREPARATION);
        this.SYS_USER_MAJ = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PH_PREPARATION);
        this.PrescripteurReference = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION);
        this.Prescription_date = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PRESCRIPTION_DATE_PH_PREPARATION);
        this.PrescripteurNom = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PRESCRIPTEURNOM_PH_PREPARATION);
        this.depotOrigineReference = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_DEPOTORIGINEREFERENCE_PH_PREPARATION);
        this.depotOrigineID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_DEPOTORIGINEID_PH_PREPARATION);
        this.Commentaires = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_COMMENTAIRES_PH_PREPARATION);
        this.PreparationDate = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PREPARATIONDATE_PH_PREPARATION);
        this.LivraisonPrevueDate = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_LIVRAISONPREVUEDATE_PH_PREPARATION);
        this.DN_Groupe = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_DN_GROUPE_PH_PREPARATION);
        this.Montant_HT = cursor.getDouble(PH_PreparationOpenHelper.Constantes.NUM_COL_MONTANT_HT_PH_PREPARATION);
        this.Montant_TTC = cursor.getDouble(PH_PreparationOpenHelper.Constantes.NUM_COL_MONTANT_TTC_PH_PREPARATION);
        this.Poids = cursor.getDouble(PH_PreparationOpenHelper.Constantes.NUM_COL_POIDS_PH_PREPARATION);
        this.Commande_ID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_COMMANDE_ID_PH_PREPARATION);
        this.Preparateur = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PREPARATEUR_PH_PREPARATION);
        this.Statut = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_STATUT_PH_PREPARATION);
        this.PHIE_SYNCHRO = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PHIE_SYNCHRO_PH_PREPARATION);
        this.receptionUFNonComforme = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_RECEPTIONUFNONCOMFORME_PH_PREPARATION);
        this.livraisonDate = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_LIVRAISONDATE_PH_PREPARATION);
        this.Frequence = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_FREQUENCE_PH_PREPARATION);
        this.previsionDateDebut = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PREVISIONDATEDEBUT_PH_PREPARATION);
        this.previsionDateFin = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PREVISIONDATEFIN_PH_PREPARATION);
        this.URGENT = OutilsGestionClasses.recupererBooleen(cursor, PH_PreparationOpenHelper.Constantes.NUM_COL_URGENT_PH_PREPARATION);
        this.Motif = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_MOTIF_PH_PREPARATION);
        this.preparateur_userID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_PREPARATEUR_USERID_PH_PREPARATION);
        this.pharmacien_userID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_PHARMACIEN_USERID_PH_PREPARATION);
        this.Volume = cursor.getDouble(PH_PreparationOpenHelper.Constantes.NUM_COL_VOLUME_PH_PREPARATION);
        this.PaletteNB = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_PALETTENB_PH_PREPARATION);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }*/


    public PH_Preparation(Cursor cursor) {
        this.UID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_UID_PH_PREPARATION);
        this.Validee = OutilsGestionClasses.recupererBooleen(cursor, PH_PreparationOpenHelper.Constantes.NUM_COL_VALIDEE_PH_PREPARATION);
        this.Liste = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_LISTE_PH_PREPARATION);
        this.depotDestinataireID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_DEPOTDESTINATAIREID_PH_PREPARATION);
        this.depotDestinataireReference = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_DEPOTDESTINATAIREREFERENCE_PH_PREPARATION);
        this.depotOrigineReference = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_DEPOTORIGINEREFERENCE_PH_PREPARATION);
        this.depotOrigineID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_DEPOTORIGINEID_PH_PREPARATION);
        this.Commentaires = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_COMMENTAIRES_PH_PREPARATION);
        this.LivraisonPrevueDate = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_LIVRAISONPREVUEDATE_PH_PREPARATION);
        this.Montant_HT = cursor.getDouble(PH_PreparationOpenHelper.Constantes.NUM_COL_MONTANT_HT_PH_PREPARATION);
        this.Montant_TTC = cursor.getDouble(PH_PreparationOpenHelper.Constantes.NUM_COL_MONTANT_TTC_PH_PREPARATION);
        this.Poids = cursor.getDouble(PH_PreparationOpenHelper.Constantes.NUM_COL_POIDS_PH_PREPARATION);
        this.Commande_ID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_COMMANDE_ID_PH_PREPARATION);
        this.Statut = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_STATUT_PH_PREPARATION);
        this.livraisonDate = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_LIVRAISONDATE_PH_PREPARATION);
        this.URGENT = OutilsGestionClasses.recupererBooleen(cursor, PH_PreparationOpenHelper.Constantes.NUM_COL_URGENT_PH_PREPARATION);
        this.Motif = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_MOTIF_PH_PREPARATION);
        this.Volume = cursor.getDouble(PH_PreparationOpenHelper.Constantes.NUM_COL_VOLUME_PH_PREPARATION);
        this.livreur_userID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_LIVREUR_USERID);
        this.Signature_Livraison = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_SIGNATURE_LIVRAISON);
        this.Livree = OutilsGestionClasses.recupererBooleen(cursor, PH_PreparationOpenHelper.Constantes.NUM_COL_LIVREE_PH_PREPARATION);
        this.ColisNB = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_CAISSENB_PH_PREPARATION);
        this.Conteneur_NB = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_CONTENEUR_NB);
        this.numero_scelle = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_NUMERO_SCELLE);
        this.PaletteNB = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_PALETTENB_PH_PREPARATION);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
        this.Preparateur = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_PREPARATEUR_PH_PREPARATION);
        this.pharmacien_userID = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_PHARMACIEN_USERID_PH_PREPARATION);
        this.delivranceValider_A = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_DELIVRANCE_VALIDER_A);
        this.delivranceValider_Le = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_DELIVRANCE_VALIDER_LE);
        this.delivranceValider_Par = cursor.getInt(PH_PreparationOpenHelper.Constantes.NUM_COL_DELIVRANCE_VALIDER_PAR);
        this.SYS_USER_MAJ = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PH_PREPARATION);
        this.TempsPreparation = cursor.getString(PH_PreparationOpenHelper.Constantes.NUM_COL_TEMPS_PREPARATION);
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getService() {
        return Service;
    }

    public void setService(String service) {
        Service = service;
    }

    public Boolean getErreur_Valid() {
        return Erreur_Valid;
    }

    public void setErreur_Valid(Boolean erreur_Valid) {
        Erreur_Valid = erreur_Valid;
    }

    public String getPHIE_Tag() {
        return PHIE_Tag;
    }

    public void setPHIE_Tag(String PHIE_Tag) {
        this.PHIE_Tag = PHIE_Tag;
    }

    public String getSaisie_Le() {
        return Saisie_Le;
    }

    public void setSaisie_Le(String saisie_Le) {
        Saisie_Le = saisie_Le;
    }

    public String getA_tel_heure() {
        return A_tel_heure;
    }

    public void setA_tel_heure(String a_tel_heure) {
        A_tel_heure = a_tel_heure;
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

    public double getQte_demandee() {
        return Qte_demandee;
    }

    public void setQte_demandee(double qte_demandee) {
        Qte_demandee = qte_demandee;
    }

    public Boolean getLivree() {
        return Livree;
    }

    public void setLivree(Boolean livree) {
        Livree = livree;
    }

    public Boolean getValidee() {
        return Validee;
    }

    public void setValidee(Boolean validee) {
        Validee = validee;
    }

    public String getOrigine() {
        return Origine;
    }

    public void setOrigine(String origine) {
        Origine = origine;
    }

    public String getListe() {
        return Liste;
    }

    public void setListe(String liste) {
        Liste = liste;
    }

    public int getDepotDestinataireID() {
        return depotDestinataireID;
    }

    public void setDepotDestinataireID(int depotDestinataireID) {
        this.depotDestinataireID = depotDestinataireID;
    }

    public String getDepotDestinataireReference() {
        return depotDestinataireReference;
    }

    public void setDepotDestinataireReference(String depotDestinataireReference) {
        this.depotDestinataireReference = depotDestinataireReference;
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

    public String getPrescripteurReference() {
        return PrescripteurReference;
    }

    public void setPrescripteurReference(String prescripteurReference) {
        PrescripteurReference = prescripteurReference;
    }

    public String getPrescription_date() {
        return Prescription_date;
    }

    public void setPrescription_date(String prescription_date) {
        Prescription_date = prescription_date;
    }

    public String getPrescripteurNom() {
        return PrescripteurNom;
    }

    public void setPrescripteurNom(String prescripteurNom) {
        PrescripteurNom = prescripteurNom;
    }

    public String getDepotOrigineReference() {
        return depotOrigineReference;
    }

    public void setDepotOrigineReference(String depotOrigineReference) {
        this.depotOrigineReference = depotOrigineReference;
    }

    public int getDepotOrigineID() {
        return depotOrigineID;
    }

    public void setDepotOrigineID(int depotOrigineID) {
        this.depotOrigineID = depotOrigineID;
    }

    public String getCommentaires() {
        return Commentaires;
    }

    public void setCommentaires(String commentaires) {
        Commentaires = commentaires;
    }

    public String getPreparationDate() {
        return PreparationDate;
    }

    public void setPreparationDate(String preparationDate) {
        PreparationDate = preparationDate;
    }

    public String getLivraisonPrevueDate() {
        return LivraisonPrevueDate;
    }

    public void setLivraisonPrevueDate(String livraisonPrevueDate) {
        LivraisonPrevueDate = livraisonPrevueDate;
    }

    public String getDN_Groupe() {
        return DN_Groupe;
    }

    public void setDN_Groupe(String DN_Groupe) {
        this.DN_Groupe = DN_Groupe;
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

    public double getPoids() {
        return Poids;
    }

    public void setPoids(double poids) {
        Poids = poids;
    }

    public int getCommande_ID() {
        return Commande_ID;
    }

    public void setCommande_ID(int commande_ID) {
        Commande_ID = commande_ID;
    }

    public String getPreparateur() {
        return Preparateur;
    }

    public void setPreparateur(String preparateur) {
        Preparateur = preparateur;
    }

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }

    public String getPHIE_SYNCHRO() {
        return PHIE_SYNCHRO;
    }

    public void setPHIE_SYNCHRO(String PHIE_SYNCHRO) {
        this.PHIE_SYNCHRO = PHIE_SYNCHRO;
    }

    public String getReceptionUFNonComforme() {
        return receptionUFNonComforme;
    }

    public void setReceptionUFNonComforme(String receptionUFNonComforme) {
        this.receptionUFNonComforme = receptionUFNonComforme;
    }

    public String getLivraisonDate() {
        return livraisonDate;
    }

    public void setLivraisonDate(String livraisonDate) {
        this.livraisonDate = livraisonDate;
    }

    public String getFrequence() {
        return Frequence;
    }

    public void setFrequence(String frequence) {
        Frequence = frequence;
    }

    public String getPrevisionDateDebut() {
        return previsionDateDebut;
    }

    public void setPrevisionDateDebut(String previsionDateDebut) {
        this.previsionDateDebut = previsionDateDebut;
    }

    public String getPrevisionDateFin() {
        return previsionDateFin;
    }

    public void setPrevisionDateFin(String previsionDateFin) {
        this.previsionDateFin = previsionDateFin;
    }

    public Boolean getURGENT() {
        return URGENT;
    }

    public void setURGENT(Boolean URGENT) {
        this.URGENT = URGENT;
    }

    public String getMotif() {
        return Motif;
    }

    public void setMotif(String motif) {
        Motif = motif;
    }

    public int getPreparateur_userID() {
        return preparateur_userID;
    }

    public void setPreparateur_userID(int preparateur_userID) {
        this.preparateur_userID = preparateur_userID;
    }

    public int getPharmacien_userID() {
        return pharmacien_userID;
    }

    public void setPharmacien_userID(int pharmacien_userID) {
        this.pharmacien_userID = pharmacien_userID;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public int getPaletteNB() {
        return PaletteNB;
    }

    public void setPaletteNB(int paletteNB) {
        PaletteNB = paletteNB;
    }

    public int getColisNB() {
        return ColisNB;
    }

    public void setColisNB(int Colis_NB) {
        ColisNB = Colis_NB;
    }

    public int getConteneur_NB() {
        return Conteneur_NB;
    }

    public void setConteneur_NB(int conteneur_NB) {
        Conteneur_NB = conteneur_NB;
    }

    public String getNumero_scelle() {
        return numero_scelle;
    }

    public void setNumero_scelle(String numero_scelle) {
        this.numero_scelle = numero_scelle;
    }

    public int getLivreur_userID() {
        return livreur_userID;
    }

    public void setLivreur_userID(int livreur_userID) {
        this.livreur_userID = livreur_userID;
    }

    public String getSignature_Livraison() {
        return Signature_Livraison;
    }

    public void setSignature_Livraison(String signature_Livraison) {
        Signature_Livraison = signature_Livraison;
    }

    public String getTempsPreparation() {
        return TempsPreparation;
    }

    public void setTempsPreparation(String tempsPreparation) {
        TempsPreparation = tempsPreparation;
    }

    public String getChaineFiltrePhPreparationLigne(SQLiteDatabase db) {
        String filter = this.toString() + " ";

        List<PH_Preparation_Ligne> phPreparationLigneList = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, this);
        for (int i = 0; i < phPreparationLigneList.size(); i++) {
            PH_Preparation_Ligne phPreparationLigne = phPreparationLigneList.get(i);
            filter += phPreparationLigne.getProduitDesignation();
        }

        return filter;
    }

    public String getDelivranceValider_A() {
        return delivranceValider_A;
    }

    public void setDelivranceValider_A(String delivranceValider_A) {
        this.delivranceValider_A = delivranceValider_A;
    }

    public String getDelivranceValider_Le() {
        return delivranceValider_Le;
    }

    public void setDelivranceValider_Le(String delivranceValider_Le) {
        this.delivranceValider_Le = delivranceValider_Le;
    }

    public int getDelivranceValider_Par() {
        return delivranceValider_Par;
    }

    public void setDelivranceValider_Par(int delivranceValider_Par) {
        this.delivranceValider_Par = delivranceValider_Par;
    }


    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_Preparation)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        PH_Preparation ph_preparation = (PH_Preparation) obj;

        if (this.getPhiMR4UUID() == ph_preparation.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getUID() > ph_preparation.getUID() ? 1 : -1;
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UID", UID);
            jsonObject.put("Service", Service);
            jsonObject.put("Erreur_Valid", Erreur_Valid);
            jsonObject.put("PHIE_Tag", PHIE_Tag);
            jsonObject.put("Saisie_Le", Saisie_Le);
            jsonObject.put("A_tel_heure", A_tel_heure);
            jsonObject.put("produitID", produitID);
            jsonObject.put("produitDesignation", produitDesignation);
            jsonObject.put("Qte_demandee", Qte_demandee);
            jsonObject.put("Livree", Livree);
            jsonObject.put("Validee", Validee);
            jsonObject.put("Origine", Origine);
            jsonObject.put("Liste", Liste);
            jsonObject.put("depotDestinataireID", depotDestinataireID);
            jsonObject.put("depotDestinataireReference", depotDestinataireReference);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("PrescripteurReference", PrescripteurReference);
            jsonObject.put("Prescription_date", Prescription_date);
            jsonObject.put("PrescripteurNom", PrescripteurNom);
            jsonObject.put("depotOrigineReference", depotOrigineReference);
            jsonObject.put("depotOrigineID", depotOrigineID);
            jsonObject.put("Commentaires", Commentaires);
            jsonObject.put("PreparationDate", PreparationDate);
            jsonObject.put("LivraisonPrevueDate", LivraisonPrevueDate);
            jsonObject.put("DN_Groupe", DN_Groupe);
            jsonObject.put("Montant_HT", Montant_HT);
            jsonObject.put("Montant_TTC", Montant_TTC);
            jsonObject.put("Poids", Poids);
            jsonObject.put("Commande_ID", Commande_ID);
            jsonObject.put("Preparateur", Preparateur);
            jsonObject.put("Statut", Statut);
            jsonObject.put("PHIE_SYNCHRO", PHIE_SYNCHRO);
            jsonObject.put("receptionUFNonComforme", receptionUFNonComforme);
            jsonObject.put("livraisonDate", livraisonDate);
            jsonObject.put("Frequence", Frequence);
            jsonObject.put("previsionDateDebut", previsionDateDebut);
            jsonObject.put("previsionDateFin", previsionDateFin);
            jsonObject.put("URGENT", URGENT);
            jsonObject.put("Motif", Motif);
            jsonObject.put("preparateur_userID", preparateur_userID);
            jsonObject.put("pharmacien_userID", pharmacien_userID);
            jsonObject.put("Volume", Volume);
            jsonObject.put("PaletteNB", PaletteNB);
            jsonObject.put("ColisNB", ColisNB);
            jsonObject.put("Conteneur_NB", Conteneur_NB);
            jsonObject.put("numero_scelle", numero_scelle);
            jsonObject.put("livreur_userID", livreur_userID);
            jsonObject.put("Signature_Livraison", Signature_Livraison);
            jsonObject.put("TempsPreparation", TempsPreparation);
            jsonObject.put("delivranceValider_A", delivranceValider_A);
            jsonObject.put("delivranceValider_Le", delivranceValider_Le);
            jsonObject.put("delivranceValider_Par", delivranceValider_Par);

        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return jsonObject;
    }
}
