package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import fr.alcyons.phiwms_mobile.Classes.Frequences;

/**
 * Created by olivier on 12/03/2018.
 */

public class FrequencesOpenHelper extends DBOpenHelper {

    public FrequencesOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static Frequences getFrequencesByIdent(SQLiteDatabase db, String ident) {
        Frequences objet = null;

        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_FREQUENCES + " WHERE " + Constantes.CLE_COL_IDENT_FREQUENCES + "=? ", new String[]{ident});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            objet = new Frequences(cursor);
        }

        return objet;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_FREQUENCES = "Frequences";
        public static final String CLE_COL__UID_FREQUENCES = "_UID";
        public static final int NUM_COL__UID_FREQUENCES = 1;
        public static final String TYPE_COL__UID_FREQUENCES = "INTEGER";
        public static final String CLE_COL_IDENT_FREQUENCES = "ident";
        public static final int NUM_COL_IDENT_FREQUENCES = 2;
        public static final String TYPE_COL_IDENT_FREQUENCES = "TEXT";
        public static final String CLE_COL_CODAGE_FREQUENCES = "Codage";
        public static final int NUM_COL_CODAGE_FREQUENCES = 3;
        public static final String TYPE_COL_CODAGE_FREQUENCES = "INTEGER";
        public static final String CLE_COL_L1_FREQUENCES = "L1";
        public static final int NUM_COL_L1_FREQUENCES = 4;
        public static final String TYPE_COL_L1_FREQUENCES = "INTEGER";
        public static final String CLE_COL_MA_FREQUENCES = "Ma";
        public static final int NUM_COL_MA_FREQUENCES = 5;
        public static final String TYPE_COL_MA_FREQUENCES = "INTEGER";
        public static final String CLE_COL_MER_FREQUENCES = "Mer";
        public static final int NUM_COL_MER_FREQUENCES = 6;
        public static final String TYPE_COL_MER_FREQUENCES = "INTEGER";
        public static final String CLE_COL_J_FREQUENCES = "J";
        public static final int NUM_COL_J_FREQUENCES = 7;
        public static final String TYPE_COL_J_FREQUENCES = "INTEGER";
        public static final String CLE_COL_V_FREQUENCES = "V";
        public static final int NUM_COL_V_FREQUENCES = 8;
        public static final String TYPE_COL_V_FREQUENCES = "INTEGER";
        public static final String CLE_COL_S_FREQUENCES = "S";
        public static final int NUM_COL_S_FREQUENCES = 9;
        public static final String TYPE_COL_S_FREQUENCES = "INTEGER";
        public static final String CLE_COL_D_FREQUENCES = "D";
        public static final int NUM_COL_D_FREQUENCES = 10;
        public static final String TYPE_COL_D_FREQUENCES = "INTEGER";
        public static final String CLE_COL_L2_FREQUENCES = "L2";
        public static final int NUM_COL_L2_FREQUENCES = 11;
        public static final String TYPE_COL_L2_FREQUENCES = "INTEGER";
        public static final String CLE_COL_COMMENTAIRE_FREQUENCES = "Commentaire";
        public static final int NUM_COL_COMMENTAIRE_FREQUENCES = 12;
        public static final String TYPE_COL_COMMENTAIRE_FREQUENCES = "TEXT";
        public static final String CLE_COL_01_FREQUENCES = "_01";
        public static final int NUM_COL_01_FREQUENCES = 13;
        public static final String TYPE_COL_01_FREQUENCES = "INTEGER";
        public static final String CLE_COL_02_FREQUENCES = "_02";
        public static final int NUM_COL_02_FREQUENCES = 14;
        public static final String TYPE_COL_02_FREQUENCES = "INTEGER";
        public static final String CLE_COL_03_FREQUENCES = "_03";
        public static final int NUM_COL_03_FREQUENCES = 15;
        public static final String TYPE_COL_03_FREQUENCES = "INTEGER";
        public static final String CLE_COL_04_FREQUENCES = "_04";
        public static final int NUM_COL_04_FREQUENCES = 16;
        public static final String TYPE_COL_04_FREQUENCES = "INTEGER";
        public static final String CLE_COL_05_FREQUENCES = "_05";
        public static final int NUM_COL_05_FREQUENCES = 17;
        public static final String TYPE_COL_05_FREQUENCES = "INTEGER";
        public static final String CLE_COL_06_FREQUENCES = "_06";
        public static final int NUM_COL_06_FREQUENCES = 18;
        public static final String TYPE_COL_06_FREQUENCES = "INTEGER";
        public static final String CLE_COL_07_FREQUENCES = "_07";
        public static final int NUM_COL_07_FREQUENCES = 19;
        public static final String TYPE_COL_07_FREQUENCES = "INTEGER";
        public static final String CLE_COL_08_FREQUENCES = "_08";
        public static final int NUM_COL_08_FREQUENCES = 20;
        public static final String TYPE_COL_08_FREQUENCES = "INTEGER";
        public static final String CLE_COL_09_FREQUENCES = "_09";
        public static final int NUM_COL_09_FREQUENCES = 21;
        public static final String TYPE_COL_09_FREQUENCES = "INTEGER";
        public static final String CLE_COL_10_FREQUENCES = "_10";
        public static final int NUM_COL_10_FREQUENCES = 22;
        public static final String TYPE_COL_10_FREQUENCES = "INTEGER";
        public static final String CLE_COL_11_FREQUENCES = "_11";
        public static final int NUM_COL_11_FREQUENCES = 23;
        public static final String TYPE_COL_11_FREQUENCES = "INTEGER";
        public static final String CLE_COL_12_FREQUENCES = "_12";
        public static final int NUM_COL_12_FREQUENCES = 24;
        public static final String TYPE_COL_12_FREQUENCES = "INTEGER";
        public static final String CLE_COL_SYS_DT_MAJ_FREQUENCES = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_FREQUENCES = 25;
        public static final String TYPE_COL_SYS_DT_MAJ_FREQUENCES = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_FREQUENCES = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_FREQUENCES = 26;
        public static final String TYPE_COL_SYS_HEURE_MAJ_FREQUENCES = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_FREQUENCES = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_FREQUENCES = 27;
        public static final String TYPE_COL_SYS_USER_MAJ_FREQUENCES = "TEXT";

        public static final String CREATION_TABLE_FREQUENCES = "CREATE TABLE " +
                Constantes.TABLE_FREQUENCES +
                "(" +
                DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY, " +
                Constantes.CLE_COL__UID_FREQUENCES + " " + Constantes.TYPE_COL__UID_FREQUENCES + ", " +
                Constantes.CLE_COL_IDENT_FREQUENCES + " " + Constantes.TYPE_COL_IDENT_FREQUENCES + ", " +
                Constantes.CLE_COL_CODAGE_FREQUENCES + " " + Constantes.TYPE_COL_CODAGE_FREQUENCES + ", " +
                Constantes.CLE_COL_L1_FREQUENCES + " " + Constantes.TYPE_COL_L1_FREQUENCES + ", " +
                Constantes.CLE_COL_MA_FREQUENCES + " " + Constantes.TYPE_COL_MA_FREQUENCES + ", " +
                Constantes.CLE_COL_MER_FREQUENCES + " " + Constantes.TYPE_COL_MER_FREQUENCES + ", " +
                Constantes.CLE_COL_J_FREQUENCES + " " + Constantes.TYPE_COL_J_FREQUENCES + ", " +
                Constantes.CLE_COL_V_FREQUENCES + " " + Constantes.TYPE_COL_V_FREQUENCES + ", " +
                Constantes.CLE_COL_S_FREQUENCES + " " + Constantes.TYPE_COL_S_FREQUENCES + ", " +
                Constantes.CLE_COL_D_FREQUENCES + " " + Constantes.TYPE_COL_D_FREQUENCES + ", " +
                Constantes.CLE_COL_L2_FREQUENCES + " " + Constantes.TYPE_COL_L2_FREQUENCES + ", " +
                Constantes.CLE_COL_COMMENTAIRE_FREQUENCES + " " + Constantes.TYPE_COL_COMMENTAIRE_FREQUENCES + ", " +
                Constantes.CLE_COL_01_FREQUENCES + " " + Constantes.TYPE_COL_01_FREQUENCES + ", " +
                Constantes.CLE_COL_02_FREQUENCES + " " + Constantes.TYPE_COL_02_FREQUENCES + ", " +
                Constantes.CLE_COL_03_FREQUENCES + " " + Constantes.TYPE_COL_03_FREQUENCES + ", " +
                Constantes.CLE_COL_04_FREQUENCES + " " + Constantes.TYPE_COL_04_FREQUENCES + ", " +
                Constantes.CLE_COL_05_FREQUENCES + " " + Constantes.TYPE_COL_05_FREQUENCES + ", " +
                Constantes.CLE_COL_06_FREQUENCES + " " + Constantes.TYPE_COL_06_FREQUENCES + ", " +
                Constantes.CLE_COL_07_FREQUENCES + " " + Constantes.TYPE_COL_07_FREQUENCES + ", " +
                Constantes.CLE_COL_08_FREQUENCES + " " + Constantes.TYPE_COL_08_FREQUENCES + ", " +
                Constantes.CLE_COL_09_FREQUENCES + " " + Constantes.TYPE_COL_09_FREQUENCES + ", " +
                Constantes.CLE_COL_10_FREQUENCES + " " + Constantes.TYPE_COL_10_FREQUENCES + ", " +
                Constantes.CLE_COL_11_FREQUENCES + " " + Constantes.TYPE_COL_11_FREQUENCES + ", " +
                Constantes.CLE_COL_12_FREQUENCES + " " + Constantes.TYPE_COL_12_FREQUENCES + ", " +
                Constantes.CLE_COL_SYS_DT_MAJ_FREQUENCES + " " + Constantes.TYPE_COL_SYS_DT_MAJ_FREQUENCES + ", " +
                Constantes.CLE_COL_SYS_HEURE_MAJ_FREQUENCES + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_FREQUENCES + ", " +
                Constantes.CLE_COL_SYS_USER_MAJ_FREQUENCES + " " + Constantes.TYPE_COL_SYS_USER_MAJ_FREQUENCES +
                " ); ";


    }
}
