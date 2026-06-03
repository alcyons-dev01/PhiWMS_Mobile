package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.BaseColumns;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.client.util.DateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.ElementASynchroniser;
import fr.alcyons.phiwms_mobile.Classes.Inventaire;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.PH_Utiliser;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Produit_Identification;
import fr.alcyons.phiwms_mobile.Classes.Produit_Place;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Service;
import fr.alcyons.phiwms_mobile.Classes.StockUtilises;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.Classes.TableTrace;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.MedicalObjective;

public class ElementASynchroniserOpenHelper extends DBOpenHelper {

    public ElementASynchroniserOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static long ajouterElementASynchroniser(SQLiteDatabase db, String tableCorrespondante, int idDansTableCorrespondante, int idOrigine4D, String action) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER, tableCorrespondante);
        contentValues.put(Constantes.CLE_COL_ID_DANS_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER, idDansTableCorrespondante);
        contentValues.put(Constantes.CLE_COL_ACTION_ELEMENT_A_SYNCHRONISER, action);
        contentValues.put(Constantes.CLE_COL_ID_ORIGINE_4D, idOrigine4D);

        // Insertion de l'élément à synchroniser en BD
        long rowId = db.insert(Constantes.TABLE_ELEMENT_A_SYNCHRONISER, null, contentValues);

        return rowId;
    }

    public static void viderTableElementASynchroniser(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_ELEMENT_A_SYNCHRONISER, null, null);
    }

    public static void toutSynchroniser(Context context, SQLiteDatabase db, final Utilisateur utilisateur, boolean suppression) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_ELEMENT_A_SYNCHRONISER, null);
        while (cursor.moveToNext()) {
            ElementASynchroniser elementCourant = new ElementASynchroniser(cursor);

            switch (elementCourant.getAction()) {
                case ActionsEAS.AJOUT:
                    editerElementEnBDDistante(context, db, elementCourant, utilisateur, suppression);
                    break;
                case ActionsEAS.MAJ:
                    editerElementEnBDDistante(context, db, elementCourant, utilisateur, suppression);
                    break;
                case ActionsEAS.SUPPR:
                    supprimerElementASynchroniserBDDistante(context, db, elementCourant, utilisateur.getToken(), utilisateur);
                    break;
            }
        }
        cursor.close();
        cursor = null;
        viderTableElementASynchroniser(db);
    }

    public static String getUrlTableConcernee(String table, SQLiteDatabase db) {
        String url = "";
        switch (table) {
            case ZoneOpenHelper.Constantes.TABLE_DEPOT_ZONE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteDepotsZones;
                break;
            case EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteDepotsEmplacements;
                break;
            case ProduitOpenHelper.Constantes.TABLE_PRODUIT:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteProduits;
                break;
            case RetourOpenHelper.Constantes.TABLE_RETOUR:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteRetours;
                break;
            case Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteRetour_Lignes;
                break;
            case PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequetePH_Preparations;
                break;
            case PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequetePh_Preparation_Lignes;
                break;
            case Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteStock_Lot_Emplacements;
                break;
            case InventaireOpenHelper.Constantes.TABLE_INVENTAIRE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteInventaires;
                break;
            case Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteInventaire_Ligne_Temps;
                break;
            case CommandeOpenHelper.Constantes.TABLE_COMMANDE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteCommandes;
                break;
            case PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequetePH_Reliquat;
                break;
            case PH_UtiliserOpenHelper.Constantes.TABLE_PH_UTILISER:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequetePhUtiliser;
                break;
            case PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequetePHSerialisation;
                break;
            case ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteActionUtilisateur;
                break;
            case ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteActionUtilisateurLigne;
                break;
            case TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteTableTrace;
                break;
            case ServiceOpenHelper.Constantes.TABLE_SERVICE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteServices;
                break;
            case StockUtilisesOpenHelper.Constantes.TABLE_STOCK_UTILISE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriStockUtilises;
                break;
            case ProduitPlaceOpenHelper.Constantes.TABLE_PRODUIT_PLACE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriProduitPlace;
                break;
            case Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE:
                url += ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriProduitIdentification;
                break;
        }
        return url;
    }

    public static void editerElementEnBDDistante(final Context context, final SQLiteDatabase db, final ElementASynchroniser element, final Utilisateur utilisateur, boolean suppression) {
        String urlRequete = getUrlTableConcernee(element.getTableConcernee(), db);
        int ancien_id_action = 0;
        if (element.getAction().equals(ActionsEAS.MAJ)) {
            urlRequete += "update";
        }
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        final JSONObject body = new JSONObject();
        try {
            switch (element.getTableConcernee()) {
                case ZoneOpenHelper.Constantes.TABLE_DEPOT_ZONE:
                    /* CAS OU ON SOUHAITE CREER OU MODIFIER UNE ZONE */
                    Depot_Zone zoneAEnvoyer = ZoneOpenHelper.getUneZoneByID(db, element.getIdOrigine4D());
                    if (zoneAEnvoyer == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject zoneJson = new JSONObject();
                    zoneJson.put(ZoneOpenHelper.Constantes.CLE_COL_ID_DEPOT_ZONE, zoneAEnvoyer.getZoneID());
                    zoneJson.put(ZoneOpenHelper.Constantes.CLE_COL_NOM_DEPOT_ZONE, zoneAEnvoyer.getZoneName().toString());
                    zoneJson.put(ZoneOpenHelper.Constantes.CLE_COL_LONGITUDE_DEPOT_ZONE, zoneAEnvoyer.getZoneLongitude());
                    zoneJson.put(ZoneOpenHelper.Constantes.CLE_COL_LATITUDE_DEPOT_ZONE, zoneAEnvoyer.getZoneLatitude());
                    zoneJson.put(ZoneOpenHelper.Constantes.CLE_COL_DATA_MATRIX_REFERENCE_DEPOT_ZONE, String.valueOf(zoneAEnvoyer.getDataMatrixReference()));
                    zoneJson.put(ZoneOpenHelper.Constantes.CLE_COL_DEPOT_ID_DEPOT_ZONE, zoneAEnvoyer.getDepotID());
                    zoneJson.put(ZoneOpenHelper.Constantes.CLE_COL_CONSERVATION_DEPOT_ZONE, String.valueOf(zoneAEnvoyer.getConservation()));
                    zoneJson.put(ZoneOpenHelper.Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT_ZONE, String.valueOf(zoneAEnvoyer.getDepot_Reference()));
                    zoneJson.put(ZoneOpenHelper.Constantes.CLE_COL_TYPE_EMPLACEMENT_DEPOT_ZONE, String.valueOf(zoneAEnvoyer.getType_Emplacement()));
                    body.put("depot_zone", zoneJson);
                    break;
                case EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT:
                    /* CAS OU ON SOUHAITE CREER OU MODIFIER UN EMPLACEMENT */
                    Depot_Emplacement emplacementAEnvoyer = EmplacementOpenHelper.getUnEmplacementByID(db, element.getIdOrigine4D());
                    if (emplacementAEnvoyer == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject emplacementJson = new JSONObject();
                    emplacementJson.put(EmplacementOpenHelper.Constantes.CLE_COL_UID_DEPOT_EMPLACEMENT, emplacementAEnvoyer.get_UID());
                    emplacementJson.put(EmplacementOpenHelper.Constantes.CLE_COL_ADRESSAGE_DEPOT_EMPLACEMENT, String.valueOf(emplacementAEnvoyer.getAdressage()));
                    emplacementJson.put(EmplacementOpenHelper.Constantes.CLE_COL_HALL_DEPOT_EMPLACEMENT, String.valueOf(emplacementAEnvoyer.getHall()));
                    emplacementJson.put(EmplacementOpenHelper.Constantes.CLE_COL_PALETIER_DEPOT_EMPLACEMENT, String.valueOf(emplacementAEnvoyer.getPaletier()));
                    emplacementJson.put(EmplacementOpenHelper.Constantes.CLE_COL_ALVEOLE_DEPOT_EMPLACEMENT, String.valueOf(emplacementAEnvoyer.getAlveole()));
                    emplacementJson.put(EmplacementOpenHelper.Constantes.CLE_COL_NIVEAU_DEPOT_EMPLACEMENT, String.valueOf(emplacementAEnvoyer.getNiveau()));
                    emplacementJson.put(EmplacementOpenHelper.Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT, emplacementAEnvoyer.getZoneID());
                    emplacementJson.put(EmplacementOpenHelper.Constantes.CLE_COL_DEPOT_ID_DEPOT_EMPLACEMENT, emplacementAEnvoyer.getDepotID());
                    emplacementJson.put(EmplacementOpenHelper.Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT_EMPLACEMENT, String.valueOf(emplacementAEnvoyer.getDepot_Reference()));
                    emplacementJson.put(EmplacementOpenHelper.Constantes.CLE_COL_CODE_GLN_DEPOT_EMPLACEMENT, String.valueOf(emplacementAEnvoyer.getCode_GLN()));
                    body.put("depot_emplacement", emplacementJson);
                    break;
                case ProduitOpenHelper.Constantes.TABLE_PRODUIT:
                    Produit produitAEnvoyer = ProduitOpenHelper.getProduitByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (produitAEnvoyer == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject produitJson = produitAEnvoyer.toJson();
                    if (produitJson != null) {
                        body.put("produit", produitJson);
                    }
                    break;
                case RetourOpenHelper.Constantes.TABLE_RETOUR:
                    Retour retourAEnvoyer = RetourOpenHelper.getRetourByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (retourAEnvoyer == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject retourJson = retourAEnvoyer.toJson();
                    if (retourJson != null) {
                        body.put("retour", retourJson);
                    }
                    break;
                case Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE:
                    Retour_Ligne retour_ligne = Retour_LigneOpenHelper.getRetourLigneByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (retour_ligne == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject retourLigneJson = retour_ligne.toJson();
                    if (retourLigneJson != null) {
                        body.put("retour_ligne", retourLigneJson);
                    }
                    break;
                case PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION:
                    PH_Preparation phPreparation = PH_PreparationOpenHelper.getPH_PreparationByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (phPreparation == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject phPrepJson = phPreparation.toJson();
                    if (phPrepJson != null) {
                        body.put("ph_preparation", phPrepJson);
                    }
                    break;
                case PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE:
                    PH_Preparation_Ligne phPreparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (phPreparationLigne == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }

                    JSONObject phPrepLigneJson = phPreparationLigne.toJson();
                    if (phPrepLigneJson != null) {
                        body.put("ph_preparation_ligne", phPrepLigneJson);
                    }
                    if(suppression)
                    {
                        PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, phPreparationLigne);
                    }
                    break;
                case Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT:
                    Stock_Lot_Emplacement_Light stockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByPhiMR4UUID(db, element.getIdDansTableConcernee());
                    if (stockLotEmplacement == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject stockLotEmplacementJson = stockLotEmplacement.toJson();
                    if (stockLotEmplacementJson != null) {
                        body.put("ph_stock_lot_emplacement", stockLotEmplacementJson);
                    }
                    break;
                case InventaireOpenHelper.Constantes.TABLE_INVENTAIRE:
                    Inventaire inventaire = InventaireOpenHelper.getInventaireByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (inventaire == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject inventaireJson = inventaire.toJson();
                    if (inventaireJson != null) {
                        body.put("inventaire", inventaireJson);
                    }
                    break;
                case Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP:
                    Inventaire_Ligne_Temp inventaireLigneTemp = Inventaire_Ligne_TempOpenHelper.getInventaireLigneTempByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (inventaireLigneTemp == null) {
                        return;
                    }
                    JSONObject inventaireLigneTempJson = inventaireLigneTemp.toJson();
                    if (inventaireLigneTempJson != null) {
                        body.put("inventaire_ligne_temp", inventaireLigneTempJson);
                    }
                    break;
                case CommandeOpenHelper.Constantes.TABLE_COMMANDE:
                    Commande commande = CommandeOpenHelper.getCommandeByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (commande == null) {
                        return;
                    }
                    JSONObject commandeJSONObject = commande.toJson();
                    if (commandeJSONObject != null) {
                        body.put("ph_commande", commandeJSONObject);
                    }
                    break;
                case StockUtilisesOpenHelper.Constantes.TABLE_STOCK_UTILISE:
                    StockUtilises stockUtilises = StockUtilisesOpenHelper.getStockUtiliserByPhiWMSUUID(db, element.getIdDansTableConcernee());
                    if (stockUtilises == null) {
                        return;
                    }
                    JSONObject stockUtiliseJSONObject = stockUtilises.toJson();
                    if (stockUtiliseJSONObject != null) {
                        body.put("stock_utilises", stockUtiliseJSONObject);
                    }
                    break;
                case PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT:
                    PH_Reliquat phReliquat = PH_ReliquatOpenHelper.getPH_ReliquatByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (phReliquat == null) {
                        return;
                    }
                    JSONObject phReliquatJSONObject = phReliquat.toJson();
                    if (phReliquatJSONObject != null) {
                        body.put("ph_reliquat", phReliquatJSONObject);
                    }
                    break;
                case PH_UtiliserOpenHelper.Constantes.TABLE_PH_UTILISER:
                    PH_Utiliser phUtiliser = PH_UtiliserOpenHelper.getPH_UtiliserByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (phUtiliser == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    Depot depotPUI = DepotOpenHelper.getDepotPUI(db);
                    Depot depotOrigine = DepotOpenHelper.getDepotParID(db,phUtiliser.getDepotUID());
                    Produit produit = ProduitOpenHelper.getProduitByID(db, phUtiliser.getProduitUID());
                    MedicalObjective medicalObjective = new MedicalObjective(context, utilisateur, depotPUI, depotOrigine, produit, true);
                    medicalObjective.saveUtiliser(phUtiliser.getPhotoNom(), String.valueOf(phUtiliser.getPhotoUID()));

                    JSONObject phUtiliserJSONObject = phUtiliser.toJson();
                    if (phUtiliserJSONObject != null) {
                        body.put("PH_Utiliser", phUtiliserJSONObject);
                    }
                    break;
                case PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION:
                    PH_Serialisation serialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, element.getIdDansTableConcernee());
                    if (serialisation == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject SerialisationJSON = serialisation.toJson();
                    if (SerialisationJSON != null) {
                        body.put("ph_serialisation", SerialisationJSON);
                    }
                    break;
                case ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR:
                    ActionUtilisateur actionUtilisateur = ActionUtilisateurOpenHelper.getActionUtilisateurByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (actionUtilisateur == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject actionUtilisateurJSON = actionUtilisateur.toJson();
                    if (actionUtilisateurJSON != null) {
                        ancien_id_action = actionUtilisateur.getId();
                        body.put("ActionUtilisateur", actionUtilisateurJSON);
                    }
                    break;
                case ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE:
                    ActionUtilisateur_Ligne actionUtilisateurLigne = ActionUtilisateur_LigneOpenHelper.getActionUtilisateurByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (actionUtilisateurLigne == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject actionUtilisateurLigneJson = actionUtilisateurLigne.toJson();
                    if (actionUtilisateurLigneJson != null) {
                        body.put("ActionUtilisateur_Ligne", actionUtilisateurLigneJson);
                    }
                    break;
                case TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE:
                    TableTrace TableTrace = TableTraceOpenHelper.getTableTraceByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (TableTrace == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject TableTraceJSON = TableTrace.toJson();
                    if (TableTraceJSON != null) {
                        body.put("TableTrace", TableTraceJSON);
                    }
                    break;
                case ServiceOpenHelper.Constantes.TABLE_SERVICE:
                    Service service = ServiceOpenHelper.getServiceByID(db, element.getIdDansTableConcernee());
                    if (service == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject serviceJSON = service.toJson();
                    if (serviceJSON != null) {
                        body.put("Service", serviceJSON);
                        body.put("Utilisateur", utilisateur.getId());
                    }
                    break;
                case ProduitPlaceOpenHelper.Constantes.TABLE_PRODUIT_PLACE:
                    Produit_Place produitPlace = ProduitPlaceOpenHelper.getProduitPlaceByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (produitPlace == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject produitPlaceJSON = produitPlace.toJson();
                    if (produitPlaceJSON != null) {
                        body.put("ProduitPlace", produitPlaceJSON);
                    }
                    break;
                case Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE:
                    Produit_Identification produitIdentification = Produit_IdentificationOpenHelper.getProduitIdentificationByphiwms_mobileUUID(db, element.getIdDansTableConcernee());
                    if (produitIdentification == null) {
                        // Si on ne trouve pas l'élément en BD locale, c'est qu'il a déjà été supprimé localement, donc on empêche de tenter la modification
                        return;
                    }
                    JSONObject produitIdentificationJSON = produitIdentification.toJson();
                    if (produitIdentificationJSON != null) {
                        body.put("ProduitIdentification", produitIdentificationJSON);
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final int finalAncien_id_action = ancien_id_action;
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int nbResultats = response.getInt("resultCount");
                    if (nbResultats == 1) {
                        int nouvelId = response.getInt("succes");
                        long rowId = -1;
                        ContentValues contentValues = new ContentValues();
                        switch (element.getTableConcernee()) {
                            case ZoneOpenHelper.Constantes.TABLE_DEPOT_ZONE:
                                contentValues.put(ZoneOpenHelper.Constantes.CLE_COL_ID_DEPOT_ZONE, nouvelId);

                                Depot_Zone zoneAModifier = ZoneOpenHelper.getUneZoneByID(db, element.getIdDansTableConcernee());
                                zoneAModifier.setEmplacements(EmplacementOpenHelper.getEmplacementsParZone(db, zoneAModifier));

                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + element.getIdDansTableConcernee(), null);

                                zoneAModifier.setZoneID(nouvelId);

                                for (Depot_Emplacement emplacementCourant : zoneAModifier.getEmplacements()
                                        ) {
                                    ContentValues contentValueEmplacement = new ContentValues();
                                    contentValueEmplacement.put(EmplacementOpenHelper.Constantes.CLE_COL_ZONE_ID_DEPOT_EMPLACEMENT, nouvelId);

                                    long rowIdEmplacement = db.update(EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, contentValues, EmplacementOpenHelper.Constantes.CLE_COL_UID_DEPOT_EMPLACEMENT + " = " + emplacementCourant.get_UID(), null);
                                    if (rowIdEmplacement == -1) {
                                        Alerte.afficherAlerte(context, "Erreur", "Veuillez contacter la société Alcyons (erreur MAJ idEmplacement)", "alerte");
                                    } else {
                                        long rowID = ajouterElementASynchroniser(db, EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, emplacementCourant.getPhiMR4UUID(), emplacementCourant.get_UID(), ActionsEAS.MAJ);
                                        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_ELEMENT_A_SYNCHRONISER + " WHERE " + Constantes.CLE_COL_ID_DANS_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER + "=?", new String[]{String.valueOf(emplacementCourant.getPhiMR4UUID())});
                                        if (cursor.getCount() == 1) {
                                            cursor.moveToFirst();
                                            ElementASynchroniser elementASynchroniser = new ElementASynchroniser(cursor);
                                            editerElementEnBDDistante(context, db, elementASynchroniser, utilisateur, true);
                                        } else {
                                            Alerte.afficherAlerte(context, "Alerte", "Un problème est survenu dans la gestion des emplacement, veuillez contacter la société Alcyons (erreur synchro zone et emplacement", "alerte");
                                        }
                                    }
                                }
                                break;
                            case EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT:
                                contentValues.put(EmplacementOpenHelper.Constantes.CLE_COL_UID_DEPOT_EMPLACEMENT, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + element.getIdDansTableConcernee(), null);
                                break;
                            case ProduitOpenHelper.Constantes.TABLE_PRODUIT:
                                contentValues.put(ProduitOpenHelper.Constantes.CLE_COL_ID_PRODUIT, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + element.getIdDansTableConcernee(), null);
                                break;
                            case RetourOpenHelper.Constantes.TABLE_RETOUR:
                                contentValues.put(RetourOpenHelper.Constantes.CLE_COL__UID_RETOUR, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + element.getIdDansTableConcernee(), null);
                                break;
                            case Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE:
                                contentValues.put(Retour_LigneOpenHelper.Constantes.CLE_COL__UID_RETOUR_LIGNE, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + element.getIdDansTableConcernee(), null);
                                break;
                            case StockUtilisesOpenHelper.Constantes.TABLE_STOCK_UTILISE:
                                contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + element.getIdDansTableConcernee(), null);
                                break;
                            case PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION:
                                contentValues.put(PH_PreparationOpenHelper.Constantes.CLE_COL_UID_PH_PREPARATION, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + element.getIdDansTableConcernee(), null);
                                break;
                            case PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE:
                                contentValues.put(PH_Preparation_LigneOpenHelper.Constantes.CLE_COL__UID_PH_PREPARATION_LIGNE, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + element.getIdDansTableConcernee(), null);
                                break;
                            case Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT:
                                contentValues.put(Stock_Lot_EmplacementLightOpenHelper.Constantes.CLE_COL__UID_STOCK_LOT_EMPLACEMENT, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + element.getIdDansTableConcernee(), null);
                                break;
                            case InventaireOpenHelper.Constantes.TABLE_INVENTAIRE:
                                rowId = db.delete(InventaireOpenHelper.Constantes.TABLE_INVENTAIRE, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(element.getIdDansTableConcernee())});
                                break;
                            case Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP:
                                rowId = nouvelId;
                                //rowId = db.delete(Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(element.getIdDansTableConcernee())});
                                break;
                            case CommandeOpenHelper.Constantes.TABLE_COMMANDE:
                                rowId = db.delete(CommandeOpenHelper.Constantes.TABLE_COMMANDE, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(element.getIdDansTableConcernee())});
                                break;
                            case PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT:
                                rowId = nouvelId;
                                //contentValues.put(PH_ReliquatOpenHelper.Constantes.CLE_COL_RELIQUAT_UID_PH_RELIQUAT, nouvelId);
                                //rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + element.getIdDansTableConcernee(), null);
                                //rowId = db.delete(PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(element.getIdDansTableConcernee())});
                                break;
                            case PH_UtiliserOpenHelper.Constantes.TABLE_PH_UTILISER:
                                rowId = db.delete(PH_UtiliserOpenHelper.Constantes.TABLE_PH_UTILISER, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(element.getIdDansTableConcernee())});
                                break;
                            case TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE:
                                rowId = db.delete(TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(element.getIdDansTableConcernee())});
                                break;
                            case PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION:
                                contentValues.put(PH_SerialisationOpenHelper.Constantes.CLE_COL__UID_PH_SERIALISATION, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + element.getIdDansTableConcernee(), null);
                                break;
                            case ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR:
                                contentValues.put(ActionUtilisateurOpenHelper.Constantes.CLE_COL_ID_ACTION_UTILISATEUR, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + element.getIdDansTableConcernee(), null);
                                if(finalAncien_id_action != nouvelId)
                                {
                                    List<ActionUtilisateur_Ligne> actionligne = ActionUtilisateur_LigneOpenHelper.getLigneByAction(db, finalAncien_id_action);
                                    for(ActionUtilisateur_Ligne actioncourant : actionligne)
                                    {
                                        actioncourant.setIdActionUtilisateur(nouvelId);
                                        ActionUtilisateur_LigneOpenHelper.mettreAJourActionUtilisateurLigne(db, actioncourant);
                                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actioncourant.getPhiMR4UUID(), actioncourant.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                                    }
                                    toutSynchroniser(context, db, utilisateur, true);
                                }
                                break;
                            case ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE:
                                contentValues.put(ActionUtilisateur_LigneOpenHelper.Constantes.CLE_COL_ID_ACTION_UTILISATEUR_LIGNE, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " = " + element.getIdDansTableConcernee(), null);
                                break;
                            case ProduitPlaceOpenHelper.Constantes.TABLE_PRODUIT_PLACE:
                                rowId = nbResultats;
                                break;
                            case Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE:
                                contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, nouvelId);
                                rowId = db.update(element.getTableConcernee(), contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + element.getIdDansTableConcernee(), null);

                                Service serviceActuel = ServiceOpenHelper.getServiceByName(db, "Identification par scan");
                                //Création de l'action utilisateur
                                Random random= new Random();
                                int actionId = random.nextInt();
                                if (actionId > 0) actionId = actionId * -1;
                                SimpleDateFormat parseFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                String date_string = parseFormat.format(date);
                                ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(
                                        actionId,
                                        utilisateur.getId(),
                                        date_string,
                                        serviceActuel.getId(),
                                        utilisateur.getEtablissementId(),
                                        "En attente",
                                        nouvelId,
                                        "",
                                        "ProduitIdentification"
                                );

                                ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(
                                        db,
                                        ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR,
                                        new_action_utilisateur.getPhiMR4UUID(),
                                        new_action_utilisateur.getId(),
                                        DBOpenHelper.ActionsEAS.AJOUT
                                );

                                Produit_Identification produitIdentification = Produit_IdentificationOpenHelper.getProduitIdentificationByphiwms_mobileUUID(db, nouvelId);
                                Produit produit = ProduitOpenHelper.getProduitByID(db, produitIdentification.getCodeProduit());
                                int actionligneId = random.nextInt();
                                if (actionligneId > 0) actionligneId = actionligneId * -1;
                                ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(
                                        actionligneId,
                                        new_action_utilisateur.getId(),
                                        "Produit_Identification",
                                        nouvelId,
                                        "",
                                        0,
                                        0,
                                        produit.getDesignation_interne()
                                );
                                ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(
                                        db,
                                        actionUtilisateur_ligne
                                );

                                toutSynchroniser(context, db, utilisateur, false);
                                break;
                        }
                        if (rowId == -1) {
                            Alerte.afficherAlerte(context, "Erreur", "Une erreur réseau est survenue. Votre action sera exécuté ultérieurement (erreur MAJ synchronisationEdit : "+element.getTableConcernee()+")", "alerte");
                        }
                    } else if (nbResultats == 0) {
                        Alerte.afficherAlerte(context, "Erreur", "Une erreur réseau est survenue. Votre action sera exécuté ultérieurement (erreur requete : synchronisationEdit : "+element.getTableConcernee()+")", "alerte");
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        //Alerte.afficherAlerte(context, "Erreur", "Veuillez contacter la société Alcyons (erreur volley : "+error.getMessage()+" : "+element.getTableConcernee()+")", "alerte");
                    }
                }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", utilisateur.getToken());
                //headers.put("Content-Type", "application/json;charset=utf-8");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreq);

    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void supprimerElementASynchroniserBDDistante(final Context context, final SQLiteDatabase db, final ElementASynchroniser element, final String token, final Utilisateur utilisateur) {
        String urlRequete = getUrlTableConcernee(element.getTableConcernee(), db);
        urlRequete += "delete";

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject body = new JSONObject();
        try {
            JSONArray listeId = new JSONArray();
            listeId.put(element.getIdOrigine4D());
            switch (element.getTableConcernee()) {
                case ZoneOpenHelper.Constantes.TABLE_DEPOT_ZONE:
                    body.put("zoneIdListe", listeId);
                    break;
                case EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT:
                    body.put("emplacementIdListe", listeId);
                    break;
                case ProduitOpenHelper.Constantes.TABLE_PRODUIT:
                    body.put("produitIdListe", listeId);
                    break;
                case RetourOpenHelper.Constantes.TABLE_RETOUR:
                    body.put("retourIdListe", listeId);
                    break;
                case Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE:
                    body.put("retour_ligneIdListe", listeId);
                    break;
                case PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION:
                    body.put("ph_preparationIdListe", listeId);
                    break;
                case PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE:
                    body.put("ph_preparation_ligneIdListe", listeId);
                    break;
                case Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT:
                    body.put("ph_stock_lot_emplacementIdListe", listeId);
                    break;
                case InventaireOpenHelper.Constantes.TABLE_INVENTAIRE:
                    body.put("ph_inventaireIdListe", listeId);
                    break;
                case Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP:
                    body.put("ph_inventaire_Ligne_TempIdListe", listeId);
                    break;
                case CommandeOpenHelper.Constantes.TABLE_COMMANDE:
                    body.put("ph_commandeIdListe", listeId);
                    break;
                case PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT:
                    body.put("ph_reliquatIdListe", listeId);
                    break;
                case PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION:
                    body.put("ph_serialisation", listeId);
                    break;
                case ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR:
                    body.put("ActionUtilisateur", listeId);
                    break;
                case ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE:
                    body.put("ActionUtilisateur_Ligne", listeId);
                    break;
                case StockUtilisesOpenHelper.Constantes.TABLE_STOCK_UTILISE:
                    body.put("stock_utilises", listeId);
                    break;
                case Produit_IdentificationOpenHelper.Constantes.TABLE_IDENTIFICATION_REFERENCE:
                    body.put("ProduitIdentification", listeId);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, body,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int nbResultats = response.getInt("resultCount");
                            if (nbResultats < 1) {
                                Alerte.afficherAlerte(context, "Erreur", "Veuillez contacter la société Alcyons (erreur requete : synchronisationSuppression)", "alerte");
                            } else {
                                switch (element.getTableConcernee()) {
                                    case ZoneOpenHelper.Constantes.TABLE_DEPOT_ZONE:
                                        List<Depot_Emplacement> emplacements = EmplacementOpenHelper.getEmplacementsParZoneID(db, element.getIdDansTableConcernee());
                                        for (Depot_Emplacement emplacement : emplacements
                                                ) {
                                            ajouterElementASynchroniser(db, EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, emplacement.getPhiMR4UUID(), emplacement.get_UID(), ActionsEAS.SUPPR);
                                            Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_ELEMENT_A_SYNCHRONISER + " WHERE " + Constantes.CLE_COL_ID_DANS_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER + "=?", new String[]{String.valueOf(emplacement.get_UID())});
                                            if (cursor.getCount() == 1) {
                                                cursor.moveToFirst();
                                                ElementASynchroniser emplacementASynchroniser = new ElementASynchroniser(cursor);
                                                supprimerElementASynchroniserBDDistante(context, db, emplacementASynchroniser, token, utilisateur);
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur", "Veuillez contacter la société Alcyons (erreur volley : synchronisationSuppression)", "alerte");
                    }
                }
        ) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("UserId", String.valueOf(utilisateur.getId()));
                headers.put("EtablissementId", String.valueOf(utilisateur.getEtablissementId()));
                headers.put("Content-Type", "application/json;charset=utf-8");
                return headers;
            }
        };
        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreq);
    }

    public static int compterElementsASynchroniser(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_ELEMENT_A_SYNCHRONISER, null);
        int nombre = cursor.getCount();
        cursor.close();
        cursor = null;
        return nombre;
    }

    public static class Constantes implements BaseColumns {
        // Nom de la table Element à synchroniser
        public static final String TABLE_ELEMENT_A_SYNCHRONISER = "ElementASynchroniser";


        public static final String CLE_COL_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER = "tableConcernee";
        public static final int NUM_COL_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER = 1;
        public static final String TYPE_COL_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER = "TEXT";

        public static final String CLE_COL_ID_DANS_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER = "idDansTableConcernee";
        public static final int NUM_COL_ID_DANS_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER = 2;
        public static final String TYPE_COL_ID_DANS_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER = "INTEGER";

        public static final String CLE_COL_ACTION_ELEMENT_A_SYNCHRONISER = "action";
        public static final int NUM_COL_ACTION_ELEMENT_A_SYNCHRONISER = 3;
        public static final String TYPE_COL_ACTION_ELEMENT_A_SYNCHRONISER = "TEXT";

        public static final String CLE_COL_ID_ELEMENT_A_SYNCHRONISER = "id";
        public static final int NUM_COL_ID_ELEMENT_A_SYNCHRONISER = 4;
        public static final String TYPE_COL_ID_ELEMENT_A_SYNCHRONISER = "INTEGER";

        public static final String CLE_COL_ID_ORIGINE_4D = "idOrigine4D";
        public static final int NUM_COL_ID_ORIGINE_4D = 5;
        public static final String TYPE_COL_ID_ORIGINE_4D = "INTEGER";


        public static final String CREATION_TABLE_ELEMENT_A_SYNCHRONISER = "CREATE TABLE "
                + TABLE_ELEMENT_A_SYNCHRONISER + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + CLE_COL_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER + " " + Constantes.TYPE_COL_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER + ","
                + CLE_COL_ID_DANS_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER + " " + Constantes.TYPE_COL_ID_DANS_TABLE_CONCERNEE_ELEMENT_A_SYNCHRONISER + ","
                + CLE_COL_ACTION_ELEMENT_A_SYNCHRONISER + " " + Constantes.TYPE_COL_ACTION_ELEMENT_A_SYNCHRONISER + ","
                + CLE_COL_ID_ELEMENT_A_SYNCHRONISER + " " + Constantes.TYPE_COL_ID_ELEMENT_A_SYNCHRONISER + ","
                + CLE_COL_ID_ORIGINE_4D + " " + TYPE_COL_ID_ORIGINE_4D
                + ");";

    }
}
