package com.example.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import com.example.phiwms_mobile.Classes.PH_Reassort;
import com.example.phiwms_mobile.Classes.PH_Reassort_Ligne;

/**
 * Created by jessica on 02/10/2017.
 */

public class PH_Reassort_LigneOpenHelper extends DBOpenHelper {

    public PH_Reassort_LigneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static long insererPH_Reassort_LigneEnBDD(SQLiteDatabase db, PH_Reassort_Ligne objet) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_REASSORT_UID_PH_REASSORT_LIGNE, objet.getReassort_UID());
        contentValues.put(Constantes.CLE_COL_PRODUIT_ID_PH_REASSORT_LIGNE, objet.getProduit_ID());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_INT_PH_REASSORT_LIGNE, objet.getDesignation_int());
        contentValues.put(Constantes.CLE_COL_CONDITIONNEMENT_PH_REASSORT_LIGNE, objet.getConditionnement());
        contentValues.put(Constantes.CLE_COL_QUANTITE_PH_REASSORT_LIGNE, objet.getQuantite());
        contentValues.put(Constantes.CLE_COL_PRODUIT_REFERENCE_PH_REASSORT_LIGNE, objet.getProduit_Reference());
        contentValues.put(Constantes.CLE_COL_ZONE_STOCKAGE_PH_REASSORT_LIGNE, objet.getZone_stockage());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_PH_REASSORT_LIGNE, objet.getCategorie());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PH_REASSORT_LIGNE, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PH_REASSORT_LIGNE, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PH_REASSORT_LIGNE, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_STOCK_MINIMUM_PH_REASSORT_LIGNE, objet.getStock_Minimum());
        contentValues.put(Constantes.CLE_COL__UID_PH_REASSORT_LIGNE, objet.get_UID());

        long rowID = db.insert(Constantes.TABLE_PH_REASSORT_LIGNE, null, contentValues);
        objet.setPhiMR4UUID((int) rowID);
        return rowID;
    }

    public static List<PH_Reassort_Ligne> getAllPH_Reassort_LigneParPH_Reassort(SQLiteDatabase db, PH_Reassort phReassort) {
        List<PH_Reassort_Ligne> phReassortLigneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_REASSORT_LIGNE + " WHERE " + Constantes.CLE_COL_REASSORT_UID_PH_REASSORT_LIGNE + "=?", new String[]{String.valueOf(phReassort.getCode())});

        while (cursor.moveToNext()) {
            phReassortLigneList.add(new PH_Reassort_Ligne(cursor));
        }
        cursor.close();
        cursor = null;

        return phReassortLigneList;
    }

    public void supprimerUnPH_Reassort_Ligne(SQLiteDatabase db, PH_Reassort_Ligne ph_reassort_ligne) {
        db.delete(Constantes.TABLE_PH_REASSORT_LIGNE, DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(ph_reassort_ligne.getPhiMR4UUID())});
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PH_REASSORT_LIGNE = "PH_Reassort_Ligne";
        public static final String CLE_COL_REASSORT_UID_PH_REASSORT_LIGNE = "reassort_UID";
        public static final int NUM_COL_REASSORT_UID_PH_REASSORT_LIGNE = 1;
        public static final String TYPE_COL_REASSORT_UID_PH_REASSORT_LIGNE = "INTEGER";
        public static final String CLE_COL_PRODUIT_ID_PH_REASSORT_LIGNE = "produit_ID";
        public static final int NUM_COL_PRODUIT_ID_PH_REASSORT_LIGNE = 2;
        public static final String TYPE_COL_PRODUIT_ID_PH_REASSORT_LIGNE = "INTEGER";
        public static final String CLE_COL_DESIGNATION_INT_PH_REASSORT_LIGNE = "Designation_int";
        public static final int NUM_COL_DESIGNATION_INT_PH_REASSORT_LIGNE = 3;
        public static final String TYPE_COL_DESIGNATION_INT_PH_REASSORT_LIGNE = "TEXT";
        public static final String CLE_COL_CONDITIONNEMENT_PH_REASSORT_LIGNE = "Conditionnement";
        public static final int NUM_COL_CONDITIONNEMENT_PH_REASSORT_LIGNE = 4;
        public static final String TYPE_COL_CONDITIONNEMENT_PH_REASSORT_LIGNE = "INTEGER";
        public static final String CLE_COL_QUANTITE_PH_REASSORT_LIGNE = "Quantite";
        public static final int NUM_COL_QUANTITE_PH_REASSORT_LIGNE = 5;
        public static final String TYPE_COL_QUANTITE_PH_REASSORT_LIGNE = "INTEGER";
        public static final String CLE_COL_PRODUIT_REFERENCE_PH_REASSORT_LIGNE = "produit_Reference";
        public static final int NUM_COL_PRODUIT_REFERENCE_PH_REASSORT_LIGNE = 6;
        public static final String TYPE_COL_PRODUIT_REFERENCE_PH_REASSORT_LIGNE = "TEXT";
        public static final String CLE_COL_ZONE_STOCKAGE_PH_REASSORT_LIGNE = "Zone_stockage";
        public static final int NUM_COL_ZONE_STOCKAGE_PH_REASSORT_LIGNE = 7;
        public static final String TYPE_COL_ZONE_STOCKAGE_PH_REASSORT_LIGNE = "TEXT";
        public static final String CLE_COL_CATEGORIE_PH_REASSORT_LIGNE = "Categorie";
        public static final int NUM_COL_CATEGORIE_PH_REASSORT_LIGNE = 8;
        public static final String TYPE_COL_CATEGORIE_PH_REASSORT_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_PH_REASSORT_LIGNE = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PH_REASSORT_LIGNE = 9;
        public static final String TYPE_COL_SYS_DT_MAJ_PH_REASSORT_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PH_REASSORT_LIGNE = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PH_REASSORT_LIGNE = 10;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PH_REASSORT_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PH_REASSORT_LIGNE = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PH_REASSORT_LIGNE = 11;
        public static final String TYPE_COL_SYS_USER_MAJ_PH_REASSORT_LIGNE = "TEXT";
        public static final String CLE_COL_STOCK_MINIMUM_PH_REASSORT_LIGNE = "stock_Minimum";
        public static final int NUM_COL_STOCK_MINIMUM_PH_REASSORT_LIGNE = 12;
        public static final String TYPE_COL_STOCK_MINIMUM_PH_REASSORT_LIGNE = "INTEGER";
        public static final String CLE_COL__UID_PH_REASSORT_LIGNE = "_UID";
        public static final int NUM_COL__UID_PH_REASSORT_LIGNE = 13;
        public static final String TYPE_COL__UID_PH_REASSORT_LIGNE = "INTEGER";

        public static final String CREATION_TABLE_PH_REASSORT_LIGNE = " CREATE TABLE       " + Constantes.TABLE_PH_REASSORT_LIGNE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_REASSORT_UID_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_REASSORT_UID_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_PRODUIT_ID_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_PRODUIT_ID_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_DESIGNATION_INT_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_DESIGNATION_INT_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_CONDITIONNEMENT_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_CONDITIONNEMENT_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_QUANTITE_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_QUANTITE_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_PRODUIT_REFERENCE_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_PRODUIT_REFERENCE_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_ZONE_STOCKAGE_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_ZONE_STOCKAGE_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_CATEGORIE_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_CATEGORIE_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_SYS_DT_MAJ_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_SYS_USER_MAJ_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL_STOCK_MINIMUM_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL_STOCK_MINIMUM_PH_REASSORT_LIGNE + " , "
                + Constantes.CLE_COL__UID_PH_REASSORT_LIGNE + " " + Constantes.TYPE_COL__UID_PH_REASSORT_LIGNE
                + " ); ";

    }
}
