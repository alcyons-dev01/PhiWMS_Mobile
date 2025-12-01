package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.StockUtilises;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;

public class StockUtilisesOpenHelper extends DBOpenHelper {
    public StockUtilisesOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableStockUtiliser(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_STOCK_UTILISE, null, null);
    }

    public static StockUtilises getStockUtiliserByPhiWMSUUID(SQLiteDatabase db, int phiwms_mobileuuid) {
        StockUtilises stockUtilises = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_UTILISE + " WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(phiwms_mobileuuid)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            stockUtilises = new StockUtilises(cursor);
        }

        cursor.close();
        cursor = null;
        return stockUtilises;
    }

    public static StockUtilises getStockUtiliserByStockIdAndUser(SQLiteDatabase db, int stockid, int userid) {
        StockUtilises stockUtilises = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_UTILISE + " WHERE " + Constantes.CLE_COL_ID_STOCK + "=? AND "+ Constantes.CLE_COL_USER_ID +"=?", new String[]{String.valueOf(stockid), String.valueOf(userid)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            stockUtilises = new StockUtilises(cursor);
        }

        cursor.close();
        cursor = null;
        return stockUtilises;
    }

    public static List<StockUtilises> getStockUtiliserByNotUser(SQLiteDatabase db, int userid) {
        List<StockUtilises> stockUtilisesList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_UTILISE + " WHERE " + Constantes.CLE_COL_USER_ID +"!=?", new String[]{String.valueOf(userid)});

        while (cursor.moveToNext()) {
            StockUtilises stockUtilises = new StockUtilises(cursor);
            stockUtilisesList.add(stockUtilises);
        }

        cursor.close();
        cursor = null;
        return stockUtilisesList;
    }

    public static void supprimerUnStockUtilise(SQLiteDatabase db, StockUtilises stockUtilises) {
        db.delete(Constantes.TABLE_STOCK_UTILISE, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(stockUtilises.getphiwms_mobileUUID())});
    }

    public static long insererUnStockUtilisesEnBDD(SQLiteDatabase db, StockUtilises stockUtilises) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_NUMERO_DOCUMENT, stockUtilises.getNumeroDocument());
        contentValues.put(Constantes.CLE_COL_ID_PRODUIT, stockUtilises.getIdProduit());
        contentValues.put(Constantes.CLE_COL_ID_STOCK, stockUtilises.getStockId());
        contentValues.put(Constantes.CLE_COL_NUM_LOT, stockUtilises.getNumeroLot());
        contentValues.put(Constantes.CLE_COL_DATE_PEREMPTION, stockUtilises.getDatePeremption());
        contentValues.put(Constantes.CLE_COL_DEPOT_ID, stockUtilises.getDepotId());
        contentValues.put(Constantes.CLE_COL_ZONE, stockUtilises.getZone());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT, stockUtilises.getEmplacement());
        contentValues.put(Constantes.CLE_COL_QUANTITE, stockUtilises.getQuantite());
        contentValues.put(Constantes.CLE_COL_USER_ID, stockUtilises.getUserId());
        contentValues.put(Constantes.CLE_COL_DATE_CREATION, stockUtilises.getDateCreation());
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_ID, stockUtilises.getEtablissementID());
        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_STOCK_UTILISE, null, contentValues);

        stockUtilises.setphiwms_mobileUUID((int) rowId);

        return rowId;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_STOCK_UTILISE = "StockUtilises";

        public static final String CLE_COL_NUMERO_DOCUMENT = "numeroDocument";
        public static final int NUM_COL_NUMERO_DOCUMENT = 1;
        public static final String TYPE_COL_NUMERO_DOCUMENT = "TEXT";

        public static final String CLE_COL_ID_PRODUIT = "idProduit";
        public static final int NUM_COL_ID_PRODUIT = 2;
        public static final String TYPE_COL_ID_PRODUIT = "INTEGER";

        public static final String CLE_COL_ID_STOCK = "idStock";
        public static final int NUM_COL_ID_STOCK = 3;
        public static final String TYPE_COL_ID_STOCK = "INTEGER";

        public static final String CLE_COL_NUM_LOT = "NumeroLot";
        public static final int NUM_COL_NUM_LOT = 4;
        public static final String TYPE_COL_NUM_LOT = "TEXT";

        public static final String CLE_COL_DATE_PEREMPTION = "DatePeremption";
        public static final int NUM_COL_DATE_PEREMPTION = 5;
        public static final String TYPE_COL_DATE_PEREMPTION = "TEXT";

        public static final String CLE_COL_DEPOT_ID = "DepotId";
        public static final int NUM_COL_DEPOT_ID = 6;
        public static final String TYPE_COL_DEPOT_ID = "INTEGER";

        public static final String CLE_COL_ZONE = "Zone";
        public static final int NUM_COL_ZONE = 7;
        public static final String TYPE_COL_ZONE = "TEXT";

        public static final String CLE_COL_EMPLACEMENT = "Emplacement";
        public static final int NUM_COL_EMPLACEMENT = 8;
        public static final String TYPE_COL_EMPLACEMENT = "TEXT";

        public static final String CLE_COL_QUANTITE = "Quantite";
        public static final int NUM_COL_QUANTITE = 9;
        public static final String TYPE_COL_QUANTITE = "INTEGER";

        public static final String CLE_COL_USER_ID = "UserId";
        public static final int NUM_COL_USER_ID = 10;
        public static final String TYPE_COL_USER_ID = "INTEGER";

        public static final String CLE_COL_DATE_CREATION = "DateCreation";
        public static final int NUM_COL_DATE_CREATION = 11;
        public static final String TYPE_COL_DATE_CREATION = "TEXT";

        public static final String CLE_COL_ETABLISSEMENT_ID = "Etablissement_ID";
        public static final int NUM_COL_ETABLISSEMENT_ID = 12;
        public static final String TYPE_COL_ETABLISSEMENT_ID = "INTEGER";
        public static final String CREATION_TABLE_STOCK_UTILISE = "CREATE TABLE "
                + Constantes.TABLE_STOCK_UTILISE + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_NUMERO_DOCUMENT + " " + Constantes.TYPE_COL_NUMERO_DOCUMENT + ","
                + Constantes.CLE_COL_ID_PRODUIT + " " + Constantes.TYPE_COL_ID_PRODUIT + ","
                + Constantes.CLE_COL_ID_STOCK + " " + Constantes.TYPE_COL_ID_STOCK + ","
                + Constantes.CLE_COL_NUM_LOT + " " + Constantes.TYPE_COL_NUM_LOT + ","
                + Constantes.CLE_COL_DATE_PEREMPTION + " " + Constantes.TYPE_COL_DATE_PEREMPTION + ","
                + Constantes.CLE_COL_DEPOT_ID + " " + Constantes.TYPE_COL_DEPOT_ID+ ","
                + Constantes.CLE_COL_ZONE + " " + Constantes.TYPE_COL_ZONE+ ","
                + Constantes.CLE_COL_EMPLACEMENT + " " + Constantes.TYPE_COL_EMPLACEMENT+ ","
                + Constantes.CLE_COL_QUANTITE + " " + Constantes.TYPE_COL_QUANTITE+ ","
                + Constantes.CLE_COL_USER_ID + " " + Constantes.TYPE_COL_USER_ID+ ","
                + Constantes.CLE_COL_DATE_CREATION + " " + Constantes.TYPE_COL_DATE_CREATION+ ","
                + Constantes.CLE_COL_ETABLISSEMENT_ID + " " + Constantes.TYPE_COL_ETABLISSEMENT_ID
                + ");";
    }

}
