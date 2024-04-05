package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Service;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;

/**
 * Created by quentinlanusse on 01/06/2017.
 */

public class UtilisateurOpenHelper extends DBOpenHelper {

    public UtilisateurOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static Utilisateur getUtilisateurSelonCurseur(Cursor cursor, SQLiteDatabase db) {
        Utilisateur utilisateur = new Utilisateur(cursor);

        List<Service> servicesHabilites = ServiceOpenHelper.recupererListeServiceUtilisateur(utilisateur, db);

        utilisateur.setServicesHabilites(servicesHabilites);

        return utilisateur;
    }

    public static Utilisateur getUtilisateurByID(SQLiteDatabase db, int id) {
        Utilisateur utilisateur = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_UTILISATEUR + " WHERE " + Constantes.CLE_COL_ID_UTILISATEUR + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            utilisateur = getUtilisateurSelonCurseur(cursor, db);
        }

        cursor.close();
        cursor = null;
        return utilisateur;
    }

    public static boolean recupererBooleen(int valeur) {
        // Permet d'éviter de tenter d'insérer la valeur null dans un booléen
        boolean booleen = false;
        if (valeur == 1) {
            booleen = true;
        }
        return booleen;
    }

    public long insererUnUtilisateurEnBD(SQLiteDatabase db, Utilisateur utilisateur) {
        // Récupération des valeurs de l'utilisateur à insérer
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_UTILISATEUR, utilisateur.getId());
        contentValues.put(Constantes.CLE_COL_IDENTIFIANT_UTILISATEUR, utilisateur.getIdentifiant());
        contentValues.put(Constantes.CLE_COL_MDP_UTILISATEUR, utilisateur.getMdp());
        contentValues.put(Constantes.CLE_COL_MAIL_UTILISATEUR, utilisateur.getMail());
        contentValues.put(Constantes.CLE_COL_NOM_UTILISATEUR, utilisateur.getNom());
        contentValues.put(Constantes.CLE_COL_PRENOM_UTILISATEUR, utilisateur.getPrenom());
        contentValues.put(Constantes.CLE_COL_ACTIVE_UTILISATEUR, utilisateur.isActive());
        contentValues.put(Constantes.CLE_COL_PLAN_HABILITATION_UTILISATEUR, utilisateur.getPlanHabilitation());
        contentValues.put(Constantes.CLE_COL_BLOQUE_UTILISATEUR, utilisateur.isBloque());
        contentValues.put(Constantes.CLE_COL_DEPOT_UID_UTILISATEUR, utilisateur.getDepot_UID());
        contentValues.put(Constantes.CLE_COL_TOKEN_UTILISATEUR, utilisateur.getToken());
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_UTILISATEUR, utilisateur.getEtablissement());
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_ID_UTILISATEUR, utilisateur.getEtablissementId());
        if (utilisateur.getServicesHabilites() != null) {
            String listeServices = "{ \"Services\": [";
            for (Service serviceCourant : utilisateur.getServicesHabilites()
                    ) {
                listeServices += serviceCourant.toJsonString();
                listeServices += ",";
            }
            listeServices = listeServices.substring(0, listeServices.length() - 1);
            listeServices += "]}";
            contentValues.put(Constantes.CLE_COL_SERVICES_HABILITES_UTILISATEUR, listeServices);
        }

        if(utilisateur.getLastPerimetre() != 0)
        {
            int perimetreid = utilisateur.getLastPerimetre();
            contentValues.put(Constantes.CLE_COL_LAST_PERIMETRE, perimetreid);
        }


        // Insertion de l'utilisateur en BDD
        long rowID = db.insert(Constantes.TABLE_UTILISATEUR, null, contentValues);

        utilisateur.setphiwms_mobileUUID((int) rowID);

        return rowID;
    }

    public static long mettreAJourUtilisateur(SQLiteDatabase db, Utilisateur utilisateur) {
        // Récupération des valeurs de l'utilisateur à insérer
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_UTILISATEUR, utilisateur.getId());
        contentValues.put(Constantes.CLE_COL_IDENTIFIANT_UTILISATEUR, utilisateur.getIdentifiant());
        contentValues.put(Constantes.CLE_COL_MDP_UTILISATEUR, utilisateur.getMdp());
        contentValues.put(Constantes.CLE_COL_MAIL_UTILISATEUR, utilisateur.getMail());
        contentValues.put(Constantes.CLE_COL_NOM_UTILISATEUR, utilisateur.getNom());
        contentValues.put(Constantes.CLE_COL_PRENOM_UTILISATEUR, utilisateur.getPrenom());
        contentValues.put(Constantes.CLE_COL_ACTIVE_UTILISATEUR, utilisateur.isActive());
        contentValues.put(Constantes.CLE_COL_PLAN_HABILITATION_UTILISATEUR, utilisateur.getPlanHabilitation());
        contentValues.put(Constantes.CLE_COL_BLOQUE_UTILISATEUR, utilisateur.isBloque());
        contentValues.put(Constantes.CLE_COL_TOKEN_UTILISATEUR, utilisateur.getToken());
        contentValues.put(Constantes.CLE_COL_DEPOT_UID_UTILISATEUR, utilisateur.getDepot_UID());
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_UTILISATEUR, utilisateur.getEtablissement());
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_ID_UTILISATEUR, utilisateur.getEtablissementId());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, utilisateur.getphiwms_mobileUUID());
        if (utilisateur.getServicesHabilites() != null) {
            String listeServices = "{ \"Services\": [";
            for (Service service : utilisateur.getServicesHabilites()
                    ) {
                listeServices += service.toJsonString();
                listeServices += ",";
            }
            listeServices = listeServices.substring(0, listeServices.length() - 1);
            listeServices += "]}";
            contentValues.put(Constantes.CLE_COL_SERVICES_HABILITES_UTILISATEUR, listeServices);
        }
        else
        {
            String listeServices = "{ \"Services\": [";
            listeServices += "]}";

            contentValues.put(Constantes.CLE_COL_SERVICES_HABILITES_UTILISATEUR, listeServices);
        }

        if(utilisateur.getLastPerimetre() != 0)
        {
            int perimetreid = utilisateur.getLastPerimetre();
            contentValues.put(Constantes.CLE_COL_LAST_PERIMETRE, perimetreid);
        }


        long rowID = db.update(Constantes.TABLE_UTILISATEUR, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + utilisateur.getphiwms_mobileUUID(), null);

        return rowID;
    }

    public Utilisateur identifierUtilisateurLocalement(String identifiant, String mdp, SQLiteDatabase db) {
        Utilisateur utilisateur = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_UTILISATEUR + " WHERE " + Constantes.CLE_COL_IDENTIFIANT_UTILISATEUR + "=? and " + Constantes.CLE_COL_MDP_UTILISATEUR + "=?", new String[]{identifiant, mdp});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            utilisateur = getUtilisateurSelonCurseur(cursor, db);
        }

        cursor.close();
        cursor = null;
        return utilisateur;
    }

    public long mettreAJourToken(SQLiteDatabase db, Utilisateur utilisateur) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_TOKEN_UTILISATEUR, utilisateur.getToken());

        long rowId = db.update(Constantes.TABLE_UTILISATEUR, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + utilisateur.getphiwms_mobileUUID(), null);

        return rowId;
    }

    public long mettreAJourEtablissement(SQLiteDatabase db, Utilisateur utilisateur) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_UTILISATEUR, utilisateur.getEtablissement());

        long rowId = db.update(Constantes.TABLE_UTILISATEUR, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + utilisateur.getphiwms_mobileUUID(), null);

        return rowId;
    }

    public static class Constantes implements BaseColumns {

        public static final String TABLE_UTILISATEUR = "Utilisateur";


        public static final String CLE_COL_IDENTIFIANT_UTILISATEUR = "identifiant";
        public static final int NUM_COL_IDENTIFIANT_UTILISATEUR = 1;
        public static final String TYPE_COL_IDENTIFIANT_UTILISATEUR = "TEXT";

        public static final String CLE_COL_MDP_UTILISATEUR = "mdp";
        public static final int NUM_COL_MDP_UTILISATEUR = 2;
        public static final String TYPE_COL_MDP_UTILISATEUR = "TEXT";

        public static final String CLE_COL_MAIL_UTILISATEUR = "mail";
        public static final int NUM_COL_MAIL_UTILISATEUR = 3;
        public static final String TYPE_COL_MAIL_UTILISATEUR = "TEXT";

        public static final String CLE_COL_NOM_UTILISATEUR = "nom";
        public static final int NUM_COL_NOM_UTILISATEUR = 4;
        public static final String TYPE_COL_NOM_UTILISATEUR = "TEXT";

        public static final String CLE_COL_PRENOM_UTILISATEUR = "prenom";
        public static final int NUM_COL_PRENOM_UTILISATEUR = 5;
        public static final String TYPE_COL_PRENOM_UTILISATEUR = "TEXT";

        public static final String CLE_COL_ACTIVE_UTILISATEUR = "active";
        public static final int NUM_COL_ACTIVE_UTILISATEUR = 6;
        public static final String TYPE_COL_ACTIVE_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_PLAN_HABILITATION_UTILISATEUR = "planHabilitation";
        public static final int NUM_COL_PLAN_HABILITATION_UTILISATEUR = 7;
        public static final String TYPE_COL_PLAN_HABILITATION_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_BLOQUE_UTILISATEUR = "bloque";
        public static final int NUM_COL_BLOQUE_UTILISATEUR = 8;
        public static final String TYPE_COL_BLOQUE_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_TOKEN_UTILISATEUR = "token";
        public static final int NUM_COL_TOKEN_UTILISATEUR = 9;
        public static final String TYPE_COL_TOKEN_UTILISATEUR = "TEXT";

        public static final String CLE_COL_SERVICES_HABILITES_UTILISATEUR = "serviceshabilites";
        public static final int NUM_COL_SERVICES_HABILITES_UTILISATEUR = 10;
        public static final String TYPE_COL_SERVICES_HABILITES_UTILISATEUR = "TEXT";

        public static final String CLE_COL_ID_UTILISATEUR = "id";
        public static final int NUM_COL_ID_UTILISATEUR = 11;
        public static final String TYPE_COL_ID_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_DEPOT_UID_UTILISATEUR = "depot_UID";
        public static final int NUM_COL_DEPOT_UID_UTILISATEUR = 12;
        public static final String TYPE_COL_DEPOT_UID_UTILISATEUR = "INTEGER";

        public static final String CLE_COL_ETABLISSEMENT_UTILISATEUR = "Etablissement";
        public static final int NUM_COL_ETABLISSEMENT_UTILISATEUR = 13;
        public static final String TYPE_COL_ETABLISSEMENT_UTILISATEUR = "TEXT";

        public static final String CLE_COL_LAST_PERIMETRE = "LastPerimetreId";
        public static final int NUM_COL_LAST_PERIMETRE = 14;
        public static final String TYPE_COL_LAST_PERIMETRE = "INTEGER";

        public static final String CLE_COL_ETABLISSEMENT_ID_UTILISATEUR = "EtablissementId";
        public static final int NUM_COL_ETABLISSEMENT_ID_UTILISATEUR = 15;
        public static final String TYPE_COL_ETABLISSEMENT_ID_UTILISATEUR = "INTEGER";

        public static final String CREATION_TABLE_UTILISATEUR = "CREATE TABLE "
                + Constantes.TABLE_UTILISATEUR + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_IDENTIFIANT_UTILISATEUR + " " + Constantes.TYPE_COL_IDENTIFIANT_UTILISATEUR + ","
                + Constantes.CLE_COL_MDP_UTILISATEUR + " " + Constantes.TYPE_COL_MDP_UTILISATEUR + ","
                + Constantes.CLE_COL_MAIL_UTILISATEUR + " " + Constantes.TYPE_COL_MAIL_UTILISATEUR + ","
                + Constantes.CLE_COL_NOM_UTILISATEUR + " " + Constantes.TYPE_COL_NOM_UTILISATEUR + ","
                + Constantes.CLE_COL_PRENOM_UTILISATEUR + " " + Constantes.TYPE_COL_PRENOM_UTILISATEUR + ","
                + Constantes.CLE_COL_ACTIVE_UTILISATEUR + " " + Constantes.TYPE_COL_ACTIVE_UTILISATEUR + ","
                + Constantes.CLE_COL_PLAN_HABILITATION_UTILISATEUR + " " + Constantes.TYPE_COL_PLAN_HABILITATION_UTILISATEUR + ","
                + Constantes.CLE_COL_BLOQUE_UTILISATEUR + " " + Constantes.TYPE_COL_BLOQUE_UTILISATEUR + ","
                + Constantes.CLE_COL_TOKEN_UTILISATEUR + " " + Constantes.TYPE_COL_TOKEN_UTILISATEUR + ","
                + Constantes.CLE_COL_SERVICES_HABILITES_UTILISATEUR + " " + Constantes.TYPE_COL_SERVICES_HABILITES_UTILISATEUR + ","
                + Constantes.CLE_COL_ID_UTILISATEUR + " " + Constantes.TYPE_COL_ID_UTILISATEUR + ","
                + Constantes.CLE_COL_DEPOT_UID_UTILISATEUR + " " + Constantes.TYPE_COL_DEPOT_UID_UTILISATEUR + ","
                + Constantes.CLE_COL_ETABLISSEMENT_UTILISATEUR + " " + Constantes.TYPE_COL_ETABLISSEMENT_UTILISATEUR + ","
                + Constantes.CLE_COL_LAST_PERIMETRE + " " + Constantes.TYPE_COL_LAST_PERIMETRE + ","
                + Constantes.CLE_COL_ETABLISSEMENT_ID_UTILISATEUR + " " + Constantes.TYPE_COL_ETABLISSEMENT_ID_UTILISATEUR
                + ");";
    }

}
