package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import fr.alcyons.phiwms_mobile.Classes.Preparation;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class PreparationOpenHelper extends DBOpenHelper {

    public PreparationOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static Preparation getPreparationsByPhiMR4UUID(SQLiteDatabase db, int id) {
        Preparation objet = null;

        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PREPARATION + " WHERE " + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=? ", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            objet = new Preparation(cursor);
        }

        return objet;
    }

    public static Preparation getPreparationByDepot(SQLiteDatabase db, int depot_id) {
        Preparation preparation = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PREPARATION + " WHERE " + Constantes.CLE_COL_ID_DEPOT_PREPARATION + "=?", new String[]{String.valueOf(depot_id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            preparation = new Preparation(cursor);
        }

        cursor.close();
        cursor = null;
        return preparation;
    }

    public static long supprimerUnePreparationEnBDD(SQLiteDatabase db, int preparationID) {
        return db.delete(Constantes.TABLE_PREPARATION, Constantes.CLE_COL_ID_PREPARATION + "=?", new String[]{String.valueOf(preparationID)});
    }

    public static long insererUnPreparationEnBDD(SQLiteDatabase db, Preparation preparation) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_PREPARATION, preparation.getID());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_PREPARATION, preparation.getRef_depot());
        contentValues.put(Constantes.CLE_COL_CYCLE_PREPARATION, preparation.getCycle());
        contentValues.put(Constantes.CLE_COL_ID_CYCLE_PREPARATION, preparation.getID_Cycle());
        contentValues.put(Constantes.CLE_COL_ANNEE_PREPARATION, preparation.getAnnee());
        contentValues.put(Constantes.CLE_COL_MOIS_PREPARATION, preparation.getMois());
        contentValues.put(Constantes.CLE_COL_MONTANT_HT_PREPARATION, preparation.getMontant_HT());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_PREPARATION, preparation.getMontant_TTC());
        contentValues.put(Constantes.CLE_COL_VALIDEE_PREPARATION, preparation.getValidée());
        contentValues.put(Constantes.CLE_COL_PROPOSEE_PREPARATION, preparation.getProposée());
        contentValues.put(Constantes.CLE_COL_CYCLE_DEPOT_PREPARATION, preparation.getCycle_Depot());
        contentValues.put(Constantes.CLE_COL_DOMICILE_PREPARATION, preparation.getDomicile());
        contentValues.put(Constantes.CLE_COL_STATUT_PREPARATION, preparation.getStatut());
        contentValues.put(Constantes.CLE_COL_DATE_INVENTAIRE_PREPARATION, preparation.getDate_inventaire());
        contentValues.put(Constantes.CLE_COL_DATE_LIVRAISON2_PREPARATION, preparation.getDate_Livraison2());
        contentValues.put(Constantes.CLE_COL_MONTANT_TVA_PREPARATION, preparation.getMontant_TVA());
        contentValues.put(Constantes.CLE_COL_ID_DEPOT_PREPARATION, preparation.getID_Depot());
        contentValues.put(Constantes.CLE_COL_NUM_BON_PREV_PREPARATION, preparation.getNum_Bon_Prev());
        contentValues.put(Constantes.CLE_COL_DATE_LIVRAISON1_PREPARATION, preparation.getDate_Livraison1());
        contentValues.put(Constantes.CLE_COL_PREPARATION_NOMINATIVE_PREPARATION, preparation.getPreparation_Nominative());
        contentValues.put(Constantes.CLE_COL_IPP_PATIENT_PREPARATION, preparation.getIPP_Patient());
        contentValues.put(Constantes.CLE_COL_NOM_PATIENT_PREPARATION, preparation.getNom_Patient());
        contentValues.put(Constantes.CLE_COL_CB_BON_COMMANDE_PATIENT_PREPARATION, preparation.getCB_Bon_Commande_Patient());
        contentValues.put(Constantes.CLE_COL_DATE_PREVISION_PREPARATION, preparation.getDate_Prevision());
        contentValues.put(Constantes.CLE_COL_DATE_LIVRAISON3_PREPARATION, preparation.getDate_Livraison3());
        contentValues.put(Constantes.CLE_COL_DATE_LIVRAISON4_PREPARATION, preparation.getDate_Livraison4());
        contentValues.put(Constantes.CLE_COL_DATE_LIVRAISON5_PREPARATION, preparation.getDate_Livraison5());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_PREPARATION, null, contentValues);

        preparation.setPhiMR4UUID((int) rowId);

        return rowId;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PREPARATION = "Preparation";

        public static final String CLE_COL_REF_DEPOT_PREPARATION = "Ref_depot";
        public static final int NUM_COL_REF_DEPOT_PREPARATION = 1;
        public static final String TYPE_COL_REF_DEPOT_PREPARATION = "TEXT";
        public static final String CLE_COL_CYCLE_PREPARATION = "Cycle";
        public static final int NUM_COL_CYCLE_PREPARATION = 2;
        public static final String TYPE_COL_CYCLE_PREPARATION = "TEXT";
        public static final String CLE_COL_ID_CYCLE_PREPARATION = "ID_Cycle";
        public static final int NUM_COL_ID_CYCLE_PREPARATION = 3;
        public static final String TYPE_COL_ID_CYCLE_PREPARATION = "INTEGER";
        public static final String CLE_COL_ANNEE_PREPARATION = "Annee";
        public static final int NUM_COL_ANNEE_PREPARATION = 4;
        public static final String TYPE_COL_ANNEE_PREPARATION = "INTEGER";
        public static final String CLE_COL_MOIS_PREPARATION = "Mois";
        public static final int NUM_COL_MOIS_PREPARATION = 5;
        public static final String TYPE_COL_MOIS_PREPARATION = "INTEGER";
        public static final String CLE_COL_MONTANT_HT_PREPARATION = "Montant_HT";
        public static final int NUM_COL_MONTANT_HT_PREPARATION = 6;
        public static final String TYPE_COL_MONTANT_HT_PREPARATION = "REAL";
        public static final String CLE_COL_MONTANT_TTC_PREPARATION = "Montant_TTC";
        public static final int NUM_COL_MONTANT_TTC_PREPARATION = 7;
        public static final String TYPE_COL_MONTANT_TTC_PREPARATION = "REAL";
        public static final String CLE_COL_VALIDEE_PREPARATION = "Validée";
        public static final int NUM_COL_VALIDEE_PREPARATION = 8;
        public static final String TYPE_COL_VALIDEE_PREPARATION = "INTEGER";
        public static final String CLE_COL_PROPOSEE_PREPARATION = "Proposée";
        public static final int NUM_COL_PROPOSEE_PREPARATION = 9;
        public static final String TYPE_COL_PROPOSEE_PREPARATION = "INTEGER";
        public static final String CLE_COL_CYCLE_DEPOT_PREPARATION = "Cycle_Depot";
        public static final int NUM_COL_CYCLE_DEPOT_PREPARATION = 10;
        public static final String TYPE_COL_CYCLE_DEPOT_PREPARATION = "TEXT";
        public static final String CLE_COL_DOMICILE_PREPARATION = "Domicile";
        public static final int NUM_COL_DOMICILE_PREPARATION = 11;
        public static final String TYPE_COL_DOMICILE_PREPARATION = "INTEGER";
        public static final String CLE_COL_STATUT_PREPARATION = "Statut";
        public static final int NUM_COL_STATUT_PREPARATION = 12;
        public static final String TYPE_COL_STATUT_PREPARATION = "TEXT";
        public static final String CLE_COL_DATE_INVENTAIRE_PREPARATION = "Date_inventaire";
        public static final int NUM_COL_DATE_INVENTAIRE_PREPARATION = 13;
        public static final String TYPE_COL_DATE_INVENTAIRE_PREPARATION = "TEXT";
        public static final String CLE_COL_DATE_LIVRAISON2_PREPARATION = "Date_Livraison2";
        public static final int NUM_COL_DATE_LIVRAISON2_PREPARATION = 14;
        public static final String TYPE_COL_DATE_LIVRAISON2_PREPARATION = "TEXT";
        public static final String CLE_COL_MONTANT_TVA_PREPARATION = "Montant_TVA";
        public static final int NUM_COL_MONTANT_TVA_PREPARATION = 15;
        public static final String TYPE_COL_MONTANT_TVA_PREPARATION = "REAL";
        public static final String CLE_COL_ID_DEPOT_PREPARATION = "ID_Depot";
        public static final int NUM_COL_ID_DEPOT_PREPARATION = 16;
        public static final String TYPE_COL_ID_DEPOT_PREPARATION = "INTEGER";
        public static final String CLE_COL_NUM_BON_PREV_PREPARATION = "Num_Bon_Prev";
        public static final int NUM_COL_NUM_BON_PREV_PREPARATION = 17;
        public static final String TYPE_COL_NUM_BON_PREV_PREPARATION = "TEXT";
        public static final String CLE_COL_DATE_LIVRAISON1_PREPARATION = "Date_Livraison1";
        public static final int NUM_COL_DATE_LIVRAISON1_PREPARATION = 18;
        public static final String TYPE_COL_DATE_LIVRAISON1_PREPARATION = "TEXT";
        public static final String CLE_COL_PREPARATION_NOMINATIVE_PREPARATION = "Preparation_Nominative";
        public static final int NUM_COL_PREPARATION_NOMINATIVE_PREPARATION = 19;
        public static final String TYPE_COL_PREPARATION_NOMINATIVE_PREPARATION = "INTEGER";
        public static final String CLE_COL_IPP_PATIENT_PREPARATION = "IPP_Patient";
        public static final int NUM_COL_IPP_PATIENT_PREPARATION = 20;
        public static final String TYPE_COL_IPP_PATIENT_PREPARATION = "TEXT";
        public static final String CLE_COL_NOM_PATIENT_PREPARATION = "Nom_Patient";
        public static final int NUM_COL_NOM_PATIENT_PREPARATION = 21;
        public static final String TYPE_COL_NOM_PATIENT_PREPARATION = "TEXT";
        public static final String CLE_COL_CB_BON_COMMANDE_PATIENT_PREPARATION = "CB_Bon_Commande_Patient";
        public static final int NUM_COL_CB_BON_COMMANDE_PATIENT_PREPARATION = 22;
        public static final String TYPE_COL_CB_BON_COMMANDE_PATIENT_PREPARATION = "TEXT";
        public static final String CLE_COL_DATE_PREVISION_PREPARATION = "Date_Prevision";
        public static final int NUM_COL_DATE_PREVISION_PREPARATION = 23;
        public static final String TYPE_COL_DATE_PREVISION_PREPARATION = "TEXT";
        public static final String CLE_COL_DATE_LIVRAISON3_PREPARATION = "Date_Livraison3";
        public static final int NUM_COL_DATE_LIVRAISON3_PREPARATION = 24;
        public static final String TYPE_COL_DATE_LIVRAISON3_PREPARATION = "TEXT";
        public static final String CLE_COL_DATE_LIVRAISON4_PREPARATION = "Date_Livraison4";
        public static final int NUM_COL_DATE_LIVRAISON4_PREPARATION = 25;
        public static final String TYPE_COL_DATE_LIVRAISON4_PREPARATION = "TEXT";
        public static final String CLE_COL_DATE_LIVRAISON5_PREPARATION = "Date_Livraison5";
        public static final int NUM_COL_DATE_LIVRAISON5_PREPARATION = 26;
        public static final String TYPE_COL_DATE_LIVRAISON5_PREPARATION = "TEXT";
        public static final String CLE_COL_ID_PREPARATION = "ID";
        public static final int NUM_COL_ID_PREPARATION = 27;
        public static final String TYPE_COL_ID_PREPARATION = "INTEGER";


        public static final String CREATION_TABLE_PREPARATION = "CREATE TABLE " + Constantes.TABLE_PREPARATION
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_REF_DEPOT_PREPARATION + " " + Constantes.TYPE_COL_REF_DEPOT_PREPARATION + " ,"
                + Constantes.CLE_COL_CYCLE_PREPARATION + " " + Constantes.TYPE_COL_CYCLE_PREPARATION + " ,"
                + Constantes.CLE_COL_ID_CYCLE_PREPARATION + " " + Constantes.TYPE_COL_ID_CYCLE_PREPARATION + " ,"
                + Constantes.CLE_COL_ANNEE_PREPARATION + " " + Constantes.TYPE_COL_ANNEE_PREPARATION + " ,"
                + Constantes.CLE_COL_MOIS_PREPARATION + " " + Constantes.TYPE_COL_MOIS_PREPARATION + " ,"
                + Constantes.CLE_COL_MONTANT_HT_PREPARATION + " " + Constantes.TYPE_COL_MONTANT_HT_PREPARATION + " ,"
                + Constantes.CLE_COL_MONTANT_TTC_PREPARATION + " " + Constantes.TYPE_COL_MONTANT_TTC_PREPARATION + " ,"
                + Constantes.CLE_COL_VALIDEE_PREPARATION + " " + Constantes.TYPE_COL_VALIDEE_PREPARATION + " ,"
                + Constantes.CLE_COL_PROPOSEE_PREPARATION + " " + Constantes.TYPE_COL_PROPOSEE_PREPARATION + " ,"
                + Constantes.CLE_COL_CYCLE_DEPOT_PREPARATION + " " + Constantes.TYPE_COL_CYCLE_DEPOT_PREPARATION + " ,"
                + Constantes.CLE_COL_DOMICILE_PREPARATION + " " + Constantes.TYPE_COL_DOMICILE_PREPARATION + " ,"
                + Constantes.CLE_COL_STATUT_PREPARATION + " " + Constantes.TYPE_COL_STATUT_PREPARATION + " ,"
                + Constantes.CLE_COL_DATE_INVENTAIRE_PREPARATION + " " + Constantes.TYPE_COL_DATE_INVENTAIRE_PREPARATION + " ,"
                + Constantes.CLE_COL_DATE_LIVRAISON2_PREPARATION + " " + Constantes.TYPE_COL_DATE_LIVRAISON2_PREPARATION + " ,"
                + Constantes.CLE_COL_MONTANT_TVA_PREPARATION + " " + Constantes.TYPE_COL_MONTANT_TVA_PREPARATION + " ,"
                + Constantes.CLE_COL_ID_DEPOT_PREPARATION + " " + Constantes.TYPE_COL_ID_DEPOT_PREPARATION + " ,"
                + Constantes.CLE_COL_NUM_BON_PREV_PREPARATION + " " + Constantes.TYPE_COL_NUM_BON_PREV_PREPARATION + " ,"
                + Constantes.CLE_COL_DATE_LIVRAISON1_PREPARATION + " " + Constantes.TYPE_COL_DATE_LIVRAISON1_PREPARATION + " ,"
                + Constantes.CLE_COL_PREPARATION_NOMINATIVE_PREPARATION + " " + Constantes.TYPE_COL_PREPARATION_NOMINATIVE_PREPARATION + " ,"
                + Constantes.CLE_COL_IPP_PATIENT_PREPARATION + " " + Constantes.TYPE_COL_IPP_PATIENT_PREPARATION + " ,"
                + Constantes.CLE_COL_NOM_PATIENT_PREPARATION + " " + Constantes.TYPE_COL_NOM_PATIENT_PREPARATION + " ,"
                + Constantes.CLE_COL_CB_BON_COMMANDE_PATIENT_PREPARATION + " " + Constantes.TYPE_COL_CB_BON_COMMANDE_PATIENT_PREPARATION + " ,"
                + Constantes.CLE_COL_DATE_PREVISION_PREPARATION + " " + Constantes.TYPE_COL_DATE_PREVISION_PREPARATION + " ,"
                + Constantes.CLE_COL_DATE_LIVRAISON3_PREPARATION + " " + Constantes.TYPE_COL_DATE_LIVRAISON3_PREPARATION + " ,"
                + Constantes.CLE_COL_DATE_LIVRAISON4_PREPARATION + " " + Constantes.TYPE_COL_DATE_LIVRAISON4_PREPARATION + " ,"
                + Constantes.CLE_COL_DATE_LIVRAISON5_PREPARATION + " " + Constantes.TYPE_COL_DATE_LIVRAISON5_PREPARATION + ","
                + Constantes.CLE_COL_ID_PREPARATION + " " + Constantes.TYPE_COL_ID_PREPARATION
                + ");";
    }
}
