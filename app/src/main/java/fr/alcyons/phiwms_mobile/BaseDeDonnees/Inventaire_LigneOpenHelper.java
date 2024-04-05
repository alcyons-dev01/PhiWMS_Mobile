package fr.alcyons.phiwms_mobile.BaseDeDonnees;


import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Inventaire_LigneOpenHelper extends DBOpenHelper {

    public Inventaire_LigneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public static class Constantes implements BaseColumns {
        public static final String TABLE_INVENTAIRE_LIGNE = "Inventaire_Ligne_ligne";

        public static final String CLE_COL_PRODUIT_REFERENCE_INVENTAIRE_LIGNE = "Produit_Reference";
        public static final int NUM_COL_PRODUIT_REFERENCE_INVENTAIRE_LIGNE = 1;
        public static final String TYPE_COL_PRODUIT_REFERENCE_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL_FOURNISSEUR_INVENTAIRE_LIGNE = "Fournisseur";
        public static final int NUM_COL_FOURNISSEUR_INVENTAIRE_LIGNE = 2;
        public static final String TYPE_COL_FOURNISSEUR_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL_CATEGORIE_INVENTAIRE_LIGNE = "Categorie";
        public static final int NUM_COL_CATEGORIE_INVENTAIRE_LIGNE = 3;
        public static final String TYPE_COL_CATEGORIE_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL_DESIGNATION_INVENTAIRE_LIGNE = "Designation";
        public static final int NUM_COL_DESIGNATION_INVENTAIRE_LIGNE = 4;
        public static final String TYPE_COL_DESIGNATION_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL_STOCK_THEORIQUE_INVENTAIRE_LIGNE = "stock_theorique";
        public static final int NUM_COL_STOCK_THEORIQUE_INVENTAIRE_LIGNE = 5;
        public static final String TYPE_COL_STOCK_THEORIQUE_INVENTAIRE_LIGNE = "REAL";
        public static final String CLE_COL_STOCK_PHYSIQUE_INVENTAIRE_LIGNE = "stock_Physique";
        public static final int NUM_COL_STOCK_PHYSIQUE_INVENTAIRE_LIGNE = 6;
        public static final String TYPE_COL_STOCK_PHYSIQUE_INVENTAIRE_LIGNE = "REAL";
        public static final String CLE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE = "depotReference";
        public static final int NUM_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE = 7;
        public static final String TYPE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE = "_SYS_DT_MAJ";
        public static final int NUM_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE = 8;
        public static final String TYPE_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE = "_SYS_HEURE_MAJ";
        public static final int NUM_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE = 9;
        public static final String TYPE_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE = "_SYS_USER_MAJ";
        public static final int NUM_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE = 10;
        public static final String TYPE_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL_ZONE_INVENTAIRE_LIGNE = "Zone";
        public static final int NUM_COL_ZONE_INVENTAIRE_LIGNE = 11;
        public static final String TYPE_COL_ZONE_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL_ID_INV_INVENTAIRE_LIGNE = "ID_Inv";
        public static final int NUM_COL_ID_INV_INVENTAIRE_LIGNE = 12;
        public static final String TYPE_COL_ID_INV_INVENTAIRE_LIGNE = "INTEGER";
        public static final String CLE_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE = "_NePasImprimer";
        public static final int NUM_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE = 13;
        public static final String TYPE_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE = "INTEGER";
        public static final String CLE_COL_PUHT_INVENTAIRE_LIGNE = "PUHT";
        public static final int NUM_COL_PUHT_INVENTAIRE_LIGNE = 14;
        public static final String TYPE_COL_PUHT_INVENTAIRE_LIGNE = "REAL";
        public static final String CLE_COL_TVATX_INVENTAIRE_LIGNE = "tvaTx";
        public static final int NUM_COL_TVATX_INVENTAIRE_LIGNE = 15;
        public static final String TYPE_COL_TVATX_INVENTAIRE_LIGNE = "REAL";
        public static final String CLE_COL_SUSPENDU_INVENTAIRE_LIGNE = "suspendu";
        public static final int NUM_COL_SUSPENDU_INVENTAIRE_LIGNE = 16;
        public static final String TYPE_COL_SUSPENDU_INVENTAIRE_LIGNE = "INTEGER";
        public static final String CLE_COL_VALEUR_TTC_INVENTAIRE_LIGNE = "valeur_TTC";
        public static final int NUM_COL_VALEUR_TTC_INVENTAIRE_LIGNE = 17;
        public static final String TYPE_COL_VALEUR_TTC_INVENTAIRE_LIGNE = "REAL";
        public static final String CLE_COL_ECART_INVENTAIRE_LIGNE = "ecart";
        public static final int NUM_COL_ECART_INVENTAIRE_LIGNE = 18;
        public static final String TYPE_COL_ECART_INVENTAIRE_LIGNE = "REAL";
        public static final String CLE_COL_UNITE_INVENTAIRE_LIGNE = "unite";
        public static final int NUM_COL_UNITE_INVENTAIRE_LIGNE = 19;
        public static final String TYPE_COL_UNITE_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL_CONDITIONNEMENT_ACHAT_INVENTAIRE_LIGNE = "Conditionnement_Achat";
        public static final int NUM_COL_CONDITIONNEMENT_ACHAT_INVENTAIRE_LIGNE = 20;
        public static final String TYPE_COL_CONDITIONNEMENT_ACHAT_INVENTAIRE_LIGNE = "REAL";
        public static final String CLE_COL_ID_INVENTAIRE_LIGNE = "ID";
        public static final int NUM_COL_ID_INVENTAIRE_LIGNE = 21;
        public static final String TYPE_COL_ID_INVENTAIRE_LIGNE = "INTEGER";
        public static final String CLE_COL__UID_INVENTAIRE_LIGNE = "_UID";
        public static final int NUM_COL__UID_INVENTAIRE_LIGNE = 22;
        public static final String TYPE_COL__UID_INVENTAIRE_LIGNE = "INTEGER";
        public static final String CLE_COL_CLASSE_INVENTAIRE_LIGNE = "Classe";
        public static final int NUM_COL_CLASSE_INVENTAIRE_LIGNE = 23;
        public static final String TYPE_COL_CLASSE_INVENTAIRE_LIGNE = "TEXT";
        public static final String CLE_COL_PRODUITID_INVENTAIRE_LIGNE = "ProduitID";
        public static final int NUM_COL_PRODUITID_INVENTAIRE_LIGNE = 24;
        public static final String TYPE_COL_PRODUITID_INVENTAIRE_LIGNE = "INTEGER";


        public static final String CREATION_TABLE_INVENTAIRE_LIGNE = "CREATE TABLE " + Constantes.TABLE_INVENTAIRE_LIGNE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_PRODUIT_REFERENCE_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_PRODUIT_REFERENCE_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_FOURNISSEUR_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_FOURNISSEUR_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_CATEGORIE_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_CATEGORIE_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_DESIGNATION_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_DESIGNATION_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_STOCK_THEORIQUE_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_STOCK_THEORIQUE_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_STOCK_PHYSIQUE_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_STOCK_PHYSIQUE_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_DEPOTREFERENCE_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL__SYS_DT_MAJ_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL__SYS_HEURE_MAJ_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL__SYS_USER_MAJ_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_ZONE_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_ZONE_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_ID_INV_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_ID_INV_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL__NEPASIMPRIMER_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_PUHT_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_PUHT_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_TVATX_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_TVATX_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_SUSPENDU_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_SUSPENDU_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_VALEUR_TTC_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_VALEUR_TTC_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_ECART_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_ECART_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_UNITE_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_UNITE_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_CONDITIONNEMENT_ACHAT_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_CONDITIONNEMENT_ACHAT_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL_ID_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_ID_INVENTAIRE_LIGNE + " ,"
                + Constantes.CLE_COL__UID_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL__UID_INVENTAIRE_LIGNE + ","
                + Constantes.CLE_COL_CLASSE_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_CLASSE_INVENTAIRE_LIGNE + ","
                + Constantes.CLE_COL_PRODUITID_INVENTAIRE_LIGNE + " " + Constantes.TYPE_COL_PRODUITID_INVENTAIRE_LIGNE
                + ");";
    }
}
