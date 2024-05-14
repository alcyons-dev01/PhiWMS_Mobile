package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class SYS_Document_TypeOpenHelper extends DBOpenHelper {

    public SYS_Document_TypeOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_SYS_DOCUMENT_TYPE = "SYS_document_type";

        public static final String CLE_COL_TYD_CODE_SYS_DOCUMENT_TYPE = "TYD_CODE";
        public static final int NUM_COL_TYD_CODE_SYS_DOCUMENT_TYPE = 1;
        public static final String TYPE_COL_TYD_CODE_SYS_DOCUMENT_TYPE = "TEXT";
        public static final String CLE_COL_TYD_DEPOT_SYS_DOCUMENT_TYPE = "TYD_DEPOT";
        public static final int NUM_COL_TYD_DEPOT_SYS_DOCUMENT_TYPE = 2;
        public static final String TYPE_COL_TYD_DEPOT_SYS_DOCUMENT_TYPE = "TEXT";
        public static final String CLE_COL_TYD_TYM_CODE1_SYS_DOCUMENT_TYPE = "TYD_TYM_CODE1";
        public static final int NUM_COL_TYD_TYM_CODE1_SYS_DOCUMENT_TYPE = 3;
        public static final String TYPE_COL_TYD_TYM_CODE1_SYS_DOCUMENT_TYPE = "TEXT";
        public static final String CLE_COL_TYD_TYM_CODE2_SYS_DOCUMENT_TYPE = "TYD_TYM_CODE2";
        public static final int NUM_COL_TYD_TYM_CODE2_SYS_DOCUMENT_TYPE = 4;
        public static final String TYPE_COL_TYD_TYM_CODE2_SYS_DOCUMENT_TYPE = "TEXT";
        public static final String CLE_COL_TYD_DT_CREAT_SYS_DOCUMENT_TYPE = "TYD_DT_CREAT";
        public static final int NUM_COL_TYD_DT_CREAT_SYS_DOCUMENT_TYPE = 5;
        public static final String TYPE_COL_TYD_DT_CREAT_SYS_DOCUMENT_TYPE = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_SYS_DOCUMENT_TYPE = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_SYS_DOCUMENT_TYPE = 6;
        public static final String TYPE_COL_SYS_DT_MAJ_SYS_DOCUMENT_TYPE = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_SYS_DOCUMENT_TYPE = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_SYS_DOCUMENT_TYPE = 7;
        public static final String TYPE_COL_SYS_USER_MAJ_SYS_DOCUMENT_TYPE = "TEXT";
        public static final String CLE_COL_TYD_DESIGNATION_SYS_DOCUMENT_TYPE = "TYD_DESIGNATION";
        public static final int NUM_COL_TYD_DESIGNATION_SYS_DOCUMENT_TYPE = 8;
        public static final String TYPE_COL_TYD_DESIGNATION_SYS_DOCUMENT_TYPE = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_SYS_DOCUMENT_TYPE = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_SYS_DOCUMENT_TYPE = 9;
        public static final String TYPE_COL_SYS_HEURE_MAJ_SYS_DOCUMENT_TYPE = "TEXT";
        public static final String CLE_COL_RIEN_SYS_DOCUMENT_TYPE = "rien";
        public static final int NUM_COL_RIEN_SYS_DOCUMENT_TYPE = 10;
        public static final String TYPE_COL_RIEN_SYS_DOCUMENT_TYPE = "TEXT";
        public static final String CLE_COL_TYD_ID_SYS_DOCUMENT_TYPE = "TYD_ID";
        public static final int NUM_COL_TYD_ID_SYS_DOCUMENT_TYPE = 11;
        public static final String TYPE_COL_TYD_ID_SYS_DOCUMENT_TYPE = "INTEGER";

        public static final String CREATION_TABLE_SYS_DOCUMENT_TYPE = "CREATE TABLE " + Constantes.TABLE_SYS_DOCUMENT_TYPE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_TYD_CODE_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_TYD_CODE_SYS_DOCUMENT_TYPE + " ,"
                + Constantes.CLE_COL_TYD_DEPOT_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_TYD_DEPOT_SYS_DOCUMENT_TYPE + " ,"
                + Constantes.CLE_COL_TYD_TYM_CODE1_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_TYD_TYM_CODE1_SYS_DOCUMENT_TYPE + " ,"
                + Constantes.CLE_COL_TYD_TYM_CODE2_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_TYD_TYM_CODE2_SYS_DOCUMENT_TYPE + " ,"
                + Constantes.CLE_COL_TYD_DT_CREAT_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_TYD_DT_CREAT_SYS_DOCUMENT_TYPE + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_SYS_DT_MAJ_SYS_DOCUMENT_TYPE + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_SYS_USER_MAJ_SYS_DOCUMENT_TYPE + " ,"
                + Constantes.CLE_COL_TYD_DESIGNATION_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_TYD_DESIGNATION_SYS_DOCUMENT_TYPE + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_SYS_DOCUMENT_TYPE + " ,"
                + Constantes.CLE_COL_RIEN_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_RIEN_SYS_DOCUMENT_TYPE + ","
                + Constantes.CLE_COL_TYD_ID_SYS_DOCUMENT_TYPE + " " + Constantes.TYPE_COL_TYD_ID_SYS_DOCUMENT_TYPE
                + ");";
    }
}
