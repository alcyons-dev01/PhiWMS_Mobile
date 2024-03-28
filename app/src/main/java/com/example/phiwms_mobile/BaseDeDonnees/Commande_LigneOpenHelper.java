package com.example.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
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

import com.example.phiwms_mobile.Classes.Commande_Ligne;
import com.example.phiwms_mobile.Outils.Alerte;
import com.example.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import com.example.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 19/06/2017.
 */

public class Commande_LigneOpenHelper extends DBOpenHelper {

    public Commande_LigneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static long insererUnCommande_LigneEnBDD(SQLiteDatabase db, Commande_Ligne commande_ligne) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_COMMANDE_UID_COMMANDE_LIGNE, commande_ligne.getCommande_UID());
        contentValues.put(Constantes.CLE_COL_RIEN_COMMANDE_LIGNE, commande_ligne.getRien());
        contentValues.put(Constantes.CLE_COL_PRODUIT_REFERENCE_COMMANDE_LIGNE, commande_ligne.getProduit_Reference());
        contentValues.put(Constantes.CLE_COL_TYPE_PRODUIT_COMMANDE_LIGNE, commande_ligne.getType_produit());
        contentValues.put(Constantes.CLE_COL_QTE_COM_COMMANDE_LIGNE, commande_ligne.getQté_COM());
        contentValues.put(Constantes.CLE_COL_PU_COM_COMMANDE_LIGNE, commande_ligne.getPU_Com());
        contentValues.put(Constantes.CLE_COL_REMISE_COMMANDE_LIGNE, commande_ligne.getRemise());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_COMMANDE_LIGNE, commande_ligne.getDésignation());
        contentValues.put(Constantes.CLE_COL_CODE_PRODUIT_COMMANDE_LIGNE, commande_ligne.getCode_produit());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_COMMANDE_LIGNE, commande_ligne.getCatégorie());
        contentValues.put(Constantes.CLE_COL_UNITE_COMMANDE_LIGNE, commande_ligne.getUnité());
        contentValues.put(Constantes.CLE_COL_CONDITIONNEMENT_COMMANDE_LIGNE, commande_ligne.getConditionnement());
        contentValues.put(Constantes.CLE_COL_CODE_FOURNISSEU_COMMANDE_LIGNE, commande_ligne.getCode_fournisseu());
        contentValues.put(Constantes.CLE_COL_FOURNISSEUR_COMMANDE_LIGNE, commande_ligne.getFournisseur());
        contentValues.put(Constantes.CLE_COL_MONTANT_HT_COMMANDE_LIGNE, commande_ligne.getMontant_HT());
        contentValues.put(Constantes.CLE_COL__UID_COMMANDE_LIGNE, commande_ligne.get_UID());
        contentValues.put(Constantes.CLE_COL_TX_TVA_COMMANDE_LIGNE, commande_ligne.getTx_TVA());
        contentValues.put(Constantes.CLE_COL_MTTC_LIGNE_COMMANDE_LIGNE, commande_ligne.getMTTC_ligne());
        contentValues.put(Constantes.CLE_COL_PEREMPTION_COMMANDE_LIGNE, commande_ligne.getPeremption());
        contentValues.put(Constantes.CLE_COL_QTE_RAL_COMMANDE_LIGNE, commande_ligne.getQte_RAL());
        contentValues.put(Constantes.CLE_COL_QTE_RAF_COMMANDE_LIGNE, commande_ligne.getQte_RAF());
        contentValues.put(Constantes.CLE_COL_GRATUIT_COMMANDE_LIGNE, commande_ligne.getGratuit());
        contentValues.put(Constantes.CLE_COL_DEVISE_COMMANDE_LIGNE, commande_ligne.getDevise());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_COMMANDE_LIGNE, commande_ligne.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_COMMANDE_LIGNE, commande_ligne.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_COMMANDE_LIGNE, commande_ligne.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_NUM_BL_COMMANDE_LIGNE, commande_ligne.getNum_BL());
        contentValues.put(Constantes.CLE_COL_RAFF_TTC_COMMANDE_LIGNE, commande_ligne.getRAFF_TTC());
        contentValues.put(Constantes.CLE_COL_SECTION_ANALYTIQUE_COMMANDE_LIGNE, commande_ligne.getSection_Analytique());
        contentValues.put(Constantes.CLE_COL_ID_MVT_COMMANDE_LIGNE, commande_ligne.getID_MVT());
        contentValues.put(Constantes.CLE_COL_PU_FACT_COMMANDE_LIGNE, commande_ligne.getPU_FACT());
        contentValues.put(Constantes.CLE_COL_QTE_FACT_COMMANDE_LIGNE, commande_ligne.getQte_Fact());
        contentValues.put(Constantes.CLE_COL_CLASSE_N_COMMANDE_LIGNE, commande_ligne.getClasse_N());
        contentValues.put(Constantes.CLE_COL_NBCOLIS_COMMANDE_LIGNE, commande_ligne.getNbColis());
        contentValues.put(Constantes.CLE_COL_NBPALETTE_COMMANDE_LIGNE, commande_ligne.getNbPalette());
        contentValues.put(Constantes.CLE_COL_POIDS_COMMANDE_LIGNE, commande_ligne.getPoids());
        contentValues.put(Constantes.CLE_COL_RAF_HT_COMMANDE_LIGNE, commande_ligne.getRAF_HT());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_COMMANDE_LIGNE, null, contentValues);

        commande_ligne.setPhiMR4UUID((int) rowId);

        return rowId;
    }

    public static void viderTableCommande_Ligne(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_COMMANDE_LIGNE, null, null);
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_COMMANDE_LIGNE = "Commande_ligne";

        public static final String CLE_COL_RIEN_COMMANDE_LIGNE = "Rien";
        public static final int NUM_COL_RIEN_COMMANDE_LIGNE = 1;
        public static final String TYPE_COL_RIEN_COMMANDE_LIGNE = "INTEGER";
        public static final String CLE_COL_PRODUIT_REFERENCE_COMMANDE_LIGNE = "produit_Reference";
        public static final int NUM_COL_PRODUIT_REFERENCE_COMMANDE_LIGNE = 2;
        public static final String TYPE_COL_PRODUIT_REFERENCE_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_TYPE_PRODUIT_COMMANDE_LIGNE = "Type_produit";
        public static final int NUM_COL_TYPE_PRODUIT_COMMANDE_LIGNE = 3;
        public static final String TYPE_COL_TYPE_PRODUIT_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_QTE_COM_COMMANDE_LIGNE = "Qté_COM";
        public static final int NUM_COL_QTE_COM_COMMANDE_LIGNE = 4;
        public static final String TYPE_COL_QTE_COM_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_PU_COM_COMMANDE_LIGNE = "PU_Com";
        public static final int NUM_COL_PU_COM_COMMANDE_LIGNE = 5;
        public static final String TYPE_COL_PU_COM_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_REMISE_COMMANDE_LIGNE = "Remise";
        public static final int NUM_COL_REMISE_COMMANDE_LIGNE = 6;
        public static final String TYPE_COL_REMISE_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_DESIGNATION_COMMANDE_LIGNE = "Désignation";
        public static final int NUM_COL_DESIGNATION_COMMANDE_LIGNE = 7;
        public static final String TYPE_COL_DESIGNATION_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_CODE_PRODUIT_COMMANDE_LIGNE = "Code_produit";
        public static final int NUM_COL_CODE_PRODUIT_COMMANDE_LIGNE = 8;
        public static final String TYPE_COL_CODE_PRODUIT_COMMANDE_LIGNE = "INTEGER";
        public static final String CLE_COL_CATEGORIE_COMMANDE_LIGNE = "Catégorie";
        public static final int NUM_COL_CATEGORIE_COMMANDE_LIGNE = 9;
        public static final String TYPE_COL_CATEGORIE_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_UNITE_COMMANDE_LIGNE = "Unité";
        public static final int NUM_COL_UNITE_COMMANDE_LIGNE = 10;
        public static final String TYPE_COL_UNITE_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_CONDITIONNEMENT_COMMANDE_LIGNE = "Conditionnement";
        public static final int NUM_COL_CONDITIONNEMENT_COMMANDE_LIGNE = 11;
        public static final String TYPE_COL_CONDITIONNEMENT_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_CODE_FOURNISSEU_COMMANDE_LIGNE = "Code_fournisseu";
        public static final int NUM_COL_CODE_FOURNISSEU_COMMANDE_LIGNE = 12;
        public static final String TYPE_COL_CODE_FOURNISSEU_COMMANDE_LIGNE = "INTEGER";
        public static final String CLE_COL_FOURNISSEUR_COMMANDE_LIGNE = "Fournisseur";
        public static final int NUM_COL_FOURNISSEUR_COMMANDE_LIGNE = 13;
        public static final String TYPE_COL_FOURNISSEUR_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_MONTANT_HT_COMMANDE_LIGNE = "Montant_HT";
        public static final int NUM_COL_MONTANT_HT_COMMANDE_LIGNE = 14;
        public static final String TYPE_COL_MONTANT_HT_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL__UID_COMMANDE_LIGNE = "_UID";
        public static final int NUM_COL__UID_COMMANDE_LIGNE = 15;
        public static final String TYPE_COL__UID_COMMANDE_LIGNE = "INTEGER";
        public static final String CLE_COL_TX_TVA_COMMANDE_LIGNE = "Tx_TVA";
        public static final int NUM_COL_TX_TVA_COMMANDE_LIGNE = 16;
        public static final String TYPE_COL_TX_TVA_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_MTTC_LIGNE_COMMANDE_LIGNE = "MTTC_ligne";
        public static final int NUM_COL_MTTC_LIGNE_COMMANDE_LIGNE = 17;
        public static final String TYPE_COL_MTTC_LIGNE_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_PEREMPTION_COMMANDE_LIGNE = "Peremption";
        public static final int NUM_COL_PEREMPTION_COMMANDE_LIGNE = 18;
        public static final String TYPE_COL_PEREMPTION_COMMANDE_LIGNE = "INTEGER";
        public static final String CLE_COL_QTE_RAL_COMMANDE_LIGNE = "Qte_RAL";
        public static final int NUM_COL_QTE_RAL_COMMANDE_LIGNE = 19;
        public static final String TYPE_COL_QTE_RAL_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_QTE_RAF_COMMANDE_LIGNE = "Qte_RAF";
        public static final int NUM_COL_QTE_RAF_COMMANDE_LIGNE = 20;
        public static final String TYPE_COL_QTE_RAF_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_GRATUIT_COMMANDE_LIGNE = "Gratuit";
        public static final int NUM_COL_GRATUIT_COMMANDE_LIGNE = 21;
        public static final String TYPE_COL_GRATUIT_COMMANDE_LIGNE = "INTEGER";
        public static final String CLE_COL_DEVISE_COMMANDE_LIGNE = "Devise";
        public static final int NUM_COL_DEVISE_COMMANDE_LIGNE = 22;
        public static final String TYPE_COL_DEVISE_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_COMMANDE_LIGNE = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_COMMANDE_LIGNE = 23;
        public static final String TYPE_COL_SYS_DT_MAJ_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_COMMANDE_LIGNE = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_COMMANDE_LIGNE = 24;
        public static final String TYPE_COL_SYS_HEURE_MAJ_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_COMMANDE_LIGNE = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_COMMANDE_LIGNE = 25;
        public static final String TYPE_COL_SYS_USER_MAJ_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_NUM_BL_COMMANDE_LIGNE = "Num_BL";
        public static final int NUM_COL_NUM_BL_COMMANDE_LIGNE = 26;
        public static final String TYPE_COL_NUM_BL_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_RAFF_TTC_COMMANDE_LIGNE = "RAFF_TTC";
        public static final int NUM_COL_RAFF_TTC_COMMANDE_LIGNE = 27;
        public static final String TYPE_COL_RAFF_TTC_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_SECTION_ANALYTIQUE_COMMANDE_LIGNE = "Section_Analytique";
        public static final int NUM_COL_SECTION_ANALYTIQUE_COMMANDE_LIGNE = 28;
        public static final String TYPE_COL_SECTION_ANALYTIQUE_COMMANDE_LIGNE = "TEXT";
        public static final String CLE_COL_ID_MVT_COMMANDE_LIGNE = "ID_MVT";
        public static final int NUM_COL_ID_MVT_COMMANDE_LIGNE = 29;
        public static final String TYPE_COL_ID_MVT_COMMANDE_LIGNE = "INTEGER";
        public static final String CLE_COL_PU_FACT_COMMANDE_LIGNE = "PU_FACT";
        public static final int NUM_COL_PU_FACT_COMMANDE_LIGNE = 30;
        public static final String TYPE_COL_PU_FACT_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_QTE_FACT_COMMANDE_LIGNE = "Qte_Fact";
        public static final int NUM_COL_QTE_FACT_COMMANDE_LIGNE = 31;
        public static final String TYPE_COL_QTE_FACT_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_CLASSE_N_COMMANDE_LIGNE = "Classe_N";
        public static final int NUM_COL_CLASSE_N_COMMANDE_LIGNE = 32;
        public static final String TYPE_COL_CLASSE_N_COMMANDE_LIGNE = "INTEGER";
        public static final String CLE_COL_NBCOLIS_COMMANDE_LIGNE = "NbColis";
        public static final int NUM_COL_NBCOLIS_COMMANDE_LIGNE = 33;
        public static final String TYPE_COL_NBCOLIS_COMMANDE_LIGNE = "INTEGER";
        public static final String CLE_COL_NBPALETTE_COMMANDE_LIGNE = "NbPalette";
        public static final int NUM_COL_NBPALETTE_COMMANDE_LIGNE = 34;
        public static final String TYPE_COL_NBPALETTE_COMMANDE_LIGNE = "INTEGER";
        public static final String CLE_COL_POIDS_COMMANDE_LIGNE = "Poids";
        public static final int NUM_COL_POIDS_COMMANDE_LIGNE = 35;
        public static final String TYPE_COL_POIDS_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_RAF_HT_COMMANDE_LIGNE = "RAF_HT";
        public static final int NUM_COL_RAF_HT_COMMANDE_LIGNE = 36;
        public static final String TYPE_COL_RAF_HT_COMMANDE_LIGNE = "REAL";
        public static final String CLE_COL_COMMANDE_UID_COMMANDE_LIGNE = "commande_UID";
        public static final int NUM_COL_COMMANDE_UID_COMMANDE_LIGNE = 37;
        public static final String TYPE_COL_COMMANDE_UID_COMMANDE_LIGNE = "INTEGER";


        public static final String CREATION_TABLE_COMMANDE_LIGNE = "CREATE TABLE " + Constantes.TABLE_COMMANDE_LIGNE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_RIEN_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_RIEN_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUIT_REFERENCE_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_PRODUIT_REFERENCE_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_TYPE_PRODUIT_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_TYPE_PRODUIT_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_COM_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_QTE_COM_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_PU_COM_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_PU_COM_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_REMISE_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_REMISE_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_DESIGNATION_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_DESIGNATION_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_PRODUIT_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_CODE_PRODUIT_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_CATEGORIE_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_CATEGORIE_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_UNITE_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_UNITE_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_CONDITIONNEMENT_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_CONDITIONNEMENT_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_FOURNISSEU_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_CODE_FOURNISSEU_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_FOURNISSEUR_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_FOURNISSEUR_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_MONTANT_HT_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_MONTANT_HT_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL__UID_COMMANDE_LIGNE + " " + Constantes.TYPE_COL__UID_COMMANDE_LIGNE + ","
                + Constantes.CLE_COL_TX_TVA_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_TX_TVA_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_MTTC_LIGNE_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_MTTC_LIGNE_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_PEREMPTION_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_PEREMPTION_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_RAL_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_QTE_RAL_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_RAF_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_QTE_RAF_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_GRATUIT_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_GRATUIT_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_DEVISE_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_DEVISE_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_SYS_DT_MAJ_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_SYS_USER_MAJ_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_NUM_BL_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_NUM_BL_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_RAFF_TTC_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_RAFF_TTC_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_SECTION_ANALYTIQUE_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_SECTION_ANALYTIQUE_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_ID_MVT_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_ID_MVT_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_PU_FACT_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_PU_FACT_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_FACT_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_QTE_FACT_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_CLASSE_N_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_CLASSE_N_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_NBCOLIS_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_NBCOLIS_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_NBPALETTE_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_NBPALETTE_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_POIDS_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_POIDS_COMMANDE_LIGNE + " ,"
                + Constantes.CLE_COL_RAF_HT_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_RAF_HT_COMMANDE_LIGNE + ","
                + Constantes.CLE_COL_COMMANDE_UID_COMMANDE_LIGNE + " " + Constantes.TYPE_COL_COMMANDE_UID_COMMANDE_LIGNE
                + ");";

    }

}
