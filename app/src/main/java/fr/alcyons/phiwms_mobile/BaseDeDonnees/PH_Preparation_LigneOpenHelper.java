package fr.alcyons.phiwms_mobile.BaseDeDonnees;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;

public class PH_Preparation_LigneOpenHelper extends DBOpenHelper {

    public PH_Preparation_LigneOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTablePH_Preparation_Lignes(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PH_PREPARATION_LIGNE, null, null);
    }

    public static int getCountPHPreparationLignesParPHPreparation(SQLiteDatabase db, PH_Preparation phPreparation) {
        int total = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=?", new String[]{String.valueOf(phPreparation.getUID())});
        total = cursor.getCount();
        cursor.close();
        cursor = null;
        return total;
    }

    public static int getCountPHPreparationLignesDemandeParPHPreparation(SQLiteDatabase db, PH_Preparation phPreparation) {
        int total = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=?", new String[]{String.valueOf(phPreparation.getUID())});

        while (cursor.moveToNext()) {
            PH_Preparation_Ligne ph_preparation_ligne = new PH_Preparation_Ligne(cursor);
            if (ph_preparation_ligne.getQte_StockSaisie() != -1 && !ph_preparation_ligne.getSYS_DT_MAJ().contentEquals("0000-00-00")) {
                total ++;
            }
        }

        cursor.close();
        cursor = null;
        return total;
    }


    public static void viderTablePH_Preparation_LignesParPreparation(SQLiteDatabase db, int preparationID) {
        db.delete(Constantes.TABLE_PH_PREPARATION_LIGNE, Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE+" = ?", new String[]{String.valueOf(preparationID)});
    }
    public static long insererUnPH_Preparation_LigneEnBDD(SQLiteDatabase db, PH_Preparation_Ligne ph_preparation_ligne) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPreparationID());
        contentValues.put(Constantes.CLE_COL__UID_PH_PREPARATION_LIGNE, ph_preparation_ligne.get_UID());
        contentValues.put(Constantes.CLE_COL_PRODUITID_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitID());
        contentValues.put(Constantes.CLE_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitDesignation());
        contentValues.put(Constantes.CLE_COL_QTE_APREPARER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_APreparer());
        contentValues.put(Constantes.CLE_COL_QTE_LIVRER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_livrer());
        contentValues.put(Constantes.CLE_COL_LIVRER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getLivrer());
        contentValues.put(Constantes.CLE_COL_VALIDER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getValider());
        contentValues.put(Constantes.CLE_COL_VALIDATIONDATE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getValidationDate());
        contentValues.put(Constantes.CLE_COL_PRODUITREFERENCE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitReference());
        contentValues.put(Constantes.CLE_COL_ZONEDEPOT_PH_PREPARATION_LIGNE, ph_preparation_ligne.getZoneDepot());
        contentValues.put(Constantes.CLE_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitCategorie());
        contentValues.put(Constantes.CLE_COL_QTE_RAL_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_RAL());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PH_PREPARATION_LIGNE, ph_preparation_ligne.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PH_PREPARATION_LIGNE, ph_preparation_ligne.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PH_PREPARATION_LIGNE, ph_preparation_ligne.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_PRODUITCONDDISTRIB_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitCondDistrib());
        contentValues.put(Constantes.CLE_COL_PRODUITPUHT_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitPUHT());
        contentValues.put(Constantes.CLE_COL_SUIVI_PAR_LOT_PH_PREPARATION_LIGNE, ph_preparation_ligne.getSuivi_Par_Lot());
        contentValues.put(Constantes.CLE_COL_PATIENTID_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPatientID());
        contentValues.put(Constantes.CLE_COL_PATIENTNOM_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPatientNom());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTEURNOM_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPrescripteurNom());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPrescripteurReference());
        contentValues.put(Constantes.CLE_COL_ORDRE_IMPRESSION_PH_PREPARATION_LIGNE, ph_preparation_ligne.getOrdre_Impression());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTION_ID_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPrescription_ID());
        contentValues.put(Constantes.CLE_COL_LOTNUMERO_PH_PREPARATION_LIGNE, ph_preparation_ligne.getLotNumero());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPeremptionDate());
        contentValues.put(Constantes.CLE_COL_PRODUITPOIDS_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitPoids());
        contentValues.put(Constantes.CLE_COL_PRODUITTVA_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitTVA());
        contentValues.put(Constantes.CLE_COL_MONTANT_HT_PH_PREPARATION_LIGNE, ph_preparation_ligne.getMontant_HT());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_PH_PREPARATION_LIGNE, ph_preparation_ligne.getMontant_TTC());
        contentValues.put(Constantes.CLE_COL_POIDSTOTAL_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPoidsTotal());
        contentValues.put(Constantes.CLE_COL_DEPOT_DESTINATAIRE_REFERENCE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getDepot_Destinataire_Reference());
        contentValues.put(Constantes.CLE_COL_UTILISATION_DATE_PREVUE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getUtilisation_Date_Prevue());
        contentValues.put(Constantes.CLE_COL_QTE_BESOIN_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_besoin());
        contentValues.put(Constantes.CLE_COL_QTE_STOCKSAISIE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_StockSaisie());
        contentValues.put(Constantes.CLE_COL_QTE_DEMANDER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_Demander());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENTPARDEFAUT_PH_PREPARATION_LIGNE, ph_preparation_ligne.getEmplacementParDefaut());
        contentValues.put(Constantes.CLE_COL_QTE_PREPARER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_preparer());
        contentValues.put(Constantes.CLE_COL_ACCEPTER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getAccepter());
        contentValues.put(Constantes.CLE_COL_SUIVI_PAR_SERIE, ph_preparation_ligne.isSuivi_Par_Serie());
        contentValues.put(Constantes.CLE_COL_SERIALISER_RECEPTION, ph_preparation_ligne.isSerialiser_Reception());
        contentValues.put(Constantes.CLE_COL_SERIE_NUMERO, ph_preparation_ligne.getSerieNumero());
        contentValues.put(Constantes.CLE_COL_VERROUILLER, ph_preparation_ligne.isVerrouiller());
        contentValues.put(Constantes.CLE_COL_UID_4D, ph_preparation_ligne.get_UID_4D());

        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PH_PREPARATION_LIGNE, ph_preparation_ligne.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PH_PREPARATION_LIGNE, ph_preparation_ligne.getSYS_HEURE_MAJ());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_PH_PREPARATION_LIGNE, null, contentValues);

        ph_preparation_ligne.setPhiMR4UUID((int) rowId);

        return rowId;
    }

    public static long mettreAJourUnPHPreparationLigne(SQLiteDatabase db, PH_Preparation_Ligne ph_preparation_ligne) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPreparationID());
        contentValues.put(Constantes.CLE_COL__UID_PH_PREPARATION_LIGNE, ph_preparation_ligne.get_UID());
        contentValues.put(Constantes.CLE_COL_PRODUITID_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitID());
        contentValues.put(Constantes.CLE_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitDesignation());
        contentValues.put(Constantes.CLE_COL_QTE_APREPARER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_APreparer());
        contentValues.put(Constantes.CLE_COL_QTE_LIVRER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_livrer());
        contentValues.put(Constantes.CLE_COL_LIVRER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getLivrer());
        contentValues.put(Constantes.CLE_COL_VALIDER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getValider());
        contentValues.put(Constantes.CLE_COL_VALIDATIONDATE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getValidationDate());
        contentValues.put(Constantes.CLE_COL_PRODUITREFERENCE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitReference());
        contentValues.put(Constantes.CLE_COL_ZONEDEPOT_PH_PREPARATION_LIGNE, ph_preparation_ligne.getZoneDepot());
        contentValues.put(Constantes.CLE_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitCategorie());
        contentValues.put(Constantes.CLE_COL_QTE_RAL_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_RAL());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PH_PREPARATION_LIGNE, ph_preparation_ligne.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PH_PREPARATION_LIGNE, ph_preparation_ligne.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PH_PREPARATION_LIGNE, ph_preparation_ligne.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_PRODUITCONDDISTRIB_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitCondDistrib());
        contentValues.put(Constantes.CLE_COL_PRODUITPUHT_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitPUHT());
        contentValues.put(Constantes.CLE_COL_SUIVI_PAR_LOT_PH_PREPARATION_LIGNE, ph_preparation_ligne.getSuivi_Par_Lot());
        contentValues.put(Constantes.CLE_COL_PATIENTID_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPatientID());
        contentValues.put(Constantes.CLE_COL_PATIENTNOM_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPatientNom());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTEURNOM_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPrescripteurNom());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPrescripteurReference());
        contentValues.put(Constantes.CLE_COL_ORDRE_IMPRESSION_PH_PREPARATION_LIGNE, ph_preparation_ligne.getOrdre_Impression());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTION_ID_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPrescription_ID());
        contentValues.put(Constantes.CLE_COL_LOTNUMERO_PH_PREPARATION_LIGNE, ph_preparation_ligne.getLotNumero());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPeremptionDate());
        contentValues.put(Constantes.CLE_COL_PRODUITPOIDS_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitPoids());
        contentValues.put(Constantes.CLE_COL_PRODUITTVA_PH_PREPARATION_LIGNE, ph_preparation_ligne.getProduitTVA());
        contentValues.put(Constantes.CLE_COL_MONTANT_HT_PH_PREPARATION_LIGNE, ph_preparation_ligne.getMontant_HT());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_PH_PREPARATION_LIGNE, ph_preparation_ligne.getMontant_TTC());
        contentValues.put(Constantes.CLE_COL_POIDSTOTAL_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPoidsTotal());
        contentValues.put(Constantes.CLE_COL_DEPOT_DESTINATAIRE_REFERENCE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getDepot_Destinataire_Reference());
        contentValues.put(Constantes.CLE_COL_UTILISATION_DATE_PREVUE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getUtilisation_Date_Prevue());
        contentValues.put(Constantes.CLE_COL_QTE_BESOIN_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_besoin());
        contentValues.put(Constantes.CLE_COL_QTE_STOCKSAISIE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_StockSaisie());
        contentValues.put(Constantes.CLE_COL_QTE_DEMANDER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_Demander());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENTPARDEFAUT_PH_PREPARATION_LIGNE, ph_preparation_ligne.getEmplacementParDefaut());
        contentValues.put(Constantes.CLE_COL_QTE_PREPARER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getQte_preparer());
        contentValues.put(Constantes.CLE_COL_ACCEPTER_PH_PREPARATION_LIGNE, ph_preparation_ligne.getAccepter());
        contentValues.put(Constantes.CLE_COL_SUIVI_PAR_SERIE, ph_preparation_ligne.isSuivi_Par_Serie());
        contentValues.put(Constantes.CLE_COL_SERIALISER_RECEPTION, ph_preparation_ligne.isSerialiser_Reception());
        contentValues.put(Constantes.CLE_COL_SERIE_NUMERO, ph_preparation_ligne.getSerieNumero());
        contentValues.put(Constantes.CLE_COL_UID_4D, ph_preparation_ligne.get_UID_4D());
        contentValues.put(Constantes.CLE_COL_VERROUILLER, ph_preparation_ligne.isVerrouiller());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, ph_preparation_ligne.getPhiMR4UUID());

        long rowId = db.update(Constantes.TABLE_PH_PREPARATION_LIGNE, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + ph_preparation_ligne.getPhiMR4UUID(), null);

        return rowId;
    }

    public static PH_Preparation_Ligne getPH_Preparation_LigneByID(SQLiteDatabase db, int id) {
        PH_Preparation_Ligne phPreparationLigne = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL__UID_PH_PREPARATION_LIGNE + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            phPreparationLigne = new PH_Preparation_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return phPreparationLigne;
    }

    public static PH_Preparation_Ligne getPH_Preparation_LigneByProduitLotPreparation(SQLiteDatabase db, int produitid, int prepartionid, String lotnumero) {
        PH_Preparation_Ligne phPreparationLigne = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PRODUITID_PH_PREPARATION_LIGNE + "=? AND "+Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE+"=? AND "+Constantes.CLE_COL_LOTNUMERO_PH_PREPARATION_LIGNE+"=?", new String[]{String.valueOf(produitid), String.valueOf(prepartionid), String.valueOf(lotnumero)});

        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            phPreparationLigne = new PH_Preparation_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return phPreparationLigne;
    }

    public static PH_Preparation_Ligne getPH_Preparation_LigneByProduitLotPreparationSerieEmplacement(SQLiteDatabase db, int produitid, int prepartionid, String lotnumero, String serie, String emplacement) {
        PH_Preparation_Ligne phPreparationLigne = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PRODUITID_PH_PREPARATION_LIGNE + "=? AND "+Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE+"=? AND "+Constantes.CLE_COL_LOTNUMERO_PH_PREPARATION_LIGNE+"=? AND "+Constantes.CLE_COL_SERIE_NUMERO+"=? AND "+Constantes.CLE_COL_EMPLACEMENTPARDEFAUT_PH_PREPARATION_LIGNE+"=?", new String[]{String.valueOf(produitid), String.valueOf(prepartionid), String.valueOf(lotnumero), serie, emplacement});

        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            phPreparationLigne = new PH_Preparation_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return phPreparationLigne;
    }

    public static PH_Preparation_Ligne getPH_Preparation_LigneByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        PH_Preparation_Ligne phPreparationLigne = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phPreparationLigne = new PH_Preparation_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return phPreparationLigne;
    }

    public static List<PH_Preparation_Ligne> getAllPHPreparationLignesParPHPreparation(SQLiteDatabase db, PH_Preparation phPreparation) {
        List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=? ORDER BY "+Constantes.CLE_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE+","+Constantes.CLE_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE, new String[]{String.valueOf(phPreparation.getUID())});

        while (cursor.moveToNext()) {
            phPreparationLigneList.add(new PH_Preparation_Ligne(cursor));
        }

        cursor.close();
        cursor = null;
        return phPreparationLigneList;
    }

    public static List<PH_Preparation_Ligne> getAllPHPreparationLignesBaseParPHPreparation(SQLiteDatabase db, PH_Preparation phPreparation) {
        List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=? AND "+Constantes.CLE_COL__UID_PH_PREPARATION_LIGNE+" > 0 ORDER BY "+Constantes.CLE_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE+","+Constantes.CLE_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE, new String[]{String.valueOf(phPreparation.getUID())});

        while (cursor.moveToNext()) {
            phPreparationLigneList.add(new PH_Preparation_Ligne(cursor));
        }

        cursor.close();
        cursor = null;
        return phPreparationLigneList;
    }

    public static List<PH_Preparation_Ligne> getAllPHPreparationLignesParPHPreparationOrderDesignation(SQLiteDatabase db, PH_Preparation phPreparation) {
        List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=? ORDER BY "+Constantes.CLE_COL_QTE_APREPARER_PH_PREPARATION_LIGNE+" DESC, "+Constantes.CLE_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE, new String[]{String.valueOf(phPreparation.getUID())});

        while (cursor.moveToNext()) {
            phPreparationLigneList.add(new PH_Preparation_Ligne(cursor));
        }

        cursor.close();
        cursor = null;
        return phPreparationLigneList;
    }

    public static PH_Preparation_Ligne getPHPreparationLignesParPHPreparationAndProduit(SQLiteDatabase db, PH_Preparation phPreparation, int produitCode) {
        PH_Preparation_Ligne phPreparationLigne = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=? AND "+Constantes.CLE_COL_PRODUITID_PH_PREPARATION_LIGNE +"=?", new String[]{String.valueOf(phPreparation.getUID()), String.valueOf(produitCode)});

        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            phPreparationLigne = new PH_Preparation_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return phPreparationLigne;
    }

    public static List<PH_Preparation_Ligne> getAllPHPreparationLignesParPHPreparationAndProduitNeg(SQLiteDatabase db, PH_Preparation phPreparation, int produitCode) {
        List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=? AND "+Constantes.CLE_COL_PRODUITID_PH_PREPARATION_LIGNE +"=? AND "+Constantes.CLE_COL__UID_PH_PREPARATION_LIGNE +" < 0", new String[]{String.valueOf(phPreparation.getUID()), String.valueOf(produitCode)});

        while (cursor.moveToNext()) {
            phPreparationLigneList.add(new PH_Preparation_Ligne(cursor));
        }

        cursor.close();
        cursor = null;
        return phPreparationLigneList;
    }

    public static List<PH_Preparation_Ligne> getAllPHPreparationLignesParPHPreparationNeg(SQLiteDatabase db, PH_Preparation phPreparation) {
        List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=? AND "+Constantes.CLE_COL__UID_PH_PREPARATION_LIGNE +" < 0", new String[]{String.valueOf(phPreparation.getUID())});

        while (cursor.moveToNext()) {
            phPreparationLigneList.add(new PH_Preparation_Ligne(cursor));
        }

        cursor.close();
        cursor = null;
        return phPreparationLigneList;
    }

    public static List<PH_Preparation_Ligne> getAllPHPreparationLignesParPHPreparationLivraison(SQLiteDatabase db, PH_Preparation phPreparation) {
        List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=? AND "+Constantes.CLE_COL_QTE_DEMANDER_PH_PREPARATION_LIGNE+" > 0 ORDER BY "+Constantes.CLE_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE+","+Constantes.CLE_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE, new String[]{String.valueOf(phPreparation.getUID())});

        while (cursor.moveToNext()) {
            phPreparationLigneList.add(new PH_Preparation_Ligne(cursor));
        }

        cursor.close();
        cursor = null;
        return phPreparationLigneList;
    }

    public static List<PH_Preparation_Ligne> getAllPHPreparationLignesAPreparerParPHPreparation(SQLiteDatabase db, PH_Preparation phPreparation) {
        List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=? and "+Constantes.CLE_COL_QTE_APREPARER_PH_PREPARATION_LIGNE+" != 0", new String[]{String.valueOf(phPreparation.getUID())});

        while (cursor.moveToNext()) {
            phPreparationLigneList.add(new PH_Preparation_Ligne(cursor));
        }

        cursor.close();
        cursor = null;
        return phPreparationLigneList;
    }

    public static PH_Preparation_Ligne getUnPHPreparationLignesAPreparerParPHPreparationetProduit(SQLiteDatabase db, PH_Preparation ph_preparation, int codeProduit) {
        PH_Preparation_Ligne ph_preparation_ligne = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=? and "+Constantes.CLE_COL_QTE_APREPARER_PH_PREPARATION_LIGNE+" != 0 and "+Constantes.CLE_COL_PRODUITID_PH_PREPARATION_LIGNE+"=?", new String[]{String.valueOf(ph_preparation.getUID()), String.valueOf(codeProduit)});

        while (cursor.moveToNext()) {
            ph_preparation_ligne = new PH_Preparation_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return ph_preparation_ligne;
    }

    public static PH_Preparation_Ligne getPH_Preparation_LigneByPreparationAndIdProduit(SQLiteDatabase db, PH_Preparation ph_preparation, int codeProduit) {
        PH_Preparation_Ligne ph_preparation_ligne = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=? and "+Constantes.CLE_COL_PRODUITID_PH_PREPARATION_LIGNE+"=?", new String[]{String.valueOf(ph_preparation.getUID()), String.valueOf(codeProduit)});

        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            ph_preparation_ligne = new PH_Preparation_Ligne(cursor);
        }

        cursor.close();
        cursor = null;
        return ph_preparation_ligne;
    }

    public static List<PH_Preparation_Ligne> getAllPHPreparationLignesRAL(SQLiteDatabase db, PH_Preparation phPreparation) {
        List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=?", new String[]{String.valueOf(phPreparation.getUID())});

        while (cursor.moveToNext()) {
            PH_Preparation_Ligne phPreparationLigneCourant = new PH_Preparation_Ligne(cursor);
            /*if(phPreparationLigneCourant.getQte_preparer() == 0 && phPreparationLigneCourant.getQte_RAL() != 0)
            {
                phPreparationLigneList.add(phPreparationLigneCourant);
            }*/
            if(phPreparationLigneCourant.getQte_RAL() != 0)
            {
                phPreparationLigneList.add(phPreparationLigneCourant);
            }
        }

        cursor.close();
        cursor = null;
        return phPreparationLigneList;
    }

    public static List<PH_Preparation_Ligne> getALivrerPHPreparationLignesParPHPreparation(SQLiteDatabase db, PH_Preparation phPreparation) {
        List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE + " WHERE " + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + "=?", new String[]{String.valueOf(phPreparation.getUID())});

        while (cursor.moveToNext()) {
            PH_Preparation_Ligne ph_preparation_ligne = new PH_Preparation_Ligne(cursor);
            if (ph_preparation_ligne.getQte_livrer() > 0) {
                phPreparationLigneList.add(ph_preparation_ligne);
            }
        }

        cursor.close();
        cursor = null;
        return phPreparationLigneList;
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        int compteur_suppression = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION_LIGNE, null);

        while (cursor.moveToNext()) {
            PH_Preparation_Ligne ph_preparation_ligne = new PH_Preparation_Ligne(cursor);
            if (ph_preparation_ligne.getProduitDesignation().contentEquals("Traceur_Medicament_ALCYONS") || ph_preparation_ligne.getProduitDesignation().contentEquals("Traceur_Dispositif_ALCYONS")) {
                db.delete(Constantes.TABLE_PH_PREPARATION_LIGNE, Constantes.CLE_COL__UID_PH_PREPARATION_LIGNE + "=?", new String[]{String.valueOf(ph_preparation_ligne.get_UID())});
                compteur_suppression++;
            }
        }

        cursor.close();
        cursor = null;
        return (long) compteur_suppression;
    }

    public static void supprimerUnPhPreparationLigne(SQLiteDatabase db, PH_Preparation_Ligne ph_preparation_ligne) {
        db.delete(Constantes.TABLE_PH_PREPARATION_LIGNE, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(ph_preparation_ligne.getPhiMR4UUID())});
    }

    public static void supprimerPHPreparationLigneByPreparation(SQLiteDatabase db, PH_Preparation phPreparation) {
        db.delete(Constantes.TABLE_PH_PREPARATION_LIGNE, Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE+" = ?", new String[]{String.valueOf(phPreparation.getUID())});
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PH_PREPARATION_LIGNE = "PH_Preparation_ligne";

        public static final String CLE_COL__UID_PH_PREPARATION_LIGNE = "_UID";
        public static final int NUM_COL__UID_PH_PREPARATION_LIGNE = 1;
        public static final String TYPE_COL__UID_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_PRODUITID_PH_PREPARATION_LIGNE = "produitID";
        public static final int NUM_COL_PRODUITID_PH_PREPARATION_LIGNE = 2;
        public static final String TYPE_COL_PRODUITID_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE = "produitDesignation";
        public static final int NUM_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE = 3;
        public static final String TYPE_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_QTE_APREPARER_PH_PREPARATION_LIGNE = "Qte_APreparer";
        public static final int NUM_COL_QTE_APREPARER_PH_PREPARATION_LIGNE = 4;
        public static final String TYPE_COL_QTE_APREPARER_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_QTE_LIVRER_PH_PREPARATION_LIGNE = "Qte_livrer";
        public static final int NUM_COL_QTE_LIVRER_PH_PREPARATION_LIGNE = 5;
        public static final String TYPE_COL_QTE_LIVRER_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_LIVRER_PH_PREPARATION_LIGNE = "Livrer";
        public static final int NUM_COL_LIVRER_PH_PREPARATION_LIGNE = 6;
        public static final String TYPE_COL_LIVRER_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_VALIDER_PH_PREPARATION_LIGNE = "Valider";
        public static final int NUM_COL_VALIDER_PH_PREPARATION_LIGNE = 7;
        public static final String TYPE_COL_VALIDER_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_VALIDATIONDATE_PH_PREPARATION_LIGNE = "ValidationDate";
        public static final int NUM_COL_VALIDATIONDATE_PH_PREPARATION_LIGNE = 8;
        public static final String TYPE_COL_VALIDATIONDATE_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_PRODUITREFERENCE_PH_PREPARATION_LIGNE = "produitReference";
        public static final int NUM_COL_PRODUITREFERENCE_PH_PREPARATION_LIGNE = 9;
        public static final String TYPE_COL_PRODUITREFERENCE_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_ZONEDEPOT_PH_PREPARATION_LIGNE = "ZoneDepot";
        public static final int NUM_COL_ZONEDEPOT_PH_PREPARATION_LIGNE = 10;
        public static final String TYPE_COL_ZONEDEPOT_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE = "produitCategorie";
        public static final int NUM_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE = 11;
        public static final String TYPE_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_QTE_RAL_PH_PREPARATION_LIGNE = "Qte_RAL";
        public static final int NUM_COL_QTE_RAL_PH_PREPARATION_LIGNE = 12;
        public static final String TYPE_COL_QTE_RAL_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_SYS_DT_MAJ_PH_PREPARATION_LIGNE = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PH_PREPARATION_LIGNE = 13;
        public static final String TYPE_COL_SYS_DT_MAJ_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PH_PREPARATION_LIGNE = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PH_PREPARATION_LIGNE = 14;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PH_PREPARATION_LIGNE = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PH_PREPARATION_LIGNE = 15;
        public static final String TYPE_COL_SYS_USER_MAJ_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_PRODUITCONDDISTRIB_PH_PREPARATION_LIGNE = "produitCondDistrib";
        public static final int NUM_COL_PRODUITCONDDISTRIB_PH_PREPARATION_LIGNE = 16;
        public static final String TYPE_COL_PRODUITCONDDISTRIB_PH_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_PRODUITPUHT_PH_PREPARATION_LIGNE = "produitPUHT";
        public static final int NUM_COL_PRODUITPUHT_PH_PREPARATION_LIGNE = 17;
        public static final String TYPE_COL_PRODUITPUHT_PH_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_SUIVI_PAR_LOT_PH_PREPARATION_LIGNE = "Suivi_Par_Lot";
        public static final int NUM_COL_SUIVI_PAR_LOT_PH_PREPARATION_LIGNE = 18;
        public static final String TYPE_COL_SUIVI_PAR_LOT_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_PATIENTID_PH_PREPARATION_LIGNE = "patientID";
        public static final int NUM_COL_PATIENTID_PH_PREPARATION_LIGNE = 19;
        public static final String TYPE_COL_PATIENTID_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_PATIENTNOM_PH_PREPARATION_LIGNE = "PatientNom";
        public static final int NUM_COL_PATIENTNOM_PH_PREPARATION_LIGNE = 20;
        public static final String TYPE_COL_PATIENTNOM_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_PRESCRIPTEURNOM_PH_PREPARATION_LIGNE = "PrescripteurNom";
        public static final int NUM_COL_PRESCRIPTEURNOM_PH_PREPARATION_LIGNE = 21;
        public static final String TYPE_COL_PRESCRIPTEURNOM_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION_LIGNE = "prescripteurReference";
        public static final int NUM_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION_LIGNE = 22;
        public static final String TYPE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_ORDRE_IMPRESSION_PH_PREPARATION_LIGNE = "Ordre_Impression";
        public static final int NUM_COL_ORDRE_IMPRESSION_PH_PREPARATION_LIGNE = 23;
        public static final String TYPE_COL_ORDRE_IMPRESSION_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_PRESCRIPTION_ID_PH_PREPARATION_LIGNE = "Prescription_ID";
        public static final int NUM_COL_PRESCRIPTION_ID_PH_PREPARATION_LIGNE = 24;
        public static final String TYPE_COL_PRESCRIPTION_ID_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_LOTNUMERO_PH_PREPARATION_LIGNE = "LotNumero";
        public static final int NUM_COL_LOTNUMERO_PH_PREPARATION_LIGNE = 25;
        public static final String TYPE_COL_LOTNUMERO_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_PEREMPTIONDATE_PH_PREPARATION_LIGNE = "PeremptionDate";
        public static final int NUM_COL_PEREMPTIONDATE_PH_PREPARATION_LIGNE = 26;
        public static final String TYPE_COL_PEREMPTIONDATE_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_PRODUITPOIDS_PH_PREPARATION_LIGNE = "produitPoids";
        public static final int NUM_COL_PRODUITPOIDS_PH_PREPARATION_LIGNE = 27;
        public static final String TYPE_COL_PRODUITPOIDS_PH_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_PRODUITTVA_PH_PREPARATION_LIGNE = "produitTVA";
        public static final int NUM_COL_PRODUITTVA_PH_PREPARATION_LIGNE = 28;
        public static final String TYPE_COL_PRODUITTVA_PH_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_MONTANT_HT_PH_PREPARATION_LIGNE = "Montant_HT";
        public static final int NUM_COL_MONTANT_HT_PH_PREPARATION_LIGNE = 29;
        public static final String TYPE_COL_MONTANT_HT_PH_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_MONTANT_TTC_PH_PREPARATION_LIGNE = "Montant_TTC";
        public static final int NUM_COL_MONTANT_TTC_PH_PREPARATION_LIGNE = 30;
        public static final String TYPE_COL_MONTANT_TTC_PH_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_POIDSTOTAL_PH_PREPARATION_LIGNE = "PoidsTotal";
        public static final int NUM_COL_POIDSTOTAL_PH_PREPARATION_LIGNE = 31;
        public static final String TYPE_COL_POIDSTOTAL_PH_PREPARATION_LIGNE = "REAL";
        public static final String CLE_COL_DEPOT_DESTINATAIRE_REFERENCE_PH_PREPARATION_LIGNE = "depot_Destinataire_Reference";
        public static final int NUM_COL_DEPOT_DESTINATAIRE_REFERENCE_PH_PREPARATION_LIGNE = 32;
        public static final String TYPE_COL_DEPOT_DESTINATAIRE_REFERENCE_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_UTILISATION_DATE_PREVUE_PH_PREPARATION_LIGNE = "utilisation_Date_Prevue";
        public static final int NUM_COL_UTILISATION_DATE_PREVUE_PH_PREPARATION_LIGNE = 33;
        public static final String TYPE_COL_UTILISATION_DATE_PREVUE_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_QTE_BESOIN_PH_PREPARATION_LIGNE = "Qte_besoin";
        public static final int NUM_COL_QTE_BESOIN_PH_PREPARATION_LIGNE = 34;
        public static final String TYPE_COL_QTE_BESOIN_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_QTE_STOCKSAISIE_PH_PREPARATION_LIGNE = "Qte_StockSaisie";
        public static final int NUM_COL_QTE_STOCKSAISIE_PH_PREPARATION_LIGNE = 35;
        public static final String TYPE_COL_QTE_STOCKSAISIE_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_QTE_DEMANDER_PH_PREPARATION_LIGNE = "Qte_Demander";
        public static final int NUM_COL_QTE_DEMANDER_PH_PREPARATION_LIGNE = 36;
        public static final String TYPE_COL_QTE_DEMANDER_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_EMPLACEMENTPARDEFAUT_PH_PREPARATION_LIGNE = "EmplacementParDefaut";
        public static final int NUM_COL_EMPLACEMENTPARDEFAUT_PH_PREPARATION_LIGNE = 37;
        public static final String TYPE_COL_EMPLACEMENTPARDEFAUT_PH_PREPARATION_LIGNE = "TEXT";
        public static final String CLE_COL_QTE_PREPARER_PH_PREPARATION_LIGNE = "Qte_preparer";
        public static final int NUM_COL_QTE_PREPARER_PH_PREPARATION_LIGNE = 38;
        public static final String TYPE_COL_QTE_PREPARER_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE = "PreparationID";
        public static final int NUM_COL_PREPARATIONID_PH_PREPARATION_LIGNE = 39;
        public static final String TYPE_COL_PREPARATIONID_PH_PREPARATION_LIGNE = "INTEGER";
        public static final String CLE_COL_ACCEPTER_PH_PREPARATION_LIGNE = "Accepter";
        public static final int NUM_COL_ACCEPTER_PH_PREPARATION_LIGNE = 40;
        public static final String TYPE_COL_ACCEPTER_PH_PREPARATION_LIGNE = "BOOLEAN";
        public static final String CLE_COL_SUIVI_PAR_SERIE = "Suivi_Par_Serie";
        public static final int NUM_COL_SUIVI_PAR_SERIE = 41;
        public static final String TYPE_COL_SUIVI_PAR_SERIE = "INTEGER";
        public static final String CLE_COL_SERIALISER_RECEPTION = "Serialiser_Reception";
        public static final int NUM_COL_SERIALISER_RECEPTION = 42;
        public static final String TYPE_COL_SERIALISER_RECEPTION = "INTEGER";
        public static final String CLE_COL_SERIE_NUMERO = "SerieNumero";
        public static final int NUM_COL_SERIE_NUMERO = 43;
        public static final String TYPE_COL_SERIE_NUMERO = "TEXT";
        public static final String CLE_COL_UID_4D = "_UID_4D";
        public static final int NUM_COL_UID_4D = 44;
        public static final String TYPE_COL_UID_4D = "INTEGER";
        public static final String CLE_COL_VERROUILLER = "Verrouiller";
        public static final int NUM_COL_VERROUILLER = 45;
        public static final String TYPE_COL_VERROUILLER = "INTEGER";


        public static final String CREATION_TABLE_PH_PREPARATION_LIGNE = "CREATE TABLE " + Constantes.TABLE_PH_PREPARATION_LIGNE
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL__UID_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL__UID_PH_PREPARATION_LIGNE + ","
                + Constantes.CLE_COL_PRODUITID_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRODUITID_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRODUITDESIGNATION_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_APREPARER_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_APREPARER_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_LIVRER_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_LIVRER_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_LIVRER_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_LIVRER_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_VALIDER_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_VALIDER_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_VALIDATIONDATE_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_VALIDATIONDATE_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUITREFERENCE_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRODUITREFERENCE_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_ZONEDEPOT_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_ZONEDEPOT_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRODUITCATEGORIE_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_RAL_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_RAL_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_SYS_DT_MAJ_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_SYS_USER_MAJ_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUITCONDDISTRIB_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRODUITCONDDISTRIB_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUITPUHT_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRODUITPUHT_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_SUIVI_PAR_LOT_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_SUIVI_PAR_LOT_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PATIENTID_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PATIENTID_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PATIENTNOM_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PATIENTNOM_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRESCRIPTEURNOM_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRESCRIPTEURNOM_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_ORDRE_IMPRESSION_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_ORDRE_IMPRESSION_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRESCRIPTION_ID_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRESCRIPTION_ID_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_LOTNUMERO_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_LOTNUMERO_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PEREMPTIONDATE_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PEREMPTIONDATE_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUITPOIDS_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRODUITPOIDS_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_PRODUITTVA_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PRODUITTVA_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_MONTANT_HT_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_MONTANT_HT_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_MONTANT_TTC_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_MONTANT_TTC_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_POIDSTOTAL_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_POIDSTOTAL_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_DEPOT_DESTINATAIRE_REFERENCE_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_DEPOT_DESTINATAIRE_REFERENCE_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_UTILISATION_DATE_PREVUE_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_UTILISATION_DATE_PREVUE_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_BESOIN_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_BESOIN_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_STOCKSAISIE_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_STOCKSAISIE_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_DEMANDER_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_DEMANDER_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_EMPLACEMENTPARDEFAUT_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_EMPLACEMENTPARDEFAUT_PH_PREPARATION_LIGNE + " ,"
                + Constantes.CLE_COL_QTE_PREPARER_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_QTE_PREPARER_PH_PREPARATION_LIGNE + ","
                + Constantes.CLE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_PREPARATIONID_PH_PREPARATION_LIGNE + ","
                + Constantes.CLE_COL_ACCEPTER_PH_PREPARATION_LIGNE + " " + Constantes.TYPE_COL_ACCEPTER_PH_PREPARATION_LIGNE+ ","
                + Constantes.CLE_COL_SUIVI_PAR_SERIE + " " + Constantes.TYPE_COL_SUIVI_PAR_SERIE + ","
                + Constantes.CLE_COL_SERIALISER_RECEPTION + " " + Constantes.TYPE_COL_SERIALISER_RECEPTION + ","
                + Constantes.CLE_COL_SERIE_NUMERO + " " + Constantes.TYPE_COL_SERIE_NUMERO + ","
                + Constantes.CLE_COL_UID_4D + " " + Constantes.TYPE_COL_UID_4D + ","
                + Constantes.CLE_COL_VERROUILLER + " " + Constantes.TYPE_COL_VERROUILLER
                + ");";
    }
}
