package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class Stock_Lot_EmplacementLightOpenHelper extends DBOpenHelper {

    public Stock_Lot_EmplacementLightOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableStock_Lot_Emplacements(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_STOCK_LOT_EMPLACEMENT, null, null);
    }

    public static long insererUnStock_Lot_EmplacementEnBDD(SQLiteDatabase db, Stock_Lot_Emplacement_Light stock_lot_emplacement) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.get_UID());
        contentValues.put(Constantes.CLE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getProduit_Code());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_ZONE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getZone());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getEmplacement());
        contentValues.put(Constantes.CLE_COL_LOT_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getLot());
        contentValues.put(Constantes.CLE_COL_QTE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getQte());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getPeremptionDate());
        contentValues.put(Constantes.CLE_COL_QTE_PREPARER_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getQte_Preparer());
        //contentValues.put(Constantes.CLE_COL_SERIE, stock_lot_emplacement.getSerie());
        contentValues.put(Constantes.CLE_COL_SERIE, "");

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_STOCK_LOT_EMPLACEMENT, null, contentValues);

        stock_lot_emplacement.setphiwms_mobileUUID((int) rowId);

        return rowId;
    }

    public static long mettreAJourUnStockLotEmplacement(SQLiteDatabase db, Stock_Lot_Emplacement_Light stock_lot_emplacement) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.get_UID());
        contentValues.put(Constantes.CLE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getProduit_Code());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_ZONE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getZone());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getEmplacement());
        contentValues.put(Constantes.CLE_COL_LOT_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getLot());
        contentValues.put(Constantes.CLE_COL_QTE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getQte());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getPeremptionDate());
        contentValues.put(Constantes.CLE_COL_QTE_PREPARER_STOCK_LOT_EMPLACEMENT, stock_lot_emplacement.getQte_Preparer());
        contentValues.put(Constantes.CLE_COL_SERIE, stock_lot_emplacement.getSerie());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, stock_lot_emplacement.getPhiMR4UUID());

        return db.update(Constantes.TABLE_STOCK_LOT_EMPLACEMENT, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + stock_lot_emplacement.getPhiMR4UUID(), null);
    }

    public static List<Stock_Lot_Emplacement_Light> getStockLotEmplacementByStock(SQLiteDatabase db, Stock stock) {
        List<Stock_Lot_Emplacement_Light> stockLotEmplacementLightList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_LOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT + "=? and " + Constantes.CLE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT + "=?", new String[]{stock.getDepot_Reference(), String.valueOf(stock.getProduit_UID())});
        while (cursor.moveToNext()) {
            Stock_Lot_Emplacement_Light stockLotEmplacementLight = new Stock_Lot_Emplacement_Light(cursor);
            if (stockLotEmplacementLight.getQte() > 0) {
                stockLotEmplacementLightList.add(stockLotEmplacementLight);
            }
        }
        cursor.close();
        cursor = null;
        return stockLotEmplacementLightList;
    }

    public static void supprimerUnStockLotEmplacement(SQLiteDatabase db, Stock_Lot_Emplacement_Light stockLotEmplacement) {
        db.delete(Constantes.TABLE_STOCK_LOT_EMPLACEMENT, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(stockLotEmplacement.getPhiMR4UUID())});
    }

    public static List<Stock_Lot_Emplacement_Light> getAllStock_Lot_EmplacementsByProduitIDEtCommandeNumero(SQLiteDatabase db, Produit produit, String critereCommandeNumero) {
        List<Stock_Lot_Emplacement_Light> stockLotEmplacementLightList = new ArrayList<>();

        //Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_LOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT + "=? and " + Constantes.CLE_COL_EMPLACEMENT_STOCK_LOT_EMPLACEMENT + " LIKE '%-" + String.valueOf(critereCommandeNumero) + "-%'", new String[]{String.valueOf(produit.getID_produit())});
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_LOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT + "=?", new String[]{String.valueOf(produit.getID_produit())});

        while (cursor.moveToNext()) {
            Stock_Lot_Emplacement_Light courant = new Stock_Lot_Emplacement_Light(cursor);
            if(courant.getEmplacement().contains(critereCommandeNumero))
            {
                stockLotEmplacementLightList.add(new Stock_Lot_Emplacement_Light(cursor));
            }
        }
        cursor.close();
        cursor = null;
        return stockLotEmplacementLightList;
    }

    public static List<Stock_Lot_Emplacement_Light> getAllStockLotEmplacementByProduitEtDepot(SQLiteDatabase db, Produit produit, Depot depot) {
        List<Stock_Lot_Emplacement_Light> stockLotEmplacementLightList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_LOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT + "=? and " + Constantes.CLE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT + "=?", new String[]{depot.getDepot_Reference(), String.valueOf(produit.getID_produit())});

        while (cursor.moveToNext()) {
            stockLotEmplacementLightList.add(new Stock_Lot_Emplacement_Light(cursor));
        }
        cursor.close();
        cursor = null;
        return stockLotEmplacementLightList;
    }

    public static Stock_Lot_Emplacement_Light getPremierStockLotEmplacementByProduitEtDepot(SQLiteDatabase db, Produit produit, Depot depot)
    {
        Stock_Lot_Emplacement_Light stockLotEmplacementLightList = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_LOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT + "=? and " + Constantes.CLE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT + "=?", new String[]{depot.getDepot_Reference(), String.valueOf(produit.getID_produit())});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            stockLotEmplacementLightList = new Stock_Lot_Emplacement_Light(cursor);
        }
        cursor.close();
        cursor = null;
        return stockLotEmplacementLightList;
    }

    public static Stock_Lot_Emplacement_Light getStockLotEmplacementByProduitLotSerieEtDepot(SQLiteDatabase db, Produit produit, Depot depot, String numLot, String numSerie)
    {
        Stock_Lot_Emplacement_Light stockLotEmplacementLightList = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_LOT_EMPLACEMENT + " WHERE " + Constantes.CLE_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT + "=? and " + Constantes.CLE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT + "=? and "+Constantes.CLE_COL_LOT_STOCK_LOT_EMPLACEMENT+" =? and "+Constantes.CLE_COL_SERIE+"=?", new String[]{depot.getDepot_Reference(), String.valueOf(produit.getID_produit()), numLot, numSerie});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            stockLotEmplacementLightList = new Stock_Lot_Emplacement_Light(cursor);
        }
        cursor.close();
        cursor = null;
        return stockLotEmplacementLightList;
    }

    public static Stock_Lot_Emplacement_Light getStock_Lot_EmplacementByID(SQLiteDatabase db, int id) {
        Stock_Lot_Emplacement_Light stockLotEmplacementLight = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_LOT_EMPLACEMENT + " WHERE  " + Constantes.CLE_COL__UID_STOCK_LOT_EMPLACEMENT + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            stockLotEmplacementLight = new Stock_Lot_Emplacement_Light(cursor);
        }
        cursor.close();
        cursor = null;

        return stockLotEmplacementLight;
    }

    public static Stock_Lot_Emplacement_Light getStock_Lot_EmplacementByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        Stock_Lot_Emplacement_Light stockLotEmplacementLight = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK_LOT_EMPLACEMENT + " WHERE  " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            stockLotEmplacementLight = new Stock_Lot_Emplacement_Light(cursor);
        }
        cursor.close();
        cursor = null;
        return stockLotEmplacementLight;
    }

    public static class Constantes implements BaseColumns {

        public static final String TABLE_STOCK_LOT_EMPLACEMENT = "Stock_lot_emplacement";

        public static final String CLE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT = "Produit_Code";
        public static final int NUM_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT = 1;
        public static final String TYPE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT = "INTEGER";
        public static final String CLE_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT = "Depot_Reference";
        public static final int NUM_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT = 2;
        public static final String TYPE_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT = "TEXT";
        public static final String CLE_COL_ZONE_STOCK_LOT_EMPLACEMENT = "Zone";
        public static final int NUM_COL_ZONE_STOCK_LOT_EMPLACEMENT = 3;
        public static final String TYPE_COL_ZONE_STOCK_LOT_EMPLACEMENT = "TEXT";
        public static final String CLE_COL_EMPLACEMENT_STOCK_LOT_EMPLACEMENT = "Emplacement";
        public static final int NUM_COL_EMPLACEMENT_STOCK_LOT_EMPLACEMENT = 4;
        public static final String TYPE_COL_EMPLACEMENT_STOCK_LOT_EMPLACEMENT = "TEXT";
        public static final String CLE_COL_LOT_STOCK_LOT_EMPLACEMENT = "Lot";
        public static final int NUM_COL_LOT_STOCK_LOT_EMPLACEMENT = 5;
        public static final String TYPE_COL_LOT_STOCK_LOT_EMPLACEMENT = "TEXT";
        public static final String CLE_COL_QTE_STOCK_LOT_EMPLACEMENT = "Qte";
        public static final int NUM_COL_QTE_STOCK_LOT_EMPLACEMENT = 6;
        public static final String TYPE_COL_QTE_STOCK_LOT_EMPLACEMENT = "REAL";
        public static final String CLE_COL_PEREMPTIONDATE_STOCK_LOT_EMPLACEMENT = "peremptionDate";
        public static final int NUM_COL_PEREMPTIONDATE_STOCK_LOT_EMPLACEMENT = 7;
        public static final String TYPE_COL_PEREMPTIONDATE_STOCK_LOT_EMPLACEMENT = "TEXT";
        public static final String CLE_COL_QTE_PREPARER_STOCK_LOT_EMPLACEMENT = "Qte_Preparer";
        public static final int NUM_COL_QTE_PREPARER_STOCK_LOT_EMPLACEMENT = 8;
        public static final String TYPE_COL_QTE_PREPARER_STOCK_LOT_EMPLACEMENT = "INTEGER";
        public static final String CLE_COL__UID_STOCK_LOT_EMPLACEMENT = "_UID";
        public static final int NUM_COL__UID_STOCK_LOT_EMPLACEMENT = 9;
        public static final String TYPE_COL__UID_STOCK_LOT_EMPLACEMENT = "INTEGER";
        public static final String CLE_COL_SERIE = "Serie";
        public static final int NUM_COL_SERIE = 10;
        public static final String TYPE_COL_SERIE = "TEXT";



        public static final String CREATION_TABLE_STOCK_LOT_EMPLACEMENT = "CREATE TABLE " + Constantes.TABLE_STOCK_LOT_EMPLACEMENT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT + " " + Constantes.TYPE_COL_PRODUIT_CODE_STOCK_LOT_EMPLACEMENT + " ,"
                + Constantes.CLE_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT + " " + Constantes.TYPE_COL_DEPOT_REFERENCE_STOCK_LOT_EMPLACEMENT + " ,"
                + Constantes.CLE_COL_ZONE_STOCK_LOT_EMPLACEMENT + " " + Constantes.TYPE_COL_ZONE_STOCK_LOT_EMPLACEMENT + " ,"
                + Constantes.CLE_COL_EMPLACEMENT_STOCK_LOT_EMPLACEMENT + " " + Constantes.TYPE_COL_EMPLACEMENT_STOCK_LOT_EMPLACEMENT + " ,"
                + Constantes.CLE_COL_LOT_STOCK_LOT_EMPLACEMENT + " " + Constantes.TYPE_COL_LOT_STOCK_LOT_EMPLACEMENT + " ,"
                + Constantes.CLE_COL_QTE_STOCK_LOT_EMPLACEMENT + " " + Constantes.TYPE_COL_QTE_STOCK_LOT_EMPLACEMENT + " ,"
                + Constantes.CLE_COL_PEREMPTIONDATE_STOCK_LOT_EMPLACEMENT + " " + Constantes.TYPE_COL_PEREMPTIONDATE_STOCK_LOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_QTE_PREPARER_STOCK_LOT_EMPLACEMENT + " " + Constantes.TYPE_COL_QTE_PREPARER_STOCK_LOT_EMPLACEMENT + ","
                + Constantes.CLE_COL__UID_STOCK_LOT_EMPLACEMENT + " " + Constantes.TYPE_COL__UID_STOCK_LOT_EMPLACEMENT + ","
                + Constantes.CLE_COL_SERIE + " " + Constantes.TYPE_COL_SERIE
                + ");";
    }
}
