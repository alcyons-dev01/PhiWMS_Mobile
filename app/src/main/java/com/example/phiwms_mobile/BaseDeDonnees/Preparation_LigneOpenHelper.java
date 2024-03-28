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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.phiwms_mobile.AuthentificationActivity;
import com.example.phiwms_mobile.Classes.Preparation_Ligne;
import com.example.phiwms_mobile.Outils.Alerte;
import com.example.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import com.example.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Preparation_LigneOpenHelper extends DBOpenHelper {

    public Preparation_LigneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static Preparation_Ligne getPreparationLigneByPhiMR4UUID(SQLiteDatabase db, int id) {
        Preparation_Ligne objet = null;

        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PREPARATION_LIGNE + " WHERE " + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=? ", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            objet = new Preparation_Ligne(cursor);
        }

        return objet;
    }

    public static List<Preparation_Ligne> getPreparationLigneByPreparation(SQLiteDatabase db, int preparation_id) {
        List<Preparation_Ligne> preparation_ligneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_ID_PREVISION_PREPARATION_LIGNE + "=?", new String[]{String.valueOf(preparation_id)});

        while (cursor.moveToNext()) {
            Preparation_Ligne preparation_ligneCourant = new Preparation_Ligne(cursor);
            preparation_ligneList.add(preparation_ligneCourant);
        }

        cursor.close();
        cursor = null;
        return preparation_ligneList;
    }


    public static long supprimerUnePreparationLigneParPreparation(SQLiteDatabase db, int preparationID){
        return db.delete(Constantes.TABLE_PREPARATION_LIGNE, Constantes.CLE_COL_ID_PREVISION_PREPARATION_LIGNE + "=?", new String[]{String.valueOf(preparationID)});

    }

    public static long insererUnPreparation_LigneEnBDD(SQLiteDatabase db, Preparation_Ligne preparation_ligne) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_PREPARATION_LIGNE, preparation_ligne.get_UID());
        contentValues.put(Constantes.CLE_COL_DU_PREPARATION_LIGNE, preparation_ligne.getDu());
        contentValues.put(Constantes.CLE_COL_AU_PREPARATION_LIGNE, preparation_ligne.getAu());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_PREPARATION_LIGNE, preparation_ligne.getRéf_depot());
        contentValues.put(Constantes.CLE_COL_CODE_IPP_PREPARATION_LIGNE, preparation_ligne.getCode_IPP());
        contentValues.put(Constantes.CLE_COL_NOM_PREPARATION_LIGNE, preparation_ligne.getNom());
        contentValues.put(Constantes.CLE_COL_CODE_PROD_PREPARATION_LIGNE, preparation_ligne.getCode_prod());
        contentValues.put(Constantes.CLE_COL_CODE_FRS_PREPARATION_LIGNE, preparation_ligne.getCode_frs());
        contentValues.put(Constantes.CLE_COL_FRS_PREPARATION_LIGNE, preparation_ligne.getFrs());
        contentValues.put(Constantes.CLE_COL_PRODUIT_PREPARATION_LIGNE, preparation_ligne.getProduit());
        contentValues.put(Constantes.CLE_COL_REF_PROD_PREPARATION_LIGNE, preparation_ligne.getRef_prod());
        contentValues.put(Constantes.CLE_COL_CONSO_PREVUE_PREPARATION_LIGNE, preparation_ligne.getConso_prévue());
        contentValues.put(Constantes.CLE_COL_CONSO_COND_PREPARATION_LIGNE, preparation_ligne.getConso_Cond());
        contentValues.put(Constantes.CLE_COL_CYCLE_PREPARATION_LIGNE, preparation_ligne.getCycle());
        contentValues.put(Constantes.CLE_COL_STATUT_PREPARATION_LIGNE, preparation_ligne.getStatut());
        contentValues.put(Constantes.CLE_COL_RAD_PREPARATION_LIGNE, preparation_ligne.getRAD());
        contentValues.put(Constantes.CLE_COL_CODE_CYCLE_PREPARATION_LIGNE, preparation_ligne.getCode_Cycle());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_DIRECTE_PREPARATION_LIGNE, preparation_ligne.getLivraison_directe());
        contentValues.put(Constantes.CLE_COL_RAC_PREPARATION_LIGNE, preparation_ligne.getRAC());
        contentValues.put(Constantes.CLE_COL_CODE_DEPOT_PREPARATION_LIGNE, preparation_ligne.getCode_depot());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_PREPARATION_LIGNE, preparation_ligne.getCatégorie());
        contentValues.put(Constantes.CLE_COL_N_COMMANDE_PREPARATION_LIGNE, preparation_ligne.getN_Commande());
        contentValues.put(Constantes.CLE_COL_CODE_LIGNE_COM_PREPARATION_LIGNE, preparation_ligne.getCode_ligne_com());
        contentValues.put(Constantes.CLE_COL_STOCK_ACTUEL_PREPARATION_LIGNE, preparation_ligne.getStock_Actuel());
        contentValues.put(Constantes.CLE_COL_STOCK_FINAL_PREPARATION_LIGNE, preparation_ligne.getStock_Final());
        contentValues.put(Constantes.CLE_COL_PRIORITAIRE_PREPARATION_LIGNE, preparation_ligne.getPrioritaire());
        contentValues.put(Constantes.CLE_COL_STOCK_SECURITE_PREPARATION_LIGNE, preparation_ligne.getStock_sécurité());
        contentValues.put(Constantes.CLE_COL_DATE_COMMANDE_PREPARATION_LIGNE, preparation_ligne.getDate_commande());
        contentValues.put(Constantes.CLE_COL_DATE_LIVRAISON_PREPARATION_LIGNE, preparation_ligne.getDate_livraison());
        contentValues.put(Constantes.CLE_COL_DATE_DELIVRANCE_PREPARATION_LIGNE, preparation_ligne.getDate_Délivrance());
        contentValues.put(Constantes.CLE_COL_COND_ACHAT_PREPARATION_LIGNE, preparation_ligne.getCond_Achat());
        contentValues.put(Constantes.CLE_COL_COND_DISTRIBUTION_PREPARATION_LIGNE, preparation_ligne.getCond_Distribution());
        contentValues.put(Constantes.CLE_COL_RESPECT_COND_ACHAT_PREPARATION_LIGNE, preparation_ligne.getRespect_cond_achat());
        contentValues.put(Constantes.CLE_COL_STOCK_IDEAL_PREPARATION_LIGNE, preparation_ligne.getStock_Idéal());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PREPARATION_LIGNE, preparation_ligne.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PREPARATION_LIGNE, preparation_ligne.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PREPARATION_LIGNE, preparation_ligne.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_PATIENT_PREPARATION_LIGNE, preparation_ligne.getPatient());
        contentValues.put(Constantes.CLE_COL_REF_ANTENNE_PREPARATION_LIGNE, preparation_ligne.getRéf_Antenne());
        contentValues.put(Constantes.CLE_COL_ID_PREVISION_PREPARATION_LIGNE, preparation_ligne.getID_Prevision());
        contentValues.put(Constantes.CLE_COL_PXRIX_UNIT_PREPARATION_LIGNE, preparation_ligne.getPxrix_Unit());
        contentValues.put(Constantes.CLE_COL_MT_HT_PREPARATION_LIGNE, preparation_ligne.getMt_HT());
        contentValues.put(Constantes.CLE_COL_TX_TVA_PREPARATION_LIGNE, preparation_ligne.getTx_TVA());
        contentValues.put(Constantes.CLE_COL_MT_TTC_PREPARATION_LIGNE, preparation_ligne.getMt_TTC());
        contentValues.put(Constantes.CLE_COL_DOTATION_PROTOCOLE_PREPARATION_LIGNE, preparation_ligne.getDotation_Protocole());
        contentValues.put(Constantes.CLE_COL_RESTE_A_CONSOMME_PREPARATION_LIGNE, preparation_ligne.getReste_A_Consomme());
        contentValues.put(Constantes.CLE_COL_QTE_BESOIN_PREPARATION_LIGNE, preparation_ligne.getQte_Besoin());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTION_PREPARATION_LIGNE, preparation_ligne.getPrescription());
        contentValues.put(Constantes.CLE_COL_NB_PATIENT_PREPARATION_LIGNE, preparation_ligne.getNb_Patient());
        contentValues.put(Constantes.CLE_COL_QTE_COMMANDELIV1_PREPARATION_LIGNE, preparation_ligne.getQte_CommandeLiv1());
        contentValues.put(Constantes.CLE_COL_QTE_COMMANDELIV2_PREPARATION_LIGNE, preparation_ligne.getQte_CommandeLiv2());
        contentValues.put(Constantes.CLE_COL_RELIQUAT_PREPARATION_LIGNE, preparation_ligne.getReliquat());
        contentValues.put(Constantes.CLE_COL_QTE_COMMANDELIV3_PREPARATION_LIGNE, preparation_ligne.getQte_CommandeLiv3());
        contentValues.put(Constantes.CLE_COL_QTE_COMMANDELIV4_PREPARATION_LIGNE, preparation_ligne.getQte_CommandeLiv4());
        contentValues.put(Constantes.CLE_COL_QTE_COMMANDELIV5_PREPARATION_LIGNE, preparation_ligne.getQte_CommandeLiv5());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_PREPARATION_LIGNE, null, contentValues);

        preparation_ligne.setPhiMR4UUID((int) rowId);

        return rowId;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PREPARATION_LIGNE = "Preparation_ligne";

        public static final String CLE_COL_DU_PREPARATION_LIGNE = "Du";
        public static final int NUM_COL_DU_PREPARATION_LIGNE = 1;
        public static final String TYPE_COL_DU_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_AU_PREPARATION_LIGNE = "Au";
        public static final int NUM_COL_AU_PREPARATION_LIGNE = 2;
        public static final String TYPE_COL_AU_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_REF_DEPOT_PREPARATION_LIGNE = "Réf_depot";
        public static final int NUM_COL_REF_DEPOT_PREPARATION_LIGNE = 3;
        public static final String TYPE_COL_REF_DEPOT_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_CODE_IPP_PREPARATION_LIGNE = "Code_IPP";
        public static final int NUM_COL_CODE_IPP_PREPARATION_LIGNE = 4;
        public static final String TYPE_COL_CODE_IPP_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_NOM_PREPARATION_LIGNE = "Nom";
        public static final int NUM_COL_NOM_PREPARATION_LIGNE = 5;
        public static final String TYPE_COL_NOM_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_CODE_PROD_PREPARATION_LIGNE = "Code_prod";
        public static final int NUM_COL_CODE_PROD_PREPARATION_LIGNE = 6;
        public static final String TYPE_COL_CODE_PROD_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_CODE_FRS_PREPARATION_LIGNE = "Code_frs";
        public static final int NUM_COL_CODE_FRS_PREPARATION_LIGNE = 7;
        public static final String TYPE_COL_CODE_FRS_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_FRS_PREPARATION_LIGNE = "Frs";
        public static final int NUM_COL_FRS_PREPARATION_LIGNE = 8;
        public static final String TYPE_COL_FRS_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_PRODUIT_PREPARATION_LIGNE = "Produit";
        public static final int NUM_COL_PRODUIT_PREPARATION_LIGNE = 9;
        public static final String TYPE_COL_PRODUIT_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_REF_PROD_PREPARATION_LIGNE = "Ref_prod";
        public static final int NUM_COL_REF_PROD_PREPARATION_LIGNE = 10;
        public static final String TYPE_COL_REF_PROD_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_CONSO_PREVUE_PREPARATION_LIGNE = "Conso_prévue";
        public static final int NUM_COL_CONSO_PREVUE_PREPARATION_LIGNE = 11;
        public static final String TYPE_COL_CONSO_PREVUE_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_CONSO_COND_PREPARATION_LIGNE = "Conso_Cond";
        public static final int NUM_COL_CONSO_COND_PREPARATION_LIGNE = 12;
        public static final String TYPE_COL_CONSO_COND_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_CYCLE_PREPARATION_LIGNE = "Cycle";
        public static final int NUM_COL_CYCLE_PREPARATION_LIGNE = 13;
        public static final String TYPE_COL_CYCLE_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_STATUT_PREPARATION_LIGNE = "Statut";
        public static final int NUM_COL_STATUT_PREPARATION_LIGNE = 14;
        public static final String TYPE_COL_STATUT_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_RAD_PREPARATION_LIGNE = "RAD";
        public static final int NUM_COL_RAD_PREPARATION_LIGNE = 15;
        public static final String TYPE_COL_RAD_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_CODE_CYCLE_PREPARATION_LIGNE = "Code_Cycle";
        public static final int NUM_COL_CODE_CYCLE_PREPARATION_LIGNE = 16;
        public static final String TYPE_COL_CODE_CYCLE_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_LIVRAISON_DIRECTE_PREPARATION_LIGNE = "Livraison_directe";
        public static final int NUM_COL_LIVRAISON_DIRECTE_PREPARATION_LIGNE = 17;
        public static final String TYPE_COL_LIVRAISON_DIRECTE_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_RAC_PREPARATION_LIGNE = "RAC";
        public static final int NUM_COL_RAC_PREPARATION_LIGNE = 18;
        public static final String TYPE_COL_RAC_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_CODE_DEPOT_PREPARATION_LIGNE = "Code_depot";
        public static final int NUM_COL_CODE_DEPOT_PREPARATION_LIGNE = 19;
        public static final String TYPE_COL_CODE_DEPOT_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_CATEGORIE_PREPARATION_LIGNE = "Catégorie";
        public static final int NUM_COL_CATEGORIE_PREPARATION_LIGNE = 20;
        public static final String TYPE_COL_CATEGORIE_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_N_COMMANDE_PREPARATION_LIGNE = "N_Commande";
        public static final int NUM_COL_N_COMMANDE_PREPARATION_LIGNE = 21;
        public static final String TYPE_COL_N_COMMANDE_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_CODE_LIGNE_COM_PREPARATION_LIGNE = "Code_ligne_com";
        public static final int NUM_COL_CODE_LIGNE_COM_PREPARATION_LIGNE = 22;
        public static final String TYPE_COL_CODE_LIGNE_COM_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_STOCK_ACTUEL_PREPARATION_LIGNE = "Stock_Actuel";
        public static final int NUM_COL_STOCK_ACTUEL_PREPARATION_LIGNE = 23;
        public static final String TYPE_COL_STOCK_ACTUEL_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_STOCK_FINAL_PREPARATION_LIGNE = "Stock_Final";
        public static final int NUM_COL_STOCK_FINAL_PREPARATION_LIGNE = 24;
        public static final String TYPE_COL_STOCK_FINAL_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_PRIORITAIRE_PREPARATION_LIGNE = "Prioritaire";
        public static final int NUM_COL_PRIORITAIRE_PREPARATION_LIGNE = 25;
        public static final String TYPE_COL_PRIORITAIRE_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_STOCK_SECURITE_PREPARATION_LIGNE = "Stock_sécurité";
        public static final int NUM_COL_STOCK_SECURITE_PREPARATION_LIGNE = 26;
        public static final String TYPE_COL_STOCK_SECURITE_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_DATE_COMMANDE_PREPARATION_LIGNE = "Date_commande";
        public static final int NUM_COL_DATE_COMMANDE_PREPARATION_LIGNE = 27;
        public static final String TYPE_COL_DATE_COMMANDE_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_DATE_LIVRAISON_PREPARATION_LIGNE = "Date_livraison";
        public static final int NUM_COL_DATE_LIVRAISON_PREPARATION_LIGNE = 28;
        public static final String TYPE_COL_DATE_LIVRAISON_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_DATE_DELIVRANCE_PREPARATION_LIGNE = "Date_Délivrance";
        public static final int NUM_COL_DATE_DELIVRANCE_PREPARATION_LIGNE = 29;
        public static final String TYPE_COL_DATE_DELIVRANCE_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_COND_ACHAT_PREPARATION_LIGNE = "Cond_Achat";
        public static final int NUM_COL_COND_ACHAT_PREPARATION_LIGNE = 30;
        public static final String TYPE_COL_COND_ACHAT_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_COND_DISTRIBUTION_PREPARATION_LIGNE = "Cond_Distribution";
        public static final int NUM_COL_COND_DISTRIBUTION_PREPARATION_LIGNE = 31;
        public static final String TYPE_COL_COND_DISTRIBUTION_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_RESPECT_COND_ACHAT_PREPARATION_LIGNE = "Respect_cond_achat";
        public static final int NUM_COL_RESPECT_COND_ACHAT_PREPARATION_LIGNE = 32;
        public static final String TYPE_COL_RESPECT_COND_ACHAT_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_STOCK_IDEAL_PREPARATION_LIGNE = "Stock_Idéal";
        public static final int NUM_COL_STOCK_IDEAL_PREPARATION_LIGNE = 33;
        public static final String TYPE_COL_STOCK_IDEAL_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_SYS_DT_MAJ_PREPARATION_LIGNE = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PREPARATION_LIGNE = 34;
        public static final String TYPE_COL_SYS_DT_MAJ_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PREPARATION_LIGNE = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PREPARATION_LIGNE = 35;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PREPARATION_LIGNE = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PREPARATION_LIGNE = 36;
        public static final String TYPE_COL_SYS_USER_MAJ_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_PATIENT_PREPARATION_LIGNE = "Patient";
        public static final int NUM_COL_PATIENT_PREPARATION_LIGNE = 37;
        public static final String TYPE_COL_PATIENT_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_REF_ANTENNE_PREPARATION_LIGNE = "Réf_Antenne";
        public static final int NUM_COL_REF_ANTENNE_PREPARATION_LIGNE = 38;
        public static final String TYPE_COL_REF_ANTENNE_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_ID_PREVISION_PREPARATION_LIGNE = "ID_Prevision";
        public static final int NUM_COL_ID_PREVISION_PREPARATION_LIGNE = 39;
        public static final String TYPE_COL_ID_PREVISION_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_PXRIX_UNIT_PREPARATION_LIGNE = "Pxrix_Unit";
        public static final int NUM_COL_PXRIX_UNIT_PREPARATION_LIGNE = 40;
        public static final String TYPE_COL_PXRIX_UNIT_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_MT_HT_PREPARATION_LIGNE = "Mt_HT";
        public static final int NUM_COL_MT_HT_PREPARATION_LIGNE = 41;
        public static final String TYPE_COL_MT_HT_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_TX_TVA_PREPARATION_LIGNE = "Tx_TVA";
        public static final int NUM_COL_TX_TVA_PREPARATION_LIGNE = 42;
        public static final String TYPE_COL_TX_TVA_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_MT_TTC_PREPARATION_LIGNE = "Mt_TTC";
        public static final int NUM_COL_MT_TTC_PREPARATION_LIGNE = 43;
        public static final String TYPE_COL_MT_TTC_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_DOTATION_PROTOCOLE_PREPARATION_LIGNE = "Dotation_Protocole";
        public static final int NUM_COL_DOTATION_PROTOCOLE_PREPARATION_LIGNE = 44;
        public static final String TYPE_COL_DOTATION_PROTOCOLE_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_RESTE_A_CONSOMME_PREPARATION_LIGNE = "Reste_A_Consomme";
        public static final int NUM_COL_RESTE_A_CONSOMME_PREPARATION_LIGNE = 45;
        public static final String TYPE_COL_RESTE_A_CONSOMME_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_QTE_BESOIN_PREPARATION_LIGNE = "Qte_Besoin";
        public static final int NUM_COL_QTE_BESOIN_PREPARATION_LIGNE = 46;
        public static final String TYPE_COL_QTE_BESOIN_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_PRESCRIPTION_PREPARATION_LIGNE = "Prescription";
        public static final int NUM_COL_PRESCRIPTION_PREPARATION_LIGNE = 47;
        public static final String TYPE_COL_PRESCRIPTION_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_NB_PATIENT_PREPARATION_LIGNE = "Nb_Patient";
        public static final int NUM_COL_NB_PATIENT_PREPARATION_LIGNE = 48;
        public static final String TYPE_COL_NB_PATIENT_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_QTE_COMMANDELIV1_PREPARATION_LIGNE = "Qte_CommandeLiv1";
        public static final int NUM_COL_QTE_COMMANDELIV1_PREPARATION_LIGNE = 49;
        public static final String TYPE_COL_QTE_COMMANDELIV1_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_QTE_COMMANDELIV2_PREPARATION_LIGNE = "Qte_CommandeLiv2";
        public static final int NUM_COL_QTE_COMMANDELIV2_PREPARATION_LIGNE = 50;
        public static final String TYPE_COL_QTE_COMMANDELIV2_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_RELIQUAT_PREPARATION_LIGNE = "Reliquat";
        public static final int NUM_COL_RELIQUAT_PREPARATION_LIGNE = 51;
        public static final String TYPE_COL_RELIQUAT_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_QTE_COMMANDELIV3_PREPARATION_LIGNE = "Qte_CommandeLiv3";
        public static final int NUM_COL_QTE_COMMANDELIV3_PREPARATION_LIGNE = 52;
        public static final String TYPE_COL_QTE_COMMANDELIV3_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_QTE_COMMANDELIV4_PREPARATION_LIGNE = "Qte_CommandeLiv4";
        public static final int NUM_COL_QTE_COMMANDELIV4_PREPARATION_LIGNE = 53;
        public static final String TYPE_COL_QTE_COMMANDELIV4_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_QTE_COMMANDELIV5_PREPARATION_LIGNE = "Qte_CommandeLiv5";
        public static final int NUM_COL_QTE_COMMANDELIV5_PREPARATION_LIGNE = 54;
        public static final String TYPE_COL_QTE_COMMANDELIV5_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL__UID_PREPARATION_LIGNE = "_UID";
        public static final int NUM_COL__UID_PREPARATION_LIGNE = 55;
        public static final String TYPE_COL__UID_PREPARATION_LIGNE = "INTEGER";


        public static final String CREATION_TABLE_PREPARATION_LIGNE = "CREATE TABLE " + Constantes.TABLE_PREPARATION_LIGNE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_DU_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_DU_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_AU_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_AU_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_REF_DEPOT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_REF_DEPOT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_IPP_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_CODE_IPP_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_NOM_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_NOM_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_PROD_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_CODE_PROD_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_FRS_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_CODE_FRS_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_FRS_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_FRS_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUIT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRODUIT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_REF_PROD_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_REF_PROD_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_CONSO_PREVUE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_CONSO_PREVUE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_CONSO_COND_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_CONSO_COND_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_CYCLE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_CYCLE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_STATUT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_STATUT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_RAD_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_RAD_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_CYCLE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_CODE_CYCLE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_LIVRAISON_DIRECTE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_LIVRAISON_DIRECTE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_RAC_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_RAC_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_DEPOT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_CODE_DEPOT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_CATEGORIE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_CATEGORIE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_N_COMMANDE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_N_COMMANDE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_CODE_LIGNE_COM_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_CODE_LIGNE_COM_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_STOCK_ACTUEL_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_STOCK_ACTUEL_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_STOCK_FINAL_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_STOCK_FINAL_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRIORITAIRE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRIORITAIRE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_STOCK_SECURITE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_STOCK_SECURITE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_DATE_COMMANDE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_DATE_COMMANDE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_DATE_LIVRAISON_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_DATE_LIVRAISON_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_DATE_DELIVRANCE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_DATE_DELIVRANCE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_COND_ACHAT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_COND_ACHAT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_COND_DISTRIBUTION_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_COND_DISTRIBUTION_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_RESPECT_COND_ACHAT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_RESPECT_COND_ACHAT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_STOCK_IDEAL_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_STOCK_IDEAL_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_SYS_DT_MAJ_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_SYS_USER_MAJ_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PATIENT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PATIENT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_REF_ANTENNE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_REF_ANTENNE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_ID_PREVISION_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_ID_PREVISION_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PXRIX_UNIT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PXRIX_UNIT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_MT_HT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_MT_HT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_TX_TVA_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_TX_TVA_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_MT_TTC_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_MT_TTC_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_DOTATION_PROTOCOLE_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_DOTATION_PROTOCOLE_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_RESTE_A_CONSOMME_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_RESTE_A_CONSOMME_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_BESOIN_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_BESOIN_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRESCRIPTION_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRESCRIPTION_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_NB_PATIENT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_NB_PATIENT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_COMMANDELIV1_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_COMMANDELIV1_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_COMMANDELIV2_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_COMMANDELIV2_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_RELIQUAT_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_RELIQUAT_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_COMMANDELIV3_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_COMMANDELIV3_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_COMMANDELIV4_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_COMMANDELIV4_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_COMMANDELIV5_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_COMMANDELIV5_PREPARATION_LIGNE + ","
                + Constantes.CLE_COL__UID_PREPARATION_LIGNE + " " + Constantes.TYPE_COL__UID_PREPARATION_LIGNE
                + ");";
    }
}
