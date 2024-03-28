package com.example.phiwms_mobile.BaseDeDonnees;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import com.example.phiwms_mobile.Classes.Inventaire;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class InventaireOpenHelper extends DBOpenHelper {

    public InventaireOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static long insererUnInventaireEnBDD(SQLiteDatabase db, Inventaire inventaire) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_INVENTAIRE_ID_INVENTAIRE, inventaire.getInventaire_ID());
        contentValues.put(Constantes.CLE_COL_CYCLE_INVENTAIRE, inventaire.getCycle());
        contentValues.put(Constantes.CLE_COL_INVENTAIREDATE_INVENTAIRE, inventaire.getInventaireDate());
        contentValues.put(Constantes.CLE_COL_CYCLEDATEDEBUT_INVENTAIRE, inventaire.getCycleDateDebut());
        contentValues.put(Constantes.CLE_COL__SYS_DT_MAJ_INVENTAIRE, inventaire.get_SYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_USER_MAJ_INVENTAIRE, inventaire.get_SYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_HEURE_MAJ_INVENTAIRE, inventaire.get_SYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_CYCLEDATEFIN_INVENTAIRE, inventaire.getCycleDateFin());
        contentValues.put(Constantes.CLE_COL_CLOTUREACTIVE_INVENTAIRE, inventaire.getClotureActive());
        contentValues.put(Constantes.CLE_COL_OBJET_INVENTAIRE, inventaire.getObjet());
        contentValues.put(Constantes.CLE_COL_MODE_COMPTABILISATION_INVENTAIRE, inventaire.getMode_Comptabilisation());
        contentValues.put(Constantes.CLE_COL_DEPOTREFERENCE_INVENTAIRE, inventaire.getDepotReference());
        contentValues.put(Constantes.CLE_COL_DEPOTNOM_INVENTAIRE, inventaire.getDepotNom());
        contentValues.put(Constantes.CLE_COL_OPERATEUR_INVENTAIRE, inventaire.getOperateur());
        contentValues.put(Constantes.CLE_COL_NBLIGNES_INVENTAIRE, inventaire.getNBLignes());
        contentValues.put(Constantes.CLE_COL_VALEUR_TTC_INVENTAIRE, inventaire.getValeur_TTC());
        contentValues.put(Constantes.CLE_COL_VALEUR_PUMP_TTC_INVENTAIRE, inventaire.getValeur_PUMP_TTC());

        long rowID = db.insert(Constantes.TABLE_INVENTAIRE, null, contentValues);

        inventaire.setPhiMR4UUID((int) rowID);

        return rowID;
    }


    public static Inventaire getInventaireByPhiMR4UUID(SQLiteDatabase db, int id) {
        Inventaire inventaire = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_INVENTAIRE + " WHERE " + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            inventaire = new Inventaire(cursor);
        }

        cursor.close();
        cursor = null;
        return inventaire;
    }

    public static class Constantes implements BaseColumns {

        public static final String TABLE_INVENTAIRE = "Inventaire";

        public static final String CLE_COL_CYCLE_INVENTAIRE = "Cycle";
        public static final int NUM_COL_CYCLE_INVENTAIRE = 1;
        public static final String TYPE_COL_CYCLE_INVENTAIRE = "TEXT";
        public static final String CLE_COL_INVENTAIREDATE_INVENTAIRE = "InventaireDate";
        public static final int NUM_COL_INVENTAIREDATE_INVENTAIRE = 2;
        public static final String TYPE_COL_INVENTAIREDATE_INVENTAIRE = "TEXT";
        public static final String CLE_COL_CYCLEDATEDEBUT_INVENTAIRE = "cycleDateDebut";
        public static final int NUM_COL_CYCLEDATEDEBUT_INVENTAIRE = 3;
        public static final String TYPE_COL_CYCLEDATEDEBUT_INVENTAIRE = "TEXT";
        public static final String CLE_COL__SYS_DT_MAJ_INVENTAIRE = "_SYS_DT_MAJ";
        public static final int NUM_COL__SYS_DT_MAJ_INVENTAIRE = 4;
        public static final String TYPE_COL__SYS_DT_MAJ_INVENTAIRE = "TEXT";
        public static final String CLE_COL__SYS_USER_MAJ_INVENTAIRE = "_SYS_USER_MAJ";
        public static final int NUM_COL__SYS_USER_MAJ_INVENTAIRE = 5;
        public static final String TYPE_COL__SYS_USER_MAJ_INVENTAIRE = "TEXT";
        public static final String CLE_COL__SYS_HEURE_MAJ_INVENTAIRE = "_SYS_HEURE_MAJ";
        public static final int NUM_COL__SYS_HEURE_MAJ_INVENTAIRE = 6;
        public static final String TYPE_COL__SYS_HEURE_MAJ_INVENTAIRE = "TEXT";
        public static final String CLE_COL_CYCLEDATEFIN_INVENTAIRE = "cycleDateFin";
        public static final int NUM_COL_CYCLEDATEFIN_INVENTAIRE = 7;
        public static final String TYPE_COL_CYCLEDATEFIN_INVENTAIRE = "TEXT";
        public static final String CLE_COL_CLOTUREACTIVE_INVENTAIRE = "clotureActive";
        public static final int NUM_COL_CLOTUREACTIVE_INVENTAIRE = 8;
        public static final String TYPE_COL_CLOTUREACTIVE_INVENTAIRE = "INTEGER";
        public static final String CLE_COL_OBJET_INVENTAIRE = "objet";
        public static final int NUM_COL_OBJET_INVENTAIRE = 9;
        public static final String TYPE_COL_OBJET_INVENTAIRE = "TEXT";
        public static final String CLE_COL_MODE_COMPTABILISATION_INVENTAIRE = "Mode_Comptabilisation";
        public static final int NUM_COL_MODE_COMPTABILISATION_INVENTAIRE = 10;
        public static final String TYPE_COL_MODE_COMPTABILISATION_INVENTAIRE = "TEXT";
        public static final String CLE_COL_DEPOTREFERENCE_INVENTAIRE = "depotReference";
        public static final int NUM_COL_DEPOTREFERENCE_INVENTAIRE = 11;
        public static final String TYPE_COL_DEPOTREFERENCE_INVENTAIRE = "TEXT";
        public static final String CLE_COL_DEPOTNOM_INVENTAIRE = "depotNom";
        public static final int NUM_COL_DEPOTNOM_INVENTAIRE = 12;
        public static final String TYPE_COL_DEPOTNOM_INVENTAIRE = "TEXT";
        public static final String CLE_COL_OPERATEUR_INVENTAIRE = "operateur";
        public static final int NUM_COL_OPERATEUR_INVENTAIRE = 13;
        public static final String TYPE_COL_OPERATEUR_INVENTAIRE = "TEXT";
        public static final String CLE_COL_NBLIGNES_INVENTAIRE = "NBLignes";
        public static final int NUM_COL_NBLIGNES_INVENTAIRE = 14;
        public static final String TYPE_COL_NBLIGNES_INVENTAIRE = "INTEGER";
        public static final String CLE_COL_VALEUR_TTC_INVENTAIRE = "Valeur_TTC";
        public static final int NUM_COL_VALEUR_TTC_INVENTAIRE = 15;
        public static final String TYPE_COL_VALEUR_TTC_INVENTAIRE = "REAL";
        public static final String CLE_COL_VALEUR_PUMP_TTC_INVENTAIRE = "Valeur_PUMP_TTC";
        public static final int NUM_COL_VALEUR_PUMP_TTC_INVENTAIRE = 16;
        public static final String TYPE_COL_VALEUR_PUMP_TTC_INVENTAIRE = "REAL";
        public static final String CLE_COL_INVENTAIRE_ID_INVENTAIRE = "Inventaire_ID";
        public static final int NUM_COL_INVENTAIRE_ID_INVENTAIRE = 17;
        public static final String TYPE_COL_INVENTAIRE_ID_INVENTAIRE = "INTEGER";


        public static final String CREATION_TABLE_INVENTAIRE = "CREATE TABLE " + Constantes.TABLE_INVENTAIRE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_CYCLE_INVENTAIRE + " " + Constantes.TYPE_COL_CYCLE_INVENTAIRE + " ,"
                + Constantes.CLE_COL_INVENTAIREDATE_INVENTAIRE + " " + Constantes.TYPE_COL_INVENTAIREDATE_INVENTAIRE + " ,"
                + Constantes.CLE_COL_CYCLEDATEDEBUT_INVENTAIRE + " " + Constantes.TYPE_COL_CYCLEDATEDEBUT_INVENTAIRE + " ,"
                + Constantes.CLE_COL__SYS_DT_MAJ_INVENTAIRE + " " + Constantes.TYPE_COL__SYS_DT_MAJ_INVENTAIRE + " ,"
                + Constantes.CLE_COL__SYS_USER_MAJ_INVENTAIRE + " " + Constantes.TYPE_COL__SYS_USER_MAJ_INVENTAIRE + " ,"
                + Constantes.CLE_COL__SYS_HEURE_MAJ_INVENTAIRE + " " + Constantes.TYPE_COL__SYS_HEURE_MAJ_INVENTAIRE + " ,"
                + Constantes.CLE_COL_CYCLEDATEFIN_INVENTAIRE + " " + Constantes.TYPE_COL_CYCLEDATEFIN_INVENTAIRE + " ,"
                + Constantes.CLE_COL_CLOTUREACTIVE_INVENTAIRE + " " + Constantes.TYPE_COL_CLOTUREACTIVE_INVENTAIRE + " ,"
                + Constantes.CLE_COL_OBJET_INVENTAIRE + " " + Constantes.TYPE_COL_OBJET_INVENTAIRE + " ,"
                + Constantes.CLE_COL_MODE_COMPTABILISATION_INVENTAIRE + " " + Constantes.TYPE_COL_MODE_COMPTABILISATION_INVENTAIRE + " ,"
                + Constantes.CLE_COL_DEPOTREFERENCE_INVENTAIRE + " " + Constantes.TYPE_COL_DEPOTREFERENCE_INVENTAIRE + " ,"
                + Constantes.CLE_COL_DEPOTNOM_INVENTAIRE + " " + Constantes.TYPE_COL_DEPOTNOM_INVENTAIRE + " ,"
                + Constantes.CLE_COL_OPERATEUR_INVENTAIRE + " " + Constantes.TYPE_COL_OPERATEUR_INVENTAIRE + " ,"
                + Constantes.CLE_COL_NBLIGNES_INVENTAIRE + " " + Constantes.TYPE_COL_NBLIGNES_INVENTAIRE + " ,"
                + Constantes.CLE_COL_VALEUR_TTC_INVENTAIRE + " " + Constantes.TYPE_COL_VALEUR_TTC_INVENTAIRE + " ,"
                + Constantes.CLE_COL_VALEUR_PUMP_TTC_INVENTAIRE + " " + Constantes.TYPE_COL_VALEUR_PUMP_TTC_INVENTAIRE + ","
                + Constantes.CLE_COL_INVENTAIRE_ID_INVENTAIRE + " " + Constantes.TYPE_COL_INVENTAIRE_ID_INVENTAIRE
                + ");";
    }
}
