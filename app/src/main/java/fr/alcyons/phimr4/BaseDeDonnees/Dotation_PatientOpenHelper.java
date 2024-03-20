package fr.alcyons.phimr4.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.Classes.Dotation_Patient;

/**
 * Created by jessica on 02/10/2017.
 */

public class Dotation_PatientOpenHelper extends DBOpenHelper {

    public Dotation_PatientOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableDotation_Patient(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_DOTATION_PATIENT, null, null);
    }

    public static long insererDotation_PatientEnBDD(SQLiteDatabase db, Dotation_Patient objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_PROTOCOLEPATIENT_UID_DOTATION_PATIENT, objet.getProtocolePatient_UID());
        contentValues.put(Constantes.CLE_COL_CODE_PRODUIT_DOTATION_PATIENT, objet.getCode_Produit());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_DOTATION_PATIENT, objet.getDesignation());
        contentValues.put(Constantes.CLE_COL_COND_DOTATION_PATIENT, objet.getCond());
        contentValues.put(Constantes.CLE_COL_QTE_DOTATION_PATIENT, objet.getQté());
        contentValues.put(Constantes.CLE_COL_REF_FOUR_DOTATION_PATIENT, objet.getRef_four());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_DOTATION_PATIENT, objet.getCategorie());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_DIRECTE_DOTATION_PATIENT, objet.isLivraison_Directe());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_DOTATION_PATIENT, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_DOTATION_PATIENT, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_DOTATION_PATIENT, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_PU_HT_DOTATION_PATIENT, objet.getPU_HT());
        contentValues.put(Constantes.CLE_COL_PU_TTC_DOTATION_PATIENT, objet.getPU_TTC());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_LIGNE_DOTATION_PATIENT, objet.getMontant_TTC_Ligne());
        contentValues.put(Constantes.CLE_COL_MONTANT_HT_LIGNE_DOTATION_PATIENT, objet.getMontant_HT_Ligne());
        contentValues.put(Constantes.CLE_COL_QTE_COMMANDE_DOTATION_PATIENT, objet.getQte_Commande());
        contentValues.put(Constantes.CLE_COL__UID_DOTATION_PATIENT, objet.get_UID());
        long rowID = db.insert(Constantes.TABLE_DOTATION_PATIENT, null, contentValues);
        objet.setPhiMR4UUID((int) rowID);
        return rowID;
    }


    public static Dotation_Patient getDotation_PatientByID(SQLiteDatabase db, int id) {
        Dotation_Patient dotationPatient = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DOTATION_PATIENT + " WHERE " + Constantes.CLE_COL__UID_DOTATION_PATIENT + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            dotationPatient = new Dotation_Patient(cursor);
        }

        cursor.close();
        cursor = null;
        return dotationPatient;
    }

    public static List<Dotation_Patient> getDotation_PatientByProcotolesPatients(SQLiteDatabase db, Integer protocoles_patient_uid) {
        List<Dotation_Patient> dotationPatientList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DOTATION_PATIENT + " WHERE " + Constantes.CLE_COL_PROTOCOLEPATIENT_UID_DOTATION_PATIENT + "=?", new String[]{String.valueOf(protocoles_patient_uid)});

        while (cursor.moveToNext()) {
            dotationPatientList.add(new Dotation_Patient(cursor));
        }

        cursor.close();
        cursor = null;
        return dotationPatientList;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_DOTATION_PATIENT = "Dotation_Patient";
        public static final String CLE_COL_PROTOCOLEPATIENT_UID_DOTATION_PATIENT = "protocolePatient_UID";
        public static final int NUM_COL_PROTOCOLEPATIENT_UID_DOTATION_PATIENT = 1;
        public static final String TYPE_COL_PROTOCOLEPATIENT_UID_DOTATION_PATIENT = "INTEGER";
        public static final String CLE_COL_CODE_PRODUIT_DOTATION_PATIENT = "Code_Produit";
        public static final int NUM_COL_CODE_PRODUIT_DOTATION_PATIENT = 2;
        public static final String TYPE_COL_CODE_PRODUIT_DOTATION_PATIENT = "INTEGER";
        public static final String CLE_COL_DESIGNATION_DOTATION_PATIENT = "Designation";
        public static final int NUM_COL_DESIGNATION_DOTATION_PATIENT = 3;
        public static final String TYPE_COL_DESIGNATION_DOTATION_PATIENT = "TEXT";
        public static final String CLE_COL_COND_DOTATION_PATIENT = "Cond";
        public static final int NUM_COL_COND_DOTATION_PATIENT = 4;
        public static final String TYPE_COL_COND_DOTATION_PATIENT = "INTEGER";
        public static final String CLE_COL_QTE_DOTATION_PATIENT = "Qté";
        public static final int NUM_COL_QTE_DOTATION_PATIENT = 5;
        public static final String TYPE_COL_QTE_DOTATION_PATIENT = "DECIMAL";
        public static final String CLE_COL_REF_FOUR_DOTATION_PATIENT = "Ref_four";
        public static final int NUM_COL_REF_FOUR_DOTATION_PATIENT = 6;
        public static final String TYPE_COL_REF_FOUR_DOTATION_PATIENT = "TEXT";
        public static final String CLE_COL_CATEGORIE_DOTATION_PATIENT = "Categorie";
        public static final int NUM_COL_CATEGORIE_DOTATION_PATIENT = 7;
        public static final String TYPE_COL_CATEGORIE_DOTATION_PATIENT = "TEXT";
        public static final String CLE_COL_LIVRAISON_DIRECTE_DOTATION_PATIENT = "Livraison_Directe";
        public static final int NUM_COL_LIVRAISON_DIRECTE_DOTATION_PATIENT = 8;
        public static final String TYPE_COL_LIVRAISON_DIRECTE_DOTATION_PATIENT = "INTEGER";
        public static final String CLE_COL_SYS_DT_MAJ_DOTATION_PATIENT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_DOTATION_PATIENT = 9;
        public static final String TYPE_COL_SYS_DT_MAJ_DOTATION_PATIENT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_DOTATION_PATIENT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_DOTATION_PATIENT = 10;
        public static final String TYPE_COL_SYS_HEURE_MAJ_DOTATION_PATIENT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_DOTATION_PATIENT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_DOTATION_PATIENT = 11;
        public static final String TYPE_COL_SYS_USER_MAJ_DOTATION_PATIENT = "TEXT";
        public static final String CLE_COL_PU_HT_DOTATION_PATIENT = "PU_HT";
        public static final int NUM_COL_PU_HT_DOTATION_PATIENT = 12;
        public static final String TYPE_COL_PU_HT_DOTATION_PATIENT = "INTEGER";
        public static final String CLE_COL_PU_TTC_DOTATION_PATIENT = "PU_TTC";
        public static final int NUM_COL_PU_TTC_DOTATION_PATIENT = 13;
        public static final String TYPE_COL_PU_TTC_DOTATION_PATIENT = "INTEGER";
        public static final String CLE_COL_MONTANT_TTC_LIGNE_DOTATION_PATIENT = "Montant_TTC_Ligne";
        public static final int NUM_COL_MONTANT_TTC_LIGNE_DOTATION_PATIENT = 14;
        public static final String TYPE_COL_MONTANT_TTC_LIGNE_DOTATION_PATIENT = "INTEGER";
        public static final String CLE_COL_MONTANT_HT_LIGNE_DOTATION_PATIENT = "Montant_HT_Ligne";
        public static final int NUM_COL_MONTANT_HT_LIGNE_DOTATION_PATIENT = 15;
        public static final String TYPE_COL_MONTANT_HT_LIGNE_DOTATION_PATIENT = "INTEGER";
        public static final String CLE_COL_QTE_COMMANDE_DOTATION_PATIENT = "Qte_Commande";
        public static final int NUM_COL_QTE_COMMANDE_DOTATION_PATIENT = 16;
        public static final String TYPE_COL_QTE_COMMANDE_DOTATION_PATIENT = "DECIMAL";
        public static final String CLE_COL__UID_DOTATION_PATIENT = "_UID";
        public static final int NUM_COL__UID_DOTATION_PATIENT = 17;
        public static final String TYPE_COL__UID_DOTATION_PATIENT = "INTEGER";

        public static final String CREATION_TABLE_DOTATION_PATIENT = " CREATE TABLE       " + Constantes.TABLE_DOTATION_PATIENT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_PROTOCOLEPATIENT_UID_DOTATION_PATIENT + " " + Constantes.TYPE_COL_PROTOCOLEPATIENT_UID_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_CODE_PRODUIT_DOTATION_PATIENT + " " + Constantes.TYPE_COL_CODE_PRODUIT_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_DESIGNATION_DOTATION_PATIENT + " " + Constantes.TYPE_COL_DESIGNATION_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_COND_DOTATION_PATIENT + " " + Constantes.TYPE_COL_COND_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_QTE_DOTATION_PATIENT + " " + Constantes.TYPE_COL_QTE_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_REF_FOUR_DOTATION_PATIENT + " " + Constantes.TYPE_COL_REF_FOUR_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_CATEGORIE_DOTATION_PATIENT + " " + Constantes.TYPE_COL_CATEGORIE_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_LIVRAISON_DIRECTE_DOTATION_PATIENT + " " + Constantes.TYPE_COL_LIVRAISON_DIRECTE_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_DOTATION_PATIENT + " " + Constantes.TYPE_COL_SYS_DT_MAJ_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_DOTATION_PATIENT + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_DOTATION_PATIENT + " " + Constantes.TYPE_COL_SYS_USER_MAJ_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_PU_HT_DOTATION_PATIENT + " " + Constantes.TYPE_COL_PU_HT_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_PU_TTC_DOTATION_PATIENT + " " + Constantes.TYPE_COL_PU_TTC_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_MONTANT_TTC_LIGNE_DOTATION_PATIENT + " " + Constantes.TYPE_COL_MONTANT_TTC_LIGNE_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_MONTANT_HT_LIGNE_DOTATION_PATIENT + " " + Constantes.TYPE_COL_MONTANT_HT_LIGNE_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL_QTE_COMMANDE_DOTATION_PATIENT + " " + Constantes.TYPE_COL_QTE_COMMANDE_DOTATION_PATIENT + " , "
                + Constantes.CLE_COL__UID_DOTATION_PATIENT + " " + Constantes.TYPE_COL__UID_DOTATION_PATIENT
                + " ); ";

    }
}
