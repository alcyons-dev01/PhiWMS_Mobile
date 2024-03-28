package com.example.phiwms_mobile.BaseDeDonnees;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import com.example.phiwms_mobile.Classes.Parametres_Serialisation;

/**
 * Created by olivier on 26/02/2019.
 */

public class Parametres_SerialisationOpenHelper  extends DBOpenHelper {

    private static final int MY_SOCKET_TIMEOUT_MS = 100;

    public Parametres_SerialisationOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static Parametres_Serialisation getParametres_Serialisation(SQLiteDatabase db){
        Parametres_Serialisation objet=null;
        Cursor cursor=db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PARAMETRES_SERIALISATION, null);
        if(cursor.getCount()==1){
            cursor.moveToFirst();
            objet=new Parametres_Serialisation(cursor);
        }

        return objet;
    }

    public static class Constantes implements BaseColumns{
        public static final String TABLE_PARAMETRES_SERIALISATION="Parametres_Serialisation";
        public static final String CLE_COL_ID_PARAMETRES_SERIALISATION="ID";
        public static final int NUM_COL_ID_PARAMETRES_SERIALISATION=1;
        public static final String TYPE_COL_ID_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION="serveurAPI_host";
        public static final int NUM_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION=2;
        public static final String TYPE_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION="serveurLDAP_host";
        public static final int NUM_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION=3;
        public static final String TYPE_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION="communicationDiffere";
        public static final int NUM_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION=4;
        public static final String TYPE_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION="dispenserReception";
        public static final int NUM_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION=5;
        public static final String TYPE_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION="dispenserDelivrance";
        public static final int NUM_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION=6;
        public static final String TYPE_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION="stockParNumeroDeSerie";
        public static final int NUM_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION=7;
        public static final String TYPE_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION="serveurLDAP_port";
        public static final int NUM_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION=8;
        public static final String TYPE_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION="serveurLDAP_login";
        public static final int NUM_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION=9;
        public static final String TYPE_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION="serveurLDAP_password";
        public static final int NUM_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION=10;
        public static final String TYPE_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION="serveurLDAP_nomDomaine";
        public static final int NUM_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION=11;
        public static final String TYPE_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_DOSSIERVISION_PARAMETRES_SERIALISATION="dossierVision";
        public static final int NUM_COL_DOSSIERVISION_PARAMETRES_SERIALISATION=12;
        public static final String TYPE_COL_DOSSIERVISION_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION="franceMVO_identifiant";
        public static final int NUM_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION=13;
        public static final String TYPE_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION="franceMVO_mdp";
        public static final int NUM_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION=14;
        public static final String TYPE_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION="franceMVO_tan";
        public static final int NUM_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION=15;
        public static final String TYPE_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION="TEXT";

        public static final String CLE_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION="franceMVO_termesEtConditions";
        public static final int NUM_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION=16;
        public static final String TYPE_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CLE_COL_MODULEVISION_PARAMETRES_SERIALISATION="moduleVision";
        public static final int NUM_COL_MODULEVISION_PARAMETRES_SERIALISATION=17;
        public static final String TYPE_COL_MODULEVISION_PARAMETRES_SERIALISATION="INTEGER";

        public static final String CREATION_TABLE_PARAMETRES_SERIALISATION = " CREATE TABLE       " + Constantes.TABLE_PARAMETRES_SERIALISATION
                +"("+
                DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID+"    PRIMARY KEY,"
                + Constantes.CLE_COL_ID_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_ID_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_DOSSIERVISION_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_DOSSIERVISION_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION + " , "
                + Constantes.CLE_COL_MODULEVISION_PARAMETRES_SERIALISATION + "   " + Constantes.TYPE_COL_MODULEVISION_PARAMETRES_SERIALISATION
                + " ); ";

    }
}
