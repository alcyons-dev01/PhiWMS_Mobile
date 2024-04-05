package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class SYS_Mvt_Stock_TypeOpenHelper extends DBOpenHelper {

    public SYS_Mvt_Stock_TypeOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class Constantes {
        public static final String TABLE_SYS_MVT_STOCK_TYPE = "SYS_mvt_stock_type";

        public static final String CLE_COL_TYM_RUID_SYS_MVT_STOCK_TYPE = "TYM_RUID";
        public static final int NUM_COL_TYM_RUID_SYS_MVT_STOCK_TYPE = 1;
        public static final String TYPE_COL_TYM_RUID_SYS_MVT_STOCK_TYPE = "TEXT";
        public static final String CLE_COL_TYM_LIB_SYS_MVT_STOCK_TYPE = "TYM_LIB";
        public static final int NUM_COL_TYM_LIB_SYS_MVT_STOCK_TYPE = 2;
        public static final String TYPE_COL_TYM_LIB_SYS_MVT_STOCK_TYPE = "TEXT";
        public static final String CLE_COL_TYM_SENS_ENTREE_SYS_MVT_STOCK_TYPE = "TYM_SENS_ENTREE";
        public static final int NUM_COL_TYM_SENS_ENTREE_SYS_MVT_STOCK_TYPE = 3;
        public static final String TYPE_COL_TYM_SENS_ENTREE_SYS_MVT_STOCK_TYPE = "INTEGER";
        public static final String CLE_COL_TYM_SENS_SORTIE_SYS_MVT_STOCK_TYPE = "TYM_SENS_SORTIE";
        public static final int NUM_COL_TYM_SENS_SORTIE_SYS_MVT_STOCK_TYPE = 4;
        public static final String TYPE_COL_TYM_SENS_SORTIE_SYS_MVT_STOCK_TYPE = "INTEGER";
        public static final String CLE_COL_TYM_SENS_INVENTAIRE_SYS_MVT_STOCK_TYPE = "TYM_SENS_INVENTAIRE";
        public static final int NUM_COL_TYM_SENS_INVENTAIRE_SYS_MVT_STOCK_TYPE = 5;
        public static final String TYPE_COL_TYM_SENS_INVENTAIRE_SYS_MVT_STOCK_TYPE = "INTEGER";
        public static final String CLE_COL_TYM_TYPE_MAJ_SYS_MVT_STOCK_TYPE = "TYM_TYPE_MAJ";
        public static final int NUM_COL_TYM_TYPE_MAJ_SYS_MVT_STOCK_TYPE = 6;
        public static final String TYPE_COL_TYM_TYPE_MAJ_SYS_MVT_STOCK_TYPE = "TEXT";
        public static final String CLE_COL_TYM_DT_CREATION_SYS_MVT_STOCK_TYPE = "TYM_DT_CREATION";
        public static final int NUM_COL_TYM_DT_CREATION_SYS_MVT_STOCK_TYPE = 7;
        public static final String TYPE_COL_TYM_DT_CREATION_SYS_MVT_STOCK_TYPE = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_SYS_MVT_STOCK_TYPE = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_SYS_MVT_STOCK_TYPE = 8;
        public static final String TYPE_COL_SYS_DT_MAJ_SYS_MVT_STOCK_TYPE = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_SYS_MVT_STOCK_TYPE = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_SYS_MVT_STOCK_TYPE = 9;
        public static final String TYPE_COL_SYS_USER_MAJ_SYS_MVT_STOCK_TYPE = "TEXT";
        public static final String CLE_COL_TYM_SENS_ATTENDU_SYS_MVT_STOCK_TYPE = "TYM_SENS_ATTENDU";
        public static final int NUM_COL_TYM_SENS_ATTENDU_SYS_MVT_STOCK_TYPE = 10;
        public static final String TYPE_COL_TYM_SENS_ATTENDU_SYS_MVT_STOCK_TYPE = "INTEGER";
        public static final String CLE_COL_SYS_HEURE_MAJ_SYS_MVT_STOCK_TYPE = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_SYS_MVT_STOCK_TYPE = 11;
        public static final String TYPE_COL_SYS_HEURE_MAJ_SYS_MVT_STOCK_TYPE = "TEXT";
        public static final String CLE_COL_XSYS_USER_MAJ_SYS_MVT_STOCK_TYPE = "XSYS_USER_MAJ";
        public static final int NUM_COL_XSYS_USER_MAJ_SYS_MVT_STOCK_TYPE = 13;
        public static final String TYPE_COL_XSYS_USER_MAJ_SYS_MVT_STOCK_TYPE = "TEXT";
        public static final String CLE_COL_TYM_ID_SYS_MVT_STOCK_TYPE = "TYM_ID";
        public static final int NUM_COL_TYM_ID_SYS_MVT_STOCK_TYPE = 14;
        public static final String TYPE_COL_TYM_ID_SYS_MVT_STOCK_TYPE = "INTEGER";

        public static final String CREATION_TABLE_SYS_MVT_STOCK_TYPE = "CREATE TABLE " + Constantes.TABLE_SYS_MVT_STOCK_TYPE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_TYM_RUID_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_TYM_RUID_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_TYM_LIB_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_TYM_LIB_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_TYM_SENS_ENTREE_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_TYM_SENS_ENTREE_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_TYM_SENS_SORTIE_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_TYM_SENS_SORTIE_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_TYM_SENS_INVENTAIRE_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_TYM_SENS_INVENTAIRE_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_TYM_TYPE_MAJ_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_TYM_TYPE_MAJ_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_TYM_DT_CREATION_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_TYM_DT_CREATION_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_SYS_DT_MAJ_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_SYS_USER_MAJ_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_TYM_SENS_ATTENDU_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_TYM_SENS_ATTENDU_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_SYS_MVT_STOCK_TYPE + " ,"
                + Constantes.CLE_COL_XSYS_USER_MAJ_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_XSYS_USER_MAJ_SYS_MVT_STOCK_TYPE + ","
                + Constantes.CLE_COL_TYM_ID_SYS_MVT_STOCK_TYPE + " " + Constantes.TYPE_COL_TYM_ID_SYS_MVT_STOCK_TYPE
                + ");";
    }
}
