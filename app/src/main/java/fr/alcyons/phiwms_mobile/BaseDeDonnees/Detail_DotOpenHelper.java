package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Detail_Dot;
import fr.alcyons.phiwms_mobile.Classes.Dotation;

/**
 * Created by jessica on 02/10/2017.
 */

public class Detail_DotOpenHelper extends DBOpenHelper {
    public Detail_DotOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableDetail_Dot(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_DETAIL_DOT, null, null);
    }

    public static void supprimerUnDetailDot(SQLiteDatabase db, int detailDotUId) {
        db.delete(Constantes.TABLE_DETAIL_DOT, Constantes.CLE_COL__UID_DETAIL_DOT + "=?", new String[]{String.valueOf(detailDotUId)});
    }

    public static long insererDetail_DotEnBDD(SQLiteDatabase db, Detail_Dot objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_DOTATION_UID_DETAIL_DOT, objet.getDotation_UID());
        contentValues.put(Constantes.CLE_COL_CODE_PRODUIT_DETAIL_DOT, objet.getCode_produit());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_DETAIL_DOT, objet.getDesignation());
        contentValues.put(Constantes.CLE_COL_COND_DETAIL_DOT, objet.getCond());
        contentValues.put(Constantes.CLE_COL_QTE_DETAIL_DOT, objet.getQte());
        contentValues.put(Constantes.CLE_COL_REF_FOUR_DETAIL_DOT, objet.getRef_four());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_DETAIL_DOT, objet.getCategorie());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_DIRECTE_DETAIL_DOT, objet.isLivraison_Directe());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_DETAIL_DOT, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_DETAIL_DOT, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_DETAIL_DOT, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL__UID_DETAIL_DOT, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_VALEUR_TTC_DETAIL_DOT, objet.getValeur_TTC());
        contentValues.put(Constantes.CLE_COL_STOCK_MINIMUM_DETAIL_DOT, objet.getStock_minimum());
        contentValues.put(Constantes.CLE_COL_PLEINVIDE_ADRESSAGE_DETAIL_DOT, objet.getPleinVide_Adressage());

        long rowID = db.insert(Constantes.TABLE_DETAIL_DOT, null, contentValues);
        objet.setPhiMR4UUID((int) rowID);
        return rowID;
    }

    public static Detail_Dot getDetailDotById(SQLiteDatabase db, int id) {
        Detail_Dot detailDot = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_DETAIL_DOT + " WHERE " + Constantes.CLE_COL__UID_DETAIL_DOT + " =? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            detailDot = new Detail_Dot(cursor);
        }
        cursor.close();
        cursor = null;
        return detailDot;
    }

    public static Detail_Dot getDetailDotByProduitAndDotation(SQLiteDatabase db, int produitCode, int dotationUID) {
        Detail_Dot detailDot = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_DETAIL_DOT + " WHERE " + Constantes.CLE_COL_CODE_PRODUIT_DETAIL_DOT + " =? AND "+ Constantes.CLE_COL_DOTATION_UID_DETAIL_DOT +"= ?", new String[]{String.valueOf(produitCode), String.valueOf(dotationUID)});
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            detailDot = new Detail_Dot(cursor);
        }
        cursor.close();
        cursor = null;
        return detailDot;
    }

    public static List<Detail_Dot> getAllDetailDotParDotation(SQLiteDatabase db, Dotation dotation) {
        List<Detail_Dot> detailDotList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DETAIL_DOT + " WHERE " + Constantes.CLE_COL_DOTATION_UID_DETAIL_DOT + "=? ORDER BY "+Constantes.CLE_COL_DESIGNATION_DETAIL_DOT, new String[]{String.valueOf(dotation.get_UID())});

        while (cursor.moveToNext()) {
            detailDotList.add(new Detail_Dot(cursor));
        }

        cursor.close();
        cursor = null;
        return detailDotList;
    }

    public static int getNbDetailDot(SQLiteDatabase db, Dotation dotation) {
        int nbDetail = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DETAIL_DOT + " WHERE " + Constantes.CLE_COL_DOTATION_UID_DETAIL_DOT + "=?", new String[]{String.valueOf(dotation.get_UID())});

        if(cursor != null)
        {
            nbDetail = cursor.getCount();
        }
        cursor.close();
        cursor = null;
        return nbDetail;
    }


    public static Detail_Dot getDetailDotByDotationPleinVideAdressage(SQLiteDatabase db, Dotation dotation, String pleinVideAdressage) {
        Detail_Dot detailDot = null;
        int dotationUID = dotation.get_UID();

        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_DETAIL_DOT + " WHERE " + Constantes.CLE_COL_DOTATION_UID_DETAIL_DOT + " =? AND " + Constantes.CLE_COL_PLEINVIDE_ADRESSAGE_DETAIL_DOT + " =? ", new String[]{String.valueOf(dotationUID), pleinVideAdressage});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            detailDot = new Detail_Dot(cursor);
        }
        cursor.close();
        cursor = null;
        return detailDot;
    }

    public static Detail_Dot getDetailDotPleinVideAdressage(SQLiteDatabase db, String pleinVideAdressage) {
        Detail_Dot detailDot = null;

        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_DETAIL_DOT + " WHERE " + Constantes.CLE_COL_PLEINVIDE_ADRESSAGE_DETAIL_DOT + " =? ", new String[]{pleinVideAdressage});
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            detailDot = new Detail_Dot(cursor);
        }
        cursor.close();
        cursor = null;
        return detailDot;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_DETAIL_DOT = "Detail_Dot";
        public static final String CLE_COL_DOTATION_UID_DETAIL_DOT = "dotation_UID";
        public static final int NUM_COL_DOTATION_UID_DETAIL_DOT = 1;
        public static final String TYPE_COL_DOTATION_UID_DETAIL_DOT = "INTEGER";
        public static final String CLE_COL_CODE_PRODUIT_DETAIL_DOT = "Code_produit";
        public static final int NUM_COL_CODE_PRODUIT_DETAIL_DOT = 2;
        public static final String TYPE_COL_CODE_PRODUIT_DETAIL_DOT = "INTEGER";
        public static final String CLE_COL_DESIGNATION_DETAIL_DOT = "Designation";
        public static final int NUM_COL_DESIGNATION_DETAIL_DOT = 3;
        public static final String TYPE_COL_DESIGNATION_DETAIL_DOT = "TEXT";
        public static final String CLE_COL_COND_DETAIL_DOT = "Cond";
        public static final int NUM_COL_COND_DETAIL_DOT = 4;
        public static final String TYPE_COL_COND_DETAIL_DOT = "INTEGER";
        public static final String CLE_COL_QTE_DETAIL_DOT = "Qte";
        public static final int NUM_COL_QTE_DETAIL_DOT = 5;
        public static final String TYPE_COL_QTE_DETAIL_DOT = "INTEGER";
        public static final String CLE_COL_REF_FOUR_DETAIL_DOT = "Ref_four";
        public static final int NUM_COL_REF_FOUR_DETAIL_DOT = 6;
        public static final String TYPE_COL_REF_FOUR_DETAIL_DOT = "TEXT";
        public static final String CLE_COL_CATEGORIE_DETAIL_DOT = "Categorie";
        public static final int NUM_COL_CATEGORIE_DETAIL_DOT = 7;
        public static final String TYPE_COL_CATEGORIE_DETAIL_DOT = "TEXT";
        public static final String CLE_COL_LIVRAISON_DIRECTE_DETAIL_DOT = "Livraison_Directe";
        public static final int NUM_COL_LIVRAISON_DIRECTE_DETAIL_DOT = 8;
        public static final String TYPE_COL_LIVRAISON_DIRECTE_DETAIL_DOT = "INTEGER";
        public static final String CLE_COL_SYS_DT_MAJ_DETAIL_DOT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_DETAIL_DOT = 9;
        public static final String TYPE_COL_SYS_DT_MAJ_DETAIL_DOT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_DETAIL_DOT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_DETAIL_DOT = 10;
        public static final String TYPE_COL_SYS_HEURE_MAJ_DETAIL_DOT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_DETAIL_DOT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_DETAIL_DOT = 11;
        public static final String TYPE_COL_SYS_USER_MAJ_DETAIL_DOT = "TEXT";
        public static final String CLE_COL__UID_DETAIL_DOT = "_UID";
        public static final int NUM_COL__UID_DETAIL_DOT = 12;
        public static final String TYPE_COL__UID_DETAIL_DOT = "INTEGER";
        public static final String CLE_COL_VALEUR_TTC_DETAIL_DOT = "Valeur_TTC";
        public static final int NUM_COL_VALEUR_TTC_DETAIL_DOT = 13;
        public static final String TYPE_COL_VALEUR_TTC_DETAIL_DOT = "INTEGER";
        public static final String CLE_COL_STOCK_MINIMUM_DETAIL_DOT = "Stock_minimum";
        public static final int NUM_COL_STOCK_MINIMUM_DETAIL_DOT = 14;
        public static final String TYPE_COL_STOCK_MINIMUM_DETAIL_DOT = "INTEGER";

        public static final String CLE_COL_PLEINVIDE_ADRESSAGE_DETAIL_DOT = "PleinVide_Adressage";
        public static final int NUM_COL_PLEINVIDE_ADRESSAGE_DETAIL_DOT = 15;
        public static final String TYPE_COL_PLEINVIDE_ADRESSAGE_DETAIL_DOT = "TEXT";

        public static final String CREATION_TABLE_DETAIL_DOT = " CREATE TABLE       " + Constantes.TABLE_DETAIL_DOT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY, "
                + Constantes.CLE_COL_DOTATION_UID_DETAIL_DOT + " " + Constantes.TYPE_COL_DOTATION_UID_DETAIL_DOT + " , "
                + Constantes.CLE_COL_CODE_PRODUIT_DETAIL_DOT + " " + Constantes.TYPE_COL_CODE_PRODUIT_DETAIL_DOT + " , "
                + Constantes.CLE_COL_DESIGNATION_DETAIL_DOT + " " + Constantes.TYPE_COL_DESIGNATION_DETAIL_DOT + " , "
                + Constantes.CLE_COL_COND_DETAIL_DOT + " " + Constantes.TYPE_COL_COND_DETAIL_DOT + " , "
                + Constantes.CLE_COL_QTE_DETAIL_DOT + " " + Constantes.TYPE_COL_QTE_DETAIL_DOT + " , "
                + Constantes.CLE_COL_REF_FOUR_DETAIL_DOT + " " + Constantes.TYPE_COL_REF_FOUR_DETAIL_DOT + " , "
                + Constantes.CLE_COL_CATEGORIE_DETAIL_DOT + " " + Constantes.TYPE_COL_CATEGORIE_DETAIL_DOT + " , "
                + Constantes.CLE_COL_LIVRAISON_DIRECTE_DETAIL_DOT + " " + Constantes.TYPE_COL_LIVRAISON_DIRECTE_DETAIL_DOT + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_DETAIL_DOT + " " + Constantes.TYPE_COL_SYS_DT_MAJ_DETAIL_DOT + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_DETAIL_DOT + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_DETAIL_DOT + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_DETAIL_DOT + " " + Constantes.TYPE_COL_SYS_USER_MAJ_DETAIL_DOT + " , "
                + Constantes.CLE_COL__UID_DETAIL_DOT + " " + Constantes.TYPE_COL__UID_DETAIL_DOT + " , "
                + Constantes.CLE_COL_VALEUR_TTC_DETAIL_DOT + " " + Constantes.TYPE_COL_VALEUR_TTC_DETAIL_DOT + " , "
                + Constantes.CLE_COL_STOCK_MINIMUM_DETAIL_DOT + " " + Constantes.TYPE_COL_STOCK_MINIMUM_DETAIL_DOT + " , "
                + Constantes.CLE_COL_PLEINVIDE_ADRESSAGE_DETAIL_DOT + " " + Constantes.TYPE_COL_PLEINVIDE_ADRESSAGE_DETAIL_DOT
                + " ); ";

    }
}
