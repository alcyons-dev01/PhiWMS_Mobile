package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class Photo_Stock_EtablissementOpenHelper extends DBOpenHelper {

    public Photo_Stock_EtablissementOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class Constantes implements BaseColumns {

        public static final String TABLE_PHOTO_STOCK_ETABLISSEMENT = "Photo_stock_etablissement";

        public static final String CLE_COL_ANNEE_PHOTO_STOCK_ETABLISSEMENT = "Annee";
        public static final int NUM_COL_ANNEE_PHOTO_STOCK_ETABLISSEMENT = 1;
        public static final String TYPE_COL_ANNEE_PHOTO_STOCK_ETABLISSEMENT = "INTEGER";
        public static final String CLE_COL_MOIS_PHOTO_STOCK_ETABLISSEMENT = "Mois";
        public static final int NUM_COL_MOIS_PHOTO_STOCK_ETABLISSEMENT = 2;
        public static final String TYPE_COL_MOIS_PHOTO_STOCK_ETABLISSEMENT = "INTEGER";
        public static final String CLE_COL_REF_CYCLE_PHOTO_STOCK_ETABLISSEMENT = "Ref_Cycle";
        public static final int NUM_COL_REF_CYCLE_PHOTO_STOCK_ETABLISSEMENT = 3;
        public static final String TYPE_COL_REF_CYCLE_PHOTO_STOCK_ETABLISSEMENT = "TEXT";
        public static final String CLE_COL_STOCK_DEBUT_PHOTO_STOCK_ETABLISSEMENT = "STOCK_DEBUT";
        public static final int NUM_COL_STOCK_DEBUT_PHOTO_STOCK_ETABLISSEMENT = 4;
        public static final String TYPE_COL_STOCK_DEBUT_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_ENTREE_CDE_PHOTO_STOCK_ETABLISSEMENT = "QTE_ENTREE_CDE";
        public static final int NUM_COL_QTE_ENTREE_CDE_PHOTO_STOCK_ETABLISSEMENT = 5;
        public static final String TYPE_COL_QTE_ENTREE_CDE_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_ETABLISSEMENT = "QTE_SORTIE_DELIV";
        public static final int NUM_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_ETABLISSEMENT = 6;
        public static final String TYPE_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_ETABLISSEMENT = "QTE_ENTREE_DELIV";
        public static final int NUM_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_ETABLISSEMENT = 7;
        public static final String TYPE_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_ETABLISSEMENT = "QTE_SORTIE_RETOUR";
        public static final int NUM_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_ETABLISSEMENT = 8;
        public static final String TYPE_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_ETABLISSEMENT = "QTE_ENTREE_RETOUR";
        public static final int NUM_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_ETABLISSEMENT = 9;
        public static final String TYPE_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_ETABLISSEMENT = "QTE_SORTIE_REGUL";
        public static final int NUM_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_ETABLISSEMENT = 10;
        public static final String TYPE_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_ETABLISSEMENT = "QTE_ENTREE_REGUL";
        public static final int NUM_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_ETABLISSEMENT = 11;
        public static final String TYPE_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_SORTIE_ECART_PHOTO_STOCK_ETABLISSEMENT = "QTE_SORTIE_ECART";
        public static final int NUM_COL_QTE_SORTIE_ECART_PHOTO_STOCK_ETABLISSEMENT = 12;
        public static final String TYPE_COL_QTE_SORTIE_ECART_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_ENTREE_ECART_PHOTO_STOCK_ETABLISSEMENT = "QTE_ENTREE_ECART";
        public static final int NUM_COL_QTE_ENTREE_ECART_PHOTO_STOCK_ETABLISSEMENT = 13;
        public static final String TYPE_COL_QTE_ENTREE_ECART_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_STOCK_FIN_PHOTO_STOCK_ETABLISSEMENT = "STOCK_FIN";
        public static final int NUM_COL_STOCK_FIN_PHOTO_STOCK_ETABLISSEMENT = 14;
        public static final String TYPE_COL_STOCK_FIN_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_ETABLISSEMENT = "VALEUR_STOCK_FIN";
        public static final int NUM_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_ETABLISSEMENT = 15;
        public static final String TYPE_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_ETABLISSEMENT = "VALEUR_STOCK_DEBUT";
        public static final int NUM_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_ETABLISSEMENT = 16;
        public static final String TYPE_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CATEGORIE_PHOTO_STOCK_ETABLISSEMENT = "CATEGORIE";
        public static final int NUM_COL_CATEGORIE_PHOTO_STOCK_ETABLISSEMENT = 17;
        public static final String TYPE_COL_CATEGORIE_PHOTO_STOCK_ETABLISSEMENT = "TEXT";
        public static final String CLE_COL_REFERENCE_PHOTO_STOCK_ETABLISSEMENT = "REFERENCE";
        public static final int NUM_COL_REFERENCE_PHOTO_STOCK_ETABLISSEMENT = 18;
        public static final String TYPE_COL_REFERENCE_PHOTO_STOCK_ETABLISSEMENT = "TEXT";
        public static final String CLE_COL_DESIGNATION_PHOTO_STOCK_ETABLISSEMENT = "DESIGNATION";
        public static final int NUM_COL_DESIGNATION_PHOTO_STOCK_ETABLISSEMENT = 19;
        public static final String TYPE_COL_DESIGNATION_PHOTO_STOCK_ETABLISSEMENT = "TEXT";
        public static final String CLE_COL_PRIX_UNIT_HT_PHOTO_STOCK_ETABLISSEMENT = "PRIX_UNIT_HT";
        public static final int NUM_COL_PRIX_UNIT_HT_PHOTO_STOCK_ETABLISSEMENT = 20;
        public static final String TYPE_COL_PRIX_UNIT_HT_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_ETABLISSEMENT = "Consommation_mensuelle";
        public static final int NUM_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_ETABLISSEMENT = 21;
        public static final String TYPE_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_ENTREE_PHOTO_STOCK_ETABLISSEMENT = "Qte_Entree";
        public static final int NUM_COL_QTE_ENTREE_PHOTO_STOCK_ETABLISSEMENT = 22;
        public static final String TYPE_COL_QTE_ENTREE_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_QTE_SORTIE_PHOTO_STOCK_ETABLISSEMENT = "Qte_Sortie";
        public static final int NUM_COL_QTE_SORTIE_PHOTO_STOCK_ETABLISSEMENT = 23;
        public static final String TYPE_COL_QTE_SORTIE_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_ETABLISSEMENT = "Consommation_Trimestrielle";
        public static final int NUM_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_ETABLISSEMENT = 24;
        public static final String TYPE_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSO_JOURNALIERE_PHOTO_STOCK_ETABLISSEMENT = "Conso_Journaliere";
        public static final int NUM_COL_CONSO_JOURNALIERE_PHOTO_STOCK_ETABLISSEMENT = 25;
        public static final String TYPE_COL_CONSO_JOURNALIERE_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSO_Q1_PHOTO_STOCK_ETABLISSEMENT = "Conso_Q1";
        public static final int NUM_COL_CONSO_Q1_PHOTO_STOCK_ETABLISSEMENT = 26;
        public static final String TYPE_COL_CONSO_Q1_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSO_Q2_PHOTO_STOCK_ETABLISSEMENT = "Conso_Q2";
        public static final int NUM_COL_CONSO_Q2_PHOTO_STOCK_ETABLISSEMENT = 27;
        public static final String TYPE_COL_CONSO_Q2_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSO_Q3_PHOTO_STOCK_ETABLISSEMENT = "Conso_Q3";
        public static final int NUM_COL_CONSO_Q3_PHOTO_STOCK_ETABLISSEMENT = 28;
        public static final String TYPE_COL_CONSO_Q3_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSO_Q4_PHOTO_STOCK_ETABLISSEMENT = "Conso_Q4";
        public static final int NUM_COL_CONSO_Q4_PHOTO_STOCK_ETABLISSEMENT = 29;
        public static final String TYPE_COL_CONSO_Q4_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSO_S1_PHOTO_STOCK_ETABLISSEMENT = "Conso_S1";
        public static final int NUM_COL_CONSO_S1_PHOTO_STOCK_ETABLISSEMENT = 30;
        public static final String TYPE_COL_CONSO_S1_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSO_S2_PHOTO_STOCK_ETABLISSEMENT = "Conso_S2";
        public static final int NUM_COL_CONSO_S2_PHOTO_STOCK_ETABLISSEMENT = 31;
        public static final String TYPE_COL_CONSO_S2_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSO_S3_PHOTO_STOCK_ETABLISSEMENT = "Conso_S3";
        public static final int NUM_COL_CONSO_S3_PHOTO_STOCK_ETABLISSEMENT = 32;
        public static final String TYPE_COL_CONSO_S3_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSO_S4_PHOTO_STOCK_ETABLISSEMENT = "Conso_S4";
        public static final int NUM_COL_CONSO_S4_PHOTO_STOCK_ETABLISSEMENT = 33;
        public static final String TYPE_COL_CONSO_S4_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_CONSO_S5_PHOTO_STOCK_ETABLISSEMENT = "Conso_S5";
        public static final int NUM_COL_CONSO_S5_PHOTO_STOCK_ETABLISSEMENT = 34;
        public static final String TYPE_COL_CONSO_S5_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_RIEN_PHOTO_STOCK_ETABLISSEMENT = "Rien";
        public static final int NUM_COL_RIEN_PHOTO_STOCK_ETABLISSEMENT = 35;
        public static final String TYPE_COL_RIEN_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL_PMP_TTC_PHOTO_STOCK_ETABLISSEMENT = "PMP_TTC";
        public static final int NUM_COL_PMP_TTC_PHOTO_STOCK_ETABLISSEMENT = 36;
        public static final String TYPE_COL_PMP_TTC_PHOTO_STOCK_ETABLISSEMENT = "REAL";
        public static final String CLE_COL__UID_PHOTO_STOCK_ETABLISSEMENT = "_UID";
        public static final int NUM_COL__UID_PHOTO_STOCK_ETABLISSEMENT = 37;
        public static final String TYPE_COL__UID_PHOTO_STOCK_ETABLISSEMENT = "INTEGER";
        public static final String CLE_COL_QTE_COMMANDE_PHOTO_STOCK_ETABLISSEMENT = "QTE_COMMANDE";
        public static final int NUM_COL_QTE_COMMANDE_PHOTO_STOCK_ETABLISSEMENT = 38;
        public static final String TYPE_COL_QTE_COMMANDE_PHOTO_STOCK_ETABLISSEMENT = "INTEGER";
        public static final String CLE_COL_QTE_RAF_PHOTO_STOCK_ETABLISSEMENT = "QTE_RAF";
        public static final int NUM_COL_QTE_RAF_PHOTO_STOCK_ETABLISSEMENT = 39;
        public static final String TYPE_COL_QTE_RAF_PHOTO_STOCK_ETABLISSEMENT = "INTEGER";
        public static final String CLE_COL_CODE_PRODUIT_PHOTO_STOCK_ETABLISSEMENT = "Code_Produit";
        public static final int NUM_COL_CODE_PRODUIT_PHOTO_STOCK_ETABLISSEMENT = 40;
        public static final String TYPE_COL_CODE_PRODUIT_PHOTO_STOCK_ETABLISSEMENT = "INTEGER";

        public static final String CREATION_TABLE_PHOTO_STOCK_ETABLISSEMENT = "CREATE TABLE " + Constantes.TABLE_PHOTO_STOCK_ETABLISSEMENT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_ANNEE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_ANNEE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_MOIS_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_MOIS_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_REF_CYCLE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_REF_CYCLE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_STOCK_DEBUT_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_STOCK_DEBUT_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_CDE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_ENTREE_CDE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_ECART_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_SORTIE_ECART_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_ECART_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_ENTREE_ECART_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_STOCK_FIN_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_STOCK_FIN_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CATEGORIE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CATEGORIE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_REFERENCE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_REFERENCE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_DESIGNATION_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_DESIGNATION_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_PRIX_UNIT_HT_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_PRIX_UNIT_HT_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_ENTREE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_SORTIE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSO_JOURNALIERE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSO_JOURNALIERE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSO_Q1_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSO_Q1_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSO_Q2_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSO_Q2_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSO_Q3_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSO_Q3_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSO_Q4_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSO_Q4_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSO_S1_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSO_S1_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSO_S2_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSO_S2_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSO_S3_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSO_S3_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSO_S4_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSO_S4_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_CONSO_S5_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CONSO_S5_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_RIEN_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_RIEN_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_PMP_TTC_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_PMP_TTC_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL__UID_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL__UID_PHOTO_STOCK_ETABLISSEMENT + ","
                + Constantes.CLE_COL_QTE_COMMANDE_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_COMMANDE_PHOTO_STOCK_ETABLISSEMENT + " ,"
                + Constantes.CLE_COL_QTE_RAF_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_QTE_RAF_PHOTO_STOCK_ETABLISSEMENT + ","
                + Constantes.CLE_COL_CODE_PRODUIT_PHOTO_STOCK_ETABLISSEMENT + " " + Constantes.TYPE_COL_CODE_PRODUIT_PHOTO_STOCK_ETABLISSEMENT
                + ");";
    }
}
