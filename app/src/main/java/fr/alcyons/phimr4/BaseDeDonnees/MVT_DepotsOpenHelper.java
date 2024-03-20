package fr.alcyons.phimr4.BaseDeDonnees;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.Classes.MVT_Depots;

/**
 * Created by olivier on 12/03/2018.
 */

public class MVT_DepotsOpenHelper extends DBOpenHelper {

    public MVT_DepotsOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static List<MVT_Depots> getMVT_DepotsPAD(SQLiteDatabase db, int depotid, int codeProduit) {
        List<MVT_Depots> mvtDepotsList = new ArrayList<>();
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_MVT_DEPOTS + " WHERE " + Constantes.CLE_COL_CODE_DEPOT_MVT_DEPOTS + "=? AND " + Constantes.CLE_COL_CODE_PRODUIT_MVT_DEPOTS + "=? AND " + Constantes.CLE_COL_RELIQUAT_MVT_DEPOTS + "= 1", new String[]{String.valueOf(depotid), String.valueOf(codeProduit)});

        while (cursor.moveToNext()) {
            mvtDepotsList.add(new MVT_Depots(cursor));
        }

        cursor.close();
        cursor = null;
        return mvtDepotsList;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_MVT_DEPOTS = "MVT_Depots";
        public static final String CLE_COL_CODE_MVT_DEPOTS = "Code";
        public static final int NUM_COL_CODE_MVT_DEPOTS = 1;
        public static final String TYPE_COL_CODE_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_DEPOT_REFERENCE_MVT_DEPOTS = "Depot_Reference";
        public static final int NUM_COL_DEPOT_REFERENCE_MVT_DEPOTS = 2;
        public static final String TYPE_COL_DEPOT_REFERENCE_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_CODE_DEPOT_MVT_DEPOTS = "Code_dépot";
        public static final int NUM_COL_CODE_DEPOT_MVT_DEPOTS = 3;
        public static final String TYPE_COL_CODE_DEPOT_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_CODE_PRODUIT_MVT_DEPOTS = "Code_produit";
        public static final int NUM_COL_CODE_PRODUIT_MVT_DEPOTS = 4;
        public static final String TYPE_COL_CODE_PRODUIT_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_REFERENCE_F_MVT_DEPOTS = "Référence_F";
        public static final int NUM_COL_REFERENCE_F_MVT_DEPOTS = 5;
        public static final String TYPE_COL_REFERENCE_F_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_PU_COM_MVT_DEPOTS = "PU_com";
        public static final int NUM_COL_PU_COM_MVT_DEPOTS = 6;
        public static final String TYPE_COL_PU_COM_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_DESIGNATION_MVT_DEPOTS = "Désignation";
        public static final int NUM_COL_DESIGNATION_MVT_DEPOTS = 7;
        public static final String TYPE_COL_DESIGNATION_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_UNITE_MVT_DEPOTS = "Unité";
        public static final int NUM_COL_UNITE_MVT_DEPOTS = 8;
        public static final String TYPE_COL_UNITE_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_FOURNISSEUR_MVT_DEPOTS = "Fournisseur";
        public static final int NUM_COL_FOURNISSEUR_MVT_DEPOTS = 9;
        public static final String TYPE_COL_FOURNISSEUR_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_CODE_FRS_MVT_DEPOTS = "Code_frs";
        public static final int NUM_COL_CODE_FRS_MVT_DEPOTS = 10;
        public static final String TYPE_COL_CODE_FRS_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_QTE_COM_MVT_DEPOTS = "Qté_com";
        public static final int NUM_COL_QTE_COM_MVT_DEPOTS = 11;
        public static final String TYPE_COL_QTE_COM_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_QTE_LIVREE_MVT_DEPOTS = "Qté_livrée";
        public static final int NUM_COL_QTE_LIVREE_MVT_DEPOTS = 12;
        public static final String TYPE_COL_QTE_LIVREE_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_QTE_RESTE_MVT_DEPOTS = "Qté_reste";
        public static final int NUM_COL_QTE_RESTE_MVT_DEPOTS = 13;
        public static final String TYPE_COL_QTE_RESTE_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_RELIQUAT_MVT_DEPOTS = "Reliquat";
        public static final int NUM_COL_RELIQUAT_MVT_DEPOTS = 14;
        public static final String TYPE_COL_RELIQUAT_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_DATE_ENTREE_MVT_DEPOTS = "Date_entrée";
        public static final int NUM_COL_DATE_ENTREE_MVT_DEPOTS = 15;
        public static final String TYPE_COL_DATE_ENTREE_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_COMMANDE_MVT_DEPOTS = "Commande";
        public static final int NUM_COL_COMMANDE_MVT_DEPOTS = 17;
        public static final String TYPE_COL_COMMANDE_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_DATE_COM_MVT_DEPOTS = "Date_com";
        public static final int NUM_COL_DATE_COM_MVT_DEPOTS = 18;
        public static final String TYPE_COL_DATE_COM_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_CONDI_ACHAT_MVT_DEPOTS = "Condi_achat";
        public static final int NUM_COL_CONDI_ACHAT_MVT_DEPOTS = 19;
        public static final String TYPE_COL_CONDI_ACHAT_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_CONDI_DISTRI_MVT_DEPOTS = "Condi_distri";
        public static final int NUM_COL_CONDI_DISTRI_MVT_DEPOTS = 20;
        public static final String TYPE_COL_CONDI_DISTRI_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_DATE_PEREMPTION_MVT_DEPOTS = "Date_péremption";
        public static final int NUM_COL_DATE_PEREMPTION_MVT_DEPOTS = 21;
        public static final String TYPE_COL_DATE_PEREMPTION_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_NUM_LOT_MVT_DEPOTS = "Num_lot";
        public static final int NUM_COL_NUM_LOT_MVT_DEPOTS = 22;
        public static final String TYPE_COL_NUM_LOT_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_CODE_BARRE_MVT_DEPOTS = "Code_barre";
        public static final int NUM_COL_CODE_BARRE_MVT_DEPOTS = 23;
        public static final String TYPE_COL_CODE_BARRE_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_PEREMPTION_MVT_DEPOTS = "Péremption";
        public static final int NUM_COL_PEREMPTION_MVT_DEPOTS = 24;
        public static final String TYPE_COL_PEREMPTION_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_ID_LIGNE_COMMANDE_MVT_DEPOTS = "id_ligne_commande";
        public static final int NUM_COL_ID_LIGNE_COMMANDE_MVT_DEPOTS = 25;
        public static final String TYPE_COL_ID_LIGNE_COMMANDE_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_PU_FACT_MVT_DEPOTS = "Pu_fact";
        public static final int NUM_COL_PU_FACT_MVT_DEPOTS = 26;
        public static final String TYPE_COL_PU_FACT_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_REPRIS_MVT_DEPOTS = "Repris";
        public static final int NUM_COL_REPRIS_MVT_DEPOTS = 27;
        public static final String TYPE_COL_REPRIS_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_QTE_MVT_MVT_DEPOTS = "Qté_Mvt";
        public static final int NUM_COL_QTE_MVT_MVT_DEPOTS = 28;
        public static final String TYPE_COL_QTE_MVT_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_QTE_RAL_MVT_DEPOTS = "Qté_RAL";
        public static final int NUM_COL_QTE_RAL_MVT_DEPOTS = 29;
        public static final String TYPE_COL_QTE_RAL_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_GRATUITS_MVT_DEPOTS = "Gratuits";
        public static final int NUM_COL_GRATUITS_MVT_DEPOTS = 30;
        public static final String TYPE_COL_GRATUITS_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL_DEVISE_MVT_DEPOTS = "Devise";
        public static final int NUM_COL_DEVISE_MVT_DEPOTS = 31;
        public static final String TYPE_COL_DEVISE_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_MVT_DEPOTS = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_MVT_DEPOTS = 32;
        public static final String TYPE_COL_SYS_DT_MAJ_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_MVT_DEPOTS = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_MVT_DEPOTS = 33;
        public static final String TYPE_COL_SYS_HEURE_MAJ_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_MVT_DEPOTS = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_MVT_DEPOTS = 34;
        public static final String TYPE_COL_SYS_USER_MAJ_MVT_DEPOTS = "TEXT";
        public static final String CLE_COL_SUIVI_PAR_LOT_MVT_DEPOTS = "Suivi_Par_Lot";
        public static final int NUM_COL_SUIVI_PAR_LOT_MVT_DEPOTS = 35;
        public static final String TYPE_COL_SUIVI_PAR_LOT_MVT_DEPOTS = "INTEGER";
        public static final String CLE_COL__UID_MVT_DEPOTS = "_UID";
        public static final int NUM_COL__UID_MVT_DEPOTS = 36;
        public static final String TYPE_COL__UID_MVT_DEPOTS = "INTEGER";

        public static final String CREATION_TABLE_MVT_DEPOTS = " CREATE TABLE       " + Constantes.TABLE_MVT_DEPOTS
                + "(" +
                DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_CODE_MVT_DEPOTS + "   " + Constantes.TYPE_COL_CODE_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_DEPOT_REFERENCE_MVT_DEPOTS + "   " + Constantes.TYPE_COL_DEPOT_REFERENCE_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_CODE_DEPOT_MVT_DEPOTS + "   " + Constantes.TYPE_COL_CODE_DEPOT_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_CODE_PRODUIT_MVT_DEPOTS + "   " + Constantes.TYPE_COL_CODE_PRODUIT_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_REFERENCE_F_MVT_DEPOTS + "   " + Constantes.TYPE_COL_REFERENCE_F_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_PU_COM_MVT_DEPOTS + "   " + Constantes.TYPE_COL_PU_COM_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_DESIGNATION_MVT_DEPOTS + "   " + Constantes.TYPE_COL_DESIGNATION_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_UNITE_MVT_DEPOTS + "   " + Constantes.TYPE_COL_UNITE_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_FOURNISSEUR_MVT_DEPOTS + "   " + Constantes.TYPE_COL_FOURNISSEUR_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_CODE_FRS_MVT_DEPOTS + "   " + Constantes.TYPE_COL_CODE_FRS_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_QTE_COM_MVT_DEPOTS + "   " + Constantes.TYPE_COL_QTE_COM_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_QTE_LIVREE_MVT_DEPOTS + "   " + Constantes.TYPE_COL_QTE_LIVREE_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_QTE_RESTE_MVT_DEPOTS + "   " + Constantes.TYPE_COL_QTE_RESTE_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_RELIQUAT_MVT_DEPOTS + "   " + Constantes.TYPE_COL_RELIQUAT_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_DATE_ENTREE_MVT_DEPOTS + "   " + Constantes.TYPE_COL_DATE_ENTREE_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_COMMANDE_MVT_DEPOTS + "   " + Constantes.TYPE_COL_COMMANDE_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_DATE_COM_MVT_DEPOTS + "   " + Constantes.TYPE_COL_DATE_COM_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_CONDI_ACHAT_MVT_DEPOTS + "   " + Constantes.TYPE_COL_CONDI_ACHAT_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_CONDI_DISTRI_MVT_DEPOTS + "   " + Constantes.TYPE_COL_CONDI_DISTRI_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_DATE_PEREMPTION_MVT_DEPOTS + "   " + Constantes.TYPE_COL_DATE_PEREMPTION_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_NUM_LOT_MVT_DEPOTS + "   " + Constantes.TYPE_COL_NUM_LOT_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_CODE_BARRE_MVT_DEPOTS + "   " + Constantes.TYPE_COL_CODE_BARRE_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_PEREMPTION_MVT_DEPOTS + "   " + Constantes.TYPE_COL_PEREMPTION_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_ID_LIGNE_COMMANDE_MVT_DEPOTS + "   " + Constantes.TYPE_COL_ID_LIGNE_COMMANDE_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_PU_FACT_MVT_DEPOTS + "   " + Constantes.TYPE_COL_PU_FACT_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_REPRIS_MVT_DEPOTS + "   " + Constantes.TYPE_COL_REPRIS_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_QTE_MVT_MVT_DEPOTS + "   " + Constantes.TYPE_COL_QTE_MVT_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_QTE_RAL_MVT_DEPOTS + "   " + Constantes.TYPE_COL_QTE_RAL_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_GRATUITS_MVT_DEPOTS + "   " + Constantes.TYPE_COL_GRATUITS_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_DEVISE_MVT_DEPOTS + "   " + Constantes.TYPE_COL_DEVISE_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_MVT_DEPOTS + "   " + Constantes.TYPE_COL_SYS_DT_MAJ_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_MVT_DEPOTS + "   " + Constantes.TYPE_COL_SYS_HEURE_MAJ_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_MVT_DEPOTS + "   " + Constantes.TYPE_COL_SYS_USER_MAJ_MVT_DEPOTS + " , "
                + Constantes.CLE_COL_SUIVI_PAR_LOT_MVT_DEPOTS + "   " + Constantes.TYPE_COL_SUIVI_PAR_LOT_MVT_DEPOTS + " , "
                + Constantes.CLE_COL__UID_MVT_DEPOTS + "   " + Constantes.TYPE_COL__UID_MVT_DEPOTS
                + " ); ";

    }
}
