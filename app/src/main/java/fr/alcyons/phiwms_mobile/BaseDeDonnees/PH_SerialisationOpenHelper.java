package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;


public class PH_SerialisationOpenHelper extends DBOpenHelper {

    public PH_SerialisationOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static PH_Serialisation getPH_SerialisationByPhiMR4UUID(SQLiteDatabase db, int id) {
        PH_Serialisation objet = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_SERIALISATION + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            objet = new PH_Serialisation(cursor);
        }
        cursor.close();
        cursor = null;
        return objet;
    }

    public static PH_Serialisation getPH_SerialisationByid(SQLiteDatabase db, int id) {
        PH_Serialisation objet = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_SERIALISATION + "   WHERE " + Constantes.CLE_COL__UID_PH_SERIALISATION + "=? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            objet = new PH_Serialisation(cursor);
        }

        cursor.close();
        cursor = null;
        return objet;
    }

    public static PH_Serialisation getPH_SerialisationByMultiple(SQLiteDatabase db, String GTIN, String Scheme, String Lot, String ExpDate, String Serie) {
        PH_Serialisation objet = null;
        if(GTIN.length() > 14)
            GTIN = GTIN.substring(2);
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_SERIALISATION + "   WHERE " + Constantes.CLE_COL_PRODUITCODEVALUE_PH_SERIALISATION + "=? and "+ Constantes.CLE_COL_PRODUITCODESHEME_PH_SERIALISATION + "=? and "+ Constantes.CLE_COL_NUMEROLOT_PH_SERIALISATION+ "=? and "+ Constantes.CLE_COL_DATEPEREMPTIONAAMMJJ_PH_SERIALISATION+ "=? and "+ Constantes.CLE_COL_NUMEROSERIE_PH_SERIALISATION+ "=? and "+Constantes.CLE_COL_STATUT_PH_SERIALISATION+" = 'En attente'", new String[]{GTIN, Scheme, Lot, ExpDate, Serie});

        if(cursor.getCount() == 0)
        {
            GTIN = "01"+GTIN;
            cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_SERIALISATION + "   WHERE " + Constantes.CLE_COL_PRODUITCODEVALUE_PH_SERIALISATION + "=? and "+ Constantes.CLE_COL_PRODUITCODESHEME_PH_SERIALISATION + "=? and "+ Constantes.CLE_COL_NUMEROLOT_PH_SERIALISATION+ "=? and "+ Constantes.CLE_COL_DATEPEREMPTIONAAMMJJ_PH_SERIALISATION+ "=? and "+ Constantes.CLE_COL_NUMEROSERIE_PH_SERIALISATION+ "=? and "+Constantes.CLE_COL_STATUT_PH_SERIALISATION+" = 'En attente'", new String[]{GTIN, Scheme, Lot, ExpDate, Serie});
        }

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            objet = new PH_Serialisation(cursor);
        }

        cursor.close();
        cursor = null;
        return objet;
    }

    public static PH_Serialisation getPH_SerialisationQuarantaine(SQLiteDatabase db, String GTIN, String Serie) {
        PH_Serialisation objet = null;
        if(GTIN.length() > 14)
            GTIN = GTIN.substring(2);
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_SERIALISATION + "   WHERE " + Constantes.CLE_COL_PRODUITCODEVALUE_PH_SERIALISATION + "=? and "+ Constantes.CLE_COL_NUMEROSERIE_PH_SERIALISATION+ "=? and "+Constantes.CLE_COL_REQTYPE_PH_SERIALISATION+" = 'G110'", new String[]{GTIN, Serie});

        if(cursor.getCount() == 0)
        {
            GTIN = "01"+GTIN;
            cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_SERIALISATION + "   WHERE " + Constantes.CLE_COL_PRODUITCODEVALUE_PH_SERIALISATION + "=? and "+ Constantes.CLE_COL_NUMEROSERIE_PH_SERIALISATION+ "=? and "+Constantes.CLE_COL_REQTYPE_PH_SERIALISATION+" = 'G110'", new String[]{GTIN, Serie});
        }

        if (cursor.getCount() >= 1) {
            cursor.moveToLast();
            objet = new PH_Serialisation(cursor);
        }

        cursor.close();
        cursor = null;
        return objet;
    }

    public static PH_Serialisation getPH_SerialisationVerrou(SQLiteDatabase db, String MvtUID, int ProduitId) {
        PH_Serialisation objet = null;
        Produit produit = ProduitOpenHelper.getProduitByID(db, ProduitId);

        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_SERIALISATION + "   WHERE " + Constantes.CLE_COL_MVTUID_PH_SERIALISATION + "=? and "+ Constantes.CLE_COL_PRODUITUID_PH_SERIALISATION+ "=? and "+Constantes.CLE_COL_PRODUITCODEVALUE_PH_SERIALISATION+"=? and "+ Constantes.CLE_COL_MVTTYPE_PH_SERIALISATION+"='CDE' and "+Constantes.CLE_COL_REQTYPE_PH_SERIALISATION+" = 'G110'", new String[]{MvtUID, String.valueOf(ProduitId), produit.getGTIN()});

        if (cursor.getCount() >= 1) {
            cursor.moveToLast();
            objet = new PH_Serialisation(cursor);
        }

        cursor.close();
        cursor = null;
        return objet;
    }

    public static int getLastId(SQLiteDatabase db) {
        int id = 0;
        Cursor cursor = db.rawQuery("SELECT MAX("+Constantes.CLE_COL__UID_PH_SERIALISATION+") FROM " + Constantes.TABLE_PH_SERIALISATION , null);

        while(cursor.moveToNext()) {
            id = cursor.getInt(0);
        }

        cursor.close();
        cursor = null;
        return id;
    }

    public static List<PH_Serialisation> getListePH_SerialisationG196(SQLiteDatabase db, String numeroSerie, String produitCodeSheme, String produitCodeValue, String reqType) {

        String statut = "En attente";


        List<PH_Serialisation> phSerialisationList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_SERIALISATION + " WHERE " + Constantes.CLE_COL_NUMEROSERIE_PH_SERIALISATION + "=? and " + Constantes.CLE_COL_PRODUITCODESHEME_PH_SERIALISATION + "=? and " + Constantes.CLE_COL_PRODUITCODEVALUE_PH_SERIALISATION + "=? and " + Constantes.CLE_COL_REQTYPE_PH_SERIALISATION + "=? and " + Constantes.CLE_COL_STATUT_PH_SERIALISATION + "=? "
                , new String[]{numeroSerie, produitCodeSheme, produitCodeValue, reqType, statut});

        while (cursor.moveToNext()) {
            PH_Serialisation phSerialisation = new PH_Serialisation(cursor);
            phSerialisationList.add(phSerialisation);
        }
        cursor.close();
        cursor = null;
        return phSerialisationList;
    }

    public static void viderTablePH_Serialisation(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PH_SERIALISATION, null, null);
    }

    public static long insererPH_SerialisationEnBDD(SQLiteDatabase db, PH_Serialisation objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_PH_SERIALISATION, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_PRODUITUID_PH_SERIALISATION, objet.getProduitUID());
        contentValues.put(Constantes.CLE_COL_DATAMATRIX_PH_SERIALISATION, objet.getDatamatrix());
        contentValues.put(Constantes.CLE_COL_PRODUITCODEVALUE_PH_SERIALISATION, objet.getProduitCodeValue());
        contentValues.put(Constantes.CLE_COL_PRODUITCODESHEME_PH_SERIALISATION, objet.getProduitCodeSheme());
        contentValues.put(Constantes.CLE_COL_NUMEROLOT_PH_SERIALISATION, objet.getNumeroLot());
        contentValues.put(Constantes.CLE_COL_DATEPEREMPTIONAAMMJJ_PH_SERIALISATION, objet.getDatePeremptionAAMMJJ());
        contentValues.put(Constantes.CLE_COL_NUMEROSERIE_PH_SERIALISATION, objet.getNumeroSerie());
        contentValues.put(Constantes.CLE_COL_REFCLIENTTRXID_PH_SERIALISATION, objet.getRefClientTrxId());
        contentValues.put(Constantes.CLE_COL_REQTYPE_PH_SERIALISATION, objet.getReqType());
        contentValues.put(Constantes.CLE_COL_RESULTAT_PH_SERIALISATION, objet.getResultat());
        contentValues.put(Constantes.CLE_COL_STATUT_PH_SERIALISATION, objet.getStatut());
        contentValues.put(Constantes.CLE_COL_NMVSTRXID_PH_SERIALISATION, objet.getNMVSTrxId());
        contentValues.put(Constantes.CLE_COL_USERUID_PH_SERIALISATION, objet.getUserUID());
        contentValues.put(Constantes.CLE_COL_DEMANDEDATE_PH_SERIALISATION, objet.getDemandeDate());
        contentValues.put(Constantes.CLE_COL_DEMANDEHEURE_PH_SERIALISATION, objet.getDemandeHeure());
        contentValues.put(Constantes.CLE_COL_MVTTYPE_PH_SERIALISATION, objet.getMvtType());
        contentValues.put(Constantes.CLE_COL_MVTUID_PH_SERIALISATION, objet.getMvtUID());
        contentValues.put(Constantes.CLE_COL_RAISON_PH_SERIALISATION, objet.getRaison());
        long rowID = db.insert(Constantes.TABLE_PH_SERIALISATION, null, contentValues);

        objet.setSerialexpressUUID((int) rowID);

        return rowID;
    }

    public static long mettreAJourPH_SerialisationEnBDD(SQLiteDatabase db, PH_Serialisation objet) {
        // Récupération des valeurs du service à insérer
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_PH_SERIALISATION, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_PRODUITUID_PH_SERIALISATION, objet.getProduitUID());
        contentValues.put(Constantes.CLE_COL_DATAMATRIX_PH_SERIALISATION, objet.getDatamatrix());
        contentValues.put(Constantes.CLE_COL_PRODUITCODEVALUE_PH_SERIALISATION, objet.getProduitCodeValue());
        contentValues.put(Constantes.CLE_COL_PRODUITCODESHEME_PH_SERIALISATION, objet.getProduitCodeSheme());
        contentValues.put(Constantes.CLE_COL_NUMEROLOT_PH_SERIALISATION, objet.getNumeroLot());
        contentValues.put(Constantes.CLE_COL_DATEPEREMPTIONAAMMJJ_PH_SERIALISATION, objet.getDatePeremptionAAMMJJ());
        contentValues.put(Constantes.CLE_COL_NUMEROSERIE_PH_SERIALISATION, objet.getNumeroSerie());
        contentValues.put(Constantes.CLE_COL_REFCLIENTTRXID_PH_SERIALISATION, objet.getRefClientTrxId());
        contentValues.put(Constantes.CLE_COL_REQTYPE_PH_SERIALISATION, objet.getReqType());
        contentValues.put(Constantes.CLE_COL_RESULTAT_PH_SERIALISATION, objet.getResultat());
        contentValues.put(Constantes.CLE_COL_STATUT_PH_SERIALISATION, objet.getStatut());
        contentValues.put(Constantes.CLE_COL_NMVSTRXID_PH_SERIALISATION, objet.getNMVSTrxId());
        contentValues.put(Constantes.CLE_COL_USERUID_PH_SERIALISATION, objet.getUserUID());
        contentValues.put(Constantes.CLE_COL_DEMANDEDATE_PH_SERIALISATION, objet.getDemandeDate());
        contentValues.put(Constantes.CLE_COL_DEMANDEHEURE_PH_SERIALISATION, objet.getDemandeHeure());
        contentValues.put(Constantes.CLE_COL_MVTTYPE_PH_SERIALISATION, objet.getMvtType());
        contentValues.put(Constantes.CLE_COL_MVTUID_PH_SERIALISATION, objet.getMvtUID());
        contentValues.put(Constantes.CLE_COL_RAISON_PH_SERIALISATION, objet.getRaison());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID    , objet.getPhiMR4UUID());

        long rowID = db.update(Constantes.TABLE_PH_SERIALISATION, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + objet.getPhiMR4UUID(), null);

        return rowID;
    }

    private static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PH_SERIALISATION = "PH_Serialisation";
        public static final String CLE_COL__UID_PH_SERIALISATION = "_UID";
        public static final int NUM_COL__UID_PH_SERIALISATION = 1;
        public static final String TYPE_COL__UID_PH_SERIALISATION = "INTEGER";
        public static final String CLE_COL_PRODUITUID_PH_SERIALISATION = "produitUID";
        public static final int NUM_COL_PRODUITUID_PH_SERIALISATION = 2;
        public static final String TYPE_COL_PRODUITUID_PH_SERIALISATION = "INTEGER";
        public static final String CLE_COL_DATAMATRIX_PH_SERIALISATION = "datamatrix";
        public static final int NUM_COL_DATAMATRIX_PH_SERIALISATION = 3;
        public static final String TYPE_COL_DATAMATRIX_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_PRODUITCODEVALUE_PH_SERIALISATION = "produitCodeValue";
        public static final int NUM_COL_PRODUITCODEVALUE_PH_SERIALISATION = 4;
        public static final String TYPE_COL_PRODUITCODEVALUE_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_PRODUITCODESHEME_PH_SERIALISATION = "produitCodeSheme";
        public static final int NUM_COL_PRODUITCODESHEME_PH_SERIALISATION = 5;
        public static final String TYPE_COL_PRODUITCODESHEME_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_NUMEROLOT_PH_SERIALISATION = "numeroLot";
        public static final int NUM_COL_NUMEROLOT_PH_SERIALISATION = 6;
        public static final String TYPE_COL_NUMEROLOT_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_DATEPEREMPTIONAAMMJJ_PH_SERIALISATION = "datePeremptionAAMMJJ";
        public static final int NUM_COL_DATEPEREMPTIONAAMMJJ_PH_SERIALISATION = 7;
        public static final String TYPE_COL_DATEPEREMPTIONAAMMJJ_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_NUMEROSERIE_PH_SERIALISATION = "numeroSerie";
        public static final int NUM_COL_NUMEROSERIE_PH_SERIALISATION = 8;
        public static final String TYPE_COL_NUMEROSERIE_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_REFCLIENTTRXID_PH_SERIALISATION = "refClientTrxId";
        public static final int NUM_COL_REFCLIENTTRXID_PH_SERIALISATION = 9;
        public static final String TYPE_COL_REFCLIENTTRXID_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_REQTYPE_PH_SERIALISATION = "reqType";
        public static final int NUM_COL_REQTYPE_PH_SERIALISATION = 10;
        public static final String TYPE_COL_REQTYPE_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_RESULTAT_PH_SERIALISATION = "resultat";
        public static final int NUM_COL_RESULTAT_PH_SERIALISATION = 11;
        public static final String TYPE_COL_RESULTAT_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_STATUT_PH_SERIALISATION = "statut";
        public static final int NUM_COL_STATUT_PH_SERIALISATION = 12;
        public static final String TYPE_COL_STATUT_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_NMVSTRXID_PH_SERIALISATION = "NMVSTrxId";
        public static final int NUM_COL_NMVSTRXID_PH_SERIALISATION = 13;
        public static final String TYPE_COL_NMVSTRXID_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_USERUID_PH_SERIALISATION = "userUID";
        public static final int NUM_COL_USERUID_PH_SERIALISATION = 14;
        public static final String TYPE_COL_USERUID_PH_SERIALISATION = "INTEGER";
        public static final String CLE_COL_DEMANDEDATE_PH_SERIALISATION = "demandeDate";
        public static final int NUM_COL_DEMANDEDATE_PH_SERIALISATION = 15;
        public static final String TYPE_COL_DEMANDEDATE_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_DEMANDEHEURE_PH_SERIALISATION = "demandeHeure";
        public static final int NUM_COL_DEMANDEHEURE_PH_SERIALISATION = 16;
        public static final String TYPE_COL_DEMANDEHEURE_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_MVTTYPE_PH_SERIALISATION = "mvtType";
        public static final int NUM_COL_MVTTYPE_PH_SERIALISATION = 17;
        public static final String TYPE_COL_MVTTYPE_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_MVTUID_PH_SERIALISATION = "mvtUID";
        public static final int NUM_COL_MVTUID_PH_SERIALISATION = 18;
        public static final String TYPE_COL_MVTUID_PH_SERIALISATION = "TEXT";
        public static final String CLE_COL_RAISON_PH_SERIALISATION = "raison";
        public static final int NUM_COL_RAISON_PH_SERIALISATION = 19;
        public static final String TYPE_COL_RAISON_PH_SERIALISATION = "TEXT";

        public static final String CREATION_TABLE_PH_SERIALISATION = " CREATE TABLE       " + Constantes.TABLE_PH_SERIALISATION
                + "(" +
                DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL__UID_PH_SERIALISATION + " " + Constantes.TYPE_COL__UID_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_PRODUITUID_PH_SERIALISATION + " " + Constantes.TYPE_COL_PRODUITUID_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_DATAMATRIX_PH_SERIALISATION + " " + Constantes.TYPE_COL_DATAMATRIX_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_PRODUITCODEVALUE_PH_SERIALISATION + " " + Constantes.TYPE_COL_PRODUITCODEVALUE_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_PRODUITCODESHEME_PH_SERIALISATION + " " + Constantes.TYPE_COL_PRODUITCODESHEME_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_NUMEROLOT_PH_SERIALISATION + " " + Constantes.TYPE_COL_NUMEROLOT_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_DATEPEREMPTIONAAMMJJ_PH_SERIALISATION + " " + Constantes.TYPE_COL_DATEPEREMPTIONAAMMJJ_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_NUMEROSERIE_PH_SERIALISATION + " " + Constantes.TYPE_COL_NUMEROSERIE_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_REFCLIENTTRXID_PH_SERIALISATION + " " + Constantes.TYPE_COL_REFCLIENTTRXID_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_REQTYPE_PH_SERIALISATION + " " + Constantes.TYPE_COL_REQTYPE_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_RESULTAT_PH_SERIALISATION + " " + Constantes.TYPE_COL_RESULTAT_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_STATUT_PH_SERIALISATION + " " + Constantes.TYPE_COL_STATUT_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_NMVSTRXID_PH_SERIALISATION + " " + Constantes.TYPE_COL_NMVSTRXID_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_USERUID_PH_SERIALISATION + " " + Constantes.TYPE_COL_USERUID_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_DEMANDEDATE_PH_SERIALISATION + " " + Constantes.TYPE_COL_DEMANDEDATE_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_DEMANDEHEURE_PH_SERIALISATION + " " + Constantes.TYPE_COL_DEMANDEHEURE_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_MVTTYPE_PH_SERIALISATION + " " + Constantes.TYPE_COL_MVTTYPE_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_MVTUID_PH_SERIALISATION + " " + Constantes.TYPE_COL_MVTUID_PH_SERIALISATION + " , "
                + Constantes.CLE_COL_RAISON_PH_SERIALISATION + " " + Constantes.TYPE_COL_RAISON_PH_SERIALISATION
                + " ); ";

    }

}
