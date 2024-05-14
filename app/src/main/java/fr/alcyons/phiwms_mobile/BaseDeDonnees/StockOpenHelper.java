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

public class StockOpenHelper extends DBOpenHelper {

    public StockOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public static void viderTableStocks(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_STOCK, null, null);
    }


    public static long insererUnStockEnBDD(SQLiteDatabase db, Stock stock) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_PRODUIT_UID_STOCK, stock.getProduit_UID());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_STOCK, stock.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_QUANTITE_ACTUELLE_STOCK, stock.getQuantite_Actuelle());
        contentValues.put(Constantes.CLE_COL_PRODUIT_REFERENCE_STOCK, stock.getProduit_Reference());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_STOCK, stock.getDesignation());
        contentValues.put(Constantes.CLE_COL_FOURNISSEUR_STOCK, stock.getFournisseur());
        contentValues.put(Constantes.CLE_COL__UID_STOCK, stock.get_UID());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_STOCK, null, contentValues);

        stock.setphiwms_mobileUUID((int) rowId);

        return rowId;
    }

    public static List<Stock> getStockByDepot(SQLiteDatabase db, Depot depot) {
        List<Stock> stockList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK + " WHERE " + Constantes.CLE_COL_DEPOT_REFERENCE_STOCK + "=?", new String[]{depot.getDepot_Reference()});

        while (cursor.moveToNext()) {
            Stock stockCourant = new Stock(cursor);
            if(!stockCourant.getDesignation().contentEquals(""))
            {
                stockList.add(stockCourant);
            }
        }

        cursor.close();
        cursor = null;
        return stockList;
    }

    public static List<Stock> getStockByProduit(SQLiteDatabase db, Produit produit) {
        List<Stock> stockList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK + " WHERE " + Constantes.CLE_COL_PRODUIT_UID_STOCK + "=?", new String[]{String.valueOf(produit.getID_produit())});

        while (cursor.moveToNext()) {
            Stock stockCourant = new Stock(cursor);
            if(!stockCourant.getDesignation().contentEquals(""))
            {
                stockList.add(stockCourant);
            }
        }

        cursor.close();
        cursor = null;
        return stockList;
    }

    public static Stock getStockByProduitEtDepot(SQLiteDatabase db, Produit produit, Depot depot) {
        Stock stock = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_STOCK + " WHERE " + Constantes.CLE_COL_PRODUIT_UID_STOCK + "=? and " + Constantes.CLE_COL_DEPOT_REFERENCE_STOCK + "=?", new String[]{String.valueOf(produit.getID_produit()), depot.getDepot_Reference()});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            stock = new Stock(cursor);
        }
        cursor.close();
        cursor = null;

        return stock;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_STOCK = "Stock";

        public static final String CLE_COL_DEPOT_REFERENCE_STOCK = "Depot_Reference";
        public static final int NUM_COL_DEPOT_REFERENCE_STOCK = 1;
        public static final String TYPE_COL_DEPOT_REFERENCE_STOCK = "TEXT";
        public static final String CLE_COL_STO_DT_INVENTAIRE_STOCK = "STO_DT_INVENTAIRE";
        public static final int NUM_COL_STO_DT_INVENTAIRE_STOCK = 2;
        public static final String TYPE_COL_STO_DT_INVENTAIRE_STOCK = "TEXT";
        public static final String CLE_COL_STO_QTE_INVENT_STOCK = "STO_QTE_INVENT";
        public static final int NUM_COL_STO_QTE_INVENT_STOCK = 3;
        public static final String TYPE_COL_STO_QTE_INVENT_STOCK = "REAL";
        public static final String CLE_COL_STO_DT_DER_ENTREE_STOCK = "STO_DT_DER_ENTREE";
        public static final int NUM_COL_STO_DT_DER_ENTREE_STOCK = 4;
        public static final String TYPE_COL_STO_DT_DER_ENTREE_STOCK = "TEXT";
        public static final String CLE_COL_STO_QTE_ENTREE_STOCK = "STO_QTE_ENTREE";
        public static final int NUM_COL_STO_QTE_ENTREE_STOCK = 5;
        public static final String TYPE_COL_STO_QTE_ENTREE_STOCK = "REAL";
        public static final String CLE_COL_STO_DT_DER_SORTIE_STOCK = "STO_DT_DER_SORTIE";
        public static final int NUM_COL_STO_DT_DER_SORTIE_STOCK = 6;
        public static final String TYPE_COL_STO_DT_DER_SORTIE_STOCK = "TEXT";
        public static final String CLE_COL_STO_QTE_SORTIE_STOCK = "STO_QTE_SORTIE";
        public static final int NUM_COL_STO_QTE_SORTIE_STOCK = 7;
        public static final String TYPE_COL_STO_QTE_SORTIE_STOCK = "REAL";
        public static final String CLE_COL_STO_QTE_AVANT_INVENT_STOCK = "STO_QTE_AVANT_INVENT";
        public static final int NUM_COL_STO_QTE_AVANT_INVENT_STOCK = 8;
        public static final String TYPE_COL_STO_QTE_AVANT_INVENT_STOCK = "REAL";
        public static final String CLE_COL_STO_QTE_ATTENDUE_STOCK = "STO_QTE_ATTENDUE";
        public static final int NUM_COL_STO_QTE_ATTENDUE_STOCK = 9;
        public static final String TYPE_COL_STO_QTE_ATTENDUE_STOCK = "REAL";
        public static final String CLE_COL_QUANTITE_ACTUELLE_STOCK = "Quantite_Actuelle";
        public static final int NUM_COL_QUANTITE_ACTUELLE_STOCK = 10;
        public static final String TYPE_COL_QUANTITE_ACTUELLE_STOCK = "REAL";
        public static final String CLE_COL_STO_PRIX_STOCK = "STO_PRIX";
        public static final int NUM_COL_STO_PRIX_STOCK = 11;
        public static final String TYPE_COL_STO_PRIX_STOCK = "REAL";
        public static final String CLE_COL_STO_VAL_ENTREES_STOCK = "STO_VAL_ENTREES";
        public static final int NUM_COL_STO_VAL_ENTREES_STOCK = 12;
        public static final String TYPE_COL_STO_VAL_ENTREES_STOCK = "REAL";
        public static final String CLE_COL_STO_VAL_SORTIES_STOCK = "STO_VAL_SORTIES";
        public static final int NUM_COL_STO_VAL_SORTIES_STOCK = 13;
        public static final String TYPE_COL_STO_VAL_SORTIES_STOCK = "REAL";
        public static final String CLE_COL_STO_VAL_INVENT_STOCK = "STO_VAL_INVENT";
        public static final int NUM_COL_STO_VAL_INVENT_STOCK = 14;
        public static final String TYPE_COL_STO_VAL_INVENT_STOCK = "REAL";
        public static final String CLE_COL_STO_JOURS_ROTATION_STOCK = "STO_JOURS_ROTATION";
        public static final int NUM_COL_STO_JOURS_ROTATION_STOCK = 15;
        public static final String TYPE_COL_STO_JOURS_ROTATION_STOCK = "INTEGER";
        public static final String CLE_COL_STO_DT_CREAT_STOCK = "STO_DT_CREAT";
        public static final int NUM_COL_STO_DT_CREAT_STOCK = 16;
        public static final String TYPE_COL_STO_DT_CREAT_STOCK = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_STOCK = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_STOCK = 17;
        public static final String TYPE_COL_SYS_DT_MAJ_STOCK = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_STOCK = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_STOCK = 18;
        public static final String TYPE_COL_SYS_USER_MAJ_STOCK = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_STOCK = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_STOCK = 19;
        public static final String TYPE_COL_SYS_HEURE_MAJ_STOCK = "TEXT";
        public static final String CLE_COL_TVA_STOCK = "TVA";
        public static final int NUM_COL_TVA_STOCK = 20;
        public static final String TYPE_COL_TVA_STOCK = "REAL";
        public static final String CLE_COL_CATEGORIE_STOCK = "Categorie";
        public static final int NUM_COL_CATEGORIE_STOCK = 21;
        public static final String TYPE_COL_CATEGORIE_STOCK = "TEXT";
        public static final String CLE_COL_PRODUIT_REFERENCE_STOCK = "produit_Reference";
        public static final int NUM_COL_PRODUIT_REFERENCE_STOCK = 22;
        public static final String TYPE_COL_PRODUIT_REFERENCE_STOCK = "TEXT";
        public static final String CLE_COL_DESIGNATION_STOCK = "Designation";
        public static final int NUM_COL_DESIGNATION_STOCK = 23;
        public static final String TYPE_COL_DESIGNATION_STOCK = "TEXT";
        public static final String CLE_COL_ARRET_DISTRIBUTION_STOCK = "Arret_distribution";
        public static final int NUM_COL_ARRET_DISTRIBUTION_STOCK = 24;
        public static final String TYPE_COL_ARRET_DISTRIBUTION_STOCK = "INTEGER";
        public static final String CLE_COL_VALEUR_HT_STOCK = "Valeur_HT";
        public static final int NUM_COL_VALEUR_HT_STOCK = 25;
        public static final String TYPE_COL_VALEUR_HT_STOCK = "REAL";
        public static final String CLE_COL_VALEUR_TTC_STOCK = "Valeur_TTC";
        public static final int NUM_COL_VALEUR_TTC_STOCK = 26;
        public static final String TYPE_COL_VALEUR_TTC_STOCK = "REAL";
        public static final String CLE_COL_ZONE_STOCKAGE_STOCK = "ZONE_STOCKAGE";
        public static final int NUM_COL_ZONE_STOCKAGE_STOCK = 27;
        public static final String TYPE_COL_ZONE_STOCKAGE_STOCK = "TEXT";
        public static final String CLE_COL_FOURNISSEUR_STOCK = "Fournisseur";
        public static final int NUM_COL_FOURNISSEUR_STOCK = 28;
        public static final String TYPE_COL_FOURNISSEUR_STOCK = "TEXT";
        public static final String CLE_COL_LIVRAISON_DIRECTE_STOCK = "Livraison_Directe";
        public static final int NUM_COL_LIVRAISON_DIRECTE_STOCK = 29;
        public static final String TYPE_COL_LIVRAISON_DIRECTE_STOCK = "INTEGER";
        public static final String CLE_COL_PUMP_HT_DERNIERE_CLOTURE_STOCK = "PUMP_HT_Derniere_cloture";
        public static final int NUM_COL_PUMP_HT_DERNIERE_CLOTURE_STOCK = 30;
        public static final String TYPE_COL_PUMP_HT_DERNIERE_CLOTURE_STOCK = "REAL";
        public static final String CLE_COL_VALEUR_PUMP_HT_STOCK = "Valeur_PUMP_HT";
        public static final int NUM_COL_VALEUR_PUMP_HT_STOCK = 31;
        public static final String TYPE_COL_VALEUR_PUMP_HT_STOCK = "REAL";
        public static final String CLE_COL_VALEUR_PUMP_TTC_STOCK = "Valeur_PUMP_TTC";
        public static final int NUM_COL_VALEUR_PUMP_TTC_STOCK = 32;
        public static final String TYPE_COL_VALEUR_PUMP_TTC_STOCK = "REAL";
        public static final String CLE_COL_PUMP_TTC_DERNIERE_CLOTURE_STOCK = "PUMP_TTC_Derniere_cloture";
        public static final int NUM_COL_PUMP_TTC_DERNIERE_CLOTURE_STOCK = 33;
        public static final String TYPE_COL_PUMP_TTC_DERNIERE_CLOTURE_STOCK = "REAL";
        public static final String CLE_COL_CLASSIFICATION_STOCK = "Classification";
        public static final int NUM_COL_CLASSIFICATION_STOCK = 34;
        public static final String TYPE_COL_CLASSIFICATION_STOCK = "INTEGER";
        public static final String CLE_COL_INVENTAIRE_FIN_DE_MOIS_STOCK = "Inventaire_Fin_de_Mois";
        public static final int NUM_COL_INVENTAIRE_FIN_DE_MOIS_STOCK = 35;
        public static final String TYPE_COL_INVENTAIRE_FIN_DE_MOIS_STOCK = "INTEGER";
        public static final String CLE_COL_RAZ_STOCK_INVENTAIRE_STOCK = "RAZ_Stock_Inventaire";
        public static final int NUM_COL_RAZ_STOCK_INVENTAIRE_STOCK = 36;
        public static final String TYPE_COL_RAZ_STOCK_INVENTAIRE_STOCK = "INTEGER";
        public static final String CLE_COL_SEUILALERTE_STOCK = "SeuilAlerte";
        public static final int NUM_COL_SEUILALERTE_STOCK = 37;
        public static final String TYPE_COL_SEUILALERTE_STOCK = "REAL";
        public static final String CLE_COL__UID_STOCK = "_UID";
        public static final int NUM_COL__UID_STOCK = 38;
        public static final String TYPE_COL__UID_STOCK = "INTEGER";
        public static final String CLE_COL_PRODUIT_UID_STOCK = "Produit_UID";
        public static final int NUM_COL_PRODUIT_UID_STOCK = 39;
        public static final String TYPE_COL_PRODUIT_UID_STOCK = "INTEGER";


        public static final String CREATION_TABLE_STOCK = "CREATE TABLE " + Constantes.TABLE_STOCK
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_DEPOT_REFERENCE_STOCK + " " + Constantes.TYPE_COL_DEPOT_REFERENCE_STOCK + " ,"
                + Constantes.CLE_COL_STO_DT_INVENTAIRE_STOCK + " " + Constantes.TYPE_COL_STO_DT_INVENTAIRE_STOCK + " ,"
                + Constantes.CLE_COL_STO_QTE_INVENT_STOCK + " " + Constantes.TYPE_COL_STO_QTE_INVENT_STOCK + " ,"
                + Constantes.CLE_COL_STO_DT_DER_ENTREE_STOCK + " " + Constantes.TYPE_COL_STO_DT_DER_ENTREE_STOCK + " ,"
                + Constantes.CLE_COL_STO_QTE_ENTREE_STOCK + " " + Constantes.TYPE_COL_STO_QTE_ENTREE_STOCK + " ,"
                + Constantes.CLE_COL_STO_DT_DER_SORTIE_STOCK + " " + Constantes.TYPE_COL_STO_DT_DER_SORTIE_STOCK + " ,"
                + Constantes.CLE_COL_STO_QTE_SORTIE_STOCK + " " + Constantes.TYPE_COL_STO_QTE_SORTIE_STOCK + " ,"
                + Constantes.CLE_COL_STO_QTE_AVANT_INVENT_STOCK + " " + Constantes.TYPE_COL_STO_QTE_AVANT_INVENT_STOCK + " ,"
                + Constantes.CLE_COL_STO_QTE_ATTENDUE_STOCK + " " + Constantes.TYPE_COL_STO_QTE_ATTENDUE_STOCK + " ,"
                + Constantes.CLE_COL_QUANTITE_ACTUELLE_STOCK + " " + Constantes.TYPE_COL_QUANTITE_ACTUELLE_STOCK + " ,"
                + Constantes.CLE_COL_STO_PRIX_STOCK + " " + Constantes.TYPE_COL_STO_PRIX_STOCK + " ,"
                + Constantes.CLE_COL_STO_VAL_ENTREES_STOCK + " " + Constantes.TYPE_COL_STO_VAL_ENTREES_STOCK + " ,"
                + Constantes.CLE_COL_STO_VAL_SORTIES_STOCK + " " + Constantes.TYPE_COL_STO_VAL_SORTIES_STOCK + " ,"
                + Constantes.CLE_COL_STO_VAL_INVENT_STOCK + " " + Constantes.TYPE_COL_STO_VAL_INVENT_STOCK + " ,"
                + Constantes.CLE_COL_STO_JOURS_ROTATION_STOCK + " " + Constantes.TYPE_COL_STO_JOURS_ROTATION_STOCK + " ,"
                + Constantes.CLE_COL_STO_DT_CREAT_STOCK + " " + Constantes.TYPE_COL_STO_DT_CREAT_STOCK + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_STOCK + " " + Constantes.TYPE_COL_SYS_DT_MAJ_STOCK + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_STOCK + " " + Constantes.TYPE_COL_SYS_USER_MAJ_STOCK + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_STOCK + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_STOCK + " ,"
                + Constantes.CLE_COL_TVA_STOCK + " " + Constantes.TYPE_COL_TVA_STOCK + " ,"
                + Constantes.CLE_COL_CATEGORIE_STOCK + " " + Constantes.TYPE_COL_CATEGORIE_STOCK + " ,"
                + Constantes.CLE_COL_PRODUIT_REFERENCE_STOCK + " " + Constantes.TYPE_COL_PRODUIT_REFERENCE_STOCK + " ,"
                + Constantes.CLE_COL_DESIGNATION_STOCK + " " + Constantes.TYPE_COL_DESIGNATION_STOCK + " ,"
                + Constantes.CLE_COL_ARRET_DISTRIBUTION_STOCK + " " + Constantes.TYPE_COL_ARRET_DISTRIBUTION_STOCK + " ,"
                + Constantes.CLE_COL_VALEUR_HT_STOCK + " " + Constantes.TYPE_COL_VALEUR_HT_STOCK + " ,"
                + Constantes.CLE_COL_VALEUR_TTC_STOCK + " " + Constantes.TYPE_COL_VALEUR_TTC_STOCK + " ,"
                + Constantes.CLE_COL_ZONE_STOCKAGE_STOCK + " " + Constantes.TYPE_COL_ZONE_STOCKAGE_STOCK + " ,"
                + Constantes.CLE_COL_FOURNISSEUR_STOCK + " " + Constantes.TYPE_COL_FOURNISSEUR_STOCK + " ,"
                + Constantes.CLE_COL_LIVRAISON_DIRECTE_STOCK + " " + Constantes.TYPE_COL_LIVRAISON_DIRECTE_STOCK + " ,"
                + Constantes.CLE_COL_PUMP_HT_DERNIERE_CLOTURE_STOCK + " " + Constantes.TYPE_COL_PUMP_HT_DERNIERE_CLOTURE_STOCK + " ,"
                + Constantes.CLE_COL_VALEUR_PUMP_HT_STOCK + " " + Constantes.TYPE_COL_VALEUR_PUMP_HT_STOCK + " ,"
                + Constantes.CLE_COL_VALEUR_PUMP_TTC_STOCK + " " + Constantes.TYPE_COL_VALEUR_PUMP_TTC_STOCK + " ,"
                + Constantes.CLE_COL_PUMP_TTC_DERNIERE_CLOTURE_STOCK + " " + Constantes.TYPE_COL_PUMP_TTC_DERNIERE_CLOTURE_STOCK + " ,"
                + Constantes.CLE_COL_CLASSIFICATION_STOCK + " " + Constantes.TYPE_COL_CLASSIFICATION_STOCK + " ,"
                + Constantes.CLE_COL_INVENTAIRE_FIN_DE_MOIS_STOCK + " " + Constantes.TYPE_COL_INVENTAIRE_FIN_DE_MOIS_STOCK + " ,"
                + Constantes.CLE_COL_RAZ_STOCK_INVENTAIRE_STOCK + " " + Constantes.TYPE_COL_RAZ_STOCK_INVENTAIRE_STOCK + " ,"
                + Constantes.CLE_COL_SEUILALERTE_STOCK + " " + Constantes.TYPE_COL_SEUILALERTE_STOCK + " ,"
                + Constantes.CLE_COL__UID_STOCK + " " + Constantes.TYPE_COL__UID_STOCK + ","
                + Constantes.CLE_COL_PRODUIT_UID_STOCK + " " + Constantes.TYPE_COL_PRODUIT_UID_STOCK
                + ");";
    }
}
