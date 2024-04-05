package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.PH_Reassort;

/**
 * Created by jessica on 02/10/2017.
 */

public class PH_ReassortOpenHelper extends DBOpenHelper {

    public PH_ReassortOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static long insererPH_ReassortEnBDD(SQLiteDatabase db, PH_Reassort objet) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(Constantes.CLE_COL_CODE_PH_REASSORT, objet.getCode());
        contentValues.put(Constantes.CLE_COL_LISTE_PH_REASSORT, objet.getListe());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PH_REASSORT, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PH_REASSORT, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PH_REASSORT, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_PH_REASSORT, objet.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_FREQUENCE_PH_REASSORT, objet.getFrequence());
        contentValues.put(Constantes.CLE_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT, objet.isSynchroDM_Medicament());
        contentValues.put(Constantes.CLE_COL_SYNCHRODM_DMDMS_PH_REASSORT, objet.isSynchroDM_DMDMS());
        contentValues.put(Constantes.CLE_COL_VALORISATION_TTC_PH_REASSORT, objet.getValorisation_TTC());

        long rowID = db.insert(Constantes.TABLE_PH_REASSORT, null, contentValues);
        objet.setPhiMR4UUID((int) rowID);
        return rowID;
    }

    public static PH_Reassort getPH_ReassortByPhiMR4UUID(SQLiteDatabase db, int id) {
        PH_Reassort phReassort = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_REASSORT + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phReassort = new PH_Reassort(cursor);
        }
        cursor.close();
        cursor = null;
        return phReassort;
    }


    public List<PH_Reassort> getPH_ReassortByDepot(SQLiteDatabase db, String depot_Reference) {
        String critereRecherche = depot_Reference;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_REASSORT + " WHERE " + Constantes.CLE_COL_DEPOT_REFERENCE_PH_REASSORT + " LIKE ?", new String[]{critereRecherche});

        List<PH_Reassort> phReassortList = new ArrayList<>();

        while (cursor.moveToNext()) {
            phReassortList.add(new PH_Reassort(cursor));
        }
        cursor.close();
        cursor = null;
        return phReassortList;
    }


    public void supprimerUnePH_Reassort(SQLiteDatabase db, PH_Reassort ph_reassort) {
        db.delete(Constantes.TABLE_PH_REASSORT, DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(ph_reassort.getPhiMR4UUID())});
    }

    public static class Constantes implements BaseColumns {

        public static final String TABLE_PH_REASSORT = "PH_Reassort";
        public static final String CLE_COL_CODE_PH_REASSORT = "Code";
        public static final int NUM_COL_CODE_PH_REASSORT = 1;
        public static final String TYPE_COL_CODE_PH_REASSORT = "INTEGER";
        public static final String CLE_COL_LISTE_PH_REASSORT = "Liste";
        public static final int NUM_COL_LISTE_PH_REASSORT = 2;
        public static final String TYPE_COL_LISTE_PH_REASSORT = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_PH_REASSORT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PH_REASSORT = 3;
        public static final String TYPE_COL_SYS_DT_MAJ_PH_REASSORT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PH_REASSORT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PH_REASSORT = 4;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PH_REASSORT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PH_REASSORT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PH_REASSORT = 5;
        public static final String TYPE_COL_SYS_USER_MAJ_PH_REASSORT = "TEXT";
        public static final String CLE_COL_DEPOT_REFERENCE_PH_REASSORT = "Depot_Reference";
        public static final int NUM_COL_DEPOT_REFERENCE_PH_REASSORT = 6;
        public static final String TYPE_COL_DEPOT_REFERENCE_PH_REASSORT = "TEXT";
        public static final String CLE_COL_FREQUENCE_PH_REASSORT = "Frequence";
        public static final int NUM_COL_FREQUENCE_PH_REASSORT = 7;
        public static final String TYPE_COL_FREQUENCE_PH_REASSORT = "TEXT";
        public static final String CLE_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT = "SynchroDM_Medicament";
        public static final int NUM_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT = 8;
        public static final String TYPE_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT = "INTEGER";
        public static final String CLE_COL_SYNCHRODM_DMDMS_PH_REASSORT = "SynchroDM_DMDMS";
        public static final int NUM_COL_SYNCHRODM_DMDMS_PH_REASSORT = 9;
        public static final String TYPE_COL_SYNCHRODM_DMDMS_PH_REASSORT = "INTEGER";
        public static final String CLE_COL_VALORISATION_TTC_PH_REASSORT = "Valorisation_TTC";
        public static final int NUM_COL_VALORISATION_TTC_PH_REASSORT = 10;
        public static final String TYPE_COL_VALORISATION_TTC_PH_REASSORT = "INTEGER";

        public static final String CREATION_TABLE_PH_REASSORT = " CREATE TABLE       " + Constantes.TABLE_PH_REASSORT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_CODE_PH_REASSORT + " " + Constantes.TYPE_COL_CODE_PH_REASSORT + " , "
                + Constantes.CLE_COL_LISTE_PH_REASSORT + " " + Constantes.TYPE_COL_LISTE_PH_REASSORT + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_PH_REASSORT + " " + Constantes.TYPE_COL_SYS_DT_MAJ_PH_REASSORT + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PH_REASSORT + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PH_REASSORT + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_PH_REASSORT + " " + Constantes.TYPE_COL_SYS_USER_MAJ_PH_REASSORT + " , "
                + Constantes.CLE_COL_DEPOT_REFERENCE_PH_REASSORT + " " + Constantes.TYPE_COL_DEPOT_REFERENCE_PH_REASSORT + " , "
                + Constantes.CLE_COL_FREQUENCE_PH_REASSORT + " " + Constantes.TYPE_COL_FREQUENCE_PH_REASSORT + " , "
                + Constantes.CLE_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT + " " + Constantes.TYPE_COL_SYNCHRODM_MEDICAMENT_PH_REASSORT + " , "
                + Constantes.CLE_COL_SYNCHRODM_DMDMS_PH_REASSORT + " " + Constantes.TYPE_COL_SYNCHRODM_DMDMS_PH_REASSORT + " , "
                + Constantes.CLE_COL_VALORISATION_TTC_PH_REASSORT + " " + Constantes.TYPE_COL_VALORISATION_TTC_PH_REASSORT
                + " ); ";

    }
}
