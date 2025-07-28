package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

public class ParametresServeurOpenHelper extends DBOpenHelper {

    public ParametresServeurOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static int getNbParametresServeur(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        int nbParametresServeur = cursor.getCount();
        cursor.close();
        cursor = null;
        return nbParametresServeur;
    }

    public static void viderTableParametresServeur(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PARAMETRES_SERVEUR, null, null);
    }

    public static long updateParametreMailEnBDD(SQLiteDatabase db, String Mail_Emetteur, String MDP_Emetteur, int SMTP_Port, String SMTP_Serveur)
    {
        long rowID = -1;

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_MailEmetteur_PARAMETRES_SERVEUR, Mail_Emetteur);
        contentValues.put(Constantes.CLE_COL_MDPEmetteur_PARAMETRES_SERVEUR, MDP_Emetteur);
        contentValues.put(Constantes.CLE_COL_SMTPPORT_PARAMETRES_SERVEUR, SMTP_Port);
        contentValues.put(Constantes.CLE_COL_SMTPSERVEUR_PARAMETRES_SERVEUR, SMTP_Serveur);

            rowID = db.update(Constantes.TABLE_PARAMETRES_SERVEUR, contentValues, null, null);


        return rowID;
    }

    public static long updateParametresServeurEnBDD(SQLiteDatabase db, String ipServeur, String portServeur, String version,
                                                    String mailPharmacie,
                                                    String publishKey, String subscribeKey,
                                                    String etablissementNom, int etablissementNumero, String etablissementLogoNom,
                                                    Boolean Reliquats_pour_prevision, Boolean Liv_indirecte_egal_Cond_achat, Boolean plan_de_cueillette, Boolean module_transport,
                                                    String Mail_Emetteur, String MDP_Emetteur, int SMTP_Port, String SMTP_Serveur, int SMTP_Session, String loginEmetteur) {
        viderTableParametresServeur(db);

        long rowID = -1;
        if (getNbParametresServeur(db) == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constantes.CLE_COL_IP_SERVEUR_PARAMETRES_SERVEUR, ipServeur);
            contentValues.put(Constantes.CLE_COL_PORT_PARAMETRES_SERVEUR, portServeur);
            contentValues.put(Constantes.CLE_COL_VERSION_API_PARAMETRES_SERVEUR, version);

            contentValues.put(Constantes.CLE_COL_MAIL_PHARMACIE_PARAMETRES_SERVEUR, mailPharmacie);

            contentValues.put(Constantes.CLE_COL_PUBLISH_KEY_PARAMETRES_SERVEUR, publishKey);
            contentValues.put(Constantes.CLE_COL_SUBSCRIBE_KEY_PARAMETRES_SERVEUR, subscribeKey);

            contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_PARAMETRES_SERVEUR, etablissementNom);
            contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_NUMERO_PARAMETRES_SERVEUR, etablissementNumero);
            contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_LOGO_NOM_PARAMETRES_SERVEUR, etablissementLogoNom);
            contentValues.put(Constantes.CLE_COL_RELIQUAT_POUR_PROVISION_PARAMETRES_SERVEUR, Reliquats_pour_prevision);
            contentValues.put(Constantes.CLE_COL_LIV_DIRECT_EGAL_COND_ACHAT_PARAMETRES_SERVEUR, Liv_indirecte_egal_Cond_achat);
            contentValues.put(Constantes.CLE_COL_PlanDeCueillette_Actif_PARAMETRES_SERVEUR, plan_de_cueillette);
            contentValues.put(Constantes.CLE_COL_Module_Transport_PARAMETRES_SERVEUR, module_transport);

            contentValues.put(Constantes.CLE_COL_MailEmetteur_PARAMETRES_SERVEUR, Mail_Emetteur);
            contentValues.put(Constantes.CLE_COL_MDPEmetteur_PARAMETRES_SERVEUR, MDP_Emetteur);
            contentValues.put(Constantes.CLE_COL_SMTPPORT_PARAMETRES_SERVEUR, SMTP_Port);
            contentValues.put(Constantes.CLE_COL_SMTPSERVEUR_PARAMETRES_SERVEUR, SMTP_Serveur);
            contentValues.put(Constantes.CLE_COL_SMTPSESSION_PARAMETRES_SERVEUR, SMTP_Session);
            contentValues.put(Constantes.CLE_COL_LOGIN_EMETEUR, loginEmetteur);

            rowID = db.insert(Constantes.TABLE_PARAMETRES_SERVEUR, null, contentValues);
        }

        return rowID;
    }

    public static String getIPServeur(SQLiteDatabase db) {
        String ipServeur = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            ipServeur = cursor.getString(Constantes.NUM_COL_IP_SERVEUR_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return ipServeur;
    }

    public static String getPortServeur(SQLiteDatabase db) {
        String portServeur = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            portServeur = cursor.getString(Constantes.NUM_COL_PORT_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return portServeur;
    }

    public static String getAPIVersion(SQLiteDatabase db) {
        String apiVersion = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            apiVersion = cursor.getString(Constantes.NUM_COL_VERSION_API_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return apiVersion;
    }

    public static Boolean getLiv_indirecte_egal_Cond_achat(SQLiteDatabase db) {
        Boolean Liv_indirecte_egal_Cond_achat = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            Liv_indirecte_egal_Cond_achat = recupererBooleen(cursor, Constantes.NUM_COL_LIV_DIRECT_EGAL_COND_ACHAT_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return Liv_indirecte_egal_Cond_achat;
    }

    public static Boolean getPlanDeCueilletteActif(SQLiteDatabase db)
    {
        Boolean plan_de_cueillette = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            plan_de_cueillette = recupererBooleen(cursor, Constantes.NUM_COL_PlanDeCueillette_Actif_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return plan_de_cueillette;
    }

    public static Boolean getModuleTransport(SQLiteDatabase db)
    {
        Boolean module_transport = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            module_transport = recupererBooleen(cursor, Constantes.NUM_COL_Module_Transport_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return module_transport;
    }

    public static Boolean getReliquats_pour_prevision(SQLiteDatabase db) {
        Boolean Reliquats_pour_prevision = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            Reliquats_pour_prevision = recupererBooleen(cursor, Constantes.NUM_COL_RELIQUAT_POUR_PROVISION_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return Reliquats_pour_prevision;
    }

    public static String getEtablissementNom(SQLiteDatabase db) {
        String etablissementNom = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            etablissementNom = cursor.getString(Constantes.NUM_COL_ETABLISSEMENT_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return etablissementNom;
    }

    public static String getMailPharmacie(SQLiteDatabase db) {
        String mailPharmacie = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            mailPharmacie = cursor.getString(Constantes.NUM_COL_MAIL_PHARMACIE_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return mailPharmacie;
    }

    public static String getMailEmetteur(SQLiteDatabase db) {
        String mailEmetteur = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            mailEmetteur = cursor.getString(Constantes.NUM_COL_MailEmetteur_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return mailEmetteur;
    }

    public static String getMDPEmetteur(SQLiteDatabase db) {
        String mdpEmetteur = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            mdpEmetteur = cursor.getString(Constantes.NUM_COL_MDPEmetteur_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return mdpEmetteur;
    }

    public static int getSMTPPort(SQLiteDatabase db) {
        int smtpPort = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            smtpPort = cursor.getInt(Constantes.NUM_COL_SMTPPORT_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return smtpPort;
    }

    public static String getSMTPServeur(SQLiteDatabase db) {
        String smtpServeur = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            smtpServeur = cursor.getString(Constantes.NUM_COL_SMTPSERVEUR_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return smtpServeur;
    }

    public static int getSMTPSession(SQLiteDatabase db) {
        int smtpSession = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            smtpSession = cursor.getInt(Constantes.NUM_COL_SMTPSESSION_PARAMETRES_SERVEUR);
        }
        cursor.close();
        cursor = null;
        return smtpSession;
    }

    public static String getLoginEmetteur(SQLiteDatabase db) {
        String loginEmetteur = "";

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            loginEmetteur = cursor.getString(Constantes.NUM_COL_LOGIN_EMETEUR);
        }
        cursor.close();
        cursor = null;
        return loginEmetteur;
    }

    public static String getPartieCommuneUrls(SQLiteDatabase db) {
        String urlARetourner = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();

            String base = "http://";
            String api = "/api/";
            String ipServeur = cursor.getString(Constantes.NUM_COL_IP_SERVEUR_PARAMETRES_SERVEUR);
            String portServeur = cursor.getString(Constantes.NUM_COL_PORT_PARAMETRES_SERVEUR);
            String versionServeur = cursor.getString(Constantes.NUM_COL_VERSION_API_PARAMETRES_SERVEUR);

            urlARetourner = base + ipServeur + ":" + portServeur + api + versionServeur + "/";
        }
        cursor.close();
        cursor = null;
        return urlARetourner;
    }

    public static String getUrlsWeb(SQLiteDatabase db) {
        String urlARetourner = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERVEUR, null);

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();

            String base = "http://";
            String ipServeur = cursor.getString(Constantes.NUM_COL_IP_SERVEUR_PARAMETRES_SERVEUR);

            urlARetourner = base + ipServeur+ ":80" + "/";
        }
        cursor.close();
        cursor = null;
        return urlARetourner;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PARAMETRES_SERVEUR = "ParametresServeur";

        public static final String CLE_COL_ID_PARAMETRES_SERVEUR = "id";
        public static final int NUM_COL_ID_PARAMETRES_SERVEUR = 0;
        public static final String TYPE_COL_ID_PARAMETRES_SERVEUR = "INTEGER";

        public static final String CLE_COL_IP_SERVEUR_PARAMETRES_SERVEUR = "ipServeur";
        public static final int NUM_COL_IP_SERVEUR_PARAMETRES_SERVEUR = 1;
        public static final String TYPE_COL_IP_SERVEUR_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_PORT_PARAMETRES_SERVEUR = "port";
        public static final int NUM_COL_PORT_PARAMETRES_SERVEUR = 2;
        public static final String TYPE_COL_PORT_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_VERSION_API_PARAMETRES_SERVEUR = "versionAPI";
        public static final int NUM_COL_VERSION_API_PARAMETRES_SERVEUR = 3;
        public static final String TYPE_COL_VERSION_API_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_MAIL_PHARMACIE_PARAMETRES_SERVEUR = "mailPharmacie";
        public static final int NUM_COL_MAIL_PHARMACIE_PARAMETRES_SERVEUR = 4;
        public static final String TYPE_COL_MAIL_PHARMACIE_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_PUBLISH_KEY_PARAMETRES_SERVEUR = "pubnubPublishKey";
        public static final int NUM_COL_PUBLISH_KEY_PARAMETRES_SERVEUR = 5;
        public static final String TYPE_COL_PUBLISH_KEY_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_SUBSCRIBE_KEY_PARAMETRES_SERVEUR = "pubnubSubscribeKey";
        public static final int NUM_COL_SUBSCRIBE_KEY_PARAMETRES_SERVEUR = 6;
        public static final String TYPE_COL_SUBSCRIBE_KEY_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_ETABLISSEMENT_PARAMETRES_SERVEUR = "etablissement";
        public static final int NUM_COL_ETABLISSEMENT_PARAMETRES_SERVEUR = 7;
        public static final String TYPE_COL_ETABLISSEMENT_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_ETABLISSEMENT_NUMERO_PARAMETRES_SERVEUR = "etablissementNumero";
        public static final int NUM_COL_ETABLISSEMENT_NUMERO_PARAMETRES_SERVEUR = 8;
        public static final String TYPE_COL_ETABLISSEMENT_NUMERO_PARAMETRES_SERVEUR = "INTEGER";

        public static final String CLE_COL_ETABLISSEMENT_LOGO_NOM_PARAMETRES_SERVEUR = "etablissementLogoNom";
        public static final int NUM_COL_ETABLISSEMENT_LOGO_NOM_PARAMETRES_SERVEUR = 9;
        public static final String TYPE_COL_ETABLISSEMENT_LOGO_NOM_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_RELIQUAT_POUR_PROVISION_PARAMETRES_SERVEUR = "Reliquats_pour_prevision";
        public static final int NUM_COL_RELIQUAT_POUR_PROVISION_PARAMETRES_SERVEUR = 10;
        public static final String TYPE_COL__RELIQUAT_POUR_PROVISION_PARAMETRES_SERVEUR = "INTEGER";

        public static final String CLE_COL_LIV_DIRECT_EGAL_COND_ACHAT_PARAMETRES_SERVEUR = "Liv_indirecte_egal_Cond_achat";
        public static final int NUM_COL_LIV_DIRECT_EGAL_COND_ACHAT_PARAMETRES_SERVEUR = 11;
        public static final String TYPE_COL_LIV_DIRECT_EGAL_COND_ACHAT_PARAMETRES_SERVEUR = "INTEGER";

        public static final String CLE_COL_PlanDeCueillette_Actif_PARAMETRES_SERVEUR = "PlanDeCueillette_Actif";
        public static final int NUM_COL_PlanDeCueillette_Actif_PARAMETRES_SERVEUR = 12;
        public static final String TYPE_COL_PlanDeCueillette_Actif_PARAMETRES_SERVEUR = "INTEGER";

        public static final String CLE_COL_Module_Transport_PARAMETRES_SERVEUR = "Module_Transport";
        public static final int NUM_COL_Module_Transport_PARAMETRES_SERVEUR = 13;
        public static final String TYPE_COL_Module_Transport_PARAMETRES_SERVEUR = "INTEGER";

        public static final String CLE_COL_MailEmetteur_PARAMETRES_SERVEUR = "Mail_Emetteur";
        public static final int NUM_COL_MailEmetteur_PARAMETRES_SERVEUR = 14;
        public static final String TYPE_COL_MailEmetteur_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_MDPEmetteur_PARAMETRES_SERVEUR = "MDP_Emetteur";
        public static final int NUM_COL_MDPEmetteur_PARAMETRES_SERVEUR = 15;
        public static final String TYPE_COL_MDPEmetteur_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_SMTPPORT_PARAMETRES_SERVEUR = "SMTP_Port";
        public static final int NUM_COL_SMTPPORT_PARAMETRES_SERVEUR = 16;
        public static final String TYPE_COL_SMTPPORT_PARAMETRES_SERVEUR = "INTEGER";

        public static final String CLE_COL_SMTPSERVEUR_PARAMETRES_SERVEUR = "SMTP_Serveur";
        public static final int NUM_COL_SMTPSERVEUR_PARAMETRES_SERVEUR = 17;
        public static final String TYPE_COL_SMTPSERVEUR_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_SMTPSESSION_PARAMETRES_SERVEUR = "SMTP_Session";
        public static final int NUM_COL_SMTPSESSION_PARAMETRES_SERVEUR = 18;
        public static final String TYPE_COL_SMTPSESSION_PARAMETRES_SERVEUR = "TEXT";

        public static final String CLE_COL_LOGIN_EMETEUR = "Login_Emeteur";
        public static final int NUM_COL_LOGIN_EMETEUR = 19;
        public static final String TYPE_CO_LOGIN_EMETEUR = "TEXT";

        public static final String CREATION_TABLE_PARAMETRES_SERVEUR = "CREATE TABLE " + TABLE_PARAMETRES_SERVEUR
                + "("
                + CLE_COL_ID_PARAMETRES_SERVEUR + " " + TYPE_COL_ID_PARAMETRES_SERVEUR + " PRIMARY KEY,"
                + CLE_COL_IP_SERVEUR_PARAMETRES_SERVEUR + " " + TYPE_COL_IP_SERVEUR_PARAMETRES_SERVEUR + ","
                + CLE_COL_PORT_PARAMETRES_SERVEUR + " " + TYPE_COL_PORT_PARAMETRES_SERVEUR + ","
                + CLE_COL_VERSION_API_PARAMETRES_SERVEUR + " " + TYPE_COL_VERSION_API_PARAMETRES_SERVEUR + ","
                + CLE_COL_MAIL_PHARMACIE_PARAMETRES_SERVEUR + " " + TYPE_COL_MAIL_PHARMACIE_PARAMETRES_SERVEUR + ","
                + CLE_COL_PUBLISH_KEY_PARAMETRES_SERVEUR + " " + TYPE_COL_PUBLISH_KEY_PARAMETRES_SERVEUR + ","
                + CLE_COL_SUBSCRIBE_KEY_PARAMETRES_SERVEUR + " " + TYPE_COL_SUBSCRIBE_KEY_PARAMETRES_SERVEUR + ","
                + CLE_COL_ETABLISSEMENT_PARAMETRES_SERVEUR + " " + TYPE_COL_ETABLISSEMENT_PARAMETRES_SERVEUR + ","
                + CLE_COL_ETABLISSEMENT_NUMERO_PARAMETRES_SERVEUR + " " + TYPE_COL_ETABLISSEMENT_NUMERO_PARAMETRES_SERVEUR + ","
                + CLE_COL_ETABLISSEMENT_LOGO_NOM_PARAMETRES_SERVEUR + " " + TYPE_COL_ETABLISSEMENT_LOGO_NOM_PARAMETRES_SERVEUR + ","
                + CLE_COL_RELIQUAT_POUR_PROVISION_PARAMETRES_SERVEUR + " " + TYPE_COL__RELIQUAT_POUR_PROVISION_PARAMETRES_SERVEUR + ","
                + CLE_COL_LIV_DIRECT_EGAL_COND_ACHAT_PARAMETRES_SERVEUR + " " + TYPE_COL_LIV_DIRECT_EGAL_COND_ACHAT_PARAMETRES_SERVEUR + ","
                + CLE_COL_PlanDeCueillette_Actif_PARAMETRES_SERVEUR + " " + TYPE_COL_PlanDeCueillette_Actif_PARAMETRES_SERVEUR + ","
                + CLE_COL_Module_Transport_PARAMETRES_SERVEUR + " " + TYPE_COL_Module_Transport_PARAMETRES_SERVEUR + ","
                + CLE_COL_MailEmetteur_PARAMETRES_SERVEUR + " " + TYPE_COL_MailEmetteur_PARAMETRES_SERVEUR + ","
                + CLE_COL_MDPEmetteur_PARAMETRES_SERVEUR + " " + TYPE_COL_MDPEmetteur_PARAMETRES_SERVEUR + ","
                + CLE_COL_SMTPPORT_PARAMETRES_SERVEUR + " " + TYPE_COL_SMTPPORT_PARAMETRES_SERVEUR + ","
                + CLE_COL_SMTPSERVEUR_PARAMETRES_SERVEUR + " " + TYPE_COL_SMTPSERVEUR_PARAMETRES_SERVEUR + ","
                + CLE_COL_SMTPSESSION_PARAMETRES_SERVEUR + " " + TYPE_COL_SMTPSESSION_PARAMETRES_SERVEUR + ","
                + CLE_COL_LOGIN_EMETEUR + " " + TYPE_CO_LOGIN_EMETEUR
                + ");";
    }

}
