package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Prescription_patient;

/**
 * Created by olivier on 12/03/2018.
 */

public class Prescription_patientOpenHelper extends DBOpenHelper {

    public Prescription_patientOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTablePrescription_patient(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PRESCRIPTION_PATIENT, null, null);
    }

    public static long insererPrescription_patientEnBDD(SQLiteDatabase db, Prescription_patient objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_PROTOCOLEPATIENT_UID_PRESCRIPTION_PATIENT, objet.getProtocolePatient_UID());
        contentValues.put(Constantes.CLE_COL_CODE_PRODUIT_PRESCRIPTION_PATIENT, objet.getCode_Produit());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_PRESCRIPTION_PATIENT, objet.getDesignation());
        contentValues.put(Constantes.CLE_COL_COND_PRESCRIPTION_PATIENT, objet.getCond());
        contentValues.put(Constantes.CLE_COL_QUANTITE_PRESCRIPTION_PATIENT, objet.getQuantite());
        contentValues.put(Constantes.CLE_COL_RF_FRS_PRESCRIPTION_PATIENT, objet.getRf_Frs());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_PRESCRIPTION_PATIENT, objet.getCategorie());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_DIRECTE_PRESCRIPTION_PATIENT, objet.isLivraison_Directe());
        contentValues.put(Constantes.CLE_COL_DATE_DEBUT_PRESCRIPTION_PATIENT, objet.getDate_Début());
        contentValues.put(Constantes.CLE_COL_DATE_FIN_PRESCRIPTION_PATIENT, objet.getDate_Fin());
        contentValues.put(Constantes.CLE_COL_L_PRESCRIPTION_PATIENT, objet.isL());
        contentValues.put(Constantes.CLE_COL_MA_PRESCRIPTION_PATIENT, objet.isMa());
        contentValues.put(Constantes.CLE_COL_ME_PRESCRIPTION_PATIENT, objet.isMe());
        contentValues.put(Constantes.CLE_COL_J_PRESCRIPTION_PATIENT, objet.isJ());
        contentValues.put(Constantes.CLE_COL_V_PRESCRIPTION_PATIENT, objet.isV());
        contentValues.put(Constantes.CLE_COL_S_PRESCRIPTION_PATIENT, objet.isS());
        contentValues.put(Constantes.CLE_COL_D_PRESCRIPTION_PATIENT, objet.isD());
        contentValues.put(Constantes.CLE_COL_PU_HT_PRESCRIPTION_PATIENT, objet.getPU_HT());
        contentValues.put(Constantes.CLE_COL_PU_TTC_PRESCRIPTION_PATIENT, objet.getPU_TTC());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_LIGNE_PRESCRIPTION_PATIENT, objet.getMontant_TTC_Ligne());
        contentValues.put(Constantes.CLE_COL_MONTANT_HT_LIGNE_PRESCRIPTION_PATIENT, objet.getMontant_HT_Ligne());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTEUR_ABREV_PRESCRIPTION_PATIENT, objet.getPrescripteur_Abrev());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTEUR_NOM_PRESCRIPTION_PATIENT, objet.getPrescripteur_Nom());
        contentValues.put(Constantes.CLE_COL_NB_SEMAINE_PRESCRIPTION_PATIENT, objet.getNb_Semaine());
        contentValues.put(Constantes.CLE_COL_VOIE_PRESCRIPTION_PATIENT, objet.getVoie());
        contentValues.put(Constantes.CLE_COL__UID_PRESCRIPTION_PATIENT, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_FREQUENCE_HEBDOMADAIRE_PRESCRIPTION_PATIENT, objet.getFrequence_Hebdomadaire());
        contentValues.put(Constantes.CLE_COL_DOCUMENT_PARTAGE_PRESCRIPTION_PATIENT, objet.getDocument_partage());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PRESCRIPTION_PATIENT, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PRESCRIPTION_PATIENT, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PRESCRIPTION_PATIENT, objet.getSYS_HEURE_MAJ());
        long rowID = db.insert(Constantes.TABLE_PRESCRIPTION_PATIENT, null, contentValues);
        objet.setPhiMR4UUID((int) rowID);
        return rowID;
    }

    public static Prescription_patient getPrescription_patientByID(SQLiteDatabase db, int id) {
        Prescription_patient objet = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PRESCRIPTION_PATIENT + "      WHERE " + Constantes.CLE_COL__UID_PRESCRIPTION_PATIENT + "=? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            objet = new Prescription_patient(cursor);
        }
        return objet;
    }

    public static List<Prescription_patient> getPrescriptionByDate(SQLiteDatabase db, Date dateDebut) throws ParseException {
        List<Prescription_patient> prescription_patientList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRESCRIPTION_PATIENT, null);

        while (cursor.moveToNext()) {
            Prescription_patient prescription_patient = new Prescription_patient(cursor);
            Date dateEnCours = format.parse(prescription_patient.getDate_Fin());
            if (dateDebut.compareTo(dateEnCours) < 0) {
                dateEnCours = format.parse(prescription_patient.getDate_Début());
                if (dateDebut.compareTo(dateEnCours) <= 0) {
                    prescription_patientList.add(prescription_patient);
                }
            }
        }

        cursor.close();
        cursor = null;
        return prescription_patientList;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PRESCRIPTION_PATIENT = "Prescription_patient";
        public static final String CLE_COL_PROTOCOLEPATIENT_UID_PRESCRIPTION_PATIENT = "protocolePatient_UID";
        public static final int NUM_COL_PROTOCOLEPATIENT_UID_PRESCRIPTION_PATIENT = 1;
        public static final String TYPE_COL_PROTOCOLEPATIENT_UID_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_CODE_PRODUIT_PRESCRIPTION_PATIENT = "Code_Produit";
        public static final int NUM_COL_CODE_PRODUIT_PRESCRIPTION_PATIENT = 2;
        public static final String TYPE_COL_CODE_PRODUIT_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_DESIGNATION_PRESCRIPTION_PATIENT = "Designation";
        public static final int NUM_COL_DESIGNATION_PRESCRIPTION_PATIENT = 3;
        public static final String TYPE_COL_DESIGNATION_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL_COND_PRESCRIPTION_PATIENT = "Cond";
        public static final int NUM_COL_COND_PRESCRIPTION_PATIENT = 4;
        public static final String TYPE_COL_COND_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_QUANTITE_PRESCRIPTION_PATIENT = "Quantite";
        public static final int NUM_COL_QUANTITE_PRESCRIPTION_PATIENT = 5;
        public static final String TYPE_COL_QUANTITE_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_RF_FRS_PRESCRIPTION_PATIENT = "Rf_Frs";
        public static final int NUM_COL_RF_FRS_PRESCRIPTION_PATIENT = 6;
        public static final String TYPE_COL_RF_FRS_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL_CATEGORIE_PRESCRIPTION_PATIENT = "Categorie";
        public static final int NUM_COL_CATEGORIE_PRESCRIPTION_PATIENT = 7;
        public static final String TYPE_COL_CATEGORIE_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL_LIVRAISON_DIRECTE_PRESCRIPTION_PATIENT = "Livraison_Directe";
        public static final int NUM_COL_LIVRAISON_DIRECTE_PRESCRIPTION_PATIENT = 8;
        public static final String TYPE_COL_LIVRAISON_DIRECTE_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_DATE_DEBUT_PRESCRIPTION_PATIENT = "Date_Début";
        public static final int NUM_COL_DATE_DEBUT_PRESCRIPTION_PATIENT = 9;
        public static final String TYPE_COL_DATE_DEBUT_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL_DATE_FIN_PRESCRIPTION_PATIENT = "Date_Fin";
        public static final int NUM_COL_DATE_FIN_PRESCRIPTION_PATIENT = 10;
        public static final String TYPE_COL_DATE_FIN_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL_L_PRESCRIPTION_PATIENT = "L";
        public static final int NUM_COL_L_PRESCRIPTION_PATIENT = 11;
        public static final String TYPE_COL_L_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_MA_PRESCRIPTION_PATIENT = "Ma";
        public static final int NUM_COL_MA_PRESCRIPTION_PATIENT = 12;
        public static final String TYPE_COL_MA_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_ME_PRESCRIPTION_PATIENT = "Me";
        public static final int NUM_COL_ME_PRESCRIPTION_PATIENT = 13;
        public static final String TYPE_COL_ME_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_J_PRESCRIPTION_PATIENT = "J";
        public static final int NUM_COL_J_PRESCRIPTION_PATIENT = 14;
        public static final String TYPE_COL_J_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_V_PRESCRIPTION_PATIENT = "V";
        public static final int NUM_COL_V_PRESCRIPTION_PATIENT = 15;
        public static final String TYPE_COL_V_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_S_PRESCRIPTION_PATIENT = "S";
        public static final int NUM_COL_S_PRESCRIPTION_PATIENT = 16;
        public static final String TYPE_COL_S_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_D_PRESCRIPTION_PATIENT = "D";
        public static final int NUM_COL_D_PRESCRIPTION_PATIENT = 17;
        public static final String TYPE_COL_D_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_PU_HT_PRESCRIPTION_PATIENT = "PU_HT";
        public static final int NUM_COL_PU_HT_PRESCRIPTION_PATIENT = 18;
        public static final String TYPE_COL_PU_HT_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_PU_TTC_PRESCRIPTION_PATIENT = "PU_TTC";
        public static final int NUM_COL_PU_TTC_PRESCRIPTION_PATIENT = 19;
        public static final String TYPE_COL_PU_TTC_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_MONTANT_TTC_LIGNE_PRESCRIPTION_PATIENT = "Montant_TTC_Ligne";
        public static final int NUM_COL_MONTANT_TTC_LIGNE_PRESCRIPTION_PATIENT = 20;
        public static final String TYPE_COL_MONTANT_TTC_LIGNE_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_MONTANT_HT_LIGNE_PRESCRIPTION_PATIENT = "Montant_HT_Ligne";
        public static final int NUM_COL_MONTANT_HT_LIGNE_PRESCRIPTION_PATIENT = 21;
        public static final String TYPE_COL_MONTANT_HT_LIGNE_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_PRESCRIPTEUR_ABREV_PRESCRIPTION_PATIENT = "Prescripteur_Abrev";
        public static final int NUM_COL_PRESCRIPTEUR_ABREV_PRESCRIPTION_PATIENT = 22;
        public static final String TYPE_COL_PRESCRIPTEUR_ABREV_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL_PRESCRIPTEUR_NOM_PRESCRIPTION_PATIENT = "Prescripteur_Nom";
        public static final int NUM_COL_PRESCRIPTEUR_NOM_PRESCRIPTION_PATIENT = 23;
        public static final String TYPE_COL_PRESCRIPTEUR_NOM_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL_NB_SEMAINE_PRESCRIPTION_PATIENT = "Nb_Semaine";
        public static final int NUM_COL_NB_SEMAINE_PRESCRIPTION_PATIENT = 24;
        public static final String TYPE_COL_NB_SEMAINE_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_VOIE_PRESCRIPTION_PATIENT = "Voie";
        public static final int NUM_COL_VOIE_PRESCRIPTION_PATIENT = 25;
        public static final String TYPE_COL_VOIE_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL__UID_PRESCRIPTION_PATIENT = "_UID";
        public static final int NUM_COL__UID_PRESCRIPTION_PATIENT = 26;
        public static final String TYPE_COL__UID_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_FREQUENCE_HEBDOMADAIRE_PRESCRIPTION_PATIENT = "Frequence_Hebdomadaire";
        public static final int NUM_COL_FREQUENCE_HEBDOMADAIRE_PRESCRIPTION_PATIENT = 27;
        public static final String TYPE_COL_FREQUENCE_HEBDOMADAIRE_PRESCRIPTION_PATIENT = "INTEGER";
        public static final String CLE_COL_DOCUMENT_PARTAGE_PRESCRIPTION_PATIENT = "document_partage";
        public static final int NUM_COL_DOCUMENT_PARTAGE_PRESCRIPTION_PATIENT = 28;
        public static final String TYPE_COL_DOCUMENT_PARTAGE_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PRESCRIPTION_PATIENT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PRESCRIPTION_PATIENT = 29;
        public static final String TYPE_COL_SYS_USER_MAJ_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_PRESCRIPTION_PATIENT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PRESCRIPTION_PATIENT = 30;
        public static final String TYPE_COL_SYS_DT_MAJ_PRESCRIPTION_PATIENT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PRESCRIPTION_PATIENT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PRESCRIPTION_PATIENT = 31;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PRESCRIPTION_PATIENT = "TEXT";

        public static final String CREATION_TABLE_PRESCRIPTION_PATIENT = " CREATE TABLE       " + Constantes.TABLE_PRESCRIPTION_PATIENT +
                "(" + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY," +
                Constantes.CLE_COL_PROTOCOLEPATIENT_UID_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_PROTOCOLEPATIENT_UID_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_CODE_PRODUIT_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_CODE_PRODUIT_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_DESIGNATION_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_DESIGNATION_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_COND_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_COND_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_QUANTITE_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_QUANTITE_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_RF_FRS_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_RF_FRS_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_CATEGORIE_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_CATEGORIE_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_LIVRAISON_DIRECTE_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_LIVRAISON_DIRECTE_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_DATE_DEBUT_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_DATE_DEBUT_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_DATE_FIN_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_DATE_FIN_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_L_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_L_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_MA_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_MA_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_ME_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_ME_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_J_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_J_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_V_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_V_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_S_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_S_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_D_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_D_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_PU_HT_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_PU_HT_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_PU_TTC_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_PU_TTC_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_MONTANT_TTC_LIGNE_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_MONTANT_TTC_LIGNE_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_MONTANT_HT_LIGNE_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_MONTANT_HT_LIGNE_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_PRESCRIPTEUR_ABREV_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_PRESCRIPTEUR_ABREV_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_PRESCRIPTEUR_NOM_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_PRESCRIPTEUR_NOM_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_NB_SEMAINE_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_NB_SEMAINE_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_VOIE_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_VOIE_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL__UID_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL__UID_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_FREQUENCE_HEBDOMADAIRE_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_FREQUENCE_HEBDOMADAIRE_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_DOCUMENT_PARTAGE_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_DOCUMENT_PARTAGE_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_SYS_USER_MAJ_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_SYS_USER_MAJ_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_SYS_DT_MAJ_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_SYS_DT_MAJ_PRESCRIPTION_PATIENT + " , " +
                Constantes.CLE_COL_SYS_HEURE_MAJ_PRESCRIPTION_PATIENT + "   " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PRESCRIPTION_PATIENT + " ); ";
    }
}
