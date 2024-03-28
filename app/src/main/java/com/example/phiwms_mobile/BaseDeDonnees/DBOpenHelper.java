package com.example.phiwms_mobile.BaseDeDonnees;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.Serializable;

import static com.example.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper.Constantes.CREATION_TABLE_ACTION_UTILISATEUR;
import static com.example.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR;
import static com.example.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper.Constantes.CREATION_TABLE_ACTION_UTILISATEUR_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper.Constantes.CREATION_TABLE_COMMANDE;
import static com.example.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper.Constantes.TABLE_COMMANDE;
import static com.example.phiwms_mobile.BaseDeDonnees.Commande_LigneOpenHelper.Constantes.CREATION_TABLE_COMMANDE_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.Commande_LigneOpenHelper.Constantes.TABLE_COMMANDE_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.Composants_patientOpenHelper.Constantes.CREATION_TABLE_COMPOSANTS_PATIENT;
import static com.example.phiwms_mobile.BaseDeDonnees.Composants_patientOpenHelper.Constantes.TABLE_COMPOSANTS_PATIENT;
import static com.example.phiwms_mobile.BaseDeDonnees.DepotOpenHelper.Constantes.CREATION_TABLE_DEPOT;
import static com.example.phiwms_mobile.BaseDeDonnees.DepotOpenHelper.Constantes.TABLE_DEPOT;
import static com.example.phiwms_mobile.BaseDeDonnees.Detail_DotOpenHelper.Constantes.CREATION_TABLE_DETAIL_DOT;
import static com.example.phiwms_mobile.BaseDeDonnees.Detail_DotOpenHelper.Constantes.TABLE_DETAIL_DOT;
import static com.example.phiwms_mobile.BaseDeDonnees.DotationOpenHelper.Constantes.CREATION_TABLE_DOTATION;
import static com.example.phiwms_mobile.BaseDeDonnees.DotationOpenHelper.Constantes.TABLE_DOTATION;
import static com.example.phiwms_mobile.BaseDeDonnees.Dotation_PatientOpenHelper.Constantes.CREATION_TABLE_DOTATION_PATIENT;
import static com.example.phiwms_mobile.BaseDeDonnees.Dotation_PatientOpenHelper.Constantes.TABLE_DOTATION_PATIENT;
import static com.example.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper.Constantes.CREATION_TABLE_EVENT;
import static com.example.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper.Constantes.TABLE_EVENT;
import static com.example.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper.Constantes.CREATION_TABLE_ELEMENT_A_SYNCHRONISER;
import static com.example.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper.Constantes.TABLE_ELEMENT_A_SYNCHRONISER;
import static com.example.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper.Constantes.CREATION_TABLE_DEPOT_EMPLACEMENT;
import static com.example.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT;
import static com.example.phiwms_mobile.BaseDeDonnees.FactureOpenHelper.Constantes.CREATION_TABLE_FACTURE;
import static com.example.phiwms_mobile.BaseDeDonnees.FactureOpenHelper.Constantes.TABLE_FACTURE;
import static com.example.phiwms_mobile.BaseDeDonnees.Facture_LigneOpenHelper.Constantes.CREATION_TABLE_FACTURE_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.Facture_LigneOpenHelper.Constantes.TABLE_FACTURE_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.FournisseurOpenHelper.Constantes.CREATION_TABLE_FOURNISSEUR;
import static com.example.phiwms_mobile.BaseDeDonnees.FournisseurOpenHelper.Constantes.TABLE_FOURNISSEUR;
import static com.example.phiwms_mobile.BaseDeDonnees.FrequencesOpenHelper.Constantes.CREATION_TABLE_FREQUENCES;
import static com.example.phiwms_mobile.BaseDeDonnees.FrequencesOpenHelper.Constantes.TABLE_FREQUENCES;
import static com.example.phiwms_mobile.BaseDeDonnees.Histo_Mvt_StockOpenHelper.Constantes.CREATION_TABLE_HISTO_MVT_STOCK;
import static com.example.phiwms_mobile.BaseDeDonnees.Histo_Mvt_StockOpenHelper.Constantes.TABLE_HISTO_MVT_STOCK;
import static com.example.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper.Constantes.CREATION_TABLE_INVENTAIRE;
import static com.example.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper.Constantes.TABLE_INVENTAIRE;
import static com.example.phiwms_mobile.BaseDeDonnees.Inventaire_LigneOpenHelper.Constantes.CREATION_TABLE_INVENTAIRE_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.Inventaire_LigneOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper.Constantes.CREATION_TABLE_INVENTAIRE_LIGNE_TEMP;
import static com.example.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP;
import static com.example.phiwms_mobile.BaseDeDonnees.MVT_DepotsOpenHelper.Constantes.CREATION_TABLE_MVT_DEPOTS;
import static com.example.phiwms_mobile.BaseDeDonnees.MVT_DepotsOpenHelper.Constantes.TABLE_MVT_DEPOTS;
import static com.example.phiwms_mobile.BaseDeDonnees.NotificationOpenHelper.Constantes.CREATION_TABLE_NOTIFICATION;
import static com.example.phiwms_mobile.BaseDeDonnees.NotificationOpenHelper.Constantes.TABLE_NOTIFICATION;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_Lot_LigneOpenHelper.Constantes.CREATION_TABLE_PH_LOT_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_Lot_LigneOpenHelper.Constantes.TABLE_PH_LOT_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_Mvt_StockOpenHelper.Constantes.CREATION_TABLE_PH_MVT_STOCK;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_Mvt_StockOpenHelper.Constantes.TABLE_PH_MVT_STOCK;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_PatientOpenHelper.Constantes.CREATION_TABLE_PH_PATIENT;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_PatientOpenHelper.Constantes.TABLE_PH_PATIENT;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper.Constantes.CREATION_TABLE_PH_PREPARATION;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper.Constantes.CREATION_TABLE_PH_PREPARATION_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_ReassortOpenHelper.Constantes.CREATION_TABLE_PH_REASSORT;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_ReassortOpenHelper.Constantes.TABLE_PH_REASSORT;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_Reassort_LigneOpenHelper.Constantes.CREATION_TABLE_PH_REASSORT_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_Reassort_LigneOpenHelper.Constantes.TABLE_PH_REASSORT_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper.Constantes.CREATION_TABLE_PH_RELIQUAT;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_RetourMotifOpenHelper.Constantes.CREATION_TABLE_PH_RETOURMOTIF;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_RetourMotifOpenHelper.Constantes.TABLE_PH_RETOURMOTIF;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper.Constantes.CREATION_TABLE_PH_SERIALISATION;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_UtiliserOpenHelper.Constantes.CREATION_TABLE_PH_UTILISER;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_UtiliserOpenHelper.Constantes.TABLE_PH_UTILISER;
import static com.example.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper.Constantes.CREATION_TABLE_PARAMETRES_UTILISATEUR;
import static com.example.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper.Constantes.TABLE_PARAMETRES_UTILISATEUR;
import static com.example.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper.Constantes.CREATION_TABLE_PARAMETRES_SERVEUR;
import static com.example.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper.Constantes.TABLE_PARAMETRES_SERVEUR;
import static com.example.phiwms_mobile.BaseDeDonnees.Parametres_SerialisationOpenHelper.Constantes.CREATION_TABLE_PARAMETRES_SERIALISATION;
import static com.example.phiwms_mobile.BaseDeDonnees.PerimetreFonctionnelOpenHelper.Constantes.CREATION_TABLE_PERIMETRE_FONCTIONNEL;
import static com.example.phiwms_mobile.BaseDeDonnees.PerimetreFonctionnelOpenHelper.Constantes.TABLE_PERIMETRE_FONCTIONNEL;
import static com.example.phiwms_mobile.BaseDeDonnees.Photo_Stock_DepotOpenHelper.Constantes.CREATION_TABLE_PHOTO_STOCK_DEPOT;
import static com.example.phiwms_mobile.BaseDeDonnees.Photo_Stock_DepotOpenHelper.Constantes.TABLE_PHOTO_STOCK_DEPOT;
import static com.example.phiwms_mobile.BaseDeDonnees.Photo_Stock_EtablissementOpenHelper.Constantes.CREATION_TABLE_PHOTO_STOCK_ETABLISSEMENT;
import static com.example.phiwms_mobile.BaseDeDonnees.Photo_Stock_EtablissementOpenHelper.Constantes.TABLE_PHOTO_STOCK_ETABLISSEMENT;
import static com.example.phiwms_mobile.BaseDeDonnees.PreparationOpenHelper.Constantes.CREATION_TABLE_PREPARATION;
import static com.example.phiwms_mobile.BaseDeDonnees.PreparationOpenHelper.Constantes.TABLE_PREPARATION;
import static com.example.phiwms_mobile.BaseDeDonnees.Preparation_LigneOpenHelper.Constantes.CREATION_TABLE_PREPARATION_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.Preparation_LigneOpenHelper.Constantes.TABLE_PREPARATION_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.Prescription_patientOpenHelper.Constantes.CREATION_TABLE_PRESCRIPTION_PATIENT;
import static com.example.phiwms_mobile.BaseDeDonnees.Prescription_patientOpenHelper.Constantes.TABLE_PRESCRIPTION_PATIENT;
import static com.example.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper.Constantes.CREATION_TABLE_PRODUIT;
import static com.example.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper.Constantes.TABLE_PRODUIT;
import static com.example.phiwms_mobile.BaseDeDonnees.Protocoles_PatientsOpenHelper.Constantes.CREATION_TABLE_PROTOCOLES_PATIENTS;
import static com.example.phiwms_mobile.BaseDeDonnees.Protocoles_PatientsOpenHelper.Constantes.TABLE_PROTOCOLES_PATIENTS;
import static com.example.phiwms_mobile.BaseDeDonnees.RRO_LigneOpenHelper.Constantes.CREATION_TABLE_RRO_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.RRO_LigneOpenHelper.Constantes.TABLE_RRO_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.RetourOpenHelper.Constantes.CREATION_TABLE_RETOUR;
import static com.example.phiwms_mobile.BaseDeDonnees.RetourOpenHelper.Constantes.TABLE_RETOUR;
import static com.example.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper.Constantes.CREATION_TABLE_RETOUR_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE;
import static com.example.phiwms_mobile.BaseDeDonnees.SYS_Document_TypeOpenHelper.Constantes.CREATION_TABLE_SYS_DOCUMENT_TYPE;
import static com.example.phiwms_mobile.BaseDeDonnees.SYS_Document_TypeOpenHelper.Constantes.TABLE_SYS_DOCUMENT_TYPE;
import static com.example.phiwms_mobile.BaseDeDonnees.SYS_Mvt_Stock_TypeOpenHelper.Constantes.CREATION_TABLE_SYS_MVT_STOCK_TYPE;
import static com.example.phiwms_mobile.BaseDeDonnees.SYS_Mvt_Stock_TypeOpenHelper.Constantes.TABLE_SYS_MVT_STOCK_TYPE;
import static com.example.phiwms_mobile.BaseDeDonnees.SYS_User_RulesOpenHelper.Constantes.CREATION_TABLE_SYS_USER_RULES;
import static com.example.phiwms_mobile.BaseDeDonnees.SYS_User_RulesOpenHelper.Constantes.TABLE_SYS_USER_RULES;
import static com.example.phiwms_mobile.BaseDeDonnees.ServiceOpenHelper.Constantes.CREATION_TABLE_SERVICE;
import static com.example.phiwms_mobile.BaseDeDonnees.ServiceOpenHelper.Constantes.TABLE_SERVICE;
import static com.example.phiwms_mobile.BaseDeDonnees.StockOpenHelper.Constantes.CREATION_TABLE_STOCK;
import static com.example.phiwms_mobile.BaseDeDonnees.StockOpenHelper.Constantes.TABLE_STOCK;
import static com.example.phiwms_mobile.BaseDeDonnees.Stock_LotOpenHelper.Constantes.CREATION_TABLE_STOCK_LOT;
import static com.example.phiwms_mobile.BaseDeDonnees.Stock_LotOpenHelper.Constantes.TABLE_STOCK_LOT;
import static com.example.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper.Constantes.CREATION_TABLE_STOCK_LOT_EMPLACEMENT;
import static com.example.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT;
import static com.example.phiwms_mobile.BaseDeDonnees.SurveillanceReferenceOpenHelper.Constantes.CREATION_TABLE_SURVEILLANCEREFERENCE;
import static com.example.phiwms_mobile.BaseDeDonnees.TableTraceOpenHelper.Constantes.CREATION_TABLE_TABLE_TRACE;
import static com.example.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper.Constantes.CREATION_TABLE_UTILISATEUR;
import static com.example.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper.Constantes.TABLE_UTILISATEUR;
import static com.example.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper.Constantes.CREATION_TABLE_DEPOT_ZONE;
import static com.example.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper.Constantes.TABLE_DEPOT_ZONE;
import static com.example.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION;
import static com.example.phiwms_mobile.BaseDeDonnees.SurveillanceReferenceOpenHelper.Constantes.TABLE_SURVEILLANCEREFERENCE;
import static com.example.phiwms_mobile.BaseDeDonnees.Parametres_SerialisationOpenHelper.Constantes.TABLE_PARAMETRES_SERIALISATION;
import static com.example.phiwms_mobile.BaseDeDonnees.TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE;


/**
 * Created by quentinlanusse on 12/04/2017.
 */


public class DBOpenHelper extends SQLiteOpenHelper implements Serializable {

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderBasesDeDonnees(SQLiteDatabase db) {
        db.delete(TABLE_SERVICE, null, null);
        db.delete(TABLE_UTILISATEUR, null, null);
        db.delete(TABLE_PERIMETRE_FONCTIONNEL, null, null);
        db.delete(TABLE_PRODUIT, null, null);
        db.delete(TABLE_ELEMENT_A_SYNCHRONISER, null, null);
        db.delete(TABLE_DEPOT, null, null);
        db.delete(TABLE_DEPOT_ZONE, null, null);
        db.delete(TABLE_DEPOT_EMPLACEMENT, null, null);
        db.delete(TABLE_COMMANDE, null, null);
        db.delete(TABLE_COMMANDE_LIGNE, null, null);
        db.delete(TABLE_FOURNISSEUR, null, null);
        db.delete(TABLE_INVENTAIRE_LIGNE, null, null);
        db.delete(TABLE_INVENTAIRE_LIGNE_TEMP, null, null);
        db.delete(TABLE_INVENTAIRE, null, null);
        db.delete(TABLE_PH_PREPARATION, null, null);
        db.delete(TABLE_PH_PREPARATION_LIGNE, null, null);
        db.delete(TABLE_PREPARATION, null, null);
        db.delete(TABLE_PREPARATION_LIGNE, null, null);
        db.delete(TABLE_RETOUR, null, null);
        db.delete(TABLE_RETOUR_LIGNE, null, null);
        db.delete(TABLE_STOCK, null, null);
        db.delete(TABLE_STOCK_LOT, null, null);
        db.delete(TABLE_FACTURE, null, null);
        db.delete(TABLE_FACTURE_LIGNE, null, null);
        db.delete(TABLE_HISTO_MVT_STOCK, null, null);
        db.delete(TABLE_PH_MVT_STOCK, null, null);
        db.delete(TABLE_PHOTO_STOCK_DEPOT, null, null);
        db.delete(TABLE_PHOTO_STOCK_ETABLISSEMENT, null, null);
        db.delete(TABLE_STOCK_LOT_EMPLACEMENT, null, null);
        db.delete(TABLE_RRO_LIGNE, null, null);
        db.delete(TABLE_SYS_DOCUMENT_TYPE, null, null);
        db.delete(TABLE_SYS_MVT_STOCK_TYPE, null, null);
        db.delete(TABLE_NOTIFICATION, null, null);
        db.delete(TABLE_COMPOSANTS_PATIENT, null, null);
        db.delete(TABLE_DETAIL_DOT, null, null);
        db.delete(TABLE_DOTATION_PATIENT, null, null);
        db.delete(TABLE_DOTATION, null, null);
        db.delete(TABLE_PH_PATIENT, null, null);
        db.delete(TABLE_PH_REASSORT, null, null);
        db.delete(TABLE_PH_REASSORT_LIGNE, null, null);
        db.delete(TABLE_PROTOCOLES_PATIENTS, null, null);
        db.delete(TABLE_PH_RELIQUAT, null, null);
        db.delete(TABLE_PH_RETOURMOTIF, null, null);
        db.delete(TABLE_FREQUENCES, null, null);
        db.delete(TABLE_MVT_DEPOTS, null, null);
        db.delete(TABLE_PRESCRIPTION_PATIENT, null, null);
        db.delete(TABLE_PARAMETRES_UTILISATEUR, null, null);
        db.delete(TABLE_PH_UTILISER, null, null);
        db.delete(TABLE_PH_SERIALISATION, null, null);
        db.delete(TABLE_SURVEILLANCEREFERENCE, null, null);
        db.delete(TABLE_PARAMETRES_SERIALISATION, null, null);
        db.delete(TABLE_ACTION_UTILISATEUR, null, null);
        db.delete(TABLE_ACTION_UTILISATEUR_LIGNE, null, null);
        db.delete(TABLE_SYS_USER_RULES, null, null);
        db.delete(TABLE_TABLE_TRACE, null, null);
        db.delete(TABLE_EVENT, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creation des tables
        db.execSQL(CREATION_TABLE_UTILISATEUR);
        db.execSQL(CREATION_TABLE_SERVICE);
        db.execSQL(CREATION_TABLE_PERIMETRE_FONCTIONNEL);
        db.execSQL(CREATION_TABLE_PRODUIT);
        db.execSQL(CREATION_TABLE_ELEMENT_A_SYNCHRONISER);
        db.execSQL(CREATION_TABLE_DEPOT);
        db.execSQL(CREATION_TABLE_DEPOT_ZONE);
        db.execSQL(CREATION_TABLE_DEPOT_EMPLACEMENT);
        db.execSQL(CREATION_TABLE_COMMANDE);
        db.execSQL(CREATION_TABLE_COMMANDE_LIGNE);
        db.execSQL(CREATION_TABLE_FOURNISSEUR);
        db.execSQL(CREATION_TABLE_INVENTAIRE_LIGNE);
        db.execSQL(CREATION_TABLE_INVENTAIRE);
        db.execSQL(CREATION_TABLE_INVENTAIRE_LIGNE_TEMP);
        db.execSQL(CREATION_TABLE_PH_PREPARATION);
        db.execSQL(CREATION_TABLE_PH_PREPARATION_LIGNE);
        db.execSQL(CREATION_TABLE_PREPARATION);
        db.execSQL(CREATION_TABLE_PREPARATION_LIGNE);
        db.execSQL(CREATION_TABLE_RETOUR);
        db.execSQL(CREATION_TABLE_RETOUR_LIGNE);
        db.execSQL(CREATION_TABLE_STOCK);
        db.execSQL(CREATION_TABLE_STOCK_LOT);
        db.execSQL(CREATION_TABLE_FACTURE);
        db.execSQL(CREATION_TABLE_FACTURE_LIGNE);
        db.execSQL(CREATION_TABLE_HISTO_MVT_STOCK);
        db.execSQL(CREATION_TABLE_PH_MVT_STOCK);
        db.execSQL(CREATION_TABLE_PHOTO_STOCK_DEPOT);
        db.execSQL(CREATION_TABLE_PHOTO_STOCK_ETABLISSEMENT);
        db.execSQL(CREATION_TABLE_STOCK_LOT_EMPLACEMENT);
        db.execSQL(CREATION_TABLE_RRO_LIGNE);
        db.execSQL(CREATION_TABLE_SYS_DOCUMENT_TYPE);
        db.execSQL(CREATION_TABLE_SYS_MVT_STOCK_TYPE);
        db.execSQL(CREATION_TABLE_NOTIFICATION);
        db.execSQL(CREATION_TABLE_PARAMETRES_SERVEUR);
        db.execSQL(CREATION_TABLE_COMPOSANTS_PATIENT);
        db.execSQL(CREATION_TABLE_DETAIL_DOT);
        db.execSQL(CREATION_TABLE_DOTATION_PATIENT);
        db.execSQL(CREATION_TABLE_DOTATION);
        db.execSQL(CREATION_TABLE_PH_PATIENT);
        db.execSQL(CREATION_TABLE_PH_REASSORT);
        db.execSQL(CREATION_TABLE_PH_REASSORT_LIGNE);
        db.execSQL(CREATION_TABLE_PROTOCOLES_PATIENTS);
        db.execSQL(CREATION_TABLE_PH_RELIQUAT);
        db.execSQL(CREATION_TABLE_PH_RETOURMOTIF);
        db.execSQL(CREATION_TABLE_FREQUENCES);
        db.execSQL(CREATION_TABLE_MVT_DEPOTS);
        db.execSQL(CREATION_TABLE_PRESCRIPTION_PATIENT);
        db.execSQL(CREATION_TABLE_PARAMETRES_UTILISATEUR);
        db.execSQL(CREATION_TABLE_PH_UTILISER);
        db.execSQL(CREATION_TABLE_PH_SERIALISATION);
        db.execSQL(CREATION_TABLE_SURVEILLANCEREFERENCE);
        db.execSQL(CREATION_TABLE_PARAMETRES_SERIALISATION);
        db.execSQL(CREATION_TABLE_ACTION_UTILISATEUR);
        db.execSQL(CREATION_TABLE_ACTION_UTILISATEUR_LIGNE);
        db.execSQL(CREATION_TABLE_SYS_USER_RULES);
        db.execSQL(CREATION_TABLE_TABLE_TRACE);
        db.execSQL(CREATION_TABLE_EVENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DBOpenHelper", "Mise à jour de la version " + oldVersion + " vers la version " + newVersion + ", les anciennes données seront détruites");

        int parametreColumnIndex = 0;
        int phReliquatColumnIndex = 0;
        int notificationColumnIndex = 0;
        int depotEmplacementColumnIndex = 0;
        int phPreparationLigneColumnIndex = 0;
        int produitColumnIndex = 0;
        int detailDotColumnIndex = 0;
        int dotationPleinVideIndex = 0;
        int dotationProtocoleUIDIndex = 0;
        int phreliquatIPPIndex = 0;
        int utilisateurDepotUIDIndex = 0;
        int dotationPatientIndex = 0;
        int composantPatientIndex = 0;

        // TABLE_PARAMETRES_SERVEUR
        Cursor parametreCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_SERVEUR, null);
        parametreColumnIndex = parametreCursor.getColumnIndex("etablissementNumero");
        if (parametreColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN etablissementNumero INTEGER");
        }
        parametreColumnIndex = parametreCursor.getColumnIndex("etablissementLogoNom");
        if (parametreColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN etablissementLogoNom TEXT");
        }

        parametreColumnIndex = parametreCursor.getColumnIndex("PlanDeCueillette_Actif");
        if (parametreColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN PlanDeCueillette_Actif INTEGER");
        }

        parametreColumnIndex = parametreCursor.getColumnIndex("Liv_indirecte_egal_Cond_achat");
        if (parametreColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN Liv_indirecte_egal_Cond_achat INTEGER");
        }

        parametreColumnIndex = parametreCursor.getColumnIndex("Reliquats_pour_prevision");
        if (parametreColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN Reliquats_pour_prevision INTEGER");
        }

        // TABLE_PH_RELIQUAT
        Cursor phReliquatExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_PH_RELIQUAT + "'", null);
        if (phReliquatExisteCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_PH_RELIQUAT);
        }

        Cursor phReliquatCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_RELIQUAT, null);
        phReliquatColumnIndex = phReliquatCursor.getColumnIndex("Zone");
        if (phReliquatColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_RELIQUAT + " ADD COLUMN Zone TEXT");
        }
        phReliquatColumnIndex = phReliquatCursor.getColumnIndex("Emplacement");
        if (phReliquatColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_RELIQUAT + " ADD COLUMN Emplacement TEXT");
        }

        Cursor ph_reliquat_ipp = db.rawQuery("SELECT * FROM " + TABLE_PH_RELIQUAT, null);
        phreliquatIPPIndex = ph_reliquat_ipp.getColumnIndex("IPP");
        if (phreliquatIPPIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_RELIQUAT + " ADD COLUMN IPP TEXT");
        }

        Cursor ph_reliquat_bl_numero = db.rawQuery("SELECT * FROM " + TABLE_PH_RELIQUAT, null);
        phreliquatIPPIndex = ph_reliquat_bl_numero.getColumnIndex("BL_Numero");
        if (phreliquatIPPIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_RELIQUAT + " ADD COLUMN BL_Numero TEXT");
        }

        // TABLE_PH_RETOURMOTIF
        Cursor phRetourMotifExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_PH_RETOURMOTIF + "'", null);
        if (phRetourMotifExisteCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_PH_RETOURMOTIF);
        }
        // TABLE_NOTIFICATION
        Cursor notificationCursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION, null);
        notificationColumnIndex = notificationCursor.getColumnIndex("channel");
        if (notificationColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_NOTIFICATION + " ADD COLUMN channel TEXT");
        }
        // TABLE_DEPOT_EMPLACEMENT
        Cursor depotEmplacementCursor = db.rawQuery("SELECT * FROM " + TABLE_DEPOT_EMPLACEMENT, null);
        depotEmplacementColumnIndex = depotEmplacementCursor.getColumnIndex("Code_GLN");
        if (depotEmplacementColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_DEPOT_EMPLACEMENT + " ADD COLUMN Code_GLN TEXT");
        }

        // TABLE_PH_PREPARATION_LIGNE
        Cursor phPreparationLigneCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION_LIGNE, null);
        phPreparationLigneColumnIndex = phPreparationLigneCursor.getColumnIndex("Accepter");
        if (phPreparationLigneColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION_LIGNE + " ADD COLUMN Accepter BOOLEAN");
        }

        phPreparationLigneCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION_LIGNE, null);
        phPreparationLigneColumnIndex = phPreparationLigneCursor.getColumnIndex("_UID_4D");
        if (phPreparationLigneColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION_LIGNE + " ADD COLUMN _UID_4D INTEGER");
        }

        // TABLE_PH_PREPARATION_LIGNE
        Cursor produitCursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUIT, null);
        produitColumnIndex = produitCursor.getColumnIndex("CodeInconnue");
        if (produitColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PRODUIT + " ADD COLUMN CodeInconnue TEXT");
        }

        // TABLE_DETAIL_DOT
        Cursor detailDotCursor = db.rawQuery("SELECT * FROM " + TABLE_DETAIL_DOT, null);
        detailDotColumnIndex = detailDotCursor.getColumnIndex("PleinVide_Adressage");
        if (detailDotColumnIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_DETAIL_DOT + " ADD COLUMN PleinVide_Adressage TEXT");
        }

        // TABLE_DOTATION
        Cursor dotationPleinVide = db.rawQuery("SELECT * FROM " + TABLE_DOTATION, null);
        dotationPleinVideIndex = dotationPleinVide.getColumnIndex("PLEINVIDE");
        if (dotationPleinVideIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_DOTATION + " ADD COLUMN PLEINVIDE BOOLEAN");
        }

        Cursor dotationProtocoleUID = db.rawQuery("SELECT * FROM " + TABLE_DOTATION, null);
        dotationProtocoleUIDIndex = dotationProtocoleUID.getColumnIndex("protocole_UID");
        if (dotationProtocoleUIDIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_DOTATION + " ADD COLUMN protocole_UID INTEGER");
        }

        //TABLE UTILISATEUR
        Cursor utilisateurDepotUID = db.rawQuery("SELECT * FROM " + TABLE_UTILISATEUR, null);
        utilisateurDepotUIDIndex = utilisateurDepotUID.getColumnIndex("depot_UID");
        if (utilisateurDepotUIDIndex < 0) {
            db.execSQL("ALTER TABLE " + TABLE_UTILISATEUR + " ADD COLUMN depot_UID INTEGER");
        }
        // TABLE_FREQUENCE
        Cursor frequenceExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_FREQUENCES + "'", null);
        if (frequenceExisteCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_FREQUENCES);
        }

        // TABLE_MVT_DEPOTS
        Cursor MVTDEpotExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_MVT_DEPOTS + "'", null);
        if (MVTDEpotExisteCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_MVT_DEPOTS);
        }

        // TABLE_PRESCRIPTION_PATIENT
        Cursor prescriptionExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_PRESCRIPTION_PATIENT + "'", null);
        if (prescriptionExisteCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_PRESCRIPTION_PATIENT);
        }

        //TABLE PARAMETRE UTILISATEUR
        Cursor parametreUtilisateurExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_PARAMETRES_UTILISATEUR + "'", null);
        if (parametreUtilisateurExisteCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_PARAMETRES_UTILISATEUR);
        }

        //DotationPatient
        Cursor dotationPatient = db.rawQuery("SELECT * FROM " + TABLE_DOTATION_PATIENT, null);
        dotationPatientIndex = dotationPatient.getColumnIndex("Qté");
        if (dotationPatientIndex == 1) {
            db.execSQL("ALTER TABLE " + TABLE_DOTATION_PATIENT + " ALTER COLUMN Qté DECIMAL");
        }

        Cursor dotationPatientQTECOmmande = db.rawQuery("SELECT * FROM " + TABLE_DOTATION_PATIENT, null);
        dotationPatientIndex = dotationPatientQTECOmmande.getColumnIndex("Qte_Commande");
        if (dotationPatientIndex == 1) {
            db.execSQL("ALTER TABLE " + TABLE_DOTATION_PATIENT + " ALTER COLUMN Qte_Commande DECIMAL");
        }

        //TABLE COMPOSANT PATIENT
        Cursor ComposantPatient = db.rawQuery("SELECT * FROM " + TABLE_COMPOSANTS_PATIENT, null);
        composantPatientIndex = ComposantPatient.getColumnIndex("Qté");
        if (composantPatientIndex == 1) {
            db.execSQL("ALTER TABLE " + TABLE_COMPOSANTS_PATIENT + " ALTER COLUMN Qté DECIMAL");
        }

        // TABLE_PH_UTILISER
        Cursor PhUtiliserExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_PH_UTILISER + "'", null);
        if (PhUtiliserExisteCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_PH_UTILISER);
        }

        // Table PH_Serialisation
        Cursor phserialisation = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_PH_SERIALISATION + "'", null);
        if (phserialisation.getCount() == 0) {
            db.execSQL(CREATION_TABLE_PH_SERIALISATION);
        }

        // Table Surveillance
        Cursor surveillance = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_SURVEILLANCEREFERENCE + "'", null);
        if (surveillance.getCount() == 0) {
            db.execSQL(CREATION_TABLE_SURVEILLANCEREFERENCE);
        }

        // Table parametre serialisation
        Cursor parametre_serialisation = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_PARAMETRES_SERIALISATION + "'", null);
        if (parametre_serialisation.getCount() == 0) {
            db.execSQL(CREATION_TABLE_PARAMETRES_SERIALISATION);
        }

        Cursor gestionColonneProduitSuivi = db.rawQuery("SELECT * FROM " + TABLE_PRODUIT, null);
        int parametreColumnSuiviSerialisation = gestionColonneProduitSuivi.getColumnIndex("Suivi_Serialisation");
        if (parametreColumnSuiviSerialisation < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PRODUIT + " ADD COLUMN Suivi_Serialisation INTEGER");
        }

        Cursor gestionColonneProduit = db.rawQuery("SELECT * FROM " + TABLE_PRODUIT, null);
        int parametreColumnSerialiserReception = gestionColonneProduit.getColumnIndex("Serialiser_Reception_Delivrance");
        if (parametreColumnSerialiserReception < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PRODUIT + " ADD COLUMN Serialiser_Reception_Delivrance INTEGER");
        }

        Cursor ajoutSerieStock = db.rawQuery("SELECT * FROM " + TABLE_STOCK_LOT_EMPLACEMENT, null);
        int parametreColumnStockLotEmp = ajoutSerieStock.getColumnIndex("Serie");
        if (parametreColumnStockLotEmp < 0) {
            db.execSQL("ALTER TABLE " + TABLE_STOCK_LOT_EMPLACEMENT + " ADD COLUMN Serie TEXT");
        }

        Cursor ajoutSuiviSerieReliquat= db.rawQuery("SELECT * FROM " + TABLE_PH_RELIQUAT, null);
        int parametreColumnSuiviSerieReqliquat = ajoutSuiviSerieReliquat.getColumnIndex("SuiviParSerieActif");
        if (parametreColumnSuiviSerieReqliquat < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_RELIQUAT + " ADD COLUMN SuiviParSerieActif INTEGER");
        }

        Cursor ajoutSerialiserReceptionReliquat= db.rawQuery("SELECT * FROM " + TABLE_PH_RELIQUAT, null);
        int parametreColumnSerialiserReceptionReqliquat = ajoutSerialiserReceptionReliquat.getColumnIndex("SerialisationReception");
        if (parametreColumnSerialiserReceptionReqliquat < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_RELIQUAT + " ADD COLUMN SerialisationReception INTEGER");
        }

        Cursor ajoutSerieReliquat= db.rawQuery("SELECT * FROM " + TABLE_PH_RELIQUAT, null);
        int parametreColumnSerieReqliquat = ajoutSerieReliquat.getColumnIndex("Serie");
        if (parametreColumnSerieReqliquat < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_RELIQUAT + " ADD COLUMN Serie TEXT");
        }

        //Verification des champs de sérialisation pour la table PH_Preparation_Ligne
        Cursor ajoutSuiviSeriePreparationLigne= db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION_LIGNE, null);
        int parametreSuiviSeriePreparationLigne = ajoutSuiviSeriePreparationLigne.getColumnIndex("Suivi_Par_Serie");
        if (parametreSuiviSeriePreparationLigne < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION_LIGNE + " ADD COLUMN Suivi_Par_Serie INTEGER");
        }

        Cursor ajouterSerialiserReceptionPreparationLigne = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION_LIGNE, null);
        int parametreSerialiserReceptionPreparationLigne = ajouterSerialiserReceptionPreparationLigne.getColumnIndex("Serialiser_Reception");
        if (parametreSerialiserReceptionPreparationLigne < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION_LIGNE + " ADD COLUMN Serialiser_Reception INTEGER");
        }

        Cursor ajoutNumeroSeriePreparationLigne= db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION_LIGNE, null);
        int parametreNumeroSeriePreparationLigne = ajoutNumeroSeriePreparationLigne.getColumnIndex("SerieNumero");
        if (parametreNumeroSeriePreparationLigne < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION_LIGNE + " ADD COLUMN SerieNumero TEXT");
        }

        //Verification des champs de sérialisation pour la table PH_Retour_Ligne
        Cursor ajoutSerieRetournerRetourLigne = db.rawQuery("SELECT * FROM " + TABLE_RETOUR_LIGNE, null);
        int parametreSerieRetournerRetourLigne = ajoutSerieRetournerRetourLigne.getColumnIndex("Serie_Retourner");
        if (parametreSerieRetournerRetourLigne < 0) {
            db.execSQL("ALTER TABLE " + TABLE_RETOUR_LIGNE + " ADD COLUMN Serie_Retourner TEXT");
        }

        // TABLE ACTION UTILISATEUR
        Cursor actionUtilisateurCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_ACTION_UTILISATEUR + "'", null);
        if (actionUtilisateurCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_ACTION_UTILISATEUR);
        }

        actionUtilisateurCursor = db.rawQuery("SELECT * FROM " + TABLE_ACTION_UTILISATEUR, null);
        int cheminPhotoActionUtilisateur = actionUtilisateurCursor.getColumnIndex("CheminPhoto");
        if (cheminPhotoActionUtilisateur < 0) {
            db.execSQL("ALTER TABLE " + TABLE_ACTION_UTILISATEUR + " ADD COLUMN CheminPhoto TEXT");
        }

        //TABLE ACTION_UTILISATEUR_LIGNE
        Cursor actionUtilisateurLigneCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_ACTION_UTILISATEUR_LIGNE + "'", null);
        if (actionUtilisateurLigneCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_ACTION_UTILISATEUR_LIGNE);
        }

        // TABLE_SYS_USER_RULES
        Cursor SysUserRulesExisteCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_SYS_USER_RULES + "'", null);
        if (SysUserRulesExisteCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_SYS_USER_RULES);
        }

        //AJOUT CAISSE NB TABLE PREPARATION
        Cursor caisseNBExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION, null);
        int caisseNBColumn = caisseNBExisteCursor.getColumnIndex("CaisseNB");
        if (caisseNBColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION + " ADD COLUMN CaisseNB INTEGER");
        }

        //AJOUT INFORMATION CHAUFFEUR POUR LES RETOURS
        Cursor nomChauffeurExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_RETOUR, null);
        int nomChauffeurColumn = nomChauffeurExisteCursor.getColumnIndex("Nom_Chauffeur");
        if (nomChauffeurColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_RETOUR + " ADD COLUMN Nom_Chauffeur TEXT");
        }

        Cursor prenomChauffeurExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_RETOUR, null);
        int prenomChauffeurColumn = prenomChauffeurExisteCursor.getColumnIndex("Prenom_Chauffeur");
        if (prenomChauffeurColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_RETOUR + " ADD COLUMN Prenom_Chauffeur TEXT");
        }

        Cursor transporteurExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_RETOUR, null);
        int transporteurColumn = transporteurExisteCursor.getColumnIndex("Transporteur");
        if (transporteurColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_RETOUR + " ADD COLUMN Transporteur TEXT");
        }

        //AJOUT LIVREUR USER ID TABLE PREPARATION
        Cursor livreur_userIDExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION, null);
        int livreur_userIDColumn = livreur_userIDExisteCursor.getColumnIndex("livreur_userID");
        if (livreur_userIDColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION + " ADD COLUMN livreur_userID INTEGER");
        }

        //AJOUT LIVREUR USER ID TABLE PREPARATION
        Cursor livreur_signatureExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION, null);
        int livreur_signatureColumn = livreur_signatureExisteCursor.getColumnIndex("Signature_Livraison");
        if (livreur_signatureColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION + " ADD COLUMN Signature_Livraison TEXT");
        }

        //AJOUT SIGNATURE CHAUFFEUR TABLE RETOUR
        Cursor signatureChauffeurExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_RETOUR, null);
        int signatureChauffeurColumn = signatureChauffeurExisteCursor.getColumnIndex("Signature_Chauffeur");
        if (signatureChauffeurColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_RETOUR + " ADD COLUMN Signature_Chauffeur TEXT");
        }

        //AJOUT DU PARAMETRE DE CONNEXION DIRECTE DANS LES PARAMETRES UTILISATEUR
        Cursor connexionDirecteExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_UTILISATEUR, null);
        int connexionDirecteColumn = connexionDirecteExisteCursor.getColumnIndex("ConnexionDirecte");
        if (connexionDirecteColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_UTILISATEUR + " ADD COLUMN ConnexionDirecte INTEGER");
        }

        //AJOUT DU PARAMETRE NOM PRODUIT DANS LA TABLE DES ACTIONS LIGNES
        Cursor nomProduitActionLigneExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_ACTION_UTILISATEUR_LIGNE, null);
        int nomProduitActionLigneColumn = nomProduitActionLigneExisteCursor.getColumnIndex("Nom_Produit");
        if (nomProduitActionLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_ACTION_UTILISATEUR_LIGNE + " ADD COLUMN Nom_Produit TEXT");
        }

        //AJOUT DE LA TABLE TRACE
        Cursor tableTraceCursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_TABLE_TRACE + "'", null);
        if (tableTraceCursor.getCount() == 0) {
            db.execSQL(CREATION_TABLE_TABLE_TRACE);
        }

        //AJOUT DU PARAMETRE MODE TRACE DANS LES PARAMETRES UTILISATEUR
        Cursor modeTraceExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_UTILISATEUR, null);
        int modeTraceLigneColumn = modeTraceExisteCursor.getColumnIndex("ModeTrace");
        if (modeTraceLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_UTILISATEUR + " ADD COLUMN ModeTrace INTEGER");
        }

        //AJOUT DU PARAMETRE MODULE TRANSPORT DANS LES PARAMETRES SERVEUR
        Cursor moduleTransportExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_SERVEUR, null);
        int moduleTransportLigneColumn = moduleTransportExisteCursor.getColumnIndex("Module_Transport");
        if (moduleTransportLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN Module_Transport INTEGER");
        }

        //AJOUT DU NOMBRE DE CONTENEUR POUR UNE PH PREPARATION
        Cursor ConteneurNBExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION, null);
        int ConteneurNBColumn = ConteneurNBExisteCursor.getColumnIndex("Conteneur_NB");
        if (ConteneurNBColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION + " ADD COLUMN Conteneur_NB INTEGER");
        }

        //AJOUT DU NUMERO SCELLE POUR UNE PH PREPARATION
        Cursor NumeroScelleExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION, null);
        int NumeroScelleColumn = NumeroScelleExisteCursor.getColumnIndex("numero_scelle");
        if (NumeroScelleColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION + " ADD COLUMN numero_scelle TEXT");
        }

        //AJOUT DU PARAMETRE DE CHOIX DE TRI DANS LES PARAMETRES UTILISATEUR
        Cursor choixTriExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_UTILISATEUR, null);
        int choixTrDirecteColumn = choixTriExisteCursor.getColumnIndex("TriPreparation");
        if (choixTrDirecteColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_UTILISATEUR + " ADD COLUMN TriPreparation TEXT");
        }

        //AJOUT DU PARAMETRE DE CHOIX DE TRI DANS LES PARAMETRES UTILISATEUR
        Cursor choixTriReceptionExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_UTILISATEUR, null);
        int choixTriReceptionColumn = choixTriReceptionExisteCursor.getColumnIndex("TriReception");
        if (choixTriReceptionColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_UTILISATEUR + " ADD COLUMN TriReception TEXT");
        }

        //AJOUT DU PARAMETRE DE CHOIX DE TRI DANS LES PARAMETRES UTILISATEUR
        Cursor choixTriReliquatExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_UTILISATEUR, null);
        int choixTriReliquatColumn = choixTriReliquatExisteCursor.getColumnIndex("TriReliquat");
        if (choixTriReliquatColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_UTILISATEUR + " ADD COLUMN TriReliquat TEXT");
        }
        //AJOUT DU PARAMETRE DE CHOIX DE TRI DANS LES PARAMETRES UTILISATEUR
        Cursor choixTriRetourExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_UTILISATEUR, null);
        int choixTriRetourColumn = choixTriRetourExisteCursor.getColumnIndex("TriRetour");
        if (choixTriRetourColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_UTILISATEUR + " ADD COLUMN TriRetour TEXT");
        }

        //AJOUT DU PARAMETRE DE CHOIX DE TRI DANS LES PARAMETRES UTILISATEUR
        Cursor choixTriRetourLigneExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_UTILISATEUR, null);
        int choixTriLigneRetourColumn = choixTriRetourLigneExisteCursor.getColumnIndex("TriRetourLigne");
        if (choixTriLigneRetourColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_UTILISATEUR + " ADD COLUMN TriRetourLigne TEXT");
        }


        //AJOUT DES PARAMETRES MAIL DANS LES PARAMETRES SERVEUR
        Cursor mailEmetteurExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_SERVEUR, null);
        int mailEmetteurLigneColumn = mailEmetteurExisteCursor.getColumnIndex("Mail_Emetteur");
        if (mailEmetteurLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN Mail_Emetteur TEXT");
        }

        Cursor mdpEmetteurExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_SERVEUR, null);
        int mdpEmetteurLigneColumn = mdpEmetteurExisteCursor.getColumnIndex("MDP_Emetteur");
        if (mdpEmetteurLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN MDP_Emetteur TEXT");
        }

        Cursor smtpPortExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_SERVEUR, null);
        int smtpPortLigneColumn = smtpPortExisteCursor.getColumnIndex("SMTP_Port");
        if (smtpPortLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN SMTP_Port INTEGER");
        }

        Cursor smtpServeurExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_SERVEUR, null);
        int smtpServeurLigneColumn = smtpServeurExisteCursor.getColumnIndex("SMTP_Serveur");
        if (smtpServeurLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN SMTP_Serveur TEXT");
        }

        Cursor smtpSessionExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_SERVEUR, null);
        int smtpSessionLigneColumn = smtpSessionExisteCursor.getColumnIndex("SMTP_Session");
        if (smtpSessionLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN SMTP_Session INTEGER");
        }

        //TABLE UTILISATEUR
        Cursor utilisateurEtablissementExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_UTILISATEUR, null);
        int utilisateurEtablissementLigneColumn = utilisateurEtablissementExisteCursor.getColumnIndex("Etablissement");
        if (utilisateurEtablissementLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_UTILISATEUR + " ADD COLUMN Etablissement TEXT");
        }

        Cursor lastperiemetreidExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_UTILISATEUR, null);
        int lastperiemetreidLigneColumn = lastperiemetreidExisteCursor.getColumnIndex("LastPerimetreId");
        if (lastperiemetreidLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_UTILISATEUR + " ADD COLUMN LastPerimetreId INTEGER");
        }

        //TABLE SERVICE
        Cursor serviceDescriptionExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_SERVICE, null);
        int serviceDescriptionLigneColumn = serviceDescriptionExisteCursor.getColumnIndex("description");
        if (serviceDescriptionLigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_SERVICE + " ADD COLUMN description TEXT");
        }

        Cursor serviceVideoExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_SERVICE, null);
        int serviceVideoigneColumn = serviceVideoExisteCursor.getColumnIndex("lien_video");
        if (serviceVideoigneColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_SERVICE + " ADD COLUMN lien_video TEXT");
        }

        Cursor serviceWhitePaperExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_SERVICE, null);
        int serviceWhitePaperColumn = serviceWhitePaperExisteCursor.getColumnIndex("whitePaper");
        if (serviceWhitePaperColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_SERVICE + " ADD COLUMN whitePaper TEXT");
        }

        //AJOUT DE LA TABLE PH_LOT_LIGNE
        Cursor tablePHLotLigne = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLE_PH_LOT_LIGNE + "'", null);
        if (tablePHLotLigne.getCount() == 0) {
            db.execSQL(CREATION_TABLE_PH_LOT_LIGNE);
        }

        Cursor PHLotLigneSerieExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_LOT_LIGNE, null);
        int PHLotLigneSerieColumn = PHLotLigneSerieExisteCursor.getColumnIndex("NumSerie");
        if (PHLotLigneSerieColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_LOT_LIGNE + " ADD COLUMN NumSerie TEXT");
        }

        Cursor PHLotLigneDepotIDExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_LOT_LIGNE, null);
        int PHLotLigneDepotIDColumn = PHLotLigneDepotIDExisteCursor.getColumnIndex("DepotID");
        Cursor PHLotLigneSerieCheckExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_LOT_LIGNE, null);
        int PHLotLigneCheckSerieColumn = PHLotLigneSerieCheckExisteCursor.getColumnIndex("NumSerie");
        if (PHLotLigneDepotIDColumn >= 0 && PHLotLigneCheckSerieColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_LOT_LIGNE + " RENAME DepotID TO NumSerie");
        }

        Cursor PHPreparationLigneVerrouillerExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION_LIGNE, null);
        int PHPreparationLigneVerrouillerColumn = PHPreparationLigneVerrouillerExisteCursor.getColumnIndex("Verrouiller");
        if (PHPreparationLigneVerrouillerColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION_LIGNE + " ADD COLUMN Verrouiller INTEGER");
        }

        Cursor LotLigneVerrouillerExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_LOT_LIGNE, null);
        int LotLigneVerrouillerColumn = LotLigneVerrouillerExisteCursor.getColumnIndex("Verrouiller");
        if (LotLigneVerrouillerColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_LOT_LIGNE + " ADD COLUMN Verrouiller INTEGER");
        }

        Cursor TempsPreparationExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION, null);
        int TempsPreparationrColumn = TempsPreparationExisteCursor.getColumnIndex("TempsPreparation");
        if (TempsPreparationrColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION + " ADD COLUMN TempsPreparation TEXT");
        }

        Cursor ScoreExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_SERVICE, null);
        int ScoreColumn = ScoreExisteCursor.getColumnIndex("score");
        if (ScoreColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_SERVICE + " ADD COLUMN score INTEGER");
        }

        Cursor delivranceValiderAExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION, null);
        int delivranceValiderAColumn = delivranceValiderAExisteCursor.getColumnIndex("delivranceValider_A");
        if (delivranceValiderAColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION + " ADD COLUMN delivranceValider_A TEXT");
        }

        Cursor delivranceValiderLeExisteCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION, null);
        int delivranceValiderLeColumn = delivranceValiderLeExisteCursor.getColumnIndex("delivranceValider_Le");
        if (delivranceValiderLeColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION + " ADD COLUMN delivranceValider_Le TEXT");
        }

        Cursor delivranceValiderParCursor = db.rawQuery("SELECT * FROM " + TABLE_PH_PREPARATION, null);
        int delivranceValiderParColumn = delivranceValiderParCursor.getColumnIndex("delivranceValider_Par");
        if (delivranceValiderParColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PH_PREPARATION + " ADD COLUMN delivranceValider_Par INTEGER");
        }

        Cursor loginEmetteurCursor = db.rawQuery("SELECT * FROM " + TABLE_PARAMETRES_SERVEUR, null);
        int loginEmetteurColumn = loginEmetteurCursor.getColumnIndex("Login_Emeteur");
        if (loginEmetteurColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_PARAMETRES_SERVEUR + " ADD COLUMN Login_Emeteur TEXT");
        }

        Cursor utilisateurEtablissementIdCursor = db.rawQuery("SELECT * FROM " + TABLE_UTILISATEUR, null);
        int utilisateurEtablissementIdColumn = utilisateurEtablissementIdCursor.getColumnIndex("EtablissementId");
        if (utilisateurEtablissementIdColumn < 0) {
            db.execSQL("ALTER TABLE " + TABLE_UTILISATEUR + " ADD COLUMN EtablissementId INTEGER");
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

        public static final String CLE_COL_PHIMR4UUID = "phiMR4UUID";
        public static final int NUM_COL_PHIMR4UUID = 0;
        public static final String TYPE_COL_PHIMR4UUID = "INTEGER";

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

    }

    public static class ActionsEAS {
        public static final String MAJ = "MAJ";
        public static final String AJOUT = "Ajout";
        public static final String SUPPR = "suppr";
    }
}