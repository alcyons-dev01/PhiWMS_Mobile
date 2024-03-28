package com.example.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.phiwms_mobile.Classes.TableTrace;

public class TableTraceOpenHelper extends DBOpenHelper {

    public TableTraceOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static TableTrace getTableTraceByPhiMR4UUID(SQLiteDatabase db, int id){
        TableTrace objet=null;
        Cursor cursor=db.rawQuery(" SELECT * FROM " + Constantes.TABLE_TABLE_TRACE + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID+"=? ", new String[]{String.valueOf(id)});
        if(cursor.getCount()==1){
            cursor.moveToFirst();objet=new TableTrace(cursor);
        }

        return objet;
    }

    public static long insererTableTraceEnBDD(SQLiteDatabase db, TableTrace objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_TABLE_TRACE, objet.getId());
        contentValues.put(Constantes.CLE_COL_DATE, objet.getDate());
        contentValues.put(Constantes.CLE_COL_SERVICE, objet.getService());
        contentValues.put(Constantes.CLE_COL_SITUATION, objet.getSituation());
        contentValues.put(Constantes.CLE_COL_CODE_RETOURNE, objet.getCodeRetourne());
        contentValues.put(Constantes.CLE_COL_USER, objet.getUser());
        contentValues.put(Constantes.CLE_COL_USERID, objet.getUserID());
     //   contentValues.put(DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID, objet.getPhiMR4UUID());

        long rowID = db.insert(Constantes.TABLE_TABLE_TRACE, null, contentValues);
        objet.setPhiMR4UUID((int) rowID);
        return rowID;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_TABLE_TRACE = "TableTrace";

        public static final String CLE_COL_ID_TABLE_TRACE = "Id";
        public static final int NUM_COL_ID_TABLE_TRACE = 1;
        public static final String TYPE_COL_TABLE_TRACE = "INTEGER";

        public static final String CLE_COL_DATE = "date";
        public static final int NUM_COL_DATE = 2;
        public static final String TYPE_COL_DATE = "TEXT";

        public static final String CLE_COL_SERVICE = "Service";
        public static final int NUM_COL_SERVICE = 3;
        public static final String TYPE_COL_SERVICE = "TEXT";

        public static final String CLE_COL_SITUATION = "Situation";
        public static final int NUM_COL_SITUATION = 4;
        public static final String TYPE_COL_SITUATION = "TEXT";

        public static final String CLE_COL_CODE_RETOURNE = "CodeRetourne";
        public static final int NUM_COL_CODE_RETOURNE = 5;
        public static final String TYPE_COL_CODE_RETOURNE = "TEXT";

        public static final String CLE_COL_USER = "User";
        public static final int NUM_COL_USER= 6;
        public static final String TYPE_COL_USER = "TEXT";

        public static final String CLE_COL_USERID = "UserID";
        public static final int NUM_COL_USERID = 7;
        public static final String TYPE_COL_USERID = "INTEGER";

        public static final String CREATION_TABLE_TABLE_TRACE = " CREATE TABLE  " + Constantes.TABLE_TABLE_TRACE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_ID_TABLE_TRACE + " " + Constantes.TYPE_COL_TABLE_TRACE + " , "
                + Constantes.CLE_COL_DATE + " " + Constantes.TYPE_COL_DATE + " , "
                + Constantes.CLE_COL_SERVICE + " " + Constantes.TYPE_COL_SERVICE + " , "
                + Constantes.CLE_COL_SITUATION + " " + Constantes.TYPE_COL_SITUATION + " , "
                + Constantes.CLE_COL_CODE_RETOURNE + " " + Constantes.TYPE_COL_CODE_RETOURNE + " , "
                + Constantes.CLE_COL_USER + " " + Constantes.TYPE_COL_USER + " , "
                + Constantes.CLE_COL_USERID + " " + Constantes.TYPE_COL_USERID
                + " ); ";

    }
}