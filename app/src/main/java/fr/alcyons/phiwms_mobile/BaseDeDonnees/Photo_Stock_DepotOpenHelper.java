package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by quentinlanusse on 28/06/2017.
 */

public class Photo_Stock_DepotOpenHelper extends DBOpenHelper {

    public Photo_Stock_DepotOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static class Constantes implements BaseColumns {

        public static final String TABLE_PHOTO_STOCK_DEPOT = "Photo_stock_depot";

        public static final String CLE_COL_REF_DEPOT_PHOTO_STOCK_DEPOT = "Ref_Depot";
        public static final int NUM_COL_REF_DEPOT_PHOTO_STOCK_DEPOT = 1;
        public static final String TYPE_COL_REF_DEPOT_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_ANNEE_PHOTO_STOCK_DEPOT = "Annee";
        public static final int NUM_COL_ANNEE_PHOTO_STOCK_DEPOT = 2;
        public static final String TYPE_COL_ANNEE_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_MOIS_PHOTO_STOCK_DEPOT = "Mois";
        public static final int NUM_COL_MOIS_PHOTO_STOCK_DEPOT = 3;
        public static final String TYPE_COL_MOIS_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_STOCK_DEBUT_PHOTO_STOCK_DEPOT = "STOCK_DEBUT";
        public static final int NUM_COL_STOCK_DEBUT_PHOTO_STOCK_DEPOT = 4;
        public static final String TYPE_COL_STOCK_DEBUT_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_ENTREE_CDE_PHOTO_STOCK_DEPOT = "QTE_ENTREE_CDE";
        public static final int NUM_COL_QTE_ENTREE_CDE_PHOTO_STOCK_DEPOT = 5;
        public static final String TYPE_COL_QTE_ENTREE_CDE_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_DEPOT = "QTE_SORTIE_DELIV";
        public static final int NUM_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_DEPOT = 6;
        public static final String TYPE_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_DEPOT = "QTE_ENTREE_DELIV";
        public static final int NUM_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_DEPOT = 7;
        public static final String TYPE_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_DEPOT = "QTE_SORTIE_RETOUR";
        public static final int NUM_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_DEPOT = 8;
        public static final String TYPE_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_DEPOT = "QTE_ENTREE_RETOUR";
        public static final int NUM_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_DEPOT = 9;
        public static final String TYPE_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_DEPOT = "QTE_SORTIE_REGUL";
        public static final int NUM_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_DEPOT = 10;
        public static final String TYPE_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_DEPOT = "QTE_ENTREE_REGUL";
        public static final int NUM_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_DEPOT = 11;
        public static final String TYPE_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_SORTIE_ECART_PHOTO_STOCK_DEPOT = "QTE_SORTIE_ECART";
        public static final int NUM_COL_QTE_SORTIE_ECART_PHOTO_STOCK_DEPOT = 12;
        public static final String TYPE_COL_QTE_SORTIE_ECART_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_ENTREE_ECART_PHOTO_STOCK_DEPOT = "QTE_ENTREE_ECART";
        public static final int NUM_COL_QTE_ENTREE_ECART_PHOTO_STOCK_DEPOT = 13;
        public static final String TYPE_COL_QTE_ENTREE_ECART_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_STOCK_FIN_PHOTO_STOCK_DEPOT = "STOCK_FIN";
        public static final int NUM_COL_STOCK_FIN_PHOTO_STOCK_DEPOT = 14;
        public static final String TYPE_COL_STOCK_FIN_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_DEPOT = "VALEUR_STOCK_FIN";
        public static final int NUM_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_DEPOT = 15;
        public static final String TYPE_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_DEPOT = "REAL";
        public static final String CLE_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_DEPOT = "VALEUR_STOCK_DEBUT";
        public static final int NUM_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_DEPOT = 16;
        public static final String TYPE_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_DEPOT = "REAL";
        public static final String CLE_COL_CATEGORIE_PHOTO_STOCK_DEPOT = "CATEGORIE";
        public static final int NUM_COL_CATEGORIE_PHOTO_STOCK_DEPOT = 17;
        public static final String TYPE_COL_CATEGORIE_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_REFERENCE_PHOTO_STOCK_DEPOT = "REFERENCE";
        public static final int NUM_COL_REFERENCE_PHOTO_STOCK_DEPOT = 18;
        public static final String TYPE_COL_REFERENCE_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_DESIGNATION_PHOTO_STOCK_DEPOT = "DESIGNATION";
        public static final int NUM_COL_DESIGNATION_PHOTO_STOCK_DEPOT = 19;
        public static final String TYPE_COL_DESIGNATION_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_PHOTO_STOCK_DEPOT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PHOTO_STOCK_DEPOT = 20;
        public static final String TYPE_COL_SYS_DT_MAJ_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PHOTO_STOCK_DEPOT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PHOTO_STOCK_DEPOT = 21;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PHOTO_STOCK_DEPOT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PHOTO_STOCK_DEPOT = 22;
        public static final String TYPE_COL_SYS_USER_MAJ_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_PRIX_UNIT_PHOTO_STOCK_DEPOT = "Prix_Unit";
        public static final int NUM_COL_PRIX_UNIT_PHOTO_STOCK_DEPOT = 23;
        public static final String TYPE_COL_PRIX_UNIT_PHOTO_STOCK_DEPOT = "REAL";
        public static final String CLE_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_DEPOT = "Consommation_mensuelle";
        public static final int NUM_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_DEPOT = 24;
        public static final String TYPE_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_ENTREE_PHOTO_STOCK_DEPOT = "Qte_Entree";
        public static final int NUM_COL_QTE_ENTREE_PHOTO_STOCK_DEPOT = 25;
        public static final String TYPE_COL_QTE_ENTREE_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_SORTIE_PHOTO_STOCK_DEPOT = "Qte_sortie";
        public static final int NUM_COL_QTE_SORTIE_PHOTO_STOCK_DEPOT = 26;
        public static final String TYPE_COL_QTE_SORTIE_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_REF_CYCLE_PHOTO_STOCK_DEPOT = "Ref_Cycle";
        public static final int NUM_COL_REF_CYCLE_PHOTO_STOCK_DEPOT = 27;
        public static final String TYPE_COL_REF_CYCLE_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_DEPOT = "Consommation_Trimestrielle";
        public static final int NUM_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_DEPOT = 28;
        public static final String TYPE_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_LIVRAISON_DIRECTE_PHOTO_STOCK_DEPOT = "Livraison_Directe";
        public static final int NUM_COL_LIVRAISON_DIRECTE_PHOTO_STOCK_DEPOT = 29;
        public static final String TYPE_COL_LIVRAISON_DIRECTE_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_ID_INV_PHOTO_STOCK_DEPOT = "ID_Inv";
        public static final int NUM_COL_ID_INV_PHOTO_STOCK_DEPOT = 30;
        public static final String TYPE_COL_ID_INV_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_CONSO_JOURNALIERE_PHOTO_STOCK_DEPOT = "Conso_Journaliere";
        public static final int NUM_COL_CONSO_JOURNALIERE_PHOTO_STOCK_DEPOT = 31;
        public static final String TYPE_COL_CONSO_JOURNALIERE_PHOTO_STOCK_DEPOT = "REAL";
        public static final String CLE_COL_CONSO_Q1_PHOTO_STOCK_DEPOT = "Conso_Q1";
        public static final int NUM_COL_CONSO_Q1_PHOTO_STOCK_DEPOT = 32;
        public static final String TYPE_COL_CONSO_Q1_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_CONSO_Q2_PHOTO_STOCK_DEPOT = "Conso_Q2";
        public static final int NUM_COL_CONSO_Q2_PHOTO_STOCK_DEPOT = 33;
        public static final String TYPE_COL_CONSO_Q2_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_CONSO_Q3_PHOTO_STOCK_DEPOT = "Conso_Q3";
        public static final int NUM_COL_CONSO_Q3_PHOTO_STOCK_DEPOT = 34;
        public static final String TYPE_COL_CONSO_Q3_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_CONSO_Q4_PHOTO_STOCK_DEPOT = "Conso_Q4";
        public static final int NUM_COL_CONSO_Q4_PHOTO_STOCK_DEPOT = 35;
        public static final String TYPE_COL_CONSO_Q4_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_CONSO_S1_PHOTO_STOCK_DEPOT = "Conso_S1";
        public static final int NUM_COL_CONSO_S1_PHOTO_STOCK_DEPOT = 36;
        public static final String TYPE_COL_CONSO_S1_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_CONSO_S2_PHOTO_STOCK_DEPOT = "Conso_S2";
        public static final int NUM_COL_CONSO_S2_PHOTO_STOCK_DEPOT = 37;
        public static final String TYPE_COL_CONSO_S2_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_CONSO_S3_PHOTO_STOCK_DEPOT = "Conso_S3";
        public static final int NUM_COL_CONSO_S3_PHOTO_STOCK_DEPOT = 38;
        public static final String TYPE_COL_CONSO_S3_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_CONSO_S4_PHOTO_STOCK_DEPOT = "Conso_S4";
        public static final int NUM_COL_CONSO_S4_PHOTO_STOCK_DEPOT = 39;
        public static final String TYPE_COL_CONSO_S4_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_CONSO_S5_PHOTO_STOCK_DEPOT = "Conso_S5";
        public static final int NUM_COL_CONSO_S5_PHOTO_STOCK_DEPOT = 40;
        public static final String TYPE_COL_CONSO_S5_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_PMP_TTC_FIN_MOIS_PHOTO_STOCK_DEPOT = "PMP_TTC_Fin_mois";
        public static final int NUM_COL_PMP_TTC_FIN_MOIS_PHOTO_STOCK_DEPOT = 41;
        public static final String TYPE_COL_PMP_TTC_FIN_MOIS_PHOTO_STOCK_DEPOT = "REAL";
        public static final String CLE_COL_QTE_ENTREE_FACTUREE_PHOTO_STOCK_DEPOT = "Qte_Entree_Facturee";
        public static final int NUM_COL_QTE_ENTREE_FACTUREE_PHOTO_STOCK_DEPOT = 42;
        public static final String TYPE_COL_QTE_ENTREE_FACTUREE_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_PMP_TTC_DEBUT_MOIS_PHOTO_STOCK_DEPOT = "PMP_TTC_Debut_mois";
        public static final int NUM_COL_PMP_TTC_DEBUT_MOIS_PHOTO_STOCK_DEPOT = 43;
        public static final String TYPE_COL_PMP_TTC_DEBUT_MOIS_PHOTO_STOCK_DEPOT = "REAL";
        public static final String CLE_COL_VALEUR_ACHATS_HT_PHOTO_STOCK_DEPOT = "Valeur_Achats_HT";
        public static final int NUM_COL_VALEUR_ACHATS_HT_PHOTO_STOCK_DEPOT = 44;
        public static final String TYPE_COL_VALEUR_ACHATS_HT_PHOTO_STOCK_DEPOT = "REAL";
        public static final String CLE_COL_VALEUR_ACHATS_TTC_PHOTO_STOCK_DEPOT = "Valeur_Achats_TTC";
        public static final int NUM_COL_VALEUR_ACHATS_TTC_PHOTO_STOCK_DEPOT = 45;
        public static final String TYPE_COL_VALEUR_ACHATS_TTC_PHOTO_STOCK_DEPOT = "REAL";
        public static final String CLE_COL_REF_CYCLE_DEPOT_PDT_PHOTO_STOCK_DEPOT = "Ref_Cycle_Depot_Pdt";
        public static final int NUM_COL_REF_CYCLE_DEPOT_PDT_PHOTO_STOCK_DEPOT = 46;
        public static final String TYPE_COL_REF_CYCLE_DEPOT_PDT_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_REF_DEPOT_PDT_PHOTO_STOCK_DEPOT = "Ref_Depot_Pdt";
        public static final int NUM_COL_REF_DEPOT_PDT_PHOTO_STOCK_DEPOT = 47;
        public static final String TYPE_COL_REF_DEPOT_PDT_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_TVA_PHOTO_STOCK_DEPOT = "TVA";
        public static final int NUM_COL_TVA_PHOTO_STOCK_DEPOT = 48;
        public static final String TYPE_COL_TVA_PHOTO_STOCK_DEPOT = "REAL";
        public static final String CLE_COL_FOURNISSEUR_PHOTO_STOCK_DEPOT = "Fournisseur";
        public static final int NUM_COL_FOURNISSEUR_PHOTO_STOCK_DEPOT = 49;
        public static final String TYPE_COL_FOURNISSEUR_PHOTO_STOCK_DEPOT = "TEXT";
        public static final String CLE_COL_CLASSE_ID_PHOTO_STOCK_DEPOT = "classe_id";
        public static final int NUM_COL_CLASSE_ID_PHOTO_STOCK_DEPOT = 50;
        public static final String TYPE_COL_CLASSE_ID_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_ADMINISTRER_PHOTO_STOCK_DEPOT = "QTE_Administrer";
        public static final int NUM_COL_QTE_ADMINISTRER_PHOTO_STOCK_DEPOT = 51;
        public static final String TYPE_COL_QTE_ADMINISTRER_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_SEANCE_NB_PHOTO_STOCK_DEPOT = "Seance_NB";
        public static final int NUM_COL_SEANCE_NB_PHOTO_STOCK_DEPOT = 52;
        public static final String TYPE_COL_SEANCE_NB_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_RETOUR_PUI_PHOTO_STOCK_DEPOT = "QTE_Retour_PUI";
        public static final int NUM_COL_QTE_RETOUR_PUI_PHOTO_STOCK_DEPOT = 53;
        public static final String TYPE_COL_QTE_RETOUR_PUI_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_RETOUR_FRS_PHOTO_STOCK_DEPOT = "QTE_Retour_FRS";
        public static final int NUM_COL_QTE_RETOUR_FRS_PHOTO_STOCK_DEPOT = 54;
        public static final String TYPE_COL_QTE_RETOUR_FRS_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_DESTRUCTION_PHOTO_STOCK_DEPOT = "QTE_Destruction";
        public static final int NUM_COL_QTE_DESTRUCTION_PHOTO_STOCK_DEPOT = 55;
        public static final String TYPE_COL_QTE_DESTRUCTION_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_ORDONNER_PHOTO_STOCK_DEPOT = "QTE_Ordonner";
        public static final int NUM_COL_QTE_ORDONNER_PHOTO_STOCK_DEPOT = 56;
        public static final String TYPE_COL_QTE_ORDONNER_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_SEANCENB_PHOTO_STOCK_DEPOT = "QTE_SeanceNB";
        public static final int NUM_COL_QTE_SEANCENB_PHOTO_STOCK_DEPOT = 57;
        public static final String TYPE_COL_QTE_SEANCENB_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL__UID_PHOTO_STOCK_DEPOT = "_UID";
        public static final int NUM_COL__UID_PHOTO_STOCK_DEPOT = 58;
        public static final String TYPE_COL__UID_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_COMMANDE_PHOTO_STOCK_DEPOT = "QTE_COMMANDE";
        public static final int NUM_COL_QTE_COMMANDE_PHOTO_STOCK_DEPOT = 59;
        public static final String TYPE_COL_QTE_COMMANDE_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_QTE_RAF_PHOTO_STOCK_DEPOT = "QTE_RAF";
        public static final int NUM_COL_QTE_RAF_PHOTO_STOCK_DEPOT = 60;
        public static final String TYPE_COL_QTE_RAF_PHOTO_STOCK_DEPOT = "INTEGER";
        public static final String CLE_COL_CODE_PRODUIT_PHOTO_STOCK_DEPOT = "Code_Produit";
        public static final int NUM_COL_CODE_PRODUIT_PHOTO_STOCK_DEPOT = 61;
        public static final String TYPE_COL_CODE_PRODUIT_PHOTO_STOCK_DEPOT = "INTEGER";


        public static final String CREATION_TABLE_PHOTO_STOCK_DEPOT = "CREATE TABLE " + Constantes.TABLE_PHOTO_STOCK_DEPOT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_REF_DEPOT_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_REF_DEPOT_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_ANNEE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_ANNEE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_MOIS_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_MOIS_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_STOCK_DEBUT_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_STOCK_DEBUT_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_CDE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_ENTREE_CDE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_SORTIE_DELIV_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_ENTREE_DELIV_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_SORTIE_RETOUR_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_ENTREE_RETOUR_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_SORTIE_REGUL_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_ENTREE_REGUL_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_ECART_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_SORTIE_ECART_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_ECART_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_ENTREE_ECART_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_STOCK_FIN_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_STOCK_FIN_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_VALEUR_STOCK_FIN_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_VALEUR_STOCK_DEBUT_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CATEGORIE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CATEGORIE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_REFERENCE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_REFERENCE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_DESIGNATION_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_DESIGNATION_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_SYS_DT_MAJ_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_SYS_USER_MAJ_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_PRIX_UNIT_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_PRIX_UNIT_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSOMMATION_MENSUELLE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_ENTREE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_SORTIE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_SORTIE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_REF_CYCLE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_REF_CYCLE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSOMMATION_TRIMESTRIELLE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_LIVRAISON_DIRECTE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_DIRECTE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_ID_INV_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_ID_INV_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSO_JOURNALIERE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSO_JOURNALIERE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSO_Q1_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSO_Q1_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSO_Q2_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSO_Q2_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSO_Q3_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSO_Q3_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSO_Q4_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSO_Q4_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSO_S1_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSO_S1_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSO_S2_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSO_S2_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSO_S3_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSO_S3_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSO_S4_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSO_S4_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CONSO_S5_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CONSO_S5_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_PMP_TTC_FIN_MOIS_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_PMP_TTC_FIN_MOIS_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_ENTREE_FACTUREE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_ENTREE_FACTUREE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_PMP_TTC_DEBUT_MOIS_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_PMP_TTC_DEBUT_MOIS_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_VALEUR_ACHATS_HT_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_VALEUR_ACHATS_HT_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_VALEUR_ACHATS_TTC_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_VALEUR_ACHATS_TTC_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_REF_CYCLE_DEPOT_PDT_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_REF_CYCLE_DEPOT_PDT_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_REF_DEPOT_PDT_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_REF_DEPOT_PDT_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_TVA_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_TVA_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_FOURNISSEUR_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_FOURNISSEUR_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_CLASSE_ID_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CLASSE_ID_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_ADMINISTRER_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_ADMINISTRER_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_SEANCE_NB_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_SEANCE_NB_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_RETOUR_PUI_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_RETOUR_PUI_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_RETOUR_FRS_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_RETOUR_FRS_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_DESTRUCTION_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_DESTRUCTION_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_ORDONNER_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_ORDONNER_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_SEANCENB_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_SEANCENB_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL__UID_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL__UID_PHOTO_STOCK_DEPOT + ","
                + Constantes.CLE_COL_QTE_COMMANDE_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_COMMANDE_PHOTO_STOCK_DEPOT + " ,"
                + Constantes.CLE_COL_QTE_RAF_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_QTE_RAF_PHOTO_STOCK_DEPOT + ","
                + Constantes.CLE_COL_CODE_PRODUIT_PHOTO_STOCK_DEPOT + " " + Constantes.TYPE_COL_CODE_PRODUIT_PHOTO_STOCK_DEPOT
                + ");";
    }
}
