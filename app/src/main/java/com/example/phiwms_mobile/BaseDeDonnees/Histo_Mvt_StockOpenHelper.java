package com.example.phiwms_mobile.BaseDeDonnees;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by quentinlanusse on 27/06/2017.
 */

public class Histo_Mvt_StockOpenHelper extends DBOpenHelper {

    public Histo_Mvt_StockOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_HISTO_MVT_STOCK = "Histo_mvt_stock";

        public static final String CLE_COL_HMV_PDT_CODE_HISTO_MVT_STOCK = "HMV_PDT_CODE";
        public static final int NUM_COL_HMV_PDT_CODE_HISTO_MVT_STOCK = 1;
        public static final String TYPE_COL_HMV_PDT_CODE_HISTO_MVT_STOCK = "INTEGER";
        public static final String CLE_COL_HMV_PDT_REF_HISTO_MVT_STOCK = "HMV_PDT_REF";
        public static final int NUM_COL_HMV_PDT_REF_HISTO_MVT_STOCK = 2;
        public static final String TYPE_COL_HMV_PDT_REF_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_HMV_REF_DEPOT_HISTO_MVT_STOCK = "HMV_REF_DEPOT";
        public static final int NUM_COL_HMV_REF_DEPOT_HISTO_MVT_STOCK = 3;
        public static final String TYPE_COL_HMV_REF_DEPOT_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_HMV_TYD_CODE_HISTO_MVT_STOCK = "HMV_TYD_CODE";
        public static final int NUM_COL_HMV_TYD_CODE_HISTO_MVT_STOCK = 4;
        public static final String TYPE_COL_HMV_TYD_CODE_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_HMV_NUM_DOC_HISTO_MVT_STOCK = "HMV_NUM_DOC";
        public static final int NUM_COL_HMV_NUM_DOC_HISTO_MVT_STOCK = 5;
        public static final String TYPE_COL_HMV_NUM_DOC_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_HMV_TYM_CODE_HISTO_MVT_STOCK = "HMV_TYM_CODE";
        public static final int NUM_COL_HMV_TYM_CODE_HISTO_MVT_STOCK = 6;
        public static final String TYPE_COL_HMV_TYM_CODE_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_HMV_QTE_HISTO_MVT_STOCK = "HMV_QTE";
        public static final int NUM_COL_HMV_QTE_HISTO_MVT_STOCK = 7;
        public static final String TYPE_COL_HMV_QTE_HISTO_MVT_STOCK = "REAL";
        public static final String CLE_COL_HMV_STOCK_AVANT_HISTO_MVT_STOCK = "HMV_STOCK_AVANT";
        public static final int NUM_COL_HMV_STOCK_AVANT_HISTO_MVT_STOCK = 8;
        public static final String TYPE_COL_HMV_STOCK_AVANT_HISTO_MVT_STOCK = "REAL";
        public static final String CLE_COL_HMV_STOCK_APRES_HISTO_MVT_STOCK = "HMV_STOCK_APRES";
        public static final int NUM_COL_HMV_STOCK_APRES_HISTO_MVT_STOCK = 9;
        public static final String TYPE_COL_HMV_STOCK_APRES_HISTO_MVT_STOCK = "REAL";
        public static final String CLE_COL_HMV_PRIX_COM_HISTO_MVT_STOCK = "HMV_PRIX_COM";
        public static final int NUM_COL_HMV_PRIX_COM_HISTO_MVT_STOCK = 10;
        public static final String TYPE_COL_HMV_PRIX_COM_HISTO_MVT_STOCK = "REAL";
        public static final String CLE_COL_HMV_PRIX_AVANT_HISTO_MVT_STOCK = "HMV_PRIX_AVANT";
        public static final int NUM_COL_HMV_PRIX_AVANT_HISTO_MVT_STOCK = 11;
        public static final String TYPE_COL_HMV_PRIX_AVANT_HISTO_MVT_STOCK = "REAL";
        public static final String CLE_COL_HMV_PRIX_APRES_HISTO_MVT_STOCK = "HMV_PRIX_APRES";
        public static final int NUM_COL_HMV_PRIX_APRES_HISTO_MVT_STOCK = 12;
        public static final String TYPE_COL_HMV_PRIX_APRES_HISTO_MVT_STOCK = "REAL";
        public static final String CLE_COL_HMV_DT_CREAT_HISTO_MVT_STOCK = "HMV_DT_CREAT";
        public static final int NUM_COL_HMV_DT_CREAT_HISTO_MVT_STOCK = 13;
        public static final String TYPE_COL_HMV_DT_CREAT_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_HMV_DT_MVT_HISTO_MVT_STOCK = "HMV_DT_MVT";
        public static final int NUM_COL_HMV_DT_MVT_HISTO_MVT_STOCK = 14;
        public static final String TYPE_COL_HMV_DT_MVT_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_HISTO_MVT_STOCK = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_HISTO_MVT_STOCK = 15;
        public static final String TYPE_COL_SYS_DT_MAJ_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_HISTO_MVT_STOCK = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_HISTO_MVT_STOCK = 16;
        public static final String TYPE_COL_SYS_USER_MAJ_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_HISTO_MVT_STOCK = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_HISTO_MVT_STOCK = 17;
        public static final String TYPE_COL_SYS_HEURE_MAJ_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_HMV_AAAAMM_HISTO_MVT_STOCK = "HMV_AAAAMM";
        public static final int NUM_COL_HMV_AAAAMM_HISTO_MVT_STOCK = 18;
        public static final String TYPE_COL_HMV_AAAAMM_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_HMV_DEPOTPRODUIT_HISTO_MVT_STOCK = "HMV_DepotProduit";
        public static final int NUM_COL_HMV_DEPOTPRODUIT_HISTO_MVT_STOCK = 19;
        public static final String TYPE_COL_HMV_DEPOTPRODUIT_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_RETOUR_FRS_HISTO_MVT_STOCK = "Retour_Frs";
        public static final int NUM_COL_RETOUR_FRS_HISTO_MVT_STOCK = 20;
        public static final String TYPE_COL_RETOUR_FRS_HISTO_MVT_STOCK = "INTEGER";
        public static final String CLE_COL_ABREVIATION_PRESCRIPTEUR_HISTO_MVT_STOCK = "Abreviation_Prescripteur";
        public static final int NUM_COL_ABREVIATION_PRESCRIPTEUR_HISTO_MVT_STOCK = 21;
        public static final String TYPE_COL_ABREVIATION_PRESCRIPTEUR_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_DATE_PRESCRIPTION_HISTO_MVT_STOCK = "Date_Prescription";
        public static final int NUM_COL_DATE_PRESCRIPTION_HISTO_MVT_STOCK = 22;
        public static final String TYPE_COL_DATE_PRESCRIPTION_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_NUM_ORDONANCIER_HISTO_MVT_STOCK = "Num_Ordonancier";
        public static final int NUM_COL_NUM_ORDONANCIER_HISTO_MVT_STOCK = 23;
        public static final String TYPE_COL_NUM_ORDONANCIER_HISTO_MVT_STOCK = "INTEGER";
        public static final String CLE_COL_PRESCRIPTEUR_HISTO_MVT_STOCK = "Prescripteur";
        public static final int NUM_COL_PRESCRIPTEUR_HISTO_MVT_STOCK = 24;
        public static final String TYPE_COL_PRESCRIPTEUR_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_HMV_ID_COMMANDE_LIGNE_HISTO_MVT_STOCK = "HMV_ID_Commande_Ligne";
        public static final int NUM_COL_HMV_ID_COMMANDE_LIGNE_HISTO_MVT_STOCK = 25;
        public static final String TYPE_COL_HMV_ID_COMMANDE_LIGNE_HISTO_MVT_STOCK = "INTEGER";
        public static final String CLE_COL_HMV_PRIX_FACT_HISTO_MVT_STOCK = "HMV_PRIX_Fact";
        public static final int NUM_COL_HMV_PRIX_FACT_HISTO_MVT_STOCK = 26;
        public static final String TYPE_COL_HMV_PRIX_FACT_HISTO_MVT_STOCK = "REAL";
        public static final String CLE_COL_LIQUIDE_HISTO_MVT_STOCK = "Liquidé";
        public static final int NUM_COL_LIQUIDE_HISTO_MVT_STOCK = 27;
        public static final String TYPE_COL_LIQUIDE_HISTO_MVT_STOCK = "INTEGER";
        public static final String CLE_COL_HMV_QTE_FACT_HISTO_MVT_STOCK = "HMV_Qté_Fact";
        public static final int NUM_COL_HMV_QTE_FACT_HISTO_MVT_STOCK = 28;
        public static final String TYPE_COL_HMV_QTE_FACT_HISTO_MVT_STOCK = "REAL";
        public static final String CLE_COL_HMV_TOTAL_QTE_FACT_HISTO_MVT_STOCK = "HMV_Total_Qté_Fact";
        public static final int NUM_COL_HMV_TOTAL_QTE_FACT_HISTO_MVT_STOCK = 29;
        public static final String TYPE_COL_HMV_TOTAL_QTE_FACT_HISTO_MVT_STOCK = "REAL";
        public static final String CLE_COL_HMV_ID_PATIENT_HISTO_MVT_STOCK = "HMV_Id_Patient";
        public static final int NUM_COL_HMV_ID_PATIENT_HISTO_MVT_STOCK = 30;
        public static final String TYPE_COL_HMV_ID_PATIENT_HISTO_MVT_STOCK = "INTEGER";
        public static final String CLE_COL_HMV_NOM_PATIENT_HISTO_MVT_STOCK = "HMV_Nom_Patient";
        public static final int NUM_COL_HMV_NOM_PATIENT_HISTO_MVT_STOCK = 31;
        public static final String TYPE_COL_HMV_NOM_PATIENT_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_HMV_DEPOT_DEST_REF_HISTO_MVT_STOCK = "HMV_DEPOT_DEST_REF";
        public static final int NUM_COL_HMV_DEPOT_DEST_REF_HISTO_MVT_STOCK = 32;
        public static final String TYPE_COL_HMV_DEPOT_DEST_REF_HISTO_MVT_STOCK = "TEXT";
        public static final String CLE_COL_TVA_HISTO_MVT_STOCK = "TVA";
        public static final int NUM_COL_TVA_HISTO_MVT_STOCK = 33;
        public static final String TYPE_COL_TVA_HISTO_MVT_STOCK = "REAL";
        public static final String CLE_COL_HMV_ID_HISTO_MVT_STOCK = "HMV_ID";
        public static final int NUM_COL_HMV_ID_HISTO_MVT_STOCK = 34;
        public static final String TYPE_COL_HMV_ID_HISTO_MVT_STOCK = "INTEGER";


        public static final String CREATION_TABLE_HISTO_MVT_STOCK = "CREATE TABLE "
                + Constantes.TABLE_HISTO_MVT_STOCK
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_HMV_PDT_CODE_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_PDT_CODE_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_PDT_REF_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_PDT_REF_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_REF_DEPOT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_REF_DEPOT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_TYD_CODE_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_TYD_CODE_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_NUM_DOC_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_NUM_DOC_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_TYM_CODE_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_TYM_CODE_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_QTE_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_QTE_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_STOCK_AVANT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_STOCK_AVANT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_STOCK_APRES_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_STOCK_APRES_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_PRIX_COM_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_PRIX_COM_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_PRIX_AVANT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_PRIX_AVANT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_PRIX_APRES_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_PRIX_APRES_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_DT_CREAT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_DT_CREAT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_DT_MVT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_DT_MVT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_SYS_DT_MAJ_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_SYS_USER_MAJ_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_AAAAMM_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_AAAAMM_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_DEPOTPRODUIT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_DEPOTPRODUIT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_RETOUR_FRS_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_RETOUR_FRS_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_ABREVIATION_PRESCRIPTEUR_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_ABREVIATION_PRESCRIPTEUR_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_DATE_PRESCRIPTION_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_DATE_PRESCRIPTION_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_NUM_ORDONANCIER_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_NUM_ORDONANCIER_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_PRESCRIPTEUR_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_PRESCRIPTEUR_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_ID_COMMANDE_LIGNE_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_ID_COMMANDE_LIGNE_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_PRIX_FACT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_PRIX_FACT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_LIQUIDE_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_LIQUIDE_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_QTE_FACT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_QTE_FACT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_TOTAL_QTE_FACT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_TOTAL_QTE_FACT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_ID_PATIENT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_ID_PATIENT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_NOM_PATIENT_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_NOM_PATIENT_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_HMV_DEPOT_DEST_REF_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_DEPOT_DEST_REF_HISTO_MVT_STOCK + " ,"
                + Constantes.CLE_COL_TVA_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_TVA_HISTO_MVT_STOCK + ","
                + Constantes.CLE_COL_HMV_ID_HISTO_MVT_STOCK + " " + Constantes.TYPE_COL_HMV_ID_HISTO_MVT_STOCK
                + ");";
    }
}
