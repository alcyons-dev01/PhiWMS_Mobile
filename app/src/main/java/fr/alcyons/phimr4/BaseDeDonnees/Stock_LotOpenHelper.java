package fr.alcyons.phimr4.BaseDeDonnees;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Stock_LotOpenHelper extends DBOpenHelper {

    public Stock_LotOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_STOCK_LOT = "Stock_lot";

        public static final String CLE_COL_REF_DEPOT_STOCK_LOT = "Ref_Depot";
        public static final int NUM_COL_REF_DEPOT_STOCK_LOT = 1;
        public static final String TYPE_COL_REF_DEPOT_STOCK_LOT = "TEXT";
        public static final String CLE_COL_ID_PRODUIT_STOCK_LOT = "ID_Produit";
        public static final int NUM_COL_ID_PRODUIT_STOCK_LOT = 2;
        public static final String TYPE_COL_ID_PRODUIT_STOCK_LOT = "INTEGER";
        public static final String CLE_COL_REF_PRODUIT_STOCK_LOT = "Ref_Produit";
        public static final int NUM_COL_REF_PRODUIT_STOCK_LOT = 3;
        public static final String TYPE_COL_REF_PRODUIT_STOCK_LOT = "TEXT";
        public static final String CLE_COL_NUMERO_LOT_STOCK_LOT = "Numero_Lot";
        public static final int NUM_COL_NUMERO_LOT_STOCK_LOT = 4;
        public static final String TYPE_COL_NUMERO_LOT_STOCK_LOT = "TEXT";
        public static final String CLE_COL_PEREMPTION_AAAAMM_STOCK_LOT = "Peremption_AAAAMM";
        public static final int NUM_COL_PEREMPTION_AAAAMM_STOCK_LOT = 5;
        public static final String TYPE_COL_PEREMPTION_AAAAMM_STOCK_LOT = "TEXT";
        public static final String CLE_COL_QTE_INVENT_STOCK_LOT = "Qte_Invent";
        public static final int NUM_COL_QTE_INVENT_STOCK_LOT = 6;
        public static final String TYPE_COL_QTE_INVENT_STOCK_LOT = "REAL";
        public static final String CLE_COL_QTE_ENTREE_STOCK_LOT = "Qte_Entree";
        public static final int NUM_COL_QTE_ENTREE_STOCK_LOT = 7;
        public static final String TYPE_COL_QTE_ENTREE_STOCK_LOT = "REAL";
        public static final String CLE_COL_QTE_SORTIE_STOCK_LOT = "Qte_Sortie";
        public static final int NUM_COL_QTE_SORTIE_STOCK_LOT = 8;
        public static final String TYPE_COL_QTE_SORTIE_STOCK_LOT = "REAL";
        public static final String CLE_COL_SYS_DT_MAJ_STOCK_LOT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_STOCK_LOT = 9;
        public static final String TYPE_COL_SYS_DT_MAJ_STOCK_LOT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_STOCK_LOT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_STOCK_LOT = 10;
        public static final String TYPE_COL_SYS_HEURE_MAJ_STOCK_LOT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_STOCK_LOT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_STOCK_LOT = 11;
        public static final String TYPE_COL_SYS_USER_MAJ_STOCK_LOT = "TEXT";
        public static final String CLE_COL_QTE_ACTUELLE_STOCK_LOT = "Qte_Actuelle";
        public static final int NUM_COL_QTE_ACTUELLE_STOCK_LOT = 12;
        public static final String TYPE_COL_QTE_ACTUELLE_STOCK_LOT = "REAL";
        public static final String CLE_COL_DATE_CREATION_STOCK_LOT = "Date_Creation";
        public static final int NUM_COL_DATE_CREATION_STOCK_LOT = 13;
        public static final String TYPE_COL_DATE_CREATION_STOCK_LOT = "TEXT";
        public static final String CLE_COL_DATE_DER_ENTREE_STOCK_LOT = "Date_Der_Entree";
        public static final int NUM_COL_DATE_DER_ENTREE_STOCK_LOT = 14;
        public static final String TYPE_COL_DATE_DER_ENTREE_STOCK_LOT = "TEXT";
        public static final String CLE_COL_DATE_DER_SORTIE_STOCK_LOT = "Date_Der_Sortie";
        public static final int NUM_COL_DATE_DER_SORTIE_STOCK_LOT = 15;
        public static final String TYPE_COL_DATE_DER_SORTIE_STOCK_LOT = "TEXT";
        public static final String CLE_COL_DATE_INVENTAIRE_STOCK_LOT = "Date_Inventaire";
        public static final int NUM_COL_DATE_INVENTAIRE_STOCK_LOT = 16;
        public static final String TYPE_COL_DATE_INVENTAIRE_STOCK_LOT = "TEXT";
        public static final String CLE_COL_QTE_AV_INVENTAIRE_STOCK_LOT = "Qte_Av_Inventaire";
        public static final int NUM_COL_QTE_AV_INVENTAIRE_STOCK_LOT = 17;
        public static final String TYPE_COL_QTE_AV_INVENTAIRE_STOCK_LOT = "REAL";
        public static final String CLE_COL_RIEN_STOCK_LOT = "rien";
        public static final int NUM_COL_RIEN_STOCK_LOT = 18;
        public static final String TYPE_COL_RIEN_STOCK_LOT = "TEXT";
        public static final String CLE_COL_PEREMPTION_DATE_STOCK_LOT = "Peremption_date";
        public static final int NUM_COL_PEREMPTION_DATE_STOCK_LOT = 19;
        public static final String TYPE_COL_PEREMPTION_DATE_STOCK_LOT = "TEXT";
        public static final String CLE_COL_QUARANTAINE_STOCK_LOT = "Quarantaine";
        public static final int NUM_COL_QUARANTAINE_STOCK_LOT = 20;
        public static final String TYPE_COL_QUARANTAINE_STOCK_LOT = "TEXT";
        public static final String CLE_COL__UID_STOCK_LOT = "_UID";
        public static final int NUM_COL__UID_STOCK_LOT = 21;
        public static final String TYPE_COL__UID_STOCK_LOT = "INTEGER";


        public static final String CREATION_TABLE_STOCK_LOT = "CREATE TABLE " + Constantes.TABLE_STOCK_LOT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_REF_DEPOT_STOCK_LOT + " " + Constantes.TYPE_COL_REF_DEPOT_STOCK_LOT + " ,"
                + Constantes.CLE_COL_ID_PRODUIT_STOCK_LOT + " " + Constantes.TYPE_COL_ID_PRODUIT_STOCK_LOT + " ,"
                + Constantes.CLE_COL_REF_PRODUIT_STOCK_LOT + " " + Constantes.TYPE_COL_REF_PRODUIT_STOCK_LOT + " ,"
                + Constantes.CLE_COL_NUMERO_LOT_STOCK_LOT + " " + Constantes.TYPE_COL_NUMERO_LOT_STOCK_LOT + " ,"
                + Constantes.CLE_COL_PEREMPTION_AAAAMM_STOCK_LOT + " " + Constantes.TYPE_COL_PEREMPTION_AAAAMM_STOCK_LOT + " ,"
                + Constantes.CLE_COL_QTE_INVENT_STOCK_LOT + " " + Constantes.TYPE_COL_QTE_INVENT_STOCK_LOT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_STOCK_LOT + " " + Constantes.TYPE_COL_QTE_ENTREE_STOCK_LOT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_STOCK_LOT + " " + Constantes.TYPE_COL_QTE_SORTIE_STOCK_LOT + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_STOCK_LOT + " " + Constantes.TYPE_COL_SYS_DT_MAJ_STOCK_LOT + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_STOCK_LOT + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_STOCK_LOT + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_STOCK_LOT + " " + Constantes.TYPE_COL_SYS_USER_MAJ_STOCK_LOT + " ,"
                + Constantes.CLE_COL_QTE_ACTUELLE_STOCK_LOT + " " + Constantes.TYPE_COL_QTE_ACTUELLE_STOCK_LOT + " ,"
                + Constantes.CLE_COL_DATE_CREATION_STOCK_LOT + " " + Constantes.TYPE_COL_DATE_CREATION_STOCK_LOT + " ,"
                + Constantes.CLE_COL_DATE_DER_ENTREE_STOCK_LOT + " " + Constantes.TYPE_COL_DATE_DER_ENTREE_STOCK_LOT + " ,"
                + Constantes.CLE_COL_DATE_DER_SORTIE_STOCK_LOT + " " + Constantes.TYPE_COL_DATE_DER_SORTIE_STOCK_LOT + " ,"
                + Constantes.CLE_COL_DATE_INVENTAIRE_STOCK_LOT + " " + Constantes.TYPE_COL_DATE_INVENTAIRE_STOCK_LOT + " ,"
                + Constantes.CLE_COL_QTE_AV_INVENTAIRE_STOCK_LOT + " " + Constantes.TYPE_COL_QTE_AV_INVENTAIRE_STOCK_LOT + " ,"
                + Constantes.CLE_COL_RIEN_STOCK_LOT + " " + Constantes.TYPE_COL_RIEN_STOCK_LOT + " ,"
                + Constantes.CLE_COL_PEREMPTION_DATE_STOCK_LOT + " " + Constantes.TYPE_COL_PEREMPTION_DATE_STOCK_LOT + " ,"
                + Constantes.CLE_COL_QUARANTAINE_STOCK_LOT + " " + Constantes.TYPE_COL_QUARANTAINE_STOCK_LOT + ","
                + Constantes.CLE_COL__UID_STOCK_LOT + " " + Constantes.TYPE_COL__UID_STOCK_LOT
                + ");";
    }
}
