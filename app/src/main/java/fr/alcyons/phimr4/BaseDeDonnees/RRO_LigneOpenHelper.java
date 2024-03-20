package fr.alcyons.phimr4.BaseDeDonnees;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.HashMap;
import java.util.Map;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.Classes.RRO_Ligne;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class RRO_LigneOpenHelper extends DBOpenHelper {

    public RRO_LigneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class Constantes implements BaseColumns {

        public static final String TABLE_RRO_LIGNE = "RRO_ligne";

        public static final String CLE_COL_REF_FRS_RRO_LIGNE = "Ref_Frs";
        public static final int NUM_COL_REF_FRS_RRO_LIGNE = 1;
        public static final String TYPE_COL_REF_FRS_RRO_LIGNE = "TEXT";
        public static final String CLE_COL_TYPE_PDT_RRO_LIGNE = "Type_Pdt";
        public static final int NUM_COL_TYPE_PDT_RRO_LIGNE = 2;
        public static final String TYPE_COL_TYPE_PDT_RRO_LIGNE = "TEXT";
        public static final String CLE_COL_QUANTITE_RRO_LIGNE = "Quantite";
        public static final int NUM_COL_QUANTITE_RRO_LIGNE = 3;
        public static final String TYPE_COL_QUANTITE_RRO_LIGNE = "REAL";
        public static final String CLE_COL_PU_RRO_LIGNE = "PU";
        public static final int NUM_COL_PU_RRO_LIGNE = 4;
        public static final String TYPE_COL_PU_RRO_LIGNE = "REAL";
        public static final String CLE_COL_DESIGNATION_RRO_LIGNE = "Designation";
        public static final int NUM_COL_DESIGNATION_RRO_LIGNE = 5;
        public static final String TYPE_COL_DESIGNATION_RRO_LIGNE = "TEXT";
        public static final String CLE_COL_CODE_PDT_RRO_LIGNE = "Code_Pdt";
        public static final int NUM_COL_CODE_PDT_RRO_LIGNE = 6;
        public static final String TYPE_COL_CODE_PDT_RRO_LIGNE = "INTEGER";
        public static final String CLE_COL_CATEGORIE_RRO_LIGNE = "Categorie";
        public static final int NUM_COL_CATEGORIE_RRO_LIGNE = 7;
        public static final String TYPE_COL_CATEGORIE_RRO_LIGNE = "TEXT";
        public static final String CLE_COL_UNITE_RRO_LIGNE = "Unite";
        public static final int NUM_COL_UNITE_RRO_LIGNE = 8;
        public static final String TYPE_COL_UNITE_RRO_LIGNE = "TEXT";
        public static final String CLE_COL_CONDITIONNEMENT_RRO_LIGNE = "Conditionnement";
        public static final int NUM_COL_CONDITIONNEMENT_RRO_LIGNE = 9;
        public static final String TYPE_COL_CONDITIONNEMENT_RRO_LIGNE = "REAL";
        public static final String CLE_COL_CODE_FRS_RRO_LIGNE = "Code_Frs";
        public static final int NUM_COL_CODE_FRS_RRO_LIGNE = 10;
        public static final String TYPE_COL_CODE_FRS_RRO_LIGNE = "INTEGER";
        public static final String CLE_COL_FOURNISSEUR_RRO_LIGNE = "Fournisseur";
        public static final int NUM_COL_FOURNISSEUR_RRO_LIGNE = 11;
        public static final String TYPE_COL_FOURNISSEUR_RRO_LIGNE = "TEXT";
        public static final String CLE_COL_MONTANT_HT_RRO_LIGNE = "Montant_HT";
        public static final int NUM_COL_MONTANT_HT_RRO_LIGNE = 12;
        public static final String TYPE_COL_MONTANT_HT_RRO_LIGNE = "REAL";
        public static final String CLE_COL__UID_RRO_LIGNE = "_UID";
        public static final int NUM_COL__UID_RRO_LIGNE = 13;
        public static final String TYPE_COL__UID_RRO_LIGNE = "INTEGER";
        public static final String CLE_COL_TX_TVA_RRO_LIGNE = "Tx_TVA";
        public static final int NUM_COL_TX_TVA_RRO_LIGNE = 14;
        public static final String TYPE_COL_TX_TVA_RRO_LIGNE = "REAL";
        public static final String CLE_COL_MT_TTC_LIGNE_RRO_LIGNE = "Mt_TTC_Ligne";
        public static final int NUM_COL_MT_TTC_LIGNE_RRO_LIGNE = 15;
        public static final String TYPE_COL_MT_TTC_LIGNE_RRO_LIGNE = "REAL";
        public static final String CLE_COL_DEVISE_RRO_LIGNE = "Devise";
        public static final int NUM_COL_DEVISE_RRO_LIGNE = 16;
        public static final String TYPE_COL_DEVISE_RRO_LIGNE = "TEXT";
        public static final String CLE_COL_ID_MVT_RRO_LIGNE = "ID_Mvt";
        public static final int NUM_COL_ID_MVT_RRO_LIGNE = 17;
        public static final String TYPE_COL_ID_MVT_RRO_LIGNE = "INTEGER";
        public static final String CLE_COL_CLASSE_N_RRO_LIGNE = "Classe_N";
        public static final int NUM_COL_CLASSE_N_RRO_LIGNE = 18;
        public static final String TYPE_COL_CLASSE_N_RRO_LIGNE = "INTEGER";
        public static final String CLE_COL_RRO_UID_RRO_LIGNE = "RRO_UID";
        public static final int NUM_COL_RRO_UID_RRO_LIGNE = 19;
        public static final String TYPE_COL_RRO_UID_RRO_LIGNE = "INTEGER";


        public static final String CREATION_TABLE_RRO_LIGNE = "CREATE TABLE " + Constantes.TABLE_RRO_LIGNE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_REF_FRS_RRO_LIGNE + " " + Constantes.TYPE_COL_REF_FRS_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_TYPE_PDT_RRO_LIGNE + " " + Constantes.TYPE_COL_TYPE_PDT_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_QUANTITE_RRO_LIGNE + " " + Constantes.TYPE_COL_QUANTITE_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_PU_RRO_LIGNE + " " + Constantes.TYPE_COL_PU_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_DESIGNATION_RRO_LIGNE + " " + Constantes.TYPE_COL_DESIGNATION_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_PDT_RRO_LIGNE + " " + Constantes.TYPE_COL_CODE_PDT_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_CATEGORIE_RRO_LIGNE + " " + Constantes.TYPE_COL_CATEGORIE_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_UNITE_RRO_LIGNE + " " + Constantes.TYPE_COL_UNITE_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_CONDITIONNEMENT_RRO_LIGNE + " " + Constantes.TYPE_COL_CONDITIONNEMENT_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_FRS_RRO_LIGNE + " " + Constantes.TYPE_COL_CODE_FRS_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_FOURNISSEUR_RRO_LIGNE + " " + Constantes.TYPE_COL_FOURNISSEUR_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_MONTANT_HT_RRO_LIGNE + " " + Constantes.TYPE_COL_MONTANT_HT_RRO_LIGNE + " ,"
                + Constantes.CLE_COL__UID_RRO_LIGNE + " " + Constantes.TYPE_COL__UID_RRO_LIGNE + ","
                + Constantes.CLE_COL_TX_TVA_RRO_LIGNE + " " + Constantes.TYPE_COL_TX_TVA_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_MT_TTC_LIGNE_RRO_LIGNE + " " + Constantes.TYPE_COL_MT_TTC_LIGNE_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_DEVISE_RRO_LIGNE + " " + Constantes.TYPE_COL_DEVISE_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_ID_MVT_RRO_LIGNE + " " + Constantes.TYPE_COL_ID_MVT_RRO_LIGNE + " ,"
                + Constantes.CLE_COL_CLASSE_N_RRO_LIGNE + " " + Constantes.TYPE_COL_CLASSE_N_RRO_LIGNE + ","
                + Constantes.CLE_COL_RRO_UID_RRO_LIGNE + " " + Constantes.TYPE_COL_RRO_UID_RRO_LIGNE
                + ");";
    }
}
