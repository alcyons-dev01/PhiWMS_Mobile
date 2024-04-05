package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Protocoles_Patients;

/**
 * Created by jessica on 02/10/2017.
 */

public class Protocoles_PatientsOpenHelper extends DBOpenHelper {

    public Protocoles_PatientsOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableProtocoles_Patients(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PROTOCOLES_PATIENTS, null, null);
    }

    public static long insererProtocoles_PatientsEnBDD(SQLiteDatabase db, Protocoles_Patients objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_PROTOCOLES_PATIENTS, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_PROTOCOLE_STD_PROTOCOLES_PATIENTS, objet.getProtocole_Std());
        contentValues.put(Constantes.CLE_COL_IPP_PROTOCOLES_PATIENTS, objet.getIPP());
        contentValues.put(Constantes.CLE_COL_RAPPEL_TRAITEMENT_PROTOCOLES_PATIENTS, objet.getRappel_traitement());
        contentValues.put(Constantes.CLE_COL_FREQUENCE_PROTOCOLES_PATIENTS, objet.getFrequence());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_PROTOCOLES_PATIENTS, objet.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_DEBUT_DATE_PROTOCOLES_PATIENTS, objet.getDebut_Date());
        contentValues.put(Constantes.CLE_COL_FIN_DATE_PROTOCOLES_PATIENTS, objet.getFin_Date());
        contentValues.put(Constantes.CLE_COL_INTERRUPTION_PROTOCOLES_PATIENTS, objet.isInterruption());
        contentValues.put(Constantes.CLE_COL_PATIENT_IDENTITE_PROTOCOLES_PATIENTS, objet.getPatient_Identite());
        contentValues.put(Constantes.CLE_COL_VALORISATION_PROTOCOLES_PATIENTS, objet.getValorisation());
        contentValues.put(Constantes.CLE_COL_SOINS_A_DOMICILE_PROTOCOLES_PATIENTS, objet.isSoins_A_Domicile());
        contentValues.put(Constantes.CLE_COL_DOTATION_STD_PROTOCOLES_PATIENTS, objet.getDotation_Std());
        contentValues.put(Constantes.CLE_COL_DEPOT_CODE_PROTOCOLES_PATIENTS, objet.getDepot_Code());
        contentValues.put(Constantes.CLE_COL_QTE_PAR_SESSION_PROTOCOLES_PATIENTS, objet.isQte_par_session());
        contentValues.put(Constantes.CLE_COL_TOURNEE_PROTOCOLE_PROTOCOLES_PATIENTS, objet.getTournee_Protocole());
        contentValues.put(Constantes.CLE_COL_TOURNEE_DOTATION_PROTOCOLES_PATIENTS, objet.getTournee_Dotation());
        contentValues.put(Constantes.CLE_COL_ARCHIVER_PROTOCOLES_PATIENTS, objet.isArchiver());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PROTOCOLES_PATIENTS, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PROTOCOLES_PATIENTS, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PROTOCOLES_PATIENTS, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_ORDRE_PRESCRIPTION_IGNORER_PROTOCOLES_PATIENTS, objet.isOrdre_Prescription_ignorer());
        contentValues.put(Constantes.CLE_COL_SERIE_LUNDI_1_PROTOCOLES_PATIENTS, objet.getSerie_Lundi_1());
        contentValues.put(Constantes.CLE_COL_SERIE_MARDI_2_PROTOCOLES_PATIENTS, objet.getSerie_Mardi_2());
        contentValues.put(Constantes.CLE_COL_SERIE_MERCREDI_3_PROTOCOLES_PATIENTS, objet.getSerie_Mercredi_3());
        contentValues.put(Constantes.CLE_COL_SERIE_JEUDI_4_PROTOCOLES_PATIENTS, objet.getSerie_Jeudi_4());
        contentValues.put(Constantes.CLE_COL_SERIE_VENDREDI_5_PROTOCOLES_PATIENTS, objet.getSerie_Vendredi_5());
        contentValues.put(Constantes.CLE_COL_SERIE_SAMEDI_6_PROTOCOLES_PATIENTS, objet.getSerie_Samedi_6());
        contentValues.put(Constantes.CLE_COL_SERIE_DIMANCHE_7_PROTOCOLES_PATIENTS, objet.getSerie_Dimanche_7());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_A_DOMICILE_PROTOCOLES_PATIENTS, objet.isLivraison_A_domicile());
        contentValues.put(Constantes.CLE_COL_PATIENT_ID_PROTOCOLES_PATIENTS, objet.getPatient_ID());
        contentValues.put(Constantes.CLE_COL_CENTRE_HOSPITALIER_RATTACHEMENT_PROTOCOLES_PATIENTS, objet.getCentre_Hospitalier_Rattachement());
        contentValues.put(Constantes.CLE_COL_NEPHROLOGUE_REFERENT_PROTOCOLES_PATIENTS, objet.getNephrologue_Referent());
        contentValues.put(Constantes.CLE_COL_GENERALISTE_REFERENT_PROTOCOLES_PATIENTS, objet.getGeneraliste_Referent());
        contentValues.put(Constantes.CLE_COL_IDE_PROTOCOLES_PATIENTS, objet.getIDE());
        contentValues.put(Constantes.CLE_COL_TECHNIQUE_PROTOCOLES_PATIENTS, objet.getTechnique());
        contentValues.put(Constantes.CLE_COL_SOUS_TECHNIQUE_PROTOCOLES_PATIENTS, objet.getSous_Technique());
        contentValues.put(Constantes.CLE_COL_TECHNIQUE_DEBUT_DATE_PROTOCOLES_PATIENTS, objet.getTechnique_Debut_Date());
        contentValues.put(Constantes.CLE_COL_INTERRUPTION_FIN_DATE_PROTOCOLES_PATIENTS, objet.getInterruption_Fin_Date());
        contentValues.put(Constantes.CLE_COL_INTERRUPTION_MOTIF_PROTOCOLES_PATIENTS, objet.getInterruption_Motif());
        contentValues.put(Constantes.CLE_COL_ARRET_DEFINITIF_DATE_PROTOCOLES_PATIENTS, objet.getArret_Definitif_Date());
        contentValues.put(Constantes.CLE_COL_ARRET_MOTIF_PROTOCOLES_PATIENTS, objet.getArret_Motif());
        contentValues.put(Constantes.CLE_COL_TRAITEMENT_MODALITE_PROTOCOLES_PATIENTS, objet.getTraitement_Modalite());
        contentValues.put(Constantes.CLE_COL_INTERRUPTION_DEBUT_DATE_PROTOCOLES_PATIENTS, objet.getInterruption_Debut_Date());
        contentValues.put(Constantes.CLE_COL_VALIDATION_PHARMACEUTIQUE_PROTOCOLES_PATIENTS, objet.isValidation_Pharmaceutique());
        contentValues.put(Constantes.CLE_COL_VALIDATION_PHARMACEUTIQUE_DATE_PROTOCOLES_PATIENTS, objet.getValidation_Pharmaceutique_Date());
        contentValues.put(Constantes.CLE_COL_VALIDATION_PHARMACEUTIQUE_PAR_PROTOCOLES_PATIENTS, objet.getValidation_Pharmaceutique_Par());
        contentValues.put(Constantes.CLE_COL_DISCIPLINE_MEDICALE_PROTOCOLES_PATIENTS, objet.getDiscipline_Medicale());

        long rowID = db.insert(Constantes.TABLE_PROTOCOLES_PATIENTS, null, contentValues);
        objet.setPhiMR4UUID((int) rowID);
        return rowID;
    }

    public static List<Protocoles_Patients> getProtocoles_PatientsByDepot(SQLiteDatabase db, Integer depotCode) {
        List<Protocoles_Patients> protocolesPatientsList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PROTOCOLES_PATIENTS + " WHERE " + Constantes.CLE_COL_DEPOT_CODE_PROTOCOLES_PATIENTS + "=?", new String[]{String.valueOf(depotCode)});

        while (cursor.moveToNext()) {
            Protocoles_Patients protocoles_patients = new Protocoles_Patients(cursor);
            if (protocoles_patients.isArchiver() == false && protocoles_patients.isInterruption() == false) {
                protocolesPatientsList.add(protocoles_patients);
            }
        }

        cursor.close();
        cursor = null;
        return protocolesPatientsList;
    }

    public static Protocoles_Patients getProtocoles_PatientsByIPP(SQLiteDatabase db, String ipp) {
        Protocoles_Patients protocolesPatients = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PROTOCOLES_PATIENTS + " WHERE " + Constantes.CLE_COL_IPP_PROTOCOLES_PATIENTS + "=?", new String[]{ipp});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            protocolesPatients = new Protocoles_Patients(cursor);
        }
        cursor.close();
        cursor = null;
        return protocolesPatients;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PROTOCOLES_PATIENTS = "Protocoles_Patients";
        public static final String CLE_COL__UID_PROTOCOLES_PATIENTS = "_UID";
        public static final int NUM_COL__UID_PROTOCOLES_PATIENTS = 1;
        public static final String TYPE_COL__UID_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_PROTOCOLE_STD_PROTOCOLES_PATIENTS = "Protocole_Std";
        public static final int NUM_COL_PROTOCOLE_STD_PROTOCOLES_PATIENTS = 2;
        public static final String TYPE_COL_PROTOCOLE_STD_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_IPP_PROTOCOLES_PATIENTS = "IPP";
        public static final int NUM_COL_IPP_PROTOCOLES_PATIENTS = 3;
        public static final String TYPE_COL_IPP_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_RAPPEL_TRAITEMENT_PROTOCOLES_PATIENTS = "Rappel_traitement";
        public static final int NUM_COL_RAPPEL_TRAITEMENT_PROTOCOLES_PATIENTS = 4;
        public static final String TYPE_COL_RAPPEL_TRAITEMENT_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_FREQUENCE_PROTOCOLES_PATIENTS = "Frequence";
        public static final int NUM_COL_FREQUENCE_PROTOCOLES_PATIENTS = 5;
        public static final String TYPE_COL_FREQUENCE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_DEPOT_REFERENCE_PROTOCOLES_PATIENTS = "Depot_Reference";
        public static final int NUM_COL_DEPOT_REFERENCE_PROTOCOLES_PATIENTS = 6;
        public static final String TYPE_COL_DEPOT_REFERENCE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_DEBUT_DATE_PROTOCOLES_PATIENTS = "Debut_Date";
        public static final int NUM_COL_DEBUT_DATE_PROTOCOLES_PATIENTS = 7;
        public static final String TYPE_COL_DEBUT_DATE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_FIN_DATE_PROTOCOLES_PATIENTS = "Fin_Date";
        public static final int NUM_COL_FIN_DATE_PROTOCOLES_PATIENTS = 8;
        public static final String TYPE_COL_FIN_DATE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_INTERRUPTION_PROTOCOLES_PATIENTS = "Interruption";
        public static final int NUM_COL_INTERRUPTION_PROTOCOLES_PATIENTS = 9;
        public static final String TYPE_COL_INTERRUPTION_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_PATIENT_IDENTITE_PROTOCOLES_PATIENTS = "Patient_Identite";
        public static final int NUM_COL_PATIENT_IDENTITE_PROTOCOLES_PATIENTS = 10;
        public static final String TYPE_COL_PATIENT_IDENTITE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_VALORISATION_PROTOCOLES_PATIENTS = "Valorisation";
        public static final int NUM_COL_VALORISATION_PROTOCOLES_PATIENTS = 11;
        public static final String TYPE_COL_VALORISATION_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_SOINS_A_DOMICILE_PROTOCOLES_PATIENTS = "Soins_A_Domicile";
        public static final int NUM_COL_SOINS_A_DOMICILE_PROTOCOLES_PATIENTS = 12;
        public static final String TYPE_COL_SOINS_A_DOMICILE_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_DOTATION_STD_PROTOCOLES_PATIENTS = "Dotation_Std";
        public static final int NUM_COL_DOTATION_STD_PROTOCOLES_PATIENTS = 13;
        public static final String TYPE_COL_DOTATION_STD_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_DEPOT_CODE_PROTOCOLES_PATIENTS = "Depot_Code";
        public static final int NUM_COL_DEPOT_CODE_PROTOCOLES_PATIENTS = 14;
        public static final String TYPE_COL_DEPOT_CODE_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_QTE_PAR_SESSION_PROTOCOLES_PATIENTS = "Qte_par_session";
        public static final int NUM_COL_QTE_PAR_SESSION_PROTOCOLES_PATIENTS = 15;
        public static final String TYPE_COL_QTE_PAR_SESSION_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_TOURNEE_PROTOCOLE_PROTOCOLES_PATIENTS = "Tournee_Protocole";
        public static final int NUM_COL_TOURNEE_PROTOCOLE_PROTOCOLES_PATIENTS = 16;
        public static final String TYPE_COL_TOURNEE_PROTOCOLE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_TOURNEE_DOTATION_PROTOCOLES_PATIENTS = "Tournee_Dotation";
        public static final int NUM_COL_TOURNEE_DOTATION_PROTOCOLES_PATIENTS = 17;
        public static final String TYPE_COL_TOURNEE_DOTATION_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_ARCHIVER_PROTOCOLES_PATIENTS = "Archiver";
        public static final int NUM_COL_ARCHIVER_PROTOCOLES_PATIENTS = 18;
        public static final String TYPE_COL_ARCHIVER_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_SYS_DT_MAJ_PROTOCOLES_PATIENTS = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PROTOCOLES_PATIENTS = 19;
        public static final String TYPE_COL_SYS_DT_MAJ_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PROTOCOLES_PATIENTS = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PROTOCOLES_PATIENTS = 20;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PROTOCOLES_PATIENTS = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PROTOCOLES_PATIENTS = 21;
        public static final String TYPE_COL_SYS_USER_MAJ_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_ORDRE_PRESCRIPTION_IGNORER_PROTOCOLES_PATIENTS = "Ordre_Prescription_ignorer";
        public static final int NUM_COL_ORDRE_PRESCRIPTION_IGNORER_PROTOCOLES_PATIENTS = 22;
        public static final String TYPE_COL_ORDRE_PRESCRIPTION_IGNORER_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_SERIE_LUNDI_1_PROTOCOLES_PATIENTS = "Serie_Lundi_1";
        public static final int NUM_COL_SERIE_LUNDI_1_PROTOCOLES_PATIENTS = 23;
        public static final String TYPE_COL_SERIE_LUNDI_1_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_SERIE_MARDI_2_PROTOCOLES_PATIENTS = "Serie_Mardi_2";
        public static final int NUM_COL_SERIE_MARDI_2_PROTOCOLES_PATIENTS = 24;
        public static final String TYPE_COL_SERIE_MARDI_2_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_SERIE_MERCREDI_3_PROTOCOLES_PATIENTS = "Serie_Mercredi_3";
        public static final int NUM_COL_SERIE_MERCREDI_3_PROTOCOLES_PATIENTS = 25;
        public static final String TYPE_COL_SERIE_MERCREDI_3_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_SERIE_JEUDI_4_PROTOCOLES_PATIENTS = "Serie_Jeudi_4";
        public static final int NUM_COL_SERIE_JEUDI_4_PROTOCOLES_PATIENTS = 26;
        public static final String TYPE_COL_SERIE_JEUDI_4_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_SERIE_VENDREDI_5_PROTOCOLES_PATIENTS = "Serie_Vendredi_5";
        public static final int NUM_COL_SERIE_VENDREDI_5_PROTOCOLES_PATIENTS = 27;
        public static final String TYPE_COL_SERIE_VENDREDI_5_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_SERIE_SAMEDI_6_PROTOCOLES_PATIENTS = "Serie_Samedi_6";
        public static final int NUM_COL_SERIE_SAMEDI_6_PROTOCOLES_PATIENTS = 28;
        public static final String TYPE_COL_SERIE_SAMEDI_6_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_SERIE_DIMANCHE_7_PROTOCOLES_PATIENTS = "Serie_Dimanche_7";
        public static final int NUM_COL_SERIE_DIMANCHE_7_PROTOCOLES_PATIENTS = 29;
        public static final String TYPE_COL_SERIE_DIMANCHE_7_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_LIVRAISON_A_DOMICILE_PROTOCOLES_PATIENTS = "Livraison_A_domicile";
        public static final int NUM_COL_LIVRAISON_A_DOMICILE_PROTOCOLES_PATIENTS = 30;
        public static final String TYPE_COL_LIVRAISON_A_DOMICILE_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_PATIENT_ID_PROTOCOLES_PATIENTS = "Patient_ID";
        public static final int NUM_COL_PATIENT_ID_PROTOCOLES_PATIENTS = 31;
        public static final String TYPE_COL_PATIENT_ID_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_CENTRE_HOSPITALIER_RATTACHEMENT_PROTOCOLES_PATIENTS = "Centre_Hospitalier_Rattachement";
        public static final int NUM_COL_CENTRE_HOSPITALIER_RATTACHEMENT_PROTOCOLES_PATIENTS = 32;
        public static final String TYPE_COL_CENTRE_HOSPITALIER_RATTACHEMENT_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_NEPHROLOGUE_REFERENT_PROTOCOLES_PATIENTS = "Nephrologue_Referent";
        public static final int NUM_COL_NEPHROLOGUE_REFERENT_PROTOCOLES_PATIENTS = 33;
        public static final String TYPE_COL_NEPHROLOGUE_REFERENT_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_GENERALISTE_REFERENT_PROTOCOLES_PATIENTS = "Generaliste_Referent";
        public static final int NUM_COL_GENERALISTE_REFERENT_PROTOCOLES_PATIENTS = 34;
        public static final String TYPE_COL_GENERALISTE_REFERENT_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_IDE_PROTOCOLES_PATIENTS = "IDE";
        public static final int NUM_COL_IDE_PROTOCOLES_PATIENTS = 35;
        public static final String TYPE_COL_IDE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_TECHNIQUE_PROTOCOLES_PATIENTS = "Technique";
        public static final int NUM_COL_TECHNIQUE_PROTOCOLES_PATIENTS = 36;
        public static final String TYPE_COL_TECHNIQUE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_SOUS_TECHNIQUE_PROTOCOLES_PATIENTS = "Sous_Technique";
        public static final int NUM_COL_SOUS_TECHNIQUE_PROTOCOLES_PATIENTS = 37;
        public static final String TYPE_COL_SOUS_TECHNIQUE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_TECHNIQUE_DEBUT_DATE_PROTOCOLES_PATIENTS = "Technique_Debut_Date";
        public static final int NUM_COL_TECHNIQUE_DEBUT_DATE_PROTOCOLES_PATIENTS = 38;
        public static final String TYPE_COL_TECHNIQUE_DEBUT_DATE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_INTERRUPTION_FIN_DATE_PROTOCOLES_PATIENTS = "Interruption_Fin_Date";
        public static final int NUM_COL_INTERRUPTION_FIN_DATE_PROTOCOLES_PATIENTS = 39;
        public static final String TYPE_COL_INTERRUPTION_FIN_DATE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_INTERRUPTION_MOTIF_PROTOCOLES_PATIENTS = "Interruption_Motif";
        public static final int NUM_COL_INTERRUPTION_MOTIF_PROTOCOLES_PATIENTS = 40;
        public static final String TYPE_COL_INTERRUPTION_MOTIF_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_ARRET_DEFINITIF_DATE_PROTOCOLES_PATIENTS = "Arret_Definitif_Date";
        public static final int NUM_COL_ARRET_DEFINITIF_DATE_PROTOCOLES_PATIENTS = 41;
        public static final String TYPE_COL_ARRET_DEFINITIF_DATE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_ARRET_MOTIF_PROTOCOLES_PATIENTS = "Arret_Motif";
        public static final int NUM_COL_ARRET_MOTIF_PROTOCOLES_PATIENTS = 42;
        public static final String TYPE_COL_ARRET_MOTIF_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_TRAITEMENT_MODALITE_PROTOCOLES_PATIENTS = "Traitement_Modalite";
        public static final int NUM_COL_TRAITEMENT_MODALITE_PROTOCOLES_PATIENTS = 43;
        public static final String TYPE_COL_TRAITEMENT_MODALITE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_INTERRUPTION_DEBUT_DATE_PROTOCOLES_PATIENTS = "Interruption_Debut_Date";
        public static final int NUM_COL_INTERRUPTION_DEBUT_DATE_PROTOCOLES_PATIENTS = 44;
        public static final String TYPE_COL_INTERRUPTION_DEBUT_DATE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_VALIDATION_PHARMACEUTIQUE_PROTOCOLES_PATIENTS = "Validation_Pharmaceutique";
        public static final int NUM_COL_VALIDATION_PHARMACEUTIQUE_PROTOCOLES_PATIENTS = 45;
        public static final String TYPE_COL_VALIDATION_PHARMACEUTIQUE_PROTOCOLES_PATIENTS = "INTEGER";
        public static final String CLE_COL_VALIDATION_PHARMACEUTIQUE_DATE_PROTOCOLES_PATIENTS = "Validation_Pharmaceutique_Date";
        public static final int NUM_COL_VALIDATION_PHARMACEUTIQUE_DATE_PROTOCOLES_PATIENTS = 46;
        public static final String TYPE_COL_VALIDATION_PHARMACEUTIQUE_DATE_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_VALIDATION_PHARMACEUTIQUE_PAR_PROTOCOLES_PATIENTS = "Validation_Pharmaceutique_Par";
        public static final int NUM_COL_VALIDATION_PHARMACEUTIQUE_PAR_PROTOCOLES_PATIENTS = 47;
        public static final String TYPE_COL_VALIDATION_PHARMACEUTIQUE_PAR_PROTOCOLES_PATIENTS = "TEXT";
        public static final String CLE_COL_DISCIPLINE_MEDICALE_PROTOCOLES_PATIENTS = "discipline_Medicale";
        public static final int NUM_COL_DISCIPLINE_MEDICALE_PROTOCOLES_PATIENTS = 48;
        public static final String TYPE_COL_DISCIPLINE_MEDICALE_PROTOCOLES_PATIENTS = "TEXT";

        public static final String CREATION_TABLE_PROTOCOLES_PATIENTS = " CREATE TABLE       " + Constantes.TABLE_PROTOCOLES_PATIENTS
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL__UID_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL__UID_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_PROTOCOLE_STD_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_PROTOCOLE_STD_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_IPP_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_IPP_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_RAPPEL_TRAITEMENT_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_RAPPEL_TRAITEMENT_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_FREQUENCE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_FREQUENCE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_DEPOT_REFERENCE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_DEPOT_REFERENCE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_DEBUT_DATE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_DEBUT_DATE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_FIN_DATE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_FIN_DATE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_INTERRUPTION_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_INTERRUPTION_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_PATIENT_IDENTITE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_PATIENT_IDENTITE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_VALORISATION_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_VALORISATION_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SOINS_A_DOMICILE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SOINS_A_DOMICILE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_DOTATION_STD_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_DOTATION_STD_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_DEPOT_CODE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_DEPOT_CODE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_QTE_PAR_SESSION_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_QTE_PAR_SESSION_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_TOURNEE_PROTOCOLE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_TOURNEE_PROTOCOLE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_TOURNEE_DOTATION_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_TOURNEE_DOTATION_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_ARCHIVER_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_ARCHIVER_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SYS_DT_MAJ_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SYS_DT_MAJ_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SYS_USER_MAJ_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SYS_USER_MAJ_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_ORDRE_PRESCRIPTION_IGNORER_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_ORDRE_PRESCRIPTION_IGNORER_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SERIE_LUNDI_1_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SERIE_LUNDI_1_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SERIE_MARDI_2_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SERIE_MARDI_2_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SERIE_MERCREDI_3_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SERIE_MERCREDI_3_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SERIE_JEUDI_4_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SERIE_JEUDI_4_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SERIE_VENDREDI_5_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SERIE_VENDREDI_5_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SERIE_SAMEDI_6_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SERIE_SAMEDI_6_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SERIE_DIMANCHE_7_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SERIE_DIMANCHE_7_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_LIVRAISON_A_DOMICILE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_LIVRAISON_A_DOMICILE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_PATIENT_ID_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_PATIENT_ID_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_CENTRE_HOSPITALIER_RATTACHEMENT_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_CENTRE_HOSPITALIER_RATTACHEMENT_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_NEPHROLOGUE_REFERENT_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_NEPHROLOGUE_REFERENT_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_GENERALISTE_REFERENT_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_GENERALISTE_REFERENT_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_IDE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_IDE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_TECHNIQUE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_TECHNIQUE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_SOUS_TECHNIQUE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_SOUS_TECHNIQUE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_TECHNIQUE_DEBUT_DATE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_TECHNIQUE_DEBUT_DATE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_INTERRUPTION_FIN_DATE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_INTERRUPTION_FIN_DATE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_INTERRUPTION_MOTIF_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_INTERRUPTION_MOTIF_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_ARRET_DEFINITIF_DATE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_ARRET_DEFINITIF_DATE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_ARRET_MOTIF_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_ARRET_MOTIF_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_TRAITEMENT_MODALITE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_TRAITEMENT_MODALITE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_INTERRUPTION_DEBUT_DATE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_INTERRUPTION_DEBUT_DATE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_VALIDATION_PHARMACEUTIQUE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_VALIDATION_PHARMACEUTIQUE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_VALIDATION_PHARMACEUTIQUE_DATE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_VALIDATION_PHARMACEUTIQUE_DATE_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_VALIDATION_PHARMACEUTIQUE_PAR_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_VALIDATION_PHARMACEUTIQUE_PAR_PROTOCOLES_PATIENTS + " ,  "
                + Constantes.CLE_COL_DISCIPLINE_MEDICALE_PROTOCOLES_PATIENTS + " " + Constantes.TYPE_COL_DISCIPLINE_MEDICALE_PROTOCOLES_PATIENTS
                + " ); ";

    }
}
