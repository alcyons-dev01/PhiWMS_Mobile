package com.example.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.BaseColumns;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.phiwms_mobile.AuthentificationActivity;
import com.example.phiwms_mobile.Classes.SurveillanceReference;
import com.example.phiwms_mobile.Classes.Utilisateur;
import com.example.phiwms_mobile.Outils.Alerte;

/**
 * Created by olivier on 26/02/2019.
 */

public class SurveillanceReferenceOpenHelper extends DBOpenHelper {

    public SurveillanceReferenceOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SurveillanceReference getPH_SerialisationByid(SQLiteDatabase db, int id) {
        SurveillanceReference objet = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_SURVEILLANCEREFERENCE + "   WHERE " + Constantes.CLE_COL__UID_SURVEILLANCEREFERENCE + "=? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            objet = new SurveillanceReference(cursor);
        }

        cursor.close();
        cursor = null;

        return objet;
    }

    public static long insererSurveillanceReferenceEnBDD(SQLiteDatabase db, SurveillanceReference objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_SURVEILLANCEREFERENCE, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_SURVEILLANCEDATE_SURVEILLANCEREFERENCE, objet.getSurveillanceDate());
        contentValues.put(Constantes.CLE_COL_SURVEILLANCEHEURE_SURVEILLANCEREFERENCE, objet.getSurveillanceHeure());
        contentValues.put(Constantes.CLE_COL_PRODUITID_SURVEILLANCEREFERENCE, objet.getProduitID());
        contentValues.put(Constantes.CLE_COL_SERIALISATIONID_SURVEILLANCEREFERENCE, objet.getSerialisationID());
        contentValues.put(Constantes.CLE_COL_MOTIF_SURVEILLANCEREFERENCE, objet.getMotif());
        contentValues.put(Constantes.CLE_COL_ACTIONAMENER_SURVEILLANCEREFERENCE, objet.getActionAMener());
        contentValues.put(Constantes.CLE_COL_STATUT_SURVEILLANCEREFERENCE, objet.getStatut());
        contentValues.put(Constantes.CLE_COL_TRAITEPAR_SURVEILLANCEREFERENCE, objet.getTraitePar());
        contentValues.put(Constantes.CLE_COL_TRAITEDATE_SURVEILLANCEREFERENCE, objet.getTraiteDate());
        contentValues.put(Constantes.CLE_COL_TRAITEHEURE_SURVEILLANCEREFERENCE, objet.getTraiteHeure());
        contentValues.put(Constantes.CLE_COL_PRODUITLOT_SURVEILLANCEREFERENCE, objet.getProduitLot());
        contentValues.put(Constantes.CLE_COL_PRODUITDATEPEREMPTION_SURVEILLANCEREFERENCE, objet.getProduitDatePéremption());
        contentValues.put(Constantes.CLE_COL_PRODUITNUMEROSERIE_SURVEILLANCEREFERENCE, objet.getProduitNumeroSerie());

        long rowID = db.insert(Constantes.TABLE_SURVEILLANCEREFERENCE, null, contentValues);

        objet.setSerialexpressUUID((int) rowID);

        return rowID;
    }

    public static SurveillanceReference getSurveillanceReferenceByPhiMR4UUID(SQLiteDatabase db, int id) {
        SurveillanceReference objet = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_SURVEILLANCEREFERENCE + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            objet = new SurveillanceReference(cursor);
        }
        return objet;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_SURVEILLANCEREFERENCE = "SurveillanceReference";
        public static final String CLE_COL__UID_SURVEILLANCEREFERENCE = "_UID";
        public static final int NUM_COL__UID_SURVEILLANCEREFERENCE = 1;
        public static final String TYPE_COL__UID_SURVEILLANCEREFERENCE = "INTEGER";
        public static final String CLE_COL_SURVEILLANCEDATE_SURVEILLANCEREFERENCE = "surveillanceDate";
        public static final int NUM_COL_SURVEILLANCEDATE_SURVEILLANCEREFERENCE = 2;
        public static final String TYPE_COL_SURVEILLANCEDATE_SURVEILLANCEREFERENCE = "TEXT";
        public static final String CLE_COL_SURVEILLANCEHEURE_SURVEILLANCEREFERENCE = "surveillanceHeure";
        public static final int NUM_COL_SURVEILLANCEHEURE_SURVEILLANCEREFERENCE = 3;
        public static final String TYPE_COL_SURVEILLANCEHEURE_SURVEILLANCEREFERENCE = "TEXT";
        public static final String CLE_COL_PRODUITID_SURVEILLANCEREFERENCE = "produitID";
        public static final int NUM_COL_PRODUITID_SURVEILLANCEREFERENCE = 4;
        public static final String TYPE_COL_PRODUITID_SURVEILLANCEREFERENCE = "INTEGER";
        public static final String CLE_COL_SERIALISATIONID_SURVEILLANCEREFERENCE = "serialisationID";
        public static final int NUM_COL_SERIALISATIONID_SURVEILLANCEREFERENCE = 5;
        public static final String TYPE_COL_SERIALISATIONID_SURVEILLANCEREFERENCE = "INTEGER";
        public static final String CLE_COL_MOTIF_SURVEILLANCEREFERENCE = "motif";
        public static final int NUM_COL_MOTIF_SURVEILLANCEREFERENCE = 6;
        public static final String TYPE_COL_MOTIF_SURVEILLANCEREFERENCE = "TEXT";
        public static final String CLE_COL_ACTIONAMENER_SURVEILLANCEREFERENCE = "actionAMener";
        public static final int NUM_COL_ACTIONAMENER_SURVEILLANCEREFERENCE = 7;
        public static final String TYPE_COL_ACTIONAMENER_SURVEILLANCEREFERENCE = "TEXT";
        public static final String CLE_COL_STATUT_SURVEILLANCEREFERENCE = "statut";
        public static final int NUM_COL_STATUT_SURVEILLANCEREFERENCE = 8;
        public static final String TYPE_COL_STATUT_SURVEILLANCEREFERENCE = "TEXT";
        public static final String CLE_COL_TRAITEPAR_SURVEILLANCEREFERENCE = "traitePar";
        public static final int NUM_COL_TRAITEPAR_SURVEILLANCEREFERENCE = 9;
        public static final String TYPE_COL_TRAITEPAR_SURVEILLANCEREFERENCE = "TEXT";
        public static final String CLE_COL_TRAITEDATE_SURVEILLANCEREFERENCE = "traiteDate";
        public static final int NUM_COL_TRAITEDATE_SURVEILLANCEREFERENCE = 10;
        public static final String TYPE_COL_TRAITEDATE_SURVEILLANCEREFERENCE = "TEXT";
        public static final String CLE_COL_TRAITEHEURE_SURVEILLANCEREFERENCE = "traiteHeure";
        public static final int NUM_COL_TRAITEHEURE_SURVEILLANCEREFERENCE = 11;
        public static final String TYPE_COL_TRAITEHEURE_SURVEILLANCEREFERENCE = "TEXT";
        public static final String CLE_COL_PRODUITLOT_SURVEILLANCEREFERENCE = "produitLot";
        public static final int NUM_COL_PRODUITLOT_SURVEILLANCEREFERENCE = 12;
        public static final String TYPE_COL_PRODUITLOT_SURVEILLANCEREFERENCE = "TEXT";
        public static final String CLE_COL_PRODUITDATEPEREMPTION_SURVEILLANCEREFERENCE = "produitDatePéremption";
        public static final int NUM_COL_PRODUITDATEPEREMPTION_SURVEILLANCEREFERENCE = 13;
        public static final String TYPE_COL_PRODUITDATEPEREMPTION_SURVEILLANCEREFERENCE = "TEXT";
        public static final String CLE_COL_PRODUITNUMEROSERIE_SURVEILLANCEREFERENCE = "produitNumeroSerie";
        public static final int NUM_COL_PRODUITNUMEROSERIE_SURVEILLANCEREFERENCE = 14;
        public static final String TYPE_COL_PRODUITNUMEROSERIE_SURVEILLANCEREFERENCE = "TEXT";

        public static final String CREATION_TABLE_SURVEILLANCEREFERENCE = " CREATE TABLE       " + Constantes.TABLE_SURVEILLANCEREFERENCE
                + "(" +
                DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL__UID_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL__UID_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_SURVEILLANCEDATE_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_SURVEILLANCEDATE_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_SURVEILLANCEHEURE_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_SURVEILLANCEHEURE_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_PRODUITID_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_PRODUITID_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_SERIALISATIONID_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_SERIALISATIONID_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_MOTIF_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_MOTIF_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_ACTIONAMENER_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_ACTIONAMENER_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_STATUT_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_STATUT_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_TRAITEPAR_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_TRAITEPAR_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_TRAITEDATE_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_TRAITEDATE_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_TRAITEHEURE_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_TRAITEHEURE_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_PRODUITLOT_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_PRODUITLOT_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_PRODUITDATEPEREMPTION_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_PRODUITDATEPEREMPTION_SURVEILLANCEREFERENCE + " , "
                + Constantes.CLE_COL_PRODUITNUMEROSERIE_SURVEILLANCEREFERENCE + "   " + Constantes.TYPE_COL_PRODUITNUMEROSERIE_SURVEILLANCEREFERENCE
                + " ); ";

    }
}
