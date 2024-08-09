package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SysUserOpenHelper extends DBOpenHelper {
    public static final String TABLE_SYSUSER = "SYSUSER";
    public SysUserOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
    public static final String CLE_COL_IDSQL = "idSQL";
    public static final int NUM_COL_IDSQL = 1;
    public static final String TYPE_COL_IDSQL = "INTEGER";

    public static final String CLE_COL_ID = "id";
    public static final int NUM_COL_ID = 2;
    public static final String TYPE_COL_ID = "INTEGER";

    public static final String CLE_COL_IDENTIFIANT = "identifiant";
    public static final int NUM_COL_IDENTIFIANT = 3;
    public static final String TYPE_COL_IDENTIFIANT = "STRING";

    public static final String CLE_COL_MDP = "mdp";
    public static final int NUM_COL_MDP = 4;
    public static final String TYPE_COL_MDP = "STRING";

    public static final String CLE_COL_PASSWORD = "password";
    public static final int NUM_COL_PASSWORD = 5;
    public static final String TYPE_COL_PASSWORD = "STRING";

    public static final String CLE_COL_PASSWORD_REQUESTED_AT = "password_requested_at";
    public static final int NUM_COL_PASSWORD_REQUESTED_AT = 6;
    public static final String TYPE_COL_PASSWORD_REQUESTED_AT = "DATETIME";

    public static final String CLE_COL_MAIL = "mail";
    public static final int NUM_COL_MAIL = 7;
    public static final String TYPE_COL_MAIL = "STRING";

    public static final String CLE_COL_ROLES = "roles";
    public static final int NUM_COL_ROLES = 8;
    public static final String TYPE_COL_ROLES = "ARRAY";

    public static final String CLE_COL_NOM = "nom";
    public static final int NUM_COL_NOM = 9;
    public static final String TYPE_COL_NOM = "STRING";

    public static final String CLE_COL_PRENOM = "prenom";
    public static final int NUM_COL_PRENOM = 10;
    public static final String TYPE_COL_PRENOM = "STRING";

    public static final String CLE_COL_CIVILITE = "civilite";
    public static final int NUM_COL_CIVILITE = 11;
    public static final String TYPE_COL_CIVILITE = "STRING";

    public static final String CLE_COL_PHOTO_IMAGE = "photo_image";
    public static final int NUM_COL_PHOTO_IMAGE = 12;
    public static final String TYPE_COL_PHOTO_IMAGE = "BLOB";

    public static final String CLE_COL_ADRESSE1 = "adresse1";
    public static final int NUM_COL_ADRESSE1 = 13;
    public static final String TYPE_COL_ADRESSE1 = "STRING";

    public static final String CLE_COL_ADRESSE2 = "adresse2";
    public static final int NUM_COL_ADRESSE2 = 14;
    public static final String TYPE_COL_ADRESSE2 = "STRING";

    public static final String CLE_COL_CP = "cp";
    public static final int NUM_COL_CP = 15;
    public static final String TYPE_COL_CP = "STRING";

    public static final String CLE_COL_VILLE = "ville";
    public static final int NUM_COL_VILLE = 16;
    public static final String TYPE_COL_VILLE = "STRING";

    public static final String CLE_COL_PROFIL = "profil";
    public static final int NUM_COL_PROFIL = 17;
    public static final String TYPE_COL_PROFIL = "INTEGER";

    public static final String CLE_COL_ETABLISSEMENT = "etablissement";
    public static final int NUM_COL_ETABLISSEMENT = 18;
    public static final String TYPE_COL_ETABLISSEMENT = "STRING";

    public static final String CLE_COL_IDCARD = "idCard";
    public static final int NUM_COL_IDCARD = 19;
    public static final String TYPE_COL_IDCARD = "STRING";

    public static final String CLE_COL_ACTIVE = "active";
    public static final int NUM_COL_ACTIVE = 20;
    public static final String TYPE_COL_ACTIVE = "BOOLEAN";

    public static final String CLE_COL_BLOQUE = "bloque";
    public static final int NUM_COL_BLOQUE = 21;
    public static final String TYPE_COL_BLOQUE = "BOOLEAN";

    public static final String CLE_COL_BLOQUEDATE = "bloqueDate";
    public static final int NUM_COL_BLOQUEDATE = 22;
    public static final String TYPE_COL_BLOQUEDATE = "DATE";

    public static final String CLE_COL_TELEPHONE = "telephone";
    public static final int NUM_COL_TELEPHONE = 23;
    public static final String TYPE_COL_TELEPHONE = "STRING";

    public static final String CLE_COL_PLANHABILITATION = "planHabilitation";
    public static final int NUM_COL_PLANHABILITATION = 24;
    public static final String TYPE_COL_PLANHABILITATION = "INTEGER";

    public static final String CLE_COL_DATEAJOUT = "dateAjout";
    public static final int NUM_COL_DATEAJOUT = 25;
    public static final String TYPE_COL_DATEAJOUT = "DATE";

    public static final String CLE_COL_DATEMODIF = "dateModif";
    public static final int NUM_COL_DATEMODIF = 26;
    public static final String TYPE_COL_DATEMODIF = "DATE";

    public static final String CLE_COL_PHOTO_LIEN = "photo_lien";
    public static final int NUM_COL_PHOTO_LIEN = 27;
    public static final String TYPE_COL_PHOTO_LIEN = "STRING";

    public static final String CLE_COL_SIGNATURE_IMAGE = "signature_image";
    public static final int NUM_COL_SIGNATURE_IMAGE = 28;
    public static final String TYPE_COL_SIGNATURE_IMAGE = "BLOB";

    public static final String CLE_COL_SIGNATURE_LIEN = "signature_lien";
    public static final int NUM_COL_SIGNATURE_LIEN = 29;
    public static final String TYPE_COL_SIGNATURE_LIEN = "STRING";

    public static final String CLE_COL_PROFIL_ALPHA = "profil_alpha";
    public static final int NUM_COL_PROFIL_ALPHA = 30;
    public static final String TYPE_COL_PROFIL_ALPHA = "STRING";

    public static final String CLE_COL_INITIALE = "initiale";
    public static final int NUM_COL_INITIALE = 31;
    public static final String TYPE_COL_INITIALE = "STRING";

    public static final String CLE_COL_RH_UID = "RH_UID";
    public static final int NUM_COL_RH_UID = 32;
    public static final String TYPE_COL_RH_UID = "INTEGER";

    public static final String CLE_COL_DEPOT_UID = "Depot_UID";
    public static final int NUM_COL_DEPOT_UID = 33;
    public static final String TYPE_COL_DEPOT_UID = "INTEGER";

    public static final String CLE_COL_TOKEN = "token";
    public static final int NUM_COL_TOKEN = 34;
    public static final String TYPE_COL_TOKEN = "STRING";

    public static final String CLE_COL_AUTHENTIFICATION_FORTE = "authentification_forte";
    public static final int NUM_COL_AUTHENTIFICATION_FORTE = 35;
    public static final String TYPE_COL_AUTHENTIFICATION_FORTE = "BOOLEAN";

    public static final String CLE_COL_ETABLISSEMENT_UID = "Etablissement_UID";
    public static final int NUM_COL_ETABLISSEMENT_UID = 36;
    public static final String TYPE_COL_ETABLISSEMENT_UID = "INTEGER";

    public static final String CREATION_TABLE_SysUser = "CREATE TABLE SYSUSER ( "
            +CLE_COL_IDSQL+ " " + TYPE_COL_IDSQL + ","
            +CLE_COL_ID+ " " + TYPE_COL_ID + ","
            +CLE_COL_IDENTIFIANT+ " " + TYPE_COL_IDENTIFIANT + ","
            +CLE_COL_MDP+ " " + TYPE_COL_MDP + ","
            +CLE_COL_PASSWORD+ " " + TYPE_COL_PASSWORD + ","
            +CLE_COL_PASSWORD_REQUESTED_AT+ " " + TYPE_COL_PASSWORD_REQUESTED_AT + ","
            +CLE_COL_MAIL+ " " + TYPE_COL_MAIL + ","
            +CLE_COL_ROLES+ " " + TYPE_COL_ROLES + ","
            +CLE_COL_NOM+ " " + TYPE_COL_NOM + ","
            +CLE_COL_PRENOM+ " " + TYPE_COL_PRENOM + ","
            +CLE_COL_CIVILITE+ " " + TYPE_COL_CIVILITE + ","
            +CLE_COL_PHOTO_IMAGE+ " " + TYPE_COL_PHOTO_IMAGE + ","
            +CLE_COL_ADRESSE1+ " " + TYPE_COL_ADRESSE1 + ","
            +CLE_COL_ADRESSE2+ " " + TYPE_COL_ADRESSE2 + ","
            +CLE_COL_CP+ " " + TYPE_COL_CP + ","
            +CLE_COL_VILLE+ " " + TYPE_COL_VILLE + ","
            +CLE_COL_PROFIL+ " " + TYPE_COL_PROFIL + ","
            +CLE_COL_ETABLISSEMENT+ " " + TYPE_COL_ETABLISSEMENT + ","
            +CLE_COL_IDCARD+ " " + TYPE_COL_IDCARD + ","
            +CLE_COL_ACTIVE+ " " + TYPE_COL_ACTIVE + ","
            +CLE_COL_BLOQUE+ " " + TYPE_COL_BLOQUE + ","
            +CLE_COL_BLOQUEDATE+ " " + TYPE_COL_BLOQUEDATE + ","
            +CLE_COL_TELEPHONE+ " " + TYPE_COL_TELEPHONE + ","
            +CLE_COL_PLANHABILITATION+ " " + TYPE_COL_PLANHABILITATION + ","
            +CLE_COL_DATEAJOUT+ " " + TYPE_COL_DATEAJOUT + ","
            +CLE_COL_DATEMODIF+ " " + TYPE_COL_DATEMODIF + ","
            +CLE_COL_PHOTO_LIEN+ " " + TYPE_COL_PHOTO_LIEN + ","
            +CLE_COL_SIGNATURE_IMAGE+ " " + TYPE_COL_SIGNATURE_IMAGE + ","
            +CLE_COL_SIGNATURE_LIEN+ " " + TYPE_COL_SIGNATURE_LIEN + ","
            +CLE_COL_PROFIL_ALPHA+ " " + TYPE_COL_PROFIL_ALPHA + ","
            +CLE_COL_INITIALE+ " " + TYPE_COL_INITIALE + ","
            +CLE_COL_RH_UID+ " " + TYPE_COL_RH_UID + ","
            +CLE_COL_DEPOT_UID+ " " + TYPE_COL_DEPOT_UID + ","
            +CLE_COL_TOKEN+ " " + TYPE_COL_TOKEN + ","
            +CLE_COL_AUTHENTIFICATION_FORTE+ " " + TYPE_COL_AUTHENTIFICATION_FORTE + ","
            +CLE_COL_ETABLISSEMENT_UID+ " " + TYPE_COL_ETABLISSEMENT_UID+ ")";
}