package fr.alcyons.phimr4.BaseDeDonnees;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import fr.alcyons.phimr4.Classes.Facture_Ligne;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;

/**
 * Created by quentinlanusse on 27/06/2017.
 */

public class Facture_LigneOpenHelper extends DBOpenHelper {

    public Facture_LigneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class Constantes implements BaseColumns {

        public static final String TABLE_FACTURE_LIGNE = "Facture_ligne";

        public static final String CLE_COL_FACTURE_UID_FACTURE_LIGNE = "Facture_UID";
        public static final int NUM_COL_FACTURE_UID_FACTURE_LIGNE = 1;
        public static final String TYPE_COL_FACTURE_UID_FACTURE_LIGNE = "INTEGER";
        public static final String CLE_COL_REF_FRS_FACTURE_LIGNE = "Ref_Frs";
        public static final int NUM_COL_REF_FRS_FACTURE_LIGNE = 2;
        public static final String TYPE_COL_REF_FRS_FACTURE_LIGNE = "TEXT";
        public static final String CLE_COL_QTE_FACT_FACTURE_LIGNE = "Qté_FACT";
        public static final int NUM_COL_QTE_FACT_FACTURE_LIGNE = 3;
        public static final String TYPE_COL_QTE_FACT_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_QTE_COM_FACTURE_LIGNE = "Qté_Com";
        public static final int NUM_COL_QTE_COM_FACTURE_LIGNE = 4;
        public static final String TYPE_COL_QTE_COM_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_QTE_LIV_FACTURE_LIGNE = "Qté_LIV";
        public static final int NUM_COL_QTE_LIV_FACTURE_LIGNE = 5;
        public static final String TYPE_COL_QTE_LIV_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_PU_COM_FACTURE_LIGNE = "PU_COM";
        public static final int NUM_COL_PU_COM_FACTURE_LIGNE = 6;
        public static final String TYPE_COL_PU_COM_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_TAUX_TVA_FACTURE_LIGNE = "Taux_Tva";
        public static final int NUM_COL_TAUX_TVA_FACTURE_LIGNE = 7;
        public static final String TYPE_COL_TAUX_TVA_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_MHT_COM_FACTURE_LIGNE = "MHT_com";
        public static final int NUM_COL_MHT_COM_FACTURE_LIGNE = 8;
        public static final String TYPE_COL_MHT_COM_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_MHT_FACT_FACTURE_LIGNE = "MHT_Fact";
        public static final int NUM_COL_MHT_FACT_FACTURE_LIGNE = 9;
        public static final String TYPE_COL_MHT_FACT_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_PU_FACT_FACTURE_LIGNE = "PU_Fact";
        public static final int NUM_COL_PU_FACT_FACTURE_LIGNE = 10;
        public static final String TYPE_COL_PU_FACT_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_COMMANDE_FACTURE_LIGNE = "Commande";
        public static final int NUM_COL_COMMANDE_FACTURE_LIGNE = 11;
        public static final String TYPE_COL_COMMANDE_FACTURE_LIGNE = "TEXT";
        public static final String CLE_COL_DESIGNATION_FACTURE_LIGNE = "Désignation";
        public static final int NUM_COL_DESIGNATION_FACTURE_LIGNE = 12;
        public static final String TYPE_COL_DESIGNATION_FACTURE_LIGNE = "TEXT";
        public static final String CLE_COL_TTC_COM_FACTURE_LIGNE = "TTC_Com";
        public static final int NUM_COL_TTC_COM_FACTURE_LIGNE = 13;
        public static final String TYPE_COL_TTC_COM_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_TTC_FACT_FACTURE_LIGNE = "TTC_Fact";
        public static final int NUM_COL_TTC_FACT_FACTURE_LIGNE = 14;
        public static final String TYPE_COL_TTC_FACT_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_CODE_PRODUIT_FACTURE_LIGNE = "Code_produit";
        public static final int NUM_COL_CODE_PRODUIT_FACTURE_LIGNE = 15;
        public static final String TYPE_COL_CODE_PRODUIT_FACTURE_LIGNE = "INTEGER";
        public static final String CLE_COL_CODE_PIECE_FACTURE_LIGNE = "Code_piece";
        public static final int NUM_COL_CODE_PIECE_FACTURE_LIGNE = 16;
        public static final String TYPE_COL_CODE_PIECE_FACTURE_LIGNE = "INTEGER";
        public static final String CLE_COL_CATEGORIE_FACTURE_LIGNE = "Categorie";
        public static final int NUM_COL_CATEGORIE_FACTURE_LIGNE = 17;
        public static final String TYPE_COL_CATEGORIE_FACTURE_LIGNE = "TEXT";
        public static final String CLE_COL_FACT_TVA_FACTURE_LIGNE = "Fact_tva";
        public static final int NUM_COL_FACT_TVA_FACTURE_LIGNE = 18;
        public static final String TYPE_COL_FACT_TVA_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL_CODE_LIGNE_C_FACTURE_LIGNE = "Code_ligne_C";
        public static final int NUM_COL_CODE_LIGNE_C_FACTURE_LIGNE = 19;
        public static final String TYPE_COL_CODE_LIGNE_C_FACTURE_LIGNE = "INTEGER";
        public static final String CLE_COL_CODE_LIGNE_ST_FACTURE_LIGNE = "Code_ligne_ST";
        public static final int NUM_COL_CODE_LIGNE_ST_FACTURE_LIGNE = 20;
        public static final String TYPE_COL_CODE_LIGNE_ST_FACTURE_LIGNE = "INTEGER";
        public static final String CLE_COL_DEVISE_FACTURE_LIGNE = "Devise";
        public static final int NUM_COL_DEVISE_FACTURE_LIGNE = 21;
        public static final String TYPE_COL_DEVISE_FACTURE_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_FACTURE_LIGNE = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_FACTURE_LIGNE = 22;
        public static final String TYPE_COL_SYS_DT_MAJ_FACTURE_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_FACTURE_LIGNE = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_FACTURE_LIGNE = 23;
        public static final String TYPE_COL_SYS_HEURE_MAJ_FACTURE_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_FACTURE_LIGNE = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_FACTURE_LIGNE = 24;
        public static final String TYPE_COL_SYS_USER_MAJ_FACTURE_LIGNE = "TEXT";
        public static final String CLE_COL_SECTION_ANALYTIQUE_FACTURE_LIGNE = "Section_Analytique";
        public static final int NUM_COL_SECTION_ANALYTIQUE_FACTURE_LIGNE = 25;
        public static final String TYPE_COL_SECTION_ANALYTIQUE_FACTURE_LIGNE = "TEXT";
        public static final String CLE_COL_PU_FACTDEVISE_FACTURE_LIGNE = "PU_FactDevise";
        public static final int NUM_COL_PU_FACTDEVISE_FACTURE_LIGNE = 26;
        public static final String TYPE_COL_PU_FACTDEVISE_FACTURE_LIGNE = "REAL";
        public static final String CLE_COL__UID_FACTURE_LIGNE = "_UID";
        public static final int NUM_COL__UID_FACTURE_LIGNE = 27;
        public static final String TYPE_COL__UID_FACTURE_LIGNE = "INTEGER";

        public static final String CREATION_TABLE_FACTURE_LIGNE = "CREATE TABLE "
                + Constantes.TABLE_FACTURE_LIGNE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_FACTURE_UID_FACTURE_LIGNE + " " + Constantes.TYPE_COL_FACTURE_UID_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_REF_FRS_FACTURE_LIGNE + " " + Constantes.TYPE_COL_REF_FRS_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_FACT_FACTURE_LIGNE + " " + Constantes.TYPE_COL_QTE_FACT_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_COM_FACTURE_LIGNE + " " + Constantes.TYPE_COL_QTE_COM_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_LIV_FACTURE_LIGNE + " " + Constantes.TYPE_COL_QTE_LIV_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_PU_COM_FACTURE_LIGNE + " " + Constantes.TYPE_COL_PU_COM_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_TAUX_TVA_FACTURE_LIGNE + " " + Constantes.TYPE_COL_TAUX_TVA_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_MHT_COM_FACTURE_LIGNE + " " + Constantes.TYPE_COL_MHT_COM_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_MHT_FACT_FACTURE_LIGNE + " " + Constantes.TYPE_COL_MHT_FACT_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_PU_FACT_FACTURE_LIGNE + " " + Constantes.TYPE_COL_PU_FACT_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_COMMANDE_FACTURE_LIGNE + " " + Constantes.TYPE_COL_COMMANDE_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_DESIGNATION_FACTURE_LIGNE + " " + Constantes.TYPE_COL_DESIGNATION_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_TTC_COM_FACTURE_LIGNE + " " + Constantes.TYPE_COL_TTC_COM_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_TTC_FACT_FACTURE_LIGNE + " " + Constantes.TYPE_COL_TTC_FACT_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_PRODUIT_FACTURE_LIGNE + " " + Constantes.TYPE_COL_CODE_PRODUIT_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_PIECE_FACTURE_LIGNE + " " + Constantes.TYPE_COL_CODE_PIECE_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_CATEGORIE_FACTURE_LIGNE + " " + Constantes.TYPE_COL_CATEGORIE_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_FACT_TVA_FACTURE_LIGNE + " " + Constantes.TYPE_COL_FACT_TVA_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_LIGNE_C_FACTURE_LIGNE + " " + Constantes.TYPE_COL_CODE_LIGNE_C_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_LIGNE_ST_FACTURE_LIGNE + " " + Constantes.TYPE_COL_CODE_LIGNE_ST_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_DEVISE_FACTURE_LIGNE + " " + Constantes.TYPE_COL_DEVISE_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_FACTURE_LIGNE + " " + Constantes.TYPE_COL_SYS_DT_MAJ_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_FACTURE_LIGNE + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_FACTURE_LIGNE + " " + Constantes.TYPE_COL_SYS_USER_MAJ_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_SECTION_ANALYTIQUE_FACTURE_LIGNE + " " + Constantes.TYPE_COL_SECTION_ANALYTIQUE_FACTURE_LIGNE + " ,"
                + Constantes.CLE_COL_PU_FACTDEVISE_FACTURE_LIGNE + " " + Constantes.TYPE_COL_PU_FACTDEVISE_FACTURE_LIGNE + ","
                + Constantes.CLE_COL__UID_FACTURE_LIGNE + " " + Constantes.TYPE_COL__UID_FACTURE_LIGNE
                + ");";
    }
}
