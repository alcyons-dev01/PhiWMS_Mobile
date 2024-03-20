package fr.alcyons.phimr4.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Inventaire_Ligne_Temp;

/*
  Created by quentinlanusse on 20/06/2017.
*/


public class Inventaire_Ligne_TempOpenHelper extends DBOpenHelper {

    public Inventaire_Ligne_TempOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static long insererUnInventaire_Ligne_TempEnBDD(SQLiteDatabase db, Inventaire_Ligne_Temp inventaire_ligne_temp) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_PRODUITID_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getProduitID());
        contentValues.put(Constantes.CLE_COL_PRODUITREFERENCE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getProduitReference());
        contentValues.put(Constantes.CLE_COL_FOURNISSEURNOM_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getFournisseurNom());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getCategorie());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getDesignation());
        contentValues.put(Constantes.CLE_COL_STOCKTHEORIQUE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getStockTheorique());
        contentValues.put(Constantes.CLE_COL_STOCKPHYSIQUE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getStockPhysique());
        contentValues.put(Constantes.CLE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getDepotReference());
        contentValues.put(Constantes.CLE_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.get_SYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.get_SYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.get_SYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_ZONE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getZone());
        contentValues.put(Constantes.CLE_COL_INVENTAIRE_ID_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getInventaire_ID());
        contentValues.put(Constantes.CLE_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.get_NePasImprimer());
        contentValues.put(Constantes.CLE_COL_PUHT_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getPUHT());
        contentValues.put(Constantes.CLE_COL_TVATX_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getTvaTx());
        contentValues.put(Constantes.CLE_COL_SUSPENDU_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getSuspendu());
        contentValues.put(Constantes.CLE_COL_VALEURTTC_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getValeurTTC());
        contentValues.put(Constantes.CLE_COL_ECART_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getEcart());
        contentValues.put(Constantes.CLE_COL_UNITE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getUnite());
        contentValues.put(Constantes.CLE_COL_COND_ACHAT_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getCond_Achat());
        contentValues.put(Constantes.CLE_COL_CLASSE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getClasse());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getEmplacement());
        contentValues.put(Constantes.CLE_COL_LOT_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getLot());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getPeremptionDate());
        contentValues.put(Constantes.CLE_COL__UID_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.get_UID());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, null, contentValues);

        inventaire_ligne_temp.setPhiMR4UUID((int) rowId);

        return rowId;
    }

    public static long mettreAJourInventaireLigneTemp(SQLiteDatabase db, Inventaire_Ligne_Temp inventaire_ligne_temp) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_PRODUITID_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getProduitID());
        contentValues.put(Constantes.CLE_COL_PRODUITREFERENCE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getProduitReference());
        contentValues.put(Constantes.CLE_COL_FOURNISSEURNOM_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getFournisseurNom());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getCategorie());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getDesignation());
        contentValues.put(Constantes.CLE_COL_STOCKTHEORIQUE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getStockTheorique());
        contentValues.put(Constantes.CLE_COL_STOCKPHYSIQUE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getStockPhysique());
        contentValues.put(Constantes.CLE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getDepotReference());
        contentValues.put(Constantes.CLE_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.get_SYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.get_SYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.get_SYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_ZONE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getZone());
        contentValues.put(Constantes.CLE_COL_INVENTAIRE_ID_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getInventaire_ID());
        contentValues.put(Constantes.CLE_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.get_NePasImprimer());
        contentValues.put(Constantes.CLE_COL_PUHT_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getPUHT());
        contentValues.put(Constantes.CLE_COL_TVATX_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getTvaTx());
        contentValues.put(Constantes.CLE_COL_SUSPENDU_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getSuspendu());
        contentValues.put(Constantes.CLE_COL_VALEURTTC_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getValeurTTC());
        contentValues.put(Constantes.CLE_COL_ECART_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getEcart());
        contentValues.put(Constantes.CLE_COL_UNITE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getUnite());
        contentValues.put(Constantes.CLE_COL_COND_ACHAT_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getCond_Achat());
        contentValues.put(Constantes.CLE_COL_CLASSE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getClasse());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getEmplacement());
        contentValues.put(Constantes.CLE_COL_LOT_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getLot());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.getPeremptionDate());
        contentValues.put(Constantes.CLE_COL__UID_INVENTAIRE_LIGNE_TEMP, inventaire_ligne_temp.get_UID());

        return db.update(Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, contentValues, DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(inventaire_ligne_temp.getPhiMR4UUID())});
    }

    public static void supprimerTousLesInventaireLigneTempsParDepot(SQLiteDatabase db, Depot depot) {
        db.delete(Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, Constantes.CLE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE_TEMP + "=?", new String[]{depot.getDepot_Reference()});
    }

    public static List<Inventaire_Ligne_Temp> getAllInventaireLigneTempByDepot(SQLiteDatabase db, Depot depot) {
        List<Inventaire_Ligne_Temp> inventaireLigneTempList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_INVENTAIRE_LIGNE_TEMP + " WHERE " + Constantes.CLE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE_TEMP + "=?", new String[]{depot.getDepot_Reference()});

        while (cursor.moveToNext()) {
            inventaireLigneTempList.add(new Inventaire_Ligne_Temp(cursor));
        }

        cursor.close();
        cursor = null;
        return inventaireLigneTempList;
    }

    public static Inventaire_Ligne_Temp getInventaireLigneTempByPhiMR4UUID(SQLiteDatabase db, int id) {
        Inventaire_Ligne_Temp inventaireLigneTemp = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_INVENTAIRE_LIGNE_TEMP + " WHERE " + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            inventaireLigneTemp = new Inventaire_Ligne_Temp(cursor);
        }

        cursor.close();
        cursor = null;
        return inventaireLigneTemp;
    }

    public static void supprimerInventaireLigneTempEnBDD(SQLiteDatabase db, Inventaire_Ligne_Temp inventaireLigneTemp) {
        db.delete(Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(inventaireLigneTemp.getPhiMR4UUID())});
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_INVENTAIRE_LIGNE_TEMP = "Inventaire_ligne_temp";

        public static final String CLE_COL_PRODUITREFERENCE_INVENTAIRE_LIGNE_TEMP = "produitReference";
        public static final int NUM_COL_PRODUITREFERENCE_INVENTAIRE_LIGNE_TEMP = 1;
        public static final String TYPE_COL_PRODUITREFERENCE_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL_FOURNISSEURNOM_INVENTAIRE_LIGNE_TEMP = "fournisseurNom";
        public static final int NUM_COL_FOURNISSEURNOM_INVENTAIRE_LIGNE_TEMP = 2;
        public static final String TYPE_COL_FOURNISSEURNOM_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL_CATEGORIE_INVENTAIRE_LIGNE_TEMP = "categorie";
        public static final int NUM_COL_CATEGORIE_INVENTAIRE_LIGNE_TEMP = 3;
        public static final String TYPE_COL_CATEGORIE_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL_DESIGNATION_INVENTAIRE_LIGNE_TEMP = "designation";
        public static final int NUM_COL_DESIGNATION_INVENTAIRE_LIGNE_TEMP = 4;
        public static final String TYPE_COL_DESIGNATION_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL_STOCKTHEORIQUE_INVENTAIRE_LIGNE_TEMP = "stockTheorique";
        public static final int NUM_COL_STOCKTHEORIQUE_INVENTAIRE_LIGNE_TEMP = 5;
        public static final String TYPE_COL_STOCKTHEORIQUE_INVENTAIRE_LIGNE_TEMP = "REAL";
        public static final String CLE_COL_STOCKPHYSIQUE_INVENTAIRE_LIGNE_TEMP = "stockPhysique";
        public static final int NUM_COL_STOCKPHYSIQUE_INVENTAIRE_LIGNE_TEMP = 6;
        public static final String TYPE_COL_STOCKPHYSIQUE_INVENTAIRE_LIGNE_TEMP = "REAL";
        public static final String CLE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE_TEMP = "depotReference";
        public static final int NUM_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE_TEMP = 7;
        public static final String TYPE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE_TEMP = "_SYS_DT_MAJ";
        public static final int NUM_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE_TEMP = 8;
        public static final String TYPE_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE_TEMP = "_SYS_HEURE_MAJ";
        public static final int NUM_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE_TEMP = 9;
        public static final String TYPE_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE_TEMP = "_SYS_USER_MAJ";
        public static final int NUM_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE_TEMP = 10;
        public static final String TYPE_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL_ZONE_INVENTAIRE_LIGNE_TEMP = "zone";
        public static final int NUM_COL_ZONE_INVENTAIRE_LIGNE_TEMP = 11;
        public static final String TYPE_COL_ZONE_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL_INVENTAIRE_ID_INVENTAIRE_LIGNE_TEMP = "Inventaire_ID";
        public static final int NUM_COL_INVENTAIRE_ID_INVENTAIRE_LIGNE_TEMP = 12;
        public static final String TYPE_COL_INVENTAIRE_ID_INVENTAIRE_LIGNE_TEMP = "INTEGER";
        public static final String CLE_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE_TEMP = "_NePasImprimer";
        public static final int NUM_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE_TEMP = 13;
        public static final String TYPE_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE_TEMP = "INTEGER";
        public static final String CLE_COL_PUHT_INVENTAIRE_LIGNE_TEMP = "PUHT";
        public static final int NUM_COL_PUHT_INVENTAIRE_LIGNE_TEMP = 14;
        public static final String TYPE_COL_PUHT_INVENTAIRE_LIGNE_TEMP = "REAL";
        public static final String CLE_COL_TVATX_INVENTAIRE_LIGNE_TEMP = "tvaTx";
        public static final int NUM_COL_TVATX_INVENTAIRE_LIGNE_TEMP = 15;
        public static final String TYPE_COL_TVATX_INVENTAIRE_LIGNE_TEMP = "REAL";
        public static final String CLE_COL_SUSPENDU_INVENTAIRE_LIGNE_TEMP = "suspendu";
        public static final int NUM_COL_SUSPENDU_INVENTAIRE_LIGNE_TEMP = 16;
        public static final String TYPE_COL_SUSPENDU_INVENTAIRE_LIGNE_TEMP = "INTEGER";
        public static final String CLE_COL_VALEURTTC_INVENTAIRE_LIGNE_TEMP = "valeurTTC";
        public static final int NUM_COL_VALEURTTC_INVENTAIRE_LIGNE_TEMP = 17;
        public static final String TYPE_COL_VALEURTTC_INVENTAIRE_LIGNE_TEMP = "REAL";
        public static final String CLE_COL_ECART_INVENTAIRE_LIGNE_TEMP = "ecart";
        public static final int NUM_COL_ECART_INVENTAIRE_LIGNE_TEMP = 18;
        public static final String TYPE_COL_ECART_INVENTAIRE_LIGNE_TEMP = "REAL";
        public static final String CLE_COL_UNITE_INVENTAIRE_LIGNE_TEMP = "unite";
        public static final int NUM_COL_UNITE_INVENTAIRE_LIGNE_TEMP = 19;
        public static final String TYPE_COL_UNITE_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL_COND_ACHAT_INVENTAIRE_LIGNE_TEMP = "Cond_Achat";
        public static final int NUM_COL_COND_ACHAT_INVENTAIRE_LIGNE_TEMP = 20;
        public static final String TYPE_COL_COND_ACHAT_INVENTAIRE_LIGNE_TEMP = "REAL";
        public static final String CLE_COL_CLASSE_INVENTAIRE_LIGNE_TEMP = "classe";
        public static final int NUM_COL_CLASSE_INVENTAIRE_LIGNE_TEMP = 21;
        public static final String TYPE_COL_CLASSE_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL_EMPLACEMENT_INVENTAIRE_LIGNE_TEMP = "emplacement";
        public static final int NUM_COL_EMPLACEMENT_INVENTAIRE_LIGNE_TEMP = 22;
        public static final String TYPE_COL_EMPLACEMENT_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL_LOT_INVENTAIRE_LIGNE_TEMP = "lot";
        public static final int NUM_COL_LOT_INVENTAIRE_LIGNE_TEMP = 23;
        public static final String TYPE_COL_LOT_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL_PEREMPTIONDATE_INVENTAIRE_LIGNE_TEMP = "PeremptionDate";
        public static final int NUM_COL_PEREMPTIONDATE_INVENTAIRE_LIGNE_TEMP = 24;
        public static final String TYPE_COL_PEREMPTIONDATE_INVENTAIRE_LIGNE_TEMP = "TEXT";
        public static final String CLE_COL__UID_INVENTAIRE_LIGNE_TEMP = "_UID";
        public static final int NUM_COL__UID_INVENTAIRE_LIGNE_TEMP = 25;
        public static final String TYPE_COL__UID_INVENTAIRE_LIGNE_TEMP = "INTEGER";
        public static final String CLE_COL_PRODUITID_INVENTAIRE_LIGNE_TEMP = "produitID";
        public static final int NUM_COL_PRODUITID_INVENTAIRE_LIGNE_TEMP = 26;
        public static final String TYPE_COL_PRODUITID_INVENTAIRE_LIGNE_TEMP = "INTEGER";


        public static final String CREATION_TABLE_INVENTAIRE_LIGNE_TEMP = "CREATE TABLE " + Constantes.TABLE_INVENTAIRE_LIGNE_TEMP
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_PRODUITREFERENCE_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_PRODUITREFERENCE_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_FOURNISSEURNOM_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_FOURNISSEURNOM_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_CATEGORIE_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_CATEGORIE_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_DESIGNATION_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_DESIGNATION_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_STOCKTHEORIQUE_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_STOCKTHEORIQUE_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_STOCKPHYSIQUE_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_STOCKPHYSIQUE_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_ZONE_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_ZONE_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_INVENTAIRE_ID_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_INVENTAIRE_ID_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_PUHT_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_PUHT_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_TVATX_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_TVATX_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_SUSPENDU_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_SUSPENDU_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_VALEURTTC_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_VALEURTTC_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_ECART_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_ECART_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_UNITE_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_UNITE_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_COND_ACHAT_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_COND_ACHAT_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_CLASSE_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_CLASSE_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_EMPLACEMENT_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_EMPLACEMENT_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_LOT_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_LOT_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL_PEREMPTIONDATE_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_PEREMPTIONDATE_INVENTAIRE_LIGNE_TEMP + " ,"
                + Constantes.CLE_COL__UID_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL__UID_INVENTAIRE_LIGNE_TEMP + ","
                + Constantes.CLE_COL_PRODUITID_INVENTAIRE_LIGNE_TEMP + " " + Constantes.TYPE_COL_PRODUITID_INVENTAIRE_LIGNE_TEMP
                + ");";
    }
}
