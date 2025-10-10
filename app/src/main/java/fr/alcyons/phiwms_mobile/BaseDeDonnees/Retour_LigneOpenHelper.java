package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;

public class Retour_LigneOpenHelper extends DBOpenHelper {

    public Retour_LigneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        int compteur_suppression = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR_LIGNE, null);

        while (cursor.moveToNext()) {
            Retour_Ligne retour_ligne = new Retour_Ligne(cursor);
            if (retour_ligne.getProduit_Designation().contentEquals("Traceur_Medicament_ALCYONS") || retour_ligne.getProduit_Designation().contentEquals("Traceur_Dispositif_ALCYONS")) {
                db.delete(Constantes.TABLE_RETOUR_LIGNE, Constantes.CLE_COL__UID_RETOUR_LIGNE + "=?", new String[]{String.valueOf(retour_ligne.get_UID())});
                compteur_suppression++;
            }
        }

        cursor.close();
        cursor = null;
        return (long) compteur_suppression;
    }

    public static long insererUnRetour_LigneEnBDD(SQLiteDatabase db, Retour_Ligne retour_ligne) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_RETOUR_UID_RETOUR_LIGNE, retour_ligne.getRetour_UID());
        contentValues.put(Constantes.CLE_COL_CODE_PRODUIT_RETOUR_LIGNE, retour_ligne.getCode_produit());
        contentValues.put(Constantes.CLE_COL_PRODUIT_REFERENCE_RETOUR_LIGNE, retour_ligne.getProduit_Reference());
        contentValues.put(Constantes.CLE_COL_QTE_RETOURNER_RETOUR_LIGNE, retour_ligne.getQte_Retourner());
        contentValues.put(Constantes.CLE_COL_PRODUIT_FOURNISSEUR_RETOUR_LIGNE, retour_ligne.getProduit_Fournisseur());
        contentValues.put(Constantes.CLE_COL_PRODUIT_PUHT_RETOUR_LIGNE, retour_ligne.getProduit_PUHT());
        contentValues.put(Constantes.CLE_COL_PRODUIT_TVA_RETOUR_LIGNE, retour_ligne.getProduit_TVA());
        contentValues.put(Constantes.CLE_COL_PRODUIT_DESIGNATION_RETOUR_LIGNE, retour_ligne.getProduit_Designation());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_RETOUR_LIGNE, retour_ligne.getMontant_TTC());
        contentValues.put(Constantes.CLE_COL_PIECE_CODE_RETOUR_LIGNE, retour_ligne.getPiece_Code());
        contentValues.put(Constantes.CLE_COL_DATE_VALIDATION_RETOUR_LIGNE, retour_ligne.getDate_validation());
        contentValues.put(Constantes.CLE_COL_DESTINATION_RETOUR_LIGNE, retour_ligne.getDestination());
        contentValues.put(Constantes.CLE_COL_DEVISE_RETOUR_LIGNE, retour_ligne.getDevise());
        contentValues.put(Constantes.CLE_COL_QTE_AVANT_RETOUR_RETOUR_LIGNE, retour_ligne.getQte_avant_retour());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_RETOUR_LIGNE, retour_ligne.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_RETOUR_LIGNE, retour_ligne.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_RETOUR_LIGNE, retour_ligne.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_LOT_RETOUR_LIGNE, retour_ligne.getLot());
        contentValues.put(Constantes.CLE_COL_PATIENTIDENTITE_RETOUR_LIGNE, retour_ligne.getPatientIdentite());
        contentValues.put(Constantes.CLE_COL__UID_RETOUR_LIGNE, retour_ligne.get_UID());
        contentValues.put(Constantes.CLE_COL_QTE_DEMANDER_RETOUR_LIGNE, retour_ligne.getQte_Demander());
        contentValues.put(Constantes.CLE_COL_LOT_RETOURNER_RETOUR_LIGNE, retour_ligne.getLot_Retourner());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_RETOUR_LIGNE, retour_ligne.getPeremptionDate());
        contentValues.put(Constantes.CLE_COL_DESTRUCTION_QTE_RETOUR_LIGNE, retour_ligne.getDestruction_Qte());
        contentValues.put(Constantes.CLE_COL_RETOURPUI_QTE_RETOUR_LIGNE, retour_ligne.getRetourPui_Qte());
        contentValues.put(Constantes.CLE_COL_RETOURFRS_QTE_RETOUR_LIGNE, retour_ligne.getRetourFrs_Qte());
        contentValues.put(Constantes.CLE_COL_QUARANTAINE_QTE_DEMANDER_RETOUR_LIGNE, retour_ligne.getQuarantaine_Qte_Demander());
        contentValues.put(Constantes.CLE_COL_RETOURPUI_EMPLACEMENT_RETOUR_LIGNE, retour_ligne.getRetourPUI_Emplacement());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENTORIGINE_RETOUR_LIGNE, retour_ligne.getEmplacementOrigine());
        contentValues.put(Constantes.CLE_COL_PATIENTID_RETOUR_LIGNE, retour_ligne.getPatientID());
        contentValues.put(Constantes.CLE_COL_RETOURPUI_ZONE_RETOUR_LIGNE, retour_ligne.getRetourPUI_Zone());
        contentValues.put(Constantes.CLE_COL_SERIE_RETOURNER, retour_ligne.getSerie_Retourner());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_RETOUR_LIGNE, null, contentValues);

        retour_ligne.setphiwms_mobileUUID((int) rowId);

        return rowId;
    }

    public static List<Retour_Ligne> getAllRetourLignesByRetour(SQLiteDatabase db, Retour retour) {
        List<Retour_Ligne> retourLigneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR_LIGNE + " WHERE " + Constantes.CLE_COL_RETOUR_UID_RETOUR_LIGNE + "=?", new String[]{String.valueOf(retour.get_UID())});

        while (cursor.moveToNext()) {
            Retour_Ligne retourLigne = new Retour_Ligne(cursor);
            retourLigneList.add(retourLigne);
        }

        cursor.close();
        cursor = null;
        return retourLigneList;
    }

    public static List<Retour_Ligne> getAllRetourLignesBaseByRetour(SQLiteDatabase db, Retour retour) {
        List<Retour_Ligne> retourLigneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR_LIGNE + " WHERE " + Constantes.CLE_COL_RETOUR_UID_RETOUR_LIGNE + "=? AND "+Constantes.CLE_COL__UID_RETOUR_LIGNE+" > 0", new String[]{String.valueOf(retour.get_UID())});

        while (cursor.moveToNext()) {
            Retour_Ligne retourLigne = new Retour_Ligne(cursor);
            retourLigneList.add(retourLigne);
        }

        cursor.close();
        cursor = null;
        return retourLigneList;
    }

    public static List<Retour_Ligne> getAllRetourLignesByRetourProduitNeg(SQLiteDatabase db, Retour retour, int idProduit) {
        List<Retour_Ligne> retourLigneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR_LIGNE + " WHERE  " + Constantes.CLE_COL_RETOUR_UID_RETOUR_LIGNE + "=? AND "+Constantes.CLE_COL_CODE_PRODUIT_RETOUR_LIGNE+"=? AND "+Constantes.CLE_COL__UID_RETOUR_LIGNE+"< 0", new String[]{String.valueOf(retour.get_UID()), String.valueOf(idProduit)});

        while (cursor.moveToNext()) {
            Retour_Ligne retourLigne = new Retour_Ligne(cursor);
            retourLigneList.add(retourLigne);
        }

        cursor.close();
        cursor = null;
        return retourLigneList;
    }

    public static Retour_Ligne getRetourLigneNegByProduitLot(SQLiteDatabase db, int retouruid, int idProduit, String lot, String serie) {
        Retour_Ligne retourLigne = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR_LIGNE + " WHERE  " + Constantes.CLE_COL_RETOUR_UID_RETOUR_LIGNE + "=? AND "+Constantes.CLE_COL_CODE_PRODUIT_RETOUR_LIGNE+"=? AND "+Constantes.CLE_COL_LOT_RETOURNER_RETOUR_LIGNE+"=? AND "+Constantes.CLE_COL_SERIE_RETOURNER+"=? AND "+Constantes.CLE_COL__UID_RETOUR_LIGNE+"< 0", new String[]{String.valueOf(retouruid), String.valueOf(idProduit), lot, serie});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            retourLigne = new Retour_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return retourLigne;
    }

    public static Retour_Ligne getRetourLigneByID(SQLiteDatabase db, int id) {
        Retour_Ligne retourLigne = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR_LIGNE + " WHERE  " + Constantes.CLE_COL__UID_RETOUR_LIGNE + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            retourLigne = new Retour_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return retourLigne;
    }

    public static Retour_Ligne getRetourLigneByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        Retour_Ligne retourLigne = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_RETOUR_LIGNE + " WHERE  " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            retourLigne = new Retour_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return retourLigne;
    }

    public static long mettreAJourUnRetourLigne(SQLiteDatabase db, Retour_Ligne retour_ligne) {

        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_RETOUR_UID_RETOUR_LIGNE, retour_ligne.getRetour_UID());
        contentValues.put(Constantes.CLE_COL_CODE_PRODUIT_RETOUR_LIGNE, retour_ligne.getCode_produit());
        contentValues.put(Constantes.CLE_COL_PRODUIT_REFERENCE_RETOUR_LIGNE, retour_ligne.getProduit_Reference());
        contentValues.put(Constantes.CLE_COL_QTE_RETOURNER_RETOUR_LIGNE, retour_ligne.getQte_Retourner());
        contentValues.put(Constantes.CLE_COL_PRODUIT_FOURNISSEUR_RETOUR_LIGNE, retour_ligne.getProduit_Fournisseur());
        contentValues.put(Constantes.CLE_COL_PRODUIT_PUHT_RETOUR_LIGNE, retour_ligne.getProduit_PUHT());
        contentValues.put(Constantes.CLE_COL_PRODUIT_TVA_RETOUR_LIGNE, retour_ligne.getProduit_TVA());
        contentValues.put(Constantes.CLE_COL_PRODUIT_DESIGNATION_RETOUR_LIGNE, retour_ligne.getProduit_Designation());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_RETOUR_LIGNE, retour_ligne.getMontant_TTC());
        contentValues.put(Constantes.CLE_COL_PIECE_CODE_RETOUR_LIGNE, retour_ligne.getPiece_Code());
        contentValues.put(Constantes.CLE_COL_DATE_VALIDATION_RETOUR_LIGNE, retour_ligne.getDate_validation());
        contentValues.put(Constantes.CLE_COL_DESTINATION_RETOUR_LIGNE, retour_ligne.getDestination());
        contentValues.put(Constantes.CLE_COL_DEVISE_RETOUR_LIGNE, retour_ligne.getDevise());
        contentValues.put(Constantes.CLE_COL_QTE_AVANT_RETOUR_RETOUR_LIGNE, retour_ligne.getQte_avant_retour());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_RETOUR_LIGNE, retour_ligne.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_RETOUR_LIGNE, retour_ligne.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_RETOUR_LIGNE, retour_ligne.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_LOT_RETOUR_LIGNE, retour_ligne.getLot());
        contentValues.put(Constantes.CLE_COL_PATIENTIDENTITE_RETOUR_LIGNE, retour_ligne.getPatientIdentite());
        contentValues.put(Constantes.CLE_COL__UID_RETOUR_LIGNE, retour_ligne.get_UID());
        contentValues.put(Constantes.CLE_COL_QTE_DEMANDER_RETOUR_LIGNE, retour_ligne.getQte_Demander());
        contentValues.put(Constantes.CLE_COL_LOT_RETOURNER_RETOUR_LIGNE, retour_ligne.getLot_Retourner());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_RETOUR_LIGNE, retour_ligne.getPeremptionDate());
        contentValues.put(Constantes.CLE_COL_DESTRUCTION_QTE_RETOUR_LIGNE, retour_ligne.getDestruction_Qte());
        contentValues.put(Constantes.CLE_COL_RETOURPUI_QTE_RETOUR_LIGNE, retour_ligne.getRetourPui_Qte());
        contentValues.put(Constantes.CLE_COL_RETOURFRS_QTE_RETOUR_LIGNE, retour_ligne.getRetourFrs_Qte());
        contentValues.put(Constantes.CLE_COL_QUARANTAINE_QTE_DEMANDER_RETOUR_LIGNE, retour_ligne.getQuarantaine_Qte_Demander());
        contentValues.put(Constantes.CLE_COL_RETOURPUI_EMPLACEMENT_RETOUR_LIGNE, retour_ligne.getRetourPUI_Emplacement());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENTORIGINE_RETOUR_LIGNE, retour_ligne.getEmplacementOrigine());
        contentValues.put(Constantes.CLE_COL_PATIENTID_RETOUR_LIGNE, retour_ligne.getPatientID());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, retour_ligne.getPhiMR4UUID());
        contentValues.put(Constantes.CLE_COL_RETOURPUI_ZONE_RETOUR_LIGNE, retour_ligne.getRetourPUI_Zone());
        contentValues.put(Constantes.CLE_COL_SERIE_RETOURNER, retour_ligne.getSerie_Retourner());

        return db.update(Constantes.TABLE_RETOUR_LIGNE, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + retour_ligne.getPhiMR4UUID(), null);
    }

    public static void supprimerUnRetourLigne(SQLiteDatabase db, Retour_Ligne retour_ligne) {
        db.delete(Constantes.TABLE_RETOUR_LIGNE, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + retour_ligne.getPhiMR4UUID(), null);
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_RETOUR_LIGNE = "Retour_ligne";

        public static final String CLE_COL_CODE_PRODUIT_RETOUR_LIGNE = "Code_produit";
        public static final int NUM_COL_CODE_PRODUIT_RETOUR_LIGNE = 1;
        public static final String TYPE_COL_CODE_PRODUIT_RETOUR_LIGNE = "INTEGER";
        public static final String CLE_COL_PRODUIT_REFERENCE_RETOUR_LIGNE = "produit_Reference";
        public static final int NUM_COL_PRODUIT_REFERENCE_RETOUR_LIGNE = 2;
        public static final String TYPE_COL_PRODUIT_REFERENCE_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_QTE_RETOURNER_RETOUR_LIGNE = "Qte_Retourner";
        public static final int NUM_COL_QTE_RETOURNER_RETOUR_LIGNE = 3;
        public static final String TYPE_COL_QTE_RETOURNER_RETOUR_LIGNE = "REAL";
        public static final String CLE_COL_PRODUIT_FOURNISSEUR_RETOUR_LIGNE = "produit_Fournisseur";
        public static final int NUM_COL_PRODUIT_FOURNISSEUR_RETOUR_LIGNE = 4;
        public static final String TYPE_COL_PRODUIT_FOURNISSEUR_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_PRODUIT_PUHT_RETOUR_LIGNE = "produit_PUHT";
        public static final int NUM_COL_PRODUIT_PUHT_RETOUR_LIGNE = 5;
        public static final String TYPE_COL_PRODUIT_PUHT_RETOUR_LIGNE = "REAL";
        public static final String CLE_COL_PRODUIT_TVA_RETOUR_LIGNE = "produit_TVA";
        public static final int NUM_COL_PRODUIT_TVA_RETOUR_LIGNE = 6;
        public static final String TYPE_COL_PRODUIT_TVA_RETOUR_LIGNE = "REAL";
        public static final String CLE_COL_PRODUIT_DESIGNATION_RETOUR_LIGNE = "produit_Designation";
        public static final int NUM_COL_PRODUIT_DESIGNATION_RETOUR_LIGNE = 7;
        public static final String TYPE_COL_PRODUIT_DESIGNATION_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_MONTANT_TTC_RETOUR_LIGNE = "Montant_TTC";
        public static final int NUM_COL_MONTANT_TTC_RETOUR_LIGNE = 8;
        public static final String TYPE_COL_MONTANT_TTC_RETOUR_LIGNE = "REAL";
        public static final String CLE_COL_PIECE_CODE_RETOUR_LIGNE = "piece_Code";
        public static final int NUM_COL_PIECE_CODE_RETOUR_LIGNE = 9;
        public static final String TYPE_COL_PIECE_CODE_RETOUR_LIGNE = "INTEGER";
        public static final String CLE_COL_DATE_VALIDATION_RETOUR_LIGNE = "Date_validation";
        public static final int NUM_COL_DATE_VALIDATION_RETOUR_LIGNE = 10;
        public static final String TYPE_COL_DATE_VALIDATION_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_DESTINATION_RETOUR_LIGNE = "Destination";
        public static final int NUM_COL_DESTINATION_RETOUR_LIGNE = 11;
        public static final String TYPE_COL_DESTINATION_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_DEVISE_RETOUR_LIGNE = "Devise";
        public static final int NUM_COL_DEVISE_RETOUR_LIGNE = 12;
        public static final String TYPE_COL_DEVISE_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_QTE_AVANT_RETOUR_RETOUR_LIGNE = "Qte_avant_retour";
        public static final int NUM_COL_QTE_AVANT_RETOUR_RETOUR_LIGNE = 13;
        public static final String TYPE_COL_QTE_AVANT_RETOUR_RETOUR_LIGNE = "REAL";
        public static final String CLE_COL_SYS_DT_MAJ_RETOUR_LIGNE = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_RETOUR_LIGNE = 14;
        public static final String TYPE_COL_SYS_DT_MAJ_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_RETOUR_LIGNE = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_RETOUR_LIGNE = 15;
        public static final String TYPE_COL_SYS_HEURE_MAJ_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_RETOUR_LIGNE = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_RETOUR_LIGNE = 16;
        public static final String TYPE_COL_SYS_USER_MAJ_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_LOT_RETOUR_LIGNE = "Lot";
        public static final int NUM_COL_LOT_RETOUR_LIGNE = 17;
        public static final String TYPE_COL_LOT_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_PATIENTIDENTITE_RETOUR_LIGNE = "patientIdentite";
        public static final int NUM_COL_PATIENTIDENTITE_RETOUR_LIGNE = 18;
        public static final String TYPE_COL_PATIENTIDENTITE_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL__UID_RETOUR_LIGNE = "_UID";
        public static final int NUM_COL__UID_RETOUR_LIGNE = 19;
        public static final String TYPE_COL__UID_RETOUR_LIGNE = "INTEGER";
        public static final String CLE_COL_QTE_DEMANDER_RETOUR_LIGNE = "Qte_Demander";
        public static final int NUM_COL_QTE_DEMANDER_RETOUR_LIGNE = 20;
        public static final String TYPE_COL_QTE_DEMANDER_RETOUR_LIGNE = "REAL";
        public static final String CLE_COL_LOT_RETOURNER_RETOUR_LIGNE = "Lot_Retourner";
        public static final int NUM_COL_LOT_RETOURNER_RETOUR_LIGNE = 21;
        public static final String TYPE_COL_LOT_RETOURNER_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_PEREMPTIONDATE_RETOUR_LIGNE = "peremptionDate";
        public static final int NUM_COL_PEREMPTIONDATE_RETOUR_LIGNE = 22;
        public static final String TYPE_COL_PEREMPTIONDATE_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_DESTRUCTION_QTE_RETOUR_LIGNE = "Destruction_Qte";
        public static final int NUM_COL_DESTRUCTION_QTE_RETOUR_LIGNE = 23;
        public static final String TYPE_COL_DESTRUCTION_QTE_RETOUR_LIGNE = "INTEGER";
        public static final String CLE_COL_RETOURPUI_QTE_RETOUR_LIGNE = "RetourPui_Qte";
        public static final int NUM_COL_RETOURPUI_QTE_RETOUR_LIGNE = 24;
        public static final String TYPE_COL_RETOURPUI_QTE_RETOUR_LIGNE = "INTEGER";
        public static final String CLE_COL_RETOURFRS_QTE_RETOUR_LIGNE = "RetourFrs_Qte";
        public static final int NUM_COL_RETOURFRS_QTE_RETOUR_LIGNE = 25;
        public static final String TYPE_COL_RETOURFRS_QTE_RETOUR_LIGNE = "INTEGER";
        public static final String CLE_COL_QUARANTAINE_QTE_DEMANDER_RETOUR_LIGNE = "Quarantaine_Qte_Demander";
        public static final int NUM_COL_QUARANTAINE_QTE_DEMANDER_RETOUR_LIGNE = 26;
        public static final String TYPE_COL_QUARANTAINE_QTE_DEMANDER_RETOUR_LIGNE = "INTEGER";
        public static final String CLE_COL_RETOURPUI_EMPLACEMENT_RETOUR_LIGNE = "RetourPUI_Emplacement";
        public static final int NUM_COL_RETOURPUI_EMPLACEMENT_RETOUR_LIGNE = 27;
        public static final String TYPE_COL_RETOURPUI_EMPLACEMENT_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_EMPLACEMENTORIGINE_RETOUR_LIGNE = "emplacementOrigine";
        public static final int NUM_COL_EMPLACEMENTORIGINE_RETOUR_LIGNE = 28;
        public static final String TYPE_COL_EMPLACEMENTORIGINE_RETOUR_LIGNE = "TEXT";
        public static final String CLE_COL_PATIENTID_RETOUR_LIGNE = "patientID";
        public static final int NUM_COL_PATIENTID_RETOUR_LIGNE = 29;
        public static final String TYPE_COL_PATIENTID_RETOUR_LIGNE = "INTEGER";
        public static final String CLE_COL_RETOUR_UID_RETOUR_LIGNE = "retour_UID";
        public static final int NUM_COL_RETOUR_UID_RETOUR_LIGNE = 30;
        public static final String TYPE_COL_RETOUR_UID_RETOUR_LIGNE = "INTEGER";

        public static final String CLE_COL_RETOURPUI_ZONE_RETOUR_LIGNE = "RetourPUI_Zone";
        public static final int NUM_COL_RETOURPUI_ZONE_RETOUR_LIGNE = 31;
        public static final String TYPE_COL_RETOURPUI_ZONE_RETOUR_LIGNE = "TEXT";

        public static final String CLE_COL_SERIE_RETOURNER = "Serie_Retourner";
        public static final int NUM_COL_SERIE_RETOURNER = 32;
        public static final String TYPE_COL_SERIE_RETOURNER = "TEXT";


        public static final String CREATION_TABLE_RETOUR_LIGNE = "CREATE TABLE " + Constantes.TABLE_RETOUR_LIGNE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_CODE_PRODUIT_RETOUR_LIGNE + " " + Constantes.TYPE_COL_CODE_PRODUIT_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUIT_REFERENCE_RETOUR_LIGNE + " " + Constantes.TYPE_COL_PRODUIT_REFERENCE_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_RETOURNER_RETOUR_LIGNE + " " + Constantes.TYPE_COL_QTE_RETOURNER_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUIT_FOURNISSEUR_RETOUR_LIGNE + " " + Constantes.TYPE_COL_PRODUIT_FOURNISSEUR_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUIT_PUHT_RETOUR_LIGNE + " " + Constantes.TYPE_COL_PRODUIT_PUHT_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUIT_TVA_RETOUR_LIGNE + " " + Constantes.TYPE_COL_PRODUIT_TVA_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUIT_DESIGNATION_RETOUR_LIGNE + " " + Constantes.TYPE_COL_PRODUIT_DESIGNATION_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_MONTANT_TTC_RETOUR_LIGNE + " " + Constantes.TYPE_COL_MONTANT_TTC_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_PIECE_CODE_RETOUR_LIGNE + " " + Constantes.TYPE_COL_PIECE_CODE_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_DATE_VALIDATION_RETOUR_LIGNE + " " + Constantes.TYPE_COL_DATE_VALIDATION_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_DESTINATION_RETOUR_LIGNE + " " + Constantes.TYPE_COL_DESTINATION_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_DEVISE_RETOUR_LIGNE + " " + Constantes.TYPE_COL_DEVISE_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_AVANT_RETOUR_RETOUR_LIGNE + " " + Constantes.TYPE_COL_QTE_AVANT_RETOUR_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_RETOUR_LIGNE + " " + Constantes.TYPE_COL_SYS_DT_MAJ_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_RETOUR_LIGNE + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_RETOUR_LIGNE + " " + Constantes.TYPE_COL_SYS_USER_MAJ_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_LOT_RETOUR_LIGNE + " " + Constantes.TYPE_COL_LOT_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_PATIENTIDENTITE_RETOUR_LIGNE + " " + Constantes.TYPE_COL_PATIENTIDENTITE_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL__UID_RETOUR_LIGNE + " " + Constantes.TYPE_COL__UID_RETOUR_LIGNE + ","
                + Constantes.CLE_COL_QTE_DEMANDER_RETOUR_LIGNE + " " + Constantes.TYPE_COL_QTE_DEMANDER_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_LOT_RETOURNER_RETOUR_LIGNE + " " + Constantes.TYPE_COL_LOT_RETOURNER_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_PEREMPTIONDATE_RETOUR_LIGNE + " " + Constantes.TYPE_COL_PEREMPTIONDATE_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_DESTRUCTION_QTE_RETOUR_LIGNE + " " + Constantes.TYPE_COL_DESTRUCTION_QTE_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_RETOURPUI_QTE_RETOUR_LIGNE + " " + Constantes.TYPE_COL_RETOURPUI_QTE_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_RETOURFRS_QTE_RETOUR_LIGNE + " " + Constantes.TYPE_COL_RETOURFRS_QTE_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_QUARANTAINE_QTE_DEMANDER_RETOUR_LIGNE + " " + Constantes.TYPE_COL_QUARANTAINE_QTE_DEMANDER_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_RETOURPUI_EMPLACEMENT_RETOUR_LIGNE + " " + Constantes.TYPE_COL_RETOURPUI_EMPLACEMENT_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_EMPLACEMENTORIGINE_RETOUR_LIGNE + " " + Constantes.TYPE_COL_EMPLACEMENTORIGINE_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_PATIENTID_RETOUR_LIGNE + " " + Constantes.TYPE_COL_PATIENTID_RETOUR_LIGNE + ","
                + Constantes.CLE_COL_RETOUR_UID_RETOUR_LIGNE + " " + Constantes.TYPE_COL_RETOUR_UID_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_RETOURPUI_ZONE_RETOUR_LIGNE + " " + Constantes.TYPE_COL_RETOURPUI_ZONE_RETOUR_LIGNE + " ,"
                + Constantes.CLE_COL_SERIE_RETOURNER + " " + Constantes.TYPE_COL_SERIE_RETOURNER

                + ");";
    }
}
