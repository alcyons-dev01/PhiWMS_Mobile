package com.example.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import com.example.phiwms_mobile.Classes.Composants_patient;

/**
 * Created by jessica on 02/10/2017.
 */

public class Composants_patientOpenHelper extends DBOpenHelper {
    public Composants_patientOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableComposants_patient(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_COMPOSANTS_PATIENT, null, null);
    }

    public static long insererComposants_patientEnBDD(SQLiteDatabase db, Composants_patient objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_PROTOCOLEPATIENT_UID_COMPOSANTS_PATIENT, objet.getProtocolePatient_UID());
        contentValues.put(Constantes.CLE_COL_CODE_PRODUIT_COMPOSANTS_PATIENT, objet.getCode_produit());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_COMPOSANTS_PATIENT, objet.getDésignation());
        contentValues.put(Constantes.CLE_COL_COND_COMPOSANTS_PATIENT, objet.getCond());
        contentValues.put(Constantes.CLE_COL_QTE_COMPOSANTS_PATIENT, objet.getQté());
        contentValues.put(Constantes.CLE_COL_REF_FOUR_COMPOSANTS_PATIENT, objet.getRef_Four());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_COMPOSANTS_PATIENT, objet.getCatégorie());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_DIRECTE_COMPOSANTS_PATIENT, objet.isLivraison_Directe());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_COMPOSANTS_PATIENT, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_COMPOSANTS_PATIENT, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_COMPOSANTS_PATIENT, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_PU_HT_COMPOSANTS_PATIENT, objet.getPU_HT());
        contentValues.put(Constantes.CLE_COL_PU_TTC_COMPOSANTS_PATIENT, objet.getPU_TTC());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_LIGNE_COMPOSANTS_PATIENT, objet.getMontant_TTC_Ligne());
        contentValues.put(Constantes.CLE_COL_MONTANT_HT_LIGNE_COMPOSANTS_PATIENT, objet.getMontant_HT_Ligne());
        contentValues.put(Constantes.CLE_COL__UID_COMPOSANTS_PATIENT, objet.get_UID());
        long rowID = db.insert(Constantes.TABLE_COMPOSANTS_PATIENT, null, contentValues);
        objet.setPhiMR4UUID((int) rowID);
        return rowID;
    }

    public static Composants_patient getComposants_patientByID(SQLiteDatabase db, int id) {
        Composants_patient composantsPatient = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_COMPOSANTS_PATIENT + " WHERE " + Constantes.CLE_COL__UID_COMPOSANTS_PATIENT + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            composantsPatient = new Composants_patient(cursor);
        }

        cursor.close();
        cursor = null;
        return composantsPatient;
    }

    public static List<Composants_patient> getComposants_patientByProcotolesPatients(SQLiteDatabase db, Integer protocoles_patient_uid) {
        List<Composants_patient> composantsPatientList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_COMPOSANTS_PATIENT + " WHERE " + Constantes.CLE_COL_PROTOCOLEPATIENT_UID_COMPOSANTS_PATIENT + "=?", new String[]{String.valueOf(protocoles_patient_uid)});

        while (cursor.moveToNext()) {
            composantsPatientList.add(new Composants_patient(cursor));
        }

        cursor.close();
        cursor = null;
        return composantsPatientList;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_COMPOSANTS_PATIENT = "Composants_patient";
        public static final String CLE_COL_PROTOCOLEPATIENT_UID_COMPOSANTS_PATIENT = "protocolePatient_UID";
        public static final int NUM_COL_PROTOCOLEPATIENT_UID_COMPOSANTS_PATIENT = 1;
        public static final String TYPE_COL_PROTOCOLEPATIENT_UID_COMPOSANTS_PATIENT = "INTEGER";
        public static final String CLE_COL_CODE_PRODUIT_COMPOSANTS_PATIENT = "Code_produit";
        public static final int NUM_COL_CODE_PRODUIT_COMPOSANTS_PATIENT = 2;
        public static final String TYPE_COL_CODE_PRODUIT_COMPOSANTS_PATIENT = "INTEGER";
        public static final String CLE_COL_DESIGNATION_COMPOSANTS_PATIENT = "Désignation";
        public static final int NUM_COL_DESIGNATION_COMPOSANTS_PATIENT = 3;
        public static final String TYPE_COL_DESIGNATION_COMPOSANTS_PATIENT = "TEXT";
        public static final String CLE_COL_COND_COMPOSANTS_PATIENT = "Cond";
        public static final int NUM_COL_COND_COMPOSANTS_PATIENT = 4;
        public static final String TYPE_COL_COND_COMPOSANTS_PATIENT = "INTEGER";
        public static final String CLE_COL_QTE_COMPOSANTS_PATIENT = "Qté";
        public static final int NUM_COL_QTE_COMPOSANTS_PATIENT = 5;
        public static final String TYPE_COL_QTE_COMPOSANTS_PATIENT = "DECIMAL";
        public static final String CLE_COL_REF_FOUR_COMPOSANTS_PATIENT = "Ref_Four";
        public static final int NUM_COL_REF_FOUR_COMPOSANTS_PATIENT = 6;
        public static final String TYPE_COL_REF_FOUR_COMPOSANTS_PATIENT = "TEXT";
        public static final String CLE_COL_CATEGORIE_COMPOSANTS_PATIENT = "Catégorie";
        public static final int NUM_COL_CATEGORIE_COMPOSANTS_PATIENT = 7;
        public static final String TYPE_COL_CATEGORIE_COMPOSANTS_PATIENT = "TEXT";
        public static final String CLE_COL_LIVRAISON_DIRECTE_COMPOSANTS_PATIENT = "Livraison_Directe";
        public static final int NUM_COL_LIVRAISON_DIRECTE_COMPOSANTS_PATIENT = 8;
        public static final String TYPE_COL_LIVRAISON_DIRECTE_COMPOSANTS_PATIENT = "INTEGER";
        public static final String CLE_COL_SYS_DT_MAJ_COMPOSANTS_PATIENT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_COMPOSANTS_PATIENT = 9;
        public static final String TYPE_COL_SYS_DT_MAJ_COMPOSANTS_PATIENT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_COMPOSANTS_PATIENT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_COMPOSANTS_PATIENT = 10;
        public static final String TYPE_COL_SYS_HEURE_MAJ_COMPOSANTS_PATIENT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_COMPOSANTS_PATIENT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_COMPOSANTS_PATIENT = 11;
        public static final String TYPE_COL_SYS_USER_MAJ_COMPOSANTS_PATIENT = "TEXT";
        public static final String CLE_COL_PU_HT_COMPOSANTS_PATIENT = "PU_HT";
        public static final int NUM_COL_PU_HT_COMPOSANTS_PATIENT = 12;
        public static final String TYPE_COL_PU_HT_COMPOSANTS_PATIENT = "INTEGER";
        public static final String CLE_COL_PU_TTC_COMPOSANTS_PATIENT = "PU_TTC";
        public static final int NUM_COL_PU_TTC_COMPOSANTS_PATIENT = 13;
        public static final String TYPE_COL_PU_TTC_COMPOSANTS_PATIENT = "INTEGER";
        public static final String CLE_COL_MONTANT_TTC_LIGNE_COMPOSANTS_PATIENT = "Montant_TTC_Ligne";
        public static final int NUM_COL_MONTANT_TTC_LIGNE_COMPOSANTS_PATIENT = 14;
        public static final String TYPE_COL_MONTANT_TTC_LIGNE_COMPOSANTS_PATIENT = "INTEGER";
        public static final String CLE_COL_MONTANT_HT_LIGNE_COMPOSANTS_PATIENT = "Montant_HT_Ligne";
        public static final int NUM_COL_MONTANT_HT_LIGNE_COMPOSANTS_PATIENT = 15;
        public static final String TYPE_COL_MONTANT_HT_LIGNE_COMPOSANTS_PATIENT = "INTEGER";
        public static final String CLE_COL__UID_COMPOSANTS_PATIENT = "_UID";
        public static final int NUM_COL__UID_COMPOSANTS_PATIENT = 16;
        public static final String TYPE_COL__UID_COMPOSANTS_PATIENT = "INTEGER";

        public static final String CREATION_TABLE_COMPOSANTS_PATIENT = " CREATE TABLE       " + Constantes.TABLE_COMPOSANTS_PATIENT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_PROTOCOLEPATIENT_UID_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_PROTOCOLEPATIENT_UID_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_CODE_PRODUIT_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_CODE_PRODUIT_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_DESIGNATION_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_DESIGNATION_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_COND_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_COND_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_QTE_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_QTE_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_REF_FOUR_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_REF_FOUR_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_CATEGORIE_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_CATEGORIE_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_LIVRAISON_DIRECTE_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_LIVRAISON_DIRECTE_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_SYS_DT_MAJ_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_SYS_USER_MAJ_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_PU_HT_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_PU_HT_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_PU_TTC_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_PU_TTC_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_MONTANT_TTC_LIGNE_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_MONTANT_TTC_LIGNE_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL_MONTANT_HT_LIGNE_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL_MONTANT_HT_LIGNE_COMPOSANTS_PATIENT + " , "
                + Constantes.CLE_COL__UID_COMPOSANTS_PATIENT + " " + Constantes.TYPE_COL__UID_COMPOSANTS_PATIENT
                + " ); ";

    }
}
