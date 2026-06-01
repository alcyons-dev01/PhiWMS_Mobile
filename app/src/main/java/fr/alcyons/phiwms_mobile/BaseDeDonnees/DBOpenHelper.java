package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import static fr.alcyons.phiwms_mobile.BaseDeDonnees.ImprimanteEtiquetteOpenHelper.Constantes.TABLE_IMPRIMANTE_ETIQUETTE;
import static fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper.Constantes.TABLE_INVENTAIRE;
import static fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP;
import static fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitPlaceOpenHelper.Constantes.TABLE_PRODUIT_PLACE;
import static fr.alcyons.phiwms_mobile.BaseDeDonnees.StockUtilisesOpenHelper.Constantes.TABLE_STOCK_UTILISE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.Classes.Produit_Identification;

public class DBOpenHelper extends SQLiteOpenHelper implements Serializable {

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderBasesDeDonnees(SQLiteDatabase db) {
        db.delete(ServiceOpenHelper.Constantes.TABLE_SERVICE, null, null);
        db.delete(UtilisateurOpenHelper.Constantes.TABLE_UTILISATEUR, null, null);
        db.delete(PerimetreFonctionnelOpenHelper.Constantes.TABLE_PERIMETRE_FONCTIONNEL, null, null);
        db.delete(ProduitOpenHelper.Constantes.TABLE_PRODUIT, null, null);
        db.delete(ElementASynchroniserOpenHelper.Constantes.TABLE_ELEMENT_A_SYNCHRONISER, null, null);
        db.delete(DepotOpenHelper.Constantes.TABLE_DEPOT, null, null);
        db.delete(ZoneOpenHelper.Constantes.TABLE_DEPOT_ZONE, null, null);
        db.delete(EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, null, null);
        db.delete(CommandeOpenHelper.Constantes.TABLE_COMMANDE, null, null);
        db.delete(Commande_LigneOpenHelper.Constantes.TABLE_COMMANDE_LIGNE, null, null);
        db.delete(FournisseurOpenHelper.Constantes.TABLE_FOURNISSEUR, null, null);
        db.delete(Inventaire_LigneOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE, null, null);
        db.delete(TABLE_INVENTAIRE_LIGNE_TEMP, null, null);
        db.delete(TABLE_INVENTAIRE, null, null);
        db.delete(PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, null, null);
        db.delete(PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, null, null);
        db.delete(RetourOpenHelper.Constantes.TABLE_RETOUR, null, null);
        db.delete(Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, null, null);
        db.delete(StockOpenHelper.Constantes.TABLE_STOCK, null, null);
        db.delete(Stock_LotOpenHelper.Constantes.TABLE_STOCK_LOT, null, null);
        db.delete(Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, null, null);
        db.delete(SYS_Document_TypeOpenHelper.Constantes.TABLE_SYS_DOCUMENT_TYPE, null, null);
        db.delete(SYS_Mvt_Stock_TypeOpenHelper.Constantes.TABLE_SYS_MVT_STOCK_TYPE, null, null);
        db.delete(NotificationOpenHelper.Constantes.TABLE_NOTIFICATION, null, null);
        db.delete(Detail_DotOpenHelper.Constantes.TABLE_DETAIL_DOT, null, null);
        db.delete(DotationOpenHelper.Constantes.TABLE_DOTATION, null, null);
        db.delete(PH_PatientOpenHelper.Constantes.TABLE_PH_PATIENT, null, null);
        db.delete(PH_ReassortOpenHelper.Constantes.TABLE_PH_REASSORT, null, null);
        db.delete(PH_Reassort_LigneOpenHelper.Constantes.TABLE_PH_REASSORT_LIGNE, null, null);
        db.delete(PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, null, null);
        db.delete(PH_RetourMotifOpenHelper.Constantes.TABLE_PH_RETOURMOTIF, null, null);
        db.delete(FrequencesOpenHelper.Constantes.TABLE_FREQUENCES, null, null);
        db.delete(ParametreUtilisateurOpenHelper.Constantes.TABLE_PARAMETRES_UTILISATEUR, null, null);
        db.delete(PH_UtiliserOpenHelper.Constantes.TABLE_PH_UTILISER, null, null);
        db.delete(PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, null, null);
        db.delete(Parametres_SerialisationOpenHelper.Constantes.TABLE_PARAMETRES_SERIALISATION, null, null);
        db.delete(ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, null, null);
        db.delete(ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, null, null);
        db.delete(SYS_User_RulesOpenHelper.Constantes.TABLE_SYS_USER_RULES, null, null);
        db.delete(TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, null, null);
        db.delete(EVENTOpenHelper.Constantes.TABLE_EVENT, null, null);
        db.delete(PH_Demande_MotifOpenHelper.Constantes.TABLE_DEMANDE_MOTIF, null, null);
        db.delete(TABLE_IMPRIMANTE_ETIQUETTE, null, null);
        db.delete(TABLE_STOCK_UTILISE, null, null);
        db.delete(TABLE_PRODUIT_PLACE, null, null);
        db.delete(Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creation des tables
        db.execSQL(UtilisateurOpenHelper.Constantes.CREATION_TABLE_UTILISATEUR);
        db.execSQL(ServiceOpenHelper.Constantes.CREATION_TABLE_SERVICE);
        db.execSQL(PerimetreFonctionnelOpenHelper.Constantes.CREATION_TABLE_PERIMETRE_FONCTIONNEL);
        db.execSQL(ProduitOpenHelper.Constantes.CREATION_TABLE_PRODUIT);
        db.execSQL(ElementASynchroniserOpenHelper.Constantes.CREATION_TABLE_ELEMENT_A_SYNCHRONISER);
        db.execSQL(DepotOpenHelper.Constantes.CREATION_TABLE_DEPOT);
        db.execSQL(ZoneOpenHelper.Constantes.CREATION_TABLE_DEPOT_ZONE);
        db.execSQL(EmplacementOpenHelper.Constantes.CREATION_TABLE_DEPOT_EMPLACEMENT);
        db.execSQL(CommandeOpenHelper.Constantes.CREATION_TABLE_COMMANDE);
        db.execSQL(Commande_LigneOpenHelper.Constantes.CREATION_TABLE_COMMANDE_LIGNE);
        db.execSQL(FournisseurOpenHelper.Constantes.CREATION_TABLE_FOURNISSEUR);
        db.execSQL(Inventaire_LigneOpenHelper.Constantes.CREATION_TABLE_INVENTAIRE_LIGNE);
        db.execSQL(InventaireOpenHelper.Constantes.CREATION_TABLE_INVENTAIRE);
        db.execSQL(Inventaire_Ligne_TempOpenHelper.Constantes.CREATION_TABLE_INVENTAIRE_LIGNE_TEMP);
        db.execSQL(PH_PreparationOpenHelper.Constantes.CREATION_TABLE_PH_PREPARATION);
        db.execSQL(PH_Preparation_LigneOpenHelper.Constantes.CREATION_TABLE_PH_PREPARATION_LIGNE);
        db.execSQL(RetourOpenHelper.Constantes.CREATION_TABLE_RETOUR);
        db.execSQL(Retour_LigneOpenHelper.Constantes.CREATION_TABLE_RETOUR_LIGNE);
        db.execSQL(StockOpenHelper.Constantes.CREATION_TABLE_STOCK);
        db.execSQL(Stock_LotOpenHelper.Constantes.CREATION_TABLE_STOCK_LOT);
        db.execSQL(Stock_Lot_EmplacementLightOpenHelper.Constantes.CREATION_TABLE_STOCK_LOT_EMPLACEMENT);
        db.execSQL(SYS_Document_TypeOpenHelper.Constantes.CREATION_TABLE_SYS_DOCUMENT_TYPE);
        db.execSQL(SYS_Mvt_Stock_TypeOpenHelper.Constantes.CREATION_TABLE_SYS_MVT_STOCK_TYPE);
        db.execSQL(NotificationOpenHelper.Constantes.CREATION_TABLE_NOTIFICATION);
        db.execSQL(ParametresServeurOpenHelper.Constantes.CREATION_TABLE_PARAMETRES_SERVEUR);
        db.execSQL(Detail_DotOpenHelper.Constantes.CREATION_TABLE_DETAIL_DOT);
        db.execSQL(DotationOpenHelper.Constantes.CREATION_TABLE_DOTATION);
        db.execSQL(PH_PatientOpenHelper.Constantes.CREATION_TABLE_PH_PATIENT);
        db.execSQL(PH_ReassortOpenHelper.Constantes.CREATION_TABLE_PH_REASSORT);
        db.execSQL(PH_Reassort_LigneOpenHelper.Constantes.CREATION_TABLE_PH_REASSORT_LIGNE);
        db.execSQL(PH_ReliquatOpenHelper.Constantes.CREATION_TABLE_PH_RELIQUAT);
        db.execSQL(PH_RetourMotifOpenHelper.Constantes.CREATION_TABLE_PH_RETOURMOTIF);
        db.execSQL(FrequencesOpenHelper.Constantes.CREATION_TABLE_FREQUENCES);
        db.execSQL(ParametreUtilisateurOpenHelper.Constantes.CREATION_TABLE_PARAMETRES_UTILISATEUR);
        db.execSQL(PH_UtiliserOpenHelper.Constantes.CREATION_TABLE_PH_UTILISER);
        db.execSQL(PH_SerialisationOpenHelper.Constantes.CREATION_TABLE_PH_SERIALISATION);
        db.execSQL(Parametres_SerialisationOpenHelper.Constantes.CREATION_TABLE_PARAMETRES_SERIALISATION);
        db.execSQL(ActionUtilisateurOpenHelper.Constantes.CREATION_TABLE_ACTION_UTILISATEUR);
        db.execSQL(ActionUtilisateur_LigneOpenHelper.Constantes.CREATION_TABLE_ACTION_UTILISATEUR_LIGNE);
        db.execSQL(SYS_User_RulesOpenHelper.Constantes.CREATION_TABLE_SYS_USER_RULES);
        db.execSQL(TableTraceOpenHelper.Constantes.CREATION_TABLE_TABLE_TRACE);
        db.execSQL(EVENTOpenHelper.Constantes.CREATION_TABLE_EVENT);
        db.execSQL(PH_Demande_MotifOpenHelper.Constantes.CREATION_TABLE_DEMANDE_MOTIF);
        db.execSQL(ImprimanteEtiquetteOpenHelper.Constantes.CREATION_TABLE_IMPRIMANTE_ETIQUETTE);
        db.execSQL(StockUtilisesOpenHelper.Constantes.CREATION_TABLE_STOCK_UTILISE);
        db.execSQL(ProduitPlaceOpenHelper.Constantes.CREATION_TABLE_PRODUIT_PLACE);
        db.execSQL(Produit_IdentificationOpenHelper.Constantes.CREATION_TABLE_PRODUIT_IDENTIFICATION);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DBOpenHelper", "Mise à jour de la version " + oldVersion + " vers la version " + newVersion + ", les anciennes données seront détruites");

        Cursor serviceActiviteMobileExisteCursor = db.rawQuery("SELECT * FROM " + ServiceOpenHelper.Constantes.TABLE_SERVICE, null);
        int serviceActiviteMobileColumn = serviceActiviteMobileExisteCursor.getColumnIndex("activiteMobile");
        if (serviceActiviteMobileColumn < 0) {
            db.execSQL("ALTER TABLE " + ServiceOpenHelper.Constantes.TABLE_SERVICE + " ADD COLUMN activiteMobile TEXT");
        }

        Cursor ImprimanteEtiquetteExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_IMPRIMANTE_ETIQUETTE + "'", null);
        if (ImprimanteEtiquetteExisteCursor.getCount() == 0) {
            db.execSQL(ImprimanteEtiquetteOpenHelper.Constantes.CREATION_TABLE_IMPRIMANTE_ETIQUETTE);
        }

        Cursor StockUtiliseExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_STOCK_UTILISE + "'", null);
        if (StockUtiliseExisteCursor.getCount() == 0) {
            db.execSQL(StockUtilisesOpenHelper.Constantes.CREATION_TABLE_STOCK_UTILISE);
        }

        Cursor stockUtiliserIdStockCursor = db.rawQuery("SELECT * FROM " + TABLE_STOCK_UTILISE, null);
        int stockUtiliserIdStockColumn = stockUtiliserIdStockCursor.getColumnIndex("idStock");
        if (stockUtiliserIdStockColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_STOCK_UTILISE + " ADD COLUMN idStock INTEGER");
        }

        Cursor stockUtiliserEtablissementIdStockCursor = db.rawQuery("SELECT * FROM " + TABLE_STOCK_UTILISE, null);
        int stockUtiliserEtablissementIdStockColumn = stockUtiliserEtablissementIdStockCursor.getColumnIndex("Etablissement_ID");
        if (stockUtiliserEtablissementIdStockColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_STOCK_UTILISE + " ADD COLUMN Etablissement_ID INTEGER");
        }

        Cursor inventaireOuvertureDateCursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTAIRE, null);
        int inventaireOuvertureDateColumn = inventaireOuvertureDateCursor.getColumnIndex("ouvertureDate");
        if (inventaireOuvertureDateColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_INVENTAIRE + " ADD COLUMN ouvertureDate TEXT");
        }

        Cursor inventaireClotureDateCursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTAIRE, null);
        int inventaireClotureDateColumn = inventaireClotureDateCursor.getColumnIndex("clotureDate");
        if (inventaireClotureDateColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_INVENTAIRE + " ADD COLUMN clotureDate TEXT");
        }

        Cursor inventaireDateCursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTAIRE_LIGNE_TEMP, null);
        int inventaireDateColumn = inventaireDateCursor.getColumnIndex("inventaireDate");
        if (inventaireDateColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_INVENTAIRE_LIGNE_TEMP + " ADD COLUMN inventaireDate TEXT");
        }

        Cursor inventaireLigneTempSynchroniserCursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTAIRE_LIGNE_TEMP, null);
        int inventaireLigneTempSynchroniserColumn = inventaireLigneTempSynchroniserCursor.getColumnIndex("synchroniser");
        if (inventaireLigneTempSynchroniserColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_INVENTAIRE_LIGNE_TEMP + " ADD COLUMN synchroniser INTEGER");
        }

        Cursor ProduitPlaceExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_PRODUIT_PLACE + "'", null);
        if (ProduitPlaceExisteCursor.getCount() == 0) {
            db.execSQL(ProduitPlaceOpenHelper.Constantes.CREATION_TABLE_PRODUIT_PLACE);
        }

        Cursor inventaireLigneTempEtablissementUIDCursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTAIRE_LIGNE_TEMP, null);
        int inventaireLigneTempEtablissementUIDColumn = inventaireLigneTempEtablissementUIDCursor.getColumnIndex("Etablissement_UID");
        if (inventaireLigneTempEtablissementUIDColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_INVENTAIRE_LIGNE_TEMP + " ADD COLUMN Etablissement_UID INTEGER");
        }

        Cursor ProduitIdentificationExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE + "'", null);
        if (ProduitIdentificationExisteCursor.getCount() == 0) {
            db.execSQL(Produit_IdentificationOpenHelper.Constantes.CREATION_TABLE_PRODUIT_IDENTIFICATION);
        }
    }

    public SQLiteDatabase
    openDB() {
        // Cette fonction tente d'ouvrir la BD en mode écrire (read and write) si c'est impossible elle l'ouvre en mode lecture
        try {
            return this.getWritableDatabase();
        } catch (SQLiteException exception) {
            return this.getReadableDatabase();
        }
    }

    public static class Constantes implements BaseColumns {

        // nom de la BDD
        public static final String NOM_BDD = "PhiR4.db";

        // Version de la BDD
        public static final int DATABASE_VERSION = 1;

        public static final String CLE_COL_phiwms_mobileUUID = "phiwms_mobileUUID";
        public static final int NUM_COL_phiwms_mobileUUID = 0;
        public static final String TYPE_COL_phiwms_mobileUUID = "INTEGER";

    }

    public static class Urls {
        // url des requetes
        public static final String uriRequeteUtilisateur = "utilisateurs/connexion";
        public static final String uriRequeteServices = "services/";
        public static final String uriRequetePlanHabilitation = "plan_habilitations/uid/";
        public static final String uriRequeteProduits = "ph_produits/";
        public static final String uriRequeteDepots = "ph_depots/";
        public static final String uriRequeteDepotsEmplacements = "depot_emplacements/";
        public static final String uriRequeteDepotsZones = "depot_zones/";
        public static final String uriRequeteInventaires = "inventaires/";
        public static final String uriRequeteInventaireGeneral = "inventaires/general";
        public static final String uriRequeteInventaire_Lignes = "inventaire_lignes/";
        public static final String uriRequetePh_Preparation_Lignes = "ph_preparation_lignes/";
        public static final String uriRequeteCommandes = "ph_commandes/";
        public static final String uriRequeteCommande_Lignes = "ph_commande_lignes/";
        public static final String uriRequetePH_Preparations = "ph_preparations/";
        public static final String uriRequeteRetour_Lignes = "ph_retour_lignes/";
        public static final String uriRequeteRetours = "ph_retours/";
        public static final String uriRequeteStocks = "ph_stocks/";
        public static final String uriRequeteStock_Lot_Emplacements = "ph_stock_lot_emplacements/";
        public static final String uriRequeteRRO_Lignes = "rro_lignes/";
        public static final String uriRequeteSYS_Document_Types = "sys_document_types/";
        public static final String uriRequeteSYS_Mvt_Stock_Types = "sys_mvt_stock_types/";
        public static final String uriRequetePH_Mvt_Stocks = "ph_mvt_stocks/";
        public static final String uriRequetePhoto_Stock_Depots = "photo_stock_depots/";
        public static final String uriRequetePhoto_Stock_Etablissements = "photo_stock_etablissements/";
        public static final String uriRequeteFacture_Lignes = "facture_lignes/";
        public static final String uriRequeteFactures = "factures/";
        public static final String uriRequeteHisto_Mvt_Stocks = "histo_mvt_stocks/";
        public static final String uriRequeteStock_Lots = "stock_lots/";
        public static final String uriRequetePreparation_Lignes = "preparation_lignes/";
        public static final String uriRequetePreparations = "preparations/";
        public static final String uriRequeteFournisseurs = "fournisseurs/";
        public static final String uriRequeteInventaire_Ligne_Temps = "inventaire_ligne_temps/";
        public static final String uriRequeteDestruction = "ph_retours/destruction";
        public static final String uriRequeteRetourFournisseur = "ph_retours/retour_fournisseur";
        public static final String uriRequeteRetourPUI = "ph_retours/retour_pui";
        public static final String uriRequeteQuarantaine = "ph_retours/quarantaine";
        public static final String uriRequeteControleRetours = "ph_retours/controle_des_retours";
        public static final String uriRequeteVerrouPharmacie = "ph_preparations/verrou_pharmacie";
        public static final String uriRequeteServiceStock = "ph_stocks";
        public static final String uriRequeteServiceLivraison = "ph_preparations/livraison";
        public static final String uriRequeteServiceDepotLivraison = "ph_preparations/Depotlivraison";
        public static final String uriRequeteServiceLivraisonByDepot = "ph_preparations/livraisonByDepot";
        public static final String uriRequeteServiceNotification = "notifications";
        public static final String uriRequetePreparationPAD = "ph_preparations/preparationPAD";
        public static final String uriRequetePreparationPUF = "ph_preparations/preparationPUF";
        public static final String uriRequetePreparationDetail = "ph_preparations/preparation/";
        public static final String uriRequeteDotationUF = "dotations/";
        public static final String uriRequetePH_Reassort = "ph_reassorts/";
        public static final String uriRequetePH_Reassort_Ligne = "ph_reassort_lignes/";
        public static final String uriRequeteProtocoles_Patient = "protocoles_patients/";
        public static final String uriRequeteEvent = "events/";
        public static final String uriRequeteIndicateur = "indicateur/";
        public static final String uriRequeteRetourMotif = "ph_retour_motifs/";
        public static final String uriRequetePH_Reliquat = "ph_reliquats/";
        public static final String uriRequetedetail_dots = "detail_dots/";
        public static final String uriRequetePh_Patient = "ph_patients/";
        public static final String uriRequeteFrequence = "frequences/";
        public static final String uriRequetePhUtiliser = "ph_utilisers/";
        public static final String uriRequeteParametreSerialisation = "parametres_serialisation/";
        public static final String uriRequetePHSerialisation = "ph_serialisation/";
        public static final String uriRequeteSurveillanceReference = "surveillanceReference/";
        public static final String uriRequeteActionUtilisateur = "actionUtilisateur/";
        public static final String uriRequeteActionUtilisateurLigne = "actionUtilisateurLigne/";
        public static final String uriRequeteSysUserRules = "SYS_User_Rules/";
        public static final String uriRequeteTableTrace = "TableTrace/";
        public static final String uriRequetePHLotLigne = "PH_Lot_Ligne/";
        public static final String uriRequeteVerrouPharmacieInterne = "ph_preparations/delivrance";
        public static final String uriRequetePleinVideCourant = "ph_preparations/pleinvide";
        public static final String uriRequeteDotationGlobaleCourant = "ph_preparations/dotationglobale";
        public static final String uriRequeteDemandeReassortCourant = "ph_preparations/phreassort";
        public static final String uriRequeteDemandeMotif = "ph_demande_motif/";
        public static final String uriZebraImprimer = "zebrawebservice/imprimer";
        public static final String uriImprimanteEtiquette = "imprimanteEtiquette/";
        public static final String uriStockUtilises = "stock_utilises/";
        public static final String uriProduitPlace = "produitplace/";
    }

    public static class ActionsEAS {
        public static final String MAJ = "MAJ";
        public static final String AJOUT = "Ajout";
        public static final String SUPPR = "suppr";
    }
}