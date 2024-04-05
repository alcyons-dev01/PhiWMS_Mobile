package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by quentinlanusse on 27/06/2017.
 */

public class FactureOpenHelper extends DBOpenHelper {

    public FactureOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class Constantes implements BaseColumns {

        public static final String TABLE_FACTURE = "Facture";

        public static final String CLE_COL_NUMERO_FACTURE = "Numéro";
        public static final int NUM_COL_NUMERO_FACTURE = 1;
        public static final String TYPE_COL_NUMERO_FACTURE = "TEXT";
        public static final String CLE_COL_FACT_HT_FACTURE = "Fact_HT";
        public static final int NUM_COL_FACT_HT_FACTURE = 2;
        public static final String TYPE_COL_FACT_HT_FACTURE = "REAL";
        public static final String CLE_COL_FACT_TVA_FACTURE = "Fact_TVA";
        public static final int NUM_COL_FACT_TVA_FACTURE = 3;
        public static final String TYPE_COL_FACT_TVA_FACTURE = "REAL";
        public static final String CLE_COL_FACT_TTC_FACTURE = "Fact_TTC";
        public static final int NUM_COL_FACT_TTC_FACTURE = 4;
        public static final String TYPE_COL_FACT_TTC_FACTURE = "REAL";
        public static final String CLE_COL_COM_HT_FACTURE = "Com_HT";
        public static final int NUM_COL_COM_HT_FACTURE = 5;
        public static final String TYPE_COL_COM_HT_FACTURE = "REAL";
        public static final String CLE_COL_COM_TVA_FACTURE = "Com_TVA";
        public static final int NUM_COL_COM_TVA_FACTURE = 6;
        public static final String TYPE_COL_COM_TVA_FACTURE = "REAL";
        public static final String CLE_COL_COM_TTC_FACTURE = "Com_TTC";
        public static final int NUM_COL_COM_TTC_FACTURE = 7;
        public static final String TYPE_COL_COM_TTC_FACTURE = "REAL";
        public static final String CLE_COL_CODE_FOURNISSEU_FACTURE = "Code_fournisseu";
        public static final int NUM_COL_CODE_FOURNISSEU_FACTURE = 8;
        public static final String TYPE_COL_CODE_FOURNISSEU_FACTURE = "INTEGER";
        public static final String CLE_COL_FOURNISSEUR_FACTURE = "Fournisseur";
        public static final int NUM_COL_FOURNISSEUR_FACTURE = 9;
        public static final String TYPE_COL_FOURNISSEUR_FACTURE = "TEXT";
        public static final String CLE_COL_DATE_FACT_FACTURE = "Date_fact";
        public static final int NUM_COL_DATE_FACT_FACTURE = 10;
        public static final String TYPE_COL_DATE_FACT_FACTURE = "TEXT";
        public static final String CLE_COL_ECHEANCE_FACTURE = "Echéance";
        public static final int NUM_COL_ECHEANCE_FACTURE = 11;
        public static final String TYPE_COL_ECHEANCE_FACTURE = "TEXT";
        public static final String CLE_COL_SOLDE_FACTURE = "Solde";
        public static final int NUM_COL_SOLDE_FACTURE = 12;
        public static final String TYPE_COL_SOLDE_FACTURE = "REAL";
        public static final String CLE_COL_SOLDEE_LE_FACTURE = "Soldée_le";
        public static final int NUM_COL_SOLDEE_LE_FACTURE = 13;
        public static final String TYPE_COL_SOLDEE_LE_FACTURE = "TEXT";
        public static final String CLE_COL_STATUT_FACTURE = "Statut";
        public static final int NUM_COL_STATUT_FACTURE = 14;
        public static final String TYPE_COL_STATUT_FACTURE = "TEXT";
        public static final String CLE_COL_ECART_I_HT_FACTURE = "Ecart_I_HT";
        public static final int NUM_COL_ECART_I_HT_FACTURE = 15;
        public static final String TYPE_COL_ECART_I_HT_FACTURE = "REAL";
        public static final String CLE_COL_ECART_I_TVA_FACTURE = "Ecart_I_TVA";
        public static final int NUM_COL_ECART_I_TVA_FACTURE = 16;
        public static final String TYPE_COL_ECART_I_TVA_FACTURE = "REAL";
        public static final String CLE_COL_ECART_I_TTC_FACTURE = "Ecart_I_TTC";
        public static final int NUM_COL_ECART_I_TTC_FACTURE = 17;
        public static final String TYPE_COL_ECART_I_TTC_FACTURE = "REAL";
        public static final String CLE_COL_CALC_HT_FACTURE = "Calc_HT";
        public static final int NUM_COL_CALC_HT_FACTURE = 18;
        public static final String TYPE_COL_CALC_HT_FACTURE = "REAL";
        public static final String CLE_COL_CALC_TVA_FACTURE = "Calc_TVA";
        public static final int NUM_COL_CALC_TVA_FACTURE = 19;
        public static final String TYPE_COL_CALC_TVA_FACTURE = "REAL";
        public static final String CLE_COL_CALC_TTC_FACTURE = "Calc_TTC";
        public static final int NUM_COL_CALC_TTC_FACTURE = 20;
        public static final String TYPE_COL_CALC_TTC_FACTURE = "REAL";
        public static final String CLE_COL_FRAIS_HT_FACTURE = "Frais_HT";
        public static final int NUM_COL_FRAIS_HT_FACTURE = 21;
        public static final String TYPE_COL_FRAIS_HT_FACTURE = "REAL";
        public static final String CLE_COL_FRAIS_TVA_FACTURE = "Frais_TVA";
        public static final int NUM_COL_FRAIS_TVA_FACTURE = 22;
        public static final String TYPE_COL_FRAIS_TVA_FACTURE = "REAL";
        public static final String CLE_COL_FRAIS_TTC_FACTURE = "Frais_TTC";
        public static final int NUM_COL_FRAIS_TTC_FACTURE = 23;
        public static final String TYPE_COL_FRAIS_TTC_FACTURE = "REAL";
        public static final String CLE_COL_DATE_COMPTABLE_FACTURE = "date_comptable";
        public static final int NUM_COL_DATE_COMPTABLE_FACTURE = 24;
        public static final String TYPE_COL_DATE_COMPTABLE_FACTURE = "TEXT";
        public static final String CLE_COL_DATE_ECRITURE_FACTURE = "Date_ecriture";
        public static final int NUM_COL_DATE_ECRITURE_FACTURE = 25;
        public static final String TYPE_COL_DATE_ECRITURE_FACTURE = "TEXT";
        public static final String CLE_COL_PIECE_FACTURE = "Piece";
        public static final int NUM_COL_PIECE_FACTURE = 26;
        public static final String TYPE_COL_PIECE_FACTURE = "TEXT";
        public static final String CLE_COL_DEVISE_FACTURE = "Devise";
        public static final int NUM_COL_DEVISE_FACTURE = 27;
        public static final String TYPE_COL_DEVISE_FACTURE = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_FACTURE = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_FACTURE = 28;
        public static final String TYPE_COL_SYS_DT_MAJ_FACTURE = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_FACTURE = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_FACTURE = 29;
        public static final String TYPE_COL_SYS_HEURE_MAJ_FACTURE = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_FACTURE = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_FACTURE = 30;
        public static final String TYPE_COL_SYS_USER_MAJ_FACTURE = "TEXT";
        public static final String CLE_COL_MODE_REGLEMENT_FACTURE = "Mode_reglement";
        public static final int NUM_COL_MODE_REGLEMENT_FACTURE = 31;
        public static final String TYPE_COL_MODE_REGLEMENT_FACTURE = "TEXT";
        public static final String CLE_COL_PHARMACIE_FACTURE = "Pharmacie";
        public static final int NUM_COL_PHARMACIE_FACTURE = 32;
        public static final String TYPE_COL_PHARMACIE_FACTURE = "INTEGER";
        public static final String CLE_COL_LITIGE_FACTURE = "Litige";
        public static final int NUM_COL_LITIGE_FACTURE = 33;
        public static final String TYPE_COL_LITIGE_FACTURE = "INTEGER";
        public static final String CLE_COL_TRANSMISE_FACTURE = "Transmise";
        public static final int NUM_COL_TRANSMISE_FACTURE = 34;
        public static final String TYPE_COL_TRANSMISE_FACTURE = "INTEGER";
        public static final String CLE_COL_NUM_SEMAINE_FACTURE = "Num_Semaine";
        public static final int NUM_COL_NUM_SEMAINE_FACTURE = 35;
        public static final String TYPE_COL_NUM_SEMAINE_FACTURE = "INTEGER";
        public static final String CLE_COL_RECEPTION_DATE_FACTURE = "Reception_Date";
        public static final int NUM_COL_RECEPTION_DATE_FACTURE = 36;
        public static final String TYPE_COL_RECEPTION_DATE_FACTURE = "TEXT";
        public static final String CLE_COL_BORDEREAU_UID_FACTURE = "Bordereau_UID";
        public static final int NUM_COL_BORDEREAU_UID_FACTURE = 37;
        public static final String TYPE_COL_BORDEREAU_UID_FACTURE = "INTEGER";
        public static final String CLE_COL_IMPORT_FACTURE = "Import";
        public static final int NUM_COL_IMPORT_FACTURE = 38;
        public static final String TYPE_COL_IMPORT_FACTURE = "INTEGER";
        public static final String CLE_COL_COMMANDENUMERO_FACTURE = "CommandeNumero";
        public static final int NUM_COL_COMMANDENUMERO_FACTURE = 39;
        public static final String TYPE_COL_COMMANDENUMERO_FACTURE = "TEXT";
        public static final String CLE_COL_ID_FACTURE_FACTURE = "ID_Facture";
        public static final int NUM_COL_ID_FACTURE_FACTURE = 40;
        public static final String TYPE_COL_ID_FACTURE_FACTURE = "INTEGER";


        public static final String CREATION_TABLE_FACTURE = "CREATE TABLE "
                + Constantes.TABLE_FACTURE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_NUMERO_FACTURE + " " + Constantes.TYPE_COL_NUMERO_FACTURE + " ,"
                + Constantes.CLE_COL_FACT_HT_FACTURE + " " + Constantes.TYPE_COL_FACT_HT_FACTURE + " ,"
                + Constantes.CLE_COL_FACT_TVA_FACTURE + " " + Constantes.TYPE_COL_FACT_TVA_FACTURE + " ,"
                + Constantes.CLE_COL_FACT_TTC_FACTURE + " " + Constantes.TYPE_COL_FACT_TTC_FACTURE + " ,"
                + Constantes.CLE_COL_COM_HT_FACTURE + " " + Constantes.TYPE_COL_COM_HT_FACTURE + " ,"
                + Constantes.CLE_COL_COM_TVA_FACTURE + " " + Constantes.TYPE_COL_COM_TVA_FACTURE + " ,"
                + Constantes.CLE_COL_COM_TTC_FACTURE + " " + Constantes.TYPE_COL_COM_TTC_FACTURE + " ,"
                + Constantes.CLE_COL_CODE_FOURNISSEU_FACTURE + " " + Constantes.TYPE_COL_CODE_FOURNISSEU_FACTURE + " ,"
                + Constantes.CLE_COL_FOURNISSEUR_FACTURE + " " + Constantes.TYPE_COL_FOURNISSEUR_FACTURE + " ,"
                + Constantes.CLE_COL_DATE_FACT_FACTURE + " " + Constantes.TYPE_COL_DATE_FACT_FACTURE + " ,"
                + Constantes.CLE_COL_ECHEANCE_FACTURE + " " + Constantes.TYPE_COL_ECHEANCE_FACTURE + " ,"
                + Constantes.CLE_COL_SOLDE_FACTURE + " " + Constantes.TYPE_COL_SOLDE_FACTURE + " ,"
                + Constantes.CLE_COL_SOLDEE_LE_FACTURE + " " + Constantes.TYPE_COL_SOLDEE_LE_FACTURE + " ,"
                + Constantes.CLE_COL_STATUT_FACTURE + " " + Constantes.TYPE_COL_STATUT_FACTURE + " ,"
                + Constantes.CLE_COL_ECART_I_HT_FACTURE + " " + Constantes.TYPE_COL_ECART_I_HT_FACTURE + " ,"
                + Constantes.CLE_COL_ECART_I_TVA_FACTURE + " " + Constantes.TYPE_COL_ECART_I_TVA_FACTURE + " ,"
                + Constantes.CLE_COL_ECART_I_TTC_FACTURE + " " + Constantes.TYPE_COL_ECART_I_TTC_FACTURE + " ,"
                + Constantes.CLE_COL_CALC_HT_FACTURE + " " + Constantes.TYPE_COL_CALC_HT_FACTURE + " ,"
                + Constantes.CLE_COL_CALC_TVA_FACTURE + " " + Constantes.TYPE_COL_CALC_TVA_FACTURE + " ,"
                + Constantes.CLE_COL_CALC_TTC_FACTURE + " " + Constantes.TYPE_COL_CALC_TTC_FACTURE + " ,"
                + Constantes.CLE_COL_FRAIS_HT_FACTURE + " " + Constantes.TYPE_COL_FRAIS_HT_FACTURE + " ,"
                + Constantes.CLE_COL_FRAIS_TVA_FACTURE + " " + Constantes.TYPE_COL_FRAIS_TVA_FACTURE + " ,"
                + Constantes.CLE_COL_FRAIS_TTC_FACTURE + " " + Constantes.TYPE_COL_FRAIS_TTC_FACTURE + " ,"
                + Constantes.CLE_COL_DATE_COMPTABLE_FACTURE + " " + Constantes.TYPE_COL_DATE_COMPTABLE_FACTURE + " ,"
                + Constantes.CLE_COL_DATE_ECRITURE_FACTURE + " " + Constantes.TYPE_COL_DATE_ECRITURE_FACTURE + " ,"
                + Constantes.CLE_COL_PIECE_FACTURE + " " + Constantes.TYPE_COL_PIECE_FACTURE + " ,"
                + Constantes.CLE_COL_DEVISE_FACTURE + " " + Constantes.TYPE_COL_DEVISE_FACTURE + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_FACTURE + " " + Constantes.TYPE_COL_SYS_DT_MAJ_FACTURE + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_FACTURE + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_FACTURE + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_FACTURE + " " + Constantes.TYPE_COL_SYS_USER_MAJ_FACTURE + " ,"
                + Constantes.CLE_COL_MODE_REGLEMENT_FACTURE + " " + Constantes.TYPE_COL_MODE_REGLEMENT_FACTURE + " ,"
                + Constantes.CLE_COL_PHARMACIE_FACTURE + " " + Constantes.TYPE_COL_PHARMACIE_FACTURE + " ,"
                + Constantes.CLE_COL_LITIGE_FACTURE + " " + Constantes.TYPE_COL_LITIGE_FACTURE + " ,"
                + Constantes.CLE_COL_TRANSMISE_FACTURE + " " + Constantes.TYPE_COL_TRANSMISE_FACTURE + " ,"
                + Constantes.CLE_COL_NUM_SEMAINE_FACTURE + " " + Constantes.TYPE_COL_NUM_SEMAINE_FACTURE + " ,"
                + Constantes.CLE_COL_RECEPTION_DATE_FACTURE + " " + Constantes.TYPE_COL_RECEPTION_DATE_FACTURE + " ,"
                + Constantes.CLE_COL_BORDEREAU_UID_FACTURE + " " + Constantes.TYPE_COL_BORDEREAU_UID_FACTURE + " ,"
                + Constantes.CLE_COL_IMPORT_FACTURE + " " + Constantes.TYPE_COL_IMPORT_FACTURE + " ,"
                + Constantes.CLE_COL_COMMANDENUMERO_FACTURE + " " + Constantes.TYPE_COL_COMMANDENUMERO_FACTURE + ","
                + Constantes.CLE_COL_ID_FACTURE_FACTURE + " " + Constantes.TYPE_COL_ID_FACTURE_FACTURE
                + ");";
    }
}
