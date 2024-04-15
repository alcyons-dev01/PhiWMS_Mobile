package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;

/**
 * Created by olivier on 11/04/2019.
 */

public class ActionUtilisateur_LigneOpenHelper extends DBOpenHelper {

    public ActionUtilisateur_LigneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableActionUtilisateurLigne(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, null, null);
    }

    public static long insererActionUtilisateurLigneEnBDD(SQLiteDatabase db, ActionUtilisateur_Ligne objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_ACTION_UTILISATEUR_LIGNE, objet.getId());
        contentValues.put(Constantes.CLE_COL_ID_ACTION_UTILISATEUR, objet.getIdActionUtilisateur());
        contentValues.put(Constantes.CLE_COL_TABLE_CONCERNEE, objet.getTableConcerne());
        contentValues.put(Constantes.CLE_COL_NUM_CHAMPS, objet.getNumChamps());
        contentValues.put(Constantes.CLE_COL_GS1, objet.getGS1());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_ID, objet.getEmplacementId());
        contentValues.put(Constantes.CLE_COL_QUANTITE, objet.getQuantite());
        contentValues.put(Constantes.CLE_COL_NOM_PRODUIT, objet.getNom_Produit());

        long rowID = db.insert(Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, null, contentValues);
        objet.setphiwms_mobileUUID((int) rowID);
        return rowID;
    }

    public static long mettreAJourActionUtilisateurLigne(SQLiteDatabase db, ActionUtilisateur_Ligne objet) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_ACTION_UTILISATEUR_LIGNE, objet.getId());
        contentValues.put(Constantes.CLE_COL_ID_ACTION_UTILISATEUR, objet.getIdActionUtilisateur());
        contentValues.put(Constantes.CLE_COL_TABLE_CONCERNEE, objet.getTableConcerne());
        contentValues.put(Constantes.CLE_COL_NUM_CHAMPS, objet.getNumChamps());
        contentValues.put(Constantes.CLE_COL_GS1, objet.getGS1());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_ID, objet.getEmplacementId());
        contentValues.put(Constantes.CLE_COL_QUANTITE, objet.getQuantite());
        contentValues.put(Constantes.CLE_COL_NOM_PRODUIT, objet.getNom_Produit());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, objet.getPhiMR4UUID());


        long rowId = db.update(Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + objet.getPhiMR4UUID(), null);

        return rowId;
    }

    public static ActionUtilisateur_Ligne getActionUtilisateurByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        ActionUtilisateur_Ligne actionUtilisateur_ligne = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_ACTION_UTILISATEUR_LIGNE + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " =? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            actionUtilisateur_ligne = new ActionUtilisateur_Ligne(cursor);
        }
        cursor.close();
        cursor = null;
        return actionUtilisateur_ligne;
    }

    public static List<ActionUtilisateur_Ligne> getLigneByAction(SQLiteDatabase db, Integer actionId) {
        String critereRecherche = String.valueOf(actionId);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_ACTION_UTILISATEUR_LIGNE + " WHERE " + Constantes.CLE_COL_ID_ACTION_UTILISATEUR + " = ? ", new String[]{critereRecherche});

        List<ActionUtilisateur_Ligne> actionList = new ArrayList<>();

        while (cursor.moveToNext()) {
            actionList.add(new ActionUtilisateur_Ligne(cursor));
        }
        cursor.close();
        cursor = null;
        return actionList;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_ACTION_UTILISATEUR_LIGNE = "ActionUtilisateur_Ligne";

        public static final String CLE_COL_ID_ACTION_UTILISATEUR_LIGNE = "Id";
        public static final int NUM_COL_ID_ACTION_UTILISATEUR_LIGNE = 1;
        public static final String TYPE_COL_ID_ACTION_UTILISATEUR_LIGNE = "INTEGER";

        public static final String CLE_COL_ID_ACTION_UTILISATEUR = "IdActionUtilisateur";
        public static final int NUM_COL_ID_ACTION_UTILISATEUR = 2;
        public static final String TYPE_COL_ID_ACTION_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_TABLE_CONCERNEE = "TableConcerne";
        public static final int NUM_COL_TABLE_CONCERNEE = 3;
        public static final String TYPE_COL_TABLE_CONCERNEE = "TEXT";

        public static final String CLE_COL_NUM_CHAMPS = "NumChamps";
        public static final int NUM_COL_NUM_CHAMPS = 4;
        public static final String TYPE_COL_NUM_CHAMPS = "INTEGER";

        public static final String CLE_COL_GS1 = "GS1";
        public static final int NUM_COL_GS1 = 5;
        public static final String TYPE_COL_GS1 = "TEXT";

        public static final String CLE_COL_EMPLACEMENT_ID = "EmplacementId";
        public static final int NUM_COL_EMPLACEMENT_ID = 6;
        public static final String TYPE_COL_EMPLACEMENT_ID = "INTEGER";

        public static final String CLE_COL_QUANTITE = "Quantite";
        public static final int NUM_COL_NUM_QUANTITE = 7;
        public static final String TYPE_COL_NUM_QUANTITE = "INTEGER";

        public static final String CLE_COL_NOM_PRODUIT = "Nom_Produit";
        public static final int NUM_COL_NUM_NOM_PRODUIT = 8;
        public static final String TYPE_COL_NUM_NOM_PRODUIT = "TEXT";

        public static final String CREATION_TABLE_ACTION_UTILISATEUR_LIGNE = " CREATE TABLE  " + Constantes.TABLE_ACTION_UTILISATEUR_LIGNE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_ID_ACTION_UTILISATEUR_LIGNE + " " + Constantes.TYPE_COL_ID_ACTION_UTILISATEUR_LIGNE + " , "
                + Constantes.CLE_COL_ID_ACTION_UTILISATEUR + " " + Constantes.TYPE_COL_ID_ACTION_UTILISATEUR + " , "
                + Constantes.CLE_COL_TABLE_CONCERNEE + " " + Constantes.TYPE_COL_TABLE_CONCERNEE + " , "
                + Constantes.CLE_COL_NUM_CHAMPS + " " + Constantes.TYPE_COL_NUM_CHAMPS + " , "
                + Constantes.CLE_COL_GS1 + " " + Constantes.TYPE_COL_GS1 + " , "
                + Constantes.CLE_COL_EMPLACEMENT_ID + " " + Constantes.TYPE_COL_EMPLACEMENT_ID + " , "
                + Constantes.CLE_COL_QUANTITE + " " + Constantes.TYPE_COL_NUM_QUANTITE + " , "
                + Constantes.CLE_COL_NOM_PRODUIT + " " + Constantes.TYPE_COL_NUM_NOM_PRODUIT
                + " ); ";

    }
}