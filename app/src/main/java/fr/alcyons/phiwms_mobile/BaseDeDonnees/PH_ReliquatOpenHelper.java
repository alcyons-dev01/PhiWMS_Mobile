package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;

public class PH_ReliquatOpenHelper extends DBOpenHelper {

    public PH_ReliquatOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTablePH_Reliquat(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PH_RELIQUAT, null, null);
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        int compteur_suppression = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_RELIQUAT, null);

        while (cursor.moveToNext()) {
            PH_Reliquat ph_reliquat = new PH_Reliquat(cursor);
            if (ph_reliquat.getDesignationCourte().contentEquals("Traceur_Medicament_ALCYONS") || ph_reliquat.getDesignationCourte().contentEquals("Traceur_Dispositif_ALCYONS")) {
                db.delete(Constantes.TABLE_PH_RELIQUAT, Constantes.CLE_COL_RELIQUAT_UID_PH_RELIQUAT + "=?", new String[]{String.valueOf(ph_reliquat.getReliquat_UID())});
                compteur_suppression++;
            }
        }

        cursor.close();
        cursor = null;
        return (long) compteur_suppression;
    }

    public static long supprimerUnPHReliquat(SQLiteDatabase db, PH_Reliquat ph_reliquat)
    {
        return db.delete(Constantes.TABLE_PH_RELIQUAT, Constantes.CLE_COL_RELIQUAT_UID_PH_RELIQUAT + "=?", new String[]{String.valueOf(ph_reliquat.getReliquat_UID())});
    }

    public static long insererPH_ReliquatEnBDD(SQLiteDatabase db, PH_Reliquat objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_RELIQUAT_UID_PH_RELIQUAT, objet.getReliquat_UID());
        contentValues.put(Constantes.CLE_COL_PRODUITID_PH_RELIQUAT, objet.getProduitID());
        contentValues.put(Constantes.CLE_COL_PRODUIT_REFERENCE_PH_RELIQUAT, objet.getProduit_Reference());
        contentValues.put(Constantes.CLE_COL_PU_COMMANDE_PH_RELIQUAT, objet.getPU_commande());
        contentValues.put(Constantes.CLE_COL_DESIGNATIONCOURTE_PH_RELIQUAT, objet.getdesignationCourte());
        contentValues.put(Constantes.CLE_COL_UNITE_PH_RELIQUAT, objet.getunite());
        contentValues.put(Constantes.CLE_COL_FOURNISSEURNOM_PH_RELIQUAT, objet.getfournisseurNom());
        contentValues.put(Constantes.CLE_COL_FOURNISSEURID_PH_RELIQUAT, objet.getFournisseurID());
        contentValues.put(Constantes.CLE_COL_QTECOMMANDE_PH_RELIQUAT, objet.getQteCommande());
        contentValues.put(Constantes.CLE_COL_QTELIVRAISON_PH_RELIQUAT, objet.getQteLivraison());
        contentValues.put(Constantes.CLE_COL_QTERELIQUAT_X_PH_RELIQUAT, objet.getQteReliquat_X());
        contentValues.put(Constantes.CLE_COL_RELIQUATENCOURS_PH_RELIQUAT, objet.getreliquatEncours());
        contentValues.put(Constantes.CLE_COL_ENTREEDATE_PH_RELIQUAT, objet.getentreeDate());
        contentValues.put(Constantes.CLE_COL_SELECTION_PH_RELIQUAT, objet.getselection());
        contentValues.put(Constantes.CLE_COL_COMMANDENUMERO_PH_RELIQUAT, objet.getcommandeNumero());
        contentValues.put(Constantes.CLE_COL_COMMANDEDATE_PH_RELIQUAT, objet.getcommandeDate());
        contentValues.put(Constantes.CLE_COL_CONDITIONNEMENTACHAT_PH_RELIQUAT, objet.getConditionnementAchat());
        contentValues.put(Constantes.CLE_COL_CONDITIONNEMENTDISTRIBUTION_PH_RELIQUAT, objet.getConditionnementDistribution());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_PH_RELIQUAT, objet.getperemptionDate());
        contentValues.put(Constantes.CLE_COL_LOT_PH_RELIQUAT, objet.getlot());
        contentValues.put(Constantes.CLE_COL_SCANREFERENCE_PH_RELIQUAT, objet.getscanReference());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONACTIVE_PH_RELIQUAT, objet.getperemptionActive());
        contentValues.put(Constantes.CLE_COL_COMMANDELIGNEID_PH_RELIQUAT, objet.getcommandeLigneID());
        contentValues.put(Constantes.CLE_COL_PU_FACTURE_PH_RELIQUAT, objet.getPu_facture());
        contentValues.put(Constantes.CLE_COL_REPRIS_PH_RELIQUAT, objet.getRepris());
        contentValues.put(Constantes.CLE_COL_QTEMOUVEMENT_PH_RELIQUAT, objet.getQteMouvement());
        contentValues.put(Constantes.CLE_COL_QTERELIQUAT_Y_PH_RELIQUAT, objet.getQteReliquat_Y());
        contentValues.put(Constantes.CLE_COL_PRODUITGRATUITS_PH_RELIQUAT, objet.getProduitGratuits());
        contentValues.put(Constantes.CLE_COL_DEVISE_PH_RELIQUAT, objet.getDevise());
        contentValues.put(Constantes.CLE_COL_DEPOTREFERENCE_PH_RELIQUAT, objet.getDepotReference());
        contentValues.put(Constantes.CLE_COL__SYS_DT_MAJ_PH_RELIQUAT, objet.get_SYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_HEURE_MAJ_PH_RELIQUAT, objet.get_SYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_USER_MAJ_PH_RELIQUAT, objet.get_SYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_SUIVIPARLOTACTIF_PH_RELIQUAT, objet.getSuiviParLotActif());
        contentValues.put(Constantes.CLE_COL_SYNCHROTIMESTAMP_PH_RELIQUAT, objet.getSynchroTimeStamp());
        contentValues.put(Constantes.CLE_COL_SYNCHROSTATUT_PH_RELIQUAT, objet.getSynchroStatut());
        contentValues.put(Constantes.CLE_COL_FINNESS_PH_RELIQUAT, objet.getFinness());
        contentValues.put(Constantes.CLE_COL_SCANVALUE_PH_RELIQUAT, objet.getScanValue());
        contentValues.put(Constantes.CLE_COL_PATIENTIPP_PH_RELIQUAT, objet.getPatientIPP());
        contentValues.put(Constantes.CLE_COL_PATIENTNOM_PH_RELIQUAT, objet.getPatientNom());
        contentValues.put(Constantes.CLE_COL_TECH_UID_PH_RELIQUAT, objet.gettech_UID());
        contentValues.put(Constantes.CLE_COL_ZONE_PH_RELIQUAT, objet.getZone());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_PH_RELIQUAT, objet.getEmplacement());
        contentValues.put(Constantes.CLE_COL_IPP_PH_RELIQUAT, objet.getIPP());
        contentValues.put(Constantes.CLE_COL_SUIVI_PAR_SERIE_ACTIF, objet.isSuiviParSerieActif());
        contentValues.put(Constantes.CLE_COL_SERIALISATION_RECEPTION, objet.isSerialiserReception());
        contentValues.put(Constantes.CLE_COL_SERIE, objet.getSerie());
        contentValues.put(Constantes.CLE_COL_BL_Numero, objet.getBL_Numero());

        long rowID = db.insert(Constantes.TABLE_PH_RELIQUAT, null, contentValues);
        objet.setphiwms_mobileUUID((int) rowID);
        return rowID;
    }

    public static long mettreAJourUnPHReliquat(SQLiteDatabase db, PH_Reliquat ph_Reliquat) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_RELIQUAT_UID_PH_RELIQUAT, ph_Reliquat.getReliquat_UID());
        contentValues.put(Constantes.CLE_COL_PRODUITID_PH_RELIQUAT, ph_Reliquat.getProduitID());
        contentValues.put(Constantes.CLE_COL_PRODUIT_REFERENCE_PH_RELIQUAT, ph_Reliquat.getProduit_Reference());
        contentValues.put(Constantes.CLE_COL_PU_COMMANDE_PH_RELIQUAT, ph_Reliquat.getPU_commande());
        contentValues.put(Constantes.CLE_COL_DESIGNATIONCOURTE_PH_RELIQUAT, ph_Reliquat.getdesignationCourte());
        contentValues.put(Constantes.CLE_COL_UNITE_PH_RELIQUAT, ph_Reliquat.getunite());
        contentValues.put(Constantes.CLE_COL_FOURNISSEURNOM_PH_RELIQUAT, ph_Reliquat.getfournisseurNom());
        contentValues.put(Constantes.CLE_COL_FOURNISSEURID_PH_RELIQUAT, ph_Reliquat.getFournisseurID());
        contentValues.put(Constantes.CLE_COL_QTECOMMANDE_PH_RELIQUAT, ph_Reliquat.getQteCommande());
        contentValues.put(Constantes.CLE_COL_QTELIVRAISON_PH_RELIQUAT, ph_Reliquat.getQteLivraison());
        contentValues.put(Constantes.CLE_COL_QTERELIQUAT_X_PH_RELIQUAT, ph_Reliquat.getQteReliquat_X());
        contentValues.put(Constantes.CLE_COL_RELIQUATENCOURS_PH_RELIQUAT, ph_Reliquat.getreliquatEncours());
        contentValues.put(Constantes.CLE_COL_ENTREEDATE_PH_RELIQUAT, ph_Reliquat.getentreeDate());
        contentValues.put(Constantes.CLE_COL_SELECTION_PH_RELIQUAT, ph_Reliquat.getselection());
        contentValues.put(Constantes.CLE_COL_COMMANDENUMERO_PH_RELIQUAT, ph_Reliquat.getcommandeNumero());
        contentValues.put(Constantes.CLE_COL_COMMANDEDATE_PH_RELIQUAT, ph_Reliquat.getcommandeDate());
        contentValues.put(Constantes.CLE_COL_CONDITIONNEMENTACHAT_PH_RELIQUAT, ph_Reliquat.getConditionnementAchat());
        contentValues.put(Constantes.CLE_COL_CONDITIONNEMENTDISTRIBUTION_PH_RELIQUAT, ph_Reliquat.getConditionnementDistribution());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONDATE_PH_RELIQUAT, ph_Reliquat.getperemptionDate());
        contentValues.put(Constantes.CLE_COL_LOT_PH_RELIQUAT, ph_Reliquat.getlot());
        contentValues.put(Constantes.CLE_COL_SCANREFERENCE_PH_RELIQUAT, ph_Reliquat.getscanReference());
        contentValues.put(Constantes.CLE_COL_PEREMPTIONACTIVE_PH_RELIQUAT, ph_Reliquat.getperemptionActive());
        contentValues.put(Constantes.CLE_COL_COMMANDELIGNEID_PH_RELIQUAT, ph_Reliquat.getcommandeLigneID());
        contentValues.put(Constantes.CLE_COL_PU_FACTURE_PH_RELIQUAT, ph_Reliquat.getPu_facture());
        contentValues.put(Constantes.CLE_COL_REPRIS_PH_RELIQUAT, ph_Reliquat.getRepris());
        contentValues.put(Constantes.CLE_COL_QTEMOUVEMENT_PH_RELIQUAT, ph_Reliquat.getQteMouvement());
        contentValues.put(Constantes.CLE_COL_QTERELIQUAT_Y_PH_RELIQUAT, ph_Reliquat.getQteReliquat_Y());
        contentValues.put(Constantes.CLE_COL_PRODUITGRATUITS_PH_RELIQUAT, ph_Reliquat.getProduitGratuits());
        contentValues.put(Constantes.CLE_COL_DEVISE_PH_RELIQUAT, ph_Reliquat.getDevise());
        contentValues.put(Constantes.CLE_COL_DEPOTREFERENCE_PH_RELIQUAT, ph_Reliquat.getDepotReference());
        contentValues.put(Constantes.CLE_COL__SYS_DT_MAJ_PH_RELIQUAT, ph_Reliquat.get_SYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_HEURE_MAJ_PH_RELIQUAT, ph_Reliquat.get_SYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_USER_MAJ_PH_RELIQUAT, ph_Reliquat.get_SYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_SUIVIPARLOTACTIF_PH_RELIQUAT, ph_Reliquat.getSuiviParLotActif());
        contentValues.put(Constantes.CLE_COL_SYNCHROTIMESTAMP_PH_RELIQUAT, ph_Reliquat.getSynchroTimeStamp());
        contentValues.put(Constantes.CLE_COL_SYNCHROSTATUT_PH_RELIQUAT, ph_Reliquat.getSynchroStatut());
        contentValues.put(Constantes.CLE_COL_FINNESS_PH_RELIQUAT, ph_Reliquat.getFinness());
        contentValues.put(Constantes.CLE_COL_SCANVALUE_PH_RELIQUAT, ph_Reliquat.getScanValue());
        contentValues.put(Constantes.CLE_COL_PATIENTIPP_PH_RELIQUAT, ph_Reliquat.getPatientIPP());
        contentValues.put(Constantes.CLE_COL_PATIENTNOM_PH_RELIQUAT, ph_Reliquat.getPatientNom());
        contentValues.put(Constantes.CLE_COL_TECH_UID_PH_RELIQUAT, ph_Reliquat.gettech_UID());
        contentValues.put(Constantes.CLE_COL_ZONE_PH_RELIQUAT, ph_Reliquat.getZone());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_PH_RELIQUAT, ph_Reliquat.getEmplacement());
        contentValues.put(Constantes.CLE_COL_IPP_PH_RELIQUAT, ph_Reliquat.getIPP());
        contentValues.put(Constantes.CLE_COL_SUIVI_PAR_SERIE_ACTIF, ph_Reliquat.isSuiviParSerieActif());
        contentValues.put(Constantes.CLE_COL_SERIALISATION_RECEPTION, ph_Reliquat.isSerialiserReception());
        contentValues.put(Constantes.CLE_COL_SERIE, ph_Reliquat.getSerie());
        contentValues.put(Constantes.CLE_COL_BL_Numero, ph_Reliquat.getBL_Numero());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, ph_Reliquat.getPhiMR4UUID());

        return db.update(Constantes.TABLE_PH_RELIQUAT, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + ph_Reliquat.getPhiMR4UUID(), null);
    }

    public static PH_Reliquat getPH_ReliquatByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        PH_Reliquat phReliquat = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_RELIQUAT + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phReliquat = new PH_Reliquat(cursor);
        }
        cursor.close();
        cursor = null;
        return phReliquat;
    }

    public static PH_Reliquat getPH_ReliquatById(SQLiteDatabase db, int id) {
        PH_Reliquat phReliquat = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_RELIQUAT + " WHERE " + Constantes.CLE_COL_RELIQUAT_UID_PH_RELIQUAT + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phReliquat = new PH_Reliquat(cursor);
        }
        cursor.close();
        cursor = null;

        return phReliquat;
    }

    public static PH_Reliquat getPH_ReliquatByUnIdProduitetNumero(SQLiteDatabase db, Integer id_produit, String NumeroCommande) {
        PH_Reliquat phReliquat = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_RELIQUAT + " WHERE " + Constantes.CLE_COL_PRODUITID_PH_RELIQUAT + "=? AND "+Constantes.CLE_COL_COMMANDENUMERO_PH_RELIQUAT+"=?", new String[]{String.valueOf(id_produit), NumeroCommande});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phReliquat = new PH_Reliquat(cursor);
        }
        cursor.close();
        cursor = null;

        return phReliquat;
    }

    public static PH_Reliquat getPH_ReliquatByUnIdProduitetNumeroEtLotEtPeremptionEtZoneEtEmplacement(SQLiteDatabase db, Integer id_produit, String NumeroCommande, String numLot, String datePeremption, String zone, String emplacement) {
        PH_Reliquat phReliquat = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_RELIQUAT + " WHERE " + Constantes.CLE_COL_PRODUITID_PH_RELIQUAT + "=? AND "+Constantes.CLE_COL_COMMANDENUMERO_PH_RELIQUAT+"=? AND "+Constantes.CLE_COL_LOT_PH_RELIQUAT+"=? AND "+Constantes.CLE_COL_PEREMPTIONDATE_PH_RELIQUAT +"=? AND "+Constantes.CLE_COL_ZONE_PH_RELIQUAT+"=? AND "+Constantes.CLE_COL_EMPLACEMENT_PH_RELIQUAT+"=?" , new String[]{String.valueOf(id_produit), NumeroCommande, numLot, datePeremption, zone, emplacement});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phReliquat = new PH_Reliquat(cursor);
        }
        cursor.close();
        cursor = null;

        return phReliquat;
    }

    public static List<PH_Reliquat> getPH_ReliquatByCommandeNumero(SQLiteDatabase db, String num) {
        List<PH_Reliquat> phReliquatList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_RELIQUAT, null);

        while (cursor.moveToNext()) {
            PH_Reliquat phReliquat = new PH_Reliquat(cursor);

            if (phReliquat.getcommandeNumero().contentEquals(num)) {
                phReliquatList.add(phReliquat);
            }
        }
        cursor.close();
        cursor = null;
        return phReliquatList;
    }

    public static List<PH_Reliquat> getPH_ReliquatBaseByCommandeNumero(SQLiteDatabase db, String num) {
        List<PH_Reliquat> phReliquatList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_RELIQUAT, null);

        while (cursor.moveToNext()) {
            PH_Reliquat phReliquat = new PH_Reliquat(cursor);

            if (phReliquat.getcommandeNumero().contentEquals(num) && phReliquat.getReliquat_UID() > 0) {
                phReliquatList.add(phReliquat);
            }
        }
        cursor.close();
        cursor = null;
        return phReliquatList;
    }

    public static List<PH_Reliquat> getPH_ReliquatNegByCommandeNumero(SQLiteDatabase db, String num) {
        List<PH_Reliquat> phReliquatList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_RELIQUAT, null);

        while (cursor.moveToNext()) {
            PH_Reliquat phReliquat = new PH_Reliquat(cursor);

            if (phReliquat.getcommandeNumero().contentEquals(num) && phReliquat.getReliquat_UID() < 0) {
                phReliquatList.add(phReliquat);
            }
        }
        cursor.close();
        cursor = null;
        return phReliquatList;
    }

    public static List<PH_Reliquat> getPH_ReliquatNegByCommandeNumeroAndProduit(SQLiteDatabase db, String num, int produitId) {
        List<PH_Reliquat> phReliquatList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_RELIQUAT, null);

        while (cursor.moveToNext()) {
            PH_Reliquat phReliquat = new PH_Reliquat(cursor);

            if (phReliquat.getcommandeNumero().contentEquals(num) && phReliquat.getReliquat_UID() < 0 && phReliquat.getProduitID() == produitId) {
                phReliquatList.add(phReliquat);
            }
        }
        cursor.close();
        cursor = null;
        return phReliquatList;
    }

    public static List<PH_Reliquat> getPH_ReliquatByIPP(SQLiteDatabase db, int produitID, String IPP) {
        List<PH_Reliquat> phReliquat = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_RELIQUAT + " WHERE " + Constantes.CLE_COL_PRODUITID_PH_RELIQUAT + "=? AND " + Constantes.CLE_COL_IPP_PH_RELIQUAT + "=? AND " + Constantes.CLE_COL_RELIQUATENCOURS_PH_RELIQUAT + "=1", new String[]{String.valueOf(produitID), IPP});

        while (cursor.moveToNext()) {
            phReliquat.add(new PH_Reliquat(cursor));
        }
        cursor.close();
        cursor = null;

        return phReliquat;
    }

    public static void supprimerUnPhReliquat(SQLiteDatabase db, PH_Reliquat ph_reliquat) {
        db.delete(Constantes.TABLE_PH_RELIQUAT, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(ph_reliquat.getPhiMR4UUID())});
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PH_RELIQUAT = "PH_Reliquat";
        public static final String CLE_COL_RELIQUAT_UID_PH_RELIQUAT = "Reliquat_UID";
        public static final int NUM_COL_RELIQUAT_UID_PH_RELIQUAT = 1;
        public static final String TYPE_COL_RELIQUAT_UID_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_PRODUITID_PH_RELIQUAT = "ProduitID";
        public static final int NUM_COL_PRODUITID_PH_RELIQUAT = 2;
        public static final String TYPE_COL_PRODUITID_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_PRODUIT_REFERENCE_PH_RELIQUAT = "Produit_Reference";
        public static final int NUM_COL_PRODUIT_REFERENCE_PH_RELIQUAT = 3;
        public static final String TYPE_COL_PRODUIT_REFERENCE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_PU_COMMANDE_PH_RELIQUAT = "PU_commande";
        public static final int NUM_COL_PU_COMMANDE_PH_RELIQUAT = 4;
        public static final String TYPE_COL_PU_COMMANDE_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_DESIGNATIONCOURTE_PH_RELIQUAT = "designationCourte";
        public static final int NUM_COL_DESIGNATIONCOURTE_PH_RELIQUAT = 5;
        public static final String TYPE_COL_DESIGNATIONCOURTE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_UNITE_PH_RELIQUAT = "unite";
        public static final int NUM_COL_UNITE_PH_RELIQUAT = 6;
        public static final String TYPE_COL_UNITE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_FOURNISSEURNOM_PH_RELIQUAT = "fournisseurNom";
        public static final int NUM_COL_FOURNISSEURNOM_PH_RELIQUAT = 7;
        public static final String TYPE_COL_FOURNISSEURNOM_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_FOURNISSEURID_PH_RELIQUAT = "FournisseurID";
        public static final int NUM_COL_FOURNISSEURID_PH_RELIQUAT = 8;
        public static final String TYPE_COL_FOURNISSEURID_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_QTECOMMANDE_PH_RELIQUAT = "QteCommande";
        public static final int NUM_COL_QTECOMMANDE_PH_RELIQUAT = 9;
        public static final String TYPE_COL_QTECOMMANDE_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_QTELIVRAISON_PH_RELIQUAT = "QteLivraison";
        public static final int NUM_COL_QTELIVRAISON_PH_RELIQUAT = 10;
        public static final String TYPE_COL_QTELIVRAISON_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_QTERELIQUAT_X_PH_RELIQUAT = "QteReliquat_X";
        public static final int NUM_COL_QTERELIQUAT_X_PH_RELIQUAT = 11;
        public static final String TYPE_COL_QTERELIQUAT_X_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_RELIQUATENCOURS_PH_RELIQUAT = "reliquatEncours";
        public static final int NUM_COL_RELIQUATENCOURS_PH_RELIQUAT = 12;
        public static final String TYPE_COL_RELIQUATENCOURS_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_ENTREEDATE_PH_RELIQUAT = "entreeDate";
        public static final int NUM_COL_ENTREEDATE_PH_RELIQUAT = 13;
        public static final String TYPE_COL_ENTREEDATE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_SELECTION_PH_RELIQUAT = "selection";
        public static final int NUM_COL_SELECTION_PH_RELIQUAT = 14;
        public static final String TYPE_COL_SELECTION_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_COMMANDENUMERO_PH_RELIQUAT = "commandeNumero";
        public static final int NUM_COL_COMMANDENUMERO_PH_RELIQUAT = 15;
        public static final String TYPE_COL_COMMANDENUMERO_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_COMMANDEDATE_PH_RELIQUAT = "commandeDate";
        public static final int NUM_COL_COMMANDEDATE_PH_RELIQUAT = 16;
        public static final String TYPE_COL_COMMANDEDATE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_CONDITIONNEMENTACHAT_PH_RELIQUAT = "ConditionnementAchat";
        public static final int NUM_COL_CONDITIONNEMENTACHAT_PH_RELIQUAT = 17;
        public static final String TYPE_COL_CONDITIONNEMENTACHAT_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_CONDITIONNEMENTDISTRIBUTION_PH_RELIQUAT = "ConditionnementDistribution";
        public static final int NUM_COL_CONDITIONNEMENTDISTRIBUTION_PH_RELIQUAT = 18;
        public static final String TYPE_COL_CONDITIONNEMENTDISTRIBUTION_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_PEREMPTIONDATE_PH_RELIQUAT = "peremptionDate";
        public static final int NUM_COL_PEREMPTIONDATE_PH_RELIQUAT = 19;
        public static final String TYPE_COL_PEREMPTIONDATE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_LOT_PH_RELIQUAT = "lot";
        public static final int NUM_COL_LOT_PH_RELIQUAT = 20;
        public static final String TYPE_COL_LOT_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_SCANREFERENCE_PH_RELIQUAT = "scanReference";
        public static final int NUM_COL_SCANREFERENCE_PH_RELIQUAT = 21;
        public static final String TYPE_COL_SCANREFERENCE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_PEREMPTIONACTIVE_PH_RELIQUAT = "peremptionActive";
        public static final int NUM_COL_PEREMPTIONACTIVE_PH_RELIQUAT = 22;
        public static final String TYPE_COL_PEREMPTIONACTIVE_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_COMMANDELIGNEID_PH_RELIQUAT = "commandeLigneID";
        public static final int NUM_COL_COMMANDELIGNEID_PH_RELIQUAT = 23;
        public static final String TYPE_COL_COMMANDELIGNEID_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_PU_FACTURE_PH_RELIQUAT = "Pu_facture";
        public static final int NUM_COL_PU_FACTURE_PH_RELIQUAT = 24;
        public static final String TYPE_COL_PU_FACTURE_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_REPRIS_PH_RELIQUAT = "Repris";
        public static final int NUM_COL_REPRIS_PH_RELIQUAT = 25;
        public static final String TYPE_COL_REPRIS_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_QTEMOUVEMENT_PH_RELIQUAT = "QteMouvement";
        public static final int NUM_COL_QTEMOUVEMENT_PH_RELIQUAT = 26;
        public static final String TYPE_COL_QTEMOUVEMENT_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_QTERELIQUAT_Y_PH_RELIQUAT = "QteReliquat_Y";
        public static final int NUM_COL_QTERELIQUAT_Y_PH_RELIQUAT = 27;
        public static final String TYPE_COL_QTERELIQUAT_Y_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_PRODUITGRATUITS_PH_RELIQUAT = "ProduitGratuits";
        public static final int NUM_COL_PRODUITGRATUITS_PH_RELIQUAT = 28;
        public static final String TYPE_COL_PRODUITGRATUITS_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_DEVISE_PH_RELIQUAT = "Devise";
        public static final int NUM_COL_DEVISE_PH_RELIQUAT = 29;
        public static final String TYPE_COL_DEVISE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_DEPOTREFERENCE_PH_RELIQUAT = "DepotReference";
        public static final int NUM_COL_DEPOTREFERENCE_PH_RELIQUAT = 30;
        public static final String TYPE_COL_DEPOTREFERENCE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL__SYS_DT_MAJ_PH_RELIQUAT = "_SYS_DT_MAJ";
        public static final int NUM_COL__SYS_DT_MAJ_PH_RELIQUAT = 31;
        public static final String TYPE_COL__SYS_DT_MAJ_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL__SYS_HEURE_MAJ_PH_RELIQUAT = "_SYS_HEURE_MAJ";
        public static final int NUM_COL__SYS_HEURE_MAJ_PH_RELIQUAT = 32;
        public static final String TYPE_COL__SYS_HEURE_MAJ_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL__SYS_USER_MAJ_PH_RELIQUAT = "_SYS_USER_MAJ";
        public static final int NUM_COL__SYS_USER_MAJ_PH_RELIQUAT = 33;
        public static final String TYPE_COL__SYS_USER_MAJ_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_SUIVIPARLOTACTIF_PH_RELIQUAT = "SuiviParLotActif";
        public static final int NUM_COL_SUIVIPARLOTACTIF_PH_RELIQUAT = 34;
        public static final String TYPE_COL_SUIVIPARLOTACTIF_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_SYNCHROTIMESTAMP_PH_RELIQUAT = "SynchroTimeStamp";
        public static final int NUM_COL_SYNCHROTIMESTAMP_PH_RELIQUAT = 35;
        public static final String TYPE_COL_SYNCHROTIMESTAMP_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_SYNCHROSTATUT_PH_RELIQUAT = "SynchroStatut";
        public static final int NUM_COL_SYNCHROSTATUT_PH_RELIQUAT = 36;
        public static final String TYPE_COL_SYNCHROSTATUT_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_FINNESS_PH_RELIQUAT = "Finness";
        public static final int NUM_COL_FINNESS_PH_RELIQUAT = 37;
        public static final String TYPE_COL_FINNESS_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_SCANVALUE_PH_RELIQUAT = "ScanValue";
        public static final int NUM_COL_SCANVALUE_PH_RELIQUAT = 38;
        public static final String TYPE_COL_SCANVALUE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_PATIENTIPP_PH_RELIQUAT = "PatientIPP";
        public static final int NUM_COL_PATIENTIPP_PH_RELIQUAT = 39;
        public static final String TYPE_COL_PATIENTIPP_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_PATIENTNOM_PH_RELIQUAT = "PatientNom";
        public static final int NUM_COL_PATIENTNOM_PH_RELIQUAT = 40;
        public static final String TYPE_COL_PATIENTNOM_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_TECH_UID_PH_RELIQUAT = "tech_UID";
        public static final int NUM_COL_TECH_UID_PH_RELIQUAT = 41;
        public static final String TYPE_COL_TECH_UID_PH_RELIQUAT = "INTEGER";
        public static final String CLE_COL_ZONE_PH_RELIQUAT = "Zone";
        public static final int NUM_COL_ZONE_PH_RELIQUAT = 42;
        public static final String TYPE_COL_ZONE_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_EMPLACEMENT_PH_RELIQUAT = "Emplacement";
        public static final int NUM_COL_EMPLACEMENT_PH_RELIQUAT = 43;
        public static final String TYPE_COL_EMPLACEMENT_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_IPP_PH_RELIQUAT = "IPP";
        public static final int NUM_COL_IPP_PH_RELIQUAT = 44;
        public static final String TYPE_COL_IPP_PH_RELIQUAT = "TEXT";
        public static final String CLE_COL_SUIVI_PAR_SERIE_ACTIF = "SuiviParSerieActif";
        public static final int NUM_COL_SUIVI_PAR_SERIE_ACTIF = 45;
        public static final String TYPE_COL_SUIVI_PAR_SERIE_ACTIF = "INTEGER";
        public static final String CLE_COL_SERIALISATION_RECEPTION = "SerialisationReception";
        public static final int NUM_COL_SERIALISATION_RECEPTION = 46;
        public static final String TYPE_COL_SERIALISATION_RECEPTION = "INTEGER";
        public static final String CLE_COL_SERIE = "Serie";
        public static final int NUM_COL_SERIE = 47;
        public static final String TYPE_COL_SERIE = "TEXT";

        public static final String CLE_COL_BL_Numero = "BL_Numero";
        public static final int NUM_COL_BL_Numero = 48;
        public static final String TYPE_COL_BL_Numero = "TEXT";

        public static final String CREATION_TABLE_PH_RELIQUAT = " CREATE TABLE       " + Constantes.TABLE_PH_RELIQUAT
                + "(" +
                DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL_RELIQUAT_UID_PH_RELIQUAT + " " + Constantes.TYPE_COL_RELIQUAT_UID_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_PRODUITID_PH_RELIQUAT + "   " + Constantes.TYPE_COL_PRODUITID_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_PRODUIT_REFERENCE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_PRODUIT_REFERENCE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_PU_COMMANDE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_PU_COMMANDE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_DESIGNATIONCOURTE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_DESIGNATIONCOURTE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_UNITE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_UNITE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_FOURNISSEURNOM_PH_RELIQUAT + "   " + Constantes.TYPE_COL_FOURNISSEURNOM_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_FOURNISSEURID_PH_RELIQUAT + "   " + Constantes.TYPE_COL_FOURNISSEURID_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_QTECOMMANDE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_QTECOMMANDE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_QTELIVRAISON_PH_RELIQUAT + "   " + Constantes.TYPE_COL_QTELIVRAISON_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_QTERELIQUAT_X_PH_RELIQUAT + "   " + Constantes.TYPE_COL_QTERELIQUAT_X_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_RELIQUATENCOURS_PH_RELIQUAT + "   " + Constantes.TYPE_COL_RELIQUATENCOURS_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_ENTREEDATE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_ENTREEDATE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_SELECTION_PH_RELIQUAT + "   " + Constantes.TYPE_COL_SELECTION_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_COMMANDENUMERO_PH_RELIQUAT + "   " + Constantes.TYPE_COL_COMMANDENUMERO_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_COMMANDEDATE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_COMMANDEDATE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_CONDITIONNEMENTACHAT_PH_RELIQUAT + "   " + Constantes.TYPE_COL_CONDITIONNEMENTACHAT_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_CONDITIONNEMENTDISTRIBUTION_PH_RELIQUAT + "   " + Constantes.TYPE_COL_CONDITIONNEMENTDISTRIBUTION_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_PEREMPTIONDATE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_PEREMPTIONDATE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_LOT_PH_RELIQUAT + "   " + Constantes.TYPE_COL_LOT_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_SCANREFERENCE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_SCANREFERENCE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_PEREMPTIONACTIVE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_PEREMPTIONACTIVE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_COMMANDELIGNEID_PH_RELIQUAT + "   " + Constantes.TYPE_COL_COMMANDELIGNEID_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_PU_FACTURE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_PU_FACTURE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_REPRIS_PH_RELIQUAT + "   " + Constantes.TYPE_COL_REPRIS_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_QTEMOUVEMENT_PH_RELIQUAT + "   " + Constantes.TYPE_COL_QTEMOUVEMENT_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_QTERELIQUAT_Y_PH_RELIQUAT + "   " + Constantes.TYPE_COL_QTERELIQUAT_Y_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_PRODUITGRATUITS_PH_RELIQUAT + "   " + Constantes.TYPE_COL_PRODUITGRATUITS_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_DEVISE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_DEVISE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_DEPOTREFERENCE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_DEPOTREFERENCE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL__SYS_DT_MAJ_PH_RELIQUAT + "   " + Constantes.TYPE_COL__SYS_DT_MAJ_PH_RELIQUAT + " , "
                + Constantes.CLE_COL__SYS_HEURE_MAJ_PH_RELIQUAT + "   " + Constantes.TYPE_COL__SYS_HEURE_MAJ_PH_RELIQUAT + " , "
                + Constantes.CLE_COL__SYS_USER_MAJ_PH_RELIQUAT + "   " + Constantes.TYPE_COL__SYS_USER_MAJ_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_SUIVIPARLOTACTIF_PH_RELIQUAT + "   " + Constantes.TYPE_COL_SUIVIPARLOTACTIF_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_SYNCHROTIMESTAMP_PH_RELIQUAT + "   " + Constantes.TYPE_COL_SYNCHROTIMESTAMP_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_SYNCHROSTATUT_PH_RELIQUAT + "   " + Constantes.TYPE_COL_SYNCHROSTATUT_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_FINNESS_PH_RELIQUAT + "   " + Constantes.TYPE_COL_FINNESS_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_SCANVALUE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_SCANVALUE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_PATIENTIPP_PH_RELIQUAT + "   " + Constantes.TYPE_COL_PATIENTIPP_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_PATIENTNOM_PH_RELIQUAT + "   " + Constantes.TYPE_COL_PATIENTNOM_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_TECH_UID_PH_RELIQUAT + "   " + Constantes.TYPE_COL_TECH_UID_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_ZONE_PH_RELIQUAT + "   " + Constantes.TYPE_COL_ZONE_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_EMPLACEMENT_PH_RELIQUAT + "   " + Constantes.TYPE_COL_EMPLACEMENT_PH_RELIQUAT + " , "
                + Constantes.CLE_COL_IPP_PH_RELIQUAT + "   " + Constantes.TYPE_COL_IPP_PH_RELIQUAT+ " , "
                + Constantes.CLE_COL_SUIVI_PAR_SERIE_ACTIF + "   " + Constantes.TYPE_COL_SUIVI_PAR_SERIE_ACTIF+ " , "
                + Constantes.CLE_COL_SERIALISATION_RECEPTION + "   " + Constantes.TYPE_COL_SERIALISATION_RECEPTION+ " , "
                + Constantes.CLE_COL_SERIE + "   " + Constantes.TYPE_COL_SERIE+" , "
                + Constantes.CLE_COL_BL_Numero + "   " + Constantes.TYPE_COL_BL_Numero
                + " ); ";

    }

}
