package fr.alcyons.phimr4.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import fr.alcyons.phimr4.Classes.SYS_User_Rules;

/**
 * Created by olivier on 25/04/2019.
 */

public class SYS_User_RulesOpenHelper extends DBOpenHelper {

    public SYS_User_RulesOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableSYS_User_Rules(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_SYS_USER_RULES, null, null);
    }

    public static long insererSYS_User_RulesEnBDD(SQLiteDatabase db, SYS_User_Rules objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_NOM_SYS_USER_RULES, objet.getNom());
        contentValues.put(Constantes.CLE_COL_PROFIL_SYS_USER_RULES, objet.getProfil());
        contentValues.put(Constantes.CLE_COL_INITIALE_SYS_USER_RULES, objet.getInitiale());
        contentValues.put(Constantes.CLE_COL_X_MOTPASSE_SYS_USER_RULES, objet.getX_MotPasse());
        contentValues.put(Constantes.CLE_COL_USER_UID_SYS_USER_RULES, objet.getUser_UID());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_SYS_USER_RULES, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_SYS_USER_RULES, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_SYS_USER_RULES, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_DROITS_SYS_USER_RULES, objet.getDroits());
        contentValues.put(Constantes.CLE_COL_ACCES_CLASSIFICATION_SYS_USER_RULES, objet.getAcces_Classification());
        contentValues.put(Constantes.CLE_COL_AVALISER_COMMANDE_AUTORISER_SYS_USER_RULES, objet.isAvaliser_Commande_Autoriser());
        contentValues.put(Constantes.CLE_COL_ADMINISTRATEUR_SYS_USER_RULES, objet.isAdministrateur());
        contentValues.put(Constantes.CLE_COL_QUARANTAINE_AUTORISER_SYS_USER_RULES, objet.isQuarantaine_Autoriser());
        contentValues.put(Constantes.CLE_COL_PERIMETRE_PARDEFAUT_SYS_USER_RULES, objet.getPerimetre_ParDefaut());
        contentValues.put(Constantes.CLE_COL_PLANHABILITATION_SYS_USER_RULES, objet.getPlanHabilitation());
        contentValues.put(Constantes.CLE_COL_MODIFICATION_DPP_AUTORISER_SYS_USER_RULES, objet.isModification_DPP_Autoriser());
        contentValues.put(Constantes.CLE_COL_REGULATION_DEMANDE_PARTICULIERE_SYS_USER_RULES, objet.isRegulation_Demande_Particuliere());
        contentValues.put(Constantes.CLE_COL_ANONYMISER_SYS_USER_RULES, objet.isAnonymiser());
        contentValues.put(Constantes.CLE_COL_BUREAU_PARDEFAUT_SYS_USER_RULES, objet.getBureau_ParDefaut());
        contentValues.put(Constantes.CLE_COL_LOCALISATIONPARDEFAUT_SYS_USER_RULES, objet.getLocalisationParDefaut());
        contentValues.put(Constantes.CLE_COL_REGULATION_AUTOMATIQUE_SYS_USER_RULES, objet.isRegulation_Automatique());
        contentValues.put(Constantes.CLE_COL_ACCES_SERIALISATION_SYS_USER_RULES, objet.isAcces_Serialisation());
        contentValues.put(Constantes.CLE_COL_SERIALISATION_IDENTIFIANT_SYS_USER_RULES, objet.getSerialisation_identifiant());
        contentValues.put(Constantes.CLE_COL_SERIALISATION_MDP_SYS_USER_RULES, objet.getSerialisation_mdp());
        contentValues.put(Constantes.CLE_COL_SERIALISATION_TAN_SYS_USER_RULES, objet.getSerialisation_tan());
        contentValues.put(Constantes.CLE_COL_SERIALISATION_CLIENTLOGINID_SYS_USER_RULES, objet.getSerialisation_clientLoginId());
        contentValues.put(Constantes.CLE_COL_SERIALISATIONTERMSETCONDITIONS_SYS_USER_RULES, objet.isSerialisationTermsEtConditions());

        long rowID = db.insert(Constantes.TABLE_SYS_USER_RULES, null, contentValues);

        objet.setPhiMR4UUID((int) rowID);

        return rowID;
    }

    public static SYS_User_Rules getSYS_User_RulesByUser(SQLiteDatabase db, int id) {
        SYS_User_Rules objet = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_SYS_USER_RULES + "      WHERE " + Constantes.CLE_COL_USER_UID_SYS_USER_RULES + "=? ", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            objet = new SYS_User_Rules(cursor);
        }

        return objet;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_SYS_USER_RULES = "SYS_User_Rules";
        public static final String CLE_COL_NOM_SYS_USER_RULES = "Nom";
        public static final int NUM_COL_NOM_SYS_USER_RULES = 1;
        public static final String TYPE_COL_NOM_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_PROFIL_SYS_USER_RULES = "Profil";
        public static final int NUM_COL_PROFIL_SYS_USER_RULES = 2;
        public static final String TYPE_COL_PROFIL_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_INITIALE_SYS_USER_RULES = "Initiale";
        public static final int NUM_COL_INITIALE_SYS_USER_RULES = 3;
        public static final String TYPE_COL_INITIALE_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_X_MOTPASSE_SYS_USER_RULES = "X_MotPasse";
        public static final int NUM_COL_X_MOTPASSE_SYS_USER_RULES = 4;
        public static final String TYPE_COL_X_MOTPASSE_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_USER_UID_SYS_USER_RULES = "User_UID";
        public static final int NUM_COL_USER_UID_SYS_USER_RULES = 5;
        public static final String TYPE_COL_USER_UID_SYS_USER_RULES = "INTEGER";
        public static final String CLE_COL_SYS_DT_MAJ_SYS_USER_RULES = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_SYS_USER_RULES = 6;
        public static final String TYPE_COL_SYS_DT_MAJ_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_SYS_USER_RULES = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_SYS_USER_RULES = 7;
        public static final String TYPE_COL_SYS_HEURE_MAJ_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_SYS_USER_RULES = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_SYS_USER_RULES = 8;
        public static final String TYPE_COL_SYS_USER_MAJ_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_DROITS_SYS_USER_RULES = "Droits";
        public static final int NUM_COL_DROITS_SYS_USER_RULES = 9;
        public static final String TYPE_COL_DROITS_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_ACCES_CLASSIFICATION_SYS_USER_RULES = "Acces_Classification";
        public static final int NUM_COL_ACCES_CLASSIFICATION_SYS_USER_RULES = 10;
        public static final String TYPE_COL_ACCES_CLASSIFICATION_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_AVALISER_COMMANDE_AUTORISER_SYS_USER_RULES = "Avaliser_Commande_Autoriser";
        public static final int NUM_COL_AVALISER_COMMANDE_AUTORISER_SYS_USER_RULES = 11;
        public static final String TYPE_COL_AVALISER_COMMANDE_AUTORISER_SYS_USER_RULES = "INTEGER";
        public static final String CLE_COL_ADMINISTRATEUR_SYS_USER_RULES = "Administrateur";
        public static final int NUM_COL_ADMINISTRATEUR_SYS_USER_RULES = 12;
        public static final String TYPE_COL_ADMINISTRATEUR_SYS_USER_RULES = "INTEGER";
        public static final String CLE_COL_QUARANTAINE_AUTORISER_SYS_USER_RULES = "Quarantaine_Autoriser";
        public static final int NUM_COL_QUARANTAINE_AUTORISER_SYS_USER_RULES = 13;
        public static final String TYPE_COL_QUARANTAINE_AUTORISER_SYS_USER_RULES = "INTEGER";
        public static final String CLE_COL_PERIMETRE_PARDEFAUT_SYS_USER_RULES = "Perimetre_ParDefaut";
        public static final int NUM_COL_PERIMETRE_PARDEFAUT_SYS_USER_RULES = 14;
        public static final String TYPE_COL_PERIMETRE_PARDEFAUT_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_PLANHABILITATION_SYS_USER_RULES = "planHabilitation";
        public static final int NUM_COL_PLANHABILITATION_SYS_USER_RULES = 15;
        public static final String TYPE_COL_PLANHABILITATION_SYS_USER_RULES = "INTEGER";
        public static final String CLE_COL_MODIFICATION_DPP_AUTORISER_SYS_USER_RULES = "Modification_DPP_Autoriser";
        public static final int NUM_COL_MODIFICATION_DPP_AUTORISER_SYS_USER_RULES = 16;
        public static final String TYPE_COL_MODIFICATION_DPP_AUTORISER_SYS_USER_RULES = "INTEGER";
        public static final String CLE_COL_REGULATION_DEMANDE_PARTICULIERE_SYS_USER_RULES = "Regulation_Demande_Particuliere";
        public static final int NUM_COL_REGULATION_DEMANDE_PARTICULIERE_SYS_USER_RULES = 17;
        public static final String TYPE_COL_REGULATION_DEMANDE_PARTICULIERE_SYS_USER_RULES = "INTEGER";
        public static final String CLE_COL_ANONYMISER_SYS_USER_RULES = "Anonymiser";
        public static final int NUM_COL_ANONYMISER_SYS_USER_RULES = 18;
        public static final String TYPE_COL_ANONYMISER_SYS_USER_RULES = "INTEGER";
        public static final String CLE_COL_BUREAU_PARDEFAUT_SYS_USER_RULES = "Bureau_ParDefaut";
        public static final int NUM_COL_BUREAU_PARDEFAUT_SYS_USER_RULES = 19;
        public static final String TYPE_COL_BUREAU_PARDEFAUT_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_LOCALISATIONPARDEFAUT_SYS_USER_RULES = "LocalisationParDefaut";
        public static final int NUM_COL_LOCALISATIONPARDEFAUT_SYS_USER_RULES = 20;
        public static final String TYPE_COL_LOCALISATIONPARDEFAUT_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_REGULATION_AUTOMATIQUE_SYS_USER_RULES = "Regulation_Automatique";
        public static final int NUM_COL_REGULATION_AUTOMATIQUE_SYS_USER_RULES = 21;
        public static final String TYPE_COL_REGULATION_AUTOMATIQUE_SYS_USER_RULES = "INTEGER";
        public static final String CLE_COL_ACCES_SERIALISATION_SYS_USER_RULES = "Acces_Serialisation";
        public static final int NUM_COL_ACCES_SERIALISATION_SYS_USER_RULES = 22;
        public static final String TYPE_COL_ACCES_SERIALISATION_SYS_USER_RULES = "INTEGER";
        public static final String CLE_COL_SERIALISATION_IDENTIFIANT_SYS_USER_RULES = "Serialisation_identifiant";
        public static final int NUM_COL_SERIALISATION_IDENTIFIANT_SYS_USER_RULES = 23;
        public static final String TYPE_COL_SERIALISATION_IDENTIFIANT_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_SERIALISATION_MDP_SYS_USER_RULES = "Serialisation_mdp";
        public static final int NUM_COL_SERIALISATION_MDP_SYS_USER_RULES = 24;
        public static final String TYPE_COL_SERIALISATION_MDP_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_SERIALISATION_TAN_SYS_USER_RULES = "Serialisation_tan";
        public static final int NUM_COL_SERIALISATION_TAN_SYS_USER_RULES = 25;
        public static final String TYPE_COL_SERIALISATION_TAN_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_SERIALISATION_CLIENTLOGINID_SYS_USER_RULES = "Serialisation_clientLoginId";
        public static final int NUM_COL_SERIALISATION_CLIENTLOGINID_SYS_USER_RULES = 26;
        public static final String TYPE_COL_SERIALISATION_CLIENTLOGINID_SYS_USER_RULES = "TEXT";
        public static final String CLE_COL_SERIALISATIONTERMSETCONDITIONS_SYS_USER_RULES = "SerialisationTermsEtConditions";
        public static final int NUM_COL_SERIALISATIONTERMSETCONDITIONS_SYS_USER_RULES = 27;
        public static final String TYPE_COL_SERIALISATIONTERMSETCONDITIONS_SYS_USER_RULES = "INTEGER";

        public static final String CREATION_TABLE_SYS_USER_RULES = " CREATE TABLE       " + Constantes.TABLE_SYS_USER_RULES
                + "(" +
                DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_NOM_SYS_USER_RULES + "   " + Constantes.TYPE_COL_NOM_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_PROFIL_SYS_USER_RULES + "   " + Constantes.TYPE_COL_PROFIL_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_INITIALE_SYS_USER_RULES + "   " + Constantes.TYPE_COL_INITIALE_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_X_MOTPASSE_SYS_USER_RULES + "   " + Constantes.TYPE_COL_X_MOTPASSE_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_USER_UID_SYS_USER_RULES + "   " + Constantes.TYPE_COL_USER_UID_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_SYS_USER_RULES + "   " + Constantes.TYPE_COL_SYS_DT_MAJ_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_SYS_USER_RULES + "   " + Constantes.TYPE_COL_SYS_HEURE_MAJ_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_SYS_USER_RULES + "   " + Constantes.TYPE_COL_SYS_USER_MAJ_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_DROITS_SYS_USER_RULES + "   " + Constantes.TYPE_COL_DROITS_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_ACCES_CLASSIFICATION_SYS_USER_RULES + "   " + Constantes.TYPE_COL_ACCES_CLASSIFICATION_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_AVALISER_COMMANDE_AUTORISER_SYS_USER_RULES + "   " + Constantes.TYPE_COL_AVALISER_COMMANDE_AUTORISER_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_ADMINISTRATEUR_SYS_USER_RULES + "   " + Constantes.TYPE_COL_ADMINISTRATEUR_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_QUARANTAINE_AUTORISER_SYS_USER_RULES + "   " + Constantes.TYPE_COL_QUARANTAINE_AUTORISER_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_PERIMETRE_PARDEFAUT_SYS_USER_RULES + "   " + Constantes.TYPE_COL_PERIMETRE_PARDEFAUT_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_PLANHABILITATION_SYS_USER_RULES + "   " + Constantes.TYPE_COL_PLANHABILITATION_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_MODIFICATION_DPP_AUTORISER_SYS_USER_RULES + "   " + Constantes.TYPE_COL_MODIFICATION_DPP_AUTORISER_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_REGULATION_DEMANDE_PARTICULIERE_SYS_USER_RULES + "   " + Constantes.TYPE_COL_REGULATION_DEMANDE_PARTICULIERE_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_ANONYMISER_SYS_USER_RULES + "   " + Constantes.TYPE_COL_ANONYMISER_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_BUREAU_PARDEFAUT_SYS_USER_RULES + "   " + Constantes.TYPE_COL_BUREAU_PARDEFAUT_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_LOCALISATIONPARDEFAUT_SYS_USER_RULES + "   " + Constantes.TYPE_COL_LOCALISATIONPARDEFAUT_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_REGULATION_AUTOMATIQUE_SYS_USER_RULES + "   " + Constantes.TYPE_COL_REGULATION_AUTOMATIQUE_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_ACCES_SERIALISATION_SYS_USER_RULES + "   " + Constantes.TYPE_COL_ACCES_SERIALISATION_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_SERIALISATION_IDENTIFIANT_SYS_USER_RULES + "   " + Constantes.TYPE_COL_SERIALISATION_IDENTIFIANT_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_SERIALISATION_MDP_SYS_USER_RULES + "   " + Constantes.TYPE_COL_SERIALISATION_MDP_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_SERIALISATION_TAN_SYS_USER_RULES + "   " + Constantes.TYPE_COL_SERIALISATION_TAN_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_SERIALISATION_CLIENTLOGINID_SYS_USER_RULES + "   " + Constantes.TYPE_COL_SERIALISATION_CLIENTLOGINID_SYS_USER_RULES + " , "
                + Constantes.CLE_COL_SERIALISATIONTERMSETCONDITIONS_SYS_USER_RULES + "   " + Constantes.TYPE_COL_SERIALISATIONTERMSETCONDITIONS_SYS_USER_RULES
                + " ); ";
    }

}
