package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.PerimetreFonctionnel;

public class PerimetreFonctionnelOpenHelper extends DBOpenHelper {

    public PerimetreFonctionnelOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void insererPerimetresFonctionnels(SQLiteDatabase db) {
        // Récupération de tous les services séparés par périmètre fonctionnel
        Cursor cursor = ServiceOpenHelper.getServicesSeparesParPerimetreFonctionnel(db);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Récupération des attributs du périmètre fonctionnel courant
                int perimetreFonctionnelID = cursor.getInt(ServiceOpenHelper.Constantes.NUM_COL_ID_PERIMETRE_FONCTIONNEL_SERVICE);
                String perimetreFonctionnelNom = cursor.getString(ServiceOpenHelper.Constantes.NUM_COL_NOM_PERIMETRE_FONCTIONNEL_SERVICE);

                // Création du périmètre fonctionnel courant
                PerimetreFonctionnel perimetreFonctionnel = new PerimetreFonctionnel(perimetreFonctionnelID, perimetreFonctionnelNom);

                // Insertion en BDD du périmètre fonctionnel
                insererUnPerimetreFoncitonnelEnBD(db, perimetreFonctionnel);

            }
        }
        cursor.close();
        cursor = null;
    }

    public static long insererUnPerimetreFoncitonnelEnBD(SQLiteDatabase db, PerimetreFonctionnel perimetreFonctionnel) {
        // Récupération des attributs du périmètre fonctionnel
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_PERIMETRE_FONCTIONNEL, perimetreFonctionnel.getId());
        contentValues.put(Constantes.CLE_COL_NOM_PERIMETRE_FONCTIONNEL, perimetreFonctionnel.getNom());

        // Insertion du périmètre fonctionnel en BDD
        long rowID = db.insert(Constantes.TABLE_PERIMETRE_FONCTIONNEL, null, contentValues);

        perimetreFonctionnel.setphiwms_mobileUUID((int) rowID);

        return rowID;
    }

    public static void viderTablePerimetreFonctionnel(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PERIMETRE_FONCTIONNEL, null, null);
    }

    public static List<PerimetreFonctionnel> getAllPerimetreFonctionnel(SQLiteDatabase db) {
        Cursor curseur = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PERIMETRE_FONCTIONNEL, null);

        List<PerimetreFonctionnel> listperimetreFonctionnel = new ArrayList<>();

        while (curseur.moveToNext()) {
            listperimetreFonctionnel.add(new PerimetreFonctionnel(curseur));
        }

        curseur.close();
        curseur = null;
        return listperimetreFonctionnel;
    }

    public static PerimetreFonctionnel getUnPerimetreFonctionnelByID(SQLiteDatabase db, int id) {
        PerimetreFonctionnel perimetreFonctionnel = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PERIMETRE_FONCTIONNEL + " WHERE " + Constantes.CLE_COL_ID_PERIMETRE_FONCTIONNEL + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            perimetreFonctionnel = new PerimetreFonctionnel(cursor);
        }

        cursor.close();
        cursor = null;
        return perimetreFonctionnel;
    }

    public static PerimetreFonctionnel getUnPerimetreFonctionnelParNom(String nom, SQLiteDatabase db) {
        Cursor curseur = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PERIMETRE_FONCTIONNEL + " WHERE " + Constantes.CLE_COL_NOM_PERIMETRE_FONCTIONNEL + " = ?", new String[]{nom});

        PerimetreFonctionnel perimetreFonctionnel = null;

        if (curseur.getCount() == 1) {
            curseur.moveToFirst();
            perimetreFonctionnel = new PerimetreFonctionnel(curseur.getInt(Constantes.NUM_COL_ID_PERIMETRE_FONCTIONNEL), curseur.getString(Constantes.NUM_COL_NOM_PERIMETRE_FONCTIONNEL));
        }

        curseur.close();
        curseur = null;
        return perimetreFonctionnel;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PERIMETRE_FONCTIONNEL = "PerimetreFonctionnel";


        public static final String CLE_COL_NOM_PERIMETRE_FONCTIONNEL = "nom";
        public static final int NUM_COL_NOM_PERIMETRE_FONCTIONNEL = 1;
        public static final String TYPE_COL_NOM_PERIMETRE_FONCTIONNEL = "TEXT";

        public static final String CLE_COL_ID_PERIMETRE_FONCTIONNEL = "id";
        public static final int NUM_COL_ID_PERIMETRE_FONCTIONNEL = 2;
        public static final String TYPE_COL_ID_PERIMETRE_FONCTIONNEL = "INTEGER";


        public static final String CREATION_TABLE_PERIMETRE_FONCTIONNEL = "CREATE TABLE "
                + Constantes.TABLE_PERIMETRE_FONCTIONNEL + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_NOM_PERIMETRE_FONCTIONNEL + " " + Constantes.TYPE_COL_NOM_PERIMETRE_FONCTIONNEL + ","
                + Constantes.CLE_COL_ID_PERIMETRE_FONCTIONNEL + " " + Constantes.TYPE_COL_ID_PERIMETRE_FONCTIONNEL
                + ");";
    }
}
