package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by olivier on 15/03/2018.
 */

public class ParametreUtilisateurOpenHelper extends DBOpenHelper {

    public ParametreUtilisateurOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static Boolean getAuthentificationForte(SQLiteDatabase db) {
        int authentification_forte = 0;
        Boolean activer;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_UTILISATEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            authentification_forte = cursor.getInt(Constantes.NUM_COL_AUTHENTIFICATION);
        }
        cursor.close();
        cursor = null;

        if (authentification_forte == 0) {
            activer = false;
        } else {
            activer = true;
        }

        return activer;
    }

    public static Boolean getConnexionDirecte(SQLiteDatabase db) {
        int connexionDirecte = 0;
        Boolean activer;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_UTILISATEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            connexionDirecte = cursor.getInt(Constantes.NUM_COL_CONNEXIONDIRECTE);
        }
        cursor.close();
        cursor = null;

        if (connexionDirecte == 0) {
            activer = false;
        } else {
            activer = true;
        }

        return activer;
    }

    public static Boolean getModeTrace(SQLiteDatabase db) {
        int modeTrace = 0;
        Boolean activer;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_UTILISATEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            modeTrace = cursor.getInt(Constantes.NUM_COL_MODETRACE);
        }
        cursor.close();
        cursor = null;

        if (modeTrace == 0) {
            activer = false;
        } else {
            activer = true;
        }

        return activer;
    }

    public static String getChoixTriPreparation(SQLiteDatabase db)
    {
        String choixTri = "Designation";

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_UTILISATEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            choixTri = cursor.getString(Constantes.NUM_COL_TRIPREPARATION);
        }
        cursor.close();
        cursor = null;

        return choixTri;
    }

    public static String getChoixTriReception(SQLiteDatabase db)
    {
        String choixTri = "Designation";

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_UTILISATEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            choixTri = cursor.getString(Constantes.NUM_COL_TRIRECEPTION);
        }
        cursor.close();
        cursor = null;

        return choixTri;
    }

    public static String getChoixTriReliquat(SQLiteDatabase db)
    {
        String choixTri = "Designation";

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_UTILISATEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            choixTri = cursor.getString(Constantes.NUM_COL_TRIRECLIQUAT);
        }
        cursor.close();
        cursor = null;

        return choixTri;
    }

    public static String getChoixTriRetour(SQLiteDatabase db)
    {
        String choixTri = "Designation";

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_UTILISATEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            choixTri = cursor.getString(Constantes.NUM_COL_TRIRETOUR);
        }
        cursor.close();
        cursor = null;

        return choixTri;
    }

    public static String getChoixTriRetourLigne(SQLiteDatabase db)
    {
        String choixTri = "Designation";

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_UTILISATEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            choixTri = cursor.getString(Constantes.NUM_COL_TRIRETOURLIGNE);
        }
        cursor.close();
        cursor = null;

        return choixTri;
    }

    public static int getNbUtilisateur(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_UTILISATEUR, null);
        int nbParametresUtilisateur = cursor.getCount();
        cursor.close();
        cursor = null;
        return nbParametresUtilisateur;
    }

    public static long mettreAJourTriRetour(SQLiteDatabase db, int user_id, String triRetour)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_TRIRETOUR, triRetour);


        long rowId = db.update(Constantes.TABLE_PARAMETRES_UTILISATEUR, contentValues, Constantes.CLE_COL_ID_PARAMETRES_UTILISATEUR + "=" + user_id, null);
        return rowId;
    }

    public static long mettreAJourTriRetourLigne(SQLiteDatabase db, int user_id, String triRetourLigne)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_TRIRETOURLIGNE, triRetourLigne);


        long rowId = db.update(Constantes.TABLE_PARAMETRES_UTILISATEUR, contentValues, Constantes.CLE_COL_ID_PARAMETRES_UTILISATEUR + "=" + user_id, null);
        return rowId;
    }

    public static long mettreAJourTriReliquat(SQLiteDatabase db, int user_id, String triReliquat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_TRIRELIQUAT, triReliquat);


        long rowId = db.update(Constantes.TABLE_PARAMETRES_UTILISATEUR, contentValues, Constantes.CLE_COL_ID_PARAMETRES_UTILISATEUR + "=" + user_id, null);
        return rowId;
    }

    public static long mettreAJourTriReception(SQLiteDatabase db, int user_id, String triReception)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_TRIRECEPTION, triReception);


        long rowId = db.update(Constantes.TABLE_PARAMETRES_UTILISATEUR, contentValues, Constantes.CLE_COL_ID_PARAMETRES_UTILISATEUR + "=" + user_id, null);
        return rowId;
    }

    public static long mettreAJourTriPreparation(SQLiteDatabase db, int user_id, String triPreparation)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_TRIPREPARATION, triPreparation);


        long rowId = db.update(Constantes.TABLE_PARAMETRES_UTILISATEUR, contentValues, Constantes.CLE_COL_ID_PARAMETRES_UTILISATEUR + "=" + user_id, null);
        return rowId;
    }

    public static long mettreAJourParametre(SQLiteDatabase db, int user_id, Boolean activerAuthentification, Boolean connexionDirecte, Boolean modeTrace, String triPreparation, String triReception, String triReliquat, String triRetour, String triRetourLigne) {
        // Récupération des éléments du produit
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_AUTHENTIFICATION, activerAuthentification);
        contentValues.put(Constantes.CLE_COL_CONNEXIONDIRECTE, connexionDirecte);
        contentValues.put(Constantes.CLE_COL_MODETRACE, modeTrace);
        contentValues.put(Constantes.CLE_COL_TRIPREPARATION, triPreparation);
        contentValues.put(Constantes.CLE_COL_TRIRECEPTION, triReception);
        contentValues.put(Constantes.CLE_COL_TRIRELIQUAT, triReliquat);
        contentValues.put(Constantes.CLE_COL_TRIRETOUR, triRetour);
        contentValues.put(Constantes.CLE_COL_TRIRETOURLIGNE, triRetourLigne);


        long rowId = db.update(Constantes.TABLE_PARAMETRES_UTILISATEUR, contentValues, Constantes.CLE_COL_ID_PARAMETRES_UTILISATEUR + "=" + user_id, null);
        return rowId;
    }

    public static long insererParametreUtilisateurEnBDD(SQLiteDatabase db, int id_user, Boolean activerAuthentification, Boolean connexionDirecte, Boolean modeTrace, String triPreparation, String triReception, String triReliquat, String triRetour, String triRetourLigne) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_PARAMETRES_UTILISATEUR, id_user);
        contentValues.put(Constantes.CLE_COL_AUTHENTIFICATION, activerAuthentification);
        contentValues.put(Constantes.CLE_COL_CONNEXIONDIRECTE, connexionDirecte);
        contentValues.put(Constantes.CLE_COL_MODETRACE, modeTrace);
        contentValues.put(Constantes.CLE_COL_TRIPREPARATION, triPreparation);
        contentValues.put(Constantes.CLE_COL_TRIRECEPTION, triReception);
        contentValues.put(Constantes.CLE_COL_TRIRELIQUAT, triReliquat);
        contentValues.put(Constantes.CLE_COL_TRIRETOUR, triRetour);
        contentValues.put(Constantes.CLE_COL_TRIRETOURLIGNE, triRetourLigne);


        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_PARAMETRES_UTILISATEUR, null, contentValues);
        return rowId;
    }


    public static class Constantes implements BaseColumns {
        public static final String TABLE_PARAMETRES_UTILISATEUR = "ParametreUtilisateur";

        public static final String CLE_COL_ID_PARAMETRES_UTILISATEUR = "id";
        public static final int NUM_COL_ID_PARAMETRES_UTILISATEUR = 0;
        public static final String TYPE_COL_ID_PARAMETRES_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_AUTHENTIFICATION = "Authentification_forte";
        public static final int NUM_COL_AUTHENTIFICATION = 1;
        public static final String TYPE_COL_AUTHENTIFICATION = "INTEGER";

        public static final String CLE_COL_CONNEXIONDIRECTE = "ConnexionDirecte";
        public static final int NUM_COL_CONNEXIONDIRECTE = 2;
        public static final String TYPE_COL_CONNEXIONDIRECTE = "INTEGER";

        public static final String CLE_COL_MODETRACE = "ModeTrace";
        public static final int NUM_COL_MODETRACE = 3;
        public static final String TYPE_COL_MODETRACE = "INTEGER";

        public static final String CLE_COL_TRIPREPARATION = "TriPreparation";
        public static final int NUM_COL_TRIPREPARATION = 4;
        public static final String TYPE_COL_TRIPREPARATION = "TEXT";

        public static final String CLE_COL_TRIRECEPTION = "TriReception";
        public static final int NUM_COL_TRIRECEPTION = 5;
        public static final String TYPE_COL_TRIRECEPTION = "TEXT";

        public static final String CLE_COL_TRIRELIQUAT = "TriReliquat";
        public static final int NUM_COL_TRIRECLIQUAT = 6;
        public static final String TYPE_COL_TRIRELIQUAT = "TEXT";

        public static final String CLE_COL_TRIRETOUR = "TriRetour";
        public static final int NUM_COL_TRIRETOUR = 7;
        public static final String TYPE_COL_TRIRETOUR = "TEXT";

        public static final String CLE_COL_TRIRETOURLIGNE = "TriRetourLigne";
        public static final int NUM_COL_TRIRETOURLIGNE = 8;
        public static final String TYPE_COL_TRIRETOURLIGNE = "TEXT";

        public static final String CREATION_TABLE_PARAMETRES_UTILISATEUR = "CREATE TABLE " + TABLE_PARAMETRES_UTILISATEUR
                + "("
                + CLE_COL_ID_PARAMETRES_UTILISATEUR + " " + TYPE_COL_ID_PARAMETRES_UTILISATEUR + " PRIMARY KEY,"
                + CLE_COL_AUTHENTIFICATION + " " + TYPE_COL_AUTHENTIFICATION + " , "
                + CLE_COL_CONNEXIONDIRECTE + " " + TYPE_COL_CONNEXIONDIRECTE + " , "
                + CLE_COL_MODETRACE + " " + TYPE_COL_MODETRACE + " , "
                + CLE_COL_TRIPREPARATION + " " + TYPE_COL_TRIPREPARATION + " , "
                + CLE_COL_TRIRECEPTION + " " + TYPE_COL_TRIRECEPTION + " , "
                + CLE_COL_TRIRELIQUAT + " " + TYPE_COL_TRIRELIQUAT + " , "
                + CLE_COL_TRIRETOUR + " " + TYPE_COL_TRIRETOUR + " , "
                + CLE_COL_TRIRETOURLIGNE + " " + TYPE_COL_TRIRETOURLIGNE
                + ");";
    }

}
