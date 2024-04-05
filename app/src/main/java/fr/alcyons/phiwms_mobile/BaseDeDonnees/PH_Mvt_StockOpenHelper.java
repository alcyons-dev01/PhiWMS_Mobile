package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class PH_Mvt_StockOpenHelper extends DBOpenHelper {

    public PH_Mvt_StockOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PH_MVT_STOCK = "PH_Mvt_Stock";

        public static final String CLE_COL_MVT_PDT_CODE_PH_MVT_STOCK = "MVT_PDT_CODE";
        public static final int NUM_COL_MVT_PDT_CODE_PH_MVT_STOCK = 1;
        public static final String TYPE_COL_MVT_PDT_CODE_PH_MVT_STOCK = "INTEGER";
        public static final String CLE_COL_MVT_DEP_ORIG_PH_MVT_STOCK = "MVT_DEP_ORIG";
        public static final int NUM_COL_MVT_DEP_ORIG_PH_MVT_STOCK = 2;
        public static final String TYPE_COL_MVT_DEP_ORIG_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_MVT_DEP_DEST_PH_MVT_STOCK = "MVT_DEP_DEST";
        public static final int NUM_COL_MVT_DEP_DEST_PH_MVT_STOCK = 3;
        public static final String TYPE_COL_MVT_DEP_DEST_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_MVT_TYD_CODE_PH_MVT_STOCK = "MVT_TYD_CODE";
        public static final int NUM_COL_MVT_TYD_CODE_PH_MVT_STOCK = 4;
        public static final String TYPE_COL_MVT_TYD_CODE_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_MVT_NUM_DOC_PH_MVT_STOCK = "MVT_NUM_DOC";
        public static final int NUM_COL_MVT_NUM_DOC_PH_MVT_STOCK = 5;
        public static final String TYPE_COL_MVT_NUM_DOC_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_MVT_QTE_PH_MVT_STOCK = "MVT_QTE";
        public static final int NUM_COL_MVT_QTE_PH_MVT_STOCK = 6;
        public static final String TYPE_COL_MVT_QTE_PH_MVT_STOCK = "REAL";
        public static final String CLE_COL_MVT_PRIX_UNIT_PH_MVT_STOCK = "MVT_PRIX_UNIT";
        public static final int NUM_COL_MVT_PRIX_UNIT_PH_MVT_STOCK = 7;
        public static final String TYPE_COL_MVT_PRIX_UNIT_PH_MVT_STOCK = "REAL";
        public static final String CLE_COL_MVT_DT_CREAT_PH_MVT_STOCK = "MVT_DT_CREAT";
        public static final int NUM_COL_MVT_DT_CREAT_PH_MVT_STOCK = 8;
        public static final String TYPE_COL_MVT_DT_CREAT_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_MVT_DT_MVT_PH_MVT_STOCK = "MVT_DT_MVT";
        public static final int NUM_COL_MVT_DT_MVT_PH_MVT_STOCK = 9;
        public static final String TYPE_COL_MVT_DT_MVT_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_PH_MVT_STOCK = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PH_MVT_STOCK = 10;
        public static final String TYPE_COL_SYS_DT_MAJ_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PH_MVT_STOCK = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PH_MVT_STOCK = 11;
        public static final String TYPE_COL_SYS_USER_MAJ_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_RIEN_PH_MVT_STOCK = "Rien";
        public static final int NUM_COL_RIEN_PH_MVT_STOCK = 12;
        public static final String TYPE_COL_RIEN_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PH_MVT_STOCK = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PH_MVT_STOCK = 13;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_MVT_ID_COMMANDE_LIGNE_PH_MVT_STOCK = "MVT_ID_Commande_Ligne";
        public static final int NUM_COL_MVT_ID_COMMANDE_LIGNE_PH_MVT_STOCK = 14;
        public static final String TYPE_COL_MVT_ID_COMMANDE_LIGNE_PH_MVT_STOCK = "INTEGER";
        public static final String CLE_COL_HMV_PRIX_UNITAIRE_TTC_FACT_PH_MVT_STOCK = "HMV_PRIX_UNITAIRE_TTC_FACT";
        public static final int NUM_COL_HMV_PRIX_UNITAIRE_TTC_FACT_PH_MVT_STOCK = 15;
        public static final String TYPE_COL_HMV_PRIX_UNITAIRE_TTC_FACT_PH_MVT_STOCK = "REAL";
        public static final String CLE_COL_MVT_STOCK_ZONE_PH_MVT_STOCK = "MVT_Stock_Zone";
        public static final int NUM_COL_MVT_STOCK_ZONE_PH_MVT_STOCK = 16;
        public static final String TYPE_COL_MVT_STOCK_ZONE_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL_MVT_STOCK_EMPLACEMENT_PH_MVT_STOCK = "MVT_stock_emplacement";
        public static final int NUM_COL_MVT_STOCK_EMPLACEMENT_PH_MVT_STOCK = 17;
        public static final String TYPE_COL_MVT_STOCK_EMPLACEMENT_PH_MVT_STOCK = "TEXT";
        public static final String CLE_COL__UID_PH_MVT_STOCK = "_UID";
        public static final int NUM_COL__UID_PH_MVT_STOCK = 18;
        public static final String TYPE_COL__UID_PH_MVT_STOCK = "INTEGER";
        public static final String CLE_COL_MVT_ID_PH_MVT_STOCK = "MVT_ID";
        public static final int NUM_COL_MVT_ID_PH_MVT_STOCK = 19;
        public static final String TYPE_COL_MVT_ID_PH_MVT_STOCK = "INTEGER";


        public static final String CREATION_TABLE_PH_MVT_STOCK = "CREATE TABLE "
                + Constantes.TABLE_PH_MVT_STOCK
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_MVT_PDT_CODE_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_PDT_CODE_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_DEP_ORIG_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_DEP_ORIG_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_DEP_DEST_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_DEP_DEST_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_TYD_CODE_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_TYD_CODE_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_NUM_DOC_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_NUM_DOC_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_QTE_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_QTE_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_PRIX_UNIT_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_PRIX_UNIT_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_DT_CREAT_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_DT_CREAT_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_DT_MVT_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_DT_MVT_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_PH_MVT_STOCK + " " + Constantes.TYPE_COL_SYS_DT_MAJ_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_PH_MVT_STOCK + " " + Constantes.TYPE_COL_SYS_USER_MAJ_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_RIEN_PH_MVT_STOCK + " " + Constantes.TYPE_COL_RIEN_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PH_MVT_STOCK + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_ID_COMMANDE_LIGNE_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_ID_COMMANDE_LIGNE_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_PRIX_UNITAIRE_TTC_FACT_PH_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_PRIX_UNITAIRE_TTC_FACT_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_STOCK_ZONE_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_STOCK_ZONE_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL_MVT_STOCK_EMPLACEMENT_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_STOCK_EMPLACEMENT_PH_MVT_STOCK + " ,"
                + Constantes.CLE_COL__UID_PH_MVT_STOCK + " " + Constantes.TYPE_COL__UID_PH_MVT_STOCK + ","
                + Constantes.CLE_COL_MVT_ID_PH_MVT_STOCK + " " + Constantes.TYPE_COL_MVT_ID_PH_MVT_STOCK
                + ");";
    }
}
